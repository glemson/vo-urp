package org.ivoa.tap;
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
 * TAP Schemas
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
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
   * @param pSchemaName schema_name 
   */
  public void setSchema_name(final String pSchemaName) {
    this.schema_name = pSchemaName;
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
   * @param pDescription description 
   */
  public void setDescriptioon(final String pDescription) {
    this.descriptioon = pDescription;
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
   * @param pUtype utype 
   */
  public void setUtype(final String pUtype) {
    this.utype = pUtype;
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
   * @param pTables tables 
   */
  public void setTables(final List<Tables> pTables) {
    this.tables = pTables;
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
