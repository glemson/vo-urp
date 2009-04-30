package org.ivoa.xml;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple class to contain XML validation status and messages
 *
 * @author laurent bourges (voparis)
 */
public class ValidationResult {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private boolean            valid    = false;
  /**
   * TODO : Field Description
   */
  private List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  public ValidationResult() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public List<ErrorMessage> getMessages() {
    return messages;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isValid() {
    return valid;
  }

  /**
   * TODO : Method Description
   *
   * @param valid 
   */
  protected void setValid(final boolean valid) {
    this.valid = valid;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
