package de.lgohlke.homebanking.sops;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private static boolean isBinaryAvailable(String... command) {
        try {
            Process process = new ProcessBuilder(command).start();
            process.waitFor();  // Warte, bis der Prozess beendet ist
            return true;  // Wenn kein Fehler auftritt, existiert das Binary
        } catch (IOException | InterruptedException e) {
            return false;  // Wenn ein Fehler auftritt, ist das Binary nicht im Pfad
        }
    }

    @SneakyThrows
    private void decrypt(StringBuilder stdout, StringBuilder stderr) {

        var absolute_path_to_file = retrieveAbsoluteFilePath(filepath);
        log.info("reading from {}", absolute_path_to_file);
        checkFile(Paths.get(absolute_path_to_file));

        String sops_command = retrieveSopsCommand();
        ProcessBuilder processBuilder = new ProcessBuilder(sops_command,
                                                           "--verbose",
                                                           "--decrypt",
                                                           absolute_path_to_file);
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

    private String retrieveSopsCommand() {
        if (isBinaryAvailable("sops", "--disable-version-check", "--version")) {
            log.info("use sops from PATH");
            return "sops";
        }
        if (isBinaryAvailable(BIN_SOPS, "--disable-version-check", "--version")) {
            log.info("use sops from {}", BIN_SOPS);
            return BIN_SOPS;
        }
        throw new IllegalStateException("could not find sops executable");
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
