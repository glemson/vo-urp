/*
 * Keys.java
 * 
 * Author Gerard Lemson 
 * Created on Sep 16, 2009
 */
package org.ivoa.tap;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * TAP Keys
 *
 * @author Gerard Lemson (mpe) 
 */
@Entity
@Table(name = "TAP_SCHEMA.keys")
public class Keys {
  @Id
  @Column(name = "key_id", unique = true, nullable = false)
  private String key_id;

  /** container gives the parent entity which owns a collection containing instances of this class */
  @ManyToOne(cascade =  {
    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
  )
  @JoinColumn(name = "from_table", referencedColumnName = "table_name", nullable = false)
  Tables from_table;
  @ManyToOne(cascade =  {
      CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
    )
    @JoinColumn(name = "target_table", referencedColumnName = "target_table", nullable = false)
  Tables target_table;
  /**
   * TODO : Field Description
   */
  @Column(name = "utype", nullable = true)
  String utype;
  /**
   * TODO : Field Description
   */
  @Column(name = "description", nullable = true)
  String description;
  
  /**
   * TODO : Field Description
   */
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "key_id")
  private List<KeyColumns> columns;

}
