package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.commun.Commune;
import nc.mairie.metier.commun.CommuneDepartement;
import nc.mairie.metier.commun.CommuneEtrangere;
import nc.mairie.metier.commun.Pays;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OeCOMMUNEESelection Date de cr�ation : (24/01/03 15:27:10)
 * 
 */
public class OeCOMMUNESelection extends nc.mairie.technique.BasicProcess {
	// Global
	private String origine;

	// Pourla France
	private ArrayList listeCommune;

	// Pour l'�tranger

	private ArrayList listePays = new ArrayList();
	private Hashtable<Pays, ArrayList<?>> hashPaysCommuneEtrangere = new Hashtable<Pays, ArrayList<?>>();
	private ArrayList listeCommunePays;
	private Pays paysCourant;

	public boolean estPaysSelectionne = false;

	public String focus = null;

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (24/01/2003
	 * 15:53:30)
	 * 
	 * @return Hashtable
	 */
	private Hashtable<Pays, ArrayList<?>> getHashPaysCommuneEtrangere() {
		if (hashPaysCommuneEtrangere == null)
			hashPaysCommuneEtrangere = new Hashtable<Pays, ArrayList<?>>();
		return hashPaysCommuneEtrangere;
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (24/01/2003
	 * 15:52:16)
	 * 
	 * @return ArrayList
	 */
	public ArrayList getListePays() {
		if (listePays == null) {
			listePays = new ArrayList();
		}
		return listePays;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PAYS Date de
	 * cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public String getNOM_EF_PAYS() {
		return "NOM_EF_PAYS";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (24/01/03 15:27:10)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de cr�ation
	 * : (24/01/03 15:27:10)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie : EF_PAYS
	 * Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public String getVAL_EF_PAYS() {
		return getZone(getNOM_EF_PAYS());
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		if (getZone(getNOM_RG_ORIGINE_COMMUNE()) == "") {
			addZone(getNOM_RG_ORIGINE_COMMUNE(), getNOM_RB_ORIGINE_COMMUNE_FRANCE());
			setOrigine(Const.COMMUNE_FRANCE);
		}
		initialiseListeCommune();
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		String zone = getZone(getNOM_EF_PAYS());

		// Test de la zone de saisie
		if (zone.length() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR010"));
			return false;
		}

		ArrayList aListe = new ArrayList();
		setListeCommunePays(null);
		setListePays(null);

		// Si num�rique alors recherche de la commune
		if (Services.estNumerique(zone)) {
			aListe = Pays.listerPaysAvecCodPaysCommencant(getTransaction(), zone);
			// Sinon recherche de liste
		} else {
			aListe = Pays.listerPaysAvecLibPaysCommencant(getTransaction(), zone);
		}

		// Si liste vide alors erreur
		if (aListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}

		setListePays(aListe);
		estPaysSelectionne = false;
		initialiseListePays();

		return true;
	}

	private void initialiseListePays() {
		int indicePays = 0;
		if (getListePays() != null) {
			for (int i = 0; i < getListePays().size(); i++) {
				Pays p = (Pays) getListePays().get(i);

				addZone(getNOM_ST_CODE_PAYS(indicePays), p.getCodPays().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : p.getCodPays());
				addZone(getNOM_ST_LIB_PAYS(indicePays), p.getLibPays().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : p.getLibPays());

				indicePays++;
			}
		}
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (24/01/2003
	 * 15:52:16)
	 * 
	 * @param newListePays
	 *            ArrayList
	 */
	private void setListePays(ArrayList newListePays) {
		listePays = newListePays;
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
		return getNOM_EF_COMMUNE_FRANCE();
	}

	/**
	 * @param focus
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_ORIGINE_COMMUNE Date de cr�ation : (30/03/11 16:16:43)
	 * 
	 */
	public String getNOM_RG_ORIGINE_COMMUNE() {
		return "NOM_RG_ORIGINE_COMMUNE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_ORIGINE_COMMUNE Date de cr�ation : (30/03/11 16:16:43)
	 * 
	 */
	public String getVAL_RG_ORIGINE_COMMUNE() {
		return getZone(getNOM_RG_ORIGINE_COMMUNE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_ORIGINE_COMMUNE_ETRANGER
	 * Date de cr�ation : (30/03/11 16:16:43)
	 * 
	 */
	public String getNOM_RB_ORIGINE_COMMUNE_ETRANGER() {
		return "NOM_RB_ORIGINE_COMMUNE_ETRANGER";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_ORIGINE_COMMUNE_FRANCE
	 * Date de cr�ation : (30/03/11 16:16:43)
	 * 
	 */
	public String getNOM_RB_ORIGINE_COMMUNE_FRANCE() {
		return "NOM_RB_ORIGINE_COMMUNE_FRANCE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_FRANCE Date de
	 * cr�ation : (31/03/11 10:11:15)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_FRANCE() {
		return "NOM_PB_RECHERCHER_FRANCE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (31/03/11 10:11:15)
	 * 
	 */
	public boolean performPB_RECHERCHER_FRANCE(HttpServletRequest request) throws Exception {

		String zone = getZone(getNOM_EF_COMMUNE_FRANCE());

		// Test de la zone de saisie
		if (zone.length() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR012"));
			return false;
		}

		ArrayList aListe = new ArrayList();

		// Si num�rique alors recherche de la commune
		if (Services.estNumerique(zone)) {
			aListe = CommuneDepartement.listerCommuneDepartementAvecCodCommuneCommencant(getTransaction(), zone);
			// Sinon recherche de liste
		} else {
			aListe = CommuneDepartement.listerCommuneDepartementAvecLibCommuneCommencant(getTransaction(), zone);
		}

		// Si liste vide alors erreur
		if (aListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}

		setListeCommune(aListe);

		initialiseListeCommune();

		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	private void initialiseListeCommune() {
		int indiceComm = 0;
		if (getListeCommune() != null) {
			for (int i = 0; i < getListeCommune().size(); i++) {
				CommuneDepartement c = (CommuneDepartement) getListeCommune().get(i);

				addZone(getNOM_ST_CODE(indiceComm), c.getCodCommune().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getCodCommune());
				addZone(getNOM_ST_LIB(indiceComm), c.getLibVille().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getLibVille());

				indiceComm++;
			}
		}
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMUNE_FRANCE Date
	 * de cr�ation : (31/03/11 10:11:15)
	 * 
	 */
	public String getNOM_EF_COMMUNE_FRANCE() {
		return "NOM_EF_COMMUNE_FRANCE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_COMMUNE_FRANCE Date de cr�ation : (31/03/11 10:11:15)
	 * 
	 */
	public String getVAL_EF_COMMUNE_FRANCE() {
		return getZone(getNOM_EF_COMMUNE_FRANCE());
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (31/03/2011
	 * 11:08:00)
	 * 
	 * @return ArrayList
	 */
	public ArrayList getListeCommune() {
		if (listeCommune == null) {
			listeCommune = new ArrayList();
		}
		return listeCommune;
	}

	/**
	 * Ins�rez la description de la m�thode ici. Date de cr�ation : (31/03/2011
	 * 11:08:00)
	 * 
	 * @param newListeCommune
	 *            ArrayList
	 */
	private void setListeCommune(ArrayList newListeCommune) {
		listeCommune = newListeCommune;
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CHANGE_ORIGINE
			if (testerParametre(request, getNOM_PB_CHANGE_ORIGINE())) {
				return performPB_CHANGE_ORIGINE(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_FRANCE
			if (testerParametre(request, getNOM_PB_RECHERCHER_FRANCE())) {
				return performPB_RECHERCHER_FRANCE(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_OK_COMM
			for (int i = 0; i < getListeCommune().size(); i++) {
				if (testerParametre(request, getNOM_PB_OK_COMM(i))) {
					return performPB_OK_COMM(request, i);
				}
			}

			// Si clic sur le bouton PB_OK_PAYS
			for (int i = 0; i < getListeCommunePays().size(); i++) {
				if (testerParametre(request, getNOM_PB_OK_PAYS(i))) {
					return performPB_OK_PAYS(request, i);
				}
			}

			// Si clic sur le bouton PB_PAYS
			for (int i = 0; i < getListePays().size(); i++) {
				if (testerParametre(request, getNOM_PB_PAYS(i))) {
					return performPB_PAYS(request, i);
				}
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeCOMMUNESelection. Date de cr�ation : (31/03/11
	 * 15:07:33)
	 * 
	 */
	public OeCOMMUNESelection() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (31/03/11 15:07:33)
	 * 
	 */
	public String getJSP() {
		return "OeCOMMUNESelection.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGE_ORIGINE Date de
	 * cr�ation : (31/03/11 15:07:33)
	 * 
	 */
	public String getNOM_PB_CHANGE_ORIGINE() {
		return "NOM_PB_CHANGE_ORIGINE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (31/03/11 15:07:33)
	 * 
	 */
	public boolean performPB_CHANGE_ORIGINE(HttpServletRequest request) throws Exception {

		String newOrigine = Const.COMMUNE_FRANCE;
		String zone = getZone(getNOM_RG_ORIGINE_COMMUNE());
		setFocus(getNOM_EF_COMMUNE_FRANCE());
		if (!zone.equals(getNOM_RB_ORIGINE_COMMUNE_FRANCE())) {
			newOrigine = Const.COMMUNE_ETRANGERE;
			setFocus(getNOM_EF_PAYS());
		}

		if (getOrigine() != newOrigine) {
			setOrigine(newOrigine);
		}
		setListeCommune(null);
		setListeCommunePays(null);
		setListePays(null);
		addZone(getNOM_EF_COMMUNE_FRANCE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PAYS(), Const.CHAINE_VIDE);
		estPaysSelectionne = false;
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_COMMUNE_FR);
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_COMMUNE_ET);
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_PAYS);

		return true;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_PAYS Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE_PAYS(int i) {
		return "NOM_ST_CODE_PAYS" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_PAYS Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE_PAYS(int i) {
		return getZone(getNOM_ST_CODE_PAYS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_PAYS Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_PAYS(int i) {
		return "NOM_ST_LIB_PAYS" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB_PAYS Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_PAYS(int i) {
		return getZone(getNOM_ST_LIB_PAYS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE(int i) {
		return "NOM_ST_CODE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE(int i) {
		return getZone(getNOM_ST_CODE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB Date de cr�ation
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB(int i) {
		return "NOM_ST_LIB" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB(int i) {
		return getZone(getNOM_ST_LIB(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de cr�ation :
	 * (24/01/03 15:27:10)
	 * 
	 */
	public String getNOM_PB_OK_COMM(int i) {
		return "NOM_PB_OK_COMM" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public boolean performPB_OK_COMM(HttpServletRequest request, int elemSelection) throws Exception {
		// R�cup�ration de la commune, selon l'origine
		if (getOrigine().equals(Const.COMMUNE_FRANCE)) {
			// R�cup de la commune s�lectionn�e
			String codeCommune = ((CommuneDepartement) getListeCommune().get(elemSelection)).getCodCommune();
			Commune aCommune = Commune.chercherCommune(getTransaction(), codeCommune);
			if (getTransaction().isErreur()) {
				return false;
			}
			// Alimentation de la variable activite
			VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_COMMUNE_FR, aCommune);
		}

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_PAYS Date de cr�ation :
	 * (24/01/03 15:27:10)
	 * 
	 */
	public String getNOM_PB_PAYS(int i) {
		return "NOM_PB_PAYS" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public boolean performPB_PAYS(HttpServletRequest request, int elemPays) throws Exception {

		// Recup du pays courant
		Pays aPays = (Pays) getListePays().get(elemPays);
		setPaysCourant(aPays);

		// Recup des communes de ce pays si pas encore fait
		if (getHashPaysCommuneEtrangere().get(aPays) == null) {
			getHashPaysCommuneEtrangere().put(aPays, CommuneEtrangere.listerCommuneEtrangerePays(getTransaction(), aPays));
		}

		// Recup des villes
		ArrayList lesCommunes = (ArrayList) getHashPaysCommuneEtrangere().get(aPays);

		setListeCommunePays(lesCommunes);
		estPaysSelectionne = true;
		initialiseListeCommunePays();

		return true;
	}

	private void initialiseListeCommunePays() {
		int indiceCommPays = 0;
		if (getListeCommunePays() != null) {
			for (int i = 0; i < getListeCommunePays().size(); i++) {
				CommuneEtrangere ce = (CommuneEtrangere) getListeCommunePays().get(i);

				addZone(getNOM_ST_CODE(indiceCommPays),
						ce.getCodCommuneEtrangere().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : ce.getCodCommuneEtrangere());
				addZone(getNOM_ST_LIB(indiceCommPays),
						ce.getLibCommuneEtrangere().trim().equals(Const.CHAINE_VIDE) ? "&nbsp;" : ce.getLibCommuneEtrangere());

				indiceCommPays++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_OK Date de cr�ation :
	 * (24/01/03 15:27:10)
	 * 
	 */
	public String getNOM_PB_OK_PAYS(int i) {
		return "NOM_PB_OK_PAYS" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (24/01/03 15:27:10)
	 * 
	 */
	public boolean performPB_OK_PAYS(HttpServletRequest request, int elemSelection) throws Exception {
		// R�cup de la commune s�lectionn�e
		CommuneEtrangere aCommuneEtrangere = (CommuneEtrangere) getListeCommunePays().get(elemSelection);

		// Alimentation de la variable activite
		VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_PAYS, getPaysCourant());
		VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_COMMUNE_ET, aCommuneEtrangere);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	public Pays getPaysCourant() {
		return paysCourant;
	}

	private void setPaysCourant(Pays paysCourant) {
		this.paysCourant = paysCourant;
	}

	public ArrayList getListeCommunePays() {
		if (listeCommunePays == null) {
			listeCommunePays = new ArrayList();
		}
		return listeCommunePays;
	}

	private void setListeCommunePays(ArrayList listeCommunePays) {
		this.listeCommunePays = listeCommunePays;
	}
}
