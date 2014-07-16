package br.ufes.inf.nemo.okco.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.swing.JFileChooser;

import br.ufes.inf.nemo.okco.model.IRepository;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class Repository implements IRepository {

	private OntModel Model;
	public static String NameSpace;
	
	public Repository()
	{
		Model = ModelFactory.createOntologyModel();
	}
	
	public OntModel Open(String inputFileName)
	{
    	InputStream  in= FileManager.get().open(inputFileName);
		if (in == null) {
		    throw new IllegalArgumentException("File: " + inputFileName + " not found");
		}
		
		Model.read(in,null);
		
		return Model;
	}
	
	public OntModel Open(InputStream  in)
	{
		if (in == null) {
		    throw new IllegalArgumentException("File not found");
		}
		
		Model.read(in,null);
		
		return Model;
	}
	
	public OntModel CopyModel(OntModel ontModel)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ontModel.write(out, "RDF/XML");
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        OntModel mNew = ModelFactory.createOntologyModel();
        mNew.read(in,null);
        Model = mNew;
		
		return mNew;
	}

	public String getNameSpace(OntModel model)
	{
		//Get the base namespace
		NameSpace = model.getNsPrefixURI("");		
		return NameSpace;
	}

	public void Save(OntModel model, String path)
	{		
		this.Model= model;
		
		if(path.contains(".owl")){
			//OK
		} else {
			path = path + ".owl";
		}			
		
		OutputStream output = null;
		try {
			output = new FileOutputStream(path);
			this.Model.write(output,"RDF/XML");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void SaveWithDialog(OntModel model)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i= fileChooser.showSaveDialog(null);
        if (i==1){
        	System.out.println("Escolha um diretório para salvar o arquivo.\n");
        } else {
        	File file = fileChooser.getSelectedFile();
        	//textArea.setText(arquivo.getPath());
        	
        	//Commit
    		this.Save(model, file.getPath());    		
    		System.out.println( "Ontology save in file:\n" + file.getPath() + "\n" );
        }
	}
	
	public void Print(OntModel model) {
		
		model.write(System.out, "RDF/XML");
	}
	
	public String getModelString(OntModel model) {

		String syntax = "RDF/XML";
		StringWriter out = new StringWriter();
		model.write(out, syntax);
		String result = out.toString();
		
		return result;
		
        
	}
}
