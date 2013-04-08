package nc.mairie.spring.dao.metier.EAE;

import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;

public interface EaeEvaluationDaoInterface {

	public EaeEvaluation chercherEaeEvaluation(Integer idEAE) throws Exception;

	public int compterAvisSHDNonDefini(Integer idCampagneEAE, String direction, String section) throws Exception;

	public int compterAvisSHDAvct(Integer idCampagneEAE, String direction, String section, String dureeAvct) throws Exception;

	public int compterAvisSHDChangementClasse(Integer idCampagneEAE, String direction, String section) throws Exception;

	public void modifierCommentaireEvaluateurEaeEvaluation(Integer idEaeEvaluation, Integer idEaeCommentaire) throws Exception;

	public void modifierNoteEaeEvaluation(Integer idEaeEvaluation, double note) throws Exception;

	public void modifierNiveauEaeEvaluation(Integer idEaeEvaluation, String niveau) throws Exception;

	public void modifierADEaeEvaluation(Integer idEaeEvaluation, String propositionAD) throws Exception;

	public void modifierChgtClasseEaeEvaluation(Integer idEaeEvaluation, Integer chgtClasse) throws Exception;

	public void modifierRevaloEaeEvaluation(Integer idEaeEvaluation, Integer revalorisation) throws Exception;

	public void modifierRapportCirconstancieEaeEvaluation(Integer idEaeEvaluation, Integer idEaeRapportCircon) throws Exception;
}
