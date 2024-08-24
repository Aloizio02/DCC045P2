package visitor;

import parser.langBaseVisitor;
import parser.langParser;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Visitor extends langBaseVisitor<Object> {
    private Stack<HashMap<String,HashMap<String,Object>>> env = new Stack<>();
    private HashMap<String,HashMap<String,Object>> funcs = new HashMap<>();
    private HashMap<String,Object> vars = new HashMap<>();
    private HashMap<String,HashMap<String,Object>> datas = new HashMap<>();
    private HashMap<String,Object> decls = new HashMap<>();

	@Override
    public Object visitProg(langParser.ProgContext ctx) {
        if (ctx.data().size() > 0) {
            for (int i = 0; i < ctx.data().size(); i++) {
                visitData(ctx.data(i));
            }
        } 
        
        if (ctx.func().size() > 0) {
            for (int i = 0; i < ctx.func().size(); i++) {
                visitFunc(ctx.func(i));
            }
        }
        return null; 
    }

	@Override 
    public Object visitData(langParser.DataContext ctx) {
        decls.clear();
        datas.put(ctx.TYPE().getText(), decls);
        if (ctx.decl().size() > 0) {
            for (int i = 0; i < ctx.decl().size(); i++){
                visitDecl(ctx.decl(i));
            }
        }
        return null;
    }

	@Override 
    public Object visitDecl(langParser.DeclContext ctx) { 
        String data = ctx.ID().getText();
        Object decl = visitType(ctx.type());
        decls.put(data, decl);
        String key = getKey(datas);
        datas.put(key, decls);
        return null; 
    }

	@Override 
    public Object visitFunc(langParser.FuncContext ctx) {
        vars.clear();
        funcs.put(ctx.ID().getText(), vars);
        env.push(funcs);

        if (ctx.cmd().size() > 0) {
            for (int i = 0; i < ctx.cmd().size(); i++){
                visitCmd(ctx.cmd(i));
            }
        }
        return null;
    }

	@Override 
    public Object visitParams(langParser.ParamsContext ctx) { 
        
        return null;
    }

	@Override 
    public Object visitType(langParser.TypeContext ctx) { 
        if (ctx.btype() != null) {
            return visitBtype(ctx.btype());
        }
        return null;
    }

	@Override 
    public Object visitBtype(langParser.BtypeContext ctx) { 
        if (ctx.getStart().getText().equals("Int")){
            return "Int";
        } else if (ctx.getStart().getText().equals("Float")) {
            return "Float";
        } else if (ctx.getStart().getText().equals("Bool")) {
            return "Bool";
        } else if (ctx.getStart().getText().equals("Char")) {
            return "Char";
        } else if (ctx.TYPE() != null) {
            return ctx.TYPE().getText();
        }
        return null;
    }

	@Override 
    public Object visitCmd(langParser.CmdContext ctx) {
        if (ctx.getStart().getText().equals("print") && ctx.exp() != null) {
            Object result = visitExp(ctx.exp(0));
            if (vars.get(result.toString()) == null){
                System.out.println(result);
            } else {
                System.out.println(vars.get(result.toString()));
            }
        } else if (ctx.getStart().getText().equals("{")) {
            if (ctx.cmd().size() > 0) {
                for (int i = 0; i < ctx.cmd().size(); i++) {
                    visitCmd(ctx.cmd(i));
                }
            }
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
        } else if (ctx.getStart().getText().equals("iterate") && ctx.exp() != null) {
            Object exp = visitExp(ctx.exp(0));
            if (vars.get(exp.toString()) == null){
                int auxExp = Integer.parseInt(exp.toString());
                for(int i = 0; i < auxExp; i++){
                    visitCmd(ctx.cmd(0));
                }
            } else {
                String auxExp = vars.get(exp.toString()).toString();
                for(int i = 0; i < Integer.parseInt(auxExp); i++){
                    visitCmd(ctx.cmd(0));
                }
            }
        } else if (ctx.getStart().getText().equals("read") && ctx.lvalue() != null) {
            String varName = visitLvalue(ctx.lvalue(0)).toString();
            Object value = receivesUserInput();
            String key  = getKey(funcs);
            vars.put(varName, value);
            funcs.put(key, vars);
            env.pop();
            env.push(funcs);
        } else if (ctx.lvalue() != null && ctx.exp() != null) {
            String left = visitLvalue(ctx.lvalue(0)).toString();
            Object right = visitExp(ctx.exp(0));
            String key = getKey(funcs);
            if (left.contains("[")) {
                String keyVar = left.substring(0, left.indexOf("["));
                int index = Integer.parseInt(left.substring(left.indexOf("[") + 1, left.indexOf("]")));
                Object varAux = vars.get(keyVar);
                if (varAux instanceof int[]) {
                    int[] vectAux = (int[]) varAux;
                    vectAux[index] = Integer.parseInt(right.toString());
                    vars.put(keyVar, vectAux);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                } else if (varAux instanceof float[]) {
                    float[] vectAux = (float[]) varAux;
                    vectAux[index] = Float.parseFloat(right.toString());
                    vars.put(keyVar, vectAux);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                } else if (varAux instanceof boolean[]) {
                    boolean[] vectAux = (boolean[]) varAux;
                    vectAux[index] = Boolean.parseBoolean(right.toString());
                    vars.put(keyVar, vectAux);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                } else if (varAux instanceof char[]) {
                    char[] vectAux = (char[]) varAux;
                    vectAux[index] = right.toString().charAt(0);
                    vars.put(keyVar, vectAux);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                }
            } else if (left.contains(".")) {
                // implementar o caso de tipo
                System.out.println(left);
            } else {
                if (vars.get(right.toString()) == null){
                    vars.put(left, right);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                } else {
                    vars.put(left, vars.get(right.toString()));
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                }
            }
        }
        return null;
    }

	@Override 
    public Object visitExp(langParser.ExpContext ctx) {
        if (ctx.exp().size() > 0) {
            Object left = visitExp(ctx.exp(0));
            Object right = visitExp(ctx.exp(1));

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

            if (vars.get(left.toString()) != null){
                left = vars.get(left.toString());
            }
            if (vars.get(right.toString()) != null){
                right = vars.get(right.toString());
            }
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

            if (vars.get(left.toString()) != null){
                left = vars.get(left.toString());
            }
            if (vars.get(right.toString()) != null){
                right = vars.get(right.toString());
            }
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
            if (vars.get(left.toString()) != null){
                left = vars.get(left.toString());
            }
            if (vars.get(right.toString()) != null){
                right = vars.get(right.toString());
            }
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
        } else if (ctx.FLOAT() != null) {
            return Float.parseFloat(ctx.FLOAT().getText());
        } else if (ctx.BOOL() != null) {
            return Boolean.parseBoolean(ctx.BOOL().getText());
        } else if (ctx.NULL() != null) {
            return null;
        } else if (ctx.CHAR() != null) {
            return ctx.CHAR().getText().charAt(1);
        } else if (ctx.getStart().getText().equals("!") && ctx.pexp() != null) {
            boolean result = (Boolean)visitPexp(ctx.pexp());
            return !result;
        } else if (ctx.pexp() != null) {
            return visitPexp(ctx.pexp());
        }
        // pensar na necessidade de implementar o menos unário (regra '| '-' pexp' em sexp)
        return null;
    }

	@Override 
    public Object visitPexp(langParser.PexpContext ctx) { 
        if (ctx.lvalue() != null){
            return visitLvalue(ctx.lvalue());
        } else if (ctx.type() == null && ctx.ID() == null && ctx.exp() != null) {
            return visitExp(ctx.exp());
        } else if (ctx.type() != null) {
            String typeName = visitType(ctx.type()).toString();
            if (datas.get(typeName) == null) {
                Object size = visitExp(ctx.exp());
                if (size instanceof Integer){
                    if (typeName.equals("Int")) {
                        return new int[(Integer) size];
                    } else if (typeName.equals("Float")) {
                        return new float[(Integer) size];
                    } else if (typeName.equals("Char")) {
                        return new char[(Integer) size];
                    } else if (typeName.equals("Bool")) {
                        return new boolean[(Integer) size];
                    }
                }
            } else {
                return typeName;
            }
        } else if (ctx.ID() != null) {
            return null;
        }
        return null;
    }

	@Override 
    public Object visitLvalue(langParser.LvalueContext ctx) {
        if (ctx.lvalue() == null && ctx.ID() != null) {
            return ctx.ID().getText();
        } else if (ctx.lvalue() != null && ctx.exp() != null) {
            Object varAux = vars.get(ctx.lvalue().getText());
            if (varAux instanceof int[]) {
                int[] aux = (int []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())] != 0){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            } else if (varAux instanceof float[]) {
                float[] aux = (float []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())] != 0){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            } else if (varAux instanceof boolean[]){
                boolean[] aux = (boolean []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())]){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            } else if (varAux instanceof char[]) {
                char[] aux = (char []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())] != '\0'){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            }
            return ctx.lvalue().getText() + "[" + ctx.exp().getText() + "]";
        } else if (ctx.lvalue() != null && ctx.ID() != null) {
            return ctx.lvalue().getText() + "." + ctx.ID().getText();
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

    // Função para retornar a chave da Hash 'funcs' (será útil na manipulação da Hash e pilha)
    public String getKey(HashMap<String, HashMap<String, Object>> map) {
        if (map != null && !map.isEmpty()) {
            return map.keySet().iterator().next();
        }
        return null;
    }

    // Função para ler entrada do usuário e converter para uma das possíveis entradas da linguagem (int, float ou boolean)
    public Object receivesUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("");
        String value = scanner.nextLine();
        scanner.close();
        try {
            int intInput = Integer.parseInt(value);
            return intInput;
        } catch (NumberFormatException e1) {
            try {
                float floatInput = Float.parseFloat(value);
                return floatInput;
            } catch (NumberFormatException e2) {
                if (value.equals("true") ||value.equals("false")) {
                    boolean boolInput = Boolean.parseBoolean(value);
                    return boolInput;
                } else {
                    System.out.println("Entrada informada pelo usuario foi invalida!");
                    return null;
                }
            }
        }
    }
}