/**
 * 
 */
package org.adf.cashflow.helper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowResult;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.PilotException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.exceptions.AlchemyHttpException;

/**
 * @author Prasad
 *
 */
@Service
public class BankNameUpdater {

  static ObjectMapper mapper;

  private static final String BANK_SERVICE = "https://dev-ui1.adfdata.net/hacker/bank/";

  private static final String BANK_NAME_KEY = "bankName";

  static AlchemyHttp http;

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
    connManager.setMaxTotal(40);
    CloseableHttpClient client = HttpClients.custom().setKeepAliveStrategy(myStrategy)
        .setConnectionManager(connManager).build();
    http = AlchemyHttp.newInstanceWithApacheHttpClient(client);
  }

  public void updateBankName(CashFlow cfEntity, CashFlowResult result, CountDownLatch latch) {
     DateTime dt = DateTime.now();
    try {
       String file = "D:\\ADF\\workspace\\derewrite\\SprintHacker\\" + cfEntity.getId() + ".xml";
//      String file = cfEntity.getFile();
      VTDGen vg = new VTDGen();
      File f = new File(file);
      vg.setDoc(FileUtils.readFileToByteArray(f));
      vg.parse(false);
      VTDNav vn = vg.getNav();
      AutoPilot ap = new AutoPilot(vn);

      String routingNum = getRoutingNumber(vn, ap, result);
      String bankName = "test"; 
//          getBankName(routingNum);
      result.setBankName(bankName);
      cfEntity.setBankName(bankName);
    } catch (Exception e) {
      // TODO: handle exception
    }
    latch.countDown();
     System.out.println("Ending Bank ********* " + (DateTime.now().getMillis() - dt.getMillis()));
  }

  protected String getRoutingNumber(VTDNav vn, AutoPilot ap, CashFlowResult result) {
    // DateTime dt = DateTime.now();
    ap.selectElement("RoutingNumberEntered");
    try {
      while (ap.iterate()) {
        int t = vn.getText();
        if (t != -1) {
          String val = vn.toNormalizedString(t);
          // System.out.println(" Routing Num ==> "+val);
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

  public static String getBankName(String routingNum) throws AlchemyHttpException,
      IllegalArgumentException, JsonParseException, JsonMappingException, IOException {
     DateTime dt = DateTime.now();
    String resp = http.go().get().expecting(String.class).at(BANK_SERVICE + routingNum);
    Map<String, String> bankData = mapper.readValue(resp, Map.class);
    String bankName = bankData.get(BANK_NAME_KEY);
    // String resp = http.go().get().at(BANK_SERVICE +
    // routingNum).body().getAsJsonObject().get(BANK_NAME_KEY).getAsString();
    // String bankName = resp;
//     System.out.println("Bank Service == " + bankName +" -- "+ (DateTime.now().getMillis() -
//     dt.getMillis()));
    return bankName;
  }
}
