<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumStatutFichePoste"%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8"%>
<%@page import="nc.mairie.metier.poste.TitrePoste"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@ page import="nc.mairie.metier.poste.SavoirFaire" %>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFichePoste" id="process" scope="session"></jsp:useBean>
<% if (process.getMetierPrimaire() != null){ %>
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
			<% if ((process.ACTION_CREATION.equals(process.getVAL_ST_ACTION()) || process.ACTION_DUPLICATION.equals(process.getVAL_ST_ACTION())) && process.getMetierPrimaire() == null){ %>
			<%} else {%>
			<table>

				<tr>
					<td width="150px">
						<span class="sigp2Mandatory">Fiche emploi ville primaire : </span>
					</td>
					<td>
						<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="5" size="6"	type="text" readonly="readonly" value="<%=process.getVAL_ST_METIER_PRIMAIRE()%>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" height="16px" width="16px" editable="false" name="<%=process.getNOM_PB_RECHERCHE_METIER_PRIMAIRE()%>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Fiche emploi ville secondaire : </span>
					</td>
					<td>
						<INPUT <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="5" size="6" readonly="readonly" value="<%=process.getVAL_ST_METIER_SECONDAIRE()%>" <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>>
						<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/loupe.gif" height="16px" width="16px" name="<%=process.getNOM_PB_RECHERCHE_METIER_SECONDAIRE()%>">
						<INPUT type="image" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_METIER_SECONDAIRE()%>" src="images/suppression.gif" height="16px" width="16px" >
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
	<legend class="sigp2Legend">Définition du poste</legend>
	<br/>
	<textarea <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> rows="4" cols="160" class="sigp2-saisie" style="margin-right:10px;" name="<%= process.getNOM_EF_MISSIONS()%>" ><%= process.getVAL_EF_MISSIONS() %></textarea>
	<br/>
</fieldset>

<fieldset class="sigp2Fieldset" style="width:1020px">
	<legend class="sigp2Legend">Spécialisation éventuelle</legend>
	<br/>
	<textarea <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> rows="4" cols="160" class="sigp2-saisie" style="margin-right:10px;" name="<%= process.getNOM_EF_SPECIALISATION()%>" ><%= process.getVAL_EF_SPECIALISATION() %></textarea>
	<br/>
</fieldset>

<fieldset class="sigp2Fieldset" style="width:1020px">
	<legend class="sigp2Legend">Informations complémentaires</legend>
	<br/>
	<textarea <%= process.estFDPInactive ?  "disabled='disabled'" : "" %> <%= MairieUtils.getDisabled(request, process.getNomEcran()) %> rows="4" cols="160" class="sigp2-saisie" style="margin-right:10px;" name="<%= process.getNOM_EF_INFORMATIONS_COMPLEMENTAIRES()%>" ><%= process.getVAL_EF_INFORMATIONS_COMPLEMENTAIRES() %></textarea>
	<br/>
</fieldset>

<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
	<legend class="sigp2Legend">Activités métier</legend>
	<table class="display" id="tabActiMetier">
		<thead>
		<tr>
			<th class="masqued-id">idActi</th>
			<th width="50" >Selection <INPUT id="checkAllACT" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"></th>
			<th colspan="2">Libellé</th>
		</tr>
		</thead>
		<tbody>
		<%
			if (process.getListActiviteMetier()!=null){
				for (int indiceActi = 0;indiceActi<process.getListActiviteMetier().size();indiceActi++){
		%>
		<tr class="activite-metier">
			<td class="masqued-id"><%=process.getVAL_ST_ID_ACTI_METIER(indiceActi)%></td>
			<td><INPUT data-id="<%=indiceActi%>" class="actiCheckAll <%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_ACTI_METIER(indiceActi),process.getVAL_CK_SELECT_LIGNE_ACTI_METIER(indiceActi))%>></td>
			<td colspan="2"><%=process.getVAL_ST_LIB_ACTI_METIER(indiceActi)%></td>
		</tr>
		<%
					for (int indiceSavoirFaire = 0; indiceSavoirFaire < process.getListActiviteMetier().get(indiceActi).getListSavoirFaire().size(); indiceSavoirFaire++) {
						SavoirFaire sf = process.getListActiviteMetier().get(indiceActi).getListSavoirFaire().get(indiceSavoirFaire);
						%>
							<tr class="block-element-<%=(indiceSavoirFaire % 2) == 0 ? "even" : "odd" %>">
								<td  style="display: none;"><%=process.getVAL_ST_ID_ACTI_METIER_SAVOIR(indiceActi, indiceSavoirFaire)%></td>
								<td></td>
								<td><INPUT class="acti<%=indiceActi%>-sf <%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR(indiceActi, indiceSavoirFaire),process.getVAL_CK_SELECT_LIGNE_ACTI_METIER_SAVOIR(indiceActi, indiceSavoirFaire))%>></td>
								<td><%=process.getVAL_ST_LIB_ACTI_METIER_SAVOIR(indiceActi, indiceSavoirFaire)%></td>
							</tr>
						<%
					}
				}
			}
		%>
		</tbody>
	</table>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#checkAllACT").change(function() {
                var checkAllACT = $(this);
                $("#tabActiMetier > tbody > tr > td:nth-child(2) > input:checkbox").each(function() {
                    $(this).prop("checked", checkAllACT.prop('checked')).change();
                });
            });
            $(".actiCheckAll").change(function() {
                var actiCheckAll = $(this);
                var actiId = $(this).data("id");
                $("#tabActiMetier > tbody > tr > td:nth-child(3) > input:checkbox").filter(".acti" + actiId + "-sf").each(function() {
                    $(this).prop("checked", actiCheckAll.prop('checked'));
                });
            });
        } );
	</script>
