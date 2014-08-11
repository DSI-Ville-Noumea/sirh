package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.RefGroupeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.RefTypeSaisiDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.absence.dto.UnitePeriodeQuotaDto;
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
import flexjson.JSONSerializer;

/**
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEAbsenceConges extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private ArrayList<TypeAbsenceDto> listeTypeAbsence;
	private TypeAbsenceDto typeCreation;
	private ArrayList<String> listeUniteDecompte;
	private ArrayList<UnitePeriodeQuotaDto> listeTypeQuota;
	private String[] LB_UNITE_DECOMPTE;
	private String[] LB_TYPE_QUOTA;

	public String ACTION_CREATION = "Création d'un congé exceptionnel.";
	public String ACTION_MODIFICATION = "Modification d'un congé exceptionnel.";
	public String ACTION_VISUALISATION = "Visualisation d'un congé exceptionnel :";

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
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseListeDeroulante();

		if (getListeTypeAbsence().size() == 0) {
			initialiseListeTypeAbsence(request);
		}

	}

	private void initialiseListeDeroulante() {
		// Si liste unité décompte vide alors affectation
		if (getLB_UNITE_DECOMPTE() == LBVide) {
			ArrayList<String> sf = new ArrayList<String>();
			sf.add("minutes");
			sf.add("jours");
			setListeUniteDecompte(sf);
			int[] tailles = { 20 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = getListeUniteDecompte().listIterator(); list.hasNext();) {
				String u = list.next();
				String ligne[] = { u };

				aFormat.ajouteLigne(ligne);
			}
			setLB_UNITE_DECOMPTE(aFormat.getListeFormatee());
		}
		// Si liste type quota vide alors affectation
		if (getLB_TYPE_QUOTA() == LBVide) {
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeTypeQuota((ArrayList<UnitePeriodeQuotaDto>) consuAbs.getUnitePeriodeQuota());

			int[] tailles = { 20 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<UnitePeriodeQuotaDto> list = getListeTypeQuota().listIterator(); list.hasNext();) {
				UnitePeriodeQuotaDto u = list.next();
				String ligne[] = { u.getValeur() + " " + u.getUnite()
						+ (u.isGlissant() ? " glissant" : Const.CHAINE_VIDE) };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_QUOTA(aFormat.getListeFormatee(true));
		}
	}

	private void initialiseListeTypeAbsence(HttpServletRequest request) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		setListeTypeAbsence((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto());

		for (TypeAbsenceDto abs : getListeTypeAbsence()) {
			if (abs.getGroupeAbsence() == null
					|| abs.getGroupeAbsence().getIdRefGroupeAbsence() != EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
				continue;
			}
			Integer i = abs.getIdRefTypeAbsence();
			addZone(getNOM_ST_TYPE_CONGE(i), abs.getLibelle());
			String unite = Const.CHAINE_VIDE;
			if (abs.getTypeSaisiDto().isChkDateDebut()) {
				unite = "0,5 j";
			} else if (abs.getTypeSaisiDto().isCalendarHeureDebut()) {
				unite = "1 h";
			} else if (abs.getTypeSaisiDto().isCalendarDateDebut()) {
				unite = "1 j";
			}
			addZone(getNOM_ST_UNITE(i), unite);
			addZone(getNOM_ST_INFO(i), abs.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE : abs
					.getTypeSaisiDto().getMessageAlerte());
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAbsence. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAbsenceConges() {
		super();
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER_CONGES
			if (testerParametre(request, getNOM_PB_AJOUTER_CONGES())) {
				return performPB_AJOUTER_CONGES(request);
			}

			// Si clic sur le bouton PB_VALIDER_CONGES
			if (testerParametre(request, getNOM_PB_VALIDER_CONGES())) {
				return performPB_VALIDER_CONGES(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_UNITE_DECOMPTE
			if (testerParametre(request, getNOM_PB_UNITE_DECOMPTE())) {
				return performPB_UNITE_DECOMPTE(request);
			}

			// Si clic sur le bouton PB_MOTIF
			if (testerParametre(request, getNOM_PB_MOTIF())) {
				return performPB_MOTIF(request);
			}

			// Si clic sur le bouton PB_ALERTE
			if (testerParametre(request, getNOM_PB_ALERTE())) {
				return performPB_ALERTE(request);
			}

			// Si clic sur les boutons du tableau
			for (TypeAbsenceDto abs : getListeTypeAbsence()) {
				int indiceAbs = abs.getIdRefTypeAbsence();
				// Si clic sur le bouton PB_MODIFIER_CONGES
				if (testerParametre(request, getNOM_PB_MODIFIER_CONGES(indiceAbs))) {
					return performPB_MODIFIER_CONGES(request, indiceAbs);
				}
				// Si clic sur le bouton PB_VISUALISATION
				if (testerParametre(request, getNOM_PB_VISUALISATION(indiceAbs))) {
					return performPB_VISUALISATION(request, indiceAbs);
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGEAbsenceConges.jsp";
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
		return "";
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public ArrayList<TypeAbsenceDto> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<TypeAbsenceDto>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<TypeAbsenceDto> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public String getNOM_ST_TYPE_CONGE(int i) {
		return "NOM_ST_TYPE_CONGE_" + i;
	}

	public String getVAL_ST_TYPE_CONGE(int i) {
		return getZone(getNOM_ST_TYPE_CONGE(i));
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

	public String getNOM_PB_AJOUTER_CONGES() {
		return "NOM_PB_AJOUTER_CONGES";
	}

	public boolean performPB_AJOUTER_CONGES(HttpServletRequest request) throws Exception {
		viderZoneSaisie(request);

		RefGroupeAbsenceDto groupeDto = new RefGroupeAbsenceDto();
		groupeDto.setIdRefGroupeAbsence(EnumTypeGroupeAbsence.CONGES_EXCEP.getValue());

		RefTypeSaisiDto saisieDto = new RefTypeSaisiDto();
		saisieDto.setUniteDecompte("minutes");
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

	public String getNOM_PB_MODIFIER_CONGES(int i) {
		return "NOM_PB_MODIFIER_CONGES_" + i;
	}

	public boolean performPB_MODIFIER_CONGES(HttpServletRequest request, int idDemande) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		// on recupere la demande
		TypeAbsenceDto aChercher = new TypeAbsenceDto();
		aChercher.setIdRefTypeAbsence(idDemande);
		TypeAbsenceDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		setTypeCreation(type);

		addZone(getNOM_RG_DATE_FIN(), type.getTypeSaisiDto().isCalendarDateFin() ? getNOM_RB_DATE_FIN_OUI()
				: getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_HEURE_DEBUT(), type.getTypeSaisiDto().isCalendarHeureDebut() ? getNOM_RB_HEURE_DEBUT_OUI()
				: getNOM_RB_HEURE_DEBUT_NON());
		addZone(getNOM_RG_HEURE_FIN(), type.getTypeSaisiDto().isCalendarHeureFin() ? getNOM_RB_HEURE_FIN_OUI()
				: getNOM_RB_HEURE_FIN_NON());
		addZone(getNOM_RG_AM_PM_DEBUT(), type.getTypeSaisiDto().isChkDateDebut() ? getNOM_RB_AM_PM_DEBUT_OUI()
				: getNOM_RB_AM_PM_DEBUT_NON());
		addZone(getNOM_RG_AM_PM_FIN(), type.getTypeSaisiDto().isChkDateFin() ? getNOM_RB_AM_PM_FIN_OUI()
				: getNOM_RB_AM_PM_FIN_NON());
		addZone(getNOM_RG_PIECE_JOINTE(), type.getTypeSaisiDto().isPieceJointe() ? getNOM_RB_PIECE_JOINTE_OUI()
				: getNOM_RB_PIECE_JOINTE_NON());
		addZone(getNOM_RG_SAISIE_KIOSQUE(), type.getTypeSaisiDto().isSaisieKiosque() ? getNOM_RB_SAISIE_KIOSQUE_OUI()
				: getNOM_RB_SAISIE_KIOSQUE_NON());
		addZone(getNOM_RG_MOTIF(), type.getTypeSaisiDto().isMotif() ? getNOM_RB_MOTIF_OUI() : getNOM_RB_MOTIF_NON());
		addZone(getNOM_RG_ALERTE(), type.getTypeSaisiDto().isAlerte() ? getNOM_RB_ALERTE_OUI() : getNOM_RB_ALERTE_NON());

		addZone(getNOM_ST_DESCRIPTION(), type.getTypeSaisiDto().getDescription() == null ? Const.CHAINE_VIDE : type
				.getTypeSaisiDto().getDescription());
		addZone(getNOM_ST_INFO_COMPL(), type.getTypeSaisiDto().getInfosComplementaires() == null ? Const.CHAINE_VIDE
				: type.getTypeSaisiDto().getInfosComplementaires());
		addZone(getNOM_ST_MESSAGE_ALERTE(), type.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE
				: type.getTypeSaisiDto().getMessageAlerte());
		addZone(getNOM_ST_QUOTA(), type.getTypeSaisiDto().getQuotaMax() == null ? Const.CHAINE_VIDE : type
				.getTypeSaisiDto().getQuotaMax().toString());

		addZone(getNOM_CK_STATUT_F(), type.getTypeSaisiDto().isFonctionnaire() ? getCHECKED_ON() : getCHECKED_OFF());
		addZone(getNOM_CK_STATUT_C(), type.getTypeSaisiDto().isContractuel() ? getCHECKED_ON() : getCHECKED_OFF());
		addZone(getNOM_CK_STATUT_CC(), type.getTypeSaisiDto().isConventionCollective() ? getCHECKED_ON()
				: getCHECKED_OFF());

		// Quota
		if (type.getTypeSaisiDto().getUnitePeriodeQuotaDto() != null) {
			UnitePeriodeQuotaDto uniteQuota = getTypeQuotaConcerne(type.getTypeSaisiDto().getUnitePeriodeQuotaDto()
					.getIdRefUnitePeriodeQuota());

			addZone(getNOM_LB_TYPE_QUOTA_SELECT(), String.valueOf(getListeTypeQuota().indexOf(uniteQuota) + 1));
		} else {
			addZone(getNOM_LB_TYPE_QUOTA_SELECT(), Const.ZERO);
		}

		// Unite Decompte
		int indiceUnite = 0;
		for (int i = 0; i < getListeUniteDecompte().size(); i++) {
			if (getListeUniteDecompte().get(i).equals(type.getTypeSaisiDto().getUniteDecompte())) {
				indiceUnite = i;
				break;
			}
		}
		addZone(getNOM_LB_UNITE_DECOMPTE_SELECT(), String.valueOf(indiceUnite));

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
		addZone(getNOM_ST_QUOTA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_LIBELLE(), Const.CHAINE_VIDE);

		addZone(getNOM_RG_DATE_FIN(), getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_HEURE_DEBUT(), getNOM_RB_HEURE_DEBUT_NON());
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

		addZone(getNOM_LB_UNITE_DECOMPTE_SELECT(), Const.ZERO);
		addZone(getNOM_LB_TYPE_QUOTA_SELECT(), Const.ZERO);

		setTypeCreation(null);
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

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
		addZone(getNOM_ST_DESCRIPTION(), type.getTypeSaisiDto().getDescription() == null ? Const.CHAINE_VIDE : type
				.getTypeSaisiDto().getDescription());
		addZone(getNOM_ST_MESSAGE_ALERTE(), type.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE
				: type.getTypeSaisiDto().getMessageAlerte());
		addZone(getNOM_ST_MOTIF(), type.getTypeSaisiDto().isMotif() ? "oui" : "non");
		addZone(getNOM_ST_INFO_COMPL(), type.getTypeSaisiDto().getInfosComplementaires() == null ? Const.CHAINE_VIDE
				: type.getTypeSaisiDto().getInfosComplementaires());
		String nbQuota = type.getTypeSaisiDto().getQuotaMax() == null ? Const.CHAINE_VIDE : type.getTypeSaisiDto()
				.getQuotaMax().toString();
		if (type.getTypeSaisiDto().getUnitePeriodeQuotaDto() != null) {
			UnitePeriodeQuotaDto uniteQuota = getTypeQuotaConcerne(type.getTypeSaisiDto().getUnitePeriodeQuotaDto()
					.getIdRefUnitePeriodeQuota());
			addZone(getNOM_ST_QUOTA(), nbQuota + " sur " + uniteQuota.getValeur() + " " + uniteQuota.getUnite()
					+ (uniteQuota.isGlissant() ? " glissant" : Const.CHAINE_VIDE));
		} else {
			addZone(getNOM_ST_QUOTA(), nbQuota);
		}

		// Unite Decompte
		int indiceUnite = 0;
		for (int i = 0; i < getListeUniteDecompte().size(); i++) {
			if (getListeUniteDecompte().get(i).equals(type.getTypeSaisiDto().getUniteDecompte())) {
				indiceUnite = i;
				break;
			}
		}
		addZone(getNOM_LB_UNITE_DECOMPTE_SELECT(), String.valueOf(indiceUnite));

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);
		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private UnitePeriodeQuotaDto getTypeQuotaConcerne(Integer idRefUnitePeriodeQuota) {
		for (int i = 0; i < getListeTypeQuota().size(); i++) {
			if (getListeTypeQuota().get(i).getIdRefUnitePeriodeQuota().toString()
					.equals(idRefUnitePeriodeQuota.toString())) {
				return getListeTypeQuota().get(i);
			}
		}
		return null;
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

	public String getNOM_ST_QUOTA() {
		return "NOM_ST_QUOTA";
	}

	public String getVAL_ST_QUOTA() {
		return getZone(getNOM_ST_QUOTA());
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		return true;
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

	public boolean performPB_UNITE_DECOMPTE(HttpServletRequest request) throws Exception {
		// unite decompte
		int indiceUnite = (Services.estNumerique(getVAL_LB_UNITE_DECOMPTE_SELECT()) ? Integer
				.parseInt(getVAL_LB_UNITE_DECOMPTE_SELECT()) : -1);
		String unite = getListeUniteDecompte().get(indiceUnite);
		getTypeCreation().getTypeSaisiDto().setUniteDecompte(unite);
		if (unite.equals("minutes")) {
			addZone(getNOM_RG_HEURE_DEBUT(), getNOM_RB_HEURE_DEBUT_OUI());
		} else if (unite.equals("jours")) {
			addZone(getNOM_RG_HEURE_DEBUT(), getNOM_RB_HEURE_DEBUT_NON());
			addZone(getNOM_RG_HEURE_FIN(), getNOM_RB_HEURE_FIN_NON());
		}

		return true;
	}

	private String[] getLB_UNITE_DECOMPTE() {
		if (LB_UNITE_DECOMPTE == null) {
			LB_UNITE_DECOMPTE = initialiseLazyLB();
		}
		return LB_UNITE_DECOMPTE;
	}

	private void setLB_UNITE_DECOMPTE(String[] newLB_UNITE_DECOMPTE) {
		LB_UNITE_DECOMPTE = newLB_UNITE_DECOMPTE;
	}

	public String getNOM_LB_UNITE_DECOMPTE() {
		return "NOM_LB_UNITE_DECOMPTE";
	}

	public String getNOM_LB_UNITE_DECOMPTE_SELECT() {
		return "NOM_LB_UNITE_DECOMPTE_SELECT";
	}

	public String[] getVAL_LB_UNITE_DECOMPTE() {
		return getLB_UNITE_DECOMPTE();
	}

	public String getVAL_LB_UNITE_DECOMPTE_SELECT() {
		return getZone(getNOM_LB_UNITE_DECOMPTE_SELECT());
	}

	public ArrayList<String> getListeUniteDecompte() {
		return listeUniteDecompte;
	}

	public void setListeUniteDecompte(ArrayList<String> listeUniteDecompte) {
		this.listeUniteDecompte = listeUniteDecompte;
	}

	public String getNOM_PB_VALIDER_CONGES() {
		return "NOM_PB_VALIDER_CONGES";
	}

	private AgentNW getAgentConnecte(HttpServletRequest request) throws Exception {
		AgentNW agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		if (!uUser.getUserName().equals("nicno85") && !uUser.getUserName().equals("rebjo84")) {
			// on fait la correspondance entre le login et l'agent via RADI
			RadiWSConsumer radiConsu = new RadiWSConsumer();
			LightUserDto user = radiConsu.getAgentCompteADByLogin(uUser.getUserName());
			if (user == null) {
				getTransaction().traiterErreur();
				// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
				return null;
			} else {
				if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
					agent = AgentNW.chercherAgentParMatricule(getTransaction(),
							radiConsu.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
						return null;
					}
				}
			}
		} else {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
		}
		return agent;
	}

	public boolean performPB_VALIDER_CONGES(HttpServletRequest request) throws Exception {
		if (getTypeCreation() == null) {
			// "ERR804",
			// "Une erreur est survenue dans la sauvegarde d'un type d'absence. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR804"));
			return false;
		}

		AgentNW agentConnecte = getAgentConnecte(request);
		if (agentConnecte == null) {
			// "ERR183",
			// "Votre login ne nous permet pas de trouver votre identifiant. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return false;
		}

		// vérification de la validité du formulaire
		if (!performControlerChamps(request))
			return false;

		if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {
			getTypeCreation().setLibelle(getVAL_ST_LIBELLE());
		}
		getTypeCreation().getTypeSaisiDto().setCalendarDateDebut(true);
		getTypeCreation().getTypeSaisiDto().setCalendarDateFin(
				getVAL_RG_DATE_FIN().equals(getNOM_RB_DATE_FIN_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setCalendarHeureDebut(
				getVAL_RG_HEURE_DEBUT().equals(getNOM_RB_HEURE_DEBUT_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setCalendarHeureFin(
				getVAL_RG_HEURE_FIN().equals(getNOM_RB_HEURE_FIN_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setChkDateDebut(
				getVAL_RG_AM_PM_DEBUT().equals(getNOM_RB_AM_PM_DEBUT_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setChkDateFin(
				getVAL_RG_AM_PM_FIN().equals(getNOM_RB_AM_PM_FIN_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setPieceJointe(
				getVAL_RG_PIECE_JOINTE().equals(getNOM_RB_PIECE_JOINTE_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setFonctionnaire(
				getVAL_CK_STATUT_F().equals(getCHECKED_ON()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setContractuel(getVAL_CK_STATUT_C().equals(getCHECKED_ON()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setConventionCollective(
				getVAL_CK_STATUT_CC().equals(getCHECKED_ON()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setSaisieKiosque(
				getVAL_RG_SAISIE_KIOSQUE().equals(getNOM_RB_SAISIE_KIOSQUE_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setDescription(
				getVAL_ST_DESCRIPTION().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_DESCRIPTION());
		getTypeCreation().getTypeSaisiDto().setMotif(getVAL_RG_MOTIF().equals(getNOM_RB_MOTIF_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setInfosComplementaires(
				getVAL_ST_INFO_COMPL().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_INFO_COMPL());
		getTypeCreation().getTypeSaisiDto().setAlerte(getVAL_RG_ALERTE().equals(getNOM_RB_ALERTE_OUI()) ? true : false);
		getTypeCreation().getTypeSaisiDto().setMessageAlerte(
				getVAL_ST_MESSAGE_ALERTE().equals(Const.CHAINE_VIDE) ? null : getVAL_ST_MESSAGE_ALERTE());
		getTypeCreation().getTypeSaisiDto().setQuotaMax(Integer.valueOf(getVAL_ST_QUOTA()));

		// etat
		int numEtat = (Services.estNumerique(getZone(getNOM_LB_TYPE_QUOTA_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_TYPE_QUOTA_SELECT())) : -1);
		UnitePeriodeQuotaDto unitePeriodeQuotaDto = null;
		if (numEtat != -1 && numEtat != 0) {
			unitePeriodeQuotaDto = getListeTypeQuota().get(numEtat - 1);
		}
		getTypeCreation().getTypeSaisiDto().setUnitePeriodeQuotaDto(unitePeriodeQuotaDto);

		// envoyer l'info aux WS
		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(getTypeCreation());

		SirhAbsWSConsumer t = new SirhAbsWSConsumer();
		ReturnMessageDto srm = t.saveTypeAbsence(agentConnecte.getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur(err);
			return false;
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

		// quota obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_ST_QUOTA())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "quota"));
			return false;
		}

		// format QUOTA
		if (!(Const.CHAINE_VIDE).equals(getVAL_ST_QUOTA()) && !Services.estNumerique(getVAL_ST_QUOTA())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "quota"));
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

	public boolean performPB_MOTIF(HttpServletRequest request) throws Exception {
		// motif / info complémentaires
		if (getVAL_RG_MOTIF().equals(getNOM_RB_MOTIF_OUI())) {
			getTypeCreation().getTypeSaisiDto().setMotif(true);
		} else {
			getTypeCreation().getTypeSaisiDto().setMotif(false);
		}

		return true;
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

	public boolean performPB_ALERTE(HttpServletRequest request) throws Exception {
		// alerte depassement lors saisie
		if (getVAL_RG_ALERTE().equals(getNOM_RB_ALERTE_OUI())) {
			getTypeCreation().getTypeSaisiDto().setAlerte(true);
		} else {
			getTypeCreation().getTypeSaisiDto().setAlerte(false);
		}

		return true;
	}

	private String[] getLB_TYPE_QUOTA() {
		if (LB_TYPE_QUOTA == null) {
			LB_TYPE_QUOTA = initialiseLazyLB();
		}
		return LB_TYPE_QUOTA;
	}

	private void setLB_TYPE_QUOTA(String[] newLB_TYPE_QUOTA) {
		LB_TYPE_QUOTA = newLB_TYPE_QUOTA;
	}

	public String getNOM_LB_TYPE_QUOTA() {
		return "NOM_LB_TYPE_QUOTA";
	}

	public String getNOM_LB_TYPE_QUOTA_SELECT() {
		return "NOM_LB_TYPE_QUOTA_SELECT";
	}

	public String[] getVAL_LB_TYPE_QUOTA() {
		return getLB_TYPE_QUOTA();
	}

	public String getVAL_LB_TYPE_QUOTA_SELECT() {
		return getZone(getNOM_LB_TYPE_QUOTA_SELECT());
	}

	public ArrayList<UnitePeriodeQuotaDto> getListeTypeQuota() {
		return listeTypeQuota;
	}

	public void setListeTypeQuota(ArrayList<UnitePeriodeQuotaDto> listeTypeQuota) {
		this.listeTypeQuota = listeTypeQuota;
	}

	public String getNOM_ST_LIBELLE() {
		return "NOM_ST_LIBELLE";
	}

	public String getVAL_ST_LIBELLE() {
		return getZone(getNOM_ST_LIBELLE());
	}
}
