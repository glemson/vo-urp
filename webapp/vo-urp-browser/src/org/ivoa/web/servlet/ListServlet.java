package org.ivoa.web.servlet;

import org.ivoa.dm.MetaModelFactory;

import org.ivoa.web.model.CursorQuery;
import org.ivoa.web.model.EntityConfig;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * The servlet class to list Experiment from SNAP database
 */
public final class ListServlet extends BaseServlet {
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
  public static final String INPUT_QUERY_CLAUSE = "queryClause";
  /**
   * TODO : Field Description
   */
  public static final String INPUT_START = "start";
  /**
   * TODO : Field Description
   */
  public static final String INPUT_REFRESH = "refresh";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_CURSOR = "cursor";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_LIST = "list";

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns a short description of the servlet.
   *
   * @return value TODO : Value Description
   */
  @Override
  public String getServletInfo() {
    return "List servlet";
  }

  /*
   * "SELECT item FROM SnapExperiment item inner join item.parameterCollection p1 inner join item.parameterCollection p2
   * WHERE (p1.parameter.name = 'orbit_type' and p1.value = '3') and
   *       (p2.parameter.name = 'orbit_spin' and p2.value = '1')"
   *
   * SELECT distinct item FROM SnapExperiment item , IN(item.snapshotCollection) s1, IN(s1.objectCollectionCollection) o1
   * where o1.numberOfObjects = 20000
   */
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
    long         start                          = System.nanoTime();

    final String entity                         = getStringParameter(request, INPUT_ENTITY);

    if (entity == null) {
      showError(request, response, "Parameter [" + INPUT_ENTITY + "] is missing.");

      return;
    }

    final EntityConfig ec = getFacade().getEntityConfigFactory().getConfig(entity);

    if (ec == null) {
      showError(request, response, "Entity [" + INPUT_ENTITY + "] is not defined.");

      return;
    }

    // Process Session (creates a new one on first time) :
    final HttpSession session = createSession(request);

    CursorQuery       cq      = (CursorQuery) session.getAttribute(ec.getSessionCursorKey());

    if (cq == null) {
      cq = getFacade().createCursor(ec);
      session.setAttribute(ec.getSessionCursorKey(), cq);
    }

    request.setAttribute(OUTPUT_CURSOR, cq);

    // Input parameters :
    if (ec.isDoQuery()) {
      final String queryClause = getStringParameter(request, INPUT_QUERY_CLAUSE);

      if (queryClause != null) {
        cq.setQueryClause(queryClause);
      } else {
        cq.setQueryClause("");
      }
    }

    if (ec.isDoPaging()) {
      final int startPos = getIntParameter(request, INPUT_START, 0);

      cq.setStartPos(startPos);
    }

    final String refresh = request.getParameter(INPUT_REFRESH);

    if ((refresh != null) && FLAG_ACTIVE.equals(refresh)) {
      cq.setRefresh(true);
    }

    // always refresh : database content can be changed by web app :
    cq.setRefresh(true);
    cq.setRefreshCount(true);

    try {
      // Action :
      if (cq.isRefresh()) {
        getFacade().refreshCursor(ec, cq);
      }

      // Output parameters :
      request.setAttribute(OUTPUT_ENTITY, ec);
      request.setAttribute(OUTPUT_TITLE, "List of " + ec.getName() + " records");

      // add class metadata object :
      request.setAttribute(
        OUTPUT_METADATA,
        MetaModelFactory.getInstance().getObjectClassType(ec.getClasse().getSimpleName()));

      // specific :
      request.setAttribute(OUTPUT_LIST, cq.getResults());

      time = ((System.nanoTime() - start) / 1000000L);

      if (log.isInfoEnabled()) {
        log.info(
          "ListServlet [" + getSessionNo(request) + "] : entity[" + ec.getName() + "] pos[" + cq.getStartPos() +
          "] : servlet process : " + time + " ms.");
      }

      start = System.nanoTime();

      doForward(request, response, ec.getPageList());

      time = ((System.nanoTime() - start) / 1000000L);

      if (log.isInfoEnabled()) {
        log.info(
          "ListServlet [" + getSessionNo(request) + "] : entity[" + ec.getName() + "] pos[" + cq.getStartPos() +
          "] : servlet process : " + time + " ms.");
      }
    } catch (final RuntimeException re) {
      log.error("ListServlet : failure : ", re);
      showError(request, response, re.getMessage());
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
