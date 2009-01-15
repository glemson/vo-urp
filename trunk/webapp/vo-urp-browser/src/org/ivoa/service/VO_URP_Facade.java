package org.ivoa.service;

import org.apache.commons.logging.Log;

import org.ivoa.conf.RuntimeConfiguration;
import org.ivoa.dm.MetaModelFactory;

import org.ivoa.jpa.JPAFactory;

import org.ivoa.util.CollectionUtils;
import org.ivoa.util.LogUtil;

import org.ivoa.web.model.CursorQuery;
import org.ivoa.web.model.EntityConfig;
import org.ivoa.web.model.EntityConfigFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.ivoa.env.ClassLoaderCleaner;


/**
 * 
DOCUMENT ME!
 *
 * @author laurent
 */
public class VO_URP_Facade implements ServletContextListener {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final String CTX_KEY       = "VO_URP_Facade";

  /**
   * TODO : Field Description
   */
  public static final int DEF_PAGE_SIZE = 10;

  /**
   * TODO : Field Description
   */
  private static VO_URP_Facade instance = null;

  /** Logger for this class and subclasses */
  protected static final Log log = LogUtil.getLogger();

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private EntityConfigFactory ecf;

  /** EntityManager factory */
  private EntityManagerFactory emf;
  
  /** EntityManager Thread Local (lazy weaving) */
  private ThreadLocal<EntityManager> threadLocal;


