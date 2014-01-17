package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.abs.dto.DemandeDto;
import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
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
 * Process OeAGENTAbsences Date de cr�ation : (05/09/11 11:31:37)
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

	private String[] LB_TYPE_ABSENCE_NP;
	private String[] LB_TYPE_ABSENCE_EC;
	private String[] LB_TYPE_ABSENCE_TT;
	private ArrayList<EnumTypeAbsence> listeTypeAbsence;
	private String[] LB_ETAT_ABSENCE_NP;
	private String[] LB_ETAT_ABSENCE_EC;
	private String[] LB_ETAT_ABSENCE_TT;
	private ArrayList<EnumEtatAbsence> listeEtatAbsenceNP;
	private ArrayList<EnumEtatAbsence> listeEtatAbsenceEC;
	private ArrayList<EnumEtatAbsence> listeEtatAbsenceTT;

	public OeAGENTAbsencesHisto() {
		super();
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (05/09/11 11:39:24)
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

		// Initialisation des listes d�roulantes
		initialiseListeDeroulante();

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseHistoAgentNonPrises(request, null, null, null, null, null);
				initialiseHistoAgentEnCours(request, null, null, null, null, null);
				initialiseHistoAgentToutes(request, null, null, null, null, null);
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
			setLB_TYPE_ABSENCE_NP(aFormat.getListeFormatee(true));
			setLB_TYPE_ABSENCE_EC(aFormat.getListeFormatee(true));
			setLB_TYPE_ABSENCE_TT(aFormat.getListeFormatee(true));
		}

		// Si liste Etat absence vide alors affectation
		if (getListeEtatAbsenceTT() == null || getListeEtatAbsenceTT().size() == 0) {
			setListeEtatAbsenceTT(EnumEtatAbsence.getValues());

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EnumEtatAbsence> list = getListeEtatAbsenceTT().listIterator(); list.hasNext();) {
				EnumEtatAbsence etat = (EnumEtatAbsence) list.next();
				String ligne[] = { etat.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT_ABSENCE_TT(aFormat.getListeFormatee(true));
		}

		// Si liste Etat absence vide alors affectation
		if (getListeEtatAbsenceNP() == null || getListeEtatAbsenceNP().size() == 0) {
			ArrayList<EnumEtatAbsence> tousEtats = EnumEtatAbsence.getValues();
			tousEtats.remove(EnumEtatAbsence.PRISE);
			setListeEtatAbsenceNP(tousEtats);

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EnumEtatAbsence> list = getListeEtatAbsenceNP().listIterator(); list.hasNext();) {
				EnumEtatAbsence etat = (EnumEtatAbsence) list.next();
				String ligne[] = { etat.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT_ABSENCE_NP(aFormat.getListeFormatee(true));
		}

		// Si liste Etat absence vide alors affectation
		if (getListeEtatAbsenceEC() == null || getListeEtatAbsenceEC().size() == 0) {
			ArrayList<EnumEtatAbsence> etats = new ArrayList<EnumEtatAbsence>();
			etats.add(EnumEtatAbsence.SAISIE);
			etats.add(EnumEtatAbsence.VISEE_FAV);
			etats.add(EnumEtatAbsence.VISEE_DEFAV);
			etats.add(EnumEtatAbsence.APPROUVE);
			setListeEtatAbsenceEC(etats);

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EnumEtatAbsence> list = getListeEtatAbsenceEC().listIterator(); list.hasNext();) {
				EnumEtatAbsence etat = (EnumEtatAbsence) list.next();
				String ligne[] = { etat.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT_ABSENCE_EC(aFormat.getListeFormatee(true));
		}
	}

	private void initialiseHistoAgentToutes(HttpServletRequest request, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefTypeAbsence) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// Recherche des absences non prises de l'agent
		ArrayList<DemandeDto> a = (ArrayList<DemandeDto>) consuAbs.getListeDemandesAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()), "TOUTES", dateDebut, dateFin, dateDemande, idRefEtat,
				idRefTypeAbsence);
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

	private void initialiseHistoAgentEnCours(HttpServletRequest request, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefTypeAbsence) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// Recherche des absences non prises de l'agent
		ArrayList<DemandeDto> a = (ArrayList<DemandeDto>) consuAbs.getListeDemandesAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()), "EN_COURS", dateDebut, dateFin, dateDemande,
				idRefEtat, idRefTypeAbsence);
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

	private void initialiseHistoAgentNonPrises(HttpServletRequest request, String dateDebut, String dateFin,
			String dateDemande, Integer idRefEtat, Integer idRefTypeAbsence) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();

		// Recherche des absences non prises de l'agent
		ArrayList<DemandeDto> a = (ArrayList<DemandeDto>) consuAbs.getListeDemandesAgent(
				Integer.valueOf(getAgentCourant().getIdAgent()), "NON_PRISES", dateDebut, dateFin, dateDemande,
				idRefEtat, idRefTypeAbsence);
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
	 * cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_FILTRER_NP
			if (testerParametre(request, getNOM_PB_FILTRER_NP())) {
				return performPB_FILTRER_NP(request);
			}
			// Si clic sur le bouton PB_FILTRER_EC
			if (testerParametre(request, getNOM_PB_FILTRER_EC())) {
				return performPB_FILTRER_EC(request);
			}
			// Si clic sur le bouton PB_FILTRER_TT
			if (testerParametre(request, getNOM_PB_FILTRER_TT())) {
				return performPB_FILTRER_TT(request);
			}

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}
		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (29/09/11 10:03:37)
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

	private String[] getLB_TYPE_ABSENCE_NP() {
		if (LB_TYPE_ABSENCE_NP == null)
			LB_TYPE_ABSENCE_NP = initialiseLazyLB();
		return LB_TYPE_ABSENCE_NP;
	}

	private void setLB_TYPE_ABSENCE_NP(String[] newLB_TYPE_ABSENCE_NP) {
		LB_TYPE_ABSENCE_NP = newLB_TYPE_ABSENCE_NP;
	}

	public String getNOM_LB_TYPE_ABSENCE_NP() {
		return "NOM_LB_TYPE_ABSENCE_NP";
	}

	public String getNOM_LB_TYPE_ABSENCE_NP_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_NP_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE_NP() {
		return getLB_TYPE_ABSENCE_NP();
	}

	public String getVAL_LB_TYPE_ABSENCE_NP_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_NP_SELECT());
	}

	public String getNOM_PB_FILTRER_EC() {
		return "NOM_PB_FILTRER_EC";
	}

	public boolean performPB_FILTRER_EC(HttpServletRequest request) throws Exception {

		// Recuperation type absence
		EnumTypeAbsence typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_EC_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_EC_SELECT()) : -1);
		if (indiceTypeAbsence > 0) {
			typeAbsence = (EnumTypeAbsence) getListeTypeAbsence().get(indiceTypeAbsence - 1);
		}

		// Recuperation etat absence
		EnumEtatAbsence etatAbsence = null;
		int indiceEtatAbsence = (Services.estNumerique(getVAL_LB_ETAT_ABSENCE_EC_SELECT()) ? Integer
				.parseInt(getVAL_LB_ETAT_ABSENCE_EC_SELECT()) : -1);
		if (indiceEtatAbsence > 0) {
			etatAbsence = (EnumEtatAbsence) getListeEtatAbsenceEC().get(indiceEtatAbsence - 1);
		}

		// Recuperation des dates
		String dateDebut = null;
		if (!getVAL_ST_DATE_DEB_EC().equals(Const.CHAINE_VIDE)) {
			dateDebut = Services.convertitDate(getVAL_ST_DATE_DEB_EC(), "dd/MM/yyyy", "yyyyMMdd");
		}
		String dateFin = null;
		if (!getVAL_ST_DATE_FIN_EC().equals(Const.CHAINE_VIDE)) {
			dateFin = Services.convertitDate(getVAL_ST_DATE_FIN_EC(), "dd/MM/yyyy", "yyyyMMdd");
		}
		String dateDemande = null;
		if (!getVAL_ST_DATE_DEMANDE_EC().equals(Const.CHAINE_VIDE)) {
			dateDemande = Services.convertitDate(getVAL_ST_DATE_DEMANDE_EC(), "dd/MM/yyyy", "yyyyMMdd");
		}

		initialiseHistoAgentEnCours(request, dateDebut, dateFin, dateDemande,
				etatAbsence == null ? null : etatAbsence.getCode(), typeAbsence == null ? null : typeAbsence.getCode());
		return true;
	}

	public String getNOM_PB_FILTRER_NP() {
		return "NOM_PB_FILTRER_NP";
	}

	public boolean performPB_FILTRER_NP(HttpServletRequest request) throws Exception {

		// Recuperation type absence
		EnumTypeAbsence typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_NP_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_NP_SELECT()) : -1);
		if (indiceTypeAbsence > 0) {
			typeAbsence = (EnumTypeAbsence) getListeTypeAbsence().get(indiceTypeAbsence - 1);
		}

		// Recuperation etat absence
		EnumEtatAbsence etatAbsence = null;
		int indiceEtatAbsence = (Services.estNumerique(getVAL_LB_ETAT_ABSENCE_NP_SELECT()) ? Integer
				.parseInt(getVAL_LB_ETAT_ABSENCE_NP_SELECT()) : -1);
		if (indiceEtatAbsence > 0) {
			etatAbsence = (EnumEtatAbsence) getListeEtatAbsenceNP().get(indiceEtatAbsence - 1);
		}

		// Recuperation des dates
		String dateDebut = null;
		if (!getVAL_ST_DATE_DEB_NP().equals(Const.CHAINE_VIDE)) {
			dateDebut = Services.convertitDate(getVAL_ST_DATE_DEB_NP(), "dd/MM/yyyy", "yyyyMMdd");
		}
		String dateFin = null;
		if (!getVAL_ST_DATE_FIN_NP().equals(Const.CHAINE_VIDE)) {
			dateFin = Services.convertitDate(getVAL_ST_DATE_FIN_NP(), "dd/MM/yyyy", "yyyyMMdd");
		}
		String dateDemande = null;
		if (!getVAL_ST_DATE_DEMANDE_NP().equals(Const.CHAINE_VIDE)) {
			dateDemande = Services.convertitDate(getVAL_ST_DATE_DEMANDE_NP(), "dd/MM/yyyy", "yyyyMMdd");
		}

		initialiseHistoAgentNonPrises(request, dateDebut, dateFin, dateDemande, etatAbsence == null ? null
				: etatAbsence.getCode(), typeAbsence == null ? null : typeAbsence.getCode());
		return true;
	}

	public String getNOM_PB_FILTRER_TT() {
		return "NOM_PB_FILTRER_TT";
	}

	public boolean performPB_FILTRER_TT(HttpServletRequest request) throws Exception {

		// Recuperation type absence
		EnumTypeAbsence typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_TT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_TT_SELECT()) : -1);
		if (indiceTypeAbsence > 0) {
			typeAbsence = (EnumTypeAbsence) getListeTypeAbsence().get(indiceTypeAbsence - 1);
		}

		// Recuperation etat absence
		EnumEtatAbsence etatAbsence = null;
		int indiceEtatAbsence = (Services.estNumerique(getVAL_LB_ETAT_ABSENCE_TT_SELECT()) ? Integer
				.parseInt(getVAL_LB_ETAT_ABSENCE_TT_SELECT()) : -1);
		if (indiceEtatAbsence > 0) {
			etatAbsence = (EnumEtatAbsence) getListeEtatAbsenceTT().get(indiceEtatAbsence - 1);
		}

		// Recuperation des dates
		String dateDebut = null;
		if (!getVAL_ST_DATE_DEB_TT().equals(Const.CHAINE_VIDE)) {
			dateDebut = Services.convertitDate(getVAL_ST_DATE_DEB_TT(), "dd/MM/yyyy", "yyyyMMdd");
		}
		String dateFin = null;
		if (!getVAL_ST_DATE_FIN_TT().equals(Const.CHAINE_VIDE)) {
			dateFin = Services.convertitDate(getVAL_ST_DATE_FIN_TT(), "dd/MM/yyyy", "yyyyMMdd");
		}
		String dateDemande = null;
		if (!getVAL_ST_DATE_DEMANDE_TT().equals(Const.CHAINE_VIDE)) {
			dateDemande = Services.convertitDate(getVAL_ST_DATE_DEMANDE_TT(), "dd/MM/yyyy", "yyyyMMdd");
		}

		initialiseHistoAgentToutes(request, dateDebut, dateFin, dateDemande,
				etatAbsence == null ? null : etatAbsence.getCode(), typeAbsence == null ? null : typeAbsence.getCode());
		return true;
	}

	public ArrayList<EnumTypeAbsence> getListeTypeAbsence() {
		return listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<EnumTypeAbsence> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	private String[] getLB_ETAT_ABSENCE_NP() {
		if (LB_ETAT_ABSENCE_NP == null)
			LB_ETAT_ABSENCE_NP = initialiseLazyLB();
		return LB_ETAT_ABSENCE_NP;
	}

	private void setLB_ETAT_ABSENCE_NP(String[] newLB_ETAT_ABSENCE_NP) {
		LB_ETAT_ABSENCE_NP = newLB_ETAT_ABSENCE_NP;
	}

	public String getNOM_LB_ETAT_ABSENCE_NP() {
		return "NOM_LB_ETAT_ABSENCE_NP";
	}

	public String getNOM_LB_ETAT_ABSENCE_NP_SELECT() {
		return "NOM_LB_ETAT_ABSENCE_NP_SELECT";
	}

	public String[] getVAL_LB_ETAT_ABSENCE_NP() {
		return getLB_ETAT_ABSENCE_NP();
	}

	public String getVAL_LB_ETAT_ABSENCE_NP_SELECT() {
		return getZone(getNOM_LB_ETAT_ABSENCE_NP_SELECT());
	}

	private String[] getLB_ETAT_ABSENCE_EC() {
		if (LB_ETAT_ABSENCE_EC == null)
			LB_ETAT_ABSENCE_EC = initialiseLazyLB();
		return LB_ETAT_ABSENCE_EC;
	}

	private void setLB_ETAT_ABSENCE_EC(String[] newLB_ETAT_ABSENCE_EC) {
		LB_ETAT_ABSENCE_EC = newLB_ETAT_ABSENCE_EC;
	}

	public String getNOM_LB_ETAT_ABSENCE_EC() {
		return "NOM_LB_ETAT_ABSENCE_EC";
	}

	public String getNOM_LB_ETAT_ABSENCE_EC_SELECT() {
		return "NOM_LB_ETAT_ABSENCE_EC_SELECT";
	}

	public String[] getVAL_LB_ETAT_ABSENCE_EC() {
		return getLB_ETAT_ABSENCE_EC();
	}

	public String getVAL_LB_ETAT_ABSENCE_EC_SELECT() {
		return getZone(getNOM_LB_ETAT_ABSENCE_EC_SELECT());
	}

	private String[] getLB_ETAT_ABSENCE_TT() {
		if (LB_ETAT_ABSENCE_TT == null)
			LB_ETAT_ABSENCE_TT = initialiseLazyLB();
		return LB_ETAT_ABSENCE_TT;
	}

	private void setLB_ETAT_ABSENCE_TT(String[] newLB_ETAT_ABSENCE_TT) {
		LB_ETAT_ABSENCE_TT = newLB_ETAT_ABSENCE_TT;
	}

	public String getNOM_LB_ETAT_ABSENCE_TT() {
		return "NOM_LB_ETAT_ABSENCE_TT";
	}

	public String getNOM_LB_ETAT_ABSENCE_TT_SELECT() {
		return "NOM_LB_ETAT_ABSENCE_TT_SELECT";
	}

	public String[] getVAL_LB_ETAT_ABSENCE_TT() {
		return getLB_ETAT_ABSENCE_TT();
	}

	public String getVAL_LB_ETAT_ABSENCE_TT_SELECT() {
		return getZone(getNOM_LB_ETAT_ABSENCE_TT_SELECT());
	}

	private String[] getLB_TYPE_ABSENCE_EC() {
		if (LB_TYPE_ABSENCE_EC == null)
			LB_TYPE_ABSENCE_EC = initialiseLazyLB();
		return LB_TYPE_ABSENCE_EC;
	}

	private void setLB_TYPE_ABSENCE_EC(String[] newLB_TYPE_ABSENCE_EC) {
		LB_TYPE_ABSENCE_EC = newLB_TYPE_ABSENCE_EC;
	}

	public String getNOM_LB_TYPE_ABSENCE_EC() {
		return "NOM_LB_TYPE_ABSENCE_EC";
	}

	public String getNOM_LB_TYPE_ABSENCE_EC_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_EC_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE_EC() {
		return getLB_TYPE_ABSENCE_EC();
	}

	public String getVAL_LB_TYPE_ABSENCE_EC_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_EC_SELECT());
	}

	private String[] getLB_TYPE_ABSENCE_TT() {
		if (LB_TYPE_ABSENCE_TT == null)
			LB_TYPE_ABSENCE_TT = initialiseLazyLB();
		return LB_TYPE_ABSENCE_TT;
	}

	private void setLB_TYPE_ABSENCE_TT(String[] newLB_TYPE_ABSENCE_TT) {
		LB_TYPE_ABSENCE_TT = newLB_TYPE_ABSENCE_TT;
	}

	public String getNOM_LB_TYPE_ABSENCE_TT() {
		return "NOM_LB_TYPE_ABSENCE_TT";
	}

	public String getNOM_LB_TYPE_ABSENCE_TT_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_TT_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE_TT() {
		return getLB_TYPE_ABSENCE_TT();
	}

	public String getVAL_LB_TYPE_ABSENCE_TT_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_TT_SELECT());
	}

	public ArrayList<EnumEtatAbsence> getListeEtatAbsenceNP() {
		return listeEtatAbsenceNP;
	}

	public void setListeEtatAbsenceNP(ArrayList<EnumEtatAbsence> listeEtatAbsenceNP) {
		this.listeEtatAbsenceNP = listeEtatAbsenceNP;
	}

	public ArrayList<EnumEtatAbsence> getListeEtatAbsenceEC() {
		return listeEtatAbsenceEC;
	}

	public void setListeEtatAbsenceEC(ArrayList<EnumEtatAbsence> listeEtatAbsenceEC) {
		this.listeEtatAbsenceEC = listeEtatAbsenceEC;
	}

	public ArrayList<EnumEtatAbsence> getListeEtatAbsenceTT() {
		return listeEtatAbsenceTT;
	}

	public void setListeEtatAbsenceTT(ArrayList<EnumEtatAbsence> listeEtatAbsenceTT) {
		this.listeEtatAbsenceTT = listeEtatAbsenceTT;
	}

	public String getNOM_ST_DATE_DEB_NP() {
		return "NOM_ST_DATE_DEB_NP";
	}

	public String getVAL_ST_DATE_DEB_NP() {
		return getZone(getNOM_ST_DATE_DEB_NP());
	}

	public String getNOM_ST_DATE_FIN_NP() {
		return "NOM_ST_DATE_FIN_NP";
	}

	public String getVAL_ST_DATE_FIN_NP() {
		return getZone(getNOM_ST_DATE_FIN_NP());
	}

	public String getNOM_ST_DATE_DEMANDE_NP() {
		return "NOM_ST_DATE_DEMANDE_NP";
	}

	public String getVAL_ST_DATE_DEMANDE_NP() {
		return getZone(getNOM_ST_DATE_DEMANDE_NP());
	}

	public String getNOM_ST_DATE_DEB_EC() {
		return "NOM_ST_DATE_DEB_EC";
	}

	public String getVAL_ST_DATE_DEB_EC() {
		return getZone(getNOM_ST_DATE_DEB_EC());
	}

	public String getNOM_ST_DATE_FIN_EC() {
		return "NOM_ST_DATE_FIN_EC";
	}

	public String getVAL_ST_DATE_FIN_EC() {
		return getZone(getNOM_ST_DATE_FIN_EC());
	}

	public String getNOM_ST_DATE_DEMANDE_EC() {
		return "NOM_ST_DATE_DEMANDE_EC";
	}

	public String getVAL_ST_DATE_DEMANDE_EC() {
		return getZone(getNOM_ST_DATE_DEMANDE_EC());
	}

	public String getNOM_ST_DATE_DEB_TT() {
		return "NOM_ST_DATE_DEB_TT";
	}

	public String getVAL_ST_DATE_DEB_TT() {
		return getZone(getNOM_ST_DATE_DEB_TT());
	}

	public String getNOM_ST_DATE_FIN_TT() {
		return "NOM_ST_DATE_FIN_TT";
	}

	public String getVAL_ST_DATE_FIN_TT() {
		return getZone(getNOM_ST_DATE_FIN_TT());
	}

	public String getNOM_ST_DATE_DEMANDE_TT() {
		return "NOM_ST_DATE_DEMANDE_TT";
	}

	public String getVAL_ST_DATE_DEMANDE_TT() {
		return getZone(getNOM_ST_DATE_DEMANDE_TT());
	}
}