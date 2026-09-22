#!/bin/bash
mkdir -p out
javac -d out src/Main.java
java -cp out Main