<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.poste.Activite"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Gestion des fiches emploi</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/competence.js"></script>
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
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFEActivite" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;height:580px;">
		    <legend class="sigp2Legend">Liste des activités</legend>
		    <br/>			
			<BR/>
			<table class="display" id="tabActi">
				<thead>
					<tr>
						<th>idActi</th>
						<th width="50" >Selection</th>
						<th>Libellé</th>
					</tr>
				</thead>
				<tbody>
				<%
				if (process.getListeActivite()!=null){
					for (int i = 0;i<process.getListeActivite().size();i++){
						Activite acti = process.getListeActivite().get(i);
						Integer indiceActi = Integer.valueOf(acti.getIdActivite());
				%>
						<tr>
							<td><%=process.getVAL_ST_ID_ACTI(indiceActi)%></td>
							<td><INPUT type="checkbox" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE(indiceActi),process.getVAL_CK_SELECT_LIGNE(indiceActi))%> onClick='selectLigne("<%=indiceActi %>","<%=process.getListeActivite().size()%>")'></td>
							<td><%=process.getVAL_ST_LIB_ACTI(indiceActi)%></td>
						</tr>						
				<%
					}
				}
				%>
				</tbody>
			</table>
			<BR/><BR/>
			<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
	            <INPUT title="Créer une activité" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER()%>">
	            <INPUT title="Modifier une activité" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER()%>">
	            <INPUT title="Supprimer une activité"  type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER()%>">
			</div>
            <br/><br/>
            <% if (process.getVAL_ST_ACTION()=="") {%>
	            <div style="display:none">
			<% } else {%>
	            <div style="display:block">
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label><br/>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="255"
						name="<%= process.getNOM_EF_DESC_ACTIVITE() %>" size="120"
						type="text" value="<%= process.getVAL_EF_DESC_ACTIVITE() %>" style="margin-right:10px;">
					<% if (process.ACTION_AJOUT.equals(process.getVAL_ST_ACTION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ACTIVITE()%>"></span>
					<%}%>
					<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ACTIVITE()%>"></span>
					<%}%>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label><br/>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="255"
						name="<%= process.getNOM_EF_DESC_ACTIVITE() %>" size="120" disabled="disabled"
						type="text" value="<%= process.getVAL_EF_DESC_ACTIVITE() %>" style="margin-right:10px;">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ACTIVITE()%>"></span>
				<%}%>
		    	<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ACTIVITE()%>"></span>
			<% } %>
	    	</div>
			<BR/><BR/>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabActi').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false,"bSortable":false},null],
						"sDom": '<"H"fl>t<"F"ip>'						
				    });
				} );
			</script>
			<BR/>
		</FIELDSET>
	</FORM>
</BODY>
</HTML>
