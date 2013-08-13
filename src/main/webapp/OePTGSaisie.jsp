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
        <jsp:useBean class="nc.mairie.gestionagent.process.pointage.OePTGSaisie" id="process" scope="session"></jsp:useBean>
            <TITLE>Saisie des pointages</TITLE>		
            <!-- <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>  -->
            <SCRIPT type="text/javascript">
                function suppr(id) {
                    alert("delete:" + id);
                    if (document.getElementById("acc_" + id) != null) {
                        document.getElementById("acc_" + id).checked = false;
                    }
                    if (document.getElementById("chk_" + id) != null) {
                        document.getElementById("chk_" + id).checked = false;
                    }
                    if (document.getElementById("nbr_" + id) != null) {
                        document.getElementById("nbr_" + id).value = '';
                    }
                    document.getElementById("motif_" + id).value = '';
                    document.getElementById("comm_" + id).value = '';
                    if (document.getElementById("TIME_" + id + "_D") != null) {
                        document.getElementById("TIME_" + id + "_D").value = '';
                    }
                    if (document.getElementById("TIME_" + id + "_F") != null) {
                        document.getElementById("TIME_" + id + "_F").value = '';
                    }
                }

                function init() {
                }

            </SCRIPT>		
            <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        </HEAD>
        <BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple"  onload="init()">
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">		
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;">
                <legend class="sigp2Legend"><INPUT title="Retour à l'écran de visualisation" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>" src="images/fleche-gauche.png" height="16px" width="16px" name="<%=process.BACK%>"> - Saisie des pointages pour l'agent <%=process.getIdAgent()%> semaine <%=process.getWeekYear()%></legend>
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
            </FIELDSET>
        </FORM>
    </BODY>
</HTML>
