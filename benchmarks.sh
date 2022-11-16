#!/bin/sh

# Min testable version is 2.7.0 
QUTE_VERSIONS="2.13.3.Final 2.14.1.Final 999-SNAPSHOT";

# Set max to use Runtime.getRuntime().availableProcessors()
THREADS="1"

# Benchmarks to run
if [ "$1" ]; then
    BENCHMARKS=$1
else
    BENCHMARKS="Hello|HelloParser|Loop|Loop50|IfSimple|IfComplexCondition|NameResolver|IncludeSimple|When|LetSimple|LetComplex"
fi

echo "Qute versions to test: $QUTE_VERSIONS";
echo "Benchmarks to run: $BENCHMARKS"

QUTE_VERSIONS_ARRAY=$(echo $QUTE_VERSIONS);

for i in $QUTE_VERSIONS_ARRAY
do
  mvn package -Dversion.qute=$i
  java -jar target/qute-benchmarks.jar -t $THREADS -rf json -rff target/results-$i.json $BENCHMARKS
  java -cp target/qute-benchmarks.jar io.quarkus.qute.benchmark.chart.ChartGenerator target
done;
