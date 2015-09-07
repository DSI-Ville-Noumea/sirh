<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.eae.EaeEvalue"%>
<%@page import="nc.mairie.metier.eae.EAE"%>
<%@page import="nc.mairie.enums.EnumEtatEAE"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.gestionagent.servlets.ServletAgent"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des EAE</TITLE>

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

//function pour changement couleur arriere plan ligne du tableau
function SelectLigne(id,tailleTableau)
{
	for (i=0; i<tailleTableau; i++){
 		document.getElementById(i).className="";
	} 
 	document.getElementById(id).className="selectLigne";
}

// afin de mettre le focus sur une zone précise
function setfocus(nom)
{
	if (document.formu.elements[nom] != null)
		document.formu.elements[nom].focus();
}
</SCRIPT>					
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTCampagneGestionEAE" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
	<script type="text/javascript">
	function activeMAJ() {						
			<%
			for (int j = 0;j<process.getListeEAE().size();j++){
				EAE eae = process.getListeEAE().get(j);
				Integer i = eae.getIdEae();
			%>
			var box = document.formu.elements['NOM_CK_VALID_MAJ_'+<%=i %>];  		
	  		if(document.formu.elements['CHECK_ALL_MAJ'].checked ){
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

	<BR/>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Choix de la campagne</legend>
			<span class="sigp2" style="width:50px">Année : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=55px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:40px">Etat : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_ETAT() %>" style="width=100px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ETAT(), process.getVAL_LB_ETAT_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:50px">Statut : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT() %>" style="width=150px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_STATUT(), process.getVAL_LB_STATUT_SELECT()) %>
			</SELECT>			
			<span class="sigp2" style="width:40px">CAP : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_CAP() %>" style="width=50px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_CAP(), process.getVAL_LB_CAP_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:50px;">Service :</span>
				<INPUT id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
          		<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" 
					value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">
				
	             	<div style="margin-left:500px;" class="sigp2">
					<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
					<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
					<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
					</div>
			<BR/><BR/>
			<span class="sigp2" style="width:100px">Par évaluateur :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_EVALUATEUR() %>" size="10" type="text" value="<%= process.getVAL_ST_AGENT_EVALUATEUR() %>" style="margin-right:10px;">
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_EVALUATEUR()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR()%>');">
          	<span class="sigp2" style="width:100px">Par évalué :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT_EVALUE() %>" size="10"  type="text" value="<%= process.getVAL_ST_AGENT_EVALUE() %>" style="margin-right:10px;">
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT_EVALUE()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE()%>');">
          	<span class="sigp2" style="width:70px">Détachés : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_AFFECTE() %>" style="width=50px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_AFFECTE(), process.getVAL_LB_AFFECTE_SELECT()) %>
			</SELECT>
			<BR/><BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_FILTRER()%>">		
			<INPUT type="submit" class="sigp2-Bouton-100" value="Mettre à jour" name="<%=process.getNOM_PB_METTRE_A_JOUR_EAE()%>">	
			<INPUT type="submit" class="sigp2-Bouton-150" value="Générer les EAEs en différé" name="<%=process.getNOM_PB_INITIALISE_CALCUL_EAE_JOB()%>">
			<BR/><BR/>
			<span class="sigp2Mandatory" style="width:1000px">Pour info : le bouton "mettre à jour" met à jour les informations de l'évalué (ainsi que sa CAP), de sa fiche de poste, de ses diplomes, de ses parcours pro. et de ses formations.</span>	
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des EAE</legend>
			<BR/>
			<table class="display" id="tabEAE" width="100%">
				<thead>
					<tr>
						<th>Direction <br> Section <br> Service</th>
						<th>Matr</th>
						<th>Nom <br> Prénom</th>
						<th>Statut <br> Dét. </th>
						<th>&nbsp;&nbsp;SHD&nbsp;&nbsp;</th>
						<th>Evaluateur(s)</th>
						<th>Délégataire</th>
						<th>CAP</th>
						<th>Avis Eval</th>
						<th>EAE joint</th>
						<th>Etat <br> Crée le <br> Finalisé le <br> Contrôlé le</th>
						<th>Dé-finaliser EAE</th>
						<th>Mettre à jour EAE
								<INPUT type="checkbox" name="CHECK_ALL_MAJ" onClick='activeMAJ()'>
						</th>
						<th>Contrôlé par</th>
						<th>Supp EAE</th>
						<th>Document Finalisé</th>
					</tr>
				</thead>
				<tbody>
				<%for (int i = 0;i<process.getListeEAE().size();i++){
				EAE eae = process.getListeEAE().get(i);
				Integer indiceAvct = eae.getIdEae();
				EaeEvalue eaeEvalue = process.getEaeEvalueDao().chercherEaeEvalue(eae.getIdEae());
				%>
						<tr>
							<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
							<td><%=process.getVAL_ST_MATRICULE_AGENT(indiceAvct)%></td>
							<td><%=process.getVAL_ST_AGENT(indiceAvct)%></td>
							<td><%=process.getVAL_ST_STATUT(indiceAvct)%></td>
							<td><%=process.getVAL_ST_SHD(indiceAvct)%></td>
							<td><%=process.getVAL_ST_EVALUATEURS(indiceAvct)%>							
							<%if(process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() && !eaeEvalue.isAgentDetache() &&  !eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.SUPPRIME.getCode())){ %>
							<INPUT title="gérer les évaluateurs" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/ajout.gif" height="15px" width="16px" name="<%=process.getNOM_PB_GERER_EVALUATEUR(indiceAvct)%>"></td>
							<%} %>
							<td><%=process.getVAL_ST_DELEGATAIRE(indiceAvct)%>
							<%if(!eaeEvalue.isAgentDetache()){ %>							
								<%if(process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte()&& !eae.getEtat().equals(EnumEtatEAE.NON_AFFECTE.getCode())&&!eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.SUPPRIME.getCode())){ %>
									<br/>
									<%if(eae.getIdDelegataire()==null){ %>
									<INPUT title="rechercher agent" type="image" src="images/loupe.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_RECHERCHER_AGENT(indiceAvct)%>">
					    			<%}else{ %>
					    			<INPUT title="supprimer agent" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT(indiceAvct)%>">
					    			<%} %>
								<%} %>	
							<%}else{ %>
								<%if(process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte()&&!eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.SUPPRIME.getCode())){ %>
									<br/>
									<%if(eae.getIdDelegataire()==null){ %>
									<INPUT title="rechercher agent" type="image" src="images/loupe.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_RECHERCHER_AGENT(indiceAvct)%>">
					    			<%}else{ %>
					    			<INPUT title="supprimer agent" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT(indiceAvct)%>">
					    			<%} %>
								<%} %>	
							<%} %>				
							</td>
							<td><%=process.getVAL_ST_CAP(indiceAvct)%></td>
							<td><%=process.getVAL_ST_AVIS_SHD(indiceAvct)%></td>
							<td><%=process.getVAL_ST_EAE_JOINT(indiceAvct)%></td>
							<td><%=process.getVAL_ST_CONTROLE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_ACTIONS_DEFINALISE(indiceAvct)%>
							<%if( process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() &&eae!=null && eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())){ %>
							<INPUT title="dé-finalisé EAE" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_DEFINALISE_EAE(indiceAvct)%>">
							<%} %>
							</td>
							<td><%=process.getVAL_ST_ACTIONS_MAJ(indiceAvct)%>
							<%if(process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() && eae!=null && !eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.SUPPRIME.getCode())){ %>
								<INPUT type="checkbox" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_MAJ(indiceAvct),process.getVAL_CK_VALID_MAJ(indiceAvct))%>>
							<%} %>
							</td>
							<td align="center"><%=process.getVAL_ST_CONTROLE_PAR(indiceAvct)%><BR/>
								<%if( process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() &&eae!=null && eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())){ %>
								<INPUT <%=eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) ? "disabled='disabled'" : "" %>type="checkbox" onclick='executeBouton("<%=process.getNOM_PB_VALID_EAE(indiceAvct)%>")' <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_EAE(indiceAvct),process.getVAL_CK_VALID_EAE(indiceAvct))%> >
								<INPUT type="submit" class="sigp2-displayNone" name="<%=process.getNOM_PB_VALID_EAE(indiceAvct)%>">		
								<%} %>
								<%if( process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() &&eae!=null && eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode())){ %>
								<INPUT title="dé-contôlé EAE" type="image" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" src="images/suppression.gif" height="16px" width="16px" name="<%=process.getNOM_PB_DEVALID_EAE(indiceAvct)%>">
								<%} %>
							</td>	
							<td>
							<%if( process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() &&eae!=null && !eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) && !eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode())&& !eae.getEtat().equals(EnumEtatEAE.SUPPRIME.getCode())){ %>
								<INPUT title="supprimer" type="image" src="images/suppression.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_SUPP_EAE(indiceAvct)%>">
							<%} %>
							<%if( process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() &&eae!=null && eae.getEtat().equals(EnumEtatEAE.SUPPRIME.getCode())){ %>
								<INPUT title="ajouter" type="image" src="images/ajout.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_DESUPP_EAE(indiceAvct)%>">
							<%} %>
							</td>
							<td>							
							<%if( process.getCampagneCourante()!=null && process.getCampagneCourante().estOuverte() &&eae!=null && (eae.getEtat().equals(EnumEtatEAE.FINALISE.getCode()) ||eae.getEtat().equals(EnumEtatEAE.CONTROLE.getCode()) )){ %>
								<INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_DOC(indiceAvct)%>">	
							<%} %>
							</td>
						</tr>
				<%}%>
				</tbody>
			</table>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabEAE').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [{"bSearchable":false},null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false,"bSortable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false}],                        
                        "sDom": '<"H"fl>t<"F"iT>',
						"sScrollY": "375px",
						"bPaginate": false,
						"oTableTools": {
							"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"gestionEAE","sFileName":"*.xls"}], //OU : "mColumns":[1,2,3,4]
							"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
						}
				    });
				} );
			</script>
			<BR/>	
		</FIELDSET>
		
		
       	<br/><br/>
        <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1000px;">	
        	<legend class="sigp2Legend">Erreurs eventuelles sur le calcul des EAEs</legend>
				<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<tr bgcolor="#EFEFEF">
						<td>Agent</td>
						<td>Date lancement</td>
						<td>Statut</td>
						<td>Erreur</td>
					</tr>
					<%
						for (int i = 0;i<process.getListeCampagneTask().size();i++){
							int indiceCarr = process.getListeCampagneTask().get(i).getIdCampagneTask();
					%>
					<tr id="<%=indiceCarr%>" onmouseover="SelectLigne(<%=indiceCarr%>,<%=process.getListeCampagneTask().size()%>)">
						<td class="sigp2NewTab-liste"><%=process.getVAL_ST_AGENT_TASK(indiceCarr)%></td>
						<td class="sigp2NewTab-liste"><%=process.getVAL_ST_DATE_TASK(indiceCarr)%></td>
						<td class="sigp2NewTab-liste"><%=process.getVAL_ST_STATUT_TASK(indiceCarr)%></td>
						<td class="sigp2NewTab-liste"><%=process.getVAL_ST_ERREUR_TASK(indiceCarr)%></td>
					</tr>
					<%}%>
				</table>	
		</FIELDSET>
		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_EVALUATEUR()%>" value="RECHERCHERAGENTEVALUATEUR">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUATEUR()%>" value="SUPPRECHERCHERAGENTEVALUATEUR">		
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT_EVALUE()%>" value="RECHERCHERAGENTEVALUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_EVALUE()%>" value="SUPPRECHERCHERAGENTEVALUE">
		
	<%=process.getUrlFichier()%>
		</FORM>
</BODY>
</HTML>