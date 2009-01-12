package org.ivoa.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to contain XML validation status and messages
 *
 * @author laurent bourges (voparis)
 */
public class ValidationResult {
  
  private boolean valid = false;
  private List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

  /**
   * Constructor
   */
  public ValidationResult() {
  }

  public List<ErrorMessage> getMessages() {
    return messages;
  }

  public boolean isValid() {
    return valid;
  }

  protected void setValid(boolean valid) {
    this.valid = valid;
  }
  
  
}
