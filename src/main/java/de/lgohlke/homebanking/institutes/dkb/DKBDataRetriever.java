package de.lgohlke.homebanking.institutes.dkb;

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


public record DKBDataRetriever(Path dataDirectory) implements DataFromBankRetriever {

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
            DKBPage dkbLoginPage = new DKBPage(context, loginCredential);
            dkbLoginPage.open();
            dkbLoginPage.login();
            List<AccountStatus> accountStatuses = dkbLoginPage.fetchAccountData();
            new AccountStatusCSVWriter(dataDirectory).writeStatusesToCSV(accountStatuses);
        }
    }
}
