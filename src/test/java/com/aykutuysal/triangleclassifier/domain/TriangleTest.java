package com.aykutuysal.triangleclassifier.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class TriangleTest {

    @ParameterizedTest(name = "{0}, {1}, {2} -> accepted")
    @CsvSource({
            "3, 4, 5",
            "1, 2, 2.999999999999999999999",
            // Adding these sides would overflow an int.
            "2147483647, 2147483647, 2147483647",
            // Each side is larger than a long can hold.
            "9223372036854775808, 9223372036854775808, 9223372036854775808",
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
}
