package br.ufes.inf.nemo.okco.controller;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ufes.inf.nemo.okco.model.DataPropertyValue;
import br.ufes.inf.nemo.okco.model.DtoClassifyInstancePost;
import br.ufes.inf.nemo.okco.model.DtoCommitMaxCard;
import br.ufes.inf.nemo.okco.model.DtoCommitPost;
import br.ufes.inf.nemo.okco.model.DtoCompleteClass;
import br.ufes.inf.nemo.okco.model.DtoCreateDataValuePost;
import br.ufes.inf.nemo.okco.model.DtoCreateInstancePost;
import br.ufes.inf.nemo.okco.model.DtoDefinitionClass;
import br.ufes.inf.nemo.okco.model.DtoGetPrevNextSpecProperty;
import br.ufes.inf.nemo.okco.model.DtoInstanceRelation;
import br.ufes.inf.nemo.okco.model.DtoPropertyAndSubProperties;
import br.ufes.inf.nemo.okco.model.DtoResultCommit;
import br.ufes.inf.nemo.okco.model.DtoViewSelectInstance;
import br.ufes.inf.nemo.okco.model.EnumPropertyType;
import br.ufes.inf.nemo.okco.model.EnumRelationTypeCompletness;
import br.ufes.inf.nemo.okco.model.Instance;
import br.ufes.inf.nemo.okco.model.OKCoExceptionInstanceFormat;
import br.ufes.inf.nemo.okco.visualizer.GraphPlotting;
import br.ufes.inf.nemo.okco.visualizer.WOKCOGraphPlotting;

@Controller
//@RequestMapping("/instance")
public class OKCoController {

	// Save the new instances before commit in views (completePropertyObject and completePropertyData)

	//Instances to add in relation
	ArrayList<Instance> listNewInstancesRelation;

	//DataValues to add in relation
	ArrayList<DataPropertyValue> listNewDataValuesRelation;

	//All instances in model
	ArrayList<Instance> ListAllInstances;

	//Dto selected
	DtoDefinitionClass dtoSelected;

	//Instance selected
	Instance instanceSelected;

	//Specialization - Complete classes for instance class
	ArrayList<DtoCompleteClass> ListCompleteClsInstaceSelected;

	//Specialization - Property and subProperties
	ArrayList<DtoPropertyAndSubProperties> ListSpecializationProperties;  

	/*------ Common -----*/	

	@RequestMapping(method = RequestMethod.GET, value="/list")
	public String list(HttpServletRequest request) {

		this.ListAllInstances = HomeController.ListAllInstances;

		if(ListAllInstances != null) 
		{
			request.getSession().setAttribute("listInstances", ListAllInstances);
			request.getSession().setAttribute("listModifedInstances", HomeController.ListModifiedInstances);

			return "list";	//View to return

		} else{
			request.getSession().setAttribute("loadOk", "false");
			return "index";

		}

	}

	@RequestMapping(method = RequestMethod.GET, value="/details")
	public String details(@RequestParam("id") String id, HttpServletRequest request) {

		// ----- Instance selected ----//

		instanceSelected = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(id));		

		// ----- Remove repeat values -------- //

		ArrayList<DtoDefinitionClass> listSomeClassDefinition = HomeController.ManagerInstances.removeRepeatValuesOn(instanceSelected, EnumRelationTypeCompletness.SOME);
		ArrayList<DtoDefinitionClass> listMinClassDefinition = HomeController.ManagerInstances.removeRepeatValuesOn(instanceSelected, EnumRelationTypeCompletness.MIN);	
		ArrayList<DtoDefinitionClass> listMaxClassDefinition = HomeController.ManagerInstances.removeRepeatValuesOn(instanceSelected, EnumRelationTypeCompletness.MAX);
		ArrayList<DtoDefinitionClass> listExactlyClassDefinition = HomeController.ManagerInstances.removeRepeatValuesOn(instanceSelected, EnumRelationTypeCompletness.EXACTLY);	

		// ------ Complete classes list ------//

		//ArrayList<String> listClassesMembersToClassify = HomeController.ManagerInstances.getClassesToClassify(instanceSelected, HomeController.InfModel);		

		// ----- List relations ----- //

		ArrayList<DtoInstanceRelation> instanceListRelationsFromInstance = HomeController.Search.GetInstanceRelations(HomeController.InfModel, instanceSelected.ns + instanceSelected.name); 		//Get instance relations

		ListCompleteClsInstaceSelected = instanceSelected.ListCompleteClasses;

		// ------ Specialization Properties list ------//

		ListSpecializationProperties = instanceSelected.ListSpecializationProperties;

		// ------ Create sections ------//

		//Specialization
		//classes ok
		request.getSession().setAttribute("ListSpecializationProperties", ListSpecializationProperties);

