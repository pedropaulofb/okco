package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class TupleInstanceExist {
	
	/* Used in DtoViewSelectInstance */
	
	public String ns;
	public String name;
	public boolean exist;
	
	public TupleInstanceExist (String ns, String name, boolean exist)
	{
		this.ns = ns;
		this.name = name;
		this.exist = exist;
	}

	public static TupleInstanceExist getTuple(String iName,	ArrayList<TupleInstanceExist> list) {

		for (TupleInstanceExist t : list) 
		{			
			String uri = t.ns + t.name;
			if(uri.equals(iName))
			{
				return t;
			}
		}
		return null;
	}

}
