package org.ivoa.util.runner.process;



import java.io.IOException;
import java.io.Writer;

import java.util.Iterator;
import org.ivoa.bean.LogSupport;
import org.ivoa.util.FileUtils;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.concurrent.ArrayDeque;
import org.ivoa.util.concurrent.Deque;
import org.ivoa.util.concurrent.FastSemaphore;
import org.ivoa.util.text.LocalStringBuilder;


/**
 * Ring buffer : maintain a limited list of string. 
 * Thread safe on add / getContent methods
 *
 * @author laurent bourges (voparis)
 */
public final class RingBuffer extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** default line size */
  public static final int DEFAULT_LINE_SIZE = 100;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** maximum of lines */
  private final int maxCount;

  /** write logs file name */
  private final String writeLogFile;

  /** line counter */
  private int count;

  /** file writer */
  private Writer fw = null;

  /** list of lines */
  private final Deque<String> anchor;

  /** anchor semaphore */
  private final FastSemaphore semAnchor = new FastSemaphore(1, true); // LBO : test fairness

  /** buffer semaphore */
  private final FastSemaphore semBuffer = new FastSemaphore(1);

  /** line semaphore */
  private final FastSemaphore semLine = new FastSemaphore(1);

  /** internal buffer to get content */
  private StringBuilder buffer = null;

  /** internal buffer to concat prefix & line */
  private StringBuilder lineBuffer = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param max number of lines
   * @param writeLogFile file name for std out / err traces (null implies no file written)
   */
  public RingBuffer(final int max, final String writeLogFile) {
    this.maxCount = max;
    this.count = 0;
    this.anchor = new ArrayDeque<String>(this.maxCount);
    this.writeLogFile = writeLogFile;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Allocates temporary line & output buffers & file writer
   */
  public void prepare() {
    this.buffer = new StringBuilder(this.maxCount * DEFAULT_LINE_SIZE / 2);
    this.lineBuffer = new StringBuilder(DEFAULT_LINE_SIZE);

    if (!JavaUtils.isEmpty(this.writeLogFile)) {
      this.fw = FileUtils.openFile(this.writeLogFile);
    }
  }

  /**
   * Clear temporary line & output buffers & file writer
   */
  public void close() {
    this.buffer = null;
    this.lineBuffer = null;
    this.fw = FileUtils.closeFile(this.fw);
  }

  /**
   * add a line in the buffer like tail. Concatenates prefix and line before adding the new line
   *
   * @param prefix starting line prefix
   * @param line content to add in buffer
   */
  public final void add(final String prefix, final String line) {
    String              res = null;

    final StringBuilder sb  = this.lineBuffer;

    if (sb != null) {
      // thread safe protection for line buffer :
      // maybe multiple calls to this method :
      try {
        this.semLine.acquire();
        // work on lineBuffer :
        sb.append(prefix).append(" : ").append(line);
        res = LocalStringBuilder.extract(sb);
        // finished with lineBuffer
      } catch (final InterruptedException ie) {
        log.error("RingBuffer : line semaphore interrupted : ", ie);
        res = line;
      } finally {
        this.semLine.release();
      }
    } else {
      // uses a new buffer :
      res = prefix + " : " + line;
    }

    add(res);
  }

  /**
   * add a line in the buffer like tail
   *
   * @param line content to add in buffer
   *
   * @return this ring buffer
   */
  public final RingBuffer add(final String line) {
    // thread safe protection for list & count :
    // maybe multiple calls to this method :
    try {
      this.semAnchor.acquire();

      // first, write the line into file writer :
      writeLine(line);

      // work on anchor & count :
      while (count >= maxCount) {
        anchor.pollFirst();
        count--;
      }

      anchor.offerLast(line);
      count++;
      // finished with anchor & count
    } catch (final InterruptedException ie) {
      log.error("RingBuffer : anchor semaphore interrupted : ", ie);
    } finally {
      this.semAnchor.release();
    }

    return this;
  }

  /**
   * Returns buffer content like tail with CR separator
   *
   * @return buffer content
   */
  public final String getContent() {
    return getContent(null);
  }

  /**
   * Returns buffer content like tail with CR separator
   *
   * @param startLine optional begin of content string
   *
   * @return buffer content
   */
  public final String getContent(final String startLine) {
    return getContent(startLine, "\n");
  }

  /**
   * Returns buffer content like tail
   *
   * @param startLine optional begin of content string
   * @param lineSep line separator
   *
   * @return buffer content
   */
  public final String getContent(final String startLine, final String lineSep) {
    String              res = null;

    final StringBuilder sb  = this.buffer;

    if (sb != null) {
      // thread safe protection for buffer :
      // maybe multiple calls to this method :
      try {
        this.semBuffer.acquire();

        // work on buffer :
        if (startLine != null) {
          sb.append(startLine).append(lineSep);
        }

        // thread safe protection for list :
        try {
          this.semAnchor.acquire();

          // work on anchor :
          for (final Iterator<String> it = anchor.iterator(); it.hasNext();) {
            sb.append(it.next()).append(lineSep);
          }

          // finished with anchor
        } catch (final InterruptedException ie) {
          log.error("RingBuffer : anchor semaphore interrupted : ", ie);
        } finally {
          this.semAnchor.release();
        }

        res = sb.toString();
      } catch (final InterruptedException ie) {
        log.error("RingBuffer : buffer semaphore interrupted : ", ie);
      } finally {
        buffer.setLength(0);
        // finished with buffer
        this.semBuffer.release();
      }
    } else {
      // Job was not started so Ring Buffer is undefined ...
      res = "";
    }

    return res;
  }

  /**
   * Adds line into log file
   *
   * @param line content to add
   */
  private void writeLine(final String line) {
    if (this.fw != null) {
      try {
        fw.write(line);
        fw.write("\n");
      } catch (final IOException ioe) {
        log.error("RingBuffer : write line failure : ", ioe);
      }
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
