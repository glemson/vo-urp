package org.ivoa.xml;

import net.sf.saxon.s9api.*;

import org.w3c.dom.Document;

import org.xml.sax.*;

import org.xml.sax.helpers.XMLFilterImpl;

import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;


/**
 * Some examples to show how the Saxon XQuery API should be used
 */
public class S9APIExamples {
  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Class is not instantiated, so give it a private constructor
   */
  private S9APIExamples() {
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * Method main
   *
   * @param argv arguments supplied from the command line. If an argument is supplied, it should be the name of a test,
   *        or "all" to run all tests, or "nonschema" to run all the tests that are not schema-aware.
   */
  public static void main(final String[] argv) {
    List tests = new ArrayList();

    tests.add(new S9APIExamples.QueryA());
    tests.add(new S9APIExamples.QueryB());
    tests.add(new S9APIExamples.QueryC());
    tests.add(new S9APIExamples.QueryD());
    tests.add(new S9APIExamples.QueryE());
    tests.add(new S9APIExamples.TransformA());
    tests.add(new S9APIExamples.TransformB());
    tests.add(new S9APIExamples.TransformC());
    tests.add(new S9APIExamples.TransformD());
    tests.add(new S9APIExamples.SchemaA());
    tests.add(new S9APIExamples.XPathA());

    //        tests.add(new S9APIExamples.TestJ());
    //        tests.add(new S9APIExamples.TestK());
    String test = "all";

    if (argv.length > 0) {
      test = argv[0];
    }

    boolean  found    = false;
    Iterator allTests = tests.iterator();

    while (allTests.hasNext()) {
      S9APIExamples.Test next = (S9APIExamples.Test) allTests.next();

      if (test.equals("all") || (test.equals("nonschema") && ! next.isSchemaAware()) || next.name().equals(test)) {
        found = true;

        try {
          System.out.println("\n==== " + next.name() + "====\n");
          next.run();
        } catch (final SaxonApiException ex) {
          handleException(ex);
        }
      }
    }

    if (! found) {
      System.err.println("Please supply a valid test name, or 'all' or 'nonschema' (" + test + "' is invalid)");
    }
  }

  /**
   * Handle an exception thrown while running one of the examples
   *
   * @param ex the exception
   */
  private static void handleException(final Exception ex) {
    System.out.println("EXCEPTION: " + ex);
    ex.printStackTrace();
  }

  //~ Inner Interfaces -------------------------------------------------------------------------------------------------

  private interface Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name();

    public boolean isSchemaAware();

    public void run() throws SaxonApiException;
  }

  //~ Inner Classes ----------------------------------------------------------------------------------------------------

  // PART 1: XQuery tests
  /**
   * Compile and execute a simple query taking no input, producing a document as its result and serializing this
   * directly to System.out
   */
  private static class QueryA implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "QueryA";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor        proc = new Processor(false);
      XQueryCompiler   comp = proc.newXQueryCompiler();
      XQueryExecutable exp  = comp.compile("<a b='c'>{5+2}</a>");
      Serializer       out  = new Serializer();

