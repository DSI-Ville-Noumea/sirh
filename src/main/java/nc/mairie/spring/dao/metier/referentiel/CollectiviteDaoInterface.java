package nc.mairie.spring.dao.metier.referentiel;

import java.util.List;

import nc.mairie.metier.referentiel.Collectivite;

public interface CollectiviteDaoInterface {

	public List<Collectivite> listerCollectivite() throws Exception;

}
