<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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


            <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
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
                                      {"bSearchable": false,"bSortable": false},
                                      {"bSearchable": false,"bSortable": false},
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null
                        ],
                        "sDom": '<"H"flip>t<"F"rip>',
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
                            "sInfoEmpty": "Aucune information de pointage à afficher",
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




                function loadAbsenceHistory(absId, list) {
                    var oTable = $('#VisualisationAbsenceList').dataTable();
                    var tr = document.getElementById('tr' + absId);

                    if (oTable.fnIsOpen(tr)) {
                        oTable.fnClose(tr);
                    } else {
                        oTable.fnOpen(tr, buildDetailTable(list));
                    }
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
                            .attr("style", "margin-left: 380px;")
                            .attr("width", "500px")
                            .append($(document.createElement("thead")).append($(document.createElement("tr"))
                            		.append($(document.createElement("td")).html("Date demande"))
                            		.append($(document.createElement("td")).html("Date debut"))
                            		.append($(document.createElement("td")).html("Date fin"))
                            		.append($(document.createElement("td")).html("Durée"))
                            		.append($(document.createElement("td")).html("Etat"))
                            		.append($(document.createElement("td")).html("Date etat")))
                            );

                    // Append the tbody element
                    var tbody = $(document.createElement("tbody")).appendTo(detailTable);

                    var pointages = data.split("|");

                    //  alert(" pointages.length " +pointages.length);
                    for (var i = 0; i < pointages.length; i++) {
                        var donnees = pointages[i].split(",");
                        tbody.append($(document.createElement("tr"))
                                .addClass(i % 2 == 0 ? "even" : "odd")
                                .append($(document.createElement("td")).html(donnees[0]).attr("style", "width: 80px;"))
                                .append($(document.createElement("td")).html(donnees[1]).attr("style", "width: 80px;"))
                                .append($(document.createElement("td")).html(donnees[2]).attr("style", "width: 80px;"))
                                .append($(document.createElement("td")).html(donnees[3]).attr("style", "width: 50px;"))
                                .append($(document.createElement("td")).html(donnees[4]).attr("style", "width: 50px"))
                                .append($(document.createElement("td")).html(donnees[5]))
                                );
                    }
                    // Append the detail table into the detail container
                    var detailContainer = detailTable;
                    // Finally return the table
                    return detailContainer;
                }

            </SCRIPT>		
            <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        </HEAD>
        <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%=process.getFocus()%>')">	
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">		
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Filtres pour l'affichage</legend>
                <table>
                	<tr>
                		<td width="75px">
                			<span class="sigp2">Agent :</span>
                		</td>
                		<td width="100px">
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_DEMANDE()%>" size="4" maxlength="4" type="text" value="<%= process.getVAL_ST_AGENT_DEMANDE()%>">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>');">
                		</td>
                		<td width="70px">
                 			<span class="sigp2">Service :</span>
                		</td>
                		<td>
			                <INPUT id="service" class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_SERVICE()%>" size="8" type="text" value="<%= process.getVAL_EF_SERVICE()%>">
			                <img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
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
                		<td width="100px">
                			<span class="sigp2">Action faite par :</span>
                		</td>
                		<td width="100px">
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_ACTION()%>" size="4" maxlength="4" type="text" value="<%= process.getVAL_ST_AGENT_ACTION()%>">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_ACTION()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION()%>');">
                		</td>
                	</tr>
                	<tr>
                		<td width="75px">
               				<span class="sigp2">Date début : </span>
                		</td>
                		<td width="130px">
			                <input id="<%=process.getNOM_ST_DATE_MIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MIN()%>" >
			                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MIN()%>', 'dd/mm/y');">
                		</td>
                		<td width="75px">
               				<span class="sigp2">Date fin : </span>
                		</td>
                		<td width="130px">
			                <input id="<%=process.getNOM_ST_DATE_MAX()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MAX()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MAX()%>" >
			                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MAX()%>', 'dd/mm/y');">
                		</td>
                		<td width="35px">
                			<span class="sigp2">Famille : </span>
                		</td>
                		<td width="150px">
			                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE()%>" style="width:240px;">
			                    <%=process.forComboHTML(process.getVAL_LB_FAMILLE(), process.getVAL_LB_FAMILLE_SELECT())%>
			                </SELECT>
                		</td>
                	</tr>
                </table>  
                <BR/>         	
                <INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>">		
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
                <table cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationAbsenceList"> 
                    <thead>
                        <tr>
                            <th>
                            	<img src="images/ajout.gif" height="16px" width="16px" title="Creer une absence" onClick="executeBouton('<%=process.getNOM_PB_AJOUTER_ABSENCE()%>')" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
            				</th>  
                            <th> <img	src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de l'absence"></th>
                            <th>Matr</th>
                            <th>Agent</th>
                            <th>Cat<br>Statut</th>
                            <th>Type absence<br>Date demande</th>
                            <th>Début</th>
                            <th>Fin</th>
                            <th>Durée</th>
                            <th>Motif</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%    for (int indiceAbs : process.getListeAbsence().keySet()) {
                        %>
                        <tr id="tr<%=process.getValHistory(indiceAbs)%>">
                            <td align="center">&nbsp;</td>  
                            <td align="center">
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique de l'absence" onClick="loadAbsenceHistory('<%=process.getValHistory(indiceAbs)%>', '<%=process.getHistory(indiceAbs)%>')">
                            </td>
                            <td><%=process.getVAL_ST_MATRICULE(indiceAbs)%></td> 
                            <td><%=process.getVAL_ST_AGENT(indiceAbs)%></td> 
                            <td><%=process.getVAL_ST_INFO_AGENT(indiceAbs)%></td>  
                            <td><%=process.getVAL_ST_TYPE(indiceAbs)%></td>						
                            <td><%=process.getVAL_ST_DATE_DEB(indiceAbs)%></td>							
                            <td><%=process.getVAL_ST_DATE_FIN(indiceAbs)%></td>							
                            <td><%=process.getVAL_ST_DUREE(indiceAbs)%></td>							
                            <td><%=process.getVAL_ST_MOTIF(indiceAbs)%></td>			
                        </tr>
                        <%}%>
                    </tbody>
                </table>
                <BR/>	
            </FIELDSET>	
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
	            <legend class="sigp2Legend">Création d'une absence</legend>
                	<span class="sigp2">Famille : </span>
			        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_CREATION()%>" style="width:240px;">
			        	<%=process.forComboHTML(process.getVAL_LB_FAMILLE_CREATION(), process.getVAL_LB_FAMILLE_CREATION_SELECT())%>
			        </SELECT>
                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATION()%>" style="margin-right:10px;">
                    <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>');">
                    <span class="sigp2Mandatory" style="width:80px"></span>
                    <INPUT type="submit" class="sigp2-Bouton-100" value="Creer" name="<%=process.getNOM_PB_CREATION()%>">	 
                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	            </FIELDSET>
            <%} %>
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION_A48)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;">
	            <legend class="sigp2Legend"><%=process.ACTION_CREATION_A48 %></legend>
	            	<table>
	            		<tr>
	            			<td width="80px">
                        		<span class="sigp2Mandatory">Date début : </span>
	            			</td>
	            			<td>
		                        <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEBUT()%>" >
		                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
	            			</td>
	            			<td>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">AM</span>
	            			</td>
	            		</tr>
	            		<tr>
	            			<td>
                        		<span class="sigp2Mandatory">Date fin : </span>
	            			</td>
	            			<td>
		                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN()%>" >
		                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
	            			</td>
	            			<td>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">AM</span>
	            			</td>
	            		</tr>
	            	</table>
	            	<BR/>
                    <INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">	 
                    <INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	            </FIELDSET>
            <%} %>
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_DEMANDE()%>" value="RECHERCHERAGENTDEMANDE">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_DEMANDE()%>" value="SUPPRECHERCHERAGENTDEMANDE">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_ACTION()%>" value="RECHERCHERAGENTACTION">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_ACTION()%>" value="SUPPRECHERCHERAGENTACTION"> 
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_ABSENCE()%>" value="AJOUTERABSENCE">        
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>" value="RECHERCHERAGENTCREATION">        
        </FORM>
    </BODY>
</HTML>