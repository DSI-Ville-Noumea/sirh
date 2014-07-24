package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Groupe;
import nc.mairie.metier.droits.GroupeUtilisateur;
import nc.mairie.metier.droits.Utilisateur;
import nc.mairie.metier.poste.NFA;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.droits.GroupeDao;
import nc.mairie.spring.dao.metier.droits.GroupeUtilisateurDao;
import nc.mairie.spring.dao.metier.droits.UtilisateurDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.RadiWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;

import org.springframework.context.ApplicationContext;

/**
 * Process OeDROITSUtilisateurs Date de création : (12/10/11 11:52:50)
 * 
 */
public class OeDROITSUtilisateurs extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_GROUPES_AUTRES;
	private String[] LB_GROUPES_UTILISATEUR;

	private ArrayList<Utilisateur> listeUtilisateur;
	private ArrayList<Groupe> listeGroupesAutres;
	private ArrayList<Groupe> listeGroupesUtilisateur;
	private ArrayList<Groupe> listeGroupesAAjouter;
	private ArrayList<Groupe> listeGroupesARetirer;

	public String ACTION_SUPPRESSION = "Suppression d'un utilisateur.";
	private String ACTION_MODIFICATION = "Modification d'un utilisateur.";
	private String ACTION_CREATION = "Création d'un utilisateur.";

	public String ACTION_SUPPRESSION_CONSULT = "Suppression d'un utilisateur de SIRH Consultation.";

	public String focus = null;
	private Utilisateur utilisateurCourant;
	public Hashtable<String, TreeHierarchy> hTree = null;
	private ArrayList<Service> listeServices;

	private UtilisateurDao utilisateurDao;
	private GroupeDao groupeDao;
	private GroupeUtilisateurDao groupeUtilisateurDao;

	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();
		initialiseListeService();

		// Initialisation des utilisateurs
		if (getListeUtilisateur() == null || getListeUtilisateur().size() == 0) {
			setListeUtilisateur(getUtilisateurDao().listerUtilisateur());
			initialiseListeUtilisateur(request);
		}

		// Initialisation des groupes

		if (getListeGroupesUtilisateur().size() != 0) {
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Groupe> list = getListeGroupesUtilisateur().listIterator(); list.hasNext();) {
				Groupe de = (Groupe) list.next();
				String ligne[] = { de.getLibGroupe() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_GROUPES_UTILISATEUR(aFormat.getListeFormatee());
		} else {
			setLB_GROUPES_UTILISATEUR(null);
		}

		if (getListeGroupesAutres() == null) {
			setListeGroupesAutres(getGroupeDao().listerGroupe());
		}

		getListeGroupesAutres().removeAll(getListeGroupesUtilisateur());

		if (getListeGroupesAutres().size() != 0) {
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<Groupe> list = getListeGroupesAutres().listIterator(); list.hasNext();) {
				Groupe de = (Groupe) list.next();
				String ligne[] = { de.getLibGroupe() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_GROUPES_AUTRES(aFormat.getListeFormatee());
		} else {
			setLB_GROUPES_AUTRES(null);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getUtilisateurDao() == null) {
			setUtilisateurDao(new UtilisateurDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getGroupeDao() == null) {
			setGroupeDao(new GroupeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getGroupeUtilisateurDao() == null) {
			setGroupeUtilisateurDao(new GroupeUtilisateurDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             Exception
	 */
	private void initialiseListeUtilisateur(HttpServletRequest request) throws Exception {
		int indiceUtil = 0;
		if (getListeUtilisateur() != null) {
			for (int i = 0; i < getListeUtilisateur().size(); i++) {
				Utilisateur u = (Utilisateur) getListeUtilisateur().get(i);
				String listeGroupes = Const.CHAINE_VIDE;
				ArrayList<Groupe> groupesUtilisateur = getGroupeDao().listerGroupeAvecUtilisateur(u.getIdUtilisateur());
				for (int j = 0; j < groupesUtilisateur.size(); j++) {
					listeGroupes += j == 0 ? ((Groupe) groupesUtilisateur.get(j)).getLibGroupe() : ", "
							+ ((Groupe) groupesUtilisateur.get(j)).getLibGroupe();
				}
				// on fait la correspondance entre le login et l'agent via RADI
				RadiWSConsumer radiConsu = new RadiWSConsumer();
				LightUserDto user = radiConsu.getAgentCompteADByLogin(u.getLoginUtilisateur());

				String infoAgent = "&nbsp;";
				if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
					AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(),
							radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
					String prenomAgent = agent.getPrenomAgent().toLowerCase();
					String premLettre = prenomAgent.substring(0, 1).toUpperCase();
					String restePrenom = prenomAgent.substring(1, prenomAgent.length()).toLowerCase();
					prenomAgent = premLettre + restePrenom;
					String nom = agent.getNomAgent().toUpperCase();
					infoAgent = prenomAgent + " " + nom;
				}

				addZone(getNOM_ST_NOM(indiceUtil),
						u.getLoginUtilisateur().equals(Const.CHAINE_VIDE) ? "&nbsp;" : u.getLoginUtilisateur());
				addZone(getNOM_ST_GROUPES(indiceUtil), listeGroupes.equals(Const.CHAINE_VIDE) ? "&nbsp;" : listeGroupes);
				addZone(getNOM_ST_AGENT(indiceUtil), infoAgent);

				indiceUtil++;
			}
		}
	}

	public String getNomEcran() {
		return "ECR-DROIT-UTILISATEUR";
	}

	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// On vide la zone de saisie
		viderFormulaire();

		// Init de l'utilisateur courant
		setUtilisateurCourant(new Utilisateur());
		setListeGroupesAutres(getGroupeDao().listerGroupe());

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void viderFormulaire() {
		addZone(getNOM_EF_NOM_UTILISATEUR(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_GROUPES_UTILISATEUR_SELECT(), "-1");
		addZone(getNOM_LB_GROUPES_AUTRES_SELECT(), "-1");

		setListeGroupesAutres(null);
		setListeGroupesUtilisateur(null);
		setListeGroupesAAjouter(null);
		setListeGroupesARetirer(null);
	}

	public String getNOM_PB_AJOUTER_GROUPE() {
		return "NOM_PB_AJOUTER_GROUPE";
	}

	public boolean performPB_AJOUTER_GROUPE(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_GROUPES_AUTRES_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GROUPES_AUTRES_SELECT())) : -1);
		if (numLigne == -1 || getListeGroupesAutres().size() == 0 || numLigne > getListeGroupesAutres().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Autres groupes"));
			return false;
		}

		Groupe groupe = (Groupe) getListeGroupesAutres().get(numLigne);
		getListeGroupesAutres().remove(groupe);
		getListeGroupesUtilisateur().add(groupe);

		if (getListeGroupesARetirer().contains(groupe))
			getListeGroupesARetirer().remove(groupe);
		else
			getListeGroupesAAjouter().add(groupe);

		// Tri des listes
		String[] champs = { "libGroupe" };
		boolean[] ordreTri = { true };
		Services.trier(getListeGroupesAutres(), champs, ordreTri);
		Services.trier(getListeGroupesUtilisateur(), champs, ordreTri);

		return true;
	}

	public String getNOM_PB_AJOUTER_TOUT() {
		return "NOM_PB_AJOUTER_TOUT";
	}

	public boolean performPB_AJOUTER_TOUT(HttpServletRequest request) throws Exception {

		for (int i = 0; i < getListeGroupesAutres().size(); i++) {
			Groupe grpTmp = (Groupe) getListeGroupesAutres().get(i);
			if (getListeGroupesARetirer().contains(grpTmp))
				getListeGroupesARetirer().remove(grpTmp);
			else
				getListeGroupesAAjouter().add(grpTmp);
		}

		getListeGroupesUtilisateur().addAll(getListeGroupesAutres());
		getListeGroupesAutres().clear();

		// Tri des listes
		String[] champs = { "libGroupe" };
		boolean[] ordreTri = { true };
		Services.trier(getListeGroupesAutres(), champs, ordreTri);
		Services.trier(getListeGroupesUtilisateur(), champs, ordreTri);

		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		viderFormulaire();

		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		setStatut(STATUT_MEME_PROCESS);

		// Recup de l'utilisateur sélectionné
		Utilisateur aUtilisateur = (Utilisateur) getListeUtilisateur().get(indiceEltAModifier);
		setUtilisateurCourant(aUtilisateur);

		return initialiserAgentSelectionne();
	}

	public String getNOM_PB_RETIRER_GROUPE() {
		return "NOM_PB_RETIRER_GROUPE";
	}

	public boolean performPB_RETIRER_GROUPE(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_GROUPES_UTILISATEUR_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GROUPES_UTILISATEUR_SELECT())) : -1);
		if (numLigne == -1 || getListeGroupesUtilisateur().size() == 0
				|| numLigne > getListeGroupesUtilisateur().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Groupes de l'utilisateur"));
			return false;
		}

		Groupe groupe = (Groupe) getListeGroupesUtilisateur().get(numLigne);
		getListeGroupesUtilisateur().remove(groupe);
		getListeGroupesAutres().add(groupe);

		if (getListeGroupesAAjouter().contains(groupe))
			getListeGroupesAAjouter().remove(groupe);
		else
			getListeGroupesARetirer().add(groupe);

		// Tri des listes
		String[] champs = { "libGroupe" };
		boolean[] ordreTri = { true };
		Services.trier(getListeGroupesAutres(), champs, ordreTri);
		Services.trier(getListeGroupesUtilisateur(), champs, ordreTri);

		return true;
	}

	public String getNOM_PB_RETIRER_TOUT() {
		return "NOM_PB_RETIRER_TOUT";
	}

	public boolean performPB_RETIRER_TOUT(HttpServletRequest request) throws Exception {
		for (int i = 0; i < getListeGroupesUtilisateur().size(); i++) {
			Groupe grpTmp = (Groupe) getListeGroupesUtilisateur().get(i);
			if (getListeGroupesAAjouter().contains(grpTmp))
				getListeGroupesAAjouter().remove(grpTmp);
			else
				getListeGroupesARetirer().add(grpTmp);
		}

		getListeGroupesAutres().addAll(getListeGroupesUtilisateur());
		getListeGroupesUtilisateur().clear();

		// Tri des listes
		String[] champs = { "libGroupe" };
		boolean[] ordreTri = { true };
		Services.trier(getListeGroupesAutres(), champs, ordreTri);
		Services.trier(getListeGroupesUtilisateur(), champs, ordreTri);

		return true;
	}

	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);
		setStatut(STATUT_MEME_PROCESS);

		// Recup de l'utilisateur sélectionné
		Utilisateur aUtilisateur = (Utilisateur) getListeUtilisateur().get(indiceEltASuprimer);
		setUtilisateurCourant(aUtilisateur);

		return initialiserAgentSelectionne();
	}

	/**
	 * Initialise le formulaire avec les infos de l'agent sélectionné.
	 * 
	 * @throws Exception
	 *             Exception
	 */
	private boolean initialiserAgentSelectionne() throws Exception {
		// On vide la zone de saisie
		viderFormulaire();
		addZone(getNOM_EF_NOM_UTILISATEUR(), getUtilisateurCourant().getLoginUtilisateur());

		setListeGroupesAutres(getGroupeDao().listerGroupe());
		setListeGroupesUtilisateur(getGroupeDao().listerGroupeAvecUtilisateur(
				getUtilisateurCourant().getIdUtilisateur()));

		return true;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
			for (int i = 0; i < getListeGroupesUtilisateur().size(); i++) {
				Groupe g = (Groupe) getListeGroupesUtilisateur().get(i);
				GroupeUtilisateur gu = getGroupeUtilisateurDao().chercherGroupeUtilisateur(
						getUtilisateurCourant().getIdUtilisateur(), g.getIdGroupe());
				getGroupeUtilisateurDao().supprimerGroupeUtilisateurAvecGroupe(gu.getIdGroupe());
			}
			getUtilisateurDao().supprimerUtilisateur(getUtilisateurCourant().getIdUtilisateur());
		} else {
			// Contrôle des champs
			if (!performControlerSaisie())
				return false;

			getUtilisateurCourant().setLoginUtilisateur(getVAL_EF_NOM_UTILISATEUR());
			if (getUtilisateurCourant().getIdUtilisateur() == null) {
				getUtilisateurDao().creerUtilisateur(getUtilisateurCourant().getLoginUtilisateur());
			} else {
				getUtilisateurDao().modifierUtilisateur(getUtilisateurCourant().getIdUtilisateur(),
						getUtilisateurCourant().getLoginUtilisateur());
				for (int i = 0; i < getListeGroupesARetirer().size(); i++) {
					Groupe grp = (Groupe) getListeGroupesARetirer().get(i);
					GroupeUtilisateur gu = getGroupeUtilisateurDao().chercherGroupeUtilisateur(
							getUtilisateurCourant().getIdUtilisateur(), grp.getIdGroupe());
					getGroupeUtilisateurDao().supprimerGroupeUtilisateurAvecGroupe(gu.getIdGroupe());
				}
			}
			for (int i = 0; i < getListeGroupesAAjouter().size(); i++) {
				Groupe grp = (Groupe) getListeGroupesAAjouter().get(i);
				GroupeUtilisateur gu = new GroupeUtilisateur(getUtilisateurCourant().getIdUtilisateur(),
						grp.getIdGroupe());
				getGroupeUtilisateurDao().creerGroupeUtilisateur(gu.getIdUtilisateur(), gu.getIdGroupe());
			}
		}

		commitTransaction();
		setListeUtilisateur(null);
		setUtilisateurCourant(null);
		viderFormulaire();
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		return true;
	}

	/**
	 * Contrôle les zones saisies
	 * 
	 * @throws Exception
	 *             Exception
	 */
	private boolean performControlerSaisie() throws Exception {

		// ***********************
		// Verification Login
		// ***********************
		if (getZone(getNOM_EF_NOM_UTILISATEUR()).length() == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Login"));
			setFocus(getNOM_EF_NOM_UTILISATEUR());
			return false;
		}
		return true;
	}

	public String getNOM_EF_NOM_UTILISATEUR() {
		return "NOM_EF_NOM_UTILISATEUR";
	}

	public String getVAL_EF_NOM_UTILISATEUR() {
		return getZone(getNOM_EF_NOM_UTILISATEUR());
	}

	private String[] getLB_GROUPES_AUTRES() {
		if (LB_GROUPES_AUTRES == null)
			LB_GROUPES_AUTRES = initialiseLazyLB();
		return LB_GROUPES_AUTRES;
	}

	private void setLB_GROUPES_AUTRES(String[] newLB_GROUPES_AUTRES) {
		LB_GROUPES_AUTRES = newLB_GROUPES_AUTRES;
	}

	public String getNOM_LB_GROUPES_AUTRES() {
		return "NOM_LB_GROUPES_AUTRES";
	}

	public String getNOM_LB_GROUPES_AUTRES_SELECT() {
		return "NOM_LB_GROUPES_AUTRES_SELECT";
	}

	public String[] getVAL_LB_GROUPES_AUTRES() {
		return getLB_GROUPES_AUTRES();
	}

	public String getVAL_LB_GROUPES_AUTRES_SELECT() {
		return getZone(getNOM_LB_GROUPES_AUTRES_SELECT());
	}

	private String[] getLB_GROUPES_UTILISATEUR() {
		if (LB_GROUPES_UTILISATEUR == null)
			LB_GROUPES_UTILISATEUR = initialiseLazyLB();
		return LB_GROUPES_UTILISATEUR;
	}

	private void setLB_GROUPES_UTILISATEUR(String[] newLB_GROUPES_UTILISATEUR) {
		LB_GROUPES_UTILISATEUR = newLB_GROUPES_UTILISATEUR;
	}

	public String getNOM_LB_GROUPES_UTILISATEUR() {
		return "NOM_LB_GROUPES_UTILISATEUR";
	}

	public String getNOM_LB_GROUPES_UTILISATEUR_SELECT() {
		return "NOM_LB_GROUPES_UTILISATEUR_SELECT";
	}

	public String[] getVAL_LB_GROUPES_UTILISATEUR() {
		return getLB_GROUPES_UTILISATEUR();
	}

	public String getVAL_LB_GROUPES_UTILISATEUR_SELECT() {
		return getZone(getNOM_LB_GROUPES_UTILISATEUR_SELECT());
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
		return getNOM_PB_VALIDER();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_GROUPE
			if (testerParametre(request, getNOM_PB_AJOUTER_GROUPE())) {
				return performPB_AJOUTER_GROUPE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_TOUT
			if (testerParametre(request, getNOM_PB_AJOUTER_TOUT())) {
				return performPB_AJOUTER_TOUT(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeUtilisateur().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_RETIRER_GROUPE
			if (testerParametre(request, getNOM_PB_RETIRER_GROUPE())) {
				return performPB_RETIRER_GROUPE(request);
			}

			// Si clic sur le bouton PB_RETIRER_TOUT
			if (testerParametre(request, getNOM_PB_RETIRER_TOUT())) {
				return performPB_RETIRER_TOUT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeUtilisateur().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public OeDROITSUtilisateurs() {
		super();
	}

	public String getJSP() {
		return "OeDROITSUtilisateurs.jsp";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public ArrayList<Utilisateur> getListeUtilisateur() {
		return listeUtilisateur;
	}

	private void setListeUtilisateur(ArrayList<Utilisateur> listeUtilisateur) {
		this.listeUtilisateur = listeUtilisateur;
	}

	private Utilisateur getUtilisateurCourant() {
		return utilisateurCourant;
	}

	private void setUtilisateurCourant(Utilisateur utilisateurCourant) {
		this.utilisateurCourant = utilisateurCourant;
	}

	private ArrayList<Groupe> getListeGroupesUtilisateur() {
		if (listeGroupesUtilisateur == null)
			listeGroupesUtilisateur = new ArrayList<Groupe>();
		return listeGroupesUtilisateur;
	}

	private void setListeGroupesUtilisateur(ArrayList<Groupe> listeGroupesUtilisateur) {
		this.listeGroupesUtilisateur = listeGroupesUtilisateur;
	}

	private ArrayList<Groupe> getListeGroupesAutres() {
		return listeGroupesAutres;
	}

	private void setListeGroupesAutres(ArrayList<Groupe> listeGroupesAutres) {
		this.listeGroupesAutres = listeGroupesAutres;
	}

	private ArrayList<Groupe> getListeGroupesAAjouter() {
		if (listeGroupesAAjouter == null)
			listeGroupesAAjouter = new ArrayList<Groupe>();
		return listeGroupesAAjouter;
	}

	private ArrayList<Groupe> getListeGroupesARetirer() {
		if (listeGroupesARetirer == null)
			listeGroupesARetirer = new ArrayList<Groupe>();
		return listeGroupesARetirer;
	}

	private void setListeGroupesAAjouter(ArrayList<Groupe> listeGroupesAAjouter) {
		this.listeGroupesAAjouter = listeGroupesAAjouter;
	}

	private void setListeGroupesARetirer(ArrayList<Groupe> listeGroupesARetirer) {
		this.listeGroupesARetirer = listeGroupesARetirer;
	}

	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	public String getNOM_ST_GROUPES(int i) {
		return "NOM_ST_GROUPES" + i;
	}

	public String getVAL_ST_GROUPES(int i) {
		return getZone(getNOM_ST_GROUPES(i));
	}

	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	public String getNOM_ST_INFO_SERVICE() {
		return "NOM_ST_INFO_SERVICE";
	}

	public String getVAL_ST_INFO_SERVICE() {
		return getZone(getNOM_ST_INFO_SERVICE());
	}

	public String getNOM_EF_CODESERVICE() {
		return "NOM_EF_CODESERVICE";
	}

	public String getVAL_EF_CODESERVICE() {
		return getZone(getNOM_EF_CODESERVICE());
	}

	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList<Service> services = Service.listerServiceActif(getTransaction());
			setListeServices(services);

			// Tri par codeservice
			Collections.sort(getListeServices(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Service s1 = (Service) o1;
					Service s2 = (Service) o2;
					return (s1.getCodService().compareTo(s2.getCodService()));
				}
			});

			// alim de la hTree
			// RG_PE_FP_C03
			hTree = new Hashtable<String, TreeHierarchy>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService()))
					continue;

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");

				// recherche du nfa
				String nfa = NFA.chercherNFAByCodeService(getTransaction(), codeService).getNFA();
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					nfa = null;
				}

				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent, nfa));

			}
		}
	}

	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	public UtilisateurDao getUtilisateurDao() {
		return utilisateurDao;
	}

	public void setUtilisateurDao(UtilisateurDao utilisateurDao) {
		this.utilisateurDao = utilisateurDao;
	}

	public GroupeDao getGroupeDao() {
		return groupeDao;
	}

	public void setGroupeDao(GroupeDao groupeDao) {
		this.groupeDao = groupeDao;
	}

	public GroupeUtilisateurDao getGroupeUtilisateurDao() {
		return groupeUtilisateurDao;
	}

	public void setGroupeUtilisateurDao(GroupeUtilisateurDao groupeUtilisateurDao) {
		this.groupeUtilisateurDao = groupeUtilisateurDao;
	}
}
