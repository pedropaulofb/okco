package br.ufes.inf.nemo.okco.business;

import java.util.ArrayList;

import br.ufes.inf.nemo.okco.model.DtoDefinitionClass;
import br.ufes.inf.nemo.okco.model.Instance;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;

public class FactoryInstances {

	public Search Search;
	
	public FactoryInstances(Search s)
	{
		Search = s;
	}

	public OntModel CreateInstance(String instanceSource, String Relation, Instance instanceNew, String TargetClass, ArrayList<Instance> ListAllInstances, OntModel model)
	{
		ManagerInstances manager = new ManagerInstances(this.Search, null, null);	
		
		//Get instance, class, property
		Individual indInstance = model.getIndividual(instanceSource);
		OntClass ClassImage = model.getOntClass(TargetClass);
		Property relation = model.getProperty(Relation);
		
		//Create individual	
		Individual newInstance = ClassImage.createIndividual(instanceNew.ns + instanceNew.name);
		
		//Add same and different
		for (String s : instanceNew.ListDiferentInstances) 
		{
			Instance ins = manager.getInstance(ListAllInstances, s);
			if(!(ins == null))
			{
				Individual i = model.getIndividual(s);
				i.setDifferentFrom(newInstance);
			}
		}
		
		for (String s : instanceNew.ListSameInstances) 
		{
			Instance ins = manager.getInstance(ListAllInstances, s);
			if(!(ins == null))
			{
				Individual i = model.getIndividual(s);
				i.setSameAs(newInstance);
			}
		}
		
		//Add relation
		indInstance.addProperty(relation, newInstance);
		
		//Update new instance values
		instanceNew.existInModel = true;
		
		//Update List of All instances
		//Aplication.ListAllInstances.add(instanceNew);
		
		return model;
	}
	
	public OntModel UpdateInstance(Instance instance, OntModel model, InfModel infModel, ArrayList<Instance> ListAllInstances)
	{
		ManagerInstances manager = new ManagerInstances(this.Search, null, null);
		
		//Get instance, class, property
		Individual indInstance = model.getIndividual(instance.ns + instance.name);
		
		//Remove the different
		for (String s : manager.GetDifferentInstancesFrom(infModel, instance.ns + instance.name)) 
		{
			Individual i = model.getIndividual(s);
			indInstance.removeDifferentFrom(i);
		}
		
		//Remove the same
		for (String s : manager.GetSameInstancesFrom(infModel, instance.ns + instance.name)) 
		{
			Individual i = model.getIndividual(s);
			indInstance.removeSameAs(i);
		}
		
		//Add different
		for (String s : instance.ListDiferentInstances) 
		{
			Instance ins = manager.getInstance(ListAllInstances, s);
			if(!(ins == null))
			{
				Individual i = model.getIndividual(s);
				i.setDifferentFrom(indInstance);
			}
		}
		
		//Add same
		for (String s : instance.ListSameInstances) 
		{
			Instance ins = manager.getInstance(ListAllInstances, s);
			if(!(ins == null))
			{
				Individual i = model.getIndividual(s);
				i.setSameAs(indInstance);
			}
		}
					
		return model;
	}
	
	public OntModel DeleteInstance(Instance instance, OntModel model) {

		Individual ind = model.getIndividual(instance.ns + instance.name);
		ind.remove();	// remove every statement that mentions this resource as a subject or object of a statement.		
		return model;
	}

	public OntModel CreateTargetDataProperty(String instanceURI, String relationName, String value, String TargetClass, OntModel model) {
		
		//Get instance, class, property
		Individual indInstance = model.getIndividual(instanceURI);
		Literal literal = model.createTypedLiteral(value,TargetClass);
		Property relation = model.getDatatypeProperty(relationName);	
		indInstance.addProperty(relation, literal);
		
		return model;
	}

	public OntModel DeleteTargetDataProperty(String instanceURI, String relationName, String value, String TargetClass, OntModel model) {

		//Get instance, class, property
		Individual indInstance = model.getIndividual(instanceURI);
		Literal literal = model.createTypedLiteral(value,TargetClass);
		Property relation = model.getDatatypeProperty(relationName);	
		indInstance.removeProperty(relation, literal);
				
		return model;
	}

	public OntModel CreateRelationProperty(String instanceSourceURI, String relationName, String instanceTargetURI,	OntModel model) {
		
		Individual indInstanceSource = model.getIndividual(instanceSourceURI);
		Individual indInstanceTarget = model.getIndividual(instanceTargetURI);
		Property relation = model.getProperty(relationName);
		indInstanceSource.addProperty(relation, indInstanceTarget);
		
		return model;
	}
	
