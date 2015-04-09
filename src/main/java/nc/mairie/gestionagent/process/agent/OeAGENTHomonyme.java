package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.PersonnelMain;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contact;
import nc.mairie.metier.referentiel.SituationFamiliale;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.ContactDao;
import nc.mairie.spring.dao.metier.referentiel.SituationFamilialeDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTHomonyme Date de création : (29/09/11 08:36:13)
 * 
 */
public class OeAGENTHomonyme extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_AGENT_HOMONYME;
	ArrayList<Agent> listeAgtHomonyme;

	private Agent agentCourant;
	private boolean creation;
	private ContactDao contactDao;
	private SituationFamilialeDao situationFamilialeDao;
	private AgentDao agentDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (29/09/11 08:36:13)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void initialiseZones(HttpServletRequest request) throws Exception {

		if (getAgentCourant() == null) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				setCreation(getAgentCourant().getNomatr() == null);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		initialiseDao();

		setListeAgtHomonyme((ArrayList<Agent>) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_LST_AGT_HOMONYME));

		int taillesAgtHomonyme[] = { 9, 58, 58, 10 };
		FormateListe aListeAgtFormatee = new FormateListe(taillesAgtHomonyme);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (int i = 0; i < getListeAgtHomonyme().size(); i++) {
			Agent agt = (Agent) getListeAgtHomonyme().get(i);
			String colonnes[] = { agt.getNomatr().toString(), agt.getNomAgent(), agt.getPrenomAgent(),
					sdf.format(agt.getDateNaissance()) };
			aListeAgtFormatee.ajouteLigne(colonnes);
		}
		setLB_AGENT_HOMONYME(aListeAgtFormatee.getListeFormatee());
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getContactDao() == null) {
			setContactDao(new ContactDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSituationFamilialeDao() == null) {
			setSituationFamilialeDao(new SituationFamilialeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (29/09/11 08:36:13)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 08:36:13)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		VariableGlobale.enlever(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_AGT_HOMONYME Date de
	 * création : (29/09/11 08:36:13)
	 * 
	 */
	public String getNOM_PB_CREER_AGT_HOMONYME() {
		return "NOM_PB_CREER_AGT_HOMONYME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 08:36:13)
	 * 
	 */
	public boolean performPB_CREER_AGT_HOMONYME(HttpServletRequest request) throws Exception {
		// Création de l'agent
		ArrayList<Contact> lContact = getContactDao().listerContactAgent(getAgentCourant().getIdAgent());
		SituationFamiliale situFam = getSituationFamilialeDao().chercherSituationFamilialeById(
				getAgentCourant().getIdSituationFamiliale());
		getAgentDao().creerAgent(getTransaction(), getAgentCourant(), lContact, situFam);
		if (!getTransaction().isErreur()) {
			VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, getAgentCourant());
			commitTransaction();
		} else {
			return false;
		}

		setProcessAppelant(new PersonnelMain());

		if (isCreation())
			// "INF001","Agent @ créé"
			setStatut(STATUT_PROCESS_APPELANT, false,
					MessageUtils.getMessage("INF001", getAgentCourant().getNomatr().toString()));
		else
			// "INF001","Agent @ modifié"
			setStatut(STATUT_PROCESS_APPELANT, false,
					MessageUtils.getMessage("INF002", getAgentCourant().getNomatr().toString()));

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AGENT_HOMONYME Date de
	 * création : (29/09/11 08:36:13)
	 * 
	 */
	private String[] getLB_AGENT_HOMONYME() {
		if (LB_AGENT_HOMONYME == null)
			LB_AGENT_HOMONYME = initialiseLazyLB();
		return LB_AGENT_HOMONYME;
	}

	/**
	 * Setter de la liste: LB_AGENT_HOMONYME Date de création : (29/09/11
	 * 08:36:13)
	 * 
	 */
	private void setLB_AGENT_HOMONYME(String[] newLB_AGENT_HOMONYME) {
		LB_AGENT_HOMONYME = newLB_AGENT_HOMONYME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AGENT_HOMONYME Date de
	 * création : (29/09/11 08:36:13)
	 * 
	 */
	public String getNOM_LB_AGENT_HOMONYME() {
		return "NOM_LB_AGENT_HOMONYME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AGENT_HOMONYME_SELECT Date de création : (29/09/11 08:36:13)
	 * 
	 */
	public String getNOM_LB_AGENT_HOMONYME_SELECT() {
		return "NOM_LB_AGENT_HOMONYME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AGENT_HOMONYME Date de création : (29/09/11 08:36:13)
	 * 
	 */
	public String[] getVAL_LB_AGENT_HOMONYME() {
		return getLB_AGENT_HOMONYME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_AGENT_HOMONYME Date de création : (29/09/11 08:36:13)
	 * 
	 */
	public String getVAL_LB_AGENT_HOMONYME_SELECT() {
		return getZone(getNOM_LB_AGENT_HOMONYME_SELECT());
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public Agent getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met a jour l'agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Getter du booleen creation.
	 * 
	 * @return creation
	 */
	public boolean isCreation() {
		return creation;
	}

	/**
	 * Setter du booleen creation.
	 * 
	 * @param creation
	 */
	private void setCreation(boolean creation) {
		this.creation = creation;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (29/09/11 08:36:13)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECUP_AGENT_SELECTIONNE
			if (testerParametre(request, getNOM_PB_RECUP_AGENT_SELECTIONNE())) {
				return performPB_RECUP_AGENT_SELECTIONNE(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER_AGT_HOMONYME
			if (testerParametre(request, getNOM_PB_CREER_AGT_HOMONYME())) {
				return performPB_CREER_AGT_HOMONYME(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTHomonyme. Date de création : (07/11/11
	 * 09:45:21)
	 * 
	 */
	public OeAGENTHomonyme() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (07/11/11 09:45:21)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTHomonyme.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECUP_AGENT_SELECTIONNE Date
	 * de création : (07/11/11 09:45:21)
	 * 
	 */
	public String getNOM_PB_RECUP_AGENT_SELECTIONNE() {
		return "NOM_PB_RECUP_AGENT_SELECTIONNE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/11/11 09:45:21)
	 * 
	 */
	public boolean performPB_RECUP_AGENT_SELECTIONNE(HttpServletRequest request) throws Exception {
		// Recup de l'agent sélectionné
		int indiceAgt = (Services.estNumerique(getVAL_LB_AGENT_HOMONYME_SELECT()) ? Integer
				.parseInt(getVAL_LB_AGENT_HOMONYME_SELECT()) : -1);
		if (indiceAgt == -1 || getListeAgtHomonyme().size() == 0 || indiceAgt > getListeAgtHomonyme().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Agents homonymes"));
			return false;
		}

		if (indiceAgt >= 0) {
			Agent a = (Agent) getListeAgtHomonyme().get(indiceAgt);
			VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_AGENT_MAIRIE, a);
		}

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Getter de la liste des agents homonymes.
	 * 
	 * @return listeAgtHomonyme
	 */
	private ArrayList<Agent> getListeAgtHomonyme() {
		return listeAgtHomonyme;
	}

	/**
	 * Setter de la liste des agents homonymes.
	 * 
	 * @param listeAgtHomonyme
	 *            listeAgtHomonyme à définir
	 */
	private void setListeAgtHomonyme(ArrayList<Agent> listeAgtHomonyme) {
		this.listeAgtHomonyme = listeAgtHomonyme;
	}

	public ContactDao getContactDao() {
		return contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}

	public SituationFamilialeDao getSituationFamilialeDao() {
		return situationFamilialeDao;
	}

	public void setSituationFamilialeDao(SituationFamilialeDao situationFamilialeDao) {
		this.situationFamilialeDao = situationFamilialeDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
