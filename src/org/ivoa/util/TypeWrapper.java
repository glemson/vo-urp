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

import org.ivoa.bean.LogSupport;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
 */
public final class TypeWrapper extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** date format : HH:MM:SS.MS */
  public static final SimpleDateFormat HSDF = new SimpleDateFormat("hh:mm:ss.ms");
  /** data format : DD/MM/YYYY HH:MM:SS */
  public static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
  /** standard us date parser */
  public static final SimpleDateFormat US_INT_SDF = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z", Locale.US);

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Constructor
   */
  private TypeWrapper() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Default Format for date long value (
   *
   * @param val long date value
   *
   * @return String formatted
   */
  public static final String getDefaultFormat(final long val) {
    synchronized (SDF) {
      return SDF.format(new Date(val));
    }
  }

  /**
   * International Format for date long value (
   *
   * @param val long date value
   *
   * @return String formatted
   */
  public static final String getInternationalFormat(final long val) {
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
  public static final String getInternationalFormat(final String val) {
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
  public static final Date parseInternationalFormat(final String val) {
    try {
      synchronized (US_INT_SDF) {
        return US_INT_SDF.parse(val);
      }
    } catch (final ParseException pe) {
      log.error("TypeWrapper.parseInternationalFormat : parse failure : ", pe);
    }

    return null;
  }

  /**
   * Time Format for date long value
   *
   * @param val long date value
   *
   * @return String formatted
   *
   * @see System#currentTimeMillis()
   */
  public static final String getTimeFormat(final long val) {
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
  public static final Boolean getBoolean(final String str) {
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
  public static final boolean getBoolean(final Object o, final boolean def) {
    if (o == null) {
      return def;
    }

    if (o instanceof Boolean) {
      return ((Boolean) o).booleanValue();
    }

    return Boolean.valueOf(o.toString()).booleanValue();
  }

  /**
   * TODO : Method Description
   *
   * @param o
   * @param def
   *
   * @return value TODO : Value Description
   */
  public static final int getInteger(final Object o, final int def) {
    if (o == null) {
      return def;
    }

    if (o instanceof Integer) {
      return ((Integer) o).intValue();
    }

    try {
      return Integer.parseInt(o.toString());
    } catch (final NumberFormatException nfe) {
      return def;
    }
  }

  /**
   * TODO : Method Description
   *
   * @param o
   * @param def
   *
   * @return value TODO : Value Description
   */
  public static final Integer getInteger(final Object o, final Integer def) {
    if (o == null) {
      return def;
    }

    if (o instanceof Integer) {
      return (Integer) o;
    }

    try {
      return new Integer(o.toString());
    } catch (final NumberFormatException nfe) {
      return def;
    }
  }

  /**
   * TODO : Method Description
   *
   * @param o
   * @param def
   *
   * @return value TODO : Value Description
   */
  public static final Long getLong(final Object o, final Long def) {
    if (o == null) {
      return def;
    }

    if (o instanceof Long) {
      return (Long) o;
    }

    try {
      return new Long(o.toString());
    } catch (final NumberFormatException nfe) {
      return def;
    }
  }

  /**
   * TODO : Method Description
   *
   * @param o
   * @param def
   *
   * @return value TODO : Value Description
   */
  public static final Float getFloat(final Object o, final Float def) {
    if (o == null) {
      return def;
    }

    if (o instanceof Float) {
      return (Float) o;
    }

    try {
      return new Float(o.toString());
    } catch (final NumberFormatException nfe) {
      return def;
    }
  }

  /**
   * TODO : Method Description
   *
   * @param o
   * @param def
   *
   * @return value TODO : Value Description
   */
  public static final Double getDouble(final Object o, final Double def) {
    if (o == null) {
      return def;
    }

    if (o instanceof Double) {
      return (Double) o;
    }

    try {
      return new Double(o.toString());
    } catch (final NumberFormatException nfe) {
      return def;
    }
  }

  /**
   * TODO : Method Description
   *
   * @param capacity
   *
   * @return value TODO : Value Description
   */
  public static final List<Object> newList(final int capacity) {
    return new ArrayList<Object>(capacity);
  }

  /**
   * TODO : Method Description
   *
   * @param capacity
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public static final Map newMap(final int capacity) {
    if (capacity <= 0) {
      return new HashMap();
    }

    return new HashMap(capacity);
  }

  /**
   * TODO : Method Description
   *
   * @param capacity
   *
   * @return value TODO : Value Description
   */
  @SuppressWarnings("unchecked")
  public static final Set newSet(final int capacity) {
    return new HashSet(capacity);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
