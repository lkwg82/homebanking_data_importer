package de.lgohlke.homebanking.institutes.quirion;

import com.microsoft.playwright.Browser;
import de.lgohlke.homebanking.BrowserLauncher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class QuirionDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void testBrowser(@TempDir() Path tempdir) {
        QuirionDataRetriever quirionDataRetriever = new QuirionDataRetriever(tempdir);
        try (Browser browser = BrowserLauncher.createChromium()) {
            quirionDataRetriever.fetchData(browser);
        }

        assertThat(tempdir).isNotEmptyDirectory();

        File[] files = tempdir.toFile().listFiles();
        assertThat(files).hasSize(3);

        for (File ibanDir : files) {
            assertThat(ibanDir).isNotEmptyDirectory();
            File[] statusFiles = ibanDir.listFiles();
            assertThat(statusFiles[0]).isNotEmpty();
        }
    }
}
