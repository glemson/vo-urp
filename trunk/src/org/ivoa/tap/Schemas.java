/*
 * Schema.java
 *
 * Author lemson
 * Created on Oct 29, 2008
 */
package org.ivoa.tap;

import org.ivoa.dm.model.MetadataObject;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
@Entity
@Table(name = "TAP_SCHEMA.schemas")
public class Schemas {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @Id
  @Column(name = "schema_name", unique = true, nullable = false)
  private String                                                                  schema_name;
  /**
   * TODO : Field Description
   */
  @Column(name = "description", nullable = true)
  private String descriptioon;
  /**
   * TODO : Field Description
   */
  @Column(name = "utype", nullable = true)
  private String utype;
  /**
   * TODO : Field Description
   */
  @OrderBy(value = "table_name")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "schema")
  private List<Tables> tables;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getSchema_name() {
    return schema_name;
  }

  /**
   * TODO : Method Description
   *
   * @param schema_name 
   */
  public void setSchema_name(final String schema_name) {
    this.schema_name = schema_name;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getDescriptioon() {
    return descriptioon;
  }

  /**
   * TODO : Method Description
   *
   * @param descriptioon 
   */
  public void setDescriptioon(final String descriptioon) {
    this.descriptioon = descriptioon;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getUtype() {
    return utype;
  }

  /**
   * TODO : Method Description
   *
   * @param utype 
   */
  public void setUtype(final String utype) {
    this.utype = utype;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public List<Tables> getTables() {
    return tables;
  }

  /**
   * TODO : Method Description
   *
   * @param tables 
   */
  public void setTables(final List<Tables> tables) {
    this.tables = tables;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
