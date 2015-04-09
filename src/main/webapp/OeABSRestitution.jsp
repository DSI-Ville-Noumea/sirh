<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.gestionagent.absence.dto.RestitutionMassiveDto"%>
<%@page import="nc.mairie.gestionagent.absence.dto.RestitutionMassiveHistoDto"%>
<%@page import="nc.mairie.metier.agent.Agent" %>

<HTML>
    <HEAD>
        <META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
        <META http-equiv="Content-Style-Type" content="text/css">
        <LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
        <LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
        <jsp:useBean class="nc.mairie.gestionagent.process.absence.OeABSRestitution" id="process" scope="session"></jsp:useBean>
            <TITLE>Restitution massive des congés annuels</TITLE>		


            <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
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
            </SCRIPT>		
            <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
				<INPUT type="submit" class="sigp2-Bouton-200" value="Lancer la restitution" name="<%=process.getNOM_PB_LANCER_RESTITUTION()%>">
				<br/><br/>
				<span class="sigp2Mandatory">Type restitution :</span>  			
                <input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RESTITUTION(), process.getNOM_RB_TYPE_MATIN()) %> > <span class="sigp2Mandatory">Matin</span> 
				<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RESTITUTION(), process.getNOM_RB_TYPE_AM()) %> > <span class="sigp2Mandatory">Après-midi</span> 
				<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RESTITUTION(), process.getNOM_RB_TYPE_JOURNEE()) %> > <span class="sigp2Mandatory">Journée</span> 
				<br/><br/>
				<span class="sigp2Mandatory">Motif :</span>
				<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_MOTIF()%>" size="20" type="text" value="<%= process.getVAL_ST_MOTIF()%>" style="margin-right:10px;">
				<br/><br/>	
             </FIELDSET>
        
        <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
        	<legend class="sigp2Legend">Historique des restitutions massives des congés annuels</legend>
        	
        	<table class="display" cellpadding="0" cellspacing="0" border="0" >
        		<thead>
	                <tr>
	                    <th class="" style="width:120px;" align="center">Date Restitution</th>
	                    <th style="width:120px;" align="center">Date d'exécution</th>
	                    <th style="width:200px;" align="center">Motif</th>
	                    <th style="width:50px;" align="center">Statut</th>
	                    <th style="width:50px;" align="center"><img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" /></th>
	                </tr>
	            </thead>
	            <tbody>
				<%
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				if(process.getListHistoRestitutionMassive().size() > 0) {
					for (int i = 0; i<process.getListHistoRestitutionMassive().size(); i++){
						RestitutionMassiveDto histo = process.getListHistoRestitutionMassive().get(i);
				%>
				<tr>
					<td align="left" style="width:120px;"><%=sdf.format(histo.getDateRestitution()) %>
					<% if(histo.isJournee()) { %> Journée <% } %><% if(histo.isMatin()) { %> Matin <% } %><% if(histo.isApresMidi()) { %> Après-midi <% } %>
					</td>
					<td align="left" style="width:120px;"><%=sdf.format(histo.getDateModification()) %></td>
					<td align="left" style="width:200px;"><%=histo.getMotif() %></td>
					<td align="left" style="width:50px;"><%=histo.getStatus() %></td>
					<td class="sigp2NewTab-liste" align="left" style="width:100px;">
						 <img onkeydown="" onkeypress="" onkeyup="" border="0" src="images/loupe.gif" width="16px" height="16px" 
						 style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_DETAILS_RESTITUTION(histo.getIdRestitutionMassive())%>');" />
						 <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_DETAILS_RESTITUTION(histo.getIdRestitutionMassive())%>" value="" />	
					</td>
				</tr>
				<% } 
				} else{ %>
				<tr>
					<td align="center" colspan="5">Pas de restitution.</td>
				</tr>
				<% } %>
				</tbody>
			</table>
        </FIELDSET>
        
        <% if(null != process.getDetailsHisto()) { 
        	RestitutionMassiveDto histoDetails = process.getDetailsHisto();
        %>
        <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
        	<legend class="sigp2Legend">Liste des agents ayant bénéficiés de la restitution massive du <%=sdf.format(histoDetails.getDateRestitution()) %>
					<% if(histoDetails.isJournee()) { %> Journée <% } %><% if(histoDetails.isMatin()) { %> Matin <% } %><% if(histoDetails.isApresMidi()) { %> Après-midi <% } %></legend>
        	
        	<table class="display" cellpadding="0" cellspacing="0" border="0" >
        		<thead>
	                <tr>
	                    <th class="" style="width:120px;" align="center">Agent</th>
	                    <th style="width:200px;" align="center">Statut</th>
	                    <th style="width:100px;" align="center">Nombre de jours</th>
	                </tr>
	            </thead>
	            <tbody>
				<%
				if(0 < histoDetails.getListHistoAgents().size()) {
					for (int i = 0; i<histoDetails.getListHistoAgents().size(); i++){
						RestitutionMassiveHistoDto histoAgents = histoDetails.getListHistoAgents().get(i);
						Agent agent = process.getAgent(histoAgents.getIdAgent());
				%>
				<tr>
					<td align="left" style="width:120px;"><%=agent.getPrenomUsage() + " " + agent.getNomUsage() + " (" + agent.getNomatr() + ")" %></td>
					<td align="left" style="width:200px;"><%=histoAgents.getStatus() %></td>
					<td align="left" style="width:100px;"><%=histoAgents.getJours() %></td>
				</tr>
				<% } 
				}else{ %>
					<tr>
						<td align="center" colspan="3">Pas d'agent.</td>
					</tr>
				<% } %>
				</tbody>
			</table>
        </FIELDSET>
        <% } %>
        </FORM>
    </BODY>
</HTML>