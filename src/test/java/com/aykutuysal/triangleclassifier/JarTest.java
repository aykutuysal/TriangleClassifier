package com.aykutuysal.triangleclassifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class JarTest {

    private static final String SCALENE = "Scalene" + System.lineSeparator();
    private static final int PROCESS_TIMEOUT_SECONDS = 10;

    @TempDir
    Path temporaryDirectory;

    @Test
    void normalRunLogsNothing() throws Exception {
        final Result result = runJar(Map.of(), "3", "4", "5");
        assertEquals(0, result.exitCode());
        assertEquals(SCALENE, result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void rejectedInputPrintsOnlyTheReason() throws Exception {
        final Result result = runJar(Map.of(), "3", "abc", "5");
        assertEquals(2, result.exitCode());
        assertEquals("", result.stdout());
        assertEquals("Side length must be a number: \"abc\"" + System.lineSeparator(), result.stderr());
    }

    private Result runJar(final Map<String, String> environment, final String... args)
            throws IOException, InterruptedException {
        return runJar(Path.of(System.getProperty("packagedJar")), environment, args);
    }

    private Result runJar(final Path jar, final Map<String, String> environment, final String... args)
            throws IOException, InterruptedException {
        final String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        final List<String> command = new ArrayList<>(List.of(java, "-jar", jar.toString()));
        command.addAll(List.of(args));

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.environment().remove("LOG_LEVEL");
        builder.environment().remove("TRIANGLE_LOG_LEVEL");
        builder.environment().putAll(environment);

        // Files keep either output stream from filling a pipe and blocking the process.
        final Path stdout = Files.createTempFile(temporaryDirectory, "stdout-", ".txt");
        final Path stderr = Files.createTempFile(temporaryDirectory, "stderr-", ".txt");
        builder.redirectOutput(stdout.toFile());
        builder.redirectError(stderr.toFile());

        final Process process = builder.start();
        try {
            assertTrue(process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    "JAR did not exit within " + PROCESS_TIMEOUT_SECONDS + " seconds: " + command);
            return new Result(process.exitValue(), Files.readString(stdout), Files.readString(stderr));
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly().waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
        }
    }

    private record Result(int exitCode, String stdout, String stderr) {
    }
}
