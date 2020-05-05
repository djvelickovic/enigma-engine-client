package io.isotope.enigma.client.oauth2;

import io.isotope.enigma.client.config.OAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicLong;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AtomicLong fetchTime = new AtomicLong(0);
    private AccessToken currentAccessToken;


    private final WebClient authClient;
    private final OAuth2Properties oAuth2Properties;

    public AuthService(WebClient authClient, OAuth2Properties oAuth2Properties) {
        this.authClient = authClient;
        this.oAuth2Properties = oAuth2Properties;
    }

    public String getToken() {
        final AccessToken accessToken = currentAccessToken;
        fetchTime.updateAndGet(time -> {
            if (accessToken == null) {
                currentAccessToken = fetchAccessToken();
                logger.info("Fetched new access token");
                return System.currentTimeMillis();
            }
            long systemTime = System.currentTimeMillis();

            if (time + (accessToken.getExpiresIn() - 30) * 1000 < systemTime) {
                if (time + (accessToken.getRefreshExpiresIn() - 30) * 1000 < systemTime) {
                    currentAccessToken = fetchAccessToken();
                    logger.info("Fetched new access token");
                }
                else {
                    currentAccessToken = refreshAccessToken();
                    logger.info("Refreshed access token");
                }

                return systemTime;
            }
            return time;
        });
        return currentAccessToken.getAccessToken();
    }

    private AccessToken fetchAccessToken() {
        return authClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", oAuth2Properties.getClientId())
                        .with("client_secret", oAuth2Properties.getClientSecret())
                        .with("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<AccessToken>() {
                })
                .block();
    }

    private AccessToken refreshAccessToken() {
        return authClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", oAuth2Properties.getClientId())
                        .with("refresh_token", currentAccessToken.getRefreshToken())
                        .with("client_secret", oAuth2Properties.getClientSecret())
                        .with("grant_type", "refresh_token"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<AccessToken>() {
                })
                .block();
    }
}
