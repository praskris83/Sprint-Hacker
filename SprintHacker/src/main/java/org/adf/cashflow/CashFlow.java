/**
 * 
 */
package org.adf.cashflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Prasad
 *
 */
@Entity
@Table(name = "cash_flow")
public class CashFlow {

  private String id;
  
  private String file;
  
  private int cashFlow;
  
  private String bankName;

  @Id
  @Column(name = "`key`", nullable=false)
  public String getId() {
    return id;
  }

  public void setId(String key) {
    this.id = key;
  }

  @Column(name = "dl_file_name")
  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  @Column(name = "cash_flow")
  public int getCashFlow() {
    return cashFlow;
  }

  public void setCashFlow(int cashFlow) {
    this.cashFlow = cashFlow;
  }

  @Column(name = "bank_name")
  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }
  
  
}
