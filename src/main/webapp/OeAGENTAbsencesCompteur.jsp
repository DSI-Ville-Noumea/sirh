<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesCompteur" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des absences</TITLE>
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
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				<legend class="sigp2Legend">Compteurs de l'agent</legend>
				<span class="sigp2" style="width:100px">Famille d'absence : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_ABSENCE() %>" style="width=150px;margin-right:20px;">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_ABSENCE(), process.getVAL_LB_TYPE_ABSENCE_SELECT()) %>
				</SELECT>
	           <BR/><BR/>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_AFFICHER()%>">
			</FIELDSET>
		
			<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>	
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend">Compteur <%=process.getTypeAbsenceCourant().getValue() %> de l'agent</legend>
					
		            <span class="sigp2" style="margin-left:20px;position:relative;width:110px;">Solde actuel :</span>
					<INPUT class="sigp2-saisie" disabled="disabled" size="10"  name="<%= process.getNOM_ST_SOLDE() %>" type="text" value="<%= process.getVAL_ST_SOLDE() %>">
					<BR/><BR/>
		            <span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:110px;">Durée à ajouter :</span>
					<INPUT class="sigp2-saisie" size="5"  name="<%= process.getNOM_ST_DUREE_HEURE_AJOUT() %>" type="text" value="<%= process.getVAL_ST_DUREE_HEURE_AJOUT() %>">heures
					<INPUT class="sigp2-saisie" size="5"  name="<%= process.getNOM_ST_DUREE_MIN_AJOUT() %>" type="text" value="<%= process.getVAL_ST_DUREE_MIN_AJOUT() %>">minutes
					<BR/><BR/>
		            <span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:110px;">Durée à retrancher :</span>
					<INPUT class="sigp2-saisie" size="10"  name="<%= process.getNOM_ST_DUREE_HEURE_RETRAIT() %>" type="text" value="<%= process.getVAL_ST_DUREE_HEURE_RETRAIT() %>">heures
					<INPUT class="sigp2-saisie" size="10"  name="<%= process.getNOM_ST_DUREE_MIN_RETRAIT() %>" type="text" value="<%= process.getVAL_ST_DUREE_MIN_RETRAIT() %>">minutes
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:110px">Motif : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF() %>" style="width=150px;margin-right:20px;">
						<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
					</SELECT>
	           		<BR/><BR/>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
				</FIELDSET>
			
			<%} %>			
			<%} %>
	</FORM>
<%} %>	
	</BODY>
</HTML>