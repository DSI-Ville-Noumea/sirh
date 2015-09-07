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
				<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_RECHERCHE() %>" size="10"	type="text" value="<%= process.getVAL_EF_RECHERCHE() %>" style="margin-right:10px;">
				<span class="sigp2Mandatory"> Par matricule agent : </span>
				<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_RECHERCHE_BY_AGENT() %>" size="10"	type="text" value="<%= process.getVAL_EF_RECHERCHE_BY_AGENT() %>" style="margin-right:10px;">
				
          		<INPUT title="Recherche" type="image" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHER()%>">
       			<INPUT title="Recherche avancée" type="image" src="images/rechercheAvancee.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHE_AVANCEE()%>" >
          		<INPUT title="Créer une FDP" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_FP()%>">
          		<INPUT title="Dupliquer une FDP" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/dupliquer.gif" height="16px" width="16px" name="<%=process.getNOM_PB_DUPLIQUER_FP()%>">
          		<INPUT title="Supprimer une FDP" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_FP()%>">
       		</fieldset>

			<% if (!process.ACTION_RECHERCHE.equals(process.getVAL_ST_ACTION())){ %>

				<fieldset class="sigp2Fieldset" style="width:1020px">
					<legend class="sigp2Legend">Fiche de poste</legend>
					
						<% if ((process.ACTION_CREATION.equals(process.getVAL_ST_ACTION()) || process.ACTION_DUPLICATION.equals(process.getVAL_ST_ACTION())) && process.getEmploiPrimaire() == null){ %>
							<span class="sigp2Mandatory" style="width:150px;">Recherche fiche emploi primaire : </span>
							<INPUT class="sigp2-saisie" maxlength="60" size="20" type="text" style="margin-right:10px;"name="<%= process.getNOM_ST_EMPLOI_PRIMAIRE() %>" value="<%=process.getVAL_ST_EMPLOI_PRIMAIRE()%>" >
							<INPUT type="image" src="images/loupe.gif" height="16px" width="16px" editable="false" name="<%=process.getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE()%>">
						<%}else if(process.getEmploiPrimaire() == null){ %>
							<span class="sigp2Mandatory"> Il n'y a pas de fiche emploi associé à cette fiche de poste (<%=process.getVAL_ST_NUMERO()%>). Merci d'en choisir une.</span>
							<BR/><BR/>
							<span class="sigp2Mandatory" style="width:150px;">Recherche fiche emploi primaire : </span>
							<INPUT class="sigp2-saisie" maxlength="60" size="20" type="text" style="margin-right:10px;"name="<%= process.getNOM_ST_EMPLOI_PRIMAIRE() %>" value="<%=process.getVAL_ST_EMPLOI_PRIMAIRE()%>" >
							<INPUT type="image" src="images/loupe.gif" height="16px" width="16px" editable="false" name="<%=process.getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE()%>">
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
				<% if (process.getEmploiPrimaire() != null){ %>
				<div  style="width:1020px;">
					<div style="width:570px;float:left;">
						<fieldset class="sigp2Fieldset" style="width:570px;">
							<legend class="sigp2Legend">Service</legend>
							<table>
								<tr>
									<td width="110px">
										<span class="sigp2Mandatory">Service :</span>
									</td>
									<td class="sigp2">
										<INPUT onfocus='executeBouton("<%=process.getNOM_PB_INFO_SERVICE() %>")' readonly="readonly" id="service" class="sigp2-saisie"  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  name="<%= process.getNOM_EF_SERVICE() %>" style="margin-right:0px;width:100px" type="text" value="<%= process.getVAL_EF_SERVICE() %>" >
										<img border="0" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence" height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">
										<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">
												
											
										<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
										<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
										<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td class="sigp2">
										<input id="infoService" style="width:350px;" class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_ST_INFO_SERVICE() %>" value="<%= process.getVAL_ST_INFO_SERVICE() %>">
										
									</td>
								</tr>
								<tr>
									<td>
										<span  class="sigp2Mandatory">Localisation :</span>
									</td>
									<td>
										<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" style="width:350px;" name="<%=process.getNOM_LB_LOC()%>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
											<%=process.forComboHTML(process.getVAL_LB_LOC(), process.getVAL_LB_LOC_SELECT())%>
										</SELECT>
									</td>
								</tr>
							</table>	
						</fieldset>
						<fieldset class="sigp2Fieldset" style="margin-right:0px;width:570px;">
							<legend class="sigp2Legend">Information emploi</legend>		
							<table>
								<tr>
									<td width="100px">
										<span class="sigp2Mandatory"> Niveau d'étude : </span>
									</td>
									<td align="left">
										<span class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
											<INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_NIVEAU()%>">
								            <INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_NIVEAU_ETUDE()%>">
								            <% if (process.isAfficherListeNivEt()) {%>			
												<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_NIVEAU_ETUDE() %>" style="width:355px;margin-bottom:5px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE() %>")'>
													<%=process.forComboHTML(process.getVAL_LB_NIVEAU_ETUDE(), process.getVAL_LB_NIVEAU_ETUDE_SELECT())%>
												</SELECT>
											<%} %>
										</span>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>									
									<td>
										<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_EF_NIVEAU_ETUDE_MULTI() %>" style="width:393px;" type="text" value="<%= process.getVAL_EF_NIVEAU_ETUDE_MULTI() %>">
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Grade : </span>
									</td>
									<td align="left">
										<span class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
											<INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_GRADE()%>">					            
							    			<% if (process.isAfficherListeGrade()) {%>	
												<SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_GRADE()%>" style="width:355px;" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_GRADE() %>")'>
													<%=process.forComboHTML(process.getVAL_LB_GRADE(), process.getVAL_LB_GRADE_SELECT())%>
												</SELECT>
											<%} %>
										</span>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>									
									<td>
										<INPUT class="sigp2-saisie" style="width:393px" name="<%= process.getNOM_EF_GRADE() %>"  disabled="disabled" type="text" value="<%= process.getVAL_EF_GRADE() %>" >
										<br/>
										<span class="sigp2-saisie"><%= process.getVAL_ST_INFO_GRADE()%></span>
										<INPUT class="sigp2-saisie" name="<%= process.getNOM_EF_CODE_GRADE() %>"  disabled="disabled" type="hidden" value="<%= process.getVAL_EF_CODE_GRADE() %>" >	
									</td>
								</tr>
							</table>														
						</fieldset>
					</div>
					<div style="width:400px;float:right;">
						<fieldset class="sigp2Fieldset" style="width:400px;">
							<legend class="sigp2Legend">Information budgétaire</legend>
						<% if ((process.ACTION_CREATION.equals(process.getVAL_ST_ACTION()) || process.ACTION_DUPLICATION.equals(process.getVAL_ST_ACTION())) && process.getEmploiPrimaire() == null){ %>
						<%} else {%>
							<table>
								<tr>
									<td width="150px">
										<span class="sigp2Mandatory"> Fiche emploi primaire : </span>
									</td>
									<td>
										<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="5" size="6"	type="text" readonly="readonly" value="<%=process.getVAL_ST_EMPLOI_PRIMAIRE()%>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
										<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" height="16px" width="16px" editable="false" name="<%=process.getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE()%>">
										<% if(null != process.getVAL_ST_EMPLOI_PRIMAIRE() && !"".equals(process.getVAL_ST_EMPLOI_PRIMAIRE().trim())) { %>
										<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_FICHE_EMPLOI_PRIMAIRE()%>">
										<% } %>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Fiche emploi secondaire : </span>
									</td>
									<td>
										<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="5" size="6" readonly="readonly" value="<%=process.getVAL_ST_EMPLOI_SECONDAIRE()%>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
										<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHE_EMPLOI_SECONDAIRE()%>">							
										<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE()%>" src="images/suppression.gif" height="16px" width="16px" >
										<% if(null != process.getVAL_ST_EMPLOI_SECONDAIRE() && !"".equals(process.getVAL_ST_EMPLOI_SECONDAIRE().trim())) { %>
										<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_FICHE_EMPLOI_SECONDAIRE()%>">
										<% } %>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Année : </span>
									</td>
									<td>							
										<% if (!process.estFpCouranteAffectee()){ %>
											<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_EF_ANNEE() %>" size="6"
												type="text" value="<%= process.getVAL_EF_ANNEE() %>" style="margin-right:90px;" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
										<%}else{ %>
											<INPUT class="sigp2-saisie" maxlength="4"
												name="<%= process.getNOM_EF_ANNEE() %>" size="6" disabled="disabled"
												type="text" value="<%= process.getVAL_EF_ANNEE() %>" style="margin-right:90px;">							
										<%} %>
									</td>
								</tr>
								<tr>
									<td>		
										<span class="sigp2Mandatory"> Budget : </span>
									</td>
									<td>
										<% if (!process.estFpCouranteAffectee()){ %>
											<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" name="<%= process.getNOM_LB_BUDGET() %>" style="width:150px" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
												<%=process.forComboHTML(process.getVAL_LB_BUDGET(), process.getVAL_LB_BUDGET_SELECT())%>
											</SELECT>
										<%}else{%>
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_BUDGET() %>" style="width:150px" disabled="disabled">
												<%=process.forComboHTML(process.getVAL_LB_BUDGET(), process.getVAL_LB_BUDGET_SELECT())%>
											</SELECT>
										<%} %>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Numéro délibération : </span>
									</td>
									<td>
										<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="50" name="<%= process.getNOM_EF_NUM_DELIBERATION() %>" size="10" type="text" value="<%= process.getVAL_EF_NUM_DELIBERATION() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
									</td>
								</tr>
								
								<tr>
									<td>
										<span class="sigp2Mandatory"> Date d'application : </span>
									</td>
									<td>
										<INPUT id="<%=process.getNOM_EF_DATE_DEBUT_APPLI_SERV()%>" <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT_APPLI_SERV() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEBUT_APPLI_SERV() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
										<IMG class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/calendrier.gif" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT_APPLI_SERV()%>', 'dd/mm/y');" hspace="5">
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory">NFA :</span>
									</td>
									<td>
										<input <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> id="nfa" align="left" class="sigp2-saisie" maxlength="5" size="5" name="<%= process.getNOM_EF_NFA() %>" value="<%= process.getVAL_EF_NFA() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> OPI : </span>
									</td>
									<td>
										<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_OPI() %>" size="6" type="text" value="<%= process.getVAL_EF_OPI() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
									</td>
								</tr>
							</table>			
						<%}%>
						</fieldset>
						
						<fieldset class="sigp2Fieldset" style="width:400px;">
							<legend class="sigp2Legend">Temps de travail sur le poste</legend>		
							<table>
								<tr>
									<td width="100px;">
										<span class="sigp2Mandatory"> Réglementaire : </span>
									</td>
									<td width="120px">&nbsp;</td>
									<td>					
										<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" name="<%= process.getNOM_LB_REGLEMENTAIRE() %>" style="width:120px" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
											<%=process.forComboHTML(process.getVAL_LB_REGLEMENTAIRE(), process.getVAL_LB_REGLEMENTAIRE_SELECT())%>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Budgété : </span>
									</td>
									<td>
										<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" name="<%= process.getNOM_LB_NATURE_CREDIT() %>" style="width:120px" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
											<%=process.forComboHTML(process.getVAL_LB_NATURE_CREDIT(), process.getVAL_LB_NATURE_CREDIT_SELECT())%>
										</SELECT>
									</td>
									<td>
										<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" name="<%= process.getNOM_LB_BUDGETE() %>" style="width:120px" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
											<%=process.forComboHTML(process.getVAL_LB_BUDGETE(), process.getVAL_LB_BUDGETE_SELECT())%>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Base pointage : </span>
									</td>
									<td colspan="2">
										<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" name="<%= process.getNOM_LB_BASE_HORAIRE_POINTAGE() %>" style="width:120px" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
											<%=process.forComboHTML(process.getVAL_LB_BASE_HORAIRE_POINTAGE(), process.getVAL_LB_BASE_HORAIRE_POINTAGE_SELECT())%>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Base congé : </span>
									</td>
									<td colspan="2">
										<SELECT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" name="<%= process.getNOM_LB_BASE_HORAIRE_ABSENCE() %>" style="width:120px" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
											<%=process.forComboHTML(process.getVAL_LB_BASE_HORAIRE_ABSENCE(), process.getVAL_LB_BASE_HORAIRE_ABSENCE_SELECT())%>
										</SELECT>
									</td>
								</tr>
							</table>
						</fieldset>
					</div>
				</div>				
					<fieldset class="sigp2Fieldset" style="width:1020px">
						<legend class="sigp2Legend">Descriptif du poste</legend>
						<%if (process.getVAL_ST_INFO_FP().length() != 0){ %>
							<span><%= process.getVAL_ST_INFO_FP()%></span>
						<br/><br/>
						<%}%>
						<table>
							<tr>
								<td width="140px">
									<span class="sigp2Mandatory">Titre :</span>
								</td>
								<td>
									<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> id="listeTitrePoste" class="sigp2-saisie" name="<%= process.getNOM_EF_TITRE_POSTE() %>" style="width:600px" type="text" value="<%= process.getVAL_EF_TITRE_POSTE() %>"<%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
								</td>
							</tr>
							<tr>
								<td>
									<span class="<%= process.responsableObligatoire ? "sigp2Mandatory" : "sigp2" %> ">Responsable hiér. :</span>
								</td>
								<td>
									<% if(null != process.getVAL_ST_RESPONSABLE() && !"".equals(process.getVAL_ST_RESPONSABLE().trim())) { %>
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_RESPONSABLE_HIERARCHIQUE()%>">
									<% } %>
									<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" readonly="readonly" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_ST_RESPONSABLE() %>" style="width:100px" type="text" value="<%= process.getVAL_ST_RESPONSABLE() %>">
									<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHER_RESPONSABLE()%>">
									<span class="sigp2-saisie"><%= process.getVAL_ST_INFO_RESP()%></span>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2">Fiche de poste remplacée :</span>
								</td>
								<td>
									<% if(null != process.getVAL_ST_REMPLACEMENT() && !"".equals(process.getVAL_ST_REMPLACEMENT().trim())) { %>
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_REMPLACEMENT()%>">
									<% } %>
									<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" readonly="readonly" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> name="<%= process.getNOM_ST_REMPLACEMENT() %>" style="width:100px" type="text" value="<%= process.getVAL_ST_REMPLACEMENT() %>">
									<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHER_REMPLACEMENT()%>">				
									<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_REMPLACEMENT()%>" src="images/suppression.gif" height="16px" width="16px" >
									<span class="sigp2-saisie"><%= process.getVAL_ST_INFO_REMP()%></span>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2">Observation :</span>
								</td>
								<td>
									<textarea style="width: 800px;color: red;" <%= process.estFDPInactive ?  "disabled='disabled'" : "" %>  rows="4" cols="190" class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_OBSERVATION()%>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>><%= process.getVAL_EF_OBSERVATION() %></textarea>
								</td>
							</tr>
						</table>
					</fieldset>
		
					<fieldset class="sigp2Fieldset" style="width:1020px">
						<legend class="sigp2Legend">Mission</legend>
						<br/>
						<textarea <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> rows="4" cols="190" class="sigp2-saisie" style="margin-right:10px;" name="<%= process.getNOM_EF_MISSIONS()%>" ><%= process.getVAL_EF_MISSIONS() %></textarea>
						<br/>
					</fieldset>					
					
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					    <legend class="sigp2Legend">Activités</legend>
					    <span class="sigp2Mandatory" style="width:150px"> Ajouter une activité : </span>
							<span class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
					            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_ACTIVITE()%>" style="margin-bottom:5px;">

							</span>
						<BR/><BR/>
						<table class="display" id="tabActiFP">
							<thead>
								<tr>
									<th>idActi</th>
									<th width="50" >Selection <INPUT class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox" name="CHECK_ALL_ACTI" onClick='activeACTI("<%=process.getListeToutesActi().size() %>")'></th>
									<th>Libellé</th>
									<th>Provenance</th>
								</tr>
							</thead>
							<tbody>
							<%
							if (process.getListeToutesActi()!=null){
								for (int indiceActi = 0;indiceActi<process.getListeToutesActi().size();indiceActi++){
							%>
									<tr>
										<td><%=process.getVAL_ST_ID_ACTI(indiceActi)%></td>
										<td><INPUT class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_ACTI(indiceActi),process.getVAL_CK_SELECT_LIGNE_ACTI(indiceActi))%>></td>
										<td><%=process.getVAL_ST_LIB_ACTI(indiceActi)%></td>
										<td><%=process.getVAL_ST_LIB_ORIGINE_ACTI(indiceActi)%></td>
									</tr>						
							<%
								}
							}
							%>
							</tbody>
						</table>
						<script type="text/javascript">
							$(document).ready(function() {
							    $('#tabActiFP').dataTable({
									"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
									"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false,"bSortable":false},null,null],
									"sScrollY": "275px",
									"bPaginate": false,
									"aaSorting": [],
									"sDom": '<"H"f>t<"F"i>'						
							    });
							} );
						</script>
					</FIELDSET>
					
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					    <legend class="sigp2Legend">Compétences</legend>
					    <div align="left" style="width:980px">
					    	<span class="sigp2Mandatory" style="width:80px"> Ajouter : </span>
					    	<span class="sigp2Mandatory" style="width:50px">Savoir </span>
							<span class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
					            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_SAVOIR()%>" style="margin-bottom:5px;">
							</span>
							<span class="sigp2Mandatory" style="width:50px"></span>
							<span class="sigp2Mandatory" style="width:80px">Savoir Faire </span>
							<span class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
					            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE()%>" style="margin-bottom:5px;">
							</span>
							<span class="sigp2Mandatory" style="width:50px"></span>
							<span class="sigp2Mandatory" style="width:180px">Comportements professionnels </span>
							<span class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>">
					            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT()%>" style="margin-bottom:5px;">

							</span>
						</div>
						<BR/>
						<table class="display" id="tabCompFP">
							<thead>
								<tr>
									<th>idComp</th>
									<th width="50" >Selection<INPUT class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox" name="CHECK_ALL_COMP" onClick='activeCOMP("<%=process.getListeToutesComp().size() %>")'></th>
									<th width="700">Libellé</th>
									<th>Type</th>
									<th>Provenance</th>
								</tr>
							</thead>
							<tbody>
							<%
							if (process.getListeToutesComp()!=null){
								for (int indiceComp = 0;indiceComp<process.getListeToutesComp().size();indiceComp++){
							%>
									<tr>
										<td><%=process.getVAL_ST_ID_COMP(indiceComp)%></td>
										<td width="50"><INPUT type="checkbox" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_COMP(indiceComp),process.getVAL_CK_SELECT_LIGNE_COMP(indiceComp))%>></td>
										<td width="700"><%=process.getVAL_ST_LIB_COMP(indiceComp)%></td>
										<td><%=process.getVAL_ST_TYPE_COMP(indiceComp)%></td>
										<td><%=process.getVAL_ST_LIB_ORIGINE_COMP(indiceComp)%></td>
									</tr>						
							<%
								}
							}
							%>
							</tbody>
						</table>
						<script type="text/javascript">
							$(document).ready(function() {
							    $('#tabCompFP').dataTable({
									"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
									"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false,"bSortable":false},null,null,null],
									"sScrollY": "275px",
									"bPaginate": false,
									"aaSorting": [],
									"sDom": '<"H"f>t<"F"i>'						
							    });
							} );
						</script>
					</FIELDSET>
					<fieldset class="sigp2Fieldset" style="width:1030px">
						<legend class="sigp2Legend">Spécificités</legend>
						<BR/>
						<div align="left" style="float:left;">
							<span class="sigp2" style="text-align:left;width:900;"><u>Avantage(s) en nature</u></span>
							<%if(process.getListeAvantage()!= null && process.getListeAvantage().size()>0){ %>
							<br/><br/>
							<span style="margin-left:5px;position:relative;width:350px;text-align: left;">Type</span>
							<span style="position:relative;width:90px;text-align: center;">Montant</span>
							<span style="position:relative;text-align: left;">&nbsp;Nature</span>
							<br/>
							<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
									<table class="sigp2NewTab" style="text-align:left;width:980px;">
										<%
										int indiceAvantage = 0;
										if (process.getListeAvantage()!=null){
											for (int i = 0;i<process.getListeAvantage().size();i++){
										%>
												<tr>
													<td class="sigp2NewTab-liste" style="position:relative;width:350px;text-align: left;"><%=process.getVAL_ST_AV_TYPE(indiceAvantage)%></td>
													<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: right;"><%=process.getVAL_ST_AV_MNT(indiceAvantage)%></td>
													<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_AV_NATURE(indiceAvantage)%></td>
												</tr>
												<%
												indiceAvantage++;
											}
										}%>
									</table>	
							</div>
							<%} %>
							<BR/><BR/>
						</div>
						<div align="left" style="float:left;width:980px">
							<span class="sigp2" style="text-align:left;width:900;"><u>Délégation(s)</u></span>
							<%if(process.getListeDelegation()!= null && process.getListeDelegation().size()>0){ %>
							<br/><br/>
							<span style="margin-left:5px;position:relative;width:250px;">Type</span>
							<span style="position:relative;">Commentaire</span>
							<br/>
							<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
									<table class="sigp2NewTab" style="text-align:left;width:980px;">
										<%
										int indiceDelegation = 0;
										if (process.getListeDelegation()!=null){
											for (int i = 0;i<process.getListeDelegation().size();i++){
										%>
												<tr>
													<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: left;"><%=process.getVAL_ST_DEL_TYPE(indiceDelegation)%></td>
													<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_DEL_COMMENTAIRE(indiceDelegation)%></td>
												</tr>
												<%
												indiceDelegation++;
											}
										}%>
									</table>	
							</div>
							<%} %>
							<BR/><BR/>
						</div>
						<div align="left" style="float:left;width:980px">
							<span class="sigp2" style="text-align:left;width:900;"><u>Régime(s) indemnitaire(s)</u></span>
							<%if(process.getListeRegime()!= null && process.getListeRegime().size()>0){ %>
							<br/><br/>
							<span style="margin-left:5px;position:relative;width:100px;text-align: left;">Type</span>
							<span style="position:relative;width:90px;text-align: center;">Forfait</span>
							<span style="position:relative;text-align: left;">Nb points</span>
							<br/>
							<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
									<table class="sigp2NewTab" style="text-align:left;width:980px;">
										<%
										int indiceRegime = 0;
										if (process.getListeRegime()!=null){
											for (int i = 0;i<process.getListeRegime().size();i++){
										%>
												<tr>
													<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: left;"><%=process.getVAL_ST_REG_TYPE(indiceRegime)%></td>
													<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: right;"><%=process.getVAL_ST_REG_FORFAIT(indiceRegime)%></td>
													<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_REG_NB_PTS(indiceRegime)%></td>
												</tr>
												<%
												indiceRegime++;
											}
										}%>
									</table>	
							</div>
							<%} %>
							<BR/><BR/>
						</div>
						<div align="left" style="float:left;width:980px">
							<span class="sigp2" style="text-align:left;width:900;"><u>Prime(s) de pointage</u></span>
							<%if(process.getListePrimePointageFP()!= null && process.getListePrimePointageFP().size()>0){ %>
							<br/><br/>
							<span style="margin-left:5px;position:relative;text-align: left;">Rubrique</span>
							<br/>
							<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
									<table class="sigp2NewTab" style="text-align:left;width:980px;">
										<%
										int indicePrime = 0;
										if (process.getListePrimePointageFP()!=null){
											for (int i = 0;i<process.getListePrimePointageFP().size();i++){
										%>
												<tr>
													<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_PP_RUBR(indicePrime)%></td>
												</tr>
												<%
												indicePrime++;
											}
										}%>
									</table>	
							</div>
							<%} %>
							<BR/><BR/>
						</div>
						<BR/>
						<% if (!process.estFDPInactive && process.isAfficherModifSpecificites()){ %>
							<INPUT type="submit" value="Modifier Spécificités" name="<%=process.getNOM_PB_MODIFIER_SPECIFICITES()%>" class="sigp2-Bouton-200">
						<%}%>
					</fieldset>
				<% } %>
				<% if (process.getEmploiPrimaire() != null){ %>
					<FIELDSET style="text-align: center; margin: 10px; width:1020px;" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>">
					<% if (process.ACTION_CREATION.equals(process.getVAL_ST_ACTION()) ){ %>
						<INPUT type="submit" value="Créer" name="<%=process.getNOM_PB_CREER()%>" class="sigp2-Bouton-100">
					<%} else if (process.ACTION_DUPLICATION.equals(process.getVAL_ST_ACTION())) {%>
						<INPUT type="submit" value="Dupliquer" name="<%=process.getNOM_PB_CREER()%>" class="sigp2-Bouton-100">
					<%} else {%>
						<INPUT type="submit" value="Modifier" name="<%=process.getNOM_PB_CREER()%>" class="sigp2-Bouton-100">
						<INPUT type="submit" value="Imprimer" name="<%=process.getNOM_PB_IMPRIMER()%>" class="sigp2-Bouton-100">
					<%} %>
					<INPUT type="submit" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>" class="sigp2-Bouton-100">
					</FIELDSET>
					<FIELDSET style="text-align: center; margin: 10px; width:1020px;" class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "sigp2Fieldset") %>">
						<INPUT type="submit" value="Imprimer" name="<%=process.getNOM_PB_IMPRIMER()%>" class="sigp2-Bouton-100">
					</FIELDSET>
				<%} %>
			<% } %>
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SELECT_STATUT()%>" value="x">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_GRADE()%>">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE()%>">	
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_INFO_SERVICE()%>">						
			<%=process.getUrlFichier()%>
		</FORM>
	</BODY>
</HTML>