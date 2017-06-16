<%@page import="nc.noumea.mairie.abs.RefTypeGroupeAbsenceEnum"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.Map"%>
<%@page import="java.io.File"%>

<HTML>
    <jsp:useBean class="nc.mairie.gestionagent.process.absence.OeABSGestionPJ" id="process" scope="session"></jsp:useBean>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
            <TITLE>Gestion des pièces jointes</TITLE>		


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
            </SCRIPT>
            <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </HEAD>
        <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%=process.getFocus()%>')">	
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre" ENCTYPE="multipart/form-data">
           	<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
           	<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="AddDocForAbs">
           		<legend class="sigp2Legend">Ajout d'un document</legend>
           		<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
				<div>
					<table>
						<tr>
							<td>
								<span class="sigp2">Fichier : </span>
							</td>
							<td>
								<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							</td>
						</tr>
					</table>
	            	<BR/>
					<INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DOCUMENT_CREATION() %>">
                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DOCUMENT() %>">
				</div>
			</FIELDSET>
        </FORM>
    </BODY>
</HTML>