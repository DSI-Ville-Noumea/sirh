package nc.mairie.gestionagent.process.absence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.RestitutionMassiveDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.spring.ws.RadiWSConsumer;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

public class OeABSRestitution extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private Logger logger = LoggerFactory.getLogger(OeABSRestitution.class);

	private AgentDao agentDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private List<RestitutionMassiveDto> listHistoRestitutionMassive;
	
	private RestitutionMassiveDto detailsHisto;
	
	@Override
	public String getJSP() {
		return "OeABSRestitution.jsp";
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
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
		// #13594
		initialiseHisto(request);
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}

	}
	
	private void initialiseHisto(HttpServletRequest request) throws Exception {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		setListHistoRestitutionMassive(consuAbs.getHistoRestitutionMassive(getAgentConnecte(request).getIdAgent()));
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			// Si clic sur le bouton PB_LANCER_RESTITUTION
			if (testerParametre(request, getNOM_PB_LANCER_RESTITUTION())) {
				return performPB_LANCER_RESTITUTION(request);
			}

			// Si clic sur les boutons du tableau
			for (RestitutionMassiveDto histo : getListHistoRestitutionMassive()) {
				int indice = histo.getIdRestitutionMassive();
				// Si clic sur le bouton PB_DUPLIQUER
				if (testerParametre(request, getNOM_PB_DETAILS_RESTITUTION(indice))) {
					return performPB_DETAILS_RESTITUTION(request, indice);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNomEcran() {
		return "ECR-ABS-VISU";
	}

	public String getNOM_ST_DATE_RESTITUTION() {
		return "NOM_ST_DATE_RESTITUTION";
	}

	public String getVAL_ST_DATE_RESTITUTION() {
		return getZone(getNOM_ST_DATE_RESTITUTION());
	}

	public String getNOM_RG_TYPE_RESTITUTION() {
		return "NOM_RG_TYPE_RESTITUTION";
	}

	public String getVAL_RG_TYPE_RESTITUTION() {
		return getZone(getNOM_RG_TYPE_RESTITUTION());
	}

	public String getNOM_RB_TYPE_MATIN() {
		return "NOM_RB_TYPE_MATIN";
	}

	public String getNOM_RB_TYPE_AM() {
		return "NOM_RB_TYPE_AM";
	}

	public String getNOM_RB_TYPE_JOURNEE() {
		return "NOM_RB_TYPE_JOURNEE";
	}

	public String getNOM_ST_MOTIF() {
		return "NOM_RG_MOTIF";
	}

	public String getVAL_ST_MOTIF() {
		return getZone(getNOM_ST_MOTIF());
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_PB_LANCER_RESTITUTION() {
		return "NOM_PB_LANCER_RESTITUTION";
	}

	public String getNOM_PB_DETAILS_RESTITUTION(Integer i) {
		return "NOM_PB_DETAILS_RESTITUTION_" + i;
	}

	public boolean performPB_LANCER_RESTITUTION(HttpServletRequest request) throws Exception {
		// vérification de la validité du formulaire
		if (!performControlerChamps(request)) {
			return false;
		}

		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ReturnMessageDto srm = null;

		String err = Const.CHAINE_VIDE;
		String info = Const.CHAINE_VIDE;

		RestitutionMassiveDto dto = new RestitutionMassiveDto();
		dto.setApresMidi(getVAL_RG_TYPE_RESTITUTION().equals(getNOM_RB_TYPE_AM()));
		dto.setDateRestitution(sdf.parse(getZone(getNOM_ST_DATE_RESTITUTION())));
		dto.setJournee(getVAL_RG_TYPE_RESTITUTION().equals(getNOM_RB_TYPE_JOURNEE()));
		dto.setMatin(getVAL_RG_TYPE_RESTITUTION().equals(getNOM_RB_TYPE_MATIN()));
		dto.setMotif(getVAL_ST_MOTIF());

		logger.debug("Appel au WS pour restituer un CA  ");

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		srm = consuAbs.addRestitutionMassive(getAgentConnecte(request).getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
		}
		if (srm.getInfos().size() > 0) {
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
		}

		if (!err.equals(Const.CHAINE_VIDE)) {
			err += info;
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (!info.equals(Const.CHAINE_VIDE)) {
			getTransaction().declarerErreur(info);
		}
		return true;
	}
	
	public boolean performPB_DETAILS_RESTITUTION(HttpServletRequest request, Integer id) throws Exception {
		
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		RestitutionMassiveDto details = consuAbs.getDetailsHistoRestitutionMassive(getAgentConnecte(request).getIdAgent(), getRestitutionMassiveById(id));
		
		setDetailsHisto(details);
		
		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agentConnecte = null;
		// on fait la correspondance entre le login et l'agent via RADI
		RadiWSConsumer radiConsu = new RadiWSConsumer();
		LightUserDto user = radiConsu.getAgentCompteADByLogin(u.getUserName());
		if (user == null) {
			return null;
		}
		try {
			agentConnecte = getAgentDao().chercherAgentParMatricule(
					radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		} catch (Exception e) {
			return null;
		}

		return agentConnecte;
	}

	private boolean performControlerChamps(HttpServletRequest request) {
		// type matin,am, journée
		if (getZone(getNOM_RG_TYPE_RESTITUTION()).equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "type de restitution"));
			return false;
		}
		// date
		if (getZone(getNOM_ST_DATE_RESTITUTION()).equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de restitution"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_ST_DATE_RESTITUTION()))) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de restitution"));
			return false;
		}
		return true;
	}

	public List<RestitutionMassiveDto> getListHistoRestitutionMassive() {
		return listHistoRestitutionMassive;
	}

	public void setListHistoRestitutionMassive(
			List<RestitutionMassiveDto> listHistoRestitutionMassive) {
		this.listHistoRestitutionMassive = listHistoRestitutionMassive;
	}
	
	private RestitutionMassiveDto getRestitutionMassiveById(Integer id)  {
		for(RestitutionMassiveDto dto : getListHistoRestitutionMassive()) {
			if(dto.getIdRestitutionMassive().equals(id)) {
				return dto;
			}
		}
		return null;
	}

	public RestitutionMassiveDto getDetailsHisto() {
		return detailsHisto;
	}

	public void setDetailsHisto(RestitutionMassiveDto detailsHisto) {
		this.detailsHisto = detailsHisto;
	}

	public Agent getAgent(Integer idAgent) throws Exception {
		return getAgentDao().chercherAgent(idAgent);
	}
	
}
