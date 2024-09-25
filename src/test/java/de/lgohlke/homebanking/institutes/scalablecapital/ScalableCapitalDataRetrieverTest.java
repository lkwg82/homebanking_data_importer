package de.lgohlke.homebanking.institutes.scalablecapital;

import com.microsoft.playwright.Browser;
import de.lgohlke.homebanking.BrowserLauncher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class ScalableCapitalDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void testBrowser(@TempDir Path tempdir) {
        try (Browser browser = BrowserLauncher.createChromium()) {
            new ScalableCapitalDataRetriever(tempdir).fetchData(browser);
        }
    }
}
