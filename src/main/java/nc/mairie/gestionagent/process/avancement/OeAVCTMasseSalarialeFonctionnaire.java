package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.referentiel.AvisCap;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTCampagneTableauBord Date de cr�ation : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTMasseSalarialeFonctionnaire extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OeAVCTMasseSalarialeFonctionnaire.class);

	private String[] LB_ANNEE;
	private String[] LB_AVIS_CAP;

	private Hashtable<String, MotifAvancement> hashMotifAvct;
	private Hashtable<String, AvisCap> hashAvisCAP;
	private ArrayList<AvisCap> listeAvisCAP;
	private ArrayList<MotifAvancement> listeMotifAvct;

	private String[] listeAnnee;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private ArrayList<AvancementFonctionnaires> listeAvct;
	public String agentEnErreur = Const.CHAINE_VIDE;

	public String ACTION_CALCUL = "Calcul";

	public static final int STATUT_RECHERCHER_AGENT = 1;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// V�rification des droits d'acc�s. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
		initialiseListeDeroulante();

		AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (agt != null && agt.getIdAgent() != null && !agt.getIdAgent().equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_AGENT(), agt.getNoMatricule());
			performPB_LANCER(request);
		}
	}

	private void initialiseListeDeroulante() throws Exception {

		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = Services.dateDuJour().substring(6, 10);
			setListeAnnee(new String[1]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante) + 1);
			setLB_ANNEE(getListeAnnee());
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

				// recherche du sup�rieur
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
			ArrayList<MotifAvancement> motif = MotifAvancement.listerMotifAvancementSansRevalo(getTransaction());
			setListeMotifAvct(motif);

			// remplissage de la hashTable
			for (int i = 0; i < getListeMotifAvct().size(); i++) {
				MotifAvancement m = (MotifAvancement) getListeMotifAvct().get(i);
				getHashMotifAvancement().put(m.getIdMotifAvct(), m);
			}
		}

		// Si liste avisCAP vide alors affectation
		if (getListeAvisCAP() == null || getListeAvisCAP().size() == 0) {
			ArrayList<AvisCap> avis = AvisCap.listerAvisCap(getTransaction());
			setListeAvisCAP(avis);

			int[] tailles = { 7 };
			String[] champs = { "libLongAvisCAP" };
			setLB_AVIS_CAP(new FormateListe(tailles, avis, champs).getListeFormatee());

			// remplissage de la hashTable
			for (int i = 0; i < getListeAvisCAP().size(); i++) {
				AvisCap ac = (AvisCap) getListeAvisCAP().get(i);
				getHashAvisCAP().put(ac.getIdAvisCAP(), ac);
			}
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (21/11/11 09:55:36)
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
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTMasseSalarialeFonctionnaire() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTMasseSalarialeFonctionnaire.jsp";
	}

	/**
	 * Getter du nom de l'�cran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SIMU-MASSE-FONCT";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de cr�ation :
	 * (21/11/11 11:11:24)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de cr�ation :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_ANNEE Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_ANNEE Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Getter de la liste des ann�es possibles de simulation.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des ann�es possibles de simulation.
	 * 
	 * @param listeAnnee
	 *            listeAnnee � d�finir
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (12/09/11 11:49:01)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (12/09/11 11:49:01)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * cr�ation : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AGENT Date de
	 * cr�ation : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * cr�ation : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {

		// On met l'agent courant en var d'activit�
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de cr�ation : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enl�ve l'agent selectionn�e
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
	 * Date de cr�ation : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enl�ve le service selectionn�e
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_LANCER Date de cr�ation :
	 * (21/11/11 11:11:24)
	 * 
	 */
	public String getNOM_PB_LANCER() {
		return "NOM_PB_LANCER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public boolean performPB_LANCER(HttpServletRequest request) throws Exception {

		// Mise � jour de l'action men�e
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);

		String an = getListeAnnee()[0];

		// Suppression des avancements � l'�tat 'Travail' de la cat�gorie donn�e
		// et de l'ann�e
		AvancementFonctionnaires.supprimerAvancementTravailAvecCategorie(getTransaction(), an);

		// recuperation agent
		AgentNW agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT());
		}

		if (!performCalculFonctionnaire(getVAL_ST_CODE_SERVICE(), an, agent))
			return false;

		commitTransaction();
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT, an);

		// "INF200","Simulation effectu�e"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF200"));

		return true;
	}

	/**
	 * M�thode de calcul des avancements Fonctionnaires.
	 * 
	 * @param codeService
	 * @param annee
	 * @param agent
	 * @throws Exception
	 */
	private boolean performCalculFonctionnaire(String codeService, String annee, AgentNW agent) throws Exception {
		ArrayList<AgentNW> la = new ArrayList<AgentNW>();
		if (agent != null) {
			// il faut regarder si cet agent est de type Convention Collective
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carr == null
					|| carr.getCodeCategorie() == null
					|| (!carr.getCodeCategorie().equals("1") && !carr.getCodeCategorie().equals("2") && !carr.getCodeCategorie().equals("18") && !carr
							.getCodeCategorie().equals("20"))) {
				// "ERR181",
				// "Cet agent n'est pas de type @. Il ne peut pas �tre soumis � l'avancement @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR181", "fonctionnaire", "des fonctionnaires"));
				return false;
			}
			la.add(agent);
		} else {
			// R�cup�ration des agents
			// on recupere les sous-service du service selectionne

			ArrayList<String> listeSousService = null;
			if (!codeService.equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(), codeService);
				listeSousService = Service.listSousService(getTransaction(), serv.getSigleService());
			}

			// R�cup�ration des agents
			ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(getTransaction(), annee, "Fonctionnaire");
			String listeNomatrAgent = Const.CHAINE_VIDE;
			for (Carriere carr : listeCarriereActive) {
				listeNomatrAgent += carr.getNoMatricule() + ",";
			}
			if (!listeNomatrAgent.equals(Const.CHAINE_VIDE)) {
				listeNomatrAgent = listeNomatrAgent.substring(0, listeNomatrAgent.length() - 1);
			}
			la = AgentNW.listerAgentEligibleAvct(getTransaction(), listeSousService, listeNomatrAgent);
		}
		// Parcours des agents
		for (int i = 0; i < la.size(); i++) {
			AgentNW a = la.get(i);

			// Recuperation de la carriere en cours
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), a);
			if (getTransaction().isErreur() || carr == null || carr.getDateDebut() == null) {
				getTransaction().traiterErreur();
				continue;
			}
			PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(getTransaction(), a.getNoMatricule(), Services
					.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
			if (getTransaction().isErreur() || paAgent == null || paAgent.getCdpadm() == null || paAgent.estPAInactive(getTransaction())) {
				getTransaction().traiterErreur();
				continue;
			}

			// R�cup�ration de l'avancement
			AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementAvecAnneeEtAgent(getTransaction(), annee, a.getIdAgent());
			if (getTransaction().isErreur() || avct.getIdAvct() == null) {
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				// on regarde si il y a d'autre carrieres avec le meme grade
				// si oui on prend la carriere plus lointaine
				ArrayList<Carriere> listeCarrMemeGrade = Carriere.listerCarriereAvecGrade(getTransaction(), a.getNoMatricule(), carr.getCodeGrade());
				if (listeCarrMemeGrade != null && listeCarrMemeGrade.size() > 0) {
					carr = (Carriere) listeCarrMemeGrade.get(0);
				}
				Grade gradeActuel = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					continue;
				}
				// Si pas de grade suivant, agent non �ligible
				if (gradeActuel.getCodeGradeSuivant() != null && gradeActuel.getCodeGradeSuivant().length() != 0) {
					// Cr�ation de l'avancement
					avct = new AvancementFonctionnaires();
					avct.setIdAgent(a.getIdAgent());
					avct.setCodeCategorie(carr.getCodeCategorie());
					avct.setAnnee(annee);
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
					// BM/ACC
					avct.setNouvBMAnnee(carr.getBMAnnee());
					avct.setNouvBMMois(carr.getBMMois());
					avct.setNouvBMJour(carr.getBMJour());
					avct.setNouvACCAnnee(carr.getACCAnnee());
					avct.setNouvACCMois(carr.getACCMois());
					avct.setNouvACCJour(carr.getACCJour());

					// par defaut avis CAP = "MOYENNE"
					AvisCap avisCap = AvisCap.chercherAvisCapByLibCourt(getTransaction(), Const.AVIS_CAP_MOY);
					avct.setIdAvisCAP(avisCap.getIdAvisCAP());

					// calcul BM/ACC applicables
					int nbJoursBM = 0;
					int nbJoursACC = 0;
					if (gradeActuel.getBm().equals(Const.OUI)) {
						nbJoursBM += (Integer.parseInt(carr.getBMAnnee()) * 360) + (Integer.parseInt(carr.getBMMois()) * 30)
								+ Integer.parseInt(carr.getBMJour());
					}
					if (gradeActuel.getAcc().equals(Const.OUI)) {
						nbJoursACC += (Integer.parseInt(carr.getACCAnnee()) * 360) + (Integer.parseInt(carr.getACCMois()) * 30)
								+ Integer.parseInt(carr.getACCJour());
					}

					int nbJoursBonus = nbJoursBM + nbJoursACC;

					// Calcul date avancement au Grade actuel
					if (gradeActuel.getDureeMin() != null && gradeActuel.getDureeMin().length() != 0) {
						if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
							avct.setDateAvctMini(carr.getDateDebut().substring(0, 6) + annee);
						} else {
							avct.setDateAvctMini(Services.enleveJours(
									Services.ajouteMois(Services.formateDate(carr.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMin())),
									nbJoursBonus));
						}
					}
					if (gradeActuel.getDureeMoy() != null && gradeActuel.getDureeMoy().length() != 0) {
						avct.setDureeStandard(gradeActuel.getDureeMoy());
						if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
							avct.setDateAvctMoy(carr.getDateDebut().substring(0, 6) + annee);
							nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
						} else {
							avct.setDateAvctMoy(Services.enleveJours(
									Services.ajouteMois(Services.formateDate(carr.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMoy())),
									nbJoursBonus));
							nbJoursBonus = 0;
						}
					}
					if (gradeActuel.getDureeMax() != null && gradeActuel.getDureeMax().length() != 0) {
						if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
							avct.setDateAvctMaxi(carr.getDateDebut().substring(0, 6) + annee);
						} else {
							avct.setDateAvctMaxi(Services.enleveJours(
									Services.ajouteMois(Services.formateDate(carr.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMax())),
									nbJoursBonus));
						}
					}
					// si la date avct moy (ann�e ) sup � l'ann�e choisie pour
					// la simu alors on sort l'agent du calcul
					Integer anneeNumerique = Integer.valueOf(avct.getAnnee());
					Integer anneeDateAvctMoyNumerique = Integer.valueOf(avct.getDateAvctMoy().substring(6, avct.getDateAvctMoy().length()));
					if (anneeDateAvctMoyNumerique > anneeNumerique) {
						continue;
					}

					// Calcul du grade suivant (BM/ACC)
					Grade gradeSuivant = Grade.chercherGrade(getTransaction(), gradeActuel.getCodeGradeSuivant());
					if (gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0
							&& Services.estNumerique(gradeSuivant.getDureeMoy())) {
						boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
						while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
								&& gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0) {
							nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
							gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
							isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
						}
					}

					int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
					int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

					avct.setNouvBMAnnee(String.valueOf(nbJoursRestantsBM / 365));
					avct.setNouvBMMois(String.valueOf((nbJoursRestantsBM % 365) / 30));
					avct.setNouvBMJour(String.valueOf((nbJoursRestantsBM % 365) % 30));

					avct.setNouvACCAnnee(String.valueOf(nbJoursRestantsACC / 365));
					avct.setNouvACCMois(String.valueOf((nbJoursRestantsACC % 365) / 30));
					avct.setNouvACCJour(String.valueOf((nbJoursRestantsACC % 365) % 30));

					avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant
							.getCodeGrade());
					// avct.setLibNouvGrade(gradeSuivant.getLibGrade());
					avct.setCodeCadre(gradeActuel.getCodeCadre());

					// avct.setDateArrete("01/01/" + annee);
					// avct.setNumArrete(annee);

					// IBA,INM,INA
					Bareme bareme = Bareme.chercherBareme(getTransaction(), carr.getIban());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
					avct.setIban(carr.getIban());
					avct.setInm(bareme.getInm());
					avct.setIna(bareme.getIna());

					// on recupere le grade du poste
					Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), a.getIdAgent());
					if (aff.getIdFichePoste() == null) {
						// on ne fait rien
					} else {
						FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
						Service direction = Service.getDirection(getTransaction(), fp.getIdServi());
						Service section = Service.getSection(getTransaction(), fp.getIdServi());
						avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigleService());
						avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigleService());

						// on calcul le nouvel INM
						if (bareme != null && bareme.getInm() != null) {
							Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
							GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							}
							String nouvINM = Const.CHAINE_VIDE;
							if (gg.getNbPointsAvct() == null) {
								nouvINM = String.valueOf(Integer.valueOf(bareme.getInm()));
							} else {
								nouvINM = String.valueOf(Integer.valueOf(bareme.getInm()) + Integer.valueOf(gg.getNbPointsAvct()));
							}
							// avec ce nouvel INM on recupere l'iban et l'ina
							// correspondant
							ArrayList<Bareme> listeBarem = Bareme.listerBaremeByINM(getTransaction(), nouvINM);
							if (listeBarem.size() > 0) {
								Bareme nouvBareme = (Bareme) listeBarem.get(0);
								// on rempli les champs
								avct.setNouvIBAN(nouvBareme.getIban());
								avct.setNouvINM(nouvBareme.getInm());
								avct.setNouvINA(nouvBareme.getIna());
							}
						}
					}

					if (carr != null) {
						if (carr.getCodeGrade() != null && carr.getCodeGrade().length() != 0) {
							Grade grd = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
							avct.setGrade(grd.getCodeGrade());
							// avct.setLibelleGrade(grd.getLibGrade());

							// on prend l'id motif de la colonne CDTAVA du grade
							// si CDTAVA correspond � AVANCEMENT DIFF alors on
							// calcul les 3 dates sinon on calcul juste la date
							// moyenne
							if (grd.getCodeTava() != null && !grd.getCodeTava().equals(Const.CHAINE_VIDE)) {
								avct.setIdMotifAvct(grd.getCodeTava());
								MotifAvancement motif = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "AVANCEMENT DIFFERENCIE");
								if (!motif.getIdMotifAvct().equals(avct.getIdMotifAvct())) {
									// if
									// (!motif.getIdMotifAvct().equals(grd.getCodeTava()))
									// {

									avct.setDateAvctMaxi(Const.DATE_NULL);
									avct.setDateAvctMini(Const.DATE_NULL);
								}
							} else {
								avct.setIdMotifAvct(null);
							}

							if (grd.getCodeGradeGenerique() != null) {
								// on cherche le grade generique pour trouver la
								// filiere
								GradeGenerique ggCarr = GradeGenerique.chercherGradeGenerique(getTransaction(), grd.getCodeGradeGenerique());
								if (getTransaction().isErreur())
									getTransaction().traiterErreur();

								if (ggCarr != null && ggCarr.getCdfili() != null) {
									FiliereGrade fil = FiliereGrade.chercherFiliereGrade(getTransaction(), ggCarr.getCdfili());
									avct.setFiliere(fil.getLibFiliere());
								}
							}
						}
					}
					avct.setDateGrade(carr.getDateDebut());
					avct.setBMAnnee(carr.getBMAnnee());
					avct.setBMMois(carr.getBMMois());
					avct.setBMJour(carr.getBMJour());
					avct.setACCAnnee(carr.getACCAnnee());
					avct.setACCMois(carr.getACCMois());
					avct.setACCJour(carr.getACCJour());

					// on regarde si l'agent a une carriere de simulation dej�
					// saisie
					// autrement dis si la carriere actuelle a pour datfin 0
					if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
						avct.setCarriereSimu(null);
					} else {
						avct.setCarriereSimu("S");
					}

					avct.setDateVerifSEF(Const.DATE_NULL);
					avct.setDateVerifSGC(Const.DATE_NULL);
					avct.setNumArrete(annee);
					avct.creerAvancement(getTransaction());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
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
	 * Met � jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList<Service> listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Retourne une hashTable de la hi�rarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * cr�ation : (28/11/11)
	 * 
	 */
	public String getNOM_PB_CHANGER_ANNEE() {
		return "NOM_PB_CHANGER_ANNEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (28/11/11)
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

		setListeAvct(AvancementFonctionnaires.listerAvancementAvecAnneeEtat(getTransaction(), annee, null, null, null, listeSousService));
		afficherListeAvct(request);

		return true;
	}

	private void afficherListeAvct(HttpServletRequest request) throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires av = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(av.getIdAvct());
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent() + " <br> " + agent.getNoMatricule());
			addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i), (av.getCodeCadre() == null ? "&nbsp;" : av.getCodeCadre()) + " <br> " + av.getFiliere());
			addZone(getNOM_ST_DATE_DEBUT(i), av.getDateGrade());
			addZone(getNOM_ST_BM_A(i), av.getBMAnnee() + " <br> " + av.getNouvBMAnnee());
			addZone(getNOM_ST_BM_M(i), av.getBMMois() + " <br> " + av.getNouvBMMois());
			addZone(getNOM_ST_BM_J(i), av.getBMJour() + " <br> " + av.getNouvBMJour());
			addZone(getNOM_ST_ACC_A(i), av.getACCAnnee() + " <br> " + av.getNouvACCAnnee());
			addZone(getNOM_ST_ACC_M(i), av.getACCMois() + " <br> " + av.getNouvACCMois());
			addZone(getNOM_ST_ACC_J(i), av.getACCJour() + " <br> " + av.getNouvACCJour());
			addZone(getNOM_ST_GRADE(i),
					av.getGrade() + " <br> " + (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct());
			addZone(getNOM_ST_PERIODE_STD(i), av.getDureeStandard());
			addZone(getNOM_ST_DATE_AVCT(i), (av.getDateAvctMini() == null ? "&nbsp;" : av.getDateAvctMini()) + " <br> " + av.getDateAvctMoy()
					+ " <br> " + (av.getDateAvctMaxi() == null ? "&nbsp;" : av.getDateAvctMaxi()));

			addZone(getNOM_CK_VALID_DRH(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_ST_MOTIF_AVCT(i),
					av.getIdMotifAvct().equals(Const.CHAINE_VIDE) ? "&nbsp;" : getHashMotifAvancement().get(av.getIdMotifAvct()).getLibMotifAvct());
			addZone(getNOM_LB_AVIS_CAP_SELECT(i),
					av.getIdAvisCAP() == null || av.getIdAvisCAP().length() == 0 ? Const.CHAINE_VIDE : String.valueOf(getListeAvisCAP().indexOf(
							getHashAvisCAP().get(av.getIdAvisCAP()))));
			addZone(getNOM_CK_PROJET_ARRETE(i),
					av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) || av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF()
							: getCHECKED_ON());
			addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_EF_DATE_ARRETE(i), av.getDateArrete().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : av.getDateArrete());
			/*
			 * addZone(getNOM_CK_AFFECTER(i),
			 * av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue()) ||
			 * av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ?
			 * getCHECKED_ON() : getCHECKED_OFF());
			 */
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());

		}
	}

	/**
	 * Getter de la HashTable MotifAvancement.
	 * 
	 * @return Hashtable<String, MotifAvancement>
	 */
	private Hashtable<String, MotifAvancement> getHashMotifAvancement() {
		if (hashMotifAvct == null)
			hashMotifAvct = new Hashtable<String, MotifAvancement>();
		return hashMotifAvct;
	}

	/**
	 * Getter de la HashTable AvisCAP.
	 * 
	 * @return Hashtable<String, AvisCap>
	 */
	private Hashtable<String, AvisCap> getHashAvisCAP() {
		if (hashAvisCAP == null)
			hashAvisCAP = new Hashtable<String, AvisCap>();
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
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_AVCT(int i) {
		return "NOM_ST_NUM_AVCT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_NUM_AVCT Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_AVCT(int i) {
		return getZone(getNOM_ST_NUM_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AGENT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CATEGORIE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CATEGORIE(int i) {
		return "NOM_ST_CATEGORIE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CATEGORIE Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CATEGORIE(int i) {
		return getZone(getNOM_ST_CATEGORIE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_LIB Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_GRADE_LIB Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE(int i) {
		return "NOM_ST_GRADE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_GRADE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE(int i) {
		return getZone(getNOM_ST_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_A Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_A(int i) {
		return "NOM_ST_BM_A_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_BM_A Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_A(int i) {
		return getZone(getNOM_ST_BM_A(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_J Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_J(int i) {
		return "NOM_ST_BM_J_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_BM_J Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_J(int i) {
		return getZone(getNOM_ST_BM_J(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BM_M Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_BM_M(int i) {
		return "NOM_ST_BM_M_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_BM_M Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_BM_M(int i) {
		return getZone(getNOM_ST_BM_M(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_A Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_A(int i) {
		return "NOM_ST_ACC_A_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACC_A Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_A(int i) {
		return getZone(getNOM_ST_ACC_A(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_J Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_J(int i) {
		return "NOM_ST_ACC_J_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACC_J Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_J(int i) {
		return getZone(getNOM_ST_ACC_J(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACC_M Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACC_M(int i) {
		return "NOM_ST_ACC_M_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACC_M Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACC_M(int i) {
		return getZone(getNOM_ST_ACC_M(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PERIODE_STD Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_PERIODE_STD(int i) {
		return "NOM_ST_PERIODE_STD_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_PERIODE_STD
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_PERIODE_STD(int i) {
		return getZone(getNOM_ST_PERIODE_STD(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_AVCT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_AVCT(int i) {
		return "NOM_ST_DATE_AVCT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_AVCT Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_AVCT(int i) {
		return getZone(getNOM_ST_DATE_AVCT(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_AFFECTER Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_AFFECTER(int i) {
		return "NOM_CK_AFFECTER_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_AFFECTER Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF_AVCT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF_AVCT(int i) {
		return "NOM_ST_MOTIF_AVCT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF_AVCT(int i) {
		return getZone(getNOM_ST_MOTIF_AVCT(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE(int i) {
		return "NOM_EF_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE(int i) {
		return getZone(getNOM_EF_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_ARRETE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_NUM_ARRETE(int i) {
		return "NOM_EF_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NUM_ARRETE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_NUM_ARRETE(int i) {
		return getZone(getNOM_EF_NUM_ARRETE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ETAT Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_PROJET_ARRETE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_PROJET_ARRETE(int i) {
		return "NOM_CK_PROJET_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_PROJET_ARRETE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_PROJET_ARRETE(int i) {
		return getZone(getNOM_CK_PROJET_ARRETE(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_VALID_DRH Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_DRH(int i) {
		return "NOM_CK_VALID_DRH_" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_VALID_DRH Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_DRH(int i) {
		return getZone(getNOM_CK_VALID_DRH(i));
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_AVIS_CAP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP(int i) {
		return "NOM_LB_AVIS_CAP_" + i;
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_AVIS_CAP_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_AVIS_CAP_SELECT(int i) {
		return "NOM_LB_AVIS_CAP_" + i + "_SELECT";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_AVIS_CAP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_AVIS_CAP(int i) {
		if (LB_AVIS_CAP == null)
			LB_AVIS_CAP = initialiseLazyLB();
		return LB_AVIS_CAP;
	}

	/**
	 * Setter de la liste: LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_AVIS_CAP(String[] newLB_AVIS_CAP) {
		LB_AVIS_CAP = newLB_AVIS_CAP;
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_AVIS_CAP(int i) {
		return getLB_AVIS_CAP(i);
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_AVIS_CAP Date de cr�ation : (21/11/11 09:55:36)
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
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// on sauvegarde l'�tat du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recup�re la ligne concern�e
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(avct.getIdAvct());
			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE)) {
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
				int indiceAvisCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_SELECT(i)) ? Integer.parseInt(getVAL_LB_AVIS_CAP_SELECT(i)) : -1);
				if (indiceAvisCap != -1) {
					String idAvisCap = ((AvisCap) getListeAvisCAP().get(indiceAvisCap)).getIdAvisCAP();
					avct.setIdAvisCAP(idAvisCap);
				}
				// on traite le numero et la date d'arret�
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
			}
			avct.modifierAvancement(getTransaction());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on recupere les lignes qui sont coch�es pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recup�re la ligne concern�e
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(avct.getIdAvct());
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affect� est coch�e
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE)) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concern�
					AgentNW agentCarr = AgentNW.chercherAgent(getTransaction(), avct.getIdAgent());
					// on recupere la derniere carri�re dans l'ann�e
					Carriere carr = Carriere.chercherDerniereCarriereAvecAgentEtAnnee(getTransaction(), Integer.valueOf(agentCarr.getNoMatricule()),
							avct.getAnnee());
					// si la carriere est bien la derniere de la liste
					if (carr.getDateFin() == null || carr.getDateFin().equals("0")) {
						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());

						// on traite l'avis CAP
						int indiceAvisCap = (Services.estNumerique(getVAL_LB_AVIS_CAP_SELECT(i)) ? Integer.parseInt(getVAL_LB_AVIS_CAP_SELECT(i))
								: -1);
						if (indiceAvisCap != -1) {
							String idAvisCap = ((AvisCap) getListeAvisCAP().get(indiceAvisCap)).getIdAvisCAP();
							avct.setIdAvisCAP(idAvisCap);
						}
						// on traite le numero et la date d'arret�
						avct.setDateArrete(getVAL_EF_DATE_ARRETE(i));
						avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
						// avct.modifierAvancement(getTransaction());

						// on regarde l'avis CAP selectionn� pour determin� la
						// date de debut de carriere et la date de fin de la
						// precedente

						String libCourtAvisCap = AvisCap.chercherAvisCap(getTransaction(), avct.getIdAvisCAP()).getLibCourtAvisCAP();
						String dateAvct = Const.CHAINE_VIDE;
						if (libCourtAvisCap.toUpperCase().equals("MIN")) {
							dateAvct = avct.getDateAvctMini();
						} else if (libCourtAvisCap.toUpperCase().equals("MOY")) {
							dateAvct = avct.getDateAvctMoy();
						} else if (libCourtAvisCap.toUpperCase().equals("MAX")) {
							dateAvct = avct.getDateAvctMaxi();
						}

						// on ferme cette carriere
						carr.setDateFin(dateAvct);
						carr.modifierCarriere(getTransaction(), agentCarr, user);

						// on cr�e un nouvelle carriere
						Carriere nouvelleCarriere = new Carriere();
						nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
						nouvelleCarriere.setReferenceArrete(avct.getNumArrete());
						nouvelleCarriere.setDateArrete(avct.getDateArrete());
						nouvelleCarriere.setDateDebut(dateAvct);
						nouvelleCarriere.setDateFin(Const.ZERO);
						// on calcul Grade - ACC/BM en fonction de l'avis CAP
						// il est diff�rent du resultat affich� dans le tableau
						// si AVIS_CAP != MOY
						// car pour la simulation on prenait comme ref de calcul
						// la duree MOY
						calculAccBm(avct, carr, nouvelleCarriere, libCourtAvisCap);

						// on recupere iban du grade
						Grade gradeSuivant = Grade.chercherGrade(getTransaction(), avct.getIdNouvGrade());
						nouvelleCarriere.setIban(Services.lpad(gradeSuivant.getIban(), 7, "0"));

						// champ � remplir pour creer une carriere NB : on
						// reprend ceux de la carriere precedente
						nouvelleCarriere.setCodeBase(carr.getCodeBase());
						nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
						nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
						nouvelleCarriere.setCodeMotif(carr.getCodeMotif());
						nouvelleCarriere.setModeReglement(carr.getModeReglement());
						nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

						nouvelleCarriere.creerCarriere(getTransaction(), agentCarr, user);

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
						// liste � l'ecran
						agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " (" + agentCarr.getNoMatricule() + "); ";
					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();

		// "INF201","@ agents ont �t� affect�s."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		return true;
	}

	private void calculAccBm(AvancementFonctionnaires avct, Carriere ancienneCarriere, Carriere nouvelleCarriere, String libCourtAvisCap)
			throws Exception {
		// calcul BM/ACC applicables
		int nbJoursBM = 0;
		int nbJoursACC = 0;
		Grade gradeActuel = Grade.chercherGrade(getTransaction(), ancienneCarriere.getCodeGrade());
		if (gradeActuel.getBm().equals(Const.OUI)) {
			nbJoursBM += (Integer.parseInt(ancienneCarriere.getBMAnnee()) * 360) + (Integer.parseInt(ancienneCarriere.getBMMois()) * 30)
					+ Integer.parseInt(ancienneCarriere.getBMJour());
		}
		if (gradeActuel.getAcc().equals(Const.OUI)) {
			nbJoursACC += (Integer.parseInt(ancienneCarriere.getACCAnnee()) * 360) + (Integer.parseInt(ancienneCarriere.getACCMois()) * 30)
					+ Integer.parseInt(ancienneCarriere.getACCJour());
		}

		int nbJoursBonus = nbJoursBM + nbJoursACC;

		// Calcul date avancement au Grade actuel
		if (libCourtAvisCap.equals("Min")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
				avct.setDateAvctMini(ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee());
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMin()) * 30;
			} else {
				avct.setDateAvctMini(Services.enleveJours(
						Services.ajouteMois(Services.formateDate(ancienneCarriere.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMin())),
						nbJoursBonus));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("Moy")) {
			avct.setDureeStandard(gradeActuel.getDureeMoy());
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
				avct.setDateAvctMoy(ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee());
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
			} else {
				avct.setDateAvctMoy(Services.enleveJours(
						Services.ajouteMois(Services.formateDate(ancienneCarriere.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMoy())),
						nbJoursBonus));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("Max")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
				avct.setDateAvctMaxi(ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee());
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMax()) * 30;
			} else {
				avct.setDateAvctMaxi(Services.enleveJours(
						Services.ajouteMois(Services.formateDate(ancienneCarriere.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMax())),
						nbJoursBonus));
				nbJoursBonus = 0;
			}
		}

		// Calcul du grade suivant (BM/ACC)
		Grade gradeSuivant = Grade.chercherGrade(getTransaction(), gradeActuel.getCodeGradeSuivant());
		if (libCourtAvisCap.equals("Min")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
					&& gradeSuivant.getDureeMin() != null && gradeSuivant.getDureeMin().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMin()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			}
		} else if (libCourtAvisCap.equals("Moy")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
					&& gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			}
		} else if (libCourtAvisCap.equals("Max")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
					&& gradeSuivant.getDureeMax() != null && gradeSuivant.getDureeMax().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMax()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			}
		}

		int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
		int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

		// on met � jour les champs de l'avancement pour affichage tableau
		avct.setNouvBMAnnee(String.valueOf(nbJoursRestantsBM / 365));
		avct.setNouvBMMois(String.valueOf((nbJoursRestantsBM % 365) / 30));
		avct.setNouvBMJour(String.valueOf((nbJoursRestantsBM % 365) % 30));

		avct.setNouvACCAnnee(String.valueOf(nbJoursRestantsACC / 365));
		avct.setNouvACCMois(String.valueOf((nbJoursRestantsACC % 365) / 30));
		avct.setNouvACCJour(String.valueOf((nbJoursRestantsACC % 365) % 30));

		avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());
		// avct.setLibNouvGrade(gradeSuivant.getLibGrade());

		avct.modifierAvancement(getTransaction());

		// on met � jour les champs pour la creation de la carriere
		nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
		nouvelleCarriere.setACCAnnee(avct.getNouvACCAnnee());
		nouvelleCarriere.setACCMois(avct.getNouvACCMois());
		nouvelleCarriere.setACCJour(avct.getNouvACCJour());
		nouvelleCarriere.setBMAnnee(avct.getNouvBMAnnee());
		nouvelleCarriere.setBMMois(avct.getNouvBMMois());
		nouvelleCarriere.setBMJour(avct.getNouvBMJour());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setListeAvct(new ArrayList<AvancementFonctionnaires>());
		afficherListeAvct(request);
		return true;
	}
}