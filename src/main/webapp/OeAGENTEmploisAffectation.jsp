<!-- Sample JSP file -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.poste.Affectation"%>
<%@page import="java.util.ArrayList"%>

<HTML>
<HEAD>
<META name="GENERATOR"
	content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css"
	type="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>


<TITLE>Gestion des affectations d'un agent</TITLE>
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
            function SelectLigne(id, tailleTableau)
            {
                for (i = 0; i < tailleTableau; i++) {
                    document.getElementById(i).className = "";
                }
                document.getElementById(id).className = "selectLigne";
            }

        </SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
	class="nc.mairie.gestionagent.process.agent.OeAGENTEmploisAffectation"
	id="process" scope="session"></jsp:useBean>

<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED"
	background="images/fond.jpg" lang="FR" link="blue" vlink="purple"
	onload="window.parent.frames('refAgent').location.reload();
                return setfocus('<%=process.getFocus()%>')">
	<%@ include file="BanniereErreur.jsp"%>
	<%
		if (process.getAgentCourant() != null) {
	%>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<%=process.getUrlFichier()%>
		<INPUT name="JSP" type="hidden" value="<%=process.getJSP()%>">
		<FIELDSET class="sigp2Fieldset"
			style="text-align: left; width: 1030px;">
			<legend class="sigp2Legend">Gestion des affectations de
				l'agent</legend>
			<br /> <span style="position: relative; width: 9px;"></span> <span
				style="position: relative; width: 85px;"><INPUT
				title="ajouter" type="image"
				class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
				src="images/ajout.gif" height="15px" width="16px"
				name="<%=process.getNOM_PB_AJOUTER()%>"></span> <span
				style="position: relative; width: 50px; text-align: left;">Direction</span>
			<span style="position: relative; width: 300px; text-align: left;">Service/Section/...</span>
			<span style="position: relative; width: 80px; text-align: center;">Date
				début</span> <span
				style="position: relative; width: 85px; text-align: center;">Date
				fin</span> <span style="position: relative; width: 65px; text-align: left;">Fiche
				poste</span> <span style="position: relative; text-align: left;">Titre
				poste</span> <br />
			<div
				style="overflow: auto; height: 250px; width: 1000px; margin-right: 0px; margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align: left; width: 980px;">
					<%
						int indiceAff = 0;
							if (process.getListeAffectation() != null) {
								for (int i = 0; i < process.getListeAffectation().size(); i++) {
									Affectation aff = (Affectation) process.getListeAffectation().get(i);
					%>
					<tr id="<%=indiceAff%>"
						onmouseover="SelectLigne(<%=indiceAff%>,<%=process.getListeAffectation().size()%>)">
						<td class="sigp2NewTab-liste"
							style="position: relative; width: 90px;" align="center"><INPUT
							title="consulter" type="image" src="images/oeil.gif"
							height="15px" width="15px"
							class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "")%>"
							name="<%=process.getNOM_PB_CONSULTER(indiceAff)%>"> <INPUT
							title="modifier" type="image" src="images/modifier.gif"
							height="15px" width="15px"
							class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
							name="<%=process.getNOM_PB_MODIFIER(indiceAff)%>"> <%
 	if (aff.isActive()) {
 %> <INPUT title="imprimer" type="image" src="images/imprimer.gif"
							height="15px" width="15px"
							class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
							name="<%=process.getNOM_PB_IMPRIMER(indiceAff)%>"> <%
 	} else {
 %> <span style="width: 22px;"></span> <%
 	}
 %> <INPUT title="consulter" type="image" src="images/oeil.gif"
							height="15px" width="15px"
							class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
							name="<%=process.getNOM_PB_CONSULTER(indiceAff)%>"> <%
 	if (aff.isActive()) {
 %> <INPUT title="supprimer" type="image" src="images/suppression.gif"
							height="15px" width="15px"
							class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
							name="<%=process.getNOM_PB_SUPPRIMER(indiceAff)%>"> <%
 	}
 %></td>
						<td class="sigp2NewTab-liste"
							style="position: relative; width: 50px; text-align: left;"><%=process.getVAL_ST_DIR(indiceAff)%></td>
						<td class="sigp2NewTab-liste"
							style="position: relative; width: 300px; text-align: left;"><%=process.getVAL_ST_SERV(indiceAff)%></td>
						<td class="sigp2NewTab-liste"
							style="position: relative; width: 80px; text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceAff)%></td>
						<td class="sigp2NewTab-liste"
							style="position: relative; width: 80px; text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceAff)%></td>
						<td class="sigp2NewTab-liste"
							style="position: relative; width: 65px; text-align: left;"><%=process.getVAL_ST_NUM_FP(indiceAff)%></td>
						<td class="sigp2NewTab-liste"
							style="position: relative; text-align: left;"><%=process.getVAL_ST_TITRE(indiceAff)%></td>
					</tr>
					<%
						indiceAff++;
								}
							}
					%>
				</table>
			</div>
			<BR />
			<div style="text-align: center;">
				<INPUT type="submit"
					class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "sigp2-Bouton-100")%>"
					value="Historique" name="<%=process.getNOM_PB_HISTORIQUE()%>">
				<INPUT type="submit"
					class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2-Bouton-100")%>"
					value="Historique" name="<%=process.getNOM_PB_HISTORIQUE()%>">
			</div>
		</FIELDSET>
		<%
			if (!Const.CHAINE_VIDE.equals(process.getVAL_ST_ACTION())) {
		%>
		<INPUT name="JSP" type="hidden" value="<%=process.getJSP()%>">
		<FIELDSET class="sigp2Fieldset"
			style="text-align: left; margin: 10px; width: 1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%
				if (process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)) {
			%>
			<FONT color='red'>Veuillez valider votre choix.</FONT> <BR />
			<%
				}
			%>
			<BR />
			<div>
				<FIELDSET class="sigp2Fieldset"
					style="text-align: left; margin: 10px;">
					<legend class="sigp2Legend">Fiche de poste</legend>
					<span class="sigp2Mandatory"
						style="position: relative; width: 150px;">Réf. fiche de
						poste : </span> <span class="sigp2-statique" style="width: 100px"><%=process.getVAL_ST_NUM_FICHE_POSTE()%></span>
					<%
						if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)) {
					%>
					<INPUT type="image" src="images/loupe.gif"
						name="<%=process.getNOM_PB_RECHERCHER_FP()%>">
					<%
						}
					%>
					<span class="sigp2" style="width: 125px">Temps réglementaire
						:</span> <span class="sigp2-statique" style="width: 170px"><%=process.getVAL_ST_TPS_REG()%></span>
					<BR /> <BR /> <span class="sigp2" style="width: 60px">Direction
						:</span> <span class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_DIRECTION()%></span>
					<span class="sigp2" style="width: 60px">Service :</span> <span
						class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_SERVICE()%></span>
					<span class="sigp2" style="width: 60px">Section :</span> <span
						class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_SUBDIVISION()%></span>
					<BR /> <BR /> <span class="sigp2" style="width: 100px">Titre
						du poste :</span> <span class="sigp2-statique" style="width: 600px"><%=process.getVAL_ST_TITRE_FP()%></span>
					<BR /> <BR /> <span class="sigp2" style="width: 100px">Lieu
						du poste :</span> <span class="sigp2-statique" style="width: 600px"><%=process.getVAL_ST_LIEU_FP()%></span>
					<BR />
				</FIELDSET>
				<BR />
				<%
					if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)) {
				%>
				<FIELDSET class="sigp2Fieldset"
					style="text-align: left; margin: 10px;">
					<legend class="sigp2Legend">Fiche de poste secondaire</legend>
					<span class="sigp2Mandatory"
						style="position: relative; width: 150px;">Réf. fiche de
						poste : </span> <span class="sigp2-statique" style="width: 100px"><%=process.getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE()%></span>
					<INPUT type="image" src="images/loupe.gif"
						name="<%=process.getNOM_PB_RECHERCHER_FP_SECONDAIRE()%>">
					<span class="sigp2" style="width: 125px">Temps réglementaire
						:</span> <span class="sigp2-statique" style="width: 170px"><%=process.getVAL_ST_TPS_REG_SECONDAIRE()%></span>
					<BR /> <BR /> <span class="sigp2" style="width: 60px">Direction
						:</span> <span class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_DIRECTION_SECONDAIRE()%></span>
					<span class="sigp2" style="width: 60px">Service :</span> <span
						class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_SERVICE_SECONDAIRE()%></span>
					<span class="sigp2" style="width: 60px">Section :</span> <span
						class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_SUBDIVISION_SECONDAIRE()%></span>
					<BR /> <BR /> <span class="sigp2" style="width: 100px">Titre
						du poste :</span> <span class="sigp2-statique" style="width: 600px"><%=process.getVAL_ST_TITRE_FP_SECONDAIRE()%></span>
					<BR /> <BR /> <span class="sigp2" style="width: 100px">Lieu
						du poste :</span> <span class="sigp2-statique" style="width: 600px"><%=process.getVAL_ST_LIEU_FP_SECONDAIRE()%></span>
					<BR />
				</FIELDSET>

				<%
					}
				%>

				<FIELDSET class="sigp2Fieldset"
					style="text-align: left; margin: 10px;">
					<legend class="sigp2Legend">Spécificités</legend>
					<%
						if (process.getAgentCourant() != null) {
					%>
					<BR />
					<div align="left">
						<span class="sigp2-RadioBouton"> 
						<INPUT tabindex=""	type="radio" checked
						<%=process.forRadioHTML(process.getNOM_RG_SPECIFICITE_spec(), process.getNOM_RB_SPECIFICITE_PP_spec())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE_spec()%>")'>Prime pointage <span style="width: 5px"></span> 
					    <INPUT tabindex=""	type="radio" 
						<%=process.forRadioHTML(process.getNOM_RG_SPECIFICITE_spec(), process.getNOM_RB_SPECIFICITE_AN_spec())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE_spec()%>")'>Avantage en nature <span style="width: 5px"></span> 
						<INPUT tabindex=""	type="radio"
						<%=process.forRadioHTML(process.getNOM_RG_SPECIFICITE_spec(), process.getNOM_RB_SPECIFICITE_D_spec())%>	onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE_spec()%>")'>Délégation	<span style="width: 5px"></span> 
						<INPUT tabindex="" type="radio"
						<%=process.forRadioHTML(process.getNOM_RG_SPECIFICITE_spec(), process.getNOM_RB_SPECIFICITE_RI_spec())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_SPECIFICITE_spec()%>")'>Régime indemnitaire <span style="width: 5px"></span>  
						<INPUT type="submit" style="visibility: hidden;" name="<%=process.getNOM_PB_CHANGER_SPECIFICITE_spec()%>" value="OK">
					</div>
					<BR />
					<%
						if (process.getVAL_RG_SPECIFICITE_spec().equals(process.getNOM_RB_SPECIFICITE_AN_spec())) {
										out.println("<div align='left' style='float: left; width: 100%; display: block;'>");

									} else {
										out.println("<div align='left' style='float: left; width: 100%; display: none;'>");
									}
					%>
					<br />
					<table class="sigp2-tab">
						<tr>
							<th class="sigp2-tabTitre"><INPUT tabindex="" type="image"
								class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
								src="images/ajout.gif" height="16px" width="16px"
								name="<%=process.getNOM_PB_AJOUTER_AVANTAGE_spec()%>"></th>
							<th class="sigp2-tabTitre" style="width: 300px;">Type</th>
							<th class="sigp2-tabTitre" style="width: 100px;">Montant</th>
							<th class="sigp2-tabTitre" style="width: 150px;">Nature</th>
							<th class="sigp2-tabTitre" style="width: 400px;">Rubrique</th>
						</tr>
						<%
							int indiceAvNat = 0;
										if (process.getListeAvantageFP() != null) {
											for (int i = 0; i < process.getListeAvantageFP().size(); i++) {
						%>
						<tr>
							<td class="sigp2-tabLigne">FP</td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_TYPE_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_MONTANT_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_NATURE_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_RUBRIQUE_spec(indiceAvNat)%></td>
						</tr>
						<%
							indiceAvNat++;
											}
										}
										if (process.getListeAvantageAFF() != null) {
											for (int i = 0; i < process.getListeAvantageAFF().size(); i++) {
												if (!process.getListeAvantageFP().contains(process.getListeAvantageAFF().get(i))) {
						%>
						<tr>
							<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
								type="image" src="images/suppression.gif"
								class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
								height="16px" width="16px"
								name="<%=process.getNOM_PB_SUPPRIMER_AVANTAGE_spec(indiceAvNat)%>">
							</td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_TYPE_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_MONTANT_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_NATURE_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_RUBRIQUE_spec(indiceAvNat)%></td>
						</tr>
						<%
							indiceAvNat++;
												}
											}
										}
										if (process.getListeAvantageAAjouter() != null) {
											for (int i = 0; i < process.getListeAvantageAAjouter().size(); i++) {
						%>
						<tr>
							<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
								type="image" src="images/suppression.gif"
								class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
								height="16px" width="16px"
								name="<%=process.getNOM_PB_SUPPRIMER_AVANTAGE_spec(indiceAvNat)%>">
							</td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_TYPE_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_MONTANT_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_NATURE_spec(indiceAvNat)%></td>
							<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_AVANTAGE_RUBRIQUE_spec(indiceAvNat)%></td>
						</tr>
						<%
							indiceAvNat++;
											}
										}
						%>
					</table>
					<BR />
			</div>
			<%
				if (process.getVAL_RG_SPECIFICITE_spec().equals(process.getNOM_RB_SPECIFICITE_D_spec())) {
								out.println("<div align='left' style='float: left; width: 100%; display: block;'>");
							} else {
								out.println("<div align='left' style='float: left; width: 100%; display: none;'>");
							}
			%>
			<br />
			<table class="sigp2-tab">
				<tr>
					<th class="sigp2-tabTitre"><INPUT tabindex="" type="image"
						src="images/ajout.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_AJOUTER_DELEGATION_spec()%>"></th>
					<th class="sigp2-tabTitre" width="150px">Type</th>
					<th class="sigp2-tabTitre" width="700px">Commentaire</th>
				</tr>
				<%
					int indiceDel = 0;
								if (process.getListeDelegationFP() != null) {
									for (int i = 0; i < process.getListeDelegationFP().size(); i++) {
				%>
				<tr>
					<td class="sigp2-tabLigne">FP</td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_TYPE_spec(indiceDel)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_COMMENT_spec(indiceDel)%></td>
				</tr>
				<%
					indiceDel++;
									}
								}
								if (process.getListeDelegationAFF() != null) {
									for (int i = 0; i < process.getListeDelegationAFF().size(); i++) {
										if (!process.getListeDelegationFP().contains(process.getListeDelegationAFF().get(i))) {
				%>
				<tr>
					<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
						type="image" src="images/suppression.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_SUPPRIMER_DELEGATION_spec(indiceDel)%>">
					</td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_TYPE_spec(indiceDel)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_COMMENT_spec(indiceDel)%></td>
				</tr>
				<%
					indiceDel++;
										}
									}
								}
								if (process.getListeDelegationAAjouter() != null) {
									for (int i = 0; i < process.getListeDelegationAAjouter().size(); i++) {
				%>
				<tr>
					<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
						type="image" src="images/suppression.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_SUPPRIMER_DELEGATION_spec(indiceDel)%>">
					</td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_TYPE_spec(indiceDel)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_DELEGATION_COMMENT_spec(indiceDel)%></td>
				</tr>
				<%
					indiceDel++;
									}
								}
				%>
			</table>
			<BR />
			</div>
			<%
				if (process.getVAL_RG_SPECIFICITE_spec().equals(process.getNOM_RB_SPECIFICITE_RI_spec())) {
								out.println("<div align='left' style='float: left; width: 100%; display: block;'>");

							} else {
								out.println("<div align='left' style='float: left; width: 100%; display: none;'>");
							}
			%>
			<br />
			<table class="sigp2-tab">
				<tr>
					<th class="sigp2-tabTitre"><INPUT tabindex="" type="image"
						src="images/ajout.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_AJOUTER_REGIME_spec()%>"></th>
					<th class="sigp2-tabTitre" width="150px">Type</th>
					<th class="sigp2-tabTitre" width="100px">Forfait</th>
					<th class="sigp2-tabTitre" width="100px">Nb points</th>
					<th class="sigp2-tabTitre" width="380px">Rubrique</th>
				</tr>
				<%
					int indiceRegIndemn = 0;
								if (process.getListeRegimeFP() != null) {
									for (int i = 0; i < process.getListeRegimeFP().size(); i++) {
				%>
				<tr>
					<td class="sigp2-tabLigne">FP</td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_TYPE_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_FORFAIT_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_NB_POINTS_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_RUBRIQUE_spec(indiceRegIndemn)%></td>
				</tr>
				<%
					indiceRegIndemn++;
									}
								}
								if (process.getListeRegimeAFF() != null) {
									for (int i = 0; i < process.getListeRegimeAFF().size(); i++) {
										if (!process.getListeRegimeFP().contains(process.getListeRegimeAFF().get(i))) {
				%>
				<tr>
					<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
						type="image" src="images/suppression.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_SUPPRIMER_REGIME_spec(indiceRegIndemn)%>">
					</td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_TYPE_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_FORFAIT_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_NB_POINTS_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_RUBRIQUE_spec(indiceRegIndemn)%></td>
				</tr>
				<%
					indiceRegIndemn++;
										}
									}
								}
								if (process.getListeRegimeAAjouter() != null) {
									for (int i = 0; i < process.getListeRegimeAAjouter().size(); i++) {
				%>
				<tr>
					<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
						type="image" src="images/suppression.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_SUPPRIMER_REGIME_spec(indiceRegIndemn)%>">
					</td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_TYPE_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_FORFAIT_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_NB_POINTS_spec(indiceRegIndemn)%></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_REGINDEMN_RUBRIQUE_spec(indiceRegIndemn)%></td>
				</tr>
				<%
					indiceRegIndemn++;
									}
								}
				%>
			</table>
			<BR />
			</div>
			<%
				if (process.getVAL_RG_SPECIFICITE_spec().equals(process.getNOM_RB_SPECIFICITE_PP_spec())) {
								out.println("<div align='left' style='float: left; width: 100%; display: block;'>");

							} else {
								out.println("<div align='left' style='float: left; width: 100%; display: none;'>");
							}
			%>
			<br />
			<table class="sigp2-tab">
				<tr>

					<%
						if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)) {
					%>

					<th class="sigp2-tabTitre"><INPUT tabindex="" type="image"
						src="images/ajout.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_AJOUTER_PRIME_POINTAGE_spec()%>"></th>
					<%
						}
					%>
					<th class="sigp2-tabTitre" width="380px">Libelle</th>
					<th class="sigp2-tabTitre" width="380px">Provenance</th>
				</tr>
				<%
					ArrayList<Integer> rubs = process.getListeRubs();
								int indicePrimePointage = 0;
								if (process.getListePrimePointageFP() != null) {
									System.out.println("------\nfp:");
									for (int i = 0; i < process.getListePrimePointageFP().size(); i++) {
										if (!rubs.contains(process.getListePrimePointageFP().get(i).getNumRubrique())) {
				%>
				<tr>
					<td class="sigp2-tabLigne"><input tabindex="" type="image"
						src="images/check.gif"
						name="<%=process.getNomPrimeFP_AFF_spec(indicePrimePointage)%>"
						value="checked"></td>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrimePointage)%></td>
					<td class="sigp2-tabLigne"><%="Fiche Poste " + process.getPosteCourantTitle()%></td>
				</tr>
				<%
					}
										System.out.println("prime:"+ process.getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrimePointage)+"  index="+indicePrimePointage);
										indicePrimePointage++;
									}
								}
								if (process.getListePrimePointageAFF() != null && process.getListePrimePointageAFF().size() > 0) {
									System.out.println("affectés:");
									for (int i = 0; i < process.getListePrimePointageAFF().size(); i++) {
				%>
				<tr>

					<%
						if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)) {
					%>
					<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
						type="image" src="images/suppression.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_SUPPRIMER_PRIME_POINTAGE_spec(indicePrimePointage)%>">
					</td>
					<%
						}
					%>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrimePointage)%></td>
					<td class="sigp2-tabLigne"><%="Affectation "%></td>
				</tr>
				<%
					System.out.println("prime:"+ process.getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrimePointage)+"  index="+indicePrimePointage);
										indicePrimePointage++;

									}
								}
								if (process.getListePrimePointageAffAAjouter() != null && process.getListePrimePointageAffAAjouter().size() > 0) {
									System.out.println("a ajouter:");
									for (int i = 0; i < process.getListePrimePointageAffAAjouter().size(); i++) {
				%>
				<tr>
					<%
						if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)) {
					%>
					<td class="sigp2-tabLigne" align="center"><INPUT tabindex=""
						type="image" src="images/suppression.gif"
						class="<%=MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "")%>"
						height="16px" width="16px"
						name="<%=process.getNOM_PB_SUPPRIMER_PRIME_POINTAGE_spec(indicePrimePointage)%>"></td>
					<%
						}
					%>
					<td class="sigp2-tabLigne"><%=process.getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrimePointage)%></td>
					<td class="sigp2-tabLigne">En cours d'ajout</td>
				</tr>
				<%
					System.out.println("prime:"+ process.getVAL_ST_LST_PRIME_POINTAGE_RUBRIQUE_spec(indicePrimePointage)+"  index="+indicePrimePointage);
										indicePrimePointage++;
									}
								}
								//if (process.getListePrimePointageAffASupprimer()!= null && process.getListePrimePointageAffASupprimer().size()>0) {
								//	System.out.println("a supprimer:");
								//	for (PrimePointageAff p:process.getListePrimePointageAffASupprimer()) {
								//	System.out.println("prime:"+ p.getNumRubrique()+"  index="+indicePrimePointage);
								//	indicePrimePointage++;
								//	}
								//}
				%>
			</table>
			<BR />
			</div>
			<BR />
			<%
				if (!"".equals(process.getVAL_ST_ACTION_spec())) {
			%>
			<FIELDSET class="sigp2Fieldset"
				style="text-align: left; margin: 10px; width: 1030px;">
				<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_spec()%>
					<%=process.getVAL_ST_SPECIFICITE()%></legend>
				<BR />
				<%
					if (!process.getVAL_ST_ACTION_spec().equals(process.ACTION_SUPPRIMER_SPEC)) {
				%>
				<%
					if (process.getVAL_ST_SPECIFICITE().equals(process.SPEC_AVANTAGE_NATURE_SPEC)) {
				%>
				<span class="sigp2Mandatory"
					style="margin-left: 20px; position: relative; width: 150px;">Type
					d'avantage : </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_TYPE_AVANTAGE_spec()%>"
					style="width: 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_AVANTAGE_spec(), process.getVAL_LB_TYPE_AVANTAGE_SELEC_specT())%>
				</SELECT>
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 150px;">Nature
					d'avantage : </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_NATURE_AVANTAGE()%>"
					style="width: 350px;">
						<%=process.forComboHTML(process.getVAL_LB_NATURE_AVANTAGE_spec(), process.getVAL_LB_NATURE_AVANTAGE_SELECT_spec())%>
				</SELECT>
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 150px;">Montant
					: </span> <span> <INPUT class="sigp2-saisie" maxlength="8"
					name="<%=process.getNOM_EF_MONTANT_AVANTAGE_spec()%>" size="10"
					type="text" value="<%=process.getVAL_EF_MONTANT_AVANTAGE_spec()%>">
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 150px;">Rubrique
					: </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_RUBRIQUE_AVANTAGE()%>"
					style="width: 530px;">
						<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE_AVANTAGE(), process.getVAL_LB_RUBRIQUE_AVANTAGE_SELECT())%>
				</SELECT>
				</span>
				<%
					} else if (process.getVAL_ST_SPECIFICITE().equals(process.SPEC_DELEGATION_SPEC)) {
				%>
				<span class="sigp2Mandatory"
					style="margin-left: 20px; position: relative; width: 120px;">Type
					: </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_TYPE_DELEGATION_spec()%>"
					style="width: 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_DELEGATION_spec(), process.getVAL_LB_TYPE_DELEGATION_SELECT_spec())%>
				</SELECT>
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 120px;">Commentaire
					: </span> <span> <INPUT class="sigp2-saisie" maxlength="100"
					name="<%=process.getNOM_EF_COMMENT_DELEGATION_spec()%>" size="100"
					type="text"
					value="<%=process.getVAL_EF_COMMENT_DELEGATION_spec()%>">
				</span>
				<%
					} else if (process.getVAL_ST_SPECIFICITE().equals(process.SPEC_PRIME_POINTAGE_SPEC)) {
				%>
				<span class="sigp2Mandatory"
					style="margin-left: 20px; position: relative; width: 120px;">Prime
					: </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_RUBRIQUE_PRIME_POINTAGE()%>"
					style="width: 350px;">
						<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE_PRIME_POINTAGE_spec(), process.getVAL_LB_RUBRIQUE_PRIME_POINTAGE_SELECT())%>
				</SELECT>
				</span> <BR /> <BR />
				<%
					} else {
				%>
				<span class="sigp2Mandatory"
					style="margin-left: 20px; position: relative; width: 70px;">Type
					: </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_TYPE_REGIME_spec()%>"
					style="width: 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TYPE_REGIME_spec(), process.getVAL_LB_TYPE_REGIME_SELECT_spec())%>
				</SELECT>
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 70px;">Forfait
					: </span> <span> <INPUT class="sigp2-saisie" maxlength="8"
					name="<%=process.getNOM_EF_FORFAIT_REGIME_spec()%>" size="10"
					type="text" value="<%=process.getVAL_EF_FORFAIT_REGIME_spec()%>">
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 70px;">Nb
					points : </span> <span> <INPUT class="sigp2-saisie" maxlength="5"
					name="<%=process.getNOM_EF_NB_POINTS_REGIME_spec()%>" size="10"
					type="text" value="<%=process.getVAL_EF_NB_POINTS_REGIME_spec()%>">
				</span> <BR /> <BR /> <span class="sigp2"
					style="margin-left: 20px; position: relative; width: 70px;">Rubrique
					: </span> <span> <SELECT class="sigp2-liste"
					name="<%=process.getNOM_LB_RUBRIQUE_REGIME()%>"
					style="width: 530px;">
						<%=process.forComboHTML(process.getVAL_LB_RUBRIQUE_REGIME(), process.getVAL_LB_RUBRIQUE_REGIME_SELECT())%>
				</SELECT>
				</span>
				<%
					}
				%>
				<BR /> <BR /> <INPUT type="submit" class="sigp2-Bouton-100"
					value="Ajouter" name="<%=process.getNOM_PB_VALIDER_AJOUT_spec()%>">
				<%
					}
				%>
				<BR />
			</FIELDSET>
			<%
				}
			%>
			<BR />
		</FIELDSET>


		<%
			if (process.getAffectationCourant().getIdFichePosteSecondaire() != null) {
		%>
		<FIELDSET class="sigp2Fieldset"
			style="text-align: left; margin: 10px;">
			<legend class="sigp2Legend">Fiche de poste secondaire</legend>
			<span class="sigp2Mandatory"
				style="position: relative; width: 150px;">Réf. fiche de poste
				: </span> <span class="sigp2-statique" style="width: 100px"><%=process.getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE()%></span>
			<%
				if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)) {
			%>
			<INPUT type="image" src="images/loupe.gif"
				name="<%=process.getNOM_PB_RECHERCHER_FP_SECONDAIRE()%>">
			<%
				}
			%>
			<span class="sigp2" style="width: 125px">Temps réglementaire :</span>
			<span class="sigp2-statique" style="width: 170px"><%=process.getVAL_ST_TPS_REG_SECONDAIRE()%></span>
			<BR /> <BR /> <span class="sigp2" style="width: 60px">Direction
				:</span> <span class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_DIRECTION_SECONDAIRE()%></span>
			<span class="sigp2" style="width: 60px">Service :</span> <span
				class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_SERVICE_SECONDAIRE()%></span>
			<span class="sigp2" style="width: 60px">Section :</span> <span
				class="sigp2-statique" style="width: 250px"><%=process.getVAL_ST_SUBDIVISION_SECONDAIRE()%></span>
			<BR /> <BR /> <span class="sigp2" style="width: 100px">Titre
				du poste :</span> <span class="sigp2-statique" style="width: 600px"><%=process.getVAL_ST_TITRE_FP_SECONDAIRE()%></span>
			<BR /> <BR /> <span class="sigp2" style="width: 100px">Lieu
				du poste :</span> <span class="sigp2-statique" style="width: 600px"><%=process.getVAL_ST_LIEU_FP_SECONDAIRE()%></span>
			<BR />
		</FIELDSET>
		<%
			}
		%>
		<%
			}
		%>
		<BR />
		<div>
			<%
				if (!process.getVAL_ST_ACTION().equals(process.ACTION_IMPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)) {
			%>
			<span class="sigp2"
				style="margin-left: 20px; position: relative; width: 123px;">Réf.
				Arrêté : </span> <INPUT class="sigp2-saisiemajusculenongras" maxlength="6"
				name="<%=process.getNOM_EF_REF_ARRETE()%>" size="10" type="text"
				value="<%=process.getVAL_EF_REF_ARRETE()%>"
				style="margin-right: 60px;"> <span class="sigp2Mandatory"
				style="margin-left: 20px; position: relative; width: 112px;">Date
				arrêté : </span> <INPUT tabindex="" class="sigp2-saisie" maxlength="10"
				name="<%=process.getNOM_EF_DATE_ARRETE()%>" size="10" type="text"
				value="<%=process.getVAL_EF_DATE_ARRETE()%>"> <IMG
				src="images/calendrier.gif"
				onclick="return showCalendar('<%=process.getNOM_EF_DATE_ARRETE()%>', 'dd/mm/y');"
				hspace="5"> <span class="sigp2Mandatory"
				style="margin-left: 20px; position: relative; width: 85px;">Date
				début : </span> <INPUT tabindex="" class="sigp2-saisie" maxlength="10"
				name="<%=process.getNOM_EF_DATE_DEBUT()%>" size="10" type="text"
				value="<%=process.getVAL_EF_DATE_DEBUT()%>"> <IMG
				src="images/calendrier.gif"
				onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');"
				hspace="5"> <span class="sigp2"
				style="margin-left: 20px; position: relative; width: 65px;">Date
				fin : </span> <INPUT tabindex="" class="sigp2-saisie" maxlength="10"
				name="<%=process.getNOM_EF_DATE_FIN()%>" size="10" type="text"
				value="<%=process.getVAL_EF_DATE_FIN()%>"> <IMG
				src="images/calendrier.gif"
				onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');"
				hspace="5"> <BR /> <BR /> <span class="sigp2Mandatory"
				style="margin-left: 20px; position: relative; width: 120px;">Motif
				d'affectation :</span> <SELECT tabindex="" class="sigp2-saisie"
				name="<%=process.getNOM_LB_MOTIF_AFFECTATION()%>"
				style="width: 140px;">
				<%=process.forComboHTML(process.getVAL_LB_MOTIF_AFFECTATION(), process.getVAL_LB_MOTIF_AFFECTATION_SELECT())%>
			</SELECT> <span class="sigp2"
				style="margin-left: 20px; position: relative; width: 110px">Temps
				de travail :</span> <SELECT tabindex="" class="sigp2-saisie"
				name="<%=process.getNOM_LB_TEMPS_TRAVAIL()%>" style="width: 50px;">
				<%=process.forComboHTML(process.getVAL_LB_TEMPS_TRAVAIL(), process.getVAL_LB_TEMPS_TRAVAIL_SELECT())%>
			</SELECT> <span class="sigp2"
				style="margin-left: 20px; position: relative; width: 123px;">Commentaire
				: </span> <INPUT class="sigp2-saisie" maxlength="100"
				name="<%=process.getNOM_EF_COMMENTAIRE()%>" size="50" type="text"
				value="<%=process.getVAL_EF_COMMENTAIRE()%>"
				style="margin-right: 60px;"> <span class="sigp2Mandatory"
				style="width: 10px"> </span>
			<%
				} else if (process.getVAL_ST_ACTION().equals(process.ACTION_IMPRESSION)) {
			%>
			<span class="sigp2"
				style="margin-left: 20px; position: relative; width: 200px;">Choix
				du document à imprimer :</span> <SELECT tabindex="" class="sigp2-saisie"
				name="<%=process.getNOM_LB_LISTE_IMPRESSION()%>"
				style="width: 250px;">
				<%=process.forComboHTML(process.getVAL_LB_LISTE_IMPRESSION(), process.getVAL_LB_LISTE_IMPRESSION_SELECT())%>
			</SELECT> <BR /> <BR /> <span class="sigp2-saisie"
				style="margin-left: 20px; position: relative;"><%=process.getVAL_ST_WARNING()%></span>
			<%
				} else {
			%>
			<span class="sigp2"
				style="margin-left: 20px; position: relative; width: 123px;">Réf.
				Arrêté : </span> <INPUT class="sigp2-saisiemajusculenongras" maxlength="5"
				name="<%=process.getNOM_EF_REF_ARRETE()%>" size="10" type="text"
				value="<%=process.getVAL_EF_REF_ARRETE()%>"
				style="margin-right: 60px;" disabled="disabled"> <span
				class="sigp2Mandatory"
				style="margin-left: 20px; position: relative; width: 112px;">Date
				arrêté : </span> <INPUT tabindex="" class="sigp2-saisie" maxlength="10"
				name="<%=process.getNOM_EF_DATE_ARRETE()%>" size="10" type="text"
				value="<%=process.getVAL_EF_DATE_ARRETE()%>" disabled="disabled">
			<span class="sigp2Mandatory"
				style="margin-left: 20px; position: relative; width: 85px;">Date
				début : </span> <INPUT tabindex="" class="sigp2-saisie" maxlength="10"
				name="<%=process.getNOM_EF_DATE_DEBUT()%>" size="10" type="text"
				value="<%=process.getVAL_EF_DATE_DEBUT()%>" disabled="disabled">
			<span class="sigp2"
				style="margin-left: 20px; position: relative; width: 65px;">Date
				fin : </span> <INPUT tabindex="" class="sigp2-saisie" maxlength="10"
				name="<%=process.getNOM_EF_DATE_FIN()%>" size="10" type="text"
				value="<%=process.getVAL_EF_DATE_FIN()%>" disabled="disabled">
			<BR /> <BR /> <span class="sigp2Mandatory"
				style="margin-left: 20px; position: relative; width: 120px;">Motif
				d'affectation :</span> <SELECT tabindex="" class="sigp2-saisie"
				name="<%=process.getNOM_LB_MOTIF_AFFECTATION()%>"
				style="width: 140px;" disabled="disabled">
				<%=process.forComboHTML(process.getVAL_LB_MOTIF_AFFECTATION(), process.getVAL_LB_MOTIF_AFFECTATION_SELECT())%>
			</SELECT> <span class="sigp2"
				style="margin-left: 20px; position: relative; width: 110px">Temps
				de travail :</span> <SELECT tabindex="" class="sigp2-saisie"
				name="<%=process.getNOM_LB_TEMPS_TRAVAIL()%>" style="width: 50px;"
				disabled="disabled">
				<%=process.forComboHTML(process.getVAL_LB_TEMPS_TRAVAIL(), process.getVAL_LB_TEMPS_TRAVAIL_SELECT())%>
			</SELECT> <span class="sigp2"
				style="margin-left: 20px; position: relative; width: 123px;">Commentaire
				: </span> <INPUT class="sigp2-saisie" maxlength="100"
				name="<%=process.getNOM_EF_COMMENTAIRE()%>" size="50" type="text"
				value="<%=process.getVAL_EF_COMMENTAIRE()%>"
				style="margin-right: 60px;" disabled="disabled"> <span
				class="sigp2Mandatory" style="width: 10px"> </span>
			<%
				}
			%>
		</div>
		<BR /> <BR /> <BR />
		</div>

		<div style="text-align: center">
			<%
				if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)) {
			%>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider"
				name="<%=process.getNOM_PB_VALIDER()%>">
			<%
				}
			%>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler"
				name="<%=process.getNOM_PB_ANNULER()%>">
		</div>
		<BR />
		</FIELDSET>
		<%
			}
		%>
	</FORM>
	<%
		}
	%>
</BODY>
</HTML>