package org.ivoa.runner;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;

/**
 * Session Listener to manage user temporary files
 * 
 * @author laurent
 */
public final class SessionListener implements HttpSessionListener {
  /** logger */
  protected static Log log = LogUtil.getLogger();

  /**
   * Public constructor 
   */
  public SessionListener() {
  }

  public void sessionCreated(final HttpSessionEvent se) {
    
    final HttpSession session = se.getSession();

    final String sessionId = session.getId();
    if (log.isInfoEnabled()) {
      log.info("creating session folder with ID : "+ sessionId);
    }
    
    // create the session folder :
    // TODO rethink this. Currently turned off as we want folders to be created for users and when needed.
//    FileManager.getSessionFolder(sessionId);
  }

  public void sessionDestroyed(final HttpSessionEvent se) {

    final String sessionId = se.getSession().getId();
    if (log.isInfoEnabled()) {
      log.info("cleaning session folder with ID : "+ sessionId);
    }

    // purge the session folder :
    log.error("SKIP = cleaning session folder with ID : "+ sessionId);
    /*
    FileManager.purgeSessionFolder(sessionId);
     */
  }
  
}
