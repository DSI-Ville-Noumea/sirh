package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.RefTypeSaisiDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.droits.Groupe;
import nc.mairie.metier.parametrage.DestinataireMailMaladie;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.droits.GroupeDao;
import nc.mairie.spring.dao.metier.parametrage.DestinataireMailMaladieDao;
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

/**
 * Process OePARAMETRAGEAbsenceMaladies Date de création : (26/11/2015 13:52:54)
 * 
 */
public class OePARAMETRAGEAbsenceMaladies extends BasicProcess {
	/**
	 * 
	 */
	private static final long					serialVersionUID						= 1L;

	public String								focus									= null;
	private ArrayList<TypeAbsenceDto>			listeTypeAbsence;
	private TypeAbsenceDto						typeCreation;

	public String								ACTION_CREATION							= "Création d'une maladie.";
	public String								ACTION_MODIFICATION						= "Modification d'une maladie :";
	public String								ACTION_VISUALISATION					= "Visualisation d'une maladie :";
	public String								ACTION_INACTIVE							= "Désactivation d'une maladie :";

	private AgentDao							agentDao;
	private DestinataireMailMaladieDao			destinataireMailMaladieDao;
	private GroupeDao							groupeDao;

	private IRadiService						radiService;

	private IAbsService							absService;

	// Pour les listes mails des maladies
	private String[]							LB_GROUPE;
	private ArrayList<DestinataireMailMaladie>	listeDestinataireMailMaladie;
	private DestinataireMailMaladie				destinataireCourant;
	private ArrayList<Groupe>					listeGroupe;
	private Groupe								groupeCourant;
	public String								ACTION_CREATION_DESTINATAIRE_MAIL		= "Ajout d'un nouveau groupe dans les destinataires des alertes mail des maladies";
	public String								ACTION_SUPPRESSION_MAIL_DESTINATAIRE	= "Suppression d'un groupe dans les destinataires des alertes mail des maladies";

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		if (getListeTypeAbsence().size() == 0) {
			initialiseListeTypeAbsence(request);
		}

		if (getListeDestinataireMailMaladie().size() == 0) {
			initialiseListeDestinataireMailMaladie(request);
		}

