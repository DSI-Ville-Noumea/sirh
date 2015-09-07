package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
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
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementContractuelsDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.IAdsService;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTSimulation Date de création : (21/11/11 11:11:24)
 * 
 */
public class OeAVCTSimulationContractuels extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_ANNEE;

	private String[] listeAnnee;

	public String focus = null;
	public String ACTION_CALCUL = "Calcul";

	private AvancementContractuelsDao avancementContractuelsDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	
	private IAdsService adsService;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 11:11:24)
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
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
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

	/**
	 * Initialise les listes deroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {

		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) ServletAgent.getMesParametres().get("ANNEE_AVCT");
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
	 * du bouton de la JSP Date de création : (21/11/11 11:11:24)
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
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTSimulation. Date de création : (21/11/11
	 * 11:11:24)
	 * 
	 */
	public OeAVCTSimulationContractuels() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTSimulationContractuels.jsp";
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
	 * méthode de calcul des avancements Contractuels.
	 * 
	 * @param codeService
	 * @param annee
	 * @param agent
	 * @throws Exception
	 */
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
					getTransaction().traiterErreur();
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
					Bareme bareme = Bareme.chercherBareme(getTransaction(), carr.getIban());
					// on recupere les points pour cette categorie (A,B,A+..)
					if (gg.getCodCadre() == null || gg.getCodCadre().equals(Const.CHAINE_VIDE)) {
						continue;
					}
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
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_EF_SERVICE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-SIMULATION-CONTRA";
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

	public AvancementContractuelsDao getAvancementContractuelsDao() {
		return avancementContractuelsDao;
	}

	public void setAvancementContractuelsDao(AvancementContractuelsDao avancementContractuelsDao) {
		this.avancementContractuelsDao = avancementContractuelsDao;
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
