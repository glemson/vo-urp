package org.ivoa.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.SystemLogUtil;
import org.ivoa.util.concurrent.ThreadLocalUtils;
import org.ivoa.util.text.LocalStringBuilder;
import org.ivoa.xml.XmlFactory;

/**
 * Singleton design pattern implementation for Java 5+.<br/>
 *
 * TODO : Remove LogSupport dependency and set/release log instances at runtime (start / stop)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public abstract class SingletonSupport extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** internal diagnostic FLAG */
  public static final boolean SINGLETON_SUPPORT_DIAGNOSTICS = false;

  /**
   * Enumeration of life cycle status
   */
  private static enum STATUS {

    /** initial state */
    INITIAL,
    /** state to indicate if the Singleton Factory is ready */
    READY,
    /** to avoid singleton references to be kept after shutdown process */
    STOP
  }
  /** internal lifecycle state */
  private static volatile STATUS STATE = STATUS.INITIAL;
  /** instances to monitor = queue [SingletonSupport instance] */
  private static LinkedBlockingQueue<SingletonSupport> managedInstances = new LinkedBlockingQueue<SingletonSupport>();

  /**
   * Prepare the SingletonSupport
   */
  protected static final void prepareSingletonSupport() {
    // prepareSingletonSupport call barrier (first) :
    STATE = STATUS.READY;

    if (SINGLETON_SUPPORT_DIAGNOSTICS) {
      logB.error("SingletonSupport.prepareSingletonSupport : initialization now ...");
    }
    // prepare ThreadLocalUtils as the first singleton to be release the last :
    ThreadLocalUtils.getInstance();

    // prepare LocalStringBuilder as the second singleton :
    LocalStringBuilder.prepareInstance();

    // prepare XmlFactory :
    XmlFactory.prepareInstance();
  }

  /**
   * Returns true if shutdown flag is not set
   *
   * @return true if shutdown flag is not set
   */
  protected static final boolean isRunning() {
    if (SINGLETON_SUPPORT_DIAGNOSTICS && STATE == STATUS.STOP) {
      if (SystemLogUtil.isDebugEnabled()) {
        SystemLogUtil.debug("SingletonSupport.isRunning : shutdown detected : ");
      }
    }

    return STATE == STATUS.READY;
  }

  /**
   * Prepare the given singleton instance
   *
   * @param <T> SingletonSupport child class type
   * @param singleton SingletonSupport instance
   * @return prepared SingletonSupport instance
   * @throws IllegalStateException if a problem occurred
   */
  protected static final <T extends SingletonSupport> T prepareInstance(final T singleton) {
    /* prepare first */
    if (STATE == STATUS.INITIAL) {
      prepareSingletonSupport();
    }

    T managedInstance = null;
    if (singleton != null) {

      try {
        // initialize :
        singleton.initialize();

        if (logB.isInfoEnabled()) {
          logB.info("SingletonSupport.prepareInstance : new singleton ready " + getSingletonLogName(singleton));
        }

        if (isRunning()) {
          // auto register this new instance
          register(singleton);

          // keep reference in singleton class :
          managedInstance = singleton;
        } else {
          logB.error("SingletonSupport.prepareInstance : shutdown detected for singleton " + getSingletonLogName(singleton), new Throwable());
        }

      } catch (final RuntimeException re) {
        // info because the exception will be thrown again :
        if (logB.isInfoEnabled()) {
          logB.info("SingletonSupport.prepareInstance : runtime failure " + getSingletonLogName(singleton), re);
        }

        // release this phantom instance (bad state) :
        onExit(singleton);

        throw re;
      }

    }
    return managedInstance;
  }

  /**
   * Post Prepare the given singleton instance
   *
   * @param <T> SingletonSupport child class type
   * @param singleton SingletonSupport instance
   * @throws IllegalStateException if a problem occurred
   */
  protected static final <T extends SingletonSupport> void postPrepareInstance(final T singleton) {
    if (singleton != null) {
      try {
        singleton.postInitialize();

      } catch (final RuntimeException re) {
        // info because the exception will be thrown again :
        if (logB.isInfoEnabled()) {
          logB.info("SingletonSupport.postPrepareInstance : runtime failure " + getSingletonLogName(singleton), re);
        }

        // release this phantom instance (bad state) :
        onExit(singleton);

        throw re;
      }
    }
  }

  /**
   * Register the given singleton to the managed instances
   *
   * @param singleton SingletonSupport instance
   */
  public static final void register(final SingletonSupport singleton) {

    /* prepare first */
    if (STATE == STATUS.INITIAL) {
      prepareSingletonSupport();
    }

    if (logB.isInfoEnabled()) {
      logB.info("SingletonSupport.register : add " + getSingletonLogName(singleton));
    }
    if (isRunning()) {
      managedInstances.add(singleton);
    } else {
      logB.error("SingletonSupport.register : shutdown detected for singleton " + getSingletonLogName(singleton), new Throwable());
    }

  }

  /**
   * UnRegister the given singleton to the managed instances
   *
   * @param singleton instance of SingletonSupport
   */
  public static final void unregister(final SingletonSupport singleton) {
    if (logB.isInfoEnabled()) {
      logB.info("SingletonSupport.unregister : remove " + getSingletonLogName(singleton));
    }
    if (isRunning()) {
      managedInstances.remove(singleton);

      onExit(singleton);
    }
  }

  /**
   * Called on exit (clean up code)
   */
  public static final void onExit() {
    if (logB.isInfoEnabled()) {
      logB.info("SingletonSupport.onExit : enter");
    }

    if (!JavaUtils.isEmpty(managedInstances)) {
      // clean up :
      final List<SingletonSupport> instances = new ArrayList<SingletonSupport>(managedInstances.size());

      managedInstances.drainTo(instances);

      if (logB.isInfoEnabled()) {
        logB.info("SingletonSupport.onExit : instances to free : " + CollectionUtils.toString(instances));
      }

      // reverse singleton ordering :
      Collections.reverse(instances);

      SingletonSupport singleton;
      for (final Iterator<SingletonSupport> it = instances.iterator(); it.hasNext();) {
        singleton = it.next();

        onExit(singleton);

        it.remove();
      }
    }
    // status to indicate that onExit calls are done :
    STATE = STATUS.STOP;

    // force GC :
    managedInstances = null;

    if (logB.isInfoEnabled()) {
      logB.info("SingletonSupport.onExit : exit");
    }
  }

  /**
   * Called on exit (clean up code)
   *
   * @param singleton instance of SingletonSupport
   */
  protected static final void onExit(final SingletonSupport singleton) {
    if (singleton != null) {
      if (logB.isInfoEnabled()) {
        logB.info("SingletonSupport.onExit : clear : " + getSingletonLogName(singleton) + " : enter");
      }
      try {
        // clear instance fields :
        singleton.clear();

        // clear static references :
        singleton.clearStaticReferences();

      } catch (final RuntimeException re) {
        logB.error("SingletonSupport.onExit : runtime failure " + getSingletonLogName(singleton), re);
      }
      if (logB.isInfoEnabled()) {
        logB.info("SingletonSupport.onExit : clear : " + getSingletonLogName(singleton) + " : exit");
      }
    }
  }

  /**
   * Return the singleton class name
   *
   * @param singleton SingletonSupport instance
   * @return singleton class name (fully qualified)
   */
  public static final String getSingletonName(final Object singleton) {
    return singleton.getClass().getName();
  }

  /**
   * Return the singleton log message [ #getSingletonName(singleton) = #singleton]
   *
   * @param singleton SingletonSupport instance
   * @return singleton log message
   */
  public static final String getSingletonLogName(final Object singleton) {
    return "[" + singleton + "]";
  }

  // ~ Constructors
  // -----------------------------------------------------------------------------------------------------
  /**
   * Protected Constructor to avoid creating instances except by singleton pattern : getInstance()
   */
  protected SingletonSupport() {
    super();
  }

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to initialize this SingletonSupport instance
   *
   * @throws IllegalStateException if a problem occurred
   */
  protected void initialize() throws IllegalStateException {
    /* no-op */
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * This method must be called by concrete implementation after the singleton is defined.<br/>
   * Post Initialization pattern called after the singleton is defined
   *
   * @throws IllegalStateException if a problem occurred
   */
  protected void postInitialize() throws IllegalStateException {
    /* no-op */
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance iso
   * clear static references
   */
  protected void clearStaticReferences() {
    /* no-op */
  }

  /**
   * Empty method to be implemented by concrete implementations :<br/>
   * Callback to clean up this SingletonSupport instance iso clear instance fields
   */
  protected void clear() {
    /* no-op */
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------
