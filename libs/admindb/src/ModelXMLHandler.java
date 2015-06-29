import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ModelXMLHandler  extends DefaultHandler {

	public boolean isOK = false;
    public String created = null, version=null;
    public StringBuffer sb = null;
	private boolean inModel = false, inCreated = false, inVersion = false;

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if (!inModel) {
			super.endElement(uri, localName,qName);
		}else if (inCreated)
		{
			inCreated = false;
			created = sb.toString().trim();
		} else if(inVersion){
			inVersion = false;
			version = sb.toString().trim();
		} else if(qName.equals("model"))
		{
			isOK = true;
			throw new SAXException();
		}
	}

	@Override
	public void characters(char[] chars, int start, int length)
			throws SAXException {
		if (inCreated || inVersion)
			sb.append(chars,start,length);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException{
		if (qName.equals("model"))
			inModel = true;
		else if (inModel) {
			if (qName.equals("created"))
			{
				inCreated = true;
				sb = new StringBuffer();
			}
			else if(qName.equals("version"))
			{
				inVersion = true;
				sb = new StringBuffer();
			}
		}
	}

}