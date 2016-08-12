package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nc.mairie.gestionagent.pointage.dto.EtatsPayeurDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.PtgService;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OePTGPayeurFonct extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private Logger logger = LoggerFactory.getLogger(OePTGPayeurFonct.class);

	public static final String STATUT = "F";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private ArrayList<EtatsPayeurDto> listEtatsPayeurDto;

	private String libelleStatut = "fonctionnaires";

	private AgentDao agentDao;

	private IRadiService radiService;

	private IPtgService ptgService;

	@Override
	public String getJSP() {
		return "OePTGPayeurFonct.jsp";
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
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
		// liste des historiques des editions
		initialiseHistoriqueEditions();

	}

	// affichage ou non du bouton "lancer editions"
	public boolean isBoutonLancerEditionAffiche() throws Exception {

		try {
			return ptgService.canStartExportEtatsPayeur(STATUT);
		} catch (Exception e) {
			logger.debug("Erreur OePTGPayeurFonct.isBoutonLancerEditionAffiche() " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			// clic sur le bouton Lancer editions
			if (testerParametre(request, getNOM_PB_LANCER_EDITIONS())) {
				return performPB_LANCER_EDITIONS(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseHistoriqueEditions() throws Exception {

		try {
			setListEtatsPayeurDto((ArrayList<EtatsPayeurDto>) ptgService.getListEtatsPayeurByStatut(STATUT));
		} catch (Exception e) {
			logger.debug("Erreur OePTGPayeurFonct.initialiseHistoriqueEditions() " + e.getMessage());
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR700", libelleStatut));
		}

		for (int i = 0; i < getListEtatsPayeurDto().size(); i++) {
			EtatsPayeurDto dto = getListEtatsPayeurDto().get(i);
			addZone(getNOM_ST_USER_DATE_EDITION(i),
					sdf.format(dto.getDateEdition()) + "<br />" + dto.getDisplayPrenom() + " " + dto.getDisplayNom());
			addZone(getNOM_ST_LIBELLE_EDITION(i), dto.getLabel());
		}

	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_LANCER_EDITIONS(HttpServletRequest request) throws Exception {

		try {
			// on recupere l'agent connecte
			UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
			Agent agentConnecte = null;
			// on fait la correspondance entre le login et l'agent via RADI
			LightUserDto user = radiService.getAgentCompteADByLogin(u.getUserName());
			if (user == null) {
				// "ERR183",
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return false;
			}
			agentConnecte = getAgentDao().chercherAgentParMatricule(
					radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));

			ptgService.startExportEtatsPayeur(agentConnecte.getIdAgent(), STATUT);
		} catch (Exception e) {
			logger.debug("Erreur OePTGPayeurFonct.performPB_LANCER_EDITIONS() " + e.getMessage());
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR702", libelleStatut));
			return false;
		}

		return true;
	}

	/**
	 * @return the listEtatsPayeurDto
	 */
	public ArrayList<EtatsPayeurDto> getListEtatsPayeurDto() {
		return listEtatsPayeurDto == null ? new ArrayList<EtatsPayeurDto>() : listEtatsPayeurDto;
	}

	/**
	 * @param listEtatsPayeurDto
	 *            the listEtatsPayeurDto to set
	 */
	public void setListEtatsPayeurDto(ArrayList<EtatsPayeurDto> listEtatsPayeurDto) {
		this.listEtatsPayeurDto = listEtatsPayeurDto;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-PAY-TITU";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_LANCER_EDITIONS() {
		return "NOM_PB_LANCER_EDITIONS";
	}

	public String getNOM_ST_USER_DATE_EDITION(int i) {
		return "NOM_ST_USER_DATE_EDITION_" + i;
	}

	public String getVAL_ST_USER_DATE_EDITION(int i) {
		return getZone(getNOM_ST_USER_DATE_EDITION(i));
	}

	public String getNOM_ST_LIBELLE_EDITION(int i) {
		return "NOM_ST_LIBELLE_EDITION_" + i;
	}

	public String getVAL_ST_LIBELLE_EDITION(int i) {
		return getZone(getNOM_ST_LIBELLE_EDITION(i));
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
