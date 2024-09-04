# Qute microbenchmarks

This is a collection of JMH benchmarks to verify the performance of [Qute](https://quarkus.io/guides/qute-reference).

> [!NOTE] 
> By default, the following versions of Qute are tested: **2.16.12.Final** (last 2.x) **3.2.12.Final** (LTS) **3.8.6** (LTS) **3.14.1** (latest) **999-SNAPSHOT** (main branch; has to be installed locally). If you want to test different versions, set the `QUTE_VERSIONS` environment variable, e.g. `export QUTE_VERSIONS="3.13.3 3.14.1"`.

> [!IMPORTANT]  
> The min testable version is 2.7.0.

## Run the benchmarks

Run all benchmarks with all Qute versions:

```bash
./benchmarks.sh
```

The results are stored in the `target` directory.
A results file has the `results-` prefix followed by the version, e.g `results-3.14.1.json`.
A chart is generated in the root directory of the project. 
The file is named `qute-microbenchmarks.png`.
A results summary table is also printed in the output.
It looks like:

```
RESULTS SUMMARY          |3.8.6 (Base)             |3.14.1                   |999-SNAPSHOT             
=========================|Score    |Error  |Diff   |Score    |Error  |Diff   |Score    |Error  |Diff   
-------------------------|-------------------------|-------------------------|-------------------------
HelloSimple              |    57824|   4770|       |    55215|   6143|    -5%|    60895|   1694|    +5%
```

> [!NOTE]
> The `Diff` value is a difference from the base result - the result of the leftmost version marked with `(Base)`.

The first argument can be used to select specific benchmarks:

```bash
# Run Loop15
./benchmarks.sh Loop15
# Run Loop15 and Loop50
./benchmarks.sh "Loop15|Loop50"
```
## Profilers

In order to run the JMH with `GCProfiler` enabled add the `-gc` argument to the command:

```bash
./benchmarks.sh -gc
```

In order to run the JMH with `AsyncProfiler` enabled to generate a flamegraph add the `-flame` argument to the command:

```bash
./benchmarks.sh -flame
```

The flamegraphs are located in the `profile-results` directory in the root directory of the project.
