package org.ivoa.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.persistence.internal.libraries.asm.tree.MethodNode;
import org.ivoa.runner.FileManager;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.runner.JobListener;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.ParameterSetting;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.util.runner.Validator;

/**
 * The servlet class to list Experiment from SNAP database
 */
public class JobServlet extends BaseServlet implements JobListener {

	/** serial UID for Serializable interface */
	private static final long serialVersionUID = 1L;
	// actions :
	public static final String ACTION_SHOW_INPUT = "input";
	public static final String ACTION_START_JOB = "start";
	public static final String ACTION_SHOW_JOB = "detail";
	public static final String ACTION_CANCEL_JOB = "cancel";
	public static final String ACTION_KILL_JOB = "kill";
	public static final String ACTION_SHOW_QUEUE = "list";
	public static final String ACTION_SHOW_HISTORY = "history";
	// TODO : add kill action
	// constants :
	public static final String INPUT_ACTION = "action";
	public static final String INPUT_ID = "id";
	public static final String OUTPUT_CONTENT = "content";

	
	// config parameters
	private int MAX_JOBS = 6;
	
	/* members */
	/** application name defined in web.xml */
	protected String name;

	/**
	 * @return a short description of the servlet.
	 */
	@Override
	public String getServletInfo() {
		return "Job servlet";
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		this.name = config.getInitParameter("name");
		String s_mj = config.getInitParameter("max_jobs");
		if(s_mj != null)
		{
			try
			{
				this.MAX_JOBS = Integer.parseInt(s_mj);
			} catch(NumberFormatException e)
			{
				this.MAX_JOBS = 6;
			}
		}

		LocalLauncher.registerJobListener(getName(), this);
	}

	/**
	 * Return the application name used for the job listener registration and
	 * the folder name to compute the working directory
	 * 
	 * @return application name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns the folder name where the application specific input/detail/queue
	 * JSP pages are stored. This MUST correspond to the name of the application
	 * 
	 * @return application folder
	 */
	public final String getApplicationFolder() {
		return "apps/" + getName() + "/";
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 */
	@Override
	protected void processRequest(final HttpServletRequest request,
			final HttpServletResponse response) {

		long time;
		long start = System.nanoTime();

		final String action = getStringParameter(request, INPUT_ACTION);

		final String user = request.getRemoteUser();
		
		
		
		// Process Session (creates a new one on first time) :
		final HttpSession session = createSession(request);

		// note : this session is unuseful but it should be when user will have
		// login / password.

		String view = null;
		boolean wasForwarded = false;
		try {
			if (ACTION_SHOW_INPUT.equals(action))
				view = showInput(request);
			else if (ACTION_START_JOB.equals(action)) {

				// validate
				// TODO if invalid request, send back to input with error messages.
				
				// check # of jobs user has on queue, if more than MAX_JOBS do
				// not allow new one.
				if (LocalLauncher.queryActiveQueuedJobs(user) >= MAX_JOBS) {
					view = "./queueFull.jsp";
				} else {

					String baseWorkDir = FileManager.getUserRunnerFolder(user)
							.getAbsolutePath().replace('\\', '/');
					String workDir = createWorkingDirectory(baseWorkDir);
					if (!workDir.endsWith("/"))
						workDir = workDir + "/";

					// TBD next commented out, should be performed in
					// initialisation of main job
					// final String[] command = prepareJobParameters(workDir,
					// request);

					final RootContext runCtx = startJob(request, workDir);
					if (runCtx != null) {
						view = null;
						try {
							doRedirect(response, request.getRequestURI()
									+ "?action=" + ACTION_SHOW_JOB + "&"
									+ INPUT_ID + "=" + runCtx.getId());
							wasForwarded = true;
						} catch (Exception e) {
							log.error("failure : ", e);
						}
					}
				}
			} else if (ACTION_SHOW_QUEUE.equals(action)) {
				view = showQueue(request);
			} else if (ACTION_SHOW_HISTORY.equals(action)) {
				view = showHistory(request);
			} else if (ACTION_SHOW_JOB.equals(action)) {
				view = showJob(request, getLongParameter(request, INPUT_ID, 0l));
			} else if (ACTION_KILL_JOB.equals(action)
					|| ACTION_CANCEL_JOB.equals(action)) {

				if (ACTION_KILL_JOB.equals(action)) {
					LocalLauncher.killJob(getLongParameter(request, INPUT_ID,
							0l));
				} else {
					LocalLauncher.cancelJob(getLongParameter(request, INPUT_ID,
							0l));
				}

				view = null;
				try {
					doRedirect(response, request.getRequestURI() + "?action="
							+ ACTION_SHOW_QUEUE);
					wasForwarded = true;
				} catch (Exception e) {
					log.error("failure : ", e);
				}
			} else { // if action is unknown, assume it is implemented by a subclass
				view = performAction(action, request, response);
			}
		} catch (Exception e) {
			log.error("failure : ", e); // TODO need to do more, e.g. go to an
										// error page

		}

		if (wasForwarded)
			return;
		// Output parameters :
		request.setAttribute(OUTPUT_TITLE, "Job Manager");

		time = ((System.nanoTime() - start) / 1000000l);
		if (log.isInfoEnabled()) {
			log.info("JobServlet [" + getSessionNo(request)
					+ "] : servlet process : " + time + " ms.");
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
				log.info("JobServlet [" + getSessionNo(request)
						+ "] : jsp     process : " + time
						+ " ms. View[default]");
			}
		} else {
			try {
				doForward(request, response, "./error.jsp");
			} catch (Exception e) {
				log.error("failure : ", e);
			}
		}

	}

