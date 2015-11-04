package nc.mairie.gestionagent.process.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementContractuels;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Bareme;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.metier.carriere.ModeReglement;
import nc.mairie.metier.carriere.StatutCarriere;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.parametrage.MotifCarriere;
import nc.mairie.metier.paye.Matricule;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.referentiel.TypeContrat;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.agent.ContratDao;
import nc.mairie.spring.dao.metier.carriere.HistoCarriereDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.parametrage.MotifCarriereDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.referentiel.AvisCapDao;
import nc.mairie.spring.dao.metier.referentiel.TypeContratDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IAvancementService;
import nc.noumea.spring.service.ISirhService;

import org.springframework.context.ApplicationContext;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Process OeAGENTCarriere Date de création : (05/09/11 11:31:37)
 * 
 */
public class OeAGENTCarriere extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	public static final int STATUT_ACTION = 2;

	private String[] LB_BASE_HORAIRE;
	private String[] LB_BASE_REGLEMENT;
	private String[] LB_REGIMES;
	private String[] LB_STATUTS;

	private Agent agentCourant;
	private Carriere carriereCourante;
	private ArrayList<Carriere> listeCarriere;
	private ArrayList<Grade> listeGrade;
	private ArrayList<StatutCarriere> listeStatut;
	private ArrayList<Horaire> listeHoraire;
	private ArrayList<String> listeRegime;
	private ArrayList<ModeReglement> listeBaseReg;
	private ArrayList<MotifCarriere> listeMotifCarriere;

	private Hashtable<String, StatutCarriere> hashStatut;
	private Hashtable<String, Grade> hashGrade;
	private Hashtable<String, Horaire> hashHoraire;
	private Hashtable<String, ModeReglement> hashModeReglement;
	private Hashtable<String, MotifCarriere> hashMotifCarriere;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche Carrière.";
	public String ACTION_VISUALISATION = "Consultation d'une fiche Carrière.";
	public String ACTION_MODIFICATION = "Modification d'une fiche Carrière.";
	public String ACTION_CREATION = "Création d'une fiche Carrière.";
	public String ACTION_REOUVERTURE = "Suppression d'une fiche Carrière. Réouverture carrière.";
	public String ACTION_AVCT_PREV = "Avancement prévisionnel.";
	public String ACTION_AVCT_PREV_CC = "Avancement prévisionnel des conventions collectives.";

	public boolean gradeObligatoire = false;
	public boolean showIBA = false;
	public boolean IBAEditable = false;
	public boolean showDateFin = false;
	public boolean showAccBM = false;

	private static QSYSObjectPathName CALC_PATH = new QSYSObjectPathName((String) ServletAgent.getMesParametres().get("DTAARA_SCHEMA"), (String) ServletAgent.getMesParametres().get("DTAARA_NAME"),
			"DTAARA");
	public static CharacterDataArea DTAARA_CALC = new CharacterDataArea(new AS400((String) ServletAgent.getMesParametres().get("HOST_SGBD_PAYE"), (String) ServletAgent.getMesParametres().get(
			"HOST_SGBD_ADMIN"), (String) ServletAgent.getMesParametres().get("HOST_SGBD_PWD")), CALC_PATH.getPath());
	private String calculPaye;

	private MotifAvancementDao motifAvancementDao;
	private AvisCapDao avisCapDao;
	private MotifCarriereDao motifCarriereDao;
	private TypeContratDao typeContratDao;
	private ContratDao contratDao;
	private FichePosteDao fichePosteDao;
	private HistoCarriereDao histoCarriereDao;
	private AffectationDao affectationDao;
	private AutreAdministrationAgentDao autreAdministrationAgentDao;

	private ISirhService sirhService;

	private IAvancementService avancementService;

	private IAdsService adsService;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Constructeur du process OeAGENTCarriere. Date de création : (05/09/11
	 * 11:39:24)
	 * 
	 */
	public OeAGENTCarriere() {
		super();
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (05/09/11 11:39:24)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'accès a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// SI CALCUL PAYE EN COURS
		String percou = DTAARA_CALC.read().toString();
		if (!percou.trim().equals(Const.CHAINE_VIDE)) {
			setCalculPaye(percou);
		} else {
			setCalculPaye(Const.CHAINE_VIDE);
		}

		initialiseListeDeroulante();

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeCarrieres(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getMotifAvancementDao() == null) {
			setMotifAvancementDao(new MotifAvancementDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getMotifCarriereDao() == null) {
			setMotifCarriereDao(new MotifCarriereDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeContratDao() == null) {
			setTypeContratDao(new TypeContratDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getContratDao() == null) {
			setContratDao(new ContratDao((SirhDao) context.getBean("sirhDao")));
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
		if (getAutreAdministrationAgentDao() == null) {
			setAutreAdministrationAgentDao(new AutreAdministrationAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvisCapDao() == null) {
			setAvisCapDao(new AvisCapDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (null == avancementService) {
			avancementService = (IAvancementService) context.getBean("avancementService");
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
	}

	/**
	 * fonction qui permet de savoir si une paye est en cours.
	 * 
	 * @return calculPaye
	 */
	public String getCalculPaye() {
		return calculPaye;
	}

	private void setCalculPaye(String calculPaye) {
		this.calculPaye = calculPaye;
	}

	/**
	 * Initialise les listes deroulantes de l'écran. Date de création :
	 * (16/09/11)
	 * 
	 * 
	 * @throws Exception
	 *             RG_AG_CA_C01 RG_AG_CA_C03 RG_AG_CA_C11 RG_AG_CA_C02
	 *             RG_AG_CA_C06 RG_AG_CA_C05 RG_AG_CA_C07 RG_AG_CA_C10
	 *             RG_AG_CA_C12
	 */
	private void initialiseListeDeroulante() throws Exception {

		// Si hashtable des statuts vide
		// RG_AG_CA_C01
		if (getHashStatut().size() == 0) {
			ArrayList<StatutCarriere> listeStatut = StatutCarriere.listerStatutCarriere(getTransaction());
			setListeStatut(listeStatut);

			int[] tailles = { 2, 30 };
			String[] champs = { "cdCate", "liCate" };
			setLB_STATUTS(new FormateListe(tailles, listeStatut, champs).getListeFormatee(true));

			// remplissage de la hashTable
			for (StatutCarriere sc : listeStatut)
				getHashStatut().put(sc.getCdCate(), sc);
		}

		// Si liste grade vide alors affectation
		// RG_AG_CA_C03
		// RG_AG_CA_C11
		// RG_AG_CA_C02
		if (getListeGrade() == null) {
			ArrayList<Grade> listeGrade = Grade.listerGradeActif(getTransaction());
			setListeGrade(listeGrade);
			afficherListeGrade();
		}

		// Si liste base horaire vide alors affectation
		// RG_AG_CA_C06
		if (getLB_BASE_HORAIRE() == LBVide) {
			// ArrayList<Horaire> liste =
			// Horaire.listerHoraire(getTransaction());
			ArrayList<Horaire> liste = Horaire.listerHoraireSansNul(getTransaction());
			setListeHoraire(liste);

			int[] tailles = { 30 };
			String[] champs = { "libHor" };
			setLB_BASE_HORAIRE(new FormateListe(tailles, liste, champs).getListeFormatee());

			for (Horaire h : liste)
				getHashHoraire().put(h.getCdtHor(), h);
		}

		// Si liste regimes vide alors affectation
		// RG_AG_CA_C05
		if (getLB_REGIMES() == LBVide) {
			String[] list = { "1", "2", "3" };

			ArrayList<String> arrayList = new ArrayList<String>();

			for (String regime : list)
				arrayList.add(regime);

			setListeRegime(arrayList);

			setLB_REGIMES(list);
		}

		// Si liste base reg vide alors affectation
		// RG_AG_CA_C07
		if (getLB_BASE_REGLEMENT() == LBVide) {
			ArrayList<ModeReglement> liste = ModeReglement.listerModeReglement(getTransaction());
			setListeBaseReg(liste);

			int[] tailles = { 1, 15 };
			String[] champs = { "modReg", "libModReg" };
			setLB_BASE_REGLEMENT(new FormateListe(tailles, liste, champs).getListeFormatee());

			for (ModeReglement m : liste)
				getHashModeReglement().put(m.getModReg(), m);
		}

		// Si liste motifs vide alors affectation
		// RG_AG_CA_C10
		if (getLB_MOTIFS() == LBVide) {

			ArrayList<MotifCarriere> liste = getMotifCarriereDao().listerMotifCarriere();
			setListeMotifCarriere(liste);

			if (getListeMotifCarriere().size() != 0) {
				int[] tailles = { 50 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<MotifCarriere> list = getListeMotifCarriere().listIterator(); list.hasNext();) {
					MotifCarriere de = (MotifCarriere) list.next();
					String ligne[] = { de.getLibMotifCarriere() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_MOTIFS(aFormat.getListeFormatee(true));
			} else {
				setLB_MOTIFS(null);
			}

			for (MotifCarriere m : liste)
				getHashMotifCarriere().put(m.getIdMotifCarriere().toString(), m);

		}
	}

	private void afficherListeGrade() {
		if (getListeGrade().size() != 0) {
			int tailles[] = { 4, 50, 100 };
			String padding[] = { "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<Grade> list = getListeGrade().listIterator(); list.hasNext();) {
				Grade grade = (Grade) list.next();
				String iban = Const.CHAINE_VIDE;
				if (Services.estNumerique(grade.getIban())) {
					iban = Services.lpad(grade.getIban(), 7, "0");
				} else {
					iban = grade.getIban();
				}
				String ligne[] = { grade.getCodeGrade(), grade.getLibGrade(), iban };
				aFormat.ajouteLigne(ligne);
			}
		}
		for (Grade g : getListeGrade()) {
			getHashGrade().put(g.getCodeGrade(), g);
		}

	}

	/**
	 * Initialisation de la liste des carrieres de l'agent courant Date de
	 * création : (05/09/11)
	 */
	private void initialiseListeCarrieres(HttpServletRequest request) throws Exception {
		// Recherche des carrieres de l'agent
		ArrayList<Carriere> listeCarriere = Carriere.listerCarriereAvecAgent(getTransaction(), getAgentCourant());
		setListeCarriere(listeCarriere);

		int indiceCarr = 0;
		if (getListeCarriere() != null) {
			for (int i = 0; i < getListeCarriere().size(); i++) {
				Carriere carriere = (Carriere) getListeCarriere().get(i);
				Bareme bareme = null;
				if (carriere.getIban() != null && !carriere.getIban().equals(Const.CHAINE_VIDE)) {
					bareme = Bareme.chercherBareme(getTransaction(), carriere.getIban());
					if (getTransaction().isErreur())
						getTransaction().traiterErreur();
				}

				Horaire horaire = Horaire.chercherHoraire(getTransaction(), carriere.getCodeBaseHoraire2());
				StatutCarriere statut = StatutCarriere.chercherStatutCarriere(getTransaction(), carriere.getCodeCategorie());
				Float taux = Float.parseFloat(horaire.getCdTaux()) * 100;

				addZone(getNOM_ST_GRADE(indiceCarr), carriere.getCodeGrade().equals(Const.CHAINE_VIDE) ? "&nbsp;" : carriere.getCodeGrade());
				addZone(getNOM_ST_TYPE_CONTRAT(indiceCarr), carriere.getTypeContrat().equals(Const.CHAINE_VIDE) ? "&nbsp;" : carriere.getTypeContrat());
				addZone(getNOM_ST_BASE_HORAIRE(indiceCarr), String.valueOf(taux.intValue()) + "%");
				addZone(getNOM_ST_IBA(indiceCarr), bareme != null && bareme.getIban() != null ? bareme.getIban() : "&nbsp;");
				addZone(getNOM_ST_INA(indiceCarr), bareme != null && bareme.getIban() != null ? bareme.getIna() : "&nbsp;");
				addZone(getNOM_ST_INM(indiceCarr), bareme != null && bareme.getIban() != null ? bareme.getInm() : "&nbsp;");
				addZone(getNOM_ST_DEBUT(indiceCarr), carriere.getDateDebut());
				addZone(getNOM_ST_FIN(indiceCarr), carriere.getDateFin() == null || carriere.getDateFin().equals(Const.DATE_NULL) ? "&nbsp;" : carriere.getDateFin());
				addZone(getNOM_ST_REF_ARR(indiceCarr), carriere.getReferenceArrete());
				addZone(getNOM_ST_STATUT(indiceCarr), statut.getLiCate());

				indiceCarr++;
			}
		}
	}

	/**
	 * Réinitilise les champs du formulaire de création/modification d'une
	 * carriere
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {
		addZone(getNOM_LB_STATUTS_SELECT(), Const.ZERO);
		addZone(getNOM_LB_REGIMES_SELECT(), Const.ZERO);
		addZone(getNOM_LB_BASE_HORAIRE_SELECT(), Const.ZERO);

		addZone(getNOM_LB_BASE_REGLEMENT_SELECT(), Const.ZERO);
		for (int i = 0; i < getListeBaseReg().size(); i++)
			if (getListeBaseReg().get(i).getModReg().equals("I")) {
				addZone(getNOM_LB_BASE_REGLEMENT_SELECT(), String.valueOf(i));
				break;
			}

		addZone(getNOM_LB_MOTIFS_SELECT(), Const.ZERO);

		addZone(getNOM_ST_FILIERE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_IBA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ACC_JOURS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ACC_MOIS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_ACC_ANNEES(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BM_JOURS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BM_MOIS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BM_ANNEES(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_REF_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_ARR(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_CDICDD(), Const.CHAINE_VIDE);

		// Contrat contrat = Contrat.chercherContratCourant(getTransaction(),
		// getAgentCourant());
		//
		// if (contrat != null) {
		// TypeContrat typeContrat =
		// TypeContrat.chercherTypeContrat(getTransaction(),
		// contrat.getIdTypeContrat());
		// addZone(getNOM_ST_CDICDD(), typeContrat.getLibTypeContrat());
		// } else
		// addZone(getNOM_ST_CDICDD(), Const.CHAINE_VIDE);

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_CA_A01 RG_AG_CA_A02
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		Carriere derniereCarriere = getDerniereCarriere();
		// RG_AG_CA_A01
		if (derniereCarriere != null && derniereCarriere.getCodeCategorie().equals("8") && derniereCarriere.isActive()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR131"));
			return false;
		}

		// si au moins une PA active
		ArrayList<PositionAdmAgent> listePA = PositionAdmAgent.listerPositionAdmAgentAvecAgent(getTransaction(), getAgentCourant());
		boolean uneEstActive = false;
		for (int i = 0; i < listePA.size(); i++) {
			PositionAdmAgent posAdm = (PositionAdmAgent) listePA.get(i);
			if (!posAdm.estPAInactive(getTransaction())) {
				uneEstActive = true;
				break;
			}
		}
		// RG_AG_CA_A02
		if (!uneEstActive) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR132"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		setCarriereCourante(null);
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		return true;
	}

	/**
	 * Initilise les zones de la carriere courante Date de création : 20/09/2011
	 * 
	 */
	private boolean initialiseCarriereCourante(HttpServletRequest request) throws Exception {
		videZonesDeSaisie(request);

		StatutCarriere statut = (StatutCarriere) getHashStatut().get(getCarriereCourante().getCodeCategorie());
		Grade grade = (Grade) getHashGrade().get(getCarriereCourante().getCodeGrade());
		Horaire horaire = (Horaire) getHashHoraire().get(getCarriereCourante().getCodeBaseHoraire2());
		ModeReglement baseReglement = (ModeReglement) getHashModeReglement().get(getCarriereCourante().getModeReglement());
		MotifCarriere motif = (MotifCarriere) getHashMotifCarriere().get(getCarriereCourante().getIdMotif());

		// Alim zones
		if (statut != null) {
			int ligneStatut = getListeStatut().indexOf(statut);
			addZone(getNOM_LB_STATUTS_SELECT(), String.valueOf(ligneStatut + 1));
			addZone(getNOM_ST_STATUT(), statut.getLiCate());
			initialiseChampObligatoire(statut);
		}

		if (grade != null) {
			String iban = Const.CHAINE_VIDE;
			if (Services.estNumerique(grade.getIban())) {
				iban = Services.lpad(grade.getIban(), 7, "0");
			} else {
				iban = grade.getIban();
			}
			addZone(getNOM_ST_GRADE(), grade.getLibGrade());
			addZone(getNOM_EF_GRADE(), grade.getCodeGrade() + " " + grade.getLibGrade() + " " + iban);
		}

		int ligneRegime = getListeRegime().indexOf(getCarriereCourante().getCodeTypeEmploi());
		addZone(getNOM_LB_REGIMES_SELECT(), String.valueOf(ligneRegime));
		addZone(getNOM_ST_REGIME(), getCarriereCourante().getCodeTypeEmploi());

		if (horaire != null) {
			int ligneHoraire = getListeHoraire().indexOf(horaire);
			addZone(getNOM_LB_BASE_HORAIRE_SELECT(), String.valueOf(ligneHoraire));
			addZone(getNOM_ST_HORAIRE(), horaire.getLibHor());
		}

		if (baseReglement != null) {
			int ligneBaseReg = getListeBaseReg().indexOf(baseReglement);
			addZone(getNOM_LB_BASE_REGLEMENT_SELECT(), String.valueOf(ligneBaseReg));
			addZone(getNOM_ST_REGLEMENT(), baseReglement.getLibModReg());
		}

		if (motif != null) {
			int ligneMotif = getListeMotifCarriere().indexOf(motif);
			addZone(getNOM_LB_MOTIFS_SELECT(), String.valueOf(ligneMotif + 1));
			addZone(getNOM_ST_MOTIF(), motif.getLibMotifCarriere());
		} else {
			addZone(getNOM_LB_MOTIFS_SELECT(), Const.ZERO);
			addZone(getNOM_ST_MOTIF(), Const.CHAINE_VIDE);
		}

		addZone(getNOM_EF_DATE_DEBUT(), getCarriereCourante().getDateDebut());
		addZone(getNOM_EF_DATE_FIN(), getCarriereCourante().getDateFin() == null || getCarriereCourante().getDateFin().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : getCarriereCourante().getDateFin());
		addZone(getNOM_EF_DATE_ARR(), getCarriereCourante().getDateArrete() == null || getCarriereCourante().getDateArrete().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : getCarriereCourante()
				.getDateArrete());

		Bareme bareme = null;
		if (!getCarriereCourante().getIban().equals(Const.CHAINE_VIDE)) {
			bareme = Bareme.chercherBareme(getTransaction(), getCarriereCourante().getIban());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
		}

		addZone(getNOM_EF_IBA(), bareme != null && bareme.getIban() != null ? bareme.getIban() : Const.CHAINE_VIDE);
		addZone(getNOM_ST_INA(), bareme != null && bareme.getIban() != null ? bareme.getIna() : Const.CHAINE_VIDE);
		addZone(getNOM_ST_INM(), bareme != null && bareme.getIban() != null ? bareme.getInm() : Const.CHAINE_VIDE);

		addZone(getNOM_EF_ACC_JOURS(), getCarriereCourante().getACCJour());
		addZone(getNOM_EF_ACC_MOIS(), getCarriereCourante().getACCMois());
		addZone(getNOM_EF_ACC_ANNEES(), getCarriereCourante().getACCAnnee());
		addZone(getNOM_EF_BM_JOURS(), getCarriereCourante().getBMJour());
		addZone(getNOM_EF_BM_MOIS(), getCarriereCourante().getBMMois());
		addZone(getNOM_EF_BM_ANNEES(), getCarriereCourante().getBMAnnee());

		if (getCarriereCourante().getTypeContrat().equals(Const.CHAINE_VIDE)) {
			if (!getVAL_EF_DATE_DEBUT().equals(Const.CHAINE_VIDE) && Services.estUneDate(getVAL_EF_DATE_DEBUT())) {
				Contrat contrat = null;
				try {
					contrat = getContratDao().chercherContratAgentDateComprise(getAgentCourant().getIdAgent(), sdf.parse(getVAL_EF_DATE_DEBUT()));
				} catch (Exception e) {
					// pas de contrat
				}
				if (contrat != null && contrat.getIdTypeContrat() != null) {
					TypeContrat typeContrat = getTypeContratDao().chercherTypeContrat(contrat.getIdTypeContrat());
					addZone(getNOM_ST_CDICDD(), typeContrat.getLibTypeContrat());
				} else {
					addZone(getNOM_ST_CDICDD(), Const.CHAINE_VIDE);
				}
			}
		} else {
			addZone(getNOM_ST_CDICDD(), getCarriereCourante().getTypeContrat());
		}

		addZone(getNOM_EF_REF_ARR(), getCarriereCourante().getReferenceArrete());

		recupererFiliere(grade);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_CA_A06 RG_AG_CA_C13 RG_AG_CA_A04
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		boolean declarerModifDateFin = false;

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {

			Carriere derniereCarriere = getDerniereCarriere();
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			if (derniereCarriere != null) {
				if (!derniereCarriere.getCodeCategorie().equals("8")) {
					derniereCarriere.setDateFin(Const.CHAINE_VIDE);
					// RG_AG_CA_A03
					HistoCarriere histo = new HistoCarriere(derniereCarriere);
					getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
					derniereCarriere.modifierCarriere(getTransaction(), getAgentCourant(), user);
					declarerModifDateFin = true;
				} else {
					addZone(getNOM_ST_ACTION(), ACTION_REOUVERTURE);
				}
			}

			// Suppression
			// RG_AG_CA_A03
			HistoCarriere histo = new HistoCarriere(getCarriereCourante());
			getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.SUPPRESSION);
			getCarriereCourante().supprimerCarriere(getTransaction(), user);
			if (getTransaction().isErreur())
				return false;

			setCarriereCourante(null);
			setStatut(STATUT_MEME_PROCESS);

		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION) || getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {

			// Vérification de la validité du formulaire
			if (!performControlerChamps(request))
				return false;

			if (!performRegleGestion(request))
				return false;

			alimenterCarriere();

			if (!performChevauchementDate(request))
				return false;

			if (!performRegleGestionIBA(request))
				return false;

			Carriere derniereCarriere = getDerniereCarriere();
			// RG_AG_CA_A06
			// RG_AG_CA_C13
			if (getCarriereCourante().isActive()) {
				if (derniereCarriere != null
						&& !derniereCarriere.getCodeCategorie().equals("8")
						&& (derniereCarriere.getDateFin() == null || derniereCarriere.getDateFin().equals(Const.DATE_NULL) || !derniereCarriere.getDateFin().equals(
								getCarriereCourante().getDateDebut()))) {
					derniereCarriere.setDateFin(getCarriereCourante().getDateDebut());
					// RG_AG_CA_A03
					HistoCarriere histo = new HistoCarriere(derniereCarriere);
					getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
					derniereCarriere.modifierCarriere(getTransaction(), getAgentCourant(), user);
					declarerModifDateFin = true;
				}
			}

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				// RG_AG_CA_A03
				HistoCarriere histo = new HistoCarriere(getCarriereCourante());
				getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
				getCarriereCourante().modifierCarriere(getTransaction(), getAgentCourant(), user);
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Création
				// RG_AG_CA_A03
				HistoCarriere histo = new HistoCarriere(getCarriereCourante());
				getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.CREATION);
				getCarriereCourante().creerCarriere(getTransaction(), getAgentCourant(), user);
			}

			// RG_AG_CA_A04
			Matricule.updateMatricule(getTransaction(), getAgentCourant(), getCarriereCourante().getDateDebut());

			if (getTransaction().isErreur())
				return false;
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_REOUVERTURE)) {
			Carriere derniereCarriere = getDerniereCarriere();
			derniereCarriere.setDateFin(Const.CHAINE_VIDE);
			// RG_AG_CA_A03
			HistoCarriere histo = new HistoCarriere(derniereCarriere);
			getHistoCarriereDao().creerHistoCarriere(histo, user, EnumTypeHisto.MODIFICATION);
			derniereCarriere.modifierCarriere(getTransaction(), getAgentCourant(), user);
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		initialiseListeCarrieres(request);

		if (getTransaction().isErreur())
			return false;

		// Tout s'est bien passé
		commitTransaction();
		// on regarde sir il y a un changement de statut
		String messageInf = Const.CHAINE_VIDE;

		if (declarerModifDateFin)
			messageInf += MessageUtils.getMessage("INF005");

		// on regarde si le motif de la carriere est renseigné
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION) || getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			if (getCarriereCourante().getIdMotif().equals(Const.ZERO)) {
				// "INF300",
				// "Attention, vous n'avez pas saisi de motif pour cette carrière."
				messageInf += MessageUtils.getMessage("INF300");
			}

		}
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			if (estChangementStatutCarriere()) {
				messageInf += MessageUtils.getMessage("INF008");
			}
		}
		// on regarde si l'année de la carriere crée est <=année -1
		if (getCarriereCourante() != null) {
			Integer anneeCourante = Integer.parseInt(Services.dateDuJour().substring(6, 10));
			if (Integer.parseInt(Services.formateDate(getCarriereCourante().getDateDebut()).substring(6, 10)) < anneeCourante - 1) {
				messageInf += MessageUtils.getMessage("INF300");
			}
		}
		if (!messageInf.equals(Const.CHAINE_VIDE))
			getTransaction().declarerErreur(messageInf);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		return true;
	}

	private boolean performRegleGestionIBA(HttpServletRequest request) throws Exception {
		// test IBA existant
		if ((estFonctionnaire(getCarriereCourante().getCodeCategorie()) || estContractuel(getCarriereCourante().getCodeCategorie())) && !getCarriereCourante().getIban().equals(Const.CHAINE_VIDE)) {
			Bareme.chercherBareme(getTransaction(), getCarriereCourante().getIban());

			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "ERR125", "Impossible de trouver @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR125", "le barême correspondant"));
				return false;
			}
		}
		return true;
	}

	/**
	 * alimente la carriere courante avec le formulaire
	 * 
	 * créée le 22/09/2011
	 */
	public void alimenterCarriere() throws Exception {

		// récupération des informations remplies dans les zones de saisie
		String dateDebut = Services.formateDate(getVAL_EF_DATE_DEBUT());
		String dateFin = Services.formateDate(getVAL_EF_DATE_FIN());
		String dateArrete = Services.formateDate(getVAL_EF_DATE_ARR());

		String accJours = getVAL_EF_ACC_JOURS();
		String accMois = getVAL_EF_ACC_MOIS();
		String accAnnees = getVAL_EF_ACC_ANNEES();
		String bmJours = getVAL_EF_BM_JOURS();
		String bmMois = getVAL_EF_BM_MOIS();
		String bmAnnees = getVAL_EF_BM_ANNEES();

		String refArrete = getVAL_EF_REF_ARR();

		String iban = "0000000";
		if (!Const.CHAINE_VIDE.equals(getVAL_EF_IBA())) {
			if (Services.estNumerique(getVAL_EF_IBA())) {
				iban = Services.lpad(getVAL_EF_IBA(), 7, "0");
			} else {
				iban = getVAL_EF_IBA();
			}
		}

		// statut
		int numStatut = (Services.estNumerique(getZone(getNOM_LB_STATUTS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUTS_SELECT())) : -1);
		if (numStatut == -1 || getListeStatut().size() == 0 || numStatut > getListeStatut().size())
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
		StatutCarriere statut = (StatutCarriere) getListeStatut().get(numStatut - 1);

		// Régime
		String regime = getListeRegime().get(Integer.parseInt(getVAL_LB_REGIMES_SELECT()));

		// base horaire
		int numLigneBH = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_HORAIRE_SELECT())) : -1);
		if (numLigneBH == -1 || getListeHoraire().size() == 0 || numLigneBH > getListeHoraire().size())
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "bases horaire"));
		Horaire horaire = (Horaire) getListeHoraire().get(numLigneBH);

		// base reglement
		int numLigneBR = (Services.estNumerique(getZone(getNOM_LB_BASE_REGLEMENT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BASE_REGLEMENT_SELECT())) : -1);
		if (numLigneBR == -1 || getListeBaseReg().size() == 0 || numLigneBR > getListeBaseReg().size())
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "bases réglement"));
		ModeReglement baseReg = (ModeReglement) getListeBaseReg().get(numLigneBR);

		// motif
		int numLigneMotif = (Services.estNumerique(getZone(getNOM_LB_MOTIFS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_MOTIFS_SELECT())) : -1);
		MotifCarriere motif = numLigneMotif > 0 ? (MotifCarriere) getListeMotifCarriere().get(numLigneMotif - 1) : null;

		if (getCarriereCourante() == null) {
			setCarriereCourante(new Carriere());
			getCarriereCourante().setNoMatricule(getAgentCourant().getNomatr().toString());
		}

		getCarriereCourante().setDateDebut(dateDebut);
		getCarriereCourante().setDateFin(dateFin);
		getCarriereCourante().setDateArrete(dateArrete == null ? Const.ZERO : dateArrete);

		getCarriereCourante().setReferenceArrete(refArrete.equals(Const.CHAINE_VIDE) ? Const.ZERO : refArrete);

		getCarriereCourante().setACCJour(accJours);
		getCarriereCourante().setACCMois(accMois);
		getCarriereCourante().setACCAnnee(accAnnees);
		getCarriereCourante().setBMJour(bmJours);
		getCarriereCourante().setBMMois(bmMois);
		getCarriereCourante().setBMAnnee(bmAnnees);
		getCarriereCourante().setIban(iban);

		if (getCarriereCourante().getTypeContrat().equals(Const.CHAINE_VIDE)) {
			Contrat contrat = null;
			try {
				contrat = getContratDao().chercherContratAgentDateComprise(getAgentCourant().getIdAgent(), sdf.parse(dateDebut));
			} catch (Exception e) {
				// aucun contrat trouvé
			}
			if (contrat != null && contrat.getIdTypeContrat() != null) {
				TypeContrat typeContrat = getTypeContratDao().chercherTypeContrat(contrat.getIdTypeContrat());
				addZone(getNOM_ST_CDICDD(), typeContrat.getLibTypeContrat());
			} else
				addZone(getNOM_ST_CDICDD(), Const.CHAINE_VIDE);

			getCarriereCourante().setTypeContrat(getVAL_ST_CDICDD());

		}
		if (!getVAL_ST_CDICDD().equals(Const.CHAINE_VIDE)) {
			getCarriereCourante().setTypeContrat(getVAL_ST_CDICDD());
		}

		// grade
		if (gradeObligatoire) {
			Grade g = getSelectedGrade();
			getCarriereCourante().setCodeGrade(g != null && g.getCodeGrade() != null ? g.getCodeGrade() : Const.CHAINE_VIDE);
		} else {
			getCarriereCourante().setCodeGrade(Const.CHAINE_VIDE);
			try {
				Grade g = getSelectedGrade();
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					getCarriereCourante().setCodeGrade(g != null && g.getCodeGrade() != null ? g.getCodeGrade() : Const.CHAINE_VIDE);
				}
			} catch (Exception e) {

			}
		}

		// satut
		getCarriereCourante().setCodeCategorie(statut.getCdCate());

		// regime (1 a 3)
		getCarriereCourante().setCodeTypeEmploi(regime);

		// basehoraire
		getCarriereCourante().setCodeBaseHoraire2(horaire.getCdtHor());

		// basehoraire pointage
		// #13184 : on ne gere plus la base de pointage dans la carriere
		getCarriereCourante().setCodeBase(Const.CHAINE_VIDE);

		// baseReglement
		getCarriereCourante().setModeReglement(baseReg.getModReg());

		// motif
		getCarriereCourante().setIdMotif(motif != null ? motif.getIdMotifCarriere().toString() : Const.ZERO);
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire de carriere
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_CA_A05
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// RG_AG_CA_A05

		// statut obligatoire
		int indiceStatut = (Services.estNumerique(getVAL_LB_STATUTS_SELECT()) ? Integer.parseInt(getVAL_LB_STATUTS_SELECT()) : -1);
		if (indiceStatut < 1) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "statut"));
			return false;
		}

		if (gradeObligatoire) {
			Grade g = getSelectedGrade();
			if (g == null || getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "grade"));
				return false;
			}
		}

		// date de debut obligatoire
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_DEBUT())) {
			// "ERR002", "La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de début"));
			return false;
		}

		// format date de debut
		if (!Services.estUneDate(getVAL_EF_DATE_DEBUT())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de début"));
			return false;
		}

		// format date de fin
		if (!Const.CHAINE_VIDE.equals(getVAL_EF_DATE_FIN()) && !Services.estUneDate(getVAL_EF_DATE_FIN())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de fin"));
			return false;
		}

		// format ACC et BM
		if (!(Const.CHAINE_VIDE).equals(getVAL_EF_ACC_JOURS()) && !Services.estNumerique(getVAL_EF_ACC_JOURS())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "ACC jours"));
			return false;
		}
		if (!(Const.CHAINE_VIDE).equals(getVAL_EF_ACC_MOIS()) && !Services.estNumerique(getVAL_EF_ACC_MOIS())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "ACC mois"));
			return false;
		}
		if (!(Const.CHAINE_VIDE).equals(getVAL_EF_ACC_ANNEES()) && !Services.estNumerique(getVAL_EF_ACC_ANNEES())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "ACC années"));
			return false;
		}

		if (!(Const.CHAINE_VIDE).equals(getVAL_EF_BM_JOURS()) && !Services.estNumerique(getVAL_EF_BM_JOURS())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "BM jours"));
			return false;
		}
		if (!(Const.CHAINE_VIDE).equals(getVAL_EF_BM_MOIS()) && !Services.estNumerique(getVAL_EF_BM_MOIS())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "BM mois"));
			return false;
		}
		if (!(Const.CHAINE_VIDE).equals(getVAL_EF_BM_ANNEES()) && !Services.estNumerique(getVAL_EF_BM_ANNEES())) {
			// "ERR992", "La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "BM années"));
			return false;
		}
		// format date arrêté
		if ((!(Const.CHAINE_VIDE).equals(getVAL_EF_DATE_ARR())) && !Services.estUneDate(getVAL_EF_DATE_ARR())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'arrêté"));
			return false;
		}
		return true;
	}

	/**
	 * Vérifie les regles de gestion du formulaire de carriere
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_CA_A05
	 */
	public boolean performRegleGestion(HttpServletRequest request) throws Exception {
		// RG_AG_CA_A05
		// testdate debut < date fin
		if (Services.compareDates(getVAL_EF_DATE_DEBUT(), getVAL_EF_DATE_FIN()) >= 0) {
			// "ERR204", "La date @ doit être inferieure à la date @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR204", "de début", "de fin"));
			return false;
		}

		if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {

			@SuppressWarnings("unused")
			PositionAdmAgent dernierePA = null;
			// on verifie qu'il existe une PA comprise dans dateDebCarr
			PositionAdmAgent posAdmAgtActiveAUneDate = PositionAdmAgent.chercherPositionAdmAgentDateFinExclu(getTransaction(), getAgentCourant().getNomatr(),
					Services.convertitDate(Services.formateDate(getVAL_EF_DATE_DEBUT()), "dd/MM/yyyy", "yyyyMMdd"));
			// si PA recupérée est ACTIVE
			if (!posAdmAgtActiveAUneDate.estPAInactive(getTransaction())) {
				dernierePA = posAdmAgtActiveAUneDate;
			} else {
				// si PA recuperre est inactive
				// "ERR135",
				// "Il n'y a pas de PA active a cette date de debut de carriere."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR135"));
				return false;
			}
		}

		return true;
	}

	private boolean performChevauchementDate(HttpServletRequest request) throws Exception {
		Carriere derniereCarriere = getDerniereCarriere();

		// check chevauchement que si carriere active
		if (getCarriereCourante().isActive()) {
			if (derniereCarriere != null && getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION) && !derniereCarriere.getCodeCategorie().equals("8")) {
				if (!derniereCarriere.equals(getCarriereCourante())) {
					if (Services.compareDates(derniereCarriere.getDateDebut(), getCarriereCourante().getDateDebut()) > 0) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
						return false;
					}
				}

				if (!derniereCarriere.equals(getCarriereCourante())) {
					if (Services.compareDates(derniereCarriere.getDateDebut(), getCarriereCourante().getDateDebut()) > 0) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
						return false;
					}
				}
			} else if (derniereCarriere != null) {
				if (derniereCarriere.getDateFin() != null && !derniereCarriere.getDateFin().equals(Const.DATE_NULL)
						&& Services.compareDates(derniereCarriere.getDateFin(), getCarriereCourante().getDateDebut()) > 0) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
					return false;
				} else if (Services.compareDates(derniereCarriere.getDateDebut(), getCarriereCourante().getDateDebut()) >= 0) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
					return false;
				}
			}
		} else if (getCarriereCourante().getCodeCategorie().equals("8")) {
			// si statut = 8 alors on regarde que les dates de la carriere que
			// l'on souhaite modfifier
			// ne debordent pas sur la carr precedente et la carr suivante
			// on recupere la carr precedente
			Carriere carrBase = (Carriere) getCarriereCourante().getBasicMetierBase();
			if (carrBase != null) {
				Carriere carrPrec = Carriere.chercherCarriereAgentPrec(getTransaction(), getAgentCourant().getNomatr(),
						Services.convertitDate(Services.formateDate(carrBase.getDateDebut()), "dd/MM/yyyy", "yyyyMMdd"));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// si pas de carriere precendente on regarde la carriere
					// suivant
					Carriere carrSuiv = Carriere.chercherCarriereAgentSuiv(getTransaction(), getAgentCourant().getNomatr(),
							Services.convertitDate(Services.formateDate(carrBase.getDateDebut()), "dd/MM/yyyy", "yyyyMMdd"));
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						// aucune carriere ni avant ni apres donc on ne fait
						// rien
					} else {
						// pas de carriere precedente mais une carriere suivante
						// si datdebCarr > datDebCarrSuiv et dateFinCarr>
						// datDebCarrSuiv alors erreur
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), carrSuiv.getDateDebut()) > 0 || Services.compareDates(getVAL_EF_DATE_FIN(), carrSuiv.getDateDebut()) > 0) {
							// "ERR134",
							// "Attention, il existe déjà une carrière sur cette période. Veuillez contrôler.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
							return false;
						}
					}
				} else {
					// si on a une carriere precedente
					// on cherche la carriere suivante
					Carriere carrSuiv = Carriere.chercherCarriereAgentSuiv(getTransaction(), getAgentCourant().getNomatr(),
							Services.convertitDate(Services.formateDate(carrBase.getDateDebut()), "dd/MM/yyyy", "yyyyMMdd"));
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						// aucune carriere apres
						// si datdebCarr < datFinCarrPrec alors erreur
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), carrPrec.getDateFin()) < 0) {
							// "ERR134",
							// "Attention, il existe déjà une carrière sur cette période. Veuillez contrôler.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
							return false;
						}
					} else {
						// si on a une carriere precedente et une carriere
						// suivante
						// si datdebCarr < datFinCarrPrec et dateFinCarr >
						// dateDebSuiv alors erreur
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), carrPrec.getDateFin()) < 0 || Services.compareDates(getVAL_EF_DATE_FIN(), carrSuiv.getDateDebut()) > 0) {
							// "ERR134",
							// "Attention, il existe déjà une carrière sur cette période. Veuillez contrôler.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
							return false;
						}
					}
				}
			} else {
				// c'est qu'on est en cretion d'une nouvelle carriere
				Carriere carrPrec = Carriere.chercherCarriereAgentPrec(getTransaction(), getAgentCourant().getNomatr(),
						Services.convertitDate(Services.formateDate(getCarriereCourante().getDateDebut()), "dd/MM/yyyy", "yyyyMMdd"));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// si pas de carriere precendente on regarde la carriere
					// suivant
					Carriere carrSuiv = Carriere.chercherCarriereAgentSuiv(getTransaction(), getAgentCourant().getNomatr(),
							Services.convertitDate(Services.formateDate(getCarriereCourante().getDateDebut()), "dd/MM/yyyy", "yyyyMMdd"));
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						// aucune carriere ni avant ni apres donc on ne fait
						// rien
					} else {
						// pas de carriere precedente mais une carriere suivante
						// si datdebCarr > datDebCarrSuiv et dateFinCarr>
						// datDebCarrSuiv alors erreur
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), carrSuiv.getDateDebut()) > 0 || Services.compareDates(getVAL_EF_DATE_FIN(), carrSuiv.getDateDebut()) > 0) {
							// "ERR134",
							// "Attention, il existe déjà une carrière sur cette période. Veuillez contrôler.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
							return false;
						}
					}
				} else {
					// si on a une carriere precedente
					// on cherche la carriere suivante
					Carriere carrSuiv = Carriere.chercherCarriereAgentSuiv(getTransaction(), getAgentCourant().getNomatr(),
							Services.convertitDate(Services.formateDate(getCarriereCourante().getDateDebut()), "dd/MM/yyyy", "yyyyMMdd"));
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						// aucune carriere apres
						// si datdebCarr < datFinCarrPrec alors erreur
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), carrPrec.getDateFin()) < 0) {
							// "ERR134",
							// "Attention, il existe déjà une carrière sur cette période. Veuillez contrôler.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
							return false;
						}
					} else {
						// si on a une carriere precedente et une carriere
						// suivante
						// si datdebCarr < datFinCarrPrec et dateFinCarr >
						// dateDebSuiv alors erreur
						if (Services.compareDates(getVAL_EF_DATE_DEBUT(), carrPrec.getDateFin()) < 0 || Services.compareDates(getVAL_EF_DATE_FIN(), carrSuiv.getDateDebut()) > 0) {
							// "ERR134",
							// "Attention, il existe déjà une carrière sur cette période. Veuillez contrôler.");
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR134"));
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private Carriere getDerniereCarriere() throws Exception {

		Carriere derniereCarriere = null;

		if (getListeCarriere().size() != 0)
			derniereCarriere = getListeCarriere().get(getListeCarriere().size() - 1);

		if (derniereCarriere != null && getCarriereCourante() != null && derniereCarriere.equals(getCarriereCourante()))
			if (getListeCarriere().size() <= 1)
				derniereCarriere = null;
			else
				derniereCarriere = getListeCarriere().get(getListeCarriere().size() - 2);

		return derniereCarriere;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BASE_HORAIRE Date de
	 * création : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_BASE_HORAIRE() {
		if (LB_BASE_HORAIRE == null)
			LB_BASE_HORAIRE = initialiseLazyLB();
		return LB_BASE_HORAIRE;
	}

	/**
	 * Setter de la liste: LB_BASE_HORAIRE Date de création : (05/09/11
	 * 14:28:25)
	 * 
	 */
	private void setLB_BASE_HORAIRE(String[] newLB_BASE_HORAIRE) {
		LB_BASE_HORAIRE = newLB_BASE_HORAIRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BASE_HORAIRE Date de
	 * création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_HORAIRE() {
		return "NOM_LB_BASE_HORAIRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BASE_HORAIRE_SELECT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_HORAIRE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BASE_HORAIRE Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_BASE_HORAIRE() {
		return getLB_BASE_HORAIRE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_BASE_HORAIRE Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_BASE_HORAIRE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BASE_REGLEMENT Date de
	 * création : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_BASE_REGLEMENT() {
		if (LB_BASE_REGLEMENT == null)
			LB_BASE_REGLEMENT = initialiseLazyLB();
		return LB_BASE_REGLEMENT;
	}

	/**
	 * Setter de la liste: LB_BASE_REGLEMENT Date de création : (05/09/11
	 * 14:28:25)
	 * 
	 */
	private void setLB_BASE_REGLEMENT(String[] newLB_BASE_REGLEMENT) {
		LB_BASE_REGLEMENT = newLB_BASE_REGLEMENT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BASE_REGLEMENT Date de
	 * création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_REGLEMENT() {
		return "NOM_LB_BASE_REGLEMENT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BASE_REGLEMENT_SELECT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_BASE_REGLEMENT_SELECT() {
		return "NOM_LB_BASE_REGLEMENT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BASE_REGLEMENT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_BASE_REGLEMENT() {
		return getLB_BASE_REGLEMENT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_BASE_REGLEMENT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_BASE_REGLEMENT_SELECT() {
		return getZone(getNOM_LB_BASE_REGLEMENT_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REGIMES Date de création
	 * : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_REGIMES() {
		if (LB_REGIMES == null)
			LB_REGIMES = initialiseLazyLB();
		return LB_REGIMES;
	}

	/**
	 * Setter de la liste: LB_REGIMES Date de création : (05/09/11 14:28:25)
	 * 
	 */
	private void setLB_REGIMES(String[] newLB_REGIMES) {
		LB_REGIMES = newLB_REGIMES;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REGIMES Date de création
	 * : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_REGIMES() {
		return "NOM_LB_REGIMES";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_REGIMES_SELECT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_REGIMES_SELECT() {
		return "NOM_LB_REGIMES_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_REGIMES Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_REGIMES() {
		return getLB_REGIMES();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_REGIMES Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_REGIMES_SELECT() {
		return getZone(getNOM_LB_REGIMES_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUTS Date de création
	 * : (05/09/11 14:28:25)
	 * 
	 */
	private String[] getLB_STATUTS() {
		if (LB_STATUTS == null)
			LB_STATUTS = initialiseLazyLB();
		return LB_STATUTS;
	}

	/**
	 * Setter de la liste: LB_STATUTS Date de création : (05/09/11 14:28:25)
	 * 
	 */
	private void setLB_STATUTS(String[] newLB_STATUTS) {
		LB_STATUTS = newLB_STATUTS;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUTS Date de création
	 * : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_STATUTS() {
		return "NOM_LB_STATUTS";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_STATUTS_SELECT Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getNOM_LB_STATUTS_SELECT() {
		return "NOM_LB_STATUTS_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_STATUTS Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String[] getVAL_LB_STATUTS() {
		return getLB_STATUTS();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_STATUTS Date de création : (05/09/11 14:28:25)
	 * 
	 */
	public String getVAL_LB_STATUTS_SELECT() {
		return getZone(getNOM_LB_STATUTS_SELECT());
	}

	private String[] LB_MOTIFS;

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIFS Date de création :
	 * (05/09/11 14:28:45)
	 * 
	 */
	private String[] getLB_MOTIFS() {
		if (LB_MOTIFS == null)
			LB_MOTIFS = initialiseLazyLB();
		return LB_MOTIFS;
	}

	/**
	 * Setter de la liste: LB_MOTIFS Date de création : (05/09/11 14:28:45)
	 * 
	 */
	private void setLB_MOTIFS(String[] newLB_MOTIFS) {
		LB_MOTIFS = newLB_MOTIFS;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIFS Date de création :
	 * (05/09/11 14:28:45)
	 * 
	 */
	public String getNOM_LB_MOTIFS() {
		return "NOM_LB_MOTIFS";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIFS_SELECT Date de création : (05/09/11 14:28:45)
	 * 
	 */
	public String getNOM_LB_MOTIFS_SELECT() {
		return "NOM_LB_MOTIFS_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIFS Date de création : (05/09/11 14:28:45)
	 * 
	 */
	public String[] getVAL_LB_MOTIFS() {
		return getLB_MOTIFS();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_MOTIFS Date de création : (05/09/11 14:28:45)
	 * 
	 */
	public String getVAL_LB_MOTIFS_SELECT() {
		return getZone(getNOM_LB_MOTIFS_SELECT());
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * création : (05/09/11 16:01:29)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de création : (05/09/11 16:01:29)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARR Date de
	 * création : (05/09/11 16:03:53)
	 * 
	 */
	public String getNOM_EF_DATE_ARR() {
		return "NOM_EF_DATE_ARR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARR Date de création : (05/09/11 16:03:53)
	 * 
	 */
	public String getVAL_EF_DATE_ARR() {
		return getZone(getNOM_EF_DATE_ARR());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ACC_ANNEES Date de
	 * création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_ACC_ANNEES() {
		return "NOM_EF_ACC_ANNEES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ACC_ANNEES Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_ACC_ANNEES() {
		return getZone(getNOM_EF_ACC_ANNEES());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ACC_JOURS Date de
	 * création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_ACC_JOURS() {
		return "NOM_EF_ACC_JOURS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ACC_JOURS Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_ACC_JOURS() {
		return getZone(getNOM_EF_ACC_JOURS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ACC_MOIS Date de
	 * création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_ACC_MOIS() {
		return "NOM_EF_ACC_MOIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ACC_MOIS Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_ACC_MOIS() {
		return getZone(getNOM_EF_ACC_MOIS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BM_ANNEES Date de
	 * création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_BM_ANNEES() {
		return "NOM_EF_BM_ANNEES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BM_ANNEES Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_BM_ANNEES() {
		return getZone(getNOM_EF_BM_ANNEES());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BM_JOURS Date de
	 * création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_BM_JOURS() {
		return "NOM_EF_BM_JOURS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BM_JOURS Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_BM_JOURS() {
		return getZone(getNOM_EF_BM_JOURS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BM_MOIS Date de
	 * création : (05/09/11 16:21:49)
	 * 
	 */
	public String getNOM_EF_BM_MOIS() {
		return "NOM_EF_BM_MOIS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BM_MOIS Date de création : (05/09/11 16:21:49)
	 * 
	 */
	public String getVAL_EF_BM_MOIS() {
		return getZone(getNOM_EF_BM_MOIS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_REF_ARR Date de
	 * création : (05/09/11 16:33:06)
	 * 
	 */
	public String getNOM_EF_REF_ARR() {
		return "NOM_EF_REF_ARR";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_REF_ARR Date de création : (05/09/11 16:33:06)
	 * 
	 */
	public String getVAL_EF_REF_ARR() {
		return getZone(getNOM_EF_REF_ARR());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CDICDD Date de
	 * création : (06/09/11 09:41:00)
	 * 
	 */
	public String getNOM_ST_CDICDD() {
		return "NOM_ST_CDICDD";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CDICDD Date de
	 * création : (06/09/11 09:41:00)
	 * 
	 */
	public String getVAL_ST_CDICDD() {
		return getZone(getNOM_ST_CDICDD());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FILIERE Date de
	 * création : (06/09/11 09:41:00)
	 * 
	 */
	public String getNOM_ST_FILIERE() {
		return "NOM_ST_FILIERE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FILIERE Date
	 * de création : (06/09/11 09:41:00)
	 * 
	 */
	public String getVAL_ST_FILIERE() {
		return getZone(getNOM_ST_FILIERE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INA Date de création
	 * : (06/09/11 09:41:00)
	 * 
	 */
	public String getNOM_ST_INA() {
		return "NOM_ST_INA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INA Date de
	 * création : (06/09/11 09:41:00)
	 * 
	 */
	public String getVAL_ST_INA() {
		return getZone(getNOM_ST_INA());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INM Date de création
	 * : (06/09/11 09:41:00)
	 * 
	 */
	public String getNOM_ST_INM() {
		return "NOM_ST_INM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INM Date de
	 * création : (06/09/11 09:41:00)
	 * 
	 */
	public String getVAL_ST_INM() {
		return getZone(getNOM_ST_INM());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_IBA Date de
	 * création : (06/09/11 09:41:00)
	 * 
	 */
	public String getNOM_EF_IBA() {
		return "NOM_EF_IBA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_IBA
	 * Date de création : (06/09/11 09:41:00)
	 * 
	 */
	public String getVAL_EF_IBA() {
		return getZone(getNOM_EF_IBA());
	}

	private ArrayList<StatutCarriere> getListeStatut() {
		return listeStatut;
	}

	private void setListeStatut(ArrayList<StatutCarriere> listeStatut) {
		this.listeStatut = listeStatut;
	}

	private Hashtable<String, StatutCarriere> getHashStatut() {
		if (hashStatut == null)
			hashStatut = new Hashtable<String, StatutCarriere>();
		return hashStatut;
	}

	private ArrayList<Horaire> getListeHoraire() {
		return listeHoraire;
	}

	private void setListeHoraire(ArrayList<Horaire> listeHoraire) {
		this.listeHoraire = listeHoraire;
	}

	private ArrayList<String> getListeRegime() {
		return listeRegime;
	}

	private void setListeRegime(ArrayList<String> listeRegime) {
		this.listeRegime = listeRegime;
	}

	private ArrayList<ModeReglement> getListeBaseReg() {
		return listeBaseReg;
	}

	private void setListeBaseReg(ArrayList<ModeReglement> listeBaseReg) {
		this.listeBaseReg = listeBaseReg;
	}

	private ArrayList<MotifCarriere> getListeMotifCarriere() {
		return listeMotifCarriere;
	}

	private void setListeMotifCarriere(ArrayList<MotifCarriere> listeMotifCarriere) {
		this.listeMotifCarriere = listeMotifCarriere;
	}

	public Carriere getCarriereCourante() {
		return carriereCourante;
	}

	private void setCarriereCourante(Carriere carriereCourante) {
		this.carriereCourante = carriereCourante;
	}

	public ArrayList<Carriere> getListeCarriere() {
		return listeCarriere;
	}

	private void setListeCarriere(ArrayList<Carriere> listeCarriere) {
		this.listeCarriere = listeCarriere;
	}

	public ArrayList<Grade> getListeGrade() {
		return listeGrade;
	}

	private void setListeGrade(ArrayList<Grade> listeGrade) {
		this.listeGrade = listeGrade;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_GRADE Date de
	 * création : (23/09/11 09:21:39)
	 * 
	 */
	public String getNOM_PB_SELECT_GRADE() {
		return "NOM_PB_SELECT_GRADE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (23/09/11 09:21:39)
	 * 
	 */
	public boolean performPB_SELECT_GRADE(HttpServletRequest request) throws Exception {

		Grade g = getSelectedGrade();

		recupererFiliere(g);

		recupererIBA(g);

		return true;
	}

	private Grade getSelectedGrade() throws Exception {
		// récupération du grade et Vérification de son existence.
		String idGrade = Const.CHAINE_VIDE;
		for (int i = 0; i < getListeGrade().size(); i++) {
			Grade g = (Grade) getListeGrade().get(i);
			String iban = Const.CHAINE_VIDE;
			if (Services.estNumerique(g.getIban())) {
				iban = Services.lpad(g.getIban(), 7, "0");
			} else {
				iban = g.getIban();
			}
			String textGrade = g.getCodeGrade() + " " + g.getLibGrade() + " " + iban;
			if (textGrade.equals(getVAL_EF_GRADE())) {
				idGrade = g.getCodeGrade();
				break;
			}
		}
		if (idGrade.length() == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "grades"));
		}
		return Grade.chercherGrade(getTransaction(), idGrade);
	}

	/**
	 * renseigne les champs concernant le barême en fonction du grade et du
	 * statut
	 * 
	 * @param g
	 * @throws Exception
	 *             RG_AG_CA_C08 RG_AG_CA_C09
	 */
	private void recupererIBA(Grade grade) throws Exception {
		// RG_AG_CA_C08
		// RG_AG_CA_C09

		addZone(getNOM_EF_IBA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INM(), Const.CHAINE_VIDE);

		showIBA = false;
		IBAEditable = false;

		int numStatut = (Services.estNumerique(getZone(getNOM_LB_STATUTS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUTS_SELECT())) : -1);
		if (numStatut <= 0 || getListeStatut().size() == 0 || numStatut > getListeStatut().size())
			return;
		StatutCarriere statut = (StatutCarriere) getListeStatut().get(numStatut - 1);
		String codeStatut = statut.getCdCate();

		if (estFonctionnaire(codeStatut)) {
			showIBA = true;
			IBAEditable = false;
			alimenteIBAn(grade);

		} else if (codeStatut.equals("4") && getCarriereCourante() != null && getCarriereCourante().getCodeGrade() != null) {
			// #19533 : cas des contractuels sur grille
			showIBA = true;
			IBAEditable = false;
			alimenteIBAn(grade);

		} else if (codeStatut.equals("4") || codeStatut.equals("9") || codeStatut.equals("10") || codeStatut.equals("11")) {
			showIBA = true;
			IBAEditable = true;
		}
	}

	private void alimenteIBAn(Grade grade) throws Exception {
		showIBA = true;
		IBAEditable = false;

		if (grade != null && grade.getIban() != null && !Const.CHAINE_VIDE.equals(grade.getIban())) {
			if (Services.estNumerique(grade.getIban())) {
				addZone(getNOM_EF_IBA(), Services.lpad(grade.getIban(), 7, "0"));
			} else {
				addZone(getNOM_EF_IBA(), grade.getIban());
			}
			Bareme bareme = null;
			if (Services.estNumerique(grade.getIban())) {
				bareme = Bareme.chercherBareme(getTransaction(), Services.lpad(grade.getIban(), 7, "0"));
			} else {
				bareme = Bareme.chercherBareme(getTransaction(), grade.getIban());
			}

			if (getTransaction().isErreur())
				getTransaction().traiterErreur();

			if (bareme != null) {
				addZone(getNOM_ST_INA(), bareme.getIna());
				addZone(getNOM_ST_INM(), bareme.getInm());
			}
		}

	}

	/**
	 * renseigne les champs concernant la filière en fonction du grade
	 * 
	 * @param g
	 *            RG_AG_CA_C03
	 */
	private void recupererFiliere(Grade g) throws Exception {
		// RG_AG_CA_C03

		addZone(getNOM_ST_FILIERE(), Const.CHAINE_VIDE);

		if (g != null && g.getCodeGradeGenerique() != null) {
			// alors on cherche le grade generique afin de trouver la filiere
			GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			if (gg != null && gg.getIdCadreEmploi() != null && gg.getCdfili() != null) {
				FiliereGrade filiere = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();

				if (filiere != null)
					addZone(getNOM_ST_FILIERE(), filiere.getLibFiliere());

			}
		}

	}

	/**
	 * Détermine si le statut passé en parametre correspond a un fonctionnaire
	 * 
	 * @param statut
	 *            le statut a tester, si null récupére le statut courant
	 * @return true si fonctionnaire false sinon
	 */
	private boolean estFonctionnaire(String codeStatut) {

		return codeStatut.equals("1") || codeStatut.equals("2") || codeStatut.equals("3") || codeStatut.equals("6") || codeStatut.equals("15") || codeStatut.equals("17") || codeStatut.equals("18")
				|| codeStatut.equals("19") || codeStatut.equals("20");
	}

	/**
	 * Détermine si le statut courant correspond a un contractuel
	 * 
	 * @return true si contractuel false sinon
	 */
	public boolean estContractuel() {
		int numStatut = (Services.estNumerique(getZone(getNOM_LB_STATUTS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUTS_SELECT())) : -1);
		if (numStatut <= 0 || getListeStatut().size() == 0 || numStatut > getListeStatut().size())
			return false;
		StatutCarriere statut = (StatutCarriere) getListeStatut().get(numStatut - 1);

		return estContractuel(statut.getCdCate());
	}

	/**
	 * Détermine si le statut courant permet la saiaie de l'IBA
	 * 
	 * @return true si saisie IBA possible false sinon
	 */
	public boolean saisieIba() {
		int numStatut = (Services.estNumerique(getZone(getNOM_LB_STATUTS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUTS_SELECT())) : -1);
		if (numStatut <= 0 || getListeStatut().size() == 0 || numStatut > getListeStatut().size())
			return false;
		StatutCarriere statut = (StatutCarriere) getListeStatut().get(numStatut - 1);
		String categ = statut.getCdCate();
		if (categ.equals("4") || categ.equals("9") || categ.equals("10") || categ.equals("11"))
			return true;
		else
			return false;
	}

	/**
	 * Détermine si le statut courant correspond a un contractuel
	 * 
	 * @return true si contractuel false sinon
	 */
	public boolean estContractuel(String codeStatut) {
		return codeStatut.equals("4");
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_STATUT Date de
	 * création : (26/09/11 09:04:08)
	 * 
	 */
	public String getNOM_PB_SELECT_STATUT() {
		return "NOM_PB_SELECT_STATUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (26/09/11 09:04:08)
	 * 
	 */
	public boolean performPB_SELECT_STATUT(HttpServletRequest request) throws Exception {

		int numStatut = (Services.estNumerique(getZone(getNOM_LB_STATUTS_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUTS_SELECT())) : -1);
		if (numStatut <= 0 || getListeStatut().size() == 0 || numStatut > getListeStatut().size())
			return false;
		StatutCarriere statut = (StatutCarriere) getListeStatut().get(numStatut - 1);

		initialiseChampObligatoire(statut);

		// si statut = 7 ou 8 alors on affiche une liste partiel pour les grades
		if (statut.getCdCate().equals("7") || statut.getCdCate().equals("8")) {
			ArrayList<Grade> liste = Grade.listerGradeConvCol(getTransaction());
			setListeGrade(liste);
			afficherListeGrade();
		} else {
			setListeGrade(null);
		}
		return true;
	}

	/**
	 * 
	 * @param statut
	 *            RG_AG_CA_C11 RG_AG_CA_C12
	 */
	private void initialiseChampObligatoire(StatutCarriere statut) {
		// RG_AG_CA_C11
		// RG_AG_CA_C12
		gradeObligatoire = true;
		showDateFin = false;

		switch (Integer.parseInt(statut.getCdCate())) {
			case 4:
				gradeObligatoire = false;
				break;
			case 8:
				showDateFin = true;
				break;
			case 9:
				gradeObligatoire = false;
				break;
			case 10:
				gradeObligatoire = false;
				break;
			case 11:
				gradeObligatoire = false;
				break;
			case 15:
				gradeObligatoire = false;
				break;
		}

	}

	private Hashtable<String, Grade> getHashGrade() {
		if (hashGrade == null)
			hashGrade = new Hashtable<String, Grade>();
		return hashGrade;
	}

	private Hashtable<String, Horaire> getHashHoraire() {
		if (hashHoraire == null)
			hashHoraire = new Hashtable<String, Horaire>();
		return hashHoraire;
	}

	private Hashtable<String, ModeReglement> getHashModeReglement() {
		if (hashModeReglement == null)
			hashModeReglement = new Hashtable<String, ModeReglement>();
		return hashModeReglement;
	}

	private Hashtable<String, MotifCarriere> getHashMotifCarriere() {
		if (hashMotifCarriere == null)
			hashMotifCarriere = new Hashtable<String, MotifCarriere>();
		return hashMotifCarriere;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_GRADE() {
		return "NOM_ST_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_GRADE() {
		return getZone(getNOM_ST_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_HORAIRE Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_HORAIRE() {
		return "NOM_ST_HORAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_HORAIRE Date
	 * de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_HORAIRE() {
		return getZone(getNOM_ST_HORAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_MOTIF() {
		return "NOM_ST_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_MOTIF() {
		return getZone(getNOM_ST_MOTIF());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REGIME Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_REGIME() {
		return "NOM_ST_REGIME";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REGIME Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_REGIME() {
		return getZone(getNOM_ST_REGIME());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REGLEMENT Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_REGLEMENT() {
		return "NOM_ST_REGLEMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REGLEMENT Date
	 * de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_REGLEMENT() {
		return getZone(getNOM_ST_REGLEMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_STATUT() {
		return "NOM_ST_STATUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_STATUT() {
		return getZone(getNOM_ST_STATUT());
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_INIT_TYPE_CONTRAT
			if (testerParametre(request, getNOM_PB_INIT_TYPE_CONTRAT())) {
				return performPB_INIT_TYPE_CONTRAT(request);
			}

			// Si clic sur le bouton PB_SELECT_STATUT
			if (testerParametre(request, getNOM_PB_SELECT_STATUT())) {
				return performPB_SELECT_STATUT(request);
			}

			// Si clic sur le bouton PB_SELECT_GRADE
			if (testerParametre(request, getNOM_PB_SELECT_GRADE())) {
				return performPB_SELECT_GRADE(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AVANCEMENT_PREV
			if (testerParametre(request, getNOM_PB_AVANCEMENT_PREV())) {
				return performPB_AVANCEMENT_PREV(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeCarriere().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_VISUALISER
			for (int i = 0; i < getListeCarriere().size(); i++) {
				if (testerParametre(request, getNOM_PB_VISUALISATION(i))) {
					return performPB_VISUALISATION(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeCarriere().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (29/09/11 10:03:37)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTCarriere.jsp";
	}

	public String getNomEcran() {
		return "ECR-AG-ELTSAL-CARRIERES";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AVANCEMENT_PREV Date de
	 * création : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AVANCEMENT_PREV() {
		return "NOM_PB_AVANCEMENT_PREV";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AVANCEMENT_PREV(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_AVCT_PREV);

		// on lance le calcul de l'avancement prev
		// on regarde de quelle categorie est l'agent (Fonctionnaire ou
		// Contractuel)
		Carriere carr = Carriere.chercherDerniereCarriereAvecAgent(getTransaction(), getAgentCourant());
		if (carr == null || carr.getCodeCategorie() == null) {
			// cette personne n'a pas de carriere
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			getTransaction().traiterErreur();
			// "ERR136", "Cet agent n'a aucune carriere active."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR136"));
			return false;
		} else {
			// on regarde si sa derniere PA n'est pas inactive
			PositionAdmAgent posAdmn = PositionAdmAgent.chercherPositionAdmAgentEnCoursAvecAgent(getTransaction(), getAgentCourant().getNomatr());
			if (Services.estNumerique(posAdmn.getCdpadm())) {
				if (carr != null && carr.getCodeCategorie().equals("7")) {
					addZone(getNOM_ST_ACTION(), ACTION_AVCT_PREV_CC);
					// alors on est dans les conventions collectives
					return performCalculConventionCollective(getAgentCourant());
				} else if (carr != null && carr.getCodeCategorie().equals("4")) {
					// alors on est dans les contractuels
					return performCalculContractuel(getAgentCourant());
				} else if (carr != null && (carr.getCodeCategorie().equals("1") || carr.getCodeCategorie().equals("2") || carr.getCodeCategorie().equals("18") || carr.getCodeCategorie().equals("20"))) {
					// alors on est dans les fonctionnaires
					return performCalculFonctionnaire(getAgentCourant());
				} else if (carr != null
						&& (carr.getCodeCategorie().equals("6") || carr.getCodeCategorie().equals("16") || carr.getCodeCategorie().equals("17") || carr.getCodeCategorie().equals("19"))) {
					// alors on est dans les détachés
					return performCalculDetache(getAgentCourant());
				} else {
					// sinon on ne calcul pas d'avancement
					// on affiche un message
					addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
					// "ERR180",
					// "Cet agent n'est pas fontionnaire,convention collective, contractuel ou détaché.Il ne peut pas être soumis a l'avancement."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR180"));
				}
			} else {
				// cette personne n'a pas de PA active
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				// "ERR132", "L'agent n'a pas de PA en cours."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR132"));
				return false;
			}
		}

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean performCalculDetache(Agent agent) throws Exception {
		ReturnMessageDto result = avancementService.isAvancementDetache(getTransaction(), agent);

		if (result.getErrors().size() > 0) {
			String erreur = Const.CHAINE_VIDE;
			for (String err : result.getErrors()) {
				erreur += err;
			}
			getTransaction().declarerErreur(erreur);
			return false;
		}

		String anneeCourante = Services.dateDuJour().substring(6, 10);

		AvancementDetaches avct = avancementService.calculAvancementDetache(getTransaction(), agent, anneeCourante, adsService, getFichePosteDao(), getAffectationDao(),
				getAutreAdministrationAgentDao(), true);
		if (avct == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", Const.CHAINE_VIDE));
			return false;
		} else if (avct.getIdAgent() == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", ": le grade actuel de cet agent n'a pas de grade suivant"));
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(avct.getDateAvctMoy()));

		addZone(getNOM_EF_IBA(), avct.getNouvIban());
		addZone(getNOM_ST_INA(), avct.getNouvIna().toString());
		addZone(getNOM_ST_INM(), avct.getNouvInm().toString());

		addZone(getNOM_EF_BM_ANNEES(), avct.getNouvBmAnnee().toString());
		addZone(getNOM_EF_BM_MOIS(), avct.getNouvBmMois().toString());
		addZone(getNOM_EF_BM_JOURS(), avct.getNouvBmJour().toString());

		addZone(getNOM_EF_ACC_ANNEES(), avct.getNouvAccAnnee().toString());
		addZone(getNOM_EF_ACC_MOIS(), avct.getNouvAccMois().toString());
		addZone(getNOM_EF_ACC_JOURS(), avct.getNouvAccJour().toString());

		Grade gradeSuivant = Grade.chercherGrade(getTransaction(), avct.getIdNouvGrade());
		addZone(getNOM_ST_NOUV_GRADE(), avct.getIdNouvGrade());
		addZone(getNOM_ST_NOUV_GRADE_GEN(), gradeSuivant.getCodeGradeGenerique() == null || gradeSuivant.getCodeGradeGenerique().length() == 0 ? null : gradeSuivant.getCodeGradeGenerique());
		addZone(getNOM_ST_NOUV_CLASSE(), gradeSuivant.getCodeClasse() == null || gradeSuivant.getCodeClasse().length() == 0 ? null : gradeSuivant.getCodeClasse());
		addZone(getNOM_ST_NOUV_ECHELON(), gradeSuivant.getCodeEchelon() == null || gradeSuivant.getCodeEchelon().length() == 0 ? null : gradeSuivant.getCodeEchelon());

		addZone(getNOM_ST_GRADE(), avct.getGrade());
		FiliereGrade filiere = null;
		if (gradeSuivant.getCodeGradeGenerique() != null) {
			GradeGenerique gradeGeneriqueSuivant = GradeGenerique.chercherGradeGenerique(getTransaction(), gradeSuivant.getCodeGradeGenerique());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (gradeGeneriqueSuivant != null && gradeGeneriqueSuivant.getCdfili() != null) {
				filiere = FiliereGrade.chercherFiliereGrade(getTransaction(), gradeGeneriqueSuivant.getCdfili());
			}
		}
		addZone(getNOM_ST_FILIERE(), filiere == null ? Const.CHAINE_VIDE : filiere.getLibFiliere());

		// motif de l'avancement
		MotifAvancement motif = null;
		if (avct.getIdMotifAvct() != null) {
			try {
				motif = getMotifAvancementDao().chercherMotifAvancement(avct.getIdMotifAvct());
			} catch (Exception e) {
			}
		}
		addZone(getNOM_ST_TYPE_AVCT(), motif == null ? Const.CHAINE_VIDE : motif.getLibMotifAvct());

		// on indique que les champs des fonctionnaires ne sont pas a
		// afficher
		showAccBM = true;
		return true;
	}

	private boolean performCalculConventionCollective(Agent agent) throws Exception {
		ReturnMessageDto result = avancementService.isAvancementConventionCollective(getTransaction(), agent);

		if (result.getErrors().size() > 0) {
			String erreur = Const.CHAINE_VIDE;
			for (String err : result.getErrors()) {
				erreur += err;
			}
			getTransaction().declarerErreur(erreur);
			return false;
		}

		String anneeCourante = Services.dateDuJour().substring(6, 10);

		// on regarde si la prime existe deja ou pas
		@SuppressWarnings("unused")
		Prime primeExist = Prime.chercherPrime1200ByRubrAndDate(getTransaction(), agent.getNomatr(), anneeCourante + "0101");
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();

			AvancementConvCol avct = avancementService.calculAvancementConventionCollective(getTransaction(), agent, anneeCourante, adsService, getFichePosteDao(), getAffectationDao());
			if (avct == null) {
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				// "ERR189","Cet avancement ne peut être calculé @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", Const.CHAINE_VIDE));
				return false;
			} else if (avct.getIdAgent() == null) {
				addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
				// "ERR189","Cet avancement ne peut être calculé @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", ": l'agent n'a pas 3 ans d'ancienneté."));
				return false;
			}

			Prime prime = avancementService.getNewPrimeConventionCollective(getTransaction(), agent, avct);
			addZone(getNOM_EF_DATE_DEBUT(), "01/01/" + avct.getAnnee());
			addZone(getNOM_ST_GRADE(), prime.getMtPri());
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// c'est qu'il existe une prime pour cette date
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", ": une prime 1200 existe dejà dans le futur."));
			return false;
		}

		return true;
	}

	private boolean performCalculContractuel(Agent agent) throws Exception {
		ReturnMessageDto result = avancementService.isAvancementContractuel(getTransaction(), agent);

		if (result.getErrors().size() > 0) {
			String erreur = Const.CHAINE_VIDE;
			for (String err : result.getErrors()) {
				erreur += err;
			}
			getTransaction().declarerErreur(erreur);
			return false;
		}

		String anneeCourante = Services.dateDuJour().substring(6, 10);

		AvancementContractuels avct = avancementService.calculAvancementContractuel(getTransaction(), agent, anneeCourante, adsService, getFichePosteDao(), getAffectationDao(), true);
		if (avct == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", Const.CHAINE_VIDE));
			return false;
		} else if (avct.getIdAgent() == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// le nombre de point d'avancement du grade est 0.
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", ": le nombre de points d'avancement du grade de la FDP est 0"));
			return false;
		}else if (avct.getGrade() == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", ": le grade actuel de cet agent n'a pas de grade suivant"));
			return false;
		}

		// on recupere le grade et la filiere du poste
		Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(getAgentCourant().getIdAgent());
		FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
		// on cherche a quelle categorie appartient l'agent (A,B,A+..;)
		Grade grade = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
		GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), grade.getCodeGradeGenerique());
		FiliereGrade filiere = null;
		if (gg != null && gg.getIdCadreEmploi() != null && gg.getCdfili() != null) {
			filiere = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
		}

		// on rempli les champs
		addZone(getNOM_ST_GRADE(), avct.getIdNouvGrade());
		addZone(getNOM_ST_FILIERE(), filiere == null ? Const.CHAINE_VIDE : filiere.getLibFiliere());

		addZone(getNOM_EF_IBA(), avct.getNouvIban());
		addZone(getNOM_ST_INA(), avct.getNouvIna().toString());
		addZone(getNOM_ST_INM(), avct.getNouvInm().toString());
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(avct.getDateProchainGrade()));

		// on indique que les champs des fonctionnaires ne sont pas à afficher
		showAccBM = false;
		return true;
	}

	private boolean performCalculFonctionnaire(Agent agent) throws Exception {
		ReturnMessageDto result = avancementService.isAvancementFonctionnaire(getTransaction(), agent);

		if (result.getErrors().size() > 0) {
			String erreur = Const.CHAINE_VIDE;
			for (String err : result.getErrors()) {
				erreur += err;
			}
			getTransaction().declarerErreur(erreur);
			return false;
		}

		String anneeCourante = Services.dateDuJour().substring(6, 10);

		AvancementFonctionnaires avct = avancementService.calculAvancementFonctionnaire(getTransaction(), agent, anneeCourante, adsService, getFichePosteDao(), getAffectationDao(),
				getAutreAdministrationAgentDao(), getMotifAvancementDao(), getAvisCapDao(), true);
		if (avct == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", Const.CHAINE_VIDE));
			return false;
		} else if (avct.getIdAgent() == null) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			// "ERR189","Cet avancement ne peut être calculé @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR189", ": le grade actuel de cet agent n'a pas de grade suivant"));
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		addZone(getNOM_EF_DATE_DEBUT(), sdf.format(avct.getDateAvctMoy()));

		addZone(getNOM_EF_IBA(), avct.getNouvIban());
		addZone(getNOM_ST_INA(), avct.getNouvIna().toString());
		addZone(getNOM_ST_INM(), avct.getNouvInm().toString());

		addZone(getNOM_EF_BM_ANNEES(), avct.getNouvBmAnnee().toString());
		addZone(getNOM_EF_BM_MOIS(), avct.getNouvBmMois().toString());
		addZone(getNOM_EF_BM_JOURS(), avct.getNouvBmJour().toString());

		addZone(getNOM_EF_ACC_ANNEES(), avct.getNouvAccAnnee().toString());
		addZone(getNOM_EF_ACC_MOIS(), avct.getNouvAccMois().toString());
		addZone(getNOM_EF_ACC_JOURS(), avct.getNouvAccJour().toString());

		Grade gradeSuivant = Grade.chercherGrade(getTransaction(), avct.getIdNouvGrade());
		addZone(getNOM_ST_NOUV_GRADE(), avct.getIdNouvGrade());
		addZone(getNOM_ST_NOUV_GRADE_GEN(), gradeSuivant.getCodeGradeGenerique() == null || gradeSuivant.getCodeGradeGenerique().length() == 0 ? null : gradeSuivant.getCodeGradeGenerique());
		addZone(getNOM_ST_NOUV_CLASSE(), gradeSuivant.getCodeClasse() == null || gradeSuivant.getCodeClasse().length() == 0 ? null : gradeSuivant.getCodeClasse());
		addZone(getNOM_ST_NOUV_ECHELON(), gradeSuivant.getCodeEchelon() == null || gradeSuivant.getCodeEchelon().length() == 0 ? null : gradeSuivant.getCodeEchelon());

		addZone(getNOM_ST_GRADE(), avct.getGrade());
		FiliereGrade filiere = null;
		if (gradeSuivant.getCodeGradeGenerique() != null) {
			GradeGenerique gradeGeneriqueSuivant = GradeGenerique.chercherGradeGenerique(getTransaction(), gradeSuivant.getCodeGradeGenerique());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (gradeGeneriqueSuivant != null && gradeGeneriqueSuivant.getCdfili() != null) {
				filiere = FiliereGrade.chercherFiliereGrade(getTransaction(), gradeGeneriqueSuivant.getCdfili());
			}
		}
		addZone(getNOM_ST_FILIERE(), filiere == null ? Const.CHAINE_VIDE : filiere.getLibFiliere());

		// motif de l'avancement
		MotifAvancement motif = null;
		if (avct.getIdMotifAvct() != null) {
			try {
				motif = getMotifAvancementDao().chercherMotifAvancement(avct.getIdMotifAvct());
			} catch (Exception e) {
			}
		}
		addZone(getNOM_ST_TYPE_AVCT(), motif == null ? Const.CHAINE_VIDE : motif.getLibMotifAvct());

		// on indique que les champs des fonctionnaires ne sont pas a
		// afficher
		showAccBM = true;
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOUV_GRADE Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_NOUV_GRADE() {
		return "NOM_ST_NOUV_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOUV_GRADE
	 * Date de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_NOUV_GRADE() {
		return getZone(getNOM_ST_NOUV_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOUV_GRADE_GEN Date
	 * de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_NOUV_GRADE_GEN() {
		return "NOM_ST_NOUV_GRADE_GEN";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOUV_GRADE_GEN
	 * Date de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_NOUV_GRADE_GEN() {
		return getZone(getNOM_ST_NOUV_GRADE_GEN());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOUV_CLASSE Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_NOUV_CLASSE() {
		return "NOM_ST_NOUV_CLASSE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOUV_CLASSE
	 * Date de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_NOUV_CLASSE() {
		return getZone(getNOM_ST_NOUV_CLASSE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOUV_ECHELON Date de
	 * création : (28/09/11 10:20:42)
	 * 
	 */
	public String getNOM_ST_NOUV_ECHELON() {
		return "NOM_ST_NOUV_ECHELON";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOUV_ECHELON
	 * Date de création : (28/09/11 10:20:42)
	 * 
	 */
	public String getVAL_ST_NOUV_ECHELON() {
		return getZone(getNOM_ST_NOUV_ECHELON());
	}

	public boolean estDerniereCarriere() throws Exception {
		if (getCarriereCourante() != null) {
			Carriere derniereCarriere = null;
			if (getListeCarriere().size() != 0)
				derniereCarriere = getListeCarriere().get(getListeCarriere().size() - 1);
			if (derniereCarriere != null) {
				String dateDebCarr = getCarriereCourante().getDateDebut();
				String dateDebDernireCarr = derniereCarriere.getDateDebut();
				if (dateDebCarr.equals(dateDebDernireCarr)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean estChangementStatutCarriere() throws Exception {
		if (getCarriereCourante() != null) {
			Carriere derniereCarriere = null;
			if (getListeCarriere().size() > 1)
				derniereCarriere = getListeCarriere().get(getListeCarriere().size() - 2);
			if (derniereCarriere != null) {
				String statutCarr = getCarriereCourante().getCodeCategorie();
				String statutDernireCarr = derniereCarriere.getCodeCategorie();
				if (!statutCarr.equals(statutDernireCarr)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_GRADE(int i) {
		return "NOM_ST_GRADE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_GRADE(int i) {
		return getZone(getNOM_ST_GRADE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_CONTRAT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TYPE_CONTRAT(int i) {
		return "NOM_ST_TYPE_CONTRAT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_CONTRAT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TYPE_CONTRAT(int i) {
		return getZone(getNOM_ST_TYPE_CONTRAT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BASE_HORAIRE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_BASE_HORAIRE(int i) {
		return "NOM_ST_BASE_HORAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BASE_HORAIRE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_BASE_HORAIRE(int i) {
		return getZone(getNOM_ST_BASE_HORAIRE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_IBA Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_IBA(int i) {
		return "NOM_ST_IBA" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_IBA Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_IBA(int i) {
		return getZone(getNOM_ST_IBA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INA Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_INA(int i) {
		return "NOM_ST_INA" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INA Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_INA(int i) {
		return getZone(getNOM_ST_INA(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INM Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_INM(int i) {
		return "NOM_ST_INM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_INM(int i) {
		return getZone(getNOM_ST_INM(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DEBUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEBUT(int i) {
		return "NOM_ST_DEBUT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DEBUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEBUT(int i) {
		return getZone(getNOM_ST_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_FIN Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_FIN(int i) {
		return "NOM_ST_FIN" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_FIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_FIN(int i) {
		return getZone(getNOM_ST_FIN(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REF_ARR Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REF_ARR(int i) {
		return "NOM_ST_REF_ARR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REF_ARR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REF_ARR(int i) {
		return getZone(getNOM_ST_REF_ARR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_GRADE Date de
	 * création : (30/08/11 10:25:41)
	 * 
	 */
	public String getNOM_EF_GRADE() {
		return "NOM_EF_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_GRADE Date de création : (30/08/11 10:25:41)
	 * 
	 */
	public String getVAL_EF_GRADE() {
		return getZone(getNOM_EF_GRADE());
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 * RG_AG_CA_A07
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {
		// RG_AG_CA_A07

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		Carriere carriereCourante = (Carriere) getListeCarriere().get(indiceEltAModifier);
		setCarriereCourante(carriereCourante);

		if (!initialiseCarriereCourante(request))
			return false;

		StatutCarriere statut = (StatutCarriere) getHashStatut().get(getCarriereCourante().getCodeCategorie());
		// si statut = 7 ou 8 alors on affiche une liste partiel pour les grades
		if (statut.getCdCate().equals("7") || statut.getCdCate().equals("8")) {
			ArrayList<Grade> liste = Grade.listerGradeConvCol(getTransaction());
			setListeGrade(liste);
			afficherListeGrade();
		} else {
			setListeGrade(null);
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_VISUALISATION(int i) {
		return "NOM_PB_VISUALISATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_VISUALISATION(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		Carriere carriereCourante = (Carriere) getListeCarriere().get(indiceEltAConsulter);
		setCarriereCourante(carriereCourante);

		// init de la carriere courante
		if (!initialiseCarriereCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_VISUALISATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 * RG_AG_CA_A08
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		// RG_AG_CA_A08
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		Carriere carriereSelectionnee = (Carriere) getListeCarriere().get(indiceEltASuprimer);
		setCarriereCourante(carriereSelectionnee);

		if (!carriereSelectionnee.isActive()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR130"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// init de la carriere courante
		if (!initialiseCarriereCourante(request))
			return false;

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_INIT_TYPE_CONTRAT Date de
	 * création : (17/05/11 16:01:36)
	 * 
	 */
	public String getNOM_PB_INIT_TYPE_CONTRAT() {
		return "NOM_PB_INIT_TYPE_CONTRAT";
	}

	/**
	 * Initialise le champ de fin de période d'essai a partir de la date de
	 * début et du type de contrat. Date de création : (17/05/11 16:01:36)
	 * 
	 * RG_AG_CA_C04
	 */
	public boolean performPB_INIT_TYPE_CONTRAT(HttpServletRequest request) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		if (!getVAL_EF_DATE_DEBUT().equals(Const.CHAINE_VIDE) && Services.estUneDate(getVAL_EF_DATE_DEBUT())) {
			Contrat contrat = null;
			try {
				contrat = getContratDao().chercherContratAgentDateComprise(getAgentCourant().getIdAgent(), sdf.parse(getVAL_EF_DATE_DEBUT()));
			} catch (Exception e) {
				// pas de contrat
			}

			if (contrat != null && contrat.getIdTypeContrat() != null) {
				TypeContrat typeContrat = getTypeContratDao().chercherTypeContrat(contrat.getIdTypeContrat());
				addZone(getNOM_ST_CDICDD(), typeContrat.getLibTypeContrat());
			} else
				addZone(getNOM_ST_CDICDD(), Const.CHAINE_VIDE);
		}
		return true;
	}

	public String getNOM_ST_TYPE_AVCT() {
		return "NOM_ST_TYPE_AVCT";
	}

	public String getVAL_ST_TYPE_AVCT() {
		return getZone(getNOM_ST_TYPE_AVCT());
	}

	public MotifAvancementDao getMotifAvancementDao() {
		return motifAvancementDao;
	}

	public void setMotifAvancementDao(MotifAvancementDao motifAvancementDao) {
		this.motifAvancementDao = motifAvancementDao;
	}

	public MotifCarriereDao getMotifCarriereDao() {
		return motifCarriereDao;
	}

	public void setMotifCarriereDao(MotifCarriereDao motifCarriereDao) {
		this.motifCarriereDao = motifCarriereDao;
	}

	public TypeContratDao getTypeContratDao() {
		return typeContratDao;
	}

	public void setTypeContratDao(TypeContratDao typeContratDao) {
		this.typeContratDao = typeContratDao;
	}

	public ContratDao getContratDao() {
		return contratDao;
	}

	public void setContratDao(ContratDao contratDao) {
		this.contratDao = contratDao;
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

	public AutreAdministrationAgentDao getAutreAdministrationAgentDao() {
		return autreAdministrationAgentDao;
	}

	public void setAutreAdministrationAgentDao(AutreAdministrationAgentDao autreAdministrationAgentDao) {
		this.autreAdministrationAgentDao = autreAdministrationAgentDao;
	}

	public AvisCapDao getAvisCapDao() {
		return avisCapDao;
	}

	public void setAvisCapDao(AvisCapDao avisCapDao) {
		this.avisCapDao = avisCapDao;
	}
}
