package br.ufes.inf.nemo.okco.model;

public class DtoGetPrevNextSpecProperty {

	public DtoPropertyAndSubProperties dto;
	public String iSourceName;
	public String ns;
	public boolean haveNext;
	public boolean havePrev;
	
	public DtoGetPrevNextSpecProperty(String iSourceName, String ns, DtoPropertyAndSubProperties dto, boolean haveNext, boolean havePrev)
	{
		this.iSourceName = iSourceName;
		this.ns = ns;
		this.dto = dto;
		this.haveNext = haveNext;
		this.havePrev = havePrev;
	}
	
}
