package nc.mairie.gestionagent.process.absence;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

import flexjson.JSONSerializer;
import nc.mairie.comparator.DemandeDtoDateDebutComparator;
import nc.mairie.comparator.DemandeDtoDateDeclarationComparator;
import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.ControleMedicalDto;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.DemandeEtatChangeDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.dto.PieceJointeDto;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.RefTypeDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.RefEtatDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.parametrage.ReferentRh;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.parametrage.ReferentRhDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.cmis.IAlfrescoCMISService;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeABSVisualisation extends BasicProcess {
	/**
	 * 
	 */
	private static final long					serialVersionUID					= 1L;

	private SimpleDateFormat					sdf									= new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat					hrs									= new SimpleDateFormat("HH:mm");

	public String								focus								= null;
	private Logger								logger								= LoggerFactory.getLogger(OeABSVisualisation.class);

	public static final int						STATUT_RECHERCHER_AGENT_DEMANDE		= 1;
	public static final int						STATUT_RECHERCHER_AGENT_CREATION	= 3;

	private String[]							LB_ETAT;
	private String[]							LB_GROUPE;
	private String[]							LB_GROUPE_CREATE;
	private String[]							LB_FAMILLE;
	private String[]							LB_FAMILLE_CREATION;
	private String[]							LB_HEURE;
	private String[]							LB_OS;
	private String[]							LB_GESTIONNAIRE;
	private String[]							LB_SIEGE_LESION;
	private String[]							LB_TYPE_AT;
	private String[]							LB_MALADIE_PRO;
	private String[]							LB_AT_REFERENCE;

	private ArrayList<EnumEtatAbsence>			listeEtats;
	private ArrayList<TypeAbsenceDto>			listeFamilleAbsence;
	private ArrayList<TypeAbsenceDto>			listeFamilleAbsenceVisualisation;
	private ArrayList<TypeAbsenceDto>			listeFamilleAbsenceCreation;
	private ArrayList<RefGroupeAbsenceDto>		listeGroupeAbsence;
	private ArrayList<OrganisationSyndicaleDto>	listeOrganisationSyndicale;
	private ArrayList<String>					listeHeure;
	private ArrayList<ReferentRh>				listeGestionnaire;
	private ArrayList<RefTypeDto>				listeMaladiesPro;
	private ArrayList<RefTypeDto>				listeSiegeLesion;
	private ArrayList<RefTypeDto>				listeTypeAT;
	private ArrayList<DemandeDto>				listeATReference;

	private TreeMap<Integer, DemandeDto>		listeAbsence;
	private HashMap<Integer, List<DemandeDto>>	history								= new HashMap<>();

	public String								ACTION_CREATION						= "Creation_absence";
	public String								ACTION_CREATION_DEMANDE				= "Creation_demande";
	public String								ACTION_MOTIF_ANNULATION				= "Motif_annulation_demande";
	public String								ACTION_MOTIF_EN_ATTENTE				= "Motif_mise_en_attente_demande";
	public String								ACTION_DOCUMENT_AJOUT				= "Ajout d'un document";
	public String								ACTION_DOCUMENT_CREATION			= "Création d'un document";
	public String								ACTION_DOCUMENT_SUPPRESSION			= "Suppression d'un document";
	public String								ACTION_COMMENTAIRE_DRH			    = "Commentaire DRH sur la demande";	
	public String 								ACTION_CREATION_CONTROLE_MEDICAL 	= "Creation_controle_medical";
	

	private TypeAbsenceDto						typeCreation;
	private Agent								agentCreation;
	private AgentDao							agentDao;
	private AffectationDao						affectationDao;
	private ReferentRhDao						referentRhDao;

	private IAdsService							adsService;

	private IRadiService						radiService;

	private IAbsService							absService;

	private IAlfrescoCMISService				alfrescoCMISService;

	private String								typeFiltre;

	public MultipartRequest						multi								= null;
	public File									fichierUpload						= null;
	public List<File>							listFichierUpload					= new ArrayList<File>();

	public boolean								isImporting							= false;

	private DemandeDto							demandeCourante;
	private PieceJointeDto						documentCourant;

	public PieceJointeDto getDocumentCourant() {
		return documentCourant;
	}

	public void setDocumentCourant(PieceJointeDto documentCourant) {
		this.documentCourant = documentCourant;
	}

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
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
		if (null == alfrescoCMISService) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
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
		if (getListeFamilleAbsenceVisualisation() == null || getListeFamilleAbsenceVisualisation().isEmpty()) {
			List<TypeAbsenceDto> listeFamilleVisualisation = (ArrayList<TypeAbsenceDto>) absService.getListeRefAllTypeAbsenceDto();
			// #31807 : on tri la liste
			Collections.sort(listeFamilleVisualisation, new Comparator<TypeAbsenceDto>() {
				@Override
				public int compare(TypeAbsenceDto o1, TypeAbsenceDto o2) {
					return o1.getLibelle().compareTo(o2.getLibelle());
				}

			});
			setListeFamilleAbsenceVisualisation((ArrayList<TypeAbsenceDto>) listeFamilleVisualisation);
		}

		// Si liste organisation syndicale vide alors affectation
		if (getLB_OS() == LBVide) {
			ArrayList<OrganisationSyndicaleDto> listeOrga = (ArrayList<OrganisationSyndicaleDto>) absService.getListeOrganisationSyndicale();
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
			setListeGroupeAbsence((ArrayList<RefGroupeAbsenceDto>) absService.getRefGroupeAbsence());

			int[] tailles = { 100 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<RefGroupeAbsenceDto> list = getListeGroupeAbsence().listIterator(); list.hasNext();) {
				RefGroupeAbsenceDto type = (RefGroupeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_GROUPE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_GROUPE_SELECT(), Const.ZERO);
			addZone(getNOM_LB_GROUPE_CREATE_SELECT(), Const.ZERO);
		}

		// Si liste groupe absence vide alors affectation
		if (getLB_GROUPE_CREATE() == LBVide) {

			int[] tailles = { 100 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<RefGroupeAbsenceDto> list = getListeGroupeAbsence().listIterator(); list.hasNext();) {
				RefGroupeAbsenceDto type = (RefGroupeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_GROUPE_CREATE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_GROUPE_CREATE_SELECT(), Const.ZERO);
		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	/**
	 * Process incoming requests for information*
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 */
	public boolean recupererPreControles(HttpServletRequest request) throws Exception {
		String type = request.getHeader("Content-Type");
		String repTemp = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");
		@SuppressWarnings("unused")
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			request.setCharacterEncoding("UTF-8");
			multi = new MultipartRequest(request, repTemp, 10 * 1024 * 1024, "UTF-8");
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		isImporting = false;

		String JSP = null;
		if (null == request.getParameter("JSP")) {
			if (multi != null) {
				JSP = multi.getParameter("JSP");
			}
		} else {
			JSP = request.getParameter("JSP");
		}

		// Si on arrive de la JSP alors on traite le get
		if (JSP != null && JSP.equals(getJSP())) {

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

			// Si clic sur le bouton PB_SELECT_GROUPE
			if (testerParametre(request, getNOM_PB_SELECT_GROUPE_CREATE())) {
				return performPB_SELECT_GROUPE_CREATE(request);
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
				// isImporting = true;
				return performPB_CREATION(request);
			}
			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
			// Si clic sur le bouton PB_ANNULER_DOCUMENT
			if (testerParametre(request, getNOM_PB_ANNULER_DOCUMENT())) {
				return performPB_ANNULER_DOCUMENT(request);
			}
			// Si clic sur le bouton PB_VALIDER_CREATION_A48_A54_A50_Amicale
			if (testerParametre(request, getNOM_PB_VALIDER_CREATION_DEMANDE())) {
				// isImporting = true;
				return performPB_VALIDER_CREATION_DEMANDE(request);
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_CREER_DOC())) {
				// isImporting = true;
				return performPB_CREER_DOC(request);
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION())) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_AJOUT_PJ
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_AJOUT())) {
				return performPB_VALIDER_AJOUT_PJ(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOC
			if (null != listFichierUpload && !listFichierUpload.isEmpty()) {
				for (File file : listFichierUpload) {
					if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(file.getName()))) {
						return performPB_SUPPRIMER_DOC(request, file.getName());
					}
				}
			}

			// Si clic sur les boutons du tableau
			for (Integer indiceAbs : getListeAbsence().keySet()) {

				// Si clic sur le bouton PB_COMMENTAIRE_DRH
				if (testerParametre(request, getNOM_PB_COMMENTAIRE_DRH(indiceAbs))) {
					return performPB_COMMENTAIRE_DRH(request, indiceAbs);
				}				
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
				// Si clic sur le bouton PB_CONTROLE_MEDICAL
				if (testerParametre(request, getNOM_PB_CONTROLE_MEDICAL(indiceAbs))) {
					return performPB_CONTROLE_MEDICAL(request, indiceAbs);
				}
				// Si clic sur le bouton PB_CREER_DOC
				if (testerParametre(request, getNOM_PB_AJOUTER_DOC(indiceAbs))) {
					return performPB_AJOUTER_PJ(request, indiceAbs);
				}
				DemandeDto dem = getListeAbsence().get(indiceAbs);
				for (int indicePJ = 0; indicePJ < dem.getPiecesJointes().size(); indicePJ++) {
					if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(indiceAbs, indicePJ))) {
						return performPB_SUPPRIMER_DOC(request, dem, indicePJ);
					}
				}
				// Si clic sur le bouton PB_PB_SUPPRIMER_DOC

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
			// Si clic sur le bouton PB_VALIDER_COMMENTAIRE_DRH
			if (testerParametre(request, getNOM_PB_VALIDER_COMMENTAIRE_DRH())) {
				return performPB_VALIDER_COMMENTAIRE_DRH(request);
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_EN_ATTENTE
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF_EN_ATTENTE())) {
				return performPB_VALIDER_MOTIF_EN_ATTENTE(request);
			}
			// Si clic sur le bouton PB_VALIDER_COMMENTAIRE_CM
			if (testerParametre(request, getNOM_PB_VALIDER_COMMENTAIRE_CM())) {
				return performPB_VALIDER_COMMENTAIRE_CM(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_SUPPRESSION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION())) {
				return performPB_VALIDER_DOCUMENT_SUPPRESSION(request);
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

	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
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
		String dateMin = dateDeb.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateDeb, "dd/MM/yyyy", "yyyyMMdd");

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = dateFin.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateFin, "dd/MM/yyyy", "yyyyMMdd");

		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
		EnumEtatAbsence etat = null;
		if (numEtat != -1 && numEtat != 0) {
			etat = (EnumEtatAbsence) getListeEtats().get(numEtat - 1);
		}
		List<Integer> listeEtat = new ArrayList<Integer>();
		if (etat != null)
			listeEtat.add(etat.getCode());
		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_FAMILLE_SELECT())) : -1);
		TypeAbsenceDto type = null;
		if (numType != -1 && numType != 0) {
			type = (TypeAbsenceDto) getListeFamilleAbsence().get(numType - 1);
		}
		// groupe
		int numGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_GROUPE_SELECT())) : -1);
		RefGroupeAbsenceDto groupe = null;
		if (numGroupe != -1 && numGroupe != 0) {
			groupe = (RefGroupeAbsenceDto) getListeGroupeAbsence().get(numGroupe - 1);
		}

		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_DEMANDE();

		// SERVICE
		List<String> idAgentService = new ArrayList<>();
		String sigle = getVAL_EF_SERVICE().toUpperCase();
		String idServiceAds = getVAL_ST_ID_SERVICE_ADS().toUpperCase();

		if (!sigle.equals(Const.CHAINE_VIDE) && null == idAgentDemande) {
			EntiteDto service = adsService.getEntiteBySigle(sigle);
			idServiceAds = new Long(service.getIdEntite()).toString();
		}

		if (!idServiceAds.equals(Const.CHAINE_VIDE) && null == idAgentDemande) {
			// Récupération des agents
			// on recupere les sous-service du service selectionne
			List<Integer> listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));

			if (null != listeSousService && !listeSousService.isEmpty()) {
				ArrayList<Agent> listAgent = getAgentDao().listerAgentAvecServicesETMatricules(listeSousService, null, null);
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
		int numGestionnaire = (Services.estNumerique(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) : -1);
		ReferentRh gestionnaire = null;
		if (numGestionnaire != -1 && numGestionnaire != 0) {
			gestionnaire = (ReferentRh) getListeGestionnaire().get(numGestionnaire - 1);
		}
		if (idServiceAds.equals(Const.CHAINE_VIDE) && null == idAgentDemande && gestionnaire != null) {

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
			List<Integer> listeIdsServiceAdsReferentRh = new ArrayList<Integer>();
			for (ReferentRh service : listServiceRH) {
				listeIdsServiceAdsReferentRh.add(service.getIdServiceAds());
			}
			List<Integer> listeSousServiceTmp = new ArrayList<Integer>();
			for (Integer idService : listeIdsServiceAdsReferentRh) {
				listeSousServiceTmp.addAll(adsService.getListIdsEntiteWithEnfantsOfEntite(idService));
			}
			// on trie la liste des sous service pour supprimer les doublons
			ArrayList<Integer> listeSousService = new ArrayList<Integer>();
			for (Integer sousService : listeSousServiceTmp) {
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

		// #31733 : si juste filtre sur le groupe il va y avoir une erreur
		if (groupe != null && dateMin == null && dateMax == null && idAgentDemande == null) {
			getTransaction().declarerErreur("Veuillez saisir plus de filtres.");
			return false;
		}

		List<DemandeDto> listeDemande = absService.getListeDemandes(dateMin, dateMax,
				listeEtat.size() == 0 ? null : listeEtat.toString().replace("[", "").replace("]", "").replace(" ", ""),
				type == null ? null : type.getIdRefTypeAbsence(), idAgentDemande == null ? null : Integer.valueOf(idAgentDemande),
				groupe == null ? null : groupe.getIdRefGroupeAbsence(), false, idAgentService);

		logger.debug("Taille liste absences : " + listeDemande.size());
		setListeAbsence((ArrayList<DemandeDto>) listeDemande);

		// redmine #13453
		// loadHistory();

		afficheListeAbsence();
		if (299 < listeDemande.size()) {
			getTransaction().declarerErreur("Attention, les demandes sont limitées a 300 résultats. Utiliser les filtres.");
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

				// #15586 affichage des restitutions massives des CA
				TypeAbsenceDto type = 0 == abs.getIdTypeDemande() ? new TypeAbsenceDto() : getListeFamilleAbsenceVisualisation().get(getListeFamilleAbsenceVisualisation().indexOf(t));

				addZone(getNOM_ST_MATRICULE(i), null != ag ? ag.getNomatr().toString() : "");
				addZone(getNOM_ST_AGENT(i), ag.getNomAgent() + " " + ag.getPrenomAgent() + " (" + abs.getAgentWithServiceDto().getSigleService() + ")");
				addZone(getNOM_ST_INFO_AGENT(i), "<br/>" + statut);
				String baseConges = null != abs.getTypeSaisiCongeAnnuel()
						? " (Base congé : " + abs.getTypeSaisiCongeAnnuel().getCodeBaseHoraireAbsence() + ")" : "";
				String prolongation = abs.isProlongation() ? " - Prolongation" : "";
				// #15586 affichage des restitutions massives des CA
				String ST_type = 0 == abs.getIdTypeDemande() ? abs.getLibelleTypeDemande() + "<br/>" + sdf.format(abs.getDateDemande())
						: type.getLibelle() + baseConges + prolongation + "<br/>" + sdf.format(abs.getDateDemande());

				addZone(getNOM_ST_TYPE(i), ST_type);

				String debutMAM = abs.isDateDebutAM() ? "M" : abs.isDateDebutPM() ? "A" : Const.CHAINE_VIDE;
				addZone(getNOM_ST_DATE_DEB(i), sdf.format(abs.getDateDebut()) + "<br/>" + (debutMAM.equals(Const.CHAINE_VIDE) ? hrs.format(abs.getDateDebut()) : debutMAM));
				if (abs.getDateFin() != null) {
					String finMAM = abs.isDateFinAM() ? "M" : abs.isDateFinPM() ? "A" : Const.CHAINE_VIDE;
					addZone(getNOM_ST_DATE_FIN(i), sdf.format(abs.getDateFin()) + "<br/>" + (finMAM.equals(Const.CHAINE_VIDE) ? hrs.format(abs.getDateFin()) : finMAM));
				}
				if (abs.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode() || abs.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()
						|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode() || abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
						|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode() || abs.getIdTypeDemande() == EnumTypeAbsence.ASA_AMICALE.getCode()) {
					addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : getHeureMinute(abs.getDuree().intValue()));
				} else if (abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode() || abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
						|| abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode() || abs.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
						|| abs.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
					if (abs.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {

						Double nombreGarde = (null != abs.getTypeSaisiCongeAnnuel() && null != abs.getTypeSaisiCongeAnnuel().getQuotaMultiple()
								&& 0 != abs.getTypeSaisiCongeAnnuel().getQuotaMultiple() ? abs.getDuree() / abs.getTypeSaisiCongeAnnuel().getQuotaMultiple() : 0);
						String nombreGardeStr = 0 == nombreGarde ? "" : (1 == nombreGarde ? " (" + nombreGarde.toString() + " garde)" : " (" + nombreGarde.toString() + " gardes)");
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j" + (abs.isSamediOffert() ? " +S" : "") + nombreGardeStr);
					} else {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					}
				} else if (abs.getGroupeAbsence() != null && abs.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
					if (abs.getTypeSaisi().isChkDateDebut()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					} else if (abs.getTypeSaisi().isCalendarHeureDebut()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : getHeureMinute(abs.getDuree().intValue()));
					} else if (abs.getTypeSaisi().isCalendarDateDebut()) {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					} else {
						addZone(getNOM_ST_DUREE(i), "&nbsp;");
					}

				} else if (abs.getGroupeAbsence() != null
						&& abs.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.MALADIES.getValue()) {
					if (abs.getTypeSaisi().isNombreITT()) {
						// #32282 : pour AT, on affiche le nombre jours ITT dans
						// la durée
						addZone(getNOM_ST_DUREE(i), abs.getNombreITT() == null ? "&nbsp;" : abs.getNombreITT().toString() + "j");
					} else {
						addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
					}
				} else if (0 == abs.getIdTypeDemande()) {
					// #15586 affichage des restitutions massives des CA
					addZone(getNOM_ST_DUREE(i), abs.getDuree() == null ? "&nbsp;" : abs.getDuree().toString() + "j");
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
		String sigle = getVAL_EF_SERVICE().toUpperCase();
		if (!sigle.equals(Const.CHAINE_VIDE)) {
			// on cherche le code service associé
			EntiteDto serv = adsService.getEntiteBySigle(sigle);
			if (null == serv || 0 == serv.getIdEntite()) {
				// ERR502", "Le sigle service saisie ne permet pas de trouver le
				// service associé."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR502"));
				return false;
			}
		}

		return true;
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
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_FAMILLE_CREATION_SELECT())) : -1);
		TypeAbsenceDto type = null;
		if (numType != -1) {
			type = (TypeAbsenceDto) getListeFamilleAbsenceCreation().get(numType - 1);
			if (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.CONGE.getCode().toString())) {
				// on cherche la base horaire absence de l'agent
				// #15000 : si on le trouve pas alors on prend la derniere
				Affectation aff = null;
				try {
					aff = getAffectationDao().chercherAffectationActiveAvecAgent(getAgentCreation().getIdAgent());
				} catch (Exception e) {
					List<Affectation> listAffAutre = getAffectationDao().listerAffectationAvecAgent(getAgentCreation().getIdAgent());
					if (listAffAutre.size() > 0) {
						aff = listAffAutre.get(listAffAutre.size() - 1);
					}
				}
				if (aff == null || aff.getIdBaseHoraireAbsence() == null) {
					// "ERR805",
					// "L'agent @ n'a pas de base horaire d'absence. Merci de la renseigner dans l'affectation de l'agent."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR805", idAgent));
					return false;
				}
				type = absService.getTypeAbsence(aff.getIdBaseHoraireAbsence());
			}
			setTypeCreation(type);
		}

		// On nomme l'action
		if ((type != null && (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A48.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A49.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A50.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A52.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A53.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A54.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A55.getCode().toString())
				|| type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_AMICALE.getCode().toString())))
				|| (type != null && type.getGroupeAbsence() != null
						&& (type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()
								|| type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue()
								|| type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.MALADIES.getValue()))) {

			ArrayList<OrganisationSyndicaleDto> listeOrga = new ArrayList<OrganisationSyndicaleDto>();
			if (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A52.getCode().toString())) {
				listeOrga = (ArrayList<OrganisationSyndicaleDto>) absService.getListeOrganisationSyndicaleActiveByAgent(new Integer(idAgent), type.getIdRefTypeAbsence());
				if (null == listeOrga || listeOrga.isEmpty()) {
					getTransaction().declarerErreur("L'agent ne fait parti d'aucun syndicat.");
					return false;
				}
			} else if (type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.AS.getValue()) {
				listeOrga = (ArrayList<OrganisationSyndicaleDto>) absService.getListeOrganisationSyndicale();
			}

			if (type.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.MALADIES.getValue()) {
				if (isTypeATOrRechuteAT(type)) {
					formatterListeTypeAT(getListeTypeAT());
				}
				if (type.getTypeSaisiDto().isSiegeLesion()) {
					formatterListeSiegeLesion(getListeSiegeLesion());
				}
				if (type.getTypeSaisiDto().isMaladiePro()) {
					formatterListeMaladiesPro(getListeMaladiesPro());
				}
				if (type.getTypeSaisiDto().isAtReference()) {
					formatterListeATReference(getListeATReference(new Integer(idAgent)));
				}
			}

			setListeOrganisationSyndicale(listeOrga);
			formatterOS(listeOrga);

			addZone(getNOM_RG_ETAT(), getNOM_RB_ETAT_VALIDE());

			addZone(getNOM_ST_ACTION(), ACTION_CREATION_DEMANDE);
		} else {
			getTransaction().declarerErreur("Cette famille ne peut être saisie dans SIRH.");
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void formatterOS(ArrayList<OrganisationSyndicaleDto> listeOrga) {

		int[] tailles = { 50 };
		FormateListe aFormat = new FormateListe(tailles);
		for (OrganisationSyndicaleDto os : listeOrga) {
			String ligne[] = { os.getLibelle() };
			aFormat.ajouteLigne(ligne);
		}
		setLB_OS(aFormat.getListeFormatee(false));
		addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
	}

	private void formatterListeMaladiesPro(ArrayList<RefTypeDto> listeMP) {

		int[] tailles = { 50 };
		FormateListe aFormat = new FormateListe(tailles);
		for (RefTypeDto mp : listeMP) {
			String ligne[] = { mp.getLibelle() };
			aFormat.ajouteLigne(ligne);
		}
		setLB_MALADIE_PRO(aFormat.getListeFormatee(false));
	}

	private void formatterListeSiegeLesion(ArrayList<RefTypeDto> listeSL) {

		int[] tailles = { 50 };
		FormateListe aFormat = new FormateListe(tailles);
		for (RefTypeDto sl : listeSL) {
			String ligne[] = { sl.getLibelle() };
			aFormat.ajouteLigne(ligne);
		}
		setLB_SIEGE_LESION(aFormat.getListeFormatee(false));
	}

	private void formatterListeTypeAT(ArrayList<RefTypeDto> listeTypeAT) {

		int[] tailles = { 50 };
		FormateListe aFormat = new FormateListe(tailles);
		for (RefTypeDto sl : listeTypeAT) {
			String ligne[] = { sl.getLibelle() };
			aFormat.ajouteLigne(ligne);
		}
		setLB_TYPE_AT(aFormat.getListeFormatee(false));
	}

	private void formatterListeATReference(ArrayList<DemandeDto> listeAT) {

		int[] tailles = { 50 };
		FormateListe aFormat = new FormateListe(tailles);
		for (DemandeDto at : listeAT) {
			String ligne[] = { sdf.format(at.getDateDeclaration()) + " - " + at.getTypeSiegeLesion().getLibelle() };
			aFormat.ajouteLigne(ligne);
		}
		setLB_AT_REFERENCE(aFormat.getListeFormatee(false));
	}
	
	public boolean isTypeATOrRechuteAT(TypeAbsenceDto type) {
		return type.getTypeSaisiDto().getIdRefTypeDemande().equals(EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode()) ||
				type.getTypeSaisiDto().getIdRefTypeDemande().equals(EnumTypeAbsence.MALADIES_RECHUTE.getCode());
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public String getNOM_PB_ANNULER_DOCUMENT() {
		return "NOM_PB_ANNULER_DOCUMENT";
	}

	public boolean performPB_ANNULER_DOCUMENT(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		return true;
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
		addZone(getNOM_LB_GROUPE_CREATE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), Const.ZERO);
		setListeFamilleAbsenceCreation(null);
		setListeFamilleAbsenceVisualisation(null);
		setLB_FAMILLE_CREATION(null);
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
		addZone(getNOM_RG_ETAT(), getNOM_RB_ETAT_VALIDE());

		// MALADIES
		addZone(getNOM_ST_PRESCRIPTEUR(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOM_ENFANT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOMBRE_ITT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DECLARATION(), Const.CHAINE_VIDE);
		addZone(getNOM_CK_PROLONGATION(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_SIEGE_LESION_SELECT(), Const.ZERO);
		addZone(getNOM_LB_TYPE_AT_SELECT(), Const.ZERO);
		addZone(getNOM_LB_MALADIE_PRO_SELECT(), Const.ZERO);
		addZone(getNOM_LB_AT_REFERENCE_SELECT(), Const.ZERO);
		addZone(getNOM_ST_ID_COMMENTAIRE_CM(), Const.CHAINE_VIDE);

		// pieces jointes
		listFichierUpload.clear();
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

	public String getNOM_ST_NOMBRE_ITT() {
		return "NOM_ST_NOMBRE_ITT";
	}

	public String getVAL_ST_NOMBRE_ITT() {
		return getZone(getNOM_ST_NOMBRE_ITT());
	}

	public String getNOM_ST_PRESCRIPTEUR() {
		return "NOM_ST_PRESCRIPTEUR";
	}

	public String getVAL_ST_PRESCRIPTEUR() {
		return getZone(getNOM_ST_PRESCRIPTEUR());
	}

	public String getNOM_ST_NOM_ENFANT() {
		return "NOM_ST_NOM_ENFANT";
	}

	public String getVAL_ST_NOM_ENFANT() {
		return getZone(getNOM_ST_NOM_ENFANT());
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

	public String getNOM_ST_DATE_DECLARATION() {
		return "NOM_ST_DATE_DECLARATION";
	}

	public String getVAL_ST_DATE_DECLARATION() {
		return getZone(getNOM_ST_DATE_DECLARATION());
	}

	public String getNOM_CK_PROLONGATION() {
		return "NOM_CK_PROLONGATION";
	}

	public String getVAL_CK_PROLONGATION() {
		return getZone(getNOM_CK_PROLONGATION());
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
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
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
		// on tri la liste
		Collections.sort(listeAbsenceAjout, new DemandeDtoDateDebutComparator());

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

		history.put(absId, getAbsService().getVisualisationHistory(idDemande));

		List<DemandeDto> data = history.get(absId);
		int numParams = 7;
		String[][] ret = new String[data.size()][numParams];
		int index = 0;
		for (DemandeDto p : data) {
			ret[index][0] = formatDate(p.getDateDemande());
			ret[index][1] = formatDate(p.getDateDebut()) + "<br/>" + formatHeure(p.getDateDebut());
			ret[index][2] = formatDate(p.getDateFin()) + "<br/>" + formatHeure(p.getDateFin());
			String duree = "&nbsp;";
			if (p.getIdTypeDemande() == EnumTypeAbsence.RECUP.getCode() || p.getIdTypeDemande() == EnumTypeAbsence.REPOS_COMP.getCode()) {
				duree = getHeureMinute(p.getDuree().intValue());
			} else if (p.getIdTypeDemande() == EnumTypeAbsence.ASA_A48.getCode() || p.getIdTypeDemande() == EnumTypeAbsence.ASA_A54.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A53.getCode() || p.getIdTypeDemande() == EnumTypeAbsence.ASA_A50.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
				if (p.getIdTypeDemande() == EnumTypeAbsence.CONGE.getCode()) {
					duree = p.getDuree().toString() + "j" + (p.isSamediOffert() ? " +S" : "");
				} else {
					duree = p.getDuree().toString() + "j";
				}
			} else if (p.getIdTypeDemande() == EnumTypeAbsence.ASA_A55.getCode() || p.getIdTypeDemande() == EnumTypeAbsence.ASA_A52.getCode()
					|| p.getIdTypeDemande() == EnumTypeAbsence.ASA_A49.getCode() || p.getIdTypeDemande() == EnumTypeAbsence.ASA_AMICALE.getCode()) {
				duree = getHeureMinute(p.getDuree().intValue());
			} else if (p.getGroupeAbsence() != null && p.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
				if (p.getTypeSaisi().isChkDateDebut()) {
					duree = p.getDuree() == null ? "&nbsp;" : p.getDuree().toString() + "j";
				} else if (p.getTypeSaisi().isCalendarHeureDebut()) {
					duree = p.getDuree() == null ? "&nbsp;" : getHeureMinute(p.getDuree().intValue());
				} else if (p.getTypeSaisi().isCalendarDateDebut()) {
					duree = p.getDuree() == null ? "&nbsp;" : p.getDuree().toString() + "j";
				}
			} else if (p.getGroupeAbsence() != null && p.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.MALADIES.getValue()) {
				if (p.getTypeSaisi() != null && p.getTypeSaisi().isNombreITT()) {
					// #32282 : pour AT, on affiche le nombre jours ITT dans
					// la durée
					duree = p.getNombreITT() == null ? "&nbsp;" : p.getNombreITT().toString() + "j";
				} else {
					duree = p.getDuree() == null ? "&nbsp;" : p.getDuree().toString() + "j";
				}
			}
			ret[index][3] = duree;
			ret[index][4] = p.getMotif() == null ? "&nbsp;" : p.getMotif();
			ret[index][5] = EnumEtatAbsence.getValueEnumEtatAbsence(p.getIdRefEtat());
			ret[index][6] = formatDate(p.getDateSaisie()) + " " + formatHeure(p.getDateSaisie()) + "<br/>" + p.getAgentWithServiceDto().getPrenom() + " " + p.getAgentWithServiceDto().getNom() + " ("
					+ new Integer(p.getAgentWithServiceDto().getIdAgent() - 9000000).toString() + ")";
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
		
		// #39329 : Les listes n'étaient pas initialisées pour une première duplication
		if (dem.getGroupeAbsence() != null) {
			RefGroupeAbsenceDto refGroupeAbs = new RefGroupeAbsenceDto();
			refGroupeAbs.setIdRefGroupeAbsence(dem.getGroupeAbsence().getIdRefGroupeAbsence());
			
			addZone(getNOM_LB_GROUPE_CREATE_SELECT(), String.valueOf(getListeGroupeAbsence().indexOf(refGroupeAbs) + 1));
			performPB_SELECT_GROUPE_CREATE(null);
		}
		
		TypeAbsenceDto type = getListeFamilleAbsenceCreation().get(getListeFamilleAbsenceCreation().indexOf(t));

		if (type.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.CONGE.getCode().toString())) {
			// on cherche la base horaire absence de l'agent
			Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(dem.getAgentWithServiceDto().getIdAgent());
			if (aff == null || aff.getIdBaseHoraireAbsence() == null) {
				// "ERR805",
				// "L'agent @ n'a pas de base horaire d'absence. Merci de la renseigner dans l'affectation de l'agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR805", dem.getAgentWithServiceDto().getIdAgent().toString()));
				return false;
			}
			type = absService.getTypeAbsence(aff.getIdBaseHoraireAbsence());
		}
		setTypeCreation(type);

		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), String.valueOf(getListeFamilleAbsenceCreation().indexOf(type) + 1));

		Agent agt = getAgentDao().chercherAgent(dem.getAgentWithServiceDto().getIdAgent());
		addZone(getNOM_ST_AGENT_CREATION(), agt.getNomatr().toString());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// date de debut
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCalendarDateDebut()) || (null != type.getTypeSaisiCongeAnnuelDto() && type.getTypeSaisiCongeAnnuelDto().isCalendarDateDebut())) {
			addZone(getNOM_ST_DATE_DEBUT(), sdf.format(dem.getDateDebut()));
		}
		// date de fin
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isCalendarDateFin()) || (null != type.getTypeSaisiCongeAnnuelDto() && type.getTypeSaisiCongeAnnuelDto().isCalendarDateFin())) {
			addZone(getNOM_ST_DATE_FIN(), sdf.format(dem.getDateFin()));
		}
		// date de debut
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isDateDeclaration())) {
			addZone(getNOM_ST_DATE_DECLARATION(), sdf.format(dem.getDateDeclaration()));
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
			addZone(getNOM_LB_OS_SELECT(), orga == null ? Const.ZERO : String.valueOf(getListeOrganisationSyndicale().indexOf(orga)));
		}
		// /////////////// MOTIF ////////////////////
		if ((null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isMotif()) || (null != type.getTypeSaisiCongeAnnuelDto() && type.getTypeSaisiCongeAnnuelDto().isMotif())) {
			addZone(getNOM_ST_MOTIF_CREATION(), dem.getMotif() == null ? Const.CHAINE_VIDE : dem.getMotif().trim());
		}

		// /////////////// DUREE ////////////////////
		if (null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isDuree()) {
			String dureeHeures = (dem.getDuree() / 60) == 0 ? Const.CHAINE_VIDE : new Double(dem.getDuree() / 60).intValue() + Const.CHAINE_VIDE;
			String dureeMinutes = (dem.getDuree() % 60) == 0 ? Const.CHAINE_VIDE : new Double(dem.getDuree() % 60).intValue() + Const.CHAINE_VIDE;
			addZone(getNOM_ST_DUREE(), dureeHeures);
			addZone(getNOM_ST_DUREE_MIN(), dureeMinutes);
		} else if (null != type.getTypeSaisiCongeAnnuelDto()) {
			String duree = dem.getDuree() == 0 ? Const.CHAINE_VIDE : dem.getDuree() + Const.CHAINE_VIDE;
			addZone(getNOM_ST_DUREE(), duree);
		}

		// MALADIES
		if (null != type.getTypeSaisiDto()) {
			if (type.getTypeSaisiDto().isPrescripteur()) {
				addZone(getNOM_ST_PRESCRIPTEUR(), dem.getPrescripteur() == null ? Const.CHAINE_VIDE : dem.getPrescripteur().trim());
			}
			if (type.getTypeSaisiDto().isProlongation()) {
				addZone(getNOM_CK_PROLONGATION(), dem.isProlongation() ? getCHECKED_ON() : getCHECKED_OFF());
			}
			if (type.getTypeSaisiDto().isNomEnfant()) {
				addZone(getNOM_ST_NOM_ENFANT(), dem.getNomEnfant() == null ? Const.CHAINE_VIDE : dem.getNomEnfant().trim());
			}
			if (type.getTypeSaisiDto().isNombreITT()) {
				addZone(getNOM_ST_NOMBRE_ITT(), dem.getNombreITT() == null ? Const.CHAINE_VIDE : dem.getNombreITT().toString());
			}
			if (isTypeATOrRechuteAT(type)) {
				RefTypeDto typeAT = null;
				if (null != dem.getTypeAccidentTravail()) {
					typeAT = dem.getTypeAccidentTravail();
				}
				addZone(getNOM_LB_SIEGE_LESION_SELECT(),
						typeAT == null ? Const.ZERO : String.valueOf(getListeTypeAT().indexOf(typeAT)));
			}
			if (type.getTypeSaisiDto().isSiegeLesion()) {
				RefTypeDto siegeLesion = null;
				if (null != dem.getTypeSiegeLesion()) {
					siegeLesion = dem.getTypeSiegeLesion();
				}
				addZone(getNOM_LB_SIEGE_LESION_SELECT(),
						siegeLesion == null ? Const.ZERO : String.valueOf(getListeSiegeLesion().indexOf(siegeLesion)));
			}
			if (type.getTypeSaisiDto().isMaladiePro()) {
				RefTypeDto maladiePro = null;
				if (null != dem.getTypeMaladiePro()) {
					maladiePro = dem.getTypeMaladiePro();
				}
				addZone(getNOM_LB_MALADIE_PRO_SELECT(), maladiePro == null ? Const.ZERO : String.valueOf(getListeMaladiesPro().indexOf(maladiePro)));
			}
			if (type.getTypeSaisiDto().isAtReference()) {
				DemandeDto atReference = null;
				if (null != dem.getAccidentTravailReference()) {
					atReference = dem.getAccidentTravailReference();
				}
				addZone(getNOM_LB_AT_REFERENCE_SELECT(), atReference == null ? Const.ZERO
						: String.valueOf(getListeATReference(dem.getAgentWithServiceDto().getIdAgent()).indexOf(atReference)));
			}
		}

		// /////////////// PIECE JOINTE ////////////////////
		if (null != type.getTypeSaisiDto() && type.getTypeSaisiDto().isPieceJointe() && null != dem.getPiecesJointes()
				&& !dem.getPiecesJointes().isEmpty()) {
			for (PieceJointeDto pj : dem.getPiecesJointes()) {
				File file = alfrescoCMISService.readDocument(pj.getNodeRefAlfresco());

				if (null != file)
					listFichierUpload.add(file);
			}
		} else if (null != type.getTypeSaisiCongeAnnuelDto() && type.getTypeSaisiCongeAnnuelDto().isPieceJointe() && null != dem.getPiecesJointes()
				&& !dem.getPiecesJointes().isEmpty()) {
			for (PieceJointeDto pj : dem.getPiecesJointes()) {
				File file = alfrescoCMISService.readDocument(pj.getNodeRefAlfresco());

				if (null != file)
					listFichierUpload.add(file);
			}
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

	public String getNOM_PB_CONTROLE_MEDICAL(int i) {
		return "NOM_PB_CONTROLE_MEDICAL" + i;
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
		
		// #39811 : La liste des familles d'absence n'est pas initialisée
		if ((getListeFamilleAbsenceCreation() == null || getListeFamilleAbsenceCreation().size() == 0) && dem.getGroupeAbsence().getIdRefGroupeAbsence() != null) {
			setListeFamilleAbsenceCreation((ArrayList<TypeAbsenceDto>) absService.getListeRefTypeAbsenceDto(dem.getGroupeAbsence().getIdRefGroupeAbsence()));
		}
		
		if (((dem.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.AS.getValue() || dem.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) && (dem
				.getIdRefEtat() == EnumEtatAbsence.APPROUVE.getCode()
		// #14696 ajout de l etat A VALIDER car erreur lors de la reprise de
		// donnees des conges exceptionnels mis l etat A VALIDER au lieu de
		// SAISI ou APPROUVE
				|| dem.getIdRefEtat() == EnumEtatAbsence.A_VALIDER.getCode()))
				|| (dem.getGroupeAbsence().getIdRefGroupeAbsence() == EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue()) && dem.getIdRefEtat() == EnumEtatAbsence.A_VALIDER.getCode()) {
			// "ERR803",
			// "Pour @ cette demande, merci de renseigner un motif."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR803", "mettre en attente"));
			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(dem.getIdTypeDemande());
			String info = "Demande " + getListeFamilleAbsenceCreation().get(getListeFamilleAbsenceCreation().indexOf(t)).getLibelle() + " de l'agent " + ag.getNomatr() + " du "
					+ sdf.format(dem.getDateDebut()) + ".";
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

	public boolean performPB_AJOUTER_PJ(HttpServletRequest request, int idDemande) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		setDemandeCourante(dem);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_AJOUT);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_VALIDER_AJOUT_PJ(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dto = getDemandeCourante();

		// /////////////// PIECES JOINTES ////////////////////
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {
			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				File file = multi.getFile(getNOM_EF_LIENDOCUMENT());
				if (null != listFichierUpload) {
					boolean isAjout = true;
					for (File fileTmp : listFichierUpload) {
						if (fileTmp.getName().equals(file.getName())) {
							isAjout = false;
							break;
						}
					}
					if (isAjout)
						listFichierUpload.add(file);
				}
			}
		}

		if (null != listFichierUpload && !listFichierUpload.isEmpty()) {
			for (File file : listFichierUpload) {

				FileInputStream in = new FileInputStream(file);
				try {
					byte[] byteBuffer = new byte[in.available()];
					in.read(byteBuffer);
					PieceJointeDto pj = new PieceJointeDto();
					pj.setbFile(byteBuffer);
					pj.setTypeFile(new MimetypesFileTypeMap().getContentType(file));
					dto.getPiecesJointes().add(pj);
				} finally {
					in.close();
				}
			}
		}

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ReturnMessageDto srm = absService.addPieceJointeSIRH(getAgentConnecte(request).getIdAgent(), json);

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
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		listFichierUpload.clear();

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		if (getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_VALIDER(request);
		}

		return true;
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

	public boolean performPB_CONTROLE_MEDICAL(HttpServletRequest request, Integer idDemande) throws Exception {
		viderZoneSaisie(request);
		// On récupère l'id de la demande pour aller chercher la demande de contrôle médicale associée.
		DemandeDto dem = getListeAbsence().get(Integer.valueOf(idDemande));
		ControleMedicalDto dto = absService.findControleMedicalByDemandeId(dem.getIdDemande());
		
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION_CONTROLE_MEDICAL);
		addZone(getNOM_ST_ID_COMMENTAIRE_CM(), new Integer(idDemande).toString());
		addZone(getNOM_ST_COMMENTAIRE_CM(), dto != null ? dto.getCommentaire() : null);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void changeState(HttpServletRequest request, DemandeDto dem, EnumEtatAbsence state, String motif) throws Exception {
		ArrayList<DemandeDto> param = new ArrayList<DemandeDto>();
		param.add(dem);
		changeState(request, param, state, motif);
	}

	private void changeState(HttpServletRequest request, Collection<DemandeDto> dem, EnumEtatAbsence state, String motif) throws Exception {
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

			String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(listDto);
			ReturnMessageDto message = absService.setAbsState(agentConnecte.getIdAgent(), json);

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
		history.put(absId, absService.getVisualisationHistory(absId));
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
		String info = "Demande " + getListeFamilleAbsenceVisualisation().get(getListeFamilleAbsenceVisualisation().indexOf(t)).getLibelle() + " de l'agent " + ag.getNomatr() + " du "
				+ sdf.format(dem.getDateDebut()) + ".";
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

	// Contrôle médical
	public String getNOM_ST_ID_COMMENTAIRE_CM() {
		return "NOM_ST_ID_COMMENTAIRE_CM";
	}

	public String getVAL_ST_ID_COMMENTAIRE_CM() {
		return getZone(getNOM_ST_ID_COMMENTAIRE_CM());
	}

	public String getNOM_PB_VALIDER_COMMENTAIRE_CM() {
		return "NOM_PB_VALIDER_COMMENTAIRE_CM";
	}

	public String getNOM_ST_COMMENTAIRE_CM() {
		return "NOM_ST_COMMENTAIRE_CM";
	}

	public String getVAL_ST_COMMENTAIRE_CM() {
		return getZone(getNOM_ST_COMMENTAIRE_CM());
	}

	public String getVAL_ST_INFO_COMMENTAIRE_CM() {
		return getZone(getNOM_ST_ID_COMMENTAIRE_CM());
	}
	
	public boolean isCreationControleMedical() {
		return getVAL_ST_COMMENTAIRE_CM().trim().isEmpty();
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

	public boolean performPB_VALIDER_COMMENTAIRE_CM(HttpServletRequest request) throws Exception {
		// on recupere les infos
		String idDemande = getVAL_ST_ID_COMMENTAIRE_CM();
		String motif = getVAL_ST_COMMENTAIRE_CM();
		if (motif.trim().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "commentaire"));
			return false;
		}
		
		DemandeDto dem = getListeAbsence().get(Integer.valueOf(idDemande));
		
		if (dem.getControleMedical() != null && dem.getControleMedical().getId() != null) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR042"));
			return false;
		}
		
		ControleMedicalDto controleMedical = new ControleMedicalDto();
		controleMedical.setCommentaire(motif);
		controleMedical.setDate(new Date());
		controleMedical.setIdDemandeMaladie(dem.getIdDemande());
		controleMedical.setIdAgent(getAgentConnecte(request).getIdAgent());
		
		absService.persistDemandeControleMedical(controleMedical);

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
				int indiceHeureDebut = (Services.estNumerique(getVAL_LB_HEURE_DEBUT_SELECT()) ? Integer.parseInt(getVAL_LB_HEURE_DEBUT_SELECT()) : -1);
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
				if (getVAL_ST_DUREE().equals(Const.CHAINE_VIDE) && getVAL_ST_DUREE_MIN().equals(Const.CHAINE_VIDE)) {
					// "ERR002","La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "durée"));
					return false;
				}
				String dureeHeure = null != getVAL_ST_DUREE() ? getVAL_ST_DUREE() : "0";
				String dureeMinutes = null != getVAL_ST_DUREE_MIN() ? 1 == getVAL_ST_DUREE_MIN().length() ? "0" + getVAL_ST_DUREE_MIN() : getVAL_ST_DUREE_MIN() : "00";

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
				int indiceHeureFin = (Services.estNumerique(getVAL_LB_HEURE_FIN_SELECT()) ? Integer.parseInt(getVAL_LB_HEURE_FIN_SELECT()) : -1);
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
				int numOrga = (Services.estNumerique(getZone(getNOM_LB_OS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_OS_SELECT())) : -1);
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
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "commentaire"));
					return false;
				}
				dto.setCommentaire(getVAL_ST_MOTIF_CREATION());
			}
			// /////////////// PIECES JOINTES ////////////////////
			if (type.getTypeSaisiDto().isPieceJointe()) {
				if (null != listFichierUpload && !listFichierUpload.isEmpty()) {
					for (File file : listFichierUpload) {

						FileInputStream in = new FileInputStream(file);
						try {
							byte[] byteBuffer = new byte[in.available()];
							in.read(byteBuffer);
							PieceJointeDto pj = new PieceJointeDto();
							pj.setbFile(byteBuffer);
							pj.setTypeFile(new MimetypesFileTypeMap().getContentType(file));
							dto.getPiecesJointes().add(pj);
						} finally {
							in.close();
						}
					}
				}
			}

			// /////////////// RADIO BOUTON ETAT ////////////////////
			// #15893
			// on met NULL quand il s'agit de saisie sinon on applique le radio
			// bouton
			if (!getTypeCreation().getTypeSaisiDto().isSaisieKiosque()) {
				if (null == getVAL_RG_ETAT() || Const.CHAINE_VIDE.equals(getVAL_RG_ETAT())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "état"));
					return false;
				}
				RefEtatDto dtoEtat = new RefEtatDto();
				dtoEtat.setIdRefEtat(getZone(getNOM_RG_ETAT()).equals(getNOM_RB_ETAT_EN_ATTENTE()) ? EnumEtatAbsence.EN_ATTENTE.getCode() : EnumEtatAbsence.VALIDEE.getCode());
				dto.setEtatDto(dtoEtat);
			}

			// ////////////// MALADIES ////////////////
			if (type.getTypeSaisiDto().isPrescripteur()) {
				if (null == getVAL_ST_PRESCRIPTEUR() || Const.CHAINE_VIDE.equals(getVAL_ST_PRESCRIPTEUR().trim())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "prescripteur"));
					return false;
				}
				dto.setPrescripteur(getVAL_ST_PRESCRIPTEUR());
			}
			if (type.getTypeSaisiDto().isDateDeclaration()) {
				if (null == getVAL_ST_DATE_DECLARATION() || Const.CHAINE_VIDE.equals(getVAL_ST_DATE_DECLARATION().trim())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de déclaration"));
					return false;
				}
				dto.setDateDeclaration(sdf.parse(getVAL_ST_DATE_DECLARATION()));
			}
			if (type.getTypeSaisiDto().isProlongation()) {
				dto.setProlongation(null != getVAL_CK_PROLONGATION() && getVAL_CK_PROLONGATION().equals(getCHECKED_ON()));
			}
			if (type.getTypeSaisiDto().isNomEnfant()) {
				if (null == getVAL_ST_NOM_ENFANT() || Const.CHAINE_VIDE.equals(getVAL_ST_NOM_ENFANT().trim())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nom enfant"));
					return false;
				}
				dto.setNomEnfant(getVAL_ST_NOM_ENFANT());
			}
			if (type.getTypeSaisiDto().isNombreITT()) {
				if (null == getVAL_ST_NOMBRE_ITT() || Const.CHAINE_VIDE.equals(getVAL_ST_NOMBRE_ITT().trim())
						|| !Services.estFloat(getVAL_ST_NOMBRE_ITT())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nombre ITT"));
					return false;
				}
				dto.setNombreITT(new Double(getVAL_ST_NOMBRE_ITT()));
			}
			if (isTypeATOrRechuteAT(type)) {
				int numType = (Services.estNumerique(getZone(getNOM_LB_TYPE_AT_SELECT()))
						? Integer.parseInt(getZone(getNOM_LB_TYPE_AT_SELECT())) : -1);
				RefTypeDto typeAT = null;
				if (numType != -1) {
					typeAT = (RefTypeDto) getListeTypeAT().get(numType);
					dto.setTypeAccidentTravail(typeAT);
				} else {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type"));
					return false;
				}
			}
			if (type.getTypeSaisiDto().isSiegeLesion()) {
				int numTypeSiegeLesion = (Services.estNumerique(getZone(getNOM_LB_SIEGE_LESION_SELECT()))
						? Integer.parseInt(getZone(getNOM_LB_SIEGE_LESION_SELECT())) : -1);
				RefTypeDto typeSiegeLesion = null;
				if (numTypeSiegeLesion != -1) {
					typeSiegeLesion = (RefTypeDto) getListeSiegeLesion().get(numTypeSiegeLesion);
					dto.setTypeSiegeLesion(typeSiegeLesion);
				} else {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "siège lésion"));
					return false;
				}
			}
			if (type.getTypeSaisiDto().isMaladiePro()) {
				int numTypeMaladiePro = (Services.estNumerique(getZone(getNOM_LB_MALADIE_PRO_SELECT()))
						? Integer.parseInt(getZone(getNOM_LB_SIEGE_LESION_SELECT())) : -1);
				RefTypeDto typeMaladiePro = null;
				if (numTypeMaladiePro != -1) {
					typeMaladiePro = (RefTypeDto) getListeMaladiesPro().get(numTypeMaladiePro);
					dto.setTypeMaladiePro(typeMaladiePro);
				} else {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "maladie professionnelle"));
					return false;
				}
			}
			if (type.getTypeSaisiDto().isAtReference()) {
				int numATReference = (Services.estNumerique(getZone(getNOM_LB_AT_REFERENCE_SELECT()))
						? Integer.parseInt(getZone(getNOM_LB_AT_REFERENCE_SELECT())) : -1);
				DemandeDto atReference = null;
				if (numATReference != -1) {
					atReference = (DemandeDto) getListeATReference(ag.getIdAgent()).get(numATReference);
					dto.setAccidentTravailReference(atReference);
				} else {
					// "ERR002", "La zone @ est obligatoire."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "accident de travail de référence"));
					return false;
				}
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
			// /////////////// MOTIF ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isMotif()) {
				if (null == getVAL_ST_MOTIF_CREATION() || Const.CHAINE_VIDE.equals(getVAL_ST_MOTIF_CREATION().trim())) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "commentaire"));
					return false;
				}
				dto.setCommentaire(getVAL_ST_MOTIF_CREATION());
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
			
			if(null != getVAL_ST_DUREE()
					&& !"".equals(getVAL_ST_DUREE())
					&& Services.estFloat(getVAL_ST_DUREE())) {
				dto.setDuree(new Double(getVAL_ST_DUREE()));
				dto.setForceSaisieManuelleDuree(true);
			}
			// /////////////// PIECES JOINTES ////////////////////
			if (type.getTypeSaisiCongeAnnuelDto().isPieceJointe()) {
				if (null != listFichierUpload && !listFichierUpload.isEmpty()) {
					for (File file : listFichierUpload) {

						FileInputStream in = new FileInputStream(file);
						try {
							byte[] byteBuffer = new byte[in.available()];
							in.read(byteBuffer);
							PieceJointeDto pj = new PieceJointeDto();
							pj.setbFile(byteBuffer);
							pj.setTypeFile(new MimetypesFileTypeMap().getContentType(file));
							dto.getPiecesJointes().add(pj);
						} finally {
							in.close();
						}
					}
				}
			}
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

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(dto);

		ReturnMessageDto srm = absService.saveDemande(agentConnecte.getIdAgent(), json);

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

		listFichierUpload.clear();
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

	private String[] getLB_SIEGE_LESION() {
		if (LB_SIEGE_LESION == null)
			LB_SIEGE_LESION = initialiseLazyLB();
		return LB_SIEGE_LESION;
	}

	private void setLB_SIEGE_LESION(String[] newLB_SIEGE_LESION) {
		LB_SIEGE_LESION = newLB_SIEGE_LESION;
	}


	public String getNOM_LB_SIEGE_LESION() {
		return "NOM_LB_SIEGE_LESION";
	}

	public String getNOM_LB_SIEGE_LESION_SELECT() {
		return "NOM_LB_SIEGE_LESION_SELECT";
	}

	public String[] getVAL_LB_SIEGE_LESION() {
		return getLB_SIEGE_LESION();
	}

	public String getVAL_LB_SIEGE_LESION_SELECT() {
		return getZone(getNOM_LB_SIEGE_LESION_SELECT());
	}

	private String[] getLB_MALADIE_PRO() {
		if (LB_MALADIE_PRO == null)
			LB_MALADIE_PRO = initialiseLazyLB();
		return LB_MALADIE_PRO;
	}

	private void setLB_MALADIE_PRO(String[] newLB_MALADIE_PRO) {
		LB_MALADIE_PRO = newLB_MALADIE_PRO;
	}

	public String getNOM_LB_MALADIE_PRO() {
		return "NOM_LB_MALADIE_PRO";
	}

	public String getNOM_LB_MALADIE_PRO_SELECT() {
		return "NOM_LB_MALADIE_PRO_SELECT";
	}

	public String[] getVAL_LB_MALADIE_PRO() {
		return getLB_MALADIE_PRO();
	}

	public String getVAL_LB_MALADIE_PRO_SELECT() {
		return getZone(getNOM_LB_MALADIE_PRO_SELECT());
	}

	private String[] getLB_AT_REFERENCE() {
		if (LB_AT_REFERENCE == null)
			LB_AT_REFERENCE = initialiseLazyLB();
		return LB_AT_REFERENCE;
	}

	private void setLB_AT_REFERENCE(String[] newLB_AT_REFERENCE) {
		LB_AT_REFERENCE = newLB_AT_REFERENCE;
	}

	public String getNOM_LB_AT_REFERENCE() {
		return "NOM_LB_AT_REFERENCE";
	}

	public String getNOM_LB_AT_REFERENCE_SELECT() {
		return "NOM_LB_AT_REFERENCE_SELECT";
	}

	public String[] getVAL_LB_AT_REFERENCE() {
		return getLB_AT_REFERENCE();
	}

	public String getVAL_LB_AT_REFERENCE_SELECT() {
		return getZone(getNOM_LB_AT_REFERENCE_SELECT());
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

	private String[] getLB_GROUPE_CREATE() {
		if (LB_GROUPE_CREATE == null)
			LB_GROUPE_CREATE = initialiseLazyLB();
		return LB_GROUPE_CREATE;
	}

	public String[] getVAL_LB_GROUPE() {
		return getLB_GROUPE();
	}

	public String[] getVAL_LB_GROUPE_CREATE() {
		return getLB_GROUPE_CREATE();
	}

	private void setLB_GROUPE(String[] newLB_GROUPE) {
		LB_GROUPE = newLB_GROUPE;
	}

	private void setLB_GROUPE_CREATE(String[] newLB_GROUPE) {
		LB_GROUPE_CREATE = newLB_GROUPE;
	}

	public String getNOM_LB_GROUPE() {
		return "NOM_LB_GROUPE";
	}

	public String getNOM_LB_GROUPE_CREATE() {
		return "NOM_LB_GROUPE_CREATE";
	}

	public String getNOM_LB_GROUPE_SELECT() {
		return "NOM_LB_GROUPE_SELECT";
	}

	public String getVAL_LB_GROUPE_CREATE_SELECT() {
		return getZone(getNOM_LB_GROUPE_CREATE_SELECT());
	}

	public String getNOM_LB_GROUPE_CREATE_SELECT() {
		return "NOM_LB_GROUPE_CREATE_SELECT";
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

	public String getNOM_PB_SELECT_GROUPE_CREATE() {
		return "NOM_PB_SELECT_GROUPE_CREATE";
	}

	public String getNOM_LB_TYPE_AT() {
		return "NOM_LB_TYPE_AT";
	}

	public String getNOM_LB_TYPE_AT_SELECT() {
		return "NOM_LB_TYPE_AT_SELECT";
	}

	public String[] getVAL_LB_TYPE_AT() {
		return getLB_TYPE_AT();
	}

	private String[] getLB_TYPE_AT() {
		if (LB_TYPE_AT == null)
			LB_TYPE_AT = initialiseLazyLB();
		return LB_TYPE_AT;
	}

	private void setLB_TYPE_AT(String[] newLB_TYPE_AT) {
		LB_TYPE_AT = newLB_TYPE_AT;
	}

	public String getVAL_LB_TYPE_AT_SELECT() {
		return getZone(getNOM_LB_TYPE_AT_SELECT());
	}

	public boolean performPB_SELECT_GROUPE(HttpServletRequest request) throws Exception {

		// groupe
		int numGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_GROUPE_SELECT())) : -1);
		RefGroupeAbsenceDto groupe = null;
		if (numGroupe != -1 && numGroupe != 0) {
			groupe = (RefGroupeAbsenceDto) getListeGroupeAbsence().get(numGroupe - 1);
		}

		// on charge les familles
		if (groupe != null) {
			setListeFamilleAbsence((ArrayList<TypeAbsenceDto>) absService.getListeRefTypeAbsenceDto(groupe.getIdRefGroupeAbsence()));
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

	public boolean performPB_SELECT_GROUPE_CREATE(HttpServletRequest request) throws Exception {

		// groupe
		int numGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_CREATE_SELECT()))
				? Integer.parseInt(getZone(getNOM_LB_GROUPE_CREATE_SELECT())) : -1);
		RefGroupeAbsenceDto groupe = null;
		if (numGroupe != -1 && numGroupe != 0) {
			groupe = (RefGroupeAbsenceDto) getListeGroupeAbsence().get(numGroupe - 1);
		}

		// on charge les familles
		if (groupe != null) {
			setListeFamilleAbsenceCreation((ArrayList<TypeAbsenceDto>) absService.getListeRefTypeAbsenceDto(groupe.getIdRefGroupeAbsence()));
		} else {
			setListeFamilleAbsenceCreation(null);
		}

		int[] tailles = { 100 };
		FormateListe aFormat = new FormateListe(tailles);
		if (getListeFamilleAbsenceCreation() != null) {
			for (ListIterator<TypeAbsenceDto> list = getListeFamilleAbsenceCreation().listIterator(); list.hasNext();) {
				TypeAbsenceDto type = (TypeAbsenceDto) list.next();
				String ligne[] = { type.getLibelle() };
	
				aFormat.ajouteLigne(ligne);
			}
		}
		setLB_FAMILLE_CREATION(aFormat.getListeFormatee(true));
		addZone(getNOM_LB_FAMILLE_CREATION_SELECT(), Const.ZERO);
		return true;
	}

	public ArrayList<TypeAbsenceDto> getListeFamilleAbsenceVisualisation() {
		return listeFamilleAbsenceVisualisation;
	}

	public void setListeFamilleAbsenceVisualisation(ArrayList<TypeAbsenceDto> listeFamilleAbsenceVisualisation) {
		this.listeFamilleAbsenceVisualisation = listeFamilleAbsenceVisualisation;
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

		String dateDeb = getVAL_ST_DATE_MIN();
		String dateMin = dateDeb.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateDeb, "dd/MM/yyyy", "yyyyMMdd");

		String dateFin = getVAL_ST_DATE_MAX();
		String dateMax = dateFin.equals(Const.CHAINE_VIDE) ? null : Services.convertitDate(dateFin, "dd/MM/yyyy", "yyyyMMdd");

		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
		EnumEtatAbsence etat = null;
		if (numEtat != -1 && numEtat != 0) {
			etat = (EnumEtatAbsence) getListeEtats().get(numEtat - 1);
		}
		List<Integer> listeEtat = new ArrayList<Integer>();
		if (etat != null)
			listeEtat.add(etat.getCode());

		// famille
		int numType = (Services.estNumerique(getZone(getNOM_LB_FAMILLE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_FAMILLE_SELECT())) : -1);
		TypeAbsenceDto type = null;
		if (numType != -1 && numType != 0) {
			type = (TypeAbsenceDto) getListeFamilleAbsenceCreation().get(numType - 1);
		}
		// groupe
		int numGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_GROUPE_SELECT())) : -1);
		RefGroupeAbsenceDto groupe = null;
		if (numGroupe != -1 && numGroupe != 0) {
			groupe = (RefGroupeAbsenceDto) getListeGroupeAbsence().get(numGroupe - 1);
		}

		String idAgentDemande = getVAL_ST_AGENT_DEMANDE().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_DEMANDE();

		// GESTIONNAIRE
		int numGestionnaire = (Services.estNumerique(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_GESTIONNAIRE_SELECT())) : -1);
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
			List<Integer> listeService = new ArrayList<Integer>();
			for (ReferentRh service : listServiceRH) {
				listeService.add(service.getIdServiceAds());
			}
			List<Integer> listeSousServiceTmp = new ArrayList<Integer>();
			for (Integer idService : listeService) {
				listeSousServiceTmp.addAll(adsService.getListIdsEntiteWithEnfantsOfEntite(idService));
			}
			// on trie la liste des sous service pour supprimer les doublons
			ArrayList<Integer> listeSousService = new ArrayList<Integer>();
			for (Integer sousService : listeSousServiceTmp) {
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

		List<DemandeDto> listeDemande = absService.getListeDemandes(dateMin, dateMax, listeEtat.size() == 0 ? null : listeEtat.toString().replace("[", "").replace("]", "").replace(" ", ""),
				type == null ? null : type.getIdRefTypeAbsence(), idAgentDemande == null ? null : Integer.valueOf(idAgentDemande), groupe == null ? null : groupe.getIdRefGroupeAbsence(), true,
				idAgentService);

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
		if (!getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE) && (!getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE) || !getVAL_ST_DATE_REPRISE().equals(Const.CHAINE_VIDE))) {
			AgentWithServiceDto agentdto = new AgentWithServiceDto();
			agentdto.setIdAgent(getAgentCreation().getIdAgent());
			DemandeDto demandeCreation = new DemandeDto();
			demandeCreation.setAgentWithServiceDto(agentdto);
			demandeCreation.setDateDebut(sdf.parse(getVAL_ST_DATE_DEBUT()));
			demandeCreation.setDateFin(getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_ST_DATE_FIN()));
			demandeCreation.setDateReprise(getVAL_ST_DATE_REPRISE().equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_ST_DATE_REPRISE()));
			demandeCreation.setDateDebutAM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_M()));
			demandeCreation.setDateDebutPM(getZone(getNOM_RG_DEBUT_MAM()).equals(getNOM_RB_AM()));
			demandeCreation.setDateFinAM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_M()));
			demandeCreation.setDateFinPM(getZone(getNOM_RG_FIN_MAM()).equals(getNOM_RB_AM()));
			String res = getCalculDureeCongeAnnuel(getTypeCreation().getTypeSaisiCongeAnnuelDto().getCodeBaseHoraireAbsence(), demandeCreation);

			addZone(getNOM_ST_DUREE(), res);
		}
		return true;
	}

	public String getCalculDureeCongeAnnuel(String codeBaseHoraireAbsence, DemandeDto demandeDto) {
		if (demandeDto.getDateDebut() != null && (demandeDto.getDateFin() != null || demandeDto.getDateReprise() != null)) {
			demandeDto.setTypeSaisiCongeAnnuel(getTypeCreation().getTypeSaisiCongeAnnuelDto());
			DemandeDto dureeDto = absService.getDureeCongeAnnuel(demandeDto);
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

	public String getNOM_RG_ETAT() {
		return "NOM_RG_ETAT";
	}

	public String getVAL_RG_ETAT() {
		return getZone(getNOM_RG_ETAT());
	}

	public String getNOM_RB_ETAT_EN_ATTENTE() {
		return "NOM_RB_ETAT_EN_ATTENTE";
	}

	public String getNOM_RB_ETAT_VALIDE() {
		return "NOM_RB_ETAT_VALIDE";
	}

	public IAbsService getAbsService() {
		if (null == absService) {
			ApplicationContext context = ApplicationContextProvider.getContext();
			absService = (AbsService) context.getBean("absService");
		}
		return absService;
	}

	public void setAbsService(IAbsService absService) {
		this.absService = absService;
	}

	public ArrayList<RefTypeDto> getListeMaladiesPro() {

		if (null == listeMaladiesPro) {
			setListeMaladiesPro((ArrayList<RefTypeDto>) absService.getRefTypeMaladiePro());
		}

		return listeMaladiesPro;
	}

	public void setListeMaladiesPro(ArrayList<RefTypeDto> listeMaladiesPro) {
		this.listeMaladiesPro = listeMaladiesPro;
	}

	public ArrayList<RefTypeDto> getListeSiegeLesion() {

		if (null == listeSiegeLesion || listeSiegeLesion.isEmpty()) {
			setListeSiegeLesion((ArrayList<RefTypeDto>) absService.getRefTypeSiegeLesion());
		}

		return listeSiegeLesion;
	}

	public void setListeSiegeLesion(ArrayList<RefTypeDto> listeSiegeLesion) {
		this.listeSiegeLesion = listeSiegeLesion;
	}

	public ArrayList<RefTypeDto> getListeTypeAT() {

		if (null == listeTypeAT || listeTypeAT.isEmpty()) {
			setListeTypeAT((ArrayList<RefTypeDto>) absService.getRefTypeAccidentTravail());
		}

		return listeTypeAT;
	}

	public void setListeTypeAT(ArrayList<RefTypeDto> listeTypeAT) {
		this.listeTypeAT = listeTypeAT;
	}

	public ArrayList<DemandeDto> getListeATReference(Integer idAgent) {

		if (null == idAgent) {
			return new ArrayList<DemandeDto>();
		}

		if (null == listeATReference || listeATReference.isEmpty()
				|| !listeATReference.get(0).getAgentWithServiceDto().getIdAgent().equals(idAgent)) {
			List<DemandeDto> listeATReference = absService.getListeDemandesAgent(new Integer(idAgent), "TOUTES", null, null,
					null, Arrays.asList(EnumEtatAbsence.VALIDEE.getCode(), EnumEtatAbsence.PRISE.getCode()).toString().replace("[", "")
							.replace("]", "").replace(" ", ""),
					EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode(), EnumTypeGroupeAbsence.MALADIES.getValue());

			Collections.sort(listeATReference, new DemandeDtoDateDeclarationComparator());
			setListeATReference((ArrayList<DemandeDto>) listeATReference);
		}

		return listeATReference;
	}

	public void setListeATReference(ArrayList<DemandeDto> listeATReference) {
		this.listeATReference = listeATReference;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	/**
	 * méthode qui teste si un parametre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null
				|| (multi != null && multi.getParameter(param) != null));
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {
			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				File file = multi.getFile(getNOM_EF_LIENDOCUMENT());
				if (null != listFichierUpload) {
					boolean isAjout = true;
					for (File fileTmp : listFichierUpload) {
						if (fileTmp.getName().equals(file.getName())) {
							isAjout = false;
							break;
						}
					}
					if (isAjout)
						listFichierUpload.add(file);
				}
			}

			addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		}

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT
	 */
	public String getNOM_ST_ACTION_DOCUMENT() {
		return "NOM_ST_ACTION_DOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT
	 */
	public String getVAL_ST_ACTION_DOCUMENT() {
		return getZone(getNOM_ST_ACTION_DOCUMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT
	 */
	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT
	 */
	public String getVAL_PB_VALIDER_DOCUMENT_CREATION() {
		return getZone(getNOM_PB_VALIDER_DOCUMENT_CREATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT
	 */
	public String getNOM_PB_VALIDER_DOCUMENT_AJOUT() {
		return "PB_VALIDER_DOCUMENT_AJOUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT
	 */
	public String getVAL_PB_VALIDER_DOCUMENT_AJOUT() {
		return getZone(getNOM_PB_VALIDER_DOCUMENT_AJOUT());
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC
	 */
	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC
	 */
	public String getNOM_PB_AJOUTER_DOC(int i) {
		return "NOM_PB_CREER_DOC_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, String nameFile) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION_DOCUMENT(), Const.CHAINE_VIDE);
		isImporting = false;

		// Récup du Diplome courant
		if (null != listFichierUpload && !listFichierUpload.isEmpty()) {
			for (File file : listFichierUpload) {
				if (file.getName().equals(nameFile)) {
					listFichierUpload.remove(file);
					break;
				}
			}
		}

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_DOC
	 */
	public String getNOM_PB_SUPPRIMER_DOC(String fileName) {
		return "NOM_PB_SUPPRIMER_DOC" + fileName;
	}

	public DemandeDto getDemandeCourante() {
		return demandeCourante;
	}

	public void setDemandeCourante(DemandeDto demandeCourante) {
		this.demandeCourante = demandeCourante;
	}

	public String getNOM_PB_SUPPRIMER_DOC(int idAbs, int idPJ) {
		return "NOM_PB_SUPPRIMER_DOC" + idAbs + "_" + idPJ;
	}

	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, DemandeDto demande, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup
		PieceJointeDto doc = (PieceJointeDto) demande.getPiecesJointes().get(indiceEltASuprimer);

		// init des documents courant
		if (!initialiseDocumentSuppression(request, doc))
			return false;

		setDemandeCourante(demande);
		setDocumentCourant(doc);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request, PieceJointeDto doc) throws Exception {

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), doc.getTitre());

		return true;
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION() {
		return "NOM_PB_VALIDER_DOCUMENT_SUPPRESSION";
	}

	private boolean performPB_VALIDER_DOCUMENT_SUPPRESSION(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		getDemandeCourante().getPiecesJointes().remove(getDocumentCourant());

		getDemandeCourante().setFromHSCT(false);

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(getDemandeCourante());

		ReturnMessageDto srm = absService.saveDemande(getAgentConnecte(request).getIdAgent(), json);

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
		logger.debug("Document supprime de la demande");

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		performPB_FILTRER(request);

		return true;
	}
	
	public String getCommentaireDRH(DemandeDto abs){
		return abs.getCommentaireDRH()==null ? Const.CHAINE_VIDE : abs.getCommentaireDRH();
	}


	public String getNOM_PB_COMMENTAIRE_DRH(int i) {
		return "NOM_PB_COMMENTAIRE_DRH" + i;
	}

	public boolean performPB_COMMENTAIRE_DRH(HttpServletRequest request, int idDemande) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		if (getAgentConnecte(request) == null) {
			return false;
		}
		// on recupere la demande
		DemandeDto dem = getListeAbsence().get(idDemande);
		setDemandeCourante(dem);

		addZone(getNOM_ST_COMMENTAIRE_DRH(), getDemandeCourante().getCommentaire()==null ? Const.CHAINE_VIDE : getDemandeCourante().getCommentaireDRH());


		// On nomme l'action		
		addZone(getNOM_ST_ACTION(), ACTION_COMMENTAIRE_DRH);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_COMMENTAIRE_DRH() {
		return "NOM_ST_COMMENTAIRE_DRH";
	}

	public String getVAL_ST_COMMENTAIRE_DRH() {
		return getZone(getNOM_ST_COMMENTAIRE_DRH());
	}

	public String getNOM_PB_VALIDER_COMMENTAIRE_DRH() {
		return "NOM_PB_VALIDER_COMMENTAIRE_DRH";
	}
	
	public boolean performPB_VALIDER_COMMENTAIRE_DRH(HttpServletRequest request) throws Exception {
		// on recupere les infos
		String commDRH = getVAL_ST_COMMENTAIRE_DRH();
		if (commDRH.equals(Const.CHAINE_VIDE)) {
			commDRH=null;
		}
		//on sauvegarde
		ReturnMessageDto srm = absService.updateCommentaireDRH(getDemandeCourante(),commDRH);
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
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		if (getTypeFiltre().equals("GLOBAL")) {
			performPB_FILTRER(request);
		} else {
			performPB_FILTRER_DEMANDE_A_VALIDER(request);
		}
		
		return true;
	}

}
