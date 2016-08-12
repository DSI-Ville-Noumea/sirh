package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.hsct.AccidentTravail;
import nc.mairie.metier.hsct.SiegeLesion;
import nc.mairie.metier.hsct.TypeAT;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.hsct.AccidentTravailDao;
import nc.mairie.spring.dao.metier.hsct.SiegeLesionDao;
import nc.mairie.spring.dao.metier.hsct.TypeATDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.mairie.alfresco.cmis.CmisUtils;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.cmis.AlfrescoCMISService;
import nc.noumea.spring.service.cmis.IAlfrescoCMISService;

import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeAGENTAccidentTravail extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_SIEGE_LESION;
	private String[] LB_TYPE;

	private Agent agentCourant;
	private AccidentTravail accidentTravailCourant;

	private ArrayList<AccidentTravail> listeAT;
	private ArrayList<TypeAT> listeTypeAT;
	private ArrayList<SiegeLesion> listeSiegeLesion;

	private Hashtable<Integer, TypeAT> hashTypeAT;
	private Hashtable<Integer, SiegeLesion> hashSiegeLesion;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche AT.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche AT.";
	private String ACTION_MODIFICATION = "Modification d'une fiche AT.";
	private String ACTION_CREATION = "Création d'une fiche AT.";

	public String ACTION_DOCUMENT = "Documents d'une fiche AT.";
	public String ACTION_DOCUMENT_SUPPRESSION = "Suppression d'un document d'une fiche AT.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document d'une fiche AT.";
	private ArrayList<Document> listeDocuments;
	private Document documentCourant;
	private DocumentAgent lienDocumentAgentCourant;
	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

	private TypeDocumentDao typeDocumentDao;
	private AccidentTravailDao accidentTravailDao;
	private SiegeLesionDao siegeLesionDao;
	private TypeATDao typeATDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private AgentDao agentDao;

	private IAlfrescoCMISService alfrescoCMISService;
	private IRadiService radiService;

	/**
	 * Constructeur du process OeAGENTAccidentTravail. Date de création :
	 * (30/06/11 13:56:32)
	 * 
	 */
	public OeAGENTAccidentTravail() {
		super();
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (30/06/11 14:19:10)
	 * 
	 * RG_AG_AT_C01 RG_AG_AT_C02
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setAccidentTravailCourant(null);
			multi = null;
			isImporting = false;
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Si hashtable des types d'accident de travail vide
		// RG_AG_AT_C01
		if (getHashTypeAT().size() == 0) {
			ArrayList<TypeAT> listeTypeAT = getTypeATDao().listerTypeAT();
			setListeTypeAT(listeTypeAT);

			if (getListeTypeAT().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<TypeAT> list = getListeTypeAT().listIterator(); list.hasNext();) {
					TypeAT m = (TypeAT) list.next();
					String ligne[] = { m.getDescTypeAt() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeTypeAT.size(); i++) {
				TypeAT t = (TypeAT) listeTypeAT.get(i);
				getHashTypeAT().put(t.getIdTypeAt(), t);
			}
		}

		// Si hashtable des sieges de lésion vide
		// RG_AG_AT_C02

		if (getHashSiegeLesion().size() == 0) {
			ArrayList<SiegeLesion> listeSiegeLesion = getSiegeLesionDao().listerSiegeLesion();
			setListeSiegeLesion(listeSiegeLesion);

			if (getListeSiegeLesion().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<SiegeLesion> list = getListeSiegeLesion().listIterator(); list.hasNext();) {
					SiegeLesion m = (SiegeLesion) list.next();
					String ligne[] = { m.getDescSiege() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_SIEGE_LESION(aFormat.getListeFormatee(true));
			} else {
				setLB_SIEGE_LESION(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeSiegeLesion.size(); i++) {
				SiegeLesion s = (SiegeLesion) listeSiegeLesion.get(i);
				getHashSiegeLesion().put(s.getIdSiege(), s);
			}
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeAT(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAccidentTravailDao() == null) {
			setAccidentTravailDao(new AccidentTravailDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSiegeLesionDao() == null) {
			setSiegeLesionDao(new SiegeLesionDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeATDao() == null) {
			setTypeATDao(new TypeATDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == alfrescoCMISService) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
		}
	}

	/**
	 * Initialisation de la liste des accidents du travail de l'agent courant
	 * Date de création : (30/06/11)
	 */
	private void initialiseListeAT(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des accidents du travail de l'agent
		ArrayList<AccidentTravail> listeAT = getAccidentTravailDao().listerAccidentTravailAgent(
				getAgentCourant().getIdAgent());
		setListeAT(listeAT);

		int indiceAcc = 0;
		if (getListeAT() != null) {
			for (int i = 0; i < getListeAT().size(); i++) {
				AccidentTravail at = (AccidentTravail) getListeAT().get(i);
				TypeAT t = (TypeAT) getHashTypeAT().get(at.getIdTypeAt());
				SiegeLesion s = (SiegeLesion) getHashSiegeLesion().get(at.getIdSiege());
				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgentTYPE(getLienDocumentAgentDao(),
						getAgentCourant().getIdAgent(), "HSCT", "AT", at.getIdAt());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}

				addZone(getNOM_ST_DATE(indiceAcc), sdf.format(at.getDateAt()));
				addZone(getNOM_ST_DATE_RECHUTE(indiceAcc),
						at.getDateAtInitial() == null ? "&nbsp;" : sdf.format(at.getDateAtInitial()));
				addZone(getNOM_ST_NB_JOURS(indiceAcc), at.getNbJoursItt() == null ? "&nbsp;" : at.getNbJoursItt()
						.toString());
				addZone(getNOM_ST_TYPE(indiceAcc),
						t.getDescTypeAt().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getDescTypeAt());
				addZone(getNOM_ST_SIEGE(indiceAcc),
						s.getDescSiege().equals(Const.CHAINE_VIDE) ? "&nbsp;" : s.getDescSiege());
				addZone(getNOM_ST_NB_DOC(indiceAcc), nbDoc == 0 ? "&nbsp;" : String.valueOf(nbDoc));

				indiceAcc++;
			}
		}
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de la visite courante
		setAccidentTravailCourant(new AccidentTravail());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'un
	 * accident de travail
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_EF_DATE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NB_JOUR_IIT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_SELECT(), "0");
		addZone(getNOM_LB_SIEGE_LESION_SELECT(), "0");
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'un accident
	 * de travail Date de création : 11/07/01
	 */
	private boolean initialiseATCourant(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		TypeAT type = (TypeAT) getHashTypeAT().get(getAccidentTravailCourant().getIdTypeAt());
		SiegeLesion siege = (SiegeLesion) getHashSiegeLesion().get(getAccidentTravailCourant().getIdSiege());

		// Alim zones
		addZone(getNOM_EF_DATE(), sdf.format(getAccidentTravailCourant().getDateAt()));
		addZone(getNOM_EF_DATE_INITIALE(), getAccidentTravailCourant().getDateAtInitial() == null ? Const.CHAINE_VIDE
				: sdf.format(getAccidentTravailCourant().getDateAtInitial()));
		addZone(getNOM_EF_NB_JOUR_IIT(), getAccidentTravailCourant().getNbJoursItt() == null ? Const.CHAINE_VIDE
				: getAccidentTravailCourant().getNbJoursItt().toString());

		int ligneType = getListeTypeAT().indexOf(type);
		addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(ligneType + 1));
		addZone(getNOM_ST_TYPE(), type.getDescTypeAt());

		int ligneSiege = getListeSiegeLesion().indexOf(siege);
		addZone(getNOM_LB_SIEGE_LESION_SELECT(), String.valueOf(ligneSiege + 1));
		addZone(getNOM_ST_SIEGE_LESION(), siege.getDescSiege());

		return true;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/*
	 * RG_AG_AT_A02
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {

			// Suppression
			getAccidentTravailDao().supprimerAccidentTravail(getAccidentTravailCourant().getIdAt());
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			// RG_AG_AT_A02
			PositionAdmAgent dernPosAdmn = PositionAdmAgent.chercherPositionAdmAgentDateComprise(getTransaction(),
					getAgentCourant().getNomatr(),
					Services.convertitDate(Services.formateDate(getZone(getNOM_EF_DATE())), "dd/MM/yyyy", "yyyyMMdd"));
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR132"));
				return false;
			} else {
				if (!dernPosAdmn.permetAT()) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR090", "accidents du travail"));
					return false;
				}
			}

			// récupération des informations remplies dans les zones de saisie
			String date = Services.formateDate(getZone(getNOM_EF_DATE()));
			String dateInit = getZone(getNOM_EF_DATE_INITIALE()).equals(Const.CHAINE_VIDE) ? null : Services
					.formateDate(getZone(getNOM_EF_DATE_INITIALE()));
			String duree = getZone(getNOM_EF_NB_JOUR_IIT());

			int numLigneType = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);

			if (numLigneType == -1 || getListeTypeAT().size() == 0 || numLigneType > getListeTypeAT().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types"));
				return false;
			}

			TypeAT type = (TypeAT) getListeTypeAT().get(numLigneType - 1);

			int numLigneSiege = (Services.estNumerique(getZone(getNOM_LB_SIEGE_LESION_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_SIEGE_LESION_SELECT())) : -1);

			if (numLigneSiege == -1 || getListeSiegeLesion().size() == 0
					|| numLigneSiege > getListeSiegeLesion().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "sièges de lésion"));
				return false;
			}

			SiegeLesion siege = (SiegeLesion) getListeSiegeLesion().get(numLigneSiege - 1);

			// Vérification si la PA de l'agent donne le droit a accidents du
			// travail.
			// RG_AG_AT_A02
			ArrayList<PositionAdmAgent> listePA = PositionAdmAgent.listerPositionAdmAgentAvecAgent(getTransaction(),
					getAgentCourant());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			for (PositionAdmAgent pa : listePA) {
				if (Services.compareDates(pa.getDatdeb(), date) <= 0
						&& (pa.getDatfin() == null || Services.compareDates(pa.getDatfin(), date) >= 0)) {
					if (!pa.permetAT()) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR090", "accidents du travail"));
						return false;
					}
					break;
				}
			}

			// Création de l'objet VisiteMedicale a créer/modifier
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Agent agentCourant = getAgentCourant();
			getAccidentTravailCourant().setIdAgent(agentCourant.getIdAgent());
			getAccidentTravailCourant().setDateAt(sdf.parse(date));
			getAccidentTravailCourant().setDateAtInitial(dateInit == null ? null : sdf.parse(dateInit));
			getAccidentTravailCourant().setNbJoursItt(duree.equals(Const.CHAINE_VIDE) ? null : Integer.valueOf(duree));
			getAccidentTravailCourant().setIdTypeAt(type.getIdTypeAt());
			getAccidentTravailCourant().setIdSiege(siege.getIdSiege());

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getAccidentTravailDao().modifierAccidentTravail(getAccidentTravailCourant().getIdAt(),
						getAccidentTravailCourant().getIdTypeAt(), getAccidentTravailCourant().getIdSiege(),
						getAccidentTravailCourant().getIdAgent(), getAccidentTravailCourant().getDateAt(),
						getAccidentTravailCourant().getDateAtInitial(), getAccidentTravailCourant().getNbJoursItt());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				getAccidentTravailDao().creerAccidentTravail(getAccidentTravailCourant().getIdTypeAt(),
						getAccidentTravailCourant().getIdSiege(), getAccidentTravailCourant().getIdAgent(),
						getAccidentTravailCourant().getDateAt(), getAccidentTravailCourant().getDateAtInitial(),
						getAccidentTravailCourant().getNbJoursItt());
			}
			if (getTransaction().isErreur())
				return false;
		}

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien passé
		commitTransaction();
		initialiseListeAT(request);

		return true;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 *             RG_AG_AT_A01
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// date de l'accident du travail
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date d'AT"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_EF_DATE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'AT"));
			return false;
		} else if (!Const.CHAINE_VIDE.equals(getVAL_EF_NB_JOUR_IIT())) {

			// verification des regles de gestions
			// RG_AG_AT_A01
			ArrayList<AccidentTravail> listeAT = getListeAT();
			for (int i = 0; i < listeAT.size(); i++) {
				AccidentTravail at = (AccidentTravail) listeAT.get(i);

				if (Services.compareDates(getZone(getNOM_EF_DATE()), sdf.format(at.getDateAt())) == -1) {
					int resultat = Services.compareDates(
							Services.ajouteJours(Services.formateDate(getZone(getNOM_EF_DATE())),
									Integer.parseInt(getZone(getNOM_EF_NB_JOUR_IIT()))), sdf.format(at.getDateAt()));
					if (resultat == 1) {
						// erreur
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR050"));
						return false;
					}
				} else if (Services.compareDates(getZone(getNOM_EF_DATE()), sdf.format(at.getDateAt())) == 1) {
					int resultat = Services.compareDates(Services.ajouteJours(
							Services.formateDate(sdf.format(at.getDateAt())),
							at.getNbJoursItt() == null ? 0 : at.getNbJoursItt()), getZone(getNOM_EF_DATE()));
					if (resultat == 1) {
						// erreur
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR050"));
						return false;
					}
				}
			}
		}

		// dae d'AT initial
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_INITIALE()))
				&& !Services.estUneDate(getZone(getNOM_EF_DATE_INITIALE()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'AT Initial"));
			return false;
		}

		// duree ITT
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_NB_JOUR_IIT()))
				&& !Services.estNumerique(getZone(getNOM_EF_NB_JOUR_IIT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "durée ITT"));
			return false;
		}

		// type AT obligatoire
		int indiceMedecin = (Services.estNumerique(getVAL_LB_TYPE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_SELECT())
				: -1);
		if (indiceMedecin < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type"));
			return false;
		}

		// siege des lésions obligatoire
		int indiceRecommandation = (Services.estNumerique(getVAL_LB_SIEGE_LESION_SELECT()) ? Integer
				.parseInt(getVAL_LB_SIEGE_LESION_SELECT()) : -1);
		if (indiceRecommandation < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "siège des lésions"));
			return false;
		}

		return true;
	}

	public String getNOM_ST_DATE() {
		return "NOM_ST_DATE";
	}

	public String getVAL_ST_DATE() {
		return getZone(getNOM_ST_DATE());
	}

	public String getNOM_ST_NB_JOUR_IIT() {
		return "NOM_ST_NB_JOUR_IIT";
	}

	public String getVAL_ST_NB_JOUR_IIT() {
		return getZone(getNOM_ST_NB_JOUR_IIT());
	}

	public String getNOM_ST_SIEGE_LESION() {
		return "NOM_ST_SIEGE_LESION";
	}

	public String getVAL_ST_SIEGE_LESION() {
		return getZone(getNOM_ST_SIEGE_LESION());
	}

	public String getNOM_ST_TYPE() {
		return "NOM_ST_TYPE";
	}

	public String getVAL_ST_TYPE() {
		return getZone(getNOM_ST_TYPE());
	}

	public String getNOM_EF_DATE() {
		return "NOM_EF_DATE";
	}

	public String getVAL_EF_DATE() {
		return getZone(getNOM_EF_DATE());
	}

	public String getNOM_EF_NB_JOUR_IIT() {
		return "NOM_EF_NB_JOUR_IIT";
	}

	public String getVAL_EF_NB_JOUR_IIT() {
		return getZone(getNOM_EF_NB_JOUR_IIT());
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

	private String[] getLB_TYPE() {
		if (LB_TYPE == null)
			LB_TYPE = initialiseLazyLB();
		return LB_TYPE;
	}

	private void setLB_TYPE(String[] newLB_TYPE) {
		LB_TYPE = newLB_TYPE;
	}

	public String getNOM_LB_TYPE() {
		return "NOM_LB_TYPE";
	}

	public String getNOM_LB_TYPE_SELECT() {
		return "NOM_LB_TYPE_SELECT";
	}

	public String[] getVAL_LB_TYPE() {
		return getLB_TYPE();
	}

	public String getVAL_LB_TYPE_SELECT() {
		return getZone(getNOM_LB_TYPE_SELECT());
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private AccidentTravail getAccidentTravailCourant() {
		return accidentTravailCourant;
	}

	private void setAccidentTravailCourant(AccidentTravail accidentTravailCourant) {
		this.accidentTravailCourant = accidentTravailCourant;
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	private Hashtable<Integer, SiegeLesion> getHashSiegeLesion() {
		if (hashSiegeLesion == null) {
			hashSiegeLesion = new Hashtable<Integer, SiegeLesion>();
		}
		return hashSiegeLesion;
	}

	private Hashtable<Integer, TypeAT> getHashTypeAT() {
		if (hashTypeAT == null) {
			hashTypeAT = new Hashtable<Integer, TypeAT>();
		}
		return hashTypeAT;
	}

	public ArrayList<AccidentTravail> getListeAT() {
		return listeAT;
	}

	private void setListeAT(ArrayList<AccidentTravail> listeAT) {
		this.listeAT = listeAT;
	}

	private ArrayList<SiegeLesion> getListeSiegeLesion() {
		return listeSiegeLesion;
	}

	private void setListeSiegeLesion(ArrayList<SiegeLesion> listeSiegeLesion) {
		this.listeSiegeLesion = listeSiegeLesion;
	}

	private ArrayList<TypeAT> getListeTypeAT() {
		return listeTypeAT;
	}

	private void setListeTypeAT(ArrayList<TypeAT> listeTypeAT) {
		this.listeTypeAT = listeTypeAT;
	}

	public String getNOM_ST_DATE_INITIALE() {
		return "NOM_ST_DATE_INITIALE";
	}

	public String getVAL_ST_DATE_INITIALE() {
		return getZone(getNOM_ST_DATE_INITIALE());
	}

	public String getNOM_EF_DATE_INITIALE() {
		return "NOM_EF_DATE_INITIALE";
	}

	public String getVAL_EF_DATE_INITIALE() {
		return getZone(getNOM_EF_DATE_INITIALE());
	}

	public String getNomEcran() {
		return "ECR-AG-HSCT-ACCTRAVAIL";
	}

	public boolean recupererStatut(HttpServletRequest request) throws Exception {

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
			for (int i = 0; i < getListeAT().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeAT().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeAT().size(); i++) {
				if (testerParametre(request, getNOM_PB_DOCUMENT(i))) {
					return performPB_DOCUMENT(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_CREER_DOC())) {
				return performPB_CREER_DOC(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(i))) {
					return performPB_SUPPRIMER_DOC(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_SUPPRESSION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION())) {
				return performPB_VALIDER_DOCUMENT_SUPPRESSION(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_CREATION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION())) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getJSP() {
		return "OeAGENTAccidentTravail.jsp";
	}

	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE" + i;
	}

	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	public String getNOM_ST_DATE_RECHUTE(int i) {
		return "NOM_ST_DATE_RECHUTE" + i;
	}

	public String getVAL_ST_DATE_RECHUTE(int i) {
		return getZone(getNOM_ST_DATE_RECHUTE(i));
	}

	public String getNOM_ST_NB_JOURS(int i) {
		return "NOM_ST_NB_JOURS" + i;
	}

	public String getVAL_ST_NB_JOURS(int i) {
		return getZone(getNOM_ST_NB_JOURS(i));
	}

	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE" + i;
	}

	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	public String getNOM_ST_SIEGE(int i) {
		return "NOM_ST_SIEGE" + i;
	}

	public String getVAL_ST_SIEGE(int i) {
		return getZone(getNOM_ST_SIEGE(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		AccidentTravail accidentCourant = (AccidentTravail) getListeAT().get(indiceEltAModifier);
		setAccidentTravailCourant(accidentCourant);

		// init du diplome courant
		if (!initialiseATCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		AccidentTravail accidentCourant = (AccidentTravail) getListeAT().get(indiceEltAConsulter);
		setAccidentTravailCourant(accidentCourant);

		// init du diplome courant
		if (!initialiseATCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		AccidentTravail accCourant = (AccidentTravail) getListeAT().get(indiceEltDocument);
		setAccidentTravailCourant(accCourant);

		// init des documents AT de l'agent
		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void setListeDocuments(ArrayList<Document> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<Document>();
		}
		return listeDocuments;
	}

	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des documents de l'agent
		ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgentTYPE(getLienDocumentAgentDao(),
				getAgentCourant().getIdAgent(), "HSCT", CmisUtils.CODE_TYPE_AT, getAccidentTravailCourant().getIdAt());
		setListeDocuments(listeDocAgent);

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) getTypeDocumentDao().chercherTypeDocument(doc.getIdTypeDocument());

				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(indiceActeVM),
						doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_TYPE_DOC(indiceActeVM), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM),
						doc.getDateDocument() == null ? "&nbsp;" : sdf.format(doc.getDateDocument()));
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());
				addZone(getNOM_ST_URL_DOC(indiceActeVM),
						(null == doc.getNodeRefAlfresco()
							|| doc.getNodeRefAlfresco().equals(Const.CHAINE_VIDE))
							? "&nbsp;" 
							: AlfrescoCMISService.getUrlOfDocument(doc.getNodeRefAlfresco()));

				indiceActeVM++;
			}
		}
	}

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
	}

	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	public String getNOM_ST_TYPE_DOC(int i) {
		return "NOM_ST_TYPE_DOC" + i;
	}

	public String getVAL_ST_TYPE_DOC(int i) {
		return getZone(getNOM_ST_TYPE_DOC(i));
	}

	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	public String getNOM_ST_NB_DOC(int i) {
		return "NOM_ST_NB_DOC" + i;
	}

	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
	}

	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		Document d = (Document) getListeDocuments().get(indiceEltASuprimer);
		setDocumentCourant(d);

		// init des documents courant
		if (!initialiseDocumentSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Récup du Diplome courant
		Document d = getDocumentCourant();

		DocumentAgent lda = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(),
				getDocumentCourant().getIdDocument());
		setLienDocumentAgentCourant(lda);

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_NOM_ORI_DOC(), d.getNomOriginal());
		addZone(getNOM_ST_DATE_DOC(), d.getDateDocument() == null ? Const.CHAINE_VIDE : sdf.format(d.getDateDocument()));
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
	}

	private Document getDocumentCourant() {
		return documentCourant;
	}

	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	private DocumentAgent getLienDocumentAgentCourant() {
		return lienDocumentAgentCourant;
	}

	private void setLienDocumentAgentCourant(DocumentAgent lienDocumentAgentCourant) {
		this.lienDocumentAgentCourant = lienDocumentAgentCourant;
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getVAL_ST_DATE_DOC() {
		return getZone(getNOM_ST_DATE_DOC());
	}

	public String getNOM_ST_DATE_DOC() {
		return "NOM_ST_DATE_DOC";
	}

	public String getVAL_ST_COMMENTAIRE_DOC() {
		return getZone(getNOM_ST_COMMENTAIRE_DOC());
	}

	public String getNOM_ST_COMMENTAIRE_DOC() {
		return "NOM_ST_COMMENTAIRE_DOC";
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
		
		ReturnMessageDto rmd = alfrescoCMISService.removeDocument(getDocumentCourant());
		if (declarerErreurFromReturnMessageDto(rmd))
			return false;
		
		// suppression dans table DOCUMENT_AGENT
		getLienDocumentAgentDao().supprimerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(),
				getLienDocumentAgentCourant().getIdDocument());
		// Suppression dans la table DOCUMENT_ASSOCIE
		getDocumentDao().supprimerDocument(getDocumentCourant().getIdDocument());

		// tout s'est bien passé
		commitTransaction();
		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		initialiseListeDocuments(request);
		return true;
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
	}

	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Controle des champs
		if (!performControlerSaisieDocument(request))
			return false;

		AccidentTravail at = getAccidentTravailCourant();

		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {

			if (!creeDocument(request, at)) {
				return false;
			}

		} else {
			// on supprime le document existant dans la base de données
			Document d = getDocumentDao().chercherDocumentByContainsNom("AT_" + at.getIdAt());
			DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(),
					d.getIdDocument());

			ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
			if (declarerErreurFromReturnMessageDto(rmd))
				return false;
			
			getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
			getDocumentDao().supprimerDocument(d.getIdDocument());

			if (!creeDocument(request, at)) {
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met a jour le tableau des AT pour avoir le nombre de documents
		initialiseListeAT(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, AccidentTravail at) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupere le type de document
		String codTypeDoc = CmisUtils.CODE_TYPE_AT;
		TypeDocument td = getTypeDocumentDao().chercherTypeDocumentByCod(codTypeDoc);

		// on crée le document en base de données
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomOriginal(fichierUpload.getName());
		getDocumentCourant().setDateDocument(new Date());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE()));
		getDocumentCourant().setReference(at.getIdAt());
		
		// on upload le fichier
		ReturnMessageDto rmd = alfrescoCMISService.uploadDocument(getAgentConnecte(request).getIdAgent(), getAgentCourant(), getDocumentCourant(), 
				fichierUpload, codTypeDoc);
		
		if (declarerErreurFromReturnMessageDto(rmd))
			return false;

		// on crée le document en base de données
		Integer id = getDocumentDao().creerDocument(getDocumentCourant().getClasseDocument(),
				getDocumentCourant().getNomDocument(), getDocumentCourant().getLienDocument(),
				getDocumentCourant().getDateDocument(), getDocumentCourant().getCommentaire(),
				getDocumentCourant().getIdTypeDocument(), getDocumentCourant().getNomOriginal(),
				getDocumentCourant().getNodeRefAlfresco(), getDocumentCourant().getCommentaireAlfresco(),
				getDocumentCourant().getReference());

		setLienDocumentAgentCourant(new DocumentAgent());
		getLienDocumentAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocumentAgentCourant().setIdDocument(id);
		getLienDocumentAgentDao().creerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(),
				getLienDocumentAgentCourant().getIdDocument());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}
	
	private boolean declarerErreurFromReturnMessageDto(ReturnMessageDto rmd) {
		
		if(!rmd.getErrors().isEmpty()) {
			String errors = "";
			for(String error : rmd.getErrors()) {
				errors += error;
			}
			
			getTransaction().declarerErreur("Err : " + errors);
			return true;
		}
		return false;
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

	private boolean performControlerSaisieDocument(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), multi.getParameter(getNOM_EF_COMMENTAIRE()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null || (multi != null && multi
				.getParameter(param) != null));
	}

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

	public String getVAL_ST_NOM_ORI_DOC() {
		return getZone(getNOM_ST_NOM_ORI_DOC());
	}

	public String getNOM_ST_NOM_ORI_DOC() {
		return "NOM_ST_NOM_ORI_DOC";
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}

	public AccidentTravailDao getAccidentTravailDao() {
		return accidentTravailDao;
	}

	public void setAccidentTravailDao(AccidentTravailDao accidentTravailDao) {
		this.accidentTravailDao = accidentTravailDao;
	}

	public SiegeLesionDao getSiegeLesionDao() {
		return siegeLesionDao;
	}

	public void setSiegeLesionDao(SiegeLesionDao siegeLesionDao) {
		this.siegeLesionDao = siegeLesionDao;
	}

	public TypeATDao getTypeATDao() {
		return typeATDao;
	}

	public void setTypeATDao(TypeATDao typeATDao) {
		this.typeATDao = typeATDao;
	}

	public DocumentAgentDao getLienDocumentAgentDao() {
		return lienDocumentAgentDao;
	}

	public void setLienDocumentAgentDao(DocumentAgentDao lienDocumentAgentDao) {
		this.lienDocumentAgentDao = lienDocumentAgentDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
	
	public String getNOM_ST_URL_DOC(int i) {
		return "NOM_ST_URL_DOC" + i;
	}
	
	public String getVAL_ST_URL_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC(i));
	}
}
