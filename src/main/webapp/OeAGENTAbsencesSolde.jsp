<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeAbsence"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesSolde" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des absences</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
		//afin de s�lectionner un �l�ment dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone pr�cise
		function setfocus(nom)
		{
			if (document.formu.elements[nom] != null)
			document.formu.elements[nom].focus();
		}
		
		</SCRIPT>	
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des soldes de l'agent</legend>
				    <BR/>
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
				    	<legend class="sigp2Legend">Cong�s</legend>
						<table class="sigp2NewTab" style="text-align:left;width:200px;">
							<tr bgcolor="#EFEFEF">
								<td width="100px;" align="center">ann�e prec.</td>
								<td width="100px;" align="center">ann�e</td>
								<td>Historique</td>
							</tr>
							<tr>
								<td style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGE_PREC()%></td>
								<td style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGE()%></td>
								<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.CONGE.getCode())%>"></td>
							</tr>
						</table>				    
				    </FIELDSET>
				    <BR/>
				    
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
				    	<legend class="sigp2Legend">R�cup�rations</legend>
						<table class="sigp2NewTab" style="text-align:left;width:200px;">
							<tr bgcolor="#EFEFEF">
								<td width="200px;" align="center">En cours</td>
								<td>Historique</td>
							</tr>
							<tr>
								<td style="text-align: center"><%=process.getVAL_ST_SOLDE_RECUP()%></td>
								<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.RECUP.getCode())%>"></td>
							</tr>
						</table>				    
				    </FIELDSET>
				    <BR/>
				    
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
				    	<legend class="sigp2Legend">Repos compensateurs</legend>
						<table class="sigp2NewTab" style="text-align:left;width:200px;">
							<tr bgcolor="#EFEFEF">
								<td width="100px;" align="center">ann�e prec.</td>
								<td width="100px;" align="center">ann�e</td>
								<td>Historique</td>
							</tr>
							<tr>
								<td style="text-align: center"><%=process.getVAL_ST_SOLDE_REPOS_COMP_PREC()%></td>
								<td style="text-align: center"><%=process.getVAL_ST_SOLDE_REPOS_COMP()%></td>
								<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.REPOS_COMP.getCode())%>"></td>
							</tr>
						</table>				    
				    </FIELDSET>
				    <BR/>
				    
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
				    	<legend class="sigp2Legend">ASA - R�union des membres du bureau directeur (A48)</legend>
						<table class="sigp2NewTab" style="text-align:left;width:200px;">
							<tr bgcolor="#EFEFEF">
								<td width="200px;" align="center">En cours</td>
								<td>Historique</td>
							</tr>
							<tr>
								<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A48()%></td>
								<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.ASA_A48.getCode())%>"></td>
							</tr>
						</table>				    
				    </FIELDSET>
				    <BR/>				    
				</FIELDSET>				
				<BR/>
		</FORM>
<%} %>	
	</BODY>
</HTML>