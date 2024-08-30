package de.lgohlke.homebanking.dkb;

import de.lgohlke.homebanking.KeepassCredentialRetriever;
import de.lgohlke.homebanking.LoginCredentials;

import java.util.List;
import java.util.Map;

public class DKBKeepassCredentialsRetriever {
    public LoginCredentials retrieve() {
        KeepassCredentialRetriever retriever = new KeepassCredentialRetriever();
        Map<String, Object> logins = retriever.retrieveLoginsForUrl("https://banking.dkb.de");
        @SuppressWarnings("unchecked") var entries = (List<Map<String, Object>>) logins.get("entries");
        Map<String, Object> entry = entries.getFirst();
        var login = (String) entry.get("login");
        var password = (String) entry.get("password");
        return new LoginCredentials(login, password);
    }
}
