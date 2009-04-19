package org.ivoa.web.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.ivoa.conf.RuntimeConfiguration;

import org.ivoa.dm.DataModelManager;
import org.ivoa.dm.model.MetadataObject;

import org.ivoa.util.FileUtils;

import org.ivoa.xml.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
public final class ValidationServlet extends BaseServlet {
    public static final String INPUT_DOC = "doc";
    public static final String INPUT_TYPE = "type";
    public static final String OUTPUT_VALIDATION = "validation";
    public static final String OUTPUT_TEMPLATE = "template";
    public static final String OUTPUT_STATUS = "status";
    public static final String OUTPUT_ERROR = "error";

    /** Parameter name of the parameter indicating which of the actions available through this servlet should be performed.*/
    public static final String INPUT_ACTION = "action";

    /** Action parameter value indicating that an uploaded XML doc should be validated against the XML schemas for the current model */
    public static final String INPUT_ACTION_validate = "validate";


    /** Action parameter value indicating that a template/example XML doc should be produced for a specified entity */
    public static final String INPUT_ACTION_template = "template";
    public static final String PATH_MANAGE = "manage/";
    private DataModelManager dataModelManager;

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Upload servlet";
    }

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void processRequest(final HttpServletRequest request,
        final HttpServletResponse response)
        throws ServletException, IOException {
        long time;
        long start = System.nanoTime();

        Map<String, Object> params = getParameters(request);
        final String action = (String) params.get(INPUT_ACTION);
        String error = null;
        String status = "OK";
        Object result = null;

        try {
            if (INPUT_ACTION_validate.equals(action)) {
                result = handleValidate(params);
                request.setAttribute(OUTPUT_VALIDATION, result);
            } else if (INPUT_ACTION_template.equals(action)) {
                result = handleTemplate(params);
                request.setAttribute(OUTPUT_TEMPLATE, result);
            }
        } catch (Exception e) {
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
            log.info("UploadServlet [" + getSessionNo(request) +
                "] : upload succeeded : servlet process : " + time + " ms.");
        }

        start = System.nanoTime();

        String viewPath = PATH_MANAGE + "ValidateResource.jsp";
        doForward(request, response, viewPath);

        time = ((System.nanoTime() - start) / 1000000L);

        if (log.isInfoEnabled()) {
            log.info("UploadServlet [" + getSessionNo(request) +
                "] : upload forwarded:  jsp     process : " + time);
        }
    }

    private ValidationResult handleValidate(Map<String, Object> parameters)
        throws Exception {
        InputStream in = null;

        try {
            final FileItem infile = (FileItem) parameters.get(INPUT_DOC);

            in = infile.getInputStream();

            ValidationResult result = dataModelManager.validateStream(in);

            return result;
        } catch (Throwable t) {
            throw new Exception(t);
        } finally {
            FileUtils.closeStream(in);
        }
    }

    /**
     * Create a template XML document for the datatype specified in parameter INPUT_TYPE.<br/>
     * @param parameters
     * @return
     */
    private String handleTemplate(Map<String, Object> parameters)
    {
      final String type = (String) parameters.get(INPUT_TYPE);
      
      return null;
    }
    
    /**
     * Extract a Map off key value pairs from the request which is assumed to be
     * POSTed as multipart/form-data.<br>
     * NOTE the Map is NOT structured as the result of a
     * ServletRequest::getParameterMap() i.e. with the value a String[], but has
     * an single String as value.
     *
     * @param request
     * @return
     */
    private Map<String, Object> getParameters(HttpServletRequest request) {
        Map<String, Object> parameters = new Hashtable<String, Object>();

        try {
            // Check that we have a file upload request
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
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
                FileItem fi = (FileItem) fileItems.get(i);
                String name = fi.getFieldName();
                Object value = null;

                if (fi.isFormField()) {
                    value = fi.getString();
                } else {
                    value = fi;
                }

                parameters.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parameters;
    }

    @Override
    public void init(ServletConfig sc) throws ServletException {
        // TODO Auto-generated method stub
        super.init(sc);

        try {
            dataModelManager = new DataModelManager(RuntimeConfiguration.getInstance()
                                                                        .getJPAPU());
        } catch (Exception e) {
            log.error(
                "Unable to initiate DataModelManager for UploadServlet using JPA persistence unit " +
                RuntimeConfiguration.getInstance().getJPAPU());
            dataModelManager = null; // TODO should we throw an exception or simply make uploads not possible?
        }
    }
}
