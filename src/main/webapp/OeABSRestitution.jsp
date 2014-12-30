<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.absence.OeABSRestitution" id="process" scope="session"></jsp:useBean>
            <TITLE>Restitution massive des congés annuels</TITLE>		


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
        <FORM onkeypress="testClickFiltrer();" name="formu" method="POST" class="sigp2-titre">
            <INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Restitution massive des congés annuels</legend>   
                
				<span class="sigp2Mandatory">Date du jour à restituer :</span>          
				<input id="<%=process.getNOM_ST_DATE_RESTITUTION()%>" class="sigp2-saisie" name="<%= process.getNOM_ST_DATE_RESTITUTION() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_RESTITUTION() %>">
				<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_RESTITUTION()%>', 'dd/mm/y');">
				<br/><br/>
				<span class="sigp2Mandatory">Type restitution :</span>  			
                <input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RESTITUTION(), process.getNOM_RB_TYPE_MATIN()) %> > <span class="sigp2Mandatory">Matin</span> 
				<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RESTITUTION(), process.getNOM_RB_TYPE_AM()) %> > <span class="sigp2Mandatory">Après-midi</span> 
				<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RESTITUTION(), process.getNOM_RB_TYPE_JOURNEE()) %> > <span class="sigp2Mandatory">Journée</span> 
					
				<br/><br/>
				<span class="sigp2Mandatory">Agents concernés :</span>  
					<div style="overflow: auto;height: 120px;width:800px;">
						<INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AGENT()%>">
						<table class="sigp2NewTab">
							<%
							int indice = 0;
								for (int i = 0;i<process.getListeAgent().size();i++){
							%>
							<tr id="<%=indice%>" onmouseover="SelectLigne(<%=indice%>,<%=process.getListeAgent().size()%>)" >
								<td class="sigp2NewTab-liste" style="width:30px;" align="center">		
									<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_AGENT(indice)%>">
								</td>
								<td class="sigp2NewTab-liste" align="left" style="width:150px;"><%=process.getVAL_ST_NOMATR_AGENT(indice)%></td>
								<td class="sigp2NewTab-liste" align="left" style="width:150px;"><%=process.getVAL_ST_LIB_AGENT(indice)%></td>
							</tr>
							<%
							indice++;										
							}%>
						</table>	
					</div>
					
			<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
				<%if(process.getVAL_ST_ACTION().equals(process.ACTION_AJOUT)){ %>
				 	<span class="sigp2Mandatory" style="width:50px;margin-left: 20px;">Agent :</span>
	                <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_CREATION()%>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_CREATION()%>" style="margin-right:10px;">
	                <img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_CREATION()%>');">
	                <INPUT type="submit" class="sigp2-Bouton-200" value="Ajouter l'agent à la liste" name="<%=process.getNOM_PB_AJOUTER()%>">
				<%} %>
			<%} %>
             </FIELDSET>             
            
            
        </FORM>
    </BODY>
</HTML>