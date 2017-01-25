<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.gestionagent.pointage.dto.DpmIndemniteAnneeDto" %>
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
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGPrimeDpmParametrage" id="process" scope="session"></jsp:useBean>
            <TITLE>Paramétrage des campagnes de choix concernant la prime DPM SDJF</TITLE>		


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
                           {"bSearchable": true, "bSortable": false, "sWidth": "20px", "sClass" : "center"},
                           {"bSearchable": true, "bSortable": true, "sWidth": "50px", "sClass" : "center"},
                           {"bSearchable": true, "bSortable": true, "sWidth": "60px", "sClass" : "center"},
                           {"bSearchable": true, "bSortable": true, "sWidth": "60px", "sClass" : "center"}
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
            
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend">Visualisation des choix agent pour la prime DPM SDJF</legend>
                <BR/>
                <table width="190px" cellpadding="0" cellspacing="0" border="0" class="display" id="VisualisationTitreRepasList"> 
                    <thead>
                        <tr>
                            <th width="20px" align="center">
                            	<img src="images/ajout.gif" height="16px" width="16px" title="Ajouter une période." 
	                            	onClick="executeBouton('<%=process.getNOM_PB_CREATION()%>')" 
	                            	class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
	                            	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CREATION()%>" value="">
	                        </th>
                            <th>Année</th>
                            <th>Date de début</th>
                            <th>Date de fin</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map.Entry<Integer, DpmIndemniteAnneeDto> trMap : process.getListeDpmAnnee().entrySet()) {
                        	DpmIndemniteAnneeDto TR = trMap.getValue();
                        	int indiceTR = trMap.getKey();
                        %>
                        <tr id="tr<%=TR.getIdDpmIndemAnnee() %>">
                            <td width="20px" align="center">
                            	<img onkeydown="" onkeypress="" onkeyup="" src="images/modifier.gif" height="15px" width="15px" title="Editer le pointage" 
                            		onClick="executeBouton('<%=process.getNOM_PB_SAISIE_ANNEE(indiceTR) %>')" 
                            		class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>">
                            	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SAISIE_ANNEE(indiceTR)%>" value="">
                            </td>
                            <td width="50px"><%=process.getVAL_ST_ANNEE(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_DEBUT(indiceTR)%></td>
                            <td width="60px"><%=process.getVAL_ST_DATE_FIN(indiceTR)%></td>
                        </tr>
                        <%}%>
                    </tbody>
                </table>
                <BR/>	
            </FIELDSET>
            
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_AJOUTER_CHOIX_DPM()%>" value="AJOUTERABSENCE">
        
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_CREATION %>">
					<legend class="sigp2Legend">Création d'une nouvelle ouverture kiosque pour les primes DPM 7718 et 7719</legend>
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
           				
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Année :</span>
	                    <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_ANNEE()%>" size="10" type="text" value="<%= process.getVAL_ST_ANNEE()%>" style="margin-right:10px;">
	                    
	                    <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	
                        	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DEBUT()%>" >
                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
		                        
                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	
                        	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_FIN()%>" >
                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">

	                    <BR/>
	                    
	                    <div align="center">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Créer" name="<%=process.getNOM_PB_CREATION()%>">
		                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	                    </div>
	            </FIELDSET>
            <%} %>
            
            <%if(process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
            
				<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.ACTION_MODIFICATION %>">
					<legend class="sigp2Legend">Modification d'une ouverture kiosque pour les primes DPM 7718 et 7719</legend>
           				<INPUT name="<%= process.getNOM_ST_INDICE_ANNEE() %>" type="hidden" value="<%= process.getVAL_ST_INDICE_ANNEE()%>">
           				<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
           				
	                    <span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Année :</span>
	                    <INPUT class="sigp2-saisie" readonly="readonly" disabled="disabled" name="<%= process.getNOM_ST_ANNEE()%>" size="10" type="text" value="<%= process.getVAL_ST_ANNEE()%>" style="margin-right:10px;">
	                    
	                     <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	
                        	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DEBUT()%>" >
                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
		                        
                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	
                        	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_FIN()%>" >
                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
	                    
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