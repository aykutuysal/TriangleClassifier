package com.aykutuysal.triangleclassifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    void printsPackagedHelp() throws Exception {
        final Result result = runJar(Map.of(), "--help");
        assertEquals(0, result.exitCode());
        assertEquals(AppInfo.help() + System.lineSeparator(), result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void printsVersionFromTheBuild() throws Exception {
        final Result result = runJar(Map.of(), "--version");
        assertEquals(0, result.exitCode());
        assertEquals("triangle-classifier " + System.getProperty("expectedVersion") + System.lineSeparator(),
                result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void reportsMissingVersionFile() throws Exception {
        final Path jar = copyJar();
        try (FileSystem contents = FileSystems.newFileSystem(jar)) {
            Files.delete(contents.getPath("/version.properties"));
        }

        final Result result = runJar(jar, Map.of(), "--version");
        assertEquals(1, result.exitCode());
        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Version file is missing: /version.properties"), result.stderr());
        assertTrue(result.stderr().contains("Unexpected failure for arguments: [--version]"), result.stderr());
    }

    @ParameterizedTest(name = "version.properties contains {0} -> version error")
    @EmptySource
    @ValueSource(strings = {"name=triangle-classifier", "version=", "version=   "})
    void reportsMissingOrBlankVersion(final String properties) throws Exception {
        final Path jar = copyJar();
        try (FileSystem contents = FileSystems.newFileSystem(jar)) {
            Files.writeString(contents.getPath("/version.properties"), properties);
        }

        final Result result = runJar(jar, Map.of(), "--version");
        assertEquals(1, result.exitCode());
        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Version is missing or blank in /version.properties"), result.stderr());
    }

    @ParameterizedTest(name = "LOG_LEVEL={0} -> ignored")
    @ValueSource(strings = {"WARNING", ""})
    void ignoresGenericLogLevel(final String value) throws Exception {
        final Result result = runJar(Map.of("LOG_LEVEL", value), "3", "4", "5");
        assertEquals(0, result.exitCode());
        assertEquals(SCALENE, result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void debugLogsGoToStderr() throws Exception {
        final Result result = runJar(Map.of("TRIANGLE_LOG_LEVEL", "DEBUG"), "3", "4", "5");
        assertEquals(0, result.exitCode());
        assertEquals(SCALENE, result.stdout());
        assertTrue(result.stderr().contains("Classified Triangle[sideA=3, sideB=4, sideC=5] as SCALENE"), result.stderr());
    }

    @Test
    void debugLogsShowRejectedArgumentsAndCause() throws Exception {
        final Result result = runJar(Map.of("TRIANGLE_LOG_LEVEL", "DEBUG"), "3", "abc", "5");
        assertEquals(2, result.exitCode());
        assertEquals("", result.stdout());
        assertTrue(result.stderr().contains("Side length must be a number: \"abc\""), result.stderr());
        assertTrue(result.stderr().contains("[3, abc, 5]"), result.stderr());
        assertTrue(result.stderr().contains("Caused by: java.lang.NumberFormatException"), result.stderr());
    }

    private Result runJar(final Map<String, String> environment, final String... args)
            throws IOException, InterruptedException {
        return runJar(Path.of(System.getProperty("packagedJar")), environment, args);
    }

    private Path copyJar() throws IOException {
        return Files.copy(Path.of(System.getProperty("packagedJar")), temporaryDirectory.resolve("test.jar"));
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
