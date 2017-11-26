import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Simple ClassData class for easy XMLencoding and XMLdecoding
 */
public class ClassData implements Serializable{

    private String name;
    private String superClass;
    private ArrayList<String> interfaces;
    private ArrayList<String> fields;
    private ArrayList<String> methods;
    private ArrayList<String> providers;
    private ArrayList<String> clients;


    public ClassData(){
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSuperClass(){
        return superClass;
    }

    public void setSuperClass(String superClass){
        this.superClass = superClass;
    }

    public ArrayList<String> getInterfaces(){
        return interfaces;
    }

    public void setInterfaces(ArrayList<String> interfaces){
        this.interfaces = interfaces;
    }

    public ArrayList<String> getFields(){
        return fields;
    }

    public void setFields(ArrayList<String> fields){
        this.fields = fields;
    }

    public ArrayList<String> getMethods(){
        return methods;
    }

    public void setMethods(ArrayList<String> methods){
        this.methods = methods;
    }

    public ArrayList<String> getProviders(){
        return providers;
    }

    public void setProviders(ArrayList<String> providers){
        this.providers = providers;
    }

    public ArrayList<String> getClients(){
        return clients;
    }

    public void setClients(ArrayList<String> clients){
        this.clients = clients;
    }

    @Override
    public String toString(){
        String str = "\nName: " + getName();
        str += "\nSuperclass: " + getSuperClass();
        str += "\nInterfaces: ";
        for (String entry : getInterfaces()) {
            str += "\n\t" + entry;
        }
        str += "\nFields: ";
        for (String entry : getFields()) {
            str += "\n\t" + entry;
        }
        str += "\nMethods: ";
        for (String entry : getMethods()) {
            str += "\n\t" + entry;
        }
        str += "\nProviders: ";
        for (String entry : getProviders()) {
            str += "\n\t" + entry;
        }
        str += "\nClients: ";
        for (String entry : getClients()) {
            str += "\n\t" + entry;
        }

        return str;
    }

}