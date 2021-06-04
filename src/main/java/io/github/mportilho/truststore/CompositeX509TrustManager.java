package io.github.mportilho.truststore;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CompositeX509TrustManager implements X509TrustManager {

    private final List<X509TrustManager> trustManagers;

    public CompositeX509TrustManager(X509TrustManager... children) {
        this.trustManagers = Arrays.asList(children);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        CertificateException lastError = null;
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkClientTrusted(chain, authType);
                return;
            } catch (CertificateException ex) {
                lastError = ex;
            }
        }
        if (lastError != null) {
            throw lastError;
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        CertificateException lastError = null;
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                return;
            } catch (CertificateException ex) {
                lastError = ex;
            }
        }
        if (lastError != null) {
            throw lastError;
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return merge(X509TrustManager::getAcceptedIssuers);
    }

    private X509Certificate[] merge(Function<X509TrustManager, X509Certificate[]> map) {
        List<X509Certificate> list = new ArrayList<>();
        for (X509TrustManager trustManager : trustManagers) {
            list.addAll(Arrays.asList(map.apply(trustManager)));
        }
        return list.toArray(new X509Certificate[list.size()]);
    }
}
