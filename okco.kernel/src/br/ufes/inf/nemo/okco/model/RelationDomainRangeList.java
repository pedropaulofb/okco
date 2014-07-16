package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class RelationDomainRangeList {
	
	public String Relation;
	public ArrayList<DomainRange> listDomainRange;
	
	public RelationDomainRangeList()
	{
		this.listDomainRange = new ArrayList<DomainRange>();
	}
	
	public static RelationDomainRangeList getElement(ArrayList<RelationDomainRangeList> list, String subProp) 
	{
		for (RelationDomainRangeList elem : list) 
		{
			if (elem.Relation.equals(subProp)) {
				return elem;
			}
		}
		return null;
	} 

}
