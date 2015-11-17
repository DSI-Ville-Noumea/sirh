<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
									<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
								</td>
								<td align="center">AT / MP</td>
								<td align="center">Date</td>
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
									<tr id="<%=indiceAcc%>" onmouseover="SelectLigne(<%=indiceAcc%>,<%=process.getListeAT().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:70px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAcc)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceAcc)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceAcc)%>">
											<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceAcc)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_AT_MP(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DATE(indiceAcc)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_RECHUTE(indiceAcc)%></td>
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
						<span class="sigp2Mandatory">Date de déclaration :</span>
					</td>
					<td width="320px;">
						<input id="<%=process.getNOM_EF_DATE()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE()%>', 'dd/mm/y');" />
					</td>
					<td width="150px;">
						<INPUT type="radio" onchange='executeBouton("<%=process.getNOM_PB_SELECT_AT() %>")' <%= process.forRadioHTML(process.getNOM_RG_TYPE_AT_MP(), process.getNOM_RB_AT()) %> ><span class="sigp2Mandatory">AT</span>
						<INPUT type="radio" onchange='executeBouton("<%=process.getNOM_PB_SELECT_MP() %>")' <%= process.forRadioHTML(process.getNOM_RG_TYPE_AT_MP(), process.getNOM_RB_MP()) %> ><span class="sigp2Mandatory">MP</span>
					</td>
				</tr>
				<tr>
					<% if((null == process.getVoAccidentTravailMaladieProCourant().getRechute()
							|| !process.getVoAccidentTravailMaladieProCourant().getRechute())
								&& process.getVoAccidentTravailMaladieProCourant().isTypeMP()) { %>
					<td>
						<span class="sigp2">Date de fin :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text" value="<%= process.getVAL_EF_DATE_FIN() %>">
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');" />
					</td>
					<% }else{ %>
					<td></td>
					<td></td>
					<% } %>
					<td width="150px;">
						<INPUT type="checkbox" <%=process.forCheckBoxHTML(process.getNOM_CK_RECHUTE(), process.getVAL_CK_RECHUTE())%> onchange="executeBouton('<%=process.getNOM_PB_SELECT_RECHUTE() %>')" /> <span class="sigp2">RECHUTE</span>
						
						<% if((null != process.getVoAccidentTravailMaladieProCourant().getRechute()
								&& process.getVoAccidentTravailMaladieProCourant().getRechute())
								&& process.getVoAccidentTravailMaladieProCourant().isTypeAT()) { %>
							<SELECT class="sigp2-saisie" name="<%=process.getNOM_LB_AT_REFERENCE() %>">
								<%=process.forComboHTML(process.getLB_AT_REFERENCE(), process.getVAL_LB_AT_REFERENCE_SELECT()) %>
							</SELECT>
						<% } %>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Durée ITT (jours) :</span>
					</td>
					<td>
						<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_NB_JOUR_ITT() %>" size="5" type="text" value="<%= process.getVAL_EF_NB_JOUR_ITT() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Type :</span>
					</td>
					<td>
						<% if(process.getVoAccidentTravailMaladieProCourant().isTypeAT()) { %>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_AT(), process.getVAL_LB_TYPE_SELECT()) %>
						</SELECT>
						<% } %>
						<% if(process.getVoAccidentTravailMaladieProCourant().isTypeMP()) { %>
						<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_TYPE() %>">
							<%=process.forComboHTML(process.getVAL_LB_TYPE_MP(), process.getVAL_LB_TYPE_SELECT()) %>
						</SELECT>
						<% } %>
					</td>
				</tr>
				<% if(process.getVoAccidentTravailMaladieProCourant().isTypeAT()) { %>
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
				<% if(process.getVoAccidentTravailMaladieProCourant().isTypeMP()) { %>
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
            
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SELECT_AT()%>" value="<%=process.getNOM_PB_SELECT_AT()%>">
			<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SELECT_MP()%>" value="<%=process.getNOM_PB_SELECT_MP()%>">
            <INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SELECT_RECHUTE()%>" value="<%=process.getNOM_PB_SELECT_RECHUTE()%>">
            
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>			
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<tr bgcolor="#EFEFEF">
							<td>
								<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
							</td>
							<td width="230px;">Nom du document</td>
							<td width="230px;">Nom original</td>
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
							<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_ORI_DOC(indiceActes)%></td>
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
			<% if((null == process.getVoAccidentTravailMaladieProCourant().getRechute()
							|| !process.getVoAccidentTravailMaladieProCourant().getRechute())
								&& process.getVoAccidentTravailMaladieProCourant().isTypeMP()) { %>
		    <span class="sigp2" style="width:150px">Date de fin : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_FIN()%></span>
			<BR/>
			<% } %>
			<span class="sigp2" style="width:150px">Rechute : </span>
			<span class="sigp2-saisie"><%= null != process.getVAL_CK_RECHUTE() && process.getCHECKED_ON().equals(process.getVAL_CK_RECHUTE()) ? "Oui" : "Non" %></span>
			<BR/>
			<% if((null != process.getVoAccidentTravailMaladieProCourant().getRechute()
								&& process.getVoAccidentTravailMaladieProCourant().getRechute())
								&& process.getVoAccidentTravailMaladieProCourant().isTypeAT()) { %>
			<span class="sigp2" style="width:150px">AT de référence : </span>
			<span class="sigp2-saisie"><%=process.getVAL_LB_AT_REFERENCE()%></span>
			<BR/>
			<% } %>
			<span class="sigp2" style="width:150px">Durée ITT(jours) : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_NB_JOUR_ITT()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Type : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_TYPE()%></span>
			<BR/>
			<% if(process.getVoAccidentTravailMaladieProCourant().isTypeAT()) { %>
			<span class="sigp2" style="width:150px">Siège des lésions : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_SIEGE_LESION()%></span>
			<BR/>
			<% } %>
			<span class="sigp2" style="width:150px">Avis commission : </span>
			<span class="sigp2-saisie"><%=process.getVAL_LB_AVIS_COMMISSION() %></span>
			<BR/>
			<!-- suivi CAFAT pour les maladies pro -->
			<% if(process.getVoAccidentTravailMaladieProCourant().isTypeMP()) { %>
			<span class="sigp2" style="width:150px">Date de transmission CAFAT : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_TRANSMISSION_CAFAT()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Date de décision CAFAT : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_DECISION_CAFAT()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Taux de prise en charge par la CAFAT : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_TAUX_CAFAT()%></span>
			<BR/>
			<span class="sigp2" style="width:150px">Date de transmission de la commission d'aptitude : </span>
			<span class="sigp2-saisie"><%=process.getVAL_EF_DATE_COMMISSION_APTITUDE()%></span>
			<BR/>
			<% } %>
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