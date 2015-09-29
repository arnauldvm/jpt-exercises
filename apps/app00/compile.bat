@CALL ..\setenv.bat

@MKDIR target\classes
@javac -classpath target/classes -d target/classes src/main/java/jpt/app00/*.java
