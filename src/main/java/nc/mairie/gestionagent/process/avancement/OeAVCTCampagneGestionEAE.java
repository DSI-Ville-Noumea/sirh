package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.eae.dto.AgentEaeDto;
import nc.mairie.gestionagent.eae.dto.BirtDto;
import nc.mairie.gestionagent.eae.dto.CampagneEaeDto;
import nc.mairie.gestionagent.eae.dto.EaeCampagneTaskDto;
import nc.mairie.gestionagent.eae.dto.EaeDto;
import nc.mairie.gestionagent.eae.dto.EaeEvaluationDto;
import nc.mairie.gestionagent.eae.dto.EaeFichePosteDto;
import nc.mairie.gestionagent.eae.dto.EaeFinalizationDto;
import nc.mairie.gestionagent.eae.dto.FormRehercheGestionEae;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.avancement.AvancementDetachesDao;
import nc.mairie.spring.dao.metier.avancement.AvancementFonctionnairesDao;
import nc.mairie.spring.dao.metier.diplome.DiplomeAgentDao;
import nc.mairie.spring.dao.metier.diplome.FormationAgentDao;
import nc.mairie.spring.dao.metier.parametrage.CentreFormationDao;
import nc.mairie.spring.dao.metier.parametrage.MotifAvancementDao;
import nc.mairie.spring.dao.metier.parametrage.SpecialiteDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TitreDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TitreFormationDao;
import nc.mairie.spring.dao.metier.poste.ActiviteDao;
import nc.mairie.spring.dao.metier.poste.ActiviteFPDao;
import nc.mairie.spring.dao.metier.poste.AffectationDao;
import nc.mairie.spring.dao.metier.poste.CompetenceDao;
import nc.mairie.spring.dao.metier.poste.CompetenceFPDao;
import nc.mairie.spring.dao.metier.poste.FEFPDao;
import nc.mairie.spring.dao.metier.poste.FicheEmploiDao;
import nc.mairie.spring.dao.metier.poste.FichePosteDao;
import nc.mairie.spring.dao.metier.poste.TitrePosteDao;
import nc.mairie.spring.dao.metier.referentiel.AutreAdministrationDao;
import nc.mairie.spring.dao.metier.referentiel.TypeCompetenceDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IEaeService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;
import nc.noumea.spring.service.cmis.AlfrescoCMISService;

