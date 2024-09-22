package de.lgohlke.homebanking.institutes.traderepublic;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class TradeRepublicDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void testBrowser(@TempDir Path tempdir) {
        new TradeRepublicDataRetriever(tempdir).fetchData();
    }
}
