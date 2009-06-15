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
import javax.persistence.Table;


/**
 * TODO : Class Description
 *
 * @author laurent bourges (voparis) / Gerard Lemson (mpe)
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
  private long id;
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
   * Returns the id
   * @return id
   */
  public long getId() {
    return id;
  }
  
  
  
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
   * @param pColumnName column_name 
   */
  public void setColumn_name(final String pColumnName) {
    this.column_name = pColumnName;
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
   * @param pDatatype datatype 
   */
  public void setDatatype(final String pDatatype) {
    this.datatype = pDatatype;
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
   * @param pRank rank 
   */
  public void setRank(final int pRank) {
    this.rank = pRank;
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
   * @param pTable table 
   */
  public void setTable(final Tables pTable) {
    this.table = pTable;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
