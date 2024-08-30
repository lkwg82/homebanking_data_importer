package de.lgohlke.homebanking;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Slf4j
public class PersistentChromiumProfile {
    private final String path;
    @Getter
    private Browser browser;
    @Getter
    private BrowserContext context;
    private Path storage;

    public AutoCloseable openBrowser() throws IOException {
        browser = BrowserLauncher.createChromium();
        return configureStorage();
    }

    public AutoCloseable openHeadlessBrowser() throws IOException {
        browser = BrowserLauncher.createHeadlessChromium();
        return configureStorage();
    }

    private AutoCloseable configureStorage() throws IOException {
        storage = retrieveStorageState();
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        if (storage.toFile()
                   .length() > 0) { // not empty file
            options.setStorageStatePath(storage);
        }
        context = browser.newContext(options);

        return this::saveState;
    }

    private void saveState() {
        var storageStateOptions = new BrowserContext.StorageStateOptions().setPath(storage);

        log.info("saves state");
        context.storageState(storageStateOptions);
    }

    private Path retrieveStorageState() throws IOException {
        Path storageStateDir = Paths.get(path);
        Files.createDirectories(storageStateDir);
        return storageStateDir.resolve("state.json");
    }
}
