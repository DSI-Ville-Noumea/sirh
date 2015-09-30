package nc.mairie.gestionagent.robot;

import java.util.Hashtable;

import nc.mairie.droits.process.OeDROITSGestion;
import nc.mairie.droits.process.OeDROITSKiosque;
import nc.mairie.droits.process.OeDROITSUtilisateurs;
import nc.mairie.gestionagent.process.OeCOMMUNESelection;
import nc.mairie.gestionagent.process.OeENFANTGestion;
import nc.mairie.gestionagent.process.OeSMConvocation;
import nc.mairie.gestionagent.process.OeSMHistorique;
import nc.mairie.gestionagent.process.OeVOIESelection;
import nc.mairie.gestionagent.process.absence.OeABSAlimentationMensuelle;
import nc.mairie.gestionagent.process.absence.OeABSRestitution;
import nc.mairie.gestionagent.process.absence.OeABSVisualisation;
import nc.mairie.gestionagent.process.agent.OeAGENTADMINISTRATIONGestion;
import nc.mairie.gestionagent.process.agent.OeAGENTAbsencesCompteur;
import nc.mairie.gestionagent.process.agent.OeAGENTAbsencesHisto;
import nc.mairie.gestionagent.process.agent.OeAGENTAbsencesSolde;
import nc.mairie.gestionagent.process.agent.OeAGENTAccidentTravail;
import nc.mairie.gestionagent.process.agent.OeAGENTActesDonneesPerso;
import nc.mairie.gestionagent.process.agent.OeAGENTActesHSCT;
import nc.mairie.gestionagent.process.agent.OeAGENTCarriere;
import nc.mairie.gestionagent.process.agent.OeAGENTCasierJud;
import nc.mairie.gestionagent.process.agent.OeAGENTCharge;
import nc.mairie.gestionagent.process.agent.OeAGENTContrat;
import nc.mairie.gestionagent.process.agent.OeAGENTDIPLOMEGestion;
import nc.mairie.gestionagent.process.agent.OeAGENTEae;
import nc.mairie.gestionagent.process.agent.OeAGENTEmploisAffHisto;
import nc.mairie.gestionagent.process.agent.OeAGENTEmploisAffectation;
import nc.mairie.gestionagent.process.agent.OeAGENTEmploisPoste;
import nc.mairie.gestionagent.process.agent.OeAGENTEnfantHomonyme;
import nc.mairie.gestionagent.process.agent.OeAGENTEtatCivil;
import nc.mairie.gestionagent.process.agent.OeAGENTHandicap;
import nc.mairie.gestionagent.process.agent.OeAGENTHomonyme;
import nc.mairie.gestionagent.process.agent.OeAGENTPosAdm;
import nc.mairie.gestionagent.process.agent.OeAGENTPrime;
import nc.mairie.gestionagent.process.agent.OeAGENTRecherche;
import nc.mairie.gestionagent.process.agent.OeAGENTRechercheDroitKiosque;
import nc.mairie.gestionagent.process.agent.OeAGENTRechercheDroitKiosqueAgentApprobateur;
import nc.mairie.gestionagent.process.agent.OeAGENTVisiteMed;
import nc.mairie.gestionagent.process.avancement.OeAVCTCampagneEAE;
import nc.mairie.gestionagent.process.avancement.OeAVCTCampagneGestionEAE;
import nc.mairie.gestionagent.process.avancement.OeAVCTCampagnePlanification;
import nc.mairie.gestionagent.process.avancement.OeAVCTCampagneTableauBord;
import nc.mairie.gestionagent.process.avancement.OeAVCTContractuels;
import nc.mairie.gestionagent.process.avancement.OeAVCTConvCol;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctArretes;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctCarrieres;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctDetaches;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctPrepaAvct;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctPrepaCAP;
import nc.mairie.gestionagent.process.avancement.OeAVCTMasseSalarialeContractuel;
import nc.mairie.gestionagent.process.avancement.OeAVCTMasseSalarialeConvention;
import nc.mairie.gestionagent.process.avancement.OeAVCTMasseSalarialeDetaches;
import nc.mairie.gestionagent.process.avancement.OeAVCTMasseSalarialeFonctionnaire;
import nc.mairie.gestionagent.process.avancement.OeAVCTSelectionActeurs;
import nc.mairie.gestionagent.process.avancement.OeAVCTSelectionEvaluateur;
import nc.mairie.gestionagent.process.avancement.OeAVCTSimulationContractuels;
import nc.mairie.gestionagent.process.avancement.OeAVCTSimulationConvCol;
import nc.mairie.gestionagent.process.avancement.OeAVCTSimulationDetaches;
import nc.mairie.gestionagent.process.avancement.OeAVCTSimulationFonctionnaires;
import nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA48;
import nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA52;
import nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA53;
import nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA54;
import nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA55;
import nc.mairie.gestionagent.process.election.OeELECSaisieCompteurAmicale;
import nc.mairie.gestionagent.process.organigramme.OeORGAGestion;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsence;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsenceCongesAnnuels;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsenceCongesExceptionnels;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAvancement;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGECarriere;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEDonneesPerso;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEElection;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEFicheEmploi;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEFichePoste;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEGrade;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEGradeRef;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEHSCT;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEJour;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEKiosque;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGEPointage;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGERecrutement;
import nc.mairie.gestionagent.process.parametre.OePARAMETRAGERubrique;
import nc.mairie.gestionagent.process.pointage.OePTGPayeurContractuels;
import nc.mairie.gestionagent.process.pointage.OePTGPayeurConvCol;
import nc.mairie.gestionagent.process.pointage.OePTGPayeurFonct;
import nc.mairie.gestionagent.process.pointage.OePTGSaisie;
import nc.mairie.gestionagent.process.pointage.OePTGSelectionAgent;
import nc.mairie.gestionagent.process.pointage.OePTGVentilationContractuels;
import nc.mairie.gestionagent.process.pointage.OePTGVentilationConvCol;
import nc.mairie.gestionagent.process.pointage.OePTGVentilationFonct;
import nc.mairie.gestionagent.process.pointage.OePTGVisualisation;
import nc.mairie.gestionagent.process.poste.OePOSTEActivationFDP;
import nc.mairie.gestionagent.process.poste.OePOSTEDuplicationFDP;
import nc.mairie.gestionagent.process.poste.OePOSTEEmploiSelection;
import nc.mairie.gestionagent.process.poste.OePOSTEFEActivite;
import nc.mairie.gestionagent.process.poste.OePOSTEFEActiviteSelection;
import nc.mairie.gestionagent.process.poste.OePOSTEFECompetence;
import nc.mairie.gestionagent.process.poste.OePOSTEFECompetenceSelection;
import nc.mairie.gestionagent.process.poste.OePOSTEFERechercheAvancee;
import nc.mairie.gestionagent.process.poste.OePOSTEFPRechercheAvancee;
import nc.mairie.gestionagent.process.poste.OePOSTEFPSpecificites;
import nc.mairie.gestionagent.process.poste.OePOSTEFicheEmploi;
import nc.mairie.gestionagent.process.poste.OePOSTEFichePoste;
import nc.mairie.gestionagent.process.poste.OePOSTESuiviRecrutement;
import nc.mairie.gestionagent.process.poste.OePOSTESupressionFDP;
import nc.mairie.robot.Robot;
import nc.mairie.robot.Testeur;
import nc.mairie.technique.BasicProcess;

