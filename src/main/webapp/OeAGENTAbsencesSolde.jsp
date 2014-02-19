<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesSolde" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des absences</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
		//afin de sélectionner un élément dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone précise
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
				    <br/>
				    <span style="position:relative;width:80px;text-align: center;">Congés<br> année prec.</span>
					<span style="position:relative;width:80px;text-align: center;">Congés<br> année</span>
					<span style="position:relative;width:80px;text-align: center;">Récup.</span>
				    <span style="position:relative;width:80px;text-align: center;">Repos Comp<br> année prec.</span>
					<span style="position:relative;width:80px;text-align: center;">Repos Comp<br> année</span>
					<span style="position:relative;text-align: center;"></span>
					<br/>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_SOLDE_CONGE_PREC()%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_SOLDE_CONGE()%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_SOLDE_RECUP()%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_SOLDE_REPOS_COMP_PREC()%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_SOLDE_REPOS_COMP()%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: center;"></td>
							</tr>
						</table>	
					</div>					    
				</FIELDSET>				
				<BR/>
		</FORM>
<%} %>	
	</BODY>
</HTML>