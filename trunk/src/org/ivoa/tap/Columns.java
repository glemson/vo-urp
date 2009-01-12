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

@Entity
@Table(name = "TAP_SCHEMA.columns")
public class Columns {

  @Id
  @Column(name="id", nullable=false)
  private long id;
  
  @Column(name="column_name", nullable=false)
  private String column_name;
  

  @ManyToOne(cascade =  {
      CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
  )
  @JoinColumn(name = "table_name", referencedColumnName = "table_name")
  private Tables table;
  
  @Column(name="datatype", nullable=false)
  private String datatype;

  @Column(name="rank",nullable=false)
  private int rank;

  public String getColumn_name() {
    return column_name;
  }

  public void setColumn_name(String column_name) {
    this.column_name = column_name;
  }

  public String getData_type() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public Tables getTable() {
    return table;
  }

  public void setTable(Tables table) {
    this.table = table;
  }
  
}
