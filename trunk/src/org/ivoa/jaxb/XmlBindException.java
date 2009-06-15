/*
 * XmlBindException.java
 * 
 * Author lemson
 * Created on Jun 15, 2009
 */
package org.ivoa.jaxb;

import javax.xml.bind.JAXBException;

public class XmlBindException extends RuntimeException {

  public XmlBindException(JAXBException je)
  {
    super(je);
  }
  public XmlBindException(String message, JAXBException je)
  {
    super(message, je);
  }
}
