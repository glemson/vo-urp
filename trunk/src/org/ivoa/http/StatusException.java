package org.ivoa.http;

/**
 * Description : Parent of all general exceptions.
 */
public class StatusException extends Exception {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**  */
  private static final long serialVersionUID = 1L;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private String statusCode = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new ConnectorException object.
   *
   * @param code
   * @param message
   */
  public StatusException(final String code, final String message) {
    super(message);
    this.statusCode = code;
  }

/**
   * Creates a new ConnectorException object.
   *
   * @param code
   * @param message
   * @param rootCause
   */
  public StatusException(final String code, final String message, final Throwable rootCause) {
    super(message, rootCause);
    this.statusCode = code;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Gives the status code to retrieve a translated message to display
   *
   * @return the status code
   */
  public String getStatusCode() {
    return statusCode;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
