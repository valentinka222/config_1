@echo off
if not exist out mkdir out
javac -d out ..\..\src\Main.java
echo Тест: без параметров
java -cp out Main