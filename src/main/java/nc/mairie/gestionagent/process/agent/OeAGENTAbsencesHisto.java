package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
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
public class OeAGENTAbsencesHisto extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private Agent agentCourant;
	private ArrayList<DemandeDto> listeDemandeNonPrises;
	private ArrayList<DemandeDto> listeDemandeEnCours;
	private ArrayList<DemandeDto> listeToutesDemandes;

	private String[] LB_TYPE_ABSENCE_NP;
	private String[] LB_TYPE_ABSENCE_EC;
	private String[] LB_TYPE_ABSENCE_TT;
	private ArrayList<TypeAbsenceDto> listeTypeAbsence;
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
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
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
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeTypeAbsence((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto(null));

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeAbsenceDto> list = getListeTypeAbsence().listIterator(); list.hasNext();) {
				TypeAbsenceDto type = (TypeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };

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
			etats.add(EnumEtatAbsence.VALIDEE);
			etats.add(EnumEtatAbsence.A_VALIDER);
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
				getAgentCourant().getIdAgent(), "TOUTES", dateDebut, dateFin, dateDemande, idRefEtat, idRefTypeAbsence);
		setListeToutesDemandes(a);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		for (int i = 0; i < getListeToutesDemandes().size(); i++) {
			DemandeDto dto = getListeToutesDemandes().get(i);
			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(dto.getIdTypeDemande());
			addZone(getNOM_ST_TYPE_DEMANDE_TT(i), getListeTypeAbsence().get(getListeTypeAbsence().indexOf(t))
					.getLibelle());

			String dateDebAff = dto.getDateDebut() == null ? "&nbsp;" : sdfDate.format(dto.getDateDebut());
			String dateFinAff = dto.getDateFin() == null ? "&nbsp;" : sdfDate.format(dto.getDateFin());
			if (dto.getTypeSaisi() != null && "minutes".equals(dto.getTypeSaisi().getUniteDecompte())) {
				dateDebAff += dto.getDateDebut() == null ? "&nbsp;" : " - " + sdfHeure.format(dto.getDateDebut());
				dateFinAff += dto.getDateFin() == null ? "&nbsp;" : " - " + sdfHeure.format(dto.getDateFin());
			} else if (dto.getTypeSaisi() != null && "jours".equals(dto.getTypeSaisi().getUniteDecompte())) {
				dateDebAff += dto.isDateDebutAM() ? " - M" : dto.isDateDebutPM() ? " - AM" : "&nbsp;";
				dateFinAff += dto.isDateFinAM() ? " - M" : dto.isDateFinPM() ? " - AM" : "&nbsp;";
			}

			addZone(getNOM_ST_DATE_DEBUT_TT(i), dateDebAff);
			addZone(getNOM_ST_DATE_FIN_TT(i), dateFinAff);
			if (dto.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()) {
				addZone(getNOM_ST_DUREE_TT(i), getHeureMinute(dto.getDuree().intValue()));
			} else if (dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
				addZone(getNOM_ST_DUREE_TT(i), dto.getDuree() + "j");
			} else if (dto.getGroupeAbsence() != null
					&& dto.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {

				if ("jours".equals(dto.getTypeSaisi().getUniteDecompte())) {
					addZone(getNOM_ST_DUREE_TT(i), dto.getDuree() == null ? "&nbsp;" : dto.getDuree().toString() + "j");
				}
				if ("minutes".equals(dto.getTypeSaisi().getUniteDecompte())) {
					addZone(getNOM_ST_DUREE_TT(i), dto.getDuree() == null ? "&nbsp;" : getHeureMinute(dto.getDuree()
							.intValue()));
				}
			} else {
				addZone(getNOM_ST_DUREE_TT(i), "&nbsp;");
			}
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
				getAgentCourant().getIdAgent(), "EN_COURS", dateDebut, dateFin, dateDemande, idRefEtat,
				idRefTypeAbsence);
		setListeDemandeEnCours(a);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		for (int i = 0; i < getListeDemandeEnCours().size(); i++) {
			DemandeDto dto = getListeDemandeEnCours().get(i);
			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(dto.getIdTypeDemande());

			addZone(getNOM_ST_TYPE_DEMANDE_EC(i), getListeTypeAbsence().get(getListeTypeAbsence().indexOf(t))
					.getLibelle());

			String dateDebAff = dto.getDateDebut() == null ? "&nbsp;" : sdfDate.format(dto.getDateDebut());
			String dateFinAff = dto.getDateFin() == null ? "&nbsp;" : sdfDate.format(dto.getDateFin());
			if (dto.getTypeSaisi() != null && "minutes".equals(dto.getTypeSaisi().getUniteDecompte())) {
				dateDebAff += dto.getDateDebut() == null ? "&nbsp;" : " - " + sdfHeure.format(dto.getDateDebut());
				dateFinAff += dto.getDateFin() == null ? "&nbsp;" : " - " + sdfHeure.format(dto.getDateFin());
			} else if (dto.getTypeSaisi() != null && "jours".equals(dto.getTypeSaisi().getUniteDecompte())) {
				dateDebAff += dto.isDateDebutAM() ? " - M" : dto.isDateDebutPM() ? " - AM" : "&nbsp;";
				dateFinAff += dto.isDateFinAM() ? " - M" : dto.isDateFinPM() ? " - AM" : "&nbsp;";
			}
			addZone(getNOM_ST_DATE_DEBUT_EC(i), dateDebAff);
			addZone(getNOM_ST_DATE_FIN_EC(i), dateFinAff);

			if (dto.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()) {
				addZone(getNOM_ST_DUREE_EC(i), getHeureMinute(dto.getDuree().intValue()));
			} else if (dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
				addZone(getNOM_ST_DUREE_EC(i), dto.getDuree() + "j");
			} else if (dto.getGroupeAbsence() != null
					&& dto.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {

				if ("jours".equals(dto.getTypeSaisi().getUniteDecompte())) {
					addZone(getNOM_ST_DUREE_EC(i), dto.getDuree() == null ? "&nbsp;" : dto.getDuree().toString() + "j");
				}
				if ("minutes".equals(dto.getTypeSaisi().getUniteDecompte())) {
					addZone(getNOM_ST_DUREE_EC(i), dto.getDuree() == null ? "&nbsp;" : getHeureMinute(dto.getDuree()
							.intValue()));
				}
			} else {
				addZone(getNOM_ST_DUREE_EC(i), "&nbsp;");
			}
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
				getAgentCourant().getIdAgent(), "NON_PRISES", dateDebut, dateFin, dateDemande, idRefEtat,
				idRefTypeAbsence);
		setListeDemandeNonPrises(a);

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");

		for (int i = 0; i < getListeDemandeNonPrises().size(); i++) {
			DemandeDto dto = getListeDemandeNonPrises().get(i);
			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(dto.getIdTypeDemande());

			addZone(getNOM_ST_TYPE_DEMANDE_NP(i), getListeTypeAbsence().get(getListeTypeAbsence().indexOf(t))
					.getLibelle());

			String dateDebAff = dto.getDateDebut() == null ? "&nbsp;" : sdfDate.format(dto.getDateDebut());
			String dateFinAff = dto.getDateFin() == null ? "&nbsp;" : sdfDate.format(dto.getDateFin());
			if (dto.getTypeSaisi() != null && "minutes".equals(dto.getTypeSaisi().getUniteDecompte())) {
				dateDebAff += dto.getDateDebut() == null ? "&nbsp;" : " - " + sdfHeure.format(dto.getDateDebut());
				dateFinAff += dto.getDateFin() == null ? "&nbsp;" : " - " + sdfHeure.format(dto.getDateFin());
			} else if (dto.getTypeSaisi() != null && "jours".equals(dto.getTypeSaisi().getUniteDecompte())) {
				dateDebAff += dto.isDateDebutAM() ? " - M" : dto.isDateDebutPM() ? " - AM" : "&nbsp;";
				dateFinAff += dto.isDateFinAM() ? " - M" : dto.isDateFinPM() ? " - AM" : "&nbsp;";
			}
			addZone(getNOM_ST_DATE_DEBUT_NP(i), dateDebAff);
			addZone(getNOM_ST_DATE_FIN_NP(i), dateFinAff);

			if (dto.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()) {
				addZone(getNOM_ST_DUREE_NP(i), getHeureMinute(dto.getDuree().intValue()));
			} else if (dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
					|| dto.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
					| dto.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
				addZone(getNOM_ST_DUREE_NP(i), dto.getDuree() + "j");
			} else if (dto.getGroupeAbsence() != null
					&& dto.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {

				if ("jours".equals(dto.getTypeSaisi().getUniteDecompte())) {
					addZone(getNOM_ST_DUREE_NP(i), dto.getDuree() == null ? "&nbsp;" : dto.getDuree().toString() + "j");
				}
				if ("minutes".equals(dto.getTypeSaisi().getUniteDecompte())) {
					addZone(getNOM_ST_DUREE_NP(i), dto.getDuree() == null ? "&nbsp;" : getHeureMinute(dto.getDuree()
							.intValue()));
				}
			} else {
				addZone(getNOM_ST_DUREE_NP(i), "&nbsp;");
			}
			addZone(getNOM_ST_DATE_DEMANDE_NP(i),
					dto.getDateDemande() == null ? "&nbsp;" : sdfDate.format(dto.getDateDemande()));
			addZone(getNOM_ST_ETAT_DEMANDE_NP(i), EnumEtatAbsence.getValueEnumEtatAbsence(dto.getIdRefEtat()));

		}

	}

	private static String getHeureMinute(int nombreMinute) {
		int heure = nombreMinute / 60;
		int minute = nombreMinute % 60;
		String res = Const.CHAINE_VIDE;
		if (heure > 0)
			res += heure + "h";
		if (minute > 0)
			res += minute + "m";

		return res;
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

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

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Process incoming requests for information
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 */
	public boolean recupererOnglet(javax.servlet.http.HttpServletRequest request) throws Exception {

		if (super.recupererOnglet(request)) {
			performPB_RESET(request);
			return true;
		}
		return false;
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
		TypeAbsenceDto typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_EC_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_EC_SELECT()) : -1);
		if (indiceTypeAbsence > 0) {
			typeAbsence = (TypeAbsenceDto) getListeTypeAbsence().get(indiceTypeAbsence - 1);
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
				etatAbsence == null ? null : etatAbsence.getCode(),
				typeAbsence == null ? null : typeAbsence.getIdRefTypeAbsence());
		return true;
	}

	public String getNOM_PB_FILTRER_NP() {
		return "NOM_PB_FILTRER_NP";
	}

	public boolean performPB_FILTRER_NP(HttpServletRequest request) throws Exception {

		// Recuperation type absence
		TypeAbsenceDto typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_NP_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_NP_SELECT()) : -1);
		if (indiceTypeAbsence > 0) {
			typeAbsence = (TypeAbsenceDto) getListeTypeAbsence().get(indiceTypeAbsence - 1);
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
				: etatAbsence.getCode(), typeAbsence == null ? null : typeAbsence.getIdRefTypeAbsence());
		return true;
	}

	public String getNOM_PB_FILTRER_TT() {
		return "NOM_PB_FILTRER_TT";
	}

	public boolean performPB_FILTRER_TT(HttpServletRequest request) throws Exception {

		// Recuperation type absence
		TypeAbsenceDto typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_TT_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_TT_SELECT()) : -1);
		if (indiceTypeAbsence > 0) {
			typeAbsence = (TypeAbsenceDto) getListeTypeAbsence().get(indiceTypeAbsence - 1);
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
				etatAbsence == null ? null : etatAbsence.getCode(),
				typeAbsence == null ? null : typeAbsence.getIdRefTypeAbsence());
		return true;
	}

	public ArrayList<TypeAbsenceDto> getListeTypeAbsence() {
		return listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<TypeAbsenceDto> listeTypeAbsence) {
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

	public String getNOM_ST_DATE_FIN_NP(int i) {
		return "NOM_ST_DATE_FIN_NP" + i;
	}

	public String getVAL_ST_DATE_FIN_NP(int i) {
		return getZone(getNOM_ST_DATE_FIN_NP(i));
	}

	public String getNOM_ST_DATE_FIN_EC(int i) {
		return "NOM_ST_DATE_FIN_EC" + i;
	}

	public String getVAL_ST_DATE_FIN_EC(int i) {
		return getZone(getNOM_ST_DATE_FIN_EC(i));
	}

	public String getNOM_ST_DATE_FIN_TT(int i) {
		return "NOM_ST_DATE_FIN_TT" + i;
	}

	public String getVAL_ST_DATE_FIN_TT(int i) {
		return getZone(getNOM_ST_DATE_FIN_TT(i));
	}
}
