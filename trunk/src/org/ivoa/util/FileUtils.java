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
import org.apache.commons.logging.Log;


/**
 * File Utils : finds a file in classpath (jar)
 * 
 * @author laurent bourges (voparis)
 */
public final class FileUtils {

  private final static int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
  private final static int DEFAULT_WRITE_BUFFER_SIZE = 64 * 1024;
  /** logger */
  protected static final Log log = LogUtil.getLoggerDev();

  private FileUtils() {
  }

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
    } catch (IOException ioe) {
      throw new RuntimeException("Failure when loading file in classpath : " + fileName, ioe);
    }
  }

  public static void closeStream(final InputStream in) {
    if (in != null) {
      try {
        in.close();
      } catch (IOException ioe) {
        log.error("FileUtils.closeStream : io close failure : ", ioe);
      }
    }
  }

  public static void closeStream(final OutputStream out) {
    if (out != null) {
      try {
        out.close();
      } catch (IOException ioe) {
        log.error("FileUtils.closeStream : io close failure : ", ioe);
      }
    }
  }

  // File methods :
  private static File getExistingFile(final String path) {
    if (!StringUtils.isEmpty(path)) {
      final File file = new File(path);

      if (file.exists()) {
        return file;
      }
    }

    return null;
  }

  public static File getDirectory(final String path) {
    final File dir = getExistingFile(path);

    if (dir != null && dir.isDirectory()) {
      return dir;
    }

    return null;
  }

  public static File getFile(final String path) {
    final File file = getExistingFile(path);

    if (file != null && file.isFile()) {
      return file;
    }

    return null;
  }

// writers :  
  public static Writer openFile(final String absoluteFilePath) {
    return openFile(absoluteFilePath, DEFAULT_WRITE_BUFFER_SIZE);
  }

  public static Writer openFile(final String absoluteFilePath, final int bufferSize) {
    if (!StringUtils.isEmpty(absoluteFilePath)) {
      return openFile(new File(absoluteFilePath), bufferSize);
    }
    return null;
  }

  public static Writer openFile(final File file) {
    return openFile(file, DEFAULT_WRITE_BUFFER_SIZE);
  }

  public static Writer openFile(final File file, final int bufferSize) {
    try {
      return new BufferedWriter(new FileWriter(file), bufferSize);
    } catch (IOException ioe) {
      log.error("FileUtils.openFile : io failure : ", ioe);
    }
    return null;
  }

  public static Writer closeFile(final Writer w) {
    if (w != null) {
      try {
        w.close();
      } catch (IOException ioe) {
        log.error("FileUtils.closeFile : io close failure : ", ioe);
      }
    }
    return null;
  }

// readers :  
  public static Reader readFile(final String absoluteFilePath) {
    return readFile(absoluteFilePath, DEFAULT_READ_BUFFER_SIZE);
  }

  public static Reader readFile(final String absoluteFilePath, final int bufferSize) {
    return readFile(getFile(absoluteFilePath), bufferSize);
  }

  public static Reader readFile(final File file) {
    return readFile(file, DEFAULT_READ_BUFFER_SIZE);
  }

  public static Reader readFile(final File file, final int bufferSize) {
    try {
      return new BufferedReader(new FileReader(file), bufferSize);
    } catch (IOException ioe) {
      log.error("FileUtils.readFile : io failure : ", ioe);
    }
    return null;
  }

  public static Reader closeFile(final Reader r) {
    if (r != null) {
      try {
        r.close();
      } catch (IOException ioe) {
        log.error("FileUtils.closeFile : io close failure : ", ioe);
      }
    }
    return null;
  }
}
