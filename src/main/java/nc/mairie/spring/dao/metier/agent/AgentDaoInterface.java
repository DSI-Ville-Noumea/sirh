package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contact;
import nc.mairie.metier.referentiel.SituationFamiliale;
import nc.mairie.technique.Transaction;

public interface AgentDaoInterface {

	public ArrayList<Agent> listerAgentWithStatut(String population) throws Exception;

	public ArrayList<Agent> listerAgentAvecCafatCommencant(String zone) throws Exception;

	public ArrayList<Agent> listerAgentAvecServicesETMatricules(ArrayList<String> codesServices, Integer idAgentMin,
			Integer idAgentMax) throws Exception;

	public ArrayList<Agent> listerAgentEntreDeuxIdAgent(Integer idAgentMin, Integer idAgentMax) throws Exception;

	public Agent chercherIrcafex(String numIrcafex, Integer idAgent) throws Exception;

	public Agent chercherCre(String numCre, Integer idAgent) throws Exception;

	public Agent chercherClr(String numClr, Integer idAgent) throws Exception;

	public Agent chercherMutuelle(String numMutuelle, Integer idAgent) throws Exception;

	public Agent chercherRuam(String numRuamm, Integer idAgent) throws Exception;

	public Agent chercherCafat(String numCafat, Integer idAgent) throws Exception;

	public ArrayList<Agent> listerAgentWithListNomatr(String listNoMatr) throws Exception;

	public ArrayList<Agent> listerAgentEligibleEAE(String listeNomatr, Date dateJourFormatSIRH) throws Exception;

	public ArrayList<Agent> listerAgentSansVMPAEnCours(String listeNomatr) throws Exception;

	public ArrayList<Agent> listerAgentNouveauxArrivant(Integer moisChoisi, Integer anneeChoisi) throws Exception;

	public Agent chercherAgentParMatricule(Integer noMatr) throws Exception;

	public Agent chercherAgentAffecteFichePosteSecondaire(Integer idFichePoste) throws Exception;

	public Agent chercherAgentAffecteFichePoste(Integer idFichePoste) throws Exception;

	public ArrayList<Agent> listerAgentAvecEnfant(Integer idEnfant) throws Exception;

	public ArrayList<Agent> listerAgentHomonyme(String nom, String prenom, Date dateNaiss) throws Exception;

	public ArrayList<Agent> listerAgentAvecPrenomCommencant(String debPrenom) throws Exception;

	public ArrayList<Agent> listerAgentAvecServiceCommencant(String debCodeService) throws Exception;

	public ArrayList<Agent> listerAgentAvecNomCommencant(String debNom) throws Exception;

	public Agent chercherAgent(Integer idAgent) throws Exception;

	public ArrayList<Agent> listerAgentEligibleAvct(ArrayList<String> listeSousService, String listeNoMatr)
			throws Exception;

	public List<Agent> listerAgent() throws Exception;

	public void creerAgent(Transaction aTransaction, Agent agent, ArrayList<Contact> lContact,
			SituationFamiliale situFam) throws Exception;

	public void modifierAgent(Transaction aTransaction, Agent agent, ArrayList<Contact> lContact,
			SituationFamiliale situFam) throws Exception;

}
