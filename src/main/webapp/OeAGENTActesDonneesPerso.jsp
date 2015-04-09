<%@ page contentType="text/html; charset=UTF-8" %> 
<!-- Sample JSP file -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR"
	content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css"
	type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des contrats d'un agent</TITLE>

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
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTActesDonneesPerso" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>')">
<%@ include file="BanniereErreur.jsp"%>
<%if(process.getAgentCourant() !=null){ %>
<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">

<INPUT name="JSP" type="hidden"	value="<%= process.getJSP() %>" >

<FIELDSET class="sigp2Fieldset"	style="text-align:left;margin-right:10px;width:1030px;">
	<legend	class="sigp2Legend">Liste des documents d'un agent</legend> 
	<br />
	<span style="text-align:center">
	<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_VUE(),process.getNOM_RB_VUE_AUTRE())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_VUE() %>")'>Autres documents
	<span style="width:10px"></span>
	<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_VUE(),process.getNOM_RB_VUE_SAUVEGARDE())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGER_VUE() %>")'>Sauvegarde FP
	</span>
	<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGER_VUE()%>" value="OK">
	<BR/>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td>
									<%if (process.getVueCourant()!=null && !process.getVueCourant().equals("Sauvegarde")) {%>
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
									<%}else{ %>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<%} %>
								</td>
								<td>Nom du document</td>
								<td>Nom original</td>
								<td align="center">Type</td>
								<td align="center">Date</td>
								<td>Commentaire</td>
							</tr>
							<%
							int indiceActes = 0;
							if (process.getListeDocuments()!=null){
								for (int i = 0;i<process.getListeDocuments().size();i++){
							%>
									<tr id="<%=indiceActes%>" onmouseover="SelectLigne(<%=indiceActes%>,<%=process.getListeDocuments().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceActes)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceActes)%>">	
										<%if (process.getVueCourant()!=null && !process.getVueCourant().equals("Sauvegarde")) {%>			
											<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceActes)%>">
										<%} %>
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_ORI_DOC(indiceActes)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:120px;text-align: center;"><%=process.getVAL_ST_TYPE_DOC(indiceActes)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
									</tr>
									<%
									indiceActes++;
								}
							}%>
						</table>	
					</div>	
</FIELDSET>
<BR/>
<BR/>
<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
	<FIELDSET class="sigp2Fieldset" style="text-align:left;margin:10px;width:1030px;">
		<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
		<BR/>
		<% if(process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION)){ %>
		<div>
		    <FONT color='red'>Veuillez valider votre choix.</FONT>
		    <BR/><BR/>
		    <table>
		    	<tr>
		    		<td width="130px;">
						<span class="sigp2">Nom du document : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_DOC()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td width="130px;">
						<span class="sigp2">Nom original : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_ORI_DOC()%></span>
		    		</td>
		    	</tr>
		    	<tr>
		    		<td>
						<span class="sigp2">Type de document : </span>
		    		</td>
		    		<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE_DOC()%></span>
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
			<BR/>
			<BR/>
			<BR/>
			<BR/>		
		</div>
		<BR/>
		<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
			<TBODY>
				<TR>
					<TD width="31"><INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_SUPPRESSION()%>"></TD>
					<TD height="18" width="15"></TD>
					<TD class="sigp2" style="text-align : center;" height="18" width="23"><INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>"></TD>
				</TR>
			</TBODY>
		</TABLE>
        <BR>
		<% } else { %>
			<div>
			<table>
				<tr>
					<td width="130px;">
						<span class="sigp2Mandatory" > Type de document : </span>
					</td>
					<td>
						<SELECT onchange='executeBouton("<%=process.getNOM_PB_TYPE_DOCUMENT() %>")' class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_DOCUMENT() %>"> 
							<%=process.forComboHTML(process.getVAL_LB_TYPE_DOCUMENT(), process.getVAL_LB_TYPE_DOCUMENT_SELECT())%>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<% if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("CONTRAT")){ %>
							<span class="sigp2Mandatory" > Choix du contrat : </span>
						<%}else if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("FICHE DE POSTE")){ %>
							<span class="sigp2Mandatory" > Choix de la fiche de poste : </span>
						<%}else if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("NOTE DE SERVICE")){ %>
							<span class="sigp2Mandatory" > Choix de l'affectation : </span>
						<%} %>
					</td>
					<td>
						<% if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("CONTRAT")){ %>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CONTRAT() %>"> 
								<%=process.forComboHTML(process.getVAL_LB_CONTRAT(), process.getVAL_LB_CONTRAT_SELECT())%>
							</SELECT>
						<%}else if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("FICHE DE POSTE")){ %>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FICHE_POSTE() %>"> 
								<%=process.forComboHTML(process.getVAL_LB_FICHE_POSTE(), process.getVAL_LB_FICHE_POSTE_SELECT())%>
							</SELECT>			
						<%}else if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("NOTE DE SERVICE")){ %>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_AFFECTATION() %>"> 
								<%=process.forComboHTML(process.getVAL_LB_AFFECTATION(), process.getVAL_LB_AFFECTATION_SELECT())%>
							</SELECT>
						<%} %>
					</td>
				</tr>
				<% if(process.getVAL_ST_CHOIX_TYPE_DOC().equals("NOTE DE SERVICE")){ %>
				<tr>
					<td>
						<span class="sigp2Mandatory" > Type du fichier : </span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_FICHIER_AFFECTATION() %>"> 
							<%=process.forComboHTML(process.getVAL_LB_TYPE_FICHIER_AFFECTATION(), process.getVAL_LB_TYPE_FICHIER_AFFECTATION_SELECT())%>
						</SELECT>
					</td>
				</tr>
				<%} %>
				<tr>
					<td>
						<span class="sigp2">Commentaire :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Fichier : </span> 
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
			<div style="text-align: center">
				<BR/><BR/>
				<span class="sigp2-saisie"><%=process.getVAL_ST_WARNING()%></span>
				<BR/><BR/>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
		<% } %>
	</FIELDSET>
	<% } %>
	<INPUT type="submit" style="visibility:hidden;" name="<%=process.getNOM_PB_TYPE_DOCUMENT()%>" >
	<%=process.getUrlFichier()%>
</FORM>
<%} %>
</BODY>
</HTML>
