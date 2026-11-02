package com.aykutuysal.triangleclassifier.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An immutable triangle class. Impossible to create an invalid triangle.
 * Its constructor rejects sides that cannot form a triangle.
 */
public record Triangle(BigDecimal sideA, BigDecimal sideB, BigDecimal sideC) {

    /**
     * Checks each side, then checks whether the three sides form a triangle.
     *
     * @throws NullPointerException if any side is null
     * @throws InvalidTriangleException if a side is not positive or the sides cannot form a triangle
     */
    public Triangle {
        requireValidSide(sideA);
        requireValidSide(sideB);
        requireValidSide(sideC);
        requireTriangleInequality(sideA, sideB, sideC);
    }

    /**
     * Checks that a side is positive.
     *
     * @throws NullPointerException if the side is null
     * @throws InvalidTriangleException if the side is not positive
     */
    private static void requireValidSide(final BigDecimal side) {
        Objects.requireNonNull(side, "Side length must not be null");

        if (side.signum() <= 0) {
            throw new InvalidTriangleException("Side length must be positive: " + side);
        }
    }

    /**
     * Checks that the two shorter sides add up to more than the longest side.
     *
     * @throws InvalidTriangleException if the sides cannot form a triangle
     */
    private static void requireTriangleInequality(
            final BigDecimal sideA, final BigDecimal sideB, final BigDecimal sideC) {

        // All sides are positive, so only the longest side needs to be checked.
        final BigDecimal longestSide = sideA.max(sideB).max(sideC);
        final BigDecimal sumOfOtherSides = sideA.add(sideB).add(sideC).subtract(longestSide);

        // The other two sides must add up to more than the longest side.
        // Equality would make a flat triangle, with no area.
        if (sumOfOtherSides.compareTo(longestSide) <= 0) {
            throw new InvalidTriangleException(
                    "Not a triangle: the longest side (" + longestSide + ") must be shorter than the other two combined");
        }
    }
}
