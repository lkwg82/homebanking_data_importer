package de.lgohlke.homebanking.institutes.traderepublic;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.lgohlke.homebanking.BrowserLauncher;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.InstitutePage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradeRepublicPageIT {
    @Test
    void test_login_Page() {
        try (Browser browser = BrowserLauncher.createHeadlessChromium()) {
            Browser.NewContextOptions options = new Browser.NewContextOptions();
            // needed to run headless
            String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36";
            options.setUserAgent(userAgent);

            try (BrowserContext context = browser.newContext(options)) {
                LoginCredential credential = new LoginCredential("name", "password");
                InstitutePage iPage = new TradeRepublicPage(context, credential);

                iPage.open(); // action

                Page page = iPage.getPage();
                String headline = page.locator(".loginPhoneNumber h2").textContent();

                assertThat(headline).isIn(List.of(
                        "Gib deine Telefonnummer ein", // deutsch
                        "Log in with your phone number" // english
                ));
            }
        }
    }
}