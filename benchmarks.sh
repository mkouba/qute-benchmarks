#!/bin/bash

contains_arg() {
    local search="$1"
    shift
    local found=false

    for arg in "$@"; do
        if [[ "$arg" == "$search" ]]; then
            found=true
            break
        fi
    done

    if [[ "$found" == true ]]; then
        return 0
    else
        return 1
    fi
}

# Min testable version is 2.7.0 
if [ -z "$QUTE_VERSIONS" ]; then
    QUTE_VERSIONS="3.25.1 999-SNAPSHOT"
fi

# Set max to use Runtime.getRuntime().availableProcessors()
THREADS="1"

# Benchmarks to run
if [ "$1" ]; then
    BENCHMARKS=$1
else
    # JsonEscaping can only run on 3.18+
    BENCHMARKS="HelloSimple|HelloParser|Loop15|Loop50|IfSimple|IfComplex|NameResolver|IncludeSimple|When|LetSimple|LetComplex|JavaBeanValueResolver|Reflect"
fi

# Profilers
ASYNC_PROFILER_PATH="/opt/java/async-profiler-3.0-linux-x64/lib/libasyncProfiler.so"

PROFILERS=""
if contains_arg "-gc" "$@"; then
    PROFILERS="$PROFILERS -prof gc"
fi

if contains_arg "-flame" "$@"; then
    PROFILERS="$PROFILERS -prof async:libPath=$ASYNC_PROFILER_PATH;output=flamegraph;dir=profile-results"
fi

if contains_arg "-t4" "$@"; then
    THREADS="4"
fi

echo "============================================"
echo "Qute versions to test: $QUTE_VERSIONS";
echo "Benchmarks to run: $BENCHMARKS"
echo "============================================"

# Clean the target directory
mvn clean

QUTE_VERSIONS_ARRAY=$(echo $QUTE_VERSIONS);

for i in $QUTE_VERSIONS_ARRAY
do
  mvn package -Dversion.qute=$i
  java -jar target/qute-benchmarks.jar -t $THREADS $PROFILERS -rf json -rff target/results-$i.json $BENCHMARKS
  java -cp target/qute-benchmarks.jar io.quarkus.qute.benchmark.chart.ChartGenerator target
done;
