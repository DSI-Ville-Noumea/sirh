<%@page import="nc.mairie.metier.poste.FichePoste"%>
<%@ page contentType="text/html; charset=UTF-8" %> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.metier.poste.TitrePoste"%>
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFPRechercheAvancee" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<lINK rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
		<LINK href="TableTools-2.0.1/media/css/TableTools.css" rel="stylesheet" type="text/css">
		<TITLE>Sélection d'une fiche de poste</TITLE>
		
		<SCRIPT type="text/javascript" src="js/jquery-1.6.2.min.js"></SCRIPT>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="TableTools-2.0.1/media/js/TableTools.min.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<SCRIPT type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></SCRIPT>
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
		</SCRIPT>
		<%
		ArrayList<TitrePoste> listeTitres = process.getListeTitre();
		
		String res = 	"<script language=\"javascript\">\n"+
				"var availableTitres = new Array(\n";
		
		for (int i = 0; i < listeTitres.size(); i++){
			res+= "   \""+((TitrePoste)listeTitres.get(i)).getLibTitrePoste()+"\"";
			if (i+1 < listeTitres.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		%>
		<%=res%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listeTitrePoste").autocomplete({source:availableTitres
				});
			});
			
			$(document).ready(function() {
				$('#tabFDP').dataTable({
				"oLanguage": {"sUrl": "media/dataTables/language/fr_FR.txt"},
				"aoColumns": [null,null,null,null],
				"sDom": '<"H"fl>t<"F"iT>',
				"sScrollY": "375px",
				"bPaginate": false,
				"oTableTools": {
					"aButtons": [{"sExtends":"xls","sButtonText":"Export Excel","mColumns":"visible","sTitle":"rechercheFDP","sFileName":"*.xls"}], //OU : "mColumns":[0,1,2,3,4]
					"sSwfPath": "TableTools-2.0.1/media/swf/copy_cvs_xls_pdf.swf"
					}
				});
			} );
		</script>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%= process.getFocus() %>')">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche avancée d'une fiche de poste">
				<LEGEND class="sigp2Legend">Recherche avancée d'une fiche de poste</LEGEND>
				<table>
					<tr>
						<td width="70px">
							<span class="sigp2">Numero :</span>
						</td>
						<td width="200px">
							<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_FICHE_POSTE() %>" size="10" type="text" value="<%= process.getVAL_EF_NUM_FICHE_POSTE() %>" style="margin-right:10px;">
						</td>
						<td width="100px">
							<span class="sigp2">Recherche agent :</span>
						</td>
						<td width="200px">
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
							<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
			          		<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
						</td>
						<td width="100px">
							<span class="sigp2">Service :</span>
						</td>
						<td class="sigp2">
							<INPUT id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
							<span class="sigp2Mandatory" style="width:60px;">
								<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence" height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">
								<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>');">
							</span>
							<INPUT type="hidden" id="idServiceADS" size="4" name="<%=process.getNOM_ST_ID_SERVICE_ADS() %>" value="<%=process.getVAL_ST_ID_SERVICE_ADS() %>" class="sigp2-saisie">
							<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_WITH_SERVICE_ENFANT(),process.getVAL_CK_WITH_SERVICE_ENFANT())%>>Avec entités enfants									
											
							<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->
							<%=process.getCurrentWholeTreeJS(process.getVAL_EF_SERVICE().toUpperCase()) %>
							<!-- ////////// ARBRE DES SERVICES - ADS ///////////// -->	
						</td>
					</tr>
					<tr>
						<td>
							<span class="sigp2">Statut : </span>
						</td>
						<td>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT() %>">
								<%=process.forComboHTML(process.getVAL_LB_STATUT(), process.getVAL_LB_STATUT_SELECT()) %>
							</SELECT>
						</td>
						<td>
							<span class="sigp2">Nom agent :</span>
						</td>
						<td>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_NOM_AGENT() %>" size="10" type="text" value="<%= process.getVAL_ST_NOM_AGENT() %>" style="margin-right:10px;">
						</td>
						<td>
							<span class="sigp2">Statut service :</span>
						</td>
						<td>
							<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT_SERVICE() %>">
								<%=process.forComboHTML(process.getVAL_LB_STATUT_SERVICE(), process.getVAL_LB_STATUT_SERVICE_SELECT()) %>
							</SELECT>
						</td>
					</tr>
					<tr>
						<td>
							<span class="sigp2">Titre :</span>
						</td>
						<td>
							<INPUT id="listeTitrePoste" class="sigp2-saisie" name="<%= process.getNOM_EF_TITRE_POSTE() %>" type="text" value="<%= process.getVAL_EF_TITRE_POSTE() %>">
						</td>
						<td>
							<span class="sigp2">Matricule agent :</span>
						</td>
						<td>
							<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_MATR_AGENT() %>" size="10" type="text" value="<%= process.getVAL_ST_MATR_AGENT() %>" style="margin-right:10px;">
						</td>
					</tr>
					<tr>
						<td>
							<span class="sigp2">Avec commentaire :</span>
						</td>
						<td>
							<INPUT style="visibility: visible;" type="checkbox" <%= process.forCheckBoxHTML(process.getNOM_CK_WITH_COMMENTAIRE(),process.getVAL_CK_WITH_COMMENTAIRE())%>><span  class="sigp2">Oui</span>									
						</td>
					</tr>
					
							
				</table>
				<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
				<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
				<BR>
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'une fiche de poste">
				<LEGEND class="sigp2Legend">Sélection d'une fiche de poste</LEGEND>
            	<%if(process.getListeFP()!= null && process.getListeFP().size()>0){ %>
            	<table class="display" id="tabFDP">
					<thead>
						<tr>
							<th>Numéro</th>
							<th>Titre</th>
							<th>Agent affecté</th>
							<th>Service</th>				
						</tr>
					</thead>
					<tbody>
					<%
						for (int j = 0;j<process.getListeFP().size();j++){
							FichePoste fp = (FichePoste) process.getListeFP().get(j);
							Integer i = fp.getIdFichePoste();
					%>
							<tr  id="<%=i%>" ondblclick='executeBouton("<%=process.getNOM_PB_VALIDER(i)%>")'>
								<td><%=process.getVAL_ST_NUM(i)%></td>
								<td><%=process.getVAL_ST_TITRE(i)%></td>
								<td><%=process.getVAL_ST_AGENT(i)%></td>
								<td><%=process.getVAL_ST_SERVICE(i)%><INPUT style="display:none;" type="submit" name="<%=process.getNOM_PB_VALIDER(i)%>" value="x"></td>
							</tr>
					<%
						}
					%>
					</tbody>
				</table>
				<BR/>
				<%} %>
				<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>" accesskey="A">
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" >
				<LEGEND class="sigp2Legend">Voir toutes les affectations d'une fiche de poste</LEGEND>
				<span class="sigp2" style="width:60px;">Numero :</span>
				<INPUT class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_FICHE_POSTE_AFF() %>" size="10" type="text" value="<%= process.getVAL_EF_NUM_FICHE_POSTE_AFF() %>" style="margin-right:10px;">
				<BR/>
				<BR/>
				<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER_AFF()%>" accesskey="R">
				<BR/><BR/>
				<%if (process.getListeAffectation().size()>0){ %>
				    <span style="margin-left: 0px;">Direction</span>
					<span style="margin-left: 0px;">Service/Section/...</span>
					<span style="margin-left: 155px;">Agent</span>
					<span style="margin-left: 180px;">Date début</span>
					<span style="margin-left: 25px;">Date fin</span>
					<span style="margin-left: 20px;">Fiche poste</span>
					<span style="margin-left: 5px;">Titre poste</span>
					<br/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
							<%
							int indiceAff = 0;
								for (int i = 0;i<process.getListeAffectation().size();i++){
							%>
									<tr>
										<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: left;"><%=process.getVAL_ST_DIR_AFF(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:250px;text-align: left;"><%=process.getVAL_ST_SERV_AFF(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:200px;text-align: left;"><%=process.getVAL_ST_AGENT_AFF(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_DATE_DEBUT_AFF(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: center;"><%=process.getVAL_ST_DATE_FIN_AFF(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;width:65px;text-align: left;"><%=process.getVAL_ST_NUM_FP_AFF(indiceAff)%></td>
										<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_TITRE_AFF(indiceAff)%></td>
									</tr>
									<%
									indiceAff++;
								}
							%>
						</table>	
						</div>	
				<%} %>
			</FIELDSET>
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>" value="SUPPRECHERCHERAGENT">
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE()%>" value="SUPPRECHERCHERSERVICE">		
		</FORM>
	</BODY>
</HTML>