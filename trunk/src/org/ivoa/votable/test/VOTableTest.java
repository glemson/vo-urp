/*
 * VOTableTest.java
 * 
 * Author lemson
 * Created on Dec 23, 2008
 */
package org.ivoa.votable.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ivoa.votable.*;

public class VOTableTest {

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception{
    // TODO Auto-generated method stub
    VOTABLE votable = new VOTABLE();
    AnyTEXT descr = new AnyTEXT();
    descr.getContent().add("bla");
    descr.getContent().add("blabla");
    votable.setDESCRIPTION(descr);

    Resource r = new Resource();
    votable.getCOOSYSAndGROUPSAndPARAMS().add(r);
    descr = new AnyTEXT();
    descr.getContent().add("foo");
    r.setDESCRIPTION(descr);
    r.setName("aResource");
    Table t = new Table();
    t.setName("aTable");
    r.getINFOSAndCOOSYSAndGROUPS().add(t);

    JAXBContext jc = JAXBContext.newInstance( "org.ivoa.votable" );
    Marshaller m = jc.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    m.marshal(votable, System.out);
    
    
    Unmarshaller um = jc.createUnmarshaller();
    Object o = um.unmarshal(new File("E:\\workspaces\\eclipse\\SimDB\\test\\testVOTable.xml"));
    if(o instanceof VOTABLE)
      System.out.println(o);
  }

}