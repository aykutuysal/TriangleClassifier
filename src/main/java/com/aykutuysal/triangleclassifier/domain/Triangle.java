package com.aykutuysal.triangleclassifier.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An immutable triangle class. Impossible to create an invalid triangle.
 * Its constructor rejects sides that cannot form a triangle.
 *
 * <p>
 * Limits:
 *  Each side must be between {@code 1e-1000} and {@code 1e1000}, inclusive.
 *  This limit keeps exact additions manageable. Values outside it are
 *  rejected, not rounded.
 * </p>
 */
public record Triangle(BigDecimal sideA, BigDecimal sideB, BigDecimal sideC) {

    // Although BigDecimal supports extremely large numbers,
    // adding very large or very small numbers can use too much memory, take too long, or fail.
    // Limit triangle's side lengths to keep these calculations manageable.
    private static final BigDecimal MIN_SIDE_LENGTH = new BigDecimal("1e-1000");
    private static final BigDecimal MAX_SIDE_LENGTH = new BigDecimal("1e1000");

    /**
     * Checks each side, then checks whether the three sides form a triangle.
     *
     * @throws NullPointerException if any side is null
     * @throws InvalidTriangleException if a side is not positive, is outside the
     *         supported range, or the sides cannot form a triangle
     */
    public Triangle {
        requireValidSide(sideA);
        requireValidSide(sideB);
        requireValidSide(sideC);
        requireTriangleInequality(sideA, sideB, sideC);
    }

    /**
     * Checks that a side is positive and within the allowed length limits.
     *
     * @throws NullPointerException if the side is null
     * @throws InvalidTriangleException if the side is not positive or is outside these limits
     */
    private static void requireValidSide(final BigDecimal side) {
        Objects.requireNonNull(side, "Side length must not be null");

        if (side.signum() <= 0) {
            throw new InvalidTriangleException("Side length must be positive: " + side);
        }

        if (side.compareTo(MIN_SIDE_LENGTH) < 0 || side.compareTo(MAX_SIDE_LENGTH) > 0) {
            throw new InvalidTriangleException("Side length is outside the supported range: " + side);
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
