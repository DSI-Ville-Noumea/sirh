package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.hsct.BeneficiaireObligationAmenage;
import nc.mairie.metier.hsct.NaturePosteAmenage;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.hsct.BeneficiaireObligationAmenageDao;
import nc.mairie.spring.dao.metier.hsct.NaturePosteAmenageDao;
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

/**
 * Process OeAGENTHandicap Date de création : (01/07/11 09:42:08)
 * 
 */
public class OeAGENTHandicap extends BasicProcess {
	/**
	 * 
	 */
	private static final long							serialVersionUID			= 1L;
	public static final int								STATUT_RECHERCHER_AGENT		= 1;

	// Travailleur Handicapé
	public final String									TYPE_TH						= "TH";
	// Invalidité professionnelle permanente
	public final String									TYPE_IPP					= "IPP";
	// Maladie pro
	public final String									ORIGINE_MP					= "MP";
	// accident travail
	public final String									ORIGINE_AT					= "AT";
	// Poste aménagé milieu protégé
	public final String									MILIEU_ORDINAIRE			= "1";
	// Poste aménagé milieu protégé
	public final String									MILIEU_PROTEGE				= "2";

	private Agent										agentCourant;
	private BeneficiaireObligationAmenage				boeCourant;

	private ArrayList<BeneficiaireObligationAmenage>	listeBoe;
	private ArrayList<NaturePosteAmenage>				listeNaturePosteAmenage;

	private Hashtable<Integer, NaturePosteAmenage>		hashNaturePosteAmenage;

	public String										ACTION_SUPPRESSION			= "Suppression d'une fiche handicap.";
	public String										ACTION_CONSULTATION			= "Consultation d'une fiche handicap.";
	private String										ACTION_MODIFICATION			= "Modification d'une fiche handicap.";
	private String										ACTION_CREATION				= "Création d'une fiche handicap.";

	public boolean										showTH						= false;
	public boolean										showIPP						= false;

	public String										ACTION_DOCUMENT				= "Documents d'une fiche handicap.";
	public String										ACTION_DOCUMENT_SUPPRESSION	= "Suppression d'un document d'une fiche handicap.";
	public String										ACTION_DOCUMENT_CREATION	= "Création d'un document d'une fiche handicap.";
	private ArrayList<Document>							listeDocuments;
	private Document									documentCourant;
	private DocumentAgent								lienDocumentAgentCourant;
	public boolean										isImporting					= false;
	public MultipartRequest								multi						= null;
	public File											fichierUpload				= null;

	private TypeDocumentDao								typeDocumentDao;
	private BeneficiaireObligationAmenageDao			boeDao;
	private NaturePosteAmenageDao						naturePosteAmenageDao;
	private DocumentAgentDao							lienDocumentAgentDao;
	private DocumentDao									documentDao;
	private AgentDao									agentDao;

	private IAlfrescoCMISService						alfrescoCMISService;
	private IRadiService								radiService;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (01/07/11 09:42:08)
	 * 
	 * RG_AG_HC_C04 RG_AG_HC_C05
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setBoeCourant(null);
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
		// Si hashtable des noms de handicap vide
		// RG_AG_HC_C04
		if (getHashNaturePosteAmenage().size() == 0) {
			ArrayList<NaturePosteAmenage> listeNaturePosteAmenage = getNaturePosteAmenageDao().listerNaturePosteAmenagee();
			setListeNaturePosteAmenage(listeNaturePosteAmenage);

			if (getListeNaturePosteAmenage().size() != 0) {
				int[] tailles = { 40 };
				FormateListe aFormat = new FormateListe(tailles);
				for (int i = 0; i < getListeNaturePosteAmenage().size(); i++) {
					NaturePosteAmenage m = (NaturePosteAmenage) getListeNaturePosteAmenage().get(i);
					getHashNaturePosteAmenage().put(m.getIdNaturePosteAmenage(), m);
					String ligne[] = { m.getLibNaturePosteAmenage() };
					aFormat.ajouteLigne(ligne);
				}
			}
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeHandicap(request);
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
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNaturePosteAmenageDao() == null) {
			setNaturePosteAmenageDao(new NaturePosteAmenageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getBoeDao() == null) {
			setBoeDao(new BeneficiaireObligationAmenageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == alfrescoCMISService) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
		}
	}

