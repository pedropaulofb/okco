package br.ufes.inf.nemo.okco.visualizer;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;

public class QueryManager {

	/**
	 * Run query and set the ResultSet
	 * */
	public static ResultSet runQuery(InfModel ontology,String queryString){
		QueryExecution qe = null;
		try{
			Query query = QueryFactory.create(queryString);
			// Execute the query and obtain results
			qe = QueryExecutionFactory.create(query, ontology);
			ResultSet results =  qe.execSelect();
			return results;
		}catch(Exception e ){
			e.printStackTrace();
		}
		return null;
	}

	public static String getAllRelationsComingOutOf(String centerNode) {
		String query = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" SELECT DISTINCT *" +
						" WHERE {" +		
						"{ " + "<" + centerNode + ">" + " ?property" + " ?target . " +
						" ?property " + " rdf:type" + " owl:ObjectProperty . " +
						"} UNION { " +
						"<" + centerNode + ">" + " ?property" + " ?target . " +
						" ?property  rdf:type owl:DatatypeProperty. " +		
						"} UNION { " + 
						"<"+ centerNode +"> ?property ?target. " +
						" ?target  rdf:type owl:Class. " +		
						"}" +
						"}";
		return query;	
	}

	public static String getAllRelationsComingInOf(String centerNode) {
		String query = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" SELECT DISTINCT *" +
						" WHERE {" +		
						"{ ?source ?property " + " <" + centerNode + "> . " +
						" ?property " + " rdf:type" + " owl:ObjectProperty . " +
						"} UNION { " +
						" ?source ?property" + " <" + centerNode + "> . " +
						" ?property  rdf:type owl:DatatypeProperty. " +		
						"} UNION { " + 
						" ?source ?property <"+ centerNode +"> . " +
						" ?source rdf:type owl:Class . " +		
						"}" +
						"}";
		return query;
	}

	public static String getPropertiesBetweenAllIndividuals(){
		String query = 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						" SELECT DISTINCT * " +
						" WHERE {" +		
						"{ "
						+ "?source ?property ?target . " +
						" ?property rdf:type owl:ObjectProperty . " +
						"} "
						+ "UNION { " +
						" ?source ?property ?target . " +
						" ?property  rdf:type owl:DatatypeProperty. " +		
						"} "
						+ "}";
		return query;
	}

	public static String getAllIndividuous() {
		String query = 
				" PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  owl:  <http://www.w3.org/2002/07/owl#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
						+ " SELECT  DISTINCT * "
						+ " WHERE "
						+ " {"
						+ " ?indv rdf:type owl:NamedIndividual ."
						+ " ?indv rdf:type ?owlclas "
						+ " }";
		return query;
	}

	public static String getRelationsBetweenClass(String cls) {
		String query = ""
				+" PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  owl:  <http://www.w3.org/2002/07/owl#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+" SELECT  DISTINCT  *  "
				+" WHERE" 
				+" {"
				+" ?source rdf:type <"+cls+">  ."
				+" ?target rdf:type <"+cls+"> . "
				+" ?property rdf:type owl:ObjectProperty ."
				+" ?source ?property ?target . "
				+" }";
		return query;
	}

}
