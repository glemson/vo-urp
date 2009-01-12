package org.ivoa.web.servlet;

import org.ivoa.service.VO_URP_Facade;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.ivoa.util.LogUtil;
import org.ivoa.service.SessionMonitor;


/**
 * Base class for VO-URP servlets.
 * 
 * @author laurent
 * @co-author gerard
 */
public class BaseServlet extends HttpServlet {

  public final static String FLAG_ACTIVE = "1";

  public final static String ERROR_PAGE = "error.jsp";
  public final static String ERROR_MESSAGE = "errorMessage";

  public static final String INPUT_ENTITY = "entity";
  public static final String OUTPUT_ENTITY = "entity";
  public static final String OUTPUT_METADATA = "metaData";

  public static final String OUTPUT_TITLE = "title";
  
  /** Logger for this class and subclasses */
  protected static final Log log = LogUtil.getLogger();

  /** shared instance */
  private VO_URP_Facade facade;


  public BaseServlet() {
  }


  public void init(final ServletConfig sc) throws ServletException {
    super.init(sc);

    facade = (VO_URP_Facade) getServletContext().getAttribute(VO_URP_Facade.CTX_KEY);
    
    onInit();
  }
  
  protected void onInit() {}

  /** Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  /** Handles the HTTP <code>POST</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // to be implemented :
    // maybe redirect on success/failure :
  }
  
  protected final HttpSession createSession(final HttpServletRequest request) {
    // Process Session (creates a new one on first time) :
    final HttpSession session = request.getSession(true);

    SessionMonitor.attachIP(session, request);

    return session;
  }

  protected final String getSessionNo(final HttpServletRequest request) {
    // do not create a new session :
    final HttpSession session = request.getSession(false);
    if (session != null) {
      return SessionMonitor.getSessionNo(session);
    }
    return "";
  }

  
// Utils :
  public int getIntParameter(final HttpServletRequest request, final String name, final int def) {
    final String s = getStringParameter(request, name);
    if (s != null) {
      try {
        return Integer.parseInt(s);
      } catch (NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }
    return def;
  }

  public Long getLongParameter(final HttpServletRequest request, final String name, final Long def) {
    final String s = getStringParameter(request, name);
    if (s != null) {
      try {
        return Long.valueOf(s);
      } catch (NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }
    return def;
  }

  public Double getDoubleParameter(final HttpServletRequest request, final String name, final Double def) {
    final String s = getStringParameter(request, name);
    if (s != null) {
      try {
        return Double.valueOf(s);
      } catch (NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }
    return def;
  }
  
  public boolean getBoolParameter(final HttpServletRequest request, final String name, final boolean def) {
    final String s = getStringParameter(request, name);
    if (s != null) {
      try {
        return Boolean.parseBoolean(s);
      } catch (NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }
    return def;
  }
  
  public String getStringParameter(final HttpServletRequest request, final String name) {
    return getStringParameter(request, name, null);
  }

  public String getStringParameter(final HttpServletRequest request, final String name, final String def) {
    final String s = request.getParameter(name);
    if (s != null && s.length() > 0) {
      return s;
    }
    return def;
  }

  public void showError(HttpServletRequest request, HttpServletResponse response, final String msg) throws ServletException {
    request.setAttribute(ERROR_MESSAGE, msg);
    doForward(request, response, ERROR_PAGE);
  }

  
  public void doForward(HttpServletRequest request, HttpServletResponse response, final String page) throws ServletException {
      try {
       request.getRequestDispatcher(page).forward(request, response);
    } catch (Exception e) {
        throw new ServletException(e);
    }
  }
  
// Getter :  
  
  public VO_URP_Facade getFacade() {
    return facade;
  }
}
