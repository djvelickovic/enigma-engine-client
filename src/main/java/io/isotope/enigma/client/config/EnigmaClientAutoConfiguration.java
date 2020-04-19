package io.isotope.enigma.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(EnigmaClientProperties.class)
@Import(EnigmaClientConfiguration.class)
public class EnigmaClientAutoConfiguration {
}
