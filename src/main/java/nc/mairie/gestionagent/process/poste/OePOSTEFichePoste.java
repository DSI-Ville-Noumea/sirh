package nc.mairie.gestionagent.process.poste;

import java.io.BufferedOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.BaseHorairePointage;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.NatureCredit;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.ActiviteFE;
import nc.mairie.metier.poste.ActiviteFP;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Budget;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFE;
import nc.mairie.metier.poste.CompetenceFP;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FEFP;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.NiveauEtudeFP;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.NiveauEtude;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.metier.referentiel.TypeContrat;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.AvantageNatureFP;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.DelegationFP;
import nc.mairie.metier.specificites.PrimePointageFP;
import nc.mairie.metier.specificites.RegIndemFP;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.ContratDao;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.parametrage.BaseHorairePointageDao;
import nc.mairie.spring.dao.metier.parametrage.NatureAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.NatureCreditDao;
import nc.mairie.spring.dao.metier.parametrage.TypeAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDelegationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeRegIndemnDao;
import nc.mairie.spring.dao.metier.poste.ActiviteDao;
import nc.mairie.spring.dao.metier.poste.ActiviteFEDao;
import nc.mairie.spring.dao.metier.poste.ActiviteFPDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.BudgetDao;
import nc.mairie.spring.dao.metier.poste.CompetenceDao;
import nc.mairie.spring.dao.metier.poste.CompetenceFEDao;
import nc.mairie.spring.dao.metier.poste.CompetenceFPDao;
import nc.mairie.spring.dao.metier.poste.FEFPDao;
import nc.mairie.spring.dao.metier.poste.FicheEmploiDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.HistoFichePosteDao;
import nc.mairie.spring.dao.metier.poste.NiveauEtudeFPDao;
import nc.mairie.spring.dao.metier.poste.StatutFPDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.metier.referentiel.NiveauEtudeDao;
import nc.mairie.spring.dao.metier.referentiel.TypeCompetenceDao;
import nc.mairie.spring.dao.metier.referentiel.TypeContratDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureFPDao;
import nc.mairie.spring.dao.metier.specificites.DelegationDao;
import nc.mairie.spring.dao.metier.specificites.DelegationFPDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnAffDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnFPDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableActivite;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.StatutEntiteEnum;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;
import nc.noumea.spring.service.PtgService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFichePoste Date de création : (07/07/11 10:59:29)
 * 
 * 
 */
public class OePOSTEFichePoste extends BasicProcess {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHE = 1;
	public static final int STATUT_A_DUPLIQUER = 2;
	public static final int STATUT_DUPLIQUER = 3;
	public static final int STATUT_SPECIFICITES = 5;
	public static final int STATUT_ACTI_PRINC = 6;
	public static final int STATUT_EMPLOI_PRIMAIRE = 7;
	public static final int STATUT_EMPLOI_SECONDAIRE = 8;
	public static final int STATUT_RESPONSABLE = 10;
	public static final int STATUT_RECHERCHE_AVANCEE = 11;
	public static final int STATUT_REMPLACEMENT = 12;
	public static final int STATUT_COMPETENCE = 13;
	public static final int STATUT_FICHE_EMPLOI = 14;
	public String ACTION_RECHERCHE = "Recherche.";
	public String ACTION_CREATION = "Création.";
	public String ACTION_DUPLICATION = "Duplication.";
	public String ACTION_MODIFICATION = "Modification.";
	public String ACTION_IMPRESSION = "Impression.";
	private String[] LB_TITRE_POSTE;
	private String[] LB_GRADE;
	private String[] LB_LOC;
	private String[] LB_BUDGET;
	private String[] LB_BUDGETE;
	private String[] LB_REGLEMENTAIRE;
	private String[] LB_STATUT;
	private String[] LB_NIVEAU_ETUDE;
	private String[] LB_NATURE_CREDIT;
	private String[] LB_BASE_HORAIRE_POINTAGE;
	private String[] LB_BASE_HORAIRE_ABSENCE;
	// nouvelle liste suite remaniement fdp/activites
	private ArrayList<Activite> listeToutesActi;
	// activites de la fiche emploi primaire
	private ArrayList<Activite> listeActiFEP;
	// activites de la fiche emploi secondaire
	private ArrayList<Activite> listeActiFES;
	// activites de la fiche poste
	private ArrayList<ActiviteFP> listeActiFP;
	// activites de la fiche poste ajoute
	private ArrayList<Activite> listeAjoutActiFP = new ArrayList<>();
	// nouvelle liste suite remaniement fdp/compétences
	private ArrayList<Competence> listeToutesComp;
	// competences de la fiche emploi primaire
	private ArrayList<Competence> listeCompFEP;
	// competences de la fiche emploi secondaire
	private ArrayList<Competence> listeCompFES;
	// competences de la fiche poste
	private ArrayList<CompetenceFP> listeCompFP;
	// competences de la fiche poste ajoute
	private ArrayList<Competence> listeAjoutCompFP;
	// Nouvelle gestion des niveau etude
	private ArrayList<NiveauEtude> listeTousNiveau;
	// niveau etude de la fiche poste
	private ArrayList<NiveauEtudeFP> listeNiveauFP;
	// pour les liste deroulante
	private ArrayList<NiveauEtude> listeNiveauEtude;
	private ArrayList<Budget> listeBudget;
	private ArrayList<StatutFP> listeStatut;
	private ArrayList<TitrePoste> listeTitre;
	private ArrayList<Grade> listeGrade;
	private ArrayList<EntiteGeo> listeLocalisation;
	private ArrayList<NatureCredit> listeNatureCredit;
	private ArrayList<BaseHorairePointage> listeBaseHorairePointage;
	private ArrayList<RefTypeSaisiCongeAnnuelDto> listeBaseHoraireAbsence;
	private ArrayList<AvantageNature> listeAvantage;
	private ArrayList<AvantageNature> listeAvantageAAjouter;
	private ArrayList<AvantageNature> listeAvantageASupprimer;
	private ArrayList<Delegation> listeDelegation;
	private ArrayList<Delegation> listeDelegationAAjouter;
	private ArrayList<Delegation> listeDelegationASupprimer;
	private ArrayList<RegimeIndemnitaire> listeRegime;
	private ArrayList<RegimeIndemnitaire> listeRegimeAAjouter;
	private ArrayList<RegimeIndemnitaire> listeRegimeASupprimer;
	private ArrayList<PrimePointageFP> listePrimePointageFP;
	private ArrayList<PrimePointageFP> listePrimePointageFPAAjouter;
	private ArrayList<PrimePointageFP> listePrimePointageFPASupprimer;
	private ArrayList<Horaire> listeHoraire;
	private String observation;
	private String mission;
	private boolean afficherListeGrade = false;
	private boolean afficherListeNivEt = false;
	private boolean fpCouranteAffectee = false;
	public HashMap<Integer, TypeAvantage> hashtypAv = null;
	public HashMap<Integer, NatureAvantage> hashNatAv = null;
	public HashMap<Integer, TypeDelegation> hashTypDel = null;
	public HashMap<Integer, TypeRegIndemn> hashTypRegIndemn = null;
	private HashMap<Integer, String> hashOrigineActivite;
	private HashMap<Integer, String> hashOrigineCompetence;
	private FichePoste fichePosteCourante;
	private Affectation affectationCourante;
	private Agent agentCourant;
	private Contrat contratCourant;
	private TypeContrat typeContratCourant;
	private FicheEmploi emploiPrimaire;
	private FicheEmploi emploiSecondaire;
	private FichePoste responsable;
	private Agent agtResponsable;
	private TitrePoste titrePosteResponsable;
	private FichePoste remplacement;
	private Agent agtRemplacement;
	private TitrePoste titrePosteRemplacement;
	private EntiteDto service;
	private BaseHorairePointage baseHorairePointageCourant;
	public String focus = null;
	private String urlFichier;
	private String messageInf = Const.CHAINE_VIDE;
	public boolean responsableObligatoire = false;
	private boolean changementFEAutorise = true;
	public boolean estFDPInactive = false;
	private PrimePointageFPDao primePointageFPDao;
	private NatureCreditDao natureCreditDao;
	private NatureAvantageDao natureAvantageDao;
	private TypeAvantageDao typeAvantageDao;
	private TypeDelegationDao typeDelegationDao;
	private TypeRegIndemnDao typeRegIndemnDao;
	private AvantageNatureDao avantageNatureDao;
	private AvantageNatureFPDao avantageNatureFPDao;
	private DelegationDao delegationDao;
	private DelegationFPDao delegationFPDao;
	private RegIndemnDao regIndemnDao;
	private RegIndemnFPDao regIndemnFPDao;
	private RegIndemnAffDao regIndemnAffDao;
	private TypeCompetenceDao typeCompetenceDao;
	private TypeContratDao typeContratDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private ContratDao contratDao;
	private NiveauEtudeDao niveauEtudeDao;
	private TitrePosteDao titrePosteDao;
	private StatutFPDao statutFPDao;
	private NiveauEtudeFPDao niveauEtudeFPDao;
	private BudgetDao budgetDao;
	private FEFPDao fefpDao;
	private CompetenceDao competenceDao;
	private CompetenceFPDao competenceFPDao;
	private CompetenceFEDao competenceFEDao;
	private ActiviteDao activiteDao;
	private ActiviteFEDao activiteFEDao;
	private ActiviteFPDao activiteFPDao;
	private FicheEmploiDao ficheEmploiDao;
	private FichePosteDao fichePosteDao;
	private HistoFichePosteDao histoFichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	private BaseHorairePointageDao baseHorairePointageDao;

