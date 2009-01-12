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

public class TAPResult extends BaseResult{

  public static final int IS_STRING=1;
  public static final int IS_FILE=2;
  public static final int IS_URL=4;
  private int resultType = IS_STRING;
  // the actual result to be sent back as a String.
  // how it is to be interpreted is up to the resultType code
  private String result;
  
  private SQLQuery query = null;
  private Map paramQuery = null;
  
  public TAPResult(SQLQuery query)
  {
    this.query = query;
  }
  
  public TAPResult(Map map)
  {
    this.paramQuery = map;
  }
  
  public void writeResult(Writer writer) throws IOException
  {
    if(resultType == IS_FILE)// write contents of file to writer
    {
      //writer.write(result);
    }
    else if(resultType == IS_URL)// write contents of URL to writer
    {
     // writer.write(result);
    }
    else // if(resultType == IS_STRING)
      writer.write(result);
  }
  
  public void setResultFile(String file)
  {
    result = file;
    resultType = IS_FILE;
  }
  public void setResultURL(String url)
  {
    result = url;
    resultType = IS_URL;
  }
  public void setResultString(String r)
  {
    result = r;
    resultType = IS_STRING;
  }

  public SQLQuery getQuery() {
    return query;
  }

  public Map<String, String> getParamQuery() {
    return paramQuery;
  }
}
