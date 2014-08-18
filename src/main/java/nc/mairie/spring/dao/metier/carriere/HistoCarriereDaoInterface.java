package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.technique.UserAppli;

public interface HistoCarriereDaoInterface {

	public void creerHistoCarriere(HistoCarriere histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

}
