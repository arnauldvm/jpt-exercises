#!/bin/bash
pushd ..
. setenv.sh
popd

logs_dir="./logs"
mkdir -p "$logs_dir"
ts=$(date +%Y%m%d-%H%M%S)
gclog_options="-Xloggc:$logs_dir/gc-$ts.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
heap_options="-Xms1g -Xmx2g"

java -classpath target/classes $gclog_options $heap_options jpt.app01.Main -p7666 -q0
