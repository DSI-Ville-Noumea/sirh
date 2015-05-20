<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des carrières</TITLE>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGECarriere" id="process" scope="session"></jsp:useBean>
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
										<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>">
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
										<INPUT class="sigp2-saisie" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>">
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
					
					<td>&nbsp;	
					</td>
				</tr>
			</table>
		</FORM>
	</BODY>
</HTML>