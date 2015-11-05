<%@ page contentType="text/html; charset=UTF-8" %> 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.avancement.OeAVCTSimulationFonctionnaireAutre" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<lINK rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<TITLE>Simulation des avancements des fonctionnaires territoriaux ou AVCT = AUTO</TITLE>
		
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
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
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche avancée d'une fiche de poste">
				<LEGEND class="sigp2Legend">Simulation des avancements des fonctionnaires territoriaux ou AVCT = AUTO</LEGEND>
				<BR/>
				<span class="sigp2Mandatory" style="width:70px;">Année :</span>
				<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
				</SELECT>
				<BR/><BR/>
				<span class="sigp2" style="width:70px;">Service :</span>
				<INPUT id="service" class="sigp2-saisie" readonly="readonly"
					name="<%= process.getNOM_EF_SERVICE() %>" style="margin-right:10px;width:100px"
					type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"
					height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
          		<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" 
					value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">
				
	             	<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
					<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
					<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
					
				<span class="sigp2" style="width:60px">Par agent :</span>
				<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
				<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          		<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          		<BR/><BR/><BR/>
				<INPUT type="submit" value="Lancer" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_LANCER()%>">
				<BR><BR>
		    
				<% if (!process.agentEnErreurHautGrille.equals("")){ %>
					<span style="color: red;" class="sigp2Mandatory">Agents en anomalies :<br/><br/> <%=process.agentEnErreurHautGrille %></span>
					<BR/><BR/> 
					<span style="color: red;" class="sigp2Mandatory">Pour ces agents, un avancement n'a pu être calculé car ils sont en haut de grille.</span>
				<%} %>
			</FIELDSET>

			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">
		</FORM>
	</BODY>
</HTML>