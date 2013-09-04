package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.connecteur.metier.Spmtsr;
import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumEtatEAE;
import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.gestionagent.dto.KiosqueDto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.AutreAdministrationAgent;
import nc.mairie.metier.agent.PositionAdmAgent;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Classe;
import nc.mairie.metier.carriere.Echelon;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.carriere.StatutCarriere;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.MotifAvancement;
import nc.mairie.metier.parametrage.SpecialiteDiplomeNW;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.AutreAdministration;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.spring.dao.metier.EAE.CampagneEAEDao;
import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeDiplomeDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluateurDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvaluationDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.dao.metier.EAE.EaeFDPActiviteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFDPCompetenceDao;
import nc.mairie.spring.dao.metier.EAE.EaeFichePosteDao;
import nc.mairie.spring.dao.metier.EAE.EaeFinalisationDao;
import nc.mairie.spring.dao.metier.EAE.EaeFormationDao;
import nc.mairie.spring.dao.metier.EAE.EaeParcoursProDao;
import nc.mairie.spring.dao.metier.diplome.FormationAgentDao;
import nc.mairie.spring.dao.metier.parametrage.CentreFormationDao;
import nc.mairie.spring.dao.metier.parametrage.TitreFormationDao;
import nc.mairie.spring.domain.metier.EAE.CampagneEAE;
import nc.mairie.spring.domain.metier.EAE.EAE;
import nc.mairie.spring.domain.metier.EAE.EaeDiplome;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluateur;
import nc.mairie.spring.domain.metier.EAE.EaeEvaluation;
import nc.mairie.spring.domain.metier.EAE.EaeEvalue;
import nc.mairie.spring.domain.metier.EAE.EaeFDPActivite;
import nc.mairie.spring.domain.metier.EAE.EaeFDPCompetence;
import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;
import nc.mairie.spring.domain.metier.EAE.EaeFinalisation;
import nc.mairie.spring.domain.metier.EAE.EaeFormation;
import nc.mairie.spring.domain.metier.EAE.EaeParcoursPro;
import nc.mairie.spring.domain.metier.diplome.FormationAgent;
import nc.mairie.spring.domain.metier.parametrage.CentreFormation;
import nc.mairie.spring.domain.metier.parametrage.TitreFormation;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhKiosqueWSConsumer;
import nc.mairie.spring.ws.SirhKiosqueWSConsumerException;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
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
	private String[] LB_DETACHE;
	private ArrayList<CampagneEAE> listeCampagneEAE;
	private ArrayList<EnumEtatEAE> listeEnumEtatEAE;
	private ArrayList<String> listeStatut;
	private ArrayList<String> listeCAP;
	private ArrayList<String> listeDetache;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private CampagneEAE campagneCourante;

	private ArrayList<EAE> listeEAE;
	private EAE eaeCourant;

	private Integer idCreerFichePosteSecondaire;
	private Integer idCreerFichePostePrimaire;

	public String ACTION_CALCUL = "Calcul";

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
	private EAEDao eaeDao;
	private EaeEvalueDao eaeEvalueDao;
	private EaeEvaluateurDao eaeEvaluateurDao;
	private CampagneEAEDao campagneEAEDao;
	private EaeFDPCompetenceDao eaeFDPCompetenceDao;
	private EaeFinalisationDao eaeFinalisationDao;

	private String message;
	private String urlFichier;

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

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		initialiseListeService();

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
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT_EVALUATEUR(), agt.getNoMatricule());
		}

		if (etatStatut() == STATUT_RECHERCHER_AGENT_EVALUE) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT_EVALUE(), agt.getNoMatricule());
		}

		// initialisation de l'affichage la liste des eae
		initialiseAffichageListeEAE(request);

		if (getMessage() != null && !getMessage().equals(Const.CHAINE_VIDE)) {
			setStatut(STATUT_MEME_PROCESS, false, getMessage());
			setMessage(null);
		}

	}

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
	}

	private void initialiseDelegataire() throws Exception {
		AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
		if (getEaeCourant() != null && agt != null) {
			getEaeCourant().setIdDelegataire(Integer.valueOf(agt.getIdAgent()));
			getEaeDao().modifierDelegataire(getEaeCourant().getIdEAE(), getEaeCourant().getIdDelegataire());
			// si EAE d'un détachés alors on passe le statut de l'EAE en
			// "on debuté"
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEAE());
			if (evalue.isAgentDetache()) {
				getEaeCourant().setEtat(EnumEtatEAE.NON_DEBUTE.getCode());
				getEaeDao().modifierEtat(getEaeCourant().getIdEAE(), getEaeCourant().getEtat());
			}

		}

	}

	private void initialiseEvaluateur() throws Exception {
		ArrayList<AgentNW> listeEvaluateurSelect = (ArrayList<AgentNW>) VariablesActivite.recuperer(this, "EVALUATEURS");
		VariablesActivite.enlever(this, "EVALUATEURS");
		if (getEaeCourant() != null) {
			// on supprime tous les evaluateurs existants
			ArrayList<EaeEvaluateur> evaluateursExistants = getEaeEvaluateurDao().listerEvaluateurEAE(getEaeCourant().getIdEAE());
			for (int i = 0; i < evaluateursExistants.size(); i++) {
				EaeEvaluateur eval = evaluateursExistants.get(i);
				getEaeEvaluateurDao().supprimerEaeEvaluateur(eval.getIdEaeEvaluateur());
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (listeEvaluateurSelect != null && listeEvaluateurSelect.size() > 0) {
				for (int j = 0; j < listeEvaluateurSelect.size(); j++) {
					AgentNW agentEvaluateur = listeEvaluateurSelect.get(j);
					if (agentEvaluateur != null) {
						// on crée les nouveaux evaluateurs
						EaeEvaluateur eval = new EaeEvaluateur();
						eval.setIdEae(getEaeCourant().getIdEAE());
						eval.setIdAgent(Integer.valueOf(agentEvaluateur.getIdAgent()));

						// on recupere le poste
						Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), agentEvaluateur.getIdAgent());
						if (getTransaction().isErreur() || aff.getIdFichePoste() == null) {
							getTransaction().traiterErreur();
						}

						if (aff != null && aff.getIdFichePoste() != null) {
							FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
							TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
							eval.setFonction(tp.getLibTitrePoste());
							// on cherche toutes les affectations sur la FDP
							// on prend la date la plus ancienne
							ArrayList<Affectation> listeAffectationSurMemeFDP = Affectation.listerAffectationAvecFPEtAgent(getTransaction(), fp,
									agentEvaluateur.getIdAgent());
							if (listeAffectationSurMemeFDP.size() > 0) {
								eval.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff() == null
										|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.CHAINE_VIDE)
										|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.DATE_NULL) ? null : sdf
										.parse(listeAffectationSurMemeFDP.get(0).getDateDebutAff()));
							}
							// on cherche toutes les affectations sur le meme
							// service et
							// on prend la date la plus ancienne
							// NB : pour les affectations successives
							ArrayList<Affectation> listeAffectationService = Affectation.listerAffectationAgentAvecService(getTransaction(),
									agentEvaluateur.getIdAgent(), fp.getIdServi());
							String dateDebutService = null;
							for (int i = 0; i < listeAffectationService.size(); i++) {
								Affectation affCours = listeAffectationService.get(i);
								if (listeAffectationService.size() > i + 1) {
									if (listeAffectationService.get(i + 1) != null) {
										Affectation affPrecedente = listeAffectationService.get(i + 1);
										if (affCours.getDateDebutAff().equals(Services.ajouteJours(affPrecedente.getDateFinAff(), 1))) {
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
							eval.setDateEntreeService(dateDebutService == null || dateDebutService.equals(Const.CHAINE_VIDE) ? null : sdf
									.parse(dateDebutService));
						}

						eval.setDateEntreeCollectivite(agentEvaluateur.getDateDerniereEmbauche() == null
								|| agentEvaluateur.getDateDerniereEmbauche().equals(Const.CHAINE_VIDE)
								|| agentEvaluateur.getDateDerniereEmbauche().equals(Const.DATE_NULL) ? null : sdf.parse(agentEvaluateur
								.getDateDerniereEmbauche()));

						getEaeEvaluateurDao().creerEaeEvaluateur(eval.getIdEae(), eval.getIdAgent(), eval.getFonction(), eval.getDateEntreeService(),
								eval.getDateEntreeCollectivite(), eval.getDateEntreeFonction());
					}
				}
				// on sort de la boucle, on change l'état de l'EAE en NON-DEBUTE
				getEaeCourant().setEtat(EnumEtatEAE.NON_DEBUTE.getCode());
				getEaeDao().modifierEtat(getEaeCourant().getIdEAE(), getEaeCourant().getEtat());

			} else {
				if (getEaeCourant() != null) {
					// si pas d'évaluateur choisi alors on passe l'eae en NON
					// AFFECTE et on supprime le delegataire
					getEaeDao().modifierDelegataire(getEaeCourant().getIdEAE(), null);
					getEaeCourant().setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
					getEaeDao().modifierEtat(getEaeCourant().getIdEAE(), getEaeCourant().getEtat());

				}
			}
		}
	}

	private boolean initialiseListeEAE(HttpServletRequest request) throws Exception {
		if (etatStatut() != STATUT_EVALUATEUR) {
			if (getListeCampagneEAE().size() > 0) {
				// si il s'agit d'une campagne ouverte on fait le calcul
				if (getCampagneCourante().estOuverte()) {
					if (!performCalculEAE(request, getCampagneCourante().getIdCampagneEAE(), getCampagneCourante().getAnnee())) {
						// "ERR213",
						// "Une erreur est survenue dans le calcul des EAEs. Merci de contacter le responsable du projet."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR213"));
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean performCalculEAE(HttpServletRequest request, Integer idCampagneEAE, Integer anneeCampagne) throws Exception {
		// Miseà jour de l'action menée
		addZone(getNOM_ST_ACTION(), ACTION_CALCUL);
		logger.info("Entree dans le calcul");

		// ON ENELEVE CETTE PARTIE CAR SI L'EAE a été saisi dans le KIOSQUE
		// ALORS ON A DES GROS SOUCIS EN BASE :cf JIRA SIRH-232
		// Suppression des eae à l'état 'Non Affecté' de la campagne donnée
		// et des evaluateurs associés
		/*
		 * ArrayList<EAE> listeEaeNonAffASupprimer =
		 * getEaeDao().listerEAETravailPourCampagne
		 * (EnumEtatEAE.NON_AFFECTE.getCode(), idCampagneEAE); for (int i = 0; i
		 * < listeEaeNonAffASupprimer.size(); i++) { EAE eaeASupp =
		 * listeEaeNonAffASupprimer.get(i); ArrayList<EaeEvaluateur>
		 * listeEvalEae =
		 * getEaeEvaluateurDao().listerEvaluateurEAE(eaeASupp.getIdEAE()); for
		 * (int j = 0; j < listeEvalEae.size(); j++) { EaeEvaluateur eval =
		 * listeEvalEae.get(j);
		 * getEaeEvaluateurDao().supprimerEaeEvaluateur(eval
		 * .getIdEaeEvaluateur()); } ArrayList<EaeFichePoste> listeEaeFichePoste
		 * =
		 * getEaeFichePosteDao().chercherEaeFichePosteIdEae(eaeASupp.getIdEAE()
		 * ); for (int j = 0; j < listeEaeFichePoste.size(); j++) {
		 * EaeFichePoste eaeFDP = listeEaeFichePoste.get(j);
		 * ArrayList<EaeFDPActivite> listeActiFDP =
		 * getEaeFDPActiviteDao().listerEaeFDPActivite
		 * (eaeFDP.getIdEaeFichePoste()); for (int k = 0; k <
		 * listeActiFDP.size(); k++) { EaeFDPActivite acti =
		 * listeActiFDP.get(k);
		 * getEaeFDPActiviteDao().supprimerEaeFDPActivite(acti
		 * .getIdEaeFDPActivite()); } ArrayList<EaeFDPCompetence> listeCompFDP =
		 * getEaeFDPCompetenceDao
		 * ().listerEaeFDPCompetence(eaeFDP.getIdEaeFichePoste()); for (int k =
		 * 0; k < listeCompFDP.size(); k++) { EaeFDPCompetence comp =
		 * listeCompFDP.get(k);
		 * getEaeFDPCompetenceDao().supprimerEaeFDPCompetence
		 * (comp.getIdEaeFDPCompetence()); }
		 * getEaeFichePosteDao().supprimerEaeFichePoste
		 * (eaeFDP.getIdEaeFichePoste()); } // on supprime l'evalue EaeEvalue
		 * evalue = getEaeEvalueDao().chercherEaeEvalue(eaeASupp.getIdEAE());
		 * getEaeEvalueDao().supprimerEaeEvalue(evalue.getIdEaeEvalue());
		 * 
		 * // on supprime les diplomes ArrayList<EaeDiplome> listeEaeDiplome =
		 * getEaeDiplomeDao().listerEaeDiplome(eaeASupp.getIdEAE()); for (int j
		 * = 0; j < listeEaeDiplome.size(); j++) { EaeDiplome dip =
		 * listeEaeDiplome.get(j);
		 * getEaeDiplomeDao().supprimerEaeDiplome(dip.getIdEaeDiplome()); }
		 * 
		 * // on supprime les parcours pro ArrayList<EaeParcoursPro>
		 * listeEaeParcoursPro =
		 * getEaeParcoursProDao().listerEaeParcoursPro(eaeASupp.getIdEAE()); for
		 * (int j = 0; j < listeEaeParcoursPro.size(); j++) { EaeParcoursPro
		 * parcours = listeEaeParcoursPro.get(j);
		 * getEaeParcoursProDao().supprimerEaeParcoursPro
		 * (parcours.getIdEaeParcoursPro()); }
		 * 
		 * // on supprime les formations ArrayList<EaeFormation>
		 * listeEaeFormation =
		 * getEaeFormationDao().listerEaeFormation(eaeASupp.getIdEAE()); for
		 * (int j = 0; j < listeEaeFormation.size(); j++) { EaeFormation form =
		 * listeEaeFormation.get(j);
		 * getEaeFormationDao().supprimerEaeFormation(form.getIdEaeFormation());
		 * } // on supprime l'EAE getEaeDao().supprimerEAE(eaeASupp.getIdEAE());
		 * } logger.info("fin des suppressions");
		 */

		// Calcul des personnes soumises à l'EAE
		// on cherche toutes les personnes en affectation sur une FDP sur
		// l'année de la campagne
		// il faut que ces agents soient fonctionnaires ou contractuels
		// MAIRIE.SPADMN.CDPADM NOT IN ('CA','DC','DE','FC','LI','RF','RT','RV')
		// AND MAIRIE.SPCARR.CDCATE NOT IN (7, 9, 10, 11)
		SimpleDateFormat sdfMairie = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfSIRH = new SimpleDateFormat("yyyy-MM-dd");
		logger.info("Req AS400 : DEBUT listerAgentEligibleEAE sans détachés ");
		ArrayList<Carriere> listeCarrierePAActive = Carriere.listerCarriereActiveAvecPA(getTransaction(), sdfMairie.format(new Date()));
		String listeNomatr = Const.CHAINE_VIDE;
		for (Carriere carr : listeCarrierePAActive) {
			listeNomatr += carr.getNoMatricule() + ",";
		}
		if (!listeNomatr.equals(Const.CHAINE_VIDE)) {
			listeNomatr = listeNomatr.substring(0, listeNomatr.length() - 1);
		}
		ArrayList<AgentNW> la = AgentNW.listerAgentEligibleEAE(getTransaction(), listeNomatr, sdfSIRH.format(new Date()));
		logger.info("Req AS400 : FIN listerAgentEligibleEAE sans détachés : " + la.size());

		// Parcours des agents sans les détachés
		for (AgentNW a : la) {
			// Récupération de l'eae evntuel
			try {
				// logger.info("Req Oracle : chercherEAEAgent " +
				// a.getIdAgent());
				EAE eaeAgent = getEaeDao().chercherEAEAgent(Integer.valueOf(a.getIdAgent()), idCampagneEAE);
				// si on trouve un EAE dejà existant alors on ne fait rien
			} catch (Exception e) {
				// logger.info("Création de l'EAE pour l'agent : " +
				// a.getIdAgent());
				// Création de l'EAE
				EAE eae = new EAE();
				eae.setIdCampagneEAE(idCampagneEAE);
				eae.setDocumentAttache(false);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				eae.setDateCreation(null);

				// pour le CAP
				// on cherche si il y a une ligne dans les avancements
				// logger.info("Req AS400 : chercherAvancementAvecAnneeEtAgent");
				AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementFonctionnaireAvecAnneeEtAgent(getTransaction(),
						anneeCampagne.toString(), a.getIdAgent());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				if (avct != null && avct.getIdAvct() != null) {
					// on a trouvé une ligne dans avancement
					// on regarde l'etat de la ligne
					// si 'valid DRH' alors on met CAP à true;
					if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
						eae.setCap(true);
					} else {
						eae.setCap(false);
					}
				} else {
					eae.setCap(false);
				}

				eae.setEtat(EnumEtatEAE.NON_DEBUTE.getCode());
				// logger.info("Req Oracle : creerEAE " + a.getIdAgent());
				Integer idEaeCreer = getEaeDao().creerEAE(eae.getIdCampagneEAE(), eae.getEtat(), eae.isCap(), eae.isDocumentAttache(),
						eae.getDateCreation(), eae.getDateFin(), eae.getDateEntretien(), eae.getDureeEntretien(), eae.getDateFinalise(),
						eae.getDateControle(), eae.getHeureControle(), eae.getUserControle(), eae.getIdDelegataire());

				// on recupere le poste
				// logger.info("Req AS400 : chercherAffectationActiveAvecAgent");
				Affectation affAgent = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), a.getIdAgent());
				FichePoste fpPrincipale = null;
				FichePoste fpSecondaire = null;
				FichePoste fpResponsable = null;
				TitrePoste tpResp = null;
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					// logger.info("Req AS400 : chercherFichePoste (primaire)");
					fpPrincipale = FichePoste.chercherFichePoste(getTransaction(), affAgent.getIdFichePoste());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						// on recupere le superieur hierarchique
						if (fpPrincipale.getIdResponsable() != null) {
							// logger.info("Req AS400 : chercherAffectationAvecFP (Superieur)");
							Affectation affSuperieur = Affectation.chercherAffectationAvecFP(getTransaction(), fpPrincipale.getIdResponsable());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							}
							if (affSuperieur.getIdFichePoste() != null) {
								// logger.info("Req AS400 : chercherFichePoste (Superieur)");
								fpResponsable = FichePoste.chercherFichePoste(getTransaction(), affSuperieur.getIdFichePoste());
								// logger.info("Req AS400 : chercherTitrePoste (Superieur)");
								tpResp = TitrePoste.chercherTitrePoste(getTransaction(), fpResponsable.getIdTitrePoste());
							}
						}
					}
					if (affAgent.getIdFichePosteSecondaire() != null) {
						// logger.info("Req AS400 : chercherFichePoste (secondaire)");
						fpSecondaire = FichePoste.chercherFichePoste(getTransaction(), affAgent.getIdFichePosteSecondaire());
					}
				}

				// on crée les FDP et activites/competences
				// logger.info("Req Oracle : chercherEAE (celui crée )");
				EAE eaeCree = getEaeDao().chercherEAE(idEaeCreer);
				setEaeCourant(eaeCree);
				// on met les données dans EAE-evalué
				// logger.info("Req Oracle : Insert table EAE-evalué");
				performCreerEvalue(request, a, true, false, true);
				// on met les données dans EAE-FichePoste
				// logger.info("Req Oracle : Insert table EAE_FDP");
				performCreerFichePostePrincipale(request, fpPrincipale, eaeCree, true, true);
				performCreerFichePosteSecondaire(request, fpSecondaire, eaeCree);
				// on met les données dans EAE-FDP-Activites
				// logger.info("Req Oracle : Insert table EAE-FDP-Activites");
				performCreerActivitesFichePostePrincipale(request, fpPrincipale);
				performCreerActivitesFichePosteSecondaire(request, fpSecondaire);
				performCreerCompetencesFichePostePrincipale(request, fpPrincipale);
				performCreerCompetencesFichePosteSecondaire(request, fpSecondaire);
				// logger.info("Req AS400 : chercherAgent");
				// on met les données dans EAE-Diplome
				// logger.info("Req Oracle : Insert table EAE-Diplome");
				performCreerDiplome(request, a);
				// on met les données dans EAE-Parcours-Pro
				// logger.info("Req Oracle : Insert table EAE-Parcours-Pro");
				performCreerParcoursPro(request, a);
				// on met les données dans EAE-Formation
				// logger.info("Req Oracle : Insert table EAE-Formation");
				performCreerFormation(request, a);

				// on met à jour l'etat de l'eAE
				// logger.info("Req Oracle : chercherEaeFichePoste");
				EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(eaeCree.getIdEAE(), true);
				if (eaeFDP.getIdSHD() == null || eaeFDP.getIdSHD() == 0) {
					// logger.info("Req Oracle : modifierEtat");
					getEaeDao().modifierEtat(eaeCree.getIdEAE(), EnumEtatEAE.NON_AFFECTE.getCode());
				}

				// on créer les evaluateurs
				if (eaeFDP.getIdSHD() != null && eaeFDP.getIdSHD() != 0 && tpResp != null) {
					// logger.info("Req AS400 : chercherAgent (evaluateur)");
					AgentNW agentResp = AgentNW.chercherAgent(getTransaction(), eaeFDP.getIdSHD().toString());
					EaeEvaluateur eval = new EaeEvaluateur();
					eval.setIdEae(idEaeCreer);
					eval.setIdAgent(Integer.valueOf(agentResp.getIdAgent()));
					eval.setFonction(tpResp.getLibTitrePoste());
					eval.setDateEntreeCollectivite(agentResp.getDateDerniereEmbauche() == null
							|| agentResp.getDateDerniereEmbauche().equals(Const.CHAINE_VIDE)
							|| agentResp.getDateDerniereEmbauche().equals(Const.DATE_NULL) ? null : sdf.parse(agentResp.getDateDerniereEmbauche()));
					// on cherche toutes les affectations sur la FDP du
					// responsable
					// on prend la date la plus ancienne
					if (fpResponsable != null && fpResponsable.getIdServi() != null) {
						// logger.info("Req AS400 : listerAffectationAvecFP (fonction evaluateur)");
						ArrayList<Affectation> listeAffectationSurMemeFDP = Affectation.listerAffectationAvecFPEtAgent(getTransaction(),
								fpResponsable, agentResp.getIdAgent());
						if (listeAffectationSurMemeFDP.size() > 0) {
							eval.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff() == null
									|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.CHAINE_VIDE)
									|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.DATE_NULL) ? null : sdf
									.parse(listeAffectationSurMemeFDP.get(0).getDateDebutAff()));
						}
						// on cherche toutes les affectations sur le meme
						// service et
						// on prend la date la plus ancienne
						// NB : pour les affectations successives
						// logger.info("Req AS400 : listerAffectationAgentAvecService (fonction evaluateur)");
						ArrayList<Affectation> listeAffectationService = Affectation.listerAffectationAgentAvecService(getTransaction(),
								agentResp.getIdAgent(), fpResponsable.getIdServi());
						String dateDebutService = null;
						for (int i = 0; i < listeAffectationService.size(); i++) {
							Affectation affCours = listeAffectationService.get(i);
							if (listeAffectationService.size() > i + 1) {
								if (listeAffectationService.get(i + 1) != null) {
									Affectation affPrecedente = listeAffectationService.get(i + 1);
									if (affCours.getDateDebutAff().equals(Services.ajouteJours(affPrecedente.getDateFinAff(), 1))) {
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
						eval.setDateEntreeService(dateDebutService == null || dateDebutService.equals(Const.CHAINE_VIDE) ? null : sdf
								.parse(dateDebutService));
					}
					// logger.info("Req Oracle : creerEaeEvaluateur");
					getEaeEvaluateurDao().creerEaeEvaluateur(eval.getIdEae(), eval.getIdAgent(), eval.getFonction(), eval.getDateEntreeService(),
							eval.getDateEntreeCollectivite(), eval.getDateEntreeFonction());
				}

			}
		}

		logger.info("Req AS400 : DEBUT listerAgentEligibleEAE détachés ");
		ArrayList<Carriere> listeCarrierePAActiveDetache = Carriere.listerCarriereActiveAvecPADetache(getTransaction(), sdfMairie.format(new Date()));
		String listeNomatrDetache = Const.CHAINE_VIDE;
		for (Carriere carr : listeCarrierePAActiveDetache) {
			listeNomatrDetache += carr.getNoMatricule() + ",";
		}
		if (!listeNomatrDetache.equals(Const.CHAINE_VIDE)) {
			listeNomatrDetache = listeNomatrDetache.substring(0, listeNomatrDetache.length() - 1);
		}
		ArrayList<AgentNW> laSuite = AgentNW.listerAgentWithListNomatr(getTransaction(), listeNomatrDetache);
		logger.info("Req AS400 : FIN listerAgentEligibleEAE détachés : " + laSuite.size());
		// Parcours des agents sans les détachés
		for (AgentNW a : laSuite) {
			// Récupération de l'eae evntuel
			try {
				// logger.info("Req Oracle : chercherEAEAgent " +
				// a.getIdAgent());
				EAE eaeAgent = getEaeDao().chercherEAEAgent(Integer.valueOf(a.getIdAgent()), idCampagneEAE);
				// si on trouve un EAE dejà existant alors on ne fait rien
			} catch (Exception e) {
				// logger.info("Création de l'EAE pour l'agent : " +
				// a.getIdAgent());
				// Création de l'EAE
				EAE eae = new EAE();
				eae.setIdCampagneEAE(idCampagneEAE);
				eae.setDocumentAttache(false);
				eae.setDateCreation(null);

				// pour le CAP
				// on cherche si il y a une ligne dans les avancements
				// logger.info("Req AS400 : chercherAvancementAvecAnneeEtAgent");
				AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementFonctionnaireAvecAnneeEtAgent(getTransaction(),
						anneeCampagne.toString(), a.getIdAgent());
				if (getTransaction().isErreur())
					getTransaction().traiterErreur();
				if (avct != null && avct.getIdAvct() != null) {
					// on a trouvé une ligne dans avancement
					// on regarde l'etat de la ligne
					// si 'valid DRH' alors on met CAP à true;
					if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
						eae.setCap(true);
					} else {
						eae.setCap(false);
					}
				} else {
					eae.setCap(false);
				}

				eae.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
				// logger.info("Req Oracle : creerEAE " + a.getIdAgent());
				Integer idEaeCreer = getEaeDao().creerEAE(eae.getIdCampagneEAE(), eae.getEtat(), eae.isCap(), eae.isDocumentAttache(),
						eae.getDateCreation(), eae.getDateFin(), eae.getDateEntretien(), eae.getDureeEntretien(), eae.getDateFinalise(),
						eae.getDateControle(), eae.getHeureControle(), eae.getUserControle(), eae.getIdDelegataire());

				// logger.info("Req Oracle : chercherEAE (celui crée )");
				EAE eaeCree = getEaeDao().chercherEAE(idEaeCreer);
				setEaeCourant(eaeCree);
				// logger.info("Req AS400 : chercherAgent");
				// on met les données dans EAE-evalué
				// logger.info("Req Oracle : Insert table EAE-evalué");
				performCreerEvalue(request, a, true, true, true);
				// on met les données dans EAE-Diplome
				// logger.info("Req Oracle : Insert table EAE-Diplome");
				performCreerDiplome(request, a);
				// on met les données dans EAE-Parcours-Pro
				// logger.info("Req Oracle : Insert table EAE-Parcours-Pro");
				performCreerParcoursPro(request, a);
				// on met les données dans EAE-Formation
				// logger.info("Req Oracle : Insert table EAE-Formation");
				performCreerFormation(request, a);

			}
		}

		setStatut(STATUT_MEME_PROCESS);
		logger.info("Sortie du calcul");
		return true;

	}

	private void initialiseAffichageListeEAE(HttpServletRequest request) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		for (int p = 0; p < getListeEAE().size(); p++) {
			EAE eae = (EAE) getListeEAE().get(p);
			Integer i = eae.getIdEAE();
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEAE());
			try {
				EaeFichePoste eaeFDP = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEAE(), true);
				addZone(getNOM_ST_DIRECTION(i),
						(eaeFDP.getDirectionServ() == null ? "&nbsp;" : eaeFDP.getDirectionServ()) + " <br> "
								+ (eaeFDP.getSectionServ() == null ? "&nbsp;" : eaeFDP.getSectionServ()) + " <br> "
								+ (eaeFDP.getServiceServ() == null ? "&nbsp;" : eaeFDP.getServiceServ()));
				if (eaeFDP.getIdSHD() != null) {
					AgentNW agentResp = AgentNW.chercherAgent(getTransaction(), eaeFDP.getIdSHD().toString());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
					if (agentResp != null && agentResp.getIdAgent() != null) {
						addZone(getNOM_ST_SHD(i), agentResp.getNomAgent() + " " + agentResp.getPrenomAgent() + " (" + agentResp.getNoMatricule()
								+ ") ");
					} else {
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
				evaluation = getEaeEvaluationDao().chercherEaeEvaluation(eae.getIdEAE());
			} catch (Exception e) {
				// on ne fait rien
			}
			AgentNW agentEAE = AgentNW.chercherAgent(getTransaction(), evalue.getIdAgent().toString());
			addZone(getNOM_ST_AGENT(i), agentEAE.getNomAgent() + " " + agentEAE.getPrenomAgent() + " (" + agentEAE.getNoMatricule() + ") ");
			addZone(getNOM_ST_STATUT(i), (evalue.getStatut() == null ? "&nbsp;" : evalue.getStatut()) + " <br> "
					+ (evalue.isAgentDetache() ? "oui" : "&nbsp;"));
			// on recupere les evaluateurs
			ArrayList<EaeEvaluateur> listeEvaluateur = getEaeEvaluateurDao().listerEvaluateurEAE(eae.getIdEAE());
			String eval = Const.CHAINE_VIDE;
			for (int j = 0; j < listeEvaluateur.size(); j++) {
				EaeEvaluateur evaluateur = listeEvaluateur.get(j);
				AgentNW agentevaluateur = AgentNW.chercherAgent(getTransaction(), evaluateur.getIdAgent().toString());
				eval += agentevaluateur.getNomAgent() + " " + agentevaluateur.getPrenomAgent() + " (" + agentevaluateur.getNoMatricule() + ") <br> ";
			}
			addZone(getNOM_ST_EVALUATEURS(i), eval.equals(Const.CHAINE_VIDE) ? "&nbsp;" : eval);
			if (eae.getIdDelegataire() != null) {
				AgentNW agentDelegataire = AgentNW.chercherAgent(getTransaction(), eae.getIdDelegataire().toString());
				addZone(getNOM_ST_DELEGATAIRE(i),
						agentDelegataire.getNomAgent() + " " + agentDelegataire.getPrenomAgent() + " (" + agentDelegataire.getNoMatricule() + ")");
			} else {
				addZone(getNOM_ST_DELEGATAIRE(i), "&nbsp;");
			}

			addZone(getNOM_ST_CAP(i), eae.isCap() ? "oui" : "&nbsp;");
			addZone(getNOM_ST_AVIS_SHD(i), evaluation == null || evaluation.getAvis_shd() == null ? "&nbsp;" : evaluation.getAvis_shd());
			addZone(getNOM_ST_EAE_JOINT(i), eae.isDocumentAttache() ? "oui" : "non");
			addZone(getNOM_ST_CONTROLE(i), EnumEtatEAE.getValueEnumEtatEAE(eae.getEtat()) + " <br> "
					+ (eae.getDateCreation() == null ? "&nbsp;" : sdf.format(eae.getDateCreation())) + " <br> "
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

		// Oracle
		if (getCampagneEAEDao() == null) {
			setCampagneEAEDao((CampagneEAEDao) context.getBean("campagneEAEDao"));
		}

		if (getEaeDao() == null) {
			setEaeDao((EAEDao) context.getBean("eaeDao"));
		}

		if (getEaeEvalueDao() == null) {
			setEaeEvalueDao((EaeEvalueDao) context.getBean("eaeEvalueDao"));
		}

		if (getEaeEvaluateurDao() == null) {
			setEaeEvaluateurDao((EaeEvaluateurDao) context.getBean("eaeEvaluateurDao"));
		}

		if (getEaeFDPActiviteDao() == null) {
			setEaeFDPActiviteDao((EaeFDPActiviteDao) context.getBean("eaeFDPActiviteDao"));
		}

		if (getEaeFDPCompetenceDao() == null) {
			setEaeFDPCompetenceDao((EaeFDPCompetenceDao) context.getBean("eaeFDPCompetenceDao"));
		}

		if (getEaeFichePosteDao() == null) {
			setEaeFichePosteDao((EaeFichePosteDao) context.getBean("eaeFichePosteDao"));
		}

		if (getEaeDiplomeDao() == null) {
			setEaeDiplomeDao((EaeDiplomeDao) context.getBean("eaeDiplomeDao"));
		}

		if (getEaeParcoursProDao() == null) {
			setEaeParcoursProDao((EaeParcoursProDao) context.getBean("eaeParcoursProDao"));
		}

		if (getEaeFormationDao() == null) {
			setEaeFormationDao((EaeFormationDao) context.getBean("eaeFormationDao"));
		}

		if (getEaeEvaluationDao() == null) {
			setEaeEvaluationDao((EaeEvaluationDao) context.getBean("eaeEvaluationDao"));
		}

		if (getEaeFinalisationDao() == null) {
			setEaeFinalisationDao((EaeFinalisationDao) context.getBean("eaeFinalisationDao"));
		}

		// AS400
		if (getFormationAgentDao() == null) {
			setFormationAgentDao((FormationAgentDao) context.getBean("formationAgentDao"));
		}

		if (getTitreFormationDao() == null) {
			setTitreFormationDao((TitreFormationDao) context.getBean("titreFormationDao"));
		}

		if (getCentreFormationDao() == null) {
			setCentreFormationDao((CentreFormationDao) context.getBean("centreFormationDao"));
		}
	}

	/**
	 * Initialisation des liste déroulantes.
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
		// Si liste détachés vide alors affectation
		if (getLB_DETACHE() == LBVide) {
			ArrayList<String> listeDetache = new ArrayList<String>();
			listeDetache.add("oui");
			listeDetache.add("non");
			setListeDetache(listeDetache);
			int[] tailles = { 15 };
			FormateListe aFormat = new FormateListe(tailles);
			for (ListIterator<String> list = listeDetache.listIterator(); list.hasNext();) {
				String detache = (String) list.next();
				String ligne[] = { detache };
				aFormat.ajouteLigne(ligne);
			}
			setLB_DETACHE(aFormat.getListeFormatee(true));
			addZone(getNOM_LB_DETACHE_SELECT(), Const.ZERO);
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

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
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
			if (testerParametre(request, getNOM_PB_CALCULER())) {
				return performPB_CALCULER(request);
			}

			// Si clic sur le bouton PB_GERER_EVALUATEUR
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_GERER_EVALUATEUR(eae.getIdEAE()))) {
					return performPB_GERER_EVALUATEUR(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT(eae.getIdEAE()))) {
					return performPB_RECHERCHER_AGENT(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT(eae.getIdEAE()))) {
					return performPB_SUPPRIMER_RECHERCHER_AGENT(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

			// Si clic sur le bouton PB_VALID_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_VALID_EAE(eae.getIdEAE()))) {
					return performPB_VALID_EAE(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_DEVALID_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DEVALID_EAE(eae.getIdEAE()))) {
					return performPB_DEVALID_EAE(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_METRE_A_JOUR_EAE
			if (testerParametre(request, getNOM_PB_METTRE_A_JOUR_EAE())) {
				return performPB_METTRE_A_JOUR_EAE(request);
			}

			// Si clic sur le bouton PB_DEFINALISE_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DEFINALISE_EAE(eae.getIdEAE()))) {
					return performPB_DEFINALISE_EAE(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_SUPP_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_SUPP_EAE(eae.getIdEAE()))) {
					return performPB_SUPP_EAE(request, eae.getIdEAE());
				}
			}

			// Si clic sur le bouton PB_DESUPP_EAE
			for (int i = 0; i < getListeEAE().size(); i++) {
				EAE eae = getListeEAE().get(i);
				if (testerParametre(request, getNOM_PB_DESUPP_EAE(eae.getIdEAE()))) {
					return performPB_DESUPP_EAE(request, eae.getIdEAE());
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
				if (testerParametre(request, getNOM_PB_CONSULTER_DOC(eae.getIdEAE()))) {
					return performPB_CONSULTER_DOC(request, eae.getIdEAE());
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
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_GERER_EVALUATEUR(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		ArrayList<AgentNW> listeEval = new ArrayList<AgentNW>();

		setEaeCourant(getEaeDao().chercherEAE(idEae));
		ArrayList<EaeEvaluateur> listeEvalEAE = getEaeEvaluateurDao().listerEvaluateurEAE(eaeCourant.getIdEAE());

		if (listeEvalEAE != null) {
			for (int i = 0; i < listeEvalEAE.size(); i++) {
				EaeEvaluateur eval = listeEvalEAE.get(i);
				AgentNW ag = AgentNW.chercherAgent(getTransaction(), eval.getIdAgent().toString());
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		// setMessage(Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		setCampagneCourante((CampagneEAE) getListeCampagneEAE().get(indiceCampagne));

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
		ArrayList<String> listeSousService = null;
		if (getVAL_ST_CODE_SERVICE().length() != 0) {

			// on recupere les sous-service du service selectionne
			Service serv = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			listeSousService = Service.listSousService(getTransaction(), serv.getSigleService());
		}

		// Recherche des eae de la campagne en fonction du CAP
		int indiceCAP = (Services.estNumerique(getVAL_LB_CAP_SELECT()) ? Integer.parseInt(getVAL_LB_CAP_SELECT()) : -1);
		String cap = Const.CHAINE_VIDE;
		if (indiceCAP > 0) {
			cap = getListeCAP().get(indiceCAP - 1);
		}

		// Recherche des eae de la campagne en fonction du détachés
		int indiceDetache = (Services.estNumerique(getVAL_LB_DETACHE_SELECT()) ? Integer.parseInt(getVAL_LB_DETACHE_SELECT()) : -1);
		String detach = Const.CHAINE_VIDE;
		if (indiceDetache > 0) {
			detach = getListeDetache().get(indiceDetache - 1);
		}

		// recuperation agent evaluateur
		AgentNW agentEvaluateur = null;
		if (getVAL_ST_AGENT_EVALUATEUR().length() != 0) {
			agentEvaluateur = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT_EVALUATEUR());
		}

		// recuperation agent evalue
		AgentNW agentEvalue = null;
		if (getVAL_ST_AGENT_EVALUE().length() != 0) {
			agentEvalue = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT_EVALUE());
		}

		// on affiche la liste des EAE avec le filtre
		ArrayList<EAE> listeEAE = getEaeDao().listerEAEPourCampagne(getCampagneCourante().getIdCampagneEAE(), etat, statut, listeSousService, cap,
				agentEvaluateur, agentEvalue, detach);
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
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CALCULER(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		if (getListeCampagneEAE() != null && getListeCampagneEAE().size() > 0) {
			int indiceCampagne = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			setCampagneCourante((CampagneEAE) getListeCampagneEAE().get(indiceCampagne));

			if (!initialiseListeEAE(request)) {
				return false;
			}

			// "INF202","Calcul effectué."
			setMessage(MessageUtils.getMessage("INF202"));
			return true;
		} else {
			return false;
		}
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

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
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		// On enlève l'agent selectionnée
		EAE eaeSelection = getEaeDao().chercherEAE(idEae);
		eaeSelection.setIdDelegataire(null);
		getEaeDao().modifierDelegataire(eaeSelection.getIdEAE(), eaeSelection.getIdDelegataire());
		// si EAE d'un détaché alors on repasse le statut a "NA"
		EaeEvalue eval = getEaeEvalueDao().chercherEaeEvalue(eaeSelection.getIdEAE());
		if (eval.isAgentDetache()) {
			eaeSelection.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
			getEaeDao().modifierEtat(eaeSelection.getIdEAE(), eaeSelection.getEtat());
		}
		// on réinitilise l'affichage
		performPB_FILTRER(request);
		return true;
	}

	private void performCreerFormation(HttpServletRequest request, AgentNW ag) throws Exception {
		ArrayList<FormationAgent> listFormationAgent = getFormationAgentDao().listerFormationAgent(Integer.valueOf(ag.getIdAgent()));
		Integer tailleListe = listFormationAgent.size();
		if (tailleListe > 6) {
			tailleListe = 6;
		}
		for (int i = 0; i < tailleListe; i++) {
			FormationAgent formation = listFormationAgent.get(i);
			TitreFormation titre = getTitreFormationDao().chercherTitreFormation(formation.getIdTitreFormation());
			CentreFormation centre = getCentreFormationDao().chercherCentreFormation(formation.getIdCentreFormation());
			EaeFormation form = new EaeFormation();
			form.setIdEAE(getEaeCourant().getIdEAE());
			form.setAnneeFormation(formation.getAnneeFormation());
			form.setDureeFormation(formation.getDureeFormation().toString() + " " + formation.getUniteDuree());
			form.setLibelleFormation(titre.getLibTitreFormation() + " - " + centre.getLibCentreFormation());
			getEaeFormationDao().creerEaeFormation(form.getIdEAE(), form.getAnneeFormation(), form.getDureeFormation(), form.getLibelleFormation());
		}
	}

	private void performCreerCompetencesFichePosteSecondaire(HttpServletRequest request, FichePoste fpSecondaire) throws Exception {
		ArrayList<Competence> listCompFDP = Competence.listerCompetenceAvecFP(getTransaction(), fpSecondaire);
		for (int i = 0; i < listCompFDP.size(); i++) {
			Competence comp = listCompFDP.get(i);
			TypeCompetence typComp = TypeCompetence.chercherTypeCompetence(getTransaction(), comp.getIdTypeCompetence());
			EaeFDPCompetence compEAE = new EaeFDPCompetence();
			compEAE.setIdEaeFDP(getIdCreerFichePosteSecondaire());
			compEAE.setTypeCompetence(EnumTypeCompetence.getValueEnumTypeCompetence(typComp.getIdTypeCompetence()));
			compEAE.setLibCompetence(comp.getNomCompetence());

			getEaeFDPCompetenceDao().creerEaeFDPCompetence(compEAE.getIdEaeFDP(), compEAE.getTypeCompetence(), compEAE.getLibCompetence());
		}
	}

	private void performCreerCompetencesFichePostePrincipale(HttpServletRequest request, FichePoste fpPrincipale) throws Exception {
		ArrayList<Competence> listCompFDP = Competence.listerCompetenceAvecFP(getTransaction(), fpPrincipale);
		for (int i = 0; i < listCompFDP.size(); i++) {
			Competence comp = listCompFDP.get(i);
			TypeCompetence typComp = TypeCompetence.chercherTypeCompetence(getTransaction(), comp.getIdTypeCompetence());
			EaeFDPCompetence compEAE = new EaeFDPCompetence();
			compEAE.setIdEaeFDP(getIdCreerFichePostePrimaire());
			compEAE.setTypeCompetence(EnumTypeCompetence.getValueEnumTypeCompetence(typComp.getIdTypeCompetence()));
			compEAE.setLibCompetence(comp.getNomCompetence());

			getEaeFDPCompetenceDao().creerEaeFDPCompetence(compEAE.getIdEaeFDP(), compEAE.getTypeCompetence(), compEAE.getLibCompetence());
		}
	}

	private void performCreerActivitesFichePostePrincipale(HttpServletRequest request, FichePoste fpPrincipale) throws Exception {
		// gère les activites
		ArrayList<Activite> listActFDP = Activite.listerActiviteAvecFP(getTransaction(), fpPrincipale);
		for (int i = 0; i < listActFDP.size(); i++) {
			Activite act = listActFDP.get(i);
			EaeFDPActivite acti = new EaeFDPActivite();
			acti.setIdEaeFDP(getIdCreerFichePostePrimaire());
			acti.setLibActivite(act.getNomActivite());

			getEaeFDPActiviteDao().creerEaeFDPActivite(acti.getIdEaeFDP(), acti.getLibActivite());
		}
	}

	private void performCreerActivitesFichePosteSecondaire(HttpServletRequest request, FichePoste fpSecondaire) throws Exception {
		// gère les activites
		ArrayList<Activite> listActFDP = Activite.listerActiviteAvecFP(getTransaction(), fpSecondaire);
		for (int i = 0; i < listActFDP.size(); i++) {
			Activite act = listActFDP.get(i);
			EaeFDPActivite acti = new EaeFDPActivite();
			acti.setIdEaeFDP(getIdCreerFichePosteSecondaire());
			acti.setLibActivite(act.getNomActivite());

			getEaeFDPActiviteDao().creerEaeFDPActivite(acti.getIdEaeFDP(), acti.getLibActivite());
		}
	}

	private void performCreerFichePosteSecondaire(HttpServletRequest request, FichePoste fpSecondaire, EAE eae) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// on traite la secondaire
		if (fpSecondaire != null) {
			// on supprime les lignes existantes si elles existent
			try {
				EaeFichePoste aSupp = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEAE(), false);
				// on recupere les activites/competences liées
				try {
					ArrayList<EaeFDPActivite> listActi = getEaeFDPActiviteDao().listerEaeFDPActivite(aSupp.getIdEaeFichePoste());
					for (int i = 0; i < listActi.size(); i++) {
						EaeFDPActivite a = listActi.get(i);
						getEaeFDPActiviteDao().supprimerEaeFDPActivite(a.getIdEaeFDPActivite());
					}
					ArrayList<EaeFDPCompetence> listComp = getEaeFDPCompetenceDao().listerEaeFDPCompetence(aSupp.getIdEaeFichePoste());
					for (int i = 0; i < listComp.size(); i++) {
						EaeFDPCompetence c = listComp.get(i);
						getEaeFDPCompetenceDao().supprimerEaeFDPCompetence(c.getIdEaeFDPCompetence());
					}
				} catch (Exception e) {
					// il n'y avait pas d'activites
				}
				getEaeFichePosteDao().supprimerEaeFichePoste(aSupp.getIdEaeFichePoste());
			} catch (Exception e) {
				// on ne fait rien
			}
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEAE());
			EaeFichePoste fichePosteEae = new EaeFichePoste();
			fichePosteEae.setIdEae(eae.getIdEAE());
			fichePosteEae.setIdSirhFichePoste(Integer.valueOf(fpSecondaire.getIdFichePoste()));
			if (fpSecondaire.getIdResponsable() != null) {
				AgentNW agentResp = AgentNW.chercherAgentAffecteFichePoste(getTransaction(), fpSecondaire.getIdResponsable());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				fichePosteEae.setIdSHD(agentResp == null || agentResp.getIdAgent() == null ? null : Integer.valueOf(agentResp.getIdAgent()));

			}
			fichePosteEae.setPrimaire(false);
			fichePosteEae.setCodeService(fpSecondaire.getIdServi());
			Service direction = Service.getDirection(getTransaction(), fpSecondaire.getIdServi());
			fichePosteEae.setDirectionServ(direction != null ? direction.getLibService() : null);
			Service service = Service.chercherService(getTransaction(), fpSecondaire.getIdServi());
			fichePosteEae.setServiceServ(service != null ? service.getLibService() : null);
			Service section = Service.getSection(getTransaction(), fpSecondaire.getIdServi());
			fichePosteEae.setSectionServ(section != null ? section.getLibService() : null);
			// pour l'emploi
			FicheEmploi fe = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), fpSecondaire, false);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (fe != null) {
				fichePosteEae.setEmploi(fe.getNomMetierEmploi());

			}
			// pour la fonction
			TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fpSecondaire.getIdTitrePoste());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				fichePosteEae.setFonction(tp.getLibTitrePoste());
			}
			// on cherche toutes les affectations sur la FDP
			// on prend la date la plus ancienne
			ArrayList<Affectation> listeAffectationSurMemeFDP = Affectation.listerAffectationAvecFPEtAgent(getTransaction(), fpSecondaire, evalue
					.getIdAgent().toString());
			if (listeAffectationSurMemeFDP.size() > 0) {
				fichePosteEae.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff() == null
						|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.CHAINE_VIDE) ? null : sdf
						.parse(listeAffectationSurMemeFDP.get(0).getDateDebutAff()));
			}
			// grade du poste
			Grade g = Grade.chercherGrade(getTransaction(), fpSecondaire.getCodeGrade());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				fichePosteEae.setGradePoste(g.getGrade());
			}
			EntiteGeo lieu = EntiteGeo.chercherEntiteGeo(getTransaction(), fpSecondaire.getIdEntiteGeo());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				fichePosteEae.setLocalisation(lieu.getLibEntiteGeo());
			}
			fichePosteEae.setMission(fpSecondaire.getMissions());
			if (fpSecondaire.getIdResponsable() != null) {
				FichePoste fpResp = FichePoste.chercherFichePoste(getTransaction(), fpSecondaire.getIdResponsable());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					TitrePoste tpResp = TitrePoste.chercherTitrePoste(getTransaction(), fpResp.getIdTitrePoste());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						fichePosteEae.setFonctionResponsable(tpResp.getLibTitrePoste());
					}
					Affectation affResp = Affectation.chercherAffectationActiveAvecFP(getTransaction(), fpResp.getIdFichePoste());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
					if (affResp != null && affResp.getIdAgent() != null) {
						AgentNW agentResp = AgentNW.chercherAgent(getTransaction(), affResp.getIdAgent());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						if (agentResp != null && agentResp.getIdAgent() != null) {
							fichePosteEae.setDateEntreeCollectiviteResponsable(agentResp.getDateDerniereEmbauche() == null
									|| agentResp.getDateDerniereEmbauche().equals(Const.CHAINE_VIDE) ? null : sdf.parse(agentResp
									.getDateDerniereEmbauche()));
						}

						// on cherche toutes les affectations sur la FDP du
						// responsable
						// on prend la date la plus ancienne
						if (fpResp != null && fpResp.getIdServi() != null) {
							ArrayList<Affectation> listeAffectationRespSurMemeFDP = Affectation.listerAffectationAvecFPEtAgent(getTransaction(),
									fpResp, agentResp.getIdAgent());
							if (listeAffectationRespSurMemeFDP.size() > 0) {
								fichePosteEae.setDateEntreeFonctionResponsable(listeAffectationRespSurMemeFDP.get(0).getDateDebutAff() == null
										|| listeAffectationRespSurMemeFDP.get(0).getDateDebutAff().equals(Const.CHAINE_VIDE) ? null : sdf
										.parse(listeAffectationRespSurMemeFDP.get(0).getDateDebutAff()));
							}
							// on cherche toutes les affectations sur le meme
							// service et
							// on prend la date la plus ancienne
							// NB : pour les affectations successives
							ArrayList<Affectation> listeAffectationRespService = Affectation.listerAffectationAgentAvecService(getTransaction(),
									agentResp.getIdAgent(), fpResp.getIdServi());
							String dateDebutService = null;
							for (int i = 0; i < listeAffectationRespService.size(); i++) {
								Affectation affCours = listeAffectationRespService.get(i);
								if (listeAffectationRespService.size() > i + 1) {
									if (listeAffectationRespService.get(i + 1) != null) {
										Affectation affPrecedente = listeAffectationRespService.get(i + 1);
										if (affCours.getDateDebutAff().equals(Services.ajouteJours(affPrecedente.getDateFinAff(), 1))) {
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
							fichePosteEae
									.setDateEntreeServiceResponsable(dateDebutService == null || dateDebutService.equals(Const.CHAINE_VIDE) ? null
											: sdf.parse(dateDebutService));
						}
					}
				}
			}

			// on créer la ligne
			// on recupere l'id
			Integer idCreer = getEaeFichePosteDao().getIdEaeFichePoste();
			setIdCreerFichePosteSecondaire(idCreer);

			getEaeFichePosteDao().creerEaeFichePoste(idCreer, fichePosteEae.getIdEae(), fichePosteEae.getIdSHD(), fichePosteEae.isPrimaire(),
					fichePosteEae.getDirectionServ(), fichePosteEae.getServiceServ(), fichePosteEae.getSectionServ(), fichePosteEae.getEmploi(),
					fichePosteEae.getFonction(), fichePosteEae.getDateEntreeFonction(), fichePosteEae.getGradePoste(),
					fichePosteEae.getLocalisation(), fichePosteEae.getMission(), fichePosteEae.getFonctionResponsable(),
					fichePosteEae.getDateEntreeServiceResponsable(), fichePosteEae.getDateEntreeCollectiviteResponsable(),
					fichePosteEae.getDateEntreeFonctionResponsable(), fichePosteEae.getCodeService(), fichePosteEae.getIdSirhFichePoste());

		}
	}

	private void performCreerFichePostePrincipale(HttpServletRequest request, FichePoste fpPrincipale, EAE eae, boolean modifDateFonction,
			boolean modeCreation) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EaeFichePoste fpModif = null;
		// on traite la principale
		if (fpPrincipale != null) {
			// on supprime les lignes existantes si elles existent
			try {
				fpModif = getEaeFichePosteDao().chercherEaeFichePoste(eae.getIdEAE(), true);
				// on recupere les activites/competence liées
				try {
					ArrayList<EaeFDPActivite> listActi = getEaeFDPActiviteDao().listerEaeFDPActivite(fpModif.getIdEaeFichePoste());
					for (int i = 0; i < listActi.size(); i++) {
						EaeFDPActivite a = listActi.get(i);
						getEaeFDPActiviteDao().supprimerEaeFDPActivite(a.getIdEaeFDPActivite());
					}

					ArrayList<EaeFDPCompetence> listComp = getEaeFDPCompetenceDao().listerEaeFDPCompetence(fpModif.getIdEaeFichePoste());
					for (int i = 0; i < listComp.size(); i++) {
						EaeFDPCompetence c = listComp.get(i);
						getEaeFDPCompetenceDao().supprimerEaeFDPCompetence(c.getIdEaeFDPCompetence());
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
					evalue = getEaeEvalueDao().chercherEaeEvalue(eae.getIdEAE());
				} catch (Exception e) {
					// on ne fait rien, seule la date d'entrée dans la fonction
					// ne sera pas renseignée.
				}
				fpModif.setIdEae(eae.getIdEAE());
				fpModif.setIdSirhFichePoste(Integer.valueOf(fpPrincipale.getIdFichePoste()));
				if (fpPrincipale.getIdResponsable() != null) {
					AgentNW agentResp = AgentNW.chercherAgentAffecteFichePoste(getTransaction(), fpPrincipale.getIdResponsable());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					}
					fpModif.setIdSHD(agentResp == null || agentResp.getIdAgent() == null ? null : Integer.valueOf(agentResp.getIdAgent()));

				}
				fpModif.setPrimaire(true);
				fpModif.setCodeService(fpPrincipale.getIdServi());
				Service direction = Service.getDirection(getTransaction(), fpPrincipale.getIdServi());
				fpModif.setDirectionServ(direction != null ? direction.getLibService() : null);
				Service service = Service.chercherService(getTransaction(), fpPrincipale.getIdServi());
				fpModif.setServiceServ(service != null ? service.getLibService() : null);
				Service section = Service.getSection(getTransaction(), fpPrincipale.getIdServi());
				fpModif.setSectionServ(section != null ? section.getLibService() : null);
				// pour l'emploi
				FicheEmploi fe = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), fpPrincipale, true);
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				if (fe != null) {
					fpModif.setEmploi(fe.getNomMetierEmploi());
				}
				// pour la fonction
				TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fpPrincipale.getIdTitrePoste());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					fpModif.setFonction(tp.getLibTitrePoste());
				}
				if (modifDateFonction && evalue != null) {
					// on cherche toutes les affectations sur la FDP
					// on prend la date la plus ancienne
					ArrayList<Affectation> listeAffectationSurMemeFDP = Affectation.listerAffectationAvecFPEtAgent(getTransaction(), fpPrincipale,
							evalue.getIdAgent().toString());
					if (listeAffectationSurMemeFDP.size() > 0) {
						fpModif.setDateEntreeFonction(listeAffectationSurMemeFDP.get(0).getDateDebutAff() == null
								|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.CHAINE_VIDE)
								|| listeAffectationSurMemeFDP.get(0).getDateDebutAff().equals(Const.DATE_NULL) ? null : sdf
								.parse(listeAffectationSurMemeFDP.get(0).getDateDebutAff()));
					}
				}
				// grade du poste
				Grade g = Grade.chercherGrade(getTransaction(), fpPrincipale.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					fpModif.setGradePoste(g.getGrade());
				}
				EntiteGeo lieu = EntiteGeo.chercherEntiteGeo(getTransaction(), fpPrincipale.getIdEntiteGeo());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					fpModif.setLocalisation(lieu.getLibEntiteGeo());
				}
				fpModif.setMission(fpPrincipale.getMissions());
				if (fpPrincipale.getIdResponsable() != null) {
					FichePoste fpResp = FichePoste.chercherFichePoste(getTransaction(), fpPrincipale.getIdResponsable());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						TitrePoste tpResp = TitrePoste.chercherTitrePoste(getTransaction(), fpResp.getIdTitrePoste());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						} else {
							fpModif.setFonctionResponsable(tpResp.getLibTitrePoste());
						}
						Affectation affResp = Affectation.chercherAffectationActiveAvecFP(getTransaction(), fpResp.getIdFichePoste());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						if (affResp != null && affResp.getIdAgent() != null) {
							AgentNW agentResp = AgentNW.chercherAgent(getTransaction(), affResp.getIdAgent());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							}
							if (agentResp != null && agentResp.getIdAgent() != null) {
								fpModif.setDateEntreeCollectiviteResponsable(agentResp.getDateDerniereEmbauche() == null
										|| agentResp.getDateDerniereEmbauche().equals(Const.CHAINE_VIDE)
										|| agentResp.getDateDerniereEmbauche().equals(Const.DATE_NULL) ? null : sdf.parse(agentResp
										.getDateDerniereEmbauche()));
							}

							// on cherche toutes les affectations sur la FDP du
							// responsable
							// on prend la date la plus ancienne
							if (fpResp != null && fpResp.getIdServi() != null) {
								ArrayList<Affectation> listeAffectationRespSurMemeFDP = Affectation.listerAffectationAvecFPEtAgent(getTransaction(),
										fpResp, agentResp.getIdAgent());
								if (listeAffectationRespSurMemeFDP.size() > 0) {
									fpModif.setDateEntreeFonctionResponsable(listeAffectationRespSurMemeFDP.get(0).getDateDebutAff() == null
											|| listeAffectationRespSurMemeFDP.get(0).getDateDebutAff().equals(Const.CHAINE_VIDE)
											|| listeAffectationRespSurMemeFDP.get(0).getDateDebutAff().equals(Const.DATE_NULL) ? null : sdf
											.parse(listeAffectationRespSurMemeFDP.get(0).getDateDebutAff()));
								}
								// on cherche toutes les affectations sur le
								// meme
								// service et
								// on prend la date la plus ancienne
								// NB : pour les affectations successives
								ArrayList<Affectation> listeAffectationRespService = Affectation.listerAffectationAgentAvecService(getTransaction(),
										agentResp.getIdAgent(), fpResp.getIdServi());
								String dateDebutService = null;
								for (int i = 0; i < listeAffectationRespService.size(); i++) {
									Affectation affCours = listeAffectationRespService.get(i);
									if (listeAffectationRespService.size() > i + 1) {
										if (listeAffectationRespService.get(i + 1) != null) {
											Affectation affPrecedente = listeAffectationRespService.get(i + 1);
											if (affCours.getDateDebutAff().equals(Services.ajouteJours(affPrecedente.getDateFinAff(), 1))) {
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
								fpModif.setDateEntreeServiceResponsable(dateDebutService == null || dateDebutService.equals(Const.CHAINE_VIDE) ? null
										: sdf.parse(dateDebutService));
							}
						}
					}

				}

				if (modeCreation) {
					// on créer la ligne
					// on recupere l'id
					Integer idCreer = getEaeFichePosteDao().getIdEaeFichePoste();
					setIdCreerFichePostePrimaire(idCreer);
					getEaeFichePosteDao().creerEaeFichePoste(idCreer, fpModif.getIdEae(), fpModif.getIdSHD(), fpModif.isPrimaire(),
							fpModif.getDirectionServ(), fpModif.getServiceServ(), fpModif.getSectionServ(), fpModif.getEmploi(),
							fpModif.getFonction(), fpModif.getDateEntreeFonction(), fpModif.getGradePoste(), fpModif.getLocalisation(),
							fpModif.getMission(), fpModif.getFonctionResponsable(), fpModif.getDateEntreeServiceResponsable(),
							fpModif.getDateEntreeCollectiviteResponsable(), fpModif.getDateEntreeFonctionResponsable(), fpModif.getCodeService(),
							fpModif.getIdSirhFichePoste());

				} else {
					setIdCreerFichePostePrimaire(fpModif.getIdEaeFichePoste());
					getEaeFichePosteDao().modifierEaeFichePoste(fpModif.getIdEaeFichePoste(), fpModif.getIdEae(), fpModif.getIdSHD(),
							fpModif.isPrimaire(), fpModif.getDirectionServ(), fpModif.getServiceServ(), fpModif.getSectionServ(),
							fpModif.getEmploi(), fpModif.getFonction(), fpModif.getDateEntreeFonction(), fpModif.getGradePoste(),
							fpModif.getLocalisation(), fpModif.getMission(), fpModif.getFonctionResponsable(),
							fpModif.getDateEntreeServiceResponsable(), fpModif.getDateEntreeCollectiviteResponsable(),
							fpModif.getDateEntreeFonctionResponsable(), fpModif.getCodeService(), fpModif.getIdSirhFichePoste());
				}

			}
		}
	}

	private void performCreerParcoursPro(HttpServletRequest request, AgentNW ag) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		ArrayList<Spmtsr> listSpmtsr = Spmtsr.listerSpmtsrAvecAgentOrderDateDeb(getTransaction(), ag);
		for (int i = 0; i < listSpmtsr.size(); i++) {
			Spmtsr sp = listSpmtsr.get(i);
			Service direction = Service.getDirection(getTransaction(), sp.getServi());
			Service serv = Service.chercherService(getTransaction(), sp.getServi());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (sp.getDatfin() == null || sp.getDatfin().equals(Const.ZERO) || sp.getDatfin().equals(Const.DATE_NULL)) {
				// on crée une ligne pour affectation
				EaeParcoursPro parcours = new EaeParcoursPro();
				parcours.setIdEAE(getEaeCourant().getIdEAE());
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
				String lib = direction == null ? Const.CHAINE_VIDE : direction.getLibService();
				lib += serv == null || serv.getLibService() == null ? Const.CHAINE_VIDE : " " + serv.getLibService();
				parcours.setLibelleParcoursPro(lib);
				getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(),
						parcours.getLibelleParcoursPro());
			} else {
				// on regarde si il y a des lignes suivantes
				Spmtsr spSuiv = Spmtsr.chercherSpmtsrAvecAgentEtDateDebut(getTransaction(), ag.getNoMatricule(),
						(Integer.valueOf(sp.getDatfin()) + 1));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// on crée une ligne pour administration
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEAE());
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
					String lib = direction == null ? Const.CHAINE_VIDE : direction.getLibService();
					lib += serv == null || serv.getLibService() == null ? Const.CHAINE_VIDE : " " + serv.getLibService();
					parcours.setLibelleParcoursPro(lib);
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(),
							parcours.getLibelleParcoursPro());
				} else {
					boolean fin = false;
					Integer dateSortie = null;
					if (sp.getDatfin() == null || sp.getDatfin().equals(Const.ZERO) || sp.getDatfin().equals(Const.DATE_NULL)) {
						Integer dateFinSP = Integer.valueOf(sp.getDatfin()) + 1;
						String anneeDateDebSpmtsr = dateFinSP.toString().substring(0, 4);
						String moisDateDebSpmtsr = dateFinSP.toString().substring(4, 6);
						String jourDateDebSpmtsr = dateFinSP.toString().substring(6, 8);
						String dateDebSpmtsr = jourDateDebSpmtsr + "/" + moisDateDebSpmtsr + "/" + anneeDateDebSpmtsr;
						dateSortie = Integer.valueOf(dateDebSpmtsr);
						fin = true;
					}
					while (!fin) {
						spSuiv = Spmtsr
								.chercherSpmtsrAvecAgentEtDateDebut(getTransaction(), ag.getNoMatricule(), dateSortie == null ? 0 : dateSortie);
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							fin = true;
						} else {
							Integer dateFinSP = Integer.valueOf(sp.getDatfin()) + 1;
							String anneeDateDebSpmtsr = dateFinSP.toString().substring(0, 4);
							String moisDateDebSpmtsr = dateFinSP.toString().substring(4, 6);
							String jourDateDebSpmtsr = dateFinSP.toString().substring(6, 8);
							String dateDebSpmtsr = jourDateDebSpmtsr + "/" + moisDateDebSpmtsr + "/" + anneeDateDebSpmtsr;
							dateSortie = Integer.valueOf(dateDebSpmtsr);
						}
					}
					// on crée la ligne
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEAE());
					String anneeDateDebSpmtsr = sp.getDatdeb().substring(0, 4);
					String moisDateDebSpmtsr = sp.getDatdeb().substring(4, 6);
					String jourDateDebSpmtsr = sp.getDatdeb().substring(6, 8);
					String dateDebSpmtsr = jourDateDebSpmtsr + "/" + moisDateDebSpmtsr + "/" + anneeDateDebSpmtsr;
					parcours.setDateDebut(sdf.parse(dateDebSpmtsr));
					parcours.setDateFin(dateSortie == null ? null : sdf.parse(dateSortie.toString()));
					String lib = direction == null ? Const.CHAINE_VIDE : direction.getLibService();
					lib += serv == null || serv.getLibService() == null ? Const.CHAINE_VIDE : " " + serv.getLibService();
					parcours.setLibelleParcoursPro(lib);
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(),
							parcours.getLibelleParcoursPro());

				}
			}
		}
		// sur autre administration
		ArrayList<AutreAdministrationAgent> listAutreAdmin = AutreAdministrationAgent.listerAutreAdministrationAgentAvecAgent(getTransaction(), ag);
		for (int i = 0; i < listAutreAdmin.size(); i++) {
			AutreAdministrationAgent admAgent = listAutreAdmin.get(i);
			AutreAdministration administration = AutreAdministration.chercherAutreAdministration(getTransaction(), admAgent.getIdAutreAdmin());
			if (admAgent.getDateSortie() == null || admAgent.getDateSortie().equals(Const.CHAINE_VIDE)
					|| admAgent.getDateSortie().equals(Const.DATE_NULL)) {
				// on crée une ligne pour administration
				EaeParcoursPro parcours = new EaeParcoursPro();
				parcours.setIdEAE(getEaeCourant().getIdEAE());
				parcours.setDateDebut(sdf.parse(admAgent.getDateEntree()));
				parcours.setDateFin(admAgent.getDateSortie() == null || admAgent.getDateSortie().equals(Const.CHAINE_VIDE)
						|| admAgent.getDateSortie().equals(Const.DATE_NULL) ? null : sdf.parse(admAgent.getDateSortie()));
				parcours.setLibelleParcoursPro(administration.getLibAutreAdmin());
				getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(),
						parcours.getLibelleParcoursPro());
			} else {
				// on regarde si il y a des lignes suivantes
				AutreAdministrationAgent admSuiv = AutreAdministrationAgent.chercherAutreAdministrationAgentDateDebut(getTransaction(),
						ag.getIdAgent(), Services.formateDateInternationale(admAgent.getDateSortie()));
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					// on crée une ligne pour administration
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEAE());
					parcours.setDateDebut(sdf.parse(admAgent.getDateEntree()));
					parcours.setDateFin(sdf.parse(admAgent.getDateSortie()));
					parcours.setLibelleParcoursPro(administration.getLibAutreAdmin());
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(),
							parcours.getLibelleParcoursPro());
				} else {
					boolean fin = false;
					String dateSortie = admSuiv.getDateSortie();
					while (!fin) {
						admSuiv = AutreAdministrationAgent.chercherAutreAdministrationAgentDateDebut(getTransaction(), ag.getIdAgent(), dateSortie);
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							fin = true;
						} else {
							dateSortie = admSuiv.getDateSortie();
						}
					}
					// on crée la ligne
					EaeParcoursPro parcours = new EaeParcoursPro();
					parcours.setIdEAE(getEaeCourant().getIdEAE());
					parcours.setDateDebut(sdf.parse(admAgent.getDateEntree()));
					parcours.setDateFin(sdf.parse(dateSortie));
					parcours.setLibelleParcoursPro(administration.getLibAutreAdmin());
					getEaeParcoursProDao().creerParcoursPro(parcours.getIdEAE(), parcours.getDateDebut(), parcours.getDateFin(),
							parcours.getLibelleParcoursPro());
				}
			}
		}
	}

	private void performCreerDiplome(HttpServletRequest request, AgentNW ag) throws Exception {
		ArrayList<DiplomeAgent> listDiploAgent = DiplomeAgent.listerDiplomeAgentAvecAgent(getTransaction(), ag);
		for (int i = 0; i < listDiploAgent.size(); i++) {
			DiplomeAgent d = listDiploAgent.get(i);
			TitreDiplome td = TitreDiplome.chercherTitreDiplome(getTransaction(), d.getIdTitreDiplome());
			SpecialiteDiplomeNW spe = SpecialiteDiplomeNW.chercherSpecialiteDiplomeNW(getTransaction(), d.getIdSpecialiteDiplome());
			EaeDiplome eaeDiplome = new EaeDiplome();
			eaeDiplome.setIdEae(getEaeCourant().getIdEAE());
			String anneeObtention = Const.CHAINE_VIDE;
			if (d.getDateObtention() != null && !d.getDateObtention().equals(Const.DATE_NULL) && !d.getDateObtention().equals(Const.CHAINE_VIDE)) {
				anneeObtention = d.getDateObtention().substring(6, d.getDateObtention().length());
			}
			eaeDiplome.setLibelleDiplome((anneeObtention.equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : anneeObtention + " : ")
					+ td.getLibTitreDiplome() + " " + spe.getLibSpeDiplome());
			getEaeDiplomeDao().creerEaeDiplome(eaeDiplome.getIdEae(), eaeDiplome.getLibelleDiplome());
		}
	}

	private void performCreerEvalue(HttpServletRequest request, AgentNW ag, boolean miseAjourDateAdministration, boolean agentDetach,
			boolean modeCreation) throws Exception {
		// cas de la modif
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EaeEvalue evalAModif = null;
		// on cherche la ligen de l'EAE Evalué
		try {
			evalAModif = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEAE());
		} catch (Exception e) {
			// on en fait rien
			evalAModif = new EaeEvalue();
		}
		if (evalAModif != null) {

			evalAModif.setIdEae(getEaeCourant().getIdEAE());
			evalAModif.setIdAgent(Integer.valueOf(ag.getIdAgent()));
			// on recupere l'affectation en cours
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), ag.getIdAgent());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}

			if (aff != null && aff.getIdFichePoste() != null) {
				FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), aff.getIdFichePoste());
				// on cherche toutes les affectations sur le meme
				// service et
				// on prend la date la plus ancienne
				// NB : pour les affectations successives
				ArrayList<Affectation> listeAffectationService = Affectation.listerAffectationAgentAvecService(getTransaction(), ag.getIdAgent(),
						fp.getIdServi());
				String dateDebutService = null;
				for (int i = 0; i < listeAffectationService.size(); i++) {
					Affectation affCours = listeAffectationService.get(i);
					if (listeAffectationService.size() > i + 1) {
						if (listeAffectationService.get(i + 1) != null) {
							Affectation affPrecedente = listeAffectationService.get(i + 1);
							if (affCours.getDateDebutAff().equals(Services.ajouteJours(affPrecedente.getDateFinAff(), 1))) {
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
				evalAModif.setDateEntreeService(dateDebutService == null || dateDebutService.equals(Const.CHAINE_VIDE) ? null : sdf
						.parse(dateDebutService));
			}

			evalAModif.setDateEntreeCollectivite(ag.getDateDerniereEmbauche() == null || ag.getDateDerniereEmbauche().equals(Const.CHAINE_VIDE)
					|| ag.getDateDerniereEmbauche().equals(Const.DATE_NULL) ? null : sdf.parse(ag.getDateDerniereEmbauche()));
			// on cherche la date la plus ancienne dans les carrieres pour le
			// statut
			// fonctionnaire
			Carriere carr = Carriere.chercherCarriereFonctionnaireAncienne(getTransaction(), ag.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			Date dateCarriere = null;
			if (carr != null && carr.getNoMatricule() != null) {
				dateCarriere = sdf.parse(carr.getDateDebut());
			}
			// idem dans la liste des autres administration
			AutreAdministrationAgent autreAdmin = AutreAdministrationAgent.chercherAutreAdministrationAgentFonctionnaireAncienne(getTransaction(),
					ag.getIdAgent());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			Date dateAutreAdmin = null;
			if (autreAdmin != null && autreAdmin.getIdAutreAdmin() != null) {
				dateAutreAdmin = sdf.parse(autreAdmin.getDateEntree());
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
			// on regarde si la date de l'EAE precedent est différente alors on
			// prend la date de l'EAE de l'année passée
			CampagneEAE campagneActuelle = getCampagneCourante();
			CampagneEAE campagnePrec = getCampagneEAEDao().chercherCampagneEAEAnnee(campagneActuelle.getAnnee() - 1);
			EAE eaeAnneePrec = getEaeDao().chercherEAEAgent(Integer.valueOf(ag.getIdAgent()), campagnePrec.getIdCampagneEAE());
			EaeEvalue ancienneValeur = getEaeEvalueDao().chercherEaeEvalue(eaeAnneePrec.getIdEAE());
			if (evalAModif.getDateEntreeFonctionnaire() != null) {
				if (ancienneValeur.getDateEntreeFonctionnaire() != null
						&& (evalAModif.getDateEntreeFonctionnaire().compareTo(ancienneValeur.getDateEntreeFonctionnaire()) != 0)) {
					evalAModif.setDateEntreeFonctionnaire(ancienneValeur.getDateEntreeFonctionnaire());
				}
			} else {
				if (ancienneValeur.getDateEntreeFonctionnaire() != null) {
					evalAModif.setDateEntreeFonctionnaire(ancienneValeur.getDateEntreeFonctionnaire());
				}
			}

			if (miseAjourDateAdministration) {
				// on cherche la date la plus ancienne dans les PA de
				// mairie.SPADMN
				PositionAdmAgent paAncienne = PositionAdmAgent.chercherPositionAdmAgentAncienne(getTransaction(), ag.getNoMatricule());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				Date dateSpadmnAncienne = null;
				if (paAncienne != null && paAncienne.getDatdeb() != null && !paAncienne.getDatdeb().equals(Const.ZERO)) {
					dateSpadmnAncienne = sdf.parse(paAncienne.getDatdeb());
				}
				// idem dans la liste des autres administration
				AutreAdministrationAgent autreAdminAncienne = AutreAdministrationAgent.chercherAutreAdministrationAgentAncienne(getTransaction(),
						ag.getIdAgent());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				Date dateAutreAdminAncienne = null;
				if (autreAdminAncienne != null && autreAdminAncienne.getIdAutreAdmin() != null) {
					dateAutreAdminAncienne = sdf.parse(autreAdminAncienne.getDateEntree());
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

			// on regarde si la date de l'EAE precedent est différente alors on
			// prend la date de l'EAE de l'année passée
			if (evalAModif.getDateEntreeAdministration() != null) {
				if (ancienneValeur.getDateEntreeAdministration() != null
						&& (evalAModif.getDateEntreeAdministration().compareTo(ancienneValeur.getDateEntreeAdministration()) != 0)) {
					evalAModif.setDateEntreeAdministration(ancienneValeur.getDateEntreeAdministration());
				}
			} else {
				if (ancienneValeur.getDateEntreeAdministration() != null) {
					evalAModif.setDateEntreeAdministration(ancienneValeur.getDateEntreeAdministration());
				}
			}

			Carriere carrCours = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), ag);
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			if (carrCours != null & carrCours.getNoMatricule() != null) {
				evalAModif.setStatut(Carriere.getStatutCarriereEAE(carrCours.getCodeCategorie()));
				if (evalAModif.getStatut().equals("A")) {
					evalAModif.setPrecisionStatut(StatutCarriere.chercherStatutCarriere(getTransaction(), carrCours.getCodeCategorie()).getLiCate());
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
						evalAModif.setNbMoisDureeMin(grade.getDureeMin().equals(Const.ZERO) ? null : Integer.valueOf(grade.getDureeMin()));
						evalAModif.setNbMoisDureeMoy(grade.getDureeMoy().equals(Const.ZERO) ? null : Integer.valueOf(grade.getDureeMoy()));
						evalAModif.setNbMoisDureeMax(grade.getDureeMax().equals(Const.ZERO) ? null : Integer.valueOf(grade.getDureeMax()));

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
				int nbJours = Services.compteJoursEntreDates(carrCours.getDateDebut(), "31/12/" + (getCampagneCourante().getAnnee() - 1));
				evalAModif.setAncienneteEchelonJours(nbJours > 0 ? nbJours - 1 : 0);
			}

			// on regarde dans l'avancement pour le nouveau grade, le nouvel
			// echelon
			// et la date d'avancement
			AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementAvecAnneeEtAgent(getTransaction(), getCampagneCourante()
					.getAnnee().toString(), ag.getIdAgent());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				if (!avct.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())) {
					// attention dans le cas des categorie 4 on a pas de date
					// moyenne avct
					evalAModif.setDateEffetAvct(avct.getDateAvctMoy() == null || avct.getDateAvctMoy().equals(Const.DATE_NULL)
							|| avct.getDateAvctMoy().equals(Const.CHAINE_VIDE) ? null : sdf.parse(avct.getDateAvctMoy()));
				}
				Grade gradeAvct = Grade.chercherGrade(getTransaction(), avct.getIdNouvGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					// on cherche la classe si elle existe
					String classeString = Const.CHAINE_VIDE;
					if (gradeAvct.getCodeClasse() != null && !gradeAvct.getCodeClasse().equals(Const.CHAINE_VIDE)) {
						Classe classe = Classe.chercherClasse(getTransaction(), gradeAvct.getCodeClasse());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						if (classe != null && classe.getLibClasse() != null) {
							classeString = classe.getLibClasse();
						}
					}
					evalAModif.setNouvGrade(gradeAvct.getGrade() + " " + classeString);
					if (gradeAvct.getCodeTava() != null && !gradeAvct.getCodeTava().equals(Const.CHAINE_VIDE)) {
						MotifAvancement motif = MotifAvancement.chercherMotifAvancement(getTransaction(), gradeAvct.getCodeTava());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
						}
						if (motif != null && motif.getCodeMotifAvct() != null) {
							evalAModif.setTypeAvct(motif.getCodeMotifAvct());
						}
					}
				}
				if (gradeAvct != null && gradeAvct.getCodeEchelon() != null) {
					Echelon echAvct = Echelon.chercherEchelon(getTransaction(), gradeAvct.getCodeEchelon());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
					} else {
						evalAModif.setNouvEchelon(echAvct.getLibEchelon());
					}
				}
			}

			// pour la PA
			PositionAdmAgent paCours = PositionAdmAgent.chercherPositionAdmAgentEnCoursAvecAgent(getTransaction(), ag.getNoMatricule());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				evalAModif.setPosition(paCours.getPositionAdmEAE(paCours.getCdpadm()));
			}

			evalAModif.setAgentDetache(agentDetach);
			if (modeCreation) {
				// enfin on créer la ligne
				getEaeEvalueDao().creerEaeEvalue(evalAModif.getIdEae(), evalAModif.getIdAgent(), evalAModif.getDateEntreeService(),
						evalAModif.getDateEntreeCollectivite(), evalAModif.getDateEntreeFonctionnaire(), evalAModif.getDateEntreeAdministration(),
						evalAModif.getStatut(), evalAModif.getAncienneteEchelonJours(), evalAModif.getCadre(), evalAModif.getCategorie(),
						evalAModif.getClassification(), evalAModif.getGrade(), evalAModif.getEchelon(), evalAModif.getDateEffetAvct(),
						evalAModif.getNouvGrade(), evalAModif.getNouvEchelon(), evalAModif.getPosition(), evalAModif.getTypeAvct(),
						evalAModif.getPrecisionStatut(), evalAModif.getNbMoisDureeMin(), evalAModif.getNbMoisDureeMoy(),
						evalAModif.getNbMoisDureeMax(), evalAModif.isAgentDetache());
			} else {
				getEaeEvalueDao().modifierEaeEvalue(evalAModif.getIdEae(), evalAModif.getIdAgent(), evalAModif.getDateEntreeService(),
						evalAModif.getDateEntreeCollectivite(), evalAModif.getDateEntreeFonctionnaire(), evalAModif.getDateEntreeAdministration(),
						evalAModif.getStatut(), evalAModif.getAncienneteEchelonJours(), evalAModif.getCadre(), evalAModif.getCategorie(),
						evalAModif.getClassification(), evalAModif.getGrade(), evalAModif.getEchelon(), evalAModif.getDateEffetAvct(),
						evalAModif.getNouvGrade(), evalAModif.getNouvEchelon(), evalAModif.getPosition(), evalAModif.getTypeAvct(),
						evalAModif.getPrecisionStatut(), evalAModif.getNbMoisDureeMin(), evalAModif.getNbMoisDureeMoy(),
						evalAModif.getNbMoisDureeMax(), evalAModif.isAgentDetache());
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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
		// On enlève le service selectionnée
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
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
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
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

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
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
	 * à cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
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
		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
		setEaeCourant(eaeCourant);
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());
		if (getVAL_CK_VALID_EAE(idEae).equals(getCHECKED_ON())) {

			// RG-EAE-6 --> mis au moment où on controle un EAE.
			//TODO
			//on cherche le document concerné
			try{
				String docFinalise = getEaeFinalisationDao().chercherDernierDocumentFinalise(idEae);
				//on fait appel au WS de Sharepoint pour mettre à jour les droits de l'évalué sur le document.
				SirhKiosqueWSConsumer t = new SirhKiosqueWSConsumer();
				KiosqueDto retour = t.setDroitEvalueEAE(docFinalise,false);
				if(retour.getStatus().equals("ko")){
					//TODO declarer erreur
					return false;
				}
			}catch(SirhKiosqueWSConsumerException e){
				//TODO declarer erreur
				return false;
			}
			
			// on cherche pour chaque EAE de la campagne si il y a une ligne
			// dans
			// Avanacement pourla meme année
			MotifAvancement motifRevalo = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "REVALORISATION");
			MotifAvancement motifAD = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "AVANCEMENT DIFFERENCIE");
			MotifAvancement motifPromo = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "PROMOTION");
			MotifAvancement motifTitu = MotifAvancement.chercherMotifAvancementByLib(getTransaction(), "TITULARISATION");
			EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEAE());
			AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementAvecAnneeEtAgent(getTransaction(), getCampagneCourante()
					.getAnnee().toString(), evalue.getIdAgent().toString());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				// "INF500",
				// "Aucun avancement n'a été trouvé pour cet EAE. Le motif et l'avis SHD 'nont pu être mis à jour.");
				setMessage(MessageUtils.getMessage("INF500"));
			}
			if (avct != null && avct.getIdAvct() != null) {
				if (avct.getGrade() != null) {
					Grade gradeAgent = Grade.chercherGrade(getTransaction(), avct.getGrade());
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						avct.setIdMotifAvct(null);
						avct.setAvisSHD(null);
					} else {
						String typeAvct = gradeAgent.getCodeTava();
						if (!typeAvct.equals(Const.CHAINE_VIDE)) {
							// on cherche le type avancement correspondant
							MotifAvancement motif = MotifAvancement.chercherMotifAvancement(getTransaction(), typeAvct);
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								avct.setIdMotifAvct(null);
								avct.setAvisSHD(null);
							} else {
								avct.setIdMotifAvct(motif.getIdMotifAvct());
								EaeEvaluation eval = getEaeEvaluationDao().chercherEaeEvaluation(getEaeCourant().getIdEAE());
								if (typeAvct.equals(motifRevalo.getIdMotifAvct())) {
									avct.setAvisSHD(eval.getAvisRevalorisation() == 1 ? "Favorable" : "Défavorable");
								} else if (typeAvct.equals(motifAD.getIdMotifAvct())) {
									avct.setAvisSHD(eval.getPropositionAvancement());
								} else if (typeAvct.equals(motifTitu.getIdMotifAvct())) {
									avct.setAvisSHD("MOY");
								} else if (typeAvct.equals(motifPromo.getIdMotifAvct())) {
									avct.setAvisSHD(eval.getAvisChangementClasse() == 1 ? "Favorable" : "Défavorable");
								} else {
									avct.setAvisSHD(null);
								}
							}
						} else {
							avct.setIdMotifAvct(null);
							avct.setAvisSHD(null);
						}
					}
				} else {
					avct.setIdMotifAvct(null);
					avct.setAvisSHD(null);
				}
				avct.modifierAvancement(getTransaction());

				if (getTransaction().isErreur())
					return false;

				// tout s'est bien passé
				commitTransaction();

			}

			// on met à jour le statut de l'EAE
			getEaeCourant().setEtat(EnumEtatEAE.CONTROLE.getCode());
			getEaeCourant().setDateControle(new Date());
			getEaeCourant().setHeureControle(heureAction);
			getEaeCourant().setUserControle(user.getUserName());
			getEaeDao().modifierControle(getEaeCourant().getIdEAE(), getEaeCourant().getDateControle(), getEaeCourant().getHeureControle(),
					getEaeCourant().getUserControle(), getEaeCourant().getEtat());
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
		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
		setEaeCourant(eaeCourant);
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String heureAction = sdf.format(new Date());

		// RG-EAE-6 --> mis au moment où on controle un EAE.
		//TODO
		//on cherche le document concerné
		try{
			String docFinalise = getEaeFinalisationDao().chercherDernierDocumentFinalise(idEae);
			//on fait appel au WS de Sharepoint pour mettre à jour les droits de l'évalué sur le document.
			SirhKiosqueWSConsumer t = new SirhKiosqueWSConsumer();
			KiosqueDto retour = t.setDroitEvalueEAE(docFinalise,true);
			if(retour.getStatus().equals("ko")){
				//TODO declarer erreur
				return false;
			}
		}catch(SirhKiosqueWSConsumerException e){
			//TODO declarer erreur
			return false;
		}
		
		EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEAE());
		AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementAvecAnneeEtAgent(getTransaction(), getCampagneCourante()
				.getAnnee().toString(), evalue.getIdAgent().toString());
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			// "INF500",
			// "Aucun avancement n'a été trouvé pour cet EAE. Le motif et l'avis SHD 'nont pu être mis à jour.");
			setMessage(MessageUtils.getMessage("INF500"));
		}

		if (avct != null && avct.getIdAvct() != null) {
			avct.setIdMotifAvct(null);
			avct.setAvisSHD(null);

			avct.modifierAvancement(getTransaction());

			// tout s'est bien passé
			commitTransaction();
		}

		// on met à jour le statut de l'EAE
		getEaeCourant().setEtat(EnumEtatEAE.FINALISE.getCode());
		getEaeCourant().setDateControle(new Date());
		getEaeCourant().setHeureControle(heureAction);
		getEaeCourant().setUserControle(user.getUserName());
		getEaeDao().modifierControle(getEaeCourant().getIdEAE(), getEaeCourant().getDateControle(), getEaeCourant().getHeureControle(),
				getEaeCourant().getUserControle(), getEaeCourant().getEtat());

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
	public boolean perform_METTRE_A_JOUR_EAE(HttpServletRequest request, EAE eaeChoisi) throws Exception {
		setEaeCourant(eaeChoisi);

		// on met à jour les tables utiles
		// RG-EAE-41
		EaeEvalue evalue = getEaeEvalueDao().chercherEaeEvalue(getEaeCourant().getIdEAE());
		AgentNW ag = AgentNW.chercherAgent(getTransaction(), evalue.getIdAgent().toString());
		// on cherche les FDP de l'agent
		Affectation affCours = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), ag.getIdAgent());
		FichePoste fpPrincipale = null;
		FichePoste fpSecondaire = null;
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
		} else {
			fpPrincipale = FichePoste.chercherFichePoste(getTransaction(), affCours.getIdFichePoste());
			if (affCours.getIdFichePosteSecondaire() != null) {
				fpSecondaire = FichePoste.chercherFichePoste(getTransaction(), affCours.getIdFichePosteSecondaire());
			}
		}
		// on met les données dans EAE-evalué
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
		ArrayList<EaeDiplome> listeEaeDiplome = getEaeDiplomeDao().listerEaeDiplome(getEaeCourant().getIdEAE());
		for (int j = 0; j < listeEaeDiplome.size(); j++) {
			EaeDiplome dip = listeEaeDiplome.get(j);
			getEaeDiplomeDao().supprimerEaeDiplome(dip.getIdEaeDiplome());
		}
		// on met les données dans EAE-Diplome
		// logger.info("Req Oracle : Insert table EAE-Diplome");
		performCreerDiplome(request, ag);

		// on supprime les parcours pro
		ArrayList<EaeParcoursPro> listeEaeParcoursPro = getEaeParcoursProDao().listerEaeParcoursPro(getEaeCourant().getIdEAE());
		for (int j = 0; j < listeEaeParcoursPro.size(); j++) {
			EaeParcoursPro parcours = listeEaeParcoursPro.get(j);
			getEaeParcoursProDao().supprimerEaeParcoursPro(parcours.getIdEaeParcoursPro());
		}
		// on met les données dans EAE-Parcours-Pro
		// logger.info("Req Oracle : Insert table EAE-Parcours-Pro");
		performCreerParcoursPro(request, ag);

		// on supprime les formations
		ArrayList<EaeFormation> listeEaeFormation = getEaeFormationDao().listerEaeFormation(getEaeCourant().getIdEAE());
		for (int j = 0; j < listeEaeFormation.size(); j++) {
			EaeFormation form = listeEaeFormation.get(j);
			getEaeFormationDao().supprimerEaeFormation(form.getIdEaeFormation());
		}
		// on met les données dans EAE-Formation
		// logger.info("Req Oracle : Insert table EAE-Formation");
		performCreerFormation(request, ag);

		// on met à jour le champ CAP
		// on cherche si il y a une ligne dans les avancements
		// logger.info("Req AS400 : chercherAvancementAvecAnneeEtAgent");
		AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancementFonctionnaireAvecAnneeEtAgent(getTransaction(),
				getCampagneCourante().getAnnee().toString(), evalue.getIdAgent().toString());
		if (getTransaction().isErreur())
			getTransaction().traiterErreur();
		EAE eae = getEaeCourant();
		if (avct != null && avct.getIdAvct() != null) {
			// on a trouvé une ligne dans avancement
			// on regarde l'etat de la ligne
			// si 'valid DRH' alors on met CAP à true;
			if (avct.getEtat().equals(EnumEtatAvancement.SGC.getValue())) {
				eae.setCap(true);
			} else {
				eae.setCap(false);
			}
		} else {
			eae.setCap(false);
		}
		getEaeDao().modifierCAP(getEaeCourant().getIdEAE(), eae.isCap());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_DEFINALISE_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);
		EAE eaeCourant = getEaeDao().chercherEAE(idEae);
		setEaeCourant(eaeCourant);

		getEaeCourant().setEtat(EnumEtatEAE.EN_COURS.getCode());
		getEaeDao().modifierEtat(getEaeCourant().getIdEAE(), getEaeCourant().getEtat());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_SUPP_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		EAE eaeSelection = getEaeDao().chercherEAE(idEae);
		// on supprime tous les evaluateurs existants
		ArrayList<EaeEvaluateur> evaluateursExistants = getEaeEvaluateurDao().listerEvaluateurEAE(eaeSelection.getIdEAE());
		for (int i = 0; i < evaluateursExistants.size(); i++) {
			EaeEvaluateur eval = evaluateursExistants.get(i);
			getEaeEvaluateurDao().supprimerEaeEvaluateur(eval.getIdEaeEvaluateur());
		}
		// on supprime le delegataire
		getEaeDao().modifierDelegataire(eaeSelection.getIdEAE(), null);
		// on met à jour le statut de l'EAE
		eaeSelection.setEtat(EnumEtatEAE.SUPPRIME.getCode());
		getEaeDao().modifierEtat(eaeSelection.getIdEAE(), eaeSelection.getEtat());

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
	public boolean performPB_DESUPP_EAE(HttpServletRequest request, int idEae) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		EAE eaeSelection = getEaeDao().chercherEAE(idEae);
		// on met à jour le statut de l'EAE
		eaeSelection.setEtat(EnumEtatEAE.NON_AFFECTE.getCode());
		getEaeDao().modifierEtat(eaeSelection.getIdEAE(), eaeSelection.getEtat());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT_EVALUATEUR(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT_EVALUE(HttpServletRequest request) throws Exception {
		setMessage(Const.CHAINE_VIDE);

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

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
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_MAJ Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_MAJ(int i) {
		return "NOM_CK_VALID_MAJ_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_VALID_MAJ Date de création : (21/11/11 09:55:36)
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
		// on recupere les lignes qui sont cochées pour mettre à jour
		int nbEae = 0;
		for (int j = 0; j < getListeEAE().size(); j++) {
			// on recupère la ligne concernée
			EAE eae = (EAE) getListeEAE().get(j);
			Integer i = eae.getIdEAE();
			// si la colonne mettre à jour est cochée
			if (getVAL_CK_VALID_MAJ(i).equals(getCHECKED_ON())) {
				setEaeCourant(eae);
				perform_METTRE_A_JOUR_EAE(request, eae);
				nbEae++;
			}

		}

		// pour reinitiliser l'affichage du tableau.
		performPB_FILTRER(request);

		// "INF203", "@ EAE(s) ont été mis à jour.");
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
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
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
		String finalisation = getEaeFinalisationDao().chercherDernierDocumentFinalise(eae.getIdEAE());
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

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
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
	 * Getter de la liste avec un lazy initialize : LB_DETACHE Date de création
	 * : (28/11/11)
	 * 
	 */
	private String[] getLB_DETACHE() {
		if (LB_DETACHE == null)
			LB_DETACHE = initialiseLazyLB();
		return LB_DETACHE;
	}

	/**
	 * Setter de la liste: LB_DETACHE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_DETACHE(String[] newLB_DETACHE) {
		LB_DETACHE = newLB_DETACHE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DETACHE Date de création
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_LB_DETACHE() {
		return "NOM_LB_DETACHE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DETACHE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_DETACHE_SELECT() {
		return "NOM_LB_DETACHE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DETACHE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_DETACHE() {
		return getLB_DETACHE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DETACHE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_DETACHE_SELECT() {
		return getZone(getNOM_LB_DETACHE_SELECT());
	}

	public ArrayList<String> getListeDetache() {
		return listeDetache == null ? new ArrayList<String>() : listeDetache;
	}

	public void setListeDetache(ArrayList<String> listeDetache) {
		this.listeDetache = listeDetache;
	}
}