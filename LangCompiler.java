import parser.*;
import visitors.*;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class LangCompiler {
    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.err.println("Informe o endereço da pasta com os arquivos de teste!");
            return;
        }

        File folder = new File(args[0]);
        if (!folder.isDirectory()) {
            System.err.println("O caminho fornecido nao e um diretorio.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".lan"));
        if (files == null || files.length == 0) {
            System.err.println("Nenhum arquivo encontrado na pasta.");
            return;
        }

        for (File file : files) {
            processFile(file);
        }
    }

    private static void processFile(File file) {
        try {
            System.out.println("Processando arquivo: " + file.getName());

            CharStream stream = CharStreams.fromFileName(file.getAbsolutePath());
            langLexer lex = new langLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            langParser parser = new langParser(tokens);

            langParser.ProgContext progContext = parser.prog();

            InterpretVisitor visitor = new InterpretVisitor();
            visitor.visitProg(progContext);
            
            System.out.println("Processamento concluido para o arquivo: " + file.getName());
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + file.getName());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo: " + file.getName());
            e.printStackTrace();
        }
    }
}
