package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EaeEvalue;

public interface EaeEvalueDaoInterface {

	public void creerEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite, Date dateEntreeFonctionnaire,
			Date dateEntreeAdministration, String statut, Integer ancienneteEchelon, String cadre, String categorie, String classification,
			String grade, String echelon, Date dateEffectAvct, String nouvGrade, String nouvEchelon, String position, String typeAvct,
			String statutPrecision, Integer durMin, Integer durMoy, Integer durMax, boolean agentDetache) throws Exception;

	public EaeEvalue chercherEaeEvalue(Integer idEae) throws Exception;

	public void supprimerEaeEvalue(Integer idEaeEvalue) throws Exception;

	public ArrayList<EaeEvalue> listerEaeEvalue(Integer idAgent) throws Exception;

}
