<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.metier.avancement.AvancementFonctionnaires"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK rel="stylesheet" href="theme/calendrier-mairie.css" type="text/css">
<SCRIPT type="text/javascript" src="js/GestionCalendrier.js"></SCRIPT>
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des avancements des fonctionnaires</TITLE>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
<script type="text/javascript" src="js/avancementPrepaCAP.js"></script>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>

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

//afin d'afficher la hiérarchie des services
function agrandirHierarchy() {

	hier = 	document.getElementById('treeHierarchy');

	if (hier.style.display!='none') {
		reduireHierarchy();
	} else {
		hier.style.display='block';
	}
}

//afin de cacher la hiérarchie des services
function reduireHierarchy() {
	hier = 	document.getElementById('treeHierarchy');
	hier.style.display='none';
}
  
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTFonctPrepaCAP"
 id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames('refAgent').location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Tri des avancements à afficher</legend>
			<span class="sigp2" style="width:75px">Année : </span>
			<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=70px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:75px">Filière : </span>
			<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FILIERE() %>" style="width=200px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_FILIERE(), process.getVAL_LB_FILIERE_SELECT()) %>
			</SELECT>
			<span class="sigp2" style="width:75px">Par agent :</span>
			<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" >
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          	<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          	<span class="sigp2" style="width:75px;">Service :</span>
				<INPUT tabindex="" id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
				<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">	
          		<INPUT type="hidden" id="codeservice" size="4" name="<%=process.getNOM_ST_CODE_SERVICE() %>" 
					value="<%=process.getVAL_ST_CODE_SERVICE() %>" class="sigp2-saisie">
				<div id="treeHierarchy" style="display: none;margin-left:500px;margin-top:20px; height: 340; width: 500; overflow:auto; background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;">
					<script type="text/javascript">
						d = new dTree('d');
						d.add(0,-1,"Services");
						
						<%
						String serviceSaisi = process.getVAL_EF_SERVICE().toUpperCase();
						int theNode = 0;
						for (int i =1; i <  process.getListeServices().size(); i++) {
							Service serv = (Service)process.getListeServices().get(i);
							String code = serv.getCodService();
							TreeHierarchy tree = (TreeHierarchy)process.getHTree().get(code);
							if (theNode ==0 && serviceSaisi.equals(tree.getService().getSigleService())) {
								theNode=tree.getIndex();
							}
						%>
						<%=tree.getJavaScriptLine()%>
						<%}%>
						document.write(d);
				
						d.closeAll();
						<% if (theNode !=0) { %>
							d.openTo(<%=theNode%>,true);
						<%}%>
					</script>
				</div>
          	<BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER()%>">
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des fonctionnaires</legend>
			<BR/>
				<table class="display" id="tabAvctFonct">
					<thead>
						<tr>
							<th>NumAvct</th>
							<th>Dir. <br> Sect.</th>
							<th>Nom <br> Prénom <br> Matr</th>
							<th>Cat <br> Filière</th>
							<th>Carr Simu</th>
							<th>Code grade <br> Ancien <br> Nouveau</th>
							<th>Libellé grade <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ancien&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br> Nouveau</th>
							<th>Date début</th>
							<th>Date Avct Mini <br> Moy <br> Maxi</th>	
							<th>Motif Avct <br> <br> Avis SHD</th>	
							<th>Durée VDN</th>	
							<th>Class. Ordre mérite</th>	
							<th>Verif SEF <br> 							
								<INPUT type="checkbox" name="CHECK_ALL_SEF" onClick='activeSEF("<%=process.getListeAvct().size() %>")'>
							</th>
							<th>Vérifié par</th>
						</tr>
					</thead>
					<tbody>
					<%
						for (int i = 0;i<process.getListeAvct().size();i++){
							AvancementFonctionnaires avct = (AvancementFonctionnaires) process.getListeAvct().get(i);
							Integer indiceAvct = Integer.valueOf(avct.getIdAvct());
					%>
							<tr>
								<td><%=process.getVAL_ST_NUM_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
								<td><%=process.getVAL_ST_AGENT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CATEGORIE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_CARRIERE_SIMU(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE(indiceAvct)%></td>
								<td><%=process.getVAL_ST_GRADE_LIB(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_DEBUT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
								<% if (process.getVAL_CK_VALID_SEF(indiceAvct).equals(process.getCHECKED_ON())){ %>
								<td>								
									<%if(!avct.getIdMotifAvct().equals(Const.CHAINE_VIDE) && avct.getIdMotifAvct().equals("5")){%>
										<SELECT disabled="disabled"  name="<%= process.getNOM_LB_AVIS_CAP_CLASSE(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_CLASSE(indiceAvct), process.getVAL_LB_AVIS_CAP_CLASSE_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else if( !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){ %>
										<SELECT disabled="disabled"  name="<%= process.getNOM_LB_AVIS_CAP_AD(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_AD(indiceAvct), process.getVAL_LB_AVIS_CAP_AD_SELECT(indiceAvct)) %>
										</SELECT>
									<%} else{%>&nbsp;
									<%} %>
								</td>
								<td>
									<%if(avct.getIdAvisCAP()!=null && !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){%>
										<%if(avct.getIdMotifAvct().equals("7") && (avct.getIdAvisCAP().equals("1")||avct.getIdAvisCAP().equals("3"))){%>
										<INPUT disabled="disabled" class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
										<%}else{%>
										&nbsp;
										<%} %>
									<%}else{ %>
										&nbsp;
									<%} %>
								</td>								
								<%}else{%>
								<td>								
									<%if( !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE) && avct.getIdMotifAvct().equals("5")){%>										
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP_CLASSE(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_CLASSE(indiceAvct), process.getVAL_LB_AVIS_CAP_CLASSE_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else if(!avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){ %>
										<SELECT onchange='activeOrdreMerite("<%=indiceAvct %>")' name="<%= process.getNOM_LB_AVIS_CAP_AD(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_AD(indiceAvct), process.getVAL_LB_AVIS_CAP_AD_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else{%>&nbsp;
									<%} %>
								</td>
								<td>
									<%if(avct.getIdAvisCAP()!=null && !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){%>
										<%if(avct.getIdMotifAvct().equals("7") && (avct.getIdAvisCAP().equals("1")||avct.getIdAvisCAP().equals("3"))){%>
										<INPUT class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
										<%}else{%>
										<INPUT style="visibility: hidden;" class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
										<%} %>
									<%}else{ %>
										<INPUT style="visibility: hidden;" class="sigp2-saisie" maxlength="2" name="<%= process.getNOM_EF_ORDRE_MERITE(indiceAvct) %>" size="2" type="text" value="<%= process.getVAL_EF_ORDRE_MERITE(indiceAvct) %>">
									<%} %>
								</td>							
								<%} %>
								
								<td>
								<%if(!avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){ %>
								<INPUT type="checkbox" onClick='validSEF("<%=indiceAvct %>")'  <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_SEF(indiceAvct),process.getVAL_CK_VALID_SEF(indiceAvct))%>></td>
								<%} %>
								<td><%=process.getVAL_ST_USER_VALID_SEF(indiceAvct)%></td>
								
							</tr>
					<%
						}					
					%>
					</tbody>
				</table>
				<% if (!process.agentEnErreur.equals("")){ %>
					<span class="sigp2Mandatory">Agents en anomalies : <%=process.agentEnErreur %></span>
					<BR/><BR/>
					<span class="sigp2Mandatory">Pour ces agents une ligne de carrière n'a pu être crée car il y avait déjà une carrière suivante de saisie. Merci de corriger manuellement les carrières de ces agents.</span>
				<%} %>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonct').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,{"bSearchable":false},{"bSearchable":false},null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false}],
							"sDom": '<"H"fl>t<"F"iT>',
							"sScrollY": "375px",
							"bPaginate": false,
							"oTableTools": {
								"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"avctFonctionnaires","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
								"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
							}
					    });
					} );
				</script>
			<BR/>
		</FIELDSET>

		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>

		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Impression des tableaux d'avancement différencié et des EAE</legend>
		    <BR/>
				<table class="display" id="tabAvctFonctImpr">
					<thead>
						<tr>
							<th>Code CAP</th>
							<th>Cadre Emploi</th>
							<th>Consulter</th>
							<th>Tableau<br> 							
								<INPUT type="checkbox" name="CHECK_ALL_TAB" onClick='activeTab("<%=process.getListeImpression().size() %>")'>
							</th>
							<th>Tableau + EAEs<br> 							
								<INPUT type="checkbox" name="CHECK_ALL_EAE" onClick='activeEae("<%=process.getListeImpression().size() %>")'>
							</th>
						</tr>
					</thead>
					<tbody>
					<%
					if (process.getListeImpression()!=null){
						for (int indiceImpr = 0;indiceImpr<process.getListeImpression().size();indiceImpr++){
					%>
							<tr>
								<td><%=process.getVAL_ST_CODE_CAP(indiceImpr)%></td>
								<td><%=process.getVAL_ST_CADRE_EMPLOI(indiceImpr)%></td>
								<td align="center" ><INPUT title="consulter" type="image" src="images/oeil.gif" height="15px" width="15px" class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "") %>" name="<%=process.getNOM_PB_CONSULTER_TABLEAU(indiceImpr,process.getVAL_ST_CODE_CAP(indiceImpr),process.getVAL_ST_CADRE_EMPLOI(indiceImpr))%>"></td>
								<td align="center" ><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_TAB(indiceImpr),process.getVAL_CK_TAB(indiceImpr))%>></td>
								<td align="center" ><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_EAE(indiceImpr),process.getVAL_CK_EAE(indiceImpr))%>></td>								
								
							</tr>
					<%
						}
					}
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonctImpr').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [null,null,null,null,null],
							"sDom": '<"H"l>t<"F"i>',
							"aaSorting": [[ 0, "asc" ]],
							"bPaginate": false
					    });
					} );
				</script>
				<BR/><BR/>
				<INPUT type="submit" class="sigp2-Bouton-200" value="Envoyer à l'impression" name="<%=process.getNOM_PB_IMPRIMER()%>">
			<BR/>
		</FIELDSET>
		<BR/>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Impressions</legend>
		    <BR/>
				<table class="display" id="tabAvctFonctImprJob">
					<thead>
						<tr>
							<th>Code CAP</th>
							<th>Cadre Emploi</th>
							<th>User</th>
							<th>Date</th>
							<th>Etat</th>
							<th>Avec EAEs</th>
						</tr>
					</thead>
					<tbody>
					<%
						for (int indiceImpr = 0;indiceImpr<process.getListeAvancementCapPrintJob().size();indiceImpr++){
					%>
							<tr>
								<td><%=process.getVAL_ST_CODE_CAP_JOB(indiceImpr)%></td>
								<td><%=process.getVAL_ST_CADRE_EMPLOI_JOB(indiceImpr)%></td>
								<td align="center" ><%=process.getVAL_ST_USER_JOB(indiceImpr)%></td>
								<td align="center" ><%=process.getVAL_ST_DATE_JOB(indiceImpr)%></td>
								<td align="center" ><%=process.getVAL_ST_ETAT_JOB(indiceImpr)%></td>
								<td align="center" ><%=process.getVAL_ST_TYPE_JOB(indiceImpr)%></td>								
							</tr>
					<%
						}					
					%>
					</tbody>
				</table>
				<script type="text/javascript">
					$(document).ready(function() {
					    $('#tabAvctFonctImprJob').dataTable({
							"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
							"aoColumns": [null,null,null,null,null,null],
							"sDom": '<"H"l>t<"F"i>',
							"aaSorting": [[ 3, "desc" ]],
							"bPaginate": false
					    });
					} );
				</script>
				<BR/><BR/>
			<BR/>
		</FIELDSET>
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENTEVALUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENTEVALUE">					
		<%=process.getUrlFichier()%>
	</FORM>
</BODY>
</HTML>