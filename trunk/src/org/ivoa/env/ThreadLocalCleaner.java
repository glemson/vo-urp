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
 * @author laurent bourges (voparis)
 */public class ThreadLocalCleaner extends LogSupport {

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
    if (logD.isWarnEnabled()) {
      logD.warn("ThreadLocalCleaner.checkThreadLocals : begin"); 

      final Thread[] ta = new Thread[Thread.activeCount()];
      Thread.enumerate(ta);

      for (Thread t : ta) {
        ThreadLocalCleaner.checkThreadLocals(t);
      }
      
      logD.warn("ThreadLocalCleaner.checkThreadLocals : end"); 
    }
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

        final Class<?> threadLocalMapKlazz = Class
            .forName("java.lang.ThreadLocal$ThreadLocalMap");
        final Field tableField = threadLocalMapKlazz.getDeclaredField("table");
        tableField.setAccessible(true);

        final Object table = tableField.get(threadLocalsValue); /* Entry[] */

        final int threadLocalCount = Array.getLength(table);

        int leakCount = 0;

        Object entry;
        Field valueField = null;
        Object value;
        String type;
        Class<?> cl;

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
              cl = value.getClass();
              type = cl.getCanonicalName();

              sb.append("\n    + ").append(type).append("@").append(
                  Integer.toHexString(value.hashCode()));

              sb.append(" value = {\n        ");
              if (value instanceof Collection) {
                CollectionUtils.toString(sb, (Collection<?>) value);
              } else if (value instanceof Map) {
                CollectionUtils.toString(sb, (Map<?, ?>) value);
              } else if (value instanceof Reference) {
                sb.append(((Reference<?>) value).get());
              } else if (cl.isArray()) {
                // check all kind of primitive types :
                if (cl == byte[].class) {
                  sb.append(Arrays.toString((byte[]) value));
                } else if (cl == short[].class) {
                  sb.append(Arrays.toString((short[]) value));
                } else if (cl == int[].class) {
                  sb.append(Arrays.toString((int[]) value));
                } else if (cl == long[].class) {
                  sb.append(Arrays.toString((long[]) value));
                } else if (cl == char[].class) {
                  sb.append(Arrays.toString((char[]) value));
                } else if (cl == float[].class) {
                  sb.append(Arrays.toString((float[]) value));
                } else if (cl == double[].class) {
                  sb.append(Arrays.toString((double[]) value));
                } else if (cl == boolean[].class) {
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

          logD.warn("\n- Possible ThreadLocal leaks for thread : " + thread.getName() 
                  + " [" + leakCount  + " / " + threadLocalCount + "] {"
                  + sb.toString());
        }

      }
    } catch (final Exception e) {
      logD.error("ThreadLocalCleaner.checkThreadLocals : failure : ", e);
    }
  }

}