      out.setOutputProperty(Serializer.Property.METHOD, "xml");
      out.setOutputProperty(Serializer.Property.INDENT, "yes");
      out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
      out.setOutputStream(System.out);
      exp.load().run(out);
    }
  }

  /**
   * Show a query compiled using a reusable XQExpression object, taking a parameter (external variable), and
   * returning a sequence of integers
   */
  private static class QueryB implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "QueryB";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor        proc = new Processor(false);
      XQueryCompiler   comp = proc.newXQueryCompiler();
      XQueryExecutable exp  = comp.compile("declare variable $n external; for $i in 1 to $n return $i*$i");
      XQueryEvaluator  qe   = exp.load();

      qe.setExternalVariable(new QName("n"), new XdmAtomicValue(10));

      XdmValue result = qe.evaluate();
      int      total  = 0;

      for (final XdmItem item : result) {
        total += ((XdmAtomicValue) item).getLongValue();
      }

      System.out.println("Total: " + total);
    }
  }

  /**
   * Show a query taking input from the context item, reusing the compiled Expression object to run more than one
   * query in succession.
   */
  private static class QueryC implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "QueryC";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor        proc = new Processor(false);
      XQueryCompiler   comp = proc.newXQueryCompiler();
      XQueryExecutable exp  = comp.compile("contains(., 'e')");

      XQueryEvaluator qe = exp.load();

      qe.setContextItem(new XdmAtomicValue("apple"));

      XdmValue result = qe.evaluate();

      System.out.println("apple: " + ((XdmAtomicValue) result).getBooleanValue());

      qe = exp.load();
      qe.setContextItem(new XdmAtomicValue("banana"));
      result = qe.evaluate();
      System.out.println("banana: " + ((XdmAtomicValue) result).getBooleanValue());

      qe = exp.load();
      qe.setContextItem(new XdmAtomicValue("cherry"));
      result = qe.evaluate();
      System.out.println("cherry: " + ((XdmAtomicValue) result).getBooleanValue());
    }
  }

  /**
   * Show a query producing a DOM document as its output, and passing the DOM as input to a second query. Also
   * demonstrates declaring a namespace for use in the query
   */
  private static class QueryD implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "QueryD";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor      proc = new Processor(false);
      XQueryCompiler comp = proc.newXQueryCompiler();

      comp.declareNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

      XQueryExecutable exp = comp.compile("<temp>{for $i in 1 to 20 return <e>{$i}</e>}</temp>");

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      Document dom;

      try {
        dom = dfactory.newDocumentBuilder().newDocument();
      } catch (final ParserConfigurationException e) {
        throw new SaxonApiException(e);
      }

      exp.load().run(new DOMDestination(dom));

      XdmNode temp = proc.newDocumentBuilder().wrap(dom);

      exp = comp.compile("<out>{//e[xsd:integer(.) gt 10]}</out>");

      XQueryEvaluator qe = exp.load();

      qe.setContextItem(temp);

      Serializer out = new Serializer();

      out.setOutputStream(System.out);
      qe.run(out);
    }
  }

  /**
   * Show a query taking a SAX event stream as its input and producing a SAX event stream as its output.
   */
  private static class QueryE implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "QueryE";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor      proc = new Processor(false);
      XQueryCompiler comp = proc.newXQueryCompiler();

      comp.declareNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

      XQueryExecutable exp = comp.compile("<copy>{//ITEM[1]}</copy>");
      XQueryEvaluator  qe  = exp.load();

      File            inputFile = new File("data/books.xml");
      FileInputStream fis;

      try {
        fis = new FileInputStream(inputFile);
      } catch (final FileNotFoundException e) {
        throw new SaxonApiException(
          "Input file not found. The current directory should be the Saxon samples directory");
      }

      SAXSource source          = new SAXSource(new InputSource(fis));

      source.setSystemId(inputFile.toURI().toString());

      qe.setSource(source);

      ContentHandler ch = new XMLFilterImpl() {
        public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
                          throws SAXException {
          System.err.println("Start element {" + uri + "}" + localName);
        }

        public void endElement(final String uri, final String localName, final String qName)
                        throws SAXException {
          System.err.println("End element {" + uri + "}" + localName);
        }
      };

      qe.run(new SAXDestination(ch));
    }
  }

  // PART 2: XSLT tests
  /**
   * Compile and execute a simple transformation that applies a stylesheet to an input file, and serializing the
   * result to an output file
   */
  private static class TransformA implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "TransformA";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor      proc = new Processor(false);
      XsltCompiler   comp = proc.newXsltCompiler();
      XsltExecutable exp  = comp.compile(new StreamSource(new File("styles/books.xsl")));

      XdmNode    source = proc.newDocumentBuilder().build(new StreamSource(new File("data/books.xml")));
      Serializer out    = new Serializer();

      out.setOutputProperty(Serializer.Property.METHOD, "html");
      out.setOutputProperty(Serializer.Property.INDENT, "yes");
      out.setOutputFile(new File("books.html"));

      XsltTransformer trans = exp.load();

      trans.setInitialContextNode(source);
      trans.setDestination(out);
      trans.transform();

      System.err.println("Output written to books.html");
    }
  }

  /**
   * Show a transformation that takes no input file, but accepts parameters
   */
  private static class TransformB implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "TransformB";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor      proc = new Processor(false);
      XsltCompiler   comp = proc.newXsltCompiler();
      XsltExecutable exp  = comp.compile(new StreamSource(new File("styles/tour.xsl")));
      Serializer     out  = new Serializer();

      out.setOutputProperty(Serializer.Property.METHOD, "html");
      out.setOutputProperty(Serializer.Property.INDENT, "yes");
      out.setOutputFile(new File("tour.html"));

      XsltTransformer trans = exp.load();

      trans.setInitialTemplate(new QName("main"));
      trans.setDestination(out);
      trans.transform();

      System.err.println("Output written to tour.html");
    }
  }

  /**
   * Show a stylesheet being compiled once and then executed several times with different source documents. The
   * XsltTransformer object is serially reusable, but not thread-safe.
   */
  private static class TransformC implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "TransformC";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor      proc       = new Processor(false);
      XsltCompiler   comp       = proc.newXsltCompiler();
      String         stylesheet = "<xsl:transform version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                                  "  <xsl:param name='in'/>" +
                                  "  <xsl:template name='main'><xsl:value-of select=\"contains($in, 'e')\"/></xsl:template>" +
                                  "</xsl:transform>";
      XsltExecutable exp        = comp.compile(new StreamSource(new StringReader(stylesheet)));

      Serializer out = new Serializer();

      out.setOutputProperty(Serializer.Property.METHOD, "text");

      XsltTransformer t = exp.load();

      t.setInitialTemplate(new QName("main"));

      String[] fruit     = { "apple", "banana", "cherry" };
      QName    paramName = new QName("in");

      for (final String s : fruit) {
        StringWriter sw = new StringWriter();

        out.setOutputWriter(sw);
        t.setParameter(paramName, new XdmAtomicValue(s));
        t.setDestination(out);
        t.transform();
        System.err.println(s + ": " + sw.toString());
      }
    }
  }

  /**
   * Show the output of one stylesheet being passed for processing to a second stylesheet
   */
  private static class TransformD implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "TransformD";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor      proc       = new Processor(false);
      XsltCompiler   comp       = proc.newXsltCompiler();
      XsltExecutable templates1 = comp.compile(new StreamSource(new File("styles/books.xsl")));
      XdmNode        source     = proc.newDocumentBuilder().build(new StreamSource(new File("data/books.xml")));

      Serializer out = new Serializer();

      out.setOutputProperty(Serializer.Property.METHOD, "html");
      out.setOutputProperty(Serializer.Property.INDENT, "yes");
      out.setOutputFile(new File("books.html"));

      XsltTransformer trans1 = templates1.load();

      trans1.setInitialContextNode(source);

      String          stylesheet2 = "<xsl:transform version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                                    "  <xsl:template match='/'><xsl:value-of select=\"count(//*)\"/></xsl:template>" +
                                    "</xsl:transform>";
      XsltExecutable  templates2  = comp.compile(new StreamSource(new StringReader(stylesheet2)));
      XsltTransformer trans2      = templates2.load();
      XdmDestination  resultTree  = new XdmDestination();

      trans2.setDestination(resultTree);

      trans1.setDestination(trans2);
      trans1.transform();

      System.err.println(resultTree.getXdmNode());
    }
  }

  /**
   * Show schema validation of an input document.
   */
  private static class SchemaA implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "SchemaA";
    }

    public boolean isSchemaAware() {
      return true;
    }

    public void run() throws SaxonApiException {
      Processor     proc = new Processor(true);
      SchemaManager sm   = proc.getSchemaManager();

      sm.load(new StreamSource(new File("data/books.xsd")));

      try {
        SchemaValidator sv = sm.newSchemaValidator();

        sv.validate(new StreamSource(new File("data/books.xml")));
        System.err.println("First schema validation succeeded (as planned)");
      } catch (final SaxonApiException err) {
        System.err.println("First schema validation failed");
      }

      // modify books.xml to make it invalid
      XsltCompiler comp       = proc.newXsltCompiler();
      String       stylesheet = "<xsl:transform version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                                "  <xsl:import href='identity.xsl'/>" +
                                "  <xsl:template match='TITLE'><OLD-TITLE/></xsl:template>" + "</xsl:transform>";
      StreamSource ss         = new StreamSource(new StringReader(stylesheet));

      ss.setSystemId(new File("styles/books.xsl"));

      XsltTransformer trans = comp.compile(ss).load();

      trans.setSource(new StreamSource(new File("data/books.xml")));

      // Create a validator for the result of the transformation, and run the transformation
      // with this validator as its destination
      final List<Exception> errorList = new ArrayList<Exception>();

      try {
        SchemaValidator sv = sm.newSchemaValidator();

        // Create a custom ErrorListener for the validator, which collects validation
        // exceptions in a list for later reference
        ErrorListener listener = new ErrorListener() {
          public void error(final TransformerException exception)
                     throws TransformerException {
            errorList.add(exception);
          }

          public void fatalError(final TransformerException exception)
                          throws TransformerException {
            errorList.add(exception);
            throw exception;
          }

          public void warning(final TransformerException exception)
                       throws TransformerException {
            // no action
          }
        };

        sv.setErrorListener(listener);
        trans.setDestination(sv);
        trans.transform();
        System.err.println("Second schema validation succeeded");
      } catch (final SaxonApiException err) {
        System.err.println("Second schema validation failed (as intended)");

        if (! errorList.isEmpty()) {
          System.err.println(errorList.get(0).getMessage());
        }
      }
    }
  }

  /**
   * Demonstrate navigation of an input document using XPath and native methods.
   */
  private static class XPathA implements S9APIExamples.Test {
    //~ Methods --------------------------------------------------------------------------------------------------------

    public String name() {
      return "XPathA";
    }

    public boolean isSchemaAware() {
      return false;
    }

    public void run() throws SaxonApiException {
      Processor     proc  = new Processor(false);
      XPathCompiler xpath = proc.newXPathCompiler();

      xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration

      DocumentBuilder builder = proc.newDocumentBuilder();

      builder.setLineNumbering(true);
      builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

      XdmNode booksDoc = builder.build(new File("data/books.xml"));

      // find all the ITEM elements, and for each one display the TITLE child
      XPathSelector selector = xpath.compile("//ITEM").load();

      selector.setContextItem(booksDoc);

      QName titleName = new QName("TITLE");

      for (final XdmItem item : selector) {
        XdmNode title = getChild((XdmNode) item, titleName);

        System.err.println(title.getNodeName() + "(" + title.getLineNumber() + "): " + title.getStringValue());
      }
    }

    // Helper method to get the first child of an element having a given name.
    // If there is no child with the given name it returns null
    private static XdmNode getChild(final XdmNode parent, final QName childName) {
      XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, childName);

      if (iter.hasNext()) {
        return (XdmNode) iter.next();
      } else {
        return null;
      }
    }
  }
} //
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file
//
// The Initial Developer of the Original Code is Michael H. Kay.
//
// Contributor(s):
//

//~ End of file --------------------------------------------------------------------------------------------------------
