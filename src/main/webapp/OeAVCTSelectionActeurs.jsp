<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.agent.Agent"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<TITLE>Sélection des destinataires des alertes</TITLE>

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

</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.avancement.OeAVCTSelectionActeurs" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY">
	<%@ include file="BanniereErreur.jsp"%>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Sélection d'un destinataire</legend>
		    <br/>			
			<BR/>
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
					for (int i = 0;i<process.getListeActeurs().size();i++){
								Agent ag = process.getListeActeurs().get(i);
								Integer indiceActeur = ag.getIdAgent();
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
			<BR/><BR/>
			<INPUT type="submit" value="OK" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>">
			<BR/>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabActiSelect').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false,"bSortable":false},null],
						"sScrollY": "475px",
						"bPaginate": false,
						"sDom": '<"H"fl>t<"F"i>'						
				    });
				} );
			</script>
		</FIELDSET>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	</FORM>
</BODY>
</HTML>