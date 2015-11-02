<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.pointage.dto.TitreRepasDemandeDto" %>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.gestionagent.process.pointage.EtatPointageEnum" %>
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
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGTitreRepas" id="process" scope="session"></jsp:useBean>
            <TITLE>Visualisation des titres repas</TITLE>		


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
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "30px","sClass" : "center"},
                           {"bSortable": true,"sWidth": "30px"},
                           {"bSortable": true,"sWidth": "150px"},
                           {"bSortable": true,"sWidth": "40px"},
                           {"sType": "date-francais", "bSortable": true, "sWidth": "60px","sClass" : "center" },
                           {"bSortable": true,"sWidth": "150px"},
                           {"bSortable": true,"sWidth": "150px"},
                           {"bSortable": true,"sWidth": "40px"},
                           {"sType": "date-francais", "bSortable": true,"sWidth": "60px","sClass" : "center"},
                           {"bSortable": true,"sWidth": "40px"},
                           {"bSortable": true,"sWidth": "40px"}
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

                function loadTitreRepasHistory(idDemandeTR) {     
                	var oTable = $('#VisualisationTitreRepasList').dataTable();
                	var tr = document.getElementById('tr' + idDemandeTR);   
                	
                	if (oTable.fnIsOpen(tr)) {
                        oTable.fnClose(tr);
                    } else {
	                	var url = "HistoriqueTitreRepas?idDemandeTR=" + idDemandeTR;
	                	
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
                            		.append($(document.createElement("td")).html("Commande").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Commentaire").attr("style", "width: 150px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Opérateur").attr("style", "width: 150px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Etat").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Date état").attr("style", "width: 180px;text-align: center;")))
                            );

                    // Append the tbody element
                    var tbody = $(document.createElement("tbody")).appendTo(detailTable);

                    var pointages = data.split("|");

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
        <FORM onkeypress="testClickFiltrer();" name="formu" method="POST" class="sigp2-titre">
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
			                <SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_ETAT()%>" style="width:140px;"> 
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
                		<td></td>
                		<td></td> 
                	</tr>
                </table>
                <BR/>         	
                <INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>">		
                <INPUT type="submit" class="sigp2-Bouton-200" value="Demandes à valider" name="<%=process.getNOM_PB_FILTRER_DEMANDE_A_VALIDER()%>">	
             </FIELDSET>             
            
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend">Visualisation des demandes de titre repas</legend>
                <BR/>
                <table width="880px" cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationTitreRepasList"> 
                    <thead>
                        <tr>
                            <th width="20px" align="center">
                            	<img src="images/ajout.gif" height="16px" width="16px" title="Créer une demande de titre repas" onClick="executeBouton('<%=process.getNOM_PB_AJOUTER_DEMANDE_TR()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
            				</th>  
                            <th align="center"> <img src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de la demande" onkeydown="" onkeypress="" onkeyup=""></th>
                            <th>Matr</th>
                            <th>Agent</th>
                            <th>Date demande</th>
                            <th>Commande</th>
                            <th>Commentaire</th>
                            <th>Opérateur</th>
                            <th>Etat</th>
                            <th>Date saisie</th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Valider" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VALIDER_ALL()%>" onclick="executeBouton('<%=process.getNOM_PB_VALIDER_ALL()%>');"></th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER_ALL()%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER_ALL()%>');"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map.Entry<Integer, TitreRepasDemandeDto> trMap : process.getListeDemandeTR().entrySet()) {
                        	TitreRepasDemandeDto TR = trMap.getValue();
                        	int indiceTR = trMap.getKey();
                        %>
                        <tr id="tr<%=TR.getIdTrDemande() %>">
                            <td width="30px" align="center">
                            <% if(TR.getIdRefEtat()!=EtatPointageEnum.JOURNALISE.getCodeEtat()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/modifier.gif" height="15px" width="15px" title="Editer le pointage" onClick="executeBouton('<%=process.getNOM_PB_SAISIE_TR(indiceTR)%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
                            	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SAISIE_TR(indiceTR)%>" value="">
                            <% } %>
                            </td>
                            <td width="30px" align="center">
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de la demande" onClick="loadTitreRepasHistory('<%=TR.getIdTrDemande() %>')">
                            </td>
                            <td width="30px"><%=process.getVAL_ST_MATRICULE(indiceTR)%></td>
                            <td width="150px"><%=process.getVAL_ST_AGENT(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_MONTH(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_COMMANDE(indiceTR)%></td>
                            <td width="120px"><%=process.getVAL_ST_COMMENTAIRE(indiceTR)%></td>
                            <td width="120px"><%=process.getVAL_ST_OPERATEUR(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_ETAT(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_ETAT(indiceTR)%></td>
                            <td width="20px" align="center">
                            <%if(TR.getIdRefEtat()==EtatPointageEnum.SAISI.getCodeEtat() || TR.getIdRefEtat()==EtatPointageEnum.REJETE.getCodeEtat()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Appouver" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_APPROUVER(indiceTR)%>" onclick="executeBouton('<%=process.getNOM_PB_APPROUVER(indiceTR)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_APPROUVER(indiceTR)%>" value="">
                            <%} %>
							</td>  
                            <td width="20px" align="center">
                            <%if(TR.getIdRefEtat()==EtatPointageEnum.SAISI.getCodeEtat() || TR.getIdRefEtat()==EtatPointageEnum.APPROUVE.getCodeEtat()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER(indiceTR)%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER(indiceTR)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER(indiceTR)%>" value="">
                            <%} %>
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
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_AJOUTER_DEMANDE_TR()%>" value="AJOUTERABSENCE">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>" value="RECHERCHERAGENTCREATION">
        
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_CREATION %>">
					<legend class="sigp2Legend">Création d'une demande de titre repas pour le mois en cours</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATION()%>" style="margin-right:10px;">
	                    <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>');">
	                
	                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_COMMANDE(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">Oui</span>
						<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_COMMANDE(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">Non</span>
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
           				<INPUT name="<%= process.getNOM_ST_INDICE_TR() %>" type="hidden" value="<%= process.getVAL_ST_INDICE_TR()%>">
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MODIFICATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_MODIFICATION()%>" style="margin-right:10px;" readonly="readonly">
	                    
	                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_COMMANDE(), process.getNOM_RB_OUI()) %> ><span class="sigp2Mandatory">Oui</span>
						<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_COMMANDE(), process.getNOM_RB_NON()) %> ><span class="sigp2Mandatory">Non</span>
	                    <BR/>
	                    <div align="center">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_MODIFICATION()%>">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_MOTIF_REJET)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_MOTIF_REJET %>">
	            <legend class="sigp2Legend"><span style="color: red;">Motif pour l'annulation de l'absence</span></legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
						<span class="sigp2Mandatory">Informations : <%= process.getVAL_ST_INFO_MOTIF_REJET() %></span>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_ID_DEMANDE_REJET() %>" disabled="disabled" style="visibility: hidden;" type="text" value="<%= process.getVAL_ST_ID_DEMANDE_REJET() %>">
		            	<BR/><BR/>
						<span class="sigp2Mandatory">Motif :</span>
						<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_MOTIF_REJET() %>" size="150" type="text" value="<%= process.getVAL_ST_MOTIF_REJET() %>">
	                    <BR/><BR/>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_MOTIF_REJET()%>">	 
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	            </FIELDSET>
            <%} %>
			
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_VALIDER_ALL()%>" value="">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER_ALL()%>" value="">
        </FORM>
    </BODY>
</HTML>