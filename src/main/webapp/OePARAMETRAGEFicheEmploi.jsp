<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEFicheEmploi" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px;" cellpadding="0" cellspacing="0">
				<tr>
					<td width="500px">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Domaines d'emploi</legend>
							<SELECT style="width: 100%;" name="<%= process.getNOM_LB_DOMAINE() %>" size="10" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DOMAINE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DOMAINE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_DOMAINE()!= null && !process.getVAL_ST_ACTION_DOMAINE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_DOMAINE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_CODE_DOMAINE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_DOMAINE() %>">
					            		</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_LIB_DOMAINE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_DOMAINE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_DOMAINE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DOMAINE()%>">	
											<% } %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DOMAINE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" readonly="readonly" name="<%= process.getNOM_EF_CODE_DOMAINE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_DOMAINE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_LIB_DOMAINE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIB_DOMAINE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DOMAINE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DOMAINE()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>			
						</FIELDSET>								
					</td>
					<td width="500px">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Famille d'emploi</legend>
							<SELECT name="<%= process.getNOM_LB_FAMILLE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_FAMILLE(), process.getVAL_LB_FAMILLE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_FAMILLE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_FAMILLE()%>">
			    	        </div>
			            	
			            <% if (process.getVAL_ST_ACTION_FAMILLE()!= null && !process.getVAL_ST_ACTION_FAMILLE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_FAMILLE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="3" name="<%= process.getNOM_EF_CODE_FAMILLE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_FAMILLE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_FAMILLE() %>" size="35" type="text" value="<%= process.getVAL_EF_FAMILLE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_FAMILLE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_FAMILLE()%>">	
											<% } %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_FAMILLE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="3" readonly="readonly" name="<%= process.getNOM_EF_CODE_FAMILLE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_FAMILLE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisiemajuscule" maxlength="100" readonly="readonly" name="<%= process.getNOM_EF_FAMILLE() %>" size="35" type="text" value="<%= process.getVAL_EF_FAMILLE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_FAMILLE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_FAMILLE()%>">
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
					    	<legend class="sigp2Legend">Diplômes génériques</legend>
							<SELECT name="<%= process.getNOM_LB_DIPLOME() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_DIPLOME(), process.getVAL_LB_DIPLOME_SELECT()) %>
							</SELECT>
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_DIPLOME()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME()%>">
			    	        </div>   
			            	
			            <% if (process.getVAL_EF_ACTION_DIPLOME()!= null && !process.getVAL_EF_ACTION_DIPLOME().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_EF_ACTION_DIPLOME())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="20" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_EF_ACTION_DIPLOME())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>">	
											<% } %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DIPLOME()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
											<INPUT readonly="readonly" class="sigp2-saisiemajuscule" maxlength="20" name="<%= process.getNOM_EF_DIPLOME() %>" size="35" type="text" value="<%= process.getVAL_EF_DIPLOME() %>">
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
					    	<legend class="sigp2Legend">Catégories</legend>
							<SELECT name="<%= process.getNOM_LB_CATEGORIE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_CATEGORIE(), process.getVAL_LB_CATEGORIE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CATEGORIE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CATEGORIE()%>">
			    	        </div>
			            	
			            <% if (process.getVAL_ST_ACTION_CATEGORIE()!= null && !process.getVAL_ST_ACTION_CATEGORIE().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_CATEGORIE())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_LIB_CATEGORIE() %>" size="2" type="text" value="<%= process.getVAL_EF_LIB_CATEGORIE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_CATEGORIE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CATEGORIE()%>">	
											<% } %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CATEGORIE()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisiemajuscule" maxlength="2" readonly="readonly" name="<%= process.getNOM_EF_LIB_CATEGORIE() %>" size="2" type="text" value="<%= process.getVAL_EF_LIB_CATEGORIE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CATEGORIE()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CATEGORIE()%>">
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
					    	<legend class="sigp2Legend">Code rome</legend>
							<SELECT name="<%= process.getNOM_LB_CODE_ROME() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_CODE_ROME(), process.getVAL_LB_CODE_ROME_SELECT()) %>
							</SELECT>         	
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CODE_ROME()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CODE_ROME()%>">
			    	        </div>
			            	
			            <% if (process.getVAL_EF_ACTION_CODE_ROME()!= null && !process.getVAL_EF_ACTION_CODE_ROME().equals("")) {%>
			            	<br>
				            <table width="500px">
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_EF_ACTION_CODE_ROME())) { %>
					            	<tr>
					            		<td width="70px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CODE_ROME() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_DESC_CODE_ROME() %>" size="70" type="text" value="<%= process.getVAL_EF_DESC_CODE_ROME() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_EF_ACTION_CODE_ROME())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CODE_ROME()%>">	
											<% } %>	
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CODE_ROME()%>">						
					            		</td>
					            	</tr>		            	
								<%} else {%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
											<INPUT readonly="readonly" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CODE_ROME() %>" size="35" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Description:</label>
					            		</td>
					            		<td>
					            			<INPUT readonly="readonly" class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_DESC_CODE_ROME() %>" size="70" type="text" value="<%= process.getVAL_EF_DESC_CODE_ROME() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CODE_ROME()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CODE_ROME()%>">
					            		</td>
					            	</tr>
				            	<%}%>
				            </table>
				            <%}%>	
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Fiches emploi ville</legend>
							<SELECT name="<%= process.getNOM_LB_FEV() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_FEV(), process.getVAL_LB_FEV_SELECT()) %>
							</SELECT>
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_FEV()%>">
			    	        </div>
			            	
			            <% if (process.getVAL_EF_ACTION_FEV() != null && process.ACTION_MODIFICATION.equals(process.getVAL_EF_ACTION_FEV())) {%>
			            	<br>
				            <table>
				            	<tr>
				            		<td width="30px">
										<label class="sigp2Mandatory">Réf. mairie : </label>
				            		</td>
				            		<td>
				            			<INPUT class="sigp2-saisie" disabled="disabled" maxlength="100" name="<%= process.getNOM_EF_FEV_REF_MAIRIE() %>" size="20" type="text" value="<%= process.getVAL_EF_FEV_REF_MAIRIE() %>">
									</td>
				            	</tr>
				            	<tr>
				            		<td width="30px">
										<label class="sigp2Mandatory">Libellé : </label>
				            		</td>
				            		<td>
				            			<INPUT class="sigp2-saisiemajuscule" disabled="disabled" maxlength="100" name="<%= process.getNOM_EF_FEV_LABEL() %>" size="100" type="text" value="<%= process.getVAL_EF_FEV_LABEL() %>">
									</td>
				            	</tr>	
				            	<tr>
				            		<td width="30px">
										<label class="sigp2">Libellé long : </label>
				            		</td>
				            		<td>
				            			<INPUT class="sigp2-saisiemajuscule" maxlength="255" name="<%= process.getNOM_EF_FEV_LABEL_LONG() %>" height="30" size="100" type="text" value="<%= process.getVAL_EF_FEV_LABEL_LONG() %>">
									</td>
				            	</tr>
								<tr>
				            		<td colspan="2" align="center">
				            			<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDIER_MODIFICATION_FEV()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_FEV()%>">
				            		</td>
				            	</tr>
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
				</tr>
			</table>
		</FORM>
	</BODY>
</HTML>