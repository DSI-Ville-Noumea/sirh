<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page contentType="text/html; charset=UTF-8" %> 
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>

<HTML>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des types de document</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGETypeDocument" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>" /> 
			<br/>
			<table width="1030px;" cellpadding="0" cellspacing="0">
				<tr>
					<td>					
				    	<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Types de documents</legend>
							<span class="sigp2-titre">
							<SELECT name="<%= process.getNOM_LB_TYPE_DOCUMENT() %>" size="10" style="width:100%;height:300px;" class="sigp2-liste">
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
					            		<td width="150px">
											<label class="sigp2Mandatory">Libellé :</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="30" name="<%= process.getNOM_EF_TYPE_DOCUMENT() %>" size="30" type="text" value="<%= process.getVAL_EF_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>	
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Code :</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" maxlength="5" name="<%= process.getNOM_EF_CODE_TYPE_DOCUMENT() %>" size="5" type="text" value="<%= process.getVAL_EF_CODE_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Module :</label>
					            		</td>
					            		<td>
							                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MODULE()%>" style="width:140px;">
							                    <%=process.forComboHTML(process.getVAL_LB_MODULE(), process.getVAL_LB_MODULE_SELECT())%>
							                </SELECT>
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Alfresco Chemin relatif sous le dossier d'un agent :</label>
					            		</td>
					            		<td>
							                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_PATH_ALFRESCO()%>" style="width:140px;">
							                    <%=process.forComboHTML(process.getVAL_LB_PATH_ALFRESCO(), process.getVAL_LB_PATH_ALFRESCO_SELECT())%>
							                </SELECT>
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
					            		<td width="150px">
											<label class="sigp2Mandatory">Libellé :</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" readonly="readonly" name="<%= process.getNOM_EF_TYPE_DOCUMENT() %>" 
					            				size="30" type="text" value="<%= process.getVAL_EF_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Code :</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" readonly="readonly" name="<%= process.getNOM_EF_CODE_TYPE_DOCUMENT() %>" 
					            				size="5" type="text" value="<%= process.getVAL_EF_CODE_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Module :</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" readonly="readonly" name="<%= process.getNOM_EF_MODULE_TYPE_DOCUMENT() %>" 
					            				size="30" type="text" value="<%= process.getVAL_EF_MODULE_TYPE_DOCUMENT() %>">
										</td>
					            	</tr>
					            	<tr>
					            		<td>
											<label class="sigp2Mandatory">Chemin Alfresco :</label>
					            		</td>
					            		<td>
					            			<INPUT class="sigp2-saisiemajuscule" readonly="readonly" name="<%= process.getNOM_EF_PATH_ALFRESCO() %>" size="30" type="text" 
					            				value="<%= process.getVAL_EF_PATH_ALFRESCO() %>">
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
			</table>
		</FORM>
	</BODY>
</HTML>