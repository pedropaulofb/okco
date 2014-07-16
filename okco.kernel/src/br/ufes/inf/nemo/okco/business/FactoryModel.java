package br.ufes.inf.nemo.okco.business;

import br.ufes.inf.nemo.okco.model.EnumReasoner;
import br.ufes.inf.nemo.okco.model.IFactory;
import br.ufes.inf.nemo.okco.model.IReasoner;
import br.ufes.inf.nemo.okco.model.IRepository;

public class FactoryModel implements IFactory{

	public IRepository GetRepository() {
		return new Repository();
	}

	public IReasoner GetReasoner(EnumReasoner reasoner) {

		if (reasoner == EnumReasoner.PELLET)
		{
			return new PelletReasoner();
		}
		else //if (reasoner == EnumReasoner.HERMIT)
		{
			return new HermitReasoner();
			
		}
	}

}
