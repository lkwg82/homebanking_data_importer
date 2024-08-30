package de.lgohlke.homebanking;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DKBLoginPage {
    private final static String URL = "https://banking.dkb.de";

    private final BrowserContext browserContext;
    private final LoginCredentials credentials;

    @Getter
    private Page page;

    public void open() {
        page = browserContext.newPage();
        page.navigate(URL);
        page.waitForLoadState();
        log.info("DKB page loaded");
    }

    public void login() {
        var headline = page.locator("h1")
                           .textContent();
        if (headline.equals("Mein Banking")) {

            Locator cookieBanner = page.locator("button.btn.refuse-all");
            if (cookieBanner.count() > 0) {
                cookieBanner.click();
            }

            page.locator("#username")
                .fill(credentials.name());
            page.locator("#password")
                .fill(credentials.password());
            page.click("text='Anmelden'");

            if (mfa_waiting(page)) {
                log.warn(">>>> MFA bestätigen");

                while (mfa_waiting(page)) {
                    try {
                        Thread.sleep(3_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("MFA confirmed");
            }

        } else {
            System.out.println("Headline: " + headline);
        }
    }

    List<AccountStatus> fetchAccountData() {

        String selector = "[aria-label^='Umsatzliste von ']";
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        scrollViewPortDown();

        List<AccountStatus> accountStatuses = new ArrayList<>();

        Locator elementsWithAriaLabel = page.locator(selector);
        int count = elementsWithAriaLabel.count();
        for (int i = 0; i < count; i++) {
            Locator current = elementsWithAriaLabel.nth(i);
            Locator title = current.locator(".sui-list-item__left-section__content__title");
            if (title.count() > 0) {
                String name = title.textContent();
                log.info("Name: {}", name.strip());

                String iban = current.getAttribute("aria-label")
                                     .split("IBAN ")[1];
                log.info("IBAN {}", iban);
                String balance = current.locator("p")
                                        .nth(1)
                                        .textContent();

                log.info("Balance {}", balance);
                System.out.println();

                AccountStatus status = AccountStatus.parse(iban, balance, name);
                accountStatuses.add(status);
            }
        }
        return accountStatuses;
    }

    private void scrollViewPortDown() {
        int viewportHeight = page.viewportSize().height;
        int scrollHeight = 0;
        while (scrollHeight < (int) page.evaluate("document.body.scrollHeight")) {
            page.evaluate("window.scrollBy(0, " + viewportHeight + ")");
            scrollHeight += viewportHeight;

            page.waitForTimeout(1000); // 1 Sekunde warten
        }

        log.info("runterscrollen");
    }

    private boolean mfa_waiting(Page page) {
        return page.locator(".mfaApp-modal__wrapper")
                   .count() > 0;
    }
}
