package org.ivoa.web.model;

import java.util.HashMap;
import java.util.Map;
import org.ivoa.dm.model.MetadataElement;
import org.ivoa.service.QueryBuilder;

/**
 * Generic Access to DataBase SNAP
 * @author laurent
 */
public class EntityConfig {

  public static final String EXT_JSP = ".jsp";

  public static final String PATH_LIST = "list/";
  public static final String PATH_SHOW = "show/";
  public static final String PATH_PAGE = "page/";

  // members :          
  /** class */
  private final Class<? extends MetadataElement> classe;
  
  /** use paging on pages */
  private final boolean doPaging;

  /** allow custom query on pages */
  private final boolean doQuery;
  
  /** orderBy clause */
  private String orderBy;

  // computed fields :
  private String countAll;

  private String findAll;

  /** name  : 'Snapexperiment' */
  private final String name;

  // String keys :
  /** 'Snapexperiment_Count' or null if no paging */
  private String globalCountKey = null;

  /** 'Snapexperiment_Cursor' */
  private String sessionCursorKey;
  /** 'Snapexperiment_ID' */
  private String sessionItemKey;
  
  /** 'ListSnapexperiment.jsp' */
  private String pageList;
  /** 'ShowSnapexperiment.jsp' */
  private String pageItem;
  
  private Map<String,String> pages;

  /**
   * Constructor
   * @param classe type class
   */
  public EntityConfig(final Class<? extends MetadataElement> classe) {
    this(classe, true, false);
  }

  public EntityConfig(final Class<? extends MetadataElement> classe, final boolean allowPaging, final boolean allowQuery) {
    this.classe = classe;
    this.name = classe.getSimpleName();

    this.doPaging = allowPaging;
    this.doQuery = allowQuery;
  }
  
  protected void init() {
    this.findAll = QueryBuilder.computeFindAll(this);
    this.countAll = QueryBuilder.computeCountAll(this);

    if (this.doPaging) {
      this.globalCountKey = this.name + "_Count";
    }

    this.sessionCursorKey = this.name + "_Cursor";
    this.sessionItemKey = this.name + "_ID";

//    this.pageList = PATH_LIST + "List" + this.name + EXT_JSP;
    
    this.pageList = PATH_LIST + "ListGeneric" + EXT_JSP;    
    this.pageItem = PATH_SHOW + "ShowGeneric" + EXT_JSP;
  }
  
  public void addPage(final String name, final String viewName) {
    if (pages == null) {
      pages = new HashMap<String,String>();
    }
    pages.put(name, PATH_PAGE + viewName + EXT_JSP);
  }

// Getters :

  public Class<? extends MetadataElement> getClasse() {
    return classe;
  }
  
  public String getName() {
    return name;
  }

  public boolean isDoPaging() {
    return doPaging;
  }

  public boolean isDoQuery() {
    return doQuery;
  }

  public String getCountAll() {
    return countAll;
  }

  public String getFindAll() {
    return findAll;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }
  
// Keys :  

  public String getGlobalCountKey() {
    return globalCountKey;
  }

  public String getPageItem() {
    return pageItem;
  }

  public String getPageList() {
    return pageList;
  }

  public String getPages(final String name) {
    if (pages == null) {
      return null;
    }
    return pages.get(name);
  }

  public String getSessionCursorKey() {
    return sessionCursorKey;
  }

  public String getSessionItemKey() {
    return sessionItemKey;
  }
}
