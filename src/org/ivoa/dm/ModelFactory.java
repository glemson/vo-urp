package org.ivoa.dm;


import java.io.Reader;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.model.MarshallObjectPreProcessor;
import org.ivoa.dm.model.MarshallReferencePostProcessor;
import org.ivoa.dm.model.MarshallReferencePreProcessor;
import org.ivoa.dm.model.MetadataElement;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.jaxb.CustomUnmarshallListener;
import org.ivoa.jaxb.JAXBFactory;
import org.ivoa.jaxb.XmlBindException;
import org.ivoa.json.MetadataObject2JSON;
import org.ivoa.metamodel.Collection;
import org.ivoa.metamodel.Reference;
import org.ivoa.util.FileUtils;
import org.ivoa.util.ReflectionUtils;
import org.ivoa.util.StringBuilderWriter;


/**
 * MetadataElement factory  Manages creating new instances for generated classes
 *
 * @author laurent bourges (voparis)
 */
public final class ModelFactory extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** JAXB : context path */
  public static final String JAXB_PATH = RuntimeConfiguration.get().getJAXBContextClasspath();
  /** TODO : Field Description */
  public static final int DEFAULT_IDENTITY_CAPACITY = 128;
  /** singleton pattern  TODO no good in webapp context once state must be preserved */
  private static volatile ModelFactory instance = null;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** Jaxb factory corresponding to context */
  private JAXBFactory jf = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  private ModelFactory() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns singleton instance
   *
   * @return ModelFactory singleton instance
   *
   * @throws IllegalStateException if init() returns false
   */
  public static ModelFactory getInstance() {
    if (instance == null) {
      final ModelFactory f = new ModelFactory();

      if (f.init()) {
        // now factory is ok, so changes instance volatile reference :
        instance = f;
      } else {
        throw new IllegalStateException("Unable to create ModelFactory !");
      }
    }

    return instance;
  }

  /**
   * Called on exit (clean up code)
   */
  public static final void onExit() {
    if (log.isWarnEnabled()) {
        log.warn("ModelFactory.onExit : enter");
    }
    if (instance != null) {
      // clean up :
/*
      instance.jf = null;
 */
      instance = null;
    }
    if (log.isWarnEnabled()) {
        log.warn("ModelFactory.onExit : exit");
    }
  }

  /**
   * Initialization pattern called by getInstance() method
   *
   * @return true if well done
   */
  private boolean init() {
    if (jf == null) {
      jf = JAXBFactory.getInstance(JAXB_PATH);
    }

    return true;
  }

  /**
   * Factory implementation : creates new instance for MetadataElement
   *
   * @param className MetadataElement short Name
   *
   * @return new MetadataElement instance or null
   */
  public MetadataElement newInstance(final String className) {
    final Class<?extends MetadataElement> cl = MetaModelFactory.getInstance().getClass(className);

    if (cl != null) {
      return ReflectionUtils.newInstance(cl);
    }

    return null;
  }

  /**
   * Main Method : marshall an XML Document from a MetadataObject
   *
   * @param filePath absolute File path to save
   * @param source MetadataObject to marshall as an XML document
   */
  public void marshallObject(final String filePath, final MetadataObject source) {
    if (source == null) {
      if (log.isInfoEnabled()) {
        log.info("ModelFactory.marshallObject : empty source !");
      }

      return;
    }

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.marshallObject : saving : " + filePath);
    }

    Writer w = null;

    try {
      w = FileUtils.openFile(filePath);

      // object can not be null here so unmarshall References and set containerId on collections :
      // TODO : NOT working (now) :
      MarshallObjectPreProcessor mop = new MarshallObjectPreProcessor();

      source.traverse(mop);

      MarshallReferencePreProcessor mprep = new MarshallReferencePreProcessor();

      source.traverse(mprep);

      //      processExportReferences(source);

      // create an Unmarshaller
      final Marshaller m = getJaxbFactory().createMarshaller();

      // marshal a tree of Java content objects composed of classes 
      // from the VO-URP-generated root package into an instance document.
      m.marshal(source, w);

      MarshallReferencePostProcessor mpostp = new MarshallReferencePostProcessor();

      source.traverse(mpostp);

      if (log.isDebugEnabled()) {
        log.debug("-------------------------------------------------------------------------------");
        log.debug("ModelFactory.marshallObject : file saved : " + filePath);
        log.debug("-------------------------------------------------------------------------------");
      }
    } catch (final JAXBException je) {
      log.error("ModelFactory.marshallObject : JAXB Failure : ", je);
    } finally {
      FileUtils.closeFile(w);
    }
  }

  /**
   * Main Method : marshall an XML Document from a MetadataObject to a String
   *
   * @param source MetadataObject to marshall as an XML document
   * @param bufferCapacity memory buffer capacity
   *
   * @return String containing XML document or null
   */
  public String marshallObject(final MetadataObject source, final int bufferCapacity) {
    if (source != null) {
      // output in memory (should stay small) :
      final StringBuilderWriter out = new StringBuilderWriter(bufferCapacity);

      try {
        // object can not be null here so unmarshall References and set containerId on collections :
        // TODO : NOT working (now) :
        MarshallObjectPreProcessor mop = new MarshallObjectPreProcessor();

        source.traverse(mop);

        MarshallReferencePreProcessor mprep = new MarshallReferencePreProcessor();

        source.traverse(mprep);

        //        processIdentities(source);
        //        processExportReferences(source);

        // create an Unmarshaller
        final Marshaller m = getJaxbFactory().createMarshaller();

        // marshal a tree of Java content objects composed of classes 
        // from the VO-URP-generated root package into an instance document.
        m.marshal(source, out);

        MarshallReferencePostProcessor mpostp = new MarshallReferencePostProcessor();

        source.traverse(mpostp);

        return out.toString();
      } catch (final JAXBException je) {
        log.error("ModelFactory.marshallObject : JAXB Failure : ", je);
      }
    }

    return null;
  }

  /**
   * Main Method : unmarshall a XML Document from a specified file path
   *
   * @param filePath absolute File path to load
   *
   * @return value unmarshalled MetadataObject
   */
  public MetadataObject unmarshallToObject(final String filePath) {
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
   * Main Method : unmarshall an XML Document from a Reader
   *
   * @param r reader (points to an XML document)
   *
   * @return value unmarshalled MetadataObject
   */
  public MetadataObject unmarshallToObject(final Reader r) {
    try {
      // create an Unmarshaller
      final Unmarshaller u = getJaxbFactory().createUnMarshaller();

      // adds identityListener to manage references & collections properly :
      u.setListener(new CustomUnmarshallListener());

      // unmarshal an instance document into a tree of Java content
      // objects composed of classes from the VO-URP generated root package.
      final MetadataObject m = (MetadataObject) u.unmarshal(r);

      // object can not be null here so unmarshall References and set containerId on collections :
      processImportReferences(m);

      if (log.isInfoEnabled()) {
        log.info("ModelFactory.unmarshallToObject : item loaded : " + m.deepToString());
      }

      return m;
    } catch (final JAXBException je) {
      log.error("ModelFactory.unmarshallToObject : JAXB Failure : ", je);
      throw new XmlBindException(je);
    }

//    return null;
  }

  /**
   * Navigate along child axes (references / collection) to resolve all external references (see Identity)  and
   * containement references (parent with collection items)
   *
   * @param object element to process (must not be null)
   */
  private void processImportReferences(final MetadataObject object) {
    final Map<MetadataElement, Object> ids = new IdentityHashMap<MetadataElement, Object>(DEFAULT_IDENTITY_CAPACITY);

    processImportReferences(ids, object);

    // clears identity map to force gc asap :
    ids.clear();
  }

  /**
   * Navigate along child axes (references / collection) to resolve all external references (see Identity)  and
   * containement references (parent with collection items). <br>
   * <b> Recursive method </b>
   *
   * @param ids identity map to avoid cyclic loops
   * @param object object to process (must not be null)
   */
  private final void processImportReferences(final Map<MetadataElement, Object> ids, final MetadataObject object) {
    if (log.isInfoEnabled()) {
      log.info("ModelFactory.processImportReferences : enter : " + object);
    }

    // avoid cyclic loops :
    if (! MetadataElement.exists(object, ids)) {
      this.processImportReferences(ids, object, object.getClassMetaData());
    }

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.processImportReferences : exit : " + object);
    }
  }

  /**
   * Navigate along child axes (references / collection) to resolve all external references (see Identity)  and
   * containement references (parent with collection items). <br>
   * <b> Recursive method </b>
   *
   * @param ids
   * @param object
   * @param ct
   */
  @SuppressWarnings("unchecked")
  private final void processImportReferences(final Map<MetadataElement, Object> ids, final MetadataObject object,
                                             final ObjectClassType ct) {
    if (log.isInfoEnabled()) {
      log.info(
        "ModelFactory.processImportReferences : enter : " + object + " with type definition : " +
        ct.getObjectType().getName());
    }

    String                                        propertyName;
    Object                                        value;
    MetadataObject                                child;
    java.util.Collection<?extends MetadataObject> col;

    // navigate through references :
    // implies that lazy references will be resolved by ReferenceResolver :
    for (final Reference r : ct.getReferences().values()) {
      propertyName = r.getName();
      // this getProperty implies the lazy reference to be resolved now :
      value = object.getProperty(propertyName);

      if ((value != null) && value instanceof MetadataObject) {
        child = (MetadataObject) value;

        // process References (recursive loop) :
        // maybe : should only check local references ?
        this.processImportReferences(ids, child);
      }
    }

    // navigate through collections :
    for (final Collection c : ct.getCollections().values()) {
      propertyName = c.getName();
      value = object.getProperty(propertyName);

      if (value != null) {
        col = (java.util.Collection<?extends MetadataObject>) value;

        if (col.size() > 0) {
          int i = 1;

          for (final MetadataObject item : col) {
            // avoid cyclic loops :
            if (item != null) {
              checkContainer(ids, item, i, object);
              i++;
            }
          }
        }
      }
    }

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.processImportReferences : exit : " + object);
    }
  }

  /**
   * Sets containement references (parent with collection items). <br>
   * YOU SUPPORT SOON Collection Ordering base on a rank smallint field
   *
   * @param ids
   * @param object
   * @param position
   * @param parent
   */
  private void checkContainer(final Map<MetadataElement, Object> ids, final MetadataObject object, final int position,
                              final MetadataObject parent) {
    if (log.isInfoEnabled()) {
      log.info("ModelFactory.checkContainer : enter : " + object);
    }

    // check getContainer() value : 
    final MetadataObject container = (MetadataObject) object.getProperty(MetadataObject.PROPERTY_CONTAINER);

    if (container == null) {
      if (log.isInfoEnabled()) {
        log.info("ModelFactory.checkContainer : setContainer to : " + parent);
      }

      object.setProperty(MetadataObject.PROPERTY_CONTAINER, parent);
    }

    // check getRank() value : 
    final Integer rank = (Integer) object.getProperty(MetadataObject.PROPERTY_RANK);

    if (rank.intValue() == 0) {
      if (log.isInfoEnabled()) {
        log.info("ModelFactory.checkContainer : setRank to : " + position);
      }

      object.setProperty(MetadataObject.PROPERTY_RANK, Integer.valueOf(position));
    }

    // then check item : TODO should this be container iso object?
    this.processImportReferences(ids, object);

    if (log.isInfoEnabled()) {
      log.info("ModelFactory.checkContainer : exit : " + object);
    }
  }

  /**
   * Returns the JAXBFactory for the data model
   *
   * @return JAXBFactory for the data model
   */
  public JAXBFactory getJaxbFactory() {
    return jf;
  }

  /**
   * Returns JSON serialisation of object.<br>
   *
   * @param obj
   *
   * @return value TODO : Value Description
   */
  public String toJSON(final MetadataObject obj) {
    return new MetadataObject2JSON().toJSONString(obj);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
