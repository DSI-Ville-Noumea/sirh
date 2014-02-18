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
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des enfants de l'agent</legend>
				    <br/>
				    <span style="margin-left: 5px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>"></span>
				    <span style="margin-left:50px;">Nom</span>
					<span style="margin-left:65px;">Prénom</span>
					<span style="margin-left:50px;">Sexe</span>
					<span style="margin-left:20px;">Né(e) le</span>
					<span style="margin-left:30px;">Lieu de naissance</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceEnfant = 0;
							if (process.getListeEnfants()!=null){
								for (int i = 0;i<process.getListeEnfants().size();i++){
							%>
									<tr id="<%=indiceEnfant%>" onmouseover="SelectLigne(<%=indiceEnfant%>,<%=process.getListeEnfants().size()%>)">
										<td class="sigp2NewTab-liste" style="width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEnfant)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceEnfant)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceEnfant)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceEnfant)%>">
										</td>
										<td class="sigp2NewTab-liste" style="width:90px;text-align: left;"><%=process.getVAL_ST_NOM(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="width:90px;text-align: left;"><%=process.getVAL_ST_PRENOM(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="width:30px;text-align: center;"><%=process.getVAL_ST_SEXE(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="width:90px;text-align: center;"><%=process.getVAL_ST_DATE_NAISS(indiceEnfant)%></td>
										<td class="sigp2NewTab-liste" style="text-align: left;">&nbsp;<%=process.getVAL_ST_LIEU_NAISS(indiceEnfant)%></td>
									</tr>
							<%
								indiceEnfant++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
		    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			    <legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			    <br/>
				<span class="sigp2Mandatory" style="width: 100px;"> Nom : </span>
	    		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT class="sigp2-saisie" maxlength="40" style="width: 150px;margin-left: 10px;" name="<%= process.getNOM_EF_NOM() %>" type="text" value="<%= process.getVAL_EF_NOM() %>">
				<%} else {%>
					<INPUT class="sigp2-saisie" maxlength="40" style="width: 150px;margin-left: 10px;" name="<%= process.getNOM_EF_NOM() %>" type="text" disabled="disabled" value="<%= process.getVAL_EF_NOM() %>">
				<%} %>
				
				<span class="sigp2Mandatory" style="margin-left: 150px;width: 100px;"> Prénom : </span>
	    		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT class="sigp2-saisie" maxlength="40" style="width: 150px;margin-left: 10px;" name="<%= process.getNOM_EF_PRENOM() %>" type="text" value="<%= process.getVAL_EF_PRENOM() %>">
				<%} else {%>
					<INPUT class="sigp2-saisie" maxlength="40" style="width: 150px;margin-left: 10px;" name="<%= process.getNOM_EF_PRENOM() %>" type="text" disabled="disabled" value="<%= process.getVAL_EF_PRENOM() %>">
				<%} %>
				<br/><br/>
				
				<span class="sigp2Mandatory" style="width: 100px;"> Sexe : </span>
				<INPUT style="margin-left: 10px;" type="radio" <%= process.forRadioHTML(process.getNOM_RG_SEXE(),process.getNOM_RB_SEXE_M())%> >Masculin		
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_SEXE(),process.getNOM_RB_SEXE_F())%> >Féminin
				
				<span style="margin-left: 165px;width: 100px;" class="sigp2Mandatory"> Nationalité :</span>
				<SELECT class="sigp2-saisie" style="margin-left: 5px;" name="<%= process.getNOM_LB_NATIONALITE() %>">
					<%=process.forComboHTML(process.getVAL_LB_NATIONALITE(), process.getVAL_LB_NATIONALITE_SELECT()) %>
				</SELECT>
				<br/><br/>
				
				<span class="sigp2Mandatory"> Date de naissance :</span>
	    		<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
					<INPUT class="sigp2-saisie" maxlength="10" style="width: 90px;" name="<%= process.getNOM_EF_DATE_NAISS() %>" type="text" value="<%= process.getVAL_EF_DATE_NAISS() %>">
					<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_NAISS()%>', 'dd/mm/y');">
				<%} else {%>
					<INPUT class="sigp2-saisie" maxlength="10" style="width: 90px;" name="<%= process.getNOM_EF_DATE_NAISS() %>" type="text" disabled="disabled" value="<%= process.getVAL_EF_DATE_NAISS() %>">
				<%} %>

	            <span class="sigp2" style="margin-left: 210px;width: 100px;"> Lieu de naissance :</span>
	            <INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_LIEU_NAISS()%>" height="20px" width="20px">
	            <%if (! process.getVAL_ST_COMMUNE_NAISS().equals(null) ) {%>
		            <span class="sigp2-saisie"><%=process.getVAL_ST_COMMUNE_NAISS() %> - <%=process.getVAL_ST_PAYS_NAISS() %></span>
				<%}%>
				<br/><br/>
				
				<span class="sigp2Mandatory" style="width: 100px;"> Enfant à charge :</span>
				<INPUT style="margin-left: 10px;" type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHARGE(),process.getNOM_RB_CHARGE_O())%> >Oui
				<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_CHARGE(),process.getNOM_RB_CHARGE_N())%> >Non
				
				<span style="margin-left: 215px;width: 100px;" class="sigp2" > Autre parent :</span>
				<INPUT type="image" src="images/loupe.gif" name="<%=process.getNOM_PB_AUTRE_PARENT()%>" height="20px" width="20px" >
				<INPUT type="image" name="<%=process.getNOM_PB_AUTRE_PARENT_VIRE()%>" src="images/suppression.gif" height="20px" width="20px" >
				
				<span class="sigp2-saisie"><%=process.getVAL_ST_AUTRE_PARENT()%></span>
				<br/><br>
				
				<span style="width: 100px;" class="sigp2"> Date de décès :</span>
				<INPUT class="sigp2-saisie" maxlength="10" style="width: 90px;" name="<%= process.getNOM_EF_DATE_DECES() %>" type="text" value="<%= process.getVAL_EF_DATE_DECES() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DECES()%>', 'dd/mm/y');" hspace="5">
				<br/><br/>
				
				<span style="width: 100px;" class="sigp2"> Commentaire :</span>
				<INPUT class="sigp2-saisie" maxlength="30" style="width: 200px;" name="<%= process.getNOM_EF_COMMENTAIRE() %>"  type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
				<br/><br/>
				
				<FIELDSET>
					<legend>Scolarité</legend>
					<br/>
					<span class="sigp2" style="width: 120px;" > Date début scolarité :</span>
					<INPUT class="sigp2-saisie" maxlength="10" style="width: 90px;" name="<%= process.getNOM_EF_DATE_DEBUT_SCOLARITE() %>" type="text" value="<%= process.getVAL_EF_DATE_DEBUT_SCOLARITE() %>">
					<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT_SCOLARITE()%>', 'dd/mm/y');">
					
					<span class="sigp2" style="width: 120px;margin-left: 10px;" > Date fin scolarité :</span>
					<INPUT class="sigp2-saisie" maxlength="10" style="width: 90px;" name="<%= process.getNOM_EF_DATE_FIN_SCOLARITE() %>" type="text" value="<%= process.getVAL_EF_DATE_FIN_SCOLARITE() %>">
					<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN_SCOLARITE()%>', 'dd/mm/y');">
					
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER_SCOLARITE()%>">
					<BR/><BR/>
					
				    <div style="overflow: auto;height: 50px;width:300px;">
						<table class="sigp2NewTab" style="text-align:left;width:280px;">
							<%
							int indiceScol = 0;
							if (process.getListeScolarites()!=null){
								for (int i = 0;i<process.getListeScolarites().size();i++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="width:30px;" align="center">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_SCOLARITE(indiceScol)%>">
										</td>
										<td class="sigp2NewTab-liste" style="width:120px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceScol)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indiceScol)%></td>
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
				<span class="sigp2" style="width: 100px">Nom : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px;"><%=process.getVAL_ST_NOM() %></span>
				<span class="sigp2" style="width: 100px">Prénom : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px"><%=process.getVAL_ST_PRENOM() %></span>
				<br/>
				<span class="sigp2" style="width: 100px">Sexe : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px"><%=process.getVAL_ST_SEXE() %></span>
				<span class="sigp2" style="width: 100px">Nationalité : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px"><%=process.getVAL_ST_NATIONALITE() %></span>
				<br/>
				<span class="sigp2" style="width: 100px">Date de naissance : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px"><%=process.getVAL_ST_DATENAISS() %></span>
				<span class="sigp2" style="width: 100px">Lieu de naissance : </span>
	            <%if (! process.getVAL_ST_COMMUNE_NAISS().equals(null) ) {%>
		            <span class="sigp2-saisie" style="margin-left: 20px;width: 350px"><%=process.getVAL_ST_COMMUNE_NAISS() %> - <%=process.getVAL_ST_PAYS_NAISS() %></span>
				<%}%>
				<br/>
				<span class="sigp2" style="width: 100px">Enfant à charge : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px"><%=process.getVAL_ST_ACHARGE() %></span>                                 
				<span class="sigp2" style="width: 100px">Autre parent : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 350px"><%=process.getVAL_ST_AUTRE_PARENT()%></span>
				<br/>
				<span class="sigp2" style="width: 100px">Date de décès : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 120px"><%=process.getVAL_ST_DATEDECES() %></span>
				<br/>
				<span class="sigp2" style="width: 100px">Commentaire : </span>
				<span class="sigp2-saisie" style="margin-left: 20px;width: 360px"><%=process.getVAL_ST_COMMENTAIRE() %></span>
				<br/><br/>
				<span class="sigp2">Scolarités : </span>
					<br/>
				    <div style="overflow: auto;height: 50px;width:300px;">
						<table class="sigp2NewTab" style="text-align:left;width:280px;">
							<%
							int indiceScol = 0;
							if (process.getListeScolarites()!=null){
								for (int i = 0;i<process.getListeScolarites().size();i++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="width:120px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceScol)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;">&nbsp;<%=process.getVAL_ST_DATE_FIN(indiceScol)%></td>
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