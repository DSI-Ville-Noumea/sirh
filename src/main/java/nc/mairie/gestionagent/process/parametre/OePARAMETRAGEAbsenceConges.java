package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

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
	private String uniteDecompte;
	private ArrayList<String> listeUniteDecompte;
	private String[] LB_UNITE_DECOMPTE;

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

		addZone(getNOM_RG_DATE_FIN(), getNOM_RB_DATE_FIN_NON());
		addZone(getNOM_RG_HEURE_DEBUT(), getNOM_RB_HEURE_DEBUT_NON());
		addZone(getNOM_RG_HEURE_FIN(), getNOM_RB_HEURE_FIN_NON());

		addZone(getNOM_LB_UNITE_DECOMPTE_SELECT(), Const.ZERO);

		setTypeCreation(null);
		setUniteDecompte(null);
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
		addZone(getNOM_ST_QUOTA(), type.getTypeSaisiDto().getUnitePeriodeQuotaDto() == null ? Const.CHAINE_VIDE : type
				.getTypeSaisiDto().getUnitePeriodeQuotaDto().getIdRefUnitePeriodeQuota().toString());

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
		setUniteDecompte(unite);
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

	public String getUniteDecompte() {
		return uniteDecompte;
	}

	public void setUniteDecompte(String uniteDecompte) {
		this.uniteDecompte = uniteDecompte;
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

	public boolean performPB_VALIDER_CONGES(HttpServletRequest request) {
		if(getTypeCreation()==null){
			//TODO declarer erreur
			return false;
		}
		
		
		
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		viderZoneSaisie(request);
		return true;
	}
}
