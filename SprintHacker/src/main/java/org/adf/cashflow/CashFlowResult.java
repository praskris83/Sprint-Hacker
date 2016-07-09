/**
 * 
 */
package org.adf.cashflow;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Prasad
 *
 */
public class CashFlowResult {

  String key;
  
  int cashFlow;
  
  String bankName;
  
  @JsonIgnore
  CashFlow entity;

  public CashFlowResult(String key) {
    super();
    this.key = key;
  }

  public CashFlowResult() {
    super();
    // TODO Auto-generated constructor stub
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getCashFlow() {
    return cashFlow;
  }

  public void setCashFlow(int cashFlow) {
    this.cashFlow = cashFlow;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public CashFlow getEntity() {
    return entity;
  }

  public void setEntity(CashFlow entity) {
    this.entity = entity;
  }
    
  
}
