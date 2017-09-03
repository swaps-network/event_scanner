package io.lastwill.eventscan;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .addCommandLineProperties(true)
                .web(false)
                .sources(Application.class)
                .main(Application.class)
                .run(args);
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient closeableHttpClient() {
        return HttpClientBuilder.create()
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(10)
                .setConnectionManagerShared(true)
                .build();
    }

    @Bean
    public Web3j web3j(CloseableHttpClient httpClient, @Value("${io.lastwill.ecentscan.web3-url}") String web3Url) {
        return Web3j.build(new HttpService(
                web3Url,
                httpClient
        ));
    }
}
