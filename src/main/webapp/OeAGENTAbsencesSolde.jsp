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
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
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
				    	<legend class="sigp2Legend">Congés</legend>
						<table class="sigp2NewTab" style="text-align:left;width:200px;">
							<tr bgcolor="#EFEFEF">
								<td width="100px;" align="center">année prec.</td>
								<td width="100px;" align="center">année</td>
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
				    	<legend class="sigp2Legend">Récupérations</legend>
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
								<td width="100px;" align="center">année prec.</td>
								<td width="100px;" align="center">année</td>
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
				    	<legend class="sigp2Legend">ASA - Réunion des membres du bureau directeur (A48)</legend>
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
				<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td align="center" width="90px;">Le <br/> à</td>
								<td width="180px;">Par</td>
								<td width="180px;">Motif</td>
								<td>Opération</td>
							</tr>
							<%
							for (int i = 0;i<process.getListeHistorique().size();i++){
							%>
								<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeHistorique().size()%>)">
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_PAR(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_MOTIF(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_OPERATION(i)%></td>
								</tr>
							<%}%>
						</table>	
						</div>
						<BR/><BR/>
						<div style="text-align: center;">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Fermer" name="<%=process.getNOM_PB_ANNULER()%>">
						</div>	
				</FIELDSET>
				<%} %>
		</FORM>
<%} %>	
	</BODY>
</HTML>