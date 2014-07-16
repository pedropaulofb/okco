package br.ufes.inf.nemo.okco.visualizer;

import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;

public abstract class GraphPlotting {
	
	protected String VERDE = "#00FF00";
	protected String AZUL = "#0000FF";
	protected String ROXO = "#FF00FF";
	protected String VERMELHO = "#FF0000";

	public int width = 800;
	public int height = 600;
	
	//hash<individual,{classes}>
	public HashMap<String,ArrayList<String>> hash = null;
	
	public String getArborStructureComingOutOf(InfModel ontology, String centerIndividual){
		String query = QueryManager.getAllRelationsComingOutOf(centerIndividual);
		ResultSet resultSet = QueryManager.runQuery(ontology, query);

		ArborParser arborParser = new ArborParser(ontology,this);
		String arborStructure = arborParser.getArborJsStringFor(resultSet, centerIndividual);

		String arborHashStructure = arborParser.getArborHashStructure();

		return callBack(arborStructure, arborHashStructure);
	}

	public String getArborStructureComingInOf(InfModel ontology, String centerIndividual){
		String query = QueryManager.getAllRelationsComingInOf(centerIndividual);
		ResultSet resultSet = QueryManager.runQuery(ontology, query);

		ArborParser arborParser = new ArborParser(ontology,this);
		String arborStructure = arborParser.getArborJsStringFor(resultSet, centerIndividual);

		String arborHashStructure = arborParser.getArborHashStructure();

		return callBack(arborStructure, arborHashStructure);
	}

	public String getArborStructureFor(InfModel ontology){
		
		String query = QueryManager.getPropertiesBetweenAllIndividuals();
		ResultSet resultSet = QueryManager.runQuery(ontology, query);

		ArborParser arborParser = new ArborParser(ontology,this);
		String arborStructure = arborParser.getArborJsString(resultSet,true);

		String arborHashStructure = arborParser.getArborHashStructure();

		return callBack(arborStructure, arborHashStructure);
	}

	protected String callBack(String arborStructure, String arborHashStructure){
		return "	"
				+ "function addNodes(graph){"
				+ arborStructure
				+ "}"
				+ ""
				+ "function getHash(){"
				+ "		var hash = {};"
				+ arborHashStructure
				+ "		return hash;"
				+ "}";
	}
	
	public abstract String getArborNode(String elem, boolean isCenterNode);
	public abstract boolean isClass(String elem);
	public abstract String getSubtitle();
}
