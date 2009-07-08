package org.ivoa.util;

/**
 * This class has utility methods to write System logs. <br/>
 * This implementation uses System.out or System.err streams
 * 
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class SystemLogUtil {

  /** log line separator */
  public final static String LOG_LINE_SEPARATOR = "-------------------------------------------------------------------------------";

  /** debug mode */
  public static boolean DEBUG_MODE = true;

  /** flag to disable the std out */
  public static boolean STD_OUT_ALLOWED = true;

  /** flag to disable the std err */
  public static boolean STD_ERR_ALLOWED = true;

  /**
   * Forbidden constructor
   */
  private SystemLogUtil() {
    /* no-op */
  }

  /**
   * Return true if the debug mode is enabled
   * 
   * @return true if the debug mode is enabled
   */
  public static final boolean isDebugEnabled() {
    return DEBUG_MODE;
  }

  /**
   * <p>
   * Print the message for debugging purposes with a full stack trace in the Standard Error (std
   * err)
   * </p>
   * 
   * @param message message to print
   */
  public static final void debug(final String message) {
    if (STD_ERR_ALLOWED) {
      error(message, new Throwable());
    }
  }

  /**
   * Return true if the std out redirection is enabled
   * 
   * @return true if the std out redirection is enabled
   */
  public static final boolean isInfoEnabled() {
    return STD_OUT_ALLOWED;
  }

  /**
   * <p>
   * Print the message in the Standard Output (std out) with line separators
   * </p>
   * 
   * @param message message to print
   */
  public static final void info(final String message) {
    if (STD_OUT_ALLOWED) {
      System.out.println("-------------------------------------------------------------------------------");
      System.out.println(message);
      System.out.println("-------------------------------------------------------------------------------");
    }
  }

  /**
   * Return true if the std out redirection is enabled
   * 
   * @return true if the std out redirection is enabled
   */
  public static final boolean isWarnEnabled() {
    return STD_OUT_ALLOWED;
  }

  /**
   * <p>
   * Print the message in the Standard Output (std out)
   * </p>
   * 
   * @param message message to print
   */
  public static final void warn(final String message) {
    if (STD_OUT_ALLOWED) {
      System.out.println(message);
    }
  }

  /**
   * Return true if the std err redirection is enabled
   * 
   * @return true if the std err redirection is enabled
   */
  public static final boolean isErrorEnabled() {
    return STD_ERR_ALLOWED;
  }

  /**
   * <p>
   * Print the error message in the Standard Error (std err)
   * </p>
   * 
   * @param message message to print
   */
  public static final void error(final String message) {
    if (STD_ERR_ALLOWED) {
      System.err.println(message);
    }
  }

  /**
   * <p>
   * Print the error message in the Standard Error (std err)
   * </p>
   * 
   * @param message message to print
   * @param th exception to print
   */
  public static final void error(final String message, final Throwable th) {
    if (STD_ERR_ALLOWED) {
      System.err.println(message);
      th.printStackTrace(System.err);
    }
  }

}
