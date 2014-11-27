<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
            <%} %>
		</FORM>
	</BODY>
</HTML>