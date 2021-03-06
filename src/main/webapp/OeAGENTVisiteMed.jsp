<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.hsct.VisiteMedicale"%>
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des visites médicales</TITLE>
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
		//function pour changement couleur arriere plan ligne du tableau
		function SelectLigneAutreTab(id,tailleTableau)
		{
			for (i=0; i<tailleTableau; i++){
		 		document.getElementById("ligne"+i).className="";
			} 
		 document.getElementById("ligne"+id).className="selectLigne";
		}
		
		
		</SCRIPT>
		
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTVisiteMed" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" <%=process.isImporting ? "ENCTYPE=\"multipart/form-data\"" : ""%> method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
				<FIELDSET class="sigp2Fieldset" style="width:1030px;">
				    <legend class="sigp2Legend">Liste des visites médicales de l'agent</legend>
					    		<div style="overflow: auto;height: 250px;width:980px;">
									<table class="sigp2NewTab" style="text-align:left;width:960px;">
								    	<tr bgcolor="#EFEFEF" valign="bottom">
								    		<td align="left">
								    			<INPUT style="margin-left: 5px;" title="ajouter" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_CREER()%>">
								    		</td>
								    		<td align="center">Date dernière visite</td>
								    		<td align="center">Durée de validité (mois)</td>
								    		<td align="left">Nom du medecin</td>
								    		<td align="left">Motif</td>
								    		<td align="left">Recommandations</td>
								    		<td align="center">Nb docs</td>
								    	</tr>
										<%
										int indiceVisite = 0;
										if (process.getListeVisites()!=null){
											for (int i = 0;i<process.getListeVisites().size();i++){
												VisiteMedicale vm = (VisiteMedicale) process.getListeVisites().get(i);
										%>
												<tr id="<%=indiceVisite%>" <%if(vm.getIdVisite()==null){%> bgcolor="#B0C4DE"<%}else{%> onmouseover="SelectLigne(<%=indiceVisite%>,<%=process.getListeVisites().size()%>)"  <%} %> >
													<td class="sigp2NewTab-liste" style="position:relative;width:90px;" align="center">
													<%if(vm.getIdVisite()!=null){ %>
														<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceVisite)%>">								
														<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.CONSULTATION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceVisite)%>">
													<%}%>
														<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceVisite)%>">
													<%if(vm.getIdVisite()!=null){ %>
														<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER(indiceVisite)%>">
														<INPUT title="supprimer" type="image" src="images/suppression.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceVisite)%>">										
														<INPUT title="documents" type="image" src="images/ajout-doc.gif"  height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DOCUMENT(indiceVisite)%>">
													<%} %>
													</td>
													<td class="sigp2NewTab-liste" style="width:85px;text-align: center;"><%=process.getVAL_ST_DATE_VISITE(indiceVisite)%></td>
													<td class="sigp2NewTab-liste" style="width:50px;text-align: center;"><%=process.getVAL_ST_DUREE(indiceVisite)%></td>
													<td class="sigp2NewTab-liste" style="width:150px;text-align: left;"><%=process.getVAL_ST_NOM_MEDECIN(indiceVisite)%></td>
													<td class="sigp2NewTab-liste" style="width:150px;text-align: left;"><%=process.getVAL_ST_MOTIF(indiceVisite)%></td>
													<td class="sigp2NewTab-liste" style="width:300px;text-align: left;"><%=process.getVAL_ST_RECOMMANDATION(indiceVisite)%></td>
													<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_DOC(indiceVisite)%></td>
												</tr>
												<%
												indiceVisite++;
											}
										}%>
									</table>	
								</div>					
				</FIELDSET>
		<%if (! Const.CHAINE_VIDE.equals(process.getVAL_ST_ACTION()) ) {%>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
			<% if(!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && !process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
			<table>
				<tr>
					<td width="150px;">
						<span class="sigp2Mandatory">Motif :</span>
					</td>
					<td>
						<SELECT class="sigp2-saisie" <%= process.champMotifModifiable ? "" : "disabled='disabled'" %> name="<%= process.getNOM_LB_MOTIF() %>" onchange='executeBouton("<%=process.getNOM_PB_SELECT_MOTIF()%>")'>
							<%=process.forComboHTML(process.getVAL_LB_MOTIF(), process.getVAL_LB_MOTIF_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Date de visite :</span>
					</td>
					<td>
						<input id="<%=process.getNOM_EF_DATE_VISITE()%>" class="sigp2-saisie" <%= process.elementModifibale ? "" : "disabled='disabled'" %> maxlength="10"	name="<%= process.getNOM_EF_DATE_VISITE() %>" size="10" type="text"	value="<%= process.getVAL_EF_DATE_VISITE() %>">
						<%if(process.elementModifibale){ %>
						<IMG src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_EF_DATE_VISITE()%>', 'dd/mm/y');">
						<%} %>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Durée de validité (mois) :</span>
					</td>
					<td>
						<INPUT <%= process.elementModifibale ? "" : "disabled='disabled'" %> class="sigp2-saisie" maxlength="5" name="<%= process.getNOM_EF_DUREE() %>" size="5" type="text" value="<%= process.getVAL_EF_DUREE() %>">
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory">Medecin :</span>
					</td>
					<td>
						<SELECT <%= process.elementModifibale ? "" : "disabled='disabled'" %> class="sigp2-saisie" name="<%= process.getNOM_LB_MEDECIN() %>">
							<%=process.forComboHTML(process.getVAL_LB_MEDECIN(), process.getVAL_LB_MEDECIN_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2Mandatory"> Avis :</span>
					</td>
					<td>
						<SELECT <%= process.elementModifibale ? "" : "disabled='disabled'" %> class="sigp2-saisie" name="<%= process.getNOM_LB_RECOMMANDATION() %>" >
							<%=process.forComboHTML(process.getVAL_LB_RECOMMANDATION(), process.getVAL_LB_RECOMMANDATION_SELECT()) %>
						</SELECT>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Recommandations :</span>
					</td>
					<td>
						<textarea <%= process.elementModifibale ? "" : "disabled='disabled'" %> class="sigp2-saisie" rows="3" maxlength="2500" style="position:relative;width:600px" name="<%= process.getNOM_ST_COMMENTAIRE_VM() %>" ><%= process.getVAL_ST_COMMENTAIRE_VM() %></textarea>
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
						<td width="230px;">
							<span>Nom du document</span>
						</td>
						<td width="230px;">
							<span>Nom original</span>
						</td>
						<td width="90px;" align="center">
							<span>Type</span> 
						</td>
						<td width="120px;" align="center">
							<span>Date</span> 
						</td>
						<td>
							<span>Commentaire</span> 
						</td>
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
							<td class="sigp2NewTab-liste" style="position:relative;width:120px;text-align: center;"><%=process.getVAL_ST_TYPE_DOC(indiceActes)%></td>
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
				
				<% if(process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
				<table>
					<tr>
						<td colspan="2" class="sigp2Mandatory">
				   			<FONT color='red'>Veuillez valider votre choix.</FONT>
						</td>
					</tr>
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
			<table>
				<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
				<tr>
					<td colspan="2"  class="sigp2Mandatory">
				    	<FONT color='red'>Veuillez valider votre choix.</FONT>
					</td>
				</tr>
				<%} %>
				<tr>
					<td width="150px;">
				    	<span class="sigp2">Date de visite : </span>
					</td>
					<td>
						<span class="sigp2-saisie"><%=process.getVAL_ST_DATE_VISITE()%></span>
					</td>
				</tr>
				<tr>
					<td>
					<span class="sigp2">Durée de validité : </span>
					</td>
					<td>
					<span class="sigp2-saisie"><%=process.getVAL_ST_DUREE_VALIDITE()%></span>
					</td>
				</tr>
				<tr>
					<td>
					<span class="sigp2">Nom du medecin : </span>
					</td>
					<td>
					<span class="sigp2-saisie"><%=process.getVAL_ST_NOM_MEDECIN()%></span>
					</td>
				</tr>
				<tr>
					<td>
					<span class="sigp2">Motif : </span>
					</td>
					<td>
					<span class="sigp2-saisie"><%=process.getVAL_ST_MOTIF()%></span>
					</td>
				</tr>
				<tr>
					<td>
					<span class="sigp2">Avis : </span>
					</td>
					<td>
					<span class="sigp2-saisie"><%=process.getVAL_ST_RECOMMANDATION()%></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="sigp2">Recommandations :</span>
					</td>
					<td>
						<span class="sigp2-saisie">
							<textarea disabled="disabled" class="sigp2-saisie" rows="3" maxlength="2500" style="position:relative;width:600px" name="<%= process.getNOM_ST_COMMENTAIRE_VM() %>" ><%= process.getVAL_ST_COMMENTAIRE_VM() %></textarea>
						</span>
					</td>
				</tr>
			</table>
			<% } %>
			<BR/>
			<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_CREATION) && !process.getVAL_ST_ACTION().equals(process.ACTION_DOCUMENT_SUPPRESSION)){ %>
			<TABLE align="center" border="0" cellpadding="0" cellspacing="0">
			<TBODY>
				<TR>
					<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_CONSULTATION)){ %>
					    <TD width="31">
					    	<INPUT type="submit" class="sigp2-Bouton-100" value="Valider" name="<%=process.getNOM_PB_VALIDER()%>">
					    	</TD>
						<TD height="18" width="15">
						<% if (!process.getVAL_ST_ACTION().equals(process.ACTION_SUPPRESSION) && MairieUtils.estEdition(request, process.getNomDroitGenererCertificat())){ %>
							<INPUT <%= MairieUtils.getDisabled(request, process.getNomDroitGenererCertificat()) %> type="submit" class="sigp2-Bouton-200" value="Générer certificat aptitude" name="<%=process.getNOM_PB_IMPRIMER_CERTIFICAT_APTITUDE()%>">
						<%} %>
					    </TD>
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
		<% } %>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SELECT_MOTIF()%>" value="x">
	<%=process.getUrlFichier()%>
	</FORM>
<%} %>
	</BODY>
</HTML>