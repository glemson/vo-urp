/*
 * Schema.java
 * 
 * Author lemson
 * Created on Oct 29, 2008
 */
package org.ivoa.tap;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.ivoa.dm.model.MetadataObject;

@Entity
@Table(name = "TAP_SCHEMA.schemas")
public class Schemas {
  @Id
  @Column(name = "schema_name", unique = true, nullable = false)
  private String schema_name;
  
  @Column(name = "description", nullable = true)
  private String descriptioon;

  @Column(name = "utype", nullable = true)
  private String utype;
  
  @OrderBy(value = "table_name")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "schema")
  private List<Tables> tables;
  
  
  public String getSchema_name() {
    return schema_name;
  }
  public void setSchema_name(String schema_name) {
    this.schema_name = schema_name;
  }
  public String getDescriptioon() {
    return descriptioon;
  }
  public void setDescriptioon(String descriptioon) {
    this.descriptioon = descriptioon;
  }
  public String getUtype() {
    return utype;
  }
  public void setUtype(String utype) {
    this.utype = utype;
  }
  public List<Tables> getTables() {
    return tables;
  }
  public void setTables(List<Tables> tables) {
    this.tables = tables;
  }
  
}
