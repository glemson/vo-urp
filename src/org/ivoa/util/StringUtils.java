package org.ivoa.util;

/**
 * Useful string methods
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class StringUtils {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** Html break line */
  public final static String HTML_BR = "<br>";

  /** Html tags regexp matcher */
  private final static String REGEXP_HTML = "\\<.*?\\>";

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden Constructor
   */
  private StringUtils() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Extract buffer content & reset buffer
   *
   * @param sb StringBuilder
   * @return String value
   */
  public static String extract(final StringBuilder sb) {
    final String text = sb.toString();

    sb.setLength(0);

    return text;
  }

  /**
   * Extract buffer content & reset buffer
   *
   * @param sb StringBuilder
   * @return String value
   */
  public static String extract(final StringBuffer sb) {
    final String text = sb.toString();

    sb.setLength(0);

    return text;
  }

  /**
   * Return an encoded string : < > characters replaced by html entities
   *
   * @param source input string
   * @return encoded string
   */
  public static final String escapeXml(final String source) {
    return source.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }

  /**
   * Return an unencoded string : < > html entities characters replaced by < >
   * 
   * @param source input string
   * @return unencoded string
   */
  public static final String unescapeXml(final String source) {
    return source.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
  }

  /**
   * Return a string without any tag in it
   * 
   * @param source input string
   * @return string
   */
  public static final String removeHtml(final String source) {
    return source.replaceAll(REGEXP_HTML, "");
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
