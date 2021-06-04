package io.github.mportilho.truststore;

import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CompositeX509KeyManager implements X509KeyManager {

    private final List<X509KeyManager> keyManagers;

    public CompositeX509KeyManager(X509KeyManager... children) {
        this.keyManagers = Arrays.asList(children);
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return getFirstNonNull(x -> x.chooseClientAlias(keyType, issuers, socket));
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return getFirstNonNull(x -> x.chooseServerAlias(keyType, issuers, socket));
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return getFirstNonNull(x -> x.getCertificateChain(alias));
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return getFirstNonNull(x -> x.getPrivateKey(alias));
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return merge(x -> x.getClientAliases(keyType, issuers));
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return merge(x -> x.getServerAliases(keyType, issuers));
    }

    private <T> T getFirstNonNull(Function<X509KeyManager, T> map) {
        for (X509KeyManager keyManager : keyManagers) {
            T out = map.apply(keyManager);
            if (out != null) {
                return out;
            }
        }
        return null;
    }

    private String[] merge(Function<X509KeyManager, String[]> map) {
        List<String> list = new ArrayList<>();
        for (X509KeyManager keyManager : keyManagers) {
            list.addAll(Arrays.asList(map.apply(keyManager)));
        }
        return list.toArray(new String[list.size()]);
    }
}
