@CALL ..\setenv.bat

@MKDIR target\classes
@javac -classpath target/classes -d target/classes src/com/kodewerk/profile/*.java
