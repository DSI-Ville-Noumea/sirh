package nc.mairie.gestionagent.process.election;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.CompteurAsaDto;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
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
import nc.mairie.utils.VariablesActivite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.JSONSerializer;

/**
 *
 */
public class OeELECSaisieCompteurA55 extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(OeELECSaisieCompteurA55.class);

	public static final int STATUT_RECHERCHER_AGENT_CREATE = 1;

	private ArrayList<CompteurAsaDto> listeCompteur;
	private String[] LB_MOTIF;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_MODIFICATION = "Modification d'un compteur.";
	public String ACTION_CREATION = "Création d'un compteur.";
	public String ACTION_VISUALISATION = "Consultation d'un compteur.";

	@Override
	public String getJSP() {
		return "OeELECSaisieCompteurA55.jsp";
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

		if (etatStatut() == STATUT_RECHERCHER_AGENT_CREATE) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (agt != null) {
				addZone(getNOM_ST_AGENT_CREATE(), agt.getNoMatricule());
			}
		}

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		initialiseListeCompteur(request);
	}

	private void initialiseListeDeroulante() {
		// Si liste motifs vide alors affectation
		if (getLB_MOTIF() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) consuAbs
					.getListeMotifCompteur(EnumTypeAbsence.ASA_A55.getCode());
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
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private void initialiseListeCompteur(HttpServletRequest request) throws Exception {
		SirhAbsWSConsumer consum = new SirhAbsWSConsumer();
		ArrayList<CompteurAsaDto> listeCompteur = (ArrayList<CompteurAsaDto>) consum.getListeCompteursA55();
		logger.debug("Taille liste des compteurs ASA A55 : " + listeCompteur.size());
		setListeCompteur(listeCompteur);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int indiceLigne = 0;
		for (CompteurAsaDto dto : getListeCompteur()) {

			AgentNW ag = AgentNW.chercherAgent(getTransaction(), dto.getIdAgent().toString());

			addZone(getNOM_ST_MATRICULE(indiceLigne), ag.getNoMatricule());
			addZone(getNOM_ST_AGENT(indiceLigne), ag.getNomAgent() + " " + ag.getPrenomAgent());
			addZone(getNOM_ST_DATE_DEBUT(indiceLigne), sdf.format(dto.getDateDebut()));
			addZone(getNOM_ST_DATE_FIN(indiceLigne), sdf.format(dto.getDateFin()));
			String soldeAsaA55Heure = (dto.getNb().intValue() / 60) == 0 ? "" : dto.getNb().intValue() / 60 + "h ";
			String soldeAsaA55Minute = (dto.getNb().intValue() % 60) == 0 ? "&nbsp;" : dto.getNb().intValue() % 60
					+ "m";
			addZone(getNOM_ST_NB_HEURES(indiceLigne), soldeAsaA55Heure + soldeAsaA55Minute);

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

			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_CREATE())) {
				return performPB_RECHERCHER_AGENT_CREATE(request);
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

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
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
		addZone(getNOM_ST_AGENT_CREATE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
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

		addZone(getNOM_ST_DATE_DEBUT(), sdf.format(dto.getDateDebut()));
		addZone(getNOM_ST_DATE_FIN(), sdf.format(dto.getDateFin()));
		String soldeAsaA55Heure = (dto.getNb().intValue() / 60) == 0 ? "" : dto.getNb().intValue() / 60 + "";
		String soldeAsaA55Minute = (dto.getNb().intValue() % 60) == 0 ? "" : "." + dto.getNb().intValue() % 60;
		addZone(getNOM_ST_NB_HEURES(), soldeAsaA55Heure + soldeAsaA55Minute);
		addZone(getNOM_ST_AGENT_CREATE(), dto.getIdAgent().toString()
				.substring(3, dto.getIdAgent().toString().length()));
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

	public String getNOM_ST_AGENT_CREATE() {
		return "NOM_ST_AGENT_CREATE";
	}

	public String getVAL_ST_AGENT_CREATE() {
		return getZone(getNOM_ST_AGENT_CREATE());
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

	public String getNOM_PB_RECHERCHER_AGENT_CREATE() {
		return "NOM_PB_RECHERCHER_AGENT_CREATE";
	}

	public boolean performPB_RECHERCHER_AGENT_CREATE(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());
		setStatut(STATUT_RECHERCHER_AGENT_CREATE, true);
		return true;
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

		// idAgent numerique
		if (!Services.estNumerique(getZone(getNOM_ST_AGENT_CREATE()))) {
			// "ERR992", "La zone @ doit être numérique.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "agent"));
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

		// agent obligatoire
		if (getVAL_ST_AGENT_CREATE().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "agent"));
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
		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les données
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ReturnMessageDto message = new ReturnMessageDto();

		// on recupere la saisie
		String nomatr = getVAL_ST_AGENT_CREATE();
		AgentNW agCompteur = AgentNW.chercherAgentParMatricule(getTransaction(), nomatr);

		// motif
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT())
				: -1);
		MotifCompteurDto motif = null;
		if (indiceMotif > 0) {
			motif = getListeMotifCompteur().get(indiceMotif - 1);
		}
		String dateDeb = getVAL_ST_DATE_DEBUT() + " 00:00:00";
		String dateFin = getVAL_ST_DATE_FIN() + " 23:59:59";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		CompteurDto compteurDto = new CompteurDto();
		compteurDto.setIdAgent(Integer.valueOf(agCompteur.getIdAgent()));
		compteurDto.setIdMotifCompteur(motif.getIdMotifCompteur());
		compteurDto.setDureeAAjouter(Integer.valueOf(getVAL_ST_NB_HEURES()) * 60);
		compteurDto.setDateDebut(sdf.parse(dateDeb));
		compteurDto.setDateFin(sdf.parse(dateFin));

		// on sauvegarde
		message = consuAbs.addCompteurAsaA55(agentConnecte.getIdAgent(), new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class).serialize(compteurDto));

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
		} else {
			// "INF010", "Le compteur @ a bien été mis à jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", EnumTypeAbsence.ASA_A55.getValue()));
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	private AgentNW getAgentConnecte(HttpServletRequest request) throws Exception {
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		AgentNW agentConnecte = null;
		if (!(u.getUserName().equals("nicno85"))) {
			// on fait la correspondance entre le login et l'agent via RADI
			RadiWSConsumer radiConsu = new RadiWSConsumer();
			LightUserDto user = radiConsu.getAgentCompteADByLogin(u.getUserName());
			if (user == null) {
				return null;
			}
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(),
					radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				return null;
			}
		} else {
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
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
}
