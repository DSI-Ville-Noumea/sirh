<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<TITLE>Gestion des paramètres des jours fériés/chômés</TITLE>
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEJour" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des jours fériés par année</legend>
				    <br/>
				    <span style="margin-left:90px; ">Année</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceAnnee = 0;
								for (int i = 0;i<process.getListeAnnee().size();i++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAnnee)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAnnee)%>">
				    						<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAnnee)%>">				    						
				    					</td>
										<td class="sigp2NewTab-liste" style="position:relative;">&nbsp;<%=process.getVAL_ST_ANNEE(indiceAnnee)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;">&nbsp;
										<%if(i==0){ %>
				    						<INPUT type="submit" class="sigp2-Bouton-250" value="Créer une nouvelle année" name="<%=process.getNOM_PB_CREER_ANNEE()%>">
				    					<%} %>		
				    					</td>		    					
									</tr>
									<%
									indiceAnnee++;								
							}%>
						</table>	
						</div>	
				<BR/><BR/>	
				</FIELDSET>
				
				
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>

		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)||process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
				    <INPUT style="margin-left: 5px;" title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_JOUR()%>">				    
				    <span style="margin-left: 65px;">Date</span>
				    <span style="margin-left: 35px;">Type</span>
				    <span style="margin-left: 65px;">Description</span>
					<br/>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
								for (int j = 0;j<process.getListeJourFerie().size();j++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;" align="center">&nbsp;
											<%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_JOUR(j)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_JOUR(j)%>">
											<%} %>
				    					</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_JOUR(j)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: left;"><%=process.getVAL_ST_TYPE_JOUR(j)%></td>	
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_DESCRIPTION_JOUR(j)%></td>		    					
									</tr>
							<%	}%>
						</table>
					</div>
					<BR/>
					<%if(process.getVAL_ST_ACTION_JOUR().equals(process.ACTION_MODIFICATION_JOUR)||process.getVAL_ST_ACTION_JOUR().equals(process.ACTION_CREATION_JOUR)){%>	
					<table>
						<tr>
							<td width="60px;">
								<span class="sigp2Mandatory">Type :</span>
							</td>
							<td>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_JOUR() %>">
								<%=process.forComboHTML(process.getVAL_LB_TYPE_JOUR(), process.getVAL_LB_TYPE_JOUR_SELECT()) %>
							</SELECT>
							</td>
						</tr>
						<tr>
							<td>
								<span class="sigp2Mandatory">Date :</span>
							</td>
							<td>
								<input id="<%=process.getNOM_ST_DATE_JOUR()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_JOUR() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_JOUR() %>">
								<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_JOUR()%>', 'dd/mm/y');">	
							</td>
						</tr>
						<tr>
							<td>
								<span class="sigp2">Description :</span>
							</td>
							<td>
								<INPUT class="sigp2-saisie" maxlength="250" name="<%= process.getNOM_ST_DESCRIPTION() %>" size="70" type="text"  value="<%= process.getVAL_ST_DESCRIPTION() %>">	
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
								<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
							</td>
						</tr>
					</table>	
					<%}else if(process.getVAL_ST_ACTION_JOUR().equals(process.ACTION_SUPPRESSION_JOUR)){ %>	
					<table>
						<tr>
							<td width="60px;">
								<span class="sigp2Mandatory">Type :</span>
							</td>
							<td>
								<SELECT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_LB_TYPE_JOUR() %>">
									<%=process.forComboHTML(process.getVAL_LB_TYPE_JOUR(), process.getVAL_LB_TYPE_JOUR_SELECT()) %>
								</SELECT>
							</td>
						</tr>
						<tr>
							<td>
								<span class="sigp2Mandatory">Date :</span>
							</td>
							<td>
								<input class="sigp2-saisie" disabled="disabled" maxlength="10"	name="<%= process.getNOM_ST_DATE_JOUR() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_JOUR() %>">
							</td>
						</tr>
						<tr>
							<td>
								<span class="sigp2">Description :</span>
							</td>
							<td>
								<INPUT class="sigp2-saisie" disabled="disabled" maxlength="250" name="<%= process.getNOM_ST_DESCRIPTION() %>" size="70" type="text"  value="<%= process.getVAL_ST_DESCRIPTION() %>">
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
								<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
							</td>
						</tr>
					</table>	
					<%} %>
			<%} %>
		</FIELDSET>
		<%} %>
				
		</FORM>
	</BODY>
</HTML>