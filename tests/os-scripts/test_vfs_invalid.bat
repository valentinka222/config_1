@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -d out ..\..\src\*.java
echo Тест: несуществующий путь
java -cp out Main --vfs-path ..\vfs-samples\does-not-exist.xml