package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DtoViewSelectInstance {
	
	public Instance instance;
	public ArrayList<TupleInstanceExist > listSameShow;
	public ArrayList<TupleInstanceExist > listDifferentShow;
	
	public DtoViewSelectInstance(Instance i, ArrayList<Instance> listAllInstances)
	{
		this.instance = i;
		this.listDifferentShow = new ArrayList<TupleInstanceExist>();
		this.listSameShow = new ArrayList<TupleInstanceExist>();
		
		String uri = i.ns + i.name;
		
		//Create all instances using - false
		for (Instance instance : listAllInstances) 
		{	
			if(!(uri.equals(instance.ns + instance.name)))
			{
				this.listSameShow.add(new TupleInstanceExist(instance.ns, instance.name, false));
				this.listDifferentShow.add(new TupleInstanceExist(instance.ns, instance.name, false));
			}
			
		}
		
		//Setting true
		for (String iName : i.ListSameInstances) 
		{
			TupleInstanceExist t = TupleInstanceExist.getTuple(iName, listSameShow);
			if(!(t == null))
			{
				t.exist = true;
			}
		}
		
		//Setting true
		for (String iName : i.ListDiferentInstances) 
		{
			TupleInstanceExist t = TupleInstanceExist.getTuple(iName, listDifferentShow);
			if(!(t == null))
			{
				t.exist = true;
			}
		}
	}

}
