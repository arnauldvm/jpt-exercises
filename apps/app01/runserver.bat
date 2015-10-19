@CALL ..\setenv.bat

@SET logs_dir=.\logs
@MKDIR %logs_dir%
@FOR /f "skip=1" %%x in ('wmic os get localdatetime') do if not defined ts set ts=%%x
@SET "gclog_options=-Xloggc:%logs_dir%\gc-%ts%.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"

java -classpath target/classes %gclog_options% jpt.app01.Main -p7666 -q0
