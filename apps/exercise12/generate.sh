#!/bin/bash
pushd ..
. setenv.sh
popd

java -classpath target/classes com.kodewerk.profile.GenerateData 500000
