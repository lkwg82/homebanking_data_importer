package de.lgohlke.homebanking;

import de.lgohlke.homebanking.institutes.dkb.DKBDataRetriever;
import de.lgohlke.homebanking.institutes.quirion.QuirionDataRetriever;

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
        List<DataFromBankRetriever> retrievers = List.of(new DKBDataRetriever(dataDir.resolve("dkb")),
                                                         new QuirionDataRetriever(dataDir.resolve("quirion")));

        retrievers.forEach(DataFromBankRetriever::fetchData);
        System.out.println(dataDir);
    }
}
