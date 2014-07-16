package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoDefinitionClassList {
	
	/*
	 * Used in:
	 * 
	 * - Complete Class
	 * 
	 * */

	public String Source;
	public String Relation;
	public ArrayList<String> ListTarget;
	
	public DtoDefinitionClassList()
	{
		this.ListTarget = new ArrayList<String>();
	}

	public static DtoDefinitionClassList GetDtoDefinitionListClass(
			ArrayList<DtoDefinitionClassList> dtoList, String Source) {
		// 
		for (DtoDefinitionClassList dto : dtoList) {
			if(dto.Source == Source)
			{
				return dto;
			}
		}
		
		return null;
	}

	public static DtoDefinitionClassList existInList(ArrayList<DtoDefinitionClassList> dtolist, String source)
	{
		for (DtoDefinitionClassList aux : dtolist) {
			if(source.equals(aux.Source)){
				return aux;
			}
		}
		return null;
	}
	
	public void AddMember(String target) {
		this.ListTarget.add(target);		
	}
	

}
