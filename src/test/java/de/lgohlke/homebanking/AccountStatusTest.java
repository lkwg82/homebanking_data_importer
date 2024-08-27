package de.lgohlke.homebanking;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStatusTest {
    @Test
    void test_account_status() throws ParseException {
        Date date = Date.valueOf(LocalDate.of(2024, 8, 27));

        AccountStatus status = AccountStatus.parse(date, "DE75 5001 0517 2221 8623 18", "2.123,54 €",
                                                   "Tagesgeld - Otto");

        assertThat(status.iban()
                         .toPlainString()).isEqualTo("DE75500105172221862318");
        assertThat(status.balance()).isEqualTo("2123.54");
        assertThat(status.name()).isEqualTo("Tagesgeld - Otto");
    }
}
