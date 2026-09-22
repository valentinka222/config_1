@echo off
if not exist out mkdir out
javac -d out ..\..\src\Main.java
echo Тест: только --vfs-path
java -cp out Main --vfs-path C:\fake\vfs.xml