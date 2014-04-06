<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEElection" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<table width="1030px;">
				<tr>
					<td width="1000px;">
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Gestion des organisations syndicales</legend>
							<span class="sigp2-saisie">Libellé</span>
							<span class="sigp2-saisie" style="margin-left: 670px;">Sigle</span>
							<span class="sigp2-saisie" style="margin-left: 120px;">Actif</span>
							<SELECT name="<%= process.getNOM_LB_ORGANISATION() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_ORGANISATION(), process.getVAL_LB_ORGANISATION_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ORGANISATION()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ORGANISATION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_ORGANISATION()!= null && process.getVAL_ST_ACTION_ORGANISATION().equals(process.ACTION_CREATION)) {%>
			            		<br>
								<label class="sigp2Mandatory" style="width:50px">Libellé :</label>
								<INPUT class="sigp2-saisie" maxlength="250" name="<%= process.getNOM_EF_LIB_ORGANISATION() %>" size="100" type="text" value="<%= process.getVAL_EF_LIB_ORGANISATION() %>">
								<br /><BR/>
								<span class="sigp2Mandatory" style="width:50px">Sigle : </span>
								<INPUT class="sigp2-saisie" maxlength="15" name="<%= process.getNOM_EF_SIGLE_ORGANISATION() %>" size="35" type="text" value="<%= process.getVAL_EF_SIGLE_ORGANISATION() %>">
								<br /><BR/>
								<span class="sigp2Mandatory" style="width:50px">Actif : </span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_ORGANISATION_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_ORGANISATION_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>					
								<br /><br />
							    <div Style="width:100%" align="center">
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_ORGANISATION()%>"></span>
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ORGANISATION()%>"></span>
								</div>
							<% }else if(process.getVAL_ST_ACTION_ORGANISATION()!= null && process.getVAL_ST_ACTION_ORGANISATION().equals(process.ACTION_MODIFICATION)){ %>	
			            		<br>
								<label class="sigp2Mandatory" style="width:50px">Libellé:</label>
								<INPUT class="sigp2-saisie" disabled="disabled" maxlength="250" name="<%= process.getNOM_EF_LIB_ORGANISATION() %>" size="100" type="text" value="<%= process.getVAL_EF_LIB_ORGANISATION() %>">
								<br /><BR/>
								<span class="sigp2Mandatory" style="width:50px">Sigle : </span>
								<INPUT class="sigp2-saisie" disabled="disabled" maxlength="15" name="<%= process.getNOM_EF_SIGLE_ORGANISATION() %>" size="35" type="text" value="<%= process.getVAL_EF_SIGLE_ORGANISATION() %>">
								<br /><BR/>
								<span class="sigp2Mandatory" style="width:50px">Actif : </span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_ORGANISATION_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_ORGANISATION_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>					
								<br /><br />
							    <div Style="width:100%" align="center">
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_ORGANISATION()%>"></span>
									<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ORGANISATION()%>"></span>
								</div>
							<%} %>						
						</FIELDSET>	
					</td>
				</tr>
			</table>
		</FORM>
	</BODY>
</HTML>