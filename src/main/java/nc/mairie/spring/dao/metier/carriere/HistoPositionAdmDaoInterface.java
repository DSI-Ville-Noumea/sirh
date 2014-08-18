package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.carriere.HistoPositionAdm;
import nc.mairie.technique.UserAppli;

public interface HistoPositionAdmDaoInterface {

	public void creerHistoPositionAdm(HistoPositionAdm histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

}
