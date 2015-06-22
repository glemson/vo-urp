package org.ivoa.dm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.model.MetadataElement;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetadataRootEntities;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.dm.model.visitor.MarshallObjectPostProcessor;
import org.ivoa.dm.model.visitor.MarshallObjectPreProcessor;
import org.ivoa.dm.model.visitor.MarshallReferencePostProcessor;
import org.ivoa.dm.model.visitor.MarshallReferencePreProcessor;
import org.ivoa.dm.model.visitor.UnmarshallObjectProcessor;
import org.ivoa.jaxb.CustomUnmarshallListener;
import org.ivoa.jaxb.JAXBFactory;
import org.ivoa.jaxb.XmlBindException;
import org.ivoa.json.MetadataObject2JSON;
import org.ivoa.util.FileUtils;
import org.ivoa.util.ReflectionUtils;
import org.ivoa.util.SystemLogUtil;
import org.ivoa.util.text.StringBuilderWriter;
import org.ivoa.xml.validator.ErrorMessage;
import org.ivoa.xml.validator.ValidationResult;
import org.ivoa.xml.validator.XMLValidator;

/**
 * MetadataElement factory Manages creating new instances for generated classes
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ModelFactory extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** JAXB : context path */
  public static final String JAXB_PATH = RuntimeConfiguration.get().getJAXBContextClasspath();

  /** The location of a local file containing the schema.<br> */
  public static final String SCHEMA_LOCATION = RuntimeConfiguration.get().getRootSchemaLocation();
  
  /** The URL where the schema can be found.<br> */
  public static final String SCHEMA_URL = RuntimeConfiguration.get().getRootSchemaURL();

  /**
   * singleton instance (java 5 memory model)
   * no good in webapp context once state must be preserved ?
   */
  private static volatile ModelFactory instance = null;

  //~ Members ----------------------------------------------------------------------------------------------------------
  /** Jaxb factory corresponding to context */
  private JAXBFactory jf = null;
  /** xml validator */
  private XMLValidator validator;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  private ModelFactory() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Return the ModelFactory singleton instance
   *
   * @return ModelFactory singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final ModelFactory getInstance() {
    if (instance == null) {
      instance = prepareInstance(new ModelFactory());
    }
    return instance;
  }

  /**
   * Concrete implementations of the SingletonSupport's initialize() method :<br/>
   * Callback to initialize this SingletonSupport instance
   *
   * @see SingletonSupport#initialize()
   *
   * @throws IllegalStateException if a problem occured
   */
  @Override
  protected void initialize() throws IllegalStateException {
    if (jf == null) {
      jf = JAXBFactory.getInstance(JAXB_PATH);
    }
    if (validator == null) {
      validator = XMLValidator.getInstance(SCHEMA_LOCATION);
    }
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
      logB.info("ModelFactory.onExit : enter");
    }
    if (instance != null) {
      instance = null;
    }
    if (logB.isInfoEnabled()) {
      logB.info("ModelFactory.onExit : exit");
    }
  }

  /**
   * Concrete implementations of the SingletonSupport's clear() method :<br/>
   * Callback to clean up this SingletonSupport instance iso clear instance fields
   *
   * @see SingletonSupport#clear()
   */
  @Override
  protected void clear() {
    // force GC :
    this.jf = null;
    this.validator = null;
  }

  /**
   * PUBLIC API :<br/>
   * Factory implementation : creates new instance for MetadataElement
   *
   * @param className MetadataElement short Name
   *
   * @return new MetadataElement instance or null
   */
  public MetadataElement newInstance(final String className) {
    final Class<? extends MetadataElement> cl = MetaModelFactory.getInstance().getClass(className);

    if (cl != null) {
      return ReflectionUtils.newInstance(cl);
    }

    return null;
  }

  /**
   * PUBLIC API :<br/>
   * Marshall a MetadataObject instance to an XML Document
   *
   * @param filePath absolute File path to save
   * @param source MetadataObject to marshall as an XML document
   * @throws XmlBindException if the xml marshall operation failed
   */
  public void marshallObject(final String filePath, final MetadataObject source) throws XmlBindException {
    if (source == null) {
      if (log.isInfoEnabled()) {
        log.info("ModelFactory.marshallObject : empty source !");
      }
      return;
    }

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.marshallObject : saving : " + filePath);
    }

    Writer writer = null;
    try {
      writer = FileUtils.openFile(filePath);

      marshallObject(source, writer);

      if (log.isDebugEnabled()) {
        log.debug(SystemLogUtil.LOG_LINE_SEPARATOR);
        log.debug("ModelFactory.marshallObject : file saved : " + filePath);
        log.debug(SystemLogUtil.LOG_LINE_SEPARATOR);
      }
    } catch (final RuntimeException re) {
      log.error("ModelFactory.marshallObject : runtime failure : ", re);
    } finally {
      FileUtils.closeFile(writer);
    }
  }

  /**
   * PUBLIC API :<br/>
   * Marshall a MetadataObject instance to an XML Document
   *
   * @param filePath absolute File path to save
   * @param source MetadataObject to marshall as an XML document
   * @throws XmlBindException if the xml marshall operation failed
   */
  public void marshallObject(final String filePath, final MetadataRootEntities source) throws XmlBindException {
    if (source == null) {
      if (log.isInfoEnabled()) {
        log.info("ModelFactory.marshallObject : empty source !");
      }
      return;
    }

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.marshallObject : saving : " + filePath);
    }

    Writer writer = null;
    try {
      writer = FileUtils.openFile(filePath);

      marshallObject(source, writer);

      if (log.isDebugEnabled()) {
        log.debug(SystemLogUtil.LOG_LINE_SEPARATOR);
        log.debug("ModelFactory.marshallObject : file saved : " + filePath);
        log.debug(SystemLogUtil.LOG_LINE_SEPARATOR);
      }
    } catch (final RuntimeException re) {
      log.error("ModelFactory.marshallObject : runtime failure : ", re);
    } finally {
      FileUtils.closeFile(writer);
    }
  }
  /**
   * PUBLIC API :<br/>
   * Marshall a MetadataObject instance to a String (xml)
   *
   * @param source MetadataObject to marshall as an XML document
   * @param bufferCapacity memory buffer capacity
   *
   * @return String containing XML document or null
   * @throws XmlBindException if the xml marshall operation failed
   */
  public String marshallObject(final MetadataObject source, final int bufferCapacity) throws XmlBindException {
    String output = null;
    if (source != null) {
      // output in memory (should stay small) :
      final StringBuilderWriter writer = new StringBuilderWriter(bufferCapacity);

      marshallObject(source, writer);

      output = writer.toString();
    }

    return output;
  }

  /**
   * Marshall an XML Document from a MetadataObject to a Writer
   *
   * @param source MetadataObject to marshall as an XML document
   * @param writer writer to use
   * @throws XmlBindException if the xml marshall operation failed
   */
  private void marshallObject(final MetadataObject source, final Writer writer) throws XmlBindException {
    if (source != null) {
      try {
        source.accept(MarshallObjectPreProcessor.getInstance());
        source.accept(MarshallReferencePreProcessor.getInstance());

        // create an Unmarshaller
        final Marshaller m = getJaxbFactory().createMarshaller();

        // marshal a tree of Java content objects composed of classes
        // from the VO-URP-generated root package into an instance document.
        m.marshal(source, writer);

        source.accept(MarshallObjectPostProcessor.getInstance());
        source.accept(MarshallReferencePostProcessor.getInstance());

      } catch (final JAXBException je) {
        throw new XmlBindException("ModelFactory.marshallObject : JAXB Failure", je);
      }
    }
  }

  /**
   * PUBLIC API :<br/>
   * Unmarshall an XML Document from the specified file path to a MetadataObject instance
   *
   * @param filePath absolute File path to load
   * @return value unmarshalled MetadataObject
   * @throws XmlBindException if the xml unmarshall operation failed
   */
  public List<MetadataRootEntityObject> unmarshallToObject(final String filePath) throws XmlBindException {
    if (log.isInfoEnabled()) {
      log.info("ModelFactory.unmarshallToObject : loading : " + filePath);
    }

    Reader r = null;

    r = FileUtils.readFile(filePath);

    try {
      return unmarshallToObject(r);
    } finally {
      FileUtils.closeFile(r);
    }
  }

  /**
   * PUBLIC API :<br/>
   * Unmarshall an XML Document from the given reader to a MetadataObject instance
   *
   * @param r reader (points to an XML document)
   * @return value unmarshalled MetadataObject
   * @throws XmlBindException if the xml unmarshall operation failed
   */
  public List<MetadataRootEntityObject> unmarshallToObject(final Reader r) throws XmlBindException {
    try {
      // create an Unmarshaller
      final Unmarshaller u = getJaxbFactory().createUnMarshaller();

      // adds identityListener to manage references & collections properly :
      u.setListener(CustomUnmarshallListener.getInstance());

      // unmarshal an instance document into a tree of Java content
      // objects composed of classes from the VO-URP generated root package.
      // TODO TBD should this be a MetadataRootEntityObject. In current design it always should be, 
      // but should we make this restriction here or put this burden on the user?
      final Object object = u.unmarshal(r);

      ArrayList<MetadataRootEntityObject> objects = new ArrayList<MetadataRootEntityObject>();
      if(object instanceof MetadataRootEntityObject)
      {
    	  ((MetadataRootEntityObject)object).accept(UnmarshallObjectProcessor.getInstance());
    	  objects.add((MetadataRootEntityObject)object);
      } else if(object instanceof MetadataRootEntities){
    	  MetadataRootEntities root = (MetadataRootEntities)object;
    	  for(MetadataRootEntityObject o : root.getEntity())
    	  {
        	  o.accept(UnmarshallObjectProcessor.getInstance());
        	  objects.add(o);
    	  }
      }
      if (log.isInfoEnabled()) {
    	  for(MetadataObject o : objects)
    		  log.info("ModelFactory.unmarshallToObject : item loaded : " + o.deepToString());
      }

      return objects;
    } catch (final JAXBException je) {
      throw new XmlBindException("ModelFactory.unmarshallToObject : JAXB Failure", je);
    }
  }

  /**
   * PUBLIC API :<br/>
   * Return JSON serialisation of the given object.<br>
   *
   * @param object MetadataObject to serialize as a JSON string
   *
   * @return JSON serialisation of the given object
   */
  public String toJSON(final MetadataObject object) {
    return MetadataObject2JSON.toJSONString(object);
  }

  /**
   * Validate the given XML document according to the schema
   *
   * @param filePath path to the XML document to validate
   *
   * @return true if the document is valid
   */
  public boolean validate(final String filePath) {
    if (log.isInfoEnabled()) {
      log.info("ModelFactory.validate : " + filePath);
    }

    boolean res = false;

    final File file = FileUtils.getFile(filePath);

    if (file != null) {
      try {
        final InputStream in = new BufferedInputStream(new FileInputStream(file));

        final ValidationResult result = validateStream(in);

        res = result.isValid();

        if (!res) {
          for (final ErrorMessage em : result.getMessages()) {
            log.error(em.toString());
          }
        }
      } catch (final FileNotFoundException fnfe) {
        // already checked
      }
    }

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.validate : " + res);
    }

    return res;
  }

  /**
   * Validate the given XML stream according to the schema
   *
   * @param in inputstream used to get the XML document to validate
   *
   * @return ValidationResult container for validation messages
   */
  public ValidationResult validateStream(final InputStream in) {
    final ValidationResult result = getValidator().validate(in);

    return result;
  }

  /**
   * Return the JAXBFactory for the data model
   *
   * @return JAXBFactory for the data model
   */
  private JAXBFactory getJaxbFactory() {
    return jf;
  }
  /**
   * Return the XMLValidator for the data model
   *
   * @return XMLValidator for the data model
   */
  private XMLValidator getValidator() {
    return validator;
  }

/**
   * Marshall an XML Document from a MetadataObject to a Writer
   *
   * @param source MetadataObject to marshall as an XML document
   * @param writer writer to use
   * @throws XmlBindException if the xml marshall operation failed
   */
  private void marshallObject(final MetadataRootEntities source, final Writer writer) throws XmlBindException {
    if (source != null) {
      try {
    	  for(MetadataRootEntityObject o: source.getEntity()){
    		  o.accept(MarshallObjectPreProcessor.getInstance());
    		  o.accept(MarshallReferencePreProcessor.getInstance());
    	  }
        // create an Unmarshaller
        final Marshaller m = getJaxbFactory().createMarshaller();

        // marshal a tree of Java content objects composed of classes
        // from the VO-URP-generated root package into an instance document.
        m.marshal(source, writer);
  	  for(MetadataRootEntityObject o: source.getEntity()){
          o.accept(MarshallObjectPostProcessor.getInstance());
          o.accept(MarshallReferencePostProcessor.getInstance());
	  }


      } catch (final JAXBException je) {
        throw new XmlBindException("ModelFactory.marshallObject : JAXB Failure", je);
      }
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
