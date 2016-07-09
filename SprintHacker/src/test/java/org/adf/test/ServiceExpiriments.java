package org.adf.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import tech.sirwellington.alchemy.http.AlchemyHttp;
import tech.sirwellington.alchemy.http.AlchemyRequest.Step1;

public class ServiceExpiriments implements Runnable {

  private static final int count = 5;

  private static final String BANK_SERVICE = "https://dev-ui1.adfdata.net/hacker/bank/";

  private static final String BANK_NAME_KEY = "bankName";

  private static final String routingNum = "123456";

  static CountDownLatch latch = new CountDownLatch(count);

  static AlchemyHttp http = AlchemyHttp.newDefaultInstance();
  
  static ExecutorService ex = Executors.newFixedThreadPool(30);

  static ObjectMapper mapper = new ObjectMapper();

  static Map EMPTY = new HashMap();
  
  static ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      HeaderElementIterator it =
          new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
//      while (it.hasNext()) {
//        HeaderElement he = it.nextElement();
//        String param = he.getName();
//        String value = he.getValue();
//        if (value != null && param.equalsIgnoreCase("timeout")) {
//          return Long.parseLong(value) * 1000;
//        }
//      }
      return 5 * 1000;
    }
  };

  public static void main(String[] args) {
    DateTime dt = DateTime.now();
    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    connManager.setDefaultMaxPerRoute(30);
    connManager.setMaxTotal(40);
    CloseableHttpClient client = HttpClients.custom().setKeepAliveStrategy(myStrategy)
        .setConnectionManager(connManager).build();
    EMPTY.put("empoty", "empoty");
    http = AlchemyHttp.newInstance(client, ex, EMPTY);
    for (int i = 0; i < count; i++) {
      Thread t = new Thread(new ServiceExpiriments());
      t.start();
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
     System.out.println("Final TIme == " + (DateTime.now().getMillis() - dt.getMillis()));
  }

  public void testAlchemy() {
    String resp;
    try {
      Step1 httpClient = http.go();
      resp = httpClient.get().expecting(String.class).at(BANK_SERVICE + routingNum);
      Map<String, String> bankData = mapper.readValue(resp, Map.class);
      String bankName = bankData.get(BANK_NAME_KEY);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testApache() {
    try {
      HttpGet get = new HttpGet(BANK_SERVICE + routingNum);
      PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
      connManager.setDefaultMaxPerRoute(30);
      connManager.setMaxTotal(40);
      CloseableHttpClient client = HttpClients.custom().setKeepAliveStrategy(myStrategy)
          .setConnectionManager(connManager).build();
      CountDownLatch lt = new CountDownLatch(5);
      for (int i = 0; i < 5; i++) {
        new MultiHttpClientConnThread(client, get, lt).start();
      }
      lt.await();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    DateTime dt = DateTime.now();
    System.out.println("Starting ");
     testAlchemy();
//    testApache();
    latch.countDown();
    System.out.println("TIme == " + (DateTime.now().getMillis() - dt.getMillis()));
  }

  public class MultiHttpClientConnThread extends Thread {
    private CloseableHttpClient client;
    private HttpGet get;
    private CountDownLatch latch;

    public MultiHttpClientConnThread(CloseableHttpClient client, HttpGet get,
        CountDownLatch latch) {
      this.client = client;
      this.get = get;
      this.latch = latch;
    }

    public void run() {
      try {
        DateTime dt = DateTime.now();
        HttpResponse response = client.execute(get);
        InputStream is = response.getEntity().getContent();
        Map<String, String> bankData = mapper.readValue(is, Map.class);
        System.out.println(bankData.get(BANK_NAME_KEY));
        latch.countDown();
        System.out.println("TIme == " + (DateTime.now().getMillis() - dt.getMillis()));
      } catch (ClientProtocolException ex) {
      } catch (IOException ex) {
      }
    }
  }
}
