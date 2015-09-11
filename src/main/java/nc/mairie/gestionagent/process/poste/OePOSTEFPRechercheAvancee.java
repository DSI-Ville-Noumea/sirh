package nc.mairie.gestionagent.process.poste;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.StatutFPDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.StatutEntiteEnum;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAdsService;

import org.springframework.context.ApplicationContext;

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
	private String[] LB_STATUT_SERVICE;

	private ArrayList<StatutFP> listeStatut;
	private ArrayList<StatutEntiteEnum> listeStatutEntite;
	private ArrayList<TitrePoste> listeTitre;
	private ArrayList<FichePoste> listeFP;
	private ArrayList<Affectation> listeAffectation;

	public String focus = null;

	private TitrePosteDao titrePosteDao;
	private StatutFPDao statutFPDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	private IAdsService adsService;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();
		initialiseListeDeroulante();

		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null) {
			addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
			performPB_RECHERCHER(request);
		}

		// fillList();
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getStatutFPDao() == null) {
			setStatutFPDao(new StatutFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
	}

	/**
	 * Initialise les listes deroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {

		// Si liste statut vide alors affectation
		if (getLB_STATUT_SERVICE() == LBVide) {
			ArrayList<StatutEntiteEnum> statutEntite = (ArrayList<StatutEntiteEnum>) StatutEntiteEnum.getAllStatutEntiteEnum();
			setListeStatutEntite(statutEntite);

			if (getListeStatutEntite().size() != 0) {
				int[] tailles = { 20 };
				FormateListe aFormat = new FormateListe(tailles);
				for (StatutEntiteEnum st : getListeStatutEntite()) {
					String ligne[] = { st.getLibStatutEntite() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_STATUT_SERVICE(aFormat.getListeFormatee(true));
			} else {
				setLB_STATUT_SERVICE(null);
			}
		}

		// Si liste statut vide alors affectation
		if (getLB_STATUT() == LBVide) {
			ArrayList<StatutFP> statut = (ArrayList<StatutFP>) getStatutFPDao().listerStatutFP();
			setListeStatut(statut);

			if (getListeStatut().size() != 0) {
				int[] tailles = { 20 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<StatutFP> list = getListeStatut().listIterator(); list.hasNext();) {
					StatutFP de = (StatutFP) list.next();
					String ligne[] = { de.getLibStatutFp() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_STATUT(aFormat.getListeFormatee(true));
			} else {
				setLB_STATUT(null);
			}
		}

		// Si liste localisation vide alors affectation
		if (getLB_TITRE_POSTE() == LBVide) {
			ArrayList<TitrePoste> titre = getTitrePosteDao().listerTitrePoste();
			setListeTitre(titre);

			if (getListeTitre().size() != 0) {
				int[] tailles = { 100 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<TitrePoste> list = getListeTitre().listIterator(); list.hasNext();) {
					TitrePoste de = (TitrePoste) list.next();
					String ligne[] = { de.getLibTitrePoste() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_TITRE_POSTE(aFormat.getListeFormatee());
			} else {
				setLB_TITRE_POSTE(null);
			}
		}
	}

	/**
	 * Rempli la liste des fiches de poste trouvées
	 */
	private boolean fillList() throws Exception {
		if (getListeFP() != null) {

			// Si liste vide alors erreur
			if (getListeFP().size() == 0) {
				// "ERR125", "Impossible de trouver @."
				setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR125", "une fiche de poste avec ces critéres"));
				return false;
			}

			for (int i = 0; i < getListeFP().size(); i++) {
				FichePoste fp = (FichePoste) getListeFP().get(i);
				Integer indiceFp = fp.getIdFichePoste();
				String titreFichePoste = fp.getIdTitrePoste() == null ? "&nbsp;" : getTitrePosteDao().chercherTitrePoste(fp.getIdTitrePoste()).getLibTitrePoste();
				Agent agent = null;
				try {
					agent = getAgentDao().chercherAgentAffecteFichePoste(fp.getIdFichePoste());
				} catch (Exception e) {
					try {
						agent = getAgentDao().chercherAgentAffecteFichePosteSecondaire(fp.getIdFichePoste());
					} catch (Exception e2) {

					}
				}

				EntiteDto serv = adsService.getEntiteByIdEntite(fp.getIdServiceAds());

				addZone(getNOM_ST_NUM(indiceFp), fp.getNumFp());
				addZone(getNOM_ST_TITRE(indiceFp), titreFichePoste);
				addZone(getNOM_ST_AGENT(indiceFp), agent == null ? "&nbsp;" : agent.getNomAgent().toUpperCase() + " " + agent.getPrenomAgent());
				addZone(getNOM_ST_SERVICE(indiceFp), serv == null ? "Erreur Recuperationservice" : serv.getLabel());

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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		// Remise a 0 de la liste des fiches de poste.
		setListeFP(null);

		// Recuperation Service
		List<Integer> listIdServiceADSSeelect = new ArrayList<Integer>();
		if (getVAL_ST_ID_SERVICE_ADS().length() != 0) {

			if (getVAL_CK_WITH_SERVICE_ENFANT().equals(getCHECKED_ON())) {
				listIdServiceADSSeelect.addAll(adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(getVAL_ST_ID_SERVICE_ADS())));
			} else {
				EntiteDto entiteChoisie = adsService.getEntiteByIdEntite(new Integer(getVAL_ST_ID_SERVICE_ADS()));
				listIdServiceADSSeelect.add(entiteChoisie.getIdEntite());
			}
		}

		// Recuperation Statut
		StatutFP statut = null;
		int indiceStatut = (Services.estNumerique(getVAL_LB_STATUT_SELECT()) ? Integer.parseInt(getVAL_LB_STATUT_SELECT()) : -1);
		if (indiceStatut > 0)
			statut = (StatutFP) getListeStatut().get(indiceStatut - 1);

		// Recuperation Titre poste et Vérification de son existence.
		Integer idTitre = null;
		for (int i = 0; i < getListeTitre().size(); i++) {
			TitrePoste titre = (TitrePoste) getListeTitre().get(i);
			if (titre.getLibTitrePoste().equals(getVAL_EF_TITRE_POSTE())) {
				idTitre = titre.getIdTitrePoste();
				break;
			}
		}
		if (idTitre == null && getVAL_EF_TITRE_POSTE().length() != 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de postes"));
			return false;
		}

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			try {
				agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
			} catch (Exception e) {
				// "ERR1120",
				// "Aucun agent correspondant à votre recherche. Merci de passer par la recherche avancée des agents."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1120"));
				return false;
			}
		} else if (getVAL_ST_NOM_AGENT().length() != 0) {
			List<Agent> aListe = getAgentDao().listerAgentAvecNomCommencant(getVAL_ST_NOM_AGENT());
			if (aListe.size() == 0) {
				// "ERR1120",
				// "Aucun agent correspondant à votre recherche. Merci de passer par la recherche avancée des agents."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1120"));
				return false;
			} else if (aListe.size() == 1) {
				agent = aListe.get(0);
			} else if (aListe.size() > 1) {
				// "ERR1119",
				// "Plusieurs agents correspondent à votre recherche. Merci de passer par la recherche avancée des agents."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1119"));
				return false;
			}
		} else if (getVAL_ST_MATR_AGENT().length() != 0) {
			if (!Services.estNumerique(getVAL_ST_MATR_AGENT())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "matricule"));
				return false;
			} else {
				try {
					agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_MATR_AGENT()));
				} catch (Exception e) {
					// "ERR1120",
					// "Aucun agent correspondant à votre recherche. Merci de passer par la recherche avancée des agents."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR1120"));
					return false;
				}
			}
		}

		// on traite le statut des entité en fonction de l'entité choisie
		// Recuperation statut entite
		List<Integer> listIdServiceADS = new ArrayList<Integer>();
		StatutEntiteEnum statutEntite = null;
		int indiceStatutEntite = (Services.estNumerique(getVAL_LB_STATUT_SERVICE_SELECT()) ? Integer.parseInt(getVAL_LB_STATUT_SERVICE_SELECT()) : -1);
		if (indiceStatutEntite > 0)
			statutEntite = (StatutEntiteEnum) getListeStatutEntite().get(indiceStatutEntite - 1);

		if (statutEntite != null) {
			if (listIdServiceADSSeelect.size() == 0) {
				for (EntiteDto dto : adsService.getListEntiteByStatut(statutEntite.getIdRefStatutEntite())) {
					listIdServiceADS.add(dto.getIdEntite());
				}
			} else {
				for (Integer idEntite : listIdServiceADSSeelect) {
					EntiteDto entiteChoisie = adsService.getEntiteByIdEntite(idEntite);
					if (entiteChoisie.getIdStatut().toString().equals(String.valueOf(statutEntite.getIdRefStatutEntite()))) {
						listIdServiceADS.add(idEntite);
					}
				}
			}
			// on ajoute cette ligne pour ne pas retourner toutes les FDP
			if (listIdServiceADS.size() == 0) {
				listIdServiceADS.add(0);
			}
		} else {
			listIdServiceADS.addAll(listIdServiceADSSeelect);
		}

		boolean isCocheObservation = getVAL_CK_WITH_COMMENTAIRE().equals(getCHECKED_ON());

		ArrayList<FichePoste> fp = getFichePosteDao().listerFichePosteAvecCriteresAvances(listIdServiceADS, statut == null ? null : statut.getIdStatutFp(), idTitre,
				getVAL_EF_NUM_FICHE_POSTE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_NUM_FICHE_POSTE(), agent == null ? null : agent.getIdAgent(), isCocheObservation);
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request, int elemSelection) throws Exception {
		FichePoste fp = getFichePosteDao().chercherFichePoste(elemSelection);

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_POSTE, fp);
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
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
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
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
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
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
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
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
			for (int j = 0; j < getListeFP().size(); j++) {
				FichePoste fp = getListeFP().get(j);
				Integer i = fp.getIdFichePoste();
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enleve l'agent selectionnée
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enleve le service selectionnée
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public boolean performPB_RECHERCHER_AFF(HttpServletRequest request) throws Exception {
		// Recherche de la fiche de poste
		if (getVAL_EF_NUM_FICHE_POSTE_AFF() != null && !getVAL_EF_NUM_FICHE_POSTE_AFF().equals(Const.CHAINE_VIDE)) {
			try {
				FichePoste fiche = getFichePosteDao().chercherFichePosteAvecNumeroFP(getVAL_EF_NUM_FICHE_POSTE_AFF());
				if (fiche != null && fiche.getIdFichePoste() != null) {
					// on alimente une liste d'affectation que l'on affiche
					ArrayList<Affectation> listeAff = getAffectationDao().listerAffectationAvecFPOrderDatDeb(fiche.getIdFichePoste());
					setListeAffectation(listeAff);
					initialiseListeAff();

				} else {
					setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_NUM_FICHE_POSTE_AFF()));
					return false;
				}
			} catch (Exception e) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_NUM_FICHE_POSTE_AFF()));
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (int i = 0; i < getListeAffectation().size(); i++) {
			Affectation a = (Affectation) getListeAffectation().get(i);
			FichePoste fp = getFichePosteDao().chercherFichePoste(a.getIdFichePoste());
			// Titre du poste
			TitrePoste tp = getTitrePosteDao().chercherTitrePoste(fp.getIdTitrePoste());
			// Service
			EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
			EntiteDto service = adsService.getAffichageSection(fp.getIdServiceAds());
			Agent agent = getAgentDao().chercherAgent(a.getIdAgent());

			addZone(getNOM_ST_DIR_AFF(i), direction != null ? direction.getSigle() : "&nbsp;");
			addZone(getNOM_ST_SERV_AFF(i), service != null ? service.getLabel() : "&nbsp;");
			addZone(getNOM_ST_AGENT_AFF(i), agent.getNomAgent() + " " + agent.getPrenomAgent() + "(" + agent.getNomatr() + ")");
			addZone(getNOM_ST_DATE_DEBUT_AFF(i), sdf.format(a.getDateDebutAff()));
			addZone(getNOM_ST_DATE_FIN_AFF(i), a.getDateFinAff() == null ? "&nbsp;" : sdf.format(a.getDateFinAff()));
			addZone(getNOM_ST_NUM_FP_AFF(i), fp.getNumFp().equals(Const.CHAINE_VIDE) ? "&nbsp;" : fp.getNumFp());
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

	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	public TitrePosteDao getTitrePosteDao() {
		return titrePosteDao;
	}

	public void setTitrePosteDao(TitrePosteDao titrePosteDao) {
		this.titrePosteDao = titrePosteDao;
	}

	public StatutFPDao getStatutFPDao() {
		return statutFPDao;
	}

	public void setStatutFPDao(StatutFPDao statutFPDao) {
		this.statutFPDao = statutFPDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public AffectationDao getAffectationDao() {
		return affectationDao;
	}

	public void setAffectationDao(AffectationDao affectationDao) {
		this.affectationDao = affectationDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreePrevisionActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	private String[] getLB_STATUT_SERVICE() {
		if (LB_STATUT_SERVICE == null)
			LB_STATUT_SERVICE = initialiseLazyLB();
		return LB_STATUT_SERVICE;
	}

	private void setLB_STATUT_SERVICE(String[] newLB_STATUT_SERVICE) {
		LB_STATUT_SERVICE = newLB_STATUT_SERVICE;
	}

	public String getNOM_LB_STATUT_SERVICE() {
		return "NOM_LB_STATUT_SERVICE";
	}

	public String getNOM_LB_STATUT_SERVICE_SELECT() {
		return "NOM_LB_STATUT_SERVICE_SELECT";
	}

	public String[] getVAL_LB_STATUT_SERVICE() {
		return getLB_STATUT_SERVICE();
	}

	public String getVAL_LB_STATUT_SERVICE_SELECT() {
		return getZone(getNOM_LB_STATUT_SERVICE_SELECT());
	}

	public ArrayList<StatutEntiteEnum> getListeStatutEntite() {
		return listeStatutEntite == null ? new ArrayList<StatutEntiteEnum>() : listeStatutEntite;
	}

	public void setListeStatutEntite(ArrayList<StatutEntiteEnum> listeStatutEntite) {
		this.listeStatutEntite = listeStatutEntite;
	}

	public String getNOM_ST_NOM_AGENT() {
		return "NOM_ST_NOM_AGENT";
	}

	public String getVAL_ST_NOM_AGENT() {
		return getZone(getNOM_ST_NOM_AGENT());
	}

	public String getNOM_ST_MATR_AGENT() {
		return "NOM_ST_MATR_AGENT";
	}

	public String getVAL_ST_MATR_AGENT() {
		return getZone(getNOM_ST_MATR_AGENT());
	}

	public String getNOM_CK_WITH_SERVICE_ENFANT() {
		return "NOM_CK_WITH_SERVICE_ENFANT";
	}

	public String getVAL_CK_WITH_SERVICE_ENFANT() {
		return getZone(getNOM_CK_WITH_SERVICE_ENFANT());
	}

	public String getNOM_CK_WITH_COMMENTAIRE() {
		return "NOM_CK_WITH_COMMENTAIRE";
	}

	public String getVAL_CK_WITH_COMMENTAIRE() {
		return getZone(getNOM_CK_WITH_COMMENTAIRE());
	}
}
