package org.ivoa.http;

/**
 * <br> Description : Exception in communication process. It wraps exceptions such as MalformedURLException... <br>
 */
public class CommunicationException extends StatusException {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**  */
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
