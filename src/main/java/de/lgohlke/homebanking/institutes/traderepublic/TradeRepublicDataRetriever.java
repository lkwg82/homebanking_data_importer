package de.lgohlke.homebanking.institutes.traderepublic;

import com.microsoft.playwright.Browser;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.PersistentBrowserContextFactory;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.keepass.KeepassCredentialsRetriever;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

public record TradeRepublicDataRetriever(Path dataDirectory) implements DataFromBankRetriever {
    @SneakyThrows
    @Override
    public void fetchData(Browser browser) {
        KeepassCredentialsRetriever credentialsRetriever = new KeepassCredentialsRetriever();
        LoginCredential credential = credentialsRetriever.retrieveFor(BankingURL.TRADEREPUBLIC).getFirst();

        Path path = Path.of("/tmp/plStorageStage5"); // TODO security risk??
        PersistentBrowserContextFactory profile = new PersistentBrowserContextFactory(browser, path);
        try (var context = profile.newBrowserContext()) {
            TradeRepublicPage page = new TradeRepublicPage(context, credential);
            page.open();
            page.login();
            List<AccountStatus> accountStatuses = page.fetchAccountData();
            new AccountStatusCSVWriter(dataDirectory).writeStatusesToCSV(accountStatuses);
        }
    }
}
