@echo off
if not exist out mkdir out
javac -d out src/Main.java
java -cp out Main