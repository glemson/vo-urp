package org.ivoa.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Map;

import org.ivoa.util.text.LocalStringBuilder;


/**
 * Utility class to convert numbers to String
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class NumberUtils {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private static final String EXPONENT = "E00";
  /** TODO : Field Description */
  private static final int EXP_SIGN = EXPONENT.length();
  /** Format used when <code>isMoreDecimal</code> is set to <code>true</code> */
  public static final String MORE_PRECISION = "0.000000000000000" + EXPONENT;
  /** Default strFormat to display numerical values */
  public static final String LESS_PRECISION = "0.000000" + EXPONENT;

  /** Web format to display numerical values */
  public static final String WEB_PRECISION = "0.000" + EXPONENT;

  /** Short format to display numerical values */
  public static final String LOW_PRECISION = "0.0" + EXPONENT;

  /** formatter cache */
  private static Map<String, DecimalFormat> cache = new HashMap<String, DecimalFormat>(4);
  /** field position */
  private static FieldPosition FAKE_POSITION = new FieldPosition(0);

  static {
    cache.put(LOW_PRECISION, new DecimalFormat(LOW_PRECISION));
    cache.put(WEB_PRECISION, new DecimalFormat(WEB_PRECISION));
    cache.put(LESS_PRECISION, new DecimalFormat(LESS_PRECISION));
    cache.put(MORE_PRECISION, new DecimalFormat(MORE_PRECISION));
  }

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden constructor
   */
  private NumberUtils() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Format an int to string
   *
   * @param value int value
   * @return string
   */
  public static String format(final int value) {
    return Integer.toString(value, 10);
  }

  /**
   * Format an Integer to string
   *
   * @param value Integer value
   * @return string
   */
  public static String format(final Integer value) {
    return value.toString();
  }

  /**
   * Return the precision pattern among MORE_PRECISION and LESS_PRECISION
   *
   * @param isMoreDecimal flag to indicate to return the MORE_PRECISION pattern
   * @return precision pattern
   */
  public static String getPrecision(final boolean isMoreDecimal) {
    return (isMoreDecimal) ? MORE_PRECISION : LESS_PRECISION;
  }

  /**
   * Return the existing formatter associated to that precision
   *
   * @param precision precision pattern
   * @return decimal formatter
   * @throws IllegalStateException if that precision is not supported
   */
  public static DecimalFormat getFormatter(final String precision) {
    final DecimalFormat df = cache.get(precision);

    if (df == null) {
      throw new IllegalStateException("NumberUtil : precision not allowed : " + precision);
    }

    return df;
  }

  /**
   * Format the given value with the formatter associated to that precision
   *
   * @param value value to format
   * @param precision precision pattern
   * @return string
   */
  public static String format(final double value, final String precision) {
    return strFormat(getFormatter(precision), value);
  }

  /**
   * Format the given value with the formatter associated to that precision
   *
   * @param value value to format
   * @param precision precision pattern
   * @param sb the buffer to use
   * @return string
   */
  public static String format(final double value, final String precision, final StringBuffer sb) {
    return strFormat(getFormatter(precision), value, sb);
  }

  /**
   * Format the given value with the formatter associated to that precision
   *
   * @param value value to format
   * @param precision precision pattern
   * @return string
   */
  public static String format(final Number value, final String precision) {
    return strFormat(getFormatter(precision), value);
  }

  /**
   * Format the given value with the formatter associated to that precision
   *
   * @param value value to format
   * @param precision precision pattern
   * @param sb the buffer to use
   * @return string
   */
  public static String format(final Number value, final String precision, final StringBuffer sb) {
    return strFormat(getFormatter(precision), value, sb);
  }

  /**
   * Returns a formatted string for numeric object : replace E0? by E+0? Useful only for ASCII
   * export
   *
   * @param df formatter
   * @param value the data to strFormat
   * @param sb the buffer to use
   * @return A formatted string
   */
  public static final String formatFixed(final DecimalFormat df, final Double value, final StringBuffer sb) {
    format(df, value, sb);
    final int len = sb.length();
    if (len > 0) {
    // exponent part : E-00 or E00
      // hack to have E+00 instead of E00 :
      final int pos = len - EXP_SIGN;

    if (sb.charAt(pos) == 'E') {
      sb.insert(pos + 1, '+');
    }
    }
    return LocalStringBuilder.extract(sb);
  }

  /**
   * Returns the strFormat used to display numeric value in cell Use this method only if no
   * StringBuffer already available !
   *
   * @param df formatter
   * @param value the value to strFormat
   * @return The string representation of this value
   */
  public static String strFormat(final DecimalFormat df, final double value) {
    return format(df, value, new StringBuffer()).toString();
  }

  /**
   * Formats a double to produce a string.
   *
   * @param df formatter
   * @param value The double to strFormat
   * @param sb where the text is to be appended
   * @return The formatted number string
   * @see java.text.FieldPosition
   */
  public static String strFormat(final DecimalFormat df, final double value, final StringBuffer sb) {
    df.format(value, sb, FAKE_POSITION);

    return LocalStringBuilder.extract(sb);
  }

  /**
   * Formats a double to produce a string.
   *
   * @param df formatter
   * @param value The double to strFormat
   * @param sb where the text is to be appended
   * @return The formatted number string
   * @see java.text.FieldPosition
   */
  public static StringBuffer format(final DecimalFormat df, final double value, final StringBuffer sb) {
    return df.format(value, sb, FAKE_POSITION);
  }

  /**
   * Returns the strFormat used to display numeric value in cell Use this method only if no
   * StringBuffer already available !
   *
   * @param df formatter
   * @param value the Number to format
   *
   * @return The string representation of this value
   */
  public static String strFormat(final DecimalFormat df, final Number value) {
    return format(df, value, new StringBuffer()).toString();
  }

  /**
   * Formats a double to produce a string.
   *
   * @param df formatter
   * @param value The Number to format
   * @param sb where the text is to be appended
   * @return The formatted number string
   * @see java.text.FieldPosition
   */
  public static String strFormat(final DecimalFormat df, final Number value, final StringBuffer sb) {
    df.format(value, sb, FAKE_POSITION);

    return LocalStringBuilder.extract(sb);
  }

  /**
   * Formats a Number to produce a string.
   *
   * @param df formatter
   * @param value The number to format
   * @param sb where the text will be appended to
   * @return The formatted number string
   */
  public static StringBuffer format(final DecimalFormat df, final Number value, final StringBuffer sb) {
    return df.format(value, sb, FAKE_POSITION);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
