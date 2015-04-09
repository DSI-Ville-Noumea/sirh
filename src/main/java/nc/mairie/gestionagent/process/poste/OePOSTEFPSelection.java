package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.StatutFPDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFPSelection Date de création : (22/07/11 16:01:21)
 * 
 */
public class OePOSTEFPSelection extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private ArrayList<FichePoste> listeFichePoste;

	private String focus = null;
	private boolean advancedSearch = false;
	private boolean rechercheAffectation = false;
	private Service service = null;
	private Agent agentCourant = null;

	private TitrePosteDao titrePosteDao;
	private StatutFPDao statutFPDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();

		FichePoste fp = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
		if (fp != null) {
			addZone(getNOM_EF_NUM_FICHE_POSTE(), fp.getNumFp());
		}

		Boolean as = (Boolean) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE);

		Boolean aff = (Boolean) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_AFFECTATION);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_AFFECTATION);

		Boolean affSecondaire = (Boolean) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_RECHERCHE_FP_SECONDAIRE_AFFECTATION);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_RECHERCHE_FP_SECONDAIRE_AFFECTATION);

		if (as != null) {

			setAdvancedSearch(as);

			if (isAdvancedSearch()) {
				setService((Service) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_SERVICE));
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_SERVICE);

				if (getService() != null)
					addZone(getNOM_ST_SERVICE(), getService().getLibService());
				addZone(getNOM_RG_TYPE_RECHERCHE(), getNOM_RB_TYPE_NUMERO());
			}
		} else if (aff != null) {
			setRechercheAffectation(aff);
		} else if (affSecondaire != null) {
			setRechercheAffectation(affSecondaire);
		}
		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null) {
			setAgentCourant(agt);
			addZone(getNOM_ST_AGENT(), getAgentCourant().getNomatr().toString());
			addZone(getNOM_RG_TYPE_RECHERCHE(), getNOM_RB_TYPE_AGENT());
			performPB_RECHERCHER(request);
			// rechercheParAgent(request);
		}

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
	}

	/**
	 * Constructeur du process OePOSTEFPSelection. Date de création : (22/07/11
	 * 16:01:21)
	 * 
	 */
	public OePOSTEFPSelection() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (22/07/11 16:01:21)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getVAL_ST_AGENT(), Const.CHAINE_VIDE);
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (22/07/11 16:01:21)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Rempli la liste des fiches de poste trouvées
	 */
	private boolean fillList(ArrayList<FichePoste> aListe) throws Exception {
		// Affectation de la liste
		setListeFichePoste(aListe);

		int indiceFp = 0;
		if (getListeFichePoste() != null) {
			for (int i = 0; i < getListeFichePoste().size(); i++) {
				FichePoste fp = (FichePoste) getListeFichePoste().get(i);
				String titreFichePoste = fp.getIdTitrePoste() == null ? "&nbsp;" : getTitrePosteDao()
						.chercherTitrePoste(fp.getIdTitrePoste()).getLibTitrePoste();
				Agent agent = null;
				try {
					agent = getAgentDao().chercherAgentAffecteFichePoste(fp.getIdFichePoste());
				} catch (Exception e) {
					try {
						agent = getAgentDao().chercherAgentAffecteFichePosteSecondaire(fp.getIdFichePoste());
					} catch (Exception e2) {

					}
				}

				addZone(getNOM_ST_NUM(indiceFp), fp.getNumFp());
				addZone(getNOM_ST_TITRE(indiceFp), titreFichePoste);
				addZone(getNOM_ST_AGENT(indiceFp), agent == null ? "&nbsp;" : agent.getNomAgent().toUpperCase() + " "
						+ agent.getPrenomAgent());

				indiceFp++;
			}
		}

		// Si liste vide alors erreur
		if (aListe.size() == 0) {

			if (isRechercheAffectation() && getVAL_EF_NUM_FICHE_POSTE().length() != 0) {
				try {
					FichePoste fp = getFichePosteDao().chercherFichePosteAvecNumeroFP(getVAL_EF_NUM_FICHE_POSTE());
					if (!getStatutFPDao().chercherStatutFP(fp.getIdStatutFp()).getLibStatutFp()
							.equals(EnumStatutFichePoste.VALIDEE.getLibLong())
							|| !getStatutFPDao().chercherStatutFP(fp.getIdStatutFp()).getLibStatutFp()
									.equals(EnumStatutFichePoste.GELEE.getLibLong())) {
						// "ERR087",
						// "Cette fiche de poste n'est pas 'Validée'  ou 'Gelee'. Elle ne peut pas être affectée a un agent."
						setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR087"));
						return false;
					}
				} catch (Exception e) {
					// "ERR125", "Impossible de trouver @."
					setStatut(STATUT_MEME_PROCESS, false,
							MessageUtils.getMessage("ERR125", "la FDP " + getVAL_EF_NUM_FICHE_POSTE()));
					return false;
				}
				aListe = getFichePosteDao().listerFichePosteAvecNumPartiel(getVAL_EF_NUM_FICHE_POSTE());
				if (aListe.size() != 0) {
					setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR084"));
					return false;
				}
			}

			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "resultat"));
			return false;
		}
		return true;
	}

	/**
	 * Effectue la recherche de fiche de poste par numero de fiche de poste
	 * 
	 * @param request
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	private boolean rechercheParNumero(HttpServletRequest request) throws Exception {
		ArrayList<FichePoste> aListe = new ArrayList<FichePoste>();

		if (isRechercheAffectation()) {
			// Si la zone est vide alors on prend toutes les FP non affectées
			if (getVAL_EF_NUM_FICHE_POSTE().length() == 0) {
				aListe = getFichePosteDao().listerFichePosteValideesouGeleeNonAffectees();
			} else {
				aListe = getFichePosteDao().listerFichePosteValideesOuGeleesNonAffecteesAvecNumPartiel(
						getVAL_EF_NUM_FICHE_POSTE());
			}
		} else {
			// Si la zone est vide alors on prend toutes les FP
			if (getVAL_EF_NUM_FICHE_POSTE().length() == 0) {
				aListe = (ArrayList<FichePoste>) getFichePosteDao().listerFichePoste();
			} else {
				// on regarde si 1 seule FDP correspondante sinon on affiche la
				// liste avec numPartiel
				try {
					FichePoste fp = getFichePosteDao().chercherFichePosteAvecNumeroFP(getVAL_EF_NUM_FICHE_POSTE());
					if (fp == null || fp.getIdFichePoste() == null) {
						aListe = getFichePosteDao().listerFichePosteAvecNumPartiel(getVAL_EF_NUM_FICHE_POSTE());
					} else {
						aListe.add(fp);
					}
				} catch (Exception e) {
					aListe = getFichePosteDao().listerFichePosteAvecNumPartiel(getVAL_EF_NUM_FICHE_POSTE());
				}
			}
		}

		return fillList(aListe);
	}

	/**
	 * Effectu la recherche par agent
	 * 
	 * @param request
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	private boolean rechercheParAgent(HttpServletRequest request) throws Exception {
		ArrayList<FichePoste> aListe = new ArrayList<FichePoste>();

		// Si la zone est vide ?
		if (getVAL_ST_AGENT().length() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR004"));
			return false;
		}

		if (!getVAL_ST_AGENT().equals(Const.CHAINE_VIDE)) {
			Agent a = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
			// Recherche de tous les liens Agent / FichePoste
			ArrayList<Affectation> liens = getAffectationDao().listerAffectationActiveAvecAgent(a.getIdAgent());
			aListe = getFichePosteDao().listerFichePosteAvecAgent(liens);
		}

		return fillList(aListe);
	}

	/**
	 * Effectue la recherche par service
	 * 
	 * @param request
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	private boolean rechercheParService(HttpServletRequest request) throws Exception {
		ArrayList<FichePoste> aListe = new ArrayList<FichePoste>();

		// Si la zone est vide alors on prend tout
		if (getVAL_ST_SERVICE().length() != 0) {
			aListe = getFichePosteDao().listerFichePosteAvecService(getService().getCodService());
		} else {
			getTransaction().declarerErreur("ERR113");
			return false;
		}

		return fillList(aListe);
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		if (estRechercheAvancee()) {
			if (getZone(getNOM_RG_TYPE_RECHERCHE()).equals(getNOM_RB_TYPE_AGENT())) {
				rechercheParAgent(request);
			} else if (getZone(getNOM_RG_TYPE_RECHERCHE()).equals(getNOM_RB_TYPE_SERVICE())) {
				rechercheParService(request);
			} else {
				rechercheParNumero(request);
			}
		} else {
			rechercheParNumero(request);
		}
		if (getListeFichePoste().size() == 1) {
			// Alimentation de la variable fichePoste
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_POSTE, getListeFichePoste().get(0));
			addZone(getVAL_ST_AGENT(), Const.CHAINE_VIDE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			setStatut(STATUT_PROCESS_APPELANT);

		}
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_FICHE_POSTE
	 * Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public String getNOM_EF_NUM_FICHE_POSTE() {
		return "NOM_EF_NUM_FICHE_POSTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_FICHE_POSTE Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public String getVAL_EF_NUM_FICHE_POSTE() {
		return getZone(getNOM_EF_NUM_FICHE_POSTE());
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
		return getNOM_EF_NUM_FICHE_POSTE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne la liste des fiches de poste.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<FichePoste> getListeFichePoste() {
		if (listeFichePoste == null)
			listeFichePoste = new ArrayList<FichePoste>();
		return listeFichePoste;
	}

	/**
	 * Met a jour la liste des fiches de poste.
	 * 
	 * @param fichePoste
	 *            la liste des fiches de poste
	 */
	private void setListeFichePoste(ArrayList<FichePoste> newListeFichePoste) {
		this.listeFichePoste = newListeFichePoste;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TYPE_RECHERCHE Date de création : (02/08/11 08:51:46)
	 * 
	 */
	public String getNOM_RG_TYPE_RECHERCHE() {
		return "NOM_RG_TYPE_RECHERCHE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TYPE_RECHERCHE Date de création : (02/08/11 08:51:46)
	 * 
	 */
	public String getVAL_RG_TYPE_RECHERCHE() {
		return getZone(getNOM_RG_TYPE_RECHERCHE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_AGENT Date de
	 * création : (02/08/11 08:51:46)
	 * 
	 */
	public String getNOM_RB_TYPE_AGENT() {
		return "NOM_RB_TYPE_AGENT";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_NUMERO Date de
	 * création : (02/08/11 08:51:46)
	 * 
	 */
	public String getNOM_RB_TYPE_NUMERO() {
		return "NOM_RB_TYPE_NUMERO";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_SERVICE Date de
	 * création : (02/08/11 08:51:46)
	 * 
	 */
	public String getNOM_RB_TYPE_SERVICE() {
		return "NOM_RB_TYPE_SERVICE";
	}

	public boolean estRechercheAvancee() {
		return advancedSearch;
	}

	public Service getService() {
		return service;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
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
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			for (int i = 0; i < getListeFichePoste().size(); i++) {
				if (testerParametre(request, getNOM_PB_VALIDER(i))) {
					return performPB_VALIDER(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (02/08/11 09:43:30)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFPSelection.jsp";
	}

	/**
	 * Met a jour le service
	 * 
	 * @param service
	 *            Nouveau service
	 */
	private void setService(Service service) {
		this.service = service;
	}

	/**
	 * Getter du booleen de recherche de FP pour affectation.
	 * 
	 * @return rechercheAffectation
	 */
	private boolean isRechercheAffectation() {
		return rechercheAffectation;
	}

	/**
	 * Setter du booleen de recherche de FP pour affectation.
	 * 
	 * @param newRechercheAffectation
	 */
	private void setRechercheAffectation(boolean newRechercheAffectation) {
		this.rechercheAffectation = newRechercheAffectation;
	}

	/**
	 * Getter du booleen de rechercher avancee de FP.
	 * 
	 * @return advancedSearch
	 */
	private boolean isAdvancedSearch() {
		return advancedSearch;
	}

	/**
	 * Setter du booleen de recherche avancee de FP.
	 * 
	 * @param advancedSearch
	 */
	private void setAdvancedSearch(boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	/**
	 * Getter de l'agent courant.
	 * 
	 * @return agentCourant
	 */
	private Agent getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Setter de l'agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
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
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (22/07/11 16:01:21)
	 * 
	 */
	public String getNOM_PB_VALIDER(int i) {
		return "NOM_PB_VALIDER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (22/07/11 16:01:21)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request, int elemSelection) throws Exception {

		// Récup de la fiche de poste sélectionnée
		FichePoste fp = (FichePoste) getListeFichePoste().get(elemSelection);

		// Alimentation de la variable fichePoste
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_POSTE, fp);
		addZone(getVAL_ST_AGENT(), Const.CHAINE_VIDE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
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
}
