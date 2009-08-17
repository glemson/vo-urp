package eyals.sam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivoa.runner.FileManager;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.util.runner.process.ProcessContext;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;

public class EyalsSAM extends JobServlet{

	// ~ the tasks
	public static final String MAIN_TASK = "main";
	public static final String PLOT_TASK = "plot";
	
	
	public static final String FREE_PARAM_DELIMITER = "___";
	public static final String FIXED_PARAM_DELIMITER = "%%%";
	private String paramtemplate;
	private List<String> freeParameters;
	private String treesDir;
	private String bc03Dir;
	private String executable;
	private String name;
	private String[] plotCommand;
	
	@Override
	protected void initialiseMainJob(RootContext rootCtx, HttpServletRequest request) throws JobStateException
	{
		try
		{
		String[] command = prepareMainTask(rootCtx.getWorkingDir(), request);
        LocalLauncher.prepareChildJob(rootCtx, MAIN_TASK, command);
		} catch(Exception e)
		{
			throw new JobStateException(e);
		}
	}
	
	
    private String[] prepareMainTask(final String workDir, HttpServletRequest req) throws IOException {
		// TODO Auto-generated method stub
		String parameters = paramtemplate;
		for(String param : freeParameters)
		{
			String value = req.getParameter(param);
			// TODO validate
			parameters = parameters.replaceAll(FREE_PARAM_DELIMITER+param+FREE_PARAM_DELIMITER, value);
		}
		log.debug("workDir :"+workDir);
		
		final String wd = workDir.replace('\\', '/');
		log.debug("workDir repl :"+workDir);
		
		parameters = parameters.replaceAll(FIXED_PARAM_DELIMITER+"workingDir"+FIXED_PARAM_DELIMITER, wd);

		File f = new File(workDir+"/esam_params");
  	    FileWriter w = new FileWriter(f);
		w.write(parameters);
		w.flush();
		w.close();
		
		return new String[]{executable};
	}

    /**
     * Return the job default folder default/
     * @return
     */
    public String getName() {
      return name;
    }

    protected String showJob(final HttpServletRequest request, final Integer id) {
        String page = super.showJob(request, id);
        
        final RootContext ctx = (RootContext)request.getAttribute("runContext");
        if(ctx != null) {
	        File path; 
	        switch(ctx.getState())
	        {
	        	case STATE_RUNNING:
	        		path = new File(ctx.getWorkingDir());
	        		break;
	        	case STATE_FINISHED_ERROR :
	        	case STATE_FINISHED_OK :
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
		// TODO Auto-generated method stub
		//File
		name = config.getInitParameter("name");
		String file = FileManager.LEGACYAPPS+"/"+name+"/"+config.getInitParameter("parameterfile.template");
		executable = FileManager.LEGACYAPPS+"/"+name+"/"+config.getInitParameter("executable");
		treesDir = config.getInitParameter("treesDir");
		bc03Dir = config.getInitParameter("bc03Dir");
		
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null)
				sb.append(line).append("\n");
			reader.close();
		} catch(IOException e)
		{
			// TODO do something or initialise differently ?
			throw new ServletException(e);
		}
		paramtemplate = sb.toString();
		paramtemplate = paramtemplate.replaceAll(FIXED_PARAM_DELIMITER+"treesDir"+FIXED_PARAM_DELIMITER, treesDir);
		paramtemplate = paramtemplate.replaceAll(FIXED_PARAM_DELIMITER+"bc03Dir"+FIXED_PARAM_DELIMITER, bc03Dir);
		int index1 =0;
		freeParameters = new ArrayList<String>();
		while(true)
		{
		   index1 = paramtemplate.indexOf(FREE_PARAM_DELIMITER, index1);
		   if(index1 == -1)
			   break;
		   index1+=FREE_PARAM_DELIMITER.length(); // TODO optimize
		   int index2 = paramtemplate.indexOf(FREE_PARAM_DELIMITER, index1);
		   String param = paramtemplate.substring(index1, index2);
		   freeParameters.add(param);
		   index1=index2+FREE_PARAM_DELIMITER.length();// TODO optimize
		}
	}
    /**
     * Perform the event from the given run context
     * @param rootCtx root context
     * @param runCtx  current run context
     * @return boolean: true of the processing should continue, false if the job should be terminated
     */
    public boolean performTaskDone(final RootContext rootCtx, final RunContext runCtx) {
    	boolean ok = true;
    	if(MAIN_TASK.equals(runCtx.getName()))
    	{
    		//TODO add plotting tasks
    		if(runCtx.getState() == RunState.STATE_FINISHED_OK)
    		{
    			ok = true;
    			LocalLauncher.prepareChildJob(rootCtx, PLOT_TASK, plotCommand);
    		} else {
    			ok = false;
    		}
    	}
    	return ok;
    }
}
