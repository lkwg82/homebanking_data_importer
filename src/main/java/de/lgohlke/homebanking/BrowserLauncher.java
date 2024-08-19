package de.lgohlke.homebanking;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

public class BrowserLauncher {
    public static Browser createChromium() {
        return createChromium(false);
    }

    public static Browser createHeadlessChromium() {
        return createChromium(true);
    }

    private static Browser createChromium(boolean headless) {
        Playwright playwright = Playwright.create();
        BrowserType browserType = playwright.chromium();
        var options = new BrowserType.LaunchOptions();
        options.setHeadless(headless);
        return browserType.launch(options);
    }
}
