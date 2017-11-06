<%@page import="java.io.File"%>
<%@page import="nc.mairie.gestionagent.absence.dto.TypeAbsenceDto"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.enums.EnumTypeAbsence"%>
<%@page import="nc.mairie.gestionagent.absence.dto.PieceJointeDto"%>

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
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAccidentTravail" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des AT et MP de l'agent</legend>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF"  valign="bottom">
					    		<td align="left">
					    			<INPUT style="margin-left: 5px;" title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
					    		</td>
								<td align="center">AT / MP</td>
								<td align="center">Date de l'accident du travail</td>
								<td align="center">Date déclaration</td>
								<td align="center">Date début</td>
								<td align="center">Date fin</td>
								<td align="center">Rechute</td>
								<td align="center">Nbr jour(s) IIT</td>
								<td align="left">Type</td>
								<td align="left">Siège des lésions</td>
								<td align="center">Nb docs</td>
							</tr>
							<%
							int indiceAcc = 0;
							if (process.getListeAT_MP()!=null){
								for (int i = 0;i<process.getListeAT_MP().size();i++){
							%>
									<tr id="<%=indiceAcc%>" onmouseover="SelectLigne(<%=indiceAcc%>,<%=process.getListeAT_MP().size() %>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAcc)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAcc)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAcc)%>">
											<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceAcc)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: center;"><%=process.getVAL_ST_AT_MP(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_ACCIDENT_TRAVAIL(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE_FIN(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: center;"><%=process.getVAL_ST_RECHUTE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: right;"><%=process.getVAL_ST_NB_JOURS(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_TYPE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:170px;text-align: left;"><%=process.getVAL_ST_SIEGE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceAcc)%></td>
									</tr>
									<%
									indiceAcc++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) 
				&& !process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)
				&& !process.getVAL_ST_ACTION().equals(process.ACTION_CREATION_DOCUMENT_CREATION)) {%>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%> <%=process.getVAL_ST_TYPE_DEMANDE()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION)
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_CREATION) ){ %>
			<table>
				<% if(process.getDemandeCourant().getTypeSaisi().isDateAccidentTravail()) { %>
				<tr>
					<td width="155px;">
						<span class="sigp2Mandatory">Date de l'accident du travail :</span>
					</td>
					<td width="320px;">
						<input id="<%=process.getNOM_EF_DATE_ACCIDENT_TRAVAIL()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_ACCIDENT_TRAVAIL() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_ACCIDENT_TRAVAIL() %>">
					</td>
				</tr>
				<% } %>
				<% if(process.getDemandeCourant().getTypeSaisi().isDateDeclaration()) { %>
				<tr>
					<td width="155px;">
						<span class="sigp2Mandatory">Date de déclaration :</span>
					</td>
					<td width="320px;">
						<input id="<%=process.getNOM_EF_DATE()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE() %>">
					</td>
				</tr>
				<% } %>
				<% if(process.getDemandeCourant().getTypeSaisi().isCalendarDateDebut()) { %>
				<tr>
					<td>
						<span class="sigp2">Date de début :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DEBUT() %>" readonly="readonly">
					</td>
				</tr>
				<% } %>
				<% if(process.getDemandeCourant().getTypeSaisi().isCalendarDateFin()) { %>
				<tr>
					<td>
						<span class="sigp2">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN() %>" readonly="readonly">
					</td>
				</tr>
				<% } %>
				<% if(process.getDemandeCourant().getTypeSaisi().isNombreITT()) { %>
				<tr>
					<td>
						<span class="sigp2">Durée ITT (jours) :</span>
					</td>
					<td><span class="sigp2-saisie"><%= process.getVAL_EF_NB_JOUR_ITT() %></span></td>
				</tr>
				<% } %>
				<% if(process.getDemandeCourant().getTypeSaisi().isPrescripteur()) { %>
				<tr>
					<td>
						<span class="sigp2">Prescripteur :</span>
					</td>
					<td><span class="sigp2-saisie"><%= process.getVAL_EF_PRESCRIPTEUR() %></span></td>
				</tr>
				<% } %>
				<tr>
					<td>
						<span class="sigp2Mandatory">Type :</span>
					</td>
					<td>
						<% if(process.getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode())
								|| process.getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_RECHUTE.getCode())) { %>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_AT(), process.getVAL_LB_TYPE_SELECT()) %>
						</SELECT>
						<% } %>
						<% if(process.getDemandeCourant().getTypeSaisi().isMaladiePro()) { %>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_MP(), process.getVAL_LB_TYPE_SELECT()) %>
						</SELECT>
						<% } %>
					</td>
				</tr>
				<% if(process.getDemandeCourant().getTypeSaisi().isSiegeLesion()) { %>
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
				<% } %>
				<% if(process.getDemandeCourant().getTypeSaisi().isMotif()) { %>
				<tr>
					<td>
						<span class="sigp2Mandatory">Commentaire :</span> 
					</td>
					<td>
						<textarea cols="100" rows="3" name="<%=process.getNOM_EF_MOTIF()%>" title="Zone de saisie du commentaire"><%=process.getVAL_EF_MOTIF().trim() %></textarea>
					</td>
				</tr>
				<% } %>
				<tr>
					<td>
						<span class="sigp2">Avis commission :</span> 
					</td>
					<td>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_AVIS_COMMISSION() %>">
							<%=process.forComboHTML(process.getLB_AVIS_COMMISSION(), process.getVAL_LB_AVIS_COMMISSION_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<!-- suivi CAFAT pour les maladies pro -->
				<% if(process.getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode())) { %>
				<tr>
					<td>
						<span class="sigp2">Date de transmission CAFAT :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_TRANSMISSION_CAFAT() %>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_TRANSMISSION_CAFAT() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_TRANSMISSION_CAFAT() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_TRANSMISSION_CAFAT()%>', 'dd/mm/y');" />
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date de décision CAFAT :</span> 
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_DECISION_CAFAT() %>" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_DECISION_CAFAT() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_DECISION_CAFAT() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DECISION_CAFAT()%>', 'dd/mm/y');" />
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Taux de prise en charge par la CAFAT :</span> 
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="5" name="<%=process.getNOM_EF_TAUX_CAFAT() %>" size="5" type="text" value="<%= process.getVAL_EF_TAUX_CAFAT() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date de transmission de la commission d'aptitude :</span> 
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_COMMISSION_APTITUDE() %>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_COMMISSION_APTITUDE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_COMMISSION_APTITUDE() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_COMMISSION_APTITUDE()%>', 'dd/mm/y');" />
					</td>
				</tr>
				<% } %>
			</table>
            
            
			<%} else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) 
					|| process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) 
					|| process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>			
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<tr bgcolor="#EFEFEF">
							<td>
								<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
							</td>
							<td width="300px;">Nom du document</td>
							<td width="120px;" align="center">Date</td>
							<td>Commentaire</td>
						</tr>
					<%
					int indiceActes = 0;
					if (process.getDemandeCourant().getPiecesJointes() !=null){
						for (int i = 0;i<process.getDemandeCourant().getPiecesJointes().size();i++){ 
							PieceJointeDto pj = process.getDemandeCourant().getPiecesJointes().get(i);
						%>
						<tr id="doc<%=indiceActes%>" onmouseover="SelectLigneTabDoc(<%=indiceActes%>, <%=process.getDemandeCourant().getPiecesJointes().size()%>)">
							<td class="sigp2NewTab-liste" style="position:relative;width:60px;" align="center">
								
								<a href="<%=pj.getUrlFromAlfresco() %>" title="<%=pj.getTitre() %>" target="_blank" ><img onkeydown="" onkeypress="" 
									onkeyup="" src="images/oeil.gif" height="16px" width="16px" title="Voir le document <%=pj.getTitre() %>" /></a>
								<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
							</td>
							<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
							<td class="sigp2NewTab-liste" style="position:relative;text-align: center;width:90px;"><%=process.getVAL_ST_DATE_DOC(indiceActes)%></td>
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
			
			<% } else { %>

			<div>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION) ){ %>
		    	<FONT color='red'>Veuillez valider votre choix.</FONT>
		    	<BR/><BR/>
		    <% } %>
		    <table>
				<tr>
					<td width="155px;">&nbsp;</td>
					<td width="320px;">&nbsp;</td>
				</tr>
			<% if(process.getDemandeCourant().getTypeSaisi().isDateAccidentTravail()) { %>
				<tr>
					<td>
						<span class="sigp2">Date de l'accident du travail :</span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_ACCIDENT_TRAVAIL()%></span>
					</td>
				</tr>
				<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isDateDeclaration()) { %>
				<tr>
					<td>
						<span class="sigp2">Date de déclaration :</span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE()%></span>
					</td>
				</tr>
				<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isCalendarDateDebut()) { %>
				<tr>
					<td>
						<span class="sigp2">Date de début :</span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DEBUT()%></span>
					</td>
				</tr>
			<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isCalendarDateFin()) { %>
				<tr>
					<td>
						<span class="sigp2">Date de fin :</span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
					</td>
				</tr>
			<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isAtReference()) { %>
				<tr>
					<td>
						<span class="sigp2">AT de référence :</span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_LB_AT_REFERENCE()%></span>
					</td>
				</tr>
			<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isPrescripteur()) { %>
				<tr>
					<td>
						<span class="sigp2">Prescripteur :</span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_PRESCRIPTEUR()%></span>
					</td>
				</tr>
			<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isNombreITT()) { %>
				<tr>
					<td>
						<span class="sigp2">Durée ITT(jours) : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_NB_JOUR_ITT()%></span>
					</td>
				</tr>
			<% } %>
				<tr>
					<td>
						<span class="sigp2">Type : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE()%></span>
					</td>
				</tr>
			<% if(process.getDemandeCourant().getTypeSaisi().isSiegeLesion()) { %>
				<tr>
					<td>
						<span class="sigp2">Siège des lésions : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_SIEGE_LESION()%></span>
					</td>
				</tr>
			<% } %>
			<% if(process.getDemandeCourant().getTypeSaisi().isMotif()) { %>
		            		<tr>
		            			<td>
	                        		<span class="sigp2">Commentaire :</span>
		            			</td>
		            			<td>
									<textarea cols="100" rows="3" name="<%=process.getNOM_EF_MOTIF()%>" title="Zone de saisie du commentaire"  readonly="readonly"><%=process.getVAL_EF_MOTIF().trim() %></textarea>
		            			</td>
		            		</tr>
			<% } %>
				<tr>
					<td>
						<span class="sigp2">Avis commission : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_LB_AVIS_COMMISSION()%></span>
					</td>
				</tr>
			<!-- suivi CAFAT pour les maladies pro -->
			<% if(process.getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode())) { %>
				<tr>
					<td>
						<span class="sigp2">Date de transmission CAFAT : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_TRANSMISSION_CAFAT()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date de décision CAFAT : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DECISION_CAFAT()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Taux de prise en charge par la CAFAT : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_TAUX_CAFAT()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Date de transmission de la commission d'aptitude : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_COMMISSION_APTITUDE()%></span>
					</td>
				</tr>
			<% } %>
			</table>
		</div>
		<%} %>
			<BR/>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) 
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)
					&& !process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)){ %>
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
		
		<% } else if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)) {
				TypeAbsenceDto typeCreation = process.getTypeCreation(); %>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend"><%=process.getVAL_ST_TYPE_CREATION()%></legend>
					<div>
						<td>
			                <input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_CREATION(), process.getNOM_RB_TYPE_AT()) %> onclick="executeBouton('<%=process.getNOM_PB_TYPE_CREATION()%>');" > <span class="sigp2Mandatory">AT</span> 
						</td>
						<td>
							<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_CREATION(), process.getNOM_RB_TYPE_MP()) %> onclick="executeBouton('<%=process.getNOM_PB_TYPE_CREATION()%>');" > <span class="sigp2Mandatory">MP</span> 
						</td>
					</div>
					
			        <% if(typeCreation != null && typeCreation.getTypeSaisiDto()!=null &&
							process.getVAL_ST_ACTION().equals(process.ACTION_CREATION)) { %>
							
						<FIELDSET class="sigp2Fieldset" style="text-align:left;" id="<%=process.getNOM_RG_TYPE_CREATION() %>">
			            	<legend class="sigp2Legend"><%=process.getVAL_ST_TYPE_CREATION() %></legend>
			            		<INPUT name="JSP" type="hidden" value="<%= process.getJSP()%>">
				            	<table>
				            		<tr>
				            			<% if(typeCreation.getTypeSaisiDto().isCalendarDateDebut()) { %>
				            			<td width="100px">
			                        		<span class="sigp2Mandatory">Date de début :</span>
				            			</td>
				            			<td>
				            				<% if(typeCreation.getTypeSaisiDto().isNombreITT()) { %>
						                        <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	
						                        	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DEBUT()%>" 
						                        	onblur='executeBouton("<%=process.getNOM_PB_SET_ITT() %>")' >
				            				<% } else { %>
						                        <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	
						                        	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DEBUT()%>" >
				            				<% } %>
					                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');" >
				            			</td>
				            			<% } %>
				            		</tr>
				            		<tr>
				            			<% if(typeCreation.getTypeSaisiDto().isCalendarDateFin()) { %>
				            			<td>
			                        		<span class="sigp2Mandatory">Date de fin :</span>
				            			</td>
				            			<td>
				            				<% if(typeCreation.getTypeSaisiDto().isNombreITT()) { %>
						                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	
						                        	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_FIN()%>"
						                        	onblur='executeBouton("<%=process.getNOM_PB_SET_ITT() %>")' >
				            				<% } else { %>
						                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	
						                        	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_FIN()%>" >
				            				<% } %>
						                    <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
				            			</td>
				            			<% } %>
				            		</tr>
				            		
				            		<!-- Maladies -->
				            		<% if(typeCreation.getTypeSaisiDto().isPrescripteur()) { %>
				            		<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Prescripteur :</span>
				            			</td>
				            			<td colspan="2">
									       <INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_PRESCRIPTEUR() %>" size="18" type="text" value="<%= process.getVAL_ST_PRESCRIPTEUR() %>">
				            			</td>
				            		</tr>
				            		<% } %>
			            			<% if(typeCreation.getTypeSaisiDto().isDateAccidentTravail()) { %>
			            			<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Date de l'accident du travail :</span>
				            			</td>
				            			<td>
					                        <input id="<%=process.getNOM_ST_DATE_ACCIDENT_TRAVAIL()%>" class="sigp2-saisie" maxlength="10"	
					                        	name="<%= process.getNOM_ST_DATE_ACCIDENT_TRAVAIL()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_ACCIDENT_TRAVAIL()%>" >
					                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_ACCIDENT_TRAVAIL()%>', 'dd/mm/y');">
				            			</td>
			            			</tr>
			            			<% } %>
			            			<% if(typeCreation.getTypeSaisiDto().isDateDeclaration()) { %>
			            			<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Date de déclaration :</span>
				            			</td>
				            			<td>
					                        <input id="<%=process.getNOM_ST_DATE_DECLARATION()%>" class="sigp2-saisie" maxlength="10"	
					                        	name="<%= process.getNOM_ST_DATE_DECLARATION()%>" size="10" type="text" value="<%= process.getVAL_ST_DATE_DECLARATION()%>" >
					                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DECLARATION()%>', 'dd/mm/y');">
				            			</td>
			            			</tr>
			            			<% } %>
			            			<% if(typeCreation.getTypeSaisiDto().isProlongation()) { %>
			            			<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Prolongation :</span>
				            			</td>
				            			<td>
				            				<% if(typeCreation.getTypeSaisiDto().isNombreITT()) { %>
						                        <input type="checkbox" name="<%=process.getNOM_CK_PROLONGATION() %>" <% if(process.getVAL_CK_PROLONGATION().equals(process.getCHECKED_ON())){ %> checked="checked" <% } %> 
						                        onclick='executeBouton("<%=process.getNOM_PB_SET_ITT() %>")' />
						            		<% } else { %>
						                        <input type="checkbox" name="<%=process.getNOM_CK_PROLONGATION() %>" <% if(process.getVAL_CK_PROLONGATION().equals(process.getCHECKED_ON())){ %> checked="checked" <% } %> />
						            		<% } %>
				            			</td>
			            			</tr>
			            			<% } %>
			            			<% if(typeCreation.getTypeSaisiDto().isSansArretTravail()) { %>
			            			<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Sans arrêt de travail :</span>
				            			</td>
				            			<td>
				            				<% if(typeCreation.getTypeSaisiDto().isNombreITT()) { %>
						                        <input type="checkbox" name="<%=process.getNOM_CK_SANS_AT() %>" <% if(process.getVAL_CK_SANS_AT().equals(process.getCHECKED_ON())){ %> checked="checked" <% } %> 
						                        onclick='executeBouton("<%=process.getNOM_PB_SET_ITT() %>")' />
						            		<% } else { %>
						                        <input type="checkbox" name="<%=process.getNOM_CK_SANS_AT() %>" <% if(process.getVAL_CK_SANS_AT().equals(process.getCHECKED_ON())){ %> checked="checked" <% } %> />
						            		<% } %>
				            			</td>
			            			</tr>
			            			<% } %>
				            		<% if(typeCreation.getTypeSaisiDto().isNombreITT()) { %>
				            		<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Nombre ITT :</span>
				            			</td>
				            			<td colspan="2">
											<INPUT class="sigp2-saisie" maxlength="6" name="<%= process.getNOM_ST_NOMBRE_ITT() %>" 
											size="6" type="text" value="<%= process.getVAL_ST_NOMBRE_ITT() %>" >
											<span class="sigp2Mandatory"> jour(s)</span>
				            			</td>
				            		</tr>
			            			<% } %>
				            		<% if(process.getVAL_RG_TYPE_CREATION().equals(process.getNOM_RB_TYPE_AT())) { %>
				            		<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Type :</span>
				            			</td>
				            			<td colspan="2">
											<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE_AT() %>" style="width:340px;">
												<%=process.forComboHTML(process.getVAL_LB_TYPE_AT(), process.getVAL_LB_TYPE_AT_SELECT()) %>
											</SELECT>
				            			</td>
				            		</tr>
			            			<% } %>
				            		<% if(typeCreation.getTypeSaisiDto().isSiegeLesion()) { %>
				            		<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Siège des lésions :</span>
				            			</td>
				            			<td colspan="2">
									        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_SIEGE_LESION()%>" style="width:340px;">
									        	<%=process.forComboHTML(process.getVAL_LB_SIEGE_LESION(), process.getVAL_LB_SIEGE_LESION_SELECT())%>
									        </SELECT>
				            			</td>
				            		</tr>
			            			<% } %>
				            		<% if(typeCreation.getTypeSaisiDto().isMaladiePro()) { %>
				            		<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Maladie professionnelle :</span>
				            			</td>
				            			<td colspan="2">
								        <SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MALADIE_PRO()%>" style="width:500px;">
								        	<%=process.forComboHTML(process.getVAL_LB_MALADIE_PRO(), process.getVAL_LB_MALADIE_PRO_SELECT())%>
								        </SELECT>
				            			</td>
				            		</tr>
			            			<% } %>
				            		<tr>
				            			<td>
			                        		<span class="sigp2Mandatory">Commentaire :</span>
				            			</td>
				            			<td>
											<textarea cols="15" rows="3" name="<%=process.getNOM_ST_MOTIF_CREATION()%>" title="Zone de saisie du commentaire"><%=process.getVAL_ST_MOTIF_CREATION().trim() %></textarea>
											<%
											String infoCompl = "";
											if(null != typeCreation.getTypeSaisiDto().getInfosComplementaires()) {
												infoCompl = typeCreation.getTypeSaisiDto().getInfosComplementaires();
											} %>
											<%=infoCompl %>
				            			</td>
				            		</tr>
		
				            		<% if(typeCreation.getTypeSaisiDto().isPieceJointe()) { %>
				            		<tr>
										<td>
											<span class="sigp2Mandatory" style="width:130px;">Fichier : </span> 
										</td>
										<td>
											<% if(process.fichierUpload == null){ %>
							    				<INPUT style="margin-left: 5px;" title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOCUMENT()%>">
											<%}else{ %>
												<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT_CREATION() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT_CREATION() %>" >
												<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_CREATION_DOC()%>">
											<% }%>
											<br />
										</td>
				            		</tr>
				            		<% } %>
				            	</table>
				            	<%} %>
				            	<BR/>
			                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CREATION_DEMANDE() %>">
			                    <INPUT onkeydown="" onkeypress="" onkeyup="" type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER() %>">
				</FIELDSET>
			</FIELDSET>

			<!-- Fin de la création d'un AT/MP -->
		<%} else if (process.getVAL_ST_ACTION().equals(process.ACTION_CREATION_DOCUMENT_CREATION)) { %>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				<legend class="sigp2Legend">Ajout d'un document</legend>
				<div>
					<span class="sigp2Mandatory" style="width:130px;">Fichier : </span> 
					<% if(process.fichierUpload == null){ %>
						<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT_CREATION() %>" class="sigp2-saisie" type="file" value="<%= process.getVAL_EF_LIENDOCUMENT_CREATION() %>" >
					<%}else{ %>
						<INPUT name="<%= process.getNOM_EF_LIENDOCUMENT_CREATION() %>" class="sigp2-saisie" disabled="disabled" type="text" value="<%= process.getVAL_EF_LIENDOCUMENT_CREATION() %>" >
					<% }%>
					<br />
				</div>
				<div style="text-align: center">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER_CREATION_DOCUMENT_CREATION()%>">
					<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER_CREATION_DOCUMENT()%>">
				</div>
			</FIELDSET>
		<% } %>
		
    	<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_TYPE_CREATION()%>" value="TYPE_CREATION">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SET_ITT()%>" value="">
	</FORM>
<%} %>
	</BODY>
</HTML>