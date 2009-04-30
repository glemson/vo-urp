/*
 * XSLTTransformer.java
 * 
 * Author Gerard Lemson
 * Created on 24 Dec 2008
 */
package org.ivoa.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.om.Item;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

public class XSLTTransformer {

  /**
   * @param sheetFile file name of XSLT style sheet, could be a URL
   * @param parameters assumes simple parameters
   * @param in the input stream containing the document to be transformed
   * @param out the output stream to which the result should be written 
   * @throws Exception 
   */
  public static void transform(String sheetFile, java.util.Map<String, String> parameters
      , InputStream in, OutputStream out)
    throws Exception
  {
    // use Saxon's s9api.
    // Could use Saxon's JAXP implementation, interfaces compatible with other XSLT processors such as Xalan. 
    Processor proc = new Processor(false);
    XsltCompiler comp = proc.newXsltCompiler();
    // next could be passed in if precompiled, or exists as singleton for the style sheet
    XsltExecutable exp = comp.compile(new StreamSource(new File(sheetFile)));
    // doc should probably be passed in as stream.
    XdmNode source = proc.newDocumentBuilder().build(new StreamSource(in));

 
    XsltTransformer trans = exp.load();
    trans.setInitialContextNode(source);
    for(String key : parameters.keySet())
      trans.setParameter(new QName(key), new XdmAtomicValue(parameters.get(key)));

    Serializer ser = new Serializer();
    ser.setOutputProperty(Serializer.Property.METHOD, "xml");
    ser.setOutputProperty(Serializer.Property.INDENT, "yes");
    ser.setOutputStream(out);
    
    trans.setDestination(ser);
    trans.transform();
  }
  
  
  public static void main(String[] args)
  {
    /*
    mimic:
        <xslt2 in="C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/normalised_experiment.xml" 
                out="C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/denormalised_experiment.xml" 
              style="C:/workspaces/eclipse3.2.2/SimDB/xslt/transform_simdb.xsl">
            <param name="resolverURL" expression="${xslt.document.resolverURL}" />
            <param name="debug_protocol" expression="C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/theProtocol.xml" />
            <param name="mode" expression="forward"/>
        </xslt2>
    
    */
    try
    {
    String infile = "C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/normalised_experiment.xml";
    String sheetFile = "C:/workspaces/eclipse3.2.2/SimDB/xslt/transform_experiment.xsl";
    sheetFile="C:/workspaces/eclipse_vo-urp/vo-urp.googlecode.com/input/simdb/views/dn/ExpToFromExp_dn.xslt";
    HashMap<String,String> p = new HashMap<String, String>();
    p.put("mode","forward");
    p.put("debug_protocol","C:/workspaces/eclipse3.2.2/SimDB/xslt/test/views/theProtocol.xml");
    
    transform(sheetFile, p, new FileInputStream(infile), System.out);
    }
    catch(Throwable t)
    {
      t.printStackTrace();
    }
  
  }


/**
   * @param sheetFile file name of XSLT style sheet, could be a URL
   * @param parameters assumes simple parameters
   * @param in the input stream containing the document to be transformed
   * @param out the output stream to which the result should be written 
   * @throws Exception 
   */
  public static void transform(String sheetFile, java.util.Map<String, String> parameters
      , InputStream in, Writer out)
    throws Exception
  {
    // use Saxon's s9api.
    // Could use Saxon's JAXP implementation, interfaces compatible with other XSLT processors such as Xalan. 
    Processor proc = new Processor(false);
    XsltCompiler comp = proc.newXsltCompiler();
    // next could be passed in if precompiled, or exists as singleton for the style sheet
    XsltExecutable exp = comp.compile(new StreamSource(new File(sheetFile)));
    // doc should probably be passed in as stream.
    XdmNode source = proc.newDocumentBuilder().build(new StreamSource(in));

 
    XsltTransformer trans = exp.load();
    trans.setInitialContextNode(source);
    for(String key : parameters.keySet())
      trans.setParameter(new QName(key), new XdmAtomicValue(parameters.get(key)));

    Serializer ser = new Serializer();
    ser.setOutputProperty(Serializer.Property.METHOD, "xml");
    ser.setOutputProperty(Serializer.Property.INDENT, "yes");
    ser.setOutputWriter(out);
    
    trans.setDestination(ser);
    trans.transform();
  }
  
  /**
   * @param sheetFile file name of XSLT style sheet, could be a URL
   * @param parameters assumes simple parameters
   * @param xmldoc_url the file URL containing the document to be transformed
   * @throws Exception 
   */
  public static String transform2string(String sheetFile, java.util.Map<String, String> parameters,
          final String xmldoc_url)
    throws Exception
  {
	  StringWriter out = new StringWriter();
	  URL xmldoc = new URL(xmldoc_url);
	  InputStream in = xmldoc.openStream();
	  transform(sheetFile, parameters, in, out);
	  in.close();
	  return out.toString();
  }

}
