package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifRefusDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import flexjson.JSONSerializer;

/**
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEAbsence extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;

	private String[] LB_MOTIF_REFUS;
	private ArrayList<MotifRefusDto> listeMotifRefus;

	private String[] LB_TYPE_ABSENCE_REFUS;
	private String[] LB_TYPE_ABSENCE_COMPTEUR;
	private ArrayList<EnumTypeAbsence> listeTypeAbsence;

	private String[] LB_MOTIF_COMPTEUR;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

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

		if (getListeMotifRefus().size() == 0) {
			initialiseListeMotifRefus(request);
		}

		if (getListeMotifCompteur().size() == 0) {
			initialiseListeMotifCompteur(request);
		}

		if (getListeTypeAbsence().size() == 0) {
			initialiseListeTypeAbsence(request);
		}

	}

	private void initialiseListeMotifCompteur(HttpServletRequest request) {
		// Liste depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ArrayList<MotifCompteurDto> listeMotifs = (ArrayList<MotifCompteurDto>) consuAbs.getListeMotifCompteur(null);

		setListeMotifCompteur(listeMotifs);
		if (getListeMotifCompteur().size() != 0) {
			int tailles[] = { 40, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (MotifCompteurDto motif : getListeMotifCompteur()) {
				String type = EnumTypeAbsence.getValueEnumTypeAbsence(motif.getIdRefTypeAbsence());
				String ligne[] = { motif.getLibelle(), type };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF_COMPTEUR(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF_COMPTEUR(null);
		}
	}

	private void initialiseListeTypeAbsence(HttpServletRequest request) {

		// Si liste Type absence vide alors affectation
		if (getListeTypeAbsence() == null || getListeTypeAbsence().size() == 0) {
			setListeTypeAbsence(EnumTypeAbsence.getValues());

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<EnumTypeAbsence> list = getListeTypeAbsence().listIterator(); list.hasNext();) {
				EnumTypeAbsence type = (EnumTypeAbsence) list.next();
				String ligne[] = { type.getValue() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_TYPE_ABSENCE_REFUS(aFormat.getListeFormatee(false));
			setLB_TYPE_ABSENCE_COMPTEUR(aFormat.getListeFormatee(false));
		}

	}

	private void initialiseListeMotifRefus(HttpServletRequest request) throws Exception {
		// Liste depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ArrayList<MotifRefusDto> listeMotifs = (ArrayList<MotifRefusDto>) consuAbs.getListeMotifRefus(null);

		setListeMotifRefus(listeMotifs);
		if (getListeMotifRefus().size() != 0) {
			int tailles[] = { 40, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (MotifRefusDto motif : getListeMotifRefus()) {
				String type = EnumTypeAbsence.getValueEnumTypeAbsence(motif.getIdRefTypeAbsence());
				String ligne[] = { motif.getLibelle(), type };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF_REFUS(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF_REFUS(null);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAbsence. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAbsence() {
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

			// Si clic sur le bouton PB_ANNULER_MOTIF_REFUS
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF_REFUS())) {
				return performPB_ANNULER_MOTIF_REFUS(request);
			}

			// Si clic sur le bouton PB_CREER_MOTIF_REFUS
			if (testerParametre(request, getNOM_PB_CREER_MOTIF_REFUS())) {
				return performPB_CREER_MOTIF_REFUS(request);
			}

			// Si clic sur le bouton PB_MODIFIER_MOTIF_REFUS
			if (testerParametre(request, getNOM_PB_MODIFIER_MOTIF_REFUS())) {
				return performPB_MODIFIER_MOTIF_REFUS(request);
			}

			// Si clic sur le bouton PB_VALIDER_MOTIF_REFUS
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF_REFUS())) {
				return performPB_VALIDER_MOTIF_REFUS(request);
			}

			// Si clic sur le bouton PB_ANNULER_MOTIF_COMPTEUR
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF_COMPTEUR())) {
				return performPB_ANNULER_MOTIF_COMPTEUR(request);
			}

			// Si clic sur le bouton PB_CREER_MOTIF_COMPTEUR
			if (testerParametre(request, getNOM_PB_CREER_MOTIF_COMPTEUR())) {
				return performPB_CREER_MOTIF_COMPTEUR(request);
			}

			// Si clic sur le bouton PB_MODIFIER_MOTIF_COMPTEUR
			if (testerParametre(request, getNOM_PB_MODIFIER_MOTIF_COMPTEUR())) {
				return performPB_MODIFIER_MOTIF_COMPTEUR(request);
			}

			// Si clic sur le bouton PB_VALIDER_MOTIF_COMPTEUR
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF_COMPTEUR())) {
				return performPB_VALIDER_MOTIF_COMPTEUR(request);
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
		return "OePARAMETRAGEAbsence.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-ABS";
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

	private String[] getLB_MOTIF_REFUS() {
		if (LB_MOTIF_REFUS == null)
			LB_MOTIF_REFUS = initialiseLazyLB();
		return LB_MOTIF_REFUS;
	}

	private void setLB_MOTIF_REFUS(String[] newLB_MOTIF_REFUS) {
		LB_MOTIF_REFUS = newLB_MOTIF_REFUS;
	}

	public String getNOM_LB_MOTIF_REFUS() {
		return "NOM_LB_MOTIF_REFUS";
	}

	public String getNOM_LB_MOTIF_REFUS_SELECT() {
		return "NOM_LB_MOTIF_REFUS_SELECT";
	}

	public String[] getVAL_LB_MOTIF_REFUS() {
		return getLB_MOTIF_REFUS();
	}

	public String getVAL_LB_MOTIF_REFUS_SELECT() {
		return getZone(getNOM_LB_MOTIF_REFUS_SELECT());
	}

	private ArrayList<MotifRefusDto> getListeMotifRefus() {
		if (listeMotifRefus == null)
			return new ArrayList<MotifRefusDto>();
		return listeMotifRefus;
	}

	private void setListeMotifRefus(ArrayList<MotifRefusDto> listeMotifRefus) {
		this.listeMotifRefus = listeMotifRefus;
	}

	public String getNOM_PB_ANNULER_MOTIF_REFUS() {
		return "NOM_PB_ANNULER_MOTIF_REFUS";
	}

	public boolean performPB_ANNULER_MOTIF_REFUS(HttpServletRequest request) throws Exception {
		viderZonesSaisie();
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF_REFUS());
		return true;
	}

	public String getNOM_PB_CREER_MOTIF_REFUS() {
		return "NOM_PB_CREER_MOTIF_REFUS";
	}

	public boolean performPB_CREER_MOTIF_REFUS(HttpServletRequest request) throws Exception {
		viderZonesSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF_REFUS(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_MOTIF_REFUS());
		return true;
	}

	private void viderZonesSaisie() {
		// Motif refus
		addZone(getNOM_ST_ACTION_MOTIF_REFUS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_MOTIF_COMPTEUR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_MOTIF_REFUS(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_ABSENCE_REFUS_SELECT(), Const.ZERO);
		addZone(getNOM_LB_TYPE_ABSENCE_COMPTEUR_SELECT(), Const.ZERO);
		addZone(getNOM_EF_LIB_MOTIF_COMPTEUR(), Const.CHAINE_VIDE);
	}

	public String getNOM_PB_MODIFIER_MOTIF_REFUS() {
		return "NOM_PB_MODIFIER_MOTIF_REFUS";
	}

	public boolean performPB_MODIFIER_MOTIF_REFUS(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_MOTIF_REFUS_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOTIF_REFUS_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotifRefus().size()) {
			MotifRefusDto motifRefus = getListeMotifRefus().get(indice);
			addZone(getNOM_EF_LIB_MOTIF_REFUS(), motifRefus.getLibelle());
			EnumTypeAbsence enumType = EnumTypeAbsence.getEnumTypeAbsence(motifRefus.getIdRefTypeAbsence());
			int ligneTypeAbsence = getListeTypeAbsence().indexOf(enumType);
			addZone(getNOM_LB_TYPE_ABSENCE_REFUS_SELECT(), String.valueOf(ligneTypeAbsence));

			addZone(getNOM_ST_ACTION_MOTIF_REFUS(), ACTION_MODIFICATION);
		} else {
			viderZonesSaisie();
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs de refus"));
		}

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_MOTIF_REFUS());
		return true;
	}

	public String getNOM_PB_VALIDER_MOTIF_REFUS() {
		return "NOM_PB_VALIDER_MOTIF_REFUS";
	}

	public boolean performPB_VALIDER_MOTIF_REFUS(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_MOTIF_REFUS() != null && getVAL_ST_ACTION_MOTIF_REFUS() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF_REFUS().equals(ACTION_CREATION)
					|| getVAL_ST_ACTION_MOTIF_REFUS().equals(ACTION_MODIFICATION)) {

				if (!performControlerSaisieMotifRefus(request))
					return false;

				MotifRefusDto motifRefus = null;
				if (getVAL_ST_ACTION_MOTIF_REFUS().equals(ACTION_CREATION)) {
					motifRefus = new MotifRefusDto();

				} else {
					// modification
					int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_REFUS_SELECT()) ? Integer
							.parseInt(getVAL_LB_MOTIF_REFUS_SELECT()) : -1);
					motifRefus = getListeMotifRefus().get(indiceMotif);
				}

				int indiceType = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_REFUS_SELECT()) ? Integer
						.parseInt(getVAL_LB_TYPE_ABSENCE_REFUS_SELECT()) : -1);
				EnumTypeAbsence typeAbsence = getListeTypeAbsence().get(indiceType);

				motifRefus.setLibelle(getVAL_EF_LIB_MOTIF_REFUS());
				motifRefus.setIdRefTypeAbsence(typeAbsence.getCode());

				// on sauvegarde
				SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
				ReturnMessageDto message = consuAbs.saveMotifRefus(new JSONSerializer().serialize(motifRefus));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur(err);
				}

			}
		}

		viderZonesSaisie();
		setListeMotifRefus(null);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF_REFUS());
		return true;
	}

	private boolean performControlerSaisieMotifRefus(HttpServletRequest request) {
		// Verification libelle not null
		if (getZone(getNOM_EF_LIB_MOTIF_REFUS()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			setFocus(getDefaultFocus());
			return false;
		}
		return true;
	}

	public String getNOM_EF_LIB_MOTIF_REFUS() {
		return "NOM_EF_LIB_MOTIF_REFUS";
	}

	public String getVAL_EF_LIB_MOTIF_REFUS() {
		return getZone(getNOM_EF_LIB_MOTIF_REFUS());
	}

	public String getNOM_ST_ACTION_MOTIF_REFUS() {
		return "NOM_ST_ACTION_MOTIF_REFUS";
	}

	public String getVAL_ST_ACTION_MOTIF_REFUS() {
		return getZone(getNOM_ST_ACTION_MOTIF_REFUS());
	}

	private String[] getLB_TYPE_ABSENCE_REFUS() {
		if (LB_TYPE_ABSENCE_REFUS == null)
			LB_TYPE_ABSENCE_REFUS = initialiseLazyLB();
		return LB_TYPE_ABSENCE_REFUS;
	}

	private void setLB_TYPE_ABSENCE_REFUS(String[] newLB_TYPE_ABSENCE_REFUS) {
		LB_TYPE_ABSENCE_REFUS = newLB_TYPE_ABSENCE_REFUS;
	}

	public String getNOM_LB_TYPE_ABSENCE_REFUS() {
		return "NOM_LB_TYPE_ABSENCE_REFUS";
	}

	public String getNOM_LB_TYPE_ABSENCE_REFUS_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_REFUS_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE_REFUS() {
		return getLB_TYPE_ABSENCE_REFUS();
	}

	public String getVAL_LB_TYPE_ABSENCE_REFUS_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_REFUS_SELECT());
	}

	public ArrayList<EnumTypeAbsence> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<EnumTypeAbsence>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<EnumTypeAbsence> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	private String[] getLB_MOTIF_COMPTEUR() {
		if (LB_MOTIF_COMPTEUR == null)
			LB_MOTIF_COMPTEUR = initialiseLazyLB();
		return LB_MOTIF_COMPTEUR;
	}

	private void setLB_MOTIF_COMPTEUR(String[] newLB_MOTIF_COMPTEUR) {
		LB_MOTIF_COMPTEUR = newLB_MOTIF_COMPTEUR;
	}

	public String getNOM_LB_MOTIF_COMPTEUR() {
		return "NOM_LB_MOTIF_COMPTEUR";
	}

	public String getNOM_LB_MOTIF_COMPTEUR_SELECT() {
		return "NOM_LB_MOTIF_COMPTEUR_SELECT";
	}

	public String[] getVAL_LB_MOTIF_COMPTEUR() {
		return getLB_MOTIF_COMPTEUR();
	}

	public String getVAL_LB_MOTIF_COMPTEUR_SELECT() {
		return getZone(getNOM_LB_MOTIF_COMPTEUR_SELECT());
	}

	public ArrayList<MotifCompteurDto> getListeMotifCompteur() {
		if (listeMotifCompteur == null)
			return new ArrayList<MotifCompteurDto>();
		return listeMotifCompteur;
	}

	public void setListeMotifCompteur(ArrayList<MotifCompteurDto> listeMotifCompteur) {
		this.listeMotifCompteur = listeMotifCompteur;
	}

	public String getNOM_PB_ANNULER_MOTIF_COMPTEUR() {
		return "NOM_PB_ANNULER_MOTIF_COMPTEUR";
	}

	public boolean performPB_ANNULER_MOTIF_COMPTEUR(HttpServletRequest request) throws Exception {
		viderZonesSaisie();
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF_COMPTEUR());
		return true;
	}

	public String getNOM_PB_CREER_MOTIF_COMPTEUR() {
		return "NOM_PB_CREER_MOTIF_COMPTEUR";
	}

	public boolean performPB_CREER_MOTIF_COMPTEUR(HttpServletRequest request) throws Exception {
		viderZonesSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF_COMPTEUR(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_MOTIF_COMPTEUR());
		return true;
	}

	public String getNOM_PB_MODIFIER_MOTIF_COMPTEUR() {
		return "NOM_PB_MODIFIER_MOTIF_COMPTEUR";
	}

	public boolean performPB_MODIFIER_MOTIF_COMPTEUR(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_MOTIF_COMPTEUR_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOTIF_COMPTEUR_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotifCompteur().size()) {
			MotifCompteurDto motifCompteur = getListeMotifCompteur().get(indice);
			addZone(getNOM_EF_LIB_MOTIF_COMPTEUR(), motifCompteur.getLibelle());
			EnumTypeAbsence enumType = EnumTypeAbsence.getEnumTypeAbsence(motifCompteur.getIdRefTypeAbsence());
			int ligneTypeAbsence = getListeTypeAbsence().indexOf(enumType);
			addZone(getNOM_LB_TYPE_ABSENCE_COMPTEUR_SELECT(), String.valueOf(ligneTypeAbsence));

			addZone(getNOM_ST_ACTION_MOTIF_COMPTEUR(), ACTION_MODIFICATION);
		} else {
			viderZonesSaisie();
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs de compteur"));
		}

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_MOTIF_COMPTEUR());
		return true;
	}

	public String getNOM_PB_VALIDER_MOTIF_COMPTEUR() {
		return "NOM_PB_VALIDER_MOTIF_COMPTEUR";
	}

	public boolean performPB_VALIDER_MOTIF_COMPTEUR(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_MOTIF_COMPTEUR() != null && getVAL_ST_ACTION_MOTIF_COMPTEUR() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF_COMPTEUR().equals(ACTION_CREATION)
					|| getVAL_ST_ACTION_MOTIF_COMPTEUR().equals(ACTION_MODIFICATION)) {

				if (!performControlerSaisieMotifCompteur(request))
					return false;

				MotifCompteurDto motifCompteur = null;
				if (getVAL_ST_ACTION_MOTIF_COMPTEUR().equals(ACTION_CREATION)) {
					motifCompteur = new MotifCompteurDto();
				} else {
					// modification
					int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_COMPTEUR_SELECT()) ? Integer
							.parseInt(getVAL_LB_MOTIF_COMPTEUR_SELECT()) : -1);
					motifCompteur = getListeMotifCompteur().get(indiceMotif);
				}

				int indiceType = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) ? Integer
						.parseInt(getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) : -1);
				EnumTypeAbsence typeAbsence = getListeTypeAbsence().get(indiceType);

				motifCompteur.setLibelle(getVAL_EF_LIB_MOTIF_COMPTEUR());
				motifCompteur.setIdRefTypeAbsence(typeAbsence.getCode());

				// on sauvegarde
				SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
				ReturnMessageDto message = consuAbs.saveMotifCompteur(new JSONSerializer().serialize(motifCompteur));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur(err);
				}

			}
		}

		viderZonesSaisie();
		setListeMotifCompteur(null);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF_COMPTEUR());
		return true;
	}

	private boolean performControlerSaisieMotifCompteur(HttpServletRequest request) {
		// Verification libelle not null
		if (getZone(getNOM_EF_LIB_MOTIF_COMPTEUR()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			setFocus(getDefaultFocus());
			return false;
		}
		return true;
	}

	public String getNOM_EF_LIB_MOTIF_COMPTEUR() {
		return "NOM_EF_LIB_MOTIF_COMPTEUR";
	}

	public String getVAL_EF_LIB_MOTIF_COMPTEUR() {
		return getZone(getNOM_EF_LIB_MOTIF_COMPTEUR());
	}

	public String getNOM_ST_ACTION_MOTIF_COMPTEUR() {
		return "NOM_ST_ACTION_MOTIF_COMPTEUR";
	}

	public String getVAL_ST_ACTION_MOTIF_COMPTEUR() {
		return getZone(getNOM_ST_ACTION_MOTIF_COMPTEUR());
	}

	private String[] getLB_TYPE_ABSENCE_COMPTEUR() {
		if (LB_TYPE_ABSENCE_COMPTEUR == null)
			LB_TYPE_ABSENCE_COMPTEUR = initialiseLazyLB();
		return LB_TYPE_ABSENCE_COMPTEUR;
	}

	private void setLB_TYPE_ABSENCE_COMPTEUR(String[] newLB_TYPE_ABSENCE_COMPTEUR) {
		LB_TYPE_ABSENCE_COMPTEUR = newLB_TYPE_ABSENCE_COMPTEUR;
	}

	public String getNOM_LB_TYPE_ABSENCE_COMPTEUR() {
		return "NOM_LB_TYPE_ABSENCE_COMPTEUR";
	}

	public String getNOM_LB_TYPE_ABSENCE_COMPTEUR_SELECT() {
		return "NOM_LB_TYPE_ABSENCE_COMPTEUR_SELECT";
	}

	public String[] getVAL_LB_TYPE_ABSENCE_COMPTEUR() {
		return getLB_TYPE_ABSENCE_COMPTEUR();
	}

	public String getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT() {
		return getZone(getNOM_LB_TYPE_ABSENCE_COMPTEUR_SELECT());
	}
}
