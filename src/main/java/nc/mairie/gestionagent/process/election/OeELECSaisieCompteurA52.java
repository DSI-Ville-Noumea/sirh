package nc.mairie.gestionagent.process.election;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.CompteurAsaDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
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
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

/**
 *
 */
public class OeELECSaisieCompteurA52 extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(OeELECSaisieCompteurA52.class);

	private ArrayList<CompteurAsaDto> listeCompteur;
	private ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale;
	private String[] LB_OS;
	private String[] LB_MOTIF;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_MODIFICATION = "Modification d'un compteur.";
	public String ACTION_CREATION = "Création d'un compteur.";
	public String ACTION_VISUALISATION = "Consultation d'un compteur.";

	private AgentDao agentDao;

	@Override
	public String getJSP() {
		return "OeELECSaisieCompteurA52.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-ELEC-COMPTEUR";
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

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		initialiseListeCompteur(request);
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeDeroulante() {
		// Si liste motifs vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) consuAbs
					.getListeMotifCompteur(EnumTypeAbsence.ASA_A52.getCode());
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
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			ArrayList<OrganisationSyndicaleDto> listeOrga = (ArrayList<OrganisationSyndicaleDto>) consuAbs
					.getListeOrganisationSyndicale();
			setListeOrganisationSyndicale(listeOrga);

			int[] tailles = { 50 };
			FormateListe aFormat = new FormateListe(tailles);
			for (OrganisationSyndicaleDto os : listeOrga) {
				String ligne[] = { os.getLibelle() };
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
		SirhAbsWSConsumer consum = new SirhAbsWSConsumer();
		ArrayList<CompteurAsaDto> listeCompteur = (ArrayList<CompteurAsaDto>) consum.getListeCompteursA52();
		logger.debug("Taille liste des compteurs ASA A52 : " + listeCompteur.size());
		setListeCompteur(listeCompteur);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int indiceLigne = 0;
		for (CompteurAsaDto dto : getListeCompteur()) {

			addZone(getNOM_ST_OS(indiceLigne), dto.getOrganisationSyndicaleDto().getSigle());
			addZone(getNOM_ST_DATE_DEBUT(indiceLigne), sdf.format(dto.getDateDebut()));
			addZone(getNOM_ST_DATE_FIN(indiceLigne), sdf.format(dto.getDateFin()));
			String soldeAsaA52Heure = (dto.getNb().intValue() / 60) == 0 ? Const.CHAINE_VIDE : dto.getNb().intValue()
					/ 60 + "h ";
			String soldeAsaA52Minute = (dto.getNb().intValue() % 60) == 0 ? "&nbsp;" : dto.getNb().intValue() % 60
					+ "m";
			addZone(getNOM_ST_NB_HEURES(indiceLigne), soldeAsaA52Heure + soldeAsaA52Minute);

			indiceLigne++;

		}
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

	public ArrayList<CompteurAsaDto> getListeCompteur() {
		return listeCompteur == null ? new ArrayList<CompteurAsaDto>() : listeCompteur;
	}

	public void setListeCompteur(ArrayList<CompteurAsaDto> listeCompteur) {
		this.listeCompteur = listeCompteur;
	}

	public String getNOM_ST_OS(int i) {
		return "NOM_ST_OS" + i;
	}

	public String getVAL_ST_OS(int i) {
		return getZone(getNOM_ST_OS(i));
	}

	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_ST_NB_HEURES(int i) {
		return "NOM_ST_NB_HEURES" + i;
	}

	public String getVAL_ST_NB_HEURES(int i) {
		return getZone(getNOM_ST_NB_HEURES(i));
	}

	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_NB_HEURES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
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

		CompteurAsaDto compteurCourant = (CompteurAsaDto) getListeCompteur().get(indiceEltAModifier);

		if (!initialiseCompteurCourant(request, compteurCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseCompteurCourant(HttpServletRequest request, CompteurAsaDto dto) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int ligneOS = getListeOrganisationSyndicale().indexOf(dto.getOrganisationSyndicaleDto());
		addZone(getNOM_LB_OS_SELECT(), String.valueOf(ligneOS + 1));
		addZone(getNOM_ST_DATE_DEBUT(), sdf.format(dto.getDateDebut()));
		addZone(getNOM_ST_DATE_FIN(), sdf.format(dto.getDateFin()));
		String soldeAsaA52Heure = (dto.getNb().intValue() / 60) == 0 ? Const.CHAINE_VIDE : dto.getNb().intValue() / 60
				+ Const.CHAINE_VIDE;
		String soldeAsaA52Minute = (dto.getNb().intValue() % 60) == 0 ? Const.CHAINE_VIDE : "."
				+ dto.getNb().intValue() % 60;
		addZone(getNOM_ST_NB_HEURES(), soldeAsaA52Heure + soldeAsaA52Minute);
		return true;
	}

	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		videZonesDeSaisie(request);

		CompteurAsaDto compteurCourant = (CompteurAsaDto) getListeCompteur().get(indiceEltAConsulter);

		if (!initialiseCompteurCourant(request, compteurCourant))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_NB_HEURES() {
		return "NOM_ST_NB_HEURES";
	}

	public String getVAL_ST_NB_HEURES() {
		return getZone(getNOM_ST_NB_HEURES());
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

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		// nbheures numerique
		if (!Services.estNumerique(getZone(getNOM_ST_NB_HEURES()))) {
			// "ERR992", "La zone @ doit être numérique.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "nb heures"));
			return false;
		}

		// organisation syndicale obligatoire
		int indiceOS = (Services.estNumerique(getVAL_LB_OS_SELECT()) ? Integer.parseInt(getVAL_LB_OS_SELECT()) : -1);
		if (indiceOS <= 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "organisation syndicale"));
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

		// date debut obligatoire
		if (getVAL_ST_DATE_DEBUT().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}

		// date fin obligatoire
		if (getVAL_ST_DATE_FIN().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de fin"));
			return false;
		}

		// nb heures obligatoire
		if (getVAL_ST_NB_HEURES().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "nb heures"));
			return false;
		}

		return true;
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// vérification de la validité du formulaire
		if (!performControlerChamps(request))
			return false;

		// on recupere l'agent connecté
		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
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

		String dateDeb = getVAL_ST_DATE_DEBUT() + " 00:00:00";
		String dateFin = getVAL_ST_DATE_FIN() + " 23:59:59";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		CompteurDto compteurDto = new CompteurDto();
		compteurDto.setIdOrganisationSyndicale(os.getIdOrganisation());
		compteurDto.setIdMotifCompteur(motif.getIdMotifCompteur());
		compteurDto.setDureeAAjouter(Integer.valueOf(getVAL_ST_NB_HEURES()) * 60);
		compteurDto.setDateDebut(sdf.parse(dateDeb));
		compteurDto.setDateFin(sdf.parse(dateFin));

		// on sauvegarde
		message = consuAbs.addCompteurAsaA52(agentConnecte.getIdAgent(), new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class).serialize(compteurDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
		} else {
			// "INF010", "Le compteur @ a bien été mis à jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", EnumTypeAbsence.ASA_A52.getValue()));
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
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

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	public boolean peutModifierCompteur(int i) {
		CompteurAsaDto compteurCourant = (CompteurAsaDto) getListeCompteur().get(i);
		if (compteurCourant.getDateFin().compareTo(new Date()) < 0) {
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

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisationSyndicale() {
		if (listeOrganisationSyndicale == null)
			return new ArrayList<OrganisationSyndicaleDto>();
		return listeOrganisationSyndicale;
	}

	public void setListeOrganisationSyndicale(ArrayList<OrganisationSyndicaleDto> listeOrganisationSyndicale) {
		this.listeOrganisationSyndicale = listeOrganisationSyndicale;
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

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}
}
