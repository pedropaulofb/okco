package br.ufes.inf.nemo.okco.api;

import java.util.ArrayList;

public class DtoResultInstances {
	
	public ArrayList<Instance> ListInstances;
	public ArrayList<String> ListErrors;
	
	public DtoResultInstances()
	{
		this.ListErrors = new ArrayList<String>();
		this.ListInstances = new ArrayList<Instance>();
	}

}