  // global Cache (thread safe) :
  /**
   * TODO : Field Description
   */
  private Map globalCache = new ConcurrentHashMap();

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Constructor
   */
  public VO_URP_Facade() {
    this.threadLocal = new ThreadLocal<EntityManager>();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public static final VO_URP_Facade getInstance() {
    return instance;
  }

  // Init :
  /**
   * TODO : Method Description
   *
   * @param sce 
   */
  public void contextInitialized(final ServletContextEvent sce) {
    if (log != null) {
      log.warn("Application is starting ...");
    }

    instance = this;

    log.error("System.properties : ");
    log.error(CollectionUtils.toString(System.getProperties()));

    final ServletContext ctx = sce.getServletContext();

    ctx.setAttribute(CTX_KEY, this);

    prepare();
  }

  /**
   * TODO : Method Description
   */
  private void prepare() {
    if (log.isInfoEnabled()) {
//      log.info("get MetaModelFactory and load model : " + MetaModelFactory.MODEL_FILE);
    }

    try
    {
    MetaModelFactory.getInstance();
    }catch(IllegalStateException e){
    	e.printStackTrace();
    	throw e;
    }

    if (log.isInfoEnabled()) {
      log.info("DBTests.testJAXB : get MetaModelFactory : OK");
    }

    this.ecf = EntityConfigFactory.getInstance();
    String jpa_pu = RuntimeConfiguration.getInstance().getJPAPU();

    if (log.isInfoEnabled()) {
      log.info("get JPAFactory for persistence unit "+jpa_pu);
    }

    final JPAFactory jf = JPAFactory.getInstance(jpa_pu);

    if (log.isInfoEnabled()) {
      log.info("get JPAFactory for persistence unit "+jpa_pu+" : OK");
    }

    EntityManager em = null;

    try {
      if (log.isInfoEnabled()) {
        log.info("get EntityManager ...");
      }
      em = jf.getEm();

      if (log.isInfoEnabled()) {
        log.info("get EntityManager : OK");
      }
    } finally {
      if (em != null) {
        em.close();
      }
    }

    this.emf = jf.getEmf();

    if (log != null) {
      log.warn("Application is ready.");
    }
  }

  // Exit :
  /**
   * TODO : Method Description
   *
   * @param sce 
   */
  public void contextDestroyed(final ServletContextEvent sce) {
    log.info("VO_URP_Facade.contextDestroyed : enter");
    if (log != null) {
      log.warn("Application is stopping ...");
    }

    sce.getServletContext().removeAttribute(CTX_KEY);
    
    instance = null;
    
    // free Session stats Thread :
    SessionMonitor.onExit();
    
    EntityConfigFactory.clean();
    
    if (log != null) {
      log.warn("Application is unavailable.");
    }
    log.info("VO_URP_Facade.contextDestroyed : exit");
    
    // last one (clear logging) :
    ClassLoaderCleaner.clean();
  }

  // Utilities --------------
  
  public EntityManager createEntityManager() {
    EntityManager em = threadLocal.get();
    if (em == null) {
      em = doCreateEntityManager();
    }
    return em;
  }

  private EntityManager doCreateEntityManager() {

    final EntityManager em = emf.createEntityManager();
    threadLocal.set(em);

    if (log.isInfoEnabled()) {
      log.info("doCreateEntityManager : instance created : " + em);
    }
    return em;
  }

  public void closeEntityManager() {
    EntityManager em = threadLocal.get();
    if (em != null) {
      // free thread-local first
      threadLocal.set(null);
      if (em.isOpen()) {
        if (log.isInfoEnabled()) {
          log.info("doCreateEntityManager : instance closed : " + em);
        }
        em.close();
      } else {
        if (log.isInfoEnabled()) {
          log.info("doCreateEntityManager : instance already closed : " + em);
        }
      }
    }
  }
  
  /**
   * Find an entity given its identifier and its class.
   * Used by FindServlet
   *
   * @param id identifier
   * @param c class of the entity
   *
   * @return value TODO : Value Description
   */
  public Object getItem(final Long id, final Class c) {
    final EntityManager em = createEntityManager();

    return em.find(c, id);
    // close is defered in JPARequestListener
  }

  /**
   * TODO : Method Description
   *
   * @param query 
   *
   * @return value TODO : Value Description
   */
  public Integer getCount(final String query) {
    final EntityManager em = createEntityManager();

    final Long c = (Long) em.createQuery(query).getSingleResult();

    return Integer.valueOf(c.intValue());
    // close is defered in JPARequestListener
  }

  /**
   * TODO : Method Description
   *
   * @param cq 
   */
  private void refreshCursorResults(final CursorQuery cq) {
    List results = null;

    final EntityManager em = createEntityManager();
    final Query q = em.createQuery(cq.getQuery());

    if (cq.isDoPaging()) {
      if (log.isDebugEnabled()) {
        log.debug("refreshCursor : from : " + cq.getStartPos() + " - len : " + cq.getPageSize());
      }

      if (cq.getStartPos() >= 0) {
        q.setFirstResult(cq.getStartPos());
      }
      if (cq.getPageSize() > 0) {
        q.setMaxResults(cq.getPageSize());
      }

    } else {
      if (log.isDebugEnabled()) {
        log.debug("refreshCursor : getall");
      }
    }
      results = q.getResultList();

    if (log.isDebugEnabled()) {
      log.debug("refreshCursor : size : " + results.size());
    }

    cq.setResults(results);
    // close is defered in JPARequestListener
  }

  // CursorQuery  
  /**
   * TODO : Method Description
   *
   * @param ec 
   *
   * @return value TODO : Value Description
   */
  private Integer getCachedCount(final EntityConfig ec) {
    Integer c = (Integer) getGlobalCache().get(ec.getGlobalCountKey());

    if (c == null) {
      // first time :
      c = getCount(ec.getCountAll());

      if (log.isDebugEnabled()) {
        log.debug("getCount : count : " + c);
      }

// disable global cache :      
//      getGlobalCache().put(ec.getGlobalCountKey(), c);
    }

    return c;
  }

  /**
   * TODO : Method Description
   *
   * @param ec 
   *
   * @return value TODO : Value Description
   */
  public CursorQuery createCursor(final EntityConfig ec) {
    CursorQuery cq = null;

    if (log.isInfoEnabled()) {
      log.info("createCursor : query : " + ec.getFindAll());
    }

    if (ec.isDoPaging()) {
      cq = new CursorQuery(ec.getFindAll(), DEF_PAGE_SIZE, getCachedCount(ec).intValue());
    } else {
      cq = new CursorQuery(ec.getFindAll());
    }

    cq.setDoQuery(ec.isDoQuery());

    return cq;
  }

  /**
   * TODO : Method Description
   *
   * @param ec 
   * @param cq 
   */
  public void refreshCursor(final EntityConfig ec, final CursorQuery cq) {
    if (cq.isRefreshCount()) {
      // refresh count :
      String query = ec.getCountAll();
      if (cq.getQueryClause() != null) {
        query += cq.getQueryClause();
      }

      if (log.isDebugEnabled()) {
        log.debug("countQuery : " + query);
      }

      cq.setMaxPos(getCount(query));

      // update Query :
      query = ec.getFindAll();
      if (cq.getQueryClause() != null) {
        query += cq.getQueryClause();
      }      
      cq.setQuery(query);
    }

    refreshCursorResults(cq);
  }

  // Specific code :
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Map getGlobalCache() {
    return globalCache;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public EntityConfigFactory getEntityConfigFactory() {
    return ecf;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
