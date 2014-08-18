package nc.mairie.spring.dao.metier.agent;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.agent.HistoCharge;
import nc.mairie.technique.UserAppli;

public interface HistoChargeDaoInterface {

	public void creerHistoCharge(HistoCharge histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

}
