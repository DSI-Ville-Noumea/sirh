package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementContractuelsDao;
import nc.mairie.spring.dao.metier.carriere.HistoCarriereDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.IAdsService;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTMasseSalarialeContractuel extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_ANNEE;

	private String[] listeAnnee;

	private ArrayList<AvancementContractuels> listeAvct;
	public String agentEnErreur = Const.CHAINE_VIDE;

	public String ACTION_CALCUL = "Calcul";
	public static final int STATUT_RECHERCHER_AGENT = 1;

	private AvancementContractuelsDao avancementContractuelsDao;
	private TitrePosteDao titrePosteDao;
	private FichePosteDao fichePosteDao;
	private HistoCarriereDao histoCarriereDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	
	private IAdsService adsService;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
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
		initialiseListeDeroulante();

		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null) {
			addZone(getNOM_ST_AGENT(), agt.getNomatr().toString());
			performPB_LANCER(request);
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAvancementContractuelsDao() == null) {
			setAvancementContractuelsDao(new AvancementContractuelsDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoCarriereDao() == null) {
			setHistoCarriereDao(new HistoCarriereDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if(null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
	}

	private void initialiseListeDeroulante() throws Exception {

		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) ServletAgent.getMesParametres().get("ANNEE_MASSE_SALARIALE");
			setListeAnnee(new String[1]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
	}
	
	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null !=serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {
			// Si clic sur le bouton PB_LANCER
			if (testerParametre(request, getNOM_PB_LANCER())) {
				return performPB_LANCER(request);
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

			// Si clic sur le bouton PB_CHANGER_ANNEE
			if (testerParametre(request, getNOM_PB_CHANGER_ANNEE())) {
				return performPB_CHANGER_ANNEE(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AFFECTER
			if (testerParametre(request, getNOM_PB_AFFECTER())) {
				return performPB_AFFECTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTMasseSalarialeContractuel() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTMasseSalarialeContractuel.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SIMU-MASSE-CONTR";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
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
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_LANCER Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_PB_LANCER() {
		return "NOM_PB_LANCER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public boolean performPB_LANCER(HttpServletRequest request) throws Exception {

		// Mise à jour de l'action menee
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		String an = getListeAnnee()[0];

		// Suppression des avancements a l'etat 'Travail' de l'année
		getAvancementContractuelsDao().supprimerAvancementContractuelsTravailAvecAnnee(Integer.valueOf(an));

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		if (!performCalculContractuel(getVAL_ST_ID_SERVICE_ADS(), an, agent))
			return false;

		commitTransaction();
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT, an);

		// "INF200","Simulation effectuee"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF200"));

		return true;
	}

	/**
	 * Getter de la liste des années possibles de simulation.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des années possibles de simulation.
	 * 
	 * @param listeAnnee
	 *            listeAnnee à définir
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	private boolean performCalculContractuel(String idServiceAds, String annee, Agent agent) throws Exception {
		ArrayList<Agent> la = new ArrayList<Agent>();
		if (agent != null) {
			// il faut regarder si cet agent est de type Convention Collective
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carr == null || carr.getCodeCategorie() == null || !carr.getCodeCategorie().equals("4")) {
				// "ERR181",
				// "Cet agent n'est pas de type @. Il ne peut pas être soumis a l'avancement @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR181", "contractuel", "des contractuels"));
				return false;
			}
			la.add(agent);
		} else {
			List<Integer> listeSousService = null;
			if (!idServiceAds.equals(Const.CHAINE_VIDE)) {
				listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(idServiceAds));
			}

			// Récupération des agents
			ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(getTransaction(), annee,
					"Contractuel");
			String listeNomatrAgent = Const.CHAINE_VIDE;
			for (Carriere carr : listeCarriereActive) {
				listeNomatrAgent += carr.getNoMatricule() + ",";
			}
			if (!listeNomatrAgent.equals(Const.CHAINE_VIDE)) {
				listeNomatrAgent = listeNomatrAgent.substring(0, listeNomatrAgent.length() - 1);
			}
			la = getAgentDao().listerAgentEligibleAvct(listeSousService, listeNomatrAgent);
		}

		// Parcours des agents
		for (Agent a : la) {
			// Recuperation de la carriere en cours
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), a);
			if (getTransaction().isErreur() || carr == null || carr.getDateDebut() == null) {
				getTransaction().traiterErreur();
				continue;
			}

			PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(getTransaction(),
					a.getNomatr(),
					Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
			if (getTransaction().isErreur() || paAgent == null || paAgent.getCdpadm() == null
					|| paAgent.estPAInactive(getTransaction())) {
				getTransaction().traiterErreur();
				continue;
			}
			// Récupération du CDCADR de SPGRADN
			// Grade grade = Grade.chercherGrade(getTransaction(),
			// carr.getCodeGrade());
			// L'agent doit avoir la date début de la nouvelle carriere comprise
			// dans l'année d'avancement
			if (Services.compareDates(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2), "01/01/"
					+ annee) >= 0
					&& Services.compareDates(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2),
							"31/12/" + annee) <= 0) {
				// Récupération de l'avancement
				try {
					@SuppressWarnings("unused")
					AvancementContractuels avct = getAvancementContractuelsDao()
							.chercherAvancementContractuelsAvecAnneeEtAgent(Integer.valueOf(annee), a.getIdAgent());
				} catch (Exception e) {
					// Création de l'avancement
					AvancementContractuels avct = new AvancementContractuels();
					avct.setIdAgent(a.getIdAgent());
					avct.setDateEmbauche(a.getDateDerniereEmbauche());
					avct.setAnnee(Integer.valueOf(annee));
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

					PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), paAgent.getCdpadm());
					avct.setPa(pa.getLiPAdm());

					// on recupere le grade du poste
					Affectation aff = null;
					try {
						aff = getAffectationDao().chercherAffectationActiveAvecAgent(a.getIdAgent());
					} catch (Exception e2) {
						continue;
					}
					if (aff == null || aff.getIdFichePoste() == null) {
						continue;
					}
					FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());

					avct.setNumFp(fp.getNumFp());
					// on cherche a quelle categorie appartient l'agent
					// (A,B,A+..;)
					Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
					GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(),
							g.getCodeGradeGenerique());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
					// on recupere les points pour cette categorie (A,B,A+..)
					if (gg.getCodCadre() == null || gg.getCodCadre().equals(Const.CHAINE_VIDE)) {
						continue;
					}

					Bareme bareme = Bareme.chercherBareme(getTransaction(), carr.getIban());
					avct.setCdcadr(gg.getCodCadre());
					// on calcul le nouvel INM
					String nouvINM = String.valueOf(Integer.valueOf(bareme.getInm())
							+ Integer.valueOf(gg.getNbPointsAvct()));
					// avec ce nouvel INM on recupere l'iban et l'ina
					// correspondant
					Bareme nouvBareme = (Bareme) Bareme.listerBaremeByINM(getTransaction(), nouvINM).get(0);

					// on rempli les champs
					avct.setNouvIban(nouvBareme.getIban());
					avct.setNouvInm(Integer.valueOf(nouvBareme.getInm()));
					avct.setNouvIna(Integer.valueOf(nouvBareme.getIna()));
					avct.setDateProchainGrade(sdf.parse(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()),
							2)));

					avct.setDateArrete(sdf.parse("01/01/" + annee));
					avct.setNumArrete(annee);

					EntiteDto direction = adsService.getAffichageDirection(fp.getIdServiceAds());
					EntiteDto section = adsService.getAffichageSection(fp.getIdServiceAds());

					avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigle());
					avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigle());
					avct.setDateGrade(sdf.parse(carr.getDateDebut()));
					avct.setIban(carr.getIban());
					avct.setInm(Integer.valueOf(bareme.getInm()));
					avct.setIna(Integer.valueOf(bareme.getIna()));

					// on regarde si l'agent a une carriere de simulation deja
					// saisie
					// autrement dis si la carriere actuelle a pour datfin 0
					if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
						avct.setCarriereSimu(null);
					} else {
						avct.setCarriereSimu("S");
					}
					getAvancementContractuelsDao().creerAvancementContractuels(avct.getIdAgent(),
							avct.getDateEmbauche(), avct.getNumFp(), avct.getPa(), avct.getDateGrade(),
							avct.getDateProchainGrade(), avct.getIban(), avct.getInm(), avct.getIna(),
							avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getEtat(),
							avct.getDateArrete(), avct.getNumArrete(), avct.getCarriereSimu(), avct.getAnnee(),
							avct.getDirectionService(), avct.getSectionService(), avct.getCdcadr());
				}
			}
		}
		return true;
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<AvancementContractuels> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementContractuels>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementContractuels> listeAvct) {
		this.listeAvct = listeAvct;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_CHANGER_ANNEE() {
		return "NOM_PB_CHANGER_ANNEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CHANGER_ANNEE(HttpServletRequest request) throws Exception {
		agentEnErreur = Const.CHAINE_VIDE;
		String annee = getListeAnnee()[0];
		setListeAvct(getAvancementContractuelsDao().listerAvancementContractuelsAnnee(Integer.valueOf(annee)));

		afficherListeAvct(request);

		return true;
	}

	private void afficherListeAvct(HttpServletRequest request) throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementContractuels av = (AvancementContractuels) getListeAvct().get(j);
			Integer i = av.getIdAvct();
			Agent agent = getAgentDao().chercherAgent(av.getIdAgent());
			FichePoste fp = getFichePosteDao().chercherFichePosteAvecNumeroFP(av.getNumFp());
			TitrePoste tp = null;
			if (fp != null && fp.getIdTitrePoste() != null) {
				try {
					tp = getTitrePosteDao().chercherTitrePoste(fp.getIdTitrePoste());
				} catch (Exception e) {

				}
			}

			addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DATE_EMBAUCHE(i),
					av.getDateEmbauche() == null ? Const.CHAINE_VIDE : sdf.format(av.getDateEmbauche()));
			addZone(getNOM_ST_FP(i), av.getNumFp() + " <br> " + (tp == null ? "&nbsp;" : tp.getLibTitrePoste()));
			addZone(getNOM_ST_PA(i), av.getPa());
			addZone(getNOM_ST_CATEGORIE(i), av.getCdcadr());
			addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct().toString());
			addZone(getNOM_ST_DATE_DEBUT(i),
					sdf.format(av.getDateGrade()) + " <br> " + sdf.format(av.getDateProchainGrade()));
			addZone(getNOM_ST_IBA(i), av.getIban() + " <br> " + av.getNouvIban());
			addZone(getNOM_ST_INM(i), av.getInm() + " <br> " + av.getNouvInm());
			addZone(getNOM_ST_INA(i), av.getIna() + " <br> " + av.getNouvIna());

			addZone(getNOM_CK_VALID_DRH(i),
					av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_ST_MOTIF_AVCT(i), "REVALORISATION");
			addZone(getNOM_CK_PROJET_ARRETE(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())
					|| av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_EF_DATE_ARRETE(i),
					av.getDateArrete() == null ? Const.CHAINE_VIDE : sdf.format(av.getDateArrete()));
			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue())
					|| av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_EMBAUCHE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_EMBAUCHE(int i) {
		return "NOM_ST_DATE_EMBAUCHE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_EMBAUCHE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_EMBAUCHE(int i) {
		return getZone(getNOM_ST_DATE_EMBAUCHE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CATEGORIE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CATEGORIE(int i) {
		return "NOM_ST_CATEGORIE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CATEGORIE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CATEGORIE(int i) {
		return getZone(getNOM_ST_CATEGORIE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_FP(int i) {
		return "NOM_ST_FP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_FP(int i) {
		return getZone(getNOM_ST_FP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PA Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_PA(int i) {
		return "NOM_ST_PA_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PA Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_PA(int i) {
		return getZone(getNOM_ST_PA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT_IBA Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT_IBA
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE(int i) {
		return "NOM_EF_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE(int i) {
		return getZone(getNOM_EF_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_NUM_ARRETE(int i) {
		return "NOM_EF_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_NUM_ARRETE(int i) {
		return getZone(getNOM_EF_NUM_ARRETE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_AVCT(int i) {
		return "NOM_ST_NUM_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_AVCT(int i) {
		return getZone(getNOM_ST_NUM_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_AFFECTER(int i) {
		return "NOM_CK_AFFECTER_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_PROJET_ARRETE(int i) {
		return "NOM_CK_PROJET_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_PROJET_ARRETE(int i) {
		return getZone(getNOM_CK_PROJET_ARRETE(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_DRH(int i) {
		return "NOM_CK_VALID_DRH_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_DRH(int i) {
		return getZone(getNOM_CK_VALID_DRH(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_IBA Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_IBA(int i) {
		return "NOM_ST_IBA_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_IBA Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_IBA(int i) {
		return getZone(getNOM_ST_IBA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INM Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_INM(int i) {
		return "NOM_ST_INM_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INM Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_INM(int i) {
		return getZone(getNOM_ST_INM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INA Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_INA(int i) {
		return "NOM_ST_INA_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INA Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_INA(int i) {
		return getZone(getNOM_ST_INA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF_AVCT(int i) {
		return "NOM_ST_MOTIF_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF_AVCT(int i) {
		return getZone(getNOM_ST_MOTIF_AVCT(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// on sauvegarde l'etat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementContractuels avct = (AvancementContractuels) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else if (getVAL_CK_PROJET_ARRETE(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				} else if (getVAL_CK_VALID_DRH(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
				}
				// on traite le numero et la date d'arrete
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdf
						.parse(getVAL_EF_DATE_ARRETE(i)));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
			}
			getAvancementContractuelsDao().modifierAvancementContractuels(avct.getIdAvct(), avct.getIdAgent(),
					avct.getDateEmbauche(), avct.getNumFp(), avct.getPa(), avct.getDateGrade(),
					avct.getDateProchainGrade(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(),
					avct.getNouvInm(), avct.getNouvIna(), avct.getEtat(), avct.getDateArrete(), avct.getNumArrete(),
					avct.getCarriereSimu(), avct.getAnnee(), avct.getDirectionService(), avct.getSectionService(),
					avct.getCdcadr());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupere la ligne concernée
			AvancementContractuels avct = (AvancementContractuels) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concerné
					Agent agentCarr = getAgentDao().chercherAgent(avct.getIdAgent());
					// on recupere la derniere carriere dans l'année
					Carriere carr = Carriere.chercherDerniereCarriereAvecAgentEtAnnee(getTransaction(),
							agentCarr.getNomatr(), avct.getAnnee().toString());
					// si la carriere est bien la derniere de la liste
					if (carr.getDateFin() == null || carr.getDateFin().equals("0")) {
						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());
						// on traite le numero et la date d'arrete
						avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdf
								.parse(getVAL_EF_DATE_ARRETE(i)));
						avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
						getAvancementContractuelsDao()
								.modifierAvancementContractuels(avct.getIdAvct(), avct.getIdAgent(),
										avct.getDateEmbauche(), avct.getNumFp(), avct.getPa(), avct.getDateGrade(),
										avct.getDateProchainGrade(), avct.getIban(), avct.getInm(), avct.getIna(),
										avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getEtat(),
										avct.getDateArrete(), avct.getNumArrete(), avct.getCarriereSimu(),
										avct.getAnnee(), avct.getDirectionService(), avct.getSectionService(),
										avct.getCdcadr());

						// on ferme cette carriere
						carr.setDateFin(sdf.format(avct.getDateProchainGrade()));
						// RG_AG_CA_A03
						HistoCarriere histo = new HistoCarriere(carr);
						getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
						carr.modifierCarriere(getTransaction(), agentCarr, user);

						// on crée un nouvelle carriere
						Carriere nouvelleCarriere = new Carriere();
						nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
						nouvelleCarriere.setReferenceArrete(avct.getNumArrete().equals(Const.CHAINE_VIDE) ? Const.ZERO
								: avct.getNumArrete());
						nouvelleCarriere.setDateArrete(avct.getDateArrete() == null ? Const.ZERO : sdf.format(avct
								.getDateArrete()));
						nouvelleCarriere.setDateDebut(sdf.format(avct.getDateProchainGrade()));
						nouvelleCarriere.setDateFin(Const.ZERO);
						nouvelleCarriere.setIban(avct.getNouvIban());
						// champ a remplir pour creer une carriere NB : on
						// reprend ceux de la carriere precedente
						nouvelleCarriere.setCodeBase(Const.CHAINE_VIDE);
						nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
						nouvelleCarriere.setCodeGrade(carr.getCodeGrade());
						nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
						nouvelleCarriere.setIdMotif(Const.ZERO);
						nouvelleCarriere.setModeReglement(carr.getModeReglement());
						nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

						// RG_AG_CA_A03
						nouvelleCarriere.setNoMatricule(agentCarr.getNomatr().toString());
						HistoCarriere histo2 = new HistoCarriere(nouvelleCarriere);
						getHistoCarriereDao().creerHistoCarriere(histo2, user, EnumTypeHisto.CREATION);
						nouvelleCarriere.creerCarriere(getTransaction(), agentCarr, user);

						if (getTransaction().isErreur()) {
							return false;
						} else {
							nbAgentAffectes += 1;
						}
					} else {
						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste a l'ecran
						agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " ("
								+ agentCarr.getNomatr() + "); ";
					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();

		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setListeAvct(new ArrayList<AvancementContractuels>());
		afficherListeAvct(request);
		return true;
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public AvancementContractuelsDao getAvancementContractuelsDao() {
		return avancementContractuelsDao;
	}

	public void setAvancementContractuelsDao(AvancementContractuelsDao avancementContractuelsDao) {
		this.avancementContractuelsDao = avancementContractuelsDao;
	}

	public TitrePosteDao getTitrePosteDao() {
		return titrePosteDao;
	}

	public void setTitrePosteDao(TitrePosteDao titrePosteDao) {
		this.titrePosteDao = titrePosteDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public HistoCarriereDao getHistoCarriereDao() {
		return histoCarriereDao;
	}

	public void setHistoCarriereDao(HistoCarriereDao histoCarriereDao) {
		this.histoCarriereDao = histoCarriereDao;
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