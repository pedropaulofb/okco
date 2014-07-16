package br.ufes.inf.nemo.okco.model;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;

public interface IReasoner {
	
	public InfModel run(OntModel model);

}
