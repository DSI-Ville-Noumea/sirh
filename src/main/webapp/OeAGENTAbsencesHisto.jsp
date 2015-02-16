<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesHisto" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
        <LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<TITLE>Historique des absences</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		<SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
        <SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
		
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
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
		}
		
		</SCRIPT>	
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		
		<div style="margin-left:10px;margin-top:20px;text-align:left;width:1030px" align="left">
			<% if (process.onglet.equals("ONGLET1")) {%>
				<span id="titreOngletNonPrises" class="OngletActif" onclick="afficheOnglet('ONGLET1');">&nbsp;Demandes non prises&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletNonPrises" class="OngletInactif" onclick="afficheOnglet('ONGLET1');">&nbsp;Demandes non prises&nbsp;</span>&nbsp;&nbsp;
			<% } %>
			<% if (process.onglet.equals("ONGLET2")) {%>
				<span id="titreOngletEnCours" class="OngletActif" onclick="afficheOnglet('ONGLET2');">&nbsp;Demandes en cours&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletEnCours" class="OngletInactif" onclick="afficheOnglet('ONGLET2');">&nbsp;Demandes en cours&nbsp;</span>&nbsp;&nbsp;
			<% } %>
			<% if (process.onglet.equals("ONGLET3")) {%>
				<span id="titreOngletToutes" class="OngletActif" onclick="afficheOnglet('ONGLET3');">&nbsp;Toutes les demandes&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletToutes" class="OngletInactif" onclick="afficheOnglet('ONGLET3');">&nbsp;Toutes les demandes&nbsp;</span>&nbsp;&nbsp;
			<% } %>
		</div>
		
		<% if (process.onglet.equals("ONGLET1")) {%>
			<div id="corpsOngletNonPrises" title="NonPrises" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletNonPrises" title="NonPrises" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
			    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Tri des absences à afficher</legend>
					<span class="sigp2" style="width:100px">Famille d'absence : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE_NP() %>" style="width=150px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE_NP(), process.getVAL_LB_TYPE_ABSENCE_NP_SELECT()) %>
					</SELECT>
					<span class="sigp2" style="width:65px">Date début : </span>
	                <input id="<%=process.getNOM_ST_DATE_DEB_NP()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEB_NP()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEB_NP()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEB_NP()%>', 'dd/mm/y');">
	                <span class="sigp2" style="width:55px">Date fin : </span>
	                <input id="<%=process.getNOM_ST_DATE_FIN_NP()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN_NP()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN_NP()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN_NP()%>', 'dd/mm/y');">
	                <BR/><BR/>
					<span class="sigp2" style="width:40px">Etat : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT_ABSENCE_NP() %>" style="width=120px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_ETAT_ABSENCE_NP(), process.getVAL_LB_ETAT_ABSENCE_NP_SELECT()) %>
					</SELECT>
		          	<span class="sigp2" style="width:85px">Date demande : </span>
	                <input id="<%=process.getNOM_ST_DATE_DEMANDE_NP()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEMANDE_NP()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEMANDE_NP()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEMANDE_NP()%>', 'dd/mm/y');">
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER_NP()%>">
				</FIELDSET>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend">Demandes non prises</legend>	
					<div style="overflow: auto;height: 250px;width:1000px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="300px;">Type Demande</td>
								<td width="100px;" align="left">Date début</td>
								<td width="100px;" align="left">Date fin</td>
								<td width="90px;" align="center">Durée</td>
								<td width="90px;" align="center">Date Demande</td>
								<td>Etat</td>
							</tr>
							<%
							for (int i = 0;i<process.getListeDemandeNonPrises().size();i++){
							%>
							<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeDemandeNonPrises().size()%>)">
								<td class="sigp2NewTab-liste"><%=process.getVAL_ST_TYPE_DEMANDE_NP(i)%></td>
								<td style="text-align: left;" class="sigp2NewTab-liste"><%=process.getVAL_ST_DATE_DEBUT_NP(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_DATE_FIN_NP(i)%></td>
								<td style="text-align: center;" class="sigp2NewTab-liste"><%=process.getVAL_ST_DUREE_NP(i)%></td>
								<td style="text-align: center;" class="sigp2NewTab-liste"><%=process.getVAL_ST_DATE_DEMANDE_NP(i)%></td>
								<td style="text-align: left;" class="sigp2NewTab-liste"><%=process.getVAL_ST_ETAT_DEMANDE_NP(i)%></td>
							</tr>
							<%
							}%>
						</table>	
					</div>	
				</FIELDSET>	
			</div>		
		
		<% if (process.onglet.equals("ONGLET2")) {%>
			<div id="corpsOngletEnCours" title="EnCours" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletEnCours" title="EnCours" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
			    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Tri des absences à afficher</legend>
					<span class="sigp2" style="width:100px">Famille d'absence : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE_EC() %>" style="width=150px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE_EC(), process.getVAL_LB_TYPE_ABSENCE_EC_SELECT()) %>
					</SELECT>
					<span class="sigp2" style="width:65px">Date début : </span>
	                <input id="<%=process.getNOM_ST_DATE_DEB_EC()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEB_EC()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEB_EC()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEB_EC()%>', 'dd/mm/y');">
	                <span class="sigp2" style="width:55px">Date fin : </span>
	                <input id="<%=process.getNOM_ST_DATE_FIN_EC()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN_EC()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN_EC()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN_EC()%>', 'dd/mm/y');">
	                <BR/><BR/>
					<span class="sigp2" style="width:40px">Etat : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT_ABSENCE_EC() %>" style="width=120px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_ETAT_ABSENCE_EC(), process.getVAL_LB_ETAT_ABSENCE_EC_SELECT()) %>
					</SELECT>
		          	<span class="sigp2" style="width:85px">Date demande : </span>
	                <input id="<%=process.getNOM_ST_DATE_DEMANDE_EC()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEMANDE_EC()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEMANDE_EC()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEMANDE_EC()%>', 'dd/mm/y');">
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER_EC()%>">
				</FIELDSET>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend">Demandes en cours</legend>		
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="300px;">Type Demande</td>
								<td width="100px;" align="left">Date début</td>
								<td width="100px;" align="left">Date fin</td>
								<td width="90px;" align="center">Durée</td>
								<td width="90px;" align="center">Date Demande</td>
								<td>Etat</td>
							</tr>
							<%
							for (int i = 0;i<process.getListeDemandeEnCours().size();i++){
							%>
							<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeDemandeEnCours().size()%>)">
								<td class="sigp2NewTab-liste"><%=process.getVAL_ST_TYPE_DEMANDE_EC(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_DATE_DEBUT_EC(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_DATE_FIN_EC(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DUREE_EC(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_DEMANDE_EC(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_ETAT_DEMANDE_EC(i)%></td>
							</tr>
							<%
							}%>
						</table>	
					</div>
				</FIELDSET>	
			</div>	
		
		<% if (process.onglet.equals("ONGLET3")) {%>
			<div id="corpsOngletToutes" title="Toutes" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletToutes" title="Toutes" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
			    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Tri des absences à afficher</legend>
					<span class="sigp2" style="width:100px">Famille d'absence : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE_TT() %>" style="width=150px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE_TT(), process.getVAL_LB_TYPE_ABSENCE_TT_SELECT()) %>
					</SELECT>
					<span class="sigp2" style="width:65px">Date début : </span>
	                <input id="<%=process.getNOM_ST_DATE_DEB_TT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEB_TT()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEB_TT()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEB_TT()%>', 'dd/mm/y');">
	                <span class="sigp2" style="width:55px">Date fin : </span>
	                <input id="<%=process.getNOM_ST_DATE_FIN_TT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN_TT()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN_TT()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN_TT()%>', 'dd/mm/y');">
	                <BR/><BR/>
					<span class="sigp2" style="width:40px">Etat : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT_ABSENCE_TT() %>" style="width=120px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_ETAT_ABSENCE_TT(), process.getVAL_LB_ETAT_ABSENCE_TT_SELECT()) %>
					</SELECT>
		          	<span class="sigp2" style="width:85px">Date demande : </span>
	                <input id="<%=process.getNOM_ST_DATE_DEMANDE_TT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEMANDE_TT()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEMANDE_TT()%>" >
	                <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEMANDE_TT()%>', 'dd/mm/y');">
	                <INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER_TT()%>">
				</FIELDSET>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend">Toutes les demandes</legend>			
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td width="300px;">Type Demande</td>
								<td width="100px;" align="left">Date début</td>
								<td width="100px;" align="left">Date fin</td>
								<td width="90px;" align="center">Durée</td>
								<td width="90px;" align="center">Date Demande</td>
								<td>Etat</td>
							</tr>
							<%
							for (int i = 0;i<process.getListeToutesDemandes().size();i++){
							%>
							<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeToutesDemandes().size()%>)">
								<td class="sigp2NewTab-liste"><%=process.getVAL_ST_TYPE_DEMANDE_TT(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_DATE_DEBUT_TT(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_DATE_FIN_TT(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DUREE_TT(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_DEMANDE_TT(i)%></td>
								<td class="sigp2NewTab-liste" style="text-align: left;"><%=process.getVAL_ST_ETAT_DEMANDE_TT(i)%></td>
							</tr>
							<%
							}%>
						</table>	
					</div>
				</FIELDSET>	
			</div>
	<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">	
	<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET1" id="ONGLET1" style="visibility: hidden;">
	<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET2" id="ONGLET2" style="visibility: hidden;">	
	<INPUT type="submit" name = "NOM_PB_ONGLET" value="ONGLET3" id="ONGLET3" style="visibility: hidden;">	
	</FORM>
<%} %>	
	</BODY>
</HTML>