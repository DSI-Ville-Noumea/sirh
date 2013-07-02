<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des fiches de poste</TITLE>
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEFichePoste" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Entités géographiques</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_ENTITE_GEO() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_ENTITE_GEO(), process.getVAL_LB_ENTITE_GEO_SELECT()) %>
				</SELECT>
            	</span>
				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ENTITE_GEO()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ENTITE_GEO()%>">
            		<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_ENTITE_GEO()%>">
            	</div>            	
            	<% if (process.getVAL_ST_ACTION_ENTITE_GEO()!= null && !process.getVAL_ST_ACTION_ENTITE_GEO().equals("")) {%>
            	<br>	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_ENTITE_GEO())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="60" name="<%= process.getNOM_EF_ENTITE_GEO() %>" size="35" type="text" value="<%= process.getVAL_EF_ENTITE_GEO() %>" style="margin-right:10px;margin-bottom:10px">
					<br />						
					<label class="sigp2" Style="width:50px">Ecole:</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ENTITE_GEO_ECOLE() %>">
							<%=process.forComboHTML(process.getVAL_LB_ENTITE_GEO_ECOLE(), process.getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) %>
					</SELECT>				
					<br />				
					<br />				
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ENTITE_GEO())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ENTITE_GEO()%>"></span>
					<% } else{ %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ENTITE_GEO()%>"></span>
					<%} %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="20" readonly="readonly" name="<%= process.getNOM_EF_ENTITE_GEO() %>" size="35" type="text" value="<%= process.getVAL_EF_ENTITE_GEO() %>" style="margin-right:10px;margin-bottom:10px">
					<br />						
					<label class="sigp2" Style="width:50px">Ecole:</label>
					<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ENTITE_GEO_ECOLE() %>">
							<%=process.forComboHTML(process.getVAL_LB_ENTITE_GEO_ECOLE(), process.getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) %>
					</SELECT>				
					<br />			
					<br />	
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ENTITE_GEO()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ENTITE_GEO()%>"></span>
				</div>
				<% } %>
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Titres de poste</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_TITRE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TITRE(), process.getVAL_LB_TITRE_SELECT()) %>
				</SELECT>
            	</span>
				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TITRE()%>">
        	    	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TITRE()%>">
        	    </div>            	
            <% if (process.getVAL_ST_ACTION_TITRE()!= null && !process.getVAL_ST_ACTION_TITRE().equals("")) {%>
            	<br>
	            <% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TITRE())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_TITRE() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TITRE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TITRE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_TITRE() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TITRE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE()%>"></span>
				</div>
				<% } %>			
			</FIELDSET>			
			</div>		
				
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Types d'avantage en nature</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_TYPE_AVANTAGE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_AVANTAGE(), process.getVAL_LB_TYPE_AVANTAGE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_AVANTAGE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_AVANTAGE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_TYPE_AVANTAGE()!= null && !process.getVAL_ST_ACTION_TYPE_AVANTAGE().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_AVANTAGE())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_TYPE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_AVANTAGE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_AVANTAGE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_AVANTAGE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_TYPE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_AVANTAGE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_AVANTAGE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_AVANTAGE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Natures d'avantage en nature</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_NATURE_AVANTAGE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_NATURE_AVANTAGE(), process.getVAL_LB_NATURE_AVANTAGE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_NATURE_AVANTAGE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_NATURE_AVANTAGE()%>">
    	        </div>
            	
            <% if (process.getVAL_ST_ACTION_NATURE_AVANTAGE()!= null && !process.getVAL_ST_ACTION_NATURE_AVANTAGE().equals("")) {%>
            	<br>
	            <% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_NATURE_AVANTAGE())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NATURE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_NATURE_AVANTAGE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_NATURE_AVANTAGE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_NATURE_AVANTAGE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_NATURE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_NATURE_AVANTAGE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_NATURE_AVANTAGE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NATURE_AVANTAGE()%>"></span>
				</div>
				<% } %>
			
			</FIELDSET>
			
			</div>
			
			
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Types de délégation</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_TYPE_DELEGATION() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_DELEGATION(), process.getVAL_LB_TYPE_DELEGATION_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_DELEGATION()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_DELEGATION()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_TYPE_DELEGATION()!= null && !process.getVAL_ST_ACTION_TYPE_DELEGATION().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_DELEGATION())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_TYPE_DELEGATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_DELEGATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_DELEGATION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_DELEGATION()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="30" readonly="readonly" name="<%= process.getNOM_EF_TYPE_DELEGATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_DELEGATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_DELEGATION()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_DELEGATION()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Types de régime</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_TYPE_REGIME() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_REGIME(), process.getVAL_LB_TYPE_REGIME_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_REGIME()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_REGIME()%>">
    	        </div>
            	
            <% if (process.getVAL_ST_ACTION_TYPE_REGIME()!= null && !process.getVAL_ST_ACTION_TYPE_REGIME().equals("")) {%>
            	<br>
	            <% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_REGIME())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="20" name="<%= process.getNOM_EF_TYPE_REGIME() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_REGIME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_REGIME())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_REGIME()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="20" readonly="readonly" name="<%= process.getNOM_EF_TYPE_REGIME() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_REGIME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_REGIME()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_REGIME()%>"></span>
				</div>
				<% } %>
			
			</FIELDSET>
			
			</div>
			
			
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">NFA</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_NFA() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_NFA(), process.getVAL_LB_NFA_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_NFA()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_NFA()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_NFA()!= null && !process.getVAL_ST_ACTION_NFA().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_NFA())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Code service:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="4" name="<%= process.getNOM_EF_NFA_CODE_SERVICE() %>" size="4" type="text" value="<%= process.getVAL_EF_NFA_CODE_SERVICE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">NFA:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_NFA() %>" size="5" type="text" value="<%= process.getVAL_EF_NFA() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_NFA())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_NFA()%>"></span>
					<% } %>
				<%} else {%>
					
					<label class="sigp2Mandatory" Style="width:50px">Code service:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="4" readonly="readonly" name="<%= process.getNOM_EF_NFA_CODE_SERVICE() %>" size="4" type="text" value="<%= process.getVAL_EF_NFA_CODE_SERVICE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<label class="sigp2Mandatory" Style="width:50px">NFA:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" readonly="readonly" name="<%= process.getNOM_EF_NFA() %>" size="5" type="text" value="<%= process.getVAL_EF_NFA() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_NFA()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NFA()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Ecoles</legend>
				<span class="sigp2-titre" >
				<SELECT name="<%= process.getNOM_LB_ECOLE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_ECOLE(), process.getVAL_LB_ECOLE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ECOLE()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ECOLE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_ECOLE()%>">
    	        </div>
            	
            <% if (process.getVAL_ST_ACTION_ECOLE()!= null && !process.getVAL_ST_ACTION_ECOLE().equals("")) {%>
            	<br>
            	<%if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_ECOLE())){ %>
            		<label class="sigp2Mandatory" Style="width:50px">Code ecole:</label>
					<INPUT  tabindex="" class="sigp2-saisiemajuscule" maxlength="2" disabled="disabled" name="<%= process.getNOM_EF_ECOLE_CODE_ECOLE() %>" size="2" type="text" value="<%= process.getVAL_EF_ECOLE_CODE_ECOLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="40" name="<%= process.getNOM_EF_ECOLE() %>" size="40" type="text" value="<%= process.getVAL_EF_ECOLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ECOLE()%>"></span>
			   
	            <%} else if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ECOLE())) { %>
					
					<label class="sigp2Mandatory" Style="width:50px">Code ecole:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_ECOLE_CODE_ECOLE() %>" size="2" type="text" value="<%= process.getVAL_EF_ECOLE_CODE_ECOLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="40" name="<%= process.getNOM_EF_ECOLE() %>" size="40" type="text" value="<%= process.getVAL_EF_ECOLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ECOLE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ECOLE()%>"></span>
					<% } %>
				<%} else {%>
					
					<label class="sigp2Mandatory" Style="width:50px">Code ecole:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" readonly="readonly" name="<%= process.getNOM_EF_ECOLE_CODE_ECOLE() %>" size="2" type="text" value="<%= process.getVAL_EF_ECOLE_CODE_ECOLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="40" readonly="readonly" name="<%= process.getNOM_EF_ECOLE() %>" size="40" type="text" value="<%= process.getVAL_EF_ECOLE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ECOLE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ECOLE()%>"></span>
				</div>
				<% } %>
			
			</FIELDSET>
			</div>
			
		</FORM>
	</BODY>
</HTML>