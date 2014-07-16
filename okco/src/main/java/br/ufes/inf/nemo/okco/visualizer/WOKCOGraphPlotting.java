package br.ufes.inf.nemo.okco.visualizer;

public class WOKCOGraphPlotting extends GraphPlotting {

	@Override
	public String getArborNode(String elem, boolean isCenterNode){
		String arborNode = "";
		String name;
		if(elem.contains("^^")){
			//Datatype
			name = elem.substring(0, elem.indexOf("^^"));
			arborNode = "graph.addNode(\""+name+"\", {shape:\"dot\", color:\""+VERMELHO+"\"})";
		}else{
			//Element
			name = elem.substring(elem.indexOf("#")+1);
			String shape,color;

			if(isClass(elem)){
				shape = "default";
				color = AZUL;
			}else{
				shape = "dot";
				color = VERDE;
			}

			if(isCenterNode){
				color = ROXO;
			}
			arborNode += "graph.addNode(\""+name+"\", {shape:\""+shape+"\", color:\""+color+"\"})";
		}

		return arborNode;
	}

	@Override
	public boolean isClass(String elem) {
		if(hash.get(elem) == null){
			return false;
		}
		return true;
	}
	
	@Override
	public String getSubtitle() {
		return "Subtitle_WOKCO.png";
	}
}
