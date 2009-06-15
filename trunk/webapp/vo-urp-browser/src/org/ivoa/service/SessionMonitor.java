package org.ivoa.service;

import org.ivoa.util.PollingThread;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.ivoa.bean.LogSupport;

/**
 * Session monitor :
 *
 * @author laurent
 */
public final class SessionMonitor extends LogSupport implements HttpSessionListener {
    //~ Constants --------------------------------------------------------------------------------------------------------

    /**
     * TODO : Field Description
     */
    public static final String SESSION_NO = "session-no";
    /**
     * TODO : Field Description
     */
    public static final String SESSION_IP = "session-ip";
    /**
     * TODO : Field Description
     */
    public static final String SESSION_HOST = "session-host";
    /**
     * TODO : Field Description
     */
    private static final long LAPSE = 10 * 60 * 1000L; // 10 minutes
    /** stats thread */
    private static PollingThread pollTh = null;

    //~ Members ----------------------------------------------------------------------------------------------------------

    /* membres */
    /** live session count */
    private final AtomicInteger sessionCount = new AtomicInteger(0);
    /** total session count */
    private final AtomicInteger sessionTotal = new AtomicInteger(0);
    /** last total logged */
    private int lastTotal = -1;
    /** last live logged */
    private int lastLive = -1;

    //~ Constructors -----------------------------------------------------------------------------------------------------
    /**
     * Public constructor
     */
    public SessionMonitor() {
        System.out.println("SessionMonitor new");
        init();
    }

    //~ Methods ----------------------------------------------------------------------------------------------------------
    /**
     * TODO : Method Description
     */
    protected static void onExit() {
        if (logD.isWarnEnabled()) {
            logD.warn("SessionMonitor.onExit : enter");
        }

        if (pollTh != null) {
            // stop polling thread :
            pollTh.stopAndWait();

            try {
                // to be sure the thread died (exit run() method)
                Thread.sleep(100L);
            } catch (InterruptedException ie) {
                if (logD.isErrorEnabled()) {
                    logD.error("SessionMonitor.onExit : interrupted : ", ie);
                }
            }

            // force GC :
            pollTh = null;
        }

        if (logD.isWarnEnabled()) {
            logD.warn("SessionMonitor.onExit : exit");
        }
    }

    /**
     * TODO : Method Description
     */
    public void init() {
        if (pollTh == null) {
            // create a looping thread with a lapse of time :
            pollTh = new PollingThread(LAPSE) {

                public final void handle() {
                    final int live = sessionCount.get();
                    final int total = sessionTotal.get();

                    if ((live > lastLive) || (total > lastTotal)) {
                        // fast simple barrier :
                        lastLive = live;
                        lastTotal = total;

                        if (logD.isWarnEnabled()) {
                            logD.warn("SessionMonitor : Live sessions : " + live + " / Total sessions created : " + total);
                        }
                    }
                }
            };

            if (logD.isInfoEnabled()) {
                logD.info("SessionMonitor : starting monitoring thread ...");
            }

            // starts thread :
            pollTh.start();
        }
    }

    /**
     * TODO : Method Description
     *
     * @param se
     */
    public void sessionCreated(final HttpSessionEvent se) {
        sessionCount.incrementAndGet();

        final String no = "S:" + String.valueOf(sessionTotal.incrementAndGet());

        final HttpSession session = se.getSession();

        session.setAttribute(SESSION_NO, no);

        if (logD.isInfoEnabled()) {
            logD.info("New Session created with ID : " + session.getId());
        }
    }

    /**
     * TODO : Method Description
     *
     * @param se
     */
    public void sessionDestroyed(final HttpSessionEvent se) {
        sessionCount.decrementAndGet();

        if (logD.isWarnEnabled()) {
            final HttpSession session = se.getSession();

            final String ip = (String) session.getAttribute(SessionMonitor.SESSION_IP);
            final String host = (String) session.getAttribute(SessionMonitor.SESSION_HOST);
            final String no = getSessionNo(session);

            logD.warn("Session [" + no + "] destroyed with ID : " + session.getId() + " from : [" + host + ", " + ip + "]");
        }
    }

    /* callback */
    /**
     * TODO : Method Description
     *
     * @param session
     * @param request
     */
    public static void attachIP(final HttpSession session, final HttpServletRequest request) {
        if ((session != null) && (session.getAttribute(SessionMonitor.SESSION_IP) == null)) {
            final String ip = request.getRemoteAddr();
            final String host = request.getRemoteHost();

            session.setAttribute(SessionMonitor.SESSION_IP, ip);
            session.setAttribute(SessionMonitor.SESSION_HOST, host);

            final String no = getSessionNo(session);

            if (logD.isWarnEnabled()) {
                logD.warn("Session [" + no + "] created   with ID : " + session.getId() + " from : [" + host + ", " + ip + "]");
            }
        }
    }

    /**
     * TODO : Method Description
     *
     * @param session
     *
     * @return value TODO : Value Description
     */
    public static String getSessionNo(final HttpSession session) {
        return (String) session.getAttribute(SessionMonitor.SESSION_NO);
    }
}
//~ End of file --------------------------------------------------------------------------------------------------------
