package de.lgohlke.homebanking.institutes.scalablecapital;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class ScalableCapitalDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void testBrowser(@TempDir Path tempdir) {
        new ScalableCapitalDataRetriever(tempdir).fetchData();
    }
}
