package de.lgohlke.homebanking;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DataFromBankRetriever {
    void fetchData();

    Path dataDirectory();

    default Set<AccountStatus> collect() {
        FileFilter dirFilter = File::isDirectory;
        FileFilter csvFileFilter = pathname -> pathname.isFile() && pathname.getPath().endsWith(".csv");
        File[] dirs = dataDirectory().toFile().listFiles(dirFilter);
        return Stream.of(Objects.requireNonNull(dirs))
                     .map(dir -> dir.listFiles(csvFileFilter))
                     .filter(Objects::nonNull)
                     .flatMap(Arrays::stream)
                     .map(file -> {
                         try {
                             List<String> lines = Files.readAllLines(file.toPath());
                             if (lines.size() == 2) {
                                 return lines.get(1);
                             }
                             throw new IllegalStateException("should have two lines");
                         } catch (IOException e) {
                             throw new RuntimeException(e);
                         }
                     }).map(line -> {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        return AccountStatus.parse(Date.valueOf(parts[0]), parts[1], parts[2], parts[3]);
                    }
                    return null;
                })
                     .filter(Objects::nonNull)
                     .collect(Collectors.toSet());
    }
}
