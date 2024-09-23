package de.lgohlke.homebanking.institutes.scalablecapital;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.BankingURL;
import de.lgohlke.homebanking.institutes.InstitutePage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ScalableCapitalPage implements InstitutePage {
    private final static String URL = BankingURL.SCALABLECAPITAL.getUrl();

    private final BrowserContext browserContext;
    private final LoginCredential credentials;
    @Getter
    private Page page;

    @SneakyThrows
    public void open() {
        page = browserContext.newPage();

        // Override navigator.webdriver before navigating to any page
        // needs to be able to run
        // not to trigger agent detection
        page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        page.navigate(URL);
        page.waitForLoadState();

        log.info("SC page loaded");
    }

    @SneakyThrows
    public void login() {
        denyCookies();

        page.locator("#page a.login").nth(1).click();
        page.waitForLoadState();

        Locator locator = page.locator("input#username");
        if (locator.count() > 0) {
            locator.fill(credentials.name());
            page.locator("input#password").fill(credentials.password());
            page.click("text='Login'");


            if (mfa_waiting(page)) {
                log.warn(">>>> MFA bestätigen");

                while (mfa_waiting(page)) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(3_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("MFA confirmed");
            }
        } else {
            log.info("still logged in");
        }
    }

    private void denyCookies() throws InterruptedException {
        Thread.sleep(1000);
        Locator denyCookies = page.locator("button[data-testid='uc-deny-all-button']");
        if (denyCookies.count() > 0) {
            denyCookies.click();
        }
    }

    private boolean mfa_waiting(Page page) {
        return page.locator("xpath=//p[text()='Verifizieren Sie Ihren Login']")
                   .count() > 0;
    }

    @SneakyThrows
    public List<AccountStatus> fetchAccountData() {
        page.navigate("https://de.scalable.capital/cockpit/account/products");
        page.waitForLoadState();
        String iban = page.locator("[data-testid='iban']").textContent();

        page.navigate("https://de.scalable.capital/broker/cash");
        page.waitForLoadState();
        Thread.sleep(1_000);

        String balanceStr = page.locator("xpath=//div[text()='Kontostand']/preceding-sibling::div").textContent();
        AccountStatus status = AccountStatus.parse(iban, balanceStr, "ScalableCapital");
        return List.of(status);
    }
}
