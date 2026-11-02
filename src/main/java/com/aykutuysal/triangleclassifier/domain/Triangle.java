package com.aykutuysal.triangleclassifier.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An immutable triangle class. Impossible to create an invalid triangle.
 * Its constructor rejects sides that cannot form a triangle.
 */
public record Triangle(BigDecimal sideA, BigDecimal sideB, BigDecimal sideC) {

    /**
     * Checks each side.
     *
     * @throws NullPointerException if any side is null
     * @throws InvalidTriangleException if a side is not positive
     */
    public Triangle {
        requireValidSide(sideA);
        requireValidSide(sideB);
        requireValidSide(sideC);
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
}
