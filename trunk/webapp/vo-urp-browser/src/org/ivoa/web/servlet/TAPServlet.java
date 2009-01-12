package org.ivoa.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ivoa.votable.AnyTEXT;
import org.ivoa.votable.Info;
import org.ivoa.votable.Resource;
import org.ivoa.votable.VOTABLE;
import org.ivoa.votable.Values;
import org.ivoa.web.model.SQLQuery;
import org.ivoa.web.model.TAPResult;
import org.ivoa.dm.MetaModelFactory;
import org.ivoa.service.VO_URP_Facade;
import org.ivoa.tap.TAP_v0_31;
import org.ivoa.util.SQLUtils;

/**
 * Servlet implementing the TAP interface.<br/>
 *
 * Current version is (to be ) based on TAP v0.31
 * The doGet and doPost methods of this servlet interpret the corresponding HTTP requests.
 * 
 * @author Gerard Lemson
 */
public class TAPServlet extends BaseServlet implements TAP_v0_31
{
  // ~~ output attributes ~~
  public static final String OUTPUT_QUERY = "query";
  public static final String OUTPUT_ERROR = "error";
  public static final String VIEW_HTML = "/tap/table.jsp";
  public static final String VIEW_VOTABLE = "/tap/votable.jsp";
  public static final String VIEW_CSV = "/tap/csv.jsp";
  public static final String VIEW_TAP_ERROR = "/tap/error.jsp";
  
  private String TAP_ROOT_URL = "/tap/index.jsp"; 
  
  /** TODO need some way to get the generated files in the jar file */
  private String TABLESET_FILE_XML = null;
  private String TABLESET_FILE_VOTABLE = null;
  
  public static long DEFAULT_MAXREC = 1000; // unlimited
  public static long MAX_MAXREC = -1; // unlimited
    
  /** Returns a short description of the servlet.
   */
  @Override
  public String getServletInfo() {
    return "TAP servlet";
  }

  /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   */
  @Override
  protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    
    long time;
    long start = System.nanoTime();

    // Process Session (creates a new one on first time) :
    final HttpSession session = createSession(request);

    String path = request.getServletPath();
    if(path != null && path.endsWith("/sync"))
      handleSync(request, response);
    else if(path != null && path.endsWith("/async"))
      handleAsync(request, response);
    else
      doForward(request, response, TAP_ROOT_URL); 
      
