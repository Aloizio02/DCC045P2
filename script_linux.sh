#!/bin/bash

# Verifica se o arquivo ANTLR JAR existe
if [ ! -f "antlr/antlr-4.8.jar" ]; then
    echo "O arquivo antlr-4.8.jar n�o foi encontrado na pasta 'antlr'."
    exit 1
fi

# Gera o l�xico e o sint�tico com ANTLR
java -jar antlr/antlr-4.8.jar -visitor -o . parser/lang.g4

# Compila todos os arquivos Java gerados e a classe principal
javac -cp ".:antlr/antlr-4.8.jar" LangCompiler.java

# Verifica se a compila��o foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro na compila��o. Verifique os erros acima."
    exit 1
fi

echo "Build conclu�do! Iniciando o programa LangCompiler..."

# Executa o LangCompiler com o menu
java -cp ".:antlr/antlr-4.8.jar" LangCompiler