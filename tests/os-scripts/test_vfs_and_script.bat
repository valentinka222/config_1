@echo off
if not exist out mkdir out
javac -d out ..\..\src\Main.java
echo Тест: и vfs, и script
java -cp out Main --vfs-path C:\fake\vfs.xml --script ..\emulator-scripts\demo.txt