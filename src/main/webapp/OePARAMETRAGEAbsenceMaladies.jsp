<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
					    $('#maladies').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSortable":false,"bSearchable":false},null,null,null],
							"sDom": '<"H"fl>t<"F"i>',
							"sScrollY": "375px",
							"aaSorting": [[ 1, "asc" ]],
							"bPaginate": false
					    });
					} );
				</script>
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.parametre.OePARAMETRAGEAbsenceMaladies" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')" >
	<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
						<FIELDSET class="sigp2Fieldset" style="text-align: left;">
					    	<legend class="sigp2Legend">Gestion des maladies</legend>
							<table cellpadding="0" cellspacing="0" border="0" class="display" id="maladies"> 
			                    <thead>
			                        <tr>
			                            <th width="100px"></th>
			                            <th>Type</th>
			                            <th width="80px">Unite de décompte</th>	
			                            <th>Info</th>	                            
			                        </tr>
			                    </thead>
			                    <tbody>
			                        <%for (TypeAbsenceDto abs : process.getListeTypeAbsence()) {
			                			if (abs.getGroupeAbsence() == null
			                					|| abs.getGroupeAbsence().getIdRefGroupeAbsence() != EnumTypeGroupeAbsence.MALADIES.getValue()) {
			                				continue;
			                			}
			                        	int indiceAbs = abs.getIdRefTypeAbsence();
			                        %>
			                        <tr>
			                            <td align="center">
			                            	<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAbs)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceAbs)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_MALADIES(indiceAbs)%>">
                           					<INPUT title="désactiver" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_INACTIVER(indiceAbs)%>">
				    					</td>                            
			                            <td><%=process.getVAL_ST_TYPE_MALADIE(indiceAbs)%></td> 
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
	                			<span class="sigp2Mandatory">Motif : </span><span class="sigp2-saisie"><%=process.getVAL_ST_MOTIF()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Desc motif : </span><span class="sigp2-saisie"><%=process.getVAL_ST_INFO_COMPL()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Prescripteur : </span><span class="sigp2-saisie"><%=process.getVAL_ST_PRESCRIPTEUR()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Date de déclaration : </span><span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DECLARATION()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Prolongation : </span><span class="sigp2-saisie"><%=process.getVAL_ST_PROLONGATION()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Nom enfant : </span><span class="sigp2-saisie"><%=process.getVAL_ST_NOM_ENFANT()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Nombre ITT : </span><span class="sigp2-saisie"><%=process.getVAL_ST_NOMBRE_ITT()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Siège Lésion : </span><span class="sigp2-saisie"><%=process.getVAL_ST_SIEGE_LESION()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">AT de référence : </span><span class="sigp2-saisie"><%=process.getVAL_ST_AT_REFERENCE()%></span>
		            		</td>
		            		<td>
	                			<span class="sigp2Mandatory">Maladie professionnelle : </span><span class="sigp2-saisie"><%=process.getVAL_ST_MALADIE_PRO()%></span>
		            		</td>
		            	</tr>
		            	<tr>
		            		<td>
								<span class="sigp2Mandatory">Alerte depassement lors saisie : </span>
		            			<span class="sigp2-saisie"><% if(process.getTypeCreation().getTypeSaisiDto().isAlerte()){%>oui<%}else {%>non<%}%></span>
							</td>
		            		<td>
		            			<%if(process.getTypeCreation().getTypeSaisiDto().isAlerte()){ %>
	                				<span class="sigp2Mandatory">Message alerte : </span><span class="sigp2-saisie"><%=process.getVAL_ST_MESSAGE_ALERTE()%></span>
	                			<%} %>
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
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Prescripteur : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_PRESCRIPTEUR(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_PRESCRIPTEUR(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Date de déclaration : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_DECLARATION(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_DATE_DECLARATION(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Prolongation : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_PROLONGATION(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_PROLONGATION(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Nom enfant : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NOM_ENFANT(), process.getNOM_RB_OUI()) %>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NOM_ENFANT(), process.getNOM_RB_NON()) %>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">Nombre ITT : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NOMBRE_ITT(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_NOMBRE_ITT(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Siège Lésion : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SIEGE_LESION(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_SIEGE_LESION(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            	<tr>
		            		<td>
	                			<span class="sigp2Mandatory">AT de référence : </span>
	 							<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AT_REFERENCE(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_AT_REFERENCE(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
			            	</td>
		            		<td>
								<span class="sigp2Mandatory">Maladies pro : </span>
		                		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MALADIES_PRO(), process.getNOM_RB_OUI())%>><span class="sigp2-saisie">Oui</span>
			            		<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_MALADIES_PRO(), process.getNOM_RB_NON())%>><span class="sigp2-saisie">Non</span>
							</td>
		            	</tr>
		            </table>  
			        <BR/><BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MALADIES()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>          
            	</FIELDSET>  
            <%} else if(process.getVAL_ST_ACTION().equals(process.ACTION_INACTIVE)){%>
            	<div>
					<FONT color='red'>Etes-vous sûr de vouloir désactiver : <%=process.getTypeCreation().getLibelle() %></FONT>
			        <BR/><BR/>
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Désactiver" name="<%=process.getNOM_PB_VALIDER_MALADIES()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		    	</div>
            <%} %>
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_MALADIES()%>" value="AJOUTERMALADIES">
			<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_UNITE_DECOMPTE()%>" value="UNITEDECOMPTE">     
			<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_MOTIF()%>" value="MOTIF">   
			<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_ALERTE()%>" value="ALERTE">  
			
			
			
			<!-- Pour la gestion des emails sur les alertes AT -->
			<FIELDSET class="sigp2Fieldset" style="text-align:left;">
				    <legend class="sigp2Legend">Groupes destinataires des alertes mail liées aux maladies</legend>
						<table class="sigp2NewTab" style="text-align:left;">
							<tr bgcolor="#EFEFEF">
								<td>											
								    <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_DESTINATAIRE_MAIL()%>">
								</td>
								<td align="left">Groupe</td>
							</tr>
							<%
							if (process.getListeDestinataireMailMaladie()!=null){
								for (int i = 0;i<process.getListeDestinataireMailMaladie().size();i++){
									Integer indiceGroupeMail = process.getListeDestinataireMailMaladie().get(i).getIdDestinataireMailMaladie();
							%>
									<tr id="<%=indiceGroupeMail%>" onmouseover="SelectLigne(<%=indiceGroupeMail%>,<%=process.getListeDestinataireMailMaladie().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;" align="center">
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DESTINATAIRE_MAIL(indiceGroupeMail)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100%;text-align: left;"><%=process.getVAL_ST_GROUPE_DESTINATAIRE_MAIL(indiceGroupeMail)%></td>
									</tr>
									<%
								}
							}%>
						</table>	
				</FIELDSET>
			<% if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION_MAIL_DESTINATAIRE)){%>
            	<div>
					<FONT color='red'>Etes-vous sûr de vouloir supprimer le groupe : <%=process.getGroupeCourant().getLibGroupe() %></FONT>
			        <BR/><BR/>
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_VALIDER_DESTINATAIRE_MAIL_MALADIE()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		    	</div>
            <%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION_DESTINATAIRE_MAIL)){ %>
           		<FIELDSET class="sigp2Fieldset" style="text-align:left;">				
	            	<legend class="sigp2Legend"><%=process.ACTION_CREATION_DESTINATAIRE_MAIL %> </legend>					
					<span class="sigp2Mandatory" style="margin-left: 20px; position: relative; width: 120px;">Groupes :</span> 
					<SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_GROUPE()%>" style="width: 140px;">
						<%=process.forComboHTML(process.getVAL_LB_GROUPE(), process.getVAL_LB_GROUPE_SELECT())%>
					</SELECT>
				<BR/>
                    <div align="center">	 
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DESTINATAIRE_MAIL_MALADIE()%>" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
	                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                    </div>          
            	</FIELDSET>  
            <%} %>
			
			 
		</FORM>
	</BODY>
</HTML>