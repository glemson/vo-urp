package org.ivoa.util;


/**
 * Useful string methods
 *
 * @author laurent bourges (voparis)
 */
public final class StringUtils {

  /** swing string builder : mono thread => no synchronization */
  private final static StringBuilder ssb = new StringBuilder(512);

  /**
   * Forbidden Constructor
   */
  private StringUtils() {
  }

  /**
   * Test if value is empty (null or no chars)
   * @param value string value
   * @return true if value is empty (null or no chars)
   */
  public static boolean isEmpty(final String value) {
    return value == null || value.length() == 0;
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

  public static String getMessage(final String title, final String msg) {
    if (!isEmpty(msg)) {
      ssb.append("<html><body><center><b>").append(title).append(" :</b><br><br>").append(msg).append("</center></body></html>");
      return extract(ssb);
    }
    return null;
  }

  public static String getTooltipText(final String msg) {
    if (!isEmpty(msg)) {
      ssb.append("<html><body><center>").append(msg).append("</center></body></html>");
      return extract(ssb);
    }
    return null;
  }

  public static String getTooltipText(final String msg, final String msg2) {
    final boolean v1 = !isEmpty(msg);
    final boolean v2 = !isEmpty(msg2);
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

  public static final String escapeXml(final String source) {
    return source.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }
}
