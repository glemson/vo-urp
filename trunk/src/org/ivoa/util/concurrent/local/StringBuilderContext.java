package org.ivoa.util.concurrent.local;

import org.ivoa.bean.LogSupport;

/**
 * This private class defines a Context iso an array of StringBuilder instances to allow
 * reentrance
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class StringBuilderContext extends LogSupport {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /** diagnostic flag */
  public final static boolean DIAGNOSTICS = false;
  /** max reentrance depth = 4 */
  public final static int DEPTH = 4;
  /** initial buffer capacity = 384 chars */
  public final static int CAPACITY = 384;
  /** max buffer capacity = 4096 chars */
  public final static int MAX_CAPACITY = 4096;
  /** Undefined value for current Position */
  public final static int UNDEFINED = -1;
  /** first Position to use the specific first Buffer */
  public final static int FIRST = 0;

  // ~ Members
  // ---------------------------------------------------------------------------------------------------
  /** currently used buffer in the array of StringBuilder */
  private int current = UNDEFINED;
  /** first StringBuilder instance (optimized reference) */
  private StringBuilder firstBuffer = null;
  /** internal array of StringBuilder instances for reentrance */
  private final StringBuilder[] buffers = new StringBuilder[DEPTH];

  // ~ Constructors
  // ---------------------------------------------------------------------------------------------------
  /**
   * Private Constructor
   */
  protected StringBuilderContext() {
    if (DIAGNOSTICS && logD.isInfoEnabled()) {
      logD.info("LocalStringBuilder.Context.new : " + Thread.currentThread().getName());
    }
  }

  /**
   * Create a new StringBuilder with the default capacity
   * @return new StringBuilder with the default capacity
   */
  public final static StringBuilder createStringBuilder() {
    return new StringBuilder(CAPACITY);
  }

  /**
   * Reset the length of the given StringBuilder
   * @param sb buffer to reset
   */
  public final static void resetStringBuilder(final StringBuilder sb) {
    if (sb != null) {
      // reset without array operation : just set count to 0 / leave buffer available with the
      // same capacity :
      sb.setLength(0);
    }
  }

  /**
   * Return an empty buffer in the stack or create a new one if the stack capacity is exceeded
   *
   * @return empty buffer
   */
  public final StringBuilder acquire() {
    StringBuilder sb = null;

    current++;

    if (current == FIRST) {
      sb = firstBuffer;

      if (sb == null) {
        sb = createStringBuilder();
      }

      firstBuffer = sb;
    } else {
      sb = acquireInBuffers();
    }
    return sb;
  }

  /**
   * Return a buffer from the buffer array (slower mode)
   * @return empty buffer
   */
  private final StringBuilder acquireInBuffers() {
    StringBuilder sb = null;

    final int pos = current - 1;
    final boolean useCache = pos < DEPTH;

    if (useCache) {
      sb = buffers[pos];
    } else {
      if (logD.isWarnEnabled()) {
        log.warn("LocalStringBuilder.Context.acquire : create a new StringBuilder : consider to increment LocalStringBuilder.DEPTH = " + current + " / " + DEPTH);
      }
    }

    if (sb == null) {
      sb = createStringBuilder();
    }
    if (useCache) {
      buffers[pos] = sb;
    }
    return sb;
  }

  /**
   * Return the current buffer in the stack or null if the stack capacity is exceeded
   *
   * @return buffer (not empty) or null
   */
  public final StringBuilder current() {
    StringBuilder sb = null;

    if (current != UNDEFINED) {
      if (current == FIRST) {
        sb = firstBuffer;
      } else {
        final int pos = current - 1;
        if (pos < DEPTH) {
          sb = buffers[pos];
        }
      }
    }

    return sb;
  }

  /**
   * Release the current buffer in the stack and reset length of the given buffer
   *
   * @param sb buffer (optional)
   */
  public final void release(final StringBuilder sb) {
    if (sb != null) {
      if (DIAGNOSTICS && logD.isInfoEnabled()) {
        logD.info("LocalStringBuilder.Context.release : used buffer capacity : " + sb.length() + " / " + sb.capacity());
      }
      // reset buffer content :
      resetStringBuilder(sb);

      if (sb.capacity() > MAX_CAPACITY) {
        if (DIAGNOSTICS && logD.isInfoEnabled()) {
          logD.info("LocalStringBuilder.Context.release : reuse buffer capacity exceeded : " + sb.capacity() + " / " + MAX_CAPACITY);
        }
        // release it :

        if (current == FIRST) {
          firstBuffer = null;
        } else {
          final int pos = current - 1;
          if (pos < DEPTH) {
            buffers[pos] = null;
          }
        }
      }
    }
    current--;
  }
}