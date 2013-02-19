package nc.mairie.spring.dao.metier.suiviMedical;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.domain.metier.suiviMedical.SuiviMedical;

public interface SuiviMedicalDaoInterface {

	public ArrayList<SuiviMedical> listerSuiviMedical() throws Exception;

	public ArrayList<SuiviMedical> listerSuiviMedicalAvecMoisetAnneeSansEffectue(Integer mois, Integer annee, AgentNW agent,
			ArrayList<String> listeSousService, String relance, String motifVM) throws Exception;

	public ArrayList<SuiviMedical> listerSuiviMedicalNonEffectue(Integer mois, Integer annee, String etat) throws Exception;

	public SuiviMedical chercherSuiviMedical(Integer idSuiviMed) throws Exception;

	public String getStatutSM(String codeStatut);

	public void supprimerSuiviMedicalTravail(String etat) throws Exception;

	public void supprimerSuiviMedicalTravailAvecMoisetAnnee(String etat, Integer mois, Integer annee) throws Exception;

	public void supprimerSuiviMedicalById(Integer idSuiviMed) throws Exception;

	public void creerSuiviMedical(Integer idAgent, Integer nomatr, String agent, String statut, String idServi, Date dateDerniereVisite,
			Date datePrevisionVisite, Integer idMotifVM, Integer nbVisitesRatees, Integer idMedecin, Date dateProchaineVisite,
			String heureProchaineVisite, String etat, Integer mois, Integer annee, Integer relance) throws Exception;

	public SuiviMedical chercherSuiviMedicalAgentMoisetAnnee(Integer idAgent, Integer mois, Integer annee) throws Exception;

	public SuiviMedical chercherSuiviMedicalAgentNomatrMoisetAnnee(Integer noMatr, Integer mois, Integer annee) throws Exception;

	public void modifierSuiviMedicalTravail(Integer idSuiviMed, SuiviMedical smSelct) throws Exception;

	public ArrayList<SuiviMedical> listerHistoriqueSuiviMedical(Integer annee, Integer mois, String etatConvoq, String etatAccomp, String etatPlanif)
			throws Exception;

	public ArrayList<SuiviMedical> listerSuiviMedicalEtatAgent(Integer idAgent, String etatConvoq, String etatPlanif, String etatImprime)
			throws Exception;

	public ArrayList<SuiviMedical> listerSuiviMedicalAgentAnterieurDate(Integer idAgent, Integer mois, Integer annee) throws Exception;

	public SuiviMedical chercherDernierSuiviMedicalAgent(Integer idAgent) throws Exception;
}
