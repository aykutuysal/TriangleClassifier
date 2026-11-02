package com.aykutuysal.triangleclassifier;

/**
 * Provides the application's usage and help text.
 * {@link Main} prints the text and chooses the exit code.
 */
public final class AppInfo {

    public static final String USAGE = "Usage: java -jar triangle-classifier.jar <side-a> <side-b> <side-c>";

    private static final String HELP_TEMPLATE = """
            Classifies a triangle as Equilateral, Isosceles or Scalene from its side lengths.

            %s

            Options:
              -h, --help  Show this help and exit

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
}
