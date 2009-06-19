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

  /** swing string builder : mono thread => no synchronization */
  private static StringBuilder ssb = new StringBuilder(512);

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden Constructor
   */
  private StringUtils() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Return the shared string buffer (unsynchronized)
   * 
   * @return shared string buffer
   */
  public static StringBuilder getSharedBuffer() {
    return ssb;
  }

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
   * Returns an html fragment with the given title and message (center)
   *
   * @param title title of the message
   * @param msg message content
   * @return html string
   */
  public static String getMessage(final String title, final String msg) {
    if (!JavaUtils.isEmpty(msg)) {
      ssb.append("<html><body><center><b>").append(title).append(" :</b>").append(HTML_BR).append(
          HTML_BR).append(msg).append("</center></body></html>");

      return extract(ssb);
    }

    return null;
  }

  /**
   * Returns an html fragment with the given message (center)
   *
   * @param msg message content
   * @return html string
   */
  public static String getTooltipText(final String msg) {
    if (!JavaUtils.isEmpty(msg)) {
      ssb.append("<html><body><center>").append(msg).append("</center></body></html>");

      return extract(ssb);
    }

    return null;
  }

  /**
   * Returns an html fragment with the given messages (center)
   *
   * @param msg message content
   * @param msg2 message content
   * @return html string
   */
  public static String getTooltipText(final String msg, final String msg2) {
    final boolean v1 = !JavaUtils.isEmpty(msg);
    final boolean v2 = !JavaUtils.isEmpty(msg2);

    if (v1 || v2) {
      ssb.append("<html><body><center>");

      if (v1) {
        ssb.append(msg);
      }

      if (v2) {
        if (v1) {
          ssb.append(HTML_BR);
        }

        ssb.append(msg2);
      }

      ssb.append("</center></body></html>");

      return extract(ssb);
    }

    return null;
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
