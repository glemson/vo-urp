package org.ivoa.util;

import java.lang.reflect.Field;
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
            log.error(
                    "Unable to find class [" + className + "] in classpath : " + System.getProperty("java.class.path"),
                    cnfe);
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
            log.error(
                    "Unable to find class [" + className + "] in classpath : " + System.getProperty("java.class.path"),
                    cnfe);
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
            throw new IllegalStateException("unable to create instance for class : " + clazz, e);
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
    public static Field getField(final Class clazz, final String name) {
        try {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException nsfe) {
            throw new IllegalStateException("no such field[" + name + "] in class + " + clazz.getName(), nsfe);
        } catch (SecurityException se) {
            throw new IllegalStateException("security exception to access field[" + name + "] in class + " + clazz.getName(), se);
        }
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
