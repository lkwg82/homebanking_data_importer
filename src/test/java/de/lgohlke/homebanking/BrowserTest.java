package de.lgohlke.homebanking;

import com.microsoft.playwright.BrowserContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

public class BrowserTest {

    @Test
    @Disabled
    void testBrowser(@TempDir Path tempdir) throws Exception {

        DKBKeepassCredentialsRetriever dkbKeepassCredentialsRetriever = new DKBKeepassCredentialsRetriever();
        LoginCredentials loginCredentials = dkbKeepassCredentialsRetriever.retrieve();

        String path = "/tmp/plStorageStage3";
        PersistentChromiumProfile profile = new PersistentChromiumProfile(path);

        try (var ignored = profile.openBrowser()) {
            BrowserContext context = profile.getContext();

            DKBLoginPage dkbLoginPage = new DKBLoginPage(context, loginCredentials);
            dkbLoginPage.open();
            dkbLoginPage.login();
            List<AccountStatus> accountStatuses = dkbLoginPage.fetchAccountData();
            new AccountStatusCSVWriter(tempdir).writeStatusesToCSV(accountStatuses);
        }
    }
}
