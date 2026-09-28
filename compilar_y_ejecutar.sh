#!/bin/bash
# Compila y ejecuta el proyecto en Linux/macOS
mkdir -p bin
javac -d bin $(find src -name "*.java") && java -cp bin Main
