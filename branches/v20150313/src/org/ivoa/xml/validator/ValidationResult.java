package org.ivoa.xml.validator;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple class to contain XML validation status and messages
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class ValidationResult {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private boolean valid = false;
  /** TODO : Field Description */
  private List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  public ValidationResult() {
    /* no-op */
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
   * @param pValid valid
   */
  protected void setValid(final boolean pValid) {
    this.valid = pValid;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
