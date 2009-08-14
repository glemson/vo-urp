package org.ivoa.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ivoa.runner.FileManager;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.runner.JobListener;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.util.runner.process.ProcessContext;

/**
 * The servlet class to list Experiment from SNAP database
 */
public class JobServlet extends BaseServlet implements JobListener {

    /** serial UID for Serializable interface */
    private static final long serialVersionUID = 1L;
    // actions :
    public static final String ACTION_START_JOB = "start";
    public static final String ACTION_SHOW_JOB = "detail";
    public static final String ACTION_KILL_JOB = "kill";
    public static final String ACTION_SHOW_QUEUE = "list";
    
    // TODO : add kill action
    
    // constants :
    public static final String INPUT_ACTION = "action";
    public static final String INPUT_ID = "id";
    public static final String OUTPUT_CONTENT = "content";


    /** limit of lines in ring buffer */
    public final static int MAX_LINES = 25;

    /** 
     * @return a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Job servlet";
    }

    /**
     * Return the job default folder default/
     * @return
     */
    public String getName() {
      return "default";
    }
    /**
     * Returns the folder name where the application specific input/detail/queue JSP pages are stored.
     * This MUST correspond to the name of the application
     * @return
     */
    public final String getApplicationFolder()
    {
       return getName()+"/";
    }
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) {

        long time;
        long start = System.nanoTime();

        final String action = getStringParameter(request, INPUT_ACTION);

        final String user = request.getRemoteUser();
        
        // Process Session (creates a new one on first time) :
        final HttpSession session = createSession(request);

        // note : this session is unuseful but it should be when user will have login / password.
        
        String view = null;
        try {
            if (ACTION_START_JOB.equals(action)) {
                final String workDir = computeWorkingDirectory(FileManager.getSessionFolder(session.getId(), user).getAbsolutePath().replace('\\','/')) + "/";

                final String[] command = prepareJobParameters(workDir, request);
                
                final RootContext runCtx = startJob(request, workDir, command);
                if (runCtx != null) {
                    view = null; // showJob(request, runCtx.getId());
                    
                    try {
                        doRedirect(response, request.getRequestURI()+"?action="+ACTION_SHOW_JOB+"&"+INPUT_ID+"="+runCtx.getId());
                    } catch (Exception e) {
                        log.error("failure : ", e);
                    }
                    
                }
            } else if (ACTION_SHOW_QUEUE.equals(action)) {
                view = showQueue(request);
            } else if (ACTION_SHOW_JOB.equals(action)) {
                view = showJob(request, getIntParameter(request, INPUT_ID, 0));
            } else if (ACTION_KILL_JOB.equals(action)) {
                killJob(request, getIntParameter(request, INPUT_ID, 0));
                
                view = showQueue(request);                
            }
        } catch (Exception e) {
            log.error("failure : ", e);
        }

        // Output parameters : 
        request.setAttribute(OUTPUT_TITLE, "Job Manager");

        time = ((System.nanoTime() - start) / 1000000l);
        if (log.isInfoEnabled()) {
            log.info("JobServlet [" + getSessionNo(request) + "] : servlet process : " + time + " ms.");
        }

        if (!JavaUtils.isEmpty(view)) {
            start = System.nanoTime();
            try {
                doForward(request, response, view);
            } catch (Exception e) {
                log.error("failure : ", e);
            }
            time = ((System.nanoTime() - start) / 1000000l);
            if (log.isInfoEnabled()) {
                log.info("JobServlet [" + getSessionNo(request) + "] : jsp     process : " + time + " ms. View[default]");
            }
        }

    }

    private RootContext startJob(final HttpServletRequest request, final String workDir, final String[] command) {
        if (log.isDebugEnabled()) {
            log.debug("JobServlet.startJob : enter");
        }

        // create the execution context :
        
        // absolute file path to write STD OUT / ERR streams (null indicates not to use a file dump)
        final String writeLogFile = null;
        
        final RootContext rootCtx = LocalLauncher.prepareMainJob(getName(), request.getRemoteUser(), workDir, MAX_LINES, writeLogFile);

        // add the first task in the root context :
        LocalLauncher.prepareChildJob(rootCtx, "main", command);
        
        // puts the job in the job queue :
        // can throw IllegalStateException if job not queued :
        LocalLauncher.startJob(rootCtx, this);
        
        return rootCtx;
    }

    private void killJob(final HttpServletRequest request, final Integer id) {
        final RunContext runCtx = LocalLauncher.getJob(id);
        try {
            if (runCtx instanceof ProcessContext) {
                // java process is killed => unix process is killed :
                ((ProcessContext)runCtx).kill();
            }

        } finally {
            // clear ring buffer :
            runCtx.close();
        }
    }

    private String showQueue(final HttpServletRequest request) {
        final List<RootContext> list = LocalLauncher.getQueue();
        
        request.setAttribute("queue", list);
        
        return "default/queue.jsp";
    }

    protected String showJob(final HttpServletRequest request, final Integer id) {
        final RunContext runCtx = LocalLauncher.getJob(id);

        request.setAttribute("runContext", runCtx);
        
        return getApplicationFolder() + "detail.jsp";
    }

    
    
    /**
     * Perform the event from the given root context
     * @param rootCtx root context
     */
    public void performJobEvent(final RootContext rootCtx) {
        if (log.isInfoEnabled()) {
            log.info("JobServlet.performJobEvent : job : " + rootCtx);
        }
        
        if ((rootCtx.getState() == RunState.STATE_FINISHED_ERROR) || (rootCtx.getState() == RunState.STATE_FINISHED_OK)) {
        	String sourceDir = rootCtx.getWorkingDir();
        	String relativePath = sourceDir.replaceFirst(FileManager.RUNNER, "");
        	String targetDir = FileManager.ARCHIVE+relativePath;
        	
            FileManager.moveDir(sourceDir, targetDir);  
            rootCtx.setRelativePath(relativePath);
        }
    }

    /**
     * Perform the event from the given run context
     * @param rootCtx root context
     * @param runCtx  current run context
     */
    public void performTaskEvent(final RootContext rootCtx, final RunContext runCtx) {
        if (log.isInfoEnabled()) {
            log.info("JobServlet.performTaskEvent : job : " + runCtx);
        }
    }

    /**
     * Perform the event from the given run context
     * @param rootCtx root context
     * @param runCtx  current run context
     * @return boolean: true of the processing should continue, false if the job should be terminated
     */
    public boolean performTaskDone(final RootContext rootCtx, final RunContext runCtx) {
    	return runCtx.getState() == RunState.STATE_FINISHED_OK;
    }

    protected String computeWorkingDirectory(final String baseWorkDir) {
    	return baseWorkDir;
    }
    
    
    /*
     * Write parameter files for momaf job, filled with form from momaf_downloads.jsp
     */
    protected String[] prepareJobParameters(final String workDir, HttpServletRequest request) throws IOException {
        return new String[]{
                    "/usr/bin/tail", "-10000000", "/home/lbourges/dev/dev/JobRunner/lib/apache-LICENSE-2.0.txt"
                };
    }


}