/**
 * Insérez la description du type a cet endroit. Date de création : (28/10/02
 * 10:14:36)
 * 
 * 
 */
public class RobotAgent extends Robot {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Commentaire relatif au constructeur Robot.
	 */
	public RobotAgent() {
		super();
	}

	/**
	 * Insérez la description de la Méthode à cet endroit. Date de création :
	 * (28/10/02 10:16:34)
	 */
	public BasicProcess getDefaultProcess() {
		return new PersonnelMain();
	}

	/**
	 * Insérez la description de la Méthode à cet endroit. Date de création :
	 * (28/10/02 10:16:34)
	 */
	public BasicProcess getFirstProcess(String activite) throws Exception {

		// Module AGENT - Donnees personnelles
		if (activite.equals("AgentRecherche")) {
			return new OeAGENTRecherche();
		} else if (activite.equals("AgentCreation") || activite.equals("AgentEtatCivil")) {
			return new OeAGENTEtatCivil();
		} else if (activite.equals("AgentDeselection")) {
			return new PersonnelMain();
		} else if (activite.equals("EnfantGestion")) {
			return new OeENFANTGestion();
		} else if (activite.equals("AdministrationGestion")) {
			return new OeAGENTADMINISTRATIONGestion();
		} else if (activite.equals("AgentDiplomeGestion")) {
			return new OeAGENTDIPLOMEGestion();
		} else if (activite.equals("AgentCasierJud")) {
			return new OeAGENTCasierJud();
		} else if (activite.equals("AgentContrat")) {
			return new OeAGENTContrat();
		} else if (activite.equals("AgentActesDonneesPerso")) {
			return new OeAGENTActesDonneesPerso();
		} // Module AGENT - HSCT
		else if (activite.equals("HandicapGestion")) {
			return new OeAGENTHandicap();
		} else if (activite.equals("VisiteMedicaleGestion")) {
			return new OeAGENTVisiteMed();
		} else if (activite.equals("AccidentTravailGestion")) {
			return new OeAGENTAccidentTravail();
		} else if (activite.equals("AgentActesHSCT")) {
			return new OeAGENTActesHSCT();
		} // Module AGENT - Emplois
		else if (activite.equals("AgtEmploisAffectations")) {
			return new OeAGENTEmploisAffectation();
		} else if (activite.equals("AgtEmploisPoste")) {
			return new OeAGENTEmploisPoste();
		} // Module AGENT - Elements de salaire
		else if (activite.equals("PrimeGestion")) {
			return new OeAGENTPrime();
		} else if (activite.equals("PAGestion")) {
			return new OeAGENTPosAdm();
		} else if (activite.equals("ChargeGestion")) {
			return new OeAGENTCharge();
		} else if (activite.equals("CarriereGestion")) {
			return new OeAGENTCarriere();
		} // Module AGENT - ABSENCES
		else if (activite.equals("AgtAbsencesSolde")) {
			return new OeAGENTAbsencesSolde();
		} else if (activite.equals("AgtAbsencesHisto")) {
			return new OeAGENTAbsencesHisto();
		} else if (activite.equals("AgtAbsencesCompteur")) {
			return new OeAGENTAbsencesCompteur();
		} // Module AGENT - EAE
		else if (activite.equals("AgtEae")) {
			return new OeAGENTEae();
		} // Module POSTE
		else if (activite.equals("FEGestion")) {
			return new OePOSTEFicheEmploi();
		} else if (activite.equals("FECompetence")) {
			return new OePOSTEFECompetence();
		} else if (activite.equals("FEActivite")) {
			return new OePOSTEFEActivite();
		} else if (activite.equals("FPGestion")) {
			return new OePOSTEFichePoste();
		} else if (activite.equals("SuiviRecrutement")) {
			return new OePOSTESuiviRecrutement();
		} else if (activite.equals("FEActiviteSelection")) {
			return new OePOSTEFEActiviteSelection();
		} else if (activite.equals("FECompetenceSelection")) {
			return new OePOSTEFECompetenceSelection();
		} else if (activite.equals("FPGestionAutomatise")) {
			return new OePOSTESupressionFDP();
		} else if (activite.equals("FPGestionAutomatiseActivation")) {
			return new OePOSTEActivationFDP();
		} else if (activite.equals("FPGestionAutomatiseDuplication")) {
			return new OePOSTEDuplicationFDP();
		} // Module AVANCEMENT
		else if (activite.equals("AVCTSimulationFontionnaires")) {
			return new OeAVCTSimulationFonctionnaires();
		} else if (activite.equals("AVCTSimulationContractuels")) {
			return new OeAVCTSimulationContractuels();
		} else if (activite.equals("AVCTSimulationConvCol")) {
			return new OeAVCTSimulationConvCol();
		} else if (activite.equals("AVCTSimulationDetaches")) {
			return new OeAVCTSimulationDetaches();
		} else if (activite.equals("AVCTFonctPrepaAvct")) {
			return new OeAVCTFonctPrepaAvct();
		} else if (activite.equals("AVCTFonctPrepaCAP")) {
			return new OeAVCTFonctPrepaCAP();
		} else if (activite.equals("AVCTFonctArretes")) {
			return new OeAVCTFonctArretes();
		} else if (activite.equals("AVCTFonctCarrieres")) {
			return new OeAVCTFonctCarrieres();
		} else if (activite.equals("AVCTContractuels")) {
			return new OeAVCTContractuels();
		} else if (activite.equals("AVCTConvCol")) {
			return new OeAVCTConvCol();
		} else if (activite.equals("AVCTDetaches")) {
			return new OeAVCTFonctDetaches();
		} else if (activite.equals("AVCTCampagneEAE")) {
			return new OeAVCTCampagneEAE();
		} else if (activite.equals("AVCTCampagnePlanification")) {
			return new OeAVCTCampagnePlanification();
		} else if (activite.equals("AVCTCampagneGestionEAE")) {
			return new OeAVCTCampagneGestionEAE();
		} else if (activite.equals("AVCTCampagneTableauBord")) {
			return new OeAVCTCampagneTableauBord();
		} else if (activite.equals("AVCTMasseSalariale")) {
			return new OeAVCTMasseSalarialeFonctionnaire();
		} else if (activite.equals("AVCTMasseSalarialeContr")) {
			return new OeAVCTMasseSalarialeContractuel();
		} else if (activite.equals("AVCTMasseSalarialeConv")) {
			return new OeAVCTMasseSalarialeConvention();
		} else if (activite.equals("AVCTMasseSalarialeDetaches")) {
			return new OeAVCTMasseSalarialeDetaches();
			
		} // Module SUIVI MEDICAL
		else if (activite.equals("SMConvocation")) {
			return new OeSMConvocation();
		} else if (activite.equals("SMHistorique")) {
			return new OeSMHistorique();
			
		} // Module POINTAGE
		else if (activite.equals("PTGSaisie")) {
			return new OePTGVisualisation();
		} else if (activite.equals("PTGVentilationConvCol")) {
			return new OePTGVentilationConvCol();
		} else if (activite.equals("PTGVentilationTitu")) {
			return new OePTGVentilationFonct();
		} else if (activite.equals("PTGVentilationNonTitu")) {
			return new OePTGVentilationContractuels();
		} else if (activite.equals("PTGPayeurConvCol")) {
			return new OePTGPayeurConvCol();
		} else if (activite.equals("PTGPayeurFonct")) {
			return new OePTGPayeurFonct();
		} else if (activite.equals("PTGPayeurContractuels")) {
			return new OePTGPayeurContractuels();
			
		}// Module ABSENCE
		else if (activite.equals("ABSVisualisation")) {
			return new OeABSVisualisation();
		} else if (activite.equals("ABSRestitution")) {
			return new OeABSRestitution();
		} else if (activite.equals("ABSAlimentationMensuelle")) {
			return new OeABSAlimentationMensuelle();
			
			// Module ELECTION
		} else if (activite.equals("ELECSaisieCompteurA48")) {
			return new OeELECSaisieCompteurA48();
		} else if (activite.equals("ELECSaisieCompteurA54")) {
			return new OeELECSaisieCompteurA54();
		} else if (activite.equals("ELECSaisieCompteurA55")) {
			return new OeELECSaisieCompteurA55();
		} else if (activite.equals("ELECSaisieCompteurAmicale")) {
			return new OeELECSaisieCompteurAmicale();
		} else if (activite.equals("ELECSaisieCompteurA53")) {
			return new OeELECSaisieCompteurA53();
		} else if (activite.equals("ELECSaisieCompteurA52")) {
			return new OeELECSaisieCompteurA52();
			
			// Modul ORGANIGRAMME
		} else if (activite.equals("gestionOrganigramme")) {
			return new OeORGAGestion();
		} // Module PARAMETRAGE - Postes et emplois
		else if (activite.equals("ParamFicheEmploi")) {
			return new OePARAMETRAGEFicheEmploi();
		} else if (activite.equals("ParamFichePoste")) {
			return new OePARAMETRAGEFichePoste();
		} else if (activite.equals("ParamRecrutement")) {
			return new OePARAMETRAGERecrutement();
		} // Module PARAMETRAGE - AGENT
		else if (activite.equals("ParamDonneesPerso")) {
			return new OePARAMETRAGEDonneesPerso();
		} else if (activite.equals("ParamHSCT")) {
			return new OePARAMETRAGEHSCT();
		} // Module PARAMETRAGE - GRADE
		else if (activite.equals("ParamGradeRef")) {
			return new OePARAMETRAGEGradeRef();
		} else if (activite.equals("ParamGrade")) {
			return new OePARAMETRAGEGrade();
		} // Module PARAMETRAGE - AVANCEMENT
		else if (activite.equals("ParamAvancement")) {
			return new OePARAMETRAGEAvancement();
		} // Module PARAMETRAGE - ELEMENT SALAIRE
		else if (activite.equals("ParamCarriere")) {
			return new OePARAMETRAGECarriere();
		} else if (activite.equals("ParamRubrique")) {
			return new OePARAMETRAGERubrique();
		} else if (activite.equals("ParamJour")) {
			return new OePARAMETRAGEJour();
		} // Module PARAMETRAGE - ABSENCE
		else if (activite.equals("ParamAbsMotif")) {
			return new OePARAMETRAGEAbsence();
		} else if (activite.equals("ParamAbsCongeExcep")) {
			return new OePARAMETRAGEAbsenceCongesExceptionnels();
		} else if (activite.equals("ParamAbsCongeAnnuel")) {
			return new OePARAMETRAGEAbsenceCongesAnnuels();
		} // Module PARAMETRAGE - ELECTIONS
		else if (activite.equals("ParamElec")) {
			return new OePARAMETRAGEElection();
			// Module PARAMETRAGE - KIOSQUE
		} else if (activite.equals("ParamKiosque")) {
			return new OePARAMETRAGEKiosque();
		} // Module PARAMETRAGE - POINTAGE
		else if (activite.equals("ParamPointage")) {
			return new OePARAMETRAGEPointage();
		}// Module DROITS
		else if (activite.equals("DroitsUtilisateur")) {
			return new OeDROITSUtilisateurs();
		} else if (activite.equals("DroitsProfil")) {
			return new OeDROITSGestion();
		} else if (activite.equals("DroitsKiosque")) {
			return new OeDROITSKiosque();
		} else {
			return null;
		}

		// throw new
		// Exception("Activite "+activite+" non déclarée dans le robot de navigation");

	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (28/10/2002
	 * 11:59:52)
	 * 
	 * @return Hashtable
	 */
	protected Hashtable<String, String> initialiseNavigation() {

		Hashtable<String, String> navigation = new Hashtable<String, String>();

		// ///////////////////
		// AGENT - Emplois //
		// ///////////////////
		navigation.put(OeAGENTEmploisAffectation.class.getName() + OeAGENTEmploisAffectation.STATUT_RECHERCHE_FP,
				OePOSTEFPRechercheAvancee.class.getName());
		navigation.put(OeAGENTEmploisAffectation.class.getName() + OeAGENTEmploisAffectation.STATUT_HISTORIQUE,
				OeAGENTEmploisAffHisto.class.getName());
		navigation.put(OeAGENTEmploisAffectation.class.getName()
				+ OeAGENTEmploisAffectation.STATUT_RECHERCHE_FP_SECONDAIRE, OePOSTEFPRechercheAvancee.class.getName());

		// Classe OeAGENTEtatCivil
		navigation.put(OeAGENTEtatCivil.class.getName() + OeAGENTEtatCivil.STATUT_LIEU_NAISS,
				OeCOMMUNESelection.class.getName());
		navigation
				.put(OeAGENTEtatCivil.class.getName() + OeAGENTEtatCivil.STATUT_VOIE, OeVOIESelection.class.getName());
		navigation.put(OeAGENTEtatCivil.class.getName() + OeAGENTEtatCivil.STATUT_AGT_HOMONYME,
				OeAGENTHomonyme.class.getName());

		// Classe OeEnfantGestion
		navigation.put(OeENFANTGestion.class.getName() + OeENFANTGestion.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeENFANTGestion.class.getName() + OeENFANTGestion.STATUT_AUTRE_PARENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeENFANTGestion.class.getName() + OeENFANTGestion.STATUT_LIEU_NAISS,
				OeCOMMUNESelection.class.getName());
		navigation.put(OeENFANTGestion.class.getName() + OeENFANTGestion.STATUT_ENFANT_HOMONYME,
				OeAGENTEnfantHomonyme.class.getName());

		// Classe OePOSTEFicheEmploi
		navigation.put(OePOSTEFicheEmploi.class.getName() + OePOSTEFicheEmploi.STATUT_ACTI_PRINC,
				OePOSTEFEActiviteSelection.class.getName());
		navigation.put(OePOSTEFicheEmploi.class.getName() + OePOSTEFicheEmploi.STATUT_COMPETENCE,
				OePOSTEFECompetenceSelection.class.getName());
		navigation.put(OePOSTEFicheEmploi.class.getName() + OePOSTEFicheEmploi.STATUT_RECHERCHE_AVANCEE,
				OePOSTEFERechercheAvancee.class.getName());

		// Classe OePOSTEFichePoste
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_COMPETENCE,
				OePOSTEFECompetenceSelection.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_ACTI_PRINC,
				OePOSTEFEActiviteSelection.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_RECHERCHE,
				OePOSTEFichePoste.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_DUPLIQUER,
				OePOSTEFichePoste.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_A_DUPLIQUER,
				OePOSTEFichePoste.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_RECHERCHE_AVANCEE,
				OePOSTEFPRechercheAvancee.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_EMPLOI_PRIMAIRE,
				OePOSTEEmploiSelection.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_EMPLOI_SECONDAIRE,
				OePOSTEEmploiSelection.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_SPECIFICITES,
				OePOSTEFPSpecificites.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_RESPONSABLE,
				OePOSTEFPRechercheAvancee.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_REMPLACEMENT,
				OePOSTEFPRechercheAvancee.class.getName());
		navigation.put(OePOSTEFichePoste.class.getName() + OePOSTEFichePoste.STATUT_FICHE_EMPLOI,
				OePOSTEFicheEmploi.class.getName());

		// Classe OePOSTESuiviRecrutement
		navigation.put(OePOSTESuiviRecrutement.class.getName() + OePOSTESuiviRecrutement.STATUT_RECHERCHE_FP,
				OePOSTEFPRechercheAvancee.class.getName());

		// Classe OePOSTEFPRechercheAvancee
		navigation.put(OePOSTEFPRechercheAvancee.class.getName() + OePOSTEFPRechercheAvancee.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAGENTRecherche
		navigation.put(OeAGENTRecherche.class.getName() + OeAGENTRecherche.STATUT_ETAT_CIVIL,
				OeAGENTEtatCivil.class.getName());

		// ///////////////////
		// AVANCEMENT //
		// ///////////////////
		// Classe OeAVCTSimulationConvCol
		navigation.put(OeAVCTSimulationConvCol.class.getName() + OeAVCTSimulationConvCol.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTSimulationContractuels
		navigation.put(OeAVCTSimulationContractuels.class.getName()
				+ OeAVCTSimulationContractuels.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());

		// Classe OeAVCTSimulationDetaches
		navigation.put(OeAVCTSimulationDetaches.class.getName() + OeAVCTSimulationDetaches.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTSimulationFonctionnaires
		navigation.put(OeAVCTSimulationFonctionnaires.class.getName()
				+ OeAVCTSimulationFonctionnaires.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());

		// Classe OeAVCTFonctPrepaAvct
		navigation.put(OeAVCTFonctPrepaAvct.class.getName() + OeAVCTFonctPrepaAvct.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTFonctPrepaCAP
		navigation.put(OeAVCTFonctPrepaCAP.class.getName() + OeAVCTFonctPrepaCAP.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTFonctArretes
		navigation.put(OeAVCTFonctArretes.class.getName() + OeAVCTFonctArretes.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTFonctCarrieres
		navigation.put(OeAVCTFonctCarrieres.class.getName() + OeAVCTFonctCarrieres.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTFonctPrepaAvct
		navigation.put(OeAVCTFonctDetaches.class.getName() + OeAVCTFonctDetaches.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTDetaches
		navigation.put(OeAVCTFonctDetaches.class.getName() + OeAVCTFonctDetaches.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe OeAVCTCampagnePlanification
		navigation.put(OeAVCTCampagnePlanification.class.getName()
				+ OeAVCTCampagnePlanification.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagnePlanification.class.getName() + OeAVCTCampagnePlanification.STATUT_DESTINATAIRE,
				OeAVCTSelectionActeurs.class.getName());

		// Classe OeAVCTCampagneGestionEAE
		navigation.put(OeAVCTCampagneGestionEAE.class.getName() + OeAVCTCampagneGestionEAE.STATUT_EVALUATEUR,
				OeAVCTSelectionEvaluateur.class.getName());
		navigation.put(OeAVCTCampagneGestionEAE.class.getName()
				+ OeAVCTCampagneGestionEAE.STATUT_RECHERCHER_AGENT_EVALUATEUR, OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagneGestionEAE.class.getName()
				+ OeAVCTCampagneGestionEAE.STATUT_RECHERCHER_AGENT_EVALUE, OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagneGestionEAE.class.getName() + OeAVCTCampagneGestionEAE.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// Classe SIMULATION
		navigation.put(OeAVCTMasseSalarialeContractuel.class.getName()
				+ OeAVCTMasseSalarialeContractuel.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTMasseSalarialeConvention.class.getName()
				+ OeAVCTMasseSalarialeConvention.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTMasseSalarialeFonctionnaire.class.getName()
				+ OeAVCTMasseSalarialeFonctionnaire.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTMasseSalarialeDetaches.class.getName()
				+ OeAVCTMasseSalarialeDetaches.STATUT_RECHERCHER_AGENT, OeAGENTRecherche.class.getName());

		// ///////////////////
		// SUIVI MEDICAL //
		// ///////////////////
		// Classe OeSMConvocation
		navigation.put(OeSMConvocation.class.getName() + OeSMConvocation.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());

		// ///////////////////
		// POINTAGE //
		// ///////////////////

		// Classe OePTGVisualisation
		navigation.put(OePTGVisualisation.class.getName() + OePTGVisualisation.STATUT_RECHERCHER_AGENT_MIN,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVisualisation.class.getName() + OePTGVisualisation.STATUT_RECHERCHER_AGENT_MAX,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVisualisation.class.getName() + OePTGVisualisation.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVisualisation.class.getName() + OePTGVisualisation.STATUT_SAISIE_PTG,
				OePTGSaisie.class.getName());

		// Classe OePTGVentilationFonct
		navigation.put(OePTGVentilationFonct.class.getName() + OePTGVentilationFonct.STATUT_AGENT,
				OePTGSelectionAgent.class.getName());
		navigation.put(OePTGVentilationFonct.class.getName() + OePTGVentilationFonct.STATUT_RECHERCHER_AGENT_MIN,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationFonct.class.getName() + OePTGVentilationFonct.STATUT_RECHERCHER_AGENT_MAX,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationFonct.class.getName() + OePTGVentilationFonct.STATUT_SAISIE_PTG,
				OePTGSaisie.class.getName());

		// Classe OePTGVentilationContractuels
		navigation.put(OePTGVentilationContractuels.class.getName() + OePTGVentilationContractuels.STATUT_AGENT,
				OePTGSelectionAgent.class.getName());
		navigation.put(OePTGVentilationContractuels.class.getName()
				+ OePTGVentilationContractuels.STATUT_RECHERCHER_AGENT_MIN, OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationContractuels.class.getName()
				+ OePTGVentilationContractuels.STATUT_RECHERCHER_AGENT_MAX, OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationContractuels.class.getName() + OePTGVentilationContractuels.STATUT_SAISIE_PTG,
				OePTGSaisie.class.getName());
		// Classe OePTGVentilationConvCol
		navigation.put(OePTGVentilationConvCol.class.getName() + OePTGVentilationConvCol.STATUT_AGENT,
				OePTGSelectionAgent.class.getName());
		navigation.put(OePTGVentilationConvCol.class.getName() + OePTGVentilationConvCol.STATUT_RECHERCHER_AGENT_MIN,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationConvCol.class.getName() + OePTGVentilationConvCol.STATUT_RECHERCHER_AGENT_MAX,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationConvCol.class.getName() + OePTGVentilationConvCol.STATUT_SAISIE_PTG,
				OePTGSaisie.class.getName());

		// ///////////////////
		// ABSENCE //
		// ///////////////////

		// Classe OeABSVisualisation
		navigation.put(OeABSVisualisation.class.getName() + OeABSVisualisation.STATUT_RECHERCHER_AGENT_DEMANDE,
				OeAGENTRecherche.class.getName());
		navigation.put(OeABSVisualisation.class.getName() + OeABSVisualisation.STATUT_RECHERCHER_AGENT_CREATION,
				OeAGENTRecherche.class.getName());

		// ///////////////////
		// ELECTION //
		// ///////////////////
		// Classe OeELECSaisieCompteurA48
		navigation.put(
				OeELECSaisieCompteurA48.class.getName() + OeELECSaisieCompteurA48.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());
		// Classe OeELECSaisieCompteurA54
		navigation.put(
				OeELECSaisieCompteurA54.class.getName() + OeELECSaisieCompteurA54.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());
		// Classe OeELECSaisieCompteurA55
		navigation.put(
				OeELECSaisieCompteurA55.class.getName() + OeELECSaisieCompteurA55.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());
		// Classe OeELECSaisieCompteurAmicale
		navigation.put(
				OeELECSaisieCompteurAmicale.class.getName() + OeELECSaisieCompteurAmicale.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());

		// ///////////////////
		// PARAMETRES //
		// ///////////////////
		// Classe OePARAMETRAGEKiosque
		navigation.put(OePARAMETRAGEKiosque.class.getName() + OePARAMETRAGEKiosque.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEKiosque.class.getName() + OePARAMETRAGEKiosque.STATUT_RECHERCHER_AGENT_GLOBAL,
				OeAGENTRecherche.class.getName());
		// Classe OeELECSaisieCompteurA52
		navigation.put(
				OeELECSaisieCompteurA52.class.getName() + OeELECSaisieCompteurA52.STATUT_RECHERCHER_AGENT_CREATE,
				OeAGENTRecherche.class.getName());

		// ///////////////////
		// DROITS //
		// ///////////////////
		// Classe OeDROITSKiosque
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_APPROBATEUR,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_DELEGATAIRE_PTG,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_DELEGATAIRE_ABS,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_RECHERCHER_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_VISEUR_APPROBATEUR_ABS,
				OeAGENTRechercheDroitKiosque.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_OPE_APPROBATEUR_ABS,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_APPROBATEUR_ABS,
				OeAGENTRechercheDroitKiosque.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_OPE_APPROBATEUR_PTG,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_APPROBATEUR_PTG,
				OeAGENTRechercheDroitKiosque.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_OPE_APPROBATEUR_PTG,
				OeAGENTRechercheDroitKiosqueAgentApprobateur.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_OPE_APPROBATEUR_ABS,
				OeAGENTRechercheDroitKiosqueAgentApprobateur.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_VISEUR_APPROBATEUR_ABS,
				OeAGENTRechercheDroitKiosqueAgentApprobateur.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_MAIRIE_APPROBATEUR_ABS,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + OeDROITSKiosque.STATUT_AGENT_MAIRIE_APPROBATEUR_PTG,
				OeAGENTRecherche.class.getName());

		// //////////////////////////////////////
		// pour la recherche d'un agent
		// données perso
		navigation.put(OeAGENTEtatCivil.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeENFANTGestion.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTDIPLOMEGestion.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTADMINISTRATIONGestion.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTContrat.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTCasierJud.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTActesDonneesPerso.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// hsct
		navigation.put(OeAGENTVisiteMed.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTAccidentTravail.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTHandicap.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTActesHSCT.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// emplois
		navigation.put(OeAGENTEmploisPoste.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTEmploisAffectation.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// element salaire
		navigation.put(OeAGENTPosAdm.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTCarriere.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTCharge.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation
				.put(OeAGENTPrime.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT, OeAGENTRecherche.class.getName());

		// ABSENCES
		navigation.put(OeAGENTAbsencesSolde.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTAbsencesHisto.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAGENTAbsencesCompteur.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// EAE
		navigation.put(OeAGENTEae.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT, OeAGENTRecherche.class.getName());

		// autres pages
		// FE
		navigation.put(OePOSTEFicheEmploi.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePOSTEFEActivite.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePOSTEFECompetence.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// FP
		navigation.put(OePOSTEFichePoste.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// AVCT
		navigation.put(OeAVCTContractuels.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTConvCol.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTFonctDetaches.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTFonctPrepaAvct.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTFonctPrepaCAP.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTFonctArretes.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTFonctCarrieres.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTSimulationFonctionnaires.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTSimulationContractuels.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTSimulationConvCol.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTSimulationDetaches.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagneEAE.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagnePlanification.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagneGestionEAE.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTCampagneTableauBord.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		// SIMU
		navigation.put(OeAVCTMasseSalarialeFonctionnaire.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTMasseSalarialeContractuel.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTMasseSalarialeConvention.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeAVCTMasseSalarialeDetaches.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// SM - Suivi Medical
		navigation.put(OeSMConvocation.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeSMHistorique.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// POINTAGE
		navigation.put(OePTGVisualisation.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationConvCol.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationFonct.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGVentilationContractuels.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGPayeurConvCol.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGPayeurFonct.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGPayeurContractuels.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePTGSaisie.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT, OeAGENTRecherche.class.getName());

		// ABSENCE
		navigation.put(OeABSVisualisation.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeABSRestitution.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeABSAlimentationMensuelle.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// ELECTION
		navigation.put(OeELECSaisieCompteurA48.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeELECSaisieCompteurA54.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeELECSaisieCompteurA55.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeELECSaisieCompteurAmicale.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeELECSaisieCompteurAmicale.class.getName());
		navigation.put(OeELECSaisieCompteurA53.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeELECSaisieCompteurA52.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// PARAM
		navigation.put(OePARAMETRAGEFicheEmploi.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEFichePoste.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGERecrutement.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEDonneesPerso.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEHSCT.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEGradeRef.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEGrade.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEAvancement.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGECarriere.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGERubrique.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEJour.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEAbsence.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEAbsenceCongesExceptionnels.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEElection.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEKiosque.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OePARAMETRAGEPointage.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// DROITS
		navigation.put(OeDROITSUtilisateurs.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSGestion.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());
		navigation.put(OeDROITSKiosque.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		// PERSONEL MAIN
		navigation.put(PersonnelMain.class.getName() + MaClasse.STATUT_RECHERCHE_AGENT,
				OeAGENTRecherche.class.getName());

		return navigation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.mairie.robot.Robot#initialiseTesteur()
	 */
	@Override
	protected Testeur initialiseTesteur() {
		return null;
	}
}
