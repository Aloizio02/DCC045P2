package visitors.auxiliaryClasses;
import java.util.ArrayList;

public class Type {
    // Atributos da classe Type
    private String name; // guarda o nome do tipo
    private ArrayList<String> decls = new ArrayList<>(); // guarda o nome dos atributos daquele tipo

    // Construtor padrão
    public Type (String name) {
        this.name = name;
    }

    // Consultar quantidade de atributos
    public ArrayList<String> getAttributes() {
        return this.decls;
    }

    // Consultar o nome do tipo
    public String getName() {
        return this.name;
    }

    // Inserir novo atributo no tipo
    public void insertAttribute(String attr){
        decls.add(attr);
    }

    // Verificar se um determinado atributo é válido para o tipo
    public boolean validateAttribute(String attr) {
        return this.decls.contains(attr);
    }

    // Verificar se um determinado nome é válido para o nome do tipo
    public boolean validateName(String name){
        return this.name.equals(name);
    }
}