/*
 * TestTAP_SCHEMA.java
 * 
 * Author lemson
 * Created on Oct 30, 2008
 */
package org.vo.urp.test;

import java.util.List;

import javax.persistence.EntityManager;

import org.ivoa.jpa.JPAFactory;
import org.ivoa.tap.Columns;
import org.ivoa.tap.Schemas;
import org.ivoa.tap.Tables;

public class TestTAP_SCHEMA {

  public static void main(String[] args)
  {
	  String jpa_pu = "VO-URP-PU"; //args[0]; // TODO check
	  
    final JPAFactory jf = JPAFactory.getInstance(jpa_pu);

    EntityManager em = jf.getEm();

    List<Object> so = em.createQuery("select item from Schemas item where item.schema_name <> 'TAP_SCHEMA' order by item.schema_name").getResultList();

    if(so == null)
      return;
    
    for(Object o : so)
    {
      Schemas s = (Schemas)o;
      System.out.println("Schema: "+s.getSchema_name());
      for(Tables to : s.getTables())
      {

      Tables t = (Tables)to;
      System.out.println("Table: "+t);
      for(Columns c : t.getColumns())
        System.out.println("Column: "+c.getColumn_name());
      }   
    }
  }
}
