package de.lgohlke.homebanking.institutes;

import com.microsoft.playwright.Page;
import de.lgohlke.homebanking.AccountStatus;

import java.util.List;

public interface InstitutePage {
    void open();

    void login();

    List<AccountStatus> fetchAccountData();

    Page getPage();
}
