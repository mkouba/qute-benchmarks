#!/bin/sh

# Min testable version is 2.7.0 
QUTE_VERSIONS="2.16.12.Final 3.2.12.Final 3.8.6 3.14.1 999-SNAPSHOT";

# Set max to use Runtime.getRuntime().availableProcessors()
THREADS="1"

# Benchmarks to run
if [ "$1" ]; then
    BENCHMARKS=$1
else
    BENCHMARKS="HelloSimple|HelloParser|Loop15|Loop50|IfSimple|IfComplex|NameResolver|IncludeSimple|When|LetSimple|LetComplex|JavaBeanValueResolver"
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
