package de.lgohlke.homebanking.sops;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Slf4j
public class SopsDecryptor {
    private static final String BIN_SOPS = "/home/linuxbrew/.linuxbrew/bin/sops";
    private final String filepath;

    @Getter
    private String error = "";

    public String getContent() {
        val error = new StringBuilder();
        try {
            val content = new StringBuilder();
            decrypt(content, error);
            return content.toString();
        } finally {
            this.error = error.toString();
        }
    }

    @SneakyThrows
    private void decrypt(StringBuilder stdout, StringBuilder stderr) {

        var absolute_path_to_file = retrieveAbsoluteFilePath(filepath);
        log.info("reading from {}", absolute_path_to_file);
        checkFile(Paths.get(absolute_path_to_file));

        ProcessBuilder processBuilder = new ProcessBuilder(BIN_SOPS, "--verbose", "--decrypt", absolute_path_to_file);
        Process process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            try (var readerE = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stdout.append(line)
                          .append("\n");
                }

                while ((line = readerE.readLine()) != null) {
                    log.error(line);
                    stderr.append(line)
                          .append("\n");
                }
            }
        }

        // Warte auf das Ende des Prozesses
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("sops Kommando ist fehlgeschlagen.");
        }
    }

    private static void checkFile(Path path) throws FileNotFoundException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException("missing file:" + path);

        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("not a file: " + path);
        }
    }

    private String retrieveAbsoluteFilePath(String filepath) {
        if (filepath.startsWith("/")) {
            return filepath;
        }

        var currentWorkingDirectory = System.getenv("PWD");
        return currentWorkingDirectory + "/" + filepath;
    }
}
