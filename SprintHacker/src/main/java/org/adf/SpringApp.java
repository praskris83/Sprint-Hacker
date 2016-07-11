/**
 * 
 */
package org.adf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.filter.RequestContextFilter;

/**
 * @author Prasad
 *
 */
@SpringBootApplication
@EnableAsync
public class SpringApp {

  public static void main(String[] args) {
    SpringApplication.run(SpringApp.class, args);
  }
  
//  @Bean
//  public FilterRegistrationBean filterRegistrationBean() {
//      FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//      CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
//      characterEncodingFilter.setEncoding("UTF-8");
//      registrationBean.setFilter(characterEncodingFilter);
//      registrationBean.setEnabled(false);
//      return registrationBean;
//  }
  
  @Bean
  public FilterRegistrationBean registration(CharacterEncodingFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean(filter);
      registration.setEnabled(false);
      return registration;
  }
 
  @Bean
  public FilterRegistrationBean registration1(HiddenHttpMethodFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean(filter);
      registration.setEnabled(false);
      return registration;
  }
  @Bean
  public FilterRegistrationBean registration2(RequestContextFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean(filter);
      registration.setEnabled(false);
      return registration;
  }
  @Bean
  public FilterRegistrationBean registration3(HttpPutFormContentFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean(filter);
      registration.setEnabled(false);
      return registration;
  }
  
//  @Bean
//  public CommonsRequestLoggingFilter requestLoggingFilter() {
//      CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
//      crlf.setIncludeClientInfo(true);
//      crlf.setIncludeQueryString(true);
//      crlf.setIncludePayload(true);
//      return crlf;
//  }
  
//  @Bean
//  public EmbeddedServletContainerFactory servletContainer() {
//      TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//      tomcat.addAdditionalTomcatConnectors(createStandardConnector());
//      return tomcat;
//  }
//
//  private Connector createStandardConnector() {
//      Connector connector = new Connector("org.apache.coyote.http11.Http11Nio2Protocol");
//      connector.setPort(9666);
//      return connector;
//  }

}
