package br.ufes.inf.nemo.okco.business;
import java.util.ArrayList;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import br.ufes.inf.nemo.okco.model.DataPropertyValue;
import br.ufes.inf.nemo.okco.model.DomainRange;
import br.ufes.inf.nemo.okco.model.DtoCompleteClass;
import br.ufes.inf.nemo.okco.model.DtoDefinitionClass;
import br.ufes.inf.nemo.okco.model.DtoInstance;
import br.ufes.inf.nemo.okco.model.DtoInstanceRelation;
import br.ufes.inf.nemo.okco.model.EnumPropertyType;
import br.ufes.inf.nemo.okco.model.EnumRelationTypeCompletness;
import br.ufes.inf.nemo.okco.model.Instance;
import br.ufes.inf.nemo.okco.model.OKCoExceptionInstanceFormat;
import br.ufes.inf.nemo.okco.model.RelationDomainRangeList;

public class Search {

	public String NS;
	public final  String w3String = "http://www.w3.org/";
	
	public Search(String NameSpace)
	{
		NS = NameSpace;
	}

	/*
	 * General search
	 */
	
	public ArrayList<String> GetProperties(OntModel model) {
		
		ArrayList<String> lista = new ArrayList<String>();
		ExtendedIterator<OntProperty> i = model.listOntProperties();
		if( !i.hasNext() ) {
			//System.out.print( "none" );
		}
		else {
			while( i.hasNext() ) {
				Resource val = (Resource) i.next();
				lista.add( val.getURI() );
			}
		}
		return lista;
	}
	
	public ArrayList<String> GetClasses(OntModel model) {
		
		ArrayList<String> lista = new ArrayList<String>();
		ExtendedIterator<OntClass> i = model.listClasses();
		if( !i.hasNext() ) {
			//System.out.print( "none" );
		}
		else {
			while( i.hasNext() ) {
				Resource val = (Resource) i.next();
				lista.add( val.getURI() );
			}
		}
		return lista;
	}
	
