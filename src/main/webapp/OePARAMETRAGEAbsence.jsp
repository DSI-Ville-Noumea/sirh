<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des absences</TITLE>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsence" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<table width="1030px;">
				<tr>
					<td width="500px;">
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Motifs de refus des absences</legend>
							<span class="sigp2-saisie">Libellé</span>
							<span class="sigp2-saisie" style="margin-left: 250px;">Type Absence</span>
							<SELECT name="<%= process.getNOM_LB_MOTIF() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MOTIF()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_MOTIF()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_MOTIF()!= null && !process.getVAL_ST_ACTION_MOTIF().equals("")) {%>
			            		<br>
								<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
								<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>">
								<br /><br />
							    <div Style="width:100%" align="center">
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>"></span>
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF()%>"></span>
								</div>
							<% } %>
						</FIELDSET>	
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left; ">
					    	<legend class="sigp2Legend">Motifs d'alimentation manuelle des compteurs</legend>
							<span class="sigp2-saisie" >Libellé</span>
							<span class="sigp2-saisie" style="margin-left: 250px;">Famille d'absence</span>
							<SELECT name="<%= process.getNOM_LB_MOTIF_COMPTEUR() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_MOTIF_COMPTEUR(), process.getVAL_LB_MOTIF_COMPTEUR_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MOTIF_COMPTEUR()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_MOTIF_COMPTEUR()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_MOTIF_COMPTEUR()!= null && !process.getVAL_ST_ACTION_MOTIF_COMPTEUR().equals("")) {%>
			            		<br>
								<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
								<INPUT class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF_COMPTEUR() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF_COMPTEUR() %>">
								<br />
								<span class="sigp2Mandatory" style="width:110px">Famille d'absence : </span>
								<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE_COMPTEUR() %>" style="width:300px;">
									<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE_COMPTEUR(), process.getVAL_LB_TYPE_ABSENCE_COMPTEUR_SELECT()) %>
								</SELECT>					
								<br /><br />
							    <div Style="width:100%" align="center">
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF_COMPTEUR()%>"></span>
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF_COMPTEUR()%>"></span>
								</div>
							<% } %>							
						</FIELDSET>	
					</td>
				</tr>
			</table>
		</FORM>
	</BODY>
</HTML>