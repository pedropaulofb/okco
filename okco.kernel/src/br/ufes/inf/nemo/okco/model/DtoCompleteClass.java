package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoCompleteClass {

	public String CompleteClass;
	public ArrayList<String> Members;
	
	public DtoCompleteClass()
	{
		this.Members = new ArrayList<String>();
	}
	
	public static DtoCompleteClass GetDtoCompleteClass(ArrayList<DtoCompleteClass> list, String completeClass)
	{
		
		for (DtoCompleteClass dtoCompleteClass : list) {
			if(dtoCompleteClass.CompleteClass == completeClass)
			{
				return dtoCompleteClass;
			}
		}
		
		return null;
	}

	public void AddMember(String member) {
		this.Members.add(member);		
	}
}