	public ArrayList<String> GetInstancesFromClass(OntModel model, InfModel infModel, String className) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT *" +
		" WHERE {\n" +		
			" ?i rdf:type <" + className + "> .\n " +	
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);

		while (results.hasNext()) {
			
			QuerySolution row = results.next();		    
		    RDFNode i = row.get("i");		    
		    list.add(i.toString());
		}	

		return list;		
	}
	
	public ArrayList<String[]> GetSourceAndTargetForProperty(OntModel model, String propName) {
		
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		// Create a new query
		String queryString = 
		" SELECT *" +
		" WHERE {\n" +		
			" ?source <" + propName + "> ?target .\n " +	
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		
		while (results.hasNext()) {
			String [] tupla = new String[2];
			QuerySolution row = results.next();		    
		    RDFNode source = row.get("source");
		    RDFNode target = row.get("target");
		    
		    tupla[0] = source.toString();
		    tupla[1] = target.toString();
		    list.add(tupla);
		}	

		return list;		
	}
			
	public ArrayList<String> GetAllInstances(OntModel model, InfModel infModel)
	{		
		ArrayList<String> AllInstances = new ArrayList<String>();
		ArrayList<String> AllClasses = this.GetClasses(model);
		//System.out.println("-> " + AllClasses.size());
		for (String className : AllClasses) {
			
			if(!(className == null)){
				ArrayList<String> InstancesFromClass = this.GetInstancesFromClass(model, infModel, className);
				for (String instance : InstancesFromClass) {
					if (!(AllInstances.contains(instance)))
						AllInstances.add(instance);
				}
			}
		}
		
		return AllInstances;
	}
	
	public ArrayList<DtoInstance> GetAllInstancesWithClass(OntModel model, InfModel infModel)
	{		
		ArrayList<DtoInstance> AllInstances = new ArrayList<DtoInstance>();
		
		ArrayList<String> AllClasses = this.GetClasses(model);
		AllClasses.add("http://www.w3.org/2002/07/owl#Thing");
		
		DtoInstance dto = null;
		
		for (String className : AllClasses) {
			
			if(!(className == null)){
				
				ArrayList<String> InstancesFromClass = this.GetInstancesFromClass(model, infModel, className);
				for (String instance : InstancesFromClass) {
					
					dto = DtoInstance.getInstance(instance, AllInstances);
					if (dto == null)
					{
						//If dto doesn't exists						
						dto = new DtoInstance(instance, className);
						AllInstances.add(dto);
						
					} else {
						
						//Just add class
						dto.AddClass(className);
					}
					
				}
		
			}
		}
		
		return AllInstances;
	}

	
	/*
	 * Class/Instance search
	 */
	
	public ArrayList<String> GetDifferentInstancesFrom(InfModel infModel, String instanceName)
	{		
		ArrayList<String> list = new ArrayList<String>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?y " +
		" WHERE {\n" +		
			"{ " + 
				"<" + instanceName + "> owl:differentFrom" + " ?y .\n " +
			"} UNION { " +
				" ?y owl:differentFrom <" + instanceName + "> .\n " +
			" } " +
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		//System.out.println("-> " + instanceName);
		while (results.hasNext()) {
			QuerySolution row = results.next();
		    
		    RDFNode rdfY = row.get("y");
		    if(! instanceName.equals(rdfY.toString()))
		    {
		    	list.add(rdfY.toString());
		    	//System.out.println("-------> " + rdfY.toString());
		    }
		}	
		//System.out.println("");

		return list;
	}
	
	public ArrayList<String> GetSameInstancesFrom(InfModel infModel, String instanceName)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?y" +
		" WHERE {\n" +		
			"{ " + 
				"<" + instanceName + "> owl:sameAs" + " ?y .\n " +
			"} UNION { " +
				" ?y owl:sameAs <" + instanceName + "> .\n " +
			" } " +
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row = results.next();
		    
		    RDFNode rdfY = row.get("y");
		    if(! instanceName.equals(rdfY.toString()))
		    {
		    	list.add(rdfY.toString());
		    }
		}		

		return list;
	}
	
	public ArrayList<String> GetPropertiesFromClass(OntModel model,String className) {
		
		ArrayList<String> lista = new ArrayList<String>();

		ExtendedIterator<OntClass> i = model.listClasses();
		if( !i.hasNext() ) {
			//System.out.print( "none" );
		}
		else {
			while( i.hasNext() ) {
				Resource val = (Resource) i.next();
				//System.out.println( val.getURI());
			}
		}
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y ?z" +
		" WHERE {\n" +
				" ?x " + "owl:equivalentClass" + " _:b0 .\n " +
				" _:b0 " + "owl:onProperty" + " ?y .\n " +
				" _:b0 " + "owl:someValuesFrom ?z . " +
				
				//" _:b rdf:type ?tipoX ." +
				//" ?tipoX rdfs:subClassOf owl:Thing . " +
				//"?r owl:onProperty ?p . " +
				
			"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		ResultSetFormatter.out(System.out, results, query);
		
		//while (results.hasNext()) {
		//	QuerySolution row= results.next();
		    //RDFNode rdfConcept = row.get("rec");
		    
		//}		
		return lista;
	}

	public boolean CheckExistInstanceTarget(InfModel infModel, String instance, String relation, String imageClass) {

		Boolean result = false;

		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <"+ NS + ">" +
		"\n SELECT DISTINCT ?x" +
		" WHERE {\n" +
				" <" + instance + "> <" + relation + "> ?x .\n " +
				" ?x" + " rdf:type" + " <"+ imageClass + "> .\n " +
			"}";

		//System.out.println(queryString + "\n");
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row = results.next();
		    RDFNode rdfInstance = row.get("x");
		    result = true;
		}
		
		// Check Data Property
		
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <"+ NS + ">" +
		"\n SELECT DISTINCT ?x" +
		" WHERE {\n" +
				" <" + instance + "> <" + relation + "> ?x .\n " +
				" <" + relation + "> rdf:type owl:DatatypeProperty .\n " +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe2 = QueryExecutionFactory.create(query, infModel);
		ResultSet results2 = qe2.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results2.hasNext()) {
			QuerySolution row = results2.next();
		    RDFNode rdfInstance = row.get("x");
		    if(rdfInstance.toString().contains(imageClass))
		    {
		    	result = true;
		    }
		}	

		return result;
	}
	
	public EnumPropertyType GetPropertyType(InfModel infModel, String property)
	{
		/* Return the type of property */
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +
				"<" + property + "> " + " rdf:type " + " ?type .\n " +
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//System.out.println(queryString);
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode t = row.get("type");
		    String type = t.toString();		    
		    if(type.contains("DatatypeProperty"))
		    {
		    	return EnumPropertyType.DATA_PROPERTY;
		    }
		    if(type.contains("ObjectProperty"))
		    {
		    	return EnumPropertyType.OBJECT_PROPERTY;
		    }
		}
		
		return null;
	}
	
 	public boolean CheckIsDijointClassOf(InfModel infModel, String cls, String clsToCheck)
	{
		/* Return true if cls is disjoint of clsToCheck */
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +
				"<" + cls + "> " + "owl:disjointWith" + " ?classD .\n " +
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode classD = row.get("classD");
		    String strClassD = classD.toString();		    
		    if(clsToCheck.equals(strClassD))
		    {
		    	//is not disjoint
		    	return false;
		    }		    	    		    
		}
		
		return true;
	}
	
	private boolean CheckIsDisjointDomainWith(RelationDomainRangeList elem, ArrayList<String> listClsSourceInstance, InfModel infModel) {

		/* Return true if the elem have the domain classes disjoint of all classes in listClsSourceInstance at same time */
		
		boolean ok = true;
		for (DomainRange aux : elem.listDomainRange) {
						
			for (String clsToCheck : listClsSourceInstance) {
				if(this.CheckIsDijointClassOf(infModel, aux.Domain, clsToCheck))
				{
					ok = true;
				} else {
					ok = false; // have one disjoint case
				}
			}			
		}

		return ok;
	}

	private boolean CheckIsDisjointTargetWith(RelationDomainRangeList elem, ArrayList<String> listClsTargetInstance, InfModel infModel) {
		
		/* Return true if the elem have the target classes disjoint of all classes in listClsTargetInstance at same time */
		
		boolean ok = true;
		for (DomainRange aux : elem.listDomainRange) {
						
			for (String clsToCheck : listClsTargetInstance) {
				if(this.CheckIsDijointClassOf(infModel, aux.Range, clsToCheck))
				{
					ok = true;
				} else {
					ok = false; // have one disjoint case
					break;
				}
			}			
		}

		return ok;
	}
	
	public int CheckExistInstancesTargetCardinality(InfModel infModel, String instance, String relation, String imageClass, String cardinality) {
		
		int result = 0;
		ArrayList<String> listValues = new ArrayList<String>();

		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <"+ NS + ">" +
		"\n SELECT DISTINCT ?x" +
		" WHERE {\n" +
				" <" + instance + "> <" + relation + "> ?x .\n " +
				" ?x" + " rdf:type" + " <"+ imageClass + "> .\n " +
			"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row = results.next();
		    RDFNode rdfInstance = row.get("x");
		    listValues.add(rdfInstance.toString());
		}
		
		ArrayList<String> listValuesAux = new ArrayList<String>(listValues);

		for (String value : listValues) {
			
			if(listValuesAux.contains(value))
			{
				ArrayList<String> sameInstances = this.GetSameInstancesFrom(infModel, value);
				for (String sameIns : sameInstances) {
					if(listValuesAux.contains(sameIns))
					{
						listValuesAux.remove(sameIns);
					}
				}	
			}			
		}
		
		result = listValuesAux.size();
		
		// Check Data Property
		
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <"+ NS + ">" +
		"\n SELECT DISTINCT ?x" +
		" WHERE {\n" +
				" <" + instance + "> <" + relation + "> ?x .\n " +
				" <" + relation + "> rdf:type owl:DatatypeProperty .\n " +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe2 = QueryExecutionFactory.create(query, infModel);
		ResultSet results2 = qe2.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results2.hasNext()) {
			QuerySolution row = results2.next();
			RDFNode rdfInstance = row.get("x");
			if(rdfInstance.toString().contains(imageClass))
				result++;
		}
		
		return result;
	}

	public ArrayList<String> GetInstancesOfTargetWithRelation(InfModel infModel, String instance, String relation, String imageClass) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <"+ NS + ">" +
		"\n SELECT DISTINCT ?x" +
		" WHERE {\n" +
				" <" + instance + "> <" + relation + "> ?x .\n " +
				" ?x" + " rdf:type" + " <"+ imageClass + "> .\n " +
			"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row = results.next();
		    RDFNode rdfInstance = row.get("x");
		    list.add(rdfInstance.toString());	    
		}			

		return list;
	}
	
	public ArrayList<DataPropertyValue> GetDataValuesOfTargetWithRelation(InfModel infModel, String instance, String relation, String imageClass) {
		
		ArrayList<DataPropertyValue> list = new ArrayList<DataPropertyValue>();
		DataPropertyValue data = null;
		
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <"+ NS + ">" +
		"\n SELECT DISTINCT ?x" +
		" WHERE {\n" +
				" <" + instance + "> <" + relation + "> ?x .\n " +
			"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row = results.next();
			RDFNode rdfInstance = row.get("x");
			String value = rdfInstance.toString();			
			if(value.contains(imageClass))
			{
				data = new DataPropertyValue();
				data.value = value.split("\\^\\^")[0];
				data.classValue = imageClass;
				data.existInModel = true;
				list.add(data);
			}
		}			

		return list;
	}
	
	public ArrayList<DtoInstanceRelation> GetInstanceAllRelations(InfModel infModel, String individualUri){
		ArrayList<DtoInstanceRelation> listIndividualRelations = this.GetInstanceRelations(infModel, individualUri);
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +		
			"{ " + " ?domain " + " ?property " + "<" + individualUri + ">" + " .\n " +
				" ?property " + " rdf:type" + " owl:ObjectProperty .\n " +
			"} " +
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		DtoInstanceRelation dtoItem = null;
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    ResourceImpl property = (ResourceImpl) row.get("property");
		    String propertyUri = property.getURI();
		    
		    propertyUri = propertyUri.replace(property.getNameSpace(), "");
		    
		    if(propertyUri.startsWith("INV.")){
		    	propertyUri.replaceFirst("INV.", "");
		    }else{
		    	propertyUri = "INV." + propertyUri;
		    }
		    
		    propertyUri = property.getNameSpace() + propertyUri;
		    
		    ///////////////PAREI AQUI
		    RDFNode domain = row.get("domain"); 
		    
		    dtoItem = new DtoInstanceRelation();
		    //dtoItem.Property = property.toString();
		    dtoItem.Property = propertyUri;
		    
		    //since I change the relation name (including or removing the "INV." prefix), the domain result changes to target
		    dtoItem.Target = domain.toString();
		    
		    if(!listIndividualRelations.contains(dtoItem)){
		    	listIndividualRelations.add(dtoItem);
		    }					    		    		    
		}
		
		return listIndividualRelations;
	}
	
	public ArrayList<DtoInstanceRelation> GetInstanceRelations(InfModel infModel, String individualUri)
	{
		ArrayList<DtoInstanceRelation> listIndividualRelations = new ArrayList<DtoInstanceRelation>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +		
			"{ " + "<" + individualUri + ">" + " ?property" + " ?target .\n " +
				" ?property " + " rdf:type" + " owl:ObjectProperty .\n " +
			"} UNION { " +
				"<" + individualUri + ">" + " ?property" + " ?target .\n " +
				" ?property " + " rdf:type" + " owl:DatatypeProperty.\n " +		
			"}" +
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		DtoInstanceRelation dtoItem = null;
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode property = row.get("property");
		    RDFNode target = row.get("target"); 
		    
		    dtoItem = new DtoInstanceRelation();
		    dtoItem.Property = property.toString();
		    dtoItem.Target = target.toString();
		    
			listIndividualRelations.add(dtoItem);		    		    		    
		}
		
		return listIndividualRelations;
	}
	
	public ArrayList<String> GetDisjointClassesOf(String className, InfModel infModel) {
		
		ArrayList<String> listDisjointClasses = new ArrayList<String>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +
				"<" + className + "> " + "owl:disjointWith" + " ?classDisjoint .\n " +
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode completeClass = row.get("classDisjoint");
		    listDisjointClasses.add(completeClass.toString());		    		    
		}
		
		return listDisjointClasses;

	}

	public ArrayList<String> GetDisjointPropertiesOf(String propertyName, InfModel infModel) {
		
		ArrayList<String> listDisjointProps = new ArrayList<String>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +
				"<" + propertyName + "> " + "owl:propertyDisjointWith" + " ?propDisjoint .\n " +
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode prop = row.get("propDisjoint");
		    listDisjointProps.add(prop.toString());		    		    
		}
		
		return listDisjointProps;

	}
	
	public ArrayList<String> GetClassesFrom(String instanceName, InfModel infModel) {
		
		ArrayList<String> listClasses = new ArrayList<String>();
		
		//check if instance is a data value
		if(instanceName.contains("http://www.w3.org/"))
		{
			String type = instanceName.split("\\^\\^")[1];
			listClasses.add(type);
			return listClasses;
		}
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT *" +
		" WHERE {\n" +
				"<" + instanceName + "> " + "rdf:type" + " ?class .\n " +
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode cls = row.get("class");
		    if(!cls.toString().contains(w3String))
		    {
		    	listClasses.add(cls.toString());	
		    }		    	    		    
		}
		
		return listClasses;

	}

	public void QueryExample(OntModel model) {

		// create Pellet reasoner
		Reasoner r = PelletReasonerFactory.theInstance().create();		
		// create an inferencing model using the raw model
		InfModel infModel = ModelFactory.createInfModel(r, model);
		
		String queryString = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ns: <" + NS + ">" +
				" SELECT DISTINCT *" +
				" WHERE {\n" +		
						"?x ?r ?y ." +
				"}";

		System.out.println(queryString);
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		ResultSetFormatter.out(System.out, results, query);
	}

	
	/*
	 * Relations search
	 */
	
	public ArrayList<DtoDefinitionClass> GetSomeRelations(InfModel infModel) {
		
		ArrayList<DtoDefinitionClass> dtoSomeList = new ArrayList<DtoDefinitionClass>();
		
		// Create a new query -- EQUIVALENT CLASS
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y ?z" +
		" WHERE {\n" +			
			" { " +
				" ?x " + "owl:equivalentClass" + " ?blank .\n " +
				" ?blank rdf:type owl:Class .\n"  +
				" ?blank owl:intersectionOf  ?list  .\n" +
				" ?list  rdf:rest*/rdf:first  ?member . \n"  +			
				" ?member " + "owl:someValuesFrom" + " ?z .\n " +
				" ?member " + "owl:onProperty ?y .\n" +	
			"} UNION {\n" +		
				" ?x " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:someValuesFrom" + " ?z .\n " +
				" _:b0 " + "owl:onProperty ?y .\n" +
			" }\n" +	
				
			"UNION { " +
				" ?x " + "rdfs:subClassOf" + " ?blank .\n " +
				" ?blank rdf:type owl:Class .\n"  +
				" ?blank owl:intersectionOf  ?list  .\n" +
				" ?list  rdf:rest*/rdf:first  ?member . \n"  +			
				" ?member " + "owl:someValuesFrom" + " ?z .\n " +
				" ?member " + "owl:onProperty ?y .\n" +	
			"} UNION {\n" +		
				" ?x " + "rdfs:subClassOf" + " _:b1 .\n " +				
				" _:b1 " + "owl:someValuesFrom" + " ?z .\n " +
				" _:b1 " + "owl:onProperty ?y .\n" +
			" }\n" +			
		
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode Source = row.get("x");
		    RDFNode Relation = row.get("y");
		    RDFNode Target = row.get("z");
		    
		    // jump the blank node if he exist
		    //if(! Target.toString().contains("#")){
		    //	continue;
		    //}
		    
		    //jump the blank nodes - Check blank node and signal '-'
		    String TargetStr = Target.toString();
		    String SourceStr = Source.toString();
		    if ( Character.isDigit(TargetStr.charAt(0)) || TargetStr.startsWith("-") || Character.isDigit(SourceStr.charAt(0)) || SourceStr.startsWith("-")) 
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.SOME;
			itemList.Target = Target.toString();
			dtoSomeList.add(itemList);
		}
				
		// Create a new query -- SUB CLASS OF DE CLASSE DEFINIDA
		
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoSomeList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.SOME;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoSomeList.add(itemList);
					}
		    	}
		    }
		}

		return dtoSomeList;
	}
	
	public ArrayList<DtoDefinitionClass> GetMinRelations(InfModel infModel) {

		ArrayList<DtoDefinitionClass> dtoMinList = new ArrayList<DtoDefinitionClass>();		
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?source ?relation ?cardinality ?target" +
		" WHERE {\n" +
			" { " +
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list  ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +			
			"} UNION {" +		
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
			"} UNION {" +	
				" ?source " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b0 " + "owl:onProperty ?relation .\n" +
				" _:b0 " + "owl:onClass ?target" +	
			" } UNION { " +
				" ?source " + "owl:equivalentClass" + " _:b1 .\n " +				
				" _:b1 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b1 " + "owl:onProperty ?relation .\n" +
				" _:b1 " + "owl:onDataRange ?target" +
			"}" +
				
			" UNION { " +
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list  ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +			
			"} UNION {" +		
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
			"} UNION {" +	
				" ?source " + "rdfs:subClassOf" + " _:b2 .\n " +				
				" _:b2 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b2 " + "owl:onProperty ?relation .\n" +
				" _:b2 " + "owl:onClass ?target" +	
			" } UNION { " +
				" ?source " + "rdfs:subClassOf" + " _:b3 .\n " +				
				" _:b3 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b3 " + "owl:onProperty ?relation .\n" +
				" _:b3 " + "owl:onDataRange ?target" +
			"}" +				
			
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode Source = row.get("source");
		    RDFNode Relation = row.get("relation");
		    RDFNode Cardinality = row.get("cardinality");
		    RDFNode Target = row.get("target");
		    
		    //jump the blank nodes - Check blank node and signal '-'
		    String sourceStr = Source.toString();
		    if ( Character.isDigit(sourceStr.charAt(0)) || sourceStr.startsWith("-")) //
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.MIN;
			itemList.Target = Target.toString();
			itemList.Cardinality = Cardinality.toString().split("\\^")[0];
			dtoMinList.add(itemList);
		}
		
		// Create a new query -- SUB CLASS OF -- DEFINED CLASS
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
				//" _:b0 " + "owl:Class ?y .\n" +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoMinList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.MIN;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoMinList.add(itemList);
					}
		    	}
		    }
		}
		
		return dtoMinList;
	}

	public ArrayList<DtoDefinitionClass> GetMaxRelations(InfModel infModel) {

		ArrayList<DtoDefinitionClass> dtoMaxList = new ArrayList<DtoDefinitionClass>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?source ?relation ?cardinality ?target" +
		" WHERE {\n" +
			"{ " +
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +			
			"} UNION {" +		
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
			"} UNION {" +		
				" ?source " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b0 " + "owl:onProperty ?relation .\n" +
				" _:b0 " + "owl:onClass ?target" +	
			" } UNION { " +
				" ?source " + "owl:equivalentClass" + " _:b1 .\n " +				
				" _:b1 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b1 " + "owl:onProperty ?relation .\n" +
				" _:b1 " + "owl:onDataRange ?target" +
			"}" +
				
			" UNION { " +
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +			
			"} UNION {" +		
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
			"} UNION {" +		
				" ?source " + "rdfs:subClassOf" + " _:b2 .\n " +				
				" _:b2 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b2 " + "owl:onProperty ?relation .\n" +
				" _:b2 " + "owl:onClass ?target" +	
			" } UNION { " +
				" ?source " + "rdfs:subClassOf" + " _:b3 .\n " +				
				" _:b3 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b3 " + "owl:onProperty ?relation .\n" +
				" _:b3 " + "owl:onDataRange ?target" +
			"}" +			
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Source = row.get("source");
		    RDFNode Relation = row.get("relation");
		    RDFNode Cardinality = row.get("cardinality");
		    RDFNode Target = row.get("target");
		    
		    String sourceStr = Source.toString();
		    if ( Character.isDigit(sourceStr.charAt(0)) || sourceStr.startsWith("-")) //Check blank node and signal '-'
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.MAX;
			itemList.Target = Target.toString();
			itemList.Cardinality = Cardinality.toString().split("\\^")[0];
			dtoMaxList.add(itemList);
		}
		
		// Create a new query -- SUB CLASS OF -- DEFINED CLASS
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
				//" _:b0 " + "owl:Class ?y .\n" +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoMaxList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.MAX;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoMaxList.add(itemList);
					}
		    	}
		    }
		}
		
		return dtoMaxList;
	}
	
	public ArrayList<DtoDefinitionClass> GetExactlyRelations(InfModel infModel) {

		ArrayList<DtoDefinitionClass> dtoExactlyList = new ArrayList<DtoDefinitionClass>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?source ?relation ?cardinality ?target" +
		" WHERE {\n" +				
			" { " +
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +			
			"} UNION {" +		
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
			"} UNION {" +	
				" ?source " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b0 " + "owl:onProperty ?relation .\n" +
				" _:b0 " + "owl:onClass ?target" +	
			" } UNION { " +
				" ?source " + "owl:equivalentClass" + " _:b1 .\n " +				
				" _:b1 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b1 " + "owl:onProperty ?relation .\n" +
				" _:b1 " + "owl:onDataRange ?target" +
			"}" +
				
			" UNION { " +
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +			
			"} UNION {" +		
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
			"} UNION {" +	
				" ?source " + "rdfs:subClassOf" + " _:b2 .\n " +				
				" _:b2 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b2 " + "owl:onProperty ?relation .\n" +
				" _:b2 " + "owl:onClass ?target" +	
			" } UNION { " +
				" ?source " + "rdfs:subClassOf" + " _:b3 .\n " +				
				" _:b3 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b3 " + "owl:onProperty ?relation .\n" +
				" _:b3 " + "owl:onDataRange ?target" +
			"}" +
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Source = row.get("source");
		    RDFNode Relation = row.get("relation");
		    RDFNode Cardinality = row.get("cardinality");
		    RDFNode Target = row.get("target");
		    
		    //jump the blank nodes - Check blank node and signal '-'
		    String sourceStr = Source.toString();
		    if ( Character.isDigit(sourceStr.charAt(0)) || sourceStr.startsWith("-")) //
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.EXACTLY;
			itemList.Target = Target.toString();
			itemList.Cardinality = Cardinality.toString().split("\\^")[0];
			dtoExactlyList.add(itemList);
		}
		
		// Create a new query -- SUB CLASS OF -- DEFINED CLASS
		
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
				//" _:b0 " + "owl:Class ?y .\n" +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoExactlyList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.EXACTLY;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoExactlyList.add(itemList);
					}
		    	}
		    }
		}
		
		return dtoExactlyList;
	}

	public ArrayList<DtoDefinitionClass> GetSomeRelationsOfClass(InfModel infModel, String clsuri) {
		
		ArrayList<DtoDefinitionClass> dtoSomeList = new ArrayList<DtoDefinitionClass>();
		
		// Create a new query -- EQUIVALENT CLASS
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y ?z" +
		" WHERE {\n" +			
			" { " +
				" ?x " + "owl:equivalentClass" + " ?blank .\n " +
				" ?blank rdf:type owl:Class .\n"  +
				" ?blank owl:intersectionOf  ?list  .\n" +
				" ?list  rdf:rest*/rdf:first  ?member . \n"  +			
				" ?member " + "owl:someValuesFrom" + " ?z .\n " +
				" ?member " + "owl:onProperty ?y .\n" +	
				
				" FILTER( ?x = <" + clsuri + "> ) " +
			"} UNION {\n" +		
				" ?x " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:someValuesFrom" + " ?z .\n " +
				" _:b0 " + "owl:onProperty ?y .\n" +
				
				" FILTER( ?x = <" + clsuri + "> ) " +
			" }\n" +	
				
			"UNION { " +
				" ?x " + "rdfs:subClassOf" + " ?blank .\n " +
				" ?blank rdf:type owl:Class .\n"  +
				" ?blank owl:intersectionOf  ?list  .\n" +
				" ?list  rdf:rest*/rdf:first  ?member . \n"  +			
				" ?member " + "owl:someValuesFrom" + " ?z .\n " +
				" ?member " + "owl:onProperty ?y .\n" +	
				
				" FILTER( ?x = <" + clsuri + "> ) " +
			"} UNION {\n" +		
				" ?x " + "rdfs:subClassOf" + " _:b1 .\n " +				
				" _:b1 " + "owl:someValuesFrom" + " ?z .\n " +
				" _:b1 " + "owl:onProperty ?y .\n" +
				
				" FILTER( ?x = <" + clsuri + "> ) " +
			" }\n" +			
		
		"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode Source = row.get("x");
		    RDFNode Relation = row.get("y");
		    RDFNode Target = row.get("z");
		    
		    // jump the blank node if he exist
		    //if(! Target.toString().contains("#")){
		    //	continue;
		    //}
		    
		    //jump the blank nodes - Check blank node and signal '-'
		    String TargetStr = Target.toString();
		    String SourceStr = Source.toString();
		    if ( Character.isDigit(TargetStr.charAt(0)) || TargetStr.startsWith("-") || Character.isDigit(SourceStr.charAt(0)) || SourceStr.startsWith("-")) 
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.SOME;
			itemList.Target = Target.toString();
			dtoSomeList.add(itemList);
		}
				
		// Create a new query -- SUB CLASS OF DE CLASSE DEFINIDA
		
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoSomeList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.SOME;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoSomeList.add(itemList);
					}
		    	}
		    }
		}

		return dtoSomeList;
	}
	
	public ArrayList<DtoDefinitionClass> GetMinRelationsOfClass(InfModel infModel, String clsuri) {

		ArrayList<DtoDefinitionClass> dtoMinList = new ArrayList<DtoDefinitionClass>();		
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?source ?relation ?cardinality ?target" +
		" WHERE {\n" +
			" { " +
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list  ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +		
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +	
				" ?source " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b0 " + "owl:onProperty ?relation .\n" +
				" _:b0 " + "owl:onClass ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			" } UNION { " +
				" ?source " + "owl:equivalentClass" + " _:b1 .\n " +				
				" _:b1 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b1 " + "owl:onProperty ?relation .\n" +
				" _:b1 " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"}" +
				
			" UNION { " +
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list  ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +		
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +	
				" ?source " + "rdfs:subClassOf" + " _:b2 .\n " +				
				" _:b2 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b2 " + "owl:onProperty ?relation .\n" +
				" _:b2 " + "owl:onClass ?target" +	
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			" } UNION { " +
				" ?source " + "rdfs:subClassOf" + " _:b3 .\n " +				
				" _:b3 " + "owl:minQualifiedCardinality" + " ?cardinality .\n " +
				" _:b3 " + "owl:onProperty ?relation .\n" +
				" _:b3 " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"}" +				
			
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		
		while (results.hasNext()) {
			QuerySolution row= results.next();
		    RDFNode Source = row.get("source");
		    RDFNode Relation = row.get("relation");
		    RDFNode Cardinality = row.get("cardinality");
		    RDFNode Target = row.get("target");
		    
		    //jump the blank nodes - Check blank node and signal '-'
		    String sourceStr = Source.toString();
		    if ( Character.isDigit(sourceStr.charAt(0)) || sourceStr.startsWith("-")) //
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.MIN;
			itemList.Target = Target.toString();
			itemList.Cardinality = Cardinality.toString().split("\\^")[0];
			dtoMinList.add(itemList);
		}
		
		// Create a new query -- SUB CLASS OF -- DEFINED CLASS
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
				//" _:b0 " + "owl:Class ?y .\n" +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoMinList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.MIN;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoMinList.add(itemList);
					}
		    	}
		    }
		}
		
		return dtoMinList;
	}

	public ArrayList<DtoDefinitionClass> GetMaxRelationsOfClass(InfModel infModel, String clsuri) {

		ArrayList<DtoDefinitionClass> dtoMaxList = new ArrayList<DtoDefinitionClass>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?source ?relation ?cardinality ?target" +
		" WHERE {\n" +
			"{ " +
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +		
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				" ?source " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b0 " + "owl:onProperty ?relation .\n" +
				" _:b0 " + "owl:onClass ?target" +	
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			" } UNION { " +
				" ?source " + "owl:equivalentClass" + " _:b1 .\n " +				
				" _:b1 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b1 " + "owl:onProperty ?relation .\n" +
				" _:b1 " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"}" +
				
			" UNION { " +
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +	
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				" ?source " + "rdfs:subClassOf" + " _:b2 .\n " +				
				" _:b2 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b2 " + "owl:onProperty ?relation .\n" +
				" _:b2 " + "owl:onClass ?target" +	
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			" } UNION { " +
				" ?source " + "rdfs:subClassOf" + " _:b3 .\n " +				
				" _:b3 " + "owl:maxQualifiedCardinality" + " ?cardinality .\n " +
				" _:b3 " + "owl:onProperty ?relation .\n" +
				" _:b3 " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"}" +			
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Source = row.get("source");
		    RDFNode Relation = row.get("relation");
		    RDFNode Cardinality = row.get("cardinality");
		    RDFNode Target = row.get("target");
		    
		    String sourceStr = Source.toString();
		    if ( Character.isDigit(sourceStr.charAt(0)) || sourceStr.startsWith("-")) //Check blank node and signal '-'
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.MAX;
			itemList.Target = Target.toString();
			itemList.Cardinality = Cardinality.toString().split("\\^")[0];
			dtoMaxList.add(itemList);
		}
		
		// Create a new query -- SUB CLASS OF -- DEFINED CLASS
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
				//" _:b0 " + "owl:Class ?y .\n" +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoMaxList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.MAX;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoMaxList.add(itemList);
					}
		    	}
		    }
		}
		
		return dtoMaxList;
	}
	
 	public ArrayList<DtoDefinitionClass> GetExactlyRelationsOfClass(InfModel infModel, String clsuri) {

		ArrayList<DtoDefinitionClass> dtoExactlyList = new ArrayList<DtoDefinitionClass>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?source ?relation ?cardinality ?target" +
		" WHERE {\n" +				
			" { " +
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +		
				"?source " + "owl:equivalentClass" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +	
				" ?source " + "owl:equivalentClass" + " _:b0 .\n " +				
				" _:b0 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b0 " + "owl:onProperty ?relation .\n" +
				" _:b0 " + "owl:onClass ?target" +	
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			" } UNION { " +
				" ?source " + "owl:equivalentClass" + " _:b1 .\n " +				
				" _:b1 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b1 " + "owl:onProperty ?relation .\n" +
				" _:b1 " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"}" +
				
			" UNION { " +
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onClass ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +				
			"} UNION {" +		
				"?source " + "rdfs:subClassOf" + " ?blank .\n " +
				"?blank rdf:type owl:Class ."  +
				"?blank owl:intersectionOf  ?list     ." +
				"?list  rdf:rest*/rdf:first  ?member ."  +			
				" ?member " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" ?member " + "owl:onProperty ?relation .\n" +
				" ?member " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"} UNION {" +	
				" ?source " + "rdfs:subClassOf" + " _:b2 .\n " +				
				" _:b2 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b2 " + "owl:onProperty ?relation .\n" +
				" _:b2 " + "owl:onClass ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			" } UNION { " +
				" ?source " + "rdfs:subClassOf" + " _:b3 .\n " +				
				" _:b3 " + "owl:qualifiedCardinality" + " ?cardinality .\n " +
				" _:b3 " + "owl:onProperty ?relation .\n" +
				" _:b3 " + "owl:onDataRange ?target" +
				
				" FILTER( ?source = <" + clsuri + "> ) " +
			"}" +
		"}";

		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		DtoDefinitionClass itemList = null;
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Source = row.get("source");
		    RDFNode Relation = row.get("relation");
		    RDFNode Cardinality = row.get("cardinality");
		    RDFNode Target = row.get("target");
		    
		    //jump the blank nodes - Check blank node and signal '-'
		    String sourceStr = Source.toString();
		    if ( Character.isDigit(sourceStr.charAt(0)) || sourceStr.startsWith("-")) //
		    {
		        continue;
		    }
		    
			itemList = new DtoDefinitionClass();
			itemList.Source = Source.toString();
			itemList.Relation = Relation.toString();
			itemList.PropertyType = this.GetPropertyType(infModel, Relation.toString());
			itemList.TypeCompletness = EnumRelationTypeCompletness.EXACTLY;
			itemList.Target = Target.toString();
			itemList.Cardinality = Cardinality.toString().split("\\^")[0];
			dtoExactlyList.add(itemList);
		}
		
		// Create a new query -- SUB CLASS OF -- DEFINED CLASS
		
		queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?x ?y" +
		" WHERE {\n" +
				" ?x " + "rdfs:subClassOf" + " ?y .\n " +
				//" _:b0 " + "owl:Class ?y .\n" +
			"}";

		query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, infModel);
		results = qe.execSelect();
		
		// Output query results 
		//ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {
			
			QuerySolution row= results.next();
		    RDFNode Class = row.get("x");
		    RDFNode SuperClass = row.get("y");
		    
		    if(!Class.toString().contains(w3String) && !SuperClass.toString().contains(w3String) && Class.toString() != SuperClass.toString())
		    {		    	
		    	ArrayList<DtoDefinitionClass> dtoListWithSource = DtoDefinitionClass.getDtosWithSource(dtoExactlyList, SuperClass.toString());
		    	if(dtoListWithSource != null)
		    	{
		    		for (DtoDefinitionClass dto : dtoListWithSource) {
			    		itemList = new DtoDefinitionClass();
						itemList.Source = Class.toString();
						itemList.Relation = dto.Relation;
						itemList.PropertyType = this.GetPropertyType(infModel, dto.Relation);
						itemList.TypeCompletness = EnumRelationTypeCompletness.EXACTLY;
						itemList.Target = dto.Target;
						itemList.Cardinality = dto.Cardinality;
						dtoExactlyList.add(itemList);
					}
		    	}
		    }
		}
		
		return dtoExactlyList;
	}

 	public ArrayList<DtoDefinitionClass> GetModelDefinitionsInInstances(ArrayList<Instance> listAllInstances,	InfModel InfModel) {

		ArrayList<DtoDefinitionClass> resultListDefinitions = new ArrayList<DtoDefinitionClass>();
		
		for (Instance instance : listAllInstances) 
		{
			for (String cls : instance.ListClasses) 
			{
				//DtoDefinitionClass aux = DtoDefinitionClass.getDtoWithSourceAndRelationAndTarget(resultListDefinitions, cls);
				
				DtoDefinitionClass aux = null;
				if(aux == null && ! cls.contains("Thing"))	//doesn't exist yet
				{
					ArrayList<DtoDefinitionClass> dtoSomeRelationsList = this.GetSomeRelationsOfClass(InfModel,cls);
					ArrayList<DtoDefinitionClass> dtoMinRelationsList = this.GetMinRelationsOfClass(InfModel,cls);
					ArrayList<DtoDefinitionClass> dtoMaxRelationsList = this.GetMaxRelationsOfClass(InfModel,cls);
					ArrayList<DtoDefinitionClass> dtoExactlyRelationsList = this.GetExactlyRelationsOfClass(InfModel,cls);	
					
					resultListDefinitions.addAll(dtoSomeRelationsList);
					resultListDefinitions.addAll(dtoMinRelationsList);
					resultListDefinitions.addAll(dtoMaxRelationsList);
					resultListDefinitions.addAll(dtoExactlyRelationsList);
				}			
				
			}		
			
		}		
		
		return resultListDefinitions;
	}
	
 	public ArrayList<DtoDefinitionClass> GetModelDefinitionsInInstances(String instanceURI, OntModel model, InfModel InfModel, ArrayList<Instance> listAllInstances, ManagerInstances manager) {

		Instance Instance = manager.getInstance(listAllInstances, instanceURI); // GET INTANCE on MODEL
		ArrayList<DtoInstance> listInstancesDto = this.GetAllInstancesWithClass(model, InfModel);
		for (DtoInstance dto : listInstancesDto) {
			
			if(dto.Uri.equals(instanceURI))
			{				
				String nameSpace = dto.Uri.split("#")[0] + "#";
				String name = dto.Uri.split("#")[1];
				
				if (Instance == null)
				{					
					Instance = new Instance(nameSpace, name, dto.ClassNameList, this.GetDifferentInstancesFrom(InfModel, dto.Uri), this.GetSameInstancesFrom(InfModel, dto.Uri),true);
					
				} else {
					
					//Update classes
					Instance.ListClasses = dto.ClassNameList;
				}
			}
		}
	
		ArrayList<DtoDefinitionClass> resultListDefinitions = new ArrayList<DtoDefinitionClass>();

		for (String cls : Instance.ListClasses) 
		{
			DtoDefinitionClass aux = DtoDefinitionClass.getDtoWithSourceAndRelationAndTarget(resultListDefinitions, cls);
			if(aux == null && ! cls.contains("Thing"))	//don't exist yet
			{
				ArrayList<DtoDefinitionClass> dtoSomeRelationsList = this.GetSomeRelationsOfClass(InfModel,cls);
				ArrayList<DtoDefinitionClass> dtoMinRelationsList = this.GetMinRelationsOfClass(InfModel,cls);
				ArrayList<DtoDefinitionClass> dtoMaxRelationsList = this.GetMaxRelationsOfClass(InfModel,cls);
				ArrayList<DtoDefinitionClass> dtoExactlyRelationsList = this.GetExactlyRelationsOfClass(InfModel,cls);	
				
				resultListDefinitions.addAll(dtoSomeRelationsList);
				resultListDefinitions.addAll(dtoMinRelationsList);
				resultListDefinitions.addAll(dtoMaxRelationsList);
				resultListDefinitions.addAll(dtoExactlyRelationsList);
			}			
			
		}	
		
		//Add to list of intances
		//listAllInstances.add(Instance);
		
		//return
		return resultListDefinitions;
	}
 	
	/*
	 * Specializations search
	 */
	
	public ArrayList<DtoCompleteClass> GetCompleteClasses(String cls, InfModel infModel)
	{
		ArrayList<DtoCompleteClass> listClasses = new ArrayList<DtoCompleteClass>();
		
		// Create a new query
		String queryString = 
		"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX ns: <" + NS + ">" +
		" SELECT DISTINCT ?completeClass ?member " +
		" WHERE {\n" +
				"?completeClass " + "owl:equivalentClass" + " ?x .\n " +
				"?x rdf:type owl:Class ."  +
				"?x owl:unionOf  ?list     ." +
        		"?list  rdf:rest*/rdf:first  ?member ."  +
		"}";

		/* The result is order by completClass */
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		DtoCompleteClass itemList = null;
		
		while (results.hasNext()) {

			QuerySolution row= results.next();
		    RDFNode completeClass = row.get("completeClass");
		    RDFNode member = row.get("member");
		    
		    itemList = DtoCompleteClass.GetDtoCompleteClass(listClasses, completeClass.toString());
		    if(itemList == null)
		    {
		    	//New Class
		    	itemList = new DtoCompleteClass();
		    	itemList.CompleteClass = completeClass.toString();
		    	itemList.AddMember(member.toString());	
		    	listClasses.add(itemList);
		    } else {
		    	itemList.AddMember(member.toString());
		    }		    
		}
		
		return listClasses;
	}

	public ArrayList<DtoCompleteClass> GetCompleteSubClasses(String className, ArrayList<String> listClassesOfInstance, InfModel infModel)
	{
		ArrayList<DtoCompleteClass> ListCompleteClsAndSubCls = new ArrayList<DtoCompleteClass>();
		
		// Create a new query
		String queryString = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ns: <" + NS + ">" +
				" SELECT DISTINCT ?x0 ?completeClass ?member" +
				" WHERE {\n" +
					"{ " +
				
						"?completeClass owl:equivalentClass ?cls ." +
						"?cls owl:intersectionOf ?nodeFather." +
		
						//one level
						"?nodeFather ?r1 ?x0 ."+
						"?x0 owl:unionOf  ?list ."+

						"?list  rdf:rest*/rdf:first  ?member ." +
						" FILTER( ?completeClass = <" + className + "> ) " +
						
					"} UNION {" +
					
						"?completeClass owl:equivalentClass ?cls ." +
						"?cls owl:intersectionOf ?nodeFather." +
		
						//two levels
						"?nodeFather ?r2 ?x1 ." +
						"?x1 ?r1 ?x0 ." +
						"?x0 owl:unionOf  ?list ." +
		
						"?list  rdf:rest*/rdf:first  ?member ." +
						" FILTER( ?completeClass = <" + className + "> ) " +
						
					"} UNION {" +
										
						"?completeClass owl:equivalentClass ?x0 ." +
						
						//zero levels
						"?x0 owl:unionOf  ?list ." +
						
						"?list  rdf:rest*/rdf:first  ?member ." +
						" FILTER( ?completeClass = <" + className + "> ) " +			
						
					"}" + 
				"}";

		/* The result is order by completClass */
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		RDFNode blankNodeAux = null;	//Save last blank node
		DtoCompleteClass dto = null;
		while (results.hasNext()) {

			QuerySolution row= results.next();
			
			RDFNode blankNode = row.get("x0");
			RDFNode completeClass = row.get("completeClass");
		    RDFNode member = row.get("member");
		    
		    if(blankNodeAux == null)
		    {
		    	//first case blank node
		    	blankNodeAux = blankNode;    
		    	
		    	//Add here
		    	dto = new DtoCompleteClass();
		    	dto.CompleteClass = completeClass.toString();

		    	//check if member are disjoint of listClassesOfInstance
		    	
    			boolean ok = true;
    			ArrayList<String> listDisjointClassesOfMember = this.GetDisjointClassesOf(member.toString(), infModel);
    			for (String disjointCls : listDisjointClassesOfMember) {
    				if(listClassesOfInstance.contains(disjointCls))
    				{
    					//not possible specialize
    					ok = false;
    					break;
    				}
				}
    			
    			//check if member are in listClassesOfInstace
    			if(listClassesOfInstance.contains(member.toString()))
    			{
    				// not necessary specialize
    				ok = false;
    			}
    			
    			if(ok == true)
    			{
    				dto.AddMember(member.toString());	
    			}	
		    	
		    } else {
		    	
		    	if(blankNode.equals(blankNodeAux))
		    	{
		    		//we are in the same blank node, same generalization set
		    		
		    		//add with not exist
		    		if(! dto.Members.contains(member.toString()))
		    		{
		    			//check if member are disjoint of listClassesOfInstance
		    			boolean ok = true;
		    			ArrayList<String> listDisjointClassesOfMember = this.GetDisjointClassesOf(member.toString(), infModel);
		    			for (String disjointCls : listDisjointClassesOfMember) {
		    				if(listClassesOfInstance.contains(disjointCls))
		    				{
		    					//not possible specialize
		    					ok = false;
		    					break;
		    				}
						}
		    			
		    			//check if member are in listClassesOfInstace
		    			if(listClassesOfInstance.contains(member.toString()))
		    			{
		    				// not necessary specialize
		    				ok = false;
		    			}
		    			
		    			if(ok == true)
		    			{
		    				dto.AddMember(member.toString());	//ADD MEMBER
		    			}	
		    		}
		    		
		    	} else {
		    		
		    		//change generalization
		    		
		    		if(dto.Members.size() > 0)
		    			ListCompleteClsAndSubCls.add(dto);
		    	
		    		//new node
		    		//get only the not disjoint possibilities
		    		
		    		dto = new DtoCompleteClass();
		    		dto.CompleteClass = completeClass.toString();
		    		
		    		//check if member are disjoint of listClassesOfInstance
	    			boolean ok = true;
	    			ArrayList<String> listDisjointClassesOfMember = this.GetDisjointClassesOf(member.toString(), infModel);
	    			for (String disjointCls : listDisjointClassesOfMember) {
	    				if(listClassesOfInstance.contains(disjointCls))
	    				{
	    					//not possible specialize
	    					ok = false;
	    					break;
	    				}
					}
	    			
	    			//check if member are in listClassesOfInstace
	    			if(listClassesOfInstance.contains(member.toString()))
	    			{
	    				// not necessary specialize
	    				ok = false;
	    			}
	    			
	    			if(ok == true)
	    			{
	    				dto.AddMember(member.toString());	
	    			}
			    	
			    	blankNodeAux = blankNode;
		    	} 	
		    }
		}
		
		//the last case
		if(! ListCompleteClsAndSubCls.contains(dto) && dto != null)
		{
			if(dto.Members.size() > 0)
				ListCompleteClsAndSubCls.add(dto);
		}
		
		return ListCompleteClsAndSubCls;
	}
	
	public ArrayList<String> GetSubProperties(String property, InfModel infModel) {
		
		ArrayList<String> listSubProperties = new ArrayList<String>();
		
		// Create a new query
		String queryString = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ns: <" + NS + ">" +
				" SELECT DISTINCT *" +
				" WHERE {\n" +
					"?subProp rdfs:subPropertyOf" + "<" + property + "> ." +
				"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();
		
		// Output query results 
		// ResultSetFormatter.out(System.out, results, query);
		
		while (results.hasNext()) {

			QuerySolution row= results.next();
		    RDFNode subProp = row.get("subProp");
		    if(subProp.toString().contains(NS) && (!subProp.toString().equals(property)))
		    {
		    	listSubProperties.add(subProp.toString());
		    }
		}
		
		return listSubProperties;
	}

	public ArrayList<DomainRange> GetDomainRangeFromProperty(String property, InfModel infModel)
	{
		//List auxiliary for take the sub-properties and his relations
		ArrayList<DomainRange> list = new ArrayList<DomainRange>();
				
		// Create a new query
		String queryString = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ns: <" + NS + ">" +
				" SELECT DISTINCT *" +
				" WHERE {\n" +
					"<" + property + "> rdfs:domain ?domainSubProp ." +
					"<" + property + "> rdfs:range ?rangeSubProp ." +					
				"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();		
		
		while (results.hasNext()) {

			QuerySolution row= results.next();
			
		    RDFNode domainNode = row.get("domainSubProp");
		    RDFNode rangeNode = row.get("rangeSubProp");
		    
		    String domainClsSubProp = domainNode.toString();
		    String rangeClsSubProp = rangeNode.toString();
		    
	    	DomainRange dr = new DomainRange(domainClsSubProp, rangeClsSubProp);
	    	list.add(dr);		    
		}
		
		return list;
	}
	
	public ArrayList<RelationDomainRangeList> GetSubPropertiesWithDomaninAndRange(String property, InfModel infModel)
	{
		//List auxiliary for take the sub-properties and his relations
		ArrayList<RelationDomainRangeList> listMediation = new ArrayList<RelationDomainRangeList>();
				
		// Create a new query
		String queryString = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ns: <" + NS + ">" +
				" SELECT DISTINCT *" +
				" WHERE {\n" +
					"?subProp rdfs:subPropertyOf" + "<" + property + "> ." +
					"?subProp rdfs:domain ?domainSubProp ." +
					"?subProp rdfs:range ?rangeSubProp ." +					
				"}";
		
		Query query = QueryFactory.create(queryString); 
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, infModel);
		ResultSet results = qe.execSelect();		
		
		while (results.hasNext()) {

			QuerySolution row= results.next();
			
		    RDFNode subPropNode = row.get("subProp");
		    RDFNode domainNode = row.get("domainSubProp");
		    RDFNode rangeNode = row.get("rangeSubProp");
		    
		    String subProp = subPropNode.toString();
		    String domainClsSubProp = domainNode.toString();
		    String rangeClsSubProp = rangeNode.toString();
		    
		    if(!subProp.contains(w3String) && (!subProp.equals(property)))
		    {    	
		    		RelationDomainRangeList elem = RelationDomainRangeList.getElement(listMediation, subProp);
		    		if (elem == null)
		    		{
		    			elem = new RelationDomainRangeList();
		    			elem.Relation = subProp;
		    			elem.listDomainRange.add(new DomainRange(domainClsSubProp, rangeClsSubProp));
		    			listMediation.add(elem);
		    		} else {
		    			elem.listDomainRange.add(new DomainRange(domainClsSubProp, rangeClsSubProp));
		    		}
		    }
		    
		}
		
		return listMediation;
	}
	
	public ArrayList<String> GetSubPropertiesWithDomaninAndRange(String instanceSource,	String property, String instanceTarget, ArrayList<DtoInstanceRelation> instanceListRelations, InfModel infModel) {
		
		/* Get properties with domains from instanceSource class and Range from instanceTarget class */
		ArrayList<String> listSubProperties = new ArrayList<String>();
		
		//List auxiliary for take the sub-properties and his relations
		ArrayList<RelationDomainRangeList> listMediation = new ArrayList<RelationDomainRangeList>();
		
		//Get classes from instances
		ArrayList<String> listClsSourceInstance = this.GetClassesFrom(instanceSource, infModel);
		ArrayList<String> listClsTargetInstance = this.GetClassesFrom(instanceTarget, infModel);		
		
		//Get sub-properties from property and yours domain and range
		listMediation = this.GetSubPropertiesWithDomaninAndRange(property, infModel);
		
		//Select relations
		for (RelationDomainRangeList elem : listMediation) 
	    {
			boolean disjointDomain = this.CheckIsDisjointDomainWith(elem, listClsSourceInstance, infModel);
			boolean disjointTarget = this.CheckIsDisjointTargetWith(elem, listClsTargetInstance, infModel);			
			
			if(disjointDomain == true && disjointTarget == true)
	    	{
				boolean flag = true;
				
				//Check the sub-property already exist in all relations
				for (DtoInstanceRelation dto : instanceListRelations) {
					if(dto.Property.equals(elem.Relation))
					{
						//if is, doesn't add in list
						flag = false;
						break;
					}
				}				
				
				if (flag == true)
				{
					//if target and domain are disjoint
					listSubProperties.add(elem.Relation);
				}
	    	}
	    }
		
		
		
		return listSubProperties;
	}


	
}
