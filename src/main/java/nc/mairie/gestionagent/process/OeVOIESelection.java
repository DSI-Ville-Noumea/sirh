package nc.mairie.gestionagent.process;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.commun.VoieQuartier;
import nc.mairie.technique.MairieMessages;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MessageUtils;

/**
 * Process OeVOIESelection Date de création : (27/01/03 15:03:41)
 */
public class OeVOIESelection extends nc.mairie.technique.BasicProcess {
	private ArrayList<VoieQuartier> listeVoie;
	public String focus = null;

	/**
	 * Constructeur du process OeVOIESelection. Date de création : (27/01/03
	 * 15:03:41)
	 */
	public OeVOIESelection() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (27/01/03 15:03:41)
	 */
	public String getJSP() {
		return "OeVOIESelection.jsp";
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/01/2003
	 * 15:31:18)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<VoieQuartier> getListeVoie() {
		if (listeVoie == null) {
			listeVoie = new ArrayList<VoieQuartier>();
		}
		return listeVoie;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_VOIE Date de
	 * création : (27/01/03 15:03:41)
	 */
	public String getNOM_EF_VOIE() {
		return "NOM_EF_VOIE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (27/01/03 15:03:41)
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de création :
	 * (27/01/03 15:03:41)
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_VOIE
	 * Date de création : (27/01/03 15:03:41)
	 */
	public String getVAL_EF_VOIE() {
		return getZone(getNOM_EF_VOIE());
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (27/01/03 15:03:41)
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// aucune initialisation
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/01/03 15:03:41)
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/01/03 15:03:41)
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		String zone = getZone(getNOM_EF_VOIE());

		// Test de la zone de saisie
		if (zone.length() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MairieMessages.getMessage("ERR011"));
			return false;
		}

		// Recup de la liste des voies/quartier
		ArrayList<VoieQuartier> aListe = VoieQuartier.listerVoieQuartierAvecLibVoieContenant(getTransaction(), zone);

		// Si la liste est vide alors erreur
		if (aListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}

		setListeVoie(aListe);

		int indiceRue = 0;
		if (getListeVoie() != null) {
			for (int i = 0; i < getListeVoie().size(); i++) {
				VoieQuartier c = getListeVoie().get(i);

				addZone(getNOM_ST_CODE(indiceRue), c.getCodVoie().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getCodVoie());
				addZone(getNOM_ST_LIB(indiceRue), c.getLibVoie().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getLibVoie());
				addZone(getNOM_ST_QUARTIER(indiceRue), c.getLibQuartier().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getLibQuartier());

				indiceRue++;
			}
		}

		return true;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (27/01/03 15:03:41)
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_OK_PAYS
			for (int i = 0; i < getListeVoie().size(); i++) {
				if (testerParametre(request, getNOM_PB_OK(i))) {
					return performPB_OK(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/01/2003
	 * 15:31:18)
	 * 
	 * @param newListeVoie
	 *            ArrayList
	 */
	private void setListeVoie(ArrayList<VoieQuartier> newListeVoie) {
		listeVoie = newListeVoie;
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
		return getNOM_EF_VOIE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE Date de
	 * création : (18/08/11 10:21:15)
	 */
	public String getNOM_ST_CODE(int i) {
		return "NOM_ST_CODE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE Date de
	 * création : (18/08/11 10:21:15)
	 */
	public String getVAL_ST_CODE(int i) {
		return getZone(getNOM_ST_CODE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB Date de création
	 * : (18/08/11 10:21:15)
	 */
	public String getNOM_ST_LIB(int i) {
		return "NOM_ST_LIB" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB Date de
	 * création : (18/08/11 10:21:15)
	 */
	public String getVAL_ST_LIB(int i) {
		return getZone(getNOM_ST_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_QUARTIER Date de
	 * création : (18/08/11 10:21:15)
	 */
	public String getNOM_ST_QUARTIER(int i) {
		return "NOM_ST_QUARTIER" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_QUARTIER Date
	 * de création : (18/08/11 10:21:15)
	 */
	public String getVAL_ST_QUARTIER(int i) {
		return getZone(getNOM_ST_QUARTIER(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de création :
	 * (27/01/03 15:03:41)
	 * 
	 */
	public String getNOM_PB_OK(int i) {
		return "NOM_PB_OK" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/01/03 15:03:41)
	 * 
	 */
	public boolean performPB_OK(HttpServletRequest request, int elemSelection) throws Exception {
		// Récup de la voie sélectionnée
		VoieQuartier aVoieQuartier = (VoieQuartier) getListeVoie().get(elemSelection);
		if (getTransaction().isErreur()) {
			return false;
		}

		// Alimentation de la variable activite
		VariableActivite.ajouter(this, VariableActivite.ACTIVITE_VOIE_QUARTIER, aVoieQuartier);
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}
}
