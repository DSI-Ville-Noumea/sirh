<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.poste.Affectation"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
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
		function SelectLigne(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById(i).className="";
			} 
		 document.getElementById(id).className="selectLigne";
		}

</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.OeAGENTEmploisAffectation" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<%=process.getUrlFichier()%>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des affectations de l'agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:85px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>"></span>
				    <span style="position:relative;width:50px;text-align:left;">Direction</span>
					<span style="position:relative;width:300px;text-align:left;">Service/Section/...</span>
					<span style="position:relative;width:80px;text-align: center;">Date début</span>
					<span style="position:relative;width:85px;text-align: center;">Date fin</span>
					<span style="position:relative;width:65px;text-align: left;">Fiche poste</span>
					<span style="position:relative;text-align: left;">Titre poste</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceAff = 0;
							if (process.getListeAffectation()!=null){
								for (int i = 0;i<process.getListeAffectation().size();i++){
								Affectation aff = (Affectation) process.getListeAffectation().get(i);
							%>
									<tr id="<%=indiceAff%>" onmouseover="SelectLigne(<%=indiceAff%>,<%=process.getListeAffectation().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAff)%>">	
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAff)%>">
											<%if(aff.isActive()){ %>
											<INPUT title="imprimer" type="image" src="images/imprimer.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_IMPRIMER(indiceAff)%>">
											<% }else{%>
											<span style="width: 22px;"></span>
											<%} %>
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAff)%>">				
											<%if(aff.isActive()){ %>
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceAff)%>">
											<% }%>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: left;"><%=process.getVAL_ST_DIR(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: left;"><%=process.getVAL_ST_SERV(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:65px;text-align: left;"><%=process.getVAL_ST_NUM_FP(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_TITRE(indiceAff)%></td>
									</tr>
									<%
									indiceAff++;
								}
							}%>
						</table>	
						</div>	
			<BR/>
			<div style="text-align: center;">
				<INPUT type="submit" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "sigp2-Bouton-100") %>" value="Historique" name="<%=process.getNOM_PB_HISTORIQUE()%>">
				<INPUT type="submit" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2-Bouton-100") %>" value="Historique" name="<%=process.getNOM_PB_HISTORIQUE()%>">
			</div>	
				</FIELDSET>
