package de.lgohlke.homebanking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStatusFileWriterTest {
    @Test
    void should_write_account_statuses(@TempDir Path tempdir) throws IOException {

        Date date = Date.valueOf(LocalDate.of(2024, 8, 27));
        List<AccountStatus> statuses = List.of(
                AccountStatus.parse(date, "DE75 5001 0517 2221 8623 18", "2123,54 €", "Giro1"),
                AccountStatus.parse(date, "DE88 5001 0517 2235 7114 53", "5123,54 €", "Giro2")
        );

        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(tempdir);
        accountStatusCSVWriter.writeStatusesToCSV(statuses);

        assertThat(tempdir.toFile()
                          .list()).hasSize(2);

        Path directory1 = tempdir.resolve(Paths.get("DE75_5001_0517_2221_8623_18"));
        assertThat(directory1).isDirectory();
        String[] listing = directory1.toFile()
                                     .list();
        assertThat(listing).hasSize(1);

        Path csv1 = directory1.resolve(Paths.get(listing[0]));
        String content1 = Files.readString(csv1);
        assertThat(content1).isEqualTo("""
                                               Date|IBAN|Balance|Name
                                               2024-08-27|DE75 5001 0517 2221 8623 18|2123,54|Giro1
                                               """);
    }
}
