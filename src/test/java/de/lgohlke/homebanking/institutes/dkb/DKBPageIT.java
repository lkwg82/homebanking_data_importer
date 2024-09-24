package de.lgohlke.homebanking.institutes.dkb;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.lgohlke.homebanking.BrowserLauncher;
import de.lgohlke.homebanking.LoginCredential;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DKBPageIT {
    @Test
    void test_login_Page() {
        try (Browser browser = BrowserLauncher.createChromium()) {
            BrowserContext context = browser.newContext();
            LoginCredential credential = new LoginCredential("name", "password");
            DKBPage dkbPage = new DKBPage(context, credential);

            dkbPage.open(); // action

            Page page = dkbPage.getPage();
            String headline = page.locator("h1").textContent();

            assertThat(headline).isEqualTo("Mein Banking");
        }
    }
}