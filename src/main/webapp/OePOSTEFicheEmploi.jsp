<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.enums.EnumTypeCompetence"%>
<%@page import="nc.mairie.metier.parametrage.CodeRome"%>
<HTML>
<jsp:useBean class="nc.mairie.gestionagent.process.OePOSTEFicheEmploi" id="process" scope="session"></jsp:useBean>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Gestion des fiches emploi</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
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
		java.util.ArrayList listeCodeRome = process.getListeCodeRome();
		
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
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" onload="window.parent.frames('refAgent').location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<fieldset class="sigp2Fieldset" style="text-align: center;width: 1030px; float: left">
			<span class="sigp2Mandatory"> Recherche par Ref. Mairie : </span>
			<INPUT tabindex="" class="sigp2-saisiemajuscule" maxlength="8" name="<%= process.getNOM_EF_RECHERCHE_REF_MAIRIE() %>" size="10"	type="text" value="<%= process.getVAL_EF_RECHERCHE_REF_MAIRIE() %>" style="margin-right:10px;">
            <INPUT title="Recherche" tabindex="" type="image" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHER_FE()%>">
            <INPUT title="Recherche avancée" tabindex="" type="image" src="images/rechercheAvancee.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHE_AVANCEE()%>" >
            <INPUT title="Créer une FE" tabindex="" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_FE()%>">
            <INPUT title="Dupliquer une FE" tabindex="" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/dupliquer.gif" height="16px" width="16px" name="<%=process.getNOM_PB_DUPLIQUER_FE()%>">
            <INPUT title="Supprimer une FE" tabindex="" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_FE()%>">
			<br/>
		</fieldset>
		
	<% if (!process.ACTION_RECHERCHE.equals(process.getVAL_ST_ACTION())){ %>
		<% if (!process.isSuppression() && !MairieUtils.estConsultation(request, process.getNomEcran())) {%>
			<div style="width:1052px">
				<fieldset class="sigp2Fieldset" style="text-align:left;width:505px;float:left;height:410px;">
				<legend class="sigp2Legend">Identification</legend>
				<br/>
				<span class="sigp2Mandatory" style="width:150px"> Ref. Mairie : </span>
				<INPUT tabindex="2" class="sigp2-saisie" maxlength="8" disabled="disabled"
					name="<%= process.getNOM_EF_REF_MAIRIE() %>" size="10"
					type="text" value="<%= process.getVAL_EF_REF_MAIRIE() %>">
				<br/><br/>
				<br/>
				<% if (process.isModification()) {%>
					<span class="sigp2" style="width:147px">Code Rome :</span>
					<INPUT <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> tabindex="3" id="listeCodeRome" class="sigp2-saisiemajuscule"
						name="<%= process.getNOM_EF_CODE_ROME() %>" maxlength="5" size="6" 
						type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">						
					<br/><br/>
					<br/>
					<span class="sigp2Mandatory" style="width:150px"> Domaine d'activité : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE() %>" style="width=328px" disabled="disabled">
						<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT())%>
					</SELECT>
					<br/><br/>
					<br/>
					<span class="sigp2Mandatory" style="width:150px"> Famille de l'emploi : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>" style="width=328px" disabled="disabled">
						<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT())%>
					</SELECT>
					<br/><br/>
					<br/>
					<span class="sigp2Mandatory" style="width:150px"> Nom du métier / emploi : </span>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="100" disabled="disabled"
						name="<%= process.getNOM_EF_NOM_METIER() %>"
						type="text" value="<%= process.getVAL_EF_NOM_METIER() %>" style="width:328px">
				<%}else{ %>
					<span class="sigp2" style="width:147px">Code Rome :</span>
					<INPUT <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> tabindex="3" id="listeCodeRome" class="sigp2-saisiemajuscule"
						name="<%= process.getNOM_EF_CODE_ROME() %>" maxlength="5" size="6"
						type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">						
					<br/><br/>
					<br/>
					<span class="sigp2Mandatory" style="width:150px"> Domaine d'activité : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE() %>" style="width=328px">
						<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT())%>
					</SELECT>
					<br/><br/>
					<br/>
					<span class="sigp2Mandatory" style="width:150px"> Famille de l'emploi : </span>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>" style="width=328px">
						<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT())%>
					</SELECT>
					<br/><br/>
					<br/>
					<span class="sigp2Mandatory" style="width:150px"> Nom du métier / emploi : </span>
					<INPUT tabindex="" class="sigp2-saisie" maxlength="100"
						name="<%= process.getNOM_EF_NOM_METIER() %>"
						type="text" value="<%= process.getVAL_EF_NOM_METIER() %>" style="width:328px">
				<%} %>
				<br/><br/>
				<br/>
				<span class="sigp2" style="width:150px"> Autres Appellations <br/> métiers / emplois </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="100"	name="<%= process.getNOM_EF_AUTRE_APPELLATION() %>" 
					type="text" value="<%= process.getVAL_EF_AUTRE_APPELLATION() %>" style="margin-right:10px;width: 280px">
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AUTRE_APPELLATION()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_AUTRE_APPELLATION()%>">
				
				<SELECT size="5" style="width:478px;font-family:monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_AUTRE_APPELLATION()%>" onchange="this.selectedIndex=-1;">
					<%=process.forComboHTML(process.getVAL_LB_AUTRE_APPELLATION(), process.getVAL_LB_AUTRE_APPELLATION_SELECT()) %>
				</SELECT>
				<br/><br/>
			</fieldset>
			
			<fieldset class="sigp2Fieldset" style="text-align:left;width:505px;float:left;height:410px;">
				<legend class="sigp2Legend">Cadre statutaire</legend>
				<br/>
				<span class="sigp2" style="width:150px"> Catégorie / classification : </span>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_CATEGORIE()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_CATEGORIE()%>">
				<% if (process.isAfficherListeCategorie()) {%>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CATEGORIE() %>" style="width:290px;margin-bottom:5px" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_CATEGORIE() %>")'>
					<%=process.forComboHTML(process.getVAL_LB_CATEGORIE(), process.getVAL_LB_CATEGORIE_SELECT())%>
				</SELECT>
				<%} %>
				<br/>			
				<span class="sigp2" style="width:150px"></span>
				<INPUT tabindex="" class="sigp2-saisie" disabled="disabled"
					name="<%= process.getNOM_EF_CATEGORIE_MULTI() %>" style="width:328px"
					type="text" value="<%= process.getVAL_EF_CATEGORIE_MULTI() %>">
				<br/><br/>
				<span class="sigp2" style="width:150px"> Cadres emploi : </span>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_CADRE()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_CADRE_EMPLOI()%>">
				<% if (process.isAfficherListeCadre()) {%>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CADRE_EMPLOI() %>" style="width:290px;margin-bottom:5px" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_CADRE_EMPLOI() %>")'>
					<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI(), process.getVAL_LB_CADRE_EMPLOI_SELECT())%>
				</SELECT>
				<%} %>
				<br/>
				<span class="sigp2" style="width:150px"></span>
				<SELECT size="3" style="width :328px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_CADRE_EMPLOI_MULTI()%>" >
					<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI_MULTI(), process.getVAL_LB_CADRE_EMPLOI_MULTI_SELECT()) %>
				</SELECT>
				<br/><br/>
				<span class="sigp2" style="width:150px"> Niveau d'étude : </span>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_NIVEAU()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_NIVEAU_ETUDE()%>">
	            <% if (process.isAfficherListeNivEt()) {%>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_NIVEAU_ETUDE() %>" style="width:290px;margin-bottom:5px" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE() %>")'>
						<%=process.forComboHTML(process.getVAL_LB_NIVEAU_ETUDE(), process.getVAL_LB_NIVEAU_ETUDE_SELECT())%>
					</SELECT>
				<%} %>
				<br />
				<span class="sigp2" style="width:150px"></span>
				<INPUT tabindex="" class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_EF_NIVEAU_ETUDE_MULTI() %>" style="width:328px" type="text"
					value="<%= process.getVAL_EF_NIVEAU_ETUDE_MULTI() %>">
				<br/><br/>
				<span class="sigp2" style="width:150px"> Diplôme(s) : </span>
	            <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AFFICHER_LISTE_DIPLOME()%>">
	            <INPUT tabindex="" type="image" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME()%>">
	            <% if (process.isAfficherListeDiplome()) {%>
					<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DIPLOME() %>" style="width:290px;margin-bottom:5px" onchange='executeBouton("<%=process.getNOM_PB_AJOUTER_DIPLOME() %>")'>
						<%=process.forComboHTML(process.getVAL_LB_DIPLOME(), process.getVAL_LB_DIPLOME_SELECT())%>
					</SELECT>
				<%} %>
				<br />
				<span class="sigp2" style="width:150px"></span>
				<SELECT size="3" style="width :328px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_DIPLOME_MULTI()%>">
					<%=process.forComboHTML(process.getVAL_LB_DIPLOME_MULTI(), process.getVAL_LB_DIPLOME_MULTI_SELECT()) %>
				</SELECT>
				<br/><br/>
				<span class="sigp2" style="width:150px"> Précisions diplomes : </span>
				<textarea tabindex="" rows="2" cols="50" class="sigp2-saisie" name="<%= process.getNOM_EF_PRECISIONS_DIPLOMES() %>"	style="overflow:hidden;width:328px"><%= process.getVAL_EF_PRECISIONS_DIPLOMES() %></textarea>
				
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_CADRE_EMPLOI()%>">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_CATEGORIE()%>">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_DIPLOME()%>">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_AJOUTER_NIVEAU_ETUDE()%>">
			</fieldset>
		</div>		
		
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; clear: both; width:1030px">
			<legend class="sigp2Legend"><B>Définition de l'emploi</B></legend>
			<br/>
			<textarea tabindex="" rows="4" cols="190" class="sigp2-saisie" name="<%= process.getNOM_EF_DEFINITION_EMPLOI() %>"><%= process.getVAL_EF_DEFINITION_EMPLOI() %></textarea>
			<br/>
		</fieldset>
				
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; clear: both; width:1030px">
			<legend class="sigp2Legend"><B>Activités</B></legend>
			<span class="sigp2Mandatory" style="width:150px;margin:5px;"> Activités principales</span>
	        <INPUT tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_ACTIVITE_PRINC()%>">
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
					<INPUT style="margin:5px;" tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE()%>">
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
					<INPUT style="margin:5px;" tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_SAVOIR()%>">
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
				<INPUT style="margin:5px;" tabindex="" type="image" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT()%>">
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
		<div>
			<fieldset class="sigp2Fieldset" style="text-align:left;width:505px;float:left;height:470px;">
				<legend class="sigp2Legend">Identification</legend>
				<br/>
				<span class="sigp2Mandatory" style="width:150px"> Ref. Mairie : </span>
				<INPUT tabindex="2" class="sigp2-saisie" maxlength="8" disabled="disabled"
					name="<%= process.getNOM_EF_REF_MAIRIE() %>" size="10"
					type="text" value="<%= process.getVAL_EF_REF_MAIRIE() %>">
				<br/><br/>
				<span class="sigp2" style="width:150px"> Code Rome : </span>
				<INPUT <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> tabindex="3" class="sigp2-saisie" maxlength="5"
					name="<%= process.getNOM_EF_CODE_ROME() %>" size="6"
					type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">
				<br/><br/>
				<span class="sigp2" style="width:150px">Code Rome :</span>
				<INPUT <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> id="listeCodeRome" class="sigp2-saisie"
						name="<%= process.getNOM_EF_CODE_ROME() %>" style="margin-right:10px;width:450px"
						type="text" value="<%= process.getVAL_EF_CODE_ROME() %>">						
				<br/><br/>
				<span class="sigp2Mandatory" style="width:150px"> Domaine d'activité : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE() %>" style="width=328px" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_DOMAINE(), process.getVAL_LB_DOMAINE_SELECT())%>
				</SELECT>
				<br/><br/>
				<span class="sigp2Mandatory" style="width:150px"> Famille de l'emploi : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>" style="width=328px" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT())%>
				</SELECT>
				<br/><br/>
				<span class="sigp2Mandatory" style="width:150px"> Nom du métier / emploi : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="100" disabled="disabled"
					name="<%= process.getNOM_EF_NOM_METIER() %>"
					type="text" value="<%= process.getVAL_EF_NOM_METIER() %>" style="width:328px">
				<br/><br/>
				<span class="sigp2" style="width:150px"> Autres Appellations <br/> métiers / emplois </span>
				<SELECT size="2" style="width : 328px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_AUTRE_APPELLATION()%>" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_AUTRE_APPELLATION(), process.getVAL_LB_AUTRE_APPELLATION_SELECT()) %>
				</SELECT>
			</fieldset>
			
			<fieldset class="sigp2Fieldset" style="text-align:left;width:505px;float:left;height:470px;">
				<legend class="sigp2Legend">Cadre statutaire</legend>
				<br/>
				<span class="sigp2" style="width:150px"> Catégorie / classification : </span>
				<INPUT tabindex="" class="sigp2-saisie" disabled="disabled"	name="<%= process.getNOM_EF_CATEGORIE_MULTI() %>" style="width:328px"
					type="text" value="<%= process.getVAL_EF_CATEGORIE_MULTI() %>">
				<br/><br/>
				<span class="sigp2" style="width:150px"> Cadres emploi : </span>
				<SELECT size="3" style="width :328px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_CADRE_EMPLOI_MULTI()%>" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_CADRE_EMPLOI_MULTI(), process.getVAL_LB_CADRE_EMPLOI_MULTI_SELECT()) %>
				</SELECT>
				<br/><br/>
				<span class="sigp2" style="width:150px"> Niveau d'étude : </span>
				<INPUT tabindex="" class="sigp2-saisie" disabled="disabled" name="<%= process.getNOM_EF_NIVEAU_ETUDE_MULTI() %>" style="width:328px" type="text"
					value="<%= process.getVAL_EF_NIVEAU_ETUDE_MULTI() %>">
				<br/><br/>
				<span class="sigp2" style="width:150px"> Diplôme(s) : </span>
				<SELECT size="3" style="width :328px;font-family : monospace;" class="sigp2-liste" name="<%=process.getNOM_LB_DIPLOME_MULTI()%>" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_DIPLOME_MULTI(), process.getVAL_LB_DIPLOME_MULTI_SELECT()) %>
				</SELECT>
				<br/><br/>
				<span class="sigp2" style="width:150px"> Précisions diplomes : </span>
				<textarea tabindex="" rows="2" cols="50" class="sigp2-saisie" name="<%= process.getNOM_EF_PRECISIONS_DIPLOMES() %>"	style="overflow:hidden;width:328px" disabled="disabled"><%= process.getVAL_EF_PRECISIONS_DIPLOMES() %></textarea>
			</fieldset>
		</div>
		
		<fieldset class="sigp2Fieldset"  style="text-align: left; margin: 10px; width:1030px">
			<legend class="sigp2Legend"><B>Définition de l'emploi</B></legend>
			<br/>
			<textarea tabindex="" rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_EF_DEFINITION_EMPLOI() %>" style="margin-right:10px;" disabled="disabled"><%= process.getVAL_EF_DEFINITION_EMPLOI() %></textarea>
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
