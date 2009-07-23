package org.ivoa.xml.validator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.timer.TimerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import com.sun.tools.xjc.reader.xmlschema.parser.LSInputSAXWrapper;
import java.io.File;
import org.ivoa.util.FileUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;


/**
 * XML Document Validator against an xml schema
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XMLValidator extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** all factories */
  private static ConcurrentHashMap<String, XMLValidator> managedInstances = new ConcurrentHashMap<String, XMLValidator>(4);
  /** XML Resource resolver {@link CatalogResolver} */
  private static CatalogResolver entityResolver = null;

  static {
    try {
      XMLValidator.addCatalog(FileUtils.getFile("D:/WORK/dev/vo-urp-svn/catalog.xml"));
    } catch (IOException ioe) {
      logB.error("XMLValidator.addCatalog : failure : ", ioe);
    }
  }
  //~ Members ----------------------------------------------------------------------------------------------------------
  /** XML Schema instance */
  private Schema schema;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor for a given schema URL
   * @param schemaURL URL for the schema to validate against
   */
  private XMLValidator(final String schemaURL) {
    this.schema = getSchema(schemaURL);
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
   * Retrieve a schema instance from the given URL
   *
   * @param schemaURL schema URI
   *
   * @return schema instance
   *
   * @throws IllegalStateException if the schema can be retrieved or parsed
   */
  public static Schema getSchema(final String schemaURL) {

    // TODO : cache the factory :

    // 1. Lookup a factory for the W3C XML Schema language
    final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    if (entityResolver != null) {
      factory.setResourceResolver(new LSResourceResolver() {

        /**
         *  Allow the application to resolve external resources.
         * <br> The <code>LSParser</code> will call this method before opening any
         * external resource, including the external DTD subset, external
         * entities referenced within the DTD, and external entities referenced
         * within the document element (however, the top-level document entity
         * is not passed to this method). The application may then request that
         * the <code>LSParser</code> resolve the external resource itself, that
         * it use an alternative URI, or that it use an entirely different input
         * source.
         * <br> Application writers can use this method to redirect external
         * system identifiers to secure and/or local URI, to look up public
         * identifiers in a catalogue, or to read an entity from a database or
         * other input source (including, for example, a dialog box).
         * @param type  The type of the resource being resolved. For XML [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>] resources
         *   (i.e. entities), applications must use the value
         *   <code>"http://www.w3.org/TR/REC-xml"</code>. For XML Schema [<a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML Schema Part 1</a>]
         *   , applications must use the value
         *   <code>"http://www.w3.org/2001/XMLSchema"</code>. Other types of
         *   resources are outside the scope of this specification and therefore
         *   should recommend an absolute URI in order to use this method.
         * @param namespaceURI  The namespace of the resource being resolved,
         *   e.g. the target namespace of the XML Schema [<a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML Schema Part 1</a>]
         *    when resolving XML Schema resources.
         * @param publicId  The public identifier of the external entity being
         *   referenced, or <code>null</code> if no public identifier was
         *   supplied or if the resource is not an entity.
         * @param systemId  The system identifier, a URI reference [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of the
         *   external resource being referenced, or <code>null</code> if no
         *   system identifier was supplied.
         * @param baseURI  The absolute base URI of the resource being parsed, or
         *   <code>null</code> if there is no base URI.
         * @return  A <code>LSInput</code> object describing the new input
         *   source, or <code>null</code> to request that the parser open a
         *   regular URI connection to the resource.
         */
        public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
          if (logB.isWarnEnabled()) {
            logB.warn("LSResourceResolver.resolveResource : type         = " + type);
            logB.warn("LSResourceResolver.resolveResource : namespaceURI = " + namespaceURI);
            logB.warn("LSResourceResolver.resolveResource : publicId     = " + publicId);
            logB.warn("LSResourceResolver.resolveResource : systemId     = " + systemId);
            logB.warn("LSResourceResolver.resolveResource : baseURI      = " + baseURI);
          }

          final InputSource is = entityResolver.resolveEntity(namespaceURI, systemId);
          if (is == null) {
            return null;
          }
          return new LSInputSAXWrapper(is);
        }
      });
    }


    Schema s = null;
    try {
      final URL url = new URL(schemaURL);

      if (logB.isWarnEnabled()) {
        logB.warn("XMLValidator.getSchema : retrieve schema and compile it : " + schemaURL);
      }

      // 2. Compile the schema.
      final long start = System.nanoTime();

      s = factory.newSchema(url);

      TimerFactory.getTimer("XMLValidator.getSchema[" + schemaURL + "]").addMilliSeconds(start, System.nanoTime());

      if (logB.isWarnEnabled()) {
        logB.warn("XMLValidator.getSchema : schema ready : " + s);
      }

    } catch (final SAXException se) {
      throw new IllegalStateException("getSchema : unable to create a Schema for : " + schemaURL, se);
    } catch (final MalformedURLException mue) {
      throw new IllegalStateException("getSchema : unable to create a Schema for : " + schemaURL, mue);
    }
    return s;
  }

  /**
   * Adds a new catalog file.
   * @param catalogFile xml catalog to add
   * @throws IOException 
   */
  public static void addCatalog(final File catalogFile) throws IOException {
    if (catalogFile != null) {
      if (entityResolver == null) {
        final CatalogManager staticCatalogManager = CatalogManager.getStaticManager();
        if (logB.isWarnEnabled()) {
          logB.warn("XMLValidator.addCatalog : staticCatalogManager : " + staticCatalogManager);
        }

        // configuration :
        staticCatalogManager.setPreferPublic(false);
        staticCatalogManager.setUseStaticCatalog(true);
        staticCatalogManager.setRelativeCatalogs(true);

        staticCatalogManager.debug.setDebug(999);


        staticCatalogManager.setIgnoreMissingProperties(true);

        entityResolver = new CatalogResolver(true);
      }
/*
      if (logB.isWarnEnabled()) {
        logB.warn("XMLValidator.addCatalog : parsing catalog : " + catalogFile);
      }
      entityResolver.getCatalog().parseCatalog(catalogFile.getPath());
 */
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

