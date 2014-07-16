package br.ufes.inf.nemo.okco.model;

import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;

public interface IRepository {
	
	public OntModel Open(String inputFileName);
	public OntModel Open(InputStream in);
	public OntModel CopyModel(OntModel ontModel);
	public String getNameSpace(OntModel model);
	public void Save(OntModel model, String path);
	public void SaveWithDialog(OntModel model);
	public void Print(OntModel model);
	public String getModelString(OntModel model);
}
