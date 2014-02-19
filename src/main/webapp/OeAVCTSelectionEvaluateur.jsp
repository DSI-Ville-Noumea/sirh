<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.agent.AgentNW"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<TITLE>Sélection des évaluateurs des EAE</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>

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
<jsp:useBean class="nc.mairie.gestionagent.process.avancement.OeAVCTSelectionEvaluateur" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp"%>
	<FORM name="formu" method="POST" class="sigp2-titre">
	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche d'un agent">
				<LEGEND class="sigp2Legend">Recherche d'un agent</LEGEND>
				<TABLE border="0" cellpadding="0" cellspacing="0">
					<TBODY>
						<TR>
							<TD class="sigp2" valign="top">
								<INPUT type="radio" align="bottom" <%= process.forRadioHTML(process.getNOM_RG_RECHERCHE(),process.getNOM_RB_RECH_NOM())%>>
							Matricule ou début du nom de l'agent</TD>
						</TR>
						<TR>
							<TD class="sigp2">
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_RECHERCHE(),process.getNOM_RB_RECH_PRENOM())%>>
								Début du prénom de l'agent</TD>
						</TR>
						<tr><td><BR/></td></tr>
						<TR>
							<TD>
								<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display : 'none';">
								<INPUT class="sigp2-saisie" id="service" maxlength="60" name="<%= process.getNOM_EF_ZONE() %>" size="20" type="text" value="<%= process.getVAL_EF_ZONE() %>">
	                  			<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
							</TD>
						</TR>
						
					</TBODY>
	            </TABLE>
	            <BR/><BR/>
	            <% if(process.getListeEvaluateursPossible().size()>0){%>
	           	<div style="overflow: auto;height: 150px;width:1030px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:1010px;">
					<%
							for (int i = 0;i<process.getListeEvaluateursPossible().size();i++){
								AgentNW ag = process.getListeEvaluateursPossible().get(i);
								Integer indiceAgent = Integer.valueOf(ag.getIdAgent());
					%>
						<tr id="<%=indiceAgent%>" onmouseover="SelectLigne(<%=indiceAgent%>,<%=process.getListeEvaluateursPossible().size()%>)" >
							<td style="display:none;"><%=process.getVAL_ST_ID_AGENT_POSSIBLE(indiceAgent)%></td>
							<td class="sigp2NewTab-liste" style="text-align: center;width:40px;"><INPUT type="checkbox" onclick='executeBouton("<%=process.getNOM_PB_OK(indiceAgent)%>")' <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_POSSIBLE(indiceAgent),process.getVAL_CK_SELECT_LIGNE_POSSIBLE(indiceAgent))%>></td>
							<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_LIB_AGENT_POSSIBLE(indiceAgent)%></td>
							<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_OK(indiceAgent)%>" value="x"></td>
						</tr>
					<%
					}%>
					</table>	
				</div>
				<%} %>
			</FIELDSET>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Sélection d'un evaluateur</legend>
		    <BR/>
		    <FONT color='red'> Attention vous n'avez le droit qu'à 2 agents évaluateurs maximum.</FONT>
			<BR/><BR/>
			<table class="display" id="tabActiSelect">
				<thead>
					<tr>
						<th>idAgent</th>
						<th width="50" >Selection</th>
						<th>Libellé</th>
					</tr>
				</thead>
				<tbody>
				<%
					for (int i = 0;i<process.getListeEvaluateurs().size();i++){
						AgentNW ag = process.getListeEvaluateurs().get(i);
						Integer indiceActeur = Integer.valueOf(ag.getIdAgent());
				%>
						<tr>
							<td><%=process.getVAL_ST_ID_AGENT(indiceActeur)%></td>
							<td><INPUT type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE(indiceActeur),process.getVAL_CK_SELECT_LIGNE(indiceActeur))%>></td>
							<td><%=process.getVAL_ST_LIB_AGENT(indiceActeur)%></td>
						</tr>						
				<%
					}
				%>
				</tbody>
			</table>
			<INPUT type="submit" value="OK" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>">
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabActiSelect').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false,"bSortable":false},null],
						"sScrollY": "150px",
						"bPaginate": false,
						"sDom": '<"H">t<"F">'						
				    });
				} );
			</script>
		</FIELDSET>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	</FORM>
</BODY>
</HTML>