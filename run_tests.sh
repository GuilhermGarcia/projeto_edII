#!/bin/bash

echo "========================================="
echo " COMPILANDO O PROJETO DE ESTRUTURA DE DADOS II"
echo "========================================="

# Cria a pasta bin se não existir
mkdir -p bin

# Compila todas as classes do pacote ARVTRIE
javac -d bin -cp src src/ARVTRIE/*.java

if [ $? -eq 0 ]; then
    echo "Compilação bem-sucedida!"
    echo "========================================="
    echo " EXECUTANDO SUÍTE DE TESTES AUTOMATIZADOS"
    echo "========================================="
    java -cp bin ARVTRIE.MainTest
else
    echo "Erro na compilação!"
fi