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
package vellum.security;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.security.pkcs.PKCS10;
import sun.security.pkcs.PKCS10Attribute;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X500Signer;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class Certificates {
    public static final String LOCAL_DNAME = 
            "CN=localhost, OU=local, O=local, L=local, S=local, C=local";
    
    public static String formatDname(String cn, String ou, String o, String l, 
            String s, String c) {
        cn = Strings.coalesce(cn, "unknown");
        ou = Strings.coalesce(ou, "unknown");
        o = Strings.coalesce(o, "unknown");
        l = Strings.coalesce(l, "unknown");
        s = Strings.coalesce(s, "unknown");
        c = Strings.coalesce(c, "unknown");
        return String.format("CN=%s, OU=%s, O=%s, L=%s, S=%s, C=%s", cn, ou, o, l, s, c);
    }

    public static String getCommonName(String subject) {
        try {
            return new X500Name(subject).getCommonName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static X509Certificate findRootCert(KeyStore keyStore, String alias) throws Exception {
        return findRootCert(keyStore.getCertificateChain(alias));
    }    
    
    public static X509Certificate findRootCert(Certificate[] chain) throws Exception {
        for (Certificate cert : chain) {
            if (cert instanceof X509Certificate) {
                X509Certificate x509Cert = (X509Certificate) cert;
                if (x509Cert.getSubjectDN().equals(x509Cert.getIssuerDN())) {
                    return x509Cert;
                }
            }
        }
        return null;
    }

    public static PKCS10 createCertReq(PrivateKey privateKey, X509Certificate cert) 
            throws Exception {
        String sigAlgName = "SHA256WithRSA";
        PKCS10 request = new PKCS10(cert.getPublicKey());
        if (false) {
            CertificateExtensions ext = new CertificateExtensions();
            request.getAttributes().setAttribute(X509CertInfo.EXTENSIONS,
                    new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, ext));
        }
        Signature signature = Signature.getInstance(sigAlgName);
        signature.initSign(privateKey);
        X500Name subject = new X500Name(cert.getSubjectDN().toString());
        X500Signer signer = new X500Signer(signature, subject);
        request.encodeAndSign(signer);
        //request.encodeAndSign(subject, signature);
        return request;
    }

    public static X509Certificate signCert(PrivateKey signingKey, X509Certificate signingCert,
            PKCS10 certReq, Date startDate, int validityDays) throws Exception {
        String sigAlgName = "SHA256WithRSA";
        Date endDate = new Date(startDate.getTime() + TimeUnit.DAYS.toMillis(validityDays));
        CertificateValidity validity = new CertificateValidity(startDate, endDate);
        byte[] encoded = signingCert.getEncoded();
        X509CertImpl signerCertImpl = new X509CertImpl(encoded);
        X509CertInfo signerCertInfo = (X509CertInfo) signerCertImpl.get(
                X509CertImpl.NAME + "." + X509CertImpl.INFO);
        X500Name issuer = (X500Name) signerCertInfo.get(
                X509CertInfo.SUBJECT + "." + CertificateSubjectName.DN_NAME);
        Signature signature = Signature.getInstance(sigAlgName);
        signature.initSign(signingKey);
        X509CertImpl cert = new X509CertImpl(buildCertInfo(issuer, certReq, 
                sigAlgName, validity));
        cert.sign(signingKey, sigAlgName);
        return cert;
    }
    
    private static X509CertInfo buildCertInfo(X500Name issuer, PKCS10 certReq, 
            String sigAlgName, CertificateValidity validity) throws Exception {
        X509CertInfo info = new X509CertInfo();
        info.set(X509CertInfo.VALIDITY, validity);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
                new java.util.Random().nextInt() & 0x7fffffff));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.ALGORITHM_ID, 
                new CertificateAlgorithmId(AlgorithmId.get(sigAlgName)));
        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issuer));
        info.set(X509CertInfo.KEY, new CertificateX509Key(certReq.getSubjectPublicKeyInfo()));
        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(certReq.getSubjectName()));
        return info;
    }

}