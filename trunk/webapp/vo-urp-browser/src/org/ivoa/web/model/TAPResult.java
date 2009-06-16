/*
 * TAPResult.java
 *
 * Author lemson
 * Created on Oct 31, 2008
 */
package org.ivoa.web.model;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
public class TAPResult extends BaseResult {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  public static final int IS_STRING = 1;
  /**
   * TODO : Field Description
   */
  public static final int IS_FILE = 2;
  /**
   * TODO : Field Description
   */
  public static final int IS_URL = 4;

  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  private int resultType = IS_STRING;

  // the actual result to be sent back as a String.
  /**
   * TODO : Field Description
   */
  private String result;
  /**
   * TODO : Field Description
   */
  private SQLQuery query = null;
  /**
   * TODO : Field Description
   */
  private Map<String, String> paramQuery = null;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new TAPResult object
   *
   * @param pQuery 
   */
  public TAPResult(final SQLQuery pQuery) {
    this.query = pQuery;
  }

  /**
   * Creates a new TAPResult object
   *
   * @param map 
   */
  public TAPResult(final Map<String, String> map) {
    this.paramQuery = map;
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param writer 
   *
   * @throws IOException 
   */
  public void writeResult(final Writer writer) throws IOException {
    if (resultType == IS_FILE) // write contents of file to writer
     {
      //writer.write(result);
    } else if (resultType == IS_URL) // write contents of URL to writer
     {
      // writer.write(result);
    } else { // if(resultType == IS_STRING)
      writer.write(result);
    }
  }

  /**
   * TODO : Method Description
   *
   * @param file 
   */
  public void setResultFile(final String file) {
    result = file;
    resultType = IS_FILE;
  }

  /**
   * TODO : Method Description
   *
   * @param url 
   */
  public void setResultURL(final String url) {
    result = url;
    resultType = IS_URL;
  }

  /**
   * TODO : Method Description
   *
   * @param r 
   */
  public void setResultString(final String r) {
    result = r;
    resultType = IS_STRING;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public SQLQuery getQuery() {
    return query;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Map<String, String> getParamQuery() {
    return paramQuery;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
