package org.gavo.sam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;

public class SeSAMModel extends Model {
	
	  public static final String FREE_PARAM_DELIMITER = "___";
	  public static final String FIXED_PARAM_DELIMITER = "%%%";
	  public static final String FILE_PARAM_DELIMITER = "###";
	  public String paramTemplate;
	  public LinkedHashMap<String, Map<String,String>> defaultModels;

	public SeSAMModel(String templateFile)
	{
		init(templateFile);
	}
	private void init(String templateFile)
	{
		File f = new File(templateFile);
		if(f.exists() && f.isFile())
		{
		    try {
		    	StringBuffer sb = new StringBuffer();
		      BufferedReader reader = new BufferedReader(new FileReader(f));
		      String line;
		      while ((line = reader.readLine()) != null) {
		    	  String lt = line.trim();
		    	  if(lt.length() == 0 || lt.startsWith("%"))
		    		  sb.append(lt);
		    	  else
		    		  sb.append(createParameter(lt));
		    	  sb.append("\n");
		      }
		      paramTemplate=sb.toString();
		      reader.close();
		    } catch (IOException e) {
		      // TODO do something or initialise differently ?
		    }
			
		}
	}

	/**
	 * pre: line is trimmed
	 * @param line
	 * @return The line as it should appear in the parameters template string.
	 */
	private String createParameter(String line)
	{
		StringBuffer newLine = new StringBuffer();
		Parameter p = new Parameter();
		String[] words = line.split("[ \t]+");
		if(words.length < 2)
			return null; // TODO error?
		p.name=words[0];
		newLine.append(p.name);
		if(words.length > 2)
			p.group = new Parameter[words.length -1];
		for(int w = 1; w < words.length; w++)
		{
			String word = words[w];
			Parameter theParam = p;
			if(p.isGroup())
			{
				theParam = new Parameter();
				p.group[w-1] = theParam;
				theParam.name=p.name+"_"+(w-1);
			}
			if(word.startsWith(FREE_PARAM_DELIMITER) && word.endsWith(FREE_PARAM_DELIMITER))
			{
				theParam.isFixed = false;
				newLine.append(" ").append(word);
				theParam.value = "-1";
			}
			else if(word.startsWith(FILE_PARAM_DELIMITER) && word.endsWith(FILE_PARAM_DELIMITER))
			{
				theParam.isFixed = false;
				newLine.append(" ").append(word);
				theParam.datatype = Datatype._file;
			}
			else if(word.startsWith(FIXED_PARAM_DELIMITER) && word.endsWith(FIXED_PARAM_DELIMITER))
			{
				theParam.isFixed = true;
				newLine.append(" ").append(word);
			}
			else
			{
				theParam.isFixed = true;
				newLine.append(" ").append(word);
			}
		}
		parameters.put(p.name, p);
		return newLine.toString();
	}
	
	private Map<String,String> extractFreeParameters(File file){
		if(!(file.exists() && file.isFile()))
				return null;
		Hashtable<String, String> map = new Hashtable<String, String>();
		try
		{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null)
		{
			String lt = line.trim();
			if(lt.length() == 0 || lt.startsWith("%"))
				continue;
			String[] words = lt.split("[ \t]+");
			Parameter p = parameters.get(words[0]);
			if(p == null)
				continue;
			else if(p.isGroup())
			{
				if(words.length != p.group.length+1)
					throw new IllegalArgumentException("file is not a good parameters file for the model");
				int i = 1;
				for(Parameter gp : p.group)
				  if(!gp.isFixed) map.put(gp.name, words[i++]);
			} else
				if(!p.isFixed) map.put(p.name, words[1]);
		}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return map;
	}
	
	  /**
	   * Find models in model directory and instantiate objects for them.<br/>
	   * @param modelsDir
	   */
	  public void instantiateModels(String modelsDir)
	  {
		  defaultModels = new LinkedHashMap<String, Map<String, String>>();
		  File dir = new File(modelsDir);
		  if(dir.exists() && dir.isDirectory())
		  {
		    File[] dirs = dir.listFiles();
		    for(File d : dirs)
		    {
		    	if(d.isFile())
		    		continue;
		    	String name = d.getName();
		    	File params = new File(d.getAbsolutePath()+"/esam_params");
		    	if(!params.exists())
		    		continue;
		    	Map<String,String> parameterValues = extractFreeParameters(params);
		    	
		    	// find parameter files, if they exist
		    	String[] files = d.list();
		    	for(String f : files)
		    	{
		    		if("esam_params".equals(f))
		    			continue;
		    		Parameter p = parameters.get(f);
		    		if(p != null && p.datatype == Datatype._file)
		    		{
		    			if(p.possibleValues == null)
		    			{
		    				p.possibleValues = new ArrayList<String>();
		    				p.possibleValues.add("-1");
		    			}
		    			p.possibleValues.add(name);
		    		}
		    	}
		    	
		    	defaultModels.put(name,parameterValues);
		    }
		  }
		  
	  }
	  
	  public Map<String,String> getDefaultModel(String name)
	  {
		  return defaultModels.get(name);
	  }
	
}
