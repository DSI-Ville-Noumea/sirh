<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="nc.mairie.technique.VariableGlobale"%>
<%@page import="nc.mairie.technique.UserAppli"%>
<%@page import="nc.mairie.gestionagent.servlets.ServletAgent"%>
<%@page import="java.util.ArrayList"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Menu Personnel</TITLE>
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

<script language="javascript" src="js/GestionMenu.js">
</script>
<LINK rel="stylesheet" href="theme/menu.css" type="text/css">
</HEAD>
<BODY text="#000000" onload="preload();" style="cursor : auto;" class="menuGauche" >
<BASEFONT FACE="Arial" SIZE=2> 
<nobr>
<FORM name="leForm" method="POST" target="Main" action="GestionAgentServlet">
<INPUT type="hidden" name="ACTIVITE" value="">
<script>
var menu = new Menu();

//***************************************************************
//*               Le module Postes et emplois
//***************************************************************
var Module_posteEtEmploi = new Dossier("Module_posteEtEmploi", "Postes & emplois","POSTES ET EMPLOIS");
	<% 
	String affFicheEmploi =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_FICHE_EMPLOI");
	if (affFicheEmploi.equals("TRUE")){ %>
		Module_posteEtEmploi.ajouterFils(new Lien("ficheEmploi", "FEGestion", "Fiche emploi", "Gestion des fiches emploi", true));
	<%}%>
	Module_posteEtEmploi.ajouterFils(new Lien("fichePoste", "FPGestion", "Fiche de poste", "Gestion des fiches de poste", true));
	Module_posteEtEmploi.ajouterFils(new Lien("fichePosteAutomatise", "FPGestionAutomatise", "Routine organigramme", "Gestion des routine liées aux impacts organigramme sur les fiches de poste", true));
	//Module_posteEtEmploi.ajouterFils(new Lien("fichePosteOld", "FPGestionOld", "Fiche de poste", "Gestion des fiches de poste", true));
	//Module_posteEtEmploi.ajouterFils(new Lien("suiviRecrutement", "SuiviRecrutement", "Recrutement", "Suivi des recrutements", true));
	
//***************************************************************
//*               Le module Agent
//***************************************************************
var Module_agent = new Dossier("Module_agent", "Agent","AGENT");
	Module_agent.ajouterFils(new Lien("donneesPerso", "AgentEtatCivil", "Données personnelles", "Gestion des données personnelles d'un agent", true));
	<% 
	String affHSCT =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_HSCT");
	if (affHSCT.equals("TRUE")){ %>
		Module_agent.ajouterFils(new Lien("hsct", "VisiteMedicaleGestion", "HSCT", "Gestion des données HSCT", true));
	<%}%>
	Module_agent.ajouterFils(new Lien("emplois", "AgtEmploisAffectations", "Emplois", "Gestion des emplois d'un agent", true));
	Module_agent.ajouterFils(new Lien("eltsSalaires", "PAGestion", "Eléments de salaire", "Gestion des éléments de salaire d'un agent", true));
	Module_agent.ajouterFils(new Lien("absences", "AgtAbsencesSolde", "Absences", "Gestion des absences d'un agent", true));
	<% 
	String affEAEAgent =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_EAE_AGENT");
	if (affEAEAgent.equals("TRUE")){ %>
		Module_agent.ajouterFils(new Lien("eae", "AgtEae", "EAE", "Gestion des EAE d'un agent", true));
	<%}%>
	
