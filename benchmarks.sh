#!/bin/sh

#QUTE_VERSIONS="999-SNAPSHOT";
#QUTE_VERSIONS="1.6.1.Final 1.7.6.Final 1.8.3.Final 1.9.2.Final 1.10.5.Final 1.11.6.Final 1.12.2.Final 1.13.0.Final 999-SNAPSHOT";
QUTE_VERSIONS="999-SNAPSHOT 1.10.5.Final";

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
