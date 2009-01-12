/*
 * QueryBuilder.java
 *
 * Created on 29 août 2007, 15:41:22
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ivoa.service;

import org.ivoa.web.model.EntityConfig;


/**
 *
 * @author laurent
 */
public class QueryBuilder {

  // distinct item
  public static final String SELECT_ITEM = "SELECT item FROM ";
  public static final String SELECT_COUNT = "SELECT COUNT(item) FROM ";
  public static final String ITEM = " item ";

  public static final String ORDER_BY = " ORDER BY ";


  private QueryBuilder() {
  }

  public static String computeFindAll(final EntityConfig ec) {
    String query = SELECT_ITEM + ec.getName() + ITEM;
    if (ec.getOrderBy() != null) {
      query += ORDER_BY + ec.getOrderBy() + " ";
    }
    return query;
  }

  public static String computeCountAll(final EntityConfig ec) {
    return SELECT_COUNT + ec.getName() + ITEM;
  }
}
