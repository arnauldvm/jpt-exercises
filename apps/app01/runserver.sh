#!/bin/bash
pushd ..
. setenv.sh
popd

java -classpath target/classes jpt.app01.Main -p7666 -q0
