#!/bin/bash
pushd ..
. setenv.sh
popd

logs_dir="./logs"
mkdir -p "$logs_dir"
ts=$(date +%Y%m%d-%H%M%S)
gclog_options="-Xloggc:$logs_dir/gc-$ts.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
heap_options="-Xms1g -Xmx2g"
logging_options="-Djava.util.logging.config.file=./logging.properties"

java -classpath target/classes $gclog_options $heap_options $logging_options jpt.app01.Main -p7666 -q0 -t200
