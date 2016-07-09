/**
 * 
 */
package org.adf.hack.st;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowResult;
import org.adf.cashflow.helper.BankNameUpdater;
import org.adf.cashflow.helper.CashFlowUpdater;
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
  BankNameUpdater bankService;

  @Autowired
  CashFlowUpdater cashService;

  @Autowired
  DBHelper dbHelper;

  @RequestMapping("/prasad")
  @Transactional
  public List<CashFlowResult> getCashFlowDetails(
      @RequestParam(value = "key", defaultValue = "test") String key) {
//    List<CashFlow> cashflows = new LinkedList<CashFlow>();
//    System.out.println("IN == " + DateTime.now());
//    DateTime dt = DateTime.now();
    System.out.println("              dt" + Thread.currentThread().getName() + " --  " + DateTime.now().getMillisOfDay());
    String[] split = key.split(",");
    List<String> asList = Arrays.asList(split);
    List<CashFlow> cashflows = dbHelper.findAll(asList);
    List<CashFlowResult> results = new ArrayList<CashFlowResult>();
//    System.out.println("DB Fetch" + (DateTime.now().getMillis() - dt.getMillis()));
    CountDownLatch latch = new CountDownLatch(cashflows.size()*1);
    ExecutorService ex = Executors.newFixedThreadPool(cashflows.size()*1);

    for (CashFlow cashflow : cashflows) {
      CashFlowResult result = new CashFlowResult(cashflow.getId(),cashflow,latch);
      results.add(result);
//      Runnable task = new Runnable() {
//        @Override
//        public void run() {
//          try {
//            CashFlowHelper.process(cashflow, result,latch);
//          } catch (Exception e) {
//            e.printStackTrace();
//          } finally {
//            latch.countDown();
//          }
////          System.out.println("Task Done1 -- " + (DateTime.now().getMillis() - dt.getMillis()) + Thread.currentThread().getName());
//        }
//      };
      ex.submit(result);
//      ex.submit(new Runnable() {
//        @Override
//        public void run() {
//          DateTime dt = DateTime.now();
//          try {
//            CashFlowHelper.getRoutingNumber(cashflow, result,latch);
//          } catch (Exception e) {
//            e.printStackTrace();
//          } finally {
//            latch.countDown();
//          }
//          System.out.println("Task Done2 -- " + (DateTime.now().getMillis() - dt.getMillis()));
//        }
//      });
    }
//    ex.invokeAll(results);
    try {
      latch.await();
      // dbHelper.save(cashflows);
      // System.out.println("Task Completed" + " -- " + (DateTime.now().getMillis() -
      // dt.getMillis()));
      ex1.submit(new Runnable() {
        @Override
        public void run() {
          dbHelper.save(cashflows);
//           System.out.println("DB Save" + (DateTime.now().getMillis() - dt.getMillis()));
        }
      });
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//    System.out.println("Total S+ervice Time" + (DateTime.now().getMillis() - dt.getMillis()));
//    System.out.println("OUT == " + DateTime.now());
    System.out.println("              en" + Thread.currentThread().getName() + " --  " + DateTime.now().getMillisOfDay());
    return results;
  }
}
