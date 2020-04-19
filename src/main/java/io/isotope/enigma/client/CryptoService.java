package io.isotope.enigma.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class CryptoService {

    private static final Logger log = LoggerFactory.getLogger(CryptoService.class);

    private final WebClient webClient;

    public CryptoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Map<String, String> encryptMap(Map<String, String> values, String key) {
        log.debug("Fetching key {} specification", key);
        try {
            return webClient.post()
                    .uri("crypto/map/encrypt/{key}", key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(values)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .block();
        } catch (Exception e) {
            throw new EnigmaException(e);
        }
    }

    public Map<String, String> decryptMap(Map<String, String> values, String key) {
        log.debug("Fetching key {} specification", key);
        try {
            return webClient.post()
                    .uri("crypto/map/decrypt/{key}", key)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .block();
        } catch (Exception e) {
            throw new EnigmaException(e);
        }
    }
}
