package io.github.mportilho.truststore;

import javax.naming.ConfigurationException;
import javax.net.ssl.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class CertificateAuthorityContainer {

    private static final String DEFAULT_ALGORITHM = KeyManagerFactory.getDefaultAlgorithm();

    private final List<X509KeyManager> keyManagerCollector = new ArrayList<>();
    private final List<X509TrustManager> trustManagerCollector = new ArrayList<>();

    public CertificateAuthorityContainer() {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(DEFAULT_ALGORITHM);
            keyManagerFactory.init(null, null);
            Arrays.stream(keyManagerFactory.getKeyManagers()).filter(x -> x instanceof X509KeyManager)
                    .map(c -> (X509KeyManager) c).forEach(keyManagerCollector::add);

            TrustManagerFactory trustManagerfactory = TrustManagerFactory.getInstance(DEFAULT_ALGORITHM);
            trustManagerfactory.init((KeyStore) null);
            Arrays.stream(trustManagerfactory.getTrustManagers()).filter(x -> x instanceof X509TrustManager)
                    .map(c -> (X509TrustManager) c).forEach(trustManagerCollector::add);
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            throw new IllegalStateException("Error while loading JDK CaCerts", e);
        }
    }

    public void addKeyManager(String certificateIdName, KeyStore keystore, char[] passwd) throws GeneralSecurityException {
        try {
            createKeyManager(keystore, passwd).ifPresent(keyManagerCollector::add);
        } catch (UnrecoverableKeyException e) {
            throw new IllegalStateException(String.format("Error loading CA %s", certificateIdName), e);
        }
    }

    public void addTrustManager(KeyStore keystore) throws GeneralSecurityException {
        createTrustManager(keystore).ifPresent(trustManagerCollector::add);
    }

    public KeyManager[] getKeyManagers() {
        return new KeyManager[]{new CompositeX509KeyManager(keyManagerCollector.toArray(new X509KeyManager[keyManagerCollector.size()]))};
    }

    public TrustManager[] getTrustManagers() {
        return new TrustManager[]{new CompositeX509TrustManager(trustManagerCollector.toArray(new X509TrustManager[trustManagerCollector.size()]))};
    }

    private Optional<X509KeyManager> createKeyManager(KeyStore keystore, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory factory = KeyManagerFactory.getInstance(DEFAULT_ALGORITHM);
        factory.init(keystore, password);
        return Arrays.stream(factory.getKeyManagers()).filter(x -> x instanceof X509KeyManager).findFirst().map(ks -> (X509KeyManager) ks);
    }

    private Optional<X509TrustManager> createTrustManager(KeyStore keystore) throws GeneralSecurityException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(DEFAULT_ALGORITHM);
        factory.init(keystore);
        return Arrays.stream(factory.getTrustManagers()).filter(x -> x instanceof X509TrustManager).findFirst().map(ks -> (X509TrustManager) ks);
    }
}
