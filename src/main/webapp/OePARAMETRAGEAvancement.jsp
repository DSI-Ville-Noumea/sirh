<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
	<jsp:useBean class="nc.mairie.gestionagent.process.OePARAMETRAGEAvancement" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Motif des avancements</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<span class="sigp2-saisie" style="position:relative;width:290px;">Libellé</span>
				<span class="sigp2-saisie" style="position:relative;">Code</span>
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
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_MOTIF() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_MOTIF() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MOTIF())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_LIB_MOTIF() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_MOTIF() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" disabled="disabled" name="<%= process.getNOM_EF_CODE_MOTIF() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_MOTIF() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MOTIF()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MOTIF()%>"></span>
				</div>
				<% } %>
			</FIELDSET>		
			
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Représentants du personnel</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<span class="sigp2-saisie" style="position:relative;width:145px;">Nom</span>
				<span class="sigp2-saisie" style="position:relative;width:145px;">Prénom</span>
				<span class="sigp2-saisie" style="position:relative;">Type</span>
				<SELECT name="<%= process.getNOM_LB_REPRESENTANT() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_REPRESENTANT(), process.getVAL_LB_REPRESENTANT_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_REPRESENTANT()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_REPRESENTANT()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_REPRESENTANT()!= null && !process.getVAL_ST_ACTION_REPRESENTANT().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_REPRESENTANT())) { %>						
					<label class="sigp2Mandatory" Style="width:50px">Type:</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_REPRESENTANT() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_REPRESENTANT(), process.getVAL_LB_TYPE_REPRESENTANT_SELECT()) %>
					</SELECT>				
					<br />	<br />	
					<label class="sigp2Mandatory" Style="width:50px">Nom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_REPRESENTANT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Prénom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_PRENOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_REPRESENTANT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_REPRESENTANT())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_REPRESENTANT()%>"></span>
					<% } %>
				<%} else {%>						
					<label class="sigp2Mandatory" Style="width:50px">Type:</label>
					<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_REPRESENTANT() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_REPRESENTANT(), process.getVAL_LB_TYPE_REPRESENTANT_SELECT()) %>
					</SELECT>				
					<br />		<br />	
					<label class="sigp2Mandatory" Style="width:50px">Nom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_NOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_REPRESENTANT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Prénom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" disabled="disabled" name="<%= process.getNOM_EF_PRENOM_REPRESENTANT() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_REPRESENTANT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_REPRESENTANT()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_REPRESENTANT()%>"></span>
				</div>
				<% } %>
			</FIELDSET>		
			</div>	
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:1030px; float:left;">
		    	<legend class="sigp2Legend">Employeurs</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<span class="sigp2-saisie" style="position:relative;width:360px;">Libellé</span>
				<span class="sigp2-saisie" style="position:relative;">Raison</span>
				<SELECT name="<%= process.getNOM_LB_EMPLOYEUR() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_EMPLOYEUR(), process.getVAL_LB_EMPLOYEUR_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_EMPLOYEUR()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_EMPLOYEUR()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_EMPLOYEUR()!= null && !process.getVAL_ST_ACTION_EMPLOYEUR().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_EMPLOYEUR())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_EMPLOYEUR() %>" size="70" type="text" value="<%= process.getVAL_EF_EMPLOYEUR() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Titre:</label>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TITRE_EMPLOYEUR() %>" size="70" type="text" value="<%= process.getVAL_EF_TITRE_EMPLOYEUR() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_EMPLOYEUR())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_EMPLOYEUR()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="100" disabled="disabled" name="<%= process.getNOM_EF_EMPLOYEUR() %>" size="70" type="text" value="<%= process.getVAL_EF_EMPLOYEUR() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Titre:</label>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="255" disabled="disabled" name="<%= process.getNOM_EF_TITRE_EMPLOYEUR() %>" size="100" type="text" value="<%= process.getVAL_EF_TITRE_EMPLOYEUR() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_EMPLOYEUR()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_EMPLOYEUR()%>"></span>
				</div>
				<% } %>
			</FIELDSET>			
			</div>
		</FORM>
	</BODY>
</HTML>