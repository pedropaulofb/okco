package br.ufes.inf.nemo.okco.visualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;

public class ArborParser {
	private GraphPlotting graphPlotting;

	public ArborParser(InfModel ontology, GraphPlotting graphPlotting) {
		this.graphPlotting = graphPlotting;

		if(graphPlotting.hash != null)
			return;

		graphPlotting.hash = new HashMap<String, ArrayList<String>>();
		String query = QueryManager.getAllIndividuous();
		ResultSet results = QueryManager.runQuery(ontology, query);

		ArrayList<String> owlclasses;
		while (results.hasNext()) {
			QuerySolution row = results.next();
			String indiv = row.get("indv").toString();
			String owlClass = row.get("owlclas").toString();

			if(graphPlotting.hash.containsKey(indiv)){
				owlclasses = graphPlotting.hash.get(indiv);
			}else{
				owlclasses = new ArrayList<String>();
			}			

			if(indiv.contains("^^")){
				owlclasses.add("##DATATYPE##");
				owlclasses.add(indiv);
				graphPlotting.hash.put(indiv, owlclasses);	
			}else{
				owlclasses.add(owlClass);
				graphPlotting.hash.put(indiv, owlclasses);	
			}
		}
	}

	public ArborParser(InfModel ontology, GraphPlotting graphPlotting, HashMap<String, ArrayList<String>> hashClasses) {
		this.graphPlotting = graphPlotting;

		if(graphPlotting.hash != null)
			return;

		graphPlotting.hash = hashClasses;
	}

	public String getArborJsString(ResultSet results, boolean showAll) {
		
		ArrayList<Tupla> tuplas = this.getTuplas(results);

		//set screen size
		graphPlotting.width  += 400 * (tuplas.size() / 10);
		graphPlotting.height += 300  * (tuplas.size() / 10);

		String arborStrucure = "";

		HashMap<String,String> hashTuplas = new HashMap<String, String>();

		final String HH = "#!!#";

		for (Tupla tupla : tuplas) {
			if(hashTuplas.containsKey(tupla.source+HH+tupla.target)){
				String property = hashTuplas.get(tupla.source+HH+tupla.target);
				hashTuplas.remove(tupla.source+HH+tupla.target);
				hashTuplas.put(tupla.source+HH+tupla.target, property+", "+tupla.property);
			}else if(hashTuplas.containsKey(tupla.target+HH+tupla.source)){
				String property  = hashTuplas.get(tupla.target+HH+tupla.source);
				hashTuplas.remove(tupla.target+HH+tupla.source);
				hashTuplas.put(tupla.target+HH+tupla.source, property+", "+tupla.property);
			}else{
				//first occurrence
				hashTuplas.put(tupla.source+HH+tupla.target, tupla.property);
			}
		}

		for(Map.Entry<String, String> entry : hashTuplas.entrySet()){
			String source = entry.getKey().split(HH)[0];
			String target = entry.getKey().split(HH)[1];
			arborStrucure += getArborEdge(entry.getValue(), graphPlotting.getArborNode(source, false), graphPlotting.getArborNode(target, false), entry.getValue().contains(","));
		}

		if(showAll){
			for(Map.Entry<String, ArrayList<String>> entry : graphPlotting.hash.entrySet()){
				arborStrucure += graphPlotting.getArborNode(entry.getKey(), false)+";";
			}
		}

		return arborStrucure;
	}

	public String getArborJsStringFor(ResultSet results, String centerNode){
		ArrayList<Tupla> tuplas = getTuplas(results, centerNode);
		String arborStrucure = "";

		//set screen size
		graphPlotting.width  += 400 * (tuplas.size() / 9);
		graphPlotting.height += 300  * (tuplas.size() / 9);

		if(tuplas.isEmpty()){
			//Show just the center node
			arborStrucure = graphPlotting.getArborNode(centerNode, true);
			return arborStrucure;
		}

		final String HH = "#!!#";
		HashMap<String,String> usedTuplas = new HashMap<String, String>();

		for (Tupla tupla : tuplas) {
			if(usedTuplas.containsKey(tupla.source+HH+tupla.target)){
				String property = usedTuplas.get(tupla.source+HH+tupla.target);
				usedTuplas.remove(tupla.source+HH+tupla.target);
				usedTuplas.put(tupla.source+HH+tupla.target, property+", "+tupla.property);
			}else if(usedTuplas.containsKey(tupla.target+HH+tupla.source)){
				String property  = usedTuplas.get(tupla.target+HH+tupla.source);
				usedTuplas.remove(tupla.target+HH+tupla.source);
				usedTuplas.put(tupla.target+HH+tupla.source, property+", "+tupla.property);
			}else{
				usedTuplas.put(tupla.source+HH+tupla.target, tupla.property);
			}

			if(!graphPlotting.hash.containsKey(tupla.target)){
				ArrayList<String> newClass = new ArrayList<String>();
				if(tupla.target.contains("^^")){
					newClass.add("##DATATYPE##");
					newClass.add(tupla.target);
				}else{
					newClass.add("##CLASS##");
				}
				graphPlotting.hash.put(tupla.target,newClass);
			}
		}

		for(Map.Entry<String, String> entry : usedTuplas.entrySet()){
			String source = entry.getKey().split(HH)[0];
			String target = entry.getKey().split(HH)[1];
			arborStrucure += getArborEdge(entry.getValue(), graphPlotting.getArborNode(source, source.equals(centerNode)), graphPlotting.getArborNode(target, target.equals(centerNode)), entry.getValue().contains(","));
		}

		return arborStrucure;
	}

