package com.aykutuysal.triangleclassifier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class TriangleClassifierTest {

    private final TriangleClassifier classifier = new TriangleClassifier();

    @Test
    void rejectsNullTriangle() {
        final var failure = assertThrows(NullPointerException.class, () -> classifier.classify(null));
        assertEquals("Triangle must not be null", failure.getMessage());
    }

    @ParameterizedTest(name = "{0}, {1}, {2} -> {3}")
    @CsvSource({
            "3, 3, 3, EQUILATERAL",
            "0.1, 0.1, 0.1, EQUILATERAL",
            "1.0, 1.00, 1, EQUILATERAL",
            "1e2, 100, 100.0, EQUILATERAL",
            "3, 3, 4, ISOSCELES",
            "3, 4, 3, ISOSCELES",
            "4, 3, 3, ISOSCELES",
            "3.0, 3.00, 4, ISOSCELES",
            "3.0, 4, 3.00, ISOSCELES",
            "4, 3.0, 3.00, ISOSCELES",
            "1.0000000000000001, 1, 1, ISOSCELES",
            "3, 4, 5, SCALENE",
            "5, 3, 4, SCALENE",
            "4, 5, 3, SCALENE",
            "0.2, 0.3, 0.4, SCALENE",
    })
    void classifiesBySideLengths(
            final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC, final TriangleType expected) {
        assertEquals(expected, classifier.classify(new Triangle(sideA, sideB, sideC)));
    }
}
