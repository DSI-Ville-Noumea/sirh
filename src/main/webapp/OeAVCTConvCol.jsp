<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="nc.mairie.metier.avancement.AvancementConvCol"%>
<%@page import="nc.mairie.utils.MairieUtils"%>
<%@page import="nc.mairie.enums.EnumTypeDroit"%>
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
<TITLE>Gestion des avancements des conventions collectives</TITLE>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
<script type="text/javascript" src="js/avancementConvCol.js"></script>
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
</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean
 class="nc.mairie.gestionagent.process.avancement.OeAVCTConvCol"
 id="process" scope="session"></jsp:useBean>
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
		
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Tri des avancements � afficher</legend>
			<span class="sigp2" style="width:75px">Ann�e : </span>
			<SELECT disabled="disabled" class="sigp2-saisie" name="<%= process.getNOM_LB_ANNEE() %>" style="width=70px;margin-right:20px;">
				<%=process.forComboHTML(process.getVAL_LB_ANNEE(), process.getVAL_LB_ANNEE_SELECT()) %>
			</SELECT>
			<INPUT type="submit" class="sigp2-Bouton-100" value="Changer" name="<%=process.getNOM_PB_CHANGER_ANNEE()%>">			
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
									<td><INPUT type="checkbox" disabled="disabled" checked="checked" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
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
									<td><INPUT type="checkbox" disabled="disabled" checked="checked" <%= process.forCheckBoxHTML(process.getNOM_CK_VALID_DRH(indiceAvct),process.getVAL_CK_VALID_DRH(indiceAvct))%> onClick='validDRH("<%=indiceAvct %>")'></td>								
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
			
				<% if (!process.agentEnErreur.equals("")){ %>
					<span style="color: red;" class="sigp2Mandatory">Agents en anomalies : <%=process.agentEnErreur %></span>
					<BR/><BR/>
					<span style="color: red;" class="sigp2Mandatory">Pour ces agents une ligne de prime n'a pu �tre cr�e car il y avait d�j� une prime pour cette date. Merci de corriger manuellement les primes de ces agents.</span>
				<%} %>
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
		</FIELDSET>

		<FIELDSET class="<%= MairieUtils.getNomClasseCSS(request, process.getNomEcran(), EnumTypeDroit.EDITION, "sigp2Fieldset") %>" style="text-align:center;width:1030px;">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Enregistrer" name="<%=process.getNOM_PB_VALIDER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="G�n�rer" name="<%=process.getNOM_PB_AFFECTER()%>">
			<INPUT type="submit" class="sigp2-Bouton-100" value="Annuler" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>
	</FORM>
</BODY>
</HTML>