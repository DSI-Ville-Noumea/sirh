package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurDto;
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

public class OePTGTitreRepasEtatPayeur extends BasicProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3941458429982256285L;
	
	private Logger logger = LoggerFactory.getLogger(OePTGTitreRepasEtatPayeur.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private ArrayList<TitreRepasEtatPayeurDto> listEtatsPayeurDto;

	private AgentDao agentDao;

	private IRadiService radiService;

	private IPtgService ptgService;
	
	@Override
	public String getJSP() {
		return "OePTGTitreRepasEtatPayeur.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-TITRE-REPAS";
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190","Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		
		initialiseDao();
		// liste des historiques des editions
		initialiseHistoriqueEditions(request);
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == agentDao) {
			agentDao = (new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
	}

	/**
	 * Initialisation des liste deroulantes de l'écran convocation du suivi
	 * médical.
	 */
	private void initialiseHistoriqueEditions(HttpServletRequest request) throws Exception {

		try {
			setListEtatsPayeurDto((ArrayList<TitreRepasEtatPayeurDto>) ptgService.getListTitreRepasEtatPayeur(getAgentConnecte(request).getIdAgent()));
		} catch (Exception e) {
			logger.debug("Erreur OePTGTitreRepasEtatPayeur.initialiseHistoriqueEditions() " + e.getMessage());
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR703"));
		}

		for (int i = 0; i < getListEtatsPayeurDto().size(); i++) {
			TitreRepasEtatPayeurDto dto = getListEtatsPayeurDto().get(i);
			addZone(getNOM_ST_USER_DATE_EDITION(i),
					sdf.format(dto.getDateEdition()) + "<br />" + (null == dto.getAgent() ? "" : dto.getAgent().getPrenom() + " " + dto.getAgent().getNom()));
			addZone(getNOM_ST_LIBELLE_EDITION(i), dto.getLabel());
		}
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
					agent = agentDao.chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}
	
	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * @return the listEtatsPayeurDto
	 */
	public ArrayList<TitreRepasEtatPayeurDto> getListEtatsPayeurDto() {
		return listEtatsPayeurDto == null ? new ArrayList<TitreRepasEtatPayeurDto>() : listEtatsPayeurDto;
	}

	/**
	 * @param listEtatsPayeurDto
	 *            the listEtatsPayeurDto to set
	 */
	public void setListEtatsPayeurDto(ArrayList<TitreRepasEtatPayeurDto> listEtatsPayeurDto) {
		this.listEtatsPayeurDto = listEtatsPayeurDto;
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

}
