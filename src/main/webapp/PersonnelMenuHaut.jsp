<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="nc.mairie.technique.VariableGlobale"%>
<%@page import="nc.mairie.technique.UserAppli"%>
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.gestionagent.servlets.ServletAgent"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<title>Menu Haut</title>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>

<%
UserAppli aUser= (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);
ArrayList<String> droits = aUser.getListeDroits();

String res = 	"<script language=\"javascript\">\n"+
		"var listeDroits = new Array(\n";
		res+="   \"construction\",\n";

for (int i = 0; i < droits.size(); i++){
	res+= "   \""+(String)droits.get(i)+"\"";
	if (i+1 < droits.size())
		res+=",\n";
	else	res+="\n";
}

res+=")</script>";
%>
<%=res%>

<script language="javascript" src="js/GestionMenuHaut.js">
</script>
<LINK rel="stylesheet" href="theme/menu.css" type="text/css">
</head>
<!--<BODY background="file:///X:/TestLucimages/fond_titre.jpg" onload="init();">-->
<BODY id="fondMenuHaut">
<FORM name="leForm" method="POST" target="Main" action="GestionAgentServlet"><INPUT type="hidden" name="ACTIVITE" value="">
<script language="javascript">

//***************************************************************
//*               Le menu haut Postes & emplois
//***************************************************************

var menuPosteEtEmploiFE = new MenuHaut("Module_posteEtEmploi_ficheEmploi");
menuPosteEtEmploiFE.ajouterFils(new Lien("FEGestion", "FICHE EMPLOI", "Gestion des emplois", true, false,"FICHE EMPLOI"));
menuPosteEtEmploiFE.ajouterFils(new Lien("FEActivite", "ACTIVITE", "Gestion des activit�s", true, false,"ACTIVITE"));
menuPosteEtEmploiFE.ajouterFils(new Lien("FECompetence", "COMPETENCE", "Gestion des comp�tences", true, false,"COMPETENCE"));
document.write(menuPosteEtEmploiFE.afficher());

//***************************************************************
//*               Le menu haut Agent
//***************************************************************

