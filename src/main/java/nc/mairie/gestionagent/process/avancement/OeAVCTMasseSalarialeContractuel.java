package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
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
public class OeAVCTMasseSalarialeContractuel extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(OeAVCTMasseSalarialeContractuel.class);

	private String[] LB_ANNEE;

	private String[] listeAnnee;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private ArrayList<AvancementContractuels> listeAvct;
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
	public OeAVCTMasseSalarialeContractuel() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTMasseSalarialeContractuel.jsp";
	}

	/**
	 * Getter du nom de l'�cran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-SIMU-MASSE-CONTR";
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

		// Suppression des avancements � l'�tat 'Travail' de l'ann�e
		AvancementContractuels.supprimerAvancementContractuelsTravailAvecAnnee(getTransaction(), an);

		// recuperation agent
		AgentNW agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT());
		}

		if (!performCalculContractuel(getVAL_ST_CODE_SERVICE(), an, agent))
			return false;

		commitTransaction();
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT, an);

		// "INF200","Simulation effectu�e"
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF200"));

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

	private boolean performCalculContractuel(String codeService, String annee, AgentNW agent) throws Exception {
		ArrayList<AgentNW> la = new ArrayList<AgentNW>();
		if (agent != null) {
			// il faut regarder si cet agent est de type Convention Collective
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carr == null || carr.getCodeCategorie() == null || !carr.getCodeCategorie().equals("4")) {
				// "ERR181",
				// "Cet agent n'est pas de type @. Il ne peut pas �tre soumis � l'avancement @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR181", "contractuel", "des contractuels"));
				return false;
			}
			la.add(agent);
		} else {
			ArrayList<String> listeSousService = null;
			if (!codeService.equals(Const.CHAINE_VIDE)) {
				Service serv = Service.chercherService(getTransaction(), codeService);
				listeSousService = Service.listSousService(getTransaction(), serv.getSigleService());
			}

			// R�cup�ration des agents
			ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(getTransaction(), annee,
					"Contractuel");
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
		for (AgentNW a : la) {
			// Recuperation de la carriere en cours
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), a);
			if (getTransaction().isErreur() || carr == null || carr.getDateDebut() == null) {
				getTransaction().traiterErreur();
				continue;
			}
			PositionAdmAgent paAgent = PositionAdmAgent.chercherPositionAdmAgentDateComprise(getTransaction(),
					a.getNoMatricule(),
					Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE));
			if (getTransaction().isErreur() || paAgent == null || paAgent.getCdpadm() == null
					|| paAgent.estPAInactive(getTransaction()) || paAgent.estEnDispo(getTransaction())) {
				getTransaction().traiterErreur();
				continue;
			}
			// R�cup�ration du CDCADR de SPGRADN
			// Grade grade = Grade.chercherGrade(getTransaction(),
			// carr.getCodeGrade());
			// L'agent doit avoir la date d�but de la nouvelle carriere comprise
			// dans l'ann�e d'avancement
			if (Services.compareDates(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2), "01/01/"
					+ annee) >= 0
					&& Services.compareDates(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2),
							"31/12/" + annee) <= 0) {
				// R�cup�ration de l'avancement
				AvancementContractuels avct = AvancementContractuels.chercherAvancementContractuelsAvecAnneeEtAgent(
						getTransaction(), annee, a.getIdAgent());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// Cr�ation de l'avancement
					avct = new AvancementContractuels();
					avct.setIdAgent(a.getIdAgent());
					avct.setDateEmbauche(a.getDateDerniereEmbauche());
					avct.setAnnee(annee);
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

					PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), paAgent.getCdpadm());
					avct.setPa(pa.getLiPAdm());

					// on recupere le grade du poste
					Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), a.getIdAgent());
					if (aff.getIdFichePoste() == null) {
						continue;
					}
					FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
					avct.setNumFP(fp.getNumFP());
					// on cherche � quelle categorie appartient l'agent
					// (A,B,A+..;)
					Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
					GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(),
							g.getCodeGradeGenerique());
					Bareme bareme = Bareme.chercherBareme(getTransaction(), carr.getIban());
					// on recupere les points pour cette categorie (A,B,A+..)
					if (gg.getCodCadre() == null || gg.getCodCadre().equals(Const.CHAINE_VIDE)) {
						continue;
					}
					avct.setCodeCadre(gg.getCodCadre());
					// on calcul le nouvel INM
					String nouvINM = String.valueOf(Integer.valueOf(bareme.getInm())
							+ Integer.valueOf(gg.getNbPointsAvct()));
					// avec ce nouvel INM on recupere l'iban et l'ina
					// correspondant
					Bareme nouvBareme = (Bareme) Bareme.listerBaremeByINM(getTransaction(), nouvINM).get(0);
					// on rempli les champs
					avct.setNouvIBAN(nouvBareme.getIban());
					avct.setNouvINM(nouvBareme.getInm());
					avct.setNouvINA(nouvBareme.getIna());
					avct.setDateProchainGrade(Services.ajouteAnnee(Services.formateDate(carr.getDateDebut()), 2));

					avct.setDateArrete("01/01/" + annee);
					avct.setNumArrete(annee);

					Service direction = Service.getDirection(getTransaction(), fp.getIdServi());
					Service section = Service.getSection(getTransaction(), fp.getIdServi());

					avct.setDirectionService(direction == null ? Const.CHAINE_VIDE : direction.getSigleService());
					avct.setSectionService(section == null ? Const.CHAINE_VIDE : section.getSigleService());
					avct.setDateGrade(carr.getDateDebut());
					avct.setIban(carr.getIban());
					avct.setInm(bareme.getInm());
					avct.setIna(bareme.getIna());

					// on regarde si l'agent a une carriere de simulation dej�
					// saisie
					// autrement dis si la carriere actuelle a pour datfin 0
					if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
						avct.setCarriereSimu(null);
					} else {
						avct.setCarriereSimu("S");
					}
					avct.creerAvancementContractuels(getTransaction());
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
		setListeAvct(AvancementContractuels.listerAvancementContractuelsAnnee(getTransaction(), annee));

		afficherListeAvct(request);

		return true;
	}

	private void afficherListeAvct(HttpServletRequest request) throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementContractuels av = (AvancementContractuels) getListeAvct().get(j);
			Integer i = Integer.valueOf(av.getIdAvct());
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), av.getIdAgent());
			FichePoste fp = FichePoste.chercherFichePosteAvecNumeroFP(getTransaction(), av.getNumFP());
			TitrePoste tp = null;
			if (fp != null && fp.getIdTitrePoste() != null) {
				tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
			}

			addZone(getNOM_ST_MATRICULE(i), agent.getNoMatricule());
			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
			addZone(getNOM_ST_DATE_EMBAUCHE(i), av.getDateEmbauche());
			addZone(getNOM_ST_FP(i), av.getNumFP() + " <br> " + (tp == null ? "&nbsp;" : tp.getLibTitrePoste()));
			addZone(getNOM_ST_PA(i), av.getPa());
			addZone(getNOM_ST_CATEGORIE(i), av.getCodeCadre());
			addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct());
			addZone(getNOM_ST_DATE_DEBUT_IBA(i), av.getDateGrade() + " <br> " + av.getDateProchainGrade());
			addZone(getNOM_ST_IBA(i), av.getIban() + " <br> " + av.getNouvIBAN());
			addZone(getNOM_ST_INM(i), av.getInm() + " <br> " + av.getNouvINM());
			addZone(getNOM_ST_INA(i), av.getIna() + " <br> " + av.getNouvINA());

			addZone(getNOM_CK_VALID_DRH(i),
					av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_ST_MOTIF_AVCT(i), "REVALORISATION");
			addZone(getNOM_CK_PROJET_ARRETE(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())
					|| av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
			addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_EF_DATE_ARRETE(i),
					av.getDateArrete().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : av.getDateArrete());
			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue())
					|| av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
		}
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
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_EMBAUCHE Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_EMBAUCHE(int i) {
		return "NOM_ST_DATE_EMBAUCHE_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_EMBAUCHE
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_EMBAUCHE(int i) {
		return getZone(getNOM_ST_DATE_EMBAUCHE(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_FP Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_FP(int i) {
		return "NOM_ST_FP_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_FP Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_FP(int i) {
		return getZone(getNOM_ST_FP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PA Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_PA(int i) {
		return "NOM_ST_PA_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_PA Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_PA(int i) {
		return getZone(getNOM_ST_PA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT_IBA Date
	 * de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT_IBA(int i) {
		return "NOM_ST_DATE_DEBUT_IBA_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_DEBUT_IBA
	 * Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT_IBA(int i) {
		return getZone(getNOM_ST_DATE_DEBUT_IBA(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_IBA Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_IBA(int i) {
		return "NOM_ST_IBA_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_IBA Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_IBA(int i) {
		return getZone(getNOM_ST_IBA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INM Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_INM(int i) {
		return "NOM_ST_INM_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_INM Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_INM(int i) {
		return getZone(getNOM_ST_INM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INA Date de cr�ation
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_INA(int i) {
		return "NOM_ST_INA_" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_INA Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_INA(int i) {
		return getZone(getNOM_ST_INA(i));
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
			AvancementContractuels avct = (AvancementContractuels) getListeAvct().get(j);
			Integer i = Integer.valueOf(avct.getIdAvct());
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
				// on traite le numero et la date d'arret�
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
			}
			avct.modifierAvancementContractuels(getTransaction());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		return true;
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
			AvancementContractuels avct = (AvancementContractuels) getListeAvct().get(j);
			Integer i = Integer.valueOf(avct.getIdAvct());
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affect� est coch�e
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concern�
					AgentNW agentCarr = AgentNW.chercherAgent(getTransaction(), avct.getIdAgent());
					// on recupere la derniere carri�re dans l'ann�e
					Carriere carr = Carriere.chercherDerniereCarriereAvecAgentEtAnnee(getTransaction(),
							Integer.valueOf(agentCarr.getNoMatricule()), avct.getAnnee());
					// si la carriere est bien la derniere de la liste
					if (carr.getDateFin() == null || carr.getDateFin().equals("0")) {
						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());
						// on traite le numero et la date d'arret�
						avct.setDateArrete(getVAL_EF_DATE_ARRETE(i));
						avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
						avct.modifierAvancementContractuels(getTransaction());

						// on ferme cette carriere
						carr.setDateFin(avct.getDateProchainGrade());
						carr.modifierCarriere(getTransaction(), agentCarr, user);

						// on cr�e un nouvelle carriere
						Carriere nouvelleCarriere = new Carriere();
						nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
						nouvelleCarriere.setReferenceArrete(avct.getNumArrete().equals(Const.CHAINE_VIDE) ? Const.ZERO
								: avct.getNumArrete());
						nouvelleCarriere.setDateArrete(avct.getDateArrete());
						nouvelleCarriere.setDateDebut(avct.getDateProchainGrade());
						nouvelleCarriere.setDateFin(Const.ZERO);
						nouvelleCarriere.setIban(avct.getNouvIBAN());
						// champ � remplir pour creer une carriere NB : on
						// reprend ceux de la carriere precedente
						nouvelleCarriere.setCodeBase(carr.getCodeBase());
						nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
						nouvelleCarriere.setCodeGrade(carr.getCodeGrade());
						nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
						nouvelleCarriere.setCodeMotif(carr.getCodeMotif());
						nouvelleCarriere.setModeReglement(carr.getModeReglement());
						nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

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
						// liste � l'ecran
						agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " ("
								+ agentCarr.getNoMatricule() + "); ";
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

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de cr�ation :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
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
}