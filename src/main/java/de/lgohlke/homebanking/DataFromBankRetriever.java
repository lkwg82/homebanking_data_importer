package de.lgohlke.homebanking;

public interface DataFromBankRetriever {
    void fetchData();

    void collectAndWriteSummary();
}
