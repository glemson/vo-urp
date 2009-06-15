package org.ivoa.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.ivoa.bean.LogSupport;


/**
 * File Utils : finds a file in classpath (jar)
 *
 * @author laurent bourges (voparis)
 */
public final class FileUtils extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * default read buffer capacity : DEFAULT_READ_BUFFER_SIZE = 16K
   */
  private static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;

  /**
   * default write buffer capacity : DEFAULT_WRITE_BUFFER_SIZE = 16K
   */
  private static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Forbidden FileUtils constructor
   */
  private FileUtils() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @param fileName
   *
   * @return value TODO : Value Description
   *
   * @throws RuntimeException
   */
  public static final InputStream getSystemFileInputStream(final String fileName) {
    // Find properties in the classpath
    final URL url = FileUtils.class.getClassLoader().getResource(fileName);

    //ClassLoader.getSystemClassLoader().getSystemResource(fileName);
    if (url == null) {
      throw new RuntimeException("Unable to find file in classpath : " + fileName);
    }

    if (log.isInfoEnabled()) {
      log.info("FileUtils.getSystemFileInputStream : reading file : " + url);
    }

    try {
      return url.openStream();
    } catch (final IOException ioe) {
      throw new RuntimeException("Failure when loading file in classpath : " + fileName, ioe);
    }
  }

  /**
   * TODO : Method Description
   *
   * @param in
   */
  public static void closeStream(final InputStream in) {
    if (in != null) {
      try {
        in.close();
      } catch (final IOException ioe) {
        log.error("FileUtils.closeStream : io close failure : ", ioe);
      }
    }
  }

  /**
   * TODO : Method Description
   *
   * @param out
   */
  public static void closeStream(final OutputStream out) {
    if (out != null) {
      try {
        out.close();
      } catch (final IOException ioe) {
        log.error("FileUtils.closeStream : io close failure : ", ioe);
      }
    }
  }

  // File methods :
  /**
   * TODO : Method Description
   *
   * @param path
   *
   * @return value TODO : Value Description
   */
  private static File getExistingFile(final String path) {
    if (! JavaUtils.isEmpty(path)) {
      final File file = new File(path);

      if (file.exists()) {
        return file;
      }
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param path
   *
   * @return value TODO : Value Description
   */
  public static File getDirectory(final String path) {
    final File dir = getExistingFile(path);

    if ((dir != null) && dir.isDirectory()) {
      return dir;
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param path
   *
   * @return value TODO : Value Description
   */
  public static File getFile(final String path) {
    final File file = getExistingFile(path);

    if ((file != null) && file.isFile()) {
      return file;
    }

    return null;
  }

  // writers :  
  /**
   * TODO : Method Description
   *
   * @param absoluteFilePath
   *
   * @return value TODO : Value Description
   */
  public static Writer openFile(final String absoluteFilePath) {
    return openFile(absoluteFilePath, DEFAULT_WRITE_BUFFER_SIZE);
  }

  /**
   * TODO : Method Description
   *
   * @param absoluteFilePath
   * @param bufferSize
   *
   * @return value TODO : Value Description
   */
  public static Writer openFile(final String absoluteFilePath, final int bufferSize) {
    if (! JavaUtils.isEmpty(absoluteFilePath)) {
      return openFile(new File(absoluteFilePath), bufferSize);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param file
   *
   * @return value TODO : Value Description
   */
  public static Writer openFile(final File file) {
    return openFile(file, DEFAULT_WRITE_BUFFER_SIZE);
  }

  /**
   * TODO : Method Description
   *
   * @param file
   * @param bufferSize
   *
   * @return value TODO : Value Description
   */
  public static Writer openFile(final File file, final int bufferSize) {
    try {
      return new BufferedWriter(new FileWriter(file), bufferSize);
    } catch (final IOException ioe) {
      log.error("FileUtils.openFile : io failure : ", ioe);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param w
   *
   * @return value TODO : Value Description
   */
  public static Writer closeFile(final Writer w) {
    if (w != null) {
      try {
        w.close();
      } catch (final IOException ioe) {
        log.error("FileUtils.closeFile : io close failure : ", ioe);
      }
    }

    return null;
  }

  // readers :  
  /**
   * TODO : Method Description
   *
   * @param absoluteFilePath
   *
   * @return value TODO : Value Description
   */
  public static Reader readFile(final String absoluteFilePath) {
    return readFile(absoluteFilePath, DEFAULT_READ_BUFFER_SIZE);
  }

  /**
   * TODO : Method Description
   *
   * @param absoluteFilePath
   * @param bufferSize
   *
   * @return value TODO : Value Description
   */
  public static Reader readFile(final String absoluteFilePath, final int bufferSize) {
    return readFile(getFile(absoluteFilePath), bufferSize);
  }

  /**
   * TODO : Method Description
   *
   * @param file
   *
   * @return value TODO : Value Description
   */
  public static Reader readFile(final File file) {
    return readFile(file, DEFAULT_READ_BUFFER_SIZE);
  }

  /**
   * TODO : Method Description
   *
   * @param file
   * @param bufferSize
   *
   * @return value TODO : Value Description
   */
  public static Reader readFile(final File file, final int bufferSize) {
    try {
      return new BufferedReader(new FileReader(file), bufferSize);
    } catch (final IOException ioe) {
      log.error("FileUtils.readFile : io failure : ", ioe);
    }

    return null;
  }

  /**
   * TODO : Method Description
   *
   * @param r
   *
   * @return value TODO : Value Description
   */
  public static Reader closeFile(final Reader r) {
    if (r != null) {
      try {
        r.close();
      } catch (final IOException ioe) {
        log.error("FileUtils.closeFile : io close failure : ", ioe);
      }
    }

    return null;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
