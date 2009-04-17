package org.ivoa.dm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.ReferenceResolver;
import org.ivoa.jpa.JPAFactory;
import org.ivoa.util.FileUtils;
import org.ivoa.util.LogUtil;
import org.ivoa.xml.ErrorMessage;
import org.ivoa.xml.ValidationResult;
import org.ivoa.xml.XMLValidator;

/**
 * Facade Class to manage data model instance (xml documents & database representation)
 *
 * @author laurent bourges (voparis)
 */
public class DataModelManager {
  
  /** logger */
  public final static Log log = LogUtil.getLogger();
  
  /**
   * The URL where the schema can be found.<br/>
   */
  public final static String SCHEMA_URL = RuntimeConfiguration.getInstance().getRootSchemaURL();

  private String jpa_pu;
  /**
   * Constructor
   */
  public DataModelManager(String jpa_pu) {
	  this.jpa_pu = jpa_pu;
  }

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

  private MetadataObject unmarshall(final String filePath) {
    return ModelFactory.getInstance().unmarshallToObject(filePath);
  }

  private MetadataObject unmarshall(final InputStream stream) {
    return ModelFactory.getInstance().unmarshallToObject(new InputStreamReader(stream));
  }
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

        if (!res) {
          for (ErrorMessage em : result.getMessages()) {
            log.error(em.toString());
          }
        }

      } catch (FileNotFoundException fnfe) {
        // already checked
      }
    }
    
    if (log.isInfoEnabled()) {
      log.info("DataModelManager.validate : " + res);
    }
    return res;
  }
  
  public ValidationResult validateStream(final InputStream in) {
    final XMLValidator validator = new XMLValidator(SCHEMA_URL);

    final ValidationResult result = validator.validate(in);

    return result;
  }
  
  
  
  public MetadataObject load(final String filePath) {
    if (log.isInfoEnabled()) {
      log.info("DataModelManager.load : " + filePath);
    }
    InputStream stream = null;
    try
    {
      stream = new FileInputStream(filePath);
      return load(stream);
    }
    catch(IOException e)
    {
      log.error("DataModelManager.load : runtime failure : ", e);
    }
    return null;  
  }
    
    public MetadataObject load(final InputStream stream)
    {
      
    final JPAFactory jf = getJPAFactory();

    EntityManager em = null;
    MetadataObject o = null;
    Long          id = null;

    try {
      em = jf.getEm();

      // sets EntityManager to ReferenceResolver Context :
      ReferenceResolver.initContext(em);
      
      o = unmarshall(stream);
      
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
      if (em.getTransaction().isActive()) {
        log.warn("DataModelManager.load : rollbacking TX ...");
        em.getTransaction().rollback();
        log.warn("DataModelManager.load : TX rollbacked.");
      }

      throw re;
    } finally {
      // free resolver context (thread local) :
      ReferenceResolver.freeContext();
      
      em.close();
    }

    if (log.isWarnEnabled()) {
      log.warn("DataModelManager.load : exit : "+id);
    }
    
    return o;
  }
  
}
