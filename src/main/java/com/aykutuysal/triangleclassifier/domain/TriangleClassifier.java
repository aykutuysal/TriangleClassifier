package com.aykutuysal.triangleclassifier.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Determines whether a triangle is equilateral, isosceles, or scalene.
 * {@link Triangle} checks that the side lengths are valid when it is created.
 */
public final class TriangleClassifier {

    /**
     * Returns equilateral for three equal sides, isosceles for exactly two equal sides,
     * or scalene when all three sides differ.
     *
     * @throws NullPointerException if the triangle is null
     */
    public TriangleType classify(final Triangle triangle) {

        Objects.requireNonNull(triangle, "Triangle must not be null");

        final boolean aEqualsB = sameLength(triangle.sideA(), triangle.sideB());
        final boolean bEqualsC = sameLength(triangle.sideB(), triangle.sideC());
        final boolean aEqualsC = sameLength(triangle.sideA(), triangle.sideC());

        // Check equilateral first: three equal sides also pass the isosceles check below.
        if (aEqualsB && bEqualsC) {
            return TriangleType.EQUILATERAL;
        }

        if (aEqualsB || bEqualsC || aEqualsC) {
            return TriangleType.ISOSCELES;
        }

        return TriangleType.SCALENE;
    }

    // 1.0 and 1.00 represent the same length, but BigDecimal.equals treats them as different.
    // compareTo ignores that difference in decimal places and returns 0 for equal values.
    private boolean sameLength(final BigDecimal first, final BigDecimal second) {
        return first.compareTo(second) == 0;
    }
}
