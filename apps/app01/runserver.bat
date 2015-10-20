@CALL ..\setenv.bat

@SET logs_dir=.\logs
@MKDIR %logs_dir%
@FOR /f "tokens=2 delims==" %%x IN ('WMIC OS Get localdatetime /value') DO @SET ts=%%x
@SET ts=%ts:~0,8%-%ts:~8,6%
@SET "gclog_options=-Xloggc:%logs_dir%\gc-%ts%.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
@SET "heap_options=-Xms1g -Xmx2g"

java -classpath target/classes %gclog_options% %heap_options% jpt.app01.Main -p7666 -q0
