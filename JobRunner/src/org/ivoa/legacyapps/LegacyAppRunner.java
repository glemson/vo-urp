package org.ivoa.legacyapps;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ivoa.runner.apps.Workflow;
import org.ivoa.runner.apps.Workflow;
import org.ivoa.web.servlet.JobServlet;

public class LegacyAppRunner  {

	private Workflow workflow;
	
	/**
	 * Constructor that accepts a LegacyApp describing the legacy application.<br/>
	 * @param configFile
	 */
	public LegacyAppRunner(Workflow _workflow)
	{
		this.workflow = _workflow;
	}
	public LegacyAppRunner(String _legacyAppFile)
	{
    	try
    	{
        	this.workflow = null;
        	JAXBContext jc = JAXBContext.newInstance("org.ivoa.runner.apps");
        	//Create unmarshaller
        	Unmarshaller um = jc.createUnmarshaller();
        	Object o = um.unmarshal(new java.io.FileInputStream(_legacyAppFile));
        	if(o instanceof Workflow )
        		this.workflow = (Workflow)o;
        	else if(o instanceof JAXBElement)
        	{
        		JAXBElement<Workflow> je = (JAXBElement<Workflow>)o;
        		this.workflow =  je.getValue();
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
