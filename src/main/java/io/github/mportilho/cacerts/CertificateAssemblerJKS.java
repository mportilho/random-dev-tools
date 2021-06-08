//package io.github.mportilho.cacerts;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.security.KeyStore;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//public class CertificateAssemblerJKS {
//
//    protected static final List<String> CERT_EXTENSIONS = Arrays.asList(".jks");
//
//    public static void assemble(CertificateAuthorityContainer certContainer, Map<String, CertificateData> certificateDataMap) {
//        for (Map.Entry<String, CertificateData> certObject : certificateDataMap.entrySet()) {
//            if (CERT_EXTENSIONS.contains(certObject.getKey().substring(certObject.getKey().lastIndexOf('.'), certObject.getKey().length()))) {
//                attemptKeyStoreCreation(certContainer, certObject.getKey(), certObject.getValue());
//            }
//        }
//    }
//
//    private static void attemptKeyStoreCreation(CertificateAuthorityContainer certContainer, String certName, CertificateData data) {
//        char[] pwd = CertificatePasswordRetriever.getPasswordFor(certName, certsMap);
//        try {
//            addKeyStore(certContainer, certName, data, pwd);
//        } catch (Exception e) {
//            try {
//                addKeyStore(certContainer, certName, data, null);
//            } catch (Exception e1) {
//                throw new CertificateAssemblerException("Error while loading CA " + certName, e);
//            }
//        }
//    }
//
//    private static void addKeyStore(CertificateAuthorityContainer certContainer, String certName, CertificateData data,
//                                    char[] pwd) throws IOException, GeneralSecurityException {
//        try (ByteArrayInputStream bis = new ByteArrayInputStream(data.getContent())) {
//            KeyStore keyStore = KeyStore.getInstance("JKS");
//            keyStore.load(bis, pwd);
//            certContainer.addKeyManager(certName, keyStore, pwd);
//            certContainer.addTrustManager(keyStore);
//        }
//    }
//
//}
