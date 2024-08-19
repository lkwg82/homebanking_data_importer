package de.lgohlke.homebanking;

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

class PersistentChromiumProfileTest {
    @SneakyThrows
    @Test
    void shouldKeepState(@TempDir Path tempDir) {
        var cookieName = UUID.randomUUID()
                             .toString();
        Cookie cookie = new Cookie(cookieName, "test");
        cookie.setPath("/");
        cookie.setDomain(".gmx.de");

        PersistentChromiumProfile profile = new PersistentChromiumProfile(tempDir.toString());

        try (var ignored = profile.openHeadlessBrowser()) {
            BrowserContext context = profile.getContext();
            Page page = context.newPage();
            page.navigate("https://gmx.de");

            context.addCookies(List.of(cookie));
        }

        try (var ignored = profile.openHeadlessBrowser()) {
            BrowserContext context = profile.getContext();
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