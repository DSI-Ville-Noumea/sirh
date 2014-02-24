<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des utilisateurs</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>

<SCRIPT language="JavaScript">
//afin de s�lectionner un �l�ment dans une liste
function executeBouton(nom)
{
document.formu.elements[nom].click();
}

// afin de mettre le focus sur une zone pr�cise
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
			
			// afin d'afficher la hi�rarchie des services
			function agrandirHierarchy() {
			
				hier = 	document.getElementById('treeHierarchy');
			
				if (hier.style.display!='none') {
					reduireHierarchy();
				} else {
					hier.style.display='block';
				}
			}
			
			// afin de cacher la hi�rarchie des services
			function reduireHierarchy() {
				hier = 	document.getElementById('treeHierarchy');
				hier.style.display='none';
			}

</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:40px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>"></span>
				    <span style="position:relative;width:75px;text-align: left;">Login</span>
					<span style="position:relative;width:200px;text-align: left;">Agent</span>
					<span style="position:relative;text-align: left;">Groupes</span>
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
		<BR/>
		<BR/>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
		<BR/>
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %>
		<div>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom de l'utilisateur : </span>
			<span>
				<INPUT class="sigp2-saisie" maxlength="7" name="<%= process.getNOM_EF_NOM_UTILISATEUR() %>" size="20"
					type="text" value="<%= process.getVAL_EF_NOM_UTILISATEUR() %>">
			</span>
			<BR/><BR/><BR/>
		    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:960px;height:180px;">
			    <legend class="sigp2Legend">Groupes associ�s</legend>
			    <BR/>
    			<div align="left" style="float:left;display:block;width:40%;">
			    	<span style="margin-left:5px;position:relative;width:250px;">Groupes disponibles</span>
			    	<BR/>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_GROUPES_AUTRES() %>" style="width:100%;" size="10">
						<%=process.forComboHTML(process.getVAL_LB_GROUPES_AUTRES(), process.getVAL_LB_GROUPES_AUTRES_SELECT()) %>
					</SELECT>
			    </div>
    			<div align="center" style="float:left;display:block;vertical-align:middle;text-align:center;width:20%;height:100%;" >
					<BR/><BR/>
					<INPUT type="image" src="images/fleche-droite.png" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_GROUPE()%>">
					<BR/>
					<INPUT type="image" src="images/fleche-double-droite.png" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_TOUT()%>">
					<BR/><BR/>
					<INPUT type="image" src="images/fleche-gauche.png" height="20px" width="20px" name="<%=process.getNOM_PB_RETIRER_GROUPE()%>">
					<BR/>
					<INPUT type="image" src="images/fleche-double-gauche.png" height="20px" width="20px" name="<%=process.getNOM_PB_RETIRER_TOUT()%>">
			    </div>
				<div align="left" style="float:right;display:block;width:40%;">
			    	<span style="margin-left:5px;position:relative;width:250px;">Groupes de l'utilisateur</span>
			    	<BR/>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_GROUPES_UTILISATEUR() %>" style="width:100%;" size="10">
						<%=process.forComboHTML(process.getVAL_LB_GROUPES_UTILISATEUR(), process.getVAL_LB_GROUPES_UTILISATEUR_SELECT()) %>
					</SELECT>
			    </div>
			</FIELDSET>
		</div>
		<%}else{ %>
		<div>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Nom de l'utilisateur : </span>
			<span>
				<INPUT class="sigp2-saisie" maxlength="7" name="<%= process.getNOM_EF_NOM_UTILISATEUR() %>" size="80" disabled="disabled"
					type="text" value="<%= process.getVAL_EF_NOM_UTILISATEUR() %>">
			</span>
			<BR/><BR/><BR/>
		    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
			    <legend class="sigp2Legend">Groupes associ�s</legend>
			    <BR/>
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