//***************************************************************
//*               Le module Gestion des avancements
//***************************************************************
var Module_avct = new Dossier("Module_avct", "Gestion des avancements","GESTION AVANCEMENT");
	<% 
	String affAvct =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_AVCT");
	if (affAvct.equals("TRUE")){ %>
		Module_avct.ajouterFils(new Lien("simulationFonctionnaires", "AVCTSimulationFonctionnaires", "Avancement Fonctionnaires", "Gestion des avancements des fontionnaires communaux (sans AUTOMATIQUE)", true));
		Module_avct.ajouterFils(new Lien("simulationContractuels", "AVCTSimulationContractuels", "Avancement Contractuels", "Gestion des avancements des contractuels", true));
		Module_avct.ajouterFils(new Lien("simulationConvCol", "AVCTSimulationConvCol", "Avancement Conventions", "Gestion des avancements des conventions collectives", true));
		Module_avct.ajouterFils(new Lien("simulationDetaches", "AVCTSimulationDetaches", "Avancement Détachés", "Gestion des avancements des détachés", true));
		Module_avct.ajouterFils(new Lien("simulationFonctionnaireAutre", "AVCTSimulationFonctionnaireAutre", "Avct Fonct terr & auto", "Gestion des avancements des fontionnaires territoriaux et AUTOMATIQUE", true));
	<%}%>
	<% 
	String affCampagneEAE =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_CAMPAGNE_EAE");
	if (affCampagneEAE.equals("TRUE")){ %>
		Module_avct.ajouterFils(new Lien("campagneEAE", "AVCTCampagneEAE", "Campagne EAE", "Gestion des campagnes EAE", true));
	<%}%>
	<% 
	String affSimuMasse =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_SIMU");
	if (affSimuMasse.equals("TRUE")){ %>
		Module_avct.ajouterFils(new Lien("simuMasseSalariale", "AVCTMasseSalariale", "Simulation masse salariale", "SImulation de la masse salariale", true));
	<%}%>
	


//***************************************************************
//*               Le module Gestion du suivi medical
//***************************************************************
<% 
String affSuiviMed =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_SUIVI_MEDICAL");
if (affSuiviMed.equals("TRUE")){ %>
var Module_suiviMed = new Dossier("Module_suiviMed", "Gestion du suivi medical","SUIVI MEDICAL");
	Module_suiviMed.ajouterFils(new Lien("suiviMed", "SMCalcul", "Suivi médical", "Gestion du suivi médical", true));
<%}%>


//***************************************************************
//*               Le module Pointage
//***************************************************************
<% 
String affPointage =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_POINTAGE");
if (affPointage.equals("TRUE")){ %>
	var Module_pointage = new Dossier("Module_pointage", "Gestion des pointages","GESTION POINTAGES");
	Module_pointage.ajouterFils(new Lien("saisiePointage", "PTGSaisie", "Visualisation et saisie", "Gestion des pointages", true));
	Module_pointage.ajouterFils(new Lien("ventilationPointage", "PTGVentilationConvCol", "Ventilation et validation", "Gestion des pointages", true));
	Module_pointage.ajouterFils(new Lien("payeurPointage", "PTGPayeurConvCol", "Editions du payeur", "Gestion des pointages", true));
	Module_pointage.ajouterFils(new Lien("titreRepas", "PTGTitreRepas", "Titres repas", "Gestion des pointages", true));
	Module_pointage.ajouterFils(new Lien("primeDpm", "PTGPrimeDpm", "Prime DPM SDJF", "Gestion des pointages", true));
<%}%>


//***************************************************************
//*               Le module Absence
//***************************************************************
<% 
String affAbsence =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_ABSENCE");
if (affAbsence.equals("TRUE")){ %>
	var Module_absence = new Dossier("Module_absence", "Gestion des absences","GESTION DES ABSENCES");
	Module_absence.ajouterFils(new Lien("visualisationAbsence", "ABSVisualisation", "Visualisation et validation", "Gestion des absences", true));
	Module_absence.ajouterFils(new Lien("restitutionAbsence", "ABSRestitution", "Restitution massive", "Resitution massive des congés", true));
	Module_absence.ajouterFils(new Lien("alimenationMensuelleAbsence", "ABSAlimentationMensuelle", "Alimentation mensuelle", "Alimentation mensuelle des congés", true));
<%}%>

//***************************************************************
//*               Le module Gestion des élections
//***************************************************************
	<% 
	String affElection =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_ELECTION");
	if (affElection.equals("TRUE")){ %>
		var Module_election = new Dossier("Module_election", "Gestion des élections","GESTION ELECTIONS");
		Module_election.ajouterFils(new Lien("saisieCompteur", "ELECSaisieCompteurA48", "Saisie des compteurs", "Saisie des compteurs", true));
	<%}%>
	


