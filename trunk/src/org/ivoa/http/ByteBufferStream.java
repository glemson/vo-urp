package org.ivoa.http;

import org.apache.commons.logging.Log;

import org.ivoa.util.LogUtil;

/*
 * @(#)ByteArrayOutputStream.java  1.46 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * This class implements an output stream in which the data is written into a byte array. The buffer automatically
 * grows as data is written to it. The data can be retrieved using <code>toByteArray()</code> and
 * <code>toString()</code>.<p>Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in this class can
 * be called after the stream has been closed without generating an <tt>IOException</tt>.</p>
 *  TODO LAURENT : use better buffer present in voparis java library ...
 *
 * @author Arthur van Hoff
 * @version 1.46, 01/23/03
 *
 * @since JDK1.0
 */
public final class ByteBufferStream extends OutputStream {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** logger */
  private static final Log log = LogUtil.getLoggerDev();

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** The buffer where data is stored. */
  protected byte[] buf;

  /** The number of valid bytes in the buffer. */
  protected int count;

  //~ Constructors -----------------------------------------------------------------------------------------------------

  /**
   * Creates a new byte array output stream. The buffer capacity is initially 32 bytes, though its size increases
   * if necessary.
   */
  public ByteBufferStream() {
    this(32);
  }

  /**
   * Creates a new byte array output stream, with a buffer capacity of the specified size, in bytes.
   *
   * @param size the initial size.
   *
   * @exception IllegalArgumentException if size is negative.
   */
  public ByteBufferStream(final int size) {
    if (size < 0) {
      throw new IllegalArgumentException("Negative initial size: " + size);
    }

    buf = new byte[size];
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param newSize
   */
  public void ensureCapacity(final int newSize) {
    if (newSize > buf.length) {
      final byte[] newbuf = new byte[Math.max(buf.length << 1, newSize)];

      if (log.isInfoEnabled()) {
        log.info("ByteBufferStream.ensureCapacity : " + newSize + " <> " + buf.length);
      }

      System.arraycopy(buf, 0, newbuf, 0, count);
      buf = newbuf;
    }
  }

  /**
   * Writes the specified byte to this byte array output stream.
   *
   * @param b the byte to be written.
   */
  public void write(final int b) {
    int newcount = count + 1;

    if (newcount > buf.length) {
      final byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];

      log.error("resize : " + newcount + " <> " + newbuf.length);

      System.arraycopy(buf, 0, newbuf, 0, count);
      buf = newbuf;
    }

    buf[count] = (byte) b;
    count = newcount;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code> to this byte
   * array output stream.
   *
   * @param b the data.
   * @param off the start offset in the data.
   * @param len the number of bytes to write.
   *
   * @throws IndexOutOfBoundsException
   */
  @Override
  public void write(final byte[] b, final int off, final int len) {
    if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }

    int newcount = count + len;

    if (newcount > buf.length) {
      final byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];

      log.error("resize : " + newcount + " <> " + newbuf.length);

      System.arraycopy(buf, 0, newbuf, 0, count);
      buf = newbuf;
    }

    System.arraycopy(b, off, buf, count, len);
    count = newcount;
  }

  /**
   * Writes the complete contents of this byte array output stream to the specified output stream argument, as if
   * by calling the output stream's write method using <code>out.write(buf, 0, count)</code>.
   *
   * @param out the output stream to which to write the data.
   *
   * @exception IOException if an I/O error occurs.
   */
  public void writeTo(final OutputStream out) throws IOException {
    out.write(buf, 0, count);
  }

  /**
   * Resets the <code>count</code> field of this byte array output stream to zero, so that all currently
   * accumulated output in the ouput stream is discarded. The output stream can be used again, reusing the already
   * allocated buffer space.
   *
   * @see java.io.ByteArrayInputStream#count
   */
  public void reset() {
    count = 0;
  }

  /**
   * Creates a newly allocated byte array. Its size is the current size of this output stream and the valid
   * contents of the buffer have been copied into it.
   *
   * @return the current contents of this output stream, as a byte array.
   *
   * @see java.io.ByteArrayOutputStream#size()
   */
  public byte[] toByteArray() {
    final byte[] newbuf = new byte[count];

    System.arraycopy(buf, 0, newbuf, 0, count);

    return newbuf;
  }

  /**
   * Returns the current size of the buffer.
   *
   * @return the value of the <code>count</code> field, which is the number of valid bytes in this output stream.
   *
   * @see java.io.ByteArrayOutputStream#count
   */
  public int size() {
    return count;
  }

  /**
   * Converts the buffer's contents into a string, translating bytes into characters according to the platform's
   * default character encoding.
   *
   * @return String translated from the buffer's contents.
   *
   * @since JDK1.1
   */
  @Override
  public String toString() {
    return new String(buf, 0, count);
  }

  /**
   * Converts the buffer's contents into a string, translating bytes into characters according to the specified
   * character encoding.
   *
   * @param enc a character-encoding name.
   *
   * @return String translated from the buffer's contents.
   *
   * @throws UnsupportedEncodingException If the named encoding is not supported.
   *
   * @since JDK1.1
   */
  public String toString(final String enc) throws UnsupportedEncodingException {
    return new String(buf, 0, count, enc);
  }

  /**
   * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in this class can be called after the
   * stream has been closed without generating an <tt>IOException</tt>.<p></p>
   */
  @Override
  public void close() {
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
