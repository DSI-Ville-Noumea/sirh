<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.spring.domain.metier.EAE.EaeEvaluateur"%>
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
		<SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
		
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
					<span style="position:relative;width:250px;text-align: center;">Evaluateur</span>
					<span style="position:relative;width:90px;text-align: center;">Date de l'entretien</span>
					<span style="position:relative;width:300px;text-align: center;">Service</span>
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
										<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEae)%>">
										<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEae)%>">
				    					<%if(eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceEae)%>">
				    					<%} %>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_ANNEE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: center;"><%=process.getVAL_ST_EVALUATEUR(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_ENTRETIEN(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: center;"><%=process.getVAL_ST_SERVICE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: center;"><%=process.getVAL_ST_DOCUMENTS(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_STATUT(indiceEae)%></td>
									</tr>
									<%
									indiceEae++;
							}%>
						</table>	
						</div>		
				</FIELDSET>			
				<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
						<legend class="sigp2Legend">Détail de l'entretien annuel d'évaluation</legend>
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION) ){ %>
						<div style="margin-left:10px;margin-top:20px;text-align:left;width:900px" align="left">
							<% if (process.onglet.equals("ONGLET1")) {%>
								<span id="titreOngletInformations" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET1');">&nbsp;Informations&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletInformations" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET1');">&nbsp;Informations&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET2")) {%>
								<span id="titreOngletAppréciation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET2');">&nbsp;Appréciation&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletAppréciation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET2');">&nbsp;Appréciation&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET3")) {%>
								<span id="titreOngletEvaluation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET3');">&nbsp;Evaluation&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletEvaluation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET3');">&nbsp;Evaluation&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET4")) {%>
								<span id="titreOngletPlanAction" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET4');">&nbsp;Plan d'action&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletPlanAction" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET4');">&nbsp;Plan d'action&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET5")) {%>
								<span id="titreOngletEvolution" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET5');">&nbsp;Evolution&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletEvolution" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET5');">&nbsp;Evolution&nbsp;</span>&nbsp;&nbsp;
							<% } %>
						</div>
						
							<% if (process.onglet.equals("ONGLET1")) {%>
								<div id="corpsOngletInformations" title="Informations" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletInformations" title="Informations" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Date de l'entretien : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_ENTRETIEN()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:200px;">Evaluateur(s) : </span>
								<span style="position:relative;width:150px;text-align: left;">Agent</span>
								<span style="position:relative;text-align: center;">Fonction</span>
								<br/>
								<div style="overflow: auto;height: 50px;width:500px;margin-left: 100px;">
									<table class="sigp2NewTab" style="text-align:left;width:480px;">
									<%
									int indiceEvaluateur = 0;
										for (int i = 0;i<process.getListeEvaluateurEae().size();i++){
											EaeEvaluateur evaluateur = process.getListeEvaluateurEae().get(i);
									%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: center;"><%=process.getVAL_ST_EVALUATEUR_NOM(indiceEvaluateur)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_EVALUATEUR_FONCTION(indiceEvaluateur)%></td>
											</tr>
											<%
											indiceEvaluateur++;
									}%>
									</table>	
								</div>					
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Direction / Service : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_SERVICE()%></span>
							</div>
						
							<% if (process.onglet.equals("ONGLET2")) {%>
								<div id="corpsOngletAppréciation" title="Appréciation" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletAppréciation" title="Appréciation" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
							</div>
						
							<% if (process.onglet.equals("ONGLET3")) {%>
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Commentaire de l'évaluateur : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_EVALUATEUR()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Niveau : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NIVEAU()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Note de l'année : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NOTE()%></span>
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET4")) {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
							</div>
						
							<% if (process.onglet.equals("ONGLET5")) {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
							</div>
						<%} %>
						
					</FIELDSET>
				<%} %>
		<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">	
		</FORM>
	<%} %>	
	</BODY>
</HTML>