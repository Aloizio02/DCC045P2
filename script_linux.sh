#!/bin/bash

# Verifica se o arquivo ANTLR JAR existe
if [ ! -f "antlr/antlr-4.8.jar" ]; then
    echo "O arquivo antlr-4.8.jar não foi encontrado na pasta 'antlr'."
    exit 1
fi

# Gera o léxico e o sintático com ANTLR
java -jar antlr/antlr-4.8.jar -visitor -o . parser/lang.g4

# Compila todos os arquivos Java gerados e a classe principal
javac -cp ".:antlr/antlr-4.8.jar" LangCompiler.java

# Verifica se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro na compilação. Verifique os erros acima."
    exit 1
fi

echo "Build concluído! Iniciando o programa LangCompiler..."

# Executa o LangCompiler com o menu
java -cp ".:antlr/antlr-4.8.jar" LangCompiler