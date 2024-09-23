package de.lgohlke.homebanking.institutes.scalablecapital;

import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.AccountStatusCSVWriter;
import de.lgohlke.homebanking.DataFromBankRetriever;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.PersistentChromiumProfile;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.institutes.InstitutePage;
import de.lgohlke.homebanking.keepass.KeepassCredentialsRetriever;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

public record ScalableCapitalDataRetriever(Path dataDirectory) implements DataFromBankRetriever {
    @SneakyThrows
    @Override
    public void fetchData() {
        KeepassCredentialsRetriever credentialsRetriever = new KeepassCredentialsRetriever();
        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(dataDirectory);

        String path = "/tmp/plStorageStage6"; // TODO security risk??
        PersistentChromiumProfile profile = new PersistentChromiumProfile(path);
        for (LoginCredential credential : credentialsRetriever.retrieveFor(BankingURL.SCALABLECAPITAL)) {
            try (var ignored = profile.openBrowser()) {
                var context = profile.getContext();
                InstitutePage page = new ScalableCapitalPage(context, credential);
                page.open();
                page.login();
                List<AccountStatus> accountStatuses = page.fetchAccountData();
                new AccountStatusCSVWriter(dataDirectory).writeStatusesToCSV(accountStatuses);
            }

        }
    }
}
