#!/bin/bash
pushd ..
. setenv.sh
popd

java -classpath target/classes jpt.app00.Main -p7666 -q0
