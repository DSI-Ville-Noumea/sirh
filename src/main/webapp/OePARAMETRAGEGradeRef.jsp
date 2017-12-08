<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEGradeRef" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<br/>
			<table width="1030px;" cellpadding="0" cellspacing="0">
				<tr>
					<td width="500px">
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Classes</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_CLASSE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_CLASSE(), process.getVAL_LB_CLASSE_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CLASSE()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_CLASSE()%>">
			        		</div>
			            	
			            	<% if (process.getVAL_ST_ACTION_CLASSE()!= null && !process.getVAL_ST_ACTION_CLASSE().equals("")) {%>
			            	<br>
				            <table width="400px">
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="2" name="<%= process.getNOM_EF_CODE_CLASSE() %>" size="2" type="text" value="<%= process.getVAL_EF_CODE_CLASSE() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisie" maxlength="60" name="<%= process.getNOM_EF_LIBELLE_CLASSE() %>" size="35" type="text" value="<%= process.getVAL_EF_LIBELLE_CLASSE() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_CLASSE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CLASSE()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_CLASSE()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CLASSE()%>">						
					            		</td>
					            	</tr>	
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Echelons</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_ECHELON() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_ECHELON(), process.getVAL_LB_ECHELON_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ECHELON()%>">
								<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ECHELON()%>">
			        		</div>
			            	
			            	<% if (process.getVAL_ST_ACTION_ECHELON()!= null && !process.getVAL_ST_ACTION_ECHELON().equals("")) {%>
			            	<br>
				            <table width="400px">
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="3" name="<%= process.getNOM_EF_CODE_ECHELON() %>" size="3" type="text" value="<%= process.getVAL_EF_CODE_ECHELON() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisie" maxlength="60" name="<%= process.getNOM_EF_LIBELLE_ECHELON() %>" size="35" type="text" value="<%= process.getVAL_EF_LIBELLE_ECHELON() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_ECHELON())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_ECHELON()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_ECHELON()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ECHELON()%>">						
					            		</td>
					            	</tr>	
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Barêmes</legend>
							<span style="margin-left: 5px;">IBA</span>
							<span style="margin-left: 70px;">INA</span>
							<span style="margin-left: 65px;">INM</span>
							
							<br/>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_BAREME() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_BAREME(), process.getVAL_LB_BAREME_SELECT()) %>
							</SELECT>
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_BAREME()%>">
			        	    	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_BAREME()%>">
			        	    </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_BAREME()!= null && !process.getVAL_ST_ACTION_BAREME().equals("")) {%>
			            	<br>
				            <table width="400px">
					            	<tr>
					            		<td width="30px">
											<label class="sigp2Mandatory">IBA:</label>
					            		</td>
					            		<td>
										<% if (process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_BAREME())){ %>
											<INPUT class="sigp2-saisiemajuscule" maxlength="7" disabled="disabled" name="<%= process.getNOM_EF_IBAN_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_IBAN_BAREME() %>">
										<% } else { %>
											<INPUT class="sigp2-saisiemajuscule" maxlength="7" name="<%= process.getNOM_EF_IBAN_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_IBAN_BAREME() %>">
										<% } %>
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">INA:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_INA_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_INA_BAREME() %>">
										</td>
					            	</tr>		
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">INM:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_INM_BAREME() %>" size="7" type="text" value="<%= process.getVAL_EF_INM_BAREME() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_BAREME())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_BAREME()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_BAREME()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_BAREME()%>">						
					            		</td>
					            	</tr>	
				            </table>
				            <%}%>
						</FIELDSET>
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Grades génériques</legend>
							<span style="margin-left:0px;">Code</span>
							<span style="margin-left:25px;">Cat</span>
							<span style="margin-left:10px;">Inactif</span>
							<span style="margin-left:10px;">Libellé</span>
							
							<br/>
							<span class="sigp2-titre" align="center" colspan="2">
							<SELECT name="<%= process.getNOM_LB_GRADE_GENERIQUE() %>" size="10"
								style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_GRADE_GENERIQUE(), process.getVAL_LB_GRADE_GENERIQUE_SELECT()) %>
							</SELECT>
							
							
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_GRADE_GENERIQUE()%>">
			        	    	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_GRADE_GENERIQUE()%>">
			        	    </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_GRADE_GENERIQUE()!= null && !process.getVAL_ST_ACTION_GRADE_GENERIQUE().equals("")) {%>
			            	<br>
				            <table width="400px">
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="4" <%= process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_GRADE_GENERIQUE()) ? "disabled='disabled'" : Const.CHAINE_VIDE %> name="<%= process.getNOM_EF_CODE_GRADE_GENERIQUE() %>" size="4" type="text" value="<%= process.getVAL_EF_CODE_GRADE_GENERIQUE() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50"	  name="<%= process.getNOM_EF_LIBELLE_GRADE_GENERIQUE() %>" size="50" type="text" value="<%= process.getVAL_EF_LIBELLE_GRADE_GENERIQUE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Catégorie:</label>
					            		</td>
					            		<td>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CATEGORIE() %>">
													<%=process.forComboHTML(process.getVAL_LB_CATEGORIE(), process.getVAL_LB_CATEGORIE_SELECT()) %>
											</SELECT>
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Cadre emploi:</label>
					            		</td>
					            		<td>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CADRE_EMPLOI_GRADE() %>">
													<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI_GRADE(), process.getVAL_LB_CADRE_EMPLOI_GRADE_SELECT()) %>
											</SELECT>
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Inactif:</label>
					            		</td>
					            		<td>
											<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_INACTIF(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">oui</span>
											<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_INACTIF(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">non</span>
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Nb points:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_NB_PTS_CATEGORIE() %>" size="4" type="text" value="<%= process.getVAL_EF_NB_PTS_CATEGORIE() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Filière:</label>
					            		</td>
					            		<td>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FILIERE_GRADE() %>">
													<%=process.forComboHTML(process.getVAL_LB_FILIERE_GRADE(), process.getVAL_LB_FILIERE_GRADE_SELECT()) %>
											</SELECT>
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Texte CAP cadre emploi:</label>
					            		</td>
					            		<td>
											<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TEXTE_CAP_GRADE_GENERIQUE() %>" size="50" type="text" value="<%= process.getVAL_EF_TEXTE_CAP_GRADE_GENERIQUE() %>">	
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Déliberation territoriale:</label>
					            		</td>
					            		<td>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DELIB_TERR_GRADE() %>">
													<%=process.forComboHTML(process.getVAL_LB_DELIB_TERR_GRADE(), process.getVAL_LB_DELIB_TERR_GRADE_SELECT()) %>
											</SELECT>
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Déliberation communale:</label>
					            		</td>
					            		<td>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DELIB_COMM_GRADE() %>">
													<%=process.forComboHTML(process.getVAL_LB_DELIB_COMM_GRADE(), process.getVAL_LB_DELIB_COMM_GRADE_SELECT()) %>
											</SELECT>	
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_GRADE_GENERIQUE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_GRADE_GENERIQUE()%>">	
											<% }else{%>
												<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_GRADE_GENERIQUE()%>">
											<%} %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_GRADE_GENERIQUE()%>">						
					            		</td>
					            	</tr>	
				            </table>
				            <%}%>			
						</FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">			
					    	<legend class="sigp2Legend">Cadre emploi</legend>
							<span style="position:relative;width:290px;">Libellé</span>				
							<br/>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_CADRE_EMPLOI() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI(), process.getVAL_LB_CADRE_EMPLOI_SELECT()) %>
							</SELECT>
			            	</span>            	
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_CADRE_EMPLOI()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_CADRE_EMPLOI()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_EF_ACTION_CADRE_EMPLOI()!= null && !process.getVAL_EF_ACTION_CADRE_EMPLOI().equals("")) {%>
			            	<br>
				            <table width="400px">
				            
								<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_EF_ACTION_CADRE_EMPLOI())) { %>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CADRE_EMPLOI() %>" size="35" type="text" value="<%= process.getVAL_EF_CADRE_EMPLOI() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_EF_ACTION_CADRE_EMPLOI())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_CADRE_EMPLOI()%>">	
											<% }%>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CADRE_EMPLOI()%>">						
					            		</td>
					            	</tr>	
					            <% }else{%>
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT disabled="disabled" class="sigp2-saisiemajuscule" maxlength="100" name="<%= process.getNOM_EF_CADRE_EMPLOI() %>" size="35" type="text" value="<%= process.getVAL_EF_CADRE_EMPLOI() %>">
										</td>
					            	</tr>	
									<tr>
					            		<td colspan="2" align="center">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CADRE_EMPLOI()%>">
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CADRE_EMPLOI()%>">						
					            		</td>
					            	</tr>	
					            	
					            <%} %>
				            </table>
				            <%}%>		
						</FIELDSET>		
					</td>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Filières</legend>
							<span style="margin-left:0px;">Code</span>
							<span style="margin-left:10px;">Libellé</span>
							
							<br/>
							<span class="sigp2-titre" align="center" colspan="2">
							<SELECT name="<%= process.getNOM_LB_FILIERE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_FILIERE(), process.getVAL_LB_FILIERE_SELECT()) %>
							</SELECT>
							
			            	</span>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_FILIERE()%>">
			        	    	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_FILIERE()%>">
			        	    </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_FILIERE() != null && !process.getVAL_ST_ACTION_FILIERE().equals("")) {%>
			            	<br>
				            <table width="400px">
					            	<tr>
					            		<td width="50px">
											<label class="sigp2Mandatory">Code:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="4" <%= process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_FILIERE()) ? "disabled='disabled'" : Const.CHAINE_VIDE %> name="<%= process.getNOM_EF_CODE_FILIERE() %>" size="4" type="text" value="<%= process.getVAL_EF_CODE_FILIERE() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Libellé:</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="50"	  name="<%= process.getNOM_EF_LIBELLE_FILIERE() %>" size="50" type="text" value="<%= process.getVAL_EF_LIBELLE_FILIERE() %>">
										</td>
					            	</tr>
									<tr>
					            		<td colspan="2" align="center">
											<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_FILIERE())) { %>
					            				<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_FILIERE()%>">
											<% } else { %>
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_FILIERE()%>">
											<% } %>
											<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_FILIERE()%>">						
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