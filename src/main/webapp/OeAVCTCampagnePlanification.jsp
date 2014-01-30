<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.spring.domain.metier.EAE.CampagneAction"%>
<%@page import="nc.mairie.technique.Services"%>
<%@page import="nc.mairie.spring.domain.metier.EAE.CampagneEAE"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion de la planification de la campagne</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
		
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></script>

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
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTCampagnePlanification" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<BR/>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche avancée d'une fiche de poste">
				<LEGEND class="sigp2Legend">Choix de la campagne</LEGEND>
				<BR/>
				<span class="sigp2Mandatory" style="width:60px;">Année :</span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
				</SELECT>
				<INPUT type="submit" value="Changer" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_LANCER()%>">
				<BR>
			</FIELDSET>
			<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Planning et liste de diffusion des alertes</legend>
			<br/>
			<span style="position:relative;width:9px;"></span>
			<span style="position:relative;width:60px;">
			<%if(process.getCampagneCourante()!= null && process.getCampagneCourante().estOuverte()){ %>
				<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
			<%} %>
			</span>
			<span style="position:relative;width:45px;text-align: center;">Docs joint</span>
			<span style="position:relative;width:100px;text-align: center;">Action</span>
			<span style="position:relative;width:90px;text-align: center;">Transmettre le</span>
			<span style="position:relative;width:300px;text-align: center;">Message</span>
			<span style="position:relative;width:150px;text-align: center;">Action à réaliser par</span>
			<span style="position:relative;width:90px;text-align: center;">A faire pour le</span>
			<span style="position:relative;width:70px;text-align: center;">Fait le</span>
			<br/>
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<%
						int indiceAction = 0;
						if (process.getListeAction()!=null){
							for (int i = 0;i<process.getListeAction().size();i++){
								CampagneAction action = process.getListeAction().get(i);
						%>
							<tr id="<%=indiceAction%>" onmouseover="SelectLigne(<%=indiceAction%>,<%=process.getListeAction().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="left">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAction)%>">
									<%if(process.getCampagneCourante().estOuverte()){ %>
										<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAction)%>">
									<%} %>
				    				<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAction)%>">
				    				<%if(!process.isMailDiffuse(action) && process.getCampagneCourante().estOuverte()){ %>
				    					<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceAction)%>">
				    				<% }%>	
				    			</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceAction)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: center;"><%=process.getVAL_ST_NOM_ACTION(indiceAction)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_TRANSMETTRE(indiceAction)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: center;"><%=process.getVAL_ST_MESSAGE(indiceAction)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: center;"><%=process.getVAL_ST_REALISER_PAR(indiceAction)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_POUR_LE(indiceAction)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: center;"><%=process.getVAL_ST_FAIT_LE(indiceAction)%></td>
							</tr>
							<%
							indiceAction++;
							}
						}%>
				</table>	
			</div>	
			<BR/><BR/>	
		</FIELDSET>
	<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
			<div>	
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Action :</span>
				<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>
				<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_ST_NOM_ACTION() %>" size="100" type="text" value="<%= process.getVAL_ST_NOM_ACTION() %>">
				<%}else{ %>	
				<INPUT disabled="disabled" class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_ST_NOM_ACTION() %>" size="100" type="text" value="<%= process.getVAL_ST_NOM_ACTION() %>">
				<%} %>			
				<BR/><BR/>
				
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Message :</span><br/>
				<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>
				<textarea style="margin-left:20px;position:relative;" rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_MESSAGE()%>" ><%= process.getVAL_ST_MESSAGE() %></textarea>
				<%}else{ %>	
				<textarea style="margin-left:20px;position:relative;" readonly="readonly" rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_MESSAGE()%>" ><%= process.getVAL_ST_MESSAGE() %></textarea>
				<%} %>			
				<BR/><BR/>
				
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">A transmettre le :</span>
				<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>
				<input class="sigp2-saisie" name="<%= process.getNOM_ST_TRANSMETTRE() %>" size="10" type="text"	value="<%= process.getVAL_ST_TRANSMETTRE() %>">
				<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_TRANSMETTRE()%>', 'dd/mm/y');">
				<%}else{ %>
				<input disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_ST_TRANSMETTRE() %>" size="10" type="text"	value="<%= process.getVAL_ST_TRANSMETTRE() %>">
				<%} %>
				<BR/><BR/>
				
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">A réaliser par :</span>
				<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>
					<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="100" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
					<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          			<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
				<%}else{ %>
					<INPUT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="100" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
				<%} %>
				<INPUT name="<%= process.getNOM_ST_ID_AGENT() %>" type="hidden" value="<%= process.getVAL_ST_ID_AGENT() %>">          		
          		<BR/><BR/>
				
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">A faire pour le :</span>
				<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>
				<input class="sigp2-saisie"	name="<%= process.getNOM_ST_POUR_LE() %>" size="10" type="text"	value="<%= process.getVAL_ST_POUR_LE() %>">
				<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_POUR_LE()%>', 'dd/mm/y');">
				<%}else{ %>	
				<input disabled="disabled" class="sigp2-saisie"	name="<%= process.getNOM_ST_POUR_LE() %>" size="10" type="text"	value="<%= process.getVAL_ST_POUR_LE() %>">
				<%} %>			
				<BR/><BR/>
				
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">Fait le :</span>
				<input class="sigp2-saisie"	name="<%= process.getNOM_ST_FAIT_LE() %>" size="10" type="text"	value="<%= process.getVAL_ST_FAIT_LE() %>">
				<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_FAIT_LE()%>', 'dd/mm/y');">				
				<BR/><BR/>
				
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">Observation :</span><br/>
				<textarea style="margin-left:20px;position:relative;" rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_COMMENTAIRE()%>" ><%= process.getVAL_ST_COMMENTAIRE() %></textarea>
				<BR/><BR/>
				
				
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;"> Destinataires des alertes : </span>
				<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>
		        <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_DESTINATAIRE()%>">
		        <%} %>
				<br/>
	            <%if(process.getListeDestinataireMulti().size()>0){ %>
					<div style="overflow: auto;height: 120px;width:1000px;margin-left:20px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<%
						int indiceActeur = 0;
						if (process.getListeDestinataireMulti()!=null){
							for (int i = 0;i<process.getListeDestinataireMulti().size();i++){
						%>
								<tr id="<%=indiceActeur%>" onmouseover="SelectLigne(<%=indiceActeur%>,<%=process.getListeDestinataireMulti().size()%>)" >
											<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">		
												<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>									
												<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DESTINATAIRE(indiceActeur)%>">
												<%} %>
											</td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_AGENT(indiceActeur)%></td>
								</tr>
								<%
								indiceActeur++;
							}
						}%>
						</table>	
					</div>
				<br/>
				<%} %>
				<BR/><BR/>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la campagne EAE</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;">	
					<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>	
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
					<%} %>
					</span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<%if(process.getActionCourante()==null || !process.isMailDiffuse(process.getActionCourante())){ %>		
									<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
									<%} %>
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
					<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
					<div>
					    <FONT color='red'>Veuillez valider votre choix.</FONT>
					    <BR/><BR/>
						<span class="sigp2" style="width:130px;">Nom du document : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Date : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Commentaire : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_DOC()%></span>
						<BR/>		
					</div>
					<% }else if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)){ %>
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
					<%}%>
					</FIELDSET>	
			</div>
			
			<% } else{ %>
			<div>
		    	<% if (process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)) {%>
					<FONT color='red'>Veuillez valider votre choix.</FONT>
					<BR/>
				<% } %>
		    	<BR/>
		    	<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">Action : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_ACTION()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">Message : </span><br/>
				<textarea readonly="readonly" style="margin-left:20px;position:relative;" rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_MESSAGE()%>" ><%= process.getVAL_ST_MESSAGE() %></textarea>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">A transmettre le : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_TRANSMETTRE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">A réaliser par : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_AGENT()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">A faire pour le : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_POUR_LE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">Fait le : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_FAIT_LE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:100px;">Obervation : </span><br/>
				<textarea readonly="readonly" style="margin-left:20px;position:relative;" rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_COMMENTAIRE()%>" ><%= process.getVAL_ST_COMMENTAIRE() %></textarea>
				<BR/><BR/>	
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;"> Destinataires des alertes :</span>				
				<BR/><BR/>	
	            <%if(process.getListeDestinataireMulti().size()>0){ %>
					<table class="sigp2NewTab" style="margin-left:20px;position:relative;text-align:left;width:480px;">
						<%
						int indiceActeur = 0;
						if (process.getListeDestinataireMulti()!=null){
							for (int i = 0;i<process.getListeDestinataireMulti().size();i++){
						%>
								<tr>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_AGENT(indiceActeur)%></td>
								</tr>
								<%
								indiceActeur++;
							}
						}%>
					</table>
				<br/>
				<%} %>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la campagne EAE</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;"></span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
				</FIELDSET>
			</div>
			<%} %>
			<BR/>
			<div style="width:100%; text-align:center;">
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)){ %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<%} %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
		</FIELDSET>
	<%} %>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENT">
	
	<%=process.getUrlFichier()%>
	</FORM>
</BODY>
</HTML>