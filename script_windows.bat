@echo off
REM Verifica se o arquivo ANTLR JAR existe
if not exist "antlr\antlr-4.8.jar" (
    echo "O arquivo antlr-4.8.jar nao foi encontrado na pasta 'antlr'."
    pause
    exit /b
)

REM Diret�rio de sa�da para arquivos gerados
set OUTPUT_DIR=parser

REM Cria o diret�rio de sa�da, se n�o existir
if not exist %OUTPUT_DIR% mkdir %OUTPUT_DIR%

REM Gera o l�xico e o sint�tico com ANTLR
java -jar antlr\antlr-4.8.jar -visitor -o %OUTPUT_DIR% parser/lang.g4

REM Compila todos os arquivos Java gerados e a classe principal
javac -cp .;antlr\antlr-4.8.jar LangCompiler.java

REM Verifica se a compila��o foi bem-sucedida
if %errorlevel% neq 0 (
    echo "Erro na compilacao. Verifique os erros acima."
    pause
    exit /b
)

echo Analisadores gerados! Iniciando o programa LangCompiler...

REM Executa o LangCompiler com o menu
java -cp .;antlr\antlr-4.8.jar LangCompiler

pause