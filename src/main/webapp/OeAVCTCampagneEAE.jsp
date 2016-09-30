<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.technique.Services"%>
<%@page import="nc.mairie.gestionagent.eae.dto.CampagneEaeDto"%>
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
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTCampagneEAE" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Liste des campagnes EAE</legend>
			<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<tr bgcolor="#EFEFEF">
							<td width="45px;">
								<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_AJOUTER()%>">
							</td>
							<td width="45px;" align="center">Docs joint</td>
							<td width="45px;" align="center">Année</td>
							<td width="90px;" align="center">Date début</td>
							<td width="90px;" align="center">Date fin</td>
							<td width="90px;" align="center">Date début Kiosque</td>
							<td width="90px;" align="center">Date fin Kiosque</td>
							<td width="110px;" align="center">Kiosque</td>
							<td width="110px;" align="center">Kiosque</td>
							<td>Cloturer</td>
						</tr>
						<%
						int indiceCamp = 0;
						if (process.getListeCampagne()!=null){
							for (int i = 0;i<process.getListeCampagne().size();i++){
								CampagneEaeDto camp = process.getListeCampagne().get(i);
						%>
							<tr id="<%=indiceCamp%>" onmouseover="SelectLigne(<%=indiceCamp%>,<%=process.getListeCampagne().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:40px;" align="left">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceCamp)%>">
									<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceCamp)%>">				
									<%if(camp.estOuverte()){%>
										<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceCamp)%>">
				    				<%} %>
									</td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_ANNEE(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_DEBUT_KIOSQUE(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_FIN_KIOSQUE(indiceCamp)%></td>
								<td class="sigp2NewTab-liste" style="text-align: center;">
									<INPUT <%= process.peutOuvrirKiosque(indiceCamp) ? "" : "disabled='disabled'" %> type="submit" class="sigp2-Bouton-100" value="Ouvrir" name="<%=process.getNOM_PB_OUVRIR_KIOSQUE(indiceCamp)%>">
								</td>
								<td class="sigp2NewTab-liste" style="text-align: center;">
									<INPUT <%= process.peutFermerKiosque(indiceCamp) ? "" : "disabled='disabled'" %> type="submit" class="sigp2-Bouton-100" value="Fermer" name="<%=process.getNOM_PB_FERMER_KIOSQUE(indiceCamp)%>">
								</td>
								<td class="sigp2NewTab-liste" style="text-align: left;">
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
			<table>
				<tr>
					<td width="70px">
						<span class="sigp2Mandatory">Année :</span>
					</td>
					<td>
						<INPUT <%=process.getVAL_ST_ACTION().equals(process.ACTION_MODIFICATION) ? "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="4" name="<%= process.getNOM_ST_ANNEE() %>" size="4" type="text" value="<%= process.getVAL_ST_ANNEE() %>">
					</td>
				</tr>
				
				<%if(process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)|| process.dateDebutModifiable){%>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEBUT() %>">
						<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
					</td>
				</tr>
				<%} %>
				<tr>
					<td>
						<span class="sigp2">Commentaire :</span>
					</td>
					<td>
						<textarea rows="4" cols="150" class="sigp2-saisie" name="<%= process.getNOM_ST_COMMENTAIRE()%>" ><%= process.getVAL_ST_COMMENTAIRE() %></textarea>
					</td>
				</tr>
			</table>
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la campagne EAE</legend>				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
							<tr bgcolor="#EFEFEF">
								<td><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>"></td>
								<td>Nom du document</td>
								<td>Nom original</td>
								<td align="center">Date</td>
								<td>Commentaire</td>
							</tr>
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<a href="<%=process.getVAL_ST_URL_DOC(indiceActes)%>" title="Consulter le document" target="_blank" >
										<img onkeydown="" onkeypress="" onkeyup="" src="images/oeil.gif" height="16px" width="16px" title="Voir le document" />
									</a>
									<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_ORI_DOC(indiceActes)%></td>
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
						<table>
							<tr>
								<td width="100px;">
									<span class="sigp2">Nom du document : </span>
								</td>
								<td>
									<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
								</td>
							</tr>
							<tr>
								<td width="100px;">
									<span class="sigp2">Nom original : </span>
								</td>
								<td>
									<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_ORI_DOC()%></span>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2">Date : </span>
								</td>
								<td>
									<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DOC()%></span>
								</td>
							</tr>
							<tr>
								<td>
									<span class="sigp2">Commentaire : </span>
								</td>
								<td>
									<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE_DOC()%></span>
								</td>
							</tr>
						</table>	
					</div>
					<% }else if(process.getVAL_ST_ACTION_DOCUMENT().equals(process.ACTION_DOCUMENT_CREATION)){ %>
						<div>		
							<table>
								<tr>
									<td width="100px;">
										<span class="sigp2">Commentaire :</span>
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
									</td>
								</tr>
								<tr>
									<td>
										<span class="sigp2">Fichier : </span>
									</td>
									<td>
										<% if(process.fichierUpload == null){ %>
										<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
										<%}else{ %>
										<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT() %>" >
										<% }%>
									</td>
								</tr>
							</table>
						</div>
					<%}%>
					</FIELDSET>
				</div>
			
			<% } else { %>
			<div>
			<table>
				<tr>
					<td width="70px;">
		    			<span class="sigp2">Année : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_ANNEE()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date début: </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DEBUT()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date fin: </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_FIN()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Commentaire: </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE()%></span>
					</td>
				</tr>
			</table>			
				<FIELDSET class="sigp2Fieldset" style="text-align: left; width:930px;">
				<legend class="sigp2Legend">Liste des documents de la campagne EAE</legend>				
					<div style="overflow: auto;height: 150px;width:900px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:880px;">
							<tr bgcolor="#EFEFEF">
								<td>&nbsp;</td>
								<td>Nom du document</td>
								<td>Nom original</td>
								<td align="center">Date</td>
								<td>Commentaire</td>
							</tr>
						<%
						int indiceActes = 0;
						if (process.getListeDocuments()!=null){
							for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
							<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
								<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
									<a href="<%=process.getVAL_ST_URL_DOC(indiceActes)%>" title="Consulter le document" target="_blank" >
										<img onkeydown="" onkeypress="" onkeyup="" src="images/oeil.gif" height="16px" width="16px" title="Voir le document" />
									</a>	
								</td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_ORI_DOC(indiceActes)%></td>
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
	</FORM>
</BODY>
</HTML>