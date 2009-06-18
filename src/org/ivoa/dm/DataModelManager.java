package org.ivoa.dm;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.persistence.EntityManager;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.ReferenceResolver;
import org.ivoa.dm.model.MetadataRootEntityObject;
import org.ivoa.jpa.JPAFactory;
import org.ivoa.util.FileUtils;
import org.ivoa.xml.validator.ErrorMessage;
import org.ivoa.xml.validator.ValidationResult;
import org.ivoa.xml.validator.XMLValidator;


/**
 * Facade Class to manage data model instance (xml documents & database representation)
 *
 * @author laurent bourges (voparis)
 */
public class DataModelManager extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** The URL where the schema can be found.<br> */
  public static final String SCHEMA_URL = RuntimeConfiguration.get().getRootSchemaURL();

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private String jpa_pu;
  /** TODO : Field Description */
  private XMLValidator validator;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   * 
   * @param jpaPU jpa persistence unit to use
   */
  public DataModelManager(final String jpaPU) {
    this.jpa_pu = jpaPU;
    this.validator = new XMLValidator(SCHEMA_URL);
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
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
   * TODO : Method Description
   *
   * @param stream
   *
   * @return value TODO : Value Description
   */
  private MetadataObject unmarshall(final InputStream stream) {
    return ModelFactory.getInstance().unmarshallToObject(new InputStreamReader(stream));
  }

  /**
   * TODO : Method Description
   *
   * @param filePath
   *
   * @return value TODO : Value Description
   */
  public boolean validate(final String filePath) {
    if (log.isInfoEnabled()) {
      log.info("DataModelManager.validate : " + filePath);
    }

    boolean res = false;

    final File file = FileUtils.getFile(filePath);

    if (file != null) {
      try {
        final InputStream in = new BufferedInputStream(new FileInputStream(file));

        final ValidationResult result = validateStream(in);

        res = result.isValid();

        if (! res) {
          for (final ErrorMessage em : result.getMessages()) {
            log.error(em.toString());
          }
        }
      } catch (final FileNotFoundException fnfe) {
        // already checked
      }
    }

    if (log.isInfoEnabled()) {
      log.info("DataModelManager.validate : " + res);
    }

    return res;
  }

  /**
   * TODO : Method Description
   *
   * @param in
   *
   * @return value TODO : Value Description
   */
  public ValidationResult validateStream(final InputStream in) {
    final ValidationResult result = validator.validate(in);

    return result;
  }

  /**
   * TODO : Method Description
   *
   * @param filePath
   * @param userName
   *
   * @return value TODO : Value Description
   */
  public MetadataObject load(final String filePath, final String userName) {
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
  public MetadataObject load(final InputStream stream, final String userName) {
    final JPAFactory jf = getJPAFactory();

    EntityManager    em = null;
    MetadataObject   o  = null;
    Long             id = null;

    try {
      em = jf.getEm();

      // sets EntityManager to ReferenceResolver Context :
      ReferenceResolver.initContext(em);

      o = unmarshall(stream);

      if (o instanceof MetadataRootEntityObject) // TODO enforce this ...
       {
        ((MetadataRootEntityObject) o).setOwner(userName);
        ((MetadataRootEntityObject) o).setUpdateUser(userName);
      }

      // starts TX :
      // starts transaction on snap database :
      log.warn("DataModelManager.load : starting TX ...");
      em.getTransaction().begin();

      em.persist(o);

      // finally : commits transaction on snap database :
      log.warn("DataModelManager.load : committing TX");
      em.getTransaction().commit();
      log.warn("DataModelManager.load : TX commited.");

      id = o.getId();
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
      // free resolver context (thread local) :
      ReferenceResolver.freeContext();

      if (em != null) {
        em.close();
      }
    }

    if (log.isWarnEnabled()) {
      log.warn("DataModelManager.load : exit : " + id);
    }

    return o;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
