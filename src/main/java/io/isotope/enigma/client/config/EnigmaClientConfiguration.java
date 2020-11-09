package io.isotope.enigma.client.config;

import io.isotope.enigma.client.CryptoClient;
import io.isotope.enigma.client.KeyManagementClient;
import io.isotope.enigma.client.oauth2.AuthService;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({EnigmaClientProperties.class})
public class EnigmaClientConfiguration {

    @Bean
    public KeyManagementClient keyManagerGateway(@Qualifier("enigmaWebClient") WebClient enigmaWebClient) {
        return new KeyManagementClient(enigmaWebClient);
    }

    @Bean
    public CryptoClient cryptoService(@Qualifier("enigmaWebClient") WebClient enigmaWebClient) {
        return new CryptoClient(enigmaWebClient);
    }

    @Bean
    public AuthService authService(WebClient keycloakWebClient, EnigmaClientProperties enigmaClientProperties) {
        return new AuthService(keycloakWebClient, enigmaClientProperties.getOauth2());
    }


    @Bean("enigmaWebClient")
    public WebClient enigmaWebClient(EnigmaClientProperties properties, AuthService authService) throws Exception {

        // filter pri svakom slanju zahteva serveru, da doda access token
        ExchangeFilterFunction authFilterFunction = ((request, next) -> {
            String token = authService.getToken();
            ClientRequest clientRequest = ClientRequest.from(request)
                    .header("Authorization", "Bearer " + token)
                    .build();

            return next.exchange(clientRequest);
        });

        final String keyStorePass = properties.getKeyStorePassword();
        final String trustStorePass = properties.getTrustStorePassword();
        final String keyAlias = properties.getKeyStoreAlias();

        // ucitava trust store
        final KeyStore trustStore = KeyStore.getInstance(properties.getTrustStoreType());
        try (InputStream is = ResourceUtils.getURL(properties.getTrustStore()).openStream()) {
            trustStore.load(is, trustStorePass.toCharArray());
        }

        // ucitava key store
        final KeyStore keyStore = KeyStore.getInstance(properties.getKeyStoreType());
        try (InputStream is = ResourceUtils.getURL(properties.getKeyStore()).openStream()) {
            keyStore.load(is, keyStorePass.toCharArray());
        }

        // izvlaci private key klijenta da bi mogao da kreira digitalni potpis za mTLS autentikaciju sa serverom
        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyStorePass.toCharArray());

        // kreira certificate chain za private key da bi mogao da posalje serveru
        Certificate[] certChain = keyStore.getCertificateChain(keyAlias);
        X509Certificate[] x509CertificateChain = Arrays.stream(certChain)
                .map(certificate -> (X509Certificate) certificate)
                .collect(Collectors.toList())
                .toArray(new X509Certificate[certChain.length]);

        SslContext sslContext = SslContextBuilder.forClient()
                .keyManager(privateKey, keyStorePass, x509CertificateChain)
                .trustManager(extractX509Certificates(trustStore)) // izvlaci trusted sertifikate iz trust store-a
                .build();

        HttpClient httpConnector = HttpClient.create().secure(t -> t.sslContext(sslContext));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpConnector))
                .baseUrl(properties.getEnigmaBaseUrl())
                .filter(authFilterFunction)
                .build();
    }

    private X509Certificate[] extractX509Certificates(KeyStore store) throws KeyStoreException {
        return Collections.list(store.aliases())
                .stream()
                .filter(t -> {
                    try {
                        return store.isCertificateEntry(t);
                    } catch (KeyStoreException e1) {
                        throw new RuntimeException("Error reading truststore", e1);
                    }
                })
                .map(t -> {
                    try {
                        return store.getCertificate(t);
                    } catch (KeyStoreException e2) {
                        throw new RuntimeException("Error reading truststore", e2);
                    }
                }).toArray(X509Certificate[]::new);
    }

    @Bean("keycloakWebClient")
    public WebClient keycloakWebClient(EnigmaClientProperties properties) throws Exception {

        if (properties.getOauth2().getTrustStore() != null) {

            final String trustStorePass = properties.getOauth2().getTrustStorePassword();

            final KeyStore trustStore = KeyStore.getInstance(properties.getOauth2().getTrustStoreType());
            try (InputStream is = ResourceUtils.getURL(properties.getOauth2().getTrustStore()).openStream()) {
                trustStore.load(is, trustStorePass.toCharArray());
            }

            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(extractX509Certificates(trustStore))
                    .build();

            HttpClient httpConnector = HttpClient.create().secure(t -> t.sslContext(sslContext));
            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpConnector))
                    .baseUrl(properties.getOauth2().getOauth2TokenUri())
                    .build();

        } else {
            return WebClient.builder()
                    .baseUrl(properties.getEnigmaBaseUrl())
                    .build();
        }
    }
}
