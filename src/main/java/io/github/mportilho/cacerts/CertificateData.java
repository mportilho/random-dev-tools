package io.github.mportilho.cacerts;

import java.util.Objects;

public class CertificateData {

    private final String path;
    private final byte[] content;
    private final int loadingPriority;

    public CertificateData(String path, byte[] content, int loadingPriority) {
        this.path = path;
        this.content = content;
        this.loadingPriority = loadingPriority;
    }

    public String getPath() {
        return path;
    }

    public byte[] getContent() {
        return content;
    }

    public int getLoadingPriority() {
        return loadingPriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateData that = (CertificateData) o;
        return loadingPriority == that.loadingPriority && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, loadingPriority);
    }
}
