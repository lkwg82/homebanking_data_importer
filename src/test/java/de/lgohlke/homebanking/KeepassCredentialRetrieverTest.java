package de.lgohlke.homebanking;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeepassCredentialRetrieverTest {

    @Test
    @Disabled("keepass needs unlocked")
    void test_setup_proxy() {
        KeepassProxyCredentialRetriever keepassCredentialRetriever = new KeepassProxyCredentialRetriever();
        Map<String, Object> logins = keepassCredentialRetriever.retrieveLoginsForUrl("https://banking.dkb.de");
        assertThat(logins).isNotEmpty();
    }
}