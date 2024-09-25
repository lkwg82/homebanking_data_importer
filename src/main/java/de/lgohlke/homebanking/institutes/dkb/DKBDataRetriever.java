package de.lgohlke.homebanking.institutes.dkb;

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


public record DKBDataRetriever(Path dataDirectory) implements DataFromBankRetriever {

    @SneakyThrows
    @Override
    public void fetchData(Browser browser) {
        KeepassCredentialsRetriever dkbKeepassCredentialsRetriever = new KeepassCredentialsRetriever();
        LoginCredential loginCredential = dkbKeepassCredentialsRetriever.retrieveFor(BankingURL.DKB)
                                                                        .getFirst();

        Path path = Path.of("/tmp/plStorageStage3"); // TODO security risk??
        PersistentBrowserContextFactory factory = new PersistentBrowserContextFactory(browser, path);

        try (var context = factory.newBrowserContext()) {
            DKBPage dkbLoginPage = new DKBPage(context, loginCredential);
            dkbLoginPage.open();
            dkbLoginPage.login();
            List<AccountStatus> accountStatuses = dkbLoginPage.fetchAccountData();
            new AccountStatusCSVWriter(dataDirectory).writeStatusesToCSV(accountStatuses);
        }
    }
}
