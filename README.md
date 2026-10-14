# ATG Miner: Replication Package

This package contains the implementation and the evaluation subjects needed to replicate the evaluation of the
ATG Miner. Given a context-free grammar (`grammar.atg`) and a Python parser implementation (`program.py`), the ATG Miner
infers an *attributed* grammar: it executes the parser in an interpreter and extracts semantic attributes and actions
from it. The inferred grammar is then validated against manually written inputs. For every input, the result of the
program's `result` function is compared with the result of interpreting the inferred attributed grammar.

## Running the evaluation with Docker

Requirements: Docker.

```bash
./run.sh            # Linux / macOS / Git Bash
.\run.ps1           # Windows PowerShell
```

The scripts build the image `atg-miner-replication`, run the evaluation on all examples and print the score of each
example. `examples/` is mounted into the container, so all outputs are written next to the examples on the host. To run
only some examples, pass their names, e.g. `./run.sh Calc Xml`. A full run takes about 2 minutes.

Manual Docker usage:

```bash
docker build -t atg-miner-replication .
docker run --rm -v "$(pwd)/examples:/work/examples" atg-miner-replication               # all examples
docker run --rm -v "$(pwd)/examples:/work/examples" atg-miner-replication examples/ Calc
```

## Running the evaluation without Docker

Requirements: Java 11 and Maven 3.8+.

```bash
mvn package
java -Dfile.encoding=UTF-8 -jar target/atg-miner.jar examples/ [Example ...]
```

The first argument is the examples directory (default `examples/`). Any further arguments restrict the run to the named
examples. To run it from an IDE, import `pom.xml` as a Maven project and run
`at.fhooe.se.atgminer.ATGMinerEvaluation` with this folder as the working directory.

## Results

| Example        | Valid inputs |
|----------------|--------------|
| ABCParse       | 5/5          |
| Calc           | 10/10        |
| CgiDecode      | 6/6          |
| Mathexpr       | 20/31        |
| MicroJson      | 22/22        |
| NumberParser   | 4/4          |
| SimpleExprCalc | 6/6          |
| Xml            | 5/5          |
| ZeroOne        | 3/3          |

The evaluation is deterministic: the grammar fuzzer that generates the inputs for inference uses a fixed seed.

## Structure

```
examples/<Example>/
    grammar.atg           the given context-free grammar
    program.py            the parser implementation under analysis
    manual_inputs.txt     manually written inputs used for validation
src/main/java/            implementation of the ATG Miner and the evaluation (at.fhooe.se.atgminer.ATGMinerEvaluation)
repo/                     local Maven repository containing the dependency jar
Dockerfile, run.sh, run.ps1
```

Each example produces two output files next to its program:

- `result.atg`: the inferred attributed grammar
- `result.txt`: `<valid>/<total> (<ratio>)`, followed by the inputs that failed and the reason

## Dependencies

`repo/at/fhooe/se/adele/atg-miner-dependencies/1.0.0/atg-miner-dependencies-1.0.0.jar` contains the libraries the
implementation builds on: the Adele interpreter (core, Python and Pascal), the eKnows frontends (Adele, Pascal, Python),
eKnows core/commons 1.0.4, Eclipse EMF, the ANTLR 4.9.2 runtime and the Z3 4.3.2 Java binding.

## Licenses

This work contains open source libraries that are shaded within the `atg-miner-dependencies-1.0.0.jar` file. These
libraries do have the following licenses:

 - antlr@4.9.2: LICENSE-BSD.txt
 - z3@4.3.2: LICENSE-MIT.txt
 - EclipseEMF@2.15.0: LICENSE-EPL.txt
 - eKnows@1.0.4: LICENSE-eKnows.html

In addition, a maven license xml can be found under licenses.xml, with the license files in the licenses subfolder.
