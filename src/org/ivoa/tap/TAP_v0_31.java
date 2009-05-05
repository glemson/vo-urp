/*
 * TAP.java
 *
 * Author Gerard Lemson
 * Created on 30 Dec 2008
 */
package org.ivoa.tap;

/**
 * TODO : Interface Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public interface TAP_v0_31 {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final String SYNC  = "sync";
  /**
   * TODO : Field Description
   */
  public static final String ASYNC = "async";
  /**
   * TODO : Field Description
   */
  public static final String REQUEST_PARAM = "REQUEST";

  // valid values of the 
  /**
   * TODO : Field Description
   */
  public static final String REQUEST_VALUE_ADQLQuery        = "ADQLQuery";
  /**
   * TODO : Field Description
   */
  public static final String REQUEST_VALUE_ParamQuery = "ParamQuery";
  /**
   * TODO : Field Description
   */
  public static final String REQUEST_VALUE_GetCapabilities = "GetCapabilities";
  /**
   * TODO : Field Description
   */
  public static final String REQUEST_VALUE_GetAvailability = "GetAvailability";
  /**
   * TODO : Field Description
   */
  public static final String REQUEST_VALUE_GetTableMetadata = "GetTableMetadata";
  /**
   * TODO : Field Description
   */
  public static final String QUERY_PARAM = "QUERY";
  /**
   * TODO : Field Description
   */
  public static final String LANG_PARAM = "LANG";
  /**
   * TODO : Field Description
   */
  public static final String LANG_VALUE_ADQL = "ADQL"; // case insensitive
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_PARAM = "FORMAT";

  // NB case insensitive
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_VALUE_votable = "votable";
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_VALUE_xml = "xml";
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_VALUE_csv = "csv";
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_VALUE_fits = "fits";
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_VALUE_text = "text";
  /**
   * TODO : Field Description
   */
  public static final String FORMAT_VALUE_html = "html";
  /**
   * TODO : Field Description
   */
  public static final String UPLOAD_PARAM = "UPLOAD";
  /**
   * TODO : Field Description
   */
  public static final String MAXREC_PARAM = "MAXREC";

  // MAY
  /**
   * TODO : Field Description
   */
  public static final String MTIME_PARAM = "MTIME";

  // SHOULD
  /**
   * TODO : Field Description
   */
  public static final String RUNID_PARAM = "RUNID";

  // MUST
  /**
   * TODO : Field Description
   */
  public static final String VERSION_PARAM = "VERSION";

  // ParamQuery parameters
  /**
   * TODO : Field Description
   */
  public static final String SELECT_PARAM = "SELECT";
  /**
   * TODO : Field Description
   */
  public static final String FROM_PARAM = "FROM";
  /**
   * TODO : Field Description
   */
  public static final String WHERE_PARAM = "WHERE";
}
//~ End of file --------------------------------------------------------------------------------------------------------
