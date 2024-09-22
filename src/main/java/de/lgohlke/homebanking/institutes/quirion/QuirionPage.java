package de.lgohlke.homebanking.institutes.quirion;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.institutes.InstitutePage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
class QuirionPage implements InstitutePage {
    private final static String URL = BankingURL.QUIRION.getUrl();
    private final BrowserContext browserContext;
    private final LoginCredential credentials;

    @Getter
    private Page page;

    public void open() {
        page = browserContext.newPage();
        page.navigate(URL);
        page.waitForLoadState();
        log.info("Quirion page loaded");
    }

    public void login() {
        page.locator("#username").fill(credentials.name());
        page.locator("#password").fill(credentials.password());
        page.click("text='Login'");

        page.waitForLoadState();
        Locator cookieBanner = waitForLocator(page, "text='Nur notwendige Cookies'");
        if (cookieBanner.count() > 0) {
            cookieBanner.click();
        }
    }

    private Locator waitForLocator(Page page, String selector) {
        var waitOptions = new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE);
        page.waitForSelector(selector, waitOptions);
        return page.locator(selector);
    }

    public List<AccountStatus> fetchAccountData() {
        Locator element = waitForLocator(page, "text=Kundennummer");
        Locator neighborDiv = element.locator("xpath=following-sibling::div");
        String customerNumber = neighborDiv.textContent();
        Locator products = waitForLocator(page, "#ProductList_" + customerNumber + " > div");

        if (products.count() == 0) {
            // currently not planned to have empty accounts
            throw new IllegalStateException("did not find any products");
        }

        List<AccountStatus> accountStatuses = new ArrayList<>();
        for (int i = 0; i < products.count(); i++) {
            Locator product = products.nth(0);
            product.locator("button").click();

            var balance = waitForLocator(page, "span[class^='_value_']")
                    .nth(0)
                    .textContent();
            var iban = waitForLocator(page, "xpath=//dt[text()='IBAN']/following-sibling::dd[1]")
                    .nth(0)
                    .textContent();
            accountStatuses.add(AccountStatus.parse(iban, balance, "Quirion")); // TODO namen
        }
        return accountStatuses;
    }
}
