/**
 * 
 */
package org.adf.test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.PilotException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

/**
 * @author Prasad
 *
 */
public class VTDExpiriments implements Runnable {

  String file = "C:\\Users\\prasad\\github\\SprintHacker\\test5.xml";

  @Override
  public void run() {
    try {
      DateTime dt = DateTime.now();
      // System.out.println(" Start" +"" + dt);
      VTDGen vg = new VTDGen();
      File f = new File(file);
//      byte[] readFileToByteArray = FileUtils.readFileToByteArray(f);
//      Path path = Paths.get(file);
//      byte[] readFileToByteArray = Files.readAllBytes(path);
//      byte[] readFileToByteArray = com.google.common.io.Files.toByteArray(f);
//      FileUtils.readFileToString(f);
//      FileInputStream fis = new FileInputStream(f);
//      byte[] readFileToByteArray = new byte[(int) f.length()];
//      fis.read(readFileToByteArray);
//      fis.close();
      byte[] readFileToByteArray = (FileUtils.readFileToString(f, Charset.defaultCharset())).getBytes();
      System.out.println("2 " + (DateTime.now().getMillis() - dt.getMillis()));
      dt = DateTime.now();
      vg.setDoc(readFileToByteArray);
      System.out.println("3 " + (DateTime.now().getMillis() - dt.getMillis()));
      dt = DateTime.now();
      vg.parse(false);
      System.out.println("4 " + (DateTime.now().getMillis() - dt.getMillis()));
      dt = DateTime.now();
      VTDNav vn = vg.getNav();
      System.out.println("5 " + (DateTime.now().getMillis() - dt.getMillis()));
      dt = DateTime.now();
      AutoPilot ap = new AutoPilot(vn);
      System.out.println("6 " + (DateTime.now().getMillis() - dt.getMillis()));
//      System.out.println("5 " + (DateTime.now().getMillis() - dt.getMillis()));
      String routingNumber = getRoutingNumber(vn, ap);
      vn.toElement(VTDNav.ROOT);
      int amt = getCashFlowVal(vn, ap);
//      vn = vg.getNav();
//      ap = new AutoPilot(vn);
//      vn = vn.duplicateNav();
//      System.out.println("7 " + (DateTime.now().getMillis() - dt.getMillis()));
//      dt = DateTime.now();
//      ap.bind(vn);
//      System.out.println("8 " + (DateTime.now().getMillis() - dt.getMillis()));
//      ap.resetXPath();
//      System.out.println(routingNumber);
//      System.out.println(amt);
      System.out.println("Total time in ms  " + (DateTime.now().getMillis() - dt.getMillis()));
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
//      fis.c
    }
  }

  private String getRoutingNumber(VTDNav vn, AutoPilot ap) {
    ap.selectElement("RoutingNumberEntered");
    try {
      while (ap.iterate()) {
        int t = vn.getText();
        if (t != -1) {
          String val = vn.toNormalizedString(t);
           System.out.println(" Routing Num ==> "+val);
//          System.out.println("9 " + (DateTime.now().getMillis() - dt.getMillis()));
          return val;
        }
      }
    } catch (PilotException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NavException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  protected int getCashFlowVal(VTDNav vn, AutoPilot ap) throws PilotException, NavException {
    ap.selectElement("Amount");
    double cashFlow = 0d;
    while (ap.iterate()) {
      int t = vn.getText();
      if (t != -1) {
        String val = vn.toNormalizedString(t);
        // System.out.println(" CAsh ==> " + val);
        cashFlow = cashFlow + NumberUtils.toDouble(val);
      }
    }
//    System.out.println("6 " + (DateTime.now().getMillis() - dt.getMillis()));
    int amt = (int) Math.ceil(cashFlow);
//    System.out.println("7 " + (DateTime.now().getMillis() - dt.getMillis()));
    return amt;
  }

  public static void main(String[] args) {
    for (int i = 0; i < 5; i++) {
      VTDExpiriments ex = new VTDExpiriments();
      new Thread(ex).start();
    }
    try {
      Thread.currentThread().sleep(2 * 1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