	public OntModel DeleteRelationProperty(String instanceSourceURI, String relationName, String instanceTargetURI,	OntModel model) {

		//Get instance, class, property
		Individual indInstanceSource = model.getIndividual(instanceSourceURI);
		Individual indInstanceTarget = model.getIndividual(instanceTargetURI);
		Property relation = model.getProperty(relationName);	
		indInstanceSource.removeProperty(relation, indInstanceTarget);
				
		return model;
	}

	public OntModel AddInstanceToClass(String instanceUri, String clsUri, OntModel model) {

		//Get instance, class, property
		Individual indInstance = model.getIndividual(instanceUri);
		OntClass cls = model.getOntClass(clsUri);
		
		//Add to class
		indInstance.addOntClass(cls);
		
		return model;
	}
	
	public OntModel RemoveInstanceOnClass(String instanceUri, String clsUri, OntModel model) {

		//Get instance, class, property
		Individual indInstance = model.getIndividual(instanceUri);
		OntClass cls = model.getOntClass(clsUri);
		
		//Remove individual on class
		indInstance.removeOntClass(cls);
		
		return model;
	}

	
	
	
	
	/* Old functions */
	

	public OntModel CreateIndividualsForSomeRelations(ArrayList<DtoDefinitionClass> dtoSomeList, OntModel model, InfModel infModel) {

		//This one is use to capture all DtoDefinitionResult with new instance created
		ArrayList<DtoDefinitionClass> dtoSomeListNew = new ArrayList<DtoDefinitionClass>();
		
		for (DtoDefinitionClass dtoResult : dtoSomeList) {

			// List instances from Class Source
			ArrayList<String> listInstances = this.Search.GetInstancesFromClass(model, infModel, dtoResult.Source);
			System.out.println("-> " + dtoResult.Source.toString() + " - n individuos - " + listInstances.size());
			
			if(listInstances.size() > 0)	//Exist instances
			{
				//Check the list of instances
				for (String instance : listInstances) {
					
					System.out.println("\n\n#Checando o individuo: "+ instance +"#");
					
					//Here we check the instance and launch the reasoner
					System.out.println("-- REASONER ");
					boolean existInstanceTarget = this.Search.CheckExistInstanceTarget(model, instance, dtoResult.Relation, dtoResult.Target);
					if(existInstanceTarget)
					{
						//Do nothing
						System.out.println("---- Faz nada ");
						
					} else {
						
						System.out.println("---- Criando instância");
						
						//Add too dtoSomeListNew
						dtoSomeListNew.add(dtoResult);
						
						//Get instance, class, property
						Individual indInstance = model.getIndividual(instance);
						OntClass ClassImage = model.getOntClass(dtoResult.Target);
						Property relation = model.getProperty(dtoResult.Relation);				
						
						//Create individual
						String instanceName = dtoResult.Target + "-" + (this.Search.GetInstancesFromClass(model, infModel, dtoResult.Target).size() + 1);
						Individual newInstance = ClassImage.createIndividual(instanceName);
						
						//Add relation
						System.out.println("--------" + indInstance.getURI() + " -> "+ relation + " -> " + newInstance.getURI());
						indInstance.addProperty(relation, newInstance);
						System.out.println("---- Individuo " + newInstance.getURI());
					}
				}
			}
		}
		
		if(dtoSomeListNew.size() > 0)
		{
			//We need to check the list again because we create new individuals for than
			
			System.out.println("\n--------------------- Loop ------------------------\n");
			model = this.CreateIndividualsForSomeRelations(dtoSomeList, model, infModel);
		} else {
			
			return model;
		}
		
		return model;
	}

