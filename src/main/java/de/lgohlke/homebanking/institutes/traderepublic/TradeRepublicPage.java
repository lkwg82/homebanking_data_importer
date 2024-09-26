package de.lgohlke.homebanking.institutes.traderepublic;

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
class TradeRepublicPage implements InstitutePage {
    private final static String URL = BankingURL.TRADEREPUBLIC.getUrl();

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

        log.info("TR page loaded");

        Locator cookieBanner = page.locator(".consentCard__card .buttonBase__title");
        if (cookieBanner.count() > 0) {
            cookieBanner.nth(0).click();
        }
    }

    @SneakyThrows
    public void login() {
        Locator phoneNumber = page.locator("#loginPhoneNumber__input");
        if (phoneNumber.count() > 0) {
            phoneNumber.fill(credentials.name());
            Thread.sleep(1_000); // not too fast, not to trigger agent detection
            page.locator(".loginPhoneNumber__action").click();

            String password = credentials.password();
            Locator locator2 = page.locator(".loginPin__field");
            for (int i = 0; i < 4; i++) {
                String pin_at_pos = password.substring(i, i + 1);
                locator2.pressSequentially(pin_at_pos);
            }
            Thread.sleep(1_000);

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

    private boolean mfa_waiting(Page page) {
        return page.locator("xpath=//h2[text()='Gib den Bestätigungscode ein']")
                   .count() > 0;
    }

    @SneakyThrows
    public List<AccountStatus> fetchAccountData() {
        page.navigate("https://app.traderepublic.com/settings/accounts");
        page.waitForLoadState();
        String iban = page.locator("xpath=//dt[text()='IBAN']/following-sibling::dd[1]").textContent();

        page.navigate("https://app.traderepublic.com/profile/transactions");
        page.waitForLoadState();
        Thread.sleep(1_000);

        String balanceStr = page.locator(".cashBalance__amount").textContent();
        AccountStatus status = AccountStatus.parse(iban, balanceStr, "TradeRepublic");
        return List.of(status);
    }
}
