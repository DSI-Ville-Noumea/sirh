<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
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
<jsp:useBean class="nc.mairie.gestionagent.process.OeAGENTEmploisSpecificites" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Gestion des spécificités d'une fiche de poste</legend>
		    <br/>
		    <div align="left">
				<span class="sigp2-RadioBouton">
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
				<div align="left" style="float:left;width:100%;display:block;">
			<%}else{ %>
				<div align="left" style="float:left;width:100%;display:none;">
			<%} %>
				<br/>
				<table class="sigp2-tab">
					<tr>
						<th class="sigp2-tabTitre"><INPUT tabindex="" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_AVANTAGE()%>"></th>
						<th class="sigp2-tabTitre" style="width:300px;">Type</th>
						<th class="sigp2-tabTitre" style="width:100px;">Montant</th>
						<th class="sigp2-tabTitre" style="width:150px;">Nature</th>
						<th class="sigp2-tabTitre" style="width:400px;">Rubrique</th>
					</tr>
					<%
					int indiceAvNat = 0;
					if (process.getListeAvantageFP()!=null){
						for (int i = 0;i<process.getListeAvantageFP().size();i++){
					%>
							<tr>
								<td class="sigp2-tabLigne"><INPUT tabindex=""  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AVANTAGE(indiceAvNat),process.getVAL_CK_AVANTAGE(indiceAvNat))%>></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_TYPE(indiceAvNat)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_MONTANT(indiceAvNat)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_NATURE(indiceAvNat)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat)%></td>
							</tr>
							<%
							indiceAvNat++;
						}
					}
					if (process.getListeAvantageAFF()!=null){
						for (int i = 0;i<process.getListeAvantageAFF().size();i++){
							if (!process.getListeAvantageFP().contains(process.getListeAvantageAFF().get(i))) {
					%>
								<tr>
									<td class="sigp2-tabLigne" align="center">
										<INPUT tabindex="" type="image" src="images/suppression.gif"  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_AVANTAGE(indiceAvNat)%>">
									</td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_TYPE(indiceAvNat)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_MONTANT(indiceAvNat)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_NATURE(indiceAvNat)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat)%></td>
								</tr>
								<%
								indiceAvNat++;
							}
						}
					}
					if (process.getListeAvantageAAjouter()!=null){
						for (int i = 0;i<process.getListeAvantageAAjouter().size();i++){
					%>
							<tr>
								<td class="sigp2-tabLigne" align="center">
									<INPUT tabindex="" type="image" src="images/suppression.gif"  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_AVANTAGE(indiceAvNat)%>">
								</td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_TYPE(indiceAvNat)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_MONTANT(indiceAvNat)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_NATURE(indiceAvNat)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat)%></td>
							</tr>
							<%
							indiceAvNat++;
						}
					} %>
				</table>
				<BR/>
			</div>
			<%if (process.getVAL_RG_SPECIFICITE().equals(process.getNOM_RB_SPECIFICITE_D())){ %>
				<div align="left" style="float:left;width:100%;display:block;">
			<%}else{ %>
				<div align="left" style="float:left;width:100%;display:none;">
			<%} %>
				<br/>
				<table class="sigp2-tab">
					<tr>
						<th class="sigp2-tabTitre"><INPUT tabindex="" type="image" src="images/ajout.gif"  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_DELEGATION()%>"></th>
						<th class="sigp2-tabTitre" width="150px">Type</th>
						<th class="sigp2-tabTitre" width="700px">Commentaire</th>
					</tr>
					<%
					int indiceDel = 0;
					if (process.getListeDelegationFP()!=null){
						for (int i = 0;i<process.getListeDelegationFP().size();i++){
					%>
							<tr>
								<td class="sigp2-tabLigne"><INPUT tabindex=""  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_DELEGATION(indiceDel),process.getVAL_CK_DELEGATION(indiceDel))%>></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_TYPE(indiceDel)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_COMMENT(indiceDel)%></td>
							</tr>
							<%
							indiceDel++;
						}
					}
					if (process.getListeDelegationAFF()!=null){
						for (int i = 0;i<process.getListeDelegationAFF().size();i++){
							if (!process.getListeDelegationFP().contains(process.getListeDelegationAFF().get(i))) {
					%>
								<tr>
									<td class="sigp2-tabLigne" align="center">
										<INPUT tabindex="" type="image" src="images/suppression.gif"  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATION(indiceDel)%>">
									</td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_TYPE(indiceDel)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_COMMENT(indiceDel)%></td>
								</tr>
								<%
								indiceDel++;
							}
						}
					}
					if (process.getListeDelegationAAjouter()!=null){
						for (int i = 0;i<process.getListeDelegationAAjouter().size();i++){
					%>
							<tr>
								<td class="sigp2-tabLigne" align="center">
									<INPUT tabindex="" type="image" src="images/suppression.gif"  class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_DELEGATION(indiceDel)%>">
								</td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_TYPE(indiceDel)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_COMMENT(indiceDel)%></td>
							</tr>
							<%
							indiceDel++;
						}
					} %>
				</table>
				<BR/>
			</div>
			<%if (process.getVAL_RG_SPECIFICITE().equals(process.getNOM_RB_SPECIFICITE_RI())){ %>
				<div align="left" style="float:left;width:100%;display:block;">
			<%}else{ %>
				<div align="left" style="float:left;width:100%;display:none;">
			<%} %>
				<br/>
				<table class="sigp2-tab">
					<tr>
						<th class="sigp2-tabTitre"><INPUT tabindex="" type="image" src="images/ajout.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" height="16px" width="16px" name="<%=process.getNOM_PB_AJOUTER_REGIME()%>"></th>
						<th class="sigp2-tabTitre" width="150px">Type</th>
						<th class="sigp2-tabTitre" width="100px">Forfait</th>
						<th class="sigp2-tabTitre" width="100px">Nb points</th>
						<th class="sigp2-tabTitre" width="380px">Rubrique</th>
					</tr>
					<%
					int indiceRegIndemn = 0;
					if (process.getListeRegimeFP()!=null){
						for (int i = 0;i<process.getListeRegimeFP().size();i++){
					%>
							<tr>
								<td class="sigp2-tabLigne"><INPUT tabindex=""  <%= MairieUtils.getDisabled(request, process.getNomEcran()) %>  type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_REGINDEMN(indiceRegIndemn),process.getVAL_CK_REGINDEMN(indiceRegIndemn))%>></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_TYPE(indiceRegIndemn)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_FORFAIT(indiceRegIndemn)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_NB_POINTS(indiceRegIndemn)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_RUBRIQUE(indiceRegIndemn)%></td>
							</tr>
							<%
							indiceRegIndemn++;
						}
					}
					if (process.getListeRegimeAFF()!=null){
						for (int i = 0;i<process.getListeRegimeAFF().size();i++){
							if (!process.getListeRegimeFP().contains(process.getListeRegimeAFF().get(i))) {
					%>
								<tr>
									<td class="sigp2-tabLigne" align="center">
										<INPUT tabindex="" type="image" src="images/suppression.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>"  height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_REGIME(indiceRegIndemn)%>">
									</td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_TYPE(indiceRegIndemn)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_FORFAIT(indiceRegIndemn)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_NB_POINTS(indiceRegIndemn)%></td>
									<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_RUBRIQUE(indiceRegIndemn)%></td>
								</tr>
								<%
								indiceRegIndemn++;
							}
						}
					}
					if (process.getListeRegimeAAjouter()!=null){
						for (int i = 0;i<process.getListeRegimeAAjouter().size();i++){
					%>
							<tr>
								<td class="sigp2-tabLigne" align="center">
									<INPUT tabindex="" type="image" src="images/suppression.gif" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>"  height="16px" width="16px" name="<%=process.getNOM_PB_SUPPRIMER_REGIME(indiceRegIndemn)%>">
								</td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_TYPE(indiceRegIndemn)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_FORFAIT(indiceRegIndemn)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_NB_POINTS(indiceRegIndemn)%></td>
								<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_RUBRIQUE(indiceRegIndemn)%></td>
							</tr>
							<%
							indiceRegIndemn++;
						}
					} %>
				</table>
	            <BR/>
			</div>
			<BR/>
		</FIELDSET>
		<BR/>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
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

<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
	<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
	<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
</FIELDSET>
</FORM>
<%} %>
</BODY>
</HTML>