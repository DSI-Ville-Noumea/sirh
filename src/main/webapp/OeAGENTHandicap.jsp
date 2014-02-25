<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
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
		
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTHandicap" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des handicaps de l'agent</legend>
				    <br/>
				    <span style="position:relative;width:9px;"></span>
				    <span style="position:relative;width:85px;"><INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>"></span>
				    <span style="position:relative;width:100px;text-align: left;">Type</span>
				    <span style="position:relative;width:90px;text-align: center;">Début</span>
					<span style="position:relative;width:95px;text-align: center;">Fin</span>
					<span style="position:relative;width:60px;text-align: center;">% incapacité</span>
					<span style="position:relative;width:90px;text-align: center;">Reconnaissance maladie prof.</span>
					<span style="position:relative;width:90px;text-align: center;">Handicap reconnu CRDHNC</span>
					<span style="position:relative;width:90px;text-align: center;">N° de carte CRDHNC</span>
					<span style="position:relative;width:95px;text-align: center;">CRDHNC en cours renouvellement</span>
					<span style="position:relative;width:95px;text-align: center;">Amenagement</span>
					<span style="position:relative;text-align: center;">Nb docs</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceHandi = 0;
							if (process.getListeHandicap()!=null){
								for (int i = 0;i<process.getListeHandicap().size();i++){
							%>
									<tr id="<%=indiceHandi%>" onmouseover="SelectLigne(<%=indiceHandi%>,<%=process.getListeHandicap().size()%>)">
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;" align="center">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceHandi)%>">
											<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceHandi)%>">
											<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceHandi)%>">
											<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceHandi)%>">
											<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceHandi)%>">
										</td>
										<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: left;"><%=process.getVAL_ST_TYPE(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_DEBUT(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_FIN(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:60px;text-align: center;"><%=process.getVAL_ST_INCAPACITE(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_MALADIE_PROF(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_CRDHNC(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: right;"><%=process.getVAL_ST_NUM_CARTE(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;"><%=process.getVAL_ST_RENOUVELLEMENT(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:90px;text-align: center;">&nbsp;<%=process.getVAL_ST_AMENAGEMENT(indiceHandi)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceHandi)%></td>
									</tr>
									<%
									indiceHandi++;
								}
							}%>
						</table>	
						</div>	
				</FIELDSET>
		<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<%if(!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<div>
			
			<span style="width:350px">
			
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:130px;">Type :</span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_NOM() %>">
				<%=process.forComboHTML(process.getVAL_LB_NOM(), process.getVAL_LB_NOM_SELECT()) %>
			</SELECT>
			
			<BR/><BR/>

			<span class="sigp2Mandatory"  style="margin-left:20px;position:relative;width:130px;">Date de début :</span>
			<input id="<%=process.getNOM_EF_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_DEBUT() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_DEBUT() %>" />
			<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_DEBUT()%>', 'dd/mm/y');" />

			<BR/><BR/>

			<span class="sigp2"  style="margin-left:20px;position:relative;width:130px;">Date de fin :</span>
			<input id="<%=process.getNOM_EF_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_EF_DATE_FIN() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_FIN() %>" />
			<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_FIN()%>', 'dd/mm/y');" />

			<BR/><BR/>

			<span class="sigp2" style="margin-left:20px;position:relative;width:130px;">% Incapacité :</span>
			<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_INCAPACITE() %>" size="5" type="text" value="<%= process.getVAL_EF_INCAPACITE() %>" />
		
			</span>
			<span style="width:655px;">
			
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Maladie Professionnelle :</span> 
			<input type="radio" onclick='executeBouton("<%=process.getNOM_PB_SELECT_MALADIE_PRO()%>")' <%= process.forRadioHTML(process.getNOM_RG_RECO_MP(), process.getNOM_RB_RECO_MP_OUI()) %> > Oui
			<input type="radio" onclick='executeBouton("<%=process.getNOM_PB_SELECT_MALADIE_PRO()%>")' <%= process.forRadioHTML(process.getNOM_RG_RECO_MP(), process.getNOM_RB_RECO_MP_NON()) %> > Non
			
			<BR/>
			<%if(process.showMaladiePro){ %>
			<span class="sigp2" style="margin-left:20px;position:relative;width:180px;visibility:<%= process.showMaladiePro ? "visible" : "hidden" %>;" >Nom maladie professionnelle :</span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_NOM_MP() %>" style="visibility:<%= process.showMaladiePro ? "visible" : "hidden" %>; width:445px;">
				<%=process.forComboHTML(process.getVAL_LB_NOM_MP(), null, null, process.getVAL_LB_NOM_MP(), process.getVAL_LB_NOM_MP_SELECT()) %>
			</SELECT>
			<%} %>
			
			<br />
			<br />
			
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Handicap CRDHNC :</span> 
			<input type="radio" onclick='executeBouton("<%=process.getNOM_PB_SELECT_CRDHNC()%>")' <%=process.forRadioHTML(process.getNOM_RG_RECO_CRDHNC(), process.getNOM_RB_RECO_CRDHNC_OUI()) %> > Oui
			<input type="radio" onclick='executeBouton("<%=process.getNOM_PB_SELECT_CRDHNC()%>")' <%=process.forRadioHTML(process.getNOM_RG_RECO_CRDHNC(), process.getNOM_RB_RECO_CRDHNC_NON()) %> > Non
			
			<br />
			
			<span class="sigp2" style="margin-left:20px;position:relative;width:180px;visibility:<%= process.showNumCarte ? "visible" : "hidden" %>;">N° carte CRDHNC :</span>
			<INPUT class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_NUM_CRDHNC() %>" style="visibility:<%= process.showNumCarte ? "visible" : "hidden" %>;"  size="5" type="text" value="<%= process.getVAL_EF_NUM_CRDHNC() %>" />		
			<br />
			<%if(process.showNumCarte){ %>
			<span class="sigp2" style="margin-left:20px;position:relative;width:180px;visibility:<%= process.showNumCarte ? "visible" : "hidden" %>;">En cours de renouvellement :</span>
			<input type="radio" <%=process.forRadioHTML(process.getNOM_RG_RENOUV_CRDHNC(), process.getNOM_RB_RENOUV_CRDHNC_OUI()) %> > Oui
			<input type="radio" <%=process.forRadioHTML(process.getNOM_RG_RENOUV_CRDHNC(), process.getNOM_RB_RENOUV_CRDHNC_NON()) %> > Non	
			<br />
			<%} %>
			<br />
			
			<span class="sigp2Mandatory" style="margin-left:20px;position:relative;width:180px;">Amenagement de poste :</span> 
			<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_AMENAGEMENT(), process.getNOM_RB_AMENAGEMENT_OUI()) %> > Oui
			<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_AMENAGEMENT(), process.getNOM_RB_AMENAGEMENT_NON()) %> > Non
			
			</span>
			<br /><br />
			<span class="sigp2" style="margin-left:20px;position:relative;width:130px;vertical-align:top;">Commentaire :</span>	
			<textarea rows="3" maxlength="100" style="width:600px" name="<%= process.getNOM_EF_COMMENTAIRE() %>" ><%= process.getVAL_EF_COMMENTAIRE() %></textarea>
			</div>
			<%}else if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) || process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
				<span style="position:relative;width:9px;"></span>
				<span style="position:relative;width:55px;">	
				<INPUT title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER_DOC()%>">
				</span>
				<span style="margin-left:5px;position:relative;width:230px;text-align: left;">Nom du document</span>
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
								<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">
								<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceActes)%>">	
								<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_DOC(indiceActes)%>">
							</td>
							<td class="sigp2NewTab-liste" style="position:relative;width:230px;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceActes)%></td>
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
						<span class="sigp2" style="width:130px;" >Commentaire :</span><INPUT class="sigp2-saisie" maxlength="100" name="<%= process.getNOM_EF_COMMENTAIRE_DOCUMENT() %>" size="100" type="text" value="<%= process.getVAL_EF_COMMENTAIRE() %>">
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
				<span class="sigp2-saisie"><%=process.getVAL_ST_NOM()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">Date de début : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_DEBUT()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">Date de fin : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_FIN()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">% Incapacité : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_INCAPACITE()%></span>
			</span>
			<span style="width:49%">
		    	<span class="sigp2" style="width:150px">Maladie professionnelle : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_RECO_MP()%></span>
				<BR/>
				<span class="sigp2" style="width:150px">Nom maladie professionelle : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_MP()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">Handicap CRDHNC : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_RECO_CRDHNC()%></span>
				<BR/>
				<span class="sigp2" style="width:150px">N° carte CRDHNC : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_NUM_CRDHNC()%></span>
				<BR/>
				<span class="sigp2" style="width:150px">Carte en cours renouvellement : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_RENOUV_CRDHNC()%></span>
				<BR/>
				<BR/>
				<span class="sigp2" style="width:150px">Amenagement de poste : </span>
				<span class="sigp2-saisie"><%=process.getVAL_ST_AMENAGEMENT()%></span>
			</span>
			<span class="sigp2" style="width:150px">Commentaire : </span>
			<span class="sigp2-saisie"><%=process.getVAL_ST_COMMENTAIRE()%></span>
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
		<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SELECT_MALADIE_PRO()%>" value="x">
		<INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_SELECT_CRDHNC()%>" value="x">
	<%=process.getUrlFichier()%>
		</FORM>	
		<% } %>
	<%} %>
	</BODY>
</HTML>