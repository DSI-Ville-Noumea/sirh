<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.avancement.AvancementConvCol"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<lINK rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Simulation des avancements des conventions collectives</TITLE>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
<script type="text/javascript" src="js/avancementConvCol.js"></script>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javascript" src="js/dtree.js"></SCRIPT>

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

// afin d'afficher la hi�rarchie des services
function agrandirHierarchy() {

	hier = 	document.getElementById('treeHierarchy');

	if (hier.style.display!='none') {
		reduireHierarchy();
	} else {
		hier.style.display='block';
	}
}

// afin de cacher la hi�rarchie des services
function reduireHierarchy() {
	hier = 	document.getElementById('treeHierarchy');
	hier.style.display='none';
}
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTMasseSalarialeConvention" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="window.parent.frames['refAgent'].location.reload();">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
<script type="text/javascript">
	function activeDRH() {						
			<%
			for (int j = 0;j<process.getListeAvct().size();j++){
				AvancementConvCol avct = process.getListeAvct().get(j);
				Integer i = avct.getIdAvct();
			%>
			var box = document.formu.elements['NOM_CK_VALID_DRH_'+<%=i%>];  		
	  		if(document.formu.elements['CHECK_ALL_DRH'].checked ){
	  			if(box!=null && !box.disabled){			
					box.checked=true; 
					validDRH(<%=i%>);  
				}			
	  		}else{
	  			if(box!=null && !box.disabled){		
					box.checked=false; 
					validDRH(<%=i%>);
				}
			}
			<%}%>
}
	function activeProjet() {						
		<%
		for (int j = 0;j<process.getListeAvct().size();j++){
			AvancementConvCol avct = process.getListeAvct().get(j);
			Integer i = avct.getIdAvct();
		%>
		var box = document.formu.elements['NOM_CK_PROJET_ARRETE_'+<%=i%>]; 
  		var boxDRH = document.formu.elements['NOM_CK_VALID_DRH_'+<%=i%>];   		
  		if(document.formu.elements['CHECK_ALL_PROJET'].checked && boxDRH!=null && boxDRH.checked){
  			if(box!=null && !box.disabled){		
				box.checked=true; 
				validProjet(<%=i%>);
			}
  		}else{
  			if(box!=null && !box.disabled){	
				box.checked=false; 
				validProjet(<%=i%>);
			}
		}
		<%}%>
}
	function activeAffecter() {						
		<%
		for (int j = 0;j<process.getListeAvct().size();j++){
			AvancementConvCol avct = process.getListeAvct().get(j);
			Integer i = avct.getIdAvct();
		%>
		var box = document.formu.elements['NOM_CK_AFFECTER_'+<%=i%>];  
  		var boxProjet = document.formu.elements['NOM_CK_PROJET_ARRETE_'+<%=i%>];  		
  		if(document.formu.elements['CHECK_ALL_AFFECTER'].checked && boxProjet!=null && boxProjet.checked){
  			if(box!=null && !box.disabled){	
				box.checked=true;  
				validAffecter(<%=i%>);
			}			
	  	}else{
  			if(box!=null && !box.disabled){	
				box.checked=false;	 
				validAffecter(<%=i%>);
			}		
		}
		<%}%>
}
</script>
		
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<BR/>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche avanc�e d'une fiche de poste">
				<LEGEND class="sigp2Legend">Simulation des avancements des conventions collectives</LEGEND>
				<BR/>
				<span class="sigp2Mandatory" style="width:70px;">Ann�e :</span>
				<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>">
					<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
				</SELECT>
				<BR/><BR/>
				<span class="sigp2" style="width:70px;">Service :</span>
				<INPUT id="service" class="sigp2-saisie" readonly="readonly"
					name="<%= process.getNOM_EF_SERVICE() %>" style="margin-right:10px;width:100px"
					type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"
					height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">
				<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
          		<INPUT type="hidden" id="codeservice" size="4" name="<%=process.getNOM_ST_CODE_SERVICE() %>" 
					value="<%=process.getVAL_ST_CODE_SERVICE() %>" class="sigp2-saisie">
				<div id="treeHierarchy" style="display: none; height: 340; width: 500; overflow:auto; background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;">
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
				<span class="sigp2" style="width:60px">Par agent :</span>
				<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
				<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          		<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          		<BR/><BR/><BR/>
				<INPUT type="submit" value="Lancer" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_LANCER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Afficher" name="<%=process.getNOM_PB_CHANGER_ANNEE()%>">	
				<BR>
			</FIELDSET>
			<FIELDSET class="sigp2Fieldset" style="text-align:left;">
		    <legend class="sigp2Legend">Gestion des avancements des conventions collectives</legend>
			<BR/>
			<table class="display" id="tabAvctConvColl">
				<thead>
					<tr>
						<th>NumAvct</th>
						<th>Dir. <br> Sect.</th>
						<th>Matr</th>
						<th>Nom <br> Pr�nom</th>
						<th>Date embauche</th>
						<th>PA</th>
						<th>Carr Simu</th>
						<th>Taux ancien <br> nouveau </th>
						<th>Grade</th>
						<th>Lib�ll�</th>
						<th>Valid. DRH						
							<INPUT type="checkbox" name="CHECK_ALL_DRH" onClick='activeDRH()'>
						</th>
						<th>Motif Avct</th>
						<th>Projet Arrete
							<INPUT type="checkbox" name="CHECK_ALL_PROJET" onClick='activeProjet()'>
						</th>
						<th>Num Arrete</th>
						<th>Date Arrete</th>
						<th>Affecter
							<INPUT type="checkbox" name="CHECK_ALL_AFFECTER" onClick='activeAffecter()'>
						</th>
					</tr>
				</thead>
				<tbody>
				<%
					for (int i = 0;i<process.getListeAvct().size();i++){
						AvancementConvCol avct = process.getListeAvct().get(i);
						Integer indiceAvct = avct.getIdAvct();
				%>
						<tr>
							<td><%=process.getVAL_ST_NUM_AVCT(indiceAvct)%></td>
							<td><%=process.getVAL_ST_DIRECTION(indiceAvct)%></td>
							<td><%=process.getVAL_ST_MATRICULE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_AGENT(indiceAvct)%></td>
							<td><%=process.getVAL_ST_DATE_EMBAUCHE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_PA(indiceAvct)%></td>
							<td><%=process.getVAL_ST_CARRIERE_SIMU(indiceAvct)%></td>
							<td><%=process.getVAL_ST_MONTANT_PRIME(indiceAvct)%></td>
							<td><%=process.getVAL_ST_GRADE(indiceAvct)%></td>
							<td><%=process.getVAL_ST_GRADE_LIB(indiceAvct)%></td>
							<% if (process.getVAL_CK_AFFECTER(indiceAvct).equals(process.getCHECKED_ON())){ %>
									<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
									<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT disabled="disabled" class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT disabled="disabled" class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>
									<%if(process.getVAL_ST_ETAT(indiceAvct).equals("E")){ %>
										<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> ></td>																
									<%}else{ %>
										<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>																
									<%} %>															
								<%}else if(process.getVAL_CK_PROJET_ARRETE(indiceAvct).equals(process.getCHECKED_ON())){ %>
									<td><INPUT type="checkbox" disabled="disabled" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>																
								<%}else if(process.getVAL_CK_VALID_DRH(indiceAvct).equals(process.getCHECKED_ON())){ %>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>
									<td><INPUT type="checkbox" style="visibility:hidden" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>								
								<%}else{ %>
									<td><INPUT type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
									<td><%=process.getVAL_ST_MOTIF_AVCT(indiceAvct)%></td>
									<td><INPUT type="checkbox" style="visibility:hidden" <%= process.forCheckBoxHTML(process.getNOM_CK_PROJET_ARRETE(indiceAvct),process.getVAL_CK_PROJET_ARRETE(indiceAvct))%> onClick='validProjet("<%=indiceAvct %>")'></td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_ARRETE(indiceAvct) %>" size="8" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_NUM_ARRETE(indiceAvct) %>">
									</td>
									<td>
										<INPUT class="sigp2-saisie" maxlength="10" name="<%= process.getNOM_EF_DATE_ARRETE(indiceAvct) %>" size="10" style="visibility:hidden"
											type="text" value="<%= process.getVAL_EF_DATE_ARRETE(indiceAvct) %>">
									</td>
									<td><INPUT type="checkbox" style="visibility:hidden" <%= process.forCheckBoxHTML(process.getNOM_CK_AFFECTER(indiceAvct),process.getVAL_CK_AFFECTER(indiceAvct))%> onClick='validAffecter("<%=indiceAvct %>")'></td>
								<%} %>
						</tr>
				<%
					}
				%>
				</tbody>
			</table>
			<script type="text/javascript">
				$(document).ready(function() {
				    $('#tabAvctConvColl').dataTable({
						"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
						"aoColumns": [{"bSearchable":false, "bVisible":false},{"bSearchable":false},null,null,{"bSortable":false,"bSearchable":false},{"bSearchable":false},{"bSearchable":false},{"bSortable":false,"bSearchable":false},null,null,{"bSearchable":false,"bSortable":false},{"bSearchable":false},{"bSearchable":false,"bSortable":false},{"bSearchable":false},{"bSearchable":false},{"bSearchable":false,"bSortable":false}],
						"sDom": '<"H"fl>t<"F"iT>',
						"sScrollY": "375px",
						"aaSorting": [[ 2, "asc" ]],
						"bPaginate": false,
						"oTableTools": {
							"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"avctCC","sFileName":"*.xls"}], //OU : "mColumns":[1,2,3,4]
							"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
						}
				    });
				} );
			</script>
			<BR/>
			<div align="center">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="G�n�rer" name="<%=process.getNOM_PB_AFFECTER()%>">
				<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
			</div>
		</FIELDSET>

		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
			</FIELDSET>

			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">
	</FORM>
</BODY>
</HTML>