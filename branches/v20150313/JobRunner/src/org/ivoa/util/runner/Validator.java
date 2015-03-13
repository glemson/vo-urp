package org.ivoa.util.runner;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.vourp.runner.model.Choice;
import org.vourp.runner.model.EnumeratedParameter;
import org.vourp.runner.model.LegacyApp;
import org.vourp.runner.model.Literal;
import org.vourp.runner.model.ParameterDeclaration;
import org.vourp.runner.model.Validvalues;

public class Validator {

	 private Hashtable<String, String> errorMessages;
	 private boolean isValid = true;
	
	 private Map parameters;
	 private LegacyApp legacyApp;
	 
     private String dateFormatPattern = "yyyy-MM-dd HH:mm:ss.SSS";
	 private SimpleDateFormat dateFormat = null;

		

	 
	 public Validator(LegacyApp legacyApp, Map parameters, String dateFormatPattern)
	 {
		 this.dateFormatPattern = dateFormatPattern;
		 this.dateFormat = new SimpleDateFormat(dateFormatPattern);
		 this.legacyApp = legacyApp;
		 this.parameters = parameters;
		 validate();
	 }
	 
	  /**
	   * Checks validity of the parameter settings for this runcontext.<br/>
	   * If invalid parameter settings are detected their names are added into a hashtable, together with a message indicating the problem.
	   * @param app
	   * @return
	   */
	  public void validate()
	  {
		  for(ParameterDeclaration decl : legacyApp.getParameter())
		  {
			  String p = (String)parameters.get(decl.getName());
			  isValid &= isValid(p, decl);
		  }
	  }
		/** 
		 * Validate the value based on the specified parameter declaration.<br/>
		 * Return false if the the value is invalid and set the message to a corresponding value.
		 */
		public boolean isValid(String value, ParameterDeclaration decl)
		{
			String message = null;
			boolean ok = true;
			if(value == null)
			{
				// TODO test whether parameter may be null.
				// in this context check also for dependencies
			}
			else if(decl instanceof EnumeratedParameter)
			{
				EnumeratedParameter ep = (EnumeratedParameter)decl;
				Validvalues vvs = null; 
				if(ep.getDependency() != null)
				{
					EnumeratedParameter master = (EnumeratedParameter)ep.getDependency().getMaster();
					String masterValue = (String)parameters.get(master.getName());
					if(masterValue != null)
					{
						for(Choice choice : ep.getDependency().getChoice())
						{
							if(choice.getIf().equalsIgnoreCase(masterValue))
								vvs = (Validvalues)choice.getChoose();
						}
					}
				} else
				    vvs = ep.getValidvalues().get(0);
				
				if(vvs != null)
				{
					boolean found = false;
					
					for(Literal l : vvs.getLiteral())
					{
						if(l.getValue().equalsIgnoreCase(value))
						{
							found = true;
							break;
						}
					}
					ok = found;
					if(!found)
						message = String.format("Parameter %s must take values from list.", decl.getName());
				}
			}
			else
			{
				value=value.trim();
				switch(decl.getDatatype()) {
				case BOOLEAN :
					ok = "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
					message = String.format("Parameter %s must take value 'true' or 'false'", decl.getName());
					break;
				case INTEGER :
					try
					{
						int i = Integer.parseInt(value);
					} catch(NumberFormatException nef)
					{
						ok = false;
						message = String.format("Parameter %s must be an integer.", decl.getName());
					}
					break;
				case FLOAT:
					try
					{
						float i = Float.parseFloat(value);
					} catch(NumberFormatException nef)
					{
						ok = false;
						message = String.format("Parameter %s must be a float.", decl.getName());
					}
					break;
				case DOUBLE:
					try
					{
						double i = Double.parseDouble(value);
					} catch(NumberFormatException nef)
					{
						ok = false;
						message = String.format("Parameter %s must be a double.", decl.getName());
					}
					break;
				case SHORT :
					try
					{
						short i = Short.parseShort(value);
					} catch(NumberFormatException nef)
					{
						ok = false;
						message = String.format("Parameter %s must be a short.", decl.getName());
					}
					break;
				case ANY_URI :
					try
					{
						URI i = URI.create(value);
					} catch(IllegalArgumentException nef)
					{
						ok = false;
						message = String.format("Parameter %s must be a valid URI.", decl.getName());
					}
					break;
				case DATETIME:
					try
					{
						Date i = dateFormat.parse(value); // to be generalised to use declarative data format
					} catch(java.text.ParseException pe)
					{
						ok = false;
						message = String.format("Parameter %s must be a valid Date, formatted like %s.", decl.getName(), dateFormatPattern);
					}
					break;
				case STRING:
					// todo add parsing according to specified regexp.
				}
			}
			if(!ok)
				errorMessages.put(decl.getName(), message);
			return ok;
		}
}
