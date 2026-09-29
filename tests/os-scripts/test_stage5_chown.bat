@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -d out ..\..\src\*.java
echo Тест: команда chown
java -cp out Main --vfs-path ..\vfs-samples\nested.xml --script ..\emulator-scripts\demo-stage5.txt