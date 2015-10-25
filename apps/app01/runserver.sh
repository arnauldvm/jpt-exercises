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
jmx_options="-Dcom.sun.management.jmxremote.port=6789"
jmx_options="$jmx_options -Dcom.sun.management.jmxremote.ssl=false"
jmx_options="$jmx_options -Dcom.sun.management.jmxremote.authenticate=false"

java -classpath target/classes $gclog_options $heap_options $logging_options $jmx_options jpt.app01.Main -p7666 -q0 -t200
