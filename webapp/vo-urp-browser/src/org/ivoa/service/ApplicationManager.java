package org.ivoa.service;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.servlet.InitShutdownController;

/**
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ApplicationManager implements ServletContextListener {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /** diagnostics flag */
    public final static boolean ENABLE_DIAGNOSTICS = false;

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Constructor
     */
    public ApplicationManager() {
        if (ENABLE_DIAGNOSTICS) {
            System.out.println("ApplicationManager new");
        }
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * TODO : Method Description
     *
     * @param sce
     */
    public void contextInitialized(final ServletContextEvent sce) {
        if (ENABLE_DIAGNOSTICS) {
            System.out.println("ApplicationManager contextInitialized");
        }
        final ServletContext ctx = sce.getServletContext();
        // initialize Log4J with this Class Loader :
        InitShutdownController.initializeLog4j(ctx);

        VO_URP_Facade.createInstance(ctx);
    }

    /**
     * TODO : Method Description
     *
     * @param sce
     */
    public void contextDestroyed(final ServletContextEvent sce) {
        if (ENABLE_DIAGNOSTICS) {
            System.out.println("ApplicationManager contextDestroyed");
        }
        final ServletContext ctx = sce.getServletContext();

        VO_URP_Facade.freeInstance(ctx);

        /*
        In the case of shutdown we are concerned with cleaning up loggers and
        appenders within the Hierarchy that the current application is using for logging.
        If we don't do this, there is a chance that, for instance, file appenders won't
        give up handles to files they are using for logging which would leave them in a
        locked state until the current JVM is shut down. This would entail a full shutdown
        of the application server in order to release locks on log files. Using this
        servlet context listener ensures that locks will be released without requiring
        a full server shutdown.
         */
        InitShutdownController.shutdownLog4j(ctx);
    }
}
