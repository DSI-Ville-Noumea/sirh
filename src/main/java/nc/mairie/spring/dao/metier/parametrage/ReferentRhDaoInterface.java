package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.ReferentRh;

public interface ReferentRhDaoInterface {

	public List<ReferentRh> listerDistinctReferentRh() throws Exception;

	public List<ReferentRh> listerServiceAvecReferentRh(Integer idAgentReferent);

	public void creerReferentRh(Integer idAgentReferent, Integer numeroTelephone, Integer idServiceADS, String servi)
			throws Exception;

	public void supprimerReferentRh(Integer idRferent) throws Exception;

	public ReferentRh getReferentRhGlobal();

}
