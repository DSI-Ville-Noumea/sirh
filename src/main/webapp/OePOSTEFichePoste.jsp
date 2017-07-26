<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumStatutFichePoste"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="nc.mairie.metier.poste.TitrePoste"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFichePoste" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<TITLE>OePOSTEFichePoste</TITLE>

		<META name="GENERATOR" content="Rational Application Developer">

		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></script>
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/competence.js"></script>
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
		<%
		ArrayList<TitrePoste> listeTitres = process.getListeTitre();

		String res = 	"<script language=\"javascript\">\n"+
				"var availableTitres = new Array(\n";

		for (int i = 0; i < listeTitres.size(); i++){
			res+= "   \""+((TitrePoste)listeTitres.get(i)).getLibTitrePoste()+"\"";
			if (i+1 < listeTitres.size())
				res+=",\n";
			else	res+="\n";
		}

		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listeTitrePoste").autocomplete({source:availableTitres
				});
			});
		</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>

	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">

		<%@ include file="BanniereErreur.jsp" %>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>" />
			<fieldset class="sigp2Fieldset" style="width:1020px">
				<legend class="sigp2Legend">Actions</legend>
				<span class="sigp2Mandatory"> Recherche : </span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_RECHERCHE() %>" size="10"	type="text" value="<%= process.getVAL_EF_RECHERCHE() %>" style="margin-right:10px;">
				<span class="sigp2Mandatory"> Par matricule agent : </span>
				<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_RECHERCHE_BY_AGENT() %>" size="10"	type="text" value="<%= process.getVAL_EF_RECHERCHE_BY_AGENT() %>" style="margin-right:10px;">

          		<INPUT title="Recherche" type="image" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHER()%>">
       			<INPUT title="Recherche avancée" type="image" src="images/rechercheAvancee.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHE_AVANCEE()%>" >
          		<INPUT title="Créer une FDP" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_FP()%>">
				<%
					if (process.versionFicheMetier()) {
				%>
          			<INPUT title="Dupliquer une FDP" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/dupliquer.gif" height="16px" width="16px" name="<%=process.getNOM_PB_DUPLIQUER_FP()%>">
				<%
					}
				%>
          		<%--<INPUT title="Supprimer une FDP" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_FP()%>">--%>
       		</fieldset>

			<% if (!process.ACTION_RECHERCHE.equals(process.getVAL_ST_ACTION())){ %>

				<fieldset class="sigp2Fieldset" style="width:1020px">
					<legend class="sigp2Legend">Fiche de poste</legend>

						<% if ((process.ACTION_CREATION.equals(process.getVAL_ST_ACTION()) || process.ACTION_DUPLICATION.equals(process.getVAL_ST_ACTION())) && process.getMetierPrimaire() == null){ %>
							<span class="sigp2Mandatory" style="width:150px;">Recherche fiche emploi ville primaire : </span>
							<INPUT class="sigp2-saisie" maxlength="60" size="20" type="text" style="margin-right:10px;"name="<%= process.getNOM_ST_METIER_PRIMAIRE() %>" value="<%=process.getVAL_ST_METIER_PRIMAIRE()%>" >
							<INPUT type="image" src="images/loupe.gif" height="16px" width="16px" editable="false" name="<%=process.getNOM_PB_RECHERCHE_METIER_PRIMAIRE()%>">
						<%}else if(process.getEmploiPrimaire() == null && process.getMetierPrimaire() == null){ %>
							<span class="sigp2Mandatory"> Il n'y a pas de fiche métier associée à cette fiche de poste (<%=process.getVAL_ST_NUMERO()%>). Merci d'en choisir une.</span>
							<BR/><BR/>
							<span class="sigp2Mandatory" style="width:150px;">Recherche fiche emploi ville primaire : </span>
							<INPUT class="sigp2-saisie" maxlength="60" size="20" type="text" style="margin-right:10px;"name="<%= process.getNOM_ST_METIER_PRIMAIRE() %>" value="<%=process.getVAL_ST_METIER_PRIMAIRE()%>" >
							<INPUT type="image" src="images/loupe.gif" height="16px" width="16px" editable="false" name="<%=process.getNOM_PB_RECHERCHE_METIER_PRIMAIRE()%>">
						<%} else {%>
							<span class="sigp2Mandatory" style="width:70px"> Numéro : </span>
							<span class="sigp2-saisie" style="width:150px"><%=process.getVAL_ST_NUMERO()%></span>

							<span class="sigp2Mandatory" style="margin-left: 100px;"> Statut : </span>
							<%if(process.estStatutGelee()){ %>
							<SELECT onchange='executeBouton("<%=process.getNOM_PB_SELECT_STATUT()%>")' class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT() %>" style="width:100px;margin-right:98px;color: red" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
								<%=process.forComboHTML(process.getVAL_LB_STATUT(), process.getVAL_LB_STATUT_SELECT())%>
							</SELECT>
							<%}else{ %>
							<SELECT onchange='executeBouton("<%=process.getNOM_PB_SELECT_STATUT()%>")' class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT() %>" style="width:100px;margin-right:98px;" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
								<%=process.forComboHTML(process.getVAL_LB_STATUT(), process.getVAL_LB_STATUT_SELECT())%>
							</SELECT>
							<%} %>
						<%}%>
				</fieldset>
			    <% if (process.getMetierPrimaire() != null) { %>
                    <jsp:include page="POSTEFichePosteMetier.jsp" />
                <% } else { %>
                    <jsp:include page="POSTEFichePosteEmploi.jsp" />
                <% } %>
			<% } %>
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SELECT_STATUT()%>" value="x">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_GRADE()%>">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE()%>">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_INFO_SERVICE()%>">
			<%=process.getUrlFichier()%>
		</FORM>
	</BODY>
</HTML>