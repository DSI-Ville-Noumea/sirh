package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.carriere.HistoCarriereDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTMasseSalarialeFonctionnaire extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_ANNEE;
	private String[] LB_AVIS_CAP;

	private Hashtable<Integer, MotifAvancement> hashMotifAvct;
	private Hashtable<Integer, AvisCap> hashAvisCAP;
	private ArrayList<AvisCap> listeAvisCAP;
	private ArrayList<MotifAvancement> listeMotifAvct;

	private String[] listeAnnee;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private ArrayList<AvancementFonctionnaires> listeAvct;
	public String agentEnErreur = Const.CHAINE_VIDE;

	public String ACTION_CALCUL = "Calcul";

	public static final int STATUT_RECHERCHER_AGENT = 1;
	public String agentEnErreurHautGrille = Const.CHAINE_VIDE;

	private MotifAvancementDao motifAvancementDao;
	private AutreAdministrationDao autreAdministrationDao;
	private AvisCapDao avisCapDao;
	private AutreAdministrationAgentDao autreAdministrationAgentDao;
	private AvancementFonctionnairesDao avancementFonctionnairesDao;
	private FichePosteDao fichePosteDao;
	private HistoCarriereDao histoCarriereDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	private SimpleDateFormat sdfFormatDate = new SimpleDateFormat("dd/MM/yyyy");

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
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
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

		if (getMotifAvancementDao() == null) {
			setMotifAvancementDao(new MotifAvancementDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationDao() == null) {
			setAutreAdministrationDao(new AutreAdministrationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvisCapDao() == null) {
			setAvisCapDao(new AvisCapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationAgentDao() == null) {
			setAutreAdministrationAgentDao(new AutreAdministrationAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementFonctionnairesDao() == null) {
			setAvancementFonctionnairesDao(new AvancementFonctionnairesDao((SirhDao) context.getBean("sirhDao")));
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
		// Si liste motifs avancement vide alors affectation
		if (getListeMotifAvct() == null || getListeMotifAvct().size() == 0) {
			ArrayList<MotifAvancement> motif = getMotifAvancementDao().listerMotifAvancementSansRevalo();
			setListeMotifAvct(motif);

			// remplissage de la hashTable
			for (int i = 0; i < getListeMotifAvct().size(); i++) {
				MotifAvancement m = (MotifAvancement) getListeMotifAvct().get(i);
				getHashMotifAvancement().put(m.getIdMotifAvct(), m);
			}
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAP() == null || getListeAvisCAP().size() == 0) {
			ArrayList<AvisCap> avis = (ArrayList<AvisCap>) getAvisCapDao().listerAvisCap();
			setListeAvisCAP(avis);

			int[] tailles = { 7 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<AvisCap> list = getListeAvisCAP().listIterator(); list.hasNext();) {
				AvisCap fili = (AvisCap) list.next();
				String ligne[] = { fili.getLibLongAvisCap() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_AVIS_CAP(aFormat.getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAP().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAP().get(i);
				getHashAvisCAP().put(ac.getIdAvisCap(), ac);
			}
		}

	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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
	public OeAVCTMasseSalarialeFonctionnaire() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTMasseSalarialeFonctionnaire.jsp";
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SIMU-MASSE-FONCT";
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
	 * Retourne le nom d'un bouton pour la JSP : PB_LANCER Date de création :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_PB_LANCER() {
		return "NOM_PB_LANCER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 11:11:24)
	 * 
	 */
	public boolean performPB_LANCER(HttpServletRequest request) throws Exception {

		// Mise à jour de l'action menée
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		String an = getListeAnnee()[0];

		// Suppression des avancements à l'état 'Travail' de la catégorie donnée
		// et de l'année
		getAvancementFonctionnairesDao().supprimerAvancementTravailAvecCategorie(Integer.valueOf(an));

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		if (!performCalculFonctionnaire(getVAL_ST_CODE_SERVICE(), an, agent))
			return false;

		commitTransaction();
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT, an);

		// "INF200","Simulation effectuée"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF200"));

		return true;
	}

	/**
	 * Méthode de calcul des avancements Fonctionnaires.
	 * 
	 * @param codeService
	 * @param annee
	 * @param agent
	 * @throws Exception
	 */
	private boolean performCalculFonctionnaire(String codeService, String annee, Agent agent) throws Exception {
		ArrayList<Agent> la = new ArrayList<Agent>();
		if (agent != null) {
			// il faut regarder si cet agent est de type Convention Collective
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carr == null
					|| carr.getCodeCategorie() == null
					|| (!carr.getCodeCategorie().equals("1") && !carr.getCodeCategorie().equals("2")
							&& !carr.getCodeCategorie().equals("18") && !carr.getCodeCategorie().equals("20"))) {
				// "ERR181",
				// "Cet agent n'est pas de type @. Il ne peut pas être soumis à l'avancement @."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR181", "fonctionnaire", "des fonctionnaires"));
				return false;
			}
			la.add(agent);
		} else {
			// Récupération des agents
			// on recupere les sous-service du service selectionne

			ArrayList<String> listeSousService = null;
			if (!codeService.equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(), codeService);
				listeSousService = Service.listSousService(getTransaction(), serv.getSigleService());
			}

			// Récupération des agents
			ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(getTransaction(), annee,
					"Fonctionnaire");
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
		for (int i = 0; i < la.size(); i++) {
			Agent a = la.get(i);

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

			// Récupération de l'avancement
			try {
				@SuppressWarnings("unused")
				AvancementFonctionnaires avct = getAvancementFonctionnairesDao()
						.chercherAvancementFonctionnaireAvecAnneeEtAgent(Integer.valueOf(annee), a.getIdAgent());
			} catch (Exception e) {
				// on regarde si il y a d'autre carrieres avec le meme grade
				// si oui on prend la carriere plus lointaine
				ArrayList<Carriere> listeCarrMemeGrade = Carriere.listerCarriereAvecGradeEtStatut(getTransaction(),
						a.getNomatr(), carr.getCodeGrade(), carr.getCodeCategorie());
				if (listeCarrMemeGrade != null && listeCarrMemeGrade.size() > 0) {
					carr = (Carriere) listeCarrMemeGrade.get(0);
				}
				Grade gradeActuel = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				}
				// Si pas de grade suivant, agent non éligible
				if (gradeActuel.getCodeGradeSuivant() != null && gradeActuel.getCodeGradeSuivant().length() != 0) {
					// Création de l'avancement
					AvancementFonctionnaires avct = new AvancementFonctionnaires();
					avct.setIdAgent(a.getIdAgent());
					avct.setCodeCategorie(Integer.valueOf(carr.getCodeCategorie()));
					avct.setAnnee(Integer.valueOf(annee));
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

					// PA
					avct.setCodePa(paAgent.getCdpadm());

					// on traite si l'agent est detaché ou non
					if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56")
							|| paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
						avct.setAgentVdn(false);
					} else {
						avct.setAgentVdn(true);
					}
					// SI stagiaire sur un grade à durée moyenne différent de 12
					// mois
					if ((carr.getCodeCategorie().equals("2") || carr.getCodeCategorie().equals("18"))
							&& (!gradeActuel.getDureeMoy().equals("12"))) {

						avct.setNouvBmAnnee(Integer.valueOf(carr.getBMAnnee()));
						avct.setNouvBmMois(Integer.valueOf(carr.getBMMois()));
						avct.setNouvBmJour(Integer.valueOf(carr.getBMJour()));
						Integer nouvACCStage = Integer.valueOf(carr.getACCAnnee()) + 1;
						avct.setNouvAccAnnee(nouvACCStage);
						avct.setNouvAccMois(Integer.valueOf(carr.getACCMois()));
						avct.setNouvAccJour(Integer.valueOf(carr.getACCJour()));

						// par defaut avis CAP = "MOYENNE"
						AvisCap avisCap = getAvisCapDao().chercherAvisCapByLibCourt(Const.AVIS_CAP_MOY);
						avct.setIdAvisCap(avisCap.getIdAvisCap());

						avct.setPeriodeStandard(12);

						avct.setDateAvctMoy(sdfFormatDate.parse(Services.ajouteAnnee(carr.getDateDebut(), 1)));
						avct.setDateAvctMaxi(null);
						avct.setDateAvctMini(null);

						// si la date avct moy (année ) sup à l'année choisie
						// pour
						// la simu alors on sort l'agent du calcul
						Integer anneeNumerique = avct.getAnnee();
						Integer anneeDateAvctMoyNumerique = Integer.valueOf(sdfFormatDate.format(avct.getDateAvctMoy())
								.substring(6, sdfFormatDate.format(avct.getDateAvctMoy()).length()));
						if (anneeDateAvctMoyNumerique > anneeNumerique) {
							continue;
						}

						// le grade suivant reste le meme
						avct.setIdNouvGrade(gradeActuel.getCodeGrade() == null
								|| gradeActuel.getCodeGrade().length() == 0 ? null : gradeActuel.getCodeGrade());
						avct.setCdcadr(gradeActuel.getCodeCadre());

						// IBA,INM,INA
						Bareme bareme = Bareme.chercherBareme(getTransaction(), carr.getIban());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						avct.setIban(carr.getIban());
						avct.setInm(Integer.valueOf(bareme.getInm()));
						avct.setIna(Integer.valueOf(bareme.getIna()));
						avct.setNouvIban(carr.getIban());
						avct.setNouvInm(Integer.valueOf(bareme.getInm()));
						avct.setNouvIna(Integer.valueOf(bareme.getIna()));

					} else {

						// BM/ACC
						avct.setNouvBmAnnee(Integer.valueOf(carr.getBMAnnee()));
						avct.setNouvBmMois(Integer.valueOf(carr.getBMMois()));
						avct.setNouvBmJour(Integer.valueOf(carr.getBMJour()));
						avct.setNouvAccAnnee(Integer.valueOf(carr.getACCAnnee()));
						avct.setNouvAccMois(Integer.valueOf(carr.getACCMois()));
						avct.setNouvAccJour(Integer.valueOf(carr.getACCJour()));

						// par defaut avis CAP = "MOYENNE"
						AvisCap avisCap = getAvisCapDao().chercherAvisCapByLibCourt(Const.AVIS_CAP_MOY);
						avct.setIdAvisCap(avisCap.getIdAvisCap());

						// calcul BM/ACC applicables
						int nbJoursBM = AvancementFonctionnaires.calculJourBM(gradeActuel, carr);
						int nbJoursACC = AvancementFonctionnaires.calculJourACC(gradeActuel, carr);

						int nbJoursBonusDepart = nbJoursBM + nbJoursACC;
						int nbJoursBonus = nbJoursBM + nbJoursACC;
						// Calcul date avancement au Grade actuel
						if (gradeActuel.getDureeMin() != null && gradeActuel.getDureeMin().length() != 0
								&& !gradeActuel.getDureeMin().equals("0")) {
							if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
								String date = carr.getDateDebut().substring(0, 6) + annee;
								avct.setDateAvctMini(sdfFormatDate.parse(date));
								nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
							} else {
								avct.setDateAvctMini(AvancementFonctionnaires.calculDateAvctMini(gradeActuel, carr));
								nbJoursBonus = 0;
							}
						}
						if (gradeActuel.getDureeMoy() != null && gradeActuel.getDureeMoy().length() != 0) {
							avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
							if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
								String date = carr.getDateDebut().substring(0, 6) + annee;
								avct.setDateAvctMoy(sdfFormatDate.parse(date));
								nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
							} else {
								avct.setDateAvctMoy(AvancementFonctionnaires.calculDateAvctMoy(gradeActuel, carr));
								nbJoursBonus = 0;
							}
						}
						if (gradeActuel.getDureeMax() != null && gradeActuel.getDureeMax().length() != 0
								&& !gradeActuel.getDureeMax().equals("0")) {
							if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
								String date = carr.getDateDebut().substring(0, 6) + annee;
								avct.setDateAvctMaxi(sdfFormatDate.parse(date));
								nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
							} else {
								avct.setDateAvctMaxi(AvancementFonctionnaires.calculDateAvctMaxi(gradeActuel, carr));
								nbJoursBonus = 0;
							}
						}

						// si la date avct moy (année ) sup à l'année choisie
						// pour
						// la simu alors on sort l'agent du calcul
						Integer anneeNumerique = avct.getAnnee();
						Integer anneeDateAvctMoyNumerique = Integer.valueOf(sdfFormatDate.format(avct.getDateAvctMoy())
								.substring(6, sdfFormatDate.format(avct.getDateAvctMoy()).length()));
						if (anneeDateAvctMoyNumerique > anneeNumerique) {
							continue;
						}

						// Calcul du grade suivant (BM/ACC)
						Grade gradeSuivant = Grade.chercherGrade(getTransaction(), gradeActuel.getCodeGradeSuivant());
						if (gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0
								&& Services.estNumerique(gradeSuivant.getDureeMoy())) {
							boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
							while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null
									&& gradeSuivant.getCodeGradeSuivant().length() > 0
									&& gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0) {
								nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
								gradeSuivant = Grade
										.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
								isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
							}
						}

						int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer
								.parseInt(Const.ZERO);
						int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

						avct.setNouvBmAnnee(nbJoursRestantsBM / 365);
						avct.setNouvBmMois((nbJoursRestantsBM % 365) / 30);
						avct.setNouvBmJour((nbJoursRestantsBM % 365) % 30);

						avct.setNouvAccAnnee(nbJoursRestantsACC / 365);
						avct.setNouvAccMois((nbJoursRestantsACC % 365) / 30);
						avct.setNouvAccJour((nbJoursRestantsACC % 365) % 30);

						avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null
								|| gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());
						avct.setCdcadr(gradeActuel.getCodeCadre());

						// IBA,INM,INA
						Bareme bareme = Bareme.chercherBareme(getTransaction(), carr.getIban());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						avct.setIban(carr.getIban());
						avct.setInm(Integer.valueOf(bareme.getInm()));
						avct.setIna(Integer.valueOf(bareme.getIna()));

						// on cherche le nouveau bareme
						if (gradeSuivant != null && gradeSuivant.getIban() != null) {
							Bareme nouvBareme = Bareme.chercherBareme(getTransaction(), gradeSuivant.getIban());
							// on rempli les champs
							avct.setNouvIban(nouvBareme.getIban());
							avct.setNouvInm(Integer.valueOf(nouvBareme.getInm()));
							avct.setNouvIna(Integer.valueOf(nouvBareme.getIna()));
						}
					}

					// on regarde si l'agent est AFFECTE dans une autre
					// administration
					if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56")
							|| paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
						avct.setDirectionService(null);
						avct.setSectionService(null);
						// alors on va chercher l'autre administration de
						// l'agent
						try {
							AutreAdministrationAgent autreAdminAgent = getAutreAdministrationAgentDao()
									.chercherAutreAdministrationAgentActive(a.getIdAgent());
							if (autreAdminAgent != null && autreAdminAgent.getIdAutreAdmin() != null) {
								avct.setDirectionService(autreAdminAgent.getIdAutreAdmin().toString());
							}
						} catch (Exception e2) {
							// pas d'autre admin
						}
					} else {

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
						Service direction = Service.getDirection(getTransaction(), fp.getIdServi());
						Service section = Service.getSection(getTransaction(), fp.getIdServi());
						avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigleService());
						avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigleService());
					}

					if (carr != null) {
						if (carr.getCodeGrade() != null && carr.getCodeGrade().length() != 0) {
							Grade grd = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
							avct.setGrade(grd.getCodeGrade());

							// on prend l'id motif de la colonne CDTAVA du grade
							// si CDTAVA correspond à AVANCEMENT DIFF alors on
							// calcul les 3 dates sinon on calcul juste la date
							// moyenne
							if (grd.getCodeTava() != null && !grd.getCodeTava().equals(Const.CHAINE_VIDE)) {
								avct.setIdMotifAvct(Integer.valueOf(grd.getCodeTava()));
								MotifAvancement motif = getMotifAvancementDao().chercherMotifAvancementByLib(
										"AVANCEMENT DIFFERENCIE");
								if (motif.getIdMotifAvct() != avct.getIdMotifAvct()) {
									avct.setDateAvctMaxi(null);
									avct.setDateAvctMini(null);
								}
							} else {
								avct.setIdMotifAvct(null);
							}

							if (grd.getCodeGradeGenerique() != null) {
								// on cherche le grade generique pour trouver la
								// filiere
								GradeGenerique ggCarr = GradeGenerique.chercherGradeGenerique(getTransaction(),
										grd.getCodeGradeGenerique());
								if (getTransaction().isErreur())
									getTransaction().traiterErreur();

								if (ggCarr != null && ggCarr.getCdfili() != null) {
									FiliereGrade fil = FiliereGrade.chercherFiliereGrade(getTransaction(),
											ggCarr.getCdfili());
									avct.setFiliere(fil.getLibFiliere());
								}
							}
						}
					}
					avct.setDateGrade(sdfFormatDate.parse(carr.getDateDebut()));
					avct.setBmAnnee(Integer.valueOf(carr.getBMAnnee()));
					avct.setBmMois(Integer.valueOf(carr.getBMMois()));
					avct.setBmJour(Integer.valueOf(carr.getBMJour()));
					avct.setAccAnnee(Integer.valueOf(carr.getACCAnnee()));
					avct.setAccMois(Integer.valueOf(carr.getACCMois()));
					avct.setAccJour(Integer.valueOf(carr.getACCJour()));

					// on regarde si l'agent a une carriere de simulation dejà
					// saisie
					// autrement dis si la carriere actuelle a pour datfin 0
					if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
						avct.setCarriereSimu(null);
					} else {
						avct.setCarriereSimu("S");
					}

					avct.setDateVerifSef(null);
					avct.setDateVerifSgc(null);
					avct.setNumArrete(annee);

					getAvancementFonctionnairesDao().creerAvancement(avct.getIdAvisCap(), avct.getIdAgent(),
							avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
							avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(),
							avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(),
							avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(),
							avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(),
							avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(),
							avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(),
							avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(),
							avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
							avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
							avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
							avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
							avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
							avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
							avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
							avct.getCodePa());

					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
				} else {
					// on informe les agents en erreur
					agentEnErreurHautGrille += a.getNomAgent() + " " + a.getPrenomAgent() + " (" + a.getNomatr()
							+ "); ";
				}
			}
		}
		return true;
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
	 * Retourne une hashTable de la hiérarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CHANGER_ANNEE(HttpServletRequest request) throws Exception {
		agentEnErreur = Const.CHAINE_VIDE;
		String annee = getListeAnnee()[0];

		// recuperation du service
		ArrayList<String> listeSousService = null;
		if (getVAL_ST_CODE_SERVICE().length() != 0) {
			// on recupere les sous-service du service selectionne
			Service serv = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			listeSousService = Service.listSousServiceBySigle(getTransaction(), serv.getSigleService());
		}

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		setListeAvct(getAvancementFonctionnairesDao().listerAvancementAvecAnneeEtat(Integer.valueOf(annee), null, null,
				agent == null ? null : agent.getIdAgent(), listeSousService, null, null));
		afficherListeAvct(request);

		return true;
	}

	private void afficherListeAvct(HttpServletRequest request) throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires av = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = av.getIdAvct();
			Agent agent = getAgentDao().chercherAgent(av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DIRECTION(i),
					Services.estNumerique(av.getDirectionService()) ? getAutreAdministrationDao()
							.chercherAutreAdministration(Integer.valueOf(av.getDirectionService())).getLibAutreAdmin()
							: av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i),
					(av.getCdcadr() == null ? "&nbsp;" : av.getCdcadr()) + " <br> " + av.getFiliere());
			PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
			addZone(getNOM_ST_PA(i), pa.getLiPAdm());
			addZone(getNOM_ST_DATE_DEBUT(i), sdfFormatDate.format(av.getDateGrade()));
			addZone(getNOM_ST_BM_A(i), av.getBmAnnee() + " <br> " + av.getNouvBmAnnee());
			addZone(getNOM_ST_BM_M(i), av.getBmMois() + " <br> " + av.getNouvBmMois());
			addZone(getNOM_ST_BM_J(i), av.getBmJour() + " <br> " + av.getNouvBmJour());
			addZone(getNOM_ST_ACC_A(i), av.getAccAnnee() + " <br> " + av.getNouvAccAnnee());
			addZone(getNOM_ST_ACC_M(i), av.getAccMois() + " <br> " + av.getNouvAccMois());
			addZone(getNOM_ST_ACC_J(i), av.getAccJour() + " <br> " + av.getNouvAccJour());
			addZone(getNOM_ST_GRADE_ANCIEN(i), av.getGrade());
			addZone(getNOM_ST_GRADE_NOUVEAU(i),
					(av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct().toString());
			addZone(getNOM_ST_PERIODE_STD(i), av.getPeriodeStandard().toString());
			addZone(getNOM_ST_DATE_AVCT(i),
					(av.getDateAvctMini() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateAvctMini())) + " <br> "
							+ sdfFormatDate.format(av.getDateAvctMoy()) + " <br> "
							+ (av.getDateAvctMaxi() == null ? "&nbsp;" : sdfFormatDate.format(av.getDateAvctMaxi())));

			addZone(getNOM_CK_VALID_DRH(i),
					av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_ST_MOTIF_AVCT(i),
					av.getIdMotifAvct() == null ? "&nbsp;" : getHashMotifAvancement().get(av.getIdMotifAvct())
							.getLibMotifAvct());
			addZone(getNOM_LB_AVIS_CAP_SELECT(i),
					av.getIdAvisCap() == null ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAP().indexOf(
							getHashAvisCAP().get(av.getIdAvisCap()))));
			addZone(getNOM_CK_PROJET_ARRETE(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())
					|| av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_EF_DATE_ARRETE(i),
					av.getDateArrete() == null ? Const.CHAINE_VIDE : sdfFormatDate.format(av.getDateArrete()));

			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue())
					|| av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());

			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());

		}
	}

	/**
	 * Getter de la HashTable MotifAvancement.
	 * 
	 * @return Hashtable<String, MotifAvancement>
	 */
	private Hashtable<Integer, MotifAvancement> getHashMotifAvancement() {
		if (hashMotifAvct == null)
			hashMotifAvct = new Hashtable<Integer, MotifAvancement>();
		return hashMotifAvct;
	}

	/**
	 * Getter de la HashTable AvisCAP.
	 * 
	 * @return Hashtable<String, AvisCap>
	 */
	private Hashtable<Integer, AvisCap> getHashAvisCAP() {
		if (hashAvisCAP == null)
			hashAvisCAP = new Hashtable<Integer, AvisCap>();
		return hashAvisCAP;
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<AvancementFonctionnaires> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementFonctionnaires>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementFonctionnaires> listeAvct) {
		this.listeAvct = listeAvct;
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
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_LIB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE_LIB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_A(int i) {
		return "NOM_ST_BM_A_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BM_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_A(int i) {
		return getZone(getNOM_ST_BM_A(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_J(int i) {
		return "NOM_ST_BM_J_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BM_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_J(int i) {
		return getZone(getNOM_ST_BM_J(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_M(int i) {
		return "NOM_ST_BM_M_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BM_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_M(int i) {
		return getZone(getNOM_ST_BM_M(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_A(int i) {
		return "NOM_ST_ACC_A_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACC_A Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_A(int i) {
		return getZone(getNOM_ST_ACC_A(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_J(int i) {
		return "NOM_ST_ACC_J_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACC_J Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_J(int i) {
		return getZone(getNOM_ST_ACC_J(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_M(int i) {
		return "NOM_ST_ACC_M_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACC_M Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_M(int i) {
		return getZone(getNOM_ST_ACC_M(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PERIODE_STD Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_PERIODE_STD(int i) {
		return "NOM_ST_PERIODE_STD_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PERIODE_STD
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_PERIODE_STD(int i) {
		return getZone(getNOM_ST_PERIODE_STD(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_AVCT(int i) {
		return "NOM_ST_DATE_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_AVCT(int i) {
		return getZone(getNOM_ST_DATE_AVCT(i));
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
	 * à cocher : CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
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
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_PROJET_ARRETE(int i) {
		return "NOM_CK_PROJET_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
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
	 * à cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_DRH(int i) {
		return getZone(getNOM_CK_VALID_DRH(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP(int i) {
		return "NOM_LB_AVIS_CAP_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_AVIS_CAP_SELECT Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_" + i + "_SELECT";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP(int i) {
		if (LB_AVIS_CAP == null)
			LB_AVIS_CAP = initialiseLazyLB();
		return LB_AVIS_CAP;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP(String[] newLB_AVIS_CAP) {
		LB_AVIS_CAP = newLB_AVIS_CAP;
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP(int i) {
		return getLB_AVIS_CAP(i);
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_AVIS_CAP_SELECT(int i) {
		return getZone(getNOM_LB_AVIS_CAP_SELECT(i));
	}

	/**
	 * Getter de la liste des avis CAP.
	 * 
	 * @return listeAvisCAP
	 */
	private ArrayList<AvisCap> getListeAvisCAP() {
		return listeAvisCAP;
	}

	/**
	 * Setter de la liste des avis CAP.
	 * 
	 * @param listeAvisCAP
	 */
	private void setListeAvisCAP(ArrayList<AvisCap> listeAvisCAP) {
		this.listeAvisCAP = listeAvisCAP;
	}

	/**
	 * Getter de la liste des motifs d'avancement.
	 * 
	 * @return listeMotifAvct
	 */
	private ArrayList<MotifAvancement> getListeMotifAvct() {
		return listeMotifAvct;
	}

	/**
	 * Setter de la liste des motifs d'avancement.
	 * 
	 * @param listeMotifAvct
	 */
	private void setListeMotifAvct(ArrayList<MotifAvancement> listeMotifAvct) {
		this.listeMotifAvct = listeMotifAvct;
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// on sauvegarde l'état du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else if (getVAL_CK_PROJET_ARRETE(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				} else if (getVAL_CK_VALID_DRH(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
				}
				// on traite l'avis CAP
				int indiceAvisCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_SELECT(i)) ? Integer
						.parseInt(getVAL_LB_AVIS_CAP_SELECT(i)) : -1);
				if (indiceAvisCap != -1) {
					Integer idAvisCap = ((AvisCap) getListeAvisCAP().get(indiceAvisCap)).getIdAvisCap();
					avct.setIdAvisCap(idAvisCap);
				}
				// on traite le numero et la date d'arreté
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdfFormatDate
						.parse(getVAL_EF_DATE_ARRETE(i)));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
			}
			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(),
					avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
					avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(),
					avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
					avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
					avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
					avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
					avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(),
					avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(),
					avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(),
					avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
					avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
					avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
					avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
					avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
					avct.getCodePa());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		performPB_CHANGER_ANNEE(request);
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concerné
					Agent agentCarr = getAgentDao().chercherAgent(avct.getIdAgent());
					// on recupere la derniere carrière dans l'année
					Carriere carr = Carriere.chercherDerniereCarriereAvecAgentEtAnnee(getTransaction(),
							agentCarr.getNomatr(), avct.getAnnee().toString());
					// si la carriere est bien la derniere de la liste
					if (carr.getDateFin() == null || carr.getDateFin().equals("0")) {
						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());

						// on traite l'avis CAP
						int indiceAvisCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_SELECT(i)) ? Integer
								.parseInt(getVAL_LB_AVIS_CAP_SELECT(i)) : -1);
						if (indiceAvisCap != -1) {
							Integer idAvisCap = ((AvisCap) getListeAvisCAP().get(indiceAvisCap)).getIdAvisCap();
							avct.setIdAvisCap(idAvisCap);
						}
						// on traite le numero et la date d'arreté
						avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdfFormatDate
								.parse(getVAL_EF_DATE_ARRETE(i)));
						avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
						// avct.modifierAvancement(getTransaction());

						// on regarde l'avis CAP selectionné pour determiné la
						// date de debut de carriere et la date de fin de la
						// precedente

						String libCourtAvisCap = getAvisCapDao().chercherAvisCap(avct.getIdAvisCap())
								.getLibCourtAvisCap();
						Date dateAvct = avct.getDateAvctMoy();
						if (libCourtAvisCap.toUpperCase().equals("MIN")) {
							dateAvct = avct.getDateAvctMini();
						} else if (libCourtAvisCap.toUpperCase().equals("MOY")) {
							dateAvct = avct.getDateAvctMoy();
						} else if (libCourtAvisCap.toUpperCase().equals("MAX")) {
							dateAvct = avct.getDateAvctMaxi();
						} else if (libCourtAvisCap.toUpperCase().equals("FAV")) {
							dateAvct = avct.getDateAvctMoy();
						} else {
							agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " ("
									+ agentCarr.getNomatr() + "); ";
							continue;
						}
						if (dateAvct == null) {
							dateAvct = avct.getDateAvctMoy();
						}

						// on ferme cette carriere
						carr.setDateFin(sdfFormatDate.format(dateAvct));
						// RG_AG_CA_A03
						HistoCarriere histo = new HistoCarriere(carr);
						getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
						carr.modifierCarriere(getTransaction(), agentCarr, user);

						// on crée un nouvelle carriere
						Carriere nouvelleCarriere = new Carriere();

						if (avct.getCodeCategorie() == 2) {
							nouvelleCarriere.setCodeCategorie("1");
						} else if (avct.getCodeCategorie() == 18) {
							nouvelleCarriere.setCodeCategorie("20");
						} else {
							nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
						}
						nouvelleCarriere.setReferenceArrete(avct.getNumArrete().equals(Const.CHAINE_VIDE) ? Const.ZERO
								: avct.getNumArrete());
						nouvelleCarriere.setDateArrete(avct.getDateArrete() == null ? Const.ZERO : sdfFormatDate
								.format(avct.getDateArrete()));
						nouvelleCarriere.setDateDebut(sdfFormatDate.format(dateAvct));
						nouvelleCarriere.setDateFin(Const.ZERO);
						// on calcul Grade - ACC/BM en fonction de l'avis CAP
						// il est différent du resultat affiché dans le tableau
						// si AVIS_CAP != MOY
						// car pour la simulation on prenait comme ref de calcul
						// la duree MOY
						if ((carr.getCodeCategorie().equals("2") || carr.getCodeCategorie().equals("18"))
								&& avct.getPeriodeStandard().equals(12)) {
							nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
							nouvelleCarriere.setACCAnnee(avct.getNouvAccAnnee().toString());
							nouvelleCarriere.setACCMois(avct.getNouvAccMois().toString());
							nouvelleCarriere.setACCJour(avct.getNouvAccJour().toString());
							nouvelleCarriere.setBMAnnee(avct.getNouvBmAnnee().toString());
							nouvelleCarriere.setBMMois(avct.getNouvBmMois().toString());
							nouvelleCarriere.setBMJour(avct.getNouvBmJour().toString());
						} else {
							calculAccBm(avct, carr, nouvelleCarriere, libCourtAvisCap);
						}

						// on recupere iban du grade
						Grade gradeSuivant = Grade.chercherGrade(getTransaction(), avct.getIdNouvGrade());
						if (Services.estNumerique(gradeSuivant.getIban())) {
							nouvelleCarriere.setIban(Services.lpad(gradeSuivant.getIban(), 7, "0"));
						} else {
							nouvelleCarriere.setIban(gradeSuivant.getIban());
						}

						// champ à remplir pour creer une carriere NB : on
						// reprend ceux de la carriere precedente
						nouvelleCarriere.setCodeBase(carr.getCodeBase());
						nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
						nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
						nouvelleCarriere.setIdMotif(Const.ZERO);
						nouvelleCarriere.setModeReglement(carr.getModeReglement());
						nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

						// RG_AG_CA_A03
						nouvelleCarriere.setNoMatricule(agentCarr.getNomatr().toString());
						HistoCarriere histo2 = new HistoCarriere(nouvelleCarriere);
						getHistoCarriereDao().creerHistoCarriere(histo2, user, EnumTypeHisto.CREATION);
						nouvelleCarriere.creerCarriere(getTransaction(), agentCarr, user);

						getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(),
								avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(),
								avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(),
								avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(),
								avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(),
								avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
								avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
								avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
								avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
								avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(),
								avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
								avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
								avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
								avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
								avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(),
								avct.getDateCap(), avct.getObservationArr(), avct.getUserVerifArrImpr(),
								avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(),
								avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa());

						// on enregistre

						if (getTransaction().isErreur()) {
							return false;
						} else {
							nbAgentAffectes += 1;
						}
					} else {
						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste à l'ecran
						agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " ("
								+ agentCarr.getNomatr() + "); ";
					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();
		performPB_CHANGER_ANNEE(request);
		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		return true;
	}

	private void calculAccBm(AvancementFonctionnaires avct, Carriere ancienneCarriere, Carriere nouvelleCarriere,
			String libCourtAvisCap) throws Exception {
		Grade gradeActuel = Grade.chercherGrade(getTransaction(), ancienneCarriere.getCodeGrade());
		// calcul BM/ACC applicables
		int nbJoursBM = AvancementFonctionnaires.calculJourBM(gradeActuel, ancienneCarriere);
		int nbJoursACC = AvancementFonctionnaires.calculJourACC(gradeActuel, ancienneCarriere);

		int nbJoursBonus = nbJoursBM + nbJoursACC;

		// Calcul date avancement au Grade actuel
		if (libCourtAvisCap.equals("Min")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
				String date = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMini(sdfFormatDate.parse(date));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMin()) * 30;
			} else {
				avct.setDateAvctMini(AvancementFonctionnaires.calculDateAvctMini(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("Moy")) {
			avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
				String date = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMoy(sdfFormatDate.parse(date));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
			} else {
				avct.setDateAvctMoy(AvancementFonctionnaires.calculDateAvctMoy(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("Max")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
				String date = ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee();
				avct.setDateAvctMaxi(sdfFormatDate.parse(date));
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMax()) * 30;
			} else {
				avct.setDateAvctMaxi(AvancementFonctionnaires.calculDateAvctMaxi(gradeActuel, ancienneCarriere));
				nbJoursBonus = 0;
			}
		}

		// Calcul du grade suivant (BM/ACC)
		Grade gradeSuivant = Grade.chercherGrade(getTransaction(), gradeActuel.getCodeGradeSuivant());
		if (libCourtAvisCap.equals("Min")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null
					&& gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMin() != null
					&& gradeSuivant.getDureeMin().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMin()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			}
		} else if (libCourtAvisCap.equals("Moy")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null
					&& gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMoy() != null
					&& gradeSuivant.getDureeMoy().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			}
		} else if (libCourtAvisCap.equals("Max")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null
					&& gradeSuivant.getCodeGradeSuivant().length() > 0 && gradeSuivant.getDureeMax() != null
					&& gradeSuivant.getDureeMax().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMax()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			}
		}

		int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
		int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

		// on met à jour les champs de l'avancement pour affichage tableau
		avct.setNouvBmAnnee(nbJoursRestantsBM / 365);
		avct.setNouvBmMois((nbJoursRestantsBM % 365) / 30);
		avct.setNouvBmJour((nbJoursRestantsBM % 365) % 30);

		avct.setNouvAccAnnee(nbJoursRestantsACC / 365);
		avct.setNouvAccMois((nbJoursRestantsACC % 365) / 30);
		avct.setNouvAccJour((nbJoursRestantsACC % 365) % 30);

		avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null
				: gradeSuivant.getCodeGrade());

		// on met à jour les champs pour la creation de la carriere
		nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
		nouvelleCarriere.setACCAnnee(avct.getNouvAccAnnee().toString());
		nouvelleCarriere.setACCMois(avct.getNouvAccMois().toString());
		nouvelleCarriere.setACCJour(avct.getNouvAccJour().toString());
		nouvelleCarriere.setBMAnnee(avct.getNouvBmAnnee().toString());
		nouvelleCarriere.setBMMois(avct.getNouvBmMois().toString());
		nouvelleCarriere.setBMJour(avct.getNouvBmJour().toString());
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setListeAvct(new ArrayList<AvancementFonctionnaires>());
		afficherListeAvct(request);
		return true;
	}

	public String getNOM_ST_PA(int i) {
		return "NOM_ST_PA_" + i;
	}

	public String getVAL_ST_PA(int i) {
		return getZone(getNOM_ST_PA(i));
	}

	public String getNOM_ST_GRADE_ANCIEN(int i) {
		return "NOM_ST_GRADE_ANCIEN_" + i;
	}

	public String getVAL_ST_GRADE_ANCIEN(int i) {
		return getZone(getNOM_ST_GRADE_ANCIEN(i));
	}

	public String getNOM_ST_GRADE_NOUVEAU(int i) {
		return "NOM_ST_GRADE_NOUVEAU_" + i;
	}

	public String getVAL_ST_GRADE_NOUVEAU(int i) {
		return getZone(getNOM_ST_GRADE_NOUVEAU(i));
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public MotifAvancementDao getMotifAvancementDao() {
		return motifAvancementDao;
	}

	public void setMotifAvancementDao(MotifAvancementDao motifAvancementDao) {
		this.motifAvancementDao = motifAvancementDao;
	}

	public AutreAdministrationDao getAutreAdministrationDao() {
		return autreAdministrationDao;
	}

	public void setAutreAdministrationDao(AutreAdministrationDao autreAdministrationDao) {
		this.autreAdministrationDao = autreAdministrationDao;
	}

	public AvisCapDao getAvisCapDao() {
		return avisCapDao;
	}

	public void setAvisCapDao(AvisCapDao avisCapDao) {
		this.avisCapDao = avisCapDao;
	}

	public AutreAdministrationAgentDao getAutreAdministrationAgentDao() {
		return autreAdministrationAgentDao;
	}

	public void setAutreAdministrationAgentDao(AutreAdministrationAgentDao autreAdministrationAgentDao) {
		this.autreAdministrationAgentDao = autreAdministrationAgentDao;
	}

	public AvancementFonctionnairesDao getAvancementFonctionnairesDao() {
		return avancementFonctionnairesDao;
	}

	public void setAvancementFonctionnairesDao(AvancementFonctionnairesDao avancementFonctionnairesDao) {
		this.avancementFonctionnairesDao = avancementFonctionnairesDao;
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