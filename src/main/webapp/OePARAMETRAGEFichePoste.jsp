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
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px;" cellpadding="0" cellspacing="0">
				<tr>
					<td width="500px">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Entités géographiques</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_ENTITE_GEO() %>" size="10"
								style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_ENTITE_GEO(), process.getVAL_LB_ENTITE_GEO_SELECT()) %>
							</SELECT>
			            	</span>
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ENTITE_GEO()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ENTITE_GEO()%>">
			            		<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_ENTITE_GEO()%>">
			            	</div>   
			            	
			            	<% if (process.getVAL_ST_ACTION_ENTITE_GEO()!= null && !process.getVAL_ST_ACTION_ENTITE_GEO().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_ENTITE_GEO())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="60" name="<%= process.getNOM_EF_ENTITE_GEO() %>" size="35" type="text" value="<%= process.getVAL_EF_ENTITE_GEO() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Ecole:</label>
					            		</td>
					            		<td>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ENTITE_GEO_ECOLE() %>">
													<%=process.forComboHTML(process.getVAL_LB_ENTITE_GEO_ECOLE(), process.getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) %>
											</SELECT>
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ENTITE_GEO())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ENTITE_GEO()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ENTITE_GEO()%>">
											<%} %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ENTITE_GEO()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="20" readonly="readonly" name="<%= process.getNOM_EF_ENTITE_GEO() %>" size="35" type="text" value="<%= process.getVAL_EF_ENTITE_GEO() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Ecole:</label>
					            		</td>
					            		<td>
											<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ENTITE_GEO_ECOLE() %>">
													<%=process.forComboHTML(process.getVAL_LB_ENTITE_GEO_ECOLE(), process.getVAL_LB_ENTITE_GEO_ECOLE_SELECT()) %>
											</SELECT>	
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ENTITE_GEO()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ENTITE_GEO()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Titres de poste</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_TITRE() %>" size="10"
								style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TITRE(), process.getVAL_LB_TITRE_SELECT()) %>
							</SELECT>
			            	</span>
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TITRE()%>">
			        	    	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TITRE()%>">
			        	    </div>  
			            	
			            	<% if (process.getVAL_ST_ACTION_TITRE()!= null && !process.getVAL_ST_ACTION_TITRE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TITRE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_TITRE() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TITRE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TITRE()%>">	
											<% }%>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_TITRE() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TITRE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 		
						</FIELDSET>	
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Types d'avantage en nature</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_TYPE_AVANTAGE() %>" size="10"
								style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TYPE_AVANTAGE(), process.getVAL_LB_TYPE_AVANTAGE_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_AVANTAGE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_AVANTAGE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TYPE_AVANTAGE()!= null && !process.getVAL_ST_ACTION_TYPE_AVANTAGE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_AVANTAGE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_TYPE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_AVANTAGE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_AVANTAGE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_AVANTAGE()%>">	
											<% }%>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_AVANTAGE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_TYPE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_AVANTAGE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_AVANTAGE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_AVANTAGE()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 			
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Natures d'avantage en nature</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_NATURE_AVANTAGE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_NATURE_AVANTAGE(), process.getVAL_LB_NATURE_AVANTAGE_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_NATURE_AVANTAGE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_NATURE_AVANTAGE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_NATURE_AVANTAGE()!= null && !process.getVAL_ST_ACTION_NATURE_AVANTAGE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_NATURE_AVANTAGE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NATURE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_NATURE_AVANTAGE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_NATURE_AVANTAGE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_NATURE_AVANTAGE()%>">	
											<% }%>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NATURE_AVANTAGE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_NATURE_AVANTAGE() %>" size="35" type="text" value="<%= process.getVAL_EF_NATURE_AVANTAGE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_NATURE_AVANTAGE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NATURE_AVANTAGE()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 						
						</FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Types de délégation</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_TYPE_DELEGATION() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TYPE_DELEGATION(), process.getVAL_LB_TYPE_DELEGATION_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_DELEGATION()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_DELEGATION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TYPE_DELEGATION()!= null && !process.getVAL_ST_ACTION_TYPE_DELEGATION().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_DELEGATION())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_TYPE_DELEGATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_DELEGATION() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_DELEGATION())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_DELEGATION()%>">	
											<% }%>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_DELEGATION()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" readonly="readonly" name="<%= process.getNOM_EF_TYPE_DELEGATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_DELEGATION() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_DELEGATION()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_DELEGATION()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Types de régime</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_TYPE_REGIME() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TYPE_REGIME(), process.getVAL_LB_TYPE_REGIME_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_REGIME()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_REGIME()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TYPE_REGIME()!= null && !process.getVAL_ST_ACTION_TYPE_REGIME().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_REGIME())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="20" name="<%= process.getNOM_EF_TYPE_REGIME() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_REGIME() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_REGIME())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_REGIME()%>">	
											<% }%>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_REGIME()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="20" readonly="readonly" name="<%= process.getNOM_EF_TYPE_REGIME() %>" size="35" type="text" value="<%= process.getVAL_EF_TYPE_REGIME() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_REGIME()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_REGIME()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 						
						</FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">NFA</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_NFA() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_NFA(), process.getVAL_LB_NFA_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_NFA()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_NFA()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_NFA()!= null && !process.getVAL_ST_ACTION_NFA().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_NFA())) { %>
					            	<tr>
					            		<td width="90px">
											<label class="sigp2Mandatory">Code service:</label>
					            		</td>
					            		<td align="left">
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="4" name="<%= process.getNOM_EF_NFA_CODE_SERVICE() %>" size="4" type="text" value="<%= process.getVAL_EF_NFA_CODE_SERVICE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">NFA:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_NFA() %>" size="5" type="text" value="<%= process.getVAL_EF_NFA() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_NFA())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_NFA()%>">	
											<% }%>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NFA()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="90px">
											<label class="sigp2Mandatory">Code service:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="4" readonly="readonly" name="<%= process.getNOM_EF_NFA_CODE_SERVICE() %>" size="4" type="text" value="<%= process.getVAL_EF_NFA_CODE_SERVICE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td align="left">
											<label class="sigp2Mandatory">NFA:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="5" readonly="readonly" name="<%= process.getNOM_EF_NFA() %>" size="5" type="text" value="<%= process.getVAL_EF_NFA() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_NFA()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_NFA()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 			
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Ecoles</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_ECOLE() %>" size="10"
								style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_ECOLE(), process.getVAL_LB_ECOLE_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ECOLE()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ECOLE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_ECOLE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_ECOLE()!= null && !process.getVAL_ST_ACTION_ECOLE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_ECOLE())) { %>
					            	<tr>
					            		<td width="90px">
											<label class="sigp2Mandatory">Code école:</label>
					            		</td>
					            		<td align="left">
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" disabled="disabled" name="<%= process.getNOM_EF_ECOLE_CODE_ECOLE() %>" size="2" type="text" value="<%= process.getVAL_EF_ECOLE_CODE_ECOLE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="40" name="<%= process.getNOM_EF_ECOLE() %>" size="40" type="text" value="<%= process.getVAL_EF_ECOLE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ECOLE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ECOLE()%>">						
					            		</td>
					            	</tr>		
					            <%}else if(process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ECOLE())) {%> 
					            	<tr>
					            		<td width="90px">
											<label class="sigp2Mandatory">Code école:</label>
					            		</td>
					            		<td align="left">
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_ECOLE_CODE_ECOLE() %>" size="2" type="text" value="<%= process.getVAL_EF_ECOLE_CODE_ECOLE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="40" name="<%= process.getNOM_EF_ECOLE() %>" size="40" type="text" value="<%= process.getVAL_EF_ECOLE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ECOLE()%>">	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ECOLE()%>">						
					            		</td>
					            	</tr>
					                       	
								<%} else {%>
					            	<tr>
					            		<td width="90px">
											<label class="sigp2Mandatory">Code école:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" readonly="readonly" name="<%= process.getNOM_EF_ECOLE_CODE_ECOLE() %>" size="2" type="text" value="<%= process.getVAL_EF_ECOLE_CODE_ECOLE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td align="left">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="40" readonly="readonly" name="<%= process.getNOM_EF_ECOLE() %>" size="40" type="text" value="<%= process.getVAL_EF_ECOLE() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ECOLE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ECOLE()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%> 	
						</FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Liste des bases horaires de pointage</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Code</span>
							<span class="sigp2-saisie" style="margin-left: 20px;">Libellé</span>
							<SELECT name="<%= process.getNOM_LB_BASE_HORAIRE_POINTAGE() %>" size="10"
								style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_BASE_HORAIRE_POINTAGE(), process.getVAL_LB_BASE_HORAIRE_POINTAGE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_BASE_HORAIRE_POINTAGE()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_BASE_HORAIRE_POINTAGE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE()!= null && !process.getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE().equals("")) {%>
			            	<br>
			            	<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_BASE_HORAIRE_POINTAGE())) { %>
			            	<table>
								<tr>
									<td colspan="3">
											<label class="sigp2Mandatory" style="color: red;">Les heures sont à saisir sous la forme "H.Mn"</label>
									</td>
								</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Code :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CODE_BASE_HORAIRE_POINTAGE() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_BASE_HORAIRE_POINTAGE() %>">
			            			</td>
			            			<td>
										<label class="sigp2Mandatory">Libellé :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_LIB_BASE_HORAIRE_POINTAGE() %>" size="20" type="text" value="<%= process.getVAL_EF_LIB_BASE_HORAIRE_POINTAGE() %>">
			            			</td>
			            		</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Description :</label>
			            			</td>
			            			<td colspan="3">
										<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_BASE_HORAIRE_POINTAGE() %>" size="20" type="text" value="<%= process.getVAL_EF_DESC_BASE_HORAIRE_POINTAGE() %>">
			            			</td>
			            		</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Lundi :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_LUNDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_LUNDI() %>">
			            			</td>
			            			<td>
										<label class="sigp2Mandatory">Vendredi :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_VENDREDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_VENDREDI() %>">
			            			</td>
			            		</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Mardi :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_MARDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_MARDI() %>">
			            			</td>
			            			<td>
										<label class="sigp2Mandatory">Samedi :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_SAMEDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_SAMEDI() %>">
			            			</td>
			            		</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Mercredi :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_MERCREDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_MERCREDI() %>">
			            			</td>
			            			<td>
										<label class="sigp2Mandatory">Dimanche :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_DIMANCHE() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_DIMANCHE() %>">
			            			</td>
			            		</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Jeudi :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_JEUDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_JEUDI() %>">
			            			</td>
			            			<td>&nbsp;
			            			</td>
			            			<td>&nbsp;
			            			</td>
			            		</tr>
			            		<tr>
			            			<td colspan="2">
										<label class="sigp2Mandatory">Base légale hebdomadaire :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_BASE_HEBDO_LEG_H() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_LEG_H() %>"><span class="sigp2-saisie">H</span>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_BASE_HEBDO_LEG_M() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_LEG_M() %>"><span class="sigp2-saisie">Mn</span>
			            			</td>           			
			            		</tr>
			            		<tr>
			            			<td colspan="2">
										<label class="sigp2Mandatory">Base hebdomadaire (calc) :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_BASE_HEBDO_H() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_H() %>"><span class="sigp2-saisie">H</span>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_BASE_HEBDO_M() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_M() %>"><span class="sigp2-saisie">Mn</span>
			            			</td>          			
			            		</tr>
			            		<tr>
			            			<td colspan="4" align="center">
			            			<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_BASE_HORAIRE_POINTAGE()%>">
			            			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE()%>">
			            			</td>
			            		</tr>
			            	</table>					
							<%} else {%>
							<table>
								<tr>
									<td colspan="3">
											<label class="sigp2Mandatory" style="color: red;">Les heures sont à saisir sous la forme "H.Mn"</label>
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Code :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_CODE_BASE_HORAIRE_POINTAGE() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_BASE_HORAIRE_POINTAGE() %>">
									</td>
									<td>
										<label class="sigp2Mandatory">Libellé :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_LIB_BASE_HORAIRE_POINTAGE() %>" size="20" type="text" value="<%= process.getVAL_EF_LIB_BASE_HORAIRE_POINTAGE() %>">
									</td>
								</tr>
			            		<tr>
			            			<td>
										<label class="sigp2Mandatory">Description :</label>
			            			</td>
			            			<td colspan="3">
										<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_BASE_HORAIRE_POINTAGE() %>" size="20" type="text" value="<%= process.getVAL_EF_DESC_BASE_HORAIRE_POINTAGE() %>">
			            			</td>
			            		</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Lundi :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_LUNDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_LUNDI() %>">
									</td>
									<td>
										<label class="sigp2Mandatory">Vendredi :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_VENDREDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_VENDREDI() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Mardi :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_MARDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_MARDI() %>">
									</td>
									<td>
										<label class="sigp2Mandatory">Samedi :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_SAMEDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_SAMEDI() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Mercredi :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_MERCREDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_MERCREDI() %>">
									</td>
									<td>
										<label class="sigp2Mandatory">Dimanche :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_DIMANCHE() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_DIMANCHE() %>">
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Jeudi :</label>
									</td>
									<td>
										<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_HEURE_JEUDI() %>" size="5" type="text" value="<%= process.getVAL_EF_HEURE_JEUDI() %>">
									</td>
									<td colspan="2">&nbsp;
									</td>
								</tr>
			            		<tr>
			            			<td colspan="2">
										<label class="sigp2Mandatory">Base légale hebdomadaire :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_BASE_HEBDO_LEG_H() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_LEG_H() %>"><span class="sigp2-saisie">H</span>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_BASE_HEBDO_LEG_M() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_LEG_M() %>"><span class="sigp2-saisie">Mn</span>
			            			</td>           			
			            		</tr>
			            		<tr>
			            			<td colspan="2">
										<label class="sigp2Mandatory">Base hebdomadaire (calc) :</label>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_BASE_HEBDO_H() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_H() %>"><span class="sigp2-saisie">H</span>
			            			</td>
			            			<td>
										<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_BASE_HEBDO_M() %>" size="5" type="text" value="<%= process.getVAL_EF_BASE_HEBDO_M() %>"><span class="sigp2-saisie">Mn</span>
			            			</td>          			
			            		</tr>
			            		<tr>
			            			<td colspan="4" align="center">
			            			<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_BASE_HORAIRE_POINTAGE()%>">
			            			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_BASE_HORAIRE_POINTAGE()%>">
			            			</td>
			            		</tr>
							</table>
						   <%}%>
							<%} %>							
						</FIELDSET>	
					</td>
					<td></td>
				</tr>
			</table>	
		</FORM>
	</BODY>
</HTML>