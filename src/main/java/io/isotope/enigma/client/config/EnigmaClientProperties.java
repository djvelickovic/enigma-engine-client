package io.isotope.enigma.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "enigma.client")
public class EnigmaClientProperties {

    private String enigmaBaseUrl;
    private Boolean twoWayEnabled = Boolean.TRUE;

    private String keyStore;
    private String keyStoreType = "pcks12";
    private String keyStorePassword;
    private String keyStoreAlias;

    private String trustStore;
    private String trustStoreType = "pcks12";
    private String trustStorePassword;

    public Boolean getTwoWayEnabled() {
        return twoWayEnabled;
    }

    public void setTwoWayEnabled(Boolean twoWayEnabled) {
        this.twoWayEnabled = twoWayEnabled;
    }

    public String getEnigmaBaseUrl() {
        return enigmaBaseUrl;
    }

    public void setEnigmaBaseUrl(String enigmaBaseUrl) {
        this.enigmaBaseUrl = enigmaBaseUrl;
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public void setKeyStoreAlias(String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }
}
