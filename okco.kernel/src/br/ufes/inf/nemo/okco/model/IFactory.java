package br.ufes.inf.nemo.okco.model;

public interface IFactory {
	
	public IRepository GetRepository();

	public IReasoner GetReasoner(EnumReasoner reasoner);

}
