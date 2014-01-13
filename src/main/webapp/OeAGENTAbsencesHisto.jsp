<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesHisto" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Historique des absences</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		<SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
		
		<SCRIPT language="JavaScript">
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
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		
		<div style="margin-left:10px;margin-top:20px;text-align:left;width:1030px" align="left">
			<% if (process.onglet.equals("ONGLET1")) {%>
				<span id="titreOngletNonPrises" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET1');">&nbsp;Demandes non prises&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletNonPrises" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET1');">&nbsp;Demandes non prises&nbsp;</span>&nbsp;&nbsp;
			<% } %>
			<% if (process.onglet.equals("ONGLET2")) {%>
				<span id="titreOngletEnCours" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET2');">&nbsp;Demandes en cours&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletEnCours" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET2');">&nbsp;Demandes en cours&nbsp;</span>&nbsp;&nbsp;
			<% } %>
			<% if (process.onglet.equals("ONGLET3")) {%>
				<span id="titreOngletToutes" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET3');">&nbsp;Toutes les demandes&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletToutes" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET3');">&nbsp;Toutes les demandes&nbsp;</span>&nbsp;&nbsp;
			<% } %>
		</div>
		
		<% if (process.onglet.equals("ONGLET1")) {%>
			<div id="corpsOngletNonPrises" title="NonPrises" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletNonPrises" title="NonPrises" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					    <legend class="sigp2Legend">Demandes non prises</legend>				    
						
				</FIELDSET>	
			</div>		
		
		<% if (process.onglet.equals("ONGLET2")) {%>
			<div id="corpsOngletEnCours" title="EnCours" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletEnCours" title="EnCours" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					    <legend class="sigp2Legend">Demandes en cours</legend>				    
						
				</FIELDSET>	
			</div>	
		
		<% if (process.onglet.equals("ONGLET3")) {%>
			<div id="corpsOngletToutes" title="Toutes" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletToutes" title="Toutes" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					    <legend class="sigp2Legend">Toutes les demandes</legend>				    
						
				</FIELDSET>	
			</div>
	<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">	
	</FORM>
<%} %>	
	</BODY>
</HTML>