<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.OePARAMETRAGECarriere" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Liste des bases horaires</legend>
				<span class="sigp2-saisie" style="position:relative;width:50px;">Code</span>
				<span class="sigp2-saisie" style="position:relative;">Libellé</span>
				<SELECT name="<%= process.getNOM_LB_SPBASE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_SPBASE(), process.getVAL_LB_SPBASE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_SPBASE()%>">
    	        	<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_SPBASE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_SPBASE()!= null && !process.getVAL_ST_ACTION_SPBASE().equals("")) {%>
            	<br>
            	<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_SPBASE())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_LIB_SPBASE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_SPBASE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_SPBASE() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_SPBASE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />	
					<div Style="width:100%" align="center">				
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_SPBASE()%>"></span>					
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_LIB_SPBASE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_SPBASE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" disabled="disabled" name="<%= process.getNOM_EF_CODE_SPBASE() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_SPBASE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_SPBASE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_SPBASE()%>"></span>
				</div>
				<%} %>
				
			</FIELDSET>		
		</FORM>
	</BODY>
</HTML>