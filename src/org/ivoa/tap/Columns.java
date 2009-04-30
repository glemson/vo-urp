/*
 * Column.java
 *
 * Author lemson
 * Created on Oct 29, 2008
 */
package org.ivoa.tap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


/**
 * TODO : Class Description
 *
 * @author $author$
  */
@Entity
@Table(name = "TAP_SCHEMA.columns")
public class Columns {
  //~ Members ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Field Description
   */
  @Id
  @Column(name = "id", nullable = false)
  private long                                          id;
  /**
   * TODO : Field Description
   */
  @Column(name = "column_name", nullable = false)
  private String column_name;
  /**
   * TODO : Field Description
   */
  @ManyToOne(cascade =  {
    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
  )
  @JoinColumn(name = "table_name", referencedColumnName = "table_name")
  private Tables table;
  /**
   * TODO : Field Description
   */
  @Column(name = "datatype", nullable = false)
  private String datatype;
  /**
   * TODO : Field Description
   */
  @Column(name = "rank", nullable = false)
  private int rank;

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getColumn_name() {
    return column_name;
  }

  /**
   * TODO : Method Description
   *
   * @param column_name 
   */
  public void setColumn_name(final String column_name) {
    this.column_name = column_name;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getData_type() {
    return datatype;
  }

  /**
   * TODO : Method Description
   *
   * @param datatype 
   */
  public void setDatatype(final String datatype) {
    this.datatype = datatype;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getRank() {
    return rank;
  }

  /**
   * TODO : Method Description
   *
   * @param rank 
   */
  public void setRank(final int rank) {
    this.rank = rank;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public Tables getTable() {
    return table;
  }

  /**
   * TODO : Method Description
   *
   * @param table 
   */
  public void setTable(final Tables table) {
    this.table = table;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
