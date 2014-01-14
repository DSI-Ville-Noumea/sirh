package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.abs.dto.DemandeDto;
import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OeAGENTAbsences Date de création : (05/09/11 11:31:37)
 * 
 */
public class OeAGENTAbsencesHisto extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private AgentNW agentCourant;
	private ArrayList<DemandeDto> listeDemandeNonPrises;
	private ArrayList<DemandeDto> listeDemandeEnCours;
	private ArrayList<DemandeDto> listeToutesDemandes;

	public OeAGENTAbsencesHisto() {
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

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseHistoAgentNonPrises(request);
				initialiseHistoAgentEnCours(request);
				initialiseHistoAgentToutes(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseHistoAgentToutes(HttpServletRequest request) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// Recherche des absences non prises de l'agent
		ArrayList<DemandeDto> a = (ArrayList<DemandeDto>) consuAbs.getListeDemandesAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()), "TOUTES", null, null, null, null, null);
		setListeToutesDemandes(a);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		for (int i = 0; i < getListeToutesDemandes().size(); i++) {
			DemandeDto dto = getListeToutesDemandes().get(i);

			addZone(getNOM_ST_TYPE_DEMANDE_TT(i), EnumTypeAbsence.getValueEnumTypeAbsence(dto.getIdTypeDemande()));
			addZone(getNOM_ST_DATE_DEBUT_TT(i),
					dto.getDateDebut() == null ? "&nbsp;" : sdfDate.format(dto.getDateDebut()));
			addZone(getNOM_ST_HEURE_DEBUT_TT(i),
					dto.getDateDebut() == null ? "&nbsp;" : sdfHeure.format(dto.getDateDebut()));
			addZone(getNOM_ST_DUREE_TT(i), getHeureMinute(dto.getDuree()));
			addZone(getNOM_ST_DATE_DEMANDE_TT(i),
					dto.getDateDemande() == null ? "&nbsp;" : sdfDate.format(dto.getDateDemande()));
			addZone(getNOM_ST_ETAT_DEMANDE_TT(i), EnumEtatAbsence.getValueEnumEtatAbsence(dto.getIdRefEtat()));

		}
	}

	private void initialiseHistoAgentEnCours(HttpServletRequest request) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// Recherche des absences non prises de l'agent
		ArrayList<DemandeDto> a = (ArrayList<DemandeDto>) consuAbs.getListeDemandesAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()), "EN_COURS", null, null, null, null, null);
		setListeDemandeEnCours(a);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		for (int i = 0; i < getListeDemandeEnCours().size(); i++) {
			DemandeDto dto = getListeDemandeEnCours().get(i);

			addZone(getNOM_ST_TYPE_DEMANDE_EC(i), EnumTypeAbsence.getValueEnumTypeAbsence(dto.getIdTypeDemande()));
			addZone(getNOM_ST_DATE_DEBUT_EC(i),
					dto.getDateDebut() == null ? "&nbsp;" : sdfDate.format(dto.getDateDebut()));
			addZone(getNOM_ST_HEURE_DEBUT_EC(i),
					dto.getDateDebut() == null ? "&nbsp;" : sdfHeure.format(dto.getDateDebut()));
			addZone(getNOM_ST_DUREE_EC(i), getHeureMinute(dto.getDuree()));
			addZone(getNOM_ST_DATE_DEMANDE_EC(i),
					dto.getDateDemande() == null ? "&nbsp;" : sdfDate.format(dto.getDateDemande()));
			addZone(getNOM_ST_ETAT_DEMANDE_EC(i), EnumEtatAbsence.getValueEnumEtatAbsence(dto.getIdRefEtat()));

		}
	}

	private void initialiseHistoAgentNonPrises(HttpServletRequest request) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// Recherche des absences non prises de l'agent
		ArrayList<DemandeDto> a = (ArrayList<DemandeDto>) consuAbs.getListeDemandesAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()), "NON_PRISES", null, null, null, null, null);
		setListeDemandeNonPrises(a);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		for (int i = 0; i < getListeDemandeNonPrises().size(); i++) {
			DemandeDto dto = getListeDemandeNonPrises().get(i);

			addZone(getNOM_ST_TYPE_DEMANDE_NP(i), EnumTypeAbsence.getValueEnumTypeAbsence(dto.getIdTypeDemande()));
			addZone(getNOM_ST_DATE_DEBUT_NP(i),
					dto.getDateDebut() == null ? "&nbsp;" : sdfDate.format(dto.getDateDebut()));
			addZone(getNOM_ST_HEURE_DEBUT_NP(i),
					dto.getDateDebut() == null ? "&nbsp;" : sdfHeure.format(dto.getDateDebut()));
			addZone(getNOM_ST_DUREE_NP(i), getHeureMinute(dto.getDuree()));
			addZone(getNOM_ST_DATE_DEMANDE_NP(i),
					dto.getDateDemande() == null ? "&nbsp;" : sdfDate.format(dto.getDateDemande()));
			addZone(getNOM_ST_ETAT_DEMANDE_NP(i), EnumEtatAbsence.getValueEnumEtatAbsence(dto.getIdRefEtat()));

		}

	}

	private static String getHeureMinute(int nombreMinute) {
		int heure = nombreMinute / 60;
		int minute = nombreMinute % 60;
		String res = "";
		if (heure > 0)
			res += heure + "h";
		if (minute > 0)
			res += minute + "m";

		return res;
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

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
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
		return "OeAGENTAbsencesHisto.jsp";
	}

	public String getNomEcran() {
		return "ECR-AG-ABS-HISTO";
	}

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_ST_TYPE_DEMANDE_NP(int i) {
		return "NOM_ST_TYPE_DEMANDE_NP" + i;
	}

	public String getVAL_ST_TYPE_DEMANDE_NP(int i) {
		return getZone(getNOM_ST_TYPE_DEMANDE_NP(i));
	}

	public String getNOM_ST_DATE_DEBUT_NP(int i) {
		return "NOM_ST_DATE_DEBUT_NP" + i;
	}

	public String getVAL_ST_DATE_DEBUT_NP(int i) {
		return getZone(getNOM_ST_DATE_DEBUT_NP(i));
	}

	public String getNOM_ST_HEURE_DEBUT_NP(int i) {
		return "NOM_ST_HEURE_DEBUT_NP" + i;
	}

	public String getVAL_ST_HEURE_DEBUT_NP(int i) {
		return getZone(getNOM_ST_HEURE_DEBUT_NP(i));
	}

	public String getNOM_ST_DUREE_NP(int i) {
		return "NOM_ST_DUREE_NP" + i;
	}

	public String getVAL_ST_DUREE_NP(int i) {
		return getZone(getNOM_ST_DUREE_NP(i));
	}

	public String getNOM_ST_DATE_DEMANDE_NP(int i) {
		return "NOM_ST_DATE_DEMANDE_NP" + i;
	}

	public String getVAL_ST_DATE_DEMANDE_NP(int i) {
		return getZone(getNOM_ST_DATE_DEMANDE_NP(i));
	}

	public String getNOM_ST_ETAT_DEMANDE_NP(int i) {
		return "NOM_ST_ETAT_DEMANDE_NP" + i;
	}

	public String getVAL_ST_ETAT_DEMANDE_NP(int i) {
		return getZone(getNOM_ST_ETAT_DEMANDE_NP(i));
	}

	public ArrayList<DemandeDto> getListeDemandeNonPrises() {
		if (listeDemandeNonPrises == null)
			return new ArrayList<DemandeDto>();
		return listeDemandeNonPrises;
	}

	public void setListeDemandeNonPrises(ArrayList<DemandeDto> listeDemandeNonPrises) {
		this.listeDemandeNonPrises = listeDemandeNonPrises;
	}

	public ArrayList<DemandeDto> getListeDemandeEnCours() {
		if (listeDemandeEnCours == null)
			return new ArrayList<DemandeDto>();
		return listeDemandeEnCours;
	}

	public void setListeDemandeEnCours(ArrayList<DemandeDto> listeDemandeEnCours) {
		this.listeDemandeEnCours = listeDemandeEnCours;
	}

	public String getNOM_ST_TYPE_DEMANDE_EC(int i) {
		return "NOM_ST_TYPE_DEMANDE_EC" + i;
	}

	public String getVAL_ST_TYPE_DEMANDE_EC(int i) {
		return getZone(getNOM_ST_TYPE_DEMANDE_EC(i));
	}

	public String getNOM_ST_DATE_DEBUT_EC(int i) {
		return "NOM_ST_DATE_DEBUT_EC" + i;
	}

	public String getVAL_ST_DATE_DEBUT_EC(int i) {
		return getZone(getNOM_ST_DATE_DEBUT_EC(i));
	}

	public String getNOM_ST_HEURE_DEBUT_EC(int i) {
		return "NOM_ST_HEURE_DEBUT_EC" + i;
	}

	public String getVAL_ST_HEURE_DEBUT_EC(int i) {
		return getZone(getNOM_ST_HEURE_DEBUT_EC(i));
	}

	public String getNOM_ST_DUREE_EC(int i) {
		return "NOM_ST_DUREE_EC" + i;
	}

	public String getVAL_ST_DUREE_EC(int i) {
		return getZone(getNOM_ST_DUREE_EC(i));
	}

	public String getNOM_ST_DATE_DEMANDE_EC(int i) {
		return "NOM_ST_DATE_DEMANDE_EC" + i;
	}

	public String getVAL_ST_DATE_DEMANDE_EC(int i) {
		return getZone(getNOM_ST_DATE_DEMANDE_EC(i));
	}

	public String getNOM_ST_ETAT_DEMANDE_EC(int i) {
		return "NOM_ST_ETAT_DEMANDE_EC" + i;
	}

	public String getVAL_ST_ETAT_DEMANDE_EC(int i) {
		return getZone(getNOM_ST_ETAT_DEMANDE_EC(i));
	}

	public ArrayList<DemandeDto> getListeToutesDemandes() {
		if (listeToutesDemandes == null)
			return new ArrayList<DemandeDto>();
		return listeToutesDemandes;
	}

	public void setListeToutesDemandes(ArrayList<DemandeDto> listeToutesDemandes) {
		this.listeToutesDemandes = listeToutesDemandes;
	}

	public String getNOM_ST_TYPE_DEMANDE_TT(int i) {
		return "NOM_ST_TYPE_DEMANDE_TT" + i;
	}

	public String getVAL_ST_TYPE_DEMANDE_TT(int i) {
		return getZone(getNOM_ST_TYPE_DEMANDE_TT(i));
	}

	public String getNOM_ST_DATE_DEBUT_TT(int i) {
		return "NOM_ST_DATE_DEBUT_TT" + i;
	}

	public String getVAL_ST_DATE_DEBUT_TT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT_TT(i));
	}

	public String getNOM_ST_HEURE_DEBUT_TT(int i) {
		return "NOM_ST_HEURE_DEBUT_TT" + i;
	}

	public String getVAL_ST_HEURE_DEBUT_TT(int i) {
		return getZone(getNOM_ST_HEURE_DEBUT_TT(i));
	}

	public String getNOM_ST_DUREE_TT(int i) {
		return "NOM_ST_DUREE_TT" + i;
	}

	public String getVAL_ST_DUREE_TT(int i) {
		return getZone(getNOM_ST_DUREE_TT(i));
	}

	public String getNOM_ST_DATE_DEMANDE_TT(int i) {
		return "NOM_ST_DATE_DEMANDE_TT" + i;
	}

	public String getVAL_ST_DATE_DEMANDE_TT(int i) {
		return getZone(getNOM_ST_DATE_DEMANDE_TT(i));
	}

	public String getNOM_ST_ETAT_DEMANDE_TT(int i) {
		return "NOM_ST_ETAT_DEMANDE_TT" + i;
	}

	public String getVAL_ST_ETAT_DEMANDE_TT(int i) {
		return getZone(getNOM_ST_ETAT_DEMANDE_TT(i));
	}
}