	public static String getArborEdge(String edgeName, String sourceNode, String targetNode, boolean isInverse){
		if(isInverse)
			return "graph.addEdge("+sourceNode+","+targetNode+", {name:'"+edgeName+"', inverse:'true'});\n";
		else
			return "graph.addEdge("+sourceNode+","+targetNode+", {name:'"+edgeName+"'});\n";
	}

	private ArrayList<Tupla> getTuplas(ResultSet results, String centerNode){
		ArrayList<Tupla> tuplas = new ArrayList<Tupla>();

		while (results.hasNext()) {
			QuerySolution row= results.next();
			String source, target;
			boolean isSourceCenterNode = false;
			boolean isTargetCenterNode = false;

			if(row.get("source") == null){
				source = centerNode;
			}else{
				source = row.get("source").toString();
			}
			if(source.equals(centerNode))
				isSourceCenterNode = true;

			String property = row.get("property").toString();
			property = property.substring(property.indexOf("#")+1);;

			if(row.get("target") == null){
				target = centerNode;
				isTargetCenterNode = true;
			}else{
				target = row.get("target").toString();
			}
			if(target.equals(centerNode))
				isTargetCenterNode = true;

			//Exclude strange resource
			if(!source.contains("#") || !target.contains("#"))
				continue;

			Tupla tupla = new Tupla(source, property, target, isSourceCenterNode, isTargetCenterNode);
			tuplas.add(tupla);
		}
		return tuplas;
	}

	private ArrayList<Tupla> getTuplas(ResultSet results){
		ArrayList<Tupla> tuplas = new ArrayList<Tupla>();

		while (results.hasNext()) {
			QuerySolution row = results.next();

			String source;
			source = row.get("source").toString();

			String property;
			if(row.get("property") == null){
				property = "type";
			}else{
				property = row.get("property").toString();
				property = property.substring(property.indexOf("#")+1);;	
			}

			String target;
			target = row.get("target").toString();

			Tupla tupla = new Tupla(source, property, target, false, false);
			tuplas.add(tupla);
		}
		return tuplas;
	}

	public String getArborHashStructure() {
		String row = "";
		for(Map.Entry<String, ArrayList<String>> entry : graphPlotting.hash.entrySet()){
			row += getHashLine(entry.getKey(), entry.getValue());
		}
		return row;
	}

	private String getHashLine(String indv, ArrayList<String> owlClasses){
		String ret;

		if(owlClasses.get(0).equals("##DATATYPE##")){
			ret = "\nhash[\"";
			ret += owlClasses.get(1).substring(0, owlClasses.get(1).indexOf("^^"));
			ret += "\"] = \"";
			ret += "<b>"+owlClasses.get(1).substring(0, owlClasses.get(1).indexOf("^^"))+" is a Datatype.</b><br>"
					+ "Range: "+owlClasses.get(1).substring(owlClasses.get(1).indexOf("#")+1)+"\";";
			return ret;
		}

		ret = "\nhash[\"";
		ret += indv.substring(indv.indexOf("#")+1);
		ret += "\"] = \"";

		if(owlClasses.get(0).equals("##CLASS##")){
			ret += "<b>"+indv.substring(indv.indexOf("#")+1)+" is a Class.</b>\";";
			return ret;
		}

		ret += "<b>"+indv.substring(indv.indexOf("#")+1)+" is an individual of classes: </b><br><ul>";

		for (String owlClass : owlClasses) {
			if(owlClass.substring(owlClass.indexOf("#")+1).equals("NamedIndividual")){
				continue;
			}
			ret += "<li>"+owlClass.substring(owlClass.indexOf("#")+1)+"</li>";
		}
		ret += "</ul>\";";

		return ret;
	}
}
