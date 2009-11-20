package org.ivoa.dm.model.reference;

import javax.persistence.EntityManager;

import org.ivoa.bean.SingletonSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.dm.model.Identity;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.dm.model.Reference;
import org.ivoa.jpa.JPAHelper;
import org.ivoa.util.JavaUtils;
import org.ivoa.util.concurrent.ThreadLocalUtils;

/**
 * This class manages xmlId & ivoId references and looks up for corresponding items in memory (JAXB unmarshalling
 * ID / IDREF) or in database (ivoId)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ReferenceResolver extends SingletonSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** singleton instance (java 5 memory model) */
  private static ReferenceResolver instance = null;
  /** context thread Local */
  private static ThreadLocal<ResolverContext> resolverThreadLocal = ThreadLocalUtils.registerRequestThreadLocal(new ResolverThreadLocal());
  /** local copy of Configuration.IVOIdPrefix */
  private static final String IVOIdPrefix = Configuration.getInstance().getIVOIdPrefix();

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Forbidden constructor
   */
  private ReferenceResolver() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * Return the ReferenceResolver singleton instance
   *
   * @return ReferenceResolver singleton instance
   *
   * @throws IllegalStateException if a problem occured
   */
  public static final ReferenceResolver getInstance() {
    if (instance == null) {
      instance = prepareInstance(new ReferenceResolver());
    }
    return instance;
  }

  /**
   * Concrete implementations of the SingletonSupport's clearStaticReferences() method :<br/>
   * Callback to clean up the possible static references used by this SingletonSupport instance iso
   * clear static references
   *
   * @see SingletonSupport#clearStaticReferences()
   */
  @Override
  protected void clearStaticReferences() {
    resolverThreadLocal = null;
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
  public static MetadataObject resolve(final Reference reference, final Class<? extends MetadataObject> type) {
    if (log.isInfoEnabled()) {
      log.info("ReferenceResolver.resolve : enter : " + reference + " [" + type.getSimpleName() + "]");
    }

    //    final Identity ref = reference.getXmlIdentity();
    if (!JavaUtils.isEmpty(reference.getXmlId())) {
      final MetadataObject res = currentContext().getObjects().get(reference.getXmlId());

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

    if (!JavaUtils.isEmpty(reference.getIvoId())) {
      final EntityManager em = currentContext().getEm();

      String ivoId = reference.getIvoId();
      if (ivoId.startsWith(IVOIdPrefix)) {
        ivoId = ivoId.substring(IVOIdPrefix.length());
      }
      // EntityManager must be defined to be able to resolve ivoId references :
      if (em == null) {
        throw new IllegalStateException(
                "unable to resolve any reference because the entity manager is undefined", new Throwable());
      }
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

    if (!JavaUtils.isEmpty(reference.getPublisherDID())) {
      final EntityManager em = currentContext().getEm();

      // EntityManager must be defined to be able to resolve ivoId references :
      if (em == null) {
        throw new IllegalStateException(
                "unable to resolve any reference because the entity manager is undefined", new Throwable());
      }

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
    return null;
  }

  /**
   * Adds this metadata object to the ResolverContext if it has a value for the xmlId field
   *
   * @param object metadata object to process
   */
  public static void addInContext(final MetadataObject object) {
    final Identity identity = object.getIdentityField();

    if (identity != null) {
      // means at least xmlId or ivoId are not null :
      final String xmlId = identity.getXmlId();

      if (!JavaUtils.isEmpty(xmlId)) {
        if (log.isInfoEnabled()) {
          log.info("ReferenceResolver.addInContext : " + xmlId + " <=> " + object);
        }

        currentContext().getObjects().put(xmlId, object);
      }
    }
  }

  /**
   * Defines the entity manager for the ResolverContext
   *
   * @param em entity manager
   */
  public static final void initContext(final EntityManager em) {
    currentContext().setEm(em);
  }

  /**
   * Gets the ResolverContext associated with the current thread
   *
   * @return ResolverContext associated with the current thread
   */
  protected static final ResolverContext currentContext() {
    return resolverThreadLocal.get();
  }

  /**
   * Free the ResolverContext associated with the current thread
   */
  public static final void freeContext() {
    resolverThreadLocal.remove();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