/**
 * Process OeAVCTFonctionnaires Date de crï¿½ation : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagneGestionEAE extends BasicProcess {

	/**
	 * 
	 */
	private static final long			serialVersionUID					= 1L;
	public static final int				STATUT_EVALUATEUR					= 1;
	public static final int				STATUT_RECHERCHER_AGENT				= 2;
	public static final int				STATUT_RECHERCHER_AGENT_EVALUATEUR	= 3;
	public static final int				STATUT_RECHERCHER_AGENT_EVALUE		= 4;

	private String[]					LB_ANNEE;
	private String[]					LB_ETAT;
	private String[]					LB_STATUT;
	private String[]					LB_CAP;
	private String[]					LB_AFFECTE;
	private List<CampagneEaeDto>		listeCampagneEAE;
	private List<EnumEtatEAE>			listeEnumEtatEAE;
	private List<String>				listeStatut;
	private List<String>				listeCAP;
	private List<String>				listeAffecte;

	private CampagneEaeDto				campagneCourante;

	private List<EaeDto>				listeEAE;
	private EaeDto						eaeCourant;

	private Integer						idCreerFichePosteSecondaire;
	private Integer						idCreerFichePostePrimaire;

	private TitreFormationDao			titreFormationDao;
	private CentreFormationDao			centreFormationDao;
	private FormationAgentDao			formationAgentDao;
	private MotifAvancementDao			motifAvancementDao;
	private SpecialiteDiplomeDao		specialiteDiplomeDao;
	private TitreDiplomeDao				titreDiplomeDao;

	private String						message;

	private AutreAdministrationDao		autreAdministrationDao;
	private TypeCompetenceDao			typeCompetenceDao;
	private DiplomeAgentDao				diplomeAgentDao;
	private AutreAdministrationAgentDao	autreAdministrationAgentDao;
	private AvancementDetachesDao		avancementDetachesDao;
	private AvancementFonctionnairesDao	avancementFonctionnairesDao;
	private TitrePosteDao				titrePosteDao;
	private FEFPDao						fefpDao;
	private CompetenceDao				competenceDao;
	private CompetenceFPDao				competenceFPDao;
	private ActiviteDao					activiteDao;
	private ActiviteFPDao				activiteFPDao;
	private FicheEmploiDao				ficheEmploiDao;
	private FichePosteDao				fichePosteDao;
	private AffectationDao				affectationDao;
	private AgentDao					agentDao;

	private SimpleDateFormat			sdf									= new SimpleDateFormat("dd/MM/yyyy");

	private IAdsService					adsService;
	private IRadiService				radiService;
	private IAbsService					absService;
	private ISirhService				sirhService;
	private IEaeService					eaeService;

	/**
	 * Initialisation des zones ï¿½ afficher dans la JSP Alimentation des
	 * listes, s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la
	 * liste doivent avoir les Fields PUBLIC Utilisation de la mï¿½thode
	 * addZone(getNOMxxx, String); Date de crï¿½ation : (21/11/11 09:55:36)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vï¿½rification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Initialisation des listes deroulantes
		initialiseListeDeroulante(request);

		if (etatStatut() == STATUT_EVALUATEUR) {
			initialiseEvaluateur(request);
			// on revoit l'affichage car il y a peut etre un changement de
			// statut
			performPB_FILTRER(request);
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			initialiseDelegataire(request);
			// on revoit l'affichage car il y a peut etre un changement de
			// statut
			performPB_FILTRER(request);
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT_EVALUATEUR) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT_EVALUATEUR(), agt.getNomatr().toString());
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT_EVALUE) {
			Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT_EVALUE(), agt.getNomatr().toString());
		}

		// initialisation de l'affichage la liste des eae
		initialiseAffichageListeEAE(request);

		if (getMessage() != null && !getMessage().equals(Const.CHAINE_VIDE)) {
			setStatut(STATUT_MEME_PROCESS, false, getMessage());
			setMessage(null);
		}
	}

	private void initialiseDelegataire(HttpServletRequest request) throws Exception {
		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (getEaeCourant() != null && agt != null) {

			getEaeCourant().setIdAgentDelegataire(agt.getIdAgent());
			if (getEaeCourant().getEvalue().isEstDetache()) {
				getEaeCourant().setEtat(EnumEtatEAE.NON_DEBUTE.getCode());
			}

			ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), getEaeCourant());

			if (!result.getErrors().isEmpty()) {
				getTransaction().declarerErreur(result.getErrors().get(0).toString());
			}
		}
	}

	private void initialiseEvaluateur(HttpServletRequest request) throws Exception {
		@SuppressWarnings("unchecked")
		List<Agent> listeEvaluateurSelect = (List<Agent>) VariablesActivite.recuperer(this, "EVALUATEURS");
		VariablesActivite.enlever(this, "EVALUATEURS");
		if (getEaeCourant() != null) {
			// on supprime tous les evaluateurs existants
			getEaeCourant().getEvaluateurs().clear();

			if (listeEvaluateurSelect != null && listeEvaluateurSelect.size() > 0) {
				for (int j = 0; j < listeEvaluateurSelect.size(); j++) {
					Agent agentEvaluateur = listeEvaluateurSelect.get(j);
					if (agentEvaluateur != null) {
						// on cree les nouveaux evaluateurs
						BirtDto eval = new BirtDto();
						eval.setIdAgent(agentEvaluateur.getIdAgent());

						getEaeCourant().getEvaluateurs().add(eval);
					}
				}
				// on sort de la boucle, on change l'etat de l'EAE en NON-DEBUTE
				getEaeCourant().setEtat(EnumEtatEAE.NON_DEBUTE.getCode());

				ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), getEaeCourant());

				if (!result.getErrors().isEmpty()) {
					getTransaction().declarerErreur(result.getErrors().get(0).toString());
				}

			} else {
				if (getEaeCourant() != null) {
					// si pas d'evaluateur choisi alors on passe l'eae en NON
					// AFFECTE et on supprime le delegataire
					getEaeCourant().setIdAgentDelegataire(null);
					getEaeCourant().setEtat(EnumEtatEAE.NON_AFFECTE.getCode());

					ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), getEaeCourant());

					if (!result.getErrors().isEmpty()) {
						getTransaction().declarerErreur(result.getErrors().get(0).toString());
					}
				}
			}
		}
	}

	private void initialiseAffichageListeEAE(HttpServletRequest request) throws Exception {

		for (int p = 0; p < getListeEAE().size(); p++) {
			EaeDto eae = (EaeDto) getListeEAE().get(p);
			Integer i = eae.getIdEae();
			BirtDto evalue = eae.getEvalue();
			try {
				EaeFichePosteDto eaeFDP = eae.getFichePoste();
				addZone(getNOM_ST_DIRECTION(i),
						(eaeFDP.getDirectionService() == null ? "&nbsp;" : eaeFDP.getDirectionService()) + " <br> "
								+ (eaeFDP.getSectionService() == null ? "&nbsp;" : eaeFDP.getSectionService()) + " <br> "
								+ (eaeFDP.getService() == null ? "&nbsp;" : eaeFDP.getService()));
				if (eaeFDP.getIdAgentShd() != null) {
					try {
						Agent agentResp = getAgentDao().chercherAgent(eaeFDP.getIdAgentShd());
						addZone(getNOM_ST_SHD(i), agentResp.getNomAgent() + " " + agentResp.getPrenomAgent() + " (" + agentResp.getNomatr() + ") ");
					} catch (Exception e) {
						addZone(getNOM_ST_SHD(i), "&nbsp;");
					}
				} else {
					addZone(getNOM_ST_SHD(i), "&nbsp;");
				}
			} catch (Exception e) {
				addZone(getNOM_ST_DIRECTION(i), "&nbsp;");
				addZone(getNOM_ST_SHD(i), "&nbsp;");
			}
			EaeEvaluationDto evaluation = null;
			try {
				evaluation = eae.getEvaluation();
			} catch (Exception e) {
				// on ne fait rien
			}
			Agent agentEAE = getAgentDao().chercherAgent(evalue.getIdAgent());
			addZone(getNOM_ST_MATRICULE_AGENT(i), agentEAE.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agentEAE.getNomAgent() + " " + agentEAE.getPrenomAgent());
			addZone(getNOM_ST_STATUT(i),
					(evalue.getStatut() == null ? "&nbsp;" : evalue.getStatut()) + " <br> " + (evalue.isEstDetache() ? "oui" : "&nbsp;"));
			// on recupere les evaluateurs
			List<BirtDto> listeEvaluateur = eae.getEvaluateurs();
			String eval = Const.CHAINE_VIDE;
			for (int j = 0; j < listeEvaluateur.size(); j++) {
				AgentEaeDto evaluateur = listeEvaluateur.get(j).getAgent();
				Agent agentevaluateur = getAgentDao().chercherAgent(evaluateur.getIdAgent());
				eval += agentevaluateur.getNomAgent() + " " + agentevaluateur.getPrenomAgent() + " (" + agentevaluateur.getNomatr() + ") <br> ";
			}
			addZone(getNOM_ST_EVALUATEURS(i), eval.equals(Const.CHAINE_VIDE) ? "&nbsp;" : eval);
			if (eae.getIdAgentDelegataire() != null) {
				Agent agentDelegataire = getAgentDao().chercherAgent(eae.getIdAgentDelegataire());
				addZone(getNOM_ST_DELEGATAIRE(i),
						agentDelegataire.getNomAgent() + " " + agentDelegataire.getPrenomAgent() + " (" + agentDelegataire.getNomatr() + ")");
			} else {
				addZone(getNOM_ST_DELEGATAIRE(i), "&nbsp;");
			}

			addZone(getNOM_ST_CAP(i), eae.isCap() ? "oui" : "&nbsp;");
			addZone(getNOM_ST_AVIS_SHD(i), evaluation == null || evaluation.getAvisShd() == null ? "&nbsp;" : evaluation.getAvisShd());
			addZone(getNOM_ST_EAE_JOINT(i), eae.isDocAttache() ? "oui" : "non");
			addZone(getNOM_ST_CONTROLE(i),
					EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()) + " <br> "
							+ (eae.getDateCreation() == null ? "&nbsp;" : sdf.format(eae.getDateCreation())) + " <br> "
							+ (eae.getDateFinalisation() == null ? "&nbsp;" : sdf.format(eae.getDateFinalisation())) + " <br> "
							+ (eae.getDateControle() == null ? "&nbsp;" : sdf.format(eae.getDateControle())));
			addZone(getNOM_ST_ACTIONS_DEFINALISE(i), "&nbsp;");
			addZone(getNOM_ST_ACTIONS_MAJ(i), "&nbsp;");
			addZone(getNOM_CK_VALID_MAJ(i), getCHECKED_OFF());
			addZone(getNOM_CK_VALID_EAE(i), eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_CONTROLE_PAR(i), eae.getUserControle() == null ? "&nbsp;" : eae.getUserControle());
			EaeFinalizationDto finalDoc = eae.getFinalisation() == null || eae.getFinalisation().size() == 0 ? null : eae.getFinalisation().get(0);
			addZone(getNOM_ST_URL_DOC(i), (finalDoc == null || null == finalDoc.getIdDocument() || finalDoc.getIdDocument().equals(Const.CHAINE_VIDE))
					? "&nbsp;" : AlfrescoCMISService.getUrlOfDocument(finalDoc.getIdDocument()));

		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		// AS400
		if (getFormationAgentDao() == null) {
			setFormationAgentDao(new FormationAgentDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getTitreFormationDao() == null) {
			setTitreFormationDao(new TitreFormationDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCentreFormationDao() == null) {
			setCentreFormationDao(new CentreFormationDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getMotifAvancementDao() == null) {
			setMotifAvancementDao(new MotifAvancementDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSpecialiteDiplomeDao() == null) {
			setSpecialiteDiplomeDao(new SpecialiteDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitreDiplomeDao() == null) {
			setTitreDiplomeDao(new TitreDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationDao() == null) {
			setAutreAdministrationDao(new AutreAdministrationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeCompetenceDao() == null) {
			setTypeCompetenceDao(new TypeCompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDiplomeAgentDao() == null) {
			setDiplomeAgentDao(new DiplomeAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAutreAdministrationAgentDao() == null) {
			setAutreAdministrationAgentDao(new AutreAdministrationAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementDetachesDao() == null) {
			setAvancementDetachesDao(new AvancementDetachesDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvancementFonctionnairesDao() == null) {
			setAvancementFonctionnairesDao(new AvancementFonctionnairesDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitrePosteDao() == null) {
			setTitrePosteDao(new TitrePosteDao((SirhDao) context.getBean("sirhDao")));
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
		if (getActiviteDao() == null) {
			setActiviteDao(new ActiviteDao((SirhDao) context.getBean("sirhDao")));
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
		if (getAffectationDao() == null) {
			setAffectationDao(new AffectationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (null == adsService) {
			adsService = (IAdsService) context.getBean("adsService");
		}
		if (null == radiService) {
			radiService = (IRadiService) context.getBean("radiService");
		}
		if (null == absService) {
			absService = (AbsService) context.getBean("absService");
		}
		if (null == sirhService) {
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		if (null == eaeService) {
			eaeService = (IEaeService) context.getBean("eaeService");
		}
	}

	/**
	 * Initialisation des liste deroulantes.
	 */
	private void initialiseListeDeroulante(HttpServletRequest request) throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			List<CampagneEaeDto> listeCamp = eaeService.getListeCampagnesEae(getAgentConnecte(request).getIdAgent());
			setListeCampagneEAE(listeCamp);
			int[] tailles = { 5 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<CampagneEaeDto> list = listeCamp.listIterator(); list.hasNext();) {
				CampagneEaeDto camp = (CampagneEaeDto) list.next();
				String ligne[] = { camp.getAnnee().toString() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ANNEE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			List<EnumEtatEAE> listeEtat = EnumEtatEAE.getValues();
			setListeEnumEtatEAE(listeEtat);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<EnumEtatEAE> list = listeEtat.listIterator(); list.hasNext();) {
				EnumEtatEAE etat = (EnumEtatEAE) list.next();
				String ligne[] = { etat.getValue() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ETAT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);
		}
		// Si liste affecte vide alors affectation
		if (getLB_AFFECTE() == LBVide) {
			List<String> listeAffecte = new ArrayList<String>();
			listeAffecte.add("oui");
			listeAffecte.add("non");
			setListeAffecte(listeAffecte);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeAffecte.listIterator(); list.hasNext();) {
				String affecte = (String) list.next();
				String ligne[] = { affecte };
				aFormat.ajouteLigne(ligne);
			}
			setLB_AFFECTE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_AFFECTE_SELECT(), Const.ZERO);
		}
		// Si liste statut vide alors affectation
		if (getLB_STATUT() == LBVide) {
			List<String> listeStatut = new ArrayList<String>();
			listeStatut.add("Fontionnaire");
			listeStatut.add("Convention Collective");
			listeStatut.add("Contractuel");
			listeStatut.add("Autre");
			setListeStatut(listeStatut);
			int[] tailles = { 25 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeStatut.listIterator(); list.hasNext();) {
				String statut = (String) list.next();
				String ligne[] = { statut };
				aFormat.ajouteLigne(ligne);
			}
			setLB_STATUT(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_STATUT_SELECT(), Const.ZERO);
		}
		// Si liste statut vide alors affectation
		if (getLB_CAP() == LBVide) {
			List<String> listeCAP = new ArrayList<String>();
			listeCAP.add("oui");
			listeCAP.add("non");
			setListeCAP(listeCAP);
			int[] tailles = { 5 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeCAP.listIterator(); list.hasNext();) {
				String cap = (String) list.next();
				String ligne[] = { cap };
				aFormat.ajouteLigne(ligne);
			}
			setLB_CAP(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_CAP_SELECT(), Const.ZERO);
		}
	}

	public String getCurrentWholeTreeJS(String serviceSaisi) {
		return adsService.getCurrentWholeTreeActifTransitoireJS(null != serviceSaisi && !"".equals(serviceSaisi) ? serviceSaisi : null, false);
	}

	/**
	 * mï¿½thode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de crï¿½ation : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}

			// Si clic sur le bouton PB_CALCULER
			if (testerParametre(request, getNOM_PB_INITIALISE_CALCUL_EAE_JOB())) {
				return performPB_INITIALISE_CALCUL_EAE_JOB(request);
			}

			// Si clic sur le bouton PB_GERER_EVALUATEUR
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_GERER_EVALUATEUR(eae.getIdEae()))) {
					return performPB_GERER_EVALUATEUR(request, eae);
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT(eae.getIdEae()))) {
					return performPB_RECHERCHER_AGENT(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT(eae.getIdEae()))) {
					return performPB_SUPPRIMER_RECHERCHER_AGENT(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

			// Si clic sur le bouton PB_VALID_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_VALID_EAE(eae.getIdEae()))) {
					return performPB_VALID_EAE(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_DEVALID_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DEVALID_EAE(eae.getIdEae()))) {
					return performPB_DEVALID_EAE(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_METRE_A_JOUR_EAE
			if (testerParametre(request, getNOM_PB_METTRE_A_JOUR_EAE())) {
				return performPB_METTRE_A_JOUR_EAE(request);
			}

			// Si clic sur le bouton PB_DEFINALISE_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DEFINALISE_EAE(eae.getIdEae()))) {
					return performPB_DEFINALISE_EAE(request, eae);
				}
			}

			// Si clic sur le bouton PB_SUPP_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_SUPP_EAE(eae.getIdEae()))) {
					return performPB_SUPP_EAE(request, eae);
				}
			}

			// Si clic sur le bouton PB_DESUPP_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EaeDto eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DESUPP_EAE(eae.getIdEae()))) {
					return performPB_DESUPP_EAE(request, eae);
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_EVALUATEUR
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_EVALUATEUR())) {
				return performPB_RECHERCHER_AGENT_EVALUATEUR(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT_EVALUE
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_EVALUE())) {
				return performPB_RECHERCHER_AGENT_EVALUE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT_EVALUE(request);
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
	public OeAVCTCampagneGestionEAE() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à  utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTCampagneGestionEAE.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_GERER_EVALUATEUR Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_GERER_EVALUATEUR(int i) {
		return "NOM_PB_GERER_EVALUATEUR" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implï¿½mente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de crï¿½ation : (21/11/11
	 * 09:55:36)
	 * 
	 */
	public boolean performPB_GERER_EVALUATEUR(HttpServletRequest request, EaeDto eae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		ArrayList<Agent> listeEval = new ArrayList<Agent>();

		setEaeCourant(eae);
		List<BirtDto> listeEvalEAE = getEaeCourant().getEvaluateurs();

		if (listeEvalEAE != null) {
			for (int i = 0; i < listeEvalEAE.size(); i++) {
				AgentEaeDto eval = listeEvalEAE.get(i).getAgent();
				Agent ag = getAgentDao().chercherAgent(eval.getIdAgent());
				listeEval.add(ag);
			}
		}
		VariablesActivite.ajouter(this, "LISTEEVALUATEUR", listeEval);
		setStatut(STATUT_EVALUATEUR, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_FILTRER Date de crï¿½ation :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		// setMessage(Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		setCampagneCourante((CampagneEaeDto) getListeCampagneEAE().get(indiceCampagne));

		// Recherche des eae de la campagne en fonction de l'état
		int indiceEtat = (Services.estNumerique(getVAL_LB_ETAT_SELECT()) ? Integer.parseInt(getVAL_LB_ETAT_SELECT()) : -1);
		String etat = Const.CHAINE_VIDE;
		if (indiceEtat > 0) {
			etat = getListeEnumEtatEAE().get(indiceEtat - 1).getCode();
		}
		// Recherche des eae de la campagne en fonction du statut
		int indiceStatut = (Services.estNumerique(getVAL_LB_STATUT_SELECT()) ? Integer.parseInt(getVAL_LB_STATUT_SELECT()) : -1);
		String statut = Const.CHAINE_VIDE;
		if (indiceStatut > 0) {
			String statutChaineLongue = getListeStatut().get(indiceStatut - 1);
			if (statutChaineLongue.equals("Fontionnaire")) {
				statut = "F";
			} else if (statutChaineLongue.equals("Convention Collective")) {
				statut = "CC";
			} else if (statutChaineLongue.equals("Contractuel")) {
				statut = "C";
			} else {
				statut = "A";
			}
		}
		// Recherche des eae de la campagne en fonction du service
		List<Integer> listeSousService = null;
		if (getVAL_ST_ID_SERVICE_ADS().length() != 0) {
			listeSousService = adsService.getListIdsEntiteWithEnfantsOfEntite(new Integer(getVAL_ST_ID_SERVICE_ADS()));
		}

		// Recherche des eae de la campagne en fonction du CAP
		int indiceCAP = (Services.estNumerique(getVAL_LB_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_CAP_SELECT()) : -1);
		String cap = Const.CHAINE_VIDE;
		if (indiceCAP > 0) {
			cap = getListeCAP().get(indiceCAP - 1);
		}

		// Recherche des eae de la campagne en fonction du affectes
		int indiceAffecte = (Services.estNumerique(getVAL_LB_AFFECTE_SELECT()) ? Integer.parseInt(getVAL_LB_AFFECTE_SELECT()) : -1);
		String affecte = Const.CHAINE_VIDE;
		if (indiceAffecte > 0) {
			affecte = getListeAffecte().get(indiceAffecte - 1);
		}

		// recuperation agent evaluateur
		Agent agentEvaluateur = null;
		if (getVAL_ST_AGENT_EVALUATEUR().length() != 0) {
			agentEvaluateur = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT_EVALUATEUR()));
		}

		// recuperation agent evalue
		Agent agentEvalue = null;
		if (getVAL_ST_AGENT_EVALUE().length() != 0) {
			agentEvalue = getAgentDao().chercherAgentParMatricule(Integer.valueOf(getVAL_ST_AGENT_EVALUE()));
		}

		// on affiche la liste des EAE avec le filtre
		FormRehercheGestionEae form = new FormRehercheGestionEae();
		form.setIdCampagneEae(getCampagneCourante().getIdCampagneEae());
		form.setEtat(etat);
		form.setStatut(statut);
		form.setListeSousService(listeSousService);
		form.setCap(cap.equals(Const.CHAINE_VIDE) ? null : cap.equals("oui") ? true : false);
		form.setIdAgentEvaluateur(agentEvaluateur == null || null == agentEvaluateur.getIdAgent() ? null : agentEvaluateur.getIdAgent());
		form.setIdAgentEvalue(agentEvalue == null || null == agentEvalue.getIdAgent() ? null : agentEvalue.getIdAgent());
		form.setIsEstDetache(affecte.equals(Const.CHAINE_VIDE) ? null : affecte.equals("oui") ? true : false);

		List<EaeDto> listeEAE = eaeService.getListeEaeDto(getAgentConnecte(request).getIdAgent(), form);

		setListeEAE(listeEAE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CALCULER Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_PB_CALCULER() {
		return "NOM_PB_CALCULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CALCULER Date de création :
	 * (24/03/14)
	 * 
	 */
	public String getNOM_PB_INITIALISE_CALCUL_EAE_JOB() {
		return "NOM_PB_INITIALISE_CALCUL_EAE_JOB";
	}

	/**
	 * Cree une ligne dans la table EAE_CAMPAGNE_TASK pour lancer le job de
	 * calcul des EAEs
	 */
	public boolean performPB_INITIALISE_CALCUL_EAE_JOB(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		if (getListeCampagneEAE() != null && getListeCampagneEAE().size() > 0) {
			int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			setCampagneCourante((CampagneEaeDto) getListeCampagneEAE().get(indiceCampagne));

			if (!initialiseListeEAEWithJob(request)) {
				return false;
			}

			// "INF202","Calcul effectué."
			setMessage(MessageUtils.getMessage("INF502"));
			return true;
		} else {
			return false;
		}
	}

	private boolean initialiseListeEAEWithJob(HttpServletRequest request) throws Exception {
		if (etatStatut() != STATUT_EVALUATEUR) {
			if (getListeCampagneEAE().size() > 0) {
				// si il s'agit d'une campagne ouverte on fait le calcul
				if (getCampagneCourante().estOuverte()) {
					// on enregistre une ligne dans la table EAE_CAMPAGNE_TASK
					// un JOB effectuera le calcul des EAEs
					EaeCampagneTaskDto eaeCampagneTask = eaeService.findEaeCampagneTaskByIdCampagneEae(getAgentConnecte(request).getIdAgent(),
							getCampagneCourante().getIdCampagneEae());

					if (null == eaeCampagneTask.getDateCalculEae() && eaeCampagneTask.getIdEaeCampagneTask() != null) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR216"));
						return false;
					} else {
						EaeCampagneTaskDto campagneTaskDto = new EaeCampagneTaskDto();
						campagneTaskDto.setIdEaeCampagne(getCampagneCourante().getIdCampagneEae());
						campagneTaskDto.setAnnee(getCampagneCourante().getAnnee());
						campagneTaskDto.setIdAgent(getAgentConnecte(request).getIdAgent());

						eaeService.saveEaeCampagneTask(getAgentConnecte(request).getIdAgent(), campagneTaskDto);
					}
				}
			}
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
			agentConnecte = getAgentDao().chercherAgentParMatricule(radiService.getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
		} catch (Exception e) {
			return null;
		}

		return agentConnecte;
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTION Date
	 * de création : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-CAMPAGNE-GESTION";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_ANNEE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice à  sélectionner pour la zone
	 * de la JSP : LB_ANNEE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DIRECTION
	 * Date de création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_STATUT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SHD Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_SHD(int i) {
		return "NOM_ST_SHD_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_SHD Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_SHD(int i) {
		return getZone(getNOM_ST_SHD(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EVALUATEURS Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_EVALUATEURS(int i) {
		return "NOM_ST_EVALUATEURS_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_EVALUATEURS
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_EVALUATEURS(int i) {
		return getZone(getNOM_ST_EVALUATEURS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DELEGATAIRE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DELEGATAIRE(int i) {
		return "NOM_ST_DELEGATAIRE_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_DELEGATAIRE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DELEGATAIRE(int i) {
		return getZone(getNOM_ST_DELEGATAIRE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CAP Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CAP(int i) {
		return "NOM_ST_CAP_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CAP(int i) {
		return getZone(getNOM_ST_CAP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AVIS_SHD Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AVIS_SHD(int i) {
		return "NOM_ST_AVIS_SHD_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AVIS_SHD Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AVIS_SHD(int i) {
		return getZone(getNOM_ST_AVIS_SHD(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EAE_JOINT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_EAE_JOINT(int i) {
		return "NOM_ST_EAE_JOINT_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_EAE_JOINT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_EAE_JOINT(int i) {
		return getZone(getNOM_ST_EAE_JOINT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CONTROLE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CONTROLE(int i) {
		return "NOM_ST_CONTROLE_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CONTROLE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CONTROLE(int i) {
		return getZone(getNOM_ST_CONTROLE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTIONS_DEFINALISE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACTIONS_DEFINALISE(int i) {
		return "NOM_ST_ACTIONS_DEFINALISE_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_ACTIONS_DEFINALISE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACTIONS_DEFINALISE(int i) {
		return getZone(getNOM_ST_ACTIONS_DEFINALISE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTIONS_MAJ Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ACTIONS_MAJ(int i) {
		return "NOM_ST_ACTIONS_MAJ_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_ACTIONS_MAJ
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ACTIONS_MAJ(int i) {
		return getZone(getNOM_ST_ACTIONS_MAJ(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CONTROLE_PAR Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CONTROLE_PAR(int i) {
		return "NOM_ST_CONTROLE_PAR_" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CONTROLE_PAR
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CONTROLE_PAR(int i) {
		return getZone(getNOM_ST_CONTROLE_PAR(i));
	}

	public List<EaeDto> getListeEAE() {
		if (listeEAE == null)
			return new ArrayList<EaeDto>();
		return listeEAE;
	}

	public void setListeEAE(List<EaeDto> listeEAE) {
		this.listeEAE = listeEAE;
	}

	private List<CampagneEaeDto> getListeCampagneEAE() {
		if (listeCampagneEAE == null)
			return new ArrayList<CampagneEaeDto>();
		return listeCampagneEAE;
	}

	private void setListeCampagneEAE(List<CampagneEaeDto> listeCampagneEAE) {
		this.listeCampagneEAE = listeCampagneEAE;
	}

	public CampagneEaeDto getCampagneCourante() {
		return campagneCourante;
	}

	public void setCampagneCourante(CampagneEaeDto campagneCourante) {
		this.campagneCourante = campagneCourante;
	}

	public EaeDto getEaeCourant() {
		return eaeCourant;
	}

	public void setEaeCourant(EaeDto eaeCourant) {
		this.eaeCourant = eaeCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT(int i) {
		return "NOM_PB_RECHERCHER_AGENT" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		EaeDto eaeCourant = eaeService.getDetailsEae(getAgentConnecte(request).getIdAgent(), idEae);
		setEaeCourant(eaeCourant);

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT(int i) {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT" + i;
	}

	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// On enlève l'agent selectionnée
		EaeDto eaeSelection = eaeService.getDetailsEae(getAgentConnecte(request).getIdAgent(), idEae);
		eaeSelection.setIdAgentDelegataire(null);

		// si EAE d'un détaché alors on repasse le statut a "NA"
		BirtDto eval = eaeSelection.getEvalue();
		if (eval.isEstDetache()) {
			eaeSelection.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
		}

		eaeService.setEae(getAgentConnecte(request).getIdAgent(), eaeSelection);
		// on réinitilise l'affichage
		performPB_FILTRER(request);
		return true;
	}

	public Integer getIdCreerFichePosteSecondaire() {
		return idCreerFichePosteSecondaire;
	}

	public void setIdCreerFichePosteSecondaire(Integer idCreerFichePosteSecondaire) {
		this.idCreerFichePosteSecondaire = idCreerFichePosteSecondaire;
	}

	public Integer getIdCreerFichePostePrimaire() {
		return idCreerFichePostePrimaire;
	}

	public void setIdCreerFichePostePrimaire(Integer idCreerFichePostePrimaire) {
		this.idCreerFichePostePrimaire = idCreerFichePostePrimaire;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ETAT() {
		if (LB_ETAT == null)
			LB_ETAT = initialiseLazyLB();
		return LB_ETAT;
	}

	/**
	 * Setter de la liste: LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ETAT(String[] newLB_ETAT) {
		LB_ETAT = newLB_ETAT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ETAT Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_ETAT() {
		return "NOM_LB_ETAT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ETAT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ETAT_SELECT() {
		return "NOM_LB_ETAT_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice à  sélectionner pour la zone
	 * de la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	public List<EnumEtatEAE> getListeEnumEtatEAE() {
		if (listeEnumEtatEAE == null)
			return new ArrayList<EnumEtatEAE>();
		return listeEnumEtatEAE;
	}

	public void setListeEnumEtatEAE(List<EnumEtatEAE> listeEnumEtatEAE) {
		this.listeEnumEtatEAE = listeEnumEtatEAE;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUT Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_STATUT() {
		if (LB_STATUT == null)
			LB_STATUT = initialiseLazyLB();
		return LB_STATUT;
	}

	/**
	 * Setter de la liste: LB_STATUT Date de création : (28/11/11)
	 * 
	 */
	private void setLB_STATUT(String[] newLB_STATUT) {
		LB_STATUT = newLB_STATUT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUT Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_STATUT() {
		return "NOM_LB_STATUT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_STATUT_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_STATUT_SELECT() {
		return "NOM_LB_STATUT_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_STATUT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice à  sélectionner pour la zone
	 * de la JSP : LB_STATUT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_STATUT_SELECT() {
		return getZone(getNOM_LB_STATUT_SELECT());
	}

	public List<String> getListeStatut() {
		if (listeStatut == null)
			return new ArrayList<String>();
		return listeStatut;
	}

	public void setListeStatut(List<String> listeStatut) {
		this.listeStatut = listeStatut;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (13/09/11 11:47:15)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
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
		setMessage(Const.CHAINE_VIDE);
		// On enleve le service selectionnee
		addZone(getNOM_ST_ID_SERVICE_ADS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_ID_SERVICE_ADS() {
		return "NOM_ST_ID_SERVICE_ADS";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_ID_SERVICE_ADS() {
		return getZone(getNOM_ST_ID_SERVICE_ADS());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CAP Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_CAP() {
		if (LB_CAP == null)
			LB_CAP = initialiseLazyLB();
		return LB_CAP;
	}

	/**
	 * Setter de la liste: LB_CAP Date de création : (28/11/11)
	 * 
	 */
	private void setLB_CAP(String[] newLB_CAP) {
		LB_CAP = newLB_CAP;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CAP Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_CAP() {
		return "NOM_LB_CAP";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_CAP_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_CAP_SELECT() {
		return "NOM_LB_CAP_SELECT";
	}

	/**
	 * Méthode à  personnaliser Retourne la valeur à  afficher pour la zone de
	 * la JSP : LB_CAP Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_CAP() {
		return getLB_CAP();
	}

	/**
	 * Méthode à  personnaliser Retourne l'indice à  sélectionner pour la zone
	 * de la JSP : LB_CAP Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_CAP_SELECT() {
		return getZone(getNOM_LB_CAP_SELECT());
	}

	public List<String> getListeCAP() {
		if (listeCAP == null)
			return new ArrayList<String>();
		return listeCAP;
	}

	public void setListeCAP(List<String> listeCAP) {
		this.listeCAP = listeCAP;
	}

	public TitreFormationDao getTitreFormationDao() {
		return titreFormationDao;
	}

	public void setTitreFormationDao(TitreFormationDao titreFormationDao) {
		this.titreFormationDao = titreFormationDao;
	}

	public CentreFormationDao getCentreFormationDao() {
		return centreFormationDao;
	}

	public void setCentreFormationDao(CentreFormationDao centreFormationDao) {
		this.centreFormationDao = centreFormationDao;
	}

	public FormationAgentDao getFormationAgentDao() {
		return formationAgentDao;
	}

	public void setFormationAgentDao(FormationAgentDao formationAgentDao) {
		this.formationAgentDao = formationAgentDao;
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP :
	 * CK_VALID_EAE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_EAE(int i) {
		return "NOM_CK_VALID_EAE_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case à  cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_EAE(int i) {
		return getZone(getNOM_CK_VALID_EAE(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_PB_CREER_EAE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALID_EAE(int i) {
		return "NOM_PB_VALID_EAE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALID_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		EaeDto eaeCourant = eaeService.getDetailsEae(getAgentConnecte(request).getIdAgent(), idEae);
		setEaeCourant(eaeCourant);
		BirtDto evalue = eaeCourant.getEvalue();
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		if (getVAL_CK_VALID_EAE(idEae).equals(getCHECKED_ON())) {

			// on cherche pour chaque EAE de la campagne si il y a une ligne
			// dans
			// Avanacement pourla meme année
			MotifAvancement motifRevalo = getMotifAvancementDao().chercherMotifAvancementByLib("REVALORISATION");
			MotifAvancement motifAD = getMotifAvancementDao().chercherMotifAvancementByLib("AVANCEMENT DIFFERENCIE");
			MotifAvancement motifPromo = getMotifAvancementDao().chercherMotifAvancementByLib("PROMOTION");
			MotifAvancement motifTitu = getMotifAvancementDao().chercherMotifAvancementByLib("TITULARISATION");

			try {
				AvancementFonctionnaires avct = getAvancementFonctionnairesDao()
						.chercherAvancementFonctionnaireAvecAnneeEtAgent(getCampagneCourante().getAnnee(), evalue.getIdAgent());
				if (avct.getGrade() != null) {
					Grade gradeAgent = Grade.chercherGrade(getTransaction(), avct.getGrade());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						avct.setIdMotifAvct(null);
						avct.setAvisShd(null);
					} else {
						String typeAvct = gradeAgent.getCodeTava();
						if (!typeAvct.equals(Const.CHAINE_VIDE)) {
							// on cherche le type avancement correspondant
							try {
								MotifAvancement motif = getMotifAvancementDao().chercherMotifAvancement(Integer.valueOf(typeAvct));
								avct.setIdMotifAvct(motif.getIdMotifAvct());
								EaeEvaluationDto eval = getEaeCourant().getEvaluation();
								if (typeAvct.equals(motifRevalo.getIdMotifAvct())) {
									avct.setAvisShd(eval.getAvisRevalorisation() ? "Favorable" : "Défavorable");
								} else if (typeAvct.equals(motifAD.getIdMotifAvct())) {
									avct.setAvisShd(eval.getPropositionAvancement().getCourant());
								} else if (typeAvct.equals(motifTitu.getIdMotifAvct())) {
									avct.setAvisShd("MOY");
								} else if (typeAvct.equals(motifPromo.getIdMotifAvct())) {
									avct.setAvisShd(eval.getAvisChangementClasse() ? "Favorable" : "Défavorable");
								} else {
									avct.setAvisShd(null);
								}
							} catch (Exception e) {
								avct.setIdMotifAvct(null);
								avct.setAvisShd(null);
							}
						} else {
							avct.setIdMotifAvct(null);
							avct.setAvisShd(null);
						}
					}
				} else {
					avct.setIdMotifAvct(null);
					avct.setAvisShd(null);
				}
				getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(),
						avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(),
						avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(),
						avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(),
						avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(),
						avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(),
						avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(),
						avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(),
						avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(),
						avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(),
						avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(),
						avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa(), avct.isAutre());

				if (getTransaction().isErreur())
					return false;

				// tout s'est bien passé
				commitTransaction();
			} catch (Exception e) {
				// "INF500",
				// "Aucun avancement n'a été trouvé pour cet EAE. Le motif et
				// l'avis SHD n'ont pu être mis à  jour.");
				setMessage(MessageUtils.getMessage("INF500"));
			}

			// on met à  jour le statut de l'EAE
			getEaeCourant().setEtat(EnumEtatEAE.CONTROLE.getCode());
			getEaeCourant().setDateControle(new Date());
			getEaeCourant().setUserControle(user.getUserName());

			ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), getEaeCourant());

			if (!result.getErrors().isEmpty()) {
				getTransaction().declarerErreur(result.getErrors().get(0).toString());
				return false;
			}
		}
		// on reinitialise l'affichage du tableau
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_PB_CREER_EAE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_DEVALID_EAE(int i) {
		return "NOM_PB_DEVALID_EAE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_DEVALID_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		EaeDto eaeCourant = eaeService.getDetailsEae(getAgentConnecte(request).getIdAgent(), idEae);
		setEaeCourant(eaeCourant);
		BirtDto evalue = getEaeCourant().getEvalue();
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		try {
			AvancementFonctionnaires avct = getAvancementFonctionnairesDao()
					.chercherAvancementFonctionnaireAvecAnneeEtAgent(getCampagneCourante().getAnnee(), evalue.getIdAgent());
			avct.setIdMotifAvct(null);
			avct.setAvisShd(null);

			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(),
					avct.getDirectionService(), avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(),
					avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(),
					avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(),
					avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(),
					avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(),
					avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(),
					avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(),
					avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(),
					avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
					avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(),
					avct.getIdCap(), avct.getCodePa(), avct.isAutre());

			// tout s'est bien passé
			commitTransaction();
		} catch (Exception e) {
			// "INF500",
			// "Aucun avancement n'a été trouvé pour cet EAE. Le motif et l'avis
			// SHD 'nont pu être mis à  jour.");
			setMessage(MessageUtils.getMessage("INF500"));
		}

		// on met à  jour le statut de l'EAE
		getEaeCourant().setEtat(EnumEtatEAE.FINALISE.getCode());
		getEaeCourant().setDateControle(new Date());
		getEaeCourant().setUserControle(user.getUserName());

		ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), getEaeCourant());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		// on reinitialise l'affichage du tableau
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean perform_METTRE_A_JOUR_EAE(HttpServletRequest request, EaeDto eaeChoisi) throws Exception {
		setEaeCourant(eaeChoisi);

		ReturnMessageDto result = eaeService.updateEae(getAgentConnecte(request).getIdAgent(), eaeChoisi.getIdEae());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_PB_CREER_EAE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_DEFINALISE_EAE(int i) {
		return "NOM_PB_DEFINALISE_EAE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_DEFINALISE_EAE(HttpServletRequest request, EaeDto eaeCourant) throws Exception {

		setMessage(Const.CHAINE_VIDE);
		setEaeCourant(eaeCourant);

		getEaeCourant().setEtat(EnumEtatEAE.EN_COURS.getCode());

		ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), getEaeCourant());

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		// pour reinitiliser l'affichage du tableau.
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPP_EAE Date de création :
	 * (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_SUPP_EAE(int i) {
		return "NOM_PB_SUPP_EAE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_SUPP_EAE(HttpServletRequest request, EaeDto eaeSelection) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		// on supprime tous les evaluateurs existants
		eaeSelection.getEvaluateurs().clear();
		// on supprime le delegataire
		eaeSelection.setIdAgentDelegataire(null);
		// on met à  jour le statut de l'EAE
		eaeSelection.setEtat(EnumEtatEAE.SUPPRIME.getCode());

		ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), eaeSelection);

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
			return false;
		}

		performPB_FILTRER(request);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DESUPP_EAE Date de création
	 * : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_DESUPP_EAE(int i) {
		return "NOM_PB_DESUPP_EAE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_DESUPP_EAE(HttpServletRequest request, EaeDto eaeSelection) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// on met à  jour le statut de l'EAE
		eaeSelection.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());

		ReturnMessageDto result = eaeService.setEae(getAgentConnecte(request).getIdAgent(), eaeSelection);

		if (!result.getErrors().isEmpty()) {
			getTransaction().declarerErreur(result.getErrors().get(0).toString());
		}

		performPB_FILTRER(request);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT_EVALUATEUR
	 * Date de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT_EVALUATEUR() {
		return "NOM_ST_AGENT_EVALUATEUR";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone :
	 * ST_AGENT_EVALUATEUR Date de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT_EVALUATEUR() {
		return getZone(getNOM_ST_AGENT_EVALUATEUR());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT_EVALUATEUR
	 * Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT_EVALUATEUR() {
		return "NOM_PB_RECHERCHER_AGENT_EVALUATEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT_EVALUATEUR(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT_EVALUATEUR, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR Date de création : (13/07/11
	 * 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_EVALUATEUR(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT_EVALUE Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT_EVALUE() {
		return "NOM_ST_AGENT_EVALUE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_AGENT_EVALUE
	 * Date de création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT_EVALUE() {
		return getZone(getNOM_ST_AGENT_EVALUE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT_EVALUE Date
	 * de création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT_EVALUE() {
		return "NOM_PB_RECHERCHER_AGENT_EVALUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT_EVALUE(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		setStatut(STATUT_RECHERCHER_AGENT_EVALUE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE Date de création : (13/07/11
	 * 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_EVALUE(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT_EVALUE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom de la case à  cocher sélectionnée pour la JSP :
	 * CK_VALID_MAJ Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_MAJ(int i) {
		return "NOM_CK_VALID_MAJ_" + i;
	}

	/**
	 * Retourne la valeur de la case à  cocher à  afficher par la JSP pour la
	 * case à  cocher : CK_VALID_MAJ Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_MAJ(int i) {
		return getZone(getNOM_CK_VALID_MAJ(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_METTRE_A_JOUR_EAE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_METTRE_A_JOUR_EAE() {
		return "NOM_PB_METTRE_A_JOUR_EAE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_METTRE_A_JOUR_EAE(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// on recupere les lignes qui sont cochées pour mettre à  jour
		int nbEae = 0;
		for (int j = 0; j < getListeEAE().size(); j++) {
			// on recupère la ligne concernée
			EaeDto eae = (EaeDto) getListeEAE().get(j);
			Integer i = eae.getIdEae();
			// si la colonne mettre à  jour est cochée
			if (getVAL_CK_VALID_MAJ(i).equals(getCHECKED_ON())) {
				setEaeCourant(eae);
				perform_METTRE_A_JOUR_EAE(request, eae);
				nbEae++;
			}

		}

		// pour reinitiliser l'affichage du tableau.
		performPB_FILTRER(request);

		// "INF203", "@ EAE(s) ont été mis à  jour.");
		setMessage(MessageUtils.getMessage("INF203", String.valueOf(nbEae)));
		return true;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String[] getLB_AFFECTE() {
		if (LB_AFFECTE == null)
			LB_AFFECTE = initialiseLazyLB();
		return LB_AFFECTE;
	}

	private void setLB_AFFECTE(String[] newLB_AFFECTE) {
		LB_AFFECTE = newLB_AFFECTE;
	}

	public String getNOM_LB_AFFECTE() {
		return "NOM_LB_AFFECTE";
	}

	public String getNOM_LB_AFFECTE_SELECT() {
		return "NOM_LB_AFFECTE_SELECT";
	}

	public String[] getVAL_LB_AFFECTE() {
		return getLB_AFFECTE();
	}

	public String getVAL_LB_AFFECTE_SELECT() {
		return getZone(getNOM_LB_AFFECTE_SELECT());
	}

	public List<String> getListeAffecte() {
		return listeAffecte == null ? new ArrayList<String>() : listeAffecte;
	}

	public void setListeAffecte(List<String> listeAffecte) {
		this.listeAffecte = listeAffecte;
	}

	public MotifAvancementDao getMotifAvancementDao() {
		return motifAvancementDao;
	}

	public void setMotifAvancementDao(MotifAvancementDao motifAvancementDao) {
		this.motifAvancementDao = motifAvancementDao;
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

	public AutreAdministrationDao getAutreAdministrationDao() {
		return autreAdministrationDao;
	}

	public void setAutreAdministrationDao(AutreAdministrationDao autreAdministrationDao) {
		this.autreAdministrationDao = autreAdministrationDao;
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

	public AvancementFonctionnairesDao getAvancementFonctionnairesDao() {
		return avancementFonctionnairesDao;
	}

	public void setAvancementFonctionnairesDao(AvancementFonctionnairesDao avancementFonctionnairesDao) {
		this.avancementFonctionnairesDao = avancementFonctionnairesDao;
	}

	public TitrePosteDao getTitrePosteDao() {
		return titrePosteDao;
	}

	public void setTitrePosteDao(TitrePosteDao titrePosteDao) {
		this.titrePosteDao = titrePosteDao;
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

	public ActiviteDao getActiviteDao() {
		return activiteDao;
	}

	public void setActiviteDao(ActiviteDao activiteDao) {
		this.activiteDao = activiteDao;
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

	public String getNOM_ST_URL_DOC(int i) {
		return "NOM_ST_URL_DOC" + i;
	}

	public String getVAL_ST_URL_DOC(int i) {
		return getZone(getNOM_ST_URL_DOC(i));
	}

	public String getNOM_ST_MATRICULE_AGENT(int i) {
		return "NOM_ST_MATRICULE_AGENT_" + i;
	}

	public String getVAL_ST_MATRICULE_AGENT(int i) {
		return getZone(getNOM_ST_MATRICULE_AGENT(i));
	}
}