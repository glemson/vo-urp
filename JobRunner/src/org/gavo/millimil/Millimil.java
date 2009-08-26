package org.gavo.millimil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.spi.RootCategory;
import org.ivoa.runner.FileManager;
import org.ivoa.util.runner.LocalLauncher;
import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;
import org.ivoa.util.runner.RunState;
import org.ivoa.util.runner.process.ProcessContext;
import org.ivoa.web.servlet.JobServlet;
import org.ivoa.web.servlet.JobStateException;


public class Millimil extends JobServlet {

  public static final String RUNQUERY_TASK = "runQuery";
  public static final String RUNQUERY_RESULT_FILE = "result.csv";
  public static final String RUNQUERY_ERROR_FILE = "error.txt";
  public static final String RPLOT_TASK = "Rplot";
  private String name;
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
    // TODO Auto-generated method stub
    String sql = req.getParameter("SQL");
    String url = baseURL + URLEncoder.encode(sql, "UTF-8");

    return new String[]{executable, "-O", RUNQUERY_RESULT_FILE, "\"" + url + "\""};
  }

  private void prepareRPlotTask(RootContext rootCtx) {
    String[] cmd = new String[]{Rcmd, "BATCH", "--no-save", Rscript, "Rplot.log"};
    LocalLauncher.prepareChildJob(rootCtx, RPLOT_TASK, cmd);
  }

  /**
   * Return the application name
   * @return
   */
  public String getName() {
    return name;
  }

  protected String showJob(final HttpServletRequest request, final Integer id) {
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
   * @return boolean: true of the processing should continue, false if the job should be terminated
   */
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
