package org.ivoa.legacyapps;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ivoa.web.servlet.JobServlet;
import org.vourp.runner.model.LegacyApp;

public class LegacyAppRunner  {

	private LegacyApp legacyApp;
	
	/**
	 * Constructor that accepts a LegacyApp describing the legacy application.<br/>
	 * @param configFile
	 */
	public LegacyAppRunner(LegacyApp _legacyApp)
	{
		this.legacyApp = _legacyApp;
	}
	public LegacyAppRunner(String _legacyAppFile)
	{
    	try
    	{
        	this.legacyApp = null;
        	JAXBContext jc = JAXBContext.newInstance("org.ivoa.runner.apps");
        	//Create unmarshaller
        	Unmarshaller um = jc.createUnmarshaller();
        	Object o = um.unmarshal(new java.io.FileInputStream(_legacyAppFile));
        	if(o instanceof LegacyApp )
        		this.legacyApp = (LegacyApp)o;
        	else if(o instanceof JAXBElement)
        	{
        		JAXBElement<LegacyApp> je = (JAXBElement<LegacyApp>)o;
        		this.legacyApp =  je.getValue();
        	}
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	public static void main(String[] args)
	{
    	String configFile = "E:\\workspaces\\eclipse_runner\\JobRunner\\web\\apps\\Smac\\process.xml";
    	try {
    		LegacyAppRunner runner = new LegacyAppRunner(configFile);
    	
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		
	}
}
