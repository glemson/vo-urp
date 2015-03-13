package org.vo.urp.test.jaxb;

import org.ivoa.bean.LogSupport;
import org.ivoa.dm.ModelFactory;
import org.ivoa.dm.model.MetadataObject;

import org.ivoa.env.ApplicationMain;
import org.ivoa.jaxb.XmlBindException;

import org.ivoa.resource.protocol.InputParameter;
import org.ivoa.resource.protocol.Simulator;

/**
 * This class tests the ModelFactory marshall / unmarshall functions
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class XMLTests extends LogSupport implements ApplicationMain {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /**
   * local /test folder
   */
  public static final String TEST_PATH = "../test/";
  /**
   * xml file extension
   */
  public static final String XML_EXT = ".xml";

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** ModelFactory */
  private ModelFactory mf = ModelFactory.getInstance();

  //~ Constructors -----------------------------------------------------------------------------------------------------
  /**
   * Constructor
   */
  public XMLTests() {
    /* no-op */
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------
  /**
   * This method is the main entry point to execute test code
   *
   * @param args command line arguments
   */
  public void run(final String[] args) {
    log.info("XMLTests.run : enter");

    testMarshall();

    log.info("XMLTests.run : exit");
  }

  /**
   * Marshalls a simple Simulation to XML
   */
  public void testMarshall() {
    // Always declare variables as final to avoid instable code :
    final Simulator s = new Simulator();

    s.setName("Gadget");
    s.setDescription("Volker's code");

    final InputParameter p1 = new InputParameter(s);

    p1.setName("H0");
    p1.setDatatype(org.ivoa.meta.DataType.REAL);

    final InputParameter p2 = new InputParameter(s);

    p2.setName("Lambda_0");
    p2.setDatatype(org.ivoa.meta.DataType.REAL);

    testMarshall(s);
  }

  /**
   * Dump the given MetadataObject instance as an XML document to test marshalling
   *
   * @param o object to process
   */
  public void testMarshall(final MetadataObject o) {
    log.info("XMLTests.testMarshall : result = " + mf.marshallObject(o, 4096));
  }

  /**
   * Save the given MetadataObject instance as an XML document to test marshalling
   *
   * @param o object to process
   * @param filePath file to produce
   */
  public void saveMarshall(final MetadataObject o, final String filePath) {
    // create an Unmarshaller
    mf.marshallObject(filePath, o);
  }

  /**
   * Loads an XML document to test unmarshalling
   *
   * @param filePath document to load
   * @return metadataObject complete java model
   * @throws XmlBindException if the xml unmarshall operation failed
   * @throws IllegalArgumentException if the xml document is not valid
   */
  public MetadataObject testUnMashall(final String filePath) throws XmlBindException, IllegalArgumentException {
    // validate first:
    if (mf.validate(filePath)) {
      return mf.unmarshallToObject(filePath);
    }
    throw new IllegalArgumentException("The given xml document is not a valid document against xml schema (xsd) : '" + filePath + "' !");
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
