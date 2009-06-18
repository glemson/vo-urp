package org.ivoa.jaxb;

import javax.xml.bind.JAXBException;

import org.ivoa.xml.validator.ValidationResult;

/**
 * This class is a RuntimeException wrapping a JAXB Exception or a validation failure 
 * 
 * @author laurent bourges (voparis) / gerard lemson (mpe)
 */
public class XmlBindException extends RuntimeException {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** serial UID for Serializable interface */
  private static final long serialVersionUID = 1L;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * In case this exception is thrown because of failing validation, this object holds on to the
   * messages.
   */
  private ValidationResult validationResult;

  /** 
   * Constructs a new XmlBindException with the specified cause
   *
   * @param je JAXBException 
   */
  public XmlBindException(final JAXBException je) {
    super(je);
  }

  /** 
   * Constructs a new XmlBindException with the specified message and cause
   *
   * @param message the detail message
   * @param je JAXBException 
   */
  public XmlBindException(final String message, final JAXBException je) {
    super(message, je);
  }

  /** 
   * Constructs a new XmlBindException with the specified message and cause
   *
   * @param message the detail message
   * @param pValidationResult validationResult 
   */
  public XmlBindException(final String message, final ValidationResult pValidationResult) {
    super(message);
    this.validationResult = pValidationResult;
  }

  /**
   * Return the optional validationResult
   * @return validationResult
   */
  public final ValidationResult getValidationResult() {
    return validationResult;
  }
}
