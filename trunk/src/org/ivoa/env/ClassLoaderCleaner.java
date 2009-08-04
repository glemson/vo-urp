package org.ivoa.env;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.logging.Log;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.CollectionUtils;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.util.concurrent.ThreadLocalMapUtils;

/**
 * Cleans singleton before ClassLoader gets cleared (to ensure good gc)
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ClassLoaderCleaner {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** flag to indicate to clear static references found in classes of the current class loader */
  public final static boolean CLEAR_STATIC_REFERENCES = false;
  /**
   * Logger for the base framework
   * 
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = LogUtil.getLoggerBase();

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Forbidden Constructor
   */
  private ClassLoaderCleaner() {
    /* no-op */
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * clean up resource located in the classLoader like factories, singletons ...
   */
  public static void clean() {
    if (logB.isInfoEnabled()) {
      logB.info("ClassLoaderCleaner.clean : enter");
    }

    /* release Singleton resources */
    SingletonSupport.onExit();

    // ThreadLocal checks :
    ThreadLocalCleaner.cleanAndcheckThreads();

    if (CLEAR_STATIC_REFERENCES) {
      // Application or Web Application Class Loader to clean up :
      final ClassLoader appClazzLoader = ClassLoaderCleaner.class.getClassLoader();

      final String[] excludedPackages = {"org.apache.log4j"};
      final String[] includedPackages = {"org.ivoa"};

      clearReferences(appClazzLoader, JavaUtils.asList(excludedPackages), JavaUtils.asList(includedPackages));
    }

    // free Reflection caches :
    ThreadLocalMapUtils.clearFields();

    // Java Beans intrspection cache :
    Introspector.flushCaches();

    if (logB.isInfoEnabled()) {
      logB.info("ClassLoaderCleaner.clean : exit");
    }
  }

  /**
   * Clear static references for loaded classes in the given class loader excluding those
   * corresponding to the exclude package argument
   * 
   * @param clazzLoader class loader to process
   * @param excludedPackages list of package prefixes to exclude in the clean up process
   * @param includedPackages list of package prefixes to include in the clean up process
   */
  protected static void clearReferences(final ClassLoader clazzLoader, final List<String> excludedPackages, final List<String> includedPackages) {
    if (logB.isWarnEnabled()) {
      logB.warn("ClassLoaderCleaner.clearReferences : enter : " + clazzLoader + "\n Excluded packages : " + CollectionUtils.toString(excludedPackages));
    }

    final Class<?>[] classes = ClassScope.getLoadedClasses(clazzLoader);

    if (logB.isInfoEnabled()) {
      logB.info("ClassLoaderCleaner.clearReferences : loaded classes : " + CollectionUtils.toString(JavaUtils.asList(classes)));
    }

    // Null out any static or final fields from loaded classes,
    // as a workaround for apparent garbage collection bugs

    for (Class<?> clazz : classes) {
      // skip this class & excluded classes :
      if (checkClass(excludedPackages, includedPackages, clazz)) {
        clearReference(clazzLoader, clazz);
      }
    }

    if (logB.isWarnEnabled()) {
      logB.warn("ClassLoaderCleaner.clearReferences : exit");
    }

  }

  /**
   * Filter this class and classes which package starts with any prefix given in the
   * excludedPackages list
   * 
   * @param excludedPackages list of package prefixes to exclude in the clean up process
   * @param includedPackages list of package prefixes to include in the clean up process
   * @param clazz class to check
   * @return true if the class can be processed
   */
  private static boolean checkClass(final List<String> excludedPackages, final List<String> includedPackages, final Class<?> clazz) {
    boolean result = !clazz.equals(ClassLoaderCleaner.class) && !clazz.equals(ClassScope.class);

    if (result) {
      String className;
      for (String packagePrefix : includedPackages) {
        className = clazz.getName();
        if (!className.startsWith(packagePrefix)) {
          if (logB.isDebugEnabled()) {
            logB.debug("ClassLoaderCleaner.checkClass : skip class : " + className);
          }
          result = false;
          break;
        }
      }
      if (result) {
        for (String packagePrefix : excludedPackages) {
          className = clazz.getName();
          if (className.startsWith(packagePrefix)) {
            if (logB.isDebugEnabled()) {
              logB.debug("ClassLoaderCleaner.checkClass : skip class : " + className);
            }
            result = false;
            break;
          }
        }
      }
    }

    return result;
  }

  /**
   * Clear static references in the given class
   * 
   * @param clazzLoader class loader to process
   * @param clazz class to clean up
   */
  protected static void clearReference(final ClassLoader clazzLoader, final Class<?> clazz) {
    boolean used = false;
    try {
      Field field;
      int mods;
      final Field[] fields = clazz.getDeclaredFields();
      for (int i = 0, size = fields.length; i < size; i++) {
        field = fields[i];
        mods = field.getModifiers();
        if (field.getType().isPrimitive() || (field.getName().indexOf("$") != -1)) {
          continue;
        }
        if (Modifier.isStatic(mods)) {
          if (!used && logB.isWarnEnabled()) {
            logB.warn("ClassLoaderCleaner.clearReference : enter : " + clazz.getName());
            logB.warn("{");
          }
          used = true;
          if (logB.isWarnEnabled()) {
            logB.warn("ClassLoaderCleaner.clearReference : clear static field [" + field + "] ? ");
          }
          try {
            field.setAccessible(true);
            if (Modifier.isFinal(mods)) {
              if (!((field.getType().getName().startsWith("java.")) || (field.getType().getName().startsWith("javax.")))) {
                nullInstance(clazzLoader, field.get(null));
              }
            } else {
              field.set(null, null);
              if (logB.isWarnEnabled()) {
                logB.warn("ClassLoaderCleaner.clearReference : Set field [" + field + "] to null in class " + clazz.getName());
              }
            }
          } catch (Throwable t) {
            if (logB.isWarnEnabled()) {
              logB.warn("ClassLoaderCleaner.clearReference : Could not set field [" + field + "] to null in class " + clazz.getName(), t);
            }
          }
        }
      }
    } catch (Throwable t) {
      if (logB.isWarnEnabled()) {
        logB.warn("ClassLoaderCleaner.clearReference : Could not clean fields for class " + clazz.getName(), t);
      }
    }
    if (used && logB.isWarnEnabled()) {
      logB.warn("}");
      logB.warn("ClassLoaderCleaner.clearReference : exit : " + clazz.getName());
    }
  }

  /**
   * Clear the given object instance
   * 
   * @param clazzLoader class loader to process
   * @param instance object to clear
   */
  protected static void nullInstance(final ClassLoader clazzLoader, final Object instance) {
    if (instance == null) {
      return;
    }
    final Class<?> clazz = instance.getClass();

    Field field;
    Object value;
    Class<?> valueClass;
    int mods;
    final Field[] fields = clazz.getDeclaredFields();
    for (int i = 0, size = fields.length; i < size; i++) {
      field = fields[i];
      mods = field.getModifiers();
      if (field.getType().isPrimitive() || (field.getName().indexOf("$") != -1)) {
        continue;
      }
      try {
        field.setAccessible(true);
        if (Modifier.isStatic(mods) && Modifier.isFinal(mods)) {
          // Doing something recursively is too risky
          continue;
        }
        value = field.get(instance);
        if (value != null) {
          valueClass = value.getClass();
          if (!loadedByThisOrChild(clazzLoader, valueClass)) {
            if (logB.isWarnEnabled()) {
              logB.warn("ClassLoaderCleaner.nullInstance : Not setting field [" + field + "] to null in object of class " + clazz.getName() + " because the referenced object was of type " + valueClass.getName() + " which was not loaded by the same classLoader.");
            }
          } else {
            field.set(instance, null);
            if (logB.isWarnEnabled()) {
              logB.warn("ClassLoaderCleaner.nullInstance : Set field [" + field + "] to null in class " + clazz.getName());
            }
          }
        }
      } catch (Throwable t) {
        if (logB.isWarnEnabled()) {
          logB.warn("ClassLoaderCleaner.nullInstance : Could not set field [" + field + "] to null in object instance of class " + clazz.getName(), t);
        }
      }
    }
  }

  /**
   * Determine whether a class was loaded by this class loader or one of its child class loaders.
   * 
   * @param clazzLoader class loader to process
   * @param clazz class to check
   * @return true if the given class belongs to the given class loader
   */
  protected static boolean loadedByThisOrChild(final ClassLoader clazzLoader, final Class<?> clazz) {
    boolean result = false;
    for (ClassLoader classLoader = clazz.getClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
      if (classLoader.equals(clazzLoader)) {
        result = true;
        break;
      }
    }
    return result;
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
