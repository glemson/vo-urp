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

public class MyErrorHandler implements ErrorHandler {

  public int errorCount = 0;
  public int warningCount = 0;
    public void warning(SAXParseException ex) {
      warningCount++;
        System.err.println("WARNING "+ex.getLineNumber()+":"+ex.getColumnNumber()+" ==> "+ex.getMessage());
    }

    public void error(SAXParseException ex) {
      errorCount++;
        System.err.println("ERROR "+ex.getLineNumber()+":"+ex.getColumnNumber()+" ==> "+ex.getMessage());
    }

    public void fatalError(SAXParseException ex) throws SAXException {
      errorCount++;
      System.err.println("FATALERROR "+ex.getLineNumber()+":"+ex.getColumnNumber()+" ==> "+ex.getMessage());
    }

}