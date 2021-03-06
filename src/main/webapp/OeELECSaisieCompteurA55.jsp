<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<TITLE>Gestion des compteurs</TITLE>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
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
//function pour changement couleur arriere plan ligne du tableau
function SelectLigne(id,tailleTableau)
{
	for (i=0; i<tailleTableau; i++){
 		document.getElementById(i).className="";
	} 
 document.getElementById(id).className="selectLigne";
}

function testClickEnrigistrer(){
	if(event.keyCode == 13){
		executeBouton('NOM_PB_VALIDER');
	}
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.election.OeELECSaisieCompteurA55" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM onkeypress="testClickEnrigistrer();" name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Délégation DP</legend>
				
				<span class="sigp2" style="width:40px">Matricule : </span>
            	<span width="150px">
	                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_DEMANDE()%>" size="4" maxlength="4" type="text" value="<%= process.getVAL_ST_AGENT_DEMANDE()%>">
	                <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>');">
	                <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>');">
            	</span>
				<span style="margin-left:15px;">
					<span class="sigp2">Date début : </span>
				</span>
				<span>
					<input id="<%=process.getNOM_ST_DATE_MIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MIN()%>">
					<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MIN()%>', 'dd/mm/y');">
				</span>
				<span style="margin-left:15px;">
					<span class="sigp2">Date fin : </span>
				</span>
				<span>
					<input id="<%=process.getNOM_ST_DATE_MAX()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MAX()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MAX()%>" >
					<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MAX()%>', 'dd/mm/y');">
				</span>
                <INPUT style="margin-left:15px;" type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER()%>">	
				<br /><br />
	          	
				<div style="overflow: auto;height: 250px;width:1000px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="50px;">
								   <img title="ajouter" border="0" src="images/ajout.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_AJOUTER()%>');" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								</td>
								<td align="center" width="60px;">Matricule</td>
								<td width="300px;">Agent</td>
								<td align="center" width="90px;">Début</td>
								<td align="center" width="90px;">Fin</td>
								<td align="center" width="90px;">Nb heures</td>
								<td align="center">Motif</td>
							</tr>
							<%
							if (process.getListeCompteur()!=null){
								for (int i = 0;i<process.getListeCompteur().size();i++){
							%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeCompteur().size()%>)">
										<td class="sigp2NewTab-liste" align="center">
											<%if(process.peutModifierCompteur(i)){ %>
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(i)%>">
											<%} %>				
										</td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_MATRICULE(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_FIN(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_HEURES(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_MOTIF(i)%></td>
									</tr>
									<%
								}
							}%>
						</table>	
				</div>	
				<jsp:include page="Pagination.jsp" />
		</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
			
			<table>
				<tr>
					<td width="100px;">
						<span class="sigp2Mandatory">Agent :</span>
					</td>
					<td>
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>">
                        	<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>');">
                        <%}else{ %>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATE()%>" disabled="disabled" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATE()%>">
                        <%} %>
                    </td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEBUT() %>">
						<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN() %>">
						<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Nb heures/min :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_NB_HEURES() %>" size="4" type="text"  value="<%= process.getVAL_ST_NB_HEURES() %>">
						<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_NB_MINUTES() %>" size="4" type="text"  value="<%= process.getVAL_ST_NB_MINUTES() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Motif :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>">
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
							<INPUT type="submit" class="sigp2-Bouton-100" value="Creer" name="<%=process.getNOM_PB_VALIDER()%>">
						<%}else{ %>
							<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER()%>">
						<%} %>	 
                        <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">	
					</td>
				</tr>
			</table>			
			<%} %>
		</FIELDSET>
		<%} %>

    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER()%>" value="AJOUTER">
    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATE()%>" value="RECHERCHERAGENTCREATE">
	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>" value="RECHERCHERAGENTDEMANDE">
    <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>" value="SUPPRECHERCHERAGENTDEMANDE">
	</FORM>
</BODY>
</HTML>