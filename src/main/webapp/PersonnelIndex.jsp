<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Menu Personnel</TITLE>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>

<%
nc.mairie.technique.UserAppli aUser= (nc.mairie.technique.UserAppli)nc.mairie.technique.VariableGlobale.recuperer(request,nc.mairie.technique.VariableGlobale.GLOBAL_USER_APPLI);
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
<BODY text="#000000" onload="preload();" style="cursor : auto;" >
<BASEFONT FACE="Arial" SIZE=2> 
<nobr>
<FORM name="leForm" method="POST" target="Main" action="GestionAgentServlet">
<INPUT type="hidden" name="ACTIVITE" value="">
<script>
var menu = new Menu();

//***************************************************************
//*               Le module Postes et emplois
//***************************************************************
var Module_posteEtEmploi = new Dossier("Module_posteEtEmploi", "Postes & emplois","PE");
	Module_posteEtEmploi.ajouterFils(new Lien("ficheEmploi", "FEGestion", "Fiche emploi", "Gestion des fiches emploi", true));
	Module_posteEtEmploi.ajouterFils(new Lien("fichePoste", "FPGestion", "Fiche de poste", "Gestion des fiches de poste", true));
	//Module_posteEtEmploi.ajouterFils(new Lien("fichePosteOld", "FPGestionOld", "Fiche de poste", "Gestion des fiches de poste", true));
	//Module_posteEtEmploi.ajouterFils(new Lien("suiviRecrutement", "SuiviRecrutement", "Recrutement", "Suivi des recrutements", true));
	
//***************************************************************
//*               Le module Agent
//***************************************************************
var Module_agent = new Dossier("Module_agent", "Agent","AGENT");
	Module_agent.ajouterFils(new Lien("donneesPerso", "AgentEtatCivil", "Données personnelles", "Gestion des données personnelles d'un agent", true));
	Module_agent.ajouterFils(new Lien("hsct", "VisiteMedicaleGestion", "HSCT", "Gestion des données HSCT", true));
	Module_agent.ajouterFils(new Lien("emplois", "AgtEmploisAffectations", "Emplois", "Gestion des emplois d'un agent", true));
	Module_agent.ajouterFils(new Lien("eltsSalaires", "PAGestion", "Eléments de salaire", "Gestion des éléments de salaire d'un agent", true));
	Module_agent.ajouterFils(new Lien("eae", "AgtEae", "EAE", "Gestion des EAE d'un agent", true));
	
//***************************************************************
//*               Le module Gestion des avancements
//***************************************************************
var Module_avct = new Dossier("Module_avct", "Gestion des avancements","AVANCEMENT");
	Module_avct.ajouterFils(new Lien("simulationFonctionnaires", "AVCTSimulationFontionnaires", "Avancement Fonctionnaires", "Gestion des avancements des fontionnaires", true));
	Module_avct.ajouterFils(new Lien("simulationContractuels", "AVCTSimulationContractuels", "Avancement Contractuels", "Gestion des avancements des contractuels", true));
	Module_avct.ajouterFils(new Lien("simulationConvCol", "AVCTSimulationConvCol", "Avancement Conventions", "Gestion des avancements des conventions collectives", true));
	Module_avct.ajouterFils(new Lien("campagneEAE", "AVCTCampagneEAE", "Campagne EAE", "Gestion des campagnes EAE", true));


//***************************************************************
//*               Le module Gestion du suivi medical
//***************************************************************
var Module_suiviMed = new Dossier("Module_suiviMed", "Gestion du suivi medical","SUIVI_MED");
	Module_suiviMed.ajouterFils(new Lien("suiviMed", "SMConvocation", "Suivi médical", "Gestion du suivi médical", true));

		
//***************************************************************
//*               Le module Paramètres
//***************************************************************
var Module_parametres = new Dossier("Module_parametres", "Paramètres","PARAM");
	Module_parametres.ajouterFils(new Lien("posteEtEmploi", "ParamFicheEmploi", "Postes & emplois", "Gestion des parametres du module postes et emplois", true));
	Module_parametres.ajouterFils(new Lien("agent", "ParamDonneesPerso", "Agent", "Gestion des parametres du module agents", true));
	Module_parametres.ajouterFils(new Lien("grade", "ParamGradeRef", "Grade", "Gestion des grades", true));
	Module_parametres.ajouterFils(new Lien("avancement", "ParamAvancement", "Avancement", "Gestion des avancements", true));
	
//***************************************************************
//*               Le module Gestion des droits
//***************************************************************
var Module_gestionDroits = new Dossier("Module_gestionDroits", "Gestion des droits","DROIT");
	Module_gestionDroits.ajouterFils(new Lien("utilisateurs", "DroitsUtilisateur", "Utilisateurs", "Gestion des utilisateurs", true));
	Module_gestionDroits.ajouterFils(new Lien("groupes", "DroitsProfil", "Groupes", "Gestion des droits par groupe", true));
	
menu.ajouterFils(Module_posteEtEmploi);
menu.ajouterFils(Module_agent);
menu.ajouterFils(Module_avct);
menu.ajouterFils(Module_suiviMed);
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