package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.eae.CampagneEAE;
import nc.mairie.metier.eae.EAE;
import nc.mairie.metier.eae.EaeCommentaire;
import nc.mairie.metier.eae.EaeDeveloppement;
import nc.mairie.metier.eae.EaeEvaluateur;
import nc.mairie.metier.eae.EaeEvaluation;
import nc.mairie.metier.eae.EaeEvalue;
import nc.mairie.metier.eae.EaeEvolution;
import nc.mairie.metier.eae.EaeFichePoste;
import nc.mairie.metier.eae.EaeFinalisation;
import nc.mairie.metier.eae.EaePlanAction;
import nc.mairie.metier.eae.EaeTypeDeveloppement;
import nc.mairie.metier.parametrage.TypeDocument;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.spring.dao.EaeDao;
import nc.mairie.spring.dao.SirhDao;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeCommentaireDao;
import nc.mairie.spring.dao.metier.EAE.EaeDeveloppementDao;
import nc.mairie.spring.dao.metier.EAE.EaeEAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvolutionDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFinalisationDao;
import nc.mairie.spring.dao.metier.EAE.EaeNumIncrementDocumentDao;
import nc.mairie.spring.dao.metier.EAE.EaePlanActionDao;
import nc.mairie.spring.dao.metier.EAE.EaeTypeDeveloppementDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDocumentDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

/**
 * Process OeAGENTDIPLOMEGestion Date de création : (11/02/03 14:20:31)
 * 
 */
