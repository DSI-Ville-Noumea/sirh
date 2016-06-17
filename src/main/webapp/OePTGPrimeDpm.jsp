<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.pointage.dto.DpmIndemniteChoixAgentDto" %>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="java.util.Map"%>

<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGPrimeDpm" id="process" scope="session"></jsp:useBean>
            <TITLE>Visualisation des choix concernant la prime DPM SDJF</TITLE>		


<!--             <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>  -->
			<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
            <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
            <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
			<script type="text/javascript" src="js/dataTables.numericComma.js"></script>
            <script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

            <SCRIPT type="text/javascript">
                function executeBouton(nom) {
                    document.formu.elements[nom].click();
                }
                function setfocus(nom) {
                    if (document.formu.elements[nom] != null)
                        document.formu.elements[nom].focus();
                }
                
                $.fn.dataTableExt.oSort['date-francais-asc']  = function(a,b) {
                    var ukDatea = a.split('/');
                    var ukDateb = b.split('/');
                     
                    var x = parseInt(ukDatea[2] + ukDatea[1] + ukDatea[0]);
                    var y = parseInt(ukDateb[2] + ukDateb[1] + ukDateb[0]);
                     
                    return ((x < y) ? -1 : ((x > y) ?  1 : 0));
                };
                 
                $.fn.dataTableExt.oSort['date-francais-desc'] = function(a,b) {
                    var ukDatea = a.split('/');
                    var ukDateb = b.split('/');
                     
                    var x = parseInt(ukDatea[2] + ukDatea[1] + ukDatea[0]);
                    var y = parseInt(ukDateb[2] + ukDateb[1] + ukDateb[0]);
                     
                    return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
                };

                $(document).ready(function() {
                    $('#VisualisationTitreRepasList').dataTable({                        
                        "bAutoWidth":false,
                        "aoColumns": [
                           {"bSearchable": false, "bSortable": false, "sWidth": "20px", "sClass" : "center"},
                           {"bSearchable": true, "bSortable": true, "sWidth": "30px", "sClass" : "center"},
                           {"bSortable": true, "sWidth": "150px"},
                           {"bSortable": true, "sWidth": "60px"},
                           {"bSortable": true, "sWidth": "60px"},
                           {"bSortable": true, "sWidth": "120px"},
                           {"sType": "date-francais", "bSortable": true, "sWidth": "60px", "sClass" : "center" }
                        ],
                        "columnDefs": [
                                       { "type": "date-eu", targets: 0 }
                                   ],
                        "sDom": '<"H"flip>t<"F"ripT>',
                        "bStateSave": true,
                        "oLanguage": {
                            "oPaginate": {
                                "sFirst": "",
                                "sLast": "",
                                "sNext": "",
                                "sPrevious": ""
                            },
                            "sZeroRecords": "Aucune information à afficher",
                            "sInfo": "Affichage de _START_ à _END_ des _TOTAL_ au total",
                            "sInfoEmpty": "Aucune information à afficher",
                            "sEmptyTable": "Veuillez sélectionner une date de début et une date de fin pour afficher les informations",
                            "sInfoFiltered": "(filtrage sur _MAX_ au total)",
                            "sLengthMenu": "Affichage de _MENU_ par page",
                            "sSearch": "Recherche instantanée"
                        },
                        "oTableTools": {
                            "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "absenceVisu", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                            "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                        }

                    });
                });
                function testClickFiltrer(){
                	if(event.keyCode == 13){
                		executeBouton('NOM_PB_FILTRER');
                	}
                }
            </SCRIPT>
            <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </HEAD>
        <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%=process.getFocus()%>')">	
        <%@ include file="BanniereErreur.jsp" %>
        <FORM onkeypress="testClickFiltrer();" name="formu" method="POST" class="sigp2-titre">
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Filtres pour l'affichage</legend>
                <table>
                	<tr>
                		<td width="70px">
                			<span class="sigp2">Agent :</span>
                		</td>
                		<td width="120px">
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_DEMANDE()%>" size="4" maxlength="4" type="text" value="<%= process.getVAL_ST_AGENT_DEMANDE()%>">
			                <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>');">
			                <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>');">
                		</td>
                		<td width="45px">
                 			<span class="sigp2">Service :</span>
                		</td>
                		<td width="150px">
			                <INPUT id="service" class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_SERVICE()%>" size="8" type="text" value="<%= process.getVAL_EF_SERVICE()%>">
			                <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
			                <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
			            	<INPUT type="hidden" id="idServiceADS" size="4" width="1px" name="<%=process.getNOM_ST_ID_SERVICE_ADS()%>" value="<%=process.getVAL_ST_ID_SERVICE_ADS()%>" class="sigp2-saisie">
                		</td>
                		<td>
               				<span class="sigp2">Année : </span>
                		</td>
                		<td width="180px"> 
			                <SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_ANNEE()%>" style="width:140px;"> 
			                    <%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT())%>
			                </SELECT>
                		</td>
                		<td>
                			<span class="sigp2">Choix : </span>
                		</td>
                		<td width="140px">
			                <SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_CHOIX()%>" style="width:140px;"> 
			                    <%=process.forComboHTML(process.getVAL_LB_CHOIX(), process.getVAL_LB_CHOIX_SELECT())%>
			                </SELECT>
                		</td>
                	</tr>
                	<tr>
                		<td colspan="8" class="sigp2">
			             	<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
							<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
							<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
                		</td> 
                	</tr>
                </table>
                <BR/>
                <INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>" />
                <INPUT type="submit" class="sigp2-Bouton-100" style="width:280px;" value="Afficher les agents n'ayant pas fait de choix" name="<%=process.getNOM_PB_AFFICHER_AGENT_SANS_CHOIX()%>" 
					title="Pas de filtre pour ce bouton. Affiche tous les agents n'ayant pas fait de choix pour l'année ouverte en cours." width="220px" />
             </FIELDSET>
            
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend">Visualisation des choix agent pour la prime DPM SDJF</legend>
                <BR/>
                <table width="510px" cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationTitreRepasList"> 
                    <thead>
                        <tr>
                            <th width="20px" align="center">
                            <% if(null != process.getDpmAnneeOuverte()) { %>
                            	<img src="images/ajout.gif" height="16px" width="16px" title="Créer une demande de titre repas" 
	                            	onClick="executeBouton('<%=process.getNOM_PB_AJOUTER_CHOIX_DPM()%>')" 
	                            	class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
	                        <% } %>
            				</th>
            				<th>Matr</th>
                            <th>Agent</th>
                            <th>Année</th>
                            <th>Choix</th>
                            <th>Opérateur</th>
                            <th>Date saisie</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map.Entry<Integer, DpmIndemniteChoixAgentDto> trMap : process.getListeChoixAgent().entrySet()) {
                        	DpmIndemniteChoixAgentDto TR = trMap.getValue();
                        	int indiceTR = trMap.getKey();
                        %>
                        <tr id="tr<%=TR.getIdDpmIndemChoixAgent() %>">
                            <td width="20px" align="center">
                             <% if(null != process.getDpmAnneeOuverte()
                             		&& process.getDpmAnneeOuverte().getAnnee().equals(TR.getDpmIndemniteAnnee().getAnnee())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/modifier.gif" height="15px" width="15px" title="Editer le pointage" 
                            		onClick="executeBouton('<%=process.getNOM_PB_SAISIE_CHOIX(indiceTR) %>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
                            	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SAISIE_CHOIX(indiceTR)%>" value="">
                            	
                            	<% if(!"".equals(process.getVAL_ST_CHOIX(indiceTR))) { %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="annuler" type="image" src="images/suppression.gif"  
                            		height="15px" width="15px" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" 
                            		name="<%=process.getNOM_PB_SUPPRIMER(indiceTR)%>" onclick="confirm('Etes-vous sûr de vouloir supprimer?');executeBouton('<%=process.getNOM_PB_SUPPRIMER(indiceTR)%>')">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER(indiceTR)%>" value="">
								<% } %>
                            <% } %>
                            </td>
                            <td width="30px"><%=process.getVAL_ST_MATRICULE(indiceTR)%></td>
                            <td width="150px"><%=process.getVAL_ST_AGENT(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_ANNEE(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_CHOIX(indiceTR)%></td>
                            <td width="120px"><%=process.getVAL_ST_OPERATEUR(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_ETAT(indiceTR)%></td>
                        </tr>
                        <%}%>
                    </tbody>
                </table>
                <BR/>	
            </FIELDSET>
            
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">
			<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>" value="RECHERCHERAGENTDEMANDE">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>" value="SUPPRECHERCHERAGENTDEMANDE">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_AJOUTER_CHOIX_DPM()%>" value="AJOUTERABSENCE">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>" value="RECHERCHERAGENTCREATION">
        
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_CREATION %>">
					<legend class="sigp2Legend">Création d'une demande de titre repas pour le mois en cours</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATION()%>" style="margin-right:10px;">
	                    <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>');">
	                
	                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHOIX(), process.getNOM_RB_INDEMNITE()) %> ><span class="sigp2Mandatory">Indemnité</span>
						<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHOIX(), process.getNOM_RB_RECUPERATION()) %> ><span class="sigp2Mandatory">Récupération</span>
	                    <BR/>
	                    <div align="center">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Creer" name="<%=process.getNOM_PB_CREATION()%>">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>
            
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_MODIFICATION %>">
					<legend class="sigp2Legend">Modification d'une demande de titre repas pour le mois en cours</legend>
           				<INPUT name="<%= process.getNOM_ST_INDICE_CHOIX_AGENT() %>" type="hidden" value="<%= process.getVAL_ST_INDICE_CHOIX_AGENT()%>">
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MODIFICATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_MODIFICATION()%>" style="margin-right:10px;" readonly="readonly">
	                    
	                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHOIX(), process.getNOM_RB_INDEMNITE()) %> ><span class="sigp2Mandatory">Indemnité</span>
						<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHOIX(), process.getNOM_RB_RECUPERATION()) %> ><span class="sigp2Mandatory">Récupération</span>
	                    <BR/>
	                    <div align="center">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_MODIFICATION()%>">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>
        </FORM>
    </BODY>
</HTML>