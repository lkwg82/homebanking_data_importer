package de.lgohlke.homebanking;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class BrowserTest {

    @RequiredArgsConstructor
    public class DKBLoginPage {
        private final static String URL = "https://banking.dkb.de";

        private final BrowserContext browserContext;
        private final LoginCredentials credentials;

        @Getter
        private Page page;

        public void open() {
            addPrivacyCookies();

            page = browserContext.newPage();
            page.navigate(URL);
            page.waitForLoadState();

            var headline = page.locator("h1")
                               .textContent();
            System.out.println(page.title());
            if (headline.equals("Mein Banking")) {

//            page.locator("button.btn.refuse-all")
//                .click();
                page.locator("#username")
                    .fill(credentials.name());
                page.locator("#password")
                    .fill(credentials.password());
                page.click("text='Anmelden'");
            } else {
                System.out.println("Headline: " + headline);
            }
        }

        private void addPrivacyCookies() {
            var c1 = new Cookie("TC_PRIVACY",
                                "1%40006%7C47%7C4898%40%401%401723439007446%2C1723439007446%2C1757135007446%40");
            c1.setDomain(".dkb.de");
            c1.setPath("/");
            var c2 = new Cookie("TC_PRIVACY_CENTER", "");
            c2.setDomain(".dkb.de");
            c2.setPath("/");
            browserContext.addCookies(List.of(c1, c2));
        }
    }

    @Test
    @Disabled
    void testBrowser() throws Exception {

        DKBKeepassCredentialsRetriever dkbKeepassCredentialsRetriever = new DKBKeepassCredentialsRetriever();
        LoginCredentials loginCredentials = dkbKeepassCredentialsRetriever.retrieve();

        String path = "/tmp/plStorageStage2";
        PersistentChromiumProfile profile = new PersistentChromiumProfile(path);

        try (var ignored = profile.openBrowser()) {
            BrowserContext context = profile.getContext();

            DKBLoginPage dkbLoginPage = new DKBLoginPage(context, loginCredentials);
            dkbLoginPage.open();

//            // Annehmen, dass der Download-Button einen spezifischen Selektor hat
//            page.click("text='Anmelden'");
//
//            // Warten auf den Download
//            Download download = page.waitForDownload(() -> {
//                page.click("text='Download CSV'");
//            });
//
//            // Speichern der CSV-Datei im aktuellen Verzeichnis
//            download.saveAs(Paths.get("downloaded-file.csv"));

        }
    }

}
