package de.lgohlke.homebanking.institutes.quirion;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.lgohlke.homebanking.BrowserLauncher;
import de.lgohlke.homebanking.LoginCredential;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuirionPageIT {
    @Test
    void test_login_Page() {
        try (Browser browser = BrowserLauncher.createChromium()) {
            BrowserContext context = browser.newContext();
            LoginCredential credential = new LoginCredential("name", "password");
            QuirionPage qPage = new QuirionPage(context, credential);

            qPage.open(); // action

            Page page = qPage.getPage();
            String headline = page.locator("h4").textContent();

            assertThat(headline).isEqualTo("Willkommen bei quirion.");
        }
    }
}