<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.parametrage.AccueilKiosque"%>
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres du kiosque</TITLE>
		
        <script src="ckeditor/ckeditor.js"></script>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
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
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEKiosque" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
		
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<table width="1030px;">
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Référent RH global</legend>
							<span class="sigp2Mandatory">Ici, il faut saisir le référent RH qui'il faut contacter si il n'y a pas de referent RH associé au service de l'agent.</span>
							<br/><br/>
							<span class="sigp2Mandatory">Agent : </span>
					    	<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_ID_REFERENT_RH_GLOBAL() %>" size="4" type="text" value="<%= process.getVAL_EF_ID_REFERENT_RH_GLOBAL() %>">
							<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_GLOBAL()%>');">
				            <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL()%>');">
                			<span class="sigp2Mandatory">Téléphone : </span>
							<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_NUMERO_TELEPHONE_GLOBAL() %>" size="4" type="text" value="<%= process.getVAL_EF_NUMERO_TELEPHONE_GLOBAL() %>">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_REFERENT_RH_GLOBAL()%>">
						</FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Référents RH</legend>
							<span class="sigp2-saisie" style="margin-left: 5px;">Référent</span>
							<SELECT name="<%= process.getNOM_LB_REFERENT_RH() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_REFERENT_RH(), process.getVAL_LB_REFERENT_RH_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_REFERENT_RH()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_REFERENT_RH()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_REFERENT_RH()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_REFERENT_RH()!= null && !process.getVAL_ST_ACTION_REFERENT_RH().equals(Const.CHAINE_VIDE)) {%>				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_REFERENT_RH())) { %>
							<table width="100%">
								<tr>
									<td>								
										<FIELDSET class="sigp2Fieldset" style="text-align:left;">
										    <legend class="sigp2Legend">Services associés</legend>
										    <table class="sigp2Mandatory">
												<tr>
													<td width="45%">
														<span class="sigp2Mandatory">Agent : </span>
														<%if(process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_REFERENT_RH())){ %>
															<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_ID_REFERENT_RH() %>" size="4" type="text" value="<%= process.getVAL_EF_ID_REFERENT_RH() %>">
															<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
				                							<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
                										<%}else{ %>
															<label class="sigp2-saisie" ><%= process.getVAL_EF_NOM_REFERENT_RH() %></label>
														<%} %>
													</td>
													<td width="10%">&nbsp;</td>
													<td width="45%">
														<span class="sigp2Mandatory">Téléphone : </span>
														<INPUT class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_NUMERO_TELEPHONE() %>" size="4" type="text" value="<%= process.getVAL_EF_NUMERO_TELEPHONE() %>">
													</td>
												</tr>
										    	<tr>
										    		<td>
												    	<span style="margin-left:5px;">Services disponibles</span>
												    	<BR/>
												    	<INPUT type="hidden" id="service" size="4" name="<%=process.getNOM_EF_SERVICE() %>" value="<%=process.getVAL_EF_SERVICE() %>" class="sigp2-saisie">
														<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_EF_ID_SERVICE_ADS() %>" value="<%=process.getVAL_EF_ID_SERVICE_ADS() %>" class="sigp2-saisie">
														
														<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
														<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
														<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
														
										    		</td>
										    		<td align="center">
														<INPUT title="Ajouter le service" type="image" src="images/fleche-droite.png" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_SERVICE()%>">
														<BR/>
														<INPUT title="Ajouter le service et ses sous-services" type="image" src="images/fleche-double-droite.png" height="20px" width="20px" name="<%=process.getNOM_PB_AJOUTER_TOUT()%>">
														<BR/><BR/>
														<INPUT title="Retirer le service" type="image" src="images/fleche-gauche.png" height="20px" width="20px" name="<%=process.getNOM_PB_RETIRER_SERVICE()%>">
														<BR/>
														<INPUT title="Retirer le service et ses sous-services" type="image" src="images/fleche-double-gauche.png" height="20px" width="20px" name="<%=process.getNOM_PB_RETIRER_TOUT()%>">
										    		</td>
										    		<td>
												    	<span style="margin-left:5px;">Services de l'utilisateur</span>
												    	<BR/>
														<SELECT class="sigp2-liste" style="height: 340px; width: 300px;" name="<%= process.getNOM_LB_SERVICE_UTILISATEUR() %>" size="10">
															<%=process.forComboHTML(process.getVAL_LB_SERVICE_UTILISATEUR(), process.getVAL_LB_SERVICE_UTILISATEUR_SELECT()) %>
														</SELECT>
										    		</td>
										    	</tr>
										    </table>
										</FIELDSET>
									</td>
								</tr>
								<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION_REFERENT_RH())||process.ACTION_MODIFICATION.equals(process.getVAL_ST_ACTION_REFERENT_RH())) { %>
								<tr>
									<td align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_REFERENT_RH()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_REFERENT_RH()%>">
									</td>
								</tr>
								<%} %>
							</table>
							<%} else {%>
							<table>
								<tr>
									<td width="40px;">
										<label class="sigp2Mandatory">Agent :</label>
									</td>
									<td>
										<label class="sigp2-saisie" ><%= process.getVAL_EF_NOM_REFERENT_RH() %></label>
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Téléphone :</label>
									</td>
									<td>
										<label class="sigp2-saisie" ><%= process.getVAL_EF_NUMERO_TELEPHONE() %></label>
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_REFERENT_RH()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_REFERENT_RH()%>">
									</td>
								</tr>
							</table>
						    <%}%>
							<% } %>
						</FIELDSET>	
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Accueil du kiosque</legend>
					    	<span class="sigp2-saisie" style="margin-left: 5px;">Texte</span>
							<SELECT name="<%= process.getNOM_LB_TEXTE_KIOSQUE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_TEXTE_KIOSQUE(), process.getVAL_LB_TEXTE_KIOSQUE_SELECT()) %>
							</SELECT>
										
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_TEXTE_KIOSQUE()%>">
			    	        	<INPUT type="image" src="images/suppression.gif" height="20px" width="20px" name="<%=process.getNOM_PB_SUPPRIMER_TEXTE_KIOSQUE()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_TEXTE_KIOSQUE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_TEXTE_KIOSQUE()!= null && !process.getVAL_ST_ACTION_TEXTE_KIOSQUE().equals(Const.CHAINE_VIDE)) {%>				            
							<% if (!process.ACTION_SUPPRESSION.equals(process.getVAL_ST_ACTION_TEXTE_KIOSQUE())) { %>
							<table>
								<tr>
									<td>
										<label class="sigp2Mandatory">Titre :</label>
										<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TITRE_ACCUEIL_KIOSQUE() %>" size="80" type="text" value="<%= process.getVAL_EF_TITRE_ACCUEIL_KIOSQUE() %>">
									</td>
								</tr>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Texte :</label>
										<br>
										<textarea cols="100" rows="10" id="<%=process.getNOM_EF_TEXTE_KIOSQUE()%>" name="<%=process.getNOM_EF_TEXTE_KIOSQUE()%>" title="Zone de saisie du texte d'accueil"><%=process.getVAL_EF_TEXTE_KIOSQUE().trim() %></textarea>
										 <script type="text/javascript">
							                CKEDITOR.replace( '<%=process.getNOM_EF_TEXTE_KIOSQUE()%>', {
							                    language: 'fr'
							                });
							            </script>
									</td>
								</tr>
								<tr>
									<td align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_TEXTE_KIOSQUE()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TEXTE_KIOSQUE()%>">
									</td>
								</tr>
							</table>
							<%} else {%>
							<table>
								<tr>
									<td>
										<label class="sigp2Mandatory">Titre :</label>
										<br>
										<label class="sigp2-saisie" ><%= process.getVAL_EF_TITRE_ACCUEIL_KIOSQUE() %></label>
									</td>
								</tr>
								<tr>
									<td>
										<label class="sigp2Mandatory">Texte :</label>
										<br>
										<label class="sigp2-saisie" ><%= process.getVAL_EF_TEXTE_KIOSQUE() %></label>
									</td>
								</tr>
								<tr>
									<td align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_TEXTE_KIOSQUE()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_TEXTE_KIOSQUE()%>">
									</td>
								</tr>
							</table>
						    <%}%>
							<% } %>
					    </FIELDSET>
					</td>
				</tr>
				<tr>
					<td>
						<FIELDSET class="sigp2Fieldset"  style="text-align: left;">
					    	<legend class="sigp2Legend">Alertes du kiosque</legend>
					    	<span class="sigp2-saisie" style="margin-left: 5px;">Texte</span>
							<SELECT name="<%= process.getNOM_LB_ALERTE_KIOSQUE() %>" size="10" style="width:100%;" class="sigp2-liste">
								<%=process.forComboHTML(process.getVAL_LB_ALERTE_KIOSQUE(), process.getVAL_LB_ALERTE_KIOSQUE_SELECT()) %>
							</SELECT>
			
							<div class=<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>>
								<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ALERTE_KIOSQUE()%>">
			    	        	<INPUT type="image" src="images/modifier.gif" height="20px" width="20px" name="<%=process.getNOM_PB_MODIFIER_ALERTE_KIOSQUE()%>">
			    	        </div>
			            	
			            	<% if (process.getVAL_ST_ACTION_ALERTE_KIOSQUE()!= null && !process.getVAL_ST_ACTION_ALERTE_KIOSQUE().equals(Const.CHAINE_VIDE)) {%>		
							<table>
								<tr>
									<td>
										<label class="sigp2Mandatory">Titre :</label>
										<INPUT class="sigp2-saisie" maxlength="255" name="<%= process.getNOM_EF_TITRE_ALERTE_KIOSQUE() %>" size="80" type="text" value="<%= process.getVAL_EF_TITRE_ALERTE_KIOSQUE() %>">
									</td>
								</tr>
								<tr>
									<td width="50px;">
										<label class="sigp2Mandatory">Texte :</label>
										<br>
										<textarea cols="100" rows="10" id="<%=process.getNOM_EF_ALERTE_KIOSQUE()%>" name="<%=process.getNOM_EF_ALERTE_KIOSQUE()%>" title="Zone de saisie du texte d'alerte"><%=process.getVAL_EF_ALERTE_KIOSQUE().trim() %></textarea>
										 <script type="text/javascript">
							                CKEDITOR.replace( '<%=process.getNOM_EF_ALERTE_KIOSQUE()%>', {
							                    language: 'fr'
							                });
							            </script>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2-saisie">Destinataires de l'alerte : </span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AGENT(),process.getVAL_CK_AGENT())%> ><span class="sigp2-saisie">Agent</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_APPRO_ABS(),process.getVAL_CK_APPRO_ABS())%> ><span class="sigp2-saisie">Appro ABS</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_APPRO_PTG(),process.getVAL_CK_APPRO_PTG())%> ><span class="sigp2-saisie">Appro PTG</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_OPE_ABS(),process.getVAL_CK_OPE_ABS())%> ><span class="sigp2-saisie">Opé ABS</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_OPE_PTG(),process.getVAL_CK_OPE_PTG())%> ><span class="sigp2-saisie">Opé PTG</span>
										<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VISEUR_ABS(),process.getVAL_CK_VISEUR_ABS())%> ><span class="sigp2-saisie">Viseur ABS</span>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory">Date de début :</span>
										<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">
										<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
										<span class="sigp2Mandatory">Date de fin :</span>	
										<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_FIN() %>">
										<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');">						
									</td>
								</tr>
								<tr>
									<td align="center">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_ALERTE_KIOSQUE()%>">
										<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ALERTE_KIOSQUE()%>">
									</td>
								</tr>
							</table>							
							<% } %>
					    </FIELDSET>
					</td>
				</tr>
			</table>
			<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENT">
			<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_GLOBAL()%>" value="RECHERCHERAGENTGLOBAL">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_GLOBAL()%>" value="SUPPRECHERCHERAGENTGLOBAL">
		</FORM>
	</BODY>
</HTML>