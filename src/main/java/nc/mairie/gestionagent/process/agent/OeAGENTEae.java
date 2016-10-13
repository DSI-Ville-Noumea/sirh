package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.BirtDto;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeCommentaireDto;
import nc.mairie.gestionagent.eae.dto.EaeDeveloppementDto;
import nc.mairie.gestionagent.eae.dto.EaeDto;
import nc.mairie.gestionagent.eae.dto.EaeEvaluationDto;
import nc.mairie.gestionagent.eae.dto.EaeEvolutionDto;
import nc.mairie.gestionagent.eae.dto.EaeFichePosteDto;
import nc.mairie.gestionagent.eae.dto.EaeFinalizationDto;
import nc.mairie.gestionagent.eae.dto.EaeItemPlanActionDto;
import nc.mairie.gestionagent.eae.dto.EaeListeDto;
import nc.mairie.gestionagent.eae.dto.EaeObjectifProDto;
import nc.mairie.gestionagent.eae.dto.EaeTypeDeveloppementEnum;
import nc.mairie.gestionagent.eae.dto.ListItemDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
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
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IEaeService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.cmis.AlfrescoCMISService;
import nc.noumea.spring.service.cmis.IAlfrescoCMISService;

/**
 * Process OeAGENTDIPLOMEGestion Date de création : (11/02/03 14:20:31)
 * 
 */
public class OeAGENTEae extends BasicProcess {
	/**
	 * 
	 */
	private static final long				serialVersionUID					= 1L;

	public static final int					STATUT_RECHERCHER_AGENT				= 1;

	public String							ACTION_MODIFICATION					= "Modification d'un EAE.";
	public String							ACTION_MODIFICATION_DATE			= "Modification des dates d'un EAE.";
	public String							ACTION_CONSULTATION					= "Consultation d'un EAE.";
	public String							ACTION_AJOUT_OBJ_INDI				= "Ajout d'un objectif individuel.";
	public String							ACTION_MODIFICATION_OBJ_INDI		= "Modification d'un objectif individuel.";
	public String							ACTION_SUPPRESSION_OBJ_INDI			= "Suppression d'un objectif individuel.";
	public String							ACTION_AJOUT_OBJ_PRO				= "Ajout d'un objectif professionnel.";
	public String							ACTION_MODIFICATION_OBJ_PRO			= "Modification d'un objectif professionnel.";
	public String							ACTION_SUPPRESSION_OBJ_PRO			= "Suppression d'un objectif professionnel.";
	public String							ACTION_AJOUT_DEV					= "Ajout d'un développement.";
	public String							ACTION_MODIFICATION_DEV				= "Modification d'un développement.";
	public String							ACTION_SUPPRESSION_DEV				= "Suppression d'un développement.";
	public String							ACTION_DOCUMENT						= "Documents.";
	public String							ACTION_DOCUMENT_CREATION			= "Création d'un document.";
	public String							ACTION_DOCUMENT_CREATION_ANCIEN_EAE	= "Création d'un ancien EAE.";

	private String[]						LB_BASE_HORAIRE;
	private ArrayList<Horaire>				listeHoraire;
	private Hashtable<String, Horaire>		hashHoraire;

	private String[]						LB_TYPE_DEV;
	private List<ListItemDto>				listeTypeDeveloppement;
	private Hashtable<String, ListItemDto>	hashTypeDeveloppement;

	private Agent							AgentCourant;
	private ArrayList<EaeDto>				listeEae;
	private List<BirtDto>					listeEvaluateurEae;
	private List<EaeObjectifProDto>			listeObjectifPro;
	private List<EaeItemPlanActionDto>		listeObjectifIndi;
	private List<EaeDeveloppementDto>		listeDeveloppement;
	private EaeDto							eaeCourant;
	private EaeItemPlanActionDto			objectifIndiCourant;
	private EaeObjectifProDto				objectifProCourant;
	private EaeDeveloppementDto				developpementCourant;
	private List<EaeFinalizationDto>		listeDocuments;

	public boolean							isImporting							= false;
	public MultipartRequest					multi								= null;
	public File								fichierUpload						= null;

	public String							focus								= null;

	private ArrayList<Document>				listeAncienEAE;

	private TypeDocumentDao					typeDocumentDao;
	private DocumentAgentDao				lienDocumentAgentDao;
	private DocumentDao						documentDao;
	private AgentDao						agentDao;

	private SimpleDateFormat				sdf									= new SimpleDateFormat("dd/MM/yyyy");

	private IAdsService						adsService;
	private IEaeService						eaeService;
	private IRadiService					radiService;
	private IAlfrescoCMISService			alfrescoCMISService;

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setEaeCourant(null);
			multi = null;
			isImporting = false;
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		initialiseListeDeroulante();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeEae(request);
				initialiseListeAncienEae(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseListeAncienEae(HttpServletRequest request) throws Exception {

		// Recherche des documents de l'agent
		ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgent(getLienDocumentAgentDao(), getAgentCourant().getIdAgent(),
				Const.CHAINE_VIDE, CmisUtils.CODE_TYPE_EAE);
		setListeAncienEAE(listeDocAgent);

		if (getListeAncienEAE() != null) {
			for (int i = 0; i < getListeAncienEAE().size(); i++) {
				Document doc = (Document) getListeAncienEAE().get(i);
				Integer id = doc.getIdDocument();

				addZone(getNOM_ST_COMMENTAIRE_ANCIEN_EAE(id), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire());
				addZone(getNOM_ST_DOCUMENT_ANCIEN_EAE(id), doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(id), doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());
				addZone(getNOM_ST_URL_DOC(id), (null == doc.getNodeRefAlfresco() || doc.getNodeRefAlfresco().equals(Const.CHAINE_VIDE)) ? "&nbsp;"
						: AlfrescoCMISService.getUrlOfDocument(doc.getNodeRefAlfresco()));

			}
		}

		// addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
	}

	/**
	 * Initialisation de la liste des primes de l'agent courant Date de création
	 * : (04/08/11)
	 * 
	 */
	private void initialiseListeEae(HttpServletRequest request) throws Exception {
		// Recherche des EAE de l'agent
		List<EaeDto> listeEaesEvalue = eaeService.getListEaesByidAgent(getAgentConnecte(request).getIdAgent(), getAgentCourant().getIdAgent());

		ArrayList<EaeDto> listeEAE = new ArrayList<EaeDto>();

		int indiceEae = 0;
		if (null != listeEaesEvalue) {
			for (EaeDto eae : listeEaesEvalue) {

				listeEAE.add(eae);
				EaeFichePosteDto eaeFDP = eae.getFichePoste();
				CampagneEaeDto camp = eae.getCampagne();

				String evaluateur = Const.CHAINE_VIDE;
				if (null != eae.getEvaluateurs()) {
					for (BirtDto eval : eae.getEvaluateurs()) {

						evaluateur += eval.getAgent().getNomUsage() + " " + eval.getAgent().getPrenom() + " (" + eval.getIdAgent() + ") <br/> ";
					}
				}

				addZone(getNOM_ST_ANNEE(indiceEae), camp.getAnnee().toString());
				addZone(getNOM_ST_EVALUATEUR(indiceEae), evaluateur.equals(Const.CHAINE_VIDE) ? "&nbsp;" : evaluateur);
				addZone(getNOM_ST_DATE_ENTRETIEN(indiceEae), eae.getDateEntretien() == null ? "&nbsp;" : sdf.format(eae.getDateEntretien()));
				addZone(getNOM_ST_SERVICE(indiceEae), eaeFDP == null ? "&nbsp;" : eaeFDP.getService() == null ? "&nbsp;" : eaeFDP.getService());
				addZone(getNOM_ST_STATUT(indiceEae), EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()));
				String idDocument = null;
				try {
					idDocument = eae.getFinalisation().get(0).getIdDocument();
				} catch (Exception e) {
					// il n'y a pas de finalisation
				}
				addZone(getNOM_ST_URL_DOC(indiceEae),
						(null == idDocument || idDocument.equals(Const.CHAINE_VIDE)) ? "&nbsp;" : AlfrescoCMISService.getUrlOfDocument(idDocument));

				indiceEae++;
			}
		}
		setListeEae(listeEAE);
	}

