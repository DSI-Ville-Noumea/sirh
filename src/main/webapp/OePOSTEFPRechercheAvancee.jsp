<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.metier.poste.TitrePoste"%>
<%@page import="nc.mairie.metier.poste.Service"%>
<%@page import="nc.mairie.utils.TreeHierarchy"%>
<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFPRechercheAvancee" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<lINK rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<TITLE>Sélection d'une fiche de poste</TITLE>
		
		<SCRIPT type="text/javascript" src="js/jquery-1.6.2.min.js"></SCRIPT>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<SCRIPT type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></SCRIPT>
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
		
		// afin d'afficher la hiérarchie des services
		function agrandirHierarchy() {
		
			hier = 	document.getElementById('treeHierarchy');
		
			if (hier.style.display!='none') {
				reduireHierarchy();
			} else {
				hier.style.display='block';
			}
		}
		
		// afin de cacher la hiérarchie des services
		function reduireHierarchy() {
			hier = 	document.getElementById('treeHierarchy');
			hier.style.display='none';
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
		</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%= process.getFocus() %>')">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche avancée d'une fiche de poste">
				<LEGEND class="sigp2Legend">Recherche avancée d'une fiche de poste</LEGEND>
				<BR/>
				<span class="sigp2" style="width:60px;">Numero :</span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_FICHE_POSTE() %>" size="10" type="text" value="<%= process.getVAL_EF_NUM_FICHE_POSTE() %>" style="margin-right:10px;">
				<BR/>
				<BR/>
				<span class="sigp2" style="width:60px;">Service :</span>
				<INPUT tabindex="" id="service" class="sigp2-saisie" readonly="readonly" name="<%= process.getNOM_EF_SERVICE() %>" size="10" style="margin-right:10px;" type="text" value="<%= process.getVAL_EF_SERVICE() %>">
				<img border="0" src="images/loupe.gif" width="16" title="Cliquer pour afficher l'arborescence"	height="16" style="cursor : pointer;" onclick="agrandirHierarchy();">	
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
				<span class="sigp2" style="width:80px"></span>
				<span class="sigp2" style="width:60px">Statut : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_STATUT() %>">
					<%=process.forComboHTML(process.getVAL_LB_STATUT(), process.getVAL_LB_STATUT_SELECT()) %>
				</SELECT>
				<BR/><BR/>
				<span class="sigp2" style="width:60px">Titre :</span>
				<INPUT tabindex="" id="listeTitrePoste" class="sigp2-saisie"
					name="<%= process.getNOM_EF_TITRE_POSTE() %>" style="margin-right:10px;width:450px"
					type="text" value="<%= process.getVAL_EF_TITRE_POSTE() %>">
				<BR/><BR/>
				<span class="sigp2" style="width:60px">Par agent :</span>
				<INPUT class="sigp2-saisie" name="<%= process.getNOM_ST_AGENT() %>" size="10" readonly="readonly" type="text" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
				<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          		<img border="0" src="images/suppression.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_SUPPRIMER_RECHERCHER_AGENT()%>');">
          		<BR/><BR/>
				<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
				<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
				<BR>
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'une fiche de poste">
				<LEGEND class="sigp2Legend">Sélection d'une fiche de poste</LEGEND>
            	<%if(process.getListeFP()!= null && process.getListeFP().size()>0){ %>
				<BR>	            
	            <span style="position:relative;width:70px;">Numéro</span>
				<span style="position:relative;width:455px;">Titre</span>
				<span style="position:relative;width:150px;">Agent affecté</span>
				<span style="position:relative;">Service</span>
				<BR/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceFp = 0;
					if (process.getListeFP()!=null){
						for (int i = 0;i<process.getListeFP().size();i++){
					%>
							<tr id="<%=indiceFp%>" onmouseover="SelectLigne(<%=indiceFp%>,<%=process.getListeFP().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_VALIDER(indiceFp)%>")'>
								<td class="sigp2NewTab-liste" style="position:relative;width:70px;text-align: left;"><%=process.getVAL_ST_NUM(indiceFp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:450px;text-align: left;"><%=process.getVAL_ST_TITRE(indiceFp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:150px;text-align: left;"><%=process.getVAL_ST_AGENT(indiceFp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_SERVICE(indiceFp)%></td>
								<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_VALIDER(indiceFp)%>" value="x"></td>
							</tr>
							<%
							indiceFp++;
						}
					}%>
					</table>	
				</div>
				<BR/>
				<%} %>
				<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>" accesskey="A">
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" >
				<LEGEND class="sigp2Legend">Voir toutes les affectations d'une fiche de poste</LEGEND>
				<span class="sigp2" style="width:60px;">Numero :</span>
				<INPUT tabindex="" class="sigp2-saisie" maxlength="8" name="<%= process.getNOM_EF_NUM_FICHE_POSTE_AFF() %>" size="10" type="text" value="<%= process.getVAL_EF_NUM_FICHE_POSTE_AFF() %>" style="margin-right:10px;">
				<BR/>
				<BR/>
				<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER_AFF()%>" accesskey="R">
				<BR/><BR/>
				<%if (process.getListeAffectation().size()>0){ %>
				    <span style="position:relative;width:50px;text-align:left;">Direction</span>
					<span style="position:relative;width:250px;text-align:left;">Service/Section/...</span>
					<span style="position:relative;width:200px;text-align:left;">Agent</span>
					<span style="position:relative;width:80px;text-align: center;">Date début</span>
					<span style="position:relative;width:85px;text-align: center;">Date fin</span>
					<span style="position:relative;width:65px;text-align: left;">Fiche poste</span>
					<span style="position:relative;text-align: left;">Titre poste</span>
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