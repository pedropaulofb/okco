package br.ufes.inf.nemo.okco.business;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;

import br.ufes.inf.nemo.okco.model.IReasoner;

public class PelletReasoner implements IReasoner {

	public InfModel run(OntModel model) {
		
		Reasoner r = PelletReasonerFactory.theInstance().create();
		
		long antes = System.currentTimeMillis();  
	  	InfModel infModel = ModelFactory.createInfModel(r, model);
	  	  
        long tempo = System.currentTimeMillis() - antes;
        System.out.printf("O reasoner pellet executou em %d milissegundos.%n", tempo);  
	  	
	  	
		return infModel;
	}

}
