package hsrt.ontology.virtualMarketplace;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    @Retention(RetentionPolicy.RUNTIME)
    private @interface CommandMethod { }

    private static VirtualMarketplaceOntology ontology;
    private static BufferedReader reader;
    private static boolean exit;

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {

        reader = new BufferedReader(new InputStreamReader(System.in));

        ontology = new VirtualMarketplaceOntology();

        while (!exit) {
            String input = reader.readLine();

            try {
                Main.class.getMethod(input.replaceAll("-", "").trim()).invoke(Main.class);
            } catch (SecurityException | NoSuchMethodException e) {
                System.err.println("Unkown command: " + input);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (OntologyInconsistentException e) {
                System.err.println(e.getMessage() + " - Ontology inconsistent!");
            }
        }

        ontology.save();
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void info() {

        System.out.println("Available Commands:");

        for (Method method : Main.class.getMethods()) {
            if (method.isAnnotationPresent(CommandMethod.class))
                System.out.println("-" + method.getName());
        }
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void getProducts() {
        ontology.printProducts();
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void getAgents() {
        ontology.printAgents();
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void getPersons() {
        ontology.printPersons();
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void exit() {
        exit = true;
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void addProduct() throws IOException {
        String input = getInput("Enter Product name:");
        ontology.addProduct(input, input, 1234);
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void addAgent() throws IOException {
        String input = getInput("Enter Agent name:");
        String input2 = getInput("Enter Owner name:");
        ontology.addAgent(input, input2);
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void addPerson() throws IOException {
        String input = getInput("Enter Person name:");
        ontology.addPerson(input);
    }

    @CommandMethod
    @SuppressWarnings("unused")
    public static void placeOffer() throws IOException {
        String input1 = getInput("Enter Order name:");
        String input2 = getInput("Enter Vendor name:");
        String input3 = getInput("Enter Product name:");
        String input4 = getInput("Enter Price:");
        ontology.placeOffer(input1, input2, input3, Double.parseDouble(input4));
    }

    private static String getInput(String message) throws IOException {
        return getInput(message, false);
    }

    private static String getInput(String message, boolean handleWhitespace) throws IOException {
        System.out.println(message);
        String input = reader.readLine();

        if(handleWhitespace)
            return input.replaceAll(" ", "_");

        return input;
    }
}
