package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.parametrage.CodeRome;
import nc.mairie.metier.parametrage.DomaineEmploi;
import nc.mairie.metier.parametrage.FamilleEmploi;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTEFERechercheAvancee
 * Date de création : (13/09/11 08:45:29)
     *
 */
public class OePOSTEFERechercheAvancee extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] LB_FAMILLE_EMPLOI;
	private String[] LB_DOMAINE_EMPLOI;

	private ArrayList<DomaineEmploi> listeDomaineEmploi;
	private ArrayList<FamilleEmploi> listeFamilleEmploi;
	private ArrayList<FicheEmploi> listeFE;
	private ArrayList<CodeRome> listeCodeRome;
	private ArrayList<FicheEmploi> listeFormNomEmploi;

	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP
	 * Alimentation des listes, s'il y en a, avec setListeLB_XXX()
	 * ATTENTION : Les Objets dans la liste doivent avoir les Fields PUBLIC
	 * Utilisation de la méthode addZone(getNOMxxx, String);
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseListeDeroulante();
		//fillList();
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste famille vide alors affectation
		if (getLB_FAMILLE_EMPLOI() == LBVide) {
			ArrayList<FamilleEmploi> fam = FamilleEmploi.listerFamilleEmploi(getTransaction());
			setListeFamilleEmploi(fam);

			int[] tailles = { 4, 100 };
			String[] champs = { "codeFamilleEmploi", "libFamilleEmploi" };
			setLB_FAMILLE_EMPLOI(new FormateListe(tailles, fam, champs).getListeFormatee(true));
		}
		// Si liste domaine vide alors affectation
		if (getLB_DOMAINE_EMPLOI() == LBVide) {
			ArrayList<DomaineEmploi> dom = DomaineEmploi.listerDomaineEmploi(getTransaction());
			setListeDomaineEmploi(dom);

			int[] tailles = { 4, 100 };
			String[] champs = { "codeDomaineEmploi", "libDomaineEmploi" };
			setLB_DOMAINE_EMPLOI(new FormateListe(tailles, dom, champs).getListeFormatee(true));
		}

		// Si liste code rome vide alors affectation
		if (getListeCodeRome().size() == 0) {
			ArrayList<CodeRome> codeRome = CodeRome.listerCodeRome(getTransaction());
			setListeCodeRome(codeRome);
		}
		
		// Si liste Nom Emploi vide alors affectation
		if (getListeFormNomEmploi().size() == 0) {
			ArrayList<FicheEmploi> listFicheEmploi = FicheEmploi.listerFicheEmploi(getTransaction());
			setListeFormNomEmploi(listFicheEmploi);
		}
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
		return getNOM_LB_DOMAINE_EMPLOI();
	}

	/**
	 * @param focus focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Rempli la liste des fiches emploi trouvées
	 */
	private boolean fillList() throws Exception {
		int indiceFe = 0;
		if (getListeFE() != null) {
			for (int i = 0; i < getListeFE().size(); i++) {
				FicheEmploi fe = (FicheEmploi) getListeFE().get(i);

				addZone(getNOM_ST_REF(indiceFe), fe.getRefMairie());
				addZone(getNOM_ST_NOM(indiceFe), fe.getNomMetierEmploi().equals(Const.CHAINE_VIDE) ? "&nbsp;" : fe.getNomMetierEmploi());

				indiceFe++;
			}

			//Si liste vide alors erreur
			if (getListeFE().size() == 0) {
				setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
				return false;
			}
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_ANNULER
	 * Date de création : (13/09/11 08:45:29)
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
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_RECHERCHER
	 * Date de création : (13/09/11 08:45:29)
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
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		//Remise à 0 de la liste des fiches de poste.
		setListeFE(null);

		//Recuperation Domaine Emploi
		DomaineEmploi domaineEmploi = null;
		int indiceDomaine = (Services.estNumerique(getVAL_LB_DOMAINE_EMPLOI_SELECT()) ? Integer.parseInt(getVAL_LB_DOMAINE_EMPLOI_SELECT()) : -1);
		if (indiceDomaine > 0)
			domaineEmploi = (DomaineEmploi) getListeDomaineEmploi().get(indiceDomaine - 1);

		//Recuperation Famille emploi
		FamilleEmploi famEmploi = null;
		int indiceFamille = (Services.estNumerique(getVAL_LB_FAMILLE_EMPLOI_SELECT()) ? Integer.parseInt(getVAL_LB_FAMILLE_EMPLOI_SELECT()) : -1);
		if (indiceFamille > 0)
			famEmploi = (FamilleEmploi) getListeFamilleEmploi().get(indiceFamille - 1);

		//Recuperation Code Rome
		String codeRome = getVAL_EF_CODE_ROME_RECH();

		//Recuperation ref Mairie
		String refMairie = getVAL_EF_REF_MAIRIE_RECH();
		
		//Recuperation Nom mploi
		String nomEmploi = getVAL_EF_NOM_EMPLOI();

		ArrayList<FicheEmploi> fe = FicheEmploi.listerFicheEmploiAvecCriteresAvances(getTransaction(), domaineEmploi, famEmploi, codeRome, refMairie, nomEmploi);
		setListeFE(fe);

		return fillList();
	}

	/**
	 * Constructeur du process OePOSTEFERechercheAvancee.
	 * Date de création : (13/09/11 11:47:15)
     *
	 */
	public OePOSTEFERechercheAvancee() {
		super();
	}

	/**
	 * Getter liste Fiche emploi
	 * @return listeFE
	 */
	public ArrayList<FicheEmploi> getListeFE() {
		if (listeFE == null)
			listeFE = new ArrayList<FicheEmploi>();
		return listeFE;
	}

	/**
	 * Setter liste Fiche emploi
	 * @param listeFE
	 */
	private void setListeFE(ArrayList<FicheEmploi> listeFE) {
		this.listeFE = listeFE;
	}

	/**
	 * Getter de la liste des domaines emploi
	 * @return ArrayList
	 */
	private ArrayList<DomaineEmploi> getListeDomaineEmploi() {
		return listeDomaineEmploi;
	}

	/**
	 * Setter de la liste des domaines emploi
	 * @param listeDomaineEmploi
	 */
	private void setListeDomaineEmploi(ArrayList<DomaineEmploi> listeDomaineEmploi) {
		this.listeDomaineEmploi = listeDomaineEmploi;
	}

	/**
	 * Getter de la liste des familles emploi
	 * @return ArrayList
	 */
	private ArrayList<FamilleEmploi> getListeFamilleEmploi() {
		return listeFamilleEmploi;
	}

	/**
	 * Setter de la liste des familles emploi
	 * @param listeFamilleEmploi
	 */
	private void setListeFamilleEmploi(ArrayList<FamilleEmploi> listeFamilleEmploi) {
		this.listeFamilleEmploi = listeFamilleEmploi;
	}
	
	/**
	 * Getter liste Fiche emploi (liste emploi pour autocompletion champ Nom Emploi)
	 * @return listeFE
	 */
	public ArrayList<FicheEmploi> getListeFormNomEmploi() {
		if (listeFormNomEmploi == null)
			listeFormNomEmploi = new ArrayList<FicheEmploi>();
		return listeFormNomEmploi;
	}

	/**
	 * Setter liste Fiche emploi (liste emploi pour autocompletion champ Nom Emploi)
	 * @param listeFE
	 */
	private void setListeFormNomEmploi(ArrayList<FicheEmploi> listeFormNomEmploi) {
		this.listeFormNomEmploi = listeFormNomEmploi;
	}

	/**
	 * Getter de la liste avec un lazy initialize :
	 * LB_DOMAINE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	private String[] getLB_DOMAINE_EMPLOI() {
		if (LB_DOMAINE_EMPLOI == null)
			LB_DOMAINE_EMPLOI = initialiseLazyLB();
		return LB_DOMAINE_EMPLOI;
	}

	/**
	 * Setter de la liste:
	 * LB_DOMAINE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	private void setLB_DOMAINE_EMPLOI(String[] newLB_DOMAINE_EMPLOI) {
		LB_DOMAINE_EMPLOI = newLB_DOMAINE_EMPLOI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP :
	 * NOM_LB_DOMAINE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String getNOM_LB_DOMAINE_EMPLOI() {
		return "NOM_LB_DOMAINE_EMPLOI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DOMAINE_EMPLOI_SELECT
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String getNOM_LB_DOMAINE_EMPLOI_SELECT() {
		return "NOM_LB_DOMAINE_EMPLOI_SELECT";
	}

	/**
	 * Méthode à personnaliser
	 * Retourne la valeur à afficher pour la zone de la JSP :
	 * LB_DOMAINE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String[] getVAL_LB_DOMAINE_EMPLOI() {
		return getLB_DOMAINE_EMPLOI();
	}

	/**
	 * Méthode à personnaliser
	 * Retourne l'indice à sélectionner pour la zone de la JSP :
	 * LB_DOMAINE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String getVAL_LB_DOMAINE_EMPLOI_SELECT() {
		return getZone(getNOM_LB_DOMAINE_EMPLOI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize :
	 * LB_FAMILLE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	private String[] getLB_FAMILLE_EMPLOI() {
		if (LB_FAMILLE_EMPLOI == null)
			LB_FAMILLE_EMPLOI = initialiseLazyLB();
		return LB_FAMILLE_EMPLOI;
	}

	/**
	 * Setter de la liste:
	 * LB_FAMILLE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	private void setLB_FAMILLE_EMPLOI(String[] newLB_FAMILLE_EMPLOI) {
		LB_FAMILLE_EMPLOI = newLB_FAMILLE_EMPLOI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP :
	 * NOM_LB_FAMILLE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String getNOM_LB_FAMILLE_EMPLOI() {
		return "NOM_LB_FAMILLE_EMPLOI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_FAMILLE_EMPLOI_SELECT
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String getNOM_LB_FAMILLE_EMPLOI_SELECT() {
		return "NOM_LB_FAMILLE_EMPLOI_SELECT";
	}

	/**
	 * Méthode à personnaliser
	 * Retourne la valeur à afficher pour la zone de la JSP :
	 * LB_FAMILLE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String[] getVAL_LB_FAMILLE_EMPLOI() {
		return getLB_FAMILLE_EMPLOI();
	}

	/**
	 * Méthode à personnaliser
	 * Retourne l'indice à sélectionner pour la zone de la JSP :
	 * LB_FAMILLE_EMPLOI
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public String getVAL_LB_FAMILLE_EMPLOI_SELECT() {
		return getZone(getNOM_LB_FAMILLE_EMPLOI_SELECT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_REF_MAIRIE_RECH
	 * Date de création : (21/06/11 16:27:37)
     *
	 */
	public String getNOM_EF_REF_MAIRIE_RECH() {
		return "NOM_EF_REF_MAIRIE_RECH";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie  :
	 * EF_REF_MAIRIE_RECH
	 * Date de création : (21/06/11 16:27:37)
     *
	 */
	public String getVAL_EF_REF_MAIRIE_RECH() {
		return getZone(getNOM_EF_REF_MAIRIE_RECH());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_CODE_ROME_RECH
	 * Date de création : (21/06/11 16:27:37)
     *
	 */
	public String getNOM_EF_CODE_ROME_RECH() {
		return "NOM_EF_CODE_ROME_RECH";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie  :
	 * EF_CODE_ROME_RECH
	 * Date de création : (21/06/11 16:27:37)
     *
	 */
	public String getVAL_EF_CODE_ROME_RECH() {
		return getZone(getNOM_EF_CODE_ROME_RECH());
	}
	
	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM_EMPLOI Date de
	 */
	public String getNOM_EF_NOM_EMPLOI() {
		return "NOM_EF_NOM_EMPLOI";
	}
	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NOM_EMPLOI 
	 * 
	 */
	public String getVAL_EF_NOM_EMPLOI() {
		return getZone(getNOM_EF_NOM_EMPLOI());
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : 
	 * en fonction du bouton de la JSP 
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		//Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			//Si clic sur le bouton PB_TRI
			if (testerParametre(request, getNOM_PB_TRI())) {
				return performPB_TRI(request);
			}

			//Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			//Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			for (int i = 0; i < getListeFE().size(); i++) {
				if (testerParametre(request, getNOM_PB_VALIDER(i))) {
					return performPB_VALIDER(request, i);
				}
			}

		}
		//Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process
	 * Zone à utiliser dans un champ caché dans chaque formulaire de la JSP.
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public String getJSP() {
		return "OePOSTEFERechercheAvancee.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_TRI
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public String getNOM_PB_TRI() {
		return "NOM_PB_TRI";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP.
	 * - Implémente les règles de gestion du process
	 * - Positionne un statut en fonction de ces règles :
	 *   setStatut(STATUT, boolean veutRetour) ou setStatut(STATUT,Message d'erreur)
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public boolean performPB_TRI(HttpServletRequest request) throws Exception {
		String tri = "refMairie";
		if (getVAL_RG_TRI().equals(getNOM_RB_TRI_REFMAIRIE())) {
			tri = "refMairie";
		} else if (getVAL_RG_TRI().equals(getNOM_RB_TRI_DESC())) {
			tri = "nomMetierEmploi";
		}

		//Remplissage de la liste
		String[] colonnes = { tri };
		boolean[] ordres = { true };
		setListeFE(Services.trier(getListeFE(), colonnes, ordres));
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TRI
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public String getNOM_RG_TRI() {
		return "NOM_RG_TRI";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TRI
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public String getVAL_RG_TRI() {
		return getZone(getNOM_RG_TRI());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP :
	 * RB_TRI_DESC
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public String getNOM_RB_TRI_DESC() {
		return "NOM_RB_TRI_DESC";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP :
	 * RB_TRI_REFMAIRIE
	 * Date de création : (07/11/11 15:51:20)
     *
	 */
	public String getNOM_RB_TRI_REFMAIRIE() {
		return "NOM_RB_TRI_REFMAIRIE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_REF
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getNOM_ST_REF(int i) {
		return "NOM_ST_REF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_REF
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getVAL_ST_REF(int i) {
		return getZone(getNOM_ST_REF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_NOM
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP  pour la zone :
	 * ST_NOM
	 * Date de création : (18/08/11 10:21:15)
     *
	 */
	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_VALIDER
	 * Date de création : (13/09/11 08:45:29)
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
	 * Date de création : (13/09/11 08:45:29)
     *
	 */
	public boolean performPB_VALIDER(HttpServletRequest request, int elemSelection) throws Exception {

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, (FicheEmploi) getListeFE().get(elemSelection));
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	public ArrayList<CodeRome> getListeCodeRome() {
		if (listeCodeRome == null)
			listeCodeRome = new ArrayList<CodeRome>();
		return listeCodeRome;
	}

	private void setListeCodeRome(ArrayList<CodeRome> listeCodeRome) {
		this.listeCodeRome = listeCodeRome;
	}
}
