package org.ivoa.xml;

import java.io.Serializable;


/**
 * Xml validation error message
 *
 * @author laurent bourges (voparis)
 */
public final class ErrorMessage implements Serializable {

  /**
   * serial UID for Serializable interface : every concrete class must have its value corresponding to last
   * modification date of the UML model
   */
  private static final long serialVersionUID = 1L;
  private SEVERITY severity;
  private int lineNumber;
  private int columnNumber;
  private String message;

  /**
   * Constructor
   * @param severity severity of the validation error
   * @param lineNumber line number in the input document
   * @param columnNumber column number in the input document
   * @param message error message
   */
  public ErrorMessage(final SEVERITY severity, final int lineNumber, final int columnNumber, final String message) {
    this.severity = severity;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.message = message;
  }

  @Override
  public String toString() {
    return this.severity + " [" + this.lineNumber + " : " + this.columnNumber + "] " + this.message;
  }

  public int getColumnNumber() {
    return columnNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public String getMessage() {
    return message;
  }

  public SEVERITY getSeverity() {
    return severity;
  }


  public enum SEVERITY {

    WARNING,
    ERROR,
    FATAL
  }


}
