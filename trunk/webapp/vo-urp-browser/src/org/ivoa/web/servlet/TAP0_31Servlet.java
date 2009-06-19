package org.ivoa.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.tap.TAP_v0_31;
import org.ivoa.util.SQLUtils;
import org.ivoa.web.model.SQLQuery;
import org.ivoa.web.model.TAPResult;


/**
 * Servlet implementing the TAP interface.<br>
 * Current version is (to be ) based on TAP v0.31 The doGet and doPost methods of this servlet interpret the
 * corresponding HTTP requests.
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class TAP0_31Servlet extends BaseServlet implements TAP_v0_31 {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  /** TODO need some way to get the generated files in the jar file */
  public static final String TAP_METADATA_XML_FILE = RuntimeConfiguration.get().getTAPMetadataXMLFile();
  /**
   * TODO : Field Description
   */
  public static final String TAP_METADATA_VOTABLE_FILE = RuntimeConfiguration.get().getTAPMetadataVOTableFile();
  /**
   * TODO : Field Description
   */
  public static final String PATH_QUERY = "query/";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_ADQL = "adql";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_RESPONSE = "response";

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns a short description of the servlet.
   *
   * @return value TODO : Value Description
   */
  @Override
  public String getServletInfo() {
    return "TAP servlet";
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
    long start = System.nanoTime();

    // first check for sync or async
    String[] pathInfo = request.getPathInfo().split("/");
    boolean  isSync   = false;

    if ((pathInfo != null) && (pathInfo.length == 2)) {
      isSync = SYNC.equals(pathInfo[1]);
    }

    // extract the request
    final String req    = getStringParameter(request, REQUEST_PARAM);
    TAPResult    result = null;

    if (REQUEST_VALUE_ADQLQuery.equalsIgnoreCase(req)) {
      String   sql = getStringParameter(request, QUERY_PARAM);
      SQLQuery q   = new SQLQuery();

      q.setSql(sql);
      q.setMaxRows(getLongParameter(request, MAXREC_PARAM, Long.valueOf(-1L)).intValue());
      result = handleADQLQuery(q, getStringParameter(request, LANG_PARAM), isSync);
    } else if (REQUEST_VALUE_ParamQuery.equalsIgnoreCase(req)) {
      result = handleParamQuery(request, isSync);
    }

    request.setAttribute(OUTPUT_RESPONSE, result);

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("QueryServlet [" + getSessionNo(request) + "] : servlet process : " + time + " ms.");
    }

    start = System.nanoTime();

    String format   = getStringParameter(request, FORMAT_PARAM);
    String viewPath = null;

    if (FORMAT_VALUE_votable.equals(format)) {
      viewPath = PATH_QUERY + "votable.jsp";
    } // else if ...
    else //if(INPUT_FORMAT_html.equals(format))
     {
      viewPath = PATH_QUERY + "table.jsp";
    }

    doForward(request, response, viewPath);

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("QueryServlet [" + getSessionNo(request) + "] : jsp process : " + time + " ms.");
    }
  }

  /**
   * TODO : Method Description
   *
   * @param q 
   * @param lang 
   * @param isSync 
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  private TAPResult handleADQLQuery(final SQLQuery q, final String lang, final boolean isSync) {
    TAPResult r = new TAPResult(q);

    q.reset();

    if (q.getSql() != null) {
      if (q.getSql().contains(";")) {
        q.setError("SQL invalid : only a single SELECT statement is allowed (the separator ; is forbidden).");
        r.setError(q.getError());

        return r;
      }

      if (! q.getSql().startsWith("select") && ! q.getSql().startsWith("SELECT")) {
        q.setError("SQL invalid : only SELECT statements are allowed !");
        r.setError(q.getError());

        return r;
      }

      try {
        long start = System.nanoTime();

        // maxRows = 1000 / timeout = 30s :
        List rows = SQLUtils.executeQuery(q.getSql(), 1000, 30); // needs an ADQL version

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
        r.setError("SQL Error : " + re.getMessage());
      }
    }

    return r;
  }

  /**
   * TODO : Method Description
   *
   * @param request 
   * @param isSync 
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  private TAPResult handleParamQuery(final HttpServletRequest request, final boolean isSync) {
    String from   = getStringParameter(request, TAP_v0_31.FROM_PARAM);
    String where  = getStringParameter(request, TAP_v0_31.WHERE_PARAM);
    String select = getStringParameter(request, TAP_v0_31.SELECT_PARAM);
    String format = getStringParameter(request, TAP_v0_31.FORMAT_PARAM);

    // 1.
    // check whether this is a getTableMetadata query:
    // FROM=TAP_SCHEMA.tables
    // FORMAT=xml || FORMAT=votable
    // no select, no where
    TAPResult r = new TAPResult(request.getParameterMap());

    if ("TAP_SCHEMA.tables".equalsIgnoreCase(from) && (select == null) && (where == null) &&
          (TAP_v0_31.FORMAT_VALUE_xml.equalsIgnoreCase(format) ||
            TAP_v0_31.FORMAT_VALUE_votable.equalsIgnoreCase(format))) {
      if (TAP_v0_31.FORMAT_VALUE_xml.equalsIgnoreCase(format)) {
        r.setResultFile(TAP_METADATA_XML_FILE);
      } else //if(INPUT_FORMAT_votable.equalsIgnoreCase(format))
       {
        r.setResultFile(TAP_METADATA_VOTABLE_FILE);
      }
    } else // generic ParamQuery not supported
     {
      r.setError("This TAP service does not support generic ParamQuery-s.");
    }

    return r;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
