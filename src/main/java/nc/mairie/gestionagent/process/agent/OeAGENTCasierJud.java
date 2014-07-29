package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.CasierJudiciaire;
import nc.mairie.spring.dao.metier.agent.CasierJudiciaireDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTCasierJud Date de cr�ation : (12/05/11 15:48:38)
 * 
 */
public class OeAGENTCasierJud extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	public String ACTION_SUPPRESSION = "Suppression d'un extrait.";
	public String ACTION_CONSULATION = "Consultation d'un extrait.";
	private String ACTION_MODIFICATION = "Modification d'un extrait.";
	private String ACTION_CREATION = "Cr�ation d'un extrait.";

	private AgentNW agentCourant;
	private ArrayList<CasierJudiciaire> listeCasierJud;
	private CasierJudiciaire casierJudiciaireCourant;
	public String focus = null;

	private CasierJudiciaireDao casierJudiciaireDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// V�rification des droits d'acc�s.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || etatStatut() == STATUT_RECHERCHER_AGENT
				|| MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				addZone(getNOM_ST_AGENT(), getAgentCourant().getNoMatricule() + " "
						+ getAgentCourant().getLibCivilite() + " " + getAgentCourant().getNomAgent() + " "
						+ getAgentCourant().getPrenomAgent());

				// initialisation fen�tre si changement de l'agent
				initialiseListeCasierJudAgent(request);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getCasierJudiciaireDao() == null) {
			setCasierJudiciaireDao(new CasierJudiciaireDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation de la liste des extraits de casier judiciaire
	 * 
	 */
	private void initialiseListeCasierJudAgent(HttpServletRequest request) throws Exception {

		// Recherche des extraits de casier judiciaire de l'agent
		ArrayList<CasierJudiciaire> a = getCasierJudiciaireDao().listerCasierJudiciaireAvecAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()));
		setListeCasierJud(a);

		int indiceCasierJud = 0;
		if (getListeCasierJud() != null) {
			for (int i = 0; i < getListeCasierJud().size(); i++) {
				CasierJudiciaire c = (CasierJudiciaire) getListeCasierJud().get(i);

				addZone(getNOM_ST_DATE(indiceCasierJud),
						c.getDateExtrait() == null ? "&nbsp;" : sdf.format(c.getDateExtrait()));
				addZone(getNOM_ST_NUM(indiceCasierJud),
						c.getNumExtrait().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getNumExtrait());
				addZone(getNOM_ST_PRIVATION(indiceCasierJud), c.isPrivationDroitsCiv() ? "Oui" : "Non");
				addZone(getNOM_ST_COMMENTAIRE(indiceCasierJud), c.getCommExtrait().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: c.getCommExtrait());

				indiceCasierJud++;
			}
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_EXTRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_EXTRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE_EXTRAIT(), Const.CHAINE_VIDE);
	}

	/**
	 * Initialise les zones de l'extrait de casier judiciaire courant
	 * 
	 */
	private boolean initialiseCasierJudCourant(HttpServletRequest request) throws Exception {
		// R�cup de l'extrait de casier judiciaire courant
		CasierJudiciaire c = getCasierJudiciaireCourant();

		// Alim zones
		addZone(getNOM_EF_DATE_EXTRAIT(), c.getDateExtrait() == null ? null : sdf.format(c.getDateExtrait()));
		addZone(getNOM_EF_NUM_EXTRAIT(), c.getNumExtrait());
		addZone(getNOM_RG_PRIV_DROITS_CIVIQUE(), c.isPrivationDroitsCiv() ? getNOM_RB_PRIV_DROITS_CIVIQUE_O()
				: getNOM_RB_PRIV_DROITS_CIVIQUE_N());
		addZone(getNOM_EF_COMMENTAIRE_EXTRAIT(), c.getCommExtrait());

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			initialiseListeCasierJudAgent(request);
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de cr�ation :
	 * (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 * RG_AG_CJ_A01
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On vide la zone de saisie
		addZone(getNOM_EF_DATE_EXTRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_EXTRAIT(), Const.CHAINE_VIDE);
		// RG_AG_CJ_A01
		addZone(getNOM_RG_PRIV_DROITS_CIVIQUE(), getNOM_RB_PRIV_DROITS_CIVIQUE_N());
		addZone(getNOM_EF_COMMENTAIRE_EXTRAIT(), Const.CHAINE_VIDE);

		// init de l'extrait de casier judiciaire courant
		setCasierJudiciaireCourant(new CasierJudiciaire());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// R�cup des zones saisies
		String newDateExtrait = getZone(getNOM_EF_DATE_EXTRAIT());
		String newNumExtrait = getZone(getNOM_EF_NUM_EXTRAIT());
		String newCommentaire = getZone(getNOM_EF_COMMENTAIRE_EXTRAIT());
		boolean newPrivDroitsCiv = getVAL_RG_PRIV_DROITS_CIVIQUE().equals(getNOM_RB_PRIV_DROITS_CIVIQUE_O()) ? true
				: false;

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
			// Test si un des champs a �t� modifi�
			if (!newDateExtrait.equals(getCasierJudiciaireCourant().getDateExtrait() == null ? Const.CHAINE_VIDE : sdf
					.format(getCasierJudiciaireCourant().getDateExtrait()))
					|| !newNumExtrait.equals(getCasierJudiciaireCourant().getNumExtrait())
					|| !newCommentaire.equals(getCasierJudiciaireCourant().getCommExtrait())
					|| !newPrivDroitsCiv == getCasierJudiciaireCourant().isPrivationDroitsCiv()) {
				// "ERR995","En suppression, aucune zone n'est modifiable."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR995"));
				return false;
			}

			// Suppression
			getCasierJudiciaireDao().supprimerCasierJudiciaire(getCasierJudiciaireCourant().getIdCasierJud());
			if (getTransaction().isErreur())
				return false;

		} else {
			// Affectation des attributs
			getCasierJudiciaireCourant().setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			getCasierJudiciaireCourant().setDateExtrait(sdf.parse(newDateExtrait));
			getCasierJudiciaireCourant().setNumExtrait(newNumExtrait);
			getCasierJudiciaireCourant().setPrivationDroitsCiv(newPrivDroitsCiv);
			getCasierJudiciaireCourant().setCommExtrait(newCommentaire);

			if (!performControlerChamps(request)) {
				return false;
			}

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getCasierJudiciaireDao().modifierCasierJudiciaire(getCasierJudiciaireCourant().getIdCasierJud(),
						getCasierJudiciaireCourant().getIdAgent(), getCasierJudiciaireCourant().getIdDocument(),
						getCasierJudiciaireCourant().getNumExtrait(), getCasierJudiciaireCourant().getDateExtrait(),
						getCasierJudiciaireCourant().isPrivationDroitsCiv(),
						getCasierJudiciaireCourant().getCommExtrait());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Cr�ation
				getCasierJudiciaireDao().creerCasierJudiciaire(getCasierJudiciaireCourant().getIdAgent(),
						getCasierJudiciaireCourant().getIdDocument(), getCasierJudiciaireCourant().getNumExtrait(),
						getCasierJudiciaireCourant().getDateExtrait(),
						getCasierJudiciaireCourant().isPrivationDroitsCiv(),
						getCasierJudiciaireCourant().getCommExtrait());
			}
			if (getTransaction().isErreur())
				return false;
		}

		// Tout s'est bien pass�
		commitTransaction();
		initialiseListeCasierJudAgent(request);

		return true;
	}

	/**
	 * V�rifie les r�gles de gestion de saisie (champs obligatoires, ...)
	 * 
	 * @param request
	 * @return true si les r�gles de gestion sont respect�es. false sinon.
	 * @throws Exception
	 *             RG_AG_CJ_C01
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		// date de l'extrait obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_EXTRAIT()))) {
			getTransaction()
					.declarerErreur(MessageUtils.getMessage("ERR002", "date de l'extrait de casier judiciaire"));
			setFocus(getNOM_EF_DATE_EXTRAIT());
			return false;
		} else
		// date de l'extrait doit �tre bien format�e
		if (!Services.estUneDate(getZone(getNOM_EF_DATE_EXTRAIT()))) {
			getTransaction()
					.declarerErreur(MessageUtils.getMessage("ERR007", "date de l'extrait de casier judiciaire"));
			setFocus(getNOM_EF_DATE_EXTRAIT());
			return false;
		} else {
			// date de l'extrait doit �tre inf�rieure � la date du jour
			// RG_AG_CJ_C01
			if (Services.compareDates(getZone(getNOM_EF_DATE_EXTRAIT()), Services.dateDuJour()) >= 0) {
				// ERR204 : La date @ doit �tre inf�rieure � la date @.
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR204", "d'extrait de casier judiciaire", "du jour"));
				setFocus(getNOM_EF_DATE_EXTRAIT());
				return false;
			}
		}

		// num�ro de l'extrait obligatoire
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_NUM_EXTRAIT()))) {
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR002", "num�ro de l'extrait de casier judiciaire"));
			setFocus(getNOM_EF_NUM_EXTRAIT());
			return false;
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AGENT Date de
	 * cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENTAIRE_EXTRAIT
	 * Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_EF_COMMENTAIRE_EXTRAIT() {
		return "NOM_EF_COMMENTAIRE_EXTRAIT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_COMMENTAIRE_EXTRAIT Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getVAL_EF_COMMENTAIRE_EXTRAIT() {
		return getZone(getNOM_EF_COMMENTAIRE_EXTRAIT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_EXTRAIT Date
	 * de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_EF_DATE_EXTRAIT() {
		return "NOM_EF_DATE_EXTRAIT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DATE_EXTRAIT Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getVAL_EF_DATE_EXTRAIT() {
		return getZone(getNOM_EF_DATE_EXTRAIT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DOCUMENT_ASSOCIE
	 * Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_EF_DOCUMENT_ASSOCIE() {
		return "NOM_EF_DOCUMENT_ASSOCIE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DOCUMENT_ASSOCIE Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getVAL_EF_DOCUMENT_ASSOCIE() {
		return getZone(getNOM_EF_DOCUMENT_ASSOCIE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_EXTRAIT Date de
	 * cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_EF_NUM_EXTRAIT() {
		return "NOM_EF_NUM_EXTRAIT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NUM_EXTRAIT Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getVAL_EF_NUM_EXTRAIT() {
		return getZone(getNOM_EF_NUM_EXTRAIT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_PRIV_DROITS_CIVIQUE Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_RG_PRIV_DROITS_CIVIQUE() {
		return "NOM_RG_PRIV_DROITS_CIVIQUE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_PRIV_DROITS_CIVIQUE Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getVAL_RG_PRIV_DROITS_CIVIQUE() {
		return getZone(getNOM_RG_PRIV_DROITS_CIVIQUE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_PRIV_DROITS_CIVIQUE_N
	 * Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_RB_PRIV_DROITS_CIVIQUE_N() {
		return "NOM_RB_PRIV_DROITS_CIVIQUE_N";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_PRIV_DROITS_CIVIQUE_O
	 * Date de cr�ation : (12/05/11 15:48:38)
	 * 
	 */
	public String getNOM_RB_PRIV_DROITS_CIVIQUE_O() {
		return "NOM_RB_PRIV_DROITS_CIVIQUE_O";
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return AgentNW
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met � jour l'agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne l'extrait de casier judiciaire courant.
	 * 
	 * @return CasierJudiciaire
	 */
	private CasierJudiciaire getCasierJudiciaireCourant() {
		return casierJudiciaireCourant;
	}

	/**
	 * Met � jour l'extrait de casier judiciaire courant
	 * 
	 * @param casierJudiciaireCourant
	 */
	private void setCasierJudiciaireCourant(CasierJudiciaire casierJudiciaireCourant) {
		this.casierJudiciaireCourant = casierJudiciaireCourant;
	}

	/**
	 * Retourne la liste des extraits de casier judiciaire de l'agent.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<CasierJudiciaire> getListeCasierJud() {
		return listeCasierJud;
	}

	/**
	 * Met � jour la liste des extraits de casier judiciaire de l'agent.
	 * 
	 * @param listeCasierJud
	 */
	private void setListeCasierJud(ArrayList<CasierJudiciaire> listeCasierJud) {
		this.listeCasierJud = listeCasierJud;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (13/05/11 10:14:23)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (13/05/11 10:14:23)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom du focus
	 * 
	 * @return String
	 */
	public String getFocus() {
		return focus;
	}

	/**
	 * Met � jour le focus
	 * 
	 * @param focus
	 */
	private void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Constructeur du process OeAGENTCasierJud. Date de cr�ation : (13/05/11
	 * 11:30:57)
	 * 
	 */
	public OeAGENTCasierJud() {
		super();
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PRIV_DROITS_CIV Date
	 * de cr�ation : (13/05/11 11:30:57)
	 * 
	 */
	public String getNOM_ST_PRIV_DROITS_CIV() {
		return "NOM_ST_PRIV_DROITS_CIV";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_PRIV_DROITS_CIV Date de cr�ation : (13/05/11 11:30:57)
	 * 
	 */
	public String getVAL_ST_PRIV_DROITS_CIV() {
		return getZone(getNOM_ST_PRIV_DROITS_CIV());
	}

	/**
	 * Retourne le nom de l'ecran utilis� par la gestion des droits
	 */
	public String getNomEcran() {
		return "ECR-AG-DP-CASIERJUD";
	}

	/**
	 * M�thode d'initialisation de la page, appel�e par la servlet qui aiguille
	 * le traitement : en fonction du bouton de la JSP Date de cr�ation :
	 * (12/05/11 15:48:38)
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
			for (int i = 0; i < getListeCasierJud().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeCasierJud().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeCasierJud().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (17/10/11 13:31:08)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTCasierJud.jsp";
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DATE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_NUM Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM(int i) {
		return "NOM_ST_NUM" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NUM Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM(int i) {
		return getZone(getNOM_ST_NUM(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_PRIVATION
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PRIVATION(int i) {
		return "NOM_ST_PRIVATION" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_PRIVATION Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PRIVATION(int i) {
		return getZone(getNOM_ST_PRIVATION(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_COMMENTAIRE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
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

		// R�cup de l'extrait de casier judiciaire courant
		CasierJudiciaire c = (CasierJudiciaire) getListeCasierJud().get(indiceEltAModifier);
		setCasierJudiciaireCourant(c);

		// init de l'extrait de casier judiciaire courant
		if (!initialiseCasierJudCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/09/11 10:03:38)
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

		// R�cup de l'extrait de casier judiciaire courant
		CasierJudiciaire c = (CasierJudiciaire) getListeCasierJud().get(indiceEltAConsulter);
		setCasierJudiciaireCourant(c);

		// init de l'extrait de casier judiciaire courant
		if (!initialiseCasierJudCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de cr�ation
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
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

		// R�cup de l'extrait de casier judiciaire courant
		CasierJudiciaire c = (CasierJudiciaire) getListeCasierJud().get(indiceEltASuprimer);
		setCasierJudiciaireCourant(c);

		// init de l'extrait de casier judiciaire courant
		if (!initialiseCasierJudCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public CasierJudiciaireDao getCasierJudiciaireDao() {
		return casierJudiciaireDao;
	}

	public void setCasierJudiciaireDao(CasierJudiciaireDao casierJudiciaireDao) {
		this.casierJudiciaireDao = casierJudiciaireDao;
	}
}
