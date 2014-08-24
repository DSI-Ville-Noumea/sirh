package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementDetachesDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTSimulation Date de cr�ation : (21/11/11 11:11:24)
 * 
 */
public class OeAVCTSimulationDetaches extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_ANNEE;

	private String[] listeAnnee;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	public String focus = null;
	public String ACTION_CALCUL = "Calcul";

	public String agentEnErreur = Const.CHAINE_VIDE;
	private AutreAdministrationAgentDao autreAdministrationAgentDao;
	private AvancementDetachesDao avancementDetachesDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (21/11/11 11:11:24)
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
		initialiseListeService();

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
		if (getAutreAdministrationAgentDao() == null) {
			setAutreAdministrationAgentDao(new AutreAdministrationAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementDetachesDao() == null) {
			setAvancementDetachesDao(new AvancementDetachesDao((SirhDao) context.getBean("sirhDao")));
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
	 * Initialise les listes d�roulantes de l'�cran.
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

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (21/11/11 11:11:24)
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
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTSimulation. Date de cr�ation : (21/11/11
	 * 11:11:24)
	 * 
	 */
	public OeAVCTSimulationDetaches() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (21/11/11 11:11:24)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTSimulationDetaches.jsp";
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
		getAvancementDetachesDao().supprimerAvancementTravailAvecCategorie(Integer.valueOf(an));

		// recuperation agent
		Agent agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT()));
		}

		if (!performCalculDetache(getVAL_ST_CODE_SERVICE(), an, agent))
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
	private boolean performCalculDetache(String codeService, String annee, Agent agent) throws Exception {
		ArrayList<Agent> la = new ArrayList<Agent>();
		if (agent != null) {
			// il faut regarder si cet agent est de type Fonctionnaire d�tach�
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carr == null
					|| carr.getCodeCategorie() == null
					|| (!carr.getCodeCategorie().equals("6") && !carr.getCodeCategorie().equals("16")
							&& !carr.getCodeCategorie().equals("17") && !carr.getCodeCategorie().equals("19"))) {
				// "ERR181",
				// "Cet agent n'est pas de type @. Il ne peut pas �tre soumis � l'avancement @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR181", "d�tach�", "des d�tach�s"));
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
			ArrayList<Carriere> listeCarriereActive = Carriere.listerCarriereActive(getTransaction(), annee, "Detache");
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

			// R�cup�ration de l'avancement
			try {
				@SuppressWarnings("unused")
				AvancementDetaches avct = getAvancementDetachesDao().chercherAvancementAvecAnneeEtAgent(
						Integer.valueOf(annee), a.getIdAgent());
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
				// Si pas de grade suivant, agent non �ligible
				if (gradeActuel.getCodeGradeSuivant() != null && gradeActuel.getCodeGradeSuivant().length() != 0) {
					// Cr�ation de l'avancement
					AvancementDetaches avct = new AvancementDetaches();
					avct.setIdAgent(a.getIdAgent());
					avct.setCodeCategorie(Integer.valueOf(carr.getCodeCategorie()));
					avct.setAnnee(Integer.valueOf(annee));
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());

					// PA
					avct.setCodePa(paAgent.getCdpadm());

					// on traite si l'agent est detach� ou non
					if (paAgent.getCdpadm().equals("54") || paAgent.getCdpadm().equals("56")
							|| paAgent.getCdpadm().equals("57") || paAgent.getCdpadm().equals("58")) {
						avct.setAgentVdn(false);
					} else {
						avct.setAgentVdn(true);
					}
					// BM/ACC
					avct.setNouvBmAnnee(Integer.valueOf(carr.getBMAnnee()));
					avct.setNouvBmMois(Integer.valueOf(carr.getBMMois()));
					avct.setNouvBmJour(Integer.valueOf(carr.getBMJour()));
					avct.setNouvAccAnnee(Integer.valueOf(carr.getACCAnnee()));
					avct.setNouvAccMois(Integer.valueOf(carr.getACCMois()));
					avct.setNouvAccJour(Integer.valueOf(carr.getACCJour()));

					// calcul BM/ACC applicables
					int nbJoursBM = AvancementDetaches.calculJourBM(gradeActuel, carr);
					int nbJoursACC = AvancementDetaches.calculJourACC(gradeActuel, carr);

					int nbJoursBonusDepart = nbJoursBM + nbJoursACC;
					int nbJoursBonus = nbJoursBM + nbJoursACC;
					// Calcul date avancement au Grade actuel
					if (gradeActuel.getDureeMoy() != null && gradeActuel.getDureeMoy().length() != 0) {
						avct.setPeriodeStandard(Integer.valueOf(gradeActuel.getDureeMoy()));
						if (nbJoursBonusDepart > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
							String dateAvct = carr.getDateDebut().substring(0, 6) + annee;
							avct.setDateAvctMoy(sdf.parse(dateAvct));
							nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
						} else {
							avct.setDateAvctMoy(AvancementDetaches.calculDateAvctMoy(gradeActuel, carr));
							nbJoursBonus = 0;
						}
					}
					// si la date avct moy (ann�e ) sup � l'ann�e choisie pour
					// la simu alors on sort l'agent du calcul
					Integer anneeNumerique = avct.getAnnee();
					Integer anneeDateAvctMoyNumerique = Integer.valueOf(sdf.format(avct.getDateAvctMoy()).substring(6,
							sdf.format(avct.getDateAvctMoy()).length()));
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
							gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
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
								avct.setIdMotifAvct(Integer.valueOf(grd.getCodeTava()));
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
					avct.setDateGrade(sdf.parse(carr.getDateDebut()));
					avct.setBmAnnee(Integer.valueOf(carr.getBMAnnee()));
					avct.setBmMois(Integer.valueOf(carr.getBMMois()));
					avct.setBmJour(Integer.valueOf(carr.getBMJour()));
					avct.setAccAnnee(Integer.valueOf(carr.getACCAnnee()));
					avct.setAccMois(Integer.valueOf(carr.getACCMois()));
					avct.setAccJour(Integer.valueOf(carr.getACCJour()));

					// on regarde si l'agent a une carriere de simulation dej�
					// saisie
					// autrement dis si la carriere actuelle a pour datfin 0
					if (carr.getDateFin() == null || carr.getDateFin().equals(Const.ZERO)) {
						avct.setCarriereSimu(null);
					} else {
						avct.setCarriereSimu("S");
					}

					avct.setDateVerifSef(null);
					avct.setDateVerifSgc(null);
					getAvancementDetachesDao().creerAvancement(avct.getIdAgent(), avct.getIdMotifAvct(),
							avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(), avct.getGrade(),
							avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(),
							avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(),
							avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
							avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(),
							avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
							avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMoy(), avct.getNumArrete(),
							avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
							avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(),
							avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
							avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(),
							avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
							avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getCodePa());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
				} else {
					// on informe les agents en erreur
					agentEnErreur += a.getNomAgent() + " " + a.getPrenomAgent() + " (" + a.getNomatr() + "); ";
				}
			}
		}
		return true;
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
	 *            focus � d�finir.
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
	 * Getter du nom de l'�cran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-SIMULATION-DETACHE";
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
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

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

	public AutreAdministrationAgentDao getAutreAdministrationAgentDao() {
		return autreAdministrationAgentDao;
	}

	public void setAutreAdministrationAgentDao(AutreAdministrationAgentDao autreAdministrationAgentDao) {
		this.autreAdministrationAgentDao = autreAdministrationAgentDao;
	}

	public AvancementDetachesDao getAvancementDetachesDao() {
		return avancementDetachesDao;
	}

	public void setAvancementDetachesDao(AvancementDetachesDao avancementDetachesDao) {
		this.avancementDetachesDao = avancementDetachesDao;
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
