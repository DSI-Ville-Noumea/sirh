package nc.mairie.gestionagent.process.pointage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.management.InvalidAttributeValueException;
import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.DpmIndemniteAnneeDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.PtgService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class OePTGPrimeDpmParametrage extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OePTGPrimeDpmParametrage.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4481591314447007514L;
	
	public final static String RECUPERATION = "Récupération";
	public final static String INDEMNITE = "Indémnité";

	public static final int STATUT_RECHERCHER_AGENT_DEMANDE = 1;
	public static final int STATUT_RECHERCHER_AGENT_CREATION = 2;

	public String focus = null;

	private IPtgService ptgService;

	private IAdsService adsService;

	private IRadiService radiService;

	private AgentDao agentDao;

	private TreeMap<Integer, DpmIndemniteAnneeDto> listeDpmAnnee;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public String ACTION_CREATION = "Creation_demande";
	public String ACTION_MODIFICATION = "Modification_demande";
	public String ACTION_MOTIF_REJET = "Motif_rejet_demande";

	@Override
	public String getJSP() {
		return "OePTGPrimeDpmParametrage.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-PTG-PRIME-DPM";
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
		setFocus(getDefaultFocus());
		
		afficheListeAnnees(request);
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
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

	private void afficheListeAnnees(HttpServletRequest request) throws Exception {
		
		setListeDpmAnnee((ArrayList<DpmIndemniteAnneeDto>)ptgService.getListDpmIndemAnnee(getAgentConnecte(request).getIdAgent()));
		
		for (Entry<Integer, DpmIndemniteAnneeDto> choixMap : getListeDpmAnnee().entrySet()) {
			DpmIndemniteAnneeDto annee = choixMap.getValue();
			Integer i = choixMap.getKey();
			
			try {
				addZone(getNOM_ST_ANNEE(i), annee.getAnnee().toString());
				addZone(getNOM_ST_DATE_DEBUT(i), sdf.format(annee.getDateDebut()));
				addZone(getNOM_ST_DATE_FIN(i), sdf.format(annee.getDateFin()));
			} catch (Exception e) {
				logger.debug(e.getMessage());
				continue;
			}
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

	public boolean performPB_ANNULER(HttpServletRequest request) {
		viderZoneSaisie(request);
		return true;
	}

	private void viderZoneSaisie(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
	}

	public boolean performPB_CREATION(HttpServletRequest request) throws Exception {

		// On vérifie si c'est le clic d'ouverture ou d'enregistrement
		if (getZone(getNOM_ST_ACTION()) != ACTION_CREATION) {
			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_CREATION);
			return true;
		}

		if (getVAL_ST_ANNEE().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			return false;
		}
		
		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}
		
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}
		
		Integer annee = new Integer(getVAL_ST_ANNEE());
		Date dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT());
		Date dateFin = sdf.parse(getVAL_ST_DATE_FIN());

		DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto();
		dto.setAnnee(annee);
		dto.setDateDebut(dateDebut);
		dto.setDateFin(dateFin);

		if (!verifyDpmIndemniteAnnee(dto))
			return false;

		ReturnMessageDto srm = ptgService.createDpmIndemAnnee(getAgentConnecte(request).getIdAgent(), dto);

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

		afficheListeAnnees(request);
		
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	public boolean performPB_MODIFICATION(HttpServletRequest request, Integer indiceTR) throws Exception {

		DpmIndemniteAnneeDto dto = getListeDpmAnnee().get(indiceTR);

		addZone(getNOM_ST_INDICE_ANNEE(), indiceTR.toString());
		addZone(getNOM_ST_ANNEE(), null != dto.getAnnee() ? dto.getAnnee().toString() : Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), null != dto.getDateDebut() ? sdf.format(dto.getDateDebut()) : Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), null != dto.getDateFin() ? sdf.format(dto.getDateFin()) : Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		return true;
	}

	public boolean performPB_VALIDER_MODIFICATION(HttpServletRequest request) throws Exception {


		Integer indiceTR = new Integer(getVAL_ST_INDICE_ANNEE());

		DpmIndemniteAnneeDto dto = getListeDpmAnnee().get(indiceTR);

		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}
		
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}
		
		Date dateDebut = sdf.parse(getVAL_ST_DATE_DEBUT());
		Date dateFin = sdf.parse(getVAL_ST_DATE_FIN());

		dto.setDateDebut(dateDebut);
		dto.setDateFin(dateFin);
		dto.setListDpmIndemniteChoixAgentDto(null);
		
		if (!verifyDpmIndemniteAnnee(dto))
			return false;

		ReturnMessageDto srm = ptgService.saveDpmIndemAnnee(getAgentConnecte(request).getIdAgent(), dto);

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

		afficheListeAnnees(request);
		
		setStatut(STATUT_MEME_PROCESS);
		
		return true;
	}
	
	/**
	 * Vérifie que l'indémnité est correctement renseignée
	 * @param dto
	 * @throws InvalidAttributeValueException 
	 * @throws ParseException 
	 */
	private boolean verifyDpmIndemniteAnnee(DpmIndemniteAnneeDto dto) throws ParseException {
		if (dto.getAnnee().toString().length() != 4) {
			getTransaction().declarerErreur("L'année n'est pas dans un format correct.");
			return false;
		}

		if (dto.getDateDebut().after(dto.getDateFin())){
			getTransaction().declarerErreur("La date de début doit être antérieur à la date de fin.");
			return false;
		}

		String startYearFormat = dto.getAnnee() + "-01-01";
		String endYearFormat = dto.getAnnee() + "-12-31";
		Date starYear = new SimpleDateFormat("yyyy-MM-dd").parse(startYearFormat);
		Date endYear = new SimpleDateFormat("yyyy-MM-dd").parse(endYearFormat);
		
		if ((dto.getDateDebut().after(endYear) || dto.getDateDebut().before(starYear)) || (dto.getDateFin().before(starYear) || dto.getDateFin().after(endYear))){
			getTransaction().declarerErreur("Les dates doivent être comprises dans l'année voulue.");
			return false;
		}
		
		return true;
	}

	public String getDefaultFocus() {
		return getNOM_PB_VALIDATION();
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

	public String getNOM_PB_VALIDATION() {
		return "NOM_PB_VALIDATION";
	}

	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE_" + i;
	}

	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	public String getNOM_ST_ANNEE() {
		return "NOM_ST_ANNEE";
	}

	public String getVAL_ST_ANNEE() {
		return getZone(getNOM_ST_ANNEE());
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

	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT_" + i;
	}

	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN_" + i;
	}

	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_PB_AJOUTER_CHOIX_DPM() {
		return "NOM_PB_CREATE_BOX";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public String getNOM_PB_CREATION() {
		return "NOM_PB_CREATION";
	}

	public String getNOM_PB_MODIFICATION() {
		return "NOM_PB_MODIFICATION";
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public String getNOM_PB_SAISIE_ANNEE(int i) {
		return "NOM_PB_SAISIE_ANNEE_" + i;
	}

	public String getNOM_ST_INDICE_ANNEE() {
		return "NOM_ST_INDICE_ANNEE";
	}

	public String getVAL_ST_INDICE_ANNEE() {
		return getZone(getNOM_ST_INDICE_ANNEE());
	}

	public TreeMap<Integer, DpmIndemniteAnneeDto> getListeDpmAnnee() {
		return listeDpmAnnee == null ? new TreeMap<Integer, DpmIndemniteAnneeDto>() : listeDpmAnnee;
	}

	public void setListeDpmAnnee(ArrayList<DpmIndemniteAnneeDto> pListeDpmAnnee) {

		listeDpmAnnee = new TreeMap<>();
		int i = 0;
		for (DpmIndemniteAnneeDto choix : pListeDpmAnnee) {
			listeDpmAnnee.put(i, choix);
			i++;
		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	public IPtgService getPtgService() {

		if (null == ptgService) {
			ApplicationContext context = ApplicationContextProvider.getContext();
			ptgService = (PtgService) context.getBean("ptgService");
		}
		return ptgService;
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CREATION
			if (testerParametre(request, getNOM_PB_CREATION())) {
				return performPB_CREATION(request);
			}
			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur les boutons du tableau
			for (Integer indiceTR : getListeDpmAnnee().keySet()) {
				if (testerParametre(request, getNOM_PB_SAISIE_ANNEE(indiceTR))) {
					return performPB_MODIFICATION(request, indiceTR);
				}
			}
			// Si clic sur le bouton PB_VALIDER_MOTIF_ANNULATION
			if (testerParametre(request, getNOM_PB_MODIFICATION())) {
				return performPB_VALIDER_MODIFICATION(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
}
