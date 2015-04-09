<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des contacts d'un agent</TITLE>
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
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean id="process" class="nc.mairie.gestionagent.process.agent.OeAGENTADMINISTRATIONGestion" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp"%>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">			
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des administrations d'un agent</legend>
				    <br/>
				    <span style="margin-left: 5px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>"></span>
				    <span style="margin-left: 50px;">Administration</span>
				    <span style="margin-left: 525px;">Fonctionnaire</span>
					<span style="margin-left: 15px;">Date d'entrée</span>
					<span style="margin-left: 20px;">Date de sortie</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceAdministration = 0;
							if (process.getListeAgentAdministrations()!=null){
								for (int i = 0;i<process.getListeAgentAdministrations().size();i++){
							%>
									<tr id="<%=indiceAdministration%>" onmouseover="SelectLigne(<%=indiceAdministration%>,<%=process.getListeAgentAdministrations().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAdministration)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAdministration)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAdministration)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceAdministration)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:600px;text-align: left;"><%=process.getVAL_ST_ADMINISTRATION(indiceAdministration)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_FONCTIONNAIRE(indiceAdministration)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_ENTREE(indiceAdministration)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_DATE_SORTIE(indiceAdministration)%></td>
									</tr>
									<%
									indiceAdministration++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>

<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<BR>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
	<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<br/>
			<span class="sigp2Mandatory">Administration : </span>
			<span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ADMINISTRATION() %>">
					<%=process.forComboHTML(process.getVAL_LB_ADMINISTRATION(), process.getVAL_LB_ADMINISTRATION_SELECT()) %>
				</SELECT>
			</span>
			<BR/><BR/>
			<span class="sigp2Mandatory"> Fonctionnaire : </span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FONCTIONNAIRE(),process.getNOM_RB_FONCTIONNAIRE_O())%> >Oui
				<span style="width:10px"></span>			
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FONCTIONNAIRE(),process.getNOM_RB_FONCTIONNAIRE_N())%> >Non
				<span style="width:107px">
			</span>
			<BR/><BR/>
			<span class="sigp2Mandatory">Date d'entrée : </span>
			<span style="padding-left:5px;">
				<INPUT id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEBUT() %>">
			</span>
			<span>
				<IMG src="images/calendrier.gif" hspace="5"	onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
			</span>
			<span class="sigp2" style="padding-left:10px;">Date de sortie : </span>
			<span style="padding-left:10px;">
				<INPUT id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN() %>">
			</span>
			<span>
				<IMG src="images/calendrier.gif" hspace="5"	onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');">
			</span>
			<BR/>
	<%}else{ %>
			<% if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %>
				<FONT color='red'>Veuillez valider votre choix.</FONT>
				<BR/><BR/>
			<% } %>
			<span class="sigp2">Administration</span>
			<span class="sigp2-saisie" style="margin-left: 10px;"><%=process.getVAL_ST_ADMINISTRATION() %></span>
			<BR/>
			<span class="sigp2">Fonctionnaire</span>
			<span class="sigp2-saisie" style="margin-left: 10px;"><%=process.getVAL_ST_FONCTIONNAIRE() %></span>
			<BR/>
			<span class="sigp2">Date d'entrée</span>
			<span class="sigp2-saisie" style="margin-left: 10px;"><%=process.getVAL_EF_DATE_DEBUT() %></span>
			<span style="margin-left: 10px;" class="sigp2">Date de sortie</span>
			<span class="sigp2-saisie"  style="margin-left: 10px;"><%=process.getVAL_EF_DATE_FIN() %></span>
			<BR/>
	<%} %>
			<BR>
			<div style="text-align: center">
			<% if(!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<% } %>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
			<BR/>
		</FIELDSET>
<%}%>			
	</FORM>
<%} %>
</BODY>
</HTML>