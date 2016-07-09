/**
 * 
 */
package org.adf.cashflow;

import java.util.concurrent.CountDownLatch;

import org.adf.hack.st.CashFlowHelper;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ximpleware.VTDGen;

/**
 * @author Prasad
 *
 */
public class CashFlowResult implements Runnable {

  String key;

  int cashFlow;

  String bankName;
  
  @JsonIgnore
  String routing;

  @JsonIgnore
  CashFlow entity;

  @JsonIgnore
  CountDownLatch latch;


  public CashFlowResult(String key, CashFlow entity, CountDownLatch latch) {
    super();
    this.key = key;
    this.entity = entity;
    this.latch = latch;
  }

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
  
  public String getRouting() {
    return routing;
  }

  public void setRouting(String routing) {
    this.routing = routing;
  }

  @Override
  public void run() {
    try {
      CountDownLatch fileRead = new CountDownLatch(1);
      VTDGen vg = new VTDGen();
      CashFlowHelper.initParser(entity, this,fileRead,vg);
      fileRead.await();
      CountDownLatch latch = new CountDownLatch(2);
      CashFlowHelper.setBankNameAsync(this,latch);
      CashFlowHelper.parseXml(entity, this, vg,latch);
      latch.await();
//      CountDownLatch service = new CountDownLatch(1);
//      service.await();
      System.out.println("              ts" + Thread.currentThread().getName() + " --  " + DateTime.now().getMillisOfDay());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      latch.countDown();
    }
  }

}
