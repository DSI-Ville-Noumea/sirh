<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.diplome.DiplomeAgent"%>
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<jsp:useBean class="nc.mairie.gestionagent.process.OeAGENTDIPLOMEGestion" id="process" scope="session"></jsp:useBean>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<SCRIPT language="javascript" src="js/GestionOnglet.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des diplômes d'un agent</TITLE>

<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
		
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
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
		function SelectLigneTabDoc(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById("doc"+i).className="";
			} 
		 document.getElementById("doc"+id).className="selectLigne";
		}

</SCRIPT>		
		<%
		ArrayList listeEcoles = process.getListeEcoles();
		
		String res = 	"<script language=\"javascript\">\n"+
				"var availableEcoles = new Array(\n";
		
		for (int i = 0; i < listeEcoles.size(); i++){
			DiplomeAgent ecole = (DiplomeAgent) listeEcoles.get(i);
			res+= "   \""+ecole.getNomEcole().trim().toUpperCase()+"\"";
			if (i+1 < listeEcoles.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listeEcoles").autocomplete({source:availableEcoles
				});
			});		
		</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">			
				

		<div style="margin-left:10px;margin-top:20px;text-align:left;width:1030px" align="left">
			<% if (process.onglet.equals("ONGLET1")) {%>
				<span id="titreOngletDiplome" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET1');">&nbsp;Diplômes&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletDiplome" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET1');">&nbsp;Diplômes&nbsp;</span>&nbsp;&nbsp;
			<% } %>
			<% if (process.onglet.equals("ONGLET2")) {%>
				<span id="titreOngletFormation" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET2');">&nbsp;Formations&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletFormation" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET2');">&nbsp;Formations&nbsp;</span>&nbsp;&nbsp;
			<% } %>
			<% if (process.onglet.equals("ONGLET3")) {%>
				<span id="titreOngletPermis" class="OngletActif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET3');">&nbsp;Permis & Habilitations&nbsp;</span>&nbsp;&nbsp;
			<% }else {%>
				<span id="titreOngletPermis" class="OngletInactif" onclick="executeBouton('<%=process.getNOM_PB_RESET()%>');afficheOnglet('ONGLET3');">&nbsp;Permis & Habilitations&nbsp;</span>&nbsp;&nbsp;
			<% } %>
		</div>
		<% if (process.onglet.equals("ONGLET1")) {%>
			<div id="corpsOngletDiplome" title="Diplomes" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletDiplome" title=""Diplomes"" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des diplômes d'un agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:65px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DIPLOME()%>"></span>
				    <span style="position:relative;width:400px;text-align: left;">Titre</span>
					<span style="position:relative;width:300px;text-align: left;">Spécialité</span>
					<span style="position:relative;width:100px;text-align: left;">Niveau</span>
					<span style="position:relative;text-align: left;">Nb docs</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceDiplome = 0;
							if (process.getListeDiplomesAgent()!=null){
								for (int i = 0;i<process.getListeDiplomesAgent().size();i++){
							%>
									<tr id="<%=indiceDiplome%>" onmouseover="SelectLigne(<%=indiceDiplome%>,<%=process.getListeDiplomesAgent().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DIPLOME(indiceDiplome)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_DIPLOME(indiceDiplome)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DIPLOME(indiceDiplome)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DIPLOME(indiceDiplome)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:400px;text-align: left;"><%=process.getVAL_ST_TITRE_DIPLOME(indiceDiplome)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: left;"><%=process.getVAL_ST_SPE_DIPLOME(indiceDiplome)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: left;">&nbsp;<%=process.getVAL_ST_NIVEAU(indiceDiplome)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_NB_DOC_DIPLOME(indiceDiplome)%></td>
									</tr>
									<%
									indiceDiplome++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
	<%if (! "".equals(process.getVAL_ST_ACTION_DIPLOME()) ) {%>
		<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_TITRE_DIPLOME()%>" value="go">
	<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_DIPLOME()%></legend>
		<%if(!process.getVAL_ST_ACTION_DIPLOME().equals(process.ACTION_SUPPRESSION_DIPLOME) && !process.getVAL_ST_ACTION_DIPLOME().equals(process.ACTION_CONSULTATION_DIPLOME)){ %>
		<div>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Titre du diplôme : </span>
			<span>
				<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_TITRE_DIPLOME() %>" onchange='executeBouton("<%=process.getNOM_PB_TITRE_DIPLOME()%>")' style="width : 350px;">
				<%=process.forComboHTML(process.getVAL_LB_TITRE_DIPLOME(), process.getVAL_LB_TITRE_DIPLOME_SELECT()) %>
				</SELECT>
			</span>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:65px;">Niveau : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_NIVEAU()%></span>
			<BR/><BR/>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Specialite du diplôme : </span>
			<span>
				<SELECT class="sigp2-liste" name="<%= process.getNOM_LB_SPECIALITE_DIPLOME() %>" style="width : 530px;">
				<%=process.forComboHTML(process.getVAL_LB_SPECIALITE_DIPLOME(), process.getVAL_LB_SPECIALITE_DIPLOME_SELECT()) %>
				</SELECT>
			</span>
			<BR/><BR/>
			<span class="sigp2" style="margin-left:20px;position:relative;width:150px;">Nom de l'école : </span>
			<span>
				<INPUT id="listeEcoles" class="sigp2-saisiemajusculenongras" maxlength="100" name="<%= process.getNOM_EF_NOM_ECOLE() %>" size="100"
					type="text" value="<%= process.getVAL_EF_NOM_ECOLE() %>">
			</span>
			<BR/><BR/>
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Date d'obtention : </span>
		    <span>
				<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_OBTENTION_DIPLOME() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_OBTENTION_DIPLOME() %>">
				<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_OBTENTION_DIPLOME() %>', 'dd/mm/y');">
			</span>
			<BR/><BR/>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents du diplôme</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;">	
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC_DIPLOME()%>">
					</span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
					<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
					<div>
					    <FONT color='red'>Veuillez valider votre choix.</FONT>
					    <BR/><BR/>
						<span class="sigp2" style="width:130px;">Nom du document : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Date : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Commentaire : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_DOC()%></span>
						<BR/>		
					</div>
					<% }else if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)){ %>
						<div>		
							<span class="sigp2" style="width:130px;" >Commentaire :</span><INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
							<BR/>
							<BR/>
							<span class="sigp2Mandatory" style="width:130px;">Fichier : </span> 
							<% if(process.fichierUpload == null){ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							<%}else{ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							<% }%>
							<br />
						</div>
					<%}%>
					</FIELDSET>
		</div>
		<%}else{ %>
		<div>
			<% if (process.getVAL_ST_ACTION_DIPLOME().equals(process.ACTION_SUPPRESSION_DIPLOME)) { %>
		  	 	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
			<span class="sigp2" style="width:150px">Titre du diplôme : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_TITRE()%></span>
			<BR/>
			<BR/>
			<span class="sigp2" style="width:150px">Spécialité du diplôme : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_SPECIALITE_DIPLOME()%></span>
			<BR/>
			<BR/>
			<span class="sigp2" style="width:150px">Niveau : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_NIVEAU()%></span>
			<BR/>
			<BR/>
			<span class="sigp2" style="width:150px">Nom de l'école : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_NOM_ECOLE()%></span>
			<BR/>
			<BR/>
			<span class="sigp2" style="width:150px">Date d'obtention : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_OBTENTION_DIPLOME()%></span>
				<BR/><BR/>				
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents du diplôme</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;"></span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
				</FIELDSET>
		</div>
		<BR/>
		<%} %>
		<% if (!process.getVAL_ST_ACTION_DIPLOME().equals(process.ACTION_CONSULTATION_DIPLOME)) { %>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DIPLOME()%>">
		<%} %>
		<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_DIPLOME()%>">
        <BR>
	</FIELDSET>
	<%}%>
		</div>
	<% if (process.onglet.equals("ONGLET2")) {%>
			<div id="corpsOngletFormation" title="Formations" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletFormation" title="Formations" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des formations d'un agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:65px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_FORMATION()%>"></span>
				    <span style="position:relative;width:600px;text-align: left;">Titre de la formation</span>
					<span style="position:relative;width:70px;text-align: center;">Année</span>
					<span style="position:relative;text-align: left;">Nb docs</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceFormation = 0;
							if (process.getListeFormationsAgent()!=null){
								for (int i = 0;i<process.getListeFormationsAgent().size();i++){
							%>
									<tr id="<%=indiceFormation%>" onmouseover="SelectLigne(<%=indiceFormation%>,<%=process.getListeFormationsAgent().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_FORMATION(indiceFormation)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_FORMATION(indiceFormation)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_FORMATION(indiceFormation)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_FORMATION(indiceFormation)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:600px;text-align: left;"><%=process.getVAL_ST_FORMATION(indiceFormation)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: center;"><%=process.getVAL_ST_ANNEE(indiceFormation)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_NB_DOC_FORMATION(indiceFormation)%></td>
									</tr>
									<%
									indiceFormation++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
				
		<%if (! "".equals(process.getVAL_ST_ACTION_FORMATION()) ) {%>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_FORMATION()%></legend>
				<%if(!process.getVAL_ST_ACTION_FORMATION().equals(process.ACTION_SUPPRESSION_FORMATION) && !process.getVAL_ST_ACTION_FORMATION().equals(process.ACTION_CONSULTATION_FORMATION)){ %>
				<div>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Titre de la formation : </span>
					<span>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TITRE_FORMATION() %>"  style="width : 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TITRE_FORMATION(), process.getVAL_LB_TITRE_FORMATION_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Centre de formation : </span>
					<span>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CENTRE_FORMATION() %>"  style="width : 350px;">
						<%=process.forComboHTML(process.getVAL_LB_CENTRE_FORMATION(), process.getVAL_LB_CENTRE_FORMATION_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Durée formation : </span>
					<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_DUREE_FORMATION() %>" size="10" type="text"  value="<%= process.getVAL_ST_DUREE_FORMATION() %>">
					<span style="margin-left:20px;">
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_UNITE_DUREE() %>"  style="width : 70px;">
						<%=process.forComboHTML(process.getVAL_LB_UNITE_DUREE(), process.getVAL_LB_UNITE_DUREE_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Année formation : </span>
					<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_ANNEE_FORMATION() %>" size="10" type="text"  value="<%= process.getVAL_ST_ANNEE_FORMATION() %>">
					<BR/><BR/>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la formation</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;">	
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC_FORMATION()%>">
					</span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
					<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
					<div>
					    <FONT color='red'>Veuillez valider votre choix.</FONT>
					    <BR/><BR/>
						<span class="sigp2" style="width:130px;">Nom du document : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Date : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Commentaire : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_DOC()%></span>
						<BR/>		
					</div>
					<% }else if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)){ %>
						<div>		
							<span class="sigp2" style="width:130px;" >Commentaire :</span><INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
							<BR/>
							<BR/>
							<span class="sigp2Mandatory" style="width:130px;">Fichier : </span> 
							<% if(process.fichierUpload == null){ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							<%}else{ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							<% }%>
							<br />
						</div>
					<%}%>
					</FIELDSET>
				</div>
				<%}else{ %>
				<div>
					<% if (process.getVAL_ST_ACTION_FORMATION().equals(process.ACTION_SUPPRESSION_FORMATION)) { %>
				  	 	<FONT color='red'>Veuillez valider votre choix.</FONT>
				    	<BR/><BR/>
				    <% } %>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Titre de la formation : </span>
					<span>
						<SELECT disabled="disabled" class="sigp2-liste" name="<%= process.getNOM_LB_TITRE_FORMATION() %>"  style="width : 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TITRE_FORMATION(), process.getVAL_LB_TITRE_FORMATION_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Centre de formation : </span>
					<span>
						<SELECT disabled="disabled" class="sigp2-liste" name="<%= process.getNOM_LB_CENTRE_FORMATION() %>"  style="width : 350px;">
						<%=process.forComboHTML(process.getVAL_LB_CENTRE_FORMATION(), process.getVAL_LB_CENTRE_FORMATION_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Durée formation : </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_DUREE_FORMATION()%></span>
					<span style="margin-left:20px;">
						<SELECT disabled="disabled" class="sigp2-liste" name="<%= process.getNOM_LB_UNITE_DUREE() %>"  style="width : 70px;">
						<%=process.forComboHTML(process.getVAL_LB_UNITE_DUREE(), process.getVAL_LB_UNITE_DUREE_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Année formation : </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_ANNEE_FORMATION()%></span>
				<BR/><BR/>				
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la formation</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;"></span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
				</FIELDSET>
				</div>
				<BR/>
				<%} %>
				<% if (!process.getVAL_ST_ACTION_FORMATION().equals(process.ACTION_CONSULTATION_FORMATION)) { %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_FORMATION()%>">
				<%} %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_FORMATION()%>">
		        <BR>
			</FIELDSET>
			<%}%>
		</div>
		<% if (process.onglet.equals("ONGLET3")) {%>
			<div id="corpsOngletPermis" title="Permis & Habilitations" class="OngletCorps" style="display:block;margin-right:10px;width:1030px;">
		<% }else {%>
			<div id="corpsOngletPermis" title="Permis & Habilitations" class="OngletCorps" style="display:none;margin-right:10px;width:1030px;">
		<% } %>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Gestion des permis et habilitations d'un agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:60px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_PERMIS()%>"></span>
				    <span style="position:relative;width:600px;text-align: left;">Titre</span>
				    <span style="position:relative;width:105px;text-align: center;">Limite de validité</span>
					<span style="position:relative;text-align: left;">Nb docs</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indicePermis = 0;
							if (process.getListePermisAgent()!=null){
								for (int i = 0;i<process.getListePermisAgent().size();i++){
							%>
									<tr id="<%=indicePermis%>" onmouseover="SelectLigne(<%=indicePermis%>,<%=process.getListePermisAgent().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:65px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_PERMIS(indicePermis)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER_PERMIS(indicePermis)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_PERMIS(indicePermis)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_PERMIS(indicePermis)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:600px;text-align: left;"><%=process.getVAL_ST_PERMIS(indicePermis)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: center;"><%=process.getVAL_ST_LIMITE_PERMIS(indicePermis)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_NB_DOC_PERMIS(indicePermis)%></td>
									</tr>
									<%
									indicePermis++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
				
		<%if (! "".equals(process.getVAL_ST_ACTION_PERMIS()) ) {%>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION_PERMIS()%></legend>
				<%if(!process.getVAL_ST_ACTION_PERMIS().equals(process.ACTION_SUPPRESSION_PERMIS) && !process.getVAL_ST_ACTION_PERMIS().equals(process.ACTION_CONSULTATION_PERMIS)){ %>
				<div>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Titre : </span>
					<span>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TITRE_PERMIS() %>"  style="width : 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TITRE_PERMIS(), process.getVAL_LB_TITRE_PERMIS_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Durée de validité : </span>
					<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_DUREE_PERMIS() %>" size="10" type="text"  value="<%= process.getVAL_ST_DUREE_PERMIS() %>">
					<span style="margin-left:20px;">
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_UNITE_DUREE() %>"  style="width : 70px;">
						<%=process.forComboHTML(process.getVAL_LB_UNITE_DUREE(), process.getVAL_LB_UNITE_DUREE_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Date d'obtention : </span>
		    		<span>
						<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_OBTENTION_PERMIS() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_OBTENTION_PERMIS() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%= process.getNOM_EF_DATE_OBTENTION_PERMIS() %>', 'dd/mm/y');">
					</span>
					<BR/><BR/>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents du permis</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;">	
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC_PERMIS()%>">
					</span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
					<% if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
					<div>
					    <FONT color='red'>Veuillez valider votre choix.</FONT>
					    <BR/><BR/>
						<span class="sigp2" style="width:130px;">Nom du document : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Date : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DOC()%></span>
						<BR/>
						<span class="sigp2" style="width:130px;">Commentaire : </span>
						<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_DOC()%></span>
						<BR/>		
					</div>
					<% }else if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)){ %>
						<div>		
							<span class="sigp2" style="width:130px;" >Commentaire :</span><INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
							<BR/>
							<BR/>
							<span class="sigp2Mandatory" style="width:130px;">Fichier : </span> 
							<% if(process.fichierUpload == null){ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							<%}else{ %>
							<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
							<% }%>
							<br />
						</div>
					<%}%>
					</FIELDSET>
				</div>
				<%}else{ %>
				<div>
					<% if (process.getVAL_ST_ACTION_PERMIS().equals(process.ACTION_SUPPRESSION_PERMIS)) { %>
				  	 	<FONT color='red'>Veuillez valider votre choix.</FONT>
				    	<BR/><BR/>
				    <% } %>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Titre : </span>
					<span>
						<SELECT disabled="disabled" class="sigp2-liste" name="<%= process.getNOM_LB_TITRE_PERMIS() %>"  style="width : 350px;">
						<%=process.forComboHTML(process.getVAL_LB_TITRE_PERMIS(), process.getVAL_LB_TITRE_PERMIS_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Durée de validité : </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_DUREE_PERMIS()%></span>
					<span style="margin-left:20px;">
						<SELECT disabled="disabled" class="sigp2-liste" name="<%= process.getNOM_LB_UNITE_DUREE() %>"  style="width : 70px;">
						<%=process.forComboHTML(process.getVAL_LB_UNITE_DUREE(), process.getVAL_LB_UNITE_DUREE_SELECT()) %>
						</SELECT>
					</span>
					<BR/><BR/>
					<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:150px;">Date d'obtention : </span>
					<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_OBTENTION_PERMIS()%></span>
				<BR/><BR/>				
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents du diplôme</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;"></span>
					<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
					<span style="position:relative;width:120px;text-align: center;">Date</span> 
					<span style="position:relative;text-align: left">Commentaire</span> 
				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
									</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
							</tr>
						<%
							indiceActes++;
							}
						}
						%>
						</table>	
					</div>	
				</FIELDSET>
				</div>
				<BR/>
				<%} %>
				<% if (!process.getVAL_ST_ACTION_PERMIS().equals(process.ACTION_CONSULTATION_PERMIS)) { %>
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_PERMIS()%>">
				<%} %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_PERMIS()%>">
		        <BR>
			</FIELDSET>
			<%}%>
		</div>
	<%=process.getUrlFichier()%>
	<INPUT type="submit" style="display:none;"  name="<%=process.getNOM_PB_RESET()%>" value="reset">	
	</FORM>
<%} %>
</BODY>
</HTML>