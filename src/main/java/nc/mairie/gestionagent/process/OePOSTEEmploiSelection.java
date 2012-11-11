package nc.mairie.gestionagent.process;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTEEmploiSelection
 * Date de création : (13/07/11 10:23:55)
     *
 */
public class OePOSTEEmploiSelection extends nc.mairie.technique.BasicProcess {

	private ArrayList listeFicheEmploi = new ArrayList();

	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP
	 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
	 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
	 * Utilisation de la méthode addZone(getNOMxxx, String);
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		//aucune initialisation
	}

	/**
	 * Constructeur du process OePOSTEEmploiSelection.
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public OePOSTEEmploiSelection() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_ANNULER
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_RECHERCHER
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		String zone = getZone(getNOM_EF_RECHERCHE());

		ArrayList eListe = FicheEmploi.listerFicheEmploiavecRefMairie(getTransaction(), zone);

		//	Si liste vide alors erreur
		if (eListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}

		setListeFicheEmploi(eListe);

		int indiceFe = 0;
		if (getListeFicheEmploi() != null) {
			for (int i = 0; i < getListeFicheEmploi().size(); i++) {
				FicheEmploi p = (FicheEmploi) getListeFicheEmploi().get(i);

				addZone(getNOM_ST_CODE(indiceFe), p.getRefMairie());
				addZone(getNOM_ST_LIB(indiceFe), p.getNomMetierEmploi());

				indiceFe++;
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_RECHERCHE
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public String getNOM_EF_RECHERCHE() {
		return "NOM_EF_RECHERCHE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie  :
	 * EF_RECHERCHE
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public String getVAL_EF_RECHERCHE() {
		return getZone(getNOM_EF_RECHERCHE());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : 
	 * en fonction du bouton de la JSP 
	 * Date de création : (13/07/11 10:23:55)
     *
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		//Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER
			for (int i = 0; i < getListeFicheEmploi().size(); i++) {
				if (testerParametre(request, getNOM_PB_VALIDER(i))) {
					return performPB_VALIDER(request, i);
				}
			}

			//Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			//Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}
		}
		//Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process
	 * Zone à utiliser dans un champ caché dans chaque formulaire de la JSP.
	 * Date de création : (13/07/11 10:31:33)
     *
	 */
	public String getJSP() {
		return "OePOSTEEmploiSelection.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_VALIDER
	 * Date de création : (13/07/11 10:31:33)
     *
	 */
	public String getNOM_PB_VALIDER(int i) {
		return "NOM_PB_VALIDER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (13/07/11 10:31:33)
     *
	 */
	public boolean performPB_VALIDER(HttpServletRequest request, int elemSelection) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);

		FicheEmploi ficheEmploi = (FicheEmploi) getListeFicheEmploi().get(elemSelection);
		VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, ficheEmploi);

		return true;
	}

	public ArrayList getListeFicheEmploi() {
		if (listeFicheEmploi == null)
			listeFicheEmploi = new ArrayList();
		return listeFicheEmploi;
	}

	private void setListeFicheEmploi(ArrayList listeFicheEmploi) {
		this.listeFicheEmploi = listeFicheEmploi;
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
		return getNOM_EF_RECHERCHE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_CODE
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getNOM_ST_CODE(int i) {
		return "NOM_ST_CODE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_CODE
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getVAL_ST_CODE(int i) {
		return getZone(getNOM_ST_CODE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LIB
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getNOM_ST_LIB(int i) {
		return "NOM_ST_LIB" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_LIB
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getVAL_ST_LIB(int i) {
		return getZone(getNOM_ST_LIB(i));
	}
}
