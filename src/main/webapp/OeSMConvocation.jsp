<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumEtatSuiviMed"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
	
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">

<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT> 

		<TITLE>Convocation pour le suivi m�dical</TITLE>		

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
<script type="text/javascript" src="js/suiviMed.js"></script>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="JavaScript">
		//afin de s�lectionner un �l�ment dans une liste
		function executeBouton(nom)
		{
			document.formu.elements[nom].click();
		}

		// afin de mettre le focus sur une zone pr�cise
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
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<jsp:useBean class="nc.mairie.gestionagent.process.OeSMConvocation" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<% if (!process.convocationsEnErreur.equals("")){ %>
		 <FIELDSET style="border-color : red red red red;">
		 <TABLE border="0" width="100%" cellpadding="0" cellspacing="0">
		 	<TBODY>
		 	<TR>
		 		<td width="30" class="sigp2-titre"><IMG src="images/info.gif" width="20" height="20" border="0"></td>
		 		<td valign="middle" class="sigp2-titre">
		 			<span class="sigp2Mandatory">Agents en anomalies : <%=process.convocationsEnErreur %></span>
					<BR/>
					<span class="sigp2Mandatory">Ces agents ne sont ni fonctionnaire ni convention collective. Merci de corriger manuellement les carri�res de ces agents.</span>
		 		</td>
		 	</TR>
		 	</TBODY>					
		</TABLE>
		</FIELDSET>
		<BR/>
		<BR/>
		<%} %>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Pr�visions des visites m�dicales du travail</legend>
			<span class="sigp2" style="width:75px">Mois - Ann�e : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOIS() %>" style="width=140px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_MOIS(), process.getVAL_LB_MOIS_SELECT()) %>
			</SELECT>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_RECHERCHER()%>">
			<INPUT type="submit" class="sigp2-Bouton-250" value="Calculer pour le mois s�lectionn�" name="<%=process.getNOM_PB_CALCULER()%>">
		</FIELDSET>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;height:530px;">
		    <legend class="sigp2Legend">Gestion des pr�visions</legend>
			<BR/>
			<div>
				<table class="display" id="tabSuiviMed">
					<thead>
						<tr>
							<th>NumSuiviMed</th>
							<th>Matr.</th>
							<th>Agent</th>
							<th>Statut</th>
							<th>Serv.</th>
							<th>Date derniere visite</th>
							<th>Pr�vision de visite</th>
							<th>Motif</th>
							<th>Nb visites rat�es</th>
							<th>Actions</th>
							<th>Medecin</th>
							<th>Date prochain RDV</th>
							<th>Heure prochain RDV</th>
							<th>Imprimer convoc.							
								<INPUT type="checkbox" name="CHECK_ALL_IMPRIMER_CONVOC" onClick='activeImprimerConvoc("<%=process.getListeSuiviMed().size() %>")'>
							</th>
							<th>Imprimer accomp.
								<INPUT type="checkbox" name="CHECK_ALL_IMPRIMER_ACCOMP" onClick='activeImprimerAccomp("<%=process.getListeSuiviMed().size() %>")'>
							</th>
							<th style="display: none;" >Etat</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeSuiviMed()!=null){
						for (int indiceSM = 0;indiceSM<process.getListeSuiviMed().size();indiceSM++){
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_SM(indiceSM)%></td>
								<td><%=process.getVAL_ST_MATR(indiceSM)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceSM)%></td>
								<td><%=process.getVAL_ST_STATUT(indiceSM)%></td>
								<td><%=process.getVAL_ST_SERVICE(indiceSM)%></td>
								<td><%=process.getVAL_ST_DATE_DERNIERE_VISITE(indiceSM)%></td>					
								<td><%=process.getVAL_ST_DATE_PREVISION_VISITE(indiceSM)%></td>							
								<td><%=process.getVAL_ST_MOTIF(indiceSM)%></td>						
								<td><%=process.getVAL_ST_NB_VISITES_RATEES(indiceSM)%></td>	
								<td>
								<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceSM)%>">
				    			<%if(!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.TRAVAIL.getValue())){ %>
				    				<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceSM)%>">
				    			<%} %>
				    			</td>	
				    			<%if(process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.TRAVAIL.getValue())){ %>
				    			<td>&nbsp;</td>					
								<td>&nbsp;</td>					
								<td>&nbsp;</td>	
								<td>
									<INPUT disabled="disabled" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_A_IMPRIMER_CONVOC(indiceSM),process.getVAL_CK_A_IMPRIMER_CONVOC(indiceSM))%>>
								</td>		
								<td>
									<INPUT disabled="disabled" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_A_IMPRIMER_ACCOMP(indiceSM),process.getVAL_CK_A_IMPRIMER_ACCOMP(indiceSM))%>>
								</td>
								<%}else{ %>
				    			<td>
									<SELECT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getValue()) ?  "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_MEDECIN(indiceSM) %>" class="sigp2-liste">
										<%=process.forComboHTML(process.getVAL_LB_MEDECIN(indiceSM), process.getVAL_LB_MEDECIN_SELECT(indiceSM)) %>
									</SELECT>
								</td>										
								<td>
									<input <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getValue())  ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_PROCHAIN_RDV(indiceSM) %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_PROCHAIN_RDV(indiceSM) %>">
									<%if(!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue())&&!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getValue())){ %>
									<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_PROCHAIN_RDV(indiceSM)%>', 'dd/mm/y');">
									<%} %>
								</td>				
								<td>
									<SELECT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getValue())  ?  "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_HEURE_RDV(indiceSM) %>" class="sigp2-liste">
										<%=process.forComboHTML(process.getVAL_LB_HEURE_RDV(indiceSM), process.getVAL_LB_HEURE_RDV_SELECT(indiceSM)) %>
									</SELECT>
								</td>	
								<td>
									<INPUT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getValue())  ?  "disabled='disabled'" : "" %> type="checkbox" onClick='validConvoque("<%=indiceSM %>")' <%= process.forCheckBoxHTML(process.getNOM_CK_A_IMPRIMER_CONVOC(indiceSM),process.getVAL_CK_A_IMPRIMER_CONVOC(indiceSM))%> >
								</td>		
								<td>
									<INPUT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue()) || (!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getValue()) && !process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getValue()))  ?  "disabled='disabled'" : "" %> type="checkbox" onClick='validAccomp("<%=indiceSM %>")' <%= process.forCheckBoxHTML(process.getNOM_CK_A_IMPRIMER_ACCOMP(indiceSM),process.getVAL_CK_A_IMPRIMER_ACCOMP(indiceSM))%> >
								</td>
				    			<%} %>	
				    			<td style="display: none;">
				    				<input style="visibility:hidden" class="sigp2-saisie" maxlength="2"	name="<%= process.getNOM_ST_ETAT(indiceSM) %>" size="2" type="text"	value="<%= process.getVAL_ST_ETAT(indiceSM) %>">
								</td>
							</tr>
					<%
						}
					}
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabSuiviMed').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],
							"sDom": '<"H"fl>t<"F"iT>',
							"bPaginate": false,
							"oTableTools": {
								"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"suiviMedical","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
								"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
							}
					    });
					} );
				</script>
			</div>
			<BR/>
		</FIELDSET>

		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-200" value="G�n�rer les convocations" name="<%=process.getNOM_PB_IMPRIMER_CONVOCATIONS()%>">
			<INPUT type="submit" class="sigp2-Bouton-250" value="G�n�rer les lettres d'accompagnement" name="<%=process.getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS()%>">
		</FIELDSET>

		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Documents g�n�r�s pour le mois s�lectionn�</legend>	
		<br/>
			<span style="position:relative;width:35px;"></span>
			<span style="position:relative;text-align: left;">Nom Document</span>
			<br/>
			<div style="overflow: auto;height:150px;width:1000px;margin-right: 0px;margin-left: 0px;">
				<table class="sigp2NewTab" style="text-align:left;width:980px;">
				<%int indiceDoc = 0;
				if (process.getListeDocuments()!=null){
				for (int i = 0;i<process.getListeDocuments().size();i++){
				%>
					<tr id="<%=indiceDoc%>" onmouseover="SelectLigne(<%=indiceDoc%>,<%=process.getListeDocuments().size()%>)">
						<td class="sigp2NewTab-liste" style="position:relative;width:30px;" align="center">
							<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_VISUALISATION(indiceDoc)%>">				
						</td>
						<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_NOM_DOC(indiceDoc)%></td>
					</tr>
				<%indiceDoc++;
				}
				}%>
				</table>	
			</div>
		</FIELDSET>
			<br/>
	<%=process.getUrlFichier()%>
	</FORM>
	</BODY>
</HTML>