var menuAgentDonneesPerso = new MenuHaut("Module_agent_donneesPerso");
menuAgentDonneesPerso.ajouterFils(new Lien("AgentEtatCivil", "ETAT CIVIL", "Gestion de l'�tat civil d'un agent", true, false,"ETAT CIVIL"));
menuAgentDonneesPerso.ajouterFils(new Lien("EnfantGestion", "ENFANTS", "Gestion des enfants", true, false,"ENFANTS"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentDiplomeGestion", "DIPLOMES", "Gestion des dipl�mes", true, false,"DIPLOMES"));
menuAgentDonneesPerso.ajouterFils(new Lien("AdministrationGestion", "AUTRES ADMINISTRATIONS", "Gestion des autres administrations", true, false,"AUTRES ADMINISTRATIONS"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentContrat", "CONTRAT", "Gestion des contrats", true, false,"CONTRAT"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentCasierJud", "CASIER JUDICIAIRE", "Gestion des extraits de casier judiciaire", true, false,"CASIER JUDICIAIRE"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentActesDonneesPerso", "ACTES", "Gestion des documents d'un agent",true, false,"ACTES"));
document.write(menuAgentDonneesPerso.afficher());

var menuAgentHSCT = new MenuHaut("Module_agent_hsct");
menuAgentHSCT.ajouterFils(new Lien("VisiteMedicaleGestion", "VISITES MEDICALES", "Gestion des visites m�dicales d'un agent", true, false,"VISITES MEDICALES"));
menuAgentHSCT.ajouterFils(new Lien("AccidentTravailGestion", "ACCIDENTS DU TRAVAIL", "Gestions des accidents du travail d'un agent", true, false,"ACCIDENTS DE TRAVAIL"));
menuAgentHSCT.ajouterFils(new Lien("HandicapGestion", "HANDICAPS", "Gestion des handicaps pour un agent", true, false,"HANDICAPS"));
menuAgentHSCT.ajouterFils(new Lien("AgentActesHSCT", "ACTES", "Gestion des documents HSCT d'un agent",true, false,"ACTES"));
document.write(menuAgentHSCT.afficher());

var menuAgentEmplois = new MenuHaut("Module_agent_emplois");
menuAgentEmplois.ajouterFils(new Lien("AgtEmploisAffectations", "AFFECTATIONS", "Gestion des affectations de l'agent", true, false,"AFFECTATIONS"));
menuAgentEmplois.ajouterFils(new Lien("AgtEmploisPoste", "POSTE", "Informations sur le poste occup�", true, false,"POSTE"));
document.write(menuAgentEmplois.afficher());

var menuAgentEltsSalaires = new MenuHaut("Module_agent_eltsSalaires");
menuAgentEltsSalaires.ajouterFils(new Lien("PAGestion", "PA", "Gestion des P.A", true, false,"PA"));
menuAgentEltsSalaires.ajouterFils(new Lien("CarriereGestion", "CARRIERES", "Gestion des carri�res", true, false,"CARRIERES"));
menuAgentEltsSalaires.ajouterFils(new Lien("ChargeGestion", "CHARGES", "Gestion des charges", true, false,"CHARGES"));
menuAgentEltsSalaires.ajouterFils(new Lien("PrimeGestion", "PRIMES", "Gestion des primes", true, false,"PRIMES"));
document.write(menuAgentEltsSalaires.afficher());

var menuAgentAbsence = new MenuHaut("Module_agent_absences");
menuAgentAbsence.ajouterFils(new Lien("AgtAbsencesSolde", "ABS", "Solde des Absences", true, false,"SOLDE"));
menuAgentAbsence.ajouterFils(new Lien("AgtAbsencesHisto", "ABS", "Historique des Absences", true, false,"HISTORIQUE"));
menuAgentAbsence.ajouterFils(new Lien("AgtAbsencesCompteur", "ABS", "Compteur de r�cup�ration", true, false,"COMPTEUR"));
document.write(menuAgentAbsence.afficher());

var menuAgentEAE = new MenuHaut("Module_agent_eae");
menuAgentEAE.ajouterFils(new Lien("AgtEae", "EAE", "Gestion des EAE", true, false,"EAE DE L'AGENT"));
document.write(menuAgentEAE.afficher());

//***************************************************************
//*               Le menu haut Gestion des avancements
//***************************************************************
var menuAVCTAvancementFonctionnaire = new MenuHaut("Module_avct_simulationFonctionnaires");
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTSimulationFontionnaires", "SIMU FONCTIONNAIRES", "Simulation pour les fonctionnaires", true, false,"SIMULATION"));
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctPrepaAvct", "prepa avct", "Avancement des fonctionnaires", true, false,"PREPARATION AVANCEMENT"));
<% 
String affAvctCAP =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT_CAP");
if (affAvctCAP.equals("TRUE")){ %>
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctPrepaCAP", "prepa cap", "Avancement des fonctionnaires", true, false,"PREPARATION CAP"));
<%}%>
<% 
String affAvctArr =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT_ARR");
if (affAvctArr.equals("TRUE")){ %>
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctArretes", "arretes", "Avancement des fonctionnaires", true, false,"EDITION DES ARRETES"));
<%}%>
<% 
String affAvctCarr =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT_CARR");
if (affAvctCarr.equals("TRUE")){ %>
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctCarrieres", "carr", "Avancement des fonctionnaires", true, false,"MISE A JOUR DES CARRIERES"));
<%}%>
document.write(menuAVCTAvancementFonctionnaire.afficher());

var menuAVCTAvancementContractuels = new MenuHaut("Module_avct_simulationContractuels");
menuAVCTAvancementContractuels.ajouterFils(new Lien("AVCTSimulationContractuels", "SIMU CONTRACTUELS", "Simulation pour les contractuels", true, false,"SIMULATION"));
menuAVCTAvancementContractuels.ajouterFils(new Lien("AVCTContractuels", "CONTRACTUELS", "Avancement des contractuels", true, false,"CONTRACTUELS"));
<% 
String affTest =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_TEST");
if (affTest.equals("TRUE")){ %>
menuAVCTAvancementContractuels.ajouterFils(new Lien("AVCTTest", "test", "Page de test", true, false,"AVCT/TEST"));
<%}%>
document.write(menuAVCTAvancementContractuels.afficher());

var menuAVCTAvancementDetaches = new MenuHaut("Module_avct_simulationDetaches");
menuAVCTAvancementDetaches.ajouterFils(new Lien("AVCTSimulationDetaches", "SIMU DETACHES", "Simulation pour les fonctionnaires d�tach�s", true, false,"SIMULATION"));
menuAVCTAvancementDetaches.ajouterFils(new Lien("AVCTDetaches", "DETACHES", "Avancement des fonctionnaires d�tach�s", true, false,"DETACHES"));
document.write(menuAVCTAvancementDetaches.afficher());

var menuAVCTAvancementConvCol = new MenuHaut("Module_avct_simulationConvCol");
menuAVCTAvancementConvCol.ajouterFils(new Lien("AVCTSimulationConvCol", "SIMU CONVENTIONS", "Simulation pour les conventions collectives", true, false,"SIMULATION"));
menuAVCTAvancementConvCol.ajouterFils(new Lien("AVCTConvCol", "CONVENTIONS", "Avancement des conventions collectives", true, false,"CONVENTIONS"));
document.write(menuAVCTAvancementConvCol.afficher());

var menuAVCTCampagne = new MenuHaut("Module_avct_campagneEAE");
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagneEAE", "CAMPAGNES EAE", "Campagnes pour les EAE", true, false,"CAMPAGNE EAE"));
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagnePlanification", "PLANIF EAE", "Planification des EAE", true, false,"PLANIFICATION DE LA CAMPAGNE"));
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagneGestionEAE", "GESTION EAE", "Gestion des EAE", true, false,"GESTION DES EAE"));
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagneTableauBord", "TAB EAE", "Tableaux de bord EAE", true, false,"TABLEAU DE BORD"));
document.write(menuAVCTCampagne.afficher());

var menuAVCTMasseSalariale = new MenuHaut("Module_avct_simuMasseSalariale");
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalariale", "SIMU FONCT", "Simulation des fonctionnaires", true, false,"FONCTIONNAIRES"));
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalarialeContr", "SIMU CONTR", "Simulation des contractuels", true, false,"CONTRACTUELS"));
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalarialeConv", "SIMU CONV", "Simulation des conventions", true, false,"CONVENTIONS"));
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalarialeDetaches", "SIMU DETA", "Simulation des d�tach�s", true, false,"DETACHES"));
document.write(menuAVCTMasseSalariale.afficher());

//***************************************************************
//*               Le menu haut Suivi medical
//***************************************************************
var menuSMConvocation = new MenuHaut("Module_suiviMed_suiviMed");
menuSMConvocation.ajouterFils(new Lien("SMConvocation", "CONVOCATION", "Convocation pour le suivi m�dical", true, false,"CONVOCATION"));
menuSMConvocation.ajouterFils(new Lien("SMHistorique", "HISTORIQUE", "Historique du suivi m�dical", true, false,"HISTORIQUE"));
document.write(menuSMConvocation.afficher());

//***************************************************************
//*               Le menu haut Pointage
//***************************************************************
var menuPTGVentilation = new MenuHaut("Module_pointage_ventilationPointage");
menuPTGVentilation.ajouterFils(new Lien("PTGVentilationConvCol", "VENTILATION", "Ventilation et validation", true, false,"CONVENTIONS"));
menuPTGVentilation.ajouterFils(new Lien("PTGVentilationTitu", "VENTILATION", "Ventilation et validation", true, false,"FONCTIONNAIRES"));
menuPTGVentilation.ajouterFils(new Lien("PTGVentilationNonTitu", "VENTILATION", "Ventilation et validation", true, false,"CONTRACTUELS"));
document.write(menuPTGVentilation.afficher());


var menuPTGPayeur = new MenuHaut("Module_pointage_payeurPointage");
menuPTGPayeur.ajouterFils(new Lien("PTGPayeurConvCol", "PAYEUR", "Editions du payeur", true, false,"CONVENTIONS"));
menuPTGPayeur.ajouterFils(new Lien("PTGPayeurFonct", "PAYEUR", "Editions du payeur", true, false,"FONCTIONNAIRES"));
menuPTGPayeur.ajouterFils(new Lien("PTGPayeurContractuels", "PAYEUR", "Editions du payeur", true, false,"CONTRACTUELS"));
document.write(menuPTGPayeur.afficher());

//***************************************************************
//*               Le menu haut Election
//***************************************************************
var menuElecCompteur = new MenuHaut("Module_election_saisieCompteur");
menuElecCompteur.ajouterFils(new Lien("ELECSaisieCompteurA48", "COMPTEUR", "Compteur A48", true, false,"Bureau Directeur (A48)"));
menuElecCompteur.ajouterFils(new Lien("ELECSaisieCompteurA54", "COMPTEUR", "Compteur A54", true, false,"Congres et conseil syndical (A54)"));
menuElecCompteur.ajouterFils(new Lien("ELECSaisieCompteurA55", "COMPTEUR", "Compteur A55", true, false,"Delegation DP (A55)"));
menuElecCompteur.ajouterFils(new Lien("ELECSaisieCompteurA53", "COMPTEUR", "Compteur A53", true, false,"Formation Syndicale (A53)"));
menuElecCompteur.ajouterFils(new Lien("ELECSaisieCompteurA52", "COMPTEUR", "Compteur A52", true, false,"Decharge de service (A52)"));
document.write(menuElecCompteur.afficher());

//***************************************************************
//*               Le menu haut Param�tres
//***************************************************************
var menuParametragePosteEtEmploi = new MenuHaut("Module_parametres_posteEtEmploi");
menuParametragePosteEtEmploi.ajouterFils(new Lien("ParamFicheEmploi", "FICHE EMPLOI", "Gestion des param�tres des fiches emploi", true, false,"FICHE EMPLOI"));
menuParametragePosteEtEmploi.ajouterFils(new Lien("ParamFichePoste", "FICHE POSTE", "Gestion des param�tres des fiches de poste", true, false,"FICHE POSTE"));
menuParametragePosteEtEmploi.ajouterFils(new Lien("ParamRecrutement", "RECRUTEMENT", "Gestion des param�tres des recrutements", true, false,"RECRUTEMENT"));
document.write(menuParametragePosteEtEmploi.afficher());


var menuParametrageAgent = new MenuHaut("Module_parametres_agent");
menuParametrageAgent.ajouterFils(new Lien("ParamDonneesPerso", "DONNEES PERSONNELLES", "Gestion des param�tres donn�es personnelles", true, false,"DONNEES PERSONNELLES"));
menuParametrageAgent.ajouterFils(new Lien("ParamHSCT", "HSCT", "Gestion des param�tres HSCT", true, false,"HSCT"));
document.write(menuParametrageAgent.afficher());

var menuParametrageGrade = new MenuHaut("Module_parametres_grade");
menuParametrageGrade.ajouterFils(new Lien("ParamGradeRef", "REFERENCE", "Gestion des param�tres des grades", true, false,"REFERENCE"));
menuParametrageGrade.ajouterFils(new Lien("ParamGrade", "GRADES", "Gestion des grades", true, false,"GRADE"));
document.write(menuParametrageGrade.afficher());

var menuParametrageAvancement = new MenuHaut("Module_parametres_avancement");
menuParametrageAvancement.ajouterFils(new Lien("ParamAvancement", "AVANCEMENT", "Gestion des param�tres des avancements", true, false,"AVANCEMENT"));
document.write(menuParametrageAvancement.afficher());

var menuParametrageElemSal = new MenuHaut("Module_parametres_elemSal");
menuParametrageElemSal.ajouterFils(new Lien("ParamCarriere", "CARRIERE", "Gestion des param�tres des carri�res", true, false,"BASE HORAIRE"));
menuParametrageElemSal.ajouterFils(new Lien("ParamJour", "JOUR", "Gestion des param�tres des jours f�ri�s", true, false,"JOURS FERIES"));
document.write(menuParametrageElemSal.afficher());

var menuParametrageElemAbs = new MenuHaut("Module_parametres_absence");
menuParametrageElemAbs.ajouterFils(new Lien("ParamAbsMotif", "CARRIERE", "Gestion des param�tres des motifs pour le module absence", true, false,"MOTIFS"));
menuParametrageElemAbs.ajouterFils(new Lien("ParamAbsCongeExcep", "JOUR", "Gestion des param�tres des type de cong�s exceptionnels", true, false,"CONGES EXCEPTIONNELS"));
menuParametrageElemAbs.ajouterFils(new Lien("ParamAbsCongeAnnuel", "ANNUEL", "Gestion des param�tres des type de cong�s annuels", true, false,"CONGES ANNUELS"));
document.write(menuParametrageElemAbs.afficher());

var menuParametragePointage = new MenuHaut("Module_parametres_pointage");
menuParametragePointage.ajouterFils(new Lien("ParamPointage", "POINTAGE", "Gestion des param�tres des pointages", true, false,"MOTIFS"));
document.write(menuParametragePointage.afficher());

</script>

</FORM>
</BODY>
</html>
