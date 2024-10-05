package de.lgohlke.homebanking;

import com.microsoft.playwright.Browser;
import de.lgohlke.homebanking.institutes.dkb.DKBDataRetriever;
import de.lgohlke.homebanking.institutes.quirion.QuirionDataRetriever;
import de.lgohlke.homebanking.institutes.scalablecapital.ScalableCapitalDataRetriever;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainDataRetriever {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("please pass path as argument");
            System.exit(1);
        }
        Path path = Paths.get(args[0]);
        new MainDataRetriever().execute(path);
    }

    void execute(Path dataDir) {
        // tag::list_of_institutes[]
        List<DataFromBankRetriever> retrievers = List.of(
                new DKBDataRetriever(dataDir.resolve("dkb")),
                new QuirionDataRetriever(dataDir.resolve("quirion")),
                new ScalableCapitalDataRetriever(dataDir.resolve("scalablecapital"))
//                new TradeRepublicDataRetriever(dataDir.resolve("traderepublic"))
        );
        // end::list_of_institutes[]
        try (Browser browser = BrowserLauncher.createChromium()) {
            retrievers.forEach(retriever -> {
                retriever.fetchData(browser);
            });
            new AccountStatusSummaryWriter(dataDir).writeSummaryToCSV();
            System.out.println(dataDir);
        }

        System.out.println("°°° Fertig °°°");
    }
}
