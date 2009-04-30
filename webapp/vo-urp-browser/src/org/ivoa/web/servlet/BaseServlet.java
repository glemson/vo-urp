package org.ivoa.web.servlet;

import org.apache.commons.logging.Log;

import org.ivoa.service.SessionMonitor;
import org.ivoa.service.VO_URP_Facade;

import org.ivoa.util.LogUtil;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Base class for VO-URP servlets.
 *
 * @author laurent
 * @author gerard
 */
public class BaseServlet extends HttpServlet {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface : every concrete class must have its value corresponding to last
   * modification date of the UML model
   */
  private static final long serialVersionUID = 1L;

  // constants :
  /**
   * TODO : Field Description
   */
  public static final String FLAG_ACTIVE     = "1";
  /**
   * TODO : Field Description
   */
  public static final String ERROR_PAGE = "error.jsp";
  /**
   * TODO : Field Description
   */
  public static final String ERROR_MESSAGE = "errorMessage";
  /**
   * TODO : Field Description
   */
  public static final String INPUT_ENTITY = "entity";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_ENTITY = "entity";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_METADATA = "metaData";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_TITLE = "title";
  /** Logger for this class and subclasses */
  protected static final Log log = LogUtil.getLogger();

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** shared instance */
  private VO_URP_Facade facade;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new BaseServlet object
   */
  public BaseServlet() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param sc 
   *
   * @throws ServletException 
   */
  public void init(final ServletConfig sc) throws ServletException {
    super.init(sc);

    facade = (VO_URP_Facade) getServletContext().getAttribute(VO_URP_Facade.CTX_KEY);

    onInit();
  }

  /**
   * TODO : Method Description
   */
  protected void onInit() {
  }

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   *
   * @throws ServletException
   * @throws IOException
   */
  protected final void doGet(final HttpServletRequest request, final HttpServletResponse response)
                      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   *
   * @throws ServletException
   * @throws IOException
   */
  protected final void doPost(final HttpServletRequest request, final HttpServletResponse response)
                       throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   *
   * @throws ServletException
   * @throws IOException
   */
  protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
                         throws ServletException, IOException {
    // to be implemented :
    // maybe redirect on success/failure :
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   *
   * @return value TODO : Value Description
   */
  protected final HttpSession createSession(final HttpServletRequest request) {
    // Process Session (creates a new one on first time) :
    final HttpSession session = request.getSession(true);

    SessionMonitor.attachIP(session, request);

    return session;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   *
   * @return value TODO : Value Description
   */
  protected final String getSessionNo(final HttpServletRequest request) {
    // do not create a new session :
    final HttpSession session = request.getSession(false);

    if (session != null) {
      return SessionMonitor.getSessionNo(session);
    }

    return "";
  }

  // Utils :
  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public int getIntParameter(final HttpServletRequest request, final String name, final int def) {
    final String s = getStringParameter(request, name);

    if (s != null) {
      try {
        return Integer.parseInt(s);
      } catch (final NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }

    return def;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public Long getLongParameter(final HttpServletRequest request, final String name, final Long def) {
    final String s = getStringParameter(request, name);

    if (s != null) {
      try {
        return Long.valueOf(s);
      } catch (final NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }

    return def;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public Double getDoubleParameter(final HttpServletRequest request, final String name, final Double def) {
    final String s = getStringParameter(request, name);

    if (s != null) {
      try {
        return Double.valueOf(s);
      } catch (final NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }

    return def;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public boolean getBoolParameter(final HttpServletRequest request, final String name, final boolean def) {
    final String s = getStringParameter(request, name);

    if (s != null) {
      try {
        return Boolean.parseBoolean(s);
      } catch (final NumberFormatException nfe) {
        if (log.isDebugEnabled()) {
          log.debug("bad parameter : " + name + " = " + s);
        }
      }
    }

    return def;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   *
   * @return value TODO : Value Description
   */
  public String getStringParameter(final HttpServletRequest request, final String name) {
    return getStringParameter(request, name, null);
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param name 
   * @param def 
   *
   * @return value TODO : Value Description
   */
  public String getStringParameter(final HttpServletRequest request, final String name, final String def) {
    final String s = request.getParameter(name);

    if ((s != null) && (s.length() > 0)) {
      return s;
    }

    return def;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param response 
   * @param msg 
   *
   * @throws ServletException 
   */
  public void showError(final HttpServletRequest request, final HttpServletResponse response, final String msg)
                 throws ServletException {
    request.setAttribute(ERROR_MESSAGE, msg);
    doForward(request, response, ERROR_PAGE);
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param response 
   * @param page 
   *
   * @throws ServletException 
   */
  public void doForward(final HttpServletRequest request, final HttpServletResponse response, final String page)
                 throws ServletException {
    try {
      request.getRequestDispatcher(page).forward(request, response);
    } catch (final Exception e) {
      throw new ServletException(e);
    }
  }

  // Getter :  
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public VO_URP_Facade getFacade() {
    return facade;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
