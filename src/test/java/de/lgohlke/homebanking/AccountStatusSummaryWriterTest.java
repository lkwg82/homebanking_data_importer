package de.lgohlke.homebanking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStatusSummaryWriterTest {
    @Test
    void name(@TempDir Path tempdir) throws IOException {
        List<AccountStatus> statuses = List.of(
                AccountStatus.parse("2024-08-27", "DE75 5001 0517 2221 8623 18", "2123,54 €", "Giro1"),
                AccountStatus.parse("2024-08-28", "DE75 5001 0517 2221 8623 18", "2123,54 €", "Giro1"),
                AccountStatus.parse("2024-08-29", "DE75 5001 0517 2221 8623 18", "12123,55 €", "Giro1"),
                AccountStatus.parse("2024-08-29", "DE75 5001 0517 2221 8623 18", "12123,55 €", "Giro1"),
                AccountStatus.parse("2024-08-29", "DE75 5001 0517 2221 8623 18", "2123,55 €", "Giro1"),
                AccountStatus.parse("2024-08-27", "DE88 5001 0517 2235 7114 53", "5153,54 €", "Giro2")
        );

        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(tempdir);
        accountStatusCSVWriter.writeStatusesToCSV(statuses);

        AccountStatusSummaryWriter summaryWriter = new AccountStatusSummaryWriter(tempdir);
        summaryWriter.writeSummaryToCSV();

        Path summary = tempdir.resolve("summary.csv");
        assertThat(summary).isNotEmptyFile();

        List<String> lines = Files.readAllLines(summary);
        assertThat(lines).hasSize(4);
    }

}
