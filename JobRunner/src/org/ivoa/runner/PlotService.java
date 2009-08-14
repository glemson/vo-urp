package org.ivoa.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.ivoa.bean.LogSupport;
import org.ivoa.util.FileUtils;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.runner.process.ProcessContext;
import org.ivoa.util.runner.process.ProcessRunner;
import org.ivoa.util.runner.process.RingBuffer;


/**
 * Simple interface with gnuplot
 *
 * @author laurent bourges (voparis)
 */
public final class PlotService extends LogSupport {

  /** executable */
  public final static String EXECUTABLE = "ll";

  /** plot template 2D */
  public final static String TEMPLATE = "plot_template.gp";
  /* plot template parameters */
  public final static String PLOT_INPUT = "%input%";
  public final static String PLOT_OUTPUT = "%output%";

  /**
   * Constructor
   */
  private PlotService() {
  }

  public static File preparePlot2D(final File inputFile, final File outputFile, final Object... args) {
    if (log.isDebugEnabled()) {
      log.debug("PlotService.preparePlot2D : enter : " + JavaUtils.asList(args));
    }

    final String template = loadTemplate(TEMPLATE);

    // process the template
    final String script = processTemplate(template, inputFile.getAbsolutePath(), outputFile.getAbsolutePath()); 

    final File file = new File(inputFile.getParent(), TEMPLATE);
    writeTemplate(file, script);

    if (log.isDebugEnabled()) {
      log.debug("PlotService.preparePlot2D : exit : " + script);
    }
    return file;
  }

  public static void doPlot(final File script) {
    if (log.isDebugEnabled()) {
      log.debug("PlotService.doPlot : enter");
    }

    final String[] command = new String[]{EXECUTABLE, script.getAbsolutePath()};

    final ProcessContext runCtx = null; //new ProcessContext(0, command, script.getParentFile().getAbsolutePath());
    runCtx.setRing(new RingBuffer(10, null));

    // starts program & waits for its end (and std threads) :
    // uses a ring buffer for stdout/stderr :
    int status = ProcessRunner.execute(runCtx);

    // ring buffer is not synchronized because threads have finished their jobs in ProcessRunner.run(runCtx) :
    if (status != 0) {
      runCtx.getRing().add(ProcessRunner.ERR_PREFIX, "Ended with a failure exit code : " + status + ".");

      log.error("PlotService.doPlot : process output : \n" + runCtx.getRing().getContent());

    } else {
      if (log.isInfoEnabled()) {
        log.info("PlotService.doPlot : process output : \n" + runCtx.getRing().getContent());
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("PlotService.doPlot : exit");
    }
  }

  private static String processTemplate(final String template,  final String input, final String output) {
    String res = template.replace(PLOT_INPUT, input);
    res = res.replace(PLOT_OUTPUT, output);
    // ...

    return res;
  }

  private static String loadTemplate(final String name) {
    final String fileName = FileManager.TEMPLATES + name;

    final BufferedReader input = (BufferedReader) FileUtils.readFile(fileName);
    final StringBuilder content = new StringBuilder(512);
    try {
      String line = null; //not declared within while loop
      final String sep = System.getProperty("line.separator");

      while ((line = input.readLine()) != null) {
        content.append(line).append(sep);
      }
    } catch (IOException ioe) {
      log.error("IO failure : ", ioe);
    } finally {
      FileUtils.closeFile(input);
    }
    return content.toString();
  }

  private static void writeTemplate(final File file, final String content) {
    final Writer output = FileUtils.openFile(file);
    if (output != null) {
      try {
        output.write(content);
      } catch (IOException ioe) {
        log.error("ScriptFrame.writeFile : io failure : ", ioe);
      } finally {
        FileUtils.closeFile(output);
      }
    }
  }
}
