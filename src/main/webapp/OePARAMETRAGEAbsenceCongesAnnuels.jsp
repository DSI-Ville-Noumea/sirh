<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.Date"%>
<%@page import="nc.mairie.gestionagent.absence.dto.RefAlimCongesAnnuelsDto"%>
<%@page import="nc.mairie.gestionagent.absence.dto.RefTypeSaisiCongeAnnuelDto"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
		<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des paramètres des absences</TITLE>


        <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
        <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
        <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
            
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
		
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#CongesAnnuels').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSortable":false,"bSearchable":false},null,null,null],
							"sDom": '<"H"fl>t<"F"i>',
							"sScrollY": "180px",
							"aaSorting": [[ 1, "asc" ]],
							"bPaginate": false
					    });
					} );
				</script>
		
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#refAlim').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSortable":false,"bSearchable":false},null,null,null,null,null,null,null,null,null,null,null,null,null],
							"sDom": '<"H"l>t<"F"i>',
							"sScrollY": "100px",
							"bPaginate": false
					    });
					} );
				</script>
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsenceCongesAnnuels" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Gestion des congés annuels</legend>
							<table cellpadding="0" cellspacing="0" border="0" class="display" id="CongesAnnuels"> 
			                    <thead>
			                        <tr>
			                            <th width="100px">&nbsp;</th> 
			                            <th>Code</th>
			                            <th>Description</th>
			                            <th>Quota Multiple</th>	                            
			                        </tr>
			                    </thead>
			                    <tbody>
			                        <%for (RefTypeSaisiCongeAnnuelDto abs : process.getListeTypeAbsence()) {
			                        	int indiceAbs = abs.getIdRefTypeSaisiCongeAnnuel();
			                        %>
			                        <tr>
			                            <td align="center">
			                            	<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAbs)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAbs)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_CONGES(indiceAbs)%>">
                           					<INPUT title="Voir l'alimentaion manuelle" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_ALIM_MENSUELLE(indiceAbs)%>">
											<INPUT title="Gérer l'alimentaion manuelle" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_ALIM_MENSUELLE(indiceAbs)%>">
											</td>                            
			                            <td><%=process.getVAL_ST_CODE_CONGE(indiceAbs)%></td> 
			                            <td><%=process.getVAL_ST_DESCRIPTION(indiceAbs)%></td> 
			                            <td align="center"><%=process.getVAL_ST_QUOTA_MULTIPLE(indiceAbs)%></td>  
			                        </tr>
			                        <%}%>
			                    </tbody>
			                </table>
						</FIELDSET>	
						
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
	            <legend class="sigp2Legend"><%=process.ACTION_VISUALISATION %> <%=process.getTypeAbsenceCourant().getCodeBaseHoraireAbsence() %></legend>
		            <table>
		            	<tr>
		            		<td colspan="2">
								<span class="sigp2">Description : </span>
								<BR/>
								<span class="sigp2-saisie"><%=process.getVAL_ST_DESCRIPTION()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td colspan="2">
								<span class="sigp2">Quota Multiple : </span>
								<span class="sigp2-saisie"><%=process.getVAL_ST_QUOTA_MULTIPLE()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Date debut : </span>
		            			<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_DEBUT(),process.getNOM_RB_DATE_DEBUT_OUI())%>><span class="sigp2-saisie">Oui</span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Demi-journée : </span>
	 							<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM(),process.getNOM_RB_AM_PM_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM(),process.getNOM_RB_AM_PM_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Date fin : </span>
		            			<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_FIN(),process.getNOM_RB_DATE_FIN_OUI())%>><span class="sigp2-saisie">Oui</span>
		            			<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_FIN(),process.getNOM_RB_DATE_FIN_NON())%>><span class="sigp2-saisie">Non</span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Date reprise : </span>
		            			<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_REPRISE(),process.getNOM_RB_DATE_REPRISE_OUI())%>><span class="sigp2-saisie">Oui</span>
		            			<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_REPRISE(),process.getNOM_RB_DATE_REPRISE_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Décompte samedi : </span>
	 							<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DECOMPTE_SAMEDI(),process.getNOM_RB_DECOMPTE_SAMEDI_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DECOMPTE_SAMEDI(),process.getNOM_RB_DECOMPTE_SAMEDI_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Consécutif : </span>
		                		<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONSECUTIF(),process.getNOM_RB_CONSECUTIF_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONSECUTIF(),process.getNOM_RB_CONSECUTIF_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            </table>
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>
	            </FIELDSET>
            <%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
	            <legend class="sigp2Legend"><%=process.ACTION_MODIFICATION %> <%=process.getTypeAbsenceCourant().getCodeBaseHoraireAbsence()%></legend>				
		            <table>
		            	<tr>
		            		<td colspan="2">
								<span class="sigp2">Description : </span>
								<BR/>
		            			<textarea rows="3" style="width:800px" name="<%= process.getNOM_ST_DESCRIPTION() %>" ><%= process.getVAL_ST_DESCRIPTION() %></textarea>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td colspan="2">
								<span class="sigp2">Quota Multiple : </span>
								<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_QUOTA_MULTIPLE() %>" type="text"  value="<%= process.getVAL_ST_QUOTA_MULTIPLE() %>">
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Date debut : </span>
		            			<INPUT disabled="disabled" type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_DEBUT(),process.getNOM_RB_DATE_DEBUT_OUI())%>><span class="sigp2-saisie">Oui</span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Demi-journée : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM(),process.getNOM_RB_AM_PM_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM(),process.getNOM_RB_AM_PM_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Date fin : </span>
		            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_FIN(),process.getNOM_RB_DATE_FIN_OUI())%>><span class="sigp2-saisie">Oui</span>
		            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_FIN(),process.getNOM_RB_DATE_FIN_NON())%>><span class="sigp2-saisie">Non</span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Date reprise : </span>
		            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_REPRISE(),process.getNOM_RB_DATE_REPRISE_OUI())%>><span class="sigp2-saisie">Oui</span>
		            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_REPRISE(),process.getNOM_RB_DATE_REPRISE_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Décompte samedi : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DECOMPTE_SAMEDI(),process.getNOM_RB_DECOMPTE_SAMEDI_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DECOMPTE_SAMEDI(),process.getNOM_RB_DECOMPTE_SAMEDI_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Consécutif : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONSECUTIF(),process.getNOM_RB_CONSECUTIF_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_CONSECUTIF(),process.getNOM_RB_CONSECUTIF_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            </table>  
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CONGES()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>          
            	</FIELDSET>  
            <%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_ALIM_MENSUELLE)){ %>
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Gestion de l'alimentation mensuelle des congés annuels de la base <%=process.getTypeAbsenceCourant().getCodeBaseHoraireAbsence() %></legend>
							<br/>
							<table cellpadding="0" cellspacing="0" border="0"  class="display" id="refAlim"> 
			                    <thead>
			                        <tr>
			                            <th width="30px">
											<INPUT type="image" src="images/ajout.gif" height="20px" width="20px" name="<%=process.getNOM_PB_CREER_ALIM_MENSUELLE()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
										</th> 
			                            <th>Année</th>     
			                            <th>Janvier</th>     
			                            <th>Février</th>     
			                            <th>Mars</th>     
			                            <th>Avril</th>     
			                            <th>Mai</th>     
			                            <th>Juin</th>     
			                            <th>Juillet</th>     
			                            <th>Aout</th>     
			                            <th>Septembre</th>     
			                            <th>Octobre</th>     
			                            <th>Novembre</th>     
			                            <th>Décembre</th>                           
			                        </tr>
			                    </thead>
			                    <tbody>
			                        <%for (RefAlimCongesAnnuelsDto alim : process.getListeAlimMensuelle()) {
			                        	int indiceAlim = alim.getAnnee();
			                        %>
			                        <tr>
			                            <td align="center">
			                            <%if(alim.getAnnee()>= new Date().getYear()){ %>
			                            	<INPUT title="modifier" type="image" src="images/modifier.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_ALIM_MENSUELLE(indiceAlim)%>">
			                            <%} %>
                           				</td>                            
			                            <td align="center"><%=process.getVAL_ST_ANNEE_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_JANVIER_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_FEVRIER_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_MARS_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_AVRIL_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_MAI_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_JUIN_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_JUILLET_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_AOUT_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_SEPTEMBRE_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_OCTOBRE_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_NOVEMBRE_ALIM(indiceAlim)%></td>  
			                            <td align="center"><%=process.getVAL_ST_DECEMBRE_ALIM(indiceAlim)%></td>  
			                        </tr>
			                        <%}%>
			                    </tbody>
			                </table>
			                <%if(process.getVAL_ST_ACTION_ALIM_MANUELLE().equals(process.ACTION_MODIF_ALIM_MENSUELLE)){ %>
								<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    			<legend class="sigp2Legend">Modification de l'alimentation mensuelle des congés annuels de la base <%=process.getTypeAbsenceCourant().getCodeBaseHoraireAbsence() %></legend>
					    			<table>
										<tr>
											<td colspan="3">
													<label class="sigp2Mandatory" style="color: red;">Les jours sont à saisir sous la forme "0.0"</label>
											</td>
										</tr>
					            		<tr>
					            			<td colspan="2">
												<label class="sigp2Mandatory">Année :</label>
					            			</td>
					            			<td>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" maxlength="4" name="<%= process.getNOM_EF_ANNEE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_ANNEE_ALIM() %>">
					            			</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Janvier :</label>
					            			</td>
					            			<td>
					            			<%if(new Date().getMonth()>=1 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_JANVIER_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JANVIER_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_JANVIER_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JANVIER_ALIM() %>">
					            			<%} %>
											</td>
					            			<td>
												<label class="sigp2Mandatory">Février :</label>
					            			</td>
					            			<td>
					            			<%if(new Date().getMonth()>=2 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled"  name="<%= process.getNOM_EF_FEVRIER_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_FEVRIER_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_FEVRIER_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_FEVRIER_ALIM() %>">
					            			<%} %>
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Mars :</label>
					            			</td>
					            			<td>
					            			<%if(new Date().getMonth()>=3 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled"  name="<%= process.getNOM_EF_MARS_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_MARS_ALIM() %>">
											<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule"   name="<%= process.getNOM_EF_MARS_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_MARS_ALIM() %>">
											<%} %>
					            			</td>
					            			<td>
												<label class="sigp2Mandatory">Avril :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=4 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled"  name="<%= process.getNOM_EF_AVRIL_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_AVRIL_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_AVRIL_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_AVRIL_ALIM() %>">
					            			<%} %>
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Mai :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=5 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_MAI_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_MAI_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_MAI_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_MAI_ALIM() %>">
					            			<%} %>
											</td>
					            			<td>
												<label class="sigp2Mandatory">Juin :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=6 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_JUIN_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JUIN_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_JUIN_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JUIN_ALIM() %>">
					            			<%} %>
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Juillet :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=7 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_JUILLET_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JUILLET_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_JUILLET_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JUILLET_ALIM() %>">
					            			<%} %>
											</td>
					            			<td>
												<label class="sigp2Mandatory">Aout :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=8 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled=disabled" name="<%= process.getNOM_EF_AOUT_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_AOUT_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_AOUT_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_AOUT_ALIM() %>">
					            			<%} %>
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Septembre :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=9 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_SEPTEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_SEPTEMBRE_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_SEPTEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_SEPTEMBRE_ALIM() %>">
					            			<%} %>
											</td>
					            			<td>
												<label class="sigp2Mandatory">Octobre :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=10 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_OCTOBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_OCTOBRE_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_OCTOBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_OCTOBRE_ALIM() %>">
					            			<%} %>
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Novembre :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=11 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_NOVEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_NOVEMBRE_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_NOVEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_NOVEMBRE_ALIM() %>">
					            			<%} %>
											</td>
					            			<td>
												<label class="sigp2Mandatory">Décembre :</label>
					            			</td>
					            			<td>												
					            			<%if(new Date().getMonth()>=12 ){ %>
												<INPUT class="sigp2-saisiemajuscule" disabled="disabled" name="<%= process.getNOM_EF_DECEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_DECEMBRE_ALIM() %>">
					            			<%}else{ %>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_DECEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_DECEMBRE_ALIM() %>">
					            			<%} %>
											</td>
					            		</tr>
					            		<tr>
					            			<td colspan="4" align="center">
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_ALIM_MENSUELLE()%>">
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ALIM_MENSUELLE()%>">
					            			</td>
					            		</tr>
					            	</table>
					    		</FIELDSET>
			                <%}else if(process.getVAL_ST_ACTION_ALIM_MANUELLE().equals(process.ACTION_CREATION_ALIM_MENSUELLE)){ %>
								<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    			<legend class="sigp2Legend">Création de l'alimentation mensuelle des congés annuels de la base <%=process.getTypeAbsenceCourant().getCodeBaseHoraireAbsence() %></legend>
					    			<table>
										<tr>
											<td colspan="3">
													<label class="sigp2Mandatory" style="color: red;">Les jours sont à saisir sous la forme "0.0"</label>
											</td>
										</tr>
					            		<tr>
					            			<td colspan="2">
												<label class="sigp2Mandatory">Année :</label>
					            			</td>
					            			<td>
												<INPUT class="sigp2-saisiemajuscule" maxlength="4" name="<%= process.getNOM_EF_ANNEE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_ANNEE_ALIM() %>">
					            			</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Janvier :</label>
					            			</td>
					            			<td>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_JANVIER_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JANVIER_ALIM() %>">
					            			</td>
					            			<td>
												<label class="sigp2Mandatory">Février :</label>
					            			</td>
					            			<td>
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_FEVRIER_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_FEVRIER_ALIM() %>">
					            			</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Mars :</label>
					            			</td>
					            			<td>
					            				<INPUT class="sigp2-saisiemajuscule"   name="<%= process.getNOM_EF_MARS_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_MARS_ALIM() %>">
					            			</td>
					            			<td>
												<label class="sigp2Mandatory">Avril :</label>
					            			</td>
					            			<td>	
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_AVRIL_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_AVRIL_ALIM() %>">
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Mai :</label>
					            			</td>
					            			<td>		
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_MAI_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_MAI_ALIM() %>">
											</td>
					            			<td>
												<label class="sigp2Mandatory">Juin :</label>
					            			</td>
					            			<td>		
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_JUIN_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JUIN_ALIM() %>">
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Juillet :</label>
					            			</td>
					            			<td>			
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_JUILLET_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_JUILLET_ALIM() %>">
											</td>
					            			<td>
												<label class="sigp2Mandatory">Aout :</label>
					            			</td>
					            			<td>			
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_AOUT_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_AOUT_ALIM() %>">
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Septembre :</label>
					            			</td>
					            			<td>			
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_SEPTEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_SEPTEMBRE_ALIM() %>">
											</td>
					            			<td>
												<label class="sigp2Mandatory">Octobre :</label>
					            			</td>
					            			<td>			
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_OCTOBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_OCTOBRE_ALIM() %>">
											</td>
					            		</tr>
					            		<tr>
					            			<td>
												<label class="sigp2Mandatory">Novembre :</label>
					            			</td>
					            			<td>			
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_NOVEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_NOVEMBRE_ALIM() %>">
											</td>
					            			<td>
												<label class="sigp2Mandatory">Décembre :</label>
					            			</td>
					            			<td>				
												<INPUT class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_DECEMBRE_ALIM() %>" size="5" type="text" value="<%= process.getVAL_EF_DECEMBRE_ALIM() %>">
											</td>
					            		</tr>
					            		<tr>
					            			<td colspan="4" align="center">
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_ALIM_MENSUELLE()%>">
					            			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_ALIM_MENSUELLE()%>">
					            			</td>
					            		</tr>
					            	</table>
					    		</FIELDSET>
			                <%} %>
			                
						</FIELDSET>	
            <%} %>
		</FORM>
	</BODY>
</HTML>