package org.ivoa.env;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
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

    private final static String FIELD_THREAD_THREADLOCALS = "threadLocals";
    private final static String CLASS_THREADLOCAL_MAP = "java.lang.ThreadLocal$ThreadLocalMap";
    private final static String FIELD_THREADLOCALMAP_TABLE = "table";
    private final static String FIELD_THREADLOCALMAP_ENTRY_VALUE = "value";

    private final static String CLASS_TO_REMOVE = "org.ivoa";

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
        logD.warn("ThreadLocalCleaner.cleanAndcheckThreads : begin");
        logD.warn("ThreadLocalCleaner.cleanAndcheckThreads : classLoader =\n" + ThreadLocalCleaner.class.getClassLoader());

        final Thread[] ta = new Thread[Thread.activeCount()];
        Thread.enumerate(ta);

        for (final Thread t : ta) {
            log.warn("ThreadLocalCleaner.cleanAndcheckThreads : cleaning : " + t.getName());
            ThreadLocalCleaner.cleanThreadLocals(t, CLASS_TO_REMOVE);

            log.warn("ThreadLocalCleaner.cleanAndcheckThreads : checking : " + t.getName());
            ThreadLocalCleaner.checkThreadLocals(t);
        }

        logD.warn("ThreadLocalCleaner.cleanAndcheckThreads : end");
    }

    /**
     * Clean the given thread's ThreadLocal map
     * @param thread thread to clean
     */
    private static void cleanThreadLocals(final Thread thread, final String pattern) {
        try {
            final Field threadLocalsField = ReflectionUtils.getField(Thread.class, FIELD_THREAD_THREADLOCALS);

            final Object threadLocalsValue = threadLocalsField.get(thread);

            if (threadLocalsValue != null) {

                final Class<?> threadLocalMapKlazz = ReflectionUtils.findClass(CLASS_THREADLOCAL_MAP);
                if (threadLocalMapKlazz != null) {
                    final Field tableField = ReflectionUtils.getField(threadLocalMapKlazz, FIELD_THREADLOCALMAP_TABLE);

                    final Object table = tableField.get(threadLocalsValue); /* Entry[] */

                    final int threadLocalCount = Array.getLength(table);

                    Object entry;
                    Field valueField = null;
                    Object value;
                    Class<?> clazz;

                    for (int i = 0; i < threadLocalCount; i++) {
                        entry = Array.get(table, i);
                        if (entry != null) {

                            if (valueField == null) {
                                valueField = ReflectionUtils.getField(entry.getClass(), FIELD_THREADLOCALMAP_ENTRY_VALUE);
                                valueField.setAccessible(true);
                            }

                            value = valueField.get(entry);
                            if (value != null) {
                                clazz = value.getClass();

                                if (value instanceof WeakHashMap) {

                                    final WeakHashMap wm = (WeakHashMap) value;

                                    try {
                                        for (Object o : wm.keySet()) {
                                            if (o != null && o.toString().contains(pattern)) {
                                                log.warn("Cleaning WeakHashMap :" + CollectionUtils.toString(wm));

                                                wm.clear();
                                                break;
                                            }
                                        }
                                    } catch (RuntimeException re) {
                                        logD.error("Cleaning WeakHashMap : failure : ", re);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (final IllegalAccessException iae) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", iae);
        } catch (final IllegalArgumentException iarge) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", iarge);
        } catch (final RuntimeException re) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", re);
        }
    }

    /**
     * Check the given thread's ThreadLocal map
     * @param thread thread to inspect
     */
    private static void checkThreadLocals(final Thread thread) {
        try {
            final Field threadLocalsField = ReflectionUtils.getField(Thread.class, FIELD_THREAD_THREADLOCALS);

            final Object threadLocalsValue = threadLocalsField.get(thread);

            if (threadLocalsValue != null) {

                final Class<?> threadLocalMapKlazz = ReflectionUtils.findClass(CLASS_THREADLOCAL_MAP);
                if (threadLocalMapKlazz != null) {
                    final Field tableField = ReflectionUtils.getField(threadLocalMapKlazz, FIELD_THREADLOCALMAP_TABLE);

                    final Object table = tableField.get(threadLocalsValue); /* Entry[] */

                    final int threadLocalCount = Array.getLength(table);

                    int leakCount = 0;

                    Object entry;
                    Field valueField = null;
                    Object value;
                    String type;
                    Class<?> clazz;

                    /*
                     * Create a dedicated string builder for the complete graph visit.
                     * Can not use LocalStringBuilder because of possible re-entrance issues
                     */
                    final StringBuilder sb = new StringBuilder(2048);

                    for (int i = 0; i < threadLocalCount; i++) {
                        entry = Array.get(table, i);
                        if (entry != null) {

                            if (valueField == null) {
                                valueField = ReflectionUtils.getField(entry.getClass(), FIELD_THREADLOCALMAP_ENTRY_VALUE);
                                valueField.setAccessible(true);
                            }

                            value = valueField.get(entry);
                            if (value != null) {
                                clazz = value.getClass();
                                type = clazz.getCanonicalName();

                                sb.append("\n    + ").append(type).append("@").append(
                                        Integer.toHexString(value.hashCode()));

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

                        logD.warn("Possible ThreadLocal leaks for thread : " + thread.getName() + " [" + leakCount + " / " + threadLocalCount + "] {" + sb.toString());
                    }
                }
            }
        } catch (final IllegalAccessException iae) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", iae);
        } catch (final IllegalArgumentException iarge) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", iarge);
        } catch (final RuntimeException re) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", re);
        }
    }
}
