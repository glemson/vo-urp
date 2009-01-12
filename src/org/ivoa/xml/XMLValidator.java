package org.ivoa.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML Document Validator against an xml schema
 *
 * @author laurent bourges (voparis)
 */
public class XMLValidator {

  // members :
  private Schema schema;

  /**
   * Constructor for a given schema URL
   * @param schemaURL URL for the schema to validate against
   */
  public XMLValidator(final String schemaURL) {
    this.schema = getSchema(schemaURL);
  }
  
  public static Schema getSchema(final String schemaURL) {
    // 1. Lookup a factory for the W3C XML Schema language
    final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    try {
      final URL url = new URL(schemaURL);
      // 2. Compile the schema.
      return factory.newSchema(url);
    } catch (SAXException se) {
      throw new IllegalStateException("getSchema : unable to create a Schema for : "+schemaURL, se);
    } catch (MalformedURLException mue) {
      throw new IllegalStateException("getSchema : unable to create a Schema for : "+schemaURL, mue);
    }
  }

  public ValidationResult validate(final InputStream document) {
    return validate(document, new ValidationResult());
  }
  
  public ValidationResult validate(final InputStream document, final ValidationResult result) {
    
    // 3. Get a validator from the schema.
    final Validator validator = this.schema.newValidator();
    validator.setErrorHandler(new CustomErrorHandler(result));

    // 4. Parse the document you want to check.
    final Source source = new StreamSource(document);
    try {
      // 5. Check the document
      validator.validate(source);
    } catch (SAXException se) {
      // intercepted by CustomErrorHandler
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, -1, -1, se.getMessage()));
    } catch (IOException ioe) {
      // intercepted by CustomErrorHandler
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, -1, -1, ioe.getMessage()));
    }

    if (result.getMessages().isEmpty()) {
      result.setValid(true);
    }
    return result;
}

  private final class CustomErrorHandler implements ErrorHandler {
    
    private ValidationResult result;
    
    public CustomErrorHandler(final ValidationResult result) {
      this.result = result;
    }

    public void warning(final SAXParseException se) {
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.WARNING, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
    }

    public void error(final SAXParseException se) {
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.ERROR, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
    }

    public void fatalError(final SAXParseException se) {
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
    }

  }
}
