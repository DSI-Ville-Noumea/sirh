package nc.mairie.gestionagent.process.election;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.absence.vo.VoAgentCompteur;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IRadiService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 *
 */
public class OeELECSaisieCompteurA53 extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(OeELECSaisieCompteurA53.class);

	private ArrayList<CompteurDto> listeCompteur;
	private ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale;
	private ArrayList<String> listeAnnee;
	private String[] LB_ANNEE;
	private String[] LB_OS;
	private String[] LB_MOTIF;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_MODIFICATION = "Modification d'un compteur.";
	public String ACTION_CREATION = "Création d'un compteur.";
	public String ACTION_VISUALISATION = "Consultation d'un compteur.";

	private AgentDao agentDao;
	
	private IRadiService radiService;

	private IAbsService absService;

	@Override
	public String getJSP() {
		return "OeELECSaisieCompteurA53.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-ELEC-COMPTEUR";
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
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
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

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		initialiseListeCompteur(request);
	}

	private void initialiseListeDeroulante() {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			Integer annee = cal.get(Calendar.YEAR);

			String[] list = { String.valueOf(annee), String.valueOf(annee + 1), String.valueOf(annee + 2) };

			ArrayList<String> arrayList = new ArrayList<String>();

			for (String an : list)
				arrayList.add(an);

			setListeAnnee(arrayList);

			setLB_ANNEE(list);
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}

		// Si liste motifs vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) absService
					.getListeMotifCompteur(EnumTypeAbsence.ASA_A53.getCode());
			setListeMotifCompteur(listeMotifs);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (MotifCompteurDto motif : listeMotifs) {
				String ligne[] = { motif.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		}

		// Si liste organisations syndicales vide alors affectation
		if (getLB_OS() == LBVide) {
			ArrayList<OrganisationSyndicaleDto> listeOrga = (ArrayList<OrganisationSyndicaleDto>) absService
					.getListeOrganisationSyndicale();
			setListeOrganisationSyndicale(listeOrga);

			int[] tailles = { 20, 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (OrganisationSyndicaleDto os : listeOrga) {
				String ligne[] = { os.getSigle(), os.getLibelle() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_OS(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
		}

	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private void initialiseListeCompteur(HttpServletRequest request) throws Exception {
		ArrayList<CompteurDto> listeCompteur = (ArrayList<CompteurDto>) absService.getListeCompteursA53();
		logger.debug("Taille liste des compteurs ASA A53 : " + listeCompteur.size());
		// #14737 tri par ordre alpha
		List<VoAgentCompteur> listCompteurAgent = new ArrayList<VoAgentCompteur>();
		for (CompteurDto dto : listeCompteur) {
			VoAgentCompteur voCompteur = new VoAgentCompteur(dto, null);
			voCompteur.setNom(dto.getOrganisationSyndicaleDto().getSigle());
			listCompteurAgent.add(voCompteur);
		}
		Collections.sort(listCompteurAgent);

		ArrayList<CompteurDto> listeCompteurTriee = new ArrayList<CompteurDto>();
		int indiceLigne = 0;
		for (VoAgentCompteur vo : listCompteurAgent) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(vo.getCompteur().getDateDebut());
			Integer annee = cal.get(Calendar.YEAR);

			addZone(getNOM_ST_OS(indiceLigne), vo.getCompteur().getOrganisationSyndicaleDto().getSigle() + " - "
					+ vo.getCompteur().getOrganisationSyndicaleDto().getLibelle());
			addZone(getNOM_ST_ANNEE(indiceLigne), annee.toString());
			addZone(getNOM_ST_NB_JOURS(indiceLigne), String.valueOf(vo.getCompteur().getDureeAAjouter().intValue()));
			addZone(getNOM_ST_MOTIF(indiceLigne),  vo.getCompteur().getMotifCompteurDto() == null ? Const.CHAINE_VIDE
					:vo.getCompteur().getMotifCompteurDto().getLibelle());

			indiceLigne++;
			
			listeCompteurTriee.add(vo.getCompteur());
		}
		setListeCompteur(listeCompteurTriee);
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeCompteur().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_VISUALISER
			for (int i = 0; i < getListeCompteur().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ArrayList<CompteurDto> getListeCompteur() {
		return listeCompteur == null ? new ArrayList<CompteurDto>() : listeCompteur;
	}

	public void setListeCompteur(ArrayList<CompteurDto> listeCompteur) {
		this.listeCompteur = listeCompteur;
		Collections.sort(this.listeCompteur);
	}

	public String getNOM_ST_OS(int i) {
		return "NOM_ST_OS" + i;
	}

	public String getVAL_ST_OS(int i) {
		return getZone(getNOM_ST_OS(i));
	}

	public String getNOM_ST_ANNEE(int i) {
		return "NOM_ST_ANNEE" + i;
	}

	public String getVAL_ST_ANNEE(int i) {
		return getZone(getNOM_ST_ANNEE(i));
	}

	public String getNOM_ST_NB_JOURS(int i) {
		return "NOM_ST_NB_JOURS" + i;
	}

	public String getVAL_ST_NB_JOURS(int i) {
		return getZone(getNOM_ST_NB_JOURS(i));
	}

	public String getNOM_ST_MOTIF(int i) {
		return "NOM_ST_MOTIF" + i;
	}

	public String getVAL_ST_MOTIF(int i) {
		return getZone(getNOM_ST_MOTIF(i));
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_NB_JOURS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ANNEE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		addZone(getNOM_LB_OS_SELECT(), Const.ZERO);
	}

	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);

		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(indiceEltAModifier);

		if (!initialiseCompteurCourant(request, compteurCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseCompteurCourant(HttpServletRequest request, CompteurDto dto) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(dto.getDateDebut());
		Integer annee = cal.get(Calendar.YEAR);

		int ligneAnnee = getListeAnnee().indexOf(annee.toString());
		addZone(getNOM_LB_ANNEE_SELECT(), String.valueOf(ligneAnnee));
		addZone(getNOM_ST_NB_JOURS(), String.valueOf(dto.getDureeAAjouter().intValue()));
		addZone(getNOM_ST_ANNEE(), annee.toString());
		int ligneOS = getListeOrganisationSyndicale().indexOf(dto.getOrganisationSyndicaleDto());
		addZone(getNOM_LB_OS_SELECT(), String.valueOf(ligneOS + 1));
		int ligneMotif = getListeMotifCompteur().indexOf(dto.getMotifCompteurDto());
		addZone(getNOM_LB_MOTIF_SELECT(), String.valueOf(ligneMotif + 1));
		return true;
	}

	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);

		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(indiceEltAConsulter);

		if (!initialiseCompteurCourant(request, compteurCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_NB_JOURS() {
		return "NOM_ST_NB_JOURS";
	}

	public String getVAL_ST_NB_JOURS() {
		return getZone(getNOM_ST_NB_JOURS());
	}

	public String getNOM_ST_ANNEE() {
		return "NOM_ST_ANNEE";
	}

	public String getVAL_ST_ANNEE() {
		return getZone(getNOM_ST_ANNEE());
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		// nbjours numerique
		if (!Services.estNumerique(getZone(getNOM_ST_NB_JOURS()))) {
			// "ERR992", "La zone @ doit être numérique.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "nb jours"));
			return false;
		}

		// motif obligatoire
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT())
				: -1);
		if (indiceMotif <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}

		// organisation syndicale obligatoire
		int indiceOS = (Services.estNumerique(getVAL_LB_OS_SELECT()) ? Integer.parseInt(getVAL_LB_OS_SELECT()) : -1);
		if (indiceOS <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "organisation syndicale"));
			return false;
		}

		// annee obligatoire
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer
					.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			if (indiceAnnee < 0) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
				return false;
			}
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			if (getVAL_ST_ANNEE().equals(Const.CHAINE_VIDE)) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
				return false;
			}

		}

		// nb jours obligatoire
		if (getVAL_ST_NB_JOURS().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nb jours"));
			return false;
		}

		return true;
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Vérification de la validité du formulaire
		if (!performControlerChamps(request))
			return false;

		// on recupere l'agent connecte
		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		ReturnMessageDto message = new ReturnMessageDto();

		// motif
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT())
				: -1);
		MotifCompteurDto motif = null;
		if (indiceMotif > 0) {
			motif = getListeMotifCompteur().get(indiceMotif - 1);
		}

		// organisation syndicale
		OrganisationSyndicaleDto os = null;
		int indiceOS = (Services.estNumerique(getVAL_LB_OS_SELECT()) ? Integer.parseInt(getVAL_LB_OS_SELECT()) : -1);
		if (indiceOS > 0) {
			os = getListeOrganisationSyndicale().get(indiceOS - 1);
		}

		// annee
		Integer annee = null;
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer
					.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			if (indiceAnnee >= 0) {
				annee = Integer.valueOf(getListeAnnee().get(indiceAnnee));
			}
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			annee = Integer.valueOf(getVAL_ST_ANNEE());
		}

		CompteurDto compteurDto = new CompteurDto();
		OrganisationSyndicaleDto orgaDto = new OrganisationSyndicaleDto();
		orgaDto.setIdOrganisation(os.getIdOrganisation());
		compteurDto.setOrganisationSyndicaleDto(orgaDto);
		MotifCompteurDto motifDto = new MotifCompteurDto();
		motifDto.setIdMotifCompteur(motif.getIdMotifCompteur());
		compteurDto.setMotifCompteurDto(motifDto);
		compteurDto.setDureeAAjouter(new Double(Integer.valueOf(getVAL_ST_NB_JOURS())));
		compteurDto.setDateDebut(new DateTime(annee, 1, 1, 0, 0, 0).toDate());
		compteurDto.setDateFin(new DateTime(annee, 12, 31, 23, 59, 0).toDate());

		// on sauvegarde
		message = absService.addCompteurAsaA53(agentConnecte.getIdAgent(), new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class).serialize(compteurDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
		} else {
			// "INF010", "Le compteur @ a bien été mis a jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", EnumTypeAbsence.ASA_A53.getValue()));
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
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
			agentConnecte = getAgentDao().chercherAgentParMatricule(
					radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		} catch (Exception e) {
			return null;
		}

		return agentConnecte;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		return true;
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
		return listeAnnee;
	}

	public void setListeAnnee(ArrayList<String> listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	public boolean peutModifierCompteur(int i) {
		CompteurDto compteurCourant = (CompteurDto) getListeCompteur().get(i);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Integer anneeCourante = cal.get(Calendar.YEAR);

		Calendar calDto = Calendar.getInstance();
		calDto.setTime(compteurCourant.getDateDebut());
		Integer anneeDto = calDto.get(Calendar.YEAR);
		if (anneeDto < anneeCourante) {
			return false;
		}
		return true;
	}

	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	public ArrayList<MotifCompteurDto> getListeMotifCompteur() {
		if (listeMotifCompteur == null)
			return new ArrayList<MotifCompteurDto>();
		return listeMotifCompteur;
	}

	public void setListeMotifCompteur(ArrayList<MotifCompteurDto> listeMotifCompteur) {
		this.listeMotifCompteur = listeMotifCompteur;
	}

	private String[] getLB_OS() {
		if (LB_OS == null)
			LB_OS = initialiseLazyLB();
		return LB_OS;
	}

	private void setLB_OS(String[] newLB_OS) {
		LB_OS = newLB_OS;
	}

	public String getNOM_LB_OS() {
		return "NOM_LB_OS";
	}

	public String getNOM_LB_OS_SELECT() {
		return "NOM_LB_OS_SELECT";
	}

	public String[] getVAL_LB_OS() {
		return getLB_OS();
	}

	public String getVAL_LB_OS_SELECT() {
		return getZone(getNOM_LB_OS_SELECT());
	}

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		if (listeOrganisationSyndicale == null)
			return new ArrayList<OrganisationSyndicaleDto>();
		return listeOrganisationSyndicale;
	}

	public void setListeOrganisationSyndicale(ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale) {
		this.listeOrganisationSyndicale = listeOrganisationSyndicale;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
