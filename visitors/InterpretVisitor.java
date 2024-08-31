package visitors;

import visitors.auxiliaryClasses.*;
import parser.langBaseVisitor;
import parser.langParser;
import java.util.*;

public class InterpretVisitor extends langBaseVisitor<Object> {
    // Pilha para manter o escopo atual das variáveis e funções
    private Stack<HashMap<String,HashMap<String,Object>>> env = new Stack<>();
    
    // HashMap para armazenar definições de funções, onde a chave é o nome da função e o valor é o escopo de variáveis daquela função
    private HashMap<String,HashMap<String,Object>> funcs = new HashMap<>();
    
    // HashMap para armazenar variáveis no escopo atual
    private HashMap<String,Object> vars = new HashMap<>();
    
    // HashMap para armazenar declarações de variáveis
    private HashMap<String,Object> declsVars = new HashMap<>();

    // Lista para armazenar os tipos de dados definidos no programa
    private ArrayList<Type> datas = new ArrayList<>();

    // Método principal que visita o nó "Prog" da árvore de análise sintática
    @Override
    public Object visitProg(langParser.ProgContext ctx) {
        // Verifica e processa declarações de dados se existirem
        if (ctx.data().size() > 0) {
            for (int i = 0; i < ctx.data().size(); i++) {
                visitData(ctx.data(i));
            }
        } 
        
        // Verifica e processa funções se existirem
        if (ctx.func().size() > 0) {
            for (int i = 0; i < ctx.func().size(); i++) {
                visitFunc(ctx.func(i));
            }
        }
        return null; 
    }

    // Visita o nó "Data" e processa o tipo de dado definido
    @Override 
    public Object visitData(langParser.DataContext ctx) {
        // Obtém o nome do tipo de dado e cria um novo objeto Type
        String nameData = ctx.TYPE().getText();
        Type type = new Type(nameData);
        datas.add(type);

        // Processa as declarações de atributos do tipo de dado se existirem
        if (ctx.decl().size() > 0) {
            for (int i = 0; i < ctx.decl().size(); i++){
                visitDecl(ctx.decl(i));
            }
        }
        return null;
    }

    // Visita o nó "Decl" e processa a declaração de um atributo para um tipo de dado
    @Override 
    public Object visitDecl(langParser.DeclContext ctx) {
        String attr = ctx.ID().getText();
        Type actData = datas.get(datas.size() - 1);
        datas.remove(datas.size() - 1);
        actData.insertAttribute(attr);
        datas.add(actData);
        return null; 
    }

    // Visita o nó "Func" e processa a definição de uma função
    @Override 
    public Object visitFunc(langParser.FuncContext ctx) {
        // Limpa o escopo atual de variáveis
        vars.clear();

        // Adiciona a função atual no escopo de funções
        funcs.put(ctx.ID().getText(), vars);

        // Empilha o escopo atual na pilha de escopos
        env.push(funcs);

        // Processa comandos dentro da função
        if (ctx.cmd().size() > 0) {
            for (int i = 0; i < ctx.cmd().size(); i++){
                visitCmd(ctx.cmd(i));
            }
        }
        return null;
    }

    // Visita o nó "Params" (parâmetros de funções), mas ainda não implementado
    @Override 
    public Object visitParams(langParser.ParamsContext ctx) { 
        // Preciso implementar
        return null;
    }

    @Override 
    public Object visitType(langParser.TypeContext ctx) { 
        // Se houver um tipo básico presente, visita o nó btype e retorna o valor correspondente
        if (ctx.btype() != null) {
            return visitBtype(ctx.btype());
        }
        return null;
    }
    
    @Override 
    public Object visitBtype(langParser.BtypeContext ctx) { 
        // Verifica se o texto inicial do contexto é "Int"
        if (ctx.getStart().getText().equals("Int")){
            return "Int";
        } 
        // Verifica se o texto inicial do contexto é "Float"
        else if (ctx.getStart().getText().equals("Float")) {
            return "Float";
        } 
        // Verifica se o texto inicial do contexto é "Bool"
        else if (ctx.getStart().getText().equals("Bool")) {
            return "Bool";
        } 
        // Verifica se o texto inicial do contexto é "Char"
        else if (ctx.getStart().getText().equals("Char")) {
            return "Char";
        } 
        // Se nenhum dos tipos primitivos anteriores corresponder, verifica se há um tipo definido pelo usuário (TYPE)
        else if (ctx.TYPE() != null) {
            return ctx.TYPE().getText();
        }
        return null;
    }

