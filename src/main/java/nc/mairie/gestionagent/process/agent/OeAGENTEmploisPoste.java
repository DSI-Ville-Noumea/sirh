package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.poste.*;
import nc.mairie.spring.dao.metier.poste.*;
import org.springframework.context.ApplicationContext;

import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.SpecialiteDiplome;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.PrimePointageAff;
import nc.mairie.metier.specificites.PrimePointageFP;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.diplome.DiplomeAgentDao;
import nc.mairie.spring.dao.metier.parametrage.CadreEmploiDao;
import nc.mairie.spring.dao.metier.parametrage.NatureAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.SpecialiteDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TitreDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TypeAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDelegationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeRegIndemnDao;
import nc.mairie.spring.dao.metier.referentiel.TypeCompetenceDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureDao;
import nc.mairie.spring.dao.metier.specificites.DelegationDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AdsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.ISirhService;
import nc.noumea.spring.service.PtgService;

/**
 * Process OeAGENTEmploisPoste Date de création : (03/08/11 17:03:03)
 * 
 */
public class OeAGENTEmploisPoste extends BasicProcess {

	private static final long				serialVersionUID	= 1L;
	private ArrayList<AvantageNature>		listeAvantage;
	private ArrayList<Delegation>			listeDelegation;
	private ArrayList<RegimeIndemnitaire>	listeRegIndemn;
	private ArrayList<PrimePointageFP>		listePrimePointageFP;
	private ArrayList<PrimePointageAff>		listePrimePointageAff;
	private ArrayList<Competence>			listeSavoir;
	private ArrayList<Competence>			listeSavoirFaire;
	private ArrayList<Competence>			listeComportementPro;
	private ArrayList<Activite>				listeActivite;
	private List<ActiviteMetier>            listActiviteMetier;
	private List<SavoirFaire>               listSavoirFaire;
	private List<ActiviteGenerale>          listActiviteGenerale;
	private List<ConditionExercice>         listConditionExercice;
	private ArrayList<Competence>			listeCompetence;

	private Agent							agentCourant;
	private FichePoste						fichePosteCourant;
	private FichePoste						fichePosteSecondaireCourant;
	private Affectation						affectationCourant;
	private TypeCompetence					typeCompetenceCourant;
	private FichePoste						remplacement;
	private Agent							agtRemplacement;
	private TitrePoste						titrePosteRemplacement;

	private String							titrePoste;
	private EntiteDto						service;
	private EntiteDto						direction;
	private EntiteDto						section;
	private String							localisation;
	private FichePoste						responsable;
	private String							cadreEmploi;
	private String							gradeFP;
	private String							gradeAgt;
	private String							diplomeAgt;

	private FichePoste						superieurHierarchique;
	private Agent							agtResponsable;
	private TitrePoste						titrePosteResponsable;
	private FicheMetier                     metierPrimaire;
	private FicheMetier                     metierSecondaire;

	public String							ACTION_IMPRESSION	= "Impression d'un contrat.";
	private String							focus				= null;
	private String							urlFichier;

	private PrimePointageFPDao				primePointageFPDao;
	private CadreEmploiDao					cadreEmploiDao;
	private NatureAvantageDao				natureAvantageDao;
	private SpecialiteDiplomeDao			specialiteDiplomeDao;
	private TitreDiplomeDao					titreDiplomeDao;
	private TypeAvantageDao					typeAvantageDao;
	private TypeDelegationDao				typeDelegationDao;
	private TypeRegIndemnDao				typeRegIndemnDao;
	private AvantageNatureDao				avantageNatureDao;
	private DelegationDao					delegationDao;
	private RegIndemnDao					regIndemnDao;
	private TypeCompetenceDao				typeCompetenceDao;
	private DiplomeAgentDao					diplomeAgentDao;
	private TitrePosteDao					titrePosteDao;
	private StatutFPDao						statutFPDao;
	private BudgetDao						budgetDao;
	private CompetenceDao					competenceDao;
	private CompetenceFPDao					competenceFPDao;
	private ActiviteDao						activiteDao;
	private ActiviteMetierDao 				activiteMetierDao;
	private SavoirFaireDao                  savoirFaireDao;
	private ActiviteGeneraleDao             activiteGeneraleDao;
	private ConditionExerciceDao            conditionExerciceDao;
	private FMFPDao                         fmfpDao;
	private FicheMetierDao                  ficheMetierDao;
	private ActiviteFPDao					activiteFPDao;
	private FichePosteDao					fichePosteDao;
	private AffectationDao					affectationDao;
	private AgentDao						agentDao;

	private IAdsService						adsService;

	private IPtgService						ptgService;

