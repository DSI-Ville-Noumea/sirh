package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.connecteur.metier.Spmtsr;
import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.gestionagent.dto.DateAvctDto;
import nc.mairie.gestionagent.dto.KiosqueDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.agent.SISERV;
import nc.mairie.metier.avancement.AvancementDetaches;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Classe;
import nc.mairie.metier.carriere.Echelon;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.carriere.StatutCarriere;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.diplome.FormationAgent;
import nc.mairie.metier.eae.CampagneEAE;
import nc.mairie.metier.eae.EAE;
import nc.mairie.metier.eae.EaeCampagneTask;
import nc.mairie.metier.eae.EaeDiplome;
import nc.mairie.metier.eae.EaeEvaluateur;
import nc.mairie.metier.eae.EaeEvaluation;
import nc.mairie.metier.eae.EaeEvalue;
import nc.mairie.metier.eae.EaeFDPActivite;
import nc.mairie.metier.eae.EaeFDPCompetence;
import nc.mairie.metier.eae.EaeFichePoste;
import nc.mairie.metier.eae.EaeFormation;
import nc.mairie.metier.eae.EaeParcoursPro;
import nc.mairie.metier.parametrage.CentreFormation;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.parametrage.SpecialiteDiplome;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TitreFormation;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.ActiviteFP;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFP;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FEFP;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.AutreAdministration;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeCampagneTaskDao;
import nc.mairie.spring.dao.metier.EAE.EaeDiplomeDao;
import nc.mairie.spring.dao.metier.EAE.EaeEAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.dao.metier.EAE.EaeFDPActiviteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFDPCompetenceDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFinalisationDao;
import nc.mairie.spring.dao.metier.EAE.EaeFormationDao;
import nc.mairie.spring.dao.metier.EAE.EaeParcoursProDao;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.AutreAdministrationAgentDao;
import nc.mairie.spring.dao.metier.agent.SISERVDao;
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
import nc.mairie.spring.dao.utils.EaeDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.BaseWsConsumerException;
import nc.mairie.spring.ws.SirhKiosqueWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IAdsService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.ISirhService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTCampagneGestionEAE extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_EVALUATEUR = 1;
	public static final int STATUT_RECHERCHER_AGENT = 2;
	public static final int STATUT_RECHERCHER_AGENT_EVALUATEUR = 3;
	public static final int STATUT_RECHERCHER_AGENT_EVALUE = 4;

	private String[] LB_ANNEE;
	private String[] LB_ETAT;
	private String[] LB_STATUT;
	private String[] LB_CAP;
	private String[] LB_AFFECTE;
	private ArrayList<CampagneEAE> listeCampagneEAE;
	private ArrayList<EnumEtatEAE> listeEnumEtatEAE;
	private ArrayList<String> listeStatut;
	private ArrayList<String> listeCAP;
	private ArrayList<String> listeAffecte;
	private ArrayList<EaeCampagneTask> listeCampagneTask;

	private CampagneEAE campagneCourante;

	private ArrayList<EAE> listeEAE;
	private EAE eaeCourant;

	private Integer idCreerFichePosteSecondaire;
	private Integer idCreerFichePostePrimaire;

	private Logger logger = LoggerFactory.getLogger(OeAVCTCampagneGestionEAE.class);

	private EaeEvaluationDao eaeEvaluationDao;
	private EaeFormationDao eaeFormationDao;
	private TitreFormationDao titreFormationDao;
	private CentreFormationDao centreFormationDao;
	private FormationAgentDao formationAgentDao;
	private EaeParcoursProDao eaeParcoursProDao;
	private EaeDiplomeDao eaeDiplomeDao;
	private EaeFichePosteDao eaeFichePosteDao;
	private EaeFDPActiviteDao eaeFDPActiviteDao;
	private EaeEAEDao eaeDao;
	private EaeEvalueDao eaeEvalueDao;
	private EaeEvaluateurDao eaeEvaluateurDao;
	private CampagneEAEDao campagneEAEDao;
	private EaeFDPCompetenceDao eaeFDPCompetenceDao;
	private EaeFinalisationDao eaeFinalisationDao;
	private EaeCampagneTaskDao eaeCampagneTaskDao;
	private MotifAvancementDao motifAvancementDao;
	private SpecialiteDiplomeDao specialiteDiplomeDao;
	private TitreDiplomeDao titreDiplomeDao;

	private String message;
	private String urlFichier;

	private AutreAdministrationDao autreAdministrationDao;
	private TypeCompetenceDao typeCompetenceDao;
	private DiplomeAgentDao diplomeAgentDao;
	private AutreAdministrationAgentDao autreAdministrationAgentDao;
	private AvancementDetachesDao avancementDetachesDao;
	private AvancementFonctionnairesDao avancementFonctionnairesDao;
	private TitrePosteDao titrePosteDao;
	private FEFPDao fefpDao;
	private CompetenceDao competenceDao;
	private CompetenceFPDao competenceFPDao;
	private ActiviteDao activiteDao;
	private ActiviteFPDao activiteFPDao;
	private FicheEmploiDao ficheEmploiDao;
	private FichePosteDao fichePosteDao;
	private AffectationDao affectationDao;
	private AgentDao agentDao;
	private SISERVDao siservDao;

	private IAdsService adsService;

	private IRadiService radiService;

	private IAbsService absService;

	private ISirhService sirhService;

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

		// Initialisation des listes deroulantes
		initialiseListeDeroulante();

		if (etatStatut() == STATUT_EVALUATEUR) {
			initialiseEvaluateur();
			// on revoit l'affichage car il y a peut etre un changement de
			// statut
			performPB_FILTRER(request);
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			initialiseDelegataire();
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

		// initialisation de l'affichage du resultat des calculs EAEs de
		// SIRH-JOBS
		initialiseAffichageListeCampagneTask(request);

		if (getMessage() != null && !getMessage().equals(Const.CHAINE_VIDE)) {
			setStatut(STATUT_MEME_PROCESS, false, getMessage());
			setMessage(null);
		}
	}

	private void initialiseDelegataire() throws Exception {
		Agent agt = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (getEaeCourant() != null && agt != null) {
			getEaeCourant().setIdDelegataire(agt.getIdAgent());
			getEaeDao().modifierDelegataire(getEaeCourant().getIdEae(), getEaeCourant().getIdDelegataire());
			// si EAE d'un détachés alors on passe le statut de l'EAE en
			// "on debute"
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEae());
			if (evalue.isAgentDetache()) {
				getEaeCourant().setEtat(EnumEtatEAE.NON_DEBUTE.getCode());
				getEaeDao().modifierEtat(getEaeCourant().getIdEae(), getEaeCourant().getEtat());
			}

		}

	}

	private void initialiseEvaluateur() throws Exception {
		@SuppressWarnings("unchecked")
		ArrayList<Agent> listeEvaluateurSelect = (ArrayList<Agent>) VariablesActivite.recuperer(this, "EVALUATEURS");
		VariablesActivite.enlever(this, "EVALUATEURS");
		if (getEaeCourant() != null) {
			// on supprime tous les evaluateurs existants
			ArrayList<EaeEvaluateur> evaluateursExistants = getEaeEvaluateurDao().listerEvaluateurEAE(getEaeCourant().getIdEae());
			for (int i = 0; i < evaluateursExistants.size(); i++) {
				EaeEvaluateur eval = evaluateursExistants.get(i);
				getEaeEvaluateurDao().supprimerEaeEvaluateur(eval.getIdEaeEvaluateur());
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (listeEvaluateurSelect != null && listeEvaluateurSelect.size() > 0) {
				for (int j = 0; j < listeEvaluateurSelect.size(); j++) {
					Agent agentEvaluateur = listeEvaluateurSelect.get(j);
					if (agentEvaluateur != null) {
						// on crée les nouveaux evaluateurs
						EaeEvaluateur eval = new EaeEvaluateur();
						eval.setIdEae(getEaeCourant().getIdEae());
						eval.setIdAgent(agentEvaluateur.getIdAgent());

						// on recupere le poste
						Affectation aff = null;
						try {
							aff = getAffectationDao().chercherAffectationActiveAvecAgent(agentEvaluateur.getIdAgent());
						} catch (Exception e) {

						}

						if (aff != null && aff.getIdFichePoste() != null) {
							FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
							TitrePoste tp = getTitrePosteDao().chercherTitrePoste(fp.getIdTitrePoste());
							eval.setFonction(tp.getLibTitrePoste());
							// on cherche toutes les affectations sur la FDP
							// on prend la date la plus ancienne
							ArrayList<Affectation> listeAffectationSurMemeFDP = getAffectationDao().listerAffectationAvecFPEtAgent(fp.getIdFichePoste(), agentEvaluateur.getIdAgent());
							if (listeAffectationSurMemeFDP.size() > 0) {
								eval.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff());
							}
							// on cherche toutes les affectations sur le meme
							// service et
							// on prend la date la plus ancienne
							// NB : pour les affectations successives
							ArrayList<Affectation> listeAffectationService = getAffectationDao().listerAffectationAgentAvecService(agentEvaluateur.getIdAgent(), fp.getIdServiceAds());
							Date dateDebutService = null;
							for (int i = 0; i < listeAffectationService.size(); i++) {
								Affectation affCours = listeAffectationService.get(i);
								if (listeAffectationService.size() > i + 1) {
									if (listeAffectationService.get(i + 1) != null) {
										Affectation affPrecedente = listeAffectationService.get(i + 1);
										if (sdf.format(affCours.getDateDebutAff()).equals(Services.ajouteJours(sdf.format(affPrecedente.getDateFinAff()), 1))) {
											dateDebutService = affPrecedente.getDateDebutAff();
										} else {
											dateDebutService = affCours.getDateDebutAff();
										}
									} else {
										dateDebutService = affCours.getDateDebutAff();
									}
								} else {
									dateDebutService = affCours.getDateDebutAff();
								}
							}
							eval.setDateEntreeService(dateDebutService);
						}

						eval.setDateEntreeCollectivite(agentEvaluateur.getDateDerniereEmbauche());

						getEaeEvaluateurDao().creerEaeEvaluateur(eval.getIdEae(), eval.getIdAgent(), eval.getFonction(), eval.getDateEntreeService(), eval.getDateEntreeCollectivite(),
								eval.getDateEntreeFonction());
					}
				}
				// on sort de la boucle, on change l'etat de l'EAE en NON-DEBUTE
				getEaeCourant().setEtat(EnumEtatEAE.NON_DEBUTE.getCode());
				getEaeDao().modifierEtat(getEaeCourant().getIdEae(), getEaeCourant().getEtat());

			} else {
				if (getEaeCourant() != null) {
					// si pas d'evaluateur choisi alors on passe l'eae en NON
					// AFFECTE et on supprime le delegataire
					getEaeDao().modifierDelegataire(getEaeCourant().getIdEae(), null);
					getEaeCourant().setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
					getEaeDao().modifierEtat(getEaeCourant().getIdEae(), getEaeCourant().getEtat());

				}
			}
		}
	}

	private void initialiseAffichageListeEAE(HttpServletRequest request) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		for (int p = 0; p < getListeEAE().size(); p++) {
			EAE eae = (EAE) getListeEAE().get(p);
			Integer i = eae.getIdEae();
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEae());
			try {
				EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEae(), true);

				// Service
				EntiteDto direction = adsService.getAffichageDirection(eaeFDP.getIdServiceAds());
				EntiteDto service = adsService.getEntiteByIdEntite(eaeFDP.getIdServiceAds());
				EntiteDto section = adsService.getAffichageSection(eaeFDP.getIdServiceAds());

				addZone(getNOM_ST_DIRECTION(i), (direction == null ? "&nbsp;" : direction.getLabel()) + " <br> " + (section == null ? "&nbsp;" : section.getLabel()) + " <br> "
						+ (service == null ? "&nbsp;" : service.getLabel()));
				if (eaeFDP.getIdShd() != null) {
					try {
						Agent agentResp = getAgentDao().chercherAgent(eaeFDP.getIdShd());
						addZone(getNOM_ST_SHD(i), agentResp.getNomAgent() + " " + agentResp.getPrenomAgent());
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
			EaeEvaluation evaluation = null;
			try {
				evaluation = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEae());
			} catch (Exception e) {
				// on ne fait rien
			}
			Agent agentEAE = getAgentDao().chercherAgent(evalue.getIdAgent());
			addZone(getNOM_ST_MATRICULE_AGENT(i), agentEAE.getNomatr().toString());
			addZone(getNOM_ST_AGENT(i), agentEAE.getNomAgent() + " " + agentEAE.getPrenomAgent());
			addZone(getNOM_ST_STATUT(i), (evalue.getStatut() == null ? "&nbsp;" : evalue.getStatut()) + " <br> " + (evalue.isAgentDetache() ? "oui" : "&nbsp;"));
			// on recupere les evaluateurs
			ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(eae.getIdEae());
			String eval = Const.CHAINE_VIDE;
			for (int j = 0; j < listeEvaluateur.size(); j++) {
				EaeEvaluateur evaluateur = listeEvaluateur.get(j);
				Agent agentevaluateur = getAgentDao().chercherAgent(evaluateur.getIdAgent());
				eval += agentevaluateur.getNomAgent() + " " + agentevaluateur.getPrenomAgent() + "<br> ";
			}
			addZone(getNOM_ST_EVALUATEURS(i), eval.equals(Const.CHAINE_VIDE) ? "&nbsp;" : eval);
			if (eae.getIdDelegataire() != null) {
				Agent agentDelegataire = getAgentDao().chercherAgent(eae.getIdDelegataire());
				addZone(getNOM_ST_DELEGATAIRE(i), agentDelegataire.getNomAgent() + " " + agentDelegataire.getPrenomAgent());
			} else {
				addZone(getNOM_ST_DELEGATAIRE(i), "&nbsp;");
			}

			addZone(getNOM_ST_CAP(i), eae.isCap() ? "oui" : "&nbsp;");
			addZone(getNOM_ST_AVIS_SHD(i), evaluation == null || evaluation.getAvisShd() == null ? "&nbsp;" : evaluation.getAvisShd());
			addZone(getNOM_ST_EAE_JOINT(i), eae.isDocAttache() ? "oui" : "non");
			addZone(getNOM_ST_CONTROLE(i),
					EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()) + " <br> " + (eae.getDateCreation() == null ? "&nbsp;" : sdf.format(eae.getDateCreation())) + " <br> "
							+ (eae.getDateFinalise() == null ? "&nbsp;" : sdf.format(eae.getDateFinalise())) + " <br> "
							+ (eae.getDateControle() == null ? "&nbsp;" : sdf.format(eae.getDateControle())));
			addZone(getNOM_ST_ACTIONS_DEFINALISE(i), "&nbsp;");
			addZone(getNOM_ST_ACTIONS_MAJ(i), "&nbsp;");
			addZone(getNOM_CK_VALID_MAJ(i), getCHECKED_OFF());
			addZone(getNOM_CK_VALID_EAE(i), eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) ? getCHECKED_ON() : getCHECKED_OFF());
			addZone(getNOM_ST_CONTROLE_PAR(i), eae.getUserControle() == null ? "&nbsp;" : eae.getUserControle());

		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getCampagneEAEDao() == null) {
			setCampagneEAEDao(new CampagneEAEDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeDao() == null) {
			setEaeDao(new EaeEAEDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeEvalueDao() == null) {
			setEaeEvalueDao(new EaeEvalueDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeEvaluateurDao() == null) {
			setEaeEvaluateurDao(new EaeEvaluateurDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeFDPActiviteDao() == null) {
			setEaeFDPActiviteDao(new EaeFDPActiviteDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeFDPCompetenceDao() == null) {
			setEaeFDPCompetenceDao(new EaeFDPCompetenceDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeFichePosteDao() == null) {
			setEaeFichePosteDao(new EaeFichePosteDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeDiplomeDao() == null) {
			setEaeDiplomeDao(new EaeDiplomeDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeParcoursProDao() == null) {
			setEaeParcoursProDao(new EaeParcoursProDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeFormationDao() == null) {
			setEaeFormationDao(new EaeFormationDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao(new EaeEvaluationDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeFinalisationDao() == null) {
			setEaeFinalisationDao(new EaeFinalisationDao((EaeDao) context.getBean("eaeDao")));
		}

		if (getEaeCampagneTaskDao() == null) {
			setEaeCampagneTaskDao(new EaeCampagneTaskDao((EaeDao) context.getBean("eaeDao")));
		}

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
		if (getSiservDao() == null) {
			setSiservDao(new SISERVDao((MairieDao) context.getBean("mairieDao")));
		}

	}

	/**
	 * Initialisation des liste deroulantes.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			ArrayList<CampagneEAE> listeCamp = getCampagneEAEDao().listerCampagneEAE();
			setListeCampagneEAE(listeCamp);
			int[] tailles = { 5 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<CampagneEAE> list = listeCamp.listIterator(); list.hasNext();) {
				CampagneEAE camp = (CampagneEAE) list.next();
				String ligne[] = { camp.getAnnee().toString() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_ANNEE(aFormat.getListeFormatee(false));
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
		}
		// Si liste etat vide alors affectation
		if (getLB_ETAT() == LBVide) {
			ArrayList<EnumEtatEAE> listeEtat = EnumEtatEAE.getValues();
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
			ArrayList<String> listeAffecte = new ArrayList<String>();
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
			ArrayList<String> listeStatut = new ArrayList<String>();
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
			ArrayList<String> listeCAP = new ArrayList<String>();
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
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
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
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_GERER_EVALUATEUR(eae.getIdEae()))) {
					return performPB_GERER_EVALUATEUR(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT(eae.getIdEae()))) {
					return performPB_RECHERCHER_AGENT(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
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
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_VALID_EAE(eae.getIdEae()))) {
					return performPB_VALID_EAE(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_DEVALID_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
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
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DEFINALISE_EAE(eae.getIdEae()))) {
					return performPB_DEFINALISE_EAE(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_SUPP_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_SUPP_EAE(eae.getIdEae()))) {
					return performPB_SUPP_EAE(request, eae.getIdEae());
				}
			}

			// Si clic sur le bouton PB_DESUPP_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DESUPP_EAE(eae.getIdEae()))) {
					return performPB_DESUPP_EAE(request, eae.getIdEae());
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

			// Si clic sur le bouton PB_CONSULTER_DOC
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_CONSULTER_DOC(eae.getIdEae()))) {
					return performPB_CONSULTER_DOC(request, eae.getIdEae());
				}
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
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_GERER_EVALUATEUR(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		ArrayList<Agent> listeEval = new ArrayList<Agent>();

		setEaeCourant(getEaeDao().chercherEAE(idEae));
		ArrayList<EaeEvaluateur> listeEvalEAE = getEaeEvaluateurDao().listerEvaluateurEAE(eaeCourant.getIdEae());

		if (listeEvalEAE != null) {
			for (int i = 0; i < listeEvalEAE.size(); i++) {
				EaeEvaluateur eval = listeEvalEAE.get(i);
				Agent ag = getAgentDao().chercherAgent(eval.getIdAgent());
				listeEval.add(ag);
			}
		}
		VariablesActivite.ajouter(this, "LISTEEVALUATEUR", listeEval);
		setStatut(STATUT_EVALUATEUR, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_FILTRER Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		if (!performControlerFiltres()) {
			return false;
		}
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		setCampagneCourante((CampagneEAE) getListeCampagneEAE().get(indiceCampagne));

		// Recherche des eae de la campagne en fonction de l'etat
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
		ArrayList<EAE> listeEAE = getEaeDao().listerEAEPourCampagne(getCampagneCourante().getIdCampagneEae(), etat, statut, listeSousService, cap, agentEvaluateur, agentEvalue, affecte);
		setListeEAE(listeEAE);
		return true;
	}

	private boolean performControlerFiltres() {
		if (!getVAL_ST_AGENT_EVALUATEUR().equals(Const.CHAINE_VIDE)) {
			try {
				@SuppressWarnings("unused")
				Agent agent = getAgentDao().chercherAgent(Integer.valueOf("900" + getVAL_ST_AGENT_EVALUATEUR()));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", getVAL_ST_AGENT_EVALUATEUR()));
				return false;
			}
		}

		if (!getVAL_ST_AGENT_EVALUE().equals(Const.CHAINE_VIDE)) {
			try {
				@SuppressWarnings("unused")
				Agent agent = getAgentDao().chercherAgent(Integer.valueOf("900" + getVAL_ST_AGENT_EVALUE()));
			} catch (Exception e) {
				// "ERR503",
				// "L'agent @ n'existe pas. Merci de saisir un matricule existant."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR503", getVAL_ST_AGENT_EVALUE()));
				return false;
			}
		}
		return true;
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
			setCampagneCourante((CampagneEAE) getListeCampagneEAE().get(indiceCampagne));

			if (!initialiseListeEAEWithJob(request)) {
				return false;
			}

			// "INF202","Calcul effectue."
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
					EaeCampagneTask eaeCampagneTask = getEaeCampagneTaskDao().chercherEaeCampagneTaskByIdCampagneEae(getCampagneCourante().getIdCampagneEae());

					if (null != eaeCampagneTask) {
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR216"));
						return false;
					} else {
						getEaeCampagneTaskDao().creerEaeCampagneTask(getCampagneCourante().getIdCampagneEae(), getCampagneCourante().getAnnee(), getAgentConnecte(request).getIdAgent(), null, null);
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (05/09/11 11:39:24)
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ANNEE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (28/11/11)
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
	 * Retourne pour la JSP le nom de la zone statique : ST_STATUT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_STATUT Date de
	 * création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SHD Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EVALUATEURS
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DELEGATAIRE
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CAP Date de
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AVIS_SHD Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_EAE_JOINT Date
	 * de création : (21/11/11 09:55:36)
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CONTROLE Date
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
	 * Retourne la valeur à afficher par la JSP pour la zone :
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTIONS_MAJ
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CONTROLE_PAR
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CONTROLE_PAR(int i) {
		return getZone(getNOM_ST_CONTROLE_PAR(i));
	}

	public ArrayList<EAE> getListeEAE() {
		if (listeEAE == null)
			return new ArrayList<EAE>();
		return listeEAE;
	}

	public void setListeEAE(ArrayList<EAE> listeEAE) {
		this.listeEAE = listeEAE;
	}

	private ArrayList<CampagneEAE> getListeCampagneEAE() {
		if (listeCampagneEAE == null)
			return new ArrayList<CampagneEAE>();
		return listeCampagneEAE;
	}

	private void setListeCampagneEAE(ArrayList<CampagneEAE> listeCampagneEAE) {
		this.listeCampagneEAE = listeCampagneEAE;
	}

	public CampagneEAE getCampagneCourante() {
		return campagneCourante;
	}

	public void setCampagneCourante(CampagneEAE campagneCourante) {
		this.campagneCourante = campagneCourante;
	}

	public EAE getEaeCourant() {
		return eaeCourant;
	}

	public void setEaeCourant(EAE eaeCourant) {
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new Agent());

		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
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
		// On enleve l'agent selectionnée
		EAE eaeSelection = getEaeDao().chercherEAE(idEae);
		eaeSelection.setIdDelegataire(null);
		getEaeDao().modifierDelegataire(eaeSelection.getIdEae(), eaeSelection.getIdDelegataire());
		// si EAE d'un détaché alors on repasse le statut a "NA"
		EaeEvalue eval = getEaeEvalueDao().chercherEaeEvalue(eaeSelection.getIdEae());
		if (eval.isAgentDetache()) {
			eaeSelection.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
			getEaeDao().modifierEtat(eaeSelection.getIdEae(), eaeSelection.getEtat());
		}
		// on réinitilise l'affichage
		performPB_FILTRER(request);
		return true;
	}

	private void performCreerFormation(HttpServletRequest request, Agent ag) throws Exception {
		ArrayList<FormationAgent> listFormationAgent = getFormationAgentDao().listerFormationAgentByAnnee(ag.getIdAgent(), getCampagneCourante().getAnnee() - 1);
		for (int i = 0; i < listFormationAgent.size(); i++) {
			FormationAgent formation = listFormationAgent.get(i);
			TitreFormation titre = getTitreFormationDao().chercherTitreFormation(formation.getIdTitreFormation());
			CentreFormation centre = getCentreFormationDao().chercherCentreFormation(formation.getIdCentreFormation());
			EaeFormation form = new EaeFormation();
			form.setIdEAE(getEaeCourant().getIdEae());
			form.setAnneeFormation(formation.getAnneeFormation());
			form.setDureeFormation(formation.getDureeFormation().toString() + " " + formation.getUniteDuree());
			form.setLibelleFormation(titre.getLibTitreFormation() + " - " + centre.getLibCentreFormation());
			getEaeFormationDao().creerEaeFormation(form.getIdEAE(), form.getAnneeFormation(), form.getDureeFormation(), form.getLibelleFormation());
		}
	}

	private void performCreerCompetencesFichePosteSecondaire(HttpServletRequest request, FichePoste fpSecondaire) throws Exception {
		if (fpSecondaire != null) {
			// Recherche de tous les liens FichePoste / Competence
			ArrayList<CompetenceFP> liens = getCompetenceFPDao().listerCompetenceFPAvecFP(fpSecondaire.getIdFichePoste());
			ArrayList<Competence> listCompFDP = getCompetenceDao().listerCompetenceAvecFP(liens);
			for (int i = 0; i < listCompFDP.size(); i++) {
				Competence comp = listCompFDP.get(i);
				TypeCompetence typComp = getTypeCompetenceDao().chercherTypeCompetence(comp.getIdTypeCompetence());
				EaeFDPCompetence compEAE = new EaeFDPCompetence();
				compEAE.setIdEaeFichePoste(getIdCreerFichePosteSecondaire());
				compEAE.setTypeCompetence(EnumTypeCompetence.getValueEnumTypeCompetence(typComp.getIdTypeCompetence()));
				compEAE.setLibelleCompetence(comp.getNomCompetence());

				getEaeFDPCompetenceDao().creerEaeFDPCompetence(compEAE.getIdEaeFichePoste(), compEAE.getTypeCompetence(), compEAE.getLibelleCompetence());
			}
		}
	}

	private void performCreerCompetencesFichePostePrincipale(HttpServletRequest request, FichePoste fpPrincipale) throws Exception {
		if (fpPrincipale != null) {
			// Recherche de tous les liens FichePoste / Competence
			ArrayList<CompetenceFP> liens = getCompetenceFPDao().listerCompetenceFPAvecFP(fpPrincipale.getIdFichePoste());
			ArrayList<Competence> listCompFDP = getCompetenceDao().listerCompetenceAvecFP(liens);
			for (int i = 0; i < listCompFDP.size(); i++) {
				Competence comp = listCompFDP.get(i);
				TypeCompetence typComp = getTypeCompetenceDao().chercherTypeCompetence(comp.getIdTypeCompetence());
				EaeFDPCompetence compEAE = new EaeFDPCompetence();
				compEAE.setIdEaeFichePoste(getIdCreerFichePostePrimaire());
				compEAE.setTypeCompetence(EnumTypeCompetence.getValueEnumTypeCompetence(typComp.getIdTypeCompetence()));
				compEAE.setLibelleCompetence(comp.getNomCompetence());

				getEaeFDPCompetenceDao().creerEaeFDPCompetence(compEAE.getIdEaeFichePoste(), compEAE.getTypeCompetence(), compEAE.getLibelleCompetence());
			}
		}
	}

	private void performCreerActivitesFichePostePrincipale(HttpServletRequest request, FichePoste fpPrincipale) throws Exception {
		if (fpPrincipale != null) {
			// gere les activites
			// Recherche de tous les liens FicheEmploi / Activite
			ArrayList<ActiviteFP> liens = getActiviteFPDao().listerActiviteFPAvecFP(fpPrincipale.getIdFichePoste());
			ArrayList<Activite> listActFDP = getActiviteDao().listerActiviteAvecFP(liens);
			for (int i = 0; i < listActFDP.size(); i++) {
				Activite act = listActFDP.get(i);
				EaeFDPActivite acti = new EaeFDPActivite();
				acti.setIdEaeFichePoste(getIdCreerFichePostePrimaire());
				acti.setLibelleActivite(act.getNomActivite());

				getEaeFDPActiviteDao().creerEaeFDPActivite(acti.getIdEaeFichePoste(), acti.getLibelleActivite());
			}
		}
	}

	private void performCreerActivitesFichePosteSecondaire(HttpServletRequest request, FichePoste fpSecondaire) throws Exception {
		// gere les activites
		if (fpSecondaire != null) {
			// Recherche de tous les liens FicheEmploi / Activite
			ArrayList<ActiviteFP> liens = getActiviteFPDao().listerActiviteFPAvecFP(fpSecondaire.getIdFichePoste());
			ArrayList<Activite> listActFDP = getActiviteDao().listerActiviteAvecFP(liens);
			for (int i = 0; i < listActFDP.size(); i++) {
				Activite act = listActFDP.get(i);
				EaeFDPActivite acti = new EaeFDPActivite();
				acti.setIdEaeFichePoste(getIdCreerFichePosteSecondaire());
				acti.setLibelleActivite(act.getNomActivite());

				getEaeFDPActiviteDao().creerEaeFDPActivite(acti.getIdEaeFichePoste(), acti.getLibelleActivite());
			}
		}
	}

	private void performCreerFichePosteSecondaire(HttpServletRequest request, FichePoste fpSecondaire, EAE eae) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// on traite la secondaire
		if (fpSecondaire != null) {
			// on supprime les lignes existantes si elles existent
			try {
				EaeFichePoste aSupp = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEae(), false);
				// on recupere les activites/competences liees
				try {
					ArrayList<EaeFDPActivite> listActi = getEaeFDPActiviteDao().listerEaeFDPActivite(aSupp.getIdEaeFichePoste());
					for (int i = 0; i < listActi.size(); i++) {
						EaeFDPActivite a = listActi.get(i);
						getEaeFDPActiviteDao().supprimerEaeFDPActivite(a.getIdEaeFdpActivite());
					}
					ArrayList<EaeFDPCompetence> listComp = getEaeFDPCompetenceDao().listerEaeFDPCompetence(aSupp.getIdEaeFichePoste());
					for (int i = 0; i < listComp.size(); i++) {
						EaeFDPCompetence c = listComp.get(i);
						getEaeFDPCompetenceDao().supprimerEaeFDPCompetence(c.getIdEaeFdpCompetence());
					}
				} catch (Exception e) {
					// il n'y avait pas d'activites
				}
				getEaeFichePosteDao().supprimerEaeFichePoste(aSupp.getIdEaeFichePoste());
			} catch (Exception e) {
				// on ne fait rien
			}
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEae());
			EaeFichePoste fichePosteEae = new EaeFichePoste();
			fichePosteEae.setIdEae(eae.getIdEae());
			fichePosteEae.setIdSirhFichePoste(fpSecondaire.getIdFichePoste());
			if (fpSecondaire.getIdResponsable() != null) {
				Agent agentResp = null;
				try {
					agentResp = getAgentDao().chercherAgentAffecteFichePoste(fpSecondaire.getIdResponsable());
				} catch (Exception e) {

				}
				fichePosteEae.setIdShd(agentResp == null || agentResp.getIdAgent() == null ? null : agentResp.getIdAgent());
			}
			fichePosteEae.setPrimaire(false);
			fichePosteEae.setIdServiceAds(fpSecondaire.getIdServiceAds());
			fichePosteEae.setCodeService(fpSecondaire.getIdServi());

			EntiteDto direction = adsService.getAffichageDirection(fpSecondaire.getIdServiceAds());
			fichePosteEae.setDirectionService(direction != null ? direction.getLabel() : null);
			EntiteDto service = adsService.getEntiteByIdEntite(fpSecondaire.getIdServiceAds());
			fichePosteEae.setService(service != null ? service.getLabel() : null);
			EntiteDto section = adsService.getAffichageSection(fpSecondaire.getIdServiceAds());
			fichePosteEae.setSectionService(section != null ? section.getLabel() : null);
			// pour l'emploi

			// Recherche de tous les liens FicheEmploi / FichePoste
			ArrayList<FEFP> liens = getFefpDao().listerFEFPAvecFP(fpSecondaire.getIdFichePoste());
			try {
				FicheEmploi fe = getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(false, liens);
				if (fe != null) {
					fichePosteEae.setEmploi(fe.getNomMetierEmploi());
				}
			} catch (Exception e) {

			}
			// pour la fonction
			try {
				TitrePoste tp = getTitrePosteDao().chercherTitrePoste(fpSecondaire.getIdTitrePoste());
				fichePosteEae.setFonction(tp.getLibTitrePoste());
			} catch (Exception e) {

			}
			// on cherche toutes les affectations sur la FDP
			// on prend la date la plus ancienne
			ArrayList<Affectation> listeAffectationSurMemeFDP = getAffectationDao().listerAffectationAvecFPEtAgent(fpSecondaire.getIdFichePoste(), evalue.getIdAgent());
			if (listeAffectationSurMemeFDP.size() > 0) {
				fichePosteEae.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff());
			}
			// grade du poste
			Grade g = Grade.chercherGrade(getTransaction(), fpSecondaire.getCodeGrade());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				fichePosteEae.setGradePoste(g.getGrade());
			}
			EntiteGeo lieu = EntiteGeo.chercherEntiteGeo(getTransaction(), fpSecondaire.getIdEntiteGeo().toString());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				fichePosteEae.setLocalisation(lieu.getLibEntiteGeo());
			}
			fichePosteEae.setMissions(fpSecondaire.getMissions());
			if (fpSecondaire.getIdResponsable() != null) {
				try {
					FichePoste fpResp = getFichePosteDao().chercherFichePoste(fpSecondaire.getIdResponsable());

					try {
						TitrePoste tpResp = getTitrePosteDao().chercherTitrePoste(fpResp.getIdTitrePoste());
						fichePosteEae.setFonctionResp(tpResp.getLibTitrePoste());
					} catch (Exception e) {

					}
					try {
						Affectation affResp = getAffectationDao().chercherAffectationActiveAvecFP(fpResp.getIdFichePoste());
						if (affResp != null && affResp.getIdAgent() != null) {
							try {
								Agent agentResp = getAgentDao().chercherAgent(affResp.getIdAgent());
								fichePosteEae.setDateEntreeCollectResp(agentResp.getDateDerniereEmbauche());

								// on cherche toutes les affectations sur la FDP
								// du
								// responsable
								// on prend la date la plus ancienne
								if (fpResp != null && fpResp.getIdServiceAds() != null) {
									ArrayList<Affectation> listeAffectationRespSurMemeFDP = getAffectationDao().listerAffectationAvecFPEtAgent(fpResp.getIdFichePoste(), agentResp.getIdAgent());
									if (listeAffectationRespSurMemeFDP.size() > 0) {
										fichePosteEae.setDateEntreeFonctionResp(listeAffectationRespSurMemeFDP.get(0).getDateDebutAff());
									}
									// on cherche toutes les affectations sur le
									// meme
									// service et
									// on prend la date la plus ancienne
									// NB : pour les affectations successives
									ArrayList<Affectation> listeAffectationRespService = getAffectationDao().listerAffectationAgentAvecService(agentResp.getIdAgent(), fpResp.getIdServiceAds());
									Date dateDebutService = null;
									for (int i = 0; i < listeAffectationRespService.size(); i++) {
										Affectation affCours = listeAffectationRespService.get(i);
										if (listeAffectationRespService.size() > i + 1) {
											if (listeAffectationRespService.get(i + 1) != null) {
												Affectation affPrecedente = listeAffectationRespService.get(i + 1);
												if (sdf.format(affCours.getDateDebutAff()).equals(Services.ajouteJours(sdf.format(affPrecedente.getDateFinAff()), 1))) {
													dateDebutService = affPrecedente.getDateDebutAff();
												} else {
													dateDebutService = affCours.getDateDebutAff();
												}
											} else {
												dateDebutService = affCours.getDateDebutAff();
											}
										} else {
											dateDebutService = affCours.getDateDebutAff();
										}
									}
									fichePosteEae.setDateEntreeServiceResp(dateDebutService);
								}
							} catch (Exception e) {

							}
						}
					} catch (Exception e) {

					}
				} catch (Exception e) {

				}
			}

			// on créer la ligne
			// on recupere l'id
			Integer idCreer = getEaeFichePosteDao().getIdEaeFichePoste();
			setIdCreerFichePosteSecondaire(idCreer);

			getEaeFichePosteDao().creerEaeFichePoste(idCreer, fichePosteEae.getIdEae(), fichePosteEae.getIdShd(), fichePosteEae.isPrimaire(), fichePosteEae.getDirectionService(),
					fichePosteEae.getService(), fichePosteEae.getSectionService(), fichePosteEae.getEmploi(), fichePosteEae.getFonction(), fichePosteEae.getDateEntreeFonction(),
					fichePosteEae.getGradePoste(), fichePosteEae.getLocalisation(), fichePosteEae.getMissions(), fichePosteEae.getFonctionResp(), fichePosteEae.getDateEntreeServiceResp(),
					fichePosteEae.getDateEntreeCollectResp(), fichePosteEae.getDateEntreeFonctionResp(), fichePosteEae.getIdServiceAds(), fichePosteEae.getCodeService(),
					fichePosteEae.getIdSirhFichePoste());

		} else {
			// on supprime les lignes existantes si elles existent
			try {
				EaeFichePoste aSupp = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEae(), false);
				// on recupere les activites/competences liees
				try {
					ArrayList<EaeFDPActivite> listActi = getEaeFDPActiviteDao().listerEaeFDPActivite(aSupp.getIdEaeFichePoste());
					for (int i = 0; i < listActi.size(); i++) {
						EaeFDPActivite a = listActi.get(i);
						getEaeFDPActiviteDao().supprimerEaeFDPActivite(a.getIdEaeFdpActivite());
					}
					ArrayList<EaeFDPCompetence> listComp = getEaeFDPCompetenceDao().listerEaeFDPCompetence(aSupp.getIdEaeFichePoste());
					for (int i = 0; i < listComp.size(); i++) {
						EaeFDPCompetence c = listComp.get(i);
						getEaeFDPCompetenceDao().supprimerEaeFDPCompetence(c.getIdEaeFdpCompetence());
					}
				} catch (Exception e) {
					// il n'y avait pas d'activites
				}
				getEaeFichePosteDao().supprimerEaeFichePoste(aSupp.getIdEaeFichePoste());
			} catch (Exception e) {
				// on ne fait rien
			}
		}
	}

	private void performCreerFichePostePrincipale(HttpServletRequest request, FichePoste fpPrincipale, EAE eae, boolean modifDateFonction, boolean modeCreation) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EaeFichePoste fpModif = null;
		// on traite la principale
		if (fpPrincipale != null) {
			// on supprime les lignes existantes si elles existent
			try {
				fpModif = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEae(), true);
				// on recupere les activites/competence liees
				try {
					ArrayList<EaeFDPActivite> listActi = getEaeFDPActiviteDao().listerEaeFDPActivite(fpModif.getIdEaeFichePoste());
					for (int i = 0; i < listActi.size(); i++) {
						EaeFDPActivite a = listActi.get(i);
						getEaeFDPActiviteDao().supprimerEaeFDPActivite(a.getIdEaeFdpActivite());
					}

					ArrayList<EaeFDPCompetence> listComp = getEaeFDPCompetenceDao().listerEaeFDPCompetence(fpModif.getIdEaeFichePoste());
					for (int i = 0; i < listComp.size(); i++) {
						EaeFDPCompetence c = listComp.get(i);
						getEaeFDPCompetenceDao().supprimerEaeFDPCompetence(c.getIdEaeFdpCompetence());
					}
				} catch (Exception e) {
					// il n'y avait pas d'activites
				}
			} catch (Exception e) {
				// on ne fait rien
				fpModif = new EaeFichePoste();
			}
			if (fpModif != null) {
				EaeEvalue evalue = null;
				try {
					evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEae());
				} catch (Exception e) {
					// on ne fait rien, seule la date d'entrée dans la fonction
					// ne sera pas renseignée.
				}
				fpModif.setIdEae(eae.getIdEae());
				fpModif.setIdSirhFichePoste(fpPrincipale.getIdFichePoste());
				if (fpPrincipale.getIdResponsable() != null) {
					Agent agentResp = null;
					try {
						agentResp = getAgentDao().chercherAgentAffecteFichePoste(fpPrincipale.getIdResponsable());
					} catch (Exception e) {

					}
					fpModif.setIdShd(agentResp == null || agentResp.getIdAgent() == null ? null : agentResp.getIdAgent());
				}
				fpModif.setPrimaire(true);
				fpModif.setIdServiceAds(fpPrincipale.getIdServiceAds());
				fpModif.setCodeService(fpPrincipale.getIdServi());

				EntiteDto direction = adsService.getAffichageDirection(fpPrincipale.getIdServiceAds());
				fpModif.setDirectionService(direction != null ? direction.getLabel() : null);
				EntiteDto service = adsService.getEntiteByIdEntite(fpPrincipale.getIdServiceAds());
				fpModif.setService(service != null ? service.getLabel() : null);
				EntiteDto section = adsService.getAffichageSection(fpPrincipale.getIdServiceAds());
				fpModif.setSectionService(section != null ? section.getLabel() : null);
				// pour l'emploi

				// Recherche de tous les liens FicheEmploi / FichePoste
				ArrayList<FEFP> liens = getFefpDao().listerFEFPAvecFP(fpPrincipale.getIdFichePoste());
				try {
					FicheEmploi fe = getFicheEmploiDao().chercherFicheEmploiAvecFichePoste(true, liens);
					if (fe != null) {
						fpModif.setEmploi(fe.getNomMetierEmploi());
					}
				} catch (Exception e) {

				}

				// pour la fonction
				try {
					TitrePoste tp = getTitrePosteDao().chercherTitrePoste(fpPrincipale.getIdTitrePoste());
					fpModif.setFonction(tp.getLibTitrePoste());
				} catch (Exception e) {

				}
				if (modifDateFonction && evalue != null) {
					// on cherche toutes les affectations sur la FDP
					// on prend la date la plus ancienne
					ArrayList<Affectation> listeAffectationSurMemeFDP = getAffectationDao().listerAffectationAvecFPEtAgent(fpPrincipale.getIdFichePoste(), evalue.getIdAgent());
					if (listeAffectationSurMemeFDP.size() > 0) {
						fpModif.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff());
					}
				}
				// grade du poste
				Grade g = Grade.chercherGrade(getTransaction(), fpPrincipale.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					fpModif.setGradePoste(g.getGrade());
				}
				EntiteGeo lieu = EntiteGeo.chercherEntiteGeo(getTransaction(), fpPrincipale.getIdEntiteGeo().toString());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					fpModif.setLocalisation(lieu.getLibEntiteGeo());
				}
				fpModif.setMissions(fpPrincipale.getMissions());
				if (fpPrincipale.getIdResponsable() != null) {
					try {
						FichePoste fpResp = getFichePosteDao().chercherFichePoste(fpPrincipale.getIdResponsable());

						try {
							TitrePoste tpResp = getTitrePosteDao().chercherTitrePoste(fpResp.getIdTitrePoste());
							fpModif.setFonctionResp(tpResp.getLibTitrePoste());
						} catch (Exception e) {

						}
						try {
							Affectation affResp = getAffectationDao().chercherAffectationActiveAvecFP(fpResp.getIdFichePoste());
							if (affResp != null && affResp.getIdAgent() != null) {
								try {
									Agent agentResp = getAgentDao().chercherAgent(affResp.getIdAgent());
									fpModif.setDateEntreeCollectResp(agentResp.getDateDerniereEmbauche());

									// on cherche toutes les affectations sur la
									// FDP
									// du
									// responsable
									// on prend la date la plus ancienne
									if (fpResp != null && fpResp.getIdServiceAds() != null) {
										ArrayList<Affectation> listeAffectationRespSurMemeFDP = getAffectationDao().listerAffectationAvecFPEtAgent(fpResp.getIdFichePoste(), agentResp.getIdAgent());
										if (listeAffectationRespSurMemeFDP.size() > 0) {
											fpModif.setDateEntreeFonctionResp(listeAffectationRespSurMemeFDP.get(0).getDateDebutAff());
										}
										// on cherche toutes les affectations
										// sur le
										// meme
										// service et
										// on prend la date la plus ancienne
										// NB : pour les affectations
										// successives
										ArrayList<Affectation> listeAffectationRespService = getAffectationDao().listerAffectationAgentAvecService(agentResp.getIdAgent(), fpResp.getIdServiceAds());
										Date dateDebutService = null;
										for (int i = 0; i < listeAffectationRespService.size(); i++) {
											Affectation affCours = listeAffectationRespService.get(i);
											if (listeAffectationRespService.size() > i + 1) {
												if (listeAffectationRespService.get(i + 1) != null) {
													Affectation affPrecedente = listeAffectationRespService.get(i + 1);
													if (sdf.format(affCours.getDateDebutAff()).equals(Services.ajouteJours(sdf.format(affPrecedente.getDateFinAff()), 1))) {
														dateDebutService = affPrecedente.getDateDebutAff();
													} else {
														dateDebutService = affCours.getDateDebutAff();
													}
												} else {
													dateDebutService = affCours.getDateDebutAff();
												}
											} else {
												dateDebutService = affCours.getDateDebutAff();
											}
										}
										fpModif.setDateEntreeServiceResp(dateDebutService);
									}
								} catch (Exception e) {

								}
							}
						} catch (Exception e) {

						}
					} catch (Exception e) {

					}

				}

				if (modeCreation || fpModif.getIdEaeFichePoste() == null) {
					// on créer la ligne
					// on recupere l'id
					Integer idCreer = getEaeFichePosteDao().getIdEaeFichePoste();
					setIdCreerFichePostePrimaire(idCreer);
					getEaeFichePosteDao().creerEaeFichePoste(idCreer, fpModif.getIdEae(), fpModif.getIdShd(), fpModif.isPrimaire(), fpModif.getDirectionService(), fpModif.getService(),
							fpModif.getSectionService(), fpModif.getEmploi(), fpModif.getFonction(), fpModif.getDateEntreeFonction(), fpModif.getGradePoste(), fpModif.getLocalisation(),
							fpModif.getMissions(), fpModif.getFonctionResp(), fpModif.getDateEntreeServiceResp(), fpModif.getDateEntreeCollectResp(), fpModif.getDateEntreeFonctionResp(),
							fpModif.getIdServiceAds(), fpModif.getCodeService(), fpModif.getIdSirhFichePoste());

				} else {
					setIdCreerFichePostePrimaire(fpModif.getIdEaeFichePoste());
					getEaeFichePosteDao().modifierEaeFichePoste(fpModif.getIdEaeFichePoste(), fpModif.getIdEae(), fpModif.getIdShd(), fpModif.isPrimaire(), fpModif.getDirectionService(),
							fpModif.getService(), fpModif.getSectionService(), fpModif.getEmploi(), fpModif.getFonction(), fpModif.getDateEntreeFonction(), fpModif.getGradePoste(),
							fpModif.getLocalisation(), fpModif.getMissions(), fpModif.getFonctionResp(), fpModif.getDateEntreeServiceResp(), fpModif.getDateEntreeCollectResp(),
							fpModif.getDateEntreeFonctionResp(), fpModif.getIdServiceAds(), fpModif.getCodeService(), fpModif.getIdSirhFichePoste());
				}

			}
		}
	}

	private void performCreerParcoursPro(HttpServletRequest request, Agent ag) throws Exception {
		SimpleDateFormat sdfMairie = new SimpleDateFormat("yyyyMMdd");
		// suite à la nouvelle gestion des services, on cherche d'abord dans
		// l'affectation pour avoir une information exacte de service
		Affectation derniereAff = null;
		ArrayList<Affectation> listAff = getAffectationDao().listerAffectationAvecAgent(ag.getIdAgent());
		if (listAff.size() > 0) {
			derniereAff = listAff.get(0);
		}
		for (int i = 0; i < listAff.size(); i++) {
			Affectation aff = listAff.get(i);
			FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
			EntiteDto serv = null;
			EntiteDto direction = null;
			if (fp == null || fp.getIdServiceAds() == null) {
				if (fp.getIdServi() == null) {
					continue;
				} else {
					SISERV siserv = getSiservDao().chercherSiserv(fp.getIdServi());
					if (siserv != null && siserv.getServi() != null) {
						serv = new EntiteDto();
						serv.setLabel(siserv.getLiserv());
						serv.setSigle(siserv.getSigle());
					} else {
						continue;
					}
				}
			} else {
				serv = adsService.getEntiteByIdEntite(fp.getIdServiceAds());
				direction = adsService.getAffichageDirection(fp.getIdServiceAds());
			}

			if (serv == null && direction == null) {
				continue;
			}

			if (aff.getDateFinAff() == null) {
				// on crée une ligne pour affectation
				EaeParcoursPro parcours = new EaeParcoursPro();
				parcours.setIdEAE(getEaeCourant().getIdEae());
				parcours.setDateDebut(aff.getDateDebutAff());
				parcours.setDateFin(aff.getDateFinAff());
				String lib = direction == null ? Const.CHAINE_VIDE : direction.getLabel();
				lib += serv == null || serv.getLabel() == null ? Const.CHAINE_VIDE : " " + serv.getLabel();
				parcours.setLibelleParcoursPro(lib);
				getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
			} else {
				// on regarde si il y a des lignes suivantes
				DateTime dateFin = new DateTime(aff.getDateFinAff());
				Affectation affSuiv = getAffectationDao().chercherAffectationAgentAvecDateDebut(ag.getIdAgent(), dateFin.plusDays(1).toDate());
				if (affSuiv == null) {
					// on crée une ligne pour administration
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEae());
					parcours.setDateDebut(aff.getDateDebutAff());
					parcours.setDateFin(aff.getDateFinAff());
					String lib = direction == null ? Const.CHAINE_VIDE : direction.getLabel();
					lib += serv == null || serv.getLabel() == null ? Const.CHAINE_VIDE : " " + serv.getLabel();
					parcours.setLibelleParcoursPro(lib);
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
				} else {
					boolean fin = false;
					DateTime dateSortie = null;
					if (affSuiv.getDateFinAff() == null) {
						dateSortie = new DateTime(aff.getDateFinAff());
						fin = true;
					} else {
						dateSortie = new DateTime(affSuiv.getDateFinAff());
					}

					while (!fin) {
						affSuiv = getAffectationDao().chercherAffectationAgentAvecDateDebut(ag.getIdAgent(), dateSortie == null ? null : dateSortie.plusDays(1).toDate());
						if (affSuiv == null) {
							fin = true;
						} else {
							dateSortie = new DateTime(affSuiv.getDateFinAff());
						}
					}
					// on crée la ligne
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEae());
					parcours.setDateDebut(aff.getDateDebutAff());
					parcours.setDateFin(dateSortie == null ? null : dateSortie.toDate());
					String lib = direction == null ? Const.CHAINE_VIDE : direction.getLabel();
					lib += serv == null || serv.getLabel() == null ? Const.CHAINE_VIDE : " " + serv.getLabel();
					parcours.setLibelleParcoursPro(lib);
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());

				}
			}
		}
		// on cherche dans SPMTSR pour l'historique
		ArrayList<Spmtsr> listSpmtsr = new ArrayList<Spmtsr>();
		if (derniereAff == null) {
			listSpmtsr = Spmtsr.listerSpmtsrAvecAgentOrderDateDeb(getTransaction(), ag);
		} else {
			listSpmtsr = Spmtsr.listerSpmtsrAvecAgentAPartirDateOrderDateDeb(getTransaction(), ag, new Integer(sdfMairie.format(derniereAff.getDateDebutAff())));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (int i = 0; i < listSpmtsr.size(); i++) {
			Spmtsr sp = listSpmtsr.get(i);
			EntiteDto serv = adsService.getEntiteByCodeServiceSISERV(sp.getServi());
			EntiteDto direction = null;
			if (serv != null && serv.getIdEntite() != null) {
				direction = adsService.getAffichageDirection(serv.getIdEntite());
			}

			if (sp.getDatfin() == null || sp.getDatfin().equals(Const.ZERO) || sp.getDatfin().equals(Const.DATE_NULL)) {
				// on crée une ligne pour affectation
				EaeParcoursPro parcours = new EaeParcoursPro();
				parcours.setIdEAE(getEaeCourant().getIdEae());
				String anneeDateDebSpmtsr = sp.getDatdeb().substring(0, 4);
				String moisDateDebSpmtsr = sp.getDatdeb().substring(4, 6);
				String jourDateDebSpmtsr = sp.getDatdeb().substring(6, 8);
				String dateDebSpmtsr = jourDateDebSpmtsr + "/" + moisDateDebSpmtsr + "/" + anneeDateDebSpmtsr;
				parcours.setDateDebut(sdf.parse(dateDebSpmtsr));
				Date dateFinSp = null;
				if (sp.getDatfin() != null && !sp.getDatfin().equals(Const.ZERO) && !sp.getDatfin().equals(Const.DATE_NULL)) {
					String anneeDateFinSpmtsr = sp.getDatfin().substring(0, 4);
					String moisDateFinSpmtsr = sp.getDatfin().substring(4, 6);
					String jourDateFinSpmtsr = sp.getDatfin().substring(6, 8);
					String dateFinSpmtsr = jourDateFinSpmtsr + "/" + moisDateFinSpmtsr + "/" + anneeDateFinSpmtsr;
					dateFinSp = sdf.parse(dateFinSpmtsr);
				}
				parcours.setDateFin(dateFinSp);
				String lib = direction == null ? Const.CHAINE_VIDE : direction.getLabel();
				lib += serv == null || serv.getLabel() == null ? Const.CHAINE_VIDE : " " + serv.getLabel();
				parcours.setLibelleParcoursPro(lib);
				getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
			} else {
				// on regarde si il y a des lignes suivantes
				Spmtsr spSuiv = Spmtsr.chercherSpmtsrAvecAgentEtDateDebut(getTransaction(), ag.getNomatr(),
						new Integer(sdfMairie.format(new DateTime(sdfMairie.parse(sp.getDatfin())).plusDays(1).toDate())));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// on crée une ligne pour administration
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEae());
					String anneeDateDebSpmtsr = sp.getDatdeb().substring(0, 4);
					String moisDateDebSpmtsr = sp.getDatdeb().substring(4, 6);
					String jourDateDebSpmtsr = sp.getDatdeb().substring(6, 8);
					String dateDebSpmtsr = jourDateDebSpmtsr + "/" + moisDateDebSpmtsr + "/" + anneeDateDebSpmtsr;
					parcours.setDateDebut(sdf.parse(dateDebSpmtsr));
					Date dateFinSp = null;
					if (sp.getDatfin() != null && !sp.getDatfin().equals(Const.ZERO) && !sp.getDatfin().equals(Const.DATE_NULL)) {
						String anneeDateFinSpmtsr = sp.getDatfin().substring(0, 4);
						String moisDateFinSpmtsr = sp.getDatfin().substring(4, 6);
						String jourDateFinSpmtsr = sp.getDatfin().substring(6, 8);
						String dateFinSpmtsr = jourDateFinSpmtsr + "/" + moisDateFinSpmtsr + "/" + anneeDateFinSpmtsr;
						dateFinSp = sdf.parse(dateFinSpmtsr);
					}
					parcours.setDateFin(dateFinSp);
					String lib = direction == null ? Const.CHAINE_VIDE : direction.getLabel();
					lib += serv == null || serv.getLabel() == null ? Const.CHAINE_VIDE : " " + serv.getLabel();
					parcours.setLibelleParcoursPro(lib);
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
				} else {
					boolean fin = false;
					Integer dateSortie = null;
					if (spSuiv.getDatfin() == null || spSuiv.getDatfin().equals(Const.ZERO) || spSuiv.getDatfin().equals(Const.DATE_NULL)) {
						Integer dateFinSP = Integer.valueOf(sp.getDatfin());
						String anneeDateDebSpmtsr = dateFinSP.toString().substring(0, 4);
						String moisDateDebSpmtsr = dateFinSP.toString().substring(4, 6);
						String jourDateDebSpmtsr = dateFinSP.toString().substring(6, 8);
						String dateDebSpmtsr = anneeDateDebSpmtsr + moisDateDebSpmtsr + jourDateDebSpmtsr;
						dateSortie = Integer.valueOf(dateDebSpmtsr);
						fin = true;
					} else {
						Integer dateFinSP = Integer.valueOf(spSuiv.getDatdeb());
						String anneeDateDebSpmtsr = dateFinSP.toString().substring(0, 4);
						String moisDateDebSpmtsr = dateFinSP.toString().substring(4, 6);
						String jourDateDebSpmtsr = dateFinSP.toString().substring(6, 8);
						String dateDebSpmtsr = anneeDateDebSpmtsr + moisDateDebSpmtsr + jourDateDebSpmtsr;
						dateSortie = Integer.valueOf(dateDebSpmtsr);
					}
					while (!fin) {
						spSuiv = Spmtsr.chercherSpmtsrAvecAgentEtDateDebut(getTransaction(), ag.getNomatr(),
								dateSortie == null ? 0 : new Integer(sdfMairie.format(new DateTime(sdfMairie.parse(dateSortie.toString())).plusDays(1).toDate())));
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							fin = true;
						} else {
							try {
								Integer dateFinSP = Integer.valueOf(spSuiv.getDatfin());
								String anneeDateDebSpmtsr = dateFinSP.toString().substring(0, 4);
								String moisDateDebSpmtsr = dateFinSP.toString().substring(4, 6);
								String jourDateDebSpmtsr = dateFinSP.toString().substring(6, 8);
								String dateDebSpmtsr = anneeDateDebSpmtsr + moisDateDebSpmtsr + jourDateDebSpmtsr;
								dateSortie = Integer.valueOf(dateDebSpmtsr);
							} catch (Exception e) {
								fin = true;
							}
						}
					}
					// on crée la ligne
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEae());
					String anneeDateDebSpmtsr = sp.getDatdeb().substring(0, 4);
					String moisDateDebSpmtsr = sp.getDatdeb().substring(4, 6);
					String jourDateDebSpmtsr = sp.getDatdeb().substring(6, 8);
					String dateDebSpmtsr = jourDateDebSpmtsr + "/" + moisDateDebSpmtsr + "/" + anneeDateDebSpmtsr;
					parcours.setDateDebut(sdf.parse(dateDebSpmtsr));
					Date dateFinale = null;
					if (sp.getDatfin() != null && !sp.getDatfin().equals(Const.ZERO) && !sp.getDatfin().equals(Const.DATE_NULL)) {
						dateFinale = sdfMairie.parse(sp.getDatfin());
					}

					parcours.setDateFin(dateFinale);
					String lib = direction == null ? Const.CHAINE_VIDE : direction.getLabel();
					lib += serv == null || serv.getLabel() == null ? Const.CHAINE_VIDE : " " + serv.getLabel();
					parcours.setLibelleParcoursPro(lib);
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());

				}
			}
		}
		// sur autre administration
		ArrayList<AutreAdministrationAgent> listAutreAdmin = getAutreAdministrationAgentDao().listerAutreAdministrationAgentAvecAgent(ag.getIdAgent());
		for (int i = 0; i < listAutreAdmin.size(); i++) {
			AutreAdministrationAgent admAgent = listAutreAdmin.get(i);
			AutreAdministration administration = getAutreAdministrationDao().chercherAutreAdministration(admAgent.getIdAutreAdmin());
			if (admAgent.getDateSortie() == null) {
				// on crée une ligne pour administration
				EaeParcoursPro parcours = new EaeParcoursPro();
				parcours.setIdEAE(getEaeCourant().getIdEae());
				parcours.setDateDebut(admAgent.getDateEntree());
				parcours.setDateFin(admAgent.getDateSortie());
				parcours.setLibelleParcoursPro(administration.getLibAutreAdmin());
				getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
			} else {
				// on regarde si il y a des lignes suivantes
				try {
					AutreAdministrationAgent admSuiv = getAutreAdministrationAgentDao().chercherAutreAdministrationAgentDateDebut(ag.getIdAgent(), admAgent.getDateSortie());

					boolean fin = false;
					Date dateSortie = admSuiv.getDateSortie();
					while (!fin) {
						try {
							admSuiv = getAutreAdministrationAgentDao().chercherAutreAdministrationAgentDateDebut(ag.getIdAgent(), dateSortie);
							dateSortie = admSuiv.getDateSortie();
						} catch (Exception e) {
							fin = true;
						}
					}
					// on crée la ligne
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEae());
					parcours.setDateDebut(admAgent.getDateEntree());
					parcours.setDateFin(dateSortie);
					parcours.setLibelleParcoursPro(administration.getLibAutreAdmin());
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
				} catch (Exception e) {

					// on crée une ligne pour administration
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEae());
					parcours.setDateDebut(admAgent.getDateEntree());
					parcours.setDateFin(admAgent.getDateSortie());
					parcours.setLibelleParcoursPro(administration.getLibAutreAdmin());
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(), parcours.getLibelleParcoursPro());
				}
			}
		}
	}

	private void performCreerDiplome(HttpServletRequest request, Agent ag) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<DiplomeAgent> listDiploAgent = getDiplomeAgentDao().listerDiplomeAgentAvecAgent(ag.getIdAgent());
		for (int i = 0; i < listDiploAgent.size(); i++) {
			DiplomeAgent d = listDiploAgent.get(i);
			TitreDiplome td = getTitreDiplomeDao().chercherTitreDiplome(d.getIdTitreDiplome());
			SpecialiteDiplome spe = getSpecialiteDiplomeDao().chercherSpecialiteDiplome(d.getIdSpecialiteDiplome());
			EaeDiplome eaeDiplome = new EaeDiplome();
			eaeDiplome.setIdEae(getEaeCourant().getIdEae());
			String anneeObtention = Const.CHAINE_VIDE;
			if (d.getDateObtention() != null) {
				anneeObtention = sdf.format(d.getDateObtention()).substring(6, sdf.format(d.getDateObtention()).length());
			}
			eaeDiplome.setLibelleDiplome((anneeObtention.equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : anneeObtention + " : ") + td.getLibTitreDiplome() + " " + spe.getLibSpecialiteDiplome());
			getEaeDiplomeDao().creerEaeDiplome(eaeDiplome.getIdEae(), eaeDiplome.getLibelleDiplome());
		}
	}

	private void performCreerEvalue(HttpServletRequest request, Agent ag, boolean miseAjourDateAdministration, boolean agentAffecte, boolean modeCreation) throws Exception {
		// cas de la modif
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EaeEvalue evalAModif = null;
		// on cherche la ligen de l'EAE Evalue
		try {
			evalAModif = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEae());
		} catch (Exception e) {
			// on en fait rien
			evalAModif = new EaeEvalue();
		}
		if (evalAModif != null) {

			evalAModif.setIdEae(getEaeCourant().getIdEae());
			evalAModif.setIdAgent(ag.getIdAgent());
			// on recupere l'affectation en cours
			Affectation aff = null;
			try {
				aff = getAffectationDao().chercherAffectationActiveAvecAgent(ag.getIdAgent());
			} catch (Exception e) {

			}

			if (aff != null && aff.getIdFichePoste() != null) {
				FichePoste fp = getFichePosteDao().chercherFichePoste(aff.getIdFichePoste());
				// on cherche toutes les affectations sur le meme
				// service et
				// on prend la date la plus ancienne
				// NB : pour les affectations successives
				ArrayList<Affectation> listeAffectationService = getAffectationDao().listerAffectationAgentAvecService(ag.getIdAgent(), fp.getIdServiceAds());
				Date dateDebutService = null;
				for (int i = 0; i < listeAffectationService.size(); i++) {
					Affectation affCours = listeAffectationService.get(i);
					if (listeAffectationService.size() > i + 1) {
						if (listeAffectationService.get(i + 1) != null) {
							Affectation affPrecedente = listeAffectationService.get(i + 1);
							if (sdf.format(affCours.getDateDebutAff()).equals(Services.ajouteJours(sdf.format(affPrecedente.getDateFinAff()), 1))) {
								dateDebutService = affPrecedente.getDateDebutAff();
							} else {
								dateDebutService = affCours.getDateDebutAff();
							}
						} else {
							dateDebutService = affCours.getDateDebutAff();
						}
					} else {
						dateDebutService = affCours.getDateDebutAff();
					}
				}
				evalAModif.setDateEntreeService(dateDebutService);
			}

			evalAModif.setDateEntreeCollectivite(ag.getDateDerniereEmbauche());
			// on cherche la date la plus ancienne dans les carrieres pour le
			// statut
			// fonctionnaire
			Carriere carr = Carriere.chercherCarriereFonctionnaireAncienne(getTransaction(), ag.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			Date dateCarriere = null;
			if (carr != null && carr.getNoMatricule() != null) {
				dateCarriere = sdf.parse(carr.getDateDebut());
			}
			// idem dans la liste des autres administration
			AutreAdministrationAgent autreAdmin = null;
			try {
				autreAdmin = getAutreAdministrationAgentDao().chercherAutreAdministrationAgentFonctionnaireAncienne(ag.getIdAgent());
			} catch (Exception e) {

			}
			Date dateAutreAdmin = null;
			if (autreAdmin != null && autreAdmin.getIdAutreAdmin() != null) {
				dateAutreAdmin = autreAdmin.getDateEntree();
			}

			if (dateAutreAdmin == null && dateCarriere != null) {
				evalAModif.setDateEntreeFonctionnaire(dateCarriere);
			} else if (dateAutreAdmin != null && dateCarriere == null) {
				evalAModif.setDateEntreeFonctionnaire(dateAutreAdmin);
			} else if (dateAutreAdmin != null && dateCarriere != null) {
				if (dateAutreAdmin.before(dateCarriere)) {
					evalAModif.setDateEntreeFonctionnaire(dateAutreAdmin);
				} else {
					evalAModif.setDateEntreeFonctionnaire(dateCarriere);
				}
			}
			// on regarde si la date de l'EAE precedent est differente alors on
			// prend la date de l'EAE de l'année passée
			CampagneEAE campagneActuelle = getCampagneCourante();
			CampagneEAE campagnePrec = getCampagneEAEDao().chercherCampagneEAEAnnee(campagneActuelle.getAnnee() - 1);
			EAE eaeAnneePrec = getEaeDao().chercherEAEAgent(ag.getIdAgent(), campagnePrec.getIdCampagneEae());
			// si on ne trouve pas d'EAE pour l'année precedente
			EaeEvalue ancienneValeur = null;
			if (eaeAnneePrec != null) {
				ancienneValeur = getEaeEvalueDao().chercherEaeEvalue(eaeAnneePrec.getIdEae());
				if (evalAModif.getDateEntreeFonctionnaire() != null) {
					if (ancienneValeur.getDateEntreeFonctionnaire() != null && (evalAModif.getDateEntreeFonctionnaire().compareTo(ancienneValeur.getDateEntreeFonctionnaire()) != 0)) {
						evalAModif.setDateEntreeFonctionnaire(ancienneValeur.getDateEntreeFonctionnaire());
					}
				} else {
					if (ancienneValeur.getDateEntreeFonctionnaire() != null) {
						evalAModif.setDateEntreeFonctionnaire(ancienneValeur.getDateEntreeFonctionnaire());
					}
				}
			}

			if (miseAjourDateAdministration) {
				// on cherche la date la plus ancienne dans les PA de
				// mairie.SPADMN
				PositionAdmAgent paAncienne = PositionAdmAgent.chercherPositionAdmAgentAncienne(getTransaction(), ag.getNomatr());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				Date dateSpadmnAncienne = null;
				if (paAncienne != null && paAncienne.getDatdeb() != null && !paAncienne.getDatdeb().equals(Const.ZERO)) {
					dateSpadmnAncienne = sdf.parse(paAncienne.getDatdeb());
				}
				// idem dans la liste des autres administration
				AutreAdministrationAgent autreAdminAncienne = null;
				try {
					autreAdminAncienne = getAutreAdministrationAgentDao().chercherAutreAdministrationAgentAncienne(ag.getIdAgent());
				} catch (Exception e) {

				}
				Date dateAutreAdminAncienne = null;
				if (autreAdminAncienne != null && autreAdminAncienne.getIdAutreAdmin() != null) {
					dateAutreAdminAncienne = autreAdminAncienne.getDateEntree();
				}

				if (dateAutreAdminAncienne == null && dateSpadmnAncienne != null) {
					evalAModif.setDateEntreeAdministration(dateSpadmnAncienne);
				} else if (dateAutreAdminAncienne != null && dateSpadmnAncienne == null) {
					evalAModif.setDateEntreeAdministration(dateAutreAdminAncienne);
				} else if (dateAutreAdminAncienne != null && dateSpadmnAncienne != null) {
					if (dateAutreAdminAncienne.before(dateSpadmnAncienne)) {
						evalAModif.setDateEntreeAdministration(dateAutreAdminAncienne);
					} else {
						evalAModif.setDateEntreeAdministration(dateSpadmnAncienne);
					}
				}
			}

			// on regarde si la date de l'EAE precedent est differente alors on
			// prend la date de l'EAE de l'année passée
			if (ancienneValeur != null) {
				if (evalAModif.getDateEntreeAdministration() != null) {
					if (ancienneValeur.getDateEntreeAdministration() != null && (evalAModif.getDateEntreeAdministration().compareTo(ancienneValeur.getDateEntreeAdministration()) != 0)) {
						evalAModif.setDateEntreeAdministration(ancienneValeur.getDateEntreeAdministration());
					}
				} else {
					if (ancienneValeur.getDateEntreeAdministration() != null) {
						evalAModif.setDateEntreeAdministration(ancienneValeur.getDateEntreeAdministration());
					}
				}
			}

			Carriere carrCours = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), ag);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carrCours != null & carrCours.getNoMatricule() != null) {
				evalAModif.setStatut(Carriere.getStatutCarriereEAE(carrCours.getCodeCategorie()));
				if (evalAModif.getStatut().equals("A")) {
					evalAModif.setStatutPrecision(StatutCarriere.chercherStatutCarriere(getTransaction(), carrCours.getCodeCategorie()).getLiCate());
				}
				if (Carriere.getStatutCarriereEAE(carrCours.getCodeCategorie()).equals("F")) {
					// pour le cadre
					StatutCarriere statCarr = StatutCarriere.chercherStatutCarriere(getTransaction(), carrCours.getCodeCategorie());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						evalAModif.setCadre(statCarr.getLiCate());
					}
					// pour la categorie
					Grade grade = Grade.chercherGrade(getTransaction(), carrCours.getCodeGrade());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						// pour le grade
						// on cherche la classe si elle existe
						String classeString = Const.CHAINE_VIDE;
						if (grade.getCodeClasse() != null && !grade.getCodeClasse().equals(Const.CHAINE_VIDE)) {
							Classe classe = Classe.chercherClasse(getTransaction(), grade.getCodeClasse());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							}
							if (classe != null && classe.getLibClasse() != null) {
								classeString = classe.getLibClasse();
							}
						}
						evalAModif.setAvctDurMin(grade.getDureeMin().equals(Const.ZERO) ? null : Integer.valueOf(grade.getDureeMin()));
						evalAModif.setAvctDurMoy(grade.getDureeMoy().equals(Const.ZERO) ? null : Integer.valueOf(grade.getDureeMoy()));
						evalAModif.setAvctDurMax(grade.getDureeMax().equals(Const.ZERO) ? null : Integer.valueOf(grade.getDureeMax()));

						evalAModif.setGrade(grade.getGrade() + " " + classeString);
						GradeGenerique gradeGen = GradeGenerique.chercherGradeGenerique(getTransaction(), grade.getCodeGradeGenerique());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							evalAModif.setCategorie(gradeGen.getCodCadre());
						}
						// pour l'echelon
						Echelon ech = Echelon.chercherEchelon(getTransaction(), grade.getCodeEchelon());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							evalAModif.setEchelon(ech.getLibEchelon());
						}
					}
				} else if (Carriere.getStatutCarriereEAE(carrCours.getCodeCategorie()).equals("CC")) {
					// pour la classification
					Grade grade = Grade.chercherGrade(getTransaction(), carrCours.getCodeGrade());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						evalAModif.setClassification(grade.getLibGrade());
					}
				}
			}

			// pour l'anciennete on met le resultat en nb de jours
			if (carrCours != null && carrCours.getDateDebut() != null && !carrCours.getDateDebut().equals(Const.DATE_NULL)) {
				// redmine #13152
				// on regarde si il y a d'autre carrieres avec le meme grade
				// si oui on prend la carriere plus lointaine
				Carriere carriereGrade = carrCours;
				ArrayList<Carriere> listeCarrMemeGrade = Carriere.listerCarriereAvecGradeEtStatut(getTransaction(), ag.getNomatr(), carrCours.getCodeGrade(), carrCours.getCodeCategorie());
				if (listeCarrMemeGrade != null && listeCarrMemeGrade.size() > 0) {
					carriereGrade = (Carriere) listeCarrMemeGrade.get(0);
				}

				int nbJours = Services.compteJoursEntreDates(carriereGrade.getDateDebut(), "31/12/" + (getCampagneCourante().getAnnee() - 1));
				evalAModif.setAncienneteEchelonJours(nbJours > 0 ? nbJours - 1 : 0);
			}

			// on regarde dans l'avancement pour le nouveau grade, le nouvel
			// echelon
			// et la date d'avancement
			try {
				AvancementFonctionnaires avctFonct = getAvancementFonctionnairesDao().chercherAvancementFonctionnaireAvecAnneeEtAgent(getCampagneCourante().getAnnee(), ag.getIdAgent());
				if (!avctFonct.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())) {
					// attention dans le cas des categorie 4 on a pas de date
					// moyenne avct
					evalAModif.setDateEffetAvct(avctFonct.getDateAvctMoy() == null ? null : avctFonct.getDateAvctMoy());
				}
				Grade gradeSuivAvct = Grade.chercherGrade(getTransaction(), avctFonct.getIdNouvGrade());
				Grade gradeAvct = Grade.chercherGrade(getTransaction(), avctFonct.getGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					// on cherche la classe si elle existe
					String classeString = Const.CHAINE_VIDE;
					if (gradeSuivAvct.getCodeClasse() != null && !gradeSuivAvct.getCodeClasse().equals(Const.CHAINE_VIDE)) {
						Classe classe = Classe.chercherClasse(getTransaction(), gradeSuivAvct.getCodeClasse());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						if (classe != null && classe.getLibClasse() != null) {
							classeString = classe.getLibClasse();
						}
					}
					evalAModif.setNouvGrade(gradeSuivAvct.getGrade() + " " + classeString);
					if (gradeAvct.getCodeTava() != null && !gradeAvct.getCodeTava().equals(Const.CHAINE_VIDE)) {
						try {
							MotifAvancement motif = getMotifAvancementDao().chercherMotifAvancement(Integer.valueOf(gradeAvct.getCodeTava()));
							if (motif != null && motif.getCode() != null) {
								evalAModif.setTypeAvct(motif.getCode());
							}
						} catch (Exception e) {
						}
					}
				}
				if (gradeSuivAvct != null && gradeSuivAvct.getCodeEchelon() != null) {
					Echelon echAvct = Echelon.chercherEchelon(getTransaction(), gradeSuivAvct.getCodeEchelon());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						evalAModif.setNouvEchelon(echAvct.getLibEchelon());
					}
				}
			} catch (Exception e) {
				// sinon, on cherche dans les détachés
				try {
					AvancementDetaches avctDetache = getAvancementDetachesDao().chercherAvancementAvecAnneeEtAgent(getCampagneCourante().getAnnee(), ag.getIdAgent());

					if (!avctDetache.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())) {
						// attention dans le cas des categorie 4 on a pas de
						// date
						// moyenne avct
						evalAModif.setDateEffetAvct(avctDetache.getDateAvctMoy() == null ? null : avctDetache.getDateAvctMoy());
					}
					Grade gradeSuivAvct = Grade.chercherGrade(getTransaction(), avctDetache.getIdNouvGrade());
					Grade gradeAvct = Grade.chercherGrade(getTransaction(), avctDetache.getGrade());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						// on cherche la classe si elle existe
						String classeString = Const.CHAINE_VIDE;
						if (gradeSuivAvct.getCodeClasse() != null && !gradeSuivAvct.getCodeClasse().equals(Const.CHAINE_VIDE)) {
							Classe classe = Classe.chercherClasse(getTransaction(), gradeSuivAvct.getCodeClasse());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							}
							if (classe != null && classe.getLibClasse() != null) {
								classeString = classe.getLibClasse();
							}
						}
						evalAModif.setNouvGrade(gradeSuivAvct.getGrade() + " " + classeString);
						if (gradeAvct.getCodeTava() != null && !gradeAvct.getCodeTava().equals(Const.CHAINE_VIDE)) {
							try {
								MotifAvancement motif = getMotifAvancementDao().chercherMotifAvancement(Integer.valueOf(gradeAvct.getCodeTava()));
								if (motif != null && motif.getCode() != null) {
									evalAModif.setTypeAvct(motif.getCode());
								}
							} catch (Exception e2) {
							}
						}
					}
					if (gradeSuivAvct != null && gradeSuivAvct.getCodeEchelon() != null) {
						Echelon echAvct = Echelon.chercherEchelon(getTransaction(), gradeSuivAvct.getCodeEchelon());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							evalAModif.setNouvEchelon(echAvct.getLibEchelon());
						}
					}
				} catch (Exception e2) {
					// si il n'y a pas d'avancement alors on calcul la date
					// d'avancement #11504
					DateAvctDto dateAvct = sirhService.getCalculDateAvct(ag.getIdAgent());
					evalAModif.setDateEffetAvct(dateAvct.getDateAvct());
				}
			}

			// pour la PA
			PositionAdmAgent paCours = PositionAdmAgent.chercherPositionAdmAgentEnCoursAvecAgent(getTransaction(), ag.getNomatr());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				evalAModif.setPosition(paCours.getPositionAdmEAE(paCours.getCdpadm()));
			}

			evalAModif.setAgentDetache(agentAffecte);
			if (modeCreation || evalAModif.getIdEaeEvalue() == null) {
				// enfin on créer la ligne
				getEaeEvalueDao().creerEaeEvalue(evalAModif.getIdEae(), evalAModif.getIdAgent(), evalAModif.getDateEntreeService(), evalAModif.getDateEntreeCollectivite(),
						evalAModif.getDateEntreeFonctionnaire(), evalAModif.getDateEntreeAdministration(), evalAModif.getStatut(), evalAModif.getAncienneteEchelonJours(), evalAModif.getCadre(),
						evalAModif.getCategorie(), evalAModif.getClassification(), evalAModif.getGrade(), evalAModif.getEchelon(), evalAModif.getDateEffetAvct(), evalAModif.getNouvGrade(),
						evalAModif.getNouvEchelon(), evalAModif.getPosition(), evalAModif.getTypeAvct(), evalAModif.getStatutPrecision(), evalAModif.getAvctDurMin(), evalAModif.getAvctDurMoy(),
						evalAModif.getAvctDurMax(), evalAModif.isAgentDetache());
			} else {
				getEaeEvalueDao().modifierEaeEvalue(evalAModif.getIdEae(), evalAModif.getIdAgent(), evalAModif.getDateEntreeService(), evalAModif.getDateEntreeCollectivite(),
						evalAModif.getDateEntreeFonctionnaire(), evalAModif.getDateEntreeAdministration(), evalAModif.getStatut(), evalAModif.getAncienneteEchelonJours(), evalAModif.getCadre(),
						evalAModif.getCategorie(), evalAModif.getClassification(), evalAModif.getGrade(), evalAModif.getEchelon(), evalAModif.getDateEffetAvct(), evalAModif.getNouvGrade(),
						evalAModif.getNouvEchelon(), evalAModif.getPosition(), evalAModif.getTypeAvct(), evalAModif.getStatutPrecision(), evalAModif.getAvctDurMin(), evalAModif.getAvctDurMoy(),
						evalAModif.getAvctDurMax(), evalAModif.isAgentDetache());
			}
		}
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ETAT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ETAT() {
		return getLB_ETAT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_ETAT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ETAT_SELECT() {
		return getZone(getNOM_LB_ETAT_SELECT());
	}

	public ArrayList<EnumEtatEAE> getListeEnumEtatEAE() {
		if (listeEnumEtatEAE == null)
			return new ArrayList<EnumEtatEAE>();
		return listeEnumEtatEAE;
	}

	public void setListeEnumEtatEAE(ArrayList<EnumEtatEAE> listeEnumEtatEAE) {
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_STATUT Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_STATUT Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_STATUT_SELECT() {
		return getZone(getNOM_LB_STATUT_SELECT());
	}

	public ArrayList<String> getListeStatut() {
		if (listeStatut == null)
			return new ArrayList<String>();
		return listeStatut;
	}

	public void setListeStatut(ArrayList<String> listeStatut) {
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
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
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
		setMessage(Const.CHAINE_VIDE);
		// On enleve le service selectionnée
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
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
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_CAP Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_CAP() {
		return getLB_CAP();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice a selectionner pour la zone de
	 * la JSP : LB_CAP Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_CAP_SELECT() {
		return getZone(getNOM_LB_CAP_SELECT());
	}

	public ArrayList<String> getListeCAP() {
		if (listeCAP == null)
			return new ArrayList<String>();
		return listeCAP;
	}

	public void setListeCAP(ArrayList<String> listeCAP) {
		this.listeCAP = listeCAP;
	}

	public EaeFormationDao getEaeFormationDao() {
		return eaeFormationDao;
	}

	public void setEaeFormationDao(EaeFormationDao eaeFormationDao) {
		this.eaeFormationDao = eaeFormationDao;
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

	public EaeParcoursProDao getEaeParcoursProDao() {
		return eaeParcoursProDao;
	}

	public void setEaeParcoursProDao(EaeParcoursProDao eaeParcoursProDao) {
		this.eaeParcoursProDao = eaeParcoursProDao;
	}

	public EaeDiplomeDao getEaeDiplomeDao() {
		return eaeDiplomeDao;
	}

	public void setEaeDiplomeDao(EaeDiplomeDao eaeDiplomeDao) {
		this.eaeDiplomeDao = eaeDiplomeDao;
	}

	public EaeFichePosteDao getEaeFichePosteDao() {
		return eaeFichePosteDao;
	}

	public void setEaeFichePosteDao(EaeFichePosteDao eaeFichePosteDao) {
		this.eaeFichePosteDao = eaeFichePosteDao;
	}

	public EaeFDPActiviteDao getEaeFDPActiviteDao() {
		return eaeFDPActiviteDao;
	}

	public void setEaeFDPActiviteDao(EaeFDPActiviteDao eaeFDPActiviteDao) {
		this.eaeFDPActiviteDao = eaeFDPActiviteDao;
	}

	public EaeEAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EaeEAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public EaeEvalueDao getEaeEvalueDao() {
		return eaeEvalueDao;
	}

	public void setEaeEvalueDao(EaeEvalueDao eaeEvalueDao) {
		this.eaeEvalueDao = eaeEvalueDao;
	}

	public EaeEvaluateurDao getEaeEvaluateurDao() {
		return eaeEvaluateurDao;
	}

	public void setEaeEvaluateurDao(EaeEvaluateurDao eaeEvaluateurDao) {
		this.eaeEvaluateurDao = eaeEvaluateurDao;
	}

	public CampagneEAEDao getCampagneEAEDao() {
		return campagneEAEDao;
	}

	public void setCampagneEAEDao(CampagneEAEDao campagneEAEDao) {
		this.campagneEAEDao = campagneEAEDao;
	}

	public EaeEvaluationDao getEaeEvaluationDao() {
		return eaeEvaluationDao;
	}

	public void setEaeEvaluationDao(EaeEvaluationDao eaeEvaluationDao) {
		this.eaeEvaluationDao = eaeEvaluationDao;
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_EAE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_EAE(int i) {
		return "NOM_CK_VALID_EAE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALID_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
		setEaeCourant(eaeCourant);
		EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEae());
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		if (getVAL_CK_VALID_EAE(idEae).equals(getCHECKED_ON())) {

			boolean agentAsCompteAD = radiService.asAgentCompteAD(Integer.valueOf(evalue.getIdAgent().toString().substring(3, evalue.getIdAgent().toString().length())));

			// si l'agent n'a pas de compte AD alors on ne fait pas la mise e
			// jour des droits
			if (agentAsCompteAD) {

				// RG-EAE-6 --> mis au moment ou on controle un EAE.
				// on cherche le document concerné
				try {
					String docFinalise = getEaeFinalisationDao().chercherDernierDocumentFinalise(idEae);
					// on fait appel au WS de Sharepoint pour mettre a jour les
					// droits de l'evalue sur le document.
					SirhKiosqueWSConsumer t = new SirhKiosqueWSConsumer();
					KiosqueDto retour = t.setDroitEvalueEAE(docFinalise, false);
					if (retour.getStatus().equals("ko")) {
						// "ERR214",
						// "Une erreur est survenue dans la mise à jour des droits des fichiers EAEs. Cet EAE ne peut être contrôle. Merci de contacter le responsable du projet.");
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR214"));
						logger.error("Pb mise à jour Droit EAE sur idEAe = " + getEaeCourant().getIdEae() + " et idEvalue = " + evalue.getIdAgent() + ", erreur :" + retour.getMessage());
						return false;
					}
				} catch (BaseWsConsumerException e) {
					// "ERR214",
					// "Une erreur est survenue dans la mise à jour des droits des fichiers EAEs. Cet EAE ne peut être contrôle. Merci de contacter le responsable du projet.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR214"));
					logger.error("Pb mise à jour Droit EAE sur idEAe = " + getEaeCourant().getIdEae() + " et idEvalue = " + evalue.getIdAgent() + ", erreur :" + e.getMessage());
					return false;
				}
			}

			// on cherche pour chaque EAE de la campagne si il y a une ligne
			// dans
			// Avanacement pourla meme année
			MotifAvancement motifRevalo = getMotifAvancementDao().chercherMotifAvancementByLib("REVALORISATION");
			MotifAvancement motifAD = getMotifAvancementDao().chercherMotifAvancementByLib("AVANCEMENT DIFFERENCIE");
			MotifAvancement motifPromo = getMotifAvancementDao().chercherMotifAvancementByLib("PROMOTION");
			MotifAvancement motifTitu = getMotifAvancementDao().chercherMotifAvancementByLib("TITULARISATION");

			try {
				AvancementFonctionnaires avct = getAvancementFonctionnairesDao().chercherAvancementFonctionnaireAvecAnneeEtAgent(getCampagneCourante().getAnnee(), evalue.getIdAgent());
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
								EaeEvaluation eval = getEaeEvaluationDao().chercherEaeEvaluation(getEaeCourant().getIdEae());
								// #14791 bug sur le champ AVIS_SHD de
								// SIRH.AVCT_FONCT
								if (typeAvct.equals(motifRevalo.getIdMotifAvct().toString())) {
									avct.setAvisShd(eval.getAvisRevalorisation() == 1 ? "Favorable" : "Défavorable");
								} else if (typeAvct.equals(motifAD.getIdMotifAvct().toString())) {
									avct.setAvisShd(eval.getPropositionAvancement());
								} else if (typeAvct.equals(motifTitu.getIdMotifAvct().toString())) {
									avct.setAvisShd("MOY");
								} else if (typeAvct.equals(motifPromo.getIdMotifAvct().toString())) {
									avct.setAvisShd(eval.getAvisChangementClasse() == 1 ? "Favorable" : "Défavorable");
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
				getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(),
						avct.getSectionService(), avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(),
						avct.getAccAnnee(), avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(),
						avct.getNouvAccJour(), avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(),
						avct.getDateAvctMini(), avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(),
						avct.getCarriereSimu(), avct.getUserVerifSgc(), avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(),
						avct.getOrdreMerite(), avct.getAvisShd(), avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(),
						avct.getObservationArr(), avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(),
						avct.getCodePa());

				if (getTransaction().isErreur())
					return false;

				// tout s'est bien passé
				commitTransaction();
			} catch (Exception e) {
				// "INF500",
				// "Aucun avancement n'a été trouvé pour cet EAE. Le motif et l'avis SHD n'ont pu être mis a jour.");
				setMessage(MessageUtils.getMessage("INF500"));
			}

			// on met a jour le statut de l'EAE
			getEaeCourant().setEtat(EnumEtatEAE.CONTROLE.getCode());
			getEaeCourant().setDateControle(new Date());
			getEaeCourant().setHeureControle(heureAction);
			getEaeCourant().setUserControle(user.getUserName());
			getEaeDao().modifierControle(getEaeCourant().getIdEae(), getEaeCourant().getDateControle(), getEaeCourant().getHeureControle(), getEaeCourant().getUserControle(),
					getEaeCourant().getEtat());
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_DEVALID_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
		setEaeCourant(eaeCourant);
		EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEae());
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());

		boolean agentAsCompteAD = radiService.asAgentCompteAD(Integer.valueOf(evalue.getIdAgent().toString().substring(3, evalue.getIdAgent().toString().length())));

		// si l'agent est hors VDN alors on ne fait pas la mise à jour des
		// droits
		if (agentAsCompteAD) {
			// RG-EAE-6 --> mis au moment ou on controle un EAE.
			// on cherche le document concerné
			try {
				String docFinalise = getEaeFinalisationDao().chercherDernierDocumentFinalise(idEae);
				// on fait appel au WS de Sharepoint pour mettre a jour les
				// droits
				// de l'evalue sur le document.
				SirhKiosqueWSConsumer t = new SirhKiosqueWSConsumer();
				KiosqueDto retour = t.setDroitEvalueEAE(docFinalise, true);
				if (retour.getStatus().equals("ko")) {
					// "ERR215",
					// "Une erreur est survenue dans la mise à jour des droits des fichiers EAEs.Cet EAE ne peut être dé-contrôle. Merci de contacter le responsable du projet.");
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR215"));
					logger.error("Pb mise à jour Droit EAE sur idEAe = " + getEaeCourant().getIdEae() + " et idEvalue = " + evalue.getIdAgent() + ", erreur :" + retour.getMessage());
					return false;
				}
			} catch (BaseWsConsumerException e) {
				// "ERR215",
				// "Une erreur est survenue dans la mise à jour des droits des fichiers EAEs.Cet EAE ne peut être dé-contrôle. Merci de contacter le responsable du projet.");
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR215"));
				logger.error("Pb mise à jour Droit EAE sur idEAe = " + getEaeCourant().getIdEae() + " et idEvalue = " + evalue.getIdAgent() + ", erreur :" + e.getMessage());
				return false;
			}
		}

		try {
			AvancementFonctionnaires avct = getAvancementFonctionnairesDao().chercherAvancementFonctionnaireAvecAnneeEtAgent(getCampagneCourante().getAnnee(), evalue.getIdAgent());
			avct.setIdMotifAvct(null);
			avct.setAvisShd(null);

			getAvancementFonctionnairesDao().modifierAvancement(avct.getIdAvct(), avct.getIdAvisCap(), avct.getIdAgent(), avct.getIdMotifAvct(), avct.getDirectionService(), avct.getSectionService(),
					avct.getFiliere(), avct.getGrade(), avct.getIdNouvGrade(), avct.getAnnee(), avct.getCdcadr(), avct.getBmAnnee(), avct.getBmMois(), avct.getBmJour(), avct.getAccAnnee(),
					avct.getAccMois(), avct.getAccJour(), avct.getNouvBmAnnee(), avct.getNouvBmMois(), avct.getNouvBmJour(), avct.getNouvAccAnnee(), avct.getNouvAccMois(), avct.getNouvAccJour(),
					avct.getIban(), avct.getInm(), avct.getIna(), avct.getNouvIban(), avct.getNouvInm(), avct.getNouvIna(), avct.getDateGrade(), avct.getPeriodeStandard(), avct.getDateAvctMini(),
					avct.getDateAvctMoy(), avct.getDateAvctMaxi(), avct.getNumArrete(), avct.getDateArrete(), avct.getEtat(), avct.getCodeCategorie(), avct.getCarriereSimu(), avct.getUserVerifSgc(),
					avct.getDateVerifSgc(), avct.getHeureVerifSgc(), avct.getUserVerifSef(), avct.getDateVerifSef(), avct.getHeureVerifSef(), avct.getOrdreMerite(), avct.getAvisShd(),
					avct.getIdAvisArr(), avct.getIdAvisEmp(), avct.getUserVerifArr(), avct.getDateVerifArr(), avct.getHeureVerifArr(), avct.getDateCap(), avct.getObservationArr(),
					avct.getUserVerifArrImpr(), avct.getDateVerifArrImpr(), avct.getHeureVerifArrImpr(), avct.isRegularisation(), avct.isAgentVdn(), avct.getIdCap(), avct.getCodePa());

			// tout s'est bien passé
			commitTransaction();
		} catch (Exception e) {
			// "INF500",
			// "Aucun avancement n'a été trouvé pour cet EAE. Le motif et l'avis SHD 'nont pu être mis a jour.");
			setMessage(MessageUtils.getMessage("INF500"));
		}

		// on met a jour le statut de l'EAE
		getEaeCourant().setEtat(EnumEtatEAE.FINALISE.getCode());
		getEaeCourant().setDateControle(new Date());
		getEaeCourant().setHeureControle(heureAction);
		getEaeCourant().setUserControle(user.getUserName());
		getEaeDao().modifierControle(getEaeCourant().getIdEae(), getEaeCourant().getDateControle(), getEaeCourant().getHeureControle(), getEaeCourant().getUserControle(), getEaeCourant().getEtat());

		// on reinitialise l'affichage du tableau
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean perform_METTRE_A_JOUR_EAE(HttpServletRequest request, EAE eaeChoisi) throws Exception {
		setEaeCourant(eaeChoisi);

		// on met a jour les tables utiles
		// RG-EAE-41
		EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEae());
		Agent ag = getAgentDao().chercherAgent(evalue.getIdAgent());
		// on cherche les FDP de l'agent
		FichePoste fpPrincipale = null;
		FichePoste fpSecondaire = null;
		try {
			Affectation affCours = getAffectationDao().chercherAffectationActiveAvecAgent(ag.getIdAgent());
			fpPrincipale = getFichePosteDao().chercherFichePoste(affCours.getIdFichePoste());
			if (affCours.getIdFichePosteSecondaire() != null) {
				fpSecondaire = getFichePosteDao().chercherFichePoste(affCours.getIdFichePosteSecondaire());
			}
		} catch (Exception e) {

		}
		// on met les données dans EAE-evalue
		performCreerEvalue(request, ag, false, evalue.isAgentDetache(), false);
		// on met les données dans EAE-FichePoste
		performCreerFichePostePrincipale(request, fpPrincipale, getEaeCourant(), false, false);
		performCreerFichePosteSecondaire(request, fpSecondaire, getEaeCourant());
		// on met les données dans EAE-FDP-Activites
		performCreerActivitesFichePostePrincipale(request, fpPrincipale);
		performCreerActivitesFichePosteSecondaire(request, fpSecondaire);
		performCreerCompetencesFichePostePrincipale(request, fpPrincipale);
		performCreerCompetencesFichePosteSecondaire(request, fpSecondaire);

		// on supprime les diplomes
		ArrayList<EaeDiplome> listeEaeDiplome = getEaeDiplomeDao().listerEaeDiplome(getEaeCourant().getIdEae());
		for (int j = 0; j < listeEaeDiplome.size(); j++) {
			EaeDiplome dip = listeEaeDiplome.get(j);
			getEaeDiplomeDao().supprimerEaeDiplome(dip.getIdEaeDiplome());
		}
		// on met les données dans EAE-Diplome
		performCreerDiplome(request, ag);

		// on supprime les parcours pro
		ArrayList<EaeParcoursPro> listeEaeParcoursPro = getEaeParcoursProDao().listerEaeParcoursPro(getEaeCourant().getIdEae());
		for (int j = 0; j < listeEaeParcoursPro.size(); j++) {
			EaeParcoursPro parcours = listeEaeParcoursPro.get(j);
			getEaeParcoursProDao().supprimerEaeParcoursPro(parcours.getIdEaeParcoursPro());
		}
		// on met les données dans EAE-Parcours-Pro
		performCreerParcoursPro(request, ag);

		// on supprime les formations
		ArrayList<EaeFormation> listeEaeFormation = getEaeFormationDao().listerEaeFormation(getEaeCourant().getIdEae());
		for (int j = 0; j < listeEaeFormation.size(); j++) {
			EaeFormation form = listeEaeFormation.get(j);
			getEaeFormationDao().supprimerEaeFormation(form.getIdEaeFormation());
		}
		// on met les données dans EAE-Formation
		performCreerFormation(request, ag);

		// on met a jour le champ CAP
		// on cherche si il y a une ligne dans les avancements
		EAE eae = getEaeCourant();
		try {
			AvancementFonctionnaires avct = getAvancementFonctionnairesDao().chercherAvancementFonctionnaireAvecAnneeEtAgent(getCampagneCourante().getAnnee(), evalue.getIdAgent());
			// on a trouvé une ligne dans avancement
			// on regarde l'etat de la ligne
			// si 'valid DRH' alors on met CAP a true;
			if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
				// si l'avancement est de type TITU alors on met false #11510
				if (avct.getIdMotifAvct() != null && avct.getIdMotifAvct().toString().equals("6")) {
					eae.setCap(false);
				} else {
					eae.setCap(true);
				}
			} else {
				eae.setCap(false);
			}
		} catch (Exception e) {

			eae.setCap(false);
		}
		getEaeDao().modifierCAP(getEaeCourant().getIdEae(), eae.isCap());

		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_DEFINALISE_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
		setEaeCourant(eaeCourant);

		getEaeCourant().setEtat(EnumEtatEAE.EN_COURS.getCode());
		getEaeDao().modifierEtat(getEaeCourant().getIdEae(), getEaeCourant().getEtat());

		// pour reinitiliser l'affichage du tableau.
		performPB_FILTRER(request);
		return true;
	}

	public EaeFDPCompetenceDao getEaeFDPCompetenceDao() {
		return eaeFDPCompetenceDao;
	}

	public void setEaeFDPCompetenceDao(EaeFDPCompetenceDao eaeFDPCompetenceDao) {
		this.eaeFDPCompetenceDao = eaeFDPCompetenceDao;
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_SUPP_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		EAE eaeSelection = getEaeDao().chercherEAE(idEae);
		// on supprime tous les evaluateurs existants
		ArrayList<EaeEvaluateur> evaluateursExistants = getEaeEvaluateurDao().listerEvaluateurEAE(eaeSelection.getIdEae());
		for (int i = 0; i < evaluateursExistants.size(); i++) {
			EaeEvaluateur eval = evaluateursExistants.get(i);
			getEaeEvaluateurDao().supprimerEaeEvaluateur(eval.getIdEaeEvaluateur());
		}
		// on supprime le delegataire
		getEaeDao().modifierDelegataire(eaeSelection.getIdEae(), null);
		// on met a jour le statut de l'EAE
		eaeSelection.setEtat(EnumEtatEAE.SUPPRIME.getCode());
		getEaeDao().modifierEtat(eaeSelection.getIdEae(), eaeSelection.getEtat());

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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_DESUPP_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		EAE eaeSelection = getEaeDao().chercherEAE(idEae);
		// on met a jour le statut de l'EAE
		eaeSelection.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
		getEaeDao().modifierEtat(eaeSelection.getIdEae(), eaeSelection.getEtat());

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
	 * Retourne la valeur à afficher par la JSP pour la zone :
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// On enleve l'agent selectionnée
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
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT_EVALUE
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_EVALUE(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// On enleve l'agent selectionnée
		addZone(getNOM_ST_AGENT_EVALUE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_MAJ Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_MAJ(int i) {
		return "NOM_CK_VALID_MAJ_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_VALID_MAJ Date de création : (21/11/11 09:55:36)
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
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_METTRE_A_JOUR_EAE(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// on recupere les lignes qui sont cochées pour mettre a jour
		int nbEae = 0;
		for (int j = 0; j < getListeEAE().size(); j++) {
			// on recupere la ligne concernée
			EAE eae = (EAE) getListeEAE().get(j);
			Integer i = eae.getIdEae();
			// si la colonne mettre a jour est cochée
			if (getVAL_CK_VALID_MAJ(i).equals(getCHECKED_ON())) {
				setEaeCourant(eae);
				perform_METTRE_A_JOUR_EAE(request, eae);
				nbEae++;
			}

		}

		// pour reinitiliser l'affichage du tableau.
		performPB_FILTRER(request);

		// "INF203", "@ EAE(s) ont été mis a jour.");
		setMessage(MessageUtils.getMessage("INF203", String.valueOf(nbEae)));
		return true;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION_DOC Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER_DOC(HttpServletRequest request, int idEae) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("URL_SHAREPOINT_GED");
		logger.info("Rep stock : " + repertoireStockage);

		// Récup de l'EAE courant
		EAE eae = getEaeDao().chercherEAE(idEae);
		String finalisation = getEaeFinalisationDao().chercherDernierDocumentFinalise(eae.getIdEae());
		// on affiche le document
		logger.info("Script : " + getScriptOuverture(repertoireStockage + finalisation));
		setURLFichier(getScriptOuverture(repertoireStockage + finalisation));

		return true;
	}

	public EaeFinalisationDao getEaeFinalisationDao() {
		return eaeFinalisationDao;
	}

	public void setEaeFinalisationDao(EaeFinalisationDao eaeFinalisationDao) {
		this.eaeFinalisationDao = eaeFinalisationDao;
	}

	public EaeCampagneTaskDao getEaeCampagneTaskDao() {
		return eaeCampagneTaskDao;
	}

	public void setEaeCampagneTaskDao(EaeCampagneTaskDao eaeCampagneTaskDao) {
		this.eaeCampagneTaskDao = eaeCampagneTaskDao;
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

	public ArrayList<String> getListeAffecte() {
		return listeAffecte == null ? new ArrayList<String>() : listeAffecte;
	}

	public void setListeAffecte(ArrayList<String> listeAffecte) {
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

	public String getNOM_ST_MATRICULE_AGENT(int i) {
		return "NOM_ST_MATRICULE_AGENT_" + i;
	}

	public String getVAL_ST_MATRICULE_AGENT(int i) {
		return getZone(getNOM_ST_MATRICULE_AGENT(i));
	}

	public ArrayList<EaeCampagneTask> getListeCampagneTask() {
		return listeCampagneTask == null ? new ArrayList<EaeCampagneTask>() : listeCampagneTask;
	}

	public void setListeCampagneTask(ArrayList<EaeCampagneTask> listeCampagneTask) {
		this.listeCampagneTask = listeCampagneTask;
	}

	private void initialiseAffichageListeCampagneTask(HttpServletRequest request) {

		// on recupere l'année choisie
		int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		CampagneEAE camp = getListeCampagneEAE().get(indiceCampagne);

		ArrayList<EaeCampagneTask> listeTask = getEaeCampagneTaskDao().listerCampagneTask(camp.getAnnee());
		setListeCampagneTask(listeTask);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		for (int p = 0; p < getListeCampagneTask().size(); p++) {
			EaeCampagneTask task = (EaeCampagneTask) getListeCampagneTask().get(p);
			Integer i = task.getIdCampagneTask();

			try {
				Agent agentTask = getAgentDao().chercherAgent(task.getIdAgent());
				addZone(getNOM_ST_AGENT_TASK(i), agentTask.getNomAgent() + " " + agentTask.getPrenomAgent());
				addZone(getNOM_ST_STATUT_TASK(i), task.getTaskStatus() == null ? " En cours" : "Terminé");
				addZone(getNOM_ST_ERREUR_TASK(i), task.getTaskStatus() != null ? task.getTaskStatus().length() > 100 ? task.getTaskStatus().substring(0, 99) : task.getTaskStatus() : "");
				addZone(getNOM_ST_DATE_TASK(i), sdf.format(task.getDateCalculEae()));
			} catch (Exception e) {
				// on ne fait rien
			}
		}
	}

	public String getNOM_ST_AGENT_TASK(int i) {
		return "NOM_ST_AGENT_TASK_" + i;
	}

	public String getVAL_ST_AGENT_TASK(int i) {
		return getZone(getNOM_ST_AGENT_TASK(i));
	}

	public String getNOM_ST_DATE_TASK(int i) {
		return "NOM_ST_DATE_TASK_" + i;
	}

	public String getVAL_ST_DATE_TASK(int i) {
		return getZone(getNOM_ST_DATE_TASK(i));
	}

	public String getNOM_ST_STATUT_TASK(int i) {
		return "NOM_ST_STATUT_TASK_" + i;
	}

	public String getVAL_ST_STATUT_TASK(int i) {
		return getZone(getNOM_ST_STATUT_TASK(i));
	}

	public String getNOM_ST_ERREUR_TASK(int i) {
		return "NOM_ST_ERREUR_TASK_" + i;
	}

	public String getVAL_ST_ERREUR_TASK(int i) {
		return getZone(getNOM_ST_ERREUR_TASK(i));
	}

	public SISERVDao getSiservDao() {
		return siservDao;
	}

	public void setSiservDao(SISERVDao siservDao) {
		this.siservDao = siservDao;
	}
}