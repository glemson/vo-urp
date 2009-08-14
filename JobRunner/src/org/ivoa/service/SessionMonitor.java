package org.ivoa.service;

import org.ivoa.util.PollingThread;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.ivoa.bean.LogSupport;
import org.ivoa.util.LogUtil;


/**
 * Session monitor :
 *
 * Note : this class is the first started by tomcat so the application is not started yet.
 *
 * Implication : this class can not extend LogSupport !
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class SessionMonitor implements HttpSessionListener {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** diagnostics flag */
  public final static boolean ENABLE_DIAGNOSTICS = false;
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
  /**
   * Logger for the base framework
   * @see org.ivoa.bean.LogSupport
   */
  protected static Log logB = null;
  /** singleton instance (java 5 memory model) */
  private static volatile SessionMonitor instance = null;
  /** stats thread */
  private static PollingThread pollTh = null;
  //~ Members ----------------------------------------------------------------------------------------------------------

  /* membres */
  /** live session count */
  protected final AtomicInteger sessionCount = new AtomicInteger(0);
  /** total session count */
  protected final AtomicInteger sessionTotal = new AtomicInteger(0);
  /** last total logged */
  protected int lastTotal = -1;
  /** last live logged */
  protected int lastLive = -1;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Public constructor
   */
  public SessionMonitor() {
    instance = this;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  public static void onInit() {
    logB = LogUtil.getLoggerBase();

    if (instance != null) {
      instance.init();
    }
  }

  /**
   * onExit callback
   */
  public static void onExit() {
    if (logB.isInfoEnabled()) {
      logB.info("SessionMonitor.onExit : enter");
    }

    if (pollTh != null) {
      // stop polling thread :
      pollTh.stopAndWait();

      // force GC :
      pollTh = null;
    }

    // force GC :
    instance = null;

    if (logB.isInfoEnabled()) {
      logB.info("SessionMonitor.onExit : exit");
    }
    logB = null;
  }

  /**
   * TODO : Method Description
   */
  public void init() {
    if (pollTh == null) {
      // create a looping thread with a lapse of time :
      pollTh = new PollingThread(LAPSE) {

        @Override
        public final void handle() {
          final int live = sessionCount.get();
          final int total = sessionTotal.get();

          if ((live > lastLive) || (total > lastTotal)) {
            // fast simple barrier :
            lastLive = live;
            lastTotal = total;

            if (logB.isWarnEnabled()) {
              logB.warn("SessionMonitor : Live sessions : " + live + " / Total sessions created : " + total);
            }
          }
        }
      };

      if (logB.isWarnEnabled()) {
        logB.warn("SessionMonitor : starting monitoring thread ...");
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

    if (logB.isInfoEnabled()) {
      logB.info("New Session created with ID : " + session.getId());
    }
  }

  /**
   * TODO : Method Description
   *
   * @param se
   */
  public void sessionDestroyed(final HttpSessionEvent se) {
    sessionCount.decrementAndGet();

    if (logB.isWarnEnabled()) {
      final HttpSession session = se.getSession();

      final String ip = (String) session.getAttribute(SessionMonitor.SESSION_IP);
      final String host = (String) session.getAttribute(SessionMonitor.SESSION_HOST);
      final String no = getSessionNo(session);

      logB.warn("Session [" + no + "] destroyed with ID : " + session.getId() + " from : [" + host + ", " + ip + "]");
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

      if (logB.isWarnEnabled()) {
        logB.warn("Session [" + no + "] created   with ID : " + session.getId() + " from : [" + host + ", " + ip + "]");
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

