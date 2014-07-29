package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.SituationFamiliale;

public interface SituationFamilialeDaoInterface {

	public SituationFamiliale chercherSituationFamilialeById(Integer idSituation) throws Exception;

	public List<SituationFamiliale> listerSituationFamiliale() throws Exception;

}
