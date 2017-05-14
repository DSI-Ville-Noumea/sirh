<%@page import="nc.noumea.mairie.abs.RefTypeGroupeAbsenceEnum"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeGroupeAbsence"%>
<%@page import="nc.mairie.enums.EnumTypeAbsence"%>
<%@page import="nc.mairie.enums.EnumEtatAbsence"%>
<%@page import="nc.mairie.gestionagent.absence.dto.DemandeDto" %>
<%@page import="nc.mairie.gestionagent.absence.dto.PieceJointeDto" %>
<%@page import="nc.mairie.gestionagent.absence.dto.TypeAbsenceDto" %>
<%@page import="nc.mairie.gestionagent.absence.dto.RefTypeSaisiDto" %>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.io.File"%>

<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.absence.OeABSVisualisation" id="process" scope="session"></jsp:useBean>
            <TITLE>Visualisation des absences</TITLE>		


<!--             <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>  -->
			<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
            <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
            <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
			<script type="text/javascript" src="js/dataTables.numericComma.js"></script>
            <script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

            <SCRIPT type="text/javascript">
                function executeBouton(nom)
                {
                    document.formu.elements[nom].click();
                }
                function setfocus(nom)
                {
                    if (document.formu.elements[nom] != null)
                        document.formu.elements[nom].focus();
                }
                
                function forcerDuree() {

                	var inputDuree = document.getElementById('<%=process.getNOM_ST_DUREE() %>');
                	inputDuree.disabled = false;
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
                    $('#VisualisationAbsenceList').dataTable({                        
                        "bAutoWidth":false,
                        "aoColumns": [
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "30px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "40px","sClass" : "center"},
                           {"bSortable": true,"sWidth": "30px"},
                           {"bSortable": true,"sWidth": "150px"},
                           {"bSortable": true,"sWidth": "40px"},
                           {"bSortable": true,"sWidth": "150px"},
                           {"sType": "date-francais", "bSortable": true, "sWidth": "60px","sClass" : "center" },
                           {"sType": "date-francais", "bSortable": true,"sWidth": "60px","sClass" : "center"},
                           {"bSortable": true,"sWidth": "40px"},
                           {"bSortable": true,"sWidth": "120px"},
                           {"bSortable": true,"sWidth": "60px"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"}
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
                            "sZeroRecords": "Aucune information d'absence à afficher",
                            "sInfo": "Affichage de _START_ à _END_ des _TOTAL_ absences au total",
                            "sInfoEmpty": "Aucune information d'absence à afficher",
                            "sEmptyTable": "Veuillez sélectionner une date de début et une date de fin pour afficher les informations d'absence",
                            "sInfoFiltered": "(filtrage sur _MAX_ absences au total)",
                            "sLengthMenu": "Affichage de _MENU_ absences par page",
                            "sSearch": "Recherche instantanée"
                        },
                        "oTableTools": {
                            "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "absenceVisu", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                            "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                        }

                    });
                });

                function loadAbsenceHistory(absId, idDemande) {     
                	var oTable = $('#VisualisationAbsenceList').dataTable();
                	var tr = document.getElementById('tr' + absId);   
                	
                	if (oTable.fnIsOpen(tr)) {
                        oTable.fnClose(tr);
                    } else {
	                	var url = "HistoriqueAbsence?idAbsence=" + absId + "&idDemande=" + idDemande;
	                	
	                	$.ajax({
	                		type: "GET",
	                		url: url,
	                		dataType : "html",
	                		//affichage de l'erreur en cas de problème
	                		error:function(msg, string){
	                				alert( "Error !: " + string );
	                			},
	                		success:function(html){
	                			var list = html;
	                            oTable.fnOpen(tr, buildDetailTable(list));
	                		}
	        			}); 
                    }
                    return 0;
                }

                function loadActeursAgent(nomatr, id, typeDemande, dateDemande) {     
          			
                	if(''==document.getElementById('ActeursAgent'+id).title) {
              		  	var url = "ActeursAgentAbsence?nomatr=" + nomatr + "&typeDemande=" + typeDemande+ "&dateDemande=" + dateDemande;
	          
	                	$.ajax({
	                		type: "GET",
	                		url: url,
	                		dataType : "html",
	                		//affichage de l'erreur en cas de problème
	                		error:function(msg, string){
	                				alert( "Error !: " + string );
	                			},
	                		success:function(html){
	                			document.getElementById('ActeursAgent'+id).title = html;
	                		}
	        			}); 
                	}
                    return 0;
                }

                /**
                 * Build the table containing the detail of the absence
                 */
                function buildDetailTable(data) {

                    // Build the new table that will handle the detail (append the thead and all tr, th)
                    var detailTable = $(document.createElement("table"))
                            .addClass("detailContent")
                            .addClass("subDataTable")
                            .attr("cellpadding", "0")
                            .attr("cellspacing", "0")
                            .attr("style", "margin-left: 455px;")
                            .attr("width", "750px")
                            .append($(document.createElement("thead")).append($(document.createElement("tr"))
                            		.append($(document.createElement("td")).html("Date demande").attr("style", "width: 180px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Date debut").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Date fin").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Durée").attr("style", "width: 70px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Motif").attr("style", "width: 150px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Etat").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Date etat").attr("style", "width: 180px;text-align: center;")))
                            );

                    // Append the tbody element
                    var tbody = $(document.createElement("tbody")).appendTo(detailTable);

                    var pointages = data.split("|");

                    //  alert(" pointages.length " +pointages.length);
                    for (var i = 0; i < pointages.length; i++) {
                        var donnees = pointages[i].split(",");
                        tbody.append($(document.createElement("tr"))
                                .addClass(i % 2 == 0 ? "even" : "odd")
                                .append($(document.createElement("td")).html(donnees[0]))
                                .append($(document.createElement("td")).html(donnees[1]))
                                .append($(document.createElement("td")).html(donnees[2]))
                                .append($(document.createElement("td")).html(donnees[3]))
                                .append($(document.createElement("td")).html(donnees[4]))
                                .append($(document.createElement("td")).html(donnees[5]))
                                .append($(document.createElement("td")).html(donnees[6]))
                                );
                    }
                    // Append the detail table into the detail container
                    var detailContainer = detailTable;
                    // Finally return the table
                    return detailContainer;
                }
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
        <FORM onkeypress="testClickFiltrer();" name="formu" method="POST" class="sigp2-titre" <% if(process.isImporting){ %> ENCTYPE="multipart/form-data" <% } %>>
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Filtres pour l'affichage</legend>
                <table>
                	<tr>
                		<td width="70px">
                			<span class="sigp2">Agent :</span>
                		</td>
                		<td width="150px">
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
                		<td width="70px">
                			<span class="sigp2">Etat : </span>
                		</td>
                		<td width="80px">
			                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT()%>" style="width:140px;">
			                    <%=process.forComboHTML(process.getVAL_LB_ETAT(), process.getVAL_LB_ETAT_SELECT())%>
			                </SELECT>
                		</td>
                		<td width="120px">&nbsp;</td>
                		<td>&nbsp;</td>
                	</tr>
                	<tr>
                		<td colspan="8" class="sigp2">
			             	<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
							<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
							<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
                		</td>
                	</tr>
                	<tr>
                		<td>
               				<span class="sigp2">Date début : </span>
                		</td>
                		<td>
			                <input id="<%=process.getNOM_ST_DATE_MIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MIN()%>" >
			                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MIN()%>', 'dd/mm/y');">
                		</td>
                		<td>
               				<span class="sigp2">Date fin : </span>
                		</td>
                		<td>
			                <input id="<%=process.getNOM_ST_DATE_MAX()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MAX()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MAX()%>" >
			                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MAX()%>', 'dd/mm/y');">
                		</td>
                		<td>
                			<span class="sigp2">Gestionnaire :</span>
                		</td>
                		<td>
			                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_GESTIONNAIRE()%>" style="width:140px;">
			                    <%=process.forComboHTML(process.getVAL_LB_GESTIONNAIRE(), process.getVAL_LB_GESTIONNAIRE_SELECT())%>
			                </SELECT>
                		</td> 
                	</tr>
                	<tr>
                		<td>
                			<span class="sigp2">Groupe : </span>
                		</td>
                		<td colspan="4">
			                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_GROUPE()%>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_GROUPE()%>")' >
			                    <%=process.forComboHTML(process.getVAL_LB_GROUPE(), process.getVAL_LB_GROUPE_SELECT())%>
			                </SELECT>
                		</td>
                	</tr>
                	<tr>
                		<td>
                			<span class="sigp2">Famille : </span>
                		</td>
                		<td colspan="4">
			                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE()%>" >
			                    <%=process.forComboHTML(process.getVAL_LB_FAMILLE(), process.getVAL_LB_FAMILLE_SELECT())%>
			                </SELECT>
                		</td>
                	</tr>
                </table>  
				<!-- Boutons cachés -->
				<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_SELECT_GROUPE()%>">	
                <BR/>         	
                <INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>">		
                <INPUT type="submit" class="sigp2-Bouton-200" value="Demandes à valider" name="<%=process.getNOM_PB_FILTRER_DEMANDE_A_VALIDER()%>">	
             </FIELDSET>             
             
              <%if(process.getVAL_ST_ACTION().equals(process.ACTION_COMMENTAIRE_DRH)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_COMMENTAIRE_DRH %>">
					<legend class="sigp2Legend">Gestion du commentaire DRH d'une absence</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
           				
           				<span class="sigp2Mandatory">Commentaire DRH :</span><br/>
						<textarea cols="150" rows="3" name="<%=process.getNOM_ST_COMMENTAIRE_DRH()%>" title="Zone de saisie du commentaire DRH"><%=process.getVAL_ST_COMMENTAIRE_DRH().trim() %></textarea>
									
						<!-- Boutons cachés -->
						<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_VALIDER_COMMENTAIRE_DRH()%>">	
	                    <div align="center">
	                    	<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_VALIDER_COMMENTAIRE_DRH()%>">	
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>         
             
              <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_CREATION %>">
					<legend class="sigp2Legend">Création d'une absence</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
           				
                		<span class="sigp2">Groupe : </span> 
		                <SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_GROUPE_CREATE()%>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_GROUPE_CREATE()%>")' >
		                    <%=process.forComboHTML(process.getVAL_LB_GROUPE_CREATE(), process.getVAL_LB_GROUPE_CREATE_SELECT())%>
		                </SELECT>
		                 <BR/><BR/>
                		<span class="sigp2">Famille : </span>
		                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_CREATION()%>" >
		                    <%=process.forComboHTML(process.getVAL_LB_FAMILLE_CREATION(), process.getVAL_LB_FAMILLE_CREATION_SELECT())%>
		                </SELECT>
           				<BR/><BR/>
           				
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATION()%>" style="margin-right:10px;">
	                    <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>');">
	                    <BR/><BR/>
	                    
						<!-- Boutons cachés -->
						<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_SELECT_GROUPE_CREATE()%>">	
	                    <div align="center">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Creer" name="<%=process.getNOM_PB_CREATION()%>">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_MOTIF_ANNULATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_MOTIF_ANNULATION %>">
	            <legend class="sigp2Legend"><span style="color: red;">Motif pour l'annulation de l'absence</span></legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
						<span class="sigp2Mandatory">Informations : <%= process.getVAL_ST_INFO_MOTIF_ANNULATION() %></span>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_ID_DEMANDE_ANNULATION() %>" disabled="disabled" style="visibility: hidden;" type="text" value="<%= process.getVAL_ST_ID_DEMANDE_ANNULATION() %>">
		            	<BR/><BR/>
						<span class="sigp2Mandatory">Motif :</span>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_MOTIF_ANNULATION() %>" size="150" type="text" value="<%= process.getVAL_ST_MOTIF_ANNULATION() %>">
	                    <BR/><BR/>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF_ANNULATION()%>">	 
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	            </FIELDSET>
            <%} %>
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_MOTIF_EN_ATTENTE)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_MOTIF_EN_ATTENTE %>">
	            <legend class="sigp2Legend"><span style="color: red;">Motif pour la mise en attente de l'absence</span></legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
						<span class="sigp2Mandatory">Informations : <%= process.getVAL_ST_INFO_MOTIF_EN_ATTENTE() %></span>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_ID_DEMANDE_EN_ATTENTE() %>" disabled="disabled" style="visibility: hidden;" type="text" value="<%= process.getVAL_ST_ID_DEMANDE_EN_ATTENTE() %>">
		            	<BR/><BR/>
						<span class="sigp2Mandatory">Motif :</span>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_MOTIF_EN_ATTENTE() %>" size="150" type="text" value="<%= process.getVAL_ST_MOTIF_EN_ATTENTE() %>">
	                    <BR/><BR/>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF_EN_ATTENTE()%>">	 
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                </FIELDSET>
            <%} %>

			<!-- ------------------------ CREATION D UNE DEMANDE ----------------------------- -->
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION_DEMANDE)){ 
					TypeAbsenceDto typeCreation = process.getTypeCreation(); %>
			
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_CREATION_DEMANDE %>">
	            	<legend class="sigp2Legend">Création d'une demande de type <%=typeCreation.getLibelle() %> pour l'agent <%=process.getAgentCreation().getPrenomUsage() %> <%=process.getAgentCreation().getNomUsage() %> (<%=process.getAgentCreation().getNomatr() %>)</legend>
	            		<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
		            	<% if(typeCreation.getTypeSaisiDto()!=null) { %>
		            	<table>
		            		<tr>
		            			<% if(typeCreation.getTypeSaisiDto().isCalendarDateDebut()) { %>
		            			<td width="100px">
	                        		<span class="sigp2Mandatory">Date de début :</span>
		            			</td>
		            			<td>
			                        <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	
			                        	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DEBUT()%>" >
			                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiDto().isCalendarHeureDebut()) { %>
		            			<td>
	                        		<span class="sigp2Mandatory">Heure de début :</span>
		            			</td>
		            			<td>
									<SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_HEURE_DEBUT() %>">
										<%=process.forComboHTML(process.getVAL_LB_HEURE(), process.getVAL_LB_HEURE_DEBUT_SELECT())%>
									</SELECT>	
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiDto().isDuree()) { %>
		            			<td>
	                        		<span class="sigp2Mandatory">Durée :</span>
		            			</td>
		            			<td>
									<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_ST_DUREE() %>" size="6" type="text" value="<%= process.getVAL_ST_DUREE() %>">
									<span class="sigp2Mandatory"> heure(s)</span>
		            			</td>
		            			<td>
									<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_ST_DUREE_MIN() %>" size="6" type="text" value="<%= process.getVAL_ST_DUREE_MIN() %>">
									<span class="sigp2Mandatory"> minute(s)</span>
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiDto().isChkDateDebut()) { %>
		            			<td>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">A</span>
		            			</td>
		            			<% } %>
		            		</tr>
		            		<tr>
		            			<% if(typeCreation.getTypeSaisiDto().isCalendarDateFin()) { %>
		            			<td>
	                        		<span class="sigp2Mandatory">Date de fin :</span>
		            			</td>
		            			<td>
			                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	
			                        	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_FIN()%>" >
			                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiDto().isCalendarHeureFin()) { %>
		            			<td>
	                        		<span class="sigp2Mandatory">Heure de fin :</span>
		            			</td>
		            			<td>
									<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_HEURE_FIN() %>">
										<%=process.forComboHTML(process.getVAL_LB_HEURE(), process.getVAL_LB_HEURE_FIN_SELECT()) %>
									</SELECT>	
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiDto().isChkDateFin()) { %>
		            			<td>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">A</span>
		            			</td>
		            			<% } %>
		            		</tr>
		            		<% if(typeCreation.getTypeSaisiDto().isCompteurCollectif()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Organisation Syndicale :</span>
		            			</td>
		            			<td colspan="2">
							        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_OS()%>" style="width:340px;">
							        	<%=process.forComboHTML(process.getVAL_LB_OS(), process.getVAL_LB_OS_SELECT())%>
							        </SELECT>
		            			</td>
		            		</tr>
		            		<% } %>
		            		
		            		<!-- Maladies -->
		            		<% if(typeCreation.getTypeSaisiDto().isPrescripteur()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Prescripteur :</span>
		            			</td>
		            			<td colspan="2">
							       <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_PRESCRIPTEUR() %>" size="6" type="text" value="<%= process.getVAL_ST_PRESCRIPTEUR() %>">
		            			</td>
		            		</tr>
		            		<% } %>
	            			<% if(typeCreation.getTypeSaisiDto().isDateDeclaration()) { %>
	            			<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Date de déclaration :</span>
		            			</td>
		            			<td>
			                        <input id="<%=process.getNOM_ST_DATE_DECLARATION()%>" class="sigp2-saisie" maxlength="10"	
			                        	name="<%= process.getNOM_ST_DATE_DECLARATION()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DECLARATION()%>" >
			                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DECLARATION()%>', 'dd/mm/y');">
		            			</td>
	            			</tr>
	            			<% } %>
	            			<% if(typeCreation.getTypeSaisiDto().isProlongation()) { %>
	            			<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Prolongation :</span>
		            			</td>
		            			<td>
			                        <input type="checkbox" name="<%=process.getNOM_CK_PROLONGATION() %>" <% if(process.getVAL_CK_PROLONGATION().equals(process.getCHECKED_ON())){ %> checked="checked" <% } %> />
		            			</td>
	            			</tr>
	            			<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isNomEnfant()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Nom Enfant :</span>
		            			</td>
		            			<td colspan="2">
							       <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_NOM_ENFANT() %>" size="6" type="text" value="<%= process.getVAL_ST_NOM_ENFANT() %>">
		            			</td>
		            		</tr>
		            		<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isNombreITT()) { %>
		            		<tr>		            		
		            			<td>
	                        		<span class="sigp2Mandatory">Nombre ITT :</span>
		            			</td>
		            			<td colspan="2">
									<INPUT class="sigp2-saisie" maxlength="6" name="<%= process.getNOM_ST_NOMBRE_ITT() %>" size="6" type="text" value="<%= process.getVAL_ST_NOMBRE_ITT() %>">
									<span class="sigp2Mandatory"> jour(s)</span>
		            			</td>
		            		</tr>
	            			<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isSiegeLesion()) { %>
		            		<tr>		            		
		            			<td>
	                        		<span class="sigp2Mandatory">Siège des lésions :</span>
		            			</td>
		            			<td colspan="2">
							        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_SIEGE_LESION()%>" style="width:340px;">
							        	<%=process.forComboHTML(process.getVAL_LB_SIEGE_LESION(), process.getVAL_LB_SIEGE_LESION_SELECT())%>
							        </SELECT>
		            			</td>
		            		</tr>
	            			<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isMaladiePro()) { %>
		            		<tr>		            		
		            			<td>
	                        		<span class="sigp2Mandatory">Maladie professionnelle :</span>
		            			</td>
		            			<td colspan="2">
							        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MALADIE_PRO()%>" style="width:340px;">
							        	<%=process.forComboHTML(process.getVAL_LB_MALADIE_PRO(), process.getVAL_LB_MALADIE_PRO_SELECT())%>
							        </SELECT>
		            			</td>
		            		</tr>
	            			<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isAtReference()) { %>
		            		<tr>		            		
		            			<td>
	                        		<span class="sigp2Mandatory">Accident de travail de référence :</span>
		            			</td>
		            			<td colspan="2">
							        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_AT_REFERENCE()%>" style="width:340px;">
							        	<%=process.forComboHTML(process.getVAL_LB_AT_REFERENCE(), process.getVAL_LB_AT_REFERENCE_SELECT())%>
							        </SELECT>
		            			</td>
		            		</tr>
	            			<% } %>
		            		
		            		<!-- Fin Maladies -->
		            		
		            		<% if(typeCreation.getTypeSaisiDto().isMotif()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Commentaire :</span>
		            			</td>
		            			<td>
									<textarea cols="15" rows="3" name="<%=process.getNOM_ST_MOTIF_CREATION()%>" title="Zone de saisie du commentaire"><%=process.getVAL_ST_MOTIF_CREATION().trim() %></textarea>
									<%
									String infoCompl = "";
									if(null != typeCreation.getTypeSaisiDto().getInfosComplementaires()) {
										infoCompl = typeCreation.getTypeSaisiDto().getInfosComplementaires();
									} %>
									<%=infoCompl %>
		            			</td>
		            		</tr>
		            		<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isPieceJointe()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Pièces jointes :</span>
		            			</td>
		            			<td>
		            				<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
		            				<% if(null != process.listFichierUpload
		            						&& !process.listFichierUpload.isEmpty()) {
		            					for(File file : process.listFichierUpload) { %>
		            						<div>
			            						<%=file.getName() %>
			            						<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" 
		            								class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" 
		            								name="<%=process.getNOM_PB_SUPPRIMER_DOC(file.getName())%>">
		            						</div>
		            					<% }
		            				} %>
		            			</td>
		            		</tr>
		            		<% } %>
		            		<% if(!typeCreation.getTypeSaisiDto().isSaisieKiosque()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Etat :</span>
		            			</td>
		            			<td>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_ETAT(), process.getNOM_RB_ETAT_EN_ATTENTE()) %> ><span class="sigp2Mandatory">En attente</span>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_ETAT(), process.getNOM_RB_ETAT_VALIDE()) %> ><span class="sigp2Mandatory">Validée</span>
		            			</td>
		            		</tr>
		            		<% } %>
		            	</table>
		            	<%}else{ %>
		            	<table>
		            		<tr>
		            			<% if(typeCreation.getTypeSaisiCongeAnnuelDto().isCalendarDateDebut()) { %>
		            			<td width="100px">
	                        		<span class="sigp2Mandatory">Date de début :</span>
		            			</td>
		            			<td>
			                        <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	
			                        	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DEBUT()%>" onfocus="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');">
			                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiCongeAnnuelDto().isChkDateDebut()) { %>
		            			<td>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_M()) %> onclick="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');" ><span class="sigp2Mandatory">M</span>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_AM()) %>  onclick="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');"><span class="sigp2Mandatory">A</span>
		            			</td>
		            			<% } %>
		            		</tr>
		            		<tr>
		            			<% if(typeCreation.getTypeSaisiCongeAnnuelDto().isCalendarDateFin()) { %>
		            			<td>
	                        		<span class="sigp2Mandatory">Date de fin :</span>
		            			</td>
		            			<td>
			                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	
			                        	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_FIN()%>"  onfocus="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');">
			                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiCongeAnnuelDto().isChkDateFin()) { %>
		            			<td>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_M()) %> onclick="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');"><span class="sigp2Mandatory">M</span>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_AM()) %>  onclick="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');"><span class="sigp2Mandatory">A</span>
		            			</td>
		            			<% } %>
		            		</tr>
		            		<tr>
		            			<% if(typeCreation.getTypeSaisiCongeAnnuelDto().isCalendarDateReprise()) { %>
		            			<td>
	                        		<span class="sigp2Mandatory">Date de reprise :</span>
		            			</td>
		            			<td colspan="2">
			                        <input id="<%=process.getNOM_ST_DATE_REPRISE()%>" class="sigp2-saisie" maxlength="10"	
			                        	name="<%= process.getNOM_ST_DATE_REPRISE()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_REPRISE()%>"  onfocus="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');">
			                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_REPRISE()%>', 'dd/mm/y');">
		            			</td>
		            			<% } %>
		            		</tr>
		            		<tr>		            		
		            			<td>
	                        		<span class="sigp2Mandatory">Durée :</span>
		            			</td>
		            			<td colspan="2">
									<INPUT class="sigp2-saisie" disabled="disabled" maxlength="6" id="<%=process.getNOM_ST_DUREE() %>" name="<%= process.getNOM_ST_DUREE() %>" size="6" type="text" value="<%= process.getVAL_ST_DUREE() %>">
									<span class="sigp2Mandatory"> jour(s)</span>
									<% if(typeCreation.getTypeSaisiCongeAnnuelDto().getCodeBaseHoraireAbsence().equals("C")) { %>
									<INPUT type="button" onclick="forcerDuree()" value="Forcer">
									<% } %>
		            			</td>
		            		</tr>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Commentaire :</span>
		            			</td>
		            			<td>
									<textarea cols="15" rows="3" name="<%=process.getNOM_ST_MOTIF_CREATION()%>" title="Zone de saisie du commentaire"><%=process.getVAL_ST_MOTIF_CREATION().trim() %></textarea>
									<%
									String infoCompl = "";
									if(null != typeCreation.getTypeSaisiCongeAnnuelDto().getInfosComplementaires()) {
										infoCompl = typeCreation.getTypeSaisiCongeAnnuelDto().getInfosComplementaires();
									} %>
									<%=infoCompl %>
		            			</td>
		            		</tr>
		            		<% if(typeCreation.getTypeSaisiCongeAnnuelDto().isPieceJointe()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Pièces jointes :</span>
		            			</td>
		            			<td>
		            				<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
		            				<% if(null != process.listFichierUpload
		            						&& !process.listFichierUpload.isEmpty()) {
		            					for(File file : process.listFichierUpload) { %>
		            						<div>
			            						<%=file.getName() %>
			            						<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" 
		            								class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" 
		            								name="<%=process.getNOM_PB_SUPPRIMER_DOC(file.getName())%>">
		            						</div>
		            					<% }
		            				} %>
		            			</td>
		            		</tr>
		            		<% } %>
		            	</table>
		            	<%} %>
		            	<BR/>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CREATION_DEMANDE() %>">
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER() %>">
	            </FIELDSET>
			<% } %>
            
            <!--  Création d'une demande de contrôle médical -->
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION_CONTROLE_MEDICAL)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_CREATION_CONTROLE_MEDICAL %>">
	            <legend class="sigp2Legend">Demande de contrôle médical</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            			<%if(process.isCreationControleMedical()){ %>
							<span class="sigp2" style="color:red;">Une fois la demande créée, vous ne pourrez plus la modifier, ni la supprimer !</span>
           				<%}%>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_ID_COMMENTAIRE_CM() %>" disabled="disabled" style="visibility: hidden;" type="hidden" value="<%= process.getVAL_ST_ID_COMMENTAIRE_CM() %>">
		            	<BR/><BR/>
						<span class="sigp2Mandatory">Commentaire :</span>
            			<%if(process.isCreationControleMedical()){ %>
	                    	<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_COMMENTAIRE_CM() %>" size="150" type="text" value="<%= process.getVAL_ST_COMMENTAIRE_CM() %>">
           				<%} else {%>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_COMMENTAIRE_CM() %>" readonly="readonly" size="150" type="text" value="<%= process.getVAL_ST_COMMENTAIRE_CM() %>">
           				<%}%>
	                    <BR/><BR/>
            			<%if(process.isCreationControleMedical()){ %>
	                    	<INPUT onkeydown="" onkeypress="" onkeyup="" readonly="readonly" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_COMMENTAIRE_CM()%>">
           				<%} %>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
                </FIELDSET>
            <%} %>
			
			
            	<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)
            			|| process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_AJOUT)){ %> 
            	<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.getVAL_ST_ACTION_DOCUMENT() %>">
            		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_DOCUMENT() %></legend>
					<div>
						<table>
							<tr>
								<td>
									<span class="sigp2">Fichier : </span>
								</td>
								<td>
									<% if(process.fichierUpload == null){ %>
									<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
									<%}else{ %>
									<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
									<% }%>
								</td>
							</tr>
						</table>
		            	<BR/>
		            	<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)) { %>
	                   		<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DOCUMENT_CREATION() %>">
	                    <% } %>
		            	<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_AJOUT)) { %>
	                   		<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DOCUMENT_AJOUT() %>">
	                    <% } %>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DOCUMENT() %>">
					</div>
				</FIELDSET>
			<%}%>
			
			<SCRIPT type="text/javascript">
            	window.location.hash = '#<%=process.getVAL_ST_ACTION() %>';
            </SCRIPT>
			<!-- ------------------------ CREATION D UNE DEMANDE ----------------------------- -->
            	
				
			<% if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
				<div>
				    <FONT color='red'>Veuillez valider votre choix.</FONT>
				    <BR/><BR/>
					<span class="sigp2" style="width:130px;">Nom du document : </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
					<BR/>
				</div>
				<BR/>
				<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
					<TBODY>
						<TR>
							<TD width="31"><INPUT type="submit"
								class="sigp2-Bouton-100" value="Valider"
								name="<%=process.getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION()%>"></TD>
							<TD height="18" width="15"></TD>
							<TD class="sigp2" style="text-align : center;"
								height="18" width="23"><INPUT type="submit"
								class="sigp2-Bouton-100" value="Annuler"
								name="<%=process.getNOM_PB_ANNULER()%>"></TD>
						</TR>
					</TBODY>
				</TABLE>
		        <BR>
            </FIELDSET>
			<% }%>
            
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend">Visualisation des demandes</legend>
                <BR/>
                <table width="880px" cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationAbsenceList"> 
                    <thead>
                        <tr>
                            <th width="20px" align="center">
                            	<img src="images/ajout.gif" height="16px" width="16px" title="Creer une absence" onClick="executeBouton('<%=process.getNOM_PB_AJOUTER_ABSENCE()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
            				</th>  
                            <th align="center"> <img src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de l'absence" onkeydown="" onkeypress="" onkeyup=""></th>
                            <th align="center"> <img src="images/info.jpg" height="16px" width="16px" title="Alertes." onkeydown="" onkeypress="" onkeyup=""></th>
                            <th>Matr</th>
                            <th>Agent</th>
                            <th>Cat<br>Statut</th>
                            <th>Type absence<br>Date demande</th>
                            <th>Début</th>
                            <th>Fin</th>
                            <th>Durée</th>
                            <th>Commentaire</th>
                            <th>Etat</th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Valider" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VALIDER_ALL()%>" onclick="executeBouton('<%=process.getNOM_PB_VALIDER_ALL()%>');"></th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER_ALL()%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER_ALL()%>');"></th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="En attente" type="image" src="images/clock.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>"></th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Contrôle médical" type="image" src="images/firstAidKit.png"  height="18px" width="18px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>"></th>
                            <th>PJ</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map.Entry<Integer, DemandeDto> absMap : process.getListeAbsence().entrySet()) {
                			DemandeDto abs = absMap.getValue();
                        	int indiceAbs = absMap.getKey();
                        %>
                        <tr id="tr<%=indiceAbs %>">
                            <td width="20px" align="center">
                            <%if(abs.isAffichageBoutonDupliquer()){ %>                            	
                            	<img onkeydown="" onkeypress="" onkeyup="" title="dupliquer" type="image" src="images/dupliquer.gif"  height="15px" width="15px" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DUPLIQUER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_DUPLIQUER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_DUPLIQUER(indiceAbs)%>" value="">
                            <%} %>
                            <%if(abs.isAffichageBoutonAnnuler()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="annuler" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_ANNULER_DEMANDE(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_ANNULER_DEMANDE(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_ANNULER_DEMANDE(indiceAbs)%>" value="">
                            <%} %>
							</td>  
                            <td width="30px" align="center">
                             <% // #15599 
                             if(abs.isAffichageBoutonHistorique()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de l'absence" onClick="loadAbsenceHistory('<%=indiceAbs %>', '<%=abs.getIdDemande() %>')">
                            <% } %>
                            </td>
                            <td width="40px" align="center">
                            <%if(abs.isDepassementCompteur()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/seuil.png" height="16px" width="16px" title="Le seuil du compteur est dépassé pour cette demande.">
                            <%} %>
                            <%if(abs.isDepassementMultiple()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/multiple.jpg" height="16px" width="16px" title="Cette demande n'est pas un multiple.">
                            <%} %>
                            <%if(abs.isDepassementITT()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/seuil.png" height="16px" width="16px" title="Le nombre de jours d'ITT est incohérent avec la date début/fin.">
                            <%} %>
                            </td>                            
                            <td width="30px"><%=process.getVAL_ST_MATRICULE(indiceAbs)%></td> 
                            <td width="150px"><a id="ActeursAgent<%=indiceAbs %>" style="hover:cursor:pointer;" 
                            	onmouseover="loadActeursAgent('<%=process.getVAL_ST_MATRICULE(indiceAbs)%>','<%=indiceAbs %>', '<%=abs.getIdTypeDemande() %>', '<%=new SimpleDateFormat("dd/MM/yyyy").format(abs.getDateDebut()) %>')" >
                            	<%=process.getVAL_ST_AGENT(indiceAbs)%></a></td> 
                            <td width="40px"><%=process.getVAL_ST_INFO_AGENT(indiceAbs)%></td>  
                            <td width="150px"><%=process.getVAL_ST_TYPE(indiceAbs)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_DEB(indiceAbs)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_FIN(indiceAbs)%></td>
                            <td width="40px"><%=process.getVAL_ST_DUREE(indiceAbs)%></td>
                            <td width="120px">
                            	<%=process.getVAL_ST_MOTIF(indiceAbs)%>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/loupe.gif" height="16px" width="16px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" title="<%=process.getCommentaireDRH(abs) %>"  name="<%=process.getNOM_PB_COMMENTAIRE_DRH(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_COMMENTAIRE_DRH(indiceAbs)%>');">                            	
                            	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_COMMENTAIRE_DRH(indiceAbs)%>" value="">
                            </td>
                            <td width="60px"><%=process.getVAL_ST_ETAT(indiceAbs)%></td>
                            <td width="20px" align="center">
                            <!-- #14696 ajout de l etat A VALIDER car erreur lors de la reprise de donnees des conges exceptionnels mis  l etat A VALIDER au lieu de SAISI ou APPROUVE -->
                            <%if(abs.isAffichageValidation()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Valider" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VALIDER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_VALIDER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_VALIDER(indiceAbs)%>" value="">
                            <%} %>
							</td>
                            <td width="20px" align="center">
                             <!-- #14696 ajout de l etat A VALIDER car erreur lors de la reprise de donnees des conges exceptionnels mis  l etat A VALIDER au lieu de SAISI ou APPROUVE -->
                            <%if(abs.isAffichageBoutonRejeter()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER(indiceAbs)%>" value="">
                            <%} %>
							</td>
                            <td width="20px" align="center">
                             <!-- #14696 ajout de l etat A VALIDER car erreur lors de la reprise de donnees des conges exceptionnels mis  l etat A VALIDER au lieu de SAISI ou APPROUVE -->
                            <%if(abs.isAffichageEnAttente()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="En attente" type="image" src="images/clock.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>" value="">
                            <%} %>
							</td>
							<td width="20px" align="center">
	                            <%if(abs.getGroupeAbsence().getIdRefGroupeAbsence() == RefTypeGroupeAbsenceEnum.MALADIES.getValue() && (abs.getControleMedical() == null || abs.getControleMedical().getId() == null)){ %>
	                            	<img onkeydown="" onkeypress="" onkeyup="" title="Créer une demande de contrôle médical" type="image" src="images/ajout.gif"  height="16px" width="16px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONTROLE_MEDICAL(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_CONTROLE_MEDICAL(indiceAbs)%>');">
	                            <%} %>
	                            <%if(abs.getGroupeAbsence().getIdRefGroupeAbsence() == RefTypeGroupeAbsenceEnum.MALADIES.getValue() && abs.getControleMedical() != null && abs.getControleMedical().getId() != null){ %>
	                            	<img onkeydown="" onkeypress="" onkeyup="" title="Visualiser la demande de contrôle médical" type="image" src="images/loupe.gif"  height="16px" width="16px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONTROLE_MEDICAL(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_CONTROLE_MEDICAL(indiceAbs)%>');">
	                            <%} %>
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CONTROLE_MEDICAL(indiceAbs)%>" value="">
							</td>
                            <td width="50px" >
                            <INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" 
                            	src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_DOC(indiceAbs)%>">
                            <% if(null !=abs.getPiecesJointes()
                            		&& !abs.getPiecesJointes().isEmpty()) {
            						int indicePJ = 0;
                            		for(PieceJointeDto pj : abs.getPiecesJointes()) { %>
                            			<div>
	                            			Pièce jointe 
	                            			<a href="<%=pj.getUrlFromAlfresco() %>" title="<%=pj.getTitre() %>" target="_blank" ><img onkeydown="" onkeypress="" 
	                            				onkeyup="" src="images/loupe.gif" height="16px" width="16px" title="Voir la pièce jointe <%=pj.getTitre() %>" /></a>
                            				<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceAbs,indicePJ)%>">
										</div>
                            <% 		
                            indicePJ++;
                            }
                            	} %>
							</td>
                        </tr>
                        <%}%>
                    </tbody>
                </table>
                <BR/>	
            </FIELDSET>
            
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
			<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>" value="RECHERCHERAGENTDEMANDE">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>" value="SUPPRECHERCHERAGENTDEMANDE">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_AJOUTER_ABSENCE()%>" value="AJOUTERABSENCE">        
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>" value="RECHERCHERAGENTCREATION"> 
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_CALCUL_DUREE()%>" value="CALCULDUREE">        
           
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_VALIDER_ALL()%>" value="">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER_ALL()%>" value="">
        </FORM>
    </BODY>
</HTML>