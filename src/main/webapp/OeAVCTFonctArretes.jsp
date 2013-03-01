<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.enums.EnumEtatAvancement"%>
<%@page import="nc.mairie.metier.Const"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.metier.avancement.AvancementFonctionnaires"%>
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
<script type="text/javascript" src="js/avancementArr.js"></script>
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
 class="nc.mairie.gestionagent.process.avancement.OeAVCTFonctArretes"
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
          	<span class="sigp2" style="width:100px">Date de la CAP : </span>
			<input class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_CAP_GLOBALE() %>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_CAP_GLOBALE() %>" >
			<IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_CAP_GLOBALE()%>', 'dd/mm/y');">
			<BR/><BR/>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Filtrer" name="<%=process.getNOM_PB_FILTRER()%>">
		</FIELDSET>
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des fonctionnaires</legend>
			<BR/>
				<table class="display" id="tabAvctFonct">
					<thead>
						<tr>
							<th>NumAvct</th>
							<th>Dir.<br> Sect.</th>
							<th>Nom <br> Prénom <br> Matr</th>
							<th>Cat <br> Filière</th>
							<th>Carr Simu</th>
							<th>Code grade <br> Ancien <br> Nouveau</th>
							<th>Libel. grade <br> Ancien <br> Nouveau</th>
							<th>Date Avct Mini <br> Moy <br> Maxi</th>
							<th>Prop avct <br> Type <br> SHD <br> VDN</th>
							<th>Date CAP</th>
							<th>Avis CAP <br> Decision Employeur</th>	
							<th>Observations</th>
							<th>Verif SGC</th>	
							<th>Date Avct</th>
							<th>A imprimer</th>	
							<th>Imprimé le <br> A <br> PAR</th>						
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
								<td><%=process.getVAL_ST_DATE_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
								<td><%=process.getVAL_ST_DATE_CAP(indiceAvct)%></td>
								<% if (process.getVAL_CK_VALID_ARR(indiceAvct).equals(process.getCHECKED_ON()) || process.getVAL_CK_VALID_ARR_IMPR(indiceAvct).equals(process.getCHECKED_ON())){ %>
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
									<br/>							
									<%if(!avct.getIdMotifAvct().equals(Const.CHAINE_VIDE) && avct.getIdMotifAvct().equals("5")){%>
										<SELECT disabled="disabled"  name="<%= process.getNOM_LB_AVIS_CAP_CLASSE_EMP(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_CLASSE_EMP(indiceAvct), process.getVAL_LB_AVIS_CAP_CLASSE_EMP_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else if( !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){ %>
										<SELECT disabled="disabled"  name="<%= process.getNOM_LB_AVIS_CAP_AD_EMP(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_AD_EMP(indiceAvct), process.getVAL_LB_AVIS_CAP_AD_EMP_SELECT(indiceAvct)) %>
										</SELECT>
									<%} else{%>&nbsp;
									<%} %>
								</td>
								<td>
									<textarea readonly="readonly" rows="3" cols="30" class="sigp2-saisie" name="<%= process.getNOM_ST_OBSERVATION(indiceAvct)%>" ><%= process.getVAL_ST_OBSERVATION(indiceAvct) %></textarea>
								</td>
								<%}else{%>
								<td>								
									<%if( !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE) && avct.getIdMotifAvct().equals("5")){%>										
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP_CLASSE(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_CLASSE(indiceAvct), process.getVAL_LB_AVIS_CAP_CLASSE_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else if(!avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){ %>
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP_AD(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_AD(indiceAvct), process.getVAL_LB_AVIS_CAP_AD_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else{%>&nbsp;
									<%} %>	
									<br/>							
									<%if( !avct.getIdMotifAvct().equals(Const.CHAINE_VIDE) && avct.getIdMotifAvct().equals("5")){%>										
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP_CLASSE_EMP(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_CLASSE_EMP(indiceAvct), process.getVAL_LB_AVIS_CAP_CLASSE_EMP_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else if(!avct.getIdMotifAvct().equals(Const.CHAINE_VIDE)){ %>
										<SELECT name="<%= process.getNOM_LB_AVIS_CAP_AD_EMP(indiceAvct) %>" class="sigp2-liste" >
												<%=process.forComboHTML(process.getVAL_LB_AVIS_CAP_AD_EMP(indiceAvct), process.getVAL_LB_AVIS_CAP_AD_EMP_SELECT(indiceAvct)) %>
										</SELECT>
									<%}else{%>&nbsp;
									<%} %>
								</td>
								<td>
									<textarea rows="3" cols="30" class="sigp2-saisie" name="<%= process.getNOM_ST_OBSERVATION(indiceAvct)%>" ><%= process.getVAL_ST_OBSERVATION(indiceAvct) %></textarea>
								</td>
								<%} %>
								<td>
									<%if( avct.getEtat().equals(EnumEtatAvancement.ARRETE_IMPRIME.getValue())){%>	
									<INPUT style="visibility: hidden;" type="checkbox" onClick='validArr("<%=indiceAvct %>");executeBouton("<%=process.getNOM_PB_SET_DATE_AVCT(indiceAvct) %>")'  <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR(indiceAvct),process.getVAL_CK_VALID_ARR(indiceAvct))%>>
									<%}else{%>
									<INPUT style="visibility: visible;"type="checkbox" onClick='validArr("<%=indiceAvct %>");executeBouton("<%=process.getNOM_PB_SET_DATE_AVCT(indiceAvct) %>")'  <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR(indiceAvct),process.getVAL_CK_VALID_ARR(indiceAvct))%>>
									<%} %>
									<INPUT type="submit" style="visibility : hidden;width: 5px" name="<%=process.getNOM_PB_SET_DATE_AVCT(indiceAvct)%>" value="DATE">
								</td>
								<td><%=process.getVAL_ST_DATE_AVCT_FINALE(indiceAvct)%></td>
								<td>								
									<%if( avct.getEtat().equals(EnumEtatAvancement.ARRETE.getValue())){%>	
									<INPUT style="visibility: visible;" type="checkbox" onClick='validArrImpr("<%=indiceAvct %>")'  <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR_IMPR(indiceAvct),process.getVAL_CK_VALID_ARR_IMPR(indiceAvct))%>>
									<%}else{%>
									<INPUT style="visibility: hidden;" type="checkbox" onClick='validArrImpr("<%=indiceAvct %>")'  <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_ARR_IMPR(indiceAvct),process.getVAL_CK_VALID_ARR_IMPR(indiceAvct))%>>
									<%} %>
								</td>
								<td><%=process.getVAL_ST_USER_VALID_ARR_IMPR(indiceAvct)%></td>
								
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
							"aoColumns": [{"bSearchable":false, "bVisible":false},null,null,{"bSearchable":false},{"bSearchable":false},null,null,{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false}],
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
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENTEVALUE">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENTEVALUE">
	</FORM>
</BODY>
</HTML>