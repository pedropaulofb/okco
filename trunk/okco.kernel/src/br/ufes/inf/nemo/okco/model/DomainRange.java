package br.ufes.inf.nemo.okco.model;

import java.util.ArrayList;

public class DomainRange {

	public String Domain;
	public String Range;
	
	public DomainRange(String d, String r)
	{
		this.Domain = d;
		this.Range = r;
	}

	public static boolean haveDomainList(String cls, ArrayList<DomainRange> listPropDomainRange) {

		for (DomainRange dr : listPropDomainRange) {
			
			if(dr.Domain.equals(cls))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean haveRangeinList(String cls, ArrayList<DomainRange> listPropDomainRange) {

		for (DomainRange dr : listPropDomainRange) {
			
			if(dr.Range.equals(cls))
			{
				return true;
			}
		}
		return false;
	}
}
