package io.isotope.enigma.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeyManagerService {

    private static final Logger log = LoggerFactory.getLogger(KeyManagerService.class);

    private final WebClient webClient;

    public KeyManagerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void createKey(String keyName) {
        log.debug("Creating key {}", keyName);
        try {
            Map<String, String> body = new HashMap<>();
            body.put("name", keyName);
            webClient.post()
                    .uri("keys")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new EnigmaException("Unable to create key " + keyName, e);
        }
    }

    public List<KeySpecificationReduced> getKeys() {
        log.debug("Fetching keys");
        try {
            return webClient.post()
                    .uri("keys")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<KeySpecificationReduced>>() {})
                    .block();
        } catch (Exception e) {
            throw new EnigmaException("Unable to fetch keys", e);
        }
    }
}
