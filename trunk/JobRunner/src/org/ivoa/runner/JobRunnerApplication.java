package org.ivoa.runner;

import org.ivoa.bean.LogSupport;
import org.ivoa.util.PollingThread;
import org.ivoa.util.runner.LocalLauncher;

/**
 *
 * @author laurent bourges (voparis)
 */
public final class JobRunnerApplication extends LogSupport {
    // constants :

    private final static long LAPSE = 30 * 60 * 1000l; // 30 minutes
    // constants :
    private final static long REMOVE_DELAY = 20 * 60 * 1000l; // 20 minutes
    // members :
    /** pruning queue thread */
    private static PollingThread pollTh = null;

    /**
     * Private Constructor
     */
    private JobRunnerApplication() {
    }

    public static void onInit() {

        // clean up plot folder (plots) :

        log.error("SKIP = cleaning runner folder");
        /*
          FileManager.purgeRunnerFolder();
         */

        try {
            LocalLauncher.setQueueRemovePolicy(true);

            LocalLauncher.startUp();

        } catch (RuntimeException re) {
            log.error("Application failure : ", re);
            throw re;
        }
        // create a looping thread with a lapse of time :
        pollTh = new PollingThread(LAPSE) {

            public final void handle() {
                LocalLauncher.purgeTerminated(REMOVE_DELAY);
            }
        };
        if (log.isInfoEnabled()) {
            log.info("SessionMonitor : starting monitoring thread ...");
        }

        // starts thread :
        pollTh.start();

        if (log != null) {
            log.warn("Application is ready.");
        }
    }

    // Exit :
    public static void onExit() {

        pollTh.interrupt();

        try {
            LocalLauncher.shutdown();
        } catch (RuntimeException re) {
            log.error("Application failure : ", re);
        }
    }
}
