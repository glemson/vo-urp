package org.ivoa.xml;

import java.io.Serializable;


/**
 * Xml validation error message
 *
 * @author laurent bourges (voparis)
 */
public final class ErrorMessage implements Serializable {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface : every concrete class must have its value corresponding to last
   * modification date of the UML model
   */
  private static final long serialVersionUID = 1L;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private SEVERITY severity;
  /**
   * TODO : Field Description
   */
  private int lineNumber;
  /**
   * TODO : Field Description
   */
  private int columnNumber;
  /**
   * TODO : Field Description
   */
  private String message;

  //~ Constructors -----------------------------------------------------------------------------------------------------

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

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @Override
  public String toString() {
    return this.severity + " [" + this.lineNumber + " : " + this.columnNumber + "] " + this.message;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getColumnNumber() {
    return columnNumber;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getMessage() {
    return message;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public SEVERITY getSeverity() {
    return severity;
  }

  //~ Enumerations -----------------------------------------------------------------------------------------------------

  public enum SEVERITY {//~ Enumeration constant initializers ------------------------------------------------------------------------------

    WARNING, ERROR, FATAL;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
