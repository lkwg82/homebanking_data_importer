package de.lgohlke.homebanking.keepass;

import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.BankingURL;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class KeepassCredentialsRetriever {

    public List<LoginCredential> retrieveFor(BankingURL bankingURL) {
        KeepassProxyCredentialRetriever retriever = new KeepassProxyCredentialRetriever();
        Map<String, Object> logins = retriever.retrieveLoginsForUrl(bankingURL.getUrl());

        @SuppressWarnings("unchecked") var entries = (List<Map<String, Object>>) logins.get("entries");
        if (logins.isEmpty()) {
            log.warn("no entries found for url {}", bankingURL.getUrl());
            return List.of();
        }
        return entries.stream()
                      .map(entry -> {
                          var login = (String) entry.get("login");
                          var password = (String) entry.get("password");
                          return new LoginCredential(login, password);
                      })
                      .toList();
    }
}
