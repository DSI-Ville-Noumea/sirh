<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.technique.Services"%>
<%@page import="nc.mairie.spring.domain.metier.EAE.CampagneEAE"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des campagnes EAE</TITLE>
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 
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
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTCampagneEAE" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Liste des campagnes EAE</legend>
			<br/>
			<span style="position:relative;width:9px;"></span>
			<span style="position:relative;width:40px;">
				<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
			</span>
			<span style="position:relative;width:45px;text-align: center;">Docs joint</span>
			<span style="position:relative;width:45px;text-align: center;">Année</span>
			<span style="position:relative;width:90px;text-align: center;">Date début</span>
			<span style="position:relative;width:90px;text-align: center;">Date fin</span>
			<span style="position:relative;width:90px;text-align: center;">Date début Kiosque</span>
			<span style="position:relative;width:90px;text-align: center;">Date fin Kiosque</span>
			<span style="position:relative;width:120px;text-align: center;">Kiosque</span>
			<span style="position:relative;width:120px;text-align: center;">Kiosque</span>
			<span style="position:relative;text-align: left;">Cloturer</span>
			<br/>
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<%
						int indiceCamp = 0;
						if (process.getListeCampagne()!=null){
							for (int i = 0;i<process.getListeCampagne().size();i++){
								CampagneEAE camp = process.getListeCampagne().get(i);
						%>
							<tr id="<%=indiceCamp%>" onmouseover="SelectLigne(<%=indiceCamp%>,<%=process.getListeCampagne().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="left">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceCamp)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceCamp)%>">				
									<%if(camp.estOuverte()){%>
										<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceCamp)%>">
				    				<%} %>
									</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:45px;text-align: center;"><%=process.getVAL_ST_ANNEE(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT_KIOSQUE(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_FIN_KIOSQUE(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:110px;text-align: center;">
									<INPUT <%= process.peutOuvrirKiosque(indiceCamp) ? "" : "disabled='disabled'" %> type="submit" class="sigp2-Bouton-100" value="Ouvrir" name="<%=process.getNOM_PB_OUVRIR_KIOSQUE(indiceCamp)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:110px;text-align: center;">
									<INPUT <%= process.peutFermerKiosque(indiceCamp) ? "" : "disabled='disabled'" %> type="submit" class="sigp2-Bouton-100" value="Fermer" name="<%=process.getNOM_PB_FERMER_KIOSQUE(indiceCamp)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">
									<INPUT <%= process.peutCloturerCampagne(indiceCamp) ? "" : "disabled='disabled'" %> type="submit" class="sigp2-Bouton-100" value="Cloturer" name="<%=process.getNOM_PB_CLOTURER_CAMPAGNE(indiceCamp)%>">
								</td>
							</tr>
							<%
							indiceCamp++;
							}
						}%>
				</table>	
			</div>	
			<BR/><BR/>	
		</FIELDSET>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align: left; width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION)){ %>
			<div>	
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:70px;">Année :</span>
				<INPUT <%=process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION) ? "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_ANNEE() %>" size="4" type="text" value="<%= process.getVAL_ST_ANNEE() %>">
				
				<BR/><BR/>


				<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)|| process.dateDebutModifiable){%>
				
				<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:70px;">Date début :</span>
				<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEBUT() %>">
				<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
				
				<BR/><BR/>
				
				<%} %>
				
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Commentaire :</span>
				<textarea rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_COMMENTAIRE()%>" ><%= process.getVAL_ST_COMMENTAIRE() %></textarea>
					
				<BR/><BR/>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la campagne EAE</legend>
					<span style="position:relative;width:9px;"></span>
					<span style="position:relative;width:55px;">	
					<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
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
			
			<% } else { %>
			<div>
		    	<BR/>
		    	<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Année : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_ANNEE()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Date début: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DEBUT()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Date fin: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_FIN()%></span>
				<BR/><BR/>
				<span class="sigp2" style="margin-left:20px;position:relative;width:70px;">Commentaire: </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE()%></span>
				<BR/><BR/>				
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la campagne EAE</legend>
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
			<%} %>
			<BR/>
			<div style="width:100%; text-align:center;">
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_VISUALISATION)){ %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
			<%} %>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
		</FIELDSET>
	<%} %>
	<%=process.getUrlFichier()%>
	</FORM>
</BODY>
</HTML>