//***************************************************************
//*               Le module ORGANIGRAMME
//***************************************************************
var Module_organigramme = new Dossier("Module_organigramme", "Gestion des organigrammes","ORGANIGRAMME");
	Module_organigramme.ajouterFils(new Lien("gestionOrganigramme", "gestionOrganigramme", "Gestion organigramme", "Gestion organigramme", true));
		
			
//***************************************************************
//*               Le module Paramètres
//***************************************************************
var Module_parametres = new Dossier("Module_parametres", "Paramètres","PARAMETRES");
	Module_parametres.ajouterFils(new Lien("posteEtEmploi", "ParamFicheEmploi", "Postes & emplois", "Gestion des parametres du module postes et emplois", true));
	Module_parametres.ajouterFils(new Lien("agent", "ParamDonneesPerso", "Agent", "Gestion des parametres du module agents", true));
	Module_parametres.ajouterFils(new Lien("grade", "ParamGradeRef", "Grade", "Gestion des paramètres des grades", true));
	Module_parametres.ajouterFils(new Lien("avancement", "ParamAvancement", "Avancement", "Gestion des paramètres des avancements", true));
<% 
String affParamElementSalaire =  (String) ServletAgent.getMesParametres().get("AFFICHAGE_PARAM_ELEM_SALAIRE");
if (affParamElementSalaire.equals("TRUE")){ %>
Module_parametres.ajouterFils(new Lien("elemSal", "ParamJour", "Eléments salaire", "Gestion des paramètres des éléments de salaire", true));
<%}%>
Module_parametres.ajouterFils(new Lien("absence", "ParamAbsMotif", "Absence", "Gestion des paramètres des absences", true));
Module_parametres.ajouterFils(new Lien("election", "ParamElec", "Election", "Gestion des paramètres des élections", true));
Module_parametres.ajouterFils(new Lien("kiosque", "ParamKiosque", "Kiosque", "Gestion des paramètres du kiosque", true));
Module_parametres.ajouterFils(new Lien("pointage", "ParamPointage", "Pointage", "Gestion des paramètres des pointages", true));
//***************************************************************
//*               Le module Gestion des droits
//***************************************************************
var Module_gestionDroits = new Dossier("Module_gestionDroits", "Gestion des droits","GESTION DES DROITS");
	Module_gestionDroits.ajouterFils(new Lien("utilisateurs", "DroitsUtilisateur", "Utilisateurs", "Gestion des utilisateurs", true));
	Module_gestionDroits.ajouterFils(new Lien("groupes", "DroitsProfil", "Groupes", "Gestion des droits par groupe", true));
	Module_gestionDroits.ajouterFils(new Lien("kiosque", "DroitsKiosque", "Kiosque", "Gestion des droits du Kiosque RH", true));
	
menu.ajouterFils(Module_posteEtEmploi);
menu.ajouterFils(Module_agent);
menu.ajouterFils(Module_avct);
<% 
if (affSuiviMed.equals("TRUE")){ %>
menu.ajouterFils(Module_suiviMed);
<%}%>
<% 
if (affPointage.equals("TRUE")){ %>
menu.ajouterFils(Module_pointage);
<%}%>
<% 
if (affAbsence.equals("TRUE")){ %>
menu.ajouterFils(Module_absence);
<%}%>
<% 
if (affElection.equals("TRUE")){ %>
menu.ajouterFils(Module_election);
<%}%>
menu.ajouterFils(Module_organigramme);
menu.ajouterFils(Module_parametres);
menu.ajouterFils(Module_gestionDroits);

<% if (aUser.getUserName().equals("boulu72") || aUser.getUserName().equals("peymi67")) {%>
menu.ajouterFils(new Lien("ZZZTESTEUR", "Testeur de process", "Testeur de process", true));
<%}%>
document.write(menu.afficher());
</script>
</FORM>
</nobr>
</BODY>
</HTML>