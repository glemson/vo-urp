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
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.Date;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * <p>
 * Print a brief summary of a TopLink LogRecord in a human readable
 * format.  The summary will typically be 1 or 2 lines.
 * </p>
 *
 * @author laurent bourges (voparis) : bourges.laurent@gmail.com
 */
public final class FastLogFormatter extends SimpleFormatter {

    //~ Constants --------------------------------------------------------------------------------------------------------
    /**
     * initial buffer size = 600 chars
     */
    private final static int INITIAL_BUFFER_SIZE = 600;
    /**
     * date format
     */
    private final static String format = "{0,date} {0,time}";
    /**
     * '(' character
     */
    public final static char PAR_OPEN_CHAR = '(';
    /**
     * ')' character
     */
    public final static char PAR_CLOSE_CHAR = ')';
    /**
     * ' ' character
     */
    public final static char SPACE_CHAR = ' ';
    /**
     * Line separator string.  This is the value of the line.separator
     * property at the moment that the SimpleFormatter was created.
     */
    private final static String LINE_SEPARATOR = PrivilegedAccessHelper.getLineSeparator();

    //~ Members ----------------------------------------------------------------------------------------------------------
    /**
     * date formatter
     */
    private MessageFormat dateFormatter;
    /**
     * cached date instance
     */
    private final Date dateInstance = new Date();
    /**
     * buffer to store formatted date
     */
    private final StringBuffer dateBuffer = new StringBuffer(64);
    /**
     * date formatter args
     */
    private Object[] dateFormatterArgs = new Object[1];

    /**
     * Format the given LogRecord.
     * @param pRecord the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public String format(LogRecord pRecord) {
        if (!(pRecord instanceof EclipseLinkLogRecord)) {
            return super.format(pRecord);
        } else {
            final EclipseLinkLogRecord record = (EclipseLinkLogRecord) pRecord;

            /*
             * Unsynchronized 512 character buffer to avoid too much array resize operations :
             */
            final StringBuilder sb = new StringBuilder(INITIAL_BUFFER_SIZE);

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
                sb.append(SPACE_CHAR);
            }

            if (record.getSourceClassName() != null) {
                sb.append(record.getSourceClassName());
            } else {
                sb.append(record.getLoggerName());
            }

            if (record.getSourceMethodName() != null) {
                sb.append(SPACE_CHAR);
                sb.append(record.getSourceMethodName());
            }

            if (record.getSessionString() != null) {
                sb.append(SPACE_CHAR);
                sb.append(record.getSessionString());
            }

            if (record.getConnection() != null) {
                sb.append(SPACE_CHAR);
                sb.append(AbstractSessionLog.CONNECTION_STRING).append(PAR_OPEN_CHAR);
                sb.append(String.valueOf(System.identityHashCode(record.getConnection()))).append(PAR_CLOSE_CHAR);
            }

            if (record.shouldPrintThread()) {
                sb.append(SPACE_CHAR);
                sb.append(AbstractSessionLog.THREAD_STRING).append(PAR_OPEN_CHAR);
                sb.append(String.valueOf(record.getThreadID())).append(PAR_CLOSE_CHAR);
            }

            sb.append(LINE_SEPARATOR);

            final String message = formatMessage(record);
            sb.append(record.getLevel().getLocalizedName());
            sb.append(": ");
            sb.append(message);

            /*
             * Log4J : not necessary :
             * sb.append(LINE_SEPARATOR);
             */
            if (record.getThrown() != null) {
                final StringWriter sw = new StringWriter(512);
                final PrintWriter pw = new PrintWriter(sw);
                try {
                    if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
                        record.getThrown().printStackTrace(pw);
                    } else if (record.getLevel().intValue() <= Level.WARNING.intValue()) {
                        if (record.shouldLogExceptionStackTrace()) {
                            record.getThrown().printStackTrace(pw);
                        } else {
                            pw.write(record.getThrown().toString());
                            /*
                             * Log4J : not necessary :
                             * pw.write(LINE_SEPARATOR);
                             */
                        }
                    }
                } catch (Exception e) {
                    CommonsLoggingSessionLog.error(e);
                } finally {
                    pw.close();
                    sb.append(sw.toString());
                }
            }

            if (CommonsLoggingSessionLog.FORCE_INTERNAL_DEBUG) {
                sb.append(LINE_SEPARATOR);
                sb.append("sb-Length : ");
                sb.append(sb.length());
                sb.append(" - capacity : ");
                sb.append(sb.capacity());
                sb.append(" - resized : ");
                sb.append(sb.capacity() > INITIAL_BUFFER_SIZE);
            }

            return sb.toString();
        }
    }
}
