package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.poste.Activite;
import nc.mairie.technique.BasicProcess;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTEFEActiviteSelection
 * Date de création : (03/02/09 14:56:59)
     *
 */
public class OePOSTEFEActiviteSelection extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Activite> listeActivites;
	public String focus = null;

	/**
	 * @return Returns the listeActivites.
	 */
	public ArrayList<Activite> getListeActivites() {
		return listeActivites;
	}

	/**
	 * @param listeActivites The listeActivites to set.
	 */
	public void setListeActivites(ArrayList<Activite> listeActivites) {
		this.listeActivites = listeActivites;
	}

	/**
	 * Initialisation des zones à afficher dans la JSP
	 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
	 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
	 * Utilisation de la méthode addZone(getNOMxxx, String);
	 * Date de création : (03/02/09 14:56:59)
     *
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		if (getListeActivites() == null) {
			@SuppressWarnings("unchecked")
			ArrayList<Activite> xcludeListe = (ArrayList<Activite>) VariablesActivite.recuperer(this, "LISTEACTIVITE");
			ArrayList<Activite> aListe = new ArrayList<Activite>();

			aListe = Activite.listerActivite(getTransaction(), true);
			aListe = elim_doubure_activites(aListe, xcludeListe);

			//Affectation de la liste	
			setListeActivites(new ArrayList<Activite>());
			for (int j = 0; j < aListe.size(); j++) {
				Activite activite = (Activite) aListe.get(j);
				Integer i = Integer.valueOf(activite.getIdActivite());
				if (activite != null) {
					getListeActivites().add(activite);
					addZone(getNOM_ST_ID_ACTI(i), activite.getIdActivite());
					addZone(getNOM_ST_LIB_ACTI(i), activite.getNomActivite());
				}
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_ANNULER
	 * Date de création : (03/02/09 14:56:59)
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
	 * Date de création : (03/02/09 14:56:59)
     *
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * @param focus focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe ayant éléminé de la liste l1 les éléments en communs avec l2
	 * fonctionne uniquement avec une liste l1 n'ayant pas 2 elements identiques
	 */
	public static ArrayList<Activite> elim_doubure_activites(ArrayList<Activite> l1, ArrayList<Activite> l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((Activite) l2.get(i)).getIdActivite()).equals(((Activite) l1.get(j)).getIdActivite()))
						l1.remove(j);

				}
			}
		}
		return l1;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_VALIDER
	 * Date de création : (19/07/11 16:22:13)
     *
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (19/07/11 16:22:13)
     *
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		ArrayList<Activite> listActiSelect = new ArrayList<Activite>();
		for (int j = 0; j < getListeActivites().size(); j++) {
			//on recupère la ligne concernée
			Activite acti = (Activite) getListeActivites().get(j);
			Integer i = Integer.valueOf(acti.getIdActivite());
			//si la colonne selection est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				listActiSelect.add(acti);
			}
		}
		VariablesActivite.ajouter(this, "ACTIVITE_PRINC", listActiSelect);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : 
	 * en fonction du bouton de la JSP 
	 * Date de création : (03/02/09 14:56:59)
     *
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		//Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			//Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			//Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
		}
		//Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFEActiviteSelection.
	 * Date de création : (24/08/11 09:15:05)
     *
	 */
	public OePOSTEFEActiviteSelection() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process
	 * Zone à utiliser dans un champ caché dans chaque formulaire de la JSP.
	 * Date de création : (24/08/11 09:15:05)
     *
	 */
	public String getJSP() {
		return "OePOSTEFEActiviteSelection.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_ID_ACTI
	 * Date de création : (21/11/11 09:55:36)
     *
	 */
	public String getNOM_ST_ID_ACTI(int i) {
		return "NOM_ST_ID_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_ID_ACTI
	 * Date de création : (21/11/11 09:55:36)
     *
	 */
	public String getVAL_ST_ID_ACTI(int i) {
		return getZone(getNOM_ST_ID_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LIB_ACTI
	 * Date de création : (21/11/11 09:55:36)
     *
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_LIB_ACTI
	 * Date de création : (21/11/11 09:55:36)
     *
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE
	 * Date de création : (21/11/11 09:55:36)
     *
	 */
	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case à cocher  :
	 * CK_SELECT_LIGNE
	 * Date de création : (21/11/11 09:55:36)
     *
	 */
	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

}
