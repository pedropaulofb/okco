package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DataPropertyValue {
	
	private static int count = 0;	//Count the number of instances
	
	public String value;
	public String classValue;
	public boolean existInModel;
	public int id;
	
	public DataPropertyValue()
	{
		count++;
		id = count;
		value = "";
		classValue = "";
		existInModel = false;
	}
	
	public static void removeFromList(ArrayList<DataPropertyValue> list, String id) {

		for (DataPropertyValue data : list) {
			
			if(data.id == Integer.parseInt(id))
			{
				list.remove(data);
				break;
			}
		}		
	}
	

}
