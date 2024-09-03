package de.lgohlke.homebanking.institutes.dkb;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.BankingURL;
import de.lgohlke.homebanking.LoginCredential;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
class QuirionPage {
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
        page.locator("#username")
            .fill(credentials.name());
        page.locator("#password")
            .fill(credentials.password());
        page.click("text='Login'");

        page.waitForLoadState();
        String textCookies = "text='Nur notwendige Cookies'";
        page.waitForSelector(textCookies);
        Locator cookieBanner = page.locator(textCookies);
        if (cookieBanner.count() > 0) {
            cookieBanner.click();
        }
    }

    @SneakyThrows
    public List<AccountStatus> fetchAccountData() {
        String selector = "text=Kundennummer";
        var waitOptions = new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE);
        page.waitForSelector(selector, waitOptions);
        Locator element = page.locator(selector);

        Locator neighborDiv = element.locator("xpath=following-sibling::div");

        String customerNumber = neighborDiv.textContent();
        Locator products = page.locator("#ProductList_" + customerNumber + " > div");
        List<AccountStatus> accountStatuses = new ArrayList<>();
        for (int i = 0; i < products.count(); i++) {
            Locator product = products.nth(0);
            product.locator("button")
                   .click();
            String selector1 = "span[class^='_value_']";
            page.waitForSelector(selector1, waitOptions);
            var balance = page.locator(selector1)
                              .nth(0)
                              .textContent();
            var iban = page.locator("xpath=//dt[text()='IBAN']/following-sibling::dd[1]")
                           .nth(0)
                           .textContent();
            accountStatuses.add(AccountStatus.parse(iban, balance, "Quirion")); // TODO namen
        }
        return accountStatuses;
    }
}
