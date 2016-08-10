<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">

		<TITLE>Calcul des convocations pour le suivi médical</TITLE>		

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
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
			document.getElementById(id).className="selectLigne";
		}
		</SCRIPT>		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.OeSMCalcul" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">	
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Calcul des prévisions des visites médicales du travail</legend>
			<span class="sigp2" style="width:75px">Mois : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOIS() %>" style="width=140px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_MOIS(), process.getVAL_LB_MOIS_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:75px">Année : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=140px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<INPUT type="submit" class="sigp2-Bouton-250" value="Calculer pour le mois sélectionné" name="<%=process.getNOM_PB_CALCULER()%>">
		</FIELDSET>
	</FORM>
	</BODY>
</HTML>