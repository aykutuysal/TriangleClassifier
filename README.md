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

## Tests

Run all tests:

```bash
./gradlew test
```

Run only `TriangleTest`:

```bash
./gradlew test --tests '*TriangleTest'
```

Open `build/reports/tests/test/index.html` in a browser to see the results. `./gradlew build` also runs all tests.

The tests check results and error messages, including values at the numeric limits. JUnit parameterized tests run the same check with several inputs.

| Test class | What it checks |
|---|---|
| [TriangleTest](src/test/java/com/aykutuysal/triangleclassifier/domain/TriangleTest.java) | Accepts valid sides; rejects null, zero or negative sides, flat or impossible triangles, and values outside the length or decimal-place limits |
| [TriangleClassifierTest](src/test/java/com/aykutuysal/triangleclassifier/domain/TriangleClassifierTest.java) | Recognizes all three triangle types regardless of side order; treats `1.0` and `1.00` as equal and detects small length differences |
| [TriangleParserTest](src/test/java/com/aykutuysal/triangleclassifier/input/TriangleParserTest.java) | Converts supported number formats to the correct values; rejects wrong argument counts, nulls and invalid formats with clear errors |
| [MainTest](src/test/java/com/aykutuysal/triangleclassifier/MainTest.java) | Produces the expected classification, help, version, error messages and exit codes |
| [JarTest](src/test/java/com/aykutuysal/triangleclassifier/JarTest.java) | Runs the built JAR and checks logging, help, version, process exit codes and handling of missing or incomplete version files |

`MainTest` calls `Main.run` directly and captures its output without stopping the test runner. `JarTest` launches the built JAR in separate Java processes to check that the packaged application works. Each process has a time limit and writes output to temporary files so a full output pipe cannot block it.

Run tests through Gradle so it builds the JAR first and gives the tests its location and expected build version.

## Design

For input `3 4 5`:

1. `Main` passes the arguments to `TriangleParser`.
2. `TriangleParser` converts the strings to numbers and constructs a `Triangle`.
3. `Triangle` validates the sides; `TriangleClassifier` returns `SCALENE`.
4. `Main` prints its label, `Scalene`, and exits successfully.

Classes under `com.aykutuysal.triangleclassifier`:

| Class | Responsibility |
|---|---|
| [Main](src/main/java/com/aykutuysal/triangleclassifier/Main.java) | Handles options (-h, --version etc.), CLI entry point, prints output, logs failures and chooses the exit code |
| [AppInfo](src/main/java/com/aykutuysal/triangleclassifier/AppInfo.java) | Provides usage, help and version text |
| [input.TriangleParser](src/main/java/com/aykutuysal/triangleclassifier/input/TriangleParser.java) | Parses input arguments into a valid Triangle. Checks the argument count and converts text into side lengths |
| [input.InvalidInputException](src/main/java/com/aykutuysal/triangleclassifier/input/InvalidInputException.java) | Exception for Input Validations |
| [domain.Triangle](src/main/java/com/aykutuysal/triangleclassifier/domain/Triangle.java) | Immutable domain object for a Triangle. Not possible to create an invalid Triangle. |
| [domain.InvalidTriangleException](src/main/java/com/aykutuysal/triangleclassifier/domain/InvalidTriangleException.java) | Exception for Triangle rules |
| [domain.TriangleClassifier](src/main/java/com/aykutuysal/triangleclassifier/domain/TriangleClassifier.java) | Triangle classification logic in one place, separately testable, extendable for later. |
| [domain.TriangleType](src/main/java/com/aykutuysal/triangleclassifier/domain/TriangleType.java) | Defines the three types and their output labels |

`domain` package handles numbers and triangle rules, with no command-line parsing, output or logging.
`input` package parses input texts, constructs domain objects.
