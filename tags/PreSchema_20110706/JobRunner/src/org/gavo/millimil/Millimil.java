package org.gavo.millimil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.ivoa.runner.FileManager;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;


public class Millimil extends JobServlet {

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  /* constants */
  public static final String RUNQUERY_TASK = "runQuery";
  public static final String RUNQUERY_RESULT_FILE = "result.csv";
  public static final String RUNQUERY_ERROR_FILE = "error.txt";
  public static final String RPLOT_TASK = "Rplot";

  /* members */
  private String executable;
  private String baseURL;
  private String Rcmd;
  private String Rscript;

  @Override
  protected void initialiseMainJob(RootContext rootCtx, HttpServletRequest request) throws JobStateException {
    // add the first task in the root context :
    try {
      String[] command = prepareQueryTask(rootCtx.getWorkingDir(), request);
      LocalLauncher.prepareChildJob(rootCtx, RUNQUERY_TASK, command);
    } catch (Exception e) {
      throw new JobStateException(e);
    }
  }

  private String[] prepareQueryTask(final String workDir, HttpServletRequest req) throws IOException {
    String sql = req.getParameter("SQL");
    String url = baseURL + URLEncoder.encode(sql, "UTF-8");

    return new String[]{executable, "-q", "-O", RUNQUERY_RESULT_FILE, url};
  }

  private void prepareRPlotTask(RootContext rootCtx) {
    String[] cmd = new String[]{Rcmd, "--vanilla", Rscript};
    LocalLauncher.prepareChildJob(rootCtx, RPLOT_TASK, cmd);
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
    
    name = config.getInitParameter("name");
    executable = config.getInitParameter("executable");
    baseURL = config.getInitParameter("baseURL");

    Rcmd = config.getInitParameter("Rcmd");
    Rscript = FileManager.LEGACYAPPS + "/" + name + "/" + config.getInitParameter("Rscript");
  }

  /**
   * Perform the event from the given run context
   * @param rootCtx root context
   * @param runCtx  current run context
   * @return boolean: true if the processing should continue, false if the job should be terminated
   */
  @Override
  public boolean performTaskDone(final RootContext rootCtx, final RunContext runCtx) {
    boolean ok = true;
    if (RUNQUERY_TASK.equals(runCtx.getName())) {
      if (runCtx.getState() == RunState.STATE_FINISHED_OK) {
        File result = new File(runCtx.getWorkingDir() + "/" + RUNQUERY_RESULT_FILE);
        if (result.exists()) {
          try {
            BufferedReader reader = new BufferedReader(new FileReader(result));
            String line = reader.readLine();
            if (line != null && line.startsWith("#OK")) {
              ok = true;
            } else {
              ok = false;
            }
            reader.close();
            if (!ok) {
              FileManager.moveFile(result, new File(runCtx.getWorkingDir() + "/" + RUNQUERY_ERROR_FILE));
            } else {
              prepareRPlotTask(runCtx.getParent());
            }
          } catch (IOException e) {
            // TODO TBD should we throw an exception here?
            ok = false;
          }
        }
      } else {
        ok = false;
      }
    } else if (RPLOT_TASK.equals(runCtx.getName())) {
      if (runCtx.getState() != RunState.STATE_FINISHED_OK) {
        ok = false;
      }
    }
    return ok;
  }
}
