<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.poste.Competence"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<TITLE>Sélection d'une compétence</TITLE>

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
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFECompetenceSelection" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY">
	<%@ include file="BanniereErreur.jsp"%>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Sélection d'une compétence</legend>
		    <br/>
			<table class="display" id="tabCompSelect">
				<thead>
					<tr>
						<th>idComp</th>
						<th width="50" >Selection</th>
						<th>Libellé</th>
					</tr>
				</thead>
				<tbody>
				<%
				if (process.getListeCompetences()!=null){
					for (int i = 0;i<process.getListeCompetences().size();i++){
						Competence comp = process.getListeCompetences().get(i);
						Integer indiceComp = comp.getIdCompetence();
				%>
						<tr>
							<td><%=process.getVAL_ST_ID_COMP(indiceComp)%></td>
							<td><INPUT type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE(indiceComp),process.getVAL_CK_SELECT_LIGNE(indiceComp))%>></td>
							<td><%=process.getVAL_ST_LIB_COMP(indiceComp)%></td>
						</tr>						
				<%
					}
				}
				%>
				</tbody>
			</table>
			<BR/><BR/>
			<INPUT type="submit" value="Valider" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>">
			<BR/><BR/>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabCompSelect').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false,"bSortable":false},null],
						"sScrollY": "475px",
						"bPaginate": false,
						"sDom": '<"H"fl>t<"F"i>'						
				    });
				} );
			</script>
			<BR/>
		</FIELDSET>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	</FORM>
</BODY>
</HTML>