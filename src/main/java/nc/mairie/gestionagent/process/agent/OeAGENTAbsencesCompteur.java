package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.dto.SoldeDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OeAGENTAbsences Date de création : (05/09/11 11:31:37)
 * 
 */
public class OeAGENTAbsencesCompteur extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private AgentNW agentCourant;
	private Integer soldeCourantMinute;

	private String[] LB_TYPE_ABSENCE;
	private ArrayList<EnumTypeAbsence> listeTypeAbsence;
	private EnumTypeAbsence typeAbsenceCourant;

	private String[] LB_MOTIF;

	public String ACTION_CREATION = "Alimenter le compteur";

	/**
	 * Constructeur du process OeAGENTAbsences. Date de création : (05/09/11
	 * 11:39:24)
	 * 
	 */
	public OeAGENTAbsencesCompteur() {
		super();
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (05/09/11 11:39:24)
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

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseCompteurAgent(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseListeDeroulante() {

		// Si liste Type absence vide alors affectation
		if (getListeTypeAbsence() == null || getListeTypeAbsence().size() == 0) {
			setListeTypeAbsence(EnumTypeAbsence.getValues());

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EnumTypeAbsence> list = getListeTypeAbsence().listIterator(); list.hasNext();) {
				EnumTypeAbsence type = (EnumTypeAbsence) list.next();
				String ligne[] = { type.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_ABSENCE(aFormat.getListeFormatee(false));
		}
	}

	private void initialiseCompteurAgent(HttpServletRequest request) {

	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AFFICHER
			if (testerParametre(request, getNOM_PB_AFFICHER())) {
				return performPB_AFFICHER(request);
			}
			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
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
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (29/09/11 10:03:37)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTAbsencesCompteur.jsp";
	}

	public String getNomEcran() {
		return "ECR-AG-ABS-CPTEUR";
	}

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	private String[] getLB_TYPE_ABSENCE() {
		if (LB_TYPE_ABSENCE == null)
			LB_TYPE_ABSENCE = initialiseLazyLB();
		return LB_TYPE_ABSENCE;
	}

	private void setLB_TYPE_ABSENCE(String[] newLB_TYPE_ABSENCE) {
		LB_TYPE_ABSENCE = newLB_TYPE_ABSENCE;
	}

	public String getNOM_LB_TYPE_ABSENCE() {
		return "NOM_LB_TYPE_ABSENCE";
	}

	public String getNOM_LB_TYPE_ABSENCE_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE() {
		return getLB_TYPE_ABSENCE();
	}

	public String getVAL_LB_TYPE_ABSENCE_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_SELECT());
	}

	public ArrayList<EnumTypeAbsence> getListeTypeAbsence() {
		return listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<EnumTypeAbsence> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public String getNOM_PB_AFFICHER() {
		return "NOM_PB_AFFICHER";
	}

	public boolean performPB_AFFICHER(HttpServletRequest request) throws Exception {
		setTypeAbsenceCourant(null);

		// Recuperation type absence
		EnumTypeAbsence typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_SELECT()) : -1);
		if (indiceTypeAbsence >= 0) {
			typeAbsence = (EnumTypeAbsence) getListeTypeAbsence().get(indiceTypeAbsence);
			setTypeAbsenceCourant(typeAbsence);
		}

		afficheSolde(getTypeAbsenceCourant());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		return true;
	}

	private void afficheSolde(EnumTypeAbsence typeAbsenceCourant) {
		viderZoneSaisie();

		// Solde depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		SoldeDto soldeGlobal = consuAbs.getSoldeAgent(getAgentCourant().getIdAgent());

		switch (typeAbsenceCourant) {
			case CONGE:
				addZone(getNOM_ST_SOLDE(), soldeGlobal.getSoldeCongeAnnee().toString() + " j");
				setSoldeCourantMinute((int) (soldeGlobal.getSoldeCongeAnnee() * 24));
				break;
			case REPOS_COMP:

				break;
			case RECUP:
				int soldeRecup = soldeGlobal.getSoldeRecup().intValue();
				String soldeRecupHeure = (soldeRecup / 60) == 0 ? "" : soldeRecup / 60 + "h ";
				String soldeRecupMinute = soldeRecup % 60 + "m";
				addZone(getNOM_ST_SOLDE(), soldeRecupHeure + soldeRecupMinute);
				setSoldeCourantMinute(soldeRecup);
				break;
			case ASA:

				break;
			case AUTRE:

				break;
			case MALADIE:

				break;
		}

	}

	private void viderZoneSaisie() {
		addZone(getNOM_ST_SOLDE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_HEURE_AJOUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_MIN_AJOUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_HEURE_RETRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_MIN_RETRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
	}

	public EnumTypeAbsence getTypeAbsenceCourant() {
		return typeAbsenceCourant;
	}

	public void setTypeAbsenceCourant(EnumTypeAbsence typeAbsenceCourant) {
		this.typeAbsenceCourant = typeAbsenceCourant;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// vérification de la validité du formulaire
		if (!performControlerChamps(request))
			return false;

		// on recupere la saisie
		String dureeHeure = null;
		String dureeMin = null;
		boolean ajout = false;
		if (!getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
				|| !getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)) {
			dureeHeure = getVAL_ST_DUREE_HEURE_AJOUT();
			dureeMin = getVAL_ST_DUREE_MIN_AJOUT();
			ajout = true;
		}
		if (!getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
				|| !getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
			dureeHeure = getVAL_ST_DUREE_HEURE_RETRAIT();
			dureeMin = getVAL_ST_DUREE_MIN_RETRAIT();
			ajout = false;
		}

		// verifier nouveau solde pas négatif
		int dureeTotaleSaisie = (Integer.valueOf(dureeHeure) * 60) + (Integer.valueOf(dureeMin));
		int ancienSolde = getSoldeCourantMinute();
		if (ajout) {
			// cas de l'ajout
			if (ancienSolde + dureeTotaleSaisie < 0) {
				// "ERR801",
				// "Le nouveau solde du compteur ne peut être négatif."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR801"));
				return false;
			}
		} else {
			// cas du retrait
			if (ancienSolde - dureeTotaleSaisie < 0) {
				// "ERR801",
				// "Le nouveau solde du compteur ne peut être négatif."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR801"));
				return false;
			}
		}
		
		//TODO on sauvegarde les données
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// TODO afficher message opération ok

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	private boolean performControlerChamps(HttpServletRequest request) {
		// motif obligatoire
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT())
				: -1);
		if (indiceMotif < 1) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}

		// durée obligatoire (ajout ou retrait)
		if (getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
				&& getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)
				&& getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
				&& getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée"));
			return false;
		}

		// pas 2 durées de saisie
		boolean ajoutSaisie = false;
		if (!getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
				&& !getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)) {
			ajoutSaisie = true;
		}

		boolean retraitSaisie = false;
		if (!getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
				&& !getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
			retraitSaisie = true;
		}

		if (ajoutSaisie && retraitSaisie) {
			// "ERR800", "Seul un des deux champs durée doit être renseigné."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR800"));
			return false;
		}

		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setTypeAbsenceCourant(null);
		viderZoneSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_ST_SOLDE() {
		return "NOM_ST_SOLDE";
	}

	public String getVAL_ST_SOLDE() {
		return getZone(getNOM_ST_SOLDE());
	}

	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	public String getNOM_ST_DUREE_HEURE_AJOUT() {
		return "NOM_ST_DUREE_HEURE_AJOUT";
	}

	public String getVAL_ST_DUREE_HEURE_AJOUT() {
		return getZone(getNOM_ST_DUREE_HEURE_AJOUT());
	}

	public String getNOM_ST_DUREE_MIN_AJOUT() {
		return "NOM_ST_DUREE_MIN_AJOUT";
	}

	public String getVAL_ST_DUREE_MIN_AJOUT() {
		return getZone(getNOM_ST_DUREE_MIN_AJOUT());
	}

	public String getNOM_ST_DUREE_HEURE_RETRAIT() {
		return "NOM_ST_DUREE_HEURE_RETRAIT";
	}

	public String getVAL_ST_DUREE_HEURE_RETRAIT() {
		return getZone(getNOM_ST_DUREE_HEURE_RETRAIT());
	}

	public String getNOM_ST_DUREE_MIN_RETRAIT() {
		return "NOM_ST_DUREE_MIN_RETRAIT";
	}

	public String getVAL_ST_DUREE_MIN_RETRAIT() {
		return getZone(getNOM_ST_DUREE_MIN_RETRAIT());
	}

	public Integer getSoldeCourantMinute() {
		return soldeCourantMinute;
	}

	public void setSoldeCourantMinute(Integer soldeCourantMinute) {
		this.soldeCourantMinute = soldeCourantMinute;
	}
}
