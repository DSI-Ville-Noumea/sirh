package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.referentiel.AutreAdministration;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTCONTACTGestion Date de création : (11/02/03 14:20:31)
 * 
 */
public class OeAGENTADMINISTRATIONGestion extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_ADMINISTRATION;

	public String ACTION_SUPPRESSION = "Suppression d'une administration.";
	public String ACTION_CONSULTATION = "Consultation d'une administration.";
	private String ACTION_MODIFICATION = "Modification d'une administration.";
	private String ACTION_CREATION = "Création d'une administration.";

	private Agent AgentCourant;
	private Hashtable<Integer, AutreAdministration> hashAdministration;
	private ArrayList<AutreAdministrationAgent> listeAgentAdministrations;
	private ArrayList<AutreAdministration> listeAdministrations;
	private AutreAdministrationAgent autreAdministrationAgentCourant;
	@SuppressWarnings("unused")
	private AutreAdministration autreAdministrationCourant;
	public String focus = null;

	private AutreAdministrationDao autreAdministrationDao;
	private AutreAdministrationAgentDao autreAdministrationAgentDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Constructeur du process OeAGENTCONTACTGestion. Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public OeAGENTADMINISTRATIONGestion() {
		super();
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 11:01:39)
	 * 
	 * @return nc.mairie.metier.agent.AdministrationAgent
	 */
	private AutreAdministrationAgent getAutreAdministrationAgentCourant() {
		return autreAdministrationAgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (11/02/2003
	 * 15:15:56)
	 * 
	 * @return nc.mairie.metier.agent.Agent
	 */
	public Agent getAgentCourant() {
		return AgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:14:48)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<Integer, AutreAdministration> getHashAdministration() {
		if (hashAdministration == null) {
			hashAdministration = new Hashtable<Integer, AutreAdministration>();
		}
		return hashAdministration;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ADMINISTRATION Date de
	 * création : (11/02/03 14:20:32)
	 * 
	 */
	private String[] getLB_ADMINISTRATION() {
		if (LB_ADMINISTRATION == null)
			LB_ADMINISTRATION = initialiseLazyLB();
		return LB_ADMINISTRATION;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:47:43)
	 * 
	 * @return ArrayList
	 */
	private ArrayList<AutreAdministration> getListeAdministrations() {
		if (listeAdministrations == null) {
			listeAdministrations = new ArrayList<AutreAdministration>();
		}
		return listeAdministrations;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:38:02)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<AutreAdministrationAgent> getListeAgentAdministrations() {
		if (listeAgentAdministrations == null) {
			listeAgentAdministrations = new ArrayList<AutreAdministrationAgent>();
		}
		return listeAgentAdministrations;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ADMINISTRATION Date de
	 * création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_ADMINISTRATION() {
		return "NOM_LB_ADMINISTRATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ADMINISTRATION_SELECT Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_ADMINISTRATION_SELECT() {
		return "NOM_LB_ADMINISTRATION_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AGENT_ADMINISTRATION Date
	 * de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_AGENT_ADMINISTRATION() {
		return "NOM_LB_AGENT_ADMINISTRATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AGENT_ADMINISTRATION_SELECT Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getNOM_LB_AGENT_ADMINISTRATION_SELECT() {
		return "NOM_LB_AGENT_ADMINISTRATION_SELECT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ADMINISTRATION Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String[] getVAL_LB_ADMINISTRATION() {
		return getLB_ADMINISTRATION();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ADMINISTRATION Date de création : (11/02/03 14:20:32)
	 * 
	 */
	public String getVAL_LB_ADMINISTRATION_SELECT() {
		return getZone(getNOM_LB_ADMINISTRATION_SELECT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Initialisation de la liste des administrations
	 * 
	 */
	private void initialiseFenetre(HttpServletRequest request) throws Exception {

		// Recherche des administrations de l'agent
		ArrayList<AutreAdministrationAgent> a = getAutreAdministrationAgentDao()
				.listerAutreAdministrationAgentAvecAgent(getAgentCourant().getIdAgent());
		setListeAgentAdministrations(a);

		// Init de la liste des administrations de l'agent
		initialiseListeAgentAdministrations(request);

		// Alim des zones
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_ADMINISTRATION_SELECT(), "0");
		addZone(getNOM_RG_FONCTIONNAIRE(), getNOM_RB_FONCTIONNAIRE_N());
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
	}

	/**
	 * Initialisation de la liste des administrations
	 * 
	 */
	private void initialiseListeAgentAdministrations(HttpServletRequest request) throws Exception {

		int indiceAdministration = 0;
		if (getListeAgentAdministrations() != null) {
			for (int i = 0; i < getListeAgentAdministrations().size(); i++) {
				AutreAdministrationAgent aAdministrationAgent = (AutreAdministrationAgent) getListeAgentAdministrations()
						.get(i);
				AutreAdministration aAdministration = getAutreAdministrationDao().chercherAutreAdministration(
						aAdministrationAgent.getIdAutreAdmin());

				addZone(getNOM_ST_ADMINISTRATION(indiceAdministration),
						aAdministration.getLibAutreAdmin().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aAdministration
								.getLibAutreAdmin());
				addZone(getNOM_ST_FONCTIONNAIRE(indiceAdministration),
						aAdministrationAgent.getFonctionnaire() == 0 ? "NON" : "OUI");
				addZone(getNOM_ST_DATE_ENTREE(indiceAdministration), sdf.format(aAdministrationAgent.getDateEntree()));
				addZone(getNOM_ST_DATE_SORTIE(indiceAdministration),
						aAdministrationAgent.getDateSortie() == null ? "&nbsp;" : sdf.format(aAdministrationAgent
								.getDateSortie()));

				indiceAdministration++;
			}
		}
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				addZone(getNOM_ST_AGENT(), getAgentCourant().getNomatr() + " " + getAgentCourant().getLibCivilite()
						+ " " + getAgentCourant().getNomAgent() + " " + getAgentCourant().getPrenomAgent());

				// initialisation fenêtre si changement de l'agent
				initialiseFenetre(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		// Si liste des administrations vide ou si statut gestion admin
		if (getLB_ADMINISTRATION() == LBVide) {
			ArrayList<AutreAdministration> a = getAutreAdministrationDao().listerAutreAdministration();
			setListeAdministrations(a);

			if (getListeAdministrations().size() != 0) {
				int[] tailles = { 50 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<AutreAdministration> list = getListeAdministrations().listIterator(); list.hasNext();) {
					AutreAdministration de = (AutreAdministration) list.next();
					String ligne[] = { de.getLibAutreAdmin() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_ADMINISTRATION(aFormat.getListeFormatee());
			} else {
				setLB_ADMINISTRATION(null);
			}

			// Remplissage de la hashtable des types de administrations.
			for (ListIterator<AutreAdministration> list = a.listIterator(); list.hasNext();) {
				AutreAdministration aAdministration = (AutreAdministration) list.next();
				getHashAdministration().put(aAdministration.getIdAutreAdmin(), aAdministration);
			}
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAutreAdministrationDao() == null) {
			setAutreAdministrationDao(new AutreAdministrationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationAgentDao() == null) {
			setAutreAdministrationAgentDao(new AutreAdministrationAgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			initialiseFenetre(request);
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// On vide la zone de saisie
		addZone(getNOM_LB_ADMINISTRATION_SELECT(), "0");
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_FONCTIONNAIRE(), getNOM_RB_FONCTIONNAIRE_N());

		// init du administration courant
		setAutreAdministrationAgentCourant(new AutreAdministrationAgent());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		AutreAdministrationAgent adminAgentDepart = new AutreAdministrationAgent(getAutreAdministrationAgentCourant()
				.getIdAutreAdmin(), getAutreAdministrationAgentCourant().getIdAgent(),
				getAutreAdministrationAgentCourant().getDateEntree());
		// Récup des zones saisies
		String newDateDeb = Services.formateDate(getZone(getNOM_EF_DATE_DEBUT()));
		String newDateFin = Services.formateDate(getZone(getNOM_EF_DATE_FIN()));
		if (getVAL_RG_FONCTIONNAIRE().equals(getNOM_RB_FONCTIONNAIRE_N())) {
			getAutreAdministrationAgentCourant().setFonctionnaire(0);
		} else {
			getAutreAdministrationAgentCourant().setFonctionnaire(1);
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
			// Suppression
			getAutreAdministrationAgentDao().supprimerAutreAdministrationAgent(
					getAutreAdministrationAgentCourant().getIdAutreAdmin(),
					getAutreAdministrationAgentCourant().getIdAgent(),
					getAutreAdministrationAgentCourant().getDateEntree());
			if (getTransaction().isErreur())
				return false;

		} else {
			if (!performControlerChamps()) {
				return false;
			}

			// Recup Administration
			AutreAdministration newAdministration = (AutreAdministration) getListeAdministrations().get(
					Integer.parseInt(getZone(getNOM_LB_ADMINISTRATION_SELECT())));

			// Affectation des attributs
			getAutreAdministrationAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
			getAutreAdministrationAgentCourant().setIdAutreAdmin(newAdministration.getIdAutreAdmin());
			getAutreAdministrationAgentCourant().setDateEntree(sdf.parse(newDateDeb));
			getAutreAdministrationAgentCourant().setDateSortie(newDateFin == null ? null : sdf.parse(newDateFin));

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getAutreAdministrationAgentDao().supprimerAutreAdministrationAgent(adminAgentDepart.getIdAutreAdmin(),
						adminAgentDepart.getIdAgent(), adminAgentDepart.getDateEntree());
				getAutreAdministrationAgentDao().creerAutreAdministrationAgent(
						getAutreAdministrationAgentCourant().getIdAutreAdmin(),
						getAutreAdministrationAgentCourant().getIdAgent(),
						getAutreAdministrationAgentCourant().getDateEntree(),
						getAutreAdministrationAgentCourant().getDateSortie(),
						getAutreAdministrationAgentCourant().getFonctionnaire());

			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				getAutreAdministrationAgentDao().creerAutreAdministrationAgent(
						getAutreAdministrationAgentCourant().getIdAutreAdmin(),
						getAutreAdministrationAgentCourant().getIdAgent(),
						getAutreAdministrationAgentCourant().getDateEntree(),
						getAutreAdministrationAgentCourant().getDateSortie(),
						getAutreAdministrationAgentCourant().getFonctionnaire());
			}

			if (getTransaction().isErreur())
				return false;
		}
		// Tout s'est bien passé
		commitTransaction();
		initialiseFenetre(request);

		return true;
	}

	/**
	 * Controle la bonne saisie des champs.
	 * 
	 * @return boolean
	 * @throws Exception
	 *             RG_AG_AA_C01
	 */
	public boolean performControlerChamps() throws Exception {
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			// format de date
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
				// ERR007 : La date @ est incorrecte. Elle doit être au format
				// date.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'entrée"));
				setFocus(getNOM_EF_DATE_DEBUT());
				return false;
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
				if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
					// ERR007 : La date @ est incorrecte. Elle doit être au
					// format date.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de sortie"));
					setFocus(getNOM_EF_DATE_FIN());
					return false;
				} else if (Services.compareDates(getZone(getNOM_EF_DATE_DEBUT()), getZone(getNOM_EF_DATE_FIN())) >= 0) {
					// ERR205 : La date @ doit être supérieure à la date @.
					// RG_AG_AA_C01
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de sortie", "d'entrée"));
					setFocus(getNOM_EF_DATE_FIN());
					return false;
				}
			}
		} else {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date d'entrée"));
			setFocus(getNOM_EF_DATE_DEBUT());
			return false;
		}

		return true;
	}

	/**
	 * Controle les RGs.
	 * 
	 * @return boolean
	 * @throws Exception
	 *             RG_AG_AA_A02
	 */
	public boolean performControlerRG() throws Exception {

		for (ListIterator<AutreAdministrationAgent> list = getListeAgentAdministrations().listIterator(); list
				.hasNext();) {
			AutreAdministrationAgent aAdministrationAgent = (AutreAdministrationAgent) list.next();
			if (aAdministrationAgent.getIdAutreAdmin() != getAutreAdministrationAgentCourant().getIdAutreAdmin()) {
				if (Services.compareDates(getVAL_EF_DATE_FIN(), sdf.format(aAdministrationAgent.getDateEntree())) >= 0
						&& Services.compareDates(getVAL_EF_DATE_DEBUT(),
								sdf.format(aAdministrationAgent.getDateSortie())) <= 0) {
					// "ERR201",
					// "Opération impossible. La période saisie ne doit pas chevaucher les périodes précédentes."
					// RG_AG_AA_A02
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR201"));
					setFocus(getNOM_EF_DATE_DEBUT());
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 11:01:39)
	 * 
	 * @param newAdministrationAgentCourant
	 *            nc.mairie.metier.agent.AdministrationAgent
	 */
	private void setAutreAdministrationAgentCourant(AutreAdministrationAgent newAutreAdministrationAgentCourant) {
		autreAdministrationAgentCourant = newAutreAdministrationAgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (11/02/2003
	 * 15:15:56)
	 * 
	 * @param newAgentCourant
	 *            nc.mairie.metier.agent.Agent
	 */
	private void setAgentCourant(Agent newAgentCourant) {
		AgentCourant = newAgentCourant;
	}

	/**
	 * Setter de la liste: LB_ADMINISTRATION Date de création : (11/02/03
	 * 14:20:32)
	 * 
	 */
	private void setLB_ADMINISTRATION(String[] newLB_ADMINISTRATION) {
		LB_ADMINISTRATION = newLB_ADMINISTRATION;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:47:43)
	 * 
	 * @param newListeAdministrations
	 *            ArrayList
	 */
	private void setListeAdministrations(ArrayList<AutreAdministration> newListeAdministrations) {
		listeAdministrations = newListeAdministrations;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (13/02/2003
	 * 10:38:02)
	 * 
	 * @param newListeAgentAdministrations
	 *            ArrayList
	 */
	private void setListeAgentAdministrations(ArrayList<AutreAdministrationAgent> newListeAgentAdministrations) {
		listeAgentAdministrations = newListeAgentAdministrations;
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_EF_DATE_DEBUT();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ADMINISTRATION Date
	 * de création : (29/09/08 10:39:32)
	 * 
	 */
	public String getNOM_ST_ADMINISTRATION() {
		return "NOM_ST_ADMINISTRATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ADMINISTRATION
	 * Date de création : (29/09/08 10:39:32)
	 * 
	 */
	public String getVAL_ST_ADMINISTRATION() {
		return getZone(getNOM_ST_ADMINISTRATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FONCTIONNAIRE Date
	 * de création : (29/09/08 10:39:32)
	 * 
	 */
	public String getNOM_ST_FONCTIONNAIRE() {
		return "NOM_ST_FONCTIONNAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FONCTIONNAIRE
	 * Date de création : (29/09/08 10:39:32)
	 * 
	 */
	public String getVAL_ST_FONCTIONNAIRE() {
		return getZone(getNOM_ST_FONCTIONNAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DEBUT Date de
	 * création : (29/09/08 10:39:32)
	 * 
	 */
	public String getNOM_ST_DEBUT() {
		return "NOM_ST_DEBUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DEBUT Date de
	 * création : (29/09/08 10:39:32)
	 * 
	 */
	public String getVAL_ST_DEBUT() {
		return getZone(getNOM_ST_DEBUT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FIN Date de création
	 * : (29/09/08 10:39:32)
	 * 
	 */
	public String getNOM_ST_FIN() {
		return "NOM_ST_FIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FIN Date de
	 * création : (29/09/08 10:39:32)
	 * 
	 */
	public String getVAL_ST_FIN() {
		return getZone(getNOM_ST_FIN());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_FONCTIONNAIRE Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RG_FONCTIONNAIRE() {
		return "NOM_RG_FONCTIONNAIRE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_FONCTIONNAIRE Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_RG_FONCTIONNAIRE() {
		return getZone(getNOM_RG_FONCTIONNAIRE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_FONCTIONNAIRE_N Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_FONCTIONNAIRE_N() {
		return "NOM_RB_FONCTIONNAIRE_N";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_FONCTIONNAIRE_O Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_FONCTIONNAIRE_O() {
		return "NOM_RB_FONCTIONNAIRE_O";
	}

	/**
	 * Met à jour l'administration courante
	 * 
	 * @param autreAdministrationCourant
	 */
	private void setAutreAdministrationCourant(AutreAdministration autreAdministrationCourant) {
		this.autreAdministrationCourant = autreAdministrationCourant;
	}

	public String getNomEcran() {
		return "ECR-AG-DP-AUTRESADMIN";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAgentAdministrations().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeAgentAdministrations().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeAgentAdministrations().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si pas de retour définit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non géré par le process");
		return false;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 11:24:24)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTADMINISTRATIONGestion.jsp";
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_ADMINISTRATION Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_ADMINISTRATION(int i) {
		return "NOM_ST_ADMINISTRATION" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ADMINISTRATION
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_ADMINISTRATION(int i) {
		return getZone(getNOM_ST_ADMINISTRATION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FONCTIONNAIRE Date
	 * de création : (29/09/08 10:39:32)
	 * 
	 */
	public String getNOM_ST_FONCTIONNAIRE(int i) {
		return "NOM_ST_FONCTIONNAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FONCTIONNAIRE
	 * Date de création : (29/09/08 10:39:32)
	 * 
	 */
	public String getVAL_ST_FONCTIONNAIRE(int i) {
		return getZone(getNOM_ST_FONCTIONNAIRE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DATE_ENTREE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_ENTREE(int i) {
		return "NOM_ST_DATE_ENTREE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ENTREE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_ENTREE(int i) {
		return getZone(getNOM_ST_DATE_ENTREE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DATE_SORTIE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_SORTIE(int i) {
		return "NOM_ST_DATE_SORTIE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_SORTIE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_SORTIE(int i) {
		return getZone(getNOM_ST_DATE_SORTIE(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// Récup du administration courant
		AutreAdministrationAgent c = (AutreAdministrationAgent) getListeAgentAdministrations().get(indiceEltAModifier);
		setAutreAdministrationAgentCourant(c);
		AutreAdministration t = (AutreAdministration) getHashAdministration().get(c.getIdAutreAdmin());

		// Alim zones
		int ligneType = getListeAdministrations().indexOf(t);
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(c.getDateEntree()));
		addZone(getNOM_EF_DATE_FIN(), c.getDateSortie() == null ? null : sdf.format(c.getDateSortie()));
		addZone(getNOM_LB_ADMINISTRATION_SELECT(), String.valueOf(ligneType));
		addZone(getNOM_RG_FONCTIONNAIRE(), c.getFonctionnaire() == 0 ? getNOM_RB_FONCTIONNAIRE_N()
				: getNOM_RB_FONCTIONNAIRE_O());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// Récup du administration courant
		AutreAdministrationAgent ag = (AutreAdministrationAgent) getListeAgentAdministrations()
				.get(indiceEltAConsulter);
		setAutreAdministrationAgentCourant(ag);
		AutreAdministration aa = (AutreAdministration) getHashAdministration().get(ag.getIdAutreAdmin());
		setAutreAdministrationCourant(aa);

		// Alim zones
		// int ligneType =
		// getListeAdministrations().indexOf(getHashAdministration().get(c.getCodAdministration()));
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(ag.getDateEntree()));
		addZone(getNOM_EF_DATE_FIN(), ag.getDateSortie() == null ? null : sdf.format(ag.getDateSortie()));
		addZone(getNOM_ST_ADMINISTRATION(), aa.getLibAutreAdmin());
		addZone(getNOM_ST_FONCTIONNAIRE(), ag.getFonctionnaire() == 0 ? "NON" : "OUI");

		setFocus(getNOM_PB_VALIDER());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// Récup du administration courant
		AutreAdministrationAgent ag = (AutreAdministrationAgent) getListeAgentAdministrations().get(indiceEltASuprimer);
		setAutreAdministrationAgentCourant(ag);
		AutreAdministration aa = (AutreAdministration) getHashAdministration().get(ag.getIdAutreAdmin());
		setAutreAdministrationCourant(aa);

		// Alim zones
		// int ligneType =
		// getListeAdministrations().indexOf(getHashAdministration().get(c.getCodAdministration()));
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(ag.getDateEntree()));
		addZone(getNOM_EF_DATE_FIN(), ag.getDateSortie() == null ? null : sdf.format(ag.getDateSortie()));
		addZone(getNOM_ST_ADMINISTRATION(), aa.getLibAutreAdmin());
		addZone(getNOM_ST_FONCTIONNAIRE(), ag.getFonctionnaire() == 0 ? "NON" : "OUI");

		setFocus(getNOM_PB_VALIDER());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public AutreAdministrationDao getAutreAdministrationDao() {
		return autreAdministrationDao;
	}

	public void setAutreAdministrationDao(AutreAdministrationDao autreAdministrationDao) {
		this.autreAdministrationDao = autreAdministrationDao;
	}

	public AutreAdministrationAgentDao getAutreAdministrationAgentDao() {
		return autreAdministrationAgentDao;
	}

	public void setAutreAdministrationAgentDao(AutreAdministrationAgentDao autreAdministrationAgentDao) {
		this.autreAdministrationAgentDao = autreAdministrationAgentDao;
	}
}
