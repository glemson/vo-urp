package org.ivoa.web.model;

import org.ivoa.dm.model.MetadataElement;

import org.ivoa.service.QueryBuilder;

import java.util.HashMap;
import java.util.Map;


/**
 * Generic Access to DataBase SNAP
 *
 * @author laurent
 */
public class EntityConfig {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final String EXT_JSP   = ".jsp";
  /**
   * TODO : Field Description
   */
  public static final String PATH_LIST = "list/";
  /**
   * TODO : Field Description
   */
  public static final String PATH_SHOW = "show/";
  /**
   * TODO : Field Description
   */
  public static final String PATH_PAGE = "page/";

  //~ Members ----------------------------------------------------------------------------------------------------------

  // members :          
  /** class */
  private final Class<?extends MetadataElement> classe;
  /** use paging on pages */
  private final boolean doPaging;
  /** allow custom query on pages */
  private final boolean doQuery;
  /** orderBy clause */
  private String orderBy;

  // computed fields :
  /**
   * TODO : Field Description
   */
  private String countAll;
  /**
   * TODO : Field Description
   */
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
  /**
   * TODO : Field Description
   */
  private Map<String, String> pages;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   * @param pClasse type class
   */
  public EntityConfig(final Class<?extends MetadataElement> pClasse) {
    this(pClasse, true, false);
  }

  /**
   * Creates a new EntityConfig object
   *
   * @param pClasse type class
   * @param allowPaging 
   * @param allowQuery 
   */
  public EntityConfig(final Class<?extends MetadataElement> pClasse, final boolean allowPaging, final boolean allowQuery) {
    this.classe = pClasse;
    this.name = pClasse.getSimpleName();

    this.doPaging = allowPaging;
    this.doQuery = allowQuery;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   */
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

  /**
   * TODO : Method Description
   *
   * @param pName 
   * @param viewName 
   */
  public void addPage(final String pName, final String viewName) {
    if (pages == null) {
      pages = new HashMap<String, String>();
    }

    pages.put(pName, PATH_PAGE + viewName + EXT_JSP);
  }

  // Getters :
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Class<?extends MetadataElement> getClasse() {
    return classe;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getName() {
    return name;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isDoPaging() {
    return doPaging;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public boolean isDoQuery() {
    return doQuery;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getCountAll() {
    return countAll;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getFindAll() {
    return findAll;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getOrderBy() {
    return orderBy;
  }

  /**
   * TODO : Method Description
   *
   * @param pOrderBy 
   */
  public void setOrderBy(final String pOrderBy) {
    this.orderBy = pOrderBy;
  }

  // Keys :  
  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getGlobalCountKey() {
    return globalCountKey;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getPageItem() {
    return pageItem;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getPageList() {
    return pageList;
  }

  /**
   * TODO : Method Description
   *
   * @param pName 
   *
   * @return value TODO : Value Description
   */
  public String getPages(final String pName) {
    if (pages == null) {
      return null;
    }

    return pages.get(pName);
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getSessionCursorKey() {
    return sessionCursorKey;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getSessionItemKey() {
    return sessionItemKey;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