public class OeAGENTEae extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OeAGENTEae.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	public String ACTION_MODIFICATION = "Modification d'un EAE.";
	public String ACTION_CONSULTATION = "Consultation d'un EAE.";
	public String ACTION_AJOUT_OBJ_INDI = "Ajout d'un objectif individuel.";
	public String ACTION_MODIFICATION_OBJ_INDI = "Modification d'un objectif individuel.";
	public String ACTION_SUPPRESSION_OBJ_INDI = "Suppression d'un objectif individuel.";
	public String ACTION_AJOUT_OBJ_PRO = "Ajout d'un objectif professionnel.";
	public String ACTION_MODIFICATION_OBJ_PRO = "Modification d'un objectif professionnel.";
	public String ACTION_SUPPRESSION_OBJ_PRO = "Suppression d'un objectif professionnel.";
	public String ACTION_AJOUT_DEV = "Ajout d'un développement.";
	public String ACTION_MODIFICATION_DEV = "Modification d'un développement.";
	public String ACTION_SUPPRESSION_DEV = "Suppression d'un développement.";
	public String ACTION_DOCUMENT = "Documents.";
	public String ACTION_DOCUMENT_CREATION = "Création d'un document.";
	public String ACTION_DOCUMENT_CREATION_ANCIEN_EAE = "Création d'un ancien EAE.";

	private String[] LB_BASE_HORAIRE;
	private ArrayList<Horaire> listeHoraire;
	private Hashtable<String, Horaire> hashHoraire;

	private String[] LB_TYPE_DEV;
	private ArrayList<EaeTypeDeveloppement> listeTypeDeveloppement;
	private Hashtable<String, EaeTypeDeveloppement> hashTypeDeveloppement;

	private AgentNW AgentCourant;
	private ArrayList<EAE> listeEae;
	private ArrayList<EaeEvaluateur> listeEvaluateurEae;
	private ArrayList<EaePlanAction> listeObjectifPro;
	private ArrayList<EaePlanAction> listeObjectifIndi;
	private ArrayList<EaeDeveloppement> listeDeveloppement;
	private EAE eaeCourant;
	private EaePlanAction objectifIndiCourant;
	private EaePlanAction objectifProCourant;
	private EaeDeveloppement developpementCourant;
	private ArrayList<EaeFinalisation> listeDocuments;

	private EaeEAEDao eaeDao;
	private CampagneEAEDao campagneEaeDao;
	private EaeEvaluateurDao eaeEvaluateurDao;
	private EaeFichePosteDao eaeFichePosteDao;
	private EaeEvalueDao eaeEvalueDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private EaeCommentaireDao eaeCommentaireDao;
	private EaeFinalisationDao eaeFinalisationDao;
	private EaePlanActionDao eaePlanActionDao;
	private EaeEvolutionDao eaeEvolutionDao;
	private EaeDeveloppementDao eaeDeveloppementDao;
	private EaeTypeDeveloppementDao eaeTypeDeveloppementDao;
	private EaeNumIncrementDocumentDao eaeNumIncrementDocumentDao;

	private String urlFichier;
	public boolean isImporting = false;
	public MultipartRequest multi = null;
	public File fichierUpload = null;

	public String focus = null;

	private ArrayList<Document> listeAncienEAE;

	private TypeDocumentDao typeDocumentDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		initialiseListeDeroulante();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
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
		ArrayList<Document> listeDocAgent = getDocumentDao().listerDocumentAgent(getLienDocumentAgentDao(),
				Integer.valueOf(getAgentCourant().getIdAgent()), Const.CHAINE_VIDE, "EAE");
		setListeAncienEAE(listeDocAgent);

		if (getListeAncienEAE() != null) {
			for (int i = 0; i < getListeAncienEAE().size(); i++) {
				Document doc = (Document) getListeAncienEAE().get(i);
				Integer id = doc.getIdDocument();

				addZone(getNOM_ST_COMMENTAIRE_ANCIEN_EAE(id), doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: doc.getCommentaire());
				addZone(getNOM_ST_DOCUMENT_ANCIEN_EAE(id), doc.getNomDocument());
				addZone(getNOM_ST_NOM_ORI_DOC(id), doc.getNomOriginal() == null ? "&nbsp;" : doc.getNomOriginal());

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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Recherche des EAE de l'agent
		ArrayList<EaeEvalue> listeEAEEvalue = getEaeEvalueDao().listerEaeEvalueSans2012(
				Integer.valueOf(getAgentCourant().getIdAgent()));

		ArrayList<EAE> listeEAE = new ArrayList<EAE>();

		int indiceEae = 0;
		if (listeEAEEvalue != null) {
			for (int i = 0; i < listeEAEEvalue.size(); i++) {
				EaeEvalue evalue = (EaeEvalue) listeEAEEvalue.get(i);
				EAE eae = getEaeDao().chercherEAE(evalue.getIdEae());
				listeEAE.add(eae);
				EaeFichePoste eaeFDP = null;
				try {
					eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(evalue.getIdEae(), true);
				} catch (Exception e) {
					// on ne fait rien
				}
				CampagneEAE camp = getCampagneEaeDao().chercherCampagneEAE(eae.getIdCampagneEae());
				ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(evalue.getIdEae());
				String evaluateur = Const.CHAINE_VIDE;
				for (int j = 0; j < listeEvaluateur.size(); j++) {
					EaeEvaluateur eval = listeEvaluateur.get(j);
					AgentNW agentEvaluateur = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
					evaluateur += agentEvaluateur.getNomAgent() + " " + agentEvaluateur.getPrenomAgent() + " ("
							+ agentEvaluateur.getNoMatricule() + ") <br/> ";
				}

				addZone(getNOM_ST_ANNEE(indiceEae), camp.getAnnee().toString());
				addZone(getNOM_ST_EVALUATEUR(indiceEae), evaluateur.equals(Const.CHAINE_VIDE) ? "&nbsp;" : evaluateur);
				addZone(getNOM_ST_DATE_ENTRETIEN(indiceEae),
						eae.getDateEntretien() == null ? "&nbsp;" : sdf.format(eae.getDateEntretien()));
				addZone(getNOM_ST_SERVICE(indiceEae), eaeFDP == null ? "&nbsp;"
						: eaeFDP.getService() == null ? "&nbsp;" : eaeFDP.getService());
				addZone(getNOM_ST_STATUT(indiceEae), EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()));

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
			ArrayList<EaeTypeDeveloppement> liste = getEaeTypeDeveloppementDao().listerEaeTypeDeveloppement();
			setListeTypeDeveloppement(liste);

			int[] tailles = { 30 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<EaeTypeDeveloppement> list = liste.listIterator(); list.hasNext();) {
				EaeTypeDeveloppement typeDev = (EaeTypeDeveloppement) list.next();
				String ligne[] = { typeDev.getLibelleTypeDeveloppement() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_DEV(aFormat.getListeFormatee());

			for (EaeTypeDeveloppement h : liste)
				getHashTypeDeveloppement().put(h.getLibelleTypeDeveloppement(), h);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getEaeDao() == null) {
			setEaeDao(new EaeEAEDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getCampagneEaeDao() == null) {
			setCampagneEaeDao(new CampagneEAEDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeEvaluateurDao() == null) {
			setEaeEvaluateurDao(new EaeEvaluateurDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeFichePosteDao() == null) {
			setEaeFichePosteDao(new EaeFichePosteDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeEvalueDao() == null) {
			setEaeEvalueDao(new EaeEvalueDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao(new EaeEvaluationDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeCommentaireDao() == null) {
			setEaeCommentaireDao(new EaeCommentaireDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeFinalisationDao() == null) {
			setEaeFinalisationDao(new EaeFinalisationDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaePlanActionDao() == null) {
			setEaePlanActionDao(new EaePlanActionDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeEvolutionDao() == null) {
			setEaeEvolutionDao(new EaeEvolutionDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeDeveloppementDao() == null) {
			setEaeDeveloppementDao(new EaeDeveloppementDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeTypeDeveloppementDao() == null) {
			setEaeTypeDeveloppementDao(new EaeTypeDeveloppementDao((EaeDao) context.getBean("eaeDao")));
		}
		if (getEaeNumIncrementDocumentDao() == null) {
			setEaeNumIncrementDocumentDao((EaeNumIncrementDocumentDao) context.getBean("eaeNumIncrementDocumentDao"));
		}
		if (getTypeDocumentDao() == null) {
			setTypeDocumentDao(new TypeDocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * @return Agent
	 */
	public AgentNW getAgentCourant() {
		return AgentCourant;
	}

	/**
	 * @param newAgentCourant
	 *            Agent
	 */
	private void setAgentCourant(AgentNW newAgentCourant) {
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
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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
			}
			// Si clic sur le bouton PB_VISUALISER_DOC
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISER_DOC(i))) {
					return performPB_VISUALISER_DOC(request, i);
				}
			}
			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeEae().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
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

			// Si clic sur le bouton PB_MODIFIER_OBJ_INDI
			for (int i = 0; i < getListeObjectifIndi().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_OBJ_INDI(i))) {
					return performPB_MODIFIER_OBJ_INDI(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_OBJ_INDI
			for (int i = 0; i < getListeObjectifIndi().size(); i++) {
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
			}

			// Si clic sur le bouton PB_SUPPRIMER_OBJ_PRO
			for (int i = 0; i < getListeObjectifPro().size(); i++) {
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
			for (int i = 0; i < getListeObjectifIndi().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER_DEV(i))) {
					return performPB_MODIFIER_DEV(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DEV
			for (int i = 0; i < getListeObjectifIndi().size(); i++) {
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

			// Si clic sur le bouton PB_CONSULTER_DOC
			for (int i = 0; i < getListeDocuments().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER_DOC(i))) {
					return performPB_CONSULTER_DOC(request, i);
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

			// Si clic sur le bouton PB_CONSULTER_ANCIEN_EAE
			for (int i = 0; i < getListeAncienEAE().size(); i++) {
				Document d = getListeAncienEAE().get(i);
				Integer id = d.getIdDocument();
				if (testerParametre(request, getNOM_PB_CONSULTER_ANCIEN_EAE(id))) {
					return performPB_CONSULTER_ANCIEN_EAE(request, id);
				}
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
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEE Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EVALUATEUR
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltAConsulter);
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
		EAE eae = getEaeCourant();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Alim zone Informations
		addZone(getNOM_ST_DATE_ENTRETIEN(),
				eae.getDateEntretien() == null ? "non renseigné" : sdf.format(eae.getDateEntretien()));
		ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(eae.getIdEae());
		setListeEvaluateurEae(listeEvaluateur);
		for (int j = 0; j < listeEvaluateur.size(); j++) {
			EaeEvaluateur eval = listeEvaluateur.get(j);
			AgentNW agentEvaluateur = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
			String evaluateur = agentEvaluateur.getNomAgent() + " " + agentEvaluateur.getPrenomAgent() + " ("
					+ agentEvaluateur.getNoMatricule() + ") ";

			addZone(getNOM_ST_EVALUATEUR_NOM(j), evaluateur.equals(Const.CHAINE_VIDE) ? "non renseigné" : evaluateur);
			addZone(getNOM_ST_EVALUATEUR_FONCTION(j), eval.getFonction().equals(Const.CHAINE_VIDE) ? "non renseigné"
					: eval.getFonction());
		}
		EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEae(), true);
		String direction = eaeFDP.getDirectionService() == null ? Const.CHAINE_VIDE : eaeFDP.getDirectionService();
		String serv = eaeFDP.getService() == null ? Const.CHAINE_VIDE : eaeFDP.getService();
		addZone(getNOM_ST_SERVICE(), direction.equals(Const.CHAINE_VIDE) ? serv.equals(Const.CHAINE_VIDE) ? "&nbsp;"
				: serv : direction + " / " + serv);

		// Alim zone evaluation
		EaeEvaluation evaluation = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEae());
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
			if (evaluation.getIdEaeComEvaluateur() != null) {
				EaeCommentaire commEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(
						evaluation.getIdEaeComEvaluateur());
				addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(),
						commEvaluateur == null ? Const.CHAINE_VIDE : commEvaluateur.getText());
			} else {
				addZone(getNOM_ST_COMMENTAIRE_EVALUATEUR(), Const.CHAINE_VIDE);
			}
			// commentaire de l'evaluateur sur le rapport circonstancié
			if (evaluation.getIdEaeComAvctEvaluateur() != null) {
				EaeCommentaire commAvctEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(
						evaluation.getIdEaeComAvctEvaluateur());
				addZone(getNOM_ST_RAPPORT_CIRCON(),
						commAvctEvaluateur == null ? Const.CHAINE_VIDE : commAvctEvaluateur.getText());
			} else {
				addZone(getNOM_ST_RAPPORT_CIRCON(), Const.CHAINE_VIDE);
			}
			addZone(getNOM_ST_NIVEAU(), evaluation.getNiveau() == null ? "non renseigné" : evaluation.getNiveau());
			// pour la modif
			if (evaluation.getNiveau() == null) {
				addZone(getNOM_RG_NIVEAU(), getNOM_RB_NIVEAU_SATIS());
			} else {
				String niveau = evaluation.getNiveau();
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

			addZone(getNOM_ST_NOTE(), evaluation.getNoteAnnee() == null ? "non renseigné" : evaluation.getNoteAnnee()
					.toString());
			addZone(getNOM_ST_AVIS_SHD(), evaluation.getAvisShd() == null ? "non renseigné " : evaluation.getAvisShd());
			// pour la modif
			if (evaluation.getAvisShd() == null) {
				addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MOY());
			} else {
				if (evaluation.getAvisShd().equals("Durée minimale")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MIN());
				} else if (evaluation.getAvisShd().equals("Durée maximale")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MAX());
				} else if (evaluation.getAvisShd().equals("Durée moyenne")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_MOY());
				} else if (evaluation.getAvisShd().equals("Favorable")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_FAV());
				} else if (evaluation.getAvisShd().equals("Défavorable")) {
					addZone(getNOM_RG_SHD(), getNOM_RB_SHD_DEFAV());
				}
			}

			addZone(getNOM_ST_AVCT_DIFF(),
					evaluation.getPropositionAvancement() == null ? "non renseigné" : evaluation
							.getPropositionAvancement());
			// pour la modif
			if (evaluation.getPropositionAvancement() == null) {
				addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
			} else {
				if (evaluation.getPropositionAvancement().equals("MINI")) {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MIN());
				} else if (evaluation.getPropositionAvancement().equals("MAXI")) {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MAX());
				} else {
					addZone(getNOM_RG_AD(), getNOM_RB_AD_MOY());
				}
			}
			addZone(getNOM_ST_CHANGEMENT_CLASSE(), evaluation.getAvisChangementClasse() == null ? "non renseigné"
					: evaluation.getAvisChangementClasse() == 1 ? "favorable" : "défavorable");
			// pour la modif
			if (evaluation.getAvisChangementClasse() == null) {
				addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
			} else {
				if (evaluation.getAvisChangementClasse() == 0) {
					addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_DEF());
				} else {
					addZone(getNOM_RG_CHGT(), getNOM_RB_CHGT_FAV());
				}
			}
			addZone(getNOM_ST_AVIS_REVALO(),
					evaluation.getAvisRevalorisation() == null ? "non renseigné"
							: evaluation.getAvisRevalorisation() == 1 ? "favorable" : "défavorable");
			// pour la modif
			if (evaluation.getAvisRevalorisation() == null) {
				addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
			} else {
				if (evaluation.getAvisRevalorisation() == 0) {
					addZone(getNOM_RG_REVA(), getNOM_RB_REVA_DEF());
				} else {
					addZone(getNOM_RG_REVA(), getNOM_RB_REVA_FAV());
				}
			}
		}

		// alim zone plan action
		ArrayList<EaePlanAction> listeObjectifPro = getEaePlanActionDao().listerPlanActionParType(eae.getIdEae(), 1);
		setListeObjectifPro(listeObjectifPro);
		for (int j = 0; j < listeObjectifPro.size(); j++) {
			EaePlanAction plan = listeObjectifPro.get(j);

			addZone(getNOM_ST_LIB_OBJ_PRO(j), plan.getObjectif());
			addZone(getNOM_ST_LIB_MESURE_PRO(j), plan.getMesure());
		}

		ArrayList<EaePlanAction> listeObjectifIndi = getEaePlanActionDao().listerPlanActionParType(eae.getIdEae(), 2);
		setListeObjectifIndi(listeObjectifIndi);
		for (int j = 0; j < listeObjectifIndi.size(); j++) {
			EaePlanAction plan = listeObjectifIndi.get(j);

			addZone(getNOM_ST_LIB_OBJ_INDI(j), plan.getObjectif());
		}

		// Alim zone Evolution
		try {
			EaeEvolution evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEae());
			if (evolution.getIdEaeComEvolution() != null) {
				EaeCommentaire commEvolution = getEaeCommentaireDao().chercherEaeCommentaire(
						evolution.getIdEaeComEvolution());
				addZone(getNOM_ST_COM_EVOLUTION(), commEvolution == null ? Const.CHAINE_VIDE : commEvolution.getText());
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
			addZone(getNOM_ST_MOB_FONCT(), evolution.isMobiliteFonct() ? "oui" : "non");
			// pour la modif
			if (!evolution.isMobiliteFonct()) {
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
			addZone(getNOM_ST_DELAI(),
					evolution.getDelaiEnvisage() == null ? "non renseigné" : evolution.getDelaiEnvisage());
			// pour la modif
			if (evolution.getDelaiEnvisage() == null) {
				addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_4());
			} else {
				if (evolution.getDelaiEnvisage().equals("ENTRE1ET2ANS")) {
					addZone(getNOM_RG_DELAI(), getNOM_RB_DELAI_2());
				} else if (evolution.getDelaiEnvisage().equals("MOINS1AN")) {
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
			addZone(getNOM_ST_NOM_COLL(),
					evolution.getNomCollectivite() == null ? Const.CHAINE_VIDE : evolution.getNomCollectivite());
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
			addZone(getNOM_ST_NOM_CONCOURS(),
					evolution.getNomConcours() == null ? Const.CHAINE_VIDE : evolution.getNomConcours());
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
			if (evolution.getTempsPartielIdSpbhor() != null) {
				Horaire tempsPart = (Horaire) getHashHoraire().get(evolution.getTempsPartielIdSpbhor().toString());
				if (tempsPart != null) {
					Float taux = Float.parseFloat(tempsPart.getCdTaux()) * 100;
					int ligneHoraire = getListeHoraire().indexOf(tempsPart);
					addZone(getNOM_LB_BASE_HORAIRE_SELECT(), String.valueOf(ligneHoraire + 1));
					addZone(getNOM_ST_POURC_TPS_PARTIEL(),
							tempsPart == null || tempsPart.getCdtHor() == null ? "non renseigné" : tempsPart
									.getLibHor() + " - " + String.valueOf(taux.intValue()) + "%");
				} else {
					addZone(getNOM_LB_BASE_HORAIRE_SELECT(), Const.ZERO);
					addZone(getNOM_ST_POURC_TPS_PARTIEL(), "non renseigné");
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
			addZone(getNOM_ST_DATE_RETRAITE(),
					evolution.getDateRetraite() == null ? Const.CHAINE_VIDE : sdf.format(evolution.getDateRetraite()));
			addZone(getNOM_ST_AUTRE_PERSP(), evolution.isAutrePerspective() ? "oui" : "non");
			// pour la modif
			if (!evolution.isAutrePerspective()) {
				addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_NON());
			} else {
				addZone(getNOM_RG_AUTRE_PERSP(), getNOM_RB_AUTRE_PERSP_OUI());
			}
			addZone(getNOM_ST_LIB_AUTRE_PERSP(), evolution.getLibAutrePerspective() == null ? Const.CHAINE_VIDE
					: evolution.getLibAutrePerspective());

			// Alim zones developpement
			ArrayList<EaeDeveloppement> listeDeveloppement = getEaeDeveloppementDao()
					.listerEaeDeveloppementParEvolution(evolution.getIdEaeEvolution());
			setListeDeveloppement(listeDeveloppement);
			for (int j = 0; j < listeDeveloppement.size(); j++) {
				EaeDeveloppement dev = listeDeveloppement.get(j);
				addZone(getNOM_ST_TYPE_DEV(j), dev.getTypeDeveloppement());
				addZone(getNOM_ST_LIB_DEV(j), dev.getLibelle());
				addZone(getNOM_ST_ECHEANCE_DEV(j), dev.getEcheance() == null ? Const.CHAINE_VIDE
						: new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheance()));
				addZone(getNOM_ST_PRIORISATION_DEV(j), dev.getPriorisation() == null ? Const.CHAINE_VIDE : dev
						.getPriorisation().toString());
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltAModifier);
		setEaeCourant(eaeCourant);

		initialiseEae();
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<EAE> getListeEae() {
		if (listeEae == null)
			return new ArrayList<EAE>();
		return listeEae;
	}

	public void setListeEae(ArrayList<EAE> listeEae) {
		this.listeEae = listeEae;
	}

	public EAE getEaeCourant() {
		return eaeCourant;
	}

	public void setEaeCourant(EAE eaeCourant) {
		this.eaeCourant = eaeCourant;
	}

	public EaeEAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EaeEAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public CampagneEAEDao getCampagneEaeDao() {
		return campagneEaeDao;
	}

	public void setCampagneEaeDao(CampagneEAEDao campagneEaeDao) {
		this.campagneEaeDao = campagneEaeDao;
	}

	public EaeEvaluateurDao getEaeEvaluateurDao() {
		return eaeEvaluateurDao;
	}

	public void setEaeEvaluateurDao(EaeEvaluateurDao eaeEvaluateurDao) {
		this.eaeEvaluateurDao = eaeEvaluateurDao;
	}

	public EaeFichePosteDao getEaeFichePosteDao() {
		return eaeFichePosteDao;
	}

	public void setEaeFichePosteDao(EaeFichePosteDao eaeFichePosteDao) {
		this.eaeFichePosteDao = eaeFichePosteDao;
	}

	public EaeEvalueDao getEaeEvalueDao() {
		return eaeEvalueDao;
	}

	public void setEaeEvalueDao(EaeEvalueDao eaeEvalueDao) {
		this.eaeEvalueDao = eaeEvalueDao;
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ENTRETIEN
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EVALUATEUR_NOM
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_EVALUATEUR_FONCTION Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_EVALUATEUR_FONCTION(int i) {
		return getZone(getNOM_ST_EVALUATEUR_FONCTION(i));
	}

	public ArrayList<EaeEvaluateur> getListeEvaluateurEae() {
		return listeEvaluateurEae == null ? new ArrayList<EaeEvaluateur>() : listeEvaluateurEae;
	}

	public void setListeEvaluateurEae(ArrayList<EaeEvaluateur> listeEvaluateurEae) {
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NIVEAU Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_NIVEAU() {
		return getZone(getNOM_ST_NIVEAU());
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOTE Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_COMMENTAIRE_EVALUATEUR Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE_EVALUATEUR() {
		return getZone(getNOM_ST_COMMENTAIRE_EVALUATEUR());
	}

	public EaeCommentaireDao getEaeCommentaireDao() {
		return eaeCommentaireDao;
	}

	public void setEaeCommentaireDao(EaeCommentaireDao eaeCommentaireDao) {
		this.eaeCommentaireDao = eaeCommentaireDao;
	}

	public String getNOM_PB_VISUALISER_DOC(int i) {
		return "NOM_PB_VISUALISER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VISUALISER_DOC(HttpServletRequest request, int indiceEltAVisualiser) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("URL_SHAREPOINT_GED");

		// Récup de l'EAE courant
		EAE eae = (EAE) getListeEae().get(indiceEltAVisualiser);
		String finalisation = getEaeFinalisationDao().chercherDernierDocumentFinalise(eae.getIdEae());
		// on affiche le document
		setURLFichier(getScriptOuverture(repertoireStockage + finalisation));

		return true;
	}

	public EaeFinalisationDao getEaeFinalisationDao() {
		return eaeFinalisationDao;
	}

	public void setEaeFinalisationDao(EaeFinalisationDao eaeFinalisationDao) {
		this.eaeFinalisationDao = eaeFinalisationDao;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS_SHD Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVCT_DIFF Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS_REVALO
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RAPPORT_CIRCON
	 * Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_RAPPORT_CIRCON() {
		return getZone(getNOM_ST_RAPPORT_CIRCON());
	}

	public EaePlanActionDao getEaePlanActionDao() {
		return eaePlanActionDao;
	}

	public void setEaePlanActionDao(EaePlanActionDao eaePlanActionDao) {
		this.eaePlanActionDao = eaePlanActionDao;
	}

	public ArrayList<EaePlanAction> getListeObjectifPro() {
		return listeObjectifPro == null ? new ArrayList<EaePlanAction>() : listeObjectifPro;
	}

	public void setListeObjectifPro(ArrayList<EaePlanAction> listeObjectifPro) {
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_OBJ_PRO
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_MESURE_PRO
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_MESURE_PRO(int i) {
		return getZone(getNOM_ST_LIB_MESURE_PRO(i));
	}

	public ArrayList<EaePlanAction> getListeObjectifIndi() {
		return listeObjectifIndi == null ? new ArrayList<EaePlanAction>() : listeObjectifIndi;
	}

	public void setListeObjectifIndi(ArrayList<EaePlanAction> listeObjectifIndi) {
		this.listeObjectifIndi = listeObjectifIndi;
	}

	public ArrayList<EaeDeveloppement> getListeDeveloppement() {
		return listeDeveloppement == null ? new ArrayList<EaeDeveloppement>() : listeDeveloppement;
	}

	public void setListeDeveloppement(ArrayList<EaeDeveloppement> listeDeveloppement) {
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_OBJ_INDI
	 * Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_LIB_OBJ_INDI(int i) {
		return getZone(getNOM_ST_LIB_OBJ_INDI(i));
	}

	public EaeEvolutionDao getEaeEvolutionDao() {
		return eaeEvolutionDao;
	}

	public void setEaeEvolutionDao(EaeEvolutionDao eaeEvolutionDao) {
		this.eaeEvolutionDao = eaeEvolutionDao;
	}

	public EaeDeveloppementDao getEaeDeveloppementDao() {
		return eaeDeveloppementDao;
	}

	public void setEaeDeveloppementDao(EaeDeveloppementDao eaeDeveloppementDao) {
		this.eaeDeveloppementDao = eaeDeveloppementDao;
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_GEO Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_FONCT Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DELAI Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_SERV Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_DIR Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_COLL Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_COLL Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOB_AUTRE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CONCOURS Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_CONCOURS
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_VAE Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_VAE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TPS_PARTIEL
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RETRAITE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_RETRAITE
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AUTRE_PERSP
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COM_EVOLUTION
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_DEV Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_DEV Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ECHEANCE_DEV
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_PRIORISATION_DEV Date de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_PRIORISATION_DEV(int i) {
		return getZone(getNOM_ST_PRIORISATION_DEV(i));
	}

	public boolean isCampagneOuverte(Integer idCampagneEAE) throws Exception {
		return getCampagneEaeDao().chercherCampagneEAE(idCampagneEAE).estOuverte();
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
			// vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			EAE eae = getEaeCourant();
			if (eae != null && eae.getIdEae() != null) {
				performSauvegardeEvaluation(request, eae);
				if (!performSauvegardeEvolution(request, eae)) {
					// "ERR164",
					// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
					return false;
				}

			} else {
				// "ERR164",
				// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
				return false;
			}

		}

		// "INF501", "L'EAE a été correctement sauvegardé."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF501"));
		return true;
	}

	private boolean performSauvegardeEvolution(HttpServletRequest request, EAE eae) throws Exception {
		/************* PARTIE EVOLUTION **********************/
		EaeEvolution evolution = null;
		try {
			evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEae());
		} catch (Exception e) {
			evolution = new EaeEvolution();
			evolution.setIdEae(eae.getIdEae());
			getEaeEvolutionDao().creerEaeEvolution(evolution);
			evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEae());
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
			evolution.setMobiliteFonct(false);
		} else {
			evolution.setMobiliteFonct(true);
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
		getEaeEvolutionDao().modifierMobiliteEaeEvolution(evolution.getIdEaeEvolution(), evolution.isMobiliteGeo(),
				evolution.isMobiliteFonct(), evolution.isMobiliteService(), evolution.isMobiliteDirection(),
				evolution.isMobiliteCollectivite(), evolution.isMobiliteAutre());

		// Changement de metier
		String metier = getVAL_RG_METIER();
		if (metier.equals(getNOM_RB_METIER_NON())) {
			evolution.setChangementMetier(false);
		} else {
			evolution.setChangementMetier(true);
		}
		getEaeEvolutionDao().modifierChangementMetierEaeEvolution(evolution.getIdEaeEvolution(),
				evolution.isChangementMetier());

		// Delai
		String delai = getVAL_RG_DELAI();
		if (delai.equals(getNOM_RB_DELAI_1())) {
			evolution.setDelaiEnvisage("MOINS1AN");
		} else if (delai.equals(getNOM_RB_DELAI_2())) {
			evolution.setDelaiEnvisage("ENTRE1ET2ANS");
		} else {
			evolution.setDelaiEnvisage("ENTRE2ET4ANS");
		}
		getEaeEvolutionDao().modifierDelaiEaeEvolution(evolution.getIdEaeEvolution(), evolution.getDelaiEnvisage());

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
		getEaeEvolutionDao().modifierAutresInfosEaeEvolution(evolution.getIdEaeEvolution(), evolution.isConcours(),
				evolution.isVae(), evolution.isTempsPartiel(), evolution.isRetraite(), evolution.isAutrePerspective());

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
		String nomAutrePersp = getVAL_ST_LIB_AUTRE_PERSP().equals(Const.CHAINE_VIDE) ? null
				: getVAL_ST_LIB_AUTRE_PERSP();
		evolution.setLibAutrePerspective(nomAutrePersp);
		getEaeEvolutionDao().modifierLibelleEaeEvolution(evolution.getIdEaeEvolution(), evolution.getNomCollectivite(),
				evolution.getNomConcours(), evolution.getNomVae(), evolution.getLibAutrePerspective());

		// date de la retraite
		String dateRetraire = getVAL_ST_DATE_RETRAITE().equals(Const.CHAINE_VIDE) ? null : Services
				.formateDate(getVAL_ST_DATE_RETRAITE());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		evolution.setDateRetraite(dateRetraire != null ? sdf.parse(dateRetraire) : null);
		getEaeEvolutionDao().modifierDateRetraiteEaeEvolution(evolution.getIdEaeEvolution(),
				evolution.getDateRetraite());

		// commentaire de l'evolution
		if (evolution.getIdEaeComEvolution() != null && evolution.getIdEaeComEvolution() != 0) {
			EaeCommentaire commEvolution = getEaeCommentaireDao().chercherEaeCommentaire(
					evolution.getIdEaeComEvolution());
			commEvolution.setText(getVAL_ST_COM_EVOLUTION());
			getEaeCommentaireDao().modifierEaeCommentaire(commEvolution.getIdEaeCommentaire(), commEvolution.getText());
		} else {
			if (!getVAL_ST_COM_EVOLUTION().equals(Const.CHAINE_VIDE)) {
				EaeCommentaire comm = new EaeCommentaire();
				comm.setText(getVAL_ST_COM_EVOLUTION());
				Integer idCree = getEaeCommentaireDao().creerEaeCommentaire(comm.getText());
				getEaeEvolutionDao().modifierCommentaireEaeEvaluation(evolution.getIdEaeEvolution(), idCree);
			}
		}

		// pourcentage temps partiel
		int numLigneBH = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) : -1);
		Horaire horaire = numLigneBH > 0 ? (Horaire) getListeHoraire().get(numLigneBH - 1) : null;
		evolution.setTempsPartielIdSpbhor(horaire == null ? null : Integer.valueOf(horaire.getCdtHor()));
		getEaeEvolutionDao().modifierPourcTpsPartielEaeEvolution(evolution.getIdEaeEvolution(),
				evolution.getTempsPartielIdSpbhor());
		return true;

	}

	private void performSauvegardeEvaluation(HttpServletRequest request, EAE eae) throws Exception {
		/************* PARTIE EVALUATION **********************/
		EaeEvaluation eval = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEae());
		// commentaire de l'evaluateur
		if (eval.getIdEaeComEvaluateur() != null && eval.getIdEaeComEvaluateur() != 0) {
			EaeCommentaire commEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(eval.getIdEaeComEvaluateur());
			commEvaluateur.setText(getVAL_ST_COMMENTAIRE_EVALUATEUR());
			getEaeCommentaireDao().modifierEaeCommentaire(commEvaluateur.getIdEaeCommentaire(),
					commEvaluateur.getText());
		} else {
			if (!getVAL_ST_COMMENTAIRE_EVALUATEUR().equals(Const.CHAINE_VIDE)) {
				EaeCommentaire comm = new EaeCommentaire();
				comm.setText(getVAL_ST_COMMENTAIRE_EVALUATEUR());
				Integer idCree = getEaeCommentaireDao().creerEaeCommentaire(comm.getText());
				getEaeEvaluationDao().modifierCommentaireEvaluateurEaeEvaluation(eval.getIdEaeEvaluation(), idCree);
			}
		}

		// Niveau
		String niveau = getVAL_RG_NIVEAU();
		if (niveau.equals(getNOM_RB_NIVEAU_EXCEL())) {
			eval.setNiveau("EXCELLENT");
		} else if (niveau.equals(getNOM_RB_NIVEAU_SATIS())) {
			eval.setNiveau("SATISFAISANT");
		} else if (niveau.equals(getNOM_RB_NIVEAU_PROGR())) {
			eval.setNiveau("NECESSITANT_DES_PROGRES");
		} else {
			eval.setNiveau("INSUFFISANT");
		}
		getEaeEvaluationDao().modifierNiveauEaeEvaluation(eval.getIdEaeEvaluation(), eval.getNiveau());

		// note
		Float note = Float.parseFloat(getVAL_ST_NOTE().replace(',', '.'));
		eval.setNoteAnnee(note.doubleValue());
		getEaeEvaluationDao().modifierNoteEaeEvaluation(eval.getIdEaeEvaluation(), eval.getNoteAnnee());

		// Avis SHD
		String shd = getVAL_RG_SHD();
		if (shd.equals(getNOM_RB_SHD_MIN())) {
			eval.setAvisShd("Durée minimale");
		} else if (shd.equals(getNOM_RB_SHD_MAX())) {
			eval.setAvisShd("Durée maximale");
		} else if (shd.equals(getNOM_RB_SHD_MOY())) {
			eval.setAvisShd("Durée moyenne");
		} else if (shd.equals(getNOM_RB_SHD_FAV())) {
			eval.setAvisShd("Favorable");
		} else if (shd.equals(getNOM_RB_SHD_DEFAV())) {
			eval.setAvisShd("Défavorable");
		}
		getEaeEvaluationDao().modifierAvisSHDEaeEvaluation(eval.getIdEaeEvaluation(), eval.getAvisShd());

		// Avancement Diff
		String ad = getVAL_RG_AD();
		if (ad.equals(getNOM_RB_AD_MIN())) {
			eval.setPropositionAvancement("MINI");
		} else if (ad.equals(getNOM_RB_AD_MAX())) {
			eval.setPropositionAvancement("MAXI");
		} else {
			eval.setPropositionAvancement("MOY");
		}
		getEaeEvaluationDao().modifierADEaeEvaluation(eval.getIdEaeEvaluation(), eval.getPropositionAvancement());

		// Changement classe
		String chgt = getVAL_RG_CHGT();
		if (chgt.equals(getNOM_RB_CHGT_DEF())) {
			eval.setAvisChangementClasse(0);
		} else {
			eval.setAvisChangementClasse(1);
		}
		getEaeEvaluationDao()
				.modifierChgtClasseEaeEvaluation(eval.getIdEaeEvaluation(), eval.getAvisChangementClasse());

		// Revalorisation
		String reva = getVAL_RG_REVA();
		if (reva.equals(getNOM_RB_REVA_DEF())) {
			eval.setAvisRevalorisation(0);
		} else {
			eval.setAvisRevalorisation(1);
		}
		getEaeEvaluationDao().modifierRevaloEaeEvaluation(eval.getIdEaeEvaluation(), eval.getAvisRevalorisation());

		// rapport circonstancié de l'evaluateur
		if (eval.getIdEaeComAvctEvaluateur() != null && eval.getIdEaeComAvctEvaluateur() != 0) {
			EaeCommentaire commAvctEvaluateur = getEaeCommentaireDao().chercherEaeCommentaire(
					eval.getIdEaeComAvctEvaluateur());
			commAvctEvaluateur.setText(getVAL_ST_RAPPORT_CIRCON());
			getEaeCommentaireDao().modifierEaeCommentaire(commAvctEvaluateur.getIdEaeCommentaire(),
					commAvctEvaluateur.getText());
		} else {
			if (!getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
				EaeCommentaire comm = new EaeCommentaire();
				comm.setText(getVAL_ST_RAPPORT_CIRCON());
				Integer idCree = getEaeCommentaireDao().creerEaeCommentaire(comm.getText());
				getEaeEvaluationDao().modifierRapportCirconstancieEaeEvaluation(eval.getIdEaeEvaluation(), idCree);
			}
		}

	}

	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// ********************************************
		// ///////////////////NOTE/////////////////////
		// ********************************************
		if (!Services.estFloat(getVAL_ST_NOTE())) {
			// "ERR992", "La zone @ doit être numérique."
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
			// "ERR160", "La note doit être comprise entre 0 et 20.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR160"));
			return false;

		}
		// ********************************************
		// /////////RAPPORT CIRCONSTANCIE//////////////
		// ********************************************
		// si min ou max alors rapport circonstancié obligatoire
		if ((getVAL_RG_AD().equals(getNOM_RB_AD_MIN()) || getVAL_RG_AD().equals(getNOM_RB_AD_MAX()))
				&& getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			// "ERR162",
			// "Le contenu du rapport circonstancié ne doit pas être vide pour une durée d'avancement minimale ou maximale.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR162"));
			return false;
		}
		if (getVAL_RG_AD().equals(getNOM_RB_AD_MOY()) && !getVAL_ST_RAPPORT_CIRCON().equals(Const.CHAINE_VIDE)) {
			// "ERR163",
			// "Le contenu du rapport circonstancié ne doit pas être rempli pour une durée d'avancement moyenne.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR163"));
			return false;
		}
		// ********************************************
		// /////////DATE RETRAITE//////////////
		// ********************************************
		// format date de retraite
		if (!getVAL_ST_DATE_RETRAITE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_RETRAITE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BASE_HORAIRE Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_BASE_HORAIRE() {
		return getLB_BASE_HORAIRE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_OBJ_INDI(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaePlanAction plan = getListeObjectifIndi().get(indiceEltAModifier);
		setObjectifIndiCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_INDI(), plan.getObjectif());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_OBJ_INDI(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaePlanAction plan = getListeObjectifIndi().get(indiceEltASuprimer);
		setObjectifIndiCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_INDI(), plan.getObjectif());

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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_OBJ_INDI
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_OBJ_INDI(HttpServletRequest request) throws Exception {
		EAE eae = getEaeCourant();
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_AJOUT_OBJ_INDI)) {
			if (!performControlerChampObjIndi(request)) {
				return false;
			}
			EaePlanAction planActionIndi = new EaePlanAction();
			planActionIndi.setIdEae(eae.getIdEae());
			planActionIndi.setObjectif(getVAL_ST_LIB_OBJ_INDI());
			planActionIndi.setMesure(null);
			planActionIndi.setIdEaeTypeObjectif(2);
			getEaePlanActionDao().creerPlanAction(planActionIndi.getIdEae(), planActionIndi.getIdEaeTypeObjectif(),
					planActionIndi.getObjectif(), planActionIndi.getMesure());
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_OBJ_INDI)) {
			if (!performControlerChampObjIndi(request)) {
				return false;
			}
			EaePlanAction planActionIndi = getObjectifIndiCourant();
			planActionIndi.setObjectif(getVAL_ST_LIB_OBJ_INDI());
			getEaePlanActionDao().modifierEaePlanAction(planActionIndi.getIdEaePlanAction(),
					planActionIndi.getIdEaeTypeObjectif(), planActionIndi.getObjectif(), planActionIndi.getMesure());
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION_OBJ_INDI)) {
			EaePlanAction planActionIndi = getObjectifIndiCourant();
			getEaePlanActionDao().supprimerEaePlanAction(planActionIndi.getIdEaePlanAction());
		}
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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

	public EaePlanAction getObjectifIndiCourant() {
		return objectifIndiCourant;
	}

	public void setObjectifIndiCourant(EaePlanAction objectifIndiCourant) {
		this.objectifIndiCourant = objectifIndiCourant;
	}

	public EaePlanAction getObjectifProCourant() {
		return objectifProCourant;
	}

	public void setObjectifProCourant(EaePlanAction objectifProCourant) {
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_OBJ_PRO(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaePlanAction plan = getListeObjectifPro().get(indiceEltAModifier);
		setObjectifProCourant(plan);
		addZone(getNOM_ST_LIB_OBJ_PRO(), plan.getObjectif());
		addZone(getNOM_ST_LIB_MESURE_PRO(), plan.getMesure());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_OBJ_PRO(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaePlanAction plan = getListeObjectifPro().get(indiceEltASuprimer);
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_OBJ_PRO
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_MESURE_PRO
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_OBJ_PRO(HttpServletRequest request) throws Exception {
		EAE eae = getEaeCourant();
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_AJOUT_OBJ_PRO)) {
			if (!performControlerChampObjPro(request)) {
				return false;
			}
			EaePlanAction planActionPro = new EaePlanAction();
			planActionPro.setIdEae(eae.getIdEae());
			planActionPro.setObjectif(getVAL_ST_LIB_OBJ_PRO());
			planActionPro.setMesure(getVAL_ST_LIB_MESURE_PRO());
			planActionPro.setIdEaeTypeObjectif(1);
			getEaePlanActionDao().creerPlanAction(planActionPro.getIdEae(), planActionPro.getIdEaeTypeObjectif(),
					planActionPro.getObjectif(), planActionPro.getMesure());
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_OBJ_PRO)) {
			if (!performControlerChampObjPro(request)) {
				return false;
			}
			EaePlanAction planActionPro = getObjectifProCourant();
			planActionPro.setObjectif(getVAL_ST_LIB_OBJ_PRO());
			planActionPro.setMesure(getVAL_ST_LIB_MESURE_PRO());
			getEaePlanActionDao().modifierEaePlanAction(planActionPro.getIdEaePlanAction(),
					planActionPro.getIdEaeTypeObjectif(), planActionPro.getObjectif(), planActionPro.getMesure());
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION_OBJ_PRO)) {
			EaePlanAction planActionPro = getObjectifProCourant();
			getEaePlanActionDao().supprimerEaePlanAction(planActionPro.getIdEaePlanAction());
		}
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER_DEV(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeDeveloppement dev = getListeDeveloppement().get(indiceEltAModifier);
		setDeveloppementCourant(dev);
		EaeTypeDeveloppement typeDev = (EaeTypeDeveloppement) getHashTypeDeveloppement().get(dev.getLibelle());
		if (typeDev != null) {
			int ligneTypeDev = getListeTypeDeveloppement().indexOf(typeDev);
			addZone(getNOM_LB_TYPE_DEV_SELECT(), String.valueOf(ligneTypeDev));
		}
		addZone(getNOM_ST_LIB_DEV(), dev.getLibelle());
		addZone(getNOM_ST_PRIO_DEV(), dev.getPriorisation().toString());
		addZone(getNOM_ST_DATE_DEV(), dev.getEcheance() == null ? Const.CHAINE_VIDE
				: new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheance()));

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DEV(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		EaeDeveloppement dev = getListeDeveloppement().get(indiceEltASuprimer);
		setDeveloppementCourant(dev);
		EaeTypeDeveloppement typeDev = (EaeTypeDeveloppement) getHashTypeDeveloppement().get(dev.getLibelle());
		if (typeDev != null) {
			int ligneTypeDev = getListeTypeDeveloppement().indexOf(typeDev);
			addZone(getNOM_LB_TYPE_DEV_SELECT(), String.valueOf(ligneTypeDev));
		}
		addZone(getNOM_ST_LIB_DEV(), dev.getLibelle());
		addZone(getNOM_ST_PRIO_DEV(), dev.getPriorisation().toString());
		addZone(getNOM_ST_DATE_DEV(), dev.getEcheance() == null ? Const.CHAINE_VIDE
				: new SimpleDateFormat("dd/MM/yyyy").format(dev.getEcheance()));

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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (11/02/03 14:20:31)
	 * 
	 */
	public boolean performPB_VALIDER_DEV(HttpServletRequest request) throws Exception {

		EAE eae = getEaeCourant();
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_AJOUT_DEV) && !getVAL_ST_LIB_DEV().equals(Const.CHAINE_VIDE)) {
			if (!performControlerChampDev(request)) {
				return false;
			}
			EaeEvolution evolution = getEaeEvolutionDao().chercherEaeEvolution(eae.getIdEae());
			EaeDeveloppement dev = new EaeDeveloppement();
			dev.setIdEaeEvolution(evolution.getIdEaeEvolution());
			dev.setLibelle(getVAL_ST_LIB_DEV());
			dev.setEcheance(new SimpleDateFormat("dd/MM/yyyy").parse(getVAL_ST_DATE_DEV()));
			dev.setPriorisation(Integer.valueOf(getVAL_ST_PRIO_DEV()));
			// type developpement
			int numLigneTypeDev = (Services.estNumerique(getZone(getNOM_LB_TYPE_DEV_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TYPE_DEV_SELECT())) : -1);
			EaeTypeDeveloppement typeDev = numLigneTypeDev > -1 ? (EaeTypeDeveloppement) getListeTypeDeveloppement()
					.get(numLigneTypeDev) : null;
			dev.setTypeDeveloppement(typeDev.getLibelleTypeDeveloppement());
			getEaeDeveloppementDao().creerEaeDeveloppement(dev.getIdEaeEvolution(), dev.getTypeDeveloppement(),
					dev.getLibelle(), dev.getEcheance(), dev.getPriorisation());
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION_DEV)) {
			if (!performControlerChampDev(request)) {
				return false;
			}
			EaeDeveloppement dev = getDeveloppementCourant();
			dev.setLibelle(getVAL_ST_LIB_DEV());
			dev.setEcheance(new SimpleDateFormat("dd/MM/yyyy").parse(getVAL_ST_DATE_DEV()));
			dev.setPriorisation(Integer.valueOf(getVAL_ST_PRIO_DEV()));
			// type developpement
			int numLigneTypeDev = (Services.estNumerique(getZone(getNOM_LB_TYPE_DEV_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_TYPE_DEV_SELECT())) : -1);
			EaeTypeDeveloppement typeDev = numLigneTypeDev > -1 ? (EaeTypeDeveloppement) getListeTypeDeveloppement()
					.get(numLigneTypeDev) : null;
			dev.setTypeDeveloppement(typeDev.getLibelleTypeDeveloppement());
			getEaeDeveloppementDao().modifierEaeDeveloppement(dev.getIdEaeDeveloppement(), dev.getTypeDeveloppement(),
					dev.getLibelle(), dev.getEcheance(), dev.getPriorisation());
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION_DEV)) {
			EaeDeveloppement dev = getDeveloppementCourant();
			getEaeDeveloppementDao().supprimerEaeDeveloppement(dev.getIdEaeDeveloppement());
		}
		initialiseEae();
		setDeveloppementCourant(null);
		// on nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		return true;
	}

	private boolean performControlerChampDev(HttpServletRequest request) {
		if (getVAL_ST_PRIO_DEV().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "priorisation"));
			return false;
		}
		if (!Services.estNumerique(getVAL_ST_PRIO_DEV())) {
			// "ERR992", "La zone @ doit être numérique."
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
			// "La date @ est incorrecte. Elle doit être au format date."
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
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_DEV Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PRIO_DEV Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEV Date
	 * de création : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_DATE_DEV() {
		return getZone(getNOM_ST_DATE_DEV());
	}

	public EaeDeveloppement getDeveloppementCourant() {
		return developpementCourant;
	}

	public void setDeveloppementCourant(EaeDeveloppement developpementCourant) {
		this.developpementCourant = developpementCourant;
	}

	public EaeTypeDeveloppementDao getEaeTypeDeveloppementDao() {
		return eaeTypeDeveloppementDao;
	}

	public void setEaeTypeDeveloppementDao(EaeTypeDeveloppementDao eaeTypeDeveloppementDao) {
		this.eaeTypeDeveloppementDao = eaeTypeDeveloppementDao;
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TYPE_DEV Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DEV() {
		return getLB_TYPE_DEV();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TYPE_DEV Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_TYPE_DEV_SELECT() {
		return getZone(getNOM_LB_TYPE_DEV_SELECT());
	}

	private ArrayList<EaeTypeDeveloppement> getListeTypeDeveloppement() {
		return listeTypeDeveloppement;
	}

	private void setListeTypeDeveloppement(ArrayList<EaeTypeDeveloppement> listeTypeDeveloppement) {
		this.listeTypeDeveloppement = listeTypeDeveloppement;
	}

	private Hashtable<String, EaeTypeDeveloppement> getHashTypeDeveloppement() {
		if (hashTypeDeveloppement == null)
			hashTypeDeveloppement = new Hashtable<String, EaeTypeDeveloppement>();
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'eae courant
		EAE eaeCourant = (EAE) getListeEae().get(indiceEltDocument);
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
	private void setListeDocuments(ArrayList<EaeFinalisation> newListeDocuments) {
		listeDocuments = newListeDocuments;
	}

	public ArrayList<EaeFinalisation> getListeDocuments() {
		if (listeDocuments == null) {
			listeDocuments = new ArrayList<EaeFinalisation>();
		}
		return listeDocuments;
	}

	/**
	 * Initialisation de la liste des eaes
	 * 
	 */
	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {

		// Recherche des documents de l'agent
		ArrayList<EaeFinalisation> listeDocAgent = getEaeFinalisationDao().listerDocumentFinalise(
				getEaeCourant().getIdEae());
		setListeDocuments(listeDocAgent);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int indiceDocEae = 0;
		if (getListeDocuments() != null) {
			for (int i = 0; i < getListeDocuments().size(); i++) {
				EaeFinalisation doc = (EaeFinalisation) getListeDocuments().get(i);

				addZone(getNOM_ST_NOM_DOC(indiceDocEae), doc.getIdGedDocument());
				addZone(getNOM_ST_DATE_DOC(indiceDocEae), sdf.format(doc.getDateFinalisation()));
				addZone(getNOM_ST_COMMENTAIRE(indiceDocEae), doc.getCommentaire());

				indiceDocEae++;
			}
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM_DOC Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM_DOC Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DOC Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CONSULTER_DOC Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_DOC(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("URL_SHAREPOINT_GED_VERSION");

		// Récup du document courant
		EaeFinalisation finalisation = (EaeFinalisation) getListeDocuments().get(indiceEltAConsulter);
		// on affiche le document
		int numVersion = (Integer.valueOf(finalisation.getVersionGedDocument()) + 1);
		setURLFichier(getScriptOuverture(repertoireStockage + finalisation.getIdGedDocument() + "&version="
				+ numVersion + ".0"));

		return true;
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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

		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		if (!extension.equals(".pdf")) {
			// "ERR165", "Le fichier doit être au format PDF."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR165"));
			result &= false;
		}
		return result;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Contrôle des champs
		if (!performControlerSaisieDocument(request))
			return false;

		EAE eae = getEaeCourant();

		if (!creeDocument(request, eae)) {
			return false;
		}

		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		// on met à jour le tableau des documents
		initialiseListeDocuments(request);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, EAE eae) throws Exception {
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}
		ArrayList<EaeEvaluateur> premierEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(eae.getIdEae());
		Integer numIncrement = getEaeNumIncrementDocumentDao().chercherEaeNumIncrement();

		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String nom = "EAE_" + premierEvaluateur.get(0).getIdAgent() + "_" + eae.getIdEae() + "_"
				+ numIncrement.toString() + extension;

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf")) {
			upload = uploadFichierPDF(fichierUpload, nom);
		}
		if (!upload) {
			// "ERR164",
			// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci de contacter le responsable du projet."
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
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_SP");

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

	public EaeNumIncrementDocumentDao getEaeNumIncrementDocumentDao() {
		return eaeNumIncrementDocumentDao;
	}

	public void setEaeNumIncrementDocumentDao(EaeNumIncrementDocumentDao eaeNumIncrementDocumentDao) {
		this.eaeNumIncrementDocumentDao = eaeNumIncrementDocumentDao;
	}

	/**
	 * Méthode qui teste si un paramètre se trouve dans le formulaire
	 */
	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null || (multi != null && multi
				.getParameter(param) != null));
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

	public String getNOM_PB_CONSULTER_ANCIEN_EAE(int i) {
		return "NOM_PB_CONSULTER_ANCIEN_EAE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_ANCIEN_EAE(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		logger.info("Rep stock : " + repertoireStockage);

		// Récup du document courant
		Document d = getDocumentDao().chercherDocumentById(indiceEltAConsulter);
		// on affiche le document
		logger.info("Lien doc : " + d.getLienDocument());
		logger.info("Script : " + getScriptOuverture(repertoireStockage + d.getLienDocument()));
		setURLFichier(getScriptOuverture(repertoireStockage + d.getLienDocument()));

		return true;
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
		// Contrôle des champs
		if (!performControlerSaisieDocumentAncienEAE(request))
			return false;

		if (!creeDocumentAncienEAE(request)) {
			return false;
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// on met à jour le tableau des documents
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
		addZone(getNOM_EF_LIENDOCUMENT_ANCIEN_EAE(), fichierUpload != null ? fichierUpload.getPath()
				: Const.CHAINE_VIDE);
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
				// "ERR992", "La zone @ doit être numérique.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année"));
				result &= false;
			} else {
				// on verifie qu'on n'ajoute pas de document apres 2012
				if (Integer.valueOf(getVAL_EF_ANNEE_ANCIEN_EAE()) >= 2013) {
					// "ERR166",
					// "Vous ne pouvez uploadé un document pour une année supérieure à 2012.");
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

		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		if (!extension.equals(".pdf")) {
			// "ERR165", "Le fichier doit être au format PDF."
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

		String extension = fichierUpload.getName().substring(fichierUpload.getName().indexOf('.'),
				fichierUpload.getName().length());
		String nom = "EAE_" + getVAL_EF_ANNEE_ANCIEN_EAE() + "_" + getAgentCourant().getIdAgent() + extension;

		// on verifie si il y a deja un document pour cet année
		boolean existDeja = false;
		for (int i = 0; i < getListeAncienEAE().size(); i++) {
			Document d = getListeAncienEAE().get(i);
			if (d.getNomDocument().equals(nom)) {
				existDeja = true;
				break;
			}
		}
		if (existDeja) {
			// "ERR167",
			// "Un fichier existe déjà pour cette année. Veuillez choisir une autre année.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR167"));
			return false;
		}

		// on upload le fichier
		boolean upload = false;
		if (extension.equals(".pdf")) {
			upload = uploadFichierPDFCristal(fichierUpload, nom);
		}
		if (!upload) {
			// "ERR164",
			// "Une erreur est survenue dans la sauvegarde de l'EAE. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR164"));
			return false;
		}

		// on crée le document en base de données
		TypeDocument typeEAE = getTypeDocumentDao().chercherTypeDocumentByCod("EAE");
		Document doc = new Document();
		doc.setLienDocument("EAE/" + nom);
		doc.setIdTypeDocument(typeEAE.getIdTypeDocument());
		doc.setNomOriginal(fichierUpload.getName());
		doc.setNomDocument(nom);
		doc.setDateDocument(new Date());
		doc.setCommentaire(getZone(getNOM_EF_COMMENTAIRE_ANCIEN_EAE()));
		Integer id = getDocumentDao().creerDocument(doc.getClasseDocument(), doc.getNomDocument(),
				doc.getLienDocument(), doc.getDateDocument(), doc.getCommentaire(), doc.getIdTypeDocument(),
				doc.getNomOriginal());

		DocumentAgent lien = new DocumentAgent();
		lien.setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
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

	private boolean uploadFichierPDFCristal(File f, String nomFichier) throws Exception {
		boolean resultat = false;
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
		// on verifie que les repertoires existent
		verifieRepertoire("EAE");

		File newFile = new File(repPartage + "EAE/" + nomFichier);

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

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie déjà que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
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
}
