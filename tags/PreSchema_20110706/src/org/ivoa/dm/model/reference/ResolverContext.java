package org.ivoa.dm.model.reference;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import org.ivoa.bean.LogSupport;
import org.ivoa.dm.model.MetadataObject;

/**
 * This class holds all unmarshalled metadata objects having an xmlId in order to allow ReferenceResolver to
 * resolve xsd:idref (Reference)
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class ResolverContext extends LogSupport {
  //~ Constants ------------------------------------------------------------------------------------------------------

  /** internal diagnostic FLAG */
  public static final boolean DIAGNOSTICS = false;

  /** starting capacity */
  public static final int CAPACITY = 256;

  //~ Members --------------------------------------------------------------------------------------------------------
  /** JPA entity manager in use to resolve external references (ivoId) */
  private EntityManager em = null;
  /** Map [xmlId - metadata object] filled by UnmarshallerEventListener to resolve local references (xmlId) */
  private final Map<String, MetadataObject> objects = new HashMap<String, MetadataObject>(CAPACITY);

  //~ Constructors ---------------------------------------------------------------------------------------------------
  /**
   * Protected Constructor
   */
  protected ResolverContext() {
    /* no-op */
  }

  //~ Methods --------------------------------------------------------------------------------------------------------
  /**
   * Clear the context
   */
  protected void clear() {
    if (DIAGNOSTICS && logB.isWarnEnabled()) {
      logB.warn("ResolverContext.clear : usage : " + this.objects.size() + " / " + CAPACITY);
    }

    // release the reference but do not close the entity manager :
    this.em = null;

    // force GC :
    this.objects.clear();
  }

  /**
   * Returns Map [xmlId - metadata object] to resolve local references (xmlId)
   *
   * @return Map [xmlId - metadata object]
   */
  protected Map<String, MetadataObject> getObjects() {
    return objects;
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
