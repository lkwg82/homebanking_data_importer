package de.lgohlke.homebanking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStatusFileWriterTest {
    @RequiredArgsConstructor
    public class AccountStatusCSVWriter {
        private final Path dataDirectory;

        // Methode zum Schreiben der Liste in CSV-Dateien
        public void writeStatusesToCSV(List<AccountStatus> statuses) {
            for (AccountStatus status : statuses) {
                writeSingleStatusToCSV(status);
            }
        }

        // Methode zum Schreiben eines einzelnen AccountStatus in eine CSV-Datei
        private void writeSingleStatusToCSV(AccountStatus status) {
            // Erstelle den Ordner basierend auf der IBAN
            String iban = status.iban()
                                .toString()
                                .replace(" ", "");  // IBAN ohne Leerzeichen
            Path folderPath = dataDirectory.resolve(Paths.get(iban));
            createDirectoryIfNotExists(folderPath);

            String fileName = createTimestampedFileName();
            Path filePath = folderPath.resolve(fileName);

            // Schreibe die CSV-Datei
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                // Schreibe den Header
                writer.write("Date|IBAN|Balance|Name");
                writer.newLine();

                writer.write(convertAccountStatusToCSVLine(status));
                writer.newLine();

                System.out.println("CSV-Datei erfolgreich erstellt: " + filePath);

            } catch (IOException e) {
                System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
            }
        }

        // Erzeugt einen Dateinamen mit einem hierarchischen Timestamp-Format
        private static String createTimestampedFileName() {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
            String timestamp = now.format(dateFormatter);
            long nanoTime = System.nanoTime();
            return timestamp + "_" + nanoTime + ".csv";
        }

        // Hilfsmethode zum Erstellen des Ordners, falls er nicht existiert
        private static void createDirectoryIfNotExists(Path path) {
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                    System.out.println("Ordner erstellt: " + path);
                } catch (IOException e) {
                    System.err.println("Fehler beim Erstellen des Ordners: " + e.getMessage());
                }
            }
        }

        private static String convertAccountStatusToCSVLine(AccountStatus status) {
            return String.format("%s|%s|%s|%s",
                                 status.date(),
                                 status.iban(),
                                 status.balance(),
                                 status.name()
            );
        }
    }

    @Test
    void should_write_account_statuses(@TempDir Path tempdir) throws IOException {

        Date date = Date.valueOf(LocalDate.of(2024, 8, 27));
        List<AccountStatus> statuses = List.of(
                AccountStatus.parse(date, "DE75 5001 0517 2221 8623 18", "2.123,54 €", "Giro1"),
                AccountStatus.parse(date, "DE88 5001 0517 2235 7114 53", "5.123,54 €", "Giro2")
        );

        AccountStatusCSVWriter accountStatusCSVWriter = new AccountStatusCSVWriter(tempdir);
        accountStatusCSVWriter.writeStatusesToCSV(statuses);

        Path directory1 = tempdir.resolve(Paths.get("DE75500105172221862318"));
        assertThat(directory1).isDirectory();
        String[] listing = directory1.toFile()
                                     .list();
        assertThat(listing).hasSize(1);

        Path csv1 = directory1.resolve(Paths.get(listing[0]));
        String content1 = Files.readString(csv1);
        assertThat(content1).isEqualTo("""
                                               Date|IBAN|Balance|Name
                                               2024-08-27|DE75 5001 0517 2221 8623 18|2123.54|Giro1
                                               """);
    }
}
