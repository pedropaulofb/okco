package br.ufes.inf.nemo.okco.model;

import com.hp.hpl.jena.ontology.OntModel;

public class DtoModel {

	public OntModel model;
	public String NameSpace;
	
	public OntModel getModel() {
		return model;
	}
	public void setModel(OntModel model) {
		this.model = model;
	}
	public String getNameSpace() {
		return NameSpace;
	}
	public void setNameSpace(String nameSpace) {
		NameSpace = nameSpace;
	}
	
	
}
