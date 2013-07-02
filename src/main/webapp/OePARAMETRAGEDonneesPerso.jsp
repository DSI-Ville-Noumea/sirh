<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des données personnelles</TITLE>
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEDonneesPerso" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Titre de diplômes</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_DIPLOME() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_DIPLOME(), process.getVAL_LB_DIPLOME_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DIPLOME()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_DIPLOME()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_DIPLOME()!= null && !process.getVAL_ST_ACTION_DIPLOME().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_DIPLOME())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Niveau d'études:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="10" name="<%= process.getNOM_EF_NIV_ETUDE() %>" size="10" type="text" value="<%= process.getVAL_EF_NIV_ETUDE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_DIPLOME())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>"></span>
					<% } else{ %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>"></span>
					<%} %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Niveau d'études:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="10" readonly="readonly" name="<%= process.getNOM_EF_NIV_ETUDE() %>" size="10" type="text" value="<%= process.getVAL_EF_NIV_ETUDE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DIPLOME()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Spécialité du diplôme</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_SPECIALITE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_SPECIALITE(), process.getVAL_LB_SPECIALITE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_SPECIALITE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_SPECIALITE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_SPECIALITE()!= null && !process.getVAL_ST_ACTION_SPECIALITE().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_SPECIALITE())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_SPECIALITE() %>" size="35" type="text" value="<%= process.getVAL_EF_SPECIALITE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_SPECIALITE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_SPECIALITE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_SPECIALITE() %>" size="35" type="text" value="<%= process.getVAL_EF_SPECIALITE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_SPECIALITE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_SPECIALITE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Autre administration</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_ADMIN() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_ADMIN(), process.getVAL_LB_ADMIN_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ADMIN()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_ADMIN()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_ADMIN()!= null && !process.getVAL_ST_ACTION_ADMIN().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_ADMIN())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_ADMIN() %>" size="35" type="text" value="<%= process.getVAL_EF_ADMIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ADMIN())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ADMIN()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_ADMIN() %>" size="35" type="text" value="<%= process.getVAL_EF_ADMIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ADMIN()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ADMIN()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Types de documents</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_TYPE_DOCUMENT() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_DOCUMENT(), process.getVAL_LB_TYPE_DOCUMENT_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_DOCUMENT()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_DOCUMENT()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_TYPE_DOCUMENT()!= null && !process.getVAL_ST_ACTION_TYPE_DOCUMENT().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_DOCUMENT())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_TYPE_DOCUMENT() %>" size="30" type="text" value="<%= process.getVAL_EF_TYPE_DOCUMENT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_TYPE_DOCUMENT() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_TYPE_DOCUMENT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_DOCUMENT())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_DOCUMENT()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="30" readonly="readonly" name="<%= process.getNOM_EF_TYPE_DOCUMENT() %>" size="30" type="text" value="<%= process.getVAL_EF_TYPE_DOCUMENT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_TYPE_DOCUMENT() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_TYPE_DOCUMENT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_DOCUMENT()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_DOCUMENT()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>	
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Titre de formations</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_TITRE_FORMATION() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TITRE_FORMATION(), process.getVAL_LB_TITRE_FORMATION_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TITRE_FORMATION()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_TITRE_FORMATION()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TITRE_FORMATION()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_TITRE_FORMATION()!= null && !process.getVAL_ST_ACTION_TITRE_FORMATION().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TITRE_FORMATION())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_TITRE_FORMATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_FORMATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TITRE_FORMATION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TITRE_FORMATION()%>"></span>
					<% } else{ %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_TITRE_FORMATION()%>"></span>
					<%} %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_TITRE_FORMATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_FORMATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TITRE_FORMATION()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE_FORMATION()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Centres de formations</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_CENTRE_FORMATION() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_CENTRE_FORMATION(), process.getVAL_LB_CENTRE_FORMATION_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CENTRE_FORMATION()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_CENTRE_FORMATION()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CENTRE_FORMATION()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_CENTRE_FORMATION()!= null && !process.getVAL_ST_ACTION_CENTRE_FORMATION().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_CENTRE_FORMATION())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CENTRE_FORMATION() %>" size="35" type="text" value="<%= process.getVAL_EF_CENTRE_FORMATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_CENTRE_FORMATION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CENTRE_FORMATION()%>"></span>
					<% } else{ %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_CENTRE_FORMATION()%>"></span>
					<%} %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_CENTRE_FORMATION() %>" size="35" type="text" value="<%= process.getVAL_EF_CENTRE_FORMATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CENTRE_FORMATION()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CENTRE_FORMATION()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Titre de permis</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_TITRE_PERMIS() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_TITRE_PERMIS(), process.getVAL_LB_TITRE_PERMIS_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TITRE_PERMIS()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_TITRE_PERMIS()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TITRE_PERMIS()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_TITRE_PERMIS()!= null && !process.getVAL_ST_ACTION_TITRE_PERMIS().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TITRE_PERMIS())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_TITRE_PERMIS() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_PERMIS() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TITRE_PERMIS())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TITRE_PERMIS()%>"></span>
					<% } else{ %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_TITRE_PERMIS()%>"></span>
					<%} %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_TITRE_PERMIS() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_PERMIS() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TITRE_PERMIS()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE_PERMIS()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>
		</FORM>
	</BODY>
</HTML>