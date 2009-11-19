package org.gavo.sam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.ivoa.runner.FileManager;
import org.ivoa.runner.apps.Workflow;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;

/**
 * This class must represent a generic workflow consisting of one or more tasks as defined by a 
 * an org.ivoa.runner.apps.Workflow object. It should be able to keep track of the state in the workflow
 * to know which task must be performed after the last. This likely requires additions to RunContext and/or RootContext.
 * 
 * Likely this should not be a servlet.
 * 
 * TBD [GL 2009-10-02] For the moment this would seem to require rather many changes to RootContext/RunContext and is therefore postponed.
 * 
 * @author lemson
 *
 */
public class GenericJobRunner extends JobServlet {

	private Workflow workflow;
	
  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;
  
  public static final String FREE_PARAM_DELIMITER = "___";

  @Override
  protected void initialiseMainJob(RootContext rootCtx, HttpServletRequest request) throws JobStateException {
    try {
      String[] command = prepareMainTask(rootCtx.getWorkingDir(), request);
      LocalLauncher.prepareChildJob(rootCtx, "main", command);
    } catch (Exception e) {
      throw new JobStateException(e);
    }
  }
  
  

  private String[] prepareMainTask(final String workDir, HttpServletRequest req) throws IOException {
	  
    return new String[]{};
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

  /**
   * Initialise a Workflow from the file specified in the config.
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    //File
    String file = config.getInitParameter("config.file");
	try
	{
    	this.workflow = null;
    	JAXBContext jc = JAXBContext.newInstance("org.ivoa.runner.apps");
    	Unmarshaller um = jc.createUnmarshaller();
    	Object o = um.unmarshal(new java.io.FileInputStream(file));
    	if(o instanceof Workflow )
    		this.workflow = (Workflow)o;
    	else if(o instanceof JAXBElement)
    	{
    		JAXBElement<Workflow> je = (JAXBElement<Workflow>)o;
    		this.workflow =  je.getValue();
    	}
	} catch(Exception e)
	{
		e.printStackTrace();
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
    if ("main".equals(runCtx.getName())) {
      //TODO add plotting tasks
      if (runCtx.getState() == RunState.STATE_FINISHED_OK) {
        prepareNextTask(runCtx.getParent());
        ok = true;
      } else {
        ok = false;
      }
    }
    return ok;
  }

  private void prepareNextTask(RootContext root){}
}