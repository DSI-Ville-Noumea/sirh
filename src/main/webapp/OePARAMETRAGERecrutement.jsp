<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des recrutements</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.OePARAMETRAGERecrutement" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Motif de recrutement</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_MOTIF() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MOTIF()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_MOTIF()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_MOTIF()!= null && !process.getVAL_ST_ACTION_MOTIF().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_MOTIF())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_MOTIF() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MOTIF())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_MOTIF() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Motif de non recrutement</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_NON_REC() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_NON_REC(), process.getVAL_LB_NON_REC_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_NON_REC()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_NON_REC()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_NON_REC()!= null && !process.getVAL_ST_ACTION_NON_REC().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_NON_REC())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NON_REC() %>" size="35" type="text" value="<%= process.getVAL_EF_NON_REC() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_NON_REC())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_NON_REC()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_NON_REC() %>" size="35" type="text" value="<%= process.getVAL_EF_NON_REC() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_NON_REC()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NON_REC()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>	
		</FORM>
	</BODY>
</HTML>