<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeGroupeAbsence"%>
<%@page import="nc.mairie.gestionagent.absence.dto.TypeAbsenceDto"%>
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
					    $('#CongesExcep').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSortable":false,"bSearchable":false},null,null,null],
							"sDom": '<"H"fl>t<"F"i>',
							"sScrollY": "375px",
							"aaSorting": [[ 1, "asc" ]],
							"bPaginate": false
					    });
					} );
				</script>
		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsenceConges" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Gestion des congés exceptionnels</legend>
							<table cellpadding="0" cellspacing="0" border="0" class="display" id="CongesExcep"> 
			                    <thead>
			                        <tr>
			                            <th width="80px">
			                            	<img src="images/ajout.gif" height="16px" width="16px" title="Creer une absence" onClick="executeBouton('<%=process.getNOM_PB_AJOUTER_CONGES()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
			            				</th>  
			                            <th>Type</th>
			                            <th width="80px">Unite de décompte</th>	
			                            <th>Info</th>	                            
			                        </tr>
			                    </thead>
			                    <tbody>
			                        <%for (TypeAbsenceDto abs : process.getListeTypeAbsence()) {
			                			if (abs.getGroupeAbsence() == null
			                					|| abs.getGroupeAbsence().getIdRefGroupeAbsence() != EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
			                				continue;
			                			}
			                        	int indiceAbs = abs.getIdRefTypeAbsence();
			                        %>
			                        <tr>
			                            <td align="center">
			                            	<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAbs)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAbs)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_CONGES(indiceAbs)%>">
                           					<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceAbs)%>">
				    					</td>                            
			                            <td><%=process.getVAL_ST_TYPE_CONGE(indiceAbs)%></td> 
			                            <td align="center"><%=process.getVAL_ST_UNITE(indiceAbs)%></td>  
			                            <td><%=process.getVAL_ST_INFO(indiceAbs)%></td>  
			                        </tr>
			                        <%}%>
			                    </tbody>
			                </table>
						</FIELDSET>	
						
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
	            <legend class="sigp2Legend"><%=process.ACTION_VISUALISATION %> <%=process.getTypeCreation().getLibelle() %></legend>
		            <table>
		            	<tr>
		            		<td width="500px">
	                			<span class="sigp2Mandatory">Date debut : </span><span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DEBUT()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Date fin : </span><span class="sigp2-saisie"><%=process.getVAL_ST_DATE_FIN()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Heure debut : </span><span class="sigp2-saisie"><%=process.getVAL_ST_HEURE_DEBUT()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Heure fin : </span><span class="sigp2-saisie"><%=process.getVAL_ST_HEURE_FIN()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Demi journée debut : </span><span class="sigp2-saisie"><%=process.getVAL_ST_DEBUT_MAM()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Demi journée fin : </span><span class="sigp2-saisie"><%=process.getVAL_ST_FIN_MAM()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Pièce jointe : </span><span class="sigp2-saisie"><%=process.getVAL_ST_PIECE_JOINTE()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Saisie Kiosque : </span><span class="sigp2-saisie"><%=process.getVAL_ST_SAISIE_KIOSQUE()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Statuts : </span><span class="sigp2-saisie"><%=process.getVAL_ST_STATUT()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Description : </span><span class="sigp2-saisie"><%=process.getVAL_ST_DESCRIPTION()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Message alerte : </span><span class="sigp2-saisie"><%=process.getVAL_ST_MESSAGE_ALERTE()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Quota : </span><span class="sigp2-saisie"><%=process.getVAL_ST_QUOTA()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Motif : </span><span class="sigp2-saisie"><%=process.getVAL_ST_MOTIF()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Desc motif : </span><span class="sigp2-saisie"><%=process.getVAL_ST_INFO_COMPL()%></span>
		            		</td>
		            	</tr>
		            </table>
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>
	            </FIELDSET>
            <%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION) || process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
				<%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){%>
	            	<legend class="sigp2Legend"><%=process.ACTION_MODIFICATION %> <%=process.getTypeCreation().getLibelle() %></legend>
				<%}else{ %>
	            	<legend class="sigp2Legend"><%=process.ACTION_CREATION %> </legend>
					<span class="sigp2Mandatory">Libellé : </span>
					<INPUT class="sigp2-saisie" size="150" name="<%= process.getNOM_ST_LIBELLE() %>" type="text"  value="<%= process.getVAL_ST_LIBELLE() %>">
					<BR/>
				<%} %>
		            <table>
		            	<tr>
		            		<td width="500px">
	                			<span class="sigp2Mandatory">Unité de décompte : </span>
								<span class="sigp2-saisie">
									<SELECT style="width: 90px;" onchange='executeBouton("<%=process.getNOM_PB_UNITE_DECOMPTE() %>")'  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> class="sigp2-saisie" name="<%= process.getNOM_LB_UNITE_DECOMPTE() %>">
										<%=process.forComboHTML(process.getVAL_LB_UNITE_DECOMPTE(), process.getVAL_LB_UNITE_DECOMPTE_SELECT())%>
									</SELECT>
								</span>
		            		</td>
		            		<td>&nbsp;</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Date debut : </span>
 								<INPUT type="radio" checked="checked" disabled="disabled"><span class="sigp2-saisie">Oui</span>			
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Date fin : </span><span class="sigp2-saisie"><%=process.getVAL_ST_DATE_FIN()%></span>
		            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_FIN(),process.getNOM_RB_DATE_FIN_OUI())%>><span class="sigp2-saisie">Oui</span>
		            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_FIN(),process.getNOM_RB_DATE_FIN_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Heure debut : </span>
 								<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_HEURE_DEBUT(),process.getNOM_RB_HEURE_DEBUT_OUI())%>><span class="sigp2-saisie">Oui</span>
		            			<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_HEURE_DEBUT(),process.getNOM_RB_HEURE_DEBUT_NON())%>><span class="sigp2-saisie">Non</span>
 							</td>
		            		<td>
								<span class="sigp2Mandatory">Heure fin : </span>
	                			<%if(process.getTypeCreation().getTypeSaisiDto().getUniteDecompte().equals("jours")){ %>
		                			<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_HEURE_FIN(),process.getNOM_RB_HEURE_FIN_OUI())%>><span class="sigp2-saisie">Oui</span>
			            			<INPUT type="radio" disabled="disabled" <%= process.forRadioHTML(process.getNOM_RG_HEURE_FIN(),process.getNOM_RB_HEURE_FIN_NON())%>><span class="sigp2-saisie">Non</span>
								<%}else { %>
	 								<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_HEURE_FIN(),process.getNOM_RB_HEURE_FIN_OUI())%>><span class="sigp2-saisie">Oui</span>
			            			<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_HEURE_FIN(),process.getNOM_RB_HEURE_FIN_NON())%>><span class="sigp2-saisie">Non</span>
								<%} %>
		            		</td>
		            	</tr>
	                	<%if(process.getTypeCreation().getTypeSaisiDto().getUniteDecompte().equals("jours")){ %>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Demi-journée debut : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM_DEBUT(),process.getNOM_RB_AM_PM_DEBUT_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM_DEBUT(),process.getNOM_RB_AM_PM_DEBUT_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Demi-journée fin : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM_FIN(),process.getNOM_RB_AM_PM_FIN_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AM_PM_FIN(),process.getNOM_RB_AM_PM_FIN_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<%} %>
		            	<tr>
		            		<td colspan="2">
								<span class="sigp2Mandatory">Statuts : </span>
		            			<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_STATUT_F(),process.getVAL_CK_STATUT_F())%>><span class="sigp2-saisie">Fonctionnaire</span>
		            			<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_STATUT_CC(),process.getVAL_CK_STATUT_CC())%>><span class="sigp2-saisie">Conventions</span>
		            			<INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_STATUT_C(),process.getVAL_CK_STATUT_C())%>><span class="sigp2-saisie">Contractuels</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Pièce jointe : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_PIECE_JOINTE(),process.getNOM_RB_PIECE_JOINTE_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_PIECE_JOINTE(),process.getNOM_RB_PIECE_JOINTE_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Saisie Kiosque : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SAISIE_KIOSQUE(),process.getNOM_RB_SAISIE_KIOSQUE_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SAISIE_KIOSQUE(),process.getNOM_RB_SAISIE_KIOSQUE_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td colspan="2">
								<span class="sigp2Mandatory">Description : </span>
								<BR/>
		            			<textarea rows="3" style="width:800px" name="<%= process.getNOM_ST_DESCRIPTION() %>" ><%= process.getVAL_ST_DESCRIPTION() %></textarea>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
								<span class="sigp2Mandatory">Motif obligatoire (pour saisie) : </span>
		            			<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_MOTIF() %>")' <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOTIF(),process.getNOM_RB_MOTIF_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_MOTIF() %>")'<%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MOTIF(),process.getNOM_RB_MOTIF_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            		<td>
			            		<%if(process.getTypeCreation().getTypeSaisiDto().isMotif()){ %>
									<span class="sigp2Mandatory">Indication motif : </span>
			            			<textarea rows="3" style="width:450px" name="<%= process.getNOM_ST_INFO_COMPL() %>" ><%= process.getVAL_ST_INFO_COMPL() %></textarea>
			            		<%} %>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
								<span class="sigp2Mandatory">Quota pour calcul dépassement : </span>
								<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_QUOTA() %>" type="text"  value="<%= process.getVAL_ST_QUOTA() %>">
		            		</td>
		            		<td>
								<span class="sigp2Mandatory">Type de quota : </span>
								<SELECT style="width: 150px;" class="sigp2-saisie" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_LB_TYPE_QUOTA() %>" >
									<%=process.forComboHTML(process.getVAL_LB_TYPE_QUOTA(), process.getVAL_LB_TYPE_QUOTA_SELECT()) %>
								</SELECT>
			            	</td>
		            	</tr>
		            	<tr>
		            		<td>
								<span class="sigp2Mandatory">Alerte depassement lors saisie : </span>
		            			<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_ALERTE() %>")' <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_ALERTE(),process.getNOM_RB_ALERTE_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_ALERTE() %>")'<%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_ALERTE(),process.getNOM_RB_ALERTE_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            		<td>
			            		<%if(process.getTypeCreation().getTypeSaisiDto().isAlerte()){ %>
									<span class="sigp2Mandatory">Message alerte : </span>
			            			<textarea rows="3" style="width:450px" name="<%= process.getNOM_ST_MESSAGE_ALERTE() %>" ><%= process.getVAL_ST_MESSAGE_ALERTE() %></textarea>
			            		<%} %>
		            		</td>
		            	</tr>
		            </table>  
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CONGES()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>          
            	</FIELDSET>  
            <%} else if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){%>
            	<div>
					<FONT color='red'>Etes-vous sûr de vouloir supprimer : <%=process.getTypeCreation().getLibelle() %></FONT>
			        <BR/><BR/>
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_CONGES()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		    	</div>
            <%} %>
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_CONGES()%>" value="AJOUTERCONGES">
			<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_UNITE_DECOMPTE()%>" value="UNITEDECOMPTE">     
			<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_MOTIF()%>" value="MOTIF">   
			<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_ALERTE()%>" value="ALERTE">   
		</FORM>
	</BODY>
</HTML>