	private void initialiseListeDeroulante() throws Exception {

		// Si liste base horaire vide alors affectation
		if (getLB_BASE_HORAIRE() == LBVide) {
			// ArrayList<Horaire> liste =
			// Horaire.listerHoraire(getTransaction());
			ArrayList<Horaire> liste = Horaire.listerHoraireSansNulSansComplet(getTransaction());
			setListeHoraire(liste);

			int[] tailles = { 30 };
			String[] champs = { "libHor" };
			setLB_BASE_HORAIRE(new FormateListe(tailles, liste, champs).getListeFormatee(true));

			for (Horaire h : liste)
				getHashHoraire().put(h.getCdtHor(), h);
		}

		// Si liste base type developpement vide alors affectation
		if (getLB_TYPE_DEV() == LBVide) {
			List<ListItemDto> liste = eaeService.getListeTypeDeveloppement();
			setListeTypeDeveloppement(liste);

			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<ListItemDto> list = liste.listIterator(); list.hasNext();) {
				ListItemDto typeDev = (ListItemDto) list.next();
				String ligne[] = { typeDev.getValeur() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DEV(aFormat.getListeFormatee());

			for (ListItemDto h : liste)
				getHashTypeDeveloppement().put(h.getValeur(), h);
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
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == alfrescoCMISService) {
			alfrescoCMISService = (IAlfrescoCMISService) context.getBean("alfrescoCMISService");
		}
		if (null == eaeService) {
			eaeService = (IEaeService) context.getBean("eaeService");
		}
	}

	/**
	 * @return Agent
	 */
	public Agent getAgentCourant() {
		return AgentCourant;
	}

	/**
	 * @param newAgentCourant
	 *            Agent
	 */
	private void setAgentCourant(Agent newAgentCourant) {
		AgentCourant = newAgentCourant;
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		// return getNOM_EF_DATE_OBTENTION();
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (11/02/03 14:20:31)
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

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
				// Si clic sur le bouton PB_MODIFIER
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
				// Si clic sur le bouton PB_MODIFIER_DATE
				if (testerParametre(request, getNOM_PB_MODIFIER_DATE(i))) {
					return performPB_MODIFIER_DATE(request, i);
				}
			}
			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_OBJ_INDI
			if (testerParametre(request, getNOM_PB_AJOUTER_OBJ_INDI())) {
				return performPB_AJOUTER_OBJ_INDI(request);
			}

			// Si clic sur le bouton PB_VALIDER_DATE
			if (testerParametre(request, getNOM_PB_VALIDER_DATE())) {
				return performPB_VALIDER_DATE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_OBJ_INDI
			for (int i = 0; i < getListeObjectifIndi().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_OBJ_INDI(i))) {
					return performPB_MODIFIER_OBJ_INDI(request, i);
				}
				// Si clic sur le bouton PB_SUPPRIMER_OBJ_INDI
				if (testerParametre(request, getNOM_PB_SUPPRIMER_OBJ_INDI(i))) {
					return performPB_SUPPRIMER_OBJ_INDI(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_OBJ_INDI
			if (testerParametre(request, getNOM_PB_VALIDER_OBJ_INDI())) {
				return performPB_VALIDER_OBJ_INDI(request);
			}

			// Si clic sur le bouton PB_ANNULER_OBJ_INDI
			if (testerParametre(request, getNOM_PB_ANNULER_OBJ_INDI())) {
				return performPB_ANNULER_OBJ_INDI(request);
			}

			// gestion navigation
			// Si clic sur le bouton PB_RESET
			if (testerParametre(request, getNOM_PB_RESET())) {
				return performPB_RESET(request);
			}

			// Si clic sur le bouton PB_AJOUTER_OBJ_PRO
			if (testerParametre(request, getNOM_PB_AJOUTER_OBJ_PRO())) {
				return performPB_AJOUTER_OBJ_PRO(request);
			}

			// Si clic sur le bouton PB_MODIFIER_OBJ_PRO
			for (int i = 0; i < getListeObjectifPro().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_OBJ_PRO(i))) {
					return performPB_MODIFIER_OBJ_PRO(request, i);
				}
				// Si clic sur le bouton PB_SUPPRIMER_OBJ_PRO
				if (testerParametre(request, getNOM_PB_SUPPRIMER_OBJ_PRO(i))) {
					return performPB_SUPPRIMER_OBJ_PRO(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_OBJ_PRO
			if (testerParametre(request, getNOM_PB_VALIDER_OBJ_PRO())) {
				return performPB_VALIDER_OBJ_PRO(request);
			}

			// Si clic sur le bouton PB_ANNULER_OBJ_PRO
			if (testerParametre(request, getNOM_PB_ANNULER_OBJ_PRO())) {
				return performPB_ANNULER_OBJ_PRO(request);
			}

			//

			// Si clic sur le bouton PB_AJOUTER_DEV
			if (testerParametre(request, getNOM_PB_AJOUTER_DEV())) {
				return performPB_AJOUTER_DEV(request);
			}

			// Si clic sur le bouton PB_MODIFIER_DEV
			for (int i = 0; i < getListeDeveloppement().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_DEV(i))) {
					return performPB_MODIFIER_DEV(request, i);
				}
				// Si clic sur le bouton PB_SUPPRIMER_DEV
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DEV(i))) {
					return performPB_SUPPRIMER_DEV(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_DEV
			if (testerParametre(request, getNOM_PB_VALIDER_DEV())) {
				return performPB_VALIDER_DEV(request);
			}

			// Si clic sur le bouton PB_ANNULER_DEV
			if (testerParametre(request, getNOM_PB_ANNULER_DEV())) {
				return performPB_ANNULER_DEV(request);
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_DOCUMENT(i))) {
					return performPB_DOCUMENT(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_CREER_DOC())) {
				return performPB_CREER_DOC(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_CREATION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION())) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_ANCIEN_EAE
			if (testerParametre(request, getNOM_PB_AJOUTER_ANCIEN_EAE())) {
				return performPB_AJOUTER_ANCIEN_EAE(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE())) {
				return performPB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE(request);
			}

		}
		// Si pas de retour définit
		setStatut(STATUT_MEME_PROCESS, false, "Erreur : TAG INPUT non géré par le process");
		return false;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 10:36:22)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEae.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-AG-EAE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR(int i) {
		return "NOM_ST_EVALUATEUR" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_EVALUATEUR
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR(int i) {
		return getZone(getNOM_ST_EVALUATEUR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ENTRETIEN Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_ENTRETIEN(int i) {
		return "NOM_ST_DATE_ENTRETIEN" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_ENTRETIEN(int i) {
		return getZone(getNOM_ST_DATE_ENTRETIEN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_STATUT Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
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
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// Récup de l'eae courant
		EaeDto eaeCourant = (EaeDto) getListeEae().get(indiceEltAConsulter);
		eaeCourant = eaeService.getDetailsEae(getAgentConnecte(request).getIdAgent(), eaeCourant.getIdEae());
		setEaeCourant(eaeCourant);

		initialiseEae();
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseEae() throws Exception {

		// Récup de l'EAE courant
		EaeDto eae = getEaeCourant();
		BirtDto evalue = eae.getEvalue();
		// Alim zone Informations
		addZone(getNOM_ST_DATE_ENTRETIEN(), eae.getDateEntretien() == null ? "non renseigné" : sdf.format(eae.getDateEntretien()));

		addZone(getNOM_EF_DATE_FONCTIONNAIRE(),
				evalue.getDateEntreeFonctionnaire() == null ? "non renseigné" : sdf.format(evalue.getDateEntreeFonctionnaire()));
		addZone(getNOM_EF_DATE_ADMINISTRATION(),
				evalue.getDateEntreeCollectivite() == null ? "non renseigné" : sdf.format(evalue.getDateEntreeCollectivite()));

		List<BirtDto> listeEvaluateur = eae.getEvaluateurs();
		setListeEvaluateurEae(listeEvaluateur);

		for (int j = 0; j < getListeEvaluateurEae().size(); j++) {
			BirtDto agentEvaluateur = getListeEvaluateurEae().get(j);
			String evaluateur = agentEvaluateur.getAgent().getNomUsage() + " " + agentEvaluateur.getAgent().getPrenom() + " ("
					+ agentEvaluateur.getAgent().getNomatr() + ") ";

			addZone(getNOM_ST_EVALUATEUR_NOM(j), evaluateur.equals(Const.CHAINE_VIDE) ? "non renseigné" : evaluateur);
			addZone(getNOM_ST_EVALUATEUR_FONCTION(j),
					agentEvaluateur.getFonction().equals(Const.CHAINE_VIDE) ? "non renseigné" : agentEvaluateur.getFonction());
			// #11505 : on alimente les dates modifiables
			addZone(getNOM_ST_EVALUATEUR_DATE_FONCTION(j),
					agentEvaluateur.getDateEntreeFonction() == null ? "non renseigné" : sdf.format(agentEvaluateur.getDateEntreeFonction()));
			addZone(getNOM_ST_EVALUATEUR_DATE_SERVICE(j),
					agentEvaluateur.getDateEntreeService() == null ? "non renseigné" : sdf.format(agentEvaluateur.getDateEntreeService()));
		}

		try {
			EaeFichePosteDto eaeFDP = eae.getFichePoste();
			addZone(getNOM_ST_SERVICE(), eaeFDP.getDirectionService() == null ? "&nbsp;" : eaeFDP.getDirectionService());
			// #11505 : on alimente les dates modifiables
			addZone(getNOM_EF_DATE_FONCTION(), evalue.getDateEntreeFonction() == null ? "non renseigné" : sdf.format(evalue.getDateEntreeFonction()));
		} catch (Exception e) {
			addZone(getNOM_ST_SERVICE(), Const.CHAINE_VIDE);
			// #11505 : on alimente les dates modifiables
			addZone(getNOM_EF_DATE_FONCTION(), "pas de FDP présente");
		}

		// Alim zone evaluation
		EaeEvaluationDto evaluation = eae.getEvaluation();
		if (evaluation == null) {
			addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_NIVEAU(), "non renseigné");
			addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
			addZone(getNOM_ST_NOTE(), "non renseigné");
			addZone(getNOM_ST_AVIS_SHD(), "non renseigné");
			addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MOY());
			addZone(getNOM_ST_AVCT_DIFF(), "non renseigné");
			addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
			addZone(getNOM_ST_CHANGEMENT_CLASSE(), "non renseigné");
			addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
			addZone(getNOM_ST_AVIS_REVALO(), "non renseigné");
			addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
			addZone(getNOM_ST_RAPPORT_CIRCON(), Const.CHAINE_VIDE);
		} else {
			// commentaire de l'evaluateur
			addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(),
					evaluation.getCommentaireEvaluateur() == null ? Const.CHAINE_VIDE : evaluation.getCommentaireEvaluateur().getText());
			// commentaire de l'evaluateur sur le rapport circonstancie
			addZone(getNOM_ST_RAPPORT_CIRCON(),
					evaluation.getCommentaireAvctEvaluateur() == null ? Const.CHAINE_VIDE : evaluation.getCommentaireAvctEvaluateur().getText());
			// niveau
			addZone(getNOM_ST_NIVEAU(), evaluation.getNiveau() == null ? "non renseigné" : evaluation.getNiveau().getCourant());
			// pour la modif
			if (evaluation.getNiveau() == null) {
				addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
			} else {
				String niveau = evaluation.getNiveau().getCourant();
				if (niveau.equals("NECESSITANT_DES_PROGRES")) {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_PROGR());
				} else if (niveau.equals("INSUFFISANT")) {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_INSU());
				} else if (niveau.equals("EXCELLENT")) {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_EXCEL());
				} else {
					addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
				}
			}

			addZone(getNOM_ST_NOTE(), evaluation.getNoteAnnee() == null ? "non renseigné" : evaluation.getNoteAnnee().toString());
			addZone(getNOM_ST_AVIS_SHD(), evaluation.getAvisShd() == null ? "non renseigné " : evaluation.getAvisShd());
			// pour la modif
			if (evaluation.getAvisShd() == null) {
				addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MOY());
			} else {
				if (evaluation.getAvisShd().equals("durée minimale")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MIN());
				} else if (evaluation.getAvisShd().equals("durée maximale")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MAX());
				} else if (evaluation.getAvisShd().equals("durée moyenne")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MOY());
				} else if (evaluation.getAvisShd().equals("Favorable")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_FAV());
				} else if (evaluation.getAvisShd().equals("Défavorable")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_DEFAV());
				}
			}

			addZone(getNOM_ST_AVCT_DIFF(),
					evaluation.getPropositionAvancement() == null ? "non renseigné" : evaluation.getPropositionAvancement().getCourant());

			// pour la modif
			if (evaluation.getPropositionAvancement() == null) {
				addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
			} else {
				if (evaluation.getPropositionAvancement().getCourant().equals("MINI")) {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MIN());
				} else if (evaluation.getPropositionAvancement().getCourant().equals("MAXI")) {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MAX());
				} else {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
				}
			}

			addZone(getNOM_ST_CHANGEMENT_CLASSE(), null == evaluation.getAvisChangementClasse() ? "non renseigné"
					: evaluation.getAvisChangementClasse() ? "favorable" : "défavorable");
			// pour la modif
			if (evaluation.getAvisChangementClasse() == null) {
				addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
			} else {
				if (!evaluation.getAvisChangementClasse()) {
					addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_DEF());
				} else {
					addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
				}
			}
			addZone(getNOM_ST_AVIS_REVALO(),
					evaluation.getAvisRevalorisation() == null ? "non renseigné" : evaluation.getAvisRevalorisation() ? "favorable" : "défavorable");
			// pour la modif
			if (evaluation.getAvisRevalorisation() == null) {
				addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
			} else {
				if (!evaluation.getAvisRevalorisation()) {
					addZone(getNOM_RG_REVA(), getNOM_RB_REVA_DEF());
				} else {
					addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
				}
			}
		}

		// alim zone plan action
		if (null != eae.getPlanAction()) {
			setListeObjectifPro(eae.getPlanAction().getObjectifsProfessionnels());
			for (int j = 0; j < getListeObjectifPro().size(); j++) {
				EaeObjectifProDto plan = getListeObjectifPro().get(j);

				addZone(getNOM_ST_LIB_OBJ_PRO(j), plan.getObjectif());
				addZone(getNOM_ST_LIB_MESURE_PRO(j), plan.getIndicateur());
			}

			setListeObjectifIndi(eae.getPlanAction().getListeObjectifsIndividuels());
			for (int j = 0; j < getListeObjectifIndi().size(); j++) {
				EaeItemPlanActionDto plan = getListeObjectifIndi().get(j);
				addZone(getNOM_ST_LIB_OBJ_INDI(j), plan.getLibelle());
			}
		}

		// Alim zone Evolution
		try {
			EaeEvolutionDto evolution = eae.getEvolution();
			if (null != evolution.getCommentaireEvolution()) {
				addZone(getNOM_ST_COM_EVOLUTION(),
						evolution.getCommentaireEvolution() == null ? Const.CHAINE_VIDE : evolution.getCommentaireEvolution().getText());
			} else {
				addZone(getNOM_ST_COM_EVOLUTION(), Const.CHAINE_VIDE);
			}
			addZone(getNOM_ST_MOB_GEO(), evolution.isMobiliteGeo() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteGeo()) {
				addZone(getNOM_RG_MOB_GEO(), getNOM_RB_MOB_GEO_NON());
			} else {
				addZone(getNOM_RG_MOB_GEO(), getNOM_RB_MOB_GEO_OUI());
			}
			addZone(getNOM_ST_MOB_FONCT(), evolution.isMobiliteFonctionnelle() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteFonctionnelle()) {
				addZone(getNOM_RG_MOB_FONCT(), getNOM_RB_MOB_FONCT_NON());
			} else {
				addZone(getNOM_RG_MOB_FONCT(), getNOM_RB_MOB_FONCT_OUI());
			}
			addZone(getNOM_ST_CHANGEMENT_METIER(), evolution.isChangementMetier() ? "oui" : "non");
			// pour la modif
			if (!evolution.isChangementMetier()) {
				addZone(getNOM_RG_METIER(), getNOM_RB_METIER_NON());
			} else {
				addZone(getNOM_RG_METIER(), getNOM_RB_METIER_OUI());
			}
			addZone(getNOM_ST_DELAI(), evolution.getDelaiEnvisage() == null ? "non renseigné" : evolution.getDelaiEnvisage().getCourant());
			// pour la modif
			if (evolution.getDelaiEnvisage() == null) {
				addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
			} else {
				if (evolution.getDelaiEnvisage().getCourant().equals("ENTRE1ET2ANS")) {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_2());
				} else if (evolution.getDelaiEnvisage().getCourant().equals("MOINS1AN")) {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_1());
				} else {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
				}
			}
			addZone(getNOM_ST_MOB_SERV(), evolution.isMobiliteService() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteService()) {
				addZone(getNOM_RG_MOB_SERV(), getNOM_RB_MOB_SERV_NON());
			} else {
				addZone(getNOM_RG_MOB_SERV(), getNOM_RB_MOB_SERV_OUI());
			}
			addZone(getNOM_ST_MOB_DIR(), evolution.isMobiliteDirection() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteDirection()) {
				addZone(getNOM_RG_MOB_DIR(), getNOM_RB_MOB_DIR_NON());
			} else {
				addZone(getNOM_RG_MOB_DIR(), getNOM_RB_MOB_DIR_OUI());
			}
			addZone(getNOM_ST_MOB_COLL(), evolution.isMobiliteCollectivite() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteCollectivite()) {
				addZone(getNOM_RG_MOB_COLL(), getNOM_RB_MOB_COLL_NON());
			} else {
				addZone(getNOM_RG_MOB_COLL(), getNOM_RB_MOB_COLL_OUI());
			}
			addZone(getNOM_ST_NOM_COLL(), evolution.getNomCollectivite() == null ? Const.CHAINE_VIDE : evolution.getNomCollectivite());
			addZone(getNOM_ST_MOB_AUTRE(), evolution.isMobiliteAutre() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteAutre()) {
				addZone(getNOM_RG_MOB_AUTRE(), getNOM_RB_MOB_AUTRE_NON());
			} else {
				addZone(getNOM_RG_MOB_AUTRE(), getNOM_RB_MOB_AUTRE_OUI());
			}
			addZone(getNOM_ST_CONCOURS(), evolution.isConcours() ? "oui" : "non");
			// pour la modif
			if (!evolution.isConcours()) {
				addZone(getNOM_RG_CONCOURS(), getNOM_RB_CONCOURS_NON());
			} else {
				addZone(getNOM_RG_CONCOURS(), getNOM_RB_CONCOURS_OUI());
			}
			addZone(getNOM_ST_NOM_CONCOURS(), evolution.getNomConcours() == null ? Const.CHAINE_VIDE : evolution.getNomConcours());
			addZone(getNOM_ST_VAE(), evolution.isVae() ? "oui" : "non");
			// pour la modif
			if (!evolution.isVae()) {
				addZone(getNOM_RG_VAE(), getNOM_RB_VAE_NON());
			} else {
				addZone(getNOM_RG_VAE(), getNOM_RB_VAE_OUI());
			}
			addZone(getNOM_ST_NOM_VAE(), evolution.getNomVae() == null ? Const.CHAINE_VIDE : evolution.getNomVae());
			addZone(getNOM_ST_TPS_PARTIEL(), evolution.isTempsPartiel() ? "oui" : "non");
			// pour la modif
			if (!evolution.isTempsPartiel()) {
				addZone(getNOM_RG_TPS_PARTIEL(), getNOM_RB_TPS_PARTIEL_NON());
			} else {
				addZone(getNOM_RG_TPS_PARTIEL(), getNOM_RB_TPS_PARTIEL_OUI());
			}
			if (null != evolution.getPourcentageTempsPartiel() && null != evolution.getPourcentageTempsPartiel().getCourant()) {
				Horaire tempsPart = (Horaire) getHashHoraire().get(evolution.getPourcentageTempsPartiel().getCourant().toString());
				if (tempsPart != null) {
					Float taux = Float.parseFloat(tempsPart.getCdTaux()) * 100;
					int ligneHoraire = getListeHoraire().indexOf(tempsPart);
					addZone(getNOM_LB_BASE_HORAIRE_SELECT(), String.valueOf(ligneHoraire + 1));
					addZone(getNOM_ST_POURC_TPS_PARTIEL(), tempsPart == null || tempsPart.getCdtHor() == null ? "non renseigné"
							: tempsPart.getLibHor() + " - " + String.valueOf(taux.intValue()) + "%");
				}
			} else {
				addZone(getNOM_LB_BASE_HORAIRE_SELECT(), Const.ZERO);
				addZone(getNOM_ST_POURC_TPS_PARTIEL(), "non renseigné");
			}

			addZone(getNOM_ST_RETRAITE(), evolution.isRetraite() ? "oui" : "non");
			// pour la modif
			if (!evolution.isRetraite()) {
				addZone(getNOM_RG_RETRAITE(), getNOM_RB_RETRAITE_NON());
			} else {
				addZone(getNOM_RG_RETRAITE(), getNOM_RB_RETRAITE_OUI());
			}
			addZone(getNOM_ST_DATE_RETRAITE(), evolution.getDateRetraite() == null ? Const.CHAINE_VIDE : sdf.format(evolution.getDateRetraite()));
			addZone(getNOM_ST_AUTRE_PERSP(), evolution.isAutrePerspective() ? "oui" : "non");
			// pour la modif
			if (!evolution.isAutrePerspective()) {
				addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_NON());
			} else {
				addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_OUI());
			}
			addZone(getNOM_ST_LIB_AUTRE_PERSP(),
					evolution.getLibelleAutrePerspective() == null ? Const.CHAINE_VIDE : evolution.getLibelleAutrePerspective());

			// Alim zones developpement
			List<EaeDeveloppementDto> listeDeveloppementCompetences = evolution.getDeveloppementCompetences();
			List<EaeDeveloppementDto> listeDeveloppementComportement = evolution.getDeveloppementComportement();
			List<EaeDeveloppementDto> listeDeveloppementConnaissances = evolution.getDeveloppementConnaissances();
			List<EaeDeveloppementDto> listeDeveloppementExamensConcours = evolution.getDeveloppementExamensConcours();
			List<EaeDeveloppementDto> listeDeveloppementFormateur = evolution.getDeveloppementFormateur();
			List<EaeDeveloppementDto> listeDeveloppementPersonnel = evolution.getDeveloppementPersonnel();

			List<EaeDeveloppementDto> allListeDeveloppement = new ArrayList<EaeDeveloppementDto>();
			allListeDeveloppement.addAll(listeDeveloppementCompetences);
			allListeDeveloppement.addAll(listeDeveloppementComportement);
			allListeDeveloppement.addAll(listeDeveloppementConnaissances);
			allListeDeveloppement.addAll(listeDeveloppementExamensConcours);
			allListeDeveloppement.addAll(listeDeveloppementFormateur);
			allListeDeveloppement.addAll(listeDeveloppementPersonnel);

			setListeDeveloppement(allListeDeveloppement);
			for (int j = 0; j < getListeDeveloppement().size(); j++) {
				EaeDeveloppementDto dev = getListeDeveloppement().get(j);
				addZone(getNOM_ST_TYPE_DEV(j), dev.getTypeDeveloppement());
				addZone(getNOM_ST_LIB_DEV(j), dev.getLibelle());
				addZone(getNOM_ST_ECHEANCE_DEV(j),
						dev.getEcheance() == null ? Const.CHAINE_VIDE : new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheance()));
				addZone(getNOM_ST_PRIORISATION_DEV(j), new Integer(dev.getPriorisation()).toString());
			}
		} catch (Exception e) {
			addZone(getNOM_ST_MOB_GEO(), "non renseigné");
			addZone(getNOM_RG_MOB_GEO(), getNOM_RB_MOB_GEO_NON());
			addZone(getNOM_ST_MOB_FONCT(), "non renseigné");
			addZone(getNOM_RG_MOB_FONCT(), getNOM_RB_MOB_FONCT_NON());
			addZone(getNOM_ST_CHANGEMENT_METIER(), "non renseigné");
			addZone(getNOM_RG_METIER(), getNOM_RB_METIER_NON());
			addZone(getNOM_ST_DELAI(), "non renseigné");
			addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
			addZone(getNOM_ST_MOB_SERV(), "non renseigné");
			addZone(getNOM_RG_MOB_SERV(), getNOM_RB_MOB_SERV_NON());
			addZone(getNOM_ST_MOB_DIR(), "non renseigné");
			addZone(getNOM_RG_MOB_DIR(), getNOM_RB_MOB_DIR_NON());
			addZone(getNOM_ST_MOB_COLL(), "non renseigné");
			addZone(getNOM_RG_MOB_COLL(), getNOM_RB_MOB_COLL_NON());
			addZone(getNOM_ST_NOM_COLL(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_MOB_AUTRE(), "non renseigné");
			addZone(getNOM_RG_MOB_AUTRE(), getNOM_RB_MOB_AUTRE_NON());
			addZone(getNOM_ST_CONCOURS(), "non renseigné");
			addZone(getNOM_RG_CONCOURS(), getNOM_RB_CONCOURS_NON());
			addZone(getNOM_ST_NOM_CONCOURS(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_VAE(), "non renseigné");
			addZone(getNOM_RG_VAE(), getNOM_RB_VAE_NON());
			addZone(getNOM_ST_NOM_VAE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_TPS_PARTIEL(), "non renseigné");
			addZone(getNOM_RG_TPS_PARTIEL(), getNOM_RB_TPS_PARTIEL_NON());
			addZone(getNOM_ST_POURC_TPS_PARTIEL(), "non renseigné");
			addZone(getNOM_ST_RETRAITE(), "non renseigné");
			addZone(getNOM_RG_RETRAITE(), getNOM_RB_RETRAITE_NON());
			addZone(getNOM_ST_DATE_RETRAITE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_AUTRE_PERSP(), "non renseigné");
			addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_NON());
			addZone(getNOM_ST_LIB_AUTRE_PERSP(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_COM_EVOLUTION(), Const.CHAINE_VIDE);
		}

	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
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

		// Récup de l'eae courant
		EaeDto eaeCourant = (EaeDto) getListeEae().get(indiceEltAModifier);
		eaeCourant = eaeService.getDetailsEae(getAgentConnecte(request).getIdAgent(), eaeCourant.getIdEae());
		setEaeCourant(eaeCourant);

		initialiseEae();
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<EaeDto> getListeEae() {
		if (listeEae == null)
			return new ArrayList<EaeDto>();
		return listeEae;
	}

	public void setListeEae(ArrayList<EaeDto> listeEae) {
		this.listeEae = listeEae;
	}

	public EaeDto getEaeCourant() {
		return eaeCourant;
	}

	public void setEaeCourant(EaeDto eaeCourant) {
		this.eaeCourant = eaeCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_RESET() {
		return "NOM_PB_RESET";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_RESET(HttpServletRequest request) throws Exception {
		// addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// setEaeCourant(null);
		// multi = null;
		// isImporting = false;
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_PERMIS Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setEaeCourant(null);
		multi = null;
		isImporting = false;
		fichierUpload = null;
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ENTRETIEN Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_DATE_ENTRETIEN() {
		return "NOM_ST_DATE_ENTRETIEN";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_DATE_ENTRETIEN() {
		return getZone(getNOM_ST_DATE_ENTRETIEN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR_NOM Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR_NOM(int i) {
		return "NOM_ST_EVALUATEUR_NOM" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_EVALUATEUR_NOM
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_NOM(int i) {
		return getZone(getNOM_ST_EVALUATEUR_NOM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEUR_FONCTION
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_EVALUATEUR_FONCTION(int i) {
		return "NOM_ST_EVALUATEUR_FONCTION" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_EVALUATEUR_FONCTION Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_FONCTION(int i) {
		return getZone(getNOM_ST_EVALUATEUR_FONCTION(i));
	}

	public List<BirtDto> getListeEvaluateurEae() {
		return listeEvaluateurEae == null ? new ArrayList<BirtDto>() : listeEvaluateurEae;
	}

	public void setListeEvaluateurEae(List<BirtDto> listeEvaluateurEae) {
		this.listeEvaluateurEae = listeEvaluateurEae;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NIVEAU Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_NIVEAU() {
		return "NOM_ST_NIVEAU";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_NIVEAU Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NIVEAU() {
		return getZone(getNOM_ST_NIVEAU());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOTE Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_NOTE() {
		return "NOM_ST_NOTE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_NOTE Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NOTE() {
		return getZone(getNOM_ST_NOTE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_COMMENTAIRE_EVALUATEUR Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE_EVALUATEUR() {
		return "NOM_ST_COMMENTAIRE_EVALUATEUR";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_COMMENTAIRE_EVALUATEUR Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE_EVALUATEUR() {
		return getZone(getNOM_ST_COMMENTAIRE_EVALUATEUR());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_SHD Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVIS_SHD() {
		return "NOM_ST_AVIS_SHD";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_AVIS_SHD Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVIS_SHD() {
		return getZone(getNOM_ST_AVIS_SHD());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVCT_DIFF Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVCT_DIFF() {
		return "NOM_ST_AVCT_DIFF";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_AVCT_DIFF Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVCT_DIFF() {
		return getZone(getNOM_ST_AVCT_DIFF());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_CLASSE
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_CLASSE() {
		return "NOM_ST_CHANGEMENT_CLASSE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_CLASSE Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_CLASSE() {
		return getZone(getNOM_ST_CHANGEMENT_CLASSE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_REVALO Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_AVIS_REVALO() {
		return "NOM_ST_AVIS_REVALO";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_AVIS_REVALO
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_AVIS_REVALO() {
		return getZone(getNOM_ST_AVIS_REVALO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RAPPORT_CIRCON Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_RAPPORT_CIRCON() {
		return "NOM_ST_RAPPORT_CIRCON";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_RAPPORT_CIRCON
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_RAPPORT_CIRCON() {
		return getZone(getNOM_ST_RAPPORT_CIRCON());
	}

	public List<EaeObjectifProDto> getListeObjectifPro() {
		return listeObjectifPro == null ? new ArrayList<EaeObjectifProDto>() : listeObjectifPro;
	}

	public void setListeObjectifPro(List<EaeObjectifProDto> listeObjectifPro) {
		this.listeObjectifPro = listeObjectifPro;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_PRO Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_PRO(int i) {
		return "NOM_ST_LIB_OBJ_PRO" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_OBJ_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_PRO(int i) {
		return getZone(getNOM_ST_LIB_OBJ_PRO(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_MESURE_PRO Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_MESURE_PRO(int i) {
		return "NOM_ST_LIB_MESURE_PRO" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_MESURE_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_MESURE_PRO(int i) {
		return getZone(getNOM_ST_LIB_MESURE_PRO(i));
	}

	public List<EaeItemPlanActionDto> getListeObjectifIndi() {
		return listeObjectifIndi == null ? new ArrayList<EaeItemPlanActionDto>() : listeObjectifIndi;
	}

	public void setListeObjectifIndi(List<EaeItemPlanActionDto> listeObjectifIndi) {
		this.listeObjectifIndi = listeObjectifIndi;
	}

	public List<EaeDeveloppementDto> getListeDeveloppement() {
		return listeDeveloppement == null ? new ArrayList<EaeDeveloppementDto>() : listeDeveloppement;
	}

	public void setListeDeveloppement(List<EaeDeveloppementDto> listeDeveloppement) {
		this.listeDeveloppement = listeDeveloppement;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_INDI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_INDI(int i) {
		return "NOM_ST_LIB_OBJ_INDI" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_OBJ_INDI
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_INDI(int i) {
		return getZone(getNOM_ST_LIB_OBJ_INDI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_GEO Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_GEO() {
		return "NOM_ST_MOB_GEO";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_MOB_GEO Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_GEO() {
		return getZone(getNOM_ST_MOB_GEO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_FONCT Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_FONCT() {
		return "NOM_ST_MOB_FONCT";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_MOB_FONCT Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_FONCT() {
		return getZone(getNOM_ST_MOB_FONCT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_METIER
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_METIER() {
		return "NOM_ST_CHANGEMENT_METIER";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_METIER Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_METIER() {
		return getZone(getNOM_ST_CHANGEMENT_METIER());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DELAI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DELAI() {
		return "NOM_ST_DELAI";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_DELAI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DELAI() {
		return getZone(getNOM_ST_DELAI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_SERV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_SERV() {
		return "NOM_ST_MOB_SERV";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_MOB_SERV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_SERV() {
		return getZone(getNOM_ST_MOB_SERV());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_DIR Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_DIR() {
		return "NOM_ST_MOB_DIR";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_MOB_DIR Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_DIR() {
		return getZone(getNOM_ST_MOB_DIR());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_COLL Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_COLL() {
		return "NOM_ST_MOB_COLL";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_MOB_COLL Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_COLL() {
		return getZone(getNOM_ST_MOB_COLL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_COLL Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_COLL() {
		return "NOM_ST_NOM_COLL";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_NOM_COLL Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_COLL() {
		return getZone(getNOM_ST_NOM_COLL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOB_AUTRE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_MOB_AUTRE() {
		return "NOM_ST_MOB_AUTRE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_MOB_AUTRE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_MOB_AUTRE() {
		return getZone(getNOM_ST_MOB_AUTRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CONCOURS Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CONCOURS() {
		return "NOM_ST_CONCOURS";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_CONCOURS Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CONCOURS() {
		return getZone(getNOM_ST_CONCOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_CONCOURS Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_CONCOURS() {
		return "NOM_ST_NOM_CONCOURS";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_NOM_CONCOURS
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_CONCOURS() {
		return getZone(getNOM_ST_NOM_CONCOURS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_VAE Date de création
	 * : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_VAE() {
		return "NOM_ST_VAE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_VAE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_VAE() {
		return getZone(getNOM_ST_VAE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_VAE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_NOM_VAE() {
		return "NOM_ST_NOM_VAE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_NOM_VAE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_NOM_VAE() {
		return getZone(getNOM_ST_NOM_VAE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_PARTIEL Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_TPS_PARTIEL() {
		return "NOM_ST_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_TPS_PARTIEL
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_TPS_PARTIEL() {
		return getZone(getNOM_ST_TPS_PARTIEL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POURC_TPS_PARTIEL
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_POURC_TPS_PARTIEL() {
		return "NOM_ST_POURC_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_POURC_TPS_PARTIEL Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_POURC_TPS_PARTIEL() {
		return getZone(getNOM_ST_POURC_TPS_PARTIEL());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RETRAITE Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_RETRAITE() {
		return "NOM_ST_RETRAITE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_RETRAITE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_RETRAITE() {
		return getZone(getNOM_ST_RETRAITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_RETRAITE Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_RETRAITE() {
		return "NOM_ST_DATE_RETRAITE";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_DATE_RETRAITE
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_RETRAITE() {
		return getZone(getNOM_ST_DATE_RETRAITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AUTRE_PERSP Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_AUTRE_PERSP() {
		return "NOM_ST_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_AUTRE_PERSP
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_AUTRE_PERSP() {
		return getZone(getNOM_ST_AUTRE_PERSP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_AUTRE_PERSP Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_AUTRE_PERSP() {
		return "NOM_ST_LIB_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_LIB_AUTRE_PERSP Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_AUTRE_PERSP() {
		return getZone(getNOM_ST_LIB_AUTRE_PERSP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COM_EVOLUTION Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_COM_EVOLUTION() {
		return "NOM_ST_COM_EVOLUTION";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_COM_EVOLUTION
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_COM_EVOLUTION() {
		return getZone(getNOM_ST_COM_EVOLUTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_DEV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_TYPE_DEV(int i) {
		return "NOM_ST_TYPE_DEV" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_TYPE_DEV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_TYPE_DEV(int i) {
		return getZone(getNOM_ST_TYPE_DEV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_DEV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_DEV(int i) {
		return "NOM_ST_LIB_DEV" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_DEV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_DEV(int i) {
		return getZone(getNOM_ST_LIB_DEV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ECHEANCE_DEV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ECHEANCE_DEV(int i) {
		return "NOM_ST_ECHEANCE_DEV" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_ECHEANCE_DEV
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ECHEANCE_DEV(int i) {
		return getZone(getNOM_ST_ECHEANCE_DEV(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PRIORISATION_DEV
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_PRIORISATION_DEV(int i) {
		return "NOM_ST_PRIORISATION_DEV" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone :
	 * ST_PRIORISATION_DEV Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_PRIORISATION_DEV(int i) {
		return getZone(getNOM_ST_PRIORISATION_DEV(i));
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_NIVEAU
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_NIVEAU() {
		return "NOM_RG_NIVEAU";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_NIVEAU
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_NIVEAU() {
		return getZone(getNOM_RG_NIVEAU());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_EXCEL Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_EXCEL() {
		return "NOM_RB_NIVEAU_EXCEL";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_SATIS Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_SATIS() {
		return "NOM_RB_NIVEAU_SATIS";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_PROGR Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_PROGR() {
		return "NOM_RB_NIVEAU_PROGR";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_NIVEAU_INSU Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_NIVEAU_INSU() {
		return "NOM_RB_NIVEAU_INSU";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_REVA
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_REVA() {
		return "NOM_RG_REVA";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_REVA Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_REVA() {
		return getZone(getNOM_RG_REVA());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_REVA_FAV Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_REVA_FAV() {
		return "NOM_RB_REVA_FAV";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_REVA_DEF Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_REVA_DEF() {
		return "NOM_RB_REVA_DEF";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_CHGT
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_CHGT() {
		return "NOM_RG_CHGT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_CHGT Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_CHGT() {
		return getZone(getNOM_RG_CHGT());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CHGT_FAV Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CHGT_FAV() {
		return "NOM_RB_CHGT_FAV";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CHGT_DEF Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CHGT_DEF() {
		return "NOM_RB_CHGT_DEF";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_AD Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_AD() {
		return "NOM_RG_AD";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_AD Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_AD() {
		return getZone(getNOM_RG_AD());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AD_MIN Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AD_MIN() {
		return "NOM_RB_AD_MIN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AD_MOY Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AD_MOY() {
		return "NOM_RB_AD_MOY";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AD_MAX Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AD_MAX() {
		return "NOM_RB_AD_MAX";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (15/03/11 10:49:55)
	 * 
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			// Vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			EaeDto eae = getEaeCourant();
			if (eae != null) {

				if (!performSauvegardeEvaluation(request, eae))
					return false;

				if (!performSauvegardeEvolution(request, eae)) {
					// "ERR164",
					// "Une erreur est survenue dans la sauvegarde de l'EAE.
					// Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
					return false;
				}

			} else {
				// "ERR164",
				// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci
				// de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
				return false;
			}

		}

		// "INF501", "L'EAE a été correctement sauvegardé."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF501"));
		return true;
	}

	private boolean performSauvegardeEvolution(HttpServletRequest request, EaeDto eae) throws Exception {

		/************* PARTIE EVOLUTION **********************/
		EaeEvolutionDto evolution = eae.getEvolution();
		if (null == evolution) {
			evolution = new EaeEvolutionDto();
		}

		// Mobilités
		String mobGeo = getVAL_RG_MOB_GEO();
		if (mobGeo.equals(getNOM_RB_MOB_GEO_NON())) {
			evolution.setMobiliteGeo(false);
		} else {
			evolution.setMobiliteGeo(true);
		}
		String mobFonct = getVAL_RG_MOB_FONCT();
		if (mobFonct.equals(getNOM_RB_MOB_FONCT_NON())) {
			evolution.setMobiliteFonctionnelle(false);
		} else {
			evolution.setMobiliteFonctionnelle(true);
		}
		String mobServ = getVAL_RG_MOB_SERV();
		if (mobServ.equals(getNOM_RB_MOB_SERV_NON())) {
			evolution.setMobiliteService(false);
		} else {
			evolution.setMobiliteService(true);
		}
		String mobDir = getVAL_RG_MOB_DIR();
		if (mobDir.equals(getNOM_RB_MOB_DIR_NON())) {
			evolution.setMobiliteDirection(false);
		} else {
			evolution.setMobiliteDirection(true);
		}
		String mobColl = getVAL_RG_MOB_COLL();
		if (mobColl.equals(getNOM_RB_MOB_COLL_NON())) {
			evolution.setMobiliteCollectivite(false);
		} else {
			evolution.setMobiliteCollectivite(true);
		}
		String mobAutre = getVAL_RG_MOB_AUTRE();
		if (mobAutre.equals(getNOM_RB_MOB_AUTRE_NON())) {
			evolution.setMobiliteAutre(false);
		} else {
			evolution.setMobiliteAutre(true);
		}

		// Changement de metier
		String metier = getVAL_RG_METIER();
		if (metier.equals(getNOM_RB_METIER_NON())) {
			evolution.setChangementMetier(false);
		} else {
			evolution.setChangementMetier(true);
		}

		// Delai
		String delai = getVAL_RG_DELAI();
		EaeListeDto delaiEnvisage = new EaeListeDto();
		if (delai.equals(getNOM_RB_DELAI_1())) {
			delaiEnvisage.setCourant("MOINS1AN");
		} else if (delai.equals(getNOM_RB_DELAI_2())) {
			delaiEnvisage.setCourant("ENTRE1ET2ANS");
		} else {
			delaiEnvisage.setCourant("ENTRE2ET4ANS");
		}
		evolution.setDelaiEnvisage(delaiEnvisage);

		// concours
		String concours = getVAL_RG_CONCOURS();
		if (concours.equals(getNOM_RB_CONCOURS_NON())) {
			evolution.setConcours(false);
		} else {
			evolution.setConcours(true);
		}
		// vae
		String vae = getVAL_RG_VAE();
		if (vae.equals(getNOM_RB_VAE_NON())) {
			evolution.setVae(false);
		} else {
			evolution.setVae(true);
		}
		// temps partiel
		String tpsPartiel = getVAL_RG_TPS_PARTIEL();
		if (tpsPartiel.equals(getNOM_RB_TPS_PARTIEL_NON())) {
			evolution.setTempsPartiel(false);
		} else {
			evolution.setTempsPartiel(true);
		}
		// retraite
		String retraite = getVAL_RG_RETRAITE();
		if (retraite.equals(getNOM_RB_RETRAITE_NON())) {
			evolution.setRetraite(false);
		} else {
			evolution.setRetraite(true);
		}
		// autres persp
		String autrePersp = getVAL_RG_AUTRE_PERSP();
		if (autrePersp.equals(getNOM_RB_AUTRE_PERSP_NON())) {
			evolution.setAutrePerspective(false);
		} else {
			evolution.setAutrePerspective(true);
		}

		// pour les libelles
		// collectivite
		String nomColl = getVAL_ST_NOM_COLL().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NOM_COLL();
		evolution.setNomCollectivite(nomColl);
		// concours
		String nomConcours = getVAL_ST_NOM_CONCOURS().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NOM_CONCOURS();
		evolution.setNomConcours(nomConcours);
		// vae
		String nomVae = getVAL_ST_NOM_VAE().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NOM_VAE();
		evolution.setNomVae(nomVae);
		// autre persp
		String nomAutrePersp = getVAL_ST_LIB_AUTRE_PERSP().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_LIB_AUTRE_PERSP();
		evolution.setLibelleAutrePerspective(nomAutrePersp);

		// date de la retraite
		String dateRetraire = getVAL_ST_DATE_RETRAITE().equals(Const.CHAINE_VIDE) ? null : Services.formateDate(getVAL_ST_DATE_RETRAITE());

		evolution.setDateRetraite(dateRetraire != null ? sdf.parse(dateRetraire) : null);

		// commentaire de l'evolution
		if (!getVAL_ST_COM_EVOLUTION().equals(Const.CHAINE_VIDE)) {
			EaeCommentaireDto comm = new EaeCommentaireDto();
			comm.setText(getVAL_ST_COM_EVOLUTION());
			evolution.setCommentaireEvolution(comm);
		}

		// pourcentage temps partiel
		int numLigneBH = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_SELECT()))
				: -1);
		Horaire horaire = numLigneBH > 0 ? (Horaire) getListeHoraire().get(numLigneBH - 1) : null;

		EaeListeDto prctgTpsPartiel = new EaeListeDto();
		prctgTpsPartiel.setCourant(horaire == null ? null : horaire.getCdtHor());
		evolution.setPourcentageTempsPartiel(prctgTpsPartiel);

		eae.setEvolution(evolution);

		eaeService.saveEvolution(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), evolution);

		return true;
	}

	private boolean performSauvegardeEvaluation(HttpServletRequest request, EaeDto eae) throws Exception {
		/************* PARTIE EVALUATION **********************/
		EaeEvaluationDto eval = eae.getEvaluation();
		// commentaire de l'evaluateur
		if (!getVAL_ST_COMMENTAIRE_EVALUATEUR().equals(Const.CHAINE_VIDE)) {
			EaeCommentaireDto commDto = new EaeCommentaireDto();
			commDto.setText(getVAL_ST_COMMENTAIRE_EVALUATEUR());
			eval.setCommentaireEvaluateur(commDto);
		}

		// Niveau
		EaeListeDto listeNiveau = new EaeListeDto();
		String niveau = getVAL_RG_NIVEAU();
		if (niveau.equals(getNOM_RB_NIVEAU_EXCEL())) {
			listeNiveau.setCourant("EXCELLENT");
		} else if (niveau.equals(getNOM_RB_NIVEAU_SATIS())) {
			listeNiveau.setCourant("SATISFAISANT");
		} else if (niveau.equals(getNOM_RB_NIVEAU_PROGR())) {
			listeNiveau.setCourant("NECESSITANT_DES_PROGRES");
		} else {
			listeNiveau.setCourant("INSUFFISANT");
		}
		eval.setNiveau(listeNiveau);

		// note
		Float note = Float.parseFloat(getVAL_ST_NOTE().replace(',', '.'));
		eval.setNoteAnnee(note.doubleValue());

		// Avis SHD
		String shd = getVAL_RG_SHD();
		if (shd.equals(getNOM_RB_SHD_MIN())) {
			eval.setAvisShd("durée minimale");
		} else if (shd.equals(getNOM_RB_SHD_MAX())) {
			eval.setAvisShd("durée maximale");
		} else if (shd.equals(getNOM_RB_SHD_MOY())) {
			eval.setAvisShd("durée moyenne");
		} else if (shd.equals(getNOM_RB_SHD_FAV())) {
			eval.setAvisShd("Favorable");
		} else if (shd.equals(getNOM_RB_SHD_DEFAV())) {
			eval.setAvisShd("Défavorable");
		}

		// Avancement Diff
		String ad = getVAL_RG_AD();
		EaeListeDto propositionAvancement = new EaeListeDto();
		if (ad.equals(getNOM_RB_AD_MIN())) {
			propositionAvancement.setCourant("MINI");
		} else if (ad.equals(getNOM_RB_AD_MAX())) {
			propositionAvancement.setCourant("MAXI");
		} else {
			propositionAvancement.setCourant("MOY");
		}
		eval.setPropositionAvancement(propositionAvancement);

		// Changement classe
		String chgt = getVAL_RG_CHGT();
		if (chgt.equals(getNOM_RB_CHGT_DEF())) {
			eval.setAvisChangementClasse(false);
		} else {
			eval.setAvisChangementClasse(true);
		}

		// Revalorisation
		String reva = getVAL_RG_REVA();
		if (reva.equals(getNOM_RB_REVA_DEF())) {
			eval.setAvisRevalorisation(false);
		} else {
			eval.setAvisRevalorisation(true);
		}

		// rapport circonstancie de l'evaluateur
		if (!getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			EaeCommentaireDto commDto = new EaeCommentaireDto();
			commDto.setText(getVAL_ST_RAPPORT_CIRCON());
			eval.setCommentaireAvctEvaluateur(commDto);
		}

		// sauvegarde de l evaluation par WS
		ReturnMessageDto result = eaeService.saveEvaluation(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), eval);

		if (0 < result.getErrors().size()) {
			getTransaction().declarerErreur(result.getErrors().get(0));
			return false;
		}
		return true;
	}

	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// ********************************************
		// ///////////////////NOTE/////////////////////
		// ********************************************
		if (!Services.estFloat(getVAL_ST_NOTE())) {
			// "ERR992", "La zone @ doit étre numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "note"));
			return false;
		}
		Float note = Float.parseFloat(getVAL_ST_NOTE().replace(',', '.'));
		if (getVAL_ST_NOTE().equals(Const.CHAINE_VIDE) || note == 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "note"));
			return false;
		}
		// si la note n'est pas comprise entre 0 et 20
		if (0 > note || 20 < note) {
			// "ERR160", "La note doit étre comprise entre 0 et 20.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR160"));
			return false;

		}
		// ********************************************
		// /////////RAPPORT CIRCONSTANCIE//////////////
		// ********************************************
		// si min ou max alors rapport circonstancie obligatoire
		if ((getVAL_RG_AD().equals(getNOM_RB_AD_MIN()) || getVAL_RG_AD().equals(getNOM_RB_AD_MAX()))
				&& getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			// "ERR162",
			// "Le contenu du rapport circonstancie ne doit pas étre vide pour
			// une durée d'avancement minimale ou maximale.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR162"));
			return false;
		}
		if (getVAL_RG_AD().equals(getNOM_RB_AD_MOY()) && !getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			// "ERR163",
			// "Le contenu du rapport circonstancie ne doit pas étre rempli pour
			// une durée d'avancement moyenne.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR163"));
			return false;
		}
		// ********************************************
		// /////////DATE RETRAITE//////////////
		// ********************************************
		// format date de retraite
		if (!getVAL_ST_DATE_RETRAITE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_RETRAITE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit étre au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de retraite"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_MOB_GEO
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_GEO() {
		return "NOM_RG_MOB_GEO";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_MOB_GEO
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_GEO() {
		return getZone(getNOM_RG_MOB_GEO());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_GEO_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_GEO_OUI() {
		return "NOM_RB_MOB_GEO_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_GEO_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_GEO_NON() {
		return "NOM_RB_MOB_GEO_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_MOB_FONCT Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_FONCT() {
		return "NOM_RG_MOB_FONCT";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_MOB_FONCT
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_FONCT() {
		return getZone(getNOM_RG_MOB_FONCT());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_FONCT_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_FONCT_OUI() {
		return "NOM_RB_MOB_FONCT_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_FONCT_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_FONCT_NON() {
		return "NOM_RB_MOB_FONCT_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_MOB_SERV Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_SERV() {
		return "NOM_RG_MOB_SERV";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_MOB_SERV
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_SERV() {
		return getZone(getNOM_RG_MOB_SERV());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_SERV_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_SERV_OUI() {
		return "NOM_RB_MOB_SERV_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_SERV_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_SERV_NON() {
		return "NOM_RB_MOB_SERV_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_MOB_DIR
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_DIR() {
		return "NOM_RG_MOB_DIR";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_MOB_DIR
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_DIR() {
		return getZone(getNOM_RG_MOB_DIR());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_DIR_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_DIR_OUI() {
		return "NOM_RB_MOB_DIR_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_DIR_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_DIR_NON() {
		return "NOM_RB_MOB_DIR_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_MOB_COLL Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_COLL() {
		return "NOM_RG_MOB_COLL";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_MOB_COLL
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_COLL() {
		return getZone(getNOM_RG_MOB_COLL());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_COLL_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_COLL_OUI() {
		return "NOM_RB_MOB_COLL_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_COLL_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_COLL_NON() {
		return "NOM_RB_MOB_COLL_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_MOB_AUTRE Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_MOB_AUTRE() {
		return "NOM_RG_MOB_AUTRE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_MOB_AUTRE
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_MOB_AUTRE() {
		return getZone(getNOM_RG_MOB_AUTRE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_AUTRE_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_AUTRE_OUI() {
		return "NOM_RB_MOB_AUTRE_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_MOB_AUTRE_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_MOB_AUTRE_NON() {
		return "NOM_RB_MOB_AUTRE_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_METIER
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_METIER() {
		return "NOM_RG_METIER";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_METIER
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_METIER() {
		return getZone(getNOM_RG_METIER());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_METIER_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_METIER_OUI() {
		return "NOM_RB_METIER_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_METIER_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_METIER_NON() {
		return "NOM_RB_METIER_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_DELAI
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_DELAI() {
		return "NOM_RG_DELAI";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_DELAI
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_DELAI() {
		return getZone(getNOM_RG_DELAI());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_DELAI_1 Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_DELAI_1() {
		return "NOM_RB_DELAI_1";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_DELAI_2 Date de création
	 * : (26/05/22 22:32:22)
	 * 
	 */
	public String getNOM_RB_DELAI_2() {
		return "NOM_RB_DELAI_2";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_DELAI_4 Date de création
	 * : (26/05/44 44:34:42)
	 * 
	 */
	public String getNOM_RB_DELAI_4() {
		return "NOM_RB_DELAI_4";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_CONCOURS Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_CONCOURS() {
		return "NOM_RG_CONCOURS";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_CONCOURS
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_CONCOURS() {
		return getZone(getNOM_RG_CONCOURS());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONCOURS_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CONCOURS_OUI() {
		return "NOM_RB_CONCOURS_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CONCOURS_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_CONCOURS_NON() {
		return "NOM_RB_CONCOURS_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_VAE
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_VAE() {
		return "NOM_RG_VAE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_VAE Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_VAE() {
		return getZone(getNOM_RG_VAE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VAE_OUI Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_VAE_OUI() {
		return "NOM_RB_VAE_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_VAE_NON Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_VAE_NON() {
		return "NOM_RB_VAE_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TPS_PARTIEL Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_TPS_PARTIEL() {
		return "NOM_RG_TPS_PARTIEL";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TPS_PARTIEL Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_TPS_PARTIEL() {
		return getZone(getNOM_RG_TPS_PARTIEL());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TPS_PARTIEL_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_TPS_PARTIEL_OUI() {
		return "NOM_RB_TPS_PARTIEL_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TPS_PARTIEL_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_TPS_PARTIEL_NON() {
		return "NOM_RB_TPS_PARTIEL_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_AUTRE_PERSP Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_AUTRE_PERSP() {
		return "NOM_RG_AUTRE_PERSP";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_AUTRE_PERSP Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_AUTRE_PERSP() {
		return getZone(getNOM_RG_AUTRE_PERSP());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AUTRE_PERSP_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AUTRE_PERSP_OUI() {
		return "NOM_RB_AUTRE_PERSP_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_AUTRE_PERSP_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_AUTRE_PERSP_NON() {
		return "NOM_RB_AUTRE_PERSP_NON";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_RETRAITE Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_RETRAITE() {
		return "NOM_RG_RETRAITE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_RETRAITE
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_RETRAITE() {
		return getZone(getNOM_RG_RETRAITE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RETRAITE_OUI Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_RETRAITE_OUI() {
		return "NOM_RB_RETRAITE_OUI";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_RETRAITE_NON Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_RETRAITE_NON() {
		return "NOM_RB_RETRAITE_NON";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BASE_HORAIRE Date de
	 * création : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_BASE_HORAIRE() {
		if (LB_BASE_HORAIRE == null)
			LB_BASE_HORAIRE = initialiseLazyLB();
		return LB_BASE_HORAIRE;
	}

	/**
	 * Setter de la liste: LB_BASE_HORAIRE Date de création : (05/09/11
	 * 14:28:25)
	 * 
	 */
	private void setLB_BASE_HORAIRE(String[] newLB_BASE_HORAIRE) {
		LB_BASE_HORAIRE = newLB_BASE_HORAIRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BASE_HORAIRE Date de
	 * création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_HORAIRE() {
		return "NOM_LB_BASE_HORAIRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BASE_HORAIRE_SELECT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_HORAIRE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_SELECT";
	}

	/**
	 * Méthode é personnaliser Retourne la valeur é afficher pour la zone de la
	 * JSP : LB_BASE_HORAIRE Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_BASE_HORAIRE() {
		return getLB_BASE_HORAIRE();
	}

	/**
	 * Méthode é personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_BASE_HORAIRE Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_BASE_HORAIRE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_SELECT());
	}

	private ArrayList<Horaire> getListeHoraire() {
		return listeHoraire;
	}

	private void setListeHoraire(ArrayList<Horaire> listeHoraire) {
		this.listeHoraire = listeHoraire;
	}

	private Hashtable<String, Horaire> getHashHoraire() {
		if (hashHoraire == null)
			hashHoraire = new Hashtable<String, Horaire>();
		return hashHoraire;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_OBJ_INDI Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER_OBJ_INDI() {
		return "NOM_PB_AJOUTER_OBJ_INDI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AJOUTER_OBJ_INDI(HttpServletRequest request) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		setObjectifIndiCourant(null);
		addZone(getNOM_ST_LIB_OBJ_INDI(), Const.CHAINE_VIDE);

		setObjectifIndiCourant(null);
		setObjectifProCourant(null);
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_AJOUT_OBJ_INDI);
		return true;
	}

	public String getNOM_PB_MODIFIER_OBJ_INDI(int i) {
		return "NOM_PB_MODIFIER_OBJ_INDI" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_OBJ_INDI(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeItemPlanActionDto plan = getListeObjectifIndi().get(indiceEltAModifier);
		setObjectifIndiCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_INDI(), plan.getLibelle());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_OBJ_INDI);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_OBJ_INDI Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_OBJ_INDI(int i) {
		return "NOM_PB_SUPPRIMER_OBJ_INDI" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_OBJ_INDI(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeItemPlanActionDto plan = getListeObjectifIndi().get(indiceEltASuprimer);
		setObjectifIndiCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_INDI(), plan.getLibelle());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION_OBJ_INDI);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_INDI Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_INDI() {
		return "NOM_ST_LIB_OBJ_INDI";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_OBJ_INDI
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_INDI() {
		return getZone(getNOM_ST_LIB_OBJ_INDI());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_VALIDER_OBJ_INDI
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER_OBJ_INDI() {
		return "NOM_PB_VALIDER_OBJ_INDI";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_OBJ_INDI(HttpServletRequest request) throws Exception {
		EaeDto eae = getEaeCourant();

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_AJOUT_OBJ_INDI)) {
			if (!performControlerChampObjIndi(request)) {
				return false;
			}
			EaeItemPlanActionDto planActionIndi = new EaeItemPlanActionDto();
			planActionIndi.setLibelle(getVAL_ST_LIB_OBJ_INDI());
			eae.getPlanAction().getListeObjectifsIndividuels().add(planActionIndi);
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_OBJ_INDI)) {
			if (!performControlerChampObjIndi(request)) {
				return false;
			}
			EaeItemPlanActionDto planActionIndi = getObjectifIndiCourant();

			for (EaeItemPlanActionDto objIndExist : eae.getPlanAction().getListeObjectifsIndividuels()) {
				if (objIndExist.getIdItemPlanAction().equals(planActionIndi.getIdItemPlanAction())) {
					objIndExist.setLibelle(getVAL_ST_LIB_OBJ_INDI());
					break;
				}
			}
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION_OBJ_INDI)) {
			EaeItemPlanActionDto planActionIndi = getObjectifIndiCourant();

			for (EaeItemPlanActionDto objIndExist : eae.getPlanAction().getListeObjectifsIndividuels()) {
				if (objIndExist.getIdItemPlanAction().equals(planActionIndi.getIdItemPlanAction())) {
					eae.getPlanAction().getObjectifsIndividuels().remove(objIndExist);
					break;
				}
			}
		}

		// on sauvegarde par WS EAE
		eaeService.savePlanAction(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), eae.getPlanAction());

		initialiseEae();
		setObjectifIndiCourant(null);
		setObjectifProCourant(null);
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	private boolean performControlerChampObjIndi(HttpServletRequest request) {
		if (getVAL_ST_LIB_OBJ_INDI().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ANNULER_OBJ_INDI
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER_OBJ_INDI() {
		return "NOM_PB_ANNULER_OBJ_INDI";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER_OBJ_INDI(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setObjectifIndiCourant(null);
		setObjectifProCourant(null);
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	public EaeItemPlanActionDto getObjectifIndiCourant() {
		return objectifIndiCourant;
	}

	public void setObjectifIndiCourant(EaeItemPlanActionDto objectifIndiCourant) {
		this.objectifIndiCourant = objectifIndiCourant;
	}

	public EaeObjectifProDto getObjectifProCourant() {
		return objectifProCourant;
	}

	public void setObjectifProCourant(EaeObjectifProDto objectifProCourant) {
		this.objectifProCourant = objectifProCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_OBJ_PRO Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER_OBJ_PRO() {
		return "NOM_PB_AJOUTER_OBJ_PRO";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AJOUTER_OBJ_PRO(HttpServletRequest request) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_LIB_OBJ_PRO(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIB_MESURE_PRO(), Const.CHAINE_VIDE);

		setObjectifIndiCourant(null);
		setObjectifProCourant(null);
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_AJOUT_OBJ_PRO);
		return true;
	}

	public String getNOM_PB_MODIFIER_OBJ_PRO(int i) {
		return "NOM_PB_MODIFIER_OBJ_PRO" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_OBJ_PRO(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeObjectifProDto plan = getListeObjectifPro().get(indiceEltAModifier);
		setObjectifProCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_PRO(), plan.getObjectif());
		addZone(getNOM_ST_LIB_MESURE_PRO(), plan.getIndicateur());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_OBJ_PRO);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER_OBJ_PRO Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_OBJ_PRO(int i) {
		return "NOM_PB_SUPPRIMER_OBJ_PRO" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_OBJ_PRO(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeObjectifProDto plan = getListeObjectifPro().get(indiceEltASuprimer);
		setObjectifProCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_PRO(), plan.getObjectif());
		addZone(getNOM_ST_LIB_MESURE_PRO(), plan.getObjectif());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION_OBJ_PRO);

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_OBJ_PRO Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_OBJ_PRO() {
		return "NOM_ST_LIB_OBJ_PRO";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_OBJ_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_PRO() {
		return getZone(getNOM_ST_LIB_OBJ_PRO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_MESURE_PRO Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_MESURE_PRO() {
		return "NOM_ST_LIB_MESURE_PRO";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_MESURE_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_MESURE_PRO() {
		return getZone(getNOM_ST_LIB_MESURE_PRO());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_VALIDER_OBJ_PRO Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER_OBJ_PRO() {
		return "NOM_PB_VALIDER_OBJ_PRO";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_OBJ_PRO(HttpServletRequest request) throws Exception {
		EaeDto eae = getEaeCourant();

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_AJOUT_OBJ_PRO)) {
			if (!performControlerChampObjPro(request)) {
				return false;
			}
			EaeObjectifProDto planActionPro = new EaeObjectifProDto();
			planActionPro.setObjectif(getVAL_ST_LIB_OBJ_PRO());
			planActionPro.setIndicateur(getVAL_ST_LIB_MESURE_PRO());
			eae.getPlanAction().getObjectifsProfessionnels().add(planActionPro);
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_OBJ_PRO)) {
			if (!performControlerChampObjPro(request)) {
				return false;
			}
			EaeObjectifProDto planActionPro = getObjectifProCourant();

			for (EaeObjectifProDto objProExist : eae.getPlanAction().getObjectifsProfessionnels()) {
				if (objProExist.getIdObjectifPro().equals(planActionPro.getIdObjectifPro())) {
					objProExist.setObjectif(getVAL_ST_LIB_OBJ_PRO());
					objProExist.setIndicateur(getVAL_ST_LIB_MESURE_PRO());
					break;
				}
			}
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION_OBJ_PRO)) {
			EaeObjectifProDto planActionPro = getObjectifProCourant();

			for (EaeObjectifProDto objProExist : eae.getPlanAction().getObjectifsProfessionnels()) {
				if (objProExist.getIdObjectifPro().equals(planActionPro.getIdObjectifPro())) {
					eae.getPlanAction().getObjectifsProfessionnels().remove(objProExist);
					break;
				}
			}
		}

		// sauvegarde via le WS EAE
		eaeService.savePlanAction(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), eae.getPlanAction());

		initialiseEae();
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		setObjectifIndiCourant(null);
		setObjectifProCourant(null);
		return true;
	}

	private boolean performControlerChampObjPro(HttpServletRequest request) {
		if (getVAL_ST_LIB_OBJ_PRO().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_ANNULER_OBJ_PRO Date
	 * de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER_OBJ_PRO() {
		return "NOM_PB_ANNULER_OBJ_PRO";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER_OBJ_PRO(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setObjectifIndiCourant(null);
		setObjectifProCourant(null);
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DEV Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DEV() {
		return "NOM_PB_AJOUTER_DEV";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AJOUTER_DEV(HttpServletRequest request) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		setDeveloppementCourant(null);
		addZone(getNOM_LB_TYPE_DEV_SELECT(), Const.ZERO);
		addZone(getNOM_ST_LIB_DEV(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_PRIO_DEV(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEV(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_AJOUT_DEV);
		return true;
	}

	public String getNOM_PB_MODIFIER_DEV(int i) {
		return "NOM_PB_MODIFIER_DEV" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_DEV(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeDeveloppementDto dev = getListeDeveloppement().get(indiceEltAModifier);
		setDeveloppementCourant(dev);
		ListItemDto typeDev = (ListItemDto) getHashTypeDeveloppement().get(dev.getTypeDeveloppement());
		if (typeDev != null) {
			int ligneTypeDev = getListeTypeDeveloppement().indexOf(typeDev);
			addZone(getNOM_LB_TYPE_DEV_SELECT(), String.valueOf(ligneTypeDev));
		}
		addZone(getNOM_ST_LIB_DEV(), dev.getLibelle());
		addZone(getNOM_ST_PRIO_DEV(), new Integer(dev.getPriorisation()).toString());
		addZone(getNOM_ST_DATE_DEV(), dev.getEcheance() == null ? Const.CHAINE_VIDE : new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheance()));

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_DEV);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DEV Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DEV(int i) {
		return "NOM_PB_SUPPRIMER_DEV" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DEV(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeDeveloppementDto dev = getListeDeveloppement().get(indiceEltASuprimer);
		setDeveloppementCourant(dev);
		ListItemDto typeDev = (ListItemDto) getHashTypeDeveloppement().get(dev.getTypeDeveloppement());
		if (typeDev != null) {
			int ligneTypeDev = getListeTypeDeveloppement().indexOf(typeDev);
			addZone(getNOM_LB_TYPE_DEV_SELECT(), String.valueOf(ligneTypeDev));
		}
		addZone(getNOM_ST_LIB_DEV(), dev.getLibelle());
		addZone(getNOM_ST_PRIO_DEV(), new Integer(dev.getPriorisation()).toString());
		addZone(getNOM_ST_DATE_DEV(), dev.getEcheance() == null ? Const.CHAINE_VIDE : new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheance()));

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION_DEV);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_VALIDER_OBJ_INDI
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_VALIDER_DEV() {
		return "NOM_PB_VALIDER_DEV";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_DEV(HttpServletRequest request) throws Exception {

		EaeDto eae = getEaeCourant();
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_AJOUT_DEV) && !getVAL_ST_LIB_DEV().equals(Const.CHAINE_VIDE)) {
			if (!performControlerChampDev(request)) {
				return false;
			}
			ajouterEaeDeveloppement(eae);

		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_DEV)) {
			if (!performControlerChampDev(request)) {
				return false;
			}
			supprimerEaeDeveloppement(getDeveloppementCourant(), eae);
			ajouterEaeDeveloppement(eae);

		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION_DEV)) {
			supprimerEaeDeveloppement(getDeveloppementCourant(), eae);
		}

		// sauvegarde via WS EAE
		ReturnMessageDto result = eaeService.saveEvolution(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), eae.getEvolution());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0));
			return false;
		}

		initialiseEae();
		setDeveloppementCourant(null);
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	private void ajouterEaeDeveloppement(EaeDto eae) throws ParseException {

		EaeDeveloppementDto dev = new EaeDeveloppementDto();
		dev.setLibelle(getVAL_ST_LIB_DEV());
		dev.setEcheance(sdf.parse(getVAL_ST_DATE_DEV()));
		dev.setPriorisation(Integer.valueOf(getVAL_ST_PRIO_DEV()));
		// type developpement
		int numLigneTypeDev = (Services.estNumerique(getZone(getNOM_LB_TYPE_DEV_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_TYPE_DEV_SELECT()))
				: -1);
		ListItemDto typeDev = numLigneTypeDev > -1 ? (ListItemDto) getListeTypeDeveloppement().get(numLigneTypeDev) : null;
		dev.setTypeDeveloppement(typeDev.getValeur());

		switch (EaeTypeDeveloppementEnum.valueOf(typeDev.getValeur())) {
			case CONNAISSANCE:
				eae.getEvolution().getDeveloppementConnaissances().add(dev);
				break;
			case COMPETENCE:
				eae.getEvolution().getDeveloppementCompetences().add(dev);
				break;
			case COMPORTEMENT:
				eae.getEvolution().getDeveloppementComportement().add(dev);
				break;
			case CONCOURS:
				eae.getEvolution().getDeveloppementExamensConcours().add(dev);
				break;
			case FORMATEUR:
				eae.getEvolution().getDeveloppementFormateur().add(dev);
				break;
			case PERSONNEL:
				eae.getEvolution().getDeveloppementPersonnel().add(dev);
				break;
		}
	}

	private void supprimerEaeDeveloppement(EaeDeveloppementDto dev, EaeDto eae) {

		switch (EaeTypeDeveloppementEnum.valueOf(dev.getTypeDeveloppement())) {
			case CONNAISSANCE:
				eae.getEvolution().getDeveloppementConnaissances().remove(dev);
				break;
			case COMPETENCE:
				eae.getEvolution().getDeveloppementCompetences().remove(dev);
				break;
			case COMPORTEMENT:
				eae.getEvolution().getDeveloppementComportement().remove(dev);
				break;
			case CONCOURS:
				eae.getEvolution().getDeveloppementExamensConcours().remove(dev);
				break;
			case FORMATEUR:
				eae.getEvolution().getDeveloppementFormateur().remove(dev);
				break;
			case PERSONNEL:
				eae.getEvolution().getDeveloppementPersonnel().remove(dev);
				break;
		}
	}

	private boolean performControlerChampDev(HttpServletRequest request) {
		if (getVAL_ST_PRIO_DEV().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "priorisation"));
			return false;
		}
		if (!Services.estNumerique(getVAL_ST_PRIO_DEV())) {
			// "ERR992", "La zone @ doit étre numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "priorisation"));
			return false;
		}
		if (getVAL_ST_DATE_DEV().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date d'échéance"));
			return false;
		}
		// format date de dev
		if (!Services.estUneDate(getVAL_ST_DATE_DEV())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit étre au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'échéance"));
			return false;
		}
		if (getVAL_ST_LIB_DEV().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_DEV Date de création
	 * : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_PB_ANNULER_DEV() {
		return "NOM_PB_ANNULER_DEV";
	}

	/**
	 * Initialisation des zones é afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_ANNULER_DEV(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setDeveloppementCourant(null);
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_DEV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_LIB_DEV() {
		return "NOM_ST_LIB_DEV";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_LIB_DEV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_DEV() {
		return getZone(getNOM_ST_LIB_DEV());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PRIO_DEV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_PRIO_DEV() {
		return "NOM_ST_PRIO_DEV";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_PRIO_DEV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_PRIO_DEV() {
		return getZone(getNOM_ST_PRIO_DEV());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEV Date de
	 * création : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_DATE_DEV() {
		return "NOM_ST_DATE_DEV";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_DATE_DEV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_DEV() {
		return getZone(getNOM_ST_DATE_DEV());
	}

	public EaeDeveloppementDto getDeveloppementCourant() {
		return developpementCourant;
	}

	public void setDeveloppementCourant(EaeDeveloppementDto developpementCourant) {
		this.developpementCourant = developpementCourant;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DEV Date de création
	 * : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_TYPE_DEV() {
		if (LB_TYPE_DEV == null)
			LB_TYPE_DEV = initialiseLazyLB();
		return LB_TYPE_DEV;
	}

	/**
	 * Setter de la liste: LB_TYPE_DEV Date de création : (05/09/11 14:28:25)
	 * 
	 */
	private void setLB_TYPE_DEV(String[] newLB_TYPE_DEV) {
		LB_TYPE_DEV = newLB_TYPE_DEV;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DEV Date de création
	 * : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_TYPE_DEV() {
		return "NOM_LB_TYPE_DEV";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TYPE_DEV_SELECT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_TYPE_DEV_SELECT() {
		return "NOM_LB_TYPE_DEV_SELECT";
	}

	/**
	 * Méthode é personnaliser Retourne la valeur é afficher pour la zone de la
	 * JSP : LB_TYPE_DEV Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DEV() {
		return getLB_TYPE_DEV();
	}

	/**
	 * Méthode é personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TYPE_DEV Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_TYPE_DEV_SELECT() {
		return getZone(getNOM_LB_TYPE_DEV_SELECT());
	}

	private List<ListItemDto> getListeTypeDeveloppement() {
		return listeTypeDeveloppement;
	}

	private void setListeTypeDeveloppement(List<ListItemDto> listeTypeDeveloppement) {
		this.listeTypeDeveloppement = listeTypeDeveloppement;
	}

	private Hashtable<String, ListItemDto> getHashTypeDeveloppement() {
		if (hashTypeDeveloppement == null)
			hashTypeDeveloppement = new Hashtable<String, ListItemDto>();
		return hashTypeDeveloppement;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_SHD
	 * Date de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RG_SHD() {
		return "NOM_RG_SHD";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_SHD Date
	 * de création : (26/05/11 11:31:12)
	 * 
	 */
	public String getVAL_RG_SHD() {
		return getZone(getNOM_RG_SHD());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SHD_MIN Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_SHD_MIN() {
		return "NOM_RB_SHD_MIN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SHD_MOY Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_SHD_MOY() {
		return "NOM_RB_SHD_MOY";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SHD_MAX Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_SHD_MAX() {
		return "NOM_RB_SHD_MAX";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SHD_FAV Date de création
	 * : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_SHD_FAV() {
		return "NOM_RB_SHD_FAV";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SHD_DEFAV Date de
	 * création : (26/05/11 11:31:12)
	 * 
	 */
	public String getNOM_RB_SHD_DEFAV() {
		return "NOM_RB_SHD_DEFAV";
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

		// Récup de l'eae courant
		EaeDto eaeCourant = (EaeDto) getListeEae().get(indiceEltDocument);
		setEaeCourant(eaeCourant);

		// init des documents EAE de l'agent
		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

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
	private void setListeDocuments(List<EaeFinalizationDto> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public List<EaeFinalizationDto> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<EaeFinalizationDto>();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des eaes
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de l'agent
		List<EaeFinalizationDto> listeDocAgent = getEaeCourant().getFinalisation();
		setListeDocuments(listeDocAgent);

		int indiceDocEae = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				EaeFinalizationDto doc = getListeDocuments().get(i);

				addZone(getNOM_ST_DATE_DOC(indiceDocEae), sdf.format(doc.getDateFinalisation()));
				addZone(getNOM_ST_COMMENTAIRE(indiceDocEae), doc.getCommentaire());
				String idDocument = null;
				try {
					idDocument = doc.getIdDocument();
				} catch (Exception e) {
					// il n'y a pas de finalisation
				}
				addZone(getNOM_ST_URL_DOC_LISTE_DOC(indiceDocEae),
						(null == idDocument || idDocument.equals(Const.CHAINE_VIDE)) ? "&nbsp;" : AlfrescoCMISService.getUrlOfDocument(idDocument));

				

				indiceDocEae++;
			}
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_DATE_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_DOC Date de création :
	 * (17/10/11 13:46:25)
	 * 
	 */
	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
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
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT Date
	 * de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur é afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
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

		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'), fichierUpload.getName().length());
		if (!extension.toLowerCase().equals(".pdf")) {
			// "ERR165", "Le fichier doit étre au format PDF."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR165"));
			result &= false;
		}
		return result;
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

		EaeDto eae = getEaeCourant();

		if (!creeDocument(request, eae)) {
			return false;
		}

		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met a jour le tableau des documents
		initialiseListeDocuments(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, EaeDto eae) throws Exception {
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}
		List<BirtDto> premierEvaluateur = eae.getEvaluateurs();

		Integer numIncrement = eaeService.chercherEaeNumIncrement(getAgentConnecte(request).getIdAgent());

		// TODO premierEvaluateur.get(0).getAgent().getIdAgent() =+> ID AGENT de l agent lui meme plutot
		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'), fichierUpload.getName().length());
		String nom = "EAE_" + premierEvaluateur.get(0).getAgent().getIdAgent() + "_" + eae.getIdEae() + "_" + numIncrement.toString() + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf")) {
			upload = uploadFichierPDF(fichierUpload, nom);
		}
		if (!upload) {
			// "ERR164",
			// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci de
			// contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
			return false;
		}

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private boolean uploadFichierPDF(File f, String nomFichier) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");

		File newFile = new File(repPartage + "/" + nomFichier);

		FileInputStream in = new FileInputStream(f);

		try {
			FileOutputStream out = new FileOutputStream(newFile);
			try {
				byte[] byteBuffer = new byte[in.available()];
				@SuppressWarnings("unused")
				int s = in.read(byteBuffer);
				out.write(byteBuffer);
				out.flush();
				resultat = true;
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}

		return resultat;
	}

	/**
	 * méthode qui teste si un parametre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null
				|| (multi != null && multi.getParameter(param) != null));
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

	public String getNOM_ST_COMMENTAIRE_ANCIEN_EAE(int i) {
		return "NOM_ST_COMMENTAIRE_ANCIEN_EAE" + i;
	}

	public String getVAL_ST_COMMENTAIRE_ANCIEN_EAE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE_ANCIEN_EAE(i));
	}

	public String getNOM_ST_DOCUMENT_ANCIEN_EAE(int i) {
		return "NOM_ST_DOCUMENT_ANCIEN_EAE" + i;
	}

	public String getVAL_ST_DOCUMENT_ANCIEN_EAE(int i) {
		return getZone(getNOM_ST_DOCUMENT_ANCIEN_EAE(i));
	}

	public String getNOM_PB_AJOUTER_ANCIEN_EAE() {
		return "NOM_PB_AJOUTER_ANCIEN_EAE";
	}

	public boolean performPB_AJOUTER_ANCIEN_EAE(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE_ANCIEN_EAE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ANNEE_ANCIEN_EAE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION_ANCIEN_EAE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<Document> getListeAncienEAE() {
		return listeAncienEAE;
	}

	public void setListeAncienEAE(ArrayList<Document> listeAncienEAE) {
		this.listeAncienEAE = listeAncienEAE;
	}

	public String getNOM_EF_COMMENTAIRE_ANCIEN_EAE() {
		return "NOM_EF_COMMENTAIRE_ANCIEN_EAE";
	}

	public String getVAL_EF_COMMENTAIRE_ANCIEN_EAE() {
		return getZone(getNOM_EF_COMMENTAIRE_ANCIEN_EAE());
	}

	public String getNOM_EF_ANNEE_ANCIEN_EAE() {
		return "NOM_EF_ANNEE_ANCIEN_EAE";
	}

	public String getVAL_EF_ANNEE_ANCIEN_EAE() {
		return getZone(getNOM_EF_ANNEE_ANCIEN_EAE());
	}

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE";
	}

	public boolean performPB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT_ANCIEN_EAE()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT_ANCIEN_EAE());
		}
		// Controle des champs
		if (!performControlerSaisieDocumentAncienEAE(request))
			return false;

		if (!creeDocumentAncienEAE(request)) {
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// on met a jour le tableau des documents
		initialiseListeAncienEae(request);
		return true;
	}

	public String getNOM_EF_LIENDOCUMENT_ANCIEN_EAE() {
		return "NOM_EF_LIENDOCUMENT_ANCIEN_EAE";
	}

	public String getVAL_EF_LIENDOCUMENT_ANCIEN_EAE() {
		return getZone(getNOM_EF_LIENDOCUMENT_ANCIEN_EAE());
	}

	private boolean performControlerSaisieDocumentAncienEAE(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT_ANCIEN_EAE(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE_ANCIEN_EAE(), multi.getParameter(getNOM_EF_COMMENTAIRE_ANCIEN_EAE()));
		addZone(getNOM_EF_ANNEE_ANCIEN_EAE(), multi.getParameter(getNOM_EF_ANNEE_ANCIEN_EAE()));

		boolean result = true;
		// annee
		if (getVAL_EF_ANNEE_ANCIEN_EAE().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			result &= false;
		} else {
			if (!Services.estNumerique(getVAL_EF_ANNEE_ANCIEN_EAE())) {
				// "ERR992", "La zone @ doit étre numérique.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année"));
				result &= false;
			} else {
				// on verifie qu'on n'ajoute pas de document apres 2012
				if (Integer.valueOf(getVAL_EF_ANNEE_ANCIEN_EAE()) >= 2013) {
					// "ERR166",
					// "Vous ne pouvez uploadé un document pour une année
					// supérieure a 2012.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR166"));
					result &= false;
				}
			}
		}

		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}

		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'), fichierUpload.getName().length());
		if (!extension.toLowerCase().equals(".pdf")) {
			// "ERR165", "Le fichier doit étre au format PDF."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR165"));
			result &= false;
		}
		return result;
	}

	private boolean creeDocumentAncienEAE(HttpServletRequest request) throws Exception {
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on verifie si il y a deja un document pour cet année
		boolean existDeja = false;
		for (int i = 0; i < getListeAncienEAE().size(); i++) {
			Document d = getListeAncienEAE().get(i);
			if (d.getNomDocument().contains(getVAL_EF_ANNEE_ANCIEN_EAE())) {
				existDeja = true;
				break;
			}
		}
		if (existDeja) {
			// "ERR167",
			// "Un fichier existe déjà pour cette année. Veuillez choisir une
			// autre année.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR167"));
			return false;
		}

		String codTypeDoc = CmisUtils.CODE_TYPE_EAE;
		// on crée le document en base de données
		TypeDocument typeEAE = getTypeDocumentDao().chercherTypeDocumentByCod(codTypeDoc);
		Document doc = new Document();
		doc.setIdTypeDocument(typeEAE.getIdTypeDocument());
		doc.setNomOriginal(fichierUpload.getName());
		doc.setDateDocument(new Date());
		doc.setCommentaire(getZone(getNOM_EF_COMMENTAIRE_ANCIEN_EAE()));

		// on upload le fichier
		ReturnMessageDto rmd = alfrescoCMISService.uploadDocument(getAgentConnecte(request).getIdAgent(), getAgentCourant(), doc, fichierUpload,
				new Integer(getVAL_EF_ANNEE_ANCIEN_EAE()), codTypeDoc);

		if (declarerErreurFromReturnMessageDto(rmd))
			return false;

		// on crée le document en base de données
		Integer id = getDocumentDao().creerDocument(doc.getClasseDocument(), doc.getNomDocument(), doc.getLienDocument(), doc.getDateDocument(),
				doc.getCommentaire(), doc.getIdTypeDocument(), doc.getNomOriginal(), doc.getNodeRefAlfresco(), doc.getCommentaireAlfresco(),
				doc.getReference());

		DocumentAgent lien = new DocumentAgent();
		lien.setIdAgent(getAgentCourant().getIdAgent());
		lien.setIdDocument(id);
		getLienDocumentAgentDao().creerDocumentAgent(lien.getIdAgent(), lien.getIdDocument());

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

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

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
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

	public TypeDocumentDao getTypeDocumentDao() {
		return typeDocumentDao;
	}

	public void setTypeDocumentDao(TypeDocumentDao typeDocumentDao) {
		this.typeDocumentDao = typeDocumentDao;
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

	public EaeFinalizationDto getLatestFinalisation(EaeDto eae) {

		EaeFinalizationDto latestFinalisation = null;

		for (EaeFinalizationDto finalisation : eae.getFinalisation()) {
			if (latestFinalisation == null || new Integer(finalisation.getVersionDocument()) > new Integer(latestFinalisation.getVersionDocument()))
				latestFinalisation = finalisation;
		}

		return latestFinalisation;
	}

	public boolean isCampagneOuverte(CampagneEaeDto campagne) throws Exception {
		if (campagne.getDateFin() == null) {
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

	public String getVAL_ST_EVALUATEUR_DATE_FONCTION(int i) {
		return getZone(getNOM_ST_EVALUATEUR_DATE_FONCTION(i));
	}

	public String getNOM_ST_EVALUATEUR_DATE_FONCTION(int i) {
		return "NOM_ST_EVALUATEUR_DATE_FONCTION" + i;
	}

	public String getVAL_ST_EVALUATEUR_DATE_SERVICE(int i) {
		return getZone(getNOM_ST_EVALUATEUR_DATE_SERVICE(i));
	}

	public String getNOM_ST_EVALUATEUR_DATE_SERVICE(int i) {
		return "NOM_ST_EVALUATEUR_DATE_SERVICE" + i;
	}

	public String getNOM_EF_DATE_FONCTIONNAIRE() {
		return "NOM_EF_DATE_FONCTIONNAIRE";
	}

	public String getVAL_EF_DATE_FONCTIONNAIRE() {
		return getZone(getNOM_EF_DATE_FONCTIONNAIRE());
	}

	public String getNOM_EF_DATE_ADMINISTRATION() {
		return "NOM_EF_DATE_ADMINISTRATION";
	}

	public String getVAL_EF_DATE_ADMINISTRATION() {
		return getZone(getNOM_EF_DATE_ADMINISTRATION());
	}

	public String getNOM_EF_DATE_FONCTION() {
		return "NOM_EF_DATE_FONCTION";
	}

	public String getVAL_EF_DATE_FONCTION() {
		return getZone(getNOM_EF_DATE_FONCTION());
	}

	public String getNOM_PB_VALIDER_DATE() {
		return "NOM_PB_VALIDER_DATE";
	}

	public boolean performPB_VALIDER_DATE(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_DATE)) {
			// Vérification de la validité du formulaire
			if (!performControlerChampsDate(request))
				return false;

			EaeDto eae = getEaeCourant();
			if (eae != null && eae.getIdEae() != null) {
				if (!performSauvegardeDate(request, eae)) {
					// "ERR164",
					// "Une erreur est survenue dans la sauvegarde de l'EAE.
					// Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
					return false;
				}

			} else {
				// "ERR164",
				// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci
				// de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
				return false;
			}

		}

		// "INF501", "L'EAE a été correctement sauvegardé."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF501"));
		return true;
	}

	private boolean performSauvegardeDate(HttpServletRequest request, EaeDto eae) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// #115005 on sauvegarde les dates
		BirtDto evalue = eae.getEvalue();
		evalue.setDateEntreeAdministration(sdf.parse(getVAL_EF_DATE_ADMINISTRATION()));
		evalue.setDateEntreeFonctionnaire(sdf.parse(getVAL_EF_DATE_FONCTIONNAIRE()));
		evalue.setDateEntreeFonction(sdf.parse(getVAL_EF_DATE_FONCTION()));

		// on sauvegarde les dates de l'evaluateur
		for (int j = 0; j < getListeEvaluateurEae().size(); j++) {
			BirtDto evaluateur = getListeEvaluateurEae().get(j);
			evaluateur.setDateEntreeFonction(sdf.parse(getVAL_ST_EVALUATEUR_DATE_FONCTION(j)));
			evaluateur.setDateEntreeService(sdf.parse(getVAL_ST_EVALUATEUR_DATE_SERVICE(j)));
			eaeService.saveDateEvaluateurFromSirh(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), evaluateur);
		}
		ReturnMessageDto result = eaeService.saveDateEvalueFromSirh(getAgentConnecte(request).getIdAgent(), eae.getIdEae(), evalue);

		if (!result.getErrors().isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean performControlerChampsDate(HttpServletRequest request) throws Exception {
		// format date de entrée fonctionnaire
		if (!Services.estUneDate(getVAL_EF_DATE_FONCTIONNAIRE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'entrée en tant que fonctionnaire"));
			return false;
		}
		// format date de entrée administration
		if (!Services.estUneDate(getVAL_EF_DATE_ADMINISTRATION())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'entrée dans l'administration"));
			return false;
		}
		// format date de entrée dans la fonction
		if (!Services.estUneDate(getVAL_EF_DATE_FONCTION())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'entrée dans la fonction"));
			return false;
		}
		for (int j = 0; j < getListeEvaluateurEae().size(); j++) {
			Agent agentEvaluateur = getAgentDao().chercherAgent(getListeEvaluateurEae().get(j).getIdAgent());
			// format date de entrée dans la fonction
			if (!Services.estUneDate(getVAL_ST_EVALUATEUR_DATE_FONCTION(j))) {
				// "ERR007",
				// "La date @ est incorrecte. Elle doit être au format date."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR007", "d'entrée dans la fonction de l'evaluateur " + agentEvaluateur.getNomAgent()));
				return false;
			}
			// format date de entrée dans la fonction
			if (!Services.estUneDate(getVAL_ST_EVALUATEUR_DATE_SERVICE(j))) {
				// "ERR007",
				// "La date @ est incorrecte. Elle doit être au format date."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR007", "d'entrée dans le service de l'evaluateur " + agentEvaluateur.getNomAgent()));
				return false;
			}
		}
		return true;
	}

	public String getNOM_ST_URL_DOC(int i) {
		return "NOM_ST_URL_DOC" + i;
	}

	public String getVAL_ST_URL_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC(i));
	}

	public String getNOM_PB_MODIFIER_DATE(int i) {
		return "NOM_PB_MODIFIER_DATE" + i;
	}

	public boolean performPB_MODIFIER_DATE(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'eae courant
		EaeDto eaeCourant = (EaeDto) getListeEae().get(indiceEltAModifier);
		setEaeCourant(eaeCourant);

		initialiseEae();

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION_DATE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_URL_DOC_LISTE_DOC(int i) {
		return "NOM_ST_URL_DOC_LISTE_DOC" + i;
	}

	public String getVAL_ST_URL_DOC_LISTE_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC_LISTE_DOC(i));
	}
}
