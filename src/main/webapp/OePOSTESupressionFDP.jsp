<%@page import="nc.mairie.metier.poste.ActionFdpJob"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<HTML>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTESupressionFDP" id="process" scope="session"></jsp:useBean>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Gestion des suppression des FDP à partir d'organigramme</TITLE>
<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
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
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
</HEAD>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<fieldset class="sigp2Fieldset" style="width: 1030px; float: left">
                <legend class="sigp2Legend">Fiches de poste supprimées via organigramme</legend>  
				<BR/>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher tout" name="<%=process.getNOM_PB_AFFICHER()%>">  
				<INPUT type="submit" class="sigp2-Bouton-200" value="Afficher les erreurs" name="<%=process.getNOM_PB_AFFICHER_ERREUR()%>">   
	            <BR/><BR/>		
				<table class="display" id="tabSuppFDP">
					<thead>
						<tr>
							<th>Agent</th>
							<th>Date</th>
							<th>Info</th>			
						</tr>
					</thead>
					<tbody>
					<%
						for (int j = 0;j<process.getListeJobSuppression().size();j++){
							ActionFdpJob action = process.getListeJobSuppression().get(j);
							Integer i = action.getIdActionFdpJob();
					%>
						<tr>
							<td><%=process.getVAL_ST_LIB_AGENT(i)%></td>
							<td><%=process.getVAL_ST_DATE(i)%></td>
							<td><%=process.getVAL_ST_INFO(i)%></td>								
						</tr>
					<%
								}
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabSuppFDP').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [null,null,null],
							"sDom": '<"H"fl>t<"F"iT>',
							"sScrollY": "375px",
							"bPaginate": false,
							"oTableTools": {
								"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"fdpSupprimees","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
								"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
							}
					    });
					} );
				</script>       
		</fieldset>
		
	
	</FORM>
</BODY>
</HTML>
