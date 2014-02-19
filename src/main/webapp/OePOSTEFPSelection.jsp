<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Sélection d'une fiche de poste</TITLE>

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
		//afin de sélectionner un élément dans une liste
		function executeBouton(nom)
		{
		document.formu.elements[nom].click();
		}

</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.poste.OePOSTEFPSelection" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" onload="return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp"%>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Recherche d'une fiche de poste">
			<LEGEND class="sigp2Legend">Recherche d'une fiche de poste</LEGEND>
			<BR>
			
			<%if (process.estRechercheAvancee()) {%>
			
			<input type="radio"  <%= process.forRadioHTML(process.getNOM_RG_TYPE_RECHERCHE(), process.getNOM_RB_TYPE_NUMERO()) %> width="500px;" align="left" > par numéro de fiche de poste : 
			<INPUT class="sigp2-saisie" maxlength="8"
				name="<%= process.getNOM_EF_NUM_FICHE_POSTE() %>" size="10"
				type="text" value="<%= process.getVAL_EF_NUM_FICHE_POSTE() %>" style="margin-right:10px;">
			<br/><br/>
			<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RECHERCHE(), process.getNOM_RB_TYPE_AGENT()) %> > par agent : 
			<INPUT class="sigp2-saisie" maxlength="100"	name="<%= process.getNOM_ST_AGENT() %>" size="20"
				type="hidden" value="<%= process.getVAL_ST_AGENT() %>" style="margin-right:10px;">
			<img border="0" src="images/loupe.gif" width="16px" height="16px" style="cursor : pointer;" onclick="executeBouton('<%=process.getNOM_PB_RECHERCHER_AGENT()%>');">
          	
          	<br/><br/>			
				<%if (process.getService() != null) {%>			
				<input type="radio" <%= process.forRadioHTML(process.getNOM_RG_TYPE_RECHERCHE(), process.getNOM_RB_TYPE_SERVICE()) %> > par service : 
				<span align="left" class="sigp2-saisie"><%=process.getVAL_ST_SERVICE()%></span>
				<br/><br/>
				<% } %>			
			<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
			<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
			<% } else { %>
			
			<INPUT class="sigp2-saisie" maxlength="100"	name="<%= process.getNOM_EF_NUM_FICHE_POSTE() %>" size="50"
				type="text" value="<%= process.getVAL_EF_NUM_FICHE_POSTE() %>" style="margin-right:10px;">
			<span style="width:20px;"></span>
			<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
			<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
			
			<% } %>
			<BR>
		</FIELDSET>
		
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;" title="Sélection d'une fiche de poste">
			<LEGEND class="sigp2Legend">Sélection d'une fiche de poste</LEGEND>
            <%if(process.getListeFichePoste()!= null && process.getListeFichePoste().size()>0){ %>
				<span style="width:80px;">Numéro</span>
				<span style="position:relative;width:405px;">Titre</span>
				<span style="position:relative;">Agent affecté</span>
			<BR/>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceFp = 0;
					if (process.getListeFichePoste()!=null){
						for (int i = 0;i<process.getListeFichePoste().size();i++){
					%>
							<tr id="<%=indiceFp%>" onmouseover="SelectLigne(<%=indiceFp%>,<%=process.getListeFichePoste().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_VALIDER(indiceFp)%>")'>
								<td class="sigp2NewTab-liste" style="position:relative;width:80px;text-align: left;"><%=process.getVAL_ST_NUM(indiceFp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;width:400px;text-align: left;"><%=process.getVAL_ST_TITRE(indiceFp)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_AGENT(indiceFp)%></td>
								<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_VALIDER(indiceFp)%>" value="x"></td>
							</tr>
							<%
							indiceFp++;
						}
					}%>
					</table>	
				</div>
			<%} %>
			<BR>
			<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" accesskey="A" name="<%=process.getNOM_PB_ANNULER()%>">
		</FIELDSET>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">		
		<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_RECHERCHER_AGENT()%>" value="RECHERCHERAGENT"><BR>
	</FORM>
</BODY>
</HTML>