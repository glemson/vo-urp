package org.ivoa.http;

/**
 * ADOC_COMME
 */
public class CommunicationException extends StatusException {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** ADOC_COMME */
  private static final long serialVersionUID = 1L;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new CommunicationException object.
   *
   * @param statusCode
   * @param message
   * @param rootCause
   */
  public CommunicationException(final String statusCode, final String message, final Exception rootCause) {
    super(statusCode, message, rootCause);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
