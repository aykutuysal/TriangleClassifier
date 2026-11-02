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

Example arguments after the JAR path, with their exact output.

A valid triangle exits with code `0` and prints one word to stdout:

| Arguments | Stdout |
|---|---|
| `3 3 3` | `Equilateral` |
| `3 3 4` | `Isosceles` |
| `3 4 5` | `Scalene` |
| `0.2 0.3 0.4` | `Scalene` |
| `1.0 1.00 1` | `Equilateral` |
| `1e2 100 100.0` | `Equilateral` |

Rejected input exits with code `2`, leaves stdout empty and prints one line to stderr:

| Case | Arguments | Stderr |
|---|---|---|
| Flat triangle | `1 2 3` | `Not a triangle: the longest side (3) must be shorter than the other two combined` |
| Sides cannot meet | `1 2 10` | `Not a triangle: the longest side (10) must be shorter than the other two combined` |
| Zero side | `0 1 1` | `Side length must be positive: 0` |
| Negative side | `-3 4 5` | `Side length must be positive: -3` |
| Not a number | `3 abc 5` | `Side length must be a number: "abc"` |
| Option used as a side | `3 4 --help` | `Side length must be a number: "--help"` |
| Comma instead of a dot | `1,2 2,2 3,2` | `Side length must use a dot for decimals (for example, 1.2): "1,2"` |
| Side too large | `1e1001 1 1` | `Side length is outside the supported range: 1E+1001` |
| Side too small | `1 1 1e-1001` | `Side length is outside the supported range: 1E-1001` |
| Too many decimal places | `1.000…0 1 1` (1,001 zeros, shortened here) | `Side length must have at most 1000 decimal places: 1.000…0` (prints the whole side) |
| Wrong arguments | No arguments, `3 4`, `3 4 5 6`, `"3 4 5"`, `--unknown`, `--help 3` | `Usage: java -jar triangle-classifier.jar <side-a> <side-b> <side-c>` |


| Exit code | Meaning |
|---|---|
| `0` | Success: classification, help or version printed |
| `1` | Unexpected application failure |
| `2` | Invalid input or sides that cannot form a triangle |

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
| [TriangleClassifierAppTest](src/test/java/com/aykutuysal/triangleclassifier/TriangleClassifierAppTest.java) | Produces the expected classification, help, version, error messages and exit codes |
| [JarTest](src/test/java/com/aykutuysal/triangleclassifier/JarTest.java) | Runs the built JAR and checks logging, help, version, process exit codes and handling of missing or incomplete version files |

`TriangleClassifierAppTest` calls `TriangleClassifierApp.run` directly and captures its output without stopping the test runner. `JarTest` launches the built JAR in separate Java processes to check that the packaged application works. Each process has a time limit and writes output to temporary files so a full output pipe cannot block it.

Run tests through Gradle so it builds the JAR first and gives the tests its location and expected build version.

## Design

For input `3 4 5`:

1. `TriangleClassifierApp` passes the arguments to `TriangleParser`.
2. `TriangleParser` converts the strings to numbers and constructs a `Triangle`.
3. `Triangle` validates the sides; `TriangleClassifier` returns `SCALENE`.
4. `TriangleClassifierApp` prints its label, `Scalene`, and exits successfully.

Classes under `com.aykutuysal.triangleclassifier`:

| Class | Responsibility |
|---|---|
| [TriangleClassifierApp](src/main/java/com/aykutuysal/triangleclassifier/TriangleClassifierApp.java) | Handles options (-h, --version etc.), CLI entry point, prints output, logs failures and chooses the exit code |
| [AppInfo](src/main/java/com/aykutuysal/triangleclassifier/AppInfo.java) | Provides usage, help and version text |
| [input.TriangleParser](src/main/java/com/aykutuysal/triangleclassifier/input/TriangleParser.java) | Parses input arguments into a valid Triangle. Checks the argument count and converts text into side lengths |
| [input.InvalidInputException](src/main/java/com/aykutuysal/triangleclassifier/input/InvalidInputException.java) | Exception for Input Validations |
| [domain.Triangle](src/main/java/com/aykutuysal/triangleclassifier/domain/Triangle.java) | Immutable domain object for a Triangle. Not possible to create an invalid Triangle. |
| [domain.InvalidTriangleException](src/main/java/com/aykutuysal/triangleclassifier/domain/InvalidTriangleException.java) | Exception for Triangle rules |
| [domain.TriangleClassifier](src/main/java/com/aykutuysal/triangleclassifier/domain/TriangleClassifier.java) | Triangle classification logic in one place, separately testable, extendable for later. |
| [domain.TriangleType](src/main/java/com/aykutuysal/triangleclassifier/domain/TriangleType.java) | Defines the three types and their output labels |

`domain` package handles numbers and triangle rules, with no command-line parsing, no output.

`input` package parses input texts, constructs domain objects.

## Decisions and trade-offs

- **Exact decimals.** `BigDecimal` stores each triangle side exactly. `double` makes `0.1 + 0.2` slightly more than `0.3`, so it could accept an invalid triangle. Integers can't hold decimals and can overflow. Triangle sides are compared with `compareTo`, because `equals` treats `1.0` and `1.00` as different.

- **Immutable, always valid `Triangle`.** Every `Triangle` is checked when it's created, so no caller can forget to validate it. It's a Java record, so it can't change afterwards and needs very little code.

- **Separate responsibilities.** Each class has one job, so it's easy to read and test on its own. Classification can live in `Triangle` as well, but a separate class makes it easier to test and extend.

- **Plain Java command-line handling.** The program only accepts three side lengths, `--help` or `--version`. Plain Java handles that easily, and a framework like Spring would only add setup.

- **Distinct errors.** Two exceptions distinguish invalid input text from invalid triangle values.

- **Separate failure exit codes.** `2` means the input is wrong and the user must fix it. `1` means the program itself failed. One failure code would be simpler, but callers couldn't tell the two apart.

- **Logging and packaging.** Clear, extendable logging with SL4J. The Shadow Gradle plugin bundles dependencies into one runnable JAR that requires only Java.

A file or HTTP version could reuse `Triangle` and `TriangleClassifier` and add its own parsing and output. Today's program needs no Java interfaces or extra layers.

## Triangle limits

`Triangle` rejects values outside these limits and never rounds them.

| Limit | Why |
|---|---|
| Each side is positive | A side cannot have zero or negative length |
| The longest side is shorter than the other two combined | Otherwise the sides cannot meet, or they lie flat with no area (`1 2 3`) |
| Each side is between `1e-1000` and `1e1000`, inclusive | Adding extreme values exactly can use too much memory, take too long or fail: `1e999999999 + 1` has a billion digits |
| Each side has at most 1,000 decimal places, including trailing zeros | A value near 1 passes the range limit but can still have thousands of digits, which makes addition costly |

The last two limits keep every sum to about 2,000 digits.

## Logging

Normal runs log nothing at the default `WARN` level. Show arguments, classifications and rejection details with:

```bash
TRIANGLE_LOG_LEVEL=DEBUG java -jar build/libs/triangle-classifier.jar 3 4 5
```

This syntax is for Linux/macOS shells. In PowerShell, set `$env:TRIANGLE_LOG_LEVEL = "DEBUG"` before running Java.

Levels: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `OFF`. Logback treats unknown levels as `DEBUG`. Use `TRIANGLE_LOG_LEVEL`; the generic `LOG_LEVEL` variable has no effect. Unexpected failures log at `ERROR`; input errors print as plain messages.

## Known limitations

- The parser and domain classes supply English messages directly. Translation (if needed in the future) needs a separate message mapping.

