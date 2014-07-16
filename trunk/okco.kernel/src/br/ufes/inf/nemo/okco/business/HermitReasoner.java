package br.ufes.inf.nemo.okco.business;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;

import br.ufes.inf.nemo.okco.model.IReasoner;

public class HermitReasoner implements IReasoner {

	public InfModel run(OntModel modelCame) {
		
		long antes = System.currentTimeMillis();  

		//Converting output stream from model to input stream		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        modelCame.write(out, "RDF/XML");        
        InputStream in = new ByteArrayInputStream(out.toByteArray());
		
		//------------------------------------------------------------//
		
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = null;
		try {
			o = m.loadOntologyFromOntologyDocument(in);
			
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		
		//Hermit Configuration
		
		Configuration config = new Configuration();
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		config.reasonerProgressMonitor = progressMonitor;		
		
		//Create Hermit
        
		OWLReasoner hermit = new Reasoner.ReasonerFactory().createReasoner(o, config);
				
		//Used to read in OntModel
		
		OntModel model = modelCame;		
		//model.read(in,null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        model.write(baos, "RDF/XML");        
        
        InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        BufferedReader in2 = new BufferedReader(new InputStreamReader(bais));
        try {
			while ((in2.readLine()) != null) {
			    //  System.out.println(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			m.loadOntologyFromOntologyDocument(bais);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredEquivalentClassAxiomGenerator());				//class hierarchy
		gens.add(new InferredSubClassAxiomGenerator());						//class hierarchy
		gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());		//object properties
		gens.add(new InferredInverseObjectPropertiesAxiomGenerator());		//object properties
		gens.add(new InferredSubObjectPropertyAxiomGenerator());			//object properties
		gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());		//data properties
		gens.add(new InferredSubDataPropertyAxiomGenerator());				//data properties
		gens.add(new InferredClassAssertionAxiomGenerator()); 				//class instance data structures
		gens.add(new InferredPropertyAssertionGenerator());					//property instance data structures, data properties, class instance data structures
		
		//gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
		//gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
		//gens.add(new InferredDisjointClassesAxiomGenerator());
		
		InferredOntologyGenerator iog = new InferredOntologyGenerator(hermit, gens);
		iog.fillOntology(m, o);

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try {
			m.saveOntology(o, new RDFXMLOntologyFormat(), baos2);
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
    	
    	bais = new ByteArrayInputStream(baos2.toByteArray());
		model.read(bais, null);
		
		long tempo = System.currentTimeMillis() - antes;
        System.out.printf("O programa reasoner hermit executou em %d milissegundos.%n", tempo);  

		return model;
	}

}
