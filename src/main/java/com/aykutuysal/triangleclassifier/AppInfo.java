package com.aykutuysal.triangleclassifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Provides the application's usage, help and version text.
 * {@link Main} prints the text and chooses the exit code.
 */
public final class AppInfo {

    public static final String USAGE = "Usage: java -jar triangle-classifier.jar <side-a> <side-b> <side-c>";

    private static final String NAME = "triangle-classifier";

    // Gradle writes the version from build.gradle.kts into this file when building the application.
    private static final String VERSION_FILE = "/version.properties";
    private static final String HELP_TEMPLATE = """
            Classifies a triangle as Equilateral, Isosceles or Scalene from its side lengths.

            %s

            Options:
              -h, --help     Show this help and exit
              -v, --version  Show the version and exit

            Exit codes:
              0  Success
              1  Unexpected failure
              2  Invalid input""";

    private AppInfo() {
    }

    /**
     * Returns the usage, available options and exit codes.
     */
    public static String help() {
        return HELP_TEMPLATE.formatted(USAGE);
    }

    /**
     * Returns the application name and version, for example {@code triangle-classifier 1.0.0}.
     *
     * @throws IllegalStateException if the version file is missing or its version is missing or blank
     * @throws UncheckedIOException if the version file cannot be read
     */
    public static String version() {
        return NAME + " " + readVersion();
    }

    private static String readVersion() {
        try (InputStream resource = AppInfo.class.getResourceAsStream(VERSION_FILE)) {
            if (resource == null) {
                throw new IllegalStateException("Version file is missing: " + VERSION_FILE);
            }

            final Properties properties = new Properties();
            properties.load(resource);
            final String version = properties.getProperty("version");
            if (version == null || version.isBlank()) {
                throw new IllegalStateException("Version is missing or blank in " + VERSION_FILE);
            }
            return version;
        } catch (final IOException e) {
            throw new UncheckedIOException("Cannot read version file: " + VERSION_FILE, e);
        }
    }
}
