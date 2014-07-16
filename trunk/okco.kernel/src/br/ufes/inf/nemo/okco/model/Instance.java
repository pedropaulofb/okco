package br.ufes.inf.nemo.okco.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Instance implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int count = 0;	//Count the number of instances
	
	public int id;
	public String ns;
	public String name;
	public ArrayList<String> ListClasses;
	public ArrayList<String> ListSameInstances;
	public ArrayList<String> ListDiferentInstances;
	public boolean existInModel;	//
	public boolean isModified;		// Check is modified and not run the reasoner for him
	
	//Definitions respected by the instance
	
	public ArrayList<DtoDefinitionClass> ListSome;
	public ArrayList<DtoDefinitionClass> ListMin;
	public ArrayList<DtoDefinitionClass> ListMax;
	public ArrayList<DtoDefinitionClass> ListExactly;
	public ArrayList<DtoCompleteClass> ListCompleteClasses;
	public ArrayList<DtoPropertyAndSubProperties> ListSpecializationProperties;
	
	public Instance(String ns, String name, ArrayList<String> listClasses, ArrayList<String> listDifferent, ArrayList<String> listSame, Boolean existInModel)
	{
		count++;
		this.id = count;
		this.ns = ns;
		this.name = name;
		this.ListClasses = listClasses;
		this.ListCompleteClasses = new ArrayList<DtoCompleteClass>();
		this.ListSpecializationProperties = new ArrayList<DtoPropertyAndSubProperties>();
		this.ListSome = new ArrayList<DtoDefinitionClass>();
		this.ListMin = new ArrayList<DtoDefinitionClass>();
		this.ListMax = new ArrayList<DtoDefinitionClass>();
		this.ListExactly = new ArrayList<DtoDefinitionClass>();
		if (listSame == null)
		{
			this.ListSameInstances = new ArrayList<String>();
		}else{
			this.ListSameInstances = new ArrayList<String>(listSame);
		}
		
		if (listDifferent == null)
		{
			this.ListDiferentInstances = new ArrayList<String>();
		}else{
			this.ListDiferentInstances = new ArrayList<String>(listDifferent);
		}
		this.existInModel = existInModel;
		this.isModified = false;
	}
	
 	public void print()
	{
		System.out.println("\n-------------------------------------");
		System.out.println("- Instance name: " + this.ns + this.name);
		System.out.println("- ListOfSome: ");
		for (DtoDefinitionClass dto : ListSome) {
			System.out.println("      - " + dto.Relation + " -> " + dto.Target + " (" + dto.Cardinality + ")");
		}
		System.out.println("- ListOfMin: ");
		for (DtoDefinitionClass dto : ListMin) {
			System.out.println("      - " + dto.Relation + " -> " + dto.Target + " (" + dto.Cardinality + ")");
		}
		System.out.println("- ListOfMax: ");
		for (DtoDefinitionClass dto : ListMax) {
			System.out.println("      - " + dto.Relation + " -> " + dto.Target + " (" + dto.Cardinality + ")");
		}
		System.out.println("- ListOfExactly: ");
		for (DtoDefinitionClass dto : ListExactly) {
			System.out.println("      - " + dto.Relation + " -> " + dto.Target + " (" + dto.Cardinality + ")");
		}
		System.out.println("- ListOfComplete: ");
		for (DtoCompleteClass dto : ListCompleteClasses) {
			System.out.println("      		- " + dto.CompleteClass);
			for (String sub : dto.Members) {
				System.out.println("      				- " + sub);				
			}
		}
		System.out.println("- ListSpecializationProperties: ");
		for (DtoPropertyAndSubProperties dtoSpec : this.ListSpecializationProperties) {			
			System.out.println("	- " + dtoSpec.Property + " -> " + dtoSpec.iTargetName + " (" + dtoSpec.propertyType + ")");
			for (String propEsp : dtoSpec.SubProperties) {
				System.out.println("      			- " + propEsp);				
			}
		}
		System.out.println("- ListSame: ");
		for (String s : ListSameInstances) {
			System.out.println("      - " + s);
		}
		System.out.println("- ListDifferent: ");
		for (String s : ListDiferentInstances) {
			System.out.println("      - " + s);
		}
		System.out.println("");
	}
 	
 	public boolean is_Semi_Complete()
 	{ 		
 		boolean haveSpecializationProp = false;
 		for (DtoPropertyAndSubProperties dtoSpec : this.ListSpecializationProperties) {
			if(dtoSpec.SubProperties.size() > 0)
			{
				haveSpecializationProp = true;
				break;
			}				
		}
 		
 		if(haveSpecializationProp == true)
 		{
 			return true;
 			
 		} else 
 		{
 			return false;
 		}
 	}
 	
 	public boolean haveKnwologeToComplete()
 	{		
 		boolean haveComplete = false;
 		for (DtoCompleteClass dtoComplete : this.ListCompleteClasses) {
			if(dtoComplete.Members.size() > 0)
			{
				haveComplete = true;
				break;
			}
				
		}
 		
 		if(this.ListSome.size() > 0 || this.ListMin.size() > 0 || this.ListMax.size() > 0 || this.ListExactly.size() > 0 || haveComplete == true)
 		{
 			return true;
 			
 		} else 
 		{
 			return false;
 		} 		
 	}
 	
 	public void setModified(boolean value)
 	{		
 		this.isModified = value;
 		
 	}

	public static void removeFromList(ArrayList<Instance> listNewInstances,	String id) {

		for (Instance instance : listNewInstances) {
			
			if(instance.id == Integer.parseInt(id))
			{
				listNewInstances.remove(instance);
				break;
			}
		}		
	}

}
