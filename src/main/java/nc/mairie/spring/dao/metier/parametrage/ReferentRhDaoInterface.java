package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.ReferentRh;

public interface ReferentRhDaoInterface {

	public List<ReferentRh> listerReferentRh() throws Exception;

	public List<ReferentRh> listerDistinctReferentRh() throws Exception;

	public List<ReferentRh> listerServiceAvecReferentRh(Integer idAgentReferent);

	public void creerReferentRh(String servi, Integer idAgentReferent, Integer numeroTelephone) throws Exception;

	public void supprimerReferentRh(String servi) throws Exception;

}
