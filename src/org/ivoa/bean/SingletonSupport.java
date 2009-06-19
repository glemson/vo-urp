package org.ivoa.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.concurrent.FastSemaphore;

/**
 * Singleton design pattern implementation for Java 5+
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public abstract class SingletonSupport extends LogSupport {

    /** internal semaphore (avoid synchronized blocks) */
    private static final FastSemaphore SEM = new FastSemaphore(1);
    /** instances to monitor */
    private static List<SingletonSupport> instances = new ArrayList<SingletonSupport>();

    /**
     * Register the given singleton to the managed instances
     * @param singleton instance of SingletonSupport
     */
    public static final void register(final SingletonSupport singleton) {
        if (log.isDebugEnabled()) {
            log.debug("SingletonSupport.register : adds " + singleton);
        }

        try {
            // semaphore is acquired to protect instances :
            instances.add(singleton);

        } finally {
            // semaphore is released :
            SEM.release();
        }

    }

    /**
     * UnRegister the given singleton to the managed instances
     * @param singleton instance of SingletonSupport
     */
    public static final void unregister(final SingletonSupport singleton) {
        if (log.isDebugEnabled()) {
            log.debug("SingletonSupport.unregister : removes " + singleton);
        }
        try {
            // semaphore is acquired to protect instances :
            instances.remove(singleton);

        } finally {
            // semaphore is released :
            SEM.release();
        }
    }

    /**
     * Called on exit (clean up code)
     */
    public static final void onExit() {
        if (log.isWarnEnabled()) {
            log.warn("SingletonSupport.onExit : enter");
        }
        try {
            // semaphore is acquired to protect instances :
            if (!JavaUtils.isEmpty(instances)) {
                // clean up :
                SingletonSupport s;

                for (Iterator<SingletonSupport> it = instances.iterator(); it.hasNext();) {
                    s = it.next();

                    if (s != null) {
                        if (logD.isDebugEnabled()) {
                            logD.debug("SingletonSupport.onExit : clear : " + s);
                        }

                        s.clear();
                    }

                    it.remove();
                }
            }
            instances = null;

        } finally {
            // semaphore is released :
            SEM.release();
        }


        if (log.isWarnEnabled()) {
            log.warn("SingletonSupport.onExit : exit");
        }
    }

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Protected Constructor<br/>
     * Register this new instance in managed instances
     */
    protected SingletonSupport() {
        super();

        // auto register this new instance
        register(this);
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * Abstract method to be implemented by concrete implementations :
     * Callback to initialize this SingletonSupport instance
     */
    protected void initialize() {
        /* no-op */
    }

    /**
     * Abstract method to be implemented by concrete implementations :
     * Callback to clean up this SingletonSupport instance
     */
    protected abstract void clear();

//~ End of file --------------------------------------------------------------------------------------------------------
}
