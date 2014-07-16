package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoInstance {
	
	public  String Uri;
	public ArrayList<String> ClassNameList;
	
	public DtoInstance(String Uri, String ClassName)
	{
		this.Uri = Uri;
		this.ClassNameList = new ArrayList<String>();
		this.ClassNameList.add(ClassName);
	}

	public static DtoInstance getInstance(String instanceName,	ArrayList<DtoInstance> allInstances) {

		for (DtoInstance dto : allInstances) {
			
			if(dto.Uri.equals(instanceName))
			{
				return dto;
			}
			
		}
		
		return null;
	}

	public void AddClass(String className) {
		this.ClassNameList.add(className);
		
	}

}
