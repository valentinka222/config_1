@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -d out ..\..\src\*.java
echo Тест: только --script
java -cp out Main --script ..\emulator-scripts\demo.txt