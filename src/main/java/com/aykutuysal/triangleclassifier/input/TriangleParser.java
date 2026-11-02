package com.aykutuysal.triangleclassifier.input;

import com.aykutuysal.triangleclassifier.AppInfo;
import com.aykutuysal.triangleclassifier.domain.InvalidTriangleException;
import com.aykutuysal.triangleclassifier.domain.Triangle;
import java.math.BigDecimal;
import java.util.List;

/**
 * Converts three side lengths from text into a valid triangle.
 *
 * <p>Accepts decimals and scientific notation, such as {@code 1.5} and {@code 1e2}.
 * Decimals must use a dot. Leading and trailing spaces are rejected.
 * {@link Triangle} checks the side limits and whether the sides form a triangle.
 */
public final class TriangleParser {

    private static final int SIDE_COUNT = 3;

    private TriangleParser() {}

    /**
     * Parses exactly three numbers and constructs a triangle.
     *
     * @throws InvalidInputException if the list is null, does not contain three entries,
     *         or an entry is null or cannot be read as a number
     * @throws InvalidTriangleException if the numbers do not meet {@link Triangle}'s rules
     */
    public static Triangle parse(final List<String> sides) {

        if (sides == null || sides.size() != SIDE_COUNT) {
            throw new InvalidInputException(AppInfo.USAGE);
        }

        return new Triangle(
                parseSide(sides.get(0)),
                parseSide(sides.get(1)),
                parseSide(sides.get(2)));
    }

    private static BigDecimal parseSide(final String text) {

        if (text == null) {
            throw new InvalidInputException("Side length must not be null");
        }

        try {
            return new BigDecimal(text);
        } catch (final NumberFormatException e) {
            throw new InvalidInputException("Side length must be a number: \"" + text + "\"", e);
        }
    }
}