	public OntModel CreateIndividualsForMinRestriction(ArrayList<DtoDefinitionClass> dtoMinRelationsList, OntModel model, InfModel infModel) {

		//This one is use to capture all DtoTripleResult with new instance created
		ArrayList<DtoDefinitionClass> dtoMinListNew = new ArrayList<DtoDefinitionClass>();
		
		for (DtoDefinitionClass dtoTripleResult : dtoMinRelationsList) {
			
			// List instances from Class Source
			ArrayList<String> listInstances = this.Search.GetInstancesFromClass(model, infModel, dtoTripleResult.Source);
			System.out.println("-> " + dtoTripleResult.Source.toString() + " - n individuos - " + listInstances.size());
			
			if(listInstances.size() > 0)	//Exist instances
			{
				//Check the list of instances
				for (String instance : listInstances) {
				
					System.out.println("\n\n#Checando o individuo: "+ instance +"#");
					
					//Here we check the instance and launch the reasoner
					System.out.println("-- REASONER ");
					int quantityInstancesTarget = this.Search.CheckExistInstancesTargetCardinality(model, instance, dtoTripleResult.Relation, dtoTripleResult.Target, dtoTripleResult.Cardinality);
					
					System.out.println("Quantidade no target: " + quantityInstancesTarget);
					
					if(quantityInstancesTarget < Integer.parseInt(dtoTripleResult.Cardinality))
					{
						System.out.println("---- Criando instâncias - cardialidade desejada = " + dtoTripleResult.Cardinality);
						
						//Add too dtoSomeListNew
						dtoMinListNew.add(dtoTripleResult);
						
						for (int i = quantityInstancesTarget; i < Integer.parseInt(dtoTripleResult.Cardinality); i++) {
							
							//Get instance, class, property
							Individual indInstance = model.getIndividual(instance);
							OntClass ClassImage = model.getOntClass(dtoTripleResult.Target);
							Property relation = model.getProperty(dtoTripleResult.Relation);				
							
							//Create individual
							String instanceName = dtoTripleResult.Target + "-" + (this.Search.GetInstancesFromClass(model, infModel, dtoTripleResult.Target).size() + 1);
							Individual newInstance = ClassImage.createIndividual(instanceName);
							
							//Add relation
							indInstance.addProperty(relation, newInstance);
							System.out.println("---- #Criou Individuo " + newInstance.getURI());
						}

					} else {
						//Do nothing
						System.out.println("---- Faz nada ");
						
					}
				}
			}
		}
		
		return model;
	}	

	public OntModel CreateIndividualsForExactlyRestriction(ArrayList<DtoDefinitionClass> dtoExactlyRelationsList, OntModel model, InfModel infModel)
	{
		//This one is use to capture all DtoTripleResult with new instance created
		ArrayList<DtoDefinitionClass> dtoExactlyListNew = new ArrayList<DtoDefinitionClass>();
		
		for (DtoDefinitionClass dtoTripleResult : dtoExactlyRelationsList) {
			
			// List instances from Class Source
			ArrayList<String> listInstances = this.Search.GetInstancesFromClass(model, infModel, dtoTripleResult.Source);
			System.out.println("-> " + dtoTripleResult.Source.toString() + " - n individuos - " + listInstances.size());
			
			if(listInstances.size() > 0)	//Exist instances
			{
				//Check the list of instances
				for (String instance : listInstances) {
				
					System.out.println("\n\n#Checando o individuo: "+ instance +"#");
					
					//Here we check the instance and launch the reasoner
					System.out.println("-- REASONER ");
					int quantityInstancesTarget = this.Search.CheckExistInstancesTargetCardinality(model, instance, dtoTripleResult.Relation, dtoTripleResult.Target, dtoTripleResult.Cardinality);
					
					System.out.println("Quantidade no target: " + quantityInstancesTarget);
					
					// Case 1 - same as min relation
					if(quantityInstancesTarget < Integer.parseInt(dtoTripleResult.Cardinality))
					{
						System.out.println("---- Criando instâncias - cardialidade desejada = " + dtoTripleResult.Cardinality);
						
						//Add too dtoSomeListNew
						dtoExactlyListNew.add(dtoTripleResult);
						
						for (int i = quantityInstancesTarget; i < Integer.parseInt(dtoTripleResult.Cardinality); i++) {
							
							//Get instance, class, property
							Individual indInstance = model.getIndividual(instance);
							OntClass ClassImage = model.getOntClass(dtoTripleResult.Target);
							Property relation = model.getProperty(dtoTripleResult.Relation);				
							
							//Create individual
							String instanceName = dtoTripleResult.Target + "-" + (this.Search.GetInstancesFromClass(model, infModel, dtoTripleResult.Target).size() + 1);
							Individual newInstance = ClassImage.createIndividual(instanceName);
							
							//Add relation
							indInstance.addProperty(relation, newInstance);
							System.out.println("---- #Criou Individuo " + newInstance.getURI());
						}

					// Case 2 - more individuals than necessary
					} else if (quantityInstancesTarget > Integer.parseInt(dtoTripleResult.Cardinality)) {

						System.out.println("---- Mais individuos do que o necessário. Excluir individuos ");
						
					} else {
						//Do nothing
						System.out.println("---- Faz nada ");
						
					}
				}
			}
		}
		
		return model;
	}

	
}
