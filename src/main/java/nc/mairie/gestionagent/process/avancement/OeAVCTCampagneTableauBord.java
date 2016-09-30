package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeDashboardItemDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IEaeService;
import nc.noumea.spring.service.IRadiService;

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

	private Logger logger = LoggerFactory.getLogger(OeAVCTCampagneTableauBord.class);
	
	private List<EaeDashboardItemDto> listeTableauBord;

	private ArrayList<String> listeAnnee;
	private String[] LB_ANNEE;

	private AgentDao agentDao;
	
	private IEaeService eaeService;
	private IRadiService radiService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
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

		initialiseListeDeroulante(request);
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == eaeService) {
			eaeService = (IEaeService) context.getBean("eaeService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
	}

	private void initialiseListeDeroulante(HttpServletRequest request) throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {

			List<CampagneEaeDto> listCampagne = eaeService.getListeCampagnesEae(getAgentConnecte(request).getIdAgent());
			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			ArrayList<String> listannee = new ArrayList<String>();
			for (CampagneEaeDto camp : listCampagne) {
				listannee.add(camp.getAnnee().toString());
				String ligne[] = { camp.getAnnee().toString() };
				aFormat.ajouteLigne(ligne);
			}
			setListeAnnee(listannee);

			setLB_ANNEE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_CALCULER(HttpServletRequest request) throws Exception {

		int numAnnee = (Services.estNumerique(getZone(getNOM_LB_ANNEE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ANNEE_SELECT())) : -1);

		if (numAnnee < 0 || getListeAnnee().size() == 0 || numAnnee > getListeAnnee().size())
			return false;

		String annee = getListeAnnee().get(numAnnee);
		// on commence le calcul pour la campagne en cours
		// on cherche la campagne ou datefin est vide
		setListeTableauBord(null);
		// RG-EAE-24
		try {
			
			List<EaeDashboardItemDto> listeDirectionSection = eaeService.getEaesDashboard(getAgentConnecte(request).getIdAgent(), new Integer(annee));
			
			for (int i = 0; i < listeDirectionSection.size(); i++) {
				EaeDashboardItemDto eaeFDP = listeDirectionSection.get(i);
				
				addZone(getNOM_ST_DIRECTION(i), eaeFDP.getDirection());
				addZone(getNOM_ST_SECTION(i), eaeFDP.getSection());
				addZone(getNOM_ST_NON_AFF(i), eaeFDP.getNonAffecte() == 0 ? "&nbsp;" : new Integer(eaeFDP.getNonAffecte()).toString());
				addZone(getNOM_ST_NON_DEB(i), eaeFDP.getNonDebute() == 0 ? "&nbsp;" : new Integer(eaeFDP.getNonDebute()).toString());
				addZone(getNOM_ST_CREE(i), eaeFDP.getCree() == 0 ? "&nbsp;" : new Integer(eaeFDP.getCree()).toString());
				addZone(getNOM_ST_EN_COURS(i), eaeFDP.getEnCours() == 0 ? "&nbsp;" : new Integer(eaeFDP.getEnCours()).toString());
				addZone(getNOM_ST_FINALISE(i), eaeFDP.getFinalise() == 0 ? "&nbsp;" : new Integer(eaeFDP.getFinalise()).toString());
				addZone(getNOM_ST_CONTROLE(i), eaeFDP.getNbEaeControle() == 0 ? "&nbsp;" : new Integer(eaeFDP.getNbEaeControle()).toString());
				addZone(getNOM_ST_TOTAL_EAE(i), eaeFDP.getTotalEAE() == 0 ? "&nbsp;" : new Integer(eaeFDP.getTotalEAE()).toString());
				addZone(getNOM_ST_PASSAGE_CAP(i), eaeFDP.getNbEaeCAP() == 0 ? "&nbsp;" : new Integer(eaeFDP.getNbEaeCAP()).toString());

				addZone(getNOM_ST_NON_DEFINI(i), eaeFDP.getNonDefini() == 0 ? "&nbsp;" : new Integer(eaeFDP.getNonDefini()).toString());
				addZone(getNOM_ST_MINI(i), eaeFDP.getMini() == 0 ? "&nbsp;" : new Integer(eaeFDP.getMini()).toString());
				addZone(getNOM_ST_MOY(i), eaeFDP.getMoy() == 0 ? "&nbsp;" : new Integer(eaeFDP.getMoy()).toString());
				addZone(getNOM_ST_MAXI(i), eaeFDP.getMaxi() == 0 ? "&nbsp;" : new Integer(eaeFDP.getMaxi()).toString());
				addZone(getNOM_ST_CHANGEMENT_CLASSE(i), eaeFDP.getChangClasse() == 0 ? "&nbsp;" : new Integer(eaeFDP.getChangClasse()).toString());

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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DIRECTION Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_SECTION Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NON_AFF Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NON_DEB Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CREE Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_EN_COURS Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_FINALISE Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CONTROLE Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TOTAL_EAE Date
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_PASSAGE_CAP
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NON_DEFINI
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MINI Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MOY Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MAXI Date de
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
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_CHANGEMENT_CLASSE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CHANGEMENT_CLASSE(int i) {
		return getZone(getNOM_ST_CHANGEMENT_CLASSE(i));
	}

	public List<EaeDashboardItemDto> getListeTableauBord() {
		if (listeTableauBord == null)
			return new ArrayList<EaeDashboardItemDto>();
		return listeTableauBord;
	}

	public void setListeTableauBord(List<EaeDashboardItemDto> listeTableauBord) {
		this.listeTableauBord = listeTableauBord;
	}

	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	public ArrayList<String> getListeAnnee() {
		return listeAnnee == null ? new ArrayList<String>() : listeAnnee;
	}

	public void setListeAnnee(ArrayList<String> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agentConnecte = null;
		// on fait la correspondance entre le login et l'agent via RADI

		LightUserDto user = radiService.getAgentCompteADByLogin(u.getUserName());
		if (user == null) {
			return null;
		}
		try {
			agentConnecte = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		} catch (Exception e) {
			return null;
		}

		return agentConnecte;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
	
}