@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -d out ..\..\src\*.java
echo Тест: и vfs, и script
java -cp out Main --vfs-path ..\vfs-samples\multiply-files.xml --script ..\emulator-scripts\demo.txt