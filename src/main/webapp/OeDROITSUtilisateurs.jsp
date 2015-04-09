<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des utilisateurs</TITLE>

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
 class="nc.mairie.droits.process.OeDROITSUtilisateurs"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des utilisateurs</legend>
				    <br/>
				    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
				    <span style="margin-left: 30px;">Login</span>
					<span style="margin-left: 40px;">Agent</span>
					<span style="margin-left: 165px;">Groupes</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceUtil = 0;
							if (process.getListeUtilisateur()!=null){
								for (int i = 0;i<process.getListeUtilisateur().size();i++){
							%>
									<tr id="<%=indiceUtil%>" onmouseover="SelectLigne(<%=indiceUtil%>,<%=process.getListeUtilisateur().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="center">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceUtil)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceUtil)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:75px;text-align: left;"><%=process.getVAL_ST_NOM(indiceUtil)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_AGENT(indiceUtil)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_GROUPES(indiceUtil)%></td>
									</tr>
									<%
									indiceUtil++;
								}
							}%>
						</table>	
				</div>		
		</FIELDSET>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
		<BR/>
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %>
		<table>
			<tr>
				<td>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom de l'utilisateur : </span>
					<INPUT class="sigp2-saisie" maxlength="7" name="<%= process.getNOM_EF_NOM_UTILISATEUR() %>" size="20" type="text" value="<%= process.getVAL_EF_NOM_UTILISATEUR() %>">
				</td>
			</tr>
			<tr>
				<td width="960px;">
					<FIELDSET class="sigp2Fieldset" style="text-align:left;">
					    <legend class="sigp2Legend">Groupes associés</legend>
					    <table class="sigp2Mandatory">
					    	<tr>
					    		<td width="400px;">
							    	<span style="margin-left:5px;">Groupes disponibles</span>
							    	<BR/>
									<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_GROUPES_AUTRES() %>" style="width:100%;" size="10">
										<%=process.forComboHTML(process.getVAL_LB_GROUPES_AUTRES(), process.getVAL_LB_GROUPES_AUTRES_SELECT()) %>
									</SELECT>
					    		</td>
					    		<td width="160px;" align="center">
									<INPUT type="image" src="images/fleche-droite.png" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_GROUPE()%>">
									<BR/>
									<INPUT type="image" src="images/fleche-double-droite.png" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_TOUT()%>">
									<BR/><BR/>
									<INPUT type="image" src="images/fleche-gauche.png" height="20px" width="20px" name="<%=process.getNOM_PB_RETIRER_GROUPE()%>">
									<BR/>
									<INPUT type="image" src="images/fleche-double-gauche.png" height="20px" width="20px" name="<%=process.getNOM_PB_RETIRER_TOUT()%>">
					    		</td>
					    		<td width="400px;">
							    	<span style="margin-left:5px;">Groupes de l'utilisateur</span>
							    	<BR/>
									<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_GROUPES_UTILISATEUR() %>" style="width:100%;" size="10">
										<%=process.forComboHTML(process.getVAL_LB_GROUPES_UTILISATEUR(), process.getVAL_LB_GROUPES_UTILISATEUR_SELECT()) %>
									</SELECT>
					    		</td>
					    	</tr>
					    </table>
					</FIELDSET>
				</td>
			</tr>
		</table>		
		<%}else{ %>
		<div>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom de l'utilisateur : </span>
			<span>
				<INPUT class="sigp2-saisie" maxlength="7" name="<%= process.getNOM_EF_NOM_UTILISATEUR() %>" size="80" disabled="disabled"
					type="text" value="<%= process.getVAL_EF_NOM_UTILISATEUR() %>">
			</span>
			<BR/>
		    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
			    <legend class="sigp2Legend">Groupes associés</legend>
    			<div align="left" style="float:left;display:block;width:40%;">
			    	<span style="margin-left:5px;position:relative;width:250px;">Groupes de l'utilisateur</span>
			    	<BR/>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_GROUPES_UTILISATEUR() %>" style="width:100%;" size="10" disabled="disabled">
						<%=process.forComboHTML(process.getVAL_LB_GROUPES_UTILISATEUR(), process.getVAL_LB_GROUPES_UTILISATEUR_SELECT()) %>
					</SELECT>
			    </div>
				<div align="left" style="float:right;display:block;width:40%;">
			    	<span style="margin-left:5px;position:relative;width:250px;">Autres groupes</span>
			    	<BR/>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_GROUPES_AUTRES() %>" style="width:100%;" size="10" disabled="disabled">
						<%=process.forComboHTML(process.getVAL_LB_GROUPES_AUTRES(), process.getVAL_LB_GROUPES_AUTRES_SELECT()) %>
					</SELECT>			    
			    </div>
			</FIELDSET>
		</div>
		<%} %>
		</FIELDSET>
		<FIELDSET class="sigp2Fieldset" style="text-align:center;width:1030px;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>
        <BR>
<% } %>
</FORM>
</BODY>
</HTML>