package org.ivoa.dm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.ivoa.bean.LogSupport;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.MetadataRootEntities;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.dm.model.reference.ReferenceResolver;
import org.ivoa.dm.model.visitor.PersistObjectPostProcessor;
import org.ivoa.dm.model.visitor.PersistObjectPreProcessor;
import org.ivoa.jaxb.XmlBindException;
import org.ivoa.jpa.JPAFactory;
import org.ivoa.xml.validator.ValidationResult;

/**
 * Facade Class to manage data model instance (xml documents & database representation)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class DataModelManager extends LogSupport {

  //~ Members ----------------------------------------------------------------------------------------------------------
  /** TODO : Field Description */
  private String jpa_pu;
  /** TODO : Field Description */
  private EntityManager currentEM;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   *
   * @param jpaPU jpa persistence unit to use
   */
  public DataModelManager(final String jpaPU) {
    this.jpa_pu = jpaPU;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Get the JPAFactory for the internal persistence unit
   *
   * @return JPAFactory instance
   */
  private JPAFactory getJPAFactory() {
    if (log.isInfoEnabled()) {
      log.info("DataModelManager : get JPAFactory : " + jpa_pu + " ...");
    }

    final JPAFactory jf = JPAFactory.getInstance(jpa_pu);

    if (log.isInfoEnabled()) {
      log.info("DataModelManager : get JPAFactory : " + jpa_pu + " : OK");
    }

    return jf;
  }

  /**
   * PUBLIC API :<br/>
   * Unmarshall an XML Document from the given reader to a MetadataObject instance
   *
   * @param stream input stream (points to an XML document)
   * @return unmarshalled MetadataObject
   * @throws XmlBindException if the xml unmarshall operation failed
   */
  private List<MetadataRootEntityObject> unmarshall(final InputStream stream) {
    return ModelFactory.getInstance().unmarshallToObject(new InputStreamReader(stream));
  }

  /**
   * Validate the given XML document according to the schema
   *
   * @param filePath path to the XML document to validate
   *
   * @return true if the document is valid
   */
  public boolean validate(final String filePath) {
    return ModelFactory.getInstance().validate(filePath);
  }

  /**
   * Validate the given XML stream according to the schema
   *
   * @param in inputstream used to get the XML document to validate
   *
   * @return ValidationResult container for validation messages
   */
  public ValidationResult validateStream(final InputStream in) {
    return ModelFactory.getInstance().validateStream(in);
  }

  /**
   * TODO : Method Description
   *
   * @param filePath
   * @param userName
   *
   * @return value TODO : Value Description
   */
  public List<MetadataRootEntityObject> load(final String filePath, final String userName) {
    if (log.isInfoEnabled()) {
      log.info("DataModelManager.load : " + filePath);
    }

    InputStream stream = null;

    try {
      stream = new FileInputStream(filePath);

      return load(stream, userName);
    } catch (final IOException e) {
      log.error("DataModelManager.load : runtime failure : ", e);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param stream
   * @param userName
   *
   * @return value TODO : Value Description
   */
  public List<MetadataRootEntityObject> load(final InputStream stream, final String userName) {
    EntityManager em = null;
    List<MetadataRootEntityObject> objects = null;
    Long id = null;

    try {
      em = getCurrentEM();

      // sets EntityManager to ReferenceResolver Context :
      ReferenceResolver.initContext(em);

      objects = unmarshall(stream);

      // TODO do something about the userName, maybe get from (threadlocal) context?
      persist(objects, userName);

    } catch (final RuntimeException re) {
      log.error("DataModelManager.load : runtime failure : ", re);

      // if connection failure => em is null :
      if (em != null && em.getTransaction().isActive()) {
        log.warn("DataModelManager.load : rollbacking TX ...");
        em.getTransaction().rollback();
        log.warn("DataModelManager.load : TX rollbacked.");
      }

      throw re;
    } finally {
      if (em != null) {
        em.close();
      }

      // free resolver context (thread local) :
      ReferenceResolver.freeContext();
    }

    if (log.isInfoEnabled()) {
      log.info("DataModelManager.load : exit : " + id);
    }

    return objects;
  }

  /**
   * Persist (aka flush) all specified objects (and their children) to the database.<br/>
   * For now only root entity objects can be persisted as a whole.
   * TODO decide whether this can be generalised.
   *
   * @param objects
   * @param username
   */
  public void persist(final List<MetadataRootEntityObject> objects, final String username) {
    EntityManager em = null;
    try {
      em = getCurrentEM();

      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      // TODO here we could(should?) get the current timestamp from the database, which is not necessarily in synch with the web server.
      // For now a simpler solution ...
      final Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
      final PersistObjectPreProcessor preProcessor = new PersistObjectPreProcessor(username, currentTimestamp);
      for (MetadataRootEntityObject o : objects) {
        o.accept(preProcessor);
      }
      for (MetadataRootEntityObject o : objects) {
        em.persist(o);
      }
      final PersistObjectPostProcessor postProcessor = PersistObjectPostProcessor.getInstance();
      for (MetadataRootEntityObject o : objects) {
        o.accept(postProcessor);
      }

      // finally : commits transaction on snap database :
      log.warn("DataModelManager.persist : committing TX");
      em.getTransaction().commit();
      log.warn("DataModelManager.persist : TX commited.");

    } catch (final RuntimeException re) {

      // if connection failure => em is null :
      if (em != null && em.getTransaction().isActive()) {
        log.warn("DataModelManager.persist : rollbacking TX ...");
        em.getTransaction().rollback();
        log.warn("DataModelManager.persist : TX rollbacked.");
      }
      throw re;
    }
  }

  /**
   * For sharing the EntityManager between methods, store it on the DataModelManager.<br/>
   * TODO make sure this is thread safe !!!
   * @return EntityManager
   */
  public EntityManager getCurrentEM() {
    if (currentEM == null || !currentEM.isOpen()) {
      JPAFactory jf = getJPAFactory();
      currentEM = jf.getEm();
    }
    return currentEM;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
