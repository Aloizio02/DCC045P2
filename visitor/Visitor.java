package visitor;

import parser.langBaseVisitor;
import parser.langParser;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class Visitor extends langBaseVisitor<Object> {
    private Stack<HashMap<String,HashMap<String,Object>>> env = new Stack<>();
    private HashMap<String,HashMap<String,Object>> funcs = new HashMap<>();

	@Override
    public Object visitProg(langParser.ProgContext ctx) {
        if (ctx.data() != null) {
            return visitData(ctx.data(0));
        } 
        if (ctx.func() != null) {
            return visitFunc(ctx.func(0));
        }
        return null; 
    }

	@Override 
    public Object visitData(langParser.DataContext ctx) { 
        //return visitChildren(ctx);
        return null;
    }

	@Override 
    public Object visitDecl(langParser.DeclContext ctx) { 
        //return visitChildren(ctx);
        return null; 
    }

	@Override 
    public Object visitFunc(langParser.FuncContext ctx) {
        System.out.println("teste");
        /* 
        HashMap<String,Object> operands = new HashMap<>();
        funcs.put(ctx.ID().getText(), ctx);
        if (ctx.params() != null) {
            for(int i = 0; i < ctx.params().size(); i++){
                operands.put(visitParams(ctx.params()).ID(), operands)
                funcs.put(ctx.ID().getText(), );
                return visitParams(ctx.params(i));
            }
        }
        for(int i = 0; i < ctx.cmd().size(); i++){
            operands.push(visitCmd(ctx.cmd(i)));
        }
        env.pop();
        */
        return null;
    }

	@Override 
    public Object visitParams(langParser.ParamsContext ctx) { 
        
        return null;
    }

	@Override 
    public Object visitType(langParser.TypeContext ctx) { 
        //return visitChildren(ctx); 
        return null;
    }

	@Override 
    public Object visitBtype(langParser.BtypeContext ctx) { 
        //return visitChildren(ctx); 
        return null;
    }

	@Override 
    public Object visitCmd(langParser.CmdContext ctx) { 
        if (ctx.lvalue() != null && ctx.ID() == null){
            //return (visitLvalue(ctx.lvalue(0)), visitExp(ctx.exp(0)));
        }
        return null;
    }

	@Override 
    public Object visitExp(langParser.ExpContext ctx) { 
        //return visitChildren(ctx); 
        return null;
    }

	@Override 
    public Object visitRexp(langParser.RexpContext ctx) { 
        //return visitChildren(ctx); 
        return null;
    }

	@Override 
    public Object visitAexp(langParser.AexpContext ctx) { 
        //return visitChildren(ctx); 
        return null;
    }

	@Override 
    public Object visitMexp(langParser.MexpContext ctx) { 
        //return visitChildren(ctx); 
        return null;
    }

	@Override 
    public Object visitSexp(langParser.SexpContext ctx) {
        if (ctx.INT() != null) {
            return Integer.parseInt(ctx.INT().getText());
        }
        if (ctx.FLOAT() != null) {
            return Float.parseFloat(ctx.FLOAT().getText());
        }
        if (ctx.BOOL() != null) {
            return Boolean.parseBoolean(ctx.BOOL().getText());
        }
        if (ctx.NULL() != null) {
            return null;
        }
        if (ctx.CHAR() != null) {
            return ctx.CHAR().getText().charAt(0);
        }
        if (ctx.pexp() != null) {
            return visitPexp(ctx.pexp());
        }
        return null;
    }

	@Override 
    public Object visitPexp(langParser.PexpContext ctx) { 
        if (ctx.lvalue() != null){
            return visitLvalue(ctx.lvalue());
        }
        if (ctx.type() != null) {
            String typeName = ctx.type().getText();
            Object size = visitExp(ctx.exp());

            if (size instanceof Integer){
                if (typeName == "Int") {
                    return new int[(Integer) size];
                }
                if (typeName == "Float") {
                    return new float[(Integer) size];
                }
                if (typeName == "Char") {
                    return new char[(Integer) size];
                }
                if (typeName == "Bool") {
                    return new boolean[(Integer) size];
                } else {
                    Class<?> clazz = Class.forName(typeName);
                    return java.lang.reflect.Array.newInstance(clazz, (Integer) size);
                }
            }
            return null;
        }
        if (ctx.exp() != null && ctx.type() == null) {
            return visitExp(ctx.exp());
        }
        if (ctx.ID() != null) {
            visitFunc(ctx.ID().func);
            return null;
        }
        return null;
    }

	@Override 
    public Object visitLvalue(langParser.LvalueContext ctx) {
        if (ctx.ID() != null && ctx.exp() == null && ctx.lvalue() == null) {
            return ctx.ID().getText();
        }
        if (ctx.lvalue() != null && ctx.exp() != null) {
            return null; // pensar implementação
        }
        if (ctx.lvalue() != null && ctx.ID() != null) {
            return null; // pensar implementação
        } 
        return null;
    }

	@Override public Object visitExps(langParser.ExpsContext ctx) { 
        List<Object> results = new ArrayList<>();
        if (ctx.exp(0) != null) {
            results.add(visitExp(ctx.exp(0)));
        }
        for (int i = 1; i < ctx.exp().size(); i++) {
            results.add(visitExp(ctx.exp(i)));
        }
        return results;
    }
}
