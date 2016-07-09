package org.adf.cashflow.helper;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.PilotException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

@Service
public class CashFlowUpdater{

//  private List<CashFlow> cashFlows;
//  private List<CashFlowResult> cashFlowResults;
//  private CountDownLatch latch;
//  
//  public CashFlowUpdater(List<CashFlow> cashFlows, List<CashFlowResult> cashFlowResults,CountDownLatch latch) {
//    super();
//    this.cashFlows = cashFlows;
//    this.cashFlowResults = cashFlowResults;
//    this.latch = latch;
//  }

  public void updateCashFlow(CashFlow cfEntity, CashFlowResult result, CountDownLatch latch) {
//    DateTime dt = DateTime.now();
    try {
      String file = "D:\\ADF\\workspace\\derewrite\\SprintHacker\\" + cfEntity.getId() + ".xml";
//      String file = cfEntity.getFile();
      VTDGen vg = new VTDGen();
      File f = new File(file);
      vg.setDoc(FileUtils.readFileToByteArray(f));
      vg.parse(false);
      VTDNav vn = vg.getNav();
      AutoPilot ap = new AutoPilot(vn);
      int amt = getCashFlowVal(vn, ap);
      result.setCashFlow(amt);
      cfEntity.setCashFlow(amt);
    } catch (Exception e) {
      // TODO: handle exception
    }
//    latch.countDown();
//    System.out.println("Ending CashFlow ********* " + DateTime.now() +" -- "+(DateTime.now().getMillis() - dt.getMillis()));
  }

  protected int getCashFlowVal(VTDNav vn, AutoPilot ap) throws PilotException, NavException {
    ap.selectElement("Amount");
    double cashFlow = 0d;
    while (ap.iterate()) {
      int t = vn.getText();
      if (t != -1) {
        String val = vn.toNormalizedString(t);
//        System.out.println(" CAsh ==> " + val);
        cashFlow = cashFlow + NumberUtils.toDouble(val);
      }
    }
    int amt = (int) Math.ceil(cashFlow);
    return amt;
  }

//  @Override
//  public void run() {
//    DateTime dt = DateTime.now();
//    for(CashFlowResult result : cashFlowResults){
////      updateCashFlow(cfEntity, result, latch);
//    }
//    System.out.println("Ending CashFlow ********* " + DateTime.now() +" -- "+(DateTime.now().getMillis() - dt.getMillis()));
//    latch.countDown();
//  }

}
