<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEDonneesPerso" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px;" cellpadding="0" cellspacing="0">
				<tr>
					<td width="500px">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Titre de diplômes</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_DIPLOME() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_DIPLOME(), process.getVAL_LB_DIPLOME_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DIPLOME()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_DIPLOME()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME()%>">
			    	        </div> 
			            	
			            	<% if (process.getVAL_ST_ACTION_DIPLOME()!= null && !process.getVAL_ST_ACTION_DIPLOME().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_DIPLOME())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Niveau d'études:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisiemajuscule" maxlength="10" name="<%= process.getNOM_EF_NIV_ETUDE() %>" size="10" type="text" value="<%= process.getVAL_EF_NIV_ETUDE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_DIPLOME())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>">
											<%} %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DIPLOME()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Niveau d'études:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisiemajuscule" maxlength="10" readonly="readonly" name="<%= process.getNOM_EF_NIV_ETUDE() %>" size="10" type="text" value="<%= process.getVAL_EF_NIV_ETUDE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DIPLOME()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>			
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Spécialité du diplôme</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_SPECIALITE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_SPECIALITE(), process.getVAL_LB_SPECIALITE_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_SPECIALITE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_SPECIALITE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_SPECIALITE()!= null && !process.getVAL_ST_ACTION_SPECIALITE().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_SPECIALITE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_SPECIALITE() %>" size="55" type="text" value="<%= process.getVAL_EF_SPECIALITE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_SPECIALITE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_SPECIALITE()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_SPECIALITE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_SPECIALITE() %>" size="55" type="text" value="<%= process.getVAL_EF_SPECIALITE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_SPECIALITE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_SPECIALITE()%>">
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
					    	<legend class="sigp2Legend">Autre administration</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_ADMIN() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_ADMIN(), process.getVAL_LB_ADMIN_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ADMIN()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_ADMIN()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_ADMIN()!= null && !process.getVAL_ST_ACTION_ADMIN().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_ADMIN())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_ADMIN() %>" size="35" type="text" value="<%= process.getVAL_EF_ADMIN() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ADMIN())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ADMIN()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ADMIN()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_ADMIN() %>" size="35" type="text" value="<%= process.getVAL_EF_ADMIN() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_ADMIN()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ADMIN()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>					
				    	<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Types de documents</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_TYPE_DOCUMENT() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TYPE_DOCUMENT(), process.getVAL_LB_TYPE_DOCUMENT_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TYPE_DOCUMENT()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TYPE_DOCUMENT()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TYPE_DOCUMENT()!= null && !process.getVAL_ST_ACTION_TYPE_DOCUMENT().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TYPE_DOCUMENT())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_TYPE_DOCUMENT() %>" size="30" type="text" value="<%= process.getVAL_EF_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_TYPE_DOCUMENT() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TYPE_DOCUMENT())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TYPE_DOCUMENT()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_DOCUMENT()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" readonly="readonly" name="<%= process.getNOM_EF_TYPE_DOCUMENT() %>" size="30" type="text" value="<%= process.getVAL_EF_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_TYPE_DOCUMENT() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TYPE_DOCUMENT()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TYPE_DOCUMENT()%>">
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
					    	<legend class="sigp2Legend">Titre de formations</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_TITRE_FORMATION() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TITRE_FORMATION(), process.getVAL_LB_TITRE_FORMATION_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TITRE_FORMATION()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_TITRE_FORMATION()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TITRE_FORMATION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TITRE_FORMATION()!= null && !process.getVAL_ST_ACTION_TITRE_FORMATION().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TITRE_FORMATION())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_TITRE_FORMATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_FORMATION() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TITRE_FORMATION())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TITRE_FORMATION()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_TITRE_FORMATION()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE_FORMATION()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_TITRE_FORMATION() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_FORMATION() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TITRE_FORMATION()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE_FORMATION()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>			
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Centres de formations</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_CENTRE_FORMATION() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_CENTRE_FORMATION(), process.getVAL_LB_CENTRE_FORMATION_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CENTRE_FORMATION()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_CENTRE_FORMATION()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CENTRE_FORMATION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_CENTRE_FORMATION()!= null && !process.getVAL_ST_ACTION_CENTRE_FORMATION().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_CENTRE_FORMATION())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CENTRE_FORMATION() %>" size="55" type="text" value="<%= process.getVAL_EF_CENTRE_FORMATION() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_CENTRE_FORMATION())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CENTRE_FORMATION()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_CENTRE_FORMATION()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CENTRE_FORMATION()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_CENTRE_FORMATION() %>" size="55" type="text" value="<%= process.getVAL_EF_CENTRE_FORMATION() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CENTRE_FORMATION()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CENTRE_FORMATION()%>">
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
					    	<legend class="sigp2Legend">Titre de permis</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_TITRE_PERMIS() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TITRE_PERMIS(), process.getVAL_LB_TITRE_PERMIS_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TITRE_PERMIS()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_TITRE_PERMIS()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TITRE_PERMIS()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TITRE_PERMIS()!= null && !process.getVAL_ST_ACTION_TITRE_PERMIS().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TITRE_PERMIS())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_TITRE_PERMIS() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_PERMIS() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_TITRE_PERMIS())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_TITRE_PERMIS()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_TITRE_PERMIS()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE_PERMIS()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_TITRE_PERMIS() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_PERMIS() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TITRE_PERMIS()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TITRE_PERMIS()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>&nbsp;</td>
				</tr>			
			</table>
		</FORM>
	</BODY>
</HTML>