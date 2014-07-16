package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoDefinitionClass {
	
	/*
	 * Used in:
	 * 
	 * - some relations
	 * - minimal cardinality relation
	 * - maximal cardinality relation
	 * - exactly cardinality relation
	 * 
	 * */
	
	private static int count = 0;	//Count the number of instances
	
	public int id;
	public String Source;
	public String Relation;
	public EnumPropertyType PropertyType;				//object or data
	public EnumRelationTypeCompletness TypeCompletness;	//some,min,max...
	public String Target;
	public String Cardinality;					// just in cases we need cardinality (max, min, exactly)
	public static final String sKey = "#&&#";	// separator key
	
	public DtoDefinitionClass()
	{
		count++;
		this.id = count;
		this.Source = "";
		this.Relation = "";
		this.Target = "";
		this.Cardinality = "";
		this.PropertyType = null;
		this.TypeCompletness = null;
	}
	
	public static ArrayList<DtoDefinitionClass> getDtosWithSource(ArrayList<DtoDefinitionClass> list, String source)
	{
		ArrayList<DtoDefinitionClass> listResult = new ArrayList<DtoDefinitionClass>();
		for (DtoDefinitionClass aux : list) {
			if(source.equals(aux.Source)){
				
				listResult.add(aux);
			}
		}
		return listResult;
	}
	
	@Override
	public String toString() {
		
		return this.Source + sKey + this.Relation + sKey + this.Target + sKey + this.Cardinality;
	
	}	

	public static DtoDefinitionClass get(ArrayList<DtoDefinitionClass> list, int id)
	{		
		for (DtoDefinitionClass aux : list) {	
			if(aux.id == id)
			{
				return aux;
			} 

		}
		return null;
	}

	public boolean sameAs(DtoDefinitionClass d) {
		
		if(this.Source == d.Source && this.Relation == d.Relation && this.Target == d.Target && this.Cardinality.equals(d.Cardinality) && this.PropertyType.equals(d.PropertyType))
		{
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean existDto(DtoDefinitionClass dto, ArrayList<DtoDefinitionClass> list) {
		
		for (DtoDefinitionClass d : list) {
			if(dto.sameAs(d))
			{
				return true;
			}
		}
		
		return false;
	}
	
 	public void print()
	{
		System.out.println(this.Source + " -> " + this.Relation + " (" + this.PropertyType + ") - > " + this.Target + "(" + this.Cardinality + ")");
	}

	public static DtoDefinitionClass getDtoWithSourceAndRelationAndTarget(ArrayList<DtoDefinitionClass> list, String source) {
		
		for (DtoDefinitionClass aux : list) {
			if(source.equals(aux.Source)){
				
				return aux;
			}
		}
		
		return null;
	}	
	
	
}
