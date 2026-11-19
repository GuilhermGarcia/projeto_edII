#!/bin/bash
# Compila o projeto e executa a suite de testes e o arquivo data/testes.txt.
# Execute na raiz do projeto:  bash run_tests.sh
# Sai com codigo 0 somente se TODOS os testes passarem.
cd "$(dirname "$0")" || exit 1

if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
    echo "Erro: javac/java nao encontrados no PATH. Instale um JDK (versao 8 ou superior)."
    exit 1
fi

echo "========================================="
echo " COMPILANDO O PROJETO DE ESTRUTURA DE DADOS II"
echo "========================================="
mkdir -p bin
if ! javac -encoding UTF-8 -d bin src/*.java; then
    echo "Erro na compilacao!"
    exit 1
fi
echo "Compilacao bem-sucedida!"

status=0

echo "========================================="
echo " EXECUTANDO SUITE DE TESTES AUTOMATIZADOS (MainTest)"
echo "========================================="
java -cp bin MainTest || status=1

echo "========================================="
echo " EXECUTANDO data/testes.txt (java Dicionario)"
echo "========================================="
java -cp bin Dicionario data/testes.txt data/palavras.txt || status=1

echo "========================================="
if [ $status -eq 0 ]; then
    echo " TODOS OS TESTES PASSARAM"
else
    echo " HA TESTES FALHANDO (codigo de saida $status)"
fi
echo "========================================="
exit $status
