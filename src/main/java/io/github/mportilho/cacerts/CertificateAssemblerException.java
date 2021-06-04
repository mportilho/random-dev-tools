package io.github.mportilho.cacerts;

public class CertificateAssemblerException extends RuntimeException {

    public CertificateAssemblerException() {
        super();
    }

    public CertificateAssemblerException(String message) {
        super(message);
    }

    public CertificateAssemblerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateAssemblerException(Throwable cause) {
        super(cause);
    }

    protected CertificateAssemblerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
