<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.technique.VariableGlobale"%>
<%@page import="nc.mairie.technique.UserAppli"%>
<%@page import="nc.mairie.metier.droits.Groupe"%>
<%@page import="nc.mairie.metier.droits.Element"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/tabDroits.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des droits de l'application</TITLE>

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

function doOnScroll(mondiv)
{
 	document.getElementById("first_col").style.top=(70-mondiv.scrollTop)+"px";
	document.getElementById("entete").style.left=(200-mondiv.scrollLeft)+"px";
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.droits.process.OeDROITSGestion"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;height:580px;">
		    <legend class="sigp2Legend">Gestion des droits de l'application</legend>
			<BR/>
			<table>
				<tr>
					<td>
						<div class="general">
							<div class="titre">
								<table>
									<tr>
										<td></td>
									</tr>
								</table>
							</div>
							<div id="entete" class="entete">
								<table>
									<tr>
										<%if (process.getListeGroupe()!=null && process.getListeGroupe().size()>0){%>
											<%for (int i = 0;i<process.getListeGroupe().size();i++){
												Groupe g = (Groupe)process.getListeGroupe().get(i); %>
												<td>
													<div>
														<%= g.getLibGroupe() %>
													</div>
													<div>
														<INPUT type="image" src="images/modifier.gif" height="16px" width="16px" name="<%=process.getNOM_PB_MODIFIER_GROUPE(i)%>">
														<INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_GROUPE(i)%>">
													</div>
												</td>
											<%} %>
										<%} %>
									</tr>
								</table>
							</div>
							<%if (process.getListeElement()!=null){%>
								<div id="first_col" class="first_col">
									<table>
										<%for (int i = 0;i<process.getListeElement().size();i++){
											Element elt = (Element)process.getListeElement().get(i);%>
											<tr>
												<td><%= elt.getLibElement() %></td>
											</tr>
										<%} %>
									</table>
								</div>
								<div class="the_table" onscroll="doOnScroll(this);">
									<table>
										<%for (int i = 0;i<process.getListeElement().size();i++){%>
											<tr>
												<%for (int j = 0;j<process.getListeGroupe().size();j++){%>
													<td>
														<SELECT <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_LB_TYPE_DROIT(i,j) %>" class="sigp2-liste">
															<%=process.forComboHTML(process.getVAL_LB_TYPE_DROIT(i,j), process.getVAL_LB_TYPE_DROIT_SELECT(i,j)) %>
														</SELECT>
													</td>
												<%} %>
											</tr>
										<%} %>
									</table>
								</div>
							<%} %>
						</div>
					</td>
				</tr>
			</table>
			
			<div style="margin-left:960px;">
				<INPUT  <%=MairieUtils.getDisabled(request, process.getNomEcran()) %> type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_GROUPE()%>">
			</div>
			<%
			UserAppli aUser= (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);
			if (aUser.getUserName().equals("chata73") || aUser.getUserName().equals("nicno85") 
					|| aUser.getUserName().equals("rebjo84") || aUser.getUserName().equals("bodth91")) {%>
			<BR/>
			<div style="margin-top: 485px;">
				<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_ELEMENT()%>">
			</div>				
			<%}%>		
		</FIELDSET>
		<BR/>
		<% if (!process.getVAL_ST_ACTION().equals("")) {%>
		    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			    <legend class="sigp2Legend"><%=process.getVAL_ST_ACTION() %></legend>
				<% if (process.ACTION_CREATION_ELEMENT.equals(process.getVAL_ST_ACTION())) {%>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_NOM_ELEMENT() %>" size="120"
						type="text" value="<%= process.getVAL_EF_NOM_ELEMENT() %>">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_AJOUT()%>"></span>
				<%}else if (process.ACTION_CREATION_GROUPE.equals(process.getVAL_ST_ACTION())) {%>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_NOM_GROUPE() %>" size="120"
						type="text" value="<%= process.getVAL_EF_NOM_GROUPE() %>">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_AJOUT()%>"></span>
				<%}else if (process.ACTION_MODIFICATION_GROUPE.equals(process.getVAL_ST_ACTION())) {%>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_NOM_GROUPE() %>" size="120"
						type="text" value="<%= process.getVAL_EF_NOM_GROUPE() %>">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_MODIFICATION_GRPE()%>"></span>
				<%}else if (process.ACTION_SUPPRESSION_GROUPE.equals(process.getVAL_ST_ACTION())) {%>
					<FONT color='red'>Veuillez valider votre choix.</FONT>
		    		<BR/><BR/>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_NOM_GROUPE() %>" size="120"
						type="text" value="<%= process.getVAL_EF_NOM_GROUPE() %>" disabled="disabled">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_SUPPRESSION_GRPE()%>"></span>
				<%} %>
			</FIELDSET>
			<BR/>
		<%} %>
		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>
	</FORM>
</BODY>
</HTML>