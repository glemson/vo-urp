package org.ivoa.jalopy;
import java.io.File;
import java.io.FileFilter;
import java.util.List;

import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.language.JavaNode;
import de.hunsicker.jalopy.storage.History;


/**
 * TODO 
 */
public class Formatter {

  private String convention = null;
  private Jalopy jalopy;
  
  private FileFilter filter = new FileFilter()
  {
    public boolean accept(File pathname) 
    {
      return pathname.getAbsolutePath().endsWith(".java");
    }
  };
    /**
     * 
     */
    public Formatter(String convention) throws Exception {
        super();
        jalopy = new Jalopy();
        if(convention != null)
          Jalopy.setConvention(new File(convention));
    }
    
    
    public static void main(String[] args)
    {
      try
      {
        String convention = null;
        if(args.length < 1)
        {
          System.out.println("usage: Formatter <dir> [<convention-path>]");
          return;
        }
        File src = new File(args[0]);
        if(args.length > 1)
          convention = args[1];
        Formatter formatter = new Formatter(convention);
        formatter.format(src);
      }
      catch(Throwable t)
      {
        t.printStackTrace();
      }
    }
    	
    private void format(File src) throws Exception
    {
      if(src.isFile() && filter.accept(src))
      {
          jalopy.setInput(src);
          jalopy.setOutput(src);
          jalopy.setHistoryPolicy(History.Policy.DISABLED);
          jalopy.format();
          if(jalopy.getState() == Jalopy.State.OK)
              System.out.println("Formatted "+src.getAbsolutePath());
          else if(jalopy.getState() == Jalopy.State.WARN)
              System.out.println("Formatted "+src.getAbsolutePath()+" with warnings");
          else if(jalopy.getState() == Jalopy.State.ERROR)
              System.out.println("Failed formatting "+src.getAbsolutePath());
      }
      else if(src.isDirectory())
      {
        File[] files = src.listFiles();
        for(File file : files)
          format(file);
      }
      
    }
}
