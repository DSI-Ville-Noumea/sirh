<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>

    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
       <TITLE>Ventilation des contractuels</TITLE>		

		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
        <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
        <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

        <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
        <SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT> 
        <SCRIPT language="javascript">

        $(document).ready(function() {
           $('#VentilationTable').dataTable({
                "sDom": '<"H"fl>t<"F"Trip>',
                "oLanguage": {
                    "oPaginate": {
                        "sFirst": "",
                        "sLast": "",
                        "sNext": "",
                        "sPrevious": ""
                    },
                    "sZeroRecords": "Aucune information de ventilation à afficher",
                    "sInfo": "Affichage de _START_ à _END_ des _TOTAL_ ventilation(s) au total",
                    "sInfoEmpty": "Aucune information de ventilation à afficher",
                    "sEmptyTable": "Veuillez sélectionner au moins un agent pour afficher les informations de ventilation",
                    "sInfoFiltered": "(filtrage sur _MAX_ ventilation au total)",
                    "sLengthMenu": "Affichage de _MENU_ ventilation par page",
                    "sSearch": "Recherche instantanée"
                },
                "oTableTools": {
                    "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "ventilContractuels", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                    "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                }

            });    
        });

        $(document).ready(function() {
           $('#VentilationTableAbs').dataTable({
                "sDom": '<"H"fl>t<"F"Trip>',
                "oLanguage": {
                    "oPaginate": {
                        "sFirst": "",
                        "sLast": "",
                        "sNext": "",
                        "sPrevious": ""
                    },
                    "sZeroRecords": "Aucune information de ventilation à afficher",
                    "sInfo": "Affichage de _START_ à _END_ des _TOTAL_ ventilation(s) au total",
                    "sInfoEmpty": "Aucune information de ventilation à afficher",
                    "sEmptyTable": "Veuillez sélectionner au moins un agent pour afficher les informations de ventilation",
                    "sInfoFiltered": "(filtrage sur _MAX_ ventilation au total)",
                    "sLengthMenu": "Affichage de _MENU_ ventilation par page",
                    "sSearch": "Recherche instantanée"
                },
                "oTableTools": {
                    "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "ventilContractuels", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                    "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                }

            });    
        });




        function loadVentilationAbsHistory(id, list) {
            var oTable = $('#VentilationTableAbs').dataTable();
            var tr = document.getElementById(id);

            if (oTable.fnIsOpen(tr)) {
                oTable.fnClose(tr);
            } else {
                oTable.fnOpen(tr, buildDetailTable(list));
            }
        }

        /**
         * Build the table containing the detail of the pointage
         */
        function buildDetailTable(data) {

            // Build the new table that will handle the detail (append the thead and all tr, th)
            var detailTable = $(document.createElement("table"))
                    .addClass("detailContent")
                    .addClass("subDataTable")
                    .attr("cellpadding", "0")
                    .attr("cellspacing", "0")
                    .attr("style", "margin-left: 318px;")
                    .append($(document.createElement("thead"))
                    );

            // Append the tbody element
            var tbody = $(document.createElement("tbody")).appendTo(detailTable);

            var pointages = data.split("|");

            //  alert(" pointages.length " +pointages.length);
            for (var i = 0; i < pointages.length; i++) {
                var donnees = pointages[i].split(",");
                tbody.append($(document.createElement("tr"))
                        .addClass(i % 2 == 0 ? "even" : "odd")
                        .append($(document.createElement("td")).html(donnees[0]).attr("style", "width: 93px;"))
                        .append($(document.createElement("td")).html(donnees[1]).attr("style", "width: 93px;"))
                        .append($(document.createElement("td")).html(donnees[2]).attr("style", "width: 93px;"))
                        .append($(document.createElement("td")).html(donnees[3]).attr("style", "width: 93px;"))
                        .append($(document.createElement("td")).html(donnees[4]).attr("style", "width: 93px"))
                        );
            }
            // Append the detail table into the detail container
            var detailContainer = detailTable;
            // Finally return the table
            return detailContainer;
        }


        $(document).ready(function() {
           $('#VentilationTableHsupNonTitulaire').dataTable({
                "sDom": '<"H"fl>t<"F"Trip>',
                "bAutoWidth": false,   
                "aoColumns": [
                              {"sWidth": "65px"},
                              {"sWidth": "200px"},
                              {"sWidth": "50px"},
                              {"sWidth": "70px"},
                              {"sWidth": "40px"},
                              {"sWidth": "70px"},
                              {"sWidth": "70px"},
                              {"sWidth": "70px"},
                              {"sWidth": "70px"},
                              {"sWidth": "70px"},
                              {"sWidth": "70px"},
                              {"sWidth": "70px"},
                              {"sWidth": "20px"}
                ],                 
                "oLanguage": {
                    "oPaginate": {
                        "sFirst": "",
                        "sLast": "",
                        "sNext": "",
                        "sPrevious": ""
                    },
                    "sZeroRecords": "Aucune information de ventilation à afficher",
                    "sInfo": "Affichage de _START_ à _END_ des _TOTAL_ ventilation(s) au total",
                    "sInfoEmpty": "Aucune information de ventilation à afficher",
                    "sEmptyTable": "Veuillez sélectionner au moins un agent pour afficher les informations de ventilation",
                    "sInfoFiltered": "(filtrage sur _MAX_ ventilation au total)",
                    "sLengthMenu": "Affichage de _MENU_ ventilation par page",
                    "sSearch": "Recherche instantanée"
                },
                "oTableTools": {
                    "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "ventilContractuels", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                    "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                }

            });    
        });




        function loadVentilationHsupHistory(id, list) {
            var oTable = $('#VentilationTableHsupNonTitulaire').dataTable();
            var tr = document.getElementById(id);

            if (oTable.fnIsOpen(tr)) {
                oTable.fnClose(tr);
            } else {
                oTable.fnOpen(tr, buildDetailTableHsup(list));
            }
        }

        /**
         * Build the table containing the detail of the pointage
         */
        function buildDetailTableHsup(data) {

            // Build the new table that will handle the detail (append the thead and all tr, th)
            var detailTable = $(document.createElement("table"))
                    .addClass("detailContent")
                    .addClass("subDataTable")
                    .attr("cellpadding", "0")
                    .attr("cellspacing", "0")
                    .attr("style", "margin-left: 302px;")
                    .append($(document.createElement("thead"))
                    );

            // Append the tbody element
            var tbody = $(document.createElement("tbody")).appendTo(detailTable);

            var pointages = data.split("|");

            //  alert(" pointages.length " +pointages.length);
            for (var i = 0; i < pointages.length; i++) {
                var donnees = pointages[i].split(",");
                tbody.append($(document.createElement("tr"))
                        .addClass(i % 2 == 0 ? "even" : "odd")
                        .append($(document.createElement("td")).html(donnees[0]).attr("style", "width: 55px;"))
                        .append($(document.createElement("td")).html(donnees[1]).attr("style", "width: 75px;"))
                        .append($(document.createElement("td")).html(donnees[2]).attr("style", "width: 45px;"))
                        .append($(document.createElement("td")).html(donnees[3]).attr("style", "width: 74px;"))
                        .append($(document.createElement("td")).html(donnees[4]).attr("style", "width: 75px"))
                        .append($(document.createElement("td")).html(donnees[5]).attr("style", "width: 75px"))
                        .append($(document.createElement("td")).html(donnees[6]).attr("style", "width: 73px"))
                        .append($(document.createElement("td")).html(donnees[7]).attr("style", "width: 75px"))
                        .append($(document.createElement("td")).html(donnees[8]).attr("style", "width: 73px"))
                        .append($(document.createElement("td")).html(donnees[9]).attr("style", "width: 75px"))
                        );
            }
            // Append the detail table into the detail container
            var detailContainer = detailTable;
            // Finally return the table
            return detailContainer;
        }

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
        
		function startCompteur(duree){
			<%if(OePTGVentilationUtils.isVentilationEnCours("C")){ %>
			var o=document.getElementById("box" );
			if(duree >= 0) {
				//on format les minutes et les secondes
				var minutes = Math.floor(duree/60) +"m";
				var secondes = Math.floor(duree%60)+"s";
				o.innerHTML = "Début de la ventilation dans "+ minutes +secondes;
				setTimeout("startCompteur("+duree+"-1)", 1000);
			} else {
				o.innerHTML ="Ventilation en cours.";					
			}	
			<%}%>
		}
		 
		function startCompteurDeversement(duree){
			<%if(OePTGVentilationUtils.isDeversementEnCours("C")){ %>
			var o=document.getElementById("boxDeversement");
			if(duree >= 0) {
				//on format les minutes et les secondes
				var minutes = Math.floor(duree/60) +"m";
				var secondes = Math.floor(duree%60)+"s";
				o.innerHTML = "Début de la validation dans "+ minutes +secondes;
				setTimeout("startCompteurDeversement("+duree+"-1)", 1000);
			} else {
				o.innerHTML ="Validation en cours.";					
			}	
			<%}%>
		}
		</script>
        <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </HEAD>
    <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGVentilationContractuels" id="process" scope="session"></jsp:useBean>
        <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();startCompteur('120');startCompteurDeversement('300');" >
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <div style="margin-left:10px;margin-top:20px;text-align:left;" align="left">
                <% if (process.onglet.equals("ONGLET1")) {%>
                <span id="titreOngletVentilation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET1');">&nbsp;Ventilation&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletVentilation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET1');">&nbsp;Ventilation&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET2")) {%>
                <span id="titreOngletHS" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET2');">&nbsp;Heures supplémentaires&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletHS" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET2');">&nbsp;Heures supplémentaires&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET3")) {%>
                <span id="titreOngletPrimes" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET3');">&nbsp;Primes&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletPrimes" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET3');">&nbsp;Primes&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET4")) {%>
                <span id="titreOngletAbs" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET4');">&nbsp;Absences&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletAbs" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET4');">&nbsp;Absences&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET5")) {%>
                <span id="titreOngletValidation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET5');">&nbsp;Validation&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletValidation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET5');">&nbsp;Validation&nbsp;</span>&nbsp;&nbsp;
                <% }%>
            </div>


            <% if (process.onglet.equals("ONGLET1")) {%>
            <div id="corpsOngletVentilation" title="Ventilation" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                   <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                        <legend class="sigp2Legend">Ventilation des pointages des <span style="color: red;">CONTRACTUELS</span></legend>
						<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:50px;">Date :</span>
						<%if(process.ventilationExist()){ %>
							<input class="sigp2-saisie" disabled="disabled"  maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">				
						<%}else{ %>
							<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>">
							<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');">
						<%} %>
                		<span class="sigp2Mandatory" style="width:50px;margin-left:50px;">Type :</span>
                        <INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_HS())%>>Heures supplémentaires
						<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_PRIME())%>>Primes
						<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_ABS())%>>Absences
						<INPUT type="radio" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> <%= process.forRadioHTML(process.getNOM_RG_TYPE(),process.getNOM_RB_TYPE_TOUT())%>>Tout
						<BR/><BR/>
                		<span class="sigp2" style="width:50px;margin-left:20px;">Agents :</span>
				        <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT()%>">
				       <br/>
			            <%if(process.getListeAgentsVentil()!=null && process.getListeAgentsVentil().size()>0){ %>
							<div style="overflow: auto;height: 120px;width:1000px;margin-left:20px;">
								<table class="sigp2NewTab" style="text-align:left;width:980px;">
								<%
								int indiceAgent = 0;
									for (int i = 0;i<process.getListeAgentsVentil().size();i++){
								%>
										<tr id="<%=indiceAgent%>" onmouseover="SelectLigne(<%=indiceAgent%>,<%=process.getListeAgentsVentil().size()%>)" >
											<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">											
												<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT(indiceAgent)%>">
											</td>
											<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_AGENT(indiceAgent)%></td>
										</tr>
										<%
										indiceAgent++;
									}
								%>
								</table>	
							</div>
						<br/>
						<%} %>
						<BR/><BR/>
						<%if(OePTGVentilationUtils.canProcessVentilation("C")){ %>
						<INPUT type="submit" class="sigp2-Bouton-100" value="Ventiler" name="<%=process.getNOM_PB_VENTILER()%>">
						<%}else{ %>		
							<%if(OePTGVentilationUtils.isDeversementEnCours("C")){ %>
		             			<span style="color: red;">Une validation est en cours. Vous ne pouvez effectuer de ventilation.</span>
							<% }else if(OePTGVentilationUtils.isVentilationEnCours("C")){ %>
								<INPUT type="submit" class="sigp2-Bouton-200" value="En cours, rafraichîr" name="<%=process.getNOM_PB_RAFRAICHIR()%>">
		             			<span style="color: red;" id="box"></span>
	             			<%} %>
						<%} %>	
             			<span style="color: red;" id="box"></span>	
                    </FIELDSET>
                    <br/><br/>
                    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                        <legend class="sigp2Legend">Erreurs eventuelles sur la ventilation des pointages des contractuels</legend>
                        <%=process.getTabErreurVentil()%>		
                    </FIELDSET>
                <% } else {%>
                <div id="corpsOngletVentilation" title="Ventilation" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                    <% }%>
                 </div>


                <% if (process.onglet.equals("ONGLET2")) {%>
                <div id="corpsOngletHS" title="Heures supplémentaires" class="OngletCorps" style="display:block;margin-right:10px;width:1500px;">
            		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                            <legend class="sigp2Legend">Filtres des heures supplémentaires</legend>		
			                <span class="sigp2" style="width:100px">Agent min :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10"  maxlength="4"  type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
							<span class="sigp2" style="width:100px">Agent max :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10"  maxlength="4"  type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER_VENTIL(2)%>">
                 	</FIELDSET>
                    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1300px;">	
	                	<legend class="sigp2Legend">Visualisation de la ventilation des heures supplémentaires des <span style="color: red;">CONTRACTUELS</span></legend>	
	                	<%@ include file="TabVentilationHsupNonTitulaire.jsp" %>
	                </FIELDSET>
                   <% } else {%>
                <div id="corpsOngletHS" title="Heures supplémentaires" class="OngletCorps" style="display:none;margin-right:10px;">
                <% }%> </div>


                 <% if (process.onglet.equals("ONGLET3")) {%>
                 <div id="corpsOngletPrimes" title="Primes" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                            <legend class="sigp2Legend">Filtres des primes</legend>		
			                <span class="sigp2" style="width:100px">Agent min :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10"  maxlength="4"  type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
							<span class="sigp2" style="width:100px">Agent max :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10"  maxlength="4"  type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER_VENTIL(3)%>">
                 	</FIELDSET>
                 	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                    	<legend class="sigp2Legend">Visualisation de la ventilation des primes des <span style="color: red;">CONTRACTUELS</span></legend>	
                        <%=process.getTabVisuP()%>		
                    </FIELDSET>
                  <% } else {%>
                 <div id="corpsOngletPrimes" title="Primes" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                 <% }%>
                </div>


                <% if (process.onglet.equals("ONGLET4")) {%>
                <div id="corpsOngletAbs" title="Absences" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                 	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
                            <legend class="sigp2Legend">Filtres des absences</legend>		
			                <span class="sigp2" style="width:100px">Agent min :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MIN()%>" size="10"  maxlength="4"  type="text" value="<%= process.getVAL_ST_AGENT_MIN()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>');">
							<span class="sigp2" style="width:100px">Agent max :</span>
			                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_MAX()%>" size="10"  maxlength="4"  type="text" value="<%= process.getVAL_ST_AGENT_MAX()%>" style="margin-right:10px;">
			                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>');">
			                <img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>');">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER_VENTIL(1)%>">
                 	</FIELDSET>	                
	                <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
	                	<legend class="sigp2Legend">Visualisation de la ventilation des absences des <span style="color: red;">CONTRACTUELS</span></legend>
	                	<%@ include file="TabVentilationAbs.jsp" %>        
	                </FIELDSET>
             <% } else {%>
                <div id="corpsOngletAbs" title="Absences" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                <% }%>
                  </div>


                <% if (process.onglet.equals("ONGLET5")) {%>
                <div id="corpsOngletValidation" title="Validation" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                <% } else {%>
                <div id="corpsOngletValidation" title="Validation" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                <% }%>
	                <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
		                <legend class="sigp2Legend">Validation de la ventilation des pointages des <span style="color: red;">CONRACTUELS</span></legend>	
		              	<span style="color: red;">Attention , cette action est irreversible !</span>    
		              	<br>  <br>         
		              	<%if(OePTGVentilationUtils.isVentilationEnCours("C")){ %>
		             		<span style="color: red;">Une ventilation est en cours. Vous ne pouvez effectuer de validation.</span>		              	
		              	<%}else if(OePTGVentilationUtils.canProcessDeversementPaie("C")){ %>
		              		<INPUT type="submit" class="sigp2-Bouton-200" value="Deverser dans la paie" name="<%=process.getNOM_PB_DEVERSER()%>">
		              	<% }else if(OePTGVentilationUtils.isDeversementEnCours("C")){%>
								<INPUT type="submit" class="sigp2-Bouton-200" value="En cours, rafraichîr" name="<%=process.getNOM_PB_RAFRAICHIR()%>">
		             			<span style="color: red;" id="boxDeversement"></span>
		              	<% } %>
	                </FIELDSET>
                </div>
                
            <INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">

            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MIN()%>" value="RECHERCHERAGENTMIN">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN()%>" value="SUPPRECHERCHERAGENTMIN">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_MAX()%>" value="RECHERCHERAGENTMAX">
            <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX()%>" value="SUPPRECHERCHERAGENTMAX">	
			<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET1" id="ONGLET1" style="visibility: hidden;">
			<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET2" id="ONGLET2" style="visibility: hidden;">	
			<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET3" id="ONGLET3" style="visibility: hidden;">
			<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET4" id="ONGLET4" style="visibility: hidden;">
			<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET5" id="ONGLET5" style="visibility: hidden;">
	</FORM>
</BODY>
</HTML>