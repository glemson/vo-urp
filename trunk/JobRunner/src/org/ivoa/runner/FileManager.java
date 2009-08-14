package org.ivoa.runner;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ivoa.bean.LogSupport;
import org.ivoa.conf.Configuration;
import org.ivoa.util.FileUtils;


/**
 * This class manages files & folders
 *
 * @author laurent bourges (voparis)
 */
public final class FileManager extends LogSupport {
  //~ Constants --------------------------------------------------------------------------------------------------------
  /** underscore char */
  public static final String UNDERSCORE = "_";
  /** dat extension (ascii) */
  public static final String DAT_EXT = ".dat";
  /** png extension */
  public static final String PNG_EXT = ".png";
  /** xml extension */
  public static final String XML_EXT = ".xml";
  /** xml extension */
  public static final String FITS_EXT = ".fits";
  /** template folder */
  public static final String TEMPLATES = Configuration.getInstance().getProperty("template.folder") + "/";
  /** legacy applications root folder */
  public static final String LEGACYAPPS = Configuration.getInstance().getProperty("legacyapps.folder") + "/";
  /** temporary folder to store runner files */
  public static final String RUNNER = Configuration.getInstance().getProperty("runner.folder") + "/";
  /** temporary folder to store runner files */
  public static final String ARCHIVE = Configuration.getInstance().getProperty("archive.folder") + "/";
  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  private FileManager() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------


  public static File getRunnerFolder() {
    return getOrCreateFolder(RUNNER);
  }
  
  public static File getOrCreateFolder(final String folder) {
    File f = FileUtils.getDirectory(folder);
    if (f == null) {
      // f does not exist
      f = new File(folder);
      f.mkdirs();
    }
    return f;
  }
  
  public static void moveFile(final File sourceFile, final File destFile) {
    if (!sourceFile.exists()) {
      return;
    }
    if (destFile.exists()) {
      // To be sure for remove operation :
      destFile.delete();
    }
    boolean success = sourceFile.renameTo(destFile);
    if (!success) {
      // File was not successfully moved
      throw new IllegalStateException("unable to move file " + sourceFile.getAbsolutePath() + " to "+destFile.getAbsolutePath());
    }      
  }

  public static void moveDir(String s_sourceDir, String s_targetDir)
  {
	  File sourceDir = new File(s_sourceDir);
	  File targetDir = new File(s_targetDir);
	  if(!targetDir.exists())
		  targetDir.mkdirs();
	  if(sourceDir.isDirectory() && targetDir.isDirectory())
		  moveDir(sourceDir, targetDir);
	  else
	  {} // TODO do something
		  
  }
  
  public static void moveDir(File sourceDir, File targetDir)
  {
	  if(!targetDir.exists())
		  targetDir.mkdirs();
	  if (sourceDir.isDirectory()) {
	        final File[] children = sourceDir.listFiles();
	        for(File file : children)
	        {
	        	if(file.isFile())
	        		moveFile(file, new File(targetDir, file.getName()));
	        	else
	        		moveDir(file, new File(targetDir, file.getName()));
	        }
	  }
	  else {} // TODO do something?
  }
  
  public static void getFileListForZip(File dir, List<File> list)
  {
	  if (dir.isDirectory()) {
	     File[] files = dir.listFiles();
	     for(File file : files)
	     {
	        list.add(file);
	        if(file.isDirectory())
	        	getFileListForZip(file, list);
	     }
	  }
	  }
  public static List<File> getFileListForZip(File dir)
  {
     List<File> list = new ArrayList<File>();
     getFileListForZip(dir, list);
     return list;
  }

  
  public static File getSessionFolder(final String sessionId) {
	  return getSessionFolder(sessionId, null);
  }
  
  public static File getSessionFolder(final String sessionId, String user) {
    return getOrCreateFolder(getRunnerFolder() + File.separator + (user != null && user.trim().length() > 0?user+File.separator:"") +  sessionId);
  }

  public static File getArchiveUserFolder(String relativePath) {
	    return getOrCreateFolder(ARCHIVE + relativePath);
	  }  
  
  public static void purgeSessionFolder(final String sessionId) {
    final File dir = getSessionFolder(sessionId);
    deleteDirectoryFiles(dir);
    dir.delete();
  }
  
  public static void purgeRunnerFolder() {
    deleteDirectoryFiles(getRunnerFolder());
  }

  /* 
   * Deletes all files in a directory
   * Returns true if all deletions were successful.
   * If a deletion fails, the method stops attempting to delete and returns false.
   * @param dir directory to purge
   * @param delay time before removing a file
   */
  public static boolean deleteDirectoryFiles(final File dir) {
	  if (log.isWarnEnabled()) {
		  log.warn("FileManager.deleteDirectoryFiles : " + dir.getAbsolutePath());
	  }
    if (dir.isDirectory()) {
      boolean success;
      File f;
      final String[] children = dir.list();
      for (int i = 0, size = children.length; i < size; i++) {
        f = new File(dir, children[i]);
        
        // note : f can be a file or a folder !
        if (f.isDirectory()) {
        	deleteDirectoryFiles(f);
        }
        
        success = f.delete();
        if (!success) {
          return false;
        }
      }
    }
    return false;
  }  
}
//~ End of file --------------------------------------------------------------------------------------------------------
