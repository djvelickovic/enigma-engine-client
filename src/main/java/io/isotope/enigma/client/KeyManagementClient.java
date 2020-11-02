package io.isotope.enigma.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyManagementClient {

    private static final Logger log = LoggerFactory.getLogger(KeyManagementClient.class);

    private final WebClient webClient;

    public KeyManagementClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void createKey(String keyName, Integer size) {
        log.debug("Creating key with name {} and size {}", keyName, size);
        try {
            Map<String, String> body = new HashMap<>();
            body.put("name", keyName);
            body.put("size", Integer.toString(size));
            webClient.post()
                    .uri("keys")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new EnigmaException("Unable to create key " + keyName, e);
        }
    }

    public List<RSAKeyMetadata> getKeys() {
        log.debug("Fetching keys");
        try {
            return webClient.get()
                    .uri("keys")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<RSAKeyMetadata>>() {
                    })
                    .block();
        } catch (Exception e) {
            throw new EnigmaException("Unable to fetch keys", e);
        }
    }

    public void updateKey(String key, UpdateKeyRequest request) {
        try {
            webClient.put()
                    .uri("keys/{key}", key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new EnigmaException("Unable to update key " + key, e);
        }
    }
}
