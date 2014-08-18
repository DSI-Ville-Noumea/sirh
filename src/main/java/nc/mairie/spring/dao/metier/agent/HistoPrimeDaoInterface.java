package nc.mairie.spring.dao.metier.agent;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.agent.HistoPrime;
import nc.mairie.technique.UserAppli;

public interface HistoPrimeDaoInterface {

	public void creerHistoPrime(HistoPrime histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

}
