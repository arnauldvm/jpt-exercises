#!/bin/bash
pushd ..
. setenv.sh
popd

java -Xprof -classpath target/classes com.kodewerk.profile.CheckIntegerTestHarness
