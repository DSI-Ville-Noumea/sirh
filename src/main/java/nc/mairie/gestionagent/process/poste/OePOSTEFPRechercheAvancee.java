package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OePOSTEFPRechercheAvancee Date de création : (13/09/11 08:45:29)
 * 
 */
public class OePOSTEFPRechercheAvancee extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private String[] LB_TITRE_POSTE;
	private String[] LB_STATUT;

	private ArrayList<StatutFP> listeStatut;
	private ArrayList<TitrePoste> listeTitre;
	private ArrayList<Service> listeServices;
	private ArrayList<FichePoste> listeFP;
	private ArrayList<Affectation> listeAffectation;

	public Hashtable<String, TreeHierarchy> hTree = null;
	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseListeDeroulante();
		initialiseListeService();

		AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null && !agt.getIdAgent().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT(), agt.getNoMatricule());
			performPB_RECHERCHER(request);
		}

		// fillList();
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {

		// Si liste statut vide alors affectation
		if (getLB_STATUT() == LBVide) {
			ArrayList<StatutFP> statut = StatutFP.listerStatutFP(getTransaction());
			setListeStatut(statut);

			int[] tailles = { 20 };
			String[] champs = { "libStatutFP" };
			setLB_STATUT(new FormateListe(tailles, statut, champs).getListeFormatee(true));
		}

		// Si liste localisation vide alors affectation
		if (getLB_TITRE_POSTE() == LBVide) {
			ArrayList<TitrePoste> titre = TitrePoste.listerTitrePoste(getTransaction());
			setListeTitre(titre);

			int[] tailles = { 100 };
			String[] champs = { "libTitrePoste" };
			setLB_TITRE_POSTE(new FormateListe(tailles, titre, champs).getListeFormatee());
		}
	}

	/**
	 * Initialise la liste des services.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList<Service> services = Service.listerServiceActif(getTransaction());
			setListeServices(services);

			// Tri par codeservice
			Collections.sort(getListeServices(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Service s1 = (Service) o1;
					Service s2 = (Service) o2;
					return (s1.getCodService().compareTo(s2.getCodService()));
				}
			});

			// alim de la hTree
			hTree = new Hashtable<String, TreeHierarchy>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService()))
					continue;

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");
				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

			}
		}
	}

	/**
	 * Rempli la liste des fiches de poste trouvées
	 */
	private boolean fillList() throws Exception {
		int indiceFp = 0;
		if (getListeFP() != null) {

			// Si liste vide alors erreur
			if (getListeFP().size() == 0) {	
				//"ERR125", "Impossible de trouver @."
				setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR125", "une fiche de poste avec ces critères"));
				return false;
			}

			for (int i = 0; i < getListeFP().size(); i++) {
				FichePoste fp = (FichePoste) getListeFP().get(i);
				String titreFichePoste = fp.getIdTitrePoste() == null ? "&nbsp;" : TitrePoste.chercherTitrePoste(
						getTransaction(), fp.getIdTitrePoste()).getLibTitrePoste();
				AgentNW agent = AgentNW.chercherAgentAffecteFichePoste(getTransaction(), fp.getIdFichePoste());
				if (agent == null)
					agent = AgentNW.chercherAgentAffecteFichePosteSecondaire(getTransaction(), fp.getIdFichePoste());

				addZone(getNOM_ST_NUM(indiceFp), fp.getNumFP());
				addZone(getNOM_ST_TITRE(indiceFp), titreFichePoste);
				addZone(getNOM_ST_AGENT(indiceFp), agent == null ? "&nbsp;" : agent.getNomAgent().toUpperCase() + " "
						+ agent.getPrenomAgent());

				indiceFp++;
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		// Remise à 0 de la liste des fiches de poste.
		setListeFP(null);

		// Recuperation Service
		String prefixeServ = Const.CHAINE_VIDE;
		if (getVAL_ST_CODE_SERVICE().length() != 0) {
			Service serv = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			prefixeServ = serv.getCodService().substring(
					0,
					Service.isEntite(serv.getCodService()) ? 1 : Service.isDirection(serv.getCodService()) ? 2
							: Service.isDivision(serv.getCodService()) ? 3
									: Service.isSection(serv.getCodService()) ? 4 : 0);
		}

		// Recuperation Statut
		StatutFP statut = null;
		int indiceStatut = (Services.estNumerique(getVAL_LB_STATUT_SELECT()) ? Integer
				.parseInt(getVAL_LB_STATUT_SELECT()) : -1);
		if (indiceStatut > 0)
			statut = (StatutFP) getListeStatut().get(indiceStatut - 1);

		// Recuperation Titre poste et vérification de son existence.
		String idTitre = Const.CHAINE_VIDE;
		for (int i = 0; i < getListeTitre().size(); i++) {
			TitrePoste titre = (TitrePoste) getListeTitre().get(i);
			if (titre.getLibTitrePoste().equals(getVAL_EF_TITRE_POSTE())) {
				idTitre = titre.getIdTitrePoste();
				break;
			}
		}
		if (idTitre.length() == 0 && getVAL_EF_TITRE_POSTE().length() != 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titre de poste"));
			return false;
		}

		// recuperation agent
		AgentNW agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT());
		}

		ArrayList<FichePoste> fp = FichePoste.listerFichePosteAvecCriteresAvances(getTransaction(), prefixeServ,
				statut, idTitre, getVAL_EF_NUM_FICHE_POSTE(), agent);
		setListeFP(fp);

		fillList();

		if (getListeFP().size() == 1) {
			// Alimentation de la variable fichePoste
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_POSTE, (FichePoste) getListeFP().get(0));
			setStatut(STATUT_PROCESS_APPELANT);

		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_PB_VALIDER(int i) {
		return "NOM_PB_VALIDER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request, int elemSelection) throws Exception {

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_POSTE,
				(FichePoste) getListeFP().get(elemSelection));
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_POSTE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_EF_TITRE_POSTE() {
		return "NOM_EF_TITRE_POSTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_POSTE Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_EF_TITRE_POSTE() {
		return getZone(getNOM_EF_TITRE_POSTE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUT Date de création :
	 * (13/09/11 08:45:29)
	 * 
	 */
	private String[] getLB_STATUT() {
		if (LB_STATUT == null)
			LB_STATUT = initialiseLazyLB();
		return LB_STATUT;
	}

	/**
	 * Setter de la liste: LB_STATUT Date de création : (13/09/11 08:45:29)
	 * 
	 */
	private void setLB_STATUT(String[] newLB_STATUT) {
		LB_STATUT = newLB_STATUT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUT Date de création :
	 * (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_LB_STATUT() {
		return "NOM_LB_STATUT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_STATUT_SELECT Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_LB_STATUT_SELECT() {
		return "NOM_LB_STATUT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_STATUT Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_STATUT Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_LB_STATUT_SELECT() {
		return getZone(getNOM_LB_STATUT_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_POSTE Date de
	 * création : (13/09/11 10:33:47)
	 * 
	 */
	private String[] getLB_TITRE_POSTE() {
		if (LB_TITRE_POSTE == null)
			LB_TITRE_POSTE = initialiseLazyLB();
		return LB_TITRE_POSTE;
	}

	/**
	 * Setter de la liste: LB_TITRE_POSTE Date de création : (13/09/11 10:33:47)
	 * 
	 */
	private void setLB_TITRE_POSTE(String[] newLB_TITRE_POSTE) {
		LB_TITRE_POSTE = newLB_TITRE_POSTE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_POSTE Date de
	 * création : (13/09/11 10:33:47)
	 * 
	 */
	public String getNOM_LB_TITRE_POSTE() {
		return "NOM_LB_TITRE_POSTE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_POSTE_SELECT Date de création : (13/09/11 10:33:47)
	 * 
	 */
	public String getNOM_LB_TITRE_POSTE_SELECT() {
		return "NOM_LB_TITRE_POSTE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_POSTE Date de création : (13/09/11 10:33:47)
	 * 
	 */
	public String[] getVAL_LB_TITRE_POSTE() {
		return getLB_TITRE_POSTE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE_POSTE Date de création : (13/09/11 10:33:47)
	 * 
	 */
	public String getVAL_LB_TITRE_POSTE_SELECT() {
		return getZone(getNOM_LB_TITRE_POSTE_SELECT());
	}

	/**
	 * Getter de la liste de titres
	 * 
	 * @return ArrayList
	 */
	public ArrayList<TitrePoste> getListeTitre() {
		return listeTitre;
	}

	/**
	 * Setter de la liste de titres
	 * 
	 * @param listeTitre
	 */
	private void setListeTitre(ArrayList<TitrePoste> listeTitre) {
		this.listeTitre = listeTitre;
	}

	/**
	 * Getter de la liste de statuts
	 * 
	 * @return ArrayList
	 */
	private ArrayList<StatutFP> getListeStatut() {
		return listeStatut;
	}

	/**
	 * Setter de la liste de statuts
	 * 
	 * @param listeStatut
	 */
	private void setListeStatut(ArrayList<StatutFP> listeStatut) {
		this.listeStatut = listeStatut;
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
		return getNOM_EF_TITRE_POSTE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne la liste des services.
	 * 
	 * @return listeServices
	 */
	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	/**
	 * Met à jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Constructeur du process OePOSTEFPRechercheAvancee. Date de création :
	 * (13/09/11 11:47:15)
	 * 
	 */
	public OePOSTEFPRechercheAvancee() {
		super();
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (13/09/11 11:47:15)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne une hashTable de la hiérarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	/**
	 * Getter liste Fiche de poste
	 * 
	 * @return listeFP
	 */
	public ArrayList<FichePoste> getListeFP() {
		if (listeFP == null)
			listeFP = new ArrayList<FichePoste>();
		return listeFP;
	}

	/**
	 * Setter liste Fiche de poste
	 * 
	 * @param listeFP
	 */
	private void setListeFP(ArrayList<FichePoste> listeFP) {
		this.listeFP = listeFP;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (13/09/11 08:45:29)
	 * 
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

			// Si clic sur le bouton PB_VALIDER
			for (int i = 0; i < getListeFP().size(); i++) {
				if (testerParametre(request, getNOM_PB_VALIDER(i))) {
					return performPB_VALIDER(request, i);
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AFF
			if (testerParametre(request, getNOM_PB_RECHERCHER_AFF())) {
				return performPB_RECHERCHER_AFF(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (07/11/11 16:40:08)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFPRechercheAvancee.jsp";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_FICHE_POSTE
	 * Date de création : (07/11/11 16:40:08)
	 * 
	 */
	public String getNOM_EF_NUM_FICHE_POSTE() {
		return "NOM_EF_NUM_FICHE_POSTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_FICHE_POSTE Date de création : (07/11/11 16:40:08)
	 * 
	 */
	public String getVAL_EF_NUM_FICHE_POSTE() {
		return getZone(getNOM_EF_NUM_FICHE_POSTE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enlève le service selectionnée
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM(int i) {
		return "NOM_ST_NUM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM(int i) {
		return getZone(getNOM_ST_NUM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TITRE(int i) {
		return "NOM_ST_TITRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TITRE(int i) {
		return getZone(getNOM_ST_TITRE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_FICHE_POSTE_AFF
	 * Date de création : (07/11/11 16:40:08)
	 * 
	 */
	public String getNOM_EF_NUM_FICHE_POSTE_AFF() {
		return "NOM_EF_NUM_FICHE_POSTE_AFF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_FICHE_POSTE_AFF Date de création : (07/11/11 16:40:08)
	 * 
	 */
	public String getVAL_EF_NUM_FICHE_POSTE_AFF() {
		return getZone(getNOM_EF_NUM_FICHE_POSTE_AFF());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AFF Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AFF() {
		return "NOM_PB_RECHERCHER_AFF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_RECHERCHER_AFF(HttpServletRequest request) throws Exception {
		// Recherche de la fiche de poste
		if (getVAL_EF_NUM_FICHE_POSTE_AFF() != null && !getVAL_EF_NUM_FICHE_POSTE_AFF().equals(Const.CHAINE_VIDE)) {
			FichePoste fiche = FichePoste.chercherFichePosteAvecNumeroFP(getTransaction(),
					getVAL_EF_NUM_FICHE_POSTE_AFF());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_NUM_FICHE_POSTE_AFF()));
				return false;
			}
			if (fiche != null && fiche.getIdFichePoste() != null) {
				// on alimente une liste d'affectation que l'on affiche
				ArrayList<Affectation> listeAff = Affectation.listerAffectationAvecFPOrderDatDeb(getTransaction(),
						fiche);
				setListeAffectation(listeAff);
				initialiseListeAff();

			} else {
				setStatut(STATUT_MEME_PROCESS, true,
						MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_NUM_FICHE_POSTE_AFF()));
				return false;
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR982"));
			return false;
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseListeAff() throws Exception {
		for (int i = 0; i < getListeAffectation().size(); i++) {
			Affectation a = (Affectation) getListeAffectation().get(i);
			FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), a.getIdFichePoste());
			// Titre du poste
			TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
			// Service
			Service direction = Service.getDirection(getTransaction(), fp.getIdServi());
			Service service = Service.getSection(getTransaction(), fp.getIdServi());
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), a.getIdAgent());

			addZone(getNOM_ST_DIR_AFF(i), direction != null ? direction.getCodService() : "&nbsp;");
			addZone(getNOM_ST_SERV_AFF(i), service != null ? service.getLibService() : "&nbsp;");
			addZone(getNOM_ST_AGENT_AFF(i),
					agent.getNomAgent() + " " + agent.getPrenomAgent() + "(" + agent.getNoMatricule() + ")");
			addZone(getNOM_ST_DATE_DEBUT_AFF(i), a.getDateDebutAff());
			addZone(getNOM_ST_DATE_FIN_AFF(i),
					a.getDateFinAff() == null || a.getDateFinAff().equals(Const.CHAINE_VIDE) ? "&nbsp;" : a
							.getDateFinAff());
			addZone(getNOM_ST_NUM_FP_AFF(i), fp.getNumFP().equals(Const.CHAINE_VIDE) ? "&nbsp;" : fp.getNumFP());
			addZone(getNOM_ST_TITRE_AFF(i), tp == null ? "&nbsp;" : tp.getLibTitrePoste());

		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT_AFF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AGENT_AFF(int i) {
		return "NOM_ST_AGENT_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT_AFF Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AGENT_AFF(int i) {
		return getZone(getNOM_ST_AGENT_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIR_AFF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DIR_AFF(int i) {
		return "NOM_ST_DIR_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIR_AFF Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DIR_AFF(int i) {
		return getZone(getNOM_ST_DIR_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERV_AFF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SERV_AFF(int i) {
		return "NOM_ST_SERV_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERV_AFF Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERV_AFF(int i) {
		return getZone(getNOM_ST_SERV_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT_AFF Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT_AFF(int i) {
		return "NOM_ST_DATE_DEBUT_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT_AFF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT_AFF(int i) {
		return getZone(getNOM_ST_DATE_DEBUT_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN_AFF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN_AFF(int i) {
		return "NOM_ST_DATE_FIN_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN_AFF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN_AFF(int i) {
		return getZone(getNOM_ST_DATE_FIN_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_FP_AFF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NUM_FP_AFF(int i) {
		return "NOM_ST_NUM_FP_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_FP_AFF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NUM_FP_AFF(int i) {
		return getZone(getNOM_ST_NUM_FP_AFF(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE_AFF Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TITRE_AFF(int i) {
		return "NOM_ST_TITRE_AFF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE_AFF Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TITRE_AFF(int i) {
		return getZone(getNOM_ST_TITRE_AFF(i));
	}

	public ArrayList<Affectation> getListeAffectation() {
		return listeAffectation == null ? new ArrayList<Affectation>() : listeAffectation;
	}

	public void setListeAffectation(ArrayList<Affectation> listeAffectation) {
		this.listeAffectation = listeAffectation;
	}
}