		// Si liste groupe vide alors affectation
		if (getLB_GROUPE() == LBVide) {
			ArrayList<Groupe> listeGroupe = (ArrayList<Groupe>) getGroupeDao().listerGroupe();
			setListeGroupe(listeGroupe);
			if (getListeGroupe().size() != 0) {
				int tailles[] = { 50 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (Groupe gr : getListeGroupe()) {
					String ligne[] = { gr.getLibGroupe() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_GROUPE(aFormat.getListeFormatee(true));
			} else {
				setLB_GROUPE(null);
			}
		}
	}

	private void initialiseListeDestinataireMailMaladie(HttpServletRequest request) {
		try {
			setListeDestinataireMailMaladie(getDestinataireMailMaladieDao().listerDestinataireMailMaladie());
		} catch (Exception e) {
			setListeDestinataireMailMaladie(null);
		}

		for (DestinataireMailMaladie dest : getListeDestinataireMailMaladie()) {
			Integer i = dest.getIdDestinataireMailMaladie();
			// on recupere le groupe
			Groupe gr;
			try {
				gr = getGroupeDao().chercherGroupeById(dest.getIdGroupe());
				addZone(getNOM_ST_GROUPE_DESTINATAIRE_MAIL(i), gr.getLibGroupe());
			} catch (Exception e) {
				addZone(getNOM_ST_GROUPE_DESTINATAIRE_MAIL(i), "Erreur de recuperation du groupe, merci de contacter le responsable du projet.");
			}
		}

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
		if (getDestinataireMailMaladieDao() == null) {
			setDestinataireMailMaladieDao(new DestinataireMailMaladieDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getGroupeDao() == null) {
			setGroupeDao(new GroupeDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void initialiseListeTypeAbsence(HttpServletRequest request) {
		setListeTypeAbsence((ArrayList<TypeAbsenceDto>) absService.getListeRefTypeAbsenceDto(null));

		for (TypeAbsenceDto abs : getListeTypeAbsence()) {
			if (abs.getGroupeAbsence() == null || abs.getGroupeAbsence().getIdRefGroupeAbsence() != EnumTypeGroupeAbsence.MALADIES.getValue()) {
				continue;
			}
			Integer i = abs.getIdRefTypeAbsence();
			addZone(getNOM_ST_TYPE_MALADIE(i), abs.getLibelle());
			String unite = "1 j";
			addZone(getNOM_ST_UNITE(i), unite);
			addZone(getNOM_ST_INFO(i),
					abs.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE : abs.getTypeSaisiDto().getMessageAlerte());
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAbsence. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAbsenceMaladies() {
		super();
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER_MALADIES
			if (testerParametre(request, getNOM_PB_AJOUTER_MALADIES())) {
				return performPB_AJOUTER_MALADIES(request);
			}

			// Si clic sur le bouton PB_VALIDER_MALADIES
			if (testerParametre(request, getNOM_PB_VALIDER_MALADIES())) {
				return performPB_VALIDER_MALADIES(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_MOTIF
			if (testerParametre(request, getNOM_PB_MOTIF())) {
				return performPB_MOTIF(request);
			}

			// Si clic sur le bouton PB_ALERTE
			if (testerParametre(request, getNOM_PB_ALERTE())) {
				return performPB_ALERTE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DESTINATAIRE_MAIL
			if (testerParametre(request, getNOM_PB_AJOUTER_DESTINATAIRE_MAIL())) {
				return performPB_AJOUTER_DESTINATAIRE_MAIL(request);
			}

			// Si clic sur le bouton PB_VALIDER_DESTINATAIRE_MAIL_MALADIE
			if (testerParametre(request, getNOM_PB_VALIDER_DESTINATAIRE_MAIL_MALADIE())) {
				return performPB_VALIDER_DESTINATAIRE_MAIL_MALADIE(request);
			}

			for (int i = 0; i < getListeDestinataireMailMaladie().size(); i++) {
				Integer indice = getListeDestinataireMailMaladie().get(i).getIdDestinataireMailMaladie();
				// Si clic sur le bouton PB_SUPPRIMER_DESTINATAIRE_MAIL
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DESTINATAIRE_MAIL(indice))) {
					return performPB_SUPPRIMER_DESTINATAIRE_MAIL(request, indice);
				}
			}

			// Si clic sur les boutons du tableau
			for (TypeAbsenceDto abs : getListeTypeAbsence()) {
				int indiceAbs = abs.getIdRefTypeAbsence();
				// Si clic sur le bouton PB_MODIFIER_MALADIES
				if (testerParametre(request, getNOM_PB_MODIFIER_MALADIES(indiceAbs))) {
					return performPB_MODIFIER_MALADIES(request, indiceAbs);
				}
				// Si clic sur le bouton PB_VISUALISATION
				if (testerParametre(request, getNOM_PB_VISUALISATION(indiceAbs))) {
					return performPB_VISUALISATION(request, indiceAbs);
				}
				// Si clic sur le bouton PB_INACTIVER
				if (testerParametre(request, getNOM_PB_INACTIVER(indiceAbs))) {
					return performPB_INACTIVER(request, indiceAbs);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEAbsenceMaladies.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-ABS-CONG";
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public boolean performPB_AJOUTER_MALADIES(HttpServletRequest request) throws Exception {
		viderZoneSaisie(request);

		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto();
		groupeDto.setIdRefGroupeAbsence(EnumTypeGroupeAbsence.MALADIES.getValue());

		RefTypeSaisiDto saisieDto = new RefTypeSaisiDto();
		saisieDto.setUniteDecompte("jours");
		saisieDto.setMotif(false);
		saisieDto.setAlerte(false);

		TypeAbsenceDto newType = new TypeAbsenceDto();
		newType.setGroupeAbsence(groupeDto);
		newType.setTypeSaisiDto(saisieDto);

		setTypeCreation(newType);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER_MALADIES(int i) {
		return "NOM_PB_MODIFIER_MALADIES_" + i;
	}

	public boolean performPB_MODIFIER_MALADIES(HttpServletRequest request, int idDemande) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		// on recupere la demande
		TypeAbsenceDto aChercher = new TypeAbsenceDto();
		aChercher.setIdRefTypeAbsence(idDemande);
		TypeAbsenceDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeCreation(type);

		addZone(getNOM_RG_DATE_FIN(), type.getTypeSaisiDto().isCalendarDateFin() ? getNOM_RB_DATE_FIN_OUI() : getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_PIECE_JOINTE(), type.getTypeSaisiDto().isPieceJointe() ? getNOM_RB_PIECE_JOINTE_OUI() : getNOM_RB_PIECE_JOINTE_NON());
		addZone(getNOM_RG_SAISIE_KIOSQUE(),
				type.getTypeSaisiDto().isSaisieKiosque() ? getNOM_RB_SAISIE_KIOSQUE_OUI() : getNOM_RB_SAISIE_KIOSQUE_NON());
		addZone(getNOM_RG_MOTIF(), type.getTypeSaisiDto().isMotif() ? getNOM_RB_MOTIF_OUI() : getNOM_RB_MOTIF_NON());
		addZone(getNOM_RG_ALERTE(), type.getTypeSaisiDto().isAlerte() ? getNOM_RB_ALERTE_OUI() : getNOM_RB_ALERTE_NON());

		addZone(getNOM_ST_DESCRIPTION(),
				type.getTypeSaisiDto().getDescription() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto().getDescription());
		addZone(getNOM_ST_INFO_COMPL(),
				type.getTypeSaisiDto().getInfosComplementaires() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto().getInfosComplementaires());
		addZone(getNOM_ST_MESSAGE_ALERTE(),
				type.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto().getMessageAlerte());

		addZone(getNOM_CK_STATUT_F(), type.getTypeSaisiDto().isFonctionnaire() ? getCHECKED_ON() : getCHECKED_OFF());
		addZone(getNOM_CK_STATUT_C(), type.getTypeSaisiDto().isContractuel() ? getCHECKED_ON() : getCHECKED_OFF());
		addZone(getNOM_CK_STATUT_CC(), type.getTypeSaisiDto().isConventionCollective() ? getCHECKED_ON() : getCHECKED_OFF());

		addZone(getNOM_RG_PRESCRIPTEUR(), type.getTypeSaisiDto().isPrescripteur() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_DATE_DECLARATION(), type.getTypeSaisiDto().isDateDeclaration() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_PROLONGATION(), type.getTypeSaisiDto().isProlongation() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_NOM_ENFANT(), type.getTypeSaisiDto().isNomEnfant() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_NOMBRE_ITT(), type.getTypeSaisiDto().isNombreITT() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_SIEGE_LESION(), type.getTypeSaisiDto().isSiegeLesion() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_AT_REFERENCE(), type.getTypeSaisiDto().isAtReference() ? getNOM_RB_OUI() : getNOM_RB_NON());
		addZone(getNOM_RG_MALADIES_PRO(), type.getTypeSaisiDto().isMaladiePro() ? getNOM_RB_OUI() : getNOM_RB_NON());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	private void viderZoneSaisie(HttpServletRequest request) {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DEBUT_MAM(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_FIN_MAM(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_HEURE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_HEURE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_PIECE_JOINTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_STATUT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_SAISIE_KIOSQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DESCRIPTION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_MESSAGE_ALERTE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_MOTIF(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_COMPL(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIBELLE(), Const.CHAINE_VIDE);

		addZone(getNOM_RG_DATE_FIN(), getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_HEURE_DEBUT(), getNOM_RB_HEURE_DEBUT_OUI());
		addZone(getNOM_RG_HEURE_FIN(), getNOM_RB_HEURE_FIN_NON());
		addZone(getNOM_RG_AM_PM_DEBUT(), getNOM_RB_AM_PM_DEBUT_NON());
		addZone(getNOM_RG_AM_PM_FIN(), getNOM_RB_AM_PM_FIN_NON());
		addZone(getNOM_RG_PIECE_JOINTE(), getNOM_RB_PIECE_JOINTE_NON());
		addZone(getNOM_RG_SAISIE_KIOSQUE(), getNOM_RB_SAISIE_KIOSQUE_NON());
		addZone(getNOM_RG_MOTIF(), getNOM_RB_MOTIF_NON());
		addZone(getNOM_RG_ALERTE(), getNOM_RB_ALERTE_NON());
		addZone(getNOM_CK_STATUT_F(), getCHECKED_OFF());
		addZone(getNOM_CK_STATUT_C(), getCHECKED_OFF());
		addZone(getNOM_CK_STATUT_CC(), getCHECKED_OFF());

		addZone(getNOM_RG_PRESCRIPTEUR(), getNOM_RB_NON());
		addZone(getNOM_RG_DATE_DECLARATION(), getNOM_RB_NON());
		addZone(getNOM_RG_PROLONGATION(), getNOM_RB_NON());
		addZone(getNOM_RG_NOM_ENFANT(), getNOM_RB_NON());
		addZone(getNOM_RG_NOMBRE_ITT(), getNOM_RB_NON());
		addZone(getNOM_RG_SIEGE_LESION(), getNOM_RB_NON());
		addZone(getNOM_RG_AT_REFERENCE(), getNOM_RB_NON());
		addZone(getNOM_RG_MALADIES_PRO(), getNOM_RB_NON());

		setTypeCreation(null);
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);

		// on recupere la demande
		TypeAbsenceDto aChercher = new TypeAbsenceDto();
		aChercher.setIdRefTypeAbsence(indiceEltAConsulter);
		TypeAbsenceDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeCreation(type);

		addZone(getNOM_ST_DATE_DEBUT(), type.getTypeSaisiDto().isCalendarDateDebut() ? "oui" : "non");
		addZone(getNOM_ST_DATE_FIN(), type.getTypeSaisiDto().isCalendarDateFin() ? "oui" : "non");
		addZone(getNOM_ST_HEURE_DEBUT(), type.getTypeSaisiDto().isCalendarHeureDebut() ? "oui" : "non");
		addZone(getNOM_ST_HEURE_FIN(), type.getTypeSaisiDto().isCalendarHeureFin() ? "oui" : "non");
		addZone(getNOM_ST_DEBUT_MAM(), type.getTypeSaisiDto().isChkDateDebut() ? "oui" : "non");
		addZone(getNOM_ST_FIN_MAM(), type.getTypeSaisiDto().isChkDateFin() ? "oui" : "non");
		addZone(getNOM_ST_PIECE_JOINTE(), type.getTypeSaisiDto().isPieceJointe() ? "oui" : "non");
		addZone(getNOM_ST_SAISIE_KIOSQUE(), type.getTypeSaisiDto().isSaisieKiosque() ? "oui" : "non");
		addZone(getNOM_ST_STATUT(), getStatut(type));
		addZone(getNOM_ST_DESCRIPTION(),
				type.getTypeSaisiDto().getDescription() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto().getDescription());
		addZone(getNOM_ST_MESSAGE_ALERTE(),
				type.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto().getMessageAlerte());
		addZone(getNOM_ST_MOTIF(), type.getTypeSaisiDto().isMotif() ? "oui" : "non");
		addZone(getNOM_ST_INFO_COMPL(),
				type.getTypeSaisiDto().getInfosComplementaires() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto().getInfosComplementaires());

		addZone(getNOM_ST_PRESCRIPTEUR(), type.getTypeSaisiDto().isPrescripteur() ? "oui" : "non");
		addZone(getNOM_ST_DATE_DECLARATION(), type.getTypeSaisiDto().isDateDeclaration() ? "oui" : "non");
		addZone(getNOM_ST_PROLONGATION(), type.getTypeSaisiDto().isProlongation() ? "oui" : "non");
		addZone(getNOM_ST_NOM_ENFANT(), type.getTypeSaisiDto().isNomEnfant() ? "oui" : "non");
		addZone(getNOM_ST_NOMBRE_ITT(), type.getTypeSaisiDto().isNombreITT() ? "oui" : "non");
		addZone(getNOM_ST_SIEGE_LESION(), type.getTypeSaisiDto().isSiegeLesion() ? "oui" : "non");
		addZone(getNOM_ST_AT_REFERENCE(), type.getTypeSaisiDto().isAtReference() ? "oui" : "non");
		addZone(getNOM_ST_MALADIE_PRO(), type.getTypeSaisiDto().isMaladiePro() ? "oui" : "non");

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private String getStatut(TypeAbsenceDto type) {
		String statut = Const.CHAINE_VIDE;
		boolean isFirst = true;
		if (type.getTypeSaisiDto().isFonctionnaire()) {
			if (isFirst) {
				statut += "F";
				isFirst = false;
			} else {
				statut += ", F";
			}
		}
		if (type.getTypeSaisiDto().isContractuel()) {
			if (isFirst) {
				statut += "C";
				isFirst = false;
			} else {
				statut += ", C";
			}
		}
		if (type.getTypeSaisiDto().isConventionCollective()) {
			if (isFirst) {
				statut += "CC";
				isFirst = false;
			} else {
				statut += ", CC";
			}
		}
		return statut;
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre
					// identifiant. Merci de contacter le responsable du
					// projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

	public boolean performPB_VALIDER_MALADIES(HttpServletRequest request) throws Exception {
		if (getTypeCreation() == null) {
			// "ERR804",
			// "Une erreur est survenue dans la sauvegarde d'un type d'absence.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR804"));
			return false;
		}

		Agent agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "ERR183",
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}
		if (getVAL_ST_ACTION().equals(ACTION_INACTIVE)) {

			ReturnMessageDto srm = absService.inactiveTypeAbsence(agentConnecte.getIdAgent(), getTypeCreation().getIdRefTypeAbsence());

			if (srm.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : srm.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
				return false;
			}
		} else {

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {
				getTypeCreation().setLibelle(getVAL_ST_LIBELLE());
			}
			getTypeCreation().getTypeSaisiDto().setCalendarDateDebut(true);
			getTypeCreation().getTypeSaisiDto().setCalendarDateFin(getVAL_RG_DATE_FIN().equals(getNOM_RB_DATE_FIN_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setCalendarHeureDebut(getVAL_RG_HEURE_DEBUT().equals(getNOM_RB_HEURE_DEBUT_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setCalendarHeureFin(getVAL_RG_HEURE_FIN().equals(getNOM_RB_HEURE_FIN_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setChkDateDebut(getVAL_RG_AM_PM_DEBUT().equals(getNOM_RB_AM_PM_DEBUT_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setChkDateFin(getVAL_RG_AM_PM_FIN().equals(getNOM_RB_AM_PM_FIN_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setPieceJointe(getVAL_RG_PIECE_JOINTE().equals(getNOM_RB_PIECE_JOINTE_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setFonctionnaire(getVAL_CK_STATUT_F().equals(getCHECKED_ON()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setContractuel(getVAL_CK_STATUT_C().equals(getCHECKED_ON()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setConventionCollective(getVAL_CK_STATUT_CC().equals(getCHECKED_ON()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setSaisieKiosque(getVAL_RG_SAISIE_KIOSQUE().equals(getNOM_RB_SAISIE_KIOSQUE_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setDescription(getVAL_ST_DESCRIPTION().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_DESCRIPTION());
			getTypeCreation().getTypeSaisiDto().setMotif(getVAL_RG_MOTIF().equals(getNOM_RB_MOTIF_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto()
					.setInfosComplementaires(getVAL_ST_INFO_COMPL().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_INFO_COMPL());
			getTypeCreation().getTypeSaisiDto().setAlerte(getVAL_RG_ALERTE().equals(getNOM_RB_ALERTE_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto()
					.setMessageAlerte(getVAL_ST_MESSAGE_ALERTE().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_MESSAGE_ALERTE());

			getTypeCreation().getTypeSaisiDto().setPrescripteur(getVAL_RG_PRESCRIPTEUR().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setDateDeclaration(getVAL_RG_DATE_DECLARATION().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setProlongation(getVAL_RG_PROLONGATION().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setNomEnfant(getVAL_RG_NOM_ENFANT().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setNombreITT(getVAL_RG_NOMBRE_ITT().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setSiegeLesion(getVAL_RG_SIEGE_LESION().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setAtReference(getVAL_RG_AT_REFERENCE().equals(getNOM_RB_OUI()) ? true : false);
			getTypeCreation().getTypeSaisiDto().setMaladiePro(getVAL_RG_MALADIES_PRO().equals(getNOM_RB_OUI()) ? true : false);

			// envoyer l'info aux WS
			String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(getTypeCreation());

			ReturnMessageDto srm = absService.saveTypeAbsence(agentConnecte.getIdAgent(), json);

			if (srm.getErrors().size() > 0) {
				String err = Const.CHAINE_VIDE;
				for (String erreur : srm.getErrors()) {
					err += " " + erreur;
				}
				getTransaction().declarerErreur("ERREUR : " + err);
				return false;
			}
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		// on vide la liste afin qu'elle soit re-affichée
		getListeTypeAbsence().clear();
		return true;
	}

	private boolean performControlerChamps(HttpServletRequest request) {

		if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {
			// libelle obligatoire
			if ((Const.CHAINE_VIDE).equals(getVAL_ST_LIBELLE())) {
				// "ERR002", "La zone @ est obligatoire."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
				return false;
			}
		}

		// description obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_DESCRIPTION())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "description"));
			return false;
		}

		// statuts obligatoire
		if (getVAL_CK_STATUT_F().equals(getCHECKED_OFF()) && getVAL_CK_STATUT_C().equals(getCHECKED_OFF())
				&& getVAL_CK_STATUT_CC().equals(getCHECKED_OFF())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "statuts"));
			return false;
		}
		return true;
	}

	public boolean performPB_MOTIF(HttpServletRequest request) throws Exception {
		// motif / info complémentaires
		if (getVAL_RG_MOTIF().equals(getNOM_RB_MOTIF_OUI())) {
			getTypeCreation().getTypeSaisiDto().setMotif(true);
		} else {
			getTypeCreation().getTypeSaisiDto().setMotif(false);
		}

		return true;
	}

	public boolean performPB_ALERTE(HttpServletRequest request) throws Exception {
		// alerte depassement lors saisie
		if (getVAL_RG_ALERTE().equals(getNOM_RB_ALERTE_OUI())) {
			getTypeCreation().getTypeSaisiDto().setAlerte(true);
		} else {
			getTypeCreation().getTypeSaisiDto().setAlerte(false);
		}

		return true;
	}

	public boolean performPB_INACTIVER(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);

		// on recupere la demande
		TypeAbsenceDto aChercher = new TypeAbsenceDto();
		aChercher.setIdRefTypeAbsence(indiceEltASupprimer);
		TypeAbsenceDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeCreation(type);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_INACTIVE);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_ST_LIBELLE() {
		return "NOM_ST_LIBELLE";
	}

	public String getVAL_ST_LIBELLE() {
		return getZone(getNOM_ST_LIBELLE());
	}

	public String getNOM_PB_INACTIVER(int i) {
		return "NOM_PB_INACTIVER" + i;
	}

	public String getNOM_RG_AM_PM_DEBUT() {
		return "NOM_RG_AM_PM_DEBUT";
	}

	public String getVAL_RG_AM_PM_DEBUT() {
		return getZone(getNOM_RG_AM_PM_DEBUT());
	}

	public String getNOM_RB_AM_PM_DEBUT_OUI() {
		return "NOM_RB_AM_PM_DEBUT_OUI";
	}

	public String getNOM_RB_AM_PM_DEBUT_NON() {
		return "NOM_RB_AM_PM_DEBUT_NON";
	}

	public String getNOM_RG_AM_PM_FIN() {
		return "NOM_RG_AM_PM_FIN";
	}

	public String getVAL_RG_AM_PM_FIN() {
		return getZone(getNOM_RG_AM_PM_FIN());
	}

	public String getNOM_RB_AM_PM_FIN_OUI() {
		return "NOM_RB_AM_PM_FIN_OUI";
	}

	public String getNOM_RB_AM_PM_FIN_NON() {
		return "NOM_RB_AM_PM_FIN_NON";
	}

	public String getNOM_RG_PIECE_JOINTE() {
		return "NOM_RG_PIECE_JOINTE";
	}

	public String getVAL_RG_PIECE_JOINTE() {
		return getZone(getNOM_RG_PIECE_JOINTE());
	}

	public String getNOM_RB_PIECE_JOINTE_OUI() {
		return "NOM_RB_PIECE_JOINTE_OUI";
	}

	public String getNOM_RB_PIECE_JOINTE_NON() {
		return "NOM_RB_PIECE_JOINTE_NON";
	}

	public String getNOM_RG_SAISIE_KIOSQUE() {
		return "NOM_RG_SAISIE_KIOSQUE";
	}

	public String getVAL_RG_SAISIE_KIOSQUE() {
		return getZone(getNOM_RG_SAISIE_KIOSQUE());
	}

	public String getNOM_RB_SAISIE_KIOSQUE_OUI() {
		return "NOM_RB_SAISIE_KIOSQUE_OUI";
	}

	public String getNOM_RB_SAISIE_KIOSQUE_NON() {
		return "NOM_RB_SAISIE_KIOSQUE_NON";
	}

	public String getNOM_RG_MOTIF() {
		return "NOM_RG_MOTIF";
	}

	public String getVAL_RG_MOTIF() {
		return getZone(getNOM_RG_MOTIF());
	}

	public String getNOM_RB_MOTIF_OUI() {
		return "NOM_RB_MOTIF_OUI";
	}

	public String getNOM_RB_MOTIF_NON() {
		return "NOM_RB_MOTIF_NON";
	}

	public String getNOM_PB_MOTIF() {
		return "NOM_PB_MOTIF";
	}

	public String getNOM_CK_STATUT_F() {
		return "NOM_CK_STATUT_F";
	}

	public String getVAL_CK_STATUT_F() {
		return getZone(getNOM_CK_STATUT_F());
	}

	public String getNOM_CK_STATUT_C() {
		return "NOM_CK_STATUT_C";
	}

	public String getVAL_CK_STATUT_C() {
		return getZone(getNOM_CK_STATUT_C());
	}

	public String getNOM_CK_STATUT_CC() {
		return "NOM_CK_STATUT_CC";
	}

	public String getVAL_CK_STATUT_CC() {
		return getZone(getNOM_CK_STATUT_CC());
	}

	public String getNOM_RG_ALERTE() {
		return "NOM_RG_ALERTE";
	}

	public String getVAL_RG_ALERTE() {
		return getZone(getNOM_RG_ALERTE());
	}

	public String getNOM_RB_ALERTE_OUI() {
		return "NOM_RB_ALERTE_OUI";
	}

	public String getNOM_RB_ALERTE_NON() {
		return "NOM_RB_ALERTE_NON";
	}

	public String getNOM_PB_ALERTE() {
		return "NOM_PB_ALERTE";
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
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

	public String getNOM_ST_DEBUT_MAM() {
		return "NOM_ST_DEBUT_MAM";
	}

	public String getVAL_ST_DEBUT_MAM() {
		return getZone(getNOM_ST_DEBUT_MAM());
	}

	public String getNOM_ST_FIN_MAM() {
		return "NOM_ST_FIN_MAM";
	}

	public String getVAL_ST_FIN_MAM() {
		return getZone(getNOM_ST_FIN_MAM());
	}

	public String getNOM_ST_HEURE_DEBUT() {
		return "NOM_ST_HEURE_DEBUT";
	}

	public String getVAL_ST_HEURE_DEBUT() {
		return getZone(getNOM_ST_HEURE_DEBUT());
	}

	public String getNOM_ST_HEURE_FIN() {
		return "NOM_ST_HEURE_FIN";
	}

	public String getVAL_ST_HEURE_FIN() {
		return getZone(getNOM_ST_HEURE_FIN());
	}

	public String getNOM_ST_PIECE_JOINTE() {
		return "NOM_ST_PIECE_JOINTE";
	}

	public String getVAL_ST_PIECE_JOINTE() {
		return getZone(getNOM_ST_PIECE_JOINTE());
	}

	public String getNOM_ST_STATUT() {
		return "NOM_ST_STATUT";
	}

	public String getVAL_ST_STATUT() {
		return getZone(getNOM_ST_STATUT());
	}

	public String getNOM_ST_SAISIE_KIOSQUE() {
		return "NOM_ST_SAISIE_KIOSQUE";
	}

	public String getVAL_ST_SAISIE_KIOSQUE() {
		return getZone(getNOM_ST_SAISIE_KIOSQUE());
	}

	public String getNOM_ST_DESCRIPTION() {
		return "NOM_ST_DESCRIPTION";
	}

	public String getVAL_ST_DESCRIPTION() {
		return getZone(getNOM_ST_DESCRIPTION());
	}

	public String getNOM_ST_MESSAGE_ALERTE() {
		return "NOM_ST_MESSAGE_ALERTE";
	}

	public String getVAL_ST_MESSAGE_ALERTE() {
		return getZone(getNOM_ST_MESSAGE_ALERTE());
	}

	public String getNOM_ST_MOTIF() {
		return "NOM_ST_MOTIF";
	}

	public String getVAL_ST_MOTIF() {
		return getZone(getNOM_ST_MOTIF());
	}

	public String getNOM_ST_INFO_COMPL() {
		return "NOM_ST_INFO_COMPL";
	}

	public String getVAL_ST_INFO_COMPL() {
		return getZone(getNOM_ST_INFO_COMPL());
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public String getNOM_RG_DATE_FIN() {
		return "NOM_RG_DATE_FIN";
	}

	public String getVAL_RG_DATE_FIN() {
		return getZone(getNOM_RG_DATE_FIN());
	}

	public String getNOM_RB_DATE_FIN_OUI() {
		return "NOM_RB_DATE_FIN_OUI";
	}

	public String getNOM_RB_DATE_FIN_NON() {
		return "NOM_RB_DATE_FIN_NON";
	}

	public String getNOM_RG_HEURE_DEBUT() {
		return "NOM_RG_HEURE_DEBUT";
	}

	public String getVAL_RG_HEURE_DEBUT() {
		return getZone(getNOM_RG_HEURE_DEBUT());
	}

	public String getNOM_RB_HEURE_DEBUT_OUI() {
		return "NOM_RB_HEURE_DEBUT_OUI";
	}

	public String getNOM_RB_HEURE_DEBUT_NON() {
		return "NOM_RB_HEURE_DEBUT_NON";
	}

	public String getNOM_RG_HEURE_FIN() {
		return "NOM_RG_HEURE_FIN";
	}

	public String getVAL_RG_HEURE_FIN() {
		return getZone(getNOM_RG_HEURE_FIN());
	}

	public String getNOM_RB_HEURE_FIN_OUI() {
		return "NOM_RB_HEURE_FIN_OUI";
	}

	public String getNOM_RB_HEURE_FIN_NON() {
		return "NOM_RB_HEURE_FIN_NON";
	}

	public String getNOM_PB_UNITE_DECOMPTE() {
		return "NOM_PB_UNITE_DECOMPTE";
	}

	public String getNOM_PB_VALIDER_MALADIES() {
		return "NOM_PB_VALIDER_MALADIES";
	}

	public TypeAbsenceDto getTypeCreation() {
		return typeCreation;
	}

	public void setTypeCreation(TypeAbsenceDto typeCreation) {
		this.typeCreation = typeCreation;
	}

	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	public ArrayList<TypeAbsenceDto> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<TypeAbsenceDto>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<TypeAbsenceDto> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public String getNOM_ST_TYPE_MALADIE(int i) {
		return "NOM_ST_TYPE_MALADIE_" + i;
	}

	public String getVAL_ST_TYPE_MALADIE(int i) {
		return getZone(getNOM_ST_TYPE_MALADIE(i));
	}

	public String getNOM_ST_UNITE(int i) {
		return "NOM_ST_UNITE_" + i;
	}

	public String getVAL_ST_UNITE(int i) {
		return getZone(getNOM_ST_UNITE(i));
	}

	public String getNOM_ST_INFO(int i) {
		return "NOM_ST_INFO_" + i;
	}

	public String getVAL_ST_INFO(int i) {
		return getZone(getNOM_ST_INFO(i));
	}

	public String getNOM_PB_AJOUTER_MALADIES() {
		return "NOM_PB_AJOUTER_MALADIES";
	}

	public String getNOM_ST_PRESCRIPTEUR() {
		return "NOM_ST_PRESCRIPTEUR";
	}

	public String getVAL_ST_PRESCRIPTEUR() {
		return getZone(getNOM_ST_PRESCRIPTEUR());
	}

	public String getNOM_ST_DATE_DECLARATION() {
		return "NOM_ST_DATE_DECLARATION";
	}

	public String getVAL_ST_DATE_DECLARATION() {
		return getZone(getNOM_ST_DATE_DECLARATION());
	}

	public String getNOM_ST_PROLONGATION() {
		return "NOM_ST_PROLONGATION";
	}

	public String getVAL_ST_PROLONGATION() {
		return getZone(getNOM_ST_PROLONGATION());
	}

	public String getNOM_ST_NOM_ENFANT() {
		return "NOM_ST_NOM_ENFANT";
	}

	public String getVAL_ST_NOM_ENFANT() {
		return getZone(getNOM_ST_NOM_ENFANT());
	}

	public String getNOM_ST_NOMBRE_ITT() {
		return "NOM_ST_NOMBRE_ITT";
	}

	public String getVAL_ST_NOMBRE_ITT() {
		return getZone(getNOM_ST_NOMBRE_ITT());
	}

	public String getNOM_ST_SIEGE_LESION() {
		return "NOM_ST_SIEGE_LESION";
	}

	public String getVAL_ST_SIEGE_LESION() {
		return getZone(getNOM_ST_SIEGE_LESION());
	}

	public String getNOM_ST_AT_REFERENCE() {
		return "NOM_ST_AT_REFERENCE";
	}

	public String getVAL_ST_AT_REFERENCE() {
		return getZone(getNOM_ST_AT_REFERENCE());
	}

	public String getNOM_ST_MALADIE_PRO() {
		return "NOM_ST_MALADIE_PRO";
	}

	public String getVAL_ST_MALADIE_PRO() {
		return getZone(getNOM_ST_MALADIE_PRO());
	}

	public String getNOM_RG_PRESCRIPTEUR() {
		return "NOM_RG_PRESCRIPTEUR";
	}

	public String getVAL_RG_PRESCRIPTEUR() {
		return getZone(getNOM_RG_PRESCRIPTEUR());
	}

	public String getNOM_RG_DATE_DECLARATION() {
		return "NOM_RG_DATE_DECLARATION";
	}

	public String getVAL_RG_DATE_DECLARATION() {
		return getZone(getNOM_RG_DATE_DECLARATION());
	}

	public String getNOM_RG_PROLONGATION() {
		return "NOM_RG_PROLONGATION";
	}

	public String getVAL_RG_PROLONGATION() {
		return getZone(getNOM_RG_PROLONGATION());
	}

	public String getNOM_RG_NOM_ENFANT() {
		return "NOM_RG_NOM_ENFANT";
	}

	public String getVAL_RG_NOM_ENFANT() {
		return getZone(getNOM_RG_NOM_ENFANT());
	}

	public String getNOM_RG_NOMBRE_ITT() {
		return "NOM_RG_NOMBRE_ITT";
	}

	public String getVAL_RG_NOMBRE_ITT() {
		return getZone(getNOM_RG_NOMBRE_ITT());
	}

	public String getNOM_RG_SIEGE_LESION() {
		return "NOM_RG_SIEGE_LESION";
	}

	public String getVAL_RG_SIEGE_LESION() {
		return getZone(getNOM_RG_SIEGE_LESION());
	}

	public String getNOM_RG_MALADIES_PRO() {
		return "NOM_RG_MALADIES_PRO";
	}

	public String getVAL_RG_MALADIES_PRO() {
		return getZone(getNOM_RG_MALADIES_PRO());
	}

	public String getNOM_RG_AT_REFERENCE() {
		return "NOM_RG_AT_REFERENCE";
	}

	public String getVAL_RG_AT_REFERENCE() {
		return getZone(getNOM_RG_AT_REFERENCE());
	}

	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	public ArrayList<DestinataireMailMaladie> getListeDestinataireMailMaladie() {
		return listeDestinataireMailMaladie == null ? new ArrayList<DestinataireMailMaladie>() : listeDestinataireMailMaladie;
	}

	public void setListeDestinataireMailMaladie(ArrayList<DestinataireMailMaladie> listeDestinataireMailMaladie) {
		this.listeDestinataireMailMaladie = listeDestinataireMailMaladie;
	}

	public String getNOM_PB_SUPPRIMER_DESTINATAIRE_MAIL(int i) {
		return "NOM_PB_SUPPRIMER_DESTINATAIRE_MAIL" + i;
	}

	public boolean performPB_SUPPRIMER_DESTINATAIRE_MAIL(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		DestinataireMailMaladie dest = getDestinataireMailMaladieDao().chercherDestinataireMailMaladieById(indiceEltASupprimer);
		setDestinataireCourant(dest);
		setGroupeCourant(getGroupeDao().chercherGroupeById(dest.getIdGroupe()));

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION_MAIL_DESTINATAIRE);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public DestinataireMailMaladieDao getDestinataireMailMaladieDao() {
		return destinataireMailMaladieDao;
	}

	public void setDestinataireMailMaladieDao(DestinataireMailMaladieDao destinataireMailMaladieDao) {
		this.destinataireMailMaladieDao = destinataireMailMaladieDao;
	}

	public String getNOM_ST_GROUPE_DESTINATAIRE_MAIL(int i) {
		return "NOM_ST_GROUPE_DESTINATAIRE_MAIL_" + i;
	}

	public String getVAL_ST_GROUPE_DESTINATAIRE_MAIL(int i) {
		return getZone(getNOM_ST_GROUPE_DESTINATAIRE_MAIL(i));
	}

	public String getNOM_PB_AJOUTER_DESTINATAIRE_MAIL() {
		return "NOM_PB_AJOUTER_DESTINATAIRE_MAIL";
	}

	public boolean performPB_AJOUTER_DESTINATAIRE_MAIL(HttpServletRequest request) throws Exception {

		setDestinataireCourant(new DestinataireMailMaladie());
		setGroupeCourant(new Groupe());

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION_DESTINATAIRE_MAIL);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public GroupeDao getGroupeDao() {
		return groupeDao;
	}

	public void setGroupeDao(GroupeDao groupeDao) {
		this.groupeDao = groupeDao;
	}

	public DestinataireMailMaladie getDestinataireCourant() {
		return destinataireCourant;
	}

	public void setDestinataireCourant(DestinataireMailMaladie destinataireCourant) {
		this.destinataireCourant = destinataireCourant;
	}

	public Groupe getGroupeCourant() {
		return groupeCourant;
	}

	public void setGroupeCourant(Groupe groupeCourant) {
		this.groupeCourant = groupeCourant;
	}

	public String getNOM_PB_VALIDER_DESTINATAIRE_MAIL_MALADIE() {
		return "NOM_PB_VALIDER_DESTINATAIRE_MAIL_MALADIE";
	}

	public boolean performPB_VALIDER_DESTINATAIRE_MAIL_MALADIE(HttpServletRequest request) throws Exception {

		if (getDestinataireCourant() == null) {
			// "ERR009", "Une erreur s'est produite sur la base de données."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR009"));
			return false;
		}

		if (getVAL_ST_ACTION().equals(ACTION_SUPPRESSION_MAIL_DESTINATAIRE)) {
			getDestinataireMailMaladieDao().supprimerDestinataireMailMaladie(getDestinataireCourant().getIdDestinataireMailMaladie());

		} else if (getVAL_ST_ACTION().equals(ACTION_CREATION_DESTINATAIRE_MAIL)) {
			// on recupere le groupe selectionné

			int numLigneGroupe = (Services.estNumerique(getZone(getNOM_LB_GROUPE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_GROUPE_SELECT()))
					: -1);

			if (numLigneGroupe == 0 || getListeGroupe().isEmpty() || numLigneGroupe > getListeGroupe().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "groupe"));
				return false;
			}

			Groupe groupe = (Groupe) getListeGroupe().get(numLigneGroupe - 1);

			getDestinataireMailMaladieDao().creerDestinataireMailMaladie(groupe.getIdGroupe());
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// on vide la liste afin qu'elle soit re-affichée
		getListeDestinataireMailMaladie().clear();
		return true;
	}

	private String[] getLB_GROUPE() {
		if (LB_GROUPE == null)
			LB_GROUPE = initialiseLazyLB();
		return LB_GROUPE;
	}

	private void setLB_GROUPE(String[] newLB_GROUPE) {
		LB_GROUPE = newLB_GROUPE;
	}

	public String getNOM_LB_GROUPE() {
		return "NOM_LB_GROUPE";
	}

	public String getNOM_LB_GROUPE_SELECT() {
		return "NOM_LB_GROUPE_SELECT";
	}

	public String[] getVAL_LB_GROUPE() {
		return getLB_GROUPE();
	}

	public String getVAL_LB_GROUPE_SELECT() {
		return getZone(getNOM_LB_GROUPE_SELECT());
	}

	public ArrayList<Groupe> getListeGroupe() {
		return listeGroupe;
	}

	public void setListeGroupe(ArrayList<Groupe> listeGroupe) {
		this.listeGroupe = listeGroupe;
	}

}
