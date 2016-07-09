/**
 * 
 */
package org.adf.hack.st;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.PilotException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

/**
 * @author Prasad
 *
 */
public class CashFlowHelper {

  static ObjectMapper mapper = new ObjectMapper();

  private static final String BANK_SERVICE = "https://dev-ui1.adfdata.net/hacker/bank/";

  private static final String BANK_NAME_KEY = "bankName";

  // static AlchemyHttp http;

  static CloseableHttpClient client;

  static ExecutorService ex = Executors.newWorkStealingPool(10);

  static ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      return 50 * 1000;
    }
  };

  static {
    PoolingHttpClientConnectionManager connManager =
        new PoolingHttpClientConnectionManager(10, TimeUnit.MINUTES);
    connManager.setDefaultMaxPerRoute(30);
    connManager.setMaxTotal(60);
    // connManager.getD
    client = HttpClients.custom()
        // .setKeepAliveStrategy(myStrategy)
        .setConnectionManager(connManager).build();
    // http = AlchemyHttp.newInstanceWithApacheHttpClient(client);
  }

  // private CashFlow cashFlow;
  // private CashFlowResult cashFlowResult;
  //
  //
  // public CashFlowHelper(CashFlow cashFlow, CashFlowResult cashFlowResult) {
  // super();
  // this.cashFlow = cashFlow;
  // this.cashFlowResult = cashFlowResult;
  // }

  public static void process(CashFlow cfEntity, CashFlowResult result, CountDownLatch latch)
      throws Exception {
    // DateTime dt = DateTime.now();
    result.setKey(cfEntity.getId());
    // String file = "D:\\ADF\\workspace\\derewrite\\SprintHacker\\" + "test5.xml";
    String file = cfEntity.getFile();
    VTDGen vg = new VTDGen();
    File f = new File(file);
    FileInputStream fis = new FileInputStream(f);
    byte[] readFileToByteArray = new byte[(int) f.length()];
    fis.read(readFileToByteArray);
    fis.close();
    // vg.setDoc(FileUtils.readFileToByteArray(f));
    vg.setDoc(readFileToByteArray);
    vg.parse(false);
    VTDNav vn = vg.getNav();
    AutoPilot ap = new AutoPilot(vn);
    result.setCashFlow(getCashFlowVal(vn, ap));
    result.setBankName(getBankName(getRoutingNumber(vn, ap)));
    // System.out.println("XML Parsing 1 ==" + (DateTime.now().getMillis() - dt.getMillis()));
    // ex.submit(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // result.setBankName(getBankName(routingNumber));
    // latch.countDown();
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // });
    // System.out.println("CashFlow Int" + result.getCashFlow());
  }

  public static int getCashFlowVal(VTDNav vn, AutoPilot ap) throws PilotException, NavException {
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
    // System.out.println("Cash Flow Actual " + cashFlow);
    int amt = (int) Math.ceil(cashFlow);
    return amt;
  }

  public static String getRoutingNumber(VTDNav vn, AutoPilot ap) {
    // DateTime dt = DateTime.now();
    vn = vn.duplicateNav();
    ap.bind(vn);
    ap.selectElement("RoutingNumberEntered");
    try {
      while (ap.iterate()) {
        int t = vn.getText();
        if (t != -1) {
          String val = vn.toNormalizedString(t);
          // System.out.println(" Routing Num ==> " + val);
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
    // System.out.println("Routing Service == " + (DateTime.now().getMillis() - dt.getMillis()));
    return null;
  }

  public static String getBankName(String routingNum) throws Exception {
    DateTime dt = DateTime.now();
    // String resp = simulate();
    // String resp = http.go().get().expecting(String.class).at(BANK_SERVICE + routingNum);
    HttpGet getRequest = new HttpGet(BANK_SERVICE + routingNum);
    // StringWriter writer = new StringWriter();
    // String resp =
    // IOUtils.toString(client.execute(getRequest).getEntity().getContent(),Charset.defaultCharset());
    Map<String, String> bankData = mapper.readValue(client.execute(getRequest).getEntity().getContent(), Map.class);
    // Map<String, String> bankData = mapper.readValue(resp, Map.class);
    String bankName = bankData.get(BANK_NAME_KEY);
    // String resp = http.go().get().at(BANK_SERVICE +
    // routingNum).body().getAsJsonObject().get(BANK_NAME_KEY).getAsString();
    // String bankName = resp;
    System.out.println(
        "Bank Service == " + bankName + " -- " + (DateTime.now().getMillis() - dt.getMillis()));
    return bankName;
  }

  private static String simulate() throws InterruptedException {
    Thread.sleep(400);
    return "{\r\n" + "    \"routingNumber\": \"061000052\",\r\n"
        + "    \"bankName\": \"Mariamman Indian Bank\"\r\n" + "}\r\n" + "";
  }

  public static void getRoutingNumber(CashFlow cfEntity, CashFlowResult result,
      CountDownLatch latch) {
    try {
      DateTime dt = DateTime.now();
      result.setKey(cfEntity.getId());
      // String file = "D:\\ADF\\workspace\\derewrite\\SprintHacker\\" + "test5.xml";
      String file = cfEntity.getFile();
      VTDGen vg = new VTDGen();
      File f = new File(file);
      vg.setDoc(FileUtils.readFileToByteArray(f));
      vg.parse(false);
      VTDNav vn = vg.getNav();
      AutoPilot ap = new AutoPilot(vn);
      String routingNumber = getRoutingNumber(vn, ap);
      result.setBankName(getBankName(routingNumber));
      System.out.println("XML Parsing 2 ==" + (DateTime.now().getMillis() - dt.getMillis()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
