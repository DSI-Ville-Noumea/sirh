<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
        <LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGSaisie" id="process" scope="session"></jsp:useBean>
            <TITLE>Saisie des pointages</TITLE>		
        <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>   <!--  -->
            <SCRIPT type="text/javascript">
                function suppr(id) {
                    if (document.getElementById("NOM_chk_" + id) !== null) {  document.getElementById("NOM_chk_" + id).checked = false;  }
                    if (document.getElementById("NOM_nbr_" + id) !== null) {  document.getElementById("NOM_nbr_" + id).value = '';       }
                    document.getElementById("NOM_motif_" + id).value = '';
                    document.getElementById("NOM_comm_" + id).value = '';
                    if (document.getElementById("NOM_time_" + id + "_D") !== null) {document.getElementById("NOM_time_" + id + "_D").value = '';}
                    if (document.getElementById("NOM_time_" + id + "_F") !== null) {document.getElementById("NOM_time_" + id + "_F").value = '';}
                }
            </SCRIPT>		
            <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        </HEAD>
        <BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple">
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">		
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend"> Saisie des pointages pour l'agent <%=process.getIdAgent()%> semaine <%=process.getWeekYear()%>
                    <INPUT title="Retourner à l'écran de visualisation" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/annuler.png" height="16px" width="55px" name="<%=process.BACK%>">
                    <INPUT title="Enregister et Retourner à l'écran de visualisation" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/enregistrer.png" height="16px" width="68px" name="<%=process.VALIDATION%>"></legend>
                <BR/>
                <table cellpadding="0" cellspacing="0" border="0" class="display" id="SaisiePointageList"> 
                    <thead>
                        <%=process.getHeaderTable()%>
                    </thead>
                    <tbody>
                        <%=process.getPrimesTab()%>		
                        <%=process.getHSTab()%>		
                        <%=process.getAbsTab()%>		
                    </tbody>
                </table>
                <BR/>	
                <INPUT title="Retourner à l'écran de visualisation" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/annuler.png" height="16px" width="55px" name="<%=process.BACK%>">
                <INPUT title="Enregister et Retourner à l'écran de visualisation" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/enregistrer.png" height="16px" width="68px" name="<%=process.VALIDATION%>">
            </FIELDSET>
        </FORM>
    </BODY>
</HTML>
