package org.ivoa.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.util.concurrent.LinkedBlockingQueue;
import org.ivoa.util.CollectionUtils;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.LocalStringBuilder;
import org.ivoa.util.concurrent.ThreadLocalUtils;

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
    public static final boolean SINGLETON_SUPPORT_DIAGNOSTICS = true;
    /** shutdown flag to avoid singleton references to be kept after shutdown process (java 5 memory model) */
    private static volatile boolean isShutdown = false;
    /** instances to monitor = queue [SingletonSupport instance] */
    private static LinkedBlockingQueue<SingletonSupport> managedInstances = new LinkedBlockingQueue<SingletonSupport>();

    static {
        // prepare ThreadLocalUtils as the first singleton to be release the last :
        ThreadLocalUtils.getInstance();

        // prepare LocalStringBuilder as the second singleton :
        LocalStringBuilder.prepareInstance();
    }
    /**
     * Returns true if shutdown flag is not set
     *
     * @return true if shutdown flag is not set
     */
    protected static final boolean isRunning() {
        if (SINGLETON_SUPPORT_DIAGNOSTICS && isShutdown) {
            System.out.println("SingletonSupport.isRunning : shutdown detected : ");
            new Throwable().printStackTrace(System.out);
        }

        return !isShutdown;
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
        T managedInstance = null;
        if (singleton != null) {
            try {
                // initialize :
                singleton.initialize();

                if (log.isInfoEnabled()) {
                    log.info("SingletonSupport.prepareInstance : new singleton ready " + getSingletonLogName(singleton));
                }

                if (isRunning()) {
                    // auto register this new instance
                    register(singleton);

                    // keep reference in singleton class :
                    managedInstance = singleton;
                } else {
                    log.error("SingletonSupport.prepareInstance : shutdown detected for singleton " + getSingletonLogName(singleton), new Throwable());
                }

            } catch (final RuntimeException re) {
                log.error("SingletonSupport.prepareInstance : runtime failure " + getSingletonLogName(singleton), re);

                // release this phantom instance (bad state) :
                onExit(singleton);

                throw re;
            }

        }
        return managedInstance;
    }

    /**
     * Return the singleton class name
     *
     * @param singleton SingletonSupport instance
     * @return singleton class name (fully qualified)
     */
    protected static final String getSingletonName(final SingletonSupport singleton) {
        return singleton.getClass().getName();
    }

    /**
     * Return the singleton log message [ #getSingletonName(singleton) = #singleton]
     *
     * @param singleton SingletonSupport instance
     * @return singleton log message
     */
    protected static final String getSingletonLogName(final SingletonSupport singleton) {
        return "[" + singleton + "]";
    }

    /**
     * Register the given singleton to the managed instances
     *
     * @param singleton SingletonSupport instance
     */
    public static final void register(final SingletonSupport singleton) {
        if (log.isInfoEnabled()) {
            log.info("SingletonSupport.register : add " + getSingletonLogName(singleton));
        }

        if (isRunning()) {
            managedInstances.add(singleton);
        } else {
            log.error("SingletonSupport.register : shutdown detected for singleton " + getSingletonLogName(singleton), new Throwable());
        }

    }

    /**
     * UnRegister the given singleton to the managed instances
     *
     * @param singleton instance of SingletonSupport
     */
    public static final void unregister(final SingletonSupport singleton) {
        if (log.isInfoEnabled()) {
            log.info("SingletonSupport.unregister : remove " + getSingletonLogName(singleton));
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
        isShutdown = true;
        if (log.isWarnEnabled()) {
            log.warn("SingletonSupport.onExit : enter");
        }

        if (!JavaUtils.isEmpty(managedInstances)) {
                // clean up :
                SingletonSupport singleton;

                final List<SingletonSupport> instances = new ArrayList<SingletonSupport>(managedInstances.size());

                managedInstances.drainTo(instances);

                if (log.isWarnEnabled()) {
                    log.warn("SingletonSupport.onExit : instances to free : " + CollectionUtils.toString(instances));
                }

                // reverse singleton ordering :
                Collections.reverse(instances);

                for (final Iterator<SingletonSupport> it = instances.iterator(); it.hasNext();) {
                    singleton = it.next();

                    onExit(singleton);

                    it.remove();
                }
            }
            managedInstances = null;

        if (log.isWarnEnabled()) {
            log.warn("SingletonSupport.onExit : exit");
        }
    }

    /**
     * Called on exit (clean up code)
     *
     * @param singleton instance of SingletonSupport
     */
    protected static final void onExit(final SingletonSupport singleton) {
        if (singleton != null) {
            if (log.isWarnEnabled()) {
                log.warn("SingletonSupport.onExit : clear : " + getSingletonLogName(singleton) + " : enter");
            }
            try {
                // clear instance fields :
                singleton.clear();

                // clear static references :
                singleton.clearStaticReferences();

            } catch (final RuntimeException re) {
                log.error("SingletonSupport.onExit : runtime failure " + getSingletonLogName(singleton), re);
            }
            if (log.isWarnEnabled()) {
                log.warn("SingletonSupport.onExit : clear : " + getSingletonLogName(singleton) + " : exit");
            }
        }
    }

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Protected Constructor to avoid creating instances except by singleton pattern : getInstance()
     */
    protected SingletonSupport() {
        super();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
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
//~ End of file --------------------------------------------------------------------------------------------------------
