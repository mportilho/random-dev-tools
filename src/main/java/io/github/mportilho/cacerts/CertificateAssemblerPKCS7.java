package io.github.mportilho.cacerts;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class CertificateAssemblerPKCS7 {

    protected static final List<String> CERT_EXTENSIONS = Arrays.asList(".p7b");

    public static void assemble(CertificateAuthorityContainer certContainer, Map<String, CertificateData> certificateDataMap) {
        for (Map.Entry<String, CertificateData> certObject : certificateDataMap.entrySet()) {
            if (CERT_EXTENSIONS.contains(certObject.getKey().substring(certObject.getKey().lastIndexOf('.'), certObject.getKey().length()))) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(certObject.getValue().getContent())) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    Iterator<? extends Certificate> certsIterator = cf.generateCertificates(bis).iterator();
                    while (certsIterator.hasNext()) {
                        Certificate ca = certsIterator.next();
                        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        keyStore.load(null, null);
                        keyStore.setCertificateEntry(certObject.getKey(), ca);
                        certContainer.addTrustManager(keyStore);
                    }
                } catch (Exception e) {
                    throw new CertificateAssemblerException("Error while loading CA " + certObject.getKey(), e);
                }
            }
        }
    }
}
