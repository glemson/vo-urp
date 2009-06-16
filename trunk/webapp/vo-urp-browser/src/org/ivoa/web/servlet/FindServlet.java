package org.ivoa.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivoa.web.model.EntityConfig;


/**
 * The servlet class to list Experiment from SNAP database
 */
public final class FindServlet extends BaseServlet {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface : 1
   */
  private static final long serialVersionUID = 1L;

  // constants :
  /**
   * TODO : Field Description
   */
  public static final String INPUT_ID    = "id";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_ITEM = "item";
  /**
   * TODO : Field Description
   */
  public static final String INPUT_VIEW = "view";

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns a short description of the servlet.
   *
   * @return value TODO : Value Description
   */
  @Override
  public String getServletInfo() {
    return "Find servlet";
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
  @Override
  protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
                         throws ServletException, IOException {
    long         time;
    long         start                   = System.nanoTime();

    final String entity                  = getStringParameter(request, INPUT_ENTITY);

    if (entity == null) {
      showError(request, response, "Parameter [" + INPUT_ENTITY + "] is missing.");

      return;
    }

    final EntityConfig ec = getFacade().getEntityConfigFactory().getConfig(entity);

    if (ec == null) {
      showError(request, response, "Entity [" + INPUT_ENTITY + "] is not defined.");

      return;
    }

    final Long id = getLongParameter(request, INPUT_ID, null);

    if (id == null) {
      showError(request, response, "Parameter [" + INPUT_ID + "] is missing.");

      return;
    }

    if (ec.getPageItem() == null) {
      showError(request, response, "Entity can not be displayed (page missing).");

      return;
    }

    // note : this session is unuseful but it should be when user will have login / password.
    final String view = getStringParameter(request, INPUT_VIEW);

    // get view :
    String viewPath = null;

    if (view != null) {
      viewPath = ec.getPages(view);
    }

    if (viewPath == null) {
      viewPath = ec.getPageItem();
    }

    final Object item = getFacade().getItem(id, ec.getClasse());

    // Output parameters :
    request.setAttribute(OUTPUT_ENTITY, ec);
    request.setAttribute(OUTPUT_TITLE, "Detail of " + ec.getName() + " : " + id);
    // specific :
    request.setAttribute(OUTPUT_ITEM, item);

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info(
        "FindServlet [" + getSessionNo(request) + "] : entity[" + ec.getName() + "] id[" + id +
        "] : servlet process : " + time + " ms.");
    }

    start = System.nanoTime();

    doForward(request, response, viewPath);

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info(
        "FindServlet [" + getSessionNo(request) + "] : entity[" + ec.getName() + "] id[" + id +
        "] : jsp     process : " + time + " ms. View[" + ((view != null) ? view : "default") + "]");
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
