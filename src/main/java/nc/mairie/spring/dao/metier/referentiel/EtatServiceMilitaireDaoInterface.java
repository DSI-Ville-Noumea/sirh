package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.EtatServiceMilitaire;

public interface EtatServiceMilitaireDaoInterface {

	public List<EtatServiceMilitaire> listerEtatServiceMilitaire() throws Exception;

}
