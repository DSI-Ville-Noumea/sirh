<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des rubriques</TITLE>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGERubrique" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px">
				<tr>
					<td  width="500px;">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Motifs des carrières</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Libellé</span>
							<SELECT name="<%= process.getNOM_LB_RUBRIQUE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE(), process.getVAL_LB_RUBRIQUE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_RUBRIQUE()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_RUBRIQUE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_RUBRIQUE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_RUBRIQUE()!= null && !process.getVAL_ST_ACTION_RUBRIQUE().equals("")) {%>
			            	<br>
				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_RUBRIQUE())) { %>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Numéro:</label>
									</td>
									<td>
										<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_RUBRIQUE())) { %>
										<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_LIB_RUBRIQUE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_RUBRIQUE() %>">
										<%} %>
										<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_RUBRIQUE())) { %>
										<INPUT class="sigp2-saisie" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_LIB_RUBRIQUE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_RUBRIQUE() %>">
										<%} %>
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Paramétrage:</label>
									</td>
									<td>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_CODE_CHARGE(),process.getVAL_CK_CODE_CHARGE())%> ><span class="sigp2-saisie">Code charge</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_CREANCIER(),process.getVAL_CK_CREANCIER())%> ><span class="sigp2-saisie">Creancier</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_MATRICULE_CHARGE(),process.getVAL_CK_MATRICULE_CHARGE())%> ><span class="sigp2-saisie">Matricule charge</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_MONTANT(),process.getVAL_CK_MONTANT())%> ><span class="sigp2-saisie">Montant</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_DONNEES_MUTU(),process.getVAL_CK_DONNEES_MUTU())%> ><span class="sigp2-saisie">Donées mutu</span>
									</td>
								</tr>
								<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_RUBRIQUE())) { %>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_RUBRIQUE()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_RUBRIQUE()%>">
									</td>
								</tr>
								<%} %>
								<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_RUBRIQUE())) { %>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_RUBRIQUE()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_RUBRIQUE()%>">
									</td>
								</tr>
								<%} %>
							</table>
							<%} else {%>
							<table>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Numéro::</label>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_LIB_RUBRIQUE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_RUBRIQUE() %>">
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_RUBRIQUE()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_RUBRIQUE()%>">
									</td>
								</tr>
							</table>
						   <%}%>
							<% } %>
						</FIELDSET>	
					</td>
					
					<td>&nbsp;	
					</td>
				</tr>
			</table>
		</FORM>
	</BODY>
</HTML>