/*
 * TAP.java
 * 
 * Author Gerard Lemson
 * Created on 30 Dec 2008
 */
package org.ivoa.tap;

public interface TAP_v0_31 {
  // ~~ HTTP parameters ~~
  public static final String SYNC="sync";
  public static final String ASYNC="async";
  
  public static final String REQUEST_PARAM="REQUEST";
  // valid values of the 
  public static final String REQUEST_VALUE_ADQLQuery="ADQLQuery";
  public static final String REQUEST_VALUE_ParamQuery="ParamQuery";
  public static final String REQUEST_VALUE_GetCapabilities="GetCapabilities";
  public static final String REQUEST_VALUE_GetAvailability="GetAvailability";
  public static final String REQUEST_VALUE_GetTableMetadata="GetTableMetadata";

  
  public static final String QUERY_PARAM="QUERY";
  
  
  public static final String LANG_PARAM="LANG";
  public static final String LANG_VALUE_ADQL="ADQL"; // case insensitive
  
  
  public static final String FORMAT_PARAM="FORMAT";
  // NB case insensitive
  public static final String FORMAT_VALUE_votable="votable";
  public static final String FORMAT_VALUE_xml="xml";
  public static final String FORMAT_VALUE_csv="csv";
  public static final String FORMAT_VALUE_fits="fits";
  public static final String FORMAT_VALUE_text="text";
  public static final String FORMAT_VALUE_html="html";

  public static final String UPLOAD_PARAM="UPLOAD";
  
  public static final String MAXREC_PARAM = "MAXREC";
  // MAY
  public static final String MTIME_PARAM = "MTIME";
  

  // SHOULD
  public static final String RUNID_PARAM = "RUNID";
  
  // MUST
  public static final String VERSION_PARAM = "VERSION";
  
  
  // ParamQuery parameters
  public static final String SELECT_PARAM = "SELECT";
  public static final String FROM_PARAM = "FROM";
  public static final String WHERE_PARAM = "WHERE";  
  
  
  
  
  
}
