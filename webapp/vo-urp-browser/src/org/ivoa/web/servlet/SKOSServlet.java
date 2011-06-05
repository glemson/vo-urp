package org.ivoa.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.DataModelManager;


/**
 * The servlet class performs various functions having to do with SKOS concepts:
 * - List all attributes with <<skosconcept>> stereotype and link to their vocabularies. 
 * - Find appropriate SKOS concept for an input string
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class SKOSServlet extends BaseServlet {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  /**
   * The broadest term when trying to find a concept matching a given search term.
   * Vocabulary containing the term will be searched.
   */
  public static final String INPUT_BROADEST_CONCEPT         = "broadest";
  /**
   * The term to be matched to a SKOS concept.
   */
  public static final String INPUT_TERM = "term";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_LIST = "list";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_QUERY = "query";
  public static final String OUTPUT_QUERY_RESULT = "queryresult";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_STATUS = "status";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_ERROR = "error";
  /**
   * Parameter name of the parameter indicating which of the actions available through this servlet should be
   * performed.
   */
  public static final String INPUT_ACTION = "action";
  /**
   * Action parameter value indicating that a simple listing of the SKOS concepts in the model should be provided.
   */
  public static final String INPUT_ACTION_list = "lis";
  /** Action parameter value indicating that a query for a given term should be performed. */
  public static final String INPUT_ACTION_query = "query";
  /**
   * Path to the folder containing the JSP page showing the results of this servlet.
   */
  public static final String PATH_MANAGE = "manage/";

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private DataModelManager dataModelManager;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns a short description of the servlet.
   *
   * @return value TODO : Value Description
   */
  @Override
  public String getServletInfo() {
    return "SKOS servlet";
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

    Map<String, Object> params = getParameters(request);
    final String        action = (String) params.get(INPUT_ACTION);
    String              error  = null;
    String              status = "OK";
    Object              result = null;

    try {
      if (INPUT_ACTION_list.equals(action)) {
        request.setAttribute(OUTPUT_LIST, OUTPUT_LIST);
      } else if (INPUT_ACTION_query.equals(action)) {
        result = handleQuery(params);
        request.setAttribute(OUTPUT_QUERY_RESULT, result);
      }
    } catch (final Exception e) {
      error = e.getMessage();
      status = "ERROR";
    }

    // note : this session is unuseful but it should be when user will have login / password.

    // Output parameters : 
    request.setAttribute(OUTPUT_TITLE, "SKOS Concepts");
    // specific :
    request.setAttribute(OUTPUT_STATUS, status);

    if (error != null) {
      request.setAttribute(OUTPUT_ERROR, error);
    }

    time = ((System.nanoTime() - start) / 1000000L);

    start = System.nanoTime();

    String viewPath = PATH_MANAGE + "SKOSConcepts.jsp";

    doForward(request, response, viewPath);

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("SKOSServlet [" + getSessionNo(request) + "] : request forwarded:  jsp     process : " + time);
    }
  }

  /**
   * Create a template XML document for the datatype specified in parameter INPUT_TYPE.<br>
   *
   * @param parameters
   *
   * @return null ??
   */
  private List<String> handleQuery(final Map<String, Object> parameters) {
    final String term = (String) parameters.get(INPUT_TERM);
    final String broadest = (String) parameters.get(INPUT_BROADEST_CONCEPT);

    if (log.isInfoEnabled()) {
      log.info("handleQuert : TODO implement - term/broades : " + term+"/"+broadest);
    }
    
    return null;
  }

  /**
   * Extract a Map off key value pairs from the request which is assumed to be POSTed as multipart/form-data.<br>
   * NOTE the Map is NOT structured as the result of a ServletRequest::getParameterMap() i.e. with the value a
   * String[], but has an single String as value.
   *
   * @param request
   *
   * @return parameter map
   */
  @SuppressWarnings({ "unchecked" })
  private Map<String, Object> getParameters(final HttpServletRequest request) {
    return request.getParameterMap();
  }

  /**
   * TODO : Method Description
   *
   * @param sc 
   *
   * @throws ServletException 
   */
  @Override
  public void init(final ServletConfig sc) throws ServletException {
    super.init(sc);

    try {
      dataModelManager = new DataModelManager(RuntimeConfiguration.get().getJPAPU());
    } catch (final Exception e) {
      log.error(
        "Unable to initiate DataModelManager for SKOSServlet using JPA persistence unit " +
        RuntimeConfiguration.get().getJPAPU());
      // TO ADD to get root exception :
      log.error("exception = ", e);
      dataModelManager = null; 
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
