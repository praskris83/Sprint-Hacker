/**
 * 
 */
package org.adf.hack.st;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.protocol.HttpContext;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.PilotException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

/**
 * @author Prasad
 *
 */
public class CashFlowHelper {

  static ObjectMapper mapper = new ObjectMapper();

  private static final String BANK_SERVICE = "https://dev-ui1.adfdata.net/hacker/bank/";

  private static final String BANK_NAME_KEY = "bankName";
  
  private static final String MOCK_FILE = "C:\\Users\\prasad\\github\\SprintHacker\\dl10.xml";

  // static AlchemyHttp http;

  static CloseableHttpAsyncClient client;

  static HttpClient sync;

  static ExecutorService ex = Executors.newFixedThreadPool(20);

  static ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      return 50 * 1000;
    }
  };

  static {
//    PoolingHttpClientConnectionManager connManager =
//        new PoolingHttpClientConnectionManager(10, TimeUnit.MINUTES);
//    connManager.setDefaultMaxPerRoute(30);
//    connManager.setMaxTotal(60);
    // connManager.getD
    ConnectionReuseStrategy reuseStrategy = new ConnectionReuseStrategy() {

      @Override
      public boolean keepAlive(HttpResponse response, HttpContext context) {
//        System.out.println("Keep It LIve *******************************");
        return true;
      }
    };
    client = HttpAsyncClients.custom().setKeepAliveStrategy(myStrategy).setMaxConnPerRoute(5)
        .setMaxConnTotal(10).setConnectionReuseStrategy(reuseStrategy)
        .build();
    client.start();
//    sync = HttpClientBuilder.create().setConnectionManager(connManager)
//        .setConnectionReuseStrategy(reuseStrategy).setConnectionTimeToLive(10, TimeUnit.MINUTES)
//        .setKeepAliveStrategy(myStrategy).build();
    // .set
    // .setConnectionManager(connManager).build();
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

  // public static void process(CashFlow cfEntity, CashFlowResult result,VTDGen vg) throws Exception
  // {
  // CountDownLatch latch = new CountDownLatch(1);
  // // result.setKey(cfEntity.getId());
  // Runnable task = new Runnable() {
  // @Override
  // public void run() {
  // parseXml(cfEntity, result,vg);
  // latch.countDown();
  // }
  // };
  // Thread t = new Thread(task);
  // t.setPriority(Thread.MAX_PRIORITY);
  //// t.start();
  // ex.submit(task);
  // latch.await();
  // setBankNameAsync(result);
  // // setBankName(result, routingNumber);
  // }

  public static void parseXml(CashFlow cfEntity, CashFlowResult result, VTDNav vn,
      CountDownLatch latch) {
    // String file = "D:\\ADF\\workspace\\derewrite\\SprintHacker\\" + "test5.xml";
    Runnable task = new Runnable() {
      @Override
      public void run() {
        try {
//          DateTime dt = DateTime.now();
          setXMLDetails(result, vn, cfEntity);
//          System.out.println("XML  Pars Time for " + Thread.currentThread().getName() + " -- " +
//          (DateTime.now().getMillis() - dt.getMillis()));
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      }
    };
    ex.submit(task);
  }

  protected static void setXMLDetails(CashFlowResult result, VTDNav vn, CashFlow cfEntity)
      throws PilotException, NavException, XPathParseException, XPathEvalException {
//    VTDNav vn = vn.getNav();
    vn.toElement(VTDNav.ROOT);
    AutoPilot ap = new AutoPilot(vn);
    int cashFlowVal = getCashFlowVal(vn, ap);
    result.setCashFlow(cashFlowVal);
    cfEntity.setCashFlow(cashFlowVal);
  }

  public static VTDNav read(CashFlow cfEntity) throws FileNotFoundException,
      IOException, EncodingException, EOFException, EntityException, ParseException {
//    DateTime dt = DateTime.now();
    String file = cfEntity.getFile();
//    file = MOCK_FILE;
    File f = new File(file);
    FileInputStream fis = new FileInputStream(f);
    byte[] readFileToByteArray = new byte[(int) f.length()];
    fis.read(readFileToByteArray);
    fis.close();
    // vg.setDoc(FileUtils.readFileToByteArray(f));
    VTDGen vg = new VTDGen();
    vg.setDoc(readFileToByteArray);
    vg.parse(false);
    VTDNav vn = vg.getNav();
//    System.out.println("File Read Time for " + Thread.currentThread().getName() + " -- " +
//        (DateTime.now().getMillis() - dt.getMillis()));
    return vn;
  }

  public static int getCashFlowVal(VTDNav vn, AutoPilot ap) throws PilotException, NavException, XPathParseException, XPathEvalException {
    ap.selectXPath("//TransactionSummary4/Amount");
    double cashFlow = 0d;
    int i=-1;
    while((i=ap.evalXPath())!=-1){
      long l = vn.getContentFragment();
      String cash = vn.toString((int )l, (int)(l>>32));
      cashFlow = cashFlow + NumberUtils.toDouble(cash);
//      System.out.println(" -==> "+ cashFlow);
    }
    // System.out.println("Cash Flow Actual " + cashFlow);
    int amt = (int) Math.ceil(cashFlow);
    return amt;
  }

  public static String getRoutingNumber(VTDNav vn) {
    // DateTime dt = DateTime.now();
    try {
      AutoPilot ap = new AutoPilot(vn);
      ap .selectElement("RoutingNumberEntered");
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

  public static void setBankNameAsync(CashFlowResult result, CountDownLatch latch, CashFlow entity) throws Exception {
//    DateTime dt = DateTime.now();
    HttpGet getRequest = new HttpGet(BANK_SERVICE + result.getRouting());
    client.execute(getRequest, new FutureCallback<HttpResponse>() {
      public void completed(final HttpResponse execute) {
        Map<String, String> bankData;
        try {
          bankData = mapper.readValue(execute.getEntity().getContent(), Map.class);
          String bankName = bankData.get(BANK_NAME_KEY);
          result.setBankName(bankName);
          entity.setBankName(bankName);
//          System.out.println("Bank Srvc Time for " + Thread.currentThread().getName() + " -- " +
//          (DateTime.now().getMillis() - dt.getMillis()));
          // execute.close();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      }

      public void failed(final Exception ex) {
        latch.countDown();
      }

      public void cancelled() {
        latch.countDown();
      }
    });
  }

  public static void setBankName(CashFlowResult result, String routingNum) throws Exception {
    DateTime dt = DateTime.now();
    HttpGet getRequest = new HttpGet(BANK_SERVICE + routingNum);
    HttpEntity resp = sync.execute(getRequest).getEntity();
    try {
      Map<String, String> bankData = mapper.readValue(resp.getContent(), Map.class);
      result.setBankName(bankData.get(BANK_NAME_KEY));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
    }
    System.out.println("Bank Service == " + Thread.currentThread().getName() + " -- "
        + (DateTime.now().getMillis() - dt.getMillis()) + DateTime.now().getMillisOfDay());
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
//      AutoPilot ap = new AutoPilot(vn);
      String routingNumber = getRoutingNumber(vn);
      // result.setBankName(getBankName(routingNumber));
      // System.out.println("XML Parsing 2 ==" + (DateTime.now().getMillis() - dt.getMillis()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void initParser(CashFlow cfEntity, CashFlowResult cashFlowResult,
      CountDownLatch latch, VTDNav vn) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        try {
          DateTime dt = DateTime.now();
          read(cfEntity);
          cashFlowResult.setRouting(getRoutingNumber(vn));
          System.out.println("File Read Time for " + Thread.currentThread().getName() + " -- " +
          (DateTime.now().getMillis() - dt.getMillis()));
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      }
    };
    ex.submit(task);
  }
}
