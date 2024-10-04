package de.lgohlke.homebanking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountStatusTest {
    private final String IBAN = "DE75 5001 0517 2221 8623 18";

    @Test
    void test_account_status() {
        var status = AccountStatus.parse("2024-08-27", IBAN, "2.123,54 €", "Tagesgeld - Otto");

        assertThat(status.iban()
                         .toPlainString()).isEqualTo("DE75500105172221862318");
        assertThat(status.balanceAsStr()).isEqualTo("2123,54");
        assertThat(status.name()).isEqualTo("Tagesgeld - Otto");
    }

    @Test
    void account_should_parse_stringdate() {
        var status = AccountStatus.parse("2024-08-27", IBAN, "2.123,54 €", "X");

        assertThat(status.date()).isEqualTo("2024-08-27");
    }

    @Test
    void test_account_status_with_zeros() {
        var status = AccountStatus.parse("2024-08-27", IBAN, "2.123,00 €", "Tagesgeld - Otto");

        assertThat(status.balanceAsStr()).isEqualTo("2123,00");
    }

    @Test
    void fail_with_empty_balance() {
        assertThatThrownBy(() -> AccountStatus.parse(IBAN, "", "test")
                , "just blank str: ''"
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
