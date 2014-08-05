package nc.mairie.gestionagent.process.absence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.DemandeEtatChangeDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.spring.ws.RadiWSConsumer;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.JSONSerializer;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeABSVisualisation extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat hrs = new SimpleDateFormat("HH:mm");

	public String focus = null;
	private Logger logger = LoggerFactory.getLogger(OeABSVisualisation.class);

	public static final int STATUT_RECHERCHER_AGENT_DEMANDE = 1;
	public static final int STATUT_RECHERCHER_AGENT_ACTION = 2;
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 3;

	private String[] LB_ETAT;
	private String[] LB_FAMILLE;
	private String[] LB_FAMILLE_CREATION;
	private String[] LB_HEURE;
	private String[] LB_OS;

	public Hashtable<String, TreeHierarchy> hTree = null;
	private ArrayList<Service> listeServices;
	private ArrayList<EnumEtatAbsence> listeEtats;
	private ArrayList<TypeAbsenceDto> listeFamilleAbsence;
	private ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale;
	private ArrayList<String> listeHeure;

	private HashMap<Integer, DemandeDto> listeAbsence;
	private HashMap<Integer, List<DemandeDto>> history = new HashMap<>();

	public String ACTION_CREATION = "Création d'une absence.";
	public String ACTION_CREATION_A48_A54_A50 = "Création d'une demande ASA.";
	public String ACTION_CREATION_A55 = "Création d'une délégation (DP).";
	public String ACTION_CREATION_A53 = "Création d'une formation syndicale.";
	public String ACTION_CREATION_A52 = "Création d'une décharge de service.";
	public String ACTION_CREATION_A49 = "Création d'une participation à une réunion syndicale.";
	public String ACTION_MOTIF_ANNULATION = "Motif pour l'annulation de la demande.";
	public String ACTION_MOTIF_EN_ATTENTE = "Motif pour la mise en attente de la demande.";

	private TypeAbsenceDto typeCreation;
	private AgentNW agentCreation;

	@Override
	public String getJSP() {
		return "OeABSVisualisation.jsp";
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return "";
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		if (etatStatut() == STATUT_RECHERCHER_AGENT_DEMANDE) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_DEMANDE(), agt.getNoMatricule());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_ACTION) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_ACTION(), agt.getNoMatricule());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATION) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATION(), agt.getNoMatricule());
			}
		}
	}

	private void initialiseListeDeroulante() throws Exception {
		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			List<EnumEtatAbsence> etats = EnumEtatAbsence.getValues();
			setListeEtats((ArrayList<EnumEtatAbsence>) etats);
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (EnumEtatAbsence etat : etats) {
				String ligne[] = { etat.getValue() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);

		}

		// Si liste famille absence vide alors affectation
		if (getLB_FAMILLE() == LBVide || getLB_FAMILLE_CREATION() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeFamilleAbsence((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto());

			int[] tailles = { 100 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<TypeAbsenceDto> list = getListeFamilleAbsence().listIterator(); list.hasNext();) {
				TypeAbsenceDto type = (TypeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FAMILLE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_FAMILLE_SELECT(), Const.ZERO);
			setLB_FAMILLE_CREATION(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), Const.ZERO);
		}

		// Si liste organisation syndicale vide alors affectation
		if (getLB_OS() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			ArrayList<OrganisationSyndicaleDto> listeOrga = (ArrayList<OrganisationSyndicaleDto>) consuAbs
					.getListeOrganisationSyndicale();
			setListeOrganisationSyndicale(listeOrga);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (OrganisationSyndicaleDto os : listeOrga) {
				String ligne[] = { os.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_OS(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
		}
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().isEmpty()) {
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
			hTree = new Hashtable<>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService())) {
					continue;
				}

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");
				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

			}
		}

		// si liste des heures vide alors affectation
		if (getListeHeure() == null || getListeHeure().size() == 0) {
			setListeHeure(new ArrayList<String>());
			int heureDeb = 6; // heures depart
			int minuteDeb = 0; // minutes debut
			int diffFinDeb = 14 * 60; // différence en minute entre le début et
										// la
										// fin
			int interval = 30; // interval en minute

			SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm"); // format
																			// de
																			// la
																			// date

			GregorianCalendar deb = new GregorianCalendar();
			deb.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
			if (heureDeb > 11) // gestion AM PM
				deb.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);
			else
				deb.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
			deb.set(GregorianCalendar.HOUR, heureDeb % 12);
			deb.set(GregorianCalendar.MINUTE, minuteDeb);

			GregorianCalendar fin = (GregorianCalendar) deb.clone();
			fin.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
			fin.set(GregorianCalendar.MINUTE, diffFinDeb);

			getListeHeure().add(formatDate.format(deb.getTime()));
			Integer i = 1;
			while (deb.compareTo(fin) < 0) {
				deb.add(GregorianCalendar.MINUTE, interval);
				getListeHeure().add(formatDate.format(deb.getTime()));
				i++;
			}
			String[] a = new String[29];
			for (int j = 0; j < getListeHeure().size(); j++) {
				a[j] = getListeHeure().get(j);
			}
			setLB_HEURE(a);
		}

	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER_AGENT_DEMANDE
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_DEMANDE())) {
				return performPB_RECHERCHER_AGENT_DEMANDE(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}
			// Si clic sur le bouton PB_RECHERCHER_AGENT_ACTION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_ACTION())) {
				return performPB_RECHERCHER_AGENT_ACTION(request);
			}
			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_ACTION
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_ACTION(request);
			}
			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}
			// Si clic sur le bouton PB_AJOUTER_ABSENCE
			if (testerParametre(request, getNOM_PB_AJOUTER_ABSENCE())) {
				return performPB_AJOUTER_ABSENCE(request);
			}
			// Si clic sur le bouton PB_RECHERCHER_AGENT_CREATION
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATION())) {
				return performPB_RECHERCHER_AGENT_CREATION(request);
			}
			// Si clic sur le bouton PB_CREATION
			if (testerParametre(request, getNOM_PB_CREATION())) {
				return performPB_CREATION(request);
			}
			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
			// Si clic sur le bouton PB_VALIDER_CREATION_A48_A54_A50
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_A48_A54_A50())) {
				return performPB_VALIDER_CREATION_A48_A54_A50(request);
			}
			// Si clic sur le bouton PB_VALIDER_CREATION_A55
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_A55())) {
				return performPB_VALIDER_CREATION_A55(request);
			}
			// Si clic sur le bouton PB_VALIDER_CREATION_A53
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_A53())) {
				return performPB_VALIDER_CREATION_A53(request);
			}
			// Si clic sur le bouton PB_VALIDER_CREATION_A52
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_A52())) {
				return performPB_VALIDER_CREATION_A52(request);
			}
			// Si clic sur le bouton PB_VALIDER_CREATION_A49
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_A49())) {
				return performPB_VALIDER_CREATION_A49(request);
			}

			// Si clic sur les boutons du tableau
			for (DemandeDto abs : getListeAbsence().values()) {
				int indiceAbs = abs.getIdDemande();
				// Si clic sur le bouton PB_DUPLIQUER
				if (testerParametre(request, getNOM_PB_DUPLIQUER(indiceAbs))) {
					return performPB_DUPLIQUER(request, indiceAbs);
				}
				// Si clic sur le bouton PB_ANNULER_DEMANDE
				if (testerParametre(request, getNOM_PB_ANNULER_DEMANDE(indiceAbs))) {
					return performPB_ANNULER_DEMANDE(request, indiceAbs);
				}
				// Si clic sur le bouton PB_VALIDER
				if (testerParametre(request, getNOM_PB_VALIDER(indiceAbs))) {
					return performPB_VALIDER(request, indiceAbs);
				}
				// Si clic sur le bouton PB_REJETER
				if (testerParametre(request, getNOM_PB_REJETER(indiceAbs))) {
					return performPB_REJETER(request, indiceAbs);
				}
				// Si clic sur le bouton PB_EN_ATTENTE
				if (testerParametre(request, getNOM_PB_EN_ATTENTE(indiceAbs))) {
					return performPB_EN_ATTENTE(request, indiceAbs);
				}
				// Si clic sur le bouton PB_DOCUMENT
				if (testerParametre(request, getNOM_PB_DOCUMENT(indiceAbs))) {
					return performPB_DOCUMENT(request, indiceAbs);
				}
			}
			// Si clic sur le bouton PB_VALIDER_ALL
			if (testerParametre(request, getNOM_PB_VALIDER_ALL())) {
				return performPB_VALIDER_ALL(request);
			}
			// Si clic sur le bouton PB_REJETER_ALL
			if (testerParametre(request, getNOM_PB_REJETER_ALL())) {
				return performPB_REJETER_ALL(request);
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_ANNULATION
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF_ANNULATION())) {
				return performPB_VALIDER_MOTIF_ANNULATION(request);
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_EN_ATTENTE
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF_EN_ATTENTE())) {
				return performPB_VALIDER_MOTIF_EN_ATTENTE(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNomEcran() {
		return "ECR-ABS-VISU";
	}

	public String getNOM_ST_AGENT_DEMANDE() {
		return "NOM_ST_AGENT_DEMANDE";
	}

	public String getVAL_ST_AGENT_DEMANDE() {
		return getZone(getNOM_ST_AGENT_DEMANDE());
	}

	public String getNOM_PB_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_DEMANDE, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_DEMANDE(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enlève le service selectionnée
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	private String[] getLB_ETAT() {
		if (LB_ETAT == null)
			LB_ETAT = initialiseLazyLB();
		return LB_ETAT;
	}

	private void setLB_ETAT(String[] newLB_ETAT) {
		LB_ETAT = newLB_ETAT;
	}

	public String getNOM_LB_ETAT() {
		return "NOM_LB_ETAT";
	}

	public String getNOM_LB_ETAT_SELECT() {
		return "NOM_LB_ETAT_SELECT";
	}

	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	private String[] getLB_FAMILLE() {
		if (LB_FAMILLE == null)
			LB_FAMILLE = initialiseLazyLB();
		return LB_FAMILLE;
	}

	private void setLB_FAMILLE(String[] newLB_FAMILLE) {
		LB_FAMILLE = newLB_FAMILLE;
	}

	public String getNOM_LB_FAMILLE() {
		return "NOM_LB_FAMILLE";
	}

	public String getNOM_LB_FAMILLE_SELECT() {
		return "NOM_LB_FAMILLE_SELECT";
	}

	public String[] getVAL_LB_FAMILLE() {
		return getLB_FAMILLE();
	}

	public String getVAL_LB_FAMILLE_SELECT() {
		return getZone(getNOM_LB_FAMILLE_SELECT());
	}

	public String getNOM_ST_AGENT_ACTION() {
		return "NOM_ST_AGENT_ACTION";
	}

	public String getVAL_ST_AGENT_ACTION() {
		return getZone(getNOM_ST_AGENT_ACTION());
	}

	public String getNOM_PB_RECHERCHER_AGENT_ACTION() {
		return "NOM_PB_RECHERCHER_AGENT_ACTION";
	}

	public boolean performPB_RECHERCHER_AGENT_ACTION(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_ACTION, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_ACTION(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_ST_DATE_MIN() {
		return "NOM_ST_DATE_MIN";
	}

	public String getVAL_ST_DATE_MIN() {
		return getZone(getNOM_ST_DATE_MIN());
	}

	public String getNOM_ST_DATE_MAX() {
		return "NOM_ST_DATE_MAX";
	}

	public String getVAL_ST_DATE_MAX() {
		return getZone(getNOM_ST_DATE_MAX());
	}

	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {

		if (!performControlerFiltres()) {
			return false;
		}

		String dateDeb = getVAL_ST_DATE_MIN();
		String dateMin = dateDeb.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateDeb, "dd/MM/yyyy",
				"yyyyMMdd");

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = dateFin.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateFin, "dd/MM/yyyy",
				"yyyyMMdd");

		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
		EnumEtatAbsence etat = null;
		if (numEtat != -1 && numEtat != 0) {
			etat = (EnumEtatAbsence) getListeEtats().get(numEtat - 1);
		}
		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_FAMILLE_SELECT())) : -1);
		TypeAbsenceDto type = null;
		if (numType != -1 && numType != 0) {
			type = (TypeAbsenceDto) getListeFamilleAbsence().get(numType - 1);
		}

		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900"
				+ getVAL_ST_AGENT_DEMANDE();
		String idAgentAction = getVAL_ST_AGENT_ACTION().equals(Const.CHAINE_VIDE) ? null : "900"
				+ getVAL_ST_AGENT_ACTION();

		// SERVICE
		String sigleService = getVAL_EF_SERVICE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_SERVICE().toUpperCase();

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		List<DemandeDto> listeDemande = t.getListeDemandes(dateMin, dateMax, etat == null ? null : etat.getCode(),
				type == null ? null : type.getIdRefTypeAbsence(),
				idAgentDemande == null ? null : Integer.valueOf(idAgentDemande));
		logger.debug("Taille liste absences : " + listeDemande.size());

		setListeAbsence((ArrayList<DemandeDto>) listeDemande);

		loadHistory();

		afficheListeAbsence();

		return true;
	}

	private void afficheListeAbsence() throws Exception {

		for (DemandeDto abs : getListeAbsence().values()) {
			Integer i = abs.getIdDemande();
			AgentNW ag = AgentNW.chercherAgent(getTransaction(), abs.getAgentWithServiceDto().getIdAgent().toString());
			if (ag == null || ag.getIdAgent() == null) {
				getTransaction().traiterErreur();
				continue;
			}
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), ag);
			if (carr == null || carr.getNoMatricule() == null) {
				getTransaction().traiterErreur();
			}
			String statut = carr == null ? "&nbsp;" : Carriere.getStatutCarriere(carr.getCodeCategorie());

			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(abs.getIdTypeDemande());
			TypeAbsenceDto type = getListeFamilleAbsence().get(getListeFamilleAbsence().indexOf(t));

			addZone(getNOM_ST_MATRICULE(i), ag.getNoMatricule());
			addZone(getNOM_ST_AGENT(i), ag.getNomAgent() + " " + ag.getPrenomAgent());
			addZone(getNOM_ST_INFO_AGENT(i), "<br/>" + statut);
			addZone(getNOM_ST_TYPE(i), type.getLibelle() + "<br/>" + sdf.format(abs.getDateDemande()));

			String debutMAM = abs.isDateDebutAM() ? "M" : abs.isDateDebutPM() ? "AM" : Const.CHAINE_VIDE;
			addZone(getNOM_ST_DATE_DEB(i),
					sdf.format(abs.getDateDebut()) + "<br/>"
							+ (debutMAM.equals(Const.CHAINE_VIDE) ? hrs.format(abs.getDateDebut()) : debutMAM));
			if (abs.getDateFin() != null) {
				String finMAM = abs.isDateFinAM() ? "M" : abs.isDateFinPM() ? "AM" : Const.CHAINE_VIDE;
				addZone(getNOM_ST_DATE_FIN(i),
						sdf.format(abs.getDateFin()) + "<br/>"
								+ (finMAM.equals(Const.CHAINE_VIDE) ? hrs.format(abs.getDateFin()) : finMAM));
			}
			if (abs.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()) {
				addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : getHeureMinute(abs.getDuree()
						.intValue()));
			} else if (abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
					|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()) {
				addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
			} else {
				addZone(getNOM_ST_DUREE(i), "&nbsp;");
			}
			addZone(getNOM_ST_MOTIF(i), abs.getMotif());
			addZone(getNOM_ST_ETAT(i), EnumEtatAbsence.getValueEnumEtatAbsence(abs.getIdRefEtat()));
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

	private boolean performControlerFiltres() throws Exception {

		// on controle que le service saisie est bien un service
		String sigleService = getVAL_EF_SERVICE().toUpperCase();
		if (!sigleService.equals(Const.CHAINE_VIDE)) {
			// on cherche le code service associé
			Service siserv = Service.chercherServiceBySigle(getTransaction(), sigleService);
			if (getTransaction().isErreur() || siserv == null || siserv.getCodService() == null) {
				getTransaction().traiterErreur();
				// ERR502", "Le sigle service saisie ne permet pas de trouver le
				// service associé."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR502"));
				return false;

			}
		}

		return true;
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

	public ArrayList<EnumEtatAbsence> getListeEtats() {
		return listeEtats == null ? new ArrayList<EnumEtatAbsence>() : listeEtats;
	}

	public void setListeEtats(ArrayList<EnumEtatAbsence> listeEtats) {
		this.listeEtats = listeEtats;
	}

	public ArrayList<TypeAbsenceDto> getListeFamilleAbsence() {
		return listeFamilleAbsence == null ? new ArrayList<TypeAbsenceDto>() : listeFamilleAbsence;
	}

	public void setListeFamilleAbsence(ArrayList<TypeAbsenceDto> listeFamilleAbsence) {
		this.listeFamilleAbsence = listeFamilleAbsence;
	}

	public String getNOM_PB_AJOUTER_ABSENCE() {
		return "NOM_PB_CREATE_BOX";
	}

	public boolean performPB_AJOUTER_ABSENCE(HttpServletRequest request) throws Exception {
		viderZoneSaisie(request);
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private String[] getLB_FAMILLE_CREATION() {
		if (LB_FAMILLE_CREATION == null)
			LB_FAMILLE_CREATION = initialiseLazyLB();
		return LB_FAMILLE_CREATION;
	}

	private void setLB_FAMILLE_CREATION(String[] newLB_FAMILLE_CREATION) {
		LB_FAMILLE_CREATION = newLB_FAMILLE_CREATION;
	}

	public String getNOM_LB_FAMILLE_CREATION() {
		return "NOM_LB_FAMILLE_CREATION";
	}

	public String getNOM_LB_FAMILLE_CREATION_SELECT() {
		return "NOM_LB_FAMILLE_CREATION_SELECT";
	}

	public String[] getVAL_LB_FAMILLE_CREATION() {
		return getLB_FAMILLE_CREATION();
	}

	public String getVAL_LB_FAMILLE_CREATION_SELECT() {
		return getZone(getNOM_LB_FAMILLE_CREATION_SELECT());
	}

	public String getNOM_ST_AGENT_CREATION() {
		return "NOM_ST_AGENT_CREATION";
	}

	public String getVAL_ST_AGENT_CREATION() {
		return getZone(getNOM_ST_AGENT_CREATION());
	}

	public String getNOM_PB_RECHERCHER_AGENT_CREATION() {
		return "NOM_PB_RECHERCHER_AGENT_CREATION";
	}

	public boolean performPB_RECHERCHER_AGENT_CREATION(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_CREATION, true);
		return true;
	}

	public String getNOM_PB_CREATION() {
		return "NOM_PB_CREATION";
	}

	public boolean performPB_CREATION(HttpServletRequest request) throws Exception {
		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) : -1);
		TypeAbsenceDto type = null;
		if (numType != -1) {
			type = (TypeAbsenceDto) getListeFamilleAbsence().get(numType);
			setTypeCreation(type);
		}
		String idAgent = "";
		if (getVAL_ST_AGENT_CREATION().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		} else {
			idAgent = "900" + getVAL_ST_AGENT_CREATION();
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), idAgent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
				return false;

			}
			setAgentCreation(agent);
		}

		// On nomme l'action
		if (type != null
				&& (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A48.getCode().toString())
						|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A54.getCode().toString()) || type
						.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A50.getCode().toString()))) {
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_A48_A54_A50);
		} else if (type != null
				&& type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A55.getCode().toString())) {
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_A55);
		} else if (type != null
				&& type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A53.getCode().toString())) {
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_A53);
		} else if (type != null
				&& type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A52.getCode().toString())) {
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_A52);
		} else if (type != null
				&& type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A49.getCode().toString())) {
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_A49);
		} else {
			getTransaction().declarerErreur("Cette famille ne peut être saisie dans SIRH");
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		viderZoneSaisie(request);
		return true;
	}

	private void viderZoneSaisie(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_AGENT_CREATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_DEBUT_MAM(), getNOM_RB_M());
		addZone(getNOM_RG_FIN_MAM(), getNOM_RB_M());
		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), Const.ZERO);
		addZone(getNOM_ST_INFO_MOTIF_ANNULATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_MOTIF_ANNULATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ID_DEMANDE_ANNULATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_MOTIF_EN_ATTENTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_MOTIF_EN_ATTENTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ID_DEMANDE_EN_ATTENTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_HEURE(), Const.ZERO);
		addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
		setAgentCreation(null);
		setTypeCreation(null);
	}

	public String getNOM_RG_DEBUT_MAM() {
		return "NOM_RG_DEBUT_MAM";
	}

	public String getVAL_RG_DEBUT_MAM() {
		return getZone(getNOM_RG_DEBUT_MAM());
	}

	public String getNOM_RB_M() {
		return "NOM_RB_M";
	}

	public String getNOM_RB_AM() {
		return "NOM_RB_AM";
	}

	public String getNOM_ST_DUREE() {
		return "NOM_ST_DUREE";
	}

	public String getVAL_ST_DUREE() {
		return getZone(getNOM_ST_DUREE());
	}

	public String getNOM_ST_DATE_DEBUT() {
		return "NOM_ST_DATE_DEBUT";
	}

	public String getVAL_ST_DATE_DEBUT() {
		return getZone(getNOM_ST_DATE_DEBUT());
	}

	public String getNOM_ST_DATE_FIN() {
		return "NOM_ST_DATE_FIN";
	}

	public String getVAL_ST_DATE_FIN() {
		return getZone(getNOM_ST_DATE_FIN());
	}

	public String getNOM_RG_FIN_MAM() {
		return "NOM_RG_FIN_MAM";
	}

	public String getVAL_RG_FIN_MAM() {
		return getZone(getNOM_RG_FIN_MAM());
	}

	public String getNOM_PB_VALIDER_CREATION_A48_A54_A50() {
		return "NOM_PB_VALIDER_CREATION_A48_A54_A50";
	}

	public boolean performPB_VALIDER_CREATION_A48_A54_A50(HttpServletRequest request) throws Exception {
		AgentNW ag = getAgentCreation();
		TypeAbsenceDto type = getTypeCreation();

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
			return false;
		}
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateDeb = sdf.parse(getVAL_ST_DATE_DEBUT());
		Date dateFin = sdf.parse(getVAL_ST_DATE_FIN());
		Boolean matinDebut = getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M());
		Boolean apresMidiDebut = getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM());
		Boolean matinFin = getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M());
		Boolean apresMidiFin = getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM());

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			return false;
		}

		DemandeDto dto = new DemandeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateDebutAM(matinDebut);
		dto.setDateDebutPM(apresMidiDebut);
		dto.setDateFin(dateFin);
		dto.setDateFinAM(matinFin);
		dto.setDateFinPM(apresMidiFin);
		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(Integer.valueOf(ag.getIdAgent()));
		dto.setAgentWithServiceDto(agDto);
		dto.setIdTypeDemande(type.getIdRefTypeAbsence());
		dto.setIdRefEtat(EnumEtatAbsence.SAISIE.getCode());
		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto(EnumTypeGroupeAbsence.ASA);
		dto.setGroupeAbsence(groupeDto);

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveDemande(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		performPB_FILTRER(request);
		return true;
	}

	private AgentNW getAgentConnecte(HttpServletRequest request) throws Exception {
		AgentNW agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		if (!uUser.getUserName().equals("nicno85") && !uUser.getUserName().equals("rebjo84")) {
			// on fait la correspondance entre le login et l'agent via RADI
			RadiWSConsumer radiConsu = new RadiWSConsumer();
			LightUserDto user = radiConsu.getAgentCompteADByLogin(uUser.getUserName());
			if (user == null) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return null;
			} else {
				if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
					agent = AgentNW.chercherAgentParMatricule(getTransaction(),
							radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
						return null;
					}
				}
			}
		} else {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
		return agent;
	}

	public TypeAbsenceDto getTypeCreation() {
		return typeCreation;
	}

	public void setTypeCreation(TypeAbsenceDto typeCreation) {
		this.typeCreation = typeCreation;
	}

	public AgentNW getAgentCreation() {
		return agentCreation;
	}

	public void setAgentCreation(AgentNW agentCreation) {
		this.agentCreation = agentCreation;
	}

	public HashMap<Integer, DemandeDto> getListeAbsence() {
		return listeAbsence == null ? new HashMap<Integer, DemandeDto>() : listeAbsence;
	}

	public void setListeAbsence(ArrayList<DemandeDto> listeAbsenceAjout) {
		listeAbsence = new HashMap<>();
		for (DemandeDto dem : listeAbsenceAjout) {
			listeAbsence.put(dem.getIdDemande(), dem);
		}
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	public String getNOM_ST_INFO_AGENT(int i) {
		return "NOM_ST_INFO_AGENT_" + i;
	}

	public String getVAL_ST_INFO_AGENT(int i) {
		return getZone(getNOM_ST_INFO_AGENT(i));
	}

	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE_" + i;
	}

	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	public String getNOM_ST_DATE_DEB(int i) {
		return "NOM_ST_DATE_DEB_" + i;
	}

	public String getVAL_ST_DATE_DEB(int i) {
		return getZone(getNOM_ST_DATE_DEB(i));
	}

	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN_" + i;
	}

	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF_" + i;
	}

	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	public String getNOM_ST_DUREE(int i) {
		return "NOM_ST_DUREE_" + i;
	}

	public String getVAL_ST_DUREE(int i) {
		return getZone(getNOM_ST_DUREE(i));
	}

	public String getValHistory(int id) {
		return "History_" + id;
	}

	public String getHistory(int absId) {
		if (!history.containsKey(absId)) {
			SirhAbsWSConsumer t = new SirhAbsWSConsumer();
			history.put(absId, t.getVisualisationHistory(absId));
		}
		List<DemandeDto> data = history.get(absId);
		int numParams = 7;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		for (DemandeDto p : data) {
			ret[index][0] = formatDate(p.getDateDemande());
			ret[index][1] = formatDate(p.getDateDebut()) + "<br/>" + formatHeure(p.getDateDebut());
			ret[index][2] = formatDate(p.getDateFin()) + "<br/>" + formatHeure(p.getDateFin());
			String duree = "&nbsp;";
			if (p.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()) {
				duree = getHeureMinute(p.getDuree().intValue());
			} else if (p.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()) {
				duree = p.getDuree().toString() + "j";
			} else if (p.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()) {
				duree = getHeureMinute(p.getDuree().intValue());
			}
			ret[index][3] = duree;
			ret[index][4] = EnumEtatAbsence.getValueEnumEtatAbsence(p.getIdRefEtat());
			ret[index][5] = p.getMotif() == null ? "&nbsp;" : p.getMotif();
			ret[index][6] = formatDate(p.getDateSaisie()) + "<br/>" + formatHeure(p.getDateSaisie());
			index++;
		}

		StringBuilder strret = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			// strret.append("[");
			for (int j = 0; j < numParams; j++) {
				strret.append(ret[i][j]).append(",");
			}
			strret.deleteCharAt(strret.lastIndexOf(","));
			strret.append("|");
		}
		strret.deleteCharAt(strret.lastIndexOf("|"));
		return strret.toString();
	}

	private String formatDate(Date d) {
		if (d != null) {
			return sdf.format(d);
		} else {
			return "";
		}
	}

	private String formatHeure(Date d) {
		if (d != null) {
			return hrs.format(d);
		} else {
			return "00:00";
		}
	}

	private void loadHistory() {
		for (Integer i : listeAbsence.keySet()) {
			loadHistory(i);
		}
	}

	private void loadHistory(int absId) {
		if (!history.containsKey(absId)) {
			SirhAbsWSConsumer t = new SirhAbsWSConsumer();
			history.put(absId, t.getVisualisationHistory(absId));
		}

	}

	public String getNOM_PB_DUPLIQUER(int i) {
		return "NOM_PB_DUPLIQUER" + i;
	}

	public boolean performPB_DUPLIQUER(HttpServletRequest request, int idDemande) throws Exception {
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		TypeAbsenceDto type = getListeFamilleAbsence().get(getListeFamilleAbsence().indexOf(dem.getIdTypeDemande()));

		// on recup l'organisation syndicale
		OrganisationSyndicaleDto orga = null;
		if (dem.getOrganisationSyndicale() != null) {
			orga = dem.getOrganisationSyndicale();
		}
		AgentNW agt = AgentNW.chercherAgent(getTransaction(), dem.getAgentWithServiceDto().getIdAgent().toString());

		addZone(getNOM_ST_AGENT_CREATION(), agt.getNoMatricule());
		addZone(getNOM_ST_DATE_DEBUT(), new SimpleDateFormat("dd/MM/yyyy").format(dem.getDateDebut()));
		addZone(getNOM_ST_DATE_FIN(), new SimpleDateFormat("dd/MM/yyyy").format(dem.getDateFin()));
		addZone(getNOM_RG_DEBUT_MAM(), dem.isDateDebutAM() ? getNOM_RB_M() : getNOM_RB_AM());
		addZone(getNOM_RG_FIN_MAM(), dem.isDateFinAM() ? getNOM_RB_M() : getNOM_RB_AM());
		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), String.valueOf(getListeFamilleAbsence().indexOf(type)));
		addZone(getNOM_LB_OS_SELECT(),
				orga == null ? Const.ZERO : String.valueOf(getListeOrganisationSyndicale().indexOf(orga)));
		if (dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()) {
			String soldeAsaA55Heure = (dem.getDuree().intValue() / 60) == 0 ? "" : dem.getDuree().intValue() / 60 + "";
			String soldeAsaA55Minute = (dem.getDuree().intValue() % 60) == 0 ? "" : "." + dem.getDuree().intValue()
					% 60;
			addZone(getNOM_ST_DUREE(), soldeAsaA55Heure + soldeAsaA55Minute);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			Integer resHeure = getListeHeure().indexOf(sdf.format(dem.getDateDebut()));
			addZone(getNOM_LB_HEURE_SELECT(), resHeure.toString());
		}
		setTypeCreation(type);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_VALIDER(int i) {
		return "NOM_PB_VALIDER" + i;
	}

	public boolean performPB_VALIDER(HttpServletRequest request, int idDemande) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		changeState(request, dem, EnumEtatAbsence.VALIDEE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);
		return true;
	}

	public String getNOM_PB_REJETER(int i) {
		return "NOM_PB_REJETER" + i;
	}

	public boolean performPB_REJETER(HttpServletRequest request, int idDemande) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		changeState(request, dem, EnumEtatAbsence.REJETE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_EN_ATTENTE(int i) {
		return "NOM_PB_EN_ATTENTE" + i;
	}

	public boolean performPB_EN_ATTENTE(HttpServletRequest request, int idDemande) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		AgentNW ag = AgentNW.chercherAgent(getTransaction(), dem.getAgentWithServiceDto().getIdAgent().toString());
		// Si ASA : A48, A54, A55, A52, A53, A50, A49 et etat=validé ou prise,
		// alors un
		// motif est
		// obligatoire
		if ((dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode() || dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A52
				.getCode()) && dem.getIdRefEtat() == EnumEtatAbsence.APPROUVE.getCode()) {
			// "ERR803",
			// "Pour @ cette demande, merci de renseigner un motif."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR803", "mettre en attente"));

			String info = "Demande "
					+ getListeFamilleAbsence().get(getListeFamilleAbsence().indexOf(dem.getIdTypeDemande()))
							.getLibelle() + " de l'agent " + ag.getNoMatricule() + " du "
					+ sdf.format(dem.getDateDemande()) + ".";
			addZone(getNOM_ST_INFO_MOTIF_EN_ATTENTE(), info);
			addZone(getNOM_ST_MOTIF_EN_ATTENTE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ID_DEMANDE_EN_ATTENTE(), dem.getIdDemande().toString());
			addZone(getNOM_ST_ACTION(), ACTION_MOTIF_EN_ATTENTE);
			return false;
		} else {
			getTransaction().declarerErreur("Cette demande ne peut être mise en attente.");
			return false;
		}
	}

	public String getNOM_PB_VALIDER_ALL() {
		return "NOM_PB_VALIDER_ALL";
	}

	public boolean performPB_VALIDER_ALL(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}

		changeState(request, getListeAbsence().values(), EnumEtatAbsence.VALIDEE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);
		return true;
	}

	public String getNOM_PB_REJETER_ALL() {
		return "NOM_PB_REJETER_ALL";
	}

	public boolean performPB_REJETER_ALL(HttpServletRequest request) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}

		changeState(request, getListeAbsence().values(), EnumEtatAbsence.REJETE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	public boolean performPB_DOCUMENT(HttpServletRequest request, int idDemande) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void changeState(HttpServletRequest request, DemandeDto dem, EnumEtatAbsence state, String motif)
			throws Exception {
		ArrayList<DemandeDto> param = new ArrayList<DemandeDto>();
		param.add(dem);
		changeState(request, param, state, motif);
	}

	private void changeState(HttpServletRequest request, Collection<DemandeDto> dem, EnumEtatAbsence state, String motif)
			throws Exception {
		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			logger.debug("Agent nul dans jsp visualisation");
		} else {
			List<DemandeEtatChangeDto> listDto = new ArrayList<DemandeEtatChangeDto>();
			for (DemandeDto d : dem) {
				DemandeEtatChangeDto dto = new DemandeEtatChangeDto();
				dto.setIdDemande(d.getIdDemande());
				dto.setIdRefEtat(state.getCode());
				dto.setMotif(motif);
				listDto.add(dto);
				refreshHistory(d.getIdDemande());
			}

			String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
					.deepSerialize(listDto);
			ReturnMessageDto message = t.setAbsState(Integer.valueOf(agentConnecte.getIdAgent()), json);

			if (message.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur(err);
			}
			performPB_FILTRER(request);
		}
	}

	private void refreshHistory(int absId) {
		history.remove(absId);
		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		history.put(absId, t.getVisualisationHistory(absId));
	}

	public String getNOM_PB_ANNULER_DEMANDE(int i) {
		return "NOM_PB_ANNULER_DEMANDE" + i;
	}

	public boolean performPB_ANNULER_DEMANDE(HttpServletRequest request, int idDemande) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		AgentNW ag = AgentNW.chercherAgent(getTransaction(), dem.getAgentWithServiceDto().getIdAgent().toString());
		// Si ASA : A48, A54, A55, A52, A53, A50, A49 et etat=validé ou prise,
		// alors un
		// motif est
		// obligatoire
		if ((dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()
				|| dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode() || dem.getIdTypeDemande() == EnumTypeAbsence.ASA_A52
				.getCode())
				&& (dem.getIdRefEtat() == EnumEtatAbsence.VALIDEE.getCode() || dem.getIdRefEtat() == EnumEtatAbsence.PRISE
						.getCode())) {
			// "ERR803",
			// "Pour @ cette demande, merci de renseigner un motif."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR803", "annuler"));
			String info = "Demande "
					+ getListeFamilleAbsence().get(getListeFamilleAbsence().indexOf(dem.getIdTypeDemande()))
							.getLibelle() + " de l'agent " + ag.getNoMatricule() + " du "
					+ sdf.format(dem.getDateDemande()) + ".";
			addZone(getNOM_ST_INFO_MOTIF_ANNULATION(), info);
			addZone(getNOM_ST_MOTIF_ANNULATION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ID_DEMANDE_ANNULATION(), dem.getIdDemande().toString());
			addZone(getNOM_ST_ACTION(), ACTION_MOTIF_ANNULATION);
			return false;
		} else if ((dem.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode() || dem.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP
				.getCode()) && dem.getIdRefEtat() == EnumEtatAbsence.APPROUVE.getCode()) {
			// "ERR803",
			// "Pour @ cette demande, merci de renseigner un motif."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR803", "annuler"));
			String info = "Demande "
					+ getListeFamilleAbsence().get(getListeFamilleAbsence().indexOf(dem.getIdTypeDemande()))
							.getLibelle() + " de l'agent " + ag.getNoMatricule() + " du "
					+ sdf.format(dem.getDateDemande()) + ".";
			addZone(getNOM_ST_INFO_MOTIF_ANNULATION(), info);
			addZone(getNOM_ST_MOTIF_ANNULATION(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ID_DEMANDE_ANNULATION(), dem.getIdDemande().toString());
			addZone(getNOM_ST_ACTION(), ACTION_MOTIF_ANNULATION);
			return false;
		}
		changeState(request, dem, EnumEtatAbsence.ANNULEE, null);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_INFO_MOTIF_ANNULATION() {
		return "NOM_ST_INFO_MOTIF_ANNULATION";
	}

	public String getVAL_ST_INFO_MOTIF_ANNULATION() {
		return getZone(getNOM_ST_INFO_MOTIF_ANNULATION());
	}

	public String getNOM_ST_MOTIF_ANNULATION() {
		return "NOM_ST_MOTIF_ANNULATION";
	}

	public String getVAL_ST_MOTIF_ANNULATION() {
		return getZone(getNOM_ST_MOTIF_ANNULATION());
	}

	public String getNOM_ST_ID_DEMANDE_ANNULATION() {
		return "NOM_ST_ID_DEMANDE_ANNULATION";
	}

	public String getVAL_ST_ID_DEMANDE_ANNULATION() {
		return getZone(getNOM_ST_ID_DEMANDE_ANNULATION());
	}

	public String getNOM_PB_VALIDER_MOTIF_ANNULATION() {
		return "NOM_PB_VALIDER_MOTIF_ANNULATION";
	}

	public boolean performPB_VALIDER_MOTIF_ANNULATION(HttpServletRequest request) throws Exception {
		// on recupere les infos
		String idDemande = getVAL_ST_ID_DEMANDE_ANNULATION();
		String motif = getVAL_ST_MOTIF_ANNULATION();
		if (motif.equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}
		DemandeDto dem = getListeAbsence().get(Integer.valueOf(idDemande));

		changeState(request, dem, EnumEtatAbsence.ANNULEE, motif);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);
		return true;
	}

	public String getNOM_ST_INFO_MOTIF_EN_ATTENTE() {
		return "NOM_ST_INFO_MOTIF_EN_ATTENTE";
	}

	public String getVAL_ST_INFO_MOTIF_EN_ATTENTE() {
		return getZone(getNOM_ST_INFO_MOTIF_EN_ATTENTE());
	}

	public String getNOM_ST_MOTIF_EN_ATTENTE() {
		return "NOM_ST_MOTIF_EN_ATTENTE";
	}

	public String getVAL_ST_MOTIF_EN_ATTENTE() {
		return getZone(getNOM_ST_MOTIF_EN_ATTENTE());
	}

	public String getNOM_ST_ID_DEMANDE_EN_ATTENTE() {
		return "NOM_ST_ID_DEMANDE_EN_ATTENTE";
	}

	public String getVAL_ST_ID_DEMANDE_EN_ATTENTE() {
		return getZone(getNOM_ST_ID_DEMANDE_EN_ATTENTE());
	}

	public String getNOM_PB_VALIDER_MOTIF_EN_ATTENTE() {
		return "NOM_PB_VALIDER_MOTIF_EN_ATTENTE";
	}

	public boolean performPB_VALIDER_MOTIF_EN_ATTENTE(HttpServletRequest request) throws Exception {
		// on recupere les infos
		String idDemande = getVAL_ST_ID_DEMANDE_EN_ATTENTE();
		String motif = getVAL_ST_MOTIF_EN_ATTENTE();
		if (motif.equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}
		DemandeDto dem = getListeAbsence().get(Integer.valueOf(idDemande));

		changeState(request, dem, EnumEtatAbsence.EN_ATTENTE, motif);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		performPB_FILTRER(request);
		return true;
	}

	private String[] getLB_HEURE() {
		if (LB_HEURE == null)
			LB_HEURE = initialiseLazyLB();
		return LB_HEURE;
	}

	private void setLB_HEURE(String[] newLB_HEURE) {
		LB_HEURE = newLB_HEURE;
	}

	public String getNOM_LB_HEURE() {
		return "NOM_LB_HEURE";
	}

	public String getNOM_LB_HEURE_SELECT() {
		return "NOM_LB_HEURE_SELECT";
	}

	public String[] getVAL_LB_HEURE() {
		return getLB_HEURE();
	}

	public String getVAL_LB_HEURE_SELECT() {
		return getZone(getNOM_LB_HEURE_SELECT());
	}

	public String getNOM_PB_VALIDER_CREATION_A55() {
		return "NOM_PB_VALIDER_CREATION_A55";
	}

	public boolean performPB_VALIDER_CREATION_A55(HttpServletRequest request) throws Exception {
		AgentNW ag = getAgentCreation();
		TypeAbsenceDto type = getTypeCreation();

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
			return false;
		}
		if (getVAL_ST_DUREE().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée"));
			return false;
		}
		// heure obligatoire
		int indiceHeure = (Services.estNumerique(getVAL_LB_HEURE_SELECT()) ? Integer.parseInt(getVAL_LB_HEURE_SELECT())
				: -1);
		if (indiceHeure <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "heure"));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String heure = getListeHeure().get(Integer.valueOf(getVAL_LB_HEURE_SELECT()));
		Date dateDeb = sdf.parse(getVAL_ST_DATE_DEBUT() + " " + heure);

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			return false;
		}

		DemandeDto dto = new DemandeDto();
		dto.setDateDebut(dateDeb);
		dto.setDuree(Double.valueOf(getVAL_ST_DUREE().replace(",", ".")) * 60);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(Integer.valueOf(ag.getIdAgent()));
		dto.setAgentWithServiceDto(agDto);
		dto.setIdTypeDemande(type.getIdRefTypeAbsence());
		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto(EnumTypeGroupeAbsence.ASA);
		dto.setGroupeAbsence(groupeDto);
		dto.setIdRefEtat(EnumEtatAbsence.SAISIE.getCode());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveDemande(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		performPB_FILTRER(request);
		return true;
	}

	public ArrayList<String> getListeHeure() {
		return listeHeure;
	}

	private void setListeHeure(ArrayList<String> listeHeure) {
		this.listeHeure = listeHeure;
	}

	private String[] getLB_OS() {
		if (LB_OS == null)
			LB_OS = initialiseLazyLB();
		return LB_OS;
	}

	private void setLB_OS(String[] newLB_OS) {
		LB_OS = newLB_OS;
	}

	public String getNOM_LB_OS() {
		return "NOM_LB_OS";
	}

	public String getNOM_LB_OS_SELECT() {
		return "NOM_LB_OS_SELECT";
	}

	public String[] getVAL_LB_OS() {
		return getLB_OS();
	}

	public String getVAL_LB_OS_SELECT() {
		return getZone(getNOM_LB_OS_SELECT());
	}

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		if (listeOrganisationSyndicale == null)
			return new ArrayList<OrganisationSyndicaleDto>();
		return listeOrganisationSyndicale;
	}

	public void setListeOrganisationSyndicale(ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale) {
		this.listeOrganisationSyndicale = listeOrganisationSyndicale;
	}

	public String getNOM_PB_VALIDER_CREATION_A53() {
		return "NOM_PB_VALIDER_CREATION_A53";
	}

	public boolean performPB_VALIDER_CREATION_A53(HttpServletRequest request) throws Exception {
		AgentNW ag = getAgentCreation();
		TypeAbsenceDto type = getTypeCreation();

		// on recup l'organisation syndicale
		int numOrga = (Services.estNumerique(getZone(getNOM_LB_OS_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_OS_SELECT())) : -1);
		OrganisationSyndicaleDto orga = null;
		if (numOrga != -1) {
			orga = (OrganisationSyndicaleDto) getListeOrganisationSyndicale().get(numOrga);
		} else {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "organisation syndicale"));
			return false;
		}

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
			return false;
		}
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateDeb = sdf.parse(getVAL_ST_DATE_DEBUT());
		Date dateFin = sdf.parse(getVAL_ST_DATE_FIN());
		Boolean matinDebut = getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M());
		Boolean apresMidiDebut = getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM());
		Boolean matinFin = getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M());
		Boolean apresMidiFin = getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM());

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			return false;
		}

		DemandeDto dto = new DemandeDto();
		dto.setOrganisationSyndicale(orga);
		dto.setDateDebut(dateDeb);
		dto.setDateDebutAM(matinDebut);
		dto.setDateDebutPM(apresMidiDebut);
		dto.setDateFin(dateFin);
		dto.setDateFinAM(matinFin);
		dto.setDateFinPM(apresMidiFin);
		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(Integer.valueOf(ag.getIdAgent()));
		dto.setAgentWithServiceDto(agDto);
		dto.setIdTypeDemande(type.getIdRefTypeAbsence());
		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto(EnumTypeGroupeAbsence.ASA);
		dto.setGroupeAbsence(groupeDto);
		dto.setIdRefEtat(EnumEtatAbsence.SAISIE.getCode());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveDemande(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		performPB_FILTRER(request);
		return true;
	}

	public String getNOM_PB_VALIDER_CREATION_A52() {
		return "NOM_PB_VALIDER_CREATION_A52";
	}

	public boolean performPB_VALIDER_CREATION_A52(HttpServletRequest request) throws Exception {
		AgentNW ag = getAgentCreation();
		TypeAbsenceDto type = getTypeCreation();

		// on recup l'organisation syndicale
		int numOrga = (Services.estNumerique(getZone(getNOM_LB_OS_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_OS_SELECT())) : -1);
		OrganisationSyndicaleDto orga = null;
		if (numOrga != -1) {
			orga = (OrganisationSyndicaleDto) getListeOrganisationSyndicale().get(numOrga);
		} else {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "organisation syndicale"));
			return false;
		}

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
			return false;
		}
		if (getVAL_ST_DUREE().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée"));
			return false;
		}
		// heure obligatoire
		int indiceHeure = (Services.estNumerique(getVAL_LB_HEURE_SELECT()) ? Integer.parseInt(getVAL_LB_HEURE_SELECT())
				: -1);
		if (indiceHeure <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "heure"));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String heure = getListeHeure().get(Integer.valueOf(getVAL_LB_HEURE_SELECT()));
		Date dateDeb = sdf.parse(getVAL_ST_DATE_DEBUT() + " " + heure);

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			return false;
		}

		DemandeDto dto = new DemandeDto();
		dto.setOrganisationSyndicale(orga);
		dto.setDateDebut(dateDeb);
		dto.setDuree(Double.valueOf(getVAL_ST_DUREE().replace(",", ".")) * 60);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(Integer.valueOf(ag.getIdAgent()));
		dto.setAgentWithServiceDto(agDto);
		dto.setIdTypeDemande(type.getIdRefTypeAbsence());
		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto(EnumTypeGroupeAbsence.ASA);
		dto.setGroupeAbsence(groupeDto);
		dto.setIdRefEtat(EnumEtatAbsence.SAISIE.getCode());

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveDemande(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		performPB_FILTRER(request);
		return true;
	}

	public String getNOM_PB_VALIDER_CREATION_A49() {
		return "NOM_PB_VALIDER_CREATION_A49";
	}

	public boolean performPB_VALIDER_CREATION_A49(HttpServletRequest request) throws Exception {
		AgentNW ag = getAgentCreation();
		TypeAbsenceDto type = getTypeCreation();

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
			return false;
		}
		// heure obligatoire
		int indiceHeure = (Services.estNumerique(getVAL_LB_HEURE_SELECT()) ? Integer.parseInt(getVAL_LB_HEURE_SELECT())
				: -1);
		if (indiceHeure <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "heure"));
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String heure = getListeHeure().get(Integer.valueOf(getVAL_LB_HEURE_SELECT()));
		Date dateDeb = sdf.parse(getVAL_ST_DATE_DEBUT() + " " + heure);

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			return false;
		}

		DemandeDto dto = new DemandeDto();
		dto.setDateDebut(dateDeb);
		dto.setDuree(60.0);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(Integer.valueOf(ag.getIdAgent()));
		dto.setAgentWithServiceDto(agDto);
		dto.setIdTypeDemande(type.getIdRefTypeAbsence());
		dto.setIdRefEtat(EnumEtatAbsence.SAISIE.getCode());
		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto(EnumTypeGroupeAbsence.ASA);
		dto.setGroupeAbsence(groupeDto);

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveDemande(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		performPB_FILTRER(request);
		return true;
	}
}
