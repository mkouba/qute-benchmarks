#!/bin/sh

#QUTE_VERSIONS="999-SNAPSHOT";
QUTE_VERSIONS="2.4.2.Final 2.5.4.Final 2.6.1.Final 999-SNAPSHOT";

# Set max to use Runtime.getRuntime().availableProcessors()
THREADS="1"

# Benchmarks to run
if [ "$1" ]; then
    BENCHMARKS=$1
else
    BENCHMARKS="Hello|Loop|Loop50|IfSimple|IfComplexCondition|NameResolver"
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