	private ISirhService					sirhService;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		// Mise à  jour de la liste des compétences
		if (getTypeCompetenceCourant() == null || getTypeCompetenceCourant().getIdTypeCompetence() == null) {
			setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetence(1));
			addZone(getNOM_RG_TYPE_COMPETENCE(), getNOM_RB_TYPE_COMPETENCE_S());
		} else {
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_S()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(EnumTypeCompetence.SAVOIR.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_SF()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(EnumTypeCompetence.SAVOIR_FAIRE.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_C()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(EnumTypeCompetence.COMPORTEMENT.getValue()));
		}

		// Si pas d'affectation en cours
		if (getFichePosteCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			ArrayList<Affectation> affActives = getAffectationDao().listerAffectationActiveAvecAgent(getAgentCourant().getIdAgent());
			if (affActives.size() == 0) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR083"));
				return;
			} else if (affActives.size() > 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR082"));
				return;
			} else {
				setAffectationCourant((Affectation) affActives.get(0));
				setFichePosteCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePoste()));
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(getFichePosteDao().chercherFichePoste(getAffectationCourant().getIdFichePosteSecondaire()));
				}
				FMFP fmfpPrimaire = getFmfpDao().chercherFMFPAvecNumFP(getFichePosteCourant().getIdFichePoste(), true);
				// Init fiche métier primaire
				if (fmfpPrimaire != null) {
					setMetierPrimaire(getFicheMetierDao().chercherFicheMetierAvecFichePoste(fmfpPrimaire));
				}

				// Recherche du lien FicheMetier / FichePoste secondaire
				FMFP fmfpSecondaire = getFmfpDao().chercherFMFPAvecNumFP(getFichePosteCourant().getIdFichePoste(), false);

				if (fmfpSecondaire != null) {
					// Init fiche métier secondaire
					setMetierSecondaire(getFicheMetierDao().chercherFicheMetierAvecFichePoste(fmfpSecondaire));
				}
				alimenterFicheDePoste();
			}
		} else {
			alimenterFicheDePoste();
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getPrimePointageFPDao() == null) {
			setPrimePointageFPDao(new PrimePointageFPDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCadreEmploiDao() == null) {
			setCadreEmploiDao(new CadreEmploiDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getNatureAvantageDao() == null) {
			setNatureAvantageDao(new NatureAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSpecialiteDiplomeDao() == null) {
			setSpecialiteDiplomeDao(new SpecialiteDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitreDiplomeDao() == null) {
			setTitreDiplomeDao(new TitreDiplomeDao((SirhDao) context.getBean("sirhDao")));
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
		if (getDelegationDao() == null) {
			setDelegationDao(new DelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnDao() == null) {
			setRegIndemnDao(new RegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeCompetenceDao() == null) {
			setTypeCompetenceDao(new TypeCompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDiplomeAgentDao() == null) {
			setDiplomeAgentDao(new DiplomeAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getStatutFPDao() == null) {
			setStatutFPDao(new StatutFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getBudgetDao() == null) {
			setBudgetDao(new BudgetDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCompetenceDao() == null) {
			setCompetenceDao(new CompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getCompetenceFPDao() == null) {
			setCompetenceFPDao(new CompetenceFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteDao() == null) {
			setActiviteDao(new ActiviteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteMetierDao() == null) {
			setActiviteMetierDao(new ActiviteMetierDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSavoirFaireDao() == null) {
			setSavoirFaireDao(new SavoirFaireDao((SirhDao) context.getBean("sirhDao")));
		}
        if (getActiviteGeneraleDao() == null) {
		    setActiviteGeneraleDao(new ActiviteGeneraleDao((SirhDao) context.getBean("sirhDao")));
        }
		if (getConditionExerciceDao() == null) {
			setConditionExerciceDao(new ConditionExerciceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFmfpDao() == null) {
			setFmfpDao(new FMFPDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getFicheMetierDao() == null) {
			setFicheMetierDao(new FicheMetierDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getActiviteFPDao() == null) {
			setActiviteFPDao(new ActiviteFPDao((SirhDao) context.getBean("sirhDao")));
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
		if (null == ptgService) {
			ptgService = (PtgService) context.getBean("ptgService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
	}

	private void alimenterFicheDePoste() throws Exception {
		if (getFichePosteCourant() != null) {
			// Recherche des informations à  afficher
			setTitrePoste(getFichePosteCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE
					: getTitrePosteDao().chercherTitrePoste(getFichePosteCourant().getIdTitrePoste()).getLibTitrePoste());

			setDirection(adsService.getAffichageDirection(getFichePosteCourant().getIdServiceAds()));
			EntiteDto division = adsService.getAffichageService(getFichePosteCourant().getIdServiceAds());
			setService(division == null ? adsService.getEntiteByIdEntite(getFichePosteCourant().getIdServiceAds()) : division);
			setSection(adsService.getAffichageSection(getFichePosteCourant().getIdServiceAds()));

			setLocalisation(getFichePosteCourant().getIdEntiteGeo() == null ? Const.CHAINE_VIDE
					: EntiteGeo.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo().toString()).getLibEntiteGeo());

			String gradeAffichage = Const.CHAINE_VIDE;
			if (getFichePosteCourant().getCodeGrade() != null) {
				Grade g = Grade.chercherGrade(getTransaction(), getFichePosteCourant().getCodeGrade());
				gradeAffichage = g.getGrade();

				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
				CadreEmploi cadreEmp = null;
				if (gg != null && gg.getIdCadreEmploi() != null) {
					cadreEmp = getCadreEmploiDao().chercherCadreEmploi(Integer.valueOf(gg.getIdCadreEmploi()));
				}
				setCadreEmploi(cadreEmp == null || cadreEmp.getIdCadreEmploi() == null ? Const.CHAINE_VIDE : cadreEmp.getLibCadreEmploi());
			}

			setGradeFP(gradeAffichage);

			// Diplome Agent
			try {
				DiplomeAgent dipl = getDiplomeAgentDao().chercherDernierDiplomeAgentAvecAgent(getAgentCourant().getIdAgent());
				TitreDiplome t = getTitreDiplomeDao().chercherTitreDiplome(dipl.getIdTitreDiplome());
				SpecialiteDiplome s = getSpecialiteDiplomeDao().chercherSpecialiteDiplome(dipl.getIdSpecialiteDiplome());
				setDiplomeAgt(t.getLibTitreDiplome() + " - " + s.getLibSpecialiteDiplome());
			} catch (Exception e) {
				// aucun diplome
			}

			// Carriere
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), getAgentCourant());
			if (carr != null) {
				Grade grade = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					setGradeAgt(Const.CHAINE_VIDE);
				} else {
					setGradeAgt(grade.getLibGrade());
				}
			}

			// Responsable hierarchique
			if (getFichePosteCourant() != null && getFichePosteCourant().getIdResponsable() != null) {
				setSuperieurHierarchique(getFichePosteDao().chercherFichePoste(getFichePosteCourant().getIdResponsable()));
				afficheSuperieurHierarchique();
			}

			// Affiche les zones de la page
			alimenterZones();
			if (versionFicheMetier()) {
				initialiserActiviteMetier();
				initialiseSavoirFaireGeneraux();
				initialiserActiviteGenerale();
				initialiserConditionExercice();
			} else {
				// Affiche les activités
				initialiserActivite();
				// Affiche les compétences
				initialiserCompetence();
			}
			// Affiche les spécificités de la fiche de poste
			initialiserSpecificites();

			// affichage du responsable hierarchique et le remplace
			if (getFichePosteCourant() != null && getFichePosteCourant().getIdResponsable() != null) {
				setResponsable(getFichePosteDao().chercherFichePoste(getFichePosteCourant().getIdResponsable()));
			} else {
				setResponsable(null);
			}
			afficheResponsable();

			if (getFichePosteCourant() != null && getFichePosteCourant().getIdRemplacement() != null) {
				setRemplacement(getFichePosteDao().chercherFichePoste(getFichePosteCourant().getIdRemplacement()));
			} else {
				setRemplacement(null);
			}
			afficheRemplacement();
		}
	}

	/**
	 * Récupere les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void initialiserSpecificites() throws Exception {
		setListeAvantage(null);
		setListeDelegation(null);
		setListeRegIndemn(null);
		setListePrimePointageFP(null);
		setListePrimePointageAff(null);

		// Avantages en nature
		if (getListeAvantage().size() == 0) {
			setListeAvantage(getAvantageNatureDao().listerAvantageNatureAvecFP(getFichePosteCourant().getIdFichePoste()));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceAvantage = 0;
		if (getListeAvantage() != null) {
			for (ListIterator<AvantageNature> list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = getNatureAvantageDao().chercherNatureAvantage(aAvNat.getIdNatureAvantage());

					addZone(getNOM_ST_AV_TYPE(indiceAvantage),
							typAv.getLibTypeAvantage().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typAv.getLibTypeAvantage());
					addZone(getNOM_ST_AV_MNT(indiceAvantage), aAvNat.getMontant().toString());
					addZone(getNOM_ST_AV_NATURE(indiceAvantage), natAv == null ? "&nbsp;" : natAv.getLibNatureAvantage());
				}
				indiceAvantage++;
			}
		}

		// délégations
		setListeDelegation((ArrayList<Delegation>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation().size() == 0) {
			setListeDelegation(getDelegationDao().listerDelegationAvecFP(getFichePosteCourant().getIdFichePoste()));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceDelegation = 0;
		if (getListeDelegation() != null) {
			for (ListIterator<Delegation> list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());

					addZone(getNOM_ST_DEL_TYPE(indiceDelegation),
							typDel.getLibTypeDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typDel.getLibTypeDelegation());
					addZone(getNOM_ST_DEL_COMMENTAIRE(indiceDelegation),
							aDel.getLibDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aDel.getLibDelegation());
				}
				indiceDelegation++;
			}
		}

		// Régimes indemnitaires
		if (getListeRegIndemn().size() == 0) {
			setListeRegIndemn(getRegIndemnDao().listerRegimeIndemnitaireAvecFP(getFichePosteCourant().getIdFichePoste()));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceRegime = 0;
		if (getListeRegIndemn() != null) {
			for (ListIterator<RegimeIndemnitaire> list = getListeRegIndemn().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());

					addZone(getNOM_ST_REG_TYPE(indiceRegime),
							typReg.getLibTypeRegIndemn().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_REG_FORFAIT(indiceRegime), aReg.getForfait().toString());
					addZone(getNOM_ST_REG_NB_PTS(indiceRegime), aReg.getNombrePoints().toString());
				}
				indiceRegime++;
			}
		}

		// Prime pointage
		ArrayList<RefPrimeDto> listeTotale = new ArrayList<RefPrimeDto>();
		if (getListePrimePointageFP().size() == 0) {
			setListePrimePointageFP(getPrimePointageFPDao().listerPrimePointageFP(getFichePosteCourant().getIdFichePoste()));
			for (PrimePointageFP primeFP : getListePrimePointageFP()) {
				RefPrimeDto rubr = ptgService.getPrimeDetail(primeFP.getNumRubrique());
				listeTotale.add(rubr);
			}
		}
		int indicePrime = 0;
		for (RefPrimeDto list : listeTotale) {
			if (list != null) {
				addZone(getNOM_ST_PP_RUBR(indicePrime), list.getNumRubrique() + " - " + list.getLibelle());
				indicePrime++;
			}
		}

	}

	/**
	 * Récupere les compétences des activités principales et supplementaires
	 * choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiserCompetence() throws Exception {
		TypeCompetence savoir = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(EnumTypeCompetence.SAVOIR.getValue());
		TypeCompetence savoirFaire = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(EnumTypeCompetence.SAVOIR_FAIRE.getValue());
		TypeCompetence comportement = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(EnumTypeCompetence.COMPORTEMENT.getValue());

		// Compétences
		// Recherche de tous les liens FichePoste / Competence
		ArrayList<CompetenceFP> liens = getCompetenceFPDao().listerCompetenceFPAvecFP(getFichePosteCourant().getIdFichePoste());
		ArrayList<Competence> comp = getCompetenceDao().listerCompetenceAvecFP(liens);
		if (comp != null) {
			setListeCompetence(comp);
		} else {
			setListeCompetence(null);
		}

		getListeSavoir().clear();
		getListeSavoirFaire().clear();
		getListeComportementPro().clear();

		for (int j = 0; j < getListeCompetence().size(); j++) {
			Competence c = (Competence) getListeCompetence().get(j);
			if (savoir.getIdTypeCompetence().toString().equals(c.getIdTypeCompetence().toString())) {
				if (!getListeSavoir().contains(c))
					getListeSavoir().add(c);
			} else if (savoirFaire.getIdTypeCompetence().toString().equals(c.getIdTypeCompetence().toString())) {
				if (!getListeSavoirFaire().contains(c))
					getListeSavoirFaire().add(c);
			} else if (comportement.getIdTypeCompetence().toString().equals(c.getIdTypeCompetence().toString())) {
				if (!getListeComportementPro().contains(c))
					getListeComportementPro().add(c);
			}
		}

		int indiceCompS = 0;
		if (getListeSavoir() != null) {
			for (int i = 0; i < getListeSavoir().size(); i++) {
				Competence co = (Competence) getListeSavoir().get(i);
				addZone(getNOM_ST_LIB_COMP_S(indiceCompS), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
				indiceCompS++;
			}
		}

		int indiceCompSF = 0;
		if (getListeSavoirFaire() != null) {
			for (int i = 0; i < getListeSavoirFaire().size(); i++) {
				Competence co = (Competence) getListeSavoirFaire().get(i);
				addZone(getNOM_ST_LIB_COMP_SF(indiceCompSF), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
				indiceCompSF++;
			}
		}

		int indiceCompPro = 0;
		if (getListeComportementPro() != null) {
			for (int i = 0; i < getListeComportementPro().size(); i++) {
				Competence co = (Competence) getListeComportementPro().get(i);
				addZone(getNOM_ST_LIB_COMP_PRO(indiceCompPro), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
				indiceCompPro++;
			}
		}
	}

	/**
	 * Récupere les compétences des activités principales et supplementaires
	 * choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiserActivite() throws Exception {
		// Activités
		// Recherche de tous les liens FicheEmploi / Activite
		ArrayList<ActiviteFP> liens = getActiviteFPDao().listerActiviteFPAvecFP(getFichePosteCourant().getIdFichePoste());
		setListeActivite(getActiviteDao().listerToutesActiviteAvecFP(liens));
		int indiceActi = 0;
		if (getListeActivite() != null) {
			for (int i = 0; i < getListeActivite().size(); i++) {
				Activite acti = (Activite) getListeActivite().get(i);
				addZone(getNOM_ST_LIB_ACTI(indiceActi), acti.getNomActivite().equals(Const.CHAINE_VIDE) ? "&nbsp;" : acti.getNomActivite());
				indiceActi++;
			}
		}
	}

	private void initialiserActiviteMetier() throws Exception {
		if (getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListActiviteMetier(getActiviteMetierDao().listerToutesActiviteMetierChecked(getFichePosteCourant()));
		} else {
			setListActiviteMetier(getActiviteMetierDao().listerToutesActiviteMetier(
					getMetierPrimaire() != null ? getMetierPrimaire().getIdFicheMetier() : null,
					getMetierSecondaire() != null ? getMetierSecondaire().getIdFicheMetier() : null));
		}
		for (int i = 0; i < listActiviteMetier.size(); i++) {
			ActiviteMetier am = listActiviteMetier.get(i);
			addZone(getNOM_ST_ID_ACTI_METIER(i), am.getIdActiviteMetier().toString());
			addZone(getNOM_ST_LIB_ACTI_METIER(i), am.getNomActiviteMetier());
			addZone(getNOM_CK_SELECT_LIGNE_ACTI_METIER(i), am.isChecked() ? getCHECKED_ON() : getCHECKED_OFF());
			for (int j = 0; j < am.getListSavoirFaire().size(); j++) {
				SavoirFaire sf = am.getListSavoirFaire().get(j);
				addZone(getNOM_ST_ID_ACTI_METIER_SAVOIR(i, j), sf.getIdSavoirFaire().toString());
				addZone(getNOM_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR(i, j), sf.getChecked() ? getCHECKED_ON() : getCHECKED_OFF());
				addZone(getNOM_ST_LIB_ACTI_METIER_SAVOIR(i, j), sf.getNomSavoirFaire());
			}
		}
	}

	private void initialiseSavoirFaireGeneraux() {
		setListSavoirFaire(getSavoirFaireDao().listerTousSavoirFaireGenerauxChecked(getFichePosteCourant()));
		for (int i = 0; i < listSavoirFaire.size(); i++) {
			SavoirFaire sf = listSavoirFaire.get(i);
			addZone(getNOM_ST_ID_SF(i), sf.getIdSavoirFaire().toString());
			addZone(getNOM_CK_SELECT_LIGNE_SF(i), sf.getChecked()? getCHECKED_ON(): getCHECKED_OFF());
			addZone(getNOM_ST_LIB_SF(i), sf.getNomSavoirFaire());

		}
	}

    private void initialiserActiviteGenerale() {
        setListActiviteGenerale(getActiviteGeneraleDao().listerToutesActiviteGeneraleChecked(getFichePosteCourant()));
        for (int i = 0; i < listActiviteGenerale.size(); i++) {
            ActiviteGenerale ag = listActiviteGenerale.get(i);
            addZone(getNOM_ST_ID_AG(i), ag.getIdActiviteGenerale().toString());
            addZone(getNOM_CK_SELECT_LIGNE_AG(i), ag.getChecked()? getCHECKED_ON(): getCHECKED_OFF());
            addZone(getNOM_ST_LIB_AG(i), ag.getNomActiviteGenerale());
        }
    }

	private void initialiserConditionExercice() {
		setListConditionExercice(getConditionExerciceDao().listerToutesConditionExerciceChecked(getFichePosteCourant()));
		for (int i = 0; i < listConditionExercice.size(); i++) {
			ConditionExercice ce = listConditionExercice.get(i);
			addZone(getNOM_ST_ID_CE(i), ce.getIdConditionExercice().toString());
			addZone(getNOM_CK_SELECT_LIGNE_CE(i), ce.getChecked()? getCHECKED_ON(): getCHECKED_OFF());
			addZone(getNOM_ST_LIB_CE(i), ce.getNomConditionExercice());
		}
	}

	public void alimenterZones() throws Exception {
		addZone(getNOM_ST_BUDGET(), getFichePosteCourant().getIdBudget() == null ? Const.CHAINE_VIDE
				: getBudgetDao().chercherBudget(getFichePosteCourant().getIdBudget()).getLibBudget());
		addZone(getNOM_ST_ANNEE(), getFichePosteCourant().getAnneeCreation().toString());
		addZone(getNOM_ST_NUMERO(), getFichePosteCourant().getNumFp());
		addZone(getNOM_ST_NFA(), getFichePosteCourant().getNfa());
		addZone(getNOM_ST_OPI(), getFichePosteCourant().getOpi());
		addZone(getNOM_ST_REGLEMENTAIRE(), Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg().toString()).getLibHor());
		addZone(getNOM_ST_POURCENT_BUDGETE(),
				Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorBud().toString()).getLibHor());
		addZone(getNOM_ST_ACT_INACT(), getFichePosteCourant().getIdStatutFp() == null ? Const.CHAINE_VIDE
				: getStatutFPDao().chercherStatutFP(getFichePosteCourant().getIdStatutFp()).getLibStatutFp());

		addZone(getNOM_ST_TITRE(), getTitrePoste());
		addZone(getNOM_ST_DIRECTION(), getDirection() == null ? Const.CHAINE_VIDE : getDirection().getLabel());
		addZone(getNOM_ST_SERVICE(), getService() == null ? Const.CHAINE_VIDE : getService().getLabel());
		addZone(getNOM_ST_SECTION(), getSection() == null ? Const.CHAINE_VIDE : getSection().getLabel());

		addZone(getNOM_ST_LOCALISATION(), getLocalisation());
		addZone(getNOM_ST_GRADE(), getGradeFP());
		addZone(getNOM_ST_CADRE_EMPLOI(), getCadreEmploi());
		addZone(getNOM_ST_MISSION(), getFichePosteCourant().getMissions());

		addZone(getNOM_ST_TITULAIRE(), getAgentCourant().getNomAgent() + " " + getAgentCourant().getPrenomAgent());

		addZone(getNOM_ST_GRADE_AGT(), getGradeAgt());
		addZone(getNOM_ST_ETUDE_AGT(), getDiplomeAgt());
		addZone(getNOM_ST_TPS_TRAVAIL_AGT(), getAffectationCourant().getTempsTravail() + " %");
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si clic sur le bouton PB_IMPRIMER
		if (testerParametre(request, getNOM_PB_IMPRIMER())) {
			return performPB_IMPRIMER(request);
		}
		// Si clic sur le bouton PB_ANNULER
		if (testerParametre(request, getNOM_PB_ANNULER())) {
			return performPB_ANNULER(request);
		}
		// Si clic sur le bouton PB_VALIDER_IMPRIMER
		if (testerParametre(request, getNOM_PB_VALIDER_IMPRIMER())) {
			return performPB_VALIDER_IMPRIMER(request);
		}
		// Si clic sur le bouton PB_VOIR_AUTRE_FP
		if (testerParametre(request, getNOM_PB_VOIR_AUTRE_FP())) {
			return performPB_VOIR_AUTRE_FP(request);
		}
		// Si clic sur le bouton PB_CHANGER_TYPE
		if (testerParametre(request, getNOM_PB_CHANGER_TYPE())) {
			return performPB_CHANGER_TYPE(request);
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		}
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public boolean performPB_VALIDER_IMPRIMER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}
		imprimeModele(request);

		return true;
	}

	public boolean performPB_VOIR_AUTRE_FP(HttpServletRequest request) throws Exception {
		FichePoste origine = getFichePosteCourant();
		setFichePosteCourant(getFichePosteSecondaireCourant());
		setFichePosteSecondaireCourant(origine);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Affiche la fiche de poste "Responsable"
	 */
	private void afficheSuperieurHierarchique() {
		if (getFichePosteCourant().getIdResponsable() != null) {
			if (getAgtResponsable() != null) {
				addZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE(), getAgtResponsable().getNomAgent() + " " + getAgtResponsable().getPrenomAgent() + " ("
						+ getAgtResponsable().getNomatr() + ") - " + getTitrePosteResponsable().getLibTitrePoste());
			} else {
				addZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE(),
						"Cette fiche de poste (" + getTitrePosteResponsable().getLibTitrePoste() + ") n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Constructeur du process OeAGENTEmploisPoste. Date de création : (03/08/11
	 * 17:03:03)
	 * 
	 */
	public OeAGENTEmploisPoste() {
		super();
	}

	public String getVAL_CK_SELECT_LIGNE_ACTI_METIER(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_ACTI_METIER(i));
	}

	public String getVAL_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR(int i, int j) {
		return getZone(getNOM_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR(i, j));
	}

	public String getNOM_CK_SELECT_LIGNE_ACTI(int i) {
		return "NOM_CK_SELECT_LIGNE_ACTI_" + i;
	}

	public String getVAL_CK_SELECT_LIGNE_ACTI(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_ACTI(i));
	}

	public String getNOM_CK_SELECT_LIGNE_SF(int i) {
		return "NOM_CK_SELECT_LIGNE_SF_" + i;
	}

	public String getVAL_CK_SELECT_LIGNE_SF(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_SF(i));
	}

	public String getNOM_CK_SELECT_LIGNE_AG(int i) {
		return "NOM_CK_SELECT_LIGNE_AG_" + i;
	}

	public String getVAL_CK_SELECT_LIGNE_AG(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_AG(i));
	}

	public String getNOM_CK_SELECT_LIGNE_CE(int i) {
		return "NOM_CK_SELECT_LIGNE_CE_" + i;
	}

	public String getVAL_CK_SELECT_LIGNE_CE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_CE(i));
	}

	public String getNOM_ST_ID_CE(int i) {
		return "NOM_ST_ID_CE_" + i;
	}

	public String getVAL_ST_ID_CE(int i) {
		return getZone(getNOM_ST_ID_CE(i));
	}

	public String getNOM_ST_LIB_CE(int i) {
		return "NOM_ST_LIB_CE_" + i;
	}

	public String getVAL_ST_LIB_CE(int i) {
		return getZone(getNOM_ST_LIB_CE(i));
	}

    public String getNOM_ST_ID_AG(int i) {
        return "NOM_ST_ID_AG_" + i;
    }

    public String getVAL_ST_ID_AG(int i) {
        return getZone(getNOM_ST_ID_AG(i));
    }

    public String getNOM_ST_LIB_AG(int i) {
        return "NOM_ST_LIB_AG_" + i;
    }

    public String getVAL_ST_LIB_AG(int i) {
        return getZone(getNOM_ST_LIB_AG(i));
    }

	public String getNOM_ST_ID_SF(int i) {
		return "NOM_ST_ID_SF_" + i;
	}

	public String getVAL_ST_ID_SF(int i) {
		return getZone(getNOM_ST_ID_SF(i));
	}

	public String getNOM_ST_LIB_SF(int i) {
		return "NOM_ST_LIB_SF_" + i;
	}

	public String getVAL_ST_LIB_SF(int i) {
		return getZone(getNOM_ST_LIB_SF(i));
	}

	public String getNOM_CK_SELECT_LIGNE_ACTI_METIER(int i) {
		return "NOM_CK_SELECT_LIGNE_ACTI_METIER_" + i;
	}

	public String getNOM_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR(int i, int j) {
		return "NOM_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR_" + i + "_" + j;
	}

	public String getNOM_ST_LIB_ACTI_METIER_SAVOIR(int i, int j) {
		return "NOM_ST_LIB_ACTI_METIER_SAVOIR_" + i + "_" + j;
	}

	public String getVAL_ST_LIB_ACTI_METIER_SAVOIR(int i, int j) {
		return getZone(getNOM_ST_LIB_ACTI_METIER_SAVOIR(i, j));
	}

	public String getNOM_ST_LIB_ACTI_METIER(int i) {
		return "NOM_ST_LIB_ACTI_METIER_" + i;
	}

	public String getVAL_ST_LIB_ACTI_METIER(int i) {
		return getZone(getNOM_ST_LIB_ACTI_METIER(i));
	}

	public String getNOM_ST_ID_ACTI_METIER(int i) {
		return "NOM_ST_ID_ACTI_METIER_" + i;
	}

	public String getVAL_ST_ID_ACTI_METIER(int i) {
		return getZone(getNOM_ST_ID_ACTI_METIER(i));
	}

	public String getNOM_ST_ID_ACTI_METIER_SAVOIR(int i, int j) {
		return "NOM_ST_ID_ACTI_METIER_SAVOIR_" + i + "_" + j;
	}

	public String getVAL_ST_ID_ACTI_METIER_SAVOIR(int i, int j) {
		return getZone(getNOM_ST_ID_ACTI_METIER_SAVOIR(i, j));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACT_INACT Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ACT_INACT() {
		return "NOM_ST_ACT_INACT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACT_INACT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ACT_INACT() {
		return getZone(getNOM_ST_ACT_INACT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ANNEE() {
		return "NOM_ST_ANNEE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ANNEE() {
		return getZone(getNOM_ST_ANNEE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BUDGET Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_BUDGET() {
		return "NOM_ST_BUDGET";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_BUDGET Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_BUDGET() {
		return getZone(getNOM_ST_BUDGET());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI() {
		return "NOM_ST_CADRE_EMPLOI";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CADRE_EMPLOI
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI() {
		return getZone(getNOM_ST_CADRE_EMPLOI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_DIRECTION() {
		return "NOM_ST_DIRECTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_DIRECTION() {
		return getZone(getNOM_ST_DIRECTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETUDE_AGT Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ETUDE_AGT() {
		return "NOM_ST_ETUDE_AGT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ETUDE_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ETUDE_AGT() {
		return getZone(getNOM_ST_ETUDE_AGT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_GRADE() {
		return "NOM_ST_GRADE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_GRADE() {
		return getZone(getNOM_ST_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_AGT Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_GRADE_AGT() {
		return "NOM_ST_GRADE_AGT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_GRADE_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_GRADE_AGT() {
		return getZone(getNOM_ST_GRADE_AGT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LOCALISATION Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_LOCALISATION() {
		return "NOM_ST_LOCALISATION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LOCALISATION
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_LOCALISATION() {
		return getZone(getNOM_ST_LOCALISATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MISSION Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_MISSION() {
		return "NOM_ST_MISSION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_MISSION Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_MISSION() {
		return getZone(getNOM_ST_MISSION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NFA Date de création
	 * : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_NFA() {
		return "NOM_ST_NFA";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NFA Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_NFA() {
		return getZone(getNOM_ST_NFA());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUMERO Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_NUMERO() {
		return "NOM_ST_NUMERO";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_NUMERO Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_NUMERO() {
		return getZone(getNOM_ST_NUMERO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_OPI Date de création
	 * : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_OPI() {
		return "NOM_ST_OPI";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_OPI Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_OPI() {
		return getZone(getNOM_ST_OPI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POURCENT_BUDGETE
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_POURCENT_BUDGETE() {
		return "NOM_ST_POURCENT_BUDGETE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_POURCENT_BUDGETE Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_POURCENT_BUDGETE() {
		return getZone(getNOM_ST_POURCENT_BUDGETE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REGLEMENTAIRE Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_REGLEMENTAIRE() {
		return "NOM_ST_REGLEMENTAIRE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_REGLEMENTAIRE
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_REGLEMENTAIRE() {
		return getZone(getNOM_ST_REGLEMENTAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RESPONSABLE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_RESPONSABLE() {
		return "NOM_ST_RESPONSABLE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_RESPONSABLE
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_RESPONSABLE() {
		return getZone(getNOM_ST_RESPONSABLE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_TITRE() {
		return "NOM_ST_TITRE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_TITRE() {
		return getZone(getNOM_ST_TITRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITULAIRE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_TITULAIRE() {
		return "NOM_ST_TITULAIRE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_TITULAIRE Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_TITULAIRE() {
		return getZone(getNOM_ST_TITULAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_TRAVAIL_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_TPS_TRAVAIL_AGT() {
		return "NOM_ST_TPS_TRAVAIL_AGT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_TPS_TRAVAIL_AGT Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_TPS_TRAVAIL_AGT() {
		return getZone(getNOM_ST_TPS_TRAVAIL_AGT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_TRAVAIL_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_SUPERIEUR_HIERARCHIQUE() {
		return "NOM_ST_SUPERIEUR_HIERARCHIQUE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_TPS_TRAVAIL_AGT Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_SUPERIEUR_HIERARCHIQUE() {
		return getZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE());
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public Agent getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met a jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return fichePosteCourant
	 */
	public FichePoste getFichePosteCourant() {
		return fichePosteCourant;
	}

	/**
	 * Met a jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteCourant(FichePoste fichePosteCourant) {
		this.fichePosteCourant = fichePosteCourant;
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return fichePosteSecondaireCourant
	 */
	public FichePoste getFichePosteSecondaireCourant() {
		return fichePosteSecondaireCourant;
	}

	/**
	 * Met a jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteSecondaireCourant(FichePoste fichePosteSecondaireCourant) {
		this.fichePosteSecondaireCourant = fichePosteSecondaireCourant;
	}

	public List<ActiviteMetier> getListActiviteMetier() {
		return listActiviteMetier;
	}

	public void setListActiviteMetier(List<ActiviteMetier> listActiviteMetier) {
		this.listActiviteMetier = listActiviteMetier;
	}

	public List<SavoirFaire> getListSavoirFaire() {
		return listSavoirFaire;
	}

	public void setListSavoirFaire(List<SavoirFaire> listSavoirFaire) {
		this.listSavoirFaire = listSavoirFaire;
	}

    public List<ActiviteGenerale> getListActiviteGenerale() {
        return listActiviteGenerale;
    }

    public void setListActiviteGenerale(List<ActiviteGenerale> listActiviteGenerale) {
        this.listActiviteGenerale = listActiviteGenerale;
    }

	public List<ConditionExercice> getListConditionExercice() {
		return listConditionExercice;
	}

	public void setListConditionExercice(List<ConditionExercice> listConditionExercice) {
		this.listConditionExercice = listConditionExercice;
	}

	/**
	 * Retourne la liste des activités.
	 * 
	 * @return listeActivite
	 */
	public ArrayList<Activite> getListeActivite() {
		if (listeActivite == null)
			listeActivite = new ArrayList<Activite>();
		return listeActivite;
	}

	/**
	 * Retourne la liste des activités principales sous forme d'une chaine de
	 * caracteres.
	 * 
	 * @return listeActivitePrinc
	 */
	/*
	 * private String getListeActiviteIntoString() throws Exception { String
	 * result = Const.CHAINE_VIDE; for (Activite actiPrinc : getListeActivite())
	 * { if (result.length() == 0) result = actiPrinc.getNomActivite(); else
	 * result = result.concat(", " + actiPrinc.getNomActivite()); } return
	 * result; }
	 */

	/**
	 * Met a jour la liste des activités principales.
	 * 
	 * @param listeActivitePrinc
	 *            Nouvelle liste des activités
	 */
	private void setListeActivite(ArrayList<Activite> listeActivite) {
		this.listeActivite = listeActivite;
	}

	/**
	 * Retourne la liste des comportements professionels.
	 * 
	 * @return listeComportementPro
	 */
	public ArrayList<Competence> getListeComportementPro() {
		if (listeComportementPro == null)
			listeComportementPro = new ArrayList<Competence>();
		return listeComportementPro;
	}

	/**
	 * Retourne la liste des Savoir.
	 * 
	 * @return listeSavoir
	 */
	public ArrayList<Competence> getListeSavoir() {
		if (listeSavoir == null)
			listeSavoir = new ArrayList<Competence>();
		return listeSavoir;
	}

	/**
	 * Retourne la liste des Savoir-Faire.
	 * 
	 * @return listeSavoirFaire
	 */
	public ArrayList<Competence> getListeSavoirFaire() {
		if (listeSavoirFaire == null)
			listeSavoirFaire = new ArrayList<Competence>();
		return listeSavoirFaire;
	}

	/**
	 * Retourne la liste des AvantageNature.
	 * 
	 * @return listeAvantage
	 */
	public ArrayList<AvantageNature> getListeAvantage() {
		if (listeAvantage == null)
			listeAvantage = new ArrayList<AvantageNature>();
		return listeAvantage;
	}

	/**
	 * Retourne la liste des Delegation.
	 * 
	 * @return listeDelegation
	 */
	public ArrayList<Delegation> getListeDelegation() {
		if (listeDelegation == null)
			listeDelegation = new ArrayList<Delegation>();
		return listeDelegation;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeRegIndemn
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegIndemn() {
		if (listeRegIndemn == null)
			listeRegIndemn = new ArrayList<RegimeIndemnitaire>();
		return listeRegIndemn;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListeRegIndemn
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListeRegIndemn(ArrayList<RegimeIndemnitaire> newListeRegIndemn) {
		this.listeRegIndemn = newListeRegIndemn;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listePrimePointage
	 */
	public ArrayList<PrimePointageFP> getListePrimePointageFP() {
		if (listePrimePointageFP == null)
			listePrimePointageFP = new ArrayList<PrimePointageFP>();
		return listePrimePointageFP;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListePrimePointage
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListePrimePointageFP(ArrayList<PrimePointageFP> newListePrimePointage) {
		this.listePrimePointageFP = newListePrimePointage;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listePrimePointage
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAff() {
		if (listePrimePointageAff == null)
			listePrimePointageAff = new ArrayList<PrimePointageAff>();
		return listePrimePointageAff;
	}

	/**
	 * Met a jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListePrimePointage
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListePrimePointageAff(ArrayList<PrimePointageAff> newListePrimePointage) {
		this.listePrimePointageAff = newListePrimePointage;
	}

	/**
	 * Met a jour la liste des Delegation.
	 * 
	 * @param newListeDelegation
	 *            Nouvelle liste des Delegation
	 */
	private void setListeDelegation(ArrayList<Delegation> newListeDelegation) {
		this.listeDelegation = newListeDelegation;
	}

	/**
	 * Met a jour la liste des AvantageNature.
	 * 
	 * @param newListeAvantage
	 *            Nouvelle liste des AvantageNature
	 */
	private void setListeAvantage(ArrayList<AvantageNature> newListeAvantage) {
		this.listeAvantage = newListeAvantage;
	}

	/**
	 * Retourne l'affectation courante.
	 * 
	 * @return affectationCourant
	 */
	private Affectation getAffectationCourant() {
		return affectationCourant;
	}

	/**
	 * Met a jour l'affectation courante.
	 * 
	 * @param affectationCourant
	 *            Nouvelle affectation courante
	 */
	private void setAffectationCourant(Affectation affectationCourant) {
		this.affectationCourant = affectationCourant;
	}

	private EntiteDto getService() {
		return service;
	}

	/**
	 * Setter du Service.
	 * 
	 * @param service
	 */
	private void setService(EntiteDto service) {
		this.service = service;
	}

	/**
	 * Getter du Titre du poste.
	 */
	private String getTitrePoste() {
		return titrePoste;
	}

	/**
	 * Setter du Titre du poste.
	 * 
	 * @param titrePoste
	 */
	private void setTitrePoste(String titrePoste) {
		this.titrePoste = titrePoste;
	}

	private EntiteDto getDirection() {
		return direction;
	}

	/**
	 * Setter de la direction du service.
	 * 
	 * @param direction
	 */
	private void setDirection(EntiteDto direction) {
		this.direction = direction;
	}

	private String getLocalisation() {
		return localisation;
	}

	/**
	 * Setter de la localisation.
	 * 
	 * @param localisation
	 */
	private void setLocalisation(String localisation) {
		this.localisation = localisation;
	}

	private FichePoste getResponsable() {
		return responsable;
	}

	/**
	 * Setter du responsable.
	 * 
	 * @param responsable
	 *            responsable à  définir
	 */
	private void setResponsable(FichePoste responsable) {
		this.responsable = responsable;
	}

	private String getCadreEmploi() {
		return cadreEmploi;
	}

	/**
	 * Setter du cadreEmploi.
	 * 
	 * @param cadreEmploi
	 */
	private void setCadreEmploi(String cadreEmploi) {
		this.cadreEmploi = cadreEmploi;
	}

	private String getGradeFP() {
		return gradeFP;
	}

	/**
	 * Setter du garde de la fiche de poste.
	 * 
	 * @param gradeFP
	 */
	private void setGradeFP(String gradeFP) {
		this.gradeFP = gradeFP;
	}

	/**
	 * @param diplomeAgt
	 *            diplomeAgt à  définir
	 */
	private void setDiplomeAgt(String diplomeAgt) {
		this.diplomeAgt = diplomeAgt;
	}

	public String getNomEcran() {
		return "ECR-AG-EMPLOIS-POSTE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VOIR_AUTRE_FP Date de
	 * création : (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_VOIR_AUTRE_FP() {
		return "NOM_PB_VOIR_AUTRE_FP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_VALIDER_IMPRIMER() {
		return "NOM_PB_VALIDER_IMPRIMER";
	}

	private boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		imprimeModele(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean imprimeModele(HttpServletRequest request) throws Exception {

		String nomFichier = "FP_" + getFichePosteCourant().getIdFichePoste() + ".doc";
		String url = "PrintDocument?fromPage=" + this.getClass().getName() + "&nomFichier=" + nomFichier + "&idFichePoste="
				+ getFichePosteCourant().getIdFichePoste();
		setURLFichier(getScriptOuverture(url));

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		return true;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
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
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_WARNING Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		return focus;
	}

	/**
	 * Met a jour le focus de la JSP.
	 * 
	 * @param focus
	 */
	private void setFocus(String focus) {
		this.focus = focus;
	}

	private String getDiplomeAgt() {
		return diplomeAgt;
	}

	private String getGradeAgt() {
		return gradeAgt;
	}

	private void setGradeAgt(String gradeAgt) {
		this.gradeAgt = gradeAgt;
	}

	private EntiteDto getSection() {
		return section;
	}

	private void setSection(EntiteDto section) {
		this.section = section;
	}

	public FichePoste getSuperieurHierarchique() {
		return superieurHierarchique;
	}

	public void setSuperieurHierarchique(FichePoste superieurHierarchique) throws Exception {
		this.superieurHierarchique = superieurHierarchique;
		if (superieurHierarchique != null) {
			try {
				setAgtResponsable(getAgentDao().chercherAgentAffecteFichePoste(getSuperieurHierarchique().getIdFichePoste()));
			} catch (Exception e) {
				setAgtResponsable(null);
			}
			setTitrePosteResponsable(getTitrePosteDao().chercherTitrePoste(getSuperieurHierarchique().getIdTitrePoste()));
		} else {
			setAgtResponsable(null);
			setTitrePosteResponsable(null);
		}
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (08/11/11 16:35:28)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEmploisPoste.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SECTION Date de
	 * création : (08/11/11 16:35:28)
	 * 
	 */
	public String getNOM_ST_SECTION() {
		return "NOM_ST_SECTION";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_SECTION Date
	 * de création : (08/11/11 16:35:28)
	 * 
	 */
	public String getVAL_ST_SECTION() {
		return getZone(getNOM_ST_SECTION());
	}

	public ArrayList<Competence> getListeCompetence() {
		if (listeCompetence == null)
			listeCompetence = new ArrayList<Competence>();
		return listeCompetence;
	}

	private void setListeCompetence(ArrayList<Competence> listeCompetence) {
		this.listeCompetence = listeCompetence;
	}

	public TypeCompetence getTypeCompetenceCourant() {
		return typeCompetenceCourant;
	}

	private void setTypeCompetenceCourant(TypeCompetence typeCompetenceCourant) {
		this.typeCompetenceCourant = typeCompetenceCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_TYPE Date de
	 * création : (04/07/11 13:57:35)
	 * 
	 */
	public String getNOM_PB_CHANGER_TYPE() {
		return "NOM_PB_CHANGER_TYPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/07/11 13:57:35)
	 * 
	 */
	public boolean performPB_CHANGER_TYPE(HttpServletRequest request) throws Exception {
		setFocus(getNOM_PB_IMPRIMER());
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TYPE_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RG_TYPE_COMPETENCE() {
		return "NOM_RG_TYPE_COMPETENCE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TYPE_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getVAL_RG_TYPE_COMPETENCE() {
		return getZone(getNOM_RG_TYPE_COMPETENCE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_C Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_C() {
		return "NOM_RB_TYPE_COMPETENCE_C";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_S Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_S() {
		return "NOM_RB_TYPE_COMPETENCE_S";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_SF Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_SF() {
		return "NOM_RB_TYPE_COMPETENCE_SF";
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_ACTI Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_ACTI Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_COMP_SF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_SF(int i) {
		return "NOM_ST_LIB_COMP_SF" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_COMPSF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_SF(int i) {
		return getZone(getNOM_ST_LIB_COMP_SF(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_COMP_S
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_S(int i) {
		return "NOM_ST_LIB_COMP_S" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_COMPS Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_S(int i) {
		return getZone(getNOM_ST_LIB_COMP_S(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_COMP_PRO
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_PRO(int i) {
		return "NOM_ST_LIB_COMP_PRO" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB_COMPPRO
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_PRO(int i) {
		return getZone(getNOM_ST_LIB_COMP_PRO(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AV_TYPE(int i) {
		return "NOM_ST_AV_TYPE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AV_TYPE(int i) {
		return getZone(getNOM_ST_AV_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_MNT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AV_MNT(int i) {
		return "NOM_ST_AV_MNT" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AV_MNT(int i) {
		return getZone(getNOM_ST_AV_MNT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_NATURE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AV_NATURE(int i) {
		return "NOM_ST_AV_NATURE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AV_NATURE(int i) {
		return getZone(getNOM_ST_AV_NATURE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DEL_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEL_TYPE(int i) {
		return "NOM_ST_DEL_TYPE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEL_TYPE(int i) {
		return getZone(getNOM_ST_DEL_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_DEL_COMMENTAIRE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEL_COMMENTAIRE(int i) {
		return "NOM_ST_DEL_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEL_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_DEL_COMMENTAIRE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REG_TYPE(int i) {
		return "NOM_ST_REG_TYPE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REG_TYPE(int i) {
		return getZone(getNOM_ST_REG_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_FORFAIT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REG_FORFAIT(int i) {
		return "NOM_ST_REG_FORFAIT" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REG_FORFAIT(int i) {
		return getZone(getNOM_ST_REG_FORFAIT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_NB_PTS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REG_NB_PTS(int i) {
		return "NOM_ST_REG_NB_PTS" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REG_NB_PTS(int i) {
		return getZone(getNOM_ST_REG_NB_PTS(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_PP_RUBR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PP_RUBR(int i) {
		return "NOM_ST_PP_RUBR" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PP_RUBR(int i) {
		return getZone(getNOM_ST_PP_RUBR(i));
	}

	public PrimePointageFPDao getPrimePointageFPDao() {
		return primePointageFPDao;
	}

	public void setPrimePointageFPDao(PrimePointageFPDao primePointageFPDao) {
		this.primePointageFPDao = primePointageFPDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REMPLACEMENT Date de
	 * création : (11/10/11 10:23:53)
	 */
	public String getNOM_ST_REMPLACEMENT() {
		return "NOM_ST_REMPLACEMENT";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_REMPLACEMENT
	 * Date de création : (11/10/11 10:23:53)
	 */
	public String getVAL_ST_REMPLACEMENT() {
		return getZone(getNOM_ST_REMPLACEMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_REMP Date de
	 * création : (29/11/11 16:42:44)
	 */
	public String getNOM_ST_INFO_REMP() {
		return "NOM_ST_INFO_REMP";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_INFO_REMP Date
	 * de création : (29/11/11 16:42:44)
	 */
	public String getVAL_ST_INFO_REMP() {
		return getZone(getNOM_ST_INFO_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_RESP Date de
	 * création : (29/11/11 16:42:44)
	 */
	public String getNOM_ST_INFO_RESP() {
		return "NOM_ST_INFO_RESP";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_INFO_RESP Date
	 * de création : (29/11/11 16:42:44)
	 */
	public String getVAL_ST_INFO_RESP() {
		return getZone(getNOM_ST_INFO_RESP());
	}

	/**
	 * Affiche la fiche de poste "Responsable"
	 */
	private void afficheResponsable() {
		if (getResponsable() != null) {
			addZone(getNOM_ST_RESPONSABLE(), getTitrePosteResponsable().getLibTitrePoste() + " (" + getResponsable().getNumFp() + ")");
			if (getAgtResponsable() != null) {
				addZone(getNOM_ST_INFO_RESP(), getAgtResponsable().getNomAgent() + " " + getAgtResponsable().getPrenomAgent() + " ("
						+ getAgtResponsable().getNomatr() + ")");
			} else {
				addZone(getNOM_ST_INFO_RESP(), "Cette fiche de poste n'est pas affectée");
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
			addZone(getNOM_ST_REMPLACEMENT(), getTitrePosteRemplacement().getLibTitrePoste() + " (" + getRemplacement().getNumFp() + ")");
			if (getAgtRemplacement() != null) {
				addZone(getNOM_ST_INFO_REMP(), getAgtRemplacement().getNomAgent() + " " + getAgtRemplacement().getPrenomAgent() + " ("
						+ getAgtRemplacement().getNomatr() + ")");
			} else {
				addZone(getNOM_ST_INFO_REMP(), "Cette fiche de poste n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_REMPLACEMENT(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		}
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

	public FicheMetier getMetierPrimaire() {
		return metierPrimaire;
	}

	public void setMetierPrimaire(FicheMetier metierPrimaire) {
		this.metierPrimaire = metierPrimaire;
	}

	public FicheMetier getMetierSecondaire() {
		return metierSecondaire;
	}

	public void setMetierSecondaire(FicheMetier metierSecondaire) {
		this.metierSecondaire = metierSecondaire;
	}

	public CadreEmploiDao getCadreEmploiDao() {
		return cadreEmploiDao;
	}

	public void setCadreEmploiDao(CadreEmploiDao cadreEmploiDao) {
		this.cadreEmploiDao = cadreEmploiDao;
	}

	public NatureAvantageDao getNatureAvantageDao() {
		return natureAvantageDao;
	}

	public void setNatureAvantageDao(NatureAvantageDao natureAvantageDao) {
		this.natureAvantageDao = natureAvantageDao;
	}

	public SpecialiteDiplomeDao getSpecialiteDiplomeDao() {
		return specialiteDiplomeDao;
	}

	public void setSpecialiteDiplomeDao(SpecialiteDiplomeDao specialiteDiplomeDao) {
		this.specialiteDiplomeDao = specialiteDiplomeDao;
	}

	public TitreDiplomeDao getTitreDiplomeDao() {
		return titreDiplomeDao;
	}

	public void setTitreDiplomeDao(TitreDiplomeDao titreDiplomeDao) {
		this.titreDiplomeDao = titreDiplomeDao;
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

	public DelegationDao getDelegationDao() {
		return delegationDao;
	}

	public void setDelegationDao(DelegationDao delegationDao) {
		this.delegationDao = delegationDao;
	}

	public RegIndemnDao getRegIndemnDao() {
		return regIndemnDao;
	}

	public void setRegIndemnDao(RegIndemnDao regIndemnDao) {
		this.regIndemnDao = regIndemnDao;
	}

	public TypeCompetenceDao getTypeCompetenceDao() {
		return typeCompetenceDao;
	}

	public void setTypeCompetenceDao(TypeCompetenceDao typeCompetenceDao) {
		this.typeCompetenceDao = typeCompetenceDao;
	}

	public DiplomeAgentDao getDiplomeAgentDao() {
		return diplomeAgentDao;
	}

	public void setDiplomeAgentDao(DiplomeAgentDao diplomeAgentDao) {
		this.diplomeAgentDao = diplomeAgentDao;
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

	public BudgetDao getBudgetDao() {
		return budgetDao;
	}

	public void setBudgetDao(BudgetDao budgetDao) {
		this.budgetDao = budgetDao;
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

	public ActiviteDao getActiviteDao() {
		return activiteDao;
	}

	public void setActiviteDao(ActiviteDao activiteDao) {
		this.activiteDao = activiteDao;
	}

	public ActiviteMetierDao getActiviteMetierDao() {
		return activiteMetierDao;
	}

	public void setActiviteMetierDao(ActiviteMetierDao activiteMetierDao) {
		this.activiteMetierDao = activiteMetierDao;
	}

	public SavoirFaireDao getSavoirFaireDao() {
		return savoirFaireDao;
	}

	public void setSavoirFaireDao(SavoirFaireDao savoirFaireDao) {
		this.savoirFaireDao = savoirFaireDao;
	}

    public ActiviteGeneraleDao getActiviteGeneraleDao() {
        return activiteGeneraleDao;
    }

    public void setActiviteGeneraleDao(ActiviteGeneraleDao activiteGeneraleDao) {
        this.activiteGeneraleDao = activiteGeneraleDao;
    }

	public ConditionExerciceDao getConditionExerciceDao() {
		return conditionExerciceDao;
	}

	public void setConditionExerciceDao(ConditionExerciceDao conditionExerciceDao) {
		this.conditionExerciceDao = conditionExerciceDao;
	}

	public FMFPDao getFmfpDao() {
		return fmfpDao;
	}

	public void setFmfpDao(FMFPDao fmfpDao) {
		this.fmfpDao = fmfpDao;
	}

	public FicheMetierDao getFicheMetierDao() {
		return ficheMetierDao;
	}

	public void setFicheMetierDao(FicheMetierDao ficheMetierDao) {
		this.ficheMetierDao = ficheMetierDao;
	}

	public ActiviteFPDao getActiviteFPDao() {
		return activiteFPDao;
	}

	public void setActiviteFPDao(ActiviteFPDao activiteFPDao) {
		this.activiteFPDao = activiteFPDao;
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

	public boolean versionFicheMetier() {
		return getMetierPrimaire() != null;
	}
}
