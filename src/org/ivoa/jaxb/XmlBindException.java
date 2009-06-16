/*
 * XmlBindException.java
 * 
 * Author lemson
 * Created on Jun 15, 2009
 */
package org.ivoa.jaxb;

import javax.xml.bind.JAXBException;

import org.ivoa.xml.validator.ValidationResult;

public class XmlBindException extends RuntimeException {

  /** In case this exception is thrown because of failing validation, this object holds on to the messages. */
  private ValidationResult validationResult;
  public XmlBindException(JAXBException je)
  {
    super(je);
  }
  public XmlBindException(String message, JAXBException je)
  {
    super(message, je);
  }
  public XmlBindException(String message, ValidationResult validationResult)
  {
    super(message);
    this.validationResult = validationResult;
  }
  public ValidationResult getValidationResult() {
    return validationResult;
  }
}
