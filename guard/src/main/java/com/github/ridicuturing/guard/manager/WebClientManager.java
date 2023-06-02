package com.github.ridicuturing.guard.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

@Component
@Scope("prototype")
@EnableConfigurationProperties(WebClientManager.ProxyConfig.class)
public class WebClientManager {

    @Autowired
    ProxyConfig proxyConfig;

    public WebClient create() {
        WebClient.Builder builder = WebClient.builder();
        if(Boolean.getBoolean("http.proxySet")) {
            ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
                    HttpClient.create().responseTimeout(Duration.ofSeconds(30)).proxy(proxy ->
                            proxy.type(ProxyProvider.Proxy.HTTP)
                                    .address(new InetSocketAddress(
                                            System.getProperty("http.proxyHost")
                                            , Integer.parseInt(System.getProperty("http.proxyPort"))))));
            builder.clientConnector(httpConnector);
        } else if(Boolean.TRUE.equals(proxyConfig.enable)) {
            ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
                    HttpClient.create().responseTimeout(Duration.ofSeconds(30)).proxy(proxy ->
                            proxy.type(ProxyProvider.Proxy.HTTP)
                                    .address(new InetSocketAddress(
                                            proxyConfig.hostname
                                            , proxyConfig.port))));
            builder.clientConnector(httpConnector);
        }
        return builder.build();
    }


    @ConfigurationProperties(prefix = "http-proxy")
    public record ProxyConfig(Boolean enable, String hostname, Integer port) {}
}
