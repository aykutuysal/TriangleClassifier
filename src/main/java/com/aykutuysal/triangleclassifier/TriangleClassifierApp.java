package com.aykutuysal.triangleclassifier;

import com.aykutuysal.triangleclassifier.domain.InvalidTriangleException;
import com.aykutuysal.triangleclassifier.domain.Triangle;
import com.aykutuysal.triangleclassifier.domain.TriangleClassifier;
import com.aykutuysal.triangleclassifier.domain.TriangleType;
import com.aykutuysal.triangleclassifier.input.InvalidInputException;
import com.aykutuysal.triangleclassifier.input.TriangleParser;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command-line entry point for TriangleClassifier.
 *
 * <p>Classifies a triangle as Equilateral, Isosceles or Scalene from its side lengths.
 *
 * <pre>{@code
 * java -jar triangle-classifier.jar 3 4 5
 * java -jar triangle-classifier.jar --help (or -h)
 * java -jar triangle-classifier.jar --version (or -v)
 * }</pre>
 *
 * <p>{@link AppInfo#help()} lists the usage, options and exit codes.
 */
public final class TriangleClassifierApp {

    private static final Logger log = LoggerFactory.getLogger(TriangleClassifierApp.class);

    /**
     * Exit Codes
     * UNEXPECTED_FAILURE and INVALID_INPUT are kept separate so callers can differentiate
     * bad input, which they must fix, from a failure in the program itself.
     */
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_UNEXPECTED_FAILURE = 1;
    private static final int EXIT_INVALID_INPUT = 2;

    private final TriangleParser parser;
    private final TriangleClassifier classifier;

    TriangleClassifierApp(final TriangleParser parser, final TriangleClassifier classifier) {
        this.parser = parser;
        this.classifier = classifier;
    }

    /**
     * Runs the app and exits with its exit code.
     */
    public static void main(final String[] args) {
        final TriangleClassifierApp app =
                new TriangleClassifierApp(new TriangleParser(), new TriangleClassifier());
        final int exitCode = app.run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Handles an option (-h, -v etc.) or classifies a triangle, and returns the exit code.
     * Takes the streams as parameters so tests can call it directly.
     */
    int run(final String[] args, final PrintStream out, final PrintStream err) {

        log.debug("Received arguments: {}", Arrays.asList(args));

        try {
            final String output = isOption(args) ? handleOption(args[0]) : classify(args);
            out.println(output);
            return EXIT_SUCCESS;
        } catch (final InvalidInputException | InvalidTriangleException e) {
            log.debug("Rejected input", e);
            err.println(e.getMessage());
            return EXIT_INVALID_INPUT;
        } catch (final RuntimeException e) {
            log.error("Unexpected failure for arguments: {}", Arrays.asList(args), e);
            return EXIT_UNEXPECTED_FAILURE;
        }
    }

    /**
     * Parses the sides and returns the triangle's type.
     */
    private String classify(final String[] args)
            throws InvalidInputException, InvalidTriangleException {

        final Triangle triangle = parser.parse(List.of(args));
        final TriangleType type = classifier.classify(triangle);

        log.debug("Classified {} as {}", triangle, type);
        return type.label();
    }

    /**
     * Returns the help or version text, and rejects any other option.
     */
    private String handleOption(final String option)
            throws InvalidInputException {
        return switch (option) {
            case "-h", "--help" -> AppInfo.help();
            case "-v", "--version" -> AppInfo.version();
            default -> throw new InvalidInputException(AppInfo.USAGE);
        };
    }

    /**
     * Returns true if the only argument starts with a dash, like --help, -v
     */
    private boolean isOption(final String[] args) {
        return args.length == 1 && args[0].startsWith("-");
    }
}
