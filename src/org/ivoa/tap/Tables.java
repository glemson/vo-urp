/*
 * Table.java
 *
 * Author lemson
 * Created on Oct 29, 2008
 */
package org.ivoa.tap;

import org.ivoa.dm.model.MetadataObject;

import java.util.List;

import javax.persistence.*;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
  */
@Entity
@Table(name = "TAP_SCHEMA.tables")
public class Tables {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /** container gives the parent entity which owns a collection containing instances of this class */
  @ManyToOne(cascade =  {
    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
  )
  @JoinColumn(name = "schema_name", referencedColumnName = "schema_name", nullable = false)
  private Schemas schema;
  /**
   * TODO : Field Description
   */
  @Id
  @Column(name = "table_name", unique = true, nullable = false)
  private String table_name;
  /**
   * TODO : Field Description
   */
  @Column(name = "table_type", nullable = false)
  private String table_type;
  /**
   * TODO : Field Description
   */
  @Column(name = "description", nullable = true)
  private String description;
  /**
   * TODO : Field Description
   */
  @Column(name = "utype", nullable = true)
  private String utype;
  /**
   * TODO : Field Description
   */
  @OrderBy(value = "rank")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "table")
  private List<Columns> columns;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getSchema_name() {
    return schema.getSchema_name();
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Schemas getSchema() {
    return schema;
  }

  /**
   * TODO : Method Description
   *
   * @param schema 
   */
  public void setSchema(final Schemas schema) {
    this.schema = schema;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getTable_name() {
    return table_name;
  }

  /**
   * TODO : Method Description
   *
   * @param table_name 
   */
  public void setTable_name(final String table_name) {
    this.table_name = table_name;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getTable_type() {
    return table_type;
  }

  /**
   * TODO : Method Description
   *
   * @param table_type 
   */
  public void setTable_type(final String table_type) {
    this.table_type = table_type;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getDescription() {
    return description;
  }

  /**
   * TODO : Method Description
   *
   * @param description 
   */
  public void setDescription(final String description) {
    this.description = description;
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
  public List<Columns> getColumns() {
    return columns;
  }

  /**
   * TODO : Method Description
   *
   * @param columns 
   */
  public void setColumns(final List<Columns> columns) {
    this.columns = columns;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String toString() {
    return table_name + " [" + table_type + "]";
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
