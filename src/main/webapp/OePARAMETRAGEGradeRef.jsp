<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.Const"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres de grade</TITLE>
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEGradeRef" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<div style="width:100%">
	    	<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Classes</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_CLASSE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_CLASSE(), process.getVAL_LB_CLASSE_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CLASSE()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_CLASSE()%>">
        		</div>
				
            	<% if (process.getVAL_ST_ACTION_CLASSE()!= null && !process.getVAL_ST_ACTION_CLASSE().equals("")) {%>
            	<br>
	            
				<label class="sigp2Mandatory" Style="width:100px">Code:</label>
				<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_CODE_CLASSE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_CLASSE() %>" style="margin-right:10px; margin-bottom:10px">
				<br />
				<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
				<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="60" name="<%= process.getNOM_EF_LIBELLE_CLASSE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIBELLE_CLASSE() %>" style="margin-right:10px;margin-bottom:10px">
				<br />
					
				<div Style="width:100%" align="center">
				<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_CLASSE())){ %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_CLASSE()%>">
				<% } else { %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CLASSE()%>">
				<% } %>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CLASSE()%>"></span>
				</div>
				<% } %>

			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Echelons</legend>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_ECHELON() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_ECHELON(), process.getVAL_LB_ECHELON_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ECHELON()%>">
					<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ECHELON()%>">
        		</div>

            <% if (process.getVAL_ST_ACTION_ECHELON()!= null && !process.getVAL_ST_ACTION_ECHELON().equals("")) {%>
            	<br>
	            <label class="sigp2Mandatory" Style="width:100px">Code:</label>
				<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="3" name="<%= process.getNOM_EF_CODE_ECHELON() %>" size="3" type="text" value="<%= process.getVAL_EF_CODE_ECHELON() %>" style="margin-right:10px; margin-bottom:10px">
				<br />
				<label class="sigp2Mandatory" Style="width:100px">Libellé:</label>
				<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="60" name="<%= process.getNOM_EF_LIBELLE_ECHELON() %>" size="35" type="text" value="<%= process.getVAL_EF_LIBELLE_ECHELON() %>" style="margin-right:10px;margin-bottom:10px">
				<br />
				
				<div Style="width:100%" align="center">
				<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_ECHELON())){ %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ECHELON()%>">
				<% } else { %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ECHELON()%>">
				<% } %>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ECHELON()%>"></span>
				</div>
			<% } %>
			
			</FIELDSET>
			
			</div>
			<div style="width:100%">
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
		    	<legend class="sigp2Legend">Barêmes</legend>
				<span style="position:relative;width:100px;">IBA</span>
				<span style="position:relative;width:73px;">INA</span>
				<span style="position:relative;width:73px;">INM</span>
				
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_BAREME() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_BAREME(), process.getVAL_LB_BAREME_SELECT()) %>
				</SELECT>
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_BAREME()%>">
        	    	<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_BAREME()%>">
        	    </div>
            	
            	<% if (process.getVAL_ST_ACTION_BAREME()!= null && !process.getVAL_ST_ACTION_BAREME().equals("")) {%>
            	<br>
            	<label class="sigp2Mandatory" Style="width:100px">IBA:</label>
				<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_BAREME())){ %>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="7" disabled="disabled" name="<%= process.getNOM_EF_IBAN_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_IBAN_BAREME() %>" style="margin-right:10px;margin-bottom:10px">
				<% } else { %>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="7" name="<%= process.getNOM_EF_IBAN_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_IBAN_BAREME() %>" style="margin-right:10px;margin-bottom:10px">
				<% } %>
				 	
				<br />
				
				<label class="sigp2Mandatory" Style="width:100px">INA:</label>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_INA_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_INA_BAREME() %>" style="margin-right:10px;margin-bottom:10px">
				<br />
				<label class="sigp2Mandatory" Style="width:100px">INM:</label>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_INM_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_INM_BAREME() %>" style="margin-right:10px;margin-bottom:10px">
				<br />
				
				<div Style="width:100%" align="center">
				<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_BAREME())){ %>
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_BAREME()%>"></span>
				<% } else { %>
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_BAREME()%>"></span>
				<% } %>
				
				
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_BAREME()%>"></span>
				</div>	
			<% } %>
			
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">
			
		    	<legend class="sigp2Legend">Grades génériques</legend>
				<span style="position:relative;width:44px;">Code</span>
				<span style="position:relative;width:40px;">Cat</span>
				<span style="position:relative;width:40px;">Inactif</span>
				<span style="position:relative;width:73px;">Libellé</span>
				
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_GRADE_GENERIQUE() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_GRADE_GENERIQUE(), process.getVAL_LB_GRADE_GENERIQUE_SELECT()) %>
				</SELECT>
				
				
            	</span>

				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_GRADE_GENERIQUE()%>">
        	    	<INPUT tabindex="" type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_GRADE_GENERIQUE()%>">
        	    </div>
            	
            	<% if (process.getVAL_ST_ACTION_GRADE_GENERIQUE()!= null && !process.getVAL_ST_ACTION_GRADE_GENERIQUE().equals(Const.CHAINE_VIDE)) {%>
	            	<br>
					<label class="sigp2Mandatory" Style="width:100px">Code :</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="4" <%= process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_GRADE_GENERIQUE()) ? "disabled='disabled'" : Const.CHAINE_VIDE %> name="<%= process.getNOM_EF_CODE_GRADE_GENERIQUE() %>" size="4" type="text" value="<%= process.getVAL_EF_CODE_GRADE_GENERIQUE() %>" style="margin-right:10px;margin-bottom:10px">
					<br />
					
					<label class="sigp2Mandatory" Style="width:100px">Libellé :</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="50"	  name="<%= process.getNOM_EF_LIBELLE_GRADE_GENERIQUE() %>" size="50" type="text" value="<%= process.getVAL_EF_LIBELLE_GRADE_GENERIQUE() %>" style="margin-right:10px;margin-bottom:10px">
					
					<br />
					
					<label class="sigp2" Style="width:100px">Catégorie :</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CATEGORIE() %>">
							<%=process.forComboHTML(process.getVAL_LB_CATEGORIE(), process.getVAL_LB_CATEGORIE_SELECT()) %>
					</SELECT>				
					<br />
					<br />
					
					<label class="sigp2" Style="width:100px">Cadre emploi :</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CADRE_EMPLOI_GRADE() %>">
							<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI_GRADE(), process.getVAL_LB_CADRE_EMPLOI_GRADE_SELECT()) %>
					</SELECT>				
					<br />
	
					<LABEL class="sigp2Mandatory" Style="width:100px">Inactif :</label>					
					<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_INACTIF(), process.getNOM_RB_OUI()) %> >oui
					<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_INACTIF(), process.getNOM_RB_NON()) %> >non
					<br />
					<br />
					<label class="sigp2Mandatory" Style="width:100px">Nb points :</label>
					<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_NB_PTS_CATEGORIE() %>" size="4" type="text" value="<%= process.getVAL_EF_NB_PTS_CATEGORIE() %>" style="margin-right:10px; margin-bottom:10px">
					<br />											
					<label class="sigp2" Style="width:100px">Filière :</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FILIERE() %>">
							<%=process.forComboHTML(process.getVAL_LB_FILIERE(), process.getVAL_LB_FILIERE_SELECT()) %>
					</SELECT>				
					<br />				
					<br />						
					<label class="sigp2" Style="width:100px">Texte CAP cadre emploi :</label>
					<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TEXTE_CAP_GRADE_GENERIQUE() %>" size="50" type="text" value="<%= process.getVAL_EF_TEXTE_CAP_GRADE_GENERIQUE() %>" style="margin-right:10px;margin-bottom:10px">					
					<br />
					<br/>
					
					<label class="sigp2" Style="width:100px">Déliberation territoriale :</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DELIB_TERR_GRADE() %>">
							<%=process.forComboHTML(process.getVAL_LB_DELIB_TERR_GRADE(), process.getVAL_LB_DELIB_TERR_GRADE_SELECT()) %>
					</SELECT>				
					<br />
					<br />
					
					<label class="sigp2" Style="width:100px">Déliberation communale :</label>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DELIB_COMM_GRADE() %>">
							<%=process.forComboHTML(process.getVAL_LB_DELIB_COMM_GRADE(), process.getVAL_LB_DELIB_COMM_GRADE_SELECT()) %>
					</SELECT>				
					<br />
					<br />
					<div Style="width:100%" align="center">
					
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_GRADE_GENERIQUE())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_GRADE_GENERIQUE()%>"></span>
					<% } else { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_GRADE_GENERIQUE()%>"></span>
					<% } %>
					
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_GRADE_GENERIQUE()%>"></span>
					</div>
				<% } %>
			
				</FIELDSET>
			</div>
			<div style="width:100%">
			<FIELDSET class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:500px; float:left;">			
		    	<legend class="sigp2Legend">Cadre emploi</legend>
				<span style="position:relative;width:290px;">Libellé</span>				
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
				<SELECT name="<%= process.getNOM_LB_CADRE_EMPLOI() %>" size="10"
					style="width:100%;" class="sigp2-liste">
					<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI(), process.getVAL_LB_CADRE_EMPLOI_SELECT()) %>
				</SELECT>
            	</span>            	
				<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
					<INPUT tabindex="" type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CADRE_EMPLOI()%>">
    	        	<INPUT tabindex="" type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CADRE_EMPLOI()%>">
    	        </div>
            	<% if (process.getVAL_EF_ACTION_CADRE_EMPLOI()!= null && !process.getVAL_EF_ACTION_CADRE_EMPLOI().equals("")) {%>
            	<br>
				<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_EF_ACTION_CADRE_EMPLOI())) { %>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CADRE_EMPLOI() %>" size="35" type="text" value="<%= process.getVAL_EF_CADRE_EMPLOI() %>" style="margin-right:10px;margin-bottom:10px">
					<br />	
					<br />				
					<div Style="width:100%" align="center">					
					<% if (process.ACTION_CREATION.equals(process.getVAL_EF_ACTION_CADRE_EMPLOI())) { %>
						<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CADRE_EMPLOI()%>"></span>
					<% } %>
				<%} else {%>
					<label class="sigp2Mandatory" Style="width:50px">Libellé:</label>
					<INPUT disabled="disabled" tabindex="" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CADRE_EMPLOI() %>" size="35" type="text" value="<%= process.getVAL_EF_CADRE_EMPLOI() %>" style="margin-right:10px;margin-bottom:10px">
					<br />	
					<br />					
					<div Style="width:100%" align="center">
					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CADRE_EMPLOI()%>"></span>
			   <%}%>
				<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CADRE_EMPLOI()%>"></span>
				</div>
				<% } %>			
			</FIELDSET>		
			</div>
		</FORM>
	</BODY>
</HTML>