	/**
	 * Initialisation de la liste des handicaps de l'agent courant Date de
	 * création : (01/07/11)
	 * 
	 */
	private void initialiseListeHandicap(HttpServletRequest request) throws Exception {
		// Recherche des handicaps de l'agent
		ArrayList<BeneficiaireObligationAmenage> listeBoe = getBoeDao().listerBeneficiaireObligationAmenageByAgent(getAgentCourant().getIdAgent());
		setListeBoe(listeBoe);

		if (getListeBoe() != null) {
			for (int i = 0; i < getListeBoe().size(); i++) {
				BeneficiaireObligationAmenage h = (BeneficiaireObligationAmenage) getListeBoe().get(i);
				// calcul du nb de docs
				ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgentTYPE(getLienDocumentAgentDao(),
						getAgentCourant().getIdAgent(), "HSCT", "HANDI", h.getIdBoe());
				int nbDoc = 0;
				if (listeDocAgent != null) {
					nbDoc = listeDocAgent.size();
				}
				h.setNbDoc(nbDoc);
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTHandicap. Date de création : (01/07/11
	 * 09:42:08)
	 * 
	 */
	public OeAGENTHandicap() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 09:42:08)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (01/07/11 09:42:08)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 09:42:08)
	 * 
	 */
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
		setBoeCourant(new BeneficiaireObligationAmenage());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'un
	 * accident de travail Date de création : 01/07/11
	 * 
	 * RG_AG_HC_C03 RG_AG_HC_C08
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		showTH = false;
		showIPP = false;
		// On vide les zone de saisie
		addZone(getNOM_RG_TYPE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_ATTRIBUTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INCAPACITE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_RB_POSTE_AMENAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_RB_ORIGINE(), Const.CHAINE_VIDE);
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'un handicap
	 * Date de création : 11/07/01
	 * 
	 */
	private boolean initialiseHandicapCourant(HttpServletRequest request) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Alim zones
		addZone(getNOM_RG_TYPE(), getBoeCourant().getType() == null ? Const.CHAINE_VIDE : getBoeCourant().getType());
		addZone(getNOM_EF_DATE_DEBUT(), getBoeCourant().getDateDebut() == null ? Const.CHAINE_VIDE : sdf.format(getBoeCourant().getDateDebut()));
		addZone(getNOM_EF_DATE_ATTRIBUTION(),
				getBoeCourant().getDateAttribution() == null ? Const.CHAINE_VIDE : sdf.format(getBoeCourant().getDateAttribution()));
		addZone(getNOM_EF_DATE_FIN(), getBoeCourant().getDateFin() == null ? Const.CHAINE_VIDE : sdf.format(getBoeCourant().getDateFin()));

		addZone(getNOM_ST_INCAPACITE(),
				null == getBoeCourant().getTaux() || getBoeCourant().getTaux() == 0 ? Const.CHAINE_VIDE : getBoeCourant().getTaux().toString());

		addZone(getNOM_ST_COMMENTAIRE(), getBoeCourant().getNatureHandicap());

		addZone(getNOM_RB_POSTE_AMENAGE(),
				null != getBoeCourant().getIdNaturePosteAmenage() ? getBoeCourant().getIdNaturePosteAmenage().toString() : Const.CHAINE_VIDE);
		addZone(getNOM_RB_ORIGINE(), null != getBoeCourant().getOrigineIpp() ? getBoeCourant().getOrigineIpp().toString() : Const.CHAINE_VIDE);

		performPB_SELECT_TYPE_IPP_TH(request);

		return true;
	}

