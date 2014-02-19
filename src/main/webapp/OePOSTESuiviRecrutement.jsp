<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Suivi des recrutements</TITLE>

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
<jsp:useBean
 class="nc.mairie.gestionagent.process.poste.OePOSTESuiviRecrutement"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" class="sigp2-BODY" onload="return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
		    <legend class="sigp2Legend">Suivi des recrutements</legend>
		    <br/>
			<span style="margin-left:5px;position:relative;width:40px;">Réf. SES</span>
			<span style="position:relative;width:55px;">Réf. Mairie</span>
			<span style="position:relative;width:75px;">Réf. DRHFPNC</span>
			<span style="position:relative;width:70px;">Direction</span>
			<span style="position:relative;width:60px;">Section</span>
			<span style="position:relative;width:55px;">Réf. FP</span>
			<span style="position:relative;width:80px;">Titre poste</span>
			<span style="position:relative;width:75px;">Date ouverture</span>
			<span style="position:relative;width:60px;">Date clôture</span>
			<span style="position:relative;width:75px;">Date validation</span>
			<span style="position:relative;width:100px;">Date de transmission</span>
			<span style="position:relative;width:70px;">Date de réponse</span>
			<span style="position:relative;width:75px;">Motif non recrut.</span>
			<span style="position:relative;width:70px;">Agent recruté</span>
		
			<span class="sigp2-titre">
				<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_RECRUTEMENT() %>" size="6" style="width:1000px;font-family : monospace;">
					<%=process.forComboHTML(process.getVAL_LB_RECRUTEMENT(), process.getVAL_LB_RECRUTEMENT_SELECT()) %>
				</SELECT>
			</span>
			<BR/><BR/>
			<div style="text-align: center;">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Créer" name="<%=process.getNOM_PB_CREER_RECRUT()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Modifier" name="<%=process.getNOM_PB_MODIFIER_RECRUT()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="<%=process.getNOM_PB_SUPPRIMER_RECRUT()%>">
			</div>
			<BR/>
		</FIELDSET>
		<BR/>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
		<br/>
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %>
		<div>
		    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
			    <legend class="sigp2Legend">Fiche de poste</legend>
	   			<span class="sigp2Mandatory" style="position:relative;width:180px;">Réf. fiche de poste : </span>
				<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_RECHERCHER_FP()%>">
				<BR/>
				<span class="sigp2" style="width:100px">Direction :</span>
				<span class="sigp2-statique" style="width:80px"><%=process.getVAL_ST_DIRECTION()%></span>
				<span class="sigp2" style="width:60px">Service :</span>
				<span class="sigp2-statique" style="width:80px"><%=process.getVAL_ST_SERVICE()%></span>
				<span class="sigp2" style="width:60px">Section :</span>
				<span class="sigp2-statique" style="width:80px"><%=process.getVAL_ST_SECTION()%></span>
				<span class="sigp2" style="width:60px">Subdivision :</span>
				<span class="sigp2-statique" style="width:80px"><%=process.getVAL_ST_SUBDIVISION()%></span>
				<BR/>
				<span class="sigp2" style="width:100px">Titre du poste :</span>
				<span class="sigp2-statique" style="width:80px"><%=process.getVAL_ST_TITRE_POSTE()%></span>
				<span class="sigp2" style="width:60px">Grade :</span>
				<span class="sigp2-statique" style="width:80px"><%=process.getVAL_ST_GRADE()%></span>
				<BR/>
			</FIELDSET>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Réf. SES : </span>
			<INPUT class="sigp2-saisiemajusculenongras" maxlength="5" name="<%= process.getNOM_ST_REF_SES() %>" size="10"
				type="text" value="<%= process.getVAL_ST_REF_SES() %>">
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Réf. Mairie : </span>
			<INPUT class="sigp2-saisiemajusculenongras" maxlength="5" name="<%= process.getNOM_EF_REF_MAIRIE() %>" size="10"
				type="text" value="<%= process.getVAL_EF_REF_MAIRIE() %>">
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Réf. DRHFPNC : </span>
			<INPUT class="sigp2-saisiemajusculenongras" maxlength="5" name="<%= process.getNOM_EF_REF_DRHFPNC() %>" size="10"
				type="text" value="<%= process.getVAL_EF_REF_DRHFPNC() %>">
			<BR/><BR/>
   			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">agent remplacé : </span>
			<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_RECHERCHER_FP()%>">
			<span class="sigp2Mandatory" style="width:150px">Motif de recrutement :</span>
			<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF_RECRUTEMENT() %>" style="width:150px;margin-right:30px;">
				<%=process.forComboHTML(process.getVAL_LB_MOTIF_RECRUTEMENT(), process.getVAL_LB_MOTIF_RECRUTEMENT_SELECT())%>
			</SELECT>
			<BR/><BR/>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Date ouverture : </span>
		    <span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_OUVERTURE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_OUVERTURE() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_OUVERTURE() %>', 'dd/mm/y');">
			</span>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Date validation : </span>
		    <span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_VALIDATION() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_VALIDATION() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_VALIDATION() %>', 'dd/mm/y');">
			</span>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Date clôture : </span>
		    <span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_CLOTURE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_CLOTURE() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_CLOTURE() %>', 'dd/mm/y');">
			</span>
			<BR/>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Date transmission : </span>
		    <span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_TRANSMISSION() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_TRANSMISSION() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_TRANSMISSION() %>', 'dd/mm/y');">
			</span>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Nb candidatures reçues : </span>
			<INPUT class="sigp2-saisiemajusculenongras" maxlength="5" name="<%= process.getNOM_EF_NB_CAND_RECUES() %>" size="10" style="margin-right:38px"
				type="text" value="<%= process.getVAL_EF_NB_CAND_RECUES() %>">
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:100px;">Date réponse : </span>
		    <span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_REPONSE_CAND() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_REPONSE_CAND() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_REPONSE_CAND() %>', 'dd/mm/y');">
			</span>
			<BR/><BR/>
			<span class="sigp2Mandatory" style="margin-left:20px;width:180px">Motif de non recrutement :</span>
			<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF_NON_RECRUTEMENT() %>" style="width:150px;">
				<%=process.forComboHTML(process.getVAL_LB_MOTIF_NON_RECRUTEMENT(), process.getVAL_LB_MOTIF_NON_RECRUTEMENT_SELECT())%>
			</SELECT>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:160px;">Nom de l'agent recruté : </span>
			<INPUT class="sigp2-saisiemajusculenongras" maxlength="100" name="<%= process.getNOM_EF_NOM_AGENT_RECRUT() %>" size="80"
				type="text" value="<%= process.getVAL_EF_NOM_AGENT_RECRUT() %>">
			<BR/><BR/>
		</div>
		<%}else{ %>
		<div>
			<FONT color='red'>Veuillez valider votre choix.</FONT>
			<BR/><BR/>
		</div>
		<BR/>
		<%} %>
		<div style="text-align: center">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</div>
		<BR/>
	</FIELDSET>
<%} else {%>
	<BR>
	<FIELDSET class="sigp2Fieldset" style="text-align : center;">
		<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
	</FIELDSET>
<% }%>
</FORM>
</BODY>
</HTML>