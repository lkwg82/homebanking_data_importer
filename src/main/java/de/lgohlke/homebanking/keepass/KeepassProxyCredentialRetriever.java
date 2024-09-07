package de.lgohlke.homebanking.keepass;

import de.lgohlke.homebanking.sops.SopsDecryptor;
import lombok.extern.slf4j.Slf4j;
import org.purejava.KeepassProxyAccess;

import java.util.List;
import java.util.Map;

@Slf4j
public class KeepassProxyCredentialRetriever {

    public Map<String, Object> retrieveLoginsForUrl(String url) {

        var sopsDecryptor = new SopsDecryptor("keepass.enc.yaml");
        String content = sopsDecryptor.getContent();
        var lines = content.split("\\n");
        var id = lines[0].replaceFirst("id: ", "");
        var key = lines[1].replaceFirst("key: ", "");

        KeepassProxyAccess keepassProxyAccess = new KeepassProxyAccess();
        try {
            if (keepassProxyAccess.connect()) {
                log.info("connected");
            } else {
                throw new IllegalStateException("connection failed");
            }
            if (keepassProxyAccess.testAssociate(id, key)) {
                log.info("keepass works");
            } else {
                throw new IllegalStateException("no connection to keepass");
            }

            var id_key_map = List.of(Map.of("id", id, "key", key));
            return keepassProxyAccess.getLogins(url, "", true, id_key_map);
        } finally {
            if (keepassProxyAccess.connect()) {
                log.info("closing");
                keepassProxyAccess.shutdown();
                keepassProxyAccess.closeConnection();
                keepassProxyAccess.getScheduler().shutdownNow();
            }
        }
    }
}
