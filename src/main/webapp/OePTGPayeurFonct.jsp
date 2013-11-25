<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.dto.ListEtatsPayeurDto"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>

<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Edition du payeur</TITLE>		


<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="JavaScript">
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
	<jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGPayeurFonct" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		
		<br />
		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<span style="color: red; margin-left:5px;">Attention, cette action est irr&eacute;versible !</span>
		<br />
		<INPUT type="submit" class="sigp2-Bouton-100" value="Lancer éditions" name="<%=process.getNOM_PB_LANCER_EDITIONS() %>" <%if(!process.isBoutonLancerEditionAffiche()){ %> disabled="disabled"<% } %> />
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Historique des éditions</legend>
		    <br/>
		    <span style="position:relative;width:210px;text-align: center;">Imprimé le <br> A <br> PAR</span>
		    <span style="position:relative;width:290px;text-align: center;">Libellé</span>
		    <span style="position:relative;width:150px;text-align: center;">Consulter</span>
			<br/>
			
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:650px;">
					<%
					int indice = 0;
					if (null != process.getListEtatsPayeurDto()){
						for (int i = 0; i < process.getListEtatsPayeurDto().size(); i++){
							int idEtat = process.getListEtatsPayeurDto().get(i).getIdEtatPayeur();
							%>
						<tr id="<%=indice%>" onmouseover="SelectLigne(<%=indice%>, <%=process.getListEtatsPayeurDto().size()%>)">
							
							<td class="sigp2NewTab-liste" style="position:relative;width:210px;text-align: center;"><%=process.getVAL_ST_USER_DATE_EDITION(idEtat) %></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:290px;text-align: center;"><%=process.getVAL_ST_LIBELLE_EDITION(idEtat) %></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: center;" align="center">
								<a href="UploadEtatPayeur?idEtatPayeur=<%=idEtat %>&nomFichier=<%=process.getVAL_FICHIER_EDITION(idEtat)%>" target="_blank"><img title="consulter" src="images/oeil.gif" height="15px" width="15px" border="0px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" /></a> 	
							</td>
						</tr>
						<%
						indice++;
						}
					} %>
				</table>
			</div>
		
		</FIELDSET>
		
	</FORM>
	</BODY>
</HTML>