<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.absence.OeABSAlimentationMensuelle" id="process" scope="session"></jsp:useBean>
            <TITLE>Alimentation mensuelle des congés annuels</TITLE>		


            <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 

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
            <META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        </HEAD>
        <BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%=process.getFocus()%>')">	
        <%@ include file="BanniereErreur.jsp" %>
        <FORM name="formu" method="POST" class="sigp2-titre">
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Alimentation mensuelle des congés annuels</legend>   
				<span class="sigp2Mandatory">Retrouver ici toutes les alimenations automatiques de congés annuels.</span>      
				<br/><br/>   
				<span class="sigp2" style="width:100px">Choisissez le mois à afficher : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOIS_ALIM_AUTO() %>" style="width:100px;">
					<%=process.forComboHTML(process.getVAL_LB_MOIS_ALIM_AUTO(), process.getVAL_LB_MOIS_ALIM_AUTO_SELECT()) %>
				</SELECT>  
	            <BR/><BR/>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER()%>">   
	            <BR/><BR/>				
						<table class="sigp2NewTab" width="1000px">
							<%
								for (int i = 0;i<process.getListeAlimAuto().size();i++){
									Integer indice = process.getListeAlimAuto().get(i).getAgent().getIdAgent();
							%>
							<tr>
								<td class="sigp2NewTab-liste" align="left" style="width:50px;"><%=process.getVAL_ST_NOMATR_AGENT(indice)%></td>
								<td class="sigp2NewTab-liste" align="left" style="width:150px;"><%=process.getVAL_ST_LIB_AGENT(indice)%></td>
								<td class="sigp2NewTab-liste" align="left"><%=process.getVAL_ST_STATUT(indice)%></td>
							</tr>
							<%
							indice++;										
							}%>
						</table>         
				
             </FIELDSET>             
            
            
        </FORM>
    </BODY>
</HTML>