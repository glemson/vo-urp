package org.gavo.hydrosims;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;

import org.ivoa.jaxb.JAXBFactory;
import org.ivoa.runner.FileManager;
import org.ivoa.util.FileUtils;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;
import org.vourp.runner.model.LegacyApp;

public class Smac extends JobServlet {

	public static final String COMMENT_PREFIX = "#";
	public static final String FREE_PARAM_DELIMITER = "___";
	public static final String FIXED_PARAM_DELIMITER = "%%%";
	public static final String FILE_PARAM_DELIMITER = "###";

	/* constants */
	public static final String MAIN_TASK = "main";
	/** The root directory wrt which the file paths in the database are defined */
	private String rootDataDir;
	private String executable;
	private JAXBFactory jaxbFactory;
	private LegacyApp legacyApp;

	public static final String INPUT_PARAMETERS = "parameters";
	public static final String SIMULATION = "simulation";
	public static final String SNAPSHOT = "snapshot";

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config); // sets name etc
		// File
		try {
			String jaxbpath = config.getInitParameter("jaxb.path");
			String legacyAppXML = FileManager.LEGACYAPPS + "/" + name + "/"
					+ config.getInitParameter("legacy.app.xml");
			String file = FileManager.LEGACYAPPS + "/" + name + "/"
					+ config.getInitParameter("parameterfile.template");
			rootDataDir = config.getInitParameter("root.data.dir");
			executable = FileManager.LEGACYAPPS + "/" + name + "/"
					+ config.getInitParameter("executable");

			jaxbFactory = JAXBFactory.getInstance(jaxbpath);
			legacyApp = (LegacyApp) ((JAXBElement)jaxbFactory.createUnMarshaller().unmarshal(
					new File(legacyAppXML))).getValue();

			initialiseParameterFile(file);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void initialiseParameterFile(String file) {

	}

	@Override
	protected void initialiseMainJob(RootContext rootCtx,
			HttpServletRequest request) throws JobStateException {
		try {
			super.initialiseMainJob(rootCtx, request);
			String[] command = prepareMainTask(rootCtx.getWorkingDir(), request);
			LocalLauncher.prepareChildJob(rootCtx, MAIN_TASK, command);
		} catch (Exception e) {
			throw new JobStateException(e);
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
	@Override
	public boolean performTaskDone(final RootContext rootCtx,
			final RunContext runCtx) {
		boolean ok = true;
		if (log.isInfoEnabled())
			log.info("SeSAM.performTaskDone : " + rootCtx);
		if (MAIN_TASK.equals(runCtx.getName())) {
			// TODO add plotting tasks
			if (runCtx.getState() == RunState.STATE_FINISHED_OK) {
			}
		}
		return ok;
	}

	private String[] prepareMainTask(final String workDir,
			HttpServletRequest req) throws IOException {
		return new String[] { executable };
	}

	@Override
	protected String showInput(final HttpServletRequest request) {
		/*
		 * String m = getStringParameter(request, INPUT_MODEL_current); if(m ==
		 * null || m.trim().length() ==0) m = "standard";
		 * 
		 * request.setAttribute(INPUT_MODEL_current, m);
		 * request.setAttribute(INPUT_MODEL, model);
		 */
		java.util.Map<String, String> p = null;
		if (p == null)
			p = new java.util.Hashtable<String, String>();
		request.setAttribute(INPUT_PARAMETERS, p);
		request.setAttribute("LEGACY_APP", legacyApp);
		return getApplicationFolder() + "input.jsp";
	}

	@Override
	protected String showJob(final HttpServletRequest request, final Long id) {
		String page = super.showJob(request, id);

		final RootContext ctx = (RootContext) request
				.getAttribute("runContext");
		if (ctx != null) {
			File path;
			switch (ctx.getState()) {
			case STATE_RUNNING:
				path = new File(ctx.getWorkingDir());
				break;
			case STATE_FINISHED_ERROR:
			case STATE_FINISHED_OK:
				path = new File(FileManager.ARCHIVE, ctx.getRelativePath());
				break;
			default:
				path = null;
			}
			request.setAttribute("resultDir", path);
		}

		return page;
	}

}
