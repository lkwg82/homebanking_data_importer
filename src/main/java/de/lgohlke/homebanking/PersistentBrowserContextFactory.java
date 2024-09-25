package de.lgohlke.homebanking;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Clock;
import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.WebError;
import com.microsoft.playwright.options.BindingCallback;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.FunctionCallback;
import com.microsoft.playwright.options.Geolocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class PersistentBrowserContextFactory {
    private final Browser browser;
    private final Path storageStateDir;

    public BrowserContext newBrowserContext() throws IOException {
        Path storageFile = retrieveStorageState();
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        if (!isEmptyFile(storageFile)) {
            options.setStorageStatePath(storageFile);
        }

        BrowserContext context = this.browser.newContext(options);

        return extendCloseMethodOfBrowserContext(context, storageFile);
    }

    private static BrowserContext extendCloseMethodOfBrowserContext(BrowserContext context, Path storageFile) {
        return new BrowserContext() {

            @Override
            public void onBackgroundPage(Consumer<Page> handler) {
                context.onBackgroundPage(handler);
            }

            @Override
            public void offBackgroundPage(Consumer<Page> handler) {
                context.offBackgroundPage(handler);
            }

            @Override
            public void onClose(Consumer<BrowserContext> handler) {
                context.onClose(handler);
            }

            @Override
            public void offClose(Consumer<BrowserContext> handler) {
                context.offClose(handler);
            }

            @Override
            public void onConsoleMessage(Consumer<ConsoleMessage> handler) {
                context.onConsoleMessage(handler);
            }

            @Override
            public void offConsoleMessage(Consumer<ConsoleMessage> handler) {
                context.offConsoleMessage(handler);
            }

            @Override
            public void onDialog(Consumer<Dialog> handler) {
                context.onDialog(handler);
            }

            @Override
            public void offDialog(Consumer<Dialog> handler) {
                context.offDialog(handler);
            }

            @Override
            public void onPage(Consumer<Page> handler) {
                context.onPage(handler);
            }

            @Override
            public void offPage(Consumer<Page> handler) {
                context.offPage(handler);
            }

            @Override
            public void onWebError(Consumer<WebError> handler) {
                context.onWebError(handler);
            }

            @Override
            public void offWebError(Consumer<WebError> handler) {
                context.offWebError(handler);
            }

            @Override
            public void onRequest(Consumer<Request> handler) {
                context.onRequest(handler);
            }

            @Override
            public void offRequest(Consumer<Request> handler) {
                context.offRequest(handler);
            }

            @Override
            public void onRequestFailed(Consumer<Request> handler) {
                context.onRequestFailed(handler);
            }

            @Override
            public void offRequestFailed(Consumer<Request> handler) {
                context.offRequestFailed(handler);
            }

            @Override
            public void onRequestFinished(Consumer<Request> handler) {
                context.onRequestFinished(handler);
            }

            @Override
            public void offRequestFinished(Consumer<Request> handler) {
                context.offRequestFinished(handler);
            }

            @Override
            public void onResponse(Consumer<Response> handler) {
                context.onResponse(handler);
            }

            @Override
            public void offResponse(Consumer<Response> handler) {
                context.offResponse(handler);
            }

            @Override
            public Clock clock() {
                return context.clock();
            }

            @Override
            public void addCookies(List<Cookie> cookies) {
                context.addCookies(cookies);
            }

            @Override
            public void addInitScript(String script) {
                context.addInitScript(script);
            }

            @Override
            public void addInitScript(Path script) {
                context.addInitScript(script);
            }

            @Override
            public List<Page> backgroundPages() {
                return context.backgroundPages();
            }

            @Override
            public Browser browser() {
                return context.browser();
            }

            @Override
            public void clearCookies(ClearCookiesOptions options) {
                context.clearCookies(options);
            }

            @Override
            public void clearPermissions() {
                context.clearPermissions();
            }

            @Override
            public void close(CloseOptions options) {
                saveState(context, storageFile);
                context.close(options);
            }

            @Override
            public List<Cookie> cookies(String urls) {
                return context.cookies(urls);
            }

            @Override
            public List<Cookie> cookies(List<String> urls) {
                return context.cookies(urls);
            }

            @Override
            public void exposeBinding(String name, BindingCallback callback, ExposeBindingOptions options) {
                context.exposeBinding(name, callback, options);
            }

            @Override
            public void exposeFunction(String name, FunctionCallback callback) {
                context.exposeFunction(name, callback);
            }

            @Override
            public void grantPermissions(List<String> permissions, GrantPermissionsOptions options) {
                context.grantPermissions(permissions, options);
            }

            @Override
            public CDPSession newCDPSession(Page page) {
                return context.newCDPSession(page);
            }

            @Override
            public CDPSession newCDPSession(Frame page) {
                return context.newCDPSession(page);
            }

            @Override
            public Page newPage() {
                return context.newPage();
            }

            @Override
            public List<Page> pages() {
                return context.pages();
            }

            @Override
            public APIRequestContext request() {
                return context.request();
            }

            @Override
            public void route(String url, Consumer<Route> handler, RouteOptions options) {
                context.route(url, handler, options);
            }

            @Override
            public void route(Pattern url, Consumer<Route> handler, RouteOptions options) {
                context.route(url, handler, options);
            }

            @Override
            public void route(Predicate<String> url, Consumer<Route> handler, RouteOptions options) {
                context.route(url, handler, options);
            }

            @Override
            public void routeFromHAR(Path har, RouteFromHAROptions options) {
                context.routeFromHAR(har, options);
            }

            @Override
            public void setDefaultNavigationTimeout(double timeout) {
                context.setDefaultNavigationTimeout(timeout);
            }

            @Override
            public void setDefaultTimeout(double timeout) {
                context.setDefaultTimeout(timeout);
            }

            @Override
            public void setExtraHTTPHeaders(Map<String, String> headers) {
                context.setExtraHTTPHeaders(headers);
            }

            @Override
            public void setGeolocation(Geolocation geolocation) {
                context.setGeolocation(geolocation);
            }

            @Override
            public void setOffline(boolean offline) {
                context.setOffline(offline);
            }

            @Override
            public String storageState(StorageStateOptions options) {
                return context.storageState(options);
            }

            @Override
            public Tracing tracing() {
                return context.tracing();
            }

            @Override
            public void unrouteAll() {
                context.unrouteAll();
            }

            @Override
            public void unroute(String url, Consumer<Route> handler) {
                context.unroute(url, handler);
            }

            @Override
            public void unroute(Pattern url, Consumer<Route> handler) {
                context.unroute(url, handler);
            }

            @Override
            public void unroute(Predicate<String> url, Consumer<Route> handler) {
                context.unroute(url, handler);
            }

            @Override
            public void waitForCondition(BooleanSupplier condition, WaitForConditionOptions options) {
                context.waitForCondition(condition, options);
            }

            @Override
            public ConsoleMessage waitForConsoleMessage(WaitForConsoleMessageOptions options, Runnable callback) {
                return context.waitForConsoleMessage(options, callback);
            }

            @Override
            public Page waitForPage(WaitForPageOptions options, Runnable callback) {
                return context.waitForPage(options, callback);
            }
        };
    }

    private static boolean isEmptyFile(Path storage) {
        return storage.toFile()
                      .length() == 0;
    }

    private static void saveState(BrowserContext context, Path storageFile) {
        var storageStateOptions = new BrowserContext.StorageStateOptions().setPath(storageFile);

        log.info("saves state");
        context.storageState(storageStateOptions);
    }

    private Path retrieveStorageState() throws IOException {
        Files.createDirectories(this.storageStateDir); // in case dirs are missing
        return this.storageStateDir.resolve("state.json");
    }
}
