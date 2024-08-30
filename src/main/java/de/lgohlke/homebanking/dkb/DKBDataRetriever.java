package de.lgohlke.homebanking.dkb;

import com.microsoft.playwright.BrowserContext;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.BankingURL;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.KeepassCredentialsRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.PersistentChromiumProfile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class DKBDataRetriever implements DataFromBankRetriever {
    private final Path dataDir;

    @SneakyThrows
    @Override
    public void fetchData() {
        KeepassCredentialsRetriever dkbKeepassCredentialsRetriever = new KeepassCredentialsRetriever();
        LoginCredential loginCredential = dkbKeepassCredentialsRetriever.retrieveFor(BankingURL.DKB)
                                                                        .getFirst();

        String path = "/tmp/plStorageStage3"; // TODO security risk??
        PersistentChromiumProfile profile = new PersistentChromiumProfile(path);

        try (var ignored = profile.openBrowser()) {
            BrowserContext context = profile.getContext();

            DKBLoginPage dkbLoginPage = new DKBLoginPage(context, loginCredential);
            dkbLoginPage.open();
            dkbLoginPage.login();
            List<AccountStatus> accountStatuses = dkbLoginPage.fetchAccountData();
            new AccountStatusCSVWriter(dataDir).writeStatusesToCSV(accountStatuses);
        }
    }
}
