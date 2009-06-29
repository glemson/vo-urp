package org.ivoa.xml.validator;

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
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XMLValidator {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** XML Schema instance */
  private Schema schema;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor for a given schema URL
   * @param schemaURL URL for the schema to validate against
   */
  public XMLValidator(final String schemaURL) {
    this.schema = getSchema(schemaURL);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Retrieve a schema instance from the given URL
   *
   * @param schemaURL schema URI
   *
   * @return schema instance
   *
   * @throws IllegalStateException if the schema can be retrieved or parsed
   */
  public static Schema getSchema(final String schemaURL) {
    // 1. Lookup a factory for the W3C XML Schema language
    final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

    try {
      final URL url = new URL(schemaURL);

      // 2. Compile the schema.
      return factory.newSchema(url);
    } catch (final SAXException se) {
      throw new IllegalStateException("getSchema : unable to create a Schema for : " + schemaURL, se);
    } catch (final MalformedURLException mue) {
      throw new IllegalStateException("getSchema : unable to create a Schema for : " + schemaURL, mue);
    }
  }

  /**
   * Validate the given XML stream according to the schema
   *
   * @param document XML stream
   *
   * @return ValidationResult instance
   */
  public ValidationResult validate(final InputStream document) {
    return validate(document, new ValidationResult());
  }

  /**
   * Validate the given XML stream according to the schema
   *
   * @param document XML stream
   * @param result ValidationResult instance
   *
   * @return ValidationResult instance
   */
  public ValidationResult validate(final InputStream document, final ValidationResult result) {
    // 3. Get a validator from the schema.
    final Validator validator = this.schema.newValidator();

    validator.setErrorHandler(new CustomErrorHandler(result));

    // 4. Parse the document you want to check.
    final Source source = new StreamSource(document);

    try {
      // 5. Check the document
      validator.validate(source);
    } catch (final SAXException se) {
      // intercepted by CustomErrorHandler
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, -1, -1, se.getMessage()));
    } catch (final IOException ioe) {
      // intercepted by CustomErrorHandler
      result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, -1, -1, ioe.getMessage()));
    }

    if (result.getMessages().isEmpty()) {
      result.setValid(true);
    }

    return result;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * SAX ErrorHandler implementation to add validation exception to the given ValidationResult instance
   * @see org.xml.sax.ErrorHandler
   */
  private final class CustomErrorHandler implements ErrorHandler {
    //~ Members --------------------------------------------------------------------------------------------------------

    /** validation result */
    private ValidationResult result;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    /**
     * Public constructor with the given validation result
     * @param pResult validation result to use
     */
    public CustomErrorHandler(final ValidationResult pResult) {
      this.result = pResult;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Wrap the SAX exception in an ErrorMessage instance added to the validation result
     * @param se SAX parse exception 
     * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
     */
    public void warning(final SAXParseException se) {
      result.getMessages().add(
        new ErrorMessage(ErrorMessage.SEVERITY.WARNING, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
    }

    /**
     * Wrap the SAX exception in an ErrorMessage instance added to the validation result
     * @param se SAX parse exception 
     * @see org.xml.sax.ErrorHandler#error(SAXParseException)
     */
    public void error(final SAXParseException se) {
      result.getMessages().add(
        new ErrorMessage(ErrorMessage.SEVERITY.ERROR, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
    }

    /**
     * Wrap the SAX exception in an ErrorMessage instance added to the validation result
     * @param se SAX parse exception 
     * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
     */
    public void fatalError(final SAXParseException se) {
      result.getMessages().add(
        new ErrorMessage(ErrorMessage.SEVERITY.FATAL, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