</FIELDSET>

<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
	<legend class="sigp2Legend">Savoir-faire</legend>
	<table class="display" id="tabSavoirFaire">
		<thead>
		<tr>
			<th class="masqued-id">idSavoirFaire</th>
			<th width="50" >Selection <INPUT id="checkAllSF" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox" ></th>
			<th>Libellé</th>
		</tr>
		</thead>
		<tbody>
		<%
			if (process.getListSavoirFaire()!=null){
				for (int indiceSF = 0; indiceSF < process.getListSavoirFaire().size(); indiceSF++) {
		%>
		<tr class="block-element-<%=(indiceSF % 2) == 0 ? "even" : "odd" %>">
			<td class="masqued-id"><%=process.getVAL_ST_ID_SF(indiceSF)%></td>
			<td><INPUT class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_SF(indiceSF),process.getVAL_CK_SELECT_LIGNE_SF(indiceSF))%>></td>
			<td><%=process.getVAL_ST_LIB_SF(indiceSF)%></td>
		</tr>
		<%
				}
			}
		%>
		</tbody>
	</table>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#checkAllSF").change(function() {
                var checkAllSF = $(this);
                $("#tabSavoirFaire > tbody > tr > td:nth-child(2) > input:checkbox").each(function() {
                    $(this).prop("checked", checkAllSF.prop('checked'));
                });
			});
        } );
	</script>
</FIELDSET>

<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
	<legend class="sigp2Legend">Activités et compétences générales</legend>
	<table class="display" id="tabActiviteGenerale">
		<thead>
		<tr>
			<th class="masqued-id">idActiviteGenerale</th>
			<th width="50" >Selection <INPUT id="checkAllAG" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox" name="CHECK_ALL_ACTI" onClick='/** activeACTI("<%=process.getListeToutesActi().size() %>")***/'></th>
			<th>Libellé</th>
		</tr>
		</thead>
		<tbody>
		<%
			if (process.getListActiviteGenerale()!=null){
				for (int indiceAG = 0; indiceAG < process.getListActiviteGenerale().size(); indiceAG++) {
		%>
		<tr class="block-element-<%=(indiceAG % 2) == 0 ? "even" : "odd" %>">
			<td class="masqued-id"><%=process.getVAL_ST_ID_AG(indiceAG)%></td>
			<td><INPUT class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_AG(indiceAG),process.getVAL_CK_SELECT_LIGNE_AG(indiceAG))%>></td>
			<td><%=process.getVAL_ST_LIB_AG(indiceAG)%></td>
		</tr>
		<%
				}
			}
		%>
		</tbody>
	</table>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#checkAllAG").change(function() {
                var checkAllAG = $(this);
                $("#tabActiviteGenerale > tbody > tr > td:nth-child(2) > input:checkbox").each(function() {
                    $(this).prop("checked", checkAllAG.prop('checked'));
                });
            });
        } );
	</script>
</FIELDSET>

<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
	<legend class="sigp2Legend">Conditions d'exercice</legend>
	<table class="display" id="tabConditionsExercice">
		<thead>
		<tr>
			<th class="masqued-id">idConditionExercice</th>
			<th width="50" >Selection <INPUT id="checkAllCE" class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox" name="CHECK_ALL_ACTI" onClick='/** activeACTI("<%=process.getListeToutesActi().size() %>")***/'></th>
			<th>Libellé</th>
		</tr>
		</thead>
		<tbody>
		<%
			if (process.getListConditionExercice()!=null){
				for (int indiceCE = 0; indiceCE < process.getListConditionExercice().size(); indiceCE++) {
		%>
		<tr class="block-element-<%=(indiceCE % 2) == 0 ? "even" : "odd" %>">
			<td class="masqued-id"><%=process.getVAL_ST_ID_CE(indiceCE)%></td>
			<td><INPUT class="<%=process.estFDPInactive ? MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, ""): MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" type="checkbox"  <%= process.forCheckBoxHTML(process.getNOM_CK_SELECT_LIGNE_CE(indiceCE),process.getVAL_CK_SELECT_LIGNE_CE(indiceCE))%>></td>
			<td><%=process.getVAL_ST_LIB_CE(indiceCE)%></td>
		</tr>
		<%
				}
			}
		%>
		</tbody>
	</table>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#checkAllCE").change(function() {
                var checkAllCE = $(this);
                $("#tabConditionsExercice > tbody > tr > td:nth-child(2) > input:checkbox").each(function() {
                    $(this).prop("checked", checkAllCE.prop('checked'));
                });
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

<% if (process.getMetierPrimaire() != null){ %>
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