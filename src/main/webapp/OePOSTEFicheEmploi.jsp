<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.enums.EnumTypeCompetence"%>
<%@page import="nc.mairie.metier.parametrage.CodeRome"%>
<HTML>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFicheEmploi" id="process" scope="session"></jsp:useBean>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Gestion des fiches emploi</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></script>
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
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigneComp(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById("ligne"+i).className="";
			} 
		 document.getElementById("ligne"+id).className="selectLigne";
		}

</SCRIPT>
		<%
		ArrayList<CodeRome> listeCodeRome = process.getListeCodeRome();
		
		String res = 	"<script language=\"javascript\">\n"+
				"var availableCodeRome = new Array(\n";
		
		for (int i = 0; i < listeCodeRome.size(); i++){
			res+= "   \""+((CodeRome)listeCodeRome.get(i)).getLibCodeRome()+"\"";
			if (i+1 < listeCodeRome.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listeCodeRome").autocomplete({source:availableCodeRome
				});
			});
		</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
</HEAD>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<fieldset class="sigp2Fieldset" style="text-align: center;width: 1030px; float: left">
			<span class="sigp2Mandatory"> Recherche par Ref. Mairie : </span>
			<INPUT class="sigp2-saisiemajuscule" maxlength="8" name="<%= process.getNOM_EF_RECHERCHE_REF_MAIRIE() %>" size="10"	type="text" value="<%= process.getVAL_EF_RECHERCHE_REF_MAIRIE() %>" style="margin-right:10px;">
            <INPUT title="Recherche" type="image" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHER_FE()%>">
            <INPUT title="Recherche avancée" type="image" src="images/rechercheAvancee.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHE_AVANCEE()%>" >
            <INPUT title="Créer une FE" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_FE()%>">
            <INPUT title="Dupliquer une FE" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/dupliquer.gif" height="16px" width="16px" name="<%=process.getNOM_PB_DUPLIQUER_FE()%>">
            <INPUT title="Supprimer une FE" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_FE()%>">
			<br/>
		</fieldset>
		
	<% if (!process.ACTION_RECHERCHE.equals(process.getVAL_ST_ACTION())){ %>
		<% if (!process.isSuppression() && !MairieUtils.estConsultation(request, process.getNomEcran())) {%>
			<table width="1030px" border="0">
				<tr>
					<td width="505px" height="410px" valign="top">
						<fieldset class="sigp2Fieldset" style="text-align:left;">
							<legend class="sigp2Legend">Identification</legend>
							<br/>
							<table border="0">
								<tr>
									<td width="150px">
										<span class="sigp2Mandatory"> Ref. Mairie : </span>
									</td>
									<td width="260px">
										<INPUT class="sigp2-saisie" maxlength="8" disabled="disabled" name="<%= process.getNOM_EF_REF_MAIRIE() %>" size="10" type="text" value="<%= process.getVAL_EF_REF_MAIRIE() %>">
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
							<% if (process.isModification()) {%>
								<tr>
									<td>
										<span class="sigp2">Code Rome :</span>
									</td>
									<td>
										<INPUT id="listeCodeRome" size="10" class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_CODE_ROME() %>" maxlength="5" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Domaine d'activité : </span>
									</td>
									<td>
										<SELECT style="width: 300px;" class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE() %>" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT())%>
										</SELECT>
									</td>
								</tr>	
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>	
								<tr>
									<td>
										<span class="sigp2Mandatory"> Famille de l'emploi : </span>
									</td>
									<td>
										<SELECT style="width: 300px;" class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT())%>
										</SELECT>
									</td>
								</tr>	
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>	
								<tr>
									<td>
										<span class="sigp2Mandatory"> Nom du métier / emploi : </span>
									</td>
									<td>
										<INPUT style="width: 300px;" class="sigp2-saisie" maxlength="100" disabled="disabled"name="<%= process.getNOM_EF_NOM_METIER() %>" type="text" value="<%= process.getVAL_EF_NOM_METIER() %>">
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
							<%}else{ %>
								<tr>
									<td>
										<span class="sigp2">Code Rome :</span>
									</td>
									<td>
										<INPUT size="10" id="listeCodeRome" class="sigp2-saisiemajuscule" name="<%= process.getNOM_EF_CODE_ROME() %>" maxlength="5" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>	
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Domaine d'activité : </span>
									</td>
									<td>
										<SELECT style="width: 300px;" class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE() %>">
											<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT())%>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Famille de l'emploi : </span>
									</td>
									<td>
										<SELECT style="width: 300px;" class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>">
											<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT())%>
										</SELECT>
									</td>
								</tr>	
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory" style="width:150px"> Nom du métier / emploi : </span>
									</td>
									<td>
										<INPUT style="width: 300px;" class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_NOM_METIER() %>" type="text" value="<%= process.getVAL_EF_NOM_METIER() %>">
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
							<%} %>
								<tr>
									<td>
										<span class="sigp2"> Autres Appellations <br/> métiers / emplois </span>
									</td>
									<td>
										<INPUT style="width: 260px;" class="sigp2-saisie" maxlength="100"	name="<%= process.getNOM_EF_AUTRE_APPELLATION() %>" type="text" value="<%= process.getVAL_EF_AUTRE_APPELLATION() %>">
							            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AUTRE_APPELLATION()%>">
							            <INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_AUTRE_APPELLATION()%>">
									</td>
								</tr>
								<tr>
									<td colspan="2">										
										<SELECT style="width: 100%;" size="5" class="sigp2-liste" name="<%=process.getNOM_LB_AUTRE_APPELLATION()%>" onchange="this.selectedIndex=-1;">
											<%=process.forComboHTML(process.getVAL_LB_AUTRE_APPELLATION(), process.getVAL_LB_AUTRE_APPELLATION_SELECT()) %>
										</SELECT>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
					<td width="20px"></td>
					<td width="505px" valign="top">
						<fieldset class="sigp2Fieldset" style="text-align:left;">
							<legend class="sigp2Legend">Cadre statutaire</legend>
							<br/>
							<table border="0">
								<tr>
									<td width="130px">
										<span class="sigp2"> Catégorie / classification : </span>
									</td>
									<td width="280px">
							            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_CATEGORIE()%>">
							            <INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_CATEGORIE()%>">
										<% if (process.isAfficherListeCategorie()) {%>							
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CATEGORIE() %>" style="width:240px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_CATEGORIE() %>")'>
												<%=process.forComboHTML(process.getVAL_LB_CATEGORIE(), process.getVAL_LB_CATEGORIE_SELECT())%>
											</SELECT>	
										<%} %>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>
										<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_EF_CATEGORIE_MULTI() %>" style="width:300px" type="text" value="<%= process.getVAL_EF_CATEGORIE_MULTI() %>">
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Cadres emploi : </span>
									</td>
									<td>				
							            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_CADRE()%>">
							            <INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_CADRE_EMPLOI()%>">	
										<% if (process.isAfficherListeCadre()) {%>				
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CADRE_EMPLOI() %>" style="width:240px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_CADRE_EMPLOI() %>")'>
												<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI(), process.getVAL_LB_CADRE_EMPLOI_SELECT())%>
											</SELECT>						
										<%} %>								
									</td>
								</tr>	
								<tr>
									<td>&nbsp;</td>
									<td>
										<SELECT size="3" style="width :300px;" class="sigp2-liste" name="<%=process.getNOM_LB_CADRE_EMPLOI_MULTI()%>" >
											<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI_MULTI(), process.getVAL_LB_CADRE_EMPLOI_MULTI_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Niveau d'étude : </span>
									</td>
									<td>				
							            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_NIVEAU()%>">
							            <INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_NIVEAU_ETUDE()%>">		
										<% if (process.isAfficherListeNivEt()) {%>	
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_NIVEAU_ETUDE() %>" style="width:240px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE() %>")'>
												<%=process.forComboHTML(process.getVAL_LB_NIVEAU_ETUDE(), process.getVAL_LB_NIVEAU_ETUDE_SELECT())%>
											</SELECT>						
										<%} %>						
									</td>
								</tr>	
								<tr>
									<td>&nbsp;</td>
									<td>
										<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_EF_NIVEAU_ETUDE_MULTI() %>" style="width:300px" type="text" value="<%= process.getVAL_EF_NIVEAU_ETUDE_MULTI() %>">
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Diplôme(s) : </span>
									</td>
									<td>				
							            <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_DIPLOME()%>">
							            <INPUT type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME()%>">		
										<% if (process.isAfficherListeDiplome()) {%>	
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DIPLOME() %>" style="width:240px;" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_DIPLOME() %>")'>
												<%=process.forComboHTML(process.getVAL_LB_DIPLOME(), process.getVAL_LB_DIPLOME_SELECT())%>
											</SELECT>						
										<%} %>					
									</td>
								</tr>		
								<tr>
									<td>&nbsp;</td>
									<td>
										<SELECT size="3" style="width :300px;" class="sigp2-liste" name="<%=process.getNOM_LB_DIPLOME_MULTI()%>">
											<%=process.forComboHTML(process.getVAL_LB_DIPLOME_MULTI(), process.getVAL_LB_DIPLOME_MULTI_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Précisions diplomes : </span>
									</td>
									<td>
										<textarea rows="2" cols="50" class="sigp2-saisie" name="<%= process.getNOM_EF_PRECISIONS_DIPLOMES() %>"	style="overflow:hidden;width:300px"><%= process.getVAL_EF_PRECISIONS_DIPLOMES() %></textarea>
									</td>
								</tr>	
							</table>							
							<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_CADRE_EMPLOI()%>">
							<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_CATEGORIE()%>">
							<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_DIPLOME()%>">
							<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE()%>">
						</fieldset>
					</td>
				</tr>
			</table>	
		
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; clear: both; width:1030px">
			<legend class="sigp2Legend"><B>Définition de l'emploi</B></legend>
			<br/>
			<textarea rows="4" cols="190" class="sigp2-saisie" name="<%= process.getNOM_EF_DEFINITION_EMPLOI() %>"><%= process.getVAL_EF_DEFINITION_EMPLOI() %></textarea>
			<br/>
		</fieldset>
				
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; clear: both; width:1030px">
			<legend class="sigp2Legend">Activités principales</legend>
	        <INPUT type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_ACTIVITE_PRINC()%>">
			<br/>
            <%if(process.getListeActiPrincMulti().size()>0){ %>
				<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceActi = 0;
					if (process.getListeActiPrincMulti()!=null){
						for (int i = 0;i<process.getListeActiPrincMulti().size();i++){
					%>
							<tr id="<%=indiceActi%>" onmouseover="SelectLigne(<%=indiceActi%>,<%=process.getListeActiPrincMulti().size()%>)" >
										<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">											
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_ACTIVITE_PRINC(indiceActi)%>">
										</td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_ACTI(indiceActi)%></td>
							</tr>
							<%
							indiceActi++;
						}
					}%>
					</table>	
				</div>
			<br/>
			<%} %>
		</fieldset>
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:1030px;">
			<legend class="sigp2Legend">Compétences</legend>
			<span class="sigp2Mandatory" style="text-align:center">
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_S())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Savoir
				<span style="width:10px"></span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_SF())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Savoir-faire
				<span style="width:10px"></span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_C())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Comportements professionnels
			</span>
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGER_TYPE()%>" value="OK">	
			<br/><br/>
			<%if(process.getTypeCompetenceCourant()!=null && process.getTypeCompetenceCourant().getIdTypeCompetence().equals(EnumTypeCompetence.SAVOIR_FAIRE.getCode())){ %>
				<div align="left">
					<INPUT style="margin:5px;" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE()%>">
		            <BR/>
		            <%if(process.getListeSavoirFaireMulti().size()>0){ %>
						<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCompSF = 0;
							if (process.getListeSavoirFaireMulti()!=null){
								for (int i = 0;i<process.getListeSavoirFaireMulti().size();i++){
							%>
									<tr id="ligne<%=indiceCompSF%>" onmouseover="SelectLigneComp(<%=indiceCompSF%>,<%=process.getListeSavoirFaireMulti().size()%>)" >
												<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">											
													<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_COMPETENCE_SAVOIR_FAIRE(indiceCompSF)%>">
												</td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_COMP_SF(indiceCompSF)%></td>
									</tr>
									<%
									indiceCompSF++;
								}
							}%>
							</table>	
						</div>
					<br/>
					<%} %>
				</div>
				<BR/>
			<%}else if(process.getTypeCompetenceCourant()!=null && process.getTypeCompetenceCourant().getIdTypeCompetence().equals(EnumTypeCompetence.SAVOIR.getCode())){ %>
				<div align="left">
					<INPUT style="margin:5px;" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_SAVOIR()%>">
					<BR/>
		            <%if(process.getListeSavoirFaireMulti().size()>0){ %>
						<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCompS = 0;
							if (process.getListeSavoirMulti()!=null){
								for (int i = 0;i<process.getListeSavoirMulti().size();i++){
							%>
									<tr id="ligne<%=indiceCompS%>" onmouseover="SelectLigneComp(<%=indiceCompS%>,<%=process.getListeSavoirMulti().size()%>)" >
												<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">											
													<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_COMPETENCE_SAVOIR(indiceCompS)%>">
												</td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_COMP_S(indiceCompS)%></td>
									</tr>
									<%
									indiceCompS++;
								}
							}%>
							</table>	
						</div>
					<br/>
					<%} %>
				</div>
				<BR/>
			<% }else if(process.getTypeCompetenceCourant()!=null && process.getTypeCompetenceCourant().getIdTypeCompetence().equals(EnumTypeCompetence.COMPORTEMENT.getCode())){ %>
			<div align="left">
				<INPUT style="margin:5px;" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT()%>">
				<BR/>
		            <%if(process.getListeComportementMulti().size()>0){ %>
						<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCompPro = 0;
							if (process.getListeComportementMulti()!=null){
								for (int i = 0;i<process.getListeComportementMulti().size();i++){
							%>
									<tr id="ligne<%=indiceCompPro%>" onmouseover="SelectLigneComp(<%=indiceCompPro%>,<%=process.getListeComportementMulti().size()%>)" >
												<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">											
													<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_COMPETENCE_COMPORTEMENT(indiceCompPro)%>">
												</td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_COMP_PRO(indiceCompPro)%></td>
									</tr>
									<%
									indiceCompPro++;
								}
							}%>
							</table>	
						</div>
					<br/>
					<%} %>
			</div>
			<%} %>
		</fieldset>	
		<%} else {%>
			<table width="1030px" border="0">
				<tr>
					<td width="505px" height="410px" valign="top">
						<fieldset class="sigp2Fieldset" style="text-align:left;">
							<legend class="sigp2Legend">Identification</legend>
							<br/>
							<table>
								<tr>
									<td width="150px;">
										<span class="sigp2Mandatory"> Ref. Mairie : </span>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" disabled="disabled" name="<%= process.getNOM_EF_REF_MAIRIE() %>" size="10" type="text" value="<%= process.getVAL_EF_REF_MAIRIE() %>">									
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Code Rome : </span>
									</td>
									<td>
										<INPUT <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_CODE_ROME() %>" size="6" type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">								
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Domaine d'activité : </span>
									</td>
									<td>
										<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE() %>" style="width:260px" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT())%>
										</SELECT>								
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Famille de l'emploi : </span>
									</td>
									<td>
										<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>" style="width:260px" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT())%>
										</SELECT>								
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2Mandatory"> Nom du métier / emploi : </span>
									</td>
									<td>		
										<INPUT class="sigp2-saisie" maxlength="100" disabled="disabled" name="<%= process.getNOM_EF_NOM_METIER() %>" type="text" value="<%= process.getVAL_EF_NOM_METIER() %>" style="width:260px">					
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Autres Appellations <br/> métiers / emplois </span>
									</td>
									<td>
										<SELECT size="2" style="width : 260px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_AUTRE_APPELLATION()%>" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_AUTRE_APPELLATION(), process.getVAL_LB_AUTRE_APPELLATION_SELECT()) %>
										</SELECT>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>					
					<td width="20px"></td>
					<td width="505px" valign="top">			
						<fieldset class="sigp2Fieldset" style="text-align:left;">
							<legend class="sigp2Legend">Cadre statutaire</legend>
							<table>
								<tr>
									<td width="150px">
								<span class="sigp2"> Catégorie / classification : </span>
									</td>
									<td>
								<INPUT class="sigp2-saisie" disabled="disabled"	name="<%= process.getNOM_EF_CATEGORIE_MULTI() %>" style="width:260px" type="text" value="<%= process.getVAL_EF_CATEGORIE_MULTI() %>">
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Cadres emploi : </span>
									</td>
									<td>
										<SELECT size="3" style="width :260px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_CADRE_EMPLOI_MULTI()%>" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI_MULTI(), process.getVAL_LB_CADRE_EMPLOI_MULTI_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Niveau d'étude : </span>
									</td>
									<td>
										<INPUT class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_EF_NIVEAU_ETUDE_MULTI() %>" style="width:260px" type="text" value="<%= process.getVAL_EF_NIVEAU_ETUDE_MULTI() %>">
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Diplôme(s) : </span>
									</td>
									<td>
										<SELECT size="3" style="width :260px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_DIPLOME_MULTI()%>" disabled="disabled">
											<%=process.forComboHTML(process.getVAL_LB_DIPLOME_MULTI(), process.getVAL_LB_DIPLOME_MULTI_SELECT()) %>
										</SELECT>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td></td>
								</tr>
								<tr>
									<td>
										<span class="sigp2"> Précisions diplomes : </span>
									</td>
									<td>
										<textarea rows="2" cols="50" class="sigp2-saisie" name="<%= process.getNOM_EF_PRECISIONS_DIPLOMES() %>"	style="overflow:hidden;width:260px" disabled="disabled"><%= process.getVAL_EF_PRECISIONS_DIPLOMES() %></textarea>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			</table>
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:1030px">
			<legend class="sigp2Legend"><B>Définition de l'emploi</B></legend>
			<br/>
			<textarea rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_EF_DEFINITION_EMPLOI() %>" style="margin-right:10px;" disabled="disabled"><%= process.getVAL_EF_DEFINITION_EMPLOI() %></textarea>
			<br/>
		</fieldset>
		
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:1030px">
			<legend class="sigp2Legend"><B>Activités</B></legend>
			<span class="sigp2Mandatory" style="width:150px;margin:5px;"> Activités principales</span>
            <%if(process.getListeActiPrincMulti().size()>0){ %>
				<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceActi = 0;
					if (process.getListeActiPrincMulti()!=null){
						for (int i = 0;i<process.getListeActiPrincMulti().size();i++){
					%>
							<tr id="<%=indiceActi%>" onmouseover="SelectLigne(<%=indiceActi%>,<%=process.getListeActiPrincMulti().size()%>)" >										
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_ACTI(indiceActi)%></td>
							</tr>
							<%
							indiceActi++;
						}
					}%>
					</table>	
				</div>
			<br/>
			<%} %>
		<br/>
		</fieldset>
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:1030px;">
			<legend class="sigp2Legend">Compétences</legend>
			<span class="sigp2Mandatory" style="text-align:center">
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_S())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Savoir
				<span style="width:10px"></span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_SF())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Savoir-faire
				<span style="width:10px"></span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_C())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Comportements professionnels
			</span>
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGER_TYPE()%>" value="OK">	
			<br/><br/>
			<%if(process.getTypeCompetenceCourant()!=null && process.getTypeCompetenceCourant().getIdTypeCompetence().equals(EnumTypeCompetence.SAVOIR_FAIRE.getCode())){ %>
				<div align="left">
		            <%if(process.getListeSavoirFaireMulti().size()>0){ %>
						<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCompSF = 0;
							if (process.getListeSavoirFaireMulti()!=null){
								for (int i = 0;i<process.getListeSavoirFaireMulti().size();i++){
							%>
									<tr id="ligne<%=indiceCompSF%>" onmouseover="SelectLigneComp(<%=indiceCompSF%>,<%=process.getListeSavoirFaireMulti().size()%>)" >
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_COMP_SF(indiceCompSF)%></td>
									</tr>
									<%
									indiceCompSF++;
								}
							}%>
							</table>	
						</div>
					<br/>
					<%} %>
				</div>
				<BR/>
			<%}else if(process.getTypeCompetenceCourant()!=null && process.getTypeCompetenceCourant().getIdTypeCompetence().equals(EnumTypeCompetence.SAVOIR.getCode())){ %>
				<div align="left">
		            <%if(process.getListeSavoirMulti().size()>0){ %>
						<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCompS = 0;
							if (process.getListeSavoirMulti()!=null){
								for (int i = 0;i<process.getListeSavoirMulti().size();i++){
							%>
									<tr id="ligne<%=indiceCompS%>" onmouseover="SelectLigneComp(<%=indiceCompS%>,<%=process.getListeSavoirMulti().size()%>)" >
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_COMP_S(indiceCompS)%></td>
									</tr>
									<%
									indiceCompS++;
								}
							}%>
							</table>	
						</div>
					<br/>
					<%} %>
				</div>
				<BR/>
			<% }else if(process.getTypeCompetenceCourant()!=null && process.getTypeCompetenceCourant().getIdTypeCompetence().equals(EnumTypeCompetence.COMPORTEMENT.getCode())){ %>
			<div align="left">
		            <%if(process.getListeComportementMulti().size()>0){ %>
						<div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceCompPro = 0;
							if (process.getListeComportementMulti()!=null){
								for (int i = 0;i<process.getListeComportementMulti().size();i++){
							%>
									<tr id="ligne<%=indiceCompPro%>" onmouseover="SelectLigneComp(<%=indiceCompPro%>,<%=process.getListeComportementMulti().size()%>)" >
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_COMP_PRO(indiceCompPro)%></td>
									</tr>
									<%
									indiceCompPro++;
								}
							}%>
							</table>	
						</div>
					<br/>
					<%} %>
			</div>
			<%} %>
		</fieldset>	
		<%} %>
	<%} %>
		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align: center; margin: 10px;width:1030px">
			<% if (process.isSuppression()) {%>
				<INPUT type="submit" value="Supprimer" name="<%=process.getNOM_PB_VALIDER()%>" class="sigp2-Bouton-100">	
				<INPUT type="submit" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>" class="sigp2-Bouton-100">	
			<%} else if (process.isModification()) {%>
				<INPUT type="submit" value="Modifier" name="<%=process.getNOM_PB_VALIDER()%>" class="sigp2-Bouton-100">
				<INPUT type="submit" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>" class="sigp2-Bouton-100">
			<%} else if(!process.ACTION_RECHERCHE.equals(process.getVAL_ST_ACTION())) {%>
				<INPUT type="submit" value="Créer" name="<%=process.getNOM_PB_VALIDER()%>" class="sigp2-Bouton-100">
				<INPUT type="submit" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>" class="sigp2-Bouton-100">
			<%} %>
		</FIELDSET>
		<br>
	</FORM>
</BODY>
</HTML>
