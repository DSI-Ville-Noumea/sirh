<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des extraits de casier judiciaire d'un agent</TITLE>

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
<jsp:useBean
 class="nc.mairie.gestionagent.process.agent.OeAGENTCasierJud" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">			
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des extraits de casier judiciaire d'un agent</legend>
				    <br/>
				    <span style="margin-left: 5px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>"></span>
				    <span style="margin-left: 50px;">Date de l'extrait</span>
					<span style="margin-left: 5px;">Numéro</span>
					<span style="margin-left: 70px;">Privation</span>
					<span style="margin-left: 10px;">Commentaire</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCasierJud = 0;
							if (process.getListeCasierJud()!=null){
								for (int i = 0;i<process.getListeCasierJud().size();i++){
							%>
									<tr id="<%=indiceCasierJud%>" onmouseover="SelectLigne(<%=indiceCasierJud%>,<%=process.getListeCasierJud().size()%>)">
										<td class="sigp2NewTab-liste" style="width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceCasierJud)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceCasierJud)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceCasierJud)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceCasierJud)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE(indiceCasierJud)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:110px;text-align: left;"><%=process.getVAL_ST_NUM(indiceCasierJud)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:60px;text-align: center;"><%=process.getVAL_ST_PRIVATION(indiceCasierJud)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceCasierJud)%></td>
									</tr>
									<%
									indiceCasierJud++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		<BR/>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) ){ %>
		<div>
		<table>
			<tr>
				<td width="180px;">
					<span class="sigp2Mandatory" >Date de l'extrait : </span>
				</td>
				<td>
				    <span>
						<INPUT id="<%=process.getNOM_EF_DATE_EXTRAIT()%>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_EXTRAIT() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_EXTRAIT() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_EXTRAIT() %>', 'dd/mm/y');">
					</span>
				</td>
			</tr>
			<tr>
				<td>
					<span class="sigp2Mandatory">Numéro : </span>
				</td>
				<td>
					<span>
						<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_NUM_EXTRAIT() %>" size="10"
							type="text" value="<%= process.getVAL_EF_NUM_EXTRAIT() %>">
					</span>
				</td>
			</tr>
			<tr>
				<td>
					<span class="sigp2Mandatory">Privation des droits civiques : </span>
				</td>
				<td>
					<INPUT type="radio"	<%= process.forRadioHTML(process.getNOM_RG_PRIV_DROITS_CIVIQUE(),process.getNOM_RB_PRIV_DROITS_CIVIQUE_O())%>> <span class="sigp2Mandatory">Oui</span>
					<INPUT type="radio"	<%= process.forRadioHTML(process.getNOM_RG_PRIV_DROITS_CIVIQUE(),process.getNOM_RB_PRIV_DROITS_CIVIQUE_N())%>> <span class="sigp2Mandatory">Non</span>
				</td>
			</tr>
			<tr>
				<td>
			<span class="sigp2">Commentaire : </span>
				</td>
				<td>
			<span>
				<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE_EXTRAIT() %>" size="100"
					type="text" value="<%= process.getVAL_EF_COMMENTAIRE_EXTRAIT() %>">
			</span>
				</td>
			</tr>
		</table>
		</div>
		<%}else{ %>
		<div>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULATION) ){ %>
			<FONT color='red'>Veuillez valider votre choix.</FONT>
			<BR/><BR/>
			<% } %>
			<table>
				<tr>
					<td width="180px">
						<span class="sigp2">Date de l'extrait : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_EXTRAIT()%></span>
					</td>
				</tr>
				<tr>
					<td width="180px">
						<span class="sigp2">Numéro : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_NUM_EXTRAIT()%></span>
					</td>
				</tr>
				<tr>
					<td width="180px">
						<span class="sigp2">Privation des droits civiques : </span>
					</td>
					<td>
						<INPUT type="radio"	disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_PRIV_DROITS_CIVIQUE(),process.getNOM_RB_PRIV_DROITS_CIVIQUE_O())%>><span class="sigp2">Oui</span>
						<INPUT type="radio"	disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_PRIV_DROITS_CIVIQUE(),process.getNOM_RB_PRIV_DROITS_CIVIQUE_N())%>><span class="sigp2">Non</span>
					</td>
				</tr>
				<tr>
					<td width="180px">
						<span class="sigp2">Commentaire : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_COMMENTAIRE_EXTRAIT()%></span>
					</td>
				</tr>
			</table>
		</div>
		<BR/>
		<%} %>
		<div style="text-align: center">
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULATION) ){ %>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<% } %>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</div>
		<BR/>
	</FIELDSET>
<%} %>
</FORM>
<%} %>
</BODY>
</HTML>