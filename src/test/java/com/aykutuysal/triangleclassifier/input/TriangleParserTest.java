package com.aykutuysal.triangleclassifier.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.aykutuysal.triangleclassifier.AppInfo;
import com.aykutuysal.triangleclassifier.domain.InvalidTriangleException;
import com.aykutuysal.triangleclassifier.domain.Triangle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

final class TriangleParserTest {

    @Test
    void parsesThreeNumbers() {
        final Triangle expected = new Triangle(new BigDecimal("3"), new BigDecimal("4"), new BigDecimal("5"));
        assertEquals(expected, TriangleParser.parse(List.of("3", "4", "5")));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "+3, 3",
            ".5, 0.5",
            "5., 5",
            "1e2, 100",
            "1.0000000000000001, 1.0000000000000001"
    })
    void parsesSupportedNumberFormats(final String text, final BigDecimal expected) {
        final Triangle triangle = TriangleParser.parse(List.of(text, text, text));

        // compareTo checks the numeric value, so 1e2 and 100 are equal.
        assertEquals(0, triangle.sideA().compareTo(expected), "side A");
        assertEquals(0, triangle.sideB().compareTo(expected), "side B");
        assertEquals(0, triangle.sideC().compareTo(expected), "side C");
    }

    @ParameterizedTest(name = "{0} -> wrong argument count")
    @MethodSource("wrongArgumentCounts")
    void rejectsWrongArgumentCount(final List<String> sides) {
        final var failure = assertThrows(InvalidInputException.class, () -> TriangleParser.parse(sides));
        assertEquals(AppInfo.USAGE, failure.getMessage());
    }

    @Test
    void rejectsNullList() {
        final var failure = assertThrows(InvalidInputException.class, () -> TriangleParser.parse(null));
        assertEquals(AppInfo.USAGE, failure.getMessage());
    }

    @ParameterizedTest(name = "null at index {0} -> rejected")
    @ValueSource(ints = {0, 1, 2})
    void rejectsNullSide(final int position) {
        final List<String> sides = new ArrayList<>(List.of("3", "4", "5"));
        sides.set(position, null);
        final var failure = assertThrows(InvalidInputException.class, () -> TriangleParser.parse(sides));
        assertEquals("Side length must not be null", failure.getMessage());
    }

    @ParameterizedTest(name = "{0} -> not a number")
    @ValueSource(strings = {
            "abc", "NaN", "Infinity", "", " 3", "3 ", "1d", "1e", "1e+",
            "1,2,3", "1,234.5", "abc,def", "5,"
    })
    void rejectsNonNumbers(final String side) {
        final List<String> sides = List.of("3", side, "5");
        final var failure = assertThrows(InvalidInputException.class, () -> TriangleParser.parse(sides));
        assertEquals("Side length must be a number: \"" + side + "\"", failure.getMessage());
        assertInstanceOf(NumberFormatException.class, failure.getCause());
    }

    @ParameterizedTest(name = "{0} -> comma instead of a dot")
    @ValueSource(strings = {"1,2", "3,5", "10,25", ",5", "-1,2", "+1,2"})
    void explainsCommaDecimals(final String side) {
        final List<String> sides = List.of("3", side, "5");
        final var failure = assertThrows(InvalidInputException.class, () -> TriangleParser.parse(sides));
        assertEquals(
                "Side length must use a dot for decimals (for example, 1.2): \"" + side + "\"", failure.getMessage());
        assertInstanceOf(NumberFormatException.class, failure.getCause());
    }

    @ParameterizedTest(name = "{0}, {1}, {2} -> error names abc")
    @CsvSource({
            "abc, 4, 5",
            "3, abc, 5",
            "3, 4, abc",
    })
    void namesTheBadSide(final String sideA, final String sideB, final String sideC) {
        final List<String> sides = List.of(sideA, sideB, sideC);
        final var failure = assertThrows(InvalidInputException.class, () -> TriangleParser.parse(sides));
        assertEquals("Side length must be a number: \"abc\"", failure.getMessage());
    }

    @Test
    void rejectsSidesThatCannotFormATriangle() {
        assertThrows(InvalidTriangleException.class, () -> TriangleParser.parse(List.of("1", "2", "10")));
    }

    private static Stream<List<String>> wrongArgumentCounts() {
        return Stream.of(
                List.of(),
                List.of("3"),
                List.of("3", "4"),
                List.of("3", "4", "5", "6"),
                List.of("3 4 5"));
    }
}
