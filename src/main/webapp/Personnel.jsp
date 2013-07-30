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
		
		/*Module AGENT - EAE*/
		listeDroits.add("AgtEae");

		/*Module POSTE*/
		listeDroits.add("FEGestion");
		listeDroits.add("FECompetence");
		listeDroits.add("FEActivite");
		listeDroits.add("FPGestionOld");
		listeDroits.add("FPGestion");
		listeDroits.add("SuiviRecrutement");
		
		/*Module AVANCEMENT*/
		listeDroits.add("AVCTSimulationFontionnaires");
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
		listeDroits.add("AVCTTest");
		
		/*Module POINTAGE*/
		listeDroits.add("PTGDroits");
		listeDroits.add("PTGSaisie");
		listeDroits.add("PTGVentilationConvCol");
		listeDroits.add("PTGVentilationTitu");
		listeDroits.add("PTGVentilationNonTitu");
		listeDroits.add("PTGPayeurConvCol");
		listeDroits.add("PTGPayeurTitu");
		listeDroits.add("PTGPayeurNonTitu");
		
		
		/*Module SUIVI MEDICAL*/
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
		listeDroits.add("ParamElemSalaire");	
		listeDroits.add("ParamCarriere");		
		listeDroits.add("ParamJour");	
		
		
		/*Module GESTION DROITS*/
		listeDroits.add("DroitsUtilisateur");
		listeDroits.add("DroitsProfil");	

	}
%>

<frameset rows="*" cols="200,*" frameborder="0" border="0"
	framespacing="0">
	<FRAMESET rows="47,120,20,*">
		<FRAME src="PersonnelCarreBleu.jsp" name="carreBleu" scrolling="NO"
			noresize marginwidth="0" marginheight="0">
		<FRAME src="CartoucheAgentMenu.jsp" name="refAgent" frameborder="no"
			noresize marginwidth="0" marginheight="0" scrolling="no"
			style="margin-top:10px;">
		<FRAME src="CartoucheAgentMenuNew.jsp" name="actionAgent" frameborder="no" noresize
			marginwidth="0" marginheight="0" scrolling="no">
		<FRAME src="PersonnelIndex.jsp" name="Index" frameborder="no" noresize
			marginwidth="0" marginheight="0" scrolling="no">
	</FRAMESET>
	<frameset rows="47,*" cols="*" framespacing="0" frameborder="NO"
		border="0">
		<FRAME src="PersonnelMenuHaut.jsp" name="MenuHaut" scrolling="NO" noresize marginwidth="0">
		<FRAME src="PersonnelMain.jsp" name="Main" marginwidth="0"
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
