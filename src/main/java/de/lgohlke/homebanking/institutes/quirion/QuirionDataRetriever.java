package de.lgohlke.homebanking.institutes.quirion;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.institutes.InstitutePage;
import de.lgohlke.homebanking.keepass.KeepassCredentialsRetriever;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

@Slf4j
public record QuirionDataRetriever(Path dataDirectory) implements DataFromBankRetriever {
    @Override
    public void fetchData(Browser browser) {
        KeepassCredentialsRetriever credentialsRetriever = new KeepassCredentialsRetriever();
        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(dataDirectory);

        for (LoginCredential loginCredential : credentialsRetriever.retrieveFor(BankingURL.QUIRION)) {
            try (BrowserContext context = browser.newContext()) {
                InstitutePage page = new QuirionPage(context, loginCredential);
                page.open();
                page.login();
                List<AccountStatus> accountStatuses = page.fetchAccountData();
                accountStatusCSVWriter.writeStatusesToCSV(accountStatuses);
            }
        }
    }
}
