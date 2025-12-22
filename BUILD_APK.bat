@echo off
REM ================================================
REM Script para Gerar Gradle Wrapper e Compilar APK
REM Agente-Smith-Android
REM ================================================

echo [*] Iniciando processo de compilacao...
echo.

REM Verifica se gradle está instalado
where gradle >nul 2>nul
if errorlevel 1 (
    echo [!] ERRO: Gradle nao encontrado no PATH
    echo [*] Instalando Gradle Wrapper...
    REM Tenta usar java -jar para gerar wrapper
    for /f "delims=" %%A in ('where java') do set "JAVA_HOME=%%~dpA.."
    echo [*] JAVA_HOME: %JAVA_HOME%
) else (
    echo [+] Gradle encontrado no PATH
)

echo.
echo [*] Gerando Gradle Wrapper...
gradle wrapper --gradle-version 8.0

if errorlevel 1 (
    echo [!] Falha ao gerar wrapper, tentando alternativa...
    REM Se gradle nao estiver, tenta com java direto
    java -version
)

echo.
echo [*] Limpando build anterior...
call gradlew.bat clean

echo.
echo [*] Compilando APK para Android...
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo [!] ERRO: Falha na compilacao!
    echo [*] Tentando sincronizar dependencias...
    call gradlew.bat --refresh-dependencies clean assembleDebug
) else (
    echo.
    echo [+] APK compilado com sucesso!
    echo [+] Localizacao: app\build\outputs\apk\debug\app-debug.apk
    echo.
)

pause
