package br.ufes.inf.nemo.okco.api;

import java.util.ArrayList;

public class InstanceClassDefinition {
	
	public String TopClass;
	public ArrayList<String> SubClassesToClassify;
	
	public InstanceClassDefinition()
	{
		this.SubClassesToClassify = new ArrayList<String>();
	}

}
