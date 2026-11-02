# Triangle Classifier

A Java command-line application that classifies three side lengths:

| Result | Meaning | Example sides |
|---|---|---|
| `Equilateral` | All three sides are equal | `3 3 3` |
| `Isosceles` | Exactly two sides are equal | `3 3 4` |
| `Scalene` | All three sides are different | `3 4 5` |

The sides must form a valid triangle: `1 2 10` is rejected because `1 + 2 < 10`.

## Quick start

Install **JDK 21 or newer**. From the project directory, run:

```bash
./gradlew build
java -jar build/libs/triangle-classifier.jar 3 4 5
```

Output:

```text
Scalene
```

The build runs all tests and packages the application and dependencies in one runnable JAR. No Gradle installation is needed. On Windows, use `gradlew.bat build`.

## Usage

```text
java -jar build/libs/triangle-classifier.jar <side-a> <side-b> <side-c>
```

- Replace the placeholders with exactly three positive side lengths, separated by spaces, in any order. `"3 4 5"` is one argument and is rejected.
- Integers, decimals and scientific notation are accepted: `1e2` means `100`.
- Use a decimal dot (`1.5`), without commas, spaces inside numbers or units such as `cm`.

Example arguments after the JAR path:

| Arguments | Result |
|---|---|
| `3 3 3` | `Equilateral` |
| `3 3 4` | `Isosceles` |
| `3 4 5` | `Scalene` |
| `0.2 0.3 0.4` | `Scalene` |
| `1.0 1.00 1` | `Equilateral` |
| `1e2 100 100.0` | `Equilateral` |
| `1 2 3` | Error: the triangle would be flat |
| `-3 4 5` | Error: sides must be positive |
| `3 abc 5` | Error: each side must be a number |

### Help and version

```bash
java -jar build/libs/triangle-classifier.jar --help
java -jar build/libs/triangle-classifier.jar --version
```

| Option | Short form | What it does |
|---|---|---|
| `--help` | `-h` | Prints usage, options and exit codes |
| `--version` | `-v` | Prints the application name and build version |

Use each option alone, without sides or other options. Gradle embeds the version from [build.gradle.kts](build.gradle.kts) in the JAR.

### Output and errors

A classification prints one word to standard output (`stdout`). Help and version also use stdout; errors and logs use standard error (`stderr`).

```bash
java -jar build/libs/triangle-classifier.jar 1 2 10
```

This command leaves stdout empty, exits with code `2` and writes to stderr:

```text
Not a triangle: the longest side (10) must be shorter than the other two combined
```

| Exit code | Meaning |
|---|---|
| `0` | Success: classification, help or version printed |
| `1` | Unexpected application failure |
| `2` | Invalid input or sides that cannot form a triangle |
