package de.lgohlke.homebanking.institutes.dkb;

import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.PersistentChromiumProfile;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.keepass.KeepassCredentialsRetriever;
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
            var context = profile.getContext();
            DKBLoginPage dkbLoginPage = new DKBLoginPage(context, loginCredential);
            dkbLoginPage.open();
            dkbLoginPage.login();
            List<AccountStatus> accountStatuses = dkbLoginPage.fetchAccountData();
            new AccountStatusCSVWriter(dataDir).writeStatusesToCSV(accountStatuses);
        }
    }

    @Override
    public void collectAndWriteSummary() {
        new AccountStatusCSVWriter(dataDir).writeSummaryToCSV();
    }
}
