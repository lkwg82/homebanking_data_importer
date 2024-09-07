package de.lgohlke.homebanking.institutes.dkb;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.WaitForSelectorState;
import de.lgohlke.homebanking.AccountStatus;
import de.lgohlke.homebanking.LoginCredential;
import de.lgohlke.homebanking.institutes.BankingURL;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DKBLoginPage {
    private final static String URL = BankingURL.DKB.getUrl();

    private final BrowserContext browserContext;
    private final LoginCredential credentials;

    @Getter
    private Page page;

    public void open() {
        List<Cookie> privacySettingCookies = createPrivacySettingCookies();
        browserContext.addCookies(privacySettingCookies);
        page = browserContext.newPage();
        page.navigate(URL);
        page.waitForLoadState();
        log.info("DKB page loaded");
    }

    public void login() {
        var headline = page.locator("h1")
                           .textContent();
        if (headline.equals("Mein Banking")) {


            page.locator("#username")
                .fill(credentials.name());
            page.locator("#password")
                .fill(credentials.password());
            page.click("text='Anmelden'");

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
            System.out.println("Headline: " + headline);
        }
    }

    private List<Cookie> createPrivacySettingCookies() {
        Cookie tcPrivacy = new Cookie("TC_PRIVACY",
                                      "1%40006%7C47%7C4898%40%401%401725603940191%2C1725603940191%2C1759299940191%40")
                .setDomain(".dkb.de")
                .setPath("/");
        Cookie tcPrivacy2 = new Cookie("TC_PRIVACY_CENTER", "")
                .setDomain(".dkb.de")
                .setPath("/");

        return List.of(tcPrivacy, tcPrivacy2);
    }

    public List<AccountStatus> fetchAccountData() {

        String selector = "[aria-label^='Umsatzliste von ']";
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        scrollViewPortDown();

        List<AccountStatus> accountStatuses = new ArrayList<>();

        Locator elementsWithAriaLabel = page.locator(selector);
        int count = elementsWithAriaLabel.count();
        for (int i = 0; i < count; i++) {
//            log.info("current");
            Locator current = elementsWithAriaLabel.nth(i);
            current.evaluate("element => element.style.border = '2px solid red'");
//            log.info("title");
            Locator title = current.locator(".sui-list-item__left-section__content__title");
            if (title.count() > 0) {
                String name = title.textContent();
                log.info("name: {}", name);
                String iban = current.getAttribute("aria-label")
                                     .split("IBAN ")[1];
                String balance = current.locator("p")
                                        .nth(0)
                                        .textContent();

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

            page.waitForTimeout(2_000); // 2 Sekunde warten
        }

        log.info("runterscrollen");
    }

    private boolean mfa_waiting(Page page) {
        return page.locator(".mfaApp-modal__wrapper")
                   .count() > 0;
    }
}
