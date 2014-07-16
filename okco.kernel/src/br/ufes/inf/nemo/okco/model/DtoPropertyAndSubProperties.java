package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoPropertyAndSubProperties {
	
	private static int count = 0;	//Count the number of instances
	public String Property;
	public ArrayList<String> SubProperties;
	public String iTargetNs;
	public String iTargetName;
	public EnumPropertyType propertyType;
	public int id;
	
	public DtoPropertyAndSubProperties()
	{
		count++;
		this.id = count;
		this.SubProperties = new ArrayList<String>();
	}

	public static DtoPropertyAndSubProperties getInstance(ArrayList<DtoPropertyAndSubProperties> listSpecializationProperties,	int id) {
		
		for (DtoPropertyAndSubProperties dto : listSpecializationProperties) {
			if(dto.id == id)
			{
				return dto;
			}
		}
		
		return null;
	}

}
