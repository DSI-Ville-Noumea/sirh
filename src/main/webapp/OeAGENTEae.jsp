<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.agent.Document"%>
<%@page import="nc.mairie.technique.VariableGlobale"%>
<%@page import="nc.mairie.technique.UserAppli"%>
<%@page import="nc.mairie.metier.eae.CampagneEAE"%>
<%@page import="nc.mairie.metier.eae.EaeDeveloppement"%>
<%@page import="nc.mairie.metier.eae.EaePlanAction"%>
<%@page import="nc.mairie.metier.eae.EaeEvaluateur"%>
<%@page import="nc.mairie.enums.EnumEtatEAE"%>
<%@page import="nc.mairie.metier.eae.EAE"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
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
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTEae" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%>  method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des entretiens annuels d'évaluation de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<tr bgcolor="#EFEFEF">
							<td width="70px;"></td>
							<td width="45px;" align="center">Année</td>
							<td width="250px;" align="center">Evaluateur</td>
							<td width="90px;" align="center">Date de l'entretien</td>
							<td width="300px;" align="center">Service</td>
							<td width="75px;" align="center">Documents</td>
							<td>Statut</td>
						</tr>
						<%
						UserAppli aUser= (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);
			
							int indiceEae = 0;
								for (int i = 0;i<process.getListeEae().size();i++){
									EAE eae = process.getListeEae().get(i);
							%>
									<tr id="<%=indiceEae%>" onmouseover="SelectLigne(<%=indiceEae%>,<%=process.getListeEae().size()%>)">
										<td class="sigp2NewTab-liste" align="center">&nbsp;
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEae)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEae)%>">
					    					<%if(!process.isCampagneOuverte(eae.getIdCampagneEae()) && eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())){ %>
													<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceEae)%>">
						    				<%}else if(process.isCampagneOuverte(eae.getIdCampagneEae()) && !(eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())||eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode()))){ %>
						    					<INPUT title="modifier les dates" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DATE(indiceEae)%>">
						    				<%} %>								
											<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceEae)%>">
										</td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_ANNEE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_EVALUATEUR(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_ENTRETIEN(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_SERVICE(indiceEae)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;">
										<%if(eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())){ %>
											<INPUT title="voir le document" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_VISUALISER_DOC(indiceEae)%>">
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
				<%if (! "".equals(process.getVAL_ST_ACTION()) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION_ANCIEN_EAE) ) {%>
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
						<legend class="sigp2Legend">Détail de l'entretien annuel d'évaluation</legend>
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION) ){ %>
						<div style="margin-left:10px;text-align:left;width:980px" align="left">
							<% if (process.onglet.equals("ONGLET1")) {%>
								<span id="titreOngletInformations" class="OngletActif" onclick="afficheOnglet('ONGLET1');">&nbsp;Informations&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletInformations" class="OngletInactif" onclick="afficheOnglet('ONGLET1');">&nbsp;Informations&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET3")) {%>
								<span id="titreOngletEvaluation" class="OngletActif" onclick="afficheOnglet('ONGLET3');">&nbsp;Evaluation&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletEvaluation" class="OngletInactif" onclick="afficheOnglet('ONGLET3');">&nbsp;Evaluation&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET4")) {%>
								<span id="titreOngletPlanAction" class="OngletActif" onclick="afficheOnglet('ONGLET4');">&nbsp;Plan d'action&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletPlanAction" class="OngletInactif" onclick="afficheOnglet('ONGLET4');">&nbsp;Plan d'action&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET5")) {%>
								<span id="titreOngletEvolution" class="OngletActif" onclick="afficheOnglet('ONGLET5');">&nbsp;Evolution&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletEvolution" class="OngletInactif" onclick="afficheOnglet('ONGLET5');">&nbsp;Evolution&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET6")) {%>
								<span id="titreOngletDeveloppement" class="OngletActif" onclick="afficheOnglet('ONGLET6');">&nbsp;Développement&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletDeveloppement" class="OngletInactif" onclick="afficheOnglet('ONGLET6');">&nbsp;Développement&nbsp;</span>&nbsp;&nbsp;
							<% } %>
						</div>
						
							<% if (process.onglet.equals("ONGLET1")) {%>
								<div id="corpsOngletInformations" title="Informations" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletInformations" title="Informations" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
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
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
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
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
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
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
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
								<div id="corpsOngletDeveloppement" title="Developpement" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletDeveloppement" title="Developpement" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
							<% } %>
							<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Développements : </span>
								<br/><br/>
								<%if(process.getListeDeveloppement().size()>0){ %>
								<span style="margin-left:25px;position:relative;width:120px;text-align: left;">Type</span>
								<span style="position:relative;width:400px;text-align: left;">Libellé</span>
								<span style="position:relative;width:90px;text-align: center;">Echéance</span>
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
						<%}else if (process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)||process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT_OBJ_INDI)||process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_OBJ_INDI)||process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_OBJ_INDI)||process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT_OBJ_PRO)||process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_OBJ_PRO)||process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_OBJ_PRO)||process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT_DEV)||process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_DEV)||process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_DEV)){%>
						<div style="margin-left:10px;margin-top:20px;text-align:left;width:900px" align="left">
							<% if (process.onglet.equals("ONGLET1")) {%>
								<span id="titreOngletInformations" class="OngletActif" onclick="afficheOnglet('ONGLET1');">&nbsp;Informations&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletInformations" class="OngletInactif" onclick="afficheOnglet('ONGLET1');">&nbsp;Informations&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET3")) {%>
								<span id="titreOngletEvaluation" class="OngletActif" onclick="afficheOnglet('ONGLET3');">&nbsp;Evaluation&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletEvaluation" class="OngletInactif" onclick="afficheOnglet('ONGLET3');">&nbsp;Evaluation&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET4")) {%>
								<span id="titreOngletPlanAction" class="OngletActif" onclick="afficheOnglet('ONGLET4');">&nbsp;Plan d'action&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletPlanAction" class="OngletInactif" onclick="afficheOnglet('ONGLET4');">&nbsp;Plan d'action&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET5")) {%>
								<span id="titreOngletEvolution" class="OngletActif" onclick="afficheOnglet('ONGLET5');">&nbsp;Evolution&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletEvolution" class="OngletInactif" onclick="afficheOnglet('ONGLET5');">&nbsp;Evolution&nbsp;</span>&nbsp;&nbsp;
							<% } %>
							<% if (process.onglet.equals("ONGLET6")) {%>
								<span id="titreOngletDeveloppement" class="OngletActif" onclick="afficheOnglet('ONGLET6');">&nbsp;Développement&nbsp;</span>&nbsp;&nbsp;
							<% }else {%>
								<span id="titreOngletDeveloppement" class="OngletInactif" onclick="afficheOnglet('ONGLET6');">&nbsp;Développement&nbsp;</span>&nbsp;&nbsp;
							<% } %>
						</div>
						
							<% if (process.onglet.equals("ONGLET1")) {%>
								<div id="corpsOngletInformations" title="Informations" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletInformations" title="Informations" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
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
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletEvaluation" title="Evaluation" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
							<% } %>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Commentaire de l'évaluateur : </span>
								<textarea class="sigp2-saisie" rows="3" maxlength="3900" style="position:relative;width:600px" name="<%= process.getNOM_ST_COMMENTAIRE_EVALUATEUR() %>" ><%= process.getVAL_ST_COMMENTAIRE_EVALUATEUR() %></textarea>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Niveau : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NIVEAU(),process.getNOM_RB_NIVEAU_EXCEL())%>>Excellent
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NIVEAU(),process.getNOM_RB_NIVEAU_SATIS())%>>Satisfaisant
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NIVEAU(),process.getNOM_RB_NIVEAU_PROGR())%>>Nécéssitant des progrès
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NIVEAU(),process.getNOM_RB_NIVEAU_INSU())%>>Insuffisant
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Note de l'année : </span>
								<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_ST_NOTE() %>" size="5" type="text"  value="<%= process.getVAL_ST_NOTE() %>">
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Avis Evaluateur : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SHD(),process.getNOM_RB_SHD_MIN())%>>Minimale
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SHD(),process.getNOM_RB_SHD_MOY())%>>Moyenne
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SHD(),process.getNOM_RB_SHD_MAX())%>>Maximale
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SHD(),process.getNOM_RB_SHD_FAV())%>>Favorable
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SHD(),process.getNOM_RB_SHD_DEFAV())%>>Défavorable
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Avancement différencié : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AD(),process.getNOM_RB_AD_MIN())%>>Minimale
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AD(),process.getNOM_RB_AD_MOY())%>>Moyenne
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AD(),process.getNOM_RB_AD_MAX())%>>Maximale
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Changement de classe : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CHGT(),process.getNOM_RB_CHGT_FAV())%>>Favorable
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CHGT(),process.getNOM_RB_CHGT_DEF())%>>Défavorable
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Avis revalorisation : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_REVA(),process.getNOM_RB_REVA_FAV())%>>Favorable
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_REVA(),process.getNOM_RB_REVA_DEF())%>>Défavorable
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Rapport Circonstancié : </span>
								<textarea class="sigp2-saisie" rows="3" maxlength="3900" style="position:relative;width:600px" name="<%= process.getNOM_ST_RAPPORT_CIRCON() %>" ><%= process.getVAL_ST_RAPPORT_CIRCON() %></textarea>
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET4")) {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletPlanAction" title="PlanAction" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
							<% } %>
								<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Objectifs professionnels : </span>
								<br/><br/>
				    			<span style="margin-left:20px;position:relative;width:9px;"></span>
				    			<span style="position:relative;width:40px;">
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_OBJ_PRO()%>">
								</span>
								<span style="position:relative;width:500px;text-align: left;">Objectif</span>
								<span style="position:relative;text-align: center;">Mesure</span>
								<br/>
								<div style="overflow: auto;height: 80px;width:900px;margin-left: 20px;">
									<table class="sigp2NewTab" style="text-align:left;width:880px;">
									<%
									int indiceObjPro = 0;
										for (int i = 0;i<process.getListeObjectifPro().size();i++){
											EaePlanAction plan = process.getListeObjectifPro().get(i);
									%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="center">
						    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_OBJ_PRO(indiceObjPro)%>">
						    						<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_OBJ_PRO(indiceObjPro)%>">
						    					</td>
												<td width="500px" class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_OBJ_PRO(indiceObjPro)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_LIB_MESURE_PRO(indiceObjPro)%></td>
											</tr>
											<%
											indiceObjPro++;
									}%>
									</table>	
								</div>	
								
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT_OBJ_PRO)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_OBJ_PRO() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_OBJ_PRO() %>">
									<BR/>	
									<span class="sigp2" style="margin-left:20px;position:relative;width:50px;">Mesure :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_MESURE_PRO() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_MESURE_PRO() %>">				
									<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_OBJ_PRO()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_OBJ_PRO()%>">	
								</div>										
								<%} %>
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_OBJ_PRO)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_OBJ_PRO() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_OBJ_PRO() %>">	
									<BR/>	
									<span class="sigp2" style="margin-left:20px;position:relative;width:50px;">Mesure :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_MESURE_PRO() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_MESURE_PRO() %>">					
									<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_OBJ_PRO()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_OBJ_PRO()%>">	
								</div>										
								<%} %>
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_OBJ_PRO)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" disabled="disabled" name="<%= process.getNOM_ST_LIB_OBJ_PRO() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_OBJ_PRO() %>">
									<BR/>	
									<span class="sigp2" style="margin-left:20px;position:relative;width:50px;">Mesure :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" disabled="disabled" name="<%= process.getNOM_ST_LIB_MESURE_PRO() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_MESURE_PRO() %>">						
									<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_OBJ_PRO()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_OBJ_PRO()%>">	
								</div>										
								<%} %>			
								<BR/><BR/>
								<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Objectifs individuels : </span>
								<br/><br/>
				    			<span style="margin-left:20px;position:relative;width:9px;"></span>
				    			<span style="position:relative;width:40px;">
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_OBJ_INDI()%>">
								</span>
				    			<span style="position:relative;width:150px;text-align: left;">Objectif</span>
								<br/>
								<div style="overflow: auto;height: 80px;width:900px;margin-left: 20px;">
									<table class="sigp2NewTab" style="text-align:left;width:880px;">
									<%
									int indiceObjIndi = 0;
										for (int i = 0;i<process.getListeObjectifIndi().size();i++){
											EaePlanAction plan = process.getListeObjectifIndi().get(i);
									%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="center">
						    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_OBJ_INDI(indiceObjIndi)%>">
						    						<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_OBJ_INDI(indiceObjIndi)%>">
						    					</td>
												<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_OBJ_INDI(indiceObjIndi)%></td>
											</tr>
											<%
											indiceObjIndi++;
									}%>
									</table>	
								</div>	
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT_OBJ_INDI)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_OBJ_INDI() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_OBJ_INDI() %>">				
									<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_OBJ_INDI()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_OBJ_INDI()%>">	
								</div>										
								<%} %>
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_OBJ_INDI)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_OBJ_INDI() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_OBJ_INDI() %>">				
									<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_OBJ_INDI()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_OBJ_INDI()%>">	
								</div>										
								<%} %>
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_OBJ_INDI)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" disabled="disabled" name="<%= process.getNOM_ST_LIB_OBJ_INDI() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_OBJ_INDI() %>">				
									<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_OBJ_INDI()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_OBJ_INDI()%>">	
								</div>										
								<%} %>
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET5")) {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletEvolution" title="Evolution" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
							<% } %>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité géographique : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_GEO(),process.getNOM_RB_MOB_GEO_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_GEO(),process.getNOM_RB_MOB_GEO_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité fonctionnelle : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_FONCT(),process.getNOM_RB_MOB_FONCT_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_FONCT(),process.getNOM_RB_MOB_FONCT_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Changement de métier : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_METIER(),process.getNOM_RB_METIER_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_METIER(),process.getNOM_RB_METIER_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Délai envisagé : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DELAI(),process.getNOM_RB_DELAI_1())%>>Inférieur à 1 an
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DELAI(),process.getNOM_RB_DELAI_2())%>>Entre 1 et 2 ans
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DELAI(),process.getNOM_RB_DELAI_4())%>>Entre 2 et 4 ans
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité service : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_SERV(),process.getNOM_RB_MOB_SERV_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_SERV(),process.getNOM_RB_MOB_SERV_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité direction : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_DIR(),process.getNOM_RB_MOB_DIR_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_DIR(),process.getNOM_RB_MOB_DIR_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité collectivité : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_COLL(),process.getNOM_RB_MOB_COLL_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_COLL(),process.getNOM_RB_MOB_COLL_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom collectivité : </span>
								<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_ST_NOM_COLL() %>" size="70" type="text"  value="<%= process.getVAL_ST_NOM_COLL() %>">
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Mobilité autre : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_AUTRE(),process.getNOM_RB_MOB_AUTRE_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOB_AUTRE(),process.getNOM_RB_MOB_AUTRE_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Concours : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONCOURS(),process.getNOM_RB_CONCOURS_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONCOURS(),process.getNOM_RB_CONCOURS_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom concours : </span>
								<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_ST_NOM_CONCOURS() %>" size="70" type="text"  value="<%= process.getVAL_ST_NOM_CONCOURS() %>">
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">VAE : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_VAE(),process.getNOM_RB_VAE_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_VAE(),process.getNOM_RB_VAE_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom VAE : </span>
								<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_ST_NOM_VAE() %>" size="70" type="text"  value="<%= process.getVAL_ST_NOM_VAE() %>">
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Temps partiel : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TPS_PARTIEL(),process.getNOM_RB_TPS_PARTIEL_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TPS_PARTIEL(),process.getNOM_RB_TPS_PARTIEL_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Pourcentage temps partiel : </span>
								<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_BASE_HORAIRE() %>">
									<%=process.forComboHTML(process.getVAL_LB_BASE_HORAIRE(), process.getVAL_LB_BASE_HORAIRE_SELECT()) %>
								</SELECT>
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Retraite prévue : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_RETRAITE(),process.getNOM_RB_RETRAITE_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_RETRAITE(),process.getNOM_RB_RETRAITE_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Date retraite : </span>
								<input id="<%=process.getNOM_ST_DATE_RETRAITE()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_RETRAITE() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_RETRAITE() %>" >
								<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_RETRAITE()%>', 'dd/mm/y');">
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Autre perspective : </span>
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AUTRE_PERSP(),process.getNOM_RB_AUTRE_PERSP_OUI())%>>Oui
								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AUTRE_PERSP(),process.getNOM_RB_AUTRE_PERSP_NON())%>>Non
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Libellé autre perspective : </span>
								<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_ST_LIB_AUTRE_PERSP() %>" size="70" type="text"  value="<%= process.getVAL_ST_LIB_AUTRE_PERSP() %>">
								<BR/><BR/>
								<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Commentaire : </span>
								<textarea class="sigp2-saisie" rows="3" maxlength="3900" style="position:relative;width:600px" name="<%= process.getNOM_ST_COM_EVOLUTION() %>" ><%= process.getVAL_ST_COM_EVOLUTION() %></textarea>
								<BR/><BR/>
							</div>
						
							<% if (process.onglet.equals("ONGLET6")) {%>
								<div id="corpsOngletDeveloppement" title="Developpement" class="OngletCorps" style="display:block;margin-right:10px;width:960px;">
							<% }else {%>
								<div id="corpsOngletDeveloppement" title="Developpement" class="OngletCorps" style="display:none;margin-right:10px;width:960px;">
							<% } %>
							<span class="sigp2Mandatory" style="text-decoration: underline;margin-left:20px;position:relative;width:200px;">Développements : </span>
								<br/><br/>
								<span style="margin-left:20px;position:relative;width:9px;"></span>
				    			<span style="position:relative;width:40px;">
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_DEV()%>">
								</span>
				    			<span style="position:relative;width:120px;text-align: left;">Type</span>
								<span style="position:relative;width:400px;text-align: left;">Libellé</span>
								<span style="position:relative;width:90px;text-align: center;">Echéance</span>
								<span style="position:relative;text-align: center;">Priorité</span>
								<br/>
								<div style="overflow: auto;height: 150px;width:900px;margin-left: 20px;">
									<table class="sigp2NewTab" style="text-align:left;width:880px;">
									<%
									int indiceDev = 0;
										for (int i = 0;i<process.getListeDeveloppement().size();i++){
									%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="center">
						    						<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DEV(indiceDev)%>">
						    						<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DEV(indiceDev)%>">
						    					</td>
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
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT_DEV)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Type :</span>
									<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_DEV() %>">
										<%=process.forComboHTML(process.getVAL_LB_TYPE_DEV(), process.getVAL_LB_TYPE_DEV_SELECT()) %>
									</SELECT>
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_DEV() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_DEV() %>">
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Priorisation :</span>
									<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_ST_PRIO_DEV() %>" size="5" type="text" value="<%= process.getVAL_ST_PRIO_DEV() %>">
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Echéance :</span>
									<input id="<%=process.getNOM_ST_DATE_DEV()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEV() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEV() %>">
									<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEV()%>', 'dd/mm/y');">
								
									<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DEV()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DEV()%>">	
								</div>										
								<%} %>
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_DEV)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Type :</span>
									<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_DEV() %>">
										<%=process.forComboHTML(process.getVAL_LB_TYPE_DEV(), process.getVAL_LB_TYPE_DEV_SELECT()) %>
									</SELECT>
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" name="<%= process.getNOM_ST_LIB_DEV() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_DEV() %>">				
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Priorisation :</span>
									<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_ST_PRIO_DEV() %>" size="5" type="text" value="<%= process.getVAL_ST_PRIO_DEV() %>">
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Echéance :</span>
									<input id="<%=process.getNOM_ST_DATE_DEV()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEV() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEV() %>">
									<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEV()%>', 'dd/mm/y');">
								
									<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_DEV()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DEV()%>">	
								</div>										
								<%} %>
								<%if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_DEV)) {%>	
								<div>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Type :</span>
									<SELECT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_LB_TYPE_DEV() %>">
										<%=process.forComboHTML(process.getVAL_LB_TYPE_DEV(), process.getVAL_LB_TYPE_DEV_SELECT()) %>
									</SELECT>
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Libellé :</span>
									<INPUT class="sigp2-saisie" maxlength="1000" disabled="disabled" name="<%= process.getNOM_ST_LIB_DEV() %>" size="70" type="text" value="<%= process.getVAL_ST_LIB_DEV() %>">				
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Priorisation :</span>
									<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_ST_PRIO_DEV() %>" size="5" type="text" value="<%= process.getVAL_ST_PRIO_DEV() %>">
									<BR/>
									<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:80px;">Echéance :</span>
									<input class="sigp2-saisie" disabled="disabled"	name="<%= process.getNOM_ST_DATE_DEV() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEV() %>">
									
									<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DEV()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DEV()%>">	
								</div>										
								<%} %>			
								<BR/><BR/>
							</div>
							<BR/><BR/>
							<INPUT type="submit" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>" class="sigp2-Bouton-100">
						<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION)){ %>
									<span style="position:relative;width:35px;">
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
									</span>
									<span style="margin-left:5px;position:relative;width:90px;text-align: center;">Date</span> 
									<span style="position:relative;width:200px;text-align: left;">Nom</span> 
									<span style="position:relative;text-align: left">Commentaire</span> 
								
									<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
										<table class="sigp2NewTab" style="text-align:left;width:980px;">
										<%
										int indiceActes = 0;
										if (process.getListeDocuments()!=null){
											for (int i = 0;i<process.getListeDocuments().size();i++){
											%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">
													<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
													<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
												</td>
												<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
											</tr>
										<%
											indiceActes++;
											}
										}
										%>
										</table>	
									</div>	
									<% if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION)){ %>
									<div>		
										<span class="sigp2" style="width:130px;" >Commentaire :</span><INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
										<BR/>
										<BR/>
										<span class="sigp2Mandatory" style="width:130px;">Fichier : </span> 
										<% if(process.fichierUpload == null){ %>
										<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
										<%}else{ %>
										<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
										<% }%>
										<br />
									</div>
									<div style="text-align: left">
										<BR/><BR/>
										<span class="sigp2Mandatory" >Une fois la validation terminée, il faut attendre environ 3min que le document soit bien téléchargé. Si passez ce délai, et après rafraichissement de la page, le document n'apparait pas, merci de contacter le chef de projet.</span>
									</div>
									<div style="text-align: center">
										<BR/><BR/>
										<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DOCUMENT_CREATION()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
									</div>
								<%}%>
						<%}else if (process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION_DATE)){%>
								<%if(process.getListeEvaluateurEae().size()>0){ %>
									<table class="sigp2NewTab" style="text-align:left;width:680px;">
									<tr>
										<td>Evaluateur</td>
										<td>Fonction</td>
										<td>Date entrée fonction</td>
										<td>Date entrée service</td>
										
									</tr>
									<%
									int indiceEvaluateur = 0;
										for (int i = 0;i<process.getListeEvaluateurEae().size();i++){
											EaeEvaluateur evaluateur = process.getListeEvaluateurEae().get(i);
									%>
											<tr>
												<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: center;"><%=process.getVAL_ST_EVALUATEUR_NOM(indiceEvaluateur)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">&nbsp;<%=process.getVAL_ST_EVALUATEUR_FONCTION(indiceEvaluateur)%></td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">
													<INPUT class="sigp2-saisie" maxlength="10"  name="<%= process.getNOM_ST_EVALUATEUR_DATE_FONCTION(indiceEvaluateur) %>" size="10" type="text" value="<%= process.getVAL_ST_EVALUATEUR_DATE_FONCTION(indiceEvaluateur) %>" >
												</td>
												<td class="sigp2NewTab-liste" style="position:relative;;text-align: left;">
													<INPUT class="sigp2-saisie" maxlength="10"  name="<%= process.getNOM_ST_EVALUATEUR_DATE_SERVICE(indiceEvaluateur) %>" size="10" type="text" value="<%= process.getVAL_ST_EVALUATEUR_DATE_SERVICE(indiceEvaluateur) %>" >
												</td>
											</tr>
											<%
											indiceEvaluateur++;
									}%>
									</table>	
								<BR/><BR/>
								<%} %>	
								<span class="sigp2" style="width:130px;" >Date d'éntrée en qualité de fonctionnaire :</span>
								<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FONCTIONNAIRE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FONCTIONNAIRE() %>">
								<BR/><BR/>
								<span class="sigp2" style="width:130px;" >Date d'éntrée dans l'administration :</span>
								<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ADMINISTRATION() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_ADMINISTRATION() %>">
								<BR/><BR/>
								<span class="sigp2" style="width:130px;" >Date d'éntrée dans la fonction :</span>
								<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FONCTION() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FONCTION() %>">
								<BR/><BR/>
								<div style="text-align: center">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DATE()%>">
									<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
								</div>
						<%} %>						
						
					</FIELDSET>
			
		<%} %>		
			
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Anciens EAEs de l'agent</legend>
			<table class="sigp2NewTab" style="text-align:left;width:980px;">
				<tr bgcolor="#EFEFEF">
					<td width="30px;">
						<INPUT name="<%=process.getNOM_PB_AJOUTER_ANCIEN_EAE()%>"  title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" >
					</td>
					<td width="70px;">
						Document
					</td>
					<td width="70px;">
						Nom Original
					</td>
					<td align="left">
						Commentaire
					</td>
				</tr>
				<%
				if (process.getListeAncienEAE()!=null){
					for (int i = 0;i<process.getListeAncienEAE().size();i++){
						Document doc = (Document) process.getListeAncienEAE().get(i);	
						Integer id = doc.getIdDocument();
				%>
				<tr>
					<td>
						<a href="<%=process.getVAL_ST_URL_DOC(id)%>" title="Consulter le document" target="_blank" >
							<img onkeydown="" onkeypress="" onkeyup="" src="images/oeil.gif" height="16px" width="16px" title="Voir le document" />
						</a>
				    </td>
					<td>
						<%=process.getVAL_ST_DOCUMENT_ANCIEN_EAE(id)%>
					</td>
					<td>
						<%=process.getVAL_ST_NOM_ORI_DOC(id)%>
					</td>
					<td>
						<%=process.getVAL_ST_COMMENTAIRE_ANCIEN_EAE(id)%>
					</td>
				</tr>
				<%
					}
				}%>
			</table>	
			
			<% if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION_ANCIEN_EAE)){ %>
			<table>
				<tr>
					<td width="100px;">
						<span class="sigp2">Commentaire :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE_ANCIEN_EAE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE_ANCIEN_EAE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Année :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_ANNEE_ANCIEN_EAE() %>" size="5" type="text" value="<%= process.getVAL_EF_ANNEE_ANCIEN_EAE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Fichier : </span> 
					</td>
					<td>
						<% if(process.fichierUpload == null){ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT_ANCIEN_EAE() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT_ANCIEN_EAE() %>" >
						<%}else{ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT_ANCIEN_EAE() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT_ANCIEN_EAE() %>" >
						<% }%>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DOCUMENT_CREATION_ANCIEN_EAE()%>">
						<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
					</td>
				</tr>
			</table>
			<%}%>	
		</FIELDSET>	
		<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">
		<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET1" id="ONGLET1" style="visibility: hidden;">	
		<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET3" id="ONGLET3" style="visibility: hidden;">
		<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET4" id="ONGLET4" style="visibility: hidden;">
		<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET5" id="ONGLET5" style="visibility: hidden;">
		<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET6" id="ONGLET6" style="visibility: hidden;">		
		<%=process.getUrlFichier()%>
		</FORM>
	<%} %>	
	</BODY>
</HTML>