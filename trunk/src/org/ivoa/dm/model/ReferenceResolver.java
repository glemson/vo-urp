package org.ivoa.dm.model;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.jpa.JPAHelper;
import org.ivoa.util.JavaUtils;


/**
 * This class manages xmlId & ivoId references and looks up for corresponding items in memory (JAXB unmarshalling
 * ID / IDREF) or in database (ivoId)
 *
 * @author laurent bourges (voparis)
 */
public final class ReferenceResolver extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * serial UID for Serializable interface : every concrete class must have its value corresponding to last
   * modification date of the UML model
   */
  private static final long serialVersionUID = 1L;
  /** Identity / Reference resolver singleton */
  private static final ReferenceResolver resolver = new ReferenceResolver();
  /** singleton : thread local contexts */
  private static final ResolverThreadLocal localContext = new ResolverThreadLocal();

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden constructor
   */
  private ReferenceResolver() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Returns this ReferenceResolver singleton
   *
   * @return ReferenceResolver singleton
   */
  public static ReferenceResolver getInstance() {
    return resolver;
  }

  /**
   * Defines the entity manager for the ResolverContext
   *
   * @param em entity manager
   */
  public static final void initContext(final EntityManager em) {
    localContext.getContext().setEm(em);
  }

  /**
   * Free the ResolverContext associated with the current thread
   */
  public static final void freeContext() {
    localContext.remove();
  }

  /**
   * Gets the ResolverContext associated with the current thread
   *
   * @return ResolverContext associated with the current thread
   */
  protected static final ResolverContext currentContext() {
    return localContext.getContext();
  }

  /**
   * Resolve a given reference with the given type using the ResolverContext associated with the current thread
   *
   * @param reference reference to resolve
   * @param type reference type
   *
   * @return metadata object if resolved or null if the reference has no identifier (xmlId or ivoId)
   *
   * @throws IllegalStateException if the xmlId reference does not exist or the ivoId does not exist in the database
   */
  public static MetadataObject resolve(final Reference reference, final Class<?extends MetadataObject> type) {
    if (log.isInfoEnabled()) {
      log.info("ReferenceResolver.resolve : enter : " + reference + " [" + type.getSimpleName() + "]");
    }

    //    final Identity ref = reference.getXmlIdentity();
    if (! JavaUtils.isEmpty(reference.getXmlId())) {
      final MetadataObject res = currentContext().getContext().get(reference.getXmlId());

      if (res == null) {
        throw new IllegalStateException(
          "unable to resolve reference : " + reference.toString() + " with the context : " + currentContext(),
          new Throwable());
      }

      if (log.isInfoEnabled()) {
        log.info("ReferenceResolver.resolve : exit : " + reference + " <=> " + res);
      }

      return res;
    }

    if (! JavaUtils.isEmpty(reference.getIvoId())) {
      final EntityManager em = currentContext().getEm();

      String ivoId = reference.getIvoId();
      if(ivoId.startsWith(Configuration.getInstance().getIVOIdPrefix())) {
        ivoId = ivoId.substring(Configuration.getInstance().getIVOIdPrefix().length());
      }
      // EntityManager must be defined to be able to resolve ivoId references :
      if (em != null) {
        final MetadataObject res = JPAHelper.findItemByIvoId(em, type, ivoId);

        if (res == null) {
          throw new IllegalStateException(
            "unable to resolve reference : " + reference.toString() + " in the database",
            new Throwable());
        }

        if (log.isInfoEnabled()) {
          log.info("ReferenceResolver.resolve : exit : " + reference + " <=> " + res);
        }

        return res;
      }
    }

    if (! JavaUtils.isEmpty(reference.getPublisherDID())) {
      final EntityManager em = currentContext().getEm();

      // EntityManager must be defined to be able to resolve ivoId references :
      if (em != null) {
        final MetadataObject res = JPAHelper.findItemByPublisherDID(em, type, reference.getPublisherDID());

        if (res == null) {
          throw new IllegalStateException(
            "unable to resolve reference : " + reference.toString() + " in the database",
            new Throwable());
        }

        if (log.isInfoEnabled()) {
          log.info("ReferenceResolver.resolve : exit : " + reference + " <=> " + res);
        }

        return res;
      }
    }
    return null;
  }

  /**
   * Adds this metadata object to the ResolverContext if it has a value for the xmlId field
   *
   * @param object metadata object to process
   */
  public static void addInContext(final MetadataObject object) {
    final Identity old = object.getIdentityField();

    if (old != null) {
      // means at least xmlId or ivoId are not null :
      final String xmlId = old.getXmlId();

      if (! JavaUtils.isEmpty(xmlId)) {
        if (log.isInfoEnabled()) {
          log.info("ReferenceResolver.addInContext : " + xmlId + " <=> " + object);
        }

        currentContext().getContext().put(xmlId, object);
      }
    }
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  /**
   * This class uses the ThreadLocal pattern to associate a ResolverContext to the current thread
   */
  protected static final class ResolverThreadLocal extends ThreadLocal<ResolverContext> {
    //~ Constructors ---------------------------------------------------------------------------------------------------

/**
     * Protected constructor
     */
    protected ResolverThreadLocal() {
      super();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Creates a new ResolverContext for the current thread
     *
     * @return new ResolverContext instance
     */
    @Override
    public ResolverContext initialValue() {
      return new ResolverContext();
    }

    /**
     * Returns the current ResolverContext for the current thread (creates a new one if needed)
     *
     * @return ResolverContext instance associated with the current thread
     */
    protected final ResolverContext getContext() {
      return super.get();
    }
  }

  /**
   * This class holds all unmarshalled metadata objects having an xmlId in order to allow ReferenceResolver to
   * resolve xsd:idref (Reference)
   */
  protected static final class ResolverContext {
    //~ Constants ------------------------------------------------------------------------------------------------------

    /** starting capacity */
    public static final int CAPACITY = 64;

    //~ Members --------------------------------------------------------------------------------------------------------

    /** JPA entity manager in use to resolve external references (ivoId) */
    private EntityManager em = null;
    /** Map [xmlId - metadata object] filled by UnmarshallerEventListener to resolve local references (xmlId) */
    private final Map<String, MetadataObject> context = new HashMap<String, MetadataObject>(CAPACITY);

    //~ Constructors ---------------------------------------------------------------------------------------------------

/**
     * Protected Constructor
     */
    protected ResolverContext() {
      /* no-op */
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Returns Map [xmlId - metadata object] to resolve local references (xmlId)
     *
     * @return Map [xmlId - metadata object]
     */
    protected Map<String, MetadataObject> getContext() {
      return context;
    }

    /**
     * Returns the JPA entity manager in use to resolve external references
     *
     * @return JPA entity manager
     */
    protected EntityManager getEm() {
      return em;
    }

    /**
     * Sets the JPA entity manager to use to resolve external references
     *
     * @param pEm JPA entity manager
     */
    protected void setEm(final EntityManager pEm) {
      this.em = pEm;
    }
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
