package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.StatutFP;

public interface StatutFPDaoInterface {

	public List<StatutFP> listerStatutFP() throws Exception;

	public StatutFP chercherStatutFP(Integer idStatut) throws Exception;

}
