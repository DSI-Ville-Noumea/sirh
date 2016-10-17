package nc.mairie.spring.dao.metier.poste;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.poste.HistoVisiteMedicale;
import nc.mairie.technique.UserAppli;

public interface HistoVisiteMedicaleDaoInterface {

	public void creerHistoVisiteMedicale(HistoVisiteMedicale histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

}
