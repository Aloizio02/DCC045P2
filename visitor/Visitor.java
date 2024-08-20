package visitor;

import parser.langBaseVisitor;
import parser.langParser;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Visitor extends langBaseVisitor<Object> {
    private Stack<HashMap<String,HashMap<String,Object>>> env = new Stack<>();
    private HashMap<String,HashMap<String,Object>> funcs = new HashMap<>();
    private HashMap<String,Object> vars = new HashMap<>();

	@Override
    public Object visitProg(langParser.ProgContext ctx) {
        if (ctx.data(0) != null) {
            return visitData(ctx.data(0));
        } 
        if (ctx.func(0) != null) {
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
        HashMap<String, Object> varAux = new HashMap<>();
        funcs.put(ctx.ID().getText(), varAux);
        env.push(funcs);

        if (ctx.cmd().size() > 0) {
            for (int i = 0; i < ctx.cmd().size(); i++){
                visitCmd(ctx.cmd(i));
            }
        }

        /*
        //HashMap<String, Object> varAux = new HashMap<>();
        //funcs.put(ctx.ID().getText(), varAux);
        //env.push(funcs);
        if (ctx.params(). == 0 && ctx.type().size() == 0) {
            if (ctx.cmd() != null) {
                for (int i = 0; i < ctx.cmd().size(); i++) {
                    return visitCmd(ctx.cmd(i));
                }
            }
        }
        HashMap<String,Object> operands = new HashMap<>();
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
        //System.out.println(ctx.cmd().size());
        if (ctx.getStart().getText().equals("print") && ctx.exp() != null) {
            Object result = visitExp(ctx.exp(0));
            System.out.println(result);
        } else if (ctx.getStart().getText().equals("if") && ctx.cmd().size() == 1){
            boolean result = (Boolean)visitExp(ctx.exp(0));
            if (result) {
                visitCmd(ctx.cmd(0));
            }
        } else if (ctx.getStart().getText().equals("if") && ctx.cmd().size() == 2) {
            boolean result = (Boolean)visitExp(ctx.exp(0));
            if (result) {
                visitCmd(ctx.cmd(0));
            } else {
                visitCmd(ctx.cmd(1));
            }
        } else if (ctx.lvalue() != null && ctx.exp() != null) {
            String left = visitLvalue(ctx.lvalue(0)).toString();
            Object right = visitExp(ctx.exp(0));
            String key = getKey(funcs);
            vars.put(left, right);
            funcs.put(key, vars);
            env.pop();
            env.push(funcs);
        }
        return null;
    }

	@Override 
    public Object visitExp(langParser.ExpContext ctx) {
        if (ctx.exp().size() > 0) {
            Object left = visitExp(ctx.exp(0));
            Object right = visitExp(ctx.exp(1));

            String operator = ctx.getStart().getText();
            if (ctx.getChildCount() > 1) {
                return ((Boolean) left).booleanValue() && ((Boolean) right).booleanValue();
            }
        } else if (ctx.rexp() != null) {
            return visitRexp(ctx.rexp());
        }
        return null;
    }

	@Override 
    public Object visitRexp(langParser.RexpContext ctx) {
        if (ctx.rexp() != null && ctx.aexp() != null) {
            Object left  = visitRexp(ctx.rexp());
            Object right = visitAexp(ctx.aexp());

            String operator = ctx.getStart().getText();
            if (ctx.getChildCount() > 1) {
                if (ctx.getChild(1).getText().equals("<")) {
                    return ((Number) left).doubleValue() < ((Number) right).doubleValue();
                } else if (ctx.getChild(1).getText().equals("==")) {
                    return ((Number) left).doubleValue() == ((Number) right).doubleValue();
                } else if (ctx.getChild(1).getText().equals("!=")) {
                    return ((Number) left).doubleValue() != ((Number) right).doubleValue();
                }
            }
        } else if (ctx.aexp() != null) {
            return visitAexp(ctx.aexp());
        }
        return null;
    }

	@Override 
    public Object visitAexp(langParser.AexpContext ctx) {
        if (ctx.aexp() != null && ctx.mexp() != null) {
            Object left = visitAexp(ctx.aexp());
            Object right = visitMexp(ctx.mexp());

            String operator = ctx.getStart().getText();
            if (ctx.getChildCount() > 1) {
                if (ctx.getChild(1).getText().equals("+")) {
                    return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                } else if (ctx.getChild(1).getText().equals("-")) {
                    return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                }
            }
        } else if (ctx.mexp() != null) {
            return visitMexp(ctx.mexp());
        }
        return null;
    }

	@Override 
    public Object visitMexp(langParser.MexpContext ctx) {
        if (ctx.mexp() != null && ctx.sexp() != null) {
            Object left = visitMexp(ctx.mexp());
            Object right = visitSexp(ctx.sexp());
            String operator = ctx.getStart().getText();
            if (ctx.getChildCount() > 1) {
                if (ctx.getChild(1).getText().equals("*")) {
                    return ((Number) left).doubleValue() * ((Number) right).doubleValue();
                } else if (ctx.getChild(1).getText().equals("/")) {
                    return ((Number) left).doubleValue() / ((Number) right).doubleValue();
                } else if (ctx.getChild(1).getText().equals("%")) {
                    return ((Number) left).doubleValue() % ((Number) right).doubleValue();
                }
            }
        } else if (ctx.mexp() == null) {
            return visitSexp(ctx.sexp());
        }
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
                if (typeName.equals("Int")) {
                    return new int[(Integer) size];
                }
                if (typeName.equals("Float")) {
                    return new float[(Integer) size];
                }
                if (typeName.equals("Char")) {
                    return new char[(Integer) size];
                }
                if (typeName.equals("Bool")) {
                    return new boolean[(Integer) size];
                } else {

                }
            }
            return null;
        }
        if (ctx.exp() != null && ctx.type() == null) {
            return visitExp(ctx.exp());
        }
        if (ctx.ID() != null) {
            return null;
        }
        return null;
    }

	@Override 
    public Object visitLvalue(langParser.LvalueContext ctx) {
        if (ctx.ID() != null) {
            if(vars.get(ctx.ID().getText()) == null){
                return ctx.ID().getText();
            }
            return vars.get(ctx.ID().getText());
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

    public String getKey(HashMap<String, HashMap<String, Object>> map) {
        if (map != null && !map.isEmpty()) {
            return map.keySet().iterator().next();
        }
        return null;
    }
}