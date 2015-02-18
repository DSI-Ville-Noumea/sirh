<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeGroupeAbsence"%>
<%@page import="nc.mairie.enums.EnumTypeAbsence"%>
<%@page import="nc.mairie.enums.EnumEtatAbsence"%>
<%@page import="nc.mairie.gestionagent.absence.dto.DemandeDto" %>
<%@page import="nc.mairie.gestionagent.absence.dto.TypeAbsenceDto" %>
<%@page import="nc.mairie.gestionagent.absence.dto.RefTypeSaisiDto" %>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.metier.poste.Service"%>

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
            <SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>
			<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
            <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
            <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
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

                function agrandirHierarchy() {

                    hier = document.getElementById('treeHierarchy');

                    if (hier.style.display != 'none') {
                        reduireHierarchy();
                    } else {
                        hier.style.display = 'block';
                    }
                }

                function reduireHierarchy() {
                    hier = document.getElementById('treeHierarchy');
                    hier.style.display = 'none';
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
                           {"bSortable": true,"sWidth": "60px","sClass" : "center"},
                           {"bSortable": true,"sWidth": "60px","sClass" : "center"},
                           {"bSortable": true,"sWidth": "40px"},
                           {"bSortable": true,"sWidth": "120px"},
                           {"bSortable": true,"sWidth": "60px"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"},
                           {"bSearchable": false,"bSortable": false,"sWidth": "20px","sClass" : "center"}
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

                function loadAbsenceHistory(absId) {     
                	var oTable = $('#VisualisationAbsenceList').dataTable();
                	var tr = document.getElementById('tr' + absId);   
                	
                	if (oTable.fnIsOpen(tr)) {
                        oTable.fnClose(tr);
                    } else {
	                	var url = "HistoriqueAbsence?idAbsence=" + absId;
	                	
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
                            		.append($(document.createElement("td")).html("Date debut").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Date fin").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Durée").attr("style", "width: 70px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Motif").attr("style", "width: 150px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Etat").attr("style", "width: 90px;text-align: center;"))
                            		.append($(document.createElement("td")).html("Date etat").attr("style", "width: 80px;text-align: center;")))
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
            <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
			            	<INPUT type="hidden" id="codeservice" size="4" width="1px" name="<%=process.getNOM_ST_CODE_SERVICE()%>" value="<%=process.getVAL_ST_CODE_SERVICE()%>" class="sigp2-saisie">
                		</td>
                		<td width="35px">
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
                			<span class="sigp2">Action faite par :</span>
                		</td>
                		<td>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_ACTION()%>" size="4" maxlength="4" type="text" value="<%= process.getVAL_ST_AGENT_ACTION()%>">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_ACTION()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION()%>');">
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
             	<div id="treeHierarchy" style="display: none;margin-left:300px;margin-top:20px; height: 340; width: 500; overflow:auto; background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;">
                <script type="text/javascript">
                d = new dTree('d');
                d.add(0, -1, "Services");

                        <%
                            String serviceSaisi = process.getVAL_EF_SERVICE().toUpperCase();
                            int theNode = 0;
                            for (int i = 1; i < process.getListeServices().size(); i++) {
                                Service serv = (Service) process.getListeServices().get(i);
                                String code = serv.getCodService();
                                TreeHierarchy tree = (TreeHierarchy) process.getHTree().get(code);
                                if (theNode == 0 && serviceSaisi.equals(tree.getService().getSigleService())) {
                                    theNode = tree.getIndex();
                                }
                        %>
                        <%=tree.getJavaScriptLine()%>
                        <%}%>
                document.write(d);

                d.closeAll();
                        <% if (theNode != 0) {%>
                d.openTo(<%=theNode%>, true);
                        <%}%>
                    </script>
                </div>
                
             </FIELDSET>             
            
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
                            <th>Motif</th>
                            <th>Etat</th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Valider" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VALIDER_ALL()%>" onclick="executeBouton('<%=process.getNOM_PB_VALIDER_ALL()%>');"></th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER_ALL()%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER_ALL()%>');"></th>
                            <th><img onkeydown="" onkeypress="" onkeyup="" title="En attente" type="image" src="images/clock.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>"></th>
                            <th>PJ</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%    for (DemandeDto abs : process.getListeAbsence().values()) {
                        	int indiceAbs = abs.getIdDemande();
                        %>
                        <tr id="tr<%=process.getValHistory(indiceAbs)%>">
                            <td width="20px" align="center">
                            <%if(abs.getIdRefEtat()==EnumEtatAbsence.APPROUVE.getCode() && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.AS.getValue()||abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()||abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue())){ %>                            	
                            	<img onkeydown="" onkeypress="" onkeyup="" title="dupliquer" type="image" src="images/dupliquer.gif"  height="15px" width="15px" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DUPLIQUER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_DUPLIQUER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_DUPLIQUER(indiceAbs)%>" value="">
                            <%} %>
                            <%if(abs.isAffichageBoutonAnnuler()){ %>                            	
                            	<img onkeydown="" onkeypress="" onkeyup="" title="annuler" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_ANNULER_DEMANDE(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_ANNULER_DEMANDE(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_ANNULER_DEMANDE(indiceAbs)%>" value="">
                            <%} %>
							</td>  
                            <td width="30px" align="center">
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de l'absence" onClick="loadAbsenceHistory('<%=process.getValHistory(indiceAbs)%>')">
                            </td>
                            <td width="40px" align="center">
                            <%if(abs.isDepassementCompteur()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/seuil.png" height="16px" width="16px" title="Le seuil du compteur est dépassé pour cette demande.">
                            <%} %>
                            <%if(abs.isDepassementMultiple()){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/multiple.jpg" height="16px" width="16px" title="Cette demande n'est pas un multiple.">
                            <%} %>
                            </td>                            
                            <td width="30px"><%=process.getVAL_ST_MATRICULE(indiceAbs)%></td> 
                            <td width="150px"><%=process.getVAL_ST_AGENT(indiceAbs)%></td> 
                            <td width="40px"><%=process.getVAL_ST_INFO_AGENT(indiceAbs)%></td>  
                            <td width="150px"><%=process.getVAL_ST_TYPE(indiceAbs)%></td>						
                            <td width="60px"><%=process.getVAL_ST_DATE_DEB(indiceAbs)%></td>							
                            <td width="60px"><%=process.getVAL_ST_DATE_FIN(indiceAbs)%></td>							
                            <td width="40px"><%=process.getVAL_ST_DUREE(indiceAbs)%></td>							
                            <td width="120px"><%=process.getVAL_ST_MOTIF(indiceAbs)%></td>							
                            <td width="60px"><%=process.getVAL_ST_ETAT(indiceAbs)%></td>	
                            <td width="20px" align="center">
                            <%if((abs.getIdRefEtat()==EnumEtatAbsence.APPROUVE.getCode() || abs.getIdRefEtat()==EnumEtatAbsence.EN_ATTENTE.getCode()) && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.AS.getValue()||abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_EXCEP.getValue())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Valider" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VALIDER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_VALIDER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_VALIDER(indiceAbs)%>" value="">
                            <%} %>
                            <%if((abs.getIdRefEtat()==EnumEtatAbsence.A_VALIDER.getCode() || abs.getIdRefEtat()==EnumEtatAbsence.EN_ATTENTE.getCode()) && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Valider" type="image" src="images/hand-vert.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VALIDER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_VALIDER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_VALIDER(indiceAbs)%>" value="">
                            <%} %>
							</td>  
                            <td width="20px" align="center">
                            <%if((abs.getIdRefEtat()==EnumEtatAbsence.APPROUVE.getCode() || abs.getIdRefEtat()==EnumEtatAbsence.EN_ATTENTE.getCode()) && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.AS.getValue()||abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_EXCEP.getValue())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER(indiceAbs)%>" value="">
                            <%} %>                            
                            <%if((abs.getIdRefEtat()==EnumEtatAbsence.A_VALIDER.getCode() || abs.getIdRefEtat()==EnumEtatAbsence.EN_ATTENTE.getCode()) && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="Rejeter" type="image" src="images/hand-rouge.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_REJETER(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_REJETER(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER(indiceAbs)%>" value="">
                            <%} %>
							</td>  
                            <td width="20px" align="center">
                            <%if((abs.getIdRefEtat()==EnumEtatAbsence.APPROUVE.getCode()) && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.AS.getValue()||abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_EXCEP.getValue())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="En attente" type="image" src="images/clock.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>" value="">
                            <%} %>
                            <%if((abs.getIdRefEtat()==EnumEtatAbsence.A_VALIDER.getCode()) && (abs.getGroupeAbsence().getIdRefGroupeAbsence()==EnumTypeGroupeAbsence.CONGES_ANNUELS.getValue())){ %>
                            	<img onkeydown="" onkeypress="" onkeyup="" title="En attente" type="image" src="images/clock.png"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>" onclick="executeBouton('<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>');">
								<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_EN_ATTENTE(indiceAbs)%>" value="">
                            <%} %>
							</td>
                            <td width="20px" >
<%-- 								<INPUT title="pieces jointes" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceAbs)%>"> --%>
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
			<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_ACTION()%>" value="RECHERCHERAGENTACTION">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION()%>" value="SUPPRECHERCHERAGENTACTION"> 
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_AJOUTER_ABSENCE()%>" value="AJOUTERABSENCE">        
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>" value="RECHERCHERAGENTCREATION"> 
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_CALCUL_DUREE()%>" value="CALCULDUREE">
        
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
					<legend class="sigp2Legend">Création d'une absence</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
	                	<span class="sigp2Mandatory">Famille : </span>
				        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_CREATION()%>">
				        	<%=process.forComboHTML(process.getVAL_LB_FAMILLE_CREATION(), process.getVAL_LB_FAMILLE_CREATION_SELECT())%>
				        </SELECT>
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATION()%>" style="margin-right:10px;">
	                    <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>');">
	                    <BR/><BR/>
	                    <div align="center">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Creer" name="<%=process.getNOM_PB_CREATION()%>">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_MOTIF_ANNULATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
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
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
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
			
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
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
									<INPUT class="sigp2-saisie" maxlength="6" name="<%= process.getNOM_ST_DUREE() %>" size="6" type="text" value="<%= process.getVAL_ST_DUREE() %>">
									<span class="sigp2Mandatory"> heure(s)</span>
		            			</td>
		            			<% } %>
		            			<% if(typeCreation.getTypeSaisiDto().isChkDateDebut()) { %>
		            			<td>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">AM</span>
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
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">AM</span>
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
		            		<% if(typeCreation.getTypeSaisiDto().isMotif()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Motif :</span>
		            			</td>
		            			<td>
									<textarea cols="15" rows="3" name="<%=process.getNOM_ST_MOTIF_CREATION()%>" title="Zone de saisie du commentaire"><%=process.getVAL_ST_MOTIF_CREATION().trim() %></textarea>
									<%=typeCreation.getTypeSaisiDto().getInfosComplementaires() %>
		            			</td>
		            		</tr>
		            		<% } %>
		            		<% if(typeCreation.getTypeSaisiDto().isPieceJointe()) { %>
		            		<tr>
		            			<td>
<!-- 	                        		<span class="sigp2Mandatory">Pièce jointe :</span> -->
		            			</td>
		            			<td><!-- TODO --></td>
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
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_AM()) %>  onclick="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');"><span class="sigp2Mandatory">AM</span>
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
									<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_AM()) %>  onclick="executeBouton('<%=process.getNOM_PB_CALCUL_DUREE()%>');"><span class="sigp2Mandatory">AM</span>
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
									<INPUT class="sigp2-saisie" disabled="disabled" maxlength="6" name="<%= process.getNOM_ST_DUREE() %>" size="6" type="text" value="<%= process.getVAL_ST_DUREE() %>">
									<span class="sigp2Mandatory"> jour(s)</span>
		            			</td>
		            		</tr>
		            		<tr>
		            			<td>
	                        		<span class="sigp2Mandatory">Commentaire :</span>
		            			</td>
		            			<td colspan="2">
									<textarea cols="15" rows="3" name="<%=process.getNOM_ST_MOTIF_CREATION()%>" title="Zone de saisie du commentaire"><%=process.getVAL_ST_MOTIF_CREATION().trim() %></textarea>
									<span class="sigp2Mandatory">Précisez si l'agent est joignable ou non durant son congé.</span>
		            			</td>
		            		</tr>
		            	</table>
		            	<%} %>
		            	<BR/>
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CREATION_DEMANDE() %>">
	                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER() %>">
	            </FIELDSET>
			<% } %>
			<!-- ------------------------ CREATION D UNE DEMANDE ----------------------------- -->
			
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_VALIDER_ALL()%>" value="">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_REJETER_ALL()%>" value="">
        </FORM>
    </BODY>
</HTML>