/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ivoa.util.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.ivoa.bean.LogSupport;
import org.ivoa.util.ReflectionUtils;

/**
 *
 * @author laurent
 */
public final class ThreadLocalMapUtils extends LogSupport {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** name of the attribute Thread.threadLocals */
  private final static String FIELD_THREAD_THREADLOCALS = "threadLocals";
  /** name of the class ThreadLocal$ThreadLocalMap */
  private final static String CLASS_THREADLOCAL_MAP = "java.lang.ThreadLocal$ThreadLocalMap";
  /** name of the attribute ThreadLocalMap.table */
  private final static String FIELD_THREADLOCALMAP_TABLE = "table";
  /** name of the attribute Thread.threadLocals.Entry.value */
  private final static String FIELD_THREADLOCALMAP_ENTRY_VALUE = "value";
  /** name of the method ThreadLocalMap.getEntry(ThreadLocal) */
  private final static String FIELD_THREADLOCALMAP_GET_ENTRY = "getEntry";
  /** name of the method ThreadLocalMap.get(ThreadLocal) - Java 5 only */
  private final static String FIELD_THREADLOCALMAP_GET = "get";
  /** name of the method ThreadLocalMap.remove(ThreadLocal) */
  private final static String METHOD_THREADLOCALMAP_REMOVE = "remove";
  /**
   * Java 5 flag to adapt the getThreadLocalValue() method implementation
   */
  private static boolean isJava5 = false;
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
   * Temporary cached Method ThreadLocalMap.getEntry(ThreadLocal)
   */
  private static Method threadLocalMapGetMethod = null;
  /**
   * Temporary cached Method ThreadLocalMap.remove(ThreadLocal)
   */
  private static Method threadLocalMapRemoveMethod = null;

  static {
    prepareFields();
  }

  /**
   * Forbidden constructor
   */
  private ThreadLocalMapUtils() {
    /* no-op */
  }

  /**
   * Prepare the necessary fields using reflection
   */
  public static void prepareFields() {
    isJava5 = System.getProperty("java.version").startsWith("1.5");

    threadThreadLocalsField = ReflectionUtils.getField(Thread.class, FIELD_THREAD_THREADLOCALS);

    final Class<?> threadLocalMapKlazz = ReflectionUtils.findClass(CLASS_THREADLOCAL_MAP);
    if (threadLocalMapKlazz != null) {
      threadLocalMapTableField = ReflectionUtils.getField(threadLocalMapKlazz, FIELD_THREADLOCALMAP_TABLE);
    }
  }

  /**
   * Clear the fields
   */
  public static void clearFields() {
    threadThreadLocalsField = null;
    threadLocalMapTableField = null;
    threadLocalMapEntryValueField = null;
    threadLocalMapGetMethod = null;
    threadLocalMapRemoveMethod = null;
  }

  /**
   * Return the ThreadLocalMap (private field) for the given thread
   * @param thread thread Thread to use
   * @return ThreadLocalMap for the given thread
   */
  public static Object getThreadLocalMap(final Thread thread) {
    /* private class ThreadLocal.ThreadLocalMap */
    return ReflectionUtils.getFieldValue(threadThreadLocalsField, thread);
  }

  /**
   * Return the ThreadLocalMap.Entry[] array for the given ThreadLocalMap
   * @param threadLocalMap ThreadLocalMap to use
   * @return ThreadLocalMap.Entry[] array
   */
  public static Object[] getThreadLocalMapTable(final Object threadLocalMap) {
    /* private class ThreadLocal.ThreadLocalMap.Entry[] */
    return (Object[]) ReflectionUtils.getFieldValue(threadLocalMapTableField, threadLocalMap);
  }

  /**
   * Extract the value for the given ThreadLocalMap.Entry
   * @param threadLocalMapEntry ThreadLocalMap.Entry to use
   * @return value
   */
  public static Object getThreadLocalEntryValue(final Object threadLocalMapEntry) {
    if (threadLocalMapEntryValueField == null) {
      threadLocalMapEntryValueField = ReflectionUtils.getField(threadLocalMapEntry.getClass(), FIELD_THREADLOCALMAP_ENTRY_VALUE);
    }

    return ReflectionUtils.getFieldValue(threadLocalMapEntryValueField, threadLocalMapEntry);
  }

  /**
   * Extract the ThreadLocal value for the given ThreadLocalMap
   * @param threadLocalMap ThreadLocalMap to use
   * @param threadLocal ThreadLocal to remove
   * @return ThreadLocal value
   */
  public static Object getThreadLocalValue(final Object threadLocalMap, final ThreadLocal<?> threadLocal) {

    /*
     * Java 5 : ThreadLocal (1.5) private Object get(ThreadLocal key)
     *
     * Java 6 : ThreadLocal (1.6) private Entry getEntry(ThreadLocal key)
     *
     */
    if (isJava5) {
      return getThreadLocalValueForJava5(threadLocalMap, threadLocal);
    } else {
      return getThreadLocalValueForJava6(threadLocalMap, threadLocal);
    }
  }

  /**
   * Extract the ThreadLocal value for the given ThreadLocalMap
   * @param threadLocalMap ThreadLocalMap to use
   * @param threadLocal ThreadLocal to remove
   * @return ThreadLocal value
   */
  private static Object getThreadLocalValueForJava5(final Object threadLocalMap, final ThreadLocal<?> threadLocal) {
    if (threadLocalMapGetMethod == null) {
      threadLocalMapGetMethod = ReflectionUtils.getMethod(threadLocalMap.getClass(), FIELD_THREADLOCALMAP_GET, ThreadLocal.class);
    }
    return ReflectionUtils.invokeMethod(threadLocalMapGetMethod, threadLocalMap, new Object[]{threadLocal});
  }

  /**
   * Extract the ThreadLocal value for the given ThreadLocalMap
   * @param threadLocalMap ThreadLocalMap to use
   * @param threadLocal ThreadLocal to remove
   * @return ThreadLocal value
   */
  private static Object getThreadLocalValueForJava6(final Object threadLocalMap, final ThreadLocal<?> threadLocal) {
    if (threadLocalMapGetMethod == null) {
      threadLocalMapGetMethod = ReflectionUtils.getMethod(threadLocalMap.getClass(), FIELD_THREADLOCALMAP_GET_ENTRY, ThreadLocal.class);
    }
    Object value = null;
    final Object entry = ReflectionUtils.invokeMethod(threadLocalMapGetMethod, threadLocalMap, new Object[]{threadLocal});
    if (entry != null) {
      value = ThreadLocalMapUtils.getThreadLocalEntryValue(entry);
    }
    return value;
  }

  /**
   * Remove the given threadLocal from the ThreadLocalMap
   * @param threadLocalMap ThreadLocalMap to use
   * @param threadLocal ThreadLocal to remove
   */
  public static void removeThreadLocal(final Object threadLocalMap, final ThreadLocal<?> threadLocal) {
    if (threadLocalMapRemoveMethod == null) {
      threadLocalMapRemoveMethod = ReflectionUtils.getMethod(threadLocalMap.getClass(), METHOD_THREADLOCALMAP_REMOVE, ThreadLocal.class);
      if (logB.isDebugEnabled()) {
        logB.debug("method = " + threadLocalMapRemoveMethod);
      }
    }
    // reset the thread local for the given thread :
    // idem threadLocal remove() */
    ReflectionUtils.invokeMethod(threadLocalMapRemoveMethod, threadLocalMap, new Object[]{threadLocal});
  }
}