    @Override 
    public Object visitCmd(langParser.CmdContext ctx) {
        // Verifica se o comando é "print" e se há uma expressão para avaliar
        if (ctx.getStart().getText().equals("print") && ctx.exp() != null) {
            Object result = visitExp(ctx.exp(0));
            if (vars.get(result.toString()) == null){
                System.out.println(result);
            } else {
                System.out.println(vars.get(result.toString()));
            }
        } 
        // Verifica se o comando é um bloco de comandos (delimitado por chaves "{...}")
        else if (ctx.getStart().getText().equals("{")) {
            // Se o bloco contém comandos, visita cada um recursivamente
            if (ctx.cmd().size() > 0) {
                for (int i = 0; i < ctx.cmd().size(); i++) {
                    visitCmd(ctx.cmd(i));
                }
            }
        } 
        // Verifica se o comando é um "if" simples (sem 'else') e há apenas um comando a ser executado
        else if (ctx.getStart().getText().equals("if") && ctx.cmd().size() == 1){
            boolean result = (Boolean)visitExp(ctx.exp(0));
            if (result) {
                visitCmd(ctx.cmd(0));
            }
        } 
        // Verifica se o comando é um "if-else" com dois comandos (um para o "if" e outro para o "else")
        else if (ctx.getStart().getText().equals("if") && ctx.cmd().size() == 2) {
            boolean result = (Boolean)visitExp(ctx.exp(0));
            if (result) {
                visitCmd(ctx.cmd(0));
            } else {
                visitCmd(ctx.cmd(1));
            }
        } 
        // Verifica se o comando é "iterate" (um laço de repetição)
        else if (ctx.getStart().getText().equals("iterate") && ctx.exp() != null) {
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
        } 
        // Verifica se o comando é "read" (leitura de entrada do usuário) e lida com diferentes tipos de valores de entrada
        else if (ctx.getStart().getText().equals("read") && ctx.lvalue() != null) {
            String varName = ctx.lvalue(0).getText();
            // Se o nome da variável contém '.' e '[]', trata como um acesso a um array de um tipo
            if (varName.contains(".") && varName.contains("[")){
                String key  = getKey(funcs);
                Object value = receivesUserInput();
                validateSaveArrayTypes(varName, key, value);
            } 
            // Se o nome da variável contém '.', trata como um acesso a um atributo de um tipo
            else if (varName.contains(".")) {
                Object value = receivesUserInput();
                validateSaveTypes(varName, value);
            } 
            // Caso contrário, trata como uma variável simples
            else {
                Object value = receivesUserInput();
                String key  = getKey(funcs);
                vars.put(varName, value);
                funcs.put(key, vars);
                env.pop();
                env.push(funcs);
            }
        } 
        // Atribuição de valor a variáveis e tratamento de diferentes tipos de valores
        else if (ctx.lvalue() != null && ctx.exp() != null) {
            String left = ctx.lvalue(0).getText(); // Nome da variável à esquerda
            Object right = visitExp(ctx.exp(0)); // Valor da expressão à direita
            String key = getKey(funcs); // Obtém a chave do ambiente de funções
            
            // Se o nome da variável contém '[]', trata como um acesso a array
            if (left.contains("[")) {
                String keyVar = left.substring(0, left.indexOf("["));
                int index = Integer.parseInt(left.substring(left.indexOf("[") + 1, left.indexOf("]")));
                Object varAux = vars.get(keyVar);
                // Tratamento específico para arrays de diferentes tipos (int, float, boolean, char)
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
                } else {
                    validateSaveArrayTypes(left, key, right); // Valida e salva arrays de tipo
                }
            } 
            // Se o nome da variável contém '.', trata como um acesso a um atributo de um tipo
            else if (left.contains(".")) {
                validateSaveTypes(left, right); // Valida e salva tipos
            } 
            // Caso contrário, trata como uma variável simples ou o instanciamento de um novo tipo ou array de tipo
            else {
                if (right != null && searchType(right.toString()) != null){
                    Type auxData = searchType(right.toString());
                    HashMap<String,Object> attr = new HashMap<>();
                    for (int i = 0; i < auxData.getAttributes().size(); i++){
                        attr.put(auxData.getAttributes().get(i), null);
                    }
                    vars.put(left, attr);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                } else if (right instanceof HashMap<?,?>[]){
                    @SuppressWarnings("unchecked")
                    HashMap<String,Object>[] auxRight = (HashMap<String,Object>[]) right;
                    vars.put(left, auxRight);
                    funcs.put(key, vars);
                    env.pop();
                    env.push(funcs);
                } else {
                    vars.put(left, right);
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
    
            // Se houver mais de um filho, assume que é uma operação "and" lógico
            if (ctx.getChildCount() > 1 && left instanceof Boolean) {
                return ((Boolean) left).booleanValue() && ((Boolean) right).booleanValue();
            } else if (!(left instanceof String) && !(right instanceof String)){
                return (left != null && right != null);
            }
        } else if (ctx.rexp() != null) {
            // Se a expressão é uma expressão relacional, visita a regra correspondente
            return visitRexp(ctx.rexp());
        }
        return null;
    }
    
    @Override 
    public Object visitRexp(langParser.RexpContext ctx) {
        if (ctx.rexp() != null && ctx.aexp() != null) {
            Object left  = visitRexp(ctx.rexp());
            Object right = visitAexp(ctx.aexp());
    
            // Verifica se o valor da variável está no mapa 'vars' e substitui pelo valor real
            if (vars.get(left.toString()) != null){
                left = vars.get(left.toString());
            }
            if (vars.get(right.toString()) != null){
                right = vars.get(right.toString());
            }
    
            // Realiza operações relacionais de comparação
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
            // Se é uma expressão aritmética, visita a regra correspondente
            return visitAexp(ctx.aexp());
        }
        return null;
    }
    
    @Override 
    public Object visitAexp(langParser.AexpContext ctx) {
        if (ctx.aexp() != null && ctx.mexp() != null) {
            Object left = visitAexp(ctx.aexp());
            Object right = visitMexp(ctx.mexp());
    
            // Substitui as variáveis pelos seus valores reais se estiverem no mapa 'vars'
            if (vars.get(left.toString()) != null){
                left = vars.get(left.toString());
            }
            if (vars.get(right.toString()) != null){
                right = vars.get(right.toString());
            }
    
            // Realiza operações aritméticas de soma ou subtração
            if (ctx.getChildCount() > 1) {
                if (ctx.getChild(1).getText().equals("+")) {
                    return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                } else if (ctx.getChild(1).getText().equals("-")) {
                    return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                }
            }
        } else if (ctx.mexp() != null) {
            // Se é uma expressão de multiplicação, divisão ou cálculo de resto, visita a regra correspondente
            return visitMexp(ctx.mexp());
        }
        return null;
    }
    
    @Override 
    public Object visitMexp(langParser.MexpContext ctx) {
        if (ctx.mexp() != null && ctx.sexp() != null) {
            Object left = visitMexp(ctx.mexp());
            Object right = visitSexp(ctx.sexp());
    
            // Substitui as variáveis pelos seus valores reais se estiverem no mapa 'vars'
            if (vars.get(left.toString()) != null){
                left = vars.get(left.toString());
            }
            if (vars.get(right.toString()) != null){
                right = vars.get(right.toString());
            }
    
            // Realiza operações de multiplicação, divisão ou módulo
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
            // Se não é uma expressão de multiplicação, visita a expressão simples
            return visitSexp(ctx.sexp());
        }
        return null;
    }
    
    @Override 
    public Object visitSexp(langParser.SexpContext ctx) {
        // Verifica se o token é um inteiro
        if (ctx.INT() != null) {
            return Integer.parseInt(ctx.INT().getText());
        } 
        // Verifica se o token é um float
        else if (ctx.FLOAT() != null) {
            return Float.parseFloat(ctx.FLOAT().getText());
        } 
        // Verifica se o token é um booleano
        else if (ctx.BOOL() != null) {
            return Boolean.parseBoolean(ctx.BOOL().getText());
        } 
        // Verifica se o token é nulo
        else if (ctx.NULL() != null) {
            return null;
        } 
        // Verifica se o token é um caractere
        else if (ctx.CHAR() != null) {
            return ctx.CHAR().getText().charAt(1);
        } 
        // Verifica se o token é um operador de negação
        else if (ctx.getStart().getText().equals("!") && ctx.pexp() != null) {
            boolean result = (Boolean)visitPexp(ctx.pexp());
            return !result;
        } 
        // Se for uma expressão primária, visita a regra correspondente
        else if (ctx.pexp() != null) {
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
            if (ctx.exp() != null) {
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
                    } else {
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object>[] arrayHashMaps = (HashMap<String, Object>[]) new HashMap[(Integer) size];
                        ArrayList<String> auxAttr = new ArrayList<>();
                        // Obtém os atributos para o typeName específico
                        for (int i = 0; i < datas.size(); i++) {
                            if (datas.get(i).getName().equals(typeName)) {
                                auxAttr = datas.get(i).getAttributes();                               
                            }
                        }
                        // Inicializa cada HashMap dentro do array e adiciona os atributos
                        for (int i = 0; i < arrayHashMaps.length; i++) {
                            // Inicializa o HashMap na posição 'i'
                            arrayHashMaps[i] = new HashMap<>();
                            HashMap<String, Object> auxAttrVal = new HashMap<>();
                            // Preenche o HashMap com os atributos e valores nulos
                            for (int j = 0; j < auxAttr.size(); j++) {
                                auxAttrVal.put(auxAttr.get(j), null);
                            }
                            // Coloca o HashMap de atributos no HashMap principal
                            arrayHashMaps[i].put(typeName, auxAttrVal);
                        }
                        return arrayHashMaps;
                    }
                }
            } else {
                for (int i = 0; i < datas.size(); i++){
                    if (datas.get(i).getName().equals(typeName)){
                        return typeName;
                    }
                }
            }
        } else if (ctx.ID() != null) {
            return null;
        }
        return null;
    }

    @Override 
    public Object visitLvalue(langParser.LvalueContext ctx) {
        // Se o contexto 'lvalue' não é nulo e há um identificador, retorna o nome do identificador
        if (ctx.lvalue() == null && ctx.ID() != null) {
            return ctx.ID().getText();
        } 
        // Se o contexto 'lvalue' e uma expressão estão presentes, processa o acesso a elementos de um array
        else if (ctx.lvalue() != null && ctx.exp() != null) {
            Object varAux = vars.get(ctx.lvalue().getText());
            
            // Verifica se o 'lvalue' é um array de inteiros e acessa o elemento do índice especificado
            if (varAux instanceof int[]) {
                int[] aux = (int []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())] != 0){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            } 
            // Verifica se o 'lvalue' é um array de floats e acessa o elemento do índice especificado
            else if (varAux instanceof float[]) {
                float[] aux = (float []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())] != 0){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            } 
            // Verifica se o 'lvalue' é um array de booleanos e acessa o elemento do índice especificado
            else if (varAux instanceof boolean[]){
                boolean[] aux = (boolean []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())]){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            } 
            // Verifica se o 'lvalue' é um array de caracteres e acessa o elemento do índice especificado
            else if (varAux instanceof char[]) {
                char[] aux = (char []) varAux;
                if (aux[Integer.parseInt(ctx.exp().getText())] != '\0'){
                    return aux[Integer.parseInt(ctx.exp().getText())];
                }
            }
            // Retorna o 'lvalue' como uma expressão de acesso a um array de outro tipo
            return ctx.lvalue().getText() + "[" + ctx.exp().getText() + "]";
        } 
        // Se o contexto 'lvalue' e um identificador estão presentes, processa o acesso a um campo de um tipo
        else if (ctx.lvalue() != null && ctx.ID() != null) {
            if (vars.get(ctx.lvalue().getText()) != null){
                @SuppressWarnings("unchecked")
                HashMap<String,Object> auxAtt = (HashMap<String,Object>)vars.get(ctx.lvalue().getText());
                if (auxAtt.get(ctx.ID().getText()) != null){
                    return auxAtt.get(ctx.ID().getText());
                }
            } 
            // Processa o acesso a um elemento de um array de hashmaps, verificando o índice e a chave correta
            else if(ctx.lvalue().getText().contains("[")){
                // Extrai o nome da variável do 'lvalue' (antes do índice)
                String varName = ctx.lvalue().getText().substring(0, ctx.lvalue().getText().indexOf("["));
                
                // Recupera o array de mapas associado ao nome da variável
                @SuppressWarnings("unchecked")
                HashMap<String,Object>[] auxArrayMap = (HashMap<String,Object>[]) vars.get(varName);
                
                // Obtém o índice do array a partir do 'lvalue'
                int index = Integer.parseInt(ctx.lvalue().getText().substring(ctx.lvalue().getText().indexOf("[") + 1, ctx.lvalue().getText().indexOf("]")));
                
                // Obtém a chave correspondente no array de mapas
                String key = getKeyArray(auxArrayMap, index);
                
                // Recupera o mapa no índice especificado e retorna o valor associado ao identificador
                @SuppressWarnings("unchecked")
                HashMap<String,Object> mapResult = (HashMap<String,Object>) auxArrayMap[index].get(key);
                return mapResult.get(ctx.ID().getText());
            }
        }
        return null;
    }
    
    @Override 
    public Object visitExps(langParser.ExpsContext ctx) { 
        List<Object> results = new ArrayList<>();
        
        // Verifica se a primeira expressão não é nula e a visita.
        if (ctx.exp(0) != null) {
            results.add(visitExp(ctx.exp(0)));
        }
        
        // Itera sobre as demais expressões, visita cada uma delas e adiciona os resultados à lista.
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

    // Função para consultar a chave de um vetor de hashmaps
    public String getKeyArray(HashMap <String,Object>[] map, int index) {
        if (index >= 0 && index < map.length){
            HashMap<String, Object> mapIndex = map[index];
            if (mapIndex != null && !mapIndex.isEmpty()) {
                Set<String> keys = mapIndex.keySet();
                return keys.iterator().next();
            }
        }
        return null;
    }

    // Função para ler entrada do usuário e converter para uma das possíveis entradas da linguagem (int, float ou boolean)
    public Object receivesUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("");
        String value = scanner.nextLine();
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

    // Função para validar e salvar valores usando tipos
    public void validateSaveTypes(String varDecl, Object value){
        String [] auxVarDecl = varDecl.split("\\.");
        String varName = auxVarDecl[0];
        String declName = auxVarDecl[1];
        String key = getKey(funcs);
        @SuppressWarnings("unchecked")
        HashMap<String,Object> auxAtt = (HashMap<String,Object>)vars.get(varName);
        auxAtt.put(declName, value);
        vars.put(varName, auxAtt);
        funcs.put(key, vars);
        env.pop();
        env.push(funcs);
    }

    // Função para validar e salvar valores usando vetores de tipos
    public void validateSaveArrayTypes(String varAttr, String key, Object value){
        String varName = varAttr.substring(0, varAttr.indexOf("["));
        @SuppressWarnings("unchecked")
        HashMap<String,Object>[] auxArrayMap = (HashMap<String,Object>[]) vars.get(varName);
        int index = Integer.parseInt(varAttr.substring(varAttr.indexOf("[") + 1, varAttr.indexOf("]")));
        String nameType = getKeyArray(auxArrayMap, index);
        @SuppressWarnings("unchecked")
        HashMap<String,Object> mapResult = (HashMap<String,Object>) auxArrayMap[index].get(nameType);
        String attr = varAttr.substring(varAttr.indexOf(".") + 1, varAttr.length());
        mapResult.put(attr, value);
        auxArrayMap[index].put(nameType, mapResult);
        vars.put(varName, auxArrayMap);
        funcs.put(key, vars);
        env.pop();
        env.push(funcs);
    }

    // Procura e retorna um tipo a partir do seu nome
    public Type searchType(String name) {
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).validateName(name)){
                    return datas.get(i);
                }
            }
        }
        return null;
    }
}