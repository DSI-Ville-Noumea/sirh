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
        <TITLE>Ventilation des contractuels</TITLE>		

        <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>

        <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
        <SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
        <SCRIPT language="javascript">

            $(document).ready(function() {
                $('#VentilationTable').dataTable({
                    "sDom": '<"H"fl>t<"F"Trip>',
                    "sPaginationType": "full_numbers",
                    "oLanguage": {
                        "oPaginate": {
                            "sFirst": "D�but",
                            "sLast": "Fin",
                            "sNext": "Suivant",
                            "sPrevious": "Pr�c�dent"
                        },
                        "sZeroRecords": "Aucune information de ventilation � afficher",
                        "sInfo": "Affichage de _START_ � _END_ des _TOTAL_ ventilation(s) au total",
                        "sInfoEmpty": "Aucune information de ventilation � afficher",
                        "sEmptyTable": "Veuillez s�lectionner au moins un agent pour afficher les informations de ventilation",
                        "sInfoFiltered": "(filtrage sur _MAX_ ventilation au total)",
                        "sLengthMenu": "Affichage de _MENU_ ventilation par page",
                        "sSearch": "Recherche instantan�e"
                    },
                    "oTableTools": {
                        "aButtons": [{"sExtends": "xls", "sButtonText": "Export Excel", "mColumns": "visible", "sTitle": "ventilVisu", "sFileName": "*.xls"}], //OU : "mColumns":[0,1,2,3,4]
                        "sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
                    }

                });
            });


            //afin de s�lectionner un �l�ment dans une liste
            function executeBouton(nom)
            {
                document.formu.elements[nom].click();
            }

            // afin de mettre le focus sur une zone pr�cise
            function setfocus(nom)
            {
                if (document.formu.elements[nom] != null)
                    document.formu.elements[nom].focus();
            }

            function unavailable() {
                alert("Le service demand� n'est actuellement pas disponible.\n Veuillez revenir sur cette page ult�rieurement");
            }
            
            function available() {
                alert("ok");
            }

        </SCRIPT>		
        <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </HEAD>
    <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGVentilationContractuels" id="process" scope="session"></jsp:useBean>
        <BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">

            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <div style="margin-left:10px;margin-top:20px;text-align:left;width:1030px" align="left">
                <% if (process.onglet.equals("ONGLET1")) {%>
                <span id="titreOngletVentilation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET1');">&nbsp;Ventilation&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletVentilation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET1');">&nbsp;Ventilation&nbsp;</span>&nbsp;&nbsp;
                <% }%>
                <% if (process.onglet.equals("ONGLET2")) {%>
                <span id="titreOngletHS" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET2');">&nbsp;Heures suppl�mentaires&nbsp;</span>&nbsp;&nbsp;
                <% } else {%>
                <span id="titreOngletHS" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');
                afficheOnglet('ONGLET2');">&nbsp;Heures suppl�mentaires&nbsp;</span>&nbsp;&nbsp;
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
                <% } else {%>
                <div id="corpsOngletVentilation" title="Ventilation" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                    <% }%>
                    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                        <legend class="sigp2Legend">Ventilation des pointages des contractuels</legend>	
                        <%=process.getVentil()%>		
                    </FIELDSET>

                </div>


                <% if (process.onglet.equals("ONGLET2")) {%>
                <div id="corpsOngletHS" title="Heures suppl�mentaires" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                    <% } else {%>
                    <div id="corpsOngletHS" title="Heures suppl�mentaires" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                        <% }%>
                        <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                            <legend class="sigp2Legend">Visualisation de la ventilation des heures suppl�mentaires</legend>	
                            <%=process.getTab(2)%>		
                        </FIELDSET>
                    </div>


                    <% if (process.onglet.equals("ONGLET3")) {%>
                    <div id="corpsOngletPrimes" title="Primes" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                        <% } else {%>
                        <div id="corpsOngletPrimes" title="Primes" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                            <% }%>
                            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                                <legend class="sigp2Legend">Visualisation de la ventilation des primes</legend>	
                                <%=process.getTab(3)%>		
                            </FIELDSET>
                        </div>


                        <% if (process.onglet.equals("ONGLET4")) {%>
                        <div id="corpsOngletAbs" title="Absences" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                            <% } else {%>
                            <div id="corpsOngletAbs" title="Absences" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                                <% }%>
                                <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                                    <legend class="sigp2Legend">Visualisation de la ventilation des absences</legend>	
                                    <%=process.getTab(1)%>		
                                </FIELDSET>
                            </div>


                            <% if (process.onglet.equals("ONGLET5")) {%>
                            <div id="corpsOngletValidation" title="Validation" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
                                <% } else {%>
                                <div id="corpsOngletValidation" title=""Validation"" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
                                     <% }%>
                                     <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">	
                                        <legend class="sigp2Legend">Validation de la ventilation des pointages</legend>	
                                        <%=process.getValid()%>		
                                    </FIELDSET>
                                </div>


                                <INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">
                                </FORM>
                                </BODY>
                                </HTML>