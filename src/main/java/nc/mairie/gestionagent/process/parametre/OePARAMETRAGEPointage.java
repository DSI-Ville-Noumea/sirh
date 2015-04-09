package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.MotifHeureSupDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import flexjson.JSONSerializer;

/**
 * Process OePARAMETRAGEElection Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEPointage extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;

	private String[] LB_MOTIF;
	private ArrayList<MotifHeureSupDto> listeMotif;

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
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		if (getListeMotif().size() == 0) {
			initialiseListeMotif(request);
		}

	}

	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		// Liste depuis SIRH-PTG-WS
		SirhPtgWSConsumer consuPtg = new SirhPtgWSConsumer();
		ArrayList<MotifHeureSupDto> listeMotifs = (ArrayList<MotifHeureSupDto>) consuPtg.getListeMotifHeureSup();

		setListeMotif(listeMotifs);
		if (getListeMotif().size() != 0) {
			int tailles[] = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (MotifHeureSupDto motif : getListeMotif()) {
				String ligne[] = { motif.getLibelle() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
		}
	}

	public OePARAMETRAGEPointage() {
		super();
	}

	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

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
		return "OePARAMETRAGEPointage.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-POINTAGE";
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

	private ArrayList<MotifHeureSupDto> getListeMotif() {
		if (listeMotif == null)
			return new ArrayList<MotifHeureSupDto>();
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifHeureSupDto> listeMotif) {
		this.listeMotif = listeMotif;
	}

	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	public String getNOM_EF_LIB_MOTIF() {
		return "NOM_EF_LIB_MOTIF";
	}

	public String getVAL_EF_LIB_MOTIF() {
		return getZone(getNOM_EF_LIB_MOTIF());
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
		addZone(getNOM_EF_LIB_MOTIF(), Const.CHAINE_VIDE);
	}

	public String getNOM_PB_MODIFIER_MOTIF() {
		return "NOM_PB_MODIFIER_MOTIF";
	}

	public boolean performPB_MODIFIER_MOTIF(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotif().size()) {
			MotifHeureSupDto motif = getListeMotif().get(indice);
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
			if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)
					|| getVAL_ST_ACTION_MOTIF().equals(ACTION_MODIFICATION)) {

				if (!performControlerSaisieMotif(request))
					return false;

				MotifHeureSupDto motif = null;
				if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {
					motif = new MotifHeureSupDto();

				} else {
					// modification
					int indiceMotif = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer
							.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
					motif = getListeMotif().get(indiceMotif);
				}

				motif.setLibelle(getVAL_EF_LIB_MOTIF());

				// on sauvegarde
				SirhPtgWSConsumer consuPtg = new SirhPtgWSConsumer();
				ReturnMessageDto message = consuPtg.saveMotifHeureSup(new JSONSerializer().exclude("*.class")
						.serialize(motif));

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
}
