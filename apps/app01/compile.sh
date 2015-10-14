#!/bin/bash
pushd ..
. setenv.sh
popd

mkdir -p target/classes
javac -classpath target/classes -d target/classes src/main/java/jpt/app01/*.java
cp -r src/main/resources target/classes
