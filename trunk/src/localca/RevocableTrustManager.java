/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package localca;

import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class RevocableTrustManager implements X509TrustManager {
    static Logger logger = LoggerFactory.getLogger(RevocableTrustManager.class);

    X509Certificate parentCertificate;
    X509TrustManager delegate;
    Set<String> revokedCommonNames;
    Set<BigInteger> revokedSerialNumbers;
    
    public RevocableTrustManager(X509Certificate parentCertificate, 
            X509TrustManager delegate, 
            Set<String> revokedCommonNames,
            Set<BigInteger> revokedSerialNumbers) {
        this.delegate = delegate;
        this.parentCertificate = parentCertificate;
        this.revokedCommonNames = Collections.synchronizedSet(revokedCommonNames);
        this.revokedSerialNumbers = Collections.synchronizedSet(revokedSerialNumbers);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        logger.debug("getAcceptedIssuers");
        return new X509Certificate[] {parentCertificate};
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) 
            throws CertificateException {
        logger.debug("checkClientTrusted {} {}", certs[0].getSubjectDN().getName(), authType);
        if (certs.length < 2) {
            throw new CertificateException("Invalid cert chain length");
        }
        if (!certs[0].getIssuerX500Principal().equals(
                parentCertificate.getSubjectX500Principal())) {
            throw new CertificateException("Untrusted issuer");
        }
        if (certs[1].getSubjectDN().equals(parentCertificate.getSubjectDN())) {
            throw new CertificateException("Invalid parent certificate");        
        }
        if (!Arrays.equals(certs[1].getPublicKey().getEncoded(),
                parentCertificate.getPublicKey().getEncoded())) {
            throw new CertificateException("Invalid parent certificate key");
        }
        if (revokedCommonNames != null && 
                revokedCommonNames.contains(X509Certificates.getCN(certs[0].getSubjectDN()))) {
            throw new CertificateException("Certificate CN revoked");
        }
        if (revokedSerialNumbers != null && 
                revokedSerialNumbers.contains(certs[0].getSerialNumber())) {
            throw new CertificateException("Certificate serial number revoked");
        }
        delegate.checkClientTrusted(certs, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) 
            throws CertificateException {
        logger.debug("checkServerTrusted {} {}", certs[0].getSubjectDN().getName(), authType);
        if (certs.length == 0) {
            throw new CertificateException("Empty cert chain");
        }
        if (!X509Certificates.equals(certs[0], parentCertificate)) {
            throw new CertificateException("Untrusted server certificate");
        }
        delegate.checkServerTrusted(certs, authType);
    }
    
}