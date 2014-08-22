package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.eae.EAE;

public interface EaeEAEDaoInterface {

	public ArrayList<EAE> listerEAETravailPourCampagne(String etat, Integer idCampagneEAE) throws Exception;

	public ArrayList<EAE> listerEAEPourCampagne(Integer idCampagneEAE, String etat, String statut,
			ArrayList<String> listeSousService, String cap, Agent agentEvaluateur, Agent agentEvalue, String detach)
			throws Exception;

	public ArrayList<EAE> listerEAEFinaliseControlePourCampagne(Integer idCampagneEAE) throws Exception;

	public EAE chercherEAEAgent(Integer idAgent, Integer idCampagneEAE);

	public Integer creerEAE(Integer idCampagneEae, String etat, boolean cap, boolean docAttache, Date dateCreation,
			Date dateFin, Date dateEntretien, Integer dureeEntretien, Date dateFinalise, Date dateControle,
			String heureControle, String userControle, Integer idDelegataire) throws Exception;

	public void supprimerEAE(Integer idEAE) throws Exception;

	public void modifierDelegataire(Integer idEAE, Integer idDelegataire) throws Exception;

	public void modifierCAP(Integer idEAE, boolean cap) throws Exception;

	public void modifierSuiteCreation(Integer idEAE, Date dateCreation, String etat) throws Exception;

	public void modifierEtat(Integer idEAE, String etat) throws Exception;

	public void modifierControle(Integer idEAE, Date dateControle, String heureControle, String userControle,
			String etat) throws Exception;

	public int compterEAEDirectionSectionEtat(Integer idCampagneEAE, String direction, String section, String etat)
			throws Exception;

	public int compterEAEDirectionSectionCAP(Integer idCampagneEAE, String direction, String section) throws Exception;

	public EAE chercherEAE(Integer idEae) throws Exception;
}
