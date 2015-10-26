@CALL ..\setenv.bat

@SET logs_dir=.\logs
@MKDIR %logs_dir%
@FOR /f "tokens=2 delims==" %%x IN ('WMIC OS Get localdatetime /value') DO @SET ts=%%x
@SET ts=%ts:~0,8%-%ts:~8,6%
@SET "gclog_options=-Xloggc:%logs_dir%\gc-%ts%.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
@SET "heap_options=-Xms1g -Xmx2g"
@SET "logging_options=-Djava.util.logging.config.file=./logging.properties"
@SET "jmx_options=-Dcom.sun.management.jmxremote.port=6789"
@SET "jmx_options=%jmx_options% -Dcom.sun.management.jmxremote.ssl=false"
@SET "jmx_options=%jmx_options% -Dcom.sun.management.jmxremote.authenticate=false"

java -classpath target/classes %gclog_options% %heap_options% %logging_options% %jmx_options% jpt.app01.Main -p7666 -q0
