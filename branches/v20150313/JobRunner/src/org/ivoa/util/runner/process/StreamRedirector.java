package org.ivoa.util.runner.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.ivoa.util.concurrent.GenericRunnable;
import org.ivoa.util.concurrent.ThreadExecutors;


/**
 * This class implements Runnable to redirect an input stream to a ring buffer
 * 
 * @see ProcessRunner
 * @see RingBuffer
 * 
 * @author laurent bourges (voparis)
 */
public final class StreamRedirector extends GenericRunnable {

  /** debug flag : dump every read line in logs */
  private final static boolean DEBUG = false;
  /** pause flag : waits 10ms after each line read */
  private final static boolean PAUSE = false;

  /** prefix for example : 'ERROR' */
  private final String prefix;
  /** input Stream to redirect to buffer */
  private InputStream is;
  /** ring buffer */
  private final RingBuffer ring;

  /**
   * Constructor with the given ring buffer
   * @param ring buffer to use
   */
  public StreamRedirector(final RingBuffer ring) {
    this(ring, null);
  }

  /**
   * Constructor with the given ring buffer and prefix
   * @param ring buffer to use
   * @param prefix line prefix to use
   */
  public StreamRedirector(final RingBuffer ring, final String prefix) {
    this.ring = ring;
    this.prefix = prefix;
  }

  /**
   * Defines the inputStream to read
   * @param in stream to read
   */
  public void setInputStream(final InputStream in) {
    this.is = in;
  }

  /**
   * The method reads lines from a buffered reader for the inputStream and adds them to the ring buffer as long as the inputStream is ready.
   * The input stream is not closed by this method. 
   */
  public void run() {
    if (log.isDebugEnabled()) {
      log.debug("StreamRedirector - thread.run : enter");
    }

    if (this.is != null) {
      try {
        // 8K buffer :
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        for (String line = null; (line = br.readLine()) != null;) {

          if (DEBUG) {
            log.error(line);
            if (PAUSE) {
              // pause thread to slow down the job :
              ThreadExecutors.sleep(10l);
            }
          }

          if (prefix != null) {
            ring.add(prefix, line);
          } else {
            ring.add(line);
          }
        }
      } catch (IOException ioe) {
        // occurs when process is killed (buffer.readLine() says 'Stream closed') :
        if (log.isDebugEnabled()) {
          log.debug("StreamRedirector.run : io failure : ", ioe);
        }
      }
    } else {
      log.error("StreamRedirector.run : undefined input stream !");
    }
    if (log.isDebugEnabled()) {
      log.debug("StreamRedirector - thread.run : exit");
    }
  }
}
