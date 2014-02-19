<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@page import="nc.mairie.metier.parametrage.CodeRome"%>
<%@page import="nc.mairie.metier.poste.FicheEmploi"%>

<HTML>
	<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFERechercheAvancee" id="process" scope="session"></jsp:useBean>
	<HEAD>
		<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
		<META http-equiv="Content-Style-Type" content="text/css">
		<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet" href="css/custom-theme/jquery-ui-1.8.16.custom.css" type="text/css">
		<TITLE>Sélection d'une fiche de poste</TITLE>
		<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT>
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.core.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.position.js"></script>
		<script type="text/javascript" src="development-bundle/ui/jquery.ui.autocomplete.js"></script>
		
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
		<%
		ArrayList<CodeRome> listeCodeRome = process.getListeCodeRome();
		
		String res = 	"<script language=\"javascript\">\n"+
				"var availableCodeRome = new Array(\n";
		
		for (int i = 0; i < listeCodeRome.size(); i++){
			res+= "   \""+((CodeRome)listeCodeRome.get(i)).getLibCodeRome()+"\"";
			if (i+1 < listeCodeRome.size())
				res+=",\n";
			else	res+="\n";
		}
		
		res+=")</script>";
		
		
		ArrayList<FicheEmploi> listeNomEmploi = process.getListeFormNomEmploi();
		
		String resNomEmploi = 	"<script language=\"javascript\">\n"+
				"var availableNomEmploi = new Array(\n";
		
		for (int i = 0; i < listeNomEmploi.size(); i++){
			resNomEmploi+= "   \""+((FicheEmploi)listeNomEmploi.get(i)).getNomMetierEmploi()+"\"";
			if (i+1 < listeNomEmploi.size())
				resNomEmploi+=",\n";
			else	resNomEmploi+="\n";
		}
		
		resNomEmploi+=")</script>";
		%>
		<%=res%>
		<%=resNomEmploi%>
		<SCRIPT type="text/javascript">
			$(document).ready(function(){
				$("#listeCodeRome").autocomplete({source:availableCodeRome
				});
			});
			$(document).ready(function(){
				$("#listeNomEmploi").autocomplete({source:availableNomEmploi
				});
			});
		</SCRIPT>
		<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</HEAD>
	<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%= process.getFocus() %>')">
		<%@ include file="BanniereErreur.jsp"%>
		<FORM name="formu" method="POST" class="sigp2-titre">
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche avancée d'une fiche emploi">
				<LEGEND class="sigp2Legend">Recherche avancée d'une fiche emploi</LEGEND>
				<BR/>
				<span class="sigp2" style="width:150px">Domaine de l'emploi : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_DOMAINE_EMPLOI() %>" style="width=328px">
					<%=process.forComboHTML(process.getVAL_LB_DOMAINE_EMPLOI(), process.getVAL_LB_DOMAINE_EMPLOI_SELECT()) %>
				</SELECT>
				<BR/><BR/>
				<span class="sigp2" style="width:150px">Famille de l'emploi : </span>
				<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_FAMILLE_EMPLOI() %>" style="width=328px">
					<%=process.forComboHTML(process.getVAL_LB_FAMILLE_EMPLOI(), process.getVAL_LB_FAMILLE_EMPLOI_SELECT()) %>
				</SELECT>
				<BR/><BR/>
				<span class="sigp2" style="width:150px"> Ref. Mairie : </span>
				<INPUT class="sigp2-saisiemajuscule" maxlength="8"
					name="<%= process.getNOM_EF_REF_MAIRIE_RECH() %>" size="10"
					type="text" value="<%= process.getVAL_EF_REF_MAIRIE_RECH() %>">
				<BR/><BR/>
				<span class="sigp2" style="width:150px">Nom du métier / emploi : </span>
				<INPUT tabindex="" id="listeNomEmploi" class="sigp2-saisie" 
					name="<%= process.getNOM_EF_NOM_EMPLOI() %>" style="margin-right:10px;width:328px" 
					type="text" value="<%= process.getVAL_EF_NOM_EMPLOI() %>">
				<BR/><BR/>
				<span class="sigp2" style="width:147px">Code Rome :</span>
				<INPUT id="listeCodeRome" class="sigp2-saisiemajuscule"
					name="<%= process.getNOM_EF_CODE_ROME_RECH() %>" maxlength="5" size="10"
					type="text" value="<%= process.getVAL_EF_CODE_ROME_RECH() %>">
				<BR/><BR/>
				<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>">
				<BR>
			</FIELDSET>
			
			<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'une fiche emploi %>">
				<LEGEND class="sigp2Legend">Sélection d'une fiche emploi</LEGEND>
            	<%if(process.getListeFE()!= null && process.getListeFE().size()>0){ %>
				<BR>
	            
	            <INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_REFMAIRIE())%>>
				<span width="47" class="sigp2-titre-liste">Ref. mairie</span>
				<INPUT type="radio" onclick='executeBouton("<%=process.getNOM_PB_TRI() %>")' <%= process.forRadioHTML(process.getNOM_RG_TRI(),process.getNOM_RB_TRI_DESC())%>>
				<span width="47" class="sigp2-titre-liste">Description</span>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceFe = 0;
					if (process.getListeFE()!=null){
						for (int i = 0;i<process.getListeFE().size();i++){
					%> 
							<tr id="<%=indiceFe%>" onmouseover="SelectLigne(<%=indiceFe%>,<%=process.getListeFE().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_VALIDER(indiceFe)%>")'>
								<td class="sigp2NewTab-liste" style="position:relative;width:100px;text-align: left;"><%=process.getVAL_ST_REF(indiceFe)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_NOM(indiceFe)%></td>
								<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_VALIDER(indiceFe)%>" value="x"></td>
							</tr>
							<%
							indiceFe++;
						}
					}%>
					</table>	
				</div>
				<%} %>
				<BR>
				<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>">
			</FIELDSET>
			<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
			<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_TRI()%>" value="TRI"><BR>
		</FORM>
	</BODY>
</HTML>