package org.ivoa.xml;

import org.apache.commons.logging.Log;

import org.ivoa.util.LogUtil;
import org.ivoa.util.StringBuilderWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;

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


/**
 * Utility class for XML parsing & transforming (XSLT)  <br><b>Supports XML document & XSLT caches</b>
 *
 * @author laurent bourges (voparis)
 */
public final class XmlFactory {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** encoding used for XML and XSL documents */
  public static final String ENCODING = "UTF-8";
  /** default buffer size (4 ko) for XSLT result document */
  public static final int DEFAULT_BUFFER_SIZE = 2048;

  // constants :
  /** logger */
  private static final Log log = LogUtil.getLoggerDev();
  /** inner factory */
  private static DocumentBuilderFactory factory = null;
  /** inner xslt factory */
  private static TransformerFactory tFactory = null;
  /** cache for Dom instances */
  private static final Map<String, Document> m_oCacheDOM = new HashMap<String, Document>(32);
  /** cache for Xsl templates */
  private static final Map<String, Templates> m_oCacheXSL = new HashMap<String, Templates>(32);

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new XmlFactory object
   */
  private XmlFactory() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final DocumentBuilderFactory getFactory() {
    if (factory == null) {
      factory = DocumentBuilderFactory.newInstance();
    }

    return factory;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  protected static final TransformerFactory getTransformerFactory() {
    if (tFactory == null) {
      try {
        tFactory = TransformerFactory.newInstance();
      } catch (final TransformerFactoryConfigurationError tfce) {
        log.error("XmlFactory.getTransformerFactory : failure on TransformerFactory initialisation : ", tfce);
      }
    }

    return tFactory;
  }

  /**
   * Returns Identity transformer
   *
   * @return identity transformer
   */
  protected static final Transformer newTransformer() {
    try {
      return getOutTransformer(getTransformerFactory().newTransformer());
    } catch (final TransformerConfigurationException tce) {
      log.error("XmlFactory.newTransformer : failure on creating new Transformer : ", tce);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param source 
   *
   * @return value TODO : Value Description
   */
  protected static final Transformer newTransformer(final StreamSource source) {
    try {
      return getOutTransformer(getTransformerFactory().newTransformer(source));
    } catch (final TransformerConfigurationException tce) {
      log.error("XmlFactory.newTransformer : failure on creating new Transformer for source : " + source, tce);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param tmp 
   *
   * @return value TODO : Value Description
   */
  protected static final Transformer newTransformer(final Templates tmp) {
    try {
      return getOutTransformer(tmp.newTransformer());
    } catch (final TransformerConfigurationException tce) {
      log.error("XmlFactory.newTransformer : failure on creating new Transformer for template : " + tmp, tce);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param source 
   *
   * @return value TODO : Value Description
   */
  protected static final Templates newTemplate(final StreamSource source) {
    try {
      return getTransformerFactory().newTemplates(source);
    } catch (final TransformerConfigurationException tce) {
      log.error("XmlFactory.newTransformer : failure on creating new template : " + source, tce);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param tf 
   *
   * @return value TODO : Value Description
   */
  private static final Transformer getOutTransformer(final Transformer tf) {
    tf.setOutputProperty(OutputKeys.ENCODING, ENCODING);
    tf.setOutputProperty(OutputKeys.INDENT, "yes");

    return tf;
  }

  /**
   * TODO : Method Description
   *
   * @param f 
   *
   * @return value TODO : Value Description
   */
  public static final Document parse(final File f) {
    String uri = "file:" + f.getAbsolutePath();

    if (File.separatorChar == '\\') {
      uri = uri.replace('\\', '/');
    }

    return parse(new InputSource(uri));
  }

  /**
   * TODO : Method Description
   *
   * @param is 
   *
   * @return value TODO : Value Description
   */
  public static final Document parse(final InputStream is) {
    final InputSource in = new InputSource(is);

    return parse(in);
  }

  /**
   * TODO : Method Description
   *
   * @param is 
   * @param systemId 
   *
   * @return value TODO : Value Description
   */
  public static final Document parse(final InputStream is, final String systemId) {
    final InputSource in = new InputSource(is);

    in.setSystemId(systemId);

    return parse(in);
  }

  /**
   * TODO : Method Description
   *
   * @param input 
   *
   * @return value TODO : Value Description
   */
  public static final Document parse(final InputSource input) {
    if (log.isDebugEnabled()) {
      log.debug("XmlFactory.parse : begin");
    }

    Document document = null;

    try {
      input.setEncoding(ENCODING);
      document = getFactory().newDocumentBuilder().parse(input);
    } catch (final SAXException se) {
      log.error("XmlFactory.parse : error", se);
    } catch (final IOException ioe) {
      log.error("XmlFactory.parse : error", ioe);
    } catch (final ParserConfigurationException pce) {
      log.error("XmlFactory.parse : error", pce);
    }

    if (log.isInfoEnabled()) {
      log.info("XmlFactory.parse : exit : " + document);
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
    if (log.isDebugEnabled()) {
      log.debug("XmlFactory.transform : enter : xslFilePath : " + xslFilePath);
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

        if (log.isDebugEnabled()) {
          log.debug("XmlFactory.transform : XML Source : " + xmlSource);
        }

        asString(tf, new StreamSource(new StringReader(xmlSource)), out);
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("XmlFactory.transform : exit : " + out);
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
      log.error("XmlFactory.loadTemplate : unable to load template : empty file name !");

      return null;
    }

    Document doc = m_oCacheDOM.get(absoluteFilePath);

    if (doc == null) {
      final File file = new File(absoluteFilePath);

      if (! file.exists()) {
        log.error("XmlFactory.loadTemplate : unable to load template : no file found for : " + absoluteFilePath);

        return null;
      }

      if (log.isDebugEnabled()) {
        log.debug("XmlFactory.loadTemplate : file : " + file);
      }

      doc = parse(file);

      if (doc != null) {
        m_oCacheDOM.put(absoluteFilePath, doc);

        if (log.isDebugEnabled()) {
          log.debug(
            "XmlFactory.loadTemplate : template : " + Integer.toHexString(doc.hashCode()) + " : \n" + asString(doc));
        }
      }
    }

    if (doc != null) {
      if (log.isDebugEnabled()) {
        log.debug("XmlFactory.loadTemplate : template in use : " + Integer.toHexString(doc.hashCode()));
      }

      doc = (Document) doc.cloneNode(true);
    }

    if (log.isDebugEnabled()) {
      log.debug("XmlFactory.loadTemplate : xml document :\n" + asString(doc));
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
      log.error("XmlFactory.loadXsl : unable to load template : empty file name !");

      return null;
    }

    Transformer tf  = null;
    Templates   tmp = m_oCacheXSL.get(absoluteFilePath);

    if (tmp == null) {
      final File file = new File(absoluteFilePath);

      if (! file.exists()) {
        log.error("XmlFactory.loadXsl : unable to load xslt : no file found for : " + absoluteFilePath);

        return null;
      }

      if (log.isDebugEnabled()) {
        log.debug("XmlFactory.loadXsl : file : " + file);
      }

      try {
        tmp = newTemplate(new StreamSource(file));
        m_oCacheXSL.put(absoluteFilePath, tmp);

        if (log.isDebugEnabled()) {
          log.debug("XmlFactory.loadXsl : template : " + Integer.toHexString(tmp.hashCode()));
        }
      } catch (final Exception e) {
        log.error("XmlFactory.loadXsl : unable to create template for XSL : " + file, e);
      }
    }

    if (tmp != null) {
      if (log.isDebugEnabled()) {
        log.debug("XmlFactory.loadXsl : template in cache : " + Integer.toHexString(tmp.hashCode()));
      }

      tf = newTransformer(tmp);
    }

    if (log.isDebugEnabled()) {
      log.debug("XmlFactory.loadXsl : xslt : " + tf);
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
   * @throws RuntimeException
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
