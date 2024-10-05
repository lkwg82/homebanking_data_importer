package de.lgohlke.homebanking;

import nl.garvelink.iban.IBAN;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStatusCSVConverterTest {
    private final AccountStatusCSVConverter converter = new AccountStatusCSVConverter();

    @Test
    void should_convertCSVFileLines_toSingleStatus() {
        List<String> lines = List.of(
                "Date|IBAN|Balance|Name",
                "2024-08-27|DE75 5001 0517 2221 8623 18|2,54|Giro2"
        );

        AccountStatus status = converter.convert(lines);

        assertThat(status.date()).isEqualTo("2024-08-27");
        assertThat(status.iban()).isEqualTo(IBAN.valueOf("DE75 5001 0517 2221 8623 18"));
        assertThat(status.balanceAsStr()).isEqualTo("2,54");
        assertThat(status.name()).isEqualTo("Giro2");
    }

    @Test
    void should_convertSingleToCSVFileString() {
        AccountStatus status = AccountStatus.parse("2024-08-27", "DE75 5001 0517 2221 8623 18", "2,54 €", "Giro1");
        List<String> lines = converter.convert(status);

        assertThat(lines).hasSize(2);
        assertThat(lines.getFirst()).isEqualTo("Date|IBAN|Balance|Name");
        assertThat(lines.get(1)).isEqualTo("2024-08-27|DE75 5001 0517 2221 8623 18|2,54|Giro1");
    }

    @Test
    void should_convertListToCSVString() {
        AccountStatus status1 = AccountStatus.parse("2024-08-27", "DE75 5001 0517 2221 8623 18", "2,54 €", "Giro1");
        AccountStatus status2 = AccountStatus.parse("2024-08-28", "DE75 5001 0517 2221 8623 18", "2,54 €", "Giro1");
        List<String> lines = converter.convert(List.of(status1, status2));

        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).isEqualTo("2024-08-27|DE75 5001 0517 2221 8623 18|2,54|Giro1");
        assertThat(lines.get(1)).isEqualTo("2024-08-28|DE75 5001 0517 2221 8623 18|2,54|Giro1");
    }
}
