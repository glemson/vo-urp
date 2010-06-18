package org.ivoa.xml.validator;

import java.io.IOException;
import java.io.InputStream;

import java.util.concurrent.ConcurrentHashMap;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.xml.CustomXmlCatalogResolver;
import org.ivoa.xml.XmlFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * XML Document Validator against an xml schema
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XMLValidator extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** all factories */
  private static ConcurrentHashMap<String, XMLValidator> managedInstances = new ConcurrentHashMap<String, XMLValidator>(4);
  
  //~ Members ----------------------------------------------------------------------------------------------------------
  /** XML Schema instance */
  private Schema schema;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor for a given schema URL
   * @param schemaURL URL for the schema to validate against
   */
  private XMLValidator(final String schemaURL) {
    this.schema = XmlFactory.getSchema(schemaURL);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Factory singleton per schema URL pattern
   *
   * @param schemaURL URL for the schema to validate against
   *
   * @return XMLValidator initialized
   */
  public static final XMLValidator getInstance(final String schemaURL) {
    XMLValidator v = managedInstances.get(schemaURL);

    if (v == null) {
      if (logB.isInfoEnabled()) {
        logB.info("XMLValidator.getInstance : creating new instance for : " + schemaURL);
      }

      v = prepareInstance(new XMLValidator(schemaURL));

      if (v != null) {
        managedInstances.putIfAbsent(schemaURL, v);
        // to be sure to return the singleton :
        v = managedInstances.get(schemaURL);
      }
    }

    return v;
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance
   * iso clear static references
   *
   * @see SingletonSupport#clearStaticReferences()
   */
  @Override
  protected void clearStaticReferences() {
    if (logB.isInfoEnabled()) {
      logB.info("XMLValidator.clearStaticReferences : enter");
    }
    // reset managed instances :
    if (managedInstances != null) {
      managedInstances.clear();
      managedInstances = null;
    }
    if (logB.isInfoEnabled()) {
      logB.info("XMLValidator.clearStaticReferences : exit");
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

