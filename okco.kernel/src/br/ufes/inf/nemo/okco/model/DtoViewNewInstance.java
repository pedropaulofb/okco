package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoViewNewInstance {
	
	private static int cont = 0;	//Count the number of instances
	
	public int id;
	public String ns;
	public String name;
	public ArrayList<String> listDifferent;
	public ArrayList<String> listSame;
	public ArrayList<String> listerro;
	
	public DtoViewNewInstance()
	{
		cont++;
		this.id=cont;
		this.listDifferent = new ArrayList<String>();
		this.listSame = new ArrayList<String>();
		this.listerro = new ArrayList<String>();
	}

	public static void removeFromList(
			ArrayList<DtoViewNewInstance> listNewInstances, String idRemove) {
		
		for (DtoViewNewInstance dto : listNewInstances) {
			if(dto.id == Integer.parseInt(idRemove))
			{
				listNewInstances.remove(dto);
				break;
			}
		}		
		
	}

}
