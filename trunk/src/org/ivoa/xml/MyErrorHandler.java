/*
 * ErrorHandler.java
 *
 * Author lemson
 * Created on Apr 30, 2008
 */
package org.ivoa.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public class MyErrorHandler implements ErrorHandler {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public int errorCount   = 0;
  /**
   * TODO : Field Description
   */
  public int warningCount = 0;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param ex 
   */
  public void warning(final SAXParseException ex) {
    warningCount++;
    System.err.println("WARNING " + ex.getLineNumber() + ":" + ex.getColumnNumber() + " ==> " + ex.getMessage());
  }

  /**
   * TODO : Method Description
   *
   * @param ex 
   */
  public void error(final SAXParseException ex) {
    errorCount++;
    System.err.println("ERROR " + ex.getLineNumber() + ":" + ex.getColumnNumber() + " ==> " + ex.getMessage());
  }

  /**
   * TODO : Method Description
   *
   * @param ex 
   *
   * @throws SAXException 
   */
  public void fatalError(final SAXParseException ex) throws SAXException {
    errorCount++;
    System.err.println("FATALERROR " + ex.getLineNumber() + ":" + ex.getColumnNumber() + " ==> " + ex.getMessage());
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