	/**
	 * Initialisation de la suppression d'un handicap
	 * 
	 * @param request
	 * @return true si la suppression peut être effectuee
	 * @throws Exception
	 */
	private boolean initialiseHandicapSuppression(HttpServletRequest request) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Alim zones
		addZone(getNOM_RG_TYPE(), getBoeCourant().getType() == null ? Const.CHAINE_VIDE : getBoeCourant().getType());
		addZone(getNOM_ST_TYPE(), getBoeCourant().getType() == null ? Const.CHAINE_VIDE : getBoeCourant().getType());
		addZone(getNOM_EF_DATE_DEBUT(), getBoeCourant().getDateDebut() == null ? Const.CHAINE_VIDE : sdf.format(getBoeCourant().getDateDebut()));
		addZone(getNOM_EF_DATE_ATTRIBUTION(),
				getBoeCourant().getDateAttribution() == null ? Const.CHAINE_VIDE : sdf.format(getBoeCourant().getDateAttribution()));
		addZone(getNOM_EF_DATE_FIN(), getBoeCourant().getDateFin() == null ? Const.CHAINE_VIDE : sdf.format(getBoeCourant().getDateFin()));
		addZone(getNOM_ST_INCAPACITE(),
				null == getBoeCourant().getTaux() || getBoeCourant().getTaux() == 0 ? Const.CHAINE_VIDE : getBoeCourant().getTaux().toString());
		addZone(getNOM_ST_COMMENTAIRE(), getBoeCourant().getNatureHandicap());
		addZone(getNOM_RB_POSTE_AMENAGE(), null != getBoeCourant().getIdNaturePosteAmenage()
				? getHashNaturePosteAmenage().get(getBoeCourant().getIdNaturePosteAmenage()).getLibNaturePosteAmenage() : Const.CHAINE_VIDE);
		addZone(getNOM_RB_ORIGINE(), null != getBoeCourant().getOrigineIpp() ? getBoeCourant().getOrigineIpp().toString() : Const.CHAINE_VIDE);

		performPB_SELECT_TYPE_IPP_TH(request);

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (01/07/11 09:42:08)
	 * 
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
			getBoeDao().supprimerBeneficiaireObligationAmenage(getBoeCourant().getIdBoe());
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			String dateDebut = getVAL_EF_DATE_DEBUT();
			String dateAttribution = getVAL_EF_DATE_ATTRIBUTION();
			String dateFin = getVAL_EF_DATE_FIN();
			String taux = getVAL_ST_INCAPACITE();
			String commentaire = getZone(getNOM_ST_COMMENTAIRE());
			String type = getVAL_RB_TYPE();
			String posteAmenage = getVAL_RB_POSTE_AMENAGE();
			String origine = getVAL_RB_ORIGINE();

			// Création de l'objet VisiteMedicale a créer/modifier
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Agent agentCourant = getAgentCourant();

			if (TYPE_TH.equals(type)) {
				getBoeCourant().setDateDebut(sdf.parse(dateDebut));
				if (null != posteAmenage && !Const.CHAINE_VIDE.equals(posteAmenage)) {
					getBoeCourant().setIdNaturePosteAmenage(Integer.valueOf(posteAmenage));
				}
				getBoeCourant().setNatureHandicap(commentaire);
			}
			if (TYPE_IPP.equals(type)) {
				getBoeCourant().setDateAttribution(sdf.parse(dateAttribution));
				if (null != origine && !Const.CHAINE_VIDE.equals(origine)) {
					getBoeCourant().setOrigineIpp(origine);
				}
				if (null != taux && !Const.CHAINE_VIDE.equals(taux)) {
					getBoeCourant().setTaux(Integer.valueOf(taux));
				}
			}

			if (null != dateFin && !Const.CHAINE_VIDE.equals(dateFin)) {
				getBoeCourant().setDateFin(sdf.parse(dateFin));
			}

