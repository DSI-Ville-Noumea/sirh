<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des accidents du travail</TITLE>
		<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
		<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> <SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		
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
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAccidentTravail" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des accidents du travail de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF"  valign="bottom">
								<td align="left">
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
								</td>
								<td align="center">Date</td>
								<td align="center">Date de rechute</td>
								<td align="center">Nbr jour(s) IIT</td>
								<td align="left">Type</td>
								<td align="left">Siège des lésions</td>
								<td align="center">Nb docs</td>
							</tr>
							<%
							int indiceAcc = 0;
							if (process.getListeAT()!=null){
								for (int i = 0;i<process.getListeAT().size();i++){
							%>
									<tr id="<%=indiceAcc%>" onmouseover="SelectLigne(<%=indiceAcc%>,<%=process.getListeAT().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAcc)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAcc)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAcc)%>">
											<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceAcc)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_RECHUTE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: right;"><%=process.getVAL_ST_NB_JOURS(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: left;"><%=process.getVAL_ST_TYPE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:320px;text-align: left;"><%=process.getVAL_ST_SIEGE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceAcc)%></td>
									</tr>
									<%
									indiceAcc++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION) ){ %>
			<table>
				<tr>
					<td width="150px;">
						<span class="sigp2Mandatory">Date d'AT :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date de rechute :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_INITIALE()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_INITIALE() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_INITIALE() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_INITIALE()%>', 'dd/mm/y');">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Durée ITT (jours) :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_NB_JOUR_IIT() %>" size="5" type="text" value="<%= process.getVAL_EF_NB_JOUR_IIT() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Type :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE(), process.getVAL_LB_TYPE_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Siège des lésions :</span> 
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_SIEGE_LESION() %>">
							<%=process.forComboHTML(process.getVAL_LB_SIEGE_LESION(), process.getVAL_LB_SIEGE_LESION_SELECT()) %>
						</SELECT>
					</td>
				</tr>
			</table>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>			
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<tr bgcolor="#EFEFEF">
							<td>
								<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
							</td>
							<td width="230px;">Nom du document</td>
							<td width="90px;" align="center">Type</td>
							<td width="120px;" align="center">Date</td>
							<td>Commentaire</td>
						</tr>
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
							<td align="center" class="sigp2NewTab-liste" style="position:relative;width:120px;"><%=process.getVAL_ST_TYPE_DOC(indiceActes)%></td>
							<td align="center" class="sigp2NewTab-liste" style="position:relative;width:90px;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
							<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE(indiceActes)%></td>
						</tr>
					<%
						indiceActes++;
						}
					}
					%>
					</table>	
				</div>	
				
				<% if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
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
				<BR/>
				<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
					<TBODY>
						<TR>
							<TD width="31"><INPUT type="submit"
								class="sigp2-Bouton-100" value="Valider"
								name="<%=process.getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION()%>"></TD>
							<TD height="18" width="15"></TD>
							<TD class="sigp2" style="text-align : center;"
								height="18" width="23"><INPUT type="submit"
								class="sigp2-Bouton-100" value="Annuler"
								name="<%=process.getNOM_PB_ANNULER()%>"></TD>
						</TR>
					</TBODY>
				</TABLE>
		        <BR>
				<% }else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION)){ %>
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
					<div style="text-align: center">
						<BR/><BR/>
						<span class="sigp2-saisie"><%=process.getVAL_ST_WARNING()%></span>
						<BR/><BR/>
						<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_DOCUMENT_CREATION()%>">
						<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
					</div>
				<%}else{ %>
					<div style="text-align: center">
						<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
					</div>
				<%} %>
			<% } else{ %>
			<div>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION) ){ %>
		    	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
		    <span class="sigp2" style="width:150px">Date : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE()%></span>
			<BR/>
		    <span class="sigp2" style="width:150px">Date de rechute : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_INITIALE()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Durée ITT(jours) : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_NB_JOUR_IIT()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Type : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Siège des lésions : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_SIEGE_LESION()%></span>
		</div>
		<%} %>
			<BR/>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
			<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION) ){ %>
							<TD width="31"><INPUT type="submit"
								class="sigp2-Bouton-100" value="Valider"
								name="<%=process.getNOM_PB_VALIDER()%>"></TD>
							<TD height="18" width="15"></TD>
						<% } %>
						<TD class="sigp2" style="text-align : center;"
							height="18" width="23"><INPUT type="submit"
							class="sigp2-Bouton-100" value="Annuler"
							name="<%=process.getNOM_PB_ANNULER()%>"></TD>
					</TR>
				</TBODY>
			</TABLE>
			<%} %>
		</FIELDSET>
	<%=process.getUrlFichier()%>
	</FORM>
		
		<% } %>
<%} %>
	</BODY>
</HTML>