package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.MotifCompteurDto;
import nc.mairie.gestionagent.absence.dto.MotifDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;

import org.springframework.context.ApplicationContext;

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

	private String[] LB_MOTIF;
	private ArrayList<MotifDto> listeMotif;

	private String[] LB_TYPE_ABSENCE_COMPTEUR;
	private ArrayList<TypeAbsenceDto> listeTypeAbsence;

	private String[] LB_MOTIF_COMPTEUR;
	private ArrayList<MotifCompteurDto> listeMotifCompteur;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	private IAbsService absService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
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
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		if (getListeTypeAbsence().size() == 0) {
			initialiseListeTypeAbsence(request);
		}

		if (getListeMotif().size() == 0) {
			initialiseListeMotif(request);
		}

		if (getListeMotifCompteur().size() == 0) {
			initialiseListeMotifCompteur(request, null);
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
	}

	private void initialiseListeMotifCompteur(HttpServletRequest request, ArrayList<MotifCompteurDto> listeMotifs) {
		if (listeMotifs == null || listeMotifs.size() == 0) {
			// Liste depuis SIRH-ABS-WS
			listeMotifs = (ArrayList<MotifCompteurDto>) absService.getListeMotifCompteur(null);
		}

		setListeMotifCompteur(listeMotifs);
		if (getListeMotifCompteur().size() != 0) {
			int tailles[] = { 40, 40 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (MotifCompteurDto motif : getListeMotifCompteur()) {
				TypeAbsenceDto t = new TypeAbsenceDto();
				t.setIdRefTypeAbsence(motif.getIdRefTypeAbsence());
				String type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(t)).getLibelle();
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
			List<TypeAbsenceDto> listeComplete = absService.getListeRefTypeAbsenceDto(null);
			setListeTypeAbsence(new ArrayList<TypeAbsenceDto>());

			int[] tailles = { 100 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<TypeAbsenceDto> list = listeComplete.listIterator(); list.hasNext();) {
				TypeAbsenceDto type = (TypeAbsenceDto) list.next();
				if (!getListeTypeAbsence().contains(type)) {
					if (EnumTypeGroupeAbsence.CONGES_EXCEP.getValue() != type.getGroupeAbsence().getIdRefGroupeAbsence().intValue()) {
						String ligne[] = { type.getLibelle() };

						aFormat.ajouteLigne(ligne);
						getListeTypeAbsence().add(type);
					}
				}
			}
			setLB_TYPE_ABSENCE_COMPTEUR(aFormat.getListeFormatee(false));
		}

	}

	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		// Liste depuis SIRH-ABS-WS
		ArrayList<MotifDto> listeMotifs = (ArrayList<MotifDto>) absService.getListeMotif();

		setListeMotif(listeMotifs);
		if (getListeMotif().size() != 0) {
			int tailles[] = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (MotifDto motif : getListeMotif()) {
				String ligne[] = { motif.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
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
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_TRI
			if (testerParametre(request, getNOM_PB_TRI())) {
				return performPB_TRI(request);
			}

			// Si clic sur le bouton PB_ANNULER_MOTIF
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF())) {
				return performPB_ANNULER_MOTIF(request);
			}

			// Si clic sur le bouton PB_CREER_MOTIF
			if (testerParametre(request, getNOM_PB_CREER_MOTIF())) {
				return performPB_CREER_MOTIF(request);
			}

			// Si clic sur le bouton PB_MODIFIER_MOTIF
			if (testerParametre(request, getNOM_PB_MODIFIER_MOTIF())) {
				return performPB_MODIFIER_MOTIF(request);
			}

			// Si clic sur le bouton PB_VALIDER_MOTIF
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF())) {
				return performPB_VALIDER_MOTIF(request);
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
		return Const.CHAINE_VIDE;
	}

	/**
	 * @param focus
	 *            focus à  définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
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

	private ArrayList<MotifDto> getListeMotif() {
		if (listeMotif == null)
			return new ArrayList<MotifDto>();
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifDto> listeMotif) {
		this.listeMotif = listeMotif;
	}

	public String getNOM_PB_ANNULER_MOTIF() {
		return "NOM_PB_ANNULER_MOTIF";
	}

	public boolean performPB_ANNULER_MOTIF(HttpServletRequest request) throws Exception {
		viderZonesSaisie();
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	public String getNOM_PB_CREER_MOTIF() {
		return "NOM_PB_CREER_MOTIF";
	}

	public boolean performPB_CREER_MOTIF(HttpServletRequest request) throws Exception {
		viderZonesSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	private void viderZonesSaisie() {
		// Motif
		addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION_MOTIF_COMPTEUR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_MOTIF(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_TYPE_ABSENCE_COMPTEUR_SELECT(), Const.ZERO);
		addZone(getNOM_EF_LIB_MOTIF_COMPTEUR(), Const.CHAINE_VIDE);
	}

	public String getNOM_PB_MODIFIER_MOTIF() {
		return "NOM_PB_MODIFIER_MOTIF";
	}

	public boolean performPB_MODIFIER_MOTIF(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotif().size()) {
			MotifDto motif = getListeMotif().get(indice);
			addZone(getNOM_EF_LIB_MOTIF(), motif.getLibelle());

			addZone(getNOM_ST_ACTION_MOTIF(), ACTION_MODIFICATION);
		} else {
			viderZonesSaisie();
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs"));
		}

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	public String getNOM_PB_VALIDER_MOTIF() {
		return "NOM_PB_VALIDER_MOTIF";
	}

	public boolean performPB_VALIDER_MOTIF(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_MOTIF() != null && getVAL_ST_ACTION_MOTIF() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION) || getVAL_ST_ACTION_MOTIF().equals(ACTION_MODIFICATION)) {

				if (!performControlerSaisieMotif(request))
					return false;

				MotifDto motif = null;
				if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {
					motif = new MotifDto();

				} else {
					// modification
					int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
					motif = getListeMotif().get(indiceMotif);
				}

				motif.setLibelle(getVAL_EF_LIB_MOTIF());

				// on sauvegarde
				ReturnMessageDto message = absService.saveMotif(new JSONSerializer().exclude("*.class").serialize(motif));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur("ERREUR : " + err);
				}

			}
		}

		viderZonesSaisie();
		setListeMotif(null);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	private boolean performControlerSaisieMotif(HttpServletRequest request) {
		// Verification libelle not null
		if (getZone(getNOM_EF_LIB_MOTIF()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			setFocus(getDefaultFocus());
			return false;
		}
		return true;
	}

	public String getNOM_EF_LIB_MOTIF() {
		return "NOM_EF_LIB_MOTIF";
	}

	public String getVAL_EF_LIB_MOTIF() {
		return getZone(getNOM_EF_LIB_MOTIF());
	}

	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	public ArrayList<TypeAbsenceDto> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<TypeAbsenceDto>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<TypeAbsenceDto> listeTypeAbsence) {
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

		int indice = (Services.estNumerique(getVAL_LB_MOTIF_COMPTEUR_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_COMPTEUR_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotifCompteur().size()) {
			MotifCompteurDto motifCompteur = getListeMotifCompteur().get(indice);
			addZone(getNOM_EF_LIB_MOTIF_COMPTEUR(), motifCompteur.getLibelle());
			TypeAbsenceDto t = new TypeAbsenceDto();
			t.setIdRefTypeAbsence(motifCompteur.getIdRefTypeAbsence());
			int ligneTypeAbsence = getListeTypeAbsence().indexOf(t);
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
			if (getVAL_ST_ACTION_MOTIF_COMPTEUR().equals(ACTION_CREATION) || getVAL_ST_ACTION_MOTIF_COMPTEUR().equals(ACTION_MODIFICATION)) {

				if (!performControlerSaisieMotifCompteur(request))
					return false;

				MotifCompteurDto motifCompteur = null;
				if (getVAL_ST_ACTION_MOTIF_COMPTEUR().equals(ACTION_CREATION)) {
					motifCompteur = new MotifCompteurDto();
				} else {
					// modification
					int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_COMPTEUR_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_COMPTEUR_SELECT()) : -1);
					motifCompteur = getListeMotifCompteur().get(indiceMotif);
				}

				int indiceType = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) : -1);
				TypeAbsenceDto typeAbsence = getListeTypeAbsence().get(indiceType);

				motifCompteur.setLibelle(getVAL_EF_LIB_MOTIF_COMPTEUR());
				motifCompteur.setIdRefTypeAbsence(typeAbsence.getIdRefTypeAbsence());

				// on sauvegarde
				ReturnMessageDto message = absService.saveMotifCompteur(new JSONSerializer().exclude("*.class").serialize(motifCompteur));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur("ERREUR : " + err);
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
		// Verification si A50 ou A49 alors pas de motif saisissable
		int indiceType = (Services.estNumerique(getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) : -1);
		TypeAbsenceDto typeAbsence = getListeTypeAbsence().get(indiceType);
		if (typeAbsence.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A49.getCode().toString())
				|| typeAbsence.getIdRefTypeAbsence().toString().equals(EnumTypeAbsence.ASA_A50.getCode().toString())) {
			// "ERR147",
			// "Cette famille ne se gere pas par compteur.Il est donc impossible de saisir un motif.");
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR147"));
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

	public String getNOM_RG_TRI() {
		return "NOM_RG_TRI";
	}

	public String getVAL_RG_TRI() {
		return getZone(getNOM_RG_TRI());
	}

	public String getNOM_RB_TRI_FAMILLE() {
		return "NOM_RB_TRI_FAMILLE";
	}

	public String getNOM_RB_TRI_LIBELLE() {
		return "NOM_RB_TRI_LIBELLE";
	}

	public String getNOM_PB_TRI() {
		return "NOM_PB_TRI";
	}

	public boolean performPB_TRI(HttpServletRequest request) throws Exception {
		if (getVAL_RG_TRI().equals(getNOM_RB_TRI_LIBELLE())) {
			// on tri la liste
			Collections.sort(getListeMotifCompteur(), new Comparator<MotifCompteurDto>() {
				@Override
				public int compare(MotifCompteurDto o1, MotifCompteurDto o2) {
					return o1.getLibelle().compareTo(o2.getLibelle());
				}

			});
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_FAMILLE())) {
			// on tri la liste
			Collections.sort(getListeMotifCompteur(), new Comparator<MotifCompteurDto>() {
				@Override
				public int compare(MotifCompteurDto o1, MotifCompteurDto o2) {
					return o1.getIdRefTypeAbsence().toString().compareTo(o2.getIdRefTypeAbsence().toString());
				}

			});
		}
		initialiseListeMotifCompteur(request, getListeMotifCompteur());
		return true;
	}
}
