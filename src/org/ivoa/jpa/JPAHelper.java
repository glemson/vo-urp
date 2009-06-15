package org.ivoa.jpa;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.ivoa.bean.LogSupport;
import org.ivoa.dm.model.Identity;
import org.ivoa.dm.model.MetadataObject;
import org.ivoa.util.JavaUtils;


/**
 * JPAHelper : JPA utility methods
 *
 * @author laurent bourges (voparis)
 */
public final class JPAHelper extends LogSupport {

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  private JPAHelper() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param em
   * @param type
   * @param publisherDID
   *
   * @return value TODO : Value Description
   */
  public static MetadataObject findItemByPublisherDID(final EntityManager em, final Class<?> type,
                                                      final String publisherDID) {
    if (! JavaUtils.isEmpty(publisherDID)) {
      return (MetadataObject) JPAHelper.findSingleByNamedQuery(
        em,
        type.getSimpleName() + ".findByPublisherDID",
        "publisherDID",
        publisherDID);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param em
   * @param queryName
   * @param paramName
   * @param value
   *
   * @return value TODO : Value Description
   */
  private static Query createNamedQuery(final EntityManager em, final String queryName, final String paramName,
                                        final Object value) {
    // throw a javax.persistence.NoResultException if no result :
    return em.createNamedQuery(queryName).setParameter(paramName, value);
  }

  /**
   * Finds a single result with the given NamedQuery (queryName) that have only a single parameter (paramName,
   * value)
   *
   * @param em entity Manager
   * @param queryName NamedQuery to use
   * @param paramName parameter Name
   * @param value parameter value to set
   *
   * @return Entity or null if not found
   */
  public static Object findSingleByNamedQuery(final EntityManager em, final String queryName, final String paramName,
                                              final Object value) {
    try {
      return createNamedQuery(em, queryName, paramName, value).getSingleResult();
    } catch (final NoResultException nre) {
      if (log.isDebugEnabled()) {
        log.debug(
          "JPAHelper : Unable to find any result for query " + queryName + " where " + paramName + " = " + value,
          nre);
      }
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param em
   * @param queryName
   * @param paramName
   * @param value
   *
   * @return value TODO : Value Description
   */
  public static Object freshFindExistingByNamedQuery(final EntityManager em, final String queryName,
                                                     final String paramName, final Object value) {
    final List<?> result = createNamedQuery(em, queryName, paramName, value).getResultList();

    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

  /**
   * TODO : Method Description
   *
   * @param em
   * @param queryName
   * @param paramName
   * @param value
   *
   * @return value TODO : Value Description
   */
  public static Object findExistingByNamedQuery(final EntityManager em, final String queryName, final String paramName,
                                                final Object value) {
    final List<?> result = createNamedQuery(em, queryName, paramName, value).getResultList();

    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

  /**
   * TODO : Method Description
   *
   * @param em
   * @param queryName
   * @param paramName1
   * @param value1
   * @param paramName2
   * @param value2
   *
   * @return value TODO : Value Description
   */
  public static Object findExistingByNamedQuery(final EntityManager em, final String queryName,
                                                final String paramName1, final Object value1, final String paramName2,
                                                final Object value2) {
    final List<?> result = createNamedQuery(em, queryName, paramName1, value1).setParameter(paramName2, value2)
                          .getResultList();

    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

  /**
   * TODO : Method Description
   *
   * @param em
   * @param queryName
   * @param paramName1
   * @param value1
   * @param paramName2
   * @param value2
   *
   * @return value TODO : Value Description
   */
  public static Object findFirstExistingByNamedQuery(final EntityManager em, final String queryName,
                                                     final String paramName1, final Object value1,
                                                     final String paramName2, final Object value2) {
    final List<?> result = createNamedQuery(em, queryName, paramName1, value1).setParameter(paramName2, value2)
                          .setMaxResults(1).getResultList();

    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

  /**
   * TODO : Method Description
   *
   * @param em
   * @param type
   * @param ivoId
   *
   * @return value TODO : Value Description
   */
  public static MetadataObject findItemByIvoId(final EntityManager em, final Class<?> type, final String ivoId) {
    if (! JavaUtils.isEmpty(ivoId)) {
      Long id = Identity.resolveIvoId(ivoId, type);

      if (id == null) {
        return null;
      }

      return (MetadataObject) JPAHelper.findSingleByNamedQuery(em, type.getSimpleName() + ".findById", "id", id);
    }

    return null;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
