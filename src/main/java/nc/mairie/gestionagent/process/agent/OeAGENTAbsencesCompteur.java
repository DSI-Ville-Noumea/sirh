package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.CompteurDto;
import nc.mairie.gestionagent.absence.dto.FiltreSoldeDto;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.joda.time.DateTime;

import flexjson.JSONSerializer;

/**
 * Process OeAGENTAbsences Date de cr�ation : (05/09/11 11:31:37)
 * 
 */
public class OeAGENTAbsencesCompteur extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private AgentNW agentCourant;
	private Integer soldeCourantMinute;
	private Integer soldeCourantPrecMinute;

	private String messageInfo = Const.CHAINE_VIDE;

	private String[] LB_TYPE_ABSENCE;
	private ArrayList<EnumTypeAbsence> listeTypeAbsence;
	private EnumTypeAbsence typeAbsenceCourant;

	private String[] LB_MOTIF;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_CREATION_RECUP = "Alimenter le compteur de r�cup�ration";
	public String ACTION_CREATION_REPOS_COMP = "Alimenter le compteur de repos compensateur";

	/**
	 * Constructeur du process OeAGENTAbsences. Date de cr�ation : (05/09/11
	 * 11:39:24)
	 * 
	 */
	public OeAGENTAbsencesCompteur() {
		super();
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// V�rification des droits d'acc�s.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Initialisation des listes d�roulantes
		initialiseListeDeroulante();

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseListeDeroulante() {

		// Si liste Type absence vide alors affectation
		if (getListeTypeAbsence() == null || getListeTypeAbsence().size() == 0) {
			setListeTypeAbsence(EnumTypeAbsence.getValues());

			int[] tailles = { 100 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EnumTypeAbsence> list = getListeTypeAbsence().listIterator(); list.hasNext();) {
				EnumTypeAbsence type = (EnumTypeAbsence) list.next();
				String ligne[] = { type.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_ABSENCE(aFormat.getListeFormatee(false));
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AFFICHER
			if (testerParametre(request, getNOM_PB_AFFICHER())) {
				return performPB_AFFICHER(request);
			}
			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (29/09/11 10:03:37)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTAbsencesCompteur.jsp";
	}

	public String getNomEcran() {
		return "ECR-AG-ABS-CPTEUR";
	}

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	private String[] getLB_TYPE_ABSENCE() {
		if (LB_TYPE_ABSENCE == null)
			LB_TYPE_ABSENCE = initialiseLazyLB();
		return LB_TYPE_ABSENCE;
	}

	private void setLB_TYPE_ABSENCE(String[] newLB_TYPE_ABSENCE) {
		LB_TYPE_ABSENCE = newLB_TYPE_ABSENCE;
	}

	public String getNOM_LB_TYPE_ABSENCE() {
		return "NOM_LB_TYPE_ABSENCE";
	}

	public String getNOM_LB_TYPE_ABSENCE_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE() {
		return getLB_TYPE_ABSENCE();
	}

	public String getVAL_LB_TYPE_ABSENCE_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_SELECT());
	}

	public ArrayList<EnumTypeAbsence> getListeTypeAbsence() {
		return listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<EnumTypeAbsence> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public String getNOM_PB_AFFICHER() {
		return "NOM_PB_AFFICHER";
	}

	public boolean performPB_AFFICHER(HttpServletRequest request) throws Exception {
		setMessageInfo("");
		setTypeAbsenceCourant(null);

		// Recuperation type absence
		EnumTypeAbsence typeAbsence = null;
		int indiceTypeAbsence = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_SELECT()) ? Integer
				.parseInt(getVAL_LB_TYPE_ABSENCE_SELECT()) : -1);
		if (indiceTypeAbsence >= 0) {
			typeAbsence = (EnumTypeAbsence) getListeTypeAbsence().get(indiceTypeAbsence);
			setTypeAbsenceCourant(typeAbsence);
		}

		if (!performVerifRG(request)) {
			return false;
		}

		// Liste depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) consuAbs
				.getListeMotifCompteur(getTypeAbsenceCourant().getCode());
		setListeMotifCompteur(listeMotifs);

		int[] tailles = { 30 };
		String padding[] = { "G" };
		FormateListe aFormat = new FormateListe(tailles, padding, false);
		for (MotifCompteurDto motif : getListeMotifCompteur()) {
			String ligne[] = { motif.getLibelle() };

			aFormat.ajouteLigne(ligne);
		}
		setLB_MOTIF(aFormat.getListeFormatee(false));

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Integer annee = cal.get(Calendar.YEAR);
		Date dateDeb = new DateTime(annee, 1, 1, 0, 0, 0).toDate();
		Date dateFin = new DateTime(annee, 12, 31, 23, 59, 0).toDate();
		FiltreSoldeDto dto = new FiltreSoldeDto();
		dto.setDateDebut(dateDeb);
		dto.setDateFin(dateFin);
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(dto);

		afficheSolde(getTypeAbsenceCourant(), json);

		// On nomme l'action
		if (getTypeAbsenceCourant().equals(EnumTypeAbsence.RECUP)) {
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_RECUP);
		} else if (getTypeAbsenceCourant().equals(EnumTypeAbsence.REPOS_COMP)) {
			addZone(getNOM_RG_COMPTEUR(), getNOM_RB_COMPTEUR_ANNEE());
			addZone(getNOM_ST_ACTION(), ACTION_CREATION_REPOS_COMP);
		} else if (getTypeAbsenceCourant().equals(EnumTypeAbsence.ASA_A48)
				|| getTypeAbsenceCourant().equals(EnumTypeAbsence.ASA_A54)) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setMessageInfo("La gestion de ce compteur se fait dans le menu Election / Saisie des compteurs ASA");

		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}
		return true;
	}

	private boolean performVerifRG(HttpServletRequest request) throws Exception {
		// si l'agent n'est pas contractuel ou convention collectives, alors il
		// n'a pas le droit au repos compensateur

		if (getTypeAbsenceCourant().equals(EnumTypeAbsence.REPOS_COMP)) {
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), getAgentCourant());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "ERR136", "Cet agent n'a aucune carri�re active."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR136"));
				return false;
			}

			if (!(carr.getCodeCategorie().equals("4") || carr.getCodeCategorie().equals("7"))) {
				// "ERR802",
				// "Cet agent n'est ni contractuel ni convention collective, il ne peut avoir de repos compensateur."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR802"));
				return false;
			}
		}
		return true;
	}

	private void afficheSolde(EnumTypeAbsence typeAbsenceCourant, String json) {
		viderZoneSaisie();

		// Solde depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		SoldeDto soldeGlobal = consuAbs.getSoldeAgent(getAgentCourant().getIdAgent(), json);

		switch (typeAbsenceCourant) {
			case CONGE:
				addZone(getNOM_ST_SOLDE(), soldeGlobal.getSoldeCongeAnnee().toString() + " j");
				setSoldeCourantMinute((int) (soldeGlobal.getSoldeCongeAnnee() * 24));
				break;
			case REPOS_COMP:
				int soldeRecupAnnee = soldeGlobal.getSoldeReposCompAnnee().intValue();
				String soldeRecupAnneeHeure = (soldeRecupAnnee / 60) == 0 ? "" : soldeRecupAnnee / 60 + "h ";
				String soldeRecupAnneeMinute = soldeRecupAnnee % 60 + "m";
				int soldeRecupAnneePrec = soldeGlobal.getSoldeReposCompAnneePrec().intValue();
				String soldeRecupAnneePrecHeure = (soldeRecupAnneePrec / 60) == 0 ? "" : soldeRecupAnneePrec / 60
						+ "h ";
				String soldeRecupAnneePrecMinute = soldeRecupAnneePrec % 60 + "m";
				setSoldeCourantMinute(soldeRecupAnnee);
				setSoldeCourantPrecMinute(soldeRecupAnneePrec);

				addZone(getNOM_ST_SOLDE(), soldeRecupAnneeHeure + soldeRecupAnneeMinute);
				addZone(getNOM_ST_SOLDE_PREC(), soldeRecupAnneePrecHeure + soldeRecupAnneePrecMinute);
				break;
			case RECUP:
				int soldeRecup = soldeGlobal.getSoldeRecup().intValue();
				String soldeRecupHeure = (soldeRecup / 60) == 0 ? "" : soldeRecup / 60 + "h ";
				String soldeRecupMinute = soldeRecup % 60 + "m";
				addZone(getNOM_ST_SOLDE(), soldeRecupHeure + soldeRecupMinute);
				setSoldeCourantMinute(soldeRecup);
				break;
			case ASA_A48:
			case ASA_A54:
				break;
			case AUTRE:

				break;
			case MALADIE:

				break;
		}

	}

	private void viderZoneSaisie() {
		addZone(getNOM_ST_SOLDE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SOLDE_PREC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_HEURE_AJOUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_MIN_AJOUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_HEURE_RETRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DUREE_MIN_RETRAIT(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_MOTIF_SELECT(), Const.ZERO);
		addZone(getNOM_RG_COMPTEUR(), Const.CHAINE_VIDE);
	}

	public EnumTypeAbsence getTypeAbsenceCourant() {
		return typeAbsenceCourant;
	}

	public void setTypeAbsenceCourant(EnumTypeAbsence typeAbsenceCourant) {
		this.typeAbsenceCourant = typeAbsenceCourant;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// v�rification de la validit� du formulaire
		if (!performControlerChamps(request))
			return false;

		// on recupere l'agent connect�
		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// on sauvegarde les donn�es
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ReturnMessageDto message = new ReturnMessageDto();

		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION_RECUP)
				&& getTypeAbsenceCourant().equals(EnumTypeAbsence.RECUP)) {
			// on recupere la saisie
			String dureeHeure = null;
			String dureeMin = null;
			boolean ajout = false;
			if (!getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
					|| !getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)) {
				dureeHeure = getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_ST_DUREE_HEURE_AJOUT();
				dureeMin = getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE) ? "0" : getVAL_ST_DUREE_MIN_AJOUT();
				ajout = true;
			}
			if (!getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
					|| !getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
				dureeHeure = getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_ST_DUREE_HEURE_RETRAIT();
				dureeMin = getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_ST_DUREE_MIN_RETRAIT();
				ajout = false;
			}

			// verifier nouveau solde pas n�gatif
			int dureeTotaleSaisie = (Integer.valueOf(dureeHeure) * 60) + (Integer.valueOf(dureeMin));
			int ancienSolde = getSoldeCourantMinute();
			if (ajout) {
				// cas de l'ajout
				if (ancienSolde + dureeTotaleSaisie < 0) {
					// "ERR801",
					// "Le nouveau solde du compteur ne peut �tre n�gatif."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR801"));
					return false;
				}
			} else {
				// cas du retrait
				if (ancienSolde - dureeTotaleSaisie < 0) {
					// "ERR801",
					// "Le nouveau solde du compteur ne peut �tre n�gatif."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR801"));
					return false;
				}
			}

			// motif
			int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer
					.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
			MotifCompteurDto motif = null;
			if (indiceMotif >= 0) {
				motif = getListeMotifCompteur().get(indiceMotif);
			}

			CompteurDto compteurDto = new CompteurDto();
			compteurDto.setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			compteurDto.setIdMotifCompteur(motif.getIdMotifCompteur());
			compteurDto.setDureeAAjouter(ajout ? dureeTotaleSaisie : null);
			compteurDto.setDureeARetrancher(ajout ? null : dureeTotaleSaisie);
			compteurDto.setAnneePrecedente(false);

			// on sauvegarde
			message = consuAbs
					.addCompteurRecup(agentConnecte.getIdAgent(), new JSONSerializer().serialize(compteurDto));

		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION_REPOS_COMP)
				&& getTypeAbsenceCourant().equals(EnumTypeAbsence.REPOS_COMP)) {
			// on recupere la saisie
			String dureeHeure = null;
			String dureeMin = null;
			boolean ajout = false;
			if (!getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
					|| !getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)) {
				dureeHeure = getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_ST_DUREE_HEURE_AJOUT();
				dureeMin = getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE) ? "0" : getVAL_ST_DUREE_MIN_AJOUT();
				ajout = true;
			}
			if (!getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
					|| !getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
				dureeHeure = getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_ST_DUREE_HEURE_RETRAIT();
				dureeMin = getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_ST_DUREE_MIN_RETRAIT();
				ajout = false;
			}

			Boolean anneePrec = getZone(getNOM_RG_COMPTEUR()).equals(getNOM_RB_COMPTEUR_ANNEE_PREC());

			// verifier nouveau solde pas n�gatif
			int dureeTotaleSaisie = (Integer.valueOf(dureeHeure) * 60) + (Integer.valueOf(dureeMin));
			int ancienSolde = 0;
			if (anneePrec) {
				ancienSolde = getSoldeCourantPrecMinute();
			} else {
				ancienSolde = getSoldeCourantMinute();
			}
			if (ajout) {
				// cas de l'ajout
				if (ancienSolde + dureeTotaleSaisie < 0) {
					// "ERR801",
					// "Le nouveau solde du compteur ne peut �tre n�gatif."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR801"));
					return false;
				}
			} else {
				// cas du retrait
				if (ancienSolde - dureeTotaleSaisie < 0) {
					// "ERR801",
					// "Le nouveau solde du compteur ne peut �tre n�gatif."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR801"));
					return false;
				}
			}

			// motif
			int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer
					.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
			MotifCompteurDto motif = null;
			if (indiceMotif >= 0) {
				motif = getListeMotifCompteur().get(indiceMotif);
			}

			CompteurDto compteurDto = new CompteurDto();
			compteurDto.setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			compteurDto.setIdMotifCompteur(motif.getIdMotifCompteur());
			compteurDto.setDureeAAjouter(ajout ? dureeTotaleSaisie : null);
			compteurDto.setDureeARetrancher(ajout ? null : dureeTotaleSaisie);
			compteurDto.setAnneePrecedente(anneePrec);

			// on sauvegarde
			message = consuAbs.addCompteurReposComp(agentConnecte.getIdAgent(),
					new JSONSerializer().serialize(compteurDto));

		}

		if (message.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : message.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
		} else {
			// "INF010", "Le compteur @ a bien �t� mis � jour."
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF010", getTypeAbsenceCourant().getValue()));
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	private AgentNW getAgentConnecte(HttpServletRequest request) throws Exception {
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via la
		// table SIIDMA
		AgentNW agentConnecte = null;
		if (!(u.getUserName().equals("nicno85"))) {
			Siidma user = Siidma.chercherSiidma(getTransaction(), u.getUserName().toUpperCase());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				return null;
			}
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				return null;
			}
		} else {
			agentConnecte = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
		return agentConnecte;
	}

	private boolean performControlerChamps(HttpServletRequest request) {
		// motif obligatoire
		int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT())
				: -1);
		if (indiceMotif < 0) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "motif"));
			return false;
		}

		// dur�e obligatoire (ajout ou retrait)
		if (getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
				&& getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)
				&& getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
				&& getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "dur�e"));
			return false;
		}

		// pas 2 dur�es de saisie
		boolean ajoutSaisie = false;
		if (!getVAL_ST_DUREE_HEURE_AJOUT().equals(Const.CHAINE_VIDE)
				|| !getVAL_ST_DUREE_MIN_AJOUT().equals(Const.CHAINE_VIDE)) {
			ajoutSaisie = true;
		}

		boolean retraitSaisie = false;
		if (!getVAL_ST_DUREE_HEURE_RETRAIT().equals(Const.CHAINE_VIDE)
				|| !getVAL_ST_DUREE_MIN_RETRAIT().equals(Const.CHAINE_VIDE)) {
			retraitSaisie = true;
		}

		if (ajoutSaisie && retraitSaisie) {
			// "ERR800", "Seul un des deux champs dur�e doit �tre renseign�."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR800"));
			return false;
		}

		return true;
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setTypeAbsenceCourant(null);
		viderZoneSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public String getNOM_ST_SOLDE() {
		return "NOM_ST_SOLDE";
	}

	public String getVAL_ST_SOLDE() {
		return getZone(getNOM_ST_SOLDE());
	}

	public String getNOM_ST_SOLDE_PREC() {
		return "NOM_ST_SOLDE_PREC";
	}

	public String getVAL_ST_SOLDE_PREC() {
		return getZone(getNOM_ST_SOLDE_PREC());
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

	public String getNOM_ST_DUREE_HEURE_AJOUT() {
		return "NOM_ST_DUREE_HEURE_AJOUT";
	}

	public String getVAL_ST_DUREE_HEURE_AJOUT() {
		return getZone(getNOM_ST_DUREE_HEURE_AJOUT());
	}

	public String getNOM_ST_DUREE_MIN_AJOUT() {
		return "NOM_ST_DUREE_MIN_AJOUT";
	}

	public String getVAL_ST_DUREE_MIN_AJOUT() {
		return getZone(getNOM_ST_DUREE_MIN_AJOUT());
	}

	public String getNOM_ST_DUREE_HEURE_RETRAIT() {
		return "NOM_ST_DUREE_HEURE_RETRAIT";
	}

	public String getVAL_ST_DUREE_HEURE_RETRAIT() {
		return getZone(getNOM_ST_DUREE_HEURE_RETRAIT());
	}

	public String getNOM_ST_DUREE_MIN_RETRAIT() {
		return "NOM_ST_DUREE_MIN_RETRAIT";
	}

	public String getVAL_ST_DUREE_MIN_RETRAIT() {
		return getZone(getNOM_ST_DUREE_MIN_RETRAIT());
	}

	public Integer getSoldeCourantMinute() {
		return soldeCourantMinute;
	}

	public void setSoldeCourantMinute(Integer soldeCourantMinute) {
		this.soldeCourantMinute = soldeCourantMinute;
	}

	public ArrayList<MotifCompteurDto> getListeMotifCompteur() {
		if (listeMotifCompteur == null)
			return new ArrayList<MotifCompteurDto>();
		return listeMotifCompteur;
	}

	public void setListeMotifCompteur(ArrayList<MotifCompteurDto> listeMotifCompteur) {
		this.listeMotifCompteur = listeMotifCompteur;
	}

	public Integer getSoldeCourantPrecMinute() {
		return soldeCourantPrecMinute;
	}

	public void setSoldeCourantPrecMinute(Integer soldeCourantPrecMinute) {
		this.soldeCourantPrecMinute = soldeCourantPrecMinute;
	}

	public String getNOM_RG_COMPTEUR() {
		return "NOM_RG_COMPTEUR";
	}

	public String getVAL_RG_COMPTEUR() {
		return getZone(getNOM_RG_COMPTEUR());
	}

	public String getNOM_RB_COMPTEUR_ANNEE() {
		return "NOM_RB_COMPTEUR_ANNEE";
	}

	public String getNOM_RB_COMPTEUR_ANNEE_PREC() {
		return "NOM_RB_COMPTEUR_ANNEE_PREC";
	}

	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}
}
