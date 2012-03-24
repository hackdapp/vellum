/*
 * Copyright Evan Summers
 * 
 */
package vellum.provider;

/**
 *
 * @author evan
 */
public class VProviderProperties {
    String serverIp = "localhost";
    String keyAlias = "provider.key";
    String trustAlias = "cipher.cert";
    int sslPort = 7443;
    String keyStore; 
    String trustStore; 
}
