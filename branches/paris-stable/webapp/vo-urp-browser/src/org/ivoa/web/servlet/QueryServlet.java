package org.ivoa.web.servlet;

import org.ivoa.dm.MetaModelFactory;

import org.ivoa.util.SQLUtils;

import org.ivoa.web.model.SQLQuery;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Simple SQL Query interface
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class QueryServlet extends BaseServlet {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  /**
   * TODO : Field Description
   */
  public static final String INPUT_SQL         = "sql";
  /**
   * TODO : Field Description
   */
  public static final String INPUT_REFRESH = "refresh";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_QUERY = "query";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_TAP = "tap";
  /**
   * TODO : Field Description
   */
  public static final String SESSION_SQL_QUERY = "sqlQuery";

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns a short description of the servlet.
   *
   * @return value TODO : Value Description
   */
  @Override
  public String getServletInfo() {
    return "Query servlet";
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
    long time;
    long start                                 = System.nanoTime();

    // Process Session (creates a new one on first time) :
    final HttpSession session = createSession(request);

    SQLQuery          q = (SQLQuery) session.getAttribute(SESSION_SQL_QUERY);

    if (q == null) {
      q = new SQLQuery();
      session.setAttribute(SESSION_SQL_QUERY, q);
    }

    request.setAttribute(OUTPUT_QUERY, q);

    request.setAttribute(OUTPUT_TAP, MetaModelFactory.getInstance().getTap().values().toArray());

    // Input parameters :
    final String sql = getStringParameter(request, INPUT_SQL);

    if (sql != null) {
      q.setSql(sql.trim());
    }

    // Action :
    handleQuery(q);

    // Output parameters :
    request.setAttribute(OUTPUT_TITLE, "SQL Query");

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("QueryServlet [" + getSessionNo(request) + "] : servlet process : " + time + " ms.");
    }

    start = System.nanoTime();

    doForward(request, response, "/query/query.jsp");

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("QueryServlet [" + getSessionNo(request) + "] : jsp process : " + time + " ms.");
    }
  }

  /**
   * TODO : Method Description
   *
   * @param q 
   */
  @SuppressWarnings("unchecked")
  private void handleQuery(final SQLQuery q) {
    q.reset();

    if (q.getSql() != null) {
      if (q.getSql().contains(";")) {
        q.setError("SQL invalid : only a single SELECT statement is allowed (the separator ; is forbidden).");

        return;
      }

      if (! q.getSql().startsWith("select") && ! q.getSql().startsWith("SELECT")) {
        q.setError("SQL invalid : only SELECT statements are allowed !");

        return;
      }

      try {
        long start = System.nanoTime();

        // maxRows = 1000 / timeout = 30s :
        List rows = SQLUtils.executeQuery(q.getSql(), 1000, 30);

        q.setDuration((System.nanoTime() - start) / 1000000L);

        if ((rows != null) && (rows.size() > 0)) {
          // retrieve column headers :
          final Map record = (Map) rows.get(0);

          for (final Object name : record.keySet()) {
            q.getHeaders().add(name);
          }

          // copy results :
          q.setResults(rows);
        }
      } catch (final RuntimeException re) {
        log.error("runtime failure : ", re);
        q.setError("SQL Error : " + re.getMessage());
      }
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
