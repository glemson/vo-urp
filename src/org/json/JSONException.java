package org.json;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 *
 * @author JSON.org
 * @version 2008-09-18
 */
public class JSONException extends Exception {
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
  private Throwable cause;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
     * Constructs a JSONException with an explanatory message.
     * @param message Detail about the reason for the exception.
     */
  public JSONException(final String message) {
    super(message);
  }

  /**
   * Creates a new JSONException object
   *
   * @param t 
   */
  public JSONException(final Throwable t) {
    super(t.getMessage());
    this.cause = t;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  @Override
  public Throwable getCause() {
    return this.cause;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
