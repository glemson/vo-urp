package org.ivoa.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.util.FileUtils;
import org.ivoa.util.text.StringBuilderWriter;
import org.ivoa.util.timer.TimerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Utility class for XML parsing & transforming (XSLT)  <br><b>Supports XML document & XSLT caches</b>
 *
 * <p>
 * Unused : 08/06/2009 but may be useful for dev tests.
 * </p>
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XmlFactory extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** encoding used for XML and XSL documents */
  public static final String ENCODING = "UTF-8";
  /** default buffer size (4 ko) for XSLT result document */
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  /** inner DOM factory */
  private static DocumentBuilderFactory documentFactory = null;
  /** inner XSLT factory */
  private static TransformerFactory transformerFactory = null;
  /** inner Schema factory */
  private static SchemaFactory schemaFactory = null;
  /** cache for Dom instances */
  private static Map<String, Document> cacheDOM = new HashMap<String, Document>(32);
  /** cache for Xsl templates */
  private static Map<String, Templates> cacheXSL = new HashMap<String, Templates>(32);

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Prepare the XmlFactory singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final void prepareInstance() {
    prepareInstance(new XmlFactory());
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance iso
   * clear static references
   *
   * @see SingletonSupport#clearStaticReferences()
   */
  @Override
  protected void clearStaticReferences() {
    // free static fields :
    documentFactory = null;
    transformerFactory = null;
    schemaFactory = null;

    cacheDOM.clear();
    cacheDOM = null;

    cacheXSL.clear();
    cacheXSL = null;
  }

  /**
   * Creates a new XmlFactory object
   */
  private XmlFactory() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Returns a DocumentBuilderFactory (JAXP)
   *
   * @return DocumentBuilderFactory (JAXP)
   */
  public static final DocumentBuilderFactory getFactory() {
    if (documentFactory == null) {
      documentFactory = DocumentBuilderFactory.newInstance();
    }

    return documentFactory;
  }

  /**
   * Returns a TransformerFactory (JAXP)
   *
   * @return TransformerFactory (JAXP)
   */
  protected static final TransformerFactory getTransformerFactory() {
    if (transformerFactory == null) {
      try {
        transformerFactory = TransformerFactory.newInstance();
      } catch (final TransformerFactoryConfigurationError tfce) {
        logB.error("XmlFactory.getTransformerFactory : failure on TransformerFactory initialisation : ", tfce);
      }
    }

    return transformerFactory;
  }

  /**
   * Returns a SchemaFactory (JAXP)
   *
   * @return SchemaFactory (JAXP)
   */
  protected static final SchemaFactory getSchemaFactory() {
    if (schemaFactory == null) {

      // 1. Lookup a factory for the W3C XML Schema language
      schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // 2. Set the custom XML catalog resolver :
      schemaFactory.setResourceResolver(CustomXmlCatalogResolver.getInstance());
    }

    return schemaFactory;
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
    Schema s = null;
    try {
      final URL url = FileUtils.getResource(schemaURL);
    	
      final File file = FileUtils.getFile(schemaURL);
      

      if (logB.isInfoEnabled()) {
        logB.info("XmlFactory.getSchema : retrieve schema and compile it : " + schemaURL);
      }

      // 2. Compile the schema.
      final long start = System.nanoTime();

      SchemaFactory sf = getSchemaFactory();
      s = sf.newSchema(url);
      

      TimerFactory.getTimer("XmlFactory.getSchema[" + schemaURL + "]").addMilliSeconds(start, System.nanoTime());

      if (logB.isInfoEnabled()) {
        logB.info("XmlFactory.getSchema : schema ready : " + s);
      }

    } catch (final SAXException se) {
      throw new IllegalStateException("XmlFactory.getSchema : unable to create a Schema for : " + schemaURL, se);
/*    } catch (final MalformedURLException mue) {
      throw new IllegalStateException("XmlFactory.getSchema : unable to create a Schema for : " + schemaURL, mue);
*/    }
    return s;
  }

  /**
   * Returns an Identity transformer (identity xslt)
   *
   * @return identity transformer
   */
  protected static final Transformer newTransformer() {
    try {
      return getOutTransformer(getTransformerFactory().newTransformer());
    } catch (final TransformerConfigurationException tce) {
      logB.error("XmlFactory.newTransformer : failure on creating new Transformer : ", tce);
    }

    return null;
  }

  /**
   * Returns a transformer for the given xslt source
   *
   * @param source stream source for xslt script
   *
   * @return transformer for the given xslt source
   */
  protected static final Transformer newTransformer(final StreamSource source) {
    try {
      return getOutTransformer(getTransformerFactory().newTransformer(source));
    } catch (final TransformerConfigurationException tce) {
      logB.error("XmlFactory.newTransformer : failure on creating new Transformer for source : " + source, tce);
    }

    return null;
  }

  /**
   * Returns a transformer for the given xslt template (precompiled xslt script)
   *
   * @param tmp xslt template (precompiled xslt script)
   *
   * @return transformer for the given xslt template
   */
  protected static final Transformer newTransformer(final Templates tmp) {
    try {
      return getOutTransformer(tmp.newTransformer());
    } catch (final TransformerConfigurationException tce) {
      logB.error("XmlFactory.newTransformer : failure on creating new Transformer for template : " + tmp, tce);
    }

    return null;
  }

  /**
   * Returns a new xslt template (precompiled xslt script) for the given xslt source
   *
   * @param source stream source for xslt script
   *
   * @return new xslt template
   */
  protected static final Templates newTemplate(final StreamSource source) {
    try {
      return getTransformerFactory().newTemplates(source);
    } catch (final TransformerConfigurationException tce) {
      logB.error("XmlFactory.newTransformer : failure on creating new template : " + source, tce);
    }

    return null;
  }

  /**
   * Sets the encoding and indetation parameters for the given transformer
   *
   * @param tf transformer
   *
   * @return tf transformer
   */
  private static final Transformer getOutTransformer(final Transformer tf) {
    tf.setOutputProperty(OutputKeys.ENCODING, ENCODING);
    tf.setOutputProperty(OutputKeys.INDENT, "yes");

    return tf;
  }

  /**
   * Parses a local xml file with JAXP
   *
   * @param f local file
   *
   * @return Document (DOM)
   */
  public static final Document parse(final File f) {
    String uri = "file:" + f.getAbsolutePath();

    if (File.separatorChar == '\\') {
      uri = uri.replace('\\', '/');
    }

    return parse(new InputSource(uri));
  }

  /**
   * Parses an xml stream with JAXP
   *
   * @param is input stream
   *
   * @return Document (DOM)
   */
  public static final Document parse(final InputStream is) {
    final InputSource in = new InputSource(is);

    return parse(in);
  }

  /**
   * Parses an xml stream with JAXP
   *
   * @param is input stream
   * @param systemId absolute file or URL reference used to resolve other xml document references
   *
   * @return Document (DOM)
   */
  public static final Document parse(final InputStream is, final String systemId) {
    final InputSource in = new InputSource(is);

    in.setSystemId(systemId);

    return parse(in);
  }

  /**
   * Parses an xml stream with JAXP
   *
   * @param input xml input source
   *
   * @return Document (DOM)
   */
  public static final Document parse(final InputSource input) {
    if (logB.isDebugEnabled()) {
      logB.debug("XmlFactory.parse : begin");
    }

    Document document = null;

    try {
      input.setEncoding(ENCODING);
      document = getFactory().newDocumentBuilder().parse(input);
    } catch (final SAXException se) {
      logB.error("XmlFactory.parse : error", se);
    } catch (final IOException ioe) {
      logB.error("XmlFactory.parse : error", ioe);
    } catch (final ParserConfigurationException pce) {
      logB.error("XmlFactory.parse : error", pce);
    }

    if (logB.isInfoEnabled()) {
      logB.info("XmlFactory.parse : exit : " + document);
    }

    return document;
  }

  /**
   * Process xslt on xml document XSL code can use parameter 'lastModified'
   *
   * @param xmlSource XML content to transform
   * @param xslFilePath XSL file to use (XSLT)
   * @param doCacheXsl true indicates that XSLT can be keep in permanent cache for reuse (avoid a lot of wasted time
   *        (compiling xslt) for many transformations)
   *
   * @return result document
   */
  public static String transform(final String xmlSource, final String xslFilePath, final boolean doCacheXsl) {
    return transform(xmlSource, xslFilePath, 0L, doCacheXsl);
  }

  /**
   * Process xslt on xml document XSL code can use parameter 'lastModified'
   *
   * @param xmlSource XML content to transform
   * @param xslFilePath XSL file to use (XSLT)
   * @param lastModified document modification date (xml and / or xsl)
   * @param doCacheXsl true indicates that XSLT can be keep in permanent cache for reuse (avoid a lot of wasted time
   *        (compiling xslt) for many transformations)
   *
   * @return result document
   */
  public static String transform(final String xmlSource, final String xslFilePath, final long lastModified,
                                 final boolean doCacheXsl) {
    final StringBuilderWriter out = new StringBuilderWriter(DEFAULT_BUFFER_SIZE);

    transform(xmlSource, xslFilePath, lastModified, doCacheXsl, out);

    return out.toString();
  }

  /**
   * Process xslt on xml document
   *
   * @param xmlSource XML content to transform
   * @param xslFilePath XSL file to use (XSLT)
   * @param doCacheXsl true indicates that XSLT can be keep in permanent cache for reuse (avoid a lot of wasted time
   *        (compiling xslt) for many transformations)
   * @param out buffer (should be cleared before method invocation)
   */
  public static void transform(final String xmlSource, final String xslFilePath, final boolean doCacheXsl,
                               final Writer out) {
    transform(xmlSource, xslFilePath, 0L, doCacheXsl, out);
  }

  /**
   * Process xslt on xml document XSL code can use parameter 'lastModified'
   *
   * @param xmlSource XML content to transform
   * @param xslFilePath XSL file to use (XSLT)
   * @param lastModified document modification date (xml and / or xsl)
   * @param doCacheXsl true indicates that XSLT can be keep in permanent cache for reuse (avoid a lot of wasted time
   *        (compiling xslt) for many transformations)
   * @param out buffer (should be cleared before method invocation)
   */
  public static void transform(final String xmlSource, final String xslFilePath, final long lastModified,
                               final boolean doCacheXsl, final Writer out) {
    if (logB.isDebugEnabled()) {
      logB.debug("XmlFactory.transform : enter : xslFilePath : " + xslFilePath);
    }

    if ((xmlSource != null) && (xslFilePath != null)) {
      Transformer tf = null;

      if (doCacheXsl) {
        tf = loadXsl(xslFilePath);
      } else {
        final File file = new File(xslFilePath);

        tf = newTransformer(new StreamSource(file));
      }

      if (tf != null) {
        if (lastModified > 0L) {
          tf.setParameter("lastModified", String.valueOf(lastModified));
        }

        if (logB.isDebugEnabled()) {
          logB.debug("XmlFactory.transform : XML Source : " + xmlSource);
        }

        asString(tf, new StreamSource(new StringReader(xmlSource)), out);
      }
    }

    if (logB.isDebugEnabled()) {
      logB.debug("XmlFactory.transform : exit : " + out);
    }
  }

  /**
   * Loads xml template with cache
   *
   * @param absoluteFilePath absolute file path for xml document
   *
   * @return XML Document or null if file does not exist or not (xml) valid
   */
  public static final Document loadTemplate(final String absoluteFilePath) {
    if ((absoluteFilePath == null) || (absoluteFilePath.length() == 0)) {
      logB.error("XmlFactory.loadTemplate : unable to load template : empty file name !");

      return null;
    }

    Document doc = cacheDOM.get(absoluteFilePath);

    if (doc == null) {
      final File file = new File(absoluteFilePath);

      if (!file.exists()) {
        logB.error("XmlFactory.loadTemplate : unable to load template : no file found for : " + absoluteFilePath);

        return null;
      }

      if (logB.isDebugEnabled()) {
        logB.debug("XmlFactory.loadTemplate : file : " + file);
      }

      doc = parse(file);

      if (doc != null) {
        cacheDOM.put(absoluteFilePath, doc);

        if (logB.isDebugEnabled()) {
          logB.debug(
              "XmlFactory.loadTemplate : template : " + Integer.toHexString(doc.hashCode()) + " : \n" + asString(doc));
        }
      }
    }

    if (doc != null) {
      if (logB.isDebugEnabled()) {
        logB.debug("XmlFactory.loadTemplate : template in use : " + Integer.toHexString(doc.hashCode()));
      }

      doc = (Document) doc.cloneNode(true);
    }

    if (logB.isDebugEnabled()) {
      logB.debug("XmlFactory.loadTemplate : xml document :\n" + asString(doc));
    }

    return doc;
  }

  /**
   * Loads xsl template with cache
   *
   * @param absoluteFilePath absolute file path for xsl document
   *
   * @return transformer or null if file does not exist or xslt not valid
   */
  public static final Transformer loadXsl(final String absoluteFilePath) {
    if ((absoluteFilePath == null) || (absoluteFilePath.length() == 0)) {
      logB.error("XmlFactory.loadXsl : unable to load template : empty file name !");

      return null;
    }

    Transformer tf = null;
    Templates tmp = cacheXSL.get(absoluteFilePath);

    if (tmp == null) {
      final File file = new File(absoluteFilePath);

      if (!file.exists()) {
        logB.error("XmlFactory.loadXsl : unable to load xslt : no file found for : " + absoluteFilePath);

        return null;
      }

      if (logB.isDebugEnabled()) {
        logB.debug("XmlFactory.loadXsl : file : " + file);
      }

      try {
        tmp = newTemplate(new StreamSource(file));
        cacheXSL.put(absoluteFilePath, tmp);

        if (logB.isDebugEnabled()) {
          logB.debug("XmlFactory.loadXsl : template : " + Integer.toHexString(tmp.hashCode()));
        }
      } catch (final Exception e) {
        logB.error("XmlFactory.loadXsl : unable to create template for XSL : " + file, e);
      }
    }

    if (tmp != null) {
      if (logB.isDebugEnabled()) {
        logB.debug("XmlFactory.loadXsl : template in cache : " + Integer.toHexString(tmp.hashCode()));
      }

      tf = newTransformer(tmp);
    }

    if (logB.isDebugEnabled()) {
      logB.debug("XmlFactory.loadXsl : xslt : " + tf);
    }

    return tf;
  }

  /**
   * Converts node hierarchy with identity transformer
   *
   * @param node xml node (root)
   *
   * @return String produced by transformer
   */
  public static String asString(final Node node) {
    if (node == null) {
      return "null";
    }

    return asString(new DOMSource(node), DEFAULT_BUFFER_SIZE);
  }

  /**
   * Converts node hierarchy with identity transformer
   *
   * @param node xml node (root)
   * @param bufferSize buffer size
   *
   * @return String produced by transformer
   */
  public static String asString(final Node node, final int bufferSize) {
    if (node == null) {
      return "null";
    }

    return asString(new DOMSource(node), bufferSize);
  }

  /**
   * Converts source nodes with identity transformer
   *
   * @param source xml nodes
   * @param bufferSize buffer size
   *
   * @return String produced by transformer
   */
  public static String asString(final Source source, final int bufferSize) {
    return asString(newTransformer(), source, bufferSize);
  }

  /**
   * Converts source nodes to a string with given transformer
   *
   * @param transformer XSL transformer to use
   * @param node xml node (root)
   *
   * @return String produced by transformer
   */
  public static String asString(final Transformer transformer, final Node node) {
    if (node == null) {
      return "null";
    }

    return asString(transformer, new DOMSource(node), DEFAULT_BUFFER_SIZE);
  }

  /**
   * Converts source nodes to a string with given transformer
   *
   * @param transformer XSL transformer to use
   * @param source xml nodes
   * @param bufferSize buffer size
   *
   * @return String produced by transformer
   */
  public static String asString(final Transformer transformer, final Source source, final int bufferSize) {
    final StringBuilderWriter out = new StringBuilderWriter(bufferSize);

    asString(transformer, source, out);

    return out.toString();
  }

  /**
   * Converts source nodes into the String buffer with given transformer
   *
   * @param transformer XSL transformer to use
   * @param source xml nodes
   * @param out buffer (should be cleared before method invocation)
   *
   * @throws RuntimeException if TransformerException
   */
  public static void asString(final Transformer transformer, final Source source, final Writer out) {
    try {
      transformer.transform(source, new StreamResult(out));
    } catch (final TransformerException te) {
      throw new RuntimeException("XmlFactory.asString : transformer failure :", te);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------

