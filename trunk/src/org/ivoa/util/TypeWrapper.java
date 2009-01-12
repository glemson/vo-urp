package org.ivoa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;


public final class TypeWrapper {

  /** logger */
  protected final static Log log = LogUtil.getLoggerDev();
  /** date format : HH:MM:SS.MS */
  public static final SimpleDateFormat HSDF = new SimpleDateFormat("hh:mm:ss.ms");
  /** data format : DD/MM/YYYY HH:MM:SS */
  public static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
  /** standard us date parser */
  public static final SimpleDateFormat US_INT_SDF = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.US);

  /**
   * Constructor
   */
  private TypeWrapper() {
  }

  /**
   * Default Format for date long value (@see System.currentTimeMillis() )
   * 
   * @param val long date value
   * 
   * @return String formatted
   */
  final static public String getDefaultFormat(final long val) {
    synchronized (SDF) {
      return SDF.format(new Date(val));
    }
  }

  /**
   * International Format for date long value (@see System.currentTimeMillis() )
   * 
   * @param val long date value
   * 
   * @return String formatted
   */
  final static public String getInternationalFormat(final long val) {
    synchronized (US_INT_SDF) {
      return US_INT_SDF.format(new Date(val));
    }
  }

  /**
   * International format for string date value (parse and format) 
   * 
   * @param val String date value
   * 
   * @return String formatted
   */
  final static public String getInternationalFormat(final String val) {
    synchronized (US_INT_SDF) {
      return US_INT_SDF.format(parseInternationalFormat(val));
    }
  }

  /**
   * Parse International format for string date value 
   * 
   * @param val String date value
   * 
   * @return String formatted
   */
  final static public Date parseInternationalFormat(final String val) {
    try {
      synchronized (US_INT_SDF) {
        return US_INT_SDF.parse(val);
      }
    } catch (ParseException pe) {
      log.error("TypeWrapper.parseInternationalFormat : parse failure : ", pe);
    }
    return null;
  }

  /**
   * Time Format for date long value (@see System.currentTimeMillis() )
   * 
   * @param val long date value
   * 
   * @return String formatted
   */
  final static public String getTimeFormat(final long val) {
    synchronized (HSDF) {
      return HSDF.format(new Date(val));
    }
  }

  /**
   * gets a Boolean from String (parse)
   * 
   * @param str String value
   * 
   * @return Boolean new instance
   */
  final static public Boolean getBoolean(final String str) {

    if (str == null) {
      return Boolean.FALSE;
    }
    return Boolean.valueOf(str);
  }

  /**
   * gets a Boolean from Object (parse(o.toString() if !Boolean) )
   * 
   * @param o Object value
   * @param def default value
   * 
   * @return boolean value
   */
  final static public boolean getBoolean(final Object o, final boolean def) {

    if (o == null) {
      return def;
    }

    if (o instanceof Boolean) {
      return ((Boolean) o).booleanValue();
    }

    return Boolean.valueOf(o.toString()).booleanValue();
  }

  final static public int getInteger(final Object o, final int def) {

    if (o == null) {
      return def;
    }

    if (o instanceof Integer) {
      return ((Integer) o).intValue();
    }

    try {
      return Integer.parseInt(o.toString());
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  final static public Integer getInteger(final Object o, final Integer def) {

    if (o == null) {
      return def;
    }

    if (o instanceof Integer) {
      return (Integer) o;
    }

    try {
      return new Integer(o.toString());
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  final static public Long getLong(final Object o, final Long def) {

    if (o == null) {
      return def;
    }

    if (o instanceof Long) {
      return (Long) o;
    }

    try {
      return new Long(o.toString());
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  final static public Float getFloat(final Object o, final Float def) {

    if (o == null) {
      return def;
    }

    if (o instanceof Float) {
      return (Float) o;
    }

    try {
      return new Float(o.toString());
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  final static public Double getDouble(final Object o, final Double def) {

    if (o == null) {
      return def;
    }

    if (o instanceof Double) {
      return (Double) o;
    }

    try {
      return new Double(o.toString());
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  public final static List newList(final int capacity) {
    return new ArrayList(capacity);
  }

  public final static Map newMap(final int capacity) {
    if (capacity <= 0) {
      return new HashMap();
    }
    return new HashMap(capacity);
  }

  public final static Set newSet(final int capacity) {
    return new HashSet(capacity);
  }
}
