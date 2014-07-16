import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import br.ufes.inf.nemo.okco.business.Search;

/*
 * With the gambiarra.txt 
 * generate the parser code by http://zaach.github.io/jison/try/
 * 
 * */
public class GenerataCondelLanguage {
	
	public static void main(String[] args) {
		
		String pathOwlFile = "C://Users//nemo//Documents//advisor//Projetos//tnokco//src//main//webapp//Assets//owl//g800.owl";
//		String pathOwlFile = "C://Users//fabio_000//Desktop//OntologiasOWL//G800Completa.owl";
		
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
		
		String ituClassesAndRelations = "/* ITU CLASSES */";
		String bottomString = "";
		
		boolean ehGambiarra = true;
		
		
		for (String cls : lclasses) {
			
			if(cls != null){
				ituClassesAndRelations += "'"+cls.replace(ns, "")+"' return	'"+cls.replace(ns, "")+"'\n";
				if(ehGambiarra)
					bottomString += "'"+cls.replace(ns, "")+"'\n";
				else
					bottomString += " | '"+cls.replace(ns, "")+"'\n";
				ehGambiarra = false;
			}
		}
		bottomString += ";\n";
		
		ituClassesAndRelations += "\n\n/* ITU Relations */\n";
		bottomString += "\nRelation:\n";
		ehGambiarra = true;
		for (String prop : lpropreties) {
			
			if(prop != null && ! prop.contains("www.w3.org"))
			{
				ituClassesAndRelations += "'"+prop.replace(ns, "")+"' return	'"+prop.replace(ns, "")+"'\n";
				if(ehGambiarra)
					bottomString += "'"+prop.replace(ns, "")+"'\n";
				else
					bottomString += " | '"+prop.replace(ns, "")+"'\n";
				ehGambiarra = false;
			}
		}
		bottomString += ";";
		
		
		
		BufferedWriter writer = null;
        try {
            //create a temporary file
            File logFile = new File("newCondelLanguage.txt");

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(part_0);
            writer.write(ituClassesAndRelations);
            writer.write(part_1);
            writer.write("\n\n\n/*-------------------------------------------------------------*/\n\n\n\nClasses:\n");
            writer.write(bottomString);
            writer.write(part_2);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	  	
	}
	
	/* default texts */
	
	private static final String part_0 = "/* lexical grammar */"
			+ "\n%lex"
			+ "\n%%"
			+ "\n"
			+ "\\s+  /* skip whitespace */"
			+ "\n/* Reserved words */";

	private static final String part_1 = "'diff' return 'diff'"
			+ "\n'same' return 'same'"
			+ "\n"
			+ "\n/* Attributes */"
			+ "\n'location'		return 'location'"
			+ "\n'type'			return 'type'"
			+ "\n"
			+ "\n'lat.deg'		return 'lat.deg'"
			+ "\n'lat.min'		return 'lat.min'"
			+ "\n'lat.sec'		return 'lat.sec'"
			+ "\n"
			+ "\n'lon.deg'		return 'lon.deg'"
			+ "\n'lon.min'		return 'lon.min'"
			+ "\n'lon.sec'		return 'lon.sec'"
			+ "\n"
			+ "\n/* Language constraints */"
			+ "\n':'				return 'colon'"
			+ "\n'.'				return 'dot'"
			+ "\n';'				return 'semicolon'"
			+ "\n'='				return 'equal'"
			+ "\n','				return 'comma'"
			+ "\n'{'				return 'l_curly_bracket'"
			+ "\n'}'				return 'r_curly_bracket'"
			+ "\n'('				return 'l_parenthesis'"
			+ "\n')'				return 'r_parenthesis'"
			+ "\n"
			+ "\n('-')?[0-9]+	return 'graus'"
			+ "\n[A-Za-z_]+[0-9A-Za-z_]* return 'variable'"
			+ "\n[0-9]+					return 'n_int'"
			+ "\n"
			+ "\n\"/*\"(.|\\n|\\r)*?\"*/\"  /* ignore */           "
			+ "\n<<EOF>>         return 'EOF'"
			+ "\n.				return 'INVALID'"
			+ "\n"
			+ "\n/lex"
			+ "\n"
			+ "\n"
			+ "\n%start expressions"
			+ "\n"
			+ "\n%% /* language grammar */"
			+ "\n"
			+ "\nexpressions"
			+ "\n    : Instruction  EOF | Instruction  expressions"
			+ "\n    ;"
			+ "\n	"
			+ "\nvariable_declaration:"
			+ "\n	variable | (variable comma  variable_declaration)"
			+ "\n;"
			+ "\n"
			+ "\nInstruction:"
			+ "\nIndividualDeclaration | AttributeDeclaration | RelationDeclaration | DifferentFromDeclaration | SameAsDeclaration"
			+ "\n;"
			+ "\n"
			+ "\nIndividualDeclaration:"
			+ "\n(Classes colon variable_declaration semicolon)"
			+ "\n;"
			+ "\n"
			+ "\nAttributeDeclaration:"
			+ "\n	TypeDeclaration | LocationDeclaration | CoordinateDeclaration"
			+ "\n;"
			+ "\n"
			+ "\nTypeDeclaration:"
			+ "\n	variable dot type colon variable semicolon"
			+ "\n;"
			+ "\n"
			+ "\nLocationDeclaration:"
			+ "\n	variable dot location colon variable semicolon"
			+ "\n;"
			+ "\n"
			+ "\nCoordinateDeclaration:"
			+ "\n(variable dot lat.deg colon graus semicolon) | "
			+ "\n(variable dot lat.min colon graus semicolon) | "
			+ "\n(variable dot lat.sec colon graus semicolon) | "
			+ "\n"
			+ "\n(variable dot lon.deg colon graus semicolon) | "
			+ "\n(variable dot lon.min colon graus semicolon) | "
			+ "\n(variable dot lon.sec colon graus semicolon)"
			+ "\n;"
			+ "\n"
			+ "\nRelationDeclaration:"
			+ "\n	variable Relation variable_declaration semicolon"
			+ "\n;"
			+ "\n"
			+ "\nDifferentFromDeclaration:"
			+ "\n	variable diff variable_declaration semicolon"
			+ "\n;"
			+ "\n"
			+ "\nSameAsDeclaration:"
			+ "\n	variable same variable_declaration semicolon"
			+ "\n;";
		
	private static final String part_2 = "\n%%";
}