package org.gavo.hydrosims;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;

import org.gavo.sam.Datatype;
import org.gavo.sam.Model.Parameter;
import org.ivoa.jaxb.JAXBFactory;
import org.ivoa.runner.FileManager;
import org.ivoa.util.FileUtils;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;
import org.vourp.runner.model.EnumeratedParameter;
import org.vourp.runner.model.LegacyApp;
import org.vourp.runner.model.ParameterDeclaration;

public class Smac extends JobServlet {

	public static final String COMMENT_PREFIX = "#";
	public static final String FREE_PARAM_DELIMITER = "___";
	public static final String CONFIG_PARAM_DELIMITER = "@@@"; // are to be set
	// using data
	// from web.xml

	/* constants */
	public static final String MAIN_TASK = "main";
	/** The root directory wrt which the file paths in the database are defined */
	private String rootDataDir;
	private String executable;
	private JAXBFactory jaxbFactory;
	private LegacyApp legacyApp;
	private Hashtable<String, ParameterDeclaration> params;
	private String parametersTemplate = null;

	public static final String INPUT_PARAMETERS = "parameters";
	public static final String LEGACY_APP = "legacyApp";

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
			legacyApp = (LegacyApp) ((JAXBElement) jaxbFactory
					.createUnMarshaller().unmarshal(new File(legacyAppXML)))
					.getValue();
			parametersTemplate = initialiseParameters(file, config);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private String initialiseParameters(String parameterTemplateFile,
			ServletConfig config) throws Exception {

		params = new Hashtable<String, ParameterDeclaration>();
		Hashtable<String, ParameterDeclaration> freeParams = new Hashtable<String, ParameterDeclaration>();
		for (ParameterDeclaration p : legacyApp.getParameter()) {
			params.put(p.getName(), p);
			if (p instanceof EnumeratedParameter)
				((EnumeratedParameter) p).setNumSlaves(0);
		}

		for (ParameterDeclaration p : legacyApp.getParameter()) {
			if (p instanceof EnumeratedParameter) {
				EnumeratedParameter ep = (EnumeratedParameter) p;
				if (ep.getDependency() != null) {
					EnumeratedParameter master = (EnumeratedParameter) ep
							.getDependency().getMaster();
					if(master == null)
						System.out.println("help");
					master.setNumSlaves(master.getNumSlaves() + 1);
				}
			}
		}

		File f = new File(parameterTemplateFile);
		StringBuffer sb = new StringBuffer();
		if (f.exists() && f.isFile()) {

			BufferedReader reader = new BufferedReader(new FileReader(f));
			String l;
			while ((l = reader.readLine()) != null) {
				String lt = l.trim();
				if (lt.startsWith(COMMENT_PREFIX)) {
					sb.append(lt).append("\n");
					continue;
				}
				// check for existence of config parameters
				int index = -1;
				int length = lt.length();
				int fromIndex = 0;
				while (true) {
					index = lt.indexOf(CONFIG_PARAM_DELIMITER, fromIndex);
					if (index == -1)
						break;
					fromIndex = index;
					index = lt.indexOf(CONFIG_PARAM_DELIMITER, fromIndex+CONFIG_PARAM_DELIMITER.length());
					String configParamName = lt.substring(fromIndex+CONFIG_PARAM_DELIMITER.length(), index);
					String configParamValue = config.getInitParameter(configParamName);
					lt = lt.replaceAll(CONFIG_PARAM_DELIMITER+configParamName+CONFIG_PARAM_DELIMITER, configParamValue);
				}
				fromIndex = 0;
				while (true) {
					index = lt.indexOf(FREE_PARAM_DELIMITER, fromIndex);
					if (index == -1)
						break;
					fromIndex = index;
					index = lt.indexOf(FREE_PARAM_DELIMITER, fromIndex+ FREE_PARAM_DELIMITER.length());
					String freeParamName = lt.substring(fromIndex+ FREE_PARAM_DELIMITER.length(), index);
					ParameterDeclaration param = params.get(freeParamName);
					if(param == null)
						throw new Exception(String.format(
								"Free parameter %s detected in paramater template file which does not occur in configuraiton XML file.",freeParamName));
					else
						freeParams.put(freeParamName, param);
					fromIndex=index + FREE_PARAM_DELIMITER.length();
				}
				sb.append(lt).append("\n");
			}
		}
		return sb.toString();
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

	private String[] prepareMainTask(String workDir,
			HttpServletRequest req) throws IOException {
		
		if(!workDir.endsWith("/"))
			workDir = workDir+"/";
		// create a parameters file, write it to workDir
		String f = parametersTemplate;
		for(ParameterDeclaration p: legacyApp.getParameter())
		{
			String v = req.getParameter(p.getName());
			if(v == null)
				v = p.getDefaultValue();
		  f = f.replaceAll(FREE_PARAM_DELIMITER+p.getName()+FREE_PARAM_DELIMITER, v);
		}
		File params = new File(workDir+"smac.inp");
		FileWriter w = new FileWriter(params);
		w.write(f);
		w.flush();
		w.close();
		
		
		return executable.split("[ \t]+");
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
