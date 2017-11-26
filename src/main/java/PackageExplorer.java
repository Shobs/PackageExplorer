import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Integer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * =========Package Explorer=========
 * Simple program that allows you to get information about
 * classes in a directory.
 */
public class PackageExplorer{

    private static ArrayList<String> argsList = new ArrayList<String>();
    private static ArrayList<Option> optsList = new ArrayList<Option>();
    private static ArrayList<String> doubleOptsList = new ArrayList<String>();


    public static void main(String[] args) throws IOException {

        getOpts(args);

        // Getting current Directory or desired directory
        final File DIR = ((argsList.size() > 0) ? new File(argsList.get(0)): new File(System.getProperty("user.dir")));

        String intro = "\n Welcome to PackageExplorer - ";
        String menu = "Main Menu\n--------------------------------------\n1. List all classes\n2. View a class\n3. Save all classes\n4. Load class info from xml(Extra Credit)\n\nEnter your choice (1-4) or q to quit: ";

        String nav = "\nEnter s to save or m for Main Menu: \n";

        Console con = System.console();

        if (con == null) {
            System.err.println("No console.");
            System.exit(1);
        }

        File[] classes = loadFiles(DIR, ".class");
        File[] XMLs = loadFiles(DIR, ".xml");

        // Loading all classes and their data to hashmap
        HashMap<String, ClassData> hm = saveData(classes);

        String file = "";

        String input = con.readLine(intro + menu);

        // General navigation of program
        while(!input.equals("q")){

            switch(input){
                case "1": // 1. List all classes
                System.out.println("\nList of all the classes present in:\n\t" + DIR);
                listClasses(hm);
                input = con.readLine("\n" + menu);
                break;

                case "2": // 2. View a class
                file = con.readLine("Enter file number: ");
                System.out.println(hm.get(file).toString());
                input = con.readLine(nav);
                break;

                case "3": // 3. Save all classes
                dirToXML(hm, DIR);
                System.out.println("Files saved at:\n\t " + DIR + File.separator + DIR.getName() + ".xml");
                input = con.readLine("\n" + menu);
                break;

                case "4": // 4. Load class info from xml(Extra Credit)
                System.out.println("\nList of all the xml's present in:\n\t " + DIR);
                listXMLs(XMLs);
                file = con.readLine("Enter file number: ");
                System.out.println(file);
                hm = XMLTodir(hm, XMLs[Integer.parseInt(file)-1]);
                System.out.println("\nXML has been loaded!");
                input = con.readLine("\n" + menu);
                break;

                case "s": // Save current class
                classToXML(file, hm, DIR);
                System.out.println("File saved at: \n\t " + DIR + File.separator + hm.get(file).getName() + ".xml");
                input = con.readLine("\n" + menu);
                break;

                case "m": // return to main menu
                input = con.readLine("\n" + menu);
                break;

                default: // display main menu if invalid input
                input = con.readLine("\n" + menu);
                break;
            }
        }
    }

