package br.ufes.inf.nemo.okco.business;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;

import br.ufes.inf.nemo.okco.model.DtoCompleteClass;
import br.ufes.inf.nemo.okco.model.DtoDefinitionClass;
import br.ufes.inf.nemo.okco.model.DtoInstance;
import br.ufes.inf.nemo.okco.model.DtoInstanceRelation;
import br.ufes.inf.nemo.okco.model.DtoPropertyAndSubProperties;
import br.ufes.inf.nemo.okco.model.EnumPropertyType;
import br.ufes.inf.nemo.okco.model.Instance;
import br.ufes.inf.nemo.okco.model.EnumRelationTypeCompletness;
import br.ufes.inf.nemo.okco.model.OKCoExceptionInstanceFormat;

public class ManagerInstances {
	
	private Search search;
	private FactoryInstances factory;

	public ManagerInstances(Search search, FactoryInstances factory, OntModel model)
	{
		this.search = search;
		this.factory = factory;
	}

	public OntModel CreateInstance(String instanceSource, String Relation, Instance instanceNew, String TargetClass, ArrayList<Instance> ListAllInstances, OntModel model)
	{
		return factory.CreateInstance(instanceSource, Relation, instanceNew, TargetClass, ListAllInstances, model);
	}
	
	public OntModel DeleteInstance(Instance instance, OntModel model)
	{
		return factory.DeleteInstance(instance, model);
	}
	
	public OntModel CreateInstanceList(String instanceSource, ArrayList<Instance> list, String Relation, String TargetClass, ArrayList<Instance> ListAllInstances, OntModel model)
	{
		
		for (Instance instance : list) {
			if(instance.existInModel == false)
			{
				model = factory.CreateInstance(instanceSource, Relation, instance, TargetClass, ListAllInstances, model);
				//Aplication.ListAllInstances.add(instance);
			}
		}
		
		return model;
		
		
	}
	
	public void UpdateInstanceAndRelations(ArrayList<Instance> listInstances, ArrayList<DtoDefinitionClass> dtoRelationsList, OntModel model, InfModel infModel, String ns)
	{		

		for (DtoDefinitionClass dto : dtoRelationsList)
		{			
			ArrayList<String> listInstancesOfDomain = this.search.GetInstancesFromClass(model, infModel, dto.Source);
			if(listInstancesOfDomain.size() > 0)	//Check if are need to create
			{
				for (String instanceName : listInstancesOfDomain)
				{					
					//---SOME---//
					
					if(dto.TypeCompletness.equals(EnumRelationTypeCompletness.SOME))
					{
						boolean existInstanceTarget = this.search.CheckExistInstanceTarget(infModel, instanceName, dto.Relation, dto.Target);
						if(existInstanceTarget)
						{
							//Do nothing
							
						} else {
							
							//Check if individual already exist in list
							Instance instance = this.getInstance(listInstances, instanceName);
							if(instance == null)
							{
								ArrayList<String> listClasses = new ArrayList<String>();
								listClasses.add(dto.Source);
								instance = new Instance(ns, instanceName.replace(ns, ""), listClasses, search.GetDifferentInstancesFrom(infModel, instanceName), search.GetSameInstancesFrom(infModel, instanceName), true);
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListSome);
								if(!existDto)
								{
									instance.ListSome.add(dto);
								}
								listInstances.add(instance);
				
							} else {
								
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListSome);
								if(!existDto)
								{
									instance.ListSome.add(dto);
								}								
							}
						}
					}
					
					//---MIN---//
					
					if(dto.TypeCompletness.equals(EnumRelationTypeCompletness.MIN))
					{
						int quantityInstancesTarget = this.search.CheckExistInstancesTargetCardinality(infModel, instanceName, dto.Relation, dto.Target, dto.Cardinality);
						if (quantityInstancesTarget < Integer.parseInt(dto.Cardinality))	//Min restriction
						{
							Instance instance = this.getInstance(listInstances, instanceName);
							if(instance == null)
							{
								ArrayList<String> listClasses = new ArrayList<String>();
								listClasses.add(dto.Source);
								instance = new Instance(ns, instanceName.replace(ns, ""), listClasses, search.GetDifferentInstancesFrom(infModel, instanceName), search.GetSameInstancesFrom(infModel, instanceName),true);
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListMin);
								if(!existDto)
								{
									instance.ListMin.add(dto);
								}
								listInstances.add(instance);
				
							} else {
								
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListMin);
								if(!existDto)
								{
									instance.ListMin.add(dto);
								}	
							}
						}
					}
					
