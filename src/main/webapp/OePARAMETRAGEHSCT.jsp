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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEHSCT" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Médecins</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_MEDECIN() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_MEDECIN(), process.getVAL_LB_MEDECIN_SELECT()) %>
				</SELECT>
            	</span>
				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MEDECIN()%>">
	            	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_MEDECIN()%>">
	            </div>
            	
            	<% if (process.getVAL_ST_ACTION_MEDECIN()!= null && !process.getVAL_ST_ACTION_MEDECIN().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_MEDECIN())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Titre:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_TITRE_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_MEDECIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Prénom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_PRENOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_MEDECIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Nom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_MEDECIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MEDECIN())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MEDECIN()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Titre:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_TITRE_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_MEDECIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Prénom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_PRENOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_MEDECIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Nom:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_NOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_MEDECIN() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MEDECIN()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MEDECIN()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Recommandation</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_RECOMMANDATION() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_RECOMMANDATION(), process.getVAL_LB_RECOMMANDATION_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_RECOMMANDATION()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_RECOMMANDATION()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_RECOMMANDATION()!= null && !process.getVAL_ST_ACTION_RECOMMANDATION().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_RECOMMANDATION())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_RECOMMANDATION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_RECOMMANDATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_RECOMMANDATION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_RECOMMANDATION()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_RECOMMANDATION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_RECOMMANDATION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_RECOMMANDATION()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_RECOMMANDATION()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>	
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Type d'inaptitudes</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_INAPTITUDE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_INAPTITUDE(), process.getVAL_LB_INAPTITUDE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_INAPTITUDE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_INAPTITUDE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_INAPTITUDE()!= null && !process.getVAL_ST_ACTION_INAPTITUDE().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_INAPTITUDE())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_INAPTITUDE() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_INAPTITUDE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_INAPTITUDE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_INAPTITUDE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_INAPTITUDE() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_INAPTITUDE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_INAPTITUDE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_INAPTITUDE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Types d'AT</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_AT() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_AT(), process.getVAL_LB_AT_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_AT()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_AT()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_AT()!= null && !process.getVAL_ST_ACTION_AT().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_AT())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_AT() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_AT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_AT())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_AT()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_AT() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_AT() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_AT()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_AT()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>	
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Sièges lésions</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_LESION() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_LESION(), process.getVAL_LB_LESION_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_LESION()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_LESION()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_LESION()!= null && !process.getVAL_ST_ACTION_LESION().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_LESION())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_LESION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_LESION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_LESION())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_LESION()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Description:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_LESION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_LESION() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_LESION()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_LESION()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Maladies professionelles</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_MALADIE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_MALADIE(), null, null, process.getVAL_LB_MALADIE(), process.getVAL_LB_MALADIE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MALADIE()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_MALADIE()%>">
    	        </div>
            	
            	<% if (process.getVAL_ST_ACTION_MALADIE()!= null && !process.getVAL_ST_ACTION_MALADIE().equals("")) {%>
            	<br>
	            
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_MALADIE())) { %>
					<label class="sigp2Mandatory" Style="width:100px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_CODE_MALADIE() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_MALADIE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_LIBELLE_MALADIE() %>" size="65" type="text" value="<%= process.getVAL_EF_LIBELLE_MALADIE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MALADIE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MALADIE()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:100px">Code:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="30" readonly="readonly" name="<%= process.getNOM_EF_CODE_MALADIE() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_MALADIE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_LIBELLE_MALADIE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIBELLE_MALADIE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MALADIE()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MALADIE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			</div>	
			<div style="width:100%">
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
		</FORM>
	</BODY>
</HTML>