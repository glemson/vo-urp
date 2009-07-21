package org.ivoa.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.CommonsLoggingSessionLog;
import org.eclipse.persistence.sessions.Session;
import org.ivoa.bean.SingletonSupport;
import org.ivoa.conf.PropertyHolder;
import org.ivoa.util.CollectionUtils;

/**
 * JPAFactory is an utility class to configure JPA Connection & properties
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class JPAFactory extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** Table definition checker : seems not working with postgres JDBC driver */
  public static final boolean USE_INTEGRITY_CHECKER = false;
  /** Default config file */
  public static final String CONFIG_FILE = "jpa-config.properties";
  /** all factories */
  private static ConcurrentHashMap<String, JPAFactory> managedInstances = new ConcurrentHashMap<String, JPAFactory>(4);

  //~ Members ----------------------------------------------------------------------------------------------------------
  /** persistence Unit Label */
  private final String pu;
  /** config file name */
  private final String config;
  /** JPA factory */
  private EntityManagerFactory emf = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Creates a new JPAFactory object
   *
   * @param pPU persistence unit name
   */
  private JPAFactory(final String pPU) {
    this(pPU, CONFIG_FILE);
  }

  /**
   * Creates a new JPAFactory object
   *
   * @param pPU persistence unit name
   * @param fileName config file name
   */
  private JPAFactory(final String pPU, final String fileName) {
    this.pu = pPU;
    this.config = fileName;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Factory singleton per pu pattern
   *
   * @param pu persistence unit name
   *
   * @return JPAFactory initialized
   */
  public static final JPAFactory getInstance(final String pu) {
    JPAFactory jf = managedInstances.get(pu);

    if (jf == null) {
      if (logB.isInfoEnabled()) {
        logB.info("JPAFactory.getInstance : creating new instance for : " + pu);
      }
      jf = prepareInstance(new JPAFactory(pu));

      if (jf != null) {
        managedInstances.putIfAbsent(pu, jf);
        // to be sure to return the singleton :
        jf = managedInstances.get(pu);
      }
    }

    return jf;
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
      logB.info("JPAFactory.clearStaticReferences : enter");
    }
    // reset managed instances :
    if (managedInstances != null) {
      managedInstances.clear();
      managedInstances = null;
    }

    // clean up custom logger instances :
    CommonsLoggingSessionLog.onExit();

    if (logB.isInfoEnabled()) {
      logB.info("JPAFactory.clearStaticReferences : exit");
    }
  }

  /**
   * Concrete implementations of the SingletonSupport's clear() method :<br/>
   * Callback to clean up this SingletonSupport instance iso clear instance fields<br/>
   * Ends the JAXB Context
   *
   * @see SingletonSupport#clear()
   */
  @Override
  protected void clear() {
    if (logB.isInfoEnabled()) {
      logB.info("JPAFactory.clear : enter : " + this.pu);
    }
    if (getEmf() != null) {
      try {
        getEmf().close();
      } catch (final Exception e) {
        log.error("JPAFactory.clear : failure : ", e);
      }

      this.emf = null;
    }
  }

  /**
   * Concrete implementations of the SingletonSupport's initialize() method :<br/>
   * Callback to initialize this SingletonSupport instance<br/>
   * Initializes the EntityManagerFactory
   *
   * @see SingletonSupport#initialize()
   *
   * @throws IllegalStateException if a problem occured
   */
  @Override
  protected void initialize() throws IllegalStateException {
    if (log.isDebugEnabled()) {
      log.debug("JPAFactory.initialize : enter : " + this.pu + " with configuration file : " + this.config);
    }

    final Properties properties = new PropertyHolder(this.config).getProperties();

    try {
      final Map<Object, Object> props = new HashMap<Object, Object>(properties);

      if (log.isInfoEnabled()) {
        log.info("JPAFactory.initialize : properties : " + CollectionUtils.toString(props));
      }

      // adds integrity checker for postgres only :
      final String targetDB = properties.getProperty(PersistenceUnitProperties.TARGET_DATABASE);

      if (logB.isInfoEnabled()) {
        logB.info("JPAFactory.initialize : connecting to " + targetDB + " ...");
      }

      if (USE_INTEGRITY_CHECKER) {
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, EnableIntegrityChecker.class.getName());
      }

      this.emf = Persistence.createEntityManagerFactory(this.pu, props);
    } catch (final RuntimeException re) {
      log.error("JPAFactory.initialize : EntityManagerFactory failure : ", re);
      throw re;
    }

    if (logB.isInfoEnabled()) {
      logB.info("JPAFactory.initialize : done.");
    }

    EntityManager em = null;

    try {
      // Create an EntityManager to check if connection is ready & fails fast.
      if (logB.isInfoEnabled()) {
        logB.info("JPAFactory.initialize : test to create an entityManager instance ...");
      }

      em = getEm();

      if (logB.isInfoEnabled()) {
        logB.info("JPAFactory.initialize : test : OK");
      }
    } finally {
      if (em != null) {
        em.close();
      }
    }

    if (logB.isInfoEnabled()) {
      logB.info("JPAFactory.initialize : exit : OK");
    }
  }

  /**
   * Returns the persistence Unit ID
   *
   * @return persistence Unit ID
   */
  public String getPu() {
    return pu;
  }

  /**
   * Returns the EntityManagerFactory
   *
   * @return EntityManagerFactory
   */
  public EntityManagerFactory getEmf() {
    return emf;
  }

  /**
   * Creates a new EntityManager from factory
   *
   * @return EntityManager
   */
  public EntityManager getEm() {
    return getEmf().createEntityManager();
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------
  /**
   * SessionCustomizer implementation to check Database schema at runtime
   */
  public static final class EnableIntegrityChecker implements SessionCustomizer {
    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Customize the session to add the IntegrityChecker component
     * @param session eclipselink session
     */
    public final void customize(final Session session) {
      session.getIntegrityChecker().checkDatabase();
      session.getIntegrityChecker().setShouldCatchExceptions(true);
      session.getIntegrityChecker().setShouldCheckInstantiationPolicy(true);
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
