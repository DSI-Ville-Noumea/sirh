<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.technique.VariableGlobale"%>
<%@page import="nc.mairie.technique.UserAppli"%>
<%@page import="nc.mairie.metier.droits.Droit"%>
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.gestionagent.servlets.ServletAgent"%>
<HTML>
<HEAD>
<META name="GENERATOR"
	content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>SIRH <%=(String) ServletAgent.getMesParametres().get("TYPE_SIRH")%> - Gestion du personnel ${version}</TITLE>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
</HEAD>
<%
	if (!ServletAgent.controlerHabilitation(request)) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("WWW-Authenticate","BASIC realm=\"Habilitation HTTP pour la Mairie\"");
		javax.servlet.ServletContext sc= getServletContext();
		javax.servlet.RequestDispatcher rd = sc.getRequestDispatcher("/ConnectionInsulte.jsp");
		rd.forward(request,response);
	}else{
	
	UserAppli aUserAppli = (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);
	
	ArrayList<String> listeDroits = aUserAppli.getListeDroits();
	
	if (listeDroits.size() == 0) {
	
		/*Module RECHERCHE AGENT*/
		listeDroits.add("AgentRecherche");
		
		/*Module AGENT - Donnees personnelles*/
		listeDroits.add("AgentCreation"); 
		listeDroits.add("EnfantGestion");
		listeDroits.add("EnfantRecherche");
		listeDroits.add("AdministrationGestion");
		listeDroits.add("AgentDiplomeGestion");
		listeDroits.add("AgentEtatCivil");
		listeDroits.add("AgentCasierJud");
		listeDroits.add("AgentContrat");
		listeDroits.add("AgentActesDonneesPerso");
		
		/*Module AGENT - HSCT*/
		listeDroits.add("HandicapGestion");
		listeDroits.add("VisiteMedicaleGestion");
		listeDroits.add("InaptitudesGestion"); 
		listeDroits.add("AccidentTravailGestion"); 
		listeDroits.add("AgentActesHSCT");
		
		/*Module AGENT - Emplois*/
		listeDroits.add("AgtEmploisAffectations");
		listeDroits.add("AgtEmploisPoste");
		
		/*Module AGENT - Elts Salaire*/
		listeDroits.add("PrimeGestion");
		listeDroits.add("ChargeGestion");
		listeDroits.add("PAGestion");
		listeDroits.add("PosAdmGestion");
		listeDroits.add("CarriereGestion");
		
		/*Module AGENT - ABSENCES*/
		listeDroits.add("AgtAbsencesSolde");
		listeDroits.add("AgtAbsencesHisto");
		listeDroits.add("AgtAbsencesCompteur");
		
		/*Module AGENT - EAE*/
		listeDroits.add("AgtEae");

		/*Module POSTE*/
		listeDroits.add("FEGestion");
		listeDroits.add("FECompetence");
		listeDroits.add("FEActivite");
		listeDroits.add("FPGestionOld");
		listeDroits.add("FPGestion");
		listeDroits.add("SuiviRecrutement");
		listeDroits.add("FPGestionAutomatise");
		listeDroits.add("FPGestionAutomatiseActivation");
		listeDroits.add("FPGestionAutomatiseDuplication");
		
		/*Module AVANCEMENT*/
		listeDroits.add("AVCTSimulationFonctionnaires");
		listeDroits.add("AVCTSimulationContractuels");
		listeDroits.add("AVCTSimulationConvCol");
		listeDroits.add("AVCTFonctPrepaAvct");
		listeDroits.add("AVCTFonctPrepaCAP");
		listeDroits.add("AVCTFonctArretes");
		listeDroits.add("AVCTFonctCarrieres");
		listeDroits.add("AVCTContractuels");
		listeDroits.add("AVCTConvCol");
		listeDroits.add("AVCTCampagneEAE");
		listeDroits.add("AVCTCampagnePlanification");
		listeDroits.add("AVCTCampagneGestionEAE");
		listeDroits.add("AVCTCampagneTableauBord");
		listeDroits.add("AVCTMasseSalariale");
		listeDroits.add("AVCTMasseSalarialeContr");
		listeDroits.add("AVCTMasseSalarialeConv");
		listeDroits.add("AVCTMasseSalarialeDetaches");
		listeDroits.add("AVCTSimulationDetaches");
		listeDroits.add("AVCTSimulationFonctionnaireAutre");
		listeDroits.add("AVCTFonctionnaireAutre");
		listeDroits.add("AVCTDetaches");
		
		/*Module POINTAGE*/
		listeDroits.add("PTGSaisie");
		listeDroits.add("PTGVentilationConvCol");
		listeDroits.add("PTGVentilationTitu");
		listeDroits.add("PTGVentilationNonTitu");
		listeDroits.add("PTGPayeurConvCol");
		listeDroits.add("PTGPayeurFonct");
		listeDroits.add("PTGPayeurContractuels");
		listeDroits.add("PTGTitreRepas");
		listeDroits.add("PTGTitreRepasEtatPayeur");
		listeDroits.add("PTGPrimeDpm");
		listeDroits.add("PTGPrimeDpmParametrage");
		
		
		/*Module ABSENCE*/
		listeDroits.add("ABSVisualisation");
		listeDroits.add("ABSRestitution");
		listeDroits.add("ABSAlimentationMensuelle");
		
		
		/*Module ELECTION*/
		listeDroits.add("ELECSaisieCompteurA48");
		listeDroits.add("ELECSaisieCompteurA54");
		listeDroits.add("ELECSaisieCompteurA55");
		listeDroits.add("ELECSaisieCompteurAmicale");
		listeDroits.add("ELECSaisieCompteurA53");
		listeDroits.add("ELECSaisieCompteurA52");
		

		/*Module ORGANIGRAMME*/
		listeDroits.add("gestionOrganigramme");
		
		
		/*Module SUIVI MEDICAL*/
		listeDroits.add("SMCalcul");
		listeDroits.add("SMConvocation");
		listeDroits.add("SMHistorique");
		
		/*Module PARAMETRES*/
		listeDroits.add("ParamFicheEmploi");
		listeDroits.add("ParamFichePoste");
		listeDroits.add("ParamRecrutement");
		listeDroits.add("ParamAvancement");		
		listeDroits.add("ParamDonneesPerso");
		listeDroits.add("ParamHSCT");
		listeDroits.add("ParamGrade");
		listeDroits.add("ParamGradeRef");	
		listeDroits.add("ParamCarriere");
		listeDroits.add("ParamRubrique");		
		listeDroits.add("ParamJour");		
		listeDroits.add("ParamAbsMotif");		
		listeDroits.add("ParamAbsCongeExcep");	
		listeDroits.add("ParamAbsMaladies");
		listeDroits.add("ParamAbsCongeAnnuel");	
		listeDroits.add("ParamElec");	
		listeDroits.add("ParamKiosque");	
		listeDroits.add("ParamPointage");
		listeDroits.add("ParamTypeDocument");
		
		
		/*Module GESTION DROITS*/
		listeDroits.add("DroitsUtilisateur");
		listeDroits.add("DroitsProfil");
		listeDroits.add("DroitsKiosque");	

	}
%>

<frameset id="1" rows="*" cols="200,*" frameborder="0" border="0"
	framespacing="0">
	<FRAMESET id="2" rows="47,120,20,*">
		<FRAME id="3" src="PersonnelCarreBleu.jsp" name="carreBleu" scrolling="NO"
			noresize marginwidth="0" marginheight="0">
		<FRAME id="4" src="CartoucheAgentMenu.jsp" name="refAgent" frameborder="no"
			noresize marginwidth="0" marginheight="0" scrolling="no"
			style="margin-top:10px;">
		<FRAME id="5" src="CartoucheAgentMenuNew.jsp" name="actionAgent" frameborder="no" noresize
			marginwidth="0" marginheight="0" scrolling="no">
		<FRAME id="6" src="PersonnelIndex.jsp" name="Index" frameborder="no" noresize
			marginwidth="0" marginheight="0" scrolling="no">
	</FRAMESET>
	<frameset id="8" rows="47,*" cols="*" framespacing="0" frameborder="NO"
		border="0">
		<FRAME id="9" src="PersonnelMenuHaut.jsp" name="MenuHaut" scrolling="NO" noresize marginwidth="0">
		<FRAME id="10" src="PersonnelMain.jsp" name="Main" marginwidth="0"
			marginheight="0">
	</frameset>
	<NOFRAMES>
	<BODY>
	<P>L'affichage de cette page requiert un navigateur prenant en
	charge les cadres (frames).</P>
	</BODY>
	</NOFRAMES>
</FRAMESET>

<%}%>
</HTML>
