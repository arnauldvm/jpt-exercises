#!/bin/bash
pushd ..
. setenv.sh
popd

mkdir -p target/classes
pushd src/main/java
tgt_dir=../../../target
java_files=$tgt_dir/java_files.txt
find . -name '*.java' > $java_files
javac -classpath $tgt_dir/classes -d $tgt_dir/classes @$java_files
popd
cp -rv src/main/resources/. target/classes