    /**
     * Returns a array of files from desired directory
     * and with desired extension
     * @param  DIR [requested directory]
     * @param  ext [requested file extension]
     * @return     [Array of files]
     */
    private static File[] loadFiles(File DIR, String ext) {

        return DIR.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(ext)
                && !pathname.isDirectory();
            }
        });
    }

    /**
     * Saves files data to ClassData for easy XMLencoding and
     * decoding
     * @param  files [files to be saved as a ClassData object]
     * @return       [HashMap containing all ClassData object]
     */
    private static HashMap saveData(File[] files){

        HashMap<String, ClassData> hm = new HashMap();
        int i = 1;
        try {
            for ( File entry : files ) {
                Class c = Class.forName(entry.getName().replace(".class", ""));
                ClassData cd = new ClassData();

                cd.setName(c.getName());
                cd.setSuperClass(c.getSuperclass().getName());
                cd.setInterfaces(setInterfaces(c.getInterfaces()));
                cd.setFields(setFields(c.getDeclaredFields()));
                cd.setMethods(setMethods(c.getDeclaredMethods()));
                cd.setProviders(setProviders(c.getDeclaredFields(), files));
                cd.setClients(setClients(c, files));

                hm.put(Integer.toString(i), cd);
                i++;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return hm;
    }

    /**
     * Lists all the classes contained in HashMap
     * @param hm [hash map containing ClassData objects]
     */
    private static void listClasses(HashMap<String, ClassData> hm){
        for(Map.Entry<String, ClassData> entry : hm.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue().getName());
        }
        System.out.println("");
    }

    /**
     * Lists all the files contained in XML file array
     * @param f [XML file array]
     */
    private static void listXMLs(File[] f){
        int i = 1;
        for(File entry : f) {
            System.out.println(i + ". " + entry.getName());
            i++;
        }
        System.out.println("");
    }

    /**
     * Converts a ClassData object to and XML file
     * @param index [Desired ClassData index in HashMap]
     * @param hm    [HashMap containing all the ClassData object]
     * @param DIR   [Directory where XML file is saved]
     */
    private static void classToXML(String index, HashMap<String, ClassData> hm, File DIR){

        try{
            FileOutputStream output = new FileOutputStream(DIR + File.separator + hm.get(index).getName() + ".xml");
            final XMLEncoder encode = new XMLEncoder(new BufferedOutputStream(output));

            encode.writeObject(hm.get(index));
            encode.close();

        } catch(Exception e){
            System.out.println(e);
        }

    }

    /**
     * Converts HashMap of ClassData into an XML file
     * @param hm  [HashMap to be converted]
     * @param DIR [Directory file]
     */
    private static void dirToXML(HashMap<String, ClassData> hm, File DIR){

        try{
            FileOutputStream output = new FileOutputStream(DIR + File.separator + DIR.getName() + ".xml");
            final XMLEncoder encode = new XMLEncoder(new BufferedOutputStream(output));

            encode.writeObject(hm);
            encode.close();

        } catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Converts an XML into a HashMap of ClassData
     * @param  hm [HashMap where ClassData will be added]
     * @param  f  [Name of XML file to be converted]
     * @return    [HashMap of converted ClassData Object]
     */
    private static HashMap<String, ClassData> XMLTodir(HashMap<String, ClassData> hm, File f){

        try{

            final XMLDecoder decode = new XMLDecoder(
              new BufferedInputStream(
                  new FileInputStream(f.getName())));
            Object result = decode.readObject();
            decode.close();

            // if it's a HashMap override existing
            // otherwise clear existing and put a class
            if (result instanceof HashMap<?, ?>) {
                System.out.println("Its a match");
                hm = (HashMap<String, ClassData>) result;
            }else{
                hm.clear();
                hm.put("1", (ClassData) result);
            }

        } catch(Exception e){
            System.out.println(e);
        }

        return hm;
    }

    /**
     * Setter for interfaces
     * @param  c [Classes to be added to ClassData]
     * @return   [ArrayList of interfaces]
     */
    public static ArrayList<String> setInterfaces(Class[] c){
        ArrayList<String> al = new ArrayList();
        for (Class entry : c) {
            al.add(entry.getName());
        }
        if (al.size() == 0) {
            al.add("None");
        }
        return al;
    }

    /**
     * Setter for Fields
     * @param  f [Fields to be added to ClassData]
     * @return   [ArrayList of Fields]
     */
    public static ArrayList<String> setFields(Field[] f){
        ArrayList<String> al = new ArrayList();
        for (Field entry : f) {
            al.add(entry.getName());
        }
        if (al.size() == 0) {
            al.add("None");
        }
        return al;
    }

    /**
     * Setter for Methods
     * @param  m [Methods to be added to ClassData]
     * @return   [ArrayList of Methods]
     */
    public static ArrayList<String> setMethods(Method[] m){
        ArrayList<String> al = new ArrayList();
        for (Method entry : m) {
            al.add(entry.getName() + (Modifier.isPublic(entry.getModifiers()) ? ": public" : ""));
        }

        if (al.size() == 0) {
            al.add("None");
        }

        return al;
    }

    /**
     * Setter for Providers that compares the fields against
     * providers
     * @param  f     [Fields to be compared filters]
     * @param  files [Classes to be compared against]
     * @return       [ArrayList of Providers]
     */
    public static ArrayList<String> setProviders(Field[] f, File[] files){
        ArrayList<String> al = new ArrayList();
        for (int i = 0; i < f.length; i++){
            for (int j = 0; j< files.length;j++ ){
                try{
                    if (f[i].getType() == Class.forName(files[j].getName().replace(".class", "")))
                        al.add(f[i].getType().getName());
                }catch (Exception e) {
                    System.err.println(e);
                }
            }
        }

        if (al.size() == 0) {
            al.add("None");
        }

        return al;
    }

    /**
     * Setter for Clients that compares this class against fields
     * in other classes
     * @param  c     [Current class]
     * @param  files [List of classes to be compared]
     * @return       [ArrayList of Clients]
     */
    public static ArrayList<String> setClients(Class c, File[] files){

        ArrayList<String> al = new ArrayList();
        for (int i = 0; i < files.length; i++){
            try{
                Field f[] = Class.forName(files[i].getName().replace(".class", "")).getDeclaredFields();
                for (int j = 0; j< f.length;j++ ){
                    if (f[j].getType() == Class.forName(c.getName())){
                        al.add(files[i].getName().replace(".class", ""));
                    }
                }
            } catch(Exception e){
                System.err.println(e);
            }
        }

        if (al.size() == 0) {
            al.add("None");
        }

        return al;
    }

    /**
     * Helper to print passed options
     */
    private static void printOpts(){

        for (int i = 0; i < optsList.size() ; i++ ) {
            System.out.println(optsList.get(i));
        }
    }

    /**
     * Helper to print passed arguments
     */
    private static void printArgs(){
        for (int i = 0; i < argsList.size() ; i++ ) {
            System.out.println(argsList.get(i));
        }
    }

    /**
     * Helper to filter options and arguments
     * @param  args                   [Array of parameter passed]
     * @throws IllegalArgumentException [if something breaks]
     */
    private static void getOpts(String [] args) throws IllegalArgumentException{

        for (int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
                case '-':
                if (args[i].length() < 2)
                    throw new IllegalArgumentException("Not a valid argument: "+args[i]);
                if (args[i].charAt(1) == '-') {
                    if (args[i].length() < 3)
                        throw new IllegalArgumentException("Not a valid argument: "+args[i]);
                // --opt
                    doubleOptsList.add(args[i].substring(2, args[i].length()));
                } else {
                    if (args.length-1 == i)
                        throw new IllegalArgumentException("Expected arg after: "+args[i]);
                // -opt
                    optsList.add(new Option(args[i], args[i+1]));
                    i++;
                }
                break;
                default:
            // arg

                argsList.add(args[i]);
                break;
            }
        }
    }

    /**
     * Inner Option class
     */
    private static class Option {
        String flag, opt;
        public Option(String flag, String opt) { this.flag = flag; this.opt = opt; }
    }
}
