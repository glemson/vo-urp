package org.ivoa.env;

import java.lang.reflect.Field;
import java.util.Vector;

import org.ivoa.util.JavaUtils;

// ----------------------------------------------------------------------------
/**
 * A simple static API for listing classes loaded in a JVM. from
 * http://www.javaworld.com/javaworld/javaqa/2003-07/01-qa-0711-classsrc.html also thrown in for a
 * good measure. NOTE: use for testing/debugging only.
 * 
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ClassScope {

  /** field ClassLoader.classes set in static class initializer [can be null] */
  private static final Field CLASSES_VECTOR_FIELD;

  /** empty class Array */
  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

  /** exception set in static class initializer */
  private static Throwable CVF_FAILURE; // 

  static {
    Throwable failure = null;

    Field tempf = null;
    try {
      // this can fail if this is not a Sun-compatible JVM
      // or if the security is too tight:

      tempf = ClassLoader.class.getDeclaredField("classes");
      if (tempf.getType() != Vector.class) {
        throw new RuntimeException("not of type java.util.Vector: " + tempf.getType().getName());
      }

      tempf.setAccessible(true);
    } catch (Throwable t) {
      failure = t;
    }
    CLASSES_VECTOR_FIELD = tempf;
    CVF_FAILURE = failure;
  }

  /**
   * Forbidden constructor
   */
  private ClassScope() {
    /* no-op */
  }

  // public: ................................................................
  /**
   * Given a class loader instance, returns all classes currently loaded by that class loader.
   * 
   * @param loader class loader to inspect [may not be null]
   * @return Class array such that every Class has 'loader' as its defining class loader [never
   *         null, may be empty]
   * @throws RuntimeException if the "classes" field hack is not possible in this JRE
   */
  public static Class<?>[] getLoadedClasses(final ClassLoader loader) {
    if (loader == null) {
      throw new IllegalArgumentException("null input: loader");
    }
    if (CLASSES_VECTOR_FIELD == null) {
      throw new RuntimeException("ClassScope::getLoadedClasses() cannot be used in this JRE", CVF_FAILURE);
    }

    try {
      @SuppressWarnings("unchecked")
      final Vector<Class<?>> classes = (Vector<Class<?>>) CLASSES_VECTOR_FIELD.get(loader);
      if (JavaUtils.isEmpty(classes)) {
        return EMPTY_CLASS_ARRAY;
      }

      final Class<?>[] result;

      // note: Vector is synchronized in Java 2, which helps us make
      // the following into a safe critical section:

      synchronized (classes) {
        result = new Class[classes.size()];
        classes.toArray(result);
      }

      return result;
    } catch (IllegalAccessException e) {
      // this should not happen if the static class initializer was successful:
      e.printStackTrace(System.out);
    }
    return EMPTY_CLASS_ARRAY;
  }

} // end of class
// ----------------------------------------------------------------------------