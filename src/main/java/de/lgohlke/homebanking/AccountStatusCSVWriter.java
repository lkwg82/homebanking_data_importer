package de.lgohlke.homebanking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class AccountStatusCSVWriter {
    public static final String SUMMARY_CSV = "summary.csv";
    private final Path dataDirectory;

    public void writeStatusesToCSV(List<AccountStatus> statuses) {
        statuses.forEach(this::writeSingleStatusToCSV);
    }

    public void writeSummaryToCSV(Collection<AccountStatus> statuses) {
        var statusLines = statuses.stream()
                                  .map(AccountStatusCSVWriter::convertAccountStatusToCSVLine)
                                  .collect(Collectors.joining("\n"));
        try {
            log.info("writing {}", SUMMARY_CSV);
            Files.writeString(dataDirectory.resolve(SUMMARY_CSV), statusLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeSingleStatusToCSV(AccountStatus status) {
        String iban = status.iban()
                            .toString()
                            .replace(" ", "_");
        Path folderPath = dataDirectory.resolve(Paths.get(iban));
        createDirectoryIfNotExists(folderPath);

        String fileName = createTimestampedFileName();
        Path filePath = folderPath.resolve(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write("Date|IBAN|Balance|Name");
            writer.newLine();

            writer.write(convertAccountStatusToCSVLine(status));
            writer.newLine();

            log.info("CSV-Datei erfolgreich erstellt: {}", filePath);

        } catch (IOException e) {
            log.error("Fehler beim Schreiben der Datei: {}", e.getMessage());
        }
    }

    private static String createTimestampedFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");
        String timestamp = now.format(dateFormatter);
        long nanoTime = System.nanoTime();
        return timestamp + "_" + nanoTime + ".csv";
    }

    private static void createDirectoryIfNotExists(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Ordner erstellt: {}", path);
            } catch (IOException e) {
                log.error("Fehler beim Erstellen des Ordners: {}", e.getMessage());
            }
        }
    }

    private static String convertAccountStatusToCSVLine(AccountStatus status) {
        return String.format("%s|%s|%s|%s",
                             status.date(),
                             status.iban(),
                             status.balanceAsStr(),
                             status.name()
        );
    }
}
