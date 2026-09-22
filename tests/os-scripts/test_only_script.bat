@echo off
if not exist out mkdir out
javac -d out ..\..\src\Main.java
echo Тест: только --script
java -cp out Main --script ..\emulator-scripts\demo.txt