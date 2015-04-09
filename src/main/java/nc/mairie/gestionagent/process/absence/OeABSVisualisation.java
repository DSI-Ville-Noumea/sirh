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
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

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
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.parametrage.ReferentRh;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.parametrage.ReferentRhDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
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
import org.springframework.context.ApplicationContext;

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
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 3;

	private String[] LB_ETAT;
	private String[] LB_GROUPE;
	private String[] LB_FAMILLE;
	private String[] LB_FAMILLE_CREATION;
	private String[] LB_HEURE;
	private String[] LB_OS;
	private String[] LB_GESTIONNAIRE;

	public Hashtable<String, TreeHierarchy> hTree = null;
	private ArrayList<Service> listeServices;
	private ArrayList<EnumEtatAbsence> listeEtats;
	private ArrayList<TypeAbsenceDto> listeFamilleAbsence;
	private ArrayList<TypeAbsenceDto> listeFamilleAbsenceCreation;
	private ArrayList<RefGroupeAbsenceDto> listeGroupeAbsence;
	private ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale;
	private ArrayList<String> listeHeure;
	private ArrayList<ReferentRh> listeGestionnaire;

	private TreeMap<Integer, DemandeDto> listeAbsence;
	private HashMap<Integer, List<DemandeDto>> history = new HashMap<>();

	public String ACTION_CREATION = "Creation_absence";
	public String ACTION_CREATION_DEMANDE = "Creation_demande";
	public String ACTION_MOTIF_ANNULATION = "Motif_annulation_demande";
	public String ACTION_MOTIF_EN_ATTENTE = "Motif_mise_en_attente_demande";

	private TypeAbsenceDto typeCreation;
	private Agent agentCreation;
	private AgentDao agentDao;
	private AffectationDao affectationDao;
	private ReferentRhDao referentRhDao;

	private String typeFiltre;

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
		return Const.CHAINE_VIDE;
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		if (etatStatut() == STATUT_RECHERCHER_AGENT_DEMANDE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_DEMANDE(), agt.getNomatr().toString());
			}
		}
		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATION) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATION(), agt.getNomatr().toString());
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getReferentRhDao() == null) {
			setReferentRhDao(new ReferentRhDao((SirhDao) context.getBean("sirhDao")));
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

		// Si liste gestionnaire vide alors affectation
		if (getLB_GESTIONNAIRE() == LBVide) {
			List<ReferentRh> listeRef = getReferentRhDao().listerDistinctReferentRh();
			setListeGestionnaire((ArrayList<ReferentRh>) listeRef);
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ReferentRh ref : listeRef) {
				Agent gest = getAgentDao().chercherAgent(ref.getIdAgentReferent());
				String ligne[] = { gest.getNomAgent() + " " + gest.getPrenomAgent() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_GESTIONNAIRE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_GESTIONNAIRE_SELECT(), Const.ZERO);

		}

		// Si liste famille absence vide alors affectation
		if (getLB_FAMILLE_CREATION() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeFamilleAbsenceCreation((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto(null));

			int[] tailles = { 100 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<TypeAbsenceDto> list = getListeFamilleAbsenceCreation().listIterator(); list.hasNext();) {
				TypeAbsenceDto type = (TypeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
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
			int diffFinDeb = 14 * 60; // difference en minute entre le début et
										// la
										// fin
			int interval = 15; // interval en minute

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
			String[] a = new String[58];
			for (int j = 0; j < getListeHeure().size(); j++) {
				a[j] = getListeHeure().get(j);
			}
			setLB_HEURE(a);
		}

		// Si liste groupe absence vide alors affectation
		if (getLB_GROUPE() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeGroupeAbsence((ArrayList<RefGroupeAbsenceDto>) consuAbs.getRefGroupeAbsence());

			int[] tailles = { 100 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<RefGroupeAbsenceDto> list = getListeGroupeAbsence().listIterator(); list.hasNext();) {
				RefGroupeAbsenceDto type = (RefGroupeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_GROUPE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_GROUPE_SELECT(), Const.ZERO);
		}

	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_FILTRER_DEMANDE_A_VALIDER
			if (testerParametre(request, getNOM_PB_FILTRER_DEMANDE_A_VALIDER())) {
				return performPB_FILTRER_DEMANDE_A_VALIDER(request);
			}

			// Si clic sur le bouton PB_CALCUL_DUREE
			if (testerParametre(request, getNOM_PB_CALCUL_DUREE())) {
				return performPB_CALCUL_DUREE(request);
			}

			// Si clic sur le bouton PB_SELECT_GROUPE
			if (testerParametre(request, getNOM_PB_SELECT_GROUPE())) {
				return performPB_SELECT_GROUPE(request);
			}

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
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_DEMANDE())) {
				return performPB_VALIDER_CREATION_DEMANDE(request);
			}

			// Si clic sur les boutons du tableau
			for (Integer indiceAbs : getListeAbsence().keySet()) {
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_DEMANDE, true);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
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
		// On enleve le service selectionnée
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
			type = (TypeAbsenceDto) getListeFamilleAbsenceCreation().get(numType - 1);
		}
		// groupe
		int numGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GROUPE_SELECT())) : -1);
		RefGroupeAbsenceDto groupe = null;
		if (numGroupe != -1 && numGroupe != 0) {
			groupe = (RefGroupeAbsenceDto) getListeGroupeAbsence().get(numGroupe - 1);
		}

		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900"
				+ getVAL_ST_AGENT_DEMANDE();

		// SERVICE
		List<String> idAgentService = new ArrayList<>();
		String sigleService = getVAL_EF_SERVICE().toUpperCase();
		if (!sigleService.equals(Const.CHAINE_VIDE) && null == idAgentDemande) {
			// on cherche le code service associé
			Service siserv = Service.chercherServiceBySigle(getTransaction(), sigleService);
			String codeService = siserv.getCodService();
			// Récupération des agents
			// on recupere les sous-service du service selectionne
			ArrayList<String> listeSousService = null;
			if (!codeService.equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(), codeService);
				listeSousService = Service.listSousService(getTransaction(), serv.getSigleService());
			}

			if (!codeService.equals(Const.CHAINE_VIDE)) {
				ArrayList<String> codesServices = listeSousService;
				if (!codesServices.contains(codeService))
					codesServices.add(codeService);
				ArrayList<Agent> listAgent = getAgentDao().listerAgentAvecServicesETMatricules(codesServices, null,
						null);
				for (Agent ag : listAgent) {
					if (!idAgentService.contains(ag.getIdAgent().toString())) {
						idAgentService.add(ag.getIdAgent().toString());
					}
				}
			}
		}

		if (idAgentService.size() >= 1000) {
			// "ERR501",
			// "La sélection des filtres engendre plus de 1000 agents. Merci de reduire la sélection."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR501"));
			return false;
		}

		// GESTIONNAIRE
		int numGestionnaire = (Services.estNumerique(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) : -1);
		ReferentRh gestionnaire = null;
		if (numGestionnaire != -1 && numGestionnaire != 0) {
			gestionnaire = (ReferentRh) getListeGestionnaire().get(numGestionnaire - 1);
		}
		if (sigleService.equals(Const.CHAINE_VIDE) && null == idAgentDemande && gestionnaire != null) {

			List<ReferentRh> listServiceRH = null;
			try {
				listServiceRH = getReferentRhDao().listerServiceAvecReferentRh(gestionnaire.getIdAgentReferent());
			} catch (NumberFormatException e) {
				getTransaction().declarerErreur("Une erreur de saisie sur le gestionnaire est survenue.");
				return false;
			}
			if (null == listServiceRH || listServiceRH.isEmpty()) {
				getTransaction().declarerErreur("Le gestionnaire saisi n'est pas un référent RH.");
				return false;
			}
			// Récupération des agents
			// on recupere les sous-service du service selectionne
			List<String> listeService = new ArrayList<String>();
			for (ReferentRh service : listServiceRH) {
				listeService.add(service.getServi());
			}
			List<String> listeSousServiceTmp = new ArrayList<String>();
			for (String service : listeService) {
				Service serv = Service.chercherService(getTransaction(), service);
				listeSousServiceTmp.addAll(Service.listSousService(getTransaction(), serv.getSigleService()));
			}
			// on trie la liste des sous service pour supprimer les doublons
			ArrayList<String> listeSousService = new ArrayList<String>();
			for (String sousService : listeSousServiceTmp) {
				if (!listeSousService.contains(sousService)) {
					listeSousService.add(sousService);
				}
			}

			List<Agent> listAgent = getAgentDao().listerAgentAvecServicesETMatricules(listeSousService, null, null);
			for (Agent ag : listAgent) {
				if (!idAgentService.contains(ag.getIdAgent().toString())) {
					idAgentService.add(ag.getIdAgent().toString());
				}
			}
		}

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		List<DemandeDto> listeDemande = t.getListeDemandes(dateMin, dateMax, etat == null ? null : etat.getCode(),
				type == null ? null : type.getIdRefTypeAbsence(),
				idAgentDemande == null ? null : Integer.valueOf(idAgentDemande),
				groupe == null ? null : groupe.getIdRefGroupeAbsence(), false, idAgentService);

		logger.debug("Taille liste absences : " + listeDemande.size());
		setListeAbsence((ArrayList<DemandeDto>) listeDemande);

		// redmine #13453
		// loadHistory();

		afficheListeAbsence();
		if (299 < listeDemande.size()) {
			getTransaction().declarerErreur(
					"Attention, les demandes sont limitées a 300 résultats. Utiliser les filtres.");
		}
		setTypeFiltre("GLOBAL");

		return true;
	}

	private void afficheListeAbsence() throws Exception {

		for (Map.Entry<Integer, DemandeDto> absMap : getListeAbsence().entrySet()) {
			DemandeDto abs = absMap.getValue();
			Integer i = absMap.getKey();
			try {
				Agent ag = getAgentDao().chercherAgent(abs.getAgentWithServiceDto().getIdAgent());
				Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), ag);
				if (carr == null || carr.getNoMatricule() == null) {
					getTransaction().traiterErreur();
				}
				String statut = carr == null ? "&nbsp;" : Carriere.getStatutCarriere(carr.getCodeCategorie());

				TypeAbsenceDto t = new TypeAbsenceDto();
				t.setIdRefTypeAbsence(abs.getIdTypeDemande());
				TypeAbsenceDto type = getListeFamilleAbsenceCreation().get(getListeFamilleAbsenceCreation().indexOf(t));

				addZone(getNOM_ST_MATRICULE(i), null != ag ? ag.getNomatr().toString() : "");
				addZone(getNOM_ST_AGENT(i), ag.getNomAgent() + " " + ag.getPrenomAgent());
				addZone(getNOM_ST_INFO_AGENT(i), "<br/>" + statut);
				addZone(getNOM_ST_TYPE(i), type.getLibelle() + "<br/>" + sdf.format(abs.getDateDemande()));

				String debutMAM = abs.isDateDebutAM() ? "M" : abs.isDateDebutPM() ? "A" : Const.CHAINE_VIDE;
				addZone(getNOM_ST_DATE_DEB(i),
						sdf.format(abs.getDateDebut()) + "<br/>"
								+ (debutMAM.equals(Const.CHAINE_VIDE) ? hrs.format(abs.getDateDebut()) : debutMAM));
				if (abs.getDateFin() != null) {
					String finMAM = abs.isDateFinAM() ? "M" : abs.isDateFinPM() ? "A" : Const.CHAINE_VIDE;
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
						|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
						|| abs.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
					if (abs.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j"
								+ (abs.isSamediOffert() ? " +S" : ""));
					} else {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					}
				} else if (abs.getGroupeAbsence() != null
						&& abs.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP
								.getValue()) {
					if (abs.getTypeSaisi().isChkDateDebut()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					} else if (abs.getTypeSaisi().isCalendarHeureDebut()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : getHeureMinute(abs.getDuree()
								.intValue()));
					} else if (abs.getTypeSaisi().isCalendarDateDebut()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					} else {
						addZone(getNOM_ST_DUREE(i), "&nbsp;");
					}
				} else {
					addZone(getNOM_ST_DUREE(i), "&nbsp;");
				}
				String motif = "";
				if (null != abs.getMotif()) {
					motif += " " + abs.getMotif();
					if (null != abs.getCommentaire()) {
						motif += " - ";
					}
				}
				if (null != abs.getCommentaire()) {
					motif += abs.getCommentaire();
				}
				addZone(getNOM_ST_MOTIF(i), motif);
				addZone(getNOM_ST_ETAT(i), EnumEtatAbsence.getValueEnumEtatAbsence(abs.getIdRefEtat()));
			} catch (Exception e) {
				continue;
			}
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());
		setStatut(STATUT_RECHERCHER_AGENT_CREATION, true);
		return true;
	}

	public String getNOM_PB_CREATION() {
		return "NOM_PB_CREATION";
	}

	public boolean performPB_CREATION(HttpServletRequest request) throws Exception {
		String idAgent = Const.CHAINE_VIDE;
		if (getVAL_ST_AGENT_CREATION().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
			return false;
		} else {
			idAgent = "900" + getVAL_ST_AGENT_CREATION();
			try {
				Agent agent = getAgentDao().chercherAgent(Integer.valueOf(idAgent));
				setAgentCreation(agent);
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", idAgent));
				return false;
			}
		}
		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) : -1);
		TypeAbsenceDto type = null;
		if (numType != -1) {
			type = (TypeAbsenceDto) getListeFamilleAbsenceCreation().get(numType);
			if (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.CONGE.getCode().toString())) {
				// on cherche la base horaire absence de l'agent
				Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(
						getAgentCreation().getIdAgent());
				if (aff == null || aff.getIdBaseHoraireAbsence() == null) {
					// "ERR805",
					// "L'agent @ n'a pas de base horaire d'absence. Merci de la renseigner dans l'affectation de l'agent."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR805", idAgent));
					return false;
				}
				SirhAbsWSConsumer consu = new SirhAbsWSConsumer();
				type = consu.getTypeAbsence(aff.getIdBaseHoraireAbsence());
			}
			setTypeCreation(type);
		}

		// On nomme l'action
		if ((type != null && (type.getIdRefTypeAbsence().toString()
				.equals(EnumTypeAbsence.ASA_A48.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A49.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A50.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A52.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A53.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A54.getCode().toString()) || type
				.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A55.getCode().toString())))
				|| (type != null && type.getGroupeAbsence() != null && (type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP
						.getValue() || type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_ANNUELS
						.getValue()))) {

			addZone(getNOM_ST_ACTION(), ACTION_CREATION_DEMANDE);
		} else {
			getTransaction().declarerErreur("Cette famille ne peut être saisie dans SIRH.");
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
		addZone(getNOM_ST_DATE_REPRISE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_MOTIF_CREATION(), Const.CHAINE_VIDE);
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
		addZone(getNOM_ST_DUREE_MIN(), Const.CHAINE_VIDE);
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

	public String getNOM_ST_DUREE_MIN() {
		return "NOM_ST_DUREE_MIN";
	}

	public String getVAL_ST_DUREE_MIN() {
		return getZone(getNOM_ST_DUREE_MIN());
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

	public String getNOM_ST_DATE_REPRISE() {
		return "NOM_ST_DATE_REPRISE";
	}

	public String getVAL_ST_DATE_REPRISE() {
		return getZone(getNOM_ST_DATE_REPRISE());
	}

	public String getNOM_RG_FIN_MAM() {
		return "NOM_RG_FIN_MAM";
	}

	public String getVAL_RG_FIN_MAM() {
		return getZone(getNOM_RG_FIN_MAM());
	}

	public String getNOM_PB_VALIDER_CREATION_DEMANDE() {
		return "NOM_PB_VALIDER_CREATION_DEMANDE";
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
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
				try {
					agent = getAgentDao().chercherAgentParMatricule(
							radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

	public TypeAbsenceDto getTypeCreation() {
		return typeCreation;
	}

	public void setTypeCreation(TypeAbsenceDto typeCreation) {
		this.typeCreation = typeCreation;
	}

	public Agent getAgentCreation() {
		return agentCreation;
	}

	public void setAgentCreation(Agent agentCreation) {
		this.agentCreation = agentCreation;
	}

	public TreeMap<Integer, DemandeDto> getListeAbsence() {
		return listeAbsence == null ? new TreeMap<Integer, DemandeDto>() : listeAbsence;
	}

	public void setListeAbsence(ArrayList<DemandeDto> listeAbsenceAjout) {
		listeAbsence = new TreeMap<>();
		int i = 0;
		for (DemandeDto dem : listeAbsenceAjout) {
			listeAbsence.put(i, dem);
			i++;
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

	public String getHistory(int absId, int idDemande) {
		
		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		history.put(absId, t.getVisualisationHistory(idDemande));

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
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
				if (p.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
					duree = p.getDuree().toString() + "j" + (p.isSamediOffert() ? " +S" : "");
				} else {
					duree = p.getDuree().toString() + "j";
				}
			} else if (p.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode()) {
				duree = getHeureMinute(p.getDuree().intValue());
			} else if (p.getGroupeAbsence() != null
					&& p.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
				if (p.getTypeSaisi().isChkDateDebut()) {
					duree = p.getDuree() == null ? "&nbsp;" : p.getDuree().toString() + "j";
				} else if (p.getTypeSaisi().isCalendarHeureDebut()) {
					duree = p.getDuree() == null ? "&nbsp;" : getHeureMinute(p.getDuree().intValue());
				} else if (p.getTypeSaisi().isCalendarDateDebut()) {
					duree = p.getDuree() == null ? "&nbsp;" : p.getDuree().toString() + "j";
				}
			}
			ret[index][3] = duree;
			ret[index][4] = p.getMotif() == null ? "&nbsp;" : p.getMotif();
			ret[index][5] = EnumEtatAbsence.getValueEnumEtatAbsence(p.getIdRefEtat());
			ret[index][6] = formatDate(p.getDateSaisie()) + " " + formatHeure(p.getDateSaisie()) 
					+ "<br/>" + p.getAgentWithServiceDto().getPrenom() + " " + p.getAgentWithServiceDto().getNom() 
					+ " (" + new Integer(p.getAgentWithServiceDto().getIdAgent()-9000000).toString() + ")";
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
			return Const.CHAINE_VIDE;
		}
	}

	private String formatHeure(Date d) {
		if (d != null) {
			return hrs.format(d);
		} else {
			return "00:00";
		}
	}

	public String getNOM_PB_DUPLIQUER(int i) {
		return "NOM_PB_DUPLIQUER" + i;
	}

	public boolean performPB_DUPLIQUER(HttpServletRequest request, int idDemande) throws Exception {
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		TypeAbsenceDto t = new TypeAbsenceDto();
		t.setIdRefTypeAbsence(dem.getIdTypeDemande());
		TypeAbsenceDto type = getListeFamilleAbsenceCreation().get(getListeFamilleAbsenceCreation().indexOf(t));

		if (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.CONGE.getCode().toString())) {
			// on cherche la base horaire absence de l'agent
			Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(
					dem.getAgentWithServiceDto().getIdAgent());
			if (aff == null || aff.getIdBaseHoraireAbsence() == null) {
				// "ERR805",
				// "L'agent @ n'a pas de base horaire d'absence. Merci de la renseigner dans l'affectation de l'agent."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR805", dem.getAgentWithServiceDto().getIdAgent().toString()));
				return false;
			}
			SirhAbsWSConsumer consu = new SirhAbsWSConsumer();
			type = consu.getTypeAbsence(aff.getIdBaseHoraireAbsence());
		}
		setTypeCreation(type);

		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), String.valueOf(getListeFamilleAbsenceCreation().indexOf(type)));

		Agent agt = getAgentDao().chercherAgent(dem.getAgentWithServiceDto().getIdAgent());
		addZone(getNOM_ST_AGENT_CREATION(), agt.getNomatr().toString());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// date de debut
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCalendarDateDebut())
				|| (null != type.getTypeSaisiCongeAnnuelDto() && type.getTypeSaisiCongeAnnuelDto()
						.isCalendarDateDebut())) {
			addZone(getNOM_ST_DATE_DEBUT(), sdf.format(dem.getDateDebut()));
		}
		// date de fin
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCalendarDateFin())
				|| (null != type.getTypeSaisiCongeAnnuelDto() && type.getTypeSaisiCongeAnnuelDto().isCalendarDateFin())) {
			addZone(getNOM_ST_DATE_FIN(), sdf.format(dem.getDateFin()));
		}
		// checkbox
		addZone(getNOM_RG_DEBUT_MAM(), dem.isDateDebutAM() ? getNOM_RB_M() : getNOM_RB_AM());
		addZone(getNOM_RG_FIN_MAM(), dem.isDateFinAM() ? getNOM_RB_M() : getNOM_RB_AM());

		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH:mm");
		// HEURE DEBUT
		if (null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCalendarHeureDebut()) {
			Integer resHeure = getListeHeure().indexOf(sdfHeure.format(dem.getDateDebut()));
			addZone(getNOM_LB_HEURE_DEBUT_SELECT(), resHeure.toString());
		}
		// HEURE FIN
		if (null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCalendarHeureFin()) {
			Integer resHeure = getListeHeure().indexOf(sdfHeure.format(dem.getDateFin()));
			addZone(getNOM_LB_HEURE_FIN_SELECT(), resHeure.toString());
		}
		// organisation syndicale
		if (null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCompteurCollectif()) {
			// on recup l'organisation syndicale
			OrganisationSyndicaleDto orga = null;
			if (dem.getOrganisationSyndicale() != null) {
				orga = dem.getOrganisationSyndicale();
			}
			addZone(getNOM_LB_OS_SELECT(),
					orga == null ? Const.ZERO : String.valueOf(getListeOrganisationSyndicale().indexOf(orga)));
		}
		// /////////////// MOTIF ////////////////////
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isMotif())
				|| (null != type.getTypeSaisiCongeAnnuelDto())) {
			addZone(getNOM_ST_MOTIF_CREATION(), dem.getMotif() == null ? Const.CHAINE_VIDE : dem.getMotif().trim());
		}

		// /////////////// DUREE ////////////////////
		if (null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isDuree()) {
			String dureeHeures = (dem.getDuree() / 60) == 0 ? Const.CHAINE_VIDE : new Double(dem.getDuree()/ 60).intValue() + Const.CHAINE_VIDE;
			String dureeMinutes = (dem.getDuree() % 60) == 0 ? Const.CHAINE_VIDE : new Double(dem.getDuree()% 60).intValue() + Const.CHAINE_VIDE;
			addZone(getNOM_ST_DUREE(), dureeHeures);
			addZone(getNOM_ST_DUREE_MIN(), dureeMinutes);
		} else if (null != type.getTypeSaisiCongeAnnuelDto()) {
			String duree = dem.getDuree() == 0 ? Const.CHAINE_VIDE : dem.getDuree() + Const.CHAINE_VIDE;
			addZone(getNOM_ST_DUREE(), duree);
		}

		// /////////////// PIECE JOINTE ////////////////////
		// TODO

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
		if (getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_VALIDER(request);
		}
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
		Agent ag = getAgentDao().chercherAgent(dem.getAgentWithServiceDto().getIdAgent());
		// Si ASA ou CONGES_EXCEP et etat=validé ou prise,
		// alors un motif est obligatoire
		if (((dem.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.AS.getValue() || dem
				.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) && (dem
				.getIdRefEtat() == EnumEtatAbsence.APPROUVE.getCode() 
				// #14696 ajout de l etat A VALIDER car erreur lors de la reprise de donnees des conges exceptionnels mis  l etat A VALIDER au lieu de SAISI ou APPROUVE
				|| dem.getIdRefEtat() == EnumEtatAbsence.A_VALIDER.getCode()))
				|| (dem.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue())
				&& dem.getIdRefEtat() == EnumEtatAbsence.A_VALIDER.getCode()) {
			// "ERR803",
			// "Pour @ cette demande, merci de renseigner un motif."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR803", "mettre en attente"));
			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(dem.getIdTypeDemande());
			String info = "Demande "
					+ getListeFamilleAbsenceCreation().get(getListeFamilleAbsenceCreation().indexOf(t)).getLibelle()
					+ " de l'agent " + ag.getNomatr() + " du " + sdf.format(dem.getDateDebut()) + ".";
			addZone(getNOM_ST_INFO_MOTIF_EN_ATTENTE(), info);
			addZone(getNOM_ST_MOTIF_EN_ATTENTE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_ID_DEMANDE_EN_ATTENTE(), new Integer(idDemande).toString());
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
		if (getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_VALIDER(request);
		}
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
		Agent agentConnecte = getAgentConnecte(request);
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
			}

			String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
					.deepSerialize(listDto);
			ReturnMessageDto message = t.setAbsState(agentConnecte.getIdAgent(), json);

			for (DemandeDto d : dem) {
				refreshHistory(d.getIdDemande());
			}

			if (message.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : message.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
			}
			if (message.getInfos().size() > 0) {
				String inf = Const.CHAINE_VIDE;
				for (String info : message.getInfos()) {
					inf += " " + info;
				}
				getTransaction().declarerErreur(inf);
			}
			if (null != getTypeFiltre() && getTypeFiltre().equals("GLOBAL")) {
				performPB_FILTRER(request);
			} else {
				performPB_FILTRER_DEMANDE_A_VALIDER(request);
			}
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

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		Agent ag = getAgentDao().chercherAgent(dem.getAgentWithServiceDto().getIdAgent());

		// "ERR803",
		// "Pour @ cette demande, merci de renseigner un motif."
		getTransaction().declarerErreur(MessageUtils.getMessage("ERR803", "annuler"));
		TypeAbsenceDto t = new TypeAbsenceDto();
		t.setIdRefTypeAbsence(dem.getIdTypeDemande());
		String info = "Demande "
				+ getListeFamilleAbsenceCreation().get(getListeFamilleAbsenceCreation().indexOf(t)).getLibelle()
				+ " de l'agent " + ag.getNomatr() + " du " + sdf.format(dem.getDateDebut()) + ".";
		addZone(getNOM_ST_INFO_MOTIF_ANNULATION(), info);
		addZone(getNOM_ST_MOTIF_ANNULATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ID_DEMANDE_ANNULATION(), new Integer(idDemande).toString());
		addZone(getNOM_ST_ACTION(), ACTION_MOTIF_ANNULATION);
		return false;
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
		if (getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_VALIDER(request);
		}
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
		if (getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_VALIDER(request);
		}
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

	public String[] getVAL_LB_HEURE() {
		return getLB_HEURE();
	}

	public String getNOM_LB_HEURE_DEBUT() {
		return "NOM_LB_HEURE_DEBUT";
	}

	public String getNOM_LB_HEURE_DEBUT_SELECT() {
		return "NOM_LB_HEURE_DEBUT_SELECT";
	}

	public String getVAL_LB_HEURE_DEBUT_SELECT() {
		return getZone(getNOM_LB_HEURE_DEBUT_SELECT());
	}

	public String getNOM_LB_HEURE_FIN() {
		return "NOM_LB_HEURE_FIN";
	}

	public String getNOM_LB_HEURE_FIN_SELECT() {
		return "NOM_LB_HEURE_FIN_SELECT";
	}

	public String getVAL_LB_HEURE_FIN_SELECT() {
		return getZone(getNOM_LB_HEURE_FIN_SELECT());
	}

	public String getNOM_ST_MOTIF_CREATION() {
		return "NOM_ST_MOTIF_CREATION";
	}

	public String getVAL_ST_MOTIF_CREATION() {
		return getZone(getNOM_ST_MOTIF_CREATION());
	}

	public boolean performPB_VALIDER_CREATION_DEMANDE(HttpServletRequest request) throws Exception {

		Agent ag = getAgentCreation();
		TypeAbsenceDto type = getTypeCreation();

		DemandeDto dto = new DemandeDto();

		Date dateDebut = null;
		Date dateFin = null;
		Date dateReprise = null;

		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			return false;
		}
		if (type.getTypeSaisiDto() != null) {
			// /////////////// DATE DEBUT ////////////////////
			if (type.getTypeSaisiDto().isCalendarDateDebut()) {
				if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
					return false;
				}
				dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT());
			}
			// /////////////// HEURE DEBUT ////////////////////
			if (type.getTypeSaisiDto().isCalendarHeureDebut()) {
				// heure obligatoire
				int indiceHeureDebut = (Services.estNumerique(getVAL_LB_HEURE_DEBUT_SELECT()) ? Integer
						.parseInt(getVAL_LB_HEURE_DEBUT_SELECT()) : -1);
				if (indiceHeureDebut < 0) {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "heure de début"));
					return false;
				}
				String heureDebut = getListeHeure().get(Integer.valueOf(getVAL_LB_HEURE_DEBUT_SELECT()));
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT() + " " + heureDebut);
			}
			// /////////////// RADIO BOUTON DEBUT ////////////////////
			if (type.getTypeSaisiDto().isChkDateDebut()) {
				if (null == getVAL_RG_DEBUT_MAM() || Const.CHAINE_VIDE.equals(getVAL_RG_DEBUT_MAM())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "matin ou après-midi"));
					return false;
				}
				dto.setDateDebutAM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M()));
				dto.setDateDebutPM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM()));
			}
			// /////////////// DUREE ////////////////////
			if (type.getTypeSaisiDto().isDuree()) {
				if (getVAL_ST_DUREE().equals(Const.CHAINE_VIDE)
						&& getVAL_ST_DUREE_MIN().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée"));
					return false;
				}
				String dureeHeure = null != getVAL_ST_DUREE() ? getVAL_ST_DUREE() : "0";
				String dureeMinutes = null != getVAL_ST_DUREE_MIN() ? 1 == getVAL_ST_DUREE_MIN().length() ? "0"+getVAL_ST_DUREE_MIN() : getVAL_ST_DUREE_MIN() : "00";
				
				dto.setDuree(Double.valueOf(dureeHeure + "." + dureeMinutes));
			}
			// /////////////// DATE FIN ////////////////////
			if (type.getTypeSaisiDto().isCalendarDateFin()) {
				if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
					return false;
				}
				dateFin = sdf.parse(getVAL_ST_DATE_FIN());
			}
			// /////////////// HEURE FIN ////////////////////
			if (type.getTypeSaisiDto().isCalendarHeureFin()) {
				// heure obligatoire
				int indiceHeureFin = (Services.estNumerique(getVAL_LB_HEURE_FIN_SELECT()) ? Integer
						.parseInt(getVAL_LB_HEURE_FIN_SELECT()) : -1);
				if (indiceHeureFin <= 0) {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "heure de fin"));
					return false;
				}
				String heureFin = getListeHeure().get(Integer.valueOf(getVAL_LB_HEURE_FIN_SELECT()));
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				dateFin = sdf.parse(getVAL_ST_DATE_FIN() + " " + heureFin);
			}
			// /////////////// RADIO BOUTON FIN ////////////////////
			if (type.getTypeSaisiDto().isChkDateFin()) {
				if (null == getVAL_RG_FIN_MAM() || Const.CHAINE_VIDE.equals(getVAL_RG_FIN_MAM())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "matin ou après-midi"));
					return false;
				}
				dto.setDateFinAM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M()));
				dto.setDateFinPM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM()));
			}
			// /////////////// ORGANISATION SYNDICALE ////////////////////
			if (type.getTypeSaisiDto().isCompteurCollectif()) {
				int numOrga = (Services.estNumerique(getZone(getNOM_LB_OS_SELECT())) ? Integer
						.parseInt(getZone(getNOM_LB_OS_SELECT())) : -1);
				OrganisationSyndicaleDto orgaSynd = null;
				if (numOrga != -1) {
					orgaSynd = (OrganisationSyndicaleDto) getListeOrganisationSyndicale().get(numOrga);
					dto.setOrganisationSyndicale(orgaSynd);
				} else {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "organisation syndicale"));
					return false;
				}
			}
			// /////////////// MOTIF ////////////////////
			if (type.getTypeSaisiDto().isMotif()) {
				if (null == getVAL_ST_MOTIF_CREATION() || Const.CHAINE_VIDE.equals(getVAL_ST_MOTIF_CREATION().trim())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
					return false;
				}
				dto.setCommentaire(getVAL_ST_MOTIF_CREATION());
			}
			// /////////////// PIECE JOINTE ////////////////////
			if (type.getTypeSaisiDto().isPieceJointe()) {
				// TODO
			}
		} else if (type.getTypeSaisiCongeAnnuelDto() != null) {
			// /////////////// DATE DEBUT ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isCalendarDateDebut()) {
				if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de debut"));
					return false;
				}
				dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT());
			}
			// /////////////// RADIO BOUTON DEBUT ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isChkDateDebut()) {
				if (null == getVAL_RG_DEBUT_MAM() || Const.CHAINE_VIDE.equals(getVAL_RG_DEBUT_MAM())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "matin ou après-midi"));
					return false;
				}
				dto.setDateDebutAM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M()));
				dto.setDateDebutPM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM()));
			}
			// /////////////// DATE FIN ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isCalendarDateFin()) {
				if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
					return false;
				}
				dateFin = sdf.parse(getVAL_ST_DATE_FIN());
			}
			// /////////////// RADIO BOUTON FIN ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isChkDateFin()) {
				if (null == getVAL_RG_FIN_MAM() || Const.CHAINE_VIDE.equals(getVAL_RG_FIN_MAM())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "matin ou après-midi"));
					return false;
				}
				dto.setDateFinAM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M()));
				dto.setDateFinPM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM()));
			}
			// /////////////// DATE REPRISE ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isCalendarDateReprise()) {
				if (getVAL_ST_DATE_REPRISE().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de reprise"));
					return false;
				}
				dateReprise = sdf.parse(getVAL_ST_DATE_REPRISE());
			}
			// /////////////// MOTIF ////////////////////
			if (null == getVAL_ST_MOTIF_CREATION() || Const.CHAINE_VIDE.equals(getVAL_ST_MOTIF_CREATION().trim())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
				return false;
			}
			dto.setCommentaire(getVAL_ST_MOTIF_CREATION());

		}

		dto.setDateDebut(dateDebut);
		dto.setDateFin(dateFin);
		dto.setDateReprise(dateReprise);
		dto.setTypeSaisi(getTypeCreation().getTypeSaisiDto());
		dto.setTypeSaisiCongeAnnuel(getTypeCreation().getTypeSaisiCongeAnnuelDto());

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(ag.getIdAgent());
		dto.setAgentWithServiceDto(agDto);

		dto.setIdTypeDemande(type.getIdRefTypeAbsence());

		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto(type.getGroupeAbsence().getIdRefGroupeAbsence());
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
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (srm.getInfos().size() > 0) {
			String info = Const.CHAINE_VIDE;
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
			getTransaction().declarerErreur(info);
		}
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// performPB_FILTRER(request);
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

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	private String[] getLB_GROUPE() {
		if (LB_GROUPE == null)
			LB_GROUPE = initialiseLazyLB();
		return LB_GROUPE;
	}

	private void setLB_GROUPE(String[] newLB_GROUPE) {
		LB_GROUPE = newLB_GROUPE;
	}

	public String getNOM_LB_GROUPE() {
		return "NOM_LB_GROUPE";
	}

	public String getNOM_LB_GROUPE_SELECT() {
		return "NOM_LB_GROUPE_SELECT";
	}

	public String[] getVAL_LB_GROUPE() {
		return getLB_GROUPE();
	}

	public String getVAL_LB_GROUPE_SELECT() {
		return getZone(getNOM_LB_GROUPE_SELECT());
	}

	public ArrayList<RefGroupeAbsenceDto> getListeGroupeAbsence() {
		return listeGroupeAbsence;
	}

	public void setListeGroupeAbsence(ArrayList<RefGroupeAbsenceDto> listeGroupeAbsence) {
		this.listeGroupeAbsence = listeGroupeAbsence;
	}

	public String getNOM_PB_SELECT_GROUPE() {
		return "NOM_PB_SELECT_GROUPE";
	}

	public boolean performPB_SELECT_GROUPE(HttpServletRequest request) throws Exception {

		// groupe
		int numGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GROUPE_SELECT())) : -1);
		RefGroupeAbsenceDto groupe = null;
		if (numGroupe != -1 && numGroupe != 0) {
			groupe = (RefGroupeAbsenceDto) getListeGroupeAbsence().get(numGroupe - 1);
		}

		// on charge les familles
		if (groupe != null) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeFamilleAbsence((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto(groupe
					.getIdRefGroupeAbsence()));
		} else {
			setListeFamilleAbsence(null);
		}

		int[] tailles = { 100 };
		FormateListe aFormat = new FormateListe(tailles);
		for (ListIterator<TypeAbsenceDto> list = getListeFamilleAbsence().listIterator(); list.hasNext();) {
			TypeAbsenceDto type = (TypeAbsenceDto) list.next();
			String ligne[] = { type.getLibelle() };

			aFormat.ajouteLigne(ligne);
		}
		setLB_FAMILLE(aFormat.getListeFormatee(true));
		addZone(getNOM_LB_FAMILLE_SELECT(), Const.ZERO);
		return true;
	}

	public ArrayList<TypeAbsenceDto> getListeFamilleAbsenceCreation() {
		return listeFamilleAbsenceCreation;
	}

	public void setListeFamilleAbsenceCreation(ArrayList<TypeAbsenceDto> listeFamilleAbsenceCreation) {
		this.listeFamilleAbsenceCreation = listeFamilleAbsenceCreation;
	}

	public String getNOM_PB_FILTRER_DEMANDE_A_VALIDER() {
		return "NOM_PB_FILTRER_DEMANDE_A_VALIDER";
	}

	public boolean performPB_FILTRER_DEMANDE_A_VALIDER(HttpServletRequest request) throws Exception {

		// GESTIONNAIRE
		int numGestionnaire = (Services.estNumerique(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) : -1);
		ReferentRh gestionnaire = null;
		if (numGestionnaire != -1 && numGestionnaire != 0) {
			gestionnaire = (ReferentRh) getListeGestionnaire().get(numGestionnaire - 1);
		}
		List<String> idAgentService = new ArrayList<>();
		if (gestionnaire != null) {
			List<ReferentRh> listServiceRH = null;
			try {
				listServiceRH = getReferentRhDao().listerServiceAvecReferentRh(gestionnaire.getIdAgentReferent());
			} catch (NumberFormatException e) {
				getTransaction().declarerErreur("Une erreur de saisie sur le gestionnaire est survenue.");
				return false;
			}
			if (null == listServiceRH || listServiceRH.isEmpty()) {
				getTransaction().declarerErreur("Le gestionnaire saisi n'est pas un référent RH.");
				return false;
			}
			// Récupération des agents
			// on recupere les sous-service du service selectionne
			List<String> listeService = new ArrayList<String>();
			for (ReferentRh service : listServiceRH) {
				listeService.add(service.getServi());
			}
			List<String> listeSousServiceTmp = new ArrayList<String>();
			for (String service : listeService) {
				Service serv = Service.chercherService(getTransaction(), service);
				listeSousServiceTmp.addAll(Service.listSousService(getTransaction(), serv.getSigleService()));
			}
			// on trie la liste des sous service pour supprimer les doublons
			ArrayList<String> listeSousService = new ArrayList<String>();
			for (String sousService : listeSousServiceTmp) {
				if (!listeSousService.contains(sousService)) {
					listeSousService.add(sousService);
				}
			}

			List<Agent> listAgent = getAgentDao().listerAgentAvecServicesETMatricules(listeSousService, null, null);
			for (Agent ag : listAgent) {
				if (!idAgentService.contains(ag.getIdAgent().toString())) {
					idAgentService.add(ag.getIdAgent().toString());
				}
			}
		}
		
		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		List<DemandeDto> listeDemande = t.getListeDemandes(null, null, null, null, null, null, true, idAgentService);
		logger.debug("Taille liste absences : " + listeDemande.size());

		setListeAbsence((ArrayList<DemandeDto>) listeDemande);

		// redmine #13453
		// loadHistory();

		afficheListeAbsence();

		setTypeFiltre("VALIDER");

		return true;
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public String getNOM_PB_CALCUL_DUREE() {
		return "NOM_PB_CALCUL_DUREE";
	}

	public boolean performPB_CALCUL_DUREE(HttpServletRequest request) throws Exception {
		if (!getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)
				&& (!getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE) || !getVAL_ST_DATE_REPRISE().equals(
						Const.CHAINE_VIDE))) {
			AgentWithServiceDto agentdto = new AgentWithServiceDto();
			agentdto.setIdAgent(getAgentCreation().getIdAgent());
			DemandeDto demandeCreation = new DemandeDto();
			demandeCreation.setAgentWithServiceDto(agentdto);
			demandeCreation.setDateDebut(sdf.parse(getVAL_ST_DATE_DEBUT()));
			demandeCreation.setDateFin(getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE) ? null : sdf
					.parse(getVAL_ST_DATE_FIN()));
			demandeCreation.setDateReprise(getVAL_ST_DATE_REPRISE().equals(Const.CHAINE_VIDE) ? null : sdf
					.parse(getVAL_ST_DATE_REPRISE()));
			demandeCreation.setDateDebutAM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M()));
			demandeCreation.setDateDebutPM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM()));
			demandeCreation.setDateFinAM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M()));
			demandeCreation.setDateFinPM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM()));
			String res = getCalculDureeCongeAnnuel(getTypeCreation().getTypeSaisiCongeAnnuelDto()
					.getCodeBaseHoraireAbsence(), demandeCreation);

			addZone(getNOM_ST_DUREE(), res);
		}
		return true;
	}

	public String getCalculDureeCongeAnnuel(String codeBaseHoraireAbsence, DemandeDto demandeDto) {
		if (demandeDto.getDateDebut() != null
				&& (demandeDto.getDateFin() != null || demandeDto.getDateReprise() != null)) {
			demandeDto.setTypeSaisiCongeAnnuel(getTypeCreation().getTypeSaisiCongeAnnuelDto());
			SirhAbsWSConsumer absWsConsumer = new SirhAbsWSConsumer();
			DemandeDto dureeDto = absWsConsumer.getDureeCongeAnnuel(demandeDto);
			return dureeDto.getDuree().toString();
		}
		return null;
	}

	public String getTypeFiltre() {
		return typeFiltre;
	}

	public void setTypeFiltre(String typeFiltre) {
		this.typeFiltre = typeFiltre;
	}

	public ReferentRhDao getReferentRhDao() {
		return referentRhDao;
	}

	public void setReferentRhDao(ReferentRhDao referentRhDao) {
		this.referentRhDao = referentRhDao;
	}

	private String[] getLB_GESTIONNAIRE() {
		if (LB_GESTIONNAIRE == null)
			LB_GESTIONNAIRE = initialiseLazyLB();
		return LB_GESTIONNAIRE;
	}

	private void setLB_GESTIONNAIRE(String[] newLB_GESTIONNAIRE) {
		LB_GESTIONNAIRE = newLB_GESTIONNAIRE;
	}

	public String getNOM_LB_GESTIONNAIRE() {
		return "NOM_LB_GESTIONNAIRE";
	}

	public String getNOM_LB_GESTIONNAIRE_SELECT() {
		return "NOM_LB_GESTIONNAIRE_SELECT";
	}

	public String[] getVAL_LB_GESTIONNAIRE() {
		return getLB_GESTIONNAIRE();
	}

	public String getVAL_LB_GESTIONNAIRE_SELECT() {
		return getZone(getNOM_LB_GESTIONNAIRE_SELECT());
	}

	public ArrayList<ReferentRh> getListeGestionnaire() {
		return listeGestionnaire;
	}

	public void setListeGestionnaire(ArrayList<ReferentRh> listeGestionnaire) {
		this.listeGestionnaire = listeGestionnaire;
	}

}
