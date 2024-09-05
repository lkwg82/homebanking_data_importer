package de.lgohlke.homebanking;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

class MainDataRetrieverTest {
    @Test
    @Disabled(value = "keepass needs unlocked")
    void test(@TempDir(cleanup = CleanupMode.NEVER) Path dataDir) {
        new MainDataRetriever().execute(dataDir);
    }
}