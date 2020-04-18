package io.isotope.enigma.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class KeyManagerGateway {

    private static final Logger log = LoggerFactory.getLogger(KeyManagerGateway.class);

    private final WebClient webClient;

    public KeyManagerGateway(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<Map<String, String>> encryptMap(Map<String, String> plain, String key) {
        log.debug("Fetching key {} specification", key);
        try {
            return webClient.post()
                    .uri("internal/map/encrypt/{key}", key)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .blockOptional(Duration.ofSeconds(10))
                    .map(response -> {
                        log.info(response.toString());
                        return response;
                    });
        }
        catch (Exception e) {
            log.error("Error fetching key", e);
            return Optional.empty();
        }
    }
}
