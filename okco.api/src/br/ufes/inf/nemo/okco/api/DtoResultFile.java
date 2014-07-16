package br.ufes.inf.nemo.okco.api;

import java.util.ArrayList;

public class DtoResultFile {
	
	public ArrayList<Instance> ListInstances;
	public ArrayList<String> ListErrors;
	public String owlFile;
	
	public DtoResultFile()
	{
		this.ListInstances = new ArrayList<Instance>();
		this.ListErrors = new ArrayList<String>();
	}

}
