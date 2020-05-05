package io.isotope.enigma.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(EnigmaClientConfiguration.class)
public class EnigmaClientAutoConfiguration {
}
