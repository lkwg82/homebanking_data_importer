package de.lgohlke.homebanking.institutes.traderepublic;

import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.PersistentChromiumProfile;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.keepass.KeepassCredentialsRetriever;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

public record TradeRepublicDataRetriever(Path dataDirectory) implements DataFromBankRetriever {
    @SneakyThrows
    @Override
    public void fetchData() {
        KeepassCredentialsRetriever credentialsRetriever = new KeepassCredentialsRetriever();
        LoginCredential credential = credentialsRetriever.retrieveFor(BankingURL.TRADEREPUBLIC).getFirst();

        String path = "/tmp/plStorageStage5"; // TODO security risk??
        PersistentChromiumProfile profile = new PersistentChromiumProfile(path);
        try (var ignored = profile.openBrowser()) {
            var context = profile.getContext();
            TradeRepublicPage page = new TradeRepublicPage(context, credential);
            page.open();
            page.login();
            List<AccountStatus> accountStatuses = page.fetchAccountData();
            new AccountStatusCSVWriter(dataDirectory).writeStatusesToCSV(accountStatuses);
        }
    }
}