	private RootContext startJob(final HttpServletRequest request,
			final String workDir) throws JobStateException {
		if (log.isDebugEnabled()) {
			log.debug("JobServlet.startJob : enter");
		}

		// create the execution context :
		// TODO : manage the writeLogFile absolute file path :
		final RootContext rootCtx = LocalLauncher.prepareMainJob(getName(),
				request.getRemoteUser(), workDir, null);
		
		String relativePath = workDir.replaceFirst(FileManager
				.getRunnerFolder().getAbsolutePath().replace('\\', '/'), "");
		while (relativePath.startsWith("/"))
			// TODO fix this hack
			relativePath = relativePath.substring(1);
		rootCtx.setRelativePath(relativePath);

		initialiseMainJob(rootCtx, request);// includes if necessary adding
											// first child to main and preparing

		// puts the job in the job queue :
		// can throw IllegalStateException if job not queued :
		LocalLauncher.startJob(rootCtx);

		return rootCtx;
	}

	private String showQueue(final HttpServletRequest request) {
		final List<RootContext> list = LocalLauncher.getQueue();

		request.setAttribute("queue", list);

		return "./queue.jsp";
	}

	private String showHistory(final HttpServletRequest request) {
		String owner = request.getRemoteUser();
		final List<RootContext> list = LocalLauncher.queryHistory(owner);

		request.setAttribute("history", list);

		return "./history.jsp";
	}

	protected String showJob(final HttpServletRequest request, final Long id) {
		RunContext runCtx = LocalLauncher.getJob(id);
		if (runCtx == null)
			runCtx = LocalLauncher.queryJob(id);

		request.setAttribute("runContext", runCtx);

		return getApplicationFolder() + "detail.jsp";
	}

	protected String showInput(final HttpServletRequest request) {
		return getApplicationFolder() + "input.jsp";
	}

	protected void initialiseMainJob(RootContext rootCtx,
			HttpServletRequest request) throws JobStateException {
		Enumeration<String> e = request.getParameterNames();
		while(e.hasMoreElements())
		{
			String name = e.nextElement();
			ParameterSetting p = new ParameterSetting(rootCtx, name, request.getParameter(name));
		}
	}

	/**
	 * Perform the event from the given root context
	 * 
	 * @param rootCtx
	 *            root context
	 */
	public void performJobEvent(final RootContext rootCtx) {
		if (log.isInfoEnabled()) {
			log.info("JobServlet.performJobEvent : job : " + rootCtx);
		}

		if ((rootCtx.getState() == RunState.STATE_FINISHED_ERROR)
				|| (rootCtx.getState() == RunState.STATE_FINISHED_OK)) {
			String sourceDir = rootCtx.getWorkingDir();
			String relativePath = sourceDir
					.replaceFirst(FileManager.RUNNER, "");
			relativePath = rootCtx.getRelativePath();
			String targetDir = FileManager.ARCHIVE + relativePath;

			FileManager.moveDir(sourceDir, targetDir);
		} else if ((rootCtx.getState() == RunState.STATE_KILLED)
				|| (rootCtx.getState() == RunState.STATE_CANCELLED)) {
			File workingDir = new File(rootCtx.getWorkingDir());
			FileManager.deleteDirectoryFiles(workingDir);
			workingDir.delete();
		}
	}

	/**
	 * Perform the event from the given run context
	 * 
	 * @param rootCtx
	 *            root context
	 * @param runCtx
	 *            current run context
	 */
	public void performTaskEvent(final RootContext rootCtx,
			final RunContext runCtx) {
		if (log.isInfoEnabled()) {
			log.info("JobServlet.performTaskEvent : job : " + runCtx);
		}
	}

	/**
	 * Perform the event from the given run context
	 * 
	 * @param rootCtx
	 *            root context
	 * @param runCtx
	 *            current run context
	 * @return boolean: true of the processing should continue, false if the job
	 *         should be terminated
	 */
	public boolean performTaskDone(final RootContext rootCtx,
			final RunContext runCtx) {
		return runCtx.getState() == RunState.STATE_FINISHED_OK;
	}

	/**
	 * 
	 * GL: changed from simply returning baseWorkDir to adding the name of the
	 * program and a random uuid.
	 * 
	 * @param baseWorkDir
	 * @return working directory
	 * @throws IOException
	 *             if mkdirs failed
	 */
	protected final String createWorkingDirectory(final String baseWorkDir)
			throws IOException {

		String jobUUID = java.util.UUID.randomUUID().toString();
		final String workDir = baseWorkDir
				+ (baseWorkDir.endsWith("/") ? "" : "/") + jobUUID;

		File dir = new File(workDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return workDir;
	}

	/*
	 * 
	 */
	private String[] prepareJobParameters(final String workDir,
			HttpServletRequest request) throws IOException {
		return new String[] { "/usr/bin/tail", "-10000000",
				"/home/lbourges/dev/dev/JobRunner/lib/apache-LICENSE-2.0.txt" };
	}
	/**
	 * Method supporting a custom action, not covered by the standard actions defined on this servlet.<br/>
	 * Should be implemented by subclass, hence an exception is thrown if this method is reached.
	 * @param action
	 * @param request
	 * @param response
	 * @return
	 */
	protected String performAction(String action, HttpServletRequest request, HttpServletResponse response)
	{
		throw new UnsupportedOperationException("This method must be implemented on a subclass");
	}
	
	
}

