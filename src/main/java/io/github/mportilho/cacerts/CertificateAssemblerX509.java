package io.github.mportilho.cacerts;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class CertificateAssemblerX509 {

    protected static final List<String> CERT_EXTENSIONS = Arrays.asList(".pem", ".cer");

    public static void assemble(CertificateAuthorityContainer certContainer, Map<String, CertificateData> certificateDataMap) {
        for (Map.Entry<String, CertificateData> certObject : certificateDataMap.entrySet()) {
            if (CERT_EXTENSIONS.contains(certObject.getKey().substring(certObject.getKey().lastIndexOf('.'), certObject.getKey().length()))) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(certObject.getValue().getContent())) {
                    X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bis);
                    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry(certObject.getKey(), ca);
                    certContainer.addTrustManager(keyStore);
                } catch (Exception e) {
                    throw new CertificateAssemblerException("Error while loading CA " + certObject.getKey(), e);
                }
            }
        }
    }
}
