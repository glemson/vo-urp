package org.ivoa.xml;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import com.sun.tools.xjc.reader.xmlschema.parser.LSInputSAXWrapper;
import java.io.File;
import java.io.IOException;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.CollectionUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;


/**
 * Custom XSD Resolver to support local xml catalogs
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class CustomXmlCatalogResolver extends SingletonSupport implements LSResourceResolver {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** XML Resource resolver {@link CatalogResolver} */
  protected static CatalogResolver entityResolver = null;
  /** singleton instance (java 5 memory model) */
  private static CustomXmlCatalogResolver instance = null;

  /**
   * Return the CustomXmlCatalogResolver singleton instance
   *
   * @return CustomXmlCatalogResolver singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final CustomXmlCatalogResolver getInstance() {
    if (instance == null) {
      instance = prepareInstance(new CustomXmlCatalogResolver());
    }
    return instance;
  }

  /**
   * Concrete implementations of the SingletonSupport's initialize() method :<br/>
   * Callback to initialize this SingletonSupport instance<br/>
   *
   * Loads the configuration file found in the system classpath (removes any empty property
   * value)<br/>
   *
   * Use the Factory Pattern (introspection used to get a newInstance of the concrete PropertyHolder reference)
   *
   * @see SingletonSupport#initialize()
   *
   * @throws IllegalStateException if a problem occurred
   */
  @Override
  protected final void initialize() throws IllegalStateException {
    if (entityResolver == null) {
      final CatalogManager staticCatalogManager = CatalogManager.getStaticManager();
      staticCatalogManager.setIgnoreMissingProperties(false);

        // configuration :
        staticCatalogManager.setPreferPublic(false);
        staticCatalogManager.setUseStaticCatalog(true);
        staticCatalogManager.setRelativeCatalogs(true);
        staticCatalogManager.setVerbosity(999);

      if (logB.isWarnEnabled()) {
        logB.warn("CustomXmlCatalogResolver.initialize : catalog Manager : " + staticCatalogManager);
        logB.warn("CustomXmlCatalogResolver.initialize : catalog Files : " + CollectionUtils.toString(staticCatalogManager.getCatalogFiles()));
      }
      entityResolver = new CatalogResolver(true);
    }
  }

 /**
   * Adds a new catalog file.
   * @param catalogFile xml catalog to add
   * @throws IOException
   */
  public static void addCatalog(final File catalogFile) throws IOException {
    if (catalogFile != null) {
      if (logB.isWarnEnabled()) {
      logB.warn("XMLValidator.addCatalog : parsing catalog : " + catalogFile);
      }
      entityResolver.getCatalog().parseCatalog(catalogFile.getPath());
    }
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance
   * iso clear static references
   *
   * @see org.ivoa.bean.SingletonSupport#clearStaticReferences()
   */
  @Override
  protected void clearStaticReferences() {
    // force GC :
    entityResolver = null;
    if (instance != null) {
      instance = null;
    }
  }

  /**
   * Protected constructor to avoid to create instance except for singletons (stateless classes)
   */
  protected CustomXmlCatalogResolver() {
    super();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
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
      logB.warn("CustomXmlCatalogResolver.resolveResource : enter    = " + namespaceURI);
      logB.warn("CustomXmlCatalogResolver.resolveResource : baseURI  = " + baseURI);
      if (systemId != null) {
        logB.warn("CustomXmlCatalogResolver.resolveResource : systemId = " + systemId);
      }
      if (publicId != null) {
        logB.warn("CustomXmlCatalogResolver.resolveResource : publicId = " + publicId);
      }
    }

    LSInput res = null;

    final InputSource is = entityResolver.resolveEntity(namespaceURI, systemId);
    if (is == null) {
      if (logB.isWarnEnabled()) {
        logB.warn("CustomXmlCatalogResolver.resolveResource : exit     = systemId not found : " + systemId);
      }
    } else {
      if (logB.isWarnEnabled()) {
        logB.warn("CustomXmlCatalogResolver.resolveResource : exit     = " + is.getSystemId());
      }
      res = new LSInputSAXWrapper(is);
    }

    return res;
  }
}