					//---MAX---//
					
					if(dto.TypeCompletness.equals(EnumRelationTypeCompletness.MAX))
					{
						int quantityInstancesTarget = this.search.CheckExistInstancesTargetCardinality(infModel, instanceName, dto.Relation, dto.Target, dto.Cardinality);
						if (quantityInstancesTarget > Integer.parseInt(dto.Cardinality))	//Max restriction
						{
							Instance instance = this.getInstance(listInstances, instanceName);
							if(instance == null)
							{
								ArrayList<String> listClasses = new ArrayList<String>();
								listClasses.add(dto.Source);
								instance = new Instance(ns, instanceName.replace(ns, ""), listClasses, search.GetDifferentInstancesFrom(infModel, instanceName), search.GetSameInstancesFrom(infModel, instanceName),true);
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListMax);
								if(!existDto)
								{
									instance.ListMax.add(dto);
								}
								listInstances.add(instance);
				
							} else {
								
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListMax);
								if(!existDto)
								{
									instance.ListMax.add(dto);
								}	
							}
						}
					}
					
					//---EXACLTY---//
					
					if(dto.TypeCompletness.equals(EnumRelationTypeCompletness.EXACTLY))
					{
						int quantityInstancesTarget = this.search.CheckExistInstancesTargetCardinality(infModel, instanceName, dto.Relation, dto.Target, dto.Cardinality);
						if (quantityInstancesTarget != Integer.parseInt(dto.Cardinality))	//Exactly restriction
						{
							Instance instance = this.getInstance(listInstances, instanceName);
							if(instance == null)
							{
								ArrayList<String> listClasses = new ArrayList<String>();
								listClasses.add(dto.Source);
								instance = new Instance(ns, instanceName.replace(ns, ""), listClasses, search.GetDifferentInstancesFrom(infModel, instanceName), search.GetSameInstancesFrom(infModel, instanceName),true);
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListExactly);
								if(!existDto)
								{
									instance.ListExactly.add(dto);
								}
								listInstances.add(instance);
				
							} else {
								
								boolean existDto = DtoDefinitionClass.existDto(dto, instance.ListExactly);
								if(!existDto)
								{
									instance.ListExactly.add(dto);
								}	
							}
						}
					}
					
					//---COMPLETE---//
				}
			}			
		}	
	}

	public void UpdateInstanceSpecialization(ArrayList<Instance> listAllInstances, OntModel model,	InfModel infModel, String ns) {
		
		//update and check specialization class for all instances one by one		
		
		for (Instance instanceSelected : listAllInstances) 
		{			
			// ------ Complete classes list ------//
			
			ArrayList<DtoCompleteClass> ListCompleteClsInstaceSelected = new ArrayList<DtoCompleteClass>();
			DtoCompleteClass dto = null;
			
			if(instanceSelected.ListClasses.size() == 1 && instanceSelected.ListClasses.get(0).contains("Thing"))	//Case thing
			{
				//Case if the instance have no class selected - only Thing
				dto = new DtoCompleteClass();
				dto.CompleteClass = instanceSelected.ListClasses.get(0);
				for (String subClas : search.GetClasses(model)) {
					if(subClas != null)
						dto.AddMember(subClas);
				}
				ListCompleteClsInstaceSelected.add(dto);
				
			} else {
				
				for (String cls : instanceSelected.ListClasses)
				{
					ArrayList<DtoCompleteClass> ListCompleteClsAndSubCls = search.GetCompleteSubClasses(cls, instanceSelected.ListClasses, infModel);					
					ListCompleteClsInstaceSelected.addAll(ListCompleteClsAndSubCls);						
				}
			}
			
			instanceSelected.ListCompleteClasses = ListCompleteClsInstaceSelected;			
			
			// ------ Complete properties list ------//
			
			ArrayList<DtoPropertyAndSubProperties> ListSpecializationProperties = new ArrayList<DtoPropertyAndSubProperties>();
			DtoPropertyAndSubProperties dtoP = null;
			
			ArrayList<DtoInstanceRelation> instanceListRelations = search.GetInstanceRelations(infModel, instanceSelected.ns + instanceSelected.name); 		//Get instance relations
			for (DtoInstanceRelation dtoInstanceRelation : instanceListRelations) 
			{			
				ArrayList<String> subPropertiesWithDomainAndRange = search.GetSubPropertiesWithDomaninAndRange(instanceSelected.ns + instanceSelected.name, dtoInstanceRelation.Property, dtoInstanceRelation.Target, instanceListRelations, infModel);

				if(subPropertiesWithDomainAndRange.size() > 0)
				{
					dtoP = new DtoPropertyAndSubProperties();
					dtoP.Property = dtoInstanceRelation.Property;
					dtoP.iTargetNs = dtoInstanceRelation.Target.split("#")[0] + "#";
					dtoP.iTargetName = dtoInstanceRelation.Target.split("#")[1];
					dtoP.propertyType = search.GetPropertyType(infModel, dtoInstanceRelation.Property);
					
					for (String sub : subPropertiesWithDomainAndRange) 
					{
						boolean ok = true;
						
						ArrayList<String> distointSubPropOfProp = this.search.GetDisjointPropertiesOf(sub, infModel);
						for (String disjointrop : distointSubPropOfProp) {
							
							for (DtoInstanceRelation dtoWithRelation : instanceListRelations) {
								if(dtoWithRelation.Property.equals(disjointrop)) // instance have this sub relation
								{
									ok = false;
									break;
								}
							}
						}
						
						for (DtoInstanceRelation dtoWithRelation : instanceListRelations) {
						
							if(dtoWithRelation.Property.equals(sub)) // instance have this sub relation
							{
								ok = false;
								break;
							}
						}						
						
						
						if(ok == true)
						{
							dtoP.SubProperties.add(sub);
						}
					}
					
					if(dtoP.SubProperties.size() > 0)
						ListSpecializationProperties.add(dtoP);
				}			
			}
			
			instanceSelected.ListSpecializationProperties = ListSpecializationProperties;						
		}
		
	}
	
	public Instance getInstance(ArrayList<Instance> listInstances, String instanceName) {		
		
		for (Instance instance : listInstances) {
			if((instance.ns + instance.name).equals(instanceName))
			{
				return instance;
			}
		}
		
		return null;
	}
	
	public Instance getInstance(ArrayList<Instance> listInstances, int id) {
		
		for (Instance instance : listInstances) {
			if(instance.id == id)
			{
				return instance;
			}
		}
		
		return null;
	}
	
	public ArrayList<Instance> getIntersectionOf(ArrayList<Instance> listAllInstances, ArrayList<String> listInstancesName) {

		ArrayList<Instance> list = new ArrayList<Instance>();
		
		for (String iName : listInstancesName) 
		{			
			for (Instance instance : listAllInstances) 
			{				
				if(iName.equals(instance.ns + instance.name))
				{
					list.add(instance);
					break;
				}				
			}
		}		
		return list;
	}

	public ArrayList<Instance> getAllInstances(OntModel model, InfModel infModel, String ns) throws OKCoExceptionInstanceFormat {
		
		//NS from model
		
		ArrayList<Instance> listInstances = new ArrayList<Instance>();
		ArrayList<DtoInstance> listInstancesDto = this.search.GetAllInstancesWithClass(model, infModel);
    	for (DtoInstance dto : listInstancesDto) {
    		
    		if(! dto.Uri.contains("#"))
    		{
    			//nonstandard NS
    			throw new OKCoExceptionInstanceFormat("Entity namespace problem. The " + dto.Uri +" have to followed by \"#\".");
    		}
    		String nameSpace = dto.Uri.split("#")[0] + "#";
    		String name = dto.Uri.split("#")[1];
    		listInstances.add(new Instance(nameSpace, name, dto.ClassNameList, this. search.GetDifferentInstancesFrom(infModel, dto.Uri), search.GetSameInstancesFrom(infModel, dto.Uri),true));
		}
		
		return listInstances;
	}

	public OntModel UpdateInstanceInModel(Instance instance, OntModel model, InfModel infModel, ArrayList<Instance> ListAllInstances) {
		return this.factory.UpdateInstance(instance, model, infModel, ListAllInstances);
	}
	
	public void UpdateInstanceSameAndDifferentFrom(InfModel infModel, Instance instance)
	{
		instance.ListDiferentInstances = this.GetDifferentInstancesFrom(infModel, instance.ns + instance.name);
		instance.ListSameInstances = this.GetSameInstancesFrom(infModel, instance.ns + instance.name);
	}

	public ArrayList<String> GetDifferentInstancesFrom(InfModel infModel, String instanceName)
	{		
		return search.GetDifferentInstancesFrom(infModel, instanceName);
	}
	
	public ArrayList<String> GetSameInstancesFrom(InfModel infModel, String instanceName)
	{
		return this.search.GetSameInstancesFrom(infModel, instanceName);
	}

	public OntModel CreateTargetDataProperty(String instanceURI, String relation, String value, String TargetClass, OntModel model) {
		
		return this.factory.CreateTargetDataProperty(instanceURI, relation, value, TargetClass, model);
	}
	
	public OntModel DeleteTargetDataProperty(String instanceURI, String relation, String value, String TargetClass, OntModel model) {
		
		return this.factory.DeleteTargetDataProperty(instanceURI, relation, value, TargetClass, model);
	}
	
	public OntModel CreateRelationProperty(String instanceSourceURI, String relation, String instanceTargetURI, OntModel model)
	{
		return this.factory.CreateRelationProperty(instanceSourceURI, relation, instanceTargetURI, model);
	}
	
	public OntModel DeleteRelationProperty(String instanceSourceURI, String relation, String instanceTargetURI, OntModel model)
	{
		return this.factory.DeleteRelationProperty(instanceSourceURI, relation, instanceTargetURI, model);
	}

	public OntModel AddInstanceToClass(String instanceUri, String cls, OntModel model) {

		return this.factory.AddInstanceToClass(instanceUri, cls, model);
	}
	
	public OntModel RemoveInstanceOnClass(String instanceUri, String cls, OntModel model) {

		return this.factory.RemoveInstanceOnClass(instanceUri, cls, model);
	}
	
	public OntModel CreateInstanceAuto(String instanceSource, DtoDefinitionClass dtoSelected, Instance newInstance, OntModel model, InfModel infModel, ArrayList<Instance> ListAllInstances) {
		
		return this.CreateInstance(instanceSource, dtoSelected.Relation, newInstance, dtoSelected.Target, ListAllInstances, model);
		
	}
	
	public OntModel ClassifyInstanceAuto(Instance instance, OntModel model, InfModel infModel) {
		
		/* Check the subclasses are disjoint and complete */
		for (DtoCompleteClass dto : instance.ListCompleteClasses) 
		{
			boolean isDisjoint = true;
			for (String subCls : dto.Members)
			{
				for (String subCls2 : dto.Members) 
				{
					if(! subCls.equals(subCls2))
					{
						boolean result = this.search.CheckIsDijointClassOf(infModel, subCls, subCls2); /* Return true if subCls is disjoint of subCls2 */
						if(result == true)
						{
							//Not disjoint
							isDisjoint = false;
							
						} else {
							
							//isDisjoint = true;
						}
					}
				}
				
				if(isDisjoint == false)
				{
					break;
				}
			}
			
			if(isDisjoint == true && dto.Members.size() > 0 )
			{
				//Classify random
				model = this.AddInstanceToClass(instance.ns + instance.name, dto.Members.get(0), model);
			}
		}
		
		
		return model;
		
	}

	public OntModel CompleteInstanceAuto(Instance instance, String modelNameSpace, OntModel model, InfModel infModel, ArrayList<Instance> ListAllInstances)
	{
		//Classify instance classes
		model = this.ClassifyInstanceAuto(instance, model, infModel);
		
		//complete relations
		for (DtoDefinitionClass dto : instance.ListSome) 
		{
			if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
			{
				//create the the new instance
				String instanceName = dto.Target.split("#")[1] + "-" + (search.GetInstancesFromClass(model, infModel, dto.Target).size() + 1);
				ArrayList<String> listSame = new ArrayList<String>();		  
				ArrayList<String> listDif = new ArrayList<String>();
				ArrayList<String> listClasses = new ArrayList<String>();
				Instance newInstance = new Instance(modelNameSpace, instanceName, listClasses, listDif, listSame, false);
				
				model = this.CreateInstanceAuto(instance.ns + instance.name, dto, newInstance, model, infModel, ListAllInstances);
			}
		}
		for (DtoDefinitionClass dto : instance.ListMin) 
		{
			if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
			{
				int quantityInstancesTarget = search.CheckExistInstancesTargetCardinality(infModel, instance.ns + instance.name, dto.Relation, dto.Target, dto.Cardinality);
				
				ArrayList<String> listDif = new ArrayList<String>();
				while(quantityInstancesTarget < Integer.parseInt(dto.Cardinality))
				{
					//create the the new instance
					String instanceName = dto.Target.split("#")[1] + "-" + (quantityInstancesTarget + 1);
					ArrayList<String> listSame = new ArrayList<String>();		  
					ArrayList<String> listClasses = new ArrayList<String>();
					Instance newInstance = new Instance(modelNameSpace, instanceName, listClasses, listDif, listSame, false);
					
					model = this.CreateInstanceAuto(instance.ns + instance.name, dto, newInstance, model, infModel, ListAllInstances);				
					listDif.add(newInstance.ns + newInstance.name);
					quantityInstancesTarget ++;
				}
			}
					
		}
		for (DtoDefinitionClass dto : instance.ListExactly) 
		{
			if(dto.PropertyType.equals(EnumPropertyType.OBJECT_PROPERTY))
			{
				int quantityInstancesTarget = search.CheckExistInstancesTargetCardinality(infModel, instance.ns + instance.name, dto.Relation, dto.Target, dto.Cardinality);
				
				// Case 1 - same as min
				if(quantityInstancesTarget < Integer.parseInt(dto.Cardinality))
				{
					ArrayList<String> listDif = new ArrayList<String>();
					while(quantityInstancesTarget < Integer.parseInt(dto.Cardinality))
					{
						//create the the new instance
						String instanceName = dto.Target.split("#")[1] + "-" + (quantityInstancesTarget + 1);
						ArrayList<String> listSame = new ArrayList<String>();		  
						ArrayList<String> listClasses = new ArrayList<String>();
						Instance newInstance = new Instance(modelNameSpace, instanceName, listClasses, listDif, listSame, false);
						
						model = this.CreateInstanceAuto(instance.ns + instance.name, dto, newInstance, model, infModel, ListAllInstances);				
						listDif.add(newInstance.ns + newInstance.name);
						quantityInstancesTarget ++;
					}
				}
				
				// Case 2 - more individuals than necessary
				if(quantityInstancesTarget > Integer.parseInt(dto.Cardinality))
				{
											
				}
			}
		}
		
		
		return model;
	}

	public ArrayList<DtoDefinitionClass> removeRepeatValuesOn(Instance instanceSelected, EnumRelationTypeCompletness type) {

		ArrayList<DtoDefinitionClass> listDefinition = new ArrayList<DtoDefinitionClass>();
		
		if(type.equals(EnumRelationTypeCompletness.SOME))
		{
			for (DtoDefinitionClass dto : instanceSelected.ListSome) 
			{			
				boolean exist = false;
				for (DtoDefinitionClass dto2 : listDefinition) {
					if(dto.sameAs(dto2))
					{
						exist = true;
						break;
					}
				}
				
				if(exist == false)
				{
					listDefinition.add(dto);
					//dto.print();
				}			
			}
			
		} else if(type.equals(EnumRelationTypeCompletness.MIN))
		{
			for (DtoDefinitionClass dto : instanceSelected.ListMin) 
			{			
				boolean exist = false;
				for (DtoDefinitionClass dto2 : listDefinition) {

					//Doesn't compare the source
					if(dto.Relation == dto2.Relation && dto.Target == dto2.Target && dto.Cardinality.equals(dto2.Cardinality) && dto.PropertyType.equals(dto2.PropertyType))					
					{
						exist = true;
						break;
					}
				}
				
				if(exist == false)
				{
					listDefinition.add(dto);
					//dto.print();
				}			
			}
			
		} else  if(type.equals(EnumRelationTypeCompletness.MAX))
		{
			for (DtoDefinitionClass dto : instanceSelected.ListMax) 
			{			
				boolean exist = false;
				for (DtoDefinitionClass dto2 : listDefinition) {

					//Doesn't compare the source
					if(dto.Relation == dto2.Relation && dto.Target == dto2.Target && dto.Cardinality.equals(dto2.Cardinality) && dto.PropertyType.equals(dto2.PropertyType))					
					{
						exist = true;
						break;
					}
				}
				
				if(exist == false)
				{
					listDefinition.add(dto);
					//dto.print();
				}			
			}
			
		} else if(type.equals(EnumRelationTypeCompletness.EXACTLY))
		{
			for (DtoDefinitionClass dto : instanceSelected.ListExactly) 
			{			
				boolean exist = false;
				for (DtoDefinitionClass dto2 : listDefinition) {

					//Doesn't compare the source
					if(dto.Relation == dto2.Relation && dto.Target == dto2.Target && dto.Cardinality.equals(dto2.Cardinality) && dto.PropertyType.equals(dto2.PropertyType))					
					{
						exist = true;
						break;
					}
				}
				
				if(exist == false)
				{
					listDefinition.add(dto);
					//dto.print();
				}			
			}
		}
		
		
		return listDefinition;
	}

	public ArrayList<String> getClassesToClassify(Instance instanceSelected, InfModel infModel) {

		//Get all the subclasses without repeat
		ArrayList<String> listClassesMembersTmpWithoutRepeat = new ArrayList<String>();
		for (DtoCompleteClass dto : instanceSelected.ListCompleteClasses) {
			for (String clsComplete : dto.Members) {
				if(! listClassesMembersTmpWithoutRepeat.contains(clsComplete))
				{
					listClassesMembersTmpWithoutRepeat.add(clsComplete);					
				} 
			}
		}
		
		//Remove disjoint subclasses from some super class
		ArrayList<String> listClassesMembersTmp = new ArrayList<String>();
		for (DtoCompleteClass dto : instanceSelected.ListCompleteClasses) 
		{
			ArrayList<String> listDisjoint = search.GetDisjointClassesOf(dto.CompleteClass, infModel);
			for (String clc : listClassesMembersTmpWithoutRepeat) 
			{
				if(! listDisjoint.contains(clc))
				{
					listClassesMembersTmp.add(clc);
				}
			}			
		}
		return listClassesMembersTmp;
	}

	public OntModel setSameInstances(String i1URI, String i2URI, OntModel model)
	{
		Individual i1 = model.getIndividual(i1URI);
		Individual i2 = model.getIndividual(i2URI);
		
		i1.setSameAs(i2);
		i2.setSameAs(i1);
		
		return model;
	}
	
	public OntModel setDifferentInstances(String i1URI, String i2URI, OntModel model)
	{
		Individual i1 = model.getIndividual(i1URI);
		Individual i2 = model.getIndividual(i2URI);
		
		i1.setDifferentFrom(i2);
		i2.setDifferentFrom(i1);
		
		return model;
	}
}
