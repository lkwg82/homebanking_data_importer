package de.lgohlke.homebanking;

import de.lgohlke.homebanking.institutes.dkb.DKBDataRetriever;
import de.lgohlke.homebanking.institutes.quirion.QuirionDataRetriever;
import de.lgohlke.homebanking.institutes.traderepublic.TradeRepublicDataRetriever;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
        List<DataFromBankRetriever> retrievers = List.of(new DKBDataRetriever(dataDir.resolve("dkb")),
                                                         new QuirionDataRetriever(dataDir.resolve("quirion")),
                                                         new TradeRepublicDataRetriever(dataDir.resolve("traderepublic"))
        );

        List<AccountStatus> statuses = retrievers.stream()
                                                 .map(retriever -> {
                                                     retriever.fetchData();
                                                     return retriever.collect();
                                                 })
                                                 .flatMap(Set::stream)
                                                 .toList();
        new AccountStatusCSVWriter(dataDir).writeSummaryToCSV(statuses);
        System.out.println(dataDir);
    }
}
