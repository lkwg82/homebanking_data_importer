package de.lgohlke.homebanking.institutes.scalablecapital;

import com.microsoft.playwright.Browser;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.PersistentBrowserContextFactory;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.institutes.InstitutePage;
import de.lgohlke.homebanking.keepass.KeepassCredentialsRetriever;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

public record ScalableCapitalDataRetriever(Path dataDirectory) implements DataFromBankRetriever {
    @SneakyThrows
    public void fetchData(Browser browser) {
        KeepassCredentialsRetriever credentialsRetriever = new KeepassCredentialsRetriever();
        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(dataDirectory);

        Path path = Path.of("/tmp/plStorageStage6"); // TODO security risk??
        PersistentBrowserContextFactory factory = new PersistentBrowserContextFactory(browser, path);
        for (LoginCredential credential : credentialsRetriever.retrieveFor(BankingURL.SCALABLECAPITAL)) {
            try (var context = factory.newBrowserContext()) {
                InstitutePage page = new ScalableCapitalPage(context, credential);
                page.open();
                page.login();
                List<AccountStatus> accountStatuses = page.fetchAccountData();
                new AccountStatusCSVWriter(dataDirectory).writeStatusesToCSV(accountStatuses);
            }

        }
    }
}
