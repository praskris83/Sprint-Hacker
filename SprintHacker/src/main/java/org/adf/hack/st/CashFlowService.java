/**
 * 
 */
package org.adf.hack.st;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowResult;
import org.adf.cashflow.helper.DBHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Prasad
 *
 */
@RestController
public class CashFlowService {
  
  ExecutorService ex1 = Executors.newFixedThreadPool(10);

  @Autowired
  DBHelper dbHelper;

  @RequestMapping("/prasad")
  @Transactional
  public List<CashFlowResult> getCashFlowDetails(
      @RequestParam(value = "key", defaultValue = "test") String key) {
//    DateTime dt = DateTime.now();
//    System.out.println("                dt " + Thread.currentThread().getName() + " --  " + dt.getMillisOfDay());
    String[] split = key.split(",");
    List<String> asList = Arrays.asList(split);
//    System.out.println("DB Fetch" + (DateTime.now().getMillis() - dt.getMillis()));
    List<CashFlow> cashflows = dbHelper.findAll(asList);
//    List<CashFlow> cashflows = dbHelper.findAllNative(split);
    List<CashFlowResult> results = new ArrayList<CashFlowResult>();
//    System.out.println("DB Fetch" + (DateTime.now().getMillis() - dt.getMillis()));
    CountDownLatch latch = new CountDownLatch(cashflows.size()*1);
    ExecutorService ex = Executors.newFixedThreadPool(cashflows.size()*1);

    for (CashFlow cashflow : cashflows) {
      CashFlowResult result = new CashFlowResult(cashflow.getId(),cashflow,latch);
      results.add(result);
      ex.submit(result);
    }
    try {
      latch.await();
      // dbHelper.save(cashflows);
      // System.out.println("Task Completed" + " -- " + (DateTime.now().getMillis() -
      // dt.getMillis()));
      ex1.submit(new Runnable() {
        @Override
        public void run() {
          dbHelper.save(cashflows);
          ex.shutdown();
//           System.out.println("DB Save" + (DateTime.now().getMillis() - dt.getMillis()));
        }
      });
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//    System.out.println("Total S+ervice Time" + (DateTime.now().getMillis() - dt.getMillis()));
//    System.out.println("OUT == " + DateTime.now());
//    System.out.println("                en" + Thread.currentThread().getName() + " --  " + DateTime.now().getMillisOfDay());
//    System.out.println("Total S+ervice Time" + (DateTime.now().getMillis() - dt.getMillis()));
    return results;
  }
}
