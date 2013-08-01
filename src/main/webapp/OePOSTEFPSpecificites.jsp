<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des spécificités d'une fiche de poste</TITLE>

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
 class="nc.mairie.gestionagent.process.poste.OePOSTEFPSpecificites"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Gestion des spécificités d'une fiche de poste</legend>
		    <br/>
		    <div align="left">
				<span class="sigp2-RadioBouton">
					<INPUT tabindex="" type="radio" <%= process.forRadioHTML(process.getNOM_RG_SPECIFICITE(),process.getNOM_RB_SPECIFICITE_PP())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE() %>")'>Prime pointage
					<span style="width:5px"></span>
					<INPUT tabindex="" type="radio" <%= process.forRadioHTML(process.getNOM_RG_SPECIFICITE(),process.getNOM_RB_SPECIFICITE_AN())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE() %>")'>Avantage en nature
					<span style="width:5px"></span>
					<INPUT tabindex="" type="radio" <%= process.forRadioHTML(process.getNOM_RG_SPECIFICITE(),process.getNOM_RB_SPECIFICITE_D())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE() %>")'>Délégation
					<span style="width:5px"></span>
					<INPUT tabindex="" type="radio" <%= process.forRadioHTML(process.getNOM_RG_SPECIFICITE(),process.getNOM_RB_SPECIFICITE_RI())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE() %>")'>Régime indemnitaire
					</span>
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGER_SPECIFICITE()%>" value="OK">
		    </div>
			<BR/>
			<%if (process.getVAL_RG_SPECIFICITE().equals(process.getNOM_RB_SPECIFICITE_AN())){ %>
		    <div align="left" style="float:left;width:100%;">
				<span class="sigp2" style="text-align:center;">Avantage(s) en nature</span>
				<br/><br/>
				<span style="margin-left:5px;position:relative;width:368px;">Type</span>
				<span style="position:relative;width:74px;">Montant</span>
				<span style="position:relative;width:60px;">Nature</span>
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_AVANTAGE() %>" size="6" style="width:90%;font-family : monospace;">
						<%=process.forComboHTML(process.getVAL_LB_AVANTAGE(), process.getVAL_LB_AVANTAGE_SELECT()) %>
					</SELECT>
				</span>
				<BR/>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AVANTAGE()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_AVANTAGE()%>">
			</div>
			<%} else if (process.getVAL_RG_SPECIFICITE().equals(process.getNOM_RB_SPECIFICITE_D())){ %>
			<div align="left" style="float:left;width:100%;">
				<span class="sigp2" style="text-align:center;">Délégation(s)</span>
				<br/><br/>
				<span style="margin-left:5px;position:relative;width:228px;">Type</span>
				<span style="position:relative;width:720px;">Commentaire</span>
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_DELEGATION() %>" size="6" style="width:90%;font-family : monospace;">
						<%=process.forComboHTML(process.getVAL_LB_DELEGATION(), process.getVAL_LB_DELEGATION_SELECT()) %>
					</SELECT>
				</span>
				<BR/>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_DELEGATION()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATION()%>">
			</div>
			<%} else if (process.getVAL_RG_SPECIFICITE().equals(process.getNOM_RB_SPECIFICITE_PP())){ %>
			<div align="left" style="float:left;width:100%;">
				<span class="sigp2" style="text-align:center;">Prime(s) de pointage</span>
				<br/><br/>
				<span style="margin-left:5px;position:relative;">Rubrique</span>
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_PRIME_POINTAGE() %>" size="6" style="width:90%;font-family : monospace;">
						<%=process.forComboHTML(process.getVAL_LB_PRIME_POINTAGE(), process.getVAL_LB_PRIME_POINTAGE_SELECT()) %>
					</SELECT>
				</span>
				<BR/>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_PRIME_POINTAGE()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_PRIME_POINTAGE()%>">
			</div>
			<%} else { %>
			<div align="left" style="float:left;width:100%;">
				<span class="sigp2" style="text-align:center;">Régime(s) indemnitaire(s)</span>
				<br/><br/>
				<span style="margin-left:5px;position:relative;width:158px;">Type</span>
				<span style="position:relative;width:75px;">Forfait</span>
				<span style="position:relative;width:60px;">Nb points</span>
				<br/>
				<span class="sigp2-titre" align="center" colspan="2">
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_REGIME() %>" size="6" style="width:90%;font-family : monospace;">
						<%=process.forComboHTML(process.getVAL_LB_REGIME(), process.getVAL_LB_REGIME_SELECT()) %>
					</SELECT>
				</span>
				<BR/>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_REGIME()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_REGIME()%>">
			</div>
			<%} %>
			<BR/>
		</FIELDSET>
		<BR/>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%> <%=process.getVAL_ST_SPECIFICITE()%></legend>
		<BR/>
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRIMER)){ %>
			<%if(process.getVAL_ST_SPECIFICITE().equals(process.SPEC_AVANTAGE_NATURE)){ %>
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Type d'avantage : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_TYPE_AVANTAGE() %>" style="width : 350px;">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_AVANTAGE(), process.getVAL_LB_TYPE_AVANTAGE_SELECT()) %>
					</SELECT>
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nature d'avantage : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_NATURE_AVANTAGE() %>" style="width : 350px;">
					<%=process.forComboHTML(process.getVAL_LB_NATURE_AVANTAGE(), process.getVAL_LB_NATURE_AVANTAGE_SELECT()) %>
					</SELECT>
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Montant : </span>
				<span>
					<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_MONTANT_AVANTAGE() %>" size="10"
						type="text" value="<%= process.getVAL_EF_MONTANT_AVANTAGE() %>">
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Rubrique : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_RUBRIQUE_AVANTAGE() %>" style="width : 530px;">
					<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE_AVANTAGE(), process.getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) %>
					</SELECT>
				</span>
			<%}else if(process.getVAL_ST_SPECIFICITE().equals(process.SPEC_DELEGATION)){ %>
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:120px;">Type : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_TYPE_DELEGATION() %>" style="width : 350px;">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_DELEGATION(), process.getVAL_LB_TYPE_DELEGATION_SELECT()) %>
					</SELECT>
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:120px;">Commentaire : </span>
				<span>
					<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENT_DELEGATION() %>" size="100"
						type="text" value="<%= process.getVAL_EF_COMMENT_DELEGATION() %>">
				</span>
			<%}else if(process.getVAL_ST_SPECIFICITE().equals(process.SPEC_PRIME_POINTAGE)){ %>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Rubrique : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_RUBRIQUE_PRIME_POINTAGE() %>" style="width : 530px;">
					<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE_PRIME_POINTAGE(), process.getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT()) %>
					</SELECT>
				</span>
			<%}else { %>
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:70px;">Type : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_TYPE_REGIME() %>" style="width : 350px;">
					<%=process.forComboHTML(process.getVAL_LB_TYPE_REGIME(), process.getVAL_LB_TYPE_REGIME_SELECT()) %>
					</SELECT>
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Forfait : </span>
				<span>
					<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_FORFAIT_REGIME() %>" size="10"
						type="text" value="<%= process.getVAL_EF_FORFAIT_REGIME() %>">
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Nb points : </span>
				<span>
					<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_NB_POINTS_REGIME() %>" size="10"
						type="text" value="<%= process.getVAL_EF_NB_POINTS_REGIME() %>">
				</span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Rubrique : </span>
				<span>
					<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_RUBRIQUE_REGIME() %>" style="width : 530px;">
					<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE_REGIME(), process.getVAL_LB_RUBRIQUE_REGIME_SELECT()) %>
					</SELECT>
				</span>
			<%} %>
			<BR/><BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Ajouter" name="<%=process.getNOM_PB_VALIDER_AJOUT()%>">
		<%} %>
		<BR/>
	</FIELDSET>
<%}%>
<BR/>
<FIELDSET class="sigp2Fieldset" style="text-align:center;width:1030px">
	<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
	<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
</FIELDSET>
</FORM>
</BODY>
</HTML>