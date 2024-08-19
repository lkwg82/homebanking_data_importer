package de.lgohlke.homebanking;

import java.util.List;
import java.util.Map;

class DKBKeepassCredentialsRetriever {
    public LoginCredentials retrieve() {
        KeepassCredentialRetriever retriever = new KeepassCredentialRetriever();
        Map<String, Object> logins = retriever.retrieveLoginsForUrl("https://banking.dkb.de");
        var entries = (List<Map<String, Object>>) logins.get("entries");
        Map<String, Object> entry = entries.get(0);
        var login = (String) entry.get("login");
        var password = (String) entry.get("password");
        return new LoginCredentials(login, password);
    }
}