	private Logger logger = LoggerFactory.getLogger(OePOSTEFichePoste.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private IAdsService adsService;

	private IAbsService absService;

	private IPtgService ptgService;

	private ISirhService sirhService;

	private IRadiService radiService;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (25/07/11 14:53:21)
	 * 
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		messageInf = Const.CHAINE_VIDE;

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		initialiseDao();
		initialiseListeDeroulante();

		// #17320 provient de la page Agent > Emplois > Affectations
		String numFP = (String) request.getParameter("numFP");
		if (null != numFP && !numFP.equals("")) {
			ArrayList<FichePoste> fp = getFichePosteDao().listerFichePosteAvecCriteresAvances(new ArrayList<Integer>(),
					null, null, numFP, null, false);
			setFichePosteCourante(fp.get(0));
			afficheFicheCourante();
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
		}

		if (etatStatut() == STATUT_RECHERCHE) {
			afficheFicheCourante();
		}

		if (etatStatut() == STATUT_ACTI_PRINC) {
			initialiseActivites();
		}

		if (etatStatut() == STATUT_COMPETENCE) {
			initialiseCompetence();
		}

		if (etatStatut() == STATUT_DUPLIQUER) {
			addZone(getNOM_ST_NUMERO(), getFichePosteCourante().getNumFp());
		}

		if (etatStatut() == STATUT_A_DUPLIQUER) {
			addZone(getNOM_ST_INFO_FP(), Const.CHAINE_VIDE);
			addZone(getNOM_EF_RECHERCHE(), Const.CHAINE_VIDE);
			afficheFicheCourante();
		}

		// Récupération de la fiche de poste en session
		if (etatStatut() == STATUT_RECHERCHE_AVANCEE) {
			FichePoste fpRechAvancee = (FichePoste) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_FICHE_POSTE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			if (fpRechAvancee != null) {
				viderFichePoste();
				viderObjetsFichePoste();
				setFichePosteCourante(fpRechAvancee);
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
			} else {
				setFichePosteCourante(new FichePoste());
			}
			afficheFicheCourante();
			return;
		}

		if (etatStatut() == STATUT_RESPONSABLE) {
			// Responsable hierarchique
			if ((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE) != null) {
				setResponsable((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE));
			} else {
				if (getFichePosteCourante() != null && getFichePosteCourante().getIdResponsable() != null) {
					setResponsable(getFichePosteDao().chercherFichePoste(getFichePosteCourante().getIdResponsable()));
				} else {
					setResponsable(null);
				}
			}
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			afficheResponsable();
			return;
		} else {
			if (getResponsable() == null && getFichePosteCourante() != null
					&& getFichePosteCourante().getIdResponsable() != null) {
				setResponsable(getFichePosteDao().chercherFichePoste(getFichePosteCourante().getIdResponsable()));
				afficheResponsable();
			}
		}

		if (etatStatut() == STATUT_REMPLACEMENT) {
			// Fiche de poste remplacee
			if ((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE) != null) {
				setRemplacement((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE));
			} else {
				if (getFichePosteCourante() != null && getFichePosteCourante().getIdRemplacement() != null) {
					setRemplacement(getFichePosteDao().chercherFichePoste(getFichePosteCourante().getIdRemplacement()));
				} else {
					setRemplacement(null);
				}
			}
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			afficheRemplacement();
			return;
		} else {
			if (getRemplacement() == null && getFichePosteCourante() != null
					&& getFichePosteCourante().getIdRemplacement() != null) {
				setRemplacement(getFichePosteDao().chercherFichePoste(getFichePosteCourante().getIdRemplacement()));
				afficheRemplacement();
			}
		}

		if (etatStatut() == STATUT_EMPLOI_SECONDAIRE) {
			FicheEmploi fes = (FicheEmploi) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			if (fes == null) {
				if (getEmploiSecondaire() == null) {
					setEmploiSecondaire(fes);
				}
			} else {
				setEmploiSecondaire(fes);
			}
			afficheFES();
			initialiseMission();
			initialiseInfoEmploi();
			// si changement de FES, on ajoute la mission à la mission actuelle
			if (getEmploiSecondaire() != null) {
				if (!getMission().toUpperCase().contains(getEmploiSecondaire().getDefinitionEmploi().toUpperCase())) {
					setMission(getMission() + " " + getEmploiSecondaire().getDefinitionEmploi());
				}
			}

			addZone(getNOM_EF_MISSIONS(), getMission() == null ? Const.CHAINE_VIDE : getMission());
			return;
		} else {
			if (getEmploiSecondaire() != null) {
				afficheFES();
			}
		}

		if (etatStatut() == STATUT_EMPLOI_PRIMAIRE) {
			FicheEmploi fep = (FicheEmploi) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			if (fep == null) {
				if (getEmploiPrimaire() == null) {
					setEmploiPrimaire(fep);
				}
			} else {
				setEmploiPrimaire(fep);
			}

			afficheFEP();
			initialiseMission();
			initialiseInfoEmploi();
			// si changement de FES, on ajoute la mission à la mission actuelle
			if (getEmploiPrimaire() != null) {
				if (!getMission().toUpperCase().contains(getEmploiPrimaire().getDefinitionEmploi().toUpperCase())) {
					setMission(getMission() + " " + getEmploiPrimaire().getDefinitionEmploi());
				}
			}

			addZone(getNOM_EF_MISSIONS(), getMission() == null ? Const.CHAINE_VIDE : getMission());
			return;
		} else {
			if (getEmploiPrimaire() == null && getFichePosteCourante() != null
					&& getFichePosteCourante().getIdFichePoste() != null) {

				// Recherche de tous les liens FicheEmploi / FichePoste
				ArrayList<FEFP> liens = getFefpDao().listerFEFPAvecFP(getFichePosteCourante().getIdFichePoste());
				setEmploiPrimaire(getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(true, liens));
				afficheFEP();
			}
		}

		if (etatStatut() == STATUT_SPECIFICITES) {
			// Affiche les spécificités de la fiche de poste
			initialiseSpecificites();
			afficheSpecificites();
			return;
		}

		// Init a l'action Recherche lors du premier acces.
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);
			setFocus(getNOM_EF_RECHERCHE());
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getPrimePointageFPDao() == null) {
			setPrimePointageFPDao(new PrimePointageFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNatureCreditDao() == null) {
			setNatureCreditDao(new NatureCreditDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNatureAvantageDao() == null) {
			setNatureAvantageDao(new NatureAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeAvantageDao() == null) {
			setTypeAvantageDao(new TypeAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeDelegationDao() == null) {
			setTypeDelegationDao(new TypeDelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeRegIndemnDao() == null) {
			setTypeRegIndemnDao(new TypeRegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvantageNatureDao() == null) {
			setAvantageNatureDao(new AvantageNatureDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvantageNatureFPDao() == null) {
			setAvantageNatureFPDao(new AvantageNatureFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDelegationDao() == null) {
			setDelegationDao(new DelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDelegationFPDao() == null) {
			setDelegationFPDao(new DelegationFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnDao() == null) {
			setRegIndemnDao(new RegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnFPDao() == null) {
			setRegIndemnFPDao(new RegIndemnFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnAffDao() == null) {
			setRegIndemnAffDao(new RegIndemnAffDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeCompetenceDao() == null) {
			setTypeCompetenceDao(new TypeCompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeContratDao() == null) {
			setTypeContratDao(new TypeContratDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getContratDao() == null) {
			setContratDao(new ContratDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNiveauEtudeDao() == null) {
			setNiveauEtudeDao(new NiveauEtudeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getStatutFPDao() == null) {
			setStatutFPDao(new StatutFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getNiveauEtudeFPDao() == null) {
			setNiveauEtudeFPDao(new NiveauEtudeFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getBudgetDao() == null) {
			setBudgetDao(new BudgetDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFefpDao() == null) {
			setFefpDao(new FEFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCompetenceDao() == null) {
			setCompetenceDao(new CompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCompetenceFPDao() == null) {
			setCompetenceFPDao(new CompetenceFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCompetenceFEDao() == null) {
			setCompetenceFEDao(new CompetenceFEDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteDao() == null) {
			setActiviteDao(new ActiviteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteFEDao() == null) {
			setActiviteFEDao(new ActiviteFEDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteFPDao() == null) {
			setActiviteFPDao(new ActiviteFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFicheEmploiDao() == null) {
			setFicheEmploiDao(new FicheEmploiDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFichePosteDao() == null) {
			setFichePosteDao(new FichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoFichePosteDao() == null) {
			setHistoFichePosteDao(new HistoFichePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getBaseHorairePointageDao() == null) {
			setBaseHorairePointageDao(new BaseHorairePointageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (AdsService) context.getBean("adsService");
		}
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
	}

	/**
	 * Initialise les zones de la fiche poste courante.
	 * 
	 * @throws Exception
	 */
	private void afficheFicheCourante() throws Exception {

		// FICHE POSTE
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// si FDP inactive alors on rend les champs disabled
			StatutFP statut = getStatutFPDao().chercherStatutFP(getFichePosteCourante().getIdStatutFp());
			if (statut.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
				estFDPInactive = true;
			} else {
				estFDPInactive = false;
			}

			// SERVICE
			if (getService() != null) {
				addZone(getNOM_EF_SERVICE(), getService().getSigle());
				addZone(getNOM_ST_ID_SERVICE_ADS(), String.valueOf(getService().getIdEntite()));
				String infoService = getService().getLabel().replace("\'", " ");
				addZone(getNOM_ST_INFO_SERVICE(), infoService);
			}

			// FICHE EMPLOI PRIMAIRE
			afficheFEP();

			// FICHE EMPLOI SECONDAIRE
			afficheFES();

			// OBSERVATION
			initialiseObservation();

			// MISSION
			initialiseMission();

			// INFOS
			afficheInfosAffectationFP();
			addZone(getNOM_EF_ANNEE(), getFichePosteCourante().getAnneeCreation().toString());
			addZone(getNOM_ST_NUMERO(), getFichePosteCourante().getNumFp());
			addZone(getNOM_EF_DATE_DEBUT_APPLI_SERV(),
					getFichePosteCourante().getDateDebAppliServ() == null ? Const.CHAINE_VIDE : sdf
							.format(getFichePosteCourante().getDateDebAppliServ()));
			addZone(getNOM_EF_NFA(), getFichePosteCourante().getNfa());
			addZone(getNOM_EF_OPI(), getFichePosteCourante().getOpi());
			addZone(getNOM_EF_NUM_DELIBERATION(), getFichePosteCourante().getNumDeliberation());
			if (getFichePosteCourante().getCodeGrade() != null) {
				Grade g = Grade.chercherGrade(getTransaction(), getFichePosteCourante().getCodeGrade());
				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
				addZone(getNOM_EF_GRADE(), g.getGrade());
				// on récupere la categorie et la filiere de ce grade
				if (gg.getCodCadre() != null && (!gg.getCodCadre().equals(Const.CHAINE_VIDE))) {
					String info = "Catégorie : " + gg.getCodCadre();

					if (gg.getIdCadreEmploi() != null) {
						if (gg.getCdfili() != null) {
							FiliereGrade fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
							if (fi == null || getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							} else {
								info += " <br/> Filière : " + fi.getLibFiliere();
							}
						}
					}
					addZone(getNOM_ST_INFO_GRADE(), info);
				}
			}
			addZone(getNOM_EF_CODE_GRADE(), getFichePosteCourante().getCodeGrade());

			if (getListeStatut() != null) {
				for (int i = 0; i < getListeStatut().size(); i++) {
					StatutFP s = (StatutFP) getListeStatut().get(i);
					if (s.getIdStatutFp().toString().equals(getFichePosteCourante().getIdStatutFp().toString())) {
						addZone(getNOM_LB_STATUT_SELECT(), String.valueOf(i));
						initialiseChampObligatoire(s);
						break;
					}
				}
			}

			if (getListeBudget() != null) {
				for (int i = 0; i < getListeBudget().size(); i++) {
					Budget b = (Budget) getListeBudget().get(i);
					if (b.getIdBudget().toString().equals(getFichePosteCourante().getIdBudget().toString())) {
						addZone(getNOM_LB_BUDGET_SELECT(), String.valueOf(i));
						break;
					}
				}
			}

			if (getListeHoraire() != null) {
				for (int i = 0; i < getListeHoraire().size(); i++) {
					Horaire h = (Horaire) getListeHoraire().get(i);
					if (h.getCdtHor().equals(getFichePosteCourante().getIdCdthorReg().toString())) {
						addZone(getNOM_LB_REGLEMENTAIRE_SELECT(), String.valueOf(i));
					}
					if (h.getCdtHor().equals(getFichePosteCourante().getIdCdthorBud().toString())) {
						addZone(getNOM_LB_BUDGETE_SELECT(), String.valueOf(i));
					}
				}
			}

			if (getListeLocalisation() != null) {
				int i = 1;
				for (EntiteGeo eg : getListeLocalisation()) {
					if (eg.getIdEntiteGeo().equals(getFichePosteCourante().getIdEntiteGeo().toString())) {
						addZone(getNOM_LB_LOC_SELECT(), String.valueOf(i));
						break;
					}
					i++;
				}
			}

			if (getListeTitre() != null) {
				for (TitrePoste tp : getListeTitre()) {
					if (tp.getIdTitrePoste().toString().equals(getFichePosteCourante().getIdTitrePoste().toString())) {
						addZone(getNOM_EF_TITRE_POSTE(), tp.getLibTitrePoste());
						break;
					}
				}
			}

			if (getListeNatureCredit() != null && getFichePosteCourante().getIdNatureCredit() != null) {
				for (int i = 0; i < getListeNatureCredit().size(); i++) {
					NatureCredit b = (NatureCredit) getListeNatureCredit().get(i);
					if (b.getIdNatureCredit().toString().equals(getFichePosteCourante().getIdNatureCredit().toString())) {
						addZone(getNOM_LB_NATURE_CREDIT_SELECT(), String.valueOf(i));
						break;
					}
				}
			} else {
				addZone(getNOM_LB_NATURE_CREDIT_SELECT(), Const.ZERO);
			}

			// base horaire pointage
			if (getListeBaseHorairePointage() != null && getFichePosteCourante().getIdBaseHorairePointage() != null) {
				for (int i = 0; i < getListeBaseHorairePointage().size(); i++) {
					BaseHorairePointage b = (BaseHorairePointage) getListeBaseHorairePointage().get(i);
					if (b.getIdBaseHorairePointage().toString()
							.equals(getFichePosteCourante().getIdBaseHorairePointage().toString())) {
						addZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT(), String.valueOf(i + 1));
						break;
					}
				}
			} else {
				addZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT(), Const.ZERO);
			}

			// base horaire absence
			if (getListeBaseHoraireAbsence() != null && getFichePosteCourante().getIdBaseHoraireAbsence() != null) {
				for (int i = 0; i < getListeBaseHoraireAbsence().size(); i++) {
					RefTypeSaisiCongeAnnuelDto b = (RefTypeSaisiCongeAnnuelDto) getListeBaseHoraireAbsence().get(i);
					if (b.getIdRefTypeSaisiCongeAnnuel().toString()
							.equals(getFichePosteCourante().getIdBaseHoraireAbsence().toString())) {
						addZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT(), String.valueOf(i + 1));
						break;
					}
				}
			} else {
				addZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT(), Const.ZERO);
			}

			afficheResponsable();
			afficheRemplacement();

			// Spécificités
			initialiseSpecificites();
			afficheSpecificites();
		}
	}

	/**
	 * Affiche la FicheEmploi secondaire.
	 */
	private void afficheFES() throws Exception {
		addZone(getNOM_ST_EMPLOI_SECONDAIRE(), getEmploiSecondaire() == null ? Const.CHAINE_VIDE
				: getEmploiSecondaire().getRefMairie());
	}

	/**
	 * Affiche la FicheEmploi primaire.
	 */
	private void afficheFEP() throws Exception {
		if (getEmploiPrimaire() != null) {
			addZone(getNOM_ST_EMPLOI_PRIMAIRE(), getEmploiPrimaire().getRefMairie());
		} else {
			addZone(getNOM_ST_EMPLOI_PRIMAIRE(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Affiche la fiche de poste "Responsable"
	 */
	private void afficheResponsable() {
		if (getResponsable() != null) {
			addZone(getNOM_ST_RESPONSABLE(), getResponsable().getNumFp());
			if (getAgtResponsable() != null) {
				addZone(getNOM_ST_INFO_RESP(), getAgtResponsable().getNomAgent() + " "
						+ getAgtResponsable().getPrenomAgent() + " (" + getAgtResponsable().getNomatr() + ") - "
						+ getTitrePosteResponsable().getLibTitrePoste());
			} else {
				addZone(getNOM_ST_INFO_RESP(), "Cette fiche de poste (" + getTitrePosteResponsable().getLibTitrePoste()
						+ ") n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_RESPONSABLE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_INFO_RESP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Affiche la fiche de poste "Remplacement"
	 */
	private void afficheRemplacement() {
		if (getRemplacement() != null) {
			addZone(getNOM_ST_REMPLACEMENT(), getRemplacement().getNumFp());
			if (getAgtRemplacement() != null) {
				addZone(getNOM_ST_INFO_REMP(), getAgtRemplacement().getNomAgent() + " "
						+ getAgtRemplacement().getPrenomAgent() + " (" + getAgtRemplacement().getNomatr() + ") - "
						+ getTitrePosteRemplacement().getLibTitrePoste());
			} else {
				addZone(getNOM_ST_INFO_REMP(), "Cette fiche de poste ("
						+ getTitrePosteRemplacement().getLibTitrePoste() + ") n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_REMPLACEMENT(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Affiche infos affectation FichePoste.
	 */
	private void afficheInfosAffectationFP() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (getFichePosteCourante() != null && getAgentCourant() != null && getAffectationCourante() != null) {
			String chaine = "Cette fiche de poste est affectée a l'agent " + getAgentCourant().getNomAgent() + " "
					+ getAgentCourant().getPrenomAgent() + " (" + getAgentCourant().getNomatr() + ") depuis le "
					+ sdf.format(getAffectationCourante().getDateDebutAff());
			if (getContratCourant() != null && getContratCourant().getIdContrat() != null) {
				chaine += " (" + getTypeContratCourant().getLibTypeContrat() + " depuis le "
						+ sdf.format(getContratCourant().getDatdeb());
				if (getContratCourant().getDateFin() != null) {
					chaine += " jusqu'au " + sdf.format(getContratCourant().getDateFin()) + ")";
				} else {
					chaine += ")";
				}
			}

			addZone(getNOM_ST_INFO_FP(), chaine);
		} else {
			addZone(getNOM_ST_INFO_FP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Initialise les listes deroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste type budget vide alors affectation
		if (getLB_BUDGET() == LBVide) {
			ArrayList<Budget> budget = (ArrayList<Budget>) getBudgetDao().listerBudget();
			setListeBudget(budget);

			if (getListeBudget().size() != 0) {
				int[] tailles = { 20 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<Budget> list = getListeBudget().listIterator(); list.hasNext();) {
					Budget de = (Budget) list.next();
					String ligne[] = { de.getLibBudget() };
					aFormat.ajouteLigne(ligne);
				}
				setLB_BUDGET(aFormat.getListeFormatee());
			} else {
				setLB_BUDGET(null);
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
				setLB_STATUT(aFormat.getListeFormatee());
			} else {
				setLB_STATUT(null);
			}
			addZone(getNOM_LB_STATUT_SELECT(), Const.ZERO);
		}

		// Si liste localisation vide alors affectation
		if (getLB_LOC() == LBVide) {
			ArrayList<EntiteGeo> loc = EntiteGeo.listerEntiteGeo(getTransaction());
			setListeLocalisation(loc);

			int[] tailles = { 100 };
			String[] champs = { "libEntiteGeo" };
			setLB_LOC(new FormateListe(tailles, loc, champs).getListeFormatee(true));
		}

		// Si liste titre poste vide alors affectation
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

		// Si liste grade vide alors affectation
		if (getLB_GRADE() == LBVide) {
			ArrayList<Grade> grade = Grade.listerGradeInitialActif(getTransaction());
			setListeGrade(grade);

			int[] tailles = { 100 };
			String[] champs = { "grade" };
			setLB_GRADE(new FormateListe(tailles, grade, champs).getListeFormatee(true));
		}

		// Si liste niveau etude vide alors affectation
		if (getLB_NIVEAU_ETUDE() == LBVide) {
			ArrayList<NiveauEtude> niveau = (ArrayList<NiveauEtude>) getNiveauEtudeDao().listerNiveauEtude();
			setListeNiveauEtude(niveau);
			if (getListeNiveauEtude().size() != 0) {
				int tailles[] = { 15 };
				FormateListe aFormat = new FormateListe(tailles);
				for (ListIterator<NiveauEtude> list = getListeNiveauEtude().listIterator(); list.hasNext();) {
					NiveauEtude nature = (NiveauEtude) list.next();
					String ligne[] = { nature.getCodeNiveauEtude() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_NIVEAU_ETUDE(aFormat.getListeFormatee(true));
			} else {
				setLB_NIVEAU_ETUDE(null);
			}
		}

		// Si liste diplomes vide alors affectation
		if (getLB_REGLEMENTAIRE() == LBVide) {
			ArrayList<Horaire> hor = Horaire.listerHoraire(getTransaction());
			setListeHoraire(hor);

			int[] tailles = { 100 };
			String[] champs = { "libHor" };
			setLB_REGLEMENTAIRE(new FormateListe(tailles, hor, champs).getListeFormatee());
		}

		// Si liste diplomes vide alors affectation
		if (getLB_BUDGETE() == LBVide) {
			int[] tailles = { 100 };
			String[] champs = { "libHor" };
			setLB_BUDGETE(new FormateListe(tailles, getListeHoraire(), champs).getListeFormatee());
		}

		// Si liste nature credit vide alors affectation
		if (getLB_NATURE_CREDIT() == LBVide) {
			ArrayList<NatureCredit> listeNature = getNatureCreditDao().listerNatureCreditOrderBy();
			setListeNatureCredit(listeNature);
			if (getListeNatureCredit().size() != 0) {
				int tailles[] = { 50 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<NatureCredit> list = getListeNatureCredit().listIterator(); list.hasNext();) {
					NatureCredit nature = (NatureCredit) list.next();
					String ligne[] = { nature.getLibNatureCredit() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_NATURE_CREDIT(aFormat.getListeFormatee());
			} else {
				setLB_NATURE_CREDIT(null);
			}
		}

		// Si liste base horaire pointage vide alors affectation
		if (getLB_BASE_HORAIRE_POINTAGE() == LBVide) {
			ArrayList<BaseHorairePointage> listeBaseHorairePointage = (ArrayList<BaseHorairePointage>) getBaseHorairePointageDao()
					.listerBaseHorairePointageOrderByCode();
			setListeBaseHorairePointage(listeBaseHorairePointage);
			if (getListeBaseHorairePointage().size() != 0) {
				int tailles[] = { 5, 50 };
				String padding[] = { "G", "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<BaseHorairePointage> list = getListeBaseHorairePointage().listIterator(); list
						.hasNext();) {
					BaseHorairePointage base = (BaseHorairePointage) list.next();
					String ligne[] = { base.getCodeBaseHorairePointage(), base.getLibelleBaseHorairePointage() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_BASE_HORAIRE_POINTAGE(aFormat.getListeFormatee(true));
			} else {
				setLB_BASE_HORAIRE_POINTAGE(null);
			}
		}

		// Si liste base horaire absence vide alors affectation
		if (getLB_BASE_HORAIRE_ABSENCE() == LBVide) {
			List<TypeAbsenceDto> listeTypeAbsence = absService
					.getListeRefTypeAbsenceDto(EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue());

			ArrayList<RefTypeSaisiCongeAnnuelDto> liste = new ArrayList<RefTypeSaisiCongeAnnuelDto>();

			for (TypeAbsenceDto abs : listeTypeAbsence) {
				liste.add(abs.getTypeSaisiCongeAnnuelDto());
			}
			setListeBaseHoraireAbsence(liste);
			if (getListeBaseHoraireAbsence().size() != 0) {
				int tailles[] = { 5 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<RefTypeSaisiCongeAnnuelDto> list = getListeBaseHoraireAbsence().listIterator(); list
						.hasNext();) {
					RefTypeSaisiCongeAnnuelDto base = (RefTypeSaisiCongeAnnuelDto) list.next();
					String ligne[] = { base.getCodeBaseHoraireAbsence() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_BASE_HORAIRE_ABSENCE(aFormat.getListeFormatee(true));
			} else {
				setLB_BASE_HORAIRE_ABSENCE(null);
			}
		}
	}

	/**
	 * Récupere les compétences choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiseCompetence() throws Exception {

		// on fait une liste de toutes les competences
		setListeToutesComp(new ArrayList<Competence>());
		boolean trouve = false;
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les competences de la FDP
			setListeCompFP(getCompetenceFPDao().listerCompetenceFPAvecFP(getFichePosteCourante().getIdFichePoste()));
			for (CompetenceFP compFP : getListeCompFP()) {
				trouve = false;
				Competence competence = getCompetenceDao().chercherCompetence(compFP.getIdCompetence());
				for (Competence tteComp : getListeToutesComp()) {
					if (tteComp.getIdCompetence() == competence.getIdCompetence()) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesComp().add(competence);
					getHashOrigineCompetence().put(competence.getIdCompetence(), "FDP");
				}
			}
		} else {
			setListeCompFP(new ArrayList<CompetenceFP>());
		}

		// on recupere les competences des differentes FE
		trouve = false;
		if (getEmploiPrimaire() != null && getEmploiPrimaire().getIdFicheEmploi() != null) {
			// Recherche de tous les liens FicheEmploi / Competence
			ArrayList<CompetenceFE> liens = getCompetenceFEDao().listerCompetenceFEAvecFE(
					getEmploiPrimaire().getIdFicheEmploi());
			setListeCompFEP(getCompetenceDao().listerCompetenceAvecFE(liens));
			for (Competence compFP : getListeCompFEP()) {
				trouve = false;
				for (Competence tteComp : getListeToutesComp()) {
					if (tteComp.getIdCompetence() == compFP.getIdCompetence()) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesComp().add(compFP);
					getHashOrigineCompetence().put(compFP.getIdCompetence(), getEmploiPrimaire().getRefMairie());
				}
			}
		} else {
			setListeCompFEP(new ArrayList<Competence>());
		}

		if (getEmploiSecondaire() != null && getEmploiSecondaire().getIdFicheEmploi() != null) {
			trouve = false;
			// Recherche de tous les liens FicheEmploi / Competence
			ArrayList<CompetenceFE> liens = getCompetenceFEDao().listerCompetenceFEAvecFE(
					getEmploiSecondaire().getIdFicheEmploi());
			setListeCompFES(getCompetenceDao().listerCompetenceAvecFE(liens));
			for (Competence compFP : getListeCompFES()) {
				trouve = false;
				for (Competence tteActi : getListeToutesComp()) {
					if (tteActi.getIdCompetence() == compFP.getIdCompetence()) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesComp().add(compFP);
					getHashOrigineCompetence().put(compFP.getIdCompetence(), getEmploiSecondaire().getRefMairie());
				}
			}

		} else {
			setListeCompFES(new ArrayList<Competence>());
		}

		// on recupere les activites selectionnées dans l'ecran de selection
		@SuppressWarnings("unchecked")
		ArrayList<Competence> listeCompSelect = (ArrayList<Competence>) VariablesActivite.recuperer(this, "COMPETENCE");

		if (listeCompSelect != null && !listeCompSelect.isEmpty() && getListeAjoutCompFP() != null) {
			getListeAjoutCompFP().addAll(listeCompSelect);

		}
		for (Competence c : getListeAjoutCompFP()) {
			if (c != null) {
				if (getListeToutesComp() == null) {
					setListeToutesComp(new ArrayList<Competence>());
				}
				if (!getListeToutesComp().contains(c)) {
					getListeToutesComp().add(c);
					getHashOrigineCompetence().put(c.getIdCompetence(), "FDP");
				}
			}
		}

		// Si liste competences vide alors initialisation.
		boolean dejaCoche = false;
		for (int i = 0; i < getListeToutesComp().size(); i++) {
			dejaCoche = false;
			Competence competence = (Competence) getListeToutesComp().get(i);
			TypeCompetence typeComp = getTypeCompetenceDao().chercherTypeCompetence(competence.getIdTypeCompetence());
			String origineComp = (String) getHashOrigineCompetence().get(competence.getIdCompetence());

			if (competence != null) {
				addZone(getNOM_ST_ID_COMP(i), competence.getIdCompetence().toString());
				addZone(getNOM_ST_LIB_COMP(i), competence.getNomCompetence());
				addZone(getNOM_ST_TYPE_COMP(i), typeComp.getLibTypeCompetence());
				addZone(getNOM_ST_LIB_ORIGINE_COMP(i), origineComp);
				addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());

				if (getListeCompFP() != null) {
					// si la competence fait partie de la liste des competences
					// de la FDP
					for (int j = 0; j < getListeCompFP().size(); j++) {
						CompetenceFP compFP = (CompetenceFP) getListeCompFP().get(j);
						Competence competenceFP = getCompetenceDao().chercherCompetence(compFP.getIdCompetence());
						if (competenceFP.getIdCompetence().toString().equals(competence.getIdCompetence().toString())) {
							addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
							}
						}
					}
				} else {
					addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
				}
				if (getListeAjoutCompFP() != null) {
					// si la competence fait partie de la liste des competences
					// ajoutées à la FDP
					for (int j = 0; j < getListeAjoutCompFP().size(); j++) {
						Competence competenceFP = (Competence) getListeAjoutCompFP().get(j);
						if (competenceFP.getIdCompetence().toString().equals(competence.getIdCompetence().toString())) {
							addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
							}
						}
					}

				} else {
					addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
				}

			}
		}

		VariablesActivite.enlever(this, "COMPETENCE");
		VariablesActivite.enlever(this, "LISTECOMPETENCESAVOIR");
		VariablesActivite.enlever(this, "LISTECOMPETENCESAVOIRFAIRE");
		VariablesActivite.enlever(this, "LISTECOMPETENCECOMPORTEMENT");
	}

	/**
	 * Récupere les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void initialiseSpecificites() throws Exception {
		// Avantages en nature
		setListeAvantage((ArrayList<AvantageNature>) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_LST_AV_NATURE));
		if (getListeAvantage() != null && getListeAvantage().size() > 0) {
			setListeAvantageAAjouter((ArrayList<AvantageNature>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_AV_NATURE_A_AJOUT));
			setListeAvantageASupprimer((ArrayList<AvantageNature>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_AV_NATURE_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListeAvantage(getAvantageNatureDao().listerAvantageNatureAvecFP(
					getFichePosteCourante().getIdFichePoste()));
		}
		if (getListeAvantage() != null) {
			for (ListIterator<AvantageNature> list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() != null ? getNatureAvantageDao()
							.chercherNatureAvantage(aAvNat.getIdNatureAvantage()) : null;
					getHashtypAv().put(typAv.getIdTypeAvantage(), typAv);
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						getHashNatAv().put(natAv.getIdNatureAvantage(), natAv);
				}
			}
		}

		// délégations
		setListeDelegation((ArrayList<Delegation>) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation() != null && getListeDelegation().size() > 0) {
			setListeDelegationAAjouter((ArrayList<Delegation>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_DELEGATION_A_AJOUT));
			setListeDelegationASupprimer((ArrayList<Delegation>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_DELEGATION_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListeDelegation(getDelegationDao().listerDelegationAvecFP(getFichePosteCourante().getIdFichePoste()));
		}
		if (getListeDelegation() != null) {
			for (ListIterator<Delegation> list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());
					getHashTypDel().put(typDel.getIdTypeDelegation(), typDel);
				}
			}
		}

		// Régimes indemnitaires
		setListeRegime((ArrayList<RegimeIndemnitaire>) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_LST_REG_INDEMN));
		if (getListeRegime() != null && getListeRegime().size() > 0) {
			setListeRegimeAAjouter((ArrayList<RegimeIndemnitaire>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_AJOUT));
			setListeRegimeASupprimer((ArrayList<RegimeIndemnitaire>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListeRegime(getRegIndemnDao().listerRegimeIndemnitaireAvecFP(getFichePosteCourante().getIdFichePoste()));
		}
		if (getListeRegime() != null) {
			for (ListIterator<RegimeIndemnitaire> list = getListeRegime().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());
					getHashTypRegIndemn().put(typReg.getIdTypeRegIndemn(), typReg);
				}
			}
		}

		// Primes pointage
		ArrayList<PrimePointageFP> listeRecup = (ArrayList<PrimePointageFP>) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE);
		setListePrimePointageFP(listeRecup);
		if (getListePrimePointageFP() != null && getListePrimePointageFP().size() > 0) {
			setListePrimePointageFPAAjouter((ArrayList<PrimePointageFP>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_AJOUT));
			setListePrimePointageFPASupprimer((ArrayList<PrimePointageFP>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_SUPPR));
		} else if (etatStatut() == STATUT_SPECIFICITES && listeRecup.size() == 0) {
			setListePrimePointageFPASupprimer((ArrayList<PrimePointageFP>) VariablesActivite.recuperer(this,
					VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListePrimePointageFP(getPrimePointageFPDao().listerPrimePointageFP(
					getFichePosteCourante().getIdFichePoste()));
		}

	}

	/**
	 * Affiche les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	private void afficheSpecificites() throws Exception {
		// Avantages en nature
		int indiceAvantage = 0;
		if (getListeAvantage() != null) {
			for (AvantageNature aAvNat : getListeAvantage()) {
				addZone(getNOM_ST_AV_TYPE(indiceAvantage),
						getHashtypAv().get(aAvNat.getIdTypeAvantage()).getLibTypeAvantage().equals(Const.CHAINE_VIDE) ? "&nbsp;"
								: getHashtypAv().get(aAvNat.getIdTypeAvantage()).getLibTypeAvantage());
				addZone(getNOM_ST_AV_MNT(indiceAvantage), aAvNat.getMontant().toString());
				addZone(getNOM_ST_AV_NATURE(indiceAvantage), aAvNat.getIdNatureAvantage() == null ? "&nbsp;"
						: getHashNatAv().get(aAvNat.getIdNatureAvantage()).getLibNatureAvantage());
				indiceAvantage++;
			}
		}

		// délégations
		int indiceDelegation = 0;
		if (getListeDelegation() != null) {
			for (Delegation aDel : getListeDelegation()) {
				addZone(getNOM_ST_DEL_TYPE(indiceDelegation),
						getHashTypDel().get(aDel.getIdTypeDelegation()).getLibTypeDelegation()
								.equals(Const.CHAINE_VIDE) ? "&nbsp;" : getHashTypDel().get(aDel.getIdTypeDelegation())
								.getLibTypeDelegation());
				addZone(getNOM_ST_DEL_COMMENTAIRE(indiceDelegation),
						aDel.getLibDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aDel.getLibDelegation());
				indiceDelegation++;
			}
		}

		// Régimes indemnitaires
		int indiceRegime = 0;
		if (getListeRegime() != null) {
			for (RegimeIndemnitaire aReg : getListeRegime()) {
				addZone(getNOM_ST_REG_TYPE(indiceRegime),
						getHashTypRegIndemn().get(aReg.getIdTypeRegIndemn()).getLibTypeRegIndemn()
								.equals(Const.CHAINE_VIDE) ? "&nbsp;" : getHashTypRegIndemn().get(
								aReg.getIdTypeRegIndemn()).getLibTypeRegIndemn());
				addZone(getNOM_ST_REG_FORFAIT(indiceRegime), aReg.getForfait().toString());
				addZone(getNOM_ST_REG_NB_PTS(indiceRegime), aReg.getNombrePoints().toString());
				indiceRegime++;
			}
		}

		// Prime de pointage
		int indicePrime = 0;
		if (getListePrimePointageFP() != null) {
			for (PrimePointageFP prime : getListePrimePointageFP()) {
				RefPrimeDto rubr = null;
				rubr = ptgService.getPrimeDetail(prime.getNumRubrique());
				addZone(getNOM_ST_PP_RUBR(indicePrime), rubr.getNumRubrique() + " - " + rubr.getLibelle());

				indicePrime++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Vide les champs du formulaire.
	 * 
	 * @throws Exception
	 */
	private void viderFichePoste() throws Exception {

		addZone(getNOM_EF_ANNEE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MISSIONS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_OBSERVATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RECHERCHE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RECHERCHE_BY_AGENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TITRE_POSTE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT_APPLI_SERV(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_INFO_FP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_EMPLOI_PRIMAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_EMPLOI_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NFA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NUMERO(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_OPI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NUM_DELIBERATION(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_RESPONSABLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_GRADE(), Const.CHAINE_VIDE);

		addZone(getNOM_LB_STATUT_SELECT(), "0");
		addZone(getNOM_LB_BUDGET_SELECT(), "0");
		addZone(getNOM_LB_BUDGETE_SELECT(), "0");
		addZone(getNOM_LB_REGLEMENTAIRE_SELECT(), "0");
		addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), "0");
		addZone(getNOM_LB_LOC_SELECT(), "0");
		addZone(getNOM_LB_TITRE_POSTE_SELECT(), "0");
		addZone(getNOM_LB_GRADE_SELECT(), "0");
		addZone(getNOM_ST_INFO_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_RESP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_NATURE_CREDIT_SELECT(), "0");
		addZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT(), "0");

		setLB_NIVEAU_ETUDE(null);
	}

	/**
	 * Efface tous les objets lies à la fiche emploi courante.
	 */
	private void viderObjetsFichePoste() throws Exception {

		responsableObligatoire = true;

		setFichePosteCourante(null);
		setFpCouranteAffectee(false);

		setEmploiPrimaire(null);
		setEmploiSecondaire(null);

		setAgentCourant(null);
		setAffectationCourante(null);
		setContratCourant(null);
		setTypeContratCourant(null);
		setResponsable(null);
		setAgtResponsable(null);
		setTitrePosteResponsable(null);
		setRemplacement(null);
		setAgtRemplacement(null);
		setTitrePosteRemplacement(null);

		setListeAvantage(null);
		setListeDelegation(null);
		setListeRegime(null);
		setListePrimePointageFP(null);

		setListeTousNiveau(new ArrayList<NiveauEtude>());

		setListeNiveauFP(new ArrayList<NiveauEtudeFP>());

		setListeActiFP(new ArrayList<ActiviteFP>());
		setListeActiFES(new ArrayList<Activite>());
		setListeActiFEP(new ArrayList<Activite>());
		getListeAjoutActiFP().clear();

		setListeCompFP(new ArrayList<CompetenceFP>());
		setListeCompFES(new ArrayList<Competence>());
		setListeCompFEP(new ArrayList<Competence>());
		setListeAjoutCompFP(new ArrayList<Competence>());
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);

		viderFichePoste();
		viderObjetsFichePoste();

		setAfficherListeGrade(false);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * Controle les zones saisies Date de création : (27/06/11 14:50:00)
	 * RG_PE_FP_A01
	 */
	private boolean performControlerSaisie(HttpServletRequest request) throws Exception {
		// RG_PE_FP_A01
		// **********************
		// Verification Année
		// **********************
		if (getZone(getNOM_EF_ANNEE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		} else if (getZone(getNOM_EF_ANNEE()).length() != 4) {
			// "ERR118","L'année doit être saisie avec 4 chiffres."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR118"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		} else if (!Services.estNumerique(getZone(getNOM_EF_ANNEE()))) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		}

		// Verification grade generique
		if (getZone(getNOM_EF_GRADE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "grade"));
			setFocus(getNOM_EF_GRADE());
			return false;
		}

		// **********************
		// Verification Niveau Etudes
		// **********************
		if (getListeTousNiveau().isEmpty()) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Niveau d'étude"));
			setFocus(getNOM_LB_NIVEAU_ETUDE());
			return false;
		}
		if (getListeTousNiveau().size() > 1) {
			// "ERR110", "La liste @ ne doit contenir qu'un seul élément."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR110", "Niveau d'étude"));
			setFocus(getNOM_LB_NIVEAU_ETUDE());
			return false;
		}

		// **********************
		// Verification Service
		// **********************
		if (getZone(getNOM_EF_SERVICE()).length() == 0 || getZone(getNOM_ST_ID_SERVICE_ADS()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "service"));
			setFocus(getNOM_EF_SERVICE());
			return false;
		}

		// **********************
		// Verification Date debut application service
		// **********************
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_DEBUT_APPLI_SERV())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date application"));
			setFocus(getNOM_EF_DATE_DEBUT_APPLI_SERV());
			return false;
		}

		if (!Services.estUneDate(getVAL_EF_DATE_DEBUT_APPLI_SERV())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "application"));
			setFocus(getNOM_EF_DATE_DEBUT_APPLI_SERV());
			return false;
		}

		// **********************
		// Verification NFA
		// **********************
		if (getZone(getNOM_EF_NFA()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "NFA"));
			setFocus(getNOM_EF_NFA());
			return false;
		}

		// **********************
		// Verification Responsable hierarchique
		// **********************
		if (responsableObligatoire && getVAL_ST_RESPONSABLE().length() == 0
				&& !getVAL_EF_TITRE_POSTE().equals(Const.TITRE_POSTE_MAIRE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "responsable hiérarchique"));
			setFocus(getNOM_ST_RESPONSABLE());
			return false;
		}

		// **********************
		// Verification Missions
		// **********************
		if (getVAL_EF_MISSIONS().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "missions"));
			setFocus(getNOM_PB_RECHERCHER());
			return false;
		}
		if (getVAL_EF_MISSIONS().length() > 2000) {
			// "ERR119", "La mission ne doit pas dépasser 2000 caracteres."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR119"));
			setFocus(getNOM_PB_RECHERCHER());
			return false;
		}

		// **********************
		// Verification activites
		// **********************
		boolean auMoinsUneligneSelect = false;
		for (int i = 0; i < getListeToutesActi().size(); i++) {
			// si la ligne est cochée
			if (getVAL_CK_SELECT_LIGNE_ACTI(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				break;
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "activités"));
			return false;
		}

		// **********************
		// Verification reglementaire et budgete
		// **********************
		// si statut = "validée" alors on ne peut avoir "indetermine" dans
		// reglementaire et budgete
		// Récupération Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
			return false;
		}

		// **********************
		// Verification Base horaire de pointage
		// **********************
		int numLigneBaseHorairePointage = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) : -1);

		if (numLigneBaseHorairePointage == 0 || getListeBaseHorairePointage().isEmpty()
				|| numLigneBaseHorairePointage > getListeBaseHorairePointage().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire de pointage"));
			return false;
		}
		return true;
	}

	/**
	 * Controle les regles de gestion. RG_PE_FP_C02
	 */
	private boolean performControlerRG(HttpServletRequest request) throws Exception {

		/*
		 * Vérification RG responsable hierarchique != fiche courante && fiche
		 * de poste remplacee != fiche courante
		 */
		if (getFichePosteCourante() != null && getFichePosteCourante().getNumFp() != null
				&& !getFichePosteCourante().getNumFp().equals(Const.CHAINE_VIDE)) {
			if (getVAL_ST_RESPONSABLE().equals(getFichePosteCourante().getNumFp())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR116"));
				return false;
			}
			if (getVAL_ST_REMPLACEMENT().equals(getFichePosteCourante().getNumFp())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR117", "remplacée"));
				return false;
			}
		}

		// *********************** //
		// Verification statut //
		// *********************** //
		// RG_PE_FP_C02
		if (getFichePosteCourante() != null) {

			// Récupération Statut de la fiche
			int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);

			if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
				return false;
			}

			StatutFP statutCourant = (StatutFP) getListeStatut().get(numLigneStatut);
			StatutFP statutPrecedant = null;

			if (getFichePosteCourante().getIdStatutFp() != null) {
				statutPrecedant = getStatutFPDao().chercherStatutFP(getFichePosteCourante().getIdStatutFp());
				if (statutCourant.getIdStatutFp() != statutPrecedant.getIdStatutFp()) {

					// Passage au statut inactif impossible si la fiche est
					// affectée ou est utilisee comme responsable hierarchique.
					if (EnumStatutFichePoste.INACTIVE.getLibLong().equals(statutCourant.getLibStatutFp())) {
						if (estFpCouranteAffectee()) {
							// "ERR114",
							// "Cette fiche de poste ne peut être inactive car elle est affectée a un agent."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR114"));
							return false;
						}
					}

					if (EnumStatutFichePoste.EN_CREATION.getLibLong().equals(statutCourant.getLibStatutFp())) {
						// "ERR123", "Le statut ne peut repasser a @."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR123", "'En création'"));
						return false;
					}

					if (EnumStatutFichePoste.INACTIVE.getLibLong().equals(statutCourant.getLibStatutFp())
							&& !(EnumStatutFichePoste.VALIDEE.getLibLong().equals(statutPrecedant.getLibStatutFp())
									|| EnumStatutFichePoste.TRANSITOIRE.getLibLong().equals(
											statutPrecedant.getLibStatutFp()) || EnumStatutFichePoste.GELEE
									.getLibLong().equals(statutPrecedant.getLibStatutFp()))) {
						// "ERR124",
						// "Le statut ne peut passer a @ s'il n'est pas @."
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR124", "'" + EnumStatutFichePoste.INACTIVE.getLibLong()
										+ "'", "'" + EnumStatutFichePoste.VALIDEE.getLibLong() + "' ou '"
										+ EnumStatutFichePoste.TRANSITOIRE.getLibLong() + "' ou '"
										+ EnumStatutFichePoste.GELEE.getLibLong() + "'"));
						return false;
					}

				}
			}
		}

		// *********************** //
		// Verification information bugetaire //
		// *********************** //
		if (getFichePosteCourante() != null) {

			int numLigneReglementaire = (Services.estNumerique(getZone(getNOM_LB_REGLEMENTAIRE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_REGLEMENTAIRE_SELECT())) : -1);

			if (numLigneReglementaire == -1 || getListeHoraire().isEmpty()
					|| numLigneReglementaire > getListeHoraire().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "réglementaire"));
				return false;
			}

			Horaire reglementaire = (Horaire) getListeHoraire().get(numLigneReglementaire);

			int numLigneBudgete = (Services.estNumerique(getZone(getNOM_LB_BUDGETE_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_BUDGETE_SELECT())) : -1);

			if (numLigneBudgete == -1 || getListeHoraire().isEmpty() || numLigneBudgete > getListeHoraire().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "budgété"));
				return false;
			}

			Horaire budgete = (Horaire) getListeHoraire().get(numLigneBudgete);

			// Nature des credits
			int numLigneNatureCredit = (Services.estNumerique(getZone(getNOM_LB_NATURE_CREDIT_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_NATURE_CREDIT_SELECT())) : -1);

			if (numLigneNatureCredit == -1 || getListeNatureCredit().isEmpty()
					|| numLigneNatureCredit > getListeNatureCredit().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "nature des crédits"));
				return false;
			}

			NatureCredit natureCredit = (NatureCredit) getListeNatureCredit().get(numLigneNatureCredit);

			// si nature credit = NON alors budgete doit etre egal a 0
			if (natureCredit.getLibNatureCredit().equals("NON")
					&& !budgete.getLibHor().trim().toLowerCase().equals("non")) {
				// "ERR1111",
				// "Si la nature des crédits est @, alors budgété doit être @."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1111", "NON", "Non"));
				return false;
			}
			// si nature credit = PERMANENT ou REMPLACEMENT ou TEMPORAIRE ou
			// SURNUMERAIRE alors budgete >0 et <=100
			if (natureCredit.getLibNatureCredit().equals("PERMANENT")
					|| natureCredit.getLibNatureCredit().equals("REMPLACEMENT")
					|| natureCredit.getLibNatureCredit().equals("TEMPORAIRE")
					|| natureCredit.getLibNatureCredit().equals("SURNUMERAIRE")) {
				if (budgete.getLibHor().trim().toLowerCase().equals("non")) {
					// "ERR1112",
					// "Si la nature des crédits est @, alors budgété ne doit pas être @."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR1112", "PERMANENT", "Non"));
					return false;
				}
			}

			// si nature credit = REMPLACEMENT, alors fiche poste remplacee doit
			// etre renseigné et insersement
			if (natureCredit.getLibNatureCredit().equals("REMPLACEMENT")
					&& getVAL_ST_REMPLACEMENT().equals(Const.CHAINE_VIDE)) {
				// "ERR1113",
				// "Budget de remplacement : fiche de poste remplacee necessaire.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1113"));
				return false;

			}
			if (!getVAL_ST_REMPLACEMENT().equals(Const.CHAINE_VIDE)) {
				if (!natureCredit.getLibNatureCredit().equals("REMPLACEMENT")) {
					// "ERR1114",
					// "Fiche de poste remplacee mais budget different de remplacement."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR1114"));
					return false;
				}
			}

			// si relementaire > 0 alors budget doit etre different de permanent
			// et inversement
			if (natureCredit.getLibNatureCredit().equals("PERMANENT")
					&& reglementaire.getLibHor().trim().toLowerCase().equals("non")) {
				// "ERR1115",
				// "Le poste n'est pas reglementaire, le budget ne peut pas être permanent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1115"));
				return false;

			}
			if (!reglementaire.getLibHor().trim().toLowerCase().equals("non")) {
				if (!natureCredit.getLibNatureCredit().equals("PERMANENT")) {
					// "ERR1116",
					// "Le poste est reglementaire, le budget doit être permanent."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR1116"));
					return false;
				}
			}

		}

		return true;
	}

	/**
	 * Alimente l'objet FicheEmploi avec les champs de saisie du formulaire.
	 * Retourne true ou false Date de création : (27/06/11 15:34:00)
	 */
	private boolean alimenterFichePoste(HttpServletRequest request) throws Exception {

		// récupération des informations remplies dans les zones de saisie
		String annee = getVAL_EF_ANNEE();
		String dateDebutAppliServ = getVAL_EF_DATE_DEBUT_APPLI_SERV();
		String opi = getVAL_EF_OPI().length() == 0 ? null : getVAL_EF_OPI();
		String numDeliberation = getVAL_EF_NUM_DELIBERATION().length() == 0 ? null : getVAL_EF_NUM_DELIBERATION();
		String observation = getVAL_EF_OBSERVATION();
		String nfa = getVAL_EF_NFA();
		String missions = getVAL_EF_MISSIONS();
		Integer idServiceADS = new Integer(getVAL_ST_ID_SERVICE_ADS());
		String grade = getVAL_EF_CODE_GRADE();

		// récupération du titre de poste et Vérification de son existence.
		Integer idTitre = null;
		for (int i = 0; i < getListeTitre().size(); i++) {
			TitrePoste titre = (TitrePoste) getListeTitre().get(i);
			if (titre.getLibTitrePoste().equals(getVAL_EF_TITRE_POSTE())) {
				idTitre = titre.getIdTitrePoste();
				break;
			}
		}
		if (idTitre == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titres de postes"));
			return false;
		}

		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);

		if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
			return false;
		}

		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);

		// Budget
		int numLigneBudget = (Services.estNumerique(getZone(getNOM_LB_BUDGET_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BUDGET_SELECT())) : -1);

		if (numLigneBudget == -1 || getListeBudget().isEmpty() || numLigneBudget > getListeBudget().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "budgets"));
			return false;
		}

		Budget budget = (Budget) getListeBudget().get(numLigneBudget);

		// Lieu
		int numLigneLoc = (Services.estNumerique(getZone(getNOM_LB_LOC_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_LOC_SELECT())) : -1);

		if (numLigneLoc == -1 || getListeLocalisation().isEmpty() || numLigneLoc > getListeLocalisation().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "localisations"));
			return false;
		}

		EntiteGeo lieu = (EntiteGeo) getListeLocalisation().get(numLigneLoc - 1);

		int numLigneBudgete = (Services.estNumerique(getZone(getNOM_LB_BUDGETE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BUDGETE_SELECT())) : -1);

		if (numLigneBudgete == -1 || getListeHoraire().isEmpty() || numLigneBudgete > getListeHoraire().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "budgété"));
			return false;
		}

		Horaire budgete = (Horaire) getListeHoraire().get(numLigneBudgete);

		int numLigneReglementaire = (Services.estNumerique(getZone(getNOM_LB_REGLEMENTAIRE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_REGLEMENTAIRE_SELECT())) : -1);

		if (numLigneReglementaire == -1 || getListeHoraire().isEmpty()
				|| numLigneReglementaire > getListeHoraire().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "réglementaire"));
			return false;
		}

		Horaire reglementaire = (Horaire) getListeHoraire().get(numLigneReglementaire);

		// Base horaire de absence
		int numLigneBaseHoraireAbsence = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT())) : -1);

		if (numLigneBaseHoraireAbsence == 0 || getListeBaseHoraireAbsence().isEmpty()
				|| numLigneBaseHoraireAbsence > getListeBaseHoraireAbsence().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire d'absence"));
			return false;
		}

		RefTypeSaisiCongeAnnuelDto baseHoraireAbsence = (RefTypeSaisiCongeAnnuelDto) getListeBaseHoraireAbsence().get(
				numLigneBaseHoraireAbsence - 1);

		// Base horaire de pointage
		int numLigneBaseHorairePointage = (Services.estNumerique(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT())) : -1);

		if (numLigneBaseHorairePointage == 0 || getListeBaseHorairePointage().isEmpty()
				|| numLigneBaseHorairePointage > getListeBaseHorairePointage().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base horaire de pointage"));
			return false;
		}

		BaseHorairePointage baseHorairePointage = (BaseHorairePointage) getListeBaseHorairePointage().get(
				numLigneBaseHorairePointage - 1);

		// Nature des credits
		int numLigneNatureCredit = (Services.estNumerique(getZone(getNOM_LB_NATURE_CREDIT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_NATURE_CREDIT_SELECT())) : -1);

		if (numLigneNatureCredit == -1 || getListeNatureCredit().isEmpty()
				|| numLigneNatureCredit > getListeNatureCredit().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "nature des crédits"));
			return false;
		}

		NatureCredit natureCredit = (NatureCredit) getListeNatureCredit().get(numLigneNatureCredit);

		getFichePosteCourante().setAnneeCreation(Integer.valueOf(annee));
		// #16555 on n'alimente plus date debut validité et date fin validité
		getFichePosteCourante().setDateFinValiditeFp(null);
		getFichePosteCourante().setDateDebutValiditeFp(null);
		getFichePosteCourante().setObservation(observation);
		getFichePosteCourante().setMissions(missions);
		getFichePosteCourante().setIdStatutFp(statut.getIdStatutFp());
		getFichePosteCourante().setIdBudget(budget.getIdBudget());
		getFichePosteCourante().setOpi(opi);
		getFichePosteCourante().setNumDeliberation(numDeliberation);
		getFichePosteCourante().setNfa(nfa);
		getFichePosteCourante().setIdEntiteGeo(Integer.valueOf(lieu.getIdEntiteGeo()));
		getFichePosteCourante().setIdTitrePoste(idTitre);
		getFichePosteCourante().setCodeGrade(grade);
		EntiteDto serv = adsService.getInfoSiservByIdEntite(idServiceADS);
		getFichePosteCourante().setIdServi(serv == null || serv.getCodeServi() == null ? null : serv.getCodeServi());
		getFichePosteCourante().setIdServiceAds(idServiceADS);
		getFichePosteCourante().setIdCdthorBud(Integer.valueOf(budgete.getCdtHor()));
		getFichePosteCourante().setIdCdthorReg(Integer.valueOf(reglementaire.getCdtHor()));
		getFichePosteCourante().setDateDebAppliServ(sdf.parse(dateDebutAppliServ));
		getFichePosteCourante().setIdNatureCredit(natureCredit.getIdNatureCredit());
		getFichePosteCourante().setIdBaseHorairePointage(baseHorairePointage.getIdBaseHorairePointage());
		getFichePosteCourante().setIdBaseHoraireAbsence(baseHoraireAbsence.getIdRefTypeSaisiCongeAnnuel());

		if (getFichePosteCourante().getIdStatutFp().toString().equals(EnumStatutFichePoste.INACTIVE.getId())) {
			getFichePosteCourante().setIdResponsable(null);
		} else {
			if (getResponsable() != null) {
				getFichePosteCourante().setIdResponsable(getResponsable().getIdFichePoste());
			}
		}

		if (getRemplacement() != null) {
			getFichePosteCourante().setIdRemplacement(getRemplacement().getIdFichePoste());
		}

		return true;
	}

	private boolean saveJoin(HttpServletRequest request) throws Exception {

		// Recherche de tous les liens FicheEmploi / FichePoste
		ArrayList<FEFP> liens = getFefpDao().listerFEFPAvecFP(getFichePosteCourante().getIdFichePoste());
		// Sauvegarde des fiche emploi primaire et secondaire
		FicheEmploi emploiPrimaireTest = null;
		try {
			emploiPrimaireTest = getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(true, liens);
		} catch (Exception e) {

		}
		if (emploiPrimaireTest == null) {
			FEFP fefpPrimaire = new FEFP(getFichePosteCourante().getIdFichePoste(), getEmploiPrimaire()
					.getIdFicheEmploi(), true);
			getFefpDao().creerFEFP(fefpPrimaire.getIdFicheEmploi(), fefpPrimaire.getIdFichePoste(),
					fefpPrimaire.isFePrimaire());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "La liaison FicheEmploi-FichePoste"));
				return false;
			}
		} else {
			FEFP ancienLien = getFefpDao().chercherFEFPAvecNumFPPrimaire(getFichePosteCourante().getIdFichePoste(),
					true);
			getFefpDao().supprimerFEFP(ancienLien.getIdFicheEmploi(), ancienLien.getIdFichePoste(),
					ancienLien.isFePrimaire());
			ancienLien.setFePrimaire(true);
			ancienLien.setIdFicheEmploi(getEmploiPrimaire().getIdFicheEmploi());
			getFefpDao().creerFEFP(ancienLien.getIdFicheEmploi(), ancienLien.getIdFichePoste(),
					ancienLien.isFePrimaire());
		}

		if (getEmploiSecondaire() != null) {

			// Recherche de tous les liens FicheEmploi / FichePoste
			ArrayList<FEFP> liensA = getFefpDao().listerFEFPAvecFP(getFichePosteCourante().getIdFichePoste());
			FicheEmploi emploiSecondaireTest = null;
			try {
				emploiSecondaireTest = getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(false, liensA);
			} catch (Exception e) {

			}
			if (emploiSecondaireTest == null) {
				FEFP fefpSecondaire = new FEFP(getFichePosteCourante().getIdFichePoste(), getEmploiSecondaire()
						.getIdFicheEmploi(), false);
				getFefpDao().creerFEFP(fefpSecondaire.getIdFicheEmploi(), fefpSecondaire.getIdFichePoste(),
						fefpSecondaire.isFePrimaire());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR976", "La liaison FicheEmploi-FichePoste"));
					return false;
				}
			} else {
				// on modifie le lien avec le num FE secondaire
				FEFP ancienLien = getFefpDao().chercherFEFPAvecNumFPPrimaire(getFichePosteCourante().getIdFichePoste(),
						false);
				getFefpDao().supprimerFEFP(ancienLien.getIdFicheEmploi(), ancienLien.getIdFichePoste(),
						ancienLien.isFePrimaire());
				ancienLien.setFePrimaire(false);
				ancienLien.setIdFicheEmploi(getEmploiSecondaire().getIdFicheEmploi());
				getFefpDao().creerFEFP(ancienLien.getIdFicheEmploi(), ancienLien.getIdFichePoste(),
						ancienLien.isFePrimaire());
			}
		} else {

			// Recherche de tous les liens FicheEmploi / FichePoste
			ArrayList<FEFP> liensB = getFefpDao().listerFEFPAvecFP(getFichePosteCourante().getIdFichePoste());
			// on supprime le lien eventuel
			FicheEmploi emploiSecondaireTest = null;
			try {
				emploiSecondaireTest = getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(false, liensB);
			} catch (Exception e) {

			}
			if (emploiSecondaireTest != null) {
				// on modifie le lien avec le num FE secondaire
				FEFP ancienLien = getFefpDao().chercherFEFPAvecNumFPPrimaire(getFichePosteCourante().getIdFichePoste(),
						false);
				getFefpDao().supprimerFEFP(ancienLien.getIdFicheEmploi(), ancienLien.getIdFichePoste(),
						ancienLien.isFePrimaire());
			}
		}

		// on supprime tous les niveau etude de la FDP
		ArrayList<NiveauEtudeFP> niveauFPExistant = getNiveauEtudeFPDao().listerNiveauEtudeFPAvecFP(
				getFichePosteCourante().getIdFichePoste());
		if (niveauFPExistant != null && niveauFPExistant.size() > 0) {
			for (int i = 0; i < niveauFPExistant.size(); i++) {
				NiveauEtudeFP niveauFP = (NiveauEtudeFP) niveauFPExistant.get(i);
				getNiveauEtudeFPDao().supprimerNiveauEtudeFP(niveauFP.getIdNiveauEtude(), niveauFP.getIdFichePoste());
			}
		}
		// on ajoute le niveau etude dela FDP
		NiveauEtude niveauAAjouter = (NiveauEtude) getListeTousNiveau().get(0);
		NiveauEtudeFP niveauFP = new NiveauEtudeFP(getFichePosteCourante().getIdFichePoste(),
				niveauAAjouter.getIdNiveauEtude());
		getNiveauEtudeFPDao().creerNiveauEtudeFP(niveauFP.getIdNiveauEtude(), niveauFP.getIdFichePoste());

		// nouvelle gestion des activites
		boolean auMoinsUneligneSelect = false;
		Activite acti = null;
		for (int i = 0; i < getListeToutesActi().size(); i++) {
			// on recupere la ligne concernée
			acti = (Activite) getListeToutesActi().get(i);
			// si la ligne est cochée
			if (getVAL_CK_SELECT_LIGNE_ACTI(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				// on regarde de quelle liste elle faisait partie
				for (Activite actiFP : getListeActiFEP()) {
					if (acti.getIdActivite().toString().equals(actiFP.getIdActivite().toString())) {
						try {
							ActiviteFP actFP = getActiviteFPDao().chercherActiviteFP(
									getFichePosteCourante().getIdFichePoste(), acti.getIdActivite());
							actFP.setActivitePrincipale(true);
							getActiviteFPDao().modifierActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
									actFP.isActivitePrincipale());
						} catch (Exception e) {
							ActiviteFP actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(),
									acti.getIdActivite(), true);
							getActiviteFPDao().creerActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
									actFP.isActivitePrincipale());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(
										MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
								return false;
							}
							break;
						}
					}
				}
				for (Activite actiFP : getListeActiFES()) {
					if (acti.getIdActivite().toString().equals(actiFP.getIdActivite().toString())) {
						try {
							ActiviteFP actFP = getActiviteFPDao().chercherActiviteFP(
									getFichePosteCourante().getIdFichePoste(), acti.getIdActivite());
							actFP.setActivitePrincipale(true);
							getActiviteFPDao().modifierActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
									actFP.isActivitePrincipale());
						} catch (Exception e) {
							ActiviteFP actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(),
									acti.getIdActivite(), true);
							getActiviteFPDao().creerActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
									actFP.isActivitePrincipale());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(
										MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
								return false;
							}
							break;
						}
					}
				}
				if (getListeActiFP() != null) {
					for (ActiviteFP actiFP : getListeActiFP()) {
						if (acti.getIdActivite().toString().equals(actiFP.getIdActivite().toString())) {
							try {
								ActiviteFP actFP = getActiviteFPDao().chercherActiviteFP(
										getFichePosteCourante().getIdFichePoste(), acti.getIdActivite());
								actFP.setActivitePrincipale(false);
								getActiviteFPDao().modifierActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
										actFP.isActivitePrincipale());
							} catch (Exception e) {
								ActiviteFP actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(),
										acti.getIdActivite(), false);
								getActiviteFPDao().creerActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
										actFP.isActivitePrincipale());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction().declarerErreur(
											MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite()
													+ ")"));
									return false;
								}
								break;
							}
						}
					}

				}
				for (Activite actiFP : getListeAjoutActiFP()) {
					if (acti.getIdActivite().toString().equals(actiFP.getIdActivite().toString())) {
						try {
							ActiviteFP actFP = getActiviteFPDao().chercherActiviteFP(
									getFichePosteCourante().getIdFichePoste(), acti.getIdActivite());
							actFP.setActivitePrincipale(false);
							getActiviteFPDao().modifierActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
									actFP.isActivitePrincipale());
						} catch (Exception e) {
							ActiviteFP actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(),
									acti.getIdActivite(), false);
							getActiviteFPDao().creerActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
									actFP.isActivitePrincipale());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(
										MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
								return false;
							}
							break;
						}
					}
				}
			} else {
				try {
					ActiviteFP actFP = getActiviteFPDao().chercherActiviteFP(getFichePosteCourante().getIdFichePoste(),
							acti.getIdActivite());
					getActiviteFPDao().supprimerActiviteFP(actFP.getIdFichePoste(), actFP.getIdActivite(),
							actFP.isActivitePrincipale());
				} catch (Exception e) {

				}
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "activités"));
			return false;
		}

		// nouvelle gestion des activites
		Competence comp = null;
		for (int i = 0; i < getListeToutesComp().size(); i++) {
			// on recupere la ligne concernée
			comp = (Competence) getListeToutesComp().get(i);
			// si la ligne est cochée
			if (getVAL_CK_SELECT_LIGNE_COMP(i).equals(getCHECKED_ON())) {
				// on regarde de quelle liste elle faisait partie
				for (Competence compFP : getListeCompFEP()) {
					if (comp.getIdCompetence().toString().equals(compFP.getIdCompetence().toString())) {
						try {
							CompetenceFP comFP = getCompetenceFPDao().chercherCompetenceFP(
									getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
							getCompetenceFPDao()
									.supprimerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
							getCompetenceFPDao().creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
						} catch (Exception e) {
							CompetenceFP comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(),
									comp.getIdCompetence());
							getCompetenceFPDao().creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(
										MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence()
												+ ")"));
								return false;
							}
							break;
						}
					}
				}
				for (Competence compFP : getListeCompFES()) {
					if (comp.getIdCompetence().toString().equals(compFP.getIdCompetence().toString())) {
						try {
							CompetenceFP comFP = getCompetenceFPDao().chercherCompetenceFP(
									getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
							getCompetenceFPDao()
									.supprimerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
							getCompetenceFPDao().creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
						} catch (Exception e) {
							CompetenceFP comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(),
									comp.getIdCompetence());
							getCompetenceFPDao().creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(
										MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence()
												+ ")"));
								return false;
							}
							break;
						}
					}
				}
				if (getListeCompFP() != null) {
					for (CompetenceFP compFP : getListeCompFP()) {
						if (comp.getIdCompetence().toString().equals(compFP.getIdCompetence().toString())) {
							try {
								CompetenceFP comFP = getCompetenceFPDao().chercherCompetenceFP(
										getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
								getCompetenceFPDao().supprimerCompetenceFP(comFP.getIdFichePoste(),
										comFP.getIdCompetence());
								getCompetenceFPDao()
										.creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
							} catch (Exception e) {
								CompetenceFP comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(),
										comp.getIdCompetence());
								getCompetenceFPDao()
										.creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction().declarerErreur(
											MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence()
													+ ")"));
									return false;
								}
								break;
							}
						}
					}

				}
				if (getListeAjoutCompFP() != null) {
					for (Competence compFP : getListeAjoutCompFP()) {
						if (comp.getIdCompetence().toString().equals(compFP.getIdCompetence().toString())) {
							try {
								CompetenceFP comFP = getCompetenceFPDao().chercherCompetenceFP(
										getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());

								getCompetenceFPDao().supprimerCompetenceFP(comFP.getIdFichePoste(),
										comFP.getIdCompetence());
								getCompetenceFPDao()
										.creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
							} catch (Exception e) {

								CompetenceFP comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(),
										comp.getIdCompetence());
								getCompetenceFPDao()
										.creerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction().declarerErreur(
											MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence()
													+ ")"));
									return false;
								}
								break;
							}
						}
					}

				}

			} else {
				try {
					CompetenceFP comFP = getCompetenceFPDao().chercherCompetenceFP(
							getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
					getCompetenceFPDao().supprimerCompetenceFP(comFP.getIdFichePoste(), comFP.getIdCompetence());
				} catch (Exception e) {

				}

			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/07/11 10:59:29)
	 * 
	 * RG_PE_FP_A02
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Controle des champs
		if (!performControlerSaisie(request)) {
			return false;
		}

		// Controle des RGs
		if (!performControlerRG(request)) {
			return false;
		}

		// Controle des RGs entre stut FDP et staut des entités
		// # 16364 et #16363
		if (!performControlerRGEntite(request)) {
			return false;
		}

		// Alimentation de la fiche de poste
		if (!alimenterFichePoste(request)) {
			return false;
		}

		// Création de la fiche poste
		if (getFichePosteCourante().getIdFichePoste() == null) {
			Integer idCree = getFichePosteDao().creerFichePoste(getFichePosteCourante(), user, getHistoFichePosteDao(),
					getTransaction());
			setFichePosteCourante(getFichePosteCourante());
			getFichePosteCourante().setIdFichePoste(idCree);
			// historisation
			HistoFichePoste histo = new HistoFichePoste(getFichePosteCourante());
			getHistoFichePosteDao().creerHistoFichePoste(histo, user, EnumTypeHisto.CREATION);
			if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {
				// Fiche poste créée
				messageInf = MessageUtils.getMessage("INF103", getFichePosteCourante().getNumFp());
				// pour reinitialiser la fenetre
				setStatut(STATUT_RECHERCHE);
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
			} else {
				// Fiche poste dupliquee
				messageInf = MessageUtils.getMessage("INF104", getFichePosteCourante().getNumFp());
				setStatut(STATUT_DUPLIQUER);
			}
		} else {
			getFichePosteDao().modifierFichePoste(getFichePosteCourante(), getHistoFichePosteDao(), user,
					getTransaction(), getAffectationDao());
			if (getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
				// Fiche poste imprimee
				messageInf = MessageUtils.getMessage("INF111", getFichePosteCourante().getNumFp());
			} else {
				// Fiche poste modifiée
				messageInf = MessageUtils.getMessage("INF106", getFichePosteCourante().getNumFp());
			}
		}

		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR111"));
			return false;
		}

		if (!saveJoin(request)) {
			return false;
		}

		if (!getTransaction().isErreur()) {

			// Sauvegarde des nouveaux avantages nature et suppression des
			// anciens
			for (AvantageNature avNat : getListeAvantageAAjouter()) {
				Integer idCreer = getAvantageNatureDao().creerAvantageNature(avNat.getNumRubrique(),
						avNat.getIdTypeAvantage(), avNat.getIdNatureAvantage(), avNat.getMontant());
				AvantageNatureFP avNatFP = new AvantageNatureFP(getFichePosteCourante().getIdFichePoste(), idCreer);
				getAvantageNatureFPDao().creerAvantageNatureFP(avNatFP.getIdAvantage(), avNatFP.getIdFichePoste());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins un avantage en nature n'a pu être créé.");
					return false;
				}
			}
			for (AvantageNature avNat : getListeAvantageASupprimer()) {
				AvantageNatureFP avNatFP = new AvantageNatureFP(getFichePosteCourante().getIdFichePoste(),
						avNat.getIdAvantage());
				getAvantageNatureFPDao().supprimerAvantageNatureFP(avNatFP.getIdAvantage(), avNatFP.getIdFichePoste());
				getAvantageNatureDao().supprimerAvantageNature(avNat.getIdAvantage());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins un avantage en nature n'a pu être supprimé.");
					return false;
				}
			}

			// Sauvegarde des nouvelles Delegation et suppression des anciennes
			for (Delegation deleg : getListeDelegationAAjouter()) {
				Integer idCreer = getDelegationDao().creerDelegation(deleg.getIdTypeDelegation(),
						deleg.getLibDelegation());
				DelegationFP delFP = new DelegationFP(getFichePosteCourante().getIdFichePoste(), idCreer);
				getDelegationFPDao().creerDelegationFP(delFP.getIdDelegation(), delFP.getIdFichePoste());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins une Delegation n'a pu être créée.");
					return false;
				}
			}
			for (Delegation deleg : getListeDelegationASupprimer()) {
				DelegationFP delFP = new DelegationFP(getFichePosteCourante().getIdFichePoste(),
						deleg.getIdDelegation());
				getDelegationFPDao().supprimerDelegationFP(delFP.getIdDelegation(), delFP.getIdFichePoste());
				getDelegationDao().supprimerDelegation(deleg.getIdDelegation());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins une Delegation n'a pu être supprimée.");
					return false;
				}
			}

			// Sauvegarde des nouveaux RegimeIndemnitaire et suppression des
			// anciens
			for (RegimeIndemnitaire regIndemn : getListeRegimeAAjouter()) {
				Integer idCreer = getRegIndemnDao().creerRegimeIndemnitaire(regIndemn.getIdTypeRegIndemn(),
						regIndemn.getNumRubrique(), regIndemn.getForfait(), regIndemn.getNombrePoints());
				RegIndemFP riFP = new RegIndemFP(getFichePosteCourante().getIdFichePoste(), idCreer);
				getRegIndemnFPDao().creerRegIndemFP(riFP.getIdRegIndemn(), riFP.getIdFichePoste());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins un RegimeIndemnitaire n'a pu être créé.");
					return false;
				}
			}
			for (int i = 0; i < getListeRegimeASupprimer().size(); i++) {
				RegimeIndemnitaire ri = (RegimeIndemnitaire) getListeRegimeASupprimer().get(i);
				RegIndemFP riFP = new RegIndemFP(getFichePosteCourante().getIdFichePoste(), ri.getIdRegIndemn());
				getRegIndemnFPDao().supprimerRegIndemFP(riFP.getIdRegIndemn(), riFP.getIdFichePoste());
				if (!(getRegIndemnAffDao().listerRegIndemnAFFAvecRI(ri.getIdRegIndemn()).size() > 0)) {
					getRegIndemnDao().supprimerRegimeIndemnitaire(ri.getIdRegIndemn());
				}
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur("Au moins un RegimeIndemnitaire n'a pu être supprimé.");
					return false;
				}
			}

			// COMMIT
			commitTransaction();

			// Sauvegarde des nouvelles primes de pointage et suppression des
			// anciens
			for (int i = 0; i < getListePrimePointageFPAAjouter().size(); i++) {
				try {
					PrimePointageFP primePoint = (PrimePointageFP) getListePrimePointageFPAAjouter().get(i);
					getPrimePointageFPDao().creerPrimePointageFP(primePoint.getNumRubrique(),
							getFichePosteCourante().getIdFichePoste());
				} catch (Exception e) {
					getTransaction().declarerErreur(" Au moins une prime de pointage n'a pu être créée.");
					return false;
				}
			}

			for (int i = 0; i < getListePrimePointageFPASupprimer().size(); i++) {
				try {
					PrimePointageFP ri = (PrimePointageFP) getListePrimePointageFPASupprimer().get(i);
					getPrimePointageFPDao().supprimerPrimePointageFP(ri.getIdFichePoste(), ri.getNumRubrique());
				} catch (Exception e) {
					getTransaction().declarerErreur("Au moins une prime de pointage n'a pu être supprimée.");
					return false;
				}
			}

			getListeAjoutActiFP().clear();
			initialiseActivites();

			// setListeAjoutCompFP(null);
			getListeAjoutCompFP().clear();
			initialiseCompetence();

			// Suppression des listes de spécificités en session
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_AV_NATURE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_SUPPR);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_DELEGATION);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_SUPPR);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_SUPPR);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE_A_SUPPR);

			// si la FDP est affectée a un agent, alors on sauvegarde la fiche
			// de poste
			// RG_PE_FP_A02
			if (estFpCouranteAffectee()) {
				if (!sauvegardeFDP()) {
					return false;
				}
			}

		} else {
			return false;
		}

		majChangementFEAutorise();

		if (!messageInf.equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
			getTransaction().declarerErreur(messageInf);
		}

		// appel WS mise à jour Abre FDP
		if (!sirhService.miseAJourArbreFDP()) {
			// "ERR970","Une erreur est survenue lors de la mise à jour de l'arbre des Fiche de poste. Merci de contacter le responsable du projet car cela engendre un soucis sur le Kiosque RH."
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR970"));
			messageInf = Const.CHAINE_VIDE;
			return false;
		}

		return true;
	}

	private boolean performControlerRGEntite(HttpServletRequest request) {
		if (getFichePosteCourante() != null) {

			// Récupération Statut de la fiche
			int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);

			if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
				return false;
			}

			StatutFP statutCourant = (StatutFP) getListeStatut().get(numLigneStatut);

			Integer idServiceADS = new Integer(getVAL_ST_ID_SERVICE_ADS());
			EntiteDto entiteDto = adsService.getEntiteByIdEntite(idServiceADS);

			// #16363 : si statut FP = en creation alors l'entite doit etre au
			// statut "Prevision" et inversement
			if (EnumStatutFichePoste.EN_CREATION.getLibLong().equals(statutCourant.getLibStatutFp())) {
				if (!String.valueOf(StatutEntiteEnum.PREVISION.getIdRefStatutEntite()).equals(
						entiteDto.getIdStatut().toString())) {
					// "ERR126",
					// "Le statut de la FDP ne peut être @ si l'entité n'est pas @."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR126", "'En création'", "'En prévision'"));
					return false;
				}
			} else {
				if (String.valueOf(StatutEntiteEnum.PREVISION.getIdRefStatutEntite()).equals(
						entiteDto.getIdStatut().toString())) {
					// "ERR126",
					// "Le statut de la FDP ne peut être @ si l'entité n'est pas @."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR126", "'En création'", "'En prévision'"));
					return false;
				}
			}

			// #1634 : RG sur entité vs FDP
			if (String.valueOf(StatutEntiteEnum.INACTIF.getIdRefStatutEntite()).equals(
					entiteDto.getIdStatut().toString())) {
				// "ERR126",
				// "Le statut de la FDP ne peut être @ si l'entité n'est pas @."
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR126", statutCourant.getLibStatutFp(), "'Active'"));
				return false;
			}
		}
		return true;
	}

	private boolean sauvegardeFDP() throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("SauvegardeFDP");

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String destinationFDP = "SauvegardeFDP/SauvFP_" + getFichePosteCourante().getIdFichePoste() + "_" + dateJour
				+ ".doc";

		try {
			byte[] fileAsBytes = sirhService.downloadFichePoste(getFichePosteCourante().getIdFichePoste());

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destinationFDP)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

			// Tout s'est bien passé
			// on crée le document en base de données
			Document d = new Document();
			d.setIdTypeDocument(1);
			d.setLienDocument(destinationFDP);
			d.setNomDocument("SauvFP_" + getFichePosteCourante().getIdFichePoste() + "_" + dateJour + ".doc");
			d.setDateDocument(new Date());
			d.setCommentaire("Sauvegarde automatique lors modification FDP.");
			Integer id = getDocumentDao().creerDocument(d.getClasseDocument(), d.getNomDocument(), d.getLienDocument(),
					d.getDateDocument(), d.getCommentaire(), d.getIdTypeDocument(), d.getNomOriginal());

			DocumentAgent lda = new DocumentAgent();
			lda.setIdAgent(getAgentCourant().getIdAgent());
			lda.setIdDocument(id);
			getLienDocumentAgentDao().creerDocumentAgent(lda.getIdAgent(), lda.getIdDocument());

			if (getTransaction().isErreur()) {
				return false;
			}

			commitTransaction();

		} catch (Exception e) {
			// "ERR185",
			// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		// Mise à jour de l'action menee
		addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);

		// ////////////////////////////////
		// dans le cas ou rien n est saisi
		if ((getVAL_EF_RECHERCHE() == null || getVAL_EF_RECHERCHE().equals(Const.CHAINE_VIDE))
				&& (getVAL_EF_RECHERCHE_BY_AGENT() == null || getVAL_EF_RECHERCHE_BY_AGENT().equals(Const.CHAINE_VIDE))) {

			getTransaction().traiterErreur();
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR982"));
			return false;

			// ////////////////////////////////
			// dans le cas ou les deux champ sont saisis
		} else if (getVAL_EF_RECHERCHE() != null && !getVAL_EF_RECHERCHE().equals(Const.CHAINE_VIDE)
				&& getVAL_EF_RECHERCHE_BY_AGENT() != null && !getVAL_EF_RECHERCHE_BY_AGENT().equals(Const.CHAINE_VIDE)) {
			// recuperation agent
			Agent agent = null;
			if (getVAL_EF_RECHERCHE_BY_AGENT().length() != 0) {
				agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_EF_RECHERCHE_BY_AGENT()));
			}
			// l agent n existe pas
			if (null == agent || null == agent.getIdAgent()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR005", "agent"));
				return false;
			}

			ArrayList<FichePoste> fp = getFichePosteDao().listerFichePosteAvecCriteresAvances(new ArrayList<Integer>(),
					null, null, getVAL_EF_RECHERCHE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_RECHERCHE(),
					agent == null ? null : agent.getIdAgent(), false);

			// si aucun resultat ==> message erreur
			if (null == fp || 0 == fp.size()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR005", "résultat"));
				return false;
				// si plusieurs resultats ==> message erreur
			} else if (null != fp && 1 < fp.size()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1110"));
				return false;
				// sinon affiche fiche de poste
			} else {
				viderFichePoste();
				viderObjetsFichePoste();
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
				setFichePosteCourante(fp.get(0));
			}

			// ////////////////////////////////
			// dans le cas ou seul le numero de poste est saisi
		} else if (getVAL_EF_RECHERCHE() != null && !getVAL_EF_RECHERCHE().equals(Const.CHAINE_VIDE)) {
			try {
				FichePoste fiche = getFichePosteDao().chercherFichePosteAvecNumeroFP(getVAL_EF_RECHERCHE());
				if (fiche != null) {
					viderFichePoste();
					viderObjetsFichePoste();
					addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
					setFichePosteCourante(fiche);
				} else {
					setStatut(STATUT_RECHERCHE, true, MessageUtils.getMessage("ERR008"));
					return false;
				}
			} catch (Exception e) {
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_RECHERCHE()));
				return false;
			}

			// ////////////////////////////////
			// dans le cas ou seul le numero de poste est saisi
		} else if (getVAL_EF_RECHERCHE_BY_AGENT() != null && !getVAL_EF_RECHERCHE_BY_AGENT().equals(Const.CHAINE_VIDE)) {
			// recuperation agent
			Agent agent = null;
			if (getVAL_EF_RECHERCHE_BY_AGENT().length() != 0) {
				agent = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_EF_RECHERCHE_BY_AGENT()));
			}
			// l agent n existe pas
			if (null == agent || null == agent.getIdAgent()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR005", "agent"));
				return false;
			}

			ArrayList<FichePoste> fp = getFichePosteDao().listerFichePosteAvecCriteresAvances(new ArrayList<Integer>(),
					null, null, getVAL_EF_RECHERCHE().equals(Const.CHAINE_VIDE) ? null : getVAL_EF_RECHERCHE(),
					agent == null ? null : agent.getIdAgent(), false);

			// si aucun resultat ==> message erreur
			if (null == fp || 0 == fp.size()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR005", "résultat"));
				return false;
				// si plusieurs resultats ==> message erreur
			} else if (null != fp && 1 < fp.size()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR1110"));
				return false;
				// sinon affiche fiche de poste
			} else {
				viderFichePoste();
				viderObjetsFichePoste();
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
				setFichePosteCourante(fp.get(0));
			}
		}

		setStatut(STATUT_RECHERCHE);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RECHERCHE_BY_AGENT
	 * Date de création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getNOM_EF_RECHERCHE_BY_AGENT() {
		return "NOM_EF_RECHERCHE_BY_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_RECHERCHE_BY_AGENT Date de création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getVAL_EF_RECHERCHE_BY_AGENT() {
		return getZone(getNOM_EF_RECHERCHE_BY_AGENT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RECHERCHE Date de
	 * création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getNOM_EF_RECHERCHE() {
		return "NOM_EF_RECHERCHE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_RECHERCHE Date de création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getVAL_EF_RECHERCHE() {
		return getZone(getNOM_EF_RECHERCHE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NFA Date de création
	 * : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_EF_NFA() {
		return "NOM_EF_NFA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NFA Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_EF_NFA() {
		return getZone(getNOM_EF_NFA());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUMERO Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_ST_NUMERO() {
		return "NOM_ST_NUMERO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUMERO Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_ST_NUMERO() {
		return getZone(getNOM_ST_NUMERO());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BUDGET Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_BUDGET() {
		if (LB_BUDGET == null) {
			LB_BUDGET = initialiseLazyLB();
		}
		return LB_BUDGET;
	}

	/**
	 * Setter de la liste: LB_BUDGET Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private void setLB_BUDGET(String[] newLB_BUDGET) {
		LB_BUDGET = newLB_BUDGET;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BUDGET Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGET() {
		return "NOM_LB_BUDGET";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BUDGET_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGET_SELECT() {
		return "NOM_LB_BUDGET_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BUDGET Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_BUDGET() {
		return getLB_BUDGET();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_BUDGET Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_BUDGET_SELECT() {
		return getZone(getNOM_LB_BUDGET_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BUDGETE Date de création
	 * : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_BUDGETE() {
		if (LB_BUDGETE == null) {
			LB_BUDGETE = initialiseLazyLB();
		}
		return LB_BUDGETE;
	}

	/**
	 * Setter de la liste: LB_BUDGETE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private void setLB_BUDGETE(String[] newLB_BUDGETE) {
		LB_BUDGETE = newLB_BUDGETE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BUDGETE Date de création
	 * : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGETE() {
		return "NOM_LB_BUDGETE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BUDGETE_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGETE_SELECT() {
		return "NOM_LB_BUDGETE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BUDGETE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_BUDGETE() {
		return getLB_BUDGETE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_BUDGETE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_BUDGETE_SELECT() {
		return getZone(getNOM_LB_BUDGETE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REGLEMENTAIRE Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_REGLEMENTAIRE() {
		if (LB_REGLEMENTAIRE == null) {
			LB_REGLEMENTAIRE = initialiseLazyLB();
		}
		return LB_REGLEMENTAIRE;
	}

	/**
	 * Setter de la liste: LB_REGLEMENTAIRE Date de création : (07/07/11
	 * 13:23:11)
	 * 
	 * 
	 */
	private void setLB_REGLEMENTAIRE(String[] newLB_REGLEMENTAIRE) {
		LB_REGLEMENTAIRE = newLB_REGLEMENTAIRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REGLEMENTAIRE Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_REGLEMENTAIRE() {
		return "NOM_LB_REGLEMENTAIRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_REGLEMENTAIRE_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_REGLEMENTAIRE_SELECT() {
		return "NOM_LB_REGLEMENTAIRE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_REGLEMENTAIRE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_REGLEMENTAIRE() {
		return getLB_REGLEMENTAIRE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_REGLEMENTAIRE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_REGLEMENTAIRE_SELECT() {
		return getZone(getNOM_LB_REGLEMENTAIRE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUT Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_STATUT() {
		if (LB_STATUT == null) {
			LB_STATUT = initialiseLazyLB();
		}
		return LB_STATUT;
	}

	/**
	 * Setter de la liste: LB_STATUT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private void setLB_STATUT(String[] newLB_STATUT) {
		LB_STATUT = newLB_STATUT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUT Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_STATUT() {
		return "NOM_LB_STATUT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_STATUT_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_STATUT_SELECT() {
		return "NOM_LB_STATUT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_STATUT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_STATUT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_STATUT_SELECT() {
		return getZone(getNOM_LB_STATUT_SELECT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ANNEE Date de
	 * création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getNOM_EF_ANNEE() {
		return "NOM_EF_ANNEE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ANNEE Date de création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getVAL_EF_ANNEE() {
		return getZone(getNOM_EF_ANNEE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_GRADE Date de
	 * création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getNOM_EF_GRADE() {
		return "NOM_EF_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_GRADE Date de création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getVAL_EF_GRADE() {
		return getZone(getNOM_EF_GRADE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_GRADE Date de
	 * création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getNOM_EF_CODE_GRADE() {
		return "NOM_EF_CODE_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_GRADE Date de création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getVAL_EF_CODE_GRADE() {
		return getZone(getNOM_EF_CODE_GRADE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NIVEAU_ETUDE Date de
	 * création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_NIVEAU_ETUDE() {
		if (LB_NIVEAU_ETUDE == null) {
			LB_NIVEAU_ETUDE = initialiseLazyLB();
		}
		return LB_NIVEAU_ETUDE;
	}

	/**
	 * Setter de la liste: LB_NIVEAU_ETUDE Date de création : (08/07/11
	 * 09:13:07)
	 * 
	 * 
	 */
	private void setLB_NIVEAU_ETUDE(String[] newLB_NIVEAU_ETUDE) {
		LB_NIVEAU_ETUDE = newLB_NIVEAU_ETUDE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NIVEAU_ETUDE Date de
	 * création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE() {
		return "NOM_LB_NIVEAU_ETUDE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NIVEAU_ETUDE_SELECT Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE_SELECT() {
		return "NOM_LB_NIVEAU_ETUDE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NIVEAU_ETUDE Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_NIVEAU_ETUDE() {
		return getLB_NIVEAU_ETUDE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_NIVEAU_ETUDE Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_NIVEAU_ETUDE_SELECT() {
		return getZone(getNOM_LB_NIVEAU_ETUDE_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_GRADE Date de
	 * création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_GRADE() {
		return "NOM_PB_AJOUTER_GRADE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_GRADE(HttpServletRequest request) throws Exception {
		// Récupération du grade a ajouter
		int indiceGrade = (Services.estNumerique(getVAL_LB_GRADE_SELECT()) ? Integer.parseInt(getVAL_LB_GRADE_SELECT())
				: -1);
		if (indiceGrade == -1 || indiceGrade == 0 || getListeGrade().isEmpty() || indiceGrade > getListeGrade().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "grade"));
			return false;
		}

		if (indiceGrade != -1) {
			Grade g = (Grade) getListeGrade().get(indiceGrade - 1);
			Grade gr = Grade.chercherGradeByGradeInitial(getTransaction(), g.getGrade());
			addZone(getNOM_EF_GRADE(), gr.getGrade());
			addZone(getNOM_EF_CODE_GRADE(), gr.getCodeGrade());

			GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), gr.getCodeGradeGenerique());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			// on récupere la categorie et la filiere de ce grade
			String info = "Catégorie : " + gg.getCodCadre();
			if (gg != null && gg.getCdfili() != null) {
				FiliereGrade fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
				info += " <br/> Filière : " + fi.getLibFiliere();
			}
			addZone(getNOM_ST_INFO_GRADE(), info);

		}
		setAfficherListeGrade(false);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_NIVEAU_ETUDE Date de
	 * création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_NIVEAU_ETUDE() {
		return "NOM_PB_AJOUTER_NIVEAU_ETUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_NIVEAU_ETUDE(HttpServletRequest request) throws Exception {
		// Récupération du niveau d'étude a ajouter
		int indiceNiv = (Services.estNumerique(getVAL_LB_NIVEAU_ETUDE_SELECT()) ? Integer
				.parseInt(getVAL_LB_NIVEAU_ETUDE_SELECT()) : -1);
		if (indiceNiv == -1 || getListeNiveauEtude().size() == 0 || indiceNiv > getListeNiveauEtude().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Niveaux d'étude"));
			return false;
		}

		if (indiceNiv > 0) {
			NiveauEtude n = (NiveauEtude) getListeNiveauEtude().get(indiceNiv - 1);

			if (n != null) {
				if (getListeTousNiveau() == null) {
					setListeTousNiveau(new ArrayList<NiveauEtude>());
				}

				if (!getListeTousNiveau().contains(n)) {
					getListeTousNiveau().add(n);
				}

				String nivEtMulti = Const.CHAINE_VIDE;
				if (getListeTousNiveau() != null) {
					for (int i = 0; i < getListeTousNiveau().size(); i++) {
						NiveauEtude nivEt = (NiveauEtude) getListeTousNiveau().get(i);
						nivEtMulti += nivEt.getCodeNiveauEtude() + ", ";
					}
				}
				addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(),
						nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);
			}
		}
		setAfficherListeNivEt(false);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NIVEAU_ETUDE Date
	 * de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NIVEAU_ETUDE() {
		return "NOM_PB_SUPPRIMER_NIVEAU_ETUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_NIVEAU_ETUDE(HttpServletRequest request) throws Exception {

		// Suppression du dernier niveau d'étude de la liste
		if (getListeTousNiveau() != null && getListeTousNiveau().size() != 0) {
			NiveauEtude niv = (NiveauEtude) getListeTousNiveau().get(getListeTousNiveau().size() - 1);
			getListeTousNiveau().remove(niv);

			// Rafraichissement de la liste
			String nivEtMulti = Const.CHAINE_VIDE;
			if (getListeTousNiveau() != null) {
				for (int i = 0; i < getListeTousNiveau().size(); i++) {
					NiveauEtude nivEt = (NiveauEtude) getListeTousNiveau().get(i);
					nivEtMulti += nivEt.getCodeNiveauEtude() + ", ";
				}
			}
			addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(),
					nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);

		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_GRADE Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private String[] getLB_GRADE() {
		if (LB_GRADE == null) {
			LB_GRADE = initialiseLazyLB();
		}
		return LB_GRADE;
	}

	/**
	 * Setter de la liste: LB_GRADE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private void setLB_GRADE(String[] newLB_GRADE) {
		LB_GRADE = newLB_GRADE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_GRADE Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_GRADE() {
		return "NOM_LB_GRADE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_GRADE_SELECT Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_GRADE_SELECT() {
		return "NOM_LB_GRADE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_GRADE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String[] getVAL_LB_GRADE() {
		return getLB_GRADE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_GRADE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getVAL_LB_GRADE_SELECT() {
		return getZone(getNOM_LB_GRADE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_LOC Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private String[] getLB_LOC() {
		if (LB_LOC == null) {
			LB_LOC = initialiseLazyLB();
		}
		return LB_LOC;
	}

	/**
	 * Setter de la liste: LB_LOC Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private void setLB_LOC(String[] newLB_LOC) {
		LB_LOC = newLB_LOC;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_LOC Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_LOC() {
		return "NOM_LB_LOC";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_LOC_SELECT Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_LOC_SELECT() {
		return "NOM_LB_LOC_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_LOC Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String[] getVAL_LB_LOC() {
		return getLB_LOC();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_LOC Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getVAL_LB_LOC_SELECT() {
		return getZone(getNOM_LB_LOC_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_POSTE Date de
	 * création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private String[] getLB_TITRE_POSTE() {
		if (LB_TITRE_POSTE == null) {
			LB_TITRE_POSTE = initialiseLazyLB();
		}
		return LB_TITRE_POSTE;
	}

	/**
	 * Setter de la liste: LB_TITRE_POSTE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private void setLB_TITRE_POSTE(String[] newLB_TITRE_POSTE) {
		LB_TITRE_POSTE = newLB_TITRE_POSTE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_POSTE Date de
	 * création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_TITRE_POSTE() {
		return "NOM_LB_TITRE_POSTE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_POSTE_SELECT Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_TITRE_POSTE_SELECT() {
		return "NOM_LB_TITRE_POSTE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_POSTE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String[] getVAL_LB_TITRE_POSTE() {
		return getLB_TITRE_POSTE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_TITRE_POSTE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getVAL_LB_TITRE_POSTE_SELECT() {
		return getZone(getNOM_LB_TITRE_POSTE_SELECT());
	}

	private ArrayList<Budget> getListeBudget() {
		return listeBudget;
	}

	private void setListeBudget(ArrayList<Budget> listeBudget) {
		this.listeBudget = listeBudget;
	}

	private ArrayList<Grade> getListeGrade() {
		return listeGrade;
	}

	private void setListeGrade(ArrayList<Grade> listeGrade) {
		this.listeGrade = listeGrade;
	}

	private ArrayList<EntiteGeo> getListeLocalisation() {
		return listeLocalisation;
	}

	private void setListeLocalisation(ArrayList<EntiteGeo> listeLocalisation) {
		this.listeLocalisation = listeLocalisation;
	}

	private ArrayList<StatutFP> getListeStatut() {
		return listeStatut;
	}

	private void setListeStatut(ArrayList<StatutFP> listeStatut) {
		this.listeStatut = listeStatut;
	}

	public ArrayList<TitrePoste> getListeTitre() {
		return listeTitre;
	}

	private void setListeTitre(ArrayList<TitrePoste> listeTitre) {
		this.listeTitre = listeTitre;
	}

	private FichePoste getFichePosteCourante() {
		return fichePosteCourante;
	}

	/**
	 * Setter de la fiche de poste courante.
	 * 
	 * @param fichePosteCourante
	 * @throws Exception
	 */
	private void setFichePosteCourante(FichePoste fichePosteCourante) throws Exception {
		this.fichePosteCourante = fichePosteCourante;

		if (fichePosteCourante != null && fichePosteCourante.getIdFichePoste() != null
				&& !getVAL_ST_ACTION().equals(ACTION_CREATION)) {
			// Vérifie l'affectation
			ArrayList<Affectation> liste = getAffectationDao().listerAffectationAvecFPPrimaireOuSecondaire(
					getFichePosteCourante().getIdFichePoste());
			setFpCouranteAffectee(getFichePosteDao().estAffectee(getFichePosteCourante().getIdFichePoste(), liste));

			// Recherche de tous les liens FicheEmploi / FichePoste
			ArrayList<FEFP> liens1 = getFefpDao().listerFEFPAvecFP(getFichePosteCourante().getIdFichePoste());
			// Init fiches emploi
			setEmploiPrimaire(getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(true, liens1));

			// Recherche de tous les liens FicheEmploi / FichePoste
			ArrayList<FEFP> liens2 = getFefpDao().listerFEFPAvecFP(getFichePosteCourante().getIdFichePoste());
			setEmploiSecondaire(getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(false, liens2));

			// Init Service
			if (getFichePosteCourante().getIdServiceAds() != null) {
				setService(adsService.getEntiteByIdEntite(getFichePosteCourante().getIdServiceAds()));
			}
			// Init Responsable
			if (getFichePosteCourante().getIdResponsable() != null) {
				setResponsable(getFichePosteDao().chercherFichePoste(getFichePosteCourante().getIdResponsable()));
			}
			// Init Remplacement
			if (getFichePosteCourante().getIdRemplacement() != null) {
				setRemplacement(getFichePosteDao().chercherFichePoste(getFichePosteCourante().getIdRemplacement()));
			}
			// Init Infos Affectation FP
			try {
				setAgentCourant(getAgentDao().chercherAgentAffecteFichePoste(getFichePosteCourante().getIdFichePoste()));
			} catch (Exception e) {
				setAgentCourant(null);
			}
			// si on a pas trouve d'gent affecté sur FP primaire, on recherche
			// sur secondaire
			if (getAgentCourant() == null) {
				try {
					setAgentCourant(getAgentDao().chercherAgentAffecteFichePosteSecondaire(
							getFichePosteCourante().getIdFichePoste()));
				} catch (Exception e) {
					setAgentCourant(null);
				}
			}
			if (getAgentCourant() != null) {
				try {
					Affectation aff = getAffectationDao().chercherAffectationActiveAvecAgent(
							getAgentCourant().getIdAgent());
					setAffectationCourante(aff);
				} catch (Exception e) {
					setAffectationCourante(null);
				}
				try {
					Contrat c = getContratDao().chercherContratAgentDateComprise(getAgentCourant().getIdAgent(),
							new Date());
					setContratCourant(c);
				} catch (Exception e) {
					setContratCourant(null);
				}
				if (getContratCourant() != null && getContratCourant().getIdTypeContrat() != null) {
					setTypeContratCourant(getTypeContratDao().chercherTypeContrat(
							getContratCourant().getIdTypeContrat()));
				}
			}

			majChangementFEAutorise();
		} else {
			setFpCouranteAffectee(false);
		}
	}

	private void initialiseActivites() throws Exception {
		// on fait une liste de toutes les activites
		setListeToutesActi(new ArrayList<Activite>());
		boolean trouve = false;
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les activites de la FDP
			setListeActiFP(getActiviteFPDao().listerActiviteFPAvecFP(getFichePosteCourante().getIdFichePoste()));
			for (ActiviteFP actiFP : getListeActiFP()) {
				trouve = false;
				Activite activite = getActiviteDao().chercherActivite(actiFP.getIdActivite());
				for (Activite tteActi : getListeToutesActi()) {
					if (tteActi.getIdActivite().toString().equals(activite.getIdActivite().toString())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesActi().add(activite);
					getHashOrigineActivite().put(activite.getIdActivite(), "FDP");
				}
			}
		} else {
			setListeActiFP(new ArrayList<ActiviteFP>());
		}

		// on recupere les activites des differentes FE
		trouve = false;
		if (getEmploiPrimaire() != null && getEmploiPrimaire().getIdFicheEmploi() != null) {

			// Recherche de tous les liens FicheEmploi / Activite
			ArrayList<ActiviteFE> liens = getActiviteFEDao().listerActiviteFEAvecFE(
					getEmploiPrimaire().getIdFicheEmploi());
			setListeActiFEP(getActiviteDao().listerActiviteAvecFE(liens));
			for (int i = 0; i < getListeActiFEP().size(); i++) {
				trouve = false;
				Activite actiFP = (Activite) getListeActiFEP().get(i);
				for (int j = 0; j < getListeToutesActi().size(); j++) {
					Activite tteActi = (Activite) getListeToutesActi().get(j);
					if (tteActi.getIdActivite().toString().equals(actiFP.getIdActivite().toString())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesActi().add(actiFP);
					getHashOrigineActivite().put(actiFP.getIdActivite(), getEmploiPrimaire().getRefMairie());
				}
			}
		} else {
			setListeActiFEP(new ArrayList<Activite>());
		}

		if (getEmploiSecondaire() != null && getEmploiSecondaire().getIdFicheEmploi() != null) {
			trouve = false;

			// Recherche de tous les liens FicheEmploi / Activite
			ArrayList<ActiviteFE> liens = getActiviteFEDao().listerActiviteFEAvecFE(
					getEmploiSecondaire().getIdFicheEmploi());
			setListeActiFES(getActiviteDao().listerActiviteAvecFE(liens));
			for (Activite actiFP : getListeActiFES()) {
				trouve = false;
				for (Activite tteActi : getListeToutesActi()) {
					if (tteActi.getIdActivite().toString().equals(actiFP.getIdActivite().toString())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesActi().add(actiFP);
					getHashOrigineActivite().put(actiFP.getIdActivite(), getEmploiSecondaire().getRefMairie());
				}
			}

		} else {
			setListeActiFES(new ArrayList<Activite>());
		}

		// on recupere les activites selectionnées dans l'ecran de selection
		@SuppressWarnings("unchecked")
		ArrayList<Activite> listeActiSelect = (ArrayList<Activite>) VariablesActivite.recuperer(this, "ACTIVITE_PRINC");
		if (listeActiSelect != null && !listeActiSelect.isEmpty()) {
			if (getListeAjoutActiFP() != null) {
				getListeAjoutActiFP().addAll(listeActiSelect);
			}
		}
		for (Activite a : getListeAjoutActiFP()) {
			if (a != null) {
				if (getListeToutesActi() == null) {
					setListeToutesActi(new ArrayList<Activite>());
				}
				if (!getListeToutesActi().contains(a)) {
					getListeToutesActi().add(a);
					getHashOrigineActivite().put(a.getIdActivite(), "FDP");
				}
			}
		}

		// Si liste activites vide alors initialisation.
		boolean dejaCoche = false;
		for (int i = 0; i < getListeToutesActi().size(); i++) {
			dejaCoche = false;
			Activite activite = (Activite) getListeToutesActi().get(i);
			String origineActi = (String) getHashOrigineActivite().get(activite.getIdActivite());

			if (activite != null) {
				addZone(getNOM_ST_ID_ACTI(i), activite.getIdActivite().toString());
				addZone(getNOM_ST_LIB_ACTI(i), activite.getNomActivite());
				addZone(getNOM_ST_LIB_ORIGINE_ACTI(i), origineActi);
				addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());

				if (getListeActiFP() != null) {
					// si l'activite fait partie de la liste des activites de la
					// FDP
					for (ActiviteFP actiFP : getListeActiFP()) {
						Activite activiteFP = getActiviteDao().chercherActivite(actiFP.getIdActivite());
						if (activiteFP.getIdActivite().toString().equals(activite.getIdActivite().toString())) {
							addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
							}
						}
					}
				} else {
					addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
				}
				if (getListeAjoutActiFP() != null) {
					// si l'activite fait partie de la liste des activites
					// ajoutées à la FDP
					for (Activite activiteFP : getListeAjoutActiFP()) {
						if (activiteFP.getIdActivite().toString().equals(activite.getIdActivite().toString())) {
							addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
							}
						}
					}

				} else {
					addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
				}
			}
		}
		VariablesActivite.enlever(this, "ACTIVITE_PRINC");
		VariablesActivite.enlever(this, "LISTEACTIVITE");
	}

	private void initialiseInfoEmploi() throws Exception {
		// on fait une liste de toutes les niveau etude
		setListeTousNiveau(new ArrayList<NiveauEtude>());

		// niveau etude
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les niveau etude de la FDP
			setListeNiveauFP(getNiveauEtudeFPDao().listerNiveauEtudeFPAvecFP(getFichePosteCourante().getIdFichePoste()));
			for (NiveauEtudeFP niveauFP : getListeNiveauFP()) {
				NiveauEtude niveau = getNiveauEtudeDao().chercherNiveauEtude(niveauFP.getIdNiveauEtude());
				getListeTousNiveau().add(niveau);
			}
		} else {
			setListeNiveauFP(new ArrayList<NiveauEtudeFP>());
		}

		String nivEtMulti = Const.CHAINE_VIDE;
		for (NiveauEtude nivEt : getListeTousNiveau()) {
			nivEtMulti += nivEt.getCodeNiveauEtude() + ", ";
		}
		addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(),
				nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe ayant elemine de la liste l1 les éléments en communs
	 *         avec l2 fonctionne uniquement avec une liste l1 n'ayant pas 2
	 *         elements identiques
	 */
	public static ArrayList<Activite> elim_doubure_activites(ArrayList<Activite> l1, ArrayList<Activite> l2) {
		if (null == l1) {
			return null;
		}

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((Activite) l2.get(i)).getIdActivite().toString()).equals(((Activite) l1.get(j))
							.getIdActivite().toString())) {
						l1.remove(j);
					}
				}
			}
		}
		return l1;
	}

	private void initialiseObservation() {
		// Init observation
		setObservation(Const.CHAINE_VIDE);
		if (getFichePosteCourante() != null) {
			if (getFichePosteCourante().getObservation() != null
					&& getFichePosteCourante().getObservation().length() != 0) {
				setObservation(getObservation() + " " + getFichePosteCourante().getObservation());
			}
		}

		addZone(getNOM_EF_OBSERVATION(), getObservation() == null ? Const.CHAINE_VIDE : getObservation());
	}

	private void initialiseMission() {
		// Init missions
		setMission(Const.CHAINE_VIDE);
		if (getFichePosteCourante() != null) {
			if (getFichePosteCourante().getMissions() != null && getFichePosteCourante().getMissions().length() != 0) {
				setMission(getMission() + " " + getFichePosteCourante().getMissions());
			} else {
				if (getEmploiPrimaire() != null) {
					if (!getMission().toUpperCase().contains(getEmploiPrimaire().getDefinitionEmploi().toUpperCase())) {
						setMission(getMission() + " " + getEmploiPrimaire().getDefinitionEmploi());
					}
				}
				if (getEmploiSecondaire() != null) {
					if (!getMission().toUpperCase().contains(getEmploiSecondaire().getDefinitionEmploi().toUpperCase())) {
						setMission(getMission() + " " + getEmploiSecondaire().getDefinitionEmploi());
					}
				}
			}
		}

		addZone(getNOM_EF_MISSIONS(), getMission() == null ? Const.CHAINE_VIDE : getMission());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_EMPLOI_PRIMAIRE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE() {
		return "NOM_PB_RECHERCHE_EMPLOI_PRIMAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHE_EMPLOI_PRIMAIRE(HttpServletRequest request) throws Exception {
		// 17319 : on envoi la recherche
		if (getVAL_ST_EMPLOI_PRIMAIRE().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "emploi primaire"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, getZone(getNOM_ST_EMPLOI_PRIMAIRE()));
		setStatut(STATUT_EMPLOI_PRIMAIRE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_EMPLOI_SECONDAIRE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHE_EMPLOI_SECONDAIRE() {
		return "NOM_PB_RECHERCHE_EMPLOI_SECONDAIRE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_EMPLOI_SECONDAIRE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE() {
		return "NOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHE_EMPLOI_SECONDAIRE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_EMPLOI_SECONDAIRE, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_EMPLOI_SECONDAIRE(HttpServletRequest request) throws Exception {
		// On enleve la fiche emploi secondaire selectionnée
		setEmploiSecondaire(null);
		addZone(getNOM_ST_EMPLOI_SECONDAIRE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Getter de la fiche emploi primaire
	 * 
	 * @return FicheEmploi primaire
	 */
	public FicheEmploi getEmploiPrimaire() {
		return emploiPrimaire;
	}

	/**
	 * Setter de la fiche emploi primaire
	 * 
	 * @param newEmploiPrimaire
	 * @throws Exception
	 *             RG_PE_FP_C09
	 */
	private void setEmploiPrimaire(FicheEmploi newEmploiPrimaire) throws Exception {
		// RG_PE_FP_C09
		this.emploiPrimaire = newEmploiPrimaire;
		if (newEmploiPrimaire != null) {
			initialiseInfoEmploi();
			initialiseActivites();
			initialiseCompetence();
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EMPLOI_PRIMAIRE Date
	 * de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getNOM_ST_EMPLOI_PRIMAIRE() {
		return "NOM_ST_EMPLOI_PRIMAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_EMPLOI_PRIMAIRE Date de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getVAL_ST_EMPLOI_PRIMAIRE() {
		return getZone(getNOM_ST_EMPLOI_PRIMAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EMPLOI_SECONDAIRE
	 * Date de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getNOM_ST_EMPLOI_SECONDAIRE() {
		return "NOM_ST_EMPLOI_SECONDAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_EMPLOI_SECONDAIRE Date de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getVAL_ST_EMPLOI_SECONDAIRE() {
		return getZone(getNOM_ST_EMPLOI_SECONDAIRE());
	}

	private FicheEmploi getEmploiSecondaire() {
		return emploiSecondaire;
	}

	/**
	 * Setter de la fiche emploi secondaire.
	 * 
	 * @param emploiSecondaire
	 * @throws Exception
	 */
	private void setEmploiSecondaire(FicheEmploi newEmploiSecondaire) throws Exception {
		this.emploiSecondaire = newEmploiSecondaire;
		setListeActiFES(null);
		setListeCompFES(null);
		if (newEmploiSecondaire != null) {
			initialiseInfoEmploi();
			initialiseActivites();
			initialiseCompetence();
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_ACTIVITE Date de
	 * création : (25/07/11 09:42:05)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_ACTIVITE() {
		return "NOM_PB_AJOUTER_ACTIVITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/07/11 09:42:05)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_ACTIVITE(HttpServletRequest request) throws Exception {
		if (getListeToutesActi() != null) {
			listeToutesActi.addAll(getListeToutesActi());
		}
		VariablesActivite.ajouter(this, "LISTEACTIVITE", listeToutesActi);
		setStatut(STATUT_ACTI_PRINC, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_COMPETENCE_SAVOIR
	 * Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_SAVOIR() {
		return "NOM_PB_AJOUTER_COMPETENCE_SAVOIR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_COMPETENCE_SAVOIR(HttpServletRequest request) throws Exception {
		ArrayList<Competence> listeToutesCompSavoir = new ArrayList<Competence>();
		if (getListeToutesComp() != null) {
			for (Competence c : getListeToutesComp()) {
				if (c.getIdTypeCompetence().toString().equals(EnumTypeCompetence.SAVOIR.getCode().toString())) {
					listeToutesCompSavoir.add(c);
				}
			}
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCESAVOIR", listeToutesCompSavoir);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE() {
		return "NOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_COMPETENCE_SAVOIR_FAIRE(HttpServletRequest request) throws Exception {
		ArrayList<Competence> listeToutesCompSavoirFaire = new ArrayList<Competence>();
		if (getListeToutesComp() != null) {
			for (Competence c : getListeToutesComp()) {
				if (c.getIdTypeCompetence().toString().equals(EnumTypeCompetence.SAVOIR_FAIRE.getCode().toString())) {
					listeToutesCompSavoirFaire.add(c);
				}
			}
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCESAVOIRFAIRE", listeToutesCompSavoirFaire);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_COMPORTEMENT Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT() {
		return "NOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_COMPETENCE_COMPORTEMENT(HttpServletRequest request) throws Exception {
		ArrayList<Competence> listeToutesCompComportement = new ArrayList<>();
		if (getListeToutesComp() != null) {
			for (Competence c : getListeToutesComp()) {
				if (c.getIdTypeCompetence().toString().equals(EnumTypeCompetence.COMPORTEMENT.getCode().toString())) {
					listeToutesCompComportement.add(c);
				}
			}
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCECOMPORTEMENT", listeToutesCompComportement);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER Date de création :
	 * (25/07/11 14:54:42)
	 * 
	 * 
	 */
	public String getNOM_PB_MODIFIER() {
		return "NOM_PB_MODIFIER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/07/11 14:54:42)
	 * 
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (26/07/11 09:08:33)
	 * 
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (25/07/11 16:45:35)
	 * 
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	private ArrayList<Horaire> getListeHoraire() {
		return listeHoraire;
	}

	private void setListeHoraire(ArrayList<Horaire> listeHoraire) {
		this.listeHoraire = listeHoraire;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_SPECIFICITES Date
	 * de création : (27/07/11 15:49:01)
	 * 
	 * 
	 */
	public String getNOM_PB_MODIFIER_SPECIFICITES() {
		return "NOM_PB_MODIFIER_SPECIFICITES";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 15:49:01)
	 * 
	 * 
	 */
	public boolean performPB_MODIFIER_SPECIFICITES(HttpServletRequest request) throws Exception {
		// Mise à jour des liste de spécificités a modifier.
		if (getListeAvantage() != null) {
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE, getListeAvantage());
		}
		if (getListeDelegation() != null) {
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION, getListeDelegation());
		}
		if (getListeRegime() != null) {
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN, getListeRegime());
		}
		if (getListePrimePointageFP() != null) {
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_PRIME_POINTAGE, getListePrimePointageFP());
		}

		setStatut(STATUT_SPECIFICITES, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_RESPONSABLE Date
	 * de création : (29/07/11 11:42:25)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHER_RESPONSABLE() {
		return "NOM_PB_RECHERCHER_RESPONSABLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/07/11 11:42:25)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHER_RESPONSABLE(HttpServletRequest request) throws Exception {
		if (!getVAL_ST_ID_SERVICE_ADS().equals(Const.CHAINE_VIDE)) {
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_SERVICE,
					adsService.getEntiteByIdEntite(new Integer(getVAL_ST_ID_SERVICE_ADS())));
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE, Boolean.TRUE);

		setStatut(STATUT_RESPONSABLE, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RESPONSABLE Date de
	 * création : (29/07/11 13:47:43)
	 * 
	 * 
	 */
	public String getNOM_ST_RESPONSABLE() {
		return "NOM_ST_RESPONSABLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RESPONSABLE
	 * Date de création : (29/07/11 13:47:43)
	 * 
	 * 
	 */
	public String getVAL_ST_RESPONSABLE() {
		return getZone(getNOM_ST_RESPONSABLE());
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
		return getNOM_EF_RECHERCHE();
	}

	/**
	 * @param focus
	 *            à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Getter de la FichePoste Responsable.
	 * 
	 */
	private FichePoste getResponsable() {
		return responsable;
	}

	/**
	 * Setter de la FichePoste Responsable.
	 * 
	 * @param responsable
	 */
	private void setResponsable(FichePoste resp) throws Exception {
		this.responsable = resp;
		if (resp != null) {
			try {
				setAgtResponsable(getAgentDao().chercherAgentAffecteFichePoste(getResponsable().getIdFichePoste()));
			} catch (Exception e) {
				setAgtResponsable(null);
			}
			setTitrePosteResponsable(getTitrePosteDao().chercherTitrePoste(getResponsable().getIdTitrePoste()));
		} else {
			setAgtResponsable(null);
			setTitrePosteResponsable(null);
		}
	}

	/**
	 * Retourne la liste des AvantageNature.
	 * 
	 * @return listeAvantage
	 */
	public ArrayList<AvantageNature> getListeAvantage() {
		if (listeAvantage == null) {
			listeAvantage = new ArrayList<AvantageNature>();
		}
		return listeAvantage;
	}

	/**
	 * Met a jour la liste des AvantageNature.
	 * 
	 * @param listeAvantage
	 */
	private void setListeAvantage(ArrayList<AvantageNature> listeAvantage) {
		this.listeAvantage = listeAvantage;
	}

	/**
	 * Retourne la liste des AvantageNature a ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	private ArrayList<AvantageNature> getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null) {
			listeAvantageAAjouter = new ArrayList<AvantageNature>();
		}
		return listeAvantageAAjouter;
	}

	/**
	 * Met a jour la liste des AvantageNature a ajouter.
	 * 
	 * @param listeAvantageAAjouter
	 *            listeAvantageAAjouter à définir
	 */
	private void setListeAvantageAAjouter(ArrayList<AvantageNature> listeAvantageAAjouter) {
		this.listeAvantageAAjouter = listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des AvantageNature a supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList<AvantageNature> getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null) {
			listeAvantageASupprimer = new ArrayList<AvantageNature>();
		}
		return listeAvantageASupprimer;
	}

	/**
	 * Met a jour la liste des AvantageNature a supprimer.
	 * 
	 * @param listeAvantageASupprimer
	 */
	private void setListeAvantageASupprimer(ArrayList<AvantageNature> listeAvantageASupprimer) {
		this.listeAvantageASupprimer = listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des Delegation.
	 * 
	 * @return listeDelegation
	 */
	public ArrayList<Delegation> getListeDelegation() {
		if (listeDelegation == null) {
			listeDelegation = new ArrayList<Delegation>();
		}
		return listeDelegation;
	}

	/**
	 * Met a jour la liste des Delegation.
	 * 
	 * @param listeDelegation
	 *            listeDelegation à définir
	 */
	private void setListeDelegation(ArrayList<Delegation> listeDelegation) {
		this.listeDelegation = listeDelegation;
	}

	/**
	 * Retourne la liste des Delegation a ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	private ArrayList<Delegation> getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null) {
			listeDelegationAAjouter = new ArrayList<Delegation>();
		}
		return listeDelegationAAjouter;
	}

	/**
	 * Met a jour la liste des Delegation a ajouter.
	 * 
	 * @param listeDelegationAAjouter
	 */
	private void setListeDelegationAAjouter(ArrayList<Delegation> listeDelegationAAjouter) {
		this.listeDelegationAAjouter = listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des Delegation a supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList<Delegation> getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null) {
			listeDelegationASupprimer = new ArrayList<Delegation>();
		}
		return listeDelegationASupprimer;
	}

	/**
	 * Met a jour la liste des Delegation a supprimer.
	 * 
	 * @param listeDelegationASupprimer
	 */
	private void setListeDelegationASupprimer(ArrayList<Delegation> listeDelegationASupprimer) {
		this.listeDelegationASupprimer = listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeRegime
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegime() {
		if (listeRegime == null) {
			listeRegime = new ArrayList<RegimeIndemnitaire>();
		}
		return listeRegime;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeRegime
	 */
	private void setListeRegime(ArrayList<RegimeIndemnitaire> listeRegime) {
		this.listeRegime = listeRegime;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire a ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null) {
			listeRegimeAAjouter = new ArrayList<RegimeIndemnitaire>();
		}
		return listeRegimeAAjouter;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire a ajouter.
	 * 
	 * @param listeRegimeAAjouter
	 */
	private void setListeRegimeAAjouter(ArrayList<RegimeIndemnitaire> listeRegimeAAjouter) {
		this.listeRegimeAAjouter = listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire a supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList<RegimeIndemnitaire> getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null) {
			listeRegimeASupprimer = new ArrayList<RegimeIndemnitaire>();
		}
		return listeRegimeASupprimer;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire a supprimer.
	 * 
	 * @param listeRegimeASupprimer
	 */
	private void setListeRegimeASupprimer(ArrayList<RegimeIndemnitaire> listeRegimeASupprimer) {
		this.listeRegimeASupprimer = listeRegimeASupprimer;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire.
	 * 
	 * @return listePrimePointage
	 */
	public ArrayList<PrimePointageFP> getListePrimePointageFP() {
		if (listePrimePointageFP == null) {
			listePrimePointageFP = new ArrayList<PrimePointageFP>();
		}
		return listePrimePointageFP;
	}

	/**
	 * Met a jour la liste des PrimePointageIndemnitaire.
	 * 
	 * @param listePrimePointage
	 */
	private void setListePrimePointageFP(ArrayList<PrimePointageFP> listePrimePointageFP) {
		this.listePrimePointageFP = listePrimePointageFP;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire a ajouter.
	 * 
	 * @return listePrimePointageAAjouter
	 */
	private ArrayList<PrimePointageFP> getListePrimePointageFPAAjouter() {
		if (listePrimePointageFPAAjouter == null) {
			listePrimePointageFPAAjouter = new ArrayList<PrimePointageFP>();
		}
		return listePrimePointageFPAAjouter;
	}

	/**
	 * Met a jour la liste des PrimePointageIndemnitaire a ajouter.
	 * 
	 * @param listePrimePointageAAjouter
	 */
	private void setListePrimePointageFPAAjouter(ArrayList<PrimePointageFP> listePrimePointageFPAAjouter) {
		this.listePrimePointageFPAAjouter = listePrimePointageFPAAjouter;
	}

	/**
	 * Retourne la liste des PrimePointageIndemnitaire a supprimer.
	 * 
	 * @return listePrimePointageASupprimer
	 */
	private ArrayList<PrimePointageFP> getListePrimePointageFPASupprimer() {
		if (listePrimePointageFPASupprimer == null) {
			listePrimePointageFPASupprimer = new ArrayList<PrimePointageFP>();
		}
		return listePrimePointageFPASupprimer;
	}

	/**
	 * Met a jour la liste des PrimePointageIndemnitaire a supprimer.
	 * 
	 * @param listePrimePointageASupprimer
	 */
	private void setListePrimePointageFPASupprimer(ArrayList<PrimePointageFP> listePrimePointageFPASupprimer) {
		this.listePrimePointageFPASupprimer = listePrimePointageFPASupprimer;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_OBSERVATION Date de
	 * création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getNOM_EF_OBSERVATION() {
		return "NOM_EF_OBSERVATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_OBSERVATION Date de création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getVAL_EF_OBSERVATION() {
		return getZone(getNOM_EF_OBSERVATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MISSIONS Date de
	 * création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getNOM_EF_MISSIONS() {
		return "NOM_EF_MISSIONS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MISSIONS Date de création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getVAL_EF_MISSIONS() {
		return getZone(getNOM_EF_MISSIONS());
	}

	/**
	 * Retourne le service sélectionné.
	 * 
	 * @return service
	 */
	private EntiteDto getService() {
		return service;
	}

	/**
	 * Met a jour le service sélectionné.
	 * 
	 * @param service
	 *            service à définir
	 */
	private void setService(EntiteDto service) {
		this.service = service;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_DEBUT_APPLI_SERV Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT_APPLI_SERV() {
		return "NOM_EF_DATE_DEBUT_APPLI_SERV";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT_APPLI_SERV Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT_APPLI_SERV() {
		return getZone(getNOM_EF_DATE_DEBUT_APPLI_SERV());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_OPI Date de
	 * création : (22/08/11 11:07:24)
	 * 
	 * 
	 */
	public String getNOM_EF_OPI() {
		return "NOM_EF_OPI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_OPI
	 * Date de création : (22/08/11 11:07:24)
	 * 
	 * 
	 */
	public String getVAL_EF_OPI() {
		return getZone(getNOM_EF_OPI());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_GRADE Date de
	 * création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_GRADE() {
		return "NOM_PB_AFFICHER_LISTE_GRADE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_GRADE(HttpServletRequest request) throws Exception {
		addZone(getNOM_LB_GRADE_SELECT(), "0");
		setAfficherListeGrade(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_NIVEAU Date
	 * de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_NIVEAU() {
		return "NOM_PB_AFFICHER_LISTE_NIVEAU";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_NIVEAU(HttpServletRequest request) throws Exception {
		addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), "0");
		setAfficherListeNivEt(true);
		return true;
	}

	/**
	 * Retourne vrai si la liste des grades doit être affichée.
	 * 
	 * @return afficherListeGrade boolean
	 */
	public boolean isAfficherListeGrade() {
		return afficherListeGrade;
	}

	/**
	 * Met a jour l'indicateur d'afichage de la liste des grades.
	 * 
	 * @param afficherListeGrade
	 *            boolean
	 */
	private void setAfficherListeGrade(boolean afficherListeGrade) {
		this.afficherListeGrade = afficherListeGrade;
	}

	/**
	 * Retourne vrai si la liste des niveaux d'étude doit être affichée.
	 * 
	 * @return afficherListeNivEt boolean
	 */
	public boolean isAfficherListeNivEt() {
		return afficherListeNivEt;
	}

	/**
	 * Met a jour l'indicateur d'afichage de la liste des niveaux d'étude.
	 * 
	 * @param afficherListeNivEt
	 *            boolean
	 */
	private void setAfficherListeNivEt(boolean afficherListeNivEt) {
		this.afficherListeNivEt = afficherListeNivEt;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_POSTE Date de
	 * création : (30/08/11 10:25:41)
	 * 
	 * 
	 */
	public String getNOM_EF_TITRE_POSTE() {
		return "NOM_EF_TITRE_POSTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_POSTE Date de création : (30/08/11 10:25:41)
	 * 
	 * 
	 */
	public String getVAL_EF_TITRE_POSTE() {
		return getZone(getNOM_EF_TITRE_POSTE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NIVEAU_ETUDE_MULTI
	 * Date de création : (31/08/11 10:18:01)
	 * 
	 * 
	 */
	public String getNOM_EF_NIVEAU_ETUDE_MULTI() {
		return "NOM_EF_NIVEAU_ETUDE_MULTI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NIVEAU_ETUDE_MULTI Date de création : (31/08/11 10:18:01)
	 * 
	 * 
	 */
	public String getVAL_EF_NIVEAU_ETUDE_MULTI() {
		return getZone(getNOM_EF_NIVEAU_ETUDE_MULTI());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DUPLIQUER_FP Date de
	 * création : (02/09/11 15:58:29)
	 * 
	 * 
	 */
	public String getNOM_PB_DUPLIQUER_FP() {
		return "NOM_PB_DUPLIQUER_FP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/09/11 15:58:29)
	 * 
	 * 
	 */
	public boolean performPB_DUPLIQUER_FP(HttpServletRequest request) throws Exception {
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			FichePoste fichePDupliquee = (FichePoste) getFichePosteCourante().clone();
			// par defaut on met l'année courante dans l'annéee
			String anneeCourante = Services.dateDuJour().substring(6, 10);
			fichePDupliquee.setAnneeCreation(Integer.valueOf(anneeCourante));
			addZone(getNOM_EF_ANNEE(), anneeCourante);
			fichePDupliquee.setIdFichePoste(null);
			fichePDupliquee.setNumFp(null);
			addZone(getNOM_ST_NUMERO(), Const.CHAINE_VIDE);

			// Duplique les Delegation
			getListeDelegationAAjouter().clear();
			getListeDelegationASupprimer().clear();
			if (getListeDelegation() != null) {
				getListeDelegationAAjouter().addAll(getListeDelegation());
			}

			// Duplique les Avantages en nature
			getListeAvantageAAjouter().clear();
			getListeAvantageASupprimer().clear();
			if (getListeAvantage() != null) {
				getListeAvantageAAjouter().addAll(getListeAvantage());
			}

			// Duplique les Regime indemnitaire
			getListeRegimeAAjouter().clear();
			getListeRegimeASupprimer().clear();
			if (getListeRegime() != null) {
				getListeRegimeAAjouter().addAll(getListeRegime());
			}

			setAgentCourant(null);
			setAffectationCourante(null);
			setContratCourant(null);
			setTypeContratCourant(null);

			initialiseInfoEmploi();
			initialiseActivites();
			initialiseCompetence();

			setFichePosteCourante(fichePDupliquee);

			setStatut(STATUT_A_DUPLIQUER);
			addZone(getNOM_ST_ACTION(), ACTION_DUPLICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("INF107"));
			return false;
		}
		getTransaction().commitTransaction();
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_FP Date de création
	 * : (09/09/11 09:06:13)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_FP() {
		return "NOM_PB_AJOUTER_FP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 09:06:13)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_FP(HttpServletRequest request) throws Exception {
		viderFichePoste();
		viderObjetsFichePoste();
		setFichePosteCourante(new FichePoste());

		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_AVANCEE Date de
	 * création : (13/09/11 08:35:27)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHE_AVANCEE() {
		return "NOM_PB_RECHERCHE_AVANCEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:35:27)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHE_AVANCEE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_RECHERCHE_AVANCEE, true);
		return true;
	}

	/**
	 * Vérifie si la modification des spécificités doit être possible.
	 * 
	 * @return afficherModifSpecificites boolean RG_PE_FP_A03
	 */
	public boolean isAfficherModifSpecificites() throws Exception {
		// RG_PE_FP_A03
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null
				&& getListeStatut() != null && getListeStatut().size() != 0) {
			int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
					.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
			StatutFP sfp = (StatutFP) getListeStatut().get(numLigneStatut);

			ArrayList<Affectation> liste = getAffectationDao().listerAffectationAvecFPPrimaireOuSecondaire(
					getFichePosteCourante().getIdFichePoste());
			return (!getFichePosteDao().estAffectee(getFichePosteCourante().getIdFichePoste(), liste) && !sfp
					.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong()));
		} else {
			return false;
		}
	}

	/**
	 * Getter du booleen permettant de dire si la fiche de poste est affectee ou
	 * pas.
	 * 
	 * @return booleen
	 */
	public boolean estFpCouranteAffectee() {
		return fpCouranteAffectee;
	}

	/**
	 * Setter du booleen permettant de dire si la fiche de poste est affectee ou
	 * pas.
	 * 
	 * @param fpCouranteAffectee
	 *            booleen
	 */
	private void setFpCouranteAffectee(boolean fpCouranteAffectee) {
		this.fpCouranteAffectee = fpCouranteAffectee;
	}

	/**
	 * Getter de la FichePoste Remplacement.
	 * 
	 * @return FichePoste
	 */
	public FichePoste getRemplacement() {
		return remplacement;
	}

	/**
	 * Setter de la FichePoste Remplacement.
	 * 
	 * @param remp
	 */
	public void setRemplacement(FichePoste remp) throws Exception {
		this.remplacement = remp;
		if (remp != null) {
			try {
				setAgtRemplacement(getAgentDao().chercherAgentAffecteFichePoste(getRemplacement().getIdFichePoste()));
			} catch (Exception e) {
				setAgtRemplacement(null);
			}
			setTitrePosteRemplacement(getTitrePosteDao().chercherTitrePoste(getRemplacement().getIdTitrePoste()));
		} else {
			setAgtRemplacement(null);
			setTitrePosteRemplacement(null);
		}
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 * 
	 * @return le nom de l'ecran
	 */
	public String getNomEcran() {
		return "ECR-PE-FP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_REMPLACEMENT Date
	 * de création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHER_REMPLACEMENT() {
		return "NOM_PB_RECHERCHER_REMPLACEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHER_REMPLACEMENT(HttpServletRequest request) throws Exception {
		if (!getVAL_ST_ID_SERVICE_ADS().equals(Const.CHAINE_VIDE)) {
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_SERVICE,
					adsService.getEntiteByIdEntite(new Integer(getVAL_ST_ID_SERVICE_ADS())));
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE, Boolean.TRUE);

		setStatut(STATUT_REMPLACEMENT, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REMPLACEMENT Date de
	 * création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public String getNOM_ST_REMPLACEMENT() {
		return "NOM_ST_REMPLACEMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REMPLACEMENT
	 * Date de création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public String getVAL_ST_REMPLACEMENT() {
		return getZone(getNOM_ST_REMPLACEMENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_STATUT Date de
	 * création : (08/11/11 11:20:35)
	 * 
	 * 
	 */
	public String getNOM_PB_SELECT_STATUT() {
		return "NOM_PB_SELECT_STATUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/11/11 11:20:35)
	 * 
	 * 
	 */
	public boolean performPB_SELECT_STATUT(HttpServletRequest request) throws Exception {
		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
			return true;
		}

		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);
		initialiseChampObligatoire(statut);
		return true;
	}

	/**
	 * Définit si le reponsable est obligatoire en fonction du statut.
	 * 
	 * @param statut
	 */
	private void initialiseChampObligatoire(StatutFP statut) {
		responsableObligatoire = true;
		estFDPInactive = false;
		if (statut.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
			responsableObligatoire = false;
			estFDPInactive = true;
		}
	}

	private boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
		// on fait appel a CREER pour valider les modifications et afficher le
		// message "FDP imprimee"
		if (!performPB_CREER(request)) {
			return false;
		}

		if (!imprimeModele(request)) {
			return false;
		}
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean imprimeModele(HttpServletRequest request) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("FichePosteVierge");

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destinationFDP = "FichePosteVierge/FP_" + getFichePosteCourante().getIdFichePoste() + ".doc";

		try {
			byte[] fileAsBytes = sirhService.downloadFichePoste(getFichePosteCourante().getIdFichePoste());

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destinationFDP)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

			destinationFDP = destinationFDP.substring(destinationFDP.lastIndexOf("/"), destinationFDP.length());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
			setURLFichier(getScriptOuverture(repertoireStockage + "FichePosteVierge" + destinationFDP));

		} catch (Exception e) {
			// "ERR185",
			// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
			return false;
		}
		return true;
	}

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie déjà que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuilder scriptOuvPDF = new StringBuilder("<script language=\"JavaScript\" type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('").append(cheminFichier).append("');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_FP Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_FP() {
		return "NOM_ST_INFO_FP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_FP Date
	 * de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_FP() {
		return getZone(getNOM_ST_INFO_FP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_GRADE Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_GRADE() {
		return "NOM_ST_INFO_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_GRADE
	 * Date de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_GRADE() {
		return getZone(getNOM_ST_INFO_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_SERVICE Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_SERVICE() {
		return "NOM_ST_INFO_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_SERVICE
	 * Date de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_SERVICE() {
		return getZone(getNOM_ST_INFO_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_REMP Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_REMP() {
		return "NOM_ST_INFO_REMP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_REMP Date
	 * de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_REMP() {
		return getZone(getNOM_ST_INFO_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_RESP Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_RESP() {
		return "NOM_ST_INFO_RESP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_RESP Date
	 * de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_RESP() {
		return getZone(getNOM_ST_INFO_RESP());
	}

	/**
	 * Getter de la liste des activités de la FicheEmploi primaire.
	 * 
	 * @return listeActiFEP
	 */
	public ArrayList<Activite> getListeActiFEP() {
		if (listeActiFEP == null) {
			listeActiFEP = new ArrayList<Activite>();
		}
		return listeActiFEP;
	}

	/**
	 * Setter de la liste des activités de la FicheEmploi primaire.
	 * 
	 * @param listeActiFEP
	 */
	public void setListeActiFEP(ArrayList<Activite> listeActiFEP) {
		this.listeActiFEP = listeActiFEP;
	}

	/**
	 * Getter de la liste des activités de la FicheEmploi secondaire.
	 * 
	 * @return listeActiFES
	 */
	public ArrayList<Activite> getListeActiFES() {
		if (listeActiFES == null) {
			listeActiFES = new ArrayList<Activite>();
		}
		return listeActiFES;
	}

	/**
	 * Setter de la liste des activités de la FicheEmploi secondaire.
	 * 
	 * @param listeActiFES
	 */
	public void setListeActiFES(ArrayList<Activite> listeActiFES) {
		this.listeActiFES = listeActiFES;
	}

	/**
	 * Getter de la HashTable des types de nature d'avantage.
	 * 
	 * @return hashNatAv
	 */
	public HashMap<Integer, NatureAvantage> getHashNatAv() {
		if (hashNatAv == null) {
			hashNatAv = new HashMap<>();
		}
		return hashNatAv;
	}

	/**
	 * Setter de la HashTable des types de nature d'avantage.
	 * 
	 * @param hashNatAv
	 *            hashNatAv à définir
	 */
	public void setHashNatAv(HashMap<Integer, NatureAvantage> hashNatAv) {
		this.hashNatAv = hashNatAv;
	}

	/**
	 * Getter de la HashTable des types d'avantage en nature.
	 * 
	 * @return hashtypAv
	 */
	public HashMap<Integer, TypeAvantage> getHashtypAv() {
		if (hashtypAv == null) {
			hashtypAv = new HashMap<>();
		}
		return hashtypAv;
	}

	/**
	 * Setter de la HashTable des types d'avantage en nature.
	 * 
	 * @param hashtypAv
	 */
	public void setHashtypAv(HashMap<Integer, TypeAvantage> hashtypAv) {
		this.hashtypAv = hashtypAv;
	}

	/**
	 * Getter de la HashTable des types de délégation.
	 * 
	 * @return hashTypDel
	 */
	public HashMap<Integer, TypeDelegation> getHashTypDel() {
		if (hashTypDel == null) {
			hashTypDel = new HashMap<>();
		}
		return hashTypDel;
	}

	/**
	 * Setter de la HashTable des types de délégation.
	 * 
	 * @param hashTypDel
	 */
	public void setHashTypDel(HashMap<Integer, TypeDelegation> hashTypDel) {
		this.hashTypDel = hashTypDel;
	}

	/**
	 * Getter de la HashTable des types de régime indemnitaire.
	 * 
	 * @return hashTypRegIndemn
	 */
	public HashMap<Integer, TypeRegIndemn> getHashTypRegIndemn() {
		if (hashTypRegIndemn == null) {
			hashTypRegIndemn = new HashMap<>();
		}
		return hashTypRegIndemn;
	}

	/**
	 * Setter de la HashTable des types de régime indemnitaire.
	 * 
	 * @param hashTypRegIndemn
	 */
	public void setHashTypRegIndemn(HashMap<Integer, TypeRegIndemn> hashTypRegIndemn) {
		this.hashTypRegIndemn = hashTypRegIndemn;
	}

	/**
	 * Met a jour l'autorisation de modifier les FicheEmploi.
	 */
	public void majChangementFEAutorise() {
		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
			return;
		}
		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);
		setChangementFEAutorise(!estFpCouranteAffectee()
				&& !statut.getLibStatutFp().equals(EnumStatutFichePoste.INACTIVE.getLibLong())
				&& !statut.getLibStatutFp().equals(EnumStatutFichePoste.VALIDEE.getLibLong())
				&& !statut.getLibStatutFp().equals(EnumStatutFichePoste.GELEE.getLibLong()));
	}

	/**
	 * Getter du booleen changementFEAutorise.
	 * 
	 * @return changementFEAutorise
	 */
	public boolean estChangementFEAutorise() {
		return changementFEAutorise;
	}

	/**
	 * Setter du booleen changementFEAutorise.
	 * 
	 * @param changementFEAutorise
	 */
	private void setChangementFEAutorise(boolean changementFEAutorise) {
		this.changementFEAutorise = changementFEAutorise;
	}

	/**
	 * Getter de l'agent responsable.
	 * 
	 * @return agtResponsable
	 */
	private Agent getAgtResponsable() {
		return agtResponsable;
	}

	/**
	 * Setter de l'agent responsable.
	 * 
	 * @param agtResponsable
	 */
	private void setAgtResponsable(Agent agtResponsable) {
		this.agtResponsable = agtResponsable;
	}

	/**
	 * Getter de l'agent remplacement.
	 * 
	 * @return agtRemplacement
	 */
	private Agent getAgtRemplacement() {
		return agtRemplacement;
	}

	/**
	 * Setter de l'agent remplacement.
	 * 
	 * @param agtRemplacement
	 */
	private void setAgtRemplacement(Agent agtRemplacement) {
		this.agtRemplacement = agtRemplacement;
	}

	/**
	 * Getter du TitrePoste Remplacement.
	 * 
	 * @return titrePosteRemplacement
	 */
	private TitrePoste getTitrePosteRemplacement() {
		return titrePosteRemplacement;
	}

	/**
	 * Setter du TitrePoste Remplacement.
	 * 
	 * @param titrePosteRemplacement
	 */
	private void setTitrePosteRemplacement(TitrePoste titrePosteRemplacement) {
		this.titrePosteRemplacement = titrePosteRemplacement;
	}

	/**
	 * Getter du TitrePoste Responsable.
	 * 
	 * @return titrePosteResponsable
	 */
	private TitrePoste getTitrePosteResponsable() {
		return titrePosteResponsable;
	}

	/**
	 * Setter du TitrePoste Responsable.
	 * 
	 * @param titrePosteResponsable
	 */
	private void setTitrePosteResponsable(TitrePoste titrePosteResponsable) {
		this.titrePosteResponsable = titrePosteResponsable;
	}

	/**
	 * Getter de l'Affectation courante.
	 * 
	 * @return affectationCourante
	 */
	private Affectation getAffectationCourante() {
		return affectationCourante;
	}

	/**
	 * Setter de l'Affectation courante.
	 * 
	 * @param affectationCourante
	 */
	private void setAffectationCourante(Affectation affectationCourante) {
		this.affectationCourante = affectationCourante;
	}

	/**
	 * Getter de l'Agent courant.
	 * 
	 * @return agentCourant
	 */
	private Agent getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Setter de l'Agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Getter du contrat courant de l'agent affecte.
	 * 
	 * @return contratCourant
	 */
	private Contrat getContratCourant() {
		return contratCourant;
	}

	/**
	 * Setter du contrat courant de l'agent affecte.
	 * 
	 * @param contratCourant
	 */
	private void setContratCourant(Contrat contratCourant) {
		this.contratCourant = contratCourant;
	}

	/**
	 * Getter du type de contrat courant de l'agent affecte.
	 * 
	 * @return typeContratCourant
	 */
	private TypeContrat getTypeContratCourant() {
		return typeContratCourant;
	}

	/**
	 * Setter du type de contrat courant de l'agent affecte.
	 * 
	 * @param typeContratCourant
	 *            typeContratCourant à définir
	 */
	private void setTypeContratCourant(TypeContrat typeContratCourant) {
		this.typeContratCourant = typeContratCourant;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_SELECT_STATUT
			if (testerParametre(request, getNOM_PB_SELECT_STATUT())) {
				return performPB_SELECT_STATUT(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_REMPLACEMENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_REMPLACEMENT())) {
				return performPB_RECHERCHER_REMPLACEMENT(request);
			}

			// Si clic sur le bouton PB_RECHERCHE_AVANCEE
			if (testerParametre(request, getNOM_PB_RECHERCHE_AVANCEE())) {
				return performPB_RECHERCHE_AVANCEE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_FP
			if (testerParametre(request, getNOM_PB_AJOUTER_FP())) {
				return performPB_AJOUTER_FP(request);
			}

			// Si clic sur le bouton PB_DUPLIQUER_FP
			if (testerParametre(request, getNOM_PB_DUPLIQUER_FP())) {
				return performPB_DUPLIQUER_FP(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_GRADE
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_GRADE())) {
				return performPB_AFFICHER_LISTE_GRADE(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_NIVEAU
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_NIVEAU())) {
				return performPB_AFFICHER_LISTE_NIVEAU(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_RESPONSABLE
			if (testerParametre(request, getNOM_PB_RECHERCHER_RESPONSABLE())) {
				return performPB_RECHERCHER_RESPONSABLE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_SPECIFICITES
			if (testerParametre(request, getNOM_PB_MODIFIER_SPECIFICITES())) {
				return performPB_MODIFIER_SPECIFICITES(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			if (testerParametre(request, getNOM_PB_MODIFIER())) {
				return performPB_MODIFIER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_ACTIVITE
			if (testerParametre(request, getNOM_PB_AJOUTER_ACTIVITE())) {
				return performPB_AJOUTER_ACTIVITE(request);
			}

			// Si clic sur le bouton PB_RECHERCHE_EMPLOI_PRIMAIRE
			if (testerParametre(request, getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE())) {
				return performPB_RECHERCHE_EMPLOI_PRIMAIRE(request);
			}

			// Si clic sur le bouton PB_RECHERCHE_EMPLOI_SECONDAIRE
			if (testerParametre(request, getNOM_PB_RECHERCHE_EMPLOI_SECONDAIRE())) {
				return performPB_RECHERCHE_EMPLOI_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_EMPLOI_SECONDAIRE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE())) {
				return performPB_SUPPRIMER_EMPLOI_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_GRADE
			if (testerParametre(request, getNOM_PB_AJOUTER_GRADE())) {
				return performPB_AJOUTER_GRADE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_NIVEAU_ETUDE
			if (testerParametre(request, getNOM_PB_AJOUTER_NIVEAU_ETUDE())) {
				return performPB_AJOUTER_NIVEAU_ETUDE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NIVEAU_ETUDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NIVEAU_ETUDE())) {
				return performPB_SUPPRIMER_NIVEAU_ETUDE(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_IMPRIMER
			if (testerParametre(request, getNOM_PB_IMPRIMER())) {
				return performPB_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_SAVOIR
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_SAVOIR())) {
				return performPB_AJOUTER_COMPETENCE_SAVOIR(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE())) {
				return performPB_AJOUTER_COMPETENCE_SAVOIR_FAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_COMPORTEMENT
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT())) {
				return performPB_AJOUTER_COMPETENCE_COMPORTEMENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_REMPLACEMENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_REMPLACEMENT())) {
				return performPB_SUPPRIMER_REMPLACEMENT(request);
			}

			// Si clic sur le bouton PB_CONSULTER_FICHE_EMPLOI_PRIMAIRE
			if (testerParametre(request, getNOM_PB_CONSULTER_FICHE_EMPLOI_PRIMAIRE())) {
				return performPB_CONSULTER_FICHE_EMPLOI_PRIMAIRE(request);
			}

			// Si clic sur le bouton PB_CONSULTER_FICHE_EMPLOI_SECONDAIRE()
			if (testerParametre(request, getNOM_PB_CONSULTER_FICHE_EMPLOI_SECONDAIRE())) {
				return performPB_CONSULTER_FICHE_EMPLOI_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_CONSULTER_RESPONSABLE_HIERARCHIQUE()
			if (testerParametre(request, getNOM_PB_CONSULTER_RESPONSABLE_HIERARCHIQUE())) {
				return performPB_CONSULTER_RESPONSABLE_HIERARCHIQUE(request);
			}

			// Si clic sur le bouton PB_CONSULTER_REMPLACEMENT()
			if (testerParametre(request, getNOM_PB_CONSULTER_REMPLACEMENT())) {
				return performPB_CONSULTER_REMPLACEMENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_FP()
			if (testerParametre(request, getNOM_PB_SUPPRIMER_FP())) {
				return performPB_SUPPRIMER_FP(request);
			}

			// Si clic sur le bouton PB_INFO_SERVICE()
			if (testerParametre(request, getNOM_PB_INFO_SERVICE())) {
				return performPB_INFO_SERVICE(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFichePoste. Date de création : (07/12/11
	 * 10:22:27)
	 * 
	 * 
	 */
	public OePOSTEFichePoste() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (07/12/11 10:22:27)
	 * 
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFichePoste.jsp";
	}

	private String getObservation() {
		if (observation == null) {
			return Const.CHAINE_VIDE;
		}
		return observation;
	}

	private void setObservation(String observation) {
		this.observation = observation;
	}

	private String getMission() {
		return mission == null ? Const.CHAINE_VIDE : mission.trim();
	}

	private void setMission(String mission) {
		this.mission = mission;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_ACTI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_ID_ACTI(int i) {
		return "NOM_ST_ID_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_ACTI Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_ID_ACTI(int i) {
		return getZone(getNOM_ST_ID_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ACTI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_ACTI Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_ACTI Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_ACTI(int i) {
		return "NOM_CK_SELECT_LIGNE_ACTI_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_SELECT_LIGNE_ACTI Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_ACTI(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_ACTI(i));
	}

	public ArrayList<Activite> getListeToutesActi() {
		return listeToutesActi;
	}

	private void setListeToutesActi(ArrayList<Activite> listeToutesActi) {
		this.listeToutesActi = listeToutesActi;
	}

	private ArrayList<ActiviteFP> getListeActiFP() {
		return listeActiFP;
	}

	private void setListeActiFP(ArrayList<ActiviteFP> listeActiFP) {
		this.listeActiFP = listeActiFP;
	}

	private ArrayList<Activite> getListeAjoutActiFP() {
		return listeAjoutActiFP;
	}

	private HashMap<Integer, String> getHashOrigineActivite() {
		if (hashOrigineActivite == null) {
			hashOrigineActivite = new HashMap<>();
		}
		return hashOrigineActivite;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ORIGINE_ACTI
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_ORIGINE_ACTI(int i) {
		return "NOM_ST_LIB_ORIGINE_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_ORIGINE_ACTI Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_ORIGINE_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ORIGINE_ACTI(i));
	}

	private ArrayList<Competence> getListeCompFEP() {
		if (listeCompFEP == null) {
			listeCompFEP = new ArrayList<>();
		}
		return listeCompFEP;
	}

	private void setListeCompFEP(ArrayList<Competence> listeCompFEP) {
		this.listeCompFEP = listeCompFEP;
	}

	private ArrayList<Competence> getListeCompFES() {
		if (listeCompFES == null) {
			listeCompFES = new ArrayList<>();
		}
		return listeCompFES;
	}

	private void setListeCompFES(ArrayList<Competence> listeCompFES) {
		this.listeCompFES = listeCompFES;
	}

	private ArrayList<CompetenceFP> getListeCompFP() {
		if (listeCompFP == null) {
			listeCompFP = new ArrayList<>();
		}
		return listeCompFP;
	}

	private void setListeCompFP(ArrayList<CompetenceFP> listeCompFP) {
		this.listeCompFP = listeCompFP;
	}

	public ArrayList<Competence> getListeToutesComp() {
		if (listeToutesComp == null) {
			listeToutesComp = new ArrayList<>();
		}
		return listeToutesComp;
	}

	private void setListeToutesComp(ArrayList<Competence> listeToutesComp) {
		this.listeToutesComp = listeToutesComp;
	}

	private ArrayList<Competence> getListeAjoutCompFP() {
		if (listeAjoutCompFP == null) {
			listeAjoutCompFP = new ArrayList<>();
		}
		return listeAjoutCompFP;
	}

	private void setListeAjoutCompFP(ArrayList<Competence> listeAjoutCompFP) {
		this.listeAjoutCompFP = listeAjoutCompFP;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_ID_COMP(int i) {
		return "NOM_ST_ID_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_ID_COMP(int i) {
		return getZone(getNOM_ST_ID_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_COMP(int i) {
		return "NOM_ST_LIB_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_COMP(int i) {
		return getZone(getNOM_ST_LIB_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_TYPE_COMP(int i) {
		return "NOM_ST_TYPE_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_TYPE_COMP(int i) {
		return getZone(getNOM_ST_TYPE_COMP(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_COMP Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_COMP(int i) {
		return "NOM_CK_SELECT_LIGNE_COMP_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_SELECT_LIGNE_COMP Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_COMP(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ORIGINE_COMP
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_ORIGINE_COMP(int i) {
		return "NOM_ST_LIB_ORIGINE_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_ORIGINE_COMP Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_ORIGINE_COMP(int i) {
		return getZone(getNOM_ST_LIB_ORIGINE_COMP(i));
	}

	private HashMap<Integer, String> getHashOrigineCompetence() {
		if (hashOrigineCompetence == null) {
			hashOrigineCompetence = new HashMap<>();
		}
		return hashOrigineCompetence;
	}

	private ArrayList<NiveauEtudeFP> getListeNiveauFP() {
		return listeNiveauFP;
	}

	private void setListeNiveauFP(ArrayList<NiveauEtudeFP> listeNiveauFP) {
		this.listeNiveauFP = listeNiveauFP;
	}

	private ArrayList<NiveauEtude> getListeTousNiveau() {
		return listeTousNiveau;
	}

	private void setListeTousNiveau(ArrayList<NiveauEtude> listeTousNiveau) {
		this.listeTousNiveau = listeTousNiveau;
	}

	private ArrayList<NiveauEtude> getListeNiveauEtude() {
		return listeNiveauEtude;
	}

	private void setListeNiveauEtude(ArrayList<NiveauEtude> listeNiveauEtude) {
		this.listeNiveauEtude = listeNiveauEtude;
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_AV_TYPE(int i) {
		return "NOM_ST_AV_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_AV_TYPE(int i) {
		return getZone(getNOM_ST_AV_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_MNT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_AV_MNT(int i) {
		return "NOM_ST_AV_MNT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_AV_MNT(int i) {
		return getZone(getNOM_ST_AV_MNT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_NATURE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_AV_NATURE(int i) {
		return "NOM_ST_AV_NATURE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_AV_NATURE(int i) {
		return getZone(getNOM_ST_AV_NATURE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DEL_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_DEL_TYPE(int i) {
		return "NOM_ST_DEL_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_DEL_TYPE(int i) {
		return getZone(getNOM_ST_DEL_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_DEL_COMMENTAIRE Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_DEL_COMMENTAIRE(int i) {
		return "NOM_ST_DEL_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_DEL_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_DEL_COMMENTAIRE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_REG_TYPE(int i) {
		return "NOM_ST_REG_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_REG_TYPE(int i) {
		return getZone(getNOM_ST_REG_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_FORFAIT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_REG_FORFAIT(int i) {
		return "NOM_ST_REG_FORFAIT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_REG_FORFAIT(int i) {
		return getZone(getNOM_ST_REG_FORFAIT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_NB_PTS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_REG_NB_PTS(int i) {
		return "NOM_ST_REG_NB_PTS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_REG_NB_PTS(int i) {
		return getZone(getNOM_ST_REG_NB_PTS(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_PP_RUBR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_PP_RUBR(int i) {
		return "NOM_ST_PP_RUBR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_PP_RUBR(int i) {
		return getZone(getNOM_ST_PP_RUBR(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REMPLACEMENT Date
	 * de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REMPLACEMENT() {
		return "NOM_PB_SUPPRIMER_REMPLACEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_REMPLACEMENT(HttpServletRequest request) throws Exception {
		// On enleve la fiche poste remplacee selectionnée
		setRemplacement(null);
		getFichePosteCourante().setIdRemplacement(null);
		addZone(getNOM_ST_REMPLACEMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		return true;
	}

	public PrimePointageFPDao getPrimePointageFPDao() {
		return primePointageFPDao;
	}

	public void setPrimePointageFPDao(PrimePointageFPDao primePointageFPDao) {
		this.primePointageFPDao = primePointageFPDao;
	}

	private String[] getLB_NATURE_CREDIT() {
		if (LB_NATURE_CREDIT == null) {
			LB_NATURE_CREDIT = initialiseLazyLB();
		}
		return LB_NATURE_CREDIT;
	}

	private void setLB_NATURE_CREDIT(String[] newLB_NATURE_CREDIT) {
		LB_NATURE_CREDIT = newLB_NATURE_CREDIT;
	}

	public String getNOM_LB_NATURE_CREDIT() {
		return "NOM_LB_NATURE_CREDIT";
	}

	public String getNOM_LB_NATURE_CREDIT_SELECT() {
		return "NOM_LB_NATURE_CREDIT_SELECT";
	}

	public String[] getVAL_LB_NATURE_CREDIT() {
		return getLB_NATURE_CREDIT();
	}

	public String getVAL_LB_NATURE_CREDIT_SELECT() {
		return getZone(getNOM_LB_NATURE_CREDIT_SELECT());
	}

	public ArrayList<NatureCredit> getListeNatureCredit() {
		return listeNatureCredit;
	}

	public void setListeNatureCredit(ArrayList<NatureCredit> listeNatureCredit) {
		this.listeNatureCredit = listeNatureCredit;
	}

	public NatureCreditDao getNatureCreditDao() {
		return natureCreditDao;
	}

	public void setNatureCreditDao(NatureCreditDao natureCreditDao) {
		this.natureCreditDao = natureCreditDao;
	}

	public String getNOM_EF_NUM_DELIBERATION() {
		return "NOM_EF_NUM_DELIBERATION";
	}

	public String getVAL_EF_NUM_DELIBERATION() {
		return getZone(getNOM_EF_NUM_DELIBERATION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_COMPORTEMENT Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_CONSULTER_FICHE_EMPLOI_PRIMAIRE() {
		return "NOM_PB_CONSULTER_FICHE_EMPLOI_PRIMAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_CONSULTER_FICHE_EMPLOI_PRIMAIRE(HttpServletRequest request) throws Exception {

		FicheEmploi fiche = getFicheEmploiDao().chercherFicheEmploiAvecRefMairie(getVAL_ST_EMPLOI_PRIMAIRE());
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, fiche);
		setStatut(STATUT_FICHE_EMPLOI, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_COMPORTEMENT Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_CONSULTER_FICHE_EMPLOI_SECONDAIRE() {
		return "NOM_PB_CONSULTER_FICHE_EMPLOI_SECONDAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_CONSULTER_FICHE_EMPLOI_SECONDAIRE(HttpServletRequest request) throws Exception {

		FicheEmploi fiche = getFicheEmploiDao().chercherFicheEmploiAvecRefMairie(getVAL_ST_EMPLOI_PRIMAIRE());
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, fiche);
		setStatut(STATUT_FICHE_EMPLOI, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_CONSULTER_RESPONSABLE_HIERARCHIQUE Date de création : (18/07/11
	 * 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_CONSULTER_RESPONSABLE_HIERARCHIQUE() {
		return "NOM_PB_CONSULTER_RESPONSABLE_HIERARCHIQUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_CONSULTER_RESPONSABLE_HIERARCHIQUE(HttpServletRequest request) throws Exception {
		try {
			FichePoste fiche = getFichePosteDao().chercherFichePosteAvecNumeroFP(getVAL_ST_RESPONSABLE());
			if (fiche != null) {
				viderFichePoste();
				viderObjetsFichePoste();
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
				setFichePosteCourante(fiche);
			} else {
				setStatut(STATUT_RECHERCHE, true, MessageUtils.getMessage("ERR008"));
				return false;
			}

			setFichePosteCourante(fiche);
			setStatut(STATUT_RECHERCHE, true);
			return true;
		} catch (Exception e) {
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_RECHERCHE()));
			return false;
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : NOM_PB_CONSULTER_REMPLACEMENT
	 * Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_CONSULTER_REMPLACEMENT() {
		return "NOM_PB_CONSULTER_REMPLACEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_CONSULTER_REMPLACEMENT(HttpServletRequest request) throws Exception {
		try {
			FichePoste fiche = getFichePosteDao().chercherFichePosteAvecNumeroFP(getVAL_ST_REMPLACEMENT());
			if (fiche != null) {
				viderFichePoste();
				viderObjetsFichePoste();
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
				setFichePosteCourante(fiche);
			} else {
				setStatut(STATUT_RECHERCHE, true, MessageUtils.getMessage("ERR008"));
				return false;
			}

			setFichePosteCourante(fiche);
			setStatut(STATUT_RECHERCHE, true);
			return true;
		} catch (Exception e) {
			getTransaction().declarerErreur(
					MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_RECHERCHE()));
			return false;
		}
	}

	public boolean saveFileToRemoteFileSystem(byte[] fileAsBytes, String chemin, String filename) throws Exception {

		BufferedOutputStream bos = null;
		FileObject docFile = null;

		try {
			FileSystemManager fsManager = VFS.getManager();
			docFile = fsManager.resolveFile(String.format("%s", chemin + filename));
			bos = new BufferedOutputStream(docFile.getContent().getOutputStream());
			IOUtils.write(fileAsBytes, bos);
			IOUtils.closeQuietly(bos);

			if (docFile != null) {
				try {
					docFile.close();
				} catch (FileSystemException e) {
					// ignore the exception
				}
			}
		} catch (Exception e) {
			logger.error(String.format("An error occured while writing the report file to the following path  : "
					+ chemin + filename + " : " + e));
			return false;
		}
		return true;
	}

	public boolean estStatutGelee() {
		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().isEmpty() || numLigneStatut > getListeStatut().size()) {
			return false;
		}

		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);

		if (statut.getIdStatutFp().toString().equals(EnumStatutFichePoste.GELEE.getId())) {
			return true;
		}
		return false;
	}

	public NatureAvantageDao getNatureAvantageDao() {
		return natureAvantageDao;
	}

	public void setNatureAvantageDao(NatureAvantageDao natureAvantageDao) {
		this.natureAvantageDao = natureAvantageDao;
	}

	public TypeAvantageDao getTypeAvantageDao() {
		return typeAvantageDao;
	}

	public void setTypeAvantageDao(TypeAvantageDao typeAvantageDao) {
		this.typeAvantageDao = typeAvantageDao;
	}

	public TypeDelegationDao getTypeDelegationDao() {
		return typeDelegationDao;
	}

	public void setTypeDelegationDao(TypeDelegationDao typeDelegationDao) {
		this.typeDelegationDao = typeDelegationDao;
	}

	public TypeRegIndemnDao getTypeRegIndemnDao() {
		return typeRegIndemnDao;
	}

	public void setTypeRegIndemnDao(TypeRegIndemnDao typeRegIndemnDao) {
		this.typeRegIndemnDao = typeRegIndemnDao;
	}

	public AvantageNatureDao getAvantageNatureDao() {
		return avantageNatureDao;
	}

	public void setAvantageNatureDao(AvantageNatureDao avantageNatureDao) {
		this.avantageNatureDao = avantageNatureDao;
	}

	public AvantageNatureFPDao getAvantageNatureFPDao() {
		return avantageNatureFPDao;
	}

	public void setAvantageNatureFPDao(AvantageNatureFPDao avantageNatureFPDao) {
		this.avantageNatureFPDao = avantageNatureFPDao;
	}

	public DelegationDao getDelegationDao() {
		return delegationDao;
	}

	public void setDelegationDao(DelegationDao delegationDao) {
		this.delegationDao = delegationDao;
	}

	public DelegationFPDao getDelegationFPDao() {
		return delegationFPDao;
	}

	public void setDelegationFPDao(DelegationFPDao delegationFPDao) {
		this.delegationFPDao = delegationFPDao;
	}

	public RegIndemnDao getRegIndemnDao() {
		return regIndemnDao;
	}

	public void setRegIndemnDao(RegIndemnDao regIndemnDao) {
		this.regIndemnDao = regIndemnDao;
	}

	public RegIndemnFPDao getRegIndemnFPDao() {
		return regIndemnFPDao;
	}

	public void setRegIndemnFPDao(RegIndemnFPDao regIndemnFPDao) {
		this.regIndemnFPDao = regIndemnFPDao;
	}

	public RegIndemnAffDao getRegIndemnAffDao() {
		return regIndemnAffDao;
	}

	public void setRegIndemnAffDao(RegIndemnAffDao regIndemnAffDao) {
		this.regIndemnAffDao = regIndemnAffDao;
	}

	public TypeCompetenceDao getTypeCompetenceDao() {
		return typeCompetenceDao;
	}

	public void setTypeCompetenceDao(TypeCompetenceDao typeCompetenceDao) {
		this.typeCompetenceDao = typeCompetenceDao;
	}

	public TypeContratDao getTypeContratDao() {
		return typeContratDao;
	}

	public void setTypeContratDao(TypeContratDao typeContratDao) {
		this.typeContratDao = typeContratDao;
	}

	public DocumentAgentDao getLienDocumentAgentDao() {
		return lienDocumentAgentDao;
	}

	public void setLienDocumentAgentDao(DocumentAgentDao lienDocumentAgentDao) {
		this.lienDocumentAgentDao = lienDocumentAgentDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public ContratDao getContratDao() {
		return contratDao;
	}

	public void setContratDao(ContratDao contratDao) {
		this.contratDao = contratDao;
	}

	public NiveauEtudeDao getNiveauEtudeDao() {
		return niveauEtudeDao;
	}

	public void setNiveauEtudeDao(NiveauEtudeDao niveauEtudeDao) {
		this.niveauEtudeDao = niveauEtudeDao;
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

	public NiveauEtudeFPDao getNiveauEtudeFPDao() {
		return niveauEtudeFPDao;
	}

	public void setNiveauEtudeFPDao(NiveauEtudeFPDao niveauEtudeFPDao) {
		this.niveauEtudeFPDao = niveauEtudeFPDao;
	}

	public BudgetDao getBudgetDao() {
		return budgetDao;
	}

	public void setBudgetDao(BudgetDao budgetDao) {
		this.budgetDao = budgetDao;
	}

	public FEFPDao getFefpDao() {
		return fefpDao;
	}

	public void setFefpDao(FEFPDao fefpDao) {
		this.fefpDao = fefpDao;
	}

	public CompetenceDao getCompetenceDao() {
		return competenceDao;
	}

	public void setCompetenceDao(CompetenceDao competenceDao) {
		this.competenceDao = competenceDao;
	}

	public CompetenceFPDao getCompetenceFPDao() {
		return competenceFPDao;
	}

	public void setCompetenceFPDao(CompetenceFPDao competenceFPDao) {
		this.competenceFPDao = competenceFPDao;
	}

	public CompetenceFEDao getCompetenceFEDao() {
		return competenceFEDao;
	}

	public void setCompetenceFEDao(CompetenceFEDao competenceFEDao) {
		this.competenceFEDao = competenceFEDao;
	}

	public ActiviteDao getActiviteDao() {
		return activiteDao;
	}

	public void setActiviteDao(ActiviteDao activiteDao) {
		this.activiteDao = activiteDao;
	}

	public ActiviteFEDao getActiviteFEDao() {
		return activiteFEDao;
	}

	public void setActiviteFEDao(ActiviteFEDao activiteFEDao) {
		this.activiteFEDao = activiteFEDao;
	}

	public ActiviteFPDao getActiviteFPDao() {
		return activiteFPDao;
	}

	public void setActiviteFPDao(ActiviteFPDao activiteFPDao) {
		this.activiteFPDao = activiteFPDao;
	}

	public FicheEmploiDao getFicheEmploiDao() {
		return ficheEmploiDao;
	}

	public void setFicheEmploiDao(FicheEmploiDao ficheEmploiDao) {
		this.ficheEmploiDao = ficheEmploiDao;
	}

	public FichePosteDao getFichePosteDao() {
		return fichePosteDao;
	}

	public void setFichePosteDao(FichePosteDao fichePosteDao) {
		this.fichePosteDao = fichePosteDao;
	}

	public HistoFichePosteDao getHistoFichePosteDao() {
		return histoFichePosteDao;
	}

	public void setHistoFichePosteDao(HistoFichePosteDao histoFichePosteDao) {
		this.histoFichePosteDao = histoFichePosteDao;
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

	private String[] getLB_BASE_HORAIRE_POINTAGE() {
		if (LB_BASE_HORAIRE_POINTAGE == null)
			LB_BASE_HORAIRE_POINTAGE = initialiseLazyLB();
		return LB_BASE_HORAIRE_POINTAGE;
	}

	private void setLB_BASE_HORAIRE_POINTAGE(String[] newLB_BASE_HORAIRE_POINTAGE) {
		LB_BASE_HORAIRE_POINTAGE = newLB_BASE_HORAIRE_POINTAGE;
	}

	public String getNOM_LB_BASE_HORAIRE_POINTAGE() {
		return "NOM_LB_BASE_HORAIRE_POINTAGE";
	}

	public String getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_POINTAGE_SELECT";
	}

	public String[] getVAL_LB_BASE_HORAIRE_POINTAGE() {
		return getLB_BASE_HORAIRE_POINTAGE();
	}

	public String getVAL_LB_BASE_HORAIRE_POINTAGE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_POINTAGE_SELECT());
	}

	public BaseHorairePointageDao getBaseHorairePointageDao() {
		return baseHorairePointageDao;
	}

	public void setBaseHorairePointageDao(BaseHorairePointageDao baseHorairePointageDao) {
		this.baseHorairePointageDao = baseHorairePointageDao;
	}

	public ArrayList<BaseHorairePointage> getListeBaseHorairePointage() {
		return listeBaseHorairePointage;
	}

	public void setListeBaseHorairePointage(ArrayList<BaseHorairePointage> listeBaseHorairePointage) {
		this.listeBaseHorairePointage = listeBaseHorairePointage;
	}

	public BaseHorairePointage getBaseHorairePointageCourant() {
		return baseHorairePointageCourant;
	}

	public void setBaseHorairePointageCourant(BaseHorairePointage baseHorairePointageCourant) {
		this.baseHorairePointageCourant = baseHorairePointageCourant;
	}

	public ArrayList<RefTypeSaisiCongeAnnuelDto> getListeBaseHoraireAbsence() {
		return listeBaseHoraireAbsence == null ? new ArrayList<RefTypeSaisiCongeAnnuelDto>() : listeBaseHoraireAbsence;
	}

	public void setListeBaseHoraireAbsence(ArrayList<RefTypeSaisiCongeAnnuelDto> listeBaseHoraireAbsence) {
		this.listeBaseHoraireAbsence = listeBaseHoraireAbsence;
	}

	private String[] getLB_BASE_HORAIRE_ABSENCE() {
		if (LB_BASE_HORAIRE_ABSENCE == null)
			LB_BASE_HORAIRE_ABSENCE = initialiseLazyLB();
		return LB_BASE_HORAIRE_ABSENCE;
	}

	private void setLB_BASE_HORAIRE_ABSENCE(String[] newLB_BASE_HORAIRE_ABSENCE) {
		LB_BASE_HORAIRE_ABSENCE = newLB_BASE_HORAIRE_ABSENCE;
	}

	public String getNOM_LB_BASE_HORAIRE_ABSENCE() {
		return "NOM_LB_BASE_HORAIRE_ABSENCE";
	}

	public String getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT() {
		return "NOM_LB_BASE_HORAIRE_ABSENCE_SELECT";
	}

	public String[] getVAL_LB_BASE_HORAIRE_ABSENCE() {
		return getLB_BASE_HORAIRE_ABSENCE();
	}

	public String getVAL_LB_BASE_HORAIRE_ABSENCE_SELECT() {
		return getZone(getNOM_LB_BASE_HORAIRE_ABSENCE_SELECT());
	}

	public String getNOM_PB_SUPPRIMER_FP() {
		return "NOM_PB_SUPPRIMER_FP";
	}

	public boolean performPB_SUPPRIMER_FP(HttpServletRequest request) throws Exception {

		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {

			// on fait appel à SIRH-WS pour la suppression
			ReturnMessageDto srm = sirhService.deleteFDP(getFichePosteCourante().getIdFichePoste(),
					getAgentConnecte(request).getIdAgent());

			String err = Const.CHAINE_VIDE;
			String info = Const.CHAINE_VIDE;
			if (srm.getErrors().size() > 0) {
				for (String erreur : srm.getErrors()) {
					err += " " + erreur;
				}
			}
			if (srm.getInfos().size() > 0) {
				for (String erreur : srm.getInfos()) {
					info += " " + erreur;
				}
			}

			if (!err.equals(Const.CHAINE_VIDE)) {
				err += info;
				getTransaction().declarerErreur("ERREUR : " + err);
				return false;
			}
			if (!info.equals(Const.CHAINE_VIDE)) {
				getTransaction().declarerErreur(info);
			}

			viderFichePoste();
			viderObjetsFichePoste();

			setAfficherListeGrade(false);

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

			setStatut(STATUT_MEME_PROCESS);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("INF107"));
			return false;
		}
		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		UserAppli u = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		Agent agentConnecte = null;
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = radiService.getAgentCompteADByLogin(u.getUserName());
		if (user == null) {
			return null;
		}
		try {
			agentConnecte = getAgentDao().chercherAgentParMatricule(
					radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		} catch (Exception e) {
			return null;
		}

		return agentConnecte;
	}

	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreePrevisionActifTransitoireJS(
				null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	public String getNOM_PB_INFO_SERVICE() {
		return "NOM_PB_INFO_SERVICE";
	}

	public boolean performPB_INFO_SERVICE(HttpServletRequest request) throws Exception {
		// #16356
		// on recupere le service
		try {
			Integer idServiceADS = new Integer(getVAL_ST_ID_SERVICE_ADS());
			EntiteDto entite = adsService.getEntiteByIdEntite(idServiceADS);

			// on remplit les champs
			addZone(getNOM_EF_SERVICE(), entite.getSigle());
			addZone(getNOM_ST_ID_SERVICE_ADS(), String.valueOf(entite.getIdEntite()));
			String infoService = entite.getLabel().replace("\'", " ");
			addZone(getNOM_ST_INFO_SERVICE(), infoService);

			String dateDebutAppliServ = getVAL_EF_DATE_DEBUT_APPLI_SERV();
			String numDeliberation = getVAL_EF_NUM_DELIBERATION();
			if (dateDebutAppliServ.length() == 0 && entite.getDateDeliberationActif() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				addZone(getNOM_EF_DATE_DEBUT_APPLI_SERV(), sdf.format(entite.getDateDeliberationActif()));
			}
			if (numDeliberation.length() == 0 && entite.getRefDeliberationActif() != null) {
				addZone(getNOM_EF_NUM_DELIBERATION(), entite.getRefDeliberationActif());
			}
		} catch (Exception e) {

		}
		return true;
	}
}
