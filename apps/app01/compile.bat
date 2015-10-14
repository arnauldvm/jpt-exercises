@CALL ..\setenv.bat

@MKDIR target\classes
@javac -classpath target/classes -d target/classes src/main/java/jpt/app01/data/*.java src/main/java/jpt/app01/*.java
