package hsrt.ontology.virtualMarketplace;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import java.io.File;
import java.util.Collections;

/**
 * Created by Andreas on 21.05.2016.
 */
class VirtualMarketplaceOntology {

    private static final String BASE_URL = "http://www.reutlingen-university.de/ontologies/ss16/VirtualMarketplace";
    private static final String FILE_LOCATION = "VirtualMarketplaceV4.owl";

    private static final String MARKETPLACE_ID = "MainMarketplace";

    private OWLDataFactory factory;
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private PrefixOWLOntologyFormat pm;
    private OWLReasoner reasoner;
    private OWLObjectRenderer renderer;

    VirtualMarketplaceOntology() throws OWLOntologyCreationException {

        //prepare ontology
        manager = OWLManager.createOWLOntologyManager();
        File file = new File(FILE_LOCATION);
        ontology = manager.loadOntologyFromOntologyDocument(file);
        factory = manager.getOWLDataFactory();
        pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
        pm.setDefaultPrefix(BASE_URL + "#");
        renderer = new DLSyntaxObjectRenderer();
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());

        IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
        System.out.println("Ontology loaded from: " + documentIRI);
    }

    void placeOffer(String name, String vendor, String product, double price) throws OntologyInconsistentException {

        //Add Individual
        OWLIndividual offer = addInstance(name, "Offer");

        //Set Data Properties
        setDataProperty(offer, "hasPrice", price);

        //Set Object Properties
        setObjectProperty(offer, "hasVendor", vendor);
        setObjectProperty(offer, "placedAt", MARKETPLACE_ID);
        setObjectProperty(offer, "hasProduct", product);

        if(!reasoner.isConsistent()) {
            removeInstance(name);
            throw new OntologyInconsistentException("Error on placing Offer " + name);
        }

        System.out.println("Placed Offer: " + name);
    }

    void addProduct(String name, String description, int id) throws OntologyInconsistentException {

        //Add Individual
        OWLIndividual product = addInstance(name, "Product");

        //Set Data Properties
        setDataProperty(product, "hasDescription", description);
        setDataProperty(product, "hasProductId", id);

        if(!reasoner.isConsistent()) {
            removeInstance(name);
            throw new OntologyInconsistentException("Error on adding Product " + name);
        }

        System.out.println("Added Product: " + name);
    }

    void addAgent(String name, String owner) throws OntologyInconsistentException {

        //Add Individual
        OWLIndividual agent = addInstance(name, "Agent");
        setObjectProperty(agent, "hasOwner", owner);

        if(!reasoner.isConsistent()) {
            removeInstance(name);
            throw new OntologyInconsistentException("Error on adding Agent " + name);
        }

        System.out.println("Added Agent: " + name);
    }

    void addPerson(String name) throws OntologyInconsistentException {

        //Add Individual
        OWLIndividual person = addInstance(name, "Person");

        if(!reasoner.isConsistent()) {
            removeInstance(name);
            throw new OntologyInconsistentException("Error on adding Person " + name);
        }

        System.out.println("Added Person: " + name);
    }

    void printAgents() {
        printIndividuals("Agent");
    }

    void printPersons() {
        printIndividuals("Person");
    }

    void printProducts() {
        printIndividuals("Product");
    }

    void save() {

        try {
            IRI documentIRI2 = IRI.create(new File(FILE_LOCATION));
            manager.saveOntology(ontology, documentIRI2);
            System.out.println("Ontology saved in " + documentIRI2);
        } catch (OWLOntologyStorageException e) {
            System.out.println("Error on saving Ontology");
            e.printStackTrace();
        }
    }

    private OWLIndividual loadIndividual(String name) {
        OWLIndividual individual = factory.getOWLNamedIndividual(":" + name, pm);
        return individual;
    }

    private void setObjectProperty(OWLIndividual individual, String propertyName, String object) {
        OWLObjectPropertyExpression expression = factory.getOWLObjectProperty(":" + propertyName, pm);
        OWLObjectPropertyAssertionAxiom property =  factory.getOWLObjectPropertyAssertionAxiom(expression, individual, loadIndividual(object));
        manager.addAxiom(ontology, property);
    }

    private void setDataProperty(OWLIndividual individual, String propertyName, int value) {
        OWLDataPropertyExpression expression = factory.getOWLDataProperty(":" + propertyName, pm);
        OWLDataPropertyAssertionAxiom property =  factory.getOWLDataPropertyAssertionAxiom(expression, individual, value);
        manager.addAxiom(ontology, property);
    }

    private void setDataProperty(OWLIndividual individual, String propertyName, String value) {
        OWLDataPropertyExpression expression = factory.getOWLDataProperty(":" + propertyName, pm);
        OWLDataPropertyAssertionAxiom property =  factory.getOWLDataPropertyAssertionAxiom(expression, individual, value);
        manager.addAxiom(ontology, property);
    }

    private void setDataProperty(OWLIndividual individual, String propertyName, double value) {
        OWLDataPropertyExpression expression = factory.getOWLDataProperty(":" + propertyName, pm);
        OWLDataPropertyAssertionAxiom property =  factory.getOWLDataPropertyAssertionAxiom(expression, individual, value);
        manager.addAxiom(ontology, property);
    }

    private OWLIndividual addInstance(String instanceName, String className){
        OWLClass owlClass = factory.getOWLClass(":" + className, pm);
        OWLIndividual individual = factory.getOWLNamedIndividual(":" + instanceName, pm);
        OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, individual);
        manager.addAxiom(ontology, classAssertionAxiom);

        return individual;
    }

    private void removeInstance(String instanceName){
        OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));

        for (OWLNamedIndividual ind : ontology.getIndividualsInSignature()) {
            if(ind.getIRI().getFragment().equals(instanceName))
                ind.accept(remover);
        }

        manager.applyChanges(remover.getChanges());
    }

    private void printIndividuals(String className) {
        OWLClass parentClass = factory.getOWLClass(":" + className, pm);

        for (OWLNamedIndividual instance : reasoner.getInstances(parentClass, false).getFlattened()) {
            System.out.println(renderer.render(instance));
        }
    }
}
