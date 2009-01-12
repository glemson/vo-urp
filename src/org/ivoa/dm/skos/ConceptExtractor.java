/*
 * ExampleContentHandler.java
 * 
 * Author lemson
 * Created on Dec 16, 2008
 */
package org.ivoa.dm.skos;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class ConceptExtractor implements ContentHandler {

  public static final String skosURI = "http://www.w3.org/2004/02/skos/core#";
  public List<Concept> concepts = new java.util.ArrayList<Concept>();
  public Concept currentConcept;
  public boolean inPrefLabel = false;
  public boolean inDefinition = false;
  
    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(
            String namespaceURI, String localName, String qName, Attributes atts)
                throws SAXException {
      if(skosURI.equals(namespaceURI) && "Concept".equals(localName))
      {
        currentConcept = new Concept();
        concepts.add(currentConcept);
      }
      else if(skosURI.equals(namespaceURI) && currentConcept !=null && "prefLabel".equals(localName))
        inPrefLabel = true;
      else if(skosURI.equals(namespaceURI) &&  currentConcept !=null && "definition".equals(localName))
        inDefinition = true;
      
    }

    public void endElement(
            String namespaceURI, String localName, String qName)
                throws SAXException {
      if(skosURI.equals(namespaceURI))
      {
        if(currentConcept != null && "Concept".equals(localName))
          currentConcept = null;
        else if(inPrefLabel && "prefLabel".equals(localName))
          inPrefLabel = false;
        else if(inDefinition && "definition".equals(localName))
          inDefinition = false;
      }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

      String s = new String(ch,start,length);
      if(inPrefLabel)
          currentConcept.name=s;
      else if(inDefinition)
          currentConcept.description = s;

    }

    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }
    
    public List<Concept> run(String uri) throws Exception {
        org.xml.sax.XMLReader parser = 
            javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        parser.setContentHandler(this);
        parser.setFeature("http://xml.org/sax/features/namespaces", true);
        parser.parse(uri);
        return concepts;
    }
    
    public static void main(String[] args) throws Exception 
    {
      ConceptExtractor ce = new ConceptExtractor();
      List<Concept> concepts = ce.run(args[0]);
      for(Concept c : concepts)
        System.out.println(c.toString());
      
    }
}