<%if (! Const.CHAINE_VIDE.equals(process.getVAL_ST_ACTION()) ) {%>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %>
			<FONT color='red'>Veuillez valider votre choix.</FONT>
			<BR/>
		<%} %>
		<BR/>
		<div>
		    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
			    <legend class="sigp2Legend">Fiche de poste</legend>
	   			<span class="sigp2Mandatory" style="position:relative;width:150px;">Réf. fiche de poste : </span>
				<span class="sigp2-statique" style="width:100px"><%=process.getVAL_ST_NUM_FICHE_POSTE()%></span>
				<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_RECHERCHER_FP()%>">
				<%} %>
				<span class="sigp2" style="width:125px">Temps réglementaire :</span>
				<span class="sigp2-statique" style="width:100px"><%=process.getVAL_ST_TPS_REG()%></span>
				<BR/><BR/>
				<span class="sigp2" style="width:60px">Direction :</span>
				<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_DIRECTION()%></span>
				<span class="sigp2" style="width:60px">Service :</span>
				<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_SERVICE()%></span>
				<span class="sigp2" style="width:60px">Section :</span>
				<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_SUBDIVISION()%></span>
				<BR/><BR/>
				<span class="sigp2" style="width:100px">Titre du poste :</span>
				<span class="sigp2-statique" style="width:600px"><%=process.getVAL_ST_TITRE_FP()%></span>
				<BR/><BR/>
				<span class="sigp2" style="width:100px">Lieu du poste :</span>
				<span class="sigp2-statique" style="width:600px"><%=process.getVAL_ST_LIEU_FP()%></span>
				<BR/>
			</FIELDSET>
			<BR/>
			<% if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){%>
		    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
			    <legend class="sigp2Legend">Fiche de poste secondaire</legend>
	   			<span class="sigp2Mandatory" style="position:relative;width:150px;">Réf. fiche de poste : </span>
				<span class="sigp2-statique" style="width:100px"><%=process.getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE()%></span>
				<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_RECHERCHER_FP_SECONDAIRE()%>">
				<%} %>
				<span class="sigp2" style="width:125px">Temps réglementaire :</span>
				<span class="sigp2-statique" style="width:100px"><%=process.getVAL_ST_TPS_REG_SECONDAIRE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="width:60px">Direction :</span>
				<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_DIRECTION_SECONDAIRE()%></span>
				<span class="sigp2" style="width:60px">Service :</span>
				<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_SERVICE_SECONDAIRE()%></span>
				<span class="sigp2" style="width:60px">Section :</span>
				<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_SUBDIVISION_SECONDAIRE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="width:100px">Titre du poste :</span>
				<span class="sigp2-statique" style="width:600px"><%=process.getVAL_ST_TITRE_FP_SECONDAIRE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="width:100px">Lieu du poste :</span>
				<span class="sigp2-statique" style="width:600px"><%=process.getVAL_ST_LIEU_FP_SECONDAIRE()%></span>
				<BR/>
			</FIELDSET>
			<%}else{ %>
				<%if(process.getAffectationCourant().getIdFichePosteSecondaire()!=null) {%>
			    <FIELDSET class="sigp2Fieldset" style="text-align: left; margin: 10px;">
				    <legend class="sigp2Legend">Fiche de poste secondaire</legend>
		   			<span class="sigp2Mandatory" style="position:relative;width:150px;">Réf. fiche de poste : </span>
					<span class="sigp2-statique" style="width:100px"><%=process.getVAL_ST_NUM_FICHE_POSTE_SECONDAIRE()%></span>
					<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
						<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_RECHERCHER_FP_SECONDAIRE()%>">
					<%} %>
					<span class="sigp2" style="width:125px">Temps réglementaire :</span>
					<span class="sigp2-statique" style="width:100px"><%=process.getVAL_ST_TPS_REG_SECONDAIRE()%></span>
					<BR/><BR/>
					<span class="sigp2" style="width:60px">Direction :</span>
					<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_DIRECTION_SECONDAIRE()%></span>
					<span class="sigp2" style="width:60px">Service :</span>
					<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_SERVICE_SECONDAIRE()%></span>
					<span class="sigp2" style="width:60px">Section :</span>
					<span class="sigp2-statique" style="width:250px"><%=process.getVAL_ST_SUBDIVISION_SECONDAIRE()%></span>
					<BR/><BR/>
					<span class="sigp2" style="width:100px">Titre du poste :</span>
					<span class="sigp2-statique" style="width:600px"><%=process.getVAL_ST_TITRE_FP_SECONDAIRE()%></span>
					<BR/><BR/>
					<span class="sigp2" style="width:100px">Lieu du poste :</span>
					<span class="sigp2-statique" style="width:600px"><%=process.getVAL_ST_LIEU_FP_SECONDAIRE()%></span>
					<BR/>
				</FIELDSET>
				<%} %>
			<%} %>
			<BR/>
			<div>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_IMPRESSION)&&!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
				<span class="sigp2" style="margin-left:20px;position:relative;width:123px;">Réf. Arrêté : </span>
				<INPUT class="sigp2-saisiemajusculenongras" maxlength="6" name="<%= process.getNOM_EF_REF_ARRETE() %>" size="10"
					type="text" value="<%= process.getVAL_EF_REF_ARRETE() %>" style="margin-right:60px;">
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:112px;">Date arrêté : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE() %>" size="10"
					type="text" value="<%= process.getVAL_EF_DATE_ARRETE() %>">
					<IMG src="images/calendrier.gif" onclick="return showCalendar('<%=process.getNOM_EF_DATE_ARRETE()%>', 'dd/mm/y');" hspace="5">
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:85px;">Date début : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10"
					type="text" value="<%= process.getVAL_EF_DATE_DEBUT() %>">
					<IMG src="images/calendrier.gif" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');" hspace="5">
				<span class="sigp2" style="margin-left:20px;position:relative;width:65px;">Date fin : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN() %>" size="10"
					type="text" value="<%= process.getVAL_EF_DATE_FIN() %>">
					<IMG src="images/calendrier.gif" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');" hspace="5">
				<BR/><BR/>
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:120px;">Motif d'affectation :</span>
				<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF_AFFECTATION() %>" style="width:140px;">
					<%=process.forComboHTML(process.getVAL_LB_MOTIF_AFFECTATION(), process.getVAL_LB_MOTIF_AFFECTATION_SELECT())%>
				</SELECT>
				<span class="sigp2" style="margin-left:20px;position:relative;width:110px">Temps de travail :</span>
				<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_TEMPS_TRAVAIL() %>" style="width:50px;">
					<%=process.forComboHTML(process.getVAL_LB_TEMPS_TRAVAIL(), process.getVAL_LB_TEMPS_TRAVAIL_SELECT())%>
				</SELECT>
				<span class="sigp2" style="margin-left:20px;position:relative;width:123px;">Commentaire : </span>
				<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="50"
					type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>" style="margin-right:60px;">
				<span class="sigp2Mandatory" style="width:10px"> </span>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_IMPRESSION)){ %>
				<span class="sigp2" style="margin-left:20px;position:relative;width:200px;">Choix du document à imprimer :</span>
				<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_LISTE_IMPRESSION() %>" style="width:250px;">
					<%=process.forComboHTML(process.getVAL_LB_LISTE_IMPRESSION(), process.getVAL_LB_LISTE_IMPRESSION_SELECT())%>
				</SELECT>
				<BR/><BR/>
				<span class="sigp2-saisie" style="margin-left:20px;position:relative;"><%=process.getVAL_ST_WARNING()%></span>
			<%} else {%>
				<span class="sigp2" style="margin-left:20px;position:relative;width:123px;">Réf. Arrêté : </span>
				<INPUT class="sigp2-saisiemajusculenongras" maxlength="5" name="<%= process.getNOM_EF_REF_ARRETE() %>" size="10"
					type="text" value="<%= process.getVAL_EF_REF_ARRETE() %>" style="margin-right:60px;" disabled="disabled">
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:112px;">Date arrêté : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE() %>" size="10"
					type="text" value="<%= process.getVAL_EF_DATE_ARRETE() %>" disabled="disabled">
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:85px;">Date début : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10"
					type="text" value="<%= process.getVAL_EF_DATE_DEBUT() %>" disabled="disabled">
				<span class="sigp2" style="margin-left:20px;position:relative;width:65px;">Date fin : </span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN() %>" size="10"
					type="text" value="<%= process.getVAL_EF_DATE_FIN() %>" disabled="disabled">
				<BR/><BR/>
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:120px;">Motif d'affectation :</span>
				<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_MOTIF_AFFECTATION() %>" style="width:140px;" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_MOTIF_AFFECTATION(), process.getVAL_LB_MOTIF_AFFECTATION_SELECT())%>
				</SELECT>
				<span class="sigp2" style="margin-left:20px;position:relative;width:110px">Temps de travail :</span>
				<SELECT tabindex="" class="sigp2-saisie" name="<%= process.getNOM_LB_TEMPS_TRAVAIL() %>" style="width:50px;" disabled="disabled">
					<%=process.forComboHTML(process.getVAL_LB_TEMPS_TRAVAIL(), process.getVAL_LB_TEMPS_TRAVAIL_SELECT())%>
				</SELECT>
				<span class="sigp2" style="margin-left:20px;position:relative;width:123px;">Commentaire : </span>
				<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="50"
					type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>" style="margin-right:60px;" disabled="disabled">
				<span class="sigp2Mandatory" style="width:10px"> </span>
			<%} %>
			</div>			
			<BR/><BR/><BR/>
		</div>

		<div style="text-align: center">
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<% } %>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</div>
		<BR/>
	</FIELDSET>
<%} %>
</FORM>
<%} %>
</BODY>
</HTML>