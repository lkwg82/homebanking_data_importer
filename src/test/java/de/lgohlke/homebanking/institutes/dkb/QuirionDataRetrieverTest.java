package de.lgohlke.homebanking.institutes.dkb;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.BankingURL;
import de.lgohlke.homebanking.BrowserLauncher;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.KeepassCredentialsRetriever;
import de.lgohlke.homebanking.LoginCredential;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

public class QuirionDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void testBrowser(@TempDir Path tempdir) {
        new QuirionDataRetriever(tempdir).fetchData();
    }

    @RequiredArgsConstructor
    class QuirionDataRetriever implements DataFromBankRetriever {
        private final Path dataDir;

        @SneakyThrows
        @Override
        public void fetchData() {
            KeepassCredentialsRetriever dkbKeepassCredentialsRetriever = new KeepassCredentialsRetriever();
            LoginCredential loginCredential = dkbKeepassCredentialsRetriever.retrieveFor(BankingURL.QUIRION)
                                                                            .getFirst();

            try (Browser browser = BrowserLauncher.createChromium()) {
                BrowserContext context = browser.newContext();

                QuirionPage page = new QuirionPage(context, loginCredential);
                page.open();
                page.login();
                List<AccountStatus> accountStatuses = page.fetchAccountData();
                accountStatuses.forEach(System.out::println);
//                new AccountStatusCSVWriter(dataDir).writeStatusesToCSV(accountStatuses);
            }
        }
    }

}
