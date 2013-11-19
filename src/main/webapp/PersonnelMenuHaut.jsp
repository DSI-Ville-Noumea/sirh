<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
<BODY class="fondMenuHaut">
<FORM name="leForm" method="POST" target="Main" action="GestionAgentServlet"><INPUT type="hidden" name="ACTIVITE" value="">
<script language="javascript">

//***************************************************************
//*               Le menu haut Postes & emplois
//***************************************************************

var menuPosteEtEmploiFE = new MenuHaut("Module_posteEtEmploi_ficheEmploi");
menuPosteEtEmploiFE.ajouterFils(new Lien("FEGestion", "FICHE EMPLOI", "Gestion des emplois", true, false,"PE/FE"));
menuPosteEtEmploiFE.ajouterFils(new Lien("FEActivite", "ACTIVITE", "Gestion des activités", true, false,"PE/Activite"));
menuPosteEtEmploiFE.ajouterFils(new Lien("FECompetence", "COMPETENCE", "Gestion des compétences", true, false,"PE/Competence"));
document.write(menuPosteEtEmploiFE.afficher());

//***************************************************************
//*               Le menu haut Agent
//***************************************************************

var menuAgentDonneesPerso = new MenuHaut("Module_agent_donneesPerso");
menuAgentDonneesPerso.ajouterFils(new Lien("AgentEtatCivil", "ETAT CIVIL", "Gestion de l'état civil d'un agent", true, false,"AGENT/PERSO/ETATCIVIL"));
menuAgentDonneesPerso.ajouterFils(new Lien("EnfantGestion", "ENFANTS", "Gestion des enfants", true, false,"AGENT/PERSO/ENFANT"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentDiplomeGestion", "DIPLOMES", "Gestion des diplômes", true, false,"AGENT/PERSO/DIPLOME"));
menuAgentDonneesPerso.ajouterFils(new Lien("AdministrationGestion", "AUTRES ADMINISTRATIONS", "Gestion des autres administrations", true, false,"AGENT/PERSO/ADMINISTRATION"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentContrat", "CONTRAT", "Gestion des contrats", true, false,"AGENT/PERSO/CONTRAT"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentCasierJud", "CASIER JUDICIAIRE", "Gestion des extraits de casier judiciaire", true, false,"AGENT/PERSO/CASIERJUD"));
menuAgentDonneesPerso.ajouterFils(new Lien("AgentActesDonneesPerso", "ACTES", "Gestion des documents d'un agent",true, false,"AGENT/PERSO/ACTE"));
document.write(menuAgentDonneesPerso.afficher());

var menuAgentHSCT = new MenuHaut("Module_agent_hsct");
menuAgentHSCT.ajouterFils(new Lien("VisiteMedicaleGestion", "VISITES MEDICALES", "Gestion des visites médicales d'un agent", true, false,"AGENT/HSCT/VM"));
menuAgentHSCT.ajouterFils(new Lien("AccidentTravailGestion", "ACCIDENTS DU TRAVAIL", "Gestions des accidents du travail d'un agent", true, false,"AGENT/HSCT/AT"));
menuAgentHSCT.ajouterFils(new Lien("HandicapGestion", "HANDICAPS", "Gestion des handicaps pour un agent", true, false,"AGENT/HSCT/HANDI"));
menuAgentHSCT.ajouterFils(new Lien("AgentActesHSCT", "ACTES", "Gestion des documents HSCT d'un agent",true, false,"AGENT/HSCT/ACTE"));
document.write(menuAgentHSCT.afficher());

var menuAgentEmplois = new MenuHaut("Module_agent_emplois");
menuAgentEmplois.ajouterFils(new Lien("AgtEmploisAffectations", "AFFECTATIONS", "Gestion des affectations de l'agent", true, false,"AGENT/EMPLOI/AFF"));
menuAgentEmplois.ajouterFils(new Lien("AgtEmploisPoste", "POSTE", "Informations sur le poste occupé", true, false,"AGENT/EMPLOI/POSTE"));
document.write(menuAgentEmplois.afficher());

var menuAgentEltsSalaires = new MenuHaut("Module_agent_eltsSalaires");
menuAgentEltsSalaires.ajouterFils(new Lien("PAGestion", "PA", "Gestion des P.A", true, false,"AGENT/SALAIRE/PA"));
menuAgentEltsSalaires.ajouterFils(new Lien("CarriereGestion", "CARRIERES", "Gestion des carrières", true, false,"AGENT/SALAIRE/CARR"));
menuAgentEltsSalaires.ajouterFils(new Lien("ChargeGestion", "CHARGES", "Gestion des charges", true, false,"AGENT/SALAIRE/CHARGE"));
menuAgentEltsSalaires.ajouterFils(new Lien("PrimeGestion", "PRIMES", "Gestion des primes", true, false,"AGENT/SALAIRE/PRIME"));
document.write(menuAgentEltsSalaires.afficher());

var menuAgentEAE = new MenuHaut("Module_agent_eae");
menuAgentEAE.ajouterFils(new Lien("AgtEae", "EAE", "Gestion des EAE", true, false,"AGENT/EAE/EAE"));
document.write(menuAgentEAE.afficher());

//***************************************************************
//*               Le menu haut Gestion des avancements
//***************************************************************
var menuAVCTAvancementFonctionnaire = new MenuHaut("Module_avct_simulationFonctionnaires");
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTSimulationFontionnaires", "SIMU FONCTIONNAIRES", "Simulation pour les fonctionnaires", true, false,"AVCT/SIMU"));
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctPrepaAvct", "prepa avct", "Avancement des fonctionnaires", true, false,"AVCT/PREPAAVCT"));
<% 
String affAvctCAP =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT_CAP");
if (affAvctCAP.equals("TRUE")){ %>
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctPrepaCAP", "prepa cap", "Avancement des fonctionnaires", true, false,"AVCT/PREPACAP"));
<%}%>
<% 
String affAvctArr =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT_ARR");
if (affAvctArr.equals("TRUE")){ %>
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctArretes", "arretes", "Avancement des fonctionnaires", true, false,"AVCT/ARRETES"));
<%}%>
<% 
String affAvctCarr =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT_CARR");
if (affAvctCarr.equals("TRUE")){ %>
menuAVCTAvancementFonctionnaire.ajouterFils(new Lien("AVCTFonctCarrieres", "carr", "Avancement des fonctionnaires", true, false,"AVCT/CARRIERES"));
<%}%>
document.write(menuAVCTAvancementFonctionnaire.afficher());

var menuAVCTAvancementContractuels = new MenuHaut("Module_avct_simulationContractuels");
menuAVCTAvancementContractuels.ajouterFils(new Lien("AVCTSimulationContractuels", "SIMU CONTRACTUELS", "Simulation pour les contractuels", true, false,"AVCT/SIMU"));
menuAVCTAvancementContractuels.ajouterFils(new Lien("AVCTContractuels", "CONTRACTUELS", "Avancement des contractuels", true, false,"AVCT/CONTR"));
<% 
String affTest =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_TEST");
if (affTest.equals("TRUE")){ %>
menuAVCTAvancementContractuels.ajouterFils(new Lien("AVCTTest", "test", "Page de test", true, false,"AVCT/TEST"));
<%}%>
document.write(menuAVCTAvancementContractuels.afficher());

var menuAVCTAvancementDetaches = new MenuHaut("Module_avct_simulationDetaches");
menuAVCTAvancementDetaches.ajouterFils(new Lien("AVCTSimulationDetaches", "SIMU DETACHES", "Simulation pour les fonctionnaires détachés", true, false,"AVCT/SIMU"));
menuAVCTAvancementDetaches.ajouterFils(new Lien("AVCTDetaches", "DETACHES", "Avancement des fonctionnaires détachés", true, false,"AVCT/DETA"));
document.write(menuAVCTAvancementDetaches.afficher());

var menuAVCTAvancementConvCol = new MenuHaut("Module_avct_simulationConvCol");
menuAVCTAvancementConvCol.ajouterFils(new Lien("AVCTSimulationConvCol", "SIMU CONVENTIONS", "Simulation pour les conventions collectives", true, false,"AVCT/SIMU"));
menuAVCTAvancementConvCol.ajouterFils(new Lien("AVCTConvCol", "CONVENTIONS", "Avancement des conventions collectives", true, false,"AVCT/CONV"));
document.write(menuAVCTAvancementConvCol.afficher());

var menuAVCTCampagne = new MenuHaut("Module_avct_campagneEAE");
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagneEAE", "CAMPAGNES EAE", "Campagnes pour les EAE", true, false,"AVCT/CAMPAGNE"));
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagnePlanification", "PLANIF EAE", "Planification des EAE", true, false,"AVCT/PLANIFICATION"));
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagneGestionEAE", "GESTION EAE", "Gestion des EAE", true, false,"AVCT/EAE"));
menuAVCTCampagne.ajouterFils(new Lien("AVCTCampagneTableauBord", "TAB EAE", "Tableaux de bord EAE", true, false,"AVCT/TB"));
document.write(menuAVCTCampagne.afficher());

var menuAVCTMasseSalariale = new MenuHaut("Module_avct_simuMasseSalariale");
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalariale", "SIMU FONCT", "Simulation des fonctionnaires", true, false,"AVCT/FONCT"));
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalarialeContr", "SIMU CONTR", "Simulation des contractuels", true, false,"AVCT/CONTR"));
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalarialeConv", "SIMU CONV", "Simulation des conventions", true, false,"AVCT/CONV"));
menuAVCTMasseSalariale.ajouterFils(new Lien("AVCTMasseSalarialeDetaches", "SIMU DETA", "Simulation des détachés", true, false,"AVCT/DETA"));
document.write(menuAVCTMasseSalariale.afficher());

//***************************************************************
//*               Le menu haut Suivi medical
//***************************************************************
var menuSMConvocation = new MenuHaut("Module_suiviMed_suiviMed");
menuSMConvocation.ajouterFils(new Lien("SMConvocation", "CONVOCATION", "Convocation pour le suivi médical", true, false,"SUIVI_MED/CONVOC"));
menuSMConvocation.ajouterFils(new Lien("SMHistorique", "HISTORIQUE", "Historique du suivi médical", true, false,"SUIVI_MED/HISTO"));
document.write(menuSMConvocation.afficher());

//***************************************************************
//*               Le menu haut Pointage
//***************************************************************
var menuPTGVentilation = new MenuHaut("Module_pointage_ventilationPointage");
menuPTGVentilation.ajouterFils(new Lien("PTGVentilationConvCol", "VENTILATION", "Ventilation et validation", true, false,"POINTAGE/VENT_CC"));
menuPTGVentilation.ajouterFils(new Lien("PTGVentilationTitu", "VENTILATION", "Ventilation et validation", true, false,"POINTAGE/VENT_TITU"));
menuPTGVentilation.ajouterFils(new Lien("PTGVentilationNonTitu", "VENTILATION", "Ventilation et validation", true, false,"POINTAGE/VENT_NON_TITU"));
document.write(menuPTGVentilation.afficher());


var menuPTGPayeur = new MenuHaut("Module_pointage_payeurPointage");
menuPTGPayeur.ajouterFils(new Lien("PTGPayeurConvCol", "PAYEUR", "Editions du payeur", true, false,"POINTAGE/PAY_CC"));
menuPTGPayeur.ajouterFils(new Lien("PTGPayeurFonct", "PAYEUR", "Editions du payeur", true, false,"POINTAGE/PAY_TITU"));
menuPTGPayeur.ajouterFils(new Lien("PTGPayeurContractuels", "PAYEUR", "Editions du payeur", true, false,"POINTAGE/PAY_NON_TITU"));
document.write(menuPTGPayeur.afficher());
//***************************************************************
//*               Le menu haut Paramètres
//***************************************************************
var menuParametragePosteEtEmploi = new MenuHaut("Module_parametres_posteEtEmploi");
menuParametragePosteEtEmploi.ajouterFils(new Lien("ParamFicheEmploi", "FICHE EMPLOI", "Gestion des paramètres des fiches emploi", true, false,"PARAM/FE"));
menuParametragePosteEtEmploi.ajouterFils(new Lien("ParamFichePoste", "FICHE POSTE", "Gestion des paramètres des fiches de poste", true, false,"PARAM/FP"));
menuParametragePosteEtEmploi.ajouterFils(new Lien("ParamRecrutement", "RECRUTEMENT", "Gestion des paramètres des recrutements", true, false,"PARAM/RECRUT"));
document.write(menuParametragePosteEtEmploi.afficher());


var menuParametrageAgent = new MenuHaut("Module_parametres_agent");
menuParametrageAgent.ajouterFils(new Lien("ParamDonneesPerso", "DONNEES PERSONNELLES", "Gestion des paramètres données personnelles", true, false,"PARAM/DONNEESPERSO"));
menuParametrageAgent.ajouterFils(new Lien("ParamHSCT", "HSCT", "Gestion des paramètres HSCT", true, false,"PARAM/HSCT"));
document.write(menuParametrageAgent.afficher());

var menuParametrageGrade = new MenuHaut("Module_parametres_grade");
menuParametrageGrade.ajouterFils(new Lien("ParamGradeRef", "REFERENCE", "Gestion des paramètres des grades", true, false,"PARAM/REF"));
menuParametrageGrade.ajouterFils(new Lien("ParamGrade", "GRADES", "Gestion des grades", true, false,"PARAM/GRADE"));
document.write(menuParametrageGrade.afficher());

var menuParametrageAvancement = new MenuHaut("Module_parametres_avancement");
menuParametrageAvancement.ajouterFils(new Lien("ParamAvancement", "AVANCEMENT", "Gestion des paramètres des avancements", true, false,"PARAM/AVCT"));
document.write(menuParametrageAvancement.afficher());

var menuParametrageElemSal = new MenuHaut("Module_parametres_elemSal");
menuParametrageElemSal.ajouterFils(new Lien("ParamCarriere", "CARRIERE", "Gestion des paramètres des carrières", true, false,"PARAM/BH"));
menuParametrageElemSal.ajouterFils(new Lien("ParamJour", "JOUR", "Gestion des paramètres des jours fériés", true, false,"PARAM/JOUR"));
document.write(menuParametrageElemSal.afficher());

</script>

</FORM>
</BODY>
</html>
