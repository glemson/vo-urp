/*
 * XMLSXSDTester.java
 *
 * Author lemson
 * Created on Apr 30, 2008
 */
package org.ivoa.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.*;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public class XMLSXSDTester {
  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param args 
   *
   * @throws SAXException 
   * @throws IOException 
   */
  public static void main(final String[] args) throws SAXException, IOException {
    if ((args.length < 1) || (args.length > 2)) {
      System.out.println("usage: XMLXSDTester xml-doc [xsd-doc]");

      return;
    }

    // 1. Lookup a factory for the W3C XML Schema language
    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

    // 2. Compile the schema. 
    // Here the schema is loaded from a java.io.File, but you could use 
    // a java.net.URL or a javax.xml.transform.Source instead.
    Schema schema;

    if (args.length == 1) {
      schema = factory.newSchema(
        new URL("http://volute.googlecode.com/svn/trunk/projects/theory/snapdm/input/intermediateModel.xsd"));
    } else {
      schema = factory.newSchema(new File(args[1]));
    }

    // 3. Get a validator from the schema.
    Validator      validator = schema.newValidator();
    MyErrorHandler lenient   = new MyErrorHandler();

    validator.setErrorHandler(lenient);

    // 4. Parse the document you want to check.
    String xmlFile = args[0];
    Source source  = new StreamSource(xmlFile);

    // 5. Check the document
    try {
      validator.validate(source);

      if (lenient.errorCount == 0) {
        System.out.println(xmlFile + " is valid.");
      } else {
        System.out.println(xmlFile + " is not valid.");
      }
    } catch (final SAXException ex) {
      System.out.println(xmlFile + " is not valid because ");
      System.out.println(ex.getMessage());
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
