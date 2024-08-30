package de.lgohlke.homebanking;

import java.util.List;
import java.util.Map;

public class KeepassCredentialsRetriever {

    public List<LoginCredential> retrieveFor(BankingURL bankingURL) {
        KeepassProxyCredentialRetriever retriever = new KeepassProxyCredentialRetriever();
        Map<String, Object> logins = retriever.retrieveLoginsForUrl(bankingURL.getUrl());

        @SuppressWarnings("unchecked") var entries = (List<Map<String, Object>>) logins.get("entries");

        return entries.stream()
                      .map(entry -> {
                          var login = (String) entry.get("login");
                          var password = (String) entry.get("password");
                          return new LoginCredential(login, password);
                      })
                      .toList();
    }
}
