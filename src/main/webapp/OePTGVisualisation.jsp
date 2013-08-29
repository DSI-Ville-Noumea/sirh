<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.dto.ConsultPointageDto"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>

    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
        <LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGVisualisation" id="process" scope="session"></jsp:useBean>
            <TITLE>Visualisation des pointages</TITLE>		

            <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
            <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
            <script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

            <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
            <SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>
            <SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>

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

                $(document).ready(function() {
                    $('#VisualisationPointageList').dataTable({
                        "aoColumns": [
                            {"bSortable": false},
                            {"bSortable": false},
                            {"bSortable": true},
                            {"bSortable": true},
                            {"bSortable": true},
                            {"bSortable": true},
                            {"bSortable": true},
                            {"bSortable": true},
                            {"bSortable": false},
                            {"bSortable": false},
                            {"bSortable": false},
                            {"bSortable": false},
                            {"bSortable": false},
                            {"bSortable": false}
                        ],
                        "sDom": '<"H"fl>t<"F"rip>',
                        "oLanguage": {
                            "oPaginate": {
                                "sFirst": "",
                                "sLast": "",
                                "sNext": "",
                                "sPrevious": ""
                            },
                            "sZeroRecords": "Aucune information de pointage � afficher",
                            "sInfo": "Affichage de _START_ � _END_ des _TOTAL_ pointages au total",
                            "sInfoEmpty": "Aucune information de pointage � afficher",
                            "sEmptyTable": "Veuillez s�lectionner une date de d�but et une date de fin pour afficher les informations de pointage",
                            "sInfoFiltered": "(filtrage sur _MAX_ pointages au total)",
                            "sLengthMenu": "Affichage de _MENU_ pointages par page",
                            "sSearch": "Recherche instantan�e"
                        },
                        "oTableTools": {
                            "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "pointageVisu", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                            "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                        }

                    });
                });




                function loadPointageHistory(pointageId, list) {
                    //alert('Historique du pointage : #tr'+pointageId);
                    var oTable = $('#VisualisationPointageList').dataTable();
                    var tr = document.getElementById('tr' + pointageId);

                    if (oTable.fnIsOpen(tr)) {
                        oTable.fnClose(tr);
                    } else {
                        oTable.fnOpen(tr, buildDetailTable(list));
                    }
                }

                function launchJSPSaisie(idAgent, date) {
                    document.location.href = './OePTGSaisie.jsp?idAgent=' + idAgent + '&dateLundi=' + date;
                    //TODO	ArrayList<AgentNW> listeEvaluateurSelect = (ArrayList<AgentNW>) VariablesActivite.recuperer(this, "EVALUATEURS");

                }

                /**
                 * Build the table containing the detail of the pointage
                 */
                function buildDetailTable(data) {

                    var detailContainer = $(document.createElement("div"));

                    // Build the new table that will handle the detail (append the thead and all tr, th)
                    var detailTable = $(document.createElement("table"))
                            .addClass("detailContent")
                            .addClass("subDataTable")
                            .attr("cellpadding", "0")
                            .attr("cellspacing", "0")
                            .css({width: "100%"})
                            .append($(document.createElement("thead"))
                            .append($(document.createElement("tr"))
                            .append($(document.createElement("th")).html("Date"))
                            .append($(document.createElement("th")).html("D&eacute;but"))
                            .append($(document.createElement("th")).html("Fin"))
                            .append($(document.createElement("th")).html("Quantit&eacute;"))
                            .append($(document.createElement("th")).html("Motif<br />Commentaire"))
                            .append($(document.createElement("th")).html("Etat"))
                            .append($(document.createElement("th")).html("Date de saisie"))
                            ));

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
                    detailContainer.append(detailTable);
                    // Finally return the table
                    return detailContainer;
                }






            </SCRIPT>		
            <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        </HEAD>
        <BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">		
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">


            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Filtres pour l'affichage</legend>
                <span class="sigp2Mandatory" style="width:80px">Date d�but : </span>
                <input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MIN()%>" >
                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MIN()%>', 'dd/mm/y');">
                <span class="sigp2Mandatory" style="width:80px"></span>
                <span class="sigp2" style="width:100px">Agent min :</span>
                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
                <span class="sigp2Mandatory" style="width:80px"></span>
                <span class="sigp2" style="width:40px">Etat : </span>
                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT()%>" width=150px;margin-right:20px;">
                    <%=process.forComboHTML(process.getVAL_LB_ETAT(), process.getVAL_LB_ETAT_SELECT())%>
                </SELECT>
                <BR/><BR/>
                <span class="sigp2" style="width:80px">Date fin : </span>
                <input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MAX()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MAX()%>" >
                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MAX()%>', 'dd/mm/y');">
                <span class="sigp2Mandatory" style="width:80px"></span>
                <span class="sigp2" style="width:100px">Agent max :</span>
                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
                <span class="sigp2Mandatory" style="width:80px"></span>
                <span class="sigp2" style="width:40px">Type : </span>
                <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE()%>" width=150px;margin-right:20px;">
                    <%=process.forComboHTML(process.getVAL_LB_TYPE(), process.getVAL_LB_TYPE_SELECT())%>
                </SELECT>
                <BR/><BR/>
                <span class="sigp2" style="width:75px;">Service :</span>
                <INPUT tabindex="" id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE()%>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE()%>">
                <img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
                <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
                <INPUT type="hidden" id="codeservice" size="4" name="<%=process.getNOM_ST_CODE_SERVICE()%>" 
                       value="<%=process.getVAL_ST_CODE_SERVICE()%>" class="sigp2-saisie">
                <div id="treeHierarchy" style="display: none;margin-left:100px;margin-top:20px; height: 340; width: 500; overflow:auto; background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;">
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
                <BR/><BR/>
                <INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>">		
                <BR/><BR/>				
            </FIELDSET>

            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend">Visualisation des pointages</legend>
                <BR/>
                <table cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationPointageList"> 
                    <thead>
                        <tr>
                            <th> <img	src="images/modifier.gif" height="16px" width="16px" title="Editer le pointage"></th>
                            <th> <img	src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique du pointage"></th>
                            <th>Agent </th>
                            <th>Type</th>
                            <th>Date</th>
                            <th>D�but</th>
                            <th>Fin</th>
                            <th>Quantit�</th>
                            <th>Motif<br>Commentaires</th>
                            <th>Etat</th>
                            <th>Date de saisie</th>
                            <th align="center"><INPUT tabindex="" title="Approuver tous les pointages" type="image"	src="images/valid.png"	class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
                                                      height="16px" width="16px"	name="<%=process.getVal_ValidAll()%>"></th>
                            <th align="center"><INPUT tabindex="" type="image"  title="Refuser tous les pointages" 	src="images/del.png"	class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
                                                      height="16px" width="16px"	name="<%=process.getVal_DelAll()%>"></th>
                            <th align="center"><INPUT tabindex="" type="image"	 title="Mettre en attente tous les pointages" src="images/clock.png"	class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
                                                      height="16px" width="16px"	name="<%=process.getVal_DelayAll()%>"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <%    for (int indicePtg : process.getListePointage().keySet()) {
                        %>
                        <tr id="tr<%=process.getValHistory(indicePtg)%>">
                            <td><INPUT title="Editer le pointage" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/modifier.gif" height="16px" width="16px" name="<%=process.getSAISIE_PTG(indicePtg)%>"></td>  
                            <td><img	src="images/loupe.gif" height="16px" width="16px" title="Voir l'historique du pointage" onClick="loadPointageHistory('<%=process.getValHistory(indicePtg)%>', '<%=process.getHistory(indicePtg)%>')"></td>
                            <td><%=process.getVAL_ST_AGENT(indicePtg)%></td>  
                            <td><%=process.getVAL_ST_TYPE(indicePtg)%></td>
                            <td><%=process.getVAL_ST_DATE(indicePtg)%></td>							
                            <td><%=process.getVAL_ST_DATE_DEB(indicePtg)%></td>							
                            <td><%=process.getVAL_ST_DATE_FIN(indicePtg)%></td>							
                            <td><%=process.getVAL_ST_DUREE(indicePtg)%></td>							
                            <td><%=process.getVAL_ST_MOTIF(indicePtg)%></td>							
                            <td><%=process.getVAL_ST_ETAT(indicePtg)%></td>			
                            <td><%=process.getVAL_ST_DATE_SAISIE(indicePtg)%></td>			
                            <td align="center"><INPUT type="image" title="Approuver le pointage"	src="images/valid.png" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
                                                      height="16px" width="16px"	name="<%=process.getVal_Valid(indicePtg)%>"></td>
                            <td align="center"><INPUT type="image" title="Refuser le pointage"	src="images/del.png" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
                                                      height="16px" width="16px"	name="<%=process.getVal_Del(indicePtg)%>"></td>
                            <td align="center"><INPUT type="image" title="Mettre en attente le pointage" src="images/clock.png" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
                                                      height="16px" width="16px"	name="<%=process.getVal_Delay(indicePtg)%>"></td>				
                        </tr>
                        <%}%>
                    </tbody>
                </table>
                <BR/>	
            </FIELDSET>
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>" value="RECHERCHERAGENTMIN">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>" value="SUPPRECHERCHERAGENTMIN">	
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>" value="RECHERCHERAGENTMAX">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>" value="SUPPRECHERCHERAGENTMAX">	
        </FORM>
    </BODY>
</HTML>