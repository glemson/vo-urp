/*
 * KeyColumn.java
 * 
 * Author lemson
 * Created on Sep 16, 2009
 */
package org.ivoa.tap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class KeyColumns  {

  @Id
  @Column(name = "id", nullable = false)
  private long id;
  @ManyToOne(cascade =  {
      CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
    )
    @JoinColumn(name = "key_id", referencedColumnName = "key_id")
  private Keys keys;
  
  private String from_column;
  private String target_column;
}
