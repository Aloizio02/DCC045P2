import parser.*;
import visitor.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Teste{
    public static void main(String args[]) throws Exception{
        CharStream stream = CharStreams.fromFileName(args[0]);
        langLexer lex = new langLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        langParser parser = new langParser(tokens);

        langParser.ProgContext progContext = parser.prog(); // Obtém o ProgContext
        //System.out.println(progContext.toStringTree(parser)); // Para depuração

        Visitor visitor = new Visitor();
        visitor.visitProg(progContext); // Chama visitProg diretamente
    }
}