package org.ivoa.env;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import java.util.WeakHashMap;
import org.ivoa.bean.LogSupport;
import org.ivoa.util.CollectionUtils;
import org.ivoa.util.ReflectionUtils;

/**
 * Simple ThreadLocal inspectors to guess unreleased references (possible memory leak)
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ThreadLocalCleaner extends LogSupport {

  /** name of the attribute Thread.threadLocals */
  private final static String FIELD_THREAD_THREADLOCALS = "threadLocals";
  /** name of the class ThreadLocal$ThreadLocalMap */
  private final static String CLASS_THREADLOCAL_MAP = "java.lang.ThreadLocal$ThreadLocalMap";
  /** name of the attribute ThreadLocalMap.table */
  private final static String FIELD_THREADLOCALMAP_TABLE = "table";
  /** name of the attribute Thread.threadLocals.Entry.value */
  private final static String FIELD_THREADLOCALMAP_ENTRY_VALUE = "value";
  /** pattern to detect application classes to remove from WeakHashMap */
  private final static String CLASS_TO_REMOVE = "org.ivoa";
  /**
   * Temporary cached Field Thread.threadLocals
   */
  private static Field threadThreadLocalsField = null;
  /**
   * Temporary cached Field ThreadLocalMap.table
   */
  private static Field threadLocalMapTableField = null;
  /**
   * Temporary cached Field Thread.threadLocals.Entry.value
   */
  private static Field threadLocalMapEntryValueField = null;

  /**
   * Forbidden constructor
   */
  private ThreadLocalCleaner() {
    /* no-op */
  }

  /**
   * Check all threads in the current ThreadGroup
   */
  public static void cleanAndcheckThreads() {
    if (logB.isInfoEnabled()) {
      logB.info("ThreadLocalCleaner.cleanAndcheckThreads : begin");
      logB.info("ThreadLocalCleaner.cleanAndcheckThreads : classLoader =\n" + ThreadLocalCleaner.class.getClassLoader());
    }

    try {
      prepareFields();

      final Thread[] ta = new Thread[Thread.activeCount()];
      Thread.enumerate(ta);

      for (final Thread t : ta) {
        if (logB.isInfoEnabled()) {
          logB.info("ThreadLocalCleaner.cleanAndcheckThreads : cleaning thread [" + t.getName() + "] ...");
        }
        ThreadLocalCleaner.cleanThreadLocals(t, CLASS_TO_REMOVE);
      }

      // Checks are only logged if the debug logger is enabled for the level INFO :
      if (logB.isInfoEnabled()) {
        for (final Thread t : ta) {
          logB.info("ThreadLocalCleaner.cleanAndcheckThreads : checking thread [" + t.getName() + "] ...");
          ThreadLocalCleaner.checkThreadLocals(t);
        }
      }

    } finally {
      clearFields();
    }
    if (logB.isInfoEnabled()) {
      logB.info("ThreadLocalCleaner.cleanAndcheckThreads : end");
    }
  }

  /**
   * Prepare the necessary fields using reflection
   */
  private static void prepareFields() {
    threadThreadLocalsField = ReflectionUtils.getField(Thread.class, FIELD_THREAD_THREADLOCALS);

    final Class<?> threadLocalMapKlazz = ReflectionUtils.findClass(CLASS_THREADLOCAL_MAP);
    if (threadLocalMapKlazz != null) {
      threadLocalMapTableField = ReflectionUtils.getField(threadLocalMapKlazz, FIELD_THREADLOCALMAP_TABLE);
    }
  }

  /**
   * Clear the fields
   */
  private static void clearFields() {
    threadThreadLocalsField = null;
    threadLocalMapTableField = null;
    threadLocalMapEntryValueField = null;
  }

  /**
   * Clean the given thread's ThreadLocal map
   *
   * @param thread thread to clean
   * @param pattern String pattern to recognize classes in WeakHashMap.keys that will trigger a call
   *          to WeakHashMap.clear() in order to release ASAP these references
   */
  private static void cleanThreadLocals(final Thread thread, final String pattern) {
    try {
      final Object threadLocalsValue = ReflectionUtils.getFieldValue(threadThreadLocalsField, thread);

      if (threadLocalsValue != null) {
        /* private class ThreadLocal.ThreadLocalMap.Entry[] */
        final Object[] table = (Object[]) ReflectionUtils.getFieldValue(threadLocalMapTableField, threadLocalsValue);

        final int threadLocalCount = table.length;

        /*
         * Create a dedicated string builder for the complete graph visit. Can not use
         * LocalStringBuilder because this singleton is already stopped.
         */
        final StringBuilder sb = new StringBuilder(512);

        Object entry;
        Field valueField = null;
        Object value;

        for (int i = 0; i < threadLocalCount; i++) {
          entry = table[i];
          if (entry != null) {

            if (valueField == null) {
              valueField = ReflectionUtils.getField(entry.getClass(), FIELD_THREADLOCALMAP_ENTRY_VALUE);
            }

            value = ReflectionUtils.getFieldValue(valueField, entry);
            if (value != null) {
              if (value instanceof WeakHashMap) {
                final WeakHashMap<?, ?> wm = (WeakHashMap<?, ?>) value;

                try {
                  for (final Object o : wm.keySet()) {
                    if (o != null && o.toString().contains(pattern)) {
                      if (logB.isInfoEnabled()) {
                        logB.info("Cleaning WeakHashMap :" + CollectionUtils.toString(sb, wm));
                      }

                      wm.clear();
                      break;
                    }
                  }
                } catch (final RuntimeException re) {
                  logB.error("Cleaning WeakHashMap : failure : ", re);
                }
              }
            }
          }
        }
      }
    } catch (final RuntimeException re) {
      logB.error("ThreadLocalCleaner.checkThreadLocals : failure : ", re);
    }
  }

  /**
   * Check the given thread's ThreadLocal map
   *
   * @param thread thread to inspect
   */
  private static void checkThreadLocals(final Thread thread) {
    try {
      final Object threadLocalsValue = ReflectionUtils.getFieldValue(threadThreadLocalsField, thread);

      if (threadLocalsValue != null) {
        /* private class ThreadLocal.ThreadLocalMap.Entry[] */
        final Object[] table = (Object[]) ReflectionUtils.getFieldValue(threadLocalMapTableField, threadLocalsValue);

        final int threadLocalCount = table.length;

        /*
         * Create a dedicated string builder for the complete graph visit. Can not use
         * LocalStringBuilder because this singleton is already stopped.
         */
        final StringBuilder sb = new StringBuilder(2048);

        int leakCount = 0;
        Object entry;
        Object value;
        String type;
        Class<?> clazz;

        for (int i = 0; i < threadLocalCount; i++) {
          entry = table[i];
          if (entry != null) {

            if (threadLocalMapEntryValueField == null) {
              threadLocalMapEntryValueField = ReflectionUtils.getField(entry.getClass(), FIELD_THREADLOCALMAP_ENTRY_VALUE);
            }

            value = ReflectionUtils.getFieldValue(threadLocalMapEntryValueField, entry);
            if (value != null) {
              clazz = value.getClass();
              type = clazz.getCanonicalName();

              sb.append("\n    + ").append(type).append("@").append(Integer.toHexString(value.hashCode()));

              sb.append(" value = ");
              if (value instanceof Collection) {
                CollectionUtils.toString(sb, (Collection<?>) value);
              } else if (value instanceof Map) {
                CollectionUtils.toString(sb, (Map<?, ?>) value);
              } else if (value instanceof Reference) {
                sb.append("{\n        ").append(((Reference<?>) value).get()).append("\n  }");
              } else if (clazz.isArray()) {
                sb.append("{\n        ");
                // check all kind of primitive types :
                if (clazz == byte[].class) {
                  sb.append(Arrays.toString((byte[]) value));
                } else if (clazz == short[].class) {
                  sb.append(Arrays.toString((short[]) value));
                } else if (clazz == int[].class) {
                  sb.append(Arrays.toString((int[]) value));
                } else if (clazz == long[].class) {
                  sb.append(Arrays.toString((long[]) value));
                } else if (clazz == char[].class) {
                  sb.append(Arrays.toString((char[]) value));
                } else if (clazz == float[].class) {
                  sb.append(Arrays.toString((float[]) value));
                } else if (clazz == double[].class) {
                  sb.append(Arrays.toString((double[]) value));
                } else if (clazz == boolean[].class) {
                  sb.append(Arrays.toString((boolean[]) value));
                } else {
                  sb.append(Arrays.deepToString((Object[]) value));
                }
                sb.append("\n  }");
              } else {
                sb.append("{\n        ").append(value).append("\n  }");
              }
            }

            leakCount++;
          }
        }

        if (leakCount > 0) {
          sb.append("\n}");

          logB.info("Possible ThreadLocal leaks for thread : " + thread.getName() + " [" + leakCount + " / " + threadLocalCount + "] {" + sb.toString());
        }
      }
    } catch (final RuntimeException re) {
      logB.error("ThreadLocalCleaner.checkThreadLocals : failure : ", re);
    }
  }
}
