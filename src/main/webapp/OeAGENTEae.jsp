<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.spring.domain.metier.EAE.CampagneEAE"%>
<%@page import="nc.mairie.spring.domain.metier.EAE.EaeDeveloppement"%>
<%@page import="nc.mairie.spring.domain.metier.EAE.EaePlanAction"%>
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
				    					<%if(!process.isCampagneOuverte(eae.getIdCampagneEAE())){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceEae)%>">
				    					<%} %>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_ANNEE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: center;"><%=process.getVAL_ST_EVALUATEUR(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_ENTRETIEN(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: center;"><%=process.getVAL_ST_SERVICE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: center;">
										<%if(eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())){ %>
											<INPUT title="voir le document" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISER_DOC(indiceEae)%>">
				    					<%}else{ %>
				    					&nbsp;
				    					<%} %>
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
							<% if (process.onglet.equals("ONGLET6")) {%>
								<span id="titreOngletDeveloppement" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET6');">&nbsp;Developpement&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletDeveloppement" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET6');">&nbsp;Developpement&nbsp;</span>&nbsp;&nbsp;
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
								<%if(process.getListeEvaluateurEae().size()>0){ %>
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
								<%} %>				
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Direction / Service : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_SERVICE()%></span>
							</div>
						
							<% if (process.onglet.equals("ONGLET3")) {%>
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Commentaire de l'évaluateur : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_EVALUATEUR()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Niveau : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NIVEAU()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Note de l'année : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NOTE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Avis Evaluateur : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_AVIS_SHD()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Avancement différencié : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_AVCT_DIFF()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Changement de classe : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_CHANGEMENT_CLASSE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Avis revalorisation : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_AVIS_REVALO()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Rapport Circonstancié : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_RAPPORT_CIRCON()%></span>
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET4")) {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
								<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Objectifs professionnels : </span>
								<br/><br/>
								<%if(process.getListeObjectifPro().size()>0){ %>
								<span style="margin-left:20px;position:relative;width:500px;text-align: left;">Objectif</span>
								<span style="position:relative;text-align: center;">Mesure</span>
								<br/>
								<div style="overflow: auto;height: 50px;width:900px;margin-left: 20px;">
									<table class="sigp2NewTab" style="text-align:left;width:880px;">
									<%
									int indiceObjPro = 0;
										for (int i = 0;i<process.getListeObjectifPro().size();i++){
											EaePlanAction plan = process.getListeObjectifPro().get(i);
									%>
											<tr>
												<td width="500px" class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_OBJ_PRO(indiceObjPro)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_LIB_MESURE_PRO(indiceObjPro)%></td>
											</tr>
											<%
											indiceObjPro++;
									}%>
									</table>	
								</div>	
								<%} %>				
								<BR/><BR/>
								<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Objectifs individuels : </span>
								<br/><br/>
								<%if(process.getListeObjectifIndi().size()>0){ %>
								<span style="margin-left:20px;position:relative;width:150px;text-align: left;">Objectif</span>
								<br/>
								<div style="overflow: auto;height: 50px;width:900px;margin-left: 20px;">
									<table class="sigp2NewTab" style="text-align:left;width:880px;">
									<%
									int indiceObjIndi = 0;
										for (int i = 0;i<process.getListeObjectifIndi().size();i++){
											EaePlanAction plan = process.getListeObjectifIndi().get(i);
									%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_OBJ_INDI(indiceObjIndi)%></td>
											</tr>
											<%
											indiceObjIndi++;
									}%>
									</table>	
								</div>	
								<%} %>				
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET5")) {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité géographique : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_MOB_GEO()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité fonctionnelle : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_MOB_FONCT()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Changement de métier : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_CHANGEMENT_METIER()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Délai envisagé : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_DELAI()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité service : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_MOB_SERV()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité direction : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_MOB_DIR()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité collectivité : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_MOB_COLL()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom collectivité : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_COLL()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité autre : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_MOB_AUTRE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Concours : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_CONCOURS()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom concours : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_CONCOURS()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">VAE : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_VAE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom VAE : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_VAE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Temps partiel : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_TPS_PARTIEL()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Pourcentage temps partiel : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_POURC_TPS_PARTIEL()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Retraite prévue : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_RETRAITE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Date retraite : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_RETRAITE()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Autre perspective : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_AUTRE_PERSP()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Libellé autre perspective : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_LIB_AUTRE_PERSP()%></span>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Commentaire : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_COM_EVOLUTION()%></span>
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET6")) {%>
								<div id="corpsOngletDeveloppement" title="Developpement" class="OngletCorps" style="display:block;margin-right:10px;width:1000px;">
							<% }else {%>
								<div id="corpsOngletDeveloppement" title="Developpement" class="OngletCorps" style="display:none;margin-right:10px;width:1000px;">
							<% } %>
							<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Développements : </span>
								<br/><br/>
								<%if(process.getListeDeveloppement().size()>0){ %>
								<span style="margin-left:25px;position:relative;width:120px;text-align: left;">Type</span>
								<span style="position:relative;width:400px;text-align: left;">Libellé</span>
								<span style="position:relative;width:90px;text-align: center;">Echeance</span>
								<span style="position:relative;text-align: center;">Priorité</span>
								<br/>
								<div style="overflow: auto;height: 150px;width:900px;margin-left: 20px;">
									<table class="sigp2NewTab" style="text-align:left;width:680px;">
									<%
									int indiceDev = 0;
										for (int i = 0;i<process.getListeDeveloppement().size();i++){
									%>
											<tr>
												<td width="120px" class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_TYPE_DEV(indiceDev)%></td>
												<td width="400px" class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_DEV(indiceDev)%></td>
												<td width="90px" class="sigp2NewTab-liste" style="position:relative;text-align: center;"><%=process.getVAL_ST_ECHEANCE_DEV(indiceDev)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_PRIORISATION_DEV(indiceDev)%></td>
											</tr>
											<%
											indiceDev++;
									}%>
									</table>	
								</div>	
								<%} %>				
								<BR/><BR/>
							</div>
						<%} %>
						
					</FIELDSET>
				<%} %>
		<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">	
		<%=process.getUrlFichier()%>
		</FORM>
	<%} %>	
	</BODY>
</HTML>