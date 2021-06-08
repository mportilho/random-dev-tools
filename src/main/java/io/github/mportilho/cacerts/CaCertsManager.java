package io.github.mportilho.cacerts;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CaCertsManager {

    protected static final List<String> CERT_EXTENSIONS = new ArrayList<>();

    static {
        CERT_EXTENSIONS.addAll(CertificateAssemblerX509.CERT_EXTENSIONS);
//        CERT_EXTENSIONS.addAll(CertificateAssemblerJKS.CERT_EXTENSIONS);
        CERT_EXTENSIONS.addAll(CertificateAssemblerPKCS7.CERT_EXTENSIONS);
    }

    private static volatile boolean loaded = false;

    private static SSLContext sslContext;
    private static KeyManager[] keyManagers;
    private static TrustManager[] trustManagers;

    private CaCertsManager() {
        // empty
    }

    public synchronized void loadCertificatesFrom(String[] paths) {
        if (!loaded) {
            loaded = true;
            Map<String, CertificateData> certificateDataMap = CertificateAuthorityLoader.loadFromPaths(paths);
        }
    }

    public synchronized void loadCertificateFrom(String path, CertificateAuthorityLoader.CertificateLocation certificateLocation) {
        if (!loaded) {
            loaded = true;
            Map<String, CertificateData> certificateDataMap = CertificateAuthorityLoader.loadFrom(path, certificateLocation);
        }
    }

    public void injectCAs(Map<String, CertificateData> certificateDataMap) {
        CertificateAuthorityContainer certContainer = new CertificateAuthorityContainer();

    }


}
