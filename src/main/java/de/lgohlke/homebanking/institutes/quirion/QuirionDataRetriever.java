package de.lgohlke.homebanking.institutes.quirion;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.BankingURL;
import de.lgohlke.homebanking.BrowserLauncher;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.KeepassCredentialsRetriever;
import de.lgohlke.homebanking.LoginCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class QuirionDataRetriever implements DataFromBankRetriever {
    private final Path dataDir;

    @Override
    public void fetchData() {
        KeepassCredentialsRetriever dkbKeepassCredentialsRetriever = new KeepassCredentialsRetriever();
        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(dataDir);

        for (LoginCredential loginCredential : dkbKeepassCredentialsRetriever.retrieveFor(BankingURL.QUIRION)) {
            try (Browser browser = BrowserLauncher.createChromium()) {
                BrowserContext context = browser.newContext();

                QuirionPage page = new QuirionPage(context, loginCredential);
                page.open();
                page.login();
                List<AccountStatus> accountStatuses = page.fetchAccountData();
                accountStatusCSVWriter.writeStatusesToCSV(accountStatuses);
            }
        }
    }
}
