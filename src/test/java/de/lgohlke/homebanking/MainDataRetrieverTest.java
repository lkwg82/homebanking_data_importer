package de.lgohlke.homebanking;

import de.lgohlke.homebanking.institutes.dkb.DKBDataRetriever;
import de.lgohlke.homebanking.institutes.quirion.QuirionDataRetriever;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

class MainDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void test(@TempDir(cleanup = CleanupMode.NEVER) Path dataDir) {
        List<DataFromBankRetriever> retrievers = List.of(new DKBDataRetriever(dataDir.resolve("dkb")),
                                                         new QuirionDataRetriever(dataDir.resolve("quirion")));

        retrievers.forEach(DataFromBankRetriever::fetchData);
        System.out.println(dataDir);
    }
}