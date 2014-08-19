package nc.mairie.spring.dao.metier.poste;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.poste.HistoAffectation;
import nc.mairie.technique.UserAppli;

public interface HistoAffectationDaoInterface {

	public void creerHistoAffectation(HistoAffectation histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

}
