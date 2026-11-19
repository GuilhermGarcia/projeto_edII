@echo off
REM Compila o projeto e executa a suite de testes e o arquivo data\testes.txt.
REM Execute na raiz do projeto:  run_tests.bat
REM Sai com codigo 0 somente se TODOS os testes passarem.
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo Erro: javac nao encontrado no PATH. Instale um JDK ^(versao 8 ou superior^).
    exit /b 1
)

echo =========================================
echo  COMPILANDO O PROJETO DE ESTRUTURA DE DADOS II
echo =========================================
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\*.java
if errorlevel 1 (
    echo Erro na compilacao!
    exit /b 1
)
echo Compilacao bem-sucedida!

set STATUS=0

echo =========================================
echo  EXECUTANDO SUITE DE TESTES AUTOMATIZADOS (MainTest)
echo =========================================
java -cp bin MainTest
if errorlevel 1 set STATUS=1

echo =========================================
echo  EXECUTANDO data\testes.txt (java Dicionario)
echo =========================================
java -cp bin Dicionario data\testes.txt data\palavras.txt
if errorlevel 1 set STATUS=1

echo =========================================
if %STATUS%==0 (
    echo  TODOS OS TESTES PASSARAM
) else (
    echo  HA TESTES FALHANDO
)
echo =========================================
exit /b %STATUS%
