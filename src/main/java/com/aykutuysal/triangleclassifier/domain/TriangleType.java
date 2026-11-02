package com.aykutuysal.triangleclassifier.domain;

public enum TriangleType {

    EQUILATERAL("Equilateral"),
    ISOSCELES("Isosceles"),
    SCALENE("Scalene");

    private final String label;

    TriangleType(final String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
