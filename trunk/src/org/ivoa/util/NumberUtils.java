package org.ivoa.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Map;


/**
 * Number to String methods
 *
 * @author laurent bourges (voparis)
 */
public final class NumberUtils {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private static final String EXPONENT = "E00";
  /** TODO : Field Description */
  private static final int EXP_SIGN = EXPONENT.length();
  /** Format used when <code>isMoreDecimal</code> is set to <code>true</code> */
  public static final String MORE_PRECISION = "0.000000000000000" + EXPONENT;
  /** Default strFormat to display numreical values */
  public static final String LESS_PRECISION = "0.000000" + EXPONENT;
  /** formatter cache */
  private static Map<String, DecimalFormat> cache = new HashMap<String, DecimalFormat>(4);
  /** field position */
  private static final FieldPosition FAKE_POSITION = new FieldPosition(0);

  static {
    cache.put(LESS_PRECISION, new DecimalFormat(LESS_PRECISION));
    cache.put(MORE_PRECISION, new DecimalFormat(MORE_PRECISION));
  }

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new NumberUtils object
   */
  private NumberUtils() {
    // not used
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param value
   *
   * @return value TODO : Value Description
   */
  public static String format(final int value) {
    return Integer.toString(value, 10);
  }

  /**
   * TODO : Method Description
   *
   * @param value
   *
   * @return value TODO : Value Description
   */
  public static String format(final Integer value) {
    return value.toString();
  }

  /**
   * TODO : Method Description
   *
   * @param isMoreDecimal
   *
   * @return value TODO : Value Description
   */
  public static String getPrecision(final boolean isMoreDecimal) {
    return (isMoreDecimal) ? MORE_PRECISION : LESS_PRECISION;
  }

  /**
   * TODO : Method Description
   *
   * @param precision
   *
   * @return value TODO : Value Description
   *
   * @throws IllegalStateException
   */
  public static DecimalFormat getFormatter(final String precision) {
    final DecimalFormat df = cache.get(precision);

    if (df == null) {
      throw new IllegalStateException("NumberUtil : precision not allowed : " + precision);
    }

    return df;
  }

  /**
   * TODO : Method Description
   *
   * @param value
   * @param precision
   *
   * @return value TODO : Value Description
   */
  public static String format(final double value, final String precision) {
    return strFormat(getFormatter(precision), value);
  }

  /**
   * TODO : Method Description
   *
   * @param value
   * @param precision
   * @param sb
   *
   * @return value TODO : Value Description
   */
  public static String format(final double value, final String precision, final StringBuffer sb) {
    return strFormat(getFormatter(precision), value, sb);
  }

  /**
   * TODO : Method Description
   *
   * @param value
   * @param precision
   *
   * @return value TODO : Value Description
   */
  public static String format(final Number value, final String precision) {
    return strFormat(getFormatter(precision), value);
  }

  /**
   * TODO : Method Description
   *
   * @param value
   * @param precision
   * @param sb
   *
   * @return value TODO : Value Description
   */
  public static String format(final Number value, final String precision, final StringBuffer sb) {
    return strFormat(getFormatter(precision), value, sb);
  }

  /**
   * Returns a formatted string for numeric object  Useful only for ASCII export
   *
   * @param df formatter
   * @param value the data to strFormat
   * @param sb where the text is to be appended
   *
   * @return A formatted string
   */
  public static final String formatFixed(final DecimalFormat df, final Double value, final StringBuffer sb) {
    format(df, value, sb);

    // exponent part : E-00 or E00
    // hack to handle E+00 instead of E00 :
    final int pos = sb.length() - EXP_SIGN;

    if (sb.charAt(pos) == 'E') {
      sb.insert(pos + 1, '+');
    }

    return StringUtils.extract(sb);
  }

  /**
   * Returns the strFormat used to display numericl value in cell  Use this method only if no StringBuffer
   * already available !
   *
   * @param df formatter
   * @param value the value to strFormat
   *
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
   *
   * @return The formatted number string
   *
   * @see java.text.FieldPosition
   */
  public static String strFormat(final DecimalFormat df, final double value, final StringBuffer sb) {
    df.format(value, sb, FAKE_POSITION);

    return StringUtils.extract(sb);
  }

  /**
   * Formats a double to produce a string.
   *
   * @param df formatter
   * @param value The double to strFormat
   * @param sb where the text is to be appended
   *
   * @return The formatted number string
   *
   * @see java.text.FieldPosition
   */
  public static StringBuffer format(final DecimalFormat df, final double value, final StringBuffer sb) {
    return df.format(value, sb, FAKE_POSITION);
  }

  /**
   * Returns the strFormat used to display numericl value in cell  Use this method only if no StringBuffer
   * already available !
   *
   * @param df formatter
   * @param value the value to strFormat
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
   * @param value The double to strFormat
   * @param sb where the text is to be appended
   *
   * @return The formatted number string
   *
   * @see java.text.FieldPosition
   */
  public static String strFormat(final DecimalFormat df, final Number value, final StringBuffer sb) {
    df.format(value, sb, FAKE_POSITION);

    return StringUtils.extract(sb);
  }

  /**
   * Formats a double to produce a string.
   *
   * @param df formatter
   * @param value The double to strFormat
   * @param sb where the text is to be appended
   *
   * @return The formatted number string
   *
   * @see java.text.FieldPosition
   */
  public static StringBuffer format(final DecimalFormat df, final Number value, final StringBuffer sb) {
    return df.format(value, sb, FAKE_POSITION);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
