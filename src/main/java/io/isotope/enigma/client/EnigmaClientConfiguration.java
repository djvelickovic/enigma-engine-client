package io.isotope.enigma.client;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties({EnigmaClientProperties.class})
public class EnigmaClientConfiguration {

    @Bean
    public KeyManagerGateway keyManagerGateway(WebClient keyManagementWebClient) {
        return new KeyManagerGateway(keyManagementWebClient);
    }

    @Bean("keyManagementWebClient")
    public WebClient keyManagementWebClient(EnigmaClientProperties properties) throws Exception {

        final String keyStorePass = "2secure4u";
        final String keyAlias = "keymanagementclient";

        final KeyStore trustStore;
        final KeyStore keyStore;
        trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(EnigmaClientConfiguration.class.getClassLoader().getResourceAsStream("keymanagementclient.p12"), keyStorePass.toCharArray());
        keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(EnigmaClientConfiguration.class.getClassLoader().getResourceAsStream("keymanagementclient.p12"), keyStorePass.toCharArray());

        final X509Certificate[] certificates = Collections.list(trustStore.aliases())
                .stream()
                .filter(t -> {
                    try {
                        return trustStore.isCertificateEntry(t);
                    } catch (KeyStoreException e1) {
                        throw new RuntimeException("Error reading truststore", e1);
                    }
                })
                .map(t -> {
                    try {
                        return trustStore.getCertificate(t);
                    } catch (KeyStoreException e2) {
                        throw new RuntimeException("Error reading truststore", e2);
                    }
                }).toArray(X509Certificate[]::new);

        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyStorePass.toCharArray());

        Certificate[] certChain = keyStore.getCertificateChain(keyAlias);
        X509Certificate[] x509CertificateChain = Arrays.stream(certChain)
                .map(certificate -> (X509Certificate) certificate)
                .collect(Collectors.toList())
                .toArray(new X509Certificate[certChain.length]);

        SslContext sslContext = SslContextBuilder.forClient()
                .keyManager(privateKey, keyStorePass, x509CertificateChain)
                .trustManager(certificates)
                .build();

        HttpClient httpConnector = HttpClient.create().secure(t -> t.sslContext(sslContext));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpConnector))
//                .baseUrl(properties.getKeyManagement().getUrl())
                .build();
    }
}
