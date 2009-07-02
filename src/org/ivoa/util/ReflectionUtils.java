package org.ivoa.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

import org.ivoa.bean.LogSupport;

/**
 * Reflection tools
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ReflectionUtils extends LogSupport {

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Forbidden Constructor
     */
    private ReflectionUtils() {
        /* no-op */
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Return a class object for the given full class name (with package)
     *
     * @param className full class name (with package)
     * @return class or null if not found
     */
    @SuppressWarnings("unchecked")
    public static Class findClass(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException cnfe) {
      log.error("ReflectionUtils.findClass : Unable to find class [" + className + "] in classpath : " + System.getProperty("java.class.path"), cnfe);
        }

        return null;
    }

    /**
     * Return a class object for the given full class name (with package).
     *
     * @param <T> super Class Type
     * @param className full class name (with package)
     * @param type super type for that class
     * @return class or null if not found
     */
    public static <T> Class<? extends T> findClass(final String className, final Class<T> type) {
        try {
            return Class.forName(className).asSubclass(type);
        } catch (final ClassNotFoundException cnfe) {
      log.error("ReflectionUtils.findClass : Unable to find class [" + className + "] in classpath : " + System.getProperty("java.class.path"), cnfe);
        }

        return null;
    }

    /**
     * Factory implementation : creates new instance for given class
     *
     * @param <T> super Class Type
     * @param clazz class (should not be real implementation class, not an abstract class)
     * @return new instance
     * @throws IllegalStateException if it is not possible to create an instance
     */
    public static <T> T newInstance(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (final Exception e) {
      throw new IllegalStateException("Unable to create instance for class : " + clazz, e);
        }
    }

    /**
     * Return a field object for the given class and field name
     *
     * @param clazz class to inspect
     * @param name field name to get
     * @return Field instance
     * @throws IllegalStateException if field not found or security issue
     */
  public static Field getField(final Class<?> clazz, final String name) {
        try {
            final Field field = clazz.getDeclaredField(name);

      final Boolean result = AccessController.doPrivileged(new SetAccessibleAction(field));
      if (!result.booleanValue()) {
        throw new IllegalStateException("Security exception to define the accessibility of the field[" + field.getName() + "]");
      }
            return field;
    } catch (final NoSuchFieldException nsfe) {
      throw new IllegalStateException("No such field[" + name + "] in class + " + clazz.getName(), nsfe);
    } catch (final SecurityException se) {
      throw new IllegalStateException("Security exception to access field[" + name + "] in class + " + clazz.getName(), se);
        }
    }

  /**
   * Return the field value for the given field applied to the given subject instance
   * 
   * @param field field to use
   * @param subject object instance
   * @return value or null if any exception occurred
   */
  public static Object getFieldValue(final Field field, final Object subject) {
    Object value = null;
    try {
      value = field.get(subject);
    } catch (final IllegalAccessException iae) {
      logD.error("ReflectionUtils.getFieldValue : failure : ", iae);
    } catch (final IllegalArgumentException iarge) {
      logD.error("ReflectionUtils.getFieldValue : failure : ", iarge);
    }
    return value;
  }

  /**
   * Return a method object for the given class, method name and parameters
   * 
   * @param clazz class to inspect
   * @param name method name to get
   * @param parameterType method parameter or null
   * @return Method instance
   * @throws IllegalStateException if field not found or security issue
   */
  public static Method getMethod(final Class<?> clazz, final String name, final Class<?> parameterType) {
    return getMethod(clazz, name, new Class<?>[] { parameterType });
  }

  /**
   * Return a method object for the given class, method name and parameters
   * 
   * @param clazz class to inspect
   * @param name method name to get
   * @param parameterTypes method parameters or null
   * @return Method instance
   * @throws IllegalStateException if field not found or security issue
   */
  public static Method getMethod(final Class<?> clazz, final String name, final Class<?>[] parameterTypes) {
    try {
      final Method method = clazz.getDeclaredMethod(name, parameterTypes);

      final Boolean result = AccessController.doPrivileged(new SetAccessibleAction(method));
      if (!result.booleanValue()) {
        throw new IllegalStateException("Security exception to define the accessibility of the field[" + method.getName() + "]");
      }

      return method;
    } catch (final NoSuchMethodException nsfe) {
      throw new IllegalStateException("No such method[" + name + "] in class + " + clazz.getName(), nsfe);
    } catch (final SecurityException se) {
      throw new IllegalStateException("Security exception to access method[" + name + "] in class + " + clazz.getName(), se);
    }
  }

  /**
   * Invoke the method applied to the given subject instance with given parameters
   * 
   * @param method method to use
   * @param subject object instance
   * @param parameters method parameter values or null
   * @return value or null if any exception occurred
   */
  public static Object invokeMethod(final Method method, final Object subject, final Object[] parameters) {
    Object value = null;
    try {
      if (logD.isDebugEnabled()) {
        log.debug("ReflectionUtils.invokeMethod : method  = " + method);
        log.debug("ReflectionUtils.invokeMethod : subject = " + subject);
        log.debug("ReflectionUtils.invokeMethod : parameters = " + Arrays.toString(parameters));
      }
      
      value = method.invoke(subject, parameters);
    } catch (final IllegalAccessException iae) {
      logD.error("ReflectionUtils.invokeMethod : failure : ", iae);
    } catch (final IllegalArgumentException iarge) {
      logD.error("ReflectionUtils.invokeMethod : failure : ", iarge);
    } catch (final InvocationTargetException ite) {
      logD.error("ReflectionUtils.invokeMethod : failure : ", ite);
    }
    return value;
  }

  /**
   * This private class implements the PrivilegedAction pattern to change the object accessibility
   */
  private final static class SetAccessibleAction extends LogSupport implements PrivilegedAction<Boolean> {

    /** field to change */
    private final AccessibleObject object;

    /**
     * Protected constructor with the given field
     * 
     * @param pObject field to change
     */
    protected SetAccessibleAction(final AccessibleObject pObject) {
      this.object = pObject;
    }

    /**
     * Use the secured method Field.setAccessible(boolean)
     * 
     * @see Field#setAccessible(boolean)
     * @return true if successful, false if SecurityException occurred
     */
    public final Boolean run() {
      try {
        this.object.setAccessible(true);
      } catch (final SecurityException se) {
        log.error("FieldAccessible.run : Security exception to set the accessibility of the object[" + this.object + "]", se);
      }

      return Boolean.TRUE;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
