package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.eae.EaeEvalue;

public interface EaeEvalueDaoInterface {

	public void creerEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite,
			Date dateEntreeFonctionnaire, Date dateEntreeAdministration, String statut, Integer ancienneteEchelon,
			String cadre, String categorie, String classification, String grade, String echelon, Date dateEffectAvct,
			String nouvGrade, String nouvEchelon, String position, String typeAvct, String statutPrecision,
			Integer durMin, Integer durMoy, Integer durMax, boolean agentAffecte) throws Exception;

	public void modifierEaeEvalue(Integer idEae, Integer idAgent, Date dateEntreeService, Date dateEntreeCollectivite,
			Date dateEntreeFonctionnaire, Date dateEntreeAdministration, String statut, Integer ancienneteEchelon,
			String cadre, String categorie, String classification, String grade, String echelon, Date dateEffectAvct,
			String nouvGrade, String nouvEchelon, String position, String typeAvct, String statutPrecision,
			Integer durMin, Integer durMoy, Integer durMax, boolean agentAffecte) throws Exception;

	public EaeEvalue chercherEaeEvalue(Integer idEae) throws Exception;

	public ArrayList<EaeEvalue> listerEaeEvalueSans2012(Integer idAgent) throws Exception;

	public void modifierDateEaeEvalue(Integer idEaeEvalue, EaeEvalue evalue);

}
