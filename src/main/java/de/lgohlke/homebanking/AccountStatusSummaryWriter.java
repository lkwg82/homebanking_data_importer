package de.lgohlke.homebanking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class AccountStatusSummaryWriter {
    private static final String SUMMARY_CSV = "summary.csv";
    private final Path baseDir;

    @SneakyThrows
    public void writeSummaryToCSV() {
        Collection<AccountStatus> statuses = collectStatuses();
        AccountStatusCSVConverter converter = new AccountStatusCSVConverter();
        List<String> lines = converter.convert(statuses);
        var statusLines = String.join("\n", lines);
        try {
            log.info("writing {}", SUMMARY_CSV);
            Files.writeString(baseDir.resolve(SUMMARY_CSV), statusLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<AccountStatus> collectStatuses() throws IOException {
        List<AccountStatus> accountStatuses = new ArrayList<>();
        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && file.toString().endsWith(".csv")) {
                    List<String> lines = Files.readAllLines(file);
                    var status = new AccountStatusCSVConverter().convert(lines);
                    accountStatuses.add(status);
                }
                return super.visitFile(file, attrs);
            }
        };
        Files.walkFileTree(baseDir, visitor);

        return keepOneStatusPerDateAndIBAN(accountStatuses);
    }

    private static List<AccountStatus> keepOneStatusPerDateAndIBAN(List<AccountStatus> accountStatuses) {
        return accountStatuses
                .stream()
                .map(status -> {
                    String key = status.date() + "_" + status.iban();
                    return Map.entry(key,
                                     status);
                })
                .collect(Collectors.groupingBy(Map.Entry::getKey))
                .values()
                .stream()
                .map(List::getFirst)
                .map(Map.Entry::getValue)
                .toList();
    }
}
