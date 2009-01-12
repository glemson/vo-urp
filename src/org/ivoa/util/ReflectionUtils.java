package org.ivoa.util;

import org.apache.commons.logging.Log;


/**
 * Reflection tools :
 *
 * @author laurent bourges (voparis)
 */
public final class ReflectionUtils {

  /** logger */
  private final static Log log = LogUtil.getLoggerDev();

  /**
   * Constructor
   */
  private ReflectionUtils() {
  }

  public static Class findClass(final String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException cnfe) {
      log.error("Unable to find class [" + className + "] in classpath : " + System.getProperty("java.class.path"), cnfe);
    }
    return null;
  }

  public static <T> Class<? extends T> findClass(final String className, final Class<T> type) {
    try {
      return Class.forName(className).asSubclass(type);
    } catch (ClassNotFoundException cnfe) {
      log.error("Unable to find class [" + className + "] in classpath : " + System.getProperty("java.class.path"), cnfe);
    }
    return null;
  }
  
  /**
   * Factory implementation : creates new instance for given class
   *
   * @param <T> 
   * @param cl class (should not be real implementation class, not an abstract class)
   *
   * @return new instance or null
   *
   * @throws IllegalStateException
   */
  public static <T> T newInstance(final Class<T> cl) {
    try {
      return cl.newInstance();
    } catch (final Exception e) {
      throw new IllegalStateException("unable to create instance for class : " + cl, e);
    }
  }
}
