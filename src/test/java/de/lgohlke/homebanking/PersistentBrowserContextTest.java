package de.lgohlke.homebanking;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentBrowserContextTest {
    @SneakyThrows
    @Test
    void shouldKeepState(@TempDir Path tempDir) {
        var cookieName = UUID.randomUUID()
                             .toString();

        try (Browser browser = BrowserLauncher.createHeadlessChromium()) {
            PersistentBrowserContextFactory factory = new PersistentBrowserContextFactory(browser, tempDir);
            try (BrowserContext context = factory.newBrowserContext()) {
                Page page = context.newPage();
                page.navigate("https://gmx.de");

                Cookie cookie = new Cookie(cookieName, "test");
                cookie.setPath("/");
                cookie.setDomain(".gmx.de");
                context.addCookies(List.of(cookie));
            }
        }
        try (Browser browser = BrowserLauncher.createHeadlessChromium()) {
            PersistentBrowserContextFactory factory = new PersistentBrowserContextFactory(browser, tempDir);
            try (BrowserContext context = factory.newBrowserContext()) {
                Page page = context.newPage();
                page.navigate("https://gmx.de");

                boolean found = false;
                for (var c : context.cookies()) {
                    if (c.name.equals(cookieName)) {
                        found = true;
                        break;
                    }
                }
                assertThat(found).isTrue();
            }
        }
    }
}