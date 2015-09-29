#!/bin/bash
pushd ..
. setenv.sh
popd

mkdir -p target/classes
javac -classpath target/classes -d target/classes src/main/java/jpt/app00/*.java
