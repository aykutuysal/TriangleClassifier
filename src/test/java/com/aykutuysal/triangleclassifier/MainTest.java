package com.aykutuysal.triangleclassifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

final class MainTest {

    private static final String NEWLINE = System.lineSeparator();

    @ParameterizedTest(name = "{0}, {1}, {2} -> {3}")
    @CsvSource({
            "3, 3, 3, Equilateral",
            "3, 3, 4, Isosceles",
            "3, 4, 5, Scalene"
    })
    void printsTheType(final String sideA, final String sideB, final String sideC, final String word) {
        final Result result = run(sideA, sideB, sideC);
        assertEquals(0, result.exitCode());
        assertEquals(word + NEWLINE, result.stdout());
        assertEquals("", result.stderr());
    }

    @ParameterizedTest(name = "{0} -> prints help")
    @ValueSource(strings = {"-h", "--help"})
    void printsHelpOnStdout(final String option) {
        final Result result = run(option);
        assertEquals(0, result.exitCode());
        assertTrue(result.stdout().contains(AppInfo.USAGE));
        assertTrue(result.stdout().contains("-h, --help"));
        assertTrue(result.stdout().contains("-v, --version"));
        assertTrue(result.stdout().contains("0  Success"));
        assertTrue(result.stdout().contains("1  Unexpected failure"));
        assertTrue(result.stdout().contains("2  Invalid input"));
        assertEquals("", result.stderr());
    }

    @ParameterizedTest(name = "{0} -> prints the version")
    @ValueSource(strings = {"-v", "--version"})
    void printsTheVersionOnStdout(final String option) {
        final Result result = run(option);
        assertEquals(0, result.exitCode());
        assertEquals("triangle-classifier " + System.getProperty("expectedVersion") + NEWLINE, result.stdout());
        assertEquals("", result.stderr());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejectedInputs")
    void reportsRejectedInputOnStderr(final String[] args, final String expectedError) {
        final Result result = run(args);
        assertEquals(2, result.exitCode());
        assertEquals("", result.stdout());
        assertEquals(expectedError + NEWLINE, result.stderr());
    }

    @Test
    void reportsTooManyDecimalPlacesAsInvalidInput() {
        final String side = "1." + "0".repeat(1000) + "1";
        final Result result = run(side, "1", "1");
        assertEquals(2, result.exitCode());
        assertEquals("", result.stdout());
        assertEquals("Side length must have at most 1000 decimal places: " + side + NEWLINE, result.stderr());
    }

    @Test
    void returnsOneOnUnexpectedFailure() {
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        assertEquals(1, Main.run(new String[] {"3", "4", "5"}, failingStream(), new PrintStream(err)));
        assertEquals(0, err.size());
    }

    private static Stream<Arguments> rejectedInputs() {
        return Stream.of(
                arguments(named("no sides", new String[] {}),
                        "Usage: java -jar triangle-classifier.jar <side-a> <side-b> <side-c>"),
                arguments(named("two sides", new String[] {"3", "4"}), AppInfo.USAGE),
                arguments(named("four sides", new String[] {"3", "4", "5", "6"}), AppInfo.USAGE),
                arguments(named("three sides in one argument", new String[] {"3 4 5"}), AppInfo.USAGE),
                arguments(named("help combined with a side", new String[] {"--help", "3"}), AppInfo.USAGE),
                arguments(named("unknown option", new String[] {"--unknown"}), AppInfo.USAGE),
                arguments(named("version combined with a side", new String[] {"-v", "3"}), AppInfo.USAGE),
                arguments(named("option used as a side", new String[] {"3", "4", "--help"}),
                        "Side length must be a number: \"--help\""),
                arguments(named("non-numeric side", new String[] {"3", "abc", "5"}),
                        "Side length must be a number: \"abc\""),
                arguments(named("comma decimals", new String[] {"1,2", "2,2", "3,2"}),
                        "Side length must use a dot for decimals (for example, 1.2): \"1,2\""),
                arguments(named("sides cannot meet", new String[] {"1", "2", "10"}),
                        "Not a triangle: the longest side (10) must be shorter than the other two combined"),
                arguments(named("negative first side", new String[] {"-3", "4", "5"}),
                        "Side length must be positive: -3"),
                arguments(named("side above the range", new String[] {"1e999999999", "1", "1"}),
                        "Side length is outside the supported range: 1E+999999999"),
                arguments(named("side below the range", new String[] {"1", "1", "1e-999999999"}),
                        "Side length is outside the supported range: 1E-999999999"));
    }

    private static PrintStream failingStream() {
        return new PrintStream(OutputStream.nullOutputStream()) {
            @Override
            public void println(final String text) {
                throw new IllegalStateException("Simulated failure");
            }
        };
    }

    private static Result run(final String... args) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final int exitCode = Main.run(args, new PrintStream(out), new PrintStream(err));
        return new Result(exitCode, out.toString(), err.toString());
    }

    private record Result(int exitCode, String stdout, String stderr) {
    }
}
