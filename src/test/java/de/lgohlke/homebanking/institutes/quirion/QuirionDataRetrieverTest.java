package de.lgohlke.homebanking.institutes.quirion;

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
        quirionDataRetriever.fetchData();
        quirionDataRetriever.collectAndWriteSummary();

        assertThat(tempdir).isNotEmptyDirectory();

        File[] files = tempdir.toFile().listFiles();
        assertThat(files).hasSize(4);

        for (File ibanDir : files) {
            if (ibanDir.isFile()) {
                assertThat(ibanDir).hasFileName("summary.csv");
            } else {
                assertThat(ibanDir).isNotEmptyDirectory();
                File[] statusFiles = ibanDir.listFiles();
                assertThat(statusFiles[0]).isNotEmpty();
            }
        }
    }
}
