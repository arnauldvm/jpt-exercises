@CALL ..\setenv.bat

@MKDIR target\classes
@PUSHD src\main\java
@SET tgt_dir=..\..\..\target
@SET java_files=%tgt_dir%\java_files.txt
@DIR /s/b *.java > %java_files%
@javac -classpath %tgt_dir%/classes -d %tgt_dir%/classes @%java_files%
@POPD
@XCOPY src\main\resources target\classes /S /I /Y
