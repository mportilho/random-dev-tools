package io.github.mportilho.truststore;

import io.github.mportilho.assertions.Asserts;

import java.util.HashMap;
import java.util.Map;

public final class CaCertsManager {


    public void loadCertificatesFrom(String[] paths) {
        Map<String, CertificateData> certificateDataMap = CertificateAuthorityLoader.loadFromPaths(paths);
    }

    public void loadCertificateFrom(String path, CertificateAuthorityLoader.CertificateLocation certificateLocation) {
        Map<String, CertificateData> certificateDataMap = CertificateAuthorityLoader.loadFrom(path, certificateLocation);
    }


}
