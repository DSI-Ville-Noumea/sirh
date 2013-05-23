package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Groupe;
import nc.mairie.metier.droits.GroupeUtilisateur;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.metier.droits.Utilisateur;
import nc.mairie.metier.poste.NFA;
import nc.mairie.metier.poste.Service;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;

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

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (12/10/11 11:52:50)
	 * 
	 */
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
		initialiseListeService();

		// Initialisation des utilisateurs
		if (getListeUtilisateur() == null || getListeUtilisateur().size() == 0) {
			setListeUtilisateur(Utilisateur.listerUtilisateur(getTransaction()));
			initialiseListeUtilisateur(request);
		}

		// Initialisation des groupes
		int[] taillesGroupe = { 50 };
		String[] champs = { "libGroupe" };

		setLB_GROUPES_UTILISATEUR(new FormateListe(taillesGroupe, getListeGroupesUtilisateur(), champs).getListeFormatee());

		if (getListeGroupesAutres() == null) {
			setListeGroupesAutres(Groupe.listerGroupe(getTransaction()));
		}

		getListeGroupesAutres().removeAll(getListeGroupesUtilisateur());

		setLB_GROUPES_AUTRES(new FormateListe(taillesGroupe, getListeGroupesAutres(), champs).getListeFormatee());
	}

	/**
	 * @param request
	 * @throws Exception
	 */
	private void initialiseListeUtilisateur(HttpServletRequest request) throws Exception {
		int indiceUtil = 0;
		if (getListeUtilisateur() != null) {
			for (int i = 0; i < getListeUtilisateur().size(); i++) {
				Utilisateur u = (Utilisateur) getListeUtilisateur().get(i);
				String listeGroupes = Const.CHAINE_VIDE;
				ArrayList<Groupe> groupesUtilisateur = Groupe.listerGroupeAvecUtilisateur(getTransaction(), u.getIdUtilisateur());
				for (int j = 0; j < groupesUtilisateur.size(); j++) {
					listeGroupes += j == 0 ? ((Groupe) groupesUtilisateur.get(j)).getLibGroupe() : ", "
							+ ((Groupe) groupesUtilisateur.get(j)).getLibGroupe();
				}
				// on fait la correspondance entre le login et l'agent via la
				// table SIIDMA
				Siidma user = Siidma.chercherSiidma(getTransaction(), u.getLoginUtilisateur().toUpperCase());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				String infoAgent = "&nbsp;";
				if (user != null && user.getNomatr() != null) {
					AgentNW agent = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
					String prenomAgent = agent.getPrenomAgent().toLowerCase();
					String premLettre = prenomAgent.substring(0, 1).toUpperCase();
					String restePrenom = prenomAgent.substring(1, prenomAgent.length()).toLowerCase();
					prenomAgent = premLettre + restePrenom;
					String nom = agent.getNomAgent().toUpperCase();
					infoAgent = prenomAgent + " " + nom;
				}

				addZone(getNOM_ST_NOM(indiceUtil), u.getLoginUtilisateur().equals(Const.CHAINE_VIDE) ? "&nbsp;" : u.getLoginUtilisateur());
				addZone(getNOM_ST_GROUPES(indiceUtil), listeGroupes.equals(Const.CHAINE_VIDE) ? "&nbsp;" : listeGroupes);
				addZone(getNOM_ST_AGENT(indiceUtil), infoAgent);

				indiceUtil++;
			}
		}
	}

	/**
	 * Retourne le nom de l'ecran utilisé par la gestion des droits
	 */
	public String getNomEcran() {
		return "ECR-DROIT-UTILISATEUR";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// On vide la zone de saisie
		viderFormulaire();

		// Init de l'utilisateur courant
		setUtilisateurCourant(new Utilisateur());
		setListeGroupesAutres(Groupe.listerGroupe(getTransaction()));

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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_GROUPE Date de
	 * création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_AJOUTER_GROUPE() {
		return "NOM_PB_AJOUTER_GROUPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_TOUT Date de
	 * création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_AJOUTER_TOUT() {
		return "NOM_PB_AJOUTER_TOUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		viderFormulaire();

		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER Date de création :
	 * (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		setStatut(STATUT_MEME_PROCESS);

		// Recup de l'utilisateur sélectionné
		Utilisateur aUtilisateur = (Utilisateur) getListeUtilisateur().get(indiceEltAModifier);
		setUtilisateurCourant(aUtilisateur);

		return initialiserAgentSelectionne();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RETIRER_GROUPE Date de
	 * création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_RETIRER_GROUPE() {
		return "NOM_PB_RETIRER_GROUPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public boolean performPB_RETIRER_GROUPE(HttpServletRequest request) throws Exception {
		// Recup du groupe sélectionné
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_GROUPES_UTILISATEUR_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GROUPES_UTILISATEUR_SELECT())) : -1);
		if (numLigne == -1 || getListeGroupesUtilisateur().size() == 0 || numLigne > getListeGroupesUtilisateur().size()) {
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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RETIRER_TOUT Date de
	 * création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_RETIRER_TOUT() {
		return "NOM_PB_RETIRER_TOUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER Date de création :
	 * (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
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
	 */
	private boolean initialiserAgentSelectionne() throws Exception {
		// On vide la zone de saisie
		viderFormulaire();
		addZone(getNOM_EF_NOM_UTILISATEUR(), getUtilisateurCourant().getLoginUtilisateur());

		setListeGroupesAutres(Groupe.listerGroupe(getTransaction()));
		setListeGroupesUtilisateur(Groupe.listerGroupeAvecUtilisateur(getTransaction(), getUtilisateurCourant().getIdUtilisateur()));

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION)) {
			for (int i = 0; i < getListeGroupesUtilisateur().size(); i++) {
				Groupe g = (Groupe) getListeGroupesUtilisateur().get(i);
				GroupeUtilisateur gu = GroupeUtilisateur.chercherGroupeUtilisateur(getTransaction(), getUtilisateurCourant().getIdUtilisateur(),
						g.getIdGroupe());
				gu.supprimerGroupeUtilisateur(getTransaction());
			}
			getUtilisateurCourant().supprimerUtilisateur(getTransaction());
		} else {
			// Contrôle des champs
			if (!performControlerSaisie())
				return false;

			getUtilisateurCourant().setLoginUtilisateur(getVAL_EF_NOM_UTILISATEUR());
			if (getUtilisateurCourant().getIdUtilisateur() == null) {
				getUtilisateurCourant().creerUtilisateur(getTransaction());
			} else {
				getUtilisateurCourant().modifierUtilisateur(getTransaction());
				for (int i = 0; i < getListeGroupesARetirer().size(); i++) {
					Groupe grp = (Groupe) getListeGroupesARetirer().get(i);
					GroupeUtilisateur gu = GroupeUtilisateur.chercherGroupeUtilisateur(getTransaction(), getUtilisateurCourant().getIdUtilisateur(),
							grp.getIdGroupe());
					gu.supprimerGroupeUtilisateur(getTransaction());
				}
			}
			for (int i = 0; i < getListeGroupesAAjouter().size(); i++) {
				Groupe grp = (Groupe) getListeGroupesAAjouter().get(i);
				GroupeUtilisateur gu = new GroupeUtilisateur(getUtilisateurCourant().getIdUtilisateur(), grp.getIdGroupe());
				gu.creerGroupeUtilisateur(getTransaction());
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

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_UTILISATEUR
	 * Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_EF_NOM_UTILISATEUR() {
		return "NOM_EF_NOM_UTILISATEUR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_UTILISATEUR Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String getVAL_EF_NOM_UTILISATEUR() {
		return getZone(getNOM_EF_NOM_UTILISATEUR());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_GROUPES_AUTRES Date de
	 * création : (12/10/11 11:52:50)
	 * 
	 */
	private String[] getLB_GROUPES_AUTRES() {
		if (LB_GROUPES_AUTRES == null)
			LB_GROUPES_AUTRES = initialiseLazyLB();
		return LB_GROUPES_AUTRES;
	}

	/**
	 * Setter de la liste: LB_GROUPES_AUTRES Date de création : (12/10/11
	 * 11:52:50)
	 * 
	 */
	private void setLB_GROUPES_AUTRES(String[] newLB_GROUPES_AUTRES) {
		LB_GROUPES_AUTRES = newLB_GROUPES_AUTRES;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_GROUPES_AUTRES Date de
	 * création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_LB_GROUPES_AUTRES() {
		return "NOM_LB_GROUPES_AUTRES";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_GROUPES_AUTRES_SELECT Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_LB_GROUPES_AUTRES_SELECT() {
		return "NOM_LB_GROUPES_AUTRES_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_GROUPES_AUTRES Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String[] getVAL_LB_GROUPES_AUTRES() {
		return getLB_GROUPES_AUTRES();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_GROUPES_AUTRES Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String getVAL_LB_GROUPES_AUTRES_SELECT() {
		return getZone(getNOM_LB_GROUPES_AUTRES_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_GROUPES_UTILISATEUR Date
	 * de création : (12/10/11 11:52:50)
	 * 
	 */
	private String[] getLB_GROUPES_UTILISATEUR() {
		if (LB_GROUPES_UTILISATEUR == null)
			LB_GROUPES_UTILISATEUR = initialiseLazyLB();
		return LB_GROUPES_UTILISATEUR;
	}

	/**
	 * Setter de la liste: LB_GROUPES_UTILISATEUR Date de création : (12/10/11
	 * 11:52:50)
	 * 
	 */
	private void setLB_GROUPES_UTILISATEUR(String[] newLB_GROUPES_UTILISATEUR) {
		LB_GROUPES_UTILISATEUR = newLB_GROUPES_UTILISATEUR;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_GROUPES_UTILISATEUR Date
	 * de création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_LB_GROUPES_UTILISATEUR() {
		return "NOM_LB_GROUPES_UTILISATEUR";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_GROUPES_UTILISATEUR_SELECT Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String getNOM_LB_GROUPES_UTILISATEUR_SELECT() {
		return "NOM_LB_GROUPES_UTILISATEUR_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_GROUPES_UTILISATEUR Date de création : (12/10/11 11:52:50)
	 * 
	 */
	public String[] getVAL_LB_GROUPES_UTILISATEUR() {
		return getLB_GROUPES_UTILISATEUR();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_GROUPES_UTILISATEUR Date de création : (12/10/11 11:52:50)
	 * 
	 */
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

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (12/10/11 11:52:50)
	 * 
	 */
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

	/**
	 * Constructeur du process OeDROITSUtilisateurs. Date de création :
	 * (12/10/11 13:54:22)
	 * 
	 */
	public OeDROITSUtilisateurs() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (12/10/11 13:54:22)
	 * 
	 */
	public String getJSP() {
		return "OeDROITSUtilisateurs.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/10/11 13:54:22)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/10/11 13:54:22)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Getter de la liste des utilisateurs de SIRH.
	 * 
	 * @return listeUtilisateur
	 */
	public ArrayList<Utilisateur> getListeUtilisateur() {
		return listeUtilisateur;
	}

	/**
	 * Setter de la liste des utilisateurs de SIRH.
	 * 
	 * @param listeUtilisateur
	 */
	private void setListeUtilisateur(ArrayList<Utilisateur> listeUtilisateur) {
		this.listeUtilisateur = listeUtilisateur;
	}

	/**
	 * Getter de l'utilisateur courant.
	 * 
	 * @return utilisateurCourant
	 */
	private Utilisateur getUtilisateurCourant() {
		return utilisateurCourant;
	}

	/**
	 * Setter de l'utilisateur courant
	 * 
	 * @param utilisateurCourant
	 */
	private void setUtilisateurCourant(Utilisateur utilisateurCourant) {
		this.utilisateurCourant = utilisateurCourant;
	}

	/**
	 * Getter de la liste des groupes de l'utilisateur.
	 * 
	 * @return listeGroupesUtilisateur
	 */
	private ArrayList<Groupe> getListeGroupesUtilisateur() {
		if (listeGroupesUtilisateur == null)
			listeGroupesUtilisateur = new ArrayList<Groupe>();
		return listeGroupesUtilisateur;
	}

	/**
	 * Setter de la liste des groupes de l'utilisateur.
	 * 
	 * @param listeGroupesUtilisateur
	 */
	private void setListeGroupesUtilisateur(ArrayList<Groupe> listeGroupesUtilisateur) {
		this.listeGroupesUtilisateur = listeGroupesUtilisateur;
	}

	/**
	 * Getter de la liste des autres groupes auxquels n'appartient pas
	 * l'utilisateur.
	 * 
	 * @return listeGroupesAutres
	 */
	private ArrayList<Groupe> getListeGroupesAutres() {
		return listeGroupesAutres;
	}

	/**
	 * Setter de la liste des autres groupes auxquels n'appartient pas
	 * l'utilisateur.
	 * 
	 * @param listeGroupesAutres
	 */
	private void setListeGroupesAutres(ArrayList<Groupe> listeGroupesAutres) {
		this.listeGroupesAutres = listeGroupesAutres;
	}

	/**
	 * Getter ges groupes à ajouter à l'utilisateur.
	 * 
	 * @return listeGroupesAAjouter
	 */
	private ArrayList<Groupe> getListeGroupesAAjouter() {
		if (listeGroupesAAjouter == null)
			listeGroupesAAjouter = new ArrayList<Groupe>();
		return listeGroupesAAjouter;
	}

	/**
	 * Getter des groupes à retirer de l'utilisateur.
	 * 
	 * @return listeGroupesARetirer
	 */
	private ArrayList<Groupe> getListeGroupesARetirer() {
		if (listeGroupesARetirer == null)
			listeGroupesARetirer = new ArrayList<Groupe>();
		return listeGroupesARetirer;
	}

	/**
	 * Setter de la liste des groupes à ajouter à l'utilisateur.
	 * 
	 * @param listeGroupesAAjouter
	 */
	private void setListeGroupesAAjouter(ArrayList<Groupe> listeGroupesAAjouter) {
		this.listeGroupesAAjouter = listeGroupesAAjouter;
	}

	/**
	 * Setter de la liste des groupes à retirer de l'utilisateur.
	 * 
	 * @param listeGroupesARetirer
	 */
	private void setListeGroupesARetirer(ArrayList<Groupe> listeGroupesARetirer) {
		this.listeGroupesARetirer = listeGroupesARetirer;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GROUPES Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_GROUPES(int i) {
		return "NOM_ST_GROUPES" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GROUPES Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_GROUPES(int i) {
		return getZone(getNOM_ST_GROUPES(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (25/07/11 16:45:35)
	 * 
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (26/07/11 09:08:33)
	 * 
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_SERVICE Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 */
	public String getNOM_ST_INFO_SERVICE() {
		return "NOM_ST_INFO_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_SERVICE
	 * Date de création : (29/11/11 16:42:44)
	 * 
	 */
	public String getVAL_ST_INFO_SERVICE() {
		return getZone(getNOM_ST_INFO_SERVICE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODESERVICE Date de
	 * création : (12/08/11 11:27:01)
	 * 
	 */
	public String getNOM_EF_CODESERVICE() {
		return "NOM_EF_CODESERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODESERVICE Date de création : (12/08/11 11:27:01)
	 * 
	 */
	public String getVAL_EF_CODESERVICE() {
		return getZone(getNOM_EF_CODESERVICE());
	}

	/**
	 * Initialise la liste des services.
	 * 
	 * @throws Exception
	 *             RG_PE_FP_C03
	 */
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

	/**
	 * Retourne la liste des services.
	 * 
	 * @return listeServices
	 */
	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	/**
	 * Met à jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Retourne une hashTable de la hiérarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}
}
