<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.carriere.GradeGenerique"%>
<%@page import="nc.mairie.spring.domain.metier.parametrage.EmployeurCap"%>
<%@page import="nc.mairie.spring.domain.metier.parametrage.Representant"%>
<%@page import="nc.mairie.spring.domain.metier.parametrage.Employeur"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des avancements</TITLE>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAvancement" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px">
				<tr>
					<td width="500px;">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Motif des avancements</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Libellé</span>
							<span class="sigp2-saisie" style="margin-left: 245px;">Code</span>
							<SELECT name="<%= process.getNOM_LB_MOTIF() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MOTIF()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_MOTIF()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_MOTIF()!= null && !process.getVAL_ST_ACTION_MOTIF().equals("")) {%>
			            	<br>
				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_MOTIF())) { %>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Libellé:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Code:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_MOTIF() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_MOTIF() %>">
									</td>
								</tr>
								<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MOTIF())) { %>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF()%>">
									</td>
								</tr>
								<%} %>
							</table>
							<%} else {%>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Libellé:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Code:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="5" disabled="disabled" name="<%= process.getNOM_EF_CODE_MOTIF() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_MOTIF() %>">
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF()%>">
									</td>
								</tr>
							</table>
						   <%}%>
							<% } %>
						</FIELDSET>	
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Représentants du personnel</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Nom</span>
							<span class="sigp2-saisie" style="margin-left: 110px;">Prénom</span>
							<span class="sigp2-saisie" style="margin-left: 105px;">Type</span>
							<SELECT name="<%= process.getNOM_LB_REPRESENTANT() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_REPRESENTANT(), process.getVAL_LB_REPRESENTANT_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_REPRESENTANT()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_REPRESENTANT()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_REPRESENTANT()!= null && !process.getVAL_ST_ACTION_REPRESENTANT().equals("")) {%>
			            	<br>
				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_REPRESENTANT())) { %>	
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Type:</label>
									</td>
									<td>
										<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_REPRESENTANT() %>">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_REPRESENTANT(), process.getVAL_LB_TYPE_REPRESENTANT_SELECT()) %>
										</SELECT>	
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Nom:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_REPRESENTANT() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Prénom:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_PRENOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_REPRESENTANT() %>">
									</td>
								</tr>
								<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_REPRESENTANT())) { %>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_REPRESENTANT()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_REPRESENTANT()%>">
									</td>
								</tr>
								<%} %>
							</table>
							<%} else {%>	
							<table>
								<tr>
									<td width="80px;">
										<label class="sigp2Mandatory">Type:</label>
									</td>
									<td>
										<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_REPRESENTANT() %>">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_REPRESENTANT(), process.getVAL_LB_TYPE_REPRESENTANT_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Nom:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_NOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_REPRESENTANT() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Prénom:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_PRENOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_REPRESENTANT() %>">
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_REPRESENTANT()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_REPRESENTANT()%>">
									</td>
								</tr>
							</table>	
						   <%}%>
							<% } %>
						</FIELDSET>	
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Employeurs</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Libellé</span>
							<span class="sigp2-saisie" style="margin-left: 315px;">Raison</span>
							<SELECT name="<%= process.getNOM_LB_EMPLOYEUR() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_EMPLOYEUR(), process.getVAL_LB_EMPLOYEUR_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_EMPLOYEUR()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_EMPLOYEUR()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_EMPLOYEUR()!= null && !process.getVAL_ST_ACTION_EMPLOYEUR().equals("")) {%>
			            	<br>
				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_EMPLOYEUR())) { %>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Libellé:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_EMPLOYEUR() %>" size="70" type="text" value="<%= process.getVAL_EF_EMPLOYEUR() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Titre:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TITRE_EMPLOYEUR() %>" size="70" type="text" value="<%= process.getVAL_EF_TITRE_EMPLOYEUR() %>">
									</td>
								</tr>
								<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_EMPLOYEUR())) { %>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_EMPLOYEUR()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_EMPLOYEUR()%>">
									</td>
								</tr>
								<%} %>
							</table>
							<%} else {%>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Libellé:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="100" disabled="disabled" name="<%= process.getNOM_EF_EMPLOYEUR() %>" size="70" type="text" value="<%= process.getVAL_EF_EMPLOYEUR() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Titre:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" disabled="disabled" name="<%= process.getNOM_EF_TITRE_EMPLOYEUR() %>" size="100" type="text" value="<%= process.getVAL_EF_TITRE_EMPLOYEUR() %>">
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_EMPLOYEUR()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_EMPLOYEUR()%>">
									</td>
								</tr>
							</table>
						   <%}%>
							<% } %>
						</FIELDSET>	
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Délibérations</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Code</span>
							<span class="sigp2-saisie" style="margin-left: 45px;">Type</span>
							<span class="sigp2-saisie" style="margin-left: 115px;">Libellé</span>
							<SELECT name="<%= process.getNOM_LB_DELIBERATION() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_DELIBERATION(), process.getVAL_LB_DELIBERATION_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DELIBERATION()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DELIBERATION()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_DELIBERATION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_DELIBERATION()!= null && !process.getVAL_ST_ACTION_DELIBERATION().equals("")) {%>
			            	<br>
				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_DELIBERATION())) { %>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Code :</label>
									</td>
									<td>
										<%if (process.getVAL_ST_ACTION_DELIBERATION().equals(process.ACTION_MODIFICATION)){ %>
										<INPUT class="sigp2-saisie" disabled="disabled" maxlength="10" name="<%= process.getNOM_EF_CODE_DELIBERATION() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_DELIBERATION() %>">
										<%}else{ %>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_CODE_DELIBERATION() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_DELIBERATION() %>">
										<%} %>
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Libellé :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_LIB_DELIBERATION() %>" size="80" type="text" value="<%= process.getVAL_EF_LIB_DELIBERATION() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Texte CAP :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TEXTE_CAP_DELIBERATION() %>" size="80" type="text" value="<%= process.getVAL_EF_TEXTE_CAP_DELIBERATION() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Type:</label>
									</td>
									<td>
										<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_DELIBERATION() %>">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_DELIBERATION(), process.getVAL_LB_TYPE_DELIBERATION_SELECT()) %>
										</SELECT>	
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_DELIBERATION())) { %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DELIBERATION()%>">
										<% }else{ %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_DELIBERATION()%>">
										<%} %>
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DELIBERATION()%>">
									</td>
								</tr>
							</table>
							<%} else {%>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Code:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" disabled="disabled" name="<%= process.getNOM_EF_CODE_DELIBERATION() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_DELIBERATION() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Libellé :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" disabled="disabled" name="<%= process.getNOM_EF_LIB_DELIBERATION() %>" size="80" type="text" value="<%= process.getVAL_EF_LIB_DELIBERATION() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Texte CAP :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" disabled="disabled" name="<%= process.getNOM_EF_TEXTE_CAP_DELIBERATION() %>" size="80" type="text" value="<%= process.getVAL_EF_TEXTE_CAP_DELIBERATION() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Type:</label>
									</td>
									<td>
										<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_DELIBERATION() %>">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_DELIBERATION(), process.getVAL_LB_TYPE_DELIBERATION_SELECT()) %>
										</SELECT>	
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DELIBERATION()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DELIBERATION()%>">
									</td>
								</tr>
							</table>
						   <%}%>
							<% } %>
						</FIELDSET>	
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">CAP</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Code</span>
							<span class="sigp2-saisie" style="margin-left: 55px;">Type</span>
							<span class="sigp2-saisie" style="margin-left: 55px;">Reférence</span>
							<span class="sigp2-saisie" style="margin-left: 55px;">VDN</span>
							<SELECT name="<%= process.getNOM_LB_CAP() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_CAP(), process.getVAL_LB_CAP_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CAP()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_CAP()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CAP()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_CAP()!= null && !process.getVAL_ST_ACTION_CAP().equals("")) {%>
			            	<br>
				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_CAP())) { %>
							<table>
								<tr>
									<td width="120px;">
										<label class="sigp2Mandatory">Code :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="10" name="<%= process.getNOM_EF_CODE_CAP() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_CAP() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Référence CAP :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="10" name="<%= process.getNOM_EF_REF_CAP() %>" size="10" type="text" value="<%= process.getVAL_EF_REF_CAP() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Description :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_DESCRIPTION_CAP() %>" size="80" type="text" value="<%= process.getVAL_EF_DESCRIPTION_CAP() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Type:</label>
									</td>
									<td>
										<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_CAP() %>">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_CAP(), process.getVAL_LB_TYPE_CAP_SELECT()) %>
										</SELECT>	
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> CAP VDN :</span>
									</td>
									<td>
										<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CAP_VDN(),process.getNOM_RB_CAP_VDN_O())%> ><span class="sigp2Mandatory">Oui</span>
										<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CAP_VDN(),process.getNOM_RB_CAP_VDN_N())%> ><span class="sigp2Mandatory">Non</span>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Corps : </span>
									</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>
									<div style="overflow: auto;height: 120px;width:95%;">
										<table class="sigp2NewTab" style="text-align:left;width:90%;">
											<%
											if (process.getListeCorps()!=null){
												for (int indiceCorps = 0;indiceCorps<process.getListeCorps().size();indiceCorps++){
													GradeGenerique gg = process.getListeCorps().get(indiceCorps);
											%>
													<tr>
														<td><INPUT type="checkbox" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_CORPS(indiceCorps),process.getVAL_CK_SELECT_LIGNE_CORPS(indiceCorps))%> ></td>
														<td><%=gg.getLibGradeGenerique()%></td>								
													</tr>						
											<%
												}
											}
											%>
										</table>
									</div>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Employeurs : </span>
									</td>
									<td>
										<INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_EMPLOYEUR_CAP()%>">
									    <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_EMP_CAP() %>" style="width:250px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_EMPLOYEUR_CAP() %>")'>
											<%=process.forComboHTML(process.getVAL_LB_EMP_CAP(), process.getVAL_LB_EMP_CAP_SELECT())%>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>
										<SELECT size="4" style="width:393px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_EMP_CAP_MULTI()%>" >
											<%=process.forComboHTML(process.getVAL_LB_EMP_CAP_MULTI(), process.getVAL_LB_EMP_CAP_MULTI_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Représentants : </span>
									</td>
									<td>
										<INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_REPRESENTANT_CAP()%>">
									    <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_REPRE_CAP() %>" style="width:250px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_REPRESENTANT_CAP() %>")'>
											<%=process.forComboHTML(process.getVAL_LB_REPRE_CAP(), process.getVAL_LB_REPRE_CAP_SELECT())%>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>
										<SELECT size="4" style="width:393px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_REPRE_CAP_MULTI()%>" >
											<%=process.forComboHTML(process.getVAL_LB_REPRE_CAP_MULTI(), process.getVAL_LB_REPRE_CAP_MULTI_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_CAP())) { %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CAP()%>">
										<% } %>
										<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_CAP())) { %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_CAP()%>">
										<% } %>
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CAP()%>">
									</td>
								</tr>								
							</table>
							<%} else {%>
							<table>
								<tr>
									<td width="120px;">
										<label class="sigp2Mandatory">Code:</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="10" disabled="disabled" name="<%= process.getNOM_EF_CODE_CAP() %>" size="10" type="text" value="<%= process.getVAL_EF_CODE_CAP() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Référence CAP :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="10" disabled="disabled" name="<%= process.getNOM_EF_REF_CAP() %>" size="10" type="text" value="<%= process.getVAL_EF_REF_CAP() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Description :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" disabled="disabled" maxlength="255" name="<%= process.getNOM_EF_DESCRIPTION_CAP() %>" size="80" type="text" value="<%= process.getVAL_EF_DESCRIPTION_CAP() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Type:</label>
									</td>
									<td>
										<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_CAP() %>">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_CAP(), process.getVAL_LB_TYPE_CAP_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2">CAP VDN : </span>
									</td>
									<td>
										<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_CAP_VDN() %></span> 
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Corps : </span>
									</td>
									<td>
										<table class="sigp2NewTab" style="text-align:left;width:90%;">
											<%
											if (process.getListeCorpsCap()!=null){
												for (int indiceCorps = 0;indiceCorps<process.getListeCorpsCap().size();indiceCorps++){
													GradeGenerique gg = process.getListeCorpsCap().get(indiceCorps);
											%>
													<tr>
														<td><%=gg.getLibGradeGenerique()%></td>								
													</tr>						
											<%
												}
											}
											%>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Employeurs : </span>
									</td>
									<td>
										<table class="sigp2NewTab" style="text-align:left;width:90%;">
											<%
											if (process.getListeEmployeurCap()!=null){
												for (int indiceEmp = 0;indiceEmp<process.getListeEmployeurCap().size();indiceEmp++){
													Employeur emp = process.getListeEmployeurCap().get(indiceEmp);
											%>
													<tr>
														<td><%=emp.getLibEmployeur()%></td>								
													</tr>						
											<%
												}
											}
											%>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Représentants : </span>
									</td>
									<td>
										<table class="sigp2NewTab" style="text-align:left;width:90%;">
											<%
											if (process.getListeRepresentantCap()!=null){
												for (int indiceRep = 0;indiceRep<process.getListeRepresentantCap().size();indiceRep++){
													Representant rep = process.getListeRepresentantCap().get(indiceRep);
											%>
													<tr>
														<td><%=rep.getNomRepresentant()+ " " + rep.getPrenomRepresentant() %></td>								
													</tr>						
											<%
												}
											}
											%>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CAP()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CAP()%>">
									</td>
								</tr>
							</table>
						   <%}%>
							<% } %>
						</FIELDSET>	
					</td>
				</tr>
			</table>	
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_REPRESENTANT_CAP()%>">	
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_REPRESENTANT_CAP()%>">	
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_EMPLOYEUR_CAP()%>">	
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_EMPLOYEUR_CAP()%>">	
		</FORM>
	</BODY>
</HTML>