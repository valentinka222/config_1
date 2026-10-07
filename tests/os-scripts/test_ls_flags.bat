@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -d out ..\..\src\*.java
echo Тест: ls с ключами -l -a -h и путями
java -cp out Main --vfs-path ..\vfs-samples\hidden.xml --script ..\emulator-scripts\demo-ls.txt