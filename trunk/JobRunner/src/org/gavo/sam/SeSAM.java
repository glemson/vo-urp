package org.gavo.sam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.ivoa.runner.FileManager;
import org.ivoa.util.FileUtils;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;

import org.eclipse.persistence.tools.file.FileUtil;
import org.gavo.sam.Model.Parameter;

public class SeSAM extends JobServlet {

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  
  /* constants */
  public static final String MAIN_TASK = "main";
  public static final String PLOT_TASK = "plot";
  public static final String PARAMETER_FILE ="esam_params";
//  public static final String FREE_PARAM_DELIMITER = "___";
//  public static final String FIXED_PARAM_DELIMITER = "%%%";

  public static final String INPUT_MODEL = "model";
  public static final String INPUT_MODEL_current = "currentmodel";
  public static final String INPUT_PARAMETERS = "parameters";
  
  /* members */
  private String paramtemplate;
  private LinkedHashMap<String, Parameter> freeParameters;
//  private Hashtable<String, String> specialParameters = new Hashtable<String, String>();
  
  private String inputDir;
  private String treesDir;
  private String bc03Dir;
  private String modelsDir;
  private String executable;
  private String plotCommand;
  private String plotScript;

  private File readme;
  // ~ using Model
  private SeSAMModel model;
  private Hashtable<String, Map<String, String>> defaultModels;
  
  @Override
  protected void initialiseMainJob(RootContext rootCtx, HttpServletRequest request) throws JobStateException {
    try {
    	super.initialiseMainJob(rootCtx, request);
      String[] command = prepareMainTask(rootCtx.getWorkingDir(), request);
      LocalLauncher.prepareChildJob(rootCtx, MAIN_TASK, command);
    } catch (Exception e) {
      throw new JobStateException(e);
    }
  }

  private String[] prepareMainTask(final String workDir, HttpServletRequest req) throws IOException {
    String parameters = paramtemplate;
    String value;
    for (Parameter param : freeParameters.values()) {
      value = req.getParameter(param.name);

      if (JavaUtils.isEmpty(value)) {
        if (log.isWarnEnabled()) {
          log.warn("EyalsSAM.prepareMainTask : missing parameter value for : " + param);
        }
      } else {
        // TODO validate
    	  
          if(param.datatype == Datatype._file)
          {
        	  if(!"-1".equals(value))
        		  value=modelsDir+"/"+value+"/"+param.name;
        	  parameters = parameters.replaceAll(SeSAMModel.FILE_PARAM_DELIMITER + param.name + SeSAMModel.FILE_PARAM_DELIMITER, value);
          } else {
        	  parameters = parameters.replaceAll(SeSAMModel.FREE_PARAM_DELIMITER + param.name + SeSAMModel.FREE_PARAM_DELIMITER, value);
          }
      }

    }
    if (log.isDebugEnabled()) {
      log.debug("workDir :" + workDir);
    }

    String wd = workDir.replace('\\', '/');
    wd = workDir.replaceAll("[/]+","/");
    
    if (log.isDebugEnabled()) {
      log.debug("workDir repl :" + workDir);
    }

    parameters = parameters.replaceAll(SeSAMModel.FIXED_PARAM_DELIMITER + "workingDir" + SeSAMModel.FIXED_PARAM_DELIMITER, wd);

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
  protected String showInput(final HttpServletRequest request) {
	  String m = getStringParameter(request, INPUT_MODEL_current);
	  if(m == null || m.trim().length() ==0)
		  m = "standard";
	  
	  Map<String,String> p = this.model.getDefaultModel(m);
	  if(p == null)
		  p = new Hashtable<String,String>();
	  request.setAttribute(INPUT_PARAMETERS, p);
	  request.setAttribute(INPUT_MODEL_current, m);
	  request.setAttribute(INPUT_MODEL, model);
	  return getApplicationFolder() + "input.jsp";
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    //File
    try
    {
    String file = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("parameterfile.template");
    inputDir = FileManager.LEGACYAPPS + "/" + name + "/" ;
    readme = new File(inputDir+"README.txt");
    if(!readme.exists() || readme.isDirectory())
    	readme = null;
    // TODO check here?
    executable = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("executable");
    treesDir = config.getInitParameter("treesDir");
    bc03Dir = config.getInitParameter("bc03Dir");
    modelsDir = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("models.dir");

    model = new SeSAMModel(file);
    model.instantiateModels(modelsDir);
    
/*    
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
    */
    paramtemplate = model.paramTemplate;
    paramtemplate = paramtemplate.replaceAll(SeSAMModel.FIXED_PARAM_DELIMITER + "treesDir" + SeSAMModel.FIXED_PARAM_DELIMITER, treesDir);
    paramtemplate = paramtemplate.replaceAll(SeSAMModel.FIXED_PARAM_DELIMITER + "bc03Dir" + SeSAMModel.FIXED_PARAM_DELIMITER, bc03Dir);
    paramtemplate = paramtemplate.replaceAll(SeSAMModel.FIXED_PARAM_DELIMITER + "inputDir" + SeSAMModel.FIXED_PARAM_DELIMITER, inputDir);

    freeParameters = new LinkedHashMap<String, Parameter>();
    for(Parameter p : model.parameters.values()) {
    	if(p.isGroup())
    	{
    		for(Parameter gp : p.group)
    	    	if(!gp.isFixed)
    	    		freeParameters.put(gp.name, gp);
    	}
    	else if(!p.isFixed)
    		freeParameters.put(p.name, p);
    }
    
    
    plotCommand = config.getInitParameter("plotCommand");
    plotScript = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("plotScript");
    }
    catch(Throwable t)
    {
    	t.printStackTrace();
    }
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
    if(log.isInfoEnabled())
    	log.info("SeSAM.performTaskDone : "+rootCtx);
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
    	// copy README.txt
    	if(readme != null)
    	{
    		try
    		{
    			FileUtils.copyFile(readme, new File(dir.getAbsolutePath()+"/README.txt"));
    		} catch(IOException e){
    			if(log.isWarnEnabled())
    				log.warn("Failed copying README.txt file to working directory.");
    		}
    	}
    	
    	// ZIP up contents of directory apart from figures. 
    	// Depend on performJobDone in JobRunner servlet to copy all files to final archive.
    	// ...
    	File[] filesToCompress = dir.listFiles(new FileFilter(){
    		public boolean accept(File file) {
				String name = file.getName();
				return (name.startsWith("catalog") || PARAMETER_FILE.equals(name) || name.equalsIgnoreCase("README.txt"));
			}
    	});
    	final File zipFile = new File(dir.getAbsolutePath()+"/result.zip");
    	FileUtils.compress(filesToCompress, zipFile);
    	
    	File[] filesToDelete = dir.listFiles(new FileFilter(){
    		public boolean accept(File file) {
				String name = file.getName();
				return !(name.endsWith(".png") || file.equals(zipFile) || PARAMETER_FILE.equals(name) || name.equalsIgnoreCase("README.txt"));
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
