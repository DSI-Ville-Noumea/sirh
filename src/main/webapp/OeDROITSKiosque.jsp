<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des droits du kiosque</TITLE>

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
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.droits.process.OeDROITSKiosque"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Liste des approbateurs des pointages/absences</legend>
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:980px;">
				<tr>
					<td><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>"></td>
					<td><span><INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")'<%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_AGENT())%>> Agent</span></td>
					<td><span><INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")'<%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_SERVICE())%>> Service</span></td>
					<td align="center"><span>Délég <br> PTG</span></td>
					<td align="center"><span>Délég <br> ABS</span></td>
					<td align="center"><span>PTG</span></td>
					<td align="center"><span>ABS</span></td>					
				</tr>
				<%
				for (int indice = 0;indice<process.getListeApprobateurs().size();indice++){
					int i = process.getListeApprobateurs().get(indice).getApprobateur().getIdAgent();
				%>
					<tr>
						<td class="sigp2NewTab-liste" style="position:relative;width:35px;" align="center">
				    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(i)%>">
				    	</td>
						<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_AGENT(i)%></td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_SERVICE(i)%></td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_DELEGATAIRE_PTG(i)%>
							<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DELEGATAIRE_PTG(i)%>">
				    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATAIRE_PTG(i)%>">				    										
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_DELEGATAIRE_ABS(i)%>
							<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DELEGATAIRE_ABS(i)%>">
				    		<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATAIRE_ABS(i)%>">				    										
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: center;">
							<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_DROIT_PTG(i),process.getVAL_CK_DROIT_PTG(i))%>>
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: center;">
							<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_DROIT_ABS(i),process.getVAL_CK_DROIT_ABS(i))%>>
						</td>
					</tr>
				<%}%>
				</table>	
			</div>	
			<BR/><BR/>
			<div style="width:100%; text-align:center;">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
        </FIELDSET>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_TRI()%>" value="TRI">
	</FORM>
</BODY>
</HTML>