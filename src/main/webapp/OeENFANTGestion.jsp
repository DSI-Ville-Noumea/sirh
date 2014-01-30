<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des enfants</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
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
<jsp:useBean class="nc.mairie.gestionagent.process.OeENFANTGestion" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des enfants de l'agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:65px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>"></span>
				    <span style="position:relative;width:90px;text-align: left;">Nom</span>
					<span style="position:relative;width:95px;text-align: left;">Prénom</span>
					<span style="position:relative;width:20px;text-align: center;">Sexe</span>
					<span style="position:relative;width:95px;text-align: center;">Né(e) le</span>
					<span style="position:relative;text-align: left;">Lieu de naissance</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceEnfant = 0;
							if (process.getListeEnfants()!=null){
								for (int i = 0;i<process.getListeEnfants().size();i++){
							%>
									<tr id="<%=indiceEnfant%>" onmouseover="SelectLigne(<%=indiceEnfant%>,<%=process.getListeEnfants().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEnfant)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceEnfant)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEnfant)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceEnfant)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: left;"><%=process.getVAL_ST_NOM(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: left;"><%=process.getVAL_ST_PRENOM(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:30px;text-align: center;"><%=process.getVAL_ST_SEXE(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_NAISS(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_LIEU_NAISS(indiceEnfant)%></td>
									</tr>
									<%
									indiceEnfant++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		
	</FORM>	
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
		    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			    <legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			    <br/>
				<span style="margin-right:90px;" class="sigp2Mandatory"> Nom : </span>
	    		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT class="sigp2-saisie" maxlength="40" name="<%= process.getNOM_EF_NOM() %>" size="40" type="text" value="<%= process.getVAL_EF_NOM() %>">
				<%} else {%>
					<INPUT class="sigp2-saisie" maxlength="40" name="<%= process.getNOM_EF_NOM() %>" size="40" type="text" disabled="disabled" value="<%= process.getVAL_EF_NOM() %>">
				<%} %>
				<span style="width:30px"></span>
				<span style="margin-right:60px;" class="sigp2Mandatory"> Prénom : </span>
	    		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT class="sigp2-saisie" maxlength="40" name="<%= process.getNOM_EF_PRENOM() %>" size="40" type="text" value="<%= process.getVAL_EF_PRENOM() %>">
				<%} else {%>
					<INPUT class="sigp2-saisie" maxlength="40" name="<%= process.getNOM_EF_PRENOM() %>" size="40" type="text" disabled="disabled" value="<%= process.getVAL_EF_PRENOM() %>">
				<%} %>
				<br/><br/>
				<span style="margin-right:90px;" class="sigp2Mandatory"> Sexe : </span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_SEXE(),process.getNOM_RB_SEXE_M())%> >Masculin
				<span style="width:10px"></span>			
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_SEXE(),process.getNOM_RB_SEXE_F())%> >Féminin
				<span style="width:107px"></span>
				<span style="margin-right:45px;" class="sigp2Mandatory"> Nationalité :</span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_NATIONALITE() %>">
					<%=process.forComboHTML(process.getVAL_LB_NATIONALITE(), process.getVAL_LB_NATIONALITE_SELECT()) %>
				</SELECT>
				<br/><br/>
				<span style="margin-right:10px;" class="sigp2Mandatory"> Date de Naissance :</span>
	    		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<span class="sigp2-saisie"><INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_NAISS() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_NAISS() %>"></span>
					<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_NAISS()%>', 'dd/mm/y');">
				<%} else {%>
					<span class="sigp2-saisie"><INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_NAISS() %>" size="10" type="text" disabled="disabled" value="<%= process.getVAL_EF_DATE_NAISS() %>"></span>
				<%} %>

				<span style="width:142px"></span>
	            <span class="sigp2" nowrap> Lieu de naissance :</span>
	            <INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_LIEU_NAISS()%>" height="20px" width="20px">
	            <%if (! process.getVAL_ST_COMMUNE_NAISS().equals(null) ) {%>
		            <span align="left" class="sigp2-saisie" colspan="3">&nbsp;<%=process.getVAL_ST_COMMUNE_NAISS() %> - <%=process.getVAL_ST_PAYS_NAISS() %></span>
				<%}%>
				<span style="width:30px"></span>
				<br/><br/>
				<span style="margin-right:30px;" class="sigp2Mandatory" nowrap> Enfant à charge :</span>
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHARGE(),process.getNOM_RB_CHARGE_O())%> >Oui
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHARGE(),process.getNOM_RB_CHARGE_N())%> >Non
				<span style="width:172px"></span>
				<span class="sigp2" nowrap> Autre parent :</span>
				<span width="32" nowrap>
					<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_AUTRE_PARENT()%>" height="20px" width="20px" >
					<INPUT type="image" name="<%=process.getNOM_PB_AUTRE_PARENT_VIRE()%>" src="images/suppression.gif" height="20px" width="20px" >
				</span>
				<span class="sigp2-saisie" width="130"><%=process.getVAL_ST_AUTRE_PARENT()%></span>
				<br/><br>
				<span style="margin-right:36px;" class="sigp2"> Date de décès :</span>
				<span class="sigp2-saisie"><INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DECES() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DECES() %>"></span>
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DECES()%>', 'dd/mm/y');" hspace="5">
				<br/><br/>
				<span style="margin-right:40px;" class="sigp2" nowrap> Commentaire :</span>
				<INPUT class="sigp2-saisie" maxlength="30" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="50" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
				<br/><br/>
				<FIELDSET>
					<legend>Scolarité</legend>
					<br/>
					<span style="width:20px"></span>
					<span class="sigp2" nowrap> Date début scolarité :</span>
					<span class="sigp2-saisie"><INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DEBUT_SCOLARITE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEBUT_SCOLARITE() %>"></span>
					<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT_SCOLARITE()%>', 'dd/mm/y');">
					<span style="width:20px"></span>
					<span class="sigp2" nowrap> Date fin scolarité :</span>
					<span class="sigp2-saisie"><INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_FIN_SCOLARITE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN_SCOLARITE() %>"></span>
					<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN_SCOLARITE()%>', 'dd/mm/y');">
					
					<span style="position:relative;width:45px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_SCOLARITE()%>"></span>
					<BR/><BR/>
				    <div style="overflow: auto;height: 50px;width:300px;margin-right: 0px;margin-left: 20px;">
						<table class="sigp2NewTab" style="text-align:left;width:280px;">
							<%
							int indiceScol = 0;
							if (process.getListeScolarites()!=null){
								for (int i = 0;i<process.getListeScolarites().size();i++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_SCOLARITE(indiceScol)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:120px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceScol)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indiceScol)%></td>
									</tr>
									<%
									indiceScol++;
								}
							}%>
						</table>	
					</div>
				</FIELDSET>
			</FIELDSET>
		<%}else{ %>
		    <FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
			    <legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			    <% if (process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %><FONT color='red'>Veuillez valider votre choix.</FONT>
			    <br/><br/>
			    <% } %>
				<span class="sigp2" style="width: 120px">Nom : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_NOM() %></span>
				<span class="sigp2" style="width: 120px">Prénom : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_PRENOM() %></span>
				<br/>
				<span class="sigp2" style="width: 120px">Sexe : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_SEXE() %></span>
				<span class="sigp2" style="width: 120px">Nationalité : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_NATIONALITE() %></span>
				<br/>
				<span class="sigp2" style="width: 120px">Date de Naissance : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_DATENAISS() %></span>
				<span class="sigp2" style="width: 120px">Lieu de naissance : </span>
	            <%if (! process.getVAL_ST_COMMUNE_NAISS().equals(null) ) {%>
		            <span class="sigp2-saisie" style="width: 350px"><%=process.getVAL_ST_COMMUNE_NAISS() %> - <%=process.getVAL_ST_PAYS_NAISS() %></span>
				<%}%>
				<br/>
				<span class="sigp2" style="width: 120px">Enfant à charge : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_ACHARGE() %></span>                                 
				<span class="sigp2" style="width: 120px">Autre parent : </span>
				<span class="sigp2-saisie" style="width: 350px"><%=process.getVAL_ST_AUTRE_PARENT()%></span>
				<br/>
				<span class="sigp2" style="width: 120px">Date de décès : </span>
				<span class="sigp2-saisie" style="width: 120px"><%=process.getVAL_ST_DATEDECES() %></span>
				<br/>
				<span class="sigp2" style="width: 120px">Commentaire : </span>
				<span class="sigp2-saisie" style="width: 360px"><%=process.getVAL_ST_COMMENTAIRE() %></span>
				<br/><br/>
				<span class="sigp2">Scolarités : </span>
					<br/>
				    <div style="overflow: auto;height: 50px;width:300px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:280px;">
							<%
							int indiceScol = 0;
							if (process.getListeScolarites()!=null){
								for (int i = 0;i<process.getListeScolarites().size();i++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="position:relative;width:120px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceScol)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indiceScol)%></td>
									</tr>
									<%
									indiceScol++;
								}
							}%>
						</table>	
					</div>
				<br/>
			</FIELDSET>
			<br/>
		<%} %>
			<FIELDSET class="sigp2Fieldset" style="text-align:center;margin:10px;width:1030px;">
				<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)) { %>
  					<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>"></span>
				<% } %>
    			<span class="sigp2"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>"></span>
			</FIELDSET>
      </FORM>
<%}%>
<%}%>
</BODY>
</HTML>