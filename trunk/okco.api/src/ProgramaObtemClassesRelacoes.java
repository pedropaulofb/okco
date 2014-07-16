import java.io.InputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import br.ufes.inf.nemo.okco.business.Search;

public class ProgramaObtemClassesRelacoes {
	
	public static void main(String[] args) {
		
		String pathOwlFile = "C://Users//fabio_000//Desktop//OntologiasOWL//G800Completa.owl";
		
		InputStream in = FileManager.get().open(pathOwlFile);
		if (in == null) {
			System.out.println("Arquivo não encontrado");
		}
		
		//Create model
		OntModel model = null;
		model = ModelFactory.createOntologyModel();
		
		model.read(in,null);		
		String ns = model.getNsPrefixURI("");		  
		if(ns == null)
		{
			System.out.println("Namespace não definido");
		}
		
		Search search = new Search(ns);
		
		ArrayList<String> lclasses = search.GetClasses(model);
		ArrayList<String> lpropreties = search.GetProperties(model);
		
		String result1 = "";
		String result2 = "";
		
		for (String cls : lclasses) {
			
			if(cls != null)
				result1 = result1 + "'" + cls.replace(ns, "") + "' | ";
		}
		
		for (String prop : lpropreties) {
			
			if(prop != null && ! prop.contains("www.w3.org"))
			{
				result2 = result2 + "'" + prop.replace(ns, "") + "' | ";
				
			}
		}
		
		System.out.println(result1);
		System.out.println("");
		System.out.println(result2);		
	  	
	}

}
