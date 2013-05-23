package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagneTableauBord extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<EaeFichePoste> listeTableauBord;

	private EAEDao eaeDao;
	private CampagneEAEDao campagneEAEDao;
	private EaeEvaluationDao eaeEvaluationDao;
	private EaeFichePosteDao eaeFichePosteDao;

	private Logger logger = LoggerFactory.getLogger(OeAVCTCampagneTableauBord.class);

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
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

		initialiseDao();
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		// Oracle
		if (getCampagneEAEDao() == null) {
			setCampagneEAEDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}

		if (getEaeDao() == null) {
			setEaeDao((EAEDao) context.getBean("eaeDao"));
		}

		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao((EaeEvaluationDao) context.getBean("eaeEvaluationDao"));
		}

		if (getEaeFichePosteDao() == null) {
			setEaeFichePosteDao((EaeFichePosteDao) context.getBean("eaeFichePosteDao"));
		}
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CALCULER
			if (testerParametre(request, getNOM_PB_CALCULER())) {
				return performPB_CALCULER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTCampagneTableauBord() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTCampagneTableauBord.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CALCULER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_CALCULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_CALCULER(HttpServletRequest request) throws Exception {
		// on commence le calcul pour la campagne en cours
		// on cherche la campagne ou datefin est vide
		setListeTableauBord(null);
		// RG-EAE-24
		try {
			CampagneEAE campEnCours = getCampagneEAEDao().chercherCampagneEAEOuverte();
			// on determine les section différentes
			ArrayList<EaeFichePoste> listeDirectionSection = getEaeFichePosteDao().listerEaeFichePosteGrouperParDirectionSection(
					campEnCours.getIdCampagneEAE());
			Integer nbNonAff = 0;
			Integer nbNonDeb = 0;
			Integer nbCree = 0;
			Integer nbEnCours = 0;
			Integer nbFinalise = 0;
			Integer nbControle = 0;
			Integer nbTotalEae = 0;
			Integer nbCAP = 0;
			Integer nbNonDef = 0;
			Integer nbMini = 0;
			Integer nbMoy = 0;
			Integer nbMaxi = 0;
			Integer nbChangementClasse = 0;
			for (int i = 0; i < listeDirectionSection.size(); i++) {
				EaeFichePoste eaeFDP = listeDirectionSection.get(i);
				// on cherche la liste des EAE pour cette direction et section
				// et par etat
				Integer nbEAENonAffecte = getEaeDao().compterEAEDirectionSectionEtat(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), EnumEtatEAE.NON_AFFECTE.getCode());
				nbNonAff += nbEAENonAffecte;
				Integer nbEAENonDebute = getEaeDao().compterEAEDirectionSectionEtat(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), EnumEtatEAE.NON_DEBUTE.getCode());
				nbNonDeb += nbEAENonDebute;
				Integer nbEAECRee = getEaeDao().compterEAEDirectionSectionEtat(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), EnumEtatEAE.CREE.getCode());
				nbCree += nbEAECRee;
				Integer nbEAEEnCours = getEaeDao().compterEAEDirectionSectionEtat(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), EnumEtatEAE.EN_COURS.getCode());
				nbEnCours += nbEAEEnCours;
				Integer nbEAEFinalise = getEaeDao().compterEAEDirectionSectionEtat(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), EnumEtatEAE.FINALISE.getCode());
				nbFinalise += nbEAEFinalise;
				Integer nbEAEControle = getEaeDao().compterEAEDirectionSectionEtat(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), EnumEtatEAE.CONTROLE.getCode());
				nbControle += nbEAEControle;
				Integer nbEAECAP = getEaeDao().compterEAEDirectionSectionCAP(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ());
				nbCAP += nbEAECAP;
				Integer totalEAE = nbEAENonAffecte + nbEAENonDebute + nbEAECRee + nbEAEEnCours + nbEAEFinalise + nbEAEControle;
				nbTotalEae += totalEAE;

				// on cherche les propositions d'avancement
				Integer nbAvctNonDefini = getEaeEvaluationDao().compterAvisSHDNonDefini(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ());
				nbNonDef += nbAvctNonDefini;
				Integer nbAvctMini = getEaeEvaluationDao().compterAvisSHDAvct(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), "MINI");
				nbMini += nbAvctMini;
				Integer nbAvctMoy = getEaeEvaluationDao().compterAvisSHDAvct(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), "MOY");
				nbMoy += nbAvctMoy;
				Integer nbAvctMAxi = getEaeEvaluationDao().compterAvisSHDAvct(campEnCours.getIdCampagneEAE(), eaeFDP.getDirectionServ(),
						eaeFDP.getSectionServ(), "MAXI");
				nbMaxi += nbAvctMAxi;
				Integer nbAvctChangementClasse = getEaeEvaluationDao().compterAvisSHDChangementClasse(campEnCours.getIdCampagneEAE(),
						eaeFDP.getDirectionServ(), eaeFDP.getSectionServ());
				nbChangementClasse += nbAvctChangementClasse;

				addZone(getNOM_ST_DIRECTION(i), eaeFDP.getDirectionServ());
				addZone(getNOM_ST_SECTION(i), eaeFDP.getSectionServ());
				addZone(getNOM_ST_NON_AFF(i), nbEAENonAffecte == 0 ? "&nbsp;" : nbEAENonAffecte.toString());
				addZone(getNOM_ST_NON_DEB(i), nbEAENonDebute == 0 ? "&nbsp;" : nbEAENonDebute.toString());
				addZone(getNOM_ST_CREE(i), nbEAECRee == 0 ? "&nbsp;" : nbEAECRee.toString());
				addZone(getNOM_ST_EN_COURS(i), nbEAEEnCours == 0 ? "&nbsp;" : nbEAEEnCours.toString());
				addZone(getNOM_ST_FINALISE(i), nbEAEFinalise == 0 ? "&nbsp;" : nbEAEFinalise.toString());
				addZone(getNOM_ST_CONTROLE(i), nbEAEControle == 0 ? "&nbsp;" : nbEAEControle.toString());
				addZone(getNOM_ST_TOTAL_EAE(i), totalEAE == 0 ? "&nbsp;" : totalEAE.toString());
				addZone(getNOM_ST_PASSAGE_CAP(i), nbEAECAP == 0 ? "&nbsp;" : nbEAECAP.toString());

				addZone(getNOM_ST_NON_DEFINI(i), nbAvctNonDefini == 0 ? "&nbsp;" : nbAvctNonDefini.toString());
				addZone(getNOM_ST_MINI(i), nbAvctMini == 0 ? "&nbsp;" : nbAvctMini.toString());
				addZone(getNOM_ST_MOY(i), nbAvctMoy == 0 ? "&nbsp;" : nbAvctMoy.toString());
				addZone(getNOM_ST_MAXI(i), nbAvctMAxi == 0 ? "&nbsp;" : nbAvctMAxi.toString());
				addZone(getNOM_ST_CHANGEMENT_CLASSE(i), nbAvctChangementClasse == 0 ? "&nbsp;" : nbAvctChangementClasse.toString());

			}
			setListeTableauBord(listeDirectionSection);

		} catch (Exception e) {
			logger.error("Exception :" + e.getMessage());
			// "ERR212",
			// "Aucune campagne n'est ouverte. Le calcul ne s'effectue que sur une campagne ouverte."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR212"));
			return false;
		}
		return true;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-CAMPAGNE-TB";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SECTION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_SECTION(int i) {
		return "NOM_ST_SECTION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SECTION Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_SECTION(int i) {
		return getZone(getNOM_ST_SECTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NON_AFF Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NON_AFF(int i) {
		return "NOM_ST_NON_AFF_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NON_AFF Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NON_AFF(int i) {
		return getZone(getNOM_ST_NON_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NON_DEB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NON_DEB(int i) {
		return "NOM_ST_NON_DEB_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NON_DEB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NON_DEB(int i) {
		return getZone(getNOM_ST_NON_DEB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CREE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CREE(int i) {
		return "NOM_ST_CREE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CREE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CREE(int i) {
		return getZone(getNOM_ST_CREE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EN_COURS Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_EN_COURS(int i) {
		return "NOM_ST_EN_COURS_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EN_COURS Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_EN_COURS(int i) {
		return getZone(getNOM_ST_EN_COURS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FINALISE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_FINALISE(int i) {
		return "NOM_ST_FINALISE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FINALISE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_FINALISE(int i) {
		return getZone(getNOM_ST_FINALISE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CONTROLE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CONTROLE(int i) {
		return "NOM_ST_CONTROLE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CONTROLE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CONTROLE(int i) {
		return getZone(getNOM_ST_CONTROLE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TOTAL_EAE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_TOTAL_EAE(int i) {
		return "NOM_ST_TOTAL_EAE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TOTAL_EAE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_TOTAL_EAE(int i) {
		return getZone(getNOM_ST_TOTAL_EAE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PASSAGE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_PASSAGE_CAP(int i) {
		return "NOM_ST_PASSAGE_CAP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PASSAGE_CAP
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_PASSAGE_CAP(int i) {
		return getZone(getNOM_ST_PASSAGE_CAP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NON_DEFINI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NON_DEFINI(int i) {
		return "NOM_ST_NON_DEFINI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NON_DEFINI
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NON_DEFINI(int i) {
		return getZone(getNOM_ST_NON_DEFINI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MINI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MINI(int i) {
		return "NOM_ST_MINI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MINI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MINI(int i) {
		return getZone(getNOM_ST_MINI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOY Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOY(int i) {
		return "NOM_ST_MOY_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOY Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOY(int i) {
		return getZone(getNOM_ST_MOY(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MAXI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MAXI(int i) {
		return "NOM_ST_MAXI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MAXI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MAXI(int i) {
		return getZone(getNOM_ST_MAXI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CHANGEMENT_CLASSE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CHANGEMENT_CLASSE(int i) {
		return "NOM_ST_CHANGEMENT_CLASSE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_CLASSE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_CLASSE(int i) {
		return getZone(getNOM_ST_CHANGEMENT_CLASSE(i));
	}

	public ArrayList<EaeFichePoste> getListeTableauBord() {
		if (listeTableauBord == null)
			return new ArrayList<EaeFichePoste>();
		return listeTableauBord;
	}

	public void setListeTableauBord(ArrayList<EaeFichePoste> listeTableauBord) {
		this.listeTableauBord = listeTableauBord;
	}

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public CampagneEAEDao getCampagneEAEDao() {
		return campagneEAEDao;
	}

	public void setCampagneEAEDao(CampagneEAEDao campagneEAEDao) {
		this.campagneEAEDao = campagneEAEDao;
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
	}

	public EaeFichePosteDao getEaeFichePosteDao() {
		return eaeFichePosteDao;
	}

	public void setEaeFichePosteDao(EaeFichePosteDao eaeFichePosteDao) {
		this.eaeFichePosteDao = eaeFichePosteDao;
	}
}