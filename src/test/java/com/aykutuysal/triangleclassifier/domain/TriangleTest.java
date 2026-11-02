package com.aykutuysal.triangleclassifier.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

final class TriangleTest {

    @ParameterizedTest(name = "{0}, {1}, {2} -> accepted")
    @CsvSource({
            "3, 4, 5",
            "1, 2, 2.999999999999999999999",
            // Adding these sides would overflow an int.
            "2147483647, 2147483647, 2147483647",
            // Each side is larger than a long can hold.
            "9223372036854775808, 9223372036854775808, 9223372036854775808",
            // Exact addition must preserve the tiny side alongside the two large sides.
            "1e-1000, 1e1000, 1e1000",
    })
    void acceptsValidSides(final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC) {
        assertDoesNotThrow(() -> new Triangle(sideA, sideB, sideC));
    }

    @ParameterizedTest(name = "{0}, {1}, {2} -> {3} is not positive")
    @CsvSource({
            "0, 4, 5, 0",
            "3, 0, 5, 0",
            "3, 4, 0, 0",
            "0, 0, 0, 0",
            "-3, 4, 5, -3",
            "3, -4, 5, -4",
            "3, 4, -5, -5",
    })
    void rejectsNonPositiveSides(
            final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC, final String invalidSide) {
        final var failure = assertThrows(InvalidTriangleException.class, () -> new Triangle(sideA, sideB, sideC));
        assertEquals("Side length must be positive: " + invalidSide, failure.getMessage());
    }

    @ParameterizedTest(name = "{0}, {1}, {2} -> rejected")
    @CsvSource(value = {
            "null, 1, 1",
            "1, null, 1",
            "1, 1, null",
    }, nullValues = "null")
    void rejectsNullSide(final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC) {
        final var failure = assertThrows(
                NullPointerException.class, () -> new Triangle(sideA, sideB, sideC));
        assertEquals("Side length must not be null", failure.getMessage());
    }

    @ParameterizedTest(name = "{0}, {1}, {2} -> flat triangle")
    @CsvSource({
            "1, 2, 3, 3",
            "3, 1, 2, 3",
            "2, 3, 1, 3",
            "0.1, 0.2, 0.3, 0.3",
    })
    void rejectsFlatTriangles(
            final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC, final String longestSide) {
        final var failure = assertThrows(InvalidTriangleException.class, () -> new Triangle(sideA, sideB, sideC));
        assertEquals("Not a triangle: the longest side (" + longestSide
                + ") must be shorter than the other two combined", failure.getMessage());
    }

    @ParameterizedTest(name = "{0}, {1}, {2} -> sides cannot meet")
    @CsvSource({
            "1, 2, 10",
            "10, 1, 2",
            "2, 10, 1",
    })
    void rejectsSidesThatCannotMeet(final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC) {
        final var failure = assertThrows(InvalidTriangleException.class, () -> new Triangle(sideA, sideB, sideC));
        assertEquals("Not a triangle: the longest side (10) must be shorter than the other two combined",
                failure.getMessage());
    }

    @ParameterizedTest(name = "{0} -> accepted")
    @ValueSource(strings = {"1e1000", "1e-1000"})
    void acceptsSidesAtTheLimits(final BigDecimal side) {
        assertDoesNotThrow(() -> new Triangle(side, side, side));
    }

    @ParameterizedTest(name = "{0} -> outside the range")
    @ValueSource(strings = {
            "1.1e1000",
            "9e-1001",
            "1e1001",
            "1e-1001",
            "1e999999999",
            "1e-999999999",
            "1e2147483647",
            "1e-2147483647",
            // BigDecimal accepts this exponent, but its scale is Integer.MIN_VALUE.
            "1e2147483648",
    })
    void rejectsSidesBeyondTheLimits(final BigDecimal side) {
        final var failure = assertThrows(
                InvalidTriangleException.class, () -> new Triangle(side, BigDecimal.ONE, BigDecimal.ONE));
        assertEquals("Side length is outside the supported range: " + side, failure.getMessage());
    }

    @Test
    void rejectsTooLargeNumberWrittenInFull() {
        final BigDecimal side = new BigDecimal("1" + "0".repeat(1500));
        final var failure = assertThrows(
                InvalidTriangleException.class, () -> new Triangle(side, BigDecimal.ONE, BigDecimal.ONE));
        assertEquals("Side length is outside the supported range: " + side, failure.getMessage());
    }
}
