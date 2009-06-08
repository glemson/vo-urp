package org.ivoa.web.servlet.secure;

import org.apache.commons.fileupload.FileItem;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.ivoa.conf.RuntimeConfiguration;

import org.ivoa.dm.DataModelManager;

import org.ivoa.dm.model.MetadataObject;

import org.ivoa.util.FileUtils;

import org.ivoa.web.servlet.BaseServlet;

import org.ivoa.xml.validator.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.security.Principal;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * The servlet class to list Experiment from SNAP database
 */
public final class ManagerServlet extends BaseServlet {
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
  public static final String INPUT_DOC     = "doc";
  /**
   * TODO : Field Description
   */
  public static final String INPUT_TYPE = "type";
  /**
   * TODO : Field Description
   */
  public static final String OUTPUT_INSERT = "insert";
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
   * Action parameter value indicating that an uploaded XML doc should be validated against the XML  schemas for
   * the current model and if valid its references should be checked, whether they exist. If still OK can be inserted
   * into the database after user has been assigned.
   */
  public static final String INPUT_ACTION_insert = "insert";
  /**
   * TODO : Field Description
   */
  public static final String PATH_SECURE = "/secure/";

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
    return "Manager servlet";
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
    long start                             = System.nanoTime();

    Map<String, Object> params = getParameters(request);
    final String        action = (String) params.get(INPUT_ACTION);
    String              error  = null;
    String              status = "OK";
    Object              result = null;

    try {
      Principal user = request.getUserPrincipal();

      if (user == null) {
        throw new Exception("No user specified.");
      }

      if (INPUT_ACTION_insert.equals(action)) {
        result = handleInsert(params, user.getName());
        request.setAttribute(OUTPUT_INSERT, result);
      }
    } catch (final Exception e) {
      error = e.getMessage();
      status = "ERROR";
    }

    final HttpSession session = createSession(request);

    // note : this session is unuseful but it should be when user will have login / password.

    // Output parameters : 
    request.setAttribute(OUTPUT_TITLE, "Upload - " + status);
    // specific :
    request.setAttribute(OUTPUT_STATUS, status);

    if (error != null) {
      request.setAttribute(OUTPUT_ERROR, error);
    }

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("UploadServlet [" + getSessionNo(request) + "] : upload succeeded : servlet process : " + time + " ms.");
    }

    start = System.nanoTime();

    String viewPath = PATH_SECURE + "LoadResource.jsp";

    doForward(request, response, viewPath);

    time = ((System.nanoTime() - start) / 1000000L);

    if (log.isInfoEnabled()) {
      log.info("ManagerServlet [" + getSessionNo(request) + "] : upload forwarded:  jsp     process : " + time);
    }
  }

  /**
   * Insert an uploaded document into the databaase and return the corresponding object.<br>
   * TODO implement the insert part
   *
   * @param parameters
   * @param user
   *
   * @return MetadataObject
   *
   * @throws Exception
   */
  private MetadataObject handleInsert(final Map<String, Object> parameters, final String user)
                               throws Exception {
    InputStream in = null;

    try {
      final FileItem infile = (FileItem) parameters.get(INPUT_DOC);

      in = infile.getInputStream();

      MetadataObject result = dataModelManager.load(in, user);

      return result;
    } catch (final Throwable t) {
      throw new Exception(t);
    } finally {
      FileUtils.closeStream(in);
    }
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
  private Map<String, Object> getParameters(final HttpServletRequest request) {
    Map<String, Object> parameters = new Hashtable<String, Object>();

    try {
      // Check that we have a file upload request
      boolean isMultipart = ServletFileUpload.isMultipartContent(request);

      if (! isMultipart) {
        return request.getParameterMap();
      }

      // Create a factory for disk-based file items
      DiskFileItemFactory factory = new DiskFileItemFactory();

      // Set factory constraints
      int yourMaxMemorySize = 1000000;

      //File yourTempDirectory = new File("c:/temp/upload/");
      int yourMaxRequestSize = 1000000;

      factory.setSizeThreshold(yourMaxMemorySize);

      //factory.setRepository(yourTempDirectory);

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Set overall request size constraint
      upload.setSizeMax(yourMaxRequestSize);

      // Parse the request
      List fileItems = upload.parseRequest(request);

      // Get the image stream
      for (int i = 0; i < fileItems.size(); i++) {
        FileItem fi    = (FileItem) fileItems.get(i);
        String   name  = fi.getFieldName();
        Object   value = null;

        if (fi.isFormField()) {
          value = fi.getString();
        } else {
          value = fi;
        }

        parameters.put(name, value);
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }

    return parameters;
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
    // TODO Auto-generated method stub
    super.init(sc);

    try {
      dataModelManager = new DataModelManager(RuntimeConfiguration.getInstance().getJPAPU());
    } catch (final Exception e) {
      log.error(
        "Unable to initiate DataModelManager for UploadServlet using JPA persistence unit " +
        RuntimeConfiguration.getInstance().getJPAPU());
      dataModelManager = null; // TODO should we throw an exception or simply make uploads not possible?
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
