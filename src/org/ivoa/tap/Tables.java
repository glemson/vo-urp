/*
 * Table.java
 * 
 * Author lemson
 * Created on Oct 29, 2008
 */
package org.ivoa.tap;


import java.util.List;

import javax.persistence.*;

import org.ivoa.dm.model.MetadataObject;
@Entity
@Table(name = "TAP_SCHEMA.tables")
public class Tables  {

  
  /** container gives the parent entity which owns a collection containing instances of this class */
  @ManyToOne(cascade =  {
    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
  )
  @JoinColumn(name = "schema_name", referencedColumnName = "schema_name", nullable = false)
  private Schemas schema;
  
  @Id
  @Column(name = "table_name", unique = true, nullable = false)
  private String table_name;

  @Column(name = "table_type", nullable = false)
  private String table_type;

  @Column(name = "description", nullable = true)
  private String description;

  @Column(name = "utype", nullable = true)
  private String utype;

  @OrderBy(value = "rank")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "table")
  private List<Columns> columns;

  public String getSchema_name() {
    return schema.getSchema_name();
  }

  public Schemas getSchema() {
    return schema;
  }

  public void setSchema(Schemas schema) {
    this.schema = schema;
  }

  public String getTable_name() {
    return table_name;
  }

  public void setTable_name(String table_name) {
    this.table_name = table_name;
  }

  public String getTable_type() {
    return table_type;
  }

  public void setTable_type(String table_type) {
    this.table_type = table_type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUtype() {
    return utype;
  }

  public void setUtype(String utype) {
    this.utype = utype;
  }

  public List<Columns> getColumns() {
    return columns;
  }

  public void setColumns(List<Columns> columns) {
    this.columns = columns;
  }
  
  public String toString()
  {
    return table_name+" ["+table_type+"]";
  }
}
