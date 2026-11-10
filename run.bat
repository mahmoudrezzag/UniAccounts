@echo off
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt && java -cp out com.uniaccounts.Main
del sources.txt
