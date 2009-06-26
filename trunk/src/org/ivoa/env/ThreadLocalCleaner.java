package org.ivoa.env;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.CollectionUtils;

/**
 * Simple ThreadLocal inspectors to guess unreleased references (possible memory leak)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ThreadLocalCleaner extends LogSupport {

    /**
     * Forbidden constructor
     */
    private ThreadLocalCleaner() {
        /* no-op */
    }

    /**
     * Check all threads in the current ThreadGroup
     */
    public static void checkThreads() {
        logD.warn("ThreadLocalCleaner.checkThreadLocals : begin");

        final Thread[] ta = new Thread[Thread.activeCount()];
        Thread.enumerate(ta);

        for (final Thread t : ta) {
            log.warn("ThreadLocalCleaner.checkThreads : checking : " + t.getName());

            ThreadLocalCleaner.checkThreadLocals(t);
        }

        logD.warn("ThreadLocalCleaner.checkThreadLocals : end");


    }

    /**
     * Check the given thread's ThreadLocal map
     * @param thread thread to inspect
     */
    private static void checkThreadLocals(final Thread thread) {
        try {
            final Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);

            final Object threadLocalsValue = threadLocalsField.get(thread);

            if (threadLocalsValue != null) {

                final Class<?> threadLocalMapKlazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
                final Field tableField = threadLocalMapKlazz.getDeclaredField("table");
                tableField.setAccessible(true);

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
                            valueField = entry.getClass().getDeclaredField("value");
                            valueField.setAccessible(true);
                        }

                        value = valueField.get(entry);
                        if (value != null) {
                            clazz = value.getClass();
                            type = clazz.getCanonicalName();

                            sb.append("\n    + ").append(type).append("@").append(
                                    Integer.toHexString(value.hashCode()));

                            sb.append(" value = {\n        ");
                            if (value instanceof Collection) {
                                CollectionUtils.toString(sb, (Collection<?>) value);
                            } else if (value instanceof Map) {
                                CollectionUtils.toString(sb, (Map<?, ?>) value);
                            } else if (value instanceof Reference) {
                                sb.append(((Reference<?>) value).get());
                            } else if (clazz.isArray()) {
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
                            } else {
                                sb.append(value);
                            }
                            sb.append("\n  }");

                        } else {
                            sb.append("n/a");
                        }

                        leakCount++;
                    }
                }

                if (leakCount > 0) {
                    sb.append("\n}");

                    logD.warn("\n- Possible ThreadLocal leaks for thread : " + thread.getName() + " [" + leakCount + " / " + threadLocalCount + "] {" + sb.toString());
                }

            }
        } catch (final Exception e) {
            logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", e);
        }
    }
}
