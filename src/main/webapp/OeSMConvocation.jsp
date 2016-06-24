<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.suiviMedical.SuiviMedical"%>
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

		<TITLE>Convocation pour le suivi médical</TITLE>		

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
	<jsp:useBean class="nc.mairie.gestionagent.process.OeSMConvocation" id="process" scope="session"></jsp:useBean>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();" >
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<script type="text/javascript">
	function activeImprimerConvoc() {						
			<%
			for (int j = 0;j<process.getListeSuiviMed().size();j++){
				SuiviMedical sm = process.getListeSuiviMed().get(j);
				Integer i = sm.getIdSuiviMed();
			%>
			var box = document.formu.elements['NOM_CK_A_IMPRIMER_CONVOC_'+<%=i %>];  		
	  		if(document.formu.elements['CHECK_ALL_IMPRIMER_CONVOC'].checked){
	  			if(box!=null && !box.disabled){	
					box.checked=true;  
				}			
		  	}else{
	  			if(box!=null && !box.disabled){	
					box.checked=false;	
				}		
			}
			<%}%>
}
	function activeImprimerAccomp() {						
		<%
		for (int j = 0;j<process.getListeSuiviMed().size();j++){
			SuiviMedical sm = process.getListeSuiviMed().get(j);
			Integer i = sm.getIdSuiviMed();
		%>
		var box = document.formu.elements['NOM_CK_A_IMPRIMER_ACCOMP_'+<%=i %>];  		
  		if(document.formu.elements['CHECK_ALL_IMPRIMER_ACCOMP'].checked){
  			if(box!=null && !box.disabled){	
				box.checked=true;  
			}			
	  	}else{
  			if(box!=null && !box.disabled){	
				box.checked=false;	
			}		
		}
		<%}%>
}
</script>
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
					<span class="sigp2Mandatory">Ces agents ne sont ni fonctionnaire ni convention collective. Merci de corriger manuellement les carrières de ces agents.</span>
		 		</td>
		 	</TR>
		 	</TBODY>					
		</TABLE>
		</FIELDSET>
		<BR/>
		<BR/>
		<%} %>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Prévisions des visites médicales du travail</legend>
		
            <span class="sigp2Mandatory">Date début : </span>
			<input id="<%=process.getNOM_ST_DATE_MIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MIN()%>" >
			<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MIN()%>', 'dd/mm/y');">
			<span class="sigp2">Date fin : </span>
			<input id="<%=process.getNOM_ST_DATE_MAX()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_MAX()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_MAX()%>" >
			<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_MAX()%>', 'dd/mm/y');">
			<span class="sigp2" style="width:40px">Statut : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT() %>" style="width=50px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_STATUT(), process.getVAL_LB_STATUT_SELECT()) %>
			</SELECT>
			<span class="sigp2">Agent en CDD :</span>
			<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AGENT_CDD(),process.getVAL_CK_AGENT_CDD())%>><span  class="sigp2">Oui</span>
			
			<BR/>
            <span class="sigp2" style="width:75px">Par supérieur hiérarchique :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_HIERARCHIQUE() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT_HIERARCHIQUE() %>" >
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_HIERARCHIQUE()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE()%>');">
          	
          	<span class="sigp2" style="width:75px">Par agent :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" >
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          	
          	<span class="sigp2" style="width:75px;">Service :</span>
			<INPUT id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
			<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
			<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
          	<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" 
					value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">				
                <div style="margin-left:400px;" class="sigp2">
				<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
				<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
				<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
				</div>
			<BR/>
          	
          	<INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_RECHERCHER()%>">
		</FIELDSET>
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Calcul des prévisions des visites médicales du travail</legend>
			<span class="sigp2" style="width:75px">Mois - Année : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_MOIS() %>" style="width=140px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_MOIS(), process.getVAL_LB_MOIS_SELECT()) %>
			</SELECT>
			<INPUT type="submit" class="sigp2-Bouton-250" value="Calculer pour le mois sélectionné" name="<%=process.getNOM_PB_CALCULER()%>">
		</FIELDSET>
		
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;height:530px;">
		    <BR/>
			<legend class="sigp2Legend">Gestion des prévisions</legend>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-200" value="Générer les convocations" name="<%=process.getNOM_PB_IMPRIMER_CONVOCATIONS()%>">
			<INPUT type="submit" class="sigp2-Bouton-250" value="Générer les lettres d'accompagnement" name="<%=process.getNOM_PB_IMPRIMER_LETTRES_ACCOMPAGNEMENTS()%>">
			<BR/><BR/>
			<div>
				<table class="display" id="tabSuiviMed">
					<thead>
						<tr>
							<th>NumSuiviMed</th>
							<th>Matr.</th>
							<th>Agent</th>
							<th>Cafat</th>
							<th>Statut</th>
							<th>Serv.</th>
							<th>Date derniere visite</th>
							<th>Prévision de visite</th>
							<th>Motif</th>
							<th>Nb visites ratées</th>
							<th>Actions</th>
							<th>Medecin</th>
							<th>Date prochain RDV</th>
							<th>Heure prochain RDV</th>
							<th>Imprimer convoc.							
								<INPUT type="checkbox" name="CHECK_ALL_IMPRIMER_CONVOC" onClick='activeImprimerConvoc()'>
							</th>
							<th>Imprimer accomp.
								<INPUT type="checkbox" name="CHECK_ALL_IMPRIMER_ACCOMP" onClick='activeImprimerAccomp()'>
							</th>
							<th style="display: none;" >Etat</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeSuiviMed()!=null){
						for (int i = 0;i<process.getListeSuiviMed().size();i++){
							SuiviMedical sm = process.getListeSuiviMed().get(i);
							Integer indiceSM = sm.getIdSuiviMed();
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_SM(indiceSM)%></td>
								<td><%=process.getVAL_ST_MATR(indiceSM)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceSM)%></td>
								<td><%=process.getVAL_ST_NUM_CAFAT(indiceSM)%></td>
								<td><%=process.getVAL_ST_STATUT(indiceSM)%></td>
								<td><%=process.getVAL_ST_SERVICE(indiceSM)%></td>
								<td><%=process.getVAL_ST_DATE_DERNIERE_VISITE(indiceSM)%></td>					
								<td><%=process.getVAL_ST_DATE_PREVISION_VISITE(indiceSM)%></td>							
								<td><%=process.getVAL_ST_MOTIF(indiceSM)%></td>						
								<td><%=process.getVAL_ST_NB_VISITES_RATEES(indiceSM)%></td>	
								<td>
								<INPUT title="modifier" type="image" src="images/modifier.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_MODIFIER(indiceSM)%>">
				    			<%if(!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.TRAVAIL.getCode())){ %>
				    				<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER(indiceSM)%>">
				    			<%} %>
				    			</td>	
				    			<%if(process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.TRAVAIL.getCode())){ %>
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
									<SELECT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getCode()) ?  "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_MEDECIN(indiceSM) %>" class="sigp2-liste">
										<%=process.forComboHTML(process.getVAL_LB_MEDECIN(indiceSM), process.getVAL_LB_MEDECIN_SELECT(indiceSM)) %>
									</SELECT>
								</td>										
								<td>
									<input id="<%=process.getNOM_ST_DATE_PROCHAIN_RDV(indiceSM)%>" <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getCode())  ?  "disabled='disabled'" : "" %> class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_PROCHAIN_RDV(indiceSM) %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_PROCHAIN_RDV(indiceSM) %>">
									<%if(!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode())&&!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getCode())){ %>
									<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_PROCHAIN_RDV(indiceSM)%>', 'dd/mm/y');">
									<%} %>
								</td>				
								<td>
									<SELECT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getCode())  ?  "disabled='disabled'" : "" %> name="<%= process.getNOM_LB_HEURE_RDV(indiceSM) %>" class="sigp2-liste">
										<%=process.forComboHTML(process.getVAL_LB_HEURE_RDV(indiceSM), process.getVAL_LB_HEURE_RDV_SELECT(indiceSM)) %>
									</SELECT>
								</td>	
								<td>
									<INPUT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode())||process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getCode())  ?  "disabled='disabled'" : "" %> type="checkbox" onClick='validConvoque("<%=indiceSM %>")' <%= process.forCheckBoxHTML(process.getNOM_CK_A_IMPRIMER_CONVOC(indiceSM),process.getVAL_CK_A_IMPRIMER_CONVOC(indiceSM))%> >
								</td>		
								<td>
									<INPUT <%= process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode()) || (!process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.ACCOMP.getCode()) && !process.getVAL_ST_ETAT(indiceSM).equals(EnumEtatSuiviMed.CONVOQUE.getCode()))  ?  "disabled='disabled'" : "" %> type="checkbox" onClick='validAccomp("<%=indiceSM %>")' <%= process.forCheckBoxHTML(process.getNOM_CK_A_IMPRIMER_ACCOMP(indiceSM),process.getVAL_CK_A_IMPRIMER_ACCOMP(indiceSM))%> >
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
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,null,null,null,null,null,null,null,null,null,null,null,{"bSearchable":false,"bSortable":false},{"bSearchable":false,"bSortable":false},null],
							"sDom": '<"H"flT>t<"F"iT>',
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

		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		<legend class="sigp2Legend">Documents générés pour le mois sélectionné</legend>	
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
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENTEVALUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENTEVALUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_HIERARCHIQUE()%>" value="RECHERCHERAGENTHIERARCHIQUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_HIERARCHIQUE()%>" value="SUPPRECHERCHERAGENTHIERARCHIQUE">
	</FORM>
	</BODY>
</HTML>