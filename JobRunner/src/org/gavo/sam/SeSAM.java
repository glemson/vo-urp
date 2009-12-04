package org.gavo.sam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.ivoa.runner.FileManager;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;


public class SeSAM extends JobServlet {

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  
  /* constants */
  public static final String MAIN_TASK = "main";
  public static final String PLOT_TASK = "plot";
  public static final String PARAMETER_FILE ="esam_params";
  public static final String FREE_PARAM_DELIMITER = "___";
  public static final String FIXED_PARAM_DELIMITER = "%%%";

  public static final String PARAM_fc_file = "fc_file";
  public static final String PARAM_fc_file_upload = "upload";
  public static final String PARAM_fc_file_default = "default";
  private String DEFAULT_fc_file;
  
  
  /* members */
  private String paramtemplate;
  private List<String> freeParameters;
  private Hashtable<String, String> specialParameters = new Hashtable<String, String>();
  
  private String inputDir;
  private String treesDir;
  private String bc03Dir;
  private String executable;
  private String plotCommand;
  private String plotScript;

  @Override
  protected void initialiseMainJob(RootContext rootCtx, HttpServletRequest request) throws JobStateException {
    try {
      String[] command = prepareMainTask(rootCtx.getWorkingDir(), request);
      LocalLauncher.prepareChildJob(rootCtx, MAIN_TASK, command);
    } catch (Exception e) {
      throw new JobStateException(e);
    }
  }

  private String[] prepareMainTask(final String workDir, HttpServletRequest req) throws IOException {
    String parameters = paramtemplate;
    String value;
    for (String param : freeParameters) {
      value = req.getParameter(param);

      if (JavaUtils.isEmpty(value)) {
        if (log.isWarnEnabled()) {
          log.warn("EyalsSAM.prepareMainTask : missing parameter value for : " + param);
        }
      } else {
        // TODO validate
    	  
    	if(PARAM_fc_file.equals(param))
    	{
    		if(PARAM_fc_file_default.equals(value))
    			value = DEFAULT_fc_file;
    	}
    	parameters = parameters.replaceAll(FREE_PARAM_DELIMITER + param + FREE_PARAM_DELIMITER, value);
      }

    }
    if (log.isDebugEnabled()) {
      log.debug("workDir :" + workDir);
    }

    final String wd = workDir.replace('\\', '/');
    if (log.isDebugEnabled()) {
      log.debug("workDir repl :" + workDir);
    }

    parameters = parameters.replaceAll(FIXED_PARAM_DELIMITER + "workingDir" + FIXED_PARAM_DELIMITER, wd);

    File w = new File(workDir);
    if(!w.exists())
    {
    	if(!w.mkdirs()) {
  	      if (log.isErrorEnabled())
            log.error("Error mkdirs on " + workDir);
          throw new IOException("Error mkdirs on " + workDir);
    	}
    }
    File f = new File(workDir + "/"+PARAMETER_FILE);

    if (log.isDebugEnabled()) {
      log.debug("file = " + f.getAbsolutePath());
    }
    FileWriter writer = new FileWriter(f);
    writer.write(parameters);
    writer.flush();
    writer.close();

    return new String[]{executable};
  }

  @Override
  protected String showJob(final HttpServletRequest request, final Long id) {
    String page = super.showJob(request, id);

    final RootContext ctx = (RootContext) request.getAttribute("runContext");
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

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    //File
    String file = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("parameterfile.template");
    inputDir = FileManager.LEGACYAPPS + "/" + name + "/" ;
    executable = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("executable");
    treesDir = config.getInitParameter("treesDir");
    bc03Dir = config.getInitParameter("bc03Dir");

    DEFAULT_fc_file = inputDir+config.getInitParameter("fc_file");
    
    
    StringBuilder sb = new StringBuilder();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      reader.close();
    } catch (IOException e) {
      // TODO do something or initialise differently ?
      throw new ServletException(e);
    }
    paramtemplate = sb.toString();
    paramtemplate = paramtemplate.replaceAll(FIXED_PARAM_DELIMITER + "treesDir" + FIXED_PARAM_DELIMITER, treesDir);
    paramtemplate = paramtemplate.replaceAll(FIXED_PARAM_DELIMITER + "bc03Dir" + FIXED_PARAM_DELIMITER, bc03Dir);
    paramtemplate = paramtemplate.replaceAll(FIXED_PARAM_DELIMITER + "inputDir" + FIXED_PARAM_DELIMITER, inputDir);
    int index1 = 0;
    freeParameters = new ArrayList<String>();
    while (true) {
      index1 = paramtemplate.indexOf(FREE_PARAM_DELIMITER, index1);
      if (index1 == -1) {
        break;
      }
      index1 += FREE_PARAM_DELIMITER.length(); // TODO optimize
      int index2 = paramtemplate.indexOf(FREE_PARAM_DELIMITER, index1);
      String param = paramtemplate.substring(index1, index2);
      freeParameters.add(param);
      index1 = index2 + FREE_PARAM_DELIMITER.length();// TODO optimize
    }
    
    
    
    plotCommand = config.getInitParameter("plotCommand");
    plotScript = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("plotScript");

  }

  /**
   * Perform the event from the given run context
   * @param rootCtx root context
   * @param runCtx  current run context
   * @return boolean: true of the processing should continue, false if the job should be terminated
   */
  @Override
  public boolean performTaskDone(final RootContext rootCtx, final RunContext runCtx) {
    boolean ok = true;
    if (MAIN_TASK.equals(runCtx.getName())) {
      //TODO add plotting tasks
      if (runCtx.getState() == RunState.STATE_FINISHED_OK) {
        preparePlotTask(runCtx.getParent());
        ok = true;
      } else {
        ok = false;
      }
    } else if(PLOT_TASK.equals(runCtx.getName())) {
    	// clean up files created only for plotting
    	File dir = new File(runCtx.getWorkingDir());
    	File[] filesToDelete = dir.listFiles(new FileFilter(){
    		public boolean accept(File file) {
				String name = file.getName();
				return !(name.startsWith("catalog") || name.endsWith(".png") || PARAMETER_FILE.equals(name));
			}
    	});
    	for(File file : filesToDelete)
    	{
    		file.delete();
    	}
    }
    return ok;
  }

  private void preparePlotTask(RootContext rootCtx) {
	String cmd = plotCommand.replaceFirst(":SCRIPT:", plotScript);
    
    LocalLauncher.prepareChildJob(rootCtx, PLOT_TASK, cmd.split("[ ]"));
  }
}
