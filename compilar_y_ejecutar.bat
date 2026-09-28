@echo off
REM Compila y ejecuta el proyecto en Windows (CMD o PowerShell)
if not exist bin mkdir bin
javac -d bin src\*.java src\estructuras\*.java src\modelo\*.java src\servicios\*.java src\menus\*.java src\util\*.java src\datos\*.java
if errorlevel 1 (
    echo Error de compilacion.
    pause
    exit /b 1
)
java -cp bin Main
