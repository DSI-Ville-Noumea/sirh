<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.enums.EnumTypeCompetence"%>
<%@ page import="nc.mairie.metier.poste.SavoirFaire" %>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<TITLE>Informations sur l'affectation en cours</TITLE>

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
<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTEmploisPoste" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<BR/>
		<%if (process.getFichePosteSecondaireCourant() != null) { %>
		<FIELDSET style="text-align: left; margin: 10px; width:1030px;" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>">
			<span class="sigp2">Cet agent a deux fiches de poste pour cette affectation. Pour voir l'autre fiche de poste, cliquez sur le bouton :</span>
			<INPUT type="submit" value="Voir l'autre FDP" name="<%=process.getNOM_PB_VOIR_AUTRE_FP()%>" class="sigp2-Bouton-100">
		</FIELDSET>
		<%} %>
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Fiche de poste</legend>
		    <table>
		    	<tr>
		    		<td width="60px;">
						<span class="sigp2">Budget :</span>
		    		</td>
		    		<td width="170px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_BUDGET()%></span>
		    		</td>
		    		<td width="100px;">
						<span class="sigp2" >Année :</span>
		    		</td>
		    		<td width="160px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_ANNEE()%></span>
		    		</td>
		    		<td width="70px;">
						<span class="sigp2">Numéro :</span>
		    		</td>
		    		<td width="160px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_NUMERO()%></span>
		    		</td>
		    		<td width="80px;">
						<span class="sigp2" >NFA :</span>
		    		</td>
		    		<td width="120px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_NFA()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">OPI :</span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_OPI()%></span>
		    		</td>
		    		<td>
						<span class="sigp2">Réglementaire :</span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_REGLEMENTAIRE()%></span>
		    		</td>
		    		<td>
						<span class="sigp2">Budgété :</span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_POURCENT_BUDGETE()%></span>
		    		</td>
		    		<td>
						<span class="sigp2">Statut :</span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_ACT_INACT()%></span>
		    		</td>
		    	</tr>
		    </table>
		</FIELDSET>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		    <legend class="sigp2Legend">Définition du poste</legend>
		    <table>
		    	<tr>
		    		<td width="150px;">
						<span class="sigp2"> Titre du poste : </span>
		    		</td>
		    		<td colspan="3">
						<span class="sigp2-statique"><%=process.getVAL_ST_TITRE()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Direction : </span>
		    		</td>
		    		<td width="300px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_DIRECTION()%></span>
		    		</td>
		    		<td width="150px;">
						<span class="sigp2"> Service du poste : </span>
		    		</td>
		    		<td width="300px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_SERVICE()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Section : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_SECTION()%></span>
		    		</td>
		    		<td>
						<span class="sigp2"> Localisation : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_LOCALISATION()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Responsable hiér. : </span>
		    		</td>
		    		<td colspan="3">
						<span class="sigp2-statique" align="left" style="width:500px"><%=process.getVAL_ST_RESPONSABLE()%> - <%= process.getVAL_ST_INFO_RESP()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Fiche de poste remplacée : </span>
		    		</td>
		    		<td colspan="3">
						<span class="sigp2-statique"><%=process.getVAL_ST_REMPLACEMENT()%> - <%=process.getVAL_ST_INFO_REMP()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Grade : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_GRADE()%></span>
		    		</td>
		    		<td>
						<span class="sigp2"> Corps / Cadre d'emploi : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_CADRE_EMPLOI()%></span>
		    		</td>
		    	</tr>
		    </table>
		</FIELDSET>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			<legend class="sigp2Legend">Mission</legend>
			<div align="left">
				<br/>
				<span class="sigp2-statique" style="width:1000px"><%=process.getVAL_ST_MISSION()%></span>
			</div>
			<br/>
		</FIELDSET>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		    <legend class="sigp2Legend">Titulaire du poste</legend>
		    <table>
		    	<tr>
		    		<td width="150px;">
						<span class="sigp2"> Titulaire du poste : </span>
		    		</td>
		    		<td width="800px;">
						<span class="sigp2-statique"><%=process.getVAL_ST_TITULAIRE()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Grade du titulaire : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_GRADE_AGT()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Etude : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_ETUDE_AGT()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Temps de travail : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_TPS_TRAVAIL_AGT()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2"> Supérieur hiérarchique : </span>
		    		</td>
		    		<td>
						<span class="sigp2-statique"><%=process.getVAL_ST_SUPERIEUR_HIERARCHIQUE()%></span>
		    		</td>
		    	</tr>
		    </table>
		</FIELDSET>
        <% if (process.versionFicheMetier()) { %>
            <fieldset class="sigp2Fieldset" style="width:1020px">
                <legend class="sigp2Legend">Spécialisation éventuelle</legend>
                <br/>
                <textarea disabled='disabled' rows="4" cols="160" class="sigp2-saisie" style="margin-right:10px;" name="<%= process.getNOM_EF_SPECIALISATION()%>" ><%= process.getVAL_EF_SPECIALISATION() %></textarea>
                <br/>
            </fieldset>

            <fieldset class="sigp2Fieldset" style="width:1020px">
                <legend class="sigp2Legend">Informations complémentaires</legend>
                <br/>
                <textarea disabled='disabled' rows="4" cols="160" class="sigp2-saisie" style="margin-right:10px;" name="<%= process.getNOM_EF_INFORMATIONS_COMPLEMENTAIRES()%>" ><%= process.getVAL_EF_INFORMATIONS_COMPLEMENTAIRES() %></textarea>
                <br/>
            </fieldset>

        <% } %>
		<% if (process.versionFicheMetier()) { %>
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Activités métier et savoir-faire</legend>
                <table class="display" id="tabActiMetier">
                    <thead>
                    <tr>
                        <th class="masqued-id">idActi</th>
                        <th colspan="2"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (process.getListActiviteMetier()!=null){
                            for (int indiceActi = 0;indiceActi<process.getListActiviteMetier().size();indiceActi++){
                    %>
                    <tr style="font-weight: bold;">
                        <td class="masqued-id"><%=process.getVAL_ST_ID_ACTI_METIER(indiceActi)%></td>
                        <td colspan="2" class="sigp2-statique"><%=process.getVAL_ST_LIB_ACTI_METIER(indiceActi)%></td>
                    </tr>
                    <%
                        for (int indiceSavoirFaire = 0; indiceSavoirFaire < process.getListActiviteMetier().get(indiceActi).getListSavoirFaire().size(); indiceSavoirFaire++) {
                            SavoirFaire sf = process.getListActiviteMetier().get(indiceActi).getListSavoirFaire().get(indiceSavoirFaire);
                    %>
                    <tr>
                        <td  style="display: none;"><%=process.getVAL_ST_ID_ACTI_METIER_SAVOIR(indiceActi, indiceSavoirFaire)%></td>
                        <td width="10"></td>
                        <td class="sigp2-statique">* <%=process.getVAL_ST_LIB_ACTI_METIER_SAVOIR(indiceActi, indiceSavoirFaire)%></td>
                    </tr>
                    <%
                                }
                            }
                        }
                    %>
                    </tbody>
                </table>
            </FIELDSET>
        <% } else { %>
            <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
                <legend class="sigp2Legend">Activités</legend>
                <%if (process.getListeActivite() != null && process.getListeActivite().size() > 0) { %>
                <div align="left">
                    <br/>
                    <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                        <table class="sigp2NewTab" style="text-align:left;width:980px;">
                            <%
                                int indiceActi = 0;
                                if (process.getListeActivite() != null) {
                                    for (int i = 0; i < process.getListeActivite().size(); i++) {
                            %>
                            <tr>
                                <td class="sigp2NewTab-liste"
                                    style="position:relative;"><%=process.getVAL_ST_LIB_ACTI(indiceActi)%>
                                </td>
                            </tr>
                            <%
                                        indiceActi++;
                                    }
                                }%>
                        </table>
                    </div>
                </div>
                <%} %>
                <br/>
            </FIELDSET>
        <% }%>

        <% if (process.versionFicheMetier()) { %>
            <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
                <legend class="sigp2Legend">Savoirs</legend>
                <table class="display" id="tabSavoirFaire">
                    <thead>
                    <tr>
                        <th class="masqued-id">idSavoirFaire</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (process.getListSavoirFaire()!=null){
                            for (int indiceSF = 0; indiceSF < process.getListSavoirFaire().size(); indiceSF++) {
                    %>
                        <tr>
                            <td class="masqued-id"><%=process.getVAL_ST_ID_SF(indiceSF)%></td>
                            <td class="sigp2-statique">* <%=process.getVAL_ST_LIB_SF(indiceSF)%></td>
                        </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </FIELDSET>
        <% } else { %>
            <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
                <legend class="sigp2Legend">Compétences</legend>
                <%if ((process.getListeSavoirFaire() != null || process.getListeSavoir() != null || process.getListeComportementPro() != null) && (process.getListeSavoirFaire().size() > 0 || process.getListeSavoir().size() > 0 || process.getListeComportementPro().size() > 0)) { %>
                <span class="sigp2Mandatory" style="text-align:center">
                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_S())%>
                           onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Savoir
                    <span style="width:10px"></span>
                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_SF())%>
                           onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Savoir-faire
                    <span style="width:10px"></span>
                    <INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_COMPETENCE(),process.getNOM_RB_TYPE_COMPETENCE_C())%>
                           onclick='executeBouton("<%=process.getNOM_PB_CHANGER_TYPE() %>")'>Comportements professionnels
                </span>
                <INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGER_TYPE()%>" value="OK">
                <br/>
                <%if (process.getTypeCompetenceCourant() != null && process.getTypeCompetenceCourant().getIdTypeCompetence().toString().equals(EnumTypeCompetence.SAVOIR_FAIRE.getCode().toString())) { %>
                <div align="left" style="float:left">
                    <br/>
                    <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                        <table class="sigp2NewTab" style="text-align:left;width:980px;">
                            <%
                                int indiceCompSF = 0;
                                if (process.getListeSavoirFaire() != null) {
                                    for (int i = 0; i < process.getListeSavoirFaire().size(); i++) {
                            %>
                            <tr>
                                <td class="sigp2NewTab-liste"
                                    style="position:relative;"><%=process.getVAL_ST_LIB_COMP_SF(indiceCompSF)%>
                                </td>
                            </tr>
                            <%
                                        indiceCompSF++;
                                    }
                                }%>
                        </table>
                    </div>
                </div>
                <%} else if (process.getTypeCompetenceCourant() != null && process.getTypeCompetenceCourant().getIdTypeCompetence().toString().equals(EnumTypeCompetence.SAVOIR.getCode().toString())) {%>
                <div align="left" style="float:left">
                    <br/>
                    <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                        <table class="sigp2NewTab" style="text-align:left;width:980px;">
                            <%
                                int indiceCompS = 0;
                                if (process.getListeSavoir() != null) {
                                    for (int i = 0; i < process.getListeSavoir().size(); i++) {
                            %>
                            <tr>
                                <td class="sigp2NewTab-liste"
                                    style="position:relative;"><%=process.getVAL_ST_LIB_COMP_S(indiceCompS)%>
                                </td>
                            </tr>
                            <%
                                        indiceCompS++;
                                    }
                                }%>
                        </table>
                    </div>
                </div>
                <%} else if (process.getTypeCompetenceCourant() != null && process.getTypeCompetenceCourant().getIdTypeCompetence().toString().equals(EnumTypeCompetence.COMPORTEMENT.getCode().toString())) {%>
                <div align="left" style="float:left">
                    <br/>
                    <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                        <table class="sigp2NewTab" style="text-align:left;width:980px;">
                            <%
                                int indiceCompPro = 0;
                                if (process.getListeComportementPro() != null) {
                                    for (int i = 0; i < process.getListeComportementPro().size(); i++) {
                            %>
                            <tr>
                                <td class="sigp2NewTab-liste"
                                    style="position:relative;"><%=process.getVAL_ST_LIB_COMP_PRO(indiceCompPro)%>
                                </td>
                            </tr>
                            <%
                                        indiceCompPro++;
                                    }
                                }%>
                        </table>
                    </div>
                </div>
                <%} %>
                <%} %>
                <br/>
            </FIELDSET>
        <% } %>


        <% if (process.versionFicheMetier()) { %>
        <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
            <legend class="sigp2Legend">Activités et compétences générales</legend>
            <table class="display" >
                <thead>
                <tr>
                    <th class="masqued-id"></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (process.getListActiviteGenerale()!=null){
                        for (int indiceAG = 0; indiceAG < process.getListActiviteGenerale().size(); indiceAG++) {
                %>
                <tr>
                    <td class="masqued-id"><%=process.getVAL_ST_ID_AG(indiceAG)%></td>
                    <td class="sigp2-statique">* <%=process.getVAL_ST_LIB_AG(indiceAG)%></td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </FIELDSET>

        <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
            <legend class="sigp2Legend">Conditions d'exercice</legend>
            <table class="display" >
                <thead>
                <tr>
                    <th class="masqued-id"></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (process.getListConditionExercice()!=null){
                        for (int indiceCE = 0; indiceCE < process.getListConditionExercice().size(); indiceCE++) {
                %>
                <tr>
                    <td class="masqued-id"><%=process.getVAL_ST_ID_CE(indiceCE)%></td>
                    <td class="sigp2-statique">* <%=process.getVAL_ST_LIB_CE(indiceCE)%></td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </FIELDSET>
        <% }%>

        <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
            <legend class="sigp2Legend">Spécificités</legend>
            <BR/>
            <div align="left" style="float:left">
                <span class="sigp2" style="text-align:left;width:960;"><u>Avantage(s) en nature</u></span>
                <%if (process.getListeAvantage() != null && process.getListeAvantage().size() > 0) { %>
                <br/><br/>
                <span style="margin-left:5px;position:relative;width:350px;text-align: left;">Type</span>
                <span style="position:relative;width:90px;text-align: center;">Montant</span>
                <span style="position:relative;text-align: left;">&nbsp;Nature</span>
                <br/>
                <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                    <table class="sigp2NewTab" style="text-align:left;width:980px;">
                        <%
                            int indiceAvantage = 0;
                            if (process.getListeAvantage() != null) {
                                for (int i = 0; i < process.getListeAvantage().size(); i++) {
                        %>
                        <tr>
                            <td class="sigp2NewTab-liste"
                                style="position:relative;width:350px;text-align: left;"><%=process.getVAL_ST_AV_TYPE(indiceAvantage)%>
                            </td>
                            <td class="sigp2NewTab-liste"
                                style="position:relative;width:90px;text-align: right;"><%=process.getVAL_ST_AV_MNT(indiceAvantage)%>
                            </td>
                            <td class="sigp2NewTab-liste" style="position:relative;text-align: left;">
                                &nbsp;<%=process.getVAL_ST_AV_NATURE(indiceAvantage)%>
                            </td>
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
            <div align="left" style="float:left">
                <span class="sigp2" style="text-align:left;width:960;"><u>Délégation(s)</u></span>
                <%if (process.getListeDelegation() != null && process.getListeDelegation().size() > 0) { %>
                <br/><br/>
                <span style="margin-left:5px;position:relative;width:250px;">Type</span>
                <span style="position:relative;">Commentaire</span>
                <br/>
                <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                    <table class="sigp2NewTab" style="text-align:left;width:980px;">
                        <%
                            int indiceDelegation = 0;
                            if (process.getListeDelegation() != null) {
                                for (int i = 0; i < process.getListeDelegation().size(); i++) {
                        %>
                        <tr>
                            <td class="sigp2NewTab-liste"
                                style="position:relative;width:250px;text-align: left;"><%=process.getVAL_ST_DEL_TYPE(indiceDelegation)%>
                            </td>
                            <td class="sigp2NewTab-liste" style="position:relative;text-align: left;">
                                &nbsp;<%=process.getVAL_ST_DEL_COMMENTAIRE(indiceDelegation)%>
                            </td>
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
            <div align="left" style="float:left">
                <span class="sigp2" style="text-align:left;width:960;"><u>Régime(s) indemnitaire(s)</u></span>
                <%if (process.getListeRegIndemn() != null && process.getListeRegIndemn().size() > 0) { %>
                <br/><br/>
                <span style="margin-left:5px;position:relative;width:100px;text-align: left;">Type</span>
                <span style="position:relative;width:90px;text-align: center;">Forfait</span>
                <span style="position:relative;text-align: left;">Nb points</span>
                <br/>
                <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                    <table class="sigp2NewTab" style="text-align:left;width:980px;">
                        <%
                            int indiceRegime = 0;
                            if (process.getListeRegIndemn() != null) {
                                for (int i = 0; i < process.getListeRegIndemn().size(); i++) {
                        %>
                        <tr>
                            <td class="sigp2NewTab-liste"
                                style="position:relative;width:100px;text-align: left;"><%=process.getVAL_ST_REG_TYPE(indiceRegime)%>
                            </td>
                            <td class="sigp2NewTab-liste"
                                style="position:relative;width:90px;text-align: right;"><%=process.getVAL_ST_REG_FORFAIT(indiceRegime)%>
                            </td>
                            <td class="sigp2NewTab-liste" style="position:relative;text-align: left;">
                                &nbsp;<%=process.getVAL_ST_REG_NB_PTS(indiceRegime)%>
                            </td>
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
            <div align="left" style="float:left">
                <span class="sigp2" style="text-align:left;width:960;"><u>Prime(s) pointage</u></span>
                <%if (process.getListePrimePointageFP() != null && process.getListePrimePointageFP().size() > 0) { %>
                <br/><br/>
                <span style="margin-left:5px;position:relative;text-align: left;">Rubrique</span>
                <br/>
                <div style="overflow: auto;height: 120px;width:1000px;margin-right: 0px;margin-left: 0px;">
                    <table class="sigp2NewTab" style="text-align:left;width:980px;">
                        <%
                            int indicePrime = 0;
                            if (process.getListePrimePointageFP() != null) {
                                for (int i = 0; i < process.getListePrimePointageFP().size(); i++) {
                        %>
                        <tr>
                            <td class="sigp2NewTab-liste"
                                style="position:relative;text-align: left;"><%=process.getVAL_ST_PP_RUBR(indicePrime)%>
                            </td>
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
        </FIELDSET>
        <%if (process.getFichePosteCourant() != null && process.getFichePosteCourant().getIdFichePoste() != null) { %>
        <FIELDSET style="text-align: center; margin: 10px; width:1030px;"
                  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>">
            <% if (process.getVAL_ST_ACTION().equals(process.ACTION_IMPRESSION)) { %>
            <FONT color='red'><%=process.getVAL_ST_WARNING() %>
            </FONT>
            <BR/><BR/>
            <INPUT type="submit" value="Valider" name="<%=process.getNOM_PB_VALIDER_IMPRIMER()%>"
                   class="sigp2-Bouton-100">
            <INPUT type="submit" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>" class="sigp2-Bouton-100">
            <% } else {%>
            <INPUT type="submit" value="Exporter" name="<%=process.getNOM_PB_IMPRIMER()%>" class="sigp2-Bouton-100">
            <% } %>

        </FIELDSET>
        <% } %>
        <INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
        <%=process.getUrlFichier()%>
    </FORM>
    <%} %>
</BODY>
</HTML>