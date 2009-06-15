package org.ivoa.util;

/**
 * Useful string methods
 *
 * @author laurent bourges (voparis)
 */
public final class StringUtils {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** swing string builder : mono thread => no synchronization */
  private static final StringBuilder ssb = new StringBuilder(512);

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
   *
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
   *
   * @return String value
   */
  public static String extract(final StringBuffer sb) {
    final String text = sb.toString();

    sb.setLength(0);

    return text;
  }

  /**
   * TODO : Method Description
   *
   * @param title
   * @param msg
   *
   * @return value TODO : Value Description
   */
  public static String getMessage(final String title, final String msg) {
    if (!JavaUtils.isEmpty(msg)) {
      ssb.append("<html><body><center><b>").append(title).append(" :</b><br><br>").append(msg)
          .append("</center></body></html>");

      return extract(ssb);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param msg
   *
   * @return value TODO : Value Description
   */
  public static String getTooltipText(final String msg) {
    if (!JavaUtils.isEmpty(msg)) {
      ssb.append("<html><body><center>").append(msg).append("</center></body></html>");

      return extract(ssb);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param msg
   * @param msg2
   *
   * @return value TODO : Value Description
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
          ssb.append("<br>");
        }

        ssb.append(msg2);
      }

      ssb.append("</center></body></html>");

      return extract(ssb);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param source
   *
   * @return value TODO : Value Description
   */
  public static final String escapeXml(final String source) {
    return source.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
