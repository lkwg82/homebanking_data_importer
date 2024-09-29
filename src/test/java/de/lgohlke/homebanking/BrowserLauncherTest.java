package de.lgohlke.homebanking;

import com.microsoft.playwright.Browser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrowserLauncherIT {
    @Test
    void shouldLaunch() {
        try (Browser browser = BrowserLauncher.createHeadlessChromium()) {
            assertThat(browser).isNotNull();
        }
    }
}