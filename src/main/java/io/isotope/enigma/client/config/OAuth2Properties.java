package io.isotope.enigma.client.config;

public class OAuth2Properties {

    private String oauth2TokenUri;
    private String clientId;
    private String clientSecret;
    private String trustStore;
    private String trustStorePassword;
    private String trustStoreType;

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getOauth2TokenUri() {
        return oauth2TokenUri;
    }

    public void setOauth2TokenUri(String oauthTokenUri) {
        this.oauth2TokenUri = oauthTokenUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
