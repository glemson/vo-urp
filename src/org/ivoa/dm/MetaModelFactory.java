package org.ivoa.dm;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.model.MetadataElement;
import org.ivoa.jaxb.JAXBFactory;
import org.ivoa.jpa.JPAFactory;
import org.ivoa.metamodel.Attribute;
import org.ivoa.metamodel.DataType;
import org.ivoa.metamodel.Element;
import org.ivoa.metamodel.Enumeration;
import org.ivoa.metamodel.Model;
import org.ivoa.metamodel.ObjectType;
import org.ivoa.metamodel.PrimitiveType;
import org.ivoa.tap.Schemas;
import org.ivoa.util.CollectionUtils;
import org.ivoa.util.FileUtils;
import org.ivoa.util.ReflectionUtils;
import org.ivoa.util.text.StringUtils;

/**
 * This Class exposes a MetaModel API to get informations on every UML elements to allow easy inspection of any
 * instances of the model  It loads the metaModel from an XML document with JAXB and prepares the collections
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class MetaModelFactory extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** Preload TAP schemas */
  public static final boolean LOAD_TAP = true;
  /** configuration test flag */
  public static final boolean isTest = Configuration.getInstance().isTest();
  /** meta model path  TODO use RuntimeConfiguration */
  public static final String MODEL_NAMESPACE = "https://github.com/glemson/vo-urp/xsd/vo-urp/v0.1";
  /** model path TODO do we need this? Elsewhere we already use the path explicitly */
  public static final String BASE_PACKAGE;


  static {
    String bp = RuntimeConfiguration.get().getBasePackage();

    BASE_PACKAGE = (bp.endsWith(".") ? bp : (bp + "."));
  }
  /** Identity Type */
  public static final String IDENTITY_TYPE = "Identity";
  /** model path */
  public static final String JAXB_PACKAGE = RuntimeConfiguration.get().getJAXBPackage();
  /** model file to load */
  public static final String MODEL_FILE = RuntimeConfiguration.get().getIntermediateModelFile();
  /**
   * JPA persistence unit to load ! TODO Should this be a static variable? At some point we may want to manage
   * multiple data models in one application.
   */
  public static final String JPA_PU = RuntimeConfiguration.get().getJPAPU();
  /** singleton instance (java 5 memory model) */
  private static volatile MetaModelFactory instance = null;

  //~ Members ----------------------------------------------------------------------------------------------------------
  /** meta model loaded */
  private Model model = null;

  // Maybe we should reuse the xmiId property instead of name ?
  /** primitiveTypes in the model */
  private final Map<String, PrimitiveType> primitiveTypes = new HashMap<String, PrimitiveType>();
  /** dataTypes in the model */
  private final Map<String, DataType> dataTypes = new HashMap<String, DataType>();
  /** enumarations in the model */
  private final Map<String, Enumeration> enumerations = new HashMap<String, Enumeration>();
  /** objectTypes in the model */
  private final Map<String, ObjectType> objectTypes = new LinkedHashMap<String, ObjectType>();
  /** skosconcepts used in the model. This Map is keyed by the utype of the skosconcept and the ObjectTYpe where it appears */
  private final Map<Attribute, ObjectType> skosConcepts = new LinkedHashMap<Attribute, ObjectType>();
  /** classTypes in the model */
  private final Map<String, ClassType> classTypes = new HashMap<String, ClassType>();
  /** objectClassTypes in the model */
  private final Map<String, ObjectClassType> objectClassTypes = new LinkedHashMap<String, ObjectClassType>();
  /** classes in the model */
  private final Map<String, Class<? extends MetadataElement>> classes = new HashMap<String, Class<? extends MetadataElement>>();
  /** The TAP-like metadata for the database. */
  private LinkedHashMap<String, Schemas> tap = new LinkedHashMap<String, Schemas>();

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  protected MetaModelFactory() {
    super();
    // prepare and checks if JaxbContext is properly running :
    getJAXBFactory();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Return the EntityConfigFactory singleton instance
   *
   * @return EntityConfigFactory singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static MetaModelFactory getInstance() {
    if (instance == null) {
      instance = prepareInstance(new MetaModelFactory());

      if (instance != null) {
        postPrepareInstance(instance);
      }
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
    if (logB.isInfoEnabled()) {
      logB.info("MetaModelFactory.init ...");
    }

    this.model = loadModel(MODEL_FILE);

    // TODO check whether next is sufficient to add Identity to datatypes
    for (final org.ivoa.metamodel.Profile prof : this.model.getProfile()) {
      processProfile(prof);
    }

    for (final org.ivoa.metamodel.Package p : this.model.getPackage()) {
      processPackage(p, BASE_PACKAGE);
    }

    if (log.isInfoEnabled()) {
      log.info("primitiveTypes : \n" + CollectionUtils.toString(getPrimitiveTypes(), "\n", "", ""));
      log.info("dataTypes : \n" + CollectionUtils.toString(getDataTypes(), "\n", "", ""));
      log.info("enumerations : \n" + CollectionUtils.toString(getEnumerations(), "\n", "", ""));
      log.info("objectTypes : \n" + CollectionUtils.toString(getObjectTypes(), "\n", "", ""));
      log.info("classes : \n" + CollectionUtils.toString(getClasses(), "\n", "", ""));
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
    if (instance != null) {
      instance = null;
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
    this.model = null;

    this.primitiveTypes.clear();
    this.dataTypes.clear();
    this.enumerations.clear();
    this.objectTypes.clear();
    this.classTypes.clear();
    this.objectClassTypes.clear();
    this.classes.clear();

    if (tap != null) {
      tap.clear();
    }
  }

  /**
   * Returns the JAXBFactory (see JAXB_PACKAGE)
   *
   * @return JAXBFactory
   */
  private static JAXBFactory getJAXBFactory() {
    return JAXBFactory.getInstance(JAXB_PACKAGE);
  }

  /**
   * Concrete implementations of the SingletonSupport's postInitialize() method :<br/>
   * This method must be called by concrete implementation after the singleton is defined.<br/>
   * Post Initialization pattern called after the singleton is defined
   *
   * @see SingletonSupport#postInitialize()
   *
   * @throws IllegalStateException if a problem occured
   */
  @Override
  protected void postInitialize() throws IllegalStateException {
    if (logB.isInfoEnabled()) {
      logB.info("MetaModelFactory.postInitialize ...");
    }
    ClassType ct;

    // ClassType needs dataTypes collection to be defined to resolve inheritance hierarchy :
    for (final DataType d : getDataTypes().values()) {
      // first patch descriptions :

      // creates an associated classType :
      ct = new ClassType(d);
      ct.init();

      getClassTypes().put(d.getName(), ct);
    }

    // first : retrieve Identity DataType :
    ObjectClassType.doInitIdentity(getDataType(IDENTITY_TYPE));

    ObjectClassType ot;

    // ObjectClassType needs classTypes collection to be defined to resolve inheritance hierarchy :
    for (final ObjectType o : getObjectTypes().values()) {
      // first patch descriptions :

      // creates an associated objectClassType :
      ot = new ObjectClassType(o);
      ot.init();

      getObjectClassTypes().put(o.getName(), ot);
      
      for(Attribute attr : o.getAttribute()) {
    	  if(attr.getSkosconcept() != null) {
    		  skosConcepts.put(attr, o);
        }
      }
    }

    if (log.isInfoEnabled()) {
      log.info("classTypes : \n" + CollectionUtils.toString(getClassTypes(), "\n", "", ""));
      log.info("objectClassTypes : \n" + CollectionUtils.toString(getObjectClassTypes(), "\n", "", ""));
    }

    for (final ObjectClassType c : getObjectClassTypes().values()) {
      if (c.isRoot()) {
        log.info("root : " + c.getType().getName());
      }
    }

    for (final ObjectClassType c : getObjectClassTypes().values()) {
      if (c.getBaseclass() != null) {
        ObjectClassType base = getObjectClassType(c.getBaseclass());

        base.addSubclass(c);
      }
    }

    // loads the TAP model :
    if (LOAD_TAP) {
      initTAP();

      if (tap == null) {
        throw new IllegalStateException("Unable to create MetaDataFactory : can not load TAP metadata from the database !");
      }

      if (log.isInfoEnabled()) {
        log.info("TAP views and tables : \n" + CollectionUtils.toString(tap.values(), "\n", "", ""));
      }
    }
  }

  /**
   * Process the profile. Pay particular attention to the Identity type, which is to be mapped to the predefined
   * class org.ivoa.dm.model
   *
   * @param prof uml profile
   */
  private void processProfile(final org.ivoa.metamodel.Profile prof) {
    for (final org.ivoa.metamodel.Package p : prof.getPackage()) {
      processPackage(p, BASE_PACKAGE);
    }
  }

  /**
   * Recursive method to fill collections (primitive types, data types, enumeration & object types)
   *
   * @param p package to process
   * @param parentPath parent package path
   */
  private void processPackage(final org.ivoa.metamodel.Package p, final String parentPath) {
    if (log.isInfoEnabled()) {
      log.info("processPackage : enter : " + p.getName());
    }

    final String packagePath = parentPath + p.getName() + ".";

    Object old;

    for (final PrimitiveType t : p.getPrimitiveType()) {
      processDescription(t);

      old = getPrimitiveTypes().put(t.getName(), t);

      if (old != null) {
        log.error(
                "MetaModelFactory.processPackage : DUPLICATES detected with same name : { " + t + " } <> { " + old + " }");
      }
    }

    String className;

    for (final DataType d : p.getDataType()) {
      processDescription(d);
      processCollection(d.getAttribute());

      old = getDataTypes().put(d.getName(), d);

      if (old != null) {
        log.error(
                "MetaModelFactory.processPackage : DUPLICATES detected with same name : { " + d + " } <> { " + old + " }");
      }

      // adds ObjectType classes :
      className = packagePath + d.getName();
      getClasses().put(d.getName(), ReflectionUtils.findClass(className, MetadataElement.class));
    }

    for (final Enumeration e : p.getEnumeration()) {
      processDescription(e);

      old = getEnumerations().put(e.getName(), e);

      if (old != null) {
        log.error(
                "MetaModelFactory.processPackage : DUPLICATES detected with same name : { " + e + " } <> { " + old + " }");
      }
    }

    for (final ObjectType o : p.getObjectType()) {
      processDescription(o);

      processCollection(o.getAttribute());
      processCollection(o.getReference());
      processCollection(o.getCollection());

      old = getObjectTypes().put(o.getName(), o);

      if (old != null) {
        log.error(
                "MetaModelFactory.processPackage : DUPLICATES detected with same name : { " + o + " } <> { " + old + " }");
      }

      // adds ObjectType classes :
      className = packagePath + o.getName();
      getClasses().put(o.getName(), ReflectionUtils.findClass(className, MetadataElement.class));
    }

    for (final org.ivoa.metamodel.Package cp : p.getPackage()) {
      processDescription(cp);

      processPackage(cp, packagePath);
    }

    if (log.isInfoEnabled()) {
      log.info("processPackage : exit : " + p.getName());
    }
  }

  /**
   * For the given collection of UML Element, fix description content
   *
   * @param c collection to process
   */
  private void processCollection(final Collection<? extends Element> c) {
    for (final Element e : c) {
      processDescription(e);
    }
  }

  /**
   * Fix CR and Tab chars in description field
   *
   * @param e UML Element
   */
  private void processDescription(final Element e) {
    String desc = e.getDescription();

    // converts double quotes to simple quotes (HTML) :
    desc = desc.replaceAll("\"", "'");
    // remove all white spaces (CR)
    desc = desc.replaceAll("\\s+", " ");
    desc = StringUtils.escapeXml(desc);

    e.setDescription(desc);
  }

  /**
   * Uses Jaxb to unmarshall the given file
   *
   * @param fileName file to load
   *
   * @return Model or null
   */
  private static Model unmarshallFile(final String fileName) {
    InputStream in = null;

    try {
      in = FileUtils.getSystemFileInputStream(fileName);
          
     
      // create an Unmarshaller
      final Unmarshaller u = getJAXBFactory().createUnMarshaller();

      // unmarshall a Model instance document into a tree of Java content
      // objects composed of classes from the org.ivoa.metamodel package :
      return (Model) u.unmarshal(in);

    } catch (final JAXBException je) {
      log.error("MetaModelFactory.unmarshallFile : JAXB Failure : ", je);
    } catch (final RuntimeException re) {
      log.error("MetaModelFactory.unmarshallFile : Runtime Failure : ", re);
    } finally {
      FileUtils.closeStream(in);
    }

    return null;
  }

  /**
   * Loads an Xml Model instance
   *
   * @param file model to load
   * @return Model
   * @throws IllegalStateException if the model can not be loaded, unmarshalled or is empty
   */
  public static Model loadModel(final String file) {
    if (log.isInfoEnabled()) {
      log.info("loadModel : file : " + file);
    }
    Model model = unmarshallFile(file);

    if (model == null) {
      throw new IllegalStateException("Unable to load the model : " + file);
    }
    // check packages :
    if (model.getPackage().isEmpty()) {
      throw new IllegalStateException("Unable to get any package from the loaded model : " + file);
    }

    // create an Unmarshaller
    getJAXBFactory().dump(model, 96 * 1024);

    if (log.isInfoEnabled()) {
      log.info("loadModel : exit");
    }
    return model;
  }

  /**
   * Returns the loaded meta model
   *
   * @return loaded meta model
   */
  public Model getModel() {
    return model;
  }

  /**
   * Returns dataTypes in the model
   *
   * @return dataTypes in the model
   */
  public Map<String, DataType> getDataTypes() {
    return dataTypes;
  }

  /**
   * Returns a dataType for the given name
   *
   * @param name lookup criteria
   *
   * @return dataType or null if not found
   */
  public DataType getDataType(final String name) {
    return getDataTypes().get(name);
  }

  /**
   * Returns enumerations in the model
   *
   * @return enumerations in the model
   */
  public Map<String, Enumeration> getEnumerations() {
    return enumerations;
  }

  /**
   * Returns an enumeration for the given name
   *
   * @param name lookup criteria
   *
   * @return enumeration or null if not found
   */
  public Enumeration getEnumeration(final String name) {
    return getEnumerations().get(name);
  }

  /**
   * Returns objectTypes in the model
   *
   * @return objectTypes in the model
   */
  public Map<String, ObjectType> getObjectTypes() {
    return objectTypes;
  }

  /**
   * Returns an objectType for the given name
   *
   * @param name lookup criteria
   *
   * @return objectType or null if not found
   */
  public ObjectType getObjectType(final String name) {
    return getObjectTypes().get(name);
  }

  /**
   * Returns classTypes in the model
   *
   * @return classTypes in the model
   */
  public Map<String, ClassType> getClassTypes() {
    return classTypes;
  }

  /**
   * Returns a classType for the given name
   *
   * @param name lookup criteria
   *
   * @return classType or null if not found
   */
  public ClassType getClassType(final String name) {
    return getClassTypes().get(name);
  }

  /**
   * Returns objectClassTypes in the model
   *
   * @return objectClassTypes in the model
   */
  public Map<String, ObjectClassType> getObjectClassTypes() {
    return objectClassTypes;
  }

  /**
   * Returns objectClassTypes as an ordered list
   *
   * @return objectClassTypes in the model
   */
  public Collection<ObjectClassType> getObjectClassTypeList() {
    return objectClassTypes.values();
  }

  /**
   * Returns a classType for the given name
   *
   * @param name lookup criteria
   *
   * @return classType or null if not found
   */
  public ObjectClassType getObjectClassType(final String name) {
    return getObjectClassTypes().get(name);
  }

  /**
   * Returns a classType for the given name
   *
   * @param type class lookup criteria
   *
   * @return classType or null if not found
   */
  public ObjectClassType getObjectClassType(final Class<?> type) {
    return getObjectClassTypes().get(type.getSimpleName());
  }

  /**
   * Returns primitiveTypes in the model
   *
   * @return primitiveTypes in the model
   */
  public Map<String, PrimitiveType> getPrimitiveTypes() {
    return primitiveTypes;
  }

  /**
   * Returns a primitiveType for the given name
   *
   * @param name lookup criteria
   *
   * @return primitiveType or null if not found
   */
  public PrimitiveType getPrimitiveType(final String name) {
    return getPrimitiveTypes().get(name);
  }

  /**
   * Returns classes in the model
   *
   * @return classes in the model
   */
  public Map<String, Class<? extends MetadataElement>> getClasses() {
    return classes;
  }

  /**
   * Returns a class for the given name
   *
   * @param name lookup criteria
   *
   * @return class or null if not found
   */
  public Class<? extends MetadataElement> getClass(final String name) {
    return getClasses().get(name);
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  private void initTAP() {
    EntityManager em = null;

    try {
      final JPAFactory jf = JPAFactory.getInstance(JPA_PU);

      em = jf.getEm();

      final List<Schemas> to = em.createQuery("select item from Schemas item order by item.schema_name").getResultList();

      if (tap != null) {
        tap.clear();
      }

      for (final Object o : to) {
        Schemas s = (Schemas) o;

        tap.put(s.getSchema_name(), s);
      }
    } catch (final Exception e) {
      log.error("initTAP : failure : ", e);
      tap = null;
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public LinkedHashMap<String, Schemas> getTap() {
    return tap;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Map<Attribute, ObjectType> getSkosConcepts() {
    return skosConcepts;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
