import parser.*;
import visitor.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Teste{
    public static void main(String args[]) throws Exception{
        CharStream  stream = CharStreams.fromFileName(args[0]);
        langLexer lex = new langLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        langParser parser = new langParser(tokens);

        ParseTree tree = parser.prog();
        //System.out.println(tree.toStringTree(parser));
        Visitor visitor = new Visitor();
        visitor.visit((langParser.ProgContext) tree);
    }
}