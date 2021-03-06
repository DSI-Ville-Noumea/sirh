<%@page import="nc.mairie.gestionagent.absence.dto.MoisAlimAutoCongesAnnuelsDto"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumTypeAbsence"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.gestionagent.dto.AgentDto"%>
<%@page import="nc.mairie.gestionagent.dto.AgentWithServiceDto"%>
<%@page import="nc.mairie.gestionagent.dto.ApprobateurDto"%>
<%@page import="nc.mairie.gestionagent.absence.dto.HistoriqueSoldeDto"%>
<%@page import="nc.mairie.gestionagent.absence.dto.DemandeDto"%>
<%@page import="nc.mairie.enums.EnumEtatAbsence"%>
<%@page import="java.text.SimpleDateFormat"%>
<HTML>
	
	<jsp:useBean class="nc.mairie.gestionagent.process.agent.OeAGENTAbsencesSolde" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
		<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		<TITLE>Gestion des absences</TITLE>

		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
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
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();return setfocus('<%= process.getFocus() %>');">
	<%@ include file="BanniereErreur.jsp" %>
<%if(process.getAgentCourant() !=null){ %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">				
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
				    <legend class="sigp2Legend">Liste des soldes de l'agent</legend>
				    <table>
				    	<tr>
				    		<td width="50%">
							    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">Congés</legend>
									<table class="sigp2NewTab" style="text-align:left;width:400px;">
										<tr bgcolor="#EFEFEF">
											<td width="100px;" align="center">année prec.</td>
											<td width="120px;" align="center">année</td>
											<td width="80px;">Historique</td>
											<td>Samedi offert</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGE_PREC()%></td>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGE()%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.CONGE.getCode())%>"></td>
											<td style="text-align: center"><%=process.getVAL_ST_SAMEDI_OFFERT_SOLDE_CONGE()%></td>
										</tr>
									</table>				    
							    </FIELDSET>
				    		</td>
				    		<td>
							    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">Récupérations</legend>
									<table class="sigp2NewTab" style="text-align:left;width:200px;">
										<tr bgcolor="#EFEFEF">
											<td width="200px;" align="center">En cours</td>
											<td>Historique</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_RECUP()%></td>
											<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.RECUP.getCode())%>"></td>
										</tr>
									</table>				    
							    </FIELDSET>
				    		</td>
				    	</tr>
				    </table>
				    
					<%if(process.isAgentReposComp()){ %>
				    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
				    	<legend class="sigp2Legend">Repos compensateurs</legend>
							<table class="sigp2NewTab" style="text-align:left;width:200px;">
								<tr bgcolor="#EFEFEF">
									<td width="100px;" align="center">année prec.</td>
									<td width="100px;" align="center">année</td>
									<td>Historique</td>
								</tr>
								<tr>
									<td style="text-align: center"><%=process.getVAL_ST_SOLDE_REPOS_COMP_PREC()%></td>
									<td style="text-align: center"><%=process.getVAL_ST_SOLDE_REPOS_COMP()%></td>
									<td style="text-align: center"><INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.REPOS_COMP.getCode())%>"></td>
								</tr>
							</table>			    
				    </FIELDSET>
					<%} %>
				    <BR/>	
				    
				     <FIELDSET class="sigp2Fieldset" style="text-align:left;width:950px;">
				    	<legend class="sigp2Legend">Congés Exceptionnels</legend>
						<table class="sigp2NewTab" style="text-align:left;width:900px;">
							<tr bgcolor="#EFEFEF">
								<td width="750px;">Type</td>
								<td width="150px;" align="center">Congés déjà pris ou en cours</td>
							</tr>
							<% for(int i = 0; i< process.getListeSoldeCongesExcep().size();i++){ %>
							<tr>
								<td width="750px;"><%=process.getVAL_ST_TYPE_CONGES_EXCEP(i)%></td>
								<td width="150px;" style="text-align: center"><%=process.getVAL_ST_SOLDE_CONGES_EXCEP(i)%></td>
							</tr>
							<% } %>
						</table>
				    </FIELDSET>
				    
				    <BR/>
				    
				     <FIELDSET class="sigp2Fieldset" style="text-align:left;width:950px;">
				    	<legend class="sigp2Legend">Maladies</legend>
						<table class="sigp2NewTab" style="text-align:left;width:900px;">
							<tr bgcolor="#EFEFEF">
								<td width="150px;" align="center">Droits PS</td>
								<td width="150px;" align="center">Droits DS</td>
								<td width="150px;" align="center">Reste à prendre PS</td>
								<td width="150px;" align="center">Reste à prendre DS</td>
								<td width="150px;" align="center">Total pris</td>
								<td width="150px;" align="center">Historique</td>
							</tr>
							<% if(null != process.getSoldeGlobal().getSoldeMaladies()){ %>
							<tr>
								<td width="150px;" align="center"><%=process.getSoldeGlobal().getSoldeMaladies().getDroitsPleinSalaire()%></td>
								<td width="150px;" align="center"><%=process.getSoldeGlobal().getSoldeMaladies().getDroitsDemiSalaire()%></td>
								<td width="150px;" align="center"><%=process.getSoldeGlobal().getSoldeMaladies().getRapPleinSalaire()%></td>
								<td width="150px;" align="center"><%=process.getSoldeGlobal().getSoldeMaladies().getRapDemiSalaire()%></td>
								<td width="150px;" align="center"><%=process.getSoldeGlobal().getSoldeMaladies().getTotalPris()%></td>
								<td style="text-align: center">
									<INPUT title="historique" type="image" src="images/oeil.gif" height="15px" width="15px" 
										name="<%=process.getNOM_PB_HISTORIQUE(EnumTypeAbsence.MALADIES_MALADIE.getCode())%>">
								</td>
							</tr>
							<% } %>
						</table>
				    </FIELDSET>
				    
				</FIELDSET>
				<BR/>
				
				<%if (! "".equals(process.getVAL_ST_ACTION()) ) {%>
				<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
					<legend class="sigp2Legend"><%=process.getVAL_ST_ACTION()%></legend>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<%
						if(!process.isAfficheHistoriqueMaladies()) {
							 %>
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<tr bgcolor="#EFEFEF">
								<td align="center" width="90px;">Le <br/> à</td>
								<td width="180px;">Par</td>
								<td width="180px;">Motif</td>
								<td>Opération</td>
							</tr>
							<%
							for (int i = 0;i<process.getListeHistorique().size();i++){
							%>
								<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeHistorique().size()%>)">
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_PAR(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_MOTIF(i)%></td>
									<td class="sigp2NewTab-liste"><%=process.getVAL_ST_OPERATION(i)%></td>
								</tr>
							<%}%>
						</table>
						<% }else{ %>
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
								<tr bgcolor="#EFEFEF">
									<td align="center" width="90px;">Type</td>
									<td align="center" width="150px;">Date de début</td>
									<td align="center" width="150px;">Date de fin</td>
									<td align="center" width="150px;">Nombre de jours</td>
									<td align="center">Total Pris</td>
									<td align="center">Nombre jours coupés DS</td>
									<td align="center">Nombre jours coupés PS</td>
									<td align="center">Nombre jours RAP DS</td>
									<td align="center">Nombre jours RAP PS</td>
								</tr>
								<%
								SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
								for (int i = 0;i<process.getListeHistorique().size();i++){
									HistoriqueSoldeDto histo = process.getListeHistorique().get(i);
								%>
									<tr id="<%=i%>" onmouseover="SelectLigne(<%=i%>,<%=process.getListeHistorique().size()%>)">
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getTypeAbsence()%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=sdfDate.format(histo.getDateDebut())%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=sdfDate.format(histo.getDateFin())%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getDuree()%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getTotalPris()%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getNombreJoursCoupeDemiSalaire()%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getNombreJoursCoupePleinSalaire()%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getNombreJoursResteAPrendreDemiSalaire()%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=histo.getNombreJoursResteAPrendrePleinSalaire()%></td>
									</tr>
								<%}%>
							</table>
						<% } %>
						</div>
						<BR/><BR/>
						
						<%if(null != process.getListeDemandeCA()
								&& !process.getListeDemandeCA().isEmpty()){ %>
						<span>Historique des demandes ayant débitées/créditées le compteur</span>
						<BR/><BR/>
						<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
								<tr bgcolor="#EFEFEF">
									<td align="center" width="90px;">Date de modification compteur</td>
									<td align="center" width="90px;">Date de début</td>
									<td align="center" width="90px;">Date de fin</td>
									<td align="center" width="90px;">Nb jours</td>
									<td align="center" width="90px;">Etat demande</td>
									<td align="center" width="90px;">Solde Agent AVANT N-1</td>
									<td align="center" width="90px;">Solde Agent AVANT N</td>
									<td align="center" width="90px;">Solde Agent APRES N-1</td>
									<td align="center" width="90px;">Solde Agent APRES N</td>
								</tr>
								<%
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
								for (int i = 0;i<process.getListeDemandeCA().size();i++){
									DemandeDto demandeCA = process.getListeDemandeCA().get(i);
								%>
								<tr>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=sdf.format(demandeCA.getDateSaisie()) %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=sdf.format(demandeCA.getDateDebut()) %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=sdf.format(demandeCA.getDateFin()) %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=demandeCA.getDuree() %> <% if(demandeCA.isSamediOffert()){ %> + S<% } %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=EnumEtatAbsence.getValueEnumEtatAbsence(demandeCA.getIdRefEtat()) %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=demandeCA.getTotalJoursAnneeN1Old() %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=demandeCA.getTotalJoursOld() %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=demandeCA.getTotalJoursAnneeN1New() %></td>
									<td class="sigp2NewTab-liste" style="text-align: center;"><%=demandeCA.getTotalJoursNew() %></td>
								</tr>
								<%}%>
						</table>	
						</div>
						<BR/><BR/>
						<%}%>
						
						<%if(process.getListeHistoriqueAlimAuto().size()!=0){ %>
						<span>Historique des alimentations automatiques de fin de mois</span>
						<BR/><BR/>
						<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
								<tr bgcolor="#EFEFEF">
									<td align="center" width="90px;">Mois</td>
									<td align="center" width="90px;">Date de modification</td>
									<td align="center" width="50px;">Nb jours</td>
									<td>Commentaire</td>
								</tr>
								<%
								for (int i = 0;i<process.getListeHistoriqueAlimAuto().size();i++){
								%>
									<tr>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_MOIS(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_DATE_MODIF_HISTO_ALIM_AUTO(i)%></td>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_NB_JOUR(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_COMMENTAIRE(i)%></td>
									</tr>
								<%}%>
							</table>	
						</div>
						<BR/><BR/>
						<%} %>
						
						<%if(process.getListeHistoriqueAlimPaie().size()!=0){ %>
						<span>Historique des alimentations automatiques lors de la paie</span>
						<BR/><BR/>						
							<table class="display" id="tabAlimPaie">
							<thead>
								<tr>
									<th>Date de modification</th>
									<th>Date de Pointage ou Date de ventilation</th>
									<th>Nb</th>
									<th>Commentaire</th>					
								</tr>
							</thead>
							<tbody>
							<%
								for (int i = 0;i<process.getListeHistoriqueAlimPaie().size();i++){
									MoisAlimAutoCongesAnnuelsDto avct = (MoisAlimAutoCongesAnnuelsDto) process.getListeHistoriqueAlimPaie().get(i);
							%>
									<tr>
										<td><%=process.getVAL_ST_DATE_MODIF(i)%></td>
										<td><%=process.getVAL_ST_MOIS(i)%></td>
										<td><%=process.getVAL_ST_NB_JOUR(i)%></td>
										<td><%=process.getVAL_ST_COMMENTAIRE(i)%></td>										
									</tr>
							<%
								}
							%>
							</tbody>
						</table>
						<script type="text/javascript">
							$(document).ready(function() {
							    $('#tabAlimPaie').dataTable({
									"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
									"aoColumns": [null,null,null],
									"sDom": '<"H"l>t<"F"iT>',
									"sScrollY": "250px",
									"bPaginate": false,
									"oTableTools": {
										"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"alimPaieRecup","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
										"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
									}
							    });
							} );
						</script>	
						<BR/><BR/>
						<%} %>
						
						
						
						<%if(!process.getListeHistoriqueRestitutionMassive().isEmpty()){ %>
						<span>Historique des restitutions massives</span>
						<BR/><BR/>
						<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
							<table class="sigp2NewTab" style="text-align:left;width:980px;">
								<tr bgcolor="#EFEFEF">
									<td align="center" width="90px;">Mois</td>
									<td align="center" width="90px;">Motif</td>
									<td>Nb</td>
								</tr>
								<%
								for (int i = 0;i<process.getListeHistoriqueRestitutionMassive().size();i++){
								%>
									<tr>
										<td class="sigp2NewTab-liste" style="text-align: center;"><%=process.getVAL_ST_RESTITUTION_JOURS(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_RESTITUTION_MOTIF(i)%></td>
										<td class="sigp2NewTab-liste"><%=process.getVAL_ST_RESTITUTION_NB_JOUR(i)%></td>
									</tr>
								<%}%>
							</table>	
						</div>
						<BR/><BR/>
						<%} %>
						
						<div style="text-align: center;">
							<INPUT type="submit" class="sigp2-Bouton-100" value="Fermer" name="<%=process.getNOM_PB_ANNULER()%>">
						</div>	
				</FIELDSET>
				<%} %>
				
			
			<%if(process.isAfficheSoldeAsaA48() || process.isAfficheSoldeAsaA54() || process.isAfficheSoldeAsaA55() || process.isAfficheSoldeAsaA52()|| process.isAfficheSoldeAsaAmicale()){ %>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Filtres pour les absences syndicales
				<SELECT onchange='executeBouton("<%=process.getNOM_PB_ANNEE() %>")'  class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
				</SELECT>
			</legend>
				    <table>
				    	<tr>
				    		<td>
							<%if(process.isAfficheSoldeAsaA48()){ %>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Réunion des membres du bureau directeur</legend>
									<table class="sigp2NewTab" style="text-align:left;width:200px;">
										<tr bgcolor="#EFEFEF">
											<td align="center">En cours</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A48()%></td>
											</tr>
									</table>				    
							    </FIELDSET>	
							<%}%>
				    		</td>
				    		<td>
							<%if(process.isAfficheSoldeAsaA54()){ %>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Congrès et conseil syndical</legend>
									<table class="sigp2NewTab" style="text-align:left;width:200px;">
										<tr bgcolor="#EFEFEF">
											<td align="center">En cours</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A54()%></td>
										</tr>
									</table>				    
							    </FIELDSET>
							<%}%>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
							<%if(process.isAfficheSoldeAsaA55()){ %>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Délégation DP</legend>
									<table class="sigp2NewTab" style="text-align:left;width:400px;">
										<tr bgcolor="#EFEFEF">
											<td align="center">En cours</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A55()%></td>
										</tr>
									</table>				    
							    </FIELDSET>
							<%}%>
				    		</td>
				    		<td>
							<%if(process.isAfficheSoldeAsaA52()){ %>
					    		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Décharge de service CTP</legend>
							    	
										<table class="sigp2NewTab" style="text-align:left;width:400px;">
											<tr bgcolor="#EFEFEF">
											<%if(process.getOrganisationAgent()!=null){%>
												<td align="center">En cours pour <%=process.getOrganisationAgent().getSigle() %></td>
											<%}else{ %>
												<td align="center">En cours pour OS</td>
											<%} %>
											</tr>
											<tr>
												<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_A52()%></td>
											</tr>
										</table>	    
							    </FIELDSET>
							<%} %>	
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
							<%if(process.isAfficheSoldeAsaAmicale()){ %>
					    		 <FIELDSET class="sigp2Fieldset" style="text-align:left;width:450px;">
							    	<legend class="sigp2Legend">* Amicale VDN</legend>
									<table class="sigp2NewTab" style="text-align:left;width:400px;">
										<tr bgcolor="#EFEFEF">
											<td align="center">En cours</td>
										</tr>
										<tr>
											<td style="text-align: center"><%=process.getVAL_ST_SOLDE_ASA_AMICALE()%></td>
										</tr>
									</table>				    
							    </FIELDSET>
							<%}%>
				    		</td>
				    		<td>&nbsp;</td>
				    	</tr>
				    </table>
			</FIELDSET>
		<%} %>
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Acteurs de l'agent</legend>
		    <table>
		    	<tr>
		    		<td>
		    		
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:300px;">
				    	<legend class="sigp2Legend">Opérateurs</legend>
						<table class="sigp2NewTab" style="text-align:left;width:250px;">
							<% for(int i = 0; i< process.getActeursDto().getListOperateurs().size();i++){ 
							
								AgentDto operateur = process.getActeursDto().getListOperateurs().get(i);
								
								if(null == operateur)
									continue;
							%>
							<tr>
								<td style="text-align: center"><%=operateur.getPrenom() + " " + operateur.getNom() + " (" + operateur.getIdAgent().toString().substring(3, operateur.getIdAgent().toString().length()) + ")" %></td>
							</tr>
							<% } %>
						</table>
					</FIELDSET>
					
		    		</td>
		    		<td>
		    		
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:300px;">
				    	<legend class="sigp2Legend">Viseurs</legend>
						<table class="sigp2NewTab" style="text-align:left;width:250px;">
							<% for(int i = 0; i< process.getActeursDto().getListViseurs().size();i++){ 
							
								AgentDto viseur = process.getActeursDto().getListViseurs().get(i);
								
								if(null == viseur)
									continue;
							%>
							<tr>
								<td style="text-align: center"><%=viseur.getPrenom() + " " + viseur.getNom() + " (" + viseur.getIdAgent().toString().substring(3, viseur.getIdAgent().toString().length()) + ")" %></td>
							</tr>
							<% } %>
						</table>
					</FIELDSET>
					
		    		</td>
		    	</tr>
		    	<tr>
		    		<td colspan="2">
		    		
					<FIELDSET class="sigp2Fieldset" style="text-align:left;width:600px;">
				    	<legend class="sigp2Legend">Approbateurs</legend>
						<table class="sigp2NewTab" style="text-align:left;width:500px;">
							<% for(int i = 0; i< process.getActeursDto().getListApprobateurs().size();i++){ 
							
								ApprobateurDto approbateur = process.getActeursDto().getListApprobateurs().get(i);
								
								if(null == approbateur)
									continue;
							%>
							<tr>
								<td style="text-align: center"><%=approbateur.getApprobateur().getPrenom() + " " + approbateur.getApprobateur().getNom() + " (" + approbateur.getApprobateur().getIdAgent().toString().substring(3, approbateur.getApprobateur().getIdAgent().toString().length()) + ")" %></td>
								<td style="text-align: center"><% if(null != approbateur.getDelegataire()) { %> (délégataire : <%=approbateur.getDelegataire().getPrenom() + " " + approbateur.getDelegataire().getNom() + " (" + approbateur.getDelegataire().getIdAgent().toString().substring(3, approbateur.getDelegataire().getIdAgent().toString().length()) + ")" %>) <% } %></td>
							</tr>
							<% } %>
						</table>
					</FIELDSET>
					
		    		</td>
		    	</tr>
		    </table>
		</FIELDSET>
		
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_ANNEE()%>" value="x">
		</FORM>
<%} %>	
	</BODY>
</HTML>