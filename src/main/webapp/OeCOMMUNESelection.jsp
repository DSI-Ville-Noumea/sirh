<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Sélection d'une PAYS</TITLE>
<SCRIPT language="javascript" src="js/GestionBoutonDroit.js"></SCRIPT> 
<SCRIPT language="javaScript">

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
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.OeCOMMUNESelection" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" BGPROPERTIES="FIXED" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" style="tab-interval:35.4pt" onload="return setfocus('<%= process.getFocus() %>')">

    <%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre">
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	    <FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
		    <legend class="sigp2Legend">Sélection d'une commune</legend>
		    <br/>
		    
		    <div class="sigp2-RadioBouton">
		    	<span>
		    		<INPUT type="radio"	<%= process.forRadioHTML(process.getNOM_RG_ORIGINE_COMMUNE(),process.getNOM_RB_ORIGINE_COMMUNE_FRANCE())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGE_ORIGINE()%>")'>
		    	</span>
		    	<span>En France</span>
		    	<span>
		    		<INPUT type="radio"	<%= process.forRadioHTML(process.getNOM_RG_ORIGINE_COMMUNE(),process.getNOM_RB_ORIGINE_COMMUNE_ETRANGER())%> onclick='executeBouton("<%=process.getNOM_PB_CHANGE_ORIGINE()%>")'>
		    	</span>
		    	<span>A l'étranger</span>
				<INPUT type="submit" class="sigp2-Bouton-100" value="Changer" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGE_ORIGINE()%>">
			</div>
			<BR/>
			<%if ("Etranger".equals(process.getOrigine())) { %>
			<div id="etranger" style="display:block;">
			<%} else {%>
			<div id="etranger" style="display:none;">
			<%}%>
				<span class="sigp2">Code ou libellé pays commençant par :</span>
				<span>
					<INPUT class="sigp2-saisie" maxlength="60" name="<%= process.getNOM_EF_PAYS() %>" size="20" type="text" value="<%= process.getVAL_EF_PAYS() %>">
				</span>
				<span>
					<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>">
				</span>
				<br/><br/>
				<%if(!process.estPaysSelectionne){ %>
                <%if(process.getListePays()!= null && process.getListePays().size()>0){ %>
					<span class="sigp2-titre">&nbsp;Liste des pays</span>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<%
						int indicePays = 0;
						if (process.getListePays()!=null){
							for (int i = 0;i<process.getListePays().size();i++){
						%>
								<tr id="<%=indicePays%>" onmouseover="SelectLigne(<%=indicePays%>,<%=process.getListePays().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_PAYS(indicePays)%>")'>
									<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: left;"><%=process.getVAL_ST_CODE_PAYS(indicePays)%></td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB_PAYS(indicePays)%></td>
									<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_PAYS(indicePays)%>" value="x"></td>
								</tr>
								<%
								indicePays++;
							}
						}%>
						</table>	
					</div>
				<%} %>
				<%} %>
				<br/>
                <%if(process.getListeCommunePays()!= null && process.getListeCommunePays().size()>0){ %>
                	<%if(process.getPaysCourant()!=null){ %>
					<span class="sigp2-titre">&nbsp;Liste des communes du pays <%=process.getPaysCourant().getLibPays() %></span>
					<%}else{ %>
					<span class="sigp2-titre">&nbsp;Liste des communes du pays sélectionné</span>
					<%} %>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<%
						int indiceCommPays = 0;
						if (process.getListeCommunePays()!=null){
							for (int i = 0;i<process.getListeCommunePays().size();i++){
						%>
								<tr id="<%=indiceCommPays%>" onmouseover="SelectLigne(<%=indiceCommPays%>,<%=process.getListeCommunePays().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_OK_PAYS(indiceCommPays)%>")'>
									<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: left;"><%=process.getVAL_ST_CODE(indiceCommPays)%></td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB(indiceCommPays)%></td>
									<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_OK_PAYS(indiceCommPays)%>" value="x"></td>
								</tr>
								<%
								indiceCommPays++;
							}
						}%>
						</table>	
					</div>
				<%} %>
			</div>
			<%if ("France".equals(process.getOrigine())) { %>
			<div id="france" style="display: block;">
			<%} else {%>
			<div id="france" style="display: none;">
			<%}%>
			
				<span class="sigp2">Code ou libellé commune commençant par :</span>
				<span>
					<INPUT class="sigp2-saisie" maxlength="60" name="<%= process.getNOM_EF_COMMUNE_FRANCE() %>" size="20" type="text" value="<%= process.getVAL_EF_COMMUNE_FRANCE() %>">
				</span>
                <span>
                	<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER_FRANCE()%>" >
                </span>
                <BR><BR>
                <%if(process.getListeCommune()!= null && process.getListeCommune().size()>0){ %>
				<TABLE border="0" class="sigp2" cellpadding="0" cellspacing="0" width="100%">
                	<TBODY>
						<TR class="sigp2">
							<TD style="position:relative;width:60px;text-align:left;">&nbsp;Code</TD>
							<TD style="position:relative;text-align:left;">Libellé</TD>
						</TR>
					</TBODY>
				</TABLE>
				<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
					<table class="sigp2NewTab" style="text-align:left;width:980px;">
					<%
					int indiceComm = 0;
					if (process.getListeCommune()!=null){
						for (int i = 0;i<process.getListeCommune().size();i++){
					%>
							<tr id="<%=indiceComm%>" onmouseover="SelectLigne(<%=indiceComm%>,<%=process.getListeCommune().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_OK_COMM(indiceComm)%>")'>
								<td class="sigp2NewTab-liste" style="position:relative;width:50px;text-align: left;"><%=process.getVAL_ST_CODE(indiceComm)%></td>
								<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_LIB(indiceComm)%></td>
								<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_OK_COMM(indiceComm)%>" value="x"></td>
							</tr>
							<%
							indiceComm++;
						}
					}%>
					</table>	
				</div>
				<%} %>
			</div>
			<br/>
           <span>
           	<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_ANNULER()%>">
           </span>
           </div>
	    </FIELDSET>
    </FORM>
</BODY>
</HTML>