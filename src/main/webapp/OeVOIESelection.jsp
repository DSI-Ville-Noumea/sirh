<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/sigp2.css" rel="stylesheet" type="text/css">
<TITLE>Sélection d'une voie française</TITLE>

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

</SCRIPT>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</HEAD>
<jsp:useBean class="nc.mairie.gestionagent.process.OeVOIESelection" id="process" scope="session"></jsp:useBean>
<BODY bgcolor="#FFFFFF" background="images/fond.jpg" lang="FR" link="blue" vlink="purple" style="tab-interval:35.4pt" onload="return setfocus('<%= process.getFocus() %>')">
	<%@ include file="BanniereErreur.jsp" %>
	<FORM name="formu" method="POST" class="sigp2-titre"><BR>
		<FIELDSET class="sigp2Fieldset" style="text-align:left;width:1030px;">
			<legend class="sigp2Legend">Sélection d'une voie</legend>
			<span class="sigp2">Libellé de la voie :</span>
			<span>
				<INPUT size="1" type="text" class="sigp2-saisie" maxlength="1" name="ZoneTampon" style="display:none;">
				<INPUT class="sigp2-saisie" maxlength="60" name="<%= process.getNOM_EF_VOIE() %>" size="20" type="text" value="<%= process.getVAL_EF_VOIE() %>">
			</span>
			<span>
				<INPUT type="submit" value="Rechercher" class="sigp2-Bouton-100" name="<%=process.getNOM_PB_RECHERCHER()%>" accesskey="R">
			</span>
			<BR/><BR/>
           <%if(process.getListeVoie()!= null && process.getListeVoie().size()>0){ %>
			<TABLE border="0" class="sigp2" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<TD style="position:relative;width:50px;text-align:left;">&nbsp;code</TD>
						<TD style="position:relative;width:300px;text-align:left;">libellé</TD>
						<TD style="position:relative;text-align:left;">quartier</TD>
					</TR>
				</TBODY>
			</TABLE>
					<div style="overflow: auto;height: 250px;width:1000px;margin-right: 0px;margin-left: 0px;">
						<table class="sigp2NewTab" style="text-align:left;width:980px;">
						<%
						int indiceRue = 0;
						if (process.getListeVoie()!=null){
							for (int i = 0;i<process.getListeVoie().size();i++){
						%>
								<tr id="<%=indiceRue%>" onmouseover="SelectLigne(<%=indiceRue%>,<%=process.getListeVoie().size()%>)" ondblclick='executeBouton("<%=process.getNOM_PB_OK(indiceRue)%>")'>
									<td class="sigp2NewTab-liste" style="position:relative;width:40px;text-align: left;"><%=process.getVAL_ST_CODE(indiceRue)%></td>
									<td class="sigp2NewTab-liste" style="position:relative;width:300px;text-align: left;"><%=process.getVAL_ST_LIB(indiceRue)%></td>
									<td class="sigp2NewTab-liste" style="position:relative;text-align: left;"><%=process.getVAL_ST_QUARTIER(indiceRue)%></td>
									<td><INPUT type="submit" style="display:none;" name="<%=process.getNOM_PB_OK(indiceRue)%>" value="x"></td>
								</tr>
								<%
								indiceRue++;
							}
						}%>
						</table>	
					</div>
			<%} %>
			<BR/>
			<span width="61">
				<INPUT type="submit" value="Annuler" class="sigp2-Bouton-100" accesskey="A" name="<%=process.getNOM_PB_ANNULER()%>">
			</span>
			<BR>
		</FIELDSET>
		<INPUT name="JSP" type="hidden" value="<%= process.getJSP() %>">
	</FORM>
</BODY>
</HTML>