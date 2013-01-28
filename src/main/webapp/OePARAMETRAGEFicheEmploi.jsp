<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres dela fiche emploi</TITLE>
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
	<jsp:useBean class="nc.mairie.gestionagent.process.OePARAMETRAGEFicheEmploi" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Domaines d'emploi</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_DOMAINE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DOMAINE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DOMAINE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_DOMAINE()!= null && !process.getVAL_ST_ACTION_DOMAINE().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_DOMAINE())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_CODE_DOMAINE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_DOMAINE() %>" style="margin-right:10px; margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_LIB_DOMAINE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_DOMAINE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_DOMAINE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DOMAINE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" readonly="readonly" name="<%= process.getNOM_EF_CODE_DOMAINE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_DOMAINE() %>" style="margin-right:10px; margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_LIB_DOMAINE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_DOMAINE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DOMAINE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DOMAINE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Famille d'emploi</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_FAMILLE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_FAMILLE(), process.getVAL_LB_FAMILLE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_FAMILLE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_FAMILLE()%>">
    	        </div>
            	
            <% if (process.getVAL_ST_ACTION_FAMILLE()!= null && !process.getVAL_ST_ACTION_FAMILLE().equals("")) {%>
            	<br>
	            <% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_FAMILLE())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="3" name="<%= process.getNOM_EF_CODE_FAMILLE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_FAMILLE() %>" style="margin-right:10px; margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_FAMILLE() %>" size="35" type="text" value="<%= process.getVAL_EF_FAMILLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_FAMILLE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_FAMILLE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="3" readonly="readonly" name="<%= process.getNOM_EF_CODE_FAMILLE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_FAMILLE() %>" style="margin-right:10px; margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_FAMILLE() %>" size="35" type="text" value="<%= process.getVAL_EF_FAMILLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_FAMILLE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_FAMILLE()%>"></span>
				</div>
				<% } %>
			
			</FIELDSET>
			
			</div>
			
			<div style="width:100%">
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Diplômes génériques</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_DIPLOME() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_DIPLOME(), process.getVAL_LB_DIPLOME_SELECT()) %>
				</SELECT>
            	</span>
				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DIPLOME()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME()%>">
    	        </div>            	
            	<% if (process.getVAL_EF_ACTION_DIPLOME()!= null && !process.getVAL_EF_ACTION_DIPLOME().equals("")) {%>
	            	<br>	            	
	            	<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_EF_ACTION_DIPLOME())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="20" name="<%= process.getNOM_EF_DIPLOME() %>" size="400px" type="text" value="<%= process.getVAL_EF_DIPLOME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_EF_ACTION_DIPLOME())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT readonly="readonly" tabindex="" class="sigp2-saisiemajuscule" maxlength="20" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />					
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DIPLOME()%>"></span>
				<% } %>
			
			</FIELDSET>
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Catégories</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_CATEGORIE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_CATEGORIE(), process.getVAL_LB_CATEGORIE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CATEGORIE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CATEGORIE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_CATEGORIE()!= null && !process.getVAL_ST_ACTION_CATEGORIE().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_CATEGORIE())) { %>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_LIB_CATEGORIE() %>" size="2" type="text" value="<%= process.getVAL_EF_LIB_CATEGORIE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_CATEGORIE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CATEGORIE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" readonly="readonly" name="<%= process.getNOM_EF_LIB_CATEGORIE() %>" size="2" type="text" value="<%= process.getVAL_EF_LIB_CATEGORIE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CATEGORIE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CATEGORIE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>
			
			<div style="width:100%">		
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">			
		    	<legend class="sigp2Legend">Code rome</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_CODE_ROME() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_CODE_ROME(), process.getVAL_LB_CODE_ROME_SELECT()) %>
				</SELECT>
            	</span>            	
				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CODE_ROME()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CODE_ROME()%>">
    	        </div>
            	<% if (process.getVAL_EF_ACTION_CODE_ROME()!= null && !process.getVAL_EF_ACTION_CODE_ROME().equals("")) {%>
            	<br>
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_EF_ACTION_CODE_ROME())) { %>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CODE_ROME() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />		
					<label class="sigp2Mandatory" Style="width:70px">Decsription:</label>
					<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_DESC_CODE_ROME() %>" size="70" type="text" value="<%= process.getVAL_EF_DESC_CODE_ROME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />					
					<div Style="width:100%" align="center">					
					<% if (process.ACTION_CREATION.equals(process.getVAL_EF_ACTION_CODE_ROME())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CODE_ROME()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:70px">Libellé:</label>
					<INPUT readonly="readonly" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CODE_ROME() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />	
					<label class="sigp2Mandatory" Style="width:70px">Decsription:</label>
					<INPUT readonly="readonly" class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_DESC_CODE_ROME() %>" size="70" type="text" value="<%= process.getVAL_EF_DESC_CODE_ROME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />					
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CODE_ROME()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CODE_ROME()%>"></span>
				</div>
				<% } %>			
			</FIELDSET>
			</div>
		</FORM>
	</BODY>
</HTML>