			getBoeCourant().setIdAgent(agentCourant.getIdAgent());
			getBoeCourant().setType(type);

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getBoeDao().modifierBeneficiaireObligationAmenage(getBoeCourant());
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				getBoeDao().creerBeneficiaireObligationAmenage(getBoeCourant());
			}

			if (getTransaction().isErreur())
				return false;
		}

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien passé
		commitTransaction();
		initialiseListeHandicap(request);

		return true;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire de handicap
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_HC_C01 RG_AG_HC_C02 RG_AG_HC_C06 RG_AG_HC_C07
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		// type obligatoire
		if (Const.CHAINE_VIDE.equals(getVAL_RB_TYPE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type"));
			return false;
		}

		if (TYPE_TH.equals(getVAL_RB_TYPE())) {
			// date de début de handicap
			if (Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEBUT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
				return false;
			} else if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
				return false;
			}
		}
		// date de fin > date de début
		// RG_AG_HC_C01
		if (Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN())) && TYPE_TH.equals(getVAL_RB_TYPE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_FIN()))) {
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin"));
				return false;
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_DEBUT()))) {
				if (Services.compareDates(getZone(getNOM_EF_DATE_DEBUT()), getZone(getNOM_EF_DATE_FIN())) != -1) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "de début"));
					return false;
				}
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_ATTRIBUTION()))) {
				if (Services.compareDates(getZone(getNOM_EF_DATE_ATTRIBUTION()), getZone(getNOM_EF_DATE_FIN())) != -1) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "d'attribution"));
					return false;
				}
			}
		}

		// pourcentage incapacite doit etre un nombre entre 0 (non compris) et
		// 100
		// RG_AG_HC_C02
		if (TYPE_IPP.equals(getVAL_RB_TYPE())) {
			// date de début de handicap
			if (Const.CHAINE_VIDE.equals(getZone(getNOM_EF_DATE_ATTRIBUTION()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date d'attribution"));
				return false;
			}

			if (Const.CHAINE_VIDE.equals(getZone(getNOM_ST_INCAPACITE()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "% taux"));
				return false;
			}
			if (!Const.CHAINE_VIDE.equals(getZone(getNOM_ST_INCAPACITE()))) {
				if (!Services.estNumerique(getZone(getNOM_ST_INCAPACITE()))) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "% taux"));
					return false;
				}
				if (Float.parseFloat(getZone(getNOM_ST_INCAPACITE())) <= 0.0 || Float.parseFloat(getZone(getNOM_ST_INCAPACITE())) > 100.0) {
					// erreur pourcentage entre 0 et 100
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR981", "% taux"));
					return false;
				}
			}
		}

		// commentaire longeur <= 100
		if (!Const.CHAINE_VIDE.equals(getZone(getNOM_ST_COMMENTAIRE())) && getZone(getNOM_ST_COMMENTAIRE()).length() > 250) {
			// ERR980 : La zone commentaire ne peut exceder 100 caracteres.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR980", "commentaire", "250"));
			return false;
		}

		return true;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (01/07/11 09:42:08)
	 * 
	 */
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

			// Si clic sur le bouton PB_SELECT_CRDHNC
			if (testerParametre(request, getNOM_PB_SELECT_TYPE())) {
				return performPB_SELECT_TYPE_IPP_TH(request);
			}
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
			for (int i = 0; i < getListeBoe().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeBoe().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeBoe().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeBoe().size(); i++) {
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

	public boolean performPB_SELECT_TYPE_IPP_TH(HttpServletRequest request) throws Exception {

		showIPP = getZone(getNOM_RG_TYPE()).equals(TYPE_IPP);
		showTH = getZone(getNOM_RG_TYPE()).equals(TYPE_TH);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du handicap courant
		BeneficiaireObligationAmenage boeCourant = (BeneficiaireObligationAmenage) getListeBoe().get(indiceEltAModifier);
		setBoeCourant(boeCourant);

		// init du diplome courant
		if (!initialiseHandicapCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du handicap courant
		BeneficiaireObligationAmenage handicapCourant = (BeneficiaireObligationAmenage) getListeBoe().get(indiceEltAConsulter);
		setBoeCourant(handicapCourant);

		// init du diplome courant
		if (!initialiseHandicapSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du handicap courant
		BeneficiaireObligationAmenage handicapCourant = (BeneficiaireObligationAmenage) getListeBoe().get(indiceEltASuprimer);
		setBoeCourant(handicapCourant);

		// init du diplome courant
		if (!initialiseHandicapSuppression(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DOCUMENT Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du Handicap courante
		BeneficiaireObligationAmenage handiCourante = (BeneficiaireObligationAmenage) getListeBoe().get(indiceEltDocument);
		setBoeCourant(handiCourante);

		// init des documents VM de l'agent
		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOM_ORI_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @param newListeDocuments
	 *            ArrayList
	 */
	private void setListeDocuments(ArrayList<Document> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<Document> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<Document>();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des contacts
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des documents de l'agent
		ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgentTYPE(getLienDocumentAgentDao(), getAgentCourant().getIdAgent(),
				"HSCT", "HANDI", getBoeCourant().getIdBoe());
		setListeDocuments(listeDocAgent);

		int indiceActeVM = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				Document doc = (Document) getListeDocuments().get(i);
				TypeDocument td = (TypeDocument) getTypeDocumentDao().chercherTypeDocument(doc.getIdTypeDocument());

				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getNomDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(indiceActeVM), doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_TYPE_DOC(indiceActeVM), td.getLibTypeDocument().equals(Const.CHAINE_VIDE) ? "&nbsp;" : td.getLibTypeDocument());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), sdf.format(doc.getDateDocument()));
				addZone(getNOM_ST_COMMENTAIRE_DOCUMENT(indiceActeVM),
						doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire());
				addZone(getNOM_ST_URL_DOC(indiceActeVM), (null == doc.getNodeRefAlfresco() || doc.getNodeRefAlfresco().equals(Const.CHAINE_VIDE))
						? "&nbsp;" : AlfrescoCMISService.getUrlOfDocument(doc.getNodeRefAlfresco()));

				indiceActeVM++;
			}
		}
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (17/10/11 13:46:25)
	 * 
	 */
	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE_DOCUMENT(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
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

		DocumentAgent lda = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(), getDocumentCourant().getIdDocument());
		setLienDocumentAgentCourant(lda);

		if (getTransaction().isErreur())
			return false;

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), d.getNomDocument());
		addZone(getNOM_ST_NOM_ORI_DOC(), d.getNomOriginal());
		addZone(getNOM_ST_DATE_DOC(), sdf.format(d.getDateDocument()));
		addZone(getNOM_ST_COMMENTAIRE_DOC(), d.getCommentaire());

		return true;
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
		getLienDocumentAgentDao().supprimerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(), getLienDocumentAgentCourant().getIdDocument());
		// Suppression dans la table DOCUMENT_ASSOCIE
		getDocumentDao().supprimerDocument(getDocumentCourant().getIdDocument());

		if (getTransaction().isErreur())
			return false;

		// tout s'est bien passé
		commitTransaction();
		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NOM_ORI_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		initialiseListeDocuments(request);
		return true;
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
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Controle des champs
		if (!performControlerSaisieDocument(request))
			return false;

		BeneficiaireObligationAmenage handi = getBoeCourant();

		if (getZone(getNOM_ST_WARNING()).equals(Const.CHAINE_VIDE)) {
			if (!creeDocument(request, handi)) {
				return false;
			}

		} else {
			// on supprime le document existant dans la base de données
			Document d = getDocumentDao().chercherDocumentByContainsNom("HANDI_" + handi.getIdBoe());

			ReturnMessageDto rmd = alfrescoCMISService.removeDocument(d);
			if (declarerErreurFromReturnMessageDto(rmd))
				return false;

			DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(getAgentCourant().getIdAgent(), d.getIdDocument());
			getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
			getDocumentDao().supprimerDocument(d.getIdDocument());

			if (!creeDocument(request, handi)) {
				return false;
			}
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met a jour le tableau des Handicaps pour avoir le nombre de
		// documents
		initialiseListeHandicap(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, BeneficiaireObligationAmenage handi) throws Exception {
		// on crée l'entrée dans la table
		setDocumentCourant(new Document());
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on recupere le type de document
		String codTypeDoc = CmisUtils.CODE_TYPE_HANDI;
		TypeDocument td = getTypeDocumentDao().chercherTypeDocumentByCod(codTypeDoc);

		// on crée le document en base de données
		getDocumentCourant().setIdTypeDocument(td.getIdTypeDocument());
		getDocumentCourant().setNomOriginal(fichierUpload.getName());
		getDocumentCourant().setDateDocument(new Date());
		getDocumentCourant().setCommentaire(getZone(getNOM_EF_COMMENTAIRE_DOCUMENT()));
		getDocumentCourant().setReference(handi.getIdBoe());

		// on upload le fichier
		ReturnMessageDto rmd = alfrescoCMISService.uploadDocument(getAgentConnecte(request).getIdAgent(), getAgentCourant(), getDocumentCourant(),
				fichierUpload, codTypeDoc);

		if (declarerErreurFromReturnMessageDto(rmd))
			return false;

		// on crée le document en base de données
		Integer id = getDocumentDao().creerDocument(getDocumentCourant().getClasseDocument(), getDocumentCourant().getNomDocument(),
				getDocumentCourant().getLienDocument(), getDocumentCourant().getDateDocument(), getDocumentCourant().getCommentaire(),
				getDocumentCourant().getIdTypeDocument(), getDocumentCourant().getNomOriginal(), getDocumentCourant().getNodeRefAlfresco(),
				getDocumentCourant().getCommentaireAlfresco(), getDocumentCourant().getReference());

		setLienDocumentAgentCourant(new DocumentAgent());
		getLienDocumentAgentCourant().setIdAgent(getAgentCourant().getIdAgent());
		getLienDocumentAgentCourant().setIdDocument(id);
		getLienDocumentAgentDao().creerDocumentAgent(getLienDocumentAgentCourant().getIdAgent(), getLienDocumentAgentCourant().getIdDocument());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		addZone(getNOM_EF_COMMENTAIRE_DOCUMENT(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre
					// identifiant. Merci de contacter le responsable du
					// projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
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

	private boolean declarerErreurFromReturnMessageDto(ReturnMessageDto rmd) {

		if (!rmd.getErrors().isEmpty()) {
			String errors = "";
			for (String error : rmd.getErrors()) {
				errors += error;
			}

			getTransaction().declarerErreur("Err : " + errors);
			return true;
		}
		return false;
	}

	private boolean performControlerSaisieDocument(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE_DOCUMENT(), multi.getParameter(getNOM_EF_COMMENTAIRE_DOCUMENT()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_DOC Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	/**
	 * méthode qui teste si un parametre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null
				|| (multi != null && multi.getParameter(param) != null));
	}

	public String getVAL_ST_NOM_ORI_DOC() {
		return getZone(getNOM_ST_NOM_ORI_DOC());
	}

	public String getNOM_ST_NOM_ORI_DOC() {
		return "NOM_ST_NOM_ORI_DOC";
	}

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
	}

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
	}

	public ArrayList<BeneficiaireObligationAmenage> getListeBoe() {
		return listeBoe;
	}

	public void setListeBoe(ArrayList<BeneficiaireObligationAmenage> listeBoe) {
		this.listeBoe = listeBoe;
	}

	public ArrayList<NaturePosteAmenage> getListeNaturePosteAmenage() {
		return listeNaturePosteAmenage;
	}

	public void setListeNaturePosteAmenage(ArrayList<NaturePosteAmenage> listeNaturePosteAmenage) {
		this.listeNaturePosteAmenage = listeNaturePosteAmenage;
	}

	public BeneficiaireObligationAmenageDao getBoeDao() {
		return boeDao;
	}

	public void setBoeDao(BeneficiaireObligationAmenageDao boeDao) {
		this.boeDao = boeDao;
	}

	public NaturePosteAmenageDao getNaturePosteAmenageDao() {
		return naturePosteAmenageDao;
	}

	public void setNaturePosteAmenageDao(NaturePosteAmenageDao naturePosteAmenageDao) {
		this.naturePosteAmenageDao = naturePosteAmenageDao;
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

	public BeneficiaireObligationAmenage getBoeCourant() {
		return boeCourant;
	}

	public void setBoeCourant(BeneficiaireObligationAmenage boeCourant) {
		this.boeCourant = boeCourant;
	}

	/**
	 * Retourne le doc en cours.
	 * 
	 * @return documentCourant
	 */
	private Document getDocumentCourant() {
		return documentCourant;
	}

	/**
	 * Met a jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
	private void setDocumentCourant(Document documentCourant) {
		this.documentCourant = documentCourant;
	}

	private DocumentAgent getLienDocumentAgentCourant() {
		return lienDocumentAgentCourant;
	}

	/**
	 * Met a jour le doc en cours.
	 * 
	 * @param documentCourant
	 *            Nouvelle document en cours
	 */
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

	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	public String getNOM_ST_URL_DOC(int i) {
		return "NOM_ST_URL_DOC" + i;
	}

	public String getVAL_ST_URL_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC(i));
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

	public String getNOM_ST_COMMENTAIRE_DOCUMENT(int i) {
		return "NOM_ST_COMMENTAIRE_DOCUMENT" + i;
	}

	public String getVAL_ST_COMMENTAIRE_DOCUMENT(int i) {
		return getZone(getNOM_ST_COMMENTAIRE_DOCUMENT(i));
	}

	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	public String getJSP() {
		return "OeAGENTHandicap.jsp";
	}

	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE" + i;
	}

	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	public String getNOM_ST_DEBUT(int i) {
		return "NOM_ST_DEBUT" + i;
	}

	public String getVAL_ST_DEBUT(int i) {
		return getZone(getNOM_ST_DEBUT(i));
	}

	public String getNOM_ST_FIN(int i) {
		return "NOM_ST_FIN" + i;
	}

	public String getVAL_ST_FIN(int i) {
		return getZone(getNOM_ST_FIN(i));
	}

	public String getNOM_ST_INCAPACITE(int i) {
		return "NOM_ST_INCAPACITE" + i;
	}

	public String getVAL_ST_INCAPACITE(int i) {
		return getZone(getNOM_ST_INCAPACITE(i));
	}

	public String getNOM_ST_MALADIE_PROF(int i) {
		return "NOM_ST_MALADIE_PROF" + i;
	}

	public String getVAL_ST_MALADIE_PROF(int i) {
		return getZone(getNOM_ST_MALADIE_PROF(i));
	}

	public String getNOM_ST_CRDHNC(int i) {
		return "NOM_ST_CRDHNC" + i;
	}

	public String getVAL_ST_CRDHNC(int i) {
		return getZone(getNOM_ST_CRDHNC(i));
	}

	public String getNOM_ST_NUM_CARTE(int i) {
		return "NOM_ST_NUM_CARTE" + i;
	}

	public String getVAL_ST_NUM_CARTE(int i) {
		return getZone(getNOM_ST_NUM_CARTE(i));
	}

	public String getNOM_ST_RENOUVELLEMENT(int i) {
		return "NOM_ST_RENOUVELLEMENT" + i;
	}

	public String getVAL_ST_RENOUVELLEMENT(int i) {
		return getZone(getNOM_ST_RENOUVELLEMENT(i));
	}

	public String getNOM_ST_AMENAGEMENT(int i) {
		return "NOM_ST_AMENAGEMENT" + i;
	}

	public String getVAL_ST_AMENAGEMENT(int i) {
		return getZone(getNOM_ST_AMENAGEMENT(i));
	}

	public String getNOM_ST_NB_DOC(int i) {
		return "NOM_ST_NB_DOC" + i;
	}

	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public String getNOM_EF_COMMENTAIRE_DOCUMENT() {
		return "NOM_EF_COMMENTAIRE_DOCUMENT";
	}

	public String getVAL_EF_COMMENTAIRE_DOCUMENT() {
		return getZone(getNOM_EF_COMMENTAIRE_DOCUMENT());
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

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	public String getNOM_RB_POSTE_AMENAGE() {
		return "NOM_RB_POSTE_AMENAGE";
	}

	public String getVAL_RB_POSTE_AMENAGE() {
		return getZone(getNOM_RB_POSTE_AMENAGE());
	}

	public String getNOM_RB_ORIGINE() {
		return "NOM_RB_ORIGINE";
	}

	public String getVAL_RB_ORIGINE() {
		return getZone(getNOM_RB_ORIGINE());
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public String getNOM_ST_COMMENTAIRE() {
		return "NOM_ST_COMMENTAIRE";
	}

	public String getVAL_ST_COMMENTAIRE() {
		return getZone(getNOM_ST_COMMENTAIRE());
	}

	public String getNOM_ST_TYPE() {
		return "NOM_ST_TYPE";
	}

	public String getVAL_ST_TYPE() {
		return getZone(getNOM_ST_TYPE());
	}

	public String getNOM_ST_INCAPACITE() {
		return "NOM_ST_INCAPACITE";
	}

	public String getVAL_ST_INCAPACITE() {
		return getZone(getNOM_ST_INCAPACITE());
	}

	public String getNOM_RG_TYPE() {
		return "NOM_RG_TYPE";
	}

	public String getVAL_RB_TYPE() {
		return getZone(getNOM_RG_TYPE());
	}

	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	public String getNOM_EF_DATE_ATTRIBUTION() {
		return "NOM_EF_DATE_ATTRIBUTION";
	}

	public String getVAL_EF_DATE_ATTRIBUTION() {
		return getZone(getNOM_EF_DATE_ATTRIBUTION());
	}

	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	public Hashtable<Integer, NaturePosteAmenage> getHashNaturePosteAmenage() {
		if (hashNaturePosteAmenage == null)
			hashNaturePosteAmenage = new Hashtable<Integer, NaturePosteAmenage>();
		return hashNaturePosteAmenage;
	}

	public String getNomEcran() {
		return "ECR-AG-HSCT-HANDICAPS";
	}

	public String getNOM_PB_SELECT_TYPE() {
		return "NOM_PB_SELECT_TYPE";
	}

}
