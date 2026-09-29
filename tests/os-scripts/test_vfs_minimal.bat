@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -d out ..\..\src\*.java
echo Тест: минимальная VFS
java -cp out Main --vfs-path ..\vfs-samples\minimal.xml