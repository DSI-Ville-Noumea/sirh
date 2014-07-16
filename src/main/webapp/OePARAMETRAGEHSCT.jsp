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
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px;" cellpadding="0" cellspacing="0">
				<tr>
					<td width="300px">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Médecins</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_MEDECIN() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_MEDECIN(), process.getVAL_LB_MEDECIN_SELECT()) %>
							</SELECT>
			            	</span>
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MEDECIN()%>">
				            	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_MEDECIN()%>">
				            </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_MEDECIN()!= null && !process.getVAL_ST_ACTION_MEDECIN().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_MEDECIN())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Titre:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_TITRE_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_MEDECIN() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Prénom:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_PRENOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_MEDECIN() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Nom:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" name="<%= process.getNOM_EF_NOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_MEDECIN() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MEDECIN())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MEDECIN()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MEDECIN()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Titre:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_TITRE_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_TITRE_MEDECIN() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Prénom:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_PRENOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_PRENOM_MEDECIN() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Nom:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50" readonly="readonly" name="<%= process.getNOM_EF_NOM_MEDECIN() %>" size="35" type="text" value="<%= process.getVAL_EF_NOM_MEDECIN() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MEDECIN()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MEDECIN()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>			
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Recommandation</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_RECOMMANDATION() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_RECOMMANDATION(), process.getVAL_LB_RECOMMANDATION_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_RECOMMANDATION()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_RECOMMANDATION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_RECOMMANDATION()!= null && !process.getVAL_ST_ACTION_RECOMMANDATION().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_RECOMMANDATION())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_RECOMMANDATION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_RECOMMANDATION() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_RECOMMANDATION())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_RECOMMANDATION()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_RECOMMANDATION()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_RECOMMANDATION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_RECOMMANDATION() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_RECOMMANDATION()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_RECOMMANDATION()%>">
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
					    	<legend class="sigp2Legend">Type d'inaptitudes</legend>
							<span class="sigp2-titre" >
							<SELECT name="<%= process.getNOM_LB_INAPTITUDE() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_INAPTITUDE(), process.getVAL_LB_INAPTITUDE_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_INAPTITUDE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_INAPTITUDE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_INAPTITUDE()!= null && !process.getVAL_ST_ACTION_INAPTITUDE().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_INAPTITUDE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_INAPTITUDE() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_INAPTITUDE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_INAPTITUDE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_INAPTITUDE()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_INAPTITUDE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_INAPTITUDE() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_INAPTITUDE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_INAPTITUDE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_INAPTITUDE()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Types d'AT</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_AT() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_AT(), process.getVAL_LB_AT_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_AT()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_AT()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_AT()!= null && !process.getVAL_ST_ACTION_AT().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_AT())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_AT() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_AT() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_AT())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_AT()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_AT()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_AT() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_AT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_AT()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_AT()%>">
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
					    	<legend class="sigp2Legend">Sièges lésions</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_LESION() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_LESION(), process.getVAL_LB_LESION_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_LESION()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_LESION()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_LESION()!= null && !process.getVAL_ST_ACTION_LESION().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_LESION())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_DESC_LESION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_LESION() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_LESION())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_LESION()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_LESION()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_DESC_LESION() %>" size="35" type="text" value="<%= process.getVAL_EF_DESC_LESION() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_LESION()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_LESION()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Maladies professionelles</legend>
							<SELECT name="<%= process.getNOM_LB_MALADIE() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_MALADIE(), process.getVAL_LB_MALADIE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_MALADIE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_MALADIE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_MALADIE()!= null && !process.getVAL_ST_ACTION_MALADIE().equals("")) {%>
			            	<br>
				            <table width="400px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_MALADIE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_CODE_MALADIE() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_MALADIE() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_LIBELLE_MALADIE() %>" size="65" type="text" value="<%= process.getVAL_EF_LIBELLE_MALADIE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_MALADIE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_MALADIE()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MALADIE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" readonly="readonly" name="<%= process.getNOM_EF_CODE_MALADIE() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_MALADIE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" readonly="readonly" name="<%= process.getNOM_EF_LIBELLE_MALADIE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIBELLE_MALADIE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_MALADIE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_MALADIE()%>">
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
						<FIELDSET class="sigp2Fieldset"  style="text-align:">
					    	<legend class="sigp2Legend">Types de documents</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_TYPE_DOCUMENT() %>" size="10" class="sigp2-liste">
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
					<td>&nbsp;</td>
				</tr>
			</table>
		</FORM>
	</BODY>
</HTML>