		//Definition
		request.getSession().setAttribute("listSomeClassDefinition", listSomeClassDefinition);
		request.getSession().setAttribute("listMinClassDefinition", listMinClassDefinition);
		request.getSession().setAttribute("listMaxClassDefinition", listMaxClassDefinition);
		request.getSession().setAttribute("listExactlyClassDefinition", listExactlyClassDefinition);

		//Information
		request.getSession().setAttribute("instanceListRelations", instanceListRelationsFromInstance);
		request.getSession().setAttribute("instanceSelected", instanceSelected);		
		request.getSession().setAttribute("listInstances", ListAllInstances);

		// ------  View to return ------//

		return "details";
	}

	@RequestMapping(method = RequestMethod.GET, value="/completeProperty")
	public String completeProperty(@RequestParam("idDefinition") String idDefinition, @RequestParam("idInstance") String idInstance, @RequestParam("type") String type, @RequestParam("propType") String propType, HttpServletRequest request) {

		//Instance selected
		Instance instance = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(idInstance));

		//Search for the definition class correctly

		dtoSelected = DtoDefinitionClass.get(instance.ListSome, Integer.parseInt(idDefinition));
		if(dtoSelected == null)
			dtoSelected = DtoDefinitionClass.get(instance.ListMin, Integer.parseInt(idDefinition));
		if(dtoSelected == null)
			dtoSelected = DtoDefinitionClass.get(instance.ListMax, Integer.parseInt(idDefinition));
		if(dtoSelected == null)
			dtoSelected = DtoDefinitionClass.get(instance.ListExactly, Integer.parseInt(idDefinition));

		//Create the sections

		request.getSession().setAttribute("definitionSelected", dtoSelected);
		request.getSession().setAttribute("instanceSelected", instance);
		request.getSession().setAttribute("propType", propType);

		if(type.equals("object"))
		{
			//List auxiliary
			listNewInstancesRelation = new ArrayList<Instance>();

			//Get all instances except the instance selected for Same/Different list		
			ArrayList<Instance> listInstancesSameDifferent = new ArrayList<Instance>(ListAllInstances);

			//get instances with had this relation
			ArrayList<String> listInstancesName = HomeController.Search.GetInstancesOfTargetWithRelation(HomeController.InfModel, instance.ns + instance.name, dtoSelected.Relation, dtoSelected.Target);

			//populate the list of instances with had this relation	    	
			ArrayList<Instance> listInstancesInRelation = HomeController.ManagerInstances.getIntersectionOf(ListAllInstances, listInstancesName);

			//Create others sections
			request.getSession().setAttribute("listInstancesInRelation", listInstancesInRelation);
			request.getSession().setAttribute("listInstancesSameDifferent", listInstancesSameDifferent);
			request.getSession().setAttribute("listInstances", ListAllInstances);

			//return view	    	
			return "completePropertyObject";

		} else if (type.equals("objectMax"))
		{
			//get instances with had this relation
			ArrayList<String> listInstancesName = HomeController.Search.GetInstancesOfTargetWithRelation(HomeController.InfModel, instance.ns + instance.name, dtoSelected.Relation, dtoSelected.Target);
			Collections.sort(listInstancesName);

			//populate the list of instances with had this relation	    	
			ArrayList<Instance> listInstancesInRelation = HomeController.ManagerInstances.getIntersectionOf(ListAllInstances, listInstancesName);

			request.getSession().setAttribute("listInstancesInRelation", listInstancesInRelation);

			return "completePropertyObjectMaxCard";

		} else if (type.equals("data"))
		{
			//List auxiliary
			listNewDataValuesRelation = new ArrayList<DataPropertyValue>();

			//Get values with this data property
			ArrayList<DataPropertyValue> listValuesInRelation = HomeController.Search.GetDataValuesOfTargetWithRelation(HomeController.InfModel, instance.ns + instance.name, dtoSelected.Relation, dtoSelected.Target);

			//Create others sections
			request.getSession().setAttribute("listValuesInRelation", listValuesInRelation);

			//return view
			return "completePropertyData";

		} else {

			return "index";
		}

	}

	@RequestMapping(method = RequestMethod.GET, value="/completePropertyAuto")
	public String completePropertyAuto(@RequestParam("idDefinition") String idDefinition, @RequestParam("idInstance") String idInstance, @RequestParam("type") String type, @RequestParam("propType") String propType, HttpServletRequest request) {

		/*
		 * ATTENTION: This function works only with object properties: min, exactly and some properties
		 * 
		 * */

		//Instance selected
		Instance instance = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(idInstance));

		//Search for the definition class correctly
		dtoSelected = DtoDefinitionClass.get(instance.ListSome, Integer.parseInt(idDefinition));
		EnumRelationTypeCompletness typeRelation = EnumRelationTypeCompletness.SOME;

		if(dtoSelected == null){
			dtoSelected = DtoDefinitionClass.get(instance.ListMin, Integer.parseInt(idDefinition));
			typeRelation = EnumRelationTypeCompletness.MIN;
		}
		if(dtoSelected == null){
			dtoSelected = DtoDefinitionClass.get(instance.ListMax, Integer.parseInt(idDefinition));
			typeRelation = EnumRelationTypeCompletness.MAX;
		}
		if(dtoSelected == null){
			dtoSelected = DtoDefinitionClass.get(instance.ListExactly, Integer.parseInt(idDefinition));
			typeRelation = EnumRelationTypeCompletness.EXACTLY;
		}		

		if(type.equals("object"))
		{
			if(typeRelation.equals(EnumRelationTypeCompletness.SOME))
			{
				//create the the new instance
				String instanceName = dtoSelected.Target.split("#")[1] + "-" + (HomeController.Search.GetInstancesFromClass(HomeController.Model, HomeController.InfModel, dtoSelected.Target).size() + 1);
				ArrayList<String> listSame = new ArrayList<String>();		  
				ArrayList<String> listDif = new ArrayList<String>();
				ArrayList<String> listClasses = new ArrayList<String>();
				Instance newInstance = new Instance(HomeController.NS, instanceName, listClasses, listDif, listSame, false);

				HomeController.Model = HomeController.ManagerInstances.CreateInstanceAuto(instance.ns + instance.name, dtoSelected, newInstance, HomeController.Model, HomeController.InfModel, HomeController.ListAllInstances);
				HomeController.ListModifiedInstances.add(newInstance.ns + newInstance.name);
				try {
					HomeController.UpdateAddIntanceInLists(newInstance.ns + newInstance.name);
				} catch (InconsistentOntologyException e) {
					
					e.printStackTrace();
				} catch (OKCoExceptionInstanceFormat e) {
					
					e.printStackTrace();
				}

			} else if(typeRelation.equals(EnumRelationTypeCompletness.MIN))
			{
				int quantityInstancesTarget = HomeController.Search.CheckExistInstancesTargetCardinality(HomeController.InfModel, instance.ns + instance.name, dtoSelected.Relation, dtoSelected.Target, dtoSelected.Cardinality);

				ArrayList<String> listDif = new ArrayList<String>();
				while(quantityInstancesTarget < Integer.parseInt(dtoSelected.Cardinality))
				{
					//create the the new instance
					String instanceName = dtoSelected.Target.split("#")[1] + "-" + (quantityInstancesTarget + 1);
					ArrayList<String> listSame = new ArrayList<String>();		  
					ArrayList<String> listClasses = new ArrayList<String>();
					Instance newInstance = new Instance(HomeController.NS, instanceName, listClasses, listDif, listSame, false);

					HomeController.Model = HomeController.ManagerInstances.CreateInstanceAuto(instance.ns + instance.name, dtoSelected, newInstance, HomeController.Model, HomeController.InfModel, HomeController.ListAllInstances);
					HomeController.ListModifiedInstances.add(newInstance.ns + newInstance.name);
					HomeController.ListModifiedInstances.add(newInstance.ns + newInstance.name);
					try {
						HomeController.UpdateAddIntanceInLists(newInstance.ns + newInstance.name);
					} catch (InconsistentOntologyException e) {
						
						e.printStackTrace();
					} catch (OKCoExceptionInstanceFormat e) {
						
						e.printStackTrace();
					}

					listDif.add(newInstance.ns + newInstance.name);
					quantityInstancesTarget ++;
				}				

			} else if(typeRelation.equals(EnumRelationTypeCompletness.EXACTLY))
			{
				int quantityInstancesTarget = HomeController.Search.CheckExistInstancesTargetCardinality(HomeController.InfModel, instance.ns + instance.name, dtoSelected.Relation, dtoSelected.Target, dtoSelected.Cardinality);				

				// Case 1 - same as min relation
				if(quantityInstancesTarget < Integer.parseInt(dtoSelected.Cardinality))
				{
					ArrayList<String> listDif = new ArrayList<String>();
					while(quantityInstancesTarget < Integer.parseInt(dtoSelected.Cardinality))
					{
						//create the the new instance
						String instanceName = dtoSelected.Target.split("#")[1] + "-" + (quantityInstancesTarget + 1);
						ArrayList<String> listSame = new ArrayList<String>();
						ArrayList<String> listClasses = new ArrayList<String>();
						Instance newInstance = new Instance(HomeController.NS, instanceName, listClasses, listDif, listSame, false);

						HomeController.Model = HomeController.ManagerInstances.CreateInstanceAuto(instance.ns + instance.name, dtoSelected, newInstance, HomeController.Model, HomeController.InfModel, HomeController.ListAllInstances);
						HomeController.ListModifiedInstances.add(newInstance.ns + newInstance.name);
						HomeController.ListModifiedInstances.add(newInstance.ns + newInstance.name);
						try {
							HomeController.UpdateAddIntanceInLists(newInstance.ns + newInstance.name);
						} catch (InconsistentOntologyException e) {
							
							e.printStackTrace();
						} catch (OKCoExceptionInstanceFormat e) {
							
							e.printStackTrace();
						}

						listDif.add(newInstance.ns + newInstance.name);
						quantityInstancesTarget ++;
					}
				}

				// Case 2 - more individuals than necessary
				if(quantityInstancesTarget > Integer.parseInt(dtoSelected.Cardinality))
				{

				}

			} else if(typeRelation.equals(EnumRelationTypeCompletness.MAX))
			{

			}


		}  else if (type.equals("data")){

			//Do nothing yet

		}

		//Add on list modified instances
		HomeController.ListModifiedInstances.add(instance.ns + instance.name);

		//Update InfModel without calling reasoner
		HomeController.InfModel = HomeController.Repository.CopyModel(HomeController.Model);

		//Update lists
		//HomeController.UpdateLists();

		//Update list instances modified
		HomeController.UpdateListsModified();

		return "redirect:list";
	}

	@RequestMapping(method = RequestMethod.GET, value="/completeInstanceAuto")
	public String completeInstanceAuto(@RequestParam("idInstance") String idInstance, HttpServletRequest request) {

		//Instance selected
		Instance instance = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(idInstance));

		HomeController.Model = HomeController.ManagerInstances.CompleteInstanceAuto(instance, HomeController.NS, HomeController.Model, HomeController.InfModel, HomeController.ListAllInstances);

		HomeController.ListModifiedInstances.add(instance.ns + instance.name);

		try {
			HomeController.UpdateLists();
		} catch (InconsistentOntologyException e) {
			e.printStackTrace();
		} catch (OKCoExceptionInstanceFormat e) {
			e.printStackTrace();
		}

		//Update list instances modified
		HomeController.UpdateListsModified();

		return "redirect:list";
	}

	@RequestMapping(method = RequestMethod.GET, value="/graphVisualizer")
	public String graphVisualizer(@RequestParam("id") String id, @RequestParam("typeView") String typeView, HttpServletRequest request) {

		String valuesGraph = "";
		int width;
		int height;
		String subtitle = "";
		GraphPlotting graphPlotting = new WOKCOGraphPlotting();
		Instance i;
		int num = 0;

		try  
		{  
			num = Integer.parseInt(id);  
		}  
		catch(NumberFormatException nfe)  
		{  
			typeView = "ALL";
		}

		//TypeView -> ALL/IN/OUT
		if(typeView.equals("ALL"))
		{

			//All instances
			valuesGraph  = graphPlotting.getArborStructureFor(HomeController.InfModel); 

		} else if(id != null && num > 0){

			//Get the instance
			i = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(id));

			if(typeView.equals("IN"))			//in on instance
			{				
				//Get the values
				valuesGraph  = graphPlotting.getArborStructureComingInOf(HomeController.InfModel, i.ns + i.name);

			} else if(typeView.equals("OUT")) {	//out from instance

				//Get the values
				valuesGraph  = graphPlotting.getArborStructureComingOutOf(HomeController.InfModel, i.ns + i.name);	
			}			
		}	

		width  = graphPlotting.width;
		height = graphPlotting.height;
		subtitle = graphPlotting.getSubtitle();

		//session
		request.getSession().setAttribute("valuesGraph", valuesGraph);
		request.getSession().setAttribute("width", width);
		request.getSession().setAttribute("height", height);
		request.getSession().setAttribute("subtitle", subtitle);

		return "graphVisualizer";

	}


	/*------ AJAX - ObjectProperty -----*/	

	@RequestMapping(value="/createInstance", method = RequestMethod.POST)
	public @ResponseBody Instance createInstance(@RequestBody final DtoCreateInstancePost dto){    

		String separatorValues = "%&&%";

		/* 0 -> name
		 * 1 -> arraySame
		 * 2 -> arrayDif
		 * */
		String name = dto.name;
		String[] arraySame = dto.arraySame.split(separatorValues);
		String[] arrayDif = dto.arrayDif.split(separatorValues);

		ArrayList<String> listSame = new ArrayList<String>();
		for (String s : arraySame) {			  
			if(!(s.equals("")))
				listSame.add(s);			
		}

		ArrayList<String> listDif = new ArrayList<String>();
		for (String s : arrayDif) {			  
			if(!(s.equals("")))
				listDif.add(s);			
		}

		Instance i = new Instance(HomeController.NS, name, new ArrayList<String>(), listDif, listSame, false);

		listNewInstancesRelation.add(i);
		return i;
	}

	@RequestMapping(value="/commitInstance", method = RequestMethod.POST)
	public @ResponseBody DtoResultCommit commitInstance(@RequestBody final DtoCommitPost dtoCommit) {    

		boolean isCreate = false;
		boolean isUpdate = false;
		DtoResultCommit dto = new DtoResultCommit();
		if(listNewInstancesRelation.size() != 0)
		{
			Instance iSource = instanceSelected;
			for (Instance iTarget : listNewInstancesRelation) 
			{
				try {

					if(iTarget.existInModel == false)
					{
						//Create instance
						HomeController.Model = HomeController.ManagerInstances.CreateInstance(iSource.ns + iSource.name, dtoSelected.Relation, iTarget, dtoSelected.Target, HomeController.ListAllInstances, HomeController.Model);
						isCreate = true;

					} else {

						//Selected instance
						HomeController.Model = HomeController.ManagerInstances.CreateRelationProperty(iSource.ns + iSource.name, dtoSelected.Relation, iTarget.ns + iTarget.name, HomeController.Model);
						isUpdate = true;
					}

					if(dtoCommit.commitReasoner.equals("true"))
					{
						//Update InfModel calling reasoner
						HomeController.InfModel = HomeController.Reasoner.run(HomeController.Model);

					} else {
						//Update InfModel without calling reasoner
						HomeController.InfModel = HomeController.Repository.CopyModel(HomeController.Model);

						//Add on list modified instances
						HomeController.ListModifiedInstances.add(iTarget.ns + iTarget.name);
					}			 

					//Update list
					//HomeController.UpdateLists();
					HomeController.UpdateAddIntanceInLists(iTarget.ns + iTarget.name);

				} catch (Exception e) {

					if(isCreate == true)
						HomeController.Model = HomeController.ManagerInstances.DeleteInstance(iTarget, HomeController.Model);

					if(isUpdate == true)
						HomeController.Model = HomeController.ManagerInstances.DeleteRelationProperty(iSource.ns + iSource.name, dtoSelected.Relation, iTarget.ns + iTarget.name, HomeController.Model);

					dto.result = e.getMessage();
					dto.ok = false;
					return dto;
				}

			} // end for

			if(dtoCommit.commitReasoner.equals("true"))
			{

			} else {
				//Add on list modified instances
				HomeController.ListModifiedInstances.add(iSource.ns + iSource.name);			
			}

			//Update list instances modified
			HomeController.UpdateListsModified();

			dto.ok = true;
			dto.result = "ok";
			return dto;
		}

		dto.ok = true;
		dto.result = "nothing";
		return dto;
	}

	@RequestMapping(value="/runReasoner", method = RequestMethod.POST)
	public @ResponseBody DtoResultCommit runReasoner() {    

		DtoResultCommit dto = new DtoResultCommit();
		try {

			//Run reasoner
			HomeController.InfModel = HomeController.Reasoner.run(HomeController.Model);

			//Save temporary model
			HomeController.tmpModel = HomeController.Repository.CopyModel(HomeController.Model);

			//Update list instances
			HomeController.UpdateLists();

			//Clean list modified instances
			HomeController.ListModifiedInstances = new ArrayList<String>();

			//Update list instances modified
			HomeController.UpdateListsModified();

		} catch (Exception e) {

			//Roll back the tempModel
			HomeController.Model = HomeController.Repository.CopyModel(HomeController.tmpModel);
			HomeController.InfModel = HomeController.Reasoner.run(HomeController.Model);

			//Update list instances
			try {
				HomeController.UpdateLists();

			} catch (InconsistentOntologyException e1) {

				//e1.printStackTrace();

			} catch (OKCoExceptionInstanceFormat e1) {

				//e1.printStackTrace();
			}

			String error = "Ontology have inconsistence:" + e.toString() + ". Return the last consistent model state.";

			dto.result = error;
			dto.ok = false;
			return dto;
		}

		dto.ok = true;
		dto.result = "ok";
		return dto;
	}

	@RequestMapping(value="/removeInstance", method = RequestMethod.GET)
	public @ResponseBody String removeInstance(@RequestParam String id) {    

		if(id != null)
		{
			Instance.removeFromList(listNewInstancesRelation, id);
			return id;
		}

		return null;		  
	}

	@RequestMapping(value="/editInstance", method = RequestMethod.GET)
	public @ResponseBody DtoViewSelectInstance editInstance(@RequestParam String id) {    

		if(id != null)
		{
			Instance i = HomeController.ManagerInstances.getInstance(listNewInstancesRelation, Integer.parseInt(id));
			DtoViewSelectInstance dto = new DtoViewSelectInstance(i, listNewInstancesRelation);
			return dto;
		}

		return null;	  
	}

	@RequestMapping(value="/selectInstance", method = RequestMethod.GET)
	public @ResponseBody DtoViewSelectInstance selectInstance(@RequestParam String id) {    

		if(id != null)
		{
			Instance i = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(id));
			DtoViewSelectInstance dto = new DtoViewSelectInstance(i, ListAllInstances);
			return dto;
		}

		return null;
	}

	@RequestMapping(value="/selectInstanceAdd", method = RequestMethod.GET)
	public @ResponseBody Instance selectInstanceAdd(@RequestParam String id) { 

		//Add in listNewInstancesRelation

		if(id != null)
		{
			Instance i = HomeController.ManagerInstances.getInstance(ListAllInstances, Integer.parseInt(id));
			listNewInstancesRelation.add(i);
			return i;
		}

		return null;
	}

	@RequestMapping(value="/commitMaxCard", method = RequestMethod.POST)
	public @ResponseBody DtoResultCommit commitMaxCard(@RequestBody final DtoCommitMaxCard dto){    
		
		DtoResultCommit dtoResult = new DtoResultCommit();
		
		try {
			
			String separatorValues = "%&&%";

			String[] arrayValues = dto.ListInstanceDifSameIds.split(separatorValues);
			for (String val : arrayValues) {

				if(val.contains("x"))
				{	
					String[] parts = val.split("x");

					String type = parts[0];
					String idInsSource = parts[1];
					String idInsTarget = parts[2];
					
					Instance s1 = HomeController.ManagerInstances.getInstance(HomeController.ListAllInstances, Integer.parseInt(idInsSource));
					Instance s2 = HomeController.ManagerInstances.getInstance(HomeController.ListAllInstances, Integer.parseInt(idInsTarget));
					
					if(type.equals("dif"))
					{
						HomeController.Model = HomeController.ManagerInstances.setDifferentInstances(s1.ns + s1.name, s2.ns + s2.name, HomeController.Model);
						
					} else if (type.equals("same"))
					{
						HomeController.Model = HomeController.ManagerInstances.setSameInstances(s1.ns + s1.name, s2.ns + s2.name, HomeController.Model);
						
					} else {
						
						dtoResult.result = "error";
						dtoResult.ok = false;
						return dtoResult;
					}
					
					HomeController.ListModifiedInstances.add(s1.ns + s1.name);
					HomeController.ListModifiedInstances.add(s2.ns + s2.name);
				}

			}
			
			if(dto.runReasoner.equals("true"))
			{
				try {

					//Run reasoner
					HomeController.InfModel = HomeController.Reasoner.run(HomeController.Model);

					//Save temporary model
					HomeController.tmpModel = HomeController.Repository.CopyModel(HomeController.Model);

					//Update list instances
					HomeController.UpdateLists();

					//Clean list modified instances
					HomeController.ListModifiedInstances = new ArrayList<String>();

					//Update list instances modified
					HomeController.UpdateListsModified();

				} catch (Exception e) {

					//Roll back the tempModel
					HomeController.Model = HomeController.Repository.CopyModel(HomeController.tmpModel);
					HomeController.InfModel = HomeController.Reasoner.run(HomeController.Model);

					//Update list instances
					try {
						HomeController.UpdateLists();

					} catch (InconsistentOntologyException e1) {

						//e1.printStackTrace();

					} catch (OKCoExceptionInstanceFormat e1) {

						//e1.printStackTrace();
					}

					String error = "Ontology have inconsistence:" + e.toString() + ". Return the last consistent model state.";
					
					dtoResult.result = error;
					dtoResult.ok = false;
					return dtoResult;
				}
				
			} if(dto.runReasoner.equals("false")) {
				
				//Update list instances modified
				HomeController.UpdateListsModified();
				
			} else {
				
				dtoResult.result = "error";
				dtoResult.ok = false;
				return dtoResult;
			}
			
		} catch (Exception e) {
			
			dtoResult.result = "error";
			dtoResult.ok = false;
			return dtoResult;
		}
		

		dtoResult.result = "ok";
		dtoResult.ok = true;
		return dtoResult;
	}
	
	/*------ AJAX - DataProperty -----*/	

	@RequestMapping(value="/removeDataValue", method = RequestMethod.GET)
	public @ResponseBody String removeDataValue(@RequestParam String id) {    

		if(id != null)
		{
			DataPropertyValue.removeFromList(listNewDataValuesRelation, id);
			return id;
		}

		return null;		  
	}

	@RequestMapping(value="/createDataValue", method = RequestMethod.POST)
	public @ResponseBody DataPropertyValue createDataValue(@RequestBody final DtoCreateDataValuePost dto){    

		DataPropertyValue data = new DataPropertyValue();
		data.value = dto.value;
		data.classValue = dtoSelected.Target;
		data.existInModel = false;

		listNewDataValuesRelation.add(data);
		return data;
	}

	@RequestMapping(value="/commitDataValues", method = RequestMethod.POST)
	public @ResponseBody DtoResultCommit commitDataValues(@RequestBody final DtoCommitPost dtoCommit) {    

		DtoResultCommit dto = new DtoResultCommit();
		if(listNewDataValuesRelation.size() != 0)
		{
			Instance iSource = instanceSelected;
			for (DataPropertyValue dataTarget : listNewDataValuesRelation) 
			{
				try {

					if(dataTarget.existInModel == false)
					{
						//Create data value
						HomeController.Model = HomeController.ManagerInstances.CreateTargetDataProperty(iSource.ns + iSource.name, dtoSelected.Relation, dataTarget.value, dtoSelected.Target, HomeController.Model);
						dataTarget.existInModel = true;
					}

					if(dtoCommit.commitReasoner.equals("true"))
					{
						//Update InfModel calling reasoner
						HomeController.InfModel = HomeController.Reasoner.run(HomeController.Model);

					} else {
						//Update InfModel without calling reasoner
						HomeController.InfModel = HomeController.Repository.CopyModel(HomeController.Model);

						//Add on list modified instances
						HomeController.ListModifiedInstances.add(iSource.ns + iSource.name);
					}						 

					//Update list instances
					HomeController.UpdateLists();

				} catch (Exception e) {

					HomeController.Model = HomeController.ManagerInstances.DeleteTargetDataProperty(instanceSelected.ns + instanceSelected.name, dtoSelected.Relation, dataTarget.value, dtoSelected.Target, HomeController.Model);

					dto.result = e.getMessage();
					dto.ok = false;
					return dto;
				}
			} //end for

			//Update list instances modified
			HomeController.UpdateListsModified();

			dto.ok = true;
			dto.result = "ok";
			return dto;
		} //end if

		dto.ok = true;
		dto.result = "nothing";
		return dto;
	}


	/*------ AJAX - Specializations -----*/	

	@RequestMapping(value="/classifyInstanceClasses", method = RequestMethod.POST)
	public @ResponseBody DtoResultCommit classifyInstanceClasses(@RequestBody final DtoClassifyInstancePost dto) throws InconsistentOntologyException, OKCoExceptionInstanceFormat{    

		DtoResultCommit dtoResult = new DtoResultCommit();
		String separatorValues = "%&&%";

		/* 0 -> arrayCls */
		String[] arrayCls = dto.arrayCls.split(separatorValues);

		ArrayList<String> listCls = new ArrayList<String>();
		for (String s : arrayCls) {			  
			if(!(s.equals("")))
				listCls.add(s);			
		}

		if(listCls.size() > 0)
		{
			for (String cls : listCls) {

				try {

					HomeController.Model = HomeController.ManagerInstances.AddInstanceToClass(instanceSelected.ns + instanceSelected.name, cls, HomeController.Model);

				} catch (Exception e) {

					dtoResult.result = e.getMessage();
					dtoResult.ok = false;
					return dtoResult;
				}
			}

			try {

				//Validate and update list
				HomeController.UpdateAddIntanceInLists(instanceSelected.ns + instanceSelected.name);;

				//Instance selected update
				instanceSelected = HomeController.ManagerInstances.getInstance(ListAllInstances, instanceSelected .id);

			} catch (Exception e) {

				dtoResult.result = e.getMessage();
				dtoResult.ok = false;

				//Remove all created
				for (String clsAux : listCls) {
					HomeController.Model = HomeController.ManagerInstances.RemoveInstanceOnClass(instanceSelected.ns + instanceSelected.name, clsAux, HomeController.Model);
				}

				//Validate and update list and infModel
				HomeController.UpdateLists();

				//Instance selected update
				instanceSelected = HomeController.ManagerInstances.getInstance(ListAllInstances, instanceSelected .id);

				return dtoResult;
			}	

			//Add on list modified instances
			HomeController.ListModifiedInstances.add(instanceSelected.ns + instanceSelected.name);

			//Update list instances modified
			HomeController.UpdateListsModified();

			dtoResult.ok = true;
			dtoResult.result = "ok";
			return dtoResult;
		}

		dtoResult.ok = true;
		dtoResult.result = "nothing";
		return dtoResult;		  
	}

	@RequestMapping(value="/classifyInstanceProperty", method = RequestMethod.POST)
	public @ResponseBody DtoResultCommit classifyInstanceProperty(@RequestBody final DtoClassifyInstancePost dto) throws InconsistentOntologyException, OKCoExceptionInstanceFormat{

		DtoPropertyAndSubProperties dtoSpec = DtoPropertyAndSubProperties.getInstance(ListSpecializationProperties, Integer.parseInt(dto.id));

		DtoResultCommit dtoResult = new DtoResultCommit();
		String separatorValues = "%&&%";

		/* 0 -> arrayCls */
		String[] arraySubProp = dto.arraySubProp.split(separatorValues);

		ArrayList<String> listRelations = new ArrayList<String>();
		for (String s : arraySubProp) {			  
			if(!(s.equals("")))
				listRelations.add(s);			
		}

		if(listRelations.size() > 0)
		{
			for (String subRel : listRelations) {

				try {

					if(dtoSpec.propertyType.equals(EnumPropertyType.DATA_PROPERTY))
						//Case data property
						HomeController.Model = HomeController.ManagerInstances.CreateTargetDataProperty(instanceSelected.ns + instanceSelected.name, subRel, dtoSpec.iTargetNs.split("\\^\\^")[0], dtoSpec.iTargetNs.split("\\^\\^")[1] + dtoSpec.iTargetName, HomeController.Model);
					else
						//Case object property
						HomeController.Model = HomeController.ManagerInstances.CreateRelationProperty(instanceSelected.ns + instanceSelected.name, subRel, dtoSpec.iTargetNs + dtoSpec.iTargetName, HomeController.Model);

				} catch (Exception e) {

					dtoResult.result = e.getMessage();
					dtoResult.ok = false;
					return dtoResult;
				}
			}

			try {
				
				//Validate and update list
				HomeController.UpdateAddIntanceInLists(instanceSelected.ns + instanceSelected.name);

				//Instance selected update
				instanceSelected = HomeController.ManagerInstances.getInstance(ListAllInstances, instanceSelected .id);

			} catch (Exception e) {

				dtoResult.result = e.getMessage();
				dtoResult.ok = false;

				//Remove all created
				for (String subRelAux : listRelations) {

					if(dtoSpec.propertyType.equals(EnumPropertyType.DATA_PROPERTY))
						//Case data property
						HomeController.Model = HomeController.ManagerInstances.DeleteTargetDataProperty(instanceSelected.ns + instanceSelected.name, subRelAux, dtoSpec.iTargetNs.split("\\^\\^")[0], dtoSpec.iTargetNs.split("\\^\\^")[1] + dtoSpec.iTargetName, HomeController.Model);
					else
						//Case object property
						HomeController.Model = HomeController.ManagerInstances.DeleteRelationProperty(instanceSelected.ns + instanceSelected.name, subRelAux, dtoSpec.iTargetNs + dtoSpec.iTargetName, HomeController.Model);
				}

				//Validate and update list and infModel
				HomeController.UpdateLists();

				//Instance selected update
				instanceSelected = HomeController.ManagerInstances.getInstance(ListAllInstances, instanceSelected .id);

				return dtoResult;
			}			  

			//Add on list modified instances
			HomeController.ListModifiedInstances.add(instanceSelected.ns + instanceSelected.name);

			//Update list instances modified
			HomeController.UpdateListsModified();

			dtoResult.ok = true;
			dtoResult.result = "ok";
			return dtoResult;
		}

		dtoResult.ok = true;
		dtoResult.result = "nothing";
		return dtoResult;		  
	}

	@RequestMapping(value="/selectSpecializationProp", method = RequestMethod.GET)
	public @ResponseBody DtoGetPrevNextSpecProperty selectSpecializationProp(@RequestParam String id) {    

		if(id != null)
		{
			DtoPropertyAndSubProperties dto = DtoPropertyAndSubProperties.getInstance(ListSpecializationProperties, Integer.parseInt(id));
			if(dto == null){

				return null;

			} else {

				boolean haveNext = false;
				boolean havePrev = false;
				DtoPropertyAndSubProperties dtoNext = DtoPropertyAndSubProperties.getInstance(ListSpecializationProperties, Integer.parseInt(id) + 1);
				DtoPropertyAndSubProperties dtoPrev= DtoPropertyAndSubProperties.getInstance(ListSpecializationProperties, Integer.parseInt(id) - 1);
				if(dtoNext != null)
					haveNext = true;
				if(dtoPrev != null)
					havePrev = true;

				DtoGetPrevNextSpecProperty data = new DtoGetPrevNextSpecProperty(instanceSelected.name, instanceSelected.ns, dto, haveNext, havePrev);				  
				return data;
			}
		}

		return null;
	}


}
