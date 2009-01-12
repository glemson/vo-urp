/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ivoa.service;

import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;
import org.ivoa.util.PollingThread;

/**
 * Session monitor :
 * 
 * @author laurent
 */
public final class SessionMonitor implements HttpSessionListener {
    
  public static final String SESSION_NO = "session-no";
  public static final String SESSION_IP = "session-ip";
  public static final String SESSION_HOST = "session-host";

  private final static long LAPSE =  10 * 60 * 1000l; // 10 minutes
  
  /** Logger for this class and subclasses */
  protected static Log log = LogUtil.getLoggerDev();

  /** stats thread */
  private static PollingThread pollTh = null;

  /* membres */
  /** live session count */
  private final AtomicInteger sessionCount = new AtomicInteger(0);

  /** total session count */
  private final AtomicInteger sessionTotal = new AtomicInteger(0);
  
  /** last total logged */
  private int lastTotal = -1;
  
  /** last live logged */
  private int lastLive = -1;

  /**
   * Public constructor 
   */
  public SessionMonitor() {
    init();
  }
  
  protected static void onExit() {
    log.info("SessionMonitor.onExit : enter");
    if (pollTh != null) {
      // stop polling thread :
      pollTh.stopAndWait();
      // force GC :
      pollTh = null;
    }
    log.info("SessionMonitor.onExit : exit");
  }

  public void init() {
    if (pollTh == null) {
      // create a looping thread with a lapse of time :
      pollTh = new PollingThread(LAPSE) {
        public final void handle() {
          final int live  = sessionCount.get();
          final int total = sessionTotal.get();
          if (live > lastLive || total > lastTotal) {
            // fast simple barrier :
            lastLive  = live;
            lastTotal = total;
            if (log.isWarnEnabled()) {
              log.warn("SessionMonitor : Live sessions : " + live + " / Total sessions created : " + total);
            }
          }
        }
      };
      if (log.isInfoEnabled()) {
        log.info("SessionMonitor : starting monitoring thread ...");
      }
      // starts thread :
      pollTh.start();
    }
  }
  
  public void sessionCreated(final HttpSessionEvent se) {
    sessionCount.incrementAndGet();
    final String no = "S:" + String.valueOf(sessionTotal.incrementAndGet());
    
    final HttpSession session = se.getSession();
    session.setAttribute(SESSION_NO, no);

    if (log.isInfoEnabled()) {
      log.info("New Session created with ID : "+ session.getId());
    }
  }

  public void sessionDestroyed(final HttpSessionEvent se) {
    sessionCount.decrementAndGet();

    if (log.isWarnEnabled()) {
      final HttpSession session = se.getSession();

      final String ip = (String)session.getAttribute(SessionMonitor.SESSION_IP);
      final String host = (String)session.getAttribute(SessionMonitor.SESSION_HOST);
      final String no = getSessionNo(session);
      
      log.warn("Session ["+no+"] destroyed with ID : "+ session.getId() + " from : ["+host+", "+ip+"]");
    }
  }
  
  /* callback */
  
  public static void attachIP(final HttpSession session, final HttpServletRequest request) {
    if (session != null && session.getAttribute(SessionMonitor.SESSION_IP) == null) {
      final String ip = request.getRemoteAddr();
      final String host = request.getRemoteHost();
      session.setAttribute(SessionMonitor.SESSION_IP, ip);
      session.setAttribute(SessionMonitor.SESSION_HOST, host);

      final String no = getSessionNo(session);

      if (log.isWarnEnabled()) {
        log.warn("Session ["+no+"] created   with ID : "+ session.getId() + " from : ["+host+", "+ip+"]");
      }
    }
  }

  public static String getSessionNo(final HttpSession session) {
    return (String)session.getAttribute(SessionMonitor.SESSION_NO);
  }
  
}
