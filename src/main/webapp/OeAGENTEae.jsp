<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumEtatEAE"%>
<%@page import="nc.mairie.spring.domain.metier.EAE.EAE"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.OeAGENTEae" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des EAE</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
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
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des entretiens annuels d'évaluation de l'agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:50px;"></span>
				    <span style="position:relative;width:45px;text-align: center;">Année</span>
					<span style="position:relative;width:185px;text-align: center;">Evaluateur</span>
					<span style="position:relative;width:90px;text-align: center;">Date de l'entretien</span>
					<span style="position:relative;width:130px;text-align: center;">Service</span>
					<span style="position:relative;width:75px;text-align: center;">Documents</span>
					<span style="position:relative;text-align: left;">Statut</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceEae = 0;
								for (int i = 0;i<process.getListeEae().size();i++){
									EAE eae = process.getListeEae().get(i);
							%>
									<tr id="<%=indiceEae%>" onmouseover="SelectLigne(<%=indiceEae%>,<%=process.getListeEae().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;" align="center">&nbsp;
										<%if(eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())){ %>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEae)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceEae)%>">
				    						<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEae)%>">
				    					<%} %>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_ANNEE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:185px;text-align: center;"><%=process.getVAL_ST_EVALUATEUR(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_ENTRETIEN(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:130px;text-align: center;"><%=process.getVAL_ST_SERVICE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: center;"><%=process.getVAL_ST_DOCUMENTS(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_STATUT(indiceEae)%></td>
									</tr>
									<%
									indiceEae++;
							}%>
						</table>	
						</div>		
				</FIELDSET>			
		
		</FORM>
	<%} %>	
	</BODY>
</HTML>