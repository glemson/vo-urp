package org.ivoa.util;

import org.ivoa.util.text.LocalStringBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * Collection toString() methods
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class CollectionUtils {

  /** Line separator string */
  public final static String LINE_SEPARATOR = System.getProperty("line.separator");

  /** begin separator = \n{\n */
  public final static String BEGIN_SEPARATOR = LINE_SEPARATOR + "{" + LINE_SEPARATOR;

  /** end separator = \n} */
  public final static String END_SEPARATOR = LINE_SEPARATOR + "}";

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new CollectionUtils object
   */
  private CollectionUtils() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
 /**
   * toString method for a Collection instance Format : <code><br/>
   * {<br/>
   * value<br/>
   * ...<br/>
   * }
   * </code>
   * 
   * @param c collection
   * @return string
   */
  public static String toString(final Collection<?> c) {
    return toString(c, LINE_SEPARATOR, BEGIN_SEPARATOR, END_SEPARATOR);
  }

  /**
   * toString method for a Collection instance Format : <code><br/>
   * {<br/>
   * value<br/>
   * ...<br/>
   * }
   * </code>
   * 
   * @param sb buffer
   * @param c collection
   * @return buffer (sb)
   */
  public static StringBuilder toString(final StringBuilder sb, final Collection<?> c) {
    return toString(sb, c, LINE_SEPARATOR, BEGIN_SEPARATOR, END_SEPARATOR);
  }

  /**
   * toString method for a Map instance Format : <code><br/>
   * {<br/>
   * key = value<br/>
   * ...<br/>
   * }
   * </code>
   * 
   * @param m map
   * @return string
   */
  public static String toString(final Map<?, ?> m) {
    return toString(m, LINE_SEPARATOR, BEGIN_SEPARATOR, END_SEPARATOR);
  }

  /**
   * toString method for a Map instance Format : <code><br/>
   * {<br/>
   * key = value<br/>
   * ...<br/>
   * }
   * </code>
   * 
   * @param sb buffer
   * @param m map
   * @return buffer (sb)
   */
  public static StringBuilder toString(final StringBuilder sb, final Map<?, ?> m) {
    return toString(sb, m, LINE_SEPARATOR, BEGIN_SEPARATOR, END_SEPARATOR);
  }

  /**
   * toString method for a Collection instance with the given line separator Format : <code>
   * value lineSep ...
   * </code>
   * 
   * @param c collection
   * @param lineSep line separator
   * @return string
   */
  public static String toString(final Collection<?> c, final String lineSep) {
    return toString(c, lineSep, "", "");
  }

  /**
   * toString method for a Collection instance with the given line separator Format : <code>
   * value lineSep ...
   * </code>
   * 
   * @param sb buffer
   * @param c collection
   * @param lineSep line separator
   * @return buffer (sb)
   */
  public static StringBuilder toString(final StringBuilder sb, final Collection<?> c, final String lineSep) {
    return toString(sb, c, lineSep, "", "");
  }

  /**
   * toString method for a Map instance with the given line separator Format : <code>
   * key = value lineSep ...
   * </code>
   * 
   * @param m map
   * @param lineSep line separator
   * @return string
   */
  public static String toString(final Map<?, ?> m, final String lineSep) {
    return toString(m, lineSep, "", "");
  }

  /**
   * toString method for a Map instance with the given line separator Format : <code>
   * key = value lineSep ...
   * </code>
   * 
   * @param sb buffer
   * @param m map
   * @param lineSep line separator
   * @return buffer (sb)
   */
  public static StringBuilder toString(final StringBuilder sb, final Map<?, ?> m, final String lineSep) {
    return toString(sb, m, lineSep, "", "");
  }

  /**
   * toString method for a Collection instance with the given start, line and end separators
   * 
   * @param c collection
   * @param lineSep line separator
   * @param startSep start separator
   * @param endSep end separator
   * @return string
   */
  public static String toString(final Collection<?> c, final String lineSep, final String startSep,
      final String endSep) {
    return LocalStringBuilder.toString(toString(LocalStringBuilder.getBuffer(), c, lineSep, startSep, endSep));
  }

  /**
   * toString method for a Collection instance with the given start, line and end separators
   * 
   * @param sb buffer
   * @param c collection
   * @param lineSep line separator
   * @param startSep start separator
   * @param endSep end separator
   * @return buffer (sb)
   */
  public static StringBuilder toString(final StringBuilder sb, final Collection<?> c, final String lineSep,
      final String startSep, final String endSep) {
    final Iterator<?> it = c.iterator();

    sb.append(startSep);

    for (int i = 0, max = c.size() - 1; i <= max; i++) {
      sb.append(it.next());

      if (i < max) {
        sb.append(lineSep);
      }
    }

    return sb.append(endSep);
  }

  /**
   * toString method for a Map instance with the given start, line and end separators
   * 
   * @param m map
   * @param lineSep line separator
   * @param startSep start separator
   * @param endSep end separator
   * @return string
   */
  public static String toString(final Map<?, ?> m, final String lineSep, final String startSep,
      final String endSep) {
    return LocalStringBuilder.toString(toString(LocalStringBuilder.getBuffer(), m, lineSep, startSep, endSep));
  }

  /**
   * toString method for a Map instance with the given start, line and end separators
   * 
   * @param sb buffer
   * @param m map
   * @param lineSep line separator
   * @param startSep start separator
   * @param endSep end separator
   * @return buffer (sb)
   */
  @SuppressWarnings("unchecked")
  public static StringBuilder toString(final StringBuilder sb, final Map<?, ?> m, final String lineSep,
      final String startSep, final String endSep) {
    final Iterator it = m.entrySet().iterator();

    sb.append(startSep);

    Map.Entry e;
    Object key;
    Object value;

    for (int i = 0, max = m.size() - 1; i <= max; i++) {
      e = (Map.Entry) it.next();
      key = e.getKey();
      value = e.getValue();
      sb.append(key).append(" = ").append(value);

      if (i < max) {
        sb.append(lineSep);
      }
    }

    return sb.append(endSep);
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
