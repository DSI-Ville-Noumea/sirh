<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="nc.mairie.metier.hsct.BeneficiaireObligationAmenage"%>

<HTML>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des handicaps</TITLE>
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
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTHandicap" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des handicaps de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF" valign="bottom">
								<td align="left">
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
								</td>
								<td align="left">Type</td>
								<td align="center">Début</td>
								<td align="center">Attribution</td>
								<td align="center">Fin</td>
								<td align="center">% incapacité</td>
								<td align="center">Amenagement</td>
								<td align="center">Origine</td>
								<td align="center">Nb docs</td>
							</tr>
							<%
							int indiceHandi = 0;
							if (process.getListeBoe()!=null){

								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								
								for (int i=0; i<process.getListeBoe().size(); i++){
									BeneficiaireObligationAmenage boe = process.getListeBoe().get(i); 
							%>
									<tr id="<%=indiceHandi%>" onmouseover="SelectLigne(<%=indiceHandi%>, <%=process.getListeBoe().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceHandi)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceHandi)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceHandi)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceHandi)%>">
											<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceHandi)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: center;"><%=boe.getType()%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><% if(null != boe.getDateDebut()) { %><%=sdf.format(boe.getDateDebut())%><% } %></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><% if(null != boe.getDateAttribution()) { %><%=sdf.format(boe.getDateAttribution())%><% } %></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:60px;text-align: center;"><% if(null != boe.getDateFin()) { %><%=sdf.format(boe.getDateFin())%><% } %></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><% if(null != boe.getTaux()) { %><%=boe.getTaux()%><% } %></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><% if(null != boe.getIdNaturePosteAmenage()) { %><%=process.getHashNaturePosteAmenage().get(boe.getIdNaturePosteAmenage()).getLibNaturePosteAmenage()%><% } %></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><% if(null != boe.getOrigineIpp()) { %><%=boe.getOrigineIpp()%><% } %></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;">&nbsp;<%=process.getVAL_ST_NB_DOC(indiceHandi)%></td>
									</tr>
									<%
									indiceHandi++;
								}
							} %>
						</table>	
						</div>	
				</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<table>
				<tr>
					<td width="130px;">
						<span class="sigp2Mandatory">Type :</span>
					</td>
					<td width="250px;">
						<input type="radio" onclick='executeBouton("<%=process.getNOM_PB_SELECT_TYPE()%>")'" <%= process.forRadioHTML(process.getNOM_RG_TYPE(), process.TYPE_TH)%> >
						<span class="sigp2Mandatory">TH</span> 
						<input type="radio" onclick='executeBouton("<%=process.getNOM_PB_SELECT_TYPE()%>")'" <%= process.forRadioHTML(process.getNOM_RG_TYPE(), process.TYPE_IPP)%> >
						<span class="sigp2Mandatory">IPP</span>
					</td>
				</tr>
				<% if(process.showTH) { %>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEBUT() %>" />
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');" />
					</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<% } %>
				<% if(process.showIPP) { %>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date attribution :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_ATTRIBUTION() %>" class="sigp2-saisie" maxlength="10"  
							name="<%=process.getNOM_EF_DATE_ATTRIBUTION() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_ATTRIBUTION() %>" />
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_ATTRIBUTION()%>', 'dd/mm/y');" />
					</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<% } %>
				<tr>
					<td>
						<span class="sigp2<% if(process.showTH) { %>Mandatory<% } %>">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN() %>" />
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');" />
					</td>
				</tr>
				<% if(process.showTH) { %>
				<tr>
					<td>
						<span class="sigp2">Poste aménagé :</span> 
					</td>
					<td>
						<input type="radio" <%= process.forRadioHTML(process.getNOM_RB_POSTE_AMENAGE(), process.MILIEU_ORDINAIRE) %> ><span class="sigp2Mandatory">Milieu ordinaire</span> 
						<input type="radio" <%= process.forRadioHTML(process.getNOM_RB_POSTE_AMENAGE(), process.MILIEU_PROTEGE) %> ><span class="sigp2Mandatory">Milieu protégé</span> 
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Précision :</span>
					</td>
					<td colspan="3">
						<textarea rows="3" style="width:600px" name="<%= process.getNOM_ST_COMMENTAIRE() %>"><%= process.getVAL_ST_COMMENTAIRE() %></textarea>
					</td>
				</tr>
				<% } %>
				<% if(process.showIPP) { %>
				<tr>
					<td>
						<span class="sigp2Mandatory">% Taux :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_ST_INCAPACITE() %>" size="5" type="text" value="<%=process.getVAL_ST_INCAPACITE() %>" />
					</td>
				</tr>
				<tr>
					<td width="130px;">
						<span class="sigp2">Suite à :</span>
					</td>
					<td width="350px;">
						<input type="radio" <%= process.forRadioHTML(process.getNOM_RB_ORIGINE(), process.ORIGINE_MP)%> >
						<span class="sigp2Mandatory">Maladie professionnelle</span> 
						<input type="radio" <%= process.forRadioHTML(process.getNOM_RB_ORIGINE(), process.ORIGINE_AT)%> >
						<span class="sigp2Mandatory">Accident Travail</span>
					</td>
				</tr>
				<% } %>
			</table>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
				<span style="position:relative;width:9px;"></span>
				<span style="position:relative;width:55px;">	
				<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
				</span>
				<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
				<span style="position:relative;width:230px;text-align: left: ;">Nom original</span> 
				<span style="position:relative;width:90px;text-align: center;">Type</span> 
				<span style="position:relative;width:120px;text-align: center;">Date</span> 
				<span style="position:relative;text-align: left">Commentaire</span> 
			
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
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
							<td class="sigp2NewTab-liste" style="position:relative;width:120px;text-align: center;"><%=process.getVAL_ST_TYPE_DOC(indiceActes)%></td>
							<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
							<td class="sigp2NewTab-liste" style="position:relative;text-align: left;">&nbsp;<%=process.getVAL_ST_COMMENTAIRE_DOCUMENT(indiceActes)%></td>
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
					<span class="sigp2" style="width:130px;">Nom original : </span>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_ORI_DOC()%></span>
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
						<span class="sigp2" style="width:130px;" >Commentaire :</span><INPUT class="sigp2-saisie" maxlength="100" name="<%=process.getNOM_EF_COMMENTAIRE_DOCUMENT() %>" size="100" type="text" value="<%= process.getVAL_ST_COMMENTAIRE() %>">
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
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<FONT color='red'>Veuillez valider votre choix.</FONT>
		    <BR/><BR/>
		    <% } %>
		    <span style="width:49%">
		    	<span class="sigp2" style="width:150px">Type : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE()%></span>
				<BR/>
				<BR/>
				<% if(process.showTH) { %>
				<span class="sigp2" style="width:150px">Date de début : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>
				<BR/>
				<BR/>
				<% } %>
				<% if(process.showIPP) { %>
				<span class="sigp2" style="width:150px">Date d'attribution : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_ATTRIBUTION()%></span>
				<BR/>
				<BR/>
				<% } %>
				<span class="sigp2" style="width:150px">Date de fin : </span>
				<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
				<BR/>
				<BR/>
				<% if(process.showTH) { %>
				<span class="sigp2" style="width:150px">Poste aménagé : </span>
				<span class="sigp2-saisie"><%=process.getVAL_RB_POSTE_AMENAGE()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">Nature du handicap : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE()%></span>
				<BR/>
				<BR/>
				<% } %>
				<% if(process.showIPP) { %>
				<span class="sigp2" style="width:150px">% Taux : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_INCAPACITE()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">Origine : </span>
				<span class="sigp2-saisie"><%=process.getVAL_RB_ORIGINE()%></span>
				<BR/>
				<BR/>
				<% } %>
			</span>
			</div>
			<%} %>
			<BR/>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
			<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
			<TBODY>
				<TR>
				<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
					<TD width="31"><INPUT type="submit"
						class="sigp2-Bouton-100" value="Valider"
						name="<%=process.getNOM_PB_VALIDER()%>"></TD>
					<TD height="18" width="15"></TD>
				<%} %>
					<TD class="sigp2" style="text-align : center;"
						height="18" width="23"><INPUT type="submit"
						class="sigp2-Bouton-100" value="Annuler"
						name="<%=process.getNOM_PB_ANNULER()%>"></TD>
				</TR>
			</TBODY>
		</TABLE>
		<%} %>
		</FIELDSET>
		<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SELECT_TYPE()%>" value="">
		</FORM>	
		<% } %>
	<%} %>
	</BODY>
</HTML>