package de.lgohlke.homebanking.institutes.scalablecapital;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.lgohlke.homebanking.BrowserLauncher;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.InstitutePage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScalableCapitalPageIT {
    @Test
    void test_login_Page() {
        try (Browser browser = BrowserLauncher.createHeadlessChromium()) {
            BrowserContext context = browser.newContext();
            LoginCredential credential = new LoginCredential("name", "password");
            InstitutePage iPage = new ScalableCapitalPage(context, credential);

            iPage.open(); // action

            Page page = iPage.getPage();
            String headline = page.locator("#page div > h2").textContent();

            assertThat(headline).isEqualTo("Investierenfür alle");
        }
    }
}