    time = ((System.nanoTime() - start) / 1000000l);
    if (log.isInfoEnabled()) {
      log.info("QueryServlet ["+getSessionNo(request)+"] : servlet process : "+ time + " ms.");
    }
  }

  private void handleSync(HttpServletRequest request, HttpServletResponse response ) throws ServletException
  {
    final String req =getStringParameter(request, REQUEST_PARAM);

    if(REQUEST_VALUE_ADQLQuery.equalsIgnoreCase(req))
      handleSyncADQLQuery(request, response);
    else if (REQUEST_VALUE_ParamQuery.equalsIgnoreCase(req))
      handleSyncParamQuery(request, response);
    else if (REQUEST_VALUE_GetAvailability.equalsIgnoreCase(req))
      handleSyncGetAvailability(request, response);
    else if (REQUEST_VALUE_GetCapabilities.equalsIgnoreCase(req))
      handleSyncGetCapabilities(request, response);
    else if (REQUEST_VALUE_GetTableMetadata.equalsIgnoreCase(req))
      handleSyncGetTableMetadata(request, response);
    else
    {
      request.setAttribute(OUTPUT_ERROR,"Invalid REQUEST parameter value");
      doForward(request, response, VIEW_TAP_ERROR);
    }
  }
  private void handleAsync(HttpServletRequest request, HttpServletResponse response )  throws ServletException
  {
    final String req = getStringParameter(request, REQUEST_PARAM);
    if(REQUEST_VALUE_ADQLQuery.equalsIgnoreCase(req))
      handleSyncADQLQuery(request, response);
    else if (REQUEST_VALUE_ParamQuery.equalsIgnoreCase(req))
      handleSyncParamQuery(request, response);
    else
      doForward(request, response, TAP_ROOT_URL);
  }

  private void handleSyncGetCapabilities(HttpServletRequest request, HttpServletResponse response)  throws ServletException
  {

  }
  
  private void handleSyncGetAvailability(HttpServletRequest request, HttpServletResponse response)  throws ServletException
  {

  }
  private void handleSyncGetTableMetadata(HttpServletRequest request, HttpServletResponse response)  throws ServletException
  {

  }
  private void handleSyncADQLQuery(HttpServletRequest request, HttpServletResponse response)  throws ServletException
  {
    String runid = getRunId(request);
    String format = getFormat(request);
    handleUpload(request);
    try
    {
      String sql = getStringParameter(request, QUERY_PARAM);
      SQLQuery q = new SQLQuery();
      q.setSql(sql);
      q.setMaxRows(getLongParameter(request, MAXREC_PARAM, DEFAULT_MAXREC).intValue());
      q.reset();
      if (q.getSql() != null) {
        if (q.getSql().contains(";")) {
          throw new Exception("SQL invalid : only a single SELECT statement is allowed (the separator ; is forbidden).");
        }
        if (!q.getSql().startsWith("select") && !q.getSql().startsWith("SELECT")) {
          throw new Exception("SQL invalid : only SELECT statements are allowed !");
        }
        long start = System.nanoTime();
        // maxRows = 1000 / timeout = 30s :
        List rows = SQLUtils.executeQuery(q.getSql(), 1000, 30); // needs an ADQL version

        q.setDuration((System.nanoTime() - start) / 1000000l);

        if (rows != null && rows.size() > 0) {
        // retrieve column headers :
          final Map record = (Map)rows.get(0);
          for (Object name : record.keySet()) {
            q.getHeaders().add(name);
          }
          // copy results :
          q.setResults(rows);
        }
        if(FORMAT_VALUE_html.equalsIgnoreCase(format))
        {
          request.setAttribute(OUTPUT_QUERY, q);
          doForward(request, response, VIEW_HTML);
        } else if(FORMAT_VALUE_votable.equalsIgnoreCase(format))
        {
          request.setAttribute(OUTPUT_QUERY, q);
          doForward(request, response, VIEW_VOTABLE);
        }
        

      } 
    } catch (RuntimeException re) {
        log.error("runtime failure : ", re);
        request.setAttribute(OUTPUT_ERROR, re.getMessage());
        doForward(request, response, VIEW_TAP_ERROR);
    } catch(Exception e) {
      request.setAttribute(OUTPUT_ERROR, e.getMessage());
      doForward(request, response, VIEW_TAP_ERROR);
    }  
  }
  private void handleSyncParamQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException
  {
    // TODO not supported yet
    String from = getStringParameter(request, FROM_PARAM);
    String where = getStringParameter(request, WHERE_PARAM);
    String select = getStringParameter(request, SELECT_PARAM);
    String format = getStringParameter(request, FORMAT_PARAM);
    
  }

  @Override
  public void init() throws ServletException {
    // TODO Auto-generated method stub
    super.init();
  }

  public void init(final ServletConfig sc) throws ServletException 
  {
    super.init(sc);

    TABLESET_FILE_VOTABLE = sc.getInitParameter("TABLESET_FILE_VOTABLE");
    TABLESET_FILE_XML = sc.getInitParameter("TABLESET_FILE_XML");
  }
  /**
   * Errors MUST be provided as VOTable documents.<br/>
   * 
   * @param request
   * @param response
   * @param message
   * @param status
   */
  private void handleError(HttpServletRequest request, HttpServletResponse response, String message, String status)
  {
    VOTABLE votable = new VOTABLE();
    Resource resource = new Resource();
    votable.getCOOSYSAndGROUPSAndPARAMS().add(resource);
    resource.setType("results");
    Info info = new Info();
    resource.getINFOSAndCOOSYSAndGROUPS().add(info);
    info.setName("QUERY_STATUS");
    info.setValue(status);
    

  }
  private String getFormat(HttpServletRequest request)
  {
    String format = getStringParameter(request,FORMAT_PARAM, FORMAT_VALUE_votable);
    // TODO check whether format is supported
    return format;
  }
  /**
   * Retrieves a possible input RunID from the request and returns it.<br/>
   * If none is provided a new RunID is generated, added to the request and returned.
   * @param request
   * @return
   */
  private String getRunId(HttpServletRequest request)
  {
    String runId = request.getParameter(RUNID_PARAM);
    if(runId == null)
      runId = newRunId(request);
    request.setAttribute(RUNID_PARAM, runId);
    return runId;
  }
  /**
   * Generates a random runId for the current request.<br/>
   * 
   * @return
   */
  private String newRunId(HttpServletRequest request)
  {
    return new String("RUN-"+new Random().nextLong());
  }
  /**
   * If an UPLOAD parameter is specified, do something with it ...<br/>
   * Currently this is not supported. 
   * 
   * @param request
   * @throws ServletException
   */
  private void handleUpload(HttpServletRequest request) throws ServletException
  {
    String upload = getStringParameter(request, UPLOAD_PARAM);
    if(upload != null && upload.trim().length() > 0)
      throw new ServletException("UPLOAD is not supported for this TAP service.");
    
    // TODO in future, once UPLOAD *is* supported, do something here. 
    
  }
  
}
