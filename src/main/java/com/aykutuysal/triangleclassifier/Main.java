package com.aykutuysal.triangleclassifier;

import com.aykutuysal.triangleclassifier.domain.InvalidTriangleException;
import com.aykutuysal.triangleclassifier.domain.Triangle;
import com.aykutuysal.triangleclassifier.domain.TriangleClassifier;
import com.aykutuysal.triangleclassifier.domain.TriangleType;
import com.aykutuysal.triangleclassifier.input.InvalidInputException;
import com.aykutuysal.triangleclassifier.input.TriangleParser;

import java.io.PrintStream;
import java.util.List;

/**
 * Command-line entry point for TriangleClassifier.
 *
 * <p>Classifies a triangle as Equilateral, Isosceles or Scalene from its side lengths.
 *
 * <pre>{@code
 * java -jar triangle-classifier.jar 3 4 5
 * }</pre>
 */
public final class Main {

    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_INVALID_INPUT = 2;

    private Main() {}

    /**
     * Runs the program and exits with its exit code.
     */
    public static void main(final String[] args) {
        final int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Classifies a triangle and returns the exit code.
     * Takes the streams as parameters so tests can call it directly.
     */
    static int run(final String[] args, final PrintStream out, final PrintStream err) {
        try {
            final String output = classify(args);
            out.println(output);
            return EXIT_SUCCESS;
        } catch (final InvalidInputException | InvalidTriangleException e) {
            err.println(e.getMessage());
            return EXIT_INVALID_INPUT;
        }
    }

    /**
     * Parses the sides and returns the triangle's type.
     */
    private static String classify(final String[] args)
            throws InvalidInputException, InvalidTriangleException {

        final Triangle triangle = TriangleParser.parse(List.of(args));
        final TriangleType type = TriangleClassifier.classify(triangle);

        return type.label();
    }
}
