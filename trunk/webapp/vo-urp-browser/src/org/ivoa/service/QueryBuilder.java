package org.ivoa.service;

import org.ivoa.web.model.EntityConfig;


/**
 * QueryBuilder.java
 *
 * Created on 29 août 2007, 15:41:22
 *
 * @author laurent
 */
public class QueryBuilder {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final String SELECT_ITEM  = "SELECT item FROM ";
  /**
   * TODO : Field Description
   */
  public static final String SELECT_COUNT = "SELECT COUNT(item) FROM ";
  /**
   * TODO : Field Description
   */
  public static final String ITEM = " item ";
  /**
   * TODO : Field Description
   */
  public static final String ORDER_BY = " ORDER BY ";

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new QueryBuilder object
   */
  private QueryBuilder() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param ec 
   *
   * @return value TODO : Value Description
   */
  public static String computeFindAll(final EntityConfig ec) {
    String query                          = SELECT_ITEM + ec.getName() + ITEM;

    if (ec.getOrderBy() != null) {
      query += (ORDER_BY + ec.getOrderBy() + " ");
    }

    return query;
  }

  /**
   * TODO : Method Description
   *
   * @param ec 
   *
   * @return value TODO : Value Description
   */
  public static String computeCountAll(final EntityConfig ec) {
    return SELECT_COUNT + ec.getName() + ITEM;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
