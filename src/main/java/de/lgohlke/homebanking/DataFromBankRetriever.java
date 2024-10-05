package de.lgohlke.homebanking;

import com.microsoft.playwright.Browser;
import org.apache.commons.lang3.NotImplementedException;

public interface DataFromBankRetriever {
    default void fetchData(Browser browser) {
        throw new NotImplementedException();
    }
}
