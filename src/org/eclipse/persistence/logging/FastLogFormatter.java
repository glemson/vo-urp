/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;


/**
 * <p>
 * Print a brief summary of a TopLink LogRecord in a human readable format. <br/ * >
 * The summary will typically be 1 or 2 lines.
 * </p>
 * 
 * @author laurent bourges (voparis) : bourges.laurent@gmail.com
 */
public final class FastLogFormatter {
  // ~ Constants
  // --------------------------------------------------------------------------------------------------------

  /**
   * internal debugger FLAG to display buffer statistics
   */
  public static final boolean FORCE_INTERNAL_DEBUG = false;
  /** undefined capacity */
  private static final int UNDEFINED_CAPACITY = -1;
  /** initial buffer capacity = 256 chars */
  private static final int INITIAL_BUFFER_CAPACITY = 256;
  /** date format */
  private static final String format = "{0,date} {0,time}";
  /** '(' character */
  public static final char PAR_OPEN_CHAR = '(';
  /** ')' character */
  public static final char PAR_CLOSE_CHAR = ')';
  /** ' ' character */
  public static final char SPACE_CHAR = ' ';
  /**
   * Line separator string. This is the value of the line.separator property at
   * the moment that the SimpleFormatter was created.
   */
  private static final String LINE_SEPARATOR = PrivilegedAccessHelper.getLineSeparator();
  // ~ Members
  // ----------------------------------------------------------------------------------------------------------
  /** computed buffer capacity */
  private int adaptedBufferCapacity = UNDEFINED_CAPACITY;
  /** date formatter */
  private MessageFormat dateFormatter;
  /** cached date instance */
  private final Date dateInstance = new Date();
  /** buffer to store formatted date */
  private final StringBuffer dateBuffer = new StringBuffer(64);
  /** date formatter arguments */
  private Object[] dateFormatterArgs = new Object[1];
  /** internal debugger flag for that instance */
  private boolean internalDebug = FORCE_INTERNAL_DEBUG;

  // ~ Methods
  // ----------------------------------------------------------------------------------------------------------
  /**
   * Format the given LogRecord.<br/>
   * Thread safe so this method is not synchroniz'd
   * 
   * @param pRecord
   *          the log record to be formatted.
   * @return a formatted log record
   */
  public final String format(final LogRecord pRecord) {
    if (!(pRecord instanceof EclipseLinkLogRecord)) {
      return "unsupported LogRecord instance !";
    }
    final EclipseLinkLogRecord record = (EclipseLinkLogRecord) pRecord;

    // message was previously formatted (internationalization) :
    final String message = record.getMessage();

    final int capacity = (adaptedBufferCapacity == UNDEFINED_CAPACITY) ? INITIAL_BUFFER_CAPACITY
        : (adaptedBufferCapacity + message.length());

    /*
     * Unsynchronized & sized character buffer to avoid too much array resize
     * operations :
     */
    final StringBuilder sb = new StringBuilder(capacity);

    // local variable for performance :
    final char space = SPACE_CHAR;

    if (record.shouldPrintDate()) {
      synchronized (dateInstance) {
        // Minimize memory allocations here.
        dateInstance.setTime(record.getMillis());
        dateFormatterArgs[0] = dateInstance;

        if (dateFormatter == null) {
          dateFormatter = new MessageFormat(format);
        }

        dateFormatter.format(dateFormatterArgs, dateBuffer, null);
        sb.append(dateBuffer);
        // reset dateBuffer content :
        dateBuffer.setLength(0);
      }

      sb.append(space);
    }

    if (record.getSourceClassName() != null) {
      sb.append(record.getSourceClassName());
    } else {
      sb.append(record.getLoggerName());
    }

    if (record.getSourceMethodName() != null) {
      sb.append(space);
      sb.append(record.getSourceMethodName());
    }

    if (record.getSessionString() != null) {
      sb.append(space);
      sb.append(record.getSessionString());
    }

    if (record.getConnection() != null) {
      sb.append(space);
      sb.append(AbstractSessionLog.CONNECTION_STRING).append(PAR_OPEN_CHAR);
      sb.append(String.valueOf(System.identityHashCode(record.getConnection()))).append(PAR_CLOSE_CHAR);
    }

    if (record.shouldPrintThread()) {
      sb.append(space);
      sb.append(AbstractSessionLog.THREAD_STRING).append(PAR_OPEN_CHAR);
      sb.append(String.valueOf(record.getThreadID())).append(PAR_CLOSE_CHAR);
    }

    sb.append(LINE_SEPARATOR);

    sb.append(record.getLevel().getLocalizedName());
    sb.append(": ");
    sb.append(message);

    // first time, compute initial capacity :
    if (adaptedBufferCapacity == UNDEFINED_CAPACITY) {
      adaptedBufferCapacity = (sb.length() + 4) - message.length();

      if (CommonsLoggingSessionLog.FORCE_INTERNAL_DEBUG) {
        sb.append(LINE_SEPARATOR);
        sb.append("adaptedBufferCapacity : ");
        sb.append(adaptedBufferCapacity);
      }
    }

    if (record.getThrown() != null) {
      final StringWriter sw = new StringWriter(INITIAL_BUFFER_CAPACITY);
      final PrintWriter pw = new PrintWriter(sw);

      try {
        if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
          record.getThrown().printStackTrace(pw);
        } else {
          if (record.getLevel().intValue() <= Level.WARNING.intValue()) {
            if (record.shouldLogExceptionStackTrace()) {
              record.getThrown().printStackTrace(pw);
            } else {
              pw.write(record.getThrown().toString());
            }
          }
        }
      } catch (final Exception e) {
        CommonsLoggingSessionLog.error("FastLogFormatter.format", e);
      } finally {
        pw.close();
        sb.append(sw.toString());
      }
    }

    if (internalDebug) {
      final int len = sb.length();
      final int cap = sb.capacity();

      sb.append(LINE_SEPARATOR);
      sb.append("sb-Length : ");
      sb.append(len);
      sb.append(" - initial capacity : ");
      sb.append(capacity);
      sb.append(" - capacity : ");
      sb.append(cap);
      sb.append(" - resized : ");
      sb.append(cap > capacity);
      sb.append(" - overhead : ");
      sb.append(cap - len);
    }

    return sb.toString();
  }
}
// ~ End of file
// --------------------------------------------------------